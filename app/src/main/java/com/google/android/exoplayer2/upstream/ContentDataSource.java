package com.google.android.exoplayer2.upstream;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public final class ContentDataSource
  implements DataSource
{
  private AssetFileDescriptor assetFileDescriptor;
  private long bytesRemaining;
  private InputStream inputStream;
  private final TransferListener<? super ContentDataSource> listener;
  private boolean opened;
  private final ContentResolver resolver;
  private Uri uri;
  
  public ContentDataSource(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public ContentDataSource(Context paramContext, TransferListener<? super ContentDataSource> paramTransferListener)
  {
    this.resolver = paramContext.getContentResolver();
    this.listener = paramTransferListener;
  }
  
  /* Error */
  public void close()
    throws ContentDataSource.ContentDataSourceException
  {
    // Byte code:
    //   0: aload_0
    //   1: aconst_null
    //   2: putfield 50	com/google/android/exoplayer2/upstream/ContentDataSource:uri	Landroid/net/Uri;
    //   5: aload_0
    //   6: getfield 52	com/google/android/exoplayer2/upstream/ContentDataSource:inputStream	Ljava/io/InputStream;
    //   9: ifnull +10 -> 19
    //   12: aload_0
    //   13: getfield 52	com/google/android/exoplayer2/upstream/ContentDataSource:inputStream	Ljava/io/InputStream;
    //   16: invokevirtual 56	java/io/InputStream:close	()V
    //   19: aload_0
    //   20: aconst_null
    //   21: putfield 52	com/google/android/exoplayer2/upstream/ContentDataSource:inputStream	Ljava/io/InputStream;
    //   24: aload_0
    //   25: getfield 58	com/google/android/exoplayer2/upstream/ContentDataSource:assetFileDescriptor	Landroid/content/res/AssetFileDescriptor;
    //   28: ifnull +10 -> 38
    //   31: aload_0
    //   32: getfield 58	com/google/android/exoplayer2/upstream/ContentDataSource:assetFileDescriptor	Landroid/content/res/AssetFileDescriptor;
    //   35: invokevirtual 61	android/content/res/AssetFileDescriptor:close	()V
    //   38: aload_0
    //   39: aconst_null
    //   40: putfield 58	com/google/android/exoplayer2/upstream/ContentDataSource:assetFileDescriptor	Landroid/content/res/AssetFileDescriptor;
    //   43: aload_0
    //   44: getfield 63	com/google/android/exoplayer2/upstream/ContentDataSource:opened	Z
    //   47: ifeq +25 -> 72
    //   50: aload_0
    //   51: iconst_0
    //   52: putfield 63	com/google/android/exoplayer2/upstream/ContentDataSource:opened	Z
    //   55: aload_0
    //   56: getfield 43	com/google/android/exoplayer2/upstream/ContentDataSource:listener	Lcom/google/android/exoplayer2/upstream/TransferListener;
    //   59: ifnull +13 -> 72
    //   62: aload_0
    //   63: getfield 43	com/google/android/exoplayer2/upstream/ContentDataSource:listener	Lcom/google/android/exoplayer2/upstream/TransferListener;
    //   66: aload_0
    //   67: invokeinterface 69 2 0
    //   72: return
    //   73: astore_1
    //   74: new 8	com/google/android/exoplayer2/upstream/ContentDataSource$ContentDataSourceException
    //   77: dup
    //   78: aload_1
    //   79: invokespecial 72	com/google/android/exoplayer2/upstream/ContentDataSource$ContentDataSourceException:<init>	(Ljava/io/IOException;)V
    //   82: athrow
    //   83: astore_1
    //   84: aload_0
    //   85: aconst_null
    //   86: putfield 58	com/google/android/exoplayer2/upstream/ContentDataSource:assetFileDescriptor	Landroid/content/res/AssetFileDescriptor;
    //   89: aload_0
    //   90: getfield 63	com/google/android/exoplayer2/upstream/ContentDataSource:opened	Z
    //   93: ifeq +25 -> 118
    //   96: aload_0
    //   97: iconst_0
    //   98: putfield 63	com/google/android/exoplayer2/upstream/ContentDataSource:opened	Z
    //   101: aload_0
    //   102: getfield 43	com/google/android/exoplayer2/upstream/ContentDataSource:listener	Lcom/google/android/exoplayer2/upstream/TransferListener;
    //   105: ifnull +13 -> 118
    //   108: aload_0
    //   109: getfield 43	com/google/android/exoplayer2/upstream/ContentDataSource:listener	Lcom/google/android/exoplayer2/upstream/TransferListener;
    //   112: aload_0
    //   113: invokeinterface 69 2 0
    //   118: aload_1
    //   119: athrow
    //   120: astore_1
    //   121: new 8	com/google/android/exoplayer2/upstream/ContentDataSource$ContentDataSourceException
    //   124: dup
    //   125: aload_1
    //   126: invokespecial 72	com/google/android/exoplayer2/upstream/ContentDataSource$ContentDataSourceException:<init>	(Ljava/io/IOException;)V
    //   129: athrow
    //   130: astore_1
    //   131: aload_0
    //   132: aconst_null
    //   133: putfield 52	com/google/android/exoplayer2/upstream/ContentDataSource:inputStream	Ljava/io/InputStream;
    //   136: aload_0
    //   137: getfield 58	com/google/android/exoplayer2/upstream/ContentDataSource:assetFileDescriptor	Landroid/content/res/AssetFileDescriptor;
    //   140: ifnull +10 -> 150
    //   143: aload_0
    //   144: getfield 58	com/google/android/exoplayer2/upstream/ContentDataSource:assetFileDescriptor	Landroid/content/res/AssetFileDescriptor;
    //   147: invokevirtual 61	android/content/res/AssetFileDescriptor:close	()V
    //   150: aload_0
    //   151: aconst_null
    //   152: putfield 58	com/google/android/exoplayer2/upstream/ContentDataSource:assetFileDescriptor	Landroid/content/res/AssetFileDescriptor;
    //   155: aload_0
    //   156: getfield 63	com/google/android/exoplayer2/upstream/ContentDataSource:opened	Z
    //   159: ifeq +25 -> 184
    //   162: aload_0
    //   163: iconst_0
    //   164: putfield 63	com/google/android/exoplayer2/upstream/ContentDataSource:opened	Z
    //   167: aload_0
    //   168: getfield 43	com/google/android/exoplayer2/upstream/ContentDataSource:listener	Lcom/google/android/exoplayer2/upstream/TransferListener;
    //   171: ifnull +13 -> 184
    //   174: aload_0
    //   175: getfield 43	com/google/android/exoplayer2/upstream/ContentDataSource:listener	Lcom/google/android/exoplayer2/upstream/TransferListener;
    //   178: aload_0
    //   179: invokeinterface 69 2 0
    //   184: aload_1
    //   185: athrow
    //   186: astore_1
    //   187: new 8	com/google/android/exoplayer2/upstream/ContentDataSource$ContentDataSourceException
    //   190: dup
    //   191: aload_1
    //   192: invokespecial 72	com/google/android/exoplayer2/upstream/ContentDataSource$ContentDataSourceException:<init>	(Ljava/io/IOException;)V
    //   195: athrow
    //   196: astore_1
    //   197: aload_0
    //   198: aconst_null
    //   199: putfield 58	com/google/android/exoplayer2/upstream/ContentDataSource:assetFileDescriptor	Landroid/content/res/AssetFileDescriptor;
    //   202: aload_0
    //   203: getfield 63	com/google/android/exoplayer2/upstream/ContentDataSource:opened	Z
    //   206: ifeq +25 -> 231
    //   209: aload_0
    //   210: iconst_0
    //   211: putfield 63	com/google/android/exoplayer2/upstream/ContentDataSource:opened	Z
    //   214: aload_0
    //   215: getfield 43	com/google/android/exoplayer2/upstream/ContentDataSource:listener	Lcom/google/android/exoplayer2/upstream/TransferListener;
    //   218: ifnull +13 -> 231
    //   221: aload_0
    //   222: getfield 43	com/google/android/exoplayer2/upstream/ContentDataSource:listener	Lcom/google/android/exoplayer2/upstream/TransferListener;
    //   225: aload_0
    //   226: invokeinterface 69 2 0
    //   231: aload_1
    //   232: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	233	0	this	ContentDataSource
    //   73	6	1	localIOException1	IOException
    //   83	36	1	localObject1	Object
    //   120	6	1	localIOException2	IOException
    //   130	55	1	localObject2	Object
    //   186	6	1	localIOException3	IOException
    //   196	36	1	localObject3	Object
    // Exception table:
    //   from	to	target	type
    //   24	38	73	java/io/IOException
    //   24	38	83	finally
    //   74	83	83	finally
    //   5	19	120	java/io/IOException
    //   5	19	130	finally
    //   121	130	130	finally
    //   136	150	186	java/io/IOException
    //   136	150	196	finally
    //   187	196	196	finally
  }
  
  public Uri getUri()
  {
    return this.uri;
  }
  
  public long open(DataSpec paramDataSpec)
    throws ContentDataSource.ContentDataSourceException
  {
    try
    {
      this.uri = paramDataSpec.uri;
      this.assetFileDescriptor = this.resolver.openAssetFileDescriptor(this.uri, "r");
      this.inputStream = new FileInputStream(this.assetFileDescriptor.getFileDescriptor());
      if (this.inputStream.skip(paramDataSpec.position) < paramDataSpec.position) {
        throw new EOFException();
      }
    }
    catch (IOException paramDataSpec)
    {
      throw new ContentDataSourceException(paramDataSpec);
    }
    if (paramDataSpec.length != -1L) {
      this.bytesRemaining = paramDataSpec.length;
    }
    for (;;)
    {
      this.opened = true;
      if (this.listener != null) {
        this.listener.onTransferStart(this, paramDataSpec);
      }
      return this.bytesRemaining;
      this.bytesRemaining = this.inputStream.available();
      if (this.bytesRemaining == 0L) {
        this.bytesRemaining = -1L;
      }
    }
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws ContentDataSource.ContentDataSourceException
  {
    if (paramInt2 == 0) {
      paramInt1 = 0;
    }
    label102:
    do
    {
      return paramInt1;
      if (this.bytesRemaining == 0L) {
        return -1;
      }
      try
      {
        if (this.bytesRemaining == -1L) {}
        for (;;)
        {
          paramInt2 = this.inputStream.read(paramArrayOfByte, paramInt1, paramInt2);
          if (paramInt2 != -1) {
            break label102;
          }
          if (this.bytesRemaining == -1L) {
            break;
          }
          throw new ContentDataSourceException(new EOFException());
          long l = Math.min(this.bytesRemaining, paramInt2);
          paramInt2 = (int)l;
        }
        return -1;
      }
      catch (IOException paramArrayOfByte)
      {
        throw new ContentDataSourceException(paramArrayOfByte);
      }
      if (this.bytesRemaining != -1L) {
        this.bytesRemaining -= paramInt2;
      }
      paramInt1 = paramInt2;
    } while (this.listener == null);
    this.listener.onBytesTransferred(this, paramInt2);
    return paramInt2;
  }
  
  public static class ContentDataSourceException
    extends IOException
  {
    public ContentDataSourceException(IOException paramIOException)
    {
      super();
    }
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/upstream/ContentDataSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */