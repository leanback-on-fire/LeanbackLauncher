package android.support.v17.preference;

import android.app.Fragment;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.v7.preference.DialogPreference;
import android.support.v7.preference.DialogPreference.TargetFragment;

public class LeanbackPreferenceDialogFragment
  extends Fragment
{
  public static final String ARG_KEY = "key";
  private DialogPreference mPreference;
  
  public LeanbackPreferenceDialogFragment()
  {
    if (Build.VERSION.SDK_INT >= 21) {
      LeanbackPreferenceFragmentTransitionHelperApi21.addTransitions(this);
    }
  }
  
  public DialogPreference getPreference()
  {
    if (this.mPreference == null)
    {
      String str = getArguments().getString("key");
      this.mPreference = ((DialogPreference)((DialogPreference.TargetFragment)getTargetFragment()).findPreference(str));
    }
    return this.mPreference;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    paramBundle = getTargetFragment();
    if (!(paramBundle instanceof DialogPreference.TargetFragment)) {
      throw new IllegalStateException("Target fragment " + paramBundle + " must implement TargetFragment interface");
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/preference/LeanbackPreferenceDialogFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */