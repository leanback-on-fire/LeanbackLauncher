package com.rockon999.android.leanbacklauncher.logging;

import com.rockon999.android.leanbacklauncher.protobuf.nano.CodedOutputByteBufferNano;
import com.rockon999.android.leanbacklauncher.protobuf.nano.ExtendableMessageNano;
import com.rockon999.android.leanbacklauncher.protobuf.nano.InternalNano;

import java.io.IOException;

public final class LeanbackProto$Recommendation extends ExtendableMessageNano<LeanbackProto$Recommendation> {
    private static volatile LeanbackProto$Recommendation[] _emptyArray;
    public Integer bucketId;
    public Float normalizedPriority;
    public Integer notificationId;
    public String packageName;
    public Integer position;
    public Float score;
    public String tagName;
    public Integer unnormalizedPriority;

    public static LeanbackProto$Recommendation[] emptyArray() {
        if (_emptyArray == null) {
            synchronized (InternalNano.LAZY_INIT_LOCK) {
                if (_emptyArray == null) {
                    _emptyArray = new LeanbackProto$Recommendation[0];
                }
            }
        }
        return _emptyArray;
    }

    public LeanbackProto$Recommendation() {
        clear();
    }

    public LeanbackProto$Recommendation clear() {
        this.position = null;
        this.packageName = null;
        this.tagName = null;
        this.bucketId = null;
        this.notificationId = null;
        this.unnormalizedPriority = null;
        this.normalizedPriority = null;
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
        if (this.tagName != null) {
            output.writeString(3, this.tagName);
        }
        if (this.bucketId != null) {
            output.writeUInt32(4, this.bucketId);
        }
        if (this.notificationId != null) {
            output.writeUInt32(5, this.notificationId);
        }
        if (this.unnormalizedPriority != null) {
            output.writeInt32(6, this.unnormalizedPriority);
        }
        if (this.normalizedPriority != null) {
            output.writeFloat(7, this.normalizedPriority);
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
        if (this.tagName != null) {
            size += CodedOutputByteBufferNano.computeStringSize(3, this.tagName);
        }
        if (this.bucketId != null) {
            size += CodedOutputByteBufferNano.computeUInt32Size(4, this.bucketId);
        }
        if (this.notificationId != null) {
            size += CodedOutputByteBufferNano.computeUInt32Size(5, this.notificationId);
        }
        if (this.unnormalizedPriority != null) {
            size += CodedOutputByteBufferNano.computeInt32Size(6, this.unnormalizedPriority);
        }
        if (this.normalizedPriority != null) {
            size += CodedOutputByteBufferNano.computeFloatSize(7, this.normalizedPriority);
        }
        if (this.score == null) {
            return size;
        }
        return size + CodedOutputByteBufferNano.computeFloatSize(8, this.score);
    }
}
