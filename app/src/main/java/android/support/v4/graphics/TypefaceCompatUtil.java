package android.support.v4.graphics;

import android.content.Context;
import android.content.res.Resources;
import android.os.Process;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

@RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
public class TypefaceCompatUtil
{
  private static final String CACHE_FILE_PREFIX = ".font";
  private static final String TAG = "TypefaceCompatUtil";
  
  public static void closeQuietly(Closeable paramCloseable)
  {
    if (paramCloseable != null) {}
    try
    {
      paramCloseable.close();
      return;
    }
    catch (IOException paramCloseable) {}
  }
  
  @RequiresApi(19)
  public static ByteBuffer copyToDirectBuffer(Context paramContext, Resources paramResources, int paramInt)
  {
    paramContext = getTempFile(paramContext);
    if (paramContext == null) {
      return null;
    }
    try
    {
      boolean bool = copyToFile(paramContext, paramResources, paramInt);
      if (!bool) {
        return null;
      }
      paramResources = mmap(paramContext);
      return paramResources;
    }
    finally
    {
      paramContext.delete();
    }
  }
  
  public static boolean copyToFile(File paramFile, Resources paramResources, int paramInt)
  {
    Resources localResources = null;
    try
    {
      paramResources = paramResources.openRawResource(paramInt);
      localResources = paramResources;
      boolean bool = copyToFile(paramFile, paramResources);
      return bool;
    }
    finally
    {
      closeQuietly(localResources);
    }
  }
  
  /* Error */
  public static boolean copyToFile(File paramFile, java.io.InputStream paramInputStream)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_3
    //   2: aconst_null
    //   3: astore 4
    //   5: new 65	java/io/FileOutputStream
    //   8: dup
    //   9: aload_0
    //   10: iconst_0
    //   11: invokespecial 68	java/io/FileOutputStream:<init>	(Ljava/io/File;Z)V
    //   14: astore_0
    //   15: sipush 1024
    //   18: newarray <illegal type>
    //   20: astore_3
    //   21: aload_1
    //   22: aload_3
    //   23: invokevirtual 74	java/io/InputStream:read	([B)I
    //   26: istore_2
    //   27: iload_2
    //   28: iconst_m1
    //   29: if_icmpeq +50 -> 79
    //   32: aload_0
    //   33: aload_3
    //   34: iconst_0
    //   35: iload_2
    //   36: invokevirtual 78	java/io/FileOutputStream:write	([BII)V
    //   39: goto -18 -> 21
    //   42: astore_1
    //   43: aload_0
    //   44: astore_3
    //   45: ldc 15
    //   47: new 80	java/lang/StringBuilder
    //   50: dup
    //   51: invokespecial 81	java/lang/StringBuilder:<init>	()V
    //   54: ldc 83
    //   56: invokevirtual 87	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   59: aload_1
    //   60: invokevirtual 91	java/io/IOException:getMessage	()Ljava/lang/String;
    //   63: invokevirtual 87	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   66: invokevirtual 94	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   69: invokestatic 100	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   72: pop
    //   73: aload_0
    //   74: invokestatic 63	android/support/v4/graphics/TypefaceCompatUtil:closeQuietly	(Ljava/io/Closeable;)V
    //   77: iconst_0
    //   78: ireturn
    //   79: aload_0
    //   80: invokestatic 63	android/support/v4/graphics/TypefaceCompatUtil:closeQuietly	(Ljava/io/Closeable;)V
    //   83: iconst_1
    //   84: ireturn
    //   85: astore_0
    //   86: aload_3
    //   87: invokestatic 63	android/support/v4/graphics/TypefaceCompatUtil:closeQuietly	(Ljava/io/Closeable;)V
    //   90: aload_0
    //   91: athrow
    //   92: astore_1
    //   93: aload_0
    //   94: astore_3
    //   95: aload_1
    //   96: astore_0
    //   97: goto -11 -> 86
    //   100: astore_1
    //   101: aload 4
    //   103: astore_0
    //   104: goto -61 -> 43
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	107	0	paramFile	File
    //   0	107	1	paramInputStream	java.io.InputStream
    //   26	10	2	i	int
    //   1	94	3	localObject1	Object
    //   3	99	4	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   15	21	42	java/io/IOException
    //   21	27	42	java/io/IOException
    //   32	39	42	java/io/IOException
    //   5	15	85	finally
    //   45	73	85	finally
    //   15	21	92	finally
    //   21	27	92	finally
    //   32	39	92	finally
    //   5	15	100	java/io/IOException
  }
  
  public static File getTempFile(Context paramContext)
  {
    String str = ".font" + Process.myPid() + "-" + Process.myTid() + "-";
    int i = 0;
    while (i < 100)
    {
      File localFile = new File(paramContext.getCacheDir(), str + i);
      try
      {
        boolean bool = localFile.createNewFile();
        if (bool) {
          return localFile;
        }
      }
      catch (IOException localIOException)
      {
        i += 1;
      }
    }
    return null;
  }
  
  /* Error */
  @RequiresApi(19)
  public static ByteBuffer mmap(Context paramContext, android.os.CancellationSignal paramCancellationSignal, android.net.Uri paramUri)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 133	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   4: astore_0
    //   5: aload_0
    //   6: aload_2
    //   7: ldc -121
    //   9: aload_1
    //   10: invokevirtual 141	android/content/ContentResolver:openFileDescriptor	(Landroid/net/Uri;Ljava/lang/String;Landroid/os/CancellationSignal;)Landroid/os/ParcelFileDescriptor;
    //   13: astore_2
    //   14: new 143	java/io/FileInputStream
    //   17: dup
    //   18: aload_2
    //   19: invokevirtual 149	android/os/ParcelFileDescriptor:getFileDescriptor	()Ljava/io/FileDescriptor;
    //   22: invokespecial 152	java/io/FileInputStream:<init>	(Ljava/io/FileDescriptor;)V
    //   25: astore 5
    //   27: aload 5
    //   29: invokevirtual 156	java/io/FileInputStream:getChannel	()Ljava/nio/channels/FileChannel;
    //   32: astore_0
    //   33: aload_0
    //   34: invokevirtual 162	java/nio/channels/FileChannel:size	()J
    //   37: lstore_3
    //   38: aload_0
    //   39: getstatic 168	java/nio/channels/FileChannel$MapMode:READ_ONLY	Ljava/nio/channels/FileChannel$MapMode;
    //   42: lconst_0
    //   43: lload_3
    //   44: invokevirtual 172	java/nio/channels/FileChannel:map	(Ljava/nio/channels/FileChannel$MapMode;JJ)Ljava/nio/MappedByteBuffer;
    //   47: astore_0
    //   48: aload 5
    //   50: ifnull +12 -> 62
    //   53: iconst_0
    //   54: ifeq +52 -> 106
    //   57: aload 5
    //   59: invokevirtual 173	java/io/FileInputStream:close	()V
    //   62: aload_2
    //   63: ifnull +11 -> 74
    //   66: iconst_0
    //   67: ifeq +62 -> 129
    //   70: aload_2
    //   71: invokevirtual 174	android/os/ParcelFileDescriptor:close	()V
    //   74: aload_0
    //   75: areturn
    //   76: astore_0
    //   77: new 176	java/lang/NullPointerException
    //   80: dup
    //   81: invokespecial 177	java/lang/NullPointerException:<init>	()V
    //   84: athrow
    //   85: astore_1
    //   86: aload_1
    //   87: athrow
    //   88: astore_0
    //   89: aload_2
    //   90: ifnull +11 -> 101
    //   93: aload_1
    //   94: ifnull +89 -> 183
    //   97: aload_2
    //   98: invokevirtual 174	android/os/ParcelFileDescriptor:close	()V
    //   101: aload_0
    //   102: athrow
    //   103: astore_0
    //   104: aconst_null
    //   105: areturn
    //   106: aload 5
    //   108: invokevirtual 173	java/io/FileInputStream:close	()V
    //   111: goto -49 -> 62
    //   114: astore_0
    //   115: aconst_null
    //   116: astore_1
    //   117: goto -28 -> 89
    //   120: astore_0
    //   121: new 176	java/lang/NullPointerException
    //   124: dup
    //   125: invokespecial 177	java/lang/NullPointerException:<init>	()V
    //   128: athrow
    //   129: aload_2
    //   130: invokevirtual 174	android/os/ParcelFileDescriptor:close	()V
    //   133: aload_0
    //   134: areturn
    //   135: astore_0
    //   136: aload_0
    //   137: athrow
    //   138: astore_1
    //   139: aload 5
    //   141: ifnull +12 -> 153
    //   144: aload_0
    //   145: ifnull +21 -> 166
    //   148: aload 5
    //   150: invokevirtual 173	java/io/FileInputStream:close	()V
    //   153: aload_1
    //   154: athrow
    //   155: astore 5
    //   157: aload_0
    //   158: aload 5
    //   160: invokevirtual 181	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   163: goto -10 -> 153
    //   166: aload 5
    //   168: invokevirtual 173	java/io/FileInputStream:close	()V
    //   171: goto -18 -> 153
    //   174: astore_2
    //   175: aload_1
    //   176: aload_2
    //   177: invokevirtual 181	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   180: goto -79 -> 101
    //   183: aload_2
    //   184: invokevirtual 174	android/os/ParcelFileDescriptor:close	()V
    //   187: goto -86 -> 101
    //   190: astore_1
    //   191: aconst_null
    //   192: astore_0
    //   193: goto -54 -> 139
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	196	0	paramContext	Context
    //   0	196	1	paramCancellationSignal	android.os.CancellationSignal
    //   0	196	2	paramUri	android.net.Uri
    //   37	7	3	l	long
    //   25	124	5	localFileInputStream	FileInputStream
    //   155	12	5	localThrowable	Throwable
    // Exception table:
    //   from	to	target	type
    //   57	62	76	java/lang/Throwable
    //   14	27	85	java/lang/Throwable
    //   77	85	85	java/lang/Throwable
    //   106	111	85	java/lang/Throwable
    //   153	155	85	java/lang/Throwable
    //   157	163	85	java/lang/Throwable
    //   166	171	85	java/lang/Throwable
    //   86	88	88	finally
    //   5	14	103	java/io/IOException
    //   70	74	103	java/io/IOException
    //   97	101	103	java/io/IOException
    //   101	103	103	java/io/IOException
    //   121	129	103	java/io/IOException
    //   129	133	103	java/io/IOException
    //   175	180	103	java/io/IOException
    //   183	187	103	java/io/IOException
    //   14	27	114	finally
    //   57	62	114	finally
    //   77	85	114	finally
    //   106	111	114	finally
    //   148	153	114	finally
    //   153	155	114	finally
    //   157	163	114	finally
    //   166	171	114	finally
    //   70	74	120	java/lang/Throwable
    //   27	48	135	java/lang/Throwable
    //   136	138	138	finally
    //   148	153	155	java/lang/Throwable
    //   97	101	174	java/lang/Throwable
    //   27	48	190	finally
  }
  
  @RequiresApi(19)
  private static ByteBuffer mmap(File paramFile)
  {
    try
    {
      FileInputStream localFileInputStream = new FileInputStream(paramFile);
      try
      {
        paramFile = localFileInputStream.getChannel();
        long l = paramFile.size();
        paramFile = paramFile.map(FileChannel.MapMode.READ_ONLY, 0L, l);
        if ((localFileInputStream == null) || (0 != 0)) {
          try
          {
            localFileInputStream.close();
            return paramFile;
          }
          catch (Throwable paramFile)
          {
            throw new NullPointerException();
          }
        }
        localFileInputStream.close();
        return paramFile;
      }
      catch (Throwable paramFile)
      {
        paramFile = paramFile;
        try
        {
          throw paramFile;
        }
        finally
        {
          if ((localFileInputStream == null) || (paramFile != null)) {
            try
            {
              localFileInputStream.close();
              throw ((Throwable)localObject1);
            }
            catch (Throwable localThrowable)
            {
              for (;;)
              {
                paramFile.addSuppressed(localThrowable);
              }
            }
          }
          localThrowable.close();
        }
      }
      finally
      {
        for (;;)
        {
          localObject2 = finally;
          paramFile = null;
        }
      }
      return null;
    }
    catch (IOException paramFile) {}
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v4/graphics/TypefaceCompatUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */