package com.bumptech.glide.load.model;

import com.bumptech.glide.load.Encoder;
import com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool;
import java.io.InputStream;

public class StreamEncoder
  implements Encoder<InputStream>
{
  private static final String TAG = "StreamEncoder";
  private final ArrayPool byteArrayPool;
  
  public StreamEncoder(ArrayPool paramArrayPool)
  {
    this.byteArrayPool = paramArrayPool;
  }
  
  /* Error */
  public boolean encode(InputStream paramInputStream, java.io.File paramFile, com.bumptech.glide.load.Options paramOptions)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 20	com/bumptech/glide/load/model/StreamEncoder:byteArrayPool	Lcom/bumptech/glide/load/engine/bitmap_recycle/ArrayPool;
    //   4: ldc 26
    //   6: ldc 28
    //   8: invokeinterface 34 3 0
    //   13: checkcast 28	[B
    //   16: astore 6
    //   18: aconst_null
    //   19: astore_3
    //   20: aconst_null
    //   21: astore 5
    //   23: new 36	java/io/FileOutputStream
    //   26: dup
    //   27: aload_2
    //   28: invokespecial 39	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
    //   31: astore_2
    //   32: aload_1
    //   33: aload 6
    //   35: invokevirtual 45	java/io/InputStream:read	([B)I
    //   38: istore 4
    //   40: iload 4
    //   42: iconst_m1
    //   43: if_icmpeq +65 -> 108
    //   46: aload_2
    //   47: aload 6
    //   49: iconst_0
    //   50: iload 4
    //   52: invokevirtual 51	java/io/OutputStream:write	([BII)V
    //   55: goto -23 -> 32
    //   58: astore_3
    //   59: aload_2
    //   60: astore_1
    //   61: aload_3
    //   62: astore_2
    //   63: aload_1
    //   64: astore_3
    //   65: ldc 11
    //   67: iconst_3
    //   68: invokestatic 57	android/util/Log:isLoggable	(Ljava/lang/String;I)Z
    //   71: ifeq +14 -> 85
    //   74: aload_1
    //   75: astore_3
    //   76: ldc 11
    //   78: ldc 59
    //   80: aload_2
    //   81: invokestatic 63	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   84: pop
    //   85: aload_1
    //   86: ifnull +7 -> 93
    //   89: aload_1
    //   90: invokevirtual 66	java/io/OutputStream:close	()V
    //   93: aload_0
    //   94: getfield 20	com/bumptech/glide/load/model/StreamEncoder:byteArrayPool	Lcom/bumptech/glide/load/engine/bitmap_recycle/ArrayPool;
    //   97: aload 6
    //   99: ldc 28
    //   101: invokeinterface 70 3 0
    //   106: iconst_0
    //   107: ireturn
    //   108: aload_2
    //   109: invokevirtual 66	java/io/OutputStream:close	()V
    //   112: aload_2
    //   113: ifnull +7 -> 120
    //   116: aload_2
    //   117: invokevirtual 66	java/io/OutputStream:close	()V
    //   120: aload_0
    //   121: getfield 20	com/bumptech/glide/load/model/StreamEncoder:byteArrayPool	Lcom/bumptech/glide/load/engine/bitmap_recycle/ArrayPool;
    //   124: aload 6
    //   126: ldc 28
    //   128: invokeinterface 70 3 0
    //   133: iconst_1
    //   134: ireturn
    //   135: astore_1
    //   136: aload_3
    //   137: ifnull +7 -> 144
    //   140: aload_3
    //   141: invokevirtual 66	java/io/OutputStream:close	()V
    //   144: aload_0
    //   145: getfield 20	com/bumptech/glide/load/model/StreamEncoder:byteArrayPool	Lcom/bumptech/glide/load/engine/bitmap_recycle/ArrayPool;
    //   148: aload 6
    //   150: ldc 28
    //   152: invokeinterface 70 3 0
    //   157: aload_1
    //   158: athrow
    //   159: astore_1
    //   160: goto -40 -> 120
    //   163: astore_1
    //   164: goto -71 -> 93
    //   167: astore_2
    //   168: goto -24 -> 144
    //   171: astore_1
    //   172: aload_2
    //   173: astore_3
    //   174: goto -38 -> 136
    //   177: astore_2
    //   178: aload 5
    //   180: astore_1
    //   181: goto -118 -> 63
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	184	0	this	StreamEncoder
    //   0	184	1	paramInputStream	InputStream
    //   0	184	2	paramFile	java.io.File
    //   0	184	3	paramOptions	com.bumptech.glide.load.Options
    //   38	13	4	i	int
    //   21	158	5	localObject	Object
    //   16	133	6	arrayOfByte	byte[]
    // Exception table:
    //   from	to	target	type
    //   32	40	58	java/io/IOException
    //   46	55	58	java/io/IOException
    //   108	112	58	java/io/IOException
    //   23	32	135	finally
    //   65	74	135	finally
    //   76	85	135	finally
    //   116	120	159	java/io/IOException
    //   89	93	163	java/io/IOException
    //   140	144	167	java/io/IOException
    //   32	40	171	finally
    //   46	55	171	finally
    //   108	112	171	finally
    //   23	32	177	java/io/IOException
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/load/model/StreamEncoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */