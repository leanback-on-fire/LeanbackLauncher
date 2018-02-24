package android.support.v7.preference;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog.Builder;
import java.util.ArrayList;

public class ListPreferenceDialogFragmentCompat
  extends PreferenceDialogFragmentCompat
{
  private static final String SAVE_STATE_ENTRIES = "ListPreferenceDialogFragment.entries";
  private static final String SAVE_STATE_ENTRY_VALUES = "ListPreferenceDialogFragment.entryValues";
  private static final String SAVE_STATE_INDEX = "ListPreferenceDialogFragment.index";
  private int mClickedDialogEntryIndex;
  private CharSequence[] mEntries;
  private CharSequence[] mEntryValues;
  
  private static CharSequence[] getCharSequenceArray(Bundle paramBundle, String paramString)
  {
    paramBundle = paramBundle.getStringArrayList(paramString);
    if (paramBundle == null) {
      return null;
    }
    return (CharSequence[])paramBundle.toArray(new CharSequence[paramBundle.size()]);
  }
  
  private ListPreference getListPreference()
  {
    return (ListPreference)getPreference();
  }
  
  public static ListPreferenceDialogFragmentCompat newInstance(String paramString)
  {
    ListPreferenceDialogFragmentCompat localListPreferenceDialogFragmentCompat = new ListPreferenceDialogFragmentCompat();
    Bundle localBundle = new Bundle(1);
    localBundle.putString("key", paramString);
    localListPreferenceDialogFragmentCompat.setArguments(localBundle);
    return localListPreferenceDialogFragmentCompat;
  }
  
  private static void putCharSequenceArray(Bundle paramBundle, String paramString, CharSequence[] paramArrayOfCharSequence)
  {
    ArrayList localArrayList = new ArrayList(paramArrayOfCharSequence.length);
    int j = paramArrayOfCharSequence.length;
    int i = 0;
    while (i < j)
    {
      localArrayList.add(paramArrayOfCharSequence[i].toString());
      i += 1;
    }
    paramBundle.putStringArrayList(paramString, localArrayList);
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
    this.mEntries = getCharSequenceArray(paramBundle, "ListPreferenceDialogFragment.entries");
    this.mEntryValues = getCharSequenceArray(paramBundle, "ListPreferenceDialogFragment.entryValues");
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
        ListPreferenceDialogFragmentCompat.access$002(ListPreferenceDialogFragmentCompat.this, paramAnonymousInt);
        ListPreferenceDialogFragmentCompat.this.onClick(paramAnonymousDialogInterface, -1);
        paramAnonymousDialogInterface.dismiss();
      }
    });
    paramBuilder.setPositiveButton(null, null);
  }
  
  public void onSaveInstanceState(@NonNull Bundle paramBundle)
  {
    super.onSaveInstanceState(paramBundle);
    paramBundle.putInt("ListPreferenceDialogFragment.index", this.mClickedDialogEntryIndex);
    putCharSequenceArray(paramBundle, "ListPreferenceDialogFragment.entries", this.mEntries);
    putCharSequenceArray(paramBundle, "ListPreferenceDialogFragment.entryValues", this.mEntryValues);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v7/preference/ListPreferenceDialogFragmentCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */