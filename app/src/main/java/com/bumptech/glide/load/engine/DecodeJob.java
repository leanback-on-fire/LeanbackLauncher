package com.bumptech.glide.load.engine;

import android.support.v4.util.Pools.Pool;
import android.util.Log;
import com.bumptech.glide.GlideContext;
import com.bumptech.glide.Priority;
import com.bumptech.glide.Registry.NoResultEncoderAvailableException;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.EncodeStrategy;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceEncoder;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.data.DataRewinder;
import com.bumptech.glide.load.engine.DataFetcherGenerator.FetcherReadyCallback;
import com.bumptech.glide.load.engine.cache.DiskCache;
import com.bumptech.glide.util.LogTime;
import com.bumptech.glide.util.pool.FactoryPools.Poolable;
import com.bumptech.glide.util.pool.StateVerifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class DecodeJob<R> implements FetcherReadyCallback, Poolable, Comparable<DecodeJob<?>>, Runnable {
    private Callback<R> callback;
    private Key currentAttemptingKey;
    private Object currentData;
    private DataSource currentDataSource;
    private DataFetcher<?> currentFetcher;
    private volatile DataFetcherGenerator currentGenerator;
    private Key currentSourceKey;
    private Thread currentThread;
    private final DecodeHelper<R> decodeHelper = new DecodeHelper();
    private final DeferredEncodeManager<?> deferredEncodeManager = new DeferredEncodeManager();
    private final DiskCacheProvider diskCacheProvider;
    private DiskCacheStrategy diskCacheStrategy;
    private final List<Exception> exceptions = new ArrayList();
    private GlideContext glideContext;
    private int height;
    private volatile boolean isCallbackNotified;
    private volatile boolean isCancelled;
    private EngineKey loadKey;
    private Options options;
    private int order;
    private final Pool<DecodeJob<?>> pool;
    private Priority priority;
    private final ReleaseManager releaseManager = new ReleaseManager();
    private RunReason runReason;
    private Key signature;
    private Stage stage;
    private long startFetchTime;
    private final StateVerifier stateVerifier = StateVerifier.newInstance();
    private int width;

    interface Callback<R> {
        void onLoadFailed(GlideException glideException);

        void onResourceReady(Resource<R> resource, DataSource dataSource);

        void reschedule(DecodeJob<?> decodeJob);
    }

    class DecodeCallback<Z> implements DecodeCallback<Z> {
        private final DataSource dataSource;

        public DecodeCallback(DataSource dataSource) {
            this.dataSource = dataSource;
        }

        public Resource<Z> onResourceDecoded(Resource<Z> decoded) {
            ResourceEncoder<Z> encoder;
            EncodeStrategy encodeStrategy;
            Class<Z> resourceSubClass = getResourceClass(decoded);
            Transformation<Z> appliedTransformation = null;
            Resource<Z> transformed = decoded;
            if (this.dataSource != DataSource.RESOURCE_DISK_CACHE) {
                appliedTransformation = DecodeJob.this.decodeHelper.getTransformation(resourceSubClass);
                transformed = appliedTransformation.transform(decoded, DecodeJob.this.width, DecodeJob.this.height);
            }
            if (!decoded.equals(transformed)) {
                decoded.recycle();
            }
            if (DecodeJob.this.decodeHelper.isResourceEncoderAvailable(transformed)) {
                encoder = DecodeJob.this.decodeHelper.getResultEncoder(transformed);
                encodeStrategy = encoder.getEncodeStrategy(DecodeJob.this.options);
            } else {
                encoder = null;
                encodeStrategy = EncodeStrategy.NONE;
            }
            Resource<Z> result = transformed;
            if (!DecodeJob.this.diskCacheStrategy.isResourceCacheable(!DecodeJob.this.decodeHelper.isSourceKey(DecodeJob.this.currentSourceKey), this.dataSource, encodeStrategy)) {
                return result;
            }
            if (encoder == null) {
                throw new NoResultEncoderAvailableException(transformed.get().getClass());
            }
            Key key;
            if (encodeStrategy == EncodeStrategy.SOURCE) {
                key = new DataCacheKey(DecodeJob.this.currentSourceKey, DecodeJob.this.signature);
            } else if (encodeStrategy == EncodeStrategy.TRANSFORMED) {
                key = new ResourceCacheKey(DecodeJob.this.currentSourceKey, DecodeJob.this.signature, DecodeJob.this.width, DecodeJob.this.height, appliedTransformation, resourceSubClass, DecodeJob.this.options);
            } else {
                String valueOf = String.valueOf(encodeStrategy);
                throw new IllegalArgumentException(new StringBuilder(String.valueOf(valueOf).length() + 18).append("Unknown strategy: ").append(valueOf).toString());
            }
            LockedResource<Z> lockedResult = LockedResource.obtain(transformed);
            DecodeJob.this.deferredEncodeManager.init(key, encoder, lockedResult);
            return lockedResult;
        }

        private Class<Z> getResourceClass(Resource<Z> resource) {
            return resource.get().getClass();
        }
    }

    private static class DeferredEncodeManager<Z> {
        private ResourceEncoder<Z> encoder;
        private Key key;
        private LockedResource<Z> toEncode;

        private DeferredEncodeManager() {
        }

        <X> void init(Key key, ResourceEncoder<X> encoder, LockedResource<X> toEncode) {
            this.key = key;
            this.encoder = encoder;
            this.toEncode = toEncode;
        }

        void encode(DiskCacheProvider diskCacheProvider, Options options) {
            try {
                diskCacheProvider.getDiskCache().put(this.key, new DataCacheWriter(this.encoder, this.toEncode, options));
            } finally {
                this.toEncode.unlock();
            }
        }

        boolean hasResourceToEncode() {
            return this.toEncode != null;
        }

        void clear() {
            this.key = null;
            this.encoder = null;
            this.toEncode = null;
        }
    }

    interface DiskCacheProvider {
        DiskCache getDiskCache();
    }

    private static class ReleaseManager {
        private boolean isEncodeComplete;
        private boolean isFailed;
        private boolean isReleased;

        private ReleaseManager() {
        }

        synchronized boolean release(boolean isRemovedFromQueue) {
            this.isReleased = true;
            return isComplete(isRemovedFromQueue);
        }

        synchronized boolean onEncodeComplete() {
            this.isEncodeComplete = true;
            return isComplete(false);
        }

        synchronized boolean onFailed() {
            this.isFailed = true;
            return isComplete(false);
        }

        synchronized void reset() {
            this.isEncodeComplete = false;
            this.isReleased = false;
            this.isFailed = false;
        }

        private boolean isComplete(boolean isRemovedFromQueue) {
            return (this.isFailed || isRemovedFromQueue || this.isEncodeComplete) && this.isReleased;
        }
    }

    private enum RunReason {
        INITIALIZE,
        SWITCH_TO_SOURCE_SERVICE,
        DECODE_DATA
    }

    private enum Stage {
        INITIALIZE,
        RESOURCE_CACHE,
        DATA_CACHE,
        SOURCE,
        ENCODE,
        FINISHED
    }

    DecodeJob(DiskCacheProvider diskCacheProvider, Pool<DecodeJob<?>> pool) {
        this.diskCacheProvider = diskCacheProvider;
        this.pool = pool;
    }

    DecodeJob<R> init(GlideContext glideContext, Object model, EngineKey loadKey, Key signature, int width, int height, Class<?> resourceClass, Class<R> transcodeClass, Priority priority, DiskCacheStrategy diskCacheStrategy, Map<Class<?>, Transformation<?>> transformations, boolean isTransformationRequired, Options options, Callback<R> callback, int order) {
        this.decodeHelper.init(glideContext, model, signature, width, height, diskCacheStrategy, resourceClass, transcodeClass, priority, options, transformations, isTransformationRequired, this.diskCacheProvider);
        this.glideContext = glideContext;
        this.signature = signature;
        this.priority = priority;
        this.loadKey = loadKey;
        this.width = width;
        this.height = height;
        this.diskCacheStrategy = diskCacheStrategy;
        this.options = options;
        this.callback = callback;
        this.order = order;
        this.runReason = RunReason.INITIALIZE;
        return this;
    }

    boolean willDecodeFromCache() {
        Stage firstStage = getNextStage(Stage.INITIALIZE);
        return firstStage == Stage.RESOURCE_CACHE || firstStage == Stage.DATA_CACHE;
    }

    void release(boolean isRemovedFromQueue) {
        if (this.releaseManager.release(isRemovedFromQueue)) {
            releaseInternal();
        }
    }

    private void onEncodeComplete() {
        if (this.releaseManager.onEncodeComplete()) {
            releaseInternal();
        }
    }

    private void onLoadFailed() {
        if (this.releaseManager.onFailed()) {
            releaseInternal();
        }
    }

    private void releaseInternal() {
        this.releaseManager.reset();
        this.deferredEncodeManager.clear();
        this.decodeHelper.clear();
        this.isCallbackNotified = false;
        this.glideContext = null;
        this.signature = null;
        this.options = null;
        this.priority = null;
        this.loadKey = null;
        this.callback = null;
        this.stage = null;
        this.currentGenerator = null;
        this.currentThread = null;
        this.currentSourceKey = null;
        this.currentData = null;
        this.currentDataSource = null;
        this.currentFetcher = null;
        this.startFetchTime = 0;
        this.isCancelled = false;
        this.exceptions.clear();
        this.pool.release(this);
    }

    public int compareTo(DecodeJob<?> other) {
        int result = getPriority() - other.getPriority();
        if (result == 0) {
            return this.order - other.order;
        }
        return result;
    }

    private int getPriority() {
        return this.priority.ordinal();
    }

    public void cancel() {
        this.isCancelled = true;
        DataFetcherGenerator local = this.currentGenerator;
        if (local != null) {
            local.cancel();
        }
    }

    public void run() {
        try {
            if (this.isCancelled) {
                notifyFailed();
            } else {
                runWrapped();
            }
        } catch (RuntimeException e) {
            if (Log.isLoggable("DecodeJob", 3)) {
                boolean z = this.isCancelled;
                String valueOf = String.valueOf(this.stage);
                Log.d("DecodeJob", new StringBuilder(String.valueOf(valueOf).length() + 57).append("DecodeJob threw unexpectedly, isCancelled: ").append(z).append(", stage: ").append(valueOf).toString(), e);
            }
            if (this.stage != Stage.ENCODE) {
                notifyFailed();
            }
            if (!this.isCancelled) {
                throw e;
            }
        }
    }

    private void runWrapped() {
        switch (this.runReason) {
            case INITIALIZE:
                this.stage = getNextStage(Stage.INITIALIZE);
                this.currentGenerator = getNextGenerator();
                runGenerators();
                return;
            case SWITCH_TO_SOURCE_SERVICE:
                runGenerators();
                return;
            case DECODE_DATA:
                decodeFromRetrievedData();
                return;
            default:
                String valueOf = String.valueOf(this.runReason);
                throw new IllegalStateException(new StringBuilder(String.valueOf(valueOf).length() + 25).append("Unrecognized run reason: ").append(valueOf).toString());
        }
    }

    private DataFetcherGenerator getNextGenerator() {
        switch (this.stage) {
            case RESOURCE_CACHE:
                return new ResourceCacheGenerator(this.decodeHelper, this);
            case DATA_CACHE:
                return new DataCacheGenerator(this.decodeHelper, this);
            case SOURCE:
                return new SourceGenerator(this.decodeHelper, this);
            case FINISHED:
                return null;
            default:
                String valueOf = String.valueOf(this.stage);
                throw new IllegalStateException(new StringBuilder(String.valueOf(valueOf).length() + 20).append("Unrecognized stage: ").append(valueOf).toString());
        }
    }

    private void runGenerators() {
        this.currentThread = Thread.currentThread();
        this.startFetchTime = LogTime.getLogTime();
        boolean isStarted = false;
        while (!this.isCancelled && this.currentGenerator != null) {
            isStarted = this.currentGenerator.startNext();
            if (isStarted) {
                break;
            }
            this.stage = getNextStage(this.stage);
            this.currentGenerator = getNextGenerator();
            if (this.stage == Stage.SOURCE) {
                reschedule();
                return;
            }
        }
        if ((this.stage == Stage.FINISHED || this.isCancelled) && !isStarted) {
            notifyFailed();
        }
    }

    private void notifyFailed() {
        setNotifiedOrThrow();
        this.callback.onLoadFailed(new GlideException("Failed to load resource", new ArrayList(this.exceptions)));
        onLoadFailed();
    }

    private void notifyComplete(Resource<R> resource, DataSource dataSource) {
        setNotifiedOrThrow();
        this.callback.onResourceReady(resource, dataSource);
    }

    private void setNotifiedOrThrow() {
        this.stateVerifier.throwIfRecycled();
        if (this.isCallbackNotified) {
            throw new IllegalStateException("Already notified");
        }
        this.isCallbackNotified = true;
    }

    private Stage getNextStage(Stage current) {
        switch (current) {
            case RESOURCE_CACHE:
                return this.diskCacheStrategy.decodeCachedData() ? Stage.DATA_CACHE : getNextStage(Stage.DATA_CACHE);
            case DATA_CACHE:
                return Stage.SOURCE;
            case SOURCE:
            case FINISHED:
                return Stage.FINISHED;
            case INITIALIZE:
                return this.diskCacheStrategy.decodeCachedResource() ? Stage.RESOURCE_CACHE : getNextStage(Stage.RESOURCE_CACHE);
            default:
                String valueOf = String.valueOf(current);
                throw new IllegalArgumentException(new StringBuilder(String.valueOf(valueOf).length() + 20).append("Unrecognized stage: ").append(valueOf).toString());
        }
    }

    public void reschedule() {
        this.runReason = RunReason.SWITCH_TO_SOURCE_SERVICE;
        this.callback.reschedule(this);
    }

    public void onDataFetcherReady(Key sourceKey, Object data, DataFetcher<?> fetcher, DataSource dataSource, Key attemptedKey) {
        this.currentSourceKey = sourceKey;
        this.currentData = data;
        this.currentFetcher = fetcher;
        this.currentDataSource = dataSource;
        this.currentAttemptingKey = attemptedKey;
        if (Thread.currentThread() != this.currentThread) {
            this.runReason = RunReason.DECODE_DATA;
            this.callback.reschedule(this);
            return;
        }
        decodeFromRetrievedData();
    }

    public void onDataFetcherFailed(Key attemptedKey, Exception e, DataFetcher<?> fetcher, DataSource dataSource) {
        GlideException exception = new GlideException("Fetching data failed", e);
        exception.setLoggingDetails(attemptedKey, dataSource, fetcher.getDataClass());
        this.exceptions.add(exception);
        if (Thread.currentThread() != this.currentThread) {
            this.runReason = RunReason.SWITCH_TO_SOURCE_SERVICE;
            this.callback.reschedule(this);
            return;
        }
        runGenerators();
    }

    private void decodeFromRetrievedData() {
        if (Log.isLoggable("DecodeJob", 2)) {
            long j = this.startFetchTime;
            String valueOf = String.valueOf(this.currentData);
            String valueOf2 = String.valueOf(this.currentSourceKey);
            String valueOf3 = String.valueOf(this.currentFetcher);
            logWithTimeAndKey("Retrieved data", j, new StringBuilder(((String.valueOf(valueOf).length() + 30) + String.valueOf(valueOf2).length()) + String.valueOf(valueOf3).length()).append("data: ").append(valueOf).append(", cache key: ").append(valueOf2).append(", fetcher: ").append(valueOf3).toString());
        }
        Resource<R> resource = null;
        try {
            resource = decodeFromData(this.currentFetcher, this.currentData, this.currentDataSource);
        } catch (GlideException e) {
            e.setLoggingDetails(this.currentAttemptingKey, this.currentDataSource);
            this.exceptions.add(e);
        }
        if (resource != null) {
            notifyEncodeAndRelease(resource, this.currentDataSource);
        } else {
            runGenerators();
        }
    }

    private void notifyEncodeAndRelease(Resource<R> resource, DataSource dataSource) {
        Resource<R> result = resource;
        LockedResource<R> lockedResource = null;
        if (this.deferredEncodeManager.hasResourceToEncode()) {
            lockedResource = LockedResource.obtain(resource);
            result = lockedResource;
        }
        notifyComplete(result, dataSource);
        this.stage = Stage.ENCODE;
        try {
            if (this.deferredEncodeManager.hasResourceToEncode()) {
                this.deferredEncodeManager.encode(this.diskCacheProvider, this.options);
            }
            if (lockedResource != null) {
                lockedResource.unlock();
            }
            onEncodeComplete();
        } catch (Throwable th) {
            if (lockedResource != null) {
                lockedResource.unlock();
            }
            onEncodeComplete();
        }
    }

    private <Data> Resource<R> decodeFromData(DataFetcher<?> fetcher, Data data, DataSource dataSource) throws GlideException {
        if (data == null) {
            fetcher.cleanup();
            return null;
        }
        try {
            long startTime = LogTime.getLogTime();
            Resource<R> result = decodeFromFetcher(data, dataSource);
            if (Log.isLoggable("DecodeJob", 2)) {
                String valueOf = String.valueOf(result);
                logWithTimeAndKey(new StringBuilder(String.valueOf(valueOf).length() + 15).append("Decoded result ").append(valueOf).toString(), startTime);
            }
            fetcher.cleanup();
            return result;
        } catch (Throwable th) {
            fetcher.cleanup();
        }
    }

    private <Data> Resource<R> decodeFromFetcher(Data data, DataSource dataSource) throws GlideException {
        return runLoadPath(data, dataSource, this.decodeHelper.getLoadPath(data.getClass()));
    }

    private <Data, ResourceType> Resource<R> runLoadPath(Data data, DataSource dataSource, LoadPath<Data, ResourceType, R> path) throws GlideException {
        DataRewinder<Data> rewinder = this.glideContext.getRegistry().getRewinder(data);
        try {
            Resource<R> load = path.load(rewinder, this.options, this.width, this.height, new DecodeCallback(dataSource));
            return load;
        } finally {
            rewinder.cleanup();
        }
    }

    private void logWithTimeAndKey(String message, long startTime) {
        logWithTimeAndKey(message, startTime, null);
    }

    private void logWithTimeAndKey(String message, long startTime, String extraArgs) {
        String str;
        String valueOf;
        String str2 = "DecodeJob";
        double elapsedMillis = LogTime.getElapsedMillis(startTime);
        String valueOf2 = String.valueOf(this.loadKey);
        if (extraArgs != null) {
            str = ", ";
            valueOf = String.valueOf(extraArgs);
            valueOf = valueOf.length() != 0 ? str.concat(valueOf) : new String(str);
        } else {
            valueOf = "";
        }
        str = String.valueOf(Thread.currentThread().getName());
        Log.v(str2, new StringBuilder((((String.valueOf(message).length() + 50) + String.valueOf(valueOf2).length()) + String.valueOf(valueOf).length()) + String.valueOf(str).length()).append(message).append(" in ").append(elapsedMillis).append(", load key: ").append(valueOf2).append(valueOf).append(", thread: ").append(str).toString());
    }

    public StateVerifier getVerifier() {
        return this.stateVerifier;
    }
}
