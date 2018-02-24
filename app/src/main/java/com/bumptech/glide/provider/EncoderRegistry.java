package com.bumptech.glide.provider;

import com.bumptech.glide.load.Encoder;
import java.util.ArrayList;
import java.util.List;

public class EncoderRegistry {
    private final List<Entry<?>> encoders = new ArrayList();

    private static final class Entry<T> {
        private final Class<T> dataClass;
        private final Encoder<T> encoder;

        public Entry(Class<T> dataClass, Encoder<T> encoder) {
            this.dataClass = dataClass;
            this.encoder = encoder;
        }

        public boolean handles(Class<?> dataClass) {
            return this.dataClass.isAssignableFrom(dataClass);
        }
    }

    public synchronized <T> Encoder<T> getEncoder(Class<T> dataClass) {
        Encoder<T> access$000;
        for (Entry<?> entry : this.encoders) {
            if (entry.handles(dataClass)) {
                access$000 = entry.encoder;
                break;
            }
        }
        access$000 = null;
        return access$000;
    }

    public synchronized <T> void add(Class<T> dataClass, Encoder<T> encoder) {
        this.encoders.add(new Entry(dataClass, encoder));
    }
}
