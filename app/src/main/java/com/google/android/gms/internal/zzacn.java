package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.data.DataHolder;
import com.google.android.gms.common.internal.safeparcel.zza;

public class zzacn
  extends zza
{
  public static final Parcelable.Creator<zzacn> CREATOR = new zzaco();
  private final int zzaGj;
  private final long zzaVH;
  private final DataHolder zzaVW;
  private final DataHolder zzaVX;
  
  public zzacn(int paramInt, DataHolder paramDataHolder1, long paramLong, DataHolder paramDataHolder2)
  {
    this.zzaGj = paramInt;
    this.zzaVW = paramDataHolder1;
    this.zzaVH = paramLong;
    this.zzaVX = paramDataHolder2;
  }
  
  public int getStatusCode()
  {
    return this.zzaGj;
  }
  
  public long getThrottleEndTimeMillis()
  {
    return this.zzaVH;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zzaco.zza(this, paramParcel, paramInt);
  }
  
  public DataHolder zzBw()
  {
    return this.zzaVW;
  }
  
  public DataHolder zzBx()
  {
    return this.zzaVX;
  }
  
  public void zzBy()
  {
    if ((this.zzaVW != null) && (!this.zzaVW.isClosed())) {
      this.zzaVW.close();
    }
  }
  
  public void zzBz()
  {
    if ((this.zzaVX != null) && (!this.zzaVX.isClosed())) {
      this.zzaVX.close();
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/internal/zzacn.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */