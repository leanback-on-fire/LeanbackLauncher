package com.google.android.gms.internal;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ServiceInfo;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.util.Clock;
import com.google.android.gms.common.util.zze;
import com.google.android.gms.measurement.AppMeasurement.Event;
import com.google.android.gms.measurement.AppMeasurement.zze;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.zip.GZIPOutputStream;
import javax.security.auth.x500.X500Principal;

public class zzauw
  extends zzauk
{
  private final AtomicLong zzbRb = new AtomicLong(0L);
  private int zzbRc;
  
  zzauw(zzauh paramzzauh)
  {
    super(paramzzauh);
  }
  
  static long zzI(byte[] paramArrayOfByte)
  {
    int j = 0;
    zzac.zzC(paramArrayOfByte);
    if (paramArrayOfByte.length > 0) {}
    long l;
    for (boolean bool = true;; bool = false)
    {
      zzac.zzav(bool);
      l = 0L;
      int i = paramArrayOfByte.length - 1;
      while ((i >= 0) && (i >= paramArrayOfByte.length - 8))
      {
        l += ((paramArrayOfByte[i] & 0xFF) << j);
        j += 8;
        i -= 1;
      }
    }
    return l;
  }
  
  /* Error */
  public static Object zzQ(Object paramObject)
  {
    // Byte code:
    //   0: aload_0
    //   1: ifnonnull +5 -> 6
    //   4: aconst_null
    //   5: areturn
    //   6: new 41	java/io/ByteArrayOutputStream
    //   9: dup
    //   10: invokespecial 44	java/io/ByteArrayOutputStream:<init>	()V
    //   13: astore_1
    //   14: new 46	java/io/ObjectOutputStream
    //   17: dup
    //   18: aload_1
    //   19: invokespecial 49	java/io/ObjectOutputStream:<init>	(Ljava/io/OutputStream;)V
    //   22: astore_2
    //   23: aload_2
    //   24: aload_0
    //   25: invokevirtual 53	java/io/ObjectOutputStream:writeObject	(Ljava/lang/Object;)V
    //   28: aload_2
    //   29: invokevirtual 56	java/io/ObjectOutputStream:flush	()V
    //   32: new 58	java/io/ObjectInputStream
    //   35: dup
    //   36: new 60	java/io/ByteArrayInputStream
    //   39: dup
    //   40: aload_1
    //   41: invokevirtual 64	java/io/ByteArrayOutputStream:toByteArray	()[B
    //   44: invokespecial 67	java/io/ByteArrayInputStream:<init>	([B)V
    //   47: invokespecial 70	java/io/ObjectInputStream:<init>	(Ljava/io/InputStream;)V
    //   50: astore_1
    //   51: aload_1
    //   52: invokevirtual 74	java/io/ObjectInputStream:readObject	()Ljava/lang/Object;
    //   55: astore_0
    //   56: aload_2
    //   57: invokevirtual 77	java/io/ObjectOutputStream:close	()V
    //   60: aload_1
    //   61: invokevirtual 78	java/io/ObjectInputStream:close	()V
    //   64: aload_0
    //   65: areturn
    //   66: aload_2
    //   67: ifnull +7 -> 74
    //   70: aload_2
    //   71: invokevirtual 77	java/io/ObjectOutputStream:close	()V
    //   74: aload_1
    //   75: ifnull +7 -> 82
    //   78: aload_1
    //   79: invokevirtual 78	java/io/ObjectInputStream:close	()V
    //   82: aload_0
    //   83: athrow
    //   84: astore_0
    //   85: aconst_null
    //   86: areturn
    //   87: astore_0
    //   88: aconst_null
    //   89: areturn
    //   90: astore_0
    //   91: aconst_null
    //   92: astore_1
    //   93: goto -27 -> 66
    //   96: astore_0
    //   97: goto -31 -> 66
    //   100: astore_0
    //   101: aconst_null
    //   102: astore_1
    //   103: aconst_null
    //   104: astore_2
    //   105: goto -39 -> 66
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	108	0	paramObject	Object
    //   13	90	1	localObject	Object
    //   22	83	2	localObjectOutputStream	java.io.ObjectOutputStream
    // Exception table:
    //   from	to	target	type
    //   56	64	84	java/io/IOException
    //   70	74	84	java/io/IOException
    //   78	82	84	java/io/IOException
    //   82	84	84	java/io/IOException
    //   56	64	87	java/lang/ClassNotFoundException
    //   70	74	87	java/lang/ClassNotFoundException
    //   78	82	87	java/lang/ClassNotFoundException
    //   82	84	87	java/lang/ClassNotFoundException
    //   23	51	90	finally
    //   51	56	96	finally
    //   6	23	100	finally
  }
  
  private Object zza(int paramInt, Object paramObject, boolean paramBoolean)
  {
    Object localObject;
    if (paramObject == null) {
      localObject = null;
    }
    do
    {
      do
      {
        return localObject;
        localObject = paramObject;
      } while ((paramObject instanceof Long));
      localObject = paramObject;
    } while ((paramObject instanceof Double));
    if ((paramObject instanceof Integer)) {
      return Long.valueOf(((Integer)paramObject).intValue());
    }
    if ((paramObject instanceof Byte)) {
      return Long.valueOf(((Byte)paramObject).byteValue());
    }
    if ((paramObject instanceof Short)) {
      return Long.valueOf(((Short)paramObject).shortValue());
    }
    if ((paramObject instanceof Boolean))
    {
      if (((Boolean)paramObject).booleanValue()) {}
      for (long l = 1L;; l = 0L) {
        return Long.valueOf(l);
      }
    }
    if ((paramObject instanceof Float)) {
      return Double.valueOf(((Float)paramObject).doubleValue());
    }
    if (((paramObject instanceof String)) || ((paramObject instanceof Character)) || ((paramObject instanceof CharSequence))) {
      return zzc(String.valueOf(paramObject), paramInt, paramBoolean);
    }
    return null;
  }
  
  public static String zza(zzaux.zzb paramzzb)
  {
    int i = 0;
    if (paramzzb == null) {
      return "null";
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("\nevent_filter {\n");
    zza(localStringBuilder, 0, "filter_id", paramzzb.zzbRi);
    zza(localStringBuilder, 0, "event_name", paramzzb.zzbRj);
    zza(localStringBuilder, 1, "event_count_filter", paramzzb.zzbRm);
    localStringBuilder.append("  filters {\n");
    paramzzb = paramzzb.zzbRk;
    int j = paramzzb.length;
    while (i < j)
    {
      zza(localStringBuilder, 2, paramzzb[i]);
      i += 1;
    }
    zza(localStringBuilder, 1);
    localStringBuilder.append("}\n}\n");
    return localStringBuilder.toString();
  }
  
  public static String zza(zzaux.zze paramzze)
  {
    if (paramzze == null) {
      return "null";
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("\nproperty_filter {\n");
    zza(localStringBuilder, 0, "filter_id", paramzze.zzbRi);
    zza(localStringBuilder, 0, "property_name", paramzze.zzbRy);
    zza(localStringBuilder, 1, paramzze.zzbRz);
    localStringBuilder.append("}\n");
    return localStringBuilder.toString();
  }
  
  private static void zza(StringBuilder paramStringBuilder, int paramInt)
  {
    int i = 0;
    while (i < paramInt)
    {
      paramStringBuilder.append("  ");
      i += 1;
    }
  }
  
  private static void zza(StringBuilder paramStringBuilder, int paramInt, zzaux.zzc paramzzc)
  {
    if (paramzzc == null) {
      return;
    }
    zza(paramStringBuilder, paramInt);
    paramStringBuilder.append("filter {\n");
    zza(paramStringBuilder, paramInt, "complement", paramzzc.zzbRq);
    zza(paramStringBuilder, paramInt, "param_name", paramzzc.zzbRr);
    zza(paramStringBuilder, paramInt + 1, "string_filter", paramzzc.zzbRo);
    zza(paramStringBuilder, paramInt + 1, "number_filter", paramzzc.zzbRp);
    zza(paramStringBuilder, paramInt);
    paramStringBuilder.append("}\n");
  }
  
  private static void zza(StringBuilder paramStringBuilder, int paramInt, zzauz.zze paramzze)
  {
    if (paramzze == null) {
      return;
    }
    zza(paramStringBuilder, paramInt);
    paramStringBuilder.append("bundle {\n");
    zza(paramStringBuilder, paramInt, "protocol_version", paramzze.zzbRY);
    zza(paramStringBuilder, paramInt, "platform", paramzze.zzbSg);
    zza(paramStringBuilder, paramInt, "gmp_version", paramzze.zzbSk);
    zza(paramStringBuilder, paramInt, "uploading_gmp_version", paramzze.zzbSl);
    zza(paramStringBuilder, paramInt, "config_version", paramzze.zzbSx);
    zza(paramStringBuilder, paramInt, "gmp_app_id", paramzze.zzbLI);
    zza(paramStringBuilder, paramInt, "app_id", paramzze.zzaR);
    zza(paramStringBuilder, paramInt, "app_version", paramzze.zzbAI);
    zza(paramStringBuilder, paramInt, "app_version_major", paramzze.zzbSt);
    zza(paramStringBuilder, paramInt, "app_version_minor", paramzze.zzbSu);
    zza(paramStringBuilder, paramInt, "app_version_release", paramzze.zzbSv);
    zza(paramStringBuilder, paramInt, "firebase_instance_id", paramzze.zzbLQ);
    zza(paramStringBuilder, paramInt, "dev_cert_hash", paramzze.zzbSp);
    zza(paramStringBuilder, paramInt, "app_store", paramzze.zzbLJ);
    zza(paramStringBuilder, paramInt, "upload_timestamp_millis", paramzze.zzbSb);
    zza(paramStringBuilder, paramInt, "start_timestamp_millis", paramzze.zzbSc);
    zza(paramStringBuilder, paramInt, "end_timestamp_millis", paramzze.zzbSd);
    zza(paramStringBuilder, paramInt, "previous_bundle_start_timestamp_millis", paramzze.zzbSe);
    zza(paramStringBuilder, paramInt, "previous_bundle_end_timestamp_millis", paramzze.zzbSf);
    zza(paramStringBuilder, paramInt, "app_instance_id", paramzze.zzbSo);
    zza(paramStringBuilder, paramInt, "resettable_device_id", paramzze.zzbSm);
    zza(paramStringBuilder, paramInt, "device_id", paramzze.zzbSw);
    zza(paramStringBuilder, paramInt, "limited_ad_tracking", paramzze.zzbSn);
    zza(paramStringBuilder, paramInt, "os_version", paramzze.zzba);
    zza(paramStringBuilder, paramInt, "device_model", paramzze.zzbSh);
    zza(paramStringBuilder, paramInt, "user_default_language", paramzze.zzbSi);
    zza(paramStringBuilder, paramInt, "time_zone_offset_minutes", paramzze.zzbSj);
    zza(paramStringBuilder, paramInt, "bundle_sequential_index", paramzze.zzbSq);
    zza(paramStringBuilder, paramInt, "service_upload", paramzze.zzbSr);
    zza(paramStringBuilder, paramInt, "health_monitor", paramzze.zzbLM);
    if (paramzze.zzbSy.longValue() != 0L) {
      zza(paramStringBuilder, paramInt, "android_id", paramzze.zzbSy);
    }
    zza(paramStringBuilder, paramInt, paramzze.zzbSa);
    zza(paramStringBuilder, paramInt, paramzze.zzbSs);
    zza(paramStringBuilder, paramInt, paramzze.zzbRZ);
    zza(paramStringBuilder, paramInt);
    paramStringBuilder.append("}\n");
  }
  
  private static void zza(StringBuilder paramStringBuilder, int paramInt, String paramString, zzaux.zzd paramzzd)
  {
    if (paramzzd == null) {
      return;
    }
    zza(paramStringBuilder, paramInt);
    paramStringBuilder.append(paramString);
    paramStringBuilder.append(" {\n");
    if (paramzzd.zzbRs != null)
    {
      paramString = "UNKNOWN_COMPARISON_TYPE";
      switch (paramzzd.zzbRs.intValue())
      {
      }
    }
    for (;;)
    {
      zza(paramStringBuilder, paramInt, "comparison_type", paramString);
      zza(paramStringBuilder, paramInt, "match_as_float", paramzzd.zzbRt);
      zza(paramStringBuilder, paramInt, "comparison_value", paramzzd.zzbRu);
      zza(paramStringBuilder, paramInt, "min_comparison_value", paramzzd.zzbRv);
      zza(paramStringBuilder, paramInt, "max_comparison_value", paramzzd.zzbRw);
      zza(paramStringBuilder, paramInt);
      paramStringBuilder.append("}\n");
      return;
      paramString = "LESS_THAN";
      continue;
      paramString = "GREATER_THAN";
      continue;
      paramString = "EQUAL";
      continue;
      paramString = "BETWEEN";
    }
  }
  
  private static void zza(StringBuilder paramStringBuilder, int paramInt, String paramString, zzaux.zzf paramzzf)
  {
    if (paramzzf == null) {
      return;
    }
    zza(paramStringBuilder, paramInt);
    paramStringBuilder.append(paramString);
    paramStringBuilder.append(" {\n");
    if (paramzzf.zzbRA != null)
    {
      paramString = "UNKNOWN_MATCH_TYPE";
      switch (paramzzf.zzbRA.intValue())
      {
      }
    }
    for (;;)
    {
      zza(paramStringBuilder, paramInt, "match_type", paramString);
      zza(paramStringBuilder, paramInt, "expression", paramzzf.zzbRB);
      zza(paramStringBuilder, paramInt, "case_sensitive", paramzzf.zzbRC);
      if (paramzzf.zzbRD.length <= 0) {
        break label239;
      }
      zza(paramStringBuilder, paramInt + 1);
      paramStringBuilder.append("expression_list {\n");
      paramString = paramzzf.zzbRD;
      int j = paramString.length;
      int i = 0;
      while (i < j)
      {
        paramzzf = paramString[i];
        zza(paramStringBuilder, paramInt + 2);
        paramStringBuilder.append(paramzzf);
        paramStringBuilder.append("\n");
        i += 1;
      }
      paramString = "REGEXP";
      continue;
      paramString = "BEGINS_WITH";
      continue;
      paramString = "ENDS_WITH";
      continue;
      paramString = "PARTIAL";
      continue;
      paramString = "EXACT";
      continue;
      paramString = "IN_LIST";
    }
    paramStringBuilder.append("}\n");
    label239:
    zza(paramStringBuilder, paramInt);
    paramStringBuilder.append("}\n");
  }
  
  private static void zza(StringBuilder paramStringBuilder, int paramInt, String paramString, zzauz.zzf paramzzf)
  {
    int j = 0;
    if (paramzzf == null) {
      return;
    }
    int k = paramInt + 1;
    zza(paramStringBuilder, k);
    paramStringBuilder.append(paramString);
    paramStringBuilder.append(" {\n");
    int m;
    int i;
    long l;
    if (paramzzf.zzbSA != null)
    {
      zza(paramStringBuilder, k + 1);
      paramStringBuilder.append("results: ");
      paramString = paramzzf.zzbSA;
      m = paramString.length;
      i = 0;
      paramInt = 0;
      while (i < m)
      {
        l = paramString[i];
        if (paramInt != 0) {
          paramStringBuilder.append(", ");
        }
        paramStringBuilder.append(Long.valueOf(l));
        i += 1;
        paramInt += 1;
      }
      paramStringBuilder.append('\n');
    }
    if (paramzzf.zzbSz != null)
    {
      zza(paramStringBuilder, k + 1);
      paramStringBuilder.append("status: ");
      paramString = paramzzf.zzbSz;
      m = paramString.length;
      paramInt = 0;
      i = j;
      while (i < m)
      {
        l = paramString[i];
        if (paramInt != 0) {
          paramStringBuilder.append(", ");
        }
        paramStringBuilder.append(Long.valueOf(l));
        i += 1;
        paramInt += 1;
      }
      paramStringBuilder.append('\n');
    }
    zza(paramStringBuilder, k);
    paramStringBuilder.append("}\n");
  }
  
  private static void zza(StringBuilder paramStringBuilder, int paramInt, String paramString, Object paramObject)
  {
    if (paramObject == null) {
      return;
    }
    zza(paramStringBuilder, paramInt + 1);
    paramStringBuilder.append(paramString);
    paramStringBuilder.append(": ");
    paramStringBuilder.append(paramObject);
    paramStringBuilder.append('\n');
  }
  
  private static void zza(StringBuilder paramStringBuilder, int paramInt, zzauz.zza[] paramArrayOfzza)
  {
    if (paramArrayOfzza == null) {
      return;
    }
    int i = paramInt + 1;
    int j = paramArrayOfzza.length;
    paramInt = 0;
    label15:
    zzauz.zza localzza;
    if (paramInt < j)
    {
      localzza = paramArrayOfzza[paramInt];
      if (localzza != null) {
        break label38;
      }
    }
    for (;;)
    {
      paramInt += 1;
      break label15;
      break;
      label38:
      zza(paramStringBuilder, i);
      paramStringBuilder.append("audience_membership {\n");
      zza(paramStringBuilder, i, "audience_id", localzza.zzbRe);
      zza(paramStringBuilder, i, "new_audience", localzza.zzbRP);
      zza(paramStringBuilder, i, "current_data", localzza.zzbRN);
      zza(paramStringBuilder, i, "previous_data", localzza.zzbRO);
      zza(paramStringBuilder, i);
      paramStringBuilder.append("}\n");
    }
  }
  
  private static void zza(StringBuilder paramStringBuilder, int paramInt, zzauz.zzb[] paramArrayOfzzb)
  {
    if (paramArrayOfzzb == null) {
      return;
    }
    int i = paramInt + 1;
    int j = paramArrayOfzzb.length;
    paramInt = 0;
    label15:
    zzauz.zzb localzzb;
    if (paramInt < j)
    {
      localzzb = paramArrayOfzzb[paramInt];
      if (localzzb != null) {
        break label38;
      }
    }
    for (;;)
    {
      paramInt += 1;
      break label15;
      break;
      label38:
      zza(paramStringBuilder, i);
      paramStringBuilder.append("event {\n");
      zza(paramStringBuilder, i, "name", localzzb.name);
      zza(paramStringBuilder, i, "timestamp_millis", localzzb.zzbRS);
      zza(paramStringBuilder, i, "previous_timestamp_millis", localzzb.zzbRT);
      zza(paramStringBuilder, i, "count", localzzb.count);
      zza(paramStringBuilder, i, localzzb.zzbRR);
      zza(paramStringBuilder, i);
      paramStringBuilder.append("}\n");
    }
  }
  
  private static void zza(StringBuilder paramStringBuilder, int paramInt, zzauz.zzc[] paramArrayOfzzc)
  {
    if (paramArrayOfzzc == null) {
      return;
    }
    int i = paramInt + 1;
    int j = paramArrayOfzzc.length;
    paramInt = 0;
    label15:
    zzauz.zzc localzzc;
    if (paramInt < j)
    {
      localzzc = paramArrayOfzzc[paramInt];
      if (localzzc != null) {
        break label38;
      }
    }
    for (;;)
    {
      paramInt += 1;
      break label15;
      break;
      label38:
      zza(paramStringBuilder, i);
      paramStringBuilder.append("param {\n");
      zza(paramStringBuilder, i, "name", localzzc.name);
      zza(paramStringBuilder, i, "string_value", localzzc.stringValue);
      zza(paramStringBuilder, i, "int_value", localzzc.zzbRV);
      zza(paramStringBuilder, i, "double_value", localzzc.zzbQZ);
      zza(paramStringBuilder, i);
      paramStringBuilder.append("}\n");
    }
  }
  
  private static void zza(StringBuilder paramStringBuilder, int paramInt, zzauz.zzg[] paramArrayOfzzg)
  {
    if (paramArrayOfzzg == null) {
      return;
    }
    int i = paramInt + 1;
    int j = paramArrayOfzzg.length;
    paramInt = 0;
    label15:
    zzauz.zzg localzzg;
    if (paramInt < j)
    {
      localzzg = paramArrayOfzzg[paramInt];
      if (localzzg != null) {
        break label38;
      }
    }
    for (;;)
    {
      paramInt += 1;
      break label15;
      break;
      label38:
      zza(paramStringBuilder, i);
      paramStringBuilder.append("user_property {\n");
      zza(paramStringBuilder, i, "set_timestamp_millis", localzzg.zzbSC);
      zza(paramStringBuilder, i, "name", localzzg.name);
      zza(paramStringBuilder, i, "string_value", localzzg.stringValue);
      zza(paramStringBuilder, i, "int_value", localzzg.zzbRV);
      zza(paramStringBuilder, i, "double_value", localzzg.zzbQZ);
      zza(paramStringBuilder, i);
      paramStringBuilder.append("}\n");
    }
  }
  
  public static boolean zza(Context paramContext, String paramString, boolean paramBoolean)
  {
    try
    {
      PackageManager localPackageManager = paramContext.getPackageManager();
      if (localPackageManager == null) {
        return false;
      }
      paramContext = localPackageManager.getReceiverInfo(new ComponentName(paramContext, paramString), 2);
      if ((paramContext != null) && (paramContext.enabled)) {
        if (paramBoolean)
        {
          paramBoolean = paramContext.exported;
          if (!paramBoolean) {}
        }
        else
        {
          return true;
        }
      }
    }
    catch (PackageManager.NameNotFoundException paramContext) {}
    return false;
  }
  
  public static boolean zza(long[] paramArrayOfLong, int paramInt)
  {
    if (paramInt >= paramArrayOfLong.length * 64) {}
    while ((paramArrayOfLong[(paramInt / 64)] & 1L << paramInt % 64) == 0L) {
      return false;
    }
    return true;
  }
  
  public static long[] zza(BitSet paramBitSet)
  {
    int k = (paramBitSet.length() + 63) / 64;
    long[] arrayOfLong = new long[k];
    int i = 0;
    if (i < k)
    {
      arrayOfLong[i] = 0L;
      int j = 0;
      for (;;)
      {
        if ((j >= 64) || (i * 64 + j >= paramBitSet.length()))
        {
          i += 1;
          break;
        }
        if (paramBitSet.get(i * 64 + j)) {
          arrayOfLong[i] |= 1L << j;
        }
        j += 1;
      }
    }
    return arrayOfLong;
  }
  
  public static boolean zzao(String paramString1, String paramString2)
  {
    if ((paramString1 == null) && (paramString2 == null)) {
      return true;
    }
    if (paramString1 == null) {
      return false;
    }
    return paramString1.equals(paramString2);
  }
  
  public static String zzb(zzauz.zzd paramzzd)
  {
    if (paramzzd == null) {
      return "";
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("\nbatch {\n");
    if (paramzzd.zzbRW != null)
    {
      paramzzd = paramzzd.zzbRW;
      int j = paramzzd.length;
      int i = 0;
      if (i < j)
      {
        zzauz.zze localzze = paramzzd[i];
        if (localzze == null) {}
        for (;;)
        {
          i += 1;
          break;
          zza(localStringBuilder, 1, localzze);
        }
      }
    }
    localStringBuilder.append("}\n");
    return localStringBuilder.toString();
  }
  
  static MessageDigest zzbJ(String paramString)
  {
    int i = 0;
    while (i < 2) {
      try
      {
        MessageDigest localMessageDigest = MessageDigest.getInstance(paramString);
        if (localMessageDigest != null) {
          return localMessageDigest;
        }
      }
      catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
      {
        i += 1;
      }
    }
    return null;
  }
  
  static boolean zzfW(String paramString)
  {
    boolean bool = false;
    zzac.zzdc(paramString);
    if (paramString.charAt(0) != '_') {
      bool = true;
    }
    return bool;
  }
  
  private int zzgf(String paramString)
  {
    if ("_ldl".equals(paramString)) {
      return zzMi().zzMN();
    }
    return zzMi().zzMM();
  }
  
  public static boolean zzgg(String paramString)
  {
    return (!TextUtils.isEmpty(paramString)) && (paramString.startsWith("_"));
  }
  
  static boolean zzgi(String paramString)
  {
    return (paramString != null) && (paramString.matches("(\\+|-)?([0-9]+\\.?[0-9]*|[0-9]*\\.?[0-9]+)")) && (paramString.length() <= 310);
  }
  
  public static boolean zzy(Context paramContext, String paramString)
  {
    try
    {
      PackageManager localPackageManager = paramContext.getPackageManager();
      if (localPackageManager == null) {
        return false;
      }
      paramContext = localPackageManager.getServiceInfo(new ComponentName(paramContext, paramString), 4);
      if (paramContext != null)
      {
        boolean bool = paramContext.enabled;
        if (bool) {
          return true;
        }
      }
    }
    catch (PackageManager.NameNotFoundException paramContext) {}
    return false;
  }
  
  protected void onInitialize()
  {
    SecureRandom localSecureRandom = new SecureRandom();
    long l2 = localSecureRandom.nextLong();
    long l1 = l2;
    if (l2 == 0L)
    {
      l2 = localSecureRandom.nextLong();
      l1 = l2;
      if (l2 == 0L)
      {
        zzMg().zzNV().log("Utils falling back to Random for random id");
        l1 = l2;
      }
    }
    this.zzbRb.set(l1);
  }
  
  /* Error */
  public byte[] zzH(byte[] paramArrayOfByte)
    throws IOException
  {
    // Byte code:
    //   0: new 60	java/io/ByteArrayInputStream
    //   3: dup
    //   4: aload_1
    //   5: invokespecial 67	java/io/ByteArrayInputStream:<init>	([B)V
    //   8: astore_1
    //   9: new 775	java/util/zip/GZIPInputStream
    //   12: dup
    //   13: aload_1
    //   14: invokespecial 776	java/util/zip/GZIPInputStream:<init>	(Ljava/io/InputStream;)V
    //   17: astore_3
    //   18: new 41	java/io/ByteArrayOutputStream
    //   21: dup
    //   22: invokespecial 44	java/io/ByteArrayOutputStream:<init>	()V
    //   25: astore 4
    //   27: sipush 1024
    //   30: newarray <illegal type>
    //   32: astore 5
    //   34: aload_3
    //   35: aload 5
    //   37: invokevirtual 780	java/util/zip/GZIPInputStream:read	([B)I
    //   40: istore_2
    //   41: iload_2
    //   42: ifgt +17 -> 59
    //   45: aload_3
    //   46: invokevirtual 781	java/util/zip/GZIPInputStream:close	()V
    //   49: aload_1
    //   50: invokevirtual 782	java/io/ByteArrayInputStream:close	()V
    //   53: aload 4
    //   55: invokevirtual 64	java/io/ByteArrayOutputStream:toByteArray	()[B
    //   58: areturn
    //   59: aload 4
    //   61: aload 5
    //   63: iconst_0
    //   64: iload_2
    //   65: invokevirtual 786	java/io/ByteArrayOutputStream:write	([BII)V
    //   68: goto -34 -> 34
    //   71: astore_1
    //   72: aload_0
    //   73: invokevirtual 754	com/google/android/gms/internal/zzauw:zzMg	()Lcom/google/android/gms/internal/zzaua;
    //   76: invokevirtual 789	com/google/android/gms/internal/zzaua:zzNT	()Lcom/google/android/gms/internal/zzaua$zza;
    //   79: ldc_w 791
    //   82: aload_1
    //   83: invokevirtual 795	com/google/android/gms/internal/zzaua$zza:zzm	(Ljava/lang/String;Ljava/lang/Object;)V
    //   86: aload_1
    //   87: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	88	0	this	zzauw
    //   0	88	1	paramArrayOfByte	byte[]
    //   40	25	2	i	int
    //   17	29	3	localGZIPInputStream	java.util.zip.GZIPInputStream
    //   25	35	4	localByteArrayOutputStream	ByteArrayOutputStream
    //   32	30	5	arrayOfByte	byte[]
    // Exception table:
    //   from	to	target	type
    //   0	34	71	java/io/IOException
    //   34	41	71	java/io/IOException
    //   45	59	71	java/io/IOException
    //   59	68	71	java/io/IOException
  }
  
  @WorkerThread
  public long zzJ(byte[] paramArrayOfByte)
  {
    zzac.zzC(paramArrayOfByte);
    zzmW();
    MessageDigest localMessageDigest = zzbJ("MD5");
    if (localMessageDigest == null)
    {
      zzMg().zzNT().log("Failed to get MD5");
      return 0L;
    }
    return zzI(localMessageDigest.digest(paramArrayOfByte));
  }
  
  public boolean zzM(Intent paramIntent)
  {
    paramIntent = paramIntent.getStringExtra("android.intent.extra.REFERRER_NAME");
    return ("android-app://com.google.android.googlequicksearchbox/https/www.google.com".equals(paramIntent)) || ("https://www.google.com".equals(paramIntent)) || ("android-app://com.google.appcrawler".equals(paramIntent));
  }
  
  @WorkerThread
  long zzP(Context paramContext, String paramString)
  {
    zzmW();
    zzac.zzC(paramContext);
    zzac.zzdc(paramString);
    PackageManager localPackageManager = paramContext.getPackageManager();
    MessageDigest localMessageDigest = zzbJ("MD5");
    if (localMessageDigest == null)
    {
      zzMg().zzNT().log("Could not get MD5 instance");
      return -1L;
    }
    if (localPackageManager != null) {
      try
      {
        if (!zzQ(paramContext, paramString))
        {
          paramContext = zzaca.zzbp(paramContext).getPackageInfo(getContext().getPackageName(), 64);
          if ((paramContext.signatures != null) && (paramContext.signatures.length > 0)) {
            return zzI(localMessageDigest.digest(paramContext.signatures[0].toByteArray()));
          }
          zzMg().zzNV().log("Could not get signatures");
          return -1L;
        }
      }
      catch (PackageManager.NameNotFoundException paramContext)
      {
        zzMg().zzNT().zzm("Package name not found", paramContext);
      }
    }
    return 0L;
  }
  
  public long zzPd()
  {
    long l1;
    if (this.zzbRb.get() == 0L) {
      synchronized (this.zzbRb)
      {
        l1 = new Random(System.nanoTime() ^ zznq().currentTimeMillis()).nextLong();
        int i = this.zzbRc + 1;
        this.zzbRc = i;
        long l2 = i;
        return l1 + l2;
      }
    }
    synchronized (this.zzbRb)
    {
      this.zzbRb.compareAndSet(-1L, 1L);
      l1 = this.zzbRb.getAndIncrement();
      return l1;
    }
  }
  
  boolean zzQ(Context paramContext, String paramString)
  {
    X500Principal localX500Principal = new X500Principal("CN=Android Debug,O=Android,C=US");
    try
    {
      paramContext = zzaca.zzbp(paramContext).getPackageInfo(paramString, 64);
      if ((paramContext != null) && (paramContext.signatures != null) && (paramContext.signatures.length > 0))
      {
        paramContext = paramContext.signatures[0];
        boolean bool = ((X509Certificate)CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(paramContext.toByteArray()))).getSubjectX500Principal().equals(localX500Principal);
        return bool;
      }
    }
    catch (CertificateException paramContext)
    {
      zzMg().zzNT().zzm("Error obtaining certificate", paramContext);
      return true;
    }
    catch (PackageManager.NameNotFoundException paramContext)
    {
      for (;;)
      {
        zzMg().zzNT().zzm("Package name not found", paramContext);
      }
    }
  }
  
  Bundle zzW(Bundle paramBundle)
  {
    Bundle localBundle = new Bundle();
    if (paramBundle != null)
    {
      Iterator localIterator = paramBundle.keySet().iterator();
      while (localIterator.hasNext())
      {
        String str = (String)localIterator.next();
        Object localObject = zzMc().zzo(str, paramBundle.get(str));
        if (localObject == null) {
          zzMg().zzNV().zzm("Param value can't be null", str);
        } else {
          zzMc().zza(localBundle, str, localObject);
        }
      }
    }
    return localBundle;
  }
  
  public Bundle zza(String paramString, Bundle paramBundle, @Nullable List<String> paramList, boolean paramBoolean)
  {
    Bundle localBundle = null;
    int i;
    String str1;
    int j;
    if (paramBundle != null)
    {
      localBundle = new Bundle(paramBundle);
      zzMi().zzMG();
      Iterator localIterator = paramBundle.keySet().iterator();
      i = 0;
      if (localIterator.hasNext())
      {
        str1 = (String)localIterator.next();
        if ((paramList != null) && (paramList.contains(str1))) {
          break label356;
        }
        if (!paramBoolean) {
          break label350;
        }
        j = zzgb(str1);
        label89:
        k = j;
        if (j != 0) {}
      }
    }
    label350:
    label356:
    for (int k = zzgc(str1);; k = 0)
    {
      if (k != 0)
      {
        if (zzd(localBundle, k))
        {
          localBundle.putString("_ev", zzc(str1, zzMi().zzMJ(), true));
          if (k == 3) {
            zzb(localBundle, str1);
          }
        }
        localBundle.remove(str1);
        break;
      }
      if ((!zzn(str1, paramBundle.get(str1))) && (!"_ev".equals(str1)))
      {
        if (zzd(localBundle, 4))
        {
          localBundle.putString("_ev", zzc(str1, zzMi().zzMJ(), true));
          zzb(localBundle, paramBundle.get(str1));
        }
        localBundle.remove(str1);
        break;
      }
      j = i;
      if (zzfW(str1))
      {
        i += 1;
        j = i;
        if (i > 25)
        {
          String str2 = 48 + "Event can't contain more then " + 25 + " params";
          zzMg().zzNT().zze(str2, paramString, paramBundle);
          zzd(localBundle, 5);
          localBundle.remove(str1);
          break;
        }
      }
      i = j;
      break;
      return localBundle;
      j = 0;
      break label89;
    }
  }
  
  zzatt zza(String paramString1, Bundle paramBundle, String paramString2, long paramLong, boolean paramBoolean1, boolean paramBoolean2)
  {
    if (TextUtils.isEmpty(paramString1)) {
      return null;
    }
    if (zzMc().zzfY(paramString1) != 0)
    {
      zzMg().zzNT().zzm("Invalid conditional property event name", paramString1);
      throw new IllegalArgumentException();
    }
    if (paramBundle != null)
    {
      paramBundle = new Bundle(paramBundle);
      paramBundle.putString("_o", paramString2);
      List localList = zze.zzD("_o");
      paramBundle = zzMc().zza(paramString1, paramBundle, localList, paramBoolean2);
      if (!paramBoolean1) {
        break label127;
      }
      paramBundle = zzW(paramBundle);
    }
    label127:
    for (;;)
    {
      return new zzatt(paramString1, new zzatr(paramBundle), paramString2, paramLong);
      paramBundle = new Bundle();
      break;
    }
  }
  
  public void zza(int paramInt1, String paramString1, String paramString2, int paramInt2)
  {
    zza(null, paramInt1, paramString1, paramString2, paramInt2);
  }
  
  public void zza(Bundle paramBundle, String paramString, Object paramObject)
  {
    if (paramBundle == null) {}
    do
    {
      return;
      if ((paramObject instanceof Long))
      {
        paramBundle.putLong(paramString, ((Long)paramObject).longValue());
        return;
      }
      if ((paramObject instanceof String))
      {
        paramBundle.putString(paramString, String.valueOf(paramObject));
        return;
      }
      if ((paramObject instanceof Double))
      {
        paramBundle.putDouble(paramString, ((Double)paramObject).doubleValue());
        return;
      }
    } while (paramString == null);
    if (paramObject != null) {}
    for (paramBundle = paramObject.getClass().getSimpleName();; paramBundle = null)
    {
      zzMg().zzNW().zze("Not putting event parameter. Invalid value type. name, type", paramString, paramBundle);
      return;
    }
  }
  
  public void zza(zzauz.zzc paramzzc, Object paramObject)
  {
    zzac.zzC(paramObject);
    paramzzc.stringValue = null;
    paramzzc.zzbRV = null;
    paramzzc.zzbQZ = null;
    if ((paramObject instanceof String))
    {
      paramzzc.stringValue = ((String)paramObject);
      return;
    }
    if ((paramObject instanceof Long))
    {
      paramzzc.zzbRV = ((Long)paramObject);
      return;
    }
    if ((paramObject instanceof Double))
    {
      paramzzc.zzbQZ = ((Double)paramObject);
      return;
    }
    zzMg().zzNT().zzm("Ignoring invalid (type) event param value", paramObject);
  }
  
  public void zza(zzauz.zzg paramzzg, Object paramObject)
  {
    zzac.zzC(paramObject);
    paramzzg.stringValue = null;
    paramzzg.zzbRV = null;
    paramzzg.zzbQZ = null;
    if ((paramObject instanceof String))
    {
      paramzzg.stringValue = ((String)paramObject);
      return;
    }
    if ((paramObject instanceof Long))
    {
      paramzzg.zzbRV = ((Long)paramObject);
      return;
    }
    if ((paramObject instanceof Double))
    {
      paramzzg.zzbQZ = ((Double)paramObject);
      return;
    }
    zzMg().zzNT().zzm("Ignoring invalid (type) user attribute value", paramObject);
  }
  
  public void zza(String paramString1, int paramInt1, String paramString2, String paramString3, int paramInt2)
  {
    paramString1 = new Bundle();
    zzd(paramString1, paramInt1);
    if (!TextUtils.isEmpty(paramString2)) {
      paramString1.putString(paramString2, paramString3);
    }
    if ((paramInt1 == 6) || (paramInt1 == 7) || (paramInt1 == 2)) {
      paramString1.putLong("_el", paramInt2);
    }
    this.zzbLa.zzMi().zzNb();
    this.zzbLa.zzLV().zzd("auto", "_err", paramString1);
  }
  
  boolean zza(String paramString1, String paramString2, int paramInt, Object paramObject)
  {
    if (paramObject == null) {}
    do
    {
      do
      {
        return true;
      } while (((paramObject instanceof Long)) || ((paramObject instanceof Float)) || ((paramObject instanceof Integer)) || ((paramObject instanceof Byte)) || ((paramObject instanceof Short)) || ((paramObject instanceof Boolean)) || ((paramObject instanceof Double)));
      if ((!(paramObject instanceof String)) && (!(paramObject instanceof Character)) && (!(paramObject instanceof CharSequence))) {
        break;
      }
      paramObject = String.valueOf(paramObject);
    } while (((String)paramObject).length() <= paramInt);
    zzMg().zzNV().zzd("Value is too long; discarded. Value kind, name, value length", paramString1, paramString2, Integer.valueOf(((String)paramObject).length()));
    return false;
    return false;
  }
  
  byte[] zza(Parcelable paramParcelable)
  {
    if (paramParcelable == null) {
      return null;
    }
    Parcel localParcel = Parcel.obtain();
    try
    {
      paramParcelable.writeToParcel(localParcel, 0);
      paramParcelable = localParcel.marshall();
      return paramParcelable;
    }
    finally
    {
      localParcel.recycle();
    }
  }
  
  public byte[] zza(zzauz.zzd paramzzd)
  {
    try
    {
      byte[] arrayOfByte = new byte[paramzzd.zzann()];
      zzcfy localzzcfy = zzcfy.zzas(arrayOfByte);
      paramzzd.writeTo(localzzcfy);
      localzzcfy.zzana();
      return arrayOfByte;
    }
    catch (IOException paramzzd)
    {
      zzMg().zzNT().zzm("Data loss. Failed to serialize batch", paramzzd);
    }
    return null;
  }
  
  boolean zzam(String paramString1, String paramString2)
  {
    if (paramString2 == null)
    {
      zzMg().zzNT().zzm("Name is required and can't be null. Type", paramString1);
      return false;
    }
    if (paramString2.length() == 0)
    {
      zzMg().zzNT().zzm("Name is required and can't be empty. Type", paramString1);
      return false;
    }
    int i = paramString2.codePointAt(0);
    if (!Character.isLetter(i))
    {
      zzMg().zzNT().zze("Name must start with a letter. Type, name", paramString1, paramString2);
      return false;
    }
    int j = paramString2.length();
    i = Character.charCount(i);
    while (i < j)
    {
      int k = paramString2.codePointAt(i);
      if ((k != 95) && (!Character.isLetterOrDigit(k)))
      {
        zzMg().zzNT().zze("Name must consist of letters, digits or _ (underscores). Type, name", paramString1, paramString2);
        return false;
      }
      i += Character.charCount(k);
    }
    return true;
  }
  
  boolean zzan(String paramString1, String paramString2)
  {
    if (paramString2 == null)
    {
      zzMg().zzNT().zzm("Name is required and can't be null. Type", paramString1);
      return false;
    }
    if (paramString2.length() == 0)
    {
      zzMg().zzNT().zzm("Name is required and can't be empty. Type", paramString1);
      return false;
    }
    int i = paramString2.codePointAt(0);
    if ((!Character.isLetter(i)) && (i != 95))
    {
      zzMg().zzNT().zze("Name must start with a letter or _ (underscore). Type, name", paramString1, paramString2);
      return false;
    }
    int j = paramString2.length();
    i = Character.charCount(i);
    while (i < j)
    {
      int k = paramString2.codePointAt(i);
      if ((k != 95) && (!Character.isLetterOrDigit(k)))
      {
        zzMg().zzNT().zze("Name must consist of letters, digits or _ (underscores). Type, name", paramString1, paramString2);
        return false;
      }
      i += Character.charCount(k);
    }
    return true;
  }
  
  <T extends Parcelable> T zzb(byte[] paramArrayOfByte, Parcelable.Creator<T> paramCreator)
  {
    if (paramArrayOfByte == null) {
      return null;
    }
    Parcel localParcel = Parcel.obtain();
    try
    {
      localParcel.unmarshall(paramArrayOfByte, 0, paramArrayOfByte.length);
      localParcel.setDataPosition(0);
      paramArrayOfByte = (Parcelable)paramCreator.createFromParcel(localParcel);
      return paramArrayOfByte;
    }
    catch (zzb.zza paramArrayOfByte)
    {
      zzMg().zzNT().log("Failed to load parcelable from buffer");
      return null;
    }
    finally
    {
      localParcel.recycle();
    }
  }
  
  public void zzb(Bundle paramBundle, Object paramObject)
  {
    zzac.zzC(paramBundle);
    if ((paramObject != null) && (((paramObject instanceof String)) || ((paramObject instanceof CharSequence)))) {
      paramBundle.putLong("_el", String.valueOf(paramObject).length());
    }
  }
  
  boolean zzb(String paramString1, int paramInt, String paramString2)
  {
    if (paramString2 == null)
    {
      zzMg().zzNT().zzm("Name is required and can't be null. Type", paramString1);
      return false;
    }
    if (paramString2.length() > paramInt)
    {
      zzMg().zzNT().zzd("Name is too long. Type, maximum supported length, name", paramString1, Integer.valueOf(paramInt), paramString2);
      return false;
    }
    return true;
  }
  
  @WorkerThread
  public boolean zzby(String paramString)
  {
    zzmW();
    if (zzaca.zzbp(getContext()).checkCallingOrSelfPermission(paramString) == 0) {
      return true;
    }
    zzMg().zzNY().zzm("Permission not granted", paramString);
    return false;
  }
  
  public String zzc(String paramString, int paramInt, boolean paramBoolean)
  {
    String str = paramString;
    if (paramString.length() > paramInt)
    {
      if (paramBoolean) {
        str = String.valueOf(paramString.substring(0, paramInt)).concat("...");
      }
    }
    else {
      return str;
    }
    return null;
  }
  
  boolean zzc(String paramString1, Map<String, String> paramMap, String paramString2)
  {
    if (paramString2 == null)
    {
      zzMg().zzNT().zzm("Name is required and can't be null. Type", paramString1);
      return false;
    }
    if (paramString2.startsWith("firebase_"))
    {
      zzMg().zzNT().zze("Name starts with reserved prefix. Type, name", paramString1, paramString2);
      return false;
    }
    if ((paramMap != null) && (paramMap.containsKey(paramString2)))
    {
      zzMg().zzNT().zze("Name is reserved. Type, name", paramString1, paramString2);
      return false;
    }
    return true;
  }
  
  public boolean zzd(Bundle paramBundle, int paramInt)
  {
    if (paramBundle == null) {}
    while (paramBundle.getLong("_err") != 0L) {
      return false;
    }
    paramBundle.putLong("_err", paramInt);
    return true;
  }
  
  @WorkerThread
  boolean zzd(zzatt paramzzatt, zzatg paramzzatg)
  {
    zzac.zzC(paramzzatt);
    zzac.zzC(paramzzatg);
    if (TextUtils.isEmpty(paramzzatg.zzbLI))
    {
      zzMi().zzNb();
      return false;
    }
    return true;
  }
  
  public int zzfX(String paramString)
  {
    if (!zzam("event", paramString)) {}
    do
    {
      return 2;
      if (!zzc("event", AppMeasurement.Event.zzbLb, paramString)) {
        return 13;
      }
    } while (!zzb("event", zzMi().zzMH(), paramString));
    return 0;
  }
  
  public int zzfY(String paramString)
  {
    if (!zzan("event", paramString)) {}
    do
    {
      return 2;
      if (!zzc("event", AppMeasurement.Event.zzbLb, paramString)) {
        return 13;
      }
    } while (!zzb("event", zzMi().zzMH(), paramString));
    return 0;
  }
  
  public int zzfZ(String paramString)
  {
    if (!zzam("user property", paramString)) {}
    do
    {
      return 6;
      if (!zzc("user property", AppMeasurement.zze.zzbLf, paramString)) {
        return 15;
      }
    } while (!zzb("user property", zzMi().zzMI(), paramString));
    return 0;
  }
  
  public int zzga(String paramString)
  {
    if (!zzan("user property", paramString)) {}
    do
    {
      return 6;
      if (!zzc("user property", AppMeasurement.zze.zzbLf, paramString)) {
        return 15;
      }
    } while (!zzb("user property", zzMi().zzMI(), paramString));
    return 0;
  }
  
  public int zzgb(String paramString)
  {
    if (!zzam("event param", paramString)) {}
    do
    {
      return 3;
      if (!zzc("event param", null, paramString)) {
        return 14;
      }
    } while (!zzb("event param", zzMi().zzMJ(), paramString));
    return 0;
  }
  
  public int zzgc(String paramString)
  {
    if (!zzan("event param", paramString)) {}
    do
    {
      return 3;
      if (!zzc("event param", null, paramString)) {
        return 14;
      }
    } while (!zzb("event param", zzMi().zzMJ(), paramString));
    return 0;
  }
  
  public boolean zzgd(String paramString)
  {
    if (TextUtils.isEmpty(paramString))
    {
      zzMg().zzNT().log("Missing google_app_id. Firebase Analytics disabled. See https://goo.gl/NAOOOI");
      return false;
    }
    if (!zzge(paramString))
    {
      zzMg().zzNT().zzm("Invalid google_app_id. Firebase Analytics disabled. See https://goo.gl/NAOOOI. provided id", paramString);
      return false;
    }
    return true;
  }
  
  boolean zzge(String paramString)
  {
    zzac.zzC(paramString);
    return paramString.matches("^1:\\d+:android:[a-f0-9]+$");
  }
  
  public boolean zzgh(String paramString)
  {
    if (TextUtils.isEmpty(paramString)) {
      return false;
    }
    String str = zzMi().zzNx();
    zzMi().zzNb();
    return str.equals(paramString);
  }
  
  boolean zzgj(String paramString)
  {
    return "1".equals(zzMd().zzaj(paramString, "measurement.upload.blacklist_internal"));
  }
  
  boolean zzgk(String paramString)
  {
    return "1".equals(zzMd().zzaj(paramString, "measurement.upload.blacklist_public"));
  }
  
  @WorkerThread
  boolean zzgl(String paramString)
  {
    boolean bool = true;
    zzac.zzdc(paramString);
    int i = -1;
    switch (paramString.hashCode())
    {
    }
    for (;;)
    {
      switch (i)
      {
      default: 
        bool = false;
      }
      return bool;
      if (paramString.equals("_in"))
      {
        i = 0;
        continue;
        if (paramString.equals("_ui"))
        {
          i = 1;
          continue;
          if (paramString.equals("_ug")) {
            i = 2;
          }
        }
      }
    }
  }
  
  public boolean zzk(long paramLong1, long paramLong2)
  {
    if ((paramLong1 == 0L) || (paramLong2 <= 0L)) {}
    while (Math.abs(zznq().currentTimeMillis() - paramLong1) > paramLong2) {
      return true;
    }
    return false;
  }
  
  public byte[] zzk(byte[] paramArrayOfByte)
    throws IOException
  {
    try
    {
      ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
      GZIPOutputStream localGZIPOutputStream = new GZIPOutputStream(localByteArrayOutputStream);
      localGZIPOutputStream.write(paramArrayOfByte);
      localGZIPOutputStream.close();
      localByteArrayOutputStream.close();
      paramArrayOfByte = localByteArrayOutputStream.toByteArray();
      return paramArrayOfByte;
    }
    catch (IOException paramArrayOfByte)
    {
      zzMg().zzNT().zzm("Failed to gzip content", paramArrayOfByte);
      throw paramArrayOfByte;
    }
  }
  
  public boolean zzn(String paramString, Object paramObject)
  {
    if (zzgg(paramString)) {
      return zza("param", paramString, zzMi().zzML(), paramObject);
    }
    return zza("param", paramString, zzMi().zzMK(), paramObject);
  }
  
  public Object zzo(String paramString, Object paramObject)
  {
    if ("_ev".equals(paramString)) {
      return zza(zzMi().zzML(), paramObject, true);
    }
    if (zzgg(paramString)) {}
    for (int i = zzMi().zzML();; i = zzMi().zzMK()) {
      return zza(i, paramObject, false);
    }
  }
  
  public int zzp(String paramString, Object paramObject)
  {
    if ("_ldl".equals(paramString)) {}
    for (boolean bool = zza("user property referrer", paramString, zzgf(paramString), paramObject); bool; bool = zza("user property", paramString, zzgf(paramString), paramObject)) {
      return 0;
    }
    return 7;
  }
  
  public Object zzq(String paramString, Object paramObject)
  {
    if ("_ldl".equals(paramString)) {
      return zza(zzgf(paramString), paramObject, true);
    }
    return zza(zzgf(paramString), paramObject, false);
  }
  
  public Bundle zzx(@NonNull Uri paramUri)
  {
    Object localObject = null;
    if (paramUri == null) {
      return (Bundle)localObject;
    }
    for (;;)
    {
      try
      {
        if (paramUri.isHierarchical())
        {
          str4 = paramUri.getQueryParameter("utm_campaign");
          str3 = paramUri.getQueryParameter("utm_source");
          str2 = paramUri.getQueryParameter("utm_medium");
          str1 = paramUri.getQueryParameter("gclid");
          if ((TextUtils.isEmpty(str4)) && (TextUtils.isEmpty(str3)) && (TextUtils.isEmpty(str2)) && (TextUtils.isEmpty(str1))) {
            break;
          }
          Bundle localBundle = new Bundle();
          if (!TextUtils.isEmpty(str4)) {
            localBundle.putString("campaign", str4);
          }
          if (!TextUtils.isEmpty(str3)) {
            localBundle.putString("source", str3);
          }
          if (!TextUtils.isEmpty(str2)) {
            localBundle.putString("medium", str2);
          }
          if (!TextUtils.isEmpty(str1)) {
            localBundle.putString("gclid", str1);
          }
          str1 = paramUri.getQueryParameter("utm_term");
          if (!TextUtils.isEmpty(str1)) {
            localBundle.putString("term", str1);
          }
          str1 = paramUri.getQueryParameter("utm_content");
          if (!TextUtils.isEmpty(str1)) {
            localBundle.putString("content", str1);
          }
          str1 = paramUri.getQueryParameter("aclid");
          if (!TextUtils.isEmpty(str1)) {
            localBundle.putString("aclid", str1);
          }
          str1 = paramUri.getQueryParameter("cp1");
          if (!TextUtils.isEmpty(str1)) {
            localBundle.putString("cp1", str1);
          }
          paramUri = paramUri.getQueryParameter("anid");
          localObject = localBundle;
          if (TextUtils.isEmpty(paramUri)) {
            break;
          }
          localBundle.putString("anid", paramUri);
          return localBundle;
        }
      }
      catch (UnsupportedOperationException paramUri)
      {
        zzMg().zzNV().zzm("Install referrer url isn't a hierarchical URI", paramUri);
        return null;
      }
      String str1 = null;
      String str2 = null;
      String str3 = null;
      String str4 = null;
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/internal/zzauw.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */