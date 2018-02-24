package com.google.android.gms.internal;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public class zzcbo
{
  private static final AtomicReference<zzcbo> hF = new AtomicReference();
  
  zzcbo(Context paramContext) {}
  
  @Nullable
  public static zzcbo zzakr()
  {
    return (zzcbo)hF.get();
  }
  
  public static zzcbo zzcG(Context paramContext)
  {
    hF.compareAndSet(null, new zzcbo(paramContext));
    return (zzcbo)hF.get();
  }
  
  public Set<String> zzaks()
  {
    return Collections.emptySet();
  }
  
  public void zzg(@NonNull FirebaseApp paramFirebaseApp) {}
  
  public FirebaseOptions zzmv(@NonNull String paramString)
  {
    return null;
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/internal/zzcbo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */