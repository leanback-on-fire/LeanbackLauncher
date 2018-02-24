package com.google.android.exoplayer2.trackselection;

import android.os.SystemClock;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.chunk.MediaChunk;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import java.util.List;

public class AdaptiveVideoTrackSelection
  extends BaseTrackSelection
{
  public static final float DEFAULT_BANDWIDTH_FRACTION = 0.75F;
  public static final int DEFAULT_MAX_DURATION_FOR_QUALITY_DECREASE_MS = 25000;
  public static final int DEFAULT_MAX_INITIAL_BITRATE = 800000;
  public static final int DEFAULT_MIN_DURATION_FOR_QUALITY_INCREASE_MS = 10000;
  public static final int DEFAULT_MIN_DURATION_TO_RETAIN_AFTER_DISCARD_MS = 25000;
  private final float bandwidthFraction;
  private final BandwidthMeter bandwidthMeter;
  private final long maxDurationForQualityDecreaseUs;
  private final int maxInitialBitrate;
  private final long minDurationForQualityIncreaseUs;
  private final long minDurationToRetainAfterDiscardUs;
  private int reason;
  private int selectedIndex;
  
  public AdaptiveVideoTrackSelection(TrackGroup paramTrackGroup, int[] paramArrayOfInt, BandwidthMeter paramBandwidthMeter)
  {
    this(paramTrackGroup, paramArrayOfInt, paramBandwidthMeter, 800000, 10000L, 25000L, 25000L, 0.75F);
  }
  
  public AdaptiveVideoTrackSelection(TrackGroup paramTrackGroup, int[] paramArrayOfInt, BandwidthMeter paramBandwidthMeter, int paramInt, long paramLong1, long paramLong2, long paramLong3, float paramFloat)
  {
    super(paramTrackGroup, paramArrayOfInt);
    this.bandwidthMeter = paramBandwidthMeter;
    this.maxInitialBitrate = paramInt;
    this.minDurationForQualityIncreaseUs = (paramLong1 * 1000L);
    this.maxDurationForQualityDecreaseUs = (paramLong2 * 1000L);
    this.minDurationToRetainAfterDiscardUs = (paramLong3 * 1000L);
    this.bandwidthFraction = paramFloat;
    this.selectedIndex = determineIdealSelectedIndex(Long.MIN_VALUE);
    this.reason = 1;
  }
  
  private int determineIdealSelectedIndex(long paramLong)
  {
    long l = this.bandwidthMeter.getBitrateEstimate();
    int j;
    int i;
    if (l == -1L)
    {
      l = this.maxInitialBitrate;
      j = 0;
      i = 0;
    }
    for (;;)
    {
      if (i >= this.length) {
        break label98;
      }
      if ((paramLong == Long.MIN_VALUE) || (!isBlacklisted(i, paramLong)))
      {
        if (getFormat(i).bitrate <= l)
        {
          return i;
          l = ((float)l * this.bandwidthFraction);
          break;
        }
        j = i;
      }
      i += 1;
    }
    label98:
    return j;
  }
  
  public int evaluateQueueSize(long paramLong, List<? extends MediaChunk> paramList)
  {
    int i;
    if (paramList.isEmpty()) {
      i = 0;
    }
    int k;
    do
    {
      return i;
      k = paramList.size();
      i = k;
    } while (((MediaChunk)paramList.get(k - 1)).endTimeUs - paramLong < this.minDurationToRetainAfterDiscardUs);
    Format localFormat = getFormat(determineIdealSelectedIndex(SystemClock.elapsedRealtime()));
    int j = 0;
    for (;;)
    {
      i = k;
      if (j >= k) {
        break;
      }
      MediaChunk localMediaChunk = (MediaChunk)paramList.get(j);
      if ((localMediaChunk.startTimeUs - paramLong >= this.minDurationToRetainAfterDiscardUs) && (localMediaChunk.trackFormat.bitrate < localFormat.bitrate) && (localMediaChunk.trackFormat.height < localFormat.height) && (localMediaChunk.trackFormat.height < 720) && (localMediaChunk.trackFormat.width < 1280)) {
        return j;
      }
      j += 1;
    }
  }
  
  public int getSelectedIndex()
  {
    return this.selectedIndex;
  }
  
  public Object getSelectionData()
  {
    return null;
  }
  
  public int getSelectionReason()
  {
    return this.reason;
  }
  
  public void updateSelectedTrack(long paramLong)
  {
    long l = SystemClock.elapsedRealtime();
    int i = this.selectedIndex;
    Format localFormat1 = getSelectedFormat();
    int j = determineIdealSelectedIndex(l);
    Format localFormat2 = getFormat(j);
    this.selectedIndex = j;
    if ((localFormat1 != null) && (!isBlacklisted(this.selectedIndex, l))) {
      if ((localFormat2.bitrate <= localFormat1.bitrate) || (paramLong >= this.minDurationForQualityIncreaseUs)) {
        break label97;
      }
    }
    for (this.selectedIndex = i;; this.selectedIndex = i) {
      label97:
      do
      {
        if (this.selectedIndex != i) {
          this.reason = 3;
        }
        return;
      } while ((localFormat2.bitrate >= localFormat1.bitrate) || (paramLong < this.maxDurationForQualityDecreaseUs));
    }
  }
  
  public static final class Factory
    implements TrackSelection.Factory
  {
    private final float bandwidthFraction;
    private final BandwidthMeter bandwidthMeter;
    private final int maxDurationForQualityDecreaseMs;
    private final int maxInitialBitrate;
    private final int minDurationForQualityIncreaseMs;
    private final int minDurationToRetainAfterDiscardMs;
    
    public Factory(BandwidthMeter paramBandwidthMeter)
    {
      this(paramBandwidthMeter, 800000, 10000, 25000, 25000, 0.75F);
    }
    
    public Factory(BandwidthMeter paramBandwidthMeter, int paramInt1, int paramInt2, int paramInt3, int paramInt4, float paramFloat)
    {
      this.bandwidthMeter = paramBandwidthMeter;
      this.maxInitialBitrate = paramInt1;
      this.minDurationForQualityIncreaseMs = paramInt2;
      this.maxDurationForQualityDecreaseMs = paramInt3;
      this.minDurationToRetainAfterDiscardMs = paramInt4;
      this.bandwidthFraction = paramFloat;
    }
    
    public AdaptiveVideoTrackSelection createTrackSelection(TrackGroup paramTrackGroup, int... paramVarArgs)
    {
      return new AdaptiveVideoTrackSelection(paramTrackGroup, paramVarArgs, this.bandwidthMeter, this.maxInitialBitrate, this.minDurationForQualityIncreaseMs, this.maxDurationForQualityDecreaseMs, this.minDurationToRetainAfterDiscardMs, this.bandwidthFraction);
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/trackselection/AdaptiveVideoTrackSelection.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */