package com.bumptech.glide.load.resource.bitmap;

public abstract class DownsampleStrategy {
    public static final DownsampleStrategy AT_LEAST = new AtLeast();
    public static final DownsampleStrategy AT_MOST = new AtMost();
    public static final DownsampleStrategy CENTER_INSIDE = new CenterInside();
    public static final DownsampleStrategy CENTER_OUTSIDE = new CenterOutside();
    public static final DownsampleStrategy DEFAULT = AT_LEAST;
    public static final DownsampleStrategy FIT_CENTER = new FitCenter();
    public static final DownsampleStrategy NONE = new None();

    private static class AtLeast extends DownsampleStrategy {
        private AtLeast() {
        }

        public float getScaleFactor(int sourceWidth, int sourceHeight, int requestedWidth, int requestedHeight) {
            int minIntegerFactor = Math.min(sourceHeight / requestedHeight, sourceWidth / requestedWidth);
            if (minIntegerFactor == 0) {
                return 1.0f;
            }
            return 1.0f / ((float) Integer.highestOneBit(minIntegerFactor));
        }

        public SampleSizeRounding getSampleSizeRounding(int sourceWidth, int sourceHeight, int requestedWidth, int requestedHeight) {
            return SampleSizeRounding.QUALITY;
        }
    }

    private static class AtMost extends DownsampleStrategy {
        private AtMost() {
        }

        public float getScaleFactor(int sourceWidth, int sourceHeight, int requestedWidth, int requestedHeight) {
            int i = 1;
            int maxIntegerFactor = (int) Math.ceil((double) Math.max(((float) sourceHeight) / ((float) requestedHeight), ((float) sourceWidth) / ((float) requestedWidth)));
            int lesserOrEqualSampleSize = Math.max(1, Integer.highestOneBit(maxIntegerFactor));
            if (lesserOrEqualSampleSize >= maxIntegerFactor) {
                i = 0;
            }
            return 1.0f / ((float) (lesserOrEqualSampleSize << i));
        }

        public SampleSizeRounding getSampleSizeRounding(int sourceWidth, int sourceHeight, int requestedWidth, int requestedHeight) {
            return SampleSizeRounding.MEMORY;
        }
    }

    private static class CenterInside extends DownsampleStrategy {
        private CenterInside() {
        }

        public float getScaleFactor(int sourceWidth, int sourceHeight, int requestedWidth, int requestedHeight) {
            return Math.min(1.0f, FIT_CENTER.getScaleFactor(sourceWidth, sourceHeight, requestedWidth, requestedHeight));
        }

        public SampleSizeRounding getSampleSizeRounding(int sourceWidth, int sourceHeight, int requestedWidth, int requestedHeight) {
            return SampleSizeRounding.QUALITY;
        }
    }

    private static class CenterOutside extends DownsampleStrategy {
        private CenterOutside() {
        }

        public float getScaleFactor(int sourceWidth, int sourceHeight, int requestedWidth, int requestedHeight) {
            return Math.max(((float) requestedWidth) / ((float) sourceWidth), ((float) requestedHeight) / ((float) sourceHeight));
        }

        public SampleSizeRounding getSampleSizeRounding(int sourceWidth, int sourceHeight, int requestedWidth, int requestedHeight) {
            return SampleSizeRounding.QUALITY;
        }
    }

    private static class FitCenter extends DownsampleStrategy {
        private FitCenter() {
        }

        public float getScaleFactor(int sourceWidth, int sourceHeight, int requestedWidth, int requestedHeight) {
            return Math.min(((float) requestedWidth) / ((float) sourceWidth), ((float) requestedHeight) / ((float) sourceHeight));
        }

        public SampleSizeRounding getSampleSizeRounding(int sourceWidth, int sourceHeight, int requestedWidth, int requestedHeight) {
            return SampleSizeRounding.QUALITY;
        }
    }

    private static class None extends DownsampleStrategy {
        private None() {
        }

        public float getScaleFactor(int sourceWidth, int sourceHeight, int requestedWidth, int requestedHeight) {
            return 1.0f;
        }

        public SampleSizeRounding getSampleSizeRounding(int sourceWidth, int sourceHeight, int requestedWidth, int requestedHeight) {
            return SampleSizeRounding.QUALITY;
        }
    }

    public enum SampleSizeRounding {
        MEMORY,
        QUALITY
    }

    public abstract SampleSizeRounding getSampleSizeRounding(int i, int i2, int i3, int i4);

    public abstract float getScaleFactor(int i, int i2, int i3, int i4);
}
