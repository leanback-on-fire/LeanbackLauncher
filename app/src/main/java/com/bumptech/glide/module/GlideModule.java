package com.bumptech.glide.module;

import android.content.Context;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;

public abstract interface GlideModule
{
  public abstract void applyOptions(Context paramContext, GlideBuilder paramGlideBuilder);
  
  public abstract void registerComponents(Context paramContext, Registry paramRegistry);
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/module/GlideModule.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */