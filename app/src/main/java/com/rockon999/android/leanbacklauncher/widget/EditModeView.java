package com.rockon999.android.leanbacklauncher.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v17.leanback.widget.VerticalGridView;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rockon999.android.leanbacklauncher.ActiveItemsRowView;
import com.rockon999.android.leanbacklauncher.HomeScreenAdapter;
import com.rockon999.android.leanbacklauncher.R;
import com.rockon999.android.leanbacklauncher.animation.EditModeUninstallAnimationHolder;
import com.rockon999.android.leanbacklauncher.animation.ViewFocusAnimator;
import com.rockon999.android.leanbacklauncher.apps.BannerSelectedChangedListener;
import com.rockon999.android.leanbacklauncher.apps.BannerView;
import com.rockon999.android.leanbacklauncher.apps.OnEditModeChangedListener;
import com.rockon999.android.leanbacklauncher.graphics.ClipCircleDrawable;
import com.rockon999.android.leanbacklauncher.util.Util;

import java.util.ArrayList;

public final class EditModeView extends RelativeLayout implements OnEditModeChangedListener, BannerSelectedChangedListener, OnClickListener {
    private final ArrayList<EditModeViewActionListener> mActionListeners;
    private BannerView mCurSelectedBanner;
    private Button mFinishButton;
    private ViewFocusAnimator mFocusAnimator;
    private EditModeUninstallAnimationHolder mUninstallAnimation;
    private ImageView mUninstallApp;
    private ImageView mUninstallCircle;
    private ImageView mUninstallIcon;
    private ImageView mUninstallIconCircle;
    private HomeScreenAdapter mHomeAdapter;
    private OnEditModeUninstallPressedListener mUninstallListener;
    private TextView mUninstallText;

    public void setHomeScreenAdapter(HomeScreenAdapter homeScreenAdapter) {
        this.mHomeAdapter = homeScreenAdapter;
    }

    public interface OnEditModeUninstallPressedListener {
        void onUninstallPressed(String str);
    }

    public EditModeView(Context context) {
        this(context, null);
    }

    public EditModeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EditModeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mActionListeners = new ArrayList<>();
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mUninstallCircle = (ImageView) findViewById(R.id.uninstall_area_circle);
        this.mUninstallIconCircle = (ImageView) findViewById(R.id.uninstall_icon_circle);
        this.mUninstallIcon = (ImageView) findViewById(R.id.uninstall_icon);
        this.mUninstallText = (TextView) findViewById(R.id.uninstall_text);
        this.mFinishButton = (Button) findViewById(R.id.finish_button);
        this.mUninstallApp = (ImageView) findViewById(R.id.uninstall_app_banner);
        this.mFocusAnimator = new ViewFocusAnimator(this.mUninstallApp);
        this.mFocusAnimator.setFocusImmediate(true);
        float zDeltaIcon = (float) getResources().getDimensionPixelOffset(R.dimen.edit_uninstall_icon_z);
        this.mUninstallApp.setZ((float) getResources().getDimensionPixelOffset(R.dimen.edit_app_banner_z));
        this.mUninstallIconCircle.setZ(zDeltaIcon);
        this.mUninstallIcon.setZ(zDeltaIcon);
        this.mUninstallApp.setClipToOutline(true);
        this.mFinishButton.setOnClickListener(this);
        this.mUninstallText.setImportantForAccessibility(2);

        setUninstallCircleLayout();
        setUninstallIconCircleLayout();
        setUninstallTextLayout();
        this.mUninstallAnimation = new EditModeUninstallAnimationHolder(this);
    }

    public void onEditModeChanged(boolean editMode) {
        float f;
        int i = 0;
        if (!editMode) {
            setBannerUninstallMode(false);
            if (hasFocus()) {
                notifyOnExitEditModeTriggered();
            }
        }
        if (!editMode) {
            i = 8;
        }
        setVisibility(i);
        if (editMode) {
            f = 1.0f;
        } else {
            f = 0.0f;
        }
        setAlpha(f);
    }

    public void onSelectedChanged(BannerView v, boolean selected) {
        int i;
        int i2 = 8;
        if (selected) {
            this.mCurSelectedBanner = v;
        } else {
            this.mCurSelectedBanner = null;
        }
        ImageView imageView = this.mUninstallIconCircle;
        if (selected) {
            i = 0;
        } else {
            i = 8;
        }
        imageView.setVisibility(i);
        TextView textView = this.mUninstallText;
        if (selected) {
            i = 0;
        } else {
            i = 8;
        }
        textView.setVisibility(i);
        imageView = this.mUninstallIcon;
        if (selected) {
            i = 0;
        } else {
            i = 8;
        }
        imageView.setVisibility(i);
        imageView = this.mUninstallCircle;
        if (selected) {
            i = 0;
        } else {
            i = 8;
        }
        imageView.setVisibility(i);
        Button button = this.mFinishButton;
        if (!selected) {
            i2 = 0;
        }
        button.setVisibility(i2);
    }

    public void clearUninstallAndFinishLayers() {
        this.mUninstallIconCircle.setVisibility(View.GONE);
        this.mUninstallText.setVisibility(View.GONE);
        this.mUninstallIcon.setVisibility(View.GONE);
        this.mUninstallCircle.setVisibility(View.GONE);
        this.mFinishButton.setVisibility(View.GONE);
    }

    public void addActionListener(EditModeViewActionListener listener) {
        this.mActionListeners.add(listener);
    }

    public void removeActionListener(EditModeViewActionListener listener) {
        this.mActionListeners.remove(listener);
    }

    public void requestUninstallIconFocus(BannerView curView, ActiveItemsRowView activeItems) {
        this.mUninstallIcon.requestFocus();
        setBannerUninstallModeWithAnimation(true, curView, activeItems);
    }

    public void setBannerUninstallMode(boolean uninstallMode) {
        int i;
        this.mUninstallAnimation.setViewsToExitState();
        setUninstallCircleLayout();
        setUninstallIconCircleLayout();
        setUninstallTextLayout();
        ImageView imageView = this.mUninstallApp;
        if (uninstallMode) {
            i = 0;
        } else {
            i = 8;
        }
        imageView.setVisibility(i);
    }

    public void setBannerUninstallModeWithAnimation(boolean uninstallMode, BannerView curView, ActiveItemsRowView activeItems) {
        if (uninstallMode) {
            this.mUninstallAnimation.startAnimation(EditModeUninstallAnimationHolder.EditModeUninstallState.ENTER, curView, activeItems);
        } else {
            this.mUninstallAnimation.startAnimation(EditModeUninstallAnimationHolder.EditModeUninstallState.EXIT, curView, activeItems);
        }
    }

    public void uninstallComplete() {
        setBannerUninstallMode(false);
        notifyUninstallComplete();
    }

    public void uninstallFailure() {
        setBannerUninstallMode(false);
        notifyUninstallFailure();
    }

    public void setBannerDrawable(Drawable drawable) {
        this.mUninstallApp.setImageDrawable(drawable);
    }

    public void setUninstallListener(OnEditModeUninstallPressedListener listener) {
        this.mUninstallListener = listener;
    }

    public Button getFinishButton() {
        return this.mFinishButton;
    }


    public ImageView getUninstallIcon() {
        return this.mUninstallIcon;
    }

    public ImageView getUninstallCircle() {
        return this.mUninstallCircle;
    }

    public ImageView getUninstallApp() {
        return this.mUninstallApp;
    }

    public TextView getUninstallText() {
        return this.mUninstallText;
    }

    public ImageView getUninstallIconCircle() {
        return this.mUninstallIconCircle;
    }

    private void notifyOnExitEditModeTriggered() {
        for (EditModeViewActionListener listener : this.mActionListeners) {
            listener.onEditModeExitTriggered();
        }
    }

    private void notifyOnFocusLeavingEditMode(int from) {
        for (EditModeViewActionListener listener : this.mActionListeners) {
            listener.onFocusLeavingEditModeLayer(from);
        }
    }

    private void notifyUninstallComplete() {
        for (EditModeViewActionListener listener : this.mActionListeners) {
            listener.onUninstallComplete();
        }
    }

    private void notifyUninstallFailure() {
        for (EditModeViewActionListener listener : this.mActionListeners) {
            listener.onUninstallFailure();
        }
    }

    private String notifyPrepForUninstall() {
        String packageUninstalling = "";
        for (EditModeViewActionListener listener : this.mActionListeners) {
            String result = listener.onPrepForUninstall();
            if (!(result == null || result.isEmpty())) {
                packageUninstalling = result;
            }
        }
        return packageUninstalling;
    }

    public void onBackPressed() {
        if (this.mCurSelectedBanner != null) {
            this.mCurSelectedBanner.notifyEditModeManager(false);
            this.mCurSelectedBanner.setSelected(false);
            return;
        }
        notifyOnExitEditModeTriggered();
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();
        if (action == 0) {
            if (keyCode == KeyEvent.KEYCODE_DPAD_UP && (this.mFinishButton.isFocused())) {
                notifyOnFocusLeavingEditMode(0);
            } else if ((keyCode == KeyEvent.KEYCODE_DPAD_UP && this.mUninstallIcon.isFocused()) || (keyCode == KeyEvent.KEYCODE_BACK && this.mUninstallIcon.isFocused())) {
                notifyOnFocusLeavingEditMode(1);
            } else if ((Util.isConfirmKey(keyCode) || keyCode == KeyEvent.KEYCODE_BACK) && (this.mFinishButton.isFocused())) {
                notifyOnExitEditModeTriggered();
            } else if (Util.isConfirmKey(keyCode) && this.mUninstallIcon.isFocused()) {
                this.mUninstallListener.onUninstallPressed(notifyPrepForUninstall());
            }
            return true;
        } else
            return action == 1 || super.dispatchKeyEvent(event);
    }

    private void setUninstallCircleLayout() {
        LayoutParams circlelp = new LayoutParams(getResources().getDimensionPixelSize(R.dimen.edit_uninstall_area_circle_width), getResources().getDimensionPixelOffset(R.dimen.edit_uninstall_area_circle_height));
        circlelp.addRule(12);
        circlelp.addRule(13);
        this.mUninstallCircle.setImageDrawable(new ClipCircleDrawable(getResources().getColor(R.color.edit_uninstall_area_color)));
        this.mUninstallCircle.setLayoutParams(circlelp);
    }

    private void setUninstallIconCircleLayout() {
        LayoutParams iconCirclelp = new LayoutParams(getResources().getDimensionPixelSize(R.dimen.edit_uninstall_icon_circle_focused_size), getResources().getDimensionPixelSize(R.dimen.edit_uninstall_icon_circle_focused_size));
        iconCirclelp.setMargins(0, 0, 0, getResources().getDimensionPixelSize(R.dimen.edit_uninstall_icon_circle_focused_bottom_margin));
        iconCirclelp.addRule(2, R.id.uninstall_text);
        iconCirclelp.addRule(13);
        this.mUninstallIconCircle.setImageDrawable(new ClipCircleDrawable(getResources().getColor(R.color.edit_uninstall_circle_color)));
        this.mUninstallIconCircle.setLayoutParams(iconCirclelp);
    }

    private void setUninstallTextLayout() {
        LayoutParams textlp = new LayoutParams(-2, -2);
        textlp.setMargins(0, 0, 0, getResources().getDimensionPixelOffset(R.dimen.edit_uninstall_text_focused_bottom_margin));
        textlp.addRule(14);
        textlp.addRule(12);
        this.mUninstallText.setLayoutParams(textlp);
    }


    @Override
    public void onClick(View v) {
        VerticalGridView mGrid = (VerticalGridView) findViewById(R.id.main_list_view);

        if (v == this.mFinishButton) {
            notifyOnExitEditModeTriggered();
        }
    }
}
