package com.google.android.gms.internal;

import android.support.annotation.WorkerThread;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.measurement.AppMeasurement.Event;
import com.google.android.gms.measurement.AppMeasurement.Param;
import com.google.android.gms.measurement.AppMeasurement.zze;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class zzati
  extends zzauk
{
  zzati(zzauh paramzzauh)
  {
    super(paramzzauh);
  }
  
  private Boolean zza(zzaux.zzb paramzzb, zzauz.zzb paramzzb1, long paramLong)
  {
    if (paramzzb.zzbRm != null)
    {
      localObject1 = zza(paramLong, paramzzb.zzbRm);
      if (localObject1 == null) {
        return null;
      }
      if (!((Boolean)localObject1).booleanValue()) {
        return Boolean.valueOf(false);
      }
    }
    Object localObject2 = new HashSet();
    Object localObject1 = paramzzb.zzbRk;
    int j = localObject1.length;
    int i = 0;
    while (i < j)
    {
      localObject3 = localObject1[i];
      if (TextUtils.isEmpty(((zzaux.zzc)localObject3).zzbRr))
      {
        zzMg().zzNV().zzm("null or empty param name in filter. event", paramzzb1.name);
        return null;
      }
      ((Set)localObject2).add(((zzaux.zzc)localObject3).zzbRr);
      i += 1;
    }
    localObject1 = new ArrayMap();
    Object localObject3 = paramzzb1.zzbRR;
    j = localObject3.length;
    i = 0;
    Object localObject4;
    if (i < j)
    {
      localObject4 = localObject3[i];
      if (((Set)localObject2).contains(((zzauz.zzc)localObject4).name))
      {
        if (((zzauz.zzc)localObject4).zzbRV == null) {
          break label213;
        }
        ((Map)localObject1).put(((zzauz.zzc)localObject4).name, ((zzauz.zzc)localObject4).zzbRV);
      }
      for (;;)
      {
        i += 1;
        break;
        label213:
        if (((zzauz.zzc)localObject4).zzbQZ != null)
        {
          ((Map)localObject1).put(((zzauz.zzc)localObject4).name, ((zzauz.zzc)localObject4).zzbQZ);
        }
        else
        {
          if (((zzauz.zzc)localObject4).stringValue == null) {
            break label271;
          }
          ((Map)localObject1).put(((zzauz.zzc)localObject4).name, ((zzauz.zzc)localObject4).stringValue);
        }
      }
      label271:
      zzMg().zzNV().zze("Unknown value for param. event, param", paramzzb1.name, ((zzauz.zzc)localObject4).name);
      return null;
    }
    localObject2 = paramzzb.zzbRk;
    int k = localObject2.length;
    i = 0;
    while (i < k)
    {
      paramzzb = localObject2[i];
      int m = Boolean.TRUE.equals(paramzzb.zzbRq);
      localObject3 = paramzzb.zzbRr;
      if (TextUtils.isEmpty((CharSequence)localObject3))
      {
        zzMg().zzNV().zzm("Event has empty param name. event", paramzzb1.name);
        return null;
      }
      localObject4 = ((Map)localObject1).get(localObject3);
      if ((localObject4 instanceof Long))
      {
        if (paramzzb.zzbRp == null)
        {
          zzMg().zzNV().zze("No number filter for long param. event, param", paramzzb1.name, localObject3);
          return null;
        }
        paramzzb = zza(((Long)localObject4).longValue(), paramzzb.zzbRp);
        if (paramzzb == null) {
          return null;
        }
        if (!paramzzb.booleanValue()) {}
        for (j = 1; (j ^ m) != 0; j = 0) {
          return Boolean.valueOf(false);
        }
      }
      if ((localObject4 instanceof Double))
      {
        if (paramzzb.zzbRp == null)
        {
          zzMg().zzNV().zze("No number filter for double param. event, param", paramzzb1.name, localObject3);
          return null;
        }
        paramzzb = zza(((Double)localObject4).doubleValue(), paramzzb.zzbRp);
        if (paramzzb == null) {
          return null;
        }
        if (!paramzzb.booleanValue()) {}
        for (j = 1; (j ^ m) != 0; j = 0) {
          return Boolean.valueOf(false);
        }
      }
      if ((localObject4 instanceof String))
      {
        if (paramzzb.zzbRo != null) {
          paramzzb = zza((String)localObject4, paramzzb.zzbRo);
        }
        while (paramzzb == null)
        {
          return null;
          if (paramzzb.zzbRp != null)
          {
            if (zzauw.zzgi((String)localObject4))
            {
              paramzzb = zza((String)localObject4, paramzzb.zzbRp);
            }
            else
            {
              zzMg().zzNV().zze("Invalid param value for number filter. event, param", paramzzb1.name, localObject3);
              return null;
            }
          }
          else
          {
            zzMg().zzNV().zze("No filter for String param. event, param", paramzzb1.name, localObject3);
            return null;
          }
        }
        if (!paramzzb.booleanValue()) {}
        for (j = 1; (j ^ m) != 0; j = 0) {
          return Boolean.valueOf(false);
        }
      }
      if (localObject4 == null)
      {
        zzMg().zzNZ().zze("Missing param for filter. event, param", paramzzb1.name, localObject3);
        return Boolean.valueOf(false);
      }
      zzMg().zzNV().zze("Unknown param type. event, param", paramzzb1.name, localObject3);
      return null;
      i += 1;
    }
    return Boolean.valueOf(true);
  }
  
  private Boolean zza(zzaux.zze paramzze, zzauz.zzg paramzzg)
  {
    paramzze = paramzze.zzbRz;
    if (paramzze == null)
    {
      zzMg().zzNV().zzm("Missing property filter. property", paramzzg.name);
      return null;
    }
    boolean bool = Boolean.TRUE.equals(paramzze.zzbRq);
    if (paramzzg.zzbRV != null)
    {
      if (paramzze.zzbRp == null)
      {
        zzMg().zzNV().zzm("No number filter for long property. property", paramzzg.name);
        return null;
      }
      return zza(zza(paramzzg.zzbRV.longValue(), paramzze.zzbRp), bool);
    }
    if (paramzzg.zzbQZ != null)
    {
      if (paramzze.zzbRp == null)
      {
        zzMg().zzNV().zzm("No number filter for double property. property", paramzzg.name);
        return null;
      }
      return zza(zza(paramzzg.zzbQZ.doubleValue(), paramzze.zzbRp), bool);
    }
    if (paramzzg.stringValue != null)
    {
      if (paramzze.zzbRo == null)
      {
        if (paramzze.zzbRp == null)
        {
          zzMg().zzNV().zzm("No string or number filter defined. property", paramzzg.name);
          return null;
        }
        if (zzauw.zzgi(paramzzg.stringValue)) {
          return zza(zza(paramzzg.stringValue, paramzze.zzbRp), bool);
        }
        zzMg().zzNV().zze("Invalid user property value for Numeric number filter. property, value", paramzzg.name, paramzzg.stringValue);
        return null;
      }
      return zza(zza(paramzzg.stringValue, paramzze.zzbRo), bool);
    }
    zzMg().zzNV().zzm("User property has no value, property", paramzzg.name);
    return null;
  }
  
  static Boolean zza(Boolean paramBoolean, boolean paramBoolean1)
  {
    if (paramBoolean == null) {
      return null;
    }
    return Boolean.valueOf(paramBoolean.booleanValue() ^ paramBoolean1);
  }
  
  private Boolean zza(String paramString1, int paramInt, boolean paramBoolean, String paramString2, List<String> paramList, String paramString3)
  {
    if (paramString1 == null) {}
    do
    {
      return null;
      if (paramInt != 6) {
        break;
      }
    } while ((paramList == null) || (paramList.size() == 0));
    String str = paramString1;
    if (!paramBoolean)
    {
      if (paramInt != 1) {
        break label113;
      }
      str = paramString1;
    }
    switch (paramInt)
    {
    default: 
      return null;
    case 1: 
      if (paramBoolean) {}
      for (paramInt = 0;; paramInt = 66)
      {
        return Boolean.valueOf(Pattern.compile(paramString3, paramInt).matcher(str).matches());
        if (paramString2 != null) {
          break;
        }
        return null;
        str = paramString1.toUpperCase(Locale.ENGLISH);
        break label42;
      }
    case 2: 
      return Boolean.valueOf(str.startsWith(paramString2));
    case 3: 
      return Boolean.valueOf(str.endsWith(paramString2));
    case 4: 
      return Boolean.valueOf(str.contains(paramString2));
    case 5: 
      label42:
      label113:
      return Boolean.valueOf(str.equals(paramString2));
    }
    return Boolean.valueOf(paramList.contains(str));
  }
  
  private Boolean zza(BigDecimal paramBigDecimal1, int paramInt, BigDecimal paramBigDecimal2, BigDecimal paramBigDecimal3, BigDecimal paramBigDecimal4, double paramDouble)
  {
    boolean bool2 = true;
    boolean bool3 = true;
    boolean bool4 = true;
    boolean bool5 = true;
    boolean bool1 = true;
    if (paramBigDecimal1 == null) {
      return null;
    }
    if (paramInt == 4)
    {
      if ((paramBigDecimal3 == null) || (paramBigDecimal4 == null)) {
        return null;
      }
    }
    else if (paramBigDecimal2 == null) {
      return null;
    }
    switch (paramInt)
    {
    default: 
      return null;
    case 1: 
      if (paramBigDecimal1.compareTo(paramBigDecimal2) == -1) {}
      for (;;)
      {
        return Boolean.valueOf(bool1);
        bool1 = false;
      }
    case 2: 
      if (paramBigDecimal1.compareTo(paramBigDecimal2) == 1) {}
      for (bool1 = bool2;; bool1 = false) {
        return Boolean.valueOf(bool1);
      }
    case 3: 
      if (paramDouble != 0.0D)
      {
        if ((paramBigDecimal1.compareTo(paramBigDecimal2.subtract(new BigDecimal(paramDouble).multiply(new BigDecimal(2)))) == 1) && (paramBigDecimal1.compareTo(paramBigDecimal2.add(new BigDecimal(paramDouble).multiply(new BigDecimal(2)))) == -1)) {}
        for (bool1 = bool3;; bool1 = false) {
          return Boolean.valueOf(bool1);
        }
      }
      if (paramBigDecimal1.compareTo(paramBigDecimal2) == 0) {}
      for (bool1 = bool4;; bool1 = false) {
        return Boolean.valueOf(bool1);
      }
    }
    if ((paramBigDecimal1.compareTo(paramBigDecimal3) != -1) && (paramBigDecimal1.compareTo(paramBigDecimal4) != 1)) {}
    for (bool1 = bool5;; bool1 = false) {
      return Boolean.valueOf(bool1);
    }
  }
  
  private List<String> zza(String[] paramArrayOfString, boolean paramBoolean)
  {
    Object localObject;
    if (paramBoolean)
    {
      localObject = Arrays.asList(paramArrayOfString);
      return (List<String>)localObject;
    }
    ArrayList localArrayList = new ArrayList();
    int j = paramArrayOfString.length;
    int i = 0;
    for (;;)
    {
      localObject = localArrayList;
      if (i >= j) {
        break;
      }
      localArrayList.add(paramArrayOfString[i].toUpperCase(Locale.ENGLISH));
      i += 1;
    }
  }
  
  protected void onInitialize() {}
  
  public Boolean zza(double paramDouble, zzaux.zzd paramzzd)
  {
    try
    {
      paramzzd = zza(new BigDecimal(paramDouble), paramzzd, Math.ulp(paramDouble));
      return paramzzd;
    }
    catch (NumberFormatException paramzzd) {}
    return null;
  }
  
  public Boolean zza(long paramLong, zzaux.zzd paramzzd)
  {
    try
    {
      paramzzd = zza(new BigDecimal(paramLong), paramzzd, 0.0D);
      return paramzzd;
    }
    catch (NumberFormatException paramzzd) {}
    return null;
  }
  
  public Boolean zza(String paramString, zzaux.zzd paramzzd)
  {
    if (!zzauw.zzgi(paramString)) {
      return null;
    }
    try
    {
      paramString = zza(new BigDecimal(paramString), paramzzd, 0.0D);
      return paramString;
    }
    catch (NumberFormatException paramString) {}
    return null;
  }
  
  Boolean zza(String paramString, zzaux.zzf paramzzf)
  {
    Object localObject = null;
    zzac.zzC(paramzzf);
    if (paramString == null) {}
    do
    {
      do
      {
        return null;
      } while ((paramzzf.zzbRA == null) || (paramzzf.zzbRA.intValue() == 0));
      if (paramzzf.zzbRA.intValue() != 6) {
        break;
      }
    } while ((paramzzf.zzbRD == null) || (paramzzf.zzbRD.length == 0));
    int i = paramzzf.zzbRA.intValue();
    boolean bool;
    label86:
    String str;
    if ((paramzzf.zzbRC != null) && (paramzzf.zzbRC.booleanValue()))
    {
      bool = true;
      if ((!bool) && (i != 1) && (i != 6)) {
        break label155;
      }
      str = paramzzf.zzbRB;
      label108:
      if (paramzzf.zzbRD != null) {
        break label170;
      }
    }
    label155:
    label170:
    for (paramzzf = null;; paramzzf = zza(paramzzf.zzbRD, bool))
    {
      if (i == 1) {
        localObject = str;
      }
      return zza(paramString, i, bool, str, paramzzf, (String)localObject);
      if (paramzzf.zzbRB != null) {
        break;
      }
      return null;
      bool = false;
      break label86;
      str = paramzzf.zzbRB.toUpperCase(Locale.ENGLISH);
      break label108;
    }
  }
  
  Boolean zza(BigDecimal paramBigDecimal, zzaux.zzd paramzzd, double paramDouble)
  {
    zzac.zzC(paramzzd);
    if ((paramzzd.zzbRs == null) || (paramzzd.zzbRs.intValue() == 0)) {}
    label49:
    int i;
    do
    {
      do
      {
        return null;
        if (paramzzd.zzbRs.intValue() != 4) {
          break;
        }
      } while ((paramzzd.zzbRv == null) || (paramzzd.zzbRw == null));
      i = paramzzd.zzbRs.intValue();
    } while ((paramzzd.zzbRs.intValue() == 4) && ((!zzauw.zzgi(paramzzd.zzbRv)) || (!zzauw.zzgi(paramzzd.zzbRw))));
    for (;;)
    {
      BigDecimal localBigDecimal1;
      BigDecimal localBigDecimal2;
      try
      {
        localBigDecimal1 = new BigDecimal(paramzzd.zzbRv);
        localBigDecimal2 = new BigDecimal(paramzzd.zzbRw);
        paramzzd = null;
        return zza(paramBigDecimal, i, paramzzd, localBigDecimal1, localBigDecimal2, paramDouble);
      }
      catch (NumberFormatException paramBigDecimal) {}
      if (paramzzd.zzbRu != null) {
        break label49;
      }
      return null;
      if (!zzauw.zzgi(paramzzd.zzbRu)) {
        break;
      }
      try
      {
        paramzzd = new BigDecimal(paramzzd.zzbRu);
        localBigDecimal2 = null;
        localBigDecimal1 = null;
      }
      catch (NumberFormatException paramBigDecimal)
      {
        return null;
      }
    }
    return null;
  }
  
  @WorkerThread
  void zza(String paramString, zzaux.zza[] paramArrayOfzza)
  {
    zzac.zzC(paramArrayOfzza);
    int m = paramArrayOfzza.length;
    int i = 0;
    while (i < m)
    {
      Object localObject1 = paramArrayOfzza[i];
      zzaux.zzb[] arrayOfzzb = ((zzaux.zza)localObject1).zzbRg;
      int n = arrayOfzzb.length;
      int j = 0;
      Object localObject2;
      while (j < n)
      {
        localObject2 = arrayOfzzb[j];
        String str1 = (String)AppMeasurement.Event.zzbLb.get(((zzaux.zzb)localObject2).zzbRj);
        if (str1 != null) {
          ((zzaux.zzb)localObject2).zzbRj = str1;
        }
        localObject2 = ((zzaux.zzb)localObject2).zzbRk;
        int i1 = localObject2.length;
        k = 0;
        while (k < i1)
        {
          str1 = localObject2[k];
          String str2 = (String)AppMeasurement.Param.MAPPED_PARAM_NAMES.get(str1.zzbRr);
          if (str2 != null) {
            str1.zzbRr = str2;
          }
          k += 1;
        }
        j += 1;
      }
      localObject1 = ((zzaux.zza)localObject1).zzbRf;
      int k = localObject1.length;
      j = 0;
      while (j < k)
      {
        arrayOfzzb = localObject1[j];
        localObject2 = (String)AppMeasurement.zze.zzbLf.get(arrayOfzzb.zzbRy);
        if (localObject2 != null) {
          arrayOfzzb.zzbRy = ((String)localObject2);
        }
        j += 1;
      }
      i += 1;
    }
    zzMb().zzb(paramString, paramArrayOfzza);
  }
  
  @WorkerThread
  zzauz.zza[] zza(String paramString, zzauz.zzb[] paramArrayOfzzb, zzauz.zzg[] paramArrayOfzzg)
  {
    zzac.zzdc(paramString);
    HashSet localHashSet = new HashSet();
    ArrayMap localArrayMap1 = new ArrayMap();
    ArrayMap localArrayMap2 = new ArrayMap();
    ArrayMap localArrayMap3 = new ArrayMap();
    Object localObject4 = zzMb().zzfB(paramString);
    Object localObject5;
    int j;
    Object localObject6;
    Object localObject3;
    Object localObject2;
    Object localObject1;
    int i;
    if (localObject4 != null)
    {
      localObject5 = ((Map)localObject4).keySet().iterator();
      while (((Iterator)localObject5).hasNext())
      {
        j = ((Integer)((Iterator)localObject5).next()).intValue();
        localObject6 = (zzauz.zzf)((Map)localObject4).get(Integer.valueOf(j));
        localObject3 = (BitSet)localArrayMap2.get(Integer.valueOf(j));
        localObject2 = (BitSet)localArrayMap3.get(Integer.valueOf(j));
        localObject1 = localObject3;
        if (localObject3 == null)
        {
          localObject1 = new BitSet();
          localArrayMap2.put(Integer.valueOf(j), localObject1);
          localObject2 = new BitSet();
          localArrayMap3.put(Integer.valueOf(j), localObject2);
        }
        i = 0;
        while (i < ((zzauz.zzf)localObject6).zzbSz.length * 64)
        {
          if (zzauw.zza(((zzauz.zzf)localObject6).zzbSz, i))
          {
            zzMg().zzNZ().zze("Filter already evaluated. audience ID, filter ID", Integer.valueOf(j), Integer.valueOf(i));
            ((BitSet)localObject2).set(i);
            if (zzauw.zza(((zzauz.zzf)localObject6).zzbSA, i)) {
              ((BitSet)localObject1).set(i);
            }
          }
          i += 1;
        }
        localObject3 = new zzauz.zza();
        localArrayMap1.put(Integer.valueOf(j), localObject3);
        ((zzauz.zza)localObject3).zzbRP = Boolean.valueOf(false);
        ((zzauz.zza)localObject3).zzbRO = ((zzauz.zzf)localObject6);
        ((zzauz.zza)localObject3).zzbRN = new zzauz.zzf();
        ((zzauz.zza)localObject3).zzbRN.zzbSA = zzauw.zza((BitSet)localObject1);
        ((zzauz.zza)localObject3).zzbRN.zzbSz = zzauw.zza((BitSet)localObject2);
      }
    }
    Object localObject7;
    long l;
    if (paramArrayOfzzb != null)
    {
      localObject6 = new ArrayMap();
      j = paramArrayOfzzb.length;
      i = 0;
      if (i < j)
      {
        localObject7 = paramArrayOfzzb[i];
        localObject1 = zzMb().zzaa(paramString, ((zzauz.zzb)localObject7).name);
        if (localObject1 == null)
        {
          zzMg().zzNV().zze("Event aggregate wasn't created during raw event logging. appId, event", zzaua.zzfH(paramString), ((zzauz.zzb)localObject7).name);
          localObject1 = new zzatq(paramString, ((zzauz.zzb)localObject7).name, 1L, 1L, ((zzauz.zzb)localObject7).zzbRS.longValue());
          zzMb().zza((zzatq)localObject1);
          l = ((zzatq)localObject1).zzbMw;
          localObject1 = (Map)((Map)localObject6).get(((zzauz.zzb)localObject7).name);
          if (localObject1 != null) {
            break label1926;
          }
          localObject2 = zzMb().zzaf(paramString, ((zzauz.zzb)localObject7).name);
          localObject1 = localObject2;
          if (localObject2 == null) {
            localObject1 = new ArrayMap();
          }
          ((Map)localObject6).put(((zzauz.zzb)localObject7).name, localObject1);
        }
      }
    }
    label1043:
    label1080:
    label1670:
    label1705:
    label1920:
    label1923:
    label1926:
    for (;;)
    {
      Iterator localIterator = ((Map)localObject1).keySet().iterator();
      int k;
      Object localObject8;
      Object localObject9;
      for (;;)
      {
        if (!localIterator.hasNext()) {
          break label1080;
        }
        k = ((Integer)localIterator.next()).intValue();
        if (localHashSet.contains(Integer.valueOf(k)))
        {
          zzMg().zzNZ().zzm("Skipping failed audience ID", Integer.valueOf(k));
          continue;
          localObject1 = ((zzatq)localObject1).zzNQ();
          break;
        }
        localObject4 = (zzauz.zza)localArrayMap1.get(Integer.valueOf(k));
        localObject2 = (BitSet)localArrayMap2.get(Integer.valueOf(k));
        localObject3 = (BitSet)localArrayMap3.get(Integer.valueOf(k));
        if (localObject4 == null)
        {
          localObject2 = new zzauz.zza();
          localArrayMap1.put(Integer.valueOf(k), localObject2);
          ((zzauz.zza)localObject2).zzbRP = Boolean.valueOf(true);
          localObject2 = new BitSet();
          localArrayMap2.put(Integer.valueOf(k), localObject2);
          localObject3 = new BitSet();
          localArrayMap3.put(Integer.valueOf(k), localObject3);
        }
        localObject8 = ((List)((Map)localObject1).get(Integer.valueOf(k))).iterator();
        while (((Iterator)localObject8).hasNext())
        {
          localObject9 = (zzaux.zzb)((Iterator)localObject8).next();
          if (zzMg().zzao(2))
          {
            zzMg().zzNZ().zzd("Evaluating filter. audience, filter, event", Integer.valueOf(k), ((zzaux.zzb)localObject9).zzbRi, ((zzaux.zzb)localObject9).zzbRj);
            zzMg().zzNZ().zzm("Filter definition", zzauw.zza((zzaux.zzb)localObject9));
          }
          if ((((zzaux.zzb)localObject9).zzbRi == null) || (((zzaux.zzb)localObject9).zzbRi.intValue() > 256))
          {
            zzMg().zzNV().zze("Invalid event filter ID. appId, id", zzaua.zzfH(paramString), String.valueOf(((zzaux.zzb)localObject9).zzbRi));
          }
          else if (((BitSet)localObject2).get(((zzaux.zzb)localObject9).zzbRi.intValue()))
          {
            zzMg().zzNZ().zze("Event filter already evaluated true. audience ID, filter ID", Integer.valueOf(k), ((zzaux.zzb)localObject9).zzbRi);
          }
          else
          {
            localObject5 = zza((zzaux.zzb)localObject9, (zzauz.zzb)localObject7, l);
            zzaua.zza localzza = zzMg().zzNZ();
            if (localObject5 == null) {}
            for (localObject4 = "null";; localObject4 = localObject5)
            {
              localzza.zzm("Event filter result", localObject4);
              if (localObject5 != null) {
                break label1043;
              }
              localHashSet.add(Integer.valueOf(k));
              break;
            }
            ((BitSet)localObject3).set(((zzaux.zzb)localObject9).zzbRi.intValue());
            if (((Boolean)localObject5).booleanValue()) {
              ((BitSet)localObject2).set(((zzaux.zzb)localObject9).zzbRi.intValue());
            }
          }
        }
      }
      i += 1;
      break;
      if (paramArrayOfzzg != null)
      {
        localObject5 = new ArrayMap();
        j = paramArrayOfzzg.length;
        i = 0;
        if (i < j)
        {
          localObject6 = paramArrayOfzzg[i];
          paramArrayOfzzb = (Map)((Map)localObject5).get(((zzauz.zzg)localObject6).name);
          if (paramArrayOfzzb != null) {
            break label1923;
          }
          localObject1 = zzMb().zzag(paramString, ((zzauz.zzg)localObject6).name);
          paramArrayOfzzb = (zzauz.zzb[])localObject1;
          if (localObject1 == null) {
            paramArrayOfzzb = new ArrayMap();
          }
          ((Map)localObject5).put(((zzauz.zzg)localObject6).name, paramArrayOfzzb);
        }
      }
      for (;;)
      {
        localObject7 = paramArrayOfzzb.keySet().iterator();
        while (((Iterator)localObject7).hasNext())
        {
          k = ((Integer)((Iterator)localObject7).next()).intValue();
          if (localHashSet.contains(Integer.valueOf(k)))
          {
            zzMg().zzNZ().zzm("Skipping failed audience ID", Integer.valueOf(k));
          }
          else
          {
            localObject3 = (zzauz.zza)localArrayMap1.get(Integer.valueOf(k));
            localObject1 = (BitSet)localArrayMap2.get(Integer.valueOf(k));
            localObject2 = (BitSet)localArrayMap3.get(Integer.valueOf(k));
            if (localObject3 == null)
            {
              localObject1 = new zzauz.zza();
              localArrayMap1.put(Integer.valueOf(k), localObject1);
              ((zzauz.zza)localObject1).zzbRP = Boolean.valueOf(true);
              localObject1 = new BitSet();
              localArrayMap2.put(Integer.valueOf(k), localObject1);
              localObject2 = new BitSet();
              localArrayMap3.put(Integer.valueOf(k), localObject2);
            }
            localIterator = ((List)paramArrayOfzzb.get(Integer.valueOf(k))).iterator();
            for (;;)
            {
              if (!localIterator.hasNext()) {
                break label1705;
              }
              localObject8 = (zzaux.zze)localIterator.next();
              if (zzMg().zzao(2))
              {
                zzMg().zzNZ().zzd("Evaluating filter. audience, filter, property", Integer.valueOf(k), ((zzaux.zze)localObject8).zzbRi, ((zzaux.zze)localObject8).zzbRy);
                zzMg().zzNZ().zzm("Filter definition", zzauw.zza((zzaux.zze)localObject8));
              }
              if ((((zzaux.zze)localObject8).zzbRi == null) || (((zzaux.zze)localObject8).zzbRi.intValue() > 256))
              {
                zzMg().zzNV().zze("Invalid property filter ID. appId, id", zzaua.zzfH(paramString), String.valueOf(((zzaux.zze)localObject8).zzbRi));
                localHashSet.add(Integer.valueOf(k));
                break;
              }
              if (((BitSet)localObject1).get(((zzaux.zze)localObject8).zzbRi.intValue()))
              {
                zzMg().zzNZ().zze("Property filter already evaluated true. audience ID, filter ID", Integer.valueOf(k), ((zzaux.zze)localObject8).zzbRi);
              }
              else
              {
                localObject4 = zza((zzaux.zze)localObject8, (zzauz.zzg)localObject6);
                localObject9 = zzMg().zzNZ();
                if (localObject4 == null) {}
                for (localObject3 = "null";; localObject3 = localObject4)
                {
                  ((zzaua.zza)localObject9).zzm("Property filter result", localObject3);
                  if (localObject4 != null) {
                    break label1670;
                  }
                  localHashSet.add(Integer.valueOf(k));
                  break;
                }
                ((BitSet)localObject2).set(((zzaux.zze)localObject8).zzbRi.intValue());
                if (((Boolean)localObject4).booleanValue()) {
                  ((BitSet)localObject1).set(((zzaux.zze)localObject8).zzbRi.intValue());
                }
              }
            }
          }
        }
        i += 1;
        break;
        paramArrayOfzzg = new zzauz.zza[localArrayMap2.size()];
        localObject1 = localArrayMap2.keySet().iterator();
        i = 0;
        while (((Iterator)localObject1).hasNext())
        {
          j = ((Integer)((Iterator)localObject1).next()).intValue();
          if (!localHashSet.contains(Integer.valueOf(j)))
          {
            paramArrayOfzzb = (zzauz.zza)localArrayMap1.get(Integer.valueOf(j));
            if (paramArrayOfzzb != null) {
              break label1920;
            }
            paramArrayOfzzb = new zzauz.zza();
          }
        }
        for (;;)
        {
          paramArrayOfzzg[i] = paramArrayOfzzb;
          paramArrayOfzzb.zzbRe = Integer.valueOf(j);
          paramArrayOfzzb.zzbRN = new zzauz.zzf();
          paramArrayOfzzb.zzbRN.zzbSA = zzauw.zza((BitSet)localArrayMap2.get(Integer.valueOf(j)));
          paramArrayOfzzb.zzbRN.zzbSz = zzauw.zza((BitSet)localArrayMap3.get(Integer.valueOf(j)));
          zzMb().zza(paramString, j, paramArrayOfzzb.zzbRN);
          i += 1;
          break;
          return (zzauz.zza[])Arrays.copyOf(paramArrayOfzzg, i);
        }
      }
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/internal/zzati.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */