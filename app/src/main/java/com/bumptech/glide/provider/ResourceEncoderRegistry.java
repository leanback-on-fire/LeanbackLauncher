package com.bumptech.glide.provider;

import com.bumptech.glide.load.ResourceEncoder;
import java.util.ArrayList;
import java.util.List;

public class ResourceEncoderRegistry {
    final List<Entry<?>> encoders = new ArrayList();

    private static final class Entry<T> {
        private final ResourceEncoder<T> encoder;
        private final Class<T> resourceClass;

        Entry(Class<T> resourceClass, ResourceEncoder<T> encoder) {
            this.resourceClass = resourceClass;
            this.encoder = encoder;
        }

        private boolean handles(Class<?> resourceClass) {
            return this.resourceClass.isAssignableFrom(resourceClass);
        }
    }

    public synchronized <Z> void add(Class<Z> resourceClass, ResourceEncoder<Z> encoder) {
        this.encoders.add(new Entry(resourceClass, encoder));
    }

    public synchronized <Z> ResourceEncoder<Z> get(Class<Z> resourceClass) {
        ResourceEncoder<Z> access$100;
        int size = this.encoders.size();
        for (int i = 0; i < size; i++) {
            Entry<?> entry = (Entry) this.encoders.get(i);
            if (entry.handles(resourceClass)) {
                access$100 = entry.encoder;
                break;
            }
        }
        access$100 = null;
        return access$100;
    }
}
