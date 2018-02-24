package android.support.v7.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.annotation.RestrictTo;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

public abstract class TwoStatePreference
  extends Preference
{
  protected boolean mChecked;
  private boolean mCheckedSet;
  private boolean mDisableDependentsState;
  private CharSequence mSummaryOff;
  private CharSequence mSummaryOn;
  
  public TwoStatePreference(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public TwoStatePreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public TwoStatePreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public TwoStatePreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
  }
  
  public boolean getDisableDependentsState()
  {
    return this.mDisableDependentsState;
  }
  
  public CharSequence getSummaryOff()
  {
    return this.mSummaryOff;
  }
  
  public CharSequence getSummaryOn()
  {
    return this.mSummaryOn;
  }
  
  public boolean isChecked()
  {
    return this.mChecked;
  }
  
  protected void onClick()
  {
    super.onClick();
    if (!isChecked()) {}
    for (boolean bool = true;; bool = false)
    {
      if (callChangeListener(Boolean.valueOf(bool))) {
        setChecked(bool);
      }
      return;
    }
  }
  
  protected Object onGetDefaultValue(TypedArray paramTypedArray, int paramInt)
  {
    return Boolean.valueOf(paramTypedArray.getBoolean(paramInt, false));
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
    setChecked(paramParcelable.checked);
  }
  
  protected Parcelable onSaveInstanceState()
  {
    Object localObject = super.onSaveInstanceState();
    if (isPersistent()) {
      return (Parcelable)localObject;
    }
    localObject = new SavedState((Parcelable)localObject);
    ((SavedState)localObject).checked = isChecked();
    return (Parcelable)localObject;
  }
  
  protected void onSetInitialValue(boolean paramBoolean, Object paramObject)
  {
    if (paramBoolean) {}
    for (paramBoolean = getPersistedBoolean(this.mChecked);; paramBoolean = ((Boolean)paramObject).booleanValue())
    {
      setChecked(paramBoolean);
      return;
    }
  }
  
  public void setChecked(boolean paramBoolean)
  {
    if (this.mChecked != paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      if ((i != 0) || (!this.mCheckedSet))
      {
        this.mChecked = paramBoolean;
        this.mCheckedSet = true;
        persistBoolean(paramBoolean);
        if (i != 0)
        {
          notifyDependencyChange(shouldDisableDependents());
          notifyChanged();
        }
      }
      return;
    }
  }
  
  public void setDisableDependentsState(boolean paramBoolean)
  {
    this.mDisableDependentsState = paramBoolean;
  }
  
  public void setSummaryOff(int paramInt)
  {
    setSummaryOff(getContext().getString(paramInt));
  }
  
  public void setSummaryOff(CharSequence paramCharSequence)
  {
    this.mSummaryOff = paramCharSequence;
    if (!isChecked()) {
      notifyChanged();
    }
  }
  
  public void setSummaryOn(int paramInt)
  {
    setSummaryOn(getContext().getString(paramInt));
  }
  
  public void setSummaryOn(CharSequence paramCharSequence)
  {
    this.mSummaryOn = paramCharSequence;
    if (isChecked()) {
      notifyChanged();
    }
  }
  
  public boolean shouldDisableDependents()
  {
    boolean bool2 = false;
    boolean bool1;
    if (this.mDisableDependentsState) {
      bool1 = this.mChecked;
    }
    for (;;)
    {
      if (!bool1)
      {
        bool1 = bool2;
        if (!super.shouldDisableDependents()) {}
      }
      else
      {
        bool1 = true;
      }
      return bool1;
      if (!this.mChecked) {
        bool1 = true;
      } else {
        bool1 = false;
      }
    }
  }
  
  protected void syncSummaryView(PreferenceViewHolder paramPreferenceViewHolder)
  {
    syncSummaryView(paramPreferenceViewHolder.findViewById(16908304));
  }
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  protected void syncSummaryView(View paramView)
  {
    if (!(paramView instanceof TextView)) {
      return;
    }
    paramView = (TextView)paramView;
    int j = 1;
    int i;
    if ((this.mChecked) && (!TextUtils.isEmpty(this.mSummaryOn)))
    {
      paramView.setText(this.mSummaryOn);
      i = 0;
    }
    for (;;)
    {
      j = i;
      if (i != 0)
      {
        CharSequence localCharSequence = getSummary();
        j = i;
        if (!TextUtils.isEmpty(localCharSequence))
        {
          paramView.setText(localCharSequence);
          j = 0;
        }
      }
      i = 8;
      if (j == 0) {
        i = 0;
      }
      if (i == paramView.getVisibility()) {
        break;
      }
      paramView.setVisibility(i);
      return;
      i = j;
      if (!this.mChecked)
      {
        i = j;
        if (!TextUtils.isEmpty(this.mSummaryOff))
        {
          paramView.setText(this.mSummaryOff);
          i = 0;
        }
      }
    }
  }
  
  static class SavedState
    extends Preference.BaseSavedState
  {
    public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator()
    {
      public TwoStatePreference.SavedState createFromParcel(Parcel paramAnonymousParcel)
      {
        return new TwoStatePreference.SavedState(paramAnonymousParcel);
      }
      
      public TwoStatePreference.SavedState[] newArray(int paramAnonymousInt)
      {
        return new TwoStatePreference.SavedState[paramAnonymousInt];
      }
    };
    boolean checked;
    
    public SavedState(Parcel paramParcel)
    {
      super();
      if (paramParcel.readInt() == 1) {}
      for (;;)
      {
        this.checked = bool;
        return;
        bool = false;
      }
    }
    
    public SavedState(Parcelable paramParcelable)
    {
      super();
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      super.writeToParcel(paramParcel, paramInt);
      if (this.checked) {}
      for (paramInt = 1;; paramInt = 0)
      {
        paramParcel.writeInt(paramInt);
        return;
      }
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v7/preference/TwoStatePreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */