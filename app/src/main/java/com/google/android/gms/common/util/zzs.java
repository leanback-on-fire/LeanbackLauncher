package com.google.android.gms.common.util;

public class zzs
{
  public static long zzdi(String paramString)
  {
    if (paramString.length() > 16) {
      throw new NumberFormatException(String.valueOf(paramString).length() + 46 + "Invalid input: " + paramString + " exceeds " + 16 + " characters");
    }
    if (paramString.length() == 16) {
      return Long.parseLong(paramString.substring(8), 16) | Long.parseLong(paramString.substring(0, 8), 16) << 32;
    }
    return Long.parseLong(paramString, 16);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/common/util/zzs.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */