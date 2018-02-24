package com.google.android.tvlauncher.settings;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v14.preference.PreferenceFragment;
import android.support.v17.preference.LeanbackSettingsFragment;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.transition.Scene;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.Transition.TransitionListener;
import android.transition.TransitionManager;
import android.view.ViewGroup;
import android.view.Window;
import com.google.android.tvlauncher.analytics.LoggingActivity;

public class AppChannelPermissionActivity
  extends LoggingActivity
{
  private static final String TAG_CHANNEL_PERMISSION_FRAGMENT = "apps_channel_permission_fragment";
  
  public AppChannelPermissionActivity()
  {
    super("ManageChannels");
  }
  
  public void finish()
  {
    final Object localObject = getFragmentManager().findFragmentByTag("apps_channel_permission_fragment");
    if ((localObject != null) && (((Fragment)localObject).isResumed()))
    {
      Scene localScene = new Scene((ViewGroup)findViewById(16908290));
      localScene.setEnterAction(new Runnable()
      {
        public void run()
        {
          AppChannelPermissionActivity.this.getFragmentManager().beginTransaction().remove(localObject).commitNow();
        }
      });
      localObject = new Slide(8388613);
      ((Slide)localObject).addListener(new Transition.TransitionListener()
      {
        public void onTransitionCancel(Transition paramAnonymousTransition) {}
        
        public void onTransitionEnd(Transition paramAnonymousTransition)
        {
          paramAnonymousTransition.removeListener(this);
          AppChannelPermissionActivity.this.finish();
        }
        
        public void onTransitionPause(Transition paramAnonymousTransition) {}
        
        public void onTransitionResume(Transition paramAnonymousTransition) {}
        
        public void onTransitionStart(Transition paramAnonymousTransition)
        {
          AppChannelPermissionActivity.this.getWindow().setDimAmount(0.0F);
        }
      });
      TransitionManager.go(localScene, (Transition)localObject);
      return;
    }
    super.finish();
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    if (paramBundle == null)
    {
      getFragmentManager().beginTransaction().add(16908290, AppChannelFragment.newInstance(), "apps_channel_permission_fragment").commit();
      TransitionManager.go(new Scene((ViewGroup)findViewById(16908290)), new Slide(8388613));
    }
  }
  
  public static class AppChannelFragment
    extends LeanbackSettingsFragment
  {
    public static AppChannelFragment newInstance()
    {
      return new AppChannelFragment();
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
      startPreferenceFragment(AppChannelSelectAppFragment.newInstance());
    }
    
    public boolean onPreferenceStartScreen(PreferenceFragment paramPreferenceFragment, PreferenceScreen paramPreferenceScreen)
    {
      return false;
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/settings/AppChannelPermissionActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */