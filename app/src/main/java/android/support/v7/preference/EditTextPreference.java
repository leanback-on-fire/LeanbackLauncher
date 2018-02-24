package android.support.v7.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.v4.content.res.TypedArrayUtils;
import android.text.TextUtils;
import android.util.AttributeSet;

public class EditTextPreference
  extends DialogPreference
{
  private String mText;
  
  public EditTextPreference(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public EditTextPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, TypedArrayUtils.getAttr(paramContext, R.attr.editTextPreferenceStyle, 16842898));
  }
  
  public EditTextPreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public EditTextPreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
  }
  
  public String getText()
  {
    return this.mText;
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
    setText(paramParcelable.text);
  }
  
  protected Parcelable onSaveInstanceState()
  {
    Object localObject = super.onSaveInstanceState();
    if (isPersistent()) {
      return (Parcelable)localObject;
    }
    localObject = new SavedState((Parcelable)localObject);
    ((SavedState)localObject).text = getText();
    return (Parcelable)localObject;
  }
  
  protected void onSetInitialValue(boolean paramBoolean, Object paramObject)
  {
    if (paramBoolean) {}
    for (paramObject = getPersistedString(this.mText);; paramObject = (String)paramObject)
    {
      setText((String)paramObject);
      return;
    }
  }
  
  public void setText(String paramString)
  {
    boolean bool1 = shouldDisableDependents();
    this.mText = paramString;
    persistString(paramString);
    boolean bool2 = shouldDisableDependents();
    if (bool2 != bool1) {
      notifyDependencyChange(bool2);
    }
  }
  
  public boolean shouldDisableDependents()
  {
    return (TextUtils.isEmpty(this.mText)) || (super.shouldDisableDependents());
  }
  
  private static class SavedState
    extends Preference.BaseSavedState
  {
    public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator()
    {
      public EditTextPreference.SavedState createFromParcel(Parcel paramAnonymousParcel)
      {
        return new EditTextPreference.SavedState(paramAnonymousParcel);
      }
      
      public EditTextPreference.SavedState[] newArray(int paramAnonymousInt)
      {
        return new EditTextPreference.SavedState[paramAnonymousInt];
      }
    };
    String text;
    
    public SavedState(Parcel paramParcel)
    {
      super();
      this.text = paramParcel.readString();
    }
    
    public SavedState(Parcelable paramParcelable)
    {
      super();
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      super.writeToParcel(paramParcel, paramInt);
      paramParcel.writeString(this.text);
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v7/preference/EditTextPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */