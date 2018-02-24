package com.bumptech.glide.load.model;

import android.util.Base64;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.data.DataFetcher.DataCallback;
import com.bumptech.glide.signature.ObjectKey;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public final class DataUrlLoader<Data>
  implements ModelLoader<String, Data>
{
  private static final String BASE64_TAG = ";base64";
  private static final String DATA_SCHEME_IMAGE = "data:image";
  private final DataDecoder<Data> dataDecoder;
  
  public DataUrlLoader(DataDecoder<Data> paramDataDecoder)
  {
    this.dataDecoder = paramDataDecoder;
  }
  
  public ModelLoader.LoadData<Data> buildLoadData(String paramString, int paramInt1, int paramInt2, Options paramOptions)
  {
    return new ModelLoader.LoadData(new ObjectKey(paramString), new DataUriFetcher(paramString, this.dataDecoder));
  }
  
  public boolean handles(String paramString)
  {
    return paramString.startsWith("data:image");
  }
  
  public static abstract interface DataDecoder<Data>
  {
    public abstract void close(Data paramData)
      throws IOException;
    
    public abstract Data decode(String paramString)
      throws IllegalArgumentException;
    
    public abstract Class<Data> getDataClass();
  }
  
  private static final class DataUriFetcher<Data>
    implements DataFetcher<Data>
  {
    private Data data;
    private final String dataUri;
    private final DataUrlLoader.DataDecoder<Data> reader;
    
    public DataUriFetcher(String paramString, DataUrlLoader.DataDecoder<Data> paramDataDecoder)
    {
      this.dataUri = paramString;
      this.reader = paramDataDecoder;
    }
    
    public void cancel() {}
    
    public void cleanup()
    {
      try
      {
        this.reader.close(this.data);
        return;
      }
      catch (IOException localIOException) {}
    }
    
    public Class<Data> getDataClass()
    {
      return this.reader.getDataClass();
    }
    
    public DataSource getDataSource()
    {
      return DataSource.LOCAL;
    }
    
    public void loadData(Priority paramPriority, DataFetcher.DataCallback<? super Data> paramDataCallback)
    {
      try
      {
        this.data = this.reader.decode(this.dataUri);
        paramDataCallback.onDataReady(this.data);
        return;
      }
      catch (IllegalArgumentException paramPriority)
      {
        paramDataCallback.onLoadFailed(paramPriority);
      }
    }
  }
  
  public static final class StreamFactory
    implements ModelLoaderFactory<String, InputStream>
  {
    private final DataUrlLoader.DataDecoder<InputStream> opener = new DataUrlLoader.DataDecoder()
    {
      public void close(InputStream paramAnonymousInputStream)
        throws IOException
      {
        paramAnonymousInputStream.close();
      }
      
      public InputStream decode(String paramAnonymousString)
      {
        if (!paramAnonymousString.startsWith("data:image")) {
          throw new IllegalArgumentException("Not a valid image data URL.");
        }
        int i = paramAnonymousString.indexOf(',');
        if (i == -1) {
          throw new IllegalArgumentException("Missing comma in data URL.");
        }
        if (!paramAnonymousString.substring(0, i).endsWith(";base64")) {
          throw new IllegalArgumentException("Not a base64 image data URL.");
        }
        return new ByteArrayInputStream(Base64.decode(paramAnonymousString.substring(i + 1), 0));
      }
      
      public Class<InputStream> getDataClass()
      {
        return InputStream.class;
      }
    };
    
    public final ModelLoader<String, InputStream> build(MultiModelLoaderFactory paramMultiModelLoaderFactory)
    {
      return new DataUrlLoader(this.opener);
    }
    
    public final void teardown() {}
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/load/model/DataUrlLoader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */