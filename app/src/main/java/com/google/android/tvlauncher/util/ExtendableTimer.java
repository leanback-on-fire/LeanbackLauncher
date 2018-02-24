package com.google.android.tvlauncher.util;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class ExtendableTimer
{
  private static final boolean DEBUG = false;
  private static final int MAXIMUM_TIMEOUT_MSG = 2;
  private static final int MAX_POOL_SIZE = 10;
  private static final Object POOL_SYNC = new Object();
  private static final String TAG = "ExtendableTimer";
  private static final int TIMEOUT_MSG = 1;
  private static ExtendableTimer sPool;
  private static int sPoolSize = 0;
  private final InternalHandler mHandler = new InternalHandler();
  private long mId;
  private Listener mListener;
  private long mMaximumTimeoutMillis;
  private ExtendableTimer mNext;
  private boolean mStarted;
  private long mTimeoutMillis;
  
  private void checkTimeouts()
  {
    if ((this.mTimeoutMillis <= 0L) || (this.mMaximumTimeoutMillis <= 0L)) {
      throw new IllegalArgumentException("Both timeout and maximum timeout must be provided");
    }
    if (this.mMaximumTimeoutMillis <= this.mTimeoutMillis) {
      throw new IllegalArgumentException("Maximum timeout must be larger than timeout");
    }
  }
  
  private void fireTimer()
  {
    stopTimers();
    if (this.mListener != null) {
      this.mListener.onTimerFired(this);
    }
  }
  
  public static ExtendableTimer obtain()
  {
    synchronized (POOL_SYNC)
    {
      if (sPool != null)
      {
        ExtendableTimer localExtendableTimer = sPool;
        sPool = localExtendableTimer.mNext;
        localExtendableTimer.mNext = null;
        sPoolSize -= 1;
        return localExtendableTimer;
      }
      return new ExtendableTimer();
    }
  }
  
  private void resetFields()
  {
    this.mTimeoutMillis = 0L;
    this.mMaximumTimeoutMillis = 0L;
    this.mListener = null;
    this.mStarted = false;
  }
  
  private void stopTimers()
  {
    if (!this.mStarted) {
      return;
    }
    this.mHandler.removeMessages(1);
    this.mHandler.removeMessages(2);
    this.mStarted = false;
  }
  
  public void cancel()
  {
    stopTimers();
  }
  
  public long getId()
  {
    return this.mId;
  }
  
  public boolean isStarted()
  {
    return this.mStarted;
  }
  
  public void recycle()
  {
    synchronized (POOL_SYNC)
    {
      if ((sPoolSize < 10) && (sPool != this) && (this.mNext == null))
      {
        stopTimers();
        resetFields();
        this.mNext = sPool;
        sPool = this;
        sPoolSize += 1;
      }
      return;
    }
  }
  
  public void setId(long paramLong)
  {
    this.mId = paramLong;
  }
  
  public void setListener(Listener paramListener)
  {
    this.mListener = paramListener;
  }
  
  public void setMaximumTimeout(long paramLong)
  {
    this.mMaximumTimeoutMillis = paramLong;
  }
  
  public void setTimeout(long paramLong)
  {
    this.mTimeoutMillis = paramLong;
  }
  
  public void start()
  {
    checkTimeouts();
    if (this.mStarted)
    {
      this.mHandler.removeMessages(1);
      this.mHandler.sendEmptyMessageDelayed(1, this.mTimeoutMillis);
      return;
    }
    this.mStarted = true;
    this.mHandler.sendEmptyMessageDelayed(1, this.mTimeoutMillis);
    this.mHandler.sendEmptyMessageDelayed(2, this.mMaximumTimeoutMillis);
  }
  
  @SuppressLint({"HandlerLeak"})
  private class InternalHandler
    extends Handler
  {
    InternalHandler()
    {
      super();
    }
    
    public void handleMessage(Message paramMessage)
    {
      ExtendableTimer.this.fireTimer();
    }
  }
  
  public static abstract interface Listener
  {
    public abstract void onTimerFired(ExtendableTimer paramExtendableTimer);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/util/ExtendableTimer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */