package com.google.firebase;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.internal.zzaak;

public class zza
  implements zzaak
{
  public Exception zzO(Status paramStatus)
  {
    if (paramStatus.getStatusCode() == 8) {
      return new FirebaseException(paramStatus.zzxh());
    }
    return new FirebaseApiNotAvailableException(paramStatus.zzxh());
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/firebase/zza.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */