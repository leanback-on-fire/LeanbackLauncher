package com.google.android.gms.phenotype;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Base64;
import com.google.android.gms.internal.zzaav;
import com.google.android.gms.internal.zzbtf;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class PhenotypeFlag<T>
{
  static Context zzaJW = null;
  private static boolean zzcfA = false;
  private static zzaav<Boolean> zzcfB = zzaav.zzk("gms:phenotype:phenotype_flag:debug_bypass_phenotype", false);
  private static final Object zzuq = new Object();
  private final T zzBq;
  private T zzaeU = null;
  private final Uri zzahf;
  private final String zzcfC;
  private final String zzcfD;
  private final String zzcfE;
  
  public PhenotypeFlag(String paramString1, String paramString2, String paramString3, Uri paramUri, T paramT)
  {
    if ((paramString3 == null) && (paramUri == null)) {
      throw new IllegalArgumentException("Must pass a valid Shared Preferences file name or ContentProvider Uri");
    }
    this.zzcfC = paramString1;
    this.zzcfD = paramString2;
    this.zzcfE = paramString3;
    this.zzahf = paramUri;
    this.zzBq = paramT;
  }
  
  public static void init(Context paramContext)
  {
    synchronized (zzuq)
    {
      if (zzaJW == null)
      {
        zzaJW = paramContext.getApplicationContext();
        if (zzaJW == null) {
          zzaJW = paramContext;
        }
      }
      zzaav.init(paramContext);
      zzcfA = false;
      return;
    }
  }
  
  public static void initForTest()
  {
    zzcfA = true;
  }
  
  private static PhenotypeFlag<Double> zza(String paramString1, final String paramString2, String paramString3, Uri paramUri, double paramDouble)
  {
    new PhenotypeFlag(paramString1, paramString2, paramString3, paramUri, Double.valueOf(paramDouble))
    {
      public Double zzd(Flag paramAnonymousFlag)
      {
        return Double.valueOf(paramAnonymousFlag.getDouble());
      }
      
      public Double zzf(zzh paramAnonymouszzh)
      {
        return paramAnonymouszzh.zza(paramString2, null);
      }
      
      public Double zziO(String paramAnonymousString)
      {
        try
        {
          double d = Double.parseDouble(paramAnonymousString);
          return Double.valueOf(d);
        }
        catch (NumberFormatException paramAnonymousString) {}
        return null;
      }
      
      public Double zzj(SharedPreferences paramAnonymousSharedPreferences)
      {
        return Double.valueOf(paramAnonymousSharedPreferences.getFloat(paramString2, 0.0F));
      }
    };
  }
  
  private static PhenotypeFlag<Integer> zza(String paramString1, final String paramString2, String paramString3, Uri paramUri, int paramInt)
  {
    new PhenotypeFlag(paramString1, paramString2, paramString3, paramUri, Integer.valueOf(paramInt))
    {
      public Integer zzb(Flag paramAnonymousFlag)
      {
        return Integer.valueOf((int)paramAnonymousFlag.getLong());
      }
      
      public Integer zzd(zzh paramAnonymouszzh)
      {
        return paramAnonymouszzh.getInteger(paramString2, null);
      }
      
      public Integer zzh(SharedPreferences paramAnonymousSharedPreferences)
      {
        return Integer.valueOf((int)paramAnonymousSharedPreferences.getLong(paramString2, 0L));
      }
      
      public Integer zziM(String paramAnonymousString)
      {
        try
        {
          int i = Integer.parseInt(paramAnonymousString);
          return Integer.valueOf(i);
        }
        catch (NumberFormatException paramAnonymousString) {}
        return null;
      }
    };
  }
  
  private static PhenotypeFlag<Long> zza(String paramString1, final String paramString2, String paramString3, Uri paramUri, long paramLong)
  {
    new PhenotypeFlag(paramString1, paramString2, paramString3, paramUri, Long.valueOf(paramLong))
    {
      public Long zza(Flag paramAnonymousFlag)
      {
        return Long.valueOf(paramAnonymousFlag.getLong());
      }
      
      public Long zzc(zzh paramAnonymouszzh)
      {
        return paramAnonymouszzh.getLong(paramString2, null);
      }
      
      public Long zzg(SharedPreferences paramAnonymousSharedPreferences)
      {
        return Long.valueOf(paramAnonymousSharedPreferences.getLong(paramString2, 0L));
      }
      
      public Long zziL(String paramAnonymousString)
      {
        try
        {
          long l = Long.parseLong(paramAnonymousString);
          return Long.valueOf(l);
        }
        catch (NumberFormatException paramAnonymousString) {}
        return null;
      }
    };
  }
  
  private static PhenotypeFlag<String> zza(String paramString1, final String paramString2, String paramString3, Uri paramUri, String paramString4)
  {
    new PhenotypeFlag(paramString1, paramString2, paramString3, paramUri, paramString4)
    {
      public String zze(Flag paramAnonymousFlag)
      {
        return paramAnonymousFlag.getString();
      }
      
      public String zzg(zzh paramAnonymouszzh)
      {
        return paramAnonymouszzh.getString(paramString2, null);
      }
      
      public String zziP(String paramAnonymousString)
      {
        return paramAnonymousString;
      }
      
      public String zzk(SharedPreferences paramAnonymousSharedPreferences)
      {
        return paramAnonymousSharedPreferences.getString(paramString2, "");
      }
    };
  }
  
  private static PhenotypeFlag<Boolean> zza(String paramString1, final String paramString2, String paramString3, Uri paramUri, boolean paramBoolean)
  {
    new PhenotypeFlag(paramString1, paramString2, paramString3, paramUri, Boolean.valueOf(paramBoolean))
    {
      public Boolean zzc(Flag paramAnonymousFlag)
      {
        return Boolean.valueOf(paramAnonymousFlag.getBoolean());
      }
      
      public Boolean zze(zzh paramAnonymouszzh)
      {
        return paramAnonymouszzh.zza(paramString2, null);
      }
      
      public Boolean zzi(SharedPreferences paramAnonymousSharedPreferences)
      {
        return Boolean.valueOf(paramAnonymousSharedPreferences.getBoolean(paramString2, false));
      }
      
      public Boolean zziN(String paramAnonymousString)
      {
        if (zzbtf.fW.matcher(paramAnonymousString).matches()) {
          return Boolean.valueOf(true);
        }
        if (zzbtf.fX.matcher(paramAnonymousString).matches()) {
          return Boolean.valueOf(false);
        }
        return null;
      }
    };
  }
  
  private static PhenotypeFlag<byte[]> zza(String paramString1, final String paramString2, String paramString3, Uri paramUri, byte[] paramArrayOfByte)
  {
    new PhenotypeFlag(paramString1, paramString2, paramString3, paramUri, paramArrayOfByte)
    {
      public byte[] zzf(Flag paramAnonymousFlag)
      {
        return paramAnonymousFlag.getBytesValue();
      }
      
      public byte[] zzh(zzh paramAnonymouszzh)
      {
        return paramAnonymouszzh.zzf(paramString2, null);
      }
      
      public byte[] zziQ(String paramAnonymousString)
      {
        return paramAnonymousString.getBytes();
      }
      
      public byte[] zzl(SharedPreferences paramAnonymousSharedPreferences)
      {
        return Base64.decode(paramAnonymousSharedPreferences.getString(paramString2, ""), 3);
      }
    };
  }
  
  public abstract T fromFlag(Flag paramFlag);
  
  public abstract T fromSharedPreferences(SharedPreferences paramSharedPreferences);
  
  public abstract T fromString(String paramString);
  
  /* Error */
  public T get()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 63	com/google/android/gms/phenotype/PhenotypeFlag:zzaeU	Ljava/lang/Object;
    //   4: ifnull +10 -> 14
    //   7: aload_0
    //   8: getfield 63	com/google/android/gms/phenotype/PhenotypeFlag:zzaeU	Ljava/lang/Object;
    //   11: astore_3
    //   12: aload_3
    //   13: areturn
    //   14: getstatic 49	com/google/android/gms/phenotype/PhenotypeFlag:zzcfA	Z
    //   17: ifeq +50 -> 67
    //   20: aload_0
    //   21: getfield 74	com/google/android/gms/phenotype/PhenotypeFlag:zzcfD	Ljava/lang/String;
    //   24: invokestatic 173	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   27: astore_3
    //   28: aload_3
    //   29: invokevirtual 177	java/lang/String:length	()I
    //   32: ifeq +22 -> 54
    //   35: ldc -77
    //   37: aload_3
    //   38: invokevirtual 183	java/lang/String:concat	(Ljava/lang/String;)Ljava/lang/String;
    //   41: astore_3
    //   42: ldc -71
    //   44: aload_3
    //   45: invokestatic 191	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   48: pop
    //   49: aload_0
    //   50: getfield 80	com/google/android/gms/phenotype/PhenotypeFlag:zzBq	Ljava/lang/Object;
    //   53: areturn
    //   54: new 170	java/lang/String
    //   57: dup
    //   58: ldc -77
    //   60: invokespecial 192	java/lang/String:<init>	(Ljava/lang/String;)V
    //   63: astore_3
    //   64: goto -22 -> 42
    //   67: getstatic 47	com/google/android/gms/phenotype/PhenotypeFlag:zzaJW	Landroid/content/Context;
    //   70: ifnonnull +13 -> 83
    //   73: new 194	java/lang/IllegalStateException
    //   76: dup
    //   77: ldc -60
    //   79: invokespecial 197	java/lang/IllegalStateException:<init>	(Ljava/lang/String;)V
    //   82: athrow
    //   83: getstatic 59	com/google/android/gms/phenotype/PhenotypeFlag:zzcfB	Lcom/google/android/gms/internal/zzaav;
    //   86: invokevirtual 199	com/google/android/gms/internal/zzaav:get	()Ljava/lang/Object;
    //   89: checkcast 133	java/lang/Boolean
    //   92: invokevirtual 203	java/lang/Boolean:booleanValue	()Z
    //   95: ifne +37 -> 132
    //   98: aload_0
    //   99: getfield 78	com/google/android/gms/phenotype/PhenotypeFlag:zzahf	Landroid/net/Uri;
    //   102: ifnull +70 -> 172
    //   105: aload_0
    //   106: getstatic 47	com/google/android/gms/phenotype/PhenotypeFlag:zzaJW	Landroid/content/Context;
    //   109: invokevirtual 207	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   112: aload_0
    //   113: getfield 78	com/google/android/gms/phenotype/PhenotypeFlag:zzahf	Landroid/net/Uri;
    //   116: invokestatic 212	com/google/android/gms/phenotype/zzh:zza	(Landroid/content/ContentResolver;Landroid/net/Uri;)Lcom/google/android/gms/phenotype/zzh;
    //   119: invokevirtual 215	com/google/android/gms/phenotype/PhenotypeFlag:zzb	(Lcom/google/android/gms/phenotype/zzh;)Ljava/lang/Object;
    //   122: astore 4
    //   124: aload 4
    //   126: astore_3
    //   127: aload 4
    //   129: ifnonnull -117 -> 12
    //   132: getstatic 47	com/google/android/gms/phenotype/PhenotypeFlag:zzaJW	Landroid/content/Context;
    //   135: invokevirtual 207	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   138: astore_3
    //   139: aload_3
    //   140: aload_0
    //   141: getfield 72	com/google/android/gms/phenotype/PhenotypeFlag:zzcfC	Ljava/lang/String;
    //   144: invokestatic 221	com/google/android/gms/internal/zzbtf:getString	(Landroid/content/ContentResolver;Ljava/lang/String;)Ljava/lang/String;
    //   147: astore_3
    //   148: aload_3
    //   149: ifnull +18 -> 167
    //   152: aload_0
    //   153: aload_3
    //   154: invokevirtual 223	com/google/android/gms/phenotype/PhenotypeFlag:fromString	(Ljava/lang/String;)Ljava/lang/Object;
    //   157: astore 4
    //   159: aload 4
    //   161: astore_3
    //   162: aload 4
    //   164: ifnonnull -152 -> 12
    //   167: aload_0
    //   168: getfield 80	com/google/android/gms/phenotype/PhenotypeFlag:zzBq	Ljava/lang/Object;
    //   171: areturn
    //   172: getstatic 47	com/google/android/gms/phenotype/PhenotypeFlag:zzaJW	Landroid/content/Context;
    //   175: aload_0
    //   176: getfield 76	com/google/android/gms/phenotype/PhenotypeFlag:zzcfE	Ljava/lang/String;
    //   179: iconst_0
    //   180: invokevirtual 227	android/content/Context:getSharedPreferences	(Ljava/lang/String;I)Landroid/content/SharedPreferences;
    //   183: astore_3
    //   184: aload_3
    //   185: aload_0
    //   186: getfield 74	com/google/android/gms/phenotype/PhenotypeFlag:zzcfD	Ljava/lang/String;
    //   189: invokeinterface 233 2 0
    //   194: ifeq -62 -> 132
    //   197: aload_0
    //   198: aload_3
    //   199: invokevirtual 235	com/google/android/gms/phenotype/PhenotypeFlag:fromSharedPreferences	(Landroid/content/SharedPreferences;)Ljava/lang/Object;
    //   202: areturn
    //   203: astore_3
    //   204: invokestatic 241	android/os/Binder:clearCallingIdentity	()J
    //   207: lstore_1
    //   208: getstatic 47	com/google/android/gms/phenotype/PhenotypeFlag:zzaJW	Landroid/content/Context;
    //   211: invokevirtual 207	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   214: aload_0
    //   215: getfield 72	com/google/android/gms/phenotype/PhenotypeFlag:zzcfC	Ljava/lang/String;
    //   218: invokestatic 221	com/google/android/gms/internal/zzbtf:getString	(Landroid/content/ContentResolver;Ljava/lang/String;)Ljava/lang/String;
    //   221: astore_3
    //   222: lload_1
    //   223: invokestatic 245	android/os/Binder:restoreCallingIdentity	(J)V
    //   226: goto -78 -> 148
    //   229: astore_3
    //   230: lload_1
    //   231: invokestatic 245	android/os/Binder:restoreCallingIdentity	(J)V
    //   234: aload_3
    //   235: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	236	0	this	PhenotypeFlag
    //   207	24	1	l	long
    //   11	188	3	localObject1	Object
    //   203	1	3	localSecurityException	SecurityException
    //   221	1	3	str	String
    //   229	6	3	localObject2	Object
    //   122	41	4	localObject3	Object
    // Exception table:
    //   from	to	target	type
    //   139	148	203	java/lang/SecurityException
    //   208	222	229	finally
  }
  
  @Deprecated
  public final T getBinderSafe()
  {
    return (T)get();
  }
  
  public void override(T paramT)
  {
    this.zzaeU = paramT;
  }
  
  public void resetOverride()
  {
    this.zzaeU = null;
  }
  
  public abstract T zzb(zzh paramzzh);
  
  public static class Factory
  {
    private final Uri zzahf;
    private final String zzcfE;
    private final String zzcfG;
    private final String zzcfH;
    
    public Factory(String paramString)
    {
      this(paramString, null, "", "");
    }
    
    private Factory(String paramString1, Uri paramUri, String paramString2, String paramString3)
    {
      this.zzcfE = paramString1;
      this.zzahf = paramUri;
      this.zzcfG = paramString2;
      this.zzcfH = paramString3;
    }
    
    public PhenotypeFlag<Double> createFlag(String paramString, double paramDouble)
    {
      String str1 = String.valueOf(this.zzcfG);
      String str2 = String.valueOf(paramString);
      if (str2.length() != 0)
      {
        str1 = str1.concat(str2);
        str2 = String.valueOf(this.zzcfH);
        paramString = String.valueOf(paramString);
        if (paramString.length() == 0) {
          break label90;
        }
      }
      label90:
      for (paramString = str2.concat(paramString);; paramString = new String(str2))
      {
        return PhenotypeFlag.zzb(str1, paramString, this.zzcfE, this.zzahf, paramDouble);
        str1 = new String(str1);
        break;
      }
    }
    
    public PhenotypeFlag<Integer> createFlag(String paramString, int paramInt)
    {
      String str1 = String.valueOf(this.zzcfG);
      String str2 = String.valueOf(paramString);
      if (str2.length() != 0)
      {
        str1 = str1.concat(str2);
        str2 = String.valueOf(this.zzcfH);
        paramString = String.valueOf(paramString);
        if (paramString.length() == 0) {
          break label84;
        }
      }
      label84:
      for (paramString = str2.concat(paramString);; paramString = new String(str2))
      {
        return PhenotypeFlag.zzb(str1, paramString, this.zzcfE, this.zzahf, paramInt);
        str1 = new String(str1);
        break;
      }
    }
    
    public PhenotypeFlag<Long> createFlag(String paramString, long paramLong)
    {
      String str1 = String.valueOf(this.zzcfG);
      String str2 = String.valueOf(paramString);
      if (str2.length() != 0)
      {
        str1 = str1.concat(str2);
        str2 = String.valueOf(this.zzcfH);
        paramString = String.valueOf(paramString);
        if (paramString.length() == 0) {
          break label90;
        }
      }
      label90:
      for (paramString = str2.concat(paramString);; paramString = new String(str2))
      {
        return PhenotypeFlag.zzb(str1, paramString, this.zzcfE, this.zzahf, paramLong);
        str1 = new String(str1);
        break;
      }
    }
    
    public PhenotypeFlag<String> createFlag(String paramString1, String paramString2)
    {
      String str1 = String.valueOf(this.zzcfG);
      String str2 = String.valueOf(paramString1);
      if (str2.length() != 0)
      {
        str1 = str1.concat(str2);
        str2 = String.valueOf(this.zzcfH);
        paramString1 = String.valueOf(paramString1);
        if (paramString1.length() == 0) {
          break label84;
        }
      }
      label84:
      for (paramString1 = str2.concat(paramString1);; paramString1 = new String(str2))
      {
        return PhenotypeFlag.zzb(str1, paramString1, this.zzcfE, this.zzahf, paramString2);
        str1 = new String(str1);
        break;
      }
    }
    
    public PhenotypeFlag<Boolean> createFlag(String paramString, boolean paramBoolean)
    {
      String str1 = String.valueOf(this.zzcfG);
      String str2 = String.valueOf(paramString);
      if (str2.length() != 0)
      {
        str1 = str1.concat(str2);
        str2 = String.valueOf(this.zzcfH);
        paramString = String.valueOf(paramString);
        if (paramString.length() == 0) {
          break label84;
        }
      }
      label84:
      for (paramString = str2.concat(paramString);; paramString = new String(str2))
      {
        return PhenotypeFlag.zzb(str1, paramString, this.zzcfE, this.zzahf, paramBoolean);
        str1 = new String(str1);
        break;
      }
    }
    
    public PhenotypeFlag<byte[]> createFlag(String paramString, byte[] paramArrayOfByte)
    {
      String str1 = String.valueOf(this.zzcfG);
      String str2 = String.valueOf(paramString);
      if (str2.length() != 0)
      {
        str1 = str1.concat(str2);
        str2 = String.valueOf(this.zzcfH);
        paramString = String.valueOf(paramString);
        if (paramString.length() == 0) {
          break label84;
        }
      }
      label84:
      for (paramString = str2.concat(paramString);; paramString = new String(str2))
      {
        return PhenotypeFlag.zzb(str1, paramString, this.zzcfE, this.zzahf, paramArrayOfByte);
        str1 = new String(str1);
        break;
      }
    }
    
    public Factory withGservicePrefix(String paramString)
    {
      return new Factory(this.zzcfE, this.zzahf, paramString, this.zzcfH);
    }
    
    public Factory withPhenotypePrefix(String paramString)
    {
      return new Factory(this.zzcfE, this.zzahf, this.zzcfG, paramString);
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/phenotype/PhenotypeFlag.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */