package com.bumptech.glide.request.target;

import com.bumptech.glide.util.Util;

public abstract class SimpleTarget<Z> extends BaseTarget<Z> {
    private final int height;
    private final int width;

    public SimpleTarget() {
        this(Integer.MIN_VALUE, Integer.MIN_VALUE);
    }

    public SimpleTarget(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public final void getSize(SizeReadyCallback cb) {
        if (Util.isValidDimensions(this.width, this.height)) {
            cb.onSizeReady(this.width, this.height);
            return;
        }
        int i = this.width;
        throw new IllegalArgumentException("Width and height must both be > 0 or Target#SIZE_ORIGINAL, but given width: " + i + " and height: " + this.height + ", either provide dimensions in the constructor or call override()");
    }
}
