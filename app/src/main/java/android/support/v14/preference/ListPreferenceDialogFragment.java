package android.support.v14.preference;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.preference.ListPreference;

public class ListPreferenceDialogFragment
  extends PreferenceDialogFragment
{
  private static final String SAVE_STATE_ENTRIES = "ListPreferenceDialogFragment.entries";
  private static final String SAVE_STATE_ENTRY_VALUES = "ListPreferenceDialogFragment.entryValues";
  private static final String SAVE_STATE_INDEX = "ListPreferenceDialogFragment.index";
  private int mClickedDialogEntryIndex;
  private CharSequence[] mEntries;
  private CharSequence[] mEntryValues;
  
  private ListPreference getListPreference()
  {
    return (ListPreference)getPreference();
  }
  
  public static ListPreferenceDialogFragment newInstance(String paramString)
  {
    ListPreferenceDialogFragment localListPreferenceDialogFragment = new ListPreferenceDialogFragment();
    Bundle localBundle = new Bundle(1);
    localBundle.putString("key", paramString);
    localListPreferenceDialogFragment.setArguments(localBundle);
    return localListPreferenceDialogFragment;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    if (paramBundle == null)
    {
      paramBundle = getListPreference();
      if ((paramBundle.getEntries() == null) || (paramBundle.getEntryValues() == null)) {
        throw new IllegalStateException("ListPreference requires an entries array and an entryValues array.");
      }
      this.mClickedDialogEntryIndex = paramBundle.findIndexOfValue(paramBundle.getValue());
      this.mEntries = paramBundle.getEntries();
      this.mEntryValues = paramBundle.getEntryValues();
      return;
    }
    this.mClickedDialogEntryIndex = paramBundle.getInt("ListPreferenceDialogFragment.index", 0);
    this.mEntries = paramBundle.getCharSequenceArray("ListPreferenceDialogFragment.entries");
    this.mEntryValues = paramBundle.getCharSequenceArray("ListPreferenceDialogFragment.entryValues");
  }
  
  public void onDialogClosed(boolean paramBoolean)
  {
    ListPreference localListPreference = getListPreference();
    if ((paramBoolean) && (this.mClickedDialogEntryIndex >= 0))
    {
      String str = this.mEntryValues[this.mClickedDialogEntryIndex].toString();
      if (localListPreference.callChangeListener(str)) {
        localListPreference.setValue(str);
      }
    }
  }
  
  protected void onPrepareDialogBuilder(AlertDialog.Builder paramBuilder)
  {
    super.onPrepareDialogBuilder(paramBuilder);
    paramBuilder.setSingleChoiceItems(this.mEntries, this.mClickedDialogEntryIndex, new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        ListPreferenceDialogFragment.access$002(ListPreferenceDialogFragment.this, paramAnonymousInt);
        ListPreferenceDialogFragment.this.onClick(paramAnonymousDialogInterface, -1);
        paramAnonymousDialogInterface.dismiss();
      }
    });
    paramBuilder.setPositiveButton(null, null);
  }
  
  public void onSaveInstanceState(@NonNull Bundle paramBundle)
  {
    super.onSaveInstanceState(paramBundle);
    paramBundle.putInt("ListPreferenceDialogFragment.index", this.mClickedDialogEntryIndex);
    paramBundle.putCharSequenceArray("ListPreferenceDialogFragment.entries", this.mEntries);
    paramBundle.putCharSequenceArray("ListPreferenceDialogFragment.entryValues", this.mEntryValues);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v14/preference/ListPreferenceDialogFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */