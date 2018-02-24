package android.support.v7.preference;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class DropDownPreference
  extends ListPreference
{
  private final ArrayAdapter mAdapter;
  private final Context mContext;
  private final AdapterView.OnItemSelectedListener mItemSelectedListener = new AdapterView.OnItemSelectedListener()
  {
    public void onItemSelected(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
    {
      if (paramAnonymousInt >= 0)
      {
        paramAnonymousAdapterView = DropDownPreference.this.getEntryValues()[paramAnonymousInt].toString();
        if ((!paramAnonymousAdapterView.equals(DropDownPreference.this.getValue())) && (DropDownPreference.this.callChangeListener(paramAnonymousAdapterView))) {
          DropDownPreference.this.setValue(paramAnonymousAdapterView);
        }
      }
    }
    
    public void onNothingSelected(AdapterView<?> paramAnonymousAdapterView) {}
  };
  private Spinner mSpinner;
  
  public DropDownPreference(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public DropDownPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, R.attr.dropdownPreferenceStyle);
  }
  
  public DropDownPreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public DropDownPreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    this.mContext = paramContext;
    this.mAdapter = createAdapter();
    updateEntries();
  }
  
  private void updateEntries()
  {
    this.mAdapter.clear();
    if (getEntries() != null)
    {
      CharSequence[] arrayOfCharSequence = getEntries();
      int j = arrayOfCharSequence.length;
      int i = 0;
      while (i < j)
      {
        CharSequence localCharSequence = arrayOfCharSequence[i];
        this.mAdapter.add(localCharSequence.toString());
        i += 1;
      }
    }
  }
  
  protected ArrayAdapter createAdapter()
  {
    return new ArrayAdapter(this.mContext, 17367049);
  }
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  public int findSpinnerIndexOfValue(String paramString)
  {
    CharSequence[] arrayOfCharSequence = getEntryValues();
    if ((paramString != null) && (arrayOfCharSequence != null))
    {
      int i = arrayOfCharSequence.length - 1;
      while (i >= 0)
      {
        if (arrayOfCharSequence[i].equals(paramString)) {
          return i;
        }
        i -= 1;
      }
    }
    return -1;
  }
  
  protected void notifyChanged()
  {
    super.notifyChanged();
    this.mAdapter.notifyDataSetChanged();
  }
  
  public void onBindViewHolder(PreferenceViewHolder paramPreferenceViewHolder)
  {
    this.mSpinner = ((Spinner)paramPreferenceViewHolder.itemView.findViewById(R.id.spinner));
    this.mSpinner.setAdapter(this.mAdapter);
    this.mSpinner.setOnItemSelectedListener(this.mItemSelectedListener);
    this.mSpinner.setSelection(findSpinnerIndexOfValue(getValue()));
    super.onBindViewHolder(paramPreferenceViewHolder);
  }
  
  protected void onClick()
  {
    this.mSpinner.performClick();
  }
  
  public void setEntries(@NonNull CharSequence[] paramArrayOfCharSequence)
  {
    super.setEntries(paramArrayOfCharSequence);
    updateEntries();
  }
  
  public void setValueIndex(int paramInt)
  {
    setValue(getEntryValues()[paramInt].toString());
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v7/preference/DropDownPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */