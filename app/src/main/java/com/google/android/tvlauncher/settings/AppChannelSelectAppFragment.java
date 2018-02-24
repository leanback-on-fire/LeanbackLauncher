package com.google.android.tvlauncher.settings;

import android.app.Fragment;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v17.preference.LeanbackPreferenceFragment;
import android.support.v7.preference.Preference;
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

public class AppChannelSelectAppFragment
  extends LeanbackPreferenceFragment
  implements AppModel.LoadAppsCallback
{
  private AppModel mAppModel;
  private final FragmentEventLogger mEventLogger = new FragmentEventLogger(this);
  
  private void createWatchNextPreference()
  {
    PreferenceScreen localPreferenceScreen = getPreferenceScreen();
    Preference localPreference = new Preference(getPreferenceManager().getContext());
    localPreference.setLayoutResource(2130968747);
    localPreference.setKey("show_watch_next_row_key");
    localPreference.setTitle(2131493086);
    localPreference.setFragment(AppChannelWatchNextFragment.class.getName());
    localPreference.setPersistent(false);
    localPreferenceScreen.addPreference(localPreference);
  }
  
  public static Fragment newInstance()
  {
    return new AppChannelSelectAppFragment();
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
    PreferenceScreen localPreferenceScreen = getPreferenceScreen();
    localPreferenceScreen.removeAll();
    createWatchNextPreference();
    if ((paramList != null) && (paramList.size() > 0))
    {
      Collections.sort(paramList);
      paramList = paramList.iterator();
      while (paramList.hasNext())
      {
        AppModel.AppInfo localAppInfo = (AppModel.AppInfo)paramList.next();
        Object localObject = new AppPreference(localContext);
        ((AppPreference)localObject).setKey(localAppInfo.mPackageName);
        ((AppPreference)localObject).setTitle(localAppInfo.mTitle);
        ((AppPreference)localObject).setBanner(localAppInfo.mBanner);
        ((AppPreference)localObject).setIcon(localAppInfo.mIcon);
        ((AppPreference)localObject).setPersistent(false);
        ((AppPreference)localObject).setSummary(localContext.getResources().getQuantityString(2131427328, localAppInfo.mNumberOfChannels, new Object[] { Integer.valueOf(localAppInfo.mNumberOfChannels) }));
        localPreferenceScreen.addPreference((Preference)localObject);
        ((AppPreference)localObject).setFragment(AppChannelEnableChannelFragment.class.getName());
        localObject = ((AppPreference)localObject).getExtras();
        ((Bundle)localObject).putString("channel_app", localAppInfo.mPackageName);
        ((Bundle)localObject).putString("app_name", localAppInfo.mTitle.toString());
      }
    }
    this.mEventLogger.log(new UserActionEvent("open_manage_channels"));
  }
  
  public void onCreatePreferences(Bundle paramBundle, String paramString)
  {
    paramBundle = getPreferenceManager().getContext();
    paramBundle = getPreferenceManager().createPreferenceScreen(paramBundle);
    paramBundle.setTitle(2131492893);
    setPreferenceScreen(paramBundle);
    this.mAppModel = new AppModel(getPreferenceManager().getContext());
  }
  
  public void onPause()
  {
    super.onPause();
    this.mAppModel.onPause();
  }
  
  public void onResume()
  {
    super.onResume();
    this.mAppModel.loadApps(this);
  }
  
  private class AppPreference
    extends Preference
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
      View localView = paramPreferenceViewHolder.findViewById(2131951788);
      paramPreferenceViewHolder = (ImageView)paramPreferenceViewHolder.findViewById(2131951789);
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


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/settings/AppChannelSelectAppFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */