package com.bumptech.glide.signature;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import com.bumptech.glide.load.Key;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class ApplicationVersionSignature
{
  private static final ConcurrentHashMap<String, Key> PACKAGE_NAME_TO_KEY = new ConcurrentHashMap();
  
  public static Key obtain(Context paramContext)
  {
    String str = paramContext.getPackageName();
    Key localKey = (Key)PACKAGE_NAME_TO_KEY.get(str);
    Object localObject = localKey;
    if (localKey == null)
    {
      paramContext = obtainVersionSignature(paramContext);
      localKey = (Key)PACKAGE_NAME_TO_KEY.putIfAbsent(str, paramContext);
      localObject = localKey;
      if (localKey == null) {
        localObject = paramContext;
      }
    }
    return (Key)localObject;
  }
  
  private static Key obtainVersionSignature(Context paramContext)
  {
    Object localObject = null;
    try
    {
      paramContext = paramContext.getPackageManager().getPackageInfo(paramContext.getPackageName(), 0);
      if (paramContext != null)
      {
        paramContext = String.valueOf(paramContext.versionCode);
        return new ObjectKey(paramContext);
      }
    }
    catch (PackageManager.NameNotFoundException paramContext)
    {
      for (;;)
      {
        paramContext.printStackTrace();
        paramContext = (Context)localObject;
        continue;
        paramContext = UUID.randomUUID().toString();
      }
    }
  }
  
  static void reset()
  {
    PACKAGE_NAME_TO_KEY.clear();
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/signature/ApplicationVersionSignature.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */