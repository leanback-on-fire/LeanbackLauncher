package com.bumptech.glide.gifdecoder;

import java.util.ArrayList;
import java.util.List;

public class GifHeader
{
  int bgColor;
  int bgIndex;
  GifFrame currentFrame;
  int frameCount = 0;
  List<GifFrame> frames = new ArrayList();
  int[] gct = null;
  boolean gctFlag;
  int gctSize;
  int height;
  int loopCount;
  int pixelAspect;
  int status = 0;
  int width;
  
  public int getHeight()
  {
    return this.height;
  }
  
  public int getNumFrames()
  {
    return this.frameCount;
  }
  
  public int getStatus()
  {
    return this.status;
  }
  
  public int getWidth()
  {
    return this.width;
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/gifdecoder/GifHeader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */