package com.google.android.exoplayer2.upstream;

import android.net.Uri;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.io.InputStream;

public final class ParsingLoadable<T>
  implements Loader.Loadable
{
  private volatile long bytesLoaded;
  private final DataSource dataSource;
  public final DataSpec dataSpec;
  private volatile boolean isCanceled;
  private final Parser<T> parser;
  private volatile T result;
  public final int type;
  
  public ParsingLoadable(DataSource paramDataSource, Uri paramUri, int paramInt, Parser<T> paramParser)
  {
    this.dataSource = paramDataSource;
    this.dataSpec = new DataSpec(paramUri, 1);
    this.type = paramInt;
    this.parser = paramParser;
  }
  
  public long bytesLoaded()
  {
    return this.bytesLoaded;
  }
  
  public final void cancelLoad()
  {
    this.isCanceled = true;
  }
  
  public final T getResult()
  {
    return (T)this.result;
  }
  
  public final boolean isLoadCanceled()
  {
    return this.isCanceled;
  }
  
  public final void load()
    throws IOException, InterruptedException
  {
    DataSourceInputStream localDataSourceInputStream = new DataSourceInputStream(this.dataSource, this.dataSpec);
    try
    {
      localDataSourceInputStream.open();
      this.result = this.parser.parse(this.dataSource.getUri(), localDataSourceInputStream);
      return;
    }
    finally
    {
      this.bytesLoaded = localDataSourceInputStream.bytesRead();
      Util.closeQuietly(localDataSourceInputStream);
    }
  }
  
  public static abstract interface Parser<T>
  {
    public abstract T parse(Uri paramUri, InputStream paramInputStream)
      throws IOException;
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/upstream/ParsingLoadable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */