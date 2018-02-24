package com.google.android.gms.internal;

import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.annotation.WorkerThread;
import android.support.v4.util.ArrayMap;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.util.Clock;
import com.google.android.gms.measurement.AppMeasurement.zzd;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class zzate
  extends zzauj
{
  private final Map<String, Long> zzbLi = new ArrayMap();
  private final Map<String, Integer> zzbLj = new ArrayMap();
  private long zzbLk;
  
  public zzate(zzauh paramzzauh)
  {
    super(paramzzauh);
  }
  
  @WorkerThread
  private void zza(long paramLong, AppMeasurement.zzd paramzzd)
  {
    if (paramzzd == null)
    {
      zzMg().zzNZ().log("Not logging ad exposure. No active activity");
      return;
    }
    if (paramLong < 1000L)
    {
      zzMg().zzNZ().zzm("Not logging ad exposure. Less than 1000 ms. exposure", Long.valueOf(paramLong));
      return;
    }
    Bundle localBundle = new Bundle();
    localBundle.putLong("_xt", paramLong);
    zzaun.zza(paramzzd, localBundle);
    zzLV().zzd("am", "_xa", localBundle);
  }
  
  @WorkerThread
  private void zza(String paramString, long paramLong, AppMeasurement.zzd paramzzd)
  {
    if (paramzzd == null)
    {
      zzMg().zzNZ().log("Not logging ad unit exposure. No active activity");
      return;
    }
    if (paramLong < 1000L)
    {
      zzMg().zzNZ().zzm("Not logging ad unit exposure. Less than 1000 ms. exposure", Long.valueOf(paramLong));
      return;
    }
    Bundle localBundle = new Bundle();
    localBundle.putString("_ai", paramString);
    localBundle.putLong("_xt", paramLong);
    zzaun.zza(paramzzd, localBundle);
    zzLV().zzd("am", "_xu", localBundle);
  }
  
  @WorkerThread
  private void zzai(long paramLong)
  {
    Iterator localIterator = this.zzbLi.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      this.zzbLi.put(str, Long.valueOf(paramLong));
    }
    if (!this.zzbLi.isEmpty()) {
      this.zzbLk = paramLong;
    }
  }
  
  @WorkerThread
  private void zzf(String paramString, long paramLong)
  {
    zzLR();
    zzmW();
    zzac.zzdc(paramString);
    if (this.zzbLj.isEmpty()) {
      this.zzbLk = paramLong;
    }
    Integer localInteger = (Integer)this.zzbLj.get(paramString);
    if (localInteger != null)
    {
      this.zzbLj.put(paramString, Integer.valueOf(localInteger.intValue() + 1));
      return;
    }
    if (this.zzbLj.size() >= 100)
    {
      zzMg().zzNV().log("Too many ads visible");
      return;
    }
    this.zzbLj.put(paramString, Integer.valueOf(1));
    this.zzbLi.put(paramString, Long.valueOf(paramLong));
  }
  
  @WorkerThread
  private void zzg(String paramString, long paramLong)
  {
    zzLR();
    zzmW();
    zzac.zzdc(paramString);
    Object localObject = (Integer)this.zzbLj.get(paramString);
    if (localObject != null)
    {
      zzaun.zza localzza = zzLZ().zzOP();
      int i = ((Integer)localObject).intValue() - 1;
      if (i == 0)
      {
        this.zzbLj.remove(paramString);
        localObject = (Long)this.zzbLi.get(paramString);
        if (localObject == null) {
          zzMg().zzNT().log("First ad unit exposure time was never set");
        }
        for (;;)
        {
          if (this.zzbLj.isEmpty())
          {
            if (this.zzbLk != 0L) {
              break;
            }
            zzMg().zzNT().log("First ad exposure time was never set");
          }
          return;
          long l = ((Long)localObject).longValue();
          this.zzbLi.remove(paramString);
          zza(paramString, paramLong - l, localzza);
        }
        zza(paramLong - this.zzbLk, localzza);
        this.zzbLk = 0L;
        return;
      }
      this.zzbLj.put(paramString, Integer.valueOf(i));
      return;
    }
    zzMg().zzNT().zzm("Call to endAdUnitExposure for unknown ad unit id", paramString);
  }
  
  public void beginAdUnitExposure(final String paramString)
  {
    int i = Build.VERSION.SDK_INT;
    if ((paramString == null) || (paramString.length() == 0))
    {
      zzMg().zzNT().log("Ad unit id must be a non-empty string");
      return;
    }
    final long l = zznq().elapsedRealtime();
    zzMf().zzp(new Runnable()
    {
      public void run()
      {
        zzate.zza(zzate.this, paramString, l);
      }
    });
  }
  
  public void endAdUnitExposure(final String paramString)
  {
    int i = Build.VERSION.SDK_INT;
    if ((paramString == null) || (paramString.length() == 0))
    {
      zzMg().zzNT().log("Ad unit id must be a non-empty string");
      return;
    }
    final long l = zznq().elapsedRealtime();
    zzMf().zzp(new Runnable()
    {
      public void run()
      {
        zzate.zzb(zzate.this, paramString, l);
      }
    });
  }
  
  public void zzLP()
  {
    final long l = zznq().elapsedRealtime();
    zzMf().zzp(new Runnable()
    {
      public void run()
      {
        zzate.zza(zzate.this, l);
      }
    });
  }
  
  @WorkerThread
  public void zzah(long paramLong)
  {
    zzaun.zza localzza = zzLZ().zzOP();
    Iterator localIterator = this.zzbLi.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      zza(str, paramLong - ((Long)this.zzbLi.get(str)).longValue(), localzza);
    }
    if (!this.zzbLi.isEmpty()) {
      zza(paramLong - this.zzbLk, localzza);
    }
    zzai(paramLong);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/internal/zzate.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */