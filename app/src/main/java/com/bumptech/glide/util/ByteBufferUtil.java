package com.bumptech.glide.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicReference;

public final class ByteBufferUtil
{
  private static final AtomicReference<byte[]> BUFFER_REF = new AtomicReference();
  private static final int BUFFER_SIZE = 16384;
  
  /* Error */
  public static ByteBuffer fromFile(java.io.File paramFile)
    throws IOException
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_1
    //   2: aconst_null
    //   3: astore 4
    //   5: aconst_null
    //   6: astore_2
    //   7: new 33	java/io/RandomAccessFile
    //   10: dup
    //   11: aload_0
    //   12: ldc 35
    //   14: invokespecial 38	java/io/RandomAccessFile:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   17: astore_3
    //   18: aload 4
    //   20: astore_1
    //   21: aload_3
    //   22: invokevirtual 42	java/io/RandomAccessFile:getChannel	()Ljava/nio/channels/FileChannel;
    //   25: astore_2
    //   26: aload_2
    //   27: astore_1
    //   28: aload_2
    //   29: getstatic 48	java/nio/channels/FileChannel$MapMode:READ_ONLY	Ljava/nio/channels/FileChannel$MapMode;
    //   32: lconst_0
    //   33: aload_0
    //   34: invokevirtual 54	java/io/File:length	()J
    //   37: invokevirtual 60	java/nio/channels/FileChannel:map	(Ljava/nio/channels/FileChannel$MapMode;JJ)Ljava/nio/MappedByteBuffer;
    //   40: invokevirtual 66	java/nio/MappedByteBuffer:load	()Ljava/nio/MappedByteBuffer;
    //   43: astore_0
    //   44: aload_2
    //   45: ifnull +7 -> 52
    //   48: aload_2
    //   49: invokevirtual 69	java/nio/channels/FileChannel:close	()V
    //   52: aload_3
    //   53: ifnull +7 -> 60
    //   56: aload_3
    //   57: invokevirtual 70	java/io/RandomAccessFile:close	()V
    //   60: aload_0
    //   61: areturn
    //   62: astore_0
    //   63: aload_2
    //   64: ifnull +7 -> 71
    //   67: aload_2
    //   68: invokevirtual 69	java/nio/channels/FileChannel:close	()V
    //   71: aload_1
    //   72: ifnull +7 -> 79
    //   75: aload_1
    //   76: invokevirtual 70	java/io/RandomAccessFile:close	()V
    //   79: aload_0
    //   80: athrow
    //   81: astore_1
    //   82: goto -30 -> 52
    //   85: astore_1
    //   86: aload_0
    //   87: areturn
    //   88: astore_2
    //   89: goto -18 -> 71
    //   92: astore_1
    //   93: goto -14 -> 79
    //   96: astore_0
    //   97: aload_1
    //   98: astore_2
    //   99: aload_3
    //   100: astore_1
    //   101: goto -38 -> 63
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	104	0	paramFile	java.io.File
    //   1	75	1	localObject1	Object
    //   81	1	1	localIOException1	IOException
    //   85	1	1	localIOException2	IOException
    //   92	6	1	localIOException3	IOException
    //   100	1	1	localObject2	Object
    //   6	62	2	localFileChannel	java.nio.channels.FileChannel
    //   88	1	2	localIOException4	IOException
    //   98	1	2	localIOException5	IOException
    //   17	83	3	localRandomAccessFile	java.io.RandomAccessFile
    //   3	16	4	localObject3	Object
    // Exception table:
    //   from	to	target	type
    //   7	18	62	finally
    //   48	52	81	java/io/IOException
    //   56	60	85	java/io/IOException
    //   67	71	88	java/io/IOException
    //   75	79	92	java/io/IOException
    //   21	26	96	finally
    //   28	44	96	finally
  }
  
  public static ByteBuffer fromStream(InputStream paramInputStream)
    throws IOException
  {
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream(16384);
    byte[] arrayOfByte2 = (byte[])BUFFER_REF.getAndSet(null);
    byte[] arrayOfByte1 = arrayOfByte2;
    if (arrayOfByte2 == null) {
      arrayOfByte1 = new byte['䀀'];
    }
    for (;;)
    {
      int i = paramInputStream.read(arrayOfByte1);
      if (i < 0) {
        break;
      }
      localByteArrayOutputStream.write(arrayOfByte1, 0, i);
    }
    BUFFER_REF.set(arrayOfByte1);
    paramInputStream = localByteArrayOutputStream.toByteArray();
    return (ByteBuffer)ByteBuffer.allocateDirect(paramInputStream.length).put(paramInputStream).position(0);
  }
  
  private static SafeArray getSafeArray(ByteBuffer paramByteBuffer)
  {
    if ((!paramByteBuffer.isReadOnly()) && (paramByteBuffer.hasArray())) {
      return new SafeArray(paramByteBuffer.array(), paramByteBuffer.arrayOffset(), paramByteBuffer.limit());
    }
    return null;
  }
  
  public static byte[] toBytes(ByteBuffer paramByteBuffer)
  {
    Object localObject = getSafeArray(paramByteBuffer);
    if ((localObject != null) && (((SafeArray)localObject).offset == 0) && (((SafeArray)localObject).limit == ((SafeArray)localObject).data.length)) {
      return paramByteBuffer.array();
    }
    paramByteBuffer = paramByteBuffer.asReadOnlyBuffer();
    localObject = new byte[paramByteBuffer.limit()];
    paramByteBuffer.position(0);
    paramByteBuffer.get((byte[])localObject);
    return (byte[])localObject;
  }
  
  /* Error */
  public static void toFile(ByteBuffer paramByteBuffer, java.io.File paramFile)
    throws IOException
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 4
    //   3: aconst_null
    //   4: astore 5
    //   6: aconst_null
    //   7: astore_2
    //   8: new 33	java/io/RandomAccessFile
    //   11: dup
    //   12: aload_1
    //   13: ldc -93
    //   15: invokespecial 38	java/io/RandomAccessFile:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   18: astore_3
    //   19: aload 5
    //   21: astore_1
    //   22: aload_3
    //   23: invokevirtual 42	java/io/RandomAccessFile:getChannel	()Ljava/nio/channels/FileChannel;
    //   26: astore_2
    //   27: aload_2
    //   28: astore_1
    //   29: aload_2
    //   30: aload_0
    //   31: invokevirtual 166	java/nio/channels/FileChannel:write	(Ljava/nio/ByteBuffer;)I
    //   34: pop
    //   35: aload_2
    //   36: astore_1
    //   37: aload_2
    //   38: iconst_0
    //   39: invokevirtual 170	java/nio/channels/FileChannel:force	(Z)V
    //   42: aload_2
    //   43: astore_1
    //   44: aload_2
    //   45: invokevirtual 69	java/nio/channels/FileChannel:close	()V
    //   48: aload_2
    //   49: astore_1
    //   50: aload_3
    //   51: invokevirtual 70	java/io/RandomAccessFile:close	()V
    //   54: aload_2
    //   55: ifnull +7 -> 62
    //   58: aload_2
    //   59: invokevirtual 69	java/nio/channels/FileChannel:close	()V
    //   62: aload_3
    //   63: ifnull +7 -> 70
    //   66: aload_3
    //   67: invokevirtual 70	java/io/RandomAccessFile:close	()V
    //   70: return
    //   71: astore_0
    //   72: aload 4
    //   74: astore_1
    //   75: aload_2
    //   76: ifnull +7 -> 83
    //   79: aload_2
    //   80: invokevirtual 69	java/nio/channels/FileChannel:close	()V
    //   83: aload_1
    //   84: ifnull +7 -> 91
    //   87: aload_1
    //   88: invokevirtual 70	java/io/RandomAccessFile:close	()V
    //   91: aload_0
    //   92: athrow
    //   93: astore_0
    //   94: goto -32 -> 62
    //   97: astore_0
    //   98: return
    //   99: astore_2
    //   100: goto -17 -> 83
    //   103: astore_1
    //   104: goto -13 -> 91
    //   107: astore 4
    //   109: aload_3
    //   110: astore_0
    //   111: aload_1
    //   112: astore_2
    //   113: aload_0
    //   114: astore_1
    //   115: aload 4
    //   117: astore_0
    //   118: goto -43 -> 75
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	121	0	paramByteBuffer	ByteBuffer
    //   0	121	1	paramFile	java.io.File
    //   7	73	2	localFileChannel	java.nio.channels.FileChannel
    //   99	1	2	localIOException	IOException
    //   112	1	2	localFile	java.io.File
    //   18	92	3	localRandomAccessFile	java.io.RandomAccessFile
    //   1	72	4	localObject1	Object
    //   107	9	4	localObject2	Object
    //   4	16	5	localObject3	Object
    // Exception table:
    //   from	to	target	type
    //   8	19	71	finally
    //   58	62	93	java/io/IOException
    //   66	70	97	java/io/IOException
    //   79	83	99	java/io/IOException
    //   87	91	103	java/io/IOException
    //   22	27	107	finally
    //   29	35	107	finally
    //   37	42	107	finally
    //   44	48	107	finally
    //   50	54	107	finally
  }
  
  public static InputStream toStream(ByteBuffer paramByteBuffer)
  {
    return new ByteBufferStream(paramByteBuffer);
  }
  
  public static void toStream(ByteBuffer paramByteBuffer, OutputStream paramOutputStream)
    throws IOException
  {
    Object localObject = getSafeArray(paramByteBuffer);
    if (localObject != null)
    {
      paramOutputStream.write(((SafeArray)localObject).data, ((SafeArray)localObject).offset, ((SafeArray)localObject).offset + ((SafeArray)localObject).limit);
      return;
    }
    byte[] arrayOfByte = (byte[])BUFFER_REF.getAndSet(null);
    localObject = arrayOfByte;
    if (arrayOfByte == null) {
      localObject = new byte['䀀'];
    }
    while (paramByteBuffer.remaining() > 0)
    {
      int i = Math.min(paramByteBuffer.remaining(), localObject.length);
      paramByteBuffer.get((byte[])localObject, 0, i);
      paramOutputStream.write((byte[])localObject, 0, i);
    }
    BUFFER_REF.set(localObject);
  }
  
  private static class ByteBufferStream
    extends InputStream
  {
    private static final int UNSET = -1;
    private final ByteBuffer byteBuffer;
    private int markPos = -1;
    
    public ByteBufferStream(ByteBuffer paramByteBuffer)
    {
      this.byteBuffer = paramByteBuffer;
    }
    
    public int available()
      throws IOException
    {
      return this.byteBuffer.remaining();
    }
    
    public void mark(int paramInt)
    {
      try
      {
        this.markPos = this.byteBuffer.position();
        return;
      }
      finally
      {
        localObject = finally;
        throw ((Throwable)localObject);
      }
    }
    
    public boolean markSupported()
    {
      return true;
    }
    
    public int read()
      throws IOException
    {
      if (!this.byteBuffer.hasRemaining()) {
        return -1;
      }
      return this.byteBuffer.get();
    }
    
    public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      throws IOException
    {
      if (!this.byteBuffer.hasRemaining()) {
        return -1;
      }
      paramInt2 = Math.min(paramInt2, available());
      this.byteBuffer.get(paramArrayOfByte, paramInt1, paramInt2);
      return paramInt2;
    }
    
    public void reset()
      throws IOException
    {
      try
      {
        if (this.markPos == -1) {
          throw new IOException("Cannot reset to unset mark position");
        }
      }
      finally {}
      this.byteBuffer.position(this.markPos);
    }
    
    public long skip(long paramLong)
      throws IOException
    {
      if (!this.byteBuffer.hasRemaining()) {
        return -1L;
      }
      paramLong = Math.min(paramLong, available());
      this.byteBuffer.position((int)(this.byteBuffer.position() + paramLong));
      return paramLong;
    }
  }
  
  static final class SafeArray
  {
    private final byte[] data;
    private final int limit;
    private final int offset;
    
    public SafeArray(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    {
      this.data = paramArrayOfByte;
      this.offset = paramInt1;
      this.limit = paramInt2;
    }
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/util/ByteBufferUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */