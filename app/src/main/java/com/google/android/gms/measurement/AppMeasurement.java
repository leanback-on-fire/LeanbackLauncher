package com.google.android.gms.measurement;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresPermission;
import android.support.annotation.Size;
import android.support.annotation.WorkerThread;
import android.support.v4.util.ArrayMap;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.util.zze;
import com.google.android.gms.internal.zzaav;
import com.google.android.gms.internal.zzate;
import com.google.android.gms.internal.zzatl;
import com.google.android.gms.internal.zzatv;
import com.google.android.gms.internal.zzatv.zza;
import com.google.android.gms.internal.zzaua;
import com.google.android.gms.internal.zzaua.zza;
import com.google.android.gms.internal.zzauh;
import com.google.android.gms.internal.zzaum;
import com.google.android.gms.internal.zzaun;
import com.google.android.gms.internal.zzaut;
import com.google.android.gms.internal.zzauw;
import com.google.android.gms.internal.zzzo;
import com.google.firebase.analytics.FirebaseAnalytics.Event;
import com.google.firebase.analytics.FirebaseAnalytics.Param;
import com.google.firebase.analytics.FirebaseAnalytics.UserProperty;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Deprecated
public class AppMeasurement
{
  private final zzauh zzbLa;
  
  public AppMeasurement(zzauh paramzzauh)
  {
    zzac.zzC(paramzzauh);
    this.zzbLa = paramzzauh;
  }
  
  @Deprecated
  @Keep
  @RequiresPermission(allOf={"android.permission.INTERNET", "android.permission.ACCESS_NETWORK_STATE", "android.permission.WAKE_LOCK"})
  public static AppMeasurement getInstance(Context paramContext)
  {
    return zzauh.zzbR(paramContext).zzOr();
  }
  
  public static void initForTests(Map<String, ?> paramMap)
  {
    if (paramMap == null)
    {
      zzaav.zzyX();
      zzatv.zza.zzyX();
    }
    do
    {
      return;
      zzaav.zzyV();
      if (paramMap.containsKey("uploadUrl")) {
        zzatv.zzbNa.override((String)paramMap.get("uploadUrl"));
      }
      if (paramMap.containsKey("uploadInterval")) {
        zzatv.zzbNd.override((Long)paramMap.get("uploadInterval"));
      }
      if (paramMap.containsKey("uploadRetryTime")) {
        zzatv.zzbNl.override((Long)paramMap.get("uploadRetryTime"));
      }
      if (paramMap.containsKey("uploadInitialDelayTime")) {
        zzatv.zzbNk.override((Long)paramMap.get("uploadInitialDelayTime"));
      }
      if (paramMap.containsKey("uploadWindowInterval")) {
        zzatv.zzbNc.override((Long)paramMap.get("uploadWindowInterval"));
      }
      if (paramMap.containsKey("configUrlScheme")) {
        zzatv.zzbMO.override((String)paramMap.get("configUrlScheme"));
      }
    } while (!paramMap.containsKey("configUrlAuthority"));
    zzatv.zzbMP.override((String)paramMap.get("configUrlAuthority"));
  }
  
  private void zzc(String paramString1, String paramString2, Object paramObject)
  {
    this.zzbLa.zzLV().zzd(paramString1, paramString2, paramObject);
  }
  
  @Keep
  public void beginAdUnitExposure(@NonNull @Size(min=1L) String paramString)
  {
    this.zzbLa.zzLT().beginAdUnitExposure(paramString);
  }
  
  @Keep
  protected void clearConditionalUserProperty(@NonNull @Size(max=24L, min=1L) String paramString1, @Nullable String paramString2, @Nullable Bundle paramBundle)
  {
    this.zzbLa.zzLV().clearConditionalUserProperty(paramString1, paramString2, paramBundle);
  }
  
  @Keep
  protected void clearConditionalUserPropertyAs(@NonNull @Size(min=1L) String paramString1, @NonNull @Size(max=24L, min=1L) String paramString2, @Nullable String paramString3, @Nullable Bundle paramBundle)
  {
    this.zzbLa.zzLV().clearConditionalUserPropertyAs(paramString1, paramString2, paramString3, paramBundle);
  }
  
  @Keep
  public void endAdUnitExposure(@NonNull @Size(min=1L) String paramString)
  {
    this.zzbLa.zzLT().endAdUnitExposure(paramString);
  }
  
  @Keep
  public long generateEventId()
  {
    return this.zzbLa.zzMc().zzPd();
  }
  
  @Keep
  @Nullable
  @WorkerThread
  public String getAppInstanceId()
  {
    return this.zzbLa.zzLV().zzfT(null);
  }
  
  @Keep
  @WorkerThread
  protected List<ConditionalUserProperty> getConditionalUserProperties(@Nullable String paramString1, @Nullable @Size(max=23L, min=1L) String paramString2)
  {
    return this.zzbLa.zzLV().getConditionalUserProperties(paramString1, paramString2);
  }
  
  @Keep
  @WorkerThread
  protected List<ConditionalUserProperty> getConditionalUserPropertiesAs(@NonNull @Size(min=1L) String paramString1, @Nullable String paramString2, @Nullable @Size(max=23L, min=1L) String paramString3)
  {
    return this.zzbLa.zzLV().getConditionalUserPropertiesAs(paramString1, paramString2, paramString3);
  }
  
  @Keep
  @Nullable
  public String getCurrentScreenName()
  {
    zzd localzzd = this.zzbLa.zzLZ().zzOQ();
    if (localzzd != null) {
      return localzzd.zzbLc;
    }
    return null;
  }
  
  @Keep
  @Nullable
  public String getGmpAppId()
  {
    try
    {
      String str = zzzo.zzyC();
      return str;
    }
    catch (IllegalStateException localIllegalStateException)
    {
      this.zzbLa.zzMg().zzNT().zzm("getGoogleAppId failed with exception", localIllegalStateException);
    }
    return null;
  }
  
  @Keep
  @WorkerThread
  protected int getMaxUserProperties(@NonNull @Size(min=1L) String paramString)
  {
    return this.zzbLa.zzLV().getMaxUserProperties(paramString);
  }
  
  @Keep
  @WorkerThread
  protected Map<String, Object> getUserProperties(@Nullable String paramString1, @Nullable @Size(max=24L, min=1L) String paramString2, boolean paramBoolean)
  {
    return this.zzbLa.zzLV().getUserProperties(paramString1, paramString2, paramBoolean);
  }
  
  @WorkerThread
  public Map<String, Object> getUserProperties(boolean paramBoolean)
  {
    Object localObject = this.zzbLa.zzLV().zzaW(paramBoolean);
    ArrayMap localArrayMap = new ArrayMap(((List)localObject).size());
    localObject = ((List)localObject).iterator();
    while (((Iterator)localObject).hasNext())
    {
      zzaut localzzaut = (zzaut)((Iterator)localObject).next();
      localArrayMap.put(localzzaut.name, localzzaut.getValue());
    }
    return localArrayMap;
  }
  
  @Keep
  @WorkerThread
  protected Map<String, Object> getUserPropertiesAs(@NonNull @Size(min=1L) String paramString1, @Nullable String paramString2, @Nullable @Size(max=23L, min=1L) String paramString3, boolean paramBoolean)
  {
    return this.zzbLa.zzLV().getUserPropertiesAs(paramString1, paramString2, paramString3, paramBoolean);
  }
  
  public void logEvent(@NonNull @Size(max=40L, min=1L) String paramString, Bundle paramBundle)
  {
    Bundle localBundle = paramBundle;
    if (paramBundle == null) {
      localBundle = new Bundle();
    }
    this.zzbLa.zzMi().zzNb();
    if (!"_iap".equals(paramString))
    {
      int j = this.zzbLa.zzMc().zzfX(paramString);
      if (j != 0)
      {
        paramBundle = this.zzbLa.zzMc().zzc(paramString, this.zzbLa.zzMi().zzMH(), true);
        if (paramString != null) {}
        for (int i = paramString.length();; i = 0)
        {
          this.zzbLa.zzMc().zza(j, "_ev", paramBundle, i);
          return;
        }
      }
    }
    this.zzbLa.zzLV().zza("app", paramString, localBundle, true);
  }
  
  public byte[] logEventAndBundle(String paramString1, String paramString2, Bundle paramBundle)
  {
    Bundle localBundle = paramBundle;
    if (paramBundle == null) {
      localBundle = new Bundle();
    }
    return this.zzbLa.zzLV().zza(paramString1, "app", paramString2, localBundle);
  }
  
  @Keep
  public void logEventInternal(String paramString1, String paramString2, Bundle paramBundle)
  {
    Bundle localBundle = paramBundle;
    if (paramBundle == null) {
      localBundle = new Bundle();
    }
    this.zzbLa.zzLV().zzd(paramString1, paramString2, localBundle);
  }
  
  @Keep
  public void registerOnScreenChangeCallback(@NonNull zzc paramzzc)
  {
    this.zzbLa.zzLZ().registerOnScreenChangeCallback(paramzzc);
  }
  
  @Keep
  protected void setConditionalUserProperty(@NonNull ConditionalUserProperty paramConditionalUserProperty)
  {
    this.zzbLa.zzLV().setConditionalUserProperty(paramConditionalUserProperty);
  }
  
  @Keep
  protected void setConditionalUserPropertyAs(@NonNull ConditionalUserProperty paramConditionalUserProperty)
  {
    this.zzbLa.zzLV().setConditionalUserPropertyAs(paramConditionalUserProperty);
  }
  
  @Deprecated
  public void setMeasurementEnabled(boolean paramBoolean)
  {
    this.zzbLa.zzLV().setMeasurementEnabled(paramBoolean);
  }
  
  public void setMinimumSessionDuration(long paramLong)
  {
    this.zzbLa.zzLV().setMinimumSessionDuration(paramLong);
  }
  
  public void setSessionTimeoutDuration(long paramLong)
  {
    this.zzbLa.zzLV().setSessionTimeoutDuration(paramLong);
  }
  
  public void setUserId(String paramString)
  {
    zzb("app", "_id", paramString);
  }
  
  public void setUserProperty(@NonNull @Size(max=24L, min=1L) String paramString1, @Nullable @Size(max=36L) String paramString2)
  {
    int j = this.zzbLa.zzMc().zzfZ(paramString1);
    if (j != 0)
    {
      paramString2 = this.zzbLa.zzMc().zzc(paramString1, this.zzbLa.zzMi().zzMI(), true);
      if (paramString1 != null) {}
      for (int i = paramString1.length();; i = 0)
      {
        this.zzbLa.zzMc().zza(j, "_ev", paramString2, i);
        return;
      }
    }
    zzb("app", paramString1, paramString2);
  }
  
  @Keep
  public void unregisterOnScreenChangeCallback(@NonNull zzc paramzzc)
  {
    this.zzbLa.zzLZ().unregisterOnScreenChangeCallback(paramzzc);
  }
  
  @WorkerThread
  public void zza(zza paramzza)
  {
    this.zzbLa.zzLV().zza(paramzza);
  }
  
  public void zza(zzb paramzzb)
  {
    this.zzbLa.zzLV().zza(paramzzb);
  }
  
  public void zza(String paramString1, String paramString2, Bundle paramBundle, long paramLong)
  {
    if (paramBundle == null) {
      paramBundle = new Bundle();
    }
    for (;;)
    {
      this.zzbLa.zzLV().zzd(paramString1, paramString2, paramBundle, paramLong);
      return;
    }
  }
  
  public void zzb(String paramString1, String paramString2, Object paramObject)
  {
    zzc(paramString1, paramString2, paramObject);
  }
  
  public static class ConditionalUserProperty
  {
    @Keep
    public boolean mActive;
    @Keep
    public String mAppId;
    @Keep
    public long mCreationTimestamp;
    @Keep
    public String mExpiredEventName;
    @Keep
    public Bundle mExpiredEventParams;
    @Keep
    public String mName;
    @Keep
    public String mOrigin;
    @Keep
    public long mTimeToLive;
    @Keep
    public String mTimedOutEventName;
    @Keep
    public Bundle mTimedOutEventParams;
    @Keep
    public String mTriggerEventName;
    @Keep
    public long mTriggerTimeout;
    @Keep
    public String mTriggeredEventName;
    @Keep
    public Bundle mTriggeredEventParams;
    @Keep
    public long mTriggeredTimestamp;
    @Keep
    public Object mValue;
    
    public ConditionalUserProperty() {}
    
