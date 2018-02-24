package com.google.android.exoplayer2.upstream.crypto;

final class CryptoUtil
{
  public static long getFNV64Hash(String paramString)
  {
    long l2;
    if (paramString == null)
    {
      l2 = 0L;
      return l2;
    }
    long l1 = 0L;
    int i = 0;
    for (;;)
    {
      l2 = l1;
      if (i >= paramString.length()) {
        break;
      }
      l1 ^= paramString.charAt(i);
      l1 += (l1 << 1) + (l1 << 4) + (l1 << 5) + (l1 << 7) + (l1 << 8) + (l1 << 40);
      i += 1;
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/upstream/crypto/CryptoUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */