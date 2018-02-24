package com.bumptech.glide.gifdecoder;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Build.VERSION;
import android.support.annotation.Nullable;
import android.util.Log;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class StandardGifDecoder
  implements GifDecoder
{
  private static final int BYTES_PER_INTEGER = 4;
  private static final int DISPOSAL_BACKGROUND = 2;
  private static final int DISPOSAL_NONE = 1;
  private static final int DISPOSAL_PREVIOUS = 3;
  private static final int DISPOSAL_UNSPECIFIED = 0;
  private static final int INITIAL_FRAME_POINTER = -1;
  private static final int MAX_STACK_SIZE = 4096;
  private static final int NULL_CODE = -1;
  private static final String TAG = StandardGifDecoder.class.getSimpleName();
  private static final int WORK_BUFFER_SIZE = 16384;
  private int[] act;
  private GifDecoder.BitmapProvider bitmapProvider;
  private byte[] block;
  private int downsampledHeight;
  private int downsampledWidth;
  private int framePointer;
  private GifHeader header;
  private boolean isFirstFrameTransparent;
  private byte[] mainPixels;
  private int[] mainScratch;
  private GifHeaderParser parser;
  private byte[] pixelStack;
  private short[] prefix;
  private Bitmap previousImage;
  private ByteBuffer rawData;
  private int sampleSize;
  private boolean savePrevious;
  private int status;
  private byte[] suffix;
  @Nullable
  private byte[] workBuffer;
  private int workBufferPosition = 0;
  private int workBufferSize = 0;
  
  public StandardGifDecoder(GifDecoder.BitmapProvider paramBitmapProvider)
  {
    this.bitmapProvider = paramBitmapProvider;
    this.header = new GifHeader();
  }
  
  public StandardGifDecoder(GifDecoder.BitmapProvider paramBitmapProvider, GifHeader paramGifHeader, ByteBuffer paramByteBuffer)
  {
    this(paramBitmapProvider, paramGifHeader, paramByteBuffer, 1);
  }
  
  public StandardGifDecoder(GifDecoder.BitmapProvider paramBitmapProvider, GifHeader paramGifHeader, ByteBuffer paramByteBuffer, int paramInt)
  {
    this(paramBitmapProvider);
    setData(paramGifHeader, paramByteBuffer, paramInt);
  }
  
  private int averageColorsNear(int paramInt1, int paramInt2, int paramInt3)
  {
    int m = 0;
    int n = 0;
    int k = 0;
    int i = 0;
    int j = 0;
    int i1 = paramInt1;
    int i7;
    int i6;
    int i5;
    int i3;
    while ((i1 < this.sampleSize + paramInt1) && (i1 < this.mainPixels.length) && (i1 < paramInt2))
    {
      i2 = this.mainPixels[i1];
      i7 = this.act[(i2 & 0xFF)];
      i6 = m;
      i5 = i;
      i4 = k;
      i3 = n;
      i2 = j;
      if (i7 != 0)
      {
        i6 = m + (i7 >> 24 & 0xFF);
        i3 = n + (i7 >> 16 & 0xFF);
        i4 = k + (i7 >> 8 & 0xFF);
        i5 = i + (i7 & 0xFF);
        i2 = j + 1;
      }
      i1 += 1;
      m = i6;
      i = i5;
      k = i4;
      n = i3;
      j = i2;
    }
    int i2 = paramInt1 + paramInt3;
    i1 = n;
    n = i2;
    int i4 = m;
    while ((n < paramInt1 + paramInt3 + this.sampleSize) && (n < this.mainPixels.length) && (n < paramInt2))
    {
      m = this.mainPixels[n];
      i7 = this.act[(m & 0xFF)];
      i6 = i4;
      i5 = i;
      i3 = k;
      i2 = i1;
      m = j;
      if (i7 != 0)
      {
        i6 = i4 + (i7 >> 24 & 0xFF);
        i2 = i1 + (i7 >> 16 & 0xFF);
        i3 = k + (i7 >> 8 & 0xFF);
        i5 = i + (i7 & 0xFF);
        m = j + 1;
      }
      n += 1;
      i4 = i6;
      i = i5;
      k = i3;
      i1 = i2;
      j = m;
    }
    if (j == 0) {
      return 0;
    }
    return i4 / j << 24 | i1 / j << 16 | k / j << 8 | i / j;
  }
  
  private void decodeBitmapData(GifFrame paramGifFrame)
  {
    this.workBufferSize = 0;
    this.workBufferPosition = 0;
    if (paramGifFrame != null) {
      this.rawData.position(paramGifFrame.bufferFrameStart);
    }
    if (paramGifFrame == null) {}
    int i14;
    int i15;
    int i10;
    int j;
    int i2;
    int i1;
    for (int i6 = this.header.width * this.header.height;; i6 = paramGifFrame.iw * paramGifFrame.ih)
    {
      if ((this.mainPixels == null) || (this.mainPixels.length < i6)) {
        this.mainPixels = this.bitmapProvider.obtainByteArray(i6);
      }
      if (this.prefix == null) {
        this.prefix = new short['က'];
      }
      if (this.suffix == null) {
        this.suffix = new byte['က'];
      }
      if (this.pixelStack == null) {
        this.pixelStack = new byte['ခ'];
      }
      i14 = readByte();
      i15 = 1 << i14;
      i10 = i15 + 2;
      j = -1;
      i2 = i14 + 1;
      i1 = (1 << i2) - 1;
      i = 0;
      while (i < i15)
      {
        this.prefix[i] = 0;
        this.suffix[i] = ((byte)i);
        i += 1;
      }
    }
    int i3 = 0;
    int n = 0;
    int i = 0;
    int i4 = 0;
    int m = 0;
    int k = 0;
    int i5 = 0;
    int i7 = 0;
    for (;;)
    {
      if (i7 < i6)
      {
        i8 = i3;
        i3 = m;
        if (m != 0) {
          break label291;
        }
        i3 = readBlock();
        if (i3 <= 0) {
          this.status = 3;
        }
      }
      else
      {
        while (n < i6)
        {
          this.mainPixels[n] = 0;
          n += 1;
        }
      }
      int i8 = 0;
      label291:
      i5 += ((this.block[i8] & 0xFF) << k);
      m = k + 8;
      int i12 = i8 + 1;
      int i11 = i3 - 1;
      i3 = i10;
      for (;;)
      {
        if (m < i2) {
          break label789;
        }
        k = i5 & i1;
        i8 = i5 >> i2;
        int i9 = m - i2;
        if (k == i15)
        {
          i2 = i14 + 1;
          i1 = (1 << i2) - 1;
          i3 = i15 + 2;
          j = -1;
          m = i9;
          i5 = i8;
        }
        else
        {
          if (k > i3)
          {
            this.status = 3;
            i10 = i3;
            i3 = i12;
            k = i9;
            m = i11;
            i5 = i8;
            break;
          }
          if (k == i15 + 1)
          {
            i10 = i3;
            i3 = i12;
            k = i9;
            m = i11;
            i5 = i8;
            break;
          }
          if (j == -1)
          {
            this.pixelStack[i] = this.suffix[k];
            j = k;
            i += 1;
            m = i9;
            i5 = i8;
            i4 = k;
          }
          else
          {
            m = k;
            i5 = i;
            if (k >= i3)
            {
              this.pixelStack[i] = ((byte)i4);
              m = j;
              i5 = i + 1;
            }
            while (m >= i15)
            {
              this.pixelStack[i5] = this.suffix[m];
              m = this.prefix[m];
              i5 += 1;
            }
            int i13 = this.suffix[m] & 0xFF;
            paramGifFrame = this.pixelStack;
            i10 = i5 + 1;
            paramGifFrame[i5] = ((byte)i13);
            m = i3;
            i5 = i1;
            i4 = i2;
            if (i3 < 4096)
            {
              this.prefix[i3] = ((short)j);
              this.suffix[i3] = ((byte)i13);
              i = i3 + 1;
              m = i;
              i5 = i1;
              i4 = i2;
              if ((i & i1) == 0)
              {
                m = i;
                i5 = i1;
                i4 = i2;
                if (i < 4096)
                {
                  i4 = i2 + 1;
                  i5 = i1 + i;
                  m = i;
                }
              }
            }
            i = n;
            j = i10;
            while (j > 0)
            {
              paramGifFrame = this.mainPixels;
              byte[] arrayOfByte = this.pixelStack;
              j -= 1;
              paramGifFrame[i] = arrayOfByte[j];
              i7 += 1;
              i += 1;
              continue;
              return;
            }
            i10 = j;
            i3 = m;
            m = i9;
            i1 = i5;
            i2 = i4;
            i5 = i8;
            i4 = i13;
            j = k;
            n = i;
            i = i10;
          }
        }
      }
      label789:
      i10 = i3;
      i3 = i12;
      k = m;
      m = i11;
    }
  }
  
  private GifHeaderParser getHeaderParser()
  {
    if (this.parser == null) {
      this.parser = new GifHeaderParser();
    }
    return this.parser;
  }
  
  private Bitmap getNextBitmap()
  {
    if (this.isFirstFrameTransparent) {}
    for (Object localObject = Bitmap.Config.ARGB_8888;; localObject = Bitmap.Config.RGB_565)
    {
      localObject = this.bitmapProvider.obtain(this.downsampledWidth, this.downsampledHeight, (Bitmap.Config)localObject);
      setAlpha((Bitmap)localObject);
      return (Bitmap)localObject;
    }
  }
  
  private int readBlock()
  {
    int i = readByte();
    if (i > 0)
    {
      try
      {
        if (this.block == null) {
          this.block = this.bitmapProvider.obtainByteArray(255);
        }
        int j = this.workBufferSize - this.workBufferPosition;
        if (j >= i)
        {
          System.arraycopy(this.workBuffer, this.workBufferPosition, this.block, 0, i);
          this.workBufferPosition += i;
          return i;
        }
        if (this.rawData.remaining() + j >= i)
        {
          System.arraycopy(this.workBuffer, this.workBufferPosition, this.block, 0, j);
          this.workBufferPosition = this.workBufferSize;
          readChunkIfNeeded();
          int k = i - j;
          System.arraycopy(this.workBuffer, 0, this.block, j, k);
          this.workBufferPosition += k;
          return i;
        }
      }
      catch (Exception localException)
      {
        Log.w(TAG, "Error Reading Block", localException);
        this.status = 1;
        return i;
      }
      this.status = 1;
    }
    return i;
  }
  
  private int readByte()
  {
    try
    {
      readChunkIfNeeded();
      byte[] arrayOfByte = this.workBuffer;
      int i = this.workBufferPosition;
      this.workBufferPosition = (i + 1);
      i = arrayOfByte[i];
      return i & 0xFF;
    }
    catch (Exception localException)
    {
      this.status = 1;
    }
    return 0;
  }
  
  private void readChunkIfNeeded()
  {
    if (this.workBufferSize > this.workBufferPosition) {
      return;
    }
    if (this.workBuffer == null) {
      this.workBuffer = this.bitmapProvider.obtainByteArray(16384);
    }
    this.workBufferPosition = 0;
    this.workBufferSize = Math.min(this.rawData.remaining(), 16384);
    this.rawData.get(this.workBuffer, 0, this.workBufferSize);
  }
  
  @TargetApi(12)
  private static void setAlpha(Bitmap paramBitmap)
  {
    if (Build.VERSION.SDK_INT >= 12) {
      paramBitmap.setHasAlpha(true);
    }
  }
  
  private Bitmap setPixels(GifFrame paramGifFrame1, GifFrame paramGifFrame2)
  {
    int[] arrayOfInt = this.mainScratch;
    if (paramGifFrame2 == null) {
      Arrays.fill(arrayOfInt, 0);
    }
    int j;
    int i;
    label59:
    int i7;
    int i8;
    int i9;
    int i10;
    int n;
    int m;
    int i1;
    int i3;
    label128:
    int k;
    if ((paramGifFrame2 != null) && (paramGifFrame2.dispose > 0))
    {
      if (paramGifFrame2.dispose != 2) {
        break label412;
      }
      j = 0;
      if (!paramGifFrame1.transparency)
      {
        i = this.header.bgColor;
        Arrays.fill(arrayOfInt, i);
      }
    }
    else
    {
      decodeBitmapData(paramGifFrame1);
      i7 = paramGifFrame1.ih / this.sampleSize;
      i8 = paramGifFrame1.iy / this.sampleSize;
      i9 = paramGifFrame1.iw / this.sampleSize;
      i10 = paramGifFrame1.ix / this.sampleSize;
      n = 1;
      m = 8;
      i1 = 0;
      if (this.framePointer != 0) {
        break label454;
      }
      i3 = 1;
      k = 0;
    }
    for (;;)
    {
      if (k >= i7) {
        break label551;
      }
      i = k;
      int i5 = i1;
      int i4 = m;
      int i2 = n;
      if (paramGifFrame1.interlace)
      {
        i = i1;
        j = m;
        i2 = n;
        if (i1 >= i7) {
          i2 = n + 1;
        }
      }
      label219:
      int i11;
      label319:
      int i6;
      switch (i2)
      {
      default: 
        j = m;
        i = i1;
        m = i;
        i5 = i + j;
        i = m;
        i4 = j;
        i += i8;
        if (i >= this.downsampledHeight) {
          break label530;
        }
        j = i * this.downsampledWidth;
        n = j + i10;
        i = n + i9;
        m = i;
        if (this.downsampledWidth + j < i) {
          m = j + this.downsampledWidth;
        }
        i1 = this.sampleSize * k * paramGifFrame1.iw;
        i11 = this.sampleSize;
        j = i1;
        i = n;
        i6 = j;
        j = i;
        if (j >= m) {
          break label530;
        }
        if (this.sampleSize == 1)
        {
          i = this.mainPixels[i6];
          i = this.act[(i & 0xFF)];
          label360:
          if (i == 0) {
            break label510;
          }
          arrayOfInt[j] = i;
        }
        break;
      }
      for (;;)
      {
        i6 += this.sampleSize;
        i = j + 1;
        j = i6;
        break label319;
        i = j;
        if (this.framePointer != 0) {
          break;
        }
        this.isFirstFrameTransparent = true;
        i = j;
        break;
        label412:
        if ((paramGifFrame2.dispose != 3) || (this.previousImage == null)) {
          break label59;
        }
        this.previousImage.getPixels(arrayOfInt, 0, this.downsampledWidth, 0, 0, this.downsampledWidth, this.downsampledHeight);
        break label59;
        label454:
        i3 = 0;
        break label128;
        i = 4;
        j = m;
        break label219;
        i = 2;
        j = 4;
        break label219;
        i = 1;
        j = 2;
        break label219;
        i = averageColorsNear(i6, i1 + (m - n) * i11, paramGifFrame1.iw);
        break label360;
        label510:
        if ((!this.isFirstFrameTransparent) && (i3 != 0)) {
          this.isFirstFrameTransparent = true;
        }
      }
      label530:
      k += 1;
      i1 = i5;
      m = i4;
      n = i2;
    }
    label551:
    if ((this.savePrevious) && ((paramGifFrame1.dispose == 0) || (paramGifFrame1.dispose == 1)))
    {
      if (this.previousImage == null) {
        this.previousImage = getNextBitmap();
      }
      this.previousImage.setPixels(arrayOfInt, 0, this.downsampledWidth, 0, 0, this.downsampledWidth, this.downsampledHeight);
    }
    paramGifFrame1 = getNextBitmap();
    paramGifFrame1.setPixels(arrayOfInt, 0, this.downsampledWidth, 0, 0, this.downsampledWidth, this.downsampledHeight);
    return paramGifFrame1;
  }
  
  public void advance()
  {
    this.framePointer = ((this.framePointer + 1) % this.header.frameCount);
  }
  
  public void clear()
  {
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
  
  public int getByteSize()
  {
    return this.rawData.limit() + this.mainPixels.length + this.mainScratch.length * 4;
  }
  
  public int getCurrentFrameIndex()
  {
    return this.framePointer;
  }
  
  public ByteBuffer getData()
  {
    return this.rawData;
  }
  
  public int getDelay(int paramInt)
  {
    int j = -1;
    int i = j;
    if (paramInt >= 0)
    {
      i = j;
      if (paramInt < this.header.frameCount) {
        i = ((GifFrame)this.header.frames.get(paramInt)).delay;
      }
    }
    return i;
  }
  
  public int getFrameCount()
  {
    return this.header.frameCount;
  }
  
  public int getHeight()
  {
    return this.header.height;
  }
  
  public int getLoopCount()
  {
    return this.header.loopCount;
  }
  
  public int getNextDelay()
  {
    if ((this.header.frameCount <= 0) || (this.framePointer < 0)) {
      return 0;
    }
    return getDelay(this.framePointer);
  }
  
  public Bitmap getNextFrame()
  {
    Object localObject2 = null;
    for (;;)
    {
      int i;
      int j;
      GifFrame localGifFrame2;
      try
      {
        if ((this.header.frameCount <= 0) || (this.framePointer < 0))
        {
          if (Log.isLoggable(TAG, 3))
          {
            localObject1 = TAG;
            i = this.header.frameCount;
            j = this.framePointer;
            Log.d((String)localObject1, 71 + "unable to decode frame, frameCount=" + i + " framePointer=" + j);
          }
          this.status = 1;
        }
        if ((this.status == 1) || (this.status == 2))
        {
          localObject1 = localObject2;
          if (Log.isLoggable(TAG, 3))
          {
            localObject1 = TAG;
            i = this.status;
            Log.d((String)localObject1, 42 + "Unable to decode frame, status=" + i);
            localObject1 = localObject2;
          }
          return (Bitmap)localObject1;
        }
        this.status = 0;
        localGifFrame2 = (GifFrame)this.header.frames.get(this.framePointer);
        Object localObject1 = null;
        i = this.framePointer - 1;
        if (i >= 0) {
          localObject1 = (GifFrame)this.header.frames.get(i);
        }
        j = this.header.bgColor;
        if (localGifFrame2.lct == null)
        {
          this.act = this.header.gct;
          i = 0;
          if (localGifFrame2.transparency)
          {
            i = this.act[localGifFrame2.transIndex];
            this.act[localGifFrame2.transIndex] = 0;
          }
          if (this.act != null) {
            break label356;
          }
          if (Log.isLoggable(TAG, 3)) {
            Log.d(TAG, "No Valid Color Table");
          }
          this.status = 1;
          localObject1 = localObject2;
          continue;
        }
        this.act = localGifFrame2.lct;
      }
      finally {}
      if (this.header.bgIndex == localGifFrame2.transIndex)
      {
        this.header.bgColor = 0;
        continue;
        label356:
        Bitmap localBitmap = setPixels(localGifFrame2, localGifFrame1);
        if (localGifFrame2.transparency) {
          this.act[localGifFrame2.transIndex] = i;
        }
        this.header.bgColor = j;
      }
    }
  }
  
  public int getStatus()
  {
    return this.status;
  }
  
  public int getWidth()
  {
    return this.header.width;
  }
  
  public int read(InputStream paramInputStream, int paramInt)
  {
    int i = 16384;
    if (paramInputStream != null)
    {
      if (paramInt > 0) {
        i = paramInt + 4096;
      }
      try
      {
        ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream(i);
        byte[] arrayOfByte = new byte['䀀'];
        for (;;)
        {
          paramInt = paramInputStream.read(arrayOfByte, 0, arrayOfByte.length);
          if (paramInt == -1) {
            break;
          }
          localByteArrayOutputStream.write(arrayOfByte, 0, paramInt);
        }
        if (paramInputStream == null) {}
      }
      catch (IOException localIOException)
      {
        Log.w(TAG, "Error reading data from stream", localIOException);
      }
    }
    try
    {
      for (;;)
      {
        paramInputStream.close();
        return this.status;
        localIOException.flush();
        read(localIOException.toByteArray());
      }
      this.status = 2;
    }
    catch (IOException paramInputStream)
    {
      for (;;)
      {
        Log.w(TAG, "Error closing stream", paramInputStream);
      }
    }
  }
  
  public int read(byte[] paramArrayOfByte)
  {
    try
    {
      this.header = getHeaderParser().setData(paramArrayOfByte).parseHeader();
      if (paramArrayOfByte != null) {
        setData(this.header, paramArrayOfByte);
      }
      int i = this.status;
      return i;
    }
    finally {}
  }
  
  public void resetFrameIndex()
  {
    this.framePointer = -1;
  }
  
  public void setData(GifHeader paramGifHeader, ByteBuffer paramByteBuffer)
  {
    try
    {
      setData(paramGifHeader, paramByteBuffer, 1);
      return;
    }
    finally
    {
      paramGifHeader = finally;
      throw paramGifHeader;
    }
  }
  
  public void setData(GifHeader paramGifHeader, ByteBuffer paramByteBuffer, int paramInt)
  {
    if (paramInt <= 0) {
      try
      {
        throw new IllegalArgumentException(41 + "Sample size must be >=0, not: " + paramInt);
      }
      finally {}
    }
    paramInt = Integer.highestOneBit(paramInt);
    this.status = 0;
    this.header = paramGifHeader;
    this.isFirstFrameTransparent = false;
    this.framePointer = -1;
    this.rawData = paramByteBuffer.asReadOnlyBuffer();
    this.rawData.position(0);
    this.rawData.order(ByteOrder.LITTLE_ENDIAN);
    this.savePrevious = false;
    paramByteBuffer = paramGifHeader.frames.iterator();
    while (paramByteBuffer.hasNext()) {
      if (((GifFrame)paramByteBuffer.next()).dispose == 3) {
        this.savePrevious = true;
      }
    }
    this.sampleSize = paramInt;
    this.mainPixels = this.bitmapProvider.obtainByteArray(paramGifHeader.width * paramGifHeader.height);
    this.mainScratch = this.bitmapProvider.obtainIntArray(paramGifHeader.width / paramInt * (paramGifHeader.height / paramInt));
    this.downsampledWidth = (paramGifHeader.width / paramInt);
    this.downsampledHeight = (paramGifHeader.height / paramInt);
  }
  
  public void setData(GifHeader paramGifHeader, byte[] paramArrayOfByte)
  {
    try
    {
      setData(paramGifHeader, ByteBuffer.wrap(paramArrayOfByte));
      return;
    }
    finally
    {
      paramGifHeader = finally;
      throw paramGifHeader;
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/gifdecoder/StandardGifDecoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */