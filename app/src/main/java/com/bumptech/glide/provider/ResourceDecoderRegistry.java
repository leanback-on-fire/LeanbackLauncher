package com.bumptech.glide.provider;

import com.bumptech.glide.load.ResourceDecoder;
import java.util.ArrayList;
import java.util.List;

public class ResourceDecoderRegistry {
    private final List<Entry<?, ?>> decoders = new ArrayList();

    private static class Entry<T, R> {
        private final Class<T> dataClass;
        private final ResourceDecoder<T, R> decoder;
        private final Class<R> resourceClass;

        public Entry(Class<T> dataClass, Class<R> resourceClass, ResourceDecoder<T, R> decoder) {
            this.dataClass = dataClass;
            this.resourceClass = resourceClass;
            this.decoder = decoder;
        }

        public boolean handles(Class<?> dataClass, Class<?> resourceClass) {
            return this.dataClass.isAssignableFrom(dataClass) && resourceClass.isAssignableFrom(this.resourceClass);
        }
    }

    public synchronized <T, R> List<ResourceDecoder<T, R>> getDecoders(Class<T> dataClass, Class<R> resourceClass) {
        List<ResourceDecoder<T, R>> result;
        result = new ArrayList();
        for (Entry<?, ?> entry : this.decoders) {
            if (entry.handles(dataClass, resourceClass)) {
                result.add(entry.decoder);
            }
        }
        return result;
    }

    public synchronized <T, R> List<Class<R>> getResourceClasses(Class<T> dataClass, Class<R> resourceClass) {
        List<Class<R>> result;
        result = new ArrayList();
        for (Entry<?, ?> entry : this.decoders) {
            if (entry.handles(dataClass, resourceClass)) {
                result.add(entry.resourceClass);
            }
        }
        return result;
    }

    public synchronized <T, R> void append(ResourceDecoder<T, R> decoder, Class<T> dataClass, Class<R> resourceClass) {
        this.decoders.add(new Entry(dataClass, resourceClass, decoder));
    }

    public synchronized <T, R> void prepend(ResourceDecoder<T, R> decoder, Class<T> dataClass, Class<R> resourceClass) {
        this.decoders.add(0, new Entry(dataClass, resourceClass, decoder));
    }
}
