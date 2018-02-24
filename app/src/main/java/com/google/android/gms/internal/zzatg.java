package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.zzac;

public class zzatg
  extends zza
{
  public static final Parcelable.Creator<zzatg> CREATOR = new zzath();
  public final String packageName;
  public final String zzbAI;
  public final String zzbLI;
  public final String zzbLJ;
  public final long zzbLK;
  public final long zzbLL;
  public final String zzbLM;
  public final boolean zzbLN;
  public final boolean zzbLO;
  public final long zzbLP;
  public final String zzbLQ;
  public final long zzbLR;
  
  zzatg(String paramString1, String paramString2, String paramString3, long paramLong1, String paramString4, long paramLong2, long paramLong3, String paramString5, boolean paramBoolean1, boolean paramBoolean2, String paramString6, long paramLong4)
  {
    zzac.zzdc(paramString1);
    this.packageName = paramString1;
    paramString1 = paramString2;
    if (TextUtils.isEmpty(paramString2)) {
      paramString1 = null;
    }
    this.zzbLI = paramString1;
    this.zzbAI = paramString3;
    this.zzbLP = paramLong1;
    this.zzbLJ = paramString4;
    this.zzbLK = paramLong2;
    this.zzbLL = paramLong3;
    this.zzbLM = paramString5;
    this.zzbLN = paramBoolean1;
    this.zzbLO = paramBoolean2;
    this.zzbLQ = paramString6;
    this.zzbLR = paramLong4;
  }
  
  zzatg(String paramString1, String paramString2, String paramString3, String paramString4, long paramLong1, long paramLong2, String paramString5, boolean paramBoolean1, boolean paramBoolean2, long paramLong3, String paramString6, long paramLong4)
  {
    this.packageName = paramString1;
    this.zzbLI = paramString2;
    this.zzbAI = paramString3;
    this.zzbLP = paramLong3;
    this.zzbLJ = paramString4;
    this.zzbLK = paramLong1;
    this.zzbLL = paramLong2;
    this.zzbLM = paramString5;
    this.zzbLN = paramBoolean1;
    this.zzbLO = paramBoolean2;
    this.zzbLQ = paramString6;
    this.zzbLR = paramLong4;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zzath.zza(this, paramParcel, paramInt);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/internal/zzatg.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */