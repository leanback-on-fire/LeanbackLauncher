package com.google.android.tvlauncher.settings;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v14.preference.SwitchPreference;
import android.support.v17.preference.LeanbackPreferenceFragment;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.preference.PreferenceScreen;
import com.google.android.tvlauncher.appsview.AppsViewActivity;
import com.google.android.tvlauncher.util.BuildType;

public class HomeScreenPreferenceFragment
  extends LeanbackPreferenceFragment
  implements Preference.OnPreferenceChangeListener
{
  private static final String REORDER_APPS_KEY = "reorderapps";
  private static final String REORDER_GAMES_KEY = "reordergames";
  
  private void createAppsViewPreference(PreferenceScreen paramPreferenceScreen, Context paramContext)
  {
    Object localObject = new PreferenceCategory(paramContext);
    ((PreferenceCategory)localObject).setTitle(2131492897);
    paramPreferenceScreen.addPreference((Preference)localObject);
    localObject = new Preference(paramContext);
    ((Preference)localObject).setKey("reorderapps");
    ((Preference)localObject).setTitle(2131492931);
    paramPreferenceScreen.addPreference((Preference)localObject);
    paramContext = new Preference(paramContext);
    paramContext.setKey("reordergames");
    paramContext.setTitle(2131492932);
    paramPreferenceScreen.addPreference(paramContext);
    setPreferenceScreen(paramPreferenceScreen);
  }
  
  private void createGuideViewPreference(PreferenceScreen paramPreferenceScreen, Context paramContext)
  {
    Object localObject = new PreferenceCategory(paramContext);
    ((PreferenceCategory)localObject).setTitle(2131492996);
    paramPreferenceScreen.addPreference((Preference)localObject);
    localObject = new Preference(getPreferenceManager().getContext());
    ((Preference)localObject).setTitle(2131492893);
    ((Preference)localObject).setFragment(AppChannelSelectAppFragment.class.getName());
    ((Preference)localObject).setPersistent(false);
    paramPreferenceScreen.addPreference((Preference)localObject);
    SwitchPreference localSwitchPreference = new SwitchPreference(paramContext);
    localSwitchPreference.setKey("show_preview_video_key");
    localSwitchPreference.setTitle(2131492999);
    localObject = getContext().getSharedPreferences("com.google.android.tvlauncher.data.TvDataManager.PREVIEW_VIDEO_PREF_FILE_NAME", 0);
    localSwitchPreference.setChecked(((SharedPreferences)localObject).getBoolean("show_preview_video_key", true));
    localSwitchPreference.setPersistent(false);
    localSwitchPreference.setOnPreferenceChangeListener(this);
    paramPreferenceScreen.addPreference(localSwitchPreference);
    paramContext = new SwitchPreference(paramContext);
    paramContext.setKey("enable_preview_audio_key");
    paramContext.setTitle(2131492998);
    paramContext.setChecked(((SharedPreferences)localObject).getBoolean("enable_preview_audio_key", true));
    paramContext.setPersistent(false);
    paramContext.setOnPreferenceChangeListener(this);
    paramPreferenceScreen.addPreference(paramContext);
  }
  
  public static Fragment newInstance()
  {
    return new HomeScreenPreferenceFragment();
  }
  
  public void onCreatePreferences(Bundle paramBundle, String paramString)
  {
    paramBundle = getPreferenceManager().getContext();
    paramString = getPreferenceManager().createPreferenceScreen(paramBundle);
    paramString.setTitle(2131493075);
    createGuideViewPreference(paramString, paramBundle);
    createAppsViewPreference(paramString, paramBundle);
    if (BuildType.DOGFOOD.booleanValue()) {
      paramString.addPreference((Preference)BuildType.newInstance(Preference.class, "com.google.android.tvlauncher.settings.DogfoodPreference", new Object[] { paramBundle }));
    }
    for (;;)
    {
      setPreferenceScreen(paramString);
      return;
      if (BuildType.DEBUG.booleanValue()) {
        paramString.addPreference((Preference)BuildType.newInstance(Preference.class, "com.google.android.tvlauncher.settings.DebugPreference", new Object[] { paramBundle }));
      }
    }
  }
  
  public boolean onPreferenceChange(Preference paramPreference, Object paramObject)
  {
    SharedPreferences localSharedPreferences = getContext().getSharedPreferences("com.google.android.tvlauncher.data.TvDataManager.PREVIEW_VIDEO_PREF_FILE_NAME", 0);
    if ("show_preview_video_key".equals(paramPreference.getKey()))
    {
      paramObject = (Boolean)paramObject;
      localSharedPreferences.edit().putBoolean(paramPreference.getKey(), ((Boolean)paramObject).booleanValue()).apply();
      ((SwitchPreference)getPreferenceScreen().findPreference("enable_preview_audio_key")).setEnabled(((Boolean)paramObject).booleanValue());
      return true;
    }
    if ("enable_preview_audio_key".equals(paramPreference.getKey()))
    {
      paramObject = (Boolean)paramObject;
      localSharedPreferences.edit().putBoolean(paramPreference.getKey(), ((Boolean)paramObject).booleanValue()).apply();
      return true;
    }
    return false;
  }
  
  public boolean onPreferenceTreeClick(Preference paramPreference)
  {
    String str = paramPreference.getKey();
    if ("reorderapps".equals(str))
    {
      AppsViewActivity.startAppsViewActivity(Integer.valueOf(0), getContext());
      return true;
    }
    if ("reordergames".equals(str))
    {
      AppsViewActivity.startAppsViewActivity(Integer.valueOf(1), getContext());
      return true;
    }
    return super.onPreferenceTreeClick(paramPreference);
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/settings/HomeScreenPreferenceFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */