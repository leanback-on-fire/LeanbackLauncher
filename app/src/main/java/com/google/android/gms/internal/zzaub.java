package com.google.android.gms.internal;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.WorkerThread;
import com.google.android.gms.common.internal.zzac;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class zzaub
  extends zzauk
{
  public zzaub(zzauh paramzzauh)
  {
    super(paramzzauh);
  }
  
  @WorkerThread
  private byte[] zzd(HttpURLConnection paramHttpURLConnection)
    throws IOException
  {
    byte[] arrayOfByte = null;
    Object localObject = arrayOfByte;
    ByteArrayOutputStream localByteArrayOutputStream;
    try
    {
      localByteArrayOutputStream = new ByteArrayOutputStream();
      localObject = arrayOfByte;
      paramHttpURLConnection = paramHttpURLConnection.getInputStream();
      localObject = paramHttpURLConnection;
      arrayOfByte = new byte['Ѐ'];
      for (;;)
      {
        localObject = paramHttpURLConnection;
        int i = paramHttpURLConnection.read(arrayOfByte);
        if (i <= 0) {
          break;
        }
        localObject = paramHttpURLConnection;
        localByteArrayOutputStream.write(arrayOfByte, 0, i);
      }
      localObject = paramHttpURLConnection;
    }
    finally
    {
      if (localObject != null) {
        ((InputStream)localObject).close();
      }
    }
    arrayOfByte = localByteArrayOutputStream.toByteArray();
    if (paramHttpURLConnection != null) {
      paramHttpURLConnection.close();
    }
    return arrayOfByte;
  }
  
  public boolean isNetworkConnected()
  {
    zznA();
    Object localObject1 = (ConnectivityManager)getContext().getSystemService("connectivity");
    try
    {
      localObject1 = ((ConnectivityManager)localObject1).getActiveNetworkInfo();
      if ((localObject1 != null) && (((NetworkInfo)localObject1).isConnected())) {
        return true;
      }
    }
    catch (SecurityException localSecurityException)
    {
      for (;;)
      {
        Object localObject2 = null;
      }
    }
    return false;
  }
  
  protected void onInitialize() {}
  
  @WorkerThread
  public void zza(String paramString, URL paramURL, Map<String, String> paramMap, zza paramzza)
  {
    zzmW();
    zznA();
    zzac.zzC(paramURL);
    zzac.zzC(paramzza);
    zzMf().zzq(new zzc(paramString, paramURL, null, paramMap, paramzza));
  }
  
  @WorkerThread
  public void zza(String paramString, URL paramURL, byte[] paramArrayOfByte, Map<String, String> paramMap, zza paramzza)
  {
    zzmW();
    zznA();
    zzac.zzC(paramURL);
    zzac.zzC(paramArrayOfByte);
    zzac.zzC(paramzza);
    zzMf().zzq(new zzc(paramString, paramURL, paramArrayOfByte, paramMap, paramzza));
  }
  
  @WorkerThread
  protected HttpURLConnection zzc(URL paramURL)
    throws IOException
  {
    paramURL = paramURL.openConnection();
    if (!(paramURL instanceof HttpURLConnection)) {
      throw new IOException("Failed to obtain HTTP connection");
    }
    paramURL = (HttpURLConnection)paramURL;
    paramURL.setDefaultUseCaches(false);
    zzMi().zzMY();
    paramURL.setConnectTimeout(60000);
    zzMi().zzMZ();
    paramURL.setReadTimeout(61000);
    paramURL.setInstanceFollowRedirects(false);
    paramURL.setDoInput(true);
    return paramURL;
  }
  
  @WorkerThread
  static abstract interface zza
  {
    public abstract void zza(String paramString, int paramInt, Throwable paramThrowable, byte[] paramArrayOfByte, Map<String, List<String>> paramMap);
  }
  
  @WorkerThread
  private static class zzb
    implements Runnable
  {
    private final String mPackageName;
    private final int zzJD;
    private final Throwable zzYI;
    private final zzaub.zza zzbNL;
    private final byte[] zzbNM;
    private final Map<String, List<String>> zzbNN;
    
    private zzb(String paramString, zzaub.zza paramzza, int paramInt, Throwable paramThrowable, byte[] paramArrayOfByte, Map<String, List<String>> paramMap)
    {
      zzac.zzC(paramzza);
      this.zzbNL = paramzza;
      this.zzJD = paramInt;
      this.zzYI = paramThrowable;
      this.zzbNM = paramArrayOfByte;
      this.mPackageName = paramString;
      this.zzbNN = paramMap;
    }
    
    public void run()
    {
      this.zzbNL.zza(this.mPackageName, this.zzJD, this.zzYI, this.zzbNM, this.zzbNN);
    }
  }
  
  @WorkerThread
  private class zzc
    implements Runnable
  {
    private final String mPackageName;
    private final URL zzHT;
    private final byte[] zzaVP;
    private final zzaub.zza zzbNO;
    private final Map<String, String> zzbNP;
    
    public zzc(URL paramURL, byte[] paramArrayOfByte, Map<String, String> paramMap, zzaub.zza paramzza)
    {
      zzac.zzdc(paramURL);
      zzac.zzC(paramArrayOfByte);
      Object localObject;
      zzac.zzC(localObject);
      this.zzHT = paramArrayOfByte;
      this.zzaVP = paramMap;
      this.zzbNO = ((zzaub.zza)localObject);
      this.mPackageName = paramURL;
      this.zzbNP = paramzza;
    }
    
    /* Error */
    public void run()
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 27	com/google/android/gms/internal/zzaub$zzc:zzbNQ	Lcom/google/android/gms/internal/zzaub;
      //   4: invokevirtual 59	com/google/android/gms/internal/zzaub:zzLS	()V
      //   7: iconst_0
      //   8: istore 4
      //   10: iconst_0
      //   11: istore 5
      //   13: iconst_0
      //   14: istore_1
      //   15: aload_0
      //   16: getfield 27	com/google/android/gms/internal/zzaub$zzc:zzbNQ	Lcom/google/android/gms/internal/zzaub;
      //   19: aload_0
      //   20: getfield 42	com/google/android/gms/internal/zzaub$zzc:zzHT	Ljava/net/URL;
      //   23: invokevirtual 62	com/google/android/gms/internal/zzaub:zzc	(Ljava/net/URL;)Ljava/net/HttpURLConnection;
      //   26: astore 6
      //   28: iload_1
      //   29: istore_2
      //   30: iload 5
      //   32: istore_3
      //   33: aload_0
      //   34: getfield 50	com/google/android/gms/internal/zzaub$zzc:zzbNP	Ljava/util/Map;
      //   37: ifnull +160 -> 197
      //   40: iload_1
      //   41: istore_2
      //   42: iload 5
      //   44: istore_3
      //   45: aload_0
      //   46: getfield 50	com/google/android/gms/internal/zzaub$zzc:zzbNP	Ljava/util/Map;
      //   49: invokeinterface 68 1 0
      //   54: invokeinterface 74 1 0
      //   59: astore 7
      //   61: iload_1
      //   62: istore_2
      //   63: iload 5
      //   65: istore_3
      //   66: aload 7
      //   68: invokeinterface 80 1 0
      //   73: ifeq +124 -> 197
      //   76: iload_1
      //   77: istore_2
      //   78: iload 5
      //   80: istore_3
      //   81: aload 7
      //   83: invokeinterface 84 1 0
      //   88: checkcast 86	java/util/Map$Entry
      //   91: astore 8
      //   93: iload_1
      //   94: istore_2
      //   95: iload 5
      //   97: istore_3
      //   98: aload 6
      //   100: aload 8
      //   102: invokeinterface 89 1 0
      //   107: checkcast 91	java/lang/String
      //   110: aload 8
      //   112: invokeinterface 94 1 0
      //   117: checkcast 91	java/lang/String
      //   120: invokevirtual 100	java/net/HttpURLConnection:addRequestProperty	(Ljava/lang/String;Ljava/lang/String;)V
      //   123: goto -62 -> 61
      //   126: astore 8
      //   128: aconst_null
      //   129: astore 9
      //   131: iload_2
      //   132: istore_1
      //   133: aconst_null
      //   134: astore 10
      //   136: aload 6
      //   138: astore 7
      //   140: aload 10
      //   142: astore 6
      //   144: aload 6
      //   146: ifnull +8 -> 154
      //   149: aload 6
      //   151: invokevirtual 105	java/io/OutputStream:close	()V
      //   154: aload 7
      //   156: ifnull +8 -> 164
      //   159: aload 7
      //   161: invokevirtual 108	java/net/HttpURLConnection:disconnect	()V
      //   164: aload_0
      //   165: getfield 27	com/google/android/gms/internal/zzaub$zzc:zzbNQ	Lcom/google/android/gms/internal/zzaub;
      //   168: invokevirtual 112	com/google/android/gms/internal/zzaub:zzMf	()Lcom/google/android/gms/internal/zzaug;
      //   171: new 114	com/google/android/gms/internal/zzaub$zzb
      //   174: dup
      //   175: aload_0
      //   176: getfield 48	com/google/android/gms/internal/zzaub$zzc:mPackageName	Ljava/lang/String;
      //   179: aload_0
      //   180: getfield 46	com/google/android/gms/internal/zzaub$zzc:zzbNO	Lcom/google/android/gms/internal/zzaub$zza;
      //   183: iload_1
      //   184: aload 8
      //   186: aconst_null
      //   187: aload 9
      //   189: aconst_null
      //   190: invokespecial 117	com/google/android/gms/internal/zzaub$zzb:<init>	(Ljava/lang/String;Lcom/google/android/gms/internal/zzaub$zza;ILjava/lang/Throwable;[BLjava/util/Map;Lcom/google/android/gms/internal/zzaub$1;)V
      //   193: invokevirtual 123	com/google/android/gms/internal/zzaug:zzp	(Ljava/lang/Runnable;)V
      //   196: return
      //   197: iload_1
      //   198: istore_2
      //   199: iload 5
      //   201: istore_3
      //   202: aload_0
      //   203: getfield 44	com/google/android/gms/internal/zzaub$zzc:zzaVP	[B
      //   206: ifnull +122 -> 328
      //   209: iload_1
      //   210: istore_2
      //   211: iload 5
      //   213: istore_3
      //   214: aload_0
      //   215: getfield 27	com/google/android/gms/internal/zzaub$zzc:zzbNQ	Lcom/google/android/gms/internal/zzaub;
      //   218: invokevirtual 127	com/google/android/gms/internal/zzaub:zzMc	()Lcom/google/android/gms/internal/zzauw;
      //   221: aload_0
      //   222: getfield 44	com/google/android/gms/internal/zzaub$zzc:zzaVP	[B
      //   225: invokevirtual 133	com/google/android/gms/internal/zzauw:zzk	([B)[B
      //   228: astore 8
      //   230: iload_1
      //   231: istore_2
      //   232: iload 5
      //   234: istore_3
      //   235: aload_0
      //   236: getfield 27	com/google/android/gms/internal/zzaub$zzc:zzbNQ	Lcom/google/android/gms/internal/zzaub;
      //   239: invokevirtual 137	com/google/android/gms/internal/zzaub:zzMg	()Lcom/google/android/gms/internal/zzaua;
      //   242: invokevirtual 143	com/google/android/gms/internal/zzaua:zzNZ	()Lcom/google/android/gms/internal/zzaua$zza;
      //   245: ldc -111
      //   247: aload 8
      //   249: arraylength
      //   250: invokestatic 151	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
      //   253: invokevirtual 157	com/google/android/gms/internal/zzaua$zza:zzm	(Ljava/lang/String;Ljava/lang/Object;)V
      //   256: iload_1
      //   257: istore_2
      //   258: iload 5
      //   260: istore_3
      //   261: aload 6
      //   263: iconst_1
      //   264: invokevirtual 161	java/net/HttpURLConnection:setDoOutput	(Z)V
      //   267: iload_1
      //   268: istore_2
      //   269: iload 5
      //   271: istore_3
      //   272: aload 6
      //   274: ldc -93
      //   276: ldc -91
      //   278: invokevirtual 100	java/net/HttpURLConnection:addRequestProperty	(Ljava/lang/String;Ljava/lang/String;)V
      //   281: iload_1
      //   282: istore_2
      //   283: iload 5
      //   285: istore_3
      //   286: aload 6
      //   288: aload 8
      //   290: arraylength
      //   291: invokevirtual 169	java/net/HttpURLConnection:setFixedLengthStreamingMode	(I)V
      //   294: iload_1
      //   295: istore_2
      //   296: iload 5
      //   298: istore_3
      //   299: aload 6
      //   301: invokevirtual 172	java/net/HttpURLConnection:connect	()V
      //   304: iload_1
      //   305: istore_2
      //   306: iload 5
      //   308: istore_3
      //   309: aload 6
      //   311: invokevirtual 176	java/net/HttpURLConnection:getOutputStream	()Ljava/io/OutputStream;
      //   314: astore 7
      //   316: aload 7
      //   318: aload 8
      //   320: invokevirtual 180	java/io/OutputStream:write	([B)V
      //   323: aload 7
      //   325: invokevirtual 105	java/io/OutputStream:close	()V
      //   328: iload_1
      //   329: istore_2
      //   330: iload 5
      //   332: istore_3
      //   333: aload 6
      //   335: invokevirtual 184	java/net/HttpURLConnection:getResponseCode	()I
      //   338: istore_1
      //   339: iload_1
      //   340: istore_2
      //   341: iload_1
      //   342: istore_3
      //   343: aload 6
      //   345: invokevirtual 188	java/net/HttpURLConnection:getHeaderFields	()Ljava/util/Map;
      //   348: astore 8
      //   350: aload_0
      //   351: getfield 27	com/google/android/gms/internal/zzaub$zzc:zzbNQ	Lcom/google/android/gms/internal/zzaub;
      //   354: aload 6
      //   356: invokestatic 192	com/google/android/gms/internal/zzaub:zza	(Lcom/google/android/gms/internal/zzaub;Ljava/net/HttpURLConnection;)[B
      //   359: astore 7
      //   361: aload 6
      //   363: ifnull +8 -> 371
      //   366: aload 6
      //   368: invokevirtual 108	java/net/HttpURLConnection:disconnect	()V
      //   371: aload_0
      //   372: getfield 27	com/google/android/gms/internal/zzaub$zzc:zzbNQ	Lcom/google/android/gms/internal/zzaub;
      //   375: invokevirtual 112	com/google/android/gms/internal/zzaub:zzMf	()Lcom/google/android/gms/internal/zzaug;
      //   378: new 114	com/google/android/gms/internal/zzaub$zzb
      //   381: dup
      //   382: aload_0
      //   383: getfield 48	com/google/android/gms/internal/zzaub$zzc:mPackageName	Ljava/lang/String;
      //   386: aload_0
      //   387: getfield 46	com/google/android/gms/internal/zzaub$zzc:zzbNO	Lcom/google/android/gms/internal/zzaub$zza;
      //   390: iload_1
      //   391: aconst_null
      //   392: aload 7
      //   394: aload 8
      //   396: aconst_null
      //   397: invokespecial 117	com/google/android/gms/internal/zzaub$zzb:<init>	(Ljava/lang/String;Lcom/google/android/gms/internal/zzaub$zza;ILjava/lang/Throwable;[BLjava/util/Map;Lcom/google/android/gms/internal/zzaub$1;)V
      //   400: invokevirtual 123	com/google/android/gms/internal/zzaug:zzp	(Ljava/lang/Runnable;)V
      //   403: return
      //   404: astore 6
      //   406: aload_0
      //   407: getfield 27	com/google/android/gms/internal/zzaub$zzc:zzbNQ	Lcom/google/android/gms/internal/zzaub;
      //   410: invokevirtual 137	com/google/android/gms/internal/zzaub:zzMg	()Lcom/google/android/gms/internal/zzaua;
      //   413: invokevirtual 195	com/google/android/gms/internal/zzaua:zzNT	()Lcom/google/android/gms/internal/zzaua$zza;
      //   416: ldc -59
      //   418: aload_0
      //   419: getfield 48	com/google/android/gms/internal/zzaub$zzc:mPackageName	Ljava/lang/String;
      //   422: invokestatic 201	com/google/android/gms/internal/zzaua:zzfH	(Ljava/lang/String;)Ljava/lang/Object;
      //   425: aload 6
      //   427: invokevirtual 205	com/google/android/gms/internal/zzaua$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
      //   430: goto -276 -> 154
      //   433: astore 8
      //   435: aconst_null
      //   436: astore 6
      //   438: aconst_null
      //   439: astore 9
      //   441: aconst_null
      //   442: astore 7
      //   444: iload 4
      //   446: istore_1
      //   447: aload 7
      //   449: ifnull +8 -> 457
      //   452: aload 7
      //   454: invokevirtual 105	java/io/OutputStream:close	()V
      //   457: aload 9
      //   459: ifnull +8 -> 467
      //   462: aload 9
      //   464: invokevirtual 108	java/net/HttpURLConnection:disconnect	()V
      //   467: aload_0
      //   468: getfield 27	com/google/android/gms/internal/zzaub$zzc:zzbNQ	Lcom/google/android/gms/internal/zzaub;
      //   471: invokevirtual 112	com/google/android/gms/internal/zzaub:zzMf	()Lcom/google/android/gms/internal/zzaug;
      //   474: new 114	com/google/android/gms/internal/zzaub$zzb
      //   477: dup
      //   478: aload_0
      //   479: getfield 48	com/google/android/gms/internal/zzaub$zzc:mPackageName	Ljava/lang/String;
      //   482: aload_0
      //   483: getfield 46	com/google/android/gms/internal/zzaub$zzc:zzbNO	Lcom/google/android/gms/internal/zzaub$zza;
      //   486: iload_1
      //   487: aconst_null
      //   488: aconst_null
      //   489: aload 6
      //   491: aconst_null
      //   492: invokespecial 117	com/google/android/gms/internal/zzaub$zzb:<init>	(Ljava/lang/String;Lcom/google/android/gms/internal/zzaub$zza;ILjava/lang/Throwable;[BLjava/util/Map;Lcom/google/android/gms/internal/zzaub$1;)V
      //   495: invokevirtual 123	com/google/android/gms/internal/zzaug:zzp	(Ljava/lang/Runnable;)V
      //   498: aload 8
      //   500: athrow
      //   501: astore 7
      //   503: aload_0
      //   504: getfield 27	com/google/android/gms/internal/zzaub$zzc:zzbNQ	Lcom/google/android/gms/internal/zzaub;
      //   507: invokevirtual 137	com/google/android/gms/internal/zzaub:zzMg	()Lcom/google/android/gms/internal/zzaua;
      //   510: invokevirtual 195	com/google/android/gms/internal/zzaua:zzNT	()Lcom/google/android/gms/internal/zzaua$zza;
      //   513: ldc -59
      //   515: aload_0
      //   516: getfield 48	com/google/android/gms/internal/zzaub$zzc:mPackageName	Ljava/lang/String;
      //   519: invokestatic 201	com/google/android/gms/internal/zzaua:zzfH	(Ljava/lang/String;)Ljava/lang/Object;
      //   522: aload 7
      //   524: invokevirtual 205	com/google/android/gms/internal/zzaua$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
      //   527: goto -70 -> 457
      //   530: astore 8
      //   532: aconst_null
      //   533: astore 10
      //   535: aconst_null
      //   536: astore 7
      //   538: aload 6
      //   540: astore 9
      //   542: iload_3
      //   543: istore_1
      //   544: aload 10
      //   546: astore 6
      //   548: goto -101 -> 447
      //   551: astore 8
      //   553: aconst_null
      //   554: astore 10
      //   556: aload 6
      //   558: astore 9
      //   560: iload 4
      //   562: istore_1
      //   563: aload 10
      //   565: astore 6
      //   567: goto -120 -> 447
      //   570: astore 10
      //   572: aconst_null
      //   573: astore 7
      //   575: aload 6
      //   577: astore 9
      //   579: aload 8
      //   581: astore 6
      //   583: aload 10
      //   585: astore 8
      //   587: goto -140 -> 447
      //   590: astore 8
      //   592: aconst_null
      //   593: astore 9
      //   595: iconst_0
      //   596: istore_1
      //   597: aconst_null
      //   598: astore 6
      //   600: aconst_null
      //   601: astore 7
      //   603: goto -459 -> 144
      //   606: astore 10
      //   608: aconst_null
      //   609: astore 9
      //   611: iconst_0
      //   612: istore_1
      //   613: aload 6
      //   615: astore 8
      //   617: aload 7
      //   619: astore 6
      //   621: aload 8
      //   623: astore 7
      //   625: aload 10
      //   627: astore 8
      //   629: goto -485 -> 144
      //   632: astore 10
      //   634: aload 8
      //   636: astore 9
      //   638: aconst_null
      //   639: astore 8
      //   641: aload 6
      //   643: astore 7
      //   645: aload 8
      //   647: astore 6
      //   649: aload 10
      //   651: astore 8
      //   653: goto -509 -> 144
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	656	0	this	zzc
      //   14	599	1	i	int
      //   29	312	2	j	int
      //   32	511	3	k	int
      //   8	553	4	m	int
      //   11	320	5	n	int
      //   26	341	6	localObject1	Object
      //   404	22	6	localIOException1	IOException
      //   436	212	6	localObject2	Object
      //   59	394	7	localObject3	Object
      //   501	22	7	localIOException2	IOException
      //   536	108	7	localObject4	Object
      //   91	20	8	localEntry	java.util.Map.Entry
      //   126	59	8	localIOException3	IOException
      //   228	167	8	localObject5	Object
      //   433	66	8	localObject6	Object
      //   530	1	8	localObject7	Object
      //   551	29	8	localObject8	Object
      //   585	1	8	localObject9	Object
      //   590	1	8	localIOException4	IOException
      //   615	37	8	localObject10	Object
      //   129	508	9	localObject11	Object
      //   134	430	10	localObject12	Object
      //   570	14	10	localObject13	Object
      //   606	20	10	localIOException5	IOException
      //   632	18	10	localIOException6	IOException
      // Exception table:
      //   from	to	target	type
      //   33	40	126	java/io/IOException
      //   45	61	126	java/io/IOException
      //   66	76	126	java/io/IOException
      //   81	93	126	java/io/IOException
      //   98	123	126	java/io/IOException
      //   202	209	126	java/io/IOException
      //   214	230	126	java/io/IOException
      //   235	256	126	java/io/IOException
      //   261	267	126	java/io/IOException
      //   272	281	126	java/io/IOException
      //   286	294	126	java/io/IOException
      //   299	304	126	java/io/IOException
      //   309	316	126	java/io/IOException
      //   333	339	126	java/io/IOException
      //   343	350	126	java/io/IOException
      //   149	154	404	java/io/IOException
      //   15	28	433	finally
      //   452	457	501	java/io/IOException
      //   33	40	530	finally
      //   45	61	530	finally
      //   66	76	530	finally
      //   81	93	530	finally
      //   98	123	530	finally
      //   202	209	530	finally
      //   214	230	530	finally
      //   235	256	530	finally
      //   261	267	530	finally
      //   272	281	530	finally
      //   286	294	530	finally
      //   299	304	530	finally
      //   309	316	530	finally
      //   333	339	530	finally
      //   343	350	530	finally
      //   316	328	551	finally
      //   350	361	570	finally
      //   15	28	590	java/io/IOException
      //   316	328	606	java/io/IOException
      //   350	361	632	java/io/IOException
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/internal/zzaub.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */