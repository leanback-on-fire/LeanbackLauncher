package com.google.firebase.iid;

import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import java.io.IOException;
import java.security.KeyPair;
import java.util.HashMap;
import java.util.Map;

public class zzd
{
  private static zzg wm;
  private static Rpc wn;
  static Map<String, zzd> zzbAC = new HashMap();
  static String zzbAI;
  Context mContext;
  KeyPair zzbAF;
  String zzbAG = "";
  
  protected zzd(Context paramContext, String paramString, Bundle paramBundle)
  {
    this.mContext = paramContext.getApplicationContext();
    this.zzbAG = paramString;
  }
  
  public static zzd zza(Context paramContext, Bundle paramBundle)
  {
    String str;
    if (paramBundle == null) {
      str = "";
    }
    for (;;)
    {
      try
      {
        Context localContext = paramContext.getApplicationContext();
        if (wm == null)
        {
          wm = new zzg(localContext);
          wn = new Rpc(localContext);
        }
        zzbAI = Integer.toString(FirebaseInstanceId.zzcB(localContext));
        zzd localzzd = (zzd)zzbAC.get(str);
        paramContext = localzzd;
        if (localzzd == null)
        {
          paramContext = new zzd(localContext, str, paramBundle);
          zzbAC.put(str, paramContext);
        }
        return paramContext;
      }
      finally {}
      str = paramBundle.getString("subtype");
      while (str != null) {
        break;
      }
      str = "";
    }
  }
  
  public void deleteToken(String paramString1, String paramString2, Bundle paramBundle)
    throws IOException
  {
    if (Looper.getMainLooper() == Looper.myLooper()) {
      throw new IOException("MAIN_THREAD");
    }
    wm.zzh(this.zzbAG, paramString1, paramString2);
    Bundle localBundle = paramBundle;
    if (paramBundle == null) {
      localBundle = new Bundle();
    }
    localBundle.putString("sender", paramString1);
    if (paramString2 != null) {
      localBundle.putString("scope", paramString2);
    }
    localBundle.putString("subscription", paramString1);
    localBundle.putString("delete", "1");
    localBundle.putString("X-delete", "1");
    if ("".equals(this.zzbAG))
    {
      paramString2 = paramString1;
      localBundle.putString("subtype", paramString2);
      if (!"".equals(this.zzbAG)) {
        break label165;
      }
    }
    for (;;)
    {
      localBundle.putString("X-subtype", paramString1);
      paramString1 = wn.zza(localBundle, zzJO());
      wn.zzC(paramString1);
      return;
      paramString2 = this.zzbAG;
      break;
      label165:
      paramString1 = this.zzbAG;
    }
  }
  
  public long getCreationTime()
  {
    return wm.zzmr(this.zzbAG);
  }
  
  public String getToken(String paramString1, String paramString2, Bundle paramBundle)
    throws IOException
  {
    if (Looper.getMainLooper() == Looper.myLooper()) {
      throw new IOException("MAIN_THREAD");
    }
    Bundle localBundle = paramBundle;
    if (paramBundle == null) {
      localBundle = new Bundle();
    }
    int j = 1;
    int i;
    if ((localBundle.getString("ttl") != null) || ("jwt".equals(localBundle.getString("type")))) {
      i = 0;
    }
    do
    {
      do
      {
        paramBundle = zzb(paramString1, paramString2, localBundle);
        if ((paramBundle != null) && (i != 0)) {
          wm.zza(this.zzbAG, paramString1, paramString2, paramBundle, zzbAI);
        }
        return paramBundle;
        paramBundle = wm.zzs(this.zzbAG, paramString1, paramString2);
        i = j;
      } while (paramBundle == null);
      i = j;
    } while (paramBundle.zzmu(zzbAI));
    return paramBundle.zzanu;
  }
  
  KeyPair zzJO()
  {
    if (this.zzbAF == null) {
      this.zzbAF = wm.zzeI(this.zzbAG);
    }
    if (this.zzbAF == null) {
      this.zzbAF = wm.zzms(this.zzbAG);
    }
    return this.zzbAF;
  }
  
  public void zzJP()
  {
    wm.zzeJ(this.zzbAG);
    this.zzbAF = null;
  }
  
  public zzg zzakj()
  {
    return wm;
  }
  
  public Rpc zzakk()
  {
    return wn;
  }
  
  public String zzb(String paramString1, String paramString2, Bundle paramBundle)
    throws IOException
  {
    if (paramString2 != null) {
      paramBundle.putString("scope", paramString2);
    }
    paramBundle.putString("sender", paramString1);
    if ("".equals(this.zzbAG)) {}
    for (paramString2 = paramString1;; paramString2 = this.zzbAG)
    {
      if (!paramBundle.containsKey("legacy.register"))
      {
        paramBundle.putString("subscription", paramString1);
        paramBundle.putString("subtype", paramString2);
        paramBundle.putString("X-subscription", paramString1);
        paramBundle.putString("X-subtype", paramString2);
      }
      paramString1 = wn.zza(paramBundle, zzJO());
      return wn.zzC(paramString1);
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/firebase/iid/zzd.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */