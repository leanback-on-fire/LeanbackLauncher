package com.google.android.gms.internal;

import com.google.android.gms.common.internal.zzac;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

public final class zzatv
{
  public static zza<Boolean> zzbME = zza.zzm("measurement.service_enabled", true);
  public static zza<Boolean> zzbMF = zza.zzm("measurement.service_client_enabled", true);
  public static zza<Boolean> zzbMG = zza.zzm("measurement.log_third_party_store_events_enabled", false);
  public static zza<Boolean> zzbMH = zza.zzm("measurement.log_installs_enabled", false);
  public static zza<Boolean> zzbMI = zza.zzm("measurement.log_upgrades_enabled", false);
  public static zza<Boolean> zzbMJ = zza.zzm("measurement.log_androidId_enabled", false);
  public static zza<String> zzbMK = zza.zzk("measurement.log_tag", "FA", "FA-SVC");
  public static zza<Long> zzbML = zza.zzj("measurement.ad_id_cache_time", 10000L);
  public static zza<Long> zzbMM = zza.zzj("measurement.monitoring.sample_period_millis", 86400000L);
  public static zza<Long> zzbMN = zza.zzc("measurement.config.cache_time", 86400000L, 3600000L);
  public static zza<String> zzbMO = zza.zzai("measurement.config.url_scheme", "https");
  public static zza<String> zzbMP = zza.zzai("measurement.config.url_authority", "app-measurement.com");
  public static zza<Integer> zzbMQ = zza.zzC("measurement.upload.max_bundles", 100);
  public static zza<Integer> zzbMR = zza.zzC("measurement.upload.max_batch_size", 65536);
  public static zza<Integer> zzbMS = zza.zzC("measurement.upload.max_bundle_size", 65536);
  public static zza<Integer> zzbMT = zza.zzC("measurement.upload.max_events_per_bundle", 1000);
  public static zza<Integer> zzbMU = zza.zzC("measurement.upload.max_events_per_day", 100000);
  public static zza<Integer> zzbMV = zza.zzC("measurement.upload.max_error_events_per_day", 1000);
  public static zza<Integer> zzbMW = zza.zzC("measurement.upload.max_public_events_per_day", 50000);
  public static zza<Integer> zzbMX = zza.zzC("measurement.upload.max_conversions_per_day", 500);
  public static zza<Integer> zzbMY = zza.zzC("measurement.upload.max_realtime_events_per_day", 10);
  public static zza<Integer> zzbMZ = zza.zzC("measurement.store.max_stored_events_per_app", 100000);
  public static zza<String> zzbNa = zza.zzai("measurement.upload.url", "https://app-measurement.com/a");
  public static zza<Long> zzbNb = zza.zzj("measurement.upload.backoff_period", 43200000L);
  public static zza<Long> zzbNc = zza.zzj("measurement.upload.window_interval", 3600000L);
  public static zza<Long> zzbNd = zza.zzj("measurement.upload.interval", 3600000L);
  public static zza<Long> zzbNe = zza.zzj("measurement.upload.realtime_upload_interval", 10000L);
  public static zza<Long> zzbNf = zza.zzj("measurement.upload.debug_upload_interval", 1000L);
  public static zza<Long> zzbNg = zza.zzj("measurement.upload.minimum_delay", 500L);
  public static zza<Long> zzbNh = zza.zzj("measurement.alarm_manager.minimum_interval", 60000L);
  public static zza<Long> zzbNi = zza.zzj("measurement.upload.stale_data_deletion_interval", 86400000L);
  public static zza<Long> zzbNj = zza.zzj("measurement.upload.refresh_blacklisted_config_interval", 604800000L);
  public static zza<Long> zzbNk = zza.zzj("measurement.upload.initial_upload_delay_time", 15000L);
  public static zza<Long> zzbNl = zza.zzj("measurement.upload.retry_time", 1800000L);
  public static zza<Integer> zzbNm = zza.zzC("measurement.upload.retry_count", 6);
  public static zza<Long> zzbNn = zza.zzj("measurement.upload.max_queue_time", 2419200000L);
  public static zza<Integer> zzbNo = zza.zzC("measurement.lifetimevalue.max_currency_tracked", 4);
  public static zza<Integer> zzbNp = zza.zzC("measurement.audience.filter_result_max_count", 200);
  public static zza<Long> zzbNq = zza.zzj("measurement.service_client.idle_disconnect_millis", 5000L);
  
  public static final class zza<V>
  {
    private static Collection<zza<?>> zzaPA;
    private final String zzBp;
    private final V zzaeS;
    private final zzaav<V> zzaeT;
    private V zzaeU;
    
    private zza(String paramString, zzaav<V> paramzzaav, V paramV)
    {
      zzac.zzC(paramzzaav);
      this.zzaeT = paramzzaav;
      this.zzaeS = paramV;
      this.zzBp = paramString;
    }
    
    static zza<Integer> zzC(String paramString, int paramInt)
    {
      return zzg(paramString, paramInt, paramInt);
    }
    
    static zza<String> zzai(String paramString1, String paramString2)
    {
      return zzk(paramString1, paramString2, paramString2);
    }
    
    static zza<Boolean> zzb(String paramString, boolean paramBoolean1, boolean paramBoolean2)
    {
      return new zza(paramString, zzaav.zzk(paramString, paramBoolean2), Boolean.valueOf(paramBoolean1));
    }
    
    static zza<Long> zzc(String paramString, long paramLong1, long paramLong2)
    {
      return new zza(paramString, zzaav.zza(paramString, Long.valueOf(paramLong2)), Long.valueOf(paramLong1));
    }
    
    static zza<Integer> zzg(String paramString, int paramInt1, int paramInt2)
    {
      return new zza(paramString, zzaav.zza(paramString, Integer.valueOf(paramInt2)), Integer.valueOf(paramInt1));
    }
    
    static zza<Long> zzj(String paramString, long paramLong)
    {
      return zzc(paramString, paramLong, paramLong);
    }
    
    static zza<String> zzk(String paramString1, String paramString2, String paramString3)
    {
      return new zza(paramString1, zzaav.zzI(paramString1, paramString3), paramString2);
    }
    
    static zza<Boolean> zzm(String paramString, boolean paramBoolean)
    {
      return zzb(paramString, paramBoolean, paramBoolean);
    }
    
    public static void zzyX()
    {
      try
      {
        if (zzaPA != null)
        {
          Iterator localIterator = zzaPA.iterator();
          while (localIterator.hasNext()) {
            ((zza)localIterator.next()).resetOverride();
          }
          zzaPA.clear();
        }
      }
      finally {}
    }
    
    public V get()
    {
      if (this.zzaeU != null) {
        return (V)this.zzaeU;
      }
      return (V)this.zzaeS;
    }
    
    public V get(V paramV)
    {
      Object localObject;
      if (this.zzaeU != null) {
        localObject = this.zzaeU;
      }
      do
      {
        return (V)localObject;
        localObject = paramV;
      } while (paramV != null);
      return (V)this.zzaeS;
    }
    
    public String getKey()
    {
      return this.zzBp;
    }
    
    public void override(V paramV)
    {
      this.zzaeT.override(paramV);
      this.zzaeU = paramV;
      try
      {
        if (zzaPA == null) {
          zzaPA = new HashSet();
        }
        zzaPA.add(this);
        return;
      }
      finally {}
    }
    
    public void resetOverride()
    {
      this.zzaeU = null;
      this.zzaeT.resetOverride();
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/internal/zzatv.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */