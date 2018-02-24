package com.google.firebase.iid;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.support.v4.util.ArrayMap;
import android.util.Base64;
import android.util.Log;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import java.io.IOException;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Map;

public class FirebaseInstanceId
{
  private static zze wc;
  private static Map<String, FirebaseInstanceId> zzbAC = new ArrayMap();
  private final FirebaseApp wd;
  private final zzd we;
  private final String wf;
  
  private FirebaseInstanceId(FirebaseApp paramFirebaseApp, zzd paramzzd)
  {
    this.wd = paramFirebaseApp;
    this.we = paramzzd;
    this.wf = zzakf();
    if (this.wf == null) {
      throw new IllegalStateException("IID failing to initialize, FirebaseApp is missing project ID");
    }
    FirebaseInstanceIdService.zza(this.wd.getApplicationContext(), this);
  }
  
  public static FirebaseInstanceId getInstance()
  {
    return getInstance(FirebaseApp.getInstance());
  }
  
  @Keep
  public static FirebaseInstanceId getInstance(@NonNull FirebaseApp paramFirebaseApp)
  {
    try
    {
      FirebaseInstanceId localFirebaseInstanceId = (FirebaseInstanceId)zzbAC.get(paramFirebaseApp.getOptions().getApplicationId());
      Object localObject = localFirebaseInstanceId;
      if (localFirebaseInstanceId == null)
      {
        localObject = zzd.zza(paramFirebaseApp.getApplicationContext(), null);
        if (wc == null) {
          wc = new zze(((zzd)localObject).zzakj());
        }
        localObject = new FirebaseInstanceId(paramFirebaseApp, (zzd)localObject);
        zzbAC.put(paramFirebaseApp.getOptions().getApplicationId(), localObject);
      }
      return (FirebaseInstanceId)localObject;
    }
    finally {}
  }
  
  static String zzB(byte[] paramArrayOfByte)
  {
    return Base64.encodeToString(paramArrayOfByte, 11);
  }
  
  static int zzY(Context paramContext, String paramString)
  {
    try
    {
      int i = paramContext.getPackageManager().getPackageInfo(paramString, 0).versionCode;
      return i;
    }
    catch (PackageManager.NameNotFoundException paramContext)
    {
      paramContext = String.valueOf(paramContext);
      Log.w("FirebaseInstanceId", String.valueOf(paramContext).length() + 23 + "Failed to find package " + paramContext);
    }
    return 0;
  }
  
  static String zza(KeyPair paramKeyPair)
  {
    paramKeyPair = paramKeyPair.getPublic().getEncoded();
    try
    {
      paramKeyPair = MessageDigest.getInstance("SHA1").digest(paramKeyPair);
      paramKeyPair[0] = ((byte)((paramKeyPair[0] & 0xF) + 112 & 0xFF));
      paramKeyPair = Base64.encodeToString(paramKeyPair, 0, 8, 11);
      return paramKeyPair;
    }
    catch (NoSuchAlgorithmException paramKeyPair)
    {
      Log.w("FirebaseInstanceId", "Unexpected error, device missing required alghorithms");
    }
    return null;
  }
  
  static void zza(Context paramContext, zzg paramzzg)
  {
    paramzzg.zzJU();
    paramzzg = new Intent();
    paramzzg.putExtra("CMD", "RST");
    zzf.zzakn().zzh(paramContext, paramzzg);
  }
  
  static String zzbE(Context paramContext)
  {
    try
    {
      paramContext = paramContext.getPackageManager().getPackageInfo(paramContext.getPackageName(), 0).versionName;
      return paramContext;
    }
    catch (PackageManager.NameNotFoundException paramContext)
    {
      paramContext = String.valueOf(paramContext);
      Log.w("FirebaseInstanceId", String.valueOf(paramContext).length() + 38 + "Never happens: can't find own package " + paramContext);
    }
    return null;
  }
  
  static void zzbF(Context paramContext)
  {
    Intent localIntent = new Intent();
    localIntent.putExtra("CMD", "SYNC");
    zzf.zzakn().zzh(paramContext, localIntent);
  }
  
  static String zzcA(Context paramContext)
  {
    return getInstance().wd.getOptions().getApplicationId();
  }
  
  static int zzcB(Context paramContext)
  {
    return zzY(paramContext, paramContext.getPackageName());
  }
  
  public void deleteInstanceId()
    throws IOException
  {
    this.we.deleteToken("*", "*", null);
    this.we.zzJP();
  }
  
  @WorkerThread
  public void deleteToken(String paramString1, String paramString2)
    throws IOException
  {
    this.we.deleteToken(paramString1, paramString2, null);
  }
  
  public long getCreationTime()
  {
    return this.we.getCreationTime();
  }
  
  public String getId()
  {
    return zza(this.we.zzJO());
  }
  
  @Nullable
  public String getToken()
  {
    zzg.zza localzza = zzakg();
    if ((localzza == null) || (localzza.zzmu(zzd.zzbAI))) {
      FirebaseInstanceIdService.zzcC(this.wd.getApplicationContext());
    }
    if (localzza != null) {
      return localzza.zzanu;
    }
    return null;
  }
  
  @WorkerThread
  public String getToken(String paramString1, String paramString2)
    throws IOException
  {
    return this.we.getToken(paramString1, paramString2, null);
  }
  
  String zzakf()
  {
    Object localObject = this.wd.getOptions().getGcmSenderId();
    if (localObject != null) {}
    String str;
    do
    {
      do
      {
        return (String)localObject;
        str = this.wd.getOptions().getApplicationId();
        localObject = str;
      } while (!str.startsWith("1:"));
      localObject = str.split(":");
      if (localObject.length < 2) {
        return null;
      }
      str = localObject[1];
      localObject = str;
    } while (!str.isEmpty());
    return null;
  }
  
  @Nullable
  zzg.zza zzakg()
  {
    return this.we.zzakj().zzs("", this.wf, "*");
  }
  
  String zzakh()
    throws IOException
  {
    return getToken(this.wf, "*");
  }
  
  zze zzaki()
  {
    return wc;
  }
  
  public String zzb(String paramString1, String paramString2, Bundle paramBundle)
    throws IOException
  {
    return this.we.zzb(paramString1, paramString2, paramBundle);
  }
  
  public void zzmm(String paramString)
  {
    wc.zzmm(paramString);
    FirebaseInstanceIdService.zzcC(this.wd.getApplicationContext());
  }
  
  void zzmn(String paramString)
    throws IOException
  {
    Object localObject = zzakg();
    if ((localObject == null) || (((zzg.zza)localObject).zzmu(zzd.zzbAI))) {
      throw new IOException("token not available");
    }
    Bundle localBundle = new Bundle();
    String str1 = String.valueOf("/topics/");
    String str2 = String.valueOf(paramString);
    if (str2.length() != 0)
    {
      str1 = str1.concat(str2);
      localBundle.putString("gcm.topic", str1);
      str1 = ((zzg.zza)localObject).zzanu;
      localObject = String.valueOf("/topics/");
      paramString = String.valueOf(paramString);
      if (paramString.length() == 0) {
        break label131;
      }
    }
    label131:
    for (paramString = ((String)localObject).concat(paramString);; paramString = new String((String)localObject))
    {
      zzb(str1, paramString, localBundle);
      return;
      str1 = new String(str1);
      break;
    }
  }
  
  void zzmo(String paramString)
    throws IOException
  {
    Object localObject2 = zzakg();
    if ((localObject2 == null) || (((zzg.zza)localObject2).zzmu(zzd.zzbAI))) {
      throw new IOException("token not available");
    }
    Bundle localBundle = new Bundle();
    Object localObject1 = String.valueOf("/topics/");
    String str = String.valueOf(paramString);
    if (str.length() != 0)
    {
      localObject1 = ((String)localObject1).concat(str);
      localBundle.putString("gcm.topic", (String)localObject1);
      localObject1 = this.we;
      localObject2 = ((zzg.zza)localObject2).zzanu;
      str = String.valueOf("/topics/");
      paramString = String.valueOf(paramString);
      if (paramString.length() == 0) {
        break label137;
      }
    }
    label137:
    for (paramString = str.concat(paramString);; paramString = new String(str))
    {
      ((zzd)localObject1).deleteToken((String)localObject2, paramString, localBundle);
      return;
      localObject1 = new String((String)localObject1);
      break;
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/firebase/iid/FirebaseInstanceId.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */