package com.bumptech.glide.request.target;

import com.bumptech.glide.util.Util;

public abstract class SimpleTarget<Z>
  extends BaseTarget<Z>
{
  private final int height;
  private final int width;
  
  public SimpleTarget()
  {
    this(Integer.MIN_VALUE, Integer.MIN_VALUE);
  }
  
  public SimpleTarget(int paramInt1, int paramInt2)
  {
    this.width = paramInt1;
    this.height = paramInt2;
  }
  
  public final void getSize(SizeReadyCallback paramSizeReadyCallback)
  {
    if (!Util.isValidDimensions(this.width, this.height))
    {
      int i = this.width;
      int j = this.height;
      throw new IllegalArgumentException(176 + "Width and height must both be > 0 or Target#SIZE_ORIGINAL, but given width: " + i + " and height: " + j + ", either provide dimensions in the constructor or call override()");
    }
    paramSizeReadyCallback.onSizeReady(this.width, this.height);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/request/target/SimpleTarget.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */