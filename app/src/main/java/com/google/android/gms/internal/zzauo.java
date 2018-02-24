package com.google.android.gms.internal;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build.VERSION;
import android.os.Looper;
import android.os.RemoteException;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailabilityLight;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.internal.zzf.zzb;
import com.google.android.gms.common.internal.zzf.zzc;
import com.google.android.gms.measurement.AppMeasurement.zzd;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class zzauo
  extends zzauk
{
  private final zzatn zzbQA;
  private final zza zzbQu;
  private zzatw zzbQv;
  private Boolean zzbQw;
  private final zzatn zzbQx;
  private final zzaur zzbQy;
  private final List<Runnable> zzbQz = new ArrayList();
  
  protected zzauo(zzauh paramzzauh)
  {
    super(paramzzauh);
    this.zzbQy = new zzaur(paramzzauh.zznq());
    this.zzbQu = new zza();
    this.zzbQx = new zzatn(paramzzauh)
    {
      public void run()
      {
        zzauo.zzb(zzauo.this);
      }
    };
    this.zzbQA = new zzatn(paramzzauh)
    {
      public void run()
      {
        zzauo.this.zzMg().zzNV().log("Tasks have been queued for a long time");
      }
    };
  }
  
  @WorkerThread
  private void onServiceDisconnected(ComponentName paramComponentName)
  {
    zzmW();
    if (this.zzbQv != null)
    {
      this.zzbQv = null;
      zzMg().zzNZ().zzm("Disconnected from device MeasurementService", paramComponentName);
      zzOU();
    }
  }
  
  private boolean zzOS()
  {
    zzMi().zzNb();
    List localList = getContext().getPackageManager().queryIntentServices(new Intent().setClassName(getContext(), "com.google.android.gms.measurement.AppMeasurementService"), 65536);
    return (localList != null) && (localList.size() > 0);
  }
  
  @WorkerThread
  private void zzOU()
  {
    zzmW();
    zzoc();
  }
  
  @WorkerThread
  private void zzOV()
  {
    zzmW();
    zzMg().zzNZ().zzm("Processing queued up service tasks", Integer.valueOf(this.zzbQz.size()));
    Iterator localIterator = this.zzbQz.iterator();
    while (localIterator.hasNext())
    {
      Runnable localRunnable = (Runnable)localIterator.next();
      zzMf().zzp(localRunnable);
    }
    this.zzbQz.clear();
    this.zzbQA.cancel();
  }
  
  @WorkerThread
  private void zznN()
  {
    zzmW();
    this.zzbQy.start();
    this.zzbQx.zzC(zzMi().zzoP());
  }
  
  @WorkerThread
  private void zznO()
  {
    zzmW();
    if (!isConnected()) {
      return;
    }
    zzMg().zzNZ().log("Inactivity, disconnecting from the service");
    disconnect();
  }
  
  @WorkerThread
  private void zzr(Runnable paramRunnable)
    throws IllegalStateException
  {
    zzmW();
    if (isConnected())
    {
      paramRunnable.run();
      return;
    }
    if (this.zzbQz.size() >= zzMi().zzNh())
    {
      zzMg().zzNT().log("Discarding data. Max runnable queue size reached");
      return;
    }
    this.zzbQz.add(paramRunnable);
    this.zzbQA.zzC(60000L);
    zzoc();
  }
  
  @WorkerThread
  public void disconnect()
  {
    zzmW();
    zznA();
    try
    {
      com.google.android.gms.common.stats.zza.zzAu().zza(getContext(), this.zzbQu);
      this.zzbQv = null;
      return;
    }
    catch (IllegalStateException localIllegalStateException)
    {
      for (;;) {}
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      for (;;) {}
    }
  }
  
  @WorkerThread
  public boolean isConnected()
  {
    zzmW();
    zznA();
    return this.zzbQv != null;
  }
  
  protected void onInitialize() {}
  
  @WorkerThread
  protected void zzOM()
  {
    zzmW();
    zznA();
    zzr(new Runnable()
    {
      public void run()
      {
        zzatw localzzatw = zzauo.zzc(zzauo.this);
        if (localzzatw == null)
        {
          zzauo.this.zzMg().zzNT().log("Discarding data. Failed to send app launch");
          return;
        }
        try
        {
          localzzatw.zza(zzauo.this.zzLW().zzfG(zzauo.this.zzMg().zzOa()));
          zzauo.this.zza(localzzatw, null);
          zzauo.zzd(zzauo.this);
          return;
        }
        catch (RemoteException localRemoteException)
        {
          zzauo.this.zzMg().zzNT().zzm("Failed to send app launch to the service", localRemoteException);
        }
      }
    });
  }
  
  @WorkerThread
  protected void zzOR()
  {
    zzmW();
    zznA();
    zzr(new Runnable()
    {
      public void run()
      {
        zzatw localzzatw = zzauo.zzc(zzauo.this);
        if (localzzatw == null)
        {
          zzauo.this.zzMg().zzNT().log("Failed to send measurementEnabled to service");
          return;
        }
        try
        {
          localzzatw.zzb(zzauo.this.zzLW().zzfG(zzauo.this.zzMg().zzOa()));
          zzauo.zzd(zzauo.this);
          return;
        }
        catch (RemoteException localRemoteException)
        {
          zzauo.this.zzMg().zzNT().zzm("Failed to send measurementEnabled to the service", localRemoteException);
        }
      }
    });
  }
  
  @WorkerThread
  protected boolean zzOT()
  {
    zzmW();
    zznA();
    zzMi().zzNb();
    zzMg().zzNZ().log("Checking service availability");
    switch (GoogleApiAvailabilityLight.getInstance().isGooglePlayServicesAvailable(getContext()))
    {
    default: 
      return false;
    case 0: 
      zzMg().zzNZ().log("Service available");
      return true;
    case 1: 
      zzMg().zzNZ().log("Service missing");
      return false;
    case 18: 
      zzMg().zzNV().log("Service updating");
      return true;
    case 2: 
      zzMg().zzNY().log("Service container out of date");
      return true;
    case 3: 
      zzMg().zzNV().log("Service disabled");
      return false;
    }
    zzMg().zzNV().log("Service invalid");
    return false;
  }
  
  @WorkerThread
  protected void zza(zzatw paramzzatw)
  {
    zzmW();
    zzac.zzC(paramzzatw);
    this.zzbQv = paramzzatw;
    zznN();
    zzOV();
  }
  
  @WorkerThread
  void zza(zzatw paramzzatw, com.google.android.gms.common.internal.safeparcel.zza paramzza)
  {
    zzmW();
    zzLR();
    zznA();
    int i = Build.VERSION.SDK_INT;
    zzMi().zzNb();
    ArrayList localArrayList = new ArrayList();
    zzMi().zzNk();
    int j = 0;
    i = 100;
    Object localObject;
    if ((j < 1001) && (i == 100))
    {
      localObject = zzMa().zzpM(100);
      if (localObject == null) {
        break label339;
      }
      localArrayList.addAll((Collection)localObject);
    }
    label339:
    for (i = ((List)localObject).size();; i = 0)
    {
      if ((paramzza != null) && (i < 100)) {
        localArrayList.add(paramzza);
      }
      localObject = localArrayList.iterator();
      while (((Iterator)localObject).hasNext())
      {
        com.google.android.gms.common.internal.safeparcel.zza localzza = (com.google.android.gms.common.internal.safeparcel.zza)((Iterator)localObject).next();
        if ((localzza instanceof zzatt)) {
          try
          {
            paramzzatw.zza((zzatt)localzza, zzLW().zzfG(zzMg().zzOa()));
          }
          catch (RemoteException localRemoteException1)
          {
            zzMg().zzNT().zzm("Failed to send event to the service", localRemoteException1);
          }
        } else if ((localRemoteException1 instanceof zzaut)) {
          try
          {
            paramzzatw.zza((zzaut)localRemoteException1, zzLW().zzfG(zzMg().zzOa()));
          }
          catch (RemoteException localRemoteException2)
          {
            zzMg().zzNT().zzm("Failed to send attribute to the service", localRemoteException2);
          }
        } else if ((localRemoteException2 instanceof zzatj)) {
          try
          {
            paramzzatw.zza((zzatj)localRemoteException2, zzLW().zzfG(zzMg().zzOa()));
          }
          catch (RemoteException localRemoteException3)
          {
            zzMg().zzNT().zzm("Failed to send conditional property to the service", localRemoteException3);
          }
        } else {
          zzMg().zzNT().log("Discarding data. Unrecognized parcel type.");
        }
      }
      j += 1;
      break;
      return;
    }
  }
  
  @WorkerThread
  protected void zza(final AppMeasurement.zzd paramzzd)
  {
    zzmW();
    zznA();
    zzr(new Runnable()
    {
      public void run()
      {
        zzatw localzzatw = zzauo.zzc(zzauo.this);
        if (localzzatw == null)
        {
          zzauo.this.zzMg().zzNT().log("Failed to send current screen to service");
          return;
        }
        for (;;)
        {
          try
          {
            if (paramzzd == null)
            {
              localzzatw.zza(0L, null, null, zzauo.this.getContext().getPackageName());
              zzauo.zzd(zzauo.this);
              return;
            }
          }
          catch (RemoteException localRemoteException)
          {
            zzauo.this.zzMg().zzNT().zzm("Failed to send current screen to the service", localRemoteException);
            return;
          }
          localRemoteException.zza(paramzzd.zzbLe, paramzzd.zzbLc, paramzzd.zzbLd, zzauo.this.getContext().getPackageName());
        }
      }
    });
  }
  
  @WorkerThread
  public void zza(final AtomicReference<String> paramAtomicReference)
  {
    zzmW();
    zznA();
    zzr(new Runnable()
    {
      public void run()
      {
        localAtomicReference = paramAtomicReference;
        for (;;)
        {
          try
          {
            localzzatw = zzauo.zzc(zzauo.this);
            if (localzzatw == null) {
              zzauo.this.zzMg().zzNT().log("Failed to get app instance id");
            }
          }
          catch (RemoteException localRemoteException)
          {
            zzatw localzzatw;
            zzauo.this.zzMg().zzNT().zzm("Failed to get app instance id", localRemoteException);
            paramAtomicReference.notify();
            continue;
          }
          finally
          {
            paramAtomicReference.notify();
          }
          try
          {
            paramAtomicReference.notify();
            return;
          }
          finally {}
        }
        paramAtomicReference.set(localzzatw.zzc(zzauo.this.zzLW().zzfG(null)));
        zzauo.zzd(zzauo.this);
        paramAtomicReference.notify();
      }
    });
  }
  
  @WorkerThread
  public void zza(final AtomicReference<byte[]> paramAtomicReference, final zzatt paramzzatt, final String paramString)
  {
    zzmW();
    zznA();
    if (GoogleApiAvailabilityLight.getInstance().isGooglePlayServicesAvailable(getContext()) != 0)
    {
      zzMg().zzNV().log("Not bundling data. Service unavailable or out of date");
      try
      {
        paramAtomicReference.set(new byte[0]);
        paramAtomicReference.notify();
        return;
      }
      finally {}
    }
    zzr(new Runnable()
    {
      public void run()
      {
        localAtomicReference = paramAtomicReference;
        for (;;)
        {
          try
          {
            localzzatw = zzauo.zzc(zzauo.this);
            if (localzzatw == null) {
              zzauo.this.zzMg().zzNT().log("Discarding data. Failed to send event to service to bundle");
            }
          }
          catch (RemoteException localRemoteException)
          {
            zzatw localzzatw;
            zzauo.this.zzMg().zzNT().zzm("Failed to send event to the service to bundle", localRemoteException);
            paramAtomicReference.notify();
            continue;
          }
          finally
          {
            paramAtomicReference.notify();
          }
          try
          {
            paramAtomicReference.notify();
            return;
          }
          finally {}
        }
        paramAtomicReference.set(localzzatw.zza(paramzzatt, paramString));
        zzauo.zzd(zzauo.this);
        paramAtomicReference.notify();
      }
    });
  }
  
  @WorkerThread
  protected void zza(final AtomicReference<List<zzatj>> paramAtomicReference, final String paramString1, final String paramString2, final String paramString3)
  {
    zzmW();
    zznA();
    zzr(new Runnable()
    {
      public void run()
      {
        AtomicReference localAtomicReference = paramAtomicReference;
        for (;;)
        {
          try
          {
            localzzatw = zzauo.zzc(zzauo.this);
            if (localzzatw == null)
            {
              zzauo.this.zzMg().zzNT().zzd("Failed to get conditional properties", zzaua.zzfH(paramString1), paramString2, paramString3);
              paramAtomicReference.set(Collections.emptyList());
            }
          }
          catch (RemoteException localRemoteException)
          {
            zzatw localzzatw;
            zzauo.this.zzMg().zzNT().zzd("Failed to get conditional properties", zzaua.zzfH(paramString1), paramString2, localRemoteException);
            paramAtomicReference.set(Collections.emptyList());
            paramAtomicReference.notify();
            continue;
          }
          finally
          {
            paramAtomicReference.notify();
          }
          try
          {
            paramAtomicReference.notify();
            return;
          }
          finally {}
        }
        if (TextUtils.isEmpty(paramString1)) {
          paramAtomicReference.set(localzzatw.zza(paramString2, paramString3, zzauo.this.zzLW().zzfG(zzauo.this.zzMg().zzOa())));
        }
        for (;;)
        {
          zzauo.zzd(zzauo.this);
          paramAtomicReference.notify();
          return;
          paramAtomicReference.set(((zzatw)localObject1).zzl(paramString1, paramString2, paramString3));
        }
      }
    });
  }
  
  @WorkerThread
  protected void zza(final AtomicReference<List<zzaut>> paramAtomicReference, final String paramString1, final String paramString2, final String paramString3, final boolean paramBoolean)
  {
    zzmW();
    zznA();
    zzr(new Runnable()
    {
      public void run()
      {
        AtomicReference localAtomicReference = paramAtomicReference;
        for (;;)
        {
          try
          {
            localzzatw = zzauo.zzc(zzauo.this);
            if (localzzatw == null)
            {
              zzauo.this.zzMg().zzNT().zzd("Failed to get user properties", zzaua.zzfH(paramString1), paramString2, paramString3);
              paramAtomicReference.set(Collections.emptyList());
            }
          }
          catch (RemoteException localRemoteException)
          {
            zzatw localzzatw;
            zzauo.this.zzMg().zzNT().zzd("Failed to get user properties", zzaua.zzfH(paramString1), paramString2, localRemoteException);
            paramAtomicReference.set(Collections.emptyList());
            paramAtomicReference.notify();
            continue;
          }
          finally
          {
            paramAtomicReference.notify();
          }
          try
          {
            paramAtomicReference.notify();
            return;
          }
          finally {}
        }
        if (TextUtils.isEmpty(paramString1)) {
          paramAtomicReference.set(localzzatw.zza(paramString2, paramString3, paramBoolean, zzauo.this.zzLW().zzfG(zzauo.this.zzMg().zzOa())));
        }
        for (;;)
        {
          zzauo.zzd(zzauo.this);
          paramAtomicReference.notify();
          return;
          paramAtomicReference.set(((zzatw)localObject1).zza(paramString1, paramString2, paramString3, paramBoolean));
        }
      }
    });
  }
  
  @WorkerThread
  protected void zza(final AtomicReference<List<zzaut>> paramAtomicReference, final boolean paramBoolean)
  {
    zzmW();
    zznA();
    zzr(new Runnable()
    {
      public void run()
      {
        localAtomicReference = paramAtomicReference;
        for (;;)
        {
          try
          {
            localzzatw = zzauo.zzc(zzauo.this);
            if (localzzatw == null) {
              zzauo.this.zzMg().zzNT().log("Failed to get user properties");
            }
          }
          catch (RemoteException localRemoteException)
          {
            zzatw localzzatw;
            zzauo.this.zzMg().zzNT().zzm("Failed to get user properties", localRemoteException);
            paramAtomicReference.notify();
            continue;
          }
          finally
          {
            paramAtomicReference.notify();
          }
          try
          {
            paramAtomicReference.notify();
            return;
          }
          finally {}
        }
        paramAtomicReference.set(localzzatw.zza(zzauo.this.zzLW().zzfG(null), paramBoolean));
        zzauo.zzd(zzauo.this);
        paramAtomicReference.notify();
      }
    });
  }
  
  @WorkerThread
  protected void zzb(final zzaut paramzzaut)
  {
    zzmW();
    zznA();
    int i = Build.VERSION.SDK_INT;
    zzMi().zzNb();
    if (zzMa().zza(paramzzaut)) {}
    for (final boolean bool = true;; bool = false)
    {
      zzr(new Runnable()
      {
        public void run()
        {
          zzatw localzzatw = zzauo.zzc(zzauo.this);
          if (localzzatw == null)
          {
            zzauo.this.zzMg().zzNT().log("Discarding data. Failed to set user attribute");
            return;
          }
          zzauo localzzauo = zzauo.this;
          if (bool) {}
          for (Object localObject = null;; localObject = paramzzaut)
          {
            localzzauo.zza(localzzatw, (com.google.android.gms.common.internal.safeparcel.zza)localObject);
            zzauo.zzd(zzauo.this);
            return;
          }
        }
      });
      return;
    }
  }
  
  @WorkerThread
  protected void zzc(final zzatt paramzzatt, final String paramString)
  {
    zzac.zzC(paramzzatt);
    zzmW();
    zznA();
    int i = Build.VERSION.SDK_INT;
    zzMi().zzNb();
    if (zzMa().zza(paramzzatt)) {}
    for (final boolean bool = true;; bool = false)
    {
      zzr(new Runnable()
      {
        public void run()
        {
          zzatw localzzatw = zzauo.zzc(zzauo.this);
          if (localzzatw == null)
          {
            zzauo.this.zzMg().zzNT().log("Discarding data. Failed to send event to service");
            return;
          }
          Object localObject;
          if (this.zzbQE)
          {
            zzauo localzzauo = zzauo.this;
            if (bool)
            {
              localObject = null;
              localzzauo.zza(localzzatw, (com.google.android.gms.common.internal.safeparcel.zza)localObject);
            }
          }
          for (;;)
          {
            zzauo.zzd(zzauo.this);
            return;
            localObject = paramzzatt;
            break;
            try
            {
              if (!TextUtils.isEmpty(paramString)) {
                break label134;
              }
              localzzatw.zza(paramzzatt, zzauo.this.zzLW().zzfG(zzauo.this.zzMg().zzOa()));
            }
            catch (RemoteException localRemoteException)
            {
              zzauo.this.zzMg().zzNT().zzm("Failed to send event to the service", localRemoteException);
            }
            continue;
            label134:
            localzzatw.zza(paramzzatt, paramString, zzauo.this.zzMg().zzOa());
          }
        }
      });
      return;
    }
  }
  
  @WorkerThread
  protected void zzf(final zzatj paramzzatj)
  {
    zzac.zzC(paramzzatj);
    zzmW();
    zznA();
    zzMi().zzNb();
    if (zzMa().zzc(paramzzatj)) {}
    for (final boolean bool = true;; bool = false)
    {
      zzr(new Runnable()
      {
        public void run()
        {
          zzatw localzzatw = zzauo.zzc(zzauo.this);
          if (localzzatw == null)
          {
            zzauo.this.zzMg().zzNT().log("Discarding data. Failed to send conditional user property to service");
            return;
          }
          Object localObject;
          if (this.zzbQE)
          {
            zzauo localzzauo = zzauo.this;
            if (bool)
            {
              localObject = null;
              localzzauo.zza(localzzatw, (com.google.android.gms.common.internal.safeparcel.zza)localObject);
            }
          }
          for (;;)
          {
            zzauo.zzd(zzauo.this);
            return;
            localObject = this.zzbQG;
            break;
            try
            {
              if (!TextUtils.isEmpty(paramzzatj.packageName)) {
                break label137;
              }
              localzzatw.zza(this.zzbQG, zzauo.this.zzLW().zzfG(zzauo.this.zzMg().zzOa()));
            }
            catch (RemoteException localRemoteException)
            {
              zzauo.this.zzMg().zzNT().zzm("Failed to send conditional user property to the service", localRemoteException);
            }
            continue;
            label137:
            localzzatw.zzb(this.zzbQG);
          }
        }
      });
      return;
    }
  }
  
  @WorkerThread
  void zzoc()
  {
    zzmW();
    zznA();
    if (isConnected()) {
      return;
    }
    if (this.zzbQw == null)
    {
      this.zzbQw = zzMh().zzOh();
      if (this.zzbQw == null)
      {
        zzMg().zzNZ().log("State of service unknown");
        this.zzbQw = Boolean.valueOf(zzOT());
        zzMh().zzaT(this.zzbQw.booleanValue());
      }
    }
    if (this.zzbQw.booleanValue())
    {
      zzMg().zzNZ().log("Using measurement service");
      this.zzbQu.zzOW();
      return;
    }
    if (zzOS())
    {
      zzMg().zzNZ().log("Using local app measurement service");
      Intent localIntent = new Intent("com.google.android.gms.measurement.START");
      Context localContext = getContext();
      zzMi().zzNb();
      localIntent.setComponent(new ComponentName(localContext, "com.google.android.gms.measurement.AppMeasurementService"));
      this.zzbQu.zzL(localIntent);
      return;
    }
    zzMg().zzNT().log("Unable to use remote or local measurement implementation. Please register the AppMeasurementService service in the app manifest");
  }
  
  protected class zza
    implements ServiceConnection, zzf.zzb, zzf.zzc
  {
    private volatile boolean zzbQI;
    private volatile zzatz zzbQJ;
    
    protected zza() {}
    
    /* Error */
    @MainThread
    public void onConnected(@android.support.annotation.Nullable final android.os.Bundle paramBundle)
    {
      // Byte code:
      //   0: ldc 48
      //   2: invokestatic 54	com/google/android/gms/common/internal/zzac:zzcU	(Ljava/lang/String;)V
      //   5: aload_0
      //   6: monitorenter
      //   7: aload_0
      //   8: getfield 56	com/google/android/gms/internal/zzauo$zza:zzbQJ	Lcom/google/android/gms/internal/zzatz;
      //   11: invokevirtual 62	com/google/android/gms/internal/zzatz:zzzw	()Landroid/os/IInterface;
      //   14: checkcast 64	com/google/android/gms/internal/zzatw
      //   17: astore_1
      //   18: aload_0
      //   19: aconst_null
      //   20: putfield 56	com/google/android/gms/internal/zzauo$zza:zzbQJ	Lcom/google/android/gms/internal/zzatz;
      //   23: aload_0
      //   24: getfield 31	com/google/android/gms/internal/zzauo$zza:zzbQB	Lcom/google/android/gms/internal/zzauo;
      //   27: invokevirtual 68	com/google/android/gms/internal/zzauo:zzMf	()Lcom/google/android/gms/internal/zzaug;
      //   30: new 19	com/google/android/gms/internal/zzauo$zza$3
      //   33: dup
      //   34: aload_0
      //   35: aload_1
      //   36: invokespecial 71	com/google/android/gms/internal/zzauo$zza$3:<init>	(Lcom/google/android/gms/internal/zzauo$zza;Lcom/google/android/gms/internal/zzatw;)V
      //   39: invokevirtual 77	com/google/android/gms/internal/zzaug:zzp	(Ljava/lang/Runnable;)V
      //   42: aload_0
      //   43: monitorexit
      //   44: return
      //   45: aload_0
      //   46: aconst_null
      //   47: putfield 56	com/google/android/gms/internal/zzauo$zza:zzbQJ	Lcom/google/android/gms/internal/zzatz;
      //   50: aload_0
      //   51: iconst_0
      //   52: putfield 38	com/google/android/gms/internal/zzauo$zza:zzbQI	Z
      //   55: goto -13 -> 42
      //   58: astore_1
      //   59: aload_0
      //   60: monitorexit
      //   61: aload_1
      //   62: athrow
      //   63: astore_1
      //   64: goto -19 -> 45
      //   67: astore_1
      //   68: goto -23 -> 45
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	71	0	this	zza
      //   0	71	1	paramBundle	android.os.Bundle
      // Exception table:
      //   from	to	target	type
      //   7	42	58	finally
      //   42	44	58	finally
      //   45	55	58	finally
      //   59	61	58	finally
      //   7	42	63	android/os/DeadObjectException
      //   7	42	67	java/lang/IllegalStateException
    }
    
    @MainThread
    public void onConnectionFailed(@NonNull ConnectionResult paramConnectionResult)
    {
      zzac.zzcU("MeasurementServiceConnection.onConnectionFailed");
      zzaua localzzaua = zzauo.this.zzbLa.zzOp();
      if (localzzaua != null) {
        localzzaua.zzNV().zzm("Service connection failed", paramConnectionResult);
      }
      try
      {
        this.zzbQI = false;
        this.zzbQJ = null;
        return;
      }
      finally {}
    }
    
    @MainThread
    public void onConnectionSuspended(int paramInt)
    {
      zzac.zzcU("MeasurementServiceConnection.onConnectionSuspended");
      zzauo.this.zzMg().zzNY().log("Service connection suspended");
      zzauo.this.zzMf().zzp(new Runnable()
      {
        public void run()
        {
          zzauo localzzauo = zzauo.this;
          Context localContext = zzauo.this.getContext();
          zzauo.this.zzMi().zzNb();
          zzauo.zza(localzzauo, new ComponentName(localContext, "com.google.android.gms.measurement.AppMeasurementService"));
        }
      });
    }
    
    /* Error */
    @MainThread
    public void onServiceConnected(final ComponentName paramComponentName, android.os.IBinder paramIBinder)
    {
      // Byte code:
      //   0: ldc -122
      //   2: invokestatic 54	com/google/android/gms/common/internal/zzac:zzcU	(Ljava/lang/String;)V
      //   5: aload_0
      //   6: monitorenter
      //   7: aload_2
      //   8: ifnonnull +26 -> 34
      //   11: aload_0
      //   12: iconst_0
      //   13: putfield 38	com/google/android/gms/internal/zzauo$zza:zzbQI	Z
      //   16: aload_0
      //   17: getfield 31	com/google/android/gms/internal/zzauo$zza:zzbQB	Lcom/google/android/gms/internal/zzauo;
      //   20: invokevirtual 115	com/google/android/gms/internal/zzauo:zzMg	()Lcom/google/android/gms/internal/zzaua;
      //   23: invokevirtual 137	com/google/android/gms/internal/zzaua:zzNT	()Lcom/google/android/gms/internal/zzaua$zza;
      //   26: ldc -117
      //   28: invokevirtual 123	com/google/android/gms/internal/zzaua$zza:log	(Ljava/lang/String;)V
      //   31: aload_0
      //   32: monitorexit
      //   33: return
      //   34: aconst_null
      //   35: astore 4
      //   37: aconst_null
      //   38: astore_3
      //   39: aload 4
      //   41: astore_1
      //   42: aload_2
      //   43: invokeinterface 145 1 0
      //   48: astore 5
      //   50: aload 4
      //   52: astore_1
      //   53: ldc -109
      //   55: aload 5
      //   57: invokevirtual 153	java/lang/String:equals	(Ljava/lang/Object;)Z
      //   60: ifeq +67 -> 127
      //   63: aload 4
      //   65: astore_1
      //   66: aload_2
      //   67: invokestatic 159	com/google/android/gms/internal/zzatw$zza:zzgP	(Landroid/os/IBinder;)Lcom/google/android/gms/internal/zzatw;
      //   70: astore_2
      //   71: aload_2
      //   72: astore_1
      //   73: aload_0
      //   74: getfield 31	com/google/android/gms/internal/zzauo$zza:zzbQB	Lcom/google/android/gms/internal/zzauo;
      //   77: invokevirtual 115	com/google/android/gms/internal/zzauo:zzMg	()Lcom/google/android/gms/internal/zzaua;
      //   80: invokevirtual 162	com/google/android/gms/internal/zzaua:zzNZ	()Lcom/google/android/gms/internal/zzaua$zza;
      //   83: ldc -92
      //   85: invokevirtual 123	com/google/android/gms/internal/zzaua$zza:log	(Ljava/lang/String;)V
      //   88: aload_2
      //   89: astore_1
      //   90: aload_1
      //   91: ifnonnull +80 -> 171
      //   94: aload_0
      //   95: iconst_0
      //   96: putfield 38	com/google/android/gms/internal/zzauo$zza:zzbQI	Z
      //   99: invokestatic 170	com/google/android/gms/common/stats/zza:zzAu	()Lcom/google/android/gms/common/stats/zza;
      //   102: aload_0
      //   103: getfield 31	com/google/android/gms/internal/zzauo$zza:zzbQB	Lcom/google/android/gms/internal/zzauo;
      //   106: invokevirtual 174	com/google/android/gms/internal/zzauo:getContext	()Landroid/content/Context;
      //   109: aload_0
      //   110: getfield 31	com/google/android/gms/internal/zzauo$zza:zzbQB	Lcom/google/android/gms/internal/zzauo;
      //   113: invokestatic 177	com/google/android/gms/internal/zzauo:zza	(Lcom/google/android/gms/internal/zzauo;)Lcom/google/android/gms/internal/zzauo$zza;
      //   116: invokevirtual 180	com/google/android/gms/common/stats/zza:zza	(Landroid/content/Context;Landroid/content/ServiceConnection;)V
      //   119: aload_0
      //   120: monitorexit
      //   121: return
      //   122: astore_1
      //   123: aload_0
      //   124: monitorexit
      //   125: aload_1
      //   126: athrow
      //   127: aload 4
      //   129: astore_1
      //   130: aload_0
      //   131: getfield 31	com/google/android/gms/internal/zzauo$zza:zzbQB	Lcom/google/android/gms/internal/zzauo;
      //   134: invokevirtual 115	com/google/android/gms/internal/zzauo:zzMg	()Lcom/google/android/gms/internal/zzaua;
      //   137: invokevirtual 137	com/google/android/gms/internal/zzaua:zzNT	()Lcom/google/android/gms/internal/zzaua$zza;
      //   140: ldc -74
      //   142: aload 5
      //   144: invokevirtual 108	com/google/android/gms/internal/zzaua$zza:zzm	(Ljava/lang/String;Ljava/lang/Object;)V
      //   147: aload_3
      //   148: astore_1
      //   149: goto -59 -> 90
      //   152: astore_2
      //   153: aload_0
      //   154: getfield 31	com/google/android/gms/internal/zzauo$zza:zzbQB	Lcom/google/android/gms/internal/zzauo;
      //   157: invokevirtual 115	com/google/android/gms/internal/zzauo:zzMg	()Lcom/google/android/gms/internal/zzaua;
      //   160: invokevirtual 137	com/google/android/gms/internal/zzaua:zzNT	()Lcom/google/android/gms/internal/zzaua$zza;
      //   163: ldc -72
      //   165: invokevirtual 123	com/google/android/gms/internal/zzaua$zza:log	(Ljava/lang/String;)V
      //   168: goto -78 -> 90
      //   171: aload_0
      //   172: getfield 31	com/google/android/gms/internal/zzauo$zza:zzbQB	Lcom/google/android/gms/internal/zzauo;
      //   175: invokevirtual 68	com/google/android/gms/internal/zzauo:zzMf	()Lcom/google/android/gms/internal/zzaug;
      //   178: new 15	com/google/android/gms/internal/zzauo$zza$1
      //   181: dup
      //   182: aload_0
      //   183: aload_1
      //   184: invokespecial 185	com/google/android/gms/internal/zzauo$zza$1:<init>	(Lcom/google/android/gms/internal/zzauo$zza;Lcom/google/android/gms/internal/zzatw;)V
      //   187: invokevirtual 77	com/google/android/gms/internal/zzaug:zzp	(Ljava/lang/Runnable;)V
      //   190: goto -71 -> 119
      //   193: astore_1
      //   194: goto -75 -> 119
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	197	0	this	zza
      //   0	197	1	paramComponentName	ComponentName
      //   0	197	2	paramIBinder	android.os.IBinder
      //   38	110	3	localObject1	Object
      //   35	93	4	localObject2	Object
      //   48	95	5	str	String
      // Exception table:
      //   from	to	target	type
      //   11	33	122	finally
      //   42	50	122	finally
      //   53	63	122	finally
      //   66	71	122	finally
      //   73	88	122	finally
      //   94	99	122	finally
      //   99	119	122	finally
      //   119	121	122	finally
      //   123	125	122	finally
      //   130	147	122	finally
      //   153	168	122	finally
      //   171	190	122	finally
      //   42	50	152	android/os/RemoteException
      //   53	63	152	android/os/RemoteException
      //   66	71	152	android/os/RemoteException
      //   73	88	152	android/os/RemoteException
      //   130	147	152	android/os/RemoteException
      //   99	119	193	java/lang/IllegalArgumentException
    }
    
    @MainThread
    public void onServiceDisconnected(final ComponentName paramComponentName)
    {
      zzac.zzcU("MeasurementServiceConnection.onServiceDisconnected");
      zzauo.this.zzMg().zzNY().log("Service disconnected");
      zzauo.this.zzMf().zzp(new Runnable()
      {
        public void run()
        {
          zzauo.zza(zzauo.this, paramComponentName);
        }
      });
    }
    
    @WorkerThread
    public void zzL(Intent paramIntent)
    {
      zzauo.this.zzmW();
      Context localContext = zzauo.this.getContext();
      com.google.android.gms.common.stats.zza localzza = com.google.android.gms.common.stats.zza.zzAu();
      try
      {
        if (this.zzbQI)
        {
          zzauo.this.zzMg().zzNZ().log("Connection attempt already in progress");
          return;
        }
        this.zzbQI = true;
        localzza.zza(localContext, paramIntent, zzauo.zza(zzauo.this), 129);
        return;
      }
      finally {}
    }
    
    @WorkerThread
    public void zzOW()
    {
      zzauo.this.zzmW();
      Context localContext1 = zzauo.this.getContext();
      try
      {
        if (this.zzbQI)
        {
          zzauo.this.zzMg().zzNZ().log("Connection attempt already in progress");
          return;
        }
        if (this.zzbQJ != null)
        {
          zzauo.this.zzMg().zzNZ().log("Already awaiting connection attempt");
          return;
        }
      }
      finally {}
      this.zzbQJ = new zzatz(localContext2, Looper.getMainLooper(), this, this);
      zzauo.this.zzMg().zzNZ().log("Connecting to remote service");
      this.zzbQI = true;
      this.zzbQJ.zzzs();
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/internal/zzauo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */