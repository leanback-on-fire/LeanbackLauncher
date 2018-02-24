package com.google.android.gms.internal;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.Size;
import android.text.TextUtils;
import com.google.android.gms.common.GoogleApiAvailabilityLight;
import com.google.android.gms.common.internal.zzac;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class zzatl
  extends zzauj
{
  static final String zzbMb = String.valueOf(GoogleApiAvailabilityLight.GOOGLE_PLAY_SERVICES_VERSION_CODE / 1000).replaceAll("(\\d+)(\\d)(\\d\\d)", "$1.$2.$3");
  private Boolean zzaeI;
  
  zzatl(zzauh paramzzauh)
  {
    super(paramzzauh);
  }
  
  String zzMF()
  {
    return (String)zzatv.zzbMK.get();
  }
  
  public int zzMG()
  {
    return 25;
  }
  
  public int zzMH()
  {
    return 40;
  }
  
  public int zzMI()
  {
    return 24;
  }
  
  int zzMJ()
  {
    return 40;
  }
  
  int zzMK()
  {
    return 100;
  }
  
  int zzML()
  {
    return 256;
  }
  
  public int zzMM()
  {
    return 36;
  }
  
  public int zzMN()
  {
    return 2048;
  }
  
  int zzMO()
  {
    return 500;
  }
  
  public long zzMP()
  {
    return ((Integer)zzatv.zzbMU.get()).intValue();
  }
  
  public long zzMQ()
  {
    return ((Integer)zzatv.zzbMW.get()).intValue();
  }
  
  int zzMR()
  {
    return 25;
  }
  
  int zzMS()
  {
    return 1000;
  }
  
  int zzMT()
  {
    return 25;
  }
  
  int zzMU()
  {
    return 1000;
  }
  
  long zzMV()
  {
    return 15552000000L;
  }
  
  long zzMW()
  {
    return 15552000000L;
  }
  
  long zzMX()
  {
    return 3600000L;
  }
  
  long zzMY()
  {
    return 60000L;
  }
  
  long zzMZ()
  {
    return 61000L;
  }
  
  public long zzMq()
  {
    return 10298L;
  }
  
  String zzNa()
  {
    return "google_app_measurement_local.db";
  }
  
  public boolean zzNb()
  {
    return false;
  }
  
  public boolean zzNc()
  {
    Boolean localBoolean = zzfs("firebase_analytics_collection_deactivated");
    return (localBoolean != null) && (localBoolean.booleanValue());
  }
  
  public Boolean zzNd()
  {
    return zzfs("firebase_analytics_collection_enabled");
  }
  
  public long zzNe()
  {
    return ((Long)zzatv.zzbNn.get()).longValue();
  }
  
  public long zzNf()
  {
    return ((Long)zzatv.zzbNi.get()).longValue();
  }
  
  public long zzNg()
  {
    return ((Long)zzatv.zzbNj.get()).longValue();
  }
  
  public long zzNh()
  {
    return 1000L;
  }
  
  public int zzNi()
  {
    return Math.max(0, ((Integer)zzatv.zzbMS.get()).intValue());
  }
  
  public int zzNj()
  {
    return Math.max(1, ((Integer)zzatv.zzbMT.get()).intValue());
  }
  
  public int zzNk()
  {
    return 100000;
  }
  
  public String zzNl()
  {
    return (String)zzatv.zzbNa.get();
  }
  
  public long zzNm()
  {
    return ((Long)zzatv.zzbMN.get()).longValue();
  }
  
  public long zzNn()
  {
    return Math.max(0L, ((Long)zzatv.zzbNb.get()).longValue());
  }
  
  public long zzNo()
  {
    return Math.max(0L, ((Long)zzatv.zzbNd.get()).longValue());
  }
  
  public long zzNp()
  {
    return Math.max(0L, ((Long)zzatv.zzbNe.get()).longValue());
  }
  
  public long zzNq()
  {
    return Math.max(0L, ((Long)zzatv.zzbNf.get()).longValue());
  }
  
  public long zzNr()
  {
    return Math.max(0L, ((Long)zzatv.zzbNg.get()).longValue());
  }
  
  public long zzNs()
  {
    return Math.max(0L, ((Long)zzatv.zzbNh.get()).longValue());
  }
  
  public long zzNt()
  {
    return ((Long)zzatv.zzbNc.get()).longValue();
  }
  
  public long zzNu()
  {
    return Math.max(0L, ((Long)zzatv.zzbNk.get()).longValue());
  }
  
  public long zzNv()
  {
    return Math.max(0L, ((Long)zzatv.zzbNl.get()).longValue());
  }
  
  public int zzNw()
  {
    return Math.min(20, Math.max(0, ((Integer)zzatv.zzbNm.get()).intValue()));
  }
  
  public String zzNx()
  {
    try
    {
      String str = (String)Class.forName("android.os.SystemProperties").getMethod("get", new Class[] { String.class, String.class }).invoke(null, new Object[] { "debug.firebase.analytics.app", "" });
      return str;
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      zzMg().zzNT().zzm("Could not find SystemProperties class", localClassNotFoundException);
      return "";
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      for (;;)
      {
        zzMg().zzNT().zzm("Could not find SystemProperties.get() method", localNoSuchMethodException);
      }
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      for (;;)
      {
        zzMg().zzNT().zzm("Could not access SystemProperties.get()", localIllegalAccessException);
      }
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      for (;;)
      {
        zzMg().zzNT().zzm("SystemProperties.get() threw an exception", localInvocationTargetException);
      }
    }
  }
  
  public String zzZ(String paramString1, String paramString2)
  {
    Uri.Builder localBuilder1 = new Uri.Builder();
    Uri.Builder localBuilder2 = localBuilder1.scheme((String)zzatv.zzbMO.get()).encodedAuthority((String)zzatv.zzbMP.get());
    paramString1 = String.valueOf(paramString1);
    if (paramString1.length() != 0) {}
    for (paramString1 = "config/app/".concat(paramString1);; paramString1 = new String("config/app/"))
    {
      localBuilder2.path(paramString1).appendQueryParameter("app_instance_id", paramString2).appendQueryParameter("platform", "android").appendQueryParameter("gmp_version", String.valueOf(10298L));
      return localBuilder1.build().toString();
    }
  }
  
  public long zza(String paramString, zzatv.zza<Long> paramzza)
  {
    if (paramString == null) {
      return ((Long)paramzza.get()).longValue();
    }
    paramString = zzMd().zzaj(paramString, paramzza.getKey());
    if (TextUtils.isEmpty(paramString)) {
      return ((Long)paramzza.get()).longValue();
    }
    try
    {
      long l = ((Long)paramzza.get(Long.valueOf(Long.valueOf(paramString).longValue()))).longValue();
      return l;
    }
    catch (NumberFormatException paramString) {}
    return ((Long)paramzza.get()).longValue();
  }
  
  public int zzb(String paramString, zzatv.zza<Integer> paramzza)
  {
    if (paramString == null) {
      return ((Integer)paramzza.get()).intValue();
    }
    paramString = zzMd().zzaj(paramString, paramzza.getKey());
    if (TextUtils.isEmpty(paramString)) {
      return ((Integer)paramzza.get()).intValue();
    }
    try
    {
      int i = ((Integer)paramzza.get(Integer.valueOf(Integer.valueOf(paramString).intValue()))).intValue();
      return i;
    }
    catch (NumberFormatException paramString) {}
    return ((Integer)paramzza.get()).intValue();
  }
  
  public int zzfm(@Size(min=1L) String paramString)
  {
    return Math.max(0, Math.min(1000000, zzb(paramString, zzatv.zzbMV)));
  }
  
  public int zzfn(@Size(min=1L) String paramString)
  {
    return zzb(paramString, zzatv.zzbMX);
  }
  
  public int zzfo(@Size(min=1L) String paramString)
  {
    return zzb(paramString, zzatv.zzbMY);
  }
  
  long zzfp(String paramString)
  {
    return zza(paramString, zzatv.zzbML);
  }
  
  int zzfq(String paramString)
  {
    return zzb(paramString, zzatv.zzbNo);
  }
  
  int zzfr(String paramString)
  {
    return Math.max(0, Math.min(2000, zzb(paramString, zzatv.zzbNp)));
  }
  
  @Nullable
  Boolean zzfs(@Size(min=1L) String paramString)
  {
    Boolean localBoolean = null;
    zzac.zzdc(paramString);
    ApplicationInfo localApplicationInfo;
    try
    {
      if (getContext().getPackageManager() == null)
      {
        zzMg().zzNT().log("Failed to load metadata: PackageManager is null");
        return null;
      }
      localApplicationInfo = zzaca.zzbp(getContext()).getApplicationInfo(getContext().getPackageName(), 128);
      if (localApplicationInfo == null)
      {
        zzMg().zzNT().log("Failed to load metadata: ApplicationInfo is null");
        return null;
      }
    }
    catch (PackageManager.NameNotFoundException paramString)
    {
      zzMg().zzNT().zzm("Failed to load metadata: Package name not found", paramString);
      return null;
    }
    if (localApplicationInfo.metaData == null)
    {
      zzMg().zzNT().log("Failed to load metadata: Metadata bundle is null");
      return null;
    }
    if (localApplicationInfo.metaData.containsKey(paramString))
    {
      boolean bool = localApplicationInfo.metaData.getBoolean(paramString);
      localBoolean = Boolean.valueOf(bool);
    }
    return localBoolean;
  }
  
  public int zzft(String paramString)
  {
    return zzb(paramString, zzatv.zzbMQ);
  }
  
  public int zzfu(String paramString)
  {
    return Math.max(0, zzb(paramString, zzatv.zzbMR));
  }
  
  public int zzfv(String paramString)
  {
    return Math.max(0, Math.min(1000000, zzb(paramString, zzatv.zzbMZ)));
  }
  
  long zzoP()
  {
    return ((Long)zzatv.zzbNq.get()).longValue();
  }
  
  public String zzoU()
  {
    return "google_app_measurement.db";
  }
  
  public long zzoY()
  {
    return Math.max(0L, ((Long)zzatv.zzbMM.get()).longValue());
  }
  
  /* Error */
  public boolean zzov()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 565	com/google/android/gms/internal/zzatl:zzaeI	Ljava/lang/Boolean;
    //   4: ifnonnull +84 -> 88
    //   7: aload_0
    //   8: monitorenter
    //   9: aload_0
    //   10: getfield 565	com/google/android/gms/internal/zzatl:zzaeI	Ljava/lang/Boolean;
    //   13: ifnonnull +73 -> 86
    //   16: aload_0
    //   17: invokevirtual 481	com/google/android/gms/internal/zzatl:getContext	()Landroid/content/Context;
    //   20: invokevirtual 568	android/content/Context:getApplicationInfo	()Landroid/content/pm/ApplicationInfo;
    //   23: astore_3
    //   24: invokestatic 573	com/google/android/gms/common/util/zzu:zzBc	()Ljava/lang/String;
    //   27: astore_2
    //   28: aload_3
    //   29: ifnull +30 -> 59
    //   32: aload_3
    //   33: getfield 576	android/content/pm/ApplicationInfo:processName	Ljava/lang/String;
    //   36: astore_3
    //   37: aload_3
    //   38: ifnull +58 -> 96
    //   41: aload_3
    //   42: aload_2
    //   43: invokevirtual 580	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   46: ifeq +50 -> 96
    //   49: iconst_1
    //   50: istore_1
    //   51: aload_0
    //   52: iload_1
    //   53: invokestatic 531	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
    //   56: putfield 565	com/google/android/gms/internal/zzatl:zzaeI	Ljava/lang/Boolean;
    //   59: aload_0
    //   60: getfield 565	com/google/android/gms/internal/zzatl:zzaeI	Ljava/lang/Boolean;
    //   63: ifnonnull +23 -> 86
    //   66: aload_0
    //   67: getstatic 583	java/lang/Boolean:TRUE	Ljava/lang/Boolean;
    //   70: putfield 565	com/google/android/gms/internal/zzatl:zzaeI	Ljava/lang/Boolean;
    //   73: aload_0
    //   74: invokevirtual 318	com/google/android/gms/internal/zzatl:zzMg	()Lcom/google/android/gms/internal/zzaua;
    //   77: invokevirtual 324	com/google/android/gms/internal/zzaua:zzNT	()Lcom/google/android/gms/internal/zzaua$zza;
    //   80: ldc_w 585
    //   83: invokevirtual 492	com/google/android/gms/internal/zzaua$zza:log	(Ljava/lang/String;)V
    //   86: aload_0
    //   87: monitorexit
    //   88: aload_0
    //   89: getfield 565	com/google/android/gms/internal/zzatl:zzaeI	Ljava/lang/Boolean;
    //   92: invokevirtual 189	java/lang/Boolean:booleanValue	()Z
    //   95: ireturn
    //   96: iconst_0
    //   97: istore_1
    //   98: goto -47 -> 51
    //   101: astore_2
    //   102: aload_0
    //   103: monitorexit
    //   104: aload_2
    //   105: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	106	0	this	zzatl
    //   50	48	1	bool	boolean
    //   27	16	2	str	String
    //   101	4	2	localObject1	Object
    //   23	19	3	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   9	28	101	finally
    //   32	37	101	finally
    //   41	49	101	finally
    //   51	59	101	finally
    //   59	86	101	finally
    //   86	88	101	finally
    //   102	104	101	finally
  }
  
  public boolean zzyD()
  {
    return zzzo.zzyD();
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/internal/zzatl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */