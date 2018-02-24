package com.google.android.tvlauncher.appsview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.graphics.Outline;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.tvlauncher.R;
import com.google.android.tvlauncher.util.ContextMenu;
import com.google.android.tvlauncher.util.ContextMenuItem;
import com.google.android.tvlauncher.util.Util;
import com.google.android.tvlauncher.util.porting.Edited;
import com.google.android.tvlauncher.util.porting.Reason;

public class AppBannerView extends BaseBannerView implements View.OnFocusChangeListener, View.OnClickListener, View.OnLongClickListener, ContextMenu.OnItemClickListener {
    protected static final int MENU_FAVORITE = 2;
    protected static final int MENU_INFO = 3;
    protected static final int MENU_MOVE = 1;
    protected static final int MENU_PRIMARY_ACTION = 0;
    protected static final int MENU_UNINSTALL = 4;
    private static final String TAG = "BannerView";
    protected ContextMenu mAppMenu;
    protected final AppsManager mAppsManager;
    protected final float mBannerTitleAlpha;
    private Drawable mFavoriteIcon;
    private final String mFavoriteText;
    private final Handler mHandler;
    protected boolean mIsOemRowBanner;
    protected OnAppsViewActionListener mOnAppsViewActionListener;
    private InstallStateOverlayHelper mOverlayHelper;
    protected TextView mTitleView;
    private Drawable mUnfavoriteIcon;
    private final String mUnfavoriteText;

    public AppBannerView(Context paramContext) {
        this(paramContext, null);
    }

    public AppBannerView(Context paramContext, AttributeSet paramAttributeSet) {
        this(paramContext, paramAttributeSet, 0);
    }

    public AppBannerView(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
        setOnFocusChangeListener(this);
        setOnClickListener(this);
        setOnLongClickListener(this);
        this.mFavoriteText = getContext().getString(R.string.banner_sidebar_favorite_text);
        this.mUnfavoriteText = getContext().getString(R.string.banner_sidebar_unfavorite_text);
        this.mFavoriteIcon = getContext().getDrawable(R.drawable.ic_context_menu_favorite_black);
        this.mUnfavoriteIcon = getContext().getDrawable(R.drawable.ic_context_menu_unfavorite_black);
        this.mAppsManager = AppsManager.getInstance(getContext());
        this.mHandler = new Handler();
        this.mBannerTitleAlpha = getContext().getResources().getFraction(R.fraction.banner_app_title_alpha, 1, 1); // 2131886080
    }

    private void onFavorite() {
        if (this.mOnAppsViewActionListener != null) {
            this.mOnAppsViewActionListener.onToggleFavorite(this.mItem);
        }
    }

    @Edited(reason = Reason.IF_ELSE_DECOMPILE_ERROR)
    private void onPrimaryAction() {
        if ((this.mItem != null) && (this.mItem.getIntent() != null) && (this.mOnAppsViewActionListener != null)) {
            this.mOnAppsViewActionListener.onLaunchApp(this.mItem.getIntent());
        } else if (this.mOnAppsViewActionListener == null) {
            Toast.makeText(getContext(), 2131492990, 0).show();
            if (this.mItem == null) {
                Log.e("BannerView", "Cannot start activity: item was null");
                return;
            }
            if (this.mItem.getIntent() == null) {
                Log.e("BannerView", "Cannot start activity: intent was null for " + this.mItem);
                return;
            }
        } else {
            Log.e("BannerView", "Cannot start activity: no listener for item " + this.mItem);
        }
    }

    private void onShowInfoView() {
        if (this.mOnAppsViewActionListener != null) {
            this.mOnAppsViewActionListener.onShowAppInfo(this.mItem.getPackageName());
        }
    }

    private void onShowUninstall() {
        if (this.mOnAppsViewActionListener != null) {
            this.mOnAppsViewActionListener.onUninstallApp(this.mItem.getPackageName());
        }
    }

    public ContextMenu getAppMenu() {
        return this.mAppMenu;
    }

    public void onClick(View paramView) {
        if (Util.isAccessibilityEnabled(getContext())) {
            onLongClick(paramView);
            return;
        }
        onPrimaryAction();
    }

    protected void onEnterEditMode() {
        OnAppsViewActionListener localOnAppsViewActionListener;
        if ((!this.mIsOemRowBanner) && (this.mItem != null) && (this.mOnAppsViewActionListener != null)) {
            localOnAppsViewActionListener = this.mOnAppsViewActionListener;

            @Edited(reason = Reason.VARIABLE_TERNARY_ERROR)
            int i = this.mItem.isGame() ? 1 : 0; // removed "!"
            localOnAppsViewActionListener.onShowEditModeView(i, AppsManager.getInstance(getContext()).getOrderedPosition(this.mItem));
        }
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mTitleView = ((TextView) findViewById(R.id.app_title));
    }

    public void onFocusChange(View paramView, boolean paramBoolean) {


        if ((this.mAppMenu != null) && (this.mAppMenu.isShowing())) {
            this.mAppMenu.forceDismiss();
        }

        @Edited(reason = Reason.VARIABLE_TERNARY_ERROR)
        float f1 = paramBoolean ? this.mFocusedScale : 1.0F;
        @Edited(reason = Reason.VARIABLE_TERNARY_ERROR, comment = "removed \"!\"")
        float f2 = paramBoolean ? this.mFocusedZDelta : 0.0F;
        @Edited(reason = Reason.VARIABLE_TERNARY_ERROR)
        float f3 = paramBoolean ? this.mBannerTitleAlpha : 0.0F;

        this.mTitleView.setSelected(paramBoolean);
        paramView.animate().z(f2).scaleX(f1).scaleY(f1).setDuration(this.mAnimDuration);
        this.mTitleView.animate().alpha(f3).setDuration(this.mAnimDuration).setListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator paramAnonymousAnimator) {
                if (AppBannerView.this.mTitleView.getAlpha() == 0.0F) {
                    AppBannerView.this.mTitleView.setVisibility(4);
                }
            }

            public void onAnimationStart(Animator paramAnonymousAnimator) {
                AppBannerView.this.mTitleView.setVisibility(0);
            }
        });
    }

    @Edited(reason = Reason.SWITCH_COMPILE_ERROR, comment = "added onShowUninstall in default")
    public void onItemClick(ContextMenuItem paramContextMenuItem) {
        switch (paramContextMenuItem.getId()) {
            case MENU_PRIMARY_ACTION:
                onPrimaryAction();
                return;
            case MENU_INFO:
                onShowInfoView();
                return;
            case MENU_FAVORITE:
                onFavorite();
                return;
            case MENU_MOVE:
                onEnterEditMode();
                return;
            case MENU_UNINSTALL:
                onShowUninstall();
                return;
            default:
        }
    }

    public boolean onLongClick(View paramView) {
        @Edited(reason = Reason.VARIABLE_NAMING_CLARITY)
        boolean isFavorite = this.mAppsManager.isFavorite(this.mItem);
        this.mAppMenu = new ContextMenu((Activity) paramView.getContext(), this.mBannerImage, this.mCornerRadius, getScaleX(), getScaleY());
        this.mAppMenu.addItem(new ContextMenuItem(MENU_PRIMARY_ACTION, paramView.getContext().getString(R.string.banner_sidebar_primary_action_text), paramView.getContext().getDrawable(R.drawable.ic_context_menu_open_black)));
        this.mAppMenu.addItem(new ContextMenuItem(MENU_MOVE, paramView.getContext().getString(R.string.banner_sidebar_move_text), paramView.getContext().getDrawable(R.drawable.ic_context_menu_move_black)));
        ContextMenu localContextMenu = this.mAppMenu;

        @Edited(reason = Reason.IF_ELSE_DECOMPILE_ERROR)
        String str = isFavorite ? this.mFavoriteText : this.mUnfavoriteText;
        @Edited(reason = Reason.IF_ELSE_DECOMPILE_ERROR)
        Drawable localDrawable = isFavorite ? this.mFavoriteIcon : this.mUnfavoriteIcon;

        localContextMenu.addItem(new ContextMenuItem(MENU_FAVORITE, str, localDrawable));
        this.mAppMenu.addItem(new ContextMenuItem(MENU_INFO, paramView.getContext().getString(R.string.banner_sidebar_info_text), paramView.getContext().getDrawable(R.drawable.ic_context_menu_info_black)));
        this.mAppMenu.addItem(new ContextMenuItem(MENU_UNINSTALL, paramView.getContext().getString(R.string.banner_sidebar_uninstall_text), paramView.getContext().getDrawable(R.drawable.ic_context_menu_uninstall_black)));
        this.mAppMenu.setOnMenuItemClickListener(this);
        setMenuItems();
        this.mAppMenu.show();
        return true;
    }

    @Edited(reason = Reason.FOR_WHILE_MIXUP)
    public void setAppBannerItems(LaunchItem paramLaunchItem, boolean paramBoolean, OnAppsViewActionListener paramOnAppsViewActionListener) {
        setLaunchItem(paramLaunchItem);
        this.mTitleView.setText(paramLaunchItem.getLabel());
        this.mIsOemRowBanner = paramBoolean;
        this.mOnAppsViewActionListener = paramOnAppsViewActionListener;
        if (this.mItem.isInstalling()) {
            if (this.mOverlayHelper == null) {
                this.mOverlayHelper = new InstallStateOverlayHelper(this);
            }
            this.mOverlayHelper.updateOverlay(this.mItem);
        }

        if (this.mOverlayHelper == null) {
            return;
        }
        this.mOverlayHelper.removeOverlay();
    }

    @Edited(reason = Reason.IF_ELSE_DECOMPILE_ERROR)
    protected void setMenuItems() {
        boolean bool2 = true;
        ContextMenuItem localContextMenuItem;
        if (this.mItem.isGame()) {
            localContextMenuItem = this.mAppMenu.findItem(2);

            if ((this.mAppsManager.isFavoritesFull()) && (!this.mAppsManager.isFavorite(this.mItem))) {
                localContextMenuItem.setEnabled(true);
            }

            localContextMenuItem = this.mAppMenu.findItem(3);
            if (this.mItem.isInstalling()) {
                localContextMenuItem.setEnabled(true);
            }

            boolean isOnlyGame = this.mAppsManager.isOnlyGame(this.mItem) || (this.mIsOemRowBanner);
            localContextMenuItem = this.mAppMenu.findItem(1);
            localContextMenuItem.setEnabled(isOnlyGame);

            // this.mAppsManager.isOnlyApp(this.mItem);

            localContextMenuItem = this.mAppMenu.findItem(4);
            if ((LaunchItem.isSystemApp(getContext(), this.mItem.getPackageName())) || (this.mItem.isInstalling())) {
                localContextMenuItem.setEnabled(true); // added true?
            }
        }
    }

    private static class InstallStateOverlayHelper {
        private final AppBannerView mBanner;
        private final ImageView mIconView;
        private final View mOverlay;
        private final ProgressBar mProgressBar;
        private final TextView mProgressView;
        private final TextView mStateView;

        InstallStateOverlayHelper(AppBannerView paramAppBannerView) {
            this.mOverlay = LayoutInflater.from(paramAppBannerView.getContext()).inflate(2130968625, paramAppBannerView, false);
            final float f = paramAppBannerView.getResources().getDimensionPixelSize(R.dimen.card_rounded_corner_radius);
            this.mOverlay.setOutlineProvider(new ViewOutlineProvider() {
                public void getOutline(View paramAnonymousView, Outline paramAnonymousOutline) {
                    paramAnonymousOutline.setRoundRect(0, 0, paramAnonymousView.getWidth(), paramAnonymousView.getHeight(), f);
                }
            });
            this.mOverlay.setClipToOutline(true);
            this.mStateView = ((TextView) this.mOverlay.findViewById(R.id.banner_install_state));
            this.mProgressView = ((TextView) this.mOverlay.findViewById(R.id.banner_install_progress));
            this.mProgressBar = ((ProgressBar) this.mOverlay.findViewById(R.id.progress_bar));
            this.mIconView = ((ImageView) this.mOverlay.findViewById(R.id.app_install_icon));
            this.mBanner = paramAppBannerView;
        }

        void removeOverlay() {
            if (this.mOverlay.getParent() != null) {
                this.mBanner.removeView(this.mOverlay);
            }
        }

        void updateOverlay(LaunchItem paramLaunchItem) {
            this.mStateView.setText(paramLaunchItem.getInstallStateString(this.mStateView.getContext()));
            this.mProgressView.setText(paramLaunchItem.getInstallProgressString(this.mProgressView.getContext()));

            int i = paramLaunchItem.getInstallProgressPercent();

            if (i == -1) {
                this.mProgressBar.setIndeterminate(true);
            } else {
                this.mProgressBar.setProgress(i);
                this.mProgressBar.setIndeterminate(false);
            }

            this.mIconView.setImageDrawable(paramLaunchItem.getItemDrawable());

            if (this.mOverlay.getParent() == null) {
                this.mBanner.addView(this.mOverlay);
            }
        }
    }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/appsview/AppBannerView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */