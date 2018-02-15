package com.rockon999.android.leanbacklauncher.protobuf.nano;

import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.ReadOnlyBufferException;

public final class CodedOutputByteBufferNano {
    private final ByteBuffer buffer;

    public static class OutOfSpaceException extends IOException {
        private static final long serialVersionUID = -6947486886997889499L;
        private static final String valueOf = String.valueOf("CodedOutputStream was writing to a flat byte array and ran out of space (pos ");
        OutOfSpaceException(int position, int limit) {
            super(valueOf + position + " limit " + limit + ").");
        }
    }

    private CodedOutputByteBufferNano(byte[] buffer, int offset, int length) {
        this(ByteBuffer.wrap(buffer, offset, length));
    }

    private CodedOutputByteBufferNano(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    public static CodedOutputByteBufferNano newInstance(byte[] flatArray) {
        return newInstance(flatArray, 0, flatArray.length);
    }

    public static CodedOutputByteBufferNano newInstance(byte[] flatArray, int offset, int length) {
        return new CodedOutputByteBufferNano(flatArray, offset, length);
    }

    public void writeFloat(int fieldNumber, float value) throws IOException {
        writeTag(fieldNumber, 5);
        writeFloatNoTag(value);
    }

    public void writeInt64(int fieldNumber, long value) throws IOException {
        writeTag(fieldNumber, 0);
        writeInt64NoTag(value);
    }

    public void writeInt32(int fieldNumber, int value) throws IOException {
        writeTag(fieldNumber, 0);
        writeInt32NoTag(value);
    }

    public void writeString(int fieldNumber, String value) throws IOException {
        writeTag(fieldNumber, 2);
        writeStringNoTag(value);
    }

    public void writeMessage(int fieldNumber, MessageNano value) throws IOException {
        writeTag(fieldNumber, 2);
        writeMessageNoTag(value);
    }

    public void writeUInt32(int fieldNumber, int value) throws IOException {
        writeTag(fieldNumber, 0);
        writeUInt32NoTag(value);
    }

    public void writeFloatNoTag(float value) throws IOException {
        writeRawLittleEndian32(Float.floatToIntBits(value));
    }

    public void writeInt64NoTag(long value) throws IOException {
        writeRawVarint64(value);
    }

    public void writeInt32NoTag(int value) throws IOException {
        if (value < 0) {
            writeRawVarint64((long) value);
        } else {
            writeRawVarint32(value);
        }
    }

    public void writeStringNoTag(String value) throws IOException {
        try {
            int minLengthVarIntSize = computeRawVarint32Size(value.length());
            if (minLengthVarIntSize != computeRawVarint32Size(value.length() * 3)) {
                writeRawVarint32(encodedLength(value));
                encode(value, this.buffer);
                return;
            }
            int oldPosition = this.buffer.position();
            this.buffer.position(oldPosition + minLengthVarIntSize);
            encode(value, this.buffer);
            int newPosition = this.buffer.position();
            this.buffer.position(oldPosition);
            writeRawVarint32((newPosition - oldPosition) - minLengthVarIntSize);
            this.buffer.position(newPosition);
        } catch (BufferOverflowException e) {
            throw new OutOfSpaceException(this.buffer.position(), this.buffer.limit());
        }
    }

    private static int encodedLength(CharSequence sequence) {
        int utf16Length = sequence.length();
        int utf8Length = utf16Length;
        int i = 0;
        while (i < utf16Length && sequence.charAt(i) < '\u0080') {
            i++;
        }
        while (i < utf16Length) {
            char c = sequence.charAt(i);
            if (c >= '\u0800') {
                utf8Length += encodedLengthGeneral(sequence, i);
                break;
            }
            utf8Length += (127 - c) >>> 31;
            i++;
        }
        if (utf8Length >= utf16Length) {
            return utf8Length;
        }
        throw new IllegalArgumentException("UTF-8 length does not fit in int: " + (((long) utf8Length) + 4294967296L));
    }

    private static int encodedLengthGeneral(CharSequence sequence, int start) {
        int utf16Length = sequence.length();
        int utf8Length = 0;
        int i = start;
        while (i < utf16Length) {
            char c = sequence.charAt(i);
            if (c >= '\u0800') {
                utf8Length += 2;
                if ('\ud800' <= c && c <= '\udfff') {
                    if (Character.codePointAt(sequence, i) >= 65536) {
                        i++;
                    } else {
                        throw new IllegalArgumentException("Unpaired surrogate at index " + i);
                    }
                }
            }
            utf8Length += (127 - c) >>> 31;
            i++;
        }
        return utf8Length;
    }

    private static void encode(CharSequence sequence, ByteBuffer byteBuffer) {
        if (byteBuffer.isReadOnly()) {
            throw new ReadOnlyBufferException();
        } else if (byteBuffer.hasArray()) {
            try {
                byteBuffer.position(encode(sequence, byteBuffer.array(), byteBuffer.arrayOffset() + byteBuffer.position(), byteBuffer.remaining()) - byteBuffer.arrayOffset());
            } catch (ArrayIndexOutOfBoundsException e) {
                BufferOverflowException boe = new BufferOverflowException();
                boe.initCause(e);
                throw boe;
            }
        } else {
            encodeDirect(sequence, byteBuffer);
        }
    }

    private static void encodeDirect(CharSequence sequence, ByteBuffer byteBuffer) {
        int utf16Length = sequence.length();
        int i = 0;
        while (i < utf16Length) {
            char c = sequence.charAt(i);
            if (c < '\u0080') {
                byteBuffer.put((byte) c);
            } else if (c < '\u0800') {
                byteBuffer.put((byte) ((c >>> 6) | 960));
                byteBuffer.put((byte) ((c & 63) | 128));
            } else if (c >= '\ud800' && '\udfff' >= c) {
                if (i + 1 != sequence.length()) {
                    i++;
                    char low = sequence.charAt(i);
                    if (Character.isSurrogatePair(c, low)) {
                        int codePoint = Character.toCodePoint(c, low);
                        byteBuffer.put((byte) ((codePoint >>> 18) | 240));
                        byteBuffer.put((byte) (((codePoint >>> 12) & 63) | 128));
                        byteBuffer.put((byte) (((codePoint >>> 6) & 63) | 128));
                        byteBuffer.put((byte) ((codePoint & 63) | 128));
                    }
                }
                throw new IllegalArgumentException("Unpaired surrogate at index " + (i - 1));
            } else {
                byteBuffer.put((byte) ((c >>> 12) | 480));
                byteBuffer.put((byte) (((c >>> 6) & 63) | 128));
                byteBuffer.put((byte) ((c & 63) | 128));
            }
            i++;
        }
    }

    private static int encode(CharSequence sequence, byte[] bytes, int offset, int length) {
        int utf16Length = sequence.length();
        int j = offset;
        int i = 0;
        int limit = offset + length;
        while (i < utf16Length && i + offset < limit) {
            char c = sequence.charAt(i);
            if (c >= '\u0080') {
                break;
            }
            bytes[offset + i] = (byte) c;
            i++;
        }
        if (i == utf16Length) {
            return offset + utf16Length;
        }
        int j2 = offset + i;
        while (i < utf16Length) {
            char c = sequence.charAt(i);
            if (c < '\u0080' && j2 < limit) {
                j = j2 + 1;
                bytes[j2] = (byte) c;
            } else if (c < '\u0800' && j2 <= limit - 2) {
                j = j2 + 1;
                bytes[j2] = (byte) ((c >>> 6) | 960);
                j2 = j + 1;
                bytes[j] = (byte) ((c & 63) | 128);
                j = j2;
            } else {
                if (c < '\ud800' || '\udfff' < c) {
                    if (j2 <= limit - 3) {
                        j = j2 + 1;
                        bytes[j2] = (byte) ((c >>> 12) | 480);
                        j2 = j + 1;
                        bytes[j] = (byte) (((c >>> 6) & 63) | 128);
                        j = j2 + 1;
                        bytes[j2] = (byte) ((c & 63) | 128);
                    }
                }
                if (j2 > limit - 4) {
                    throw new ArrayIndexOutOfBoundsException("Failed writing " + c + " at index " + j2);
                }
                if (i + 1 != sequence.length()) {
                    i++;
                    char low = sequence.charAt(i);
                    if (Character.isSurrogatePair(c, low)) {
                        int codePoint = Character.toCodePoint(c, low);
                        j = j2 + 1;
                        bytes[j2] = (byte) ((codePoint >>> 18) | 240);
                        j2 = j + 1;
                        bytes[j] = (byte) (((codePoint >>> 12) & 63) | 128);
                        j = j2 + 1;
                        bytes[j2] = (byte) (((codePoint >>> 6) & 63) | 128);
                        j2 = j + 1;
                        bytes[j] = (byte) ((codePoint & 63) | 128);
                        j = j2;
                    }
                }
                throw new IllegalArgumentException("Unpaired surrogate at index " + (i - 1));
            }
            i++;
            j2 = j;
        }
        return j2;
    }

    public void writeGroupNoTag(MessageNano value) throws IOException {
        value.writeTo(this);
    }

    public void writeMessageNoTag(MessageNano value) throws IOException {
        writeRawVarint32(value.getCachedSize());
        value.writeTo(this);
    }

    public void writeUInt32NoTag(int value) throws IOException {
        writeRawVarint32(value);
    }

    public static int computeFloatSize(int fieldNumber, float value) {
        return computeTagSize(fieldNumber) + computeFloatSizeNoTag(value);
    }

    public static int computeInt64Size(int fieldNumber, long value) {
        return computeTagSize(fieldNumber) + computeInt64SizeNoTag(value);
    }

    public static int computeInt32Size(int fieldNumber, int value) {
        return computeTagSize(fieldNumber) + computeInt32SizeNoTag(value);
    }

    public static int computeStringSize(int fieldNumber, String value) {
        return computeTagSize(fieldNumber) + computeStringSizeNoTag(value);
    }

    public static int computeGroupSize(int fieldNumber, MessageNano value) {
        return (computeTagSize(fieldNumber) * 2) + computeGroupSizeNoTag(value);
    }

    public static int computeMessageSize(int fieldNumber, MessageNano value) {
        return computeTagSize(fieldNumber) + computeMessageSizeNoTag(value);
    }

    public static int computeUInt32Size(int fieldNumber, int value) {
        return computeTagSize(fieldNumber) + computeUInt32SizeNoTag(value);
    }

    public static int computeFloatSizeNoTag(float value) {
        return 4;
    }

    public static int computeInt64SizeNoTag(long value) {
        return computeRawVarint64Size(value);
    }

    public static int computeInt32SizeNoTag(int value) {
        if (value < 0) {
            return 10;
        }
        return computeRawVarint32Size(value);
    }

    public static int computeStringSizeNoTag(String value) {
        int length = encodedLength(value);
        return computeRawVarint32Size(length) + length;
    }

    public static int computeGroupSizeNoTag(MessageNano value) {
        return value.getSerializedSize();
    }

    public static int computeMessageSizeNoTag(MessageNano value) {
        int size = value.getSerializedSize();
        return computeRawVarint32Size(size) + size;
    }

    public static int computeUInt32SizeNoTag(int value) {
        return computeRawVarint32Size(value);
    }

    public int spaceLeft() {
        return this.buffer.remaining();
    }

    public void checkNoSpaceLeft() {
        if (spaceLeft() != 0) {
            throw new IllegalStateException("Did not write as much data as expected.");
        }
    }

    public void writeRawByte(byte value) throws IOException {
        if (this.buffer.hasRemaining()) {
            this.buffer.put(value);
            return;
        }
        throw new OutOfSpaceException(this.buffer.position(), this.buffer.limit());
    }

    public void writeRawByte(int value) throws IOException {
        writeRawByte((byte) value);
    }

    public void writeRawBytes(byte[] value) throws IOException {
        writeRawBytes(value, 0, value.length);
    }

    public void writeRawBytes(byte[] value, int offset, int length) throws IOException {
        if (this.buffer.remaining() < length) {
            throw new OutOfSpaceException(this.buffer.position(), this.buffer.limit());
        }
        this.buffer.put(value, offset, length);
    }

    public void writeTag(int fieldNumber, int wireType) throws IOException {
        writeRawVarint32(WireFormatNano.makeTag(fieldNumber, wireType));
    }

    public static int computeTagSize(int fieldNumber) {
        return computeRawVarint32Size(WireFormatNano.makeTag(fieldNumber, 0));
    }

    public void writeRawVarint32(int value) throws IOException {
        while ((value & -128) != 0) {
            writeRawByte((value & 127) | 128);
            value >>>= 7;
        }
        writeRawByte(value);
    }

    public static int computeRawVarint32Size(int value) {
        if ((value & -128) == 0) {
            return 1;
        }
        if ((value & -16384) == 0) {
            return 2;
        }
        if ((-2097152 & value) == 0) {
            return 3;
        }
        if ((-268435456 & value) != 0) {
            return 5;
        }
        return 4;
    }

    public void writeRawVarint64(long value) throws IOException {
        while ((-128 & value) != 0) {
            writeRawByte((((int) value) & 127) | 128);
            value >>>= 7;
        }
        writeRawByte((int) value);
    }

    public static int computeRawVarint64Size(long value) {
        if ((-128 & value) == 0) {
            return 1;
        }
        if ((-16384 & value) == 0) {
            return 2;
        }
        if ((-2097152 & value) == 0) {
            return 3;
        }
        if ((-268435456 & value) == 0) {
            return 4;
        }
        if ((-34359738368L & value) == 0) {
            return 5;
        }
        if ((-4398046511104L & value) == 0) {
            return 6;
        }
        if ((-562949953421312L & value) == 0) {
            return 7;
        }
        if ((-72057594037927936L & value) == 0) {
            return 8;
        }
        return (Long.MIN_VALUE & value) == 0 ? 9 : 10;
    }

    public void writeRawLittleEndian32(int value) throws IOException {
        writeRawByte(value & 255);
        writeRawByte((value >> 8) & 255);
        writeRawByte((value >> 16) & 255);
        writeRawByte((value >> 24) & 255);
    }
}
