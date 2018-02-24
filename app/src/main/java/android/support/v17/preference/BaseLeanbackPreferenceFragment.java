package android.support.v17.preference;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.RestrictTo;
import android.support.v14.preference.PreferenceFragment;
import android.support.v17.leanback.widget.VerticalGridView;
import android.support.v7.preference.PreferenceRecyclerViewAccessibilityDelegate;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

public abstract class BaseLeanbackPreferenceFragment
  extends PreferenceFragment
{
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  public Fragment getCallbackFragment()
  {
    return getParentFragment();
  }
  
  public RecyclerView onCreateRecyclerView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    paramLayoutInflater = (VerticalGridView)paramLayoutInflater.inflate(R.layout.leanback_preferences_list, paramViewGroup, false);
    paramLayoutInflater.setWindowAlignment(3);
    paramLayoutInflater.setFocusScrollStrategy(0);
    paramLayoutInflater.setAccessibilityDelegateCompat(new PreferenceRecyclerViewAccessibilityDelegate(paramLayoutInflater));
    return paramLayoutInflater;
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/preference/BaseLeanbackPreferenceFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */