package com.google.android.tvlauncher.home;

import android.content.Intent;
import android.content.res.Resources;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import com.google.android.tvlauncher.R;
import com.google.android.tvlauncher.settings.AppChannelPermissionActivity;
import com.google.android.tvlauncher.util.ScaleFocusHandler;

class ConfigureChannelsRowController implements HomeRow {
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

    ConfigureChannelsRowController(View v) {
        this.mView = v;
        this.mButton = (TextView) v.findViewById(R.id.button);
        this.mDescriptionView = (TextView) v.findViewById(R.id.description_text);
        this.mButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                v.getContext().startActivity(new Intent(v.getContext(), AppChannelPermissionActivity.class));
            }
        });
        String buttonContentDescriptionFormat = v.getContext().getString(R.string.action_configure_channels_content_description_format);
        this.mButton.setContentDescription(String.format(buttonContentDescriptionFormat, new Object[]{this.mButton.getText(), this.mDescriptionView.getText()}));
        Resources resources = v.getContext().getResources();
        ScaleFocusHandler focusHandler = new ScaleFocusHandler(resources.getInteger(R.integer.home_configure_channels_button_focused_animation_duration_ms), resources.getFraction(R.fraction.home_configure_channels_button_focused_scale, 1, 1), (float) resources.getDimensionPixelSize(R.dimen.home_configure_channels_button_focused_elevation), 1);
        focusHandler.setView(this.mButton);
        focusHandler.setOnFocusChangeListener(new OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    ConfigureChannelsRowController.this.mOnHomeRowSelectedListener.onHomeRowSelected(ConfigureChannelsRowController.this);
                }
                ConfigureChannelsRowController.this.updateSelectedState(hasFocus);
            }
        });
        this.mDefaultTopMargin = resources.getDimensionPixelSize(R.dimen.home_configure_channels_row_margin_top);
        this.mSelectedTopMargin = resources.getDimensionPixelSize(R.dimen.home_configure_channels_row_selected_margin_top);
        this.mDefaultStartMargin = resources.getDimensionPixelSize(R.dimen.home_configure_channels_button_margin_default);
        this.mZoomedOutStartMargin = resources.getDimensionPixelSize(R.dimen.home_configure_channels_button_margin_zoomed_out);
        this.mChannelActionsStartMargin = resources.getDimensionPixelSize(R.dimen.home_configure_channels_button_margin_channel_actions);
        this.mMoveChannelStartMargin = resources.getDimensionPixelSize(R.dimen.home_configure_channels_button_margin_move_channel);
    }

    private void updateSelectedState(boolean selected) {
        LayoutParams lp = (LayoutParams) this.mButton.getLayoutParams();
        if (selected) {
            lp.topMargin = this.mSelectedTopMargin;
            this.mDescriptionView.setVisibility(0);
        } else {
            lp.topMargin = this.mDefaultTopMargin;
            this.mDescriptionView.setVisibility(8);
        }
        this.mButton.setLayoutParams(lp);
    }

    public void setOnHomeStateChangeListener(OnHomeStateChangeListener listener) {
    }

    public void setOnHomeRowRemovedListener(OnHomeRowRemovedListener listener) {
    }

    public void setOnHomeRowSelectedListener(OnHomeRowSelectedListener listener) {
        this.mOnHomeRowSelectedListener = listener;
    }

    public View getView() {
        return this.mView;
    }

    void bind(int homeState) {
        int marginStart = this.mDefaultStartMargin;
        switch (homeState) {
            case 0:
                marginStart = this.mDefaultStartMargin;
                break;
            case 1:
                marginStart = this.mZoomedOutStartMargin;
                break;
            case 2:
                marginStart = this.mChannelActionsStartMargin;
                break;
            case 3:
                marginStart = this.mMoveChannelStartMargin;
                break;
        }
        MarginLayoutParams buttonLayoutParams = (MarginLayoutParams) this.mView.getLayoutParams();
        buttonLayoutParams.setMarginStart(marginStart);
        this.mView.setLayoutParams(buttonLayoutParams);
    }
}
