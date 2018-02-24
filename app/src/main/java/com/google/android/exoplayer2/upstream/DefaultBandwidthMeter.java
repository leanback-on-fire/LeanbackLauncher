package com.google.android.exoplayer2.upstream;

import android.os.Handler;
import android.os.SystemClock;
import com.google.android.exoplayer2.util.SlidingPercentile;

public final class DefaultBandwidthMeter
  implements BandwidthMeter, TransferListener<Object>
{
  private static final int BYTES_TRANSFERRED_FOR_ESTIMATE = 524288;
  public static final int DEFAULT_MAX_WEIGHT = 2000;
  private static final int ELAPSED_MILLIS_FOR_ESTIMATE = 2000;
  private long bitrateEstimate;
  private final Handler eventHandler;
  private final BandwidthMeter.EventListener eventListener;
  private long sampleBytesTransferred;
  private long sampleStartTimeMs;
  private final SlidingPercentile slidingPercentile;
  private int streamCount;
  private long totalBytesTransferred;
  private long totalElapsedTimeMs;
  
  public DefaultBandwidthMeter()
  {
    this(null, null);
  }
  
  public DefaultBandwidthMeter(Handler paramHandler, BandwidthMeter.EventListener paramEventListener)
  {
    this(paramHandler, paramEventListener, 2000);
  }
  
  public DefaultBandwidthMeter(Handler paramHandler, BandwidthMeter.EventListener paramEventListener, int paramInt)
  {
    this.eventHandler = paramHandler;
    this.eventListener = paramEventListener;
    this.slidingPercentile = new SlidingPercentile(paramInt);
    this.bitrateEstimate = -1L;
  }
  
  private void notifyBandwidthSample(final int paramInt, final long paramLong1, long paramLong2)
  {
    if ((this.eventHandler != null) && (this.eventListener != null)) {
      this.eventHandler.post(new Runnable()
      {
        public void run()
        {
          DefaultBandwidthMeter.this.eventListener.onBandwidthSample(paramInt, paramLong1, this.val$bitrate);
        }
      });
    }
  }
  
  public long getBitrateEstimate()
  {
    try
    {
      long l = this.bitrateEstimate;
      return l;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public void onBytesTransferred(Object paramObject, int paramInt)
  {
    try
    {
      this.sampleBytesTransferred += paramInt;
      return;
    }
    finally
    {
      paramObject = finally;
      throw ((Throwable)paramObject);
    }
  }
  
  /* Error */
  public void onTransferEnd(Object paramObject)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 79	com/google/android/exoplayer2/upstream/DefaultBandwidthMeter:streamCount	I
    //   6: ifle +176 -> 182
    //   9: iconst_1
    //   10: istore 4
    //   12: iload 4
    //   14: invokestatic 85	com/google/android/exoplayer2/util/Assertions:checkState	(Z)V
    //   17: invokestatic 90	android/os/SystemClock:elapsedRealtime	()J
    //   20: lstore 7
    //   22: lload 7
    //   24: aload_0
    //   25: getfield 92	com/google/android/exoplayer2/upstream/DefaultBandwidthMeter:sampleStartTimeMs	J
    //   28: lsub
    //   29: l2i
    //   30: istore_3
    //   31: aload_0
    //   32: aload_0
    //   33: getfield 94	com/google/android/exoplayer2/upstream/DefaultBandwidthMeter:totalElapsedTimeMs	J
    //   36: iload_3
    //   37: i2l
    //   38: ladd
    //   39: putfield 94	com/google/android/exoplayer2/upstream/DefaultBandwidthMeter:totalElapsedTimeMs	J
    //   42: aload_0
    //   43: aload_0
    //   44: getfield 96	com/google/android/exoplayer2/upstream/DefaultBandwidthMeter:totalBytesTransferred	J
    //   47: aload_0
    //   48: getfield 75	com/google/android/exoplayer2/upstream/DefaultBandwidthMeter:sampleBytesTransferred	J
    //   51: ladd
    //   52: putfield 96	com/google/android/exoplayer2/upstream/DefaultBandwidthMeter:totalBytesTransferred	J
    //   55: iload_3
    //   56: ifle +83 -> 139
    //   59: aload_0
    //   60: getfield 75	com/google/android/exoplayer2/upstream/DefaultBandwidthMeter:sampleBytesTransferred	J
    //   63: ldc2_w 97
    //   66: lmul
    //   67: iload_3
    //   68: i2l
    //   69: ldiv
    //   70: l2f
    //   71: fstore_2
    //   72: aload_0
    //   73: getfield 52	com/google/android/exoplayer2/upstream/DefaultBandwidthMeter:slidingPercentile	Lcom/google/android/exoplayer2/util/SlidingPercentile;
    //   76: aload_0
    //   77: getfield 75	com/google/android/exoplayer2/upstream/DefaultBandwidthMeter:sampleBytesTransferred	J
    //   80: l2d
    //   81: invokestatic 104	java/lang/Math:sqrt	(D)D
    //   84: d2i
    //   85: fload_2
    //   86: invokevirtual 108	com/google/android/exoplayer2/util/SlidingPercentile:addSample	(IF)V
    //   89: aload_0
    //   90: getfield 94	com/google/android/exoplayer2/upstream/DefaultBandwidthMeter:totalElapsedTimeMs	J
    //   93: ldc2_w 109
    //   96: lcmp
    //   97: ifge +14 -> 111
    //   100: aload_0
    //   101: getfield 96	com/google/android/exoplayer2/upstream/DefaultBandwidthMeter:totalBytesTransferred	J
    //   104: ldc2_w 111
    //   107: lcmp
    //   108: iflt +31 -> 139
    //   111: aload_0
    //   112: getfield 52	com/google/android/exoplayer2/upstream/DefaultBandwidthMeter:slidingPercentile	Lcom/google/android/exoplayer2/util/SlidingPercentile;
    //   115: ldc 113
    //   117: invokevirtual 117	com/google/android/exoplayer2/util/SlidingPercentile:getPercentile	(F)F
    //   120: fstore_2
    //   121: fload_2
    //   122: invokestatic 123	java/lang/Float:isNaN	(F)Z
    //   125: ifeq +63 -> 188
    //   128: ldc2_w 53
    //   131: lstore 5
    //   133: aload_0
    //   134: lload 5
    //   136: putfield 56	com/google/android/exoplayer2/upstream/DefaultBandwidthMeter:bitrateEstimate	J
    //   139: aload_0
    //   140: iload_3
    //   141: aload_0
    //   142: getfield 75	com/google/android/exoplayer2/upstream/DefaultBandwidthMeter:sampleBytesTransferred	J
    //   145: aload_0
    //   146: getfield 56	com/google/android/exoplayer2/upstream/DefaultBandwidthMeter:bitrateEstimate	J
    //   149: invokespecial 125	com/google/android/exoplayer2/upstream/DefaultBandwidthMeter:notifyBandwidthSample	(IJJ)V
    //   152: aload_0
    //   153: getfield 79	com/google/android/exoplayer2/upstream/DefaultBandwidthMeter:streamCount	I
    //   156: iconst_1
    //   157: isub
    //   158: istore_3
    //   159: aload_0
    //   160: iload_3
    //   161: putfield 79	com/google/android/exoplayer2/upstream/DefaultBandwidthMeter:streamCount	I
    //   164: iload_3
    //   165: ifle +9 -> 174
    //   168: aload_0
    //   169: lload 7
    //   171: putfield 92	com/google/android/exoplayer2/upstream/DefaultBandwidthMeter:sampleStartTimeMs	J
    //   174: aload_0
    //   175: lconst_0
    //   176: putfield 75	com/google/android/exoplayer2/upstream/DefaultBandwidthMeter:sampleBytesTransferred	J
    //   179: aload_0
    //   180: monitorexit
    //   181: return
    //   182: iconst_0
    //   183: istore 4
    //   185: goto -173 -> 12
    //   188: fload_2
    //   189: f2l
    //   190: lstore 5
    //   192: goto -59 -> 133
    //   195: astore_1
    //   196: aload_0
    //   197: monitorexit
    //   198: aload_1
    //   199: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	200	0	this	DefaultBandwidthMeter
    //   0	200	1	paramObject	Object
    //   71	118	2	f	float
    //   30	135	3	i	int
    //   10	174	4	bool	boolean
    //   131	60	5	l1	long
    //   20	150	7	l2	long
    // Exception table:
    //   from	to	target	type
    //   2	9	195	finally
    //   12	55	195	finally
    //   59	111	195	finally
    //   111	128	195	finally
    //   133	139	195	finally
    //   139	164	195	finally
    //   168	174	195	finally
    //   174	179	195	finally
  }
  
  public void onTransferStart(Object paramObject, DataSpec paramDataSpec)
  {
    try
    {
      if (this.streamCount == 0) {
        this.sampleStartTimeMs = SystemClock.elapsedRealtime();
      }
      this.streamCount += 1;
      return;
    }
    finally {}
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/upstream/DefaultBandwidthMeter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */