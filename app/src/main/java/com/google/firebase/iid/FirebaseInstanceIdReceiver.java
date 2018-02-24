package com.google.firebase.iid;

import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Base64;
import android.util.Log;

public final class FirebaseInstanceIdReceiver
  extends WakefulBroadcastReceiver
{
  public void onReceive(Context paramContext, Intent paramIntent)
  {
    if (isOrderedBroadcast()) {
      setResultCode(500);
    }
    paramIntent.setComponent(null);
    paramIntent.setPackage(paramContext.getPackageName());
    if (Build.VERSION.SDK_INT <= 18) {
      paramIntent.removeCategory(paramContext.getPackageName());
    }
    String str = paramIntent.getStringExtra("gcm.rawData64");
    if (str != null)
    {
      paramIntent.putExtra("rawData", Base64.decode(str, 0));
      paramIntent.removeExtra("gcm.rawData64");
    }
    str = paramIntent.getStringExtra("from");
    if (("com.google.android.c2dm.intent.REGISTRATION".equals(paramIntent.getAction())) || ("google.com/iid".equals(str)) || ("gcm.googleapis.com/refresh".equals(str))) {
      str = "com.google.firebase.INSTANCE_ID_EVENT";
    }
    for (;;)
    {
      int i = -1;
      if (str != null) {
        i = zza(paramContext, str, paramIntent);
      }
      if (isOrderedBroadcast()) {
        setResultCode(i);
      }
      return;
      if ("com.google.android.c2dm.intent.RECEIVE".equals(paramIntent.getAction()))
      {
        str = "com.google.firebase.MESSAGING_EVENT";
      }
      else
      {
        Log.d("FirebaseInstanceId", "Unexpected intent");
        str = null;
      }
    }
  }
  
  public int zza(Context paramContext, String paramString, Intent paramIntent)
  {
    return zzf.zzakn().zzb(paramContext, paramString, paramIntent);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/firebase/iid/FirebaseInstanceIdReceiver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */