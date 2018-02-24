package com.bumptech.glide.load.model;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.data.DataFetcher.DataCallback;
import com.bumptech.glide.load.data.mediastore.MediaStoreUtil;
import com.bumptech.glide.signature.ObjectKey;
import java.io.File;
import java.io.FileNotFoundException;

public final class MediaStoreFileLoader
  implements ModelLoader<Uri, File>
{
  private final Context context;
  
  MediaStoreFileLoader(Context paramContext)
  {
    this.context = paramContext;
  }
  
  public ModelLoader.LoadData<File> buildLoadData(Uri paramUri, int paramInt1, int paramInt2, Options paramOptions)
  {
    return new ModelLoader.LoadData(new ObjectKey(paramUri), new FilePathFetcher(this.context, paramUri));
  }
  
  public boolean handles(Uri paramUri)
  {
    return MediaStoreUtil.isMediaStoreUri(paramUri);
  }
  
  public static final class Factory
    implements ModelLoaderFactory<Uri, File>
  {
    private final Context context;
    
    public Factory(Context paramContext)
    {
      this.context = paramContext;
    }
    
    public ModelLoader<Uri, File> build(MultiModelLoaderFactory paramMultiModelLoaderFactory)
    {
      return new MediaStoreFileLoader(this.context);
    }
    
    public void teardown() {}
  }
  
  private static class FilePathFetcher
    implements DataFetcher<File>
  {
    private static final String[] PROJECTION = { "_data" };
    private final Context context;
    private final Uri uri;
    
    FilePathFetcher(Context paramContext, Uri paramUri)
    {
      this.context = paramContext;
      this.uri = paramUri;
    }
    
    public void cancel() {}
    
    public void cleanup() {}
    
    public Class<File> getDataClass()
    {
      return File.class;
    }
    
    public DataSource getDataSource()
    {
      return DataSource.LOCAL;
    }
    
    public void loadData(Priority paramPriority, DataFetcher.DataCallback<? super File> paramDataCallback)
    {
      Cursor localCursor = this.context.getContentResolver().query(this.uri, PROJECTION, null, null, null);
      paramPriority = null;
      Object localObject = null;
      if (localCursor != null) {
        paramPriority = (Priority)localObject;
      }
      try
      {
        if (localCursor.moveToFirst()) {
          paramPriority = localCursor.getString(localCursor.getColumnIndexOrThrow("_data"));
        }
        localCursor.close();
        if (TextUtils.isEmpty(paramPriority))
        {
          paramPriority = String.valueOf(this.uri);
          paramDataCallback.onLoadFailed(new FileNotFoundException(String.valueOf(paramPriority).length() + 30 + "Failed to find file path for: " + paramPriority));
          return;
        }
      }
      finally
      {
        localCursor.close();
      }
      paramDataCallback.onDataReady(new File(paramPriority));
    }
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/load/model/MediaStoreFileLoader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */