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
    this.mButton = ((TextView)paramView.findViewById(R.id.button));
    this.mDescriptionView = ((TextView)paramView.findViewById(R.id.description_text));
    this.mButton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        paramAnonymousView.getContext().startActivity(new Intent(paramAnonymousView.getContext(), AppChannelPermissionActivity.class));
      }
    });
    Object localObject = paramView.getContext().getString(R.string.action_configure_channels_content_description_format);
    this.mButton.setContentDescription(String.format((String)localObject, new Object[] { this.mButton.getText(), this.mDescriptionView.getText() }));
    paramView = paramView.getContext().getResources();
    localObject = new ScaleFocusHandler(paramView.getInteger(R.integer.home_configure_channels_button_focused_animation_duration_ms), paramView.getFraction(2131886085, 1, 1), paramView.getDimensionPixelSize(R.dimen.home_configure_channels_button_focused_elevation), 1);
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
    this.mDefaultTopMargin = paramView.getDimensionPixelSize(R.dimen.home_configure_channels_row_margin_top);
    this.mSelectedTopMargin = paramView.getDimensionPixelSize(R.dimen.home_configure_channels_row_selected_margin_top);
    this.mDefaultStartMargin = paramView.getDimensionPixelSize(R.dimen.home_configure_channels_button_margin_default);
    this.mZoomedOutStartMargin = paramView.getDimensionPixelSize(R.dimen.home_configure_channels_button_margin_zoomed_out);
    this.mChannelActionsStartMargin = paramView.getDimensionPixelSize(R.dimen.home_configure_channels_button_margin_channel_actions);
    this.mMoveChannelStartMargin = paramView.getDimensionPixelSize(R.dimen.home_configure_channels_button_margin_move_channel);
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


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/home/ConfigureChannelsRowController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */