package com.bumptech.glide;

import android.support.v4.util.Pools.Pool;
import com.bumptech.glide.load.Encoder;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.ResourceEncoder;
import com.bumptech.glide.load.data.DataRewinder;
import com.bumptech.glide.load.data.DataRewinder.Factory;
import com.bumptech.glide.load.data.DataRewinderRegistry;
import com.bumptech.glide.load.engine.DecodePath;
import com.bumptech.glide.load.engine.LoadPath;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.ModelLoaderRegistry;
import com.bumptech.glide.load.resource.transcode.ResourceTranscoder;
import com.bumptech.glide.load.resource.transcode.TranscoderRegistry;
import com.bumptech.glide.provider.EncoderRegistry;
import com.bumptech.glide.provider.LoadPathCache;
import com.bumptech.glide.provider.ModelToResourceClassCache;
import com.bumptech.glide.provider.ResourceDecoderRegistry;
import com.bumptech.glide.provider.ResourceEncoderRegistry;
import com.bumptech.glide.util.pool.FactoryPools;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Registry {
    private final DataRewinderRegistry dataRewinderRegistry = new DataRewinderRegistry();
    private final ResourceDecoderRegistry decoderRegistry = new ResourceDecoderRegistry();
    private final EncoderRegistry encoderRegistry = new EncoderRegistry();
    private final Pool<List<Exception>> exceptionListPool = FactoryPools.threadSafeList();
    private final LoadPathCache loadPathCache = new LoadPathCache();
    private final ModelLoaderRegistry modelLoaderRegistry = new ModelLoaderRegistry(this.exceptionListPool);
    private final ModelToResourceClassCache modelToResourceClassCache = new ModelToResourceClassCache();
    private final ResourceEncoderRegistry resourceEncoderRegistry = new ResourceEncoderRegistry();
    private final TranscoderRegistry transcoderRegistry = new TranscoderRegistry();

    public static class MissingComponentException extends RuntimeException {
        public MissingComponentException(String message) {
            super(message);
        }
    }

    public static class NoModelLoaderAvailableException extends MissingComponentException {
        public NoModelLoaderAvailableException(Object model) {
            String valueOf = String.valueOf(model);
            super(new StringBuilder(String.valueOf(valueOf).length() + 43).append("Failed to find any ModelLoaders for model: ").append(valueOf).toString());
        }

        public NoModelLoaderAvailableException(Class modelClass, Class dataClass) {
            String valueOf = String.valueOf(modelClass);
            String valueOf2 = String.valueOf(dataClass);
            super(new StringBuilder((String.valueOf(valueOf).length() + 54) + String.valueOf(valueOf2).length()).append("Failed to find any ModelLoaders for model: ").append(valueOf).append(" and data: ").append(valueOf2).toString());
        }
    }

    public static class NoResultEncoderAvailableException extends MissingComponentException {
        public NoResultEncoderAvailableException(Class<?> resourceClass) {
            String valueOf = String.valueOf(resourceClass);
            super(new StringBuilder(String.valueOf(valueOf).length() + 50).append("Failed to find result encoder for resource class: ").append(valueOf).toString());
        }
    }

    public static class NoSourceEncoderAvailableException extends MissingComponentException {
        public NoSourceEncoderAvailableException(Class<?> dataClass) {
            String valueOf = String.valueOf(dataClass);
            super(new StringBuilder(String.valueOf(valueOf).length() + 46).append("Failed to find source encoder for data class: ").append(valueOf).toString());
        }
    }

    public <Data> Registry register(Class<Data> dataClass, Encoder<Data> encoder) {
        this.encoderRegistry.add(dataClass, encoder);
        return this;
    }

    public <Data, TResource> Registry append(Class<Data> dataClass, Class<TResource> resourceClass, ResourceDecoder<Data, TResource> decoder) {
        this.decoderRegistry.append(decoder, dataClass, resourceClass);
        return this;
    }

    public <Data, TResource> Registry prepend(Class<Data> dataClass, Class<TResource> resourceClass, ResourceDecoder<Data, TResource> decoder) {
        this.decoderRegistry.prepend(decoder, dataClass, resourceClass);
        return this;
    }

    public <TResource> Registry register(Class<TResource> resourceClass, ResourceEncoder<TResource> encoder) {
        this.resourceEncoderRegistry.add(resourceClass, encoder);
        return this;
    }

    public Registry register(Factory factory) {
        this.dataRewinderRegistry.register(factory);
        return this;
    }

    public <TResource, Transcode> Registry register(Class<TResource> resourceClass, Class<Transcode> transcodeClass, ResourceTranscoder<TResource, Transcode> transcoder) {
        this.transcoderRegistry.register(resourceClass, transcodeClass, transcoder);
        return this;
    }

    public <Model, Data> Registry append(Class<Model> modelClass, Class<Data> dataClass, ModelLoaderFactory<Model, Data> factory) {
        this.modelLoaderRegistry.append(modelClass, dataClass, factory);
        return this;
    }

    public <Data, TResource, Transcode> LoadPath<Data, TResource, Transcode> getLoadPath(Class<Data> dataClass, Class<TResource> resourceClass, Class<Transcode> transcodeClass) {
        LoadPath<Data, TResource, Transcode> result = this.loadPathCache.get(dataClass, resourceClass, transcodeClass);
        if (result == null && !this.loadPathCache.contains(dataClass, resourceClass, transcodeClass)) {
            List<DecodePath<Data, TResource, Transcode>> decodePaths = getDecodePaths(dataClass, resourceClass, transcodeClass);
            if (decodePaths.isEmpty()) {
                result = null;
            } else {
                result = new LoadPath(dataClass, resourceClass, transcodeClass, decodePaths, this.exceptionListPool);
            }
            this.loadPathCache.put(dataClass, resourceClass, transcodeClass, result);
        }
        return result;
    }

    private <Data, TResource, Transcode> List<DecodePath<Data, TResource, Transcode>> getDecodePaths(Class<Data> dataClass, Class<TResource> resourceClass, Class<Transcode> transcodeClass) {
        List<DecodePath<Data, TResource, Transcode>> decodePaths = new ArrayList();
        for (Class<TResource> registeredResourceClass : this.decoderRegistry.getResourceClasses(dataClass, resourceClass)) {
            for (Class<Transcode> registeredTranscodeClass : this.transcoderRegistry.getTranscodeClasses(registeredResourceClass, transcodeClass)) {
                decodePaths.add(new DecodePath(dataClass, registeredResourceClass, registeredTranscodeClass, this.decoderRegistry.getDecoders(dataClass, registeredResourceClass), this.transcoderRegistry.get(registeredResourceClass, registeredTranscodeClass), this.exceptionListPool));
            }
        }
        return decodePaths;
    }

    public <Model, TResource, Transcode> List<Class<?>> getRegisteredResourceClasses(Class<Model> modelClass, Class<TResource> resourceClass, Class<Transcode> transcodeClass) {
        List<Class<?>> result = this.modelToResourceClassCache.get(modelClass, resourceClass);
        if (result == null) {
            result = new ArrayList();
            for (Class<?> dataClass : this.modelLoaderRegistry.getDataClasses(modelClass)) {
                for (Class<?> registeredResourceClass : this.decoderRegistry.getResourceClasses(dataClass, resourceClass)) {
                    if (!(this.transcoderRegistry.getTranscodeClasses(registeredResourceClass, transcodeClass).isEmpty() || result.contains(registeredResourceClass))) {
                        result.add(registeredResourceClass);
                    }
                }
            }
            this.modelToResourceClassCache.put(modelClass, resourceClass, Collections.unmodifiableList(result));
        }
        return result;
    }

    public boolean isResourceEncoderAvailable(Resource<?> resource) {
        return this.resourceEncoderRegistry.get(resource.getResourceClass()) != null;
    }

    public <X> ResourceEncoder<X> getResultEncoder(Resource<X> resource) throws NoResultEncoderAvailableException {
        ResourceEncoder<X> resourceEncoder = this.resourceEncoderRegistry.get(resource.getResourceClass());
        if (resourceEncoder != null) {
            return resourceEncoder;
        }
        throw new NoResultEncoderAvailableException(resource.getResourceClass());
    }

    public <X> Encoder<X> getSourceEncoder(X data) throws NoSourceEncoderAvailableException {
        Encoder<X> encoder = this.encoderRegistry.getEncoder(data.getClass());
        if (encoder != null) {
            return encoder;
        }
        throw new NoSourceEncoderAvailableException(data.getClass());
    }

    public <X> DataRewinder<X> getRewinder(X data) {
        return this.dataRewinderRegistry.build(data);
    }

    public <Model> List<ModelLoader<Model, ?>> getModelLoaders(Model model) {
        List<ModelLoader<Model, ?>> result = this.modelLoaderRegistry.getModelLoaders(model);
        if (!result.isEmpty()) {
            return result;
        }
        throw new NoModelLoaderAvailableException(model);
    }
}
