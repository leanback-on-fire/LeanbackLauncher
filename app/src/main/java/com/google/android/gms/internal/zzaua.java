package com.google.android.gms.internal;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.measurement.AppMeasurement;

public class zzaua
  extends zzauk
{
  private final String zzaSt = zzMi().zzMF();
  private final long zzbLu = zzMi().zzMq();
  private final zza zzbNA;
  private final zza zzbNB;
  private final zza zzbNC;
  private final zza zzbND;
  private final zza zzbNE;
  private final zza zzbNF;
  private final char zzbNw;
  private final zza zzbNx;
  private final zza zzbNy;
  private final zza zzbNz;
  
  zzaua(zzauh paramzzauh)
  {
    super(paramzzauh);
    if (zzMi().zzov()) {
      zzMi().zzNb();
    }
    for (this.zzbNw = 'C';; this.zzbNw = 'c')
    {
      this.zzbNx = new zza(6, false, false);
      this.zzbNy = new zza(6, true, false);
      this.zzbNz = new zza(6, false, true);
      this.zzbNA = new zza(5, false, false);
      this.zzbNB = new zza(5, true, false);
      this.zzbNC = new zza(5, false, true);
      this.zzbND = new zza(4, false, false);
      this.zzbNE = new zza(3, false, false);
      this.zzbNF = new zza(2, false, false);
      return;
      zzMi().zzNb();
    }
  }
  
  static String zza(boolean paramBoolean, String paramString, Object paramObject1, Object paramObject2, Object paramObject3)
  {
    String str1 = paramString;
    if (paramString == null) {
      str1 = "";
    }
    String str2 = zzc(paramBoolean, paramObject1);
    paramObject2 = zzc(paramBoolean, paramObject2);
    paramObject3 = zzc(paramBoolean, paramObject3);
    StringBuilder localStringBuilder = new StringBuilder();
    paramString = "";
    if (!TextUtils.isEmpty(str1))
    {
      localStringBuilder.append(str1);
      paramString = ": ";
    }
    paramObject1 = paramString;
    if (!TextUtils.isEmpty(str2))
    {
      localStringBuilder.append(paramString);
      localStringBuilder.append(str2);
      paramObject1 = ", ";
    }
    paramString = (String)paramObject1;
    if (!TextUtils.isEmpty((CharSequence)paramObject2))
    {
      localStringBuilder.append((String)paramObject1);
      localStringBuilder.append((String)paramObject2);
      paramString = ", ";
    }
    if (!TextUtils.isEmpty((CharSequence)paramObject3))
    {
      localStringBuilder.append(paramString);
      localStringBuilder.append((String)paramObject3);
    }
    return localStringBuilder.toString();
  }
  
  static String zzc(boolean paramBoolean, Object paramObject)
  {
    if (paramObject == null) {
      return "";
    }
    if ((paramObject instanceof Integer)) {
      paramObject = Long.valueOf(((Integer)paramObject).intValue());
    }
    for (;;)
    {
      String str1;
      if ((paramObject instanceof Long))
      {
        if (!paramBoolean) {
          return String.valueOf(paramObject);
        }
        if (Math.abs(((Long)paramObject).longValue()) < 100L) {
          return String.valueOf(paramObject);
        }
        if (String.valueOf(paramObject).charAt(0) == '-') {}
        for (str1 = "-";; str1 = "")
        {
          paramObject = String.valueOf(Math.abs(((Long)paramObject).longValue()));
          long l1 = Math.round(Math.pow(10.0D, ((String)paramObject).length() - 1));
          long l2 = Math.round(Math.pow(10.0D, ((String)paramObject).length()) - 1.0D);
          return String.valueOf(str1).length() + 43 + String.valueOf(str1).length() + str1 + l1 + "..." + str1 + l2;
        }
      }
      if ((paramObject instanceof Boolean)) {
        return String.valueOf(paramObject);
      }
      if ((paramObject instanceof Throwable))
      {
        Object localObject1 = (Throwable)paramObject;
        String str2;
        int i;
        label274:
        Object localObject2;
        if (paramBoolean)
        {
          paramObject = localObject1.getClass().getName();
          paramObject = new StringBuilder((String)paramObject);
          str1 = zzfI(AppMeasurement.class.getCanonicalName());
          str2 = zzfI(zzauh.class.getCanonicalName());
          localObject1 = ((Throwable)localObject1).getStackTrace();
          int j = localObject1.length;
          i = 0;
          if (i >= j) {
            break label362;
          }
          localObject2 = localObject1[i];
          if (!((StackTraceElement)localObject2).isNativeMethod()) {
            break label309;
          }
        }
        label309:
        String str3;
        do
        {
          do
          {
            i += 1;
            break label274;
            paramObject = ((Throwable)localObject1).toString();
            break;
            str3 = ((StackTraceElement)localObject2).getClassName();
          } while (str3 == null);
          str3 = zzfI(str3);
        } while ((!str3.equals(str1)) && (!str3.equals(str2)));
        ((StringBuilder)paramObject).append(": ");
        ((StringBuilder)paramObject).append(localObject2);
        label362:
        return ((StringBuilder)paramObject).toString();
      }
      if ((paramObject instanceof zzb)) {
        return zzb.zza((zzb)paramObject);
      }
      if (paramBoolean) {
        return "-";
      }
      return String.valueOf(paramObject);
    }
  }
  
  protected static Object zzfH(String paramString)
  {
    if (paramString == null) {
      return null;
    }
    return new zzb(paramString);
  }
  
  private static String zzfI(String paramString)
  {
    String str;
    if (TextUtils.isEmpty(paramString)) {
      str = "";
    }
    int i;
    do
    {
      return str;
      i = paramString.lastIndexOf('.');
      str = paramString;
    } while (i == -1);
    return paramString.substring(0, i);
  }
  
  protected void onInitialize() {}
  
  public zza zzNT()
  {
    return this.zzbNx;
  }
  
  public zza zzNU()
  {
    return this.zzbNy;
  }
  
  public zza zzNV()
  {
    return this.zzbNA;
  }
  
  public zza zzNW()
  {
    return this.zzbNC;
  }
  
  public zza zzNX()
  {
    return this.zzbND;
  }
  
  public zza zzNY()
  {
    return this.zzbNE;
  }
  
  public zza zzNZ()
  {
    return this.zzbNF;
  }
  
  public String zzOa()
  {
    Object localObject = zzMh().zzbNU.zzpK();
    if ((localObject == null) || (localObject == zzaud.zzbNT)) {
      return null;
    }
    String str = String.valueOf(String.valueOf(((Pair)localObject).second));
    localObject = (String)((Pair)localObject).first;
    return String.valueOf(str).length() + 1 + String.valueOf(localObject).length() + str + ":" + (String)localObject;
  }
  
  protected void zza(int paramInt, boolean paramBoolean1, boolean paramBoolean2, String paramString, Object paramObject1, Object paramObject2, Object paramObject3)
  {
    if ((!paramBoolean1) && (zzao(paramInt))) {
      zzp(paramInt, zza(false, paramString, paramObject1, paramObject2, paramObject3));
    }
    if ((!paramBoolean2) && (paramInt >= 5)) {
      zzb(paramInt, paramString, paramObject1, paramObject2, paramObject3);
    }
  }
  
  protected boolean zzao(int paramInt)
  {
    return Log.isLoggable(this.zzaSt, paramInt);
  }
  
  public void zzb(int paramInt, String paramString, final Object paramObject1, Object paramObject2, Object paramObject3)
  {
    zzac.zzC(paramString);
    zzaug localzzaug = this.zzbLa.zzOq();
    if (localzzaug == null)
    {
      zzp(6, "Scheduler not set. Not logging error/warn");
      return;
    }
    if (!localzzaug.isInitialized())
    {
      zzp(6, "Scheduler not initialized. Not logging error/warn");
      return;
    }
    int i = paramInt;
    if (paramInt < 0) {
      i = 0;
    }
    paramInt = i;
    if (i >= "01VDIWEA?".length()) {
      paramInt = "01VDIWEA?".length() - 1;
    }
    String str = String.valueOf("2");
    char c1 = "01VDIWEA?".charAt(paramInt);
    char c2 = this.zzbNw;
    long l = this.zzbLu;
    paramObject1 = String.valueOf(zza(true, paramString, paramObject1, paramObject2, paramObject3));
    paramObject2 = String.valueOf(str).length() + 23 + String.valueOf(paramObject1).length() + str + c1 + c2 + l + ":" + (String)paramObject1;
    paramObject1 = paramObject2;
    if (((String)paramObject2).length() > 1024) {
      paramObject1 = paramString.substring(0, 1024);
    }
    localzzaug.zzp(new Runnable()
    {
      public void run()
      {
        zzaud localzzaud = zzaua.this.zzbLa.zzMh();
        if (localzzaud.isInitialized())
        {
          localzzaud.zzbNU.zzbE(paramObject1);
          return;
        }
        zzaua.this.zzp(6, "Persisted config not initialized. Not logging error/warn");
      }
    });
  }
  
  protected void zzp(int paramInt, String paramString)
  {
    Log.println(paramInt, this.zzaSt, paramString);
  }
  
  public class zza
  {
    private final int mPriority;
    private final boolean zzbNI;
    private final boolean zzbNJ;
    
    zza(int paramInt, boolean paramBoolean1, boolean paramBoolean2)
    {
      this.mPriority = paramInt;
      this.zzbNI = paramBoolean1;
      this.zzbNJ = paramBoolean2;
    }
    
    public void log(String paramString)
    {
      zzaua.this.zza(this.mPriority, this.zzbNI, this.zzbNJ, paramString, null, null, null);
    }
    
    public void zzd(String paramString, Object paramObject1, Object paramObject2, Object paramObject3)
    {
      zzaua.this.zza(this.mPriority, this.zzbNI, this.zzbNJ, paramString, paramObject1, paramObject2, paramObject3);
    }
    
    public void zze(String paramString, Object paramObject1, Object paramObject2)
    {
      zzaua.this.zza(this.mPriority, this.zzbNI, this.zzbNJ, paramString, paramObject1, paramObject2, null);
    }
    
    public void zzm(String paramString, Object paramObject)
    {
      zzaua.this.zza(this.mPriority, this.zzbNI, this.zzbNJ, paramString, paramObject, null, null);
    }
  }
  
  private static class zzb
  {
    private final String zzbNK;
    
    public zzb(@NonNull String paramString)
    {
      this.zzbNK = paramString;
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/internal/zzaua.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */