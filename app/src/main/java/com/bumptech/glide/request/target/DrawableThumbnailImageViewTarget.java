package com.bumptech.glide.request.target;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

public class DrawableThumbnailImageViewTarget
  extends ThumbnailImageViewTarget<Drawable>
{
  public DrawableThumbnailImageViewTarget(ImageView paramImageView)
  {
    super(paramImageView);
  }
  
  protected Drawable getDrawable(Drawable paramDrawable)
  {
    return paramDrawable;
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/request/target/DrawableThumbnailImageViewTarget.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */