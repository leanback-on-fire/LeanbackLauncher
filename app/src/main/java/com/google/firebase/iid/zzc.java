package com.google.firebase.iid;

import android.support.annotation.Nullable;

@Deprecated
public class zzc
{
  private final FirebaseInstanceId wb;
  
  private zzc(FirebaseInstanceId paramFirebaseInstanceId)
  {
    this.wb = paramFirebaseInstanceId;
  }
  
  public static zzc zzake()
  {
    return new zzc(FirebaseInstanceId.getInstance());
  }
  
  public String getId()
  {
    return this.wb.getId();
  }
  
  @Nullable
  public String getToken()
  {
    return this.wb.getToken();
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/firebase/iid/zzc.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */