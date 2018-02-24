package com.google.android.tvlauncher.settings;

import android.app.Activity;
import android.os.Bundle;
import android.support.v17.preference.LeanbackPreferenceFragment;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceClickListener;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.preference.PreferenceScreen;
import android.util.Pair;
import com.google.android.tvlauncher.appsview.AppsManager;
import com.google.android.tvlauncher.appsview.AppsManager.AppsViewChangeListener;
import com.google.android.tvlauncher.appsview.LaunchItem;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FavoriteAppsSelectionFragment
  extends LeanbackPreferenceFragment
  implements AppsManager.AppsViewChangeListener, Preference.OnPreferenceClickListener
{
  private AppsManager mAppsManager;
  private boolean mStarted;
  
  private void addPreference(PreferenceScreen paramPreferenceScreen, LaunchItem paramLaunchItem, Preference paramPreference)
  {
    paramPreference.setLayoutResource(2130968619);
    paramPreference.setKey(paramLaunchItem.getPackageName());
    paramPreference.setIcon(paramLaunchItem.getItemDrawable());
    paramPreference.setTitle(paramLaunchItem.getLabel());
    paramPreference.setOnPreferenceClickListener(this);
    paramPreferenceScreen.addPreference(paramPreference);
  }
  
  public static FavoriteAppsSelectionFragment newInstance()
  {
    return new FavoriteAppsSelectionFragment();
  }
  
  public void onCreatePreferences(Bundle paramBundle, String paramString)
  {
    paramBundle = getPreferenceManager().getContext();
    paramBundle = getPreferenceManager().createPreferenceScreen(paramBundle);
    paramBundle.setTitle(2131493073);
    setPreferenceScreen(paramBundle);
    paramBundle.setOrderingAsAdded(false);
    this.mAppsManager = AppsManager.getInstance(getContext());
    this.mAppsManager.refreshLaunchItems();
  }
  
  public void onEditModeItemOrderChange(ArrayList<LaunchItem> paramArrayList, boolean paramBoolean, Pair<Integer, Integer> paramPair) {}
  
  public void onLaunchItemsAddedOrUpdated(ArrayList<LaunchItem> paramArrayList)
  {
    PreferenceScreen localPreferenceScreen = getPreferenceScreen();
    Iterator localIterator = paramArrayList.iterator();
    while (localIterator.hasNext())
    {
      LaunchItem localLaunchItem = (LaunchItem)localIterator.next();
      if (!this.mAppsManager.isFavorite(localLaunchItem))
      {
        Preference localPreference = getPreferenceManager().findPreference(localLaunchItem.getPackageName());
        paramArrayList = localPreference;
        if (localPreference == null) {
          paramArrayList = new Preference(getPreferenceManager().getContext());
        }
        addPreference(localPreferenceScreen, localLaunchItem, paramArrayList);
      }
    }
    setPreferenceScreen(localPreferenceScreen);
  }
  
  public void onLaunchItemsLoaded()
  {
    PreferenceScreen localPreferenceScreen = getPreferenceScreen();
    localPreferenceScreen.removeAll();
    Iterator localIterator = this.mAppsManager.getAllNonFavoriteLaunchItems().iterator();
    while (localIterator.hasNext()) {
      addPreference(localPreferenceScreen, (LaunchItem)localIterator.next(), new Preference(getPreferenceManager().getContext()));
    }
    setPreferenceScreen(localPreferenceScreen);
  }
  
  public void onLaunchItemsRemoved(ArrayList<LaunchItem> paramArrayList)
  {
    PreferenceScreen localPreferenceScreen = getPreferenceScreen();
    paramArrayList = paramArrayList.iterator();
    while (paramArrayList.hasNext())
    {
      Object localObject = (LaunchItem)paramArrayList.next();
      localObject = getPreferenceManager().findPreference(((LaunchItem)localObject).getPackageName());
      if (localObject != null) {
        localPreferenceScreen.removePreference((Preference)localObject);
      }
    }
    setPreferenceScreen(localPreferenceScreen);
  }
  
  public boolean onPreferenceClick(Preference paramPreference)
  {
    this.mAppsManager.addToFavorites(paramPreference.getKey());
    getActivity().finish();
    return false;
  }
  
  public void onStart()
  {
    super.onStart();
    if (!this.mStarted)
    {
      this.mAppsManager.registerAppsViewChangeListener(this);
      this.mStarted = true;
    }
  }
  
  public void onStop()
  {
    super.onStop();
    if (this.mStarted)
    {
      this.mAppsManager.unregisterAppsViewChangeListener(this);
      this.mStarted = false;
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/settings/FavoriteAppsSelectionFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */