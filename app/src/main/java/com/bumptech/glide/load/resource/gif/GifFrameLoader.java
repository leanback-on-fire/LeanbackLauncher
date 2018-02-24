package com.bumptech.glide.load.resource.gif;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.gifdecoder.GifDecoder;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.bumptech.glide.util.Preconditions;
import com.bumptech.glide.util.Util;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

class GifFrameLoader {
    private final BitmapPool bitmapPool;
    private final List<FrameCallback> callbacks;
    private final Context context;
    private DelayTarget current;
    private Bitmap firstFrame;
    private final GifDecoder gifDecoder;
    private final Handler handler;
    private boolean isCleared;
    private boolean isLoadPending;
    private boolean isRunning;
    private DelayTarget next;
    private RequestBuilder<Bitmap> requestBuilder;
    private final RequestManager requestManager;
    private Transformation<Bitmap> transformation;

    public interface FrameCallback {
        void onFrameReady();
    }

    static class DelayTarget extends SimpleTarget<Bitmap> {
        private final Handler handler;
        private final int index;
        private Bitmap resource;
        private final long targetTime;

        DelayTarget(Handler handler, int index, long targetTime) {
            this.handler = handler;
            this.index = index;
            this.targetTime = targetTime;
        }

        Bitmap getResource() {
            return this.resource;
        }

        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
            this.resource = resource;
            this.handler.sendMessageAtTime(this.handler.obtainMessage(1, this), this.targetTime);
        }
    }

    private class FrameLoaderCallback implements Callback {
        private FrameLoaderCallback() {
        }

        public boolean handleMessage(Message msg) {
            if (msg.what == 1) {
                GifFrameLoader.this.onFrameReady(msg.obj);
                return true;
            }
            if (msg.what == 2) {
                GifFrameLoader.this.requestManager.clear((DelayTarget) msg.obj);
            }
            return false;
        }
    }

    static class FrameSignature implements Key {
        private final UUID uuid;

        public FrameSignature() {
            this(UUID.randomUUID());
        }

        FrameSignature(UUID uuid) {
            this.uuid = uuid;
        }

        public boolean equals(Object o) {
            if (o instanceof FrameSignature) {
                return ((FrameSignature) o).uuid.equals(this.uuid);
            }
            return false;
        }

        public int hashCode() {
            return this.uuid.hashCode();
        }

        public void updateDiskCacheKey(MessageDigest messageDigest) {
            throw new UnsupportedOperationException("Not implemented");
        }
    }

    public GifFrameLoader(Glide glide, GifDecoder gifDecoder, int width, int height, Transformation<Bitmap> transformation, Bitmap firstFrame) {
        this(glide.getContext(), glide.getBitmapPool(), Glide.with(glide.getContext()), gifDecoder, null, getRequestBuilder(Glide.with(glide.getContext()), width, height), transformation, firstFrame);
    }

    GifFrameLoader(Context context, BitmapPool bitmapPool, RequestManager requestManager, GifDecoder gifDecoder, Handler handler, RequestBuilder<Bitmap> requestBuilder, Transformation<Bitmap> transformation, Bitmap firstFrame) {
        this.callbacks = new ArrayList();
        this.isRunning = false;
        this.isLoadPending = false;
        this.requestManager = requestManager;
        if (handler == null) {
            handler = new Handler(Looper.getMainLooper(), new FrameLoaderCallback());
        }
        this.context = context;
        this.bitmapPool = bitmapPool;
        this.handler = handler;
        this.requestBuilder = requestBuilder;
        this.gifDecoder = gifDecoder;
        setFrameTransformation(transformation, firstFrame);
    }

    void setFrameTransformation(Transformation<Bitmap> transformation, Bitmap firstFrame) {
        this.transformation = (Transformation) Preconditions.checkNotNull(transformation);
        this.firstFrame = (Bitmap) Preconditions.checkNotNull(firstFrame);
        this.requestBuilder = this.requestBuilder.apply(new RequestOptions().transform(this.context, transformation));
    }

    Bitmap getFirstFrame() {
        return this.firstFrame;
    }

    void subscribe(FrameCallback frameCallback) {
        if (this.isCleared) {
            throw new IllegalStateException("Cannot subscribe to a cleared frame loader");
        }
        boolean start = this.callbacks.isEmpty();
        if (this.callbacks.contains(frameCallback)) {
            throw new IllegalStateException("Cannot subscribe twice in a row");
        }
        this.callbacks.add(frameCallback);
        if (start) {
            start();
        }
    }

    void unsubscribe(FrameCallback frameCallback) {
        this.callbacks.remove(frameCallback);
        if (this.callbacks.isEmpty()) {
            stop();
        }
    }

    int getWidth() {
        return getCurrentFrame().getWidth();
    }

    int getHeight() {
        return getCurrentFrame().getHeight();
    }

    int getSize() {
        return this.gifDecoder.getByteSize() + getFrameSize();
    }

    int getCurrentIndex() {
        return this.current != null ? this.current.index : -1;
    }

    private int getFrameSize() {
        return Util.getBitmapByteSize(getCurrentFrame().getWidth(), getCurrentFrame().getHeight(), getCurrentFrame().getConfig());
    }

    ByteBuffer getBuffer() {
        return this.gifDecoder.getData().asReadOnlyBuffer();
    }

    int getFrameCount() {
        return this.gifDecoder.getFrameCount();
    }

    private void start() {
        if (!this.isRunning) {
            this.isRunning = true;
            this.isCleared = false;
            loadNextFrame();
        }
    }

    private void stop() {
        this.isRunning = false;
    }

    void clear() {
        this.callbacks.clear();
        recycleFirstFrame();
        stop();
        if (this.current != null) {
            this.requestManager.clear(this.current);
            this.current = null;
        }
        if (this.next != null) {
            this.requestManager.clear(this.next);
            this.next = null;
        }
        this.gifDecoder.clear();
        this.isCleared = true;
    }

    Bitmap getCurrentFrame() {
        return this.current != null ? this.current.getResource() : this.firstFrame;
    }

    private void loadNextFrame() {
        if (this.isRunning && !this.isLoadPending) {
            this.isLoadPending = true;
            long targetTime = SystemClock.uptimeMillis() + ((long) this.gifDecoder.getNextDelay());
            this.gifDecoder.advance();
            this.next = new DelayTarget(this.handler, this.gifDecoder.getCurrentFrameIndex(), targetTime);
            this.requestBuilder.clone().apply(RequestOptions.signatureOf(new FrameSignature())).load(this.gifDecoder).into(this.next);
        }
    }

    private void recycleFirstFrame() {
        if (this.firstFrame != null) {
            this.bitmapPool.put(this.firstFrame);
            this.firstFrame = null;
        }
    }

    void onFrameReady(DelayTarget delayTarget) {
        if (this.isCleared) {
            this.handler.obtainMessage(2, delayTarget).sendToTarget();
            return;
        }
        if (delayTarget.getResource() != null) {
            recycleFirstFrame();
            DelayTarget previous = this.current;
            this.current = delayTarget;
            for (int i = this.callbacks.size() - 1; i >= 0; i--) {
                ((FrameCallback) this.callbacks.get(i)).onFrameReady();
            }
            if (previous != null) {
                this.handler.obtainMessage(2, previous).sendToTarget();
            }
        }
        this.isLoadPending = false;
        loadNextFrame();
    }

    private static RequestBuilder<Bitmap> getRequestBuilder(RequestManager requestManager, int width, int height) {
        return requestManager.asBitmap().apply(((RequestOptions) RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE).skipMemoryCache(true)).override(width, height));
    }
}
