package com.rockon999.android.leanbacklauncher.logging;

import com.rockon999.android.leanbacklauncher.protobuf.nano.CodedOutputByteBufferNano;
import com.rockon999.android.leanbacklauncher.protobuf.nano.ExtendableMessageNano;

import java.io.IOException;

public final class LeanbackProto$LeanbackHeader extends ExtendableMessageNano<LeanbackProto$LeanbackHeader> {
    public String launcherVersion;

    public LeanbackProto$LeanbackHeader() {
        clear();
    }

    public LeanbackProto$LeanbackHeader clear() {
        this.launcherVersion = null;
        this.unknownFieldData = null;
        this.cachedSize = -1;
        return this;
    }

    public void writeTo(CodedOutputByteBufferNano output) throws IOException {
        if (this.launcherVersion != null) {
            output.writeString(1, this.launcherVersion);
        }
        super.writeTo(output);
    }

    protected int computeSerializedSize() {
        int size = super.computeSerializedSize();
        if (this.launcherVersion == null) {
            return size;
        }
        return size + CodedOutputByteBufferNano.computeStringSize(1, this.launcherVersion);
    }
}
