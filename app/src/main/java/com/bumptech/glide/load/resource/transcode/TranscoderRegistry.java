package com.bumptech.glide.load.resource.transcode;

import java.util.ArrayList;
import java.util.List;

public class TranscoderRegistry {
    private final List<Entry<?, ?>> transcoders = new ArrayList();

    private static final class Entry<Z, R> {
        private final Class<Z> fromClass;
        private final Class<R> toClass;
        private final ResourceTranscoder<Z, R> transcoder;

        Entry(Class<Z> fromClass, Class<R> toClass, ResourceTranscoder<Z, R> transcoder) {
            this.fromClass = fromClass;
            this.toClass = toClass;
            this.transcoder = transcoder;
        }

        public boolean handles(Class<?> fromClass, Class<?> toClass) {
            return this.fromClass.isAssignableFrom(fromClass) && toClass.isAssignableFrom(this.toClass);
        }
    }

    public synchronized <Z, R> void register(Class<Z> decodedClass, Class<R> transcodedClass, ResourceTranscoder<Z, R> transcoder) {
        this.transcoders.add(new Entry(decodedClass, transcodedClass, transcoder));
    }

    public synchronized <Z, R> ResourceTranscoder<Z, R> get(Class<Z> resourceClass, Class<R> transcodedClass) {
        ResourceTranscoder<Z, R> resourceTranscoder;
        if (transcodedClass.isAssignableFrom(resourceClass)) {
            resourceTranscoder = UnitTranscoder.get();
        } else {
            for (Entry<?, ?> entry : this.transcoders) {
                if (entry.handles(resourceClass, transcodedClass)) {
                    resourceTranscoder = entry.transcoder;
                }
            }
            String valueOf = String.valueOf(resourceClass);
            String valueOf2 = String.valueOf(transcodedClass);
            throw new IllegalArgumentException(new StringBuilder((String.valueOf(valueOf).length() + 47) + String.valueOf(valueOf2).length()).append("No transcoder registered to transcode from ").append(valueOf).append(" to ").append(valueOf2).toString());
        }
        return resourceTranscoder;
    }

    public synchronized <Z, R> List<Class<R>> getTranscodeClasses(Class<Z> resourceClass, Class<R> transcodeClass) {
        List<Class<R>> transcodeClasses;
        transcodeClasses = new ArrayList();
        if (transcodeClass.isAssignableFrom(resourceClass)) {
            transcodeClasses.add(transcodeClass);
        } else {
            for (Entry<?, ?> entry : this.transcoders) {
                if (entry.handles(resourceClass, transcodeClass)) {
                    transcodeClasses.add(transcodeClass);
                }
            }
        }
        return transcodeClasses;
    }
}
