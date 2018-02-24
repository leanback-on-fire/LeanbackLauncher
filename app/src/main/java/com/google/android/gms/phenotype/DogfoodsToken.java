package com.google.android.gms.phenotype;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;

public class DogfoodsToken
  extends zza
{
  public static final Parcelable.Creator<DogfoodsToken> CREATOR = new zzc();
  public final byte[] token;
  
  public DogfoodsToken(byte[] paramArrayOfByte)
  {
    this.token = paramArrayOfByte;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zzc.zza(this, paramParcel, paramInt);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/phenotype/DogfoodsToken.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */