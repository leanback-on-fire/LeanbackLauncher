package com.bumptech.glide.load.engine.bitmap_recycle;

import java.util.Arrays;

public final class ByteArrayAdapter implements ArrayAdapterInterface<byte[]> {
    public String getTag() {
        return "ByteArrayPool";
    }

    public int getArrayLength(byte[] array) {
        return array.length;
    }

    public void resetArray(byte[] array) {
        Arrays.fill(array, (byte) 0);
    }

    public byte[] newArray(int length) {
        return new byte[length];
    }

    public int getElementSizeInBytes() {
        return 1;
    }
}
