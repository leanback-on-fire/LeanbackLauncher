package com.google.android.gms.internal;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import java.util.Iterator;
import java.util.Set;

public class zzatr
  extends zza
  implements Iterable<String>
{
  public static final Parcelable.Creator<zzatr> CREATOR = new zzats();
  private final Bundle zzbMz;
  
  zzatr(Bundle paramBundle)
  {
    this.zzbMz = paramBundle;
  }
  
  Object get(String paramString)
  {
    return this.zzbMz.get(paramString);
  }
  
  public Iterator<String> iterator()
  {
    new Iterator()
    {
      Iterator<String> zzbMA = zzatr.zza(zzatr.this).keySet().iterator();
      
      public boolean hasNext()
      {
        return this.zzbMA.hasNext();
      }
      
      public String next()
      {
        return (String)this.zzbMA.next();
      }
      
      public void remove()
      {
        throw new UnsupportedOperationException("Remove not supported");
      }
    };
  }
  
  public int size()
  {
    return this.zzbMz.size();
  }
  
  public String toString()
  {
    return this.zzbMz.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zzats.zza(this, paramParcel, paramInt);
  }
  
  public Bundle zzNR()
  {
    return new Bundle(this.zzbMz);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/internal/zzatr.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */