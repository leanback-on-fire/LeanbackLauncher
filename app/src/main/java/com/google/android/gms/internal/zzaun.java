package com.google.android.gms.internal;

import android.app.Activity;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.Size;
import android.support.annotation.WorkerThread;
import android.support.v4.util.ArrayMap;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.util.Clock;
import com.google.android.gms.measurement.AppMeasurement.zzc;
import com.google.android.gms.measurement.AppMeasurement.zzd;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class zzaun
  extends zzauk
{
  private final Map<Activity, zza> zzadi = new ArrayMap();
  protected zza zzbQh;
  private volatile AppMeasurement.zzd zzbQi;
  private AppMeasurement.zzd zzbQj;
  private long zzbQk;
  private final CopyOnWriteArrayList<AppMeasurement.zzc> zzbQl = new CopyOnWriteArrayList();
  private boolean zzbQm;
  private AppMeasurement.zzd zzbQn;
  private String zzbQo;
  
  public zzaun(zzauh paramzzauh)
  {
    super(paramzzauh);
  }
  
  @MainThread
  private void zza(final Activity paramActivity, zza paramzza, final boolean paramBoolean)
  {
    boolean bool2 = true;
    boolean bool1 = true;
    if (this.zzbQi != null) {}
    label164:
    Object localObject;
    label290:
    for (AppMeasurement.zzd localzzd = this.zzbQi;; localObject = null)
    {
      if (localzzd != null) {}
      for (localzzd = new AppMeasurement.zzd(localzzd);; localObject = null)
      {
        this.zzbQm = true;
        try
        {
          Iterator localIterator = this.zzbQl.iterator();
          for (;;)
          {
            bool2 = bool1;
            if (!localIterator.hasNext()) {
              break label164;
            }
            bool2 = bool1;
            AppMeasurement.zzc localzzc = (AppMeasurement.zzc)localIterator.next();
            try
            {
              boolean bool3 = localzzc.zza(localzzd, paramzza);
              bool1 = bool3 & bool1;
            }
            catch (Exception localException2)
            {
              for (;;)
              {
                bool2 = bool1;
                zzMg().zzNT().zzm("onScreenChangeCallback threw exception", localException2);
              }
            }
          }
          if ((this.zzbQj == null) || (Math.abs(zznq().elapsedRealtime() - this.zzbQk) >= 1000L)) {
            break label290;
          }
          localzzd = this.zzbQj;
          break;
        }
        catch (Exception localException1)
        {
          for (;;)
          {
            zzMg().zzNT().zzm("onScreenChangeCallback loop threw exception", localException1);
            this.zzbQm = false;
            bool1 = bool2;
          }
        }
        finally
        {
          this.zzbQm = false;
        }
        if (bool1)
        {
          if (paramzza.zzbLd == null) {
            paramzza.zzbLd = zzfV(paramActivity.getClass().getCanonicalName());
          }
          paramActivity = new zza(paramzza);
          this.zzbQj = this.zzbQi;
          this.zzbQk = zznq().elapsedRealtime();
          this.zzbQi = paramActivity;
          zzMf().zzp(new Runnable()
          {
            public void run()
            {
              if ((paramBoolean) && (zzaun.this.zzbQh != null)) {
                zzaun.zza(zzaun.this, zzaun.this.zzbQh);
              }
              zzaun.this.zzbQh = paramActivity;
              zzaun.this.zzLY().zza(paramActivity);
            }
          });
        }
        return;
      }
    }
  }
  
  @WorkerThread
  private void zza(@NonNull zza paramzza)
  {
    zzLT().zzah(zznq().elapsedRealtime());
    if (zzMe().zzaX(paramzza.zzbQt)) {
      paramzza.zzbQt = false;
    }
  }
  
  public static void zza(AppMeasurement.zzd paramzzd, Bundle paramBundle)
  {
    if ((paramBundle != null) && (paramzzd != null) && (!paramBundle.containsKey("_sc")))
    {
      if (paramzzd.zzbLc != null) {
        paramBundle.putString("_sn", paramzzd.zzbLc);
      }
      paramBundle.putString("_sc", paramzzd.zzbLd);
      paramBundle.putLong("_si", paramzzd.zzbLe);
    }
  }
  
  static String zzfV(String paramString)
  {
    Object localObject = paramString.split("\\.");
    if (localObject.length == 0) {
      paramString = paramString.substring(0, 36);
    }
    do
    {
      return paramString;
      localObject = localObject[(localObject.length - 1)];
      paramString = (String)localObject;
    } while (((String)localObject).length() <= 36);
    return ((String)localObject).substring(0, 36);
  }
  
  @MainThread
  public void onActivityCreated(Activity paramActivity, Bundle paramBundle)
  {
    if (paramBundle == null) {}
    do
    {
      return;
      paramBundle = paramBundle.getBundle("com.google.firebase.analytics.screen_service");
    } while (paramBundle == null);
    paramActivity = zzB(paramActivity);
    paramActivity.zzbLe = paramBundle.getLong("id");
    paramActivity.zzbLc = paramBundle.getString("name");
    paramActivity.zzbLd = paramBundle.getString("referrer_name");
  }
  
  @MainThread
  public void onActivityDestroyed(Activity paramActivity)
  {
    this.zzadi.remove(paramActivity);
  }
  
  @MainThread
  public void onActivityPaused(final Activity paramActivity)
  {
    paramActivity = zzB(paramActivity);
    this.zzbQj = this.zzbQi;
    this.zzbQk = zznq().elapsedRealtime();
    this.zzbQi = null;
    zzMf().zzp(new Runnable()
    {
      public void run()
      {
        zzaun.zza(zzaun.this, paramActivity);
        zzaun.this.zzbQh = null;
        zzaun.this.zzLY().zza(null);
      }
    });
  }
  
  @MainThread
  public void onActivityResumed(Activity paramActivity)
  {
    zza(paramActivity, zzB(paramActivity), false);
    zzLT().zzLP();
  }
  
  @MainThread
  public void onActivitySaveInstanceState(Activity paramActivity, Bundle paramBundle)
  {
    if (paramBundle == null) {}
    do
    {
      return;
      paramActivity = (zza)this.zzadi.get(paramActivity);
    } while (paramActivity == null);
    Bundle localBundle = new Bundle();
    localBundle.putLong("id", paramActivity.zzbLe);
    localBundle.putString("name", paramActivity.zzbLc);
    localBundle.putString("referrer_name", paramActivity.zzbLd);
    paramBundle.putBundle("com.google.firebase.analytics.screen_service", localBundle);
  }
  
  protected void onInitialize() {}
  
  @MainThread
  public void registerOnScreenChangeCallback(@NonNull AppMeasurement.zzc paramzzc)
  {
    zzLR();
    if (paramzzc == null)
    {
      zzMg().zzNV().log("Attempting to register null OnScreenChangeCallback");
      return;
    }
    this.zzbQl.remove(paramzzc);
    this.zzbQl.add(paramzzc);
  }
  
  @MainThread
  public void setCurrentScreen(@NonNull Activity paramActivity, @Nullable @Size(max=36L, min=1L) String paramString1, @Nullable @Size(max=36L, min=1L) String paramString2)
  {
    int i = Build.VERSION.SDK_INT;
    if (paramActivity == null)
    {
      zzMg().zzNV().log("setCurrentScreen must be called with a non-null activity");
      return;
    }
    if (!zzMf().zzbb())
    {
      zzMg().zzNV().log("setCurrentScreen must be called from the main thread");
      return;
    }
    if (this.zzbQm)
    {
      zzMg().zzNV().log("Cannot call setCurrentScreen from onScreenChangeCallback");
      return;
    }
    if (this.zzbQi == null)
    {
      zzMg().zzNV().log("setCurrentScreen cannot be called while no activity active");
      return;
    }
    if (this.zzadi.get(paramActivity) == null)
    {
      zzMg().zzNV().log("setCurrentScreen must be called with an activity in the activity lifecycle");
      return;
    }
    String str = paramString2;
    if (paramString2 == null) {
      str = zzfV(paramActivity.getClass().getCanonicalName());
    }
    boolean bool = this.zzbQi.zzbLd.equals(str);
    if (((this.zzbQi.zzbLc == null) && (paramString1 == null)) || ((this.zzbQi.zzbLc != null) && (this.zzbQi.zzbLc.equals(paramString1)))) {}
    for (i = 1; (bool) && (i != 0); i = 0)
    {
      zzMg().zzNW().log("setCurrentScreen cannot be called with the same class and name");
      return;
    }
    if ((paramString1 != null) && ((paramString1.length() < 1) || (paramString1.length() > zzMi().zzMK())))
    {
      zzMg().zzNV().zzm("Invalid screen name length in setCurrentScreen. Length", Integer.valueOf(paramString1.length()));
      return;
    }
    if ((str != null) && ((str.length() < 1) || (str.length() > zzMi().zzMK())))
    {
      zzMg().zzNV().zzm("Invalid class name length in setCurrentScreen. Length", Integer.valueOf(str.length()));
      return;
    }
    zzaua.zza localzza = zzMg().zzNZ();
    if (paramString1 == null) {}
    for (paramString2 = "null";; paramString2 = paramString1)
    {
      localzza.zze("Setting current screen to name, class", paramString2, str);
      paramString1 = new zza(paramString1, str, zzMc().zzPd());
      this.zzadi.put(paramActivity, paramString1);
      zza(paramActivity, paramString1, true);
      return;
    }
  }
  
  @MainThread
  public void unregisterOnScreenChangeCallback(@NonNull AppMeasurement.zzc paramzzc)
  {
    zzLR();
    this.zzbQl.remove(paramzzc);
  }
  
  @MainThread
  zza zzB(@NonNull Activity paramActivity)
  {
    zzac.zzC(paramActivity);
    zza localzza2 = (zza)this.zzadi.get(paramActivity);
    zza localzza1 = localzza2;
    if (localzza2 == null)
    {
      localzza1 = new zza(null, zzfV(paramActivity.getClass().getCanonicalName()), zzMc().zzPd());
      this.zzadi.put(paramActivity, localzza1);
    }
    return localzza1;
  }
  
  @WorkerThread
  public zza zzOP()
  {
    zznA();
    zzmW();
    return this.zzbQh;
  }
  
  public AppMeasurement.zzd zzOQ()
  {
    zzLR();
    AppMeasurement.zzd localzzd = this.zzbQi;
    if (localzzd == null) {
      return null;
    }
    return new AppMeasurement.zzd(localzzd);
  }
  
  @WorkerThread
  public void zza(String paramString, AppMeasurement.zzd paramzzd)
  {
    zzmW();
    try
    {
      if ((this.zzbQo == null) || (this.zzbQo.equals(paramString)) || (paramzzd != null))
      {
        this.zzbQo = paramString;
        this.zzbQn = paramzzd;
      }
      return;
    }
    finally {}
  }
  
  static class zza
    extends AppMeasurement.zzd
  {
    public boolean zzbQt;
    
    public zza(zza paramzza)
    {
      this.zzbLc = paramzza.zzbLc;
      this.zzbLd = paramzza.zzbLd;
      this.zzbLe = paramzza.zzbLe;
      this.zzbQt = paramzza.zzbQt;
    }
    
    public zza(String paramString1, String paramString2, long paramLong)
    {
      this.zzbLc = paramString1;
      this.zzbLd = paramString2;
      this.zzbLe = paramLong;
      this.zzbQt = false;
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/internal/zzaun.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */