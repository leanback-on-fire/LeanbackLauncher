package com.rockon999.android.leanbacklauncher.protobuf.nano;

public final class InternalNano {
    public static final Object LAZY_INIT_LOCK;

    private InternalNano() {
    }

    static {
        LAZY_INIT_LOCK = new Object();
    }

    public static void cloneUnknownFieldData(ExtendableMessageNano original, ExtendableMessageNano cloned) {
        if (original.unknownFieldData != null) {
            cloned.unknownFieldData = original.unknownFieldData.clone();
        }
    }
}
