package com.google.android.tvlauncher.util;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;
import java.net.URISyntaxException;
import java.util.List;

public class IntentLauncher
{
  private static final boolean DEBUG = false;
  private static final String TAG = "IntentLauncher";
  
  private static boolean launchIntent(Context paramContext, Intent paramIntent)
  {
    if (paramIntent == null) {
      return false;
    }
    Object localObject = paramContext.getPackageManager();
    List localList = ((PackageManager)localObject).queryIntentActivities(paramIntent, 0);
    if ((localList != null) && (localList.size() > 0)) {
      try
      {
        paramContext.startActivity(paramIntent);
        return true;
      }
      catch (ActivityNotFoundException paramContext)
      {
        Log.e("IntentLauncher", "Failed to launch " + paramIntent, paramContext);
        return false;
      }
    }
    paramIntent.setPackage("com.google.android.tvrecommendations");
    localObject = ((PackageManager)localObject).queryBroadcastReceivers(paramIntent, 0);
    if ((localObject != null) && (((List)localObject).size() > 0))
    {
      paramContext.sendBroadcast(paramIntent);
      return true;
    }
    Log.e("IntentLauncher", "Activity not found for intent: " + paramIntent);
    return false;
  }
  
  public static void launchIntentFromUri(Context paramContext, String paramString)
  {
    launchIntentFromUri(paramContext, paramString, false);
  }
  
  private static void launchIntentFromUri(Context paramContext, String paramString, boolean paramBoolean)
  {
    if (!launchIntent(paramContext, parseUri(paramString, paramBoolean))) {
      Toast.makeText(paramContext, 2131492990, 0).show();
    }
  }
  
  public static void launchMediaIntentFromUri(Context paramContext, String paramString)
  {
    launchIntentFromUri(paramContext, paramString, true);
  }
  
  private static Intent parseUri(String paramString, boolean paramBoolean)
  {
    Object localObject;
    if (paramString == null)
    {
      Log.e("IntentLauncher", "No URI provided");
      localObject = null;
    }
    for (;;)
    {
      return (Intent)localObject;
      try
      {
        Intent localIntent = Intent.parseUri(paramString, 1);
        localObject = localIntent;
        if (paramBoolean)
        {
          localIntent.putExtra("android.intent.extra.START_PLAYBACK", true);
          return localIntent;
        }
      }
      catch (URISyntaxException localURISyntaxException)
      {
        Log.e("IntentLauncher", "Bad URI syntax: " + paramString);
      }
    }
    return null;
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/util/IntentLauncher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */