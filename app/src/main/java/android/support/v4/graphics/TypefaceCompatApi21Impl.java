package android.support.v4.graphics;

import android.os.ParcelFileDescriptor;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;
import android.system.ErrnoException;
import android.system.Os;
import android.system.OsConstants;
import android.system.StructStat;
import java.io.File;

@RequiresApi(21)
@RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
class TypefaceCompatApi21Impl
  extends TypefaceCompatBaseImpl
{
  private static final String TAG = "TypefaceCompatApi21Impl";
  
  private File getFile(ParcelFileDescriptor paramParcelFileDescriptor)
  {
    try
    {
      paramParcelFileDescriptor = Os.readlink("/proc/self/fd/" + paramParcelFileDescriptor.getFd());
      if (OsConstants.S_ISREG(Os.stat(paramParcelFileDescriptor).st_mode))
      {
        paramParcelFileDescriptor = new File(paramParcelFileDescriptor);
        return paramParcelFileDescriptor;
      }
      return null;
    }
    catch (ErrnoException paramParcelFileDescriptor) {}
    return null;
  }
  
  /* Error */
  public android.graphics.Typeface createFromFontInfo(android.content.Context paramContext, android.os.CancellationSignal paramCancellationSignal, @android.support.annotation.NonNull android.support.v4.provider.FontsContractCompat.FontInfo[] paramArrayOfFontInfo, int paramInt)
  {
    // Byte code:
    //   0: aload_3
    //   1: arraylength
    //   2: iconst_1
    //   3: if_icmpge +7 -> 10
    //   6: aconst_null
    //   7: astore_1
    //   8: aload_1
    //   9: areturn
    //   10: aload_0
    //   11: aload_3
    //   12: iload 4
    //   14: invokevirtual 83	android/support/v4/graphics/TypefaceCompatApi21Impl:findBestInfo	([Landroid/support/v4/provider/FontsContractCompat$FontInfo;I)Landroid/support/v4/provider/FontsContractCompat$FontInfo;
    //   17: astore 5
    //   19: aload_1
    //   20: invokevirtual 89	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   23: astore_3
    //   24: aload_3
    //   25: aload 5
    //   27: invokevirtual 95	android/support/v4/provider/FontsContractCompat$FontInfo:getUri	()Landroid/net/Uri;
    //   30: ldc 97
    //   32: aload_2
    //   33: invokevirtual 103	android/content/ContentResolver:openFileDescriptor	(Landroid/net/Uri;Ljava/lang/String;Landroid/os/CancellationSignal;)Landroid/os/ParcelFileDescriptor;
    //   36: astore_3
    //   37: aload_0
    //   38: aload_3
    //   39: invokespecial 105	android/support/v4/graphics/TypefaceCompatApi21Impl:getFile	(Landroid/os/ParcelFileDescriptor;)Ljava/io/File;
    //   42: astore_2
    //   43: aload_2
    //   44: ifnull +10 -> 54
    //   47: aload_2
    //   48: invokevirtual 109	java/io/File:canRead	()Z
    //   51: ifne +158 -> 209
    //   54: new 111	java/io/FileInputStream
    //   57: dup
    //   58: aload_3
    //   59: invokevirtual 115	android/os/ParcelFileDescriptor:getFileDescriptor	()Ljava/io/FileDescriptor;
    //   62: invokespecial 118	java/io/FileInputStream:<init>	(Ljava/io/FileDescriptor;)V
    //   65: astore 5
    //   67: aconst_null
    //   68: astore_2
    //   69: aload_0
    //   70: aload_1
    //   71: aload 5
    //   73: invokespecial 122	android/support/v4/graphics/TypefaceCompatBaseImpl:createFromInputStream	(Landroid/content/Context;Ljava/io/InputStream;)Landroid/graphics/Typeface;
    //   76: astore_1
    //   77: aload_1
    //   78: astore_2
    //   79: aload 5
    //   81: ifnull +12 -> 93
    //   84: iconst_0
    //   85: ifeq +63 -> 148
    //   88: aload 5
    //   90: invokevirtual 125	java/io/FileInputStream:close	()V
    //   93: aload_2
    //   94: astore_1
    //   95: aload_3
    //   96: ifnull -88 -> 8
    //   99: iconst_0
    //   100: ifeq +62 -> 162
    //   103: aload_3
    //   104: invokevirtual 126	android/os/ParcelFileDescriptor:close	()V
    //   107: aload_2
    //   108: areturn
    //   109: astore_1
    //   110: new 128	java/lang/NullPointerException
    //   113: dup
    //   114: invokespecial 129	java/lang/NullPointerException:<init>	()V
    //   117: athrow
    //   118: astore_1
    //   119: aconst_null
    //   120: areturn
    //   121: astore_1
    //   122: new 128	java/lang/NullPointerException
    //   125: dup
    //   126: invokespecial 129	java/lang/NullPointerException:<init>	()V
    //   129: athrow
    //   130: astore_1
    //   131: aload_1
    //   132: athrow
    //   133: astore_2
    //   134: aload_3
    //   135: ifnull +11 -> 146
    //   138: aload_1
    //   139: ifnull +115 -> 254
    //   142: aload_3
    //   143: invokevirtual 126	android/os/ParcelFileDescriptor:close	()V
    //   146: aload_2
    //   147: athrow
    //   148: aload 5
    //   150: invokevirtual 125	java/io/FileInputStream:close	()V
    //   153: goto -60 -> 93
    //   156: astore_2
    //   157: aconst_null
    //   158: astore_1
    //   159: goto -25 -> 134
    //   162: aload_3
    //   163: invokevirtual 126	android/os/ParcelFileDescriptor:close	()V
    //   166: aload_2
    //   167: areturn
    //   168: astore_1
    //   169: aload_1
    //   170: astore_2
    //   171: aload_1
    //   172: athrow
    //   173: astore_1
    //   174: aload 5
    //   176: ifnull +12 -> 188
    //   179: aload_2
    //   180: ifnull +21 -> 201
    //   183: aload 5
    //   185: invokevirtual 125	java/io/FileInputStream:close	()V
    //   188: aload_1
    //   189: athrow
    //   190: astore 5
    //   192: aload_2
    //   193: aload 5
    //   195: invokevirtual 133	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   198: goto -10 -> 188
    //   201: aload 5
    //   203: invokevirtual 125	java/io/FileInputStream:close	()V
    //   206: goto -18 -> 188
    //   209: aload_2
    //   210: invokestatic 139	android/graphics/Typeface:createFromFile	(Ljava/io/File;)Landroid/graphics/Typeface;
    //   213: astore_2
    //   214: aload_2
    //   215: astore_1
    //   216: aload_3
    //   217: ifnull -209 -> 8
    //   220: iconst_0
    //   221: ifeq +18 -> 239
    //   224: aload_3
    //   225: invokevirtual 126	android/os/ParcelFileDescriptor:close	()V
    //   228: aload_2
    //   229: areturn
    //   230: astore_1
    //   231: new 128	java/lang/NullPointerException
    //   234: dup
    //   235: invokespecial 129	java/lang/NullPointerException:<init>	()V
    //   238: athrow
    //   239: aload_3
    //   240: invokevirtual 126	android/os/ParcelFileDescriptor:close	()V
    //   243: aload_2
    //   244: areturn
    //   245: astore_3
    //   246: aload_1
    //   247: aload_3
    //   248: invokevirtual 133	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   251: goto -105 -> 146
    //   254: aload_3
    //   255: invokevirtual 126	android/os/ParcelFileDescriptor:close	()V
    //   258: goto -112 -> 146
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	261	0	this	TypefaceCompatApi21Impl
    //   0	261	1	paramContext	android.content.Context
    //   0	261	2	paramCancellationSignal	android.os.CancellationSignal
    //   0	261	3	paramArrayOfFontInfo	android.support.v4.provider.FontsContractCompat.FontInfo[]
    //   0	261	4	paramInt	int
    //   17	167	5	localObject	Object
    //   190	12	5	localThrowable	Throwable
    // Exception table:
    //   from	to	target	type
    //   103	107	109	java/lang/Throwable
    //   24	37	118	java/io/IOException
    //   103	107	118	java/io/IOException
    //   110	118	118	java/io/IOException
    //   142	146	118	java/io/IOException
    //   146	148	118	java/io/IOException
    //   162	166	118	java/io/IOException
    //   224	228	118	java/io/IOException
    //   231	239	118	java/io/IOException
    //   239	243	118	java/io/IOException
    //   246	251	118	java/io/IOException
    //   254	258	118	java/io/IOException
    //   88	93	121	java/lang/Throwable
    //   37	43	130	java/lang/Throwable
    //   47	54	130	java/lang/Throwable
    //   54	67	130	java/lang/Throwable
    //   122	130	130	java/lang/Throwable
    //   148	153	130	java/lang/Throwable
    //   188	190	130	java/lang/Throwable
    //   192	198	130	java/lang/Throwable
    //   201	206	130	java/lang/Throwable
    //   209	214	130	java/lang/Throwable
    //   131	133	133	finally
    //   37	43	156	finally
    //   47	54	156	finally
    //   54	67	156	finally
    //   88	93	156	finally
    //   122	130	156	finally
    //   148	153	156	finally
    //   183	188	156	finally
    //   188	190	156	finally
    //   192	198	156	finally
    //   201	206	156	finally
    //   209	214	156	finally
    //   69	77	168	java/lang/Throwable
    //   69	77	173	finally
    //   171	173	173	finally
    //   183	188	190	java/lang/Throwable
    //   224	228	230	java/lang/Throwable
    //   142	146	245	java/lang/Throwable
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v4/graphics/TypefaceCompatApi21Impl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */