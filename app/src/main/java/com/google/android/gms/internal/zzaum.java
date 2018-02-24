package com.google.android.gms.internal;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.app.Application.ActivityLifecycleCallbacks;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.util.Clock;
import com.google.android.gms.common.util.zze;
import com.google.android.gms.measurement.AppMeasurement.ConditionalUserProperty;
import com.google.android.gms.measurement.AppMeasurement.zza;
import com.google.android.gms.measurement.AppMeasurement.zzb;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

public class zzaum
  extends zzauk
{
  protected zza zzbPQ;
  private AppMeasurement.zza zzbPR;
  private final Set<AppMeasurement.zzb> zzbPS = new CopyOnWriteArraySet();
  private boolean zzbPT;
  private String zzbPU = null;
  private String zzbPV = null;
  
  protected zzaum(zzauh paramzzauh)
  {
    super(paramzzauh);
  }
  
  @WorkerThread
  private void zzON()
  {
    try
    {
      zze(Class.forName(zzOO()));
      return;
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      zzMg().zzNX().log("Tag Manager is not found and thus will not be used");
    }
  }
  
  private String zzOO()
  {
    return "com.google.android.gms.tagmanager.TagManagerService";
  }
  
  private void zza(final AppMeasurement.ConditionalUserProperty paramConditionalUserProperty)
  {
    long l = zznq().currentTimeMillis();
    zzac.zzC(paramConditionalUserProperty);
    zzac.zzdc(paramConditionalUserProperty.mName);
    zzac.zzdc(paramConditionalUserProperty.mOrigin);
    zzac.zzC(paramConditionalUserProperty.mValue);
    paramConditionalUserProperty.mCreationTimestamp = l;
    String str = paramConditionalUserProperty.mName;
    Object localObject1 = paramConditionalUserProperty.mValue;
    if (zzMc().zzga(str) != 0)
    {
      zzMg().zzNT().zzm("Invalid conditional user property name", str);
      return;
    }
    if (zzMc().zzp(str, localObject1) != 0)
    {
      zzMg().zzNT().zze("Invalid conditional user property value", str, localObject1);
      return;
    }
    Object localObject2 = zzMc().zzq(str, localObject1);
    if (localObject2 == null)
    {
      zzMg().zzNT().zze("Unable to normalize conditional user property value", str, localObject1);
      return;
    }
    paramConditionalUserProperty.mValue = localObject2;
    l = paramConditionalUserProperty.mTriggerTimeout;
    if ((l > zzMi().zzMV()) || (l < 1L))
    {
      zzMg().zzNT().zze("Invalid conditional user property timeout", str, Long.valueOf(l));
      return;
    }
    l = paramConditionalUserProperty.mTimeToLive;
    if ((l > zzMi().zzMW()) || (l < 1L))
    {
      zzMg().zzNT().zze("Invalid conditional user property time to live", str, Long.valueOf(l));
      return;
    }
    zzMf().zzp(new Runnable()
    {
      public void run()
      {
        zzaum.zza(zzaum.this, paramConditionalUserProperty);
      }
    });
  }
  
  private void zza(String paramString1, String paramString2, Bundle paramBundle, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, String paramString3)
  {
    zza(paramString1, paramString2, zznq().currentTimeMillis(), paramBundle, paramBoolean1, paramBoolean2, paramBoolean3, paramString3);
  }
  
  @WorkerThread
  private void zza(String paramString1, String paramString2, Object paramObject, long paramLong)
  {
    zzac.zzdc(paramString1);
    zzac.zzdc(paramString2);
    zzmW();
    zzLR();
    zznA();
    if (!this.zzbLa.isEnabled()) {
      zzMg().zzNY().log("User property not set since app measurement is disabled");
    }
    while (!this.zzbLa.zzOo()) {
      return;
    }
    zzMg().zzNY().zze("Setting user property (FE)", paramString2, paramObject);
    paramString1 = new zzaut(paramString2, paramLong, paramObject, paramString1);
    zzLY().zzb(paramString1);
  }
  
  @WorkerThread
  private void zzaV(boolean paramBoolean)
  {
    zzmW();
    zzLR();
    zznA();
    zzMg().zzNY().zzm("Setting app measurement enabled (FE)", Boolean.valueOf(paramBoolean));
    zzMh().setMeasurementEnabled(paramBoolean);
    zzLY().zzOR();
  }
  
  private Map<String, Object> zzb(final String paramString1, final String paramString2, final String paramString3, final boolean paramBoolean)
  {
    synchronized (new AtomicReference())
    {
      this.zzbLa.zzMf().zzp(new Runnable()
      {
        public void run()
        {
          zzaum.this.zzbLa.zzLY().zza(localAtomicReference, paramString1, paramString2, paramString3, paramBoolean);
        }
      });
      try
      {
        ???.wait(5000L);
        paramString2 = (List)???.get();
        if (paramString2 == null)
        {
          zzMg().zzNV().log("Timed out waiting for get user properties");
          return Collections.emptyMap();
        }
      }
      catch (InterruptedException paramString1)
      {
        for (;;)
        {
          zzMg().zzNV().zzm("Interrupted waiting for get user properties", paramString1);
        }
      }
    }
    paramString1 = new ArrayMap(paramString2.size());
    paramString2 = paramString2.iterator();
    while (paramString2.hasNext())
    {
      paramString3 = (zzaut)paramString2.next();
      paramString1.put(paramString3.name, paramString3.getValue());
    }
    return paramString1;
  }
  
  @WorkerThread
  private void zzb(AppMeasurement.ConditionalUserProperty paramConditionalUserProperty)
  {
    zzmW();
    zznA();
    zzac.zzC(paramConditionalUserProperty);
    zzac.zzdc(paramConditionalUserProperty.mName);
    zzac.zzdc(paramConditionalUserProperty.mOrigin);
    zzac.zzC(paramConditionalUserProperty.mValue);
    if (!this.zzbLa.isEnabled())
    {
      zzMg().zzNY().log("Conditional property not sent since Firebase Analytics is disabled");
      return;
    }
    zzaut localzzaut = new zzaut(paramConditionalUserProperty.mName, paramConditionalUserProperty.mTriggeredTimestamp, paramConditionalUserProperty.mValue, paramConditionalUserProperty.mOrigin);
    try
    {
      zzatt localzzatt1 = zzMc().zza(paramConditionalUserProperty.mTriggeredEventName, paramConditionalUserProperty.mTriggeredEventParams, paramConditionalUserProperty.mOrigin, 0L, true, false);
      zzatt localzzatt2 = zzMc().zza(paramConditionalUserProperty.mTimedOutEventName, paramConditionalUserProperty.mTimedOutEventParams, paramConditionalUserProperty.mOrigin, 0L, true, false);
      zzatt localzzatt3 = zzMc().zza(paramConditionalUserProperty.mExpiredEventName, paramConditionalUserProperty.mExpiredEventParams, paramConditionalUserProperty.mOrigin, 0L, true, false);
      paramConditionalUserProperty = new zzatj(paramConditionalUserProperty.mAppId, paramConditionalUserProperty.mOrigin, localzzaut, paramConditionalUserProperty.mCreationTimestamp, false, paramConditionalUserProperty.mTriggerEventName, localzzatt2, paramConditionalUserProperty.mTriggerTimeout, localzzatt1, paramConditionalUserProperty.mTimeToLive, localzzatt3);
      zzLY().zzf(paramConditionalUserProperty);
      return;
    }
    catch (IllegalArgumentException paramConditionalUserProperty) {}
  }
  
  @WorkerThread
  private void zzb(String paramString1, String paramString2, long paramLong, Bundle paramBundle, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, String paramString3)
  {
    zzac.zzdc(paramString1);
    zzac.zzdc(paramString2);
    zzac.zzC(paramBundle);
    zzmW();
    zznA();
    if (!this.zzbLa.isEnabled()) {
      zzMg().zzNY().log("Event not sent since app measurement is disabled");
    }
    boolean bool1;
    do
    {
      return;
      if (!this.zzbPT)
      {
        this.zzbPT = true;
        zzON();
      }
      bool1 = "am".equals(paramString1);
      boolean bool2 = zzauw.zzgg(paramString2);
      if ((paramBoolean1) && (this.zzbPR != null) && (!bool2) && (!bool1))
      {
        zzMg().zzNY().zze("Passing event to registered event handler (FE)", paramString2, paramBundle);
        this.zzbPR.zzb(paramString1, paramString2, paramBundle, paramLong);
        return;
      }
    } while (!this.zzbLa.zzOo());
    int j = zzMc().zzfY(paramString2);
    if (j != 0)
    {
      paramString1 = zzMc().zzc(paramString2, zzMi().zzMH(), true);
      if (paramString2 != null) {}
      for (int i = paramString2.length();; i = 0)
      {
        this.zzbLa.zzMc().zza(j, "_ev", paramString1, i);
        return;
      }
    }
    paramBundle.putString("_o", paramString1);
    Object localObject = zze.zzD("_o");
    localObject = zzMc().zza(paramString2, paramBundle, (List)localObject, paramBoolean3);
    if (!paramBundle.containsKey("_sc"))
    {
      zzMi().zzNb();
      paramBundle = zzLZ().zzOP();
      if (paramBundle != null) {
        paramBundle.zzbQt = true;
      }
      zzaun.zza(paramBundle, (Bundle)localObject);
    }
    if (paramBoolean2) {}
    for (paramBundle = zzMc().zzW((Bundle)localObject);; paramBundle = (Bundle)localObject)
    {
      zzMg().zzNY().zze("Logging event (FE)", paramString2, paramBundle);
      localObject = new zzatt(paramString2, new zzatr(paramBundle), paramString1, paramLong);
      zzLY().zzc((zzatt)localObject, paramString3);
      if (bool1) {
        break;
      }
      paramString3 = this.zzbPS.iterator();
      while (paramString3.hasNext()) {
        ((AppMeasurement.zzb)paramString3.next()).zzc(paramString1, paramString2, new Bundle(paramBundle), paramLong);
      }
      break;
    }
  }
  
  private void zzb(String paramString1, String paramString2, String paramString3, Bundle paramBundle)
  {
    long l = zznq().currentTimeMillis();
    zzac.zzdc(paramString2);
    final AppMeasurement.ConditionalUserProperty localConditionalUserProperty = new AppMeasurement.ConditionalUserProperty();
    localConditionalUserProperty.mAppId = paramString1;
    localConditionalUserProperty.mName = paramString2;
    localConditionalUserProperty.mCreationTimestamp = l;
    if (paramString3 != null)
    {
      localConditionalUserProperty.mExpiredEventName = paramString3;
      localConditionalUserProperty.mExpiredEventParams = paramBundle;
    }
    zzMf().zzp(new Runnable()
    {
      public void run()
      {
        zzaum.zzb(zzaum.this, localConditionalUserProperty);
      }
    });
  }
  
  @WorkerThread
  private void zzc(AppMeasurement.ConditionalUserProperty paramConditionalUserProperty)
  {
    zzmW();
    zznA();
    zzac.zzC(paramConditionalUserProperty);
    zzac.zzdc(paramConditionalUserProperty.mName);
    if (!this.zzbLa.isEnabled())
    {
      zzMg().zzNY().log("Conditional property not cleared since Firebase Analytics is disabled");
      return;
    }
    zzaut localzzaut = new zzaut(paramConditionalUserProperty.mName, 0L, null, null);
    try
    {
      zzatt localzzatt = zzMc().zza(paramConditionalUserProperty.mExpiredEventName, paramConditionalUserProperty.mExpiredEventParams, paramConditionalUserProperty.mOrigin, paramConditionalUserProperty.mCreationTimestamp, true, false);
      paramConditionalUserProperty = new zzatj(paramConditionalUserProperty.mAppId, paramConditionalUserProperty.mOrigin, localzzaut, paramConditionalUserProperty.mCreationTimestamp, paramConditionalUserProperty.mActive, paramConditionalUserProperty.mTriggerEventName, null, paramConditionalUserProperty.mTriggerTimeout, null, paramConditionalUserProperty.mTimeToLive, localzzatt);
      zzLY().zzf(paramConditionalUserProperty);
      return;
    }
    catch (IllegalArgumentException paramConditionalUserProperty) {}
  }
  
  private List<AppMeasurement.ConditionalUserProperty> zzm(final String paramString1, final String paramString2, final String paramString3)
  {
    synchronized (new AtomicReference())
    {
      this.zzbLa.zzMf().zzp(new Runnable()
      {
        public void run()
        {
          zzaum.this.zzbLa.zzLY().zza(localObject, paramString1, paramString2, paramString3);
        }
      });
      try
      {
        ???.wait(5000L);
        ??? = (List)((AtomicReference)???).get();
        if (??? == null)
        {
          zzMg().zzNV().zzm("Timed out waiting for get conditional user properties", paramString1);
          return Collections.emptyList();
        }
      }
      catch (InterruptedException paramString3)
      {
        for (;;)
        {
          zzMg().zzNV().zze("Interrupted waiting for get conditional user properties", paramString1, paramString3);
        }
      }
    }
    paramString3 = new ArrayList(((List)???).size());
    ??? = ((List)???).iterator();
    while (((Iterator)???).hasNext())
    {
      zzatj localzzatj = (zzatj)((Iterator)???).next();
      AppMeasurement.ConditionalUserProperty localConditionalUserProperty = new AppMeasurement.ConditionalUserProperty();
      localConditionalUserProperty.mAppId = paramString1;
      localConditionalUserProperty.mOrigin = paramString2;
      localConditionalUserProperty.mCreationTimestamp = localzzatj.zzbLT;
      localConditionalUserProperty.mName = localzzatj.zzbLS.name;
      localConditionalUserProperty.mValue = localzzatj.zzbLS.getValue();
      localConditionalUserProperty.mActive = localzzatj.zzbLU;
      localConditionalUserProperty.mTriggerEventName = localzzatj.zzbLV;
      if (localzzatj.zzbLW != null)
      {
        localConditionalUserProperty.mTimedOutEventName = localzzatj.zzbLW.name;
        if (localzzatj.zzbLW.zzbMC != null) {
          localConditionalUserProperty.mTimedOutEventParams = localzzatj.zzbLW.zzbMC.zzNR();
        }
      }
      localConditionalUserProperty.mTriggerTimeout = localzzatj.zzbLX;
      if (localzzatj.zzbLY != null)
      {
        localConditionalUserProperty.mTriggeredEventName = localzzatj.zzbLY.name;
        if (localzzatj.zzbLY.zzbMC != null) {
          localConditionalUserProperty.mTriggeredEventParams = localzzatj.zzbLY.zzbMC.zzNR();
        }
      }
      localConditionalUserProperty.mTriggeredTimestamp = localzzatj.zzbLS.zzbQW;
      localConditionalUserProperty.mTimeToLive = localzzatj.zzbLZ;
      if (localzzatj.zzbMa != null)
      {
        localConditionalUserProperty.mExpiredEventName = localzzatj.zzbMa.name;
        if (localzzatj.zzbMa.zzbMC != null) {
          localConditionalUserProperty.mExpiredEventParams = localzzatj.zzbMa.zzbMC.zzNR();
        }
      }
      paramString3.add(localConditionalUserProperty);
    }
    return paramString3;
  }
  
  public void clearConditionalUserProperty(String paramString1, String paramString2, Bundle paramBundle)
  {
    zzLR();
    zzb(null, paramString1, paramString2, paramBundle);
  }
  
  public void clearConditionalUserPropertyAs(String paramString1, String paramString2, String paramString3, Bundle paramBundle)
  {
    zzac.zzdc(paramString1);
    zzLQ();
    zzb(paramString1, paramString2, paramString3, paramBundle);
  }
  
  public Task<String> getAppInstanceId()
  {
    try
    {
      Object localObject = zzMh().zzOg();
      if (localObject != null) {
        return Tasks.forResult(localObject);
      }
      localObject = Tasks.call(zzMf().zzOm(), new Callable()
      {
        public String zzcj()
          throws Exception
        {
          String str = zzaum.this.zzMh().zzOg();
          if (str != null) {
            return str;
          }
          str = zzaum.this.zzLV().zzaC(120000L);
          if (str == null) {
            throw new TimeoutException();
          }
          zzaum.this.zzMh().zzfM(str);
          return str;
        }
      });
      return (Task<String>)localObject;
    }
    catch (Exception localException)
    {
      zzMg().zzNV().log("Failed to schedule task for getAppInstanceId");
      return Tasks.forException(localException);
    }
  }
  
  public List<AppMeasurement.ConditionalUserProperty> getConditionalUserProperties(String paramString1, String paramString2)
  {
    zzLR();
    return zzm(null, paramString1, paramString2);
  }
  
  public List<AppMeasurement.ConditionalUserProperty> getConditionalUserPropertiesAs(String paramString1, String paramString2, String paramString3)
  {
    zzac.zzdc(paramString1);
    zzLQ();
    return zzm(paramString1, paramString2, paramString3);
  }
  
  public int getMaxUserProperties(String paramString)
  {
    zzac.zzdc(paramString);
    return zzMi().zzMT();
  }
  
  public Map<String, Object> getUserProperties(String paramString1, String paramString2, boolean paramBoolean)
  {
    zzLR();
    return zzb(null, paramString1, paramString2, paramBoolean);
  }
  
  public Map<String, Object> getUserPropertiesAs(String paramString1, String paramString2, String paramString3, boolean paramBoolean)
  {
    zzac.zzdc(paramString1);
    zzLQ();
    return zzb(paramString1, paramString2, paramString3, paramBoolean);
  }
  
  protected void onInitialize() {}
  
  public void setConditionalUserProperty(AppMeasurement.ConditionalUserProperty paramConditionalUserProperty)
  {
    zzac.zzC(paramConditionalUserProperty);
    zzLR();
    paramConditionalUserProperty = new AppMeasurement.ConditionalUserProperty(paramConditionalUserProperty);
    if (!TextUtils.isEmpty(paramConditionalUserProperty.mAppId)) {
      zzMg().zzNV().log("Package name should be null when calling setConditionalUserProperty");
    }
    paramConditionalUserProperty.mAppId = null;
    zza(paramConditionalUserProperty);
  }
  
  public void setConditionalUserPropertyAs(AppMeasurement.ConditionalUserProperty paramConditionalUserProperty)
  {
    zzac.zzC(paramConditionalUserProperty);
    zzac.zzdc(paramConditionalUserProperty.mAppId);
    zzLQ();
    zza(new AppMeasurement.ConditionalUserProperty(paramConditionalUserProperty));
  }
  
  public void setMeasurementEnabled(final boolean paramBoolean)
  {
    zznA();
    zzLR();
    zzMf().zzp(new Runnable()
    {
      public void run()
      {
        zzaum.zza(zzaum.this, paramBoolean);
      }
    });
  }
  
  public void setMinimumSessionDuration(final long paramLong)
  {
    zzLR();
    zzMf().zzp(new Runnable()
    {
      public void run()
      {
        zzaum.this.zzMh().zzbOh.set(paramLong);
        zzaum.this.zzMg().zzNY().zzm("Minimum session duration set", Long.valueOf(paramLong));
      }
    });
  }
  
  public void setSessionTimeoutDuration(final long paramLong)
  {
    zzLR();
    zzMf().zzp(new Runnable()
    {
      public void run()
      {
        zzaum.this.zzMh().zzbOi.set(paramLong);
        zzaum.this.zzMg().zzNY().zzm("Session timeout duration set", Long.valueOf(paramLong));
      }
    });
  }
  
  @TargetApi(14)
  public void zzOL()
  {
    if ((getContext().getApplicationContext() instanceof Application))
    {
      Application localApplication = (Application)getContext().getApplicationContext();
      if (this.zzbPQ == null) {
        this.zzbPQ = new zza(null);
      }
      localApplication.unregisterActivityLifecycleCallbacks(this.zzbPQ);
      localApplication.registerActivityLifecycleCallbacks(this.zzbPQ);
      zzMg().zzNZ().log("Registered activity lifecycle callback");
    }
  }
  
  @WorkerThread
  public void zzOM()
  {
    zzmW();
    zzLR();
    zznA();
    if (!this.zzbLa.zzOo()) {}
    String str;
    do
    {
      return;
      zzLY().zzOM();
      str = zzMh().zzOj();
    } while ((TextUtils.isEmpty(str)) || (str.equals(zzLX().zzNN())));
    Bundle localBundle = new Bundle();
    localBundle.putString("_po", str);
    zzd("auto", "_ou", localBundle);
  }
  
  @WorkerThread
  public void zza(AppMeasurement.zza paramzza)
  {
    zzmW();
    zzLR();
    zznA();
    if ((paramzza != null) && (paramzza != this.zzbPR)) {
      if (this.zzbPR != null) {
        break label46;
      }
    }
    label46:
    for (boolean bool = true;; bool = false)
    {
      zzac.zza(bool, "EventInterceptor already set.");
      this.zzbPR = paramzza;
      return;
    }
  }
  
  public void zza(AppMeasurement.zzb paramzzb)
  {
    zzLR();
    zznA();
    zzac.zzC(paramzzb);
    if (!this.zzbPS.add(paramzzb)) {
      zzMg().zzNV().log("OnEventListener already registered");
    }
  }
  
  protected void zza(final String paramString1, final String paramString2, final long paramLong, Bundle paramBundle, final boolean paramBoolean1, final boolean paramBoolean2, final boolean paramBoolean3, final String paramString3)
  {
    if (paramBundle != null) {}
    for (paramBundle = new Bundle(paramBundle);; paramBundle = new Bundle())
    {
      zzMf().zzp(new Runnable()
      {
        public void run()
        {
          zzaum.zza(zzaum.this, paramString1, paramString2, paramLong, paramBoolean1, paramBoolean2, paramBoolean3, paramString3, this.zzanH);
        }
      });
      return;
    }
  }
  
  void zza(final String paramString1, final String paramString2, final long paramLong, final Object paramObject)
  {
    zzMf().zzp(new Runnable()
    {
      public void run()
      {
        zzaum.zza(zzaum.this, paramString1, paramString2, paramObject, paramLong);
      }
    });
  }
  
  public void zza(String paramString1, String paramString2, Bundle paramBundle, boolean paramBoolean)
  {
    zzLR();
    if ((this.zzbPR == null) || (zzauw.zzgg(paramString2))) {}
    for (boolean bool = true;; bool = false)
    {
      zza(paramString1, paramString2, paramBundle, true, bool, paramBoolean, null);
      return;
    }
  }
  
  /* Error */
  public byte[] zza(final String paramString1, final String paramString2, String paramString3, Bundle paramBundle)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 236	com/google/android/gms/internal/zzaum:zzLR	()V
    //   4: aload_3
    //   5: invokestatic 143	com/google/android/gms/common/internal/zzac:zzdc	(Ljava/lang/String;)Ljava/lang/String;
    //   8: pop
    //   9: aload_2
    //   10: invokestatic 143	com/google/android/gms/common/internal/zzac:zzdc	(Ljava/lang/String;)Ljava/lang/String;
    //   13: pop
    //   14: aload 4
    //   16: invokestatic 134	com/google/android/gms/common/internal/zzac:zzC	(Ljava/lang/Object;)Ljava/lang/Object;
    //   19: pop
    //   20: aload_0
    //   21: invokevirtual 122	com/google/android/gms/internal/zzaum:zznq	()Lcom/google/android/gms/common/util/Clock;
    //   24: invokeinterface 128 1 0
    //   29: lstore 5
    //   31: aload 4
    //   33: ifnull +123 -> 156
    //   36: new 460	android/os/Bundle
    //   39: dup
    //   40: aload 4
    //   42: invokespecial 524	android/os/Bundle:<init>	(Landroid/os/Bundle;)V
    //   45: astore 7
    //   47: aload 7
    //   49: ldc_w 458
    //   52: aload_2
    //   53: invokevirtual 464	android/os/Bundle:putString	(Ljava/lang/String;Ljava/lang/String;)V
    //   56: new 297	java/util/concurrent/atomic/AtomicReference
    //   59: dup
    //   60: invokespecial 298	java/util/concurrent/atomic/AtomicReference:<init>	()V
    //   63: astore 7
    //   65: new 507	com/google/android/gms/internal/zzatt
    //   68: dup
    //   69: aload_3
    //   70: new 509	com/google/android/gms/internal/zzatr
    //   73: dup
    //   74: aload 4
    //   76: invokespecial 512	com/google/android/gms/internal/zzatr:<init>	(Landroid/os/Bundle;)V
    //   79: aload_2
    //   80: lload 5
    //   82: invokespecial 515	com/google/android/gms/internal/zzatt:<init>	(Ljava/lang/String;Lcom/google/android/gms/internal/zzatr;Ljava/lang/String;J)V
    //   85: astore_2
    //   86: aload 7
    //   88: monitorenter
    //   89: aload_0
    //   90: getfield 243	com/google/android/gms/internal/zzaum:zzbLa	Lcom/google/android/gms/internal/zzauh;
    //   93: invokevirtual 299	com/google/android/gms/internal/zzauh:zzMf	()Lcom/google/android/gms/internal/zzaug;
    //   96: new 14	com/google/android/gms/internal/zzaum$13
    //   99: dup
    //   100: aload_0
    //   101: aload 7
    //   103: aload_2
    //   104: aload_1
    //   105: invokespecial 797	com/google/android/gms/internal/zzaum$13:<init>	(Lcom/google/android/gms/internal/zzaum;Ljava/util/concurrent/atomic/AtomicReference;Lcom/google/android/gms/internal/zzatt;Ljava/lang/String;)V
    //   108: invokevirtual 227	com/google/android/gms/internal/zzaug:zzp	(Ljava/lang/Runnable;)V
    //   111: aload 7
    //   113: ldc2_w 798
    //   116: invokevirtual 310	java/lang/Object:wait	(J)V
    //   119: aload 7
    //   121: monitorexit
    //   122: aload 7
    //   124: invokevirtual 314	java/util/concurrent/atomic/AtomicReference:get	()Ljava/lang/Object;
    //   127: checkcast 801	[B
    //   130: astore_2
    //   131: aload_2
    //   132: astore_1
    //   133: aload_2
    //   134: ifnonnull +20 -> 154
    //   137: aload_0
    //   138: invokevirtual 83	com/google/android/gms/internal/zzaum:zzMg	()Lcom/google/android/gms/internal/zzaua;
    //   141: invokevirtual 319	com/google/android/gms/internal/zzaua:zzNV	()Lcom/google/android/gms/internal/zzaua$zza;
    //   144: ldc_w 803
    //   147: invokevirtual 97	com/google/android/gms/internal/zzaua$zza:log	(Ljava/lang/String;)V
    //   150: iconst_0
    //   151: newarray <illegal type>
    //   153: astore_1
    //   154: aload_1
    //   155: areturn
    //   156: new 460	android/os/Bundle
    //   159: dup
    //   160: invokespecial 766	android/os/Bundle:<init>	()V
    //   163: astore 7
    //   165: goto -118 -> 47
    //   168: astore_1
    //   169: aload_0
    //   170: invokevirtual 83	com/google/android/gms/internal/zzaum:zzMg	()Lcom/google/android/gms/internal/zzaua;
    //   173: invokevirtual 319	com/google/android/gms/internal/zzaua:zzNV	()Lcom/google/android/gms/internal/zzaua$zza;
    //   176: ldc_w 805
    //   179: aload_1
    //   180: invokevirtual 173	com/google/android/gms/internal/zzaua$zza:zzm	(Ljava/lang/String;Ljava/lang/Object;)V
    //   183: goto -64 -> 119
    //   186: astore_1
    //   187: aload 7
    //   189: monitorexit
    //   190: aload_1
    //   191: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	192	0	this	zzaum
    //   0	192	1	paramString1	String
    //   0	192	2	paramString2	String
    //   0	192	3	paramString3	String
    //   0	192	4	paramBundle	Bundle
    //   29	52	5	l	long
    //   45	143	7	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   111	119	168	java/lang/InterruptedException
    //   89	111	186	finally
    //   111	119	186	finally
    //   119	122	186	finally
    //   169	183	186	finally
    //   187	190	186	finally
  }
  
  @Nullable
  public String zzaC(long paramLong)
  {
    if (zzMf().zzOl())
    {
      zzMg().zzNT().log("Cannot retrieve app instance id from analytics worker thread");
      return null;
    }
    if (zzMf().zzbb())
    {
      zzMg().zzNT().log("Cannot retrieve app instance id from main thread");
      return null;
    }
    synchronized (new AtomicReference())
    {
      zzMf().zzp(new Runnable()
      {
        public void run()
        {
          zzaum.this.zzLY().zza(localAtomicReference);
        }
      });
      try
      {
        ???.wait(paramLong);
        return (String)???.get();
      }
      catch (InterruptedException localInterruptedException)
      {
        zzMg().zzNV().log("Interrupted waiting for app instance id");
        return null;
      }
    }
  }
  
  public List<zzaut> zzaW(final boolean paramBoolean)
  {
    zzLR();
    zznA();
    zzMg().zzNY().log("Fetching user attributes (FE)");
    synchronized (new AtomicReference())
    {
      this.zzbLa.zzMf().zzp(new Runnable()
      {
        public void run()
        {
          zzaum.this.zzLY().zza(localObject1, paramBoolean);
        }
      });
      try
      {
        ???.wait(5000L);
        List localList = (List)((AtomicReference)???).get();
        ??? = localList;
        if (localList == null)
        {
          zzMg().zzNV().log("Timed out waiting for get user properties");
          ??? = Collections.emptyList();
        }
        return (List<zzaut>)???;
      }
      catch (InterruptedException localInterruptedException)
      {
        for (;;)
        {
          zzMg().zzNV().zzm("Interrupted waiting for get user properties", localInterruptedException);
        }
      }
    }
  }
  
  public void zzd(String paramString1, String paramString2, Bundle paramBundle)
  {
    zzLR();
    if ((this.zzbPR == null) || (zzauw.zzgg(paramString2))) {}
    for (boolean bool = true;; bool = false)
    {
      zza(paramString1, paramString2, paramBundle, true, bool, false, null);
      return;
    }
  }
  
  public void zzd(String paramString1, String paramString2, Bundle paramBundle, long paramLong)
  {
    zzLR();
    zza(paramString1, paramString2, paramLong, paramBundle, false, true, true, null);
  }
  
  public void zzd(String paramString1, String paramString2, Object paramObject)
  {
    int i = 0;
    int j = 0;
    zzac.zzdc(paramString1);
    long l = zznq().currentTimeMillis();
    int k = zzMc().zzga(paramString2);
    if (k != 0)
    {
      paramString1 = zzMc().zzc(paramString2, zzMi().zzMI(), true);
      i = j;
      if (paramString2 != null) {
        i = paramString2.length();
      }
      this.zzbLa.zzMc().zza(k, "_ev", paramString1, i);
    }
    do
    {
      return;
      if (paramObject == null) {
        break;
      }
      j = zzMc().zzp(paramString2, paramObject);
      if (j != 0)
      {
        paramString1 = zzMc().zzc(paramString2, zzMi().zzMI(), true);
        if (((paramObject instanceof String)) || ((paramObject instanceof CharSequence))) {
          i = String.valueOf(paramObject).length();
        }
        this.zzbLa.zzMc().zza(j, "_ev", paramString1, i);
        return;
      }
      paramObject = zzMc().zzq(paramString2, paramObject);
    } while (paramObject == null);
    zza(paramString1, paramString2, l, paramObject);
    return;
    zza(paramString1, paramString2, l, null);
  }
  
  @WorkerThread
  public void zze(Class<?> paramClass)
  {
    try
    {
      paramClass.getDeclaredMethod("initialize", new Class[] { Context.class }).invoke(null, new Object[] { getContext() });
      return;
    }
    catch (Exception paramClass)
    {
      zzMg().zzNV().zzm("Failed to invoke Tag Manager's initialize() method", paramClass);
    }
  }
  
  /* Error */
  @Nullable
  @WorkerThread
  public String zzfT(String paramString)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: invokevirtual 239	com/google/android/gms/internal/zzaum:zznA	()V
    //   6: aload_0
    //   7: invokevirtual 236	com/google/android/gms/internal/zzaum:zzLR	()V
    //   10: aload_1
    //   11: ifnull +23 -> 34
    //   14: aload_1
    //   15: aload_0
    //   16: getfield 60	com/google/android/gms/internal/zzaum:zzbPV	Ljava/lang/String;
    //   19: invokevirtual 425	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   22: ifeq +12 -> 34
    //   25: aload_0
    //   26: getfield 58	com/google/android/gms/internal/zzaum:zzbPU	Ljava/lang/String;
    //   29: astore_1
    //   30: aload_0
    //   31: monitorexit
    //   32: aload_1
    //   33: areturn
    //   34: aload_0
    //   35: ldc2_w 859
    //   38: invokevirtual 862	com/google/android/gms/internal/zzaum:zzaC	(J)Ljava/lang/String;
    //   41: astore_2
    //   42: aload_2
    //   43: ifnonnull +8 -> 51
    //   46: aconst_null
    //   47: astore_1
    //   48: goto -18 -> 30
    //   51: aload_0
    //   52: aload_1
    //   53: putfield 60	com/google/android/gms/internal/zzaum:zzbPV	Ljava/lang/String;
    //   56: aload_0
    //   57: aload_2
    //   58: putfield 58	com/google/android/gms/internal/zzaum:zzbPU	Ljava/lang/String;
    //   61: aload_0
    //   62: getfield 58	com/google/android/gms/internal/zzaum:zzbPU	Ljava/lang/String;
    //   65: astore_1
    //   66: goto -36 -> 30
    //   69: astore_1
    //   70: aload_0
    //   71: monitorexit
    //   72: aload_1
    //   73: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	74	0	this	zzaum
    //   0	74	1	paramString	String
    //   41	17	2	str	String
    // Exception table:
    //   from	to	target	type
    //   2	10	69	finally
    //   14	30	69	finally
    //   34	42	69	finally
    //   51	66	69	finally
  }
  
  @TargetApi(14)
  @MainThread
  private class zza
    implements Application.ActivityLifecycleCallbacks
  {
    private zza() {}
    
    private boolean zzfU(String paramString)
    {
      if (!TextUtils.isEmpty(paramString))
      {
        zzaum.this.zzd("auto", "_ldl", paramString);
        return true;
      }
      return false;
    }
    
    public void onActivityCreated(Activity paramActivity, Bundle paramBundle)
    {
      for (;;)
      {
        int i;
        try
        {
          zzaum.this.zzMg().zzNZ().log("onActivityCreated");
          Object localObject = paramActivity.getIntent();
          if (localObject == null) {
            continue;
          }
          Uri localUri = ((Intent)localObject).getData();
          if ((localUri == null) || (!localUri.isHierarchical())) {
            continue;
          }
          if (paramBundle == null)
          {
            Bundle localBundle = zzaum.this.zzMc().zzx(localUri);
            if (!zzaum.this.zzMc().zzM((Intent)localObject)) {
              break label266;
            }
            localObject = "gs";
            if (localBundle != null) {
              zzaum.this.zzd((String)localObject, "_cmp", localBundle);
            }
          }
          localObject = localUri.getQueryParameter("referrer");
          if (TextUtils.isEmpty((CharSequence)localObject)) {
            return;
          }
          if (!((String)localObject).contains("gclid")) {
            break label234;
          }
          if ((((String)localObject).contains("utm_campaign")) || (((String)localObject).contains("utm_source")) || (((String)localObject).contains("utm_medium")) || (((String)localObject).contains("utm_term"))) {
            break label273;
          }
          if (!((String)localObject).contains("utm_content")) {
            break label234;
          }
        }
        catch (Throwable localThrowable)
        {
          zzaum.this.zzMg().zzNT().zzm("Throwable caught in onActivityCreated", localThrowable);
          zzaum.this.zzLZ().onActivityCreated(paramActivity, paramBundle);
          return;
        }
        if (i == 0)
        {
          zzaum.this.zzMg().zzNY().log("Activity created with data 'referrer' param without gclid and at least one utm field");
          return;
          label234:
          i = 0;
        }
        else
        {
          zzaum.this.zzMg().zzNY().zzm("Activity created with referrer", localThrowable);
          zzfU(localThrowable);
          continue;
          label266:
          String str = "auto";
          continue;
          label273:
          i = 1;
        }
      }
    }
    
    public void onActivityDestroyed(Activity paramActivity)
    {
      zzaum.this.zzLZ().onActivityDestroyed(paramActivity);
    }
    
    @MainThread
    public void onActivityPaused(Activity paramActivity)
    {
      zzaum.this.zzLZ().onActivityPaused(paramActivity);
      zzaum.this.zzMe().zzOZ();
    }
    
    @MainThread
    public void onActivityResumed(Activity paramActivity)
    {
      zzaum.this.zzLZ().onActivityResumed(paramActivity);
      zzaum.this.zzMe().zzOX();
    }
    
    public void onActivitySaveInstanceState(Activity paramActivity, Bundle paramBundle)
    {
      zzaum.this.zzLZ().onActivitySaveInstanceState(paramActivity, paramBundle);
    }
    
    public void onActivityStarted(Activity paramActivity) {}
    
    public void onActivityStopped(Activity paramActivity) {}
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/internal/zzaum.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */