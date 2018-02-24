package android.support.v14.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.RestrictTo;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v7.preference.PreferenceViewHolder;
import android.support.v7.preference.R.attr;
import android.support.v7.preference.TwoStatePreference;
import android.util.AttributeSet;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;

public class SwitchPreference
  extends TwoStatePreference
{
  private final Listener mListener = new Listener(null);
  private CharSequence mSwitchOff;
  private CharSequence mSwitchOn;
  
  public SwitchPreference(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public SwitchPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, TypedArrayUtils.getAttr(paramContext, R.attr.switchPreferenceStyle, 16843629));
  }
  
  public SwitchPreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public SwitchPreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.SwitchPreference, paramInt1, paramInt2);
    setSummaryOn(TypedArrayUtils.getString(paramContext, R.styleable.SwitchPreference_summaryOn, R.styleable.SwitchPreference_android_summaryOn));
    setSummaryOff(TypedArrayUtils.getString(paramContext, R.styleable.SwitchPreference_summaryOff, R.styleable.SwitchPreference_android_summaryOff));
    setSwitchTextOn(TypedArrayUtils.getString(paramContext, R.styleable.SwitchPreference_switchTextOn, R.styleable.SwitchPreference_android_switchTextOn));
    setSwitchTextOff(TypedArrayUtils.getString(paramContext, R.styleable.SwitchPreference_switchTextOff, R.styleable.SwitchPreference_android_switchTextOff));
    setDisableDependentsState(TypedArrayUtils.getBoolean(paramContext, R.styleable.SwitchPreference_disableDependentsState, R.styleable.SwitchPreference_android_disableDependentsState, false));
    paramContext.recycle();
  }
  
  private void syncSwitchView(View paramView)
  {
    if ((paramView instanceof Switch)) {
      ((Switch)paramView).setOnCheckedChangeListener(null);
    }
    if ((paramView instanceof Checkable)) {
      ((Checkable)paramView).setChecked(this.mChecked);
    }
    if ((paramView instanceof Switch))
    {
      paramView = (Switch)paramView;
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
    syncSwitchView(paramView.findViewById(16908352));
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
    syncSwitchView(paramPreferenceViewHolder.findViewById(16908352));
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
      if (!SwitchPreference.this.callChangeListener(Boolean.valueOf(paramBoolean)))
      {
        if (!paramBoolean) {}
        for (paramBoolean = true;; paramBoolean = false)
        {
          paramCompoundButton.setChecked(paramBoolean);
          return;
        }
      }
      SwitchPreference.this.setChecked(paramBoolean);
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v14/preference/SwitchPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */