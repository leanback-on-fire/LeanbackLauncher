package com.bumptech.glide.request.target;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

public class ImageViewTargetFactory
{
  public <Z> Target<Z> buildTarget(ImageView paramImageView, Class<Z> paramClass)
  {
    if (Bitmap.class.equals(paramClass)) {
      return new BitmapImageViewTarget(paramImageView);
    }
    if (Drawable.class.isAssignableFrom(paramClass)) {
      return new DrawableImageViewTarget(paramImageView);
    }
    paramImageView = String.valueOf(paramClass);
    throw new IllegalArgumentException(String.valueOf(paramImageView).length() + 64 + "Unhandled class: " + paramImageView + ", try .as*(Class).transcode(ResourceTranscoder)");
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/request/target/ImageViewTargetFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */