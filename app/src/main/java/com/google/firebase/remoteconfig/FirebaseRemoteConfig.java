package com.google.firebase.remoteconfig;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.util.Log;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.config.ConfigApi.FetchConfigResult;
import com.google.android.gms.internal.zzbtx;
import com.google.android.gms.internal.zzcbs;
import com.google.android.gms.internal.zzcbt;
import com.google.android.gms.internal.zzcbu;
import com.google.android.gms.internal.zzcbv;
import com.google.android.gms.internal.zzcbx;
import com.google.android.gms.internal.zzcby;
import com.google.android.gms.internal.zzcbz.zza;
import com.google.android.gms.internal.zzcbz.zzb;
import com.google.android.gms.internal.zzcbz.zzc;
import com.google.android.gms.internal.zzcbz.zzd;
import com.google.android.gms.internal.zzcbz.zze;
import com.google.android.gms.internal.zzcbz.zzf;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.FirebaseApp;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FirebaseRemoteConfig
{
  public static final boolean DEFAULT_VALUE_FOR_BOOLEAN = false;
  public static final byte[] DEFAULT_VALUE_FOR_BYTE_ARRAY = new byte[0];
  public static final double DEFAULT_VALUE_FOR_DOUBLE = 0.0D;
  public static final long DEFAULT_VALUE_FOR_LONG = 0L;
  public static final String DEFAULT_VALUE_FOR_STRING = "";
  public static final int LAST_FETCH_STATUS_FAILURE = 1;
  public static final int LAST_FETCH_STATUS_NO_FETCH_YET = 0;
  public static final int LAST_FETCH_STATUS_SUCCESS = -1;
  public static final int LAST_FETCH_STATUS_THROTTLED = 2;
  public static final int VALUE_SOURCE_DEFAULT = 1;
  public static final int VALUE_SOURCE_REMOTE = 2;
  public static final int VALUE_SOURCE_STATIC = 0;
  private static FirebaseRemoteConfig wN;
  private final Context mContext;
  private zzcbv wO;
  private zzcbv wP;
  private zzcbv wQ;
  private zzcby wR;
  private final ReadWriteLock wS = new ReentrantReadWriteLock(true);
  
  FirebaseRemoteConfig(Context paramContext)
  {
    this(paramContext, null, null, null, null);
  }
  
  private FirebaseRemoteConfig(Context paramContext, zzcbv paramzzcbv1, zzcbv paramzzcbv2, zzcbv paramzzcbv3, zzcby paramzzcby)
  {
    this.mContext = paramContext;
    if (paramzzcby != null) {}
    for (this.wR = paramzzcby;; this.wR = new zzcby())
    {
      this.wR.zzbm(zzcI(this.mContext));
      if (paramzzcbv1 != null) {
        this.wO = paramzzcbv1;
      }
      if (paramzzcbv2 != null) {
        this.wP = paramzzcbv2;
      }
      if (paramzzcbv3 != null) {
        this.wQ = paramzzcbv3;
      }
      return;
    }
  }
  
  public static FirebaseRemoteConfig getInstance()
  {
    if (wN == null)
    {
      FirebaseApp localFirebaseApp = FirebaseApp.getInstance();
      if (localFirebaseApp == null) {
        throw new IllegalStateException("FirebaseApp has not been initialized.");
      }
      return getInstance(localFirebaseApp.getApplicationContext());
    }
    return wN;
  }
  
  public static FirebaseRemoteConfig getInstance(Context paramContext)
  {
    zzcbz.zze localzze;
    if (wN == null)
    {
      localzze = zzcJ(paramContext);
      if (localzze != null) {
        break label47;
      }
      if (Log.isLoggable("FirebaseRemoteConfig", 3)) {
        Log.d("FirebaseRemoteConfig", "No persisted config was found. Initializing from scratch.");
      }
    }
    label47:
    zzcbv localzzcbv1;
    zzcbv localzzcbv2;
    zzcbv localzzcbv3;
    zzcby localzzcby;
    for (wN = new FirebaseRemoteConfig(paramContext);; wN = new FirebaseRemoteConfig(paramContext, localzzcbv1, localzzcbv2, localzzcbv3, localzzcby))
    {
      return wN;
      if (Log.isLoggable("FirebaseRemoteConfig", 3)) {
        Log.d("FirebaseRemoteConfig", "Initializing from persisted config.");
      }
      localzzcbv1 = zza(localzze.xr);
      localzzcbv2 = zza(localzze.xs);
      localzzcbv3 = zza(localzze.xt);
      localzzcby = zza(localzze.xu);
      if (localzzcby != null) {
        localzzcby.zzaM(zza(localzze.xv));
      }
    }
  }
  
  private static byte[] toByteArray(InputStream paramInputStream)
    throws IOException
  {
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    zza(paramInputStream, localByteArrayOutputStream);
    return localByteArrayOutputStream.toByteArray();
  }
  
  private static long zza(InputStream paramInputStream, OutputStream paramOutputStream)
    throws IOException
  {
    byte[] arrayOfByte = new byte['က'];
    int i;
    for (long l = 0L;; l += i)
    {
      i = paramInputStream.read(arrayOfByte);
      if (i == -1) {
        return l;
      }
      paramOutputStream.write(arrayOfByte, 0, i);
    }
  }
  
  private static zzcbv zza(zzcbz.zza paramzza)
  {
    int k = 0;
    if (paramzza == null) {
      return null;
    }
    HashMap localHashMap1 = new HashMap();
    Object localObject1 = paramzza.xi;
    int m = localObject1.length;
    int i = 0;
    while (i < m)
    {
      zzcbz.zzb[] arrayOfzzb = localObject1[i];
      localObject2 = arrayOfzzb.zzaTl;
      HashMap localHashMap2 = new HashMap();
      arrayOfzzb = arrayOfzzb.xq;
      int n = arrayOfzzb.length;
      j = 0;
      while (j < n)
      {
        zzcbz.zzb localzzb = arrayOfzzb[j];
        localHashMap2.put(localzzb.zzaA, localzzb.xl);
        j += 1;
      }
      localHashMap1.put(localObject2, localHashMap2);
      i += 1;
    }
    localObject1 = paramzza.xj;
    Object localObject2 = new ArrayList();
    int j = localObject1.length;
    i = k;
    while (i < j)
    {
      ((List)localObject2).add(localObject1[i]);
      i += 1;
    }
    return new zzcbv(localHashMap1, paramzza.timestamp, (List)localObject2);
  }
  
  private static zzcby zza(zzcbz.zzc paramzzc)
  {
    if (paramzzc == null) {
      return null;
    }
    zzcby localzzcby = new zzcby();
    localzzcby.zzBf(paramzzc.xm);
    localzzcby.zzbS(paramzzc.xn);
    localzzcby.zzbn(paramzzc.xo);
    return localzzcby;
  }
  
  private static Map<String, zzcbs> zza(zzcbz.zzf[] paramArrayOfzzf)
  {
    HashMap localHashMap = new HashMap();
    if (paramArrayOfzzf == null) {}
    for (;;)
    {
      return localHashMap;
      int j = paramArrayOfzzf.length;
      int i = 0;
      while (i < j)
      {
        zzcbz.zzf localzzf = paramArrayOfzzf[i];
        localHashMap.put(localzzf.zzaTl, new zzcbs(localzzf.resourceId, localzzf.xx));
        i += 1;
      }
    }
  }
  
  private void zza(Context paramContext, List<byte[]> paramList, long paramLong)
  {
    zzw(new zzcbt(paramContext, paramList, paramLong));
  }
  
  private void zzakv()
  {
    this.wS.readLock().lock();
    try
    {
      zzw(new zzcbu(this.mContext, this.wO, this.wP, this.wQ, this.wR));
      return;
    }
    finally
    {
      this.wS.readLock().unlock();
    }
  }
  
  private void zzc(Map<String, Object> paramMap, String paramString, boolean paramBoolean)
  {
    if (paramString == null) {
      return;
    }
    int i;
    HashMap localHashMap;
    Iterator localIterator;
    if ((paramMap == null) || (paramMap.isEmpty()))
    {
      i = 1;
      localHashMap = new HashMap();
      if (i == 0) {
        localIterator = paramMap.keySet().iterator();
      }
    }
    else
    {
      for (;;)
      {
        if (localIterator.hasNext())
        {
          String str = (String)localIterator.next();
          Object localObject = paramMap.get(str);
          if ((localObject instanceof String))
          {
            localHashMap.put(str, ((String)localObject).getBytes(zzcbx.UTF_8));
            continue;
            i = 0;
            break;
          }
          if ((localObject instanceof Long)) {
            localHashMap.put(str, ((Long)localObject).toString().getBytes(zzcbx.UTF_8));
          } else if ((localObject instanceof Integer)) {
            localHashMap.put(str, ((Integer)localObject).toString().getBytes(zzcbx.UTF_8));
          } else if ((localObject instanceof Double)) {
            localHashMap.put(str, ((Double)localObject).toString().getBytes(zzcbx.UTF_8));
          } else if ((localObject instanceof Float)) {
            localHashMap.put(str, ((Float)localObject).toString().getBytes(zzcbx.UTF_8));
          } else if ((localObject instanceof byte[])) {
            localHashMap.put(str, (byte[])localObject);
          } else if ((localObject instanceof Boolean)) {
            localHashMap.put(str, ((Boolean)localObject).toString().getBytes(zzcbx.UTF_8));
          } else {
            throw new IllegalArgumentException("The type of a default value needs to beone of String, Long, Double, Boolean, or byte[].");
          }
        }
      }
    }
    this.wS.writeLock().lock();
    if (i != 0) {}
    for (;;)
    {
      try
      {
        if (this.wQ != null)
        {
          boolean bool = this.wQ.zzmy(paramString);
          if (bool) {}
        }
        else
        {
          return;
        }
        this.wQ.zzj(null, paramString);
        this.wQ.setTimestamp(System.currentTimeMillis());
        if (paramBoolean) {
          this.wR.zzmz(paramString);
        }
        zzakv();
        return;
      }
      finally
      {
        this.wS.writeLock().unlock();
      }
      if (this.wQ == null) {
        this.wQ = new zzcbv(new HashMap(), System.currentTimeMillis(), null);
      }
      this.wQ.zzj(localHashMap, paramString);
      this.wQ.setTimestamp(System.currentTimeMillis());
    }
  }
  
  private long zzcI(Context paramContext)
  {
    try
    {
      long l = this.mContext.getPackageManager().getPackageInfo(paramContext.getPackageName(), 0).lastUpdateTime;
      return l;
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      paramContext = String.valueOf(paramContext.getPackageName());
      Log.e("FirebaseRemoteConfig", String.valueOf(paramContext).length() + 25 + "Package [" + paramContext + "] was not found!");
    }
    return 0L;
  }
  
  /* Error */
  private static zzcbz.zze zzcJ(Context paramContext)
  {
    // Byte code:
    //   0: aload_0
    //   1: ifnonnull +5 -> 6
    //   4: aconst_null
    //   5: areturn
    //   6: aload_0
    //   7: ldc_w 450
    //   10: invokevirtual 454	android/content/Context:openFileInput	(Ljava/lang/String;)Ljava/io/FileInputStream;
    //   13: astore_1
    //   14: aload_1
    //   15: astore_0
    //   16: aload_1
    //   17: invokestatic 456	com/google/firebase/remoteconfig/FirebaseRemoteConfig:toByteArray	(Ljava/io/InputStream;)[B
    //   20: invokestatic 462	com/google/android/gms/internal/zzcfx:zzar	([B)Lcom/google/android/gms/internal/zzcfx;
    //   23: astore_3
    //   24: aload_1
    //   25: astore_0
    //   26: new 133	com/google/android/gms/internal/zzcbz$zze
    //   29: dup
    //   30: invokespecial 463	com/google/android/gms/internal/zzcbz$zze:<init>	()V
    //   33: astore_2
    //   34: aload_1
    //   35: astore_0
    //   36: aload_2
    //   37: aload_3
    //   38: invokevirtual 467	com/google/android/gms/internal/zzcbz$zze:mergeFrom	(Lcom/google/android/gms/internal/zzcfx;)Lcom/google/android/gms/internal/zzcgg;
    //   41: pop
    //   42: aload_1
    //   43: ifnull +7 -> 50
    //   46: aload_1
    //   47: invokevirtual 472	java/io/FileInputStream:close	()V
    //   50: aload_2
    //   51: areturn
    //   52: astore_0
    //   53: ldc 115
    //   55: ldc_w 474
    //   58: aload_0
    //   59: invokestatic 477	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   62: pop
    //   63: goto -13 -> 50
    //   66: astore_2
    //   67: aconst_null
    //   68: astore_1
    //   69: aload_1
    //   70: astore_0
    //   71: ldc 115
    //   73: iconst_3
    //   74: invokestatic 121	android/util/Log:isLoggable	(Ljava/lang/String;I)Z
    //   77: ifeq +15 -> 92
    //   80: aload_1
    //   81: astore_0
    //   82: ldc 115
    //   84: ldc_w 479
    //   87: aload_2
    //   88: invokestatic 481	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   91: pop
    //   92: aload_1
    //   93: ifnull -89 -> 4
    //   96: aload_1
    //   97: invokevirtual 472	java/io/FileInputStream:close	()V
    //   100: aconst_null
    //   101: areturn
    //   102: astore_0
    //   103: ldc 115
    //   105: ldc_w 474
    //   108: aload_0
    //   109: invokestatic 477	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   112: pop
    //   113: aconst_null
    //   114: areturn
    //   115: astore_2
    //   116: aconst_null
    //   117: astore_1
    //   118: aload_1
    //   119: astore_0
    //   120: ldc 115
    //   122: ldc_w 483
    //   125: aload_2
    //   126: invokestatic 477	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   129: pop
    //   130: aload_1
    //   131: ifnull -127 -> 4
    //   134: aload_1
    //   135: invokevirtual 472	java/io/FileInputStream:close	()V
    //   138: aconst_null
    //   139: areturn
    //   140: astore_0
    //   141: ldc 115
    //   143: ldc_w 474
    //   146: aload_0
    //   147: invokestatic 477	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   150: pop
    //   151: aconst_null
    //   152: areturn
    //   153: astore_1
    //   154: aconst_null
    //   155: astore_0
    //   156: aload_0
    //   157: ifnull +7 -> 164
    //   160: aload_0
    //   161: invokevirtual 472	java/io/FileInputStream:close	()V
    //   164: aload_1
    //   165: athrow
    //   166: astore_0
    //   167: ldc 115
    //   169: ldc_w 474
    //   172: aload_0
    //   173: invokestatic 477	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   176: pop
    //   177: goto -13 -> 164
    //   180: astore_1
    //   181: goto -25 -> 156
    //   184: astore_2
    //   185: goto -67 -> 118
    //   188: astore_2
    //   189: goto -120 -> 69
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	192	0	paramContext	Context
    //   13	122	1	localFileInputStream	java.io.FileInputStream
    //   153	12	1	localObject1	Object
    //   180	1	1	localObject2	Object
    //   33	18	2	localzze	zzcbz.zze
    //   66	22	2	localFileNotFoundException1	java.io.FileNotFoundException
    //   115	11	2	localIOException1	IOException
    //   184	1	2	localIOException2	IOException
    //   188	1	2	localFileNotFoundException2	java.io.FileNotFoundException
    //   23	15	3	localzzcfx	com.google.android.gms.internal.zzcfx
    // Exception table:
    //   from	to	target	type
    //   46	50	52	java/io/IOException
    //   6	14	66	java/io/FileNotFoundException
    //   96	100	102	java/io/IOException
    //   6	14	115	java/io/IOException
    //   134	138	140	java/io/IOException
    //   6	14	153	finally
    //   160	164	166	java/io/IOException
    //   16	24	180	finally
    //   26	34	180	finally
    //   36	42	180	finally
    //   71	80	180	finally
    //   82	92	180	finally
    //   120	130	180	finally
    //   16	24	184	java/io/IOException
    //   26	34	184	java/io/IOException
    //   36	42	184	java/io/IOException
    //   16	24	188	java/io/FileNotFoundException
    //   26	34	188	java/io/FileNotFoundException
    //   36	42	188	java/io/FileNotFoundException
  }
  
  private void zzw(Runnable paramRunnable)
  {
    int i = Build.VERSION.SDK_INT;
    AsyncTask.SERIAL_EXECUTOR.execute(paramRunnable);
  }
  
  public boolean activateFetched()
  {
    this.wS.writeLock().lock();
    try
    {
      zzcbv localzzcbv = this.wO;
      if (localzzcbv == null) {
        return false;
      }
      if (this.wP != null)
      {
        l1 = this.wP.getTimestamp();
        long l2 = this.wO.getTimestamp();
        if (l1 >= l2) {
          return false;
        }
      }
      long l1 = this.wO.getTimestamp();
      this.wP = this.wO;
      this.wP.setTimestamp(System.currentTimeMillis());
      this.wO = new zzcbv(null, l1, null);
      l1 = this.wR.zzakD();
      this.wR.zzbn(zzbtx.zza(l1, this.wP.zzBm()));
      zza(this.mContext, this.wP.zzBm(), l1);
      zzakv();
      return true;
    }
    finally
    {
      this.wS.writeLock().unlock();
    }
  }
  
  public Task<Void> fetch()
  {
    return fetch(43200L);
  }
  
  /* Error */
  public Task<Void> fetch(long paramLong)
  {
    // Byte code:
    //   0: ldc_w 526
    //   3: istore 4
    //   5: new 528	com/google/android/gms/tasks/TaskCompletionSource
    //   8: dup
    //   9: invokespecial 529	com/google/android/gms/tasks/TaskCompletionSource:<init>	()V
    //   12: astore 7
    //   14: aload_0
    //   15: getfield 65	com/google/firebase/remoteconfig/FirebaseRemoteConfig:wS	Ljava/util/concurrent/locks/ReadWriteLock;
    //   18: invokeinterface 298 1 0
    //   23: invokeinterface 303 1 0
    //   28: new 531	com/google/android/gms/config/ConfigApi$FetchConfigRequest$Builder
    //   31: dup
    //   32: invokespecial 532	com/google/android/gms/config/ConfigApi$FetchConfigRequest$Builder:<init>	()V
    //   35: astore 8
    //   37: aload 8
    //   39: lload_1
    //   40: invokevirtual 536	com/google/android/gms/config/ConfigApi$FetchConfigRequest$Builder:setCacheExpirationSeconds	(J)Lcom/google/android/gms/config/ConfigApi$FetchConfigRequest$Builder;
    //   43: pop
    //   44: aload_0
    //   45: getfield 69	com/google/firebase/remoteconfig/FirebaseRemoteConfig:wR	Lcom/google/android/gms/internal/zzcby;
    //   48: invokevirtual 539	com/google/android/gms/internal/zzcby:isDeveloperModeEnabled	()Z
    //   51: ifeq +15 -> 66
    //   54: aload 8
    //   56: ldc_w 541
    //   59: ldc_w 543
    //   62: invokevirtual 547	com/google/android/gms/config/ConfigApi$FetchConfigRequest$Builder:addCustomVariable	(Ljava/lang/String;Ljava/lang/String;)Lcom/google/android/gms/config/ConfigApi$FetchConfigRequest$Builder;
    //   65: pop
    //   66: aload 8
    //   68: sipush 10300
    //   71: invokevirtual 551	com/google/android/gms/config/ConfigApi$FetchConfigRequest$Builder:zzgy	(I)Lcom/google/android/gms/config/ConfigApi$FetchConfigRequest$Builder;
    //   74: pop
    //   75: aload_0
    //   76: getfield 83	com/google/firebase/remoteconfig/FirebaseRemoteConfig:wP	Lcom/google/android/gms/internal/zzcbv;
    //   79: ifnull +62 -> 141
    //   82: aload_0
    //   83: getfield 83	com/google/firebase/remoteconfig/FirebaseRemoteConfig:wP	Lcom/google/android/gms/internal/zzcbv;
    //   86: invokevirtual 503	com/google/android/gms/internal/zzcbv:getTimestamp	()J
    //   89: ldc2_w 552
    //   92: lcmp
    //   93: ifeq +48 -> 141
    //   96: invokestatic 391	java/lang/System:currentTimeMillis	()J
    //   99: lstore_1
    //   100: aload_0
    //   101: getfield 83	com/google/firebase/remoteconfig/FirebaseRemoteConfig:wP	Lcom/google/android/gms/internal/zzcbv;
    //   104: invokevirtual 503	com/google/android/gms/internal/zzcbv:getTimestamp	()J
    //   107: lstore 5
    //   109: getstatic 559	java/util/concurrent/TimeUnit:SECONDS	Ljava/util/concurrent/TimeUnit;
    //   112: lload_1
    //   113: lload 5
    //   115: lsub
    //   116: getstatic 562	java/util/concurrent/TimeUnit:MILLISECONDS	Ljava/util/concurrent/TimeUnit;
    //   119: invokevirtual 566	java/util/concurrent/TimeUnit:convert	(JLjava/util/concurrent/TimeUnit;)J
    //   122: lstore_1
    //   123: lload_1
    //   124: ldc2_w 567
    //   127: lcmp
    //   128: ifge +134 -> 262
    //   131: lload_1
    //   132: l2i
    //   133: istore_3
    //   134: aload 8
    //   136: iload_3
    //   137: invokevirtual 571	com/google/android/gms/config/ConfigApi$FetchConfigRequest$Builder:zzgA	(I)Lcom/google/android/gms/config/ConfigApi$FetchConfigRequest$Builder;
    //   140: pop
    //   141: aload_0
    //   142: getfield 81	com/google/firebase/remoteconfig/FirebaseRemoteConfig:wO	Lcom/google/android/gms/internal/zzcbv;
    //   145: ifnull +65 -> 210
    //   148: aload_0
    //   149: getfield 81	com/google/firebase/remoteconfig/FirebaseRemoteConfig:wO	Lcom/google/android/gms/internal/zzcbv;
    //   152: invokevirtual 503	com/google/android/gms/internal/zzcbv:getTimestamp	()J
    //   155: ldc2_w 552
    //   158: lcmp
    //   159: ifeq +51 -> 210
    //   162: invokestatic 391	java/lang/System:currentTimeMillis	()J
    //   165: lstore_1
    //   166: aload_0
    //   167: getfield 81	com/google/firebase/remoteconfig/FirebaseRemoteConfig:wO	Lcom/google/android/gms/internal/zzcbv;
    //   170: invokevirtual 503	com/google/android/gms/internal/zzcbv:getTimestamp	()J
    //   173: lstore 5
    //   175: getstatic 559	java/util/concurrent/TimeUnit:SECONDS	Ljava/util/concurrent/TimeUnit;
    //   178: lload_1
    //   179: lload 5
    //   181: lsub
    //   182: getstatic 562	java/util/concurrent/TimeUnit:MILLISECONDS	Ljava/util/concurrent/TimeUnit;
    //   185: invokevirtual 566	java/util/concurrent/TimeUnit:convert	(JLjava/util/concurrent/TimeUnit;)J
    //   188: lstore_1
    //   189: iload 4
    //   191: istore_3
    //   192: lload_1
    //   193: ldc2_w 567
    //   196: lcmp
    //   197: ifge +6 -> 203
    //   200: lload_1
    //   201: l2i
    //   202: istore_3
    //   203: aload 8
    //   205: iload_3
    //   206: invokevirtual 574	com/google/android/gms/config/ConfigApi$FetchConfigRequest$Builder:zzgz	(I)Lcom/google/android/gms/config/ConfigApi$FetchConfigRequest$Builder;
    //   209: pop
    //   210: new 576	com/google/android/gms/internal/zzacf
    //   213: dup
    //   214: aload_0
    //   215: getfield 67	com/google/firebase/remoteconfig/FirebaseRemoteConfig:mContext	Landroid/content/Context;
    //   218: invokespecial 577	com/google/android/gms/internal/zzacf:<init>	(Landroid/content/Context;)V
    //   221: aload 8
    //   223: invokevirtual 581	com/google/android/gms/config/ConfigApi$FetchConfigRequest$Builder:build	()Lcom/google/android/gms/config/ConfigApi$FetchConfigRequest;
    //   226: invokevirtual 584	com/google/android/gms/internal/zzacf:zza	(Lcom/google/android/gms/config/ConfigApi$FetchConfigRequest;)Lcom/google/android/gms/common/api/PendingResult;
    //   229: new 6	com/google/firebase/remoteconfig/FirebaseRemoteConfig$1
    //   232: dup
    //   233: aload_0
    //   234: aload 7
    //   236: invokespecial 587	com/google/firebase/remoteconfig/FirebaseRemoteConfig$1:<init>	(Lcom/google/firebase/remoteconfig/FirebaseRemoteConfig;Lcom/google/android/gms/tasks/TaskCompletionSource;)V
    //   239: invokevirtual 593	com/google/android/gms/common/api/PendingResult:setResultCallback	(Lcom/google/android/gms/common/api/ResultCallback;)V
    //   242: aload_0
    //   243: getfield 65	com/google/firebase/remoteconfig/FirebaseRemoteConfig:wS	Ljava/util/concurrent/locks/ReadWriteLock;
    //   246: invokeinterface 298 1 0
    //   251: invokeinterface 309 1 0
    //   256: aload 7
    //   258: invokevirtual 596	com/google/android/gms/tasks/TaskCompletionSource:getTask	()Lcom/google/android/gms/tasks/Task;
    //   261: areturn
    //   262: ldc_w 526
    //   265: istore_3
    //   266: goto -132 -> 134
    //   269: astore 7
    //   271: aload_0
    //   272: getfield 65	com/google/firebase/remoteconfig/FirebaseRemoteConfig:wS	Ljava/util/concurrent/locks/ReadWriteLock;
    //   275: invokeinterface 298 1 0
    //   280: invokeinterface 309 1 0
    //   285: aload 7
    //   287: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	288	0	this	FirebaseRemoteConfig
    //   0	288	1	paramLong	long
    //   133	133	3	i	int
    //   3	187	4	j	int
    //   107	73	5	l	long
    //   12	245	7	localTaskCompletionSource	TaskCompletionSource
    //   269	17	7	localObject	Object
    //   35	187	8	localBuilder	com.google.android.gms.config.ConfigApi.FetchConfigRequest.Builder
    // Exception table:
    //   from	to	target	type
    //   28	66	269	finally
    //   66	123	269	finally
    //   134	141	269	finally
    //   141	189	269	finally
    //   203	210	269	finally
    //   210	242	269	finally
  }
  
  public boolean getBoolean(String paramString)
  {
    return getBoolean(paramString, "configns:firebase");
  }
  
  public boolean getBoolean(String paramString1, String paramString2)
  {
    if (paramString2 == null) {
      return false;
    }
    this.wS.readLock().lock();
    try
    {
      boolean bool;
      if ((this.wP != null) && (this.wP.zzaP(paramString1, paramString2)))
      {
        String str = new String(this.wP.zzaQ(paramString1, paramString2), zzcbx.UTF_8);
        bool = zzcbx.zzaVw.matcher(str).matches();
        if (bool) {
          return true;
        }
        bool = zzcbx.zzaVx.matcher(str).matches();
        if (bool) {
          return false;
        }
      }
      if ((this.wQ != null) && (this.wQ.zzaP(paramString1, paramString2)))
      {
        paramString1 = new String(this.wQ.zzaQ(paramString1, paramString2), zzcbx.UTF_8);
        bool = zzcbx.zzaVw.matcher(paramString1).matches();
        if (bool) {
          return true;
        }
        bool = zzcbx.zzaVx.matcher(paramString1).matches();
        if (bool) {
          return false;
        }
      }
      return false;
    }
    finally
    {
      this.wS.readLock().unlock();
    }
  }
  
  public byte[] getByteArray(String paramString)
  {
    return getByteArray(paramString, "configns:firebase");
  }
  
  public byte[] getByteArray(String paramString1, String paramString2)
  {
    if (paramString2 == null) {
      return DEFAULT_VALUE_FOR_BYTE_ARRAY;
    }
    this.wS.readLock().lock();
    try
    {
      if ((this.wP != null) && (this.wP.zzaP(paramString1, paramString2)))
      {
        paramString1 = this.wP.zzaQ(paramString1, paramString2);
        return paramString1;
      }
      if ((this.wQ != null) && (this.wQ.zzaP(paramString1, paramString2)))
      {
        paramString1 = this.wQ.zzaQ(paramString1, paramString2);
        return paramString1;
      }
      paramString1 = DEFAULT_VALUE_FOR_BYTE_ARRAY;
      return paramString1;
    }
    finally
    {
      this.wS.readLock().unlock();
    }
  }
  
  public double getDouble(String paramString)
  {
    return getDouble(paramString, "configns:firebase");
  }
  
  public double getDouble(String paramString1, String paramString2)
  {
    if (paramString2 == null) {
      return 0.0D;
    }
    this.wS.readLock().lock();
    try
    {
      double d;
      if ((this.wP != null) && (this.wP.zzaP(paramString1, paramString2)))
      {
        String str = new String(this.wP.zzaQ(paramString1, paramString2), zzcbx.UTF_8);
        try
        {
          d = Double.valueOf(str).doubleValue();
          return d;
        }
        catch (NumberFormatException localNumberFormatException) {}
      }
      if ((this.wQ != null) && (this.wQ.zzaP(paramString1, paramString2)))
      {
        paramString1 = new String(this.wQ.zzaQ(paramString1, paramString2), zzcbx.UTF_8);
        try
        {
          d = Double.valueOf(paramString1).doubleValue();
          return d;
        }
        catch (NumberFormatException paramString1) {}
      }
      return 0.0D;
    }
    finally
    {
      this.wS.readLock().unlock();
    }
  }
  
  /* Error */
  public FirebaseRemoteConfigInfo getInfo()
  {
    // Byte code:
    //   0: new 653	com/google/android/gms/internal/zzcbw
    //   3: dup
    //   4: invokespecial 654	com/google/android/gms/internal/zzcbw:<init>	()V
    //   7: astore_3
    //   8: aload_0
    //   9: getfield 65	com/google/firebase/remoteconfig/FirebaseRemoteConfig:wS	Ljava/util/concurrent/locks/ReadWriteLock;
    //   12: invokeinterface 298 1 0
    //   17: invokeinterface 303 1 0
    //   22: aload_0
    //   23: getfield 81	com/google/firebase/remoteconfig/FirebaseRemoteConfig:wO	Lcom/google/android/gms/internal/zzcbv;
    //   26: ifnonnull +63 -> 89
    //   29: ldc2_w 552
    //   32: lstore_1
    //   33: aload_3
    //   34: lload_1
    //   35: invokevirtual 657	com/google/android/gms/internal/zzcbw:zzbl	(J)V
    //   38: aload_3
    //   39: aload_0
    //   40: getfield 69	com/google/firebase/remoteconfig/FirebaseRemoteConfig:wR	Lcom/google/android/gms/internal/zzcby;
    //   43: invokevirtual 660	com/google/android/gms/internal/zzcby:getLastFetchStatus	()I
    //   46: invokevirtual 661	com/google/android/gms/internal/zzcbw:zzBf	(I)V
    //   49: aload_3
    //   50: new 663	com/google/firebase/remoteconfig/FirebaseRemoteConfigSettings$Builder
    //   53: dup
    //   54: invokespecial 664	com/google/firebase/remoteconfig/FirebaseRemoteConfigSettings$Builder:<init>	()V
    //   57: aload_0
    //   58: getfield 69	com/google/firebase/remoteconfig/FirebaseRemoteConfig:wR	Lcom/google/android/gms/internal/zzcby;
    //   61: invokevirtual 539	com/google/android/gms/internal/zzcby:isDeveloperModeEnabled	()Z
    //   64: invokevirtual 668	com/google/firebase/remoteconfig/FirebaseRemoteConfigSettings$Builder:setDeveloperModeEnabled	(Z)Lcom/google/firebase/remoteconfig/FirebaseRemoteConfigSettings$Builder;
    //   67: invokevirtual 671	com/google/firebase/remoteconfig/FirebaseRemoteConfigSettings$Builder:build	()Lcom/google/firebase/remoteconfig/FirebaseRemoteConfigSettings;
    //   70: invokevirtual 675	com/google/android/gms/internal/zzcbw:setConfigSettings	(Lcom/google/firebase/remoteconfig/FirebaseRemoteConfigSettings;)V
    //   73: aload_0
    //   74: getfield 65	com/google/firebase/remoteconfig/FirebaseRemoteConfig:wS	Ljava/util/concurrent/locks/ReadWriteLock;
    //   77: invokeinterface 298 1 0
    //   82: invokeinterface 309 1 0
    //   87: aload_3
    //   88: areturn
    //   89: aload_0
    //   90: getfield 81	com/google/firebase/remoteconfig/FirebaseRemoteConfig:wO	Lcom/google/android/gms/internal/zzcbv;
    //   93: invokevirtual 503	com/google/android/gms/internal/zzcbv:getTimestamp	()J
    //   96: lstore_1
    //   97: goto -64 -> 33
    //   100: astore_3
    //   101: aload_0
    //   102: getfield 65	com/google/firebase/remoteconfig/FirebaseRemoteConfig:wS	Ljava/util/concurrent/locks/ReadWriteLock;
    //   105: invokeinterface 298 1 0
    //   110: invokeinterface 309 1 0
    //   115: aload_3
    //   116: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	117	0	this	FirebaseRemoteConfig
    //   32	65	1	l	long
    //   7	81	3	localzzcbw	com.google.android.gms.internal.zzcbw
    //   100	16	3	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   22	29	100	finally
    //   33	73	100	finally
    //   89	97	100	finally
  }
  
  public Set<String> getKeysByPrefix(String paramString)
  {
    return getKeysByPrefix(paramString, "configns:firebase");
  }
  
  public Set<String> getKeysByPrefix(String paramString1, String paramString2)
  {
    this.wS.readLock().lock();
    try
    {
      if (this.wP == null)
      {
        paramString1 = new TreeSet();
        return paramString1;
      }
      paramString1 = this.wP.zzaR(paramString1, paramString2);
      return paramString1;
    }
    finally
    {
      this.wS.readLock().unlock();
    }
  }
  
  public long getLong(String paramString)
  {
    return getLong(paramString, "configns:firebase");
  }
  
  public long getLong(String paramString1, String paramString2)
  {
    if (paramString2 == null) {
      return 0L;
    }
    this.wS.readLock().lock();
    try
    {
      long l;
      if ((this.wP != null) && (this.wP.zzaP(paramString1, paramString2)))
      {
        String str = new String(this.wP.zzaQ(paramString1, paramString2), zzcbx.UTF_8);
        try
        {
          l = Long.valueOf(str).longValue();
          return l;
        }
        catch (NumberFormatException localNumberFormatException) {}
      }
      if ((this.wQ != null) && (this.wQ.zzaP(paramString1, paramString2)))
      {
        paramString1 = new String(this.wQ.zzaQ(paramString1, paramString2), zzcbx.UTF_8);
        try
        {
          l = Long.valueOf(paramString1).longValue();
          return l;
        }
        catch (NumberFormatException paramString1) {}
      }
      return 0L;
    }
    finally
    {
      this.wS.readLock().unlock();
    }
  }
  
  public String getString(String paramString)
  {
    return getString(paramString, "configns:firebase");
  }
  
  public String getString(String paramString1, String paramString2)
  {
    if (paramString2 == null) {
      return "";
    }
    this.wS.readLock().lock();
    try
    {
      if ((this.wP != null) && (this.wP.zzaP(paramString1, paramString2)))
      {
        paramString1 = new String(this.wP.zzaQ(paramString1, paramString2), zzcbx.UTF_8);
        return paramString1;
      }
      if ((this.wQ != null) && (this.wQ.zzaP(paramString1, paramString2)))
      {
        paramString1 = new String(this.wQ.zzaQ(paramString1, paramString2), zzcbx.UTF_8);
        return paramString1;
      }
      return "";
    }
    finally
    {
      this.wS.readLock().unlock();
    }
  }
  
  public FirebaseRemoteConfigValue getValue(String paramString)
  {
    return getValue(paramString, "configns:firebase");
  }
  
  public FirebaseRemoteConfigValue getValue(String paramString1, String paramString2)
  {
    if (paramString2 == null) {
      return new zzcbx(DEFAULT_VALUE_FOR_BYTE_ARRAY, 0);
    }
    this.wS.readLock().lock();
    try
    {
      if ((this.wP != null) && (this.wP.zzaP(paramString1, paramString2)))
      {
        paramString1 = new zzcbx(this.wP.zzaQ(paramString1, paramString2), 2);
        return paramString1;
      }
      if ((this.wQ != null) && (this.wQ.zzaP(paramString1, paramString2)))
      {
        paramString1 = new zzcbx(this.wQ.zzaQ(paramString1, paramString2), 1);
        return paramString1;
      }
      paramString1 = new zzcbx(DEFAULT_VALUE_FOR_BYTE_ARRAY, 0);
      return paramString1;
    }
    finally
    {
      this.wS.readLock().unlock();
    }
  }
  
  /* Error */
  public void setConfigSettings(FirebaseRemoteConfigSettings paramFirebaseRemoteConfigSettings)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 65	com/google/firebase/remoteconfig/FirebaseRemoteConfig:wS	Ljava/util/concurrent/locks/ReadWriteLock;
    //   4: invokeinterface 377 1 0
    //   9: invokeinterface 303 1 0
    //   14: aload_0
    //   15: getfield 69	com/google/firebase/remoteconfig/FirebaseRemoteConfig:wR	Lcom/google/android/gms/internal/zzcby;
    //   18: invokevirtual 539	com/google/android/gms/internal/zzcby:isDeveloperModeEnabled	()Z
    //   21: istore_3
    //   22: aload_1
    //   23: ifnonnull +37 -> 60
    //   26: iconst_0
    //   27: istore_2
    //   28: aload_0
    //   29: getfield 69	com/google/firebase/remoteconfig/FirebaseRemoteConfig:wR	Lcom/google/android/gms/internal/zzcby;
    //   32: iload_2
    //   33: invokevirtual 259	com/google/android/gms/internal/zzcby:zzbS	(Z)V
    //   36: iload_3
    //   37: iload_2
    //   38: if_icmpeq +7 -> 45
    //   41: aload_0
    //   42: invokespecial 399	com/google/firebase/remoteconfig/FirebaseRemoteConfig:zzakv	()V
    //   45: aload_0
    //   46: getfield 65	com/google/firebase/remoteconfig/FirebaseRemoteConfig:wS	Ljava/util/concurrent/locks/ReadWriteLock;
    //   49: invokeinterface 377 1 0
    //   54: invokeinterface 309 1 0
    //   59: return
    //   60: aload_1
    //   61: invokevirtual 715	com/google/firebase/remoteconfig/FirebaseRemoteConfigSettings:isDeveloperModeEnabled	()Z
    //   64: istore_2
    //   65: goto -37 -> 28
    //   68: astore_1
    //   69: aload_0
    //   70: getfield 65	com/google/firebase/remoteconfig/FirebaseRemoteConfig:wS	Ljava/util/concurrent/locks/ReadWriteLock;
    //   73: invokeinterface 377 1 0
    //   78: invokeinterface 309 1 0
    //   83: aload_1
    //   84: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	85	0	this	FirebaseRemoteConfig
    //   0	85	1	paramFirebaseRemoteConfigSettings	FirebaseRemoteConfigSettings
    //   27	38	2	bool1	boolean
    //   21	18	3	bool2	boolean
    // Exception table:
    //   from	to	target	type
    //   14	22	68	finally
    //   28	36	68	finally
    //   41	45	68	finally
    //   60	65	68	finally
  }
  
  public void setDefaults(int paramInt)
  {
    setDefaults(paramInt, "configns:firebase");
  }
  
  /* Error */
  public void setDefaults(int paramInt, String paramString)
  {
    // Byte code:
    //   0: aload_2
    //   1: ifnonnull +22 -> 23
    //   4: ldc 115
    //   6: iconst_3
    //   7: invokestatic 121	android/util/Log:isLoggable	(Ljava/lang/String;I)Z
    //   10: ifeq +12 -> 22
    //   13: ldc 115
    //   15: ldc_w 723
    //   18: invokestatic 127	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   21: pop
    //   22: return
    //   23: aload_0
    //   24: getfield 65	com/google/firebase/remoteconfig/FirebaseRemoteConfig:wS	Ljava/util/concurrent/locks/ReadWriteLock;
    //   27: invokeinterface 298 1 0
    //   32: invokeinterface 303 1 0
    //   37: aload_0
    //   38: getfield 69	com/google/firebase/remoteconfig/FirebaseRemoteConfig:wR	Lcom/google/android/gms/internal/zzcby;
    //   41: ifnull +105 -> 146
    //   44: aload_0
    //   45: getfield 69	com/google/firebase/remoteconfig/FirebaseRemoteConfig:wR	Lcom/google/android/gms/internal/zzcby;
    //   48: invokevirtual 727	com/google/android/gms/internal/zzcby:zzakB	()Ljava/util/Map;
    //   51: ifnull +95 -> 146
    //   54: aload_0
    //   55: getfield 69	com/google/firebase/remoteconfig/FirebaseRemoteConfig:wR	Lcom/google/android/gms/internal/zzcby;
    //   58: invokevirtual 727	com/google/android/gms/internal/zzcby:zzakB	()Ljava/util/Map;
    //   61: aload_2
    //   62: invokeinterface 340 2 0
    //   67: ifnull +79 -> 146
    //   70: aload_0
    //   71: getfield 69	com/google/firebase/remoteconfig/FirebaseRemoteConfig:wR	Lcom/google/android/gms/internal/zzcby;
    //   74: invokevirtual 727	com/google/android/gms/internal/zzcby:zzakB	()Ljava/util/Map;
    //   77: aload_2
    //   78: invokeinterface 340 2 0
    //   83: checkcast 270	com/google/android/gms/internal/zzcbs
    //   86: astore 4
    //   88: iload_1
    //   89: aload 4
    //   91: invokevirtual 730	com/google/android/gms/internal/zzcbs:zzakw	()I
    //   94: if_icmpne +52 -> 146
    //   97: aload_0
    //   98: getfield 69	com/google/firebase/remoteconfig/FirebaseRemoteConfig:wR	Lcom/google/android/gms/internal/zzcby;
    //   101: invokevirtual 733	com/google/android/gms/internal/zzcby:zzakC	()J
    //   104: aload 4
    //   106: invokevirtual 736	com/google/android/gms/internal/zzcbs:zzakx	()J
    //   109: lcmp
    //   110: ifne +36 -> 146
    //   113: ldc 115
    //   115: iconst_3
    //   116: invokestatic 121	android/util/Log:isLoggable	(Ljava/lang/String;I)Z
    //   119: ifeq +12 -> 131
    //   122: ldc 115
    //   124: ldc_w 738
    //   127: invokestatic 127	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   130: pop
    //   131: aload_0
    //   132: getfield 65	com/google/firebase/remoteconfig/FirebaseRemoteConfig:wS	Ljava/util/concurrent/locks/ReadWriteLock;
    //   135: invokeinterface 298 1 0
    //   140: invokeinterface 309 1 0
    //   145: return
    //   146: aload_0
    //   147: getfield 65	com/google/firebase/remoteconfig/FirebaseRemoteConfig:wS	Ljava/util/concurrent/locks/ReadWriteLock;
    //   150: invokeinterface 298 1 0
    //   155: invokeinterface 309 1 0
    //   160: new 193	java/util/HashMap
    //   163: dup
    //   164: invokespecial 194	java/util/HashMap:<init>	()V
    //   167: astore 10
    //   169: aload_0
    //   170: getfield 67	com/google/firebase/remoteconfig/FirebaseRemoteConfig:mContext	Landroid/content/Context;
    //   173: invokevirtual 742	android/content/Context:getResources	()Landroid/content/res/Resources;
    //   176: iload_1
    //   177: invokevirtual 748	android/content/res/Resources:getXml	(I)Landroid/content/res/XmlResourceParser;
    //   180: astore 11
    //   182: aload 11
    //   184: invokeinterface 753 1 0
    //   189: istore_3
    //   190: aconst_null
    //   191: astore 4
    //   193: aconst_null
    //   194: astore 9
    //   196: aconst_null
    //   197: astore 5
    //   199: iload_3
    //   200: iconst_1
    //   201: if_icmpeq +232 -> 433
    //   204: iload_3
    //   205: iconst_2
    //   206: if_icmpne +60 -> 266
    //   209: aload 11
    //   211: invokeinterface 756 1 0
    //   216: astore 8
    //   218: aload 4
    //   220: astore 7
    //   222: aload 5
    //   224: astore 6
    //   226: aload 11
    //   228: invokeinterface 758 1 0
    //   233: istore_3
    //   234: aload 6
    //   236: astore 5
    //   238: aload 7
    //   240: astore 4
    //   242: aload 8
    //   244: astore 9
    //   246: goto -47 -> 199
    //   249: astore_2
    //   250: aload_0
    //   251: getfield 65	com/google/firebase/remoteconfig/FirebaseRemoteConfig:wS	Ljava/util/concurrent/locks/ReadWriteLock;
    //   254: invokeinterface 298 1 0
    //   259: invokeinterface 309 1 0
    //   264: aload_2
    //   265: athrow
    //   266: iload_3
    //   267: iconst_3
    //   268: if_icmpne +74 -> 342
    //   271: aload 5
    //   273: astore 6
    //   275: aload 4
    //   277: astore 7
    //   279: ldc_w 760
    //   282: aload 11
    //   284: invokeinterface 756 1 0
    //   289: invokevirtual 763	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   292: ifeq +189 -> 481
    //   295: aload 5
    //   297: astore 6
    //   299: aload 4
    //   301: astore 7
    //   303: aload 4
    //   305: ifnull +176 -> 481
    //   308: aload 5
    //   310: astore 6
    //   312: aload 4
    //   314: astore 7
    //   316: aload 5
    //   318: ifnull +163 -> 481
    //   321: aload 10
    //   323: aload 4
    //   325: aload 5
    //   327: invokeinterface 223 3 0
    //   332: pop
    //   333: aconst_null
    //   334: astore 6
    //   336: aconst_null
    //   337: astore 7
    //   339: goto +142 -> 481
    //   342: aload 5
    //   344: astore 6
    //   346: aload 4
    //   348: astore 7
    //   350: aload 9
    //   352: astore 8
    //   354: iload_3
    //   355: iconst_4
    //   356: if_icmpne -130 -> 226
    //   359: ldc_w 765
    //   362: aload 9
    //   364: invokevirtual 763	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   367: ifeq +23 -> 390
    //   370: aload 11
    //   372: invokeinterface 768 1 0
    //   377: astore 7
    //   379: aload 5
    //   381: astore 6
    //   383: aload 9
    //   385: astore 8
    //   387: goto -161 -> 226
    //   390: aload 5
    //   392: astore 6
    //   394: aload 4
    //   396: astore 7
    //   398: aload 9
    //   400: astore 8
    //   402: ldc_w 770
    //   405: aload 9
    //   407: invokevirtual 763	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   410: ifeq -184 -> 226
    //   413: aload 11
    //   415: invokeinterface 768 1 0
    //   420: astore 6
    //   422: aload 4
    //   424: astore 7
    //   426: aload 9
    //   428: astore 8
    //   430: goto -204 -> 226
    //   433: new 270	com/google/android/gms/internal/zzcbs
    //   436: dup
    //   437: iload_1
    //   438: aload_0
    //   439: getfield 69	com/google/firebase/remoteconfig/FirebaseRemoteConfig:wR	Lcom/google/android/gms/internal/zzcby;
    //   442: invokevirtual 733	com/google/android/gms/internal/zzcby:zzakC	()J
    //   445: invokespecial 279	com/google/android/gms/internal/zzcbs:<init>	(IJ)V
    //   448: astore 4
    //   450: aload_0
    //   451: getfield 69	com/google/firebase/remoteconfig/FirebaseRemoteConfig:wR	Lcom/google/android/gms/internal/zzcby;
    //   454: aload_2
    //   455: aload 4
    //   457: invokevirtual 773	com/google/android/gms/internal/zzcby:zza	(Ljava/lang/String;Lcom/google/android/gms/internal/zzcbs;)V
    //   460: aload_0
    //   461: aload 10
    //   463: aload_2
    //   464: iconst_0
    //   465: invokespecial 775	com/google/firebase/remoteconfig/FirebaseRemoteConfig:zzc	(Ljava/util/Map;Ljava/lang/String;Z)V
    //   468: return
    //   469: astore_2
    //   470: ldc 115
    //   472: ldc_w 777
    //   475: aload_2
    //   476: invokestatic 477	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   479: pop
    //   480: return
    //   481: aconst_null
    //   482: astore 8
    //   484: goto -258 -> 226
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	487	0	this	FirebaseRemoteConfig
    //   0	487	1	paramInt	int
    //   0	487	2	paramString	String
    //   189	168	3	i	int
    //   86	370	4	localObject1	Object
    //   197	194	5	localObject2	Object
    //   224	197	6	localObject3	Object
    //   220	205	7	localObject4	Object
    //   216	267	8	localObject5	Object
    //   194	233	9	localObject6	Object
    //   167	295	10	localHashMap	HashMap
    //   180	234	11	localXmlResourceParser	android.content.res.XmlResourceParser
    // Exception table:
    //   from	to	target	type
    //   37	131	249	finally
    //   169	190	469	java/lang/Exception
    //   209	218	469	java/lang/Exception
    //   226	234	469	java/lang/Exception
    //   279	295	469	java/lang/Exception
    //   321	333	469	java/lang/Exception
    //   359	379	469	java/lang/Exception
    //   402	422	469	java/lang/Exception
    //   433	468	469	java/lang/Exception
  }
  
  public void setDefaults(Map<String, Object> paramMap)
  {
    setDefaults(paramMap, "configns:firebase");
  }
  
  public void setDefaults(Map<String, Object> paramMap, String paramString)
  {
    zzc(paramMap, paramString, true);
  }
  
  @VisibleForTesting
  void zza(TaskCompletionSource<Void> paramTaskCompletionSource, ConfigApi.FetchConfigResult paramFetchConfigResult)
  {
    if ((paramFetchConfigResult == null) || (paramFetchConfigResult.getStatus() == null))
    {
      this.wR.zzBf(1);
      paramTaskCompletionSource.setException(new FirebaseRemoteConfigFetchException());
      zzakv();
      return;
    }
    int i = paramFetchConfigResult.getStatus().getStatusCode();
    this.wS.writeLock().lock();
    switch (i)
    {
    }
    for (;;)
    {
      try
      {
        if (paramFetchConfigResult.getStatus().isSuccess()) {
          Log.w("FirebaseRemoteConfig", 45 + "Unknown (successful) status code: " + i);
        }
        this.wR.zzBf(1);
        paramTaskCompletionSource.setException(new FirebaseRemoteConfigFetchException());
        zzakv();
        return;
      }
      finally
      {
        this.wS.writeLock().unlock();
      }
      this.wR.zzBf(1);
      paramTaskCompletionSource.setException(new FirebaseRemoteConfigFetchException());
      zzakv();
      continue;
      this.wR.zzBf(2);
      paramTaskCompletionSource.setException(new FirebaseRemoteConfigFetchThrottledException(paramFetchConfigResult.getThrottleEndTimeMillis()));
      zzakv();
      continue;
      this.wR.zzBf(-1);
      String str1;
      HashMap localHashMap2;
      Iterator localIterator2;
      String str2;
      if ((this.wO != null) && (!this.wO.zzakz()))
      {
        localMap = paramFetchConfigResult.getAllConfigKeys();
        localHashMap1 = new HashMap();
        localIterator1 = localMap.keySet().iterator();
        while (localIterator1.hasNext())
        {
          str1 = (String)localIterator1.next();
          localHashMap2 = new HashMap();
          localIterator2 = ((Set)localMap.get(str1)).iterator();
          while (localIterator2.hasNext())
          {
            str2 = (String)localIterator2.next();
            localHashMap2.put(str2, paramFetchConfigResult.getAsByteArray(str2, null, str1));
          }
          localHashMap1.put(str1, localHashMap2);
        }
        this.wO = new zzcbv(localHashMap1, this.wO.getTimestamp(), paramFetchConfigResult.zzBm());
      }
      paramTaskCompletionSource.setResult(null);
      zzakv();
      continue;
      Map localMap = paramFetchConfigResult.getAllConfigKeys();
      HashMap localHashMap1 = new HashMap();
      Iterator localIterator1 = localMap.keySet().iterator();
      while (localIterator1.hasNext())
      {
        str1 = (String)localIterator1.next();
        localHashMap2 = new HashMap();
        localIterator2 = ((Set)localMap.get(str1)).iterator();
        while (localIterator2.hasNext())
        {
          str2 = (String)localIterator2.next();
          localHashMap2.put(str2, paramFetchConfigResult.getAsByteArray(str2, null, str1));
        }
        localHashMap1.put(str1, localHashMap2);
      }
      this.wO = new zzcbv(localHashMap1, System.currentTimeMillis(), paramFetchConfigResult.zzBm());
      this.wR.zzBf(-1);
      paramTaskCompletionSource.setResult(null);
      zzakv();
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/firebase/remoteconfig/FirebaseRemoteConfig.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */