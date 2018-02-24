package com.google.android.gms.internal;

import android.content.Context;
import android.util.Log;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class zzcbu
  implements Runnable
{
  public final Context mContext;
  public final zzcby wR;
  public final zzcbv wZ;
  public final zzcbv xa;
  public final zzcbv xb;
  
  public zzcbu(Context paramContext, zzcbv paramzzcbv1, zzcbv paramzzcbv2, zzcbv paramzzcbv3, zzcby paramzzcby)
  {
    this.mContext = paramContext;
    this.wZ = paramzzcbv1;
    this.xa = paramzzcbv2;
    this.xb = paramzzcbv3;
    this.wR = paramzzcby;
  }
  
  private zzcbz.zza zza(zzcbv paramzzcbv)
  {
    zzcbz.zza localzza = new zzcbz.zza();
    Object localObject1;
    if (paramzzcbv.zzaky() != null)
    {
      localObject1 = paramzzcbv.zzaky();
      ArrayList localArrayList1 = new ArrayList();
      Iterator localIterator1 = ((Map)localObject1).keySet().iterator();
      while (localIterator1.hasNext())
      {
        String str1 = (String)localIterator1.next();
        ArrayList localArrayList2 = new ArrayList();
        Object localObject2 = (Map)((Map)localObject1).get(str1);
        Iterator localIterator2 = ((Map)localObject2).keySet().iterator();
        while (localIterator2.hasNext())
        {
          String str2 = (String)localIterator2.next();
          zzcbz.zzb localzzb = new zzcbz.zzb();
          localzzb.zzaA = str2;
          localzzb.xl = ((byte[])((Map)localObject2).get(str2));
          localArrayList2.add(localzzb);
        }
        localObject2 = new zzcbz.zzd();
        ((zzcbz.zzd)localObject2).zzaTl = str1;
        ((zzcbz.zzd)localObject2).xq = ((zzcbz.zzb[])localArrayList2.toArray(new zzcbz.zzb[localArrayList2.size()]));
        localArrayList1.add(localObject2);
      }
      localzza.xi = ((zzcbz.zzd[])localArrayList1.toArray(new zzcbz.zzd[localArrayList1.size()]));
    }
    if (paramzzcbv.zzBm() != null)
    {
      localObject1 = paramzzcbv.zzBm();
      localzza.xj = ((byte[][])((List)localObject1).toArray(new byte[((List)localObject1).size()][]));
    }
    localzza.timestamp = paramzzcbv.getTimestamp();
    return localzza;
  }
  
  public void run()
  {
    Object localObject1 = new zzcbz.zze();
    if (this.wZ != null) {
      ((zzcbz.zze)localObject1).xr = zza(this.wZ);
    }
    if (this.xa != null) {
      ((zzcbz.zze)localObject1).xs = zza(this.xa);
    }
    if (this.xb != null) {
      ((zzcbz.zze)localObject1).xt = zza(this.xb);
    }
    Object localObject2;
    if (this.wR != null)
    {
      localObject2 = new zzcbz.zzc();
      ((zzcbz.zzc)localObject2).xm = this.wR.getLastFetchStatus();
      ((zzcbz.zzc)localObject2).xn = this.wR.isDeveloperModeEnabled();
      ((zzcbz.zzc)localObject2).xo = this.wR.zzakD();
      ((zzcbz.zze)localObject1).xu = ((zzcbz.zzc)localObject2);
    }
    if ((this.wR != null) && (this.wR.zzakB() != null))
    {
      localObject2 = new ArrayList();
      Map localMap = this.wR.zzakB();
      Iterator localIterator = localMap.keySet().iterator();
      while (localIterator.hasNext())
      {
        String str = (String)localIterator.next();
        if (localMap.get(str) != null)
        {
          zzcbz.zzf localzzf = new zzcbz.zzf();
          localzzf.zzaTl = str;
          localzzf.xx = ((zzcbs)localMap.get(str)).zzakx();
          localzzf.resourceId = ((zzcbs)localMap.get(str)).zzakw();
          ((List)localObject2).add(localzzf);
        }
      }
      ((zzcbz.zze)localObject1).xv = ((zzcbz.zzf[])((List)localObject2).toArray(new zzcbz.zzf[((List)localObject2).size()]));
    }
    localObject1 = zzcgg.zzf((zzcgg)localObject1);
    try
    {
      localObject2 = this.mContext.openFileOutput("persisted_config", 0);
      ((FileOutputStream)localObject2).write((byte[])localObject1);
      ((FileOutputStream)localObject2).close();
      return;
    }
    catch (IOException localIOException)
    {
      Log.e("AsyncPersisterTask", "Could not persist config.", localIOException);
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/internal/zzcbu.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */