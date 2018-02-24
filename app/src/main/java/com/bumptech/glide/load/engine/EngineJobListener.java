package com.bumptech.glide.load.engine;

import com.bumptech.glide.load.Key;

abstract interface EngineJobListener
{
  public abstract void onEngineJobCancelled(EngineJob paramEngineJob, Key paramKey);
  
  public abstract void onEngineJobComplete(Key paramKey, EngineResource<?> paramEngineResource);
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/load/engine/EngineJobListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */