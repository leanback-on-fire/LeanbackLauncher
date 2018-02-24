package android.support.v14.preference;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.annotation.ArrayRes;
import android.support.annotation.NonNull;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v7.preference.Preference.BaseSavedState;
import android.support.v7.preference.R.attr;
import android.support.v7.preference.R.styleable;
import android.support.v7.preference.internal.AbstractMultiSelectListPreference;
import android.util.AttributeSet;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class MultiSelectListPreference
  extends AbstractMultiSelectListPreference
{
  private CharSequence[] mEntries;
  private CharSequence[] mEntryValues;
  private Set<String> mValues = new HashSet();
  
  public MultiSelectListPreference(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public MultiSelectListPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, TypedArrayUtils.getAttr(paramContext, R.attr.dialogPreferenceStyle, 16842897));
  }
  
  public MultiSelectListPreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public MultiSelectListPreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.MultiSelectListPreference, paramInt1, paramInt2);
    this.mEntries = TypedArrayUtils.getTextArray(paramContext, R.styleable.MultiSelectListPreference_entries, R.styleable.MultiSelectListPreference_android_entries);
    this.mEntryValues = TypedArrayUtils.getTextArray(paramContext, R.styleable.MultiSelectListPreference_entryValues, R.styleable.MultiSelectListPreference_android_entryValues);
    paramContext.recycle();
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
  
  public CharSequence[] getEntryValues()
  {
    return this.mEntryValues;
  }
  
  protected boolean[] getSelectedItems()
  {
    CharSequence[] arrayOfCharSequence = this.mEntryValues;
    int j = arrayOfCharSequence.length;
    Set localSet = this.mValues;
    boolean[] arrayOfBoolean = new boolean[j];
    int i = 0;
    while (i < j)
    {
      arrayOfBoolean[i] = localSet.contains(arrayOfCharSequence[i].toString());
      i += 1;
    }
    return arrayOfBoolean;
  }
  
  public Set<String> getValues()
  {
    return this.mValues;
  }
  
  protected Object onGetDefaultValue(TypedArray paramTypedArray, int paramInt)
  {
    paramTypedArray = paramTypedArray.getTextArray(paramInt);
    HashSet localHashSet = new HashSet();
    int i = paramTypedArray.length;
    paramInt = 0;
    while (paramInt < i)
    {
      localHashSet.add(paramTypedArray[paramInt].toString());
      paramInt += 1;
    }
    return localHashSet;
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
    setValues(paramParcelable.values);
  }
  
  protected Parcelable onSaveInstanceState()
  {
    Object localObject = super.onSaveInstanceState();
    if (isPersistent()) {
      return (Parcelable)localObject;
    }
    localObject = new SavedState((Parcelable)localObject);
    ((SavedState)localObject).values = getValues();
    return (Parcelable)localObject;
  }
  
  protected void onSetInitialValue(boolean paramBoolean, Object paramObject)
  {
    if (paramBoolean) {}
    for (paramObject = getPersistedStringSet(this.mValues);; paramObject = (Set)paramObject)
    {
      setValues((Set)paramObject);
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
  
  public void setValues(Set<String> paramSet)
  {
    this.mValues.clear();
    this.mValues.addAll(paramSet);
    persistStringSet(paramSet);
  }
  
  private static class SavedState
    extends Preference.BaseSavedState
  {
    public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator()
    {
      public MultiSelectListPreference.SavedState createFromParcel(Parcel paramAnonymousParcel)
      {
        return new MultiSelectListPreference.SavedState(paramAnonymousParcel);
      }
      
      public MultiSelectListPreference.SavedState[] newArray(int paramAnonymousInt)
      {
        return new MultiSelectListPreference.SavedState[paramAnonymousInt];
      }
    };
    Set<String> values;
    
    public SavedState(Parcel paramParcel)
    {
      super();
      int i = paramParcel.readInt();
      this.values = new HashSet();
      String[] arrayOfString = new String[i];
      paramParcel.readStringArray(arrayOfString);
      Collections.addAll(this.values, arrayOfString);
    }
    
    public SavedState(Parcelable paramParcelable)
    {
      super();
    }
    
    public void writeToParcel(@NonNull Parcel paramParcel, int paramInt)
    {
      super.writeToParcel(paramParcel, paramInt);
      paramParcel.writeInt(this.values.size());
      paramParcel.writeStringArray((String[])this.values.toArray(new String[this.values.size()]));
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v14/preference/MultiSelectListPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */