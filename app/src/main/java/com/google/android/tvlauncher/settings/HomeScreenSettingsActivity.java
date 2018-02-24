package com.google.android.tvlauncher.settings;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v14.preference.PreferenceFragment;
import android.support.v17.preference.LeanbackSettingsFragment;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import com.google.android.tvlauncher.analytics.LoggingActivity;

public class HomeScreenSettingsActivity
  extends LoggingActivity
{
  public HomeScreenSettingsActivity()
  {
    super("HomeScreenSettings");
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    if (paramBundle == null) {
      getFragmentManager().beginTransaction().add(16908290, SettingsFragment.newInstance()).commit();
    }
  }
  
  public static class SettingsFragment
    extends LeanbackSettingsFragment
  {
    public static SettingsFragment newInstance()
    {
      return new SettingsFragment();
    }
    
    public boolean onPreferenceStartFragment(PreferenceFragment paramPreferenceFragment, Preference paramPreference)
    {
      paramPreference = Fragment.instantiate(getActivity(), paramPreference.getFragment(), paramPreference.getExtras());
      paramPreference.setTargetFragment(paramPreferenceFragment, 0);
      startPreferenceFragment(paramPreference);
      return true;
    }
    
    public void onPreferenceStartInitialScreen()
    {
      startPreferenceFragment(HomeScreenPreferenceFragment.newInstance());
    }
    
    public boolean onPreferenceStartScreen(PreferenceFragment paramPreferenceFragment, PreferenceScreen paramPreferenceScreen)
    {
      return false;
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/settings/HomeScreenSettingsActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */