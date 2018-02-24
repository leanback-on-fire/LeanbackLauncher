package com.google.android.gms.usagereporting;

import android.os.Parcel;
import android.os.Parcelable.Creator;

public class UsageReportingOptInOptions
  extends com.google.android.gms.common.internal.safeparcel.zza
{
  public static final Parcelable.Creator<UsageReportingOptInOptions> CREATOR = new zza();
  public static final int OPTION_NO_CHANGE = 0;
  public static final int OPTION_OPT_IN = 1;
  public static final int OPTION_OPT_OUT = 2;
  public static final int VERSION_CODE = 1;
  final int mVersionCode;
  int zzcFO;
  
  public UsageReportingOptInOptions(int paramInt)
  {
    this(1, paramInt);
  }
  
  UsageReportingOptInOptions(int paramInt1, int paramInt2)
  {
    this.mVersionCode = paramInt1;
    this.zzcFO = paramInt2;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zza.zza(this, paramParcel, paramInt);
  }
  
  public int zzaaR()
  {
    return this.zzcFO;
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/usagereporting/UsageReportingOptInOptions.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */