package com.google.android.exoplayer2.source;

import android.util.Pair;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.Timeline.Period;
import com.google.android.exoplayer2.Timeline.Window;
import com.google.android.exoplayer2.upstream.Allocator;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

public final class ConcatenatingMediaSource
  implements MediaSource
{
  private final boolean[] duplicateFlags;
  private MediaSource.Listener listener;
  private final Object[] manifests;
  private final MediaSource[] mediaSources;
  private final Map<MediaPeriod, Integer> sourceIndexByMediaPeriod;
  private ConcatenatedTimeline timeline;
  private final Timeline[] timelines;
  
  public ConcatenatingMediaSource(MediaSource... paramVarArgs)
  {
    this.mediaSources = paramVarArgs;
    this.timelines = new Timeline[paramVarArgs.length];
    this.manifests = new Object[paramVarArgs.length];
    this.sourceIndexByMediaPeriod = new HashMap();
    this.duplicateFlags = buildDuplicateFlags(paramVarArgs);
  }
  
  private static boolean[] buildDuplicateFlags(MediaSource[] paramArrayOfMediaSource)
  {
    boolean[] arrayOfBoolean = new boolean[paramArrayOfMediaSource.length];
    IdentityHashMap localIdentityHashMap = new IdentityHashMap(paramArrayOfMediaSource.length);
    int i = 0;
    if (i < paramArrayOfMediaSource.length)
    {
      MediaSource localMediaSource = paramArrayOfMediaSource[i];
      if (!localIdentityHashMap.containsKey(localMediaSource)) {
        localIdentityHashMap.put(localMediaSource, null);
      }
      for (;;)
      {
        i += 1;
        break;
        arrayOfBoolean[i] = true;
      }
    }
    return arrayOfBoolean;
  }
  
  private void handleSourceInfoRefreshed(int paramInt, Timeline paramTimeline, Object paramObject)
  {
    this.timelines[paramInt] = paramTimeline;
    this.manifests[paramInt] = paramObject;
    int i = paramInt + 1;
    while (i < this.mediaSources.length)
    {
      if (this.mediaSources[i] == this.mediaSources[paramInt])
      {
        this.timelines[i] = paramTimeline;
        this.manifests[i] = paramObject;
      }
      i += 1;
    }
    paramTimeline = this.timelines;
    i = paramTimeline.length;
    paramInt = 0;
    while (paramInt < i)
    {
      if (paramTimeline[paramInt] == null) {
        return;
      }
      paramInt += 1;
    }
    this.timeline = new ConcatenatedTimeline((Timeline[])this.timelines.clone());
    this.listener.onSourceInfoRefreshed(this.timeline, this.manifests.clone());
  }
  
  public MediaPeriod createPeriod(int paramInt, Allocator paramAllocator, long paramLong)
  {
    int i = this.timeline.getSourceIndexForPeriod(paramInt);
    int j = this.timeline.getFirstPeriodIndexInSource(i);
    paramAllocator = this.mediaSources[i].createPeriod(paramInt - j, paramAllocator, paramLong);
    this.sourceIndexByMediaPeriod.put(paramAllocator, Integer.valueOf(i));
    return paramAllocator;
  }
  
  public void maybeThrowSourceInfoRefreshError()
    throws IOException
  {
    int i = 0;
    while (i < this.mediaSources.length)
    {
      if (this.duplicateFlags[i] == 0) {
        this.mediaSources[i].maybeThrowSourceInfoRefreshError();
      }
      i += 1;
    }
  }
  
  public void prepareSource(ExoPlayer paramExoPlayer, boolean paramBoolean, MediaSource.Listener paramListener)
  {
    this.listener = paramListener;
    final int i = 0;
    while (i < this.mediaSources.length)
    {
      if (this.duplicateFlags[i] == 0) {
        this.mediaSources[i].prepareSource(paramExoPlayer, false, new MediaSource.Listener()
        {
          public void onSourceInfoRefreshed(Timeline paramAnonymousTimeline, Object paramAnonymousObject)
          {
            ConcatenatingMediaSource.this.handleSourceInfoRefreshed(i, paramAnonymousTimeline, paramAnonymousObject);
          }
        });
      }
      i += 1;
    }
  }
  
  public void releasePeriod(MediaPeriod paramMediaPeriod)
  {
    int i = ((Integer)this.sourceIndexByMediaPeriod.get(paramMediaPeriod)).intValue();
    this.sourceIndexByMediaPeriod.remove(paramMediaPeriod);
    this.mediaSources[i].releasePeriod(paramMediaPeriod);
  }
  
  public void releaseSource()
  {
    int i = 0;
    while (i < this.mediaSources.length)
    {
      if (this.duplicateFlags[i] == 0) {
        this.mediaSources[i].releaseSource();
      }
      i += 1;
    }
  }
  
  private static final class ConcatenatedTimeline
    extends Timeline
  {
    private final int[] sourcePeriodOffsets;
    private final int[] sourceWindowOffsets;
    private final Timeline[] timelines;
    
    public ConcatenatedTimeline(Timeline[] paramArrayOfTimeline)
    {
      int[] arrayOfInt1 = new int[paramArrayOfTimeline.length];
      int[] arrayOfInt2 = new int[paramArrayOfTimeline.length];
      int k = 0;
      int i = 0;
      int j = 0;
      while (j < paramArrayOfTimeline.length)
      {
        Timeline localTimeline = paramArrayOfTimeline[j];
        k += localTimeline.getPeriodCount();
        arrayOfInt1[j] = k;
        i += localTimeline.getWindowCount();
        arrayOfInt2[j] = i;
        j += 1;
      }
      this.timelines = paramArrayOfTimeline;
      this.sourcePeriodOffsets = arrayOfInt1;
      this.sourceWindowOffsets = arrayOfInt2;
    }
    
    private int getFirstPeriodIndexInSource(int paramInt)
    {
      if (paramInt == 0) {
        return 0;
      }
      return this.sourcePeriodOffsets[(paramInt - 1)];
    }
    
    private int getFirstWindowIndexInSource(int paramInt)
    {
      if (paramInt == 0) {
        return 0;
      }
      return this.sourceWindowOffsets[(paramInt - 1)];
    }
    
    private int getSourceIndexForPeriod(int paramInt)
    {
      return Util.binarySearchFloor(this.sourcePeriodOffsets, paramInt, true, false) + 1;
    }
    
    private int getSourceIndexForWindow(int paramInt)
    {
      return Util.binarySearchFloor(this.sourceWindowOffsets, paramInt, true, false) + 1;
    }
    
    public int getIndexOfPeriod(Object paramObject)
    {
      if (!(paramObject instanceof Pair)) {}
      do
      {
        do
        {
          return -1;
          paramObject = (Pair)paramObject;
        } while (!(((Pair)paramObject).first instanceof Integer));
        i = ((Integer)((Pair)paramObject).first).intValue();
        paramObject = ((Pair)paramObject).second;
      } while ((i < 0) || (i >= this.timelines.length));
      int j = this.timelines[i].getIndexOfPeriod(paramObject);
      if (j == -1) {}
      for (int i = -1;; i = getFirstPeriodIndexInSource(i) + j) {
        return i;
      }
    }
    
    public Timeline.Period getPeriod(int paramInt, Timeline.Period paramPeriod, boolean paramBoolean)
    {
      int i = getSourceIndexForPeriod(paramInt);
      int j = getFirstWindowIndexInSource(i);
      int k = getFirstPeriodIndexInSource(i);
      this.timelines[i].getPeriod(paramInt - k, paramPeriod, paramBoolean);
      paramPeriod.windowIndex += j;
      if (paramBoolean) {
        paramPeriod.uid = Pair.create(Integer.valueOf(i), paramPeriod.uid);
      }
      return paramPeriod;
    }
    
    public int getPeriodCount()
    {
      return this.sourcePeriodOffsets[(this.sourcePeriodOffsets.length - 1)];
    }
    
    public Timeline.Window getWindow(int paramInt, Timeline.Window paramWindow, boolean paramBoolean, long paramLong)
    {
      int i = getSourceIndexForWindow(paramInt);
      int j = getFirstWindowIndexInSource(i);
      int k = getFirstPeriodIndexInSource(i);
      this.timelines[i].getWindow(paramInt - j, paramWindow, paramBoolean, paramLong);
      paramWindow.firstPeriodIndex += k;
      paramWindow.lastPeriodIndex += k;
      return paramWindow;
    }
    
    public int getWindowCount()
    {
      return this.sourceWindowOffsets[(this.sourceWindowOffsets.length - 1)];
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/source/ConcatenatingMediaSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */