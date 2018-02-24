package com.google.android.gms.internal;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;
import com.google.android.gms.common.api.Status;

public class zzatx
  extends zzauk
{
  private String mAppId;
  private String zzVF;
  private String zzacB;
  private String zzacC;
  private String zzbLt;
  private long zzbLx;
  private int zzbNr;
  private long zzbNs;
  
  zzatx(zzauh paramzzauh)
  {
    super(paramzzauh);
  }
  
  String getAppId()
  {
    zznA();
    return this.mAppId;
  }
  
  String getAppVersion()
  {
    zznA();
    return this.zzacC;
  }
  
  String getGmpAppId()
  {
    zznA();
    return this.zzVF;
  }
  
  protected void onInitialize()
  {
    Object localObject3 = "unknown";
    String str3 = "Unknown";
    int j = Integer.MIN_VALUE;
    String str1 = "Unknown";
    String str4 = getContext().getPackageName();
    Object localObject6 = getContext().getPackageManager();
    Object localObject5;
    String str2;
    int i;
    Object localObject1;
    if (localObject6 == null)
    {
      zzMg().zzNT().zzm("PackageManager is null, app identity information might be inaccurate. appId", zzaua.zzfH(str4));
      localObject5 = localObject3;
      str2 = str3;
      i = j;
      localObject3 = str1;
      this.mAppId = str4;
      this.zzbLt = ((String)localObject5);
      this.zzacC = str2;
      this.zzbNr = i;
      this.zzacB = ((String)localObject3);
      this.zzbNs = 0L;
      zzMi().zzNb();
      localObject1 = zzzo.zzaW(getContext());
      if ((localObject1 == null) || (!((Status)localObject1).isSuccess())) {
        break label474;
      }
      i = 1;
      label132:
      if (i == 0) {
        zzcF((Status)localObject1);
      }
      if (i == 0) {
        break label575;
      }
      localObject1 = zzMi().zzNd();
      if (!zzMi().zzNc()) {
        break label479;
      }
      zzMg().zzNX().log("Collection disabled with firebase_analytics_collection_deactivated=1");
      i = 0;
    }
    for (;;)
    {
      this.zzVF = "";
      this.zzbLx = 0L;
      zzMi().zzNb();
      try
      {
        localObject3 = zzzo.zzyC();
        localObject1 = localObject3;
        if (TextUtils.isEmpty((CharSequence)localObject3)) {
          localObject1 = "";
        }
        this.zzVF = ((String)localObject1);
        if (i != 0) {
          zzMg().zzNZ().zze("App package, google app id", this.mAppId, this.zzVF);
        }
        return;
      }
      catch (IllegalStateException localIllegalStateException)
      {
        label256:
        label264:
        PackageInfo localPackageInfo;
        Object localObject2;
        Object localObject4;
        zzMg().zzNT().zze("getGoogleAppId or isMeasurementEnabled failed with exception. appId", zzaua.zzfH(str4), localIllegalStateException);
        return;
      }
      try
      {
        localObject1 = ((PackageManager)localObject6).getInstallerPackageName(str4);
        localObject3 = localObject1;
        if (localObject3 == null)
        {
          localObject1 = "manual_install";
          localObject5 = str1;
          str2 = str3;
        }
      }
      catch (IllegalArgumentException localIllegalArgumentException)
      {
        try
        {
          localPackageInfo = ((PackageManager)localObject6).getPackageInfo(getContext().getPackageName(), 0);
          localObject3 = str1;
          i = j;
          str2 = str3;
          localObject5 = localObject1;
          if (localPackageInfo == null) {
            break;
          }
          localObject5 = str1;
          str2 = str3;
          localObject6 = ((PackageManager)localObject6).getApplicationLabel(localPackageInfo.applicationInfo);
          localObject3 = str1;
          localObject5 = str1;
          str2 = str3;
          if (!TextUtils.isEmpty((CharSequence)localObject6))
          {
            localObject5 = str1;
            str2 = str3;
            localObject3 = ((CharSequence)localObject6).toString();
          }
          localObject5 = localObject3;
          str2 = str3;
          str1 = localPackageInfo.versionName;
          localObject5 = localObject3;
          str2 = str1;
          i = localPackageInfo.versionCode;
          str2 = str1;
          localObject5 = localObject1;
        }
        catch (PackageManager.NameNotFoundException localNameNotFoundException)
        {
          zzMg().zzNT().zze("Error retrieving package info. appId, appName", zzaua.zzfH(str4), localObject5);
          localObject4 = localObject5;
          i = j;
          localObject5 = localObject2;
        }
        localIllegalArgumentException = localIllegalArgumentException;
        zzMg().zzNT().zzm("Error retrieving app installer package name. appId", zzaua.zzfH(str4));
        break label256;
        localObject2 = localObject3;
        if (!"com.android.vending".equals(localObject3)) {
          break label264;
        }
        localObject2 = "";
        break label264;
      }
      break;
      label474:
      i = 0;
      break label132;
      label479:
      if ((localObject2 != null) && (!((Boolean)localObject2).booleanValue()))
      {
        zzMg().zzNX().log("Collection disabled with firebase_analytics_collection_enabled=0");
        i = 0;
      }
      else if ((localObject2 == null) && (zzMi().zzyD()))
      {
        zzMg().zzNX().log("Collection disabled with google_app_measurement_enable=0");
        i = 0;
      }
      else
      {
        zzMg().zzNZ().log("Collection enabled");
        i = 1;
        continue;
        label575:
        i = 0;
      }
    }
  }
  
  String zzMp()
  {
    zznA();
    return this.zzbLt;
  }
  
  long zzMq()
  {
    return zzMi().zzMq();
  }
  
  @WorkerThread
  long zzMr()
  {
    zznA();
    zzmW();
    if (this.zzbNs == 0L) {
      this.zzbNs = this.zzbLa.zzMc().zzP(getContext(), getContext().getPackageName());
    }
    return this.zzbNs;
  }
  
  int zzNS()
  {
    zznA();
    return this.zzbNr;
  }
  
  protected void zzcF(Status paramStatus)
  {
    if (paramStatus == null)
    {
      zzMg().zzNT().log("GoogleService failed to initialize (no status)");
      return;
    }
    zzMg().zzNT().zze("GoogleService failed to initialize, status", Integer.valueOf(paramStatus.getStatusCode()), paramStatus.getStatusMessage());
  }
  
  @WorkerThread
  zzatg zzfG(String paramString)
  {
    zzmW();
    String str1 = getAppId();
    String str2 = getGmpAppId();
    String str3 = getAppVersion();
    long l1 = zzNS();
    String str4 = zzMp();
    long l2 = zzMq();
    long l3 = zzMr();
    boolean bool2 = this.zzbLa.isEnabled();
    if (!zzMh().zzbOm) {}
    for (boolean bool1 = true;; bool1 = false) {
      return new zzatg(str1, str2, str3, l1, str4, l2, l3, paramString, bool2, bool1, zzMh().zzMl(), zzwJ());
    }
  }
  
  long zzwJ()
  {
    zznA();
    return 0L;
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/internal/zzatx.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */