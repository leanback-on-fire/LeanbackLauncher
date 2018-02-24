package com.google.firebase.iid;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.VisibleForTesting;
import android.support.annotation.WorkerThread;
import android.util.Log;
import java.io.IOException;

public class FirebaseInstanceIdService
  extends zzb
{
  private static BroadcastReceiver wg;
  @VisibleForTesting
  static final Object wh = new Object();
  @VisibleForTesting
  static boolean wi = false;
  private boolean wj = false;
  
  private static Intent zzBb(int paramInt)
  {
    Intent localIntent = new Intent("ACTION_TOKEN_REFRESH_RETRY");
    localIntent.putExtra("next_retry_delay_in_seconds", paramInt);
    return localIntent;
  }
  
  private void zzBc(int paramInt)
  {
    AlarmManager localAlarmManager = (AlarmManager)getSystemService("alarm");
    PendingIntent localPendingIntent = zzf.zza(this, 0, zzBb(paramInt * 2), 134217728);
    localAlarmManager.set(3, SystemClock.elapsedRealtime() + paramInt * 1000, localPendingIntent);
  }
  
  private String zzT(Intent paramIntent)
  {
    String str = paramIntent.getStringExtra("subtype");
    paramIntent = str;
    if (str == null) {
      paramIntent = "";
    }
    return paramIntent;
  }
  
  private int zza(Intent paramIntent, boolean paramBoolean)
  {
    int j = 10;
    int i;
    if (paramIntent == null)
    {
      i = 10;
      if ((i >= 10) || (paramBoolean)) {
        break label39;
      }
      j = 30;
    }
    label39:
    while (i < 10)
    {
      return j;
      i = paramIntent.getIntExtra("next_retry_delay_in_seconds", 0);
      break;
    }
    if (i > 28800) {
      return 28800;
    }
    return i;
  }
  
  static void zza(Context paramContext, FirebaseInstanceId paramFirebaseInstanceId)
  {
    synchronized (wh)
    {
      if (wi) {
        return;
      }
      ??? = paramFirebaseInstanceId.zzakg();
      if ((??? == null) || (((zzg.zza)???).zzmu(zzd.zzbAI)) || (paramFirebaseInstanceId.zzaki().zzakl() != null))
      {
        zzcC(paramContext);
        return;
      }
    }
  }
  
  private void zza(Intent paramIntent, boolean paramBoolean1, boolean paramBoolean2)
  {
    String str2;
    for (;;)
    {
      synchronized (wh)
      {
        wi = false;
        if (Rpc.findAppIDPackage(this) == null) {
          return;
        }
      }
      ??? = FirebaseInstanceId.getInstance();
      localObject2 = ((FirebaseInstanceId)???).zzakg();
      if ((localObject2 == null) || (((zzg.zza)localObject2).zzmu(zzd.zzbAI))) {
        try
        {
          str2 = ((FirebaseInstanceId)???).zzakh();
          if (str2 != null)
          {
            if (this.wj) {
              Log.d("FirebaseInstanceId", "get master token succeeded");
            }
            zza(this, (FirebaseInstanceId)???);
            if ((!paramBoolean2) && (localObject2 != null) && ((localObject2 == null) || (str2.equals(((zzg.zza)localObject2).zzanu)))) {
              continue;
            }
            onTokenRefresh();
          }
        }
        catch (IOException localIOException1)
        {
          zzc(paramIntent, localIOException1.getMessage());
          return;
          zzc(paramIntent, "returned token is null");
          return;
        }
        catch (SecurityException paramIntent)
        {
          Log.e("FirebaseInstanceId", "Unable to get master token", paramIntent);
          return;
        }
      }
    }
    Object localObject2 = localIOException1.zzaki();
    String str1 = ((zze)localObject2).zzakl();
    if (str1 != null)
    {
      Object localObject3 = str1.split("!");
      int j;
      if (localObject3.length == 2)
      {
        str2 = localObject3[0];
        localObject3 = localObject3[1];
        j = -1;
      }
      for (;;)
      {
        try
        {
          int k = str2.hashCode();
          int i = j;
          switch (k)
          {
          default: 
            i = j;
          case 84: 
            switch (i)
            {
            default: 
              ((zze)localObject2).zzmq(str1);
              str1 = ((zze)localObject2).zzakl();
            }
            break;
          case 83: 
            i = j;
            if (!str2.equals("S")) {
              continue;
            }
            i = 0;
            break;
          case 85: 
            i = j;
            if (!str2.equals("U")) {
              continue;
            }
            i = 1;
            continue;
            FirebaseInstanceId.getInstance().zzmn((String)localObject3);
            if (!this.wj) {
              continue;
            }
            Log.d("FirebaseInstanceId", "subscribe operation succeeded");
            continue;
            FirebaseInstanceId.getInstance().zzmo((String)localObject3);
          }
        }
        catch (IOException localIOException2)
        {
          zzc(paramIntent, localIOException2.getMessage());
          return;
        }
        if (this.wj) {
          Log.d("FirebaseInstanceId", "unsubscribe operation succeeded");
        }
      }
    }
    Log.d("FirebaseInstanceId", "topic sync succeeded");
  }
  
  private void zza(Rpc paramRpc, Bundle paramBundle)
  {
    String str = Rpc.findAppIDPackage(this);
    if (str == null)
    {
      Log.w("FirebaseInstanceId", "Unable to respond to ping due to missing target package");
      return;
    }
    Intent localIntent = new Intent("com.google.android.gcm.intent.SEND");
    localIntent.setPackage(str);
    localIntent.putExtras(paramBundle);
    paramRpc.zzB(localIntent);
    localIntent.putExtra("google.to", "google.com/iid");
    localIntent.putExtra("google.message_id", Rpc.nextId());
    sendOrderedBroadcast(localIntent, "com.google.android.gtalkservice.permission.GTALK_SERVICE");
  }
  
  private void zzc(Intent arg1, String paramString)
  {
    boolean bool = zzcD(this);
    final int i = zza(???, bool);
    Log.d("FirebaseInstanceId", String.valueOf(paramString).length() + 47 + "background sync failed: " + paramString + ", retry in " + i + "s");
    synchronized (wh)
    {
      zzBc(i);
      wi = true;
      if (!bool)
      {
        if (this.wj) {
          Log.d("FirebaseInstanceId", "device not connected. Connectivity change received registered");
        }
        if (wg == null) {
          wg = new BroadcastReceiver()
          {
            public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
            {
              if (FirebaseInstanceIdService.zzcE(paramAnonymousContext))
              {
                if (FirebaseInstanceIdService.zza(FirebaseInstanceIdService.this)) {
                  Log.d("FirebaseInstanceId", "connectivity changed. starting background sync.");
                }
                FirebaseInstanceIdService.this.getApplicationContext().unregisterReceiver(this);
                zzf.zzakn().zzh(paramAnonymousContext, FirebaseInstanceIdService.zzBd(i));
              }
            }
          };
        }
        getApplicationContext().registerReceiver(wg, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
      }
      return;
    }
  }
  
  static void zzcC(Context paramContext)
  {
    if (Rpc.findAppIDPackage(paramContext) == null) {
      return;
    }
    synchronized (wh)
    {
      if (!wi)
      {
        zzf.zzakn().zzh(paramContext, zzBb(0));
        wi = true;
      }
      return;
    }
  }
  
  private static boolean zzcD(Context paramContext)
  {
    paramContext = ((ConnectivityManager)paramContext.getSystemService("connectivity")).getActiveNetworkInfo();
    return (paramContext != null) && (paramContext.isConnected());
  }
  
  private zzd zzmp(String paramString)
  {
    if (paramString == null) {
      return zzd.zza(this, null);
    }
    Bundle localBundle = new Bundle();
    localBundle.putString("subtype", paramString);
    return zzd.zza(this, localBundle);
  }
  
  @WorkerThread
  public void onTokenRefresh() {}
  
  protected Intent zzP(Intent paramIntent)
  {
    return zzf.zzakn().zzako();
  }
  
  public boolean zzR(Intent paramIntent)
  {
    this.wj = Log.isLoggable("FirebaseInstanceId", 3);
    if ((paramIntent.getStringExtra("error") != null) || (paramIntent.getStringExtra("registration_id") != null))
    {
      String str2 = zzT(paramIntent);
      if (this.wj)
      {
        str1 = String.valueOf(str2);
        if (str1.length() == 0) {
          break label84;
        }
      }
      label84:
      for (String str1 = "Register result in service ".concat(str1);; str1 = new String("Register result in service "))
      {
        Log.d("FirebaseInstanceId", str1);
        zzmp(str2).zzakk().zzE(paramIntent);
        return true;
      }
    }
    return false;
  }
  
  public void zzS(Intent paramIntent)
  {
    String str2 = zzT(paramIntent);
    zzd localzzd = zzmp(str2);
    String str1 = paramIntent.getStringExtra("CMD");
    Object localObject;
    if (this.wj)
    {
      localObject = String.valueOf(paramIntent.getExtras());
      Log.d("FirebaseInstanceId", String.valueOf(str2).length() + 18 + String.valueOf(str1).length() + String.valueOf(localObject).length() + "Service command " + str2 + " " + str1 + " " + (String)localObject);
    }
    if (paramIntent.getStringExtra("unregistered") != null)
    {
      localObject = localzzd.zzakj();
      str1 = str2;
      if (str2 == null) {
        str1 = "";
      }
      ((zzg)localObject).zzeK(str1);
      localzzd.zzakk().zzE(paramIntent);
    }
    do
    {
      do
      {
        return;
        if ("gcm.googleapis.com/refresh".equals(paramIntent.getStringExtra("from")))
        {
          localzzd.zzakj().zzeK(str2);
          zza(paramIntent, false, true);
          return;
        }
        if ("RST".equals(str1))
        {
          localzzd.zzJP();
          zza(paramIntent, true, true);
          return;
        }
        if (!"RST_FULL".equals(str1)) {
          break;
        }
      } while (localzzd.zzakj().isEmpty());
      localzzd.zzJP();
      localzzd.zzakj().zzJU();
      zza(paramIntent, true, true);
      return;
      if ("SYNC".equals(str1))
      {
        localzzd.zzakj().zzeK(str2);
        zza(paramIntent, false, true);
        return;
      }
    } while (!"PING".equals(str1));
    zza(localzzd.zzakk(), paramIntent.getExtras());
  }
  
  public void zzp(Intent paramIntent)
  {
    String str2 = paramIntent.getAction();
    String str1 = str2;
    if (str2 == null) {
      str1 = "";
    }
    switch (str1.hashCode())
    {
    }
    label40:
    for (int i = -1;; i = 0) {
      switch (i)
      {
      default: 
        zzS(paramIntent);
        return;
        if (!str1.equals("ACTION_TOKEN_REFRESH_RETRY")) {
          break label40;
        }
      }
    }
    zza(paramIntent, false, false);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/firebase/iid/FirebaseInstanceIdService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */