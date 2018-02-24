package com.bumptech.glide.request;

import android.support.annotation.Nullable;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.target.Target;

public abstract interface RequestListener<R>
{
  public abstract boolean onLoadFailed(@Nullable GlideException paramGlideException, Object paramObject, Target<R> paramTarget, boolean paramBoolean);
  
  public abstract boolean onResourceReady(R paramR, Object paramObject, Target<R> paramTarget, DataSource paramDataSource, boolean paramBoolean);
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/request/RequestListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */