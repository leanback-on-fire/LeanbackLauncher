package com.bumptech.glide.util.pool;

public abstract class StateVerifier {

    private static class DefaultStateVerifier extends StateVerifier {
        private volatile boolean isReleased;

        private DefaultStateVerifier() {
            super();
        }

        public void throwIfRecycled() {
            if (this.isReleased) {
                throw new IllegalStateException("Already released");
            }
        }

        public void setRecycled(boolean isRecycled) {
            this.isReleased = isRecycled;
        }
    }

    abstract void setRecycled(boolean z);

    public abstract void throwIfRecycled();

    public static StateVerifier newInstance() {
        return new DefaultStateVerifier();
    }

    private StateVerifier() {
    }
}
