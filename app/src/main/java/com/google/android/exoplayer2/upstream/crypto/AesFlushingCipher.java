package com.google.android.exoplayer2.upstream.crypto;

import com.google.android.exoplayer2.util.Assertions;
import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public final class AesFlushingCipher
{
  private final int blockSize;
  private final Cipher cipher;
  private final byte[] flushedBlock;
  private int pendingXorBytes;
  private final byte[] zerosBlock;
  
  public AesFlushingCipher(int paramInt, byte[] paramArrayOfByte, long paramLong1, long paramLong2)
  {
    try
    {
      this.cipher = Cipher.getInstance("AES/CTR/NoPadding");
      this.blockSize = this.cipher.getBlockSize();
      this.zerosBlock = new byte[this.blockSize];
      this.flushedBlock = new byte[this.blockSize];
      long l = paramLong2 / this.blockSize;
      int i = (int)(paramLong2 % this.blockSize);
      this.cipher.init(paramInt, new SecretKeySpec(paramArrayOfByte, this.cipher.getAlgorithm().split("/")[0]), new IvParameterSpec(getInitializationVector(paramLong1, l)));
      if (i != 0) {
        updateInPlace(new byte[i], 0, i);
      }
      return;
    }
    catch (InvalidAlgorithmParameterException paramArrayOfByte)
    {
      throw new RuntimeException(paramArrayOfByte);
    }
    catch (NoSuchAlgorithmException paramArrayOfByte)
    {
      for (;;) {}
    }
    catch (NoSuchPaddingException paramArrayOfByte)
    {
      for (;;) {}
    }
    catch (InvalidKeyException paramArrayOfByte)
    {
      for (;;) {}
    }
  }
  
  private byte[] getInitializationVector(long paramLong1, long paramLong2)
  {
    return ByteBuffer.allocate(16).putLong(paramLong1).putLong(paramLong2).array();
  }
  
  private int nonFlushingUpdate(byte[] paramArrayOfByte1, int paramInt1, int paramInt2, byte[] paramArrayOfByte2, int paramInt3)
  {
    try
    {
      paramInt1 = this.cipher.update(paramArrayOfByte1, paramInt1, paramInt2, paramArrayOfByte2, paramInt3);
      return paramInt1;
    }
    catch (ShortBufferException paramArrayOfByte1)
    {
      throw new RuntimeException(paramArrayOfByte1);
    }
  }
  
  public void update(byte[] paramArrayOfByte1, int paramInt1, int paramInt2, byte[] paramArrayOfByte2, int paramInt3)
  {
    do
    {
      if (this.pendingXorBytes <= 0) {
        break;
      }
      paramArrayOfByte2[paramInt3] = ((byte)(paramArrayOfByte1[paramInt1] ^ this.flushedBlock[(this.blockSize - this.pendingXorBytes)]));
      paramInt3 += 1;
      paramInt1 += 1;
      this.pendingXorBytes -= 1;
      i = paramInt2 - 1;
      paramInt2 = i;
    } while (i != 0);
    do
    {
      return;
      paramInt1 = nonFlushingUpdate(paramArrayOfByte1, paramInt1, paramInt2, paramArrayOfByte2, paramInt3);
    } while (paramInt2 == paramInt1);
    int i = paramInt2 - paramInt1;
    if (i < this.blockSize)
    {
      bool = true;
      Assertions.checkState(bool);
      this.pendingXorBytes = (this.blockSize - i);
      if (nonFlushingUpdate(this.zerosBlock, 0, this.pendingXorBytes, this.flushedBlock, 0) != this.blockSize) {
        break label188;
      }
    }
    label188:
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkState(bool);
      paramInt2 = 0;
      paramInt1 = paramInt3 + paramInt1;
      while (paramInt2 < i)
      {
        paramArrayOfByte2[paramInt1] = this.flushedBlock[paramInt2];
        paramInt2 += 1;
        paramInt1 += 1;
      }
      bool = false;
      break;
    }
  }
  
  public void updateInPlace(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    update(paramArrayOfByte, paramInt1, paramInt2, paramArrayOfByte, paramInt1);
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/upstream/crypto/AesFlushingCipher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */