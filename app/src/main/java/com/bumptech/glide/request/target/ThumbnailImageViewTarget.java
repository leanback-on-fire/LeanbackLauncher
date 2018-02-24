package com.bumptech.glide.request.target;

import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

public abstract class ThumbnailImageViewTarget<T>
  extends ImageViewTarget<T>
{
  public ThumbnailImageViewTarget(ImageView paramImageView)
  {
    super(paramImageView);
  }
  
  protected abstract Drawable getDrawable(T paramT);
  
  protected void setResource(@Nullable T paramT)
  {
    ViewGroup.LayoutParams localLayoutParams = ((ImageView)this.view).getLayoutParams();
    Drawable localDrawable = getDrawable(paramT);
    paramT = localDrawable;
    if (localLayoutParams != null)
    {
      paramT = localDrawable;
      if (localLayoutParams.width > 0)
      {
        paramT = localDrawable;
        if (localLayoutParams.height > 0) {
          paramT = new FixedSizeDrawable(localDrawable, localLayoutParams.width, localLayoutParams.height);
        }
      }
    }
    ((ImageView)this.view).setImageDrawable(paramT);
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/request/target/ThumbnailImageViewTarget.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */