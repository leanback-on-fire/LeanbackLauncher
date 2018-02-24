package com.google.android.tvlauncher.appsview;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Outline;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewOutlineProvider;
import android.view.ViewPropertyAnimator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.tvlauncher.R;
import com.google.android.tvlauncher.util.porting.Edited;
import com.google.android.tvlauncher.util.porting.Reason;

public class StoreRowButtonView
        extends LinearLayout
        implements View.OnClickListener, View.OnFocusChangeListener {
    private final int mAnimDuration;
    private final int mCornerRadius;
    private final int mElevation;
    private final int mFocusedFillColor = ContextCompat.getColor(getContext(), 2131820736);
    private final float mFocusedScale;
    private OnAppsViewActionListener mOnAppsViewActionListener;
    private ImageView mStoreIconView;
    private LaunchItem mStoreItem;
    private TextView mStoreTitleView;
    private final int mUnfocusedFillColor = ContextCompat.getColor(getContext(), 2131820737);

    public StoreRowButtonView(Context paramContext) {
        this(paramContext, null);
    }

    public StoreRowButtonView(Context paramContext, @Nullable AttributeSet paramAttributeSet) {
        this(paramContext, paramAttributeSet, 0);
    }

    public StoreRowButtonView(Context paramContext, @Nullable AttributeSet paramAttributeSet, int paramInt) {
        this(paramContext, paramAttributeSet, paramInt, 0);
    }

    public StoreRowButtonView(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2) {
        super(paramContext, paramAttributeSet, paramInt1, paramInt2);
        Resources res = getResources();
        this.mFocusedScale = res.getFraction(R.fraction.store_button_focused_scale, 1, 1); // 2131886098
        this.mAnimDuration = res.getInteger(R.integer.banner_scale_anim_duration);
        this.mCornerRadius = res.getDimensionPixelSize(R.dimen.card_rounded_corner_radius);
        this.mElevation = res.getDimensionPixelSize(R.dimen.store_button_z);
        setOutlineProvider(new ViewOutlineProvider() {
            public void getOutline(View paramAnonymousView, Outline paramAnonymousOutline) {
                paramAnonymousOutline.setRoundRect(0, 0, StoreRowButtonView.this.getWidth(), StoreRowButtonView.this.getHeight(), StoreRowButtonView.this.mCornerRadius);
            }
        });
        setClipToOutline(true);
        setOnClickListener(this);
        setOnFocusChangeListener(this);
    }

    public void onClick(View paramView) {
        this.mOnAppsViewActionListener.onStoreLaunch(this.mStoreItem.getIntent());
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mStoreIconView = ((ImageView) findViewById(R.id.store_icon));
        this.mStoreTitleView = ((TextView) findViewById(R.id.store_title));
    }

    @Edited(reason = Reason.IF_ELSE_DECOMPILE_ERROR)
    public void onFocusChange(View paramView, boolean paramBoolean) {
        float f;
        int i;
        int j;

        if (paramBoolean) {
            f = this.mFocusedScale;
            i = this.mUnfocusedFillColor;
            j = this.mFocusedFillColor;
        } else {
            f = 1.0F;
            i = this.mFocusedFillColor;
            j = this.mUnfocusedFillColor;
        }

        int k = paramBoolean ? this.mElevation : 0;

        ValueAnimator localValueAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), i, j);
        localValueAnimator.setDuration(this.mAnimDuration);
        localValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator) {
                StoreRowButtonView.this.setBackgroundColor((Integer) paramAnonymousValueAnimator.getAnimatedValue());
            }
        });
        localValueAnimator.start();
        paramView.animate().scaleX(f).scaleY(f).translationZ(k).setDuration(this.mAnimDuration);
    }

    public void setStoreItem(LaunchItem paramLaunchItem, OnAppsViewActionListener paramOnAppsViewActionListener) {
        this.mStoreItem = paramLaunchItem;
        this.mStoreIconView.setImageDrawable(this.mStoreItem.getIcon());
        AppsManager.getInstance(getContext());
        if (AppsManager.checkIfAppStore(paramLaunchItem.getPackageName())) {
            this.mStoreTitleView.setText(2131493078);
        } else {
            this.mStoreTitleView.setText(2131493079);
        }

        this.mOnAppsViewActionListener = paramOnAppsViewActionListener;
    }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/appsview/StoreRowButtonView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */