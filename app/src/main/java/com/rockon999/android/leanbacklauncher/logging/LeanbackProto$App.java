package com.rockon999.android.leanbacklauncher.logging;

import com.rockon999.android.leanbacklauncher.protobuf.nano.CodedOutputByteBufferNano;
import com.rockon999.android.leanbacklauncher.protobuf.nano.ExtendableMessageNano;
import com.rockon999.android.leanbacklauncher.protobuf.nano.InternalNano;

import java.io.IOException;

public final class LeanbackProto$App extends ExtendableMessageNano<LeanbackProto$App> {
    private static volatile LeanbackProto$App[] _emptyArray;
    public String appTitle;
    public String packageName;
    public Integer position;
    public Float score;

    public static LeanbackProto$App[] emptyArray() {
        if (_emptyArray == null) {
            synchronized (InternalNano.LAZY_INIT_LOCK) {
                if (_emptyArray == null) {
                    _emptyArray = new LeanbackProto$App[0];
                }
            }
        }
        return _emptyArray;
    }

    public LeanbackProto$App() {
        clear();
    }

    public LeanbackProto$App clear() {
        this.position = null;
        this.packageName = null;
        this.appTitle = null;
        this.score = null;
        this.unknownFieldData = null;
        this.cachedSize = -1;
        return this;
    }

    public void writeTo(CodedOutputByteBufferNano output) throws IOException {
        if (this.position != null) {
            output.writeUInt32(1, this.position);
        }
        if (this.packageName != null) {
            output.writeString(2, this.packageName);
        }
        if (this.appTitle != null) {
            output.writeString(3, this.appTitle);
        }
        if (this.score != null) {
            output.writeFloat(8, this.score);
        }
        super.writeTo(output);
    }

    protected int computeSerializedSize() {
        int size = super.computeSerializedSize();
        if (this.position != null) {
            size += CodedOutputByteBufferNano.computeUInt32Size(1, this.position);
        }
        if (this.packageName != null) {
            size += CodedOutputByteBufferNano.computeStringSize(2, this.packageName);
        }
        if (this.appTitle != null) {
            size += CodedOutputByteBufferNano.computeStringSize(3, this.appTitle);
        }
        if (this.score == null) {
            return size;
        }
        return size + CodedOutputByteBufferNano.computeFloatSize(8, this.score);
    }
}
