package com.bumptech.glide.load.data.mediastore;

import android.database.Cursor;
import android.net.Uri;

abstract interface ThumbnailQuery
{
  public abstract Cursor query(Uri paramUri);
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/load/data/mediastore/ThumbnailQuery.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */