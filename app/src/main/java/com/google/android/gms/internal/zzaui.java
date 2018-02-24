package com.google.android.gms.internal;

import android.content.Context;
import android.os.Binder;
import android.support.annotation.BinderThread;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.google.android.gms.common.GooglePlayServicesUtilLight;
import com.google.android.gms.common.GoogleSignatureVerifier;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.util.Clock;
import com.google.android.gms.common.util.zzy;
import com.google.android.gms.measurement.AppMeasurement.zzd;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class zzaui
  extends zzatw.zza
{
  private final zzauh zzbLa;
  private Boolean zzbPE;
  @Nullable
  private String zzbPF;
  
  public zzaui(zzauh paramzzauh)
  {
    this(paramzzauh, null);
  }
  
  public zzaui(zzauh paramzzauh, @Nullable String paramString)
  {
    zzac.zzC(paramzzauh);
    this.zzbLa = paramzzauh;
    this.zzbPF = paramString;
  }
  
  @BinderThread
  private void zzb(zzatg paramzzatg, boolean paramBoolean)
  {
    zzac.zzC(paramzzatg);
    zzn(paramzzatg.packageName, paramBoolean);
    this.zzbLa.zzMc().zzgd(paramzzatg.zzbLI);
  }
  
  @BinderThread
  private void zzn(String paramString, boolean paramBoolean)
  {
    if (TextUtils.isEmpty(paramString))
    {
      this.zzbLa.zzMg().zzNT().log("Measurement Service called without app package");
      throw new SecurityException("Measurement Service called without app package");
    }
    try
    {
      zzo(paramString, paramBoolean);
      return;
    }
    catch (SecurityException localSecurityException)
    {
      this.zzbLa.zzMg().zzNT().zzm("Measurement Service called with invalid calling package. appId", zzaua.zzfH(paramString));
      throw localSecurityException;
    }
  }
  
  /* Error */
  @BinderThread
  public List<zzaut> zza(final zzatg paramzzatg, boolean paramBoolean)
  {
    // Byte code:
    //   0: aload_0
    //   1: aload_1
    //   2: iconst_0
    //   3: invokespecial 143	com/google/android/gms/internal/zzaui:zzb	(Lcom/google/android/gms/internal/zzatg;Z)V
    //   6: aload_0
    //   7: getfield 62	com/google/android/gms/internal/zzaui:zzbLa	Lcom/google/android/gms/internal/zzauh;
    //   10: invokevirtual 147	com/google/android/gms/internal/zzauh:zzMf	()Lcom/google/android/gms/internal/zzaug;
    //   13: new 18	com/google/android/gms/internal/zzaui$15
    //   16: dup
    //   17: aload_0
    //   18: aload_1
    //   19: invokespecial 150	com/google/android/gms/internal/zzaui$15:<init>	(Lcom/google/android/gms/internal/zzaui;Lcom/google/android/gms/internal/zzatg;)V
    //   22: invokevirtual 156	com/google/android/gms/internal/zzaug:zze	(Ljava/util/concurrent/Callable;)Ljava/util/concurrent/Future;
    //   25: astore_3
    //   26: aload_3
    //   27: invokeinterface 162 1 0
    //   32: checkcast 164	java/util/List
    //   35: astore 4
    //   37: new 166	java/util/ArrayList
    //   40: dup
    //   41: aload 4
    //   43: invokeinterface 170 1 0
    //   48: invokespecial 173	java/util/ArrayList:<init>	(I)V
    //   51: astore_3
    //   52: aload 4
    //   54: invokeinterface 177 1 0
    //   59: astore 4
    //   61: aload 4
    //   63: invokeinterface 183 1 0
    //   68: ifeq +75 -> 143
    //   71: aload 4
    //   73: invokeinterface 186 1 0
    //   78: checkcast 188	com/google/android/gms/internal/zzauv
    //   81: astore 5
    //   83: iload_2
    //   84: ifne +14 -> 98
    //   87: aload 5
    //   89: getfield 191	com/google/android/gms/internal/zzauv:mName	Ljava/lang/String;
    //   92: invokestatic 194	com/google/android/gms/internal/zzauw:zzgg	(Ljava/lang/String;)Z
    //   95: ifne -34 -> 61
    //   98: aload_3
    //   99: new 196	com/google/android/gms/internal/zzaut
    //   102: dup
    //   103: aload 5
    //   105: invokespecial 199	com/google/android/gms/internal/zzaut:<init>	(Lcom/google/android/gms/internal/zzauv;)V
    //   108: invokeinterface 203 2 0
    //   113: pop
    //   114: goto -53 -> 61
    //   117: astore_3
    //   118: aload_0
    //   119: getfield 62	com/google/android/gms/internal/zzaui:zzbLa	Lcom/google/android/gms/internal/zzauh;
    //   122: invokevirtual 107	com/google/android/gms/internal/zzauh:zzMg	()Lcom/google/android/gms/internal/zzaua;
    //   125: invokevirtual 113	com/google/android/gms/internal/zzaua:zzNT	()Lcom/google/android/gms/internal/zzaua$zza;
    //   128: ldc -51
    //   130: aload_1
    //   131: getfield 75	com/google/android/gms/internal/zzatg:packageName	Ljava/lang/String;
    //   134: invokestatic 132	com/google/android/gms/internal/zzaua:zzfH	(Ljava/lang/String;)Ljava/lang/Object;
    //   137: aload_3
    //   138: invokevirtual 208	com/google/android/gms/internal/zzaua$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   141: aconst_null
    //   142: areturn
    //   143: aload_3
    //   144: areturn
    //   145: astore_3
    //   146: goto -28 -> 118
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	149	0	this	zzaui
    //   0	149	1	paramzzatg	zzatg
    //   0	149	2	paramBoolean	boolean
    //   25	74	3	localObject1	Object
    //   117	27	3	localInterruptedException	InterruptedException
    //   145	1	3	localExecutionException	ExecutionException
    //   35	37	4	localObject2	Object
    //   81	23	5	localzzauv	zzauv
    // Exception table:
    //   from	to	target	type
    //   26	61	117	java/lang/InterruptedException
    //   61	83	117	java/lang/InterruptedException
    //   87	98	117	java/lang/InterruptedException
    //   98	114	117	java/lang/InterruptedException
    //   26	61	145	java/util/concurrent/ExecutionException
    //   61	83	145	java/util/concurrent/ExecutionException
    //   87	98	145	java/util/concurrent/ExecutionException
    //   98	114	145	java/util/concurrent/ExecutionException
  }
  
  @BinderThread
  public List<zzatj> zza(final String paramString1, final String paramString2, final zzatg paramzzatg)
  {
    zzb(paramzzatg, false);
    paramString1 = this.zzbLa.zzMf().zze(new Callable()
    {
      public List<zzatj> zzOI()
        throws Exception
      {
        zzaui.zza(zzaui.this).zzOG();
        return zzaui.zza(zzaui.this).zzMb().zzj(paramzzatg.packageName, paramString1, paramString2);
      }
    });
    try
    {
      paramString1 = (List)paramString1.get();
      return paramString1;
    }
    catch (InterruptedException paramString1)
    {
      this.zzbLa.zzMg().zzNT().zzm("Failed to get conditional user properties", paramString1);
      return Collections.emptyList();
    }
    catch (ExecutionException paramString1)
    {
      for (;;) {}
    }
  }
  
  /* Error */
  @BinderThread
  public List<zzaut> zza(final String paramString1, final String paramString2, final String paramString3, boolean paramBoolean)
  {
    // Byte code:
    //   0: aload_0
    //   1: aload_1
    //   2: iconst_1
    //   3: invokespecial 79	com/google/android/gms/internal/zzaui:zzn	(Ljava/lang/String;Z)V
    //   6: aload_0
    //   7: getfield 62	com/google/android/gms/internal/zzaui:zzbLa	Lcom/google/android/gms/internal/zzauh;
    //   10: invokevirtual 147	com/google/android/gms/internal/zzauh:zzMf	()Lcom/google/android/gms/internal/zzaug;
    //   13: new 34	com/google/android/gms/internal/zzaui$7
    //   16: dup
    //   17: aload_0
    //   18: aload_1
    //   19: aload_2
    //   20: aload_3
    //   21: invokespecial 227	com/google/android/gms/internal/zzaui$7:<init>	(Lcom/google/android/gms/internal/zzaui;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V
    //   24: invokevirtual 156	com/google/android/gms/internal/zzaug:zze	(Ljava/util/concurrent/Callable;)Ljava/util/concurrent/Future;
    //   27: astore_2
    //   28: aload_2
    //   29: invokeinterface 162 1 0
    //   34: checkcast 164	java/util/List
    //   37: astore_3
    //   38: new 166	java/util/ArrayList
    //   41: dup
    //   42: aload_3
    //   43: invokeinterface 170 1 0
    //   48: invokespecial 173	java/util/ArrayList:<init>	(I)V
    //   51: astore_2
    //   52: aload_3
    //   53: invokeinterface 177 1 0
    //   58: astore_3
    //   59: aload_3
    //   60: invokeinterface 183 1 0
    //   65: ifeq +74 -> 139
    //   68: aload_3
    //   69: invokeinterface 186 1 0
    //   74: checkcast 188	com/google/android/gms/internal/zzauv
    //   77: astore 5
    //   79: iload 4
    //   81: ifne +14 -> 95
    //   84: aload 5
    //   86: getfield 191	com/google/android/gms/internal/zzauv:mName	Ljava/lang/String;
    //   89: invokestatic 194	com/google/android/gms/internal/zzauw:zzgg	(Ljava/lang/String;)Z
    //   92: ifne -33 -> 59
    //   95: aload_2
    //   96: new 196	com/google/android/gms/internal/zzaut
    //   99: dup
    //   100: aload 5
    //   102: invokespecial 199	com/google/android/gms/internal/zzaut:<init>	(Lcom/google/android/gms/internal/zzauv;)V
    //   105: invokeinterface 203 2 0
    //   110: pop
    //   111: goto -52 -> 59
    //   114: astore_2
    //   115: aload_0
    //   116: getfield 62	com/google/android/gms/internal/zzaui:zzbLa	Lcom/google/android/gms/internal/zzauh;
    //   119: invokevirtual 107	com/google/android/gms/internal/zzauh:zzMg	()Lcom/google/android/gms/internal/zzaua;
    //   122: invokevirtual 113	com/google/android/gms/internal/zzaua:zzNT	()Lcom/google/android/gms/internal/zzaua$zza;
    //   125: ldc -51
    //   127: aload_1
    //   128: invokestatic 132	com/google/android/gms/internal/zzaua:zzfH	(Ljava/lang/String;)Ljava/lang/Object;
    //   131: aload_2
    //   132: invokevirtual 208	com/google/android/gms/internal/zzaua$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   135: invokestatic 222	java/util/Collections:emptyList	()Ljava/util/List;
    //   138: areturn
    //   139: aload_2
    //   140: areturn
    //   141: astore_2
    //   142: goto -27 -> 115
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	145	0	this	zzaui
    //   0	145	1	paramString1	String
    //   0	145	2	paramString2	String
    //   0	145	3	paramString3	String
    //   0	145	4	paramBoolean	boolean
    //   77	24	5	localzzauv	zzauv
    // Exception table:
    //   from	to	target	type
    //   28	59	114	java/lang/InterruptedException
    //   59	79	114	java/lang/InterruptedException
    //   84	95	114	java/lang/InterruptedException
    //   95	111	114	java/lang/InterruptedException
    //   28	59	141	java/util/concurrent/ExecutionException
    //   59	79	141	java/util/concurrent/ExecutionException
    //   84	95	141	java/util/concurrent/ExecutionException
    //   95	111	141	java/util/concurrent/ExecutionException
  }
  
  /* Error */
  @BinderThread
  public List<zzaut> zza(final String paramString1, final String paramString2, boolean paramBoolean, final zzatg paramzzatg)
  {
    // Byte code:
    //   0: aload_0
    //   1: aload 4
    //   3: iconst_0
    //   4: invokespecial 143	com/google/android/gms/internal/zzaui:zzb	(Lcom/google/android/gms/internal/zzatg;Z)V
    //   7: aload_0
    //   8: getfield 62	com/google/android/gms/internal/zzaui:zzbLa	Lcom/google/android/gms/internal/zzauh;
    //   11: invokevirtual 147	com/google/android/gms/internal/zzauh:zzMf	()Lcom/google/android/gms/internal/zzaug;
    //   14: new 32	com/google/android/gms/internal/zzaui$6
    //   17: dup
    //   18: aload_0
    //   19: aload 4
    //   21: aload_1
    //   22: aload_2
    //   23: invokespecial 230	com/google/android/gms/internal/zzaui$6:<init>	(Lcom/google/android/gms/internal/zzaui;Lcom/google/android/gms/internal/zzatg;Ljava/lang/String;Ljava/lang/String;)V
    //   26: invokevirtual 156	com/google/android/gms/internal/zzaug:zze	(Ljava/util/concurrent/Callable;)Ljava/util/concurrent/Future;
    //   29: astore_1
    //   30: aload_1
    //   31: invokeinterface 162 1 0
    //   36: checkcast 164	java/util/List
    //   39: astore_2
    //   40: new 166	java/util/ArrayList
    //   43: dup
    //   44: aload_2
    //   45: invokeinterface 170 1 0
    //   50: invokespecial 173	java/util/ArrayList:<init>	(I)V
    //   53: astore_1
    //   54: aload_2
    //   55: invokeinterface 177 1 0
    //   60: astore_2
    //   61: aload_2
    //   62: invokeinterface 183 1 0
    //   67: ifeq +77 -> 144
    //   70: aload_2
    //   71: invokeinterface 186 1 0
    //   76: checkcast 188	com/google/android/gms/internal/zzauv
    //   79: astore 5
    //   81: iload_3
    //   82: ifne +14 -> 96
    //   85: aload 5
    //   87: getfield 191	com/google/android/gms/internal/zzauv:mName	Ljava/lang/String;
    //   90: invokestatic 194	com/google/android/gms/internal/zzauw:zzgg	(Ljava/lang/String;)Z
    //   93: ifne -32 -> 61
    //   96: aload_1
    //   97: new 196	com/google/android/gms/internal/zzaut
    //   100: dup
    //   101: aload 5
    //   103: invokespecial 199	com/google/android/gms/internal/zzaut:<init>	(Lcom/google/android/gms/internal/zzauv;)V
    //   106: invokeinterface 203 2 0
    //   111: pop
    //   112: goto -51 -> 61
    //   115: astore_1
    //   116: aload_0
    //   117: getfield 62	com/google/android/gms/internal/zzaui:zzbLa	Lcom/google/android/gms/internal/zzauh;
    //   120: invokevirtual 107	com/google/android/gms/internal/zzauh:zzMg	()Lcom/google/android/gms/internal/zzaua;
    //   123: invokevirtual 113	com/google/android/gms/internal/zzaua:zzNT	()Lcom/google/android/gms/internal/zzaua$zza;
    //   126: ldc -51
    //   128: aload 4
    //   130: getfield 75	com/google/android/gms/internal/zzatg:packageName	Ljava/lang/String;
    //   133: invokestatic 132	com/google/android/gms/internal/zzaua:zzfH	(Ljava/lang/String;)Ljava/lang/Object;
    //   136: aload_1
    //   137: invokevirtual 208	com/google/android/gms/internal/zzaua$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   140: invokestatic 222	java/util/Collections:emptyList	()Ljava/util/List;
    //   143: areturn
    //   144: aload_1
    //   145: areturn
    //   146: astore_1
    //   147: goto -31 -> 116
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	150	0	this	zzaui
    //   0	150	1	paramString1	String
    //   0	150	2	paramString2	String
    //   0	150	3	paramBoolean	boolean
    //   0	150	4	paramzzatg	zzatg
    //   79	23	5	localzzauv	zzauv
    // Exception table:
    //   from	to	target	type
    //   30	61	115	java/lang/InterruptedException
    //   61	81	115	java/lang/InterruptedException
    //   85	96	115	java/lang/InterruptedException
    //   96	112	115	java/lang/InterruptedException
    //   30	61	146	java/util/concurrent/ExecutionException
    //   61	81	146	java/util/concurrent/ExecutionException
    //   85	96	146	java/util/concurrent/ExecutionException
    //   96	112	146	java/util/concurrent/ExecutionException
  }
  
  @BinderThread
  public void zza(final long paramLong, final String paramString1, final String paramString2, final String paramString3)
  {
    this.zzbLa.zzMf().zzp(new Runnable()
    {
      public void run()
      {
        if (paramString2 == null)
        {
          zzaui.zza(zzaui.this).zzLZ().zza(paramString3, null);
          return;
        }
        AppMeasurement.zzd localzzd = new AppMeasurement.zzd();
        localzzd.zzbLc = paramString1;
        localzzd.zzbLd = paramString2;
        localzzd.zzbLe = paramLong;
        zzaui.zza(zzaui.this).zzLZ().zza(paramString3, localzzd);
      }
    });
  }
  
  @BinderThread
  public void zza(final zzatg paramzzatg)
  {
    zzb(paramzzatg, false);
    this.zzbLa.zzMf().zzp(new Runnable()
    {
      public void run()
      {
        zzaui.zza(zzaui.this).zzOG();
        zzaui.zza(zzaui.this).zze(paramzzatg);
      }
    });
  }
  
  @BinderThread
  public void zza(zzatj paramzzatj, final zzatg paramzzatg)
  {
    zzac.zzC(paramzzatj);
    zzac.zzC(paramzzatj.zzbLS);
    zzb(paramzzatg, false);
    final zzatj localzzatj = new zzatj(paramzzatj);
    localzzatj.packageName = paramzzatg.packageName;
    if (paramzzatj.zzbLS.getValue() == null)
    {
      this.zzbLa.zzMf().zzp(new Runnable()
      {
        public void run()
        {
          zzaui.zza(zzaui.this).zzOG();
          zzaui.zza(zzaui.this).zzc(localzzatj, paramzzatg);
        }
      });
      return;
    }
    this.zzbLa.zzMf().zzp(new Runnable()
    {
      public void run()
      {
        zzaui.zza(zzaui.this).zzOG();
        zzaui.zza(zzaui.this).zzb(localzzatj, paramzzatg);
      }
    });
  }
  
  @BinderThread
  public void zza(final zzatt paramzzatt, final zzatg paramzzatg)
  {
    zzac.zzC(paramzzatt);
    zzb(paramzzatg, false);
    this.zzbLa.zzMf().zzp(new Runnable()
    {
      public void run()
      {
        zzaui.zza(zzaui.this).zzOG();
        zzaui.zza(zzaui.this).zzb(paramzzatt, paramzzatg);
      }
    });
  }
  
  @BinderThread
  public void zza(final zzatt paramzzatt, final String paramString1, String paramString2)
  {
    zzac.zzC(paramzzatt);
    zzac.zzdc(paramString1);
    zzn(paramString1, true);
    this.zzbLa.zzMf().zzp(new Runnable()
    {
      public void run()
      {
        zzaui.zza(zzaui.this).zzOG();
        zzaui.zza(zzaui.this).zzb(paramzzatt, paramString1);
      }
    });
  }
  
  @BinderThread
  public void zza(final zzaut paramzzaut, final zzatg paramzzatg)
  {
    zzac.zzC(paramzzaut);
    zzb(paramzzatg, false);
    if (paramzzaut.getValue() == null)
    {
      this.zzbLa.zzMf().zzp(new Runnable()
      {
        public void run()
        {
          zzaui.zza(zzaui.this).zzOG();
          zzaui.zza(zzaui.this).zzc(paramzzaut, paramzzatg);
        }
      });
      return;
    }
    this.zzbLa.zzMf().zzp(new Runnable()
    {
      public void run()
      {
        zzaui.zza(zzaui.this).zzOG();
        zzaui.zza(zzaui.this).zzb(paramzzaut, paramzzatg);
      }
    });
  }
  
  @BinderThread
  public byte[] zza(final zzatt paramzzatt, final String paramString)
  {
    zzac.zzdc(paramString);
    zzac.zzC(paramzzatt);
    zzn(paramString, true);
    this.zzbLa.zzMg().zzNY().zzm("Log and bundle. event", paramzzatt.name);
    long l1 = this.zzbLa.zznq().nanoTime() / 1000000L;
    Object localObject = this.zzbLa.zzMf().zzf(new Callable()
    {
      public byte[] zzOJ()
        throws Exception
      {
        zzaui.zza(zzaui.this).zzOG();
        return zzaui.zza(zzaui.this).zza(paramzzatt, paramString);
      }
    });
    try
    {
      byte[] arrayOfByte = (byte[])((Future)localObject).get();
      localObject = arrayOfByte;
      if (arrayOfByte == null)
      {
        this.zzbLa.zzMg().zzNT().zzm("Log and bundle returned null. appId", zzaua.zzfH(paramString));
        localObject = new byte[0];
      }
      long l2 = this.zzbLa.zznq().nanoTime() / 1000000L;
      this.zzbLa.zzMg().zzNY().zzd("Log and bundle processed. event, size, time_ms", paramzzatt.name, Integer.valueOf(localObject.length), Long.valueOf(l2 - l1));
      return (byte[])localObject;
    }
    catch (InterruptedException localInterruptedException)
    {
      this.zzbLa.zzMg().zzNT().zzd("Failed to log and bundle. appId, event, error", zzaua.zzfH(paramString), paramzzatt.name, localInterruptedException);
      return null;
    }
    catch (ExecutionException localExecutionException)
    {
      for (;;) {}
    }
  }
  
  @BinderThread
  public void zzb(final zzatg paramzzatg)
  {
    zzb(paramzzatg, false);
    this.zzbLa.zzMf().zzp(new Runnable()
    {
      public void run()
      {
        zzaui.zza(zzaui.this).zzOG();
        zzaui.zza(zzaui.this).zzd(paramzzatg);
      }
    });
  }
  
  @BinderThread
  public void zzb(zzatj paramzzatj)
  {
    zzac.zzC(paramzzatj);
    zzac.zzC(paramzzatj.zzbLS);
    zzn(paramzzatj.packageName, true);
    final zzatj localzzatj = new zzatj(paramzzatj);
    if (paramzzatj.zzbLS.getValue() == null)
    {
      this.zzbLa.zzMf().zzp(new Runnable()
      {
        public void run()
        {
          zzaui.zza(zzaui.this).zzOG();
          zzaui.zza(zzaui.this).zze(localzzatj);
        }
      });
      return;
    }
    this.zzbLa.zzMf().zzp(new Runnable()
    {
      public void run()
      {
        zzaui.zza(zzaui.this).zzOG();
        zzaui.zza(zzaui.this).zzd(localzzatj);
      }
    });
  }
  
  @BinderThread
  public String zzc(zzatg paramzzatg)
  {
    zzb(paramzzatg, false);
    return this.zzbLa.zzfS(paramzzatg.packageName);
  }
  
  @BinderThread
  public List<zzatj> zzl(final String paramString1, final String paramString2, final String paramString3)
  {
    zzn(paramString1, true);
    paramString1 = this.zzbLa.zzMf().zze(new Callable()
    {
      public List<zzatj> zzOI()
        throws Exception
      {
        zzaui.zza(zzaui.this).zzOG();
        return zzaui.zza(zzaui.this).zzMb().zzj(paramString1, paramString2, paramString3);
      }
    });
    try
    {
      paramString1 = (List)paramString1.get();
      return paramString1;
    }
    catch (InterruptedException paramString1)
    {
      this.zzbLa.zzMg().zzNT().zzm("Failed to get conditional user properties", paramString1);
      return Collections.emptyList();
    }
    catch (ExecutionException paramString1)
    {
      for (;;) {}
    }
  }
  
  protected void zzo(String paramString, boolean paramBoolean)
    throws SecurityException
  {
    if (paramBoolean) {
      if (this.zzbPE == null)
      {
        if (("com.google.android.gms".equals(this.zzbPF)) || (zzy.zzf(this.zzbLa.getContext(), Binder.getCallingUid())) || (GoogleSignatureVerifier.getInstance(this.zzbLa.getContext()).isUidGoogleSigned(this.zzbLa.getContext().getPackageManager(), Binder.getCallingUid())))
        {
          paramBoolean = true;
          this.zzbPE = Boolean.valueOf(paramBoolean);
        }
      }
      else {
        if (!this.zzbPE.booleanValue()) {
          break label95;
        }
      }
    }
    label95:
    do
    {
      return;
      paramBoolean = false;
      break;
      if ((this.zzbPF == null) && (GooglePlayServicesUtilLight.zzb(this.zzbLa.getContext(), Binder.getCallingUid(), paramString))) {
        this.zzbPF = paramString;
      }
    } while (paramString.equals(this.zzbPF));
    throw new SecurityException(String.format("Unknown calling package name '%s'.", new Object[] { paramString }));
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/internal/zzaui.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */