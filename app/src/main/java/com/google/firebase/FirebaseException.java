package com.google.firebase;

import android.support.annotation.NonNull;
import com.google.android.gms.common.internal.zzac;

public class FirebaseException
  extends Exception
{
  @Deprecated
  protected FirebaseException() {}
  
  public FirebaseException(@NonNull String paramString)
  {
    super(zzac.zzi(paramString, "Detail message must not be empty"));
  }
  
  public FirebaseException(@NonNull String paramString, Throwable paramThrowable)
  {
    super(zzac.zzi(paramString, "Detail message must not be empty"), paramThrowable);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/firebase/FirebaseException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */