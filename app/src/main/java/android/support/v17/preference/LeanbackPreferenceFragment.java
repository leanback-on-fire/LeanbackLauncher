package android.support.v17.preference;

import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.v7.preference.PreferenceScreen;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public abstract class LeanbackPreferenceFragment
  extends BaseLeanbackPreferenceFragment
{
  public LeanbackPreferenceFragment()
  {
    if (Build.VERSION.SDK_INT >= 21) {
      LeanbackPreferenceFragmentTransitionHelperApi21.addTransitions(this);
    }
  }
  
  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    paramViewGroup = paramLayoutInflater.inflate(R.layout.leanback_preference_fragment, paramViewGroup, false);
    ViewGroup localViewGroup = (ViewGroup)paramViewGroup.findViewById(R.id.main_frame);
    paramLayoutInflater = super.onCreateView(paramLayoutInflater, localViewGroup, paramBundle);
    if (paramLayoutInflater != null) {
      localViewGroup.addView(paramLayoutInflater);
    }
    return paramViewGroup;
  }
  
  public void onViewCreated(View paramView, Bundle paramBundle)
  {
    super.onViewCreated(paramView, paramBundle);
    setTitle(getPreferenceScreen().getTitle());
  }
  
  public void setTitle(CharSequence paramCharSequence)
  {
    Object localObject = getView();
    if (localObject == null) {}
    for (localObject = null;; localObject = (TextView)((View)localObject).findViewById(R.id.decor_title))
    {
      if (localObject != null) {
        ((TextView)localObject).setText(paramCharSequence);
      }
      return;
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/preference/LeanbackPreferenceFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */