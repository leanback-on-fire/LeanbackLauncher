package com.google.android.gms.phenotype;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.zzaa;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Comparator;

public class Flag
  extends zza
  implements Comparable<Flag>
{
  public static final Parcelable.Creator<Flag> CREATOR = new zze();
  public static final NonValueComparator NON_VALUE_COMPARATOR = new NonValueComparator();
  private static final Charset UTF_8 = Charset.forName("UTF-8");
  public static final int VALUE_TYPE_BOOLEAN = 2;
  public static final int VALUE_TYPE_BYTES = 5;
  public static final int VALUE_TYPE_DOUBLE = 3;
  public static final int VALUE_TYPE_LONG = 1;
  public static final int VALUE_TYPE_STRING = 4;
  final boolean booleanValue;
  final double doubleValue;
  public final int flagStorageType;
  public final int flagValueType;
  public final String name;
  final String stringValue;
  final long zzceZ;
  final byte[] zzcfa;
  
  public Flag(String paramString, double paramDouble, int paramInt)
  {
    this(paramString, 0L, false, paramDouble, "", new byte[0], 3, paramInt);
  }
  
  public Flag(String paramString, long paramLong, int paramInt)
  {
    this(paramString, paramLong, false, 0.0D, "", new byte[0], 1, paramInt);
  }
  
  public Flag(String paramString1, long paramLong, boolean paramBoolean, double paramDouble, String paramString2, byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    this.name = paramString1;
    this.zzceZ = paramLong;
    this.booleanValue = paramBoolean;
    this.doubleValue = paramDouble;
    this.stringValue = paramString2;
    this.zzcfa = paramArrayOfByte;
    this.flagValueType = paramInt1;
    this.flagStorageType = paramInt2;
  }
  
  public Flag(String paramString1, String paramString2, int paramInt)
  {
    this(paramString1, 0L, false, 0.0D, paramString2, new byte[0], 4, paramInt);
  }
  
  public Flag(String paramString, boolean paramBoolean, int paramInt)
  {
    this(paramString, 0L, paramBoolean, 0.0D, "", new byte[0], 2, paramInt);
  }
  
  public Flag(String paramString, byte[] paramArrayOfByte, int paramInt)
  {
    this(paramString, 0L, false, 0.0D, "", paramArrayOfByte, 5, paramInt);
  }
  
  private static int compare(byte paramByte1, byte paramByte2)
  {
    return paramByte1 - paramByte2;
  }
  
  private static int compare(int paramInt1, int paramInt2)
  {
    if (paramInt1 < paramInt2) {
      return -1;
    }
    if (paramInt1 == paramInt2) {
      return 0;
    }
    return 1;
  }
  
  private static int compare(long paramLong1, long paramLong2)
  {
    if (paramLong1 < paramLong2) {
      return -1;
    }
    if (paramLong1 == paramLong2) {
      return 0;
    }
    return 1;
  }
  
  private static int compare(String paramString1, String paramString2)
  {
    if (paramString1 == paramString2) {
      return 0;
    }
    if (paramString1 == null) {
      return -1;
    }
    if (paramString2 == null) {
      return 1;
    }
    return paramString1.compareTo(paramString2);
  }
  
  private static int compare(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (paramBoolean1 == paramBoolean2) {
      return 0;
    }
    if (paramBoolean1) {
      return 1;
    }
    return -1;
  }
  
  public String asString()
  {
    switch (this.flagValueType)
    {
    default: 
      int i = this.flagValueType;
      throw new AssertionError(31 + "Invalid enum value: " + i);
    case 1: 
      return Long.toString(this.zzceZ);
    case 2: 
      if (this.booleanValue) {
        return "true";
      }
      return "false";
    case 3: 
      return Double.toString(this.doubleValue);
    case 4: 
      return this.stringValue;
    }
    return new String(this.zzcfa, UTF_8);
  }
  
  public int compareTo(Flag paramFlag)
  {
    int j = 0;
    int i = 0;
    int k = this.name.compareTo(paramFlag.name);
    if (k != 0) {
      i = k;
    }
    do
    {
      return i;
      k = compare(this.flagValueType, paramFlag.flagValueType);
      if (k != 0) {
        return k;
      }
      switch (this.flagValueType)
      {
      default: 
        i = this.flagValueType;
        throw new AssertionError(31 + "Invalid enum value: " + i);
      case 1: 
        return compare(this.zzceZ, paramFlag.zzceZ);
      case 2: 
        return compare(this.booleanValue, paramFlag.booleanValue);
      case 3: 
        return Double.compare(this.doubleValue, paramFlag.doubleValue);
      case 4: 
        return compare(this.stringValue, paramFlag.stringValue);
      }
    } while (this.zzcfa == paramFlag.zzcfa);
    if (this.zzcfa == null) {
      return -1;
    }
    i = j;
    if (paramFlag.zzcfa == null) {
      return 1;
    }
    do
    {
      i += 1;
      if (i >= Math.min(this.zzcfa.length, paramFlag.zzcfa.length)) {
        break;
      }
      j = compare(this.zzcfa[i], paramFlag.zzcfa[i]);
    } while (j == 0);
    return j;
    return compare(this.zzcfa.length, paramFlag.zzcfa.length);
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool = true;
    if ((paramObject != null) && ((paramObject instanceof Flag)))
    {
      paramObject = (Flag)paramObject;
      if ((!zzaa.equal(this.name, ((Flag)paramObject).name)) || (this.flagValueType != ((Flag)paramObject).flagValueType) || (this.flagStorageType != ((Flag)paramObject).flagStorageType)) {
        bool = false;
      }
      do
      {
        do
        {
          do
          {
            return bool;
            switch (this.flagValueType)
            {
            default: 
              int i = this.flagValueType;
              throw new AssertionError(31 + "Invalid enum value: " + i);
            }
          } while (this.zzceZ == ((Flag)paramObject).zzceZ);
          return false;
        } while (this.booleanValue == ((Flag)paramObject).booleanValue);
        return false;
      } while (this.doubleValue == ((Flag)paramObject).doubleValue);
      return false;
      return zzaa.equal(this.stringValue, ((Flag)paramObject).stringValue);
      return Arrays.equals(this.zzcfa, ((Flag)paramObject).zzcfa);
    }
    return false;
  }
  
  public boolean getBoolean()
  {
    if (this.flagValueType != 2) {
      throw new IllegalArgumentException("Not a boolean type");
    }
    return this.booleanValue;
  }
  
  public byte[] getBytesValue()
  {
    if (this.flagValueType != 5) {
      throw new IllegalArgumentException("Not a bytes type");
    }
    return this.zzcfa;
  }
  
  public double getDouble()
  {
    if (this.flagValueType != 3) {
      throw new IllegalArgumentException("Not a double type");
    }
    return this.doubleValue;
  }
  
  public long getLong()
  {
    if (this.flagValueType != 1) {
      throw new IllegalArgumentException("Not a long type");
    }
    return this.zzceZ;
  }
  
  public String getString()
  {
    if (this.flagValueType != 4) {
      throw new IllegalArgumentException("Not a String type");
    }
    return this.stringValue;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    toString(localStringBuilder);
    return localStringBuilder.toString();
  }
  
  public String toString(StringBuilder paramStringBuilder)
  {
    paramStringBuilder.append("Flag(");
    paramStringBuilder.append(this.name);
    paramStringBuilder.append(", ");
    switch (this.flagValueType)
    {
    default: 
      paramStringBuilder = this.name;
      int i = this.flagValueType;
      throw new AssertionError(String.valueOf(paramStringBuilder).length() + 27 + "Invalid type: " + paramStringBuilder + ", " + i);
    case 1: 
      paramStringBuilder.append(this.zzceZ);
    }
    for (;;)
    {
      paramStringBuilder.append(", ");
      paramStringBuilder.append(this.flagValueType);
      paramStringBuilder.append(", ");
      paramStringBuilder.append(this.flagStorageType);
      paramStringBuilder.append(")");
      return paramStringBuilder.toString();
      paramStringBuilder.append(this.booleanValue);
      continue;
      paramStringBuilder.append(this.doubleValue);
      continue;
      paramStringBuilder.append("'");
      paramStringBuilder.append(this.stringValue);
      paramStringBuilder.append("'");
      continue;
      if (this.zzcfa == null)
      {
        paramStringBuilder.append("null");
      }
      else
      {
        paramStringBuilder.append("'");
        paramStringBuilder.append(new String(this.zzcfa, UTF_8));
        paramStringBuilder.append("'");
      }
    }
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zze.zza(this, paramParcel, paramInt);
  }
  
  public static class NonValueComparator
    implements Comparator<Flag>
  {
    public int compare(Flag paramFlag1, Flag paramFlag2)
    {
      if (paramFlag1.flagStorageType == paramFlag2.flagStorageType) {
        return paramFlag1.name.compareTo(paramFlag2.name);
      }
      return paramFlag1.flagStorageType - paramFlag2.flagStorageType;
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/phenotype/Flag.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */