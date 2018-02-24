package com.google.android.exoplayer2;

public abstract class Timeline
{
  public static final Timeline EMPTY = new Timeline()
  {
    public int getIndexOfPeriod(Object paramAnonymousObject)
    {
      return -1;
    }
    
    public Timeline.Period getPeriod(int paramAnonymousInt, Timeline.Period paramAnonymousPeriod, boolean paramAnonymousBoolean)
    {
      throw new IndexOutOfBoundsException();
    }
    
    public int getPeriodCount()
    {
      return 0;
    }
    
    public Timeline.Window getWindow(int paramAnonymousInt, Timeline.Window paramAnonymousWindow, boolean paramAnonymousBoolean, long paramAnonymousLong)
    {
      throw new IndexOutOfBoundsException();
    }
    
    public int getWindowCount()
    {
      return 0;
    }
  };
  
  public abstract int getIndexOfPeriod(Object paramObject);
  
  public final Period getPeriod(int paramInt, Period paramPeriod)
  {
    return getPeriod(paramInt, paramPeriod, false);
  }
  
  public abstract Period getPeriod(int paramInt, Period paramPeriod, boolean paramBoolean);
  
  public abstract int getPeriodCount();
  
  public final Window getWindow(int paramInt, Window paramWindow)
  {
    return getWindow(paramInt, paramWindow, false);
  }
  
  public Window getWindow(int paramInt, Window paramWindow, boolean paramBoolean)
  {
    return getWindow(paramInt, paramWindow, paramBoolean, 0L);
  }
  
  public abstract Window getWindow(int paramInt, Window paramWindow, boolean paramBoolean, long paramLong);
  
  public abstract int getWindowCount();
  
  public final boolean isEmpty()
  {
    return getWindowCount() == 0;
  }
  
  public static final class Period
  {
    public long durationUs;
    public Object id;
    private long positionInWindowUs;
    public Object uid;
    public int windowIndex;
    
    public long getDurationMs()
    {
      return C.usToMs(this.durationUs);
    }
    
    public long getDurationUs()
    {
      return this.durationUs;
    }
    
    public long getPositionInWindowMs()
    {
      return C.usToMs(this.positionInWindowUs);
    }
    
    public long getPositionInWindowUs()
    {
      return this.positionInWindowUs;
    }
    
    public Period set(Object paramObject1, Object paramObject2, int paramInt, long paramLong1, long paramLong2)
    {
      this.id = paramObject1;
      this.uid = paramObject2;
      this.windowIndex = paramInt;
      this.durationUs = paramLong1;
      this.positionInWindowUs = paramLong2;
      return this;
    }
  }
  
  public static final class Window
  {
    public long defaultPositionUs;
    public long durationUs;
    public int firstPeriodIndex;
    public Object id;
    public boolean isDynamic;
    public boolean isSeekable;
    public int lastPeriodIndex;
    public long positionInFirstPeriodUs;
    public long presentationStartTimeMs;
    public long windowStartTimeMs;
    
    public long getDefaultPositionMs()
    {
      return C.usToMs(this.defaultPositionUs);
    }
    
    public long getDefaultPositionUs()
    {
      return this.defaultPositionUs;
    }
    
    public long getDurationMs()
    {
      return C.usToMs(this.durationUs);
    }
    
    public long getDurationUs()
    {
      return this.durationUs;
    }
    
    public long getPositionInFirstPeriodMs()
    {
      return C.usToMs(this.positionInFirstPeriodUs);
    }
    
    public long getPositionInFirstPeriodUs()
    {
      return this.positionInFirstPeriodUs;
    }
    
    public Window set(Object paramObject, long paramLong1, long paramLong2, boolean paramBoolean1, boolean paramBoolean2, long paramLong3, long paramLong4, int paramInt1, int paramInt2, long paramLong5)
    {
      this.id = paramObject;
      this.presentationStartTimeMs = paramLong1;
      this.windowStartTimeMs = paramLong2;
      this.isSeekable = paramBoolean1;
      this.isDynamic = paramBoolean2;
      this.defaultPositionUs = paramLong3;
      this.durationUs = paramLong4;
      this.firstPeriodIndex = paramInt1;
      this.lastPeriodIndex = paramInt2;
      this.positionInFirstPeriodUs = paramLong5;
      return this;
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/Timeline.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */