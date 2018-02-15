package com.rockon999.android.leanbacklauncher.logging;

import com.rockon999.android.leanbacklauncher.protobuf.nano.CodedOutputByteBufferNano;
import com.rockon999.android.leanbacklauncher.protobuf.nano.ExtendableMessageNano;
import java.io.IOException;

public final class LeanbackProto$RankerAction extends ExtendableMessageNano<LeanbackProto$RankerAction> {
    public Integer actionType;
    public String packageName;
    public Integer rowPosition;
    public String tag;

    public LeanbackProto$RankerAction() {
        clear();
    }

    public LeanbackProto$RankerAction clear() {
        this.packageName = null;
        this.actionType = null;
        this.rowPosition = null;
        this.tag = null;
        this.unknownFieldData = null;
        this.cachedSize = -1;
        return this;
    }

    public void writeTo(CodedOutputByteBufferNano output) throws IOException {
        if (this.packageName != null) {
            output.writeString(1, this.packageName);
        }
        if (this.actionType != null) {
            output.writeUInt32(2, this.actionType);
        }
        if (this.rowPosition != null) {
            output.writeUInt32(3, this.rowPosition);
        }
        if (this.tag != null) {
            output.writeString(4, this.tag);
        }
        super.writeTo(output);
    }

    protected int computeSerializedSize() {
        int size = super.computeSerializedSize();
        if (this.packageName != null) {
            size += CodedOutputByteBufferNano.computeStringSize(1, this.packageName);
        }
        if (this.actionType != null) {
            size += CodedOutputByteBufferNano.computeUInt32Size(2, this.actionType);
        }
        if (this.rowPosition != null) {
            size += CodedOutputByteBufferNano.computeUInt32Size(3, this.rowPosition);
        }
        if (this.tag == null) {
            return size;
        }
        return size + CodedOutputByteBufferNano.computeStringSize(4, this.tag);
    }
}
