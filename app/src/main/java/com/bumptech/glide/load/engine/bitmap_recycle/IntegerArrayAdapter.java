package com.bumptech.glide.load.engine.bitmap_recycle;

import java.util.Arrays;

public final class IntegerArrayAdapter implements ArrayAdapterInterface<int[]> {
    public String getTag() {
        return "IntegerArrayPool";
    }

    public int getArrayLength(int[] array) {
        return array.length;
    }

    public void resetArray(int[] array) {
        Arrays.fill(array, 0);
    }

    public int[] newArray(int length) {
        return new int[length];
    }

    public int getElementSizeInBytes() {
        return 4;
    }
}
