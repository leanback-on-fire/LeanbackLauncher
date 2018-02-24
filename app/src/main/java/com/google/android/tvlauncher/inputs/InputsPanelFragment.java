package com.google.android.tvlauncher.inputs;

import android.os.Bundle;
import android.support.v17.preference.LeanbackPreferenceFragment;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceClickListener;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.preference.PreferenceScreen;
import com.google.android.tvlauncher.analytics.FragmentEventLogger;
import com.google.android.tvlauncher.analytics.UserActionEvent;
import com.google.android.tvlauncher.util.Partner;

public class InputsPanelFragment
  extends LeanbackPreferenceFragment
  implements InputsManager.OnChangedListener, Preference.OnPreferenceClickListener
{
  private FragmentEventLogger mEventLogger;
  private InputsManager mInputsManager;
  
  public static InputsPanelFragment newInstance()
  {
    return new InputsPanelFragment();
  }
  
  private void setPreferences(PreferenceScreen paramPreferenceScreen)
  {
    paramPreferenceScreen.removeAll();
    Object localObject = Partner.get(getContext());
    localObject = new InputsManager.Configuration(((Partner)localObject).showPhysicalTunersSeparately(), ((Partner)localObject).disableDisconnectedInputs(), ((Partner)localObject).getStateIconFromTVInput());
    this.mInputsManager = new InputsManager(getContext(), (InputsManager.Configuration)localObject);
    int i = 0;
    while (i < this.mInputsManager.getItemCount())
    {
      localObject = new InputPreference(getPreferenceManager().getContext(), this.mInputsManager.getInputState(i));
      ((InputPreference)localObject).setIcon(this.mInputsManager.getItemDrawable(i));
      ((InputPreference)localObject).setTitle(this.mInputsManager.getLabel(i));
      ((InputPreference)localObject).setKey(Integer.toString(i));
      ((InputPreference)localObject).setOnPreferenceClickListener(this);
      paramPreferenceScreen.addPreference((Preference)localObject);
      i += 1;
    }
  }
  
  public void onCreatePreferences(Bundle paramBundle, String paramString)
  {
    paramBundle = getPreferenceManager().getContext();
    paramBundle = getPreferenceManager().createPreferenceScreen(paramBundle);
    paramBundle.setTitle(2131493000);
    setPreferences(paramBundle);
    setPreferenceScreen(paramBundle);
    this.mInputsManager.setOnChangedListener(this);
    this.mEventLogger = new FragmentEventLogger(this);
  }
  
  public void onDestroy()
  {
    super.onDestroy();
    this.mInputsManager.setOnChangedListener(null);
  }
  
  public void onInputsChanged()
  {
    setPreferenceScreen(getPreferenceScreen());
  }
  
  public boolean onPreferenceClick(Preference paramPreference)
  {
    String str = paramPreference.getKey();
    if (str != null)
    {
      int i = Integer.valueOf(str).intValue();
      this.mInputsManager.launchInputActivity(i);
      i = this.mInputsManager.getInputType(i);
      if (i != -1) {
        this.mEventLogger.log(new UserActionEvent("select_input").putParameter("input_type", i));
      }
      return true;
    }
    return super.onPreferenceTreeClick(paramPreference);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/inputs/InputsPanelFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */