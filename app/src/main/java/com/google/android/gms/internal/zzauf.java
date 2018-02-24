package com.google.android.gms.internal;

import android.support.annotation.WorkerThread;
import android.support.v4.util.ArrayMap;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.measurement.AppMeasurement.Event;
import java.io.IOException;
import java.util.Map;

public class zzauf
  extends zzauk
{
  private final Map<String, Map<String, Boolean>> zzbOA = new ArrayMap();
  private final Map<String, Map<String, Boolean>> zzbOB = new ArrayMap();
  private final Map<String, zzauy.zzb> zzbOC = new ArrayMap();
  private final Map<String, String> zzbOD = new ArrayMap();
  private final Map<String, Map<String, String>> zzbOz = new ArrayMap();
  
  zzauf(zzauh paramzzauh)
  {
    super(paramzzauh);
  }
  
  private Map<String, String> zza(zzauy.zzb paramzzb)
  {
    ArrayMap localArrayMap = new ArrayMap();
    if ((paramzzb != null) && (paramzzb.zzbRI != null))
    {
      paramzzb = paramzzb.zzbRI;
      int j = paramzzb.length;
      int i = 0;
      while (i < j)
      {
        Object localObject = paramzzb[i];
        if (localObject != null) {
          localArrayMap.put(((zzauy.zzc)localObject).zzaA, ((zzauy.zzc)localObject).value);
        }
        i += 1;
      }
    }
    return localArrayMap;
  }
  
  private void zza(String paramString, zzauy.zzb paramzzb)
  {
    ArrayMap localArrayMap1 = new ArrayMap();
    ArrayMap localArrayMap2 = new ArrayMap();
    if ((paramzzb != null) && (paramzzb.zzbRJ != null))
    {
      paramzzb = paramzzb.zzbRJ;
      int j = paramzzb.length;
      int i = 0;
      while (i < j)
      {
        Object localObject = paramzzb[i];
        if (localObject != null)
        {
          String str = (String)AppMeasurement.Event.zzbLb.get(((zzauy.zza)localObject).name);
          if (str != null) {
            ((zzauy.zza)localObject).name = str;
          }
          localArrayMap1.put(((zzauy.zza)localObject).name, ((zzauy.zza)localObject).zzaID);
          localArrayMap2.put(((zzauy.zza)localObject).name, ((zzauy.zza)localObject).zzbRF);
        }
        i += 1;
      }
    }
    this.zzbOA.put(paramString, localArrayMap1);
    this.zzbOB.put(paramString, localArrayMap2);
  }
  
  @WorkerThread
  private zzauy.zzb zze(String paramString, byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte == null) {
      return new zzauy.zzb();
    }
    paramArrayOfByte = zzcfx.zzar(paramArrayOfByte);
    zzauy.zzb localzzb = new zzauy.zzb();
    try
    {
      localzzb.mergeFrom(paramArrayOfByte);
      zzMg().zzNZ().zze("Parsed config. version, gmp_app_id", localzzb.zzbRG, localzzb.zzbLI);
      return localzzb;
    }
    catch (IOException paramArrayOfByte)
    {
      zzMg().zzNV().zze("Unable to merge remote config. appId", zzaua.zzfH(paramString), paramArrayOfByte);
    }
    return null;
  }
  
  @WorkerThread
  private void zzfN(String paramString)
  {
    zznA();
    zzmW();
    zzac.zzdc(paramString);
    if (this.zzbOC.get(paramString) == null)
    {
      localObject = zzMb().zzfz(paramString);
      if (localObject == null)
      {
        this.zzbOz.put(paramString, null);
        this.zzbOA.put(paramString, null);
        this.zzbOB.put(paramString, null);
        this.zzbOC.put(paramString, null);
        this.zzbOD.put(paramString, null);
      }
    }
    else
    {
      return;
    }
    Object localObject = zze(paramString, (byte[])localObject);
    this.zzbOz.put(paramString, zza((zzauy.zzb)localObject));
    zza(paramString, (zzauy.zzb)localObject);
    this.zzbOC.put(paramString, localObject);
    this.zzbOD.put(paramString, null);
  }
  
  protected void onInitialize() {}
  
  @WorkerThread
  protected boolean zza(String paramString1, byte[] paramArrayOfByte, String paramString2)
  {
    zznA();
    zzmW();
    zzac.zzdc(paramString1);
    zzauy.zzb localzzb = zze(paramString1, paramArrayOfByte);
    if (localzzb == null) {
      return false;
    }
    zza(paramString1, localzzb);
    this.zzbOC.put(paramString1, localzzb);
    this.zzbOD.put(paramString1, paramString2);
    this.zzbOz.put(paramString1, zza(localzzb));
    zzLU().zza(paramString1, localzzb.zzbRK);
    try
    {
      localzzb.zzbRK = null;
      paramString2 = new byte[localzzb.zzann()];
      localzzb.writeTo(zzcfy.zzas(paramString2));
      paramArrayOfByte = paramString2;
    }
    catch (IOException paramString2)
    {
      for (;;)
      {
        zzMg().zzNV().zze("Unable to serialize reduced-size config. Storing full config instead. appId", zzaua.zzfH(paramString1), paramString2);
      }
    }
    zzMb().zzd(paramString1, paramArrayOfByte);
    return true;
  }
  
  @WorkerThread
  String zzaj(String paramString1, String paramString2)
  {
    zzmW();
    zzfN(paramString1);
    paramString1 = (Map)this.zzbOz.get(paramString1);
    if (paramString1 != null) {
      return (String)paramString1.get(paramString2);
    }
    return null;
  }
  
  @WorkerThread
  boolean zzak(String paramString1, String paramString2)
  {
    zzmW();
    zzfN(paramString1);
    if ((zzMc().zzgj(paramString1)) && (zzauw.zzgg(paramString2))) {}
    while ((zzMc().zzgk(paramString1)) && (zzauw.zzfW(paramString2))) {
      return true;
    }
    paramString1 = (Map)this.zzbOA.get(paramString1);
    if (paramString1 != null)
    {
      paramString1 = (Boolean)paramString1.get(paramString2);
      if (paramString1 == null) {
        return false;
      }
      return paramString1.booleanValue();
    }
    return false;
  }
  
  @WorkerThread
  boolean zzal(String paramString1, String paramString2)
  {
    zzmW();
    zzfN(paramString1);
    paramString1 = (Map)this.zzbOB.get(paramString1);
    if (paramString1 != null)
    {
      paramString1 = (Boolean)paramString1.get(paramString2);
      if (paramString1 == null) {
        return false;
      }
      return paramString1.booleanValue();
    }
    return false;
  }
  
  @WorkerThread
  protected zzauy.zzb zzfO(String paramString)
  {
    zznA();
    zzmW();
    zzac.zzdc(paramString);
    zzfN(paramString);
    return (zzauy.zzb)this.zzbOC.get(paramString);
  }
  
  @WorkerThread
  protected String zzfP(String paramString)
  {
    zzmW();
    return (String)this.zzbOD.get(paramString);
  }
  
  @WorkerThread
  protected void zzfQ(String paramString)
  {
    zzmW();
    this.zzbOD.put(paramString, null);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/internal/zzauf.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */