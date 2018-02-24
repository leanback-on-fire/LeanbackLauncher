package com.bumptech.glide.load.resource.bitmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.ParcelFileDescriptor;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Option;
import com.bumptech.glide.load.Option.CacheKeyUpdater;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import java.nio.ByteBuffer;
import java.security.MessageDigest;

public class VideoBitmapDecoder
  implements ResourceDecoder<ParcelFileDescriptor, Bitmap>
{
  private static final MediaMetadataRetrieverFactory DEFAULT_FACTORY = new MediaMetadataRetrieverFactory();
  public static final long DEFAULT_FRAME = -1L;
  public static final Option<Integer> FRAME_OPTION;
  public static final Option<Long> TARGET_FRAME = Option.disk("com.bumptech.glide.load.resource.bitmap.VideoBitmapDecode.TargetFrame", Long.valueOf(-1L), new Option.CacheKeyUpdater()
  {
    private final ByteBuffer buffer = ByteBuffer.allocate(8);
    
    public void update(byte[] arg1, Long paramAnonymousLong, MessageDigest paramAnonymousMessageDigest)
    {
      paramAnonymousMessageDigest.update(???);
      synchronized (this.buffer)
      {
        this.buffer.position(0);
        paramAnonymousMessageDigest.update(this.buffer.putLong(paramAnonymousLong.longValue()).array());
        return;
      }
    }
  });
  private final BitmapPool bitmapPool;
  private final MediaMetadataRetrieverFactory factory;
  
  static
  {
    FRAME_OPTION = Option.disk("com.bumptech.glide.load.resource.bitmap.VideoBitmapDecode.FrameOption", null, new Option.CacheKeyUpdater()
    {
      private final ByteBuffer buffer = ByteBuffer.allocate(4);
      
      public void update(byte[] arg1, Integer paramAnonymousInteger, MessageDigest paramAnonymousMessageDigest)
      {
        if (paramAnonymousInteger == null) {
          return;
        }
        paramAnonymousMessageDigest.update(???);
        synchronized (this.buffer)
        {
          this.buffer.position(0);
          paramAnonymousMessageDigest.update(this.buffer.putInt(paramAnonymousInteger.intValue()).array());
          return;
        }
      }
    });
  }
  
  public VideoBitmapDecoder(Context paramContext)
  {
    this(Glide.get(paramContext).getBitmapPool());
  }
  
  public VideoBitmapDecoder(BitmapPool paramBitmapPool)
  {
    this(paramBitmapPool, DEFAULT_FACTORY);
  }
  
  VideoBitmapDecoder(BitmapPool paramBitmapPool, MediaMetadataRetrieverFactory paramMediaMetadataRetrieverFactory)
  {
    this.bitmapPool = paramBitmapPool;
    this.factory = paramMediaMetadataRetrieverFactory;
  }
  
  /* Error */
  public com.bumptech.glide.load.engine.Resource<Bitmap> decode(ParcelFileDescriptor paramParcelFileDescriptor, int paramInt1, int paramInt2, Options paramOptions)
    throws java.io.IOException
  {
    // Byte code:
    //   0: aload 4
    //   2: getstatic 49	com/bumptech/glide/load/resource/bitmap/VideoBitmapDecoder:TARGET_FRAME	Lcom/bumptech/glide/load/Option;
    //   5: invokevirtual 89	com/bumptech/glide/load/Options:get	(Lcom/bumptech/glide/load/Option;)Ljava/lang/Object;
    //   8: checkcast 34	java/lang/Long
    //   11: invokevirtual 93	java/lang/Long:longValue	()J
    //   14: lstore 5
    //   16: lload 5
    //   18: lconst_0
    //   19: lcmp
    //   20: ifge +42 -> 62
    //   23: lload 5
    //   25: ldc2_w 19
    //   28: lcmp
    //   29: ifeq +33 -> 62
    //   32: new 95	java/lang/IllegalArgumentException
    //   35: dup
    //   36: new 97	java/lang/StringBuilder
    //   39: dup
    //   40: bipush 83
    //   42: invokespecial 100	java/lang/StringBuilder:<init>	(I)V
    //   45: ldc 102
    //   47: invokevirtual 106	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   50: lload 5
    //   52: invokevirtual 109	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   55: invokevirtual 113	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   58: invokespecial 116	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   61: athrow
    //   62: aload 4
    //   64: getstatic 54	com/bumptech/glide/load/resource/bitmap/VideoBitmapDecoder:FRAME_OPTION	Lcom/bumptech/glide/load/Option;
    //   67: invokevirtual 89	com/bumptech/glide/load/Options:get	(Lcom/bumptech/glide/load/Option;)Ljava/lang/Object;
    //   70: checkcast 118	java/lang/Integer
    //   73: astore 4
    //   75: aload_0
    //   76: getfield 80	com/bumptech/glide/load/resource/bitmap/VideoBitmapDecoder:factory	Lcom/bumptech/glide/load/resource/bitmap/VideoBitmapDecoder$MediaMetadataRetrieverFactory;
    //   79: invokevirtual 122	com/bumptech/glide/load/resource/bitmap/VideoBitmapDecoder$MediaMetadataRetrieverFactory:build	()Landroid/media/MediaMetadataRetriever;
    //   82: astore 7
    //   84: aload 7
    //   86: aload_1
    //   87: invokevirtual 128	android/os/ParcelFileDescriptor:getFileDescriptor	()Ljava/io/FileDescriptor;
    //   90: invokevirtual 134	android/media/MediaMetadataRetriever:setDataSource	(Ljava/io/FileDescriptor;)V
    //   93: lload 5
    //   95: ldc2_w 19
    //   98: lcmp
    //   99: ifne +29 -> 128
    //   102: aload 7
    //   104: invokevirtual 138	android/media/MediaMetadataRetriever:getFrameAtTime	()Landroid/graphics/Bitmap;
    //   107: astore 4
    //   109: aload 7
    //   111: invokevirtual 141	android/media/MediaMetadataRetriever:release	()V
    //   114: aload_1
    //   115: invokevirtual 144	android/os/ParcelFileDescriptor:close	()V
    //   118: aload 4
    //   120: aload_0
    //   121: getfield 78	com/bumptech/glide/load/resource/bitmap/VideoBitmapDecoder:bitmapPool	Lcom/bumptech/glide/load/engine/bitmap_recycle/BitmapPool;
    //   124: invokestatic 150	com/bumptech/glide/load/resource/bitmap/BitmapResource:obtain	(Landroid/graphics/Bitmap;Lcom/bumptech/glide/load/engine/bitmap_recycle/BitmapPool;)Lcom/bumptech/glide/load/resource/bitmap/BitmapResource;
    //   127: areturn
    //   128: aload 4
    //   130: ifnonnull +15 -> 145
    //   133: aload 7
    //   135: lload 5
    //   137: invokevirtual 153	android/media/MediaMetadataRetriever:getFrameAtTime	(J)Landroid/graphics/Bitmap;
    //   140: astore 4
    //   142: goto -33 -> 109
    //   145: aload 7
    //   147: lload 5
    //   149: aload 4
    //   151: invokevirtual 157	java/lang/Integer:intValue	()I
    //   154: invokevirtual 160	android/media/MediaMetadataRetriever:getFrameAtTime	(JI)Landroid/graphics/Bitmap;
    //   157: astore 4
    //   159: goto -50 -> 109
    //   162: astore_1
    //   163: aload 7
    //   165: invokevirtual 141	android/media/MediaMetadataRetriever:release	()V
    //   168: aload_1
    //   169: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	170	0	this	VideoBitmapDecoder
    //   0	170	1	paramParcelFileDescriptor	ParcelFileDescriptor
    //   0	170	2	paramInt1	int
    //   0	170	3	paramInt2	int
    //   0	170	4	paramOptions	Options
    //   14	134	5	l	long
    //   82	82	7	localMediaMetadataRetriever	MediaMetadataRetriever
    // Exception table:
    //   from	to	target	type
    //   84	93	162	finally
    //   102	109	162	finally
    //   133	142	162	finally
    //   145	159	162	finally
  }
  
  public boolean handles(ParcelFileDescriptor paramParcelFileDescriptor, Options paramOptions)
  {
    paramOptions = this.factory.build();
    try
    {
      paramOptions.setDataSource(paramParcelFileDescriptor.getFileDescriptor());
      paramOptions.release();
      return true;
    }
    catch (RuntimeException paramParcelFileDescriptor)
    {
      paramParcelFileDescriptor = paramParcelFileDescriptor;
      paramOptions.release();
      return false;
    }
    finally
    {
      paramParcelFileDescriptor = finally;
      paramOptions.release();
      throw paramParcelFileDescriptor;
    }
  }
  
  static class MediaMetadataRetrieverFactory
  {
    public MediaMetadataRetriever build()
    {
      return new MediaMetadataRetriever();
    }
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/load/resource/bitmap/VideoBitmapDecoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */