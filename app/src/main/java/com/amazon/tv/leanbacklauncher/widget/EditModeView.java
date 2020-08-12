package com.amazon.tv.leanbacklauncher.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amazon.tv.leanbacklauncher.EditableAppsRowView;
import com.amazon.tv.leanbacklauncher.R;
import com.amazon.tv.leanbacklauncher.animation.EditModeUninstallAnimationHolder;
import com.amazon.tv.leanbacklauncher.animation.ViewFocusAnimator;
import com.amazon.tv.leanbacklauncher.apps.BannerSelectedChangedListener;
import com.amazon.tv.leanbacklauncher.apps.BannerView;
import com.amazon.tv.leanbacklauncher.apps.OnEditModeChangedListener;
import com.amazon.tv.leanbacklauncher.graphics.ClipCircleDrawable;
import com.amazon.tv.leanbacklauncher.util.Util;

import java.util.ArrayList;
import java.util.Iterator;

public final class EditModeView extends RelativeLayout implements OnClickListener, BannerSelectedChangedListener, OnEditModeChangedListener {
    private final ArrayList<EditModeViewActionListener> mActionListeners;
    private BannerView mCurSelectedBanner;
    private Button mFinishButton;
    private ViewFocusAnimator mFocusAnimator;
    private EditModeUninstallAnimationHolder mUninstallAnimation;
    private ImageView mUninstallApp;
    private ImageView mUninstallCircle;
    private ImageView mUninstallIcon;
    private ImageView mUninstallIconCircle;
    private OnEditModeUninstallPressedListener mUninstallListener;
    private TextView mUninstallText;

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
        this.mActionListeners = new ArrayList();
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mUninstallCircle = findViewById(R.id.uninstall_area_circle);
        this.mUninstallIconCircle = findViewById(R.id.uninstall_icon_circle);
        this.mUninstallIcon = findViewById(R.id.uninstall_icon);
        this.mUninstallText = findViewById(R.id.uninstall_text);
        this.mFinishButton = findViewById(R.id.finish_button);
        this.mUninstallApp = findViewById(R.id.uninstall_app_banner);
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
        int i = View.VISIBLE;
        if (!editMode) {
            setBannerUninstallMode(false);
            if (hasFocus()) {
                notifyOnExitEditModeTriggered();
            }
        }
        if (!editMode) {
            i = View.GONE;
        }
        setVisibility(i);
        setAlpha(editMode ? 1.0f : 0.0f);
    }

    public void onSelectedChanged(BannerView v, boolean selected) {
        int i;
        int i2 = View.GONE;
        if (selected) {
            this.mCurSelectedBanner = v;
        } else {
            this.mCurSelectedBanner = null;
        }
        ImageView imageView = this.mUninstallIconCircle;
        if (selected) {
            i = View.VISIBLE;
        } else {
            i = View.GONE;
        }
        imageView.setVisibility(i);
        TextView textView = this.mUninstallText;
        if (selected) {
            i = View.VISIBLE;
        } else {
            i = View.GONE;
        }
        textView.setVisibility(i);
        imageView = this.mUninstallIcon;
        if (selected) {
            i = View.VISIBLE;
        } else {
            i = View.GONE;
        }
        imageView.setVisibility(i);
        imageView = this.mUninstallCircle;
        if (selected) {
            i = View.VISIBLE;
        } else {
            i = View.GONE;
        }
        imageView.setVisibility(i);
        Button button = this.mFinishButton;
        if (!selected) {
            i2 = View.VISIBLE;
        }
        // button.setVisibility(i2); // useless DONE
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

    public void requestUninstallIconFocus(BannerView curView, EditableAppsRowView activeItems) {
        this.mUninstallIcon.requestFocus();
        setBannerUninstallModeWithAnimation(true, curView, activeItems);
    }

    public void setBannerUninstallMode(boolean uninstallMode) {
        this.mUninstallAnimation.setViewsToExitState();
        setUninstallCircleLayout();
        setUninstallIconCircleLayout();
        setUninstallTextLayout();
        this.mUninstallApp.setVisibility(uninstallMode ? View.VISIBLE : View.GONE);
    }

    public void setBannerUninstallModeWithAnimation(boolean uninstallMode, BannerView curView, EditableAppsRowView activeItems) {
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
        Iterator it = this.mActionListeners.iterator();
        while (it.hasNext()) {
            ((EditModeViewActionListener) it.next()).onEditModeExitTriggered();
        }
    }

    private void notifyOnFocusLeavingEditMode(int from) {
        Iterator it = this.mActionListeners.iterator();
        while (it.hasNext()) {
            ((EditModeViewActionListener) it.next()).onFocusLeavingEditModeLayer(from);
        }
    }

    private void notifyUninstallComplete() {
        Iterator it = this.mActionListeners.iterator();
        while (it.hasNext()) {
            ((EditModeViewActionListener) it.next()).onUninstallComplete();
        }
    }

    private void notifyUninstallFailure() {
        Iterator it = this.mActionListeners.iterator();
        while (it.hasNext()) {
            ((EditModeViewActionListener) it.next()).onUninstallFailure();
        }
    }

    private String notifyPrepForUninstall() {
        String packageUninstalling = "";
        Iterator it = this.mActionListeners.iterator();
        while (it.hasNext()) {
            String result = ((EditModeViewActionListener) it.next()).onPrepForUninstall();
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
            if (keyCode == 19 && this.mFinishButton.isFocused()) {
                notifyOnFocusLeavingEditMode(0);
                return true;
            } else if ((keyCode == 19 && this.mUninstallIcon.isFocused()) || (keyCode == 4 && this.mUninstallIcon.isFocused())) {
                notifyOnFocusLeavingEditMode(1);
                return true;
            } else if ((Util.isConfirmKey(keyCode) || keyCode == 4) && this.mFinishButton.isFocused()) {
                notifyOnExitEditModeTriggered();
                return true;
            } else if (!Util.isConfirmKey(keyCode) || !this.mUninstallIcon.isFocused()) {
                return true;
            } else {
                this.mUninstallListener.onUninstallPressed(notifyPrepForUninstall());
                return true;
            }
        } else if (action != 1) {
            return super.dispatchKeyEvent(event);
        } else {
            return true;
        }
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

    public void onClick(View v) {
        if (v == this.mFinishButton) {
            notifyOnExitEditModeTriggered();
        }
    }
}
