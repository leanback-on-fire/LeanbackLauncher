package com.google.android.exoplayer2.upstream;

import android.text.TextUtils;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Predicate;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public abstract interface HttpDataSource
  extends DataSource
{
  public static final Predicate<String> REJECT_PAYWALL_TYPES = new Predicate()
  {
    public boolean evaluate(String paramAnonymousString)
    {
      paramAnonymousString = Util.toLowerInvariant(paramAnonymousString);
      return (!TextUtils.isEmpty(paramAnonymousString)) && ((!paramAnonymousString.contains("text")) || (paramAnonymousString.contains("text/vtt"))) && (!paramAnonymousString.contains("html")) && (!paramAnonymousString.contains("xml"));
    }
  };
  
  public abstract void clearAllRequestProperties();
  
  public abstract void clearRequestProperty(String paramString);
  
  public abstract void close()
    throws HttpDataSource.HttpDataSourceException;
  
  public abstract Map<String, List<String>> getResponseHeaders();
  
  public abstract long open(DataSpec paramDataSpec)
    throws HttpDataSource.HttpDataSourceException;
  
  public abstract int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws HttpDataSource.HttpDataSourceException;
  
  public abstract void setRequestProperty(String paramString1, String paramString2);
  
  public static abstract class BaseFactory
    implements HttpDataSource.Factory
  {
    private final HashMap<String, String> requestProperties = new HashMap();
    
    public final void clearAllDefaultRequestProperties()
    {
      synchronized (this.requestProperties)
      {
        this.requestProperties.clear();
        return;
      }
    }
    
    public final void clearDefaultRequestProperty(String paramString)
    {
      Assertions.checkNotNull(paramString);
      synchronized (this.requestProperties)
      {
        this.requestProperties.remove(paramString);
        return;
      }
    }
    
    public final HttpDataSource createDataSource()
    {
      HttpDataSource localHttpDataSource1 = createDataSourceInternal();
      synchronized (this.requestProperties)
      {
        Iterator localIterator = this.requestProperties.entrySet().iterator();
        if (localIterator.hasNext())
        {
          Map.Entry localEntry = (Map.Entry)localIterator.next();
          localHttpDataSource1.setRequestProperty((String)localEntry.getKey(), (String)localEntry.getValue());
        }
      }
      return localHttpDataSource2;
    }
    
    protected abstract HttpDataSource createDataSourceInternal();
    
    public final void setDefaultRequestProperty(String paramString1, String paramString2)
    {
      Assertions.checkNotNull(paramString1);
      Assertions.checkNotNull(paramString2);
      synchronized (this.requestProperties)
      {
        this.requestProperties.put(paramString1, paramString2);
        return;
      }
    }
  }
  
  public static abstract interface Factory
    extends DataSource.Factory
  {
    public abstract void clearAllDefaultRequestProperties();
    
    public abstract void clearDefaultRequestProperty(String paramString);
    
    public abstract HttpDataSource createDataSource();
    
    public abstract void setDefaultRequestProperty(String paramString1, String paramString2);
  }
  
  public static class HttpDataSourceException
    extends IOException
  {
    public static final int TYPE_CLOSE = 3;
    public static final int TYPE_OPEN = 1;
    public static final int TYPE_READ = 2;
    public final DataSpec dataSpec;
    public final int type;
    
    public HttpDataSourceException(DataSpec paramDataSpec, int paramInt)
    {
      this.dataSpec = paramDataSpec;
      this.type = paramInt;
    }
    
    public HttpDataSourceException(IOException paramIOException, DataSpec paramDataSpec, int paramInt)
    {
      super();
      this.dataSpec = paramDataSpec;
      this.type = paramInt;
    }
    
    public HttpDataSourceException(String paramString, DataSpec paramDataSpec, int paramInt)
    {
      super();
      this.dataSpec = paramDataSpec;
      this.type = paramInt;
    }
    
    public HttpDataSourceException(String paramString, IOException paramIOException, DataSpec paramDataSpec, int paramInt)
    {
      super(paramIOException);
      this.dataSpec = paramDataSpec;
      this.type = paramInt;
    }
    
    @Retention(RetentionPolicy.SOURCE)
    public static @interface Type {}
  }
  
  public static final class InvalidContentTypeException
    extends HttpDataSource.HttpDataSourceException
  {
    public final String contentType;
    
    public InvalidContentTypeException(String paramString, DataSpec paramDataSpec)
    {
      super(paramDataSpec, 1);
      this.contentType = paramString;
    }
  }
  
  public static final class InvalidResponseCodeException
    extends HttpDataSource.HttpDataSourceException
  {
    public final Map<String, List<String>> headerFields;
    public final int responseCode;
    
    public InvalidResponseCodeException(int paramInt, Map<String, List<String>> paramMap, DataSpec paramDataSpec)
    {
      super(paramDataSpec, 1);
      this.responseCode = paramInt;
      this.headerFields = paramMap;
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/upstream/HttpDataSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */