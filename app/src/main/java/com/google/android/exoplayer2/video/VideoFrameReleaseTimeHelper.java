package com.google.android.exoplayer2.video;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Message;
import android.view.Choreographer;
import android.view.Choreographer.FrameCallback;
import android.view.Display;
import android.view.WindowManager;

@TargetApi(16)
public final class VideoFrameReleaseTimeHelper
{
  private static final long CHOREOGRAPHER_SAMPLE_DELAY_MILLIS = 500L;
  private static final long MAX_ALLOWED_DRIFT_NS = 20000000L;
  private static final int MIN_FRAMES_FOR_ADJUSTMENT = 6;
  private static final long VSYNC_OFFSET_PERCENTAGE = 80L;
  private long adjustedLastFrameTimeNs;
  private long frameCount;
  private boolean haveSync;
  private long lastFramePresentationTimeUs;
  private long pendingAdjustedFrameTimeNs;
  private long syncFramePresentationTimeNs;
  private long syncUnadjustedReleaseTimeNs;
  private final boolean useDefaultDisplayVsync;
  private final long vsyncDurationNs;
  private final long vsyncOffsetNs;
  private final VSyncSampler vsyncSampler;
  
  public VideoFrameReleaseTimeHelper()
  {
    this(-1.0D, false);
  }
  
  private VideoFrameReleaseTimeHelper(double paramDouble, boolean paramBoolean)
  {
    this.useDefaultDisplayVsync = paramBoolean;
    if (paramBoolean)
    {
      this.vsyncSampler = VSyncSampler.getInstance();
      this.vsyncDurationNs = ((1.0E9D / paramDouble));
      this.vsyncOffsetNs = (this.vsyncDurationNs * 80L / 100L);
      return;
    }
    this.vsyncSampler = null;
    this.vsyncDurationNs = -1L;
    this.vsyncOffsetNs = -1L;
  }
  
  public VideoFrameReleaseTimeHelper(Context paramContext)
  {
    this(getDefaultDisplayRefreshRate(paramContext), true);
  }
  
  private static long closestVsync(long paramLong1, long paramLong2, long paramLong3)
  {
    long l = paramLong2 + paramLong3 * ((paramLong1 - paramLong2) / paramLong3);
    if (paramLong1 <= l) {
      paramLong2 = l - paramLong3;
    }
    while (l - paramLong1 < paramLong1 - paramLong2)
    {
      return l;
      paramLong2 = l;
      l += paramLong3;
    }
    return paramLong2;
  }
  
  private static float getDefaultDisplayRefreshRate(Context paramContext)
  {
    return ((WindowManager)paramContext.getSystemService("window")).getDefaultDisplay().getRefreshRate();
  }
  
  private boolean isDriftTooLarge(long paramLong1, long paramLong2)
  {
    long l = this.syncFramePresentationTimeNs;
    return Math.abs(paramLong2 - this.syncUnadjustedReleaseTimeNs - (paramLong1 - l)) > 20000000L;
  }
  
  public long adjustReleaseTime(long paramLong1, long paramLong2)
  {
    long l5 = paramLong1 * 1000L;
    long l1 = l5;
    long l2 = paramLong2;
    long l3 = l2;
    long l4 = l1;
    if (this.haveSync)
    {
      if (paramLong1 != this.lastFramePresentationTimeUs)
      {
        this.frameCount += 1L;
        this.adjustedLastFrameTimeNs = this.pendingAdjustedFrameTimeNs;
      }
      if (this.frameCount < 6L) {
        break label195;
      }
      l3 = (l5 - this.syncFramePresentationTimeNs) / this.frameCount;
      l4 = this.adjustedLastFrameTimeNs + l3;
      if (!isDriftTooLarge(l4, paramLong2)) {
        break label178;
      }
      this.haveSync = false;
      l4 = l1;
      l3 = l2;
    }
    for (;;)
    {
      if (!this.haveSync)
      {
        this.syncFramePresentationTimeNs = l5;
        this.syncUnadjustedReleaseTimeNs = paramLong2;
        this.frameCount = 0L;
        this.haveSync = true;
        onSynced();
      }
      this.lastFramePresentationTimeUs = paramLong1;
      this.pendingAdjustedFrameTimeNs = l4;
      if ((this.vsyncSampler != null) && (this.vsyncSampler.sampledVsyncTimeNs != 0L)) {
        break;
      }
      return l3;
      label178:
      l3 = this.syncUnadjustedReleaseTimeNs + l4 - this.syncFramePresentationTimeNs;
      continue;
      label195:
      l3 = l2;
      l4 = l1;
      if (isDriftTooLarge(l5, paramLong2))
      {
        this.haveSync = false;
        l3 = l2;
        l4 = l1;
      }
    }
    return closestVsync(l3, this.vsyncSampler.sampledVsyncTimeNs, this.vsyncDurationNs) - this.vsyncOffsetNs;
  }
  
  public void disable()
  {
    if (this.useDefaultDisplayVsync) {
      this.vsyncSampler.removeObserver();
    }
  }
  
  public void enable()
  {
    this.haveSync = false;
    if (this.useDefaultDisplayVsync) {
      this.vsyncSampler.addObserver();
    }
  }
  
  protected void onSynced() {}
  
  private static final class VSyncSampler
    implements Choreographer.FrameCallback, Handler.Callback
  {
    private static final int CREATE_CHOREOGRAPHER = 0;
    private static final VSyncSampler INSTANCE = new VSyncSampler();
    private static final int MSG_ADD_OBSERVER = 1;
    private static final int MSG_REMOVE_OBSERVER = 2;
    private Choreographer choreographer;
    private final HandlerThread choreographerOwnerThread = new HandlerThread("ChoreographerOwner:Handler");
    private final Handler handler;
    private int observerCount;
    public volatile long sampledVsyncTimeNs;
    
    private VSyncSampler()
    {
      this.choreographerOwnerThread.start();
      this.handler = new Handler(this.choreographerOwnerThread.getLooper(), this);
      this.handler.sendEmptyMessage(0);
    }
    
    private void addObserverInternal()
    {
      this.observerCount += 1;
      if (this.observerCount == 1) {
        this.choreographer.postFrameCallback(this);
      }
    }
    
    private void createChoreographerInstanceInternal()
    {
      this.choreographer = Choreographer.getInstance();
    }
    
    public static VSyncSampler getInstance()
    {
      return INSTANCE;
    }
    
    private void removeObserverInternal()
    {
      this.observerCount -= 1;
      if (this.observerCount == 0)
      {
        this.choreographer.removeFrameCallback(this);
        this.sampledVsyncTimeNs = 0L;
      }
    }
    
    public void addObserver()
    {
      this.handler.sendEmptyMessage(1);
    }
    
    public void doFrame(long paramLong)
    {
      this.sampledVsyncTimeNs = paramLong;
      this.choreographer.postFrameCallbackDelayed(this, 500L);
    }
    
    public boolean handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      default: 
        return false;
      case 0: 
        createChoreographerInstanceInternal();
        return true;
      case 1: 
        addObserverInternal();
        return true;
      }
      removeObserverInternal();
      return true;
    }
    
    public void removeObserver()
    {
      this.handler.sendEmptyMessage(2);
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/video/VideoFrameReleaseTimeHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */