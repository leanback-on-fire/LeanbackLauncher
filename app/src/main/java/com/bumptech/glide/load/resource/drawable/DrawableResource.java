package com.bumptech.glide.load.resource.drawable;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.ConstantState;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.util.Preconditions;

public abstract class DrawableResource<T extends Drawable>
  implements Resource<T>
{
  protected final T drawable;
  
  public DrawableResource(T paramT)
  {
    this.drawable = ((Drawable)Preconditions.checkNotNull(paramT));
  }
  
  public final T get()
  {
    return this.drawable.getConstantState().newDrawable();
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/load/resource/drawable/DrawableResource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */