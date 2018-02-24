package com.google.android.gms.internal;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;
import android.util.Pair;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.ads.identifier.AdvertisingIdClient.Info;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.util.Clock;
import com.google.firebase.iid.zzc;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Locale;

class zzaud
  extends zzauk
{
  static final Pair<String, Long> zzbNT = new Pair("", Long.valueOf(0L));
  private SharedPreferences zzafw;
  public final zzc zzbNU = new zzc("health_monitor", zzMi().zzoY(), null);
  public final zzb zzbNV = new zzb("last_upload", 0L);
  public final zzb zzbNW = new zzb("last_upload_attempt", 0L);
  public final zzb zzbNX = new zzb("backoff", 0L);
  public final zzb zzbNY = new zzb("last_delete_stale", 0L);
  public final zzb zzbNZ = new zzb("midnight_offset", 0L);
  private String zzbOa;
  private boolean zzbOb;
  private long zzbOc;
  private String zzbOd;
  private long zzbOe;
  private final Object zzbOf = new Object();
  private SecureRandom zzbOg;
  public final zzb zzbOh = new zzb("time_before_start", 10000L);
  public final zzb zzbOi = new zzb("session_timeout", 1800000L);
  public final zza zzbOj = new zza("start_new_session", true);
  public final zzb zzbOk = new zzb("last_pause_time", 0L);
  public final zzb zzbOl = new zzb("time_active", 0L);
  public boolean zzbOm;
  
  zzaud(zzauh paramzzauh)
  {
    super(paramzzauh);
  }
  
  @WorkerThread
  private SecureRandom zzOb()
  {
    zzmW();
    if (this.zzbOg == null) {
      this.zzbOg = new SecureRandom();
    }
    return this.zzbOg;
  }
  
  @WorkerThread
  private SharedPreferences zzOe()
  {
    zzmW();
    zznA();
    return this.zzafw;
  }
  
  protected void onInitialize()
  {
    this.zzafw = getContext().getSharedPreferences("com.google.android.gms.measurement.prefs", 0);
    this.zzbOm = this.zzafw.getBoolean("has_been_opened", false);
    if (!this.zzbOm)
    {
      SharedPreferences.Editor localEditor = this.zzafw.edit();
      localEditor.putBoolean("has_been_opened", true);
      localEditor.apply();
    }
  }
  
  @WorkerThread
  void setMeasurementEnabled(boolean paramBoolean)
  {
    zzmW();
    zzMg().zzNZ().zzm("Setting measurementEnabled", Boolean.valueOf(paramBoolean));
    SharedPreferences.Editor localEditor = zzOe().edit();
    localEditor.putBoolean("measurement_enabled", paramBoolean);
    localEditor.apply();
  }
  
  @WorkerThread
  String zzMl()
  {
    zzmW();
    try
    {
      String str = zzc.zzake().getId();
      return str;
    }
    catch (IllegalStateException localIllegalStateException)
    {
      zzMg().zzNV().log("Failed to retrieve Firebase Instance Id");
    }
    return null;
  }
  
  @WorkerThread
  String zzOc()
  {
    byte[] arrayOfByte = new byte[16];
    zzOb().nextBytes(arrayOfByte);
    return String.format(Locale.US, "%032x", new Object[] { new BigInteger(1, arrayOfByte) });
  }
  
  @WorkerThread
  long zzOd()
  {
    zznA();
    zzmW();
    long l2 = this.zzbNZ.get();
    long l1 = l2;
    if (l2 == 0L)
    {
      l1 = zzOb().nextInt(86400000) + 1;
      this.zzbNZ.set(l1);
    }
    return l1;
  }
  
  @WorkerThread
  String zzOf()
  {
    zzmW();
    return zzOe().getString("gmp_app_id", null);
  }
  
  String zzOg()
  {
    synchronized (this.zzbOf)
    {
      if (Math.abs(zznq().elapsedRealtime() - this.zzbOe) < 1000L)
      {
        String str = this.zzbOd;
        return str;
      }
      return null;
    }
  }
  
  @WorkerThread
  Boolean zzOh()
  {
    zzmW();
    if (!zzOe().contains("use_service")) {
      return null;
    }
    return Boolean.valueOf(zzOe().getBoolean("use_service", false));
  }
  
  @WorkerThread
  void zzOi()
  {
    boolean bool1 = true;
    zzmW();
    zzMg().zzNZ().log("Clearing collection preferences.");
    boolean bool2 = zzOe().contains("measurement_enabled");
    if (bool2) {
      bool1 = zzaU(true);
    }
    SharedPreferences.Editor localEditor = zzOe().edit();
    localEditor.clear();
    localEditor.apply();
    if (bool2) {
      setMeasurementEnabled(bool1);
    }
  }
  
  @WorkerThread
  protected String zzOj()
  {
    zzmW();
    String str1 = zzOe().getString("previous_os_version", null);
    String str2 = zzLX().zzNN();
    if ((!TextUtils.isEmpty(str2)) && (!str2.equals(str1)))
    {
      SharedPreferences.Editor localEditor = zzOe().edit();
      localEditor.putString("previous_os_version", str2);
      localEditor.apply();
    }
    return str1;
  }
  
  @WorkerThread
  void zzaT(boolean paramBoolean)
  {
    zzmW();
    zzMg().zzNZ().zzm("Setting useService", Boolean.valueOf(paramBoolean));
    SharedPreferences.Editor localEditor = zzOe().edit();
    localEditor.putBoolean("use_service", paramBoolean);
    localEditor.apply();
  }
  
  @WorkerThread
  boolean zzaU(boolean paramBoolean)
  {
    zzmW();
    return zzOe().getBoolean("measurement_enabled", paramBoolean);
  }
  
  @NonNull
  @WorkerThread
  Pair<String, Boolean> zzfJ(String paramString)
  {
    zzmW();
    long l = zznq().elapsedRealtime();
    if ((this.zzbOa != null) && (l < this.zzbOc)) {
      return new Pair(this.zzbOa, Boolean.valueOf(this.zzbOb));
    }
    this.zzbOc = (l + zzMi().zzfp(paramString));
    AdvertisingIdClient.setShouldSkipGmsCoreVersionCheck(true);
    try
    {
      paramString = AdvertisingIdClient.getAdvertisingIdInfo(getContext());
      this.zzbOa = paramString.getId();
      if (this.zzbOa == null) {
        this.zzbOa = "";
      }
      this.zzbOb = paramString.isLimitAdTrackingEnabled();
    }
    catch (Throwable paramString)
    {
      for (;;)
      {
        zzMg().zzNY().zzm("Unable to get advertising id", paramString);
        this.zzbOa = "";
      }
    }
    AdvertisingIdClient.setShouldSkipGmsCoreVersionCheck(false);
    return new Pair(this.zzbOa, Boolean.valueOf(this.zzbOb));
  }
  
  @WorkerThread
  String zzfK(String paramString)
  {
    zzmW();
    paramString = (String)zzfJ(paramString).first;
    MessageDigest localMessageDigest = zzauw.zzbJ("MD5");
    if (localMessageDigest == null) {
      return null;
    }
    return String.format(Locale.US, "%032X", new Object[] { new BigInteger(1, localMessageDigest.digest(paramString.getBytes())) });
  }
  
  @WorkerThread
  void zzfL(String paramString)
  {
    zzmW();
    SharedPreferences.Editor localEditor = zzOe().edit();
    localEditor.putString("gmp_app_id", paramString);
    localEditor.apply();
  }
  
  void zzfM(String paramString)
  {
    synchronized (this.zzbOf)
    {
      this.zzbOd = paramString;
      this.zzbOe = zznq().elapsedRealtime();
      return;
    }
  }
  
  public final class zza
  {
    private final String zzBp;
    private boolean zzaKx;
    private final boolean zzbOn;
    private boolean zzbOo;
    
    public zza(String paramString, boolean paramBoolean)
    {
      zzac.zzdc(paramString);
      this.zzBp = paramString;
      this.zzbOn = paramBoolean;
    }
    
    @WorkerThread
    private void zzOk()
    {
      if (this.zzbOo) {
        return;
      }
      this.zzbOo = true;
      this.zzaKx = zzaud.zza(zzaud.this).getBoolean(this.zzBp, this.zzbOn);
    }
    
    @WorkerThread
    public boolean get()
    {
      zzOk();
      return this.zzaKx;
    }
    
    @WorkerThread
    public void set(boolean paramBoolean)
    {
      SharedPreferences.Editor localEditor = zzaud.zza(zzaud.this).edit();
      localEditor.putBoolean(this.zzBp, paramBoolean);
      localEditor.apply();
      this.zzaKx = paramBoolean;
    }
  }
  
  public final class zzb
  {
    private final String zzBp;
    private long zzacO;
    private boolean zzbOo;
    private final long zzbOq;
    
    public zzb(String paramString, long paramLong)
    {
      zzac.zzdc(paramString);
      this.zzBp = paramString;
      this.zzbOq = paramLong;
    }
    
    @WorkerThread
    private void zzOk()
    {
      if (this.zzbOo) {
        return;
      }
      this.zzbOo = true;
      this.zzacO = zzaud.zza(zzaud.this).getLong(this.zzBp, this.zzbOq);
    }
    
    @WorkerThread
    public long get()
    {
      zzOk();
      return this.zzacO;
    }
    
    @WorkerThread
    public void set(long paramLong)
    {
      SharedPreferences.Editor localEditor = zzaud.zza(zzaud.this).edit();
      localEditor.putLong(this.zzBp, paramLong);
      localEditor.apply();
      this.zzacO = paramLong;
    }
  }
  
  public final class zzc
  {
    private final long zzafA;
    final String zzbOr;
    private final String zzbOs;
    private final String zzbOt;
    
    private zzc(String paramString, long paramLong)
    {
      zzac.zzdc(paramString);
      if (paramLong > 0L) {}
      for (boolean bool = true;; bool = false)
      {
        zzac.zzaw(bool);
        this.zzbOr = String.valueOf(paramString).concat(":start");
        this.zzbOs = String.valueOf(paramString).concat(":count");
        this.zzbOt = String.valueOf(paramString).concat(":value");
        this.zzafA = paramLong;
        return;
      }
    }
    
    @WorkerThread
    private long getStartTimeMillis()
    {
      return zzaud.zzc(zzaud.this).getLong(this.zzbOr, 0L);
    }
    
    @WorkerThread
    private void zzpI()
    {
      zzaud.this.zzmW();
      long l = zzaud.this.zznq().currentTimeMillis();
      SharedPreferences.Editor localEditor = zzaud.zza(zzaud.this).edit();
      localEditor.remove(this.zzbOs);
      localEditor.remove(this.zzbOt);
      localEditor.putLong(this.zzbOr, l);
      localEditor.apply();
    }
    
    @WorkerThread
    private long zzpJ()
    {
      zzaud.this.zzmW();
      long l = getStartTimeMillis();
      if (l == 0L)
      {
        zzpI();
        return 0L;
      }
      return Math.abs(l - zzaud.this.zznq().currentTimeMillis());
    }
    
    @WorkerThread
    public void zzbE(String paramString)
    {
      zzk(paramString, 1L);
    }
    
    @WorkerThread
    public void zzk(String paramString, long paramLong)
    {
      zzaud.this.zzmW();
      if (getStartTimeMillis() == 0L) {
        zzpI();
      }
      String str = paramString;
      if (paramString == null) {
        str = "";
      }
      long l = zzaud.zza(zzaud.this).getLong(this.zzbOs, 0L);
      if (l <= 0L)
      {
        paramString = zzaud.zza(zzaud.this).edit();
        paramString.putString(this.zzbOt, str);
        paramString.putLong(this.zzbOs, paramLong);
        paramString.apply();
        return;
      }
      if ((zzaud.zzb(zzaud.this).nextLong() & 0x7FFFFFFFFFFFFFFF) < Long.MAX_VALUE / (l + paramLong) * paramLong) {}
      for (int i = 1;; i = 0)
      {
        paramString = zzaud.zza(zzaud.this).edit();
        if (i != 0) {
          paramString.putString(this.zzbOt, str);
        }
        paramString.putLong(this.zzbOs, l + paramLong);
        paramString.apply();
        return;
      }
    }
    
    @WorkerThread
    public Pair<String, Long> zzpK()
    {
      zzaud.this.zzmW();
      long l = zzpJ();
      if (l < this.zzafA) {
        return null;
      }
      if (l > this.zzafA * 2L)
      {
        zzpI();
        return null;
      }
      String str = zzaud.zzc(zzaud.this).getString(this.zzbOt, null);
      l = zzaud.zzc(zzaud.this).getLong(this.zzbOs, 0L);
      zzpI();
      if ((str == null) || (l <= 0L)) {
        return zzaud.zzbNT;
      }
      return new Pair(str, Long.valueOf(l));
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/internal/zzaud.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */