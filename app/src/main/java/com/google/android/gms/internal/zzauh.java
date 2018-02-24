package com.google.android.gms.internal;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.support.annotation.NonNull;
import android.support.annotation.Size;
import android.support.annotation.WorkerThread;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.Pair;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.util.Clock;
import com.google.android.gms.measurement.AppMeasurement;
import com.google.firebase.analytics.FirebaseAnalytics;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class zzauh
{
  private static volatile zzauh zzbOU;
  private final Context mContext;
  private final boolean zzady;
  private final zzatl zzbOV;
  private final zzaud zzbOW;
  private final zzaua zzbOX;
  private final zzaug zzbOY;
  private final zzauq zzbOZ;
  private final zzauf zzbPa;
  private final AppMeasurement zzbPb;
  private final FirebaseAnalytics zzbPc;
  private final zzauw zzbPd;
  private final zzatm zzbPe;
  private final zzaty zzbPf;
  private final zzaub zzbPg;
  private final zzaun zzbPh;
  private final zzauo zzbPi;
  private final zzato zzbPj;
  private final zzaum zzbPk;
  private final zzatx zzbPl;
  private final zzauc zzbPm;
  private final zzaus zzbPn;
  private final zzati zzbPo;
  private final zzate zzbPp;
  private boolean zzbPq;
  private Boolean zzbPr;
  private long zzbPs;
  private FileLock zzbPt;
  private FileChannel zzbPu;
  private List<Long> zzbPv;
  private int zzbPw;
  private int zzbPx;
  private long zzbPy;
  protected long zzbPz;
  private final Clock zzvi;
  
  zzauh(zzaul paramzzaul)
  {
    zzac.zzC(paramzzaul);
    this.mContext = paramzzaul.mContext;
    this.zzbPy = -1L;
    this.zzvi = paramzzaul.zzn(this);
    this.zzbOV = paramzzaul.zza(this);
    Object localObject = paramzzaul.zzb(this);
    ((zzaud)localObject).initialize();
    this.zzbOW = ((zzaud)localObject);
    localObject = paramzzaul.zzc(this);
    ((zzaua)localObject).initialize();
    this.zzbOX = ((zzaua)localObject);
    zzMg().zzNX().zzm("App measurement is starting up, version", Long.valueOf(zzMi().zzMq()));
    zzMi().zzNb();
    zzMg().zzNX().log("To enable debug logging run: adb shell setprop log.tag.FA VERBOSE");
    localObject = paramzzaul.zzj(this);
    ((zzauw)localObject).initialize();
    this.zzbPd = ((zzauw)localObject);
    localObject = paramzzaul.zzq(this);
    ((zzato)localObject).initialize();
    this.zzbPj = ((zzato)localObject);
    localObject = paramzzaul.zzr(this);
    ((zzatx)localObject).initialize();
    this.zzbPl = ((zzatx)localObject);
    zzMi().zzNb();
    localObject = ((zzatx)localObject).getAppId();
    if (zzMc().zzgh((String)localObject))
    {
      zzMg().zzNX().log("Faster debug mode event logging enabled. To disable, run:\n  adb shell setprop debug.firebase.analytics.app .none.");
      zzMg().zzNY().log("Debug-level message logging enabled");
      localObject = paramzzaul.zzk(this);
      ((zzatm)localObject).initialize();
      this.zzbPe = ((zzatm)localObject);
      localObject = paramzzaul.zzl(this);
      ((zzaty)localObject).initialize();
      this.zzbPf = ((zzaty)localObject);
      localObject = paramzzaul.zzu(this);
      ((zzati)localObject).initialize();
      this.zzbPo = ((zzati)localObject);
      this.zzbPp = paramzzaul.zzv(this);
      localObject = paramzzaul.zzm(this);
      ((zzaub)localObject).initialize();
      this.zzbPg = ((zzaub)localObject);
      localObject = paramzzaul.zzo(this);
      ((zzaun)localObject).initialize();
      this.zzbPh = ((zzaun)localObject);
      localObject = paramzzaul.zzp(this);
      ((zzauo)localObject).initialize();
      this.zzbPi = ((zzauo)localObject);
      localObject = paramzzaul.zzi(this);
      ((zzaum)localObject).initialize();
      this.zzbPk = ((zzaum)localObject);
      localObject = paramzzaul.zzt(this);
      ((zzaus)localObject).initialize();
      this.zzbPn = ((zzaus)localObject);
      this.zzbPm = paramzzaul.zzs(this);
      this.zzbPb = paramzzaul.zzh(this);
      this.zzbPc = paramzzaul.zzg(this);
      localObject = paramzzaul.zze(this);
      ((zzauq)localObject).initialize();
      this.zzbOZ = ((zzauq)localObject);
      localObject = paramzzaul.zzf(this);
      ((zzauf)localObject).initialize();
      this.zzbPa = ((zzauf)localObject);
      paramzzaul = paramzzaul.zzd(this);
      paramzzaul.initialize();
      this.zzbOY = paramzzaul;
      if (this.zzbPw != this.zzbPx) {
        zzMg().zzNT().zze("Not all components initialized", Integer.valueOf(this.zzbPw), Integer.valueOf(this.zzbPx));
      }
      this.zzady = true;
      this.zzbOV.zzNb();
      if (!(this.mContext.getApplicationContext() instanceof Application)) {
        break label551;
      }
      int i = Build.VERSION.SDK_INT;
      zzLV().zzOL();
    }
    for (;;)
    {
      this.zzbOY.zzp(new Runnable()
      {
        public void run()
        {
          zzauh.this.start();
        }
      });
      return;
      zzaua.zza localzza = zzMg().zzNX();
      localObject = String.valueOf(localObject);
      if (((String)localObject).length() != 0) {}
      for (localObject = "To enable faster debug mode event logging run:\n  adb shell setprop debug.firebase.analytics.app ".concat((String)localObject);; localObject = new String("To enable faster debug mode event logging run:\n  adb shell setprop debug.firebase.analytics.app "))
      {
        localzza.log((String)localObject);
        break;
      }
      label551:
      zzMg().zzNV().log("Application context is not an Application");
    }
  }
  
  private boolean zzOC()
  {
    zzmW();
    zznA();
    return (zzMb().zzNE()) || (!TextUtils.isEmpty(zzMb().zzNy()));
  }
  
  @WorkerThread
  private void zzOD()
  {
    zzmW();
    zznA();
    if (!zzOH()) {
      return;
    }
    if (this.zzbPz > 0L)
    {
      l1 = 3600000L - Math.abs(zznq().elapsedRealtime() - this.zzbPz);
      if (l1 > 0L)
      {
        zzMg().zzNZ().zzm("Upload has been suspended. Will update scheduling later in approximately ms", Long.valueOf(l1));
        zzOu().unregister();
        zzOv().cancel();
        return;
      }
      this.zzbPz = 0L;
    }
    if ((!zzOo()) || (!zzOC()))
    {
      zzOu().unregister();
      zzOv().cancel();
      return;
    }
    long l2 = zzOE();
    if (l2 == 0L)
    {
      zzOu().unregister();
      zzOv().cancel();
      return;
    }
    if (!zzOt().isNetworkConnected())
    {
      zzOu().zzpw();
      zzOv().cancel();
      return;
    }
    long l3 = zzMh().zzbNX.get();
    long l4 = zzMi().zzNn();
    long l1 = l2;
    if (!zzMc().zzk(l3, l4)) {
      l1 = Math.max(l2, l3 + l4);
    }
    zzOu().unregister();
    l2 = l1 - zznq().currentTimeMillis();
    l1 = l2;
    if (l2 <= 0L)
    {
      l1 = zzMi().zzNr();
      zzMh().zzbNV.set(zznq().currentTimeMillis());
    }
    zzMg().zzNZ().zzm("Upload scheduled in approximately ms", Long.valueOf(l1));
    zzOv().zzC(l1);
  }
  
  private long zzOE()
  {
    long l3 = zznq().currentTimeMillis();
    long l1 = zzMi().zzNu();
    long l2;
    label82:
    long l6;
    long l5;
    long l4;
    if ((zzMb().zzNF()) || (zzMb().zzNz()))
    {
      i = 1;
      if (i == 0) {
        break label155;
      }
      String str = zzMi().zzNx();
      if ((TextUtils.isEmpty(str)) || (".none.".equals(str))) {
        break label143;
      }
      l2 = zzMi().zzNq();
      l6 = zzMh().zzbNV.get();
      l5 = zzMh().zzbNW.get();
      l4 = Math.max(zzMb().zzNC(), zzMb().zzND());
      if (l4 != 0L) {
        break label167;
      }
      l2 = 0L;
    }
    label143:
    label155:
    label167:
    do
    {
      do
      {
        return l2;
        i = 0;
        break;
        l2 = zzMi().zzNp();
        break label82;
        l2 = zzMi().zzNo();
        break label82;
        l4 = l3 - Math.abs(l4 - l3);
        l6 = Math.abs(l6 - l3);
        l5 = l3 - Math.abs(l5 - l3);
        l6 = Math.max(l3 - l6, l5);
        l3 = l4 + l1;
        l1 = l3;
        if (i != 0)
        {
          l1 = l3;
          if (l6 > 0L) {
            l1 = Math.min(l4, l6) + l2;
          }
        }
        if (!zzMc().zzk(l6, l2)) {
          l1 = l6 + l2;
        }
        l2 = l1;
      } while (l5 == 0L);
      l2 = l1;
    } while (l5 < l4);
    int i = 0;
    for (;;)
    {
      if (i >= zzMi().zzNw()) {
        break label335;
      }
      l1 += (1 << i) * zzMi().zzNv();
      l2 = l1;
      if (l1 > l5) {
        break;
      }
      i += 1;
    }
    label335:
    return 0L;
  }
  
  private void zza(zzauj paramzzauj)
  {
    if (paramzzauj == null) {
      throw new IllegalStateException("Component not created");
    }
  }
  
  private void zza(zzauk paramzzauk)
  {
    if (paramzzauk == null) {
      throw new IllegalStateException("Component not created");
    }
    if (!paramzzauk.isInitialized()) {
      throw new IllegalStateException("Component not initialized");
    }
  }
  
  private boolean zza(zzatp paramzzatp)
  {
    if (paramzzatp.zzbMv == null) {}
    Object localObject;
    boolean bool;
    do
    {
      return false;
      localObject = paramzzatp.zzbMv.iterator();
      while (((Iterator)localObject).hasNext()) {
        if ("_r".equals((String)((Iterator)localObject).next())) {
          return true;
        }
      }
      bool = zzMd().zzal(paramzzatp.mAppId, paramzzatp.mName);
      localObject = zzMb().zza(zzOz(), paramzzatp.mAppId, false, false, false, false, false);
    } while ((!bool) || (((zzatm.zza)localObject).zzbMn >= zzMi().zzfo(paramzzatp.mAppId)));
    return true;
  }
  
  private zzauz.zza[] zza(String paramString, zzauz.zzg[] paramArrayOfzzg, zzauz.zzb[] paramArrayOfzzb)
  {
    zzac.zzdc(paramString);
    return zzLU().zza(paramString, paramArrayOfzzb, paramArrayOfzzg);
  }
  
  public static zzauh zzbR(Context paramContext)
  {
    zzac.zzC(paramContext);
    zzac.zzC(paramContext.getApplicationContext());
    if (zzbOU == null) {}
    try
    {
      if (zzbOU == null) {
        zzbOU = new zzaul(paramContext).zzOK();
      }
      return zzbOU;
    }
    finally {}
  }
  
  @WorkerThread
  private void zzf(zzatg paramzzatg)
  {
    int k = 1;
    zzmW();
    zznA();
    zzac.zzC(paramzzatg);
    zzac.zzdc(paramzzatg.packageName);
    zzatf localzzatf2 = zzMb().zzfx(paramzzatg.packageName);
    String str = zzMh().zzfK(paramzzatg.packageName);
    int i = 0;
    zzatf localzzatf1;
    int j;
    if (localzzatf2 == null)
    {
      localzzatf1 = new zzatf(this, paramzzatg.packageName);
      localzzatf1.zzfg(zzMh().zzOc());
      localzzatf1.zzfi(str);
      i = 1;
      j = i;
      if (!TextUtils.isEmpty(paramzzatg.zzbLI))
      {
        j = i;
        if (!paramzzatg.zzbLI.equals(localzzatf1.getGmpAppId()))
        {
          localzzatf1.zzfh(paramzzatg.zzbLI);
          j = 1;
        }
      }
      i = j;
      if (!TextUtils.isEmpty(paramzzatg.zzbLQ))
      {
        i = j;
        if (!paramzzatg.zzbLQ.equals(localzzatf1.zzMl()))
        {
          localzzatf1.zzfj(paramzzatg.zzbLQ);
          i = 1;
        }
      }
      j = i;
      if (paramzzatg.zzbLK != 0L)
      {
        j = i;
        if (paramzzatg.zzbLK != localzzatf1.zzMq())
        {
          localzzatf1.zzam(paramzzatg.zzbLK);
          j = 1;
        }
      }
      i = j;
      if (!TextUtils.isEmpty(paramzzatg.zzbAI))
      {
        i = j;
        if (!paramzzatg.zzbAI.equals(localzzatf1.getAppVersion()))
        {
          localzzatf1.setAppVersion(paramzzatg.zzbAI);
          i = 1;
        }
      }
      if (paramzzatg.zzbLP != localzzatf1.zzMo())
      {
        localzzatf1.zzal(paramzzatg.zzbLP);
        i = 1;
      }
      j = i;
      if (paramzzatg.zzbLJ != null)
      {
        j = i;
        if (!paramzzatg.zzbLJ.equals(localzzatf1.zzMp()))
        {
          localzzatf1.zzfk(paramzzatg.zzbLJ);
          j = 1;
        }
      }
      i = j;
      if (paramzzatg.zzbLL != localzzatf1.zzMr())
      {
        localzzatf1.zzan(paramzzatg.zzbLL);
        i = 1;
      }
      if (paramzzatg.zzbLN != localzzatf1.zzMs())
      {
        localzzatf1.setMeasurementEnabled(paramzzatg.zzbLN);
        i = 1;
      }
      j = i;
      if (!TextUtils.isEmpty(paramzzatg.zzbLM))
      {
        j = i;
        if (!paramzzatg.zzbLM.equals(localzzatf1.zzMD()))
        {
          localzzatf1.zzfl(paramzzatg.zzbLM);
          j = 1;
        }
      }
      if (paramzzatg.zzbLR == localzzatf1.zzwJ()) {
        break label483;
      }
      localzzatf1.zzax(paramzzatg.zzbLR);
    }
    label483:
    for (i = k;; i = j)
    {
      if (i != 0) {
        zzMb().zza(localzzatf1);
      }
      return;
      localzzatf1 = localzzatf2;
      if (str.equals(localzzatf2.zzMk())) {
        break;
      }
      localzzatf2.zzfi(str);
      localzzatf2.zzfg(zzMh().zzOc());
      i = 1;
      localzzatf1 = localzzatf2;
      break;
    }
  }
  
  private boolean zzl(String paramString, long paramLong)
  {
    zzMb().beginTransaction();
    for (;;)
    {
      zza localzza;
      zzauz.zze localzze;
      int i;
      int k;
      int n;
      int i1;
      int m;
      Object localObject1;
      zzauz.zzc[] arrayOfzzc;
      long l;
      try
      {
        localzza = new zza(null);
        zzMb().zza(paramString, paramLong, this.zzbPy, localzza);
        if (localzza.isEmpty()) {
          break label1801;
        }
        bool1 = false;
        localzze = localzza.zzbPB;
        localzze.zzbRZ = new zzauz.zzb[localzza.zztA.size()];
        i = 0;
        k = 0;
        if (k < localzza.zztA.size())
        {
          if (zzMd().zzak(localzza.zzbPB.zzaR, ((zzauz.zzb)localzza.zztA.get(k)).name))
          {
            zzMg().zzNV().zze("Dropping blacklisted raw event. appId", zzaua.zzfH(localzza.zzbPB.zzaR), ((zzauz.zzb)localzza.zztA.get(k)).name);
            if (zzMc().zzgj(localzza.zzbPB.zzaR)) {
              break label1838;
            }
            if (!zzMc().zzgk(localzza.zzbPB.zzaR)) {
              break label1853;
            }
            break label1838;
            if ((j != 0) || ("_err".equals(((zzauz.zzb)localzza.zztA.get(k)).name))) {
              break label1835;
            }
            zzMc().zza(11, "_ev", ((zzauz.zzb)localzza.zztA.get(k)).name, 0);
            break label1844;
          }
          boolean bool3 = zzMd().zzal(localzza.zzbPB.zzaR, ((zzauz.zzb)localzza.zztA.get(k)).name);
          if ((!bool3) && (!zzMc().zzgl(((zzauz.zzb)localzza.zztA.get(k)).name))) {
            break label1832;
          }
          n = 0;
          j = 0;
          if (((zzauz.zzb)localzza.zztA.get(k)).zzbRR == null) {
            ((zzauz.zzb)localzza.zztA.get(k)).zzbRR = new zzauz.zzc[0];
          }
          paramString = ((zzauz.zzb)localzza.zztA.get(k)).zzbRR;
          i1 = paramString.length;
          m = 0;
          if (m < i1)
          {
            localObject1 = paramString[m];
            if ("_c".equals(((zzauz.zzc)localObject1).name))
            {
              ((zzauz.zzc)localObject1).zzbRV = Long.valueOf(1L);
              n = 1;
              break label1859;
            }
            if (!"_r".equals(((zzauz.zzc)localObject1).name)) {
              break label1829;
            }
            ((zzauz.zzc)localObject1).zzbRV = Long.valueOf(1L);
            j = 1;
            break label1859;
          }
          if ((n == 0) && (bool3))
          {
            zzMg().zzNZ().zzm("Marking event as conversion", ((zzauz.zzb)localzza.zztA.get(k)).name);
            paramString = (zzauz.zzc[])Arrays.copyOf(((zzauz.zzb)localzza.zztA.get(k)).zzbRR, ((zzauz.zzb)localzza.zztA.get(k)).zzbRR.length + 1);
            localObject1 = new zzauz.zzc();
            ((zzauz.zzc)localObject1).name = "_c";
            ((zzauz.zzc)localObject1).zzbRV = Long.valueOf(1L);
            paramString[(paramString.length - 1)] = localObject1;
            ((zzauz.zzb)localzza.zztA.get(k)).zzbRR = paramString;
          }
          if (j == 0)
          {
            zzMg().zzNZ().zzm("Marking event as real-time", ((zzauz.zzb)localzza.zztA.get(k)).name);
            paramString = (zzauz.zzc[])Arrays.copyOf(((zzauz.zzb)localzza.zztA.get(k)).zzbRR, ((zzauz.zzb)localzza.zztA.get(k)).zzbRR.length + 1);
            localObject1 = new zzauz.zzc();
            ((zzauz.zzc)localObject1).name = "_r";
            ((zzauz.zzc)localObject1).zzbRV = Long.valueOf(1L);
            paramString[(paramString.length - 1)] = localObject1;
            ((zzauz.zzb)localzza.zztA.get(k)).zzbRR = paramString;
          }
          bool2 = true;
          if (zzMb().zza(zzOz(), localzza.zzbPB.zzaR, false, false, false, false, true).zzbMn > zzMi().zzfo(localzza.zzbPB.zzaR))
          {
            paramString = (zzauz.zzb)localzza.zztA.get(k);
            j = 0;
            if (j >= paramString.zzbRR.length) {
              break label1868;
            }
            if (!"_r".equals(paramString.zzbRR[j].name)) {
              break label1884;
            }
            localObject1 = new zzauz.zzc[paramString.zzbRR.length - 1];
            if (j > 0) {
              System.arraycopy(paramString.zzbRR, 0, localObject1, 0, j);
            }
            if (j < localObject1.length) {
              System.arraycopy(paramString.zzbRR, j + 1, localObject1, j, localObject1.length - j);
            }
            paramString.zzbRR = ((zzauz.zzc[])localObject1);
            break label1868;
          }
          if ((!zzauw.zzfW(((zzauz.zzb)localzza.zztA.get(k)).name)) || (!bool3) || (zzMb().zza(zzOz(), localzza.zzbPB.zzaR, false, false, true, false, false).zzbMl <= zzMi().zzfn(localzza.zzbPB.zzaR))) {
            break label1939;
          }
          zzMg().zzNV().zzm("Too many conversions. Not logging as conversion. appId", zzaua.zzfH(localzza.zzbPB.zzaR));
          localObject2 = (zzauz.zzb)localzza.zztA.get(k);
          j = 0;
          paramString = null;
          arrayOfzzc = ((zzauz.zzb)localObject2).zzbRR;
          n = arrayOfzzc.length;
          m = 0;
          if (m < n)
          {
            localObject1 = arrayOfzzc[m];
            if ("_c".equals(((zzauz.zzc)localObject1).name))
            {
              paramString = (String)localObject1;
              break label1875;
            }
            if (!"_err".equals(((zzauz.zzc)localObject1).name)) {
              break label1826;
            }
            j = 1;
            break label1875;
          }
          if ((j != 0) && (paramString != null))
          {
            localObject1 = new zzauz.zzc[((zzauz.zzb)localObject2).zzbRR.length - 1];
            j = 0;
            arrayOfzzc = ((zzauz.zzb)localObject2).zzbRR;
            i1 = arrayOfzzc.length;
            m = 0;
            break label1893;
            ((zzauz.zzb)localObject2).zzbRR = ((zzauz.zzc[])localObject1);
            bool1 = bool2;
            localzze.zzbRZ[i] = ((zzauz.zzb)localzza.zztA.get(k));
            i += 1;
            break label1844;
          }
          if (paramString != null)
          {
            paramString.name = "_err";
            paramString.zzbRV = Long.valueOf(10L);
            bool1 = bool2;
            continue;
          }
          zzMg().zzNT().zzm("Did not find conversion parameter. appId", zzaua.zzfH(localzza.zzbPB.zzaR));
          break label1939;
        }
        if (i < localzza.zztA.size()) {
          localzze.zzbRZ = ((zzauz.zzb[])Arrays.copyOf(localzze.zzbRZ, i));
        }
        localzze.zzbSs = zza(localzza.zzbPB.zzaR, localzza.zzbPB.zzbSa, localzze.zzbRZ);
        localzze.zzbSc = Long.valueOf(Long.MAX_VALUE);
        localzze.zzbSd = Long.valueOf(Long.MIN_VALUE);
        i = 0;
        if (i < localzze.zzbRZ.length)
        {
          paramString = localzze.zzbRZ[i];
          if (paramString.zzbRS.longValue() < localzze.zzbSc.longValue()) {
            localzze.zzbSc = paramString.zzbRS;
          }
          if (paramString.zzbRS.longValue() <= localzze.zzbSd.longValue()) {
            break label1946;
          }
          localzze.zzbSd = paramString.zzbRS;
          break label1946;
        }
        localObject1 = localzza.zzbPB.zzaR;
        Object localObject2 = zzMb().zzfx((String)localObject1);
        if (localObject2 == null)
        {
          zzMg().zzNT().zzm("Bundling raw events w/o app info. appId", zzaua.zzfH(localzza.zzbPB.zzaR));
          if (localzze.zzbRZ.length > 0)
          {
            zzMi().zzNb();
            paramString = zzMd().zzfO(localzza.zzbPB.zzaR);
            if ((paramString != null) && (paramString.zzbRG != null)) {
              break label1789;
            }
            if (!TextUtils.isEmpty(localzza.zzbPB.zzbLI)) {
              break label1762;
            }
            localzze.zzbSx = Long.valueOf(-1L);
            zzMb().zza(localzze, bool1);
          }
          zzMb().zzW(localzza.zzbPC);
          zzMb().zzfE((String)localObject1);
          zzMb().setTransactionSuccessful();
          i = localzze.zzbRZ.length;
          if (i <= 0) {
            break label1955;
          }
          bool1 = true;
          return bool1;
        }
        if (localzze.zzbRZ.length <= 0) {
          continue;
        }
        paramLong = ((zzatf)localObject2).zzMn();
        if (paramLong != 0L)
        {
          paramString = Long.valueOf(paramLong);
          localzze.zzbSf = paramString;
          l = ((zzatf)localObject2).zzMm();
          if (l != 0L) {
            break label1817;
          }
          if (paramLong == 0L) {
            break label1757;
          }
          paramString = Long.valueOf(paramLong);
          localzze.zzbSe = paramString;
          ((zzatf)localObject2).zzMw();
          localzze.zzbSq = Integer.valueOf((int)((zzatf)localObject2).zzMt());
          ((zzatf)localObject2).zzaj(localzze.zzbSc.longValue());
          ((zzatf)localObject2).zzak(localzze.zzbSd.longValue());
          localzze.zzbLM = ((zzatf)localObject2).zzME();
          zzMb().zza((zzatf)localObject2);
          continue;
        }
        paramString = null;
      }
      finally
      {
        zzMb().endTransaction();
      }
      continue;
      label1757:
      paramString = null;
      continue;
      label1762:
      zzMg().zzNV().zzm("Did not find measurement config or missing version info. appId", zzaua.zzfH(localzza.zzbPB.zzaR));
      continue;
      label1789:
      localzze.zzbSx = paramString.zzbRG;
      continue;
      label1801:
      zzMb().setTransactionSuccessful();
      zzMb().endTransaction();
      return false;
      label1817:
      paramLong = l;
      continue;
      break label1930;
      label1826:
      break label1875;
      label1829:
      break label1859;
      label1832:
      continue;
      label1835:
      break label1844;
      label1838:
      int j = 1;
      continue;
      label1844:
      k += 1;
      continue;
      label1853:
      j = 0;
      continue;
      label1859:
      m += 1;
      continue;
      label1868:
      boolean bool2 = bool1;
      continue;
      label1875:
      m += 1;
      continue;
      label1884:
      j += 1;
      continue;
      for (;;)
      {
        label1893:
        if (m >= i1) {
          break label1937;
        }
        zzauz.zzc localzzc = arrayOfzzc[m];
        if (localzzc == paramString) {
          break;
        }
        n = j + 1;
        localObject1[j] = localzzc;
        j = n;
        label1930:
        m += 1;
      }
      label1937:
      continue;
      label1939:
      boolean bool1 = bool2;
      continue;
      label1946:
      i += 1;
      continue;
      label1955:
      bool1 = false;
    }
  }
  
  public Context getContext()
  {
    return this.mContext;
  }
  
  @WorkerThread
  public boolean isEnabled()
  {
    boolean bool = false;
    zzmW();
    zznA();
    if (zzMi().zzNc()) {
      return false;
    }
    Boolean localBoolean = zzMi().zzNd();
    if (localBoolean != null) {
      bool = localBoolean.booleanValue();
    }
    for (;;)
    {
      return zzMh().zzaU(bool);
      if (!zzMi().zzyD()) {
        bool = true;
      }
    }
  }
  
  @WorkerThread
  protected void start()
  {
    zzmW();
    zzMb().zzNA();
    if (zzMh().zzbNV.get() == 0L) {
      zzMh().zzbNV.set(zznq().currentTimeMillis());
    }
    if (!zzOo())
    {
      if (isEnabled())
      {
        if (!zzMc().zzby("android.permission.INTERNET")) {
          zzMg().zzNT().log("App is missing INTERNET permission");
        }
        if (!zzMc().zzby("android.permission.ACCESS_NETWORK_STATE")) {
          zzMg().zzNT().log("App is missing ACCESS_NETWORK_STATE permission");
        }
        zzMi().zzNb();
        if (!zzaca.zzbp(getContext()).zzBi())
        {
          if (!zzaue.zzi(getContext(), false)) {
            zzMg().zzNT().log("AppMeasurementReceiver not registered/enabled");
          }
          if (!zzaup.zzj(getContext(), false)) {
            zzMg().zzNT().log("AppMeasurementService not registered/enabled");
          }
        }
        zzMg().zzNT().log("Uploading is not possible. App measurement disabled");
      }
      zzOD();
      return;
    }
    zzMi().zzNb();
    String str;
    if (!TextUtils.isEmpty(zzLW().getGmpAppId()))
    {
      str = zzMh().zzOf();
      if (str != null) {
        break label276;
      }
      zzMh().zzfL(zzLW().getGmpAppId());
    }
    for (;;)
    {
      zzMi().zzNb();
      if (TextUtils.isEmpty(zzLW().getGmpAppId())) {
        break;
      }
      zzLV().zzOM();
      break;
      label276:
      if (!str.equals(zzLW().getGmpAppId()))
      {
        zzMg().zzNX().log("Rechecking which service to use due to a GMP App Id change");
        zzMh().zzOi();
        this.zzbPi.disconnect();
        this.zzbPi.zzoc();
        zzMh().zzfL(zzLW().getGmpAppId());
      }
    }
  }
  
  @WorkerThread
  boolean zzL(int paramInt1, int paramInt2)
  {
    zzmW();
    if (paramInt1 > paramInt2)
    {
      zzMg().zzNT().zze("Panic: can't downgrade version. Previous, current version", Integer.valueOf(paramInt1), Integer.valueOf(paramInt2));
      return false;
    }
    if (paramInt1 < paramInt2)
    {
      if (zza(paramInt2, zzOw())) {
        zzMg().zzNZ().zze("Storage version upgraded. Previous, current version", Integer.valueOf(paramInt1), Integer.valueOf(paramInt2));
      }
    }
    else {
      return true;
    }
    zzMg().zzNT().zze("Storage version upgrade failed. Previous, current version", Integer.valueOf(paramInt1), Integer.valueOf(paramInt2));
    return false;
  }
  
  void zzLQ()
  {
    zzMi().zzNb();
    throw new IllegalStateException("Unexpected call on client side");
  }
  
  void zzLR()
  {
    zzMi().zzNb();
  }
  
  public zzate zzLT()
  {
    zza(this.zzbPp);
    return this.zzbPp;
  }
  
  public zzati zzLU()
  {
    zza(this.zzbPo);
    return this.zzbPo;
  }
  
  public zzaum zzLV()
  {
    zza(this.zzbPk);
    return this.zzbPk;
  }
  
  public zzatx zzLW()
  {
    zza(this.zzbPl);
    return this.zzbPl;
  }
  
  public zzato zzLX()
  {
    zza(this.zzbPj);
    return this.zzbPj;
  }
  
  public zzauo zzLY()
  {
    zza(this.zzbPi);
    return this.zzbPi;
  }
  
  public zzaun zzLZ()
  {
    zza(this.zzbPh);
    return this.zzbPh;
  }
  
  public zzaty zzMa()
  {
    zza(this.zzbPf);
    return this.zzbPf;
  }
  
  public zzatm zzMb()
  {
    zza(this.zzbPe);
    return this.zzbPe;
  }
  
  public zzauw zzMc()
  {
    zza(this.zzbPd);
    return this.zzbPd;
  }
  
  public zzauf zzMd()
  {
    zza(this.zzbPa);
    return this.zzbPa;
  }
  
  public zzauq zzMe()
  {
    zza(this.zzbOZ);
    return this.zzbOZ;
  }
  
  public zzaug zzMf()
  {
    zza(this.zzbOY);
    return this.zzbOY;
  }
  
  public zzaua zzMg()
  {
    zza(this.zzbOX);
    return this.zzbOX;
  }
  
  public zzaud zzMh()
  {
    zza(this.zzbOW);
    return this.zzbOW;
  }
  
  public zzatl zzMi()
  {
    return this.zzbOV;
  }
  
  @WorkerThread
  protected boolean zzOA()
  {
    zzmW();
    return this.zzbPv != null;
  }
  
  @WorkerThread
  public void zzOB()
  {
    int j = 0;
    zzmW();
    zznA();
    zzMi().zzNb();
    Object localObject1 = zzMh().zzOh();
    if (localObject1 == null) {
      zzMg().zzNV().log("Upload data called on the client side before use of service was decided");
    }
    long l1;
    String str1;
    int i;
    Object localObject3;
    do
    {
      return;
      if (((Boolean)localObject1).booleanValue())
      {
        zzMg().zzNT().log("Upload called in the client side when service should be used");
        return;
      }
      if (this.zzbPz > 0L)
      {
        zzOD();
        return;
      }
      if (zzOA())
      {
        zzMg().zzNV().log("Uploading requested multiple times");
        return;
      }
      if (!zzOt().isNetworkConnected())
      {
        zzMg().zzNV().log("Network not connected, ignoring upload request");
        zzOD();
        return;
      }
      l1 = zznq().currentTimeMillis();
      zzaB(l1 - zzMi().zzNm());
      long l2 = zzMh().zzbNV.get();
      if (l2 != 0L) {
        zzMg().zzNY().zzm("Uploading events. Elapsed time since last upload attempt (ms)", Long.valueOf(Math.abs(l1 - l2)));
      }
      str1 = zzMb().zzNy();
      if (TextUtils.isEmpty(str1)) {
        break;
      }
      if (this.zzbPy == -1L) {
        this.zzbPy = zzMb().zzNG();
      }
      i = zzMi().zzft(str1);
      int k = zzMi().zzfu(str1);
      localObject3 = zzMb().zzf(str1, i, k);
    } while (((List)localObject3).isEmpty());
    localObject1 = ((List)localObject3).iterator();
    Object localObject4;
    do
    {
      if (!((Iterator)localObject1).hasNext()) {
        break;
      }
      localObject4 = (zzauz.zze)((Pair)((Iterator)localObject1).next()).first;
    } while (TextUtils.isEmpty(((zzauz.zze)localObject4).zzbSm));
    Object localObject2;
    for (localObject1 = ((zzauz.zze)localObject4).zzbSm;; localObject2 = null)
    {
      if (localObject1 != null)
      {
        i = 0;
        if (i < ((List)localObject3).size())
        {
          localObject4 = (zzauz.zze)((Pair)((List)localObject3).get(i)).first;
          if (TextUtils.isEmpty(((zzauz.zze)localObject4).zzbSm)) {}
          while (((zzauz.zze)localObject4).zzbSm.equals(localObject1))
          {
            i += 1;
            break;
          }
        }
      }
      for (localObject1 = ((List)localObject3).subList(0, i);; localObject2 = localObject3)
      {
        localObject4 = new zzauz.zzd();
        ((zzauz.zzd)localObject4).zzbRW = new zzauz.zze[((List)localObject1).size()];
        localObject3 = new ArrayList(((List)localObject1).size());
        i = j;
        while (i < ((zzauz.zzd)localObject4).zzbRW.length)
        {
          ((zzauz.zzd)localObject4).zzbRW[i] = ((zzauz.zze)((Pair)((List)localObject1).get(i)).first);
          ((List)localObject3).add((Long)((Pair)((List)localObject1).get(i)).second);
          localObject4.zzbRW[i].zzbSl = Long.valueOf(zzMi().zzMq());
          localObject4.zzbRW[i].zzbSb = Long.valueOf(l1);
          localObject4.zzbRW[i].zzbSr = Boolean.valueOf(zzMi().zzNb());
          i += 1;
        }
        if (zzMg().zzao(2)) {}
        for (localObject1 = zzauw.zzb((zzauz.zzd)localObject4);; localObject2 = null)
        {
          byte[] arrayOfByte = zzMc().zza((zzauz.zzd)localObject4);
          String str2 = zzMi().zzNl();
          try
          {
            URL localURL = new URL(str2);
            zzX((List)localObject3);
            zzMh().zzbNW.set(l1);
            localObject3 = "?";
            if (((zzauz.zzd)localObject4).zzbRW.length > 0) {
              localObject3 = localObject4.zzbRW[0].zzaR;
            }
            zzMg().zzNZ().zzd("Uploading data. app, uncompressed size, data", localObject3, Integer.valueOf(arrayOfByte.length), localObject1);
            zzOt().zza(str1, localURL, arrayOfByte, null, new zzaub.zza()
            {
              public void zza(String paramAnonymousString, int paramAnonymousInt, Throwable paramAnonymousThrowable, byte[] paramAnonymousArrayOfByte, Map<String, List<String>> paramAnonymousMap)
              {
                zzauh.this.zza(paramAnonymousInt, paramAnonymousThrowable, paramAnonymousArrayOfByte);
              }
            });
            return;
          }
          catch (MalformedURLException localMalformedURLException)
          {
            zzMg().zzNT().zze("Failed to parse upload URL. Not uploading. appId", zzaua.zzfH(str1), str2);
            return;
          }
          this.zzbPy = -1L;
          localObject2 = zzMb().zzaz(l1 - zzMi().zzNm());
          if (TextUtils.isEmpty((CharSequence)localObject2)) {
            break;
          }
          localObject2 = zzMb().zzfx((String)localObject2);
          if (localObject2 == null) {
            break;
          }
          zzb((zzatf)localObject2);
          return;
        }
      }
    }
  }
  
  void zzOF()
  {
    this.zzbPx += 1;
  }
  
  @WorkerThread
  void zzOG()
  {
    zzmW();
    zznA();
    if (!this.zzbPq)
    {
      zzMg().zzNX().log("This instance being marked as an uploader");
      zzOx();
    }
    this.zzbPq = true;
  }
  
  @WorkerThread
  boolean zzOH()
  {
    zzmW();
    zznA();
    return this.zzbPq;
  }
  
  @WorkerThread
  protected boolean zzOo()
  {
    boolean bool2 = false;
    zznA();
    zzmW();
    if ((this.zzbPr == null) || (this.zzbPs == 0L) || ((this.zzbPr != null) && (!this.zzbPr.booleanValue()) && (Math.abs(zznq().elapsedRealtime() - this.zzbPs) > 1000L)))
    {
      this.zzbPs = zznq().elapsedRealtime();
      zzMi().zzNb();
      boolean bool1 = bool2;
      if (zzMc().zzby("android.permission.INTERNET"))
      {
        bool1 = bool2;
        if (zzMc().zzby("android.permission.ACCESS_NETWORK_STATE")) {
          if (!zzaca.zzbp(getContext()).zzBi())
          {
            bool1 = bool2;
            if (zzaue.zzi(getContext(), false))
            {
              bool1 = bool2;
              if (!zzaup.zzj(getContext(), false)) {}
            }
          }
          else
          {
            bool1 = true;
          }
        }
      }
      this.zzbPr = Boolean.valueOf(bool1);
      if (this.zzbPr.booleanValue()) {
        this.zzbPr = Boolean.valueOf(zzMc().zzgd(zzLW().getGmpAppId()));
      }
    }
    return this.zzbPr.booleanValue();
  }
  
  public zzaua zzOp()
  {
    if ((this.zzbOX != null) && (this.zzbOX.isInitialized())) {
      return this.zzbOX;
    }
    return null;
  }
  
  zzaug zzOq()
  {
    return this.zzbOY;
  }
  
  public AppMeasurement zzOr()
  {
    return this.zzbPb;
  }
  
  public FirebaseAnalytics zzOs()
  {
    return this.zzbPc;
  }
  
  public zzaub zzOt()
  {
    zza(this.zzbPg);
    return this.zzbPg;
  }
  
  public zzauc zzOu()
  {
    if (this.zzbPm == null) {
      throw new IllegalStateException("Network broadcast receiver not created");
    }
    return this.zzbPm;
  }
  
  public zzaus zzOv()
  {
    zza(this.zzbPn);
    return this.zzbPn;
  }
  
  FileChannel zzOw()
  {
    return this.zzbPu;
  }
  
  @WorkerThread
  void zzOx()
  {
    zzmW();
    zznA();
    if ((zzOH()) && (zzOy())) {
      zzL(zza(zzOw()), zzLW().zzNS());
    }
  }
  
  @WorkerThread
  boolean zzOy()
  {
    zzmW();
    Object localObject = this.zzbPe.zznV();
    localObject = new File(getContext().getFilesDir(), (String)localObject);
    try
    {
      this.zzbPu = new RandomAccessFile((File)localObject, "rw").getChannel();
      this.zzbPt = this.zzbPu.tryLock();
      if (this.zzbPt != null)
      {
        zzMg().zzNZ().log("Storage concurrent access okay");
        return true;
      }
      zzMg().zzNT().log("Storage concurrent data access panic");
    }
    catch (FileNotFoundException localFileNotFoundException)
    {
      for (;;)
      {
        zzMg().zzNT().zzm("Failed to acquire storage lock", localFileNotFoundException);
      }
    }
    catch (IOException localIOException)
    {
      for (;;)
      {
        zzMg().zzNT().zzm("Failed to access storage lock file", localIOException);
      }
    }
    return false;
  }
  
  long zzOz()
  {
    return (zznq().currentTimeMillis() + zzMh().zzOd()) / 1000L / 60L / 60L / 24L;
  }
  
  public void zzR(boolean paramBoolean)
  {
    zzOD();
  }
  
  protected void zzX(List<Long> paramList)
  {
    if (!paramList.isEmpty()) {}
    for (boolean bool = true;; bool = false)
    {
      zzac.zzaw(bool);
      if (this.zzbPv == null) {
        break;
      }
      zzMg().zzNT().log("Set uploading progress before finishing the previous upload");
      return;
    }
    this.zzbPv = new ArrayList(paramList);
  }
  
  @WorkerThread
  int zza(FileChannel paramFileChannel)
  {
    zzmW();
    if ((paramFileChannel == null) || (!paramFileChannel.isOpen())) {
      zzMg().zzNT().log("Bad chanel to read from");
    }
    ByteBuffer localByteBuffer;
    for (;;)
    {
      return 0;
      localByteBuffer = ByteBuffer.allocate(4);
      try
      {
        paramFileChannel.position(0L);
        i = paramFileChannel.read(localByteBuffer);
        if (i != 4)
        {
          if (i == -1) {
            continue;
          }
          zzMg().zzNV().zzm("Unexpected data length. Bytes read", Integer.valueOf(i));
          return 0;
        }
      }
      catch (IOException paramFileChannel)
      {
        zzMg().zzNT().zzm("Failed to read from channel", paramFileChannel);
        return 0;
      }
    }
    localByteBuffer.flip();
    int i = localByteBuffer.getInt();
    return i;
  }
  
  /* Error */
  @WorkerThread
  protected void zza(int paramInt, Throwable paramThrowable, byte[] paramArrayOfByte)
  {
    // Byte code:
    //   0: iconst_0
    //   1: istore 4
    //   3: aload_0
    //   4: invokevirtual 415	com/google/android/gms/internal/zzauh:zzmW	()V
    //   7: aload_0
    //   8: invokevirtual 418	com/google/android/gms/internal/zzauh:zznA	()V
    //   11: aload_3
    //   12: astore 5
    //   14: aload_3
    //   15: ifnonnull +8 -> 23
    //   18: iconst_0
    //   19: newarray <illegal type>
    //   21: astore 5
    //   23: aload_0
    //   24: getfield 1133	com/google/android/gms/internal/zzauh:zzbPv	Ljava/util/List;
    //   27: astore_3
    //   28: aload_0
    //   29: aconst_null
    //   30: putfield 1133	com/google/android/gms/internal/zzauh:zzbPv	Ljava/util/List;
    //   33: iload_1
    //   34: sipush 200
    //   37: if_icmpeq +10 -> 47
    //   40: iload_1
    //   41: sipush 204
    //   44: if_icmpne +225 -> 269
    //   47: aload_2
    //   48: ifnonnull +221 -> 269
    //   51: aload_0
    //   52: invokevirtual 501	com/google/android/gms/internal/zzauh:zzMh	()Lcom/google/android/gms/internal/zzaud;
    //   55: getfield 529	com/google/android/gms/internal/zzaud:zzbNV	Lcom/google/android/gms/internal/zzaud$zzb;
    //   58: aload_0
    //   59: invokevirtual 447	com/google/android/gms/internal/zzauh:zznq	()Lcom/google/android/gms/common/util/Clock;
    //   62: invokeinterface 523 1 0
    //   67: invokevirtual 533	com/google/android/gms/internal/zzaud$zzb:set	(J)V
    //   70: aload_0
    //   71: invokevirtual 501	com/google/android/gms/internal/zzauh:zzMh	()Lcom/google/android/gms/internal/zzaud;
    //   74: getfield 562	com/google/android/gms/internal/zzaud:zzbNW	Lcom/google/android/gms/internal/zzaud$zzb;
    //   77: lconst_0
    //   78: invokevirtual 533	com/google/android/gms/internal/zzaud$zzb:set	(J)V
    //   81: aload_0
    //   82: invokespecial 1068	com/google/android/gms/internal/zzauh:zzOD	()V
    //   85: aload_0
    //   86: invokevirtual 137	com/google/android/gms/internal/zzauh:zzMg	()Lcom/google/android/gms/internal/zzaua;
    //   89: invokevirtual 461	com/google/android/gms/internal/zzaua:zzNZ	()Lcom/google/android/gms/internal/zzaua$zza;
    //   92: ldc_w 1386
    //   95: iload_1
    //   96: invokestatic 357	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   99: aload 5
    //   101: arraylength
    //   102: invokestatic 357	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   105: invokevirtual 360	com/google/android/gms/internal/zzaua$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   108: aload_0
    //   109: invokevirtual 422	com/google/android/gms/internal/zzauh:zzMb	()Lcom/google/android/gms/internal/zzatm;
    //   112: invokevirtual 798	com/google/android/gms/internal/zzatm:beginTransaction	()V
    //   115: aload_3
    //   116: invokeinterface 1172 1 0
    //   121: astore_2
    //   122: aload_2
    //   123: invokeinterface 615 1 0
    //   128: ifeq +86 -> 214
    //   131: aload_2
    //   132: invokeinterface 621 1 0
    //   137: checkcast 155	java/lang/Long
    //   140: astore_3
    //   141: aload_0
    //   142: invokevirtual 422	com/google/android/gms/internal/zzauh:zzMb	()Lcom/google/android/gms/internal/zzatm;
    //   145: aload_3
    //   146: invokevirtual 933	java/lang/Long:longValue	()J
    //   149: invokevirtual 1389	com/google/android/gms/internal/zzatm:zzay	(J)V
    //   152: goto -30 -> 122
    //   155: astore_2
    //   156: aload_0
    //   157: invokevirtual 422	com/google/android/gms/internal/zzauh:zzMb	()Lcom/google/android/gms/internal/zzatm;
    //   160: invokevirtual 967	com/google/android/gms/internal/zzatm:endTransaction	()V
    //   163: aload_2
    //   164: athrow
    //   165: astore_2
    //   166: aload_0
    //   167: invokevirtual 137	com/google/android/gms/internal/zzauh:zzMg	()Lcom/google/android/gms/internal/zzaua;
    //   170: invokevirtual 350	com/google/android/gms/internal/zzaua:zzNT	()Lcom/google/android/gms/internal/zzaua$zza;
    //   173: ldc_w 1391
    //   176: aload_2
    //   177: invokevirtual 165	com/google/android/gms/internal/zzaua$zza:zzm	(Ljava/lang/String;Ljava/lang/Object;)V
    //   180: aload_0
    //   181: aload_0
    //   182: invokevirtual 447	com/google/android/gms/internal/zzauh:zznq	()Lcom/google/android/gms/common/util/Clock;
    //   185: invokeinterface 452 1 0
    //   190: putfield 441	com/google/android/gms/internal/zzauh:zzbPz	J
    //   193: aload_0
    //   194: invokevirtual 137	com/google/android/gms/internal/zzauh:zzMg	()Lcom/google/android/gms/internal/zzaua;
    //   197: invokevirtual 461	com/google/android/gms/internal/zzaua:zzNZ	()Lcom/google/android/gms/internal/zzaua$zza;
    //   200: ldc_w 1393
    //   203: aload_0
    //   204: getfield 441	com/google/android/gms/internal/zzauh:zzbPz	J
    //   207: invokestatic 159	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   210: invokevirtual 165	com/google/android/gms/internal/zzaua$zza:zzm	(Ljava/lang/String;Ljava/lang/Object;)V
    //   213: return
    //   214: aload_0
    //   215: invokevirtual 422	com/google/android/gms/internal/zzauh:zzMb	()Lcom/google/android/gms/internal/zzatm;
    //   218: invokevirtual 964	com/google/android/gms/internal/zzatm:setTransactionSuccessful	()V
    //   221: aload_0
    //   222: invokevirtual 422	com/google/android/gms/internal/zzauh:zzMb	()Lcom/google/android/gms/internal/zzatm;
    //   225: invokevirtual 967	com/google/android/gms/internal/zzatm:endTransaction	()V
    //   228: aload_0
    //   229: invokevirtual 491	com/google/android/gms/internal/zzauh:zzOt	()Lcom/google/android/gms/internal/zzaub;
    //   232: invokevirtual 494	com/google/android/gms/internal/zzaub:isNetworkConnected	()Z
    //   235: ifeq +20 -> 255
    //   238: aload_0
    //   239: invokespecial 484	com/google/android/gms/internal/zzauh:zzOC	()Z
    //   242: ifeq +13 -> 255
    //   245: aload_0
    //   246: invokevirtual 1395	com/google/android/gms/internal/zzauh:zzOB	()V
    //   249: aload_0
    //   250: lconst_0
    //   251: putfield 441	com/google/android/gms/internal/zzauh:zzbPz	J
    //   254: return
    //   255: aload_0
    //   256: ldc2_w 99
    //   259: putfield 102	com/google/android/gms/internal/zzauh:zzbPy	J
    //   262: aload_0
    //   263: invokespecial 1068	com/google/android/gms/internal/zzauh:zzOD	()V
    //   266: goto -17 -> 249
    //   269: aload_0
    //   270: invokevirtual 137	com/google/android/gms/internal/zzauh:zzMg	()Lcom/google/android/gms/internal/zzaua;
    //   273: invokevirtual 461	com/google/android/gms/internal/zzaua:zzNZ	()Lcom/google/android/gms/internal/zzaua$zza;
    //   276: ldc_w 1397
    //   279: iload_1
    //   280: invokestatic 357	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   283: aload_2
    //   284: invokevirtual 360	com/google/android/gms/internal/zzaua$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   287: aload_0
    //   288: invokevirtual 501	com/google/android/gms/internal/zzauh:zzMh	()Lcom/google/android/gms/internal/zzaud;
    //   291: getfield 562	com/google/android/gms/internal/zzaud:zzbNW	Lcom/google/android/gms/internal/zzaud$zzb;
    //   294: aload_0
    //   295: invokevirtual 447	com/google/android/gms/internal/zzauh:zznq	()Lcom/google/android/gms/common/util/Clock;
    //   298: invokeinterface 523 1 0
    //   303: invokevirtual 533	com/google/android/gms/internal/zzaud$zzb:set	(J)V
    //   306: iload_1
    //   307: sipush 503
    //   310: if_icmpeq +10 -> 320
    //   313: iload_1
    //   314: sipush 429
    //   317: if_icmpne +6 -> 323
    //   320: iconst_1
    //   321: istore 4
    //   323: iload 4
    //   325: ifeq +22 -> 347
    //   328: aload_0
    //   329: invokevirtual 501	com/google/android/gms/internal/zzauh:zzMh	()Lcom/google/android/gms/internal/zzaud;
    //   332: getfield 505	com/google/android/gms/internal/zzaud:zzbNX	Lcom/google/android/gms/internal/zzaud$zzb;
    //   335: aload_0
    //   336: invokevirtual 447	com/google/android/gms/internal/zzauh:zznq	()Lcom/google/android/gms/common/util/Clock;
    //   339: invokeinterface 523 1 0
    //   344: invokevirtual 533	com/google/android/gms/internal/zzaud$zzb:set	(J)V
    //   347: aload_0
    //   348: invokespecial 1068	com/google/android/gms/internal/zzauh:zzOD	()V
    //   351: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	352	0	this	zzauh
    //   0	352	1	paramInt	int
    //   0	352	2	paramThrowable	Throwable
    //   0	352	3	paramArrayOfByte	byte[]
    //   1	323	4	i	int
    //   12	88	5	arrayOfByte	byte[]
    // Exception table:
    //   from	to	target	type
    //   115	122	155	finally
    //   122	152	155	finally
    //   214	221	155	finally
    //   51	115	165	android/database/sqlite/SQLiteException
    //   156	165	165	android/database/sqlite/SQLiteException
    //   221	249	165	android/database/sqlite/SQLiteException
    //   249	254	165	android/database/sqlite/SQLiteException
    //   255	266	165	android/database/sqlite/SQLiteException
  }
  
  @WorkerThread
  void zza(zzatg paramzzatg, long paramLong)
  {
    Object localObject2 = zzMb().zzfx(paramzzatg.packageName);
    Object localObject1 = localObject2;
    if (localObject2 != null)
    {
      localObject1 = localObject2;
      if (((zzatf)localObject2).getGmpAppId() != null)
      {
        localObject1 = localObject2;
        if (!((zzatf)localObject2).getGmpAppId().equals(paramzzatg.zzbLI))
        {
          zzMg().zzNV().zzm("New GMP App Id passed in. Removing cached database data. appId", zzaua.zzfH(((zzatf)localObject2).getAppId()));
          zzMb().zzfC(((zzatf)localObject2).getAppId());
          localObject1 = null;
        }
      }
    }
    if ((localObject1 != null) && (((zzatf)localObject1).getAppVersion() != null) && (!((zzatf)localObject1).getAppVersion().equals(paramzzatg.zzbAI)))
    {
      localObject2 = new Bundle();
      ((Bundle)localObject2).putString("_pv", ((zzatf)localObject1).getAppVersion());
      zzb(new zzatt("_au", new zzatr((Bundle)localObject2), "auto", paramLong), paramzzatg);
    }
  }
  
  void zza(zzatp paramzzatp, zzatg paramzzatg)
  {
    zzmW();
    zznA();
    zzac.zzC(paramzzatp);
    zzac.zzC(paramzzatg);
    zzac.zzdc(paramzzatp.mAppId);
    zzac.zzaw(paramzzatp.mAppId.equals(paramzzatg.packageName));
    zzauz.zze localzze = new zzauz.zze();
    localzze.zzbRY = Integer.valueOf(1);
    localzze.zzbSg = "android";
    localzze.zzaR = paramzzatg.packageName;
    localzze.zzbLJ = paramzzatg.zzbLJ;
    localzze.zzbAI = paramzzatg.zzbAI;
    localzze.zzbSt = Integer.valueOf((int)paramzzatg.zzbLP);
    localzze.zzbSk = Long.valueOf(paramzzatg.zzbLK);
    localzze.zzbLI = paramzzatg.zzbLI;
    Object localObject1;
    if (paramzzatg.zzbLL == 0L)
    {
      localObject1 = null;
      localzze.zzbSp = ((Long)localObject1);
      localObject1 = zzMh().zzfJ(paramzzatg.packageName);
      if (TextUtils.isEmpty((CharSequence)((Pair)localObject1).first)) {
        break label615;
      }
      localzze.zzbSm = ((String)((Pair)localObject1).first);
      localzze.zzbSn = ((Boolean)((Pair)localObject1).second);
    }
    label615:
    while (zzLX().zzbQ(this.mContext))
    {
      localzze.zzbSh = zzLX().zzkS();
      localzze.zzba = zzLX().zzNN();
      localzze.zzbSj = Integer.valueOf((int)zzLX().zzNO());
      localzze.zzbSi = zzLX().zzNP();
      localzze.zzbSl = null;
      localzze.zzbSb = null;
      localzze.zzbSc = null;
      localzze.zzbSd = null;
      localzze.zzbSy = Long.valueOf(paramzzatg.zzbLR);
      localObject2 = zzMb().zzfx(paramzzatg.packageName);
      localObject1 = localObject2;
      if (localObject2 == null)
      {
        localObject1 = new zzatf(this, paramzzatg.packageName);
        ((zzatf)localObject1).zzfg(zzMh().zzOc());
        ((zzatf)localObject1).zzfj(paramzzatg.zzbLQ);
        ((zzatf)localObject1).zzfh(paramzzatg.zzbLI);
        ((zzatf)localObject1).zzfi(zzMh().zzfK(paramzzatg.packageName));
        ((zzatf)localObject1).zzao(0L);
        ((zzatf)localObject1).zzaj(0L);
        ((zzatf)localObject1).zzak(0L);
        ((zzatf)localObject1).setAppVersion(paramzzatg.zzbAI);
        ((zzatf)localObject1).zzal(paramzzatg.zzbLP);
        ((zzatf)localObject1).zzfk(paramzzatg.zzbLJ);
        ((zzatf)localObject1).zzam(paramzzatg.zzbLK);
        ((zzatf)localObject1).zzan(paramzzatg.zzbLL);
        ((zzatf)localObject1).setMeasurementEnabled(paramzzatg.zzbLN);
        ((zzatf)localObject1).zzax(paramzzatg.zzbLR);
        zzMb().zza((zzatf)localObject1);
      }
      localzze.zzbSo = ((zzatf)localObject1).getAppInstanceId();
      localzze.zzbLQ = ((zzatf)localObject1).zzMl();
      paramzzatg = zzMb().zzfw(paramzzatg.packageName);
      localzze.zzbSa = new zzauz.zzg[paramzzatg.size()];
      int i = 0;
      while (i < paramzzatg.size())
      {
        localObject1 = new zzauz.zzg();
        localzze.zzbSa[i] = localObject1;
        ((zzauz.zzg)localObject1).name = ((zzauv)paramzzatg.get(i)).mName;
        ((zzauz.zzg)localObject1).zzbSC = Long.valueOf(((zzauv)paramzzatg.get(i)).zzbRa);
        zzMc().zza((zzauz.zzg)localObject1, ((zzauv)paramzzatg.get(i)).mValue);
        i += 1;
      }
      localObject1 = Long.valueOf(paramzzatg.zzbLL);
      break;
    }
    Object localObject2 = Settings.Secure.getString(this.mContext.getContentResolver(), "android_id");
    if (localObject2 == null)
    {
      zzMg().zzNV().zzm("null secure ID. appId", zzaua.zzfH(localzze.zzaR));
      localObject1 = "null";
    }
    for (;;)
    {
      localzze.zzbSw = ((String)localObject1);
      break;
      localObject1 = localObject2;
      if (((String)localObject2).isEmpty())
      {
        zzMg().zzNV().zzm("empty secure ID. appId", zzaua.zzfH(localzze.zzaR));
        localObject1 = localObject2;
      }
    }
    try
    {
      long l = zzMb().zza(localzze);
      if (zzMb().zza(paramzzatp, l, zza(paramzzatp))) {
        this.zzbPz = 0L;
      }
      return;
    }
    catch (IOException paramzzatp)
    {
      zzMg().zzNT().zze("Data loss. Failed to insert raw event metadata. appId", zzaua.zzfH(localzze.zzaR), paramzzatp);
    }
  }
  
  @WorkerThread
  boolean zza(int paramInt, FileChannel paramFileChannel)
  {
    boolean bool = true;
    zzmW();
    if ((paramFileChannel == null) || (!paramFileChannel.isOpen()))
    {
      zzMg().zzNT().log("Bad chanel to read from");
      bool = false;
    }
    for (;;)
    {
      return bool;
      ByteBuffer localByteBuffer = ByteBuffer.allocate(4);
      localByteBuffer.putInt(paramInt);
      localByteBuffer.flip();
      try
      {
        paramFileChannel.truncate(0L);
        paramFileChannel.write(localByteBuffer);
        paramFileChannel.force(true);
        if (paramFileChannel.size() != 4L)
        {
          zzMg().zzNT().zzm("Error writing to channel. Bytes written", Long.valueOf(paramFileChannel.size()));
          return true;
        }
      }
      catch (IOException paramFileChannel)
      {
        zzMg().zzNT().zzm("Failed to write to channel", paramFileChannel);
      }
    }
    return false;
  }
  
  @WorkerThread
  public byte[] zza(@NonNull zzatt paramzzatt, @Size(min=1L) String paramString)
  {
    zznA();
    zzmW();
    zzLQ();
    zzac.zzC(paramzzatt);
    zzac.zzdc(paramString);
    zzauz.zzd localzzd = new zzauz.zzd();
    zzMb().beginTransaction();
    Object localObject1;
    zzauz.zze localzze;
    Object localObject2;
    try
    {
      localObject1 = zzMb().zzfx(paramString);
      if (localObject1 == null)
      {
        zzMg().zzNY().zzm("Log and bundle not available. package_name", paramString);
        return new byte[0];
      }
      if (!((zzatf)localObject1).zzMs())
      {
        zzMg().zzNY().zzm("Log and bundle disabled. package_name", paramString);
        return new byte[0];
      }
      localzze = new zzauz.zze();
      localzzd.zzbRW = new zzauz.zze[] { localzze };
      localzze.zzbRY = Integer.valueOf(1);
      localzze.zzbSg = "android";
      localzze.zzaR = ((zzatf)localObject1).getAppId();
      localzze.zzbLJ = ((zzatf)localObject1).zzMp();
      localzze.zzbAI = ((zzatf)localObject1).getAppVersion();
      localzze.zzbSt = Integer.valueOf((int)((zzatf)localObject1).zzMo());
      localzze.zzbSk = Long.valueOf(((zzatf)localObject1).zzMq());
      localzze.zzbLI = ((zzatf)localObject1).getGmpAppId();
      localzze.zzbSp = Long.valueOf(((zzatf)localObject1).zzMr());
      localObject2 = zzMh().zzfJ(((zzatf)localObject1).getAppId());
      if (!TextUtils.isEmpty((CharSequence)((Pair)localObject2).first))
      {
        localzze.zzbSm = ((String)((Pair)localObject2).first);
        localzze.zzbSn = ((Boolean)((Pair)localObject2).second);
      }
      localzze.zzbSh = zzLX().zzkS();
      localzze.zzba = zzLX().zzNN();
      localzze.zzbSj = Integer.valueOf((int)zzLX().zzNO());
      localzze.zzbSi = zzLX().zzNP();
      localzze.zzbSo = ((zzatf)localObject1).getAppInstanceId();
      localzze.zzbLQ = ((zzatf)localObject1).zzMl();
      localObject2 = zzMb().zzfw(((zzatf)localObject1).getAppId());
      localzze.zzbSa = new zzauz.zzg[((List)localObject2).size()];
      int i = 0;
      while (i < ((List)localObject2).size())
      {
        localObject3 = new zzauz.zzg();
        localzze.zzbSa[i] = localObject3;
        ((zzauz.zzg)localObject3).name = ((zzauv)((List)localObject2).get(i)).mName;
        ((zzauz.zzg)localObject3).zzbSC = Long.valueOf(((zzauv)((List)localObject2).get(i)).zzbRa);
        zzMc().zza((zzauz.zzg)localObject3, ((zzauv)((List)localObject2).get(i)).mValue);
        i += 1;
      }
      localObject2 = paramzzatt.zzbMC.zzNR();
      if ("_iap".equals(paramzzatt.name))
      {
        ((Bundle)localObject2).putLong("_c", 1L);
        zzMg().zzNY().log("Marking in-app purchase as real-time");
        ((Bundle)localObject2).putLong("_r", 1L);
      }
      ((Bundle)localObject2).putString("_o", paramzzatt.zzaIu);
      if (zzMc().zzgh(localzze.zzaR))
      {
        zzMc().zza((Bundle)localObject2, "_dbg", Long.valueOf(1L));
        zzMc().zza((Bundle)localObject2, "_r", Long.valueOf(1L));
      }
      Object localObject3 = zzMb().zzaa(paramString, paramzzatt.name);
      if (localObject3 == null)
      {
        localObject3 = new zzatq(paramString, paramzzatt.name, 1L, 0L, paramzzatt.zzbMD);
        zzMb().zza((zzatq)localObject3);
        l1 = 0L;
      }
      for (;;)
      {
        paramzzatt = new zzatp(this, paramzzatt.zzaIu, paramString, paramzzatt.name, paramzzatt.zzbMD, l1, (Bundle)localObject2);
        localObject2 = new zzauz.zzb();
        localzze.zzbRZ = new zzauz.zzb[] { localObject2 };
        ((zzauz.zzb)localObject2).zzbRS = Long.valueOf(paramzzatt.zzaHE);
        ((zzauz.zzb)localObject2).name = paramzzatt.mName;
        ((zzauz.zzb)localObject2).zzbRT = Long.valueOf(paramzzatt.zzbMu);
        ((zzauz.zzb)localObject2).zzbRR = new zzauz.zzc[paramzzatt.zzbMv.size()];
        localObject3 = paramzzatt.zzbMv.iterator();
        i = 0;
        while (((Iterator)localObject3).hasNext())
        {
          Object localObject4 = (String)((Iterator)localObject3).next();
          zzauz.zzc localzzc = new zzauz.zzc();
          ((zzauz.zzb)localObject2).zzbRR[i] = localzzc;
          localzzc.name = ((String)localObject4);
          localObject4 = paramzzatt.zzbMv.get((String)localObject4);
          zzMc().zza(localzzc, localObject4);
          i += 1;
        }
        l1 = ((zzatq)localObject3).zzbMy;
        localObject3 = ((zzatq)localObject3).zzaA(paramzzatt.zzbMD).zzNQ();
        zzMb().zza((zzatq)localObject3);
      }
      localzze.zzbSs = zza(((zzatf)localObject1).getAppId(), localzze.zzbSa, localzze.zzbRZ);
    }
    finally
    {
      zzMb().endTransaction();
    }
    localzze.zzbSc = ((zzauz.zzb)localObject2).zzbRS;
    localzze.zzbSd = ((zzauz.zzb)localObject2).zzbRS;
    long l1 = ((zzatf)localObject1).zzMn();
    long l2;
    if (l1 != 0L)
    {
      paramzzatt = Long.valueOf(l1);
      localzze.zzbSf = paramzzatt;
      l2 = ((zzatf)localObject1).zzMm();
      if (l2 != 0L) {
        break label1157;
      }
    }
    for (;;)
    {
      if (l1 != 0L) {}
      for (paramzzatt = Long.valueOf(l1);; paramzzatt = null)
      {
        localzze.zzbSe = paramzzatt;
        ((zzatf)localObject1).zzMw();
        localzze.zzbSq = Integer.valueOf((int)((zzatf)localObject1).zzMt());
        localzze.zzbSl = Long.valueOf(zzMi().zzMq());
        localzze.zzbSb = Long.valueOf(zznq().currentTimeMillis());
        localzze.zzbSr = Boolean.TRUE;
        ((zzatf)localObject1).zzaj(localzze.zzbSc.longValue());
        ((zzatf)localObject1).zzak(localzze.zzbSd.longValue());
        zzMb().zza((zzatf)localObject1);
        zzMb().setTransactionSuccessful();
        zzMb().endTransaction();
        try
        {
          paramzzatt = new byte[localzzd.zzann()];
          localObject1 = zzcfy.zzas(paramzzatt);
          localzzd.writeTo((zzcfy)localObject1);
          ((zzcfy)localObject1).zzana();
          paramzzatt = zzMc().zzk(paramzzatt);
          return paramzzatt;
        }
        catch (IOException paramzzatt)
        {
          zzMg().zzNT().zze("Data loss. Failed to bundle and serialize. appId", zzaua.zzfH(paramString), paramzzatt);
          return null;
        }
        paramzzatt = null;
        break;
      }
      label1157:
      l1 = l2;
    }
  }
  
  boolean zzaB(long paramLong)
  {
    return zzl(null, paramLong);
  }
  
  void zzb(zzatf paramzzatf)
  {
    Object localObject2 = null;
    if (TextUtils.isEmpty(paramzzatf.getGmpAppId()))
    {
      zzb(paramzzatf.getAppId(), 204, null, null, null);
      return;
    }
    String str1 = zzMi().zzZ(paramzzatf.getGmpAppId(), paramzzatf.getAppInstanceId());
    try
    {
      URL localURL = new URL(str1);
      zzMg().zzNZ().zzm("Fetching remote configuration", paramzzatf.getAppId());
      zzauy.zzb localzzb = zzMd().zzfO(paramzzatf.getAppId());
      String str2 = zzMd().zzfP(paramzzatf.getAppId());
      Object localObject1 = localObject2;
      if (localzzb != null)
      {
        localObject1 = localObject2;
        if (!TextUtils.isEmpty(str2))
        {
          localObject1 = new ArrayMap();
          ((Map)localObject1).put("If-Modified-Since", str2);
        }
      }
      zzOt().zza(paramzzatf.getAppId(), localURL, (Map)localObject1, new zzaub.zza()
      {
        public void zza(String paramAnonymousString, int paramAnonymousInt, Throwable paramAnonymousThrowable, byte[] paramAnonymousArrayOfByte, Map<String, List<String>> paramAnonymousMap)
        {
          zzauh.this.zzb(paramAnonymousString, paramAnonymousInt, paramAnonymousThrowable, paramAnonymousArrayOfByte, paramAnonymousMap);
        }
      });
      return;
    }
    catch (MalformedURLException localMalformedURLException)
    {
      zzMg().zzNT().zze("Failed to parse config URL. Not fetching. appId", zzaua.zzfH(paramzzatf.getAppId()), str1);
    }
  }
  
  @WorkerThread
  void zzb(zzatg paramzzatg, long paramLong)
  {
    zzmW();
    zznA();
    Object localObject1 = zzMb().zzfx(paramzzatg.packageName);
    if ((localObject1 != null) && (TextUtils.isEmpty(((zzatf)localObject1).getGmpAppId())) && (paramzzatg != null) && (!TextUtils.isEmpty(paramzzatg.zzbLI)))
    {
      ((zzatf)localObject1).zzap(0L);
      zzMb().zza((zzatf)localObject1);
    }
    Bundle localBundle = new Bundle();
    localBundle.putLong("_c", 1L);
    localBundle.putLong("_r", 1L);
    localBundle.putLong("_uwa", 0L);
    localBundle.putLong("_pfo", 0L);
    localBundle.putLong("_sys", 0L);
    localBundle.putLong("_sysu", 0L);
    if (getContext().getPackageManager() == null) {
      zzMg().zzNT().zzm("PackageManager is null, first open report might be inaccurate. appId", zzaua.zzfH(paramzzatg.packageName));
    }
    for (;;)
    {
      long l = zzMb().zzfD(paramzzatg.packageName);
      if (l >= 0L) {
        localBundle.putLong("_pfo", l);
      }
      zzb(new zzatt("_f", new zzatr(localBundle), "auto", paramLong), paramzzatg);
      return;
      try
      {
        localObject1 = zzaca.zzbp(getContext()).getPackageInfo(paramzzatg.packageName, 0);
        if ((localObject1 != null) && (((PackageInfo)localObject1).firstInstallTime != 0L) && (((PackageInfo)localObject1).firstInstallTime != ((PackageInfo)localObject1).lastUpdateTime)) {
          localBundle.putLong("_uwa", 1L);
        }
      }
      catch (PackageManager.NameNotFoundException localNameNotFoundException1)
      {
        try
        {
          localObject1 = zzaca.zzbp(getContext()).getApplicationInfo(paramzzatg.packageName, 0);
          if (localObject1 == null) {
            continue;
          }
          if ((((ApplicationInfo)localObject1).flags & 0x1) != 0) {
            localBundle.putLong("_sys", 1L);
          }
          if ((((ApplicationInfo)localObject1).flags & 0x80) == 0) {
            continue;
          }
          localBundle.putLong("_sysu", 1L);
          continue;
          localNameNotFoundException1 = localNameNotFoundException1;
          zzMg().zzNT().zze("Package info is null, first open report might be inaccurate. appId", zzaua.zzfH(paramzzatg.packageName), localNameNotFoundException1);
          Object localObject2 = null;
        }
        catch (PackageManager.NameNotFoundException localNameNotFoundException2)
        {
          for (;;)
          {
            zzMg().zzNT().zze("Application info is null, first open report might be inaccurate. appId", zzaua.zzfH(paramzzatg.packageName), localNameNotFoundException2);
            Object localObject3 = null;
          }
        }
      }
    }
  }
  
  @WorkerThread
  void zzb(zzatj paramzzatj, zzatg paramzzatg)
  {
    zzac.zzC(paramzzatj);
    zzac.zzdc(paramzzatj.packageName);
    zzac.zzC(paramzzatj.zzaIu);
    zzac.zzC(paramzzatj.zzbLS);
    zzac.zzdc(paramzzatj.zzbLS.name);
    zzmW();
    zznA();
    if (TextUtils.isEmpty(paramzzatg.zzbLI)) {
      return;
    }
    if (!paramzzatg.zzbLN)
    {
      zzf(paramzzatg);
      return;
    }
    paramzzatj = new zzatj(paramzzatj);
    zzMb().beginTransaction();
    for (;;)
    {
      try
      {
        Object localObject = zzMb().zzad(paramzzatj.packageName, paramzzatj.zzbLS.name);
        if ((localObject != null) && (((zzatj)localObject).zzbLU))
        {
          paramzzatj.zzaIu = ((zzatj)localObject).zzaIu;
          paramzzatj.zzbLT = ((zzatj)localObject).zzbLT;
          paramzzatj.zzbLV = ((zzatj)localObject).zzbLV;
          paramzzatj.zzbLY = ((zzatj)localObject).zzbLY;
          i = 0;
          if (paramzzatj.zzbLU)
          {
            localObject = paramzzatj.zzbLS;
            localObject = new zzauv(paramzzatj.packageName, paramzzatj.zzaIu, ((zzaut)localObject).name, ((zzaut)localObject).zzbQW, ((zzaut)localObject).getValue());
            if (!zzMb().zza((zzauv)localObject)) {
              continue;
            }
            zzMg().zzNY().zzd("User property updated immediately", paramzzatj.packageName, ((zzauv)localObject).mName, ((zzauv)localObject).mValue);
            if ((i != 0) && (paramzzatj.zzbLY != null)) {
              zzc(new zzatt(paramzzatj.zzbLY, paramzzatj.zzbLT), paramzzatg);
            }
          }
          if (!zzMb().zza(paramzzatj)) {
            break label430;
          }
          zzMg().zzNY().zzd("Conditional property added", paramzzatj.packageName, paramzzatj.zzbLS.name, paramzzatj.zzbLS.getValue());
          zzMb().setTransactionSuccessful();
        }
        else
        {
          if (!TextUtils.isEmpty(paramzzatj.zzbLV)) {
            break label467;
          }
          localObject = paramzzatj.zzbLS;
          paramzzatj.zzbLS = new zzaut(((zzaut)localObject).name, paramzzatj.zzbLT, ((zzaut)localObject).getValue(), ((zzaut)localObject).zzaIu);
          paramzzatj.zzbLU = true;
          i = 1;
          continue;
        }
        zzMg().zzNT().zzd("(2)Too many active user properties, ignoring", zzaua.zzfH(paramzzatj.packageName), ((zzauv)localObject).mName, ((zzauv)localObject).mValue);
        continue;
        zzMg().zzNT().zzd("Too many conditional properties, ignoring", zzaua.zzfH(paramzzatj.packageName), paramzzatj.zzbLS.name, paramzzatj.zzbLS.getValue());
      }
      finally
      {
        zzMb().endTransaction();
      }
      label430:
      continue;
      label467:
      int i = 0;
    }
  }
  
  @WorkerThread
  void zzb(zzatt paramzzatt, zzatg paramzzatg)
  {
    zzac.zzC(paramzzatg);
    zzac.zzdc(paramzzatg.packageName);
    zzmW();
    zznA();
    Object localObject1 = paramzzatg.packageName;
    long l = paramzzatt.zzbMD;
    if (!zzMc().zzd(paramzzatt, paramzzatg)) {
      return;
    }
    if (!paramzzatg.zzbLN)
    {
      zzf(paramzzatg);
      return;
    }
    zzMb().beginTransaction();
    try
    {
      localObject2 = zzMb().zzh((String)localObject1, l).iterator();
      while (((Iterator)localObject2).hasNext())
      {
        localObject3 = (zzatj)((Iterator)localObject2).next();
        if (localObject3 != null)
        {
          zzMg().zzNY().zzd("User property timed out", ((zzatj)localObject3).packageName, ((zzatj)localObject3).zzbLS.name, ((zzatj)localObject3).zzbLS.getValue());
          if (((zzatj)localObject3).zzbLW != null) {
            zzc(new zzatt(((zzatj)localObject3).zzbLW, l), paramzzatg);
          }
          zzMb().zzae((String)localObject1, ((zzatj)localObject3).zzbLS.name);
        }
      }
      localObject3 = zzMb().zzi((String)localObject1, l);
    }
    finally
    {
      zzMb().endTransaction();
    }
    Object localObject2 = new ArrayList(((List)localObject3).size());
    Object localObject3 = ((List)localObject3).iterator();
    Object localObject4;
    while (((Iterator)localObject3).hasNext())
    {
      localObject4 = (zzatj)((Iterator)localObject3).next();
      if (localObject4 != null)
      {
        zzMg().zzNY().zzd("User property expired", ((zzatj)localObject4).packageName, ((zzatj)localObject4).zzbLS.name, ((zzatj)localObject4).zzbLS.getValue());
        zzMb().zzab((String)localObject1, ((zzatj)localObject4).zzbLS.name);
        if (((zzatj)localObject4).zzbMa != null) {
          ((List)localObject2).add(((zzatj)localObject4).zzbMa);
        }
        zzMb().zzae((String)localObject1, ((zzatj)localObject4).zzbLS.name);
      }
    }
    localObject2 = ((List)localObject2).iterator();
    while (((Iterator)localObject2).hasNext()) {
      zzc(new zzatt((zzatt)((Iterator)localObject2).next(), l), paramzzatg);
    }
    localObject2 = zzMb().zzc((String)localObject1, paramzzatt.name, l);
    localObject1 = new ArrayList(((List)localObject2).size());
    localObject2 = ((List)localObject2).iterator();
    while (((Iterator)localObject2).hasNext())
    {
      localObject3 = (zzatj)((Iterator)localObject2).next();
      if (localObject3 != null)
      {
        localObject4 = ((zzatj)localObject3).zzbLS;
        localObject4 = new zzauv(((zzatj)localObject3).packageName, ((zzatj)localObject3).zzaIu, ((zzaut)localObject4).name, l, ((zzaut)localObject4).getValue());
        if (zzMb().zza((zzauv)localObject4)) {
          zzMg().zzNY().zzd("User property triggered", ((zzatj)localObject3).packageName, ((zzauv)localObject4).mName, ((zzauv)localObject4).mValue);
        }
        for (;;)
        {
          if (((zzatj)localObject3).zzbLY != null) {
            ((List)localObject1).add(((zzatj)localObject3).zzbLY);
          }
          ((zzatj)localObject3).zzbLS = new zzaut((zzauv)localObject4);
          ((zzatj)localObject3).zzbLU = true;
          zzMb().zza((zzatj)localObject3);
          break;
          zzMg().zzNT().zzd("Too many active user properties, ignoring", zzaua.zzfH(((zzatj)localObject3).packageName), ((zzauv)localObject4).mName, ((zzauv)localObject4).mValue);
        }
      }
    }
    zzc(paramzzatt, paramzzatg);
    paramzzatt = ((List)localObject1).iterator();
    while (paramzzatt.hasNext()) {
      zzc(new zzatt((zzatt)paramzzatt.next(), l), paramzzatg);
    }
    zzMb().setTransactionSuccessful();
    zzMb().endTransaction();
  }
  
  @WorkerThread
  void zzb(zzatt paramzzatt, String paramString)
  {
    zzatf localzzatf = zzMb().zzfx(paramString);
    if ((localzzatf == null) || (TextUtils.isEmpty(localzzatf.getAppVersion())))
    {
      zzMg().zzNY().zzm("No app data available; dropping event", paramString);
      return;
    }
    try
    {
      String str = zzaca.zzbp(getContext()).getPackageInfo(paramString, 0).versionName;
      if ((localzzatf.getAppVersion() != null) && (!localzzatf.getAppVersion().equals(str)))
      {
        zzMg().zzNV().zzm("App version does not match; dropping event. appId", zzaua.zzfH(paramString));
        return;
      }
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      if (!"_ui".equals(paramzzatt.name)) {
        zzMg().zzNV().zzm("Could not find package. appId", zzaua.zzfH(paramString));
      }
      zzb(paramzzatt, new zzatg(paramString, localzzatf.getGmpAppId(), localzzatf.getAppVersion(), localzzatf.zzMo(), localzzatf.zzMp(), localzzatf.zzMq(), localzzatf.zzMr(), null, localzzatf.zzMs(), false, localzzatf.zzMl(), localzzatf.zzwJ()));
    }
  }
  
  void zzb(zzauk paramzzauk)
  {
    this.zzbPw += 1;
  }
  
  /* Error */
  @WorkerThread
  void zzb(zzaut paramzzaut, zzatg paramzzatg)
  {
    // Byte code:
    //   0: iconst_0
    //   1: istore 4
    //   3: iconst_0
    //   4: istore_3
    //   5: aload_0
    //   6: invokevirtual 415	com/google/android/gms/internal/zzauh:zzmW	()V
    //   9: aload_0
    //   10: invokevirtual 418	com/google/android/gms/internal/zzauh:zznA	()V
    //   13: aload_2
    //   14: getfield 703	com/google/android/gms/internal/zzatg:zzbLI	Ljava/lang/String;
    //   17: invokestatic 434	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   20: ifeq +4 -> 24
    //   23: return
    //   24: aload_2
    //   25: getfield 763	com/google/android/gms/internal/zzatg:zzbLN	Z
    //   28: ifne +9 -> 37
    //   31: aload_0
    //   32: aload_2
    //   33: invokespecial 1777	com/google/android/gms/internal/zzauh:zzf	(Lcom/google/android/gms/internal/zzatg;)V
    //   36: return
    //   37: aload_0
    //   38: invokevirtual 210	com/google/android/gms/internal/zzauh:zzMc	()Lcom/google/android/gms/internal/zzauw;
    //   41: aload_1
    //   42: getfield 1775	com/google/android/gms/internal/zzaut:name	Ljava/lang/String;
    //   45: invokevirtual 1883	com/google/android/gms/internal/zzauw:zzga	(Ljava/lang/String;)I
    //   48: istore 5
    //   50: iload 5
    //   52: ifeq +53 -> 105
    //   55: aload_0
    //   56: invokevirtual 210	com/google/android/gms/internal/zzauh:zzMc	()Lcom/google/android/gms/internal/zzauw;
    //   59: aload_1
    //   60: getfield 1775	com/google/android/gms/internal/zzaut:name	Ljava/lang/String;
    //   63: aload_0
    //   64: invokevirtual 147	com/google/android/gms/internal/zzauh:zzMi	()Lcom/google/android/gms/internal/zzatl;
    //   67: invokevirtual 1886	com/google/android/gms/internal/zzatl:zzMI	()I
    //   70: iconst_1
    //   71: invokevirtual 1889	com/google/android/gms/internal/zzauw:zzc	(Ljava/lang/String;IZ)Ljava/lang/String;
    //   74: astore_2
    //   75: aload_1
    //   76: getfield 1775	com/google/android/gms/internal/zzaut:name	Ljava/lang/String;
    //   79: ifnull +11 -> 90
    //   82: aload_1
    //   83: getfield 1775	com/google/android/gms/internal/zzaut:name	Ljava/lang/String;
    //   86: invokevirtual 397	java/lang/String:length	()I
    //   89: istore_3
    //   90: aload_0
    //   91: invokevirtual 210	com/google/android/gms/internal/zzauh:zzMc	()Lcom/google/android/gms/internal/zzauw;
    //   94: iload 5
    //   96: ldc_w 854
    //   99: aload_2
    //   100: iload_3
    //   101: invokevirtual 857	com/google/android/gms/internal/zzauw:zza	(ILjava/lang/String;Ljava/lang/String;I)V
    //   104: return
    //   105: aload_0
    //   106: invokevirtual 210	com/google/android/gms/internal/zzauh:zzMc	()Lcom/google/android/gms/internal/zzauw;
    //   109: aload_1
    //   110: getfield 1775	com/google/android/gms/internal/zzaut:name	Ljava/lang/String;
    //   113: aload_1
    //   114: invokevirtual 1803	com/google/android/gms/internal/zzaut:getValue	()Ljava/lang/Object;
    //   117: invokevirtual 1892	com/google/android/gms/internal/zzauw:zzp	(Ljava/lang/String;Ljava/lang/Object;)I
    //   120: istore 5
    //   122: iload 5
    //   124: ifeq +75 -> 199
    //   127: aload_0
    //   128: invokevirtual 210	com/google/android/gms/internal/zzauh:zzMc	()Lcom/google/android/gms/internal/zzauw;
    //   131: aload_1
    //   132: getfield 1775	com/google/android/gms/internal/zzaut:name	Ljava/lang/String;
    //   135: aload_0
    //   136: invokevirtual 147	com/google/android/gms/internal/zzauh:zzMi	()Lcom/google/android/gms/internal/zzatl;
    //   139: invokevirtual 1886	com/google/android/gms/internal/zzatl:zzMI	()I
    //   142: iconst_1
    //   143: invokevirtual 1889	com/google/android/gms/internal/zzauw:zzc	(Ljava/lang/String;IZ)Ljava/lang/String;
    //   146: astore_2
    //   147: aload_1
    //   148: invokevirtual 1803	com/google/android/gms/internal/zzaut:getValue	()Ljava/lang/Object;
    //   151: astore_1
    //   152: iload 4
    //   154: istore_3
    //   155: aload_1
    //   156: ifnull +28 -> 184
    //   159: aload_1
    //   160: instanceof 390
    //   163: ifne +13 -> 176
    //   166: iload 4
    //   168: istore_3
    //   169: aload_1
    //   170: instanceof 1455
    //   173: ifeq +11 -> 184
    //   176: aload_1
    //   177: invokestatic 393	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   180: invokevirtual 397	java/lang/String:length	()I
    //   183: istore_3
    //   184: aload_0
    //   185: invokevirtual 210	com/google/android/gms/internal/zzauh:zzMc	()Lcom/google/android/gms/internal/zzauw;
    //   188: iload 5
    //   190: ldc_w 854
    //   193: aload_2
    //   194: iload_3
    //   195: invokevirtual 857	com/google/android/gms/internal/zzauw:zza	(ILjava/lang/String;Ljava/lang/String;I)V
    //   198: return
    //   199: aload_0
    //   200: invokevirtual 210	com/google/android/gms/internal/zzauh:zzMc	()Lcom/google/android/gms/internal/zzauw;
    //   203: aload_1
    //   204: getfield 1775	com/google/android/gms/internal/zzaut:name	Ljava/lang/String;
    //   207: aload_1
    //   208: invokevirtual 1803	com/google/android/gms/internal/zzaut:getValue	()Ljava/lang/Object;
    //   211: invokevirtual 1895	com/google/android/gms/internal/zzauw:zzq	(Ljava/lang/String;Ljava/lang/Object;)Ljava/lang/Object;
    //   214: astore 7
    //   216: aload 7
    //   218: ifnull -195 -> 23
    //   221: new 1505	com/google/android/gms/internal/zzauv
    //   224: dup
    //   225: aload_2
    //   226: getfield 679	com/google/android/gms/internal/zzatg:packageName	Ljava/lang/String;
    //   229: aload_1
    //   230: getfield 1822	com/google/android/gms/internal/zzaut:zzaIu	Ljava/lang/String;
    //   233: aload_1
    //   234: getfield 1775	com/google/android/gms/internal/zzaut:name	Ljava/lang/String;
    //   237: aload_1
    //   238: getfield 1800	com/google/android/gms/internal/zzaut:zzbQW	J
    //   241: aload 7
    //   243: invokespecial 1806	com/google/android/gms/internal/zzauv:<init>	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;JLjava/lang/Object;)V
    //   246: astore_1
    //   247: aload_0
    //   248: invokevirtual 137	com/google/android/gms/internal/zzauh:zzMg	()Lcom/google/android/gms/internal/zzaua;
    //   251: invokevirtual 219	com/google/android/gms/internal/zzaua:zzNY	()Lcom/google/android/gms/internal/zzaua$zza;
    //   254: ldc_w 1897
    //   257: aload_1
    //   258: getfield 1506	com/google/android/gms/internal/zzauv:mName	Ljava/lang/String;
    //   261: aload 7
    //   263: invokevirtual 360	com/google/android/gms/internal/zzaua$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   266: aload_0
    //   267: invokevirtual 422	com/google/android/gms/internal/zzauh:zzMb	()Lcom/google/android/gms/internal/zzatm;
    //   270: invokevirtual 798	com/google/android/gms/internal/zzatm:beginTransaction	()V
    //   273: aload_0
    //   274: aload_2
    //   275: invokespecial 1777	com/google/android/gms/internal/zzauh:zzf	(Lcom/google/android/gms/internal/zzatg;)V
    //   278: aload_0
    //   279: invokevirtual 422	com/google/android/gms/internal/zzauh:zzMb	()Lcom/google/android/gms/internal/zzatm;
    //   282: aload_1
    //   283: invokevirtual 1809	com/google/android/gms/internal/zzatm:zza	(Lcom/google/android/gms/internal/zzauv;)Z
    //   286: istore 6
    //   288: aload_0
    //   289: invokevirtual 422	com/google/android/gms/internal/zzauh:zzMb	()Lcom/google/android/gms/internal/zzatm;
    //   292: invokevirtual 964	com/google/android/gms/internal/zzatm:setTransactionSuccessful	()V
    //   295: iload 6
    //   297: ifeq +32 -> 329
    //   300: aload_0
    //   301: invokevirtual 137	com/google/android/gms/internal/zzauh:zzMg	()Lcom/google/android/gms/internal/zzaua;
    //   304: invokevirtual 219	com/google/android/gms/internal/zzaua:zzNY	()Lcom/google/android/gms/internal/zzaua$zza;
    //   307: ldc_w 1899
    //   310: aload_1
    //   311: getfield 1506	com/google/android/gms/internal/zzauv:mName	Ljava/lang/String;
    //   314: aload_1
    //   315: getfield 1516	com/google/android/gms/internal/zzauv:mValue	Ljava/lang/Object;
    //   318: invokevirtual 360	com/google/android/gms/internal/zzaua$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   321: aload_0
    //   322: invokevirtual 422	com/google/android/gms/internal/zzauh:zzMb	()Lcom/google/android/gms/internal/zzatm;
    //   325: invokevirtual 967	com/google/android/gms/internal/zzatm:endTransaction	()V
    //   328: return
    //   329: aload_0
    //   330: invokevirtual 137	com/google/android/gms/internal/zzauh:zzMg	()Lcom/google/android/gms/internal/zzaua;
    //   333: invokevirtual 350	com/google/android/gms/internal/zzaua:zzNT	()Lcom/google/android/gms/internal/zzaua$zza;
    //   336: ldc_w 1901
    //   339: aload_1
    //   340: getfield 1506	com/google/android/gms/internal/zzauv:mName	Ljava/lang/String;
    //   343: aload_1
    //   344: getfield 1516	com/google/android/gms/internal/zzauv:mValue	Ljava/lang/Object;
    //   347: invokevirtual 360	com/google/android/gms/internal/zzaua$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   350: aload_0
    //   351: invokevirtual 210	com/google/android/gms/internal/zzauh:zzMc	()Lcom/google/android/gms/internal/zzauw;
    //   354: bipush 9
    //   356: aconst_null
    //   357: aconst_null
    //   358: iconst_0
    //   359: invokevirtual 857	com/google/android/gms/internal/zzauw:zza	(ILjava/lang/String;Ljava/lang/String;I)V
    //   362: goto -41 -> 321
    //   365: astore_1
    //   366: aload_0
    //   367: invokevirtual 422	com/google/android/gms/internal/zzauh:zzMb	()Lcom/google/android/gms/internal/zzatm;
    //   370: invokevirtual 967	com/google/android/gms/internal/zzatm:endTransaction	()V
    //   373: aload_1
    //   374: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	375	0	this	zzauh
    //   0	375	1	paramzzaut	zzaut
    //   0	375	2	paramzzatg	zzatg
    //   4	191	3	i	int
    //   1	166	4	j	int
    //   48	141	5	k	int
    //   286	10	6	bool	boolean
    //   214	48	7	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   273	295	365	finally
    //   300	321	365	finally
    //   329	362	365	finally
  }
  
  @WorkerThread
  void zzb(String paramString, int paramInt, Throwable paramThrowable, byte[] paramArrayOfByte, Map<String, List<String>> paramMap)
  {
    int j = 0;
    zzmW();
    zznA();
    zzac.zzdc(paramString);
    byte[] arrayOfByte = paramArrayOfByte;
    if (paramArrayOfByte == null) {
      arrayOfByte = new byte[0];
    }
    zzMb().beginTransaction();
    label93:
    label108:
    int i;
    for (;;)
    {
      try
      {
        paramArrayOfByte = zzMb().zzfx(paramString);
        if ((paramInt == 200) || (paramInt == 204)) {
          break label475;
        }
        if (paramInt == 304)
        {
          break label475;
          if (paramArrayOfByte == null)
          {
            zzMg().zzNV().zzm("App does not exist in onConfigFetched. appId", zzaua.zzfH(paramString));
            zzMb().setTransactionSuccessful();
          }
        }
        else
        {
          i = 0;
          continue;
        }
        if ((i == 0) && (paramInt != 404)) {
          break;
        }
        if (paramMap != null)
        {
          paramThrowable = (List)paramMap.get("Last-Modified");
          if ((paramThrowable != null) && (paramThrowable.size() > 0))
          {
            paramThrowable = (String)paramThrowable.get(0);
            break label485;
            label172:
            if (zzMd().zzfO(paramString) != null) {
              continue;
            }
            bool = zzMd().zza(paramString, null, null);
            if (bool) {
              continue;
            }
          }
        }
        else
        {
          paramThrowable = null;
          continue;
        }
        paramThrowable = null;
        break label485;
        label218:
        boolean bool = zzMd().zza(paramString, arrayOfByte, paramThrowable);
        if (!bool) {
          return;
        }
        paramArrayOfByte.zzap(zznq().currentTimeMillis());
        zzMb().zza(paramArrayOfByte);
        if (paramInt == 404)
        {
          zzMg().zzNW().zzm("Config not found. Using empty config. appId", paramString);
          if ((!zzOt().isNetworkConnected()) || (!zzOC())) {
            break label348;
          }
          zzOB();
          continue;
        }
        zzMg().zzNZ().zze("Successfully fetched config. Got network response. code, size", Integer.valueOf(paramInt), Integer.valueOf(arrayOfByte.length));
      }
      finally
      {
        zzMb().endTransaction();
      }
      continue;
      label348:
      zzOD();
    }
    paramArrayOfByte.zzaq(zznq().currentTimeMillis());
    zzMb().zza(paramArrayOfByte);
    zzMg().zzNZ().zze("Fetching config failed. code, error", Integer.valueOf(paramInt), paramThrowable);
    zzMd().zzfQ(paramString);
    zzMh().zzbNW.set(zznq().currentTimeMillis());
    if (paramInt != 503)
    {
      i = j;
      if (paramInt == 429) {}
    }
    for (;;)
    {
      if (i != 0) {
        zzMh().zzbNX.set(zznq().currentTimeMillis());
      }
      zzOD();
      break label93;
      label475:
      if (paramThrowable != null) {
        break label108;
      }
      i = 1;
      break;
      label485:
      if (paramInt == 404) {
        break label172;
      }
      if (paramInt != 304) {
        break label218;
      }
      break label172;
      i = 1;
    }
  }
  
  @WorkerThread
  void zzc(zzatg paramzzatg, long paramLong)
  {
    Bundle localBundle = new Bundle();
    localBundle.putLong("_et", 1L);
    zzb(new zzatt("_e", new zzatr(localBundle), "auto", paramLong), paramzzatg);
  }
  
  /* Error */
  @WorkerThread
  void zzc(zzatj paramzzatj, zzatg paramzzatg)
  {
    // Byte code:
    //   0: aload_1
    //   1: invokestatic 93	com/google/android/gms/common/internal/zzac:zzC	(Ljava/lang/Object;)Ljava/lang/Object;
    //   4: pop
    //   5: aload_1
    //   6: getfield 1767	com/google/android/gms/internal/zzatj:packageName	Ljava/lang/String;
    //   9: invokestatic 655	com/google/android/gms/common/internal/zzac:zzdc	(Ljava/lang/String;)Ljava/lang/String;
    //   12: pop
    //   13: aload_1
    //   14: getfield 1772	com/google/android/gms/internal/zzatj:zzbLS	Lcom/google/android/gms/internal/zzaut;
    //   17: invokestatic 93	com/google/android/gms/common/internal/zzac:zzC	(Ljava/lang/Object;)Ljava/lang/Object;
    //   20: pop
    //   21: aload_1
    //   22: getfield 1772	com/google/android/gms/internal/zzatj:zzbLS	Lcom/google/android/gms/internal/zzaut;
    //   25: getfield 1775	com/google/android/gms/internal/zzaut:name	Ljava/lang/String;
    //   28: invokestatic 655	com/google/android/gms/common/internal/zzac:zzdc	(Ljava/lang/String;)Ljava/lang/String;
    //   31: pop
    //   32: aload_0
    //   33: invokevirtual 415	com/google/android/gms/internal/zzauh:zzmW	()V
    //   36: aload_0
    //   37: invokevirtual 418	com/google/android/gms/internal/zzauh:zznA	()V
    //   40: aload_2
    //   41: getfield 703	com/google/android/gms/internal/zzatg:zzbLI	Ljava/lang/String;
    //   44: invokestatic 434	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   47: ifeq +4 -> 51
    //   50: return
    //   51: aload_2
    //   52: getfield 763	com/google/android/gms/internal/zzatg:zzbLN	Z
    //   55: ifne +9 -> 64
    //   58: aload_0
    //   59: aload_2
    //   60: invokespecial 1777	com/google/android/gms/internal/zzauh:zzf	(Lcom/google/android/gms/internal/zzatg;)V
    //   63: return
    //   64: aload_0
    //   65: invokevirtual 422	com/google/android/gms/internal/zzauh:zzMb	()Lcom/google/android/gms/internal/zzatm;
    //   68: invokevirtual 798	com/google/android/gms/internal/zzatm:beginTransaction	()V
    //   71: aload_0
    //   72: aload_2
    //   73: invokespecial 1777	com/google/android/gms/internal/zzauh:zzf	(Lcom/google/android/gms/internal/zzatg;)V
    //   76: aload_0
    //   77: invokevirtual 422	com/google/android/gms/internal/zzauh:zzMb	()Lcom/google/android/gms/internal/zzatm;
    //   80: aload_1
    //   81: getfield 1767	com/google/android/gms/internal/zzatj:packageName	Ljava/lang/String;
    //   84: aload_1
    //   85: getfield 1772	com/google/android/gms/internal/zzatj:zzbLS	Lcom/google/android/gms/internal/zzaut;
    //   88: getfield 1775	com/google/android/gms/internal/zzaut:name	Ljava/lang/String;
    //   91: invokevirtual 1784	com/google/android/gms/internal/zzatm:zzad	(Ljava/lang/String;Ljava/lang/String;)Lcom/google/android/gms/internal/zzatj;
    //   94: astore 4
    //   96: aload 4
    //   98: ifnull +151 -> 249
    //   101: aload_0
    //   102: invokevirtual 137	com/google/android/gms/internal/zzauh:zzMg	()Lcom/google/android/gms/internal/zzaua;
    //   105: invokevirtual 219	com/google/android/gms/internal/zzaua:zzNY	()Lcom/google/android/gms/internal/zzaua$zza;
    //   108: ldc_w 1932
    //   111: aload_1
    //   112: getfield 1767	com/google/android/gms/internal/zzatj:packageName	Ljava/lang/String;
    //   115: aload_1
    //   116: getfield 1772	com/google/android/gms/internal/zzatj:zzbLS	Lcom/google/android/gms/internal/zzaut;
    //   119: getfield 1775	com/google/android/gms/internal/zzaut:name	Ljava/lang/String;
    //   122: invokevirtual 360	com/google/android/gms/internal/zzaua$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   125: aload_0
    //   126: invokevirtual 422	com/google/android/gms/internal/zzauh:zzMb	()Lcom/google/android/gms/internal/zzatm;
    //   129: aload_1
    //   130: getfield 1767	com/google/android/gms/internal/zzatj:packageName	Ljava/lang/String;
    //   133: aload_1
    //   134: getfield 1772	com/google/android/gms/internal/zzatj:zzbLS	Lcom/google/android/gms/internal/zzaut;
    //   137: getfield 1775	com/google/android/gms/internal/zzaut:name	Ljava/lang/String;
    //   140: invokevirtual 1844	com/google/android/gms/internal/zzatm:zzae	(Ljava/lang/String;Ljava/lang/String;)I
    //   143: pop
    //   144: aload 4
    //   146: getfield 1787	com/google/android/gms/internal/zzatj:zzbLU	Z
    //   149: ifeq +21 -> 170
    //   152: aload_0
    //   153: invokevirtual 422	com/google/android/gms/internal/zzauh:zzMb	()Lcom/google/android/gms/internal/zzatm;
    //   156: aload_1
    //   157: getfield 1767	com/google/android/gms/internal/zzatj:packageName	Ljava/lang/String;
    //   160: aload_1
    //   161: getfield 1772	com/google/android/gms/internal/zzatj:zzbLS	Lcom/google/android/gms/internal/zzaut;
    //   164: getfield 1775	com/google/android/gms/internal/zzaut:name	Ljava/lang/String;
    //   167: invokevirtual 1851	com/google/android/gms/internal/zzatm:zzab	(Ljava/lang/String;Ljava/lang/String;)V
    //   170: aload_1
    //   171: getfield 1854	com/google/android/gms/internal/zzatj:zzbMa	Lcom/google/android/gms/internal/zzatt;
    //   174: ifnull +60 -> 234
    //   177: aconst_null
    //   178: astore_3
    //   179: aload_1
    //   180: getfield 1854	com/google/android/gms/internal/zzatj:zzbMa	Lcom/google/android/gms/internal/zzatt;
    //   183: getfield 1589	com/google/android/gms/internal/zzatt:zzbMC	Lcom/google/android/gms/internal/zzatr;
    //   186: ifnull +14 -> 200
    //   189: aload_1
    //   190: getfield 1854	com/google/android/gms/internal/zzatj:zzbMa	Lcom/google/android/gms/internal/zzatt;
    //   193: getfield 1589	com/google/android/gms/internal/zzatt:zzbMC	Lcom/google/android/gms/internal/zzatr;
    //   196: invokevirtual 1593	com/google/android/gms/internal/zzatr:zzNR	()Landroid/os/Bundle;
    //   199: astore_3
    //   200: aload_0
    //   201: aload_0
    //   202: invokevirtual 210	com/google/android/gms/internal/zzauh:zzMc	()Lcom/google/android/gms/internal/zzauw;
    //   205: aload_1
    //   206: getfield 1854	com/google/android/gms/internal/zzatj:zzbMa	Lcom/google/android/gms/internal/zzatt;
    //   209: getfield 1596	com/google/android/gms/internal/zzatt:name	Ljava/lang/String;
    //   212: aload_3
    //   213: aload 4
    //   215: getfield 1768	com/google/android/gms/internal/zzatj:zzaIu	Ljava/lang/String;
    //   218: aload_1
    //   219: getfield 1854	com/google/android/gms/internal/zzatj:zzbMa	Lcom/google/android/gms/internal/zzatt;
    //   222: getfield 1621	com/google/android/gms/internal/zzatt:zzbMD	J
    //   225: iconst_1
    //   226: iconst_0
    //   227: invokevirtual 1935	com/google/android/gms/internal/zzauw:zza	(Ljava/lang/String;Landroid/os/Bundle;Ljava/lang/String;JZZ)Lcom/google/android/gms/internal/zzatt;
    //   230: aload_2
    //   231: invokevirtual 1816	com/google/android/gms/internal/zzauh:zzc	(Lcom/google/android/gms/internal/zzatt;Lcom/google/android/gms/internal/zzatg;)V
    //   234: aload_0
    //   235: invokevirtual 422	com/google/android/gms/internal/zzauh:zzMb	()Lcom/google/android/gms/internal/zzatm;
    //   238: invokevirtual 964	com/google/android/gms/internal/zzatm:setTransactionSuccessful	()V
    //   241: aload_0
    //   242: invokevirtual 422	com/google/android/gms/internal/zzauh:zzMb	()Lcom/google/android/gms/internal/zzatm;
    //   245: invokevirtual 967	com/google/android/gms/internal/zzatm:endTransaction	()V
    //   248: return
    //   249: aload_0
    //   250: invokevirtual 137	com/google/android/gms/internal/zzauh:zzMg	()Lcom/google/android/gms/internal/zzaua;
    //   253: invokevirtual 408	com/google/android/gms/internal/zzaua:zzNV	()Lcom/google/android/gms/internal/zzaua$zza;
    //   256: ldc_w 1937
    //   259: aload_1
    //   260: getfield 1767	com/google/android/gms/internal/zzatj:packageName	Ljava/lang/String;
    //   263: invokestatic 844	com/google/android/gms/internal/zzaua:zzfH	(Ljava/lang/String;)Ljava/lang/Object;
    //   266: aload_1
    //   267: getfield 1772	com/google/android/gms/internal/zzatj:zzbLS	Lcom/google/android/gms/internal/zzaut;
    //   270: getfield 1775	com/google/android/gms/internal/zzaut:name	Ljava/lang/String;
    //   273: invokevirtual 360	com/google/android/gms/internal/zzaua$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   276: goto -42 -> 234
    //   279: astore_1
    //   280: aload_0
    //   281: invokevirtual 422	com/google/android/gms/internal/zzauh:zzMb	()Lcom/google/android/gms/internal/zzatm;
    //   284: invokevirtual 967	com/google/android/gms/internal/zzatm:endTransaction	()V
    //   287: aload_1
    //   288: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	289	0	this	zzauh
    //   0	289	1	paramzzatj	zzatj
    //   0	289	2	paramzzatg	zzatg
    //   178	35	3	localBundle	Bundle
    //   94	120	4	localzzatj	zzatj
    // Exception table:
    //   from	to	target	type
    //   71	96	279	finally
    //   101	170	279	finally
    //   170	177	279	finally
    //   179	200	279	finally
    //   200	234	279	finally
    //   234	241	279	finally
    //   249	276	279	finally
  }
  
  @WorkerThread
  void zzc(zzatt paramzzatt, zzatg paramzzatg)
  {
    zzac.zzC(paramzzatg);
    zzac.zzdc(paramzzatg.packageName);
    long l2 = System.nanoTime();
    zzmW();
    zznA();
    String str = paramzzatg.packageName;
    if (!zzMc().zzd(paramzzatt, paramzzatg)) {
      return;
    }
    if (!paramzzatg.zzbLN)
    {
      zzf(paramzzatg);
      return;
    }
    if (zzMd().zzak(str, paramzzatt.name))
    {
      zzMg().zzNV().zze("Dropping blacklisted event. appId", zzaua.zzfH(str), paramzzatt.name);
      if ((zzMc().zzgj(str)) || (zzMc().zzgk(str))) {}
      for (int i = 1;; i = 0)
      {
        if ((i == 0) && (!"_err".equals(paramzzatt.name))) {
          zzMc().zza(11, "_ev", paramzzatt.name, 0);
        }
        if (i == 0) {
          break;
        }
        paramzzatt = zzMb().zzfx(str);
        if (paramzzatt == null) {
          break;
        }
        l1 = Math.max(paramzzatt.zzMv(), paramzzatt.zzMu());
        if (Math.abs(zznq().currentTimeMillis() - l1) <= zzMi().zzNg()) {
          break;
        }
        zzMg().zzNY().log("Fetching config for blacklisted app");
        zzb(paramzzatt);
        return;
      }
    }
    if (zzMg().zzao(2)) {
      zzMg().zzNZ().zzm("Logging event", paramzzatt);
    }
    zzMb().beginTransaction();
    Bundle localBundle;
    boolean bool1;
    boolean bool2;
    for (;;)
    {
      try
      {
        localBundle = paramzzatt.zzbMC.zzNR();
        zzf(paramzzatg);
        double d1;
        Object localObject2;
        if (("_iap".equals(paramzzatt.name)) || ("ecommerce_purchase".equals(paramzzatt.name)))
        {
          localObject1 = localBundle.getString("currency");
          if (!"ecommerce_purchase".equals(paramzzatt.name)) {
            continue;
          }
          double d2 = localBundle.getDouble("value") * 1000000.0D;
          d1 = d2;
          if (d2 == 0.0D) {
            d1 = localBundle.getLong("value") * 1000000.0D;
          }
          if ((d1 > 9.223372036854776E18D) || (d1 < -9.223372036854776E18D)) {
            continue;
          }
          l1 = Math.round(d1);
          if (!TextUtils.isEmpty((CharSequence)localObject1))
          {
            localObject2 = ((String)localObject1).toUpperCase(Locale.US);
            if (((String)localObject2).matches("[A-Z]{3}"))
            {
              localObject1 = String.valueOf("_ltv_");
              localObject2 = String.valueOf(localObject2);
              if (((String)localObject2).length() == 0) {
                continue;
              }
              localObject1 = ((String)localObject1).concat((String)localObject2);
              localObject2 = zzMb().zzac(str, (String)localObject1);
              if ((localObject2 != null) && ((((zzauv)localObject2).mValue instanceof Long))) {
                break label797;
              }
              zzMb().zzA(str, zzMi().zzfq(str) - 1);
              localObject1 = new zzauv(str, paramzzatt.zzaIu, (String)localObject1, zznq().currentTimeMillis(), Long.valueOf(l1));
              if (!zzMb().zza((zzauv)localObject1))
              {
                zzMg().zzNT().zzd("Too many unique user properties are set. Ignoring user property. appId", zzaua.zzfH(str), ((zzauv)localObject1).mName, ((zzauv)localObject1).mValue);
                zzMc().zza(9, null, null, 0);
              }
            }
          }
        }
        bool1 = zzauw.zzfW(paramzzatt.name);
        bool2 = "_err".equals(paramzzatt.name);
        localObject1 = zzMb().zza(zzOz(), str, true, bool1, false, bool2, false);
        l1 = ((zzatm.zza)localObject1).zzbMk - zzMi().zzMP();
        if (l1 <= 0L) {
          break;
        }
        if (l1 % 1000L == 1L) {
          zzMg().zzNT().zze("Data loss. Too many events logged. appId, count", zzaua.zzfH(str), Long.valueOf(((zzatm.zza)localObject1).zzbMk));
        }
        zzMc().zza(16, "_ev", paramzzatt.name, 0);
        zzMb().setTransactionSuccessful();
        return;
        zzMg().zzNV().zze("Data lost. Currency value is too big. appId", zzaua.zzfH(str), Double.valueOf(d1));
        zzMb().setTransactionSuccessful();
        return;
        l1 = localBundle.getLong("value");
        continue;
        localObject1 = new String((String)localObject1);
        continue;
        l3 = ((Long)((zzauv)localObject2).mValue).longValue();
      }
      finally
      {
        zzMb().endTransaction();
      }
      label797:
      long l3;
      localObject1 = new zzauv(str, paramzzatt.zzaIu, (String)localObject1, zznq().currentTimeMillis(), Long.valueOf(l1 + l3));
    }
    if (bool1)
    {
      l1 = ((zzatm.zza)localObject1).zzbMj - zzMi().zzMQ();
      if (l1 > 0L)
      {
        if (l1 % 1000L == 1L) {
          zzMg().zzNT().zze("Data loss. Too many public events logged. appId, count", zzaua.zzfH(str), Long.valueOf(((zzatm.zza)localObject1).zzbMj));
        }
        zzMc().zza(16, "_ev", paramzzatt.name, 0);
        zzMb().setTransactionSuccessful();
        zzMb().endTransaction();
        return;
      }
    }
    if (bool2)
    {
      l1 = ((zzatm.zza)localObject1).zzbMm - zzMi().zzfm(paramzzatg.packageName);
      if (l1 > 0L)
      {
        if (l1 == 1L) {
          zzMg().zzNT().zze("Too many error events logged. appId, count", zzaua.zzfH(str), Long.valueOf(((zzatm.zza)localObject1).zzbMm));
        }
        zzMb().setTransactionSuccessful();
        zzMb().endTransaction();
        return;
      }
    }
    zzMc().zza(localBundle, "_o", paramzzatt.zzaIu);
    if (zzMc().zzgh(str))
    {
      zzMc().zza(localBundle, "_dbg", Long.valueOf(1L));
      zzMc().zza(localBundle, "_r", Long.valueOf(1L));
    }
    long l1 = zzMb().zzfy(str);
    if (l1 > 0L) {
      zzMg().zzNV().zze("Data lost. Too many events stored on disk, deleted. appId", zzaua.zzfH(str), Long.valueOf(l1));
    }
    paramzzatt = new zzatp(this, paramzzatt.zzaIu, str, paramzzatt.name, paramzzatt.zzbMD, 0L, localBundle);
    Object localObject1 = zzMb().zzaa(str, paramzzatt.mName);
    if (localObject1 == null)
    {
      l1 = zzMb().zzfF(str);
      zzMi().zzMO();
      if (l1 >= 500L)
      {
        zzMg().zzNT().zzd("Too many event names used, ignoring event. appId, name, supported count", zzaua.zzfH(str), paramzzatt.mName, Integer.valueOf(zzMi().zzMO()));
        zzMc().zza(8, null, null, 0);
        zzMb().endTransaction();
        return;
      }
    }
    for (localObject1 = new zzatq(str, paramzzatt.mName, 0L, 0L, paramzzatt.zzaHE);; localObject1 = ((zzatq)localObject1).zzaA(paramzzatt.zzaHE))
    {
      zzMb().zza((zzatq)localObject1);
      zza(paramzzatt, paramzzatg);
      zzMb().setTransactionSuccessful();
      if (zzMg().zzao(2)) {
        zzMg().zzNZ().zzm("Event recorded", paramzzatt);
      }
      zzMb().endTransaction();
      zzOD();
      zzMg().zzNZ().zzm("Background event processing time, ms", Long.valueOf((System.nanoTime() - l2 + 500000L) / 1000000L));
      return;
      paramzzatt = paramzzatt.zza(this, ((zzatq)localObject1).zzbMy);
    }
  }
  
  @WorkerThread
  void zzc(zzaut paramzzaut, zzatg paramzzatg)
  {
    zzmW();
    zznA();
    if (TextUtils.isEmpty(paramzzatg.zzbLI)) {
      return;
    }
    if (!paramzzatg.zzbLN)
    {
      zzf(paramzzatg);
      return;
    }
    zzMg().zzNY().zzm("Removing user property", paramzzaut.name);
    zzMb().beginTransaction();
    try
    {
      zzf(paramzzatg);
      zzMb().zzab(paramzzatg.packageName, paramzzaut.name);
      zzMb().setTransactionSuccessful();
      zzMg().zzNY().zzm("User property removed", paramzzaut.name);
      return;
    }
    finally
    {
      zzMb().endTransaction();
    }
  }
  
  void zzd(zzatg paramzzatg)
  {
    zzmW();
    zznA();
    zzac.zzdc(paramzzatg.packageName);
    zzf(paramzzatg);
  }
  
  @WorkerThread
  void zzd(zzatg paramzzatg, long paramLong)
  {
    zzb(new zzatt("_cd", new zzatr(new Bundle()), "auto", paramLong), paramzzatg);
  }
  
  @WorkerThread
  void zzd(zzatj paramzzatj)
  {
    zzatg localzzatg = zzfR(paramzzatj.packageName);
    if (localzzatg != null) {
      zzb(paramzzatj, localzzatg);
    }
  }
  
  /* Error */
  @WorkerThread
  public void zze(zzatg paramzzatg)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 415	com/google/android/gms/internal/zzauh:zzmW	()V
    //   4: aload_0
    //   5: invokevirtual 418	com/google/android/gms/internal/zzauh:zznA	()V
    //   8: aload_1
    //   9: invokestatic 93	com/google/android/gms/common/internal/zzac:zzC	(Ljava/lang/Object;)Ljava/lang/Object;
    //   12: pop
    //   13: aload_1
    //   14: getfield 679	com/google/android/gms/internal/zzatg:packageName	Ljava/lang/String;
    //   17: invokestatic 655	com/google/android/gms/common/internal/zzac:zzdc	(Ljava/lang/String;)Ljava/lang/String;
    //   20: pop
    //   21: aload_1
    //   22: getfield 703	com/google/android/gms/internal/zzatg:zzbLI	Ljava/lang/String;
    //   25: invokestatic 434	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   28: ifeq +4 -> 32
    //   31: return
    //   32: aload_1
    //   33: getfield 763	com/google/android/gms/internal/zzatg:zzbLN	Z
    //   36: ifne +9 -> 45
    //   39: aload_0
    //   40: aload_1
    //   41: invokespecial 1777	com/google/android/gms/internal/zzauh:zzf	(Lcom/google/android/gms/internal/zzatg;)V
    //   44: return
    //   45: aload_0
    //   46: invokevirtual 447	com/google/android/gms/internal/zzauh:zznq	()Lcom/google/android/gms/common/util/Clock;
    //   49: invokeinterface 523 1 0
    //   54: lstore_2
    //   55: aload_0
    //   56: invokevirtual 422	com/google/android/gms/internal/zzauh:zzMb	()Lcom/google/android/gms/internal/zzatm;
    //   59: invokevirtual 798	com/google/android/gms/internal/zzatm:beginTransaction	()V
    //   62: aload_0
    //   63: aload_1
    //   64: lload_2
    //   65: invokevirtual 2083	com/google/android/gms/internal/zzauh:zza	(Lcom/google/android/gms/internal/zzatg;J)V
    //   68: aload_0
    //   69: aload_1
    //   70: invokespecial 1777	com/google/android/gms/internal/zzauh:zzf	(Lcom/google/android/gms/internal/zzatg;)V
    //   73: aload_0
    //   74: invokevirtual 422	com/google/android/gms/internal/zzauh:zzMb	()Lcom/google/android/gms/internal/zzatm;
    //   77: aload_1
    //   78: getfield 679	com/google/android/gms/internal/zzatg:packageName	Ljava/lang/String;
    //   81: ldc_w 1738
    //   84: invokevirtual 1616	com/google/android/gms/internal/zzatm:zzaa	(Ljava/lang/String;Ljava/lang/String;)Lcom/google/android/gms/internal/zzatq;
    //   87: ifnonnull +63 -> 150
    //   90: aload_0
    //   91: new 1774	com/google/android/gms/internal/zzaut
    //   94: dup
    //   95: ldc_w 2085
    //   98: lload_2
    //   99: lconst_1
    //   100: lload_2
    //   101: ldc2_w 442
    //   104: ldiv
    //   105: ladd
    //   106: ldc2_w 442
    //   109: lmul
    //   110: invokestatic 159	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   113: ldc_w 1422
    //   116: invokespecial 1825	com/google/android/gms/internal/zzaut:<init>	(Ljava/lang/String;JLjava/lang/Object;Ljava/lang/String;)V
    //   119: aload_1
    //   120: invokevirtual 2087	com/google/android/gms/internal/zzauh:zzb	(Lcom/google/android/gms/internal/zzaut;Lcom/google/android/gms/internal/zzatg;)V
    //   123: aload_0
    //   124: aload_1
    //   125: lload_2
    //   126: invokevirtual 2089	com/google/android/gms/internal/zzauh:zzb	(Lcom/google/android/gms/internal/zzatg;J)V
    //   129: aload_0
    //   130: aload_1
    //   131: lload_2
    //   132: invokevirtual 2091	com/google/android/gms/internal/zzauh:zzc	(Lcom/google/android/gms/internal/zzatg;J)V
    //   135: aload_0
    //   136: invokevirtual 422	com/google/android/gms/internal/zzauh:zzMb	()Lcom/google/android/gms/internal/zzatm;
    //   139: invokevirtual 964	com/google/android/gms/internal/zzatm:setTransactionSuccessful	()V
    //   142: aload_0
    //   143: invokevirtual 422	com/google/android/gms/internal/zzauh:zzMb	()Lcom/google/android/gms/internal/zzatm;
    //   146: invokevirtual 967	com/google/android/gms/internal/zzatm:endTransaction	()V
    //   149: return
    //   150: aload_1
    //   151: getfield 2094	com/google/android/gms/internal/zzatg:zzbLO	Z
    //   154: ifeq -19 -> 135
    //   157: aload_0
    //   158: aload_1
    //   159: lload_2
    //   160: invokevirtual 2096	com/google/android/gms/internal/zzauh:zzd	(Lcom/google/android/gms/internal/zzatg;J)V
    //   163: goto -28 -> 135
    //   166: astore_1
    //   167: aload_0
    //   168: invokevirtual 422	com/google/android/gms/internal/zzauh:zzMb	()Lcom/google/android/gms/internal/zzatm;
    //   171: invokevirtual 967	com/google/android/gms/internal/zzatm:endTransaction	()V
    //   174: aload_1
    //   175: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	176	0	this	zzauh
    //   0	176	1	paramzzatg	zzatg
    //   54	106	2	l	long
    // Exception table:
    //   from	to	target	type
    //   62	135	166	finally
    //   135	142	166	finally
    //   150	163	166	finally
  }
  
  @WorkerThread
  void zze(zzatj paramzzatj)
  {
    zzatg localzzatg = zzfR(paramzzatj.packageName);
    if (localzzatg != null) {
      zzc(paramzzatj, localzzatg);
    }
  }
  
  @WorkerThread
  zzatg zzfR(String paramString)
  {
    zzatf localzzatf = zzMb().zzfx(paramString);
    if ((localzzatf == null) || (TextUtils.isEmpty(localzzatf.getAppVersion())))
    {
      zzMg().zzNY().zzm("No app data available; dropping", paramString);
      return null;
    }
    try
    {
      String str = zzaca.zzbp(getContext()).getPackageInfo(paramString, 0).versionName;
      if ((localzzatf.getAppVersion() != null) && (!localzzatf.getAppVersion().equals(str)))
      {
        zzMg().zzNV().zzm("App version does not match; dropping. appId", zzaua.zzfH(paramString));
        return null;
      }
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException) {}
    return new zzatg(paramString, localzzatf.getGmpAppId(), localzzatf.getAppVersion(), localzzatf.zzMo(), localzzatf.zzMp(), localzzatf.zzMq(), localzzatf.zzMr(), null, localzzatf.zzMs(), false, localzzatf.zzMl(), localzzatf.zzwJ());
  }
  
  public String zzfS(final String paramString)
  {
    Object localObject = zzMf().zze(new Callable()
    {
      public String zzcj()
        throws Exception
      {
        zzatf localzzatf = zzauh.this.zzMb().zzfx(paramString);
        if (localzzatf == null) {
          return null;
        }
        return localzzatf.getAppInstanceId();
      }
    });
    try
    {
      localObject = (String)((Future)localObject).get(30000L, TimeUnit.MILLISECONDS);
      return (String)localObject;
    }
    catch (InterruptedException localInterruptedException)
    {
      zzMg().zzNT().zze("Failed to get app instance id. appId", zzaua.zzfH(paramString), localInterruptedException);
      return null;
    }
    catch (ExecutionException localExecutionException)
    {
      for (;;) {}
    }
    catch (TimeoutException localTimeoutException)
    {
      for (;;) {}
    }
  }
  
  @WorkerThread
  public void zzmW()
  {
    zzMf().zzmW();
  }
  
  void zznA()
  {
    if (!this.zzady) {
      throw new IllegalStateException("AppMeasurement is not initialized");
    }
  }
  
  public Clock zznq()
  {
    return this.zzvi;
  }
  
  private class zza
    implements zzatm.zzb
  {
    zzauz.zze zzbPB;
    List<Long> zzbPC;
    long zzbPD;
    List<zzauz.zzb> zztA;
    
    private zza() {}
    
    private long zza(zzauz.zzb paramzzb)
    {
      return paramzzb.zzbRS.longValue() / 1000L / 60L / 60L;
    }
    
    boolean isEmpty()
    {
      return (this.zztA == null) || (this.zztA.isEmpty());
    }
    
    public boolean zza(long paramLong, zzauz.zzb paramzzb)
    {
      zzac.zzC(paramzzb);
      if (this.zztA == null) {
        this.zztA = new ArrayList();
      }
      if (this.zzbPC == null) {
        this.zzbPC = new ArrayList();
      }
      if ((this.zztA.size() > 0) && (zza((zzauz.zzb)this.zztA.get(0)) != zza(paramzzb))) {
        return false;
      }
      long l = this.zzbPD + paramzzb.zzann();
      if (l >= zzauh.this.zzMi().zzNi()) {
        return false;
      }
      this.zzbPD = l;
      this.zztA.add(paramzzb);
      this.zzbPC.add(Long.valueOf(paramLong));
      return this.zztA.size() < zzauh.this.zzMi().zzNj();
    }
    
    public void zzb(zzauz.zze paramzze)
    {
      zzac.zzC(paramzze);
      this.zzbPB = paramzze;
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/internal/zzauh.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */