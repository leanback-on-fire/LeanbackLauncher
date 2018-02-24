package android.support.v7.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.RestrictTo;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v7.widget.SwitchCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class SwitchPreferenceCompat
  extends TwoStatePreference
{
  private final Listener mListener = new Listener(null);
  private CharSequence mSwitchOff;
  private CharSequence mSwitchOn;
  
  public SwitchPreferenceCompat(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public SwitchPreferenceCompat(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, R.attr.switchPreferenceCompatStyle);
  }
  
  public SwitchPreferenceCompat(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public SwitchPreferenceCompat(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.SwitchPreferenceCompat, paramInt1, paramInt2);
    setSummaryOn(TypedArrayUtils.getString(paramContext, R.styleable.SwitchPreferenceCompat_summaryOn, R.styleable.SwitchPreferenceCompat_android_summaryOn));
    setSummaryOff(TypedArrayUtils.getString(paramContext, R.styleable.SwitchPreferenceCompat_summaryOff, R.styleable.SwitchPreferenceCompat_android_summaryOff));
    setSwitchTextOn(TypedArrayUtils.getString(paramContext, R.styleable.SwitchPreferenceCompat_switchTextOn, R.styleable.SwitchPreferenceCompat_android_switchTextOn));
    setSwitchTextOff(TypedArrayUtils.getString(paramContext, R.styleable.SwitchPreferenceCompat_switchTextOff, R.styleable.SwitchPreferenceCompat_android_switchTextOff));
    setDisableDependentsState(TypedArrayUtils.getBoolean(paramContext, R.styleable.SwitchPreferenceCompat_disableDependentsState, R.styleable.SwitchPreferenceCompat_android_disableDependentsState, false));
    paramContext.recycle();
  }
  
  private void syncSwitchView(View paramView)
  {
    if ((paramView instanceof SwitchCompat)) {
      ((SwitchCompat)paramView).setOnCheckedChangeListener(null);
    }
    if ((paramView instanceof Checkable)) {
      ((Checkable)paramView).setChecked(this.mChecked);
    }
    if ((paramView instanceof SwitchCompat))
    {
      paramView = (SwitchCompat)paramView;
      paramView.setTextOn(this.mSwitchOn);
      paramView.setTextOff(this.mSwitchOff);
      paramView.setOnCheckedChangeListener(this.mListener);
    }
  }
  
  private void syncViewIfAccessibilityEnabled(View paramView)
  {
    if (!((AccessibilityManager)getContext().getSystemService("accessibility")).isEnabled()) {
      return;
    }
    syncSwitchView(paramView.findViewById(R.id.switchWidget));
    syncSummaryView(paramView.findViewById(16908304));
  }
  
  public CharSequence getSwitchTextOff()
  {
    return this.mSwitchOff;
  }
  
  public CharSequence getSwitchTextOn()
  {
    return this.mSwitchOn;
  }
  
  public void onBindViewHolder(PreferenceViewHolder paramPreferenceViewHolder)
  {
    super.onBindViewHolder(paramPreferenceViewHolder);
    syncSwitchView(paramPreferenceViewHolder.findViewById(R.id.switchWidget));
    syncSummaryView(paramPreferenceViewHolder);
  }
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  protected void performClick(View paramView)
  {
    super.performClick(paramView);
    syncViewIfAccessibilityEnabled(paramView);
  }
  
  public void setSwitchTextOff(int paramInt)
  {
    setSwitchTextOff(getContext().getString(paramInt));
  }
  
  public void setSwitchTextOff(CharSequence paramCharSequence)
  {
    this.mSwitchOff = paramCharSequence;
    notifyChanged();
  }
  
  public void setSwitchTextOn(int paramInt)
  {
    setSwitchTextOn(getContext().getString(paramInt));
  }
  
  public void setSwitchTextOn(CharSequence paramCharSequence)
  {
    this.mSwitchOn = paramCharSequence;
    notifyChanged();
  }
  
  private class Listener
    implements CompoundButton.OnCheckedChangeListener
  {
    private Listener() {}
    
    public void onCheckedChanged(CompoundButton paramCompoundButton, boolean paramBoolean)
    {
      if (!SwitchPreferenceCompat.this.callChangeListener(Boolean.valueOf(paramBoolean)))
      {
        if (!paramBoolean) {}
        for (paramBoolean = true;; paramBoolean = false)
        {
          paramCompoundButton.setChecked(paramBoolean);
          return;
        }
      }
      SwitchPreferenceCompat.this.setChecked(paramBoolean);
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v7/preference/SwitchPreferenceCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */