package com.bumptech.glide.load.resource.bitmap;

import android.util.Log;
import com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool;
import com.bumptech.glide.util.Preconditions;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;

public class ImageHeaderParser {
    private static final int[] BYTES_PER_FORMAT = new int[]{0, 1, 1, 2, 4, 8, 1, 1, 2, 4, 8, 4, 8};
    private static final byte[] JPEG_EXIF_SEGMENT_PREAMBLE_BYTES = "Exif\u0000\u0000".getBytes(Charset.forName("UTF-8"));
    private final ArrayPool byteArrayPool;
    private final Reader reader;

    private interface Reader {
        int getByte() throws IOException;

        int getUInt16() throws IOException;

        short getUInt8() throws IOException;

        int read(byte[] bArr, int i) throws IOException;

        long skip(long j) throws IOException;
    }

    private static class ByteBufferReader implements Reader {
        private final ByteBuffer byteBuffer;

        public ByteBufferReader(ByteBuffer byteBuffer) {
            this.byteBuffer = byteBuffer;
            byteBuffer.order(ByteOrder.BIG_ENDIAN);
        }

        public int getUInt16() throws IOException {
            return ((getByte() << 8) & 65280) | (getByte() & 255);
        }

        public short getUInt8() throws IOException {
            return (short) (getByte() & 255);
        }

        public long skip(long total) throws IOException {
            int toSkip = (int) Math.min((long) this.byteBuffer.remaining(), total);
            this.byteBuffer.position(this.byteBuffer.position() + toSkip);
            return (long) toSkip;
        }

        public int read(byte[] buffer, int byteCount) throws IOException {
            int toRead = Math.min(byteCount, this.byteBuffer.remaining());
            this.byteBuffer.get(buffer, 0, byteCount);
            return toRead;
        }

        public int getByte() throws IOException {
            if (this.byteBuffer.remaining() < 1) {
                return -1;
            }
            return this.byteBuffer.get();
        }
    }

    public enum ImageType {
        GIF(true),
        JPEG(false),
        PNG_A(true),
        PNG(false),
        WEBP_A(true),
        WEBP(false),
        UNKNOWN(false);
        
        private final boolean hasAlpha;

        private ImageType(boolean hasAlpha) {
            this.hasAlpha = hasAlpha;
        }

        public boolean hasAlpha() {
            return this.hasAlpha;
        }
    }

    private static class RandomAccessReader {
        private final ByteBuffer data;

        public RandomAccessReader(byte[] data, int length) {
            this.data = (ByteBuffer) ByteBuffer.wrap(data).order(ByteOrder.BIG_ENDIAN).limit(length);
        }

        public void order(ByteOrder byteOrder) {
            this.data.order(byteOrder);
        }

        public int length() {
            return this.data.remaining();
        }

        public int getInt32(int offset) {
            return this.data.getInt(offset);
        }

        public short getInt16(int offset) {
            return this.data.getShort(offset);
        }
    }

    private static class StreamReader implements Reader {
        private final InputStream is;

        public StreamReader(InputStream is) {
            this.is = is;
        }

        public int getUInt16() throws IOException {
            return ((this.is.read() << 8) & 65280) | (this.is.read() & 255);
        }

        public short getUInt8() throws IOException {
            return (short) (this.is.read() & 255);
        }

        public long skip(long total) throws IOException {
            if (total < 0) {
                return 0;
            }
            long toSkip = total;
            while (toSkip > 0) {
                long skipped = this.is.skip(toSkip);
                if (skipped > 0) {
                    toSkip -= skipped;
                } else if (this.is.read() == -1) {
                    break;
                } else {
                    toSkip--;
                }
            }
            return total - toSkip;
        }

        public int read(byte[] buffer, int byteCount) throws IOException {
            int toRead = byteCount;
            while (toRead > 0) {
                int read = this.is.read(buffer, byteCount - toRead, toRead);
                if (read == -1) {
                    break;
                }
                toRead -= read;
            }
            return byteCount - toRead;
        }

        public int getByte() throws IOException {
            return this.is.read();
        }
    }

    public ImageHeaderParser(InputStream is, ArrayPool byteArrayPool) {
        Preconditions.checkNotNull(is);
        this.byteArrayPool = (ArrayPool) Preconditions.checkNotNull(byteArrayPool);
        this.reader = new StreamReader(is);
    }

    public ImageHeaderParser(ByteBuffer byteBuffer, ArrayPool byteArrayPool) {
        Preconditions.checkNotNull(byteBuffer);
        this.byteArrayPool = (ArrayPool) Preconditions.checkNotNull(byteArrayPool);
        this.reader = new ByteBufferReader(byteBuffer);
    }

    public boolean hasAlpha() throws IOException {
        return getType().hasAlpha();
    }

    public ImageType getType() throws IOException {
        int firstTwoBytes = this.reader.getUInt16();
        if (firstTwoBytes == 65496) {
            return ImageType.JPEG;
        }
        int firstFourBytes = ((firstTwoBytes << 16) & -65536) | (this.reader.getUInt16() & 65535);
        if (firstFourBytes == -1991225785) {
            this.reader.skip(21);
            return this.reader.getByte() >= 3 ? ImageType.PNG_A : ImageType.PNG;
        } else if ((firstFourBytes >> 8) == 4671814) {
            return ImageType.GIF;
        } else {
            if (firstFourBytes != 1380533830) {
                return ImageType.UNKNOWN;
            }
            this.reader.skip(4);
            if ((((this.reader.getUInt16() << 16) & -65536) | (this.reader.getUInt16() & 65535)) != 1464156752) {
                return ImageType.UNKNOWN;
            }
            int fourthFourBytes = ((this.reader.getUInt16() << 16) & -65536) | (this.reader.getUInt16() & 65535);
            if ((fourthFourBytes & -256) != 1448097792) {
                return ImageType.UNKNOWN;
            }
            if ((fourthFourBytes & 255) == 88) {
                this.reader.skip(4);
                return (this.reader.getByte() & 16) != 0 ? ImageType.WEBP_A : ImageType.WEBP;
            } else if ((fourthFourBytes & 255) != 76) {
                return ImageType.WEBP;
            } else {
                this.reader.skip(4);
                return (this.reader.getByte() & 8) != 0 ? ImageType.WEBP_A : ImageType.WEBP;
            }
        }
    }

    public int getOrientation() throws IOException {
        int i = -1;
        int magicNumber = this.reader.getUInt16();
        if (handles(magicNumber)) {
            int exifSegmentLength = moveToExifSegmentAndGetLength();
            if (exifSegmentLength != -1) {
                byte[] exifData = (byte[]) this.byteArrayPool.get(exifSegmentLength, byte[].class);
                try {
                    i = parseExifSegment(exifData, exifSegmentLength);
                } finally {
                    this.byteArrayPool.put(exifData, byte[].class);
                }
            } else if (Log.isLoggable("ImageHeaderParser", 3)) {
                Log.d("ImageHeaderParser", "Failed to parse exif segment length, or exif segment not found");
            }
        } else if (Log.isLoggable("ImageHeaderParser", 3)) {
            Log.d("ImageHeaderParser", "Parser doesn't handle magic number: " + magicNumber);
        }
        return i;
    }

    private int parseExifSegment(byte[] tempArray, int exifSegmentLength) throws IOException {
        int read = this.reader.read(tempArray, exifSegmentLength);
        if (read != exifSegmentLength) {
            if (!Log.isLoggable("ImageHeaderParser", 3)) {
                return -1;
            }
            Log.d("ImageHeaderParser", "Unable to read exif segment data, length: " + exifSegmentLength + ", actually read: " + read);
            return -1;
        } else if (hasJpegExifPreamble(tempArray, exifSegmentLength)) {
            return parseExifSegment(new RandomAccessReader(tempArray, exifSegmentLength));
        } else {
            if (!Log.isLoggable("ImageHeaderParser", 3)) {
                return -1;
            }
            Log.d("ImageHeaderParser", "Missing jpeg exif preamble");
            return -1;
        }
    }

    private boolean hasJpegExifPreamble(byte[] exifData, int exifSegmentLength) {
        boolean result = exifData != null && exifSegmentLength > JPEG_EXIF_SEGMENT_PREAMBLE_BYTES.length;
        if (!result) {
            return result;
        }
        for (int i = 0; i < JPEG_EXIF_SEGMENT_PREAMBLE_BYTES.length; i++) {
            if (exifData[i] != JPEG_EXIF_SEGMENT_PREAMBLE_BYTES[i]) {
                return false;
            }
        }
        return result;
    }

    private int moveToExifSegmentAndGetLength() throws IOException {
        long skipped;
        int segmentLength;
        do {
            short segmentId = this.reader.getUInt8();
            if (segmentId != (short) 255) {
                if (Log.isLoggable("ImageHeaderParser", 3)) {
                    Log.d("ImageHeaderParser", "Unknown segmentId=" + segmentId);
                }
                return -1;
            }
            short segmentType = this.reader.getUInt8();
            if (segmentType == (short) 218) {
                return -1;
            }
            if (segmentType == (short) 217) {
                if (Log.isLoggable("ImageHeaderParser", 3)) {
                    Log.d("ImageHeaderParser", "Found MARKER_EOI in exif segment");
                }
                return -1;
            }
            segmentLength = this.reader.getUInt16() - 2;
            if (segmentType == (short) 225) {
                return segmentLength;
            }
            skipped = this.reader.skip((long) segmentLength);
        } while (skipped == ((long) segmentLength));
        if (Log.isLoggable("ImageHeaderParser", 3)) {
            Log.d("ImageHeaderParser", "Unable to skip enough data, type: " + segmentType + ", wanted to skip: " + segmentLength + ", but actually skipped: " + skipped);
        }
        return -1;
    }

    private static int parseExifSegment(RandomAccessReader segmentData) {
        ByteOrder byteOrder;
        int headerOffsetSize = "Exif\u0000\u0000".length();
        short byteOrderIdentifier = segmentData.getInt16(headerOffsetSize);
        if (byteOrderIdentifier == (short) 19789) {
            byteOrder = ByteOrder.BIG_ENDIAN;
        } else if (byteOrderIdentifier == (short) 18761) {
            byteOrder = ByteOrder.LITTLE_ENDIAN;
        } else {
            if (Log.isLoggable("ImageHeaderParser", 3)) {
                Log.d("ImageHeaderParser", "Unknown endianness = " + byteOrderIdentifier);
            }
            byteOrder = ByteOrder.BIG_ENDIAN;
        }
        segmentData.order(byteOrder);
        int firstIfdOffset = segmentData.getInt32(headerOffsetSize + 4) + headerOffsetSize;
        int tagCount = segmentData.getInt16(firstIfdOffset);
        for (int i = 0; i < tagCount; i++) {
            int tagOffset = calcTagOffset(firstIfdOffset, i);
            int tagType = segmentData.getInt16(tagOffset);
            if (tagType == 274) {
                int formatCode = segmentData.getInt16(tagOffset + 2);
                if (formatCode >= 1 && formatCode <= 12) {
                    int componentCount = segmentData.getInt32(tagOffset + 4);
                    if (componentCount >= 0) {
                        if (Log.isLoggable("ImageHeaderParser", 3)) {
                            Log.d("ImageHeaderParser", "Got tagIndex=" + i + " tagType=" + tagType + " formatCode=" + formatCode + " componentCount=" + componentCount);
                        }
                        int byteCount = componentCount + BYTES_PER_FORMAT[formatCode];
                        if (byteCount <= 4) {
                            int tagValueOffset = tagOffset + 8;
                            if (tagValueOffset < 0 || tagValueOffset > segmentData.length()) {
                                if (Log.isLoggable("ImageHeaderParser", 3)) {
                                    Log.d("ImageHeaderParser", "Illegal tagValueOffset=" + tagValueOffset + " tagType=" + tagType);
                                }
                            } else if (byteCount >= 0 && tagValueOffset + byteCount <= segmentData.length()) {
                                return segmentData.getInt16(tagValueOffset);
                            } else {
                                if (Log.isLoggable("ImageHeaderParser", 3)) {
                                    Log.d("ImageHeaderParser", "Illegal number of bytes for TI tag data tagType=" + tagType);
                                }
                            }
                        } else if (Log.isLoggable("ImageHeaderParser", 3)) {
                            Log.d("ImageHeaderParser", "Got byte count > 4, not orientation, continuing, formatCode=" + formatCode);
                        }
                    } else if (Log.isLoggable("ImageHeaderParser", 3)) {
                        Log.d("ImageHeaderParser", "Negative tiff component count");
                    }
                } else if (Log.isLoggable("ImageHeaderParser", 3)) {
                    Log.d("ImageHeaderParser", "Got invalid format code = " + formatCode);
                }
            }
        }
        return -1;
    }

    private static int calcTagOffset(int ifdOffset, int tagIndex) {
        return (ifdOffset + 2) + (tagIndex * 12);
    }

    private static boolean handles(int imageMagicNumber) {
        return (imageMagicNumber & 65496) == 65496 || imageMagicNumber == 19789 || imageMagicNumber == 18761;
    }
}
