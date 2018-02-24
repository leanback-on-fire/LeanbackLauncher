package com.google.android.tvlauncher.util.palette;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.graphics.Palette;
import android.support.v7.graphics.Palette.Builder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.transcode.ResourceTranscoder;

class PaletteBitmapTranscoder
  implements ResourceTranscoder<Bitmap, PaletteBitmapContainer>
{
  private final BitmapPool mBitmapPool;
  
  PaletteBitmapTranscoder(Context paramContext)
  {
    this.mBitmapPool = Glide.get(paramContext).getBitmapPool();
  }
  
  public Resource<PaletteBitmapContainer> transcode(Resource<Bitmap> paramResource)
  {
    paramResource = (Bitmap)paramResource.get();
    return new PaletteBitmapResource(new PaletteBitmapContainer(paramResource, Palette.from(paramResource).generate()), this.mBitmapPool);
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/util/palette/PaletteBitmapTranscoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */