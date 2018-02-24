package com.google.android.exoplayer2.extractor;

public abstract interface SeekMap
{
  public abstract long getDurationUs();
  
  public abstract long getPosition(long paramLong);
  
  public abstract boolean isSeekable();
  
  public static final class Unseekable
    implements SeekMap
  {
    private final long durationUs;
    
    public Unseekable(long paramLong)
    {
      this.durationUs = paramLong;
    }
    
    public long getDurationUs()
    {
      return this.durationUs;
    }
    
    public long getPosition(long paramLong)
    {
      return 0L;
    }
    
    public boolean isSeekable()
    {
      return false;
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/extractor/SeekMap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */