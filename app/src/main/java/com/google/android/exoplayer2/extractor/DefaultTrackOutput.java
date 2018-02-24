package com.google.android.exoplayer2.extractor;

import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.FormatHolder;
import com.google.android.exoplayer2.decoder.CryptoInfo;
import com.google.android.exoplayer2.decoder.DecoderInputBuffer;
import com.google.android.exoplayer2.upstream.Allocation;
import com.google.android.exoplayer2.upstream.Allocator;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.Util;
import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

public final class DefaultTrackOutput
  implements TrackOutput
{
  private static final int INITIAL_SCRATCH_SIZE = 32;
  private static final int STATE_DISABLED = 2;
  private static final int STATE_ENABLED = 0;
  private static final int STATE_ENABLED_WRITING = 1;
  private final int allocationLength;
  private final Allocator allocator;
  private final LinkedBlockingDeque<Allocation> dataQueue;
  private Format downstreamFormat;
  private final BufferExtrasHolder extrasHolder;
  private final InfoQueue infoQueue;
  private Allocation lastAllocation;
  private int lastAllocationOffset;
  private boolean needKeyframe;
  private boolean pendingSplice;
  private long sampleOffsetUs;
  private final ParsableByteArray scratch;
  private final AtomicInteger state;
  private long totalBytesDropped;
  private long totalBytesWritten;
  private UpstreamFormatChangedListener upstreamFormatChangeListener;
  
  public DefaultTrackOutput(Allocator paramAllocator)
  {
    this.allocator = paramAllocator;
    this.allocationLength = paramAllocator.getIndividualAllocationLength();
    this.infoQueue = new InfoQueue();
    this.dataQueue = new LinkedBlockingDeque();
    this.extrasHolder = new BufferExtrasHolder(null);
    this.scratch = new ParsableByteArray(32);
    this.state = new AtomicInteger();
    this.lastAllocationOffset = this.allocationLength;
    this.needKeyframe = true;
  }
  
  private void clearSampleData()
  {
    this.infoQueue.clearSampleData();
    this.allocator.release((Allocation[])this.dataQueue.toArray(new Allocation[this.dataQueue.size()]));
    this.dataQueue.clear();
    this.allocator.trim();
    this.totalBytesDropped = 0L;
    this.totalBytesWritten = 0L;
    this.lastAllocation = null;
    this.lastAllocationOffset = this.allocationLength;
    this.needKeyframe = true;
  }
  
  private void dropDownstreamTo(long paramLong)
  {
    int j = (int)(paramLong - this.totalBytesDropped) / this.allocationLength;
    int i = 0;
    while (i < j)
    {
      this.allocator.release((Allocation)this.dataQueue.remove());
      this.totalBytesDropped += this.allocationLength;
      i += 1;
    }
  }
  
  private void dropUpstreamFrom(long paramLong)
  {
    int j = (int)(paramLong - this.totalBytesDropped);
    int i = j / this.allocationLength;
    int k = j % this.allocationLength;
    j = this.dataQueue.size() - i - 1;
    i = j;
    if (k == 0) {
      i = j + 1;
    }
    j = 0;
    while (j < i)
    {
      this.allocator.release((Allocation)this.dataQueue.removeLast());
      j += 1;
    }
    this.lastAllocation = ((Allocation)this.dataQueue.peekLast());
    i = k;
    if (k == 0) {
      i = this.allocationLength;
    }
    this.lastAllocationOffset = i;
  }
  
  private void endWriteOperation()
  {
    if (!this.state.compareAndSet(1, 0)) {
      clearSampleData();
    }
  }
  
  private static Format getAdjustedSampleFormat(Format paramFormat, long paramLong)
  {
    Format localFormat;
    if (paramFormat == null) {
      localFormat = null;
    }
    do
    {
      do
      {
        return localFormat;
        localFormat = paramFormat;
      } while (paramLong == 0L);
      localFormat = paramFormat;
    } while (paramFormat.subsampleOffsetUs == Long.MAX_VALUE);
    return paramFormat.copyWithSubsampleOffsetUs(paramFormat.subsampleOffsetUs + paramLong);
  }
  
  private int prepareForAppend(int paramInt)
  {
    if (this.lastAllocationOffset == this.allocationLength)
    {
      this.lastAllocationOffset = 0;
      this.lastAllocation = this.allocator.allocate();
      this.dataQueue.add(this.lastAllocation);
    }
    return Math.min(paramInt, this.allocationLength - this.lastAllocationOffset);
  }
  
  private void readData(long paramLong, ByteBuffer paramByteBuffer, int paramInt)
  {
    while (paramInt > 0)
    {
      dropDownstreamTo(paramLong);
      int i = (int)(paramLong - this.totalBytesDropped);
      int j = Math.min(paramInt, this.allocationLength - i);
      Allocation localAllocation = (Allocation)this.dataQueue.peek();
      paramByteBuffer.put(localAllocation.data, localAllocation.translateOffset(i), j);
      paramLong += j;
      paramInt -= j;
    }
  }
  
  private void readData(long paramLong, byte[] paramArrayOfByte, int paramInt)
  {
    int i = 0;
    while (i < paramInt)
    {
      dropDownstreamTo(paramLong);
      int j = (int)(paramLong - this.totalBytesDropped);
      int k = Math.min(paramInt - i, this.allocationLength - j);
      Allocation localAllocation = (Allocation)this.dataQueue.peek();
      System.arraycopy(localAllocation.data, localAllocation.translateOffset(j), paramArrayOfByte, i, k);
      paramLong += k;
      i += k;
    }
  }
  
  private void readEncryptionData(DecoderInputBuffer paramDecoderInputBuffer, BufferExtrasHolder paramBufferExtrasHolder)
  {
    long l1 = paramBufferExtrasHolder.offset;
    this.scratch.reset(1);
    readData(l1, this.scratch.data, 1);
    l1 += 1L;
    int j = this.scratch.data[0];
    if ((j & 0x80) != 0)
    {
      i = 1;
      j &= 0x7F;
      if (paramDecoderInputBuffer.cryptoInfo.iv == null) {
        paramDecoderInputBuffer.cryptoInfo.iv = new byte[16];
      }
      readData(l1, paramDecoderInputBuffer.cryptoInfo.iv, j);
      l1 += j;
      if (i == 0) {
        break label307;
      }
      this.scratch.reset(2);
      readData(l1, this.scratch.data, 2);
      l1 += 2L;
    }
    Object localObject2;
    Object localObject1;
    label307:
    for (j = this.scratch.readUnsignedShort();; j = 1)
    {
      localObject2 = paramDecoderInputBuffer.cryptoInfo.numBytesOfClearData;
      if (localObject2 != null)
      {
        localObject1 = localObject2;
        if (localObject2.length >= j) {}
      }
      else
      {
        localObject1 = new int[j];
      }
      int[] arrayOfInt = paramDecoderInputBuffer.cryptoInfo.numBytesOfEncryptedData;
      if (arrayOfInt != null)
      {
        localObject2 = arrayOfInt;
        if (arrayOfInt.length >= j) {}
      }
      else
      {
        localObject2 = new int[j];
      }
      if (i == 0) {
        break label313;
      }
      i = j * 6;
      this.scratch.reset(i);
      readData(l1, this.scratch.data, i);
      long l2 = l1 + i;
      this.scratch.setPosition(0);
      i = 0;
      for (;;)
      {
        l1 = l2;
        if (i >= j) {
          break;
        }
        localObject1[i] = this.scratch.readUnsignedShort();
        localObject2[i] = this.scratch.readUnsignedIntToInt();
        i += 1;
      }
      i = 0;
      break;
    }
    label313:
    localObject1[0] = 0;
    localObject2[0] = (paramBufferExtrasHolder.size - (int)(l1 - paramBufferExtrasHolder.offset));
    paramDecoderInputBuffer.cryptoInfo.set(j, (int[])localObject1, (int[])localObject2, paramBufferExtrasHolder.encryptionKeyId, paramDecoderInputBuffer.cryptoInfo.iv, 1);
    int i = (int)(l1 - paramBufferExtrasHolder.offset);
    paramBufferExtrasHolder.offset += i;
    paramBufferExtrasHolder.size -= i;
  }
  
  private boolean startWriteOperation()
  {
    return this.state.compareAndSet(0, 1);
  }
  
  public void disable()
  {
    if (this.state.getAndSet(2) == 0) {
      clearSampleData();
    }
  }
  
  public void discardUpstreamSamples(int paramInt)
  {
    this.totalBytesWritten = this.infoQueue.discardUpstreamSamples(paramInt);
    dropUpstreamFrom(this.totalBytesWritten);
  }
  
  public void format(Format paramFormat)
  {
    paramFormat = getAdjustedSampleFormat(paramFormat, this.sampleOffsetUs);
    boolean bool = this.infoQueue.format(paramFormat);
    if ((this.upstreamFormatChangeListener != null) && (bool)) {
      this.upstreamFormatChangeListener.onUpstreamFormatChanged(paramFormat);
    }
  }
  
  public void formatWithOffset(Format paramFormat, long paramLong)
  {
    this.sampleOffsetUs = paramLong;
    format(paramFormat);
  }
  
  public long getLargestQueuedTimestampUs()
  {
    return this.infoQueue.getLargestQueuedTimestampUs();
  }
  
  public int getReadIndex()
  {
    return this.infoQueue.getReadIndex();
  }
  
  public Format getUpstreamFormat()
  {
    return this.infoQueue.getUpstreamFormat();
  }
  
  public int getWriteIndex()
  {
    return this.infoQueue.getWriteIndex();
  }
  
  public boolean isEmpty()
  {
    return this.infoQueue.isEmpty();
  }
  
  public int peekSourceId()
  {
    return this.infoQueue.peekSourceId();
  }
  
  public int readData(FormatHolder paramFormatHolder, DecoderInputBuffer paramDecoderInputBuffer, boolean paramBoolean, long paramLong)
  {
    switch (this.infoQueue.readData(paramFormatHolder, paramDecoderInputBuffer, this.downstreamFormat, this.extrasHolder))
    {
    default: 
      throw new IllegalStateException();
    case -3: 
      if (paramBoolean)
      {
        paramDecoderInputBuffer.setFlags(4);
        return -4;
      }
      return -3;
    case -5: 
      this.downstreamFormat = paramFormatHolder.format;
      return -5;
    }
    if (paramDecoderInputBuffer.timeUs < paramLong) {
      paramDecoderInputBuffer.addFlag(Integer.MIN_VALUE);
    }
    if (paramDecoderInputBuffer.isEncrypted()) {
      readEncryptionData(paramDecoderInputBuffer, this.extrasHolder);
    }
    paramDecoderInputBuffer.ensureSpaceForWrite(this.extrasHolder.size);
    readData(this.extrasHolder.offset, paramDecoderInputBuffer.data, this.extrasHolder.size);
    dropDownstreamTo(this.extrasHolder.nextOffset);
    return -4;
  }
  
  public void reset(boolean paramBoolean)
  {
    AtomicInteger localAtomicInteger = this.state;
    if (paramBoolean) {}
    for (int i = 0;; i = 2)
    {
      i = localAtomicInteger.getAndSet(i);
      clearSampleData();
      this.infoQueue.resetLargestParsedTimestamps();
      if (i == 2) {
        this.downstreamFormat = null;
      }
      return;
    }
  }
  
  public int sampleData(ExtractorInput paramExtractorInput, int paramInt, boolean paramBoolean)
    throws IOException, InterruptedException
  {
    if (!startWriteOperation())
    {
      int i = paramExtractorInput.skip(paramInt);
      paramInt = i;
      if (i == -1)
      {
        if (paramBoolean) {
          paramInt = -1;
        }
      }
      else {
        return paramInt;
      }
      throw new EOFException();
    }
    try
    {
      paramInt = prepareForAppend(paramInt);
      paramInt = paramExtractorInput.read(this.lastAllocation.data, this.lastAllocation.translateOffset(this.lastAllocationOffset), paramInt);
      if (paramInt == -1)
      {
        if (paramBoolean) {
          return -1;
        }
        throw new EOFException();
      }
    }
    finally
    {
      endWriteOperation();
    }
    this.lastAllocationOffset += paramInt;
    this.totalBytesWritten += paramInt;
    endWriteOperation();
    return paramInt;
  }
  
  public void sampleData(ParsableByteArray paramParsableByteArray, int paramInt)
  {
    int i = paramInt;
    if (!startWriteOperation())
    {
      paramParsableByteArray.skipBytes(paramInt);
      return;
    }
    while (i > 0)
    {
      paramInt = prepareForAppend(i);
      paramParsableByteArray.readBytes(this.lastAllocation.data, this.lastAllocation.translateOffset(this.lastAllocationOffset), paramInt);
      this.lastAllocationOffset += paramInt;
      this.totalBytesWritten += paramInt;
      i -= paramInt;
    }
    endWriteOperation();
  }
  
  public void sampleMetadata(long paramLong, int paramInt1, int paramInt2, int paramInt3, byte[] paramArrayOfByte)
  {
    if (!startWriteOperation())
    {
      this.infoQueue.commitSampleTimestamp(paramLong);
      return;
    }
    try
    {
      if (this.pendingSplice)
      {
        if ((paramInt1 & 0x1) != 0)
        {
          bool = this.infoQueue.attemptSplice(paramLong);
          if (bool) {}
        }
        else
        {
          return;
        }
        this.pendingSplice = false;
      }
      boolean bool = this.needKeyframe;
      if (bool)
      {
        if ((paramInt1 & 0x1) == 0) {
          return;
        }
        this.needKeyframe = false;
      }
      long l1 = this.sampleOffsetUs;
      long l2 = this.totalBytesWritten;
      long l3 = paramInt2;
      long l4 = paramInt3;
      this.infoQueue.commitSample(paramLong + l1, paramInt1, l2 - l3 - l4, paramInt2, paramArrayOfByte);
      return;
    }
    finally
    {
      endWriteOperation();
    }
  }
  
  public void setUpstreamFormatChangeListener(UpstreamFormatChangedListener paramUpstreamFormatChangedListener)
  {
    this.upstreamFormatChangeListener = paramUpstreamFormatChangedListener;
  }
  
  public boolean skipToKeyframeBefore(long paramLong)
  {
    return skipToKeyframeBefore(paramLong, false);
  }
  
  public boolean skipToKeyframeBefore(long paramLong, boolean paramBoolean)
  {
    paramLong = this.infoQueue.skipToKeyframeBefore(paramLong, paramBoolean);
    if (paramLong == -1L) {
      return false;
    }
    dropDownstreamTo(paramLong);
    return true;
  }
  
  public void sourceId(int paramInt)
  {
    this.infoQueue.sourceId(paramInt);
  }
  
  public void splice()
  {
    this.pendingSplice = true;
  }
  
  private static final class BufferExtrasHolder
  {
    public byte[] encryptionKeyId;
    public long nextOffset;
    public long offset;
    public int size;
  }
  
  private static final class InfoQueue
  {
    private static final int SAMPLE_CAPACITY_INCREMENT = 1000;
    private int absoluteReadIndex;
    private int capacity = 1000;
    private byte[][] encryptionKeys = new byte[this.capacity][];
    private int[] flags = new int[this.capacity];
    private Format[] formats = new Format[this.capacity];
    private long largestDequeuedTimestampUs = Long.MIN_VALUE;
    private long largestQueuedTimestampUs = Long.MIN_VALUE;
    private long[] offsets = new long[this.capacity];
    private int queueSize;
    private int relativeReadIndex;
    private int relativeWriteIndex;
    private int[] sizes = new int[this.capacity];
    private int[] sourceIds = new int[this.capacity];
    private long[] timesUs = new long[this.capacity];
    private Format upstreamFormat;
    private boolean upstreamFormatRequired = true;
    private int upstreamSourceId;
    
    /* Error */
    public boolean attemptSplice(long paramLong)
    {
      // Byte code:
      //   0: aload_0
      //   1: monitorenter
      //   2: aload_0
      //   3: getfield 62	com/google/android/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:largestDequeuedTimestampUs	J
      //   6: lstore 4
      //   8: lload 4
      //   10: lload_1
      //   11: lcmp
      //   12: iflt +11 -> 23
      //   15: iconst_0
      //   16: istore 6
      //   18: aload_0
      //   19: monitorexit
      //   20: iload 6
      //   22: ireturn
      //   23: aload_0
      //   24: getfield 71	com/google/android/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:queueSize	I
      //   27: istore_3
      //   28: iload_3
      //   29: ifle +33 -> 62
      //   32: aload_0
      //   33: getfield 46	com/google/android/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:timesUs	[J
      //   36: aload_0
      //   37: getfield 73	com/google/android/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:relativeReadIndex	I
      //   40: iload_3
      //   41: iadd
      //   42: iconst_1
      //   43: isub
      //   44: aload_0
      //   45: getfield 40	com/google/android/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:capacity	I
      //   48: irem
      //   49: laload
      //   50: lload_1
      //   51: lcmp
      //   52: iflt +10 -> 62
      //   55: iload_3
      //   56: iconst_1
      //   57: isub
      //   58: istore_3
      //   59: goto -31 -> 28
      //   62: aload_0
      //   63: aload_0
      //   64: getfield 75	com/google/android/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:absoluteReadIndex	I
      //   67: iload_3
      //   68: iadd
      //   69: invokevirtual 79	com/google/android/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:discardUpstreamSamples	(I)J
      //   72: pop2
      //   73: iconst_1
      //   74: istore 6
      //   76: goto -58 -> 18
      //   79: astore 7
      //   81: aload_0
      //   82: monitorexit
      //   83: aload 7
      //   85: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	86	0	this	InfoQueue
      //   0	86	1	paramLong	long
      //   27	42	3	i	int
      //   6	3	4	l	long
      //   16	59	6	bool	boolean
      //   79	5	7	localObject	Object
      // Exception table:
      //   from	to	target	type
      //   2	8	79	finally
      //   23	28	79	finally
      //   32	55	79	finally
      //   62	73	79	finally
    }
    
    public void clearSampleData()
    {
      this.absoluteReadIndex = 0;
      this.relativeReadIndex = 0;
      this.relativeWriteIndex = 0;
      this.queueSize = 0;
    }
    
    /* Error */
    public void commitSample(long paramLong1, int paramInt1, long paramLong2, int paramInt2, byte[] paramArrayOfByte)
    {
      // Byte code:
      //   0: aload_0
      //   1: monitorenter
      //   2: aload_0
      //   3: getfield 66	com/google/android/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:upstreamFormatRequired	Z
      //   6: ifne +460 -> 466
      //   9: iconst_1
      //   10: istore 9
      //   12: iload 9
      //   14: invokestatic 90	com/google/android/exoplayer2/util/Assertions:checkState	(Z)V
      //   17: aload_0
      //   18: lload_1
      //   19: invokevirtual 94	com/google/android/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:commitSampleTimestamp	(J)V
      //   22: aload_0
      //   23: getfield 46	com/google/android/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:timesUs	[J
      //   26: aload_0
      //   27: getfield 82	com/google/android/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:relativeWriteIndex	I
      //   30: lload_1
      //   31: lastore
      //   32: aload_0
      //   33: getfield 44	com/google/android/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:offsets	[J
      //   36: aload_0
      //   37: getfield 82	com/google/android/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:relativeWriteIndex	I
      //   40: lload 4
      //   42: lastore
      //   43: aload_0
      //   44: getfield 50	com/google/android/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:sizes	[I
      //   47: aload_0
      //   48: getfield 82	com/google/android/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:relativeWriteIndex	I
      //   51: iload 6
      //   53: iastore
      //   54: aload_0
      //   55: getfield 48	com/google/android/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:flags	[I
      //   58: aload_0
      //   59: getfield 82	com/google/android/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:relativeWriteIndex	I
      //   62: iload_3
      //   63: iastore
      //   64: aload_0
      //   65: getfield 54	com/google/android/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:encryptionKeys	[[B
      //   68: aload_0
      //   69: getfield 82	com/google/android/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:relativeWriteIndex	I
      //   72: aload 7
      //   74: aastore
      //   75: aload_0
      //   76: getfield 58	com/google/android/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:formats	[Lcom/google/android/exoplayer2/Format;
      //   79: aload_0
      //   80: getfield 82	com/google/android/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:relativeWriteIndex	I
      //   83: aload_0
      //   84: getfield 96	com/google/android/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:upstreamFormat	Lcom/google/android/exoplayer2/Format;
      //   87: aastore
      //   88: aload_0
      //   89: getfield 42	com/google/android/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:sourceIds	[I
      //   92: aload_0
      //   93: getfield 82	com/google/android/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:relativeWriteIndex	I
      //   96: aload_0
      //   97: getfield 98	com/google/android/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:upstreamSourceId	I
      //   100: iastore
      //   101: aload_0
      //   102: aload_0
      //   103: getfield 71	com/google/android/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:queueSize	I
      //   106: iconst_1
      //   107: iadd
      //   108: putfield 71	com/google/android/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:queueSize	I
      //   111: aload_0
      //   112: getfield 71	com/google/android/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:queueSize	I
      //   115: aload_0
      //   116: getfield 40	com/google/android/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:capacity	I
      //   119: if_icmpne +353 -> 472
      //   122: aload_0
      //   123: getfield 40	com/google/android/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:capacity	I
      //   126: sipush 1000
      //   129: iadd
      //   130: istore_3
      //   131: iload_3
      //   132: newarray <illegal type>
      //   134: astore 7
      //   136: iload_3
      //   137: newarray <illegal type>
      //   139: astore 10
      //   141: iload_3
      //   142: newarray <illegal type>
      //   144: astore 11
      //   146: iload_3
      //   147: newarray <illegal type>
      //   149: astore 12
      //   151: iload_3
      //   152: newarray <illegal type>
      //   154: astore 13
      //   156: iload_3
      //   157: anewarray 52	[B
      //   160: astore 14
      //   162: iload_3
      //   163: anewarray 56	com/google/android/exoplayer2/Format
      //   166: astore 15
      //   168: aload_0
      //   169: getfield 40	com/google/android/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:capacity	I
      //   172: aload_0
      //   173: getfield 73	com/google/android/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:relativeReadIndex	I
      //   176: isub
      //   177: istore 6
      //   179: aload_0
      //   180: getfield 44	com/google/android/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:offsets	[J
      //   183: aload_0
      //   184: getfield 73	com/google/android/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:relativeReadIndex	I
      //   187: aload 10
      //   189: iconst_0
      //   190: iload 6
      //   192: invokestatic 104	java/lang/System:arraycopy	(Ljava/lang/Object;ILjava/lang/Object;II)V
      //   195: aload_0
      //   196: getfield 46	com/google/android/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:timesUs	[J
      //   199: aload_0
      //   200: getfield 73	com/google/android/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:relativeReadIndex	I
      //   203: aload 11
      //   205: iconst_0
      //   206: iload 6
      //   208: invokestatic 104	java/lang/System:arraycopy	(Ljava/lang/Object;ILjava/lang/Object;II)V
      //   211: aload_0
      //   212: getfield 48	com/google/android/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:flags	[I
      //   215: aload_0
      //   216: getfield 73	com/google/android/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:relativeReadIndex	I
      //   219: aload 12
      //   221: iconst_0
      //   222: iload 6
      //   224: invokestatic 104	java/lang/System:arraycopy	(Ljava/lang/Object;ILjava/lang/Object;II)V
      //   227: aload_0
      //   228: getfield 50	com/google/android/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:sizes	[I
      //   231: aload_0
      //   232: getfield 73	com/google/android/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:relativeReadIndex	I
      //   235: aload 13
      //   237: iconst_0
      //   238: iload 6
      //   240: invokestatic 104	java/lang/System:arraycopy	(Ljava/lang/Object;ILjava/lang/Object;II)V
      //   243: aload_0
      //   244: getfield 54	com/google/android/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:encryptionKeys	[[B
      //   247: aload_0
      //   248: getfield 73	com/google/android/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:relativeReadIndex	I
      //   251: aload 14
      //   253: iconst_0
      //   254: iload 6
      //   256: invokestatic 104	java/lang/System:arraycopy	(Ljava/lang/Object;ILjava/lang/Object;II)V
      //   259: aload_0
      //   260: getfield 58	com/google/android/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:formats	[Lcom/google/android/exoplayer2/Format;
      //   263: aload_0
      //   264: getfield 73	com/google/android/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:relativeReadIndex	I
      //   267: aload 15
      //   269: iconst_0
      //   270: iload 6
      //   272: invokestatic 104	java/lang/System:arraycopy	(Ljava/lang/Object;ILjava/lang/Object;II)V
      //   275: aload_0
      //   276: getfield 42	com/google/android/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:sourceIds	[I
      //   279: aload_0
      //   280: getfield 73	com/google/android/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:relativeReadIndex	I
      //   283: aload 7
      //   285: iconst_0
      //   286: iload 6
      //   288: invokestatic 104	java/lang/System:arraycopy	(Ljava/lang/Object;ILjava/lang/Object;II)V
      //   291: aload_0
      //   292: getfield 73	com/google/android/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:relativeReadIndex	I
      //   295: istore 8
      //   297: aload_0
      //   298: getfield 44	com/google/android/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:offsets	[J
      //   301: iconst_0
      //   302: aload 10
      //   304: iload 6
      //   306: iload 8
      //   308: invokestatic 104	java/lang/System:arraycopy	(Ljava/lang/Object;ILjava/lang/Object;II)V
      //   311: aload_0
      //   312: getfield 46	com/google/android/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:timesUs	[J
      //   315: iconst_0
      //   316: aload 11
      //   318: iload 6
      //   320: iload 8
      //   322: invokestatic 104	java/lang/System:arraycopy	(Ljava/lang/Object;ILjava/lang/Object;II)V
      //   325: aload_0
      //   326: getfield 48	com/google/android/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:flags	[I
      //   329: iconst_0
      //   330: aload 12
      //   332: iload 6
      //   334: iload 8
      //   336: invokestatic 104	java/lang/System:arraycopy	(Ljava/lang/Object;ILjava/lang/Object;II)V
      //   339: aload_0
      //   340: getfield 50	com/google/android/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:sizes	[I
      //   343: iconst_0
      //   344: aload 13
      //   346: iload 6
      //   348: iload 8
      //   350: invokestatic 104	java/lang/System:arraycopy	(Ljava/lang/Object;ILjava/lang/Object;II)V
      //   353: aload_0
      //   354: getfield 54	com/google/android/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:encryptionKeys	[[B
      //   357: iconst_0
      //   358: aload 14
      //   360: iload 6
      //   362: iload 8
      //   364: invokestatic 104	java/lang/System:arraycopy	(Ljava/lang/Object;ILjava/lang/Object;II)V
      //   367: aload_0
      //   368: getfield 58	com/google/android/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:formats	[Lcom/google/android/exoplayer2/Format;
      //   371: iconst_0
      //   372: aload 15
      //   374: iload 6
      //   376: iload 8
      //   378: invokestatic 104	java/lang/System:arraycopy	(Ljava/lang/Object;ILjava/lang/Object;II)V
      //   381: aload_0
      //   382: getfield 42	com/google/android/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:sourceIds	[I
      //   385: iconst_0
      //   386: aload 7
      //   388: iload 6
      //   390: iload 8
      //   392: invokestatic 104	java/lang/System:arraycopy	(Ljava/lang/Object;ILjava/lang/Object;II)V
      //   395: aload_0
      //   396: aload 10
      //   398: putfield 44	com/google/android/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:offsets	[J
      //   401: aload_0
      //   402: aload 11
      //   404: putfield 46	com/google/android/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:timesUs	[J
      //   407: aload_0
      //   408: aload 12
      //   410: putfield 48	com/google/android/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:flags	[I
      //   413: aload_0
      //   414: aload 13
      //   416: putfield 50	com/google/android/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:sizes	[I
      //   419: aload_0
      //   420: aload 14
      //   422: putfield 54	com/google/android/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:encryptionKeys	[[B
      //   425: aload_0
      //   426: aload 15
      //   428: putfield 58	com/google/android/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:formats	[Lcom/google/android/exoplayer2/Format;
      //   431: aload_0
      //   432: aload 7
      //   434: putfield 42	com/google/android/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:sourceIds	[I
      //   437: aload_0
      //   438: iconst_0
      //   439: putfield 73	com/google/android/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:relativeReadIndex	I
      //   442: aload_0
      //   443: aload_0
      //   444: getfield 40	com/google/android/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:capacity	I
      //   447: putfield 82	com/google/android/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:relativeWriteIndex	I
      //   450: aload_0
      //   451: aload_0
      //   452: getfield 40	com/google/android/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:capacity	I
      //   455: putfield 71	com/google/android/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:queueSize	I
      //   458: aload_0
      //   459: iload_3
      //   460: putfield 40	com/google/android/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:capacity	I
      //   463: aload_0
      //   464: monitorexit
      //   465: return
      //   466: iconst_0
      //   467: istore 9
      //   469: goto -457 -> 12
      //   472: aload_0
      //   473: aload_0
      //   474: getfield 82	com/google/android/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:relativeWriteIndex	I
      //   477: iconst_1
      //   478: iadd
      //   479: putfield 82	com/google/android/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:relativeWriteIndex	I
      //   482: aload_0
      //   483: getfield 82	com/google/android/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:relativeWriteIndex	I
      //   486: aload_0
      //   487: getfield 40	com/google/android/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:capacity	I
      //   490: if_icmpne -27 -> 463
      //   493: aload_0
      //   494: iconst_0
      //   495: putfield 82	com/google/android/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:relativeWriteIndex	I
      //   498: goto -35 -> 463
      //   501: astore 7
      //   503: aload_0
      //   504: monitorexit
      //   505: aload 7
      //   507: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	508	0	this	InfoQueue
      //   0	508	1	paramLong1	long
      //   0	508	3	paramInt1	int
      //   0	508	4	paramLong2	long
      //   0	508	6	paramInt2	int
      //   0	508	7	paramArrayOfByte	byte[]
      //   295	96	8	i	int
      //   10	458	9	bool	boolean
      //   139	258	10	arrayOfLong1	long[]
      //   144	259	11	arrayOfLong2	long[]
      //   149	260	12	arrayOfInt1	int[]
      //   154	261	13	arrayOfInt2	int[]
      //   160	261	14	arrayOfByte	byte[][]
      //   166	261	15	arrayOfFormat	Format[]
      // Exception table:
      //   from	to	target	type
      //   2	9	501	finally
      //   12	463	501	finally
      //   472	498	501	finally
    }
    
    public void commitSampleTimestamp(long paramLong)
    {
      try
      {
        this.largestQueuedTimestampUs = Math.max(this.largestQueuedTimestampUs, paramLong);
        return;
      }
      finally
      {
        localObject = finally;
        throw ((Throwable)localObject);
      }
    }
    
    public long discardUpstreamSamples(int paramInt)
    {
      paramInt = getWriteIndex() - paramInt;
      if ((paramInt >= 0) && (paramInt <= this.queueSize)) {}
      for (boolean bool = true;; bool = false)
      {
        Assertions.checkArgument(bool);
        if (paramInt != 0) {
          break label82;
        }
        if (this.absoluteReadIndex != 0) {
          break;
        }
        return 0L;
      }
      if (this.relativeWriteIndex == 0) {}
      for (paramInt = this.capacity;; paramInt = this.relativeWriteIndex)
      {
        paramInt -= 1;
        return this.offsets[paramInt] + this.sizes[paramInt];
      }
      label82:
      this.queueSize -= paramInt;
      this.relativeWriteIndex = ((this.relativeWriteIndex + this.capacity - paramInt) % this.capacity);
      this.largestQueuedTimestampUs = Long.MIN_VALUE;
      paramInt = this.queueSize - 1;
      for (;;)
      {
        if (paramInt >= 0)
        {
          int i = (this.relativeReadIndex + paramInt) % this.capacity;
          this.largestQueuedTimestampUs = Math.max(this.largestQueuedTimestampUs, this.timesUs[i]);
          if ((this.flags[i] & 0x1) == 0) {}
        }
        else
        {
          return this.offsets[this.relativeWriteIndex];
        }
        paramInt -= 1;
      }
    }
    
    public boolean format(Format paramFormat)
    {
      boolean bool = false;
      if (paramFormat == null) {}
      for (;;)
      {
        try
        {
          this.upstreamFormatRequired = true;
          return bool;
        }
        finally {}
        this.upstreamFormatRequired = false;
        if (!Util.areEqual(paramFormat, this.upstreamFormat))
        {
          this.upstreamFormat = paramFormat;
          bool = true;
        }
      }
    }
    
    public long getLargestQueuedTimestampUs()
    {
      try
      {
        long l = Math.max(this.largestDequeuedTimestampUs, this.largestQueuedTimestampUs);
        return l;
      }
      finally
      {
        localObject = finally;
        throw ((Throwable)localObject);
      }
    }
    
    public int getReadIndex()
    {
      return this.absoluteReadIndex;
    }
    
    /* Error */
    public Format getUpstreamFormat()
    {
      // Byte code:
      //   0: aload_0
      //   1: monitorenter
      //   2: aload_0
      //   3: getfield 66	com/google/android/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:upstreamFormatRequired	Z
      //   6: istore_1
      //   7: iload_1
      //   8: ifeq +9 -> 17
      //   11: aconst_null
      //   12: astore_2
      //   13: aload_0
      //   14: monitorexit
      //   15: aload_2
      //   16: areturn
      //   17: aload_0
      //   18: getfield 96	com/google/android/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:upstreamFormat	Lcom/google/android/exoplayer2/Format;
      //   21: astore_2
      //   22: goto -9 -> 13
      //   25: astore_2
      //   26: aload_0
      //   27: monitorexit
      //   28: aload_2
      //   29: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	30	0	this	InfoQueue
      //   6	2	1	bool	boolean
      //   12	10	2	localFormat	Format
      //   25	4	2	localObject	Object
      // Exception table:
      //   from	to	target	type
      //   2	7	25	finally
      //   17	22	25	finally
    }
    
    public int getWriteIndex()
    {
      return this.absoluteReadIndex + this.queueSize;
    }
    
    /* Error */
    public boolean isEmpty()
    {
      // Byte code:
      //   0: aload_0
      //   1: monitorenter
      //   2: aload_0
      //   3: getfield 71	com/google/android/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:queueSize	I
      //   6: istore_1
      //   7: iload_1
      //   8: ifne +9 -> 17
      //   11: iconst_1
      //   12: istore_2
      //   13: aload_0
      //   14: monitorexit
      //   15: iload_2
      //   16: ireturn
      //   17: iconst_0
      //   18: istore_2
      //   19: goto -6 -> 13
      //   22: astore_3
      //   23: aload_0
      //   24: monitorexit
      //   25: aload_3
      //   26: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	27	0	this	InfoQueue
      //   6	2	1	i	int
      //   12	7	2	bool	boolean
      //   22	4	3	localObject	Object
      // Exception table:
      //   from	to	target	type
      //   2	7	22	finally
    }
    
    public int peekSourceId()
    {
      if (this.queueSize == 0) {
        return this.upstreamSourceId;
      }
      return this.sourceIds[this.relativeReadIndex];
    }
    
    public int readData(FormatHolder paramFormatHolder, DecoderInputBuffer paramDecoderInputBuffer, Format paramFormat, DefaultTrackOutput.BufferExtrasHolder paramBufferExtrasHolder)
    {
      int i = -5;
      try
      {
        if (this.queueSize == 0) {
          if ((this.upstreamFormat != null) && ((paramDecoderInputBuffer == null) || (this.upstreamFormat != paramFormat))) {
            paramFormatHolder.format = this.upstreamFormat;
          }
        }
        for (;;)
        {
          return i;
          i = -3;
          continue;
          if ((paramDecoderInputBuffer != null) && (this.formats[this.relativeReadIndex] == paramFormat)) {
            break;
          }
          paramFormatHolder.format = this.formats[this.relativeReadIndex];
        }
        paramDecoderInputBuffer.timeUs = this.timesUs[this.relativeReadIndex];
      }
      finally {}
      paramDecoderInputBuffer.setFlags(this.flags[this.relativeReadIndex]);
      paramBufferExtrasHolder.size = this.sizes[this.relativeReadIndex];
      paramBufferExtrasHolder.offset = this.offsets[this.relativeReadIndex];
      paramBufferExtrasHolder.encryptionKeyId = this.encryptionKeys[this.relativeReadIndex];
      this.largestDequeuedTimestampUs = Math.max(this.largestDequeuedTimestampUs, paramDecoderInputBuffer.timeUs);
      this.queueSize -= 1;
      this.relativeReadIndex += 1;
      this.absoluteReadIndex += 1;
      if (this.relativeReadIndex == this.capacity) {
        this.relativeReadIndex = 0;
      }
      if (this.queueSize > 0) {}
      for (long l = this.offsets[this.relativeReadIndex];; l += i)
      {
        paramBufferExtrasHolder.nextOffset = l;
        i = -4;
        break;
        l = paramBufferExtrasHolder.offset;
        i = paramBufferExtrasHolder.size;
      }
    }
    
    public void resetLargestParsedTimestamps()
    {
      this.largestDequeuedTimestampUs = Long.MIN_VALUE;
      this.largestQueuedTimestampUs = Long.MIN_VALUE;
    }
    
    /* Error */
    public long skipToKeyframeBefore(long paramLong, boolean paramBoolean)
    {
      // Byte code:
      //   0: ldc2_w 166
      //   3: lstore 9
      //   5: aload_0
      //   6: monitorenter
      //   7: lload 9
      //   9: lstore 7
      //   11: aload_0
      //   12: getfield 71	com/google/android/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:queueSize	I
      //   15: ifeq +25 -> 40
      //   18: aload_0
      //   19: getfield 46	com/google/android/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:timesUs	[J
      //   22: aload_0
      //   23: getfield 73	com/google/android/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:relativeReadIndex	I
      //   26: laload
      //   27: lstore 7
      //   29: lload_1
      //   30: lload 7
      //   32: lcmp
      //   33: ifge +12 -> 45
      //   36: lload 9
      //   38: lstore 7
      //   40: aload_0
      //   41: monitorexit
      //   42: lload 7
      //   44: lreturn
      //   45: lload_1
      //   46: aload_0
      //   47: getfield 64	com/google/android/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:largestQueuedTimestampUs	J
      //   50: lcmp
      //   51: ifle +11 -> 62
      //   54: lload 9
      //   56: lstore 7
      //   58: iload_3
      //   59: ifeq -19 -> 40
      //   62: iconst_0
      //   63: istore 4
      //   65: iconst_m1
      //   66: istore 6
      //   68: aload_0
      //   69: getfield 73	com/google/android/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:relativeReadIndex	I
      //   72: istore 5
      //   74: iload 5
      //   76: aload_0
      //   77: getfield 82	com/google/android/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:relativeWriteIndex	I
      //   80: if_icmpeq +15 -> 95
      //   83: aload_0
      //   84: getfield 46	com/google/android/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:timesUs	[J
      //   87: iload 5
      //   89: laload
      //   90: lload_1
      //   91: lcmp
      //   92: ifle +65 -> 157
      //   95: lload 9
      //   97: lstore 7
      //   99: iload 6
      //   101: iconst_m1
      //   102: if_icmpeq -62 -> 40
      //   105: aload_0
      //   106: aload_0
      //   107: getfield 71	com/google/android/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:queueSize	I
      //   110: iload 6
      //   112: isub
      //   113: putfield 71	com/google/android/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:queueSize	I
      //   116: aload_0
      //   117: aload_0
      //   118: getfield 73	com/google/android/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:relativeReadIndex	I
      //   121: iload 6
      //   123: iadd
      //   124: aload_0
      //   125: getfield 40	com/google/android/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:capacity	I
      //   128: irem
      //   129: putfield 73	com/google/android/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:relativeReadIndex	I
      //   132: aload_0
      //   133: aload_0
      //   134: getfield 75	com/google/android/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:absoluteReadIndex	I
      //   137: iload 6
      //   139: iadd
      //   140: putfield 75	com/google/android/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:absoluteReadIndex	I
      //   143: aload_0
      //   144: getfield 44	com/google/android/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:offsets	[J
      //   147: aload_0
      //   148: getfield 73	com/google/android/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:relativeReadIndex	I
      //   151: laload
      //   152: lstore 7
      //   154: goto -114 -> 40
      //   157: aload_0
      //   158: getfield 48	com/google/android/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:flags	[I
      //   161: iload 5
      //   163: iaload
      //   164: iconst_1
      //   165: iand
      //   166: ifeq +7 -> 173
      //   169: iload 4
      //   171: istore 6
      //   173: iload 5
      //   175: iconst_1
      //   176: iadd
      //   177: aload_0
      //   178: getfield 40	com/google/android/exoplayer2/extractor/DefaultTrackOutput$InfoQueue:capacity	I
      //   181: irem
      //   182: istore 5
      //   184: iload 4
      //   186: iconst_1
      //   187: iadd
      //   188: istore 4
      //   190: goto -116 -> 74
      //   193: astore 11
      //   195: aload_0
      //   196: monitorexit
      //   197: aload 11
      //   199: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	200	0	this	InfoQueue
      //   0	200	1	paramLong	long
      //   0	200	3	paramBoolean	boolean
      //   63	126	4	i	int
      //   72	111	5	j	int
      //   66	106	6	k	int
      //   9	144	7	l1	long
      //   3	93	9	l2	long
      //   193	5	11	localObject	Object
      // Exception table:
      //   from	to	target	type
      //   11	29	193	finally
      //   45	54	193	finally
      //   68	74	193	finally
      //   74	95	193	finally
      //   105	154	193	finally
      //   157	169	193	finally
      //   173	184	193	finally
    }
    
    public void sourceId(int paramInt)
    {
      this.upstreamSourceId = paramInt;
    }
  }
  
  public static abstract interface UpstreamFormatChangedListener
  {
    public abstract void onUpstreamFormatChanged(Format paramFormat);
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/extractor/DefaultTrackOutput.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */