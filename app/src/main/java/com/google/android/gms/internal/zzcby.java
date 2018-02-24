package com.google.android.gms.internal;

import java.util.HashMap;
import java.util.Map;

public class zzcby
{
  private boolean wV;
  private long wY;
  private int xe;
  private long xg;
  private Map<String, zzcbs> xh;
  
  public zzcby()
  {
    this(-1L);
  }
  
  public zzcby(int paramInt, long paramLong, Map<String, zzcbs> paramMap, boolean paramBoolean)
  {
    this(paramInt, paramLong, paramMap, paramBoolean, -1L);
  }
  
  public zzcby(int paramInt, long paramLong1, Map<String, zzcbs> paramMap, boolean paramBoolean, long paramLong2)
  {
    this.xe = paramInt;
    this.xg = paramLong1;
    if (paramMap != null) {}
    for (;;)
    {
      this.xh = paramMap;
      this.wV = paramBoolean;
      this.wY = paramLong2;
      return;
      paramMap = new HashMap();
    }
  }
  
  public zzcby(long paramLong)
  {
    this(0, paramLong, null, false);
  }
  
  public int getLastFetchStatus()
  {
    return this.xe;
  }
  
  public boolean isDeveloperModeEnabled()
  {
    return this.wV;
  }
  
  public void zzBf(int paramInt)
  {
    this.xe = paramInt;
  }
  
  public void zza(String paramString, zzcbs paramzzcbs)
  {
    this.xh.put(paramString, paramzzcbs);
  }
  
  public void zzaM(Map<String, zzcbs> paramMap)
  {
    if (paramMap != null) {}
    for (;;)
    {
      this.xh = paramMap;
      return;
      paramMap = new HashMap();
    }
  }
  
  public Map<String, zzcbs> zzakB()
  {
    return this.xh;
  }
  
  public long zzakC()
  {
    return this.xg;
  }
  
  public long zzakD()
  {
    return this.wY;
  }
  
  public void zzbS(boolean paramBoolean)
  {
    this.wV = paramBoolean;
  }
  
  public void zzbm(long paramLong)
  {
    this.xg = paramLong;
  }
  
  public void zzbn(long paramLong)
  {
    this.wY = paramLong;
  }
  
  public void zzmz(String paramString)
  {
    if (this.xh.get(paramString) == null) {
      return;
    }
    this.xh.remove(paramString);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/internal/zzcby.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */