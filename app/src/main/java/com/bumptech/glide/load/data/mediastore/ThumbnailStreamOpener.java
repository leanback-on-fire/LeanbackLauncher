package com.bumptech.glide.load.data.mediastore;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

class ThumbnailStreamOpener
{
  private static final FileService DEFAULT_SERVICE = new FileService();
  private static final String TAG = "ThumbStreamOpener";
  private final ArrayPool byteArrayPool;
  private final ContentResolver contentResolver;
  private final ThumbnailQuery query;
  private final FileService service;
  
  public ThumbnailStreamOpener(FileService paramFileService, ThumbnailQuery paramThumbnailQuery, ArrayPool paramArrayPool, ContentResolver paramContentResolver)
  {
    this.service = paramFileService;
    this.query = paramThumbnailQuery;
    this.byteArrayPool = paramArrayPool;
    this.contentResolver = paramContentResolver;
  }
  
  public ThumbnailStreamOpener(ThumbnailQuery paramThumbnailQuery, ArrayPool paramArrayPool, ContentResolver paramContentResolver)
  {
    this(DEFAULT_SERVICE, paramThumbnailQuery, paramArrayPool, paramContentResolver);
  }
  
  /* Error */
  public int getOrientation(Uri paramUri)
  {
    // Byte code:
    //   0: iconst_m1
    //   1: istore_3
    //   2: aconst_null
    //   3: astore 5
    //   5: aconst_null
    //   6: astore 4
    //   8: aload_0
    //   9: getfield 37	com/bumptech/glide/load/data/mediastore/ThumbnailStreamOpener:contentResolver	Landroid/content/ContentResolver;
    //   12: aload_1
    //   13: invokevirtual 50	android/content/ContentResolver:openInputStream	(Landroid/net/Uri;)Ljava/io/InputStream;
    //   16: astore 6
    //   18: aload 6
    //   20: astore 4
    //   22: aload 6
    //   24: astore 5
    //   26: new 52	com/bumptech/glide/load/resource/bitmap/ImageHeaderParser
    //   29: dup
    //   30: aload 6
    //   32: aload_0
    //   33: getfield 35	com/bumptech/glide/load/data/mediastore/ThumbnailStreamOpener:byteArrayPool	Lcom/bumptech/glide/load/engine/bitmap_recycle/ArrayPool;
    //   36: invokespecial 55	com/bumptech/glide/load/resource/bitmap/ImageHeaderParser:<init>	(Ljava/io/InputStream;Lcom/bumptech/glide/load/engine/bitmap_recycle/ArrayPool;)V
    //   39: invokevirtual 58	com/bumptech/glide/load/resource/bitmap/ImageHeaderParser:getOrientation	()I
    //   42: istore_2
    //   43: iload_2
    //   44: istore_3
    //   45: aload 6
    //   47: ifnull +10 -> 57
    //   50: aload 6
    //   52: invokevirtual 63	java/io/InputStream:close	()V
    //   55: iload_2
    //   56: istore_3
    //   57: iload_3
    //   58: ireturn
    //   59: astore 6
    //   61: aload 4
    //   63: astore 5
    //   65: ldc 10
    //   67: iconst_3
    //   68: invokestatic 69	android/util/Log:isLoggable	(Ljava/lang/String;I)Z
    //   71: ifeq +53 -> 124
    //   74: aload 4
    //   76: astore 5
    //   78: aload_1
    //   79: invokestatic 75	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   82: astore_1
    //   83: aload 4
    //   85: astore 5
    //   87: ldc 10
    //   89: new 77	java/lang/StringBuilder
    //   92: dup
    //   93: aload_1
    //   94: invokestatic 75	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   97: invokevirtual 80	java/lang/String:length	()I
    //   100: bipush 20
    //   102: iadd
    //   103: invokespecial 83	java/lang/StringBuilder:<init>	(I)V
    //   106: ldc 85
    //   108: invokevirtual 89	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   111: aload_1
    //   112: invokevirtual 89	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   115: invokevirtual 93	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   118: aload 6
    //   120: invokestatic 97	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   123: pop
    //   124: aload 4
    //   126: ifnull -69 -> 57
    //   129: aload 4
    //   131: invokevirtual 63	java/io/InputStream:close	()V
    //   134: iconst_m1
    //   135: ireturn
    //   136: astore_1
    //   137: iconst_m1
    //   138: ireturn
    //   139: astore_1
    //   140: aload 5
    //   142: ifnull +8 -> 150
    //   145: aload 5
    //   147: invokevirtual 63	java/io/InputStream:close	()V
    //   150: aload_1
    //   151: athrow
    //   152: astore_1
    //   153: iload_2
    //   154: ireturn
    //   155: astore 4
    //   157: goto -7 -> 150
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	160	0	this	ThumbnailStreamOpener
    //   0	160	1	paramUri	Uri
    //   42	112	2	i	int
    //   1	57	3	j	int
    //   6	124	4	localObject1	Object
    //   155	1	4	localIOException1	java.io.IOException
    //   3	143	5	localObject2	Object
    //   16	35	6	localInputStream	InputStream
    //   59	60	6	localIOException2	java.io.IOException
    // Exception table:
    //   from	to	target	type
    //   8	18	59	java/io/IOException
    //   26	43	59	java/io/IOException
    //   129	134	136	java/io/IOException
    //   8	18	139	finally
    //   26	43	139	finally
    //   65	74	139	finally
    //   78	83	139	finally
    //   87	124	139	finally
    //   50	55	152	java/io/IOException
    //   145	150	155	java/io/IOException
  }
  
  public InputStream open(Uri paramUri)
    throws FileNotFoundException
  {
    Object localObject = null;
    InputStream localInputStream = null;
    Cursor localCursor = this.query.query(paramUri);
    if (localCursor != null) {}
    try
    {
      boolean bool = localCursor.moveToFirst();
      if (!bool) {
        return null;
      }
      paramUri = localCursor.getString(0);
      bool = TextUtils.isEmpty(paramUri);
      if (bool) {
        return null;
      }
      File localFile = this.service.get(paramUri);
      paramUri = (Uri)localObject;
      if (this.service.exists(localFile))
      {
        paramUri = (Uri)localObject;
        if (this.service.length(localFile) > 0L) {
          paramUri = Uri.fromFile(localFile);
        }
      }
      if (localCursor != null) {
        localCursor.close();
      }
      if (paramUri != null) {
        localInputStream = this.contentResolver.openInputStream(paramUri);
      }
      return localInputStream;
    }
    finally
    {
      if (localCursor != null) {
        localCursor.close();
      }
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/load/data/mediastore/ThumbnailStreamOpener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */