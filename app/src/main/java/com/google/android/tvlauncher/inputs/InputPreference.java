package com.google.android.tvlauncher.inputs;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.TypedValue;
import android.view.View;

class InputPreference
  extends Preference
{
  private final float mConnectedStateIconAlpha;
  private final float mConnectedStateTextAlpha;
  private final float mDisconnectedStateIconAlpha;
  private final float mDisconnectedStateTextAlpha;
  private final int mInputState;
  private final float mStandbyStateIconAlpha;
  private final float mStandbyStateTextAlpha;
  
  InputPreference(Context paramContext, int paramInt)
  {
    super(paramContext);
    this.mInputState = paramInt;
    paramContext = paramContext.getResources();
    this.mDisconnectedStateTextAlpha = getFloat(paramContext, 2131296265);
    this.mConnectedStateTextAlpha = getFloat(paramContext, 2131296263);
    this.mStandbyStateTextAlpha = getFloat(paramContext, 2131296267);
    this.mDisconnectedStateIconAlpha = getFloat(paramContext, 2131296264);
    this.mConnectedStateIconAlpha = getFloat(paramContext, 2131296262);
    this.mStandbyStateIconAlpha = getFloat(paramContext, 2131296266);
  }
  
  private float getFloat(Resources paramResources, int paramInt)
  {
    TypedValue localTypedValue = new TypedValue();
    paramResources.getValue(paramInt, localTypedValue, true);
    return localTypedValue.getFloat();
  }
  
  public void onBindViewHolder(PreferenceViewHolder paramPreferenceViewHolder)
  {
    super.onBindViewHolder(paramPreferenceViewHolder);
    View localView = paramPreferenceViewHolder.findViewById(16908294);
    paramPreferenceViewHolder = paramPreferenceViewHolder.findViewById(16908310);
    if (this.mInputState == 2)
    {
      localView.setAlpha(this.mDisconnectedStateIconAlpha);
      paramPreferenceViewHolder.setAlpha(this.mDisconnectedStateTextAlpha);
      return;
    }
    if (this.mInputState == 1)
    {
      localView.setAlpha(this.mStandbyStateIconAlpha);
      paramPreferenceViewHolder.setAlpha(this.mStandbyStateTextAlpha);
      return;
    }
    localView.setAlpha(this.mConnectedStateIconAlpha);
    paramPreferenceViewHolder.setAlpha(this.mConnectedStateTextAlpha);
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/inputs/InputPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */