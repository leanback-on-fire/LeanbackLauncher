package com.bumptech.glide.load.resource.bitmap;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import com.bumptech.glide.load.EncodeStrategy;
import com.bumptech.glide.load.Option;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceEncoder;

public class BitmapEncoder
  implements ResourceEncoder<Bitmap>
{
  public static final Option<Bitmap.CompressFormat> COMPRESSION_FORMAT = Option.memory("com.bumptech.glide.load.resource.bitmap.BitmapEncoder.CompressionFormat");
  public static final Option<Integer> COMPRESSION_QUALITY = Option.memory("com.bumptech.glide.load.resource.bitmap.BitmapEncoder.CompressionQuality", Integer.valueOf(90));
  private static final String TAG = "BitmapEncoder";
  
  private Bitmap.CompressFormat getFormat(Bitmap paramBitmap, Options paramOptions)
  {
    paramOptions = (Bitmap.CompressFormat)paramOptions.get(COMPRESSION_FORMAT);
    if (paramOptions != null) {
      return paramOptions;
    }
    if (paramBitmap.hasAlpha()) {
      return Bitmap.CompressFormat.PNG;
    }
    return Bitmap.CompressFormat.JPEG;
  }
  
  /* Error */
  public boolean encode(com.bumptech.glide.load.engine.Resource<Bitmap> paramResource, java.io.File paramFile, Options paramOptions)
  {
    // Byte code:
    //   0: aload_1
    //   1: invokeinterface 77 1 0
    //   6: checkcast 57	android/graphics/Bitmap
    //   9: astore 11
    //   11: invokestatic 83	com/bumptech/glide/util/LogTime:getLogTime	()J
    //   14: lstore 7
    //   16: aload_0
    //   17: aload 11
    //   19: aload_3
    //   20: invokespecial 85	com/bumptech/glide/load/resource/bitmap/BitmapEncoder:getFormat	(Landroid/graphics/Bitmap;Lcom/bumptech/glide/load/Options;)Landroid/graphics/Bitmap$CompressFormat;
    //   23: astore 12
    //   25: aload_3
    //   26: getstatic 34	com/bumptech/glide/load/resource/bitmap/BitmapEncoder:COMPRESSION_QUALITY	Lcom/bumptech/glide/load/Option;
    //   29: invokevirtual 53	com/bumptech/glide/load/Options:get	(Lcom/bumptech/glide/load/Option;)Ljava/lang/Object;
    //   32: checkcast 22	java/lang/Integer
    //   35: invokevirtual 89	java/lang/Integer:intValue	()I
    //   38: istore 6
    //   40: iconst_0
    //   41: istore 10
    //   43: aconst_null
    //   44: astore_1
    //   45: aconst_null
    //   46: astore_3
    //   47: new 91	java/io/FileOutputStream
    //   50: dup
    //   51: aload_2
    //   52: invokespecial 94	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
    //   55: astore_2
    //   56: aload 11
    //   58: aload 12
    //   60: iload 6
    //   62: aload_2
    //   63: invokevirtual 98	android/graphics/Bitmap:compress	(Landroid/graphics/Bitmap$CompressFormat;ILjava/io/OutputStream;)Z
    //   66: pop
    //   67: aload_2
    //   68: invokevirtual 103	java/io/OutputStream:close	()V
    //   71: iconst_1
    //   72: istore 9
    //   74: aload_2
    //   75: ifnull +179 -> 254
    //   78: aload_2
    //   79: invokevirtual 103	java/io/OutputStream:close	()V
    //   82: ldc 16
    //   84: iconst_2
    //   85: invokestatic 109	android/util/Log:isLoggable	(Ljava/lang/String;I)Z
    //   88: ifeq +78 -> 166
    //   91: aload 12
    //   93: invokestatic 114	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   96: astore_1
    //   97: aload 11
    //   99: invokestatic 120	com/bumptech/glide/util/Util:getBitmapByteSize	(Landroid/graphics/Bitmap;)I
    //   102: istore 6
    //   104: lload 7
    //   106: invokestatic 124	com/bumptech/glide/util/LogTime:getElapsedMillis	(J)D
    //   109: dstore 4
    //   111: ldc 16
    //   113: new 126	java/lang/StringBuilder
    //   116: dup
    //   117: aload_1
    //   118: invokestatic 114	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   121: invokevirtual 129	java/lang/String:length	()I
    //   124: bipush 70
    //   126: iadd
    //   127: invokespecial 132	java/lang/StringBuilder:<init>	(I)V
    //   130: ldc -122
    //   132: invokevirtual 138	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   135: aload_1
    //   136: invokevirtual 138	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   139: ldc -116
    //   141: invokevirtual 138	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   144: iload 6
    //   146: invokevirtual 143	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   149: ldc -111
    //   151: invokevirtual 138	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   154: dload 4
    //   156: invokevirtual 148	java/lang/StringBuilder:append	(D)Ljava/lang/StringBuilder;
    //   159: invokevirtual 152	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   162: invokestatic 156	android/util/Log:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   165: pop
    //   166: iload 9
    //   168: ireturn
    //   169: astore_1
    //   170: goto -88 -> 82
    //   173: astore_1
    //   174: aload_3
    //   175: astore_2
    //   176: aload_1
    //   177: astore_3
    //   178: aload_2
    //   179: astore_1
    //   180: ldc 16
    //   182: iconst_3
    //   183: invokestatic 109	android/util/Log:isLoggable	(Ljava/lang/String;I)Z
    //   186: ifeq +14 -> 200
    //   189: aload_2
    //   190: astore_1
    //   191: ldc 16
    //   193: ldc -98
    //   195: aload_3
    //   196: invokestatic 162	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   199: pop
    //   200: iload 10
    //   202: istore 9
    //   204: aload_2
    //   205: ifnull -123 -> 82
    //   208: aload_2
    //   209: invokevirtual 103	java/io/OutputStream:close	()V
    //   212: iload 10
    //   214: istore 9
    //   216: goto -134 -> 82
    //   219: astore_1
    //   220: iload 10
    //   222: istore 9
    //   224: goto -142 -> 82
    //   227: astore_2
    //   228: aload_1
    //   229: ifnull +7 -> 236
    //   232: aload_1
    //   233: invokevirtual 103	java/io/OutputStream:close	()V
    //   236: aload_2
    //   237: athrow
    //   238: astore_1
    //   239: goto -3 -> 236
    //   242: astore_3
    //   243: aload_2
    //   244: astore_1
    //   245: aload_3
    //   246: astore_2
    //   247: goto -19 -> 228
    //   250: astore_3
    //   251: goto -73 -> 178
    //   254: goto -172 -> 82
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	257	0	this	BitmapEncoder
    //   0	257	1	paramResource	com.bumptech.glide.load.engine.Resource<Bitmap>
    //   0	257	2	paramFile	java.io.File
    //   0	257	3	paramOptions	Options
    //   109	46	4	d	double
    //   38	107	6	i	int
    //   14	91	7	l	long
    //   72	151	9	bool1	boolean
    //   41	180	10	bool2	boolean
    //   9	89	11	localBitmap	Bitmap
    //   23	69	12	localCompressFormat	Bitmap.CompressFormat
    // Exception table:
    //   from	to	target	type
    //   78	82	169	java/io/IOException
    //   47	56	173	java/io/IOException
    //   208	212	219	java/io/IOException
    //   47	56	227	finally
    //   180	189	227	finally
    //   191	200	227	finally
    //   232	236	238	java/io/IOException
    //   56	71	242	finally
    //   56	71	250	java/io/IOException
  }
  
  public EncodeStrategy getEncodeStrategy(Options paramOptions)
  {
    return EncodeStrategy.TRANSFORMED;
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/load/resource/bitmap/BitmapEncoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */