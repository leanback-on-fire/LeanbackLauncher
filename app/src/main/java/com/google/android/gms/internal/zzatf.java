package com.google.android.gms.internal;

import android.support.annotation.WorkerThread;
import android.text.TextUtils;
import com.google.android.gms.common.internal.zzac;

class zzatf
{
  private final String mAppId;
  private String zzVF;
  private String zzaVT;
  private String zzacC;
  private long zzbLA;
  private long zzbLB;
  private long zzbLC;
  private long zzbLD;
  private String zzbLE;
  private boolean zzbLF;
  private long zzbLG;
  private long zzbLH;
  private final zzauh zzbLa;
  private String zzbLn;
  private String zzbLo;
  private long zzbLp;
  private long zzbLq;
  private long zzbLr;
  private long zzbLs;
  private String zzbLt;
  private long zzbLu;
  private long zzbLv;
  private boolean zzbLw;
  private long zzbLx;
  private long zzbLy;
  private long zzbLz;
  
  @WorkerThread
  zzatf(zzauh paramzzauh, String paramString)
  {
    zzac.zzC(paramzzauh);
    zzac.zzdc(paramString);
    this.zzbLa = paramzzauh;
    this.mAppId = paramString;
    this.zzbLa.zzmW();
  }
  
  @WorkerThread
  public String getAppId()
  {
    this.zzbLa.zzmW();
    return this.mAppId;
  }
  
  @WorkerThread
  public String getAppInstanceId()
  {
    this.zzbLa.zzmW();
    return this.zzaVT;
  }
  
  @WorkerThread
  public String getAppVersion()
  {
    this.zzbLa.zzmW();
    return this.zzacC;
  }
  
  @WorkerThread
  public String getGmpAppId()
  {
    this.zzbLa.zzmW();
    return this.zzVF;
  }
  
  @WorkerThread
  public void setAppVersion(String paramString)
  {
    this.zzbLa.zzmW();
    boolean bool2 = this.zzbLF;
    if (!zzauw.zzao(this.zzacC, paramString)) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzbLF = (bool1 | bool2);
      this.zzacC = paramString;
      return;
    }
  }
  
  @WorkerThread
  public void setMeasurementEnabled(boolean paramBoolean)
  {
    this.zzbLa.zzmW();
    boolean bool2 = this.zzbLF;
    if (this.zzbLw != paramBoolean) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzbLF = (bool1 | bool2);
      this.zzbLw = paramBoolean;
      return;
    }
  }
  
  @WorkerThread
  public long zzMA()
  {
    this.zzbLa.zzmW();
    return this.zzbLB;
  }
  
  @WorkerThread
  public long zzMB()
  {
    this.zzbLa.zzmW();
    return this.zzbLD;
  }
  
  @WorkerThread
  public long zzMC()
  {
    this.zzbLa.zzmW();
    return this.zzbLC;
  }
  
  @WorkerThread
  public String zzMD()
  {
    this.zzbLa.zzmW();
    return this.zzbLE;
  }
  
  @WorkerThread
  public String zzME()
  {
    this.zzbLa.zzmW();
    String str = this.zzbLE;
    zzfl(null);
    return str;
  }
  
  @WorkerThread
  public void zzMj()
  {
    this.zzbLa.zzmW();
    this.zzbLF = false;
  }
  
  @WorkerThread
  public String zzMk()
  {
    this.zzbLa.zzmW();
    return this.zzbLn;
  }
  
  @WorkerThread
  public String zzMl()
  {
    this.zzbLa.zzmW();
    return this.zzbLo;
  }
  
  @WorkerThread
  public long zzMm()
  {
    this.zzbLa.zzmW();
    return this.zzbLq;
  }
  
  @WorkerThread
  public long zzMn()
  {
    this.zzbLa.zzmW();
    return this.zzbLr;
  }
  
  @WorkerThread
  public long zzMo()
  {
    this.zzbLa.zzmW();
    return this.zzbLs;
  }
  
  @WorkerThread
  public String zzMp()
  {
    this.zzbLa.zzmW();
    return this.zzbLt;
  }
  
  @WorkerThread
  public long zzMq()
  {
    this.zzbLa.zzmW();
    return this.zzbLu;
  }
  
  @WorkerThread
  public long zzMr()
  {
    this.zzbLa.zzmW();
    return this.zzbLv;
  }
  
  @WorkerThread
  public boolean zzMs()
  {
    this.zzbLa.zzmW();
    return this.zzbLw;
  }
  
  @WorkerThread
  public long zzMt()
  {
    this.zzbLa.zzmW();
    return this.zzbLp;
  }
  
  @WorkerThread
  public long zzMu()
  {
    this.zzbLa.zzmW();
    return this.zzbLG;
  }
  
  @WorkerThread
  public long zzMv()
  {
    this.zzbLa.zzmW();
    return this.zzbLH;
  }
  
  @WorkerThread
  public void zzMw()
  {
    this.zzbLa.zzmW();
    long l2 = this.zzbLp + 1L;
    long l1 = l2;
    if (l2 > 2147483647L)
    {
      this.zzbLa.zzMg().zzNV().zzm("Bundle index overflow. appId", zzaua.zzfH(this.mAppId));
      l1 = 0L;
    }
    this.zzbLF = true;
    this.zzbLp = l1;
  }
  
  @WorkerThread
  public long zzMx()
  {
    this.zzbLa.zzmW();
    return this.zzbLy;
  }
  
  @WorkerThread
  public long zzMy()
  {
    this.zzbLa.zzmW();
    return this.zzbLz;
  }
  
  @WorkerThread
  public long zzMz()
  {
    this.zzbLa.zzmW();
    return this.zzbLA;
  }
  
  @WorkerThread
  public void zzaj(long paramLong)
  {
    this.zzbLa.zzmW();
    boolean bool2 = this.zzbLF;
    if (this.zzbLq != paramLong) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzbLF = (bool1 | bool2);
      this.zzbLq = paramLong;
      return;
    }
  }
  
  @WorkerThread
  public void zzak(long paramLong)
  {
    this.zzbLa.zzmW();
    boolean bool2 = this.zzbLF;
    if (this.zzbLr != paramLong) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzbLF = (bool1 | bool2);
      this.zzbLr = paramLong;
      return;
    }
  }
  
  @WorkerThread
  public void zzal(long paramLong)
  {
    this.zzbLa.zzmW();
    boolean bool2 = this.zzbLF;
    if (this.zzbLs != paramLong) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzbLF = (bool1 | bool2);
      this.zzbLs = paramLong;
      return;
    }
  }
  
  @WorkerThread
  public void zzam(long paramLong)
  {
    this.zzbLa.zzmW();
    boolean bool2 = this.zzbLF;
    if (this.zzbLu != paramLong) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzbLF = (bool1 | bool2);
      this.zzbLu = paramLong;
      return;
    }
  }
  
  @WorkerThread
  public void zzan(long paramLong)
  {
    this.zzbLa.zzmW();
    boolean bool2 = this.zzbLF;
    if (this.zzbLv != paramLong) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzbLF = (bool1 | bool2);
      this.zzbLv = paramLong;
      return;
    }
  }
  
  @WorkerThread
  public void zzao(long paramLong)
  {
    boolean bool1 = true;
    boolean bool2;
    if (paramLong >= 0L)
    {
      bool2 = true;
      zzac.zzaw(bool2);
      this.zzbLa.zzmW();
      bool2 = this.zzbLF;
      if (this.zzbLp == paramLong) {
        break label58;
      }
    }
    for (;;)
    {
      this.zzbLF = (bool2 | bool1);
      this.zzbLp = paramLong;
      return;
      bool2 = false;
      break;
      label58:
      bool1 = false;
    }
  }
  
  @WorkerThread
  public void zzap(long paramLong)
  {
    this.zzbLa.zzmW();
    boolean bool2 = this.zzbLF;
    if (this.zzbLG != paramLong) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzbLF = (bool1 | bool2);
      this.zzbLG = paramLong;
      return;
    }
  }
  
  @WorkerThread
  public void zzaq(long paramLong)
  {
    this.zzbLa.zzmW();
    boolean bool2 = this.zzbLF;
    if (this.zzbLH != paramLong) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzbLF = (bool1 | bool2);
      this.zzbLH = paramLong;
      return;
    }
  }
  
  @WorkerThread
  public void zzar(long paramLong)
  {
    this.zzbLa.zzmW();
    boolean bool2 = this.zzbLF;
    if (this.zzbLy != paramLong) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzbLF = (bool1 | bool2);
      this.zzbLy = paramLong;
      return;
    }
  }
  
  @WorkerThread
  public void zzas(long paramLong)
  {
    this.zzbLa.zzmW();
    boolean bool2 = this.zzbLF;
    if (this.zzbLz != paramLong) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzbLF = (bool1 | bool2);
      this.zzbLz = paramLong;
      return;
    }
  }
  
  @WorkerThread
  public void zzat(long paramLong)
  {
    this.zzbLa.zzmW();
    boolean bool2 = this.zzbLF;
    if (this.zzbLA != paramLong) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzbLF = (bool1 | bool2);
      this.zzbLA = paramLong;
      return;
    }
  }
  
  @WorkerThread
  public void zzau(long paramLong)
  {
    this.zzbLa.zzmW();
    boolean bool2 = this.zzbLF;
    if (this.zzbLB != paramLong) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzbLF = (bool1 | bool2);
      this.zzbLB = paramLong;
      return;
    }
  }
  
  @WorkerThread
  public void zzav(long paramLong)
  {
    this.zzbLa.zzmW();
    boolean bool2 = this.zzbLF;
    if (this.zzbLD != paramLong) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzbLF = (bool1 | bool2);
      this.zzbLD = paramLong;
      return;
    }
  }
  
  @WorkerThread
  public void zzaw(long paramLong)
  {
    this.zzbLa.zzmW();
    boolean bool2 = this.zzbLF;
    if (this.zzbLC != paramLong) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzbLF = (bool1 | bool2);
      this.zzbLC = paramLong;
      return;
    }
  }
  
  @WorkerThread
  public void zzax(long paramLong)
  {
    this.zzbLa.zzmW();
    boolean bool2 = this.zzbLF;
    if (this.zzbLx != paramLong) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzbLF = (bool1 | bool2);
      this.zzbLx = paramLong;
      return;
    }
  }
  
  @WorkerThread
  public void zzfg(String paramString)
  {
    this.zzbLa.zzmW();
    boolean bool2 = this.zzbLF;
    if (!zzauw.zzao(this.zzaVT, paramString)) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzbLF = (bool1 | bool2);
      this.zzaVT = paramString;
      return;
    }
  }
  
  @WorkerThread
  public void zzfh(String paramString)
  {
    this.zzbLa.zzmW();
    String str = paramString;
    if (TextUtils.isEmpty(paramString)) {
      str = null;
    }
    boolean bool2 = this.zzbLF;
    if (!zzauw.zzao(this.zzVF, str)) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzbLF = (bool1 | bool2);
      this.zzVF = str;
      return;
    }
  }
  
  @WorkerThread
  public void zzfi(String paramString)
  {
    this.zzbLa.zzmW();
    boolean bool2 = this.zzbLF;
    if (!zzauw.zzao(this.zzbLn, paramString)) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzbLF = (bool1 | bool2);
      this.zzbLn = paramString;
      return;
    }
  }
  
  @WorkerThread
  public void zzfj(String paramString)
  {
    this.zzbLa.zzmW();
    boolean bool2 = this.zzbLF;
    if (!zzauw.zzao(this.zzbLo, paramString)) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzbLF = (bool1 | bool2);
      this.zzbLo = paramString;
      return;
    }
  }
  
  @WorkerThread
  public void zzfk(String paramString)
  {
    this.zzbLa.zzmW();
    boolean bool2 = this.zzbLF;
    if (!zzauw.zzao(this.zzbLt, paramString)) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzbLF = (bool1 | bool2);
      this.zzbLt = paramString;
      return;
    }
  }
  
  @WorkerThread
  public void zzfl(String paramString)
  {
    this.zzbLa.zzmW();
    boolean bool2 = this.zzbLF;
    if (!zzauw.zzao(this.zzbLE, paramString)) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzbLF = (bool1 | bool2);
      this.zzbLE = paramString;
      return;
    }
  }
  
  @WorkerThread
  public long zzwJ()
  {
    this.zzbLa.zzmW();
    return this.zzbLx;
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/internal/zzatf.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */