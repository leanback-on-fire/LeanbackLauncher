package com.google.android.exoplayer2.upstream;

public final class FileDataSourceFactory
  implements DataSource.Factory
{
  private final TransferListener<? super FileDataSource> listener;
  
  public FileDataSourceFactory()
  {
    this(null);
  }
  
  public FileDataSourceFactory(TransferListener<? super FileDataSource> paramTransferListener)
  {
    this.listener = paramTransferListener;
  }
  
  public DataSource createDataSource()
  {
    return new FileDataSource(this.listener);
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/upstream/FileDataSourceFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */