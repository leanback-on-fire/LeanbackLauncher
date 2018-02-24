package com.google.android.gms.internal;

import android.accounts.Account;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.safeparcel.zza;

public class zzbgl
  extends zza
{
  public static final Parcelable.Creator<zzbgl> CREATOR = new zzbgm();
  final int mVersionCode;
  private final Account zzagc;
  private final String zzalL;
  private final Scope[] zzcqL;
  
  zzbgl(int paramInt, Account paramAccount, Scope[] paramArrayOfScope, String paramString)
  {
    this.mVersionCode = paramInt;
    this.zzagc = paramAccount;
    this.zzcqL = paramArrayOfScope;
    this.zzalL = paramString;
  }
  
  public Account getAccount()
  {
    return this.zzagc;
  }
  
  public String getServerClientId()
  {
    return this.zzalL;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zzbgm.zza(this, paramParcel, paramInt);
  }
  
  public Scope[] zzWf()
  {
    return this.zzcqL;
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/internal/zzbgl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */