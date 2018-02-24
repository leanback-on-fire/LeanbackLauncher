package android.support.v7.preference;

import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog.Builder;
import android.support.v7.preference.internal.AbstractMultiSelectListPreference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MultiSelectListPreferenceDialogFragmentCompat
  extends PreferenceDialogFragmentCompat
{
  private static final String SAVE_STATE_CHANGED = "MultiSelectListPreferenceDialogFragmentCompat.changed";
  private static final String SAVE_STATE_ENTRIES = "MultiSelectListPreferenceDialogFragmentCompat.entries";
  private static final String SAVE_STATE_ENTRY_VALUES = "MultiSelectListPreferenceDialogFragmentCompat.entryValues";
  private static final String SAVE_STATE_VALUES = "MultiSelectListPreferenceDialogFragmentCompat.values";
  private CharSequence[] mEntries;
  private CharSequence[] mEntryValues;
  private Set<String> mNewValues = new HashSet();
  private boolean mPreferenceChanged;
  
  private AbstractMultiSelectListPreference getListPreference()
  {
    return (AbstractMultiSelectListPreference)getPreference();
  }
  
  public static MultiSelectListPreferenceDialogFragmentCompat newInstance(String paramString)
  {
    MultiSelectListPreferenceDialogFragmentCompat localMultiSelectListPreferenceDialogFragmentCompat = new MultiSelectListPreferenceDialogFragmentCompat();
    Bundle localBundle = new Bundle(1);
    localBundle.putString("key", paramString);
    localMultiSelectListPreferenceDialogFragmentCompat.setArguments(localBundle);
    return localMultiSelectListPreferenceDialogFragmentCompat;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    if (paramBundle == null)
    {
      paramBundle = getListPreference();
      if ((paramBundle.getEntries() == null) || (paramBundle.getEntryValues() == null)) {
        throw new IllegalStateException("MultiSelectListPreference requires an entries array and an entryValues array.");
      }
      this.mNewValues.clear();
      this.mNewValues.addAll(paramBundle.getValues());
      this.mPreferenceChanged = false;
      this.mEntries = paramBundle.getEntries();
      this.mEntryValues = paramBundle.getEntryValues();
      return;
    }
    this.mNewValues.clear();
    this.mNewValues.addAll(paramBundle.getStringArrayList("MultiSelectListPreferenceDialogFragmentCompat.values"));
    this.mPreferenceChanged = paramBundle.getBoolean("MultiSelectListPreferenceDialogFragmentCompat.changed", false);
    this.mEntries = paramBundle.getCharSequenceArray("MultiSelectListPreferenceDialogFragmentCompat.entries");
    this.mEntryValues = paramBundle.getCharSequenceArray("MultiSelectListPreferenceDialogFragmentCompat.entryValues");
  }
  
  public void onDialogClosed(boolean paramBoolean)
  {
    AbstractMultiSelectListPreference localAbstractMultiSelectListPreference = getListPreference();
    if ((paramBoolean) && (this.mPreferenceChanged))
    {
      Set localSet = this.mNewValues;
      if (localAbstractMultiSelectListPreference.callChangeListener(localSet)) {
        localAbstractMultiSelectListPreference.setValues(localSet);
      }
    }
    this.mPreferenceChanged = false;
  }
  
  protected void onPrepareDialogBuilder(AlertDialog.Builder paramBuilder)
  {
    super.onPrepareDialogBuilder(paramBuilder);
    int j = this.mEntryValues.length;
    boolean[] arrayOfBoolean = new boolean[j];
    int i = 0;
    while (i < j)
    {
      arrayOfBoolean[i] = this.mNewValues.contains(this.mEntryValues[i].toString());
      i += 1;
    }
    paramBuilder.setMultiChoiceItems(this.mEntries, arrayOfBoolean, new DialogInterface.OnMultiChoiceClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt, boolean paramAnonymousBoolean)
      {
        if (paramAnonymousBoolean)
        {
          MultiSelectListPreferenceDialogFragmentCompat.access$002(MultiSelectListPreferenceDialogFragmentCompat.this, MultiSelectListPreferenceDialogFragmentCompat.this.mPreferenceChanged | MultiSelectListPreferenceDialogFragmentCompat.this.mNewValues.add(MultiSelectListPreferenceDialogFragmentCompat.this.mEntryValues[paramAnonymousInt].toString()));
          return;
        }
        MultiSelectListPreferenceDialogFragmentCompat.access$002(MultiSelectListPreferenceDialogFragmentCompat.this, MultiSelectListPreferenceDialogFragmentCompat.this.mPreferenceChanged | MultiSelectListPreferenceDialogFragmentCompat.this.mNewValues.remove(MultiSelectListPreferenceDialogFragmentCompat.this.mEntryValues[paramAnonymousInt].toString()));
      }
    });
  }
  
  public void onSaveInstanceState(@NonNull Bundle paramBundle)
  {
    super.onSaveInstanceState(paramBundle);
    paramBundle.putStringArrayList("MultiSelectListPreferenceDialogFragmentCompat.values", new ArrayList(this.mNewValues));
    paramBundle.putBoolean("MultiSelectListPreferenceDialogFragmentCompat.changed", this.mPreferenceChanged);
    paramBundle.putCharSequenceArray("MultiSelectListPreferenceDialogFragmentCompat.entries", this.mEntries);
    paramBundle.putCharSequenceArray("MultiSelectListPreferenceDialogFragmentCompat.entryValues", this.mEntryValues);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v7/preference/MultiSelectListPreferenceDialogFragmentCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */