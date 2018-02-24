package com.google.android.gms.phenotype;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.zzaa;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class Configuration
  extends com.google.android.gms.common.internal.safeparcel.zza
  implements Comparable<Configuration>
{
  public static final Parcelable.Creator<Configuration> CREATOR = new zza();
  public final String[] deleteFlags;
  public final Map<String, Flag> flagMap;
  public final int flagType;
  public final Flag[] flags;
  
  public Configuration(int paramInt, Flag[] paramArrayOfFlag, String[] paramArrayOfString)
  {
    this.flagType = paramInt;
    this.flags = paramArrayOfFlag;
    this.flagMap = new TreeMap();
    int i = paramArrayOfFlag.length;
    paramInt = 0;
    while (paramInt < i)
    {
      Flag localFlag = paramArrayOfFlag[paramInt];
      this.flagMap.put(localFlag.name, localFlag);
      paramInt += 1;
    }
    this.deleteFlags = paramArrayOfString;
    if (this.deleteFlags != null) {
      Arrays.sort(this.deleteFlags);
    }
  }
  
  public int compareTo(Configuration paramConfiguration)
  {
    return this.flagType - paramConfiguration.flagType;
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (paramObject != null)
    {
      bool1 = bool2;
      if ((paramObject instanceof Configuration))
      {
        paramObject = (Configuration)paramObject;
        bool1 = bool2;
        if (this.flagType == ((Configuration)paramObject).flagType)
        {
          bool1 = bool2;
          if (zzaa.equal(this.flagMap, ((Configuration)paramObject).flagMap))
          {
            bool1 = bool2;
            if (Arrays.equals(this.deleteFlags, ((Configuration)paramObject).deleteFlags)) {
              bool1 = true;
            }
          }
        }
      }
    }
    return bool1;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder("Configuration(");
    localStringBuilder.append(this.flagType);
    localStringBuilder.append(", ");
    localStringBuilder.append("(");
    Object localObject = this.flagMap.values().iterator();
    while (((Iterator)localObject).hasNext())
    {
      localStringBuilder.append((Flag)((Iterator)localObject).next());
      localStringBuilder.append(", ");
    }
    localStringBuilder.append(")");
    localStringBuilder.append(", ");
    localStringBuilder.append("(");
    if (this.deleteFlags != null)
    {
      localObject = this.deleteFlags;
      int j = localObject.length;
      int i = 0;
      while (i < j)
      {
        localStringBuilder.append(localObject[i]);
        localStringBuilder.append(", ");
        i += 1;
      }
    }
    localStringBuilder.append("null");
    localStringBuilder.append(")");
    localStringBuilder.append(")");
    return localStringBuilder.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zza.zza(this, paramParcel, paramInt);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/phenotype/Configuration.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */