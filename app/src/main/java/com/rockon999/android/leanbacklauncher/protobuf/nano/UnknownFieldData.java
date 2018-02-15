package com.rockon999.android.leanbacklauncher.protobuf.nano;

import java.io.IOException;
import java.util.Arrays;

final class UnknownFieldData {
    byte[] bytes;
    int tag;

    int computeSerializedSize() {
        return (CodedOutputByteBufferNano.computeRawVarint32Size(this.tag)) + this.bytes.length;
    }

    void writeTo(CodedOutputByteBufferNano output) throws IOException {
        output.writeRawVarint32(this.tag);
        output.writeRawBytes(this.bytes);
    }

    public boolean equals(Object o) {
        boolean z = true;
        if (o == this) {
            return true;
        }
        if (!(o instanceof UnknownFieldData)) {
            return false;
        }
        UnknownFieldData other = (UnknownFieldData) o;
        if (this.tag == other.tag) {
            if (!Arrays.equals(this.bytes, other.bytes)) {
            }
            return z;
        }
        z = false;
        return z;
    }

    public int hashCode() {
        return ((this.tag + 527) * 31) + Arrays.hashCode(this.bytes);
    }
}
