package com.google.android.tvlauncher.home;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import com.google.android.tvlauncher.settings.AppChannelPermissionActivity;
import com.google.android.tvlauncher.util.ScaleFocusHandler;

class ConfigureChannelsRowController
  implements HomeRow
{
  private final TextView mButton;
  private int mChannelActionsStartMargin;
  private int mDefaultStartMargin;
  private int mDefaultTopMargin;
  private final TextView mDescriptionView;
  private int mMoveChannelStartMargin;
  private OnHomeRowSelectedListener mOnHomeRowSelectedListener;
  private int mSelectedTopMargin;
  private final View mView;
  private int mZoomedOutStartMargin;
  
  ConfigureChannelsRowController(View paramView)
  {
    this.mView = paramView;
    this.mButton = ((TextView)paramView.findViewById(2131951826));
    this.mDescriptionView = ((TextView)paramView.findViewById(2131951827));
    this.mButton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        paramAnonymousView.getContext().startActivity(new Intent(paramAnonymousView.getContext(), AppChannelPermissionActivity.class));
      }
    });
    Object localObject = paramView.getContext().getString(2131492891);
    this.mButton.setContentDescription(String.format((String)localObject, new Object[] { this.mButton.getText(), this.mDescriptionView.getText() }));
    paramView = paramView.getContext().getResources();
    localObject = new ScaleFocusHandler(paramView.getInteger(2131689485), paramView.getFraction(2131886085, 1, 1), paramView.getDimensionPixelSize(2131558605), 1);
    ((ScaleFocusHandler)localObject).setView(this.mButton);
    ((ScaleFocusHandler)localObject).setOnFocusChangeListener(new View.OnFocusChangeListener()
    {
      public void onFocusChange(View paramAnonymousView, boolean paramAnonymousBoolean)
      {
        if (paramAnonymousBoolean) {
          ConfigureChannelsRowController.this.mOnHomeRowSelectedListener.onHomeRowSelected(ConfigureChannelsRowController.this);
        }
        ConfigureChannelsRowController.this.updateSelectedState(paramAnonymousBoolean);
      }
    });
    this.mDefaultTopMargin = paramView.getDimensionPixelSize(2131558614);
    this.mSelectedTopMargin = paramView.getDimensionPixelSize(2131558615);
    this.mDefaultStartMargin = paramView.getDimensionPixelSize(2131558607);
    this.mZoomedOutStartMargin = paramView.getDimensionPixelSize(2131558609);
    this.mChannelActionsStartMargin = paramView.getDimensionPixelSize(2131558606);
    this.mMoveChannelStartMargin = paramView.getDimensionPixelSize(2131558608);
  }
  
  private void updateSelectedState(boolean paramBoolean)
  {
    LinearLayout.LayoutParams localLayoutParams = (LinearLayout.LayoutParams)this.mButton.getLayoutParams();
    if (paramBoolean)
    {
      localLayoutParams.topMargin = this.mSelectedTopMargin;
      this.mDescriptionView.setVisibility(0);
    }
    for (;;)
    {
      this.mButton.setLayoutParams(localLayoutParams);
      return;
      localLayoutParams.topMargin = this.mDefaultTopMargin;
      this.mDescriptionView.setVisibility(8);
    }
  }
  
  void bind(int paramInt)
  {
    int i = this.mDefaultStartMargin;
    switch (paramInt)
    {
    default: 
      paramInt = i;
    }
    for (;;)
    {
      ViewGroup.MarginLayoutParams localMarginLayoutParams = (ViewGroup.MarginLayoutParams)this.mView.getLayoutParams();
      localMarginLayoutParams.setMarginStart(paramInt);
      this.mView.setLayoutParams(localMarginLayoutParams);
      return;
      paramInt = this.mDefaultStartMargin;
      continue;
      paramInt = this.mZoomedOutStartMargin;
      continue;
      paramInt = this.mChannelActionsStartMargin;
      continue;
      paramInt = this.mMoveChannelStartMargin;
    }
  }
  
  public View getView()
  {
    return this.mView;
  }
  
  public void setOnHomeRowRemovedListener(OnHomeRowRemovedListener paramOnHomeRowRemovedListener) {}
  
  public void setOnHomeRowSelectedListener(OnHomeRowSelectedListener paramOnHomeRowSelectedListener)
  {
    this.mOnHomeRowSelectedListener = paramOnHomeRowSelectedListener;
  }
  
  public void setOnHomeStateChangeListener(OnHomeStateChangeListener paramOnHomeStateChangeListener) {}
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/home/ConfigureChannelsRowController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */