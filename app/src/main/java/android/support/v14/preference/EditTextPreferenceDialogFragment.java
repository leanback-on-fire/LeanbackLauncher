package android.support.v14.preference;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import android.support.v7.preference.EditTextPreference;
import android.view.View;
import android.widget.EditText;

public class EditTextPreferenceDialogFragment
  extends PreferenceDialogFragment
{
  private static final String SAVE_STATE_TEXT = "EditTextPreferenceDialogFragment.text";
  private EditText mEditText;
  private CharSequence mText;
  
  private EditTextPreference getEditTextPreference()
  {
    return (EditTextPreference)getPreference();
  }
  
  public static EditTextPreferenceDialogFragment newInstance(String paramString)
  {
    EditTextPreferenceDialogFragment localEditTextPreferenceDialogFragment = new EditTextPreferenceDialogFragment();
    Bundle localBundle = new Bundle(1);
    localBundle.putString("key", paramString);
    localEditTextPreferenceDialogFragment.setArguments(localBundle);
    return localEditTextPreferenceDialogFragment;
  }
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  protected boolean needInputMethod()
  {
    return true;
  }
  
  protected void onBindDialogView(View paramView)
  {
    super.onBindDialogView(paramView);
    this.mEditText = ((EditText)paramView.findViewById(16908291));
    if (this.mEditText == null) {
      throw new IllegalStateException("Dialog view must contain an EditText with id @android:id/edit");
    }
    this.mEditText.setText(this.mText);
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    if (paramBundle == null)
    {
      this.mText = getEditTextPreference().getText();
      return;
    }
    this.mText = paramBundle.getCharSequence("EditTextPreferenceDialogFragment.text");
  }
  
  public void onDialogClosed(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      String str = this.mEditText.getText().toString();
      if (getEditTextPreference().callChangeListener(str)) {
        getEditTextPreference().setText(str);
      }
    }
  }
  
  public void onSaveInstanceState(@NonNull Bundle paramBundle)
  {
    super.onSaveInstanceState(paramBundle);
    paramBundle.putCharSequence("EditTextPreferenceDialogFragment.text", this.mText);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v14/preference/EditTextPreferenceDialogFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */