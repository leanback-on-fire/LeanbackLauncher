package com.google.android.gms.internal;

import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.data.DataBuffer;
import com.google.android.gms.common.data.DataHolder;
import com.google.android.gms.common.data.zzd;
import com.google.android.gms.config.Config;
import com.google.android.gms.config.ConfigApi;
import com.google.android.gms.config.ConfigApi.FetchConfigRequest;
import com.google.android.gms.config.ConfigApi.FetchConfigResult;
import com.google.android.gms.config.ConfigApi.GetValueResult;
import com.google.android.gms.config.ConfigApi.GetValuesResult;
import com.google.android.gms.config.ConfigApi.Value;
import com.google.android.gms.config.ConfigStatusCodes;
import com.google.android.gms.phenotype.Configuration;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class zzace
  implements ConfigApi
{
  private static final Charset UTF_8 = Charset.forName("UTF-8");
  private static final Pattern zzaVw = Pattern.compile("^(1|true|t|yes|y|on)$", 2);
  private static final Pattern zzaVx = Pattern.compile("^(0|false|f|no|n|off|)$", 2);
  
  private static HashMap<String, TreeMap<String, byte[]>> zza(zzacn paramzzacn)
  {
    if (paramzzacn == null) {}
    do
    {
      return null;
      localObject1 = paramzzacn.zzBw();
    } while (localObject1 == null);
    Object localObject1 = (zzacr)new zzd((DataHolder)localObject1, zzacr.CREATOR).get(0);
    paramzzacn.zzBy();
    paramzzacn = new HashMap();
    Iterator localIterator1 = ((zzacr)localObject1).zzBA().keySet().iterator();
    while (localIterator1.hasNext())
    {
      Object localObject2 = (String)localIterator1.next();
      TreeMap localTreeMap = new TreeMap();
      paramzzacn.put(localObject2, localTreeMap);
      localObject2 = ((zzacr)localObject1).zzBA().getBundle((String)localObject2);
      Iterator localIterator2 = ((Bundle)localObject2).keySet().iterator();
      while (localIterator2.hasNext())
      {
        String str = (String)localIterator2.next();
        localTreeMap.put(str, ((Bundle)localObject2).getByteArray(str));
      }
    }
    return paramzzacn;
  }
  
  @Nullable
  static List<byte[]> zzb(@Nullable zzacn paramzzacn)
  {
    if (paramzzacn == null) {}
    do
    {
      return null;
      localObject = paramzzacn.zzBx();
    } while (localObject == null);
    ArrayList localArrayList = new ArrayList();
    Object localObject = new zzd((DataHolder)localObject, zzach.CREATOR).iterator();
    while (((Iterator)localObject).hasNext()) {
      localArrayList.add(((zzach)((Iterator)localObject).next()).getPayload());
    }
    paramzzacn.zzBz();
    return localArrayList;
  }
  
  private static Status zzgC(int paramInt)
  {
    return new Status(paramInt, ConfigStatusCodes.getStatusCodeString(paramInt));
  }
  
  public PendingResult<ConfigApi.FetchConfigResult> fetchConfig(GoogleApiClient paramGoogleApiClient, final ConfigApi.FetchConfigRequest paramFetchConfigRequest)
  {
    if ((paramGoogleApiClient == null) || (paramFetchConfigRequest == null)) {
      return null;
    }
    paramGoogleApiClient.zza(new zzc(paramGoogleApiClient)
    {
      protected ConfigApi.FetchConfigResult zzY(Status paramAnonymousStatus)
      {
        return new zzace.zzd(paramAnonymousStatus, new HashMap());
      }
      
      /* Error */
      protected void zza(Context paramAnonymousContext, zzacq paramAnonymouszzacq)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 43	com/google/android/gms/common/data/zzd:zzzc	()Lcom/google/android/gms/common/data/DataHolder$Builder;
        //   3: astore_3
        //   4: aload_0
        //   5: getfield 15	com/google/android/gms/internal/zzace$1:zzaVy	Lcom/google/android/gms/config/ConfigApi$FetchConfigRequest;
        //   8: invokevirtual 49	com/google/android/gms/config/ConfigApi$FetchConfigRequest:getCustomVariables	()Ljava/util/Map;
        //   11: invokeinterface 55 1 0
        //   16: invokeinterface 61 1 0
        //   21: astore 4
        //   23: aload 4
        //   25: invokeinterface 67 1 0
        //   30: ifeq +49 -> 79
        //   33: aload 4
        //   35: invokeinterface 71 1 0
        //   40: checkcast 73	java/util/Map$Entry
        //   43: astore 5
        //   45: aload_3
        //   46: new 75	com/google/android/gms/internal/zzacj
        //   49: dup
        //   50: aload 5
        //   52: invokeinterface 78 1 0
        //   57: checkcast 80	java/lang/String
        //   60: aload 5
        //   62: invokeinterface 83 1 0
        //   67: checkcast 80	java/lang/String
        //   70: invokespecial 86	com/google/android/gms/internal/zzacj:<init>	(Ljava/lang/String;Ljava/lang/String;)V
        //   73: invokestatic 89	com/google/android/gms/common/data/zzd:zza	(Lcom/google/android/gms/common/data/DataHolder$Builder;Lcom/google/android/gms/common/internal/safeparcel/SafeParcelable;)V
        //   76: goto -53 -> 23
        //   79: aload_3
        //   80: iconst_0
        //   81: invokevirtual 95	com/google/android/gms/common/data/DataHolder$Builder:build	(I)Lcom/google/android/gms/common/data/DataHolder;
        //   84: astore 6
        //   86: aload_1
        //   87: invokestatic 101	com/google/android/gms/internal/zzzo:zzaW	(Landroid/content/Context;)Lcom/google/android/gms/common/api/Status;
        //   90: getstatic 107	com/google/android/gms/common/api/Status:zzaLc	Lcom/google/android/gms/common/api/Status;
        //   93: if_acmpne +96 -> 189
        //   96: invokestatic 111	com/google/android/gms/internal/zzzo:zzyC	()Ljava/lang/String;
        //   99: astore 4
        //   101: invokestatic 117	com/google/firebase/iid/zzc:zzake	()Lcom/google/firebase/iid/zzc;
        //   104: invokevirtual 120	com/google/firebase/iid/zzc:getId	()Ljava/lang/String;
        //   107: astore_3
        //   108: invokestatic 117	com/google/firebase/iid/zzc:zzake	()Lcom/google/firebase/iid/zzc;
        //   111: invokevirtual 123	com/google/firebase/iid/zzc:getToken	()Ljava/lang/String;
        //   114: astore 5
        //   116: aload_1
        //   117: invokestatic 129	com/google/android/gms/internal/zzacd:zzbr	(Landroid/content/Context;)Ljava/util/List;
        //   120: astore 7
        //   122: new 131	com/google/android/gms/internal/zzacl
        //   125: dup
        //   126: aload_1
        //   127: invokevirtual 136	android/content/Context:getPackageName	()Ljava/lang/String;
        //   130: aload_0
        //   131: getfield 15	com/google/android/gms/internal/zzace$1:zzaVy	Lcom/google/android/gms/config/ConfigApi$FetchConfigRequest;
        //   134: invokevirtual 140	com/google/android/gms/config/ConfigApi$FetchConfigRequest:getCacheExpirationSeconds	()J
        //   137: aload 6
        //   139: aload 4
        //   141: aload_3
        //   142: aload 5
        //   144: aconst_null
        //   145: aload_0
        //   146: getfield 15	com/google/android/gms/internal/zzace$1:zzaVy	Lcom/google/android/gms/config/ConfigApi$FetchConfigRequest;
        //   149: invokevirtual 144	com/google/android/gms/config/ConfigApi$FetchConfigRequest:zzBj	()I
        //   152: aload 7
        //   154: aload_0
        //   155: getfield 15	com/google/android/gms/internal/zzace$1:zzaVy	Lcom/google/android/gms/config/ConfigApi$FetchConfigRequest;
        //   158: invokevirtual 147	com/google/android/gms/config/ConfigApi$FetchConfigRequest:zzBk	()I
        //   161: aload_0
        //   162: getfield 15	com/google/android/gms/internal/zzace$1:zzaVy	Lcom/google/android/gms/config/ConfigApi$FetchConfigRequest;
        //   165: invokevirtual 150	com/google/android/gms/config/ConfigApi$FetchConfigRequest:zzBl	()I
        //   168: invokespecial 153	com/google/android/gms/internal/zzacl:<init>	(Ljava/lang/String;JLcom/google/android/gms/common/data/DataHolder;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/util/List;ILjava/util/List;II)V
        //   171: astore_1
        //   172: aload_2
        //   173: aload_0
        //   174: getfield 157	com/google/android/gms/internal/zzace$1:zzaVE	Lcom/google/android/gms/internal/zzacp;
        //   177: aload_1
        //   178: invokeinterface 162 3 0
        //   183: aload 6
        //   185: invokevirtual 167	com/google/android/gms/common/data/DataHolder:close	()V
        //   188: return
        //   189: aconst_null
        //   190: astore 4
        //   192: goto -91 -> 101
        //   195: astore 5
        //   197: aconst_null
        //   198: astore_3
        //   199: ldc -87
        //   201: iconst_3
        //   202: invokestatic 175	android/util/Log:isLoggable	(Ljava/lang/String;I)Z
        //   205: ifeq +13 -> 218
        //   208: ldc -87
        //   210: ldc -79
        //   212: aload 5
        //   214: invokestatic 181	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
        //   217: pop
        //   218: aconst_null
        //   219: astore 5
        //   221: goto -105 -> 116
        //   224: astore_1
        //   225: aload 6
        //   227: invokevirtual 167	com/google/android/gms/common/data/DataHolder:close	()V
        //   230: aload_1
        //   231: athrow
        //   232: astore 5
        //   234: goto -35 -> 199
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	237	0	this	1
        //   0	237	1	paramAnonymousContext	Context
        //   0	237	2	paramAnonymouszzacq	zzacq
        //   3	196	3	localObject1	Object
        //   21	170	4	localObject2	Object
        //   43	100	5	localObject3	Object
        //   195	18	5	localIllegalStateException1	IllegalStateException
        //   219	1	5	localObject4	Object
        //   232	1	5	localIllegalStateException2	IllegalStateException
        //   84	142	6	localDataHolder	DataHolder
        //   120	33	7	localList	List
        // Exception table:
        //   from	to	target	type
        //   101	108	195	java/lang/IllegalStateException
        //   172	183	224	finally
        //   108	116	232	java/lang/IllegalStateException
      }
    });
  }
  
  public PendingResult<ConfigApi.GetValueResult> getValue(GoogleApiClient paramGoogleApiClient, String paramString)
  {
    return getValue(paramGoogleApiClient, null, paramString);
  }
  
  public PendingResult<ConfigApi.GetValueResult> getValue(GoogleApiClient paramGoogleApiClient, final String paramString1, final String paramString2)
  {
    paramGoogleApiClient.zza(new zze(paramGoogleApiClient)
    {
      protected void zza(Context paramAnonymousContext, zzacq paramAnonymouszzacq)
        throws RemoteException
      {
        if (paramString1 != null) {}
        for (paramAnonymousContext = paramString1;; paramAnonymousContext = paramAnonymousContext.getPackageName())
        {
          paramAnonymouszzacq.zza(this.zzaVE, paramAnonymousContext, paramString2);
          return;
        }
      }
      
      protected ConfigApi.GetValueResult zzaa(Status paramAnonymousStatus)
      {
        return new zzace.zzf(paramAnonymousStatus, null);
      }
    });
  }
  
  public PendingResult<ConfigApi.GetValuesResult> getValues(GoogleApiClient paramGoogleApiClient, final String paramString, final List<String> paramList)
  {
    paramGoogleApiClient.zza(new zzg(paramGoogleApiClient)
    {
      protected void zza(Context paramAnonymousContext, zzacq paramAnonymouszzacq)
        throws RemoteException
      {
        if (paramString != null) {}
        for (paramAnonymousContext = paramString;; paramAnonymousContext = paramAnonymousContext.getPackageName())
        {
          paramAnonymouszzacq.zza(this.zzaVE, paramAnonymousContext, paramList);
          return;
        }
      }
      
      protected ConfigApi.GetValuesResult zzab(Status paramAnonymousStatus)
      {
        return new zzace.zzh(paramAnonymousStatus, null);
      }
    });
  }
  
  public PendingResult<ConfigApi.GetValuesResult> getValues(GoogleApiClient paramGoogleApiClient, List<String> paramList)
  {
    return getValues(paramGoogleApiClient, null, paramList);
  }
  
  public PendingResult<ConfigApi.GetValuesResult> getValuesByPrefix(GoogleApiClient paramGoogleApiClient, String paramString)
  {
    return getValuesByPrefix(paramGoogleApiClient, null, paramString);
  }
  
  public PendingResult<ConfigApi.GetValuesResult> getValuesByPrefix(GoogleApiClient paramGoogleApiClient, final String paramString1, final String paramString2)
  {
    paramGoogleApiClient.zza(new zzg(paramGoogleApiClient)
    {
      protected void zza(Context paramAnonymousContext, zzacq paramAnonymouszzacq)
        throws RemoteException
      {
        if (paramString1 != null) {}
        for (paramAnonymousContext = paramString1;; paramAnonymousContext = paramAnonymousContext.getPackageName())
        {
          paramAnonymouszzacq.zzb(this.zzaVE, paramAnonymousContext, paramString2);
          return;
        }
      }
      
      protected ConfigApi.GetValuesResult zzab(Status paramAnonymousStatus)
      {
        return new zzace.zzh(paramAnonymousStatus, null);
      }
    });
  }
  
  public PendingResult<Status> setPhenotypeValues(GoogleApiClient paramGoogleApiClient, final Configuration paramConfiguration, final String paramString)
  {
    paramGoogleApiClient.zzb(new zzb(paramGoogleApiClient)
    {
      protected void zza(Context paramAnonymousContext, zzacq paramAnonymouszzacq)
        throws RemoteException
      {
        paramAnonymouszzacq.zza(new zzace.zza()
        {
          public void zzZ(Status paramAnonymous2Status)
          {
            zzace.2.this.zzb(paramAnonymous2Status);
          }
        }, paramAnonymousContext.getPackageName(), paramConfiguration, paramString);
      }
      
      protected Status zzd(Status paramAnonymousStatus)
      {
        return paramAnonymousStatus;
      }
    });
  }
  
  static abstract class zza
    extends zzacp.zza
  {
    public void zzZ(Status paramStatus)
    {
      throw new UnsupportedOperationException();
    }
    
    public void zza(Status paramStatus, zzacn paramzzacn)
    {
      throw new UnsupportedOperationException();
    }
    
    public void zza(Status paramStatus, Map paramMap)
    {
      throw new UnsupportedOperationException();
    }
    
    public void zza(Status paramStatus, byte[] paramArrayOfByte)
    {
      throw new UnsupportedOperationException();
    }
  }
  
  static abstract class zzb<R extends Result>
    extends zzyr.zza<R, zzacg>
  {
    public zzb(GoogleApiClient paramGoogleApiClient)
    {
      super(paramGoogleApiClient);
    }
    
    protected abstract void zza(Context paramContext, zzacq paramzzacq)
      throws RemoteException;
    
    protected final void zza(zzacg paramzzacg)
      throws RemoteException
    {
      zza(paramzzacg.getContext(), (zzacq)paramzzacg.zzzw());
    }
  }
  
  static abstract class zzc
    extends zzace.zzb<ConfigApi.FetchConfigResult>
  {
    protected zzacp zzaVE = new zzace.zza()
    {
      public void zza(Status paramAnonymousStatus, zzacn paramAnonymouszzacn)
      {
        if ((paramAnonymouszzacn.getStatusCode() == 6502) || (paramAnonymouszzacn.getStatusCode() == 6507))
        {
          zzace.zzc.this.zzb(new zzace.zzd(zzace.zzgD(paramAnonymouszzacn.getStatusCode()), zzace.zzc(paramAnonymouszzacn), paramAnonymouszzacn.getThrottleEndTimeMillis(), zzace.zzb(paramAnonymouszzacn)));
          return;
        }
        zzace.zzc.this.zzb(new zzace.zzd(zzace.zzgD(paramAnonymouszzacn.getStatusCode()), zzace.zzc(paramAnonymouszzacn), zzace.zzb(paramAnonymouszzacn)));
      }
    };
    
    public zzc(GoogleApiClient paramGoogleApiClient)
    {
      super();
    }
  }
  
  public static class zzd
    implements ConfigApi.FetchConfigResult
  {
    private final Map<String, TreeMap<String, byte[]>> zzaVG;
    private final long zzaVH;
    private final List<byte[]> zzaVI;
    private final Status zzaiT;
    
    public zzd(Status paramStatus, Map<String, TreeMap<String, byte[]>> paramMap)
    {
      this(paramStatus, paramMap, -1L);
    }
    
    public zzd(Status paramStatus, Map<String, TreeMap<String, byte[]>> paramMap, long paramLong)
    {
      this(paramStatus, paramMap, paramLong, null);
    }
    
    public zzd(Status paramStatus, Map<String, TreeMap<String, byte[]>> paramMap, long paramLong, List<byte[]> paramList)
    {
      this.zzaiT = paramStatus;
      this.zzaVG = paramMap;
      this.zzaVH = paramLong;
      this.zzaVI = paramList;
    }
    
    public zzd(Status paramStatus, Map<String, TreeMap<String, byte[]>> paramMap, List<byte[]> paramList)
    {
      this(paramStatus, paramMap, -1L, paramList);
    }
    
    public Map<String, Set<String>> getAllConfigKeys()
    {
      HashMap localHashMap = new HashMap();
      if (this.zzaVG != null)
      {
        Iterator localIterator = this.zzaVG.keySet().iterator();
        while (localIterator.hasNext())
        {
          String str = (String)localIterator.next();
          Map localMap = (Map)this.zzaVG.get(str);
          if (localMap != null) {
            localHashMap.put(str, localMap.keySet());
          }
        }
      }
      return localHashMap;
    }
    
    public boolean getAsBoolean(String paramString, boolean paramBoolean)
    {
      return getAsBoolean(paramString, paramBoolean, "configns:firebase");
    }
    
    public boolean getAsBoolean(String paramString1, boolean paramBoolean, String paramString2)
    {
      boolean bool = paramBoolean;
      if (hasConfiguredValue(paramString1, paramString2))
      {
        paramString1 = new String((byte[])((TreeMap)this.zzaVG.get(paramString2)).get(paramString1), zzace.zzBn());
        if (!zzace.zzBo().matcher(paramString1).matches()) {
          break label62;
        }
        bool = true;
      }
      label62:
      do
      {
        return bool;
        bool = paramBoolean;
      } while (!zzace.zzBp().matcher(paramString1).matches());
      return false;
    }
    
    public byte[] getAsByteArray(String paramString, byte[] paramArrayOfByte)
    {
      return getAsByteArray(paramString, paramArrayOfByte, "configns:firebase");
    }
    
    public byte[] getAsByteArray(String paramString1, byte[] paramArrayOfByte, String paramString2)
    {
      if (hasConfiguredValue(paramString1, paramString2)) {
        return (byte[])((TreeMap)this.zzaVG.get(paramString2)).get(paramString1);
      }
      return paramArrayOfByte;
    }
    
    public long getAsLong(String paramString, long paramLong)
    {
      return getAsLong(paramString, paramLong, "configns:firebase");
    }
    
    public long getAsLong(String paramString1, long paramLong, String paramString2)
    {
      long l = paramLong;
      if (hasConfiguredValue(paramString1, paramString2)) {
        paramString1 = (byte[])((TreeMap)this.zzaVG.get(paramString2)).get(paramString1);
      }
      try
      {
        l = Long.parseLong(new String(paramString1, zzace.zzBn()));
        return l;
      }
      catch (NumberFormatException paramString1) {}
      return paramLong;
    }
    
    public String getAsString(String paramString1, String paramString2)
    {
      return getAsString(paramString1, paramString2, "configns:firebase");
    }
    
    public String getAsString(String paramString1, String paramString2, String paramString3)
    {
      if (hasConfiguredValue(paramString1, paramString3)) {
        paramString2 = new String((byte[])((TreeMap)this.zzaVG.get(paramString3)).get(paramString1), zzace.zzBn());
      }
      return paramString2;
    }
    
    public Status getStatus()
    {
      return this.zzaiT;
    }
    
    public long getThrottleEndTimeMillis()
    {
      return this.zzaVH;
    }
    
    public boolean hasConfiguredValue(String paramString)
    {
      return hasConfiguredValue(paramString, "configns:firebase");
    }
    
    public boolean hasConfiguredValue(String paramString1, String paramString2)
    {
      if ((this.zzaVG == null) || (this.zzaVG.get(paramString2) == null)) {
        return false;
      }
      return ((TreeMap)this.zzaVG.get(paramString2)).get(paramString1) != null;
    }
    
    public List<byte[]> zzBm()
    {
      return this.zzaVI;
    }
  }
  
  static abstract class zze
    extends zzace.zzb<ConfigApi.GetValueResult>
  {
    protected zzacp zzaVE = new zzace.zza()
    {
      public void zza(Status paramAnonymousStatus, byte[] paramAnonymousArrayOfByte)
      {
        zzace.zze localzze = zzace.zze.this;
        if (paramAnonymousArrayOfByte != null) {}
        for (boolean bool = true;; bool = false)
        {
          localzze.zzb(new zzace.zzf(paramAnonymousStatus, new zzace.zzi(paramAnonymousArrayOfByte, bool)));
          return;
        }
      }
    };
    
    public zze(GoogleApiClient paramGoogleApiClient)
    {
      super();
    }
  }
  
  static class zzf
    implements ConfigApi.GetValueResult
  {
    private final ConfigApi.Value zzaVK;
    private final Status zzaiT;
    
    public zzf(Status paramStatus, ConfigApi.Value paramValue)
    {
      this.zzaiT = paramStatus;
      this.zzaVK = paramValue;
    }
    
    public Status getStatus()
    {
      return this.zzaiT;
    }
    
    public ConfigApi.Value getValue()
    {
      return this.zzaVK;
    }
  }
  
  static abstract class zzg
    extends zzace.zzb<ConfigApi.GetValuesResult>
  {
    protected zzacp zzaVE = new zzace.zza()
    {
      public void zza(Status paramAnonymousStatus, Map paramAnonymousMap)
      {
        if (paramAnonymousMap == null)
        {
          zzace.zzg.this.zzb(new zzace.zzh(paramAnonymousStatus, new HashMap()));
          return;
        }
        HashMap localHashMap = new HashMap();
        paramAnonymousMap = paramAnonymousMap.entrySet().iterator();
        if (paramAnonymousMap.hasNext())
        {
          Map.Entry localEntry = (Map.Entry)paramAnonymousMap.next();
          String str = (String)localEntry.getKey();
          byte[] arrayOfByte = (byte[])localEntry.getValue();
          if (localEntry.getValue() != null) {}
          for (boolean bool = true;; bool = false)
          {
            localHashMap.put(str, new zzace.zzi(arrayOfByte, bool));
            break;
          }
        }
        zzace.zzg.this.zzb(new zzace.zzh(paramAnonymousStatus, localHashMap));
      }
    };
    
    public zzg(GoogleApiClient paramGoogleApiClient)
    {
      super();
    }
  }
  
  static class zzh
    implements ConfigApi.GetValuesResult
  {
    private final Map<String, ConfigApi.Value> zzaVM;
    private final Status zzaiT;
    
    public zzh(Status paramStatus, Map<String, ConfigApi.Value> paramMap)
    {
      this.zzaiT = paramStatus;
      this.zzaVM = paramMap;
    }
    
    public Status getStatus()
    {
      return this.zzaiT;
    }
    
    public Map<String, ConfigApi.Value> getValues()
    {
      return this.zzaVM;
    }
  }
  
  static class zzi
    implements ConfigApi.Value
  {
    private final byte[] zzaVN;
    private final boolean zzaVO;
    
    public zzi(byte[] paramArrayOfByte, boolean paramBoolean)
    {
      this.zzaVN = paramArrayOfByte;
      this.zzaVO = paramBoolean;
    }
    
    public boolean getAsBoolean(boolean paramBoolean)
    {
      if ((!this.zzaVO) || (this.zzaVN == null)) {}
      String str;
      do
      {
        return paramBoolean;
        str = new String(this.zzaVN, zzace.zzBn());
        if (zzace.zzBo().matcher(str).matches()) {
          return true;
        }
      } while (!zzace.zzBp().matcher(str).matches());
      return false;
    }
    
    public byte[] getAsByteArray(byte[] paramArrayOfByte)
    {
      if (this.zzaVO) {
        paramArrayOfByte = this.zzaVN;
      }
      return paramArrayOfByte;
    }
    
    public long getAsLong(long paramLong)
    {
      if ((!this.zzaVO) || (this.zzaVN == null)) {
        return paramLong;
      }
      try
      {
        long l = Long.parseLong(new String(this.zzaVN, zzace.zzBn()));
        return l;
      }
      catch (NumberFormatException localNumberFormatException) {}
      return paramLong;
    }
    
    public String getAsString(String paramString)
    {
      if ((!this.zzaVO) || (this.zzaVN == null)) {
        return paramString;
      }
      return new String(this.zzaVN, zzace.zzBn());
    }
    
    public boolean hasConfiguredValue()
    {
      return this.zzaVO;
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/internal/zzace.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */