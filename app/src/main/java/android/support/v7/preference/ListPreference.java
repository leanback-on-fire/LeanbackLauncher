package android.support.v7.preference;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.annotation.ArrayRes;
import android.support.annotation.NonNull;
import android.support.v4.content.res.TypedArrayUtils;
import android.text.TextUtils;
import android.util.AttributeSet;

public class ListPreference
  extends DialogPreference
{
  private CharSequence[] mEntries;
  private CharSequence[] mEntryValues;
  private String mSummary;
  private String mValue;
  private boolean mValueSet;
  
  public ListPreference(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public ListPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, TypedArrayUtils.getAttr(paramContext, R.attr.dialogPreferenceStyle, 16842897));
  }
  
  public ListPreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public ListPreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.ListPreference, paramInt1, paramInt2);
    this.mEntries = TypedArrayUtils.getTextArray(localTypedArray, R.styleable.ListPreference_entries, R.styleable.ListPreference_android_entries);
    this.mEntryValues = TypedArrayUtils.getTextArray(localTypedArray, R.styleable.ListPreference_entryValues, R.styleable.ListPreference_android_entryValues);
    localTypedArray.recycle();
    paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.Preference, paramInt1, paramInt2);
    this.mSummary = TypedArrayUtils.getString(paramContext, R.styleable.Preference_summary, R.styleable.Preference_android_summary);
    paramContext.recycle();
  }
  
  private int getValueIndex()
  {
    return findIndexOfValue(this.mValue);
  }
  
  public int findIndexOfValue(String paramString)
  {
    if ((paramString != null) && (this.mEntryValues != null))
    {
      int i = this.mEntryValues.length - 1;
      while (i >= 0)
      {
        if (this.mEntryValues[i].equals(paramString)) {
          return i;
        }
        i -= 1;
      }
    }
    return -1;
  }
  
  public CharSequence[] getEntries()
  {
    return this.mEntries;
  }
  
  public CharSequence getEntry()
  {
    int i = getValueIndex();
    if ((i >= 0) && (this.mEntries != null)) {
      return this.mEntries[i];
    }
    return null;
  }
  
  public CharSequence[] getEntryValues()
  {
    return this.mEntryValues;
  }
  
  public CharSequence getSummary()
  {
    CharSequence localCharSequence = getEntry();
    if (this.mSummary == null) {
      return super.getSummary();
    }
    String str = this.mSummary;
    Object localObject = localCharSequence;
    if (localCharSequence == null) {
      localObject = "";
    }
    return String.format(str, new Object[] { localObject });
  }
  
  public String getValue()
  {
    return this.mValue;
  }
  
  protected Object onGetDefaultValue(TypedArray paramTypedArray, int paramInt)
  {
    return paramTypedArray.getString(paramInt);
  }
  
  protected void onRestoreInstanceState(Parcelable paramParcelable)
  {
    if ((paramParcelable == null) || (!paramParcelable.getClass().equals(SavedState.class)))
    {
      super.onRestoreInstanceState(paramParcelable);
      return;
    }
    paramParcelable = (SavedState)paramParcelable;
    super.onRestoreInstanceState(paramParcelable.getSuperState());
    setValue(paramParcelable.value);
  }
  
  protected Parcelable onSaveInstanceState()
  {
    Object localObject = super.onSaveInstanceState();
    if (isPersistent()) {
      return (Parcelable)localObject;
    }
    localObject = new SavedState((Parcelable)localObject);
    ((SavedState)localObject).value = getValue();
    return (Parcelable)localObject;
  }
  
  protected void onSetInitialValue(boolean paramBoolean, Object paramObject)
  {
    if (paramBoolean) {}
    for (paramObject = getPersistedString(this.mValue);; paramObject = (String)paramObject)
    {
      setValue((String)paramObject);
      return;
    }
  }
  
  public void setEntries(@ArrayRes int paramInt)
  {
    setEntries(getContext().getResources().getTextArray(paramInt));
  }
  
  public void setEntries(CharSequence[] paramArrayOfCharSequence)
  {
    this.mEntries = paramArrayOfCharSequence;
  }
  
  public void setEntryValues(@ArrayRes int paramInt)
  {
    setEntryValues(getContext().getResources().getTextArray(paramInt));
  }
  
  public void setEntryValues(CharSequence[] paramArrayOfCharSequence)
  {
    this.mEntryValues = paramArrayOfCharSequence;
  }
  
  public void setSummary(CharSequence paramCharSequence)
  {
    super.setSummary(paramCharSequence);
    if ((paramCharSequence == null) && (this.mSummary != null)) {
      this.mSummary = null;
    }
    while ((paramCharSequence == null) || (paramCharSequence.equals(this.mSummary))) {
      return;
    }
    this.mSummary = paramCharSequence.toString();
  }
  
  public void setValue(String paramString)
  {
    if (!TextUtils.equals(this.mValue, paramString)) {}
    for (int i = 1;; i = 0)
    {
      if ((i != 0) || (!this.mValueSet))
      {
        this.mValue = paramString;
        this.mValueSet = true;
        persistString(paramString);
        if (i != 0) {
          notifyChanged();
        }
      }
      return;
    }
  }
  
  public void setValueIndex(int paramInt)
  {
    if (this.mEntryValues != null) {
      setValue(this.mEntryValues[paramInt].toString());
    }
  }
  
  private static class SavedState
    extends Preference.BaseSavedState
  {
    public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator()
    {
      public ListPreference.SavedState createFromParcel(Parcel paramAnonymousParcel)
      {
        return new ListPreference.SavedState(paramAnonymousParcel);
      }
      
      public ListPreference.SavedState[] newArray(int paramAnonymousInt)
      {
        return new ListPreference.SavedState[paramAnonymousInt];
      }
    };
    String value;
    
    public SavedState(Parcel paramParcel)
    {
      super();
      this.value = paramParcel.readString();
    }
    
    public SavedState(Parcelable paramParcelable)
    {
      super();
    }
    
    public void writeToParcel(@NonNull Parcel paramParcel, int paramInt)
    {
      super.writeToParcel(paramParcel, paramInt);
      paramParcel.writeString(this.value);
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v7/preference/ListPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */