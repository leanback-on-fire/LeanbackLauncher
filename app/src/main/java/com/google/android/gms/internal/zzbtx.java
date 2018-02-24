package com.google.android.gms.internal;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.gms.measurement.AppMeasurement;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class zzbtx
{
  static int zza(@NonNull zzcgn.zzb paramzzb, int paramInt)
  {
    int i;
    if (paramzzb.HA != 0) {
      i = paramzzb.HA;
    }
    do
    {
      return i;
      i = paramInt;
    } while (paramInt != 0);
    return 1;
  }
  
  public static long zza(long paramLong, @Nullable List<byte[]> paramList)
  {
    long l;
    if (paramList == null)
    {
      l = paramLong;
      return l;
    }
    paramList = paramList.iterator();
    for (;;)
    {
      l = paramLong;
      if (!paramList.hasNext()) {
        break;
      }
      Object localObject = (byte[])paramList.next();
      if (localObject != null)
      {
        localObject = zzag((byte[])localObject);
        if ((localObject != null) && (((zzcgn.zzb)localObject).Hr > paramLong)) {
          paramLong = ((zzcgn.zzb)localObject).Hr;
        }
      }
    }
  }
  
  static Bundle zza(@NonNull zzcgn.zzb paramzzb)
  {
    return zzaB(paramzzb.Hp, paramzzb.Hq);
  }
  
  /* Error */
  @Nullable
  static Object zza(@NonNull zzcgn.zzb paramzzb, @NonNull String paramString, @NonNull zzbtw paramzzbtw)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 4
    //   3: ldc 72
    //   5: invokestatic 78	java/lang/Class:forName	(Ljava/lang/String;)Ljava/lang/Class;
    //   8: astore 5
    //   10: aload_0
    //   11: invokestatic 80	com/google/android/gms/internal/zzbtx:zza	(Lcom/google/android/gms/internal/zzcgn$zzb;)Landroid/os/Bundle;
    //   14: astore 6
    //   16: aload 5
    //   18: iconst_0
    //   19: anewarray 74	java/lang/Class
    //   22: invokevirtual 84	java/lang/Class:getConstructor	([Ljava/lang/Class;)Ljava/lang/reflect/Constructor;
    //   25: iconst_0
    //   26: anewarray 4	java/lang/Object
    //   29: invokevirtual 90	java/lang/reflect/Constructor:newInstance	([Ljava/lang/Object;)Ljava/lang/Object;
    //   32: astore_3
    //   33: aload 5
    //   35: ldc 92
    //   37: invokevirtual 96	java/lang/Class:getField	(Ljava/lang/String;)Ljava/lang/reflect/Field;
    //   40: aload_3
    //   41: aload_1
    //   42: invokevirtual 102	java/lang/reflect/Field:set	(Ljava/lang/Object;Ljava/lang/Object;)V
    //   45: aload 5
    //   47: ldc 104
    //   49: invokevirtual 96	java/lang/Class:getField	(Ljava/lang/String;)Ljava/lang/reflect/Field;
    //   52: aload_3
    //   53: aload_0
    //   54: getfield 43	com/google/android/gms/internal/zzcgn$zzb:Hr	J
    //   57: invokestatic 110	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   60: invokevirtual 102	java/lang/reflect/Field:set	(Ljava/lang/Object;Ljava/lang/Object;)V
    //   63: aload 5
    //   65: ldc 112
    //   67: invokevirtual 96	java/lang/Class:getField	(Ljava/lang/String;)Ljava/lang/reflect/Field;
    //   70: aload_3
    //   71: aload_0
    //   72: getfield 50	com/google/android/gms/internal/zzcgn$zzb:Hp	Ljava/lang/String;
    //   75: invokevirtual 102	java/lang/reflect/Field:set	(Ljava/lang/Object;Ljava/lang/Object;)V
    //   78: aload 5
    //   80: ldc 114
    //   82: invokevirtual 96	java/lang/Class:getField	(Ljava/lang/String;)Ljava/lang/reflect/Field;
    //   85: aload_3
    //   86: aload_0
    //   87: getfield 53	com/google/android/gms/internal/zzcgn$zzb:Hq	Ljava/lang/String;
    //   90: invokevirtual 102	java/lang/reflect/Field:set	(Ljava/lang/Object;Ljava/lang/Object;)V
    //   93: aload_0
    //   94: getfield 117	com/google/android/gms/internal/zzcgn$zzb:Hs	Ljava/lang/String;
    //   97: invokestatic 123	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   100: ifeq +143 -> 243
    //   103: aload 4
    //   105: astore_1
    //   106: aload 5
    //   108: ldc 125
    //   110: invokevirtual 96	java/lang/Class:getField	(Ljava/lang/String;)Ljava/lang/reflect/Field;
    //   113: aload_3
    //   114: aload_1
    //   115: invokevirtual 102	java/lang/reflect/Field:set	(Ljava/lang/Object;Ljava/lang/Object;)V
    //   118: aload 5
    //   120: ldc 127
    //   122: invokevirtual 96	java/lang/Class:getField	(Ljava/lang/String;)Ljava/lang/reflect/Field;
    //   125: aload_3
    //   126: aload_0
    //   127: aload_2
    //   128: invokestatic 131	com/google/android/gms/internal/zzbtx:zzd	(Lcom/google/android/gms/internal/zzcgn$zzb;Lcom/google/android/gms/internal/zzbtw;)Ljava/lang/String;
    //   131: invokevirtual 102	java/lang/reflect/Field:set	(Ljava/lang/Object;Ljava/lang/Object;)V
    //   134: aload 5
    //   136: ldc -123
    //   138: invokevirtual 96	java/lang/Class:getField	(Ljava/lang/String;)Ljava/lang/reflect/Field;
    //   141: aload_3
    //   142: aload 6
    //   144: invokevirtual 102	java/lang/reflect/Field:set	(Ljava/lang/Object;Ljava/lang/Object;)V
    //   147: aload 5
    //   149: ldc -121
    //   151: invokevirtual 96	java/lang/Class:getField	(Ljava/lang/String;)Ljava/lang/reflect/Field;
    //   154: aload_3
    //   155: aload_0
    //   156: getfield 138	com/google/android/gms/internal/zzcgn$zzb:Ht	I
    //   159: invokestatic 143	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   162: invokevirtual 102	java/lang/reflect/Field:set	(Ljava/lang/Object;Ljava/lang/Object;)V
    //   165: aload 5
    //   167: ldc -111
    //   169: invokevirtual 96	java/lang/Class:getField	(Ljava/lang/String;)Ljava/lang/reflect/Field;
    //   172: aload_3
    //   173: aload_0
    //   174: aload_2
    //   175: invokestatic 148	com/google/android/gms/internal/zzbtx:zzb	(Lcom/google/android/gms/internal/zzcgn$zzb;Lcom/google/android/gms/internal/zzbtw;)Ljava/lang/String;
    //   178: invokevirtual 102	java/lang/reflect/Field:set	(Ljava/lang/Object;Ljava/lang/Object;)V
    //   181: aload 5
    //   183: ldc -106
    //   185: invokevirtual 96	java/lang/Class:getField	(Ljava/lang/String;)Ljava/lang/reflect/Field;
    //   188: aload_3
    //   189: aload 6
    //   191: invokevirtual 102	java/lang/reflect/Field:set	(Ljava/lang/Object;Ljava/lang/Object;)V
    //   194: aload 5
    //   196: ldc -104
    //   198: invokevirtual 96	java/lang/Class:getField	(Ljava/lang/String;)Ljava/lang/reflect/Field;
    //   201: aload_3
    //   202: aload_0
    //   203: getfield 155	com/google/android/gms/internal/zzcgn$zzb:Hu	I
    //   206: invokestatic 143	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   209: invokevirtual 102	java/lang/reflect/Field:set	(Ljava/lang/Object;Ljava/lang/Object;)V
    //   212: aload 5
    //   214: ldc -99
    //   216: invokevirtual 96	java/lang/Class:getField	(Ljava/lang/String;)Ljava/lang/reflect/Field;
    //   219: aload_3
    //   220: aload_0
    //   221: aload_2
    //   222: invokestatic 160	com/google/android/gms/internal/zzbtx:zze	(Lcom/google/android/gms/internal/zzcgn$zzb;Lcom/google/android/gms/internal/zzbtw;)Ljava/lang/String;
    //   225: invokevirtual 102	java/lang/reflect/Field:set	(Ljava/lang/Object;Ljava/lang/Object;)V
    //   228: aload 5
    //   230: ldc -94
    //   232: invokevirtual 96	java/lang/Class:getField	(Ljava/lang/String;)Ljava/lang/reflect/Field;
    //   235: aload_3
    //   236: aload 6
    //   238: invokevirtual 102	java/lang/reflect/Field:set	(Ljava/lang/Object;Ljava/lang/Object;)V
    //   241: aload_3
    //   242: areturn
    //   243: aload_0
    //   244: getfield 117	com/google/android/gms/internal/zzcgn$zzb:Hs	Ljava/lang/String;
    //   247: astore_1
    //   248: goto -142 -> 106
    //   251: astore_0
    //   252: aconst_null
    //   253: astore_1
    //   254: ldc -92
    //   256: ldc -90
    //   258: aload_0
    //   259: invokestatic 172	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   262: pop
    //   263: aload_1
    //   264: areturn
    //   265: astore_0
    //   266: aload_3
    //   267: astore_1
    //   268: goto -14 -> 254
    //   271: astore_0
    //   272: aconst_null
    //   273: astore_1
    //   274: goto -20 -> 254
    //   277: astore_0
    //   278: aload_3
    //   279: astore_1
    //   280: goto -26 -> 254
    //   283: astore_0
    //   284: aconst_null
    //   285: astore_1
    //   286: goto -32 -> 254
    //   289: astore_0
    //   290: aload_3
    //   291: astore_1
    //   292: goto -38 -> 254
    //   295: astore_0
    //   296: aconst_null
    //   297: astore_1
    //   298: goto -44 -> 254
    //   301: astore_0
    //   302: aload_3
    //   303: astore_1
    //   304: goto -50 -> 254
    //   307: astore_0
    //   308: aconst_null
    //   309: astore_1
    //   310: goto -56 -> 254
    //   313: astore_0
    //   314: aload_3
    //   315: astore_1
    //   316: goto -62 -> 254
    //   319: astore_0
    //   320: aconst_null
    //   321: astore_1
    //   322: goto -68 -> 254
    //   325: astore_0
    //   326: aload_3
    //   327: astore_1
    //   328: goto -74 -> 254
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	331	0	paramzzb	zzcgn.zzb
    //   0	331	1	paramString	String
    //   0	331	2	paramzzbtw	zzbtw
    //   32	295	3	localObject1	Object
    //   1	103	4	localObject2	Object
    //   8	221	5	localClass	Class
    //   14	223	6	localBundle	Bundle
    // Exception table:
    //   from	to	target	type
    //   3	33	251	java/lang/ClassNotFoundException
    //   33	103	265	java/lang/ClassNotFoundException
    //   106	241	265	java/lang/ClassNotFoundException
    //   243	248	265	java/lang/ClassNotFoundException
    //   3	33	271	java/lang/InstantiationException
    //   33	103	277	java/lang/InstantiationException
    //   106	241	277	java/lang/InstantiationException
    //   243	248	277	java/lang/InstantiationException
    //   3	33	283	java/lang/NoSuchFieldException
    //   33	103	289	java/lang/NoSuchFieldException
    //   106	241	289	java/lang/NoSuchFieldException
    //   243	248	289	java/lang/NoSuchFieldException
    //   3	33	295	java/lang/IllegalAccessException
    //   33	103	301	java/lang/IllegalAccessException
    //   106	241	301	java/lang/IllegalAccessException
    //   243	248	301	java/lang/IllegalAccessException
    //   3	33	307	java/lang/NoSuchMethodException
    //   33	103	313	java/lang/NoSuchMethodException
    //   106	241	313	java/lang/NoSuchMethodException
    //   243	248	313	java/lang/NoSuchMethodException
    //   3	33	319	java/lang/reflect/InvocationTargetException
    //   33	103	325	java/lang/reflect/InvocationTargetException
    //   106	241	325	java/lang/reflect/InvocationTargetException
    //   243	248	325	java/lang/reflect/InvocationTargetException
  }
  
  static String zza(@NonNull zzcgn.zzb paramzzb, @NonNull zzbtw paramzzbtw)
  {
    if (!TextUtils.isEmpty(paramzzb.Hv)) {
      return paramzzb.Hv;
    }
    return paramzzbtw.zzadp();
  }
  
  public static void zza(@NonNull Context paramContext, @NonNull String paramString1, @NonNull String paramString2, @NonNull String paramString3, @NonNull String paramString4)
  {
    if (Log.isLoggable("FirebaseAbtUtil", 2))
    {
      paramString1 = String.valueOf(paramString1);
      if (paramString1.length() == 0) {
        break label43;
      }
    }
    label43:
    for (paramString1 = "_CE(experimentId) called by ".concat(paramString1);; paramString1 = new String("_CE(experimentId) called by "))
    {
      Log.v("FirebaseAbtUtil", paramString1);
      if (zzcx(paramContext)) {
        break;
      }
      return;
    }
    paramContext = zzbq(paramContext);
    try
    {
      paramString1 = AppMeasurement.class.getDeclaredMethod("clearConditionalUserProperty", new Class[] { String.class, String.class, Bundle.class });
      paramString1.setAccessible(true);
      if (Log.isLoggable("FirebaseAbtUtil", 2)) {
        Log.v("FirebaseAbtUtil", String.valueOf(paramString2).length() + 17 + String.valueOf(paramString3).length() + "Clearing _E: [" + paramString2 + ", " + paramString3 + "]");
      }
      paramString1.invoke(paramContext, new Object[] { paramString2, paramString4, zzaB(paramString2, paramString3) });
      return;
    }
    catch (NoSuchMethodException paramContext)
    {
      Log.e("FirebaseAbtUtil", "Could not complete the operation due to an internal error.", paramContext);
      return;
    }
    catch (IllegalAccessException paramContext)
    {
      for (;;) {}
    }
    catch (InvocationTargetException paramContext)
    {
      for (;;) {}
    }
  }
  
  public static void zza(@NonNull Context paramContext, @NonNull String paramString, @NonNull List<byte[]> paramList, int paramInt, @NonNull zzbtw paramzzbtw, long paramLong)
  {
    Object localObject1;
    if (Log.isLoggable("FirebaseAbtUtil", 2))
    {
      localObject1 = String.valueOf(paramString);
      if (((String)localObject1).length() != 0)
      {
        localObject1 = "_UE called by ".concat((String)localObject1);
        Log.v("FirebaseAbtUtil", (String)localObject1);
      }
    }
    else
    {
      if (zzcx(paramContext)) {
        break label64;
      }
    }
    for (;;)
    {
      return;
      localObject1 = new String("_UE called by ");
      break;
      label64:
      localObject1 = zzbq(paramContext);
      try
      {
        Class.forName("com.google.android.gms.measurement.AppMeasurement$ConditionalUserProperty");
        localObject3 = zzb((AppMeasurement)localObject1, paramString);
        new ArrayList();
        localObject2 = zzc(paramList, (List)localObject3);
        paramList = zzd(paramList, (List)localObject3).iterator();
        while (paramList.hasNext())
        {
          localObject4 = paramList.next();
          localObject3 = zzan(localObject4);
          localObject4 = zzao(localObject4);
          if (Log.isLoggable("FirebaseAbtUtil", 2)) {
            Log.v("FirebaseAbtUtil", String.valueOf(localObject3).length() + 30 + "Clearing _E as part of _UE: [" + (String)localObject3 + "]");
          }
          zza(paramContext, paramString, (String)localObject3, (String)localObject4, zzc(null, paramzzbtw));
        }
      }
      catch (ClassNotFoundException paramContext)
      {
        Object localObject3;
        Object localObject2;
        Object localObject4;
        Log.e("FirebaseAbtUtil", "Could not complete the operation due to an internal error.", paramContext);
        return;
        paramList = ((List)localObject2).iterator();
        while (paramList.hasNext())
        {
          localObject2 = (zzcgn.zzb)paramList.next();
          long l;
          if (((zzcgn.zzb)localObject2).Hr > paramLong)
          {
            localObject3 = String.valueOf(((zzcgn.zzb)localObject2).Hp);
            localObject4 = String.valueOf(((zzcgn.zzb)localObject2).Hq);
            l = ((zzcgn.zzb)localObject2).Hr;
            Log.v("FirebaseAbtUtil", String.valueOf(localObject3).length() + 106 + String.valueOf(localObject4).length() + "Setting _E as part of _UE: [" + (String)localObject3 + ", " + (String)localObject4 + ", " + l + "], latestOriginKnownExpStartTime: " + paramLong);
            zza((AppMeasurement)localObject1, paramContext, paramString, (zzcgn.zzb)localObject2, paramzzbtw, paramInt);
          }
          else if (Log.isLoggable("FirebaseAbtUtil", 2))
          {
            localObject3 = String.valueOf(((zzcgn.zzb)localObject2).Hp);
            localObject4 = String.valueOf(((zzcgn.zzb)localObject2).Hq);
            l = ((zzcgn.zzb)localObject2).Hr;
            Log.v("FirebaseAbtUtil", String.valueOf(localObject3).length() + 118 + String.valueOf(localObject4).length() + "Not setting _E, due to lastUpdateTime: [" + (String)localObject3 + ", " + (String)localObject4 + ", " + l + "], latestOriginKnownExpStartTime: " + paramLong);
          }
        }
      }
      catch (NoSuchFieldException paramContext)
      {
        for (;;) {}
      }
      catch (IllegalAccessException paramContext)
      {
        for (;;) {}
      }
    }
  }
  
  /* Error */
  static void zza(@NonNull AppMeasurement paramAppMeasurement, @NonNull Context paramContext, @NonNull String paramString, @NonNull zzcgn.zzb paramzzb, @NonNull zzbtw paramzzbtw, int paramInt)
  {
    // Byte code:
    //   0: ldc -92
    //   2: iconst_2
    //   3: invokestatic 187	android/util/Log:isLoggable	(Ljava/lang/String;I)Z
    //   6: ifeq +79 -> 85
    //   9: aload_3
    //   10: getfield 50	com/google/android/gms/internal/zzcgn$zzb:Hp	Ljava/lang/String;
    //   13: invokestatic 192	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   16: astore 6
    //   18: aload_3
    //   19: getfield 53	com/google/android/gms/internal/zzcgn$zzb:Hq	Ljava/lang/String;
    //   22: invokestatic 192	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   25: astore 7
    //   27: ldc -92
    //   29: new 236	java/lang/StringBuilder
    //   32: dup
    //   33: aload 6
    //   35: invokestatic 192	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   38: invokevirtual 196	java/lang/String:length	()I
    //   41: bipush 7
    //   43: iadd
    //   44: aload 7
    //   46: invokestatic 192	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   49: invokevirtual 196	java/lang/String:length	()I
    //   52: iadd
    //   53: invokespecial 239	java/lang/StringBuilder:<init>	(I)V
    //   56: ldc_w 300
    //   59: invokevirtual 245	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   62: aload 6
    //   64: invokevirtual 245	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   67: ldc_w 302
    //   70: invokevirtual 245	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   73: aload 7
    //   75: invokevirtual 245	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   78: invokevirtual 252	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   81: invokestatic 206	android/util/Log:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   84: pop
    //   85: ldc 72
    //   87: invokestatic 78	java/lang/Class:forName	(Ljava/lang/String;)Ljava/lang/Class;
    //   90: pop
    //   91: aload_0
    //   92: aload_2
    //   93: invokestatic 262	com/google/android/gms/internal/zzbtx:zzb	(Lcom/google/android/gms/measurement/AppMeasurement;Ljava/lang/String;)Ljava/util/List;
    //   96: astore 6
    //   98: aload_0
    //   99: aload_2
    //   100: invokestatic 305	com/google/android/gms/internal/zzbtx:zza	(Lcom/google/android/gms/measurement/AppMeasurement;Ljava/lang/String;)Z
    //   103: ifeq +104 -> 207
    //   106: aload_3
    //   107: iload 5
    //   109: invokestatic 307	com/google/android/gms/internal/zzbtx:zza	(Lcom/google/android/gms/internal/zzcgn$zzb;I)I
    //   112: iconst_1
    //   113: if_icmpne +261 -> 374
    //   116: aload 6
    //   118: iconst_0
    //   119: invokeinterface 311 2 0
    //   124: astore 8
    //   126: aload 8
    //   128: invokestatic 276	com/google/android/gms/internal/zzbtx:zzan	(Ljava/lang/Object;)Ljava/lang/String;
    //   131: astore 7
    //   133: aload 8
    //   135: invokestatic 279	com/google/android/gms/internal/zzbtx:zzao	(Ljava/lang/Object;)Ljava/lang/String;
    //   138: astore 8
    //   140: ldc -92
    //   142: iconst_2
    //   143: invokestatic 187	android/util/Log:isLoggable	(Ljava/lang/String;I)Z
    //   146: ifeq +46 -> 192
    //   149: ldc -92
    //   151: new 236	java/lang/StringBuilder
    //   154: dup
    //   155: aload 7
    //   157: invokestatic 192	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   160: invokevirtual 196	java/lang/String:length	()I
    //   163: bipush 38
    //   165: iadd
    //   166: invokespecial 239	java/lang/StringBuilder:<init>	(I)V
    //   169: ldc_w 313
    //   172: invokevirtual 245	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   175: aload 7
    //   177: invokevirtual 245	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   180: ldc -7
    //   182: invokevirtual 245	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   185: invokevirtual 252	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   188: invokestatic 206	android/util/Log:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   191: pop
    //   192: aload_1
    //   193: aload_2
    //   194: aload 7
    //   196: aload 8
    //   198: aload_3
    //   199: aload 4
    //   201: invokestatic 283	com/google/android/gms/internal/zzbtx:zzc	(Lcom/google/android/gms/internal/zzcgn$zzb;Lcom/google/android/gms/internal/zzbtw;)Ljava/lang/String;
    //   204: invokestatic 285	com/google/android/gms/internal/zzbtx:zza	(Landroid/content/Context;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V
    //   207: aload 6
    //   209: invokeinterface 23 1 0
    //   214: astore 6
    //   216: aload 6
    //   218: invokeinterface 29 1 0
    //   223: ifeq +235 -> 458
    //   226: aload 6
    //   228: invokeinterface 33 1 0
    //   233: astore 8
    //   235: aload 8
    //   237: invokestatic 276	com/google/android/gms/internal/zzbtx:zzan	(Ljava/lang/Object;)Ljava/lang/String;
    //   240: astore 7
    //   242: aload 8
    //   244: invokestatic 279	com/google/android/gms/internal/zzbtx:zzao	(Ljava/lang/Object;)Ljava/lang/String;
    //   247: astore 8
    //   249: aload 7
    //   251: aload_3
    //   252: getfield 50	com/google/android/gms/internal/zzcgn$zzb:Hp	Ljava/lang/String;
    //   255: invokevirtual 317	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   258: ifeq -42 -> 216
    //   261: aload 8
    //   263: aload_3
    //   264: getfield 53	com/google/android/gms/internal/zzcgn$zzb:Hq	Ljava/lang/String;
    //   267: invokevirtual 317	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   270: ifne -54 -> 216
    //   273: ldc -92
    //   275: iconst_2
    //   276: invokestatic 187	android/util/Log:isLoggable	(Ljava/lang/String;I)Z
    //   279: ifeq -63 -> 216
    //   282: ldc -92
    //   284: new 236	java/lang/StringBuilder
    //   287: dup
    //   288: aload 7
    //   290: invokestatic 192	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   293: invokevirtual 196	java/lang/String:length	()I
    //   296: bipush 77
    //   298: iadd
    //   299: aload 8
    //   301: invokestatic 192	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   304: invokevirtual 196	java/lang/String:length	()I
    //   307: iadd
    //   308: invokespecial 239	java/lang/StringBuilder:<init>	(I)V
    //   311: ldc_w 319
    //   314: invokevirtual 245	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   317: aload 7
    //   319: invokevirtual 245	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   322: ldc -9
    //   324: invokevirtual 245	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   327: aload 8
    //   329: invokevirtual 245	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   332: ldc_w 321
    //   335: invokevirtual 245	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   338: invokevirtual 252	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   341: invokestatic 206	android/util/Log:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   344: pop
    //   345: aload_1
    //   346: aload_2
    //   347: aload 7
    //   349: aload 8
    //   351: aload_3
    //   352: aload 4
    //   354: invokestatic 283	com/google/android/gms/internal/zzbtx:zzc	(Lcom/google/android/gms/internal/zzcgn$zzb;Lcom/google/android/gms/internal/zzbtw;)Ljava/lang/String;
    //   357: invokestatic 285	com/google/android/gms/internal/zzbtx:zza	(Landroid/content/Context;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V
    //   360: goto -144 -> 216
    //   363: astore_0
    //   364: ldc -92
    //   366: ldc -90
    //   368: aload_0
    //   369: invokestatic 172	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   372: pop
    //   373: return
    //   374: ldc -92
    //   376: iconst_2
    //   377: invokestatic 187	android/util/Log:isLoggable	(Ljava/lang/String;I)Z
    //   380: ifeq -7 -> 373
    //   383: aload_3
    //   384: getfield 50	com/google/android/gms/internal/zzcgn$zzb:Hp	Ljava/lang/String;
    //   387: invokestatic 192	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   390: astore_0
    //   391: aload_3
    //   392: getfield 53	com/google/android/gms/internal/zzcgn$zzb:Hq	Ljava/lang/String;
    //   395: invokestatic 192	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   398: astore_1
    //   399: ldc -92
    //   401: new 236	java/lang/StringBuilder
    //   404: dup
    //   405: aload_0
    //   406: invokestatic 192	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   409: invokevirtual 196	java/lang/String:length	()I
    //   412: bipush 44
    //   414: iadd
    //   415: aload_1
    //   416: invokestatic 192	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   419: invokevirtual 196	java/lang/String:length	()I
    //   422: iadd
    //   423: invokespecial 239	java/lang/StringBuilder:<init>	(I)V
    //   426: ldc_w 323
    //   429: invokevirtual 245	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   432: aload_0
    //   433: invokevirtual 245	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   436: ldc -9
    //   438: invokevirtual 245	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   441: aload_1
    //   442: invokevirtual 245	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   445: ldc -7
    //   447: invokevirtual 245	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   450: invokevirtual 252	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   453: invokestatic 206	android/util/Log:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   456: pop
    //   457: return
    //   458: aload_3
    //   459: aload_2
    //   460: aload 4
    //   462: invokestatic 325	com/google/android/gms/internal/zzbtx:zza	(Lcom/google/android/gms/internal/zzcgn$zzb;Ljava/lang/String;Lcom/google/android/gms/internal/zzbtw;)Ljava/lang/Object;
    //   465: astore_1
    //   466: aload_1
    //   467: ifnonnull +88 -> 555
    //   470: ldc -92
    //   472: iconst_2
    //   473: invokestatic 187	android/util/Log:isLoggable	(Ljava/lang/String;I)Z
    //   476: ifeq -103 -> 373
    //   479: aload_3
    //   480: getfield 50	com/google/android/gms/internal/zzcgn$zzb:Hp	Ljava/lang/String;
    //   483: invokestatic 192	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   486: astore_0
    //   487: aload_3
    //   488: getfield 53	com/google/android/gms/internal/zzcgn$zzb:Hq	Ljava/lang/String;
    //   491: invokestatic 192	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   494: astore_1
    //   495: ldc -92
    //   497: new 236	java/lang/StringBuilder
    //   500: dup
    //   501: aload_0
    //   502: invokestatic 192	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   505: invokevirtual 196	java/lang/String:length	()I
    //   508: bipush 42
    //   510: iadd
    //   511: aload_1
    //   512: invokestatic 192	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   515: invokevirtual 196	java/lang/String:length	()I
    //   518: iadd
    //   519: invokespecial 239	java/lang/StringBuilder:<init>	(I)V
    //   522: ldc_w 327
    //   525: invokevirtual 245	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   528: aload_0
    //   529: invokevirtual 245	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   532: ldc -9
    //   534: invokevirtual 245	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   537: aload_1
    //   538: invokevirtual 245	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   541: ldc_w 329
    //   544: invokevirtual 245	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   547: invokevirtual 252	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   550: invokestatic 206	android/util/Log:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   553: pop
    //   554: return
    //   555: aload_0
    //   556: aload_3
    //   557: aload 4
    //   559: aload_1
    //   560: aload_2
    //   561: invokestatic 332	com/google/android/gms/internal/zzbtx:zza	(Lcom/google/android/gms/measurement/AppMeasurement;Lcom/google/android/gms/internal/zzcgn$zzb;Lcom/google/android/gms/internal/zzbtw;Ljava/lang/Object;Ljava/lang/String;)V
    //   564: return
    //   565: astore_0
    //   566: goto -202 -> 364
    //   569: astore_0
    //   570: goto -206 -> 364
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	573	0	paramAppMeasurement	AppMeasurement
    //   0	573	1	paramContext	Context
    //   0	573	2	paramString	String
    //   0	573	3	paramzzb	zzcgn.zzb
    //   0	573	4	paramzzbtw	zzbtw
    //   0	573	5	paramInt	int
    //   16	211	6	localObject1	Object
    //   25	323	7	str	String
    //   124	226	8	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   85	192	363	java/lang/ClassNotFoundException
    //   192	207	363	java/lang/ClassNotFoundException
    //   207	216	363	java/lang/ClassNotFoundException
    //   216	360	363	java/lang/ClassNotFoundException
    //   374	457	363	java/lang/ClassNotFoundException
    //   458	466	363	java/lang/ClassNotFoundException
    //   470	554	363	java/lang/ClassNotFoundException
    //   555	564	363	java/lang/ClassNotFoundException
    //   85	192	565	java/lang/IllegalAccessException
    //   192	207	565	java/lang/IllegalAccessException
    //   207	216	565	java/lang/IllegalAccessException
    //   216	360	565	java/lang/IllegalAccessException
    //   374	457	565	java/lang/IllegalAccessException
    //   458	466	565	java/lang/IllegalAccessException
    //   470	554	565	java/lang/IllegalAccessException
    //   555	564	565	java/lang/IllegalAccessException
    //   85	192	569	java/lang/NoSuchFieldException
    //   192	207	569	java/lang/NoSuchFieldException
    //   207	216	569	java/lang/NoSuchFieldException
    //   216	360	569	java/lang/NoSuchFieldException
    //   374	457	569	java/lang/NoSuchFieldException
    //   458	466	569	java/lang/NoSuchFieldException
    //   470	554	569	java/lang/NoSuchFieldException
    //   555	564	569	java/lang/NoSuchFieldException
  }
  
  static void zza(@NonNull AppMeasurement paramAppMeasurement, @NonNull zzcgn.zzb paramzzb, @NonNull zzbtw paramzzbtw, @NonNull Object paramObject, @NonNull String paramString)
  {
    Object localObject;
    if (Log.isLoggable("FirebaseAbtUtil", 2))
    {
      localObject = String.valueOf(paramzzb.Hp);
      String str1 = String.valueOf(paramzzb.Hq);
      String str2 = String.valueOf(paramzzb.Hs);
      Log.v("FirebaseAbtUtil", String.valueOf(localObject).length() + 27 + String.valueOf(str1).length() + String.valueOf(str2).length() + "Setting _CUP for _E: [" + (String)localObject + ", " + str1 + ", " + str2 + "]");
    }
    try
    {
      localObject = AppMeasurement.class.getDeclaredMethod("setConditionalUserProperty", new Class[] { Class.forName("com.google.android.gms.measurement.AppMeasurement$ConditionalUserProperty") });
      ((Method)localObject).setAccessible(true);
      paramAppMeasurement.logEventInternal(paramString, zza(paramzzb, paramzzbtw), zza(paramzzb));
      ((Method)localObject).invoke(paramAppMeasurement, new Object[] { paramObject });
      return;
    }
    catch (ClassNotFoundException paramAppMeasurement)
    {
      Log.e("FirebaseAbtUtil", "Could not complete the operation due to an internal error.", paramAppMeasurement);
      return;
    }
    catch (IllegalAccessException paramAppMeasurement)
    {
      for (;;) {}
    }
    catch (NoSuchMethodException paramAppMeasurement)
    {
      for (;;) {}
    }
    catch (InvocationTargetException paramAppMeasurement)
    {
      for (;;) {}
    }
  }
  
  static boolean zza(@NonNull AppMeasurement paramAppMeasurement, @NonNull String paramString)
  {
    int i = zzc(paramAppMeasurement, paramString);
    return zzb(paramAppMeasurement, paramString).size() >= i;
  }
  
  static Bundle zzaB(@NonNull String paramString1, @NonNull String paramString2)
  {
    Bundle localBundle = new Bundle();
    localBundle.putString(paramString1, paramString2);
    return localBundle;
  }
  
  @Nullable
  static zzcgn.zzb zzag(@NonNull byte[] paramArrayOfByte)
  {
    try
    {
      paramArrayOfByte = zzcgn.zzb.zzaw(paramArrayOfByte);
      return paramArrayOfByte;
    }
    catch (zzcgf paramArrayOfByte) {}
    return null;
  }
  
  static String zzan(@NonNull Object paramObject)
    throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException
  {
    return (String)Class.forName("com.google.android.gms.measurement.AppMeasurement$ConditionalUserProperty").getField("mName").get(paramObject);
  }
  
  static String zzao(@NonNull Object paramObject)
    throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException
  {
    return (String)Class.forName("com.google.android.gms.measurement.AppMeasurement$ConditionalUserProperty").getField("mValue").get(paramObject);
  }
  
  static String zzb(@NonNull zzcgn.zzb paramzzb, @NonNull zzbtw paramzzbtw)
  {
    if (!TextUtils.isEmpty(paramzzb.Hw)) {
      return paramzzb.Hw;
    }
    return paramzzbtw.zzadq();
  }
  
  static List<Object> zzb(@NonNull AppMeasurement paramAppMeasurement, @NonNull String paramString)
  {
    ArrayList localArrayList = new ArrayList();
    try
    {
      Method localMethod = AppMeasurement.class.getDeclaredMethod("getConditionalUserProperties", new Class[] { String.class, String.class });
      localMethod.setAccessible(true);
      paramAppMeasurement = (List)localMethod.invoke(paramAppMeasurement, new Object[] { paramString, "" });
      if (Log.isLoggable("FirebaseAbtUtil", 2))
      {
        int i = paramAppMeasurement.size();
        Log.v("FirebaseAbtUtil", String.valueOf(paramString).length() + 55 + "Number of currently set _Es for origin: " + paramString + " is " + i);
      }
      return paramAppMeasurement;
    }
    catch (IllegalAccessException paramAppMeasurement)
    {
      for (;;)
      {
        Log.e("FirebaseAbtUtil", "Could not complete the operation due to an internal error.", paramAppMeasurement);
        paramAppMeasurement = localArrayList;
      }
    }
    catch (NoSuchMethodException paramAppMeasurement)
    {
      for (;;) {}
    }
    catch (InvocationTargetException paramAppMeasurement)
    {
      for (;;) {}
    }
  }
  
  @Nullable
  static AppMeasurement zzbq(Context paramContext)
  {
    try
    {
      paramContext = AppMeasurement.getInstance(paramContext);
      return paramContext;
    }
    catch (NoClassDefFoundError paramContext) {}
    return null;
  }
  
  static int zzc(@NonNull AppMeasurement paramAppMeasurement, @NonNull String paramString)
  {
    try
    {
      Method localMethod = AppMeasurement.class.getDeclaredMethod("getMaxUserProperties", new Class[] { String.class });
      localMethod.setAccessible(true);
      int i = ((Integer)localMethod.invoke(paramAppMeasurement, new Object[] { paramString })).intValue();
      return i;
    }
    catch (IllegalAccessException paramAppMeasurement)
    {
      Log.e("FirebaseAbtUtil", "Could not complete the operation due to an internal error.", paramAppMeasurement);
      return 20;
    }
    catch (NoSuchMethodException paramAppMeasurement)
    {
      for (;;) {}
    }
    catch (InvocationTargetException paramAppMeasurement)
    {
      for (;;) {}
    }
  }
  
  static String zzc(@Nullable zzcgn.zzb paramzzb, @NonNull zzbtw paramzzbtw)
  {
    if ((paramzzb != null) && (!TextUtils.isEmpty(paramzzb.Hx))) {
      return paramzzb.Hx;
    }
    return paramzzbtw.zzadt();
  }
  
  static List<zzcgn.zzb> zzc(@NonNull List<byte[]> paramList, @NonNull List<Object> paramList1)
  {
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator1 = paramList.iterator();
    zzcgn.zzb localzzb;
    while (localIterator1.hasNext())
    {
      localzzb = zzag((byte[])localIterator1.next());
      if (localzzb == null)
      {
        if (Log.isLoggable("FirebaseAbtUtil", 2)) {
          Log.v("FirebaseAbtUtil", "Couldn't deserialize the payload; skipping.");
        }
      }
      else
      {
        Iterator localIterator2 = paramList1.iterator();
        label76:
        if (!localIterator2.hasNext()) {
          break label184;
        }
        paramList = localIterator2.next();
      }
    }
    for (;;)
    {
      try
      {
        Class.forName("com.google.android.gms.measurement.AppMeasurement$ConditionalUserProperty");
        zzan(paramList);
        String str = zzao(paramList);
        if (!localzzb.Hp.equals(zzan(paramList))) {
          break label76;
        }
        boolean bool = localzzb.Hq.equals(str);
        if (!bool) {
          break label76;
        }
        i = 1;
        if (i != 0) {
          break;
        }
        localArrayList.add(localzzb);
      }
      catch (ClassNotFoundException paramList)
      {
        Log.e("FirebaseAbtUtil", "Could not complete the operation due to an internal error.", paramList);
        break label76;
        return localArrayList;
      }
      catch (NoSuchFieldException paramList)
      {
        continue;
      }
      catch (IllegalAccessException paramList)
      {
        continue;
      }
      label184:
      int i = 0;
    }
  }
  
  private static boolean zzcx(Context paramContext)
  {
    if (zzbq(paramContext) == null) {
      if (Log.isLoggable("FirebaseAbtUtil", 2)) {
        Log.v("FirebaseAbtUtil", "Firebase Analytics not available");
      }
    }
    do
    {
      return false;
      try
      {
        Class.forName("com.google.android.gms.measurement.AppMeasurement$ConditionalUserProperty");
        return true;
      }
      catch (ClassNotFoundException paramContext) {}
    } while (!Log.isLoggable("FirebaseAbtUtil", 2));
    Log.v("FirebaseAbtUtil", "Firebase Analytics library is missing support for abt. Please update to a more recent version.");
    return false;
  }
  
  static String zzd(@NonNull zzcgn.zzb paramzzb, @NonNull zzbtw paramzzbtw)
  {
    if (!TextUtils.isEmpty(paramzzb.Hy)) {
      return paramzzb.Hy;
    }
    return paramzzbtw.zzadr();
  }
  
  static List<Object> zzd(@NonNull List<byte[]> paramList, @NonNull List<Object> paramList1)
  {
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator1 = paramList1.iterator();
    for (;;)
    {
      if (localIterator1.hasNext()) {
        paramList1 = localIterator1.next();
      }
      try
      {
        Class.forName("com.google.android.gms.measurement.AppMeasurement$ConditionalUserProperty");
        str1 = zzan(paramList1);
        str2 = zzao(paramList1);
        Iterator localIterator2 = paramList.iterator();
        for (;;)
        {
          if (!localIterator2.hasNext()) {
            break label177;
          }
          localzzb = zzag((byte[])localIterator2.next());
          if (localzzb != null) {
            break;
          }
          if (Log.isLoggable("FirebaseAbtUtil", 2)) {
            Log.v("FirebaseAbtUtil", "Couldn't deserialize the payload; skipping.");
          }
        }
      }
      catch (ClassNotFoundException paramList1)
      {
        String str1;
        String str2;
        zzcgn.zzb localzzb;
        do
        {
          Log.e("FirebaseAbtUtil", "Could not complete the operation due to an internal error.", paramList1);
          break;
        } while ((!localzzb.Hp.equals(str1)) || (!localzzb.Hq.equals(str2)));
        i = 0;
        if (i == 0) {
          continue;
        }
        localArrayList.add(paramList1);
      }
      catch (IllegalAccessException paramList1)
      {
        for (;;) {}
        return localArrayList;
      }
      catch (NoSuchFieldException paramList1)
      {
        for (;;)
        {
          continue;
          label177:
          int i = 1;
        }
      }
    }
  }
  
  static String zze(@NonNull zzcgn.zzb paramzzb, @NonNull zzbtw paramzzbtw)
  {
    if (!TextUtils.isEmpty(paramzzb.Hz)) {
      return paramzzb.Hz;
    }
    return paramzzbtw.zzads();
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/internal/zzbtx.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */