package com.bumptech.glide.gifdecoder;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Build.VERSION;
import android.util.Log;
import com.bumptech.glide.gifdecoder.GifDecoder.BitmapProvider;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class StandardGifDecoder implements GifDecoder {
    private static final String TAG = StandardGifDecoder.class.getSimpleName();
    private int[] act;
    private BitmapProvider bitmapProvider;
    private byte[] block;
    private int downsampledHeight;
    private int downsampledWidth;
    private int framePointer;
    private GifHeader header;
    private boolean isFirstFrameTransparent;
    private byte[] mainPixels;
    private int[] mainScratch;
    private byte[] pixelStack;
    private short[] prefix;
    private Bitmap previousImage;
    private ByteBuffer rawData;
    private int sampleSize;
    private boolean savePrevious;
    private int status;
    private byte[] suffix;
    private byte[] workBuffer;
    private int workBufferPosition;
    private int workBufferSize;

    public StandardGifDecoder(BitmapProvider provider, GifHeader gifHeader, ByteBuffer rawData, int sampleSize) {
        this(provider);
        setData(gifHeader, rawData, sampleSize);
    }

    public StandardGifDecoder(BitmapProvider provider) {
        this.workBufferSize = 0;
        this.workBufferPosition = 0;
        this.bitmapProvider = provider;
        this.header = new GifHeader();
    }

    public ByteBuffer getData() {
        return this.rawData;
    }

    public void advance() {
        this.framePointer = (this.framePointer + 1) % this.header.frameCount;
    }

    public int getDelay(int n) {
        if (n < 0 || n >= this.header.frameCount) {
            return -1;
        }
        return ((GifFrame) this.header.frames.get(n)).delay;
    }

    public int getNextDelay() {
        if (this.header.frameCount <= 0 || this.framePointer < 0) {
            return 0;
        }
        return getDelay(this.framePointer);
    }

    public int getFrameCount() {
        return this.header.frameCount;
    }

    public int getCurrentFrameIndex() {
        return this.framePointer;
    }

    public int getByteSize() {
        return (this.rawData.limit() + this.mainPixels.length) + (this.mainScratch.length * 4);
    }

    public synchronized Bitmap getNextFrame() {
        Bitmap bitmap = null;
        synchronized (this) {
            if (this.header.frameCount <= 0 || this.framePointer < 0) {
                if (Log.isLoggable(TAG, 3)) {
                    String str = TAG;
                    int i = this.header.frameCount;
                    Log.d(str, "unable to decode frame, frameCount=" + i + " framePointer=" + this.framePointer);
                }
                this.status = 1;
            }
            if (this.status != 1 && this.status != 2) {
                this.status = 0;
                GifFrame currentFrame = (GifFrame) this.header.frames.get(this.framePointer);
                GifFrame previousFrame = null;
                int previousIndex = this.framePointer - 1;
                if (previousIndex >= 0) {
                    previousFrame = (GifFrame) this.header.frames.get(previousIndex);
                }
                int savedBgColor = this.header.bgColor;
                if (currentFrame.lct == null) {
                    this.act = this.header.gct;
                } else {
                    this.act = currentFrame.lct;
                    if (this.header.bgIndex == currentFrame.transIndex) {
                        this.header.bgColor = 0;
                    }
                }
                int save = 0;
                if (currentFrame.transparency) {
                    save = this.act[currentFrame.transIndex];
                    this.act[currentFrame.transIndex] = 0;
                }
                if (this.act == null) {
                    if (Log.isLoggable(TAG, 3)) {
                        Log.d(TAG, "No Valid Color Table");
                    }
                    this.status = 1;
                } else {
                    bitmap = setPixels(currentFrame, previousFrame);
                    if (currentFrame.transparency) {
                        this.act[currentFrame.transIndex] = save;
                    }
                    this.header.bgColor = savedBgColor;
                }
            } else if (Log.isLoggable(TAG, 3)) {
                Log.d(TAG, "Unable to decode frame, status=" + this.status);
            }
        }
        return bitmap;
    }

    public void clear() {
        this.header = null;
        if (this.mainPixels != null) {
            this.bitmapProvider.release(this.mainPixels);
        }
        if (this.mainScratch != null) {
            this.bitmapProvider.release(this.mainScratch);
        }
        if (this.previousImage != null) {
            this.bitmapProvider.release(this.previousImage);
        }
        this.previousImage = null;
        this.rawData = null;
        this.isFirstFrameTransparent = false;
        if (this.block != null) {
            this.bitmapProvider.release(this.block);
        }
        if (this.workBuffer != null) {
            this.bitmapProvider.release(this.workBuffer);
        }
    }

    public synchronized void setData(GifHeader header, ByteBuffer buffer, int sampleSize) {
        if (sampleSize <= 0) {
            throw new IllegalArgumentException("Sample size must be >=0, not: " + sampleSize);
        }
        sampleSize = Integer.highestOneBit(sampleSize);
        this.status = 0;
        this.header = header;
        this.isFirstFrameTransparent = false;
        this.framePointer = -1;
        this.rawData = buffer.asReadOnlyBuffer();
        this.rawData.position(0);
        this.rawData.order(ByteOrder.LITTLE_ENDIAN);
        this.savePrevious = false;
        for (GifFrame frame : header.frames) {
            if (frame.dispose == 3) {
                this.savePrevious = true;
                break;
            }
        }
        this.sampleSize = sampleSize;
        this.mainPixels = this.bitmapProvider.obtainByteArray(header.width * header.height);
        this.mainScratch = this.bitmapProvider.obtainIntArray((header.width / sampleSize) * (header.height / sampleSize));
        this.downsampledWidth = header.width / sampleSize;
        this.downsampledHeight = header.height / sampleSize;
    }

    private Bitmap setPixels(GifFrame currentFrame, GifFrame previousFrame) {
        int[] dest = this.mainScratch;
        if (previousFrame == null) {
            Arrays.fill(dest, 0);
        }
        if (previousFrame != null && previousFrame.dispose > 0) {
            if (previousFrame.dispose == 2) {
                int c = 0;
                if (!currentFrame.transparency) {
                    c = this.header.bgColor;
                } else if (this.framePointer == 0) {
                    this.isFirstFrameTransparent = true;
                }
                Arrays.fill(dest, c);
            } else if (previousFrame.dispose == 3 && this.previousImage != null) {
                this.previousImage.getPixels(dest, 0, this.downsampledWidth, 0, 0, this.downsampledWidth, this.downsampledHeight);
            }
        }
        decodeBitmapData(currentFrame);
        int downsampledIH = currentFrame.ih / this.sampleSize;
        int downsampledIY = currentFrame.iy / this.sampleSize;
        int downsampledIW = currentFrame.iw / this.sampleSize;
        int downsampledIX = currentFrame.ix / this.sampleSize;
        int pass = 1;
        int inc = 8;
        int iline = 0;
        boolean isFirstFrame = this.framePointer == 0;
        for (int i = 0; i < downsampledIH; i++) {
            int line = i;
            if (currentFrame.interlace) {
                if (iline >= downsampledIH) {
                    pass++;
                    switch (pass) {
                        case 2:
                            iline = 4;
                            break;
                        case 3:
                            iline = 2;
                            inc = 4;
                            break;
                        case 4:
                            iline = 1;
                            inc = 2;
                            break;
                    }
                }
                line = iline;
                iline += inc;
            }
            line += downsampledIY;
            if (line < this.downsampledHeight) {
                int k = line * this.downsampledWidth;
                int dx = k + downsampledIX;
                int dlim = dx + downsampledIW;
                if (this.downsampledWidth + k < dlim) {
                    dlim = k + this.downsampledWidth;
                }
                int sx = (this.sampleSize * i) * currentFrame.iw;
                int maxPositionInSource = sx + ((dlim - dx) * this.sampleSize);
                while (dx < dlim) {
                    int averageColor;
                    if (this.sampleSize == 1) {
                        averageColor = this.act[this.mainPixels[sx] & 255];
                    } else {
                        averageColor = averageColorsNear(sx, maxPositionInSource, currentFrame.iw);
                    }
                    if (averageColor != 0) {
                        dest[dx] = averageColor;
                    } else if (!this.isFirstFrameTransparent && isFirstFrame) {
                        this.isFirstFrameTransparent = true;
                    }
                    sx += this.sampleSize;
                    dx++;
                }
            }
        }
        if (this.savePrevious && (currentFrame.dispose == 0 || currentFrame.dispose == 1)) {
            if (this.previousImage == null) {
                this.previousImage = getNextBitmap();
            }
            this.previousImage.setPixels(dest, 0, this.downsampledWidth, 0, 0, this.downsampledWidth, this.downsampledHeight);
        }
        Bitmap result = getNextBitmap();
        result.setPixels(dest, 0, this.downsampledWidth, 0, 0, this.downsampledWidth, this.downsampledHeight);
        return result;
    }

    private int averageColorsNear(int positionInMainPixels, int maxPositionInMainPixels, int currentFrameIw) {
        int alphaSum = 0;
        int redSum = 0;
        int greenSum = 0;
        int blueSum = 0;
        int totalAdded = 0;
        int i = positionInMainPixels;
        while (i < this.sampleSize + positionInMainPixels && i < this.mainPixels.length && i < maxPositionInMainPixels) {
            int currentColor = this.act[this.mainPixels[i] & 255];
            if (currentColor != 0) {
                alphaSum += (currentColor >> 24) & 255;
                redSum += (currentColor >> 16) & 255;
                greenSum += (currentColor >> 8) & 255;
                blueSum += currentColor & 255;
                totalAdded++;
            }
            i++;
        }
        i = positionInMainPixels + currentFrameIw;
        while (i < (positionInMainPixels + currentFrameIw) + this.sampleSize && i < this.mainPixels.length && i < maxPositionInMainPixels) {
            currentColor = this.act[this.mainPixels[i] & 255];
            if (currentColor != 0) {
                alphaSum += (currentColor >> 24) & 255;
                redSum += (currentColor >> 16) & 255;
                greenSum += (currentColor >> 8) & 255;
                blueSum += currentColor & 255;
                totalAdded++;
            }
            i++;
        }
        if (totalAdded == 0) {
            return 0;
        }
        return ((((alphaSum / totalAdded) << 24) | ((redSum / totalAdded) << 16)) | ((greenSum / totalAdded) << 8)) | (blueSum / totalAdded);
    }

    private void decodeBitmapData(GifFrame frame) {
        int npix;
        int code;
        this.workBufferSize = 0;
        this.workBufferPosition = 0;
        if (frame != null) {
            this.rawData.position(frame.bufferFrameStart);
        }
        if (frame == null) {
            npix = this.header.width * this.header.height;
        } else {
            npix = frame.iw * frame.ih;
        }
        if (this.mainPixels == null || this.mainPixels.length < npix) {
            this.mainPixels = this.bitmapProvider.obtainByteArray(npix);
        }
        if (this.prefix == null) {
            this.prefix = new short[4096];
        }
        if (this.suffix == null) {
            this.suffix = new byte[4096];
        }
        if (this.pixelStack == null) {
            this.pixelStack = new byte[4097];
        }
        int dataSize = readByte();
        int clear = 1 << dataSize;
        int endOfInformation = clear + 1;
        int available = clear + 2;
        int oldCode = -1;
        int codeSize = dataSize + 1;
        int codeMask = (1 << codeSize) - 1;
        for (code = 0; code < clear; code++) {
            this.prefix[code] = (short) 0;
            this.suffix[code] = (byte) code;
        }
        int bi = 0;
        int pi = 0;
        int top = 0;
        int first = 0;
        int count = 0;
        int bits = 0;
        int datum = 0;
        int i = 0;
        while (i < npix) {
            if (count == 0) {
                count = readBlock();
                if (count <= 0) {
                    this.status = 3;
                    break;
                }
                bi = 0;
            }
            datum += (this.block[bi] & 255) << bits;
            bits += 8;
            bi++;
            count--;
            int top2 = top;
            while (bits >= codeSize) {
                code = datum & codeMask;
                datum >>= codeSize;
                bits -= codeSize;
                if (code == clear) {
                    codeSize = dataSize + 1;
                    codeMask = (1 << codeSize) - 1;
                    available = clear + 2;
                    oldCode = -1;
                } else if (code > available) {
                    this.status = 3;
                    top = top2;
                    break;
                } else if (code == endOfInformation) {
                    top = top2;
                    break;
                } else if (oldCode == -1) {
                    top = top2 + 1;
                    this.pixelStack[top2] = this.suffix[code];
                    oldCode = code;
                    first = code;
                    top2 = top;
                } else {
                    int inCode = code;
                    if (code >= available) {
                        top = top2 + 1;
                        this.pixelStack[top2] = (byte) first;
                        code = oldCode;
                        top2 = top;
                    }
                    while (code >= clear) {
                        top = top2 + 1;
                        this.pixelStack[top2] = this.suffix[code];
                        code = this.prefix[code];
                        top2 = top;
                    }
                    first = this.suffix[code] & 255;
                    top = top2 + 1;
                    this.pixelStack[top2] = (byte) first;
                    if (available < 4096) {
                        this.prefix[available] = (short) oldCode;
                        this.suffix[available] = (byte) first;
                        available++;
                        if ((available & codeMask) == 0 && available < 4096) {
                            codeSize++;
                            codeMask += available;
                        }
                    }
                    oldCode = inCode;
                    int pi2 = pi;
                    while (top > 0) {
                        pi = pi2 + 1;
                        top--;
                        this.mainPixels[pi2] = this.pixelStack[top];
                        i++;
                        pi2 = pi;
                    }
                    pi = pi2;
                    top2 = top;
                }
            }
            top = top2;
        }
        for (i = pi; i < npix; i++) {
            this.mainPixels[i] = (byte) 0;
        }
    }

    private void readChunkIfNeeded() {
        if (this.workBufferSize <= this.workBufferPosition) {
            if (this.workBuffer == null) {
                this.workBuffer = this.bitmapProvider.obtainByteArray(16384);
            }
            this.workBufferPosition = 0;
            this.workBufferSize = Math.min(this.rawData.remaining(), 16384);
            this.rawData.get(this.workBuffer, 0, this.workBufferSize);
        }
    }

    private int readByte() {
        try {
            readChunkIfNeeded();
            byte[] bArr = this.workBuffer;
            int i = this.workBufferPosition;
            this.workBufferPosition = i + 1;
            return bArr[i] & 255;
        } catch (Exception e) {
            this.status = 1;
            return 0;
        }
    }

    private int readBlock() {
        int blockSize = readByte();
        if (blockSize > 0) {
            try {
                if (this.block == null) {
                    this.block = this.bitmapProvider.obtainByteArray(255);
                }
                int remaining = this.workBufferSize - this.workBufferPosition;
                if (remaining >= blockSize) {
                    System.arraycopy(this.workBuffer, this.workBufferPosition, this.block, 0, blockSize);
                    this.workBufferPosition += blockSize;
                } else if (this.rawData.remaining() + remaining >= blockSize) {
                    System.arraycopy(this.workBuffer, this.workBufferPosition, this.block, 0, remaining);
                    this.workBufferPosition = this.workBufferSize;
                    readChunkIfNeeded();
                    int secondHalfRemaining = blockSize - remaining;
                    System.arraycopy(this.workBuffer, 0, this.block, remaining, secondHalfRemaining);
                    this.workBufferPosition += secondHalfRemaining;
                } else {
                    this.status = 1;
                }
            } catch (Exception e) {
                Log.w(TAG, "Error Reading Block", e);
                this.status = 1;
            }
        }
        return blockSize;
    }

    private Bitmap getNextBitmap() {
        Bitmap result = this.bitmapProvider.obtain(this.downsampledWidth, this.downsampledHeight, this.isFirstFrameTransparent ? Config.ARGB_8888 : Config.RGB_565);
        setAlpha(result);
        return result;
    }

    @TargetApi(12)
    private static void setAlpha(Bitmap bitmap) {
        if (VERSION.SDK_INT >= 12) {
            bitmap.setHasAlpha(true);
        }
    }
}
