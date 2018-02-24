package com.google.android.gms.internal;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Build.VERSION;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class zzato
  extends zzauk
{
  private long zzbMr;
  private String zzbMs;
  private Boolean zzbMt;
  
  zzato(zzauh paramzzauh)
  {
    super(paramzzauh);
  }
  
  protected void onInitialize()
  {
    Object localObject1 = Calendar.getInstance();
    Object localObject2 = TimeUnit.MINUTES;
    int i = ((Calendar)localObject1).get(15);
    this.zzbMr = ((TimeUnit)localObject2).convert(((Calendar)localObject1).get(16) + i, TimeUnit.MILLISECONDS);
    localObject2 = Locale.getDefault();
    localObject1 = String.valueOf(((Locale)localObject2).getLanguage().toLowerCase(Locale.ENGLISH));
    localObject2 = String.valueOf(((Locale)localObject2).getCountry().toLowerCase(Locale.ENGLISH));
    this.zzbMs = (String.valueOf(localObject1).length() + 1 + String.valueOf(localObject2).length() + (String)localObject1 + "-" + (String)localObject2);
  }
  
  public String zzNN()
  {
    zznA();
    return Build.VERSION.RELEASE;
  }
  
  public long zzNO()
  {
    zznA();
    return this.zzbMr;
  }
  
  public String zzNP()
  {
    zznA();
    return this.zzbMs;
  }
  
  public boolean zzbQ(Context paramContext)
  {
    if (this.zzbMt == null)
    {
      zzMi().zzNb();
      this.zzbMt = Boolean.valueOf(false);
    }
    try
    {
      paramContext = paramContext.getPackageManager();
      if (paramContext != null)
      {
        paramContext.getPackageInfo("com.google.android.gms", 128);
        this.zzbMt = Boolean.valueOf(true);
      }
    }
    catch (PackageManager.NameNotFoundException paramContext)
    {
      for (;;) {}
    }
    return this.zzbMt.booleanValue();
  }
  
  public String zzkS()
  {
    zznA();
    return Build.MODEL;
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/internal/zzato.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */