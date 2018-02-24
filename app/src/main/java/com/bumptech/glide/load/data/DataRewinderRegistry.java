package com.bumptech.glide.load.data;

import com.bumptech.glide.load.data.DataRewinder.Factory;
import com.bumptech.glide.util.Preconditions;
import java.util.HashMap;
import java.util.Map;

public class DataRewinderRegistry {
    private static final Factory DEFAULT_FACTORY = new Factory<Object>() {
        public DataRewinder<Object> build(Object data) {
            return new DefaultRewinder(data);
        }

        public Class<Object> getDataClass() {
            throw new UnsupportedOperationException("Not implemented");
        }
    };
    private final Map<Class, Factory> rewinders = new HashMap();

    private static class DefaultRewinder implements DataRewinder<Object> {
        private final Object data;

        public DefaultRewinder(Object data) {
            this.data = data;
        }

        public Object rewindAndGet() {
            return this.data;
        }

        public void cleanup() {
        }
    }

    public synchronized void register(Factory factory) {
        this.rewinders.put(factory.getDataClass(), factory);
    }

    public synchronized <T> DataRewinder<T> build(T data) {
        Factory result;
        Preconditions.checkNotNull(data);
        result = (Factory) this.rewinders.get(data.getClass());
        if (result == null) {
            for (Factory<?> registeredFactory : this.rewinders.values()) {
                if (registeredFactory.getDataClass().isAssignableFrom(data.getClass())) {
                    result = registeredFactory;
                    break;
                }
            }
        }
        if (result == null) {
            result = DEFAULT_FACTORY;
        }
        return result.build(data);
    }
}
