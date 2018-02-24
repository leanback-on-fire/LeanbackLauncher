package com.bumptech.glide.load.resource.bitmap;

public abstract class DownsampleStrategy
{
  public static final DownsampleStrategy AT_LEAST;
  public static final DownsampleStrategy AT_MOST;
  public static final DownsampleStrategy CENTER_INSIDE;
  public static final DownsampleStrategy CENTER_OUTSIDE;
  public static final DownsampleStrategy DEFAULT = AT_LEAST;
  public static final DownsampleStrategy FIT_CENTER = new FitCenter(null);
  public static final DownsampleStrategy NONE;
  
  static
  {
    CENTER_OUTSIDE = new CenterOutside(null);
    AT_LEAST = new AtLeast(null);
    AT_MOST = new AtMost(null);
    CENTER_INSIDE = new CenterInside(null);
    NONE = new None(null);
  }
  
  public abstract SampleSizeRounding getSampleSizeRounding(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  public abstract float getScaleFactor(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  private static class AtLeast
    extends DownsampleStrategy
  {
    public DownsampleStrategy.SampleSizeRounding getSampleSizeRounding(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      return DownsampleStrategy.SampleSizeRounding.QUALITY;
    }
    
    public float getScaleFactor(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      paramInt1 = Math.min(paramInt2 / paramInt4, paramInt1 / paramInt3);
      if (paramInt1 == 0) {
        return 1.0F;
      }
      return 1.0F / Integer.highestOneBit(paramInt1);
    }
  }
  
  private static class AtMost
    extends DownsampleStrategy
  {
    public DownsampleStrategy.SampleSizeRounding getSampleSizeRounding(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      return DownsampleStrategy.SampleSizeRounding.MEMORY;
    }
    
    public float getScaleFactor(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      int i = 1;
      paramInt1 = (int)Math.ceil(Math.max(paramInt2 / paramInt4, paramInt1 / paramInt3));
      paramInt2 = Math.max(1, Integer.highestOneBit(paramInt1));
      if (paramInt2 < paramInt1) {}
      for (paramInt1 = i;; paramInt1 = 0) {
        return 1.0F / (paramInt2 << paramInt1);
      }
    }
  }
  
  private static class CenterInside
    extends DownsampleStrategy
  {
    public DownsampleStrategy.SampleSizeRounding getSampleSizeRounding(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      return DownsampleStrategy.SampleSizeRounding.QUALITY;
    }
    
    public float getScaleFactor(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      return Math.min(1.0F, FIT_CENTER.getScaleFactor(paramInt1, paramInt2, paramInt3, paramInt4));
    }
  }
  
  private static class CenterOutside
    extends DownsampleStrategy
  {
    public DownsampleStrategy.SampleSizeRounding getSampleSizeRounding(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      return DownsampleStrategy.SampleSizeRounding.QUALITY;
    }
    
    public float getScaleFactor(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      return Math.max(paramInt3 / paramInt1, paramInt4 / paramInt2);
    }
  }
  
  private static class FitCenter
    extends DownsampleStrategy
  {
    public DownsampleStrategy.SampleSizeRounding getSampleSizeRounding(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      return DownsampleStrategy.SampleSizeRounding.QUALITY;
    }
    
    public float getScaleFactor(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      return Math.min(paramInt3 / paramInt1, paramInt4 / paramInt2);
    }
  }
  
  private static class None
    extends DownsampleStrategy
  {
    public DownsampleStrategy.SampleSizeRounding getSampleSizeRounding(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      return DownsampleStrategy.SampleSizeRounding.QUALITY;
    }
    
    public float getScaleFactor(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      return 1.0F;
    }
  }
  
  public static enum SampleSizeRounding
  {
    MEMORY,  QUALITY;
    
    private SampleSizeRounding() {}
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/load/resource/bitmap/DownsampleStrategy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */