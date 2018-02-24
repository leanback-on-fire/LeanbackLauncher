package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.data.DataHolder;
import com.google.android.gms.common.internal.safeparcel.zza;
import java.util.List;

public class zzacl
  extends zza
{
  public static final Parcelable.Creator<zzacl> CREATOR = new zzacm();
  private final String mPackageName;
  private final long zzaVQ;
  private final DataHolder zzaVR;
  private final String zzaVS;
  private final String zzaVT;
  private final String zzaVU;
  private final List<String> zzaVV;
  private final int zzaVs;
  private final List<zzacb> zzaVt;
  private final int zzaVu;
  private final int zzaVv;
  
  public zzacl(String paramString1, long paramLong, DataHolder paramDataHolder, String paramString2, String paramString3, String paramString4, List<String> paramList, int paramInt1, List<zzacb> paramList1, int paramInt2, int paramInt3)
  {
    this.mPackageName = paramString1;
    this.zzaVQ = paramLong;
    this.zzaVR = paramDataHolder;
    this.zzaVS = paramString2;
    this.zzaVT = paramString3;
    this.zzaVU = paramString4;
    this.zzaVV = paramList;
    this.zzaVs = paramInt1;
    this.zzaVt = paramList1;
    this.zzaVv = paramInt2;
    this.zzaVu = paramInt3;
  }
  
  public String getAppInstanceId()
  {
    return this.zzaVT;
  }
  
  public String getPackageName()
  {
    return this.mPackageName;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zzacm.zza(this, paramParcel, paramInt);
  }
  
  public int zzBj()
  {
    return this.zzaVs;
  }
  
  public int zzBk()
  {
    return this.zzaVv;
  }
  
  public int zzBl()
  {
    return this.zzaVu;
  }
  
  public long zzBq()
  {
    return this.zzaVQ;
  }
  
  public DataHolder zzBr()
  {
    return this.zzaVR;
  }
  
  public String zzBs()
  {
    return this.zzaVS;
  }
  
  public String zzBt()
  {
    return this.zzaVU;
  }
  
  public List<String> zzBu()
  {
    return this.zzaVV;
  }
  
  public List<zzacb> zzBv()
  {
    return this.zzaVt;
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/internal/zzacl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */