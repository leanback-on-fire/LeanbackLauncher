package com.rockon999.android.leanbacklauncher.protobuf.nano;

public final class WireFormatNano {
    public static final boolean[] EMPTY_BOOLEAN_ARRAY;
    public static final byte[] EMPTY_BYTES;
    public static final byte[][] EMPTY_BYTES_ARRAY;
    public static final double[] EMPTY_DOUBLE_ARRAY;
    public static final float[] EMPTY_FLOAT_ARRAY;
    public static final int[] EMPTY_INT_ARRAY;
    public static final long[] EMPTY_LONG_ARRAY;
    public static final String[] EMPTY_STRING_ARRAY;

    private WireFormatNano() {
    }

    public static int getTagFieldNumber(int tag) {
        return tag >>> 3;
    }

    static int makeTag(int fieldNumber, int wireType) {
        return (fieldNumber << 3) | wireType;
    }

    static {
        EMPTY_INT_ARRAY = new int[0];
        EMPTY_LONG_ARRAY = new long[0];
        EMPTY_FLOAT_ARRAY = new float[0];
        EMPTY_DOUBLE_ARRAY = new double[0];
        EMPTY_BOOLEAN_ARRAY = new boolean[0];
        EMPTY_STRING_ARRAY = new String[0];
        EMPTY_BYTES_ARRAY = new byte[0][];
        EMPTY_BYTES = new byte[0];
    }
}
