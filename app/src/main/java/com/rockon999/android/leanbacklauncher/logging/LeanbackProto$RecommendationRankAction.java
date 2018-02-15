package com.rockon999.android.leanbacklauncher.logging;

import com.rockon999.android.leanbacklauncher.protobuf.nano.CodedOutputByteBufferNano;
import com.rockon999.android.leanbacklauncher.protobuf.nano.ExtendableMessageNano;

import java.io.IOException;

public final class LeanbackProto$RecommendationRankAction extends ExtendableMessageNano<LeanbackProto$RecommendationRankAction> {
    public LeanbackProto$Recommendation[] recommendations;

    public LeanbackProto$RecommendationRankAction() {
        clear();
    }

    public LeanbackProto$RecommendationRankAction clear() {
        this.recommendations = LeanbackProto$Recommendation.emptyArray();
        this.unknownFieldData = null;
        this.cachedSize = -1;
        return this;
    }

    public void writeTo(CodedOutputByteBufferNano output) throws IOException {
        if (this.recommendations != null && this.recommendations.length > 0) {
            for (LeanbackProto$Recommendation element : this.recommendations) {
                if (element != null) {
                    output.writeMessage(1, element);
                }
            }
        }
        super.writeTo(output);
    }

    protected int computeSerializedSize() {
        int size = super.computeSerializedSize();
        if (this.recommendations != null && this.recommendations.length > 0) {
            for (LeanbackProto$Recommendation element : this.recommendations) {
                if (element != null) {
                    size += CodedOutputByteBufferNano.computeMessageSize(1, element);
                }
            }
        }
        return size;
    }
}
