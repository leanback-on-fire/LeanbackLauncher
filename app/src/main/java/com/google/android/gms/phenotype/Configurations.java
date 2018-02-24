package com.google.android.gms.phenotype;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.zzaa;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class Configurations
  extends zza
{
  public static final Parcelable.Creator<Configurations> CREATOR = new zzb();
  private static final Charset UTF_8 = Charset.forName("UTF-8");
  public final Map<Integer, Configuration> configurationMap;
  public final Configuration[] configurations;
  public final byte[] experimentToken;
  public final boolean isDelta;
  public final String serverToken;
  public final String snapshotToken;
  
  public Configurations(String paramString1, String paramString2, Configuration[] paramArrayOfConfiguration, boolean paramBoolean)
  {
    this(paramString1, paramString2, paramArrayOfConfiguration, paramBoolean, null);
  }
  
  public Configurations(String paramString1, String paramString2, Configuration[] paramArrayOfConfiguration, boolean paramBoolean, byte[] paramArrayOfByte)
  {
    this.snapshotToken = paramString1;
    this.serverToken = paramString2;
    this.configurations = paramArrayOfConfiguration;
    this.isDelta = paramBoolean;
    this.experimentToken = paramArrayOfByte;
    this.configurationMap = new TreeMap();
    int j = paramArrayOfConfiguration.length;
    int i = 0;
    while (i < j)
    {
      paramString1 = paramArrayOfConfiguration[i];
      this.configurationMap.put(Integer.valueOf(paramString1.flagType), paramString1);
      i += 1;
    }
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (paramObject != null)
    {
      bool1 = bool2;
      if ((paramObject instanceof Configurations))
      {
        paramObject = (Configurations)paramObject;
        bool1 = bool2;
        if (zzaa.equal(this.snapshotToken, ((Configurations)paramObject).snapshotToken))
        {
          bool1 = bool2;
          if (zzaa.equal(this.serverToken, ((Configurations)paramObject).serverToken))
          {
            bool1 = bool2;
            if (zzaa.equal(this.configurationMap, ((Configurations)paramObject).configurationMap))
            {
              bool1 = bool2;
              if (this.isDelta == ((Configurations)paramObject).isDelta)
              {
                bool1 = bool2;
                if (Arrays.equals(this.experimentToken, ((Configurations)paramObject).experimentToken)) {
                  bool1 = true;
                }
              }
            }
          }
        }
      }
    }
    return bool1;
  }
  
  public int hashCode()
  {
    return zzaa.hashCode(new Object[] { this.snapshotToken, this.serverToken, this.configurationMap, Boolean.valueOf(this.isDelta), this.experimentToken });
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder("Configurations(");
    localStringBuilder.append(this.snapshotToken);
    localStringBuilder.append('\'');
    localStringBuilder.append(", ");
    localStringBuilder.append('\'');
    localStringBuilder.append(this.serverToken);
    localStringBuilder.append('\'');
    localStringBuilder.append(", ");
    localStringBuilder.append('(');
    Object localObject = this.configurationMap.values().iterator();
    while (((Iterator)localObject).hasNext())
    {
      localStringBuilder.append((Configuration)((Iterator)localObject).next());
      localStringBuilder.append(", ");
    }
    localStringBuilder.append(')');
    localStringBuilder.append(", ");
    localStringBuilder.append(this.isDelta);
    localStringBuilder.append(", ");
    if (this.experimentToken == null) {}
    for (localObject = "null";; localObject = new String(this.experimentToken, UTF_8))
    {
      localStringBuilder.append((String)localObject);
      localStringBuilder.append(')');
      return localStringBuilder.toString();
    }
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zzb.zza(this, paramParcel, paramInt);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/phenotype/Configurations.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */