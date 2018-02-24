package com.google.android.gms.internal;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class zzcbv
{
  private Map<String, Map<String, byte[]>> xc;
  private long zzaHE;
  private List<byte[]> zzaVI;
  
  public zzcbv(Map<String, Map<String, byte[]>> paramMap, long paramLong, List<byte[]> paramList)
  {
    this.xc = paramMap;
    this.zzaHE = paramLong;
    this.zzaVI = paramList;
  }
  
  public long getTimestamp()
  {
    return this.zzaHE;
  }
  
  public void setTimestamp(long paramLong)
  {
    this.zzaHE = paramLong;
  }
  
  public List<byte[]> zzBm()
  {
    return this.zzaVI;
  }
  
  public boolean zzaP(String paramString1, String paramString2)
  {
    return (zzakz()) && (zzmy(paramString2)) && (zzaQ(paramString1, paramString2) != null);
  }
  
  public byte[] zzaQ(String paramString1, String paramString2)
  {
    if ((paramString1 == null) || (!zzmy(paramString2))) {
      return null;
    }
    return (byte[])((Map)this.xc.get(paramString2)).get(paramString1);
  }
  
  public Set<String> zzaR(String paramString1, String paramString2)
  {
    TreeSet localTreeSet = new TreeSet();
    if (!zzmy(paramString2)) {
      return localTreeSet;
    }
    if ((paramString1 == null) || (paramString1.isEmpty())) {
      return ((Map)this.xc.get(paramString2)).keySet();
    }
    paramString2 = ((Map)this.xc.get(paramString2)).keySet().iterator();
    while (paramString2.hasNext())
    {
      String str = (String)paramString2.next();
      if (str.startsWith(paramString1)) {
        localTreeSet.add(str);
      }
    }
    return localTreeSet;
  }
  
  public Map<String, Map<String, byte[]>> zzaky()
  {
    return this.xc;
  }
  
  public boolean zzakz()
  {
    return (this.xc != null) && (!this.xc.isEmpty());
  }
  
  public void zzj(Map<String, byte[]> paramMap, String paramString)
  {
    if (this.xc == null) {
      this.xc = new HashMap();
    }
    this.xc.put(paramString, paramMap);
  }
  
  public boolean zzmy(String paramString)
  {
    if (paramString == null) {
      return false;
    }
    if ((zzakz()) && (this.xc.get(paramString) != null) && (!((Map)this.xc.get(paramString)).isEmpty())) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/internal/zzcbv.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */