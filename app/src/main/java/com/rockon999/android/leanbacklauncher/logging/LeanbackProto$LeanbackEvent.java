package com.rockon999.android.leanbacklauncher.logging;

import com.rockon999.android.leanbacklauncher.protobuf.nano.CodedOutputByteBufferNano;
import com.rockon999.android.leanbacklauncher.protobuf.nano.ExtendableMessageNano;

import java.io.IOException;

public final class LeanbackProto$LeanbackEvent extends ExtendableMessageNano<LeanbackProto$LeanbackEvent> {
    public LeanbackProto$AppRankAction appRankAction;
    public LeanbackProto$LeanbackHeader header;
    public LeanbackProto$RankerAction rankerAction;
    public LeanbackProto$RecommendationInsertAction recommendationInsertAction;
    public LeanbackProto$RecommendationRankAction recommendationRankAction;
    public Long timestamp;
    public Integer type;

    public LeanbackProto$LeanbackEvent() {
        clear();
    }

    public LeanbackProto$LeanbackEvent clear() {
        this.type = null;
        this.timestamp = null;
        this.rankerAction = null;
        this.recommendationRankAction = null;
        this.appRankAction = null;
        this.recommendationInsertAction = null;
        this.header = null;
        this.unknownFieldData = null;
        this.cachedSize = -1;
        return this;
    }

    public void writeTo(CodedOutputByteBufferNano output) throws IOException {
        if (this.type != null) {
            output.writeInt32(1, this.type);
        }
        if (this.timestamp != null) {
            output.writeInt64(2, this.timestamp);
        }
        if (this.rankerAction != null) {
            output.writeMessage(3, this.rankerAction);
        }
        if (this.recommendationRankAction != null) {
            output.writeMessage(4, this.recommendationRankAction);
        }
        if (this.appRankAction != null) {
            output.writeMessage(5, this.appRankAction);
        }
        if (this.recommendationInsertAction != null) {
            output.writeMessage(6, this.recommendationInsertAction);
        }
        if (this.header != null) {
            output.writeMessage(7, this.header);
        }
        super.writeTo(output);
    }

    protected int computeSerializedSize() {
        int size = super.computeSerializedSize();
        if (this.type != null) {
            size += CodedOutputByteBufferNano.computeInt32Size(1, this.type);
        }
        if (this.timestamp != null) {
            size += CodedOutputByteBufferNano.computeInt64Size(2, this.timestamp);
        }
        if (this.rankerAction != null) {
            size += CodedOutputByteBufferNano.computeMessageSize(3, this.rankerAction);
        }
        if (this.recommendationRankAction != null) {
            size += CodedOutputByteBufferNano.computeMessageSize(4, this.recommendationRankAction);
        }
        if (this.appRankAction != null) {
            size += CodedOutputByteBufferNano.computeMessageSize(5, this.appRankAction);
        }
        if (this.recommendationInsertAction != null) {
            size += CodedOutputByteBufferNano.computeMessageSize(6, this.recommendationInsertAction);
        }
        if (this.header == null) {
            return size;
        }
        return size + CodedOutputByteBufferNano.computeMessageSize(7, this.header);
    }
}
