package com.bumptech.glide.load.model;

import android.util.Log;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.data.DataFetcher.DataCallback;
import com.bumptech.glide.signature.ObjectKey;
import com.bumptech.glide.util.ByteBufferUtil;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

public class ByteBufferFileLoader
  implements ModelLoader<File, ByteBuffer>
{
  private static final String TAG = "ByteBufferFileLoader";
  
  public ModelLoader.LoadData<ByteBuffer> buildLoadData(File paramFile, int paramInt1, int paramInt2, Options paramOptions)
  {
    return new ModelLoader.LoadData(new ObjectKey(paramFile), new ByteBufferFetcher(paramFile));
  }
  
  public boolean handles(File paramFile)
  {
    return true;
  }
  
  private static class ByteBufferFetcher
    implements DataFetcher<ByteBuffer>
  {
    private final File file;
    
    public ByteBufferFetcher(File paramFile)
    {
      this.file = paramFile;
    }
    
    public void cancel() {}
    
    public void cleanup() {}
    
    public Class<ByteBuffer> getDataClass()
    {
      return ByteBuffer.class;
    }
    
    public DataSource getDataSource()
    {
      return DataSource.LOCAL;
    }
    
    public void loadData(Priority paramPriority, DataFetcher.DataCallback<? super ByteBuffer> paramDataCallback)
    {
      try
      {
        paramPriority = ByteBufferUtil.fromFile(this.file);
        paramDataCallback.onDataReady(paramPriority);
        return;
      }
      catch (IOException paramPriority)
      {
        if (Log.isLoggable("ByteBufferFileLoader", 3)) {
          Log.d("ByteBufferFileLoader", "Failed to obtain ByteBuffer for file", paramPriority);
        }
        paramDataCallback.onLoadFailed(paramPriority);
      }
    }
  }
  
  public static class Factory
    implements ModelLoaderFactory<File, ByteBuffer>
  {
    public ModelLoader<File, ByteBuffer> build(MultiModelLoaderFactory paramMultiModelLoaderFactory)
    {
      return new ByteBufferFileLoader();
    }
    
    public void teardown() {}
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/load/model/ByteBufferFileLoader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */