package com.amazon.tv.leanbacklauncher.apps;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amazon.tv.firetv.leanbacklauncher.apps.RowPreferences;
import com.amazon.tv.leanbacklauncher.animation.AppViewFocusAnimator;
import com.amazon.tv.leanbacklauncher.animation.ParticipatesInLaunchAnimation;
import com.amazon.tv.leanbacklauncher.animation.ParticipatesInScrollAnimation;
import com.amazon.tv.leanbacklauncher.animation.ViewDimmer;
import com.amazon.tv.leanbacklauncher.DimmableItem;
import com.amazon.tv.leanbacklauncher.EditableAppsRowView;
import com.amazon.tv.leanbacklauncher.R;
import com.amazon.tv.leanbacklauncher.RoundedRectOutlineProvider;
import com.amazon.tv.leanbacklauncher.widget.EditModeManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class BannerView extends FrameLayout implements OnLongClickListener, DimmableItem, ParticipatesInLaunchAnimation, ParticipatesInScrollAnimation, OnEditModeChangedListener {
    private RoundedRectOutlineProvider sOutline; // was static
    private View mAppBanner;
    private ViewDimmer mDimmer;
    private ImageView mEditFocusFrame;
    private ImageView mFocusFrame;
    private OnEditModeChangedListener mEditListener;
    private boolean mEditMode;
    private EditModeManager mEditModeManager;
    private AppViewFocusAnimator mFocusAnimator;
    private View mInstallStateOverlay;
    private BannerView mLastFocusedBanner;
    private boolean mLeavingEditMode;
    private ArrayList<BannerSelectedChangedListener> mSelectedListeners;
    private AppsAdapter.AppViewHolder mViewHolder;

    public BannerView(Context context) {
        this(context, null);
    }

    public BannerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BannerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mSelectedListeners = new ArrayList();
        this.mFocusAnimator = new AppViewFocusAnimator(this);
        this.mSelectedListeners.add(this.mFocusAnimator);
        this.mEditModeManager = EditModeManager.getInstance();
        setSelected(false);
        setOnLongClickListener(this);
        if (sOutline == null) {
            //sOutline = new RoundedRectOutlineProvider((float) getResources().getDimensionPixelOffset(R.dimen.banner_corner_radius));
            sOutline = new RoundedRectOutlineProvider((float) RowPreferences.getCorners(context));
        }
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        Context ctx = getContext();
        int width = (int) getResources().getDimensionPixelSize(R.dimen.banner_width);
        int height = (int) getResources().getDimensionPixelSize(R.dimen.banner_height);
        int size = (int) RowPreferences.getBannersSize(ctx); // 50 - 200
        this.getLayoutParams().height = height * size / 100; // px
        this.getLayoutParams().width = width * size / 100; // px
        this.requestLayout(); // set new BannerView dimensions
        this.mDimmer = new ViewDimmer(this);
        this.mAppBanner = findViewById(R.id.app_banner);
        this.mInstallStateOverlay = findViewById(R.id.install_state_overlay);
        if (this.mAppBanner instanceof ImageView) {
            this.mAppBanner.setOutlineProvider(sOutline);
            this.mAppBanner.setClipToOutline(true);
            this.mDimmer.addDimTarget((ImageView) this.mAppBanner);
        } else {
            if (this.mAppBanner instanceof LinearLayout) {
                this.mAppBanner.setOutlineProvider(sOutline);
                this.mAppBanner.setClipToOutline(true);
                this.mAppBanner.setBackgroundColor(getRandomColor()); // FIXME! Extract color from APK
            }
            View inputBannerView = findViewById(R.id.input_banner);
            if (inputBannerView != null) {
                inputBannerView.setOutlineProvider(sOutline);
                inputBannerView.setClipToOutline(true);
            }
            View bannerView = findViewById(R.id.banner_icon);
            if (bannerView instanceof ImageView) {
                this.mDimmer.addDimTarget((ImageView) bannerView);
            }
            View bannerLabel = findViewById(R.id.banner_label);
            if (bannerLabel instanceof TextView) {
                this.mDimmer.addDimTarget((TextView) bannerLabel);
            }
            bannerView = findViewById(R.id.input_image);
            if (bannerView instanceof ImageView) {
                bannerView.setOutlineProvider(sOutline);
                bannerView.setClipToOutline(true);
                this.mDimmer.addDimTarget((ImageView) bannerView);
            }
            bannerLabel = findViewById(R.id.input_label);
            if (bannerLabel instanceof TextView) {
                this.mDimmer.addDimTarget((TextView) bannerLabel);
            }
            if (getBackground() != null) {
                this.mDimmer.addDimTarget(getBackground());
            }
        }
        this.mDimmer.setDimLevelImmediate();
        float radius = (float) RowPreferences.getCorners(ctx); // (float) getResources().getDimensionPixelOffset(R.dimen.banner_corner_radius);
        int stroke = 2;
        int color = (int) getResources().getColor(R.color.edit_selection_indicator_color);
        GradientDrawable gd = null;
        // edit focus frame (edit_frame_width : edit_frame_height)
        View efv = findViewById(R.id.edit_focused_frame);
        if (efv instanceof ImageView) {
            this.mEditFocusFrame = (ImageView) efv;
            efv.getLayoutParams().width = (int) getResources().getDimensionPixelSize(R.dimen.edit_frame_width) * size / 100;
            efv.getLayoutParams().height = (int) getResources().getDimensionPixelSize(R.dimen.edit_frame_height) * size / 100;
            efv.requestLayout(); // set new edit focus frame dimensions
            gd = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,new int[]{Color.TRANSPARENT,Color.TRANSPARENT,Color.TRANSPARENT});
            gd.setShape(GradientDrawable.RECTANGLE);
            gd.setStroke(stroke, color); // fixed width
            gd.setCornerRadius(radius + ((getResources().getDimensionPixelSize(R.dimen.edit_frame_width) - width) * size / 100) / 2);
            this.mEditFocusFrame.setImageDrawable(gd); // set new edit frame drawable
        }
        // focus frame (banner_frame_width : banner_frame_height)
        View ffv = findViewById(R.id.banner_focused_frame);
        if (ffv instanceof ImageView) {
            this.mFocusFrame = (ImageView) ffv;
            stroke = (int) RowPreferences.getFrameWidth(ctx); // (int) getResources().getDimensionPixelSize(R.dimen.banner_frame_stroke);
            color = (int) RowPreferences.getFrameColor(ctx); // (int) getResources().getColor(R.color.banner_focus_frame_color);
            ffv.getLayoutParams().width = (width + 2 * stroke - (int) radius / 2) * size / 100; // px
            ffv.getLayoutParams().height = (height + 2 * stroke - (int) radius / 2) * size / 100; // px
            ffv.requestLayout(); // set new focus frame dimensions
            gd = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,new int[]{Color.TRANSPARENT,Color.TRANSPARENT,Color.TRANSPARENT});
            gd.setShape(GradientDrawable.RECTANGLE);
            gd.setStroke(stroke * size / 100, color); // setStroke(10, Color.BLACK);
            gd.setCornerRadius(radius); // setCornerRadius(10f);
            this.mFocusFrame.setImageDrawable(gd); // set new focus frame drawable
        }
    }

    public ViewDimmer getViewDimmer() {
        return this.mDimmer;
    }

    public void setViewHolder(AppsAdapter.AppViewHolder viewHolder) {
        this.mViewHolder = viewHolder;
    }

    public void setTextViewColor(TextView textView, int color) {
        if (this.mDimmer != null) {
            this.mDimmer.setTargetTextColor(textView, color);
        }
    }

    public boolean isEditMode() {
        return this.mEditMode;
    }

    public void setEditMode(boolean editMode) {
        if (isEditable()) {
            this.mEditMode = editMode;
        }
        if (!editMode) {
            this.mLastFocusedBanner = null;
        }
    }

    private void setEditMode() {
        if (this.mEditListener != null) {
            this.mEditListener.onEditModeChanged(this.mEditMode);
        }
    }

    public void addSelectedListener(BannerSelectedChangedListener listener) {
        this.mSelectedListeners.add(listener);
    }

    public void clearBannerForRecycle() {
        clearFocus();
        this.mEditFocusFrame.setVisibility(8);
    }

    public void removeSelectedListener(BannerSelectedChangedListener listener) {
        this.mSelectedListeners.remove(listener);
    }

    public void setOnEditModeChangedListener(OnEditModeChangedListener listener) {
        this.mEditListener = listener;
    }

    public void setFocusedFrameState() {
        if (!isEditable()) {
            return;
        }
        if (this.mEditMode && hasFocus()) {
            this.mDimmer.setDimState(ViewDimmer.DimState.ACTIVE, false);
            if (isSelected()) {
                this.mEditFocusFrame.setVisibility(8);
                return;
            }
            this.mEditFocusFrame.setVisibility(0); // 0 - VISIBLE. 8 - INVISIBLE
            post(new Runnable() {
                public void run() {
                    BannerView.this.requestLayout();
                }
            });
            return;
        }
        this.mEditFocusFrame.setVisibility(8);
    }

    public void onEditModeChanged(boolean editMode) {
        if (isEditable()) {
            this.mEditMode = editMode;
            if (hasFocus()) {
                if (!editMode) {
                    this.mLeavingEditMode = true;
                }
                setSelected(editMode);
                setFocusedFrameState();
                this.mFocusAnimator.onEditModeChanged(this, editMode);
            }
        }
    }

    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        if (this.mEditMode && gainFocus && isEditModeSelected()) {
            setSelected(true);
            sendAccessibilityEvent(8);
        }
        setFocusedFrameState();
        // focus outline
        if (this.mFocusFrame != null) {
            if (hasFocus())
                this.mFocusFrame.setVisibility(0);
            else
                this.mFocusFrame.setVisibility(8);
        }
    }

    public boolean requestFocus(int direction, Rect previous) {
        if (this.mLastFocusedBanner == null || this.mLastFocusedBanner == this) {
            return super.requestFocus(direction, previous);
        }
        this.mLastFocusedBanner.requestFocus();
        return false;
    }

    public void sendAccessibilityEvent(int eventType) {
        if (!this.mEditMode || isSelected() || this.mEditModeManager.getSelectedComponentName() == null) {
            super.sendAccessibilityEvent(eventType);
        }
    }

    public boolean isEditable() {
        ViewParent parent = getParent();
        if (parent instanceof EditableAppsRowView) {
            return ((EditableAppsRowView) parent).isRowEditable();
        }
        return false;
    }

    public void onClickInEditMode() {
        boolean z = false;
        if (isEditable()) {
            notifyEditModeManager(false);
            if (!isSelected()) {
                z = true;
            }
            setSelected(z);
        }
    }

    public boolean onLongClick(View v) {
        if (isEditable() && !this.mEditMode) {
            this.mEditMode = true;
            setSelected(true);
            setEditMode();
            return true;
        } else if (!isEditable() || !this.mEditMode) {
            return false;
        } else {
            onClickInEditMode();
            return true;
        }
    }

    public void setSelected(boolean selected) {
        if (this.mEditMode || this.mLeavingEditMode) {
            if (selected != isSelected()) {
                super.setSelected(selected);
                setFocusedFrameState();
                if (!(this.mSelectedListeners.isEmpty() || this.mLeavingEditMode)) {
                    Iterator it = this.mSelectedListeners.iterator();
                    while (it.hasNext()) {
                        ((BannerSelectedChangedListener) it.next()).onSelectedChanged(this, selected);
                    }
                }
                if (selected) {
                    notifyEditModeManager(selected);
                }
                if (Log.isLoggable("LauncherEditMode", 2)) {
                    Log.d("LauncherEditMode", "BannerView selected is now: " + isSelected());
                }
            }
            this.mLeavingEditMode = false;
        }
    }

    private boolean isEditModeSelected() {
        return TextUtils.equals(this.mViewHolder.getComponentName(), this.mEditModeManager.getSelectedComponentName());
    }

    public void notifyEditModeManager(boolean selected) {
        if (selected) {
            this.mEditModeManager.setSelectedComponentName(this.mViewHolder.getComponentName());
        } else {
            this.mEditModeManager.setSelectedComponentName(null);
        }
    }

    public void setDimState(ViewDimmer.DimState dimState, boolean immediate) {
        this.mDimmer.setDimState(dimState, immediate);
    }

    public void setLastFocusedBanner(BannerView view) {
        this.mLastFocusedBanner = view;
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        clearAnimation();
        this.mDimmer.setDimLevelImmediate();
        this.mFocusAnimator.setFocusImmediate(hasFocus());
        setAnimationsEnabled(true);
    }

    public void setZ(float z) {
        if ((this.mAppBanner instanceof ImageView) || (this.mAppBanner instanceof LinearLayout)) {
            this.mAppBanner.setZ(z);
            this.mInstallStateOverlay.setZ(z);
            return;
        }
        super.setZ(z);
    }

    public void setAnimationsEnabled(boolean enabled) {
        this.mFocusAnimator.setEnabled(enabled);
        this.mDimmer.setAnimationEnabled(enabled);
    }

    // todo
    public AppsAdapter.AppViewHolder getViewHolder() {
        return mViewHolder;
    }
    
	private int getRandomColor() {
		Random rnd = new Random();
		return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
	}

}