    public ConditionalUserProperty(ConditionalUserProperty paramConditionalUserProperty)
    {
      zzac.zzC(paramConditionalUserProperty);
      this.mAppId = paramConditionalUserProperty.mAppId;
      this.mOrigin = paramConditionalUserProperty.mOrigin;
      this.mCreationTimestamp = paramConditionalUserProperty.mCreationTimestamp;
      this.mName = paramConditionalUserProperty.mName;
      if (paramConditionalUserProperty.mValue != null)
      {
        this.mValue = zzauw.zzQ(paramConditionalUserProperty.mValue);
        if (this.mValue == null) {
          this.mValue = paramConditionalUserProperty.mValue;
        }
      }
      this.mValue = paramConditionalUserProperty.mValue;
      this.mActive = paramConditionalUserProperty.mActive;
      this.mTriggerEventName = paramConditionalUserProperty.mTriggerEventName;
      this.mTriggerTimeout = paramConditionalUserProperty.mTriggerTimeout;
      this.mTimedOutEventName = paramConditionalUserProperty.mTimedOutEventName;
      if (paramConditionalUserProperty.mTimedOutEventParams != null) {
        this.mTimedOutEventParams = new Bundle(paramConditionalUserProperty.mTimedOutEventParams);
      }
      this.mTriggeredEventName = paramConditionalUserProperty.mTriggeredEventName;
      if (paramConditionalUserProperty.mTriggeredEventParams != null) {
        this.mTriggeredEventParams = new Bundle(paramConditionalUserProperty.mTriggeredEventParams);
      }
      this.mTriggeredTimestamp = paramConditionalUserProperty.mTriggeredTimestamp;
      this.mTimeToLive = paramConditionalUserProperty.mTimeToLive;
      this.mExpiredEventName = paramConditionalUserProperty.mExpiredEventName;
      if (paramConditionalUserProperty.mExpiredEventParams != null) {
        this.mExpiredEventParams = new Bundle(paramConditionalUserProperty.mExpiredEventParams);
      }
    }
  }
  
  public static final class Event
    extends FirebaseAnalytics.Event
  {
    public static final String IN_APP_PURCHASE = "_iap";
    public static final Map<String, String> zzbLb = zze.zzb(new String[] { "app_clear_data", "app_exception", "app_remove", "app_upgrade", "app_install", "app_update", "firebase_campaign", "error", "first_open", "in_app_purchase", "notification_dismiss", "notification_foreground", "notification_open", "notification_receive", "os_update", "session_start", "user_engagement", "firebase_ad_exposure", "firebase_adunit_exposure" }, new String[] { "_cd", "_ae", "_ui", "_in", "_ug", "_au", "_cmp", "_err", "_f", "_iap", "_nd", "_nf", "_no", "_nr", "_ou", "_s", "_e", "_xa", "_xu" });
  }
  
  public static final class Param
    extends FirebaseAnalytics.Param
  {
    public static final Map<String, String> MAPPED_PARAM_NAMES = zze.zzb(new String[] { "firebase_conversion", "engagement_time_msec", "exposure_time", "ad_event_id", "ad_unit_id", "firebase_error", "firebase_error_value", "firebase_error_length", "debug", "realtime", "firebase_event_origin", "firebase_screen", "firebase_screen_class", "firebase_screen_id", "message_device_time", "message_id", "message_name", "message_time", "previous_app_version", "previous_os_version", "topic", "update_with_analytics", "previous_first_open_count", "system_app", "system_app_update", "previous_install_count" }, new String[] { "_c", "_et", "_xt", "_aeid", "_ai", "_err", "_ev", "_el", "_dbg", "_r", "_o", "_sn", "_sc", "_si", "_ndt", "_nmid", "_nmn", "_nmt", "_pv", "_po", "_nt", "_uwa", "_pfo", "_sys", "_sysu", "_pin" });
    public static final String PRODUCT_ID = "product_id";
    public static final String PRODUCT_NAME = "product_name";
  }
  
  public static abstract interface zza
  {
    @WorkerThread
    public abstract void zzb(String paramString1, String paramString2, Bundle paramBundle, long paramLong);
  }
  
  public static abstract interface zzb
  {
    @WorkerThread
    public abstract void zzc(String paramString1, String paramString2, Bundle paramBundle, long paramLong);
  }
  
  public static abstract interface zzc
  {
    @MainThread
    public abstract boolean zza(AppMeasurement.zzd paramzzd1, AppMeasurement.zzd paramzzd2);
  }
  
  public static class zzd
  {
    public String zzbLc;
    public String zzbLd;
    public long zzbLe;
    
    public zzd() {}
    
    public zzd(zzd paramzzd)
    {
      this.zzbLc = paramzzd.zzbLc;
      this.zzbLd = paramzzd.zzbLd;
      this.zzbLe = paramzzd.zzbLe;
    }
  }
  
  public static final class zze
    extends FirebaseAnalytics.UserProperty
  {
    public static final Map<String, String> zzbLf = zze.zzb(new String[] { "firebase_last_notification", "first_open_time", "last_deep_link_referrer", "user_id" }, new String[] { "_ln", "_fot", "_ldl", "_id" });
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/measurement/AppMeasurement.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */