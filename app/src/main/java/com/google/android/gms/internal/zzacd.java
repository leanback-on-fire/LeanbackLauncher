package com.google.android.gms.internal;

import android.content.Context;
import android.util.Log;
import com.google.android.gms.measurement.AppMeasurement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class zzacd
{
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
  
  public static List<zzacb> zzbr(Context paramContext)
  {
    paramContext = zzbq(paramContext);
    if (paramContext == null) {
      if (Log.isLoggable("FRCAnalytics", 3)) {
        Log.d("FRCAnalytics", "Unable to get user properties: analytics library is missing.");
      }
    }
    ArrayList localArrayList;
    for (;;)
    {
      return null;
      try
      {
        paramContext = paramContext.getUserProperties(false);
        if (paramContext != null)
        {
          localArrayList = new ArrayList();
          paramContext = paramContext.entrySet().iterator();
          while (paramContext.hasNext())
          {
            Map.Entry localEntry = (Map.Entry)paramContext.next();
            if (localEntry.getValue() != null) {
              localArrayList.add(new zzacb((String)localEntry.getKey(), localEntry.getValue().toString()));
            }
          }
        }
      }
      catch (NullPointerException paramContext)
      {
        for (;;)
        {
          if (Log.isLoggable("FRCAnalytics", 3)) {
            Log.d("FRCAnalytics", "Unable to get user properties.", paramContext);
          }
          paramContext = null;
        }
      }
    }
    return localArrayList;
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/internal/zzacd.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */