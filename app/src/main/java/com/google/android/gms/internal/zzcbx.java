package com.google.android.gms.internal;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigValue;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class zzcbx
  implements FirebaseRemoteConfigValue
{
  public static final Charset UTF_8 = Charset.forName("UTF-8");
  public static final Pattern zzaVw = Pattern.compile("^(1|true|t|yes|y|on)$", 2);
  public static final Pattern zzaVx = Pattern.compile("^(0|false|f|no|n|off|)$", 2);
  private final int zzBo;
  private final byte[] zzaVN;
  
  public zzcbx(byte[] paramArrayOfByte, int paramInt)
  {
    this.zzaVN = paramArrayOfByte;
    this.zzBo = paramInt;
  }
  
  private void zzakA()
  {
    if (this.zzaVN == null) {
      throw new IllegalArgumentException("Value is null, and cannot be converted to the desired type.");
    }
  }
  
  public boolean asBoolean()
    throws IllegalArgumentException
  {
    if (this.zzBo == 0) {}
    String str;
    do
    {
      return false;
      str = asString();
      if (zzaVw.matcher(str).matches()) {
        return true;
      }
    } while (zzaVx.matcher(str).matches());
    throw new IllegalArgumentException(String.valueOf(str).length() + 45 + "[Value: " + str + "] cannot be interpreted as a boolean.");
  }
  
  public byte[] asByteArray()
  {
    if (this.zzBo == 0) {
      return FirebaseRemoteConfig.DEFAULT_VALUE_FOR_BYTE_ARRAY;
    }
    return this.zzaVN;
  }
  
  public double asDouble()
  {
    if (this.zzBo == 0) {
      return 0.0D;
    }
    String str = asString();
    try
    {
      double d = Double.valueOf(str).doubleValue();
      return d;
    }
    catch (NumberFormatException localNumberFormatException)
    {
      throw new IllegalArgumentException(String.valueOf(str).length() + 42 + "[Value: " + str + "] cannot be converted to a double.", localNumberFormatException);
    }
  }
  
  public long asLong()
  {
    if (this.zzBo == 0) {
      return 0L;
    }
    String str = asString();
    try
    {
      long l = Long.valueOf(str).longValue();
      return l;
    }
    catch (NumberFormatException localNumberFormatException)
    {
      throw new IllegalArgumentException(String.valueOf(str).length() + 40 + "[Value: " + str + "] cannot be converted to a long.", localNumberFormatException);
    }
  }
  
  public String asString()
  {
    if (this.zzBo == 0) {
      return "";
    }
    zzakA();
    return new String(this.zzaVN, UTF_8);
  }
  
  public int getSource()
  {
    return this.zzBo;
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/internal/zzcbx.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */