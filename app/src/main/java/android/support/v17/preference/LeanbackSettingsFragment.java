package android.support.v17.preference;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.v14.preference.MultiSelectListPreference;
import android.support.v14.preference.PreferenceFragment;
import android.support.v14.preference.PreferenceFragment.OnPreferenceDisplayDialogCallback;
import android.support.v14.preference.PreferenceFragment.OnPreferenceStartFragmentCallback;
import android.support.v14.preference.PreferenceFragment.OnPreferenceStartScreenCallback;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.Space;

public abstract class LeanbackSettingsFragment
  extends Fragment
  implements PreferenceFragment.OnPreferenceStartFragmentCallback, PreferenceFragment.OnPreferenceStartScreenCallback, PreferenceFragment.OnPreferenceDisplayDialogCallback
{
  private static final String PREFERENCE_FRAGMENT_TAG = "android.support.v17.preference.LeanbackSettingsFragment.PREFERENCE_FRAGMENT";
  private final RootViewOnKeyListener mRootViewOnKeyListener = new RootViewOnKeyListener(null);
  
  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    return paramLayoutInflater.inflate(R.layout.leanback_settings_fragment, paramViewGroup, false);
  }
  
  public void onPause()
  {
    super.onPause();
    LeanbackSettingsRootView localLeanbackSettingsRootView = (LeanbackSettingsRootView)getView();
    if (localLeanbackSettingsRootView != null) {
      localLeanbackSettingsRootView.setOnBackKeyListener(null);
    }
  }
  
  public boolean onPreferenceDisplayDialog(@NonNull PreferenceFragment paramPreferenceFragment, Preference paramPreference)
  {
    boolean bool = false;
    if (paramPreferenceFragment == null) {
      throw new IllegalArgumentException("Cannot display dialog for preference " + paramPreference + ", Caller must not be null!");
    }
    if ((paramPreference instanceof ListPreference))
    {
      paramPreference = LeanbackListPreferenceDialogFragment.newInstanceSingle(((ListPreference)paramPreference).getKey());
      paramPreference.setTargetFragment(paramPreferenceFragment, 0);
      startPreferenceFragment(paramPreference);
    }
    for (;;)
    {
      bool = true;
      do
      {
        return bool;
      } while (!(paramPreference instanceof MultiSelectListPreference));
      paramPreference = LeanbackListPreferenceDialogFragment.newInstanceMulti(((MultiSelectListPreference)paramPreference).getKey());
      paramPreference.setTargetFragment(paramPreferenceFragment, 0);
      startPreferenceFragment(paramPreference);
    }
  }
  
  public abstract void onPreferenceStartInitialScreen();
  
  public void onResume()
  {
    super.onResume();
    LeanbackSettingsRootView localLeanbackSettingsRootView = (LeanbackSettingsRootView)getView();
    if (localLeanbackSettingsRootView != null) {
      localLeanbackSettingsRootView.setOnBackKeyListener(this.mRootViewOnKeyListener);
    }
  }
  
  public void onViewCreated(View paramView, Bundle paramBundle)
  {
    super.onViewCreated(paramView, paramBundle);
    if (paramBundle == null) {
      onPreferenceStartInitialScreen();
    }
  }
  
  public void startImmersiveFragment(@NonNull Fragment paramFragment)
  {
    FragmentTransaction localFragmentTransaction = getChildFragmentManager().beginTransaction();
    Fragment localFragment = getChildFragmentManager().findFragmentByTag("android.support.v17.preference.LeanbackSettingsFragment.PREFERENCE_FRAGMENT");
    if ((localFragment != null) && (!localFragment.isHidden()))
    {
      if (Build.VERSION.SDK_INT < 23) {
        localFragmentTransaction.add(R.id.settings_preference_fragment_container, new DummyFragment());
      }
      localFragmentTransaction.remove(localFragment);
    }
    localFragmentTransaction.add(R.id.settings_dialog_container, paramFragment).addToBackStack(null).commit();
  }
  
  public void startPreferenceFragment(@NonNull Fragment paramFragment)
  {
    FragmentTransaction localFragmentTransaction = getChildFragmentManager().beginTransaction();
    if (getChildFragmentManager().findFragmentByTag("android.support.v17.preference.LeanbackSettingsFragment.PREFERENCE_FRAGMENT") != null) {
      localFragmentTransaction.addToBackStack(null).replace(R.id.settings_preference_fragment_container, paramFragment, "android.support.v17.preference.LeanbackSettingsFragment.PREFERENCE_FRAGMENT");
    }
    for (;;)
    {
      localFragmentTransaction.commit();
      return;
      localFragmentTransaction.add(R.id.settings_preference_fragment_container, paramFragment, "android.support.v17.preference.LeanbackSettingsFragment.PREFERENCE_FRAGMENT");
    }
  }
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  public static class DummyFragment
    extends Fragment
  {
    @Nullable
    public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
    {
      paramLayoutInflater = new Space(paramLayoutInflater.getContext());
      paramLayoutInflater.setVisibility(8);
      return paramLayoutInflater;
    }
  }
  
  private class RootViewOnKeyListener
    implements View.OnKeyListener
  {
    private RootViewOnKeyListener() {}
    
    public boolean onKey(View paramView, int paramInt, KeyEvent paramKeyEvent)
    {
      if (paramInt == 4) {
        return LeanbackSettingsFragment.this.getChildFragmentManager().popBackStackImmediate();
      }
      return false;
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/preference/LeanbackSettingsFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */