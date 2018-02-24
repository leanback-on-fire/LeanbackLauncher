package com.google.android.gms.config;

import com.google.android.gms.common.api.CommonStatusCodes;

public final class ConfigStatusCodes
  extends CommonStatusCodes
{
  public static final int ANOTHER_FETCH_INFLIGHT = 6501;
  public static final int FAILURE_CACHE = 6504;
  public static final int FETCH_THROTTLED = 6502;
  public static final int FETCH_THROTTLED_STALE = 6507;
  public static final int NOT_AUTHORIZED_TO_FETCH = 6500;
  public static final int NOT_AVAILABLE = 6503;
  public static final int SUCCESS_CACHE = -6506;
  public static final int SUCCESS_CACHE_STALE = -6508;
  public static final int SUCCESS_FRESH = -6505;
  
  public static String getStatusCodeString(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return CommonStatusCodes.getStatusCodeString(paramInt);
    case 6500: 
      return "NOT_AUTHORIZED_TO_FETCH";
    case 6501: 
      return "ANOTHER_FETCH_INFLIGHT";
    case 6502: 
      return "FETCH_THROTTLED";
    case 6503: 
      return "NOT_AVAILABLE";
    case 6504: 
      return "FAILURE_CACHE";
    case -6505: 
      return "SUCCESS_FRESH";
    case -6506: 
      return "SUCCESS_CACHE";
    case 6507: 
      return "FETCH_THROTTLED_STALE";
    }
    return "SUCCESS_CACHE_STALE";
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/config/ConfigStatusCodes.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */