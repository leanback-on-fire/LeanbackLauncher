package android.support.v7.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class SeekBarPreference
  extends Preference
{
  private static final String TAG = "SeekBarPreference";
  private boolean mAdjustable;
  private int mMax;
  private int mMin;
  private SeekBar mSeekBar;
  private SeekBar.OnSeekBarChangeListener mSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener()
  {
    public void onProgressChanged(SeekBar paramAnonymousSeekBar, int paramAnonymousInt, boolean paramAnonymousBoolean)
    {
      if ((paramAnonymousBoolean) && (!SeekBarPreference.this.mTrackingTouch)) {
        SeekBarPreference.this.syncValueInternal(paramAnonymousSeekBar);
      }
    }
    
    public void onStartTrackingTouch(SeekBar paramAnonymousSeekBar)
    {
      SeekBarPreference.access$002(SeekBarPreference.this, true);
    }
    
    public void onStopTrackingTouch(SeekBar paramAnonymousSeekBar)
    {
      SeekBarPreference.access$002(SeekBarPreference.this, false);
      if (paramAnonymousSeekBar.getProgress() + SeekBarPreference.this.mMin != SeekBarPreference.this.mSeekBarValue) {
        SeekBarPreference.this.syncValueInternal(paramAnonymousSeekBar);
      }
    }
  };
  private int mSeekBarIncrement;
  private View.OnKeyListener mSeekBarKeyListener = new View.OnKeyListener()
  {
    public boolean onKey(View paramAnonymousView, int paramAnonymousInt, KeyEvent paramAnonymousKeyEvent)
    {
      if (paramAnonymousKeyEvent.getAction() != 0) {}
      while (((!SeekBarPreference.this.mAdjustable) && ((paramAnonymousInt == 21) || (paramAnonymousInt == 22))) || (paramAnonymousInt == 23) || (paramAnonymousInt == 66)) {
        return false;
      }
      if (SeekBarPreference.this.mSeekBar == null)
      {
        Log.e("SeekBarPreference", "SeekBar view is null and hence cannot be adjusted.");
        return false;
      }
      return SeekBarPreference.this.mSeekBar.onKeyDown(paramAnonymousInt, paramAnonymousKeyEvent);
    }
  };
  private int mSeekBarValue;
  private TextView mSeekBarValueTextView;
  private boolean mShowSeekBarValue;
  private boolean mTrackingTouch;
  
  public SeekBarPreference(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public SeekBarPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, R.attr.seekBarPreferenceStyle);
  }
  
  public SeekBarPreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public SeekBarPreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.SeekBarPreference, paramInt1, paramInt2);
    this.mMin = paramContext.getInt(R.styleable.SeekBarPreference_min, 0);
    setMax(paramContext.getInt(R.styleable.SeekBarPreference_android_max, 100));
    setSeekBarIncrement(paramContext.getInt(R.styleable.SeekBarPreference_seekBarIncrement, 0));
    this.mAdjustable = paramContext.getBoolean(R.styleable.SeekBarPreference_adjustable, true);
    this.mShowSeekBarValue = paramContext.getBoolean(R.styleable.SeekBarPreference_showSeekBarValue, true);
    paramContext.recycle();
  }
  
  private void setValueInternal(int paramInt, boolean paramBoolean)
  {
    int i = paramInt;
    if (paramInt < this.mMin) {
      i = this.mMin;
    }
    paramInt = i;
    if (i > this.mMax) {
      paramInt = this.mMax;
    }
    if (paramInt != this.mSeekBarValue)
    {
      this.mSeekBarValue = paramInt;
      if (this.mSeekBarValueTextView != null) {
        this.mSeekBarValueTextView.setText(String.valueOf(this.mSeekBarValue));
      }
      persistInt(paramInt);
      if (paramBoolean) {
        notifyChanged();
      }
    }
  }
  
  private void syncValueInternal(SeekBar paramSeekBar)
  {
    int i = this.mMin + paramSeekBar.getProgress();
    if (i != this.mSeekBarValue)
    {
      if (callChangeListener(Integer.valueOf(i))) {
        setValueInternal(i, false);
      }
    }
    else {
      return;
    }
    paramSeekBar.setProgress(this.mSeekBarValue - this.mMin);
  }
  
  public int getMax()
  {
    return this.mMax;
  }
  
  public int getMin()
  {
    return this.mMin;
  }
  
  public final int getSeekBarIncrement()
  {
    return this.mSeekBarIncrement;
  }
  
  public int getValue()
  {
    return this.mSeekBarValue;
  }
  
  public boolean isAdjustable()
  {
    return this.mAdjustable;
  }
  
  public void onBindViewHolder(PreferenceViewHolder paramPreferenceViewHolder)
  {
    super.onBindViewHolder(paramPreferenceViewHolder);
    paramPreferenceViewHolder.itemView.setOnKeyListener(this.mSeekBarKeyListener);
    this.mSeekBar = ((SeekBar)paramPreferenceViewHolder.findViewById(R.id.seekbar));
    this.mSeekBarValueTextView = ((TextView)paramPreferenceViewHolder.findViewById(R.id.seekbar_value));
    if (this.mShowSeekBarValue) {
      this.mSeekBarValueTextView.setVisibility(0);
    }
    while (this.mSeekBar == null)
    {
      Log.e("SeekBarPreference", "SeekBar view is null in onBindViewHolder.");
      return;
      this.mSeekBarValueTextView.setVisibility(8);
      this.mSeekBarValueTextView = null;
    }
    this.mSeekBar.setOnSeekBarChangeListener(this.mSeekBarChangeListener);
    this.mSeekBar.setMax(this.mMax - this.mMin);
    if (this.mSeekBarIncrement != 0) {
      this.mSeekBar.setKeyProgressIncrement(this.mSeekBarIncrement);
    }
    for (;;)
    {
      this.mSeekBar.setProgress(this.mSeekBarValue - this.mMin);
      if (this.mSeekBarValueTextView != null) {
        this.mSeekBarValueTextView.setText(String.valueOf(this.mSeekBarValue));
      }
      this.mSeekBar.setEnabled(isEnabled());
      return;
      this.mSeekBarIncrement = this.mSeekBar.getKeyProgressIncrement();
    }
  }
  
  protected Object onGetDefaultValue(TypedArray paramTypedArray, int paramInt)
  {
    return Integer.valueOf(paramTypedArray.getInt(paramInt, 0));
  }
  
  protected void onRestoreInstanceState(Parcelable paramParcelable)
  {
    if (!paramParcelable.getClass().equals(SavedState.class))
    {
      super.onRestoreInstanceState(paramParcelable);
      return;
    }
    paramParcelable = (SavedState)paramParcelable;
    super.onRestoreInstanceState(paramParcelable.getSuperState());
    this.mSeekBarValue = paramParcelable.seekBarValue;
    this.mMin = paramParcelable.min;
    this.mMax = paramParcelable.max;
    notifyChanged();
  }
  
  protected Parcelable onSaveInstanceState()
  {
    Object localObject = super.onSaveInstanceState();
    if (isPersistent()) {
      return (Parcelable)localObject;
    }
    localObject = new SavedState((Parcelable)localObject);
    ((SavedState)localObject).seekBarValue = this.mSeekBarValue;
    ((SavedState)localObject).min = this.mMin;
    ((SavedState)localObject).max = this.mMax;
    return (Parcelable)localObject;
  }
  
  protected void onSetInitialValue(boolean paramBoolean, Object paramObject)
  {
    if (paramBoolean) {}
    for (int i = getPersistedInt(this.mSeekBarValue);; i = ((Integer)paramObject).intValue())
    {
      setValue(i);
      return;
    }
  }
  
  public void setAdjustable(boolean paramBoolean)
  {
    this.mAdjustable = paramBoolean;
  }
  
  public final void setMax(int paramInt)
  {
    int i = paramInt;
    if (paramInt < this.mMin) {
      i = this.mMin;
    }
    if (i != this.mMax)
    {
      this.mMax = i;
      notifyChanged();
    }
  }
  
  public void setMin(int paramInt)
  {
    int i = paramInt;
    if (paramInt > this.mMax) {
      i = this.mMax;
    }
    if (i != this.mMin)
    {
      this.mMin = i;
      notifyChanged();
    }
  }
  
  public final void setSeekBarIncrement(int paramInt)
  {
    if (paramInt != this.mSeekBarIncrement)
    {
      this.mSeekBarIncrement = Math.min(this.mMax - this.mMin, Math.abs(paramInt));
      notifyChanged();
    }
  }
  
  public void setValue(int paramInt)
  {
    setValueInternal(paramInt, true);
  }
  
  private static class SavedState
    extends Preference.BaseSavedState
  {
    public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator()
    {
      public SeekBarPreference.SavedState createFromParcel(Parcel paramAnonymousParcel)
      {
        return new SeekBarPreference.SavedState(paramAnonymousParcel);
      }
      
      public SeekBarPreference.SavedState[] newArray(int paramAnonymousInt)
      {
        return new SeekBarPreference.SavedState[paramAnonymousInt];
      }
    };
    int max;
    int min;
    int seekBarValue;
    
    public SavedState(Parcel paramParcel)
    {
      super();
      this.seekBarValue = paramParcel.readInt();
      this.min = paramParcel.readInt();
      this.max = paramParcel.readInt();
    }
    
    public SavedState(Parcelable paramParcelable)
    {
      super();
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      super.writeToParcel(paramParcel, paramInt);
      paramParcel.writeInt(this.seekBarValue);
      paramParcel.writeInt(this.min);
      paramParcel.writeInt(this.max);
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v7/preference/SeekBarPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */