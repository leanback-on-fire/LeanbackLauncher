package com.google.android.gms.phenotype;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.zzaa;

public class FlagOverride
  extends zza
{
  public static final Parcelable.Creator<FlagOverride> CREATOR = new zzf();
  public final boolean committed;
  public final String configurationName;
  public final Flag flag;
  public final String userName;
  
  public FlagOverride(String paramString1, String paramString2, Flag paramFlag, boolean paramBoolean)
  {
    this.configurationName = paramString1;
    this.userName = paramString2;
    this.flag = paramFlag;
    this.committed = paramBoolean;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {}
    do
    {
      return true;
      if (!(paramObject instanceof FlagOverride)) {
        return false;
      }
      paramObject = (FlagOverride)paramObject;
    } while ((zzaa.equal(this.configurationName, ((FlagOverride)paramObject).configurationName)) && (zzaa.equal(this.userName, ((FlagOverride)paramObject).userName)) && (zzaa.equal(this.flag, ((FlagOverride)paramObject).flag)) && (this.committed == ((FlagOverride)paramObject).committed));
    return false;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    toString(localStringBuilder);
    return localStringBuilder.toString();
  }
  
  public String toString(StringBuilder paramStringBuilder)
  {
    paramStringBuilder.append("FlagOverride(");
    paramStringBuilder.append(this.configurationName);
    paramStringBuilder.append(", ");
    paramStringBuilder.append(this.userName);
    paramStringBuilder.append(", ");
    this.flag.toString(paramStringBuilder);
    paramStringBuilder.append(", ");
    paramStringBuilder.append(this.committed);
    paramStringBuilder.append(")");
    return paramStringBuilder.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zzf.zza(this, paramParcel, paramInt);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/phenotype/FlagOverride.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */