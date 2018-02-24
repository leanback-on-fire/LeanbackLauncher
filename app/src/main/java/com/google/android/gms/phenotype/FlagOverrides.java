package com.google.android.gms.phenotype;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import java.util.Iterator;
import java.util.List;

public class FlagOverrides
  extends zza
{
  public static final Parcelable.Creator<FlagOverrides> CREATOR = new zzg();
  public final List<FlagOverride> overrides;
  
  public FlagOverrides(List<FlagOverride> paramList)
  {
    this.overrides = paramList;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof FlagOverrides)) {
      return false;
    }
    paramObject = (FlagOverrides)paramObject;
    return this.overrides.equals(((FlagOverrides)paramObject).overrides);
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder("FlagOverrides(");
    Iterator localIterator = this.overrides.iterator();
    int i = 1;
    while (localIterator.hasNext())
    {
      FlagOverride localFlagOverride = (FlagOverride)localIterator.next();
      if (i == 0) {
        localStringBuilder.append(", ");
      }
      i = 0;
      localFlagOverride.toString(localStringBuilder);
    }
    localStringBuilder.append(")");
    return localStringBuilder.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zzg.zza(this, paramParcel, paramInt);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/phenotype/FlagOverrides.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */