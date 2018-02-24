package com.google.android.exoplayer2.text.ttml;

final class TtmlRegion
{
  public final float line;
  public final int lineType;
  public final float position;
  public final float width;
  
  public TtmlRegion()
  {
    this(Float.MIN_VALUE, Float.MIN_VALUE, Integer.MIN_VALUE, Float.MIN_VALUE);
  }
  
  public TtmlRegion(float paramFloat1, float paramFloat2, int paramInt, float paramFloat3)
  {
    this.position = paramFloat1;
    this.line = paramFloat2;
    this.lineType = paramInt;
    this.width = paramFloat3;
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/text/ttml/TtmlRegion.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */