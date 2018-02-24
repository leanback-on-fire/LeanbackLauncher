package com.google.android.tvlauncher.settings;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v14.preference.SwitchPreference;
import android.support.v17.preference.LeanbackPreferenceFragment;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.PreferenceViewHolder;
import android.view.View;
import android.widget.ImageView;
import com.google.android.tvlauncher.analytics.FragmentEventLogger;
import com.google.android.tvlauncher.analytics.UserActionEvent;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class AppChannelWatchNextFragment
  extends LeanbackPreferenceFragment
  implements AppModel.LoadAppsCallback, Preference.OnPreferenceChangeListener
{
  private AppModel mAppModel;
  private final FragmentEventLogger mEventLogger = new FragmentEventLogger(this);
  private PreferenceCategory mSourceGroup;
  
  private void createWatchNextPreference()
  {
    PreferenceScreen localPreferenceScreen = getPreferenceScreen();
    SwitchPreference localSwitchPreference = new SwitchPreference(getPreferenceManager().getContext());
    localSwitchPreference.setLayoutResource(2130968747);
    localSwitchPreference.setKey("show_watch_next_row_key");
    localSwitchPreference.setTitle(2131493076);
    localSwitchPreference.setPersistent(false);
    SharedPreferences localSharedPreferences = getContext().getSharedPreferences("com.google.android.tvlauncher.data.TvDataManager.WATCH_NEXT_PREF_FILE_NAME", 0);
    if (localSharedPreferences != null)
    {
      boolean bool = localSharedPreferences.getBoolean("show_watch_next_row_key", true);
      localSwitchPreference.setChecked(bool);
      this.mSourceGroup.setEnabled(bool);
    }
    for (;;)
    {
      localSwitchPreference.setOnPreferenceChangeListener(this);
      localPreferenceScreen.addPreference(localSwitchPreference);
      return;
      localSwitchPreference.setChecked(true);
      this.mSourceGroup.setEnabled(true);
    }
  }
  
  public static Fragment newInstance()
  {
    return new AppChannelWatchNextFragment();
  }
  
  public void onAppsChanged()
  {
    this.mAppModel.loadApps(this);
  }
  
  public void onAppsLoaded(List<AppModel.AppInfo> paramList)
  {
    if (!isAdded()) {
      return;
    }
    Context localContext = getPreferenceManager().getContext();
    this.mSourceGroup.removeAll();
    Object localObject1 = new Preference(localContext);
    ((Preference)localObject1).setTitle(2131493088);
    ((Preference)localObject1).setSelectable(false);
    this.mSourceGroup.addPreference((Preference)localObject1);
    if ((paramList != null) && (paramList.size() > 0))
    {
      Collections.sort(paramList);
      paramList = paramList.iterator();
      if (paramList.hasNext())
      {
        Object localObject2 = (AppModel.AppInfo)paramList.next();
        localObject1 = new AppPreference(localContext);
        String str = "watch_next_package_key_prefix".concat(((AppModel.AppInfo)localObject2).mPackageName);
        ((AppPreference)localObject1).setKey(str);
        ((AppPreference)localObject1).setTitle(((AppModel.AppInfo)localObject2).mTitle);
        ((AppPreference)localObject1).setBanner(((AppModel.AppInfo)localObject2).mBanner);
        ((AppPreference)localObject1).setIcon(((AppModel.AppInfo)localObject2).mIcon);
        localObject2 = getContext().getSharedPreferences("com.google.android.tvlauncher.data.TvDataManager.WATCH_NEXT_PREF_FILE_NAME", 0);
        if ((localObject2 == null) || (!((SharedPreferences)localObject2).contains(str))) {
          ((AppPreference)localObject1).setChecked(true);
        }
        for (;;)
        {
          ((AppPreference)localObject1).setPersistent(false);
          this.mSourceGroup.addPreference((Preference)localObject1);
          ((AppPreference)localObject1).setOnPreferenceChangeListener(this);
          break;
          ((AppPreference)localObject1).setChecked(false);
        }
      }
    }
    this.mEventLogger.log(new UserActionEvent("open_manage_channels"));
  }
  
  public void onCreatePreferences(Bundle paramBundle, String paramString)
  {
    paramBundle = getPreferenceManager().getContext();
    paramString = getPreferenceManager().createPreferenceScreen(paramBundle);
    paramString.setTitle(2131493087);
    setPreferenceScreen(paramString);
    this.mSourceGroup = new PreferenceCategory(paramBundle);
    this.mSourceGroup.setTitle(2131493089);
    createWatchNextPreference();
    paramString.addPreference(this.mSourceGroup);
    this.mAppModel = new AppModel(paramBundle);
  }
  
  public void onPause()
  {
    super.onPause();
    this.mAppModel.onPause();
  }
  
  public boolean onPreferenceChange(Preference paramPreference, Object paramObject)
  {
    paramObject = (Boolean)paramObject;
    SharedPreferences localSharedPreferences = getContext().getSharedPreferences("com.google.android.tvlauncher.data.TvDataManager.WATCH_NEXT_PREF_FILE_NAME", 0);
    if ((paramPreference.getKey() != null) && (paramPreference.getKey().startsWith("watch_next_package_key_prefix")))
    {
      if (((Boolean)paramObject).booleanValue())
      {
        localSharedPreferences.edit().remove(paramPreference.getKey()).apply();
        return true;
      }
      localSharedPreferences.edit().putBoolean(paramPreference.getKey(), false).apply();
      return true;
    }
    if ("show_watch_next_row_key".equals(paramPreference.getKey()))
    {
      localSharedPreferences.edit().putBoolean(paramPreference.getKey(), ((Boolean)paramObject).booleanValue()).apply();
      this.mSourceGroup.setEnabled(((Boolean)paramObject).booleanValue());
      return true;
    }
    return false;
  }
  
  public void onResume()
  {
    super.onResume();
    this.mAppModel.loadApps(this);
  }
  
  private class AppPreference
    extends SwitchPreference
  {
    private Drawable mBanner;
    
    AppPreference(Context paramContext)
    {
      super();
      setLayoutResource(2130968608);
    }
    
    public void onBindViewHolder(PreferenceViewHolder paramPreferenceViewHolder)
    {
      super.onBindViewHolder(paramPreferenceViewHolder);
      View localView = paramPreferenceViewHolder.findViewById(R.id.icon_frame);
      paramPreferenceViewHolder = (ImageView)paramPreferenceViewHolder.findViewById(R.id.banner);
      if (this.mBanner != null)
      {
        paramPreferenceViewHolder.setImageDrawable(this.mBanner);
        paramPreferenceViewHolder.setVisibility(0);
        localView.setVisibility(8);
        return;
      }
      paramPreferenceViewHolder.setVisibility(8);
      localView.setVisibility(0);
    }
    
    public void setBanner(Drawable paramDrawable)
    {
      this.mBanner = paramDrawable;
    }
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/settings/AppChannelWatchNextFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */