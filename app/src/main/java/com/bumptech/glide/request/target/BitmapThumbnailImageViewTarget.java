package com.bumptech.glide.request.target;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

public class BitmapThumbnailImageViewTarget
  extends ThumbnailImageViewTarget<Bitmap>
{
  public BitmapThumbnailImageViewTarget(ImageView paramImageView)
  {
    super(paramImageView);
  }
  
  protected Drawable getDrawable(Bitmap paramBitmap)
  {
    return new BitmapDrawable(((ImageView)this.view).getResources(), paramBitmap);
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/request/target/BitmapThumbnailImageViewTarget.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */