package com.bumptech.glide.load.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.util.Log;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import java.io.FileNotFoundException;
import java.io.IOException;

public abstract class LocalUriFetcher<T>
  implements DataFetcher<T>
{
  private static final String TAG = "LocalUriFetcher";
  private final ContentResolver contentResolver;
  private T data;
  private final Uri uri;
  
  public LocalUriFetcher(ContentResolver paramContentResolver, Uri paramUri)
  {
    this.contentResolver = paramContentResolver;
    this.uri = paramUri;
  }
  
  public void cancel() {}
  
  public void cleanup()
  {
    if (this.data != null) {}
    try
    {
      close(this.data);
      return;
    }
    catch (IOException localIOException) {}
  }
  
  protected abstract void close(T paramT)
    throws IOException;
  
  public DataSource getDataSource()
  {
    return DataSource.LOCAL;
  }
  
  public final void loadData(Priority paramPriority, DataFetcher.DataCallback<? super T> paramDataCallback)
  {
    try
    {
      this.data = loadResource(this.uri, this.contentResolver);
      paramDataCallback.onDataReady(this.data);
      return;
    }
    catch (FileNotFoundException paramPriority)
    {
      if (Log.isLoggable("LocalUriFetcher", 3)) {
        Log.d("LocalUriFetcher", "Failed to open Uri", paramPriority);
      }
      paramDataCallback.onLoadFailed(paramPriority);
    }
  }
  
  protected abstract T loadResource(Uri paramUri, ContentResolver paramContentResolver)
    throws FileNotFoundException;
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/load/data/LocalUriFetcher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */