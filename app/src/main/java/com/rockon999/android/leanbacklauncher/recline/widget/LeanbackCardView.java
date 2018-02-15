package com.rockon999.android.leanbacklauncher.recline.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewDebug.IntToString;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;

import com.rockon999.android.leanbacklauncher.R;

import java.util.ArrayList;

public class LeanbackCardView extends ViewGroup {
    private  int mActivatedAnimDuration;
    private Animation mAnim;
    private  Runnable mAnimationTrigger;
    private boolean mCardSelected;
    private int mCardType;
    private boolean mDelaySelectedAnim;
    private ArrayList<View> mExtraViewList;
    private float mInfoAlpha;
    private float mInfoOffset;
    private boolean mInfoOnSelected;
    private ArrayList<View> mInfoViewList;
    private float mInfoVisFraction;
    private ArrayList<View> mMainViewList;
    private int mMeasuredHeight;
    private int mMeasuredWidth;
    private int mSelectedAnimDuration;
    private int mSelectedAnimationDelay;

    /* renamed from: com.rockon999.android.recline.widget.LeanbackCardView.2 */
    class C02232 implements AnimationListener {
        C02232() {
        }

        public void onAnimationStart(Animation animation) {
        }

        public void onAnimationEnd(Animation animation) {
            if (LeanbackCardView.this.mInfoOffset == 0.0f) {
                for (int i = 0; i < LeanbackCardView.this.mExtraViewList.size(); i++) {
                    LeanbackCardView.this.mExtraViewList.get(i).setVisibility(8);
                }
            }
        }

        public void onAnimationRepeat(Animation animation) {
        }
    }

    /* renamed from: com.rockon999.android.recline.widget.LeanbackCardView.3 */
    class C02243 implements AnimationListener {
        C02243() {
        }

        public void onAnimationStart(Animation animation) {
        }

        public void onAnimationEnd(Animation animation) {
            if (LeanbackCardView.this.mInfoOffset == 0.0f) {
                for (int i = 0; i < LeanbackCardView.this.mExtraViewList.size(); i++) {
                    LeanbackCardView.this.mExtraViewList.get(i).setVisibility(8);
                }
            }
        }

        public void onAnimationRepeat(Animation animation) {
        }
    }

    /* renamed from: com.rockon999.android.recline.widget.LeanbackCardView.4 */
    class C02254 implements AnimationListener {
        C02254() {
        }

        public void onAnimationStart(Animation animation) {
        }

        public void onAnimationEnd(Animation animation) {
            if (((double) LeanbackCardView.this.mInfoAlpha) == 0.0d) {
                for (int i = 0; i < LeanbackCardView.this.mInfoViewList.size(); i++) {
                    LeanbackCardView.this.mInfoViewList.get(i).setVisibility(8);
                }
            }
        }

        public void onAnimationRepeat(Animation animation) {
        }
    }

    private class InfoAlphaAnimation extends Animation {
        private float mDelta;
        private float mStartValue;

        public InfoAlphaAnimation(float start, float end) {
            this.mStartValue = start;
            this.mDelta = end - start;
        }

        protected void applyTransformation(float interpolatedTime, Transformation t) {
            LeanbackCardView.this.mInfoAlpha = this.mStartValue + (this.mDelta * interpolatedTime);
            for (int i = 0; i < LeanbackCardView.this.mInfoViewList.size(); i++) {
                LeanbackCardView.this.mInfoViewList.get(i).setAlpha(LeanbackCardView.this.mInfoAlpha);
            }
        }
    }

    private class InfoHeightAnimation extends Animation {
        private float mDelta;
        private float mStartValue;

        public InfoHeightAnimation(float start, float end) {
            this.mStartValue = start;
            this.mDelta = end - start;
        }

        protected void applyTransformation(float interpolatedTime, Transformation t) {
            LeanbackCardView.this.mInfoVisFraction = this.mStartValue + (this.mDelta * interpolatedTime);
            LeanbackCardView.this.requestLayout();
        }
    }

    private class InfoOffsetAnimation extends Animation {
        private float mDelta;
        private float mStartValue;

        public InfoOffsetAnimation(float start, float end) {
            this.mStartValue = start;
            this.mDelta = end - start;
        }

        protected void applyTransformation(float interpolatedTime, Transformation t) {
            LeanbackCardView.this.mInfoOffset = this.mStartValue + (this.mDelta * interpolatedTime);
            LeanbackCardView.this.requestLayout();
        }
    }

    public static class LayoutParams extends ViewGroup.LayoutParams {
        @ExportedProperty(category = "layout", mapping = {@IntToString(from = 0, to = "MAIN"), @IntToString(from = 1, to = "INFO"), @IntToString(from = 2, to = "EXTRA")})
        public int viewType;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            this.viewType = 0;
            //android.support.v17.leanback.R.styleable.LeanbackTheme_baseCardViewStyle
            //TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.LeanbackCardView_Layout);
            //this.viewType = a.getInt(R.styleable.LeanbackCardView_Layout_lbLayout_viewType, 0);
            TypedArray a = c.obtainStyledAttributes(attrs, new int[]{R.styleable.LeanbackTheme_baseCardViewStyle});
            this.viewType = a.getInt(R.styleable.LeanbackTheme_baseCardViewStyle, 0);
            a.recycle();
        }

        public LayoutParams(int width, int height) {
            super(width, height);
            this.viewType = 0;
        }

        public LayoutParams(ViewGroup.LayoutParams p) {
            super(p);
            this.viewType = 0;
        }

        public LayoutParams(LayoutParams source) {
            super(source);
            this.viewType = 0;
            this.viewType = source.viewType;
        }
    }

    public void setActivated(boolean activated) {
        if (activated != isActivated()) {
            super.setActivated(activated);
            applyActiveState(isActivated());
        }
    }

    public void setSelected(boolean selected) {
        if (this.mCardSelected != selected) {
            this.mCardSelected = selected;
            applySelectedState(this.mCardSelected);
        }
    }

    public boolean isSelected() {
        return this.mCardSelected;
    }

    public boolean shouldDelayChildPressedState() {
        return false;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int i;
        this.mMeasuredWidth = 0;
        this.mMeasuredHeight = 0;
        int state = 0;
        int mainHeight = 0;
        int infoHeight = 0;
        int extraHeight = 0;
        boolean infoOnSelected = this.mInfoOnSelected && (this.mCardType == 2 || this.mCardType == 1);
        findChildrenViews();
        int unspecifiedSpec = MeasureSpec.makeMeasureSpec(0, 0);
        for (i = 0; i < this.mMainViewList.size(); i++) {
            View mainView = this.mMainViewList.get(i);
            if (mainView.getVisibility() != 8) {
                measureChild(mainView, unspecifiedSpec, unspecifiedSpec);
                this.mMeasuredWidth = Math.max(this.mMeasuredWidth, mainView.getMeasuredWidth());
                mainHeight += mainView.getMeasuredHeight();
                state = View.combineMeasuredStates(state, mainView.getMeasuredState());
            }
        }
        int cardWidthMeasureSpec = MeasureSpec.makeMeasureSpec(this.mMeasuredWidth, 1073741824);
        if (this.mCardType == 1 || this.mCardType == 2 || this.mCardType == 3) {
            for (i = 0; i < this.mInfoViewList.size(); i++) {
                View infoView = this.mInfoViewList.get(i);
                if (infoView.getVisibility() != 8) {
                    measureChild(infoView, cardWidthMeasureSpec, unspecifiedSpec);
                    if (this.mCardType != 1) {
                        infoHeight += infoView.getMeasuredHeight();
                    }
                    state = View.combineMeasuredStates(state, infoView.getMeasuredState());
                }
            }
            if (this.mCardType == 3) {
                for (i = 0; i < this.mExtraViewList.size(); i++) {
                    View extraView = this.mExtraViewList.get(i);
                    if (extraView.getVisibility() != 8) {
                        measureChild(extraView, cardWidthMeasureSpec, unspecifiedSpec);
                        extraHeight += extraView.getMeasuredHeight();
                        state = View.combineMeasuredStates(state, extraView.getMeasuredState());
                    }
                }
            }
        }
        this.mMeasuredHeight = (int) ((((float) extraHeight) + ((infoOnSelected ? ((float) infoHeight) * this.mInfoVisFraction : (float) infoHeight) + ((float) mainHeight))) - (infoOnSelected ? 0.0f : this.mInfoOffset));
        setMeasuredDimension(View.resolveSizeAndState((this.mMeasuredWidth + getPaddingLeft()) + getPaddingRight(), widthMeasureSpec, state), View.resolveSizeAndState((this.mMeasuredHeight + getPaddingTop()) + getPaddingBottom(), heightMeasureSpec, state << 16));
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int i;
        float currBottom = (float) getPaddingTop();
        for (i = 0; i < this.mMainViewList.size(); i++) {
            View mainView = this.mMainViewList.get(i);
            if (mainView.getVisibility() != 8) {
                mainView.layout(getPaddingLeft(), (int) currBottom, this.mMeasuredWidth + getPaddingLeft(), (int) (((float) mainView.getMeasuredHeight()) + currBottom));
                currBottom += (float) mainView.getMeasuredHeight();
            }
        }
        if (this.mCardType != 0) {
            float infoHeight = 0.0f;
            for (i = 0; i < this.mInfoViewList.size(); i++) {
                infoHeight += (float) this.mInfoViewList.get(i).getMeasuredHeight();
            }
            if (this.mCardType == 1) {
                currBottom -= infoHeight;
                if (currBottom < 0.0f) {
                    currBottom = 0.0f;
                }
            } else if (this.mCardType != 2) {
                currBottom -= this.mInfoOffset;
            } else if (this.mInfoOnSelected) {
                infoHeight *= this.mInfoVisFraction;
            }
            for (i = 0; i < this.mInfoViewList.size(); i++) {
                View infoView = this.mInfoViewList.get(i);
                if (infoView.getVisibility() != 8) {
                    int viewHeight = infoView.getMeasuredHeight();
                    if (((float) viewHeight) > infoHeight) {
                        viewHeight = (int) infoHeight;
                    }
                    infoView.layout(getPaddingLeft(), (int) currBottom, this.mMeasuredWidth + getPaddingLeft(), (int) (((float) viewHeight) + currBottom));
                    currBottom += (float) viewHeight;
                    infoHeight -= (float) viewHeight;
                    if (infoHeight <= 0.0f) {
                        break;
                    }
                }
            }
            if (this.mCardType == 3) {
                for (i = 0; i < this.mExtraViewList.size(); i++) {
                    View extraView = this.mExtraViewList.get(i);
                    if (extraView.getVisibility() != 8) {
                        extraView.layout(getPaddingLeft(), (int) currBottom, this.mMeasuredWidth + getPaddingLeft(), (int) (((float) extraView.getMeasuredHeight()) + currBottom));
                        currBottom += (float) extraView.getMeasuredHeight();
                    }
                }
            }
        }
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks(this.mAnimationTrigger);
        cancelAnimations();
        this.mInfoOffset = 0.0f;
        this.mInfoVisFraction = 0.0f;
    }

    private void findChildrenViews() {
        this.mMainViewList.clear();
        this.mInfoViewList.clear();
        this.mExtraViewList.clear();
        int count = getChildCount();
        boolean infoVisible = false;
        boolean extraVisible = false;
        if (this.mCardType == 1) {
            infoVisible = this.mInfoOnSelected ? this.mCardSelected : isActivated();
        } else if (this.mCardType == 2) {
            infoVisible = this.mInfoOnSelected ? this.mInfoVisFraction > 0.0f : isActivated();
        } else if (this.mCardType == 3) {
            infoVisible = isActivated();
            extraVisible = this.mInfoOffset > 0.0f;
        }
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child != null) {
                LayoutParams lp = (LayoutParams) child.getLayoutParams();
                if (lp.viewType == 1) {
                    this.mInfoViewList.add(child);
                    child.setVisibility(infoVisible ? 0 : 8);
                } else if (lp.viewType == 2) {
                    this.mExtraViewList.add(child);
                    child.setVisibility(extraVisible ? 0 : 8);
                } else {
                    this.mMainViewList.add(child);
                    child.setVisibility(0);
                }
            }
        }
    }

    private void applyActiveState(boolean active) {
        if (this.mCardType != 3) {
            if (!this.mInfoOnSelected) {
                if (!(this.mCardType == 2 || this.mCardType == 1)) {
                    return;
                }
            }
            return;
        }
        setInfoVisibility(active);
    }

    private void setInfoVisibility(boolean visible) {
        int i;
        if (this.mCardType == 3) {
            if (visible) {
                for (i = 0; i < this.mInfoViewList.size(); i++) {
                    this.mInfoViewList.get(i).setVisibility(0);
                }
                return;
            }
            for (i = 0; i < this.mInfoViewList.size(); i++) {
                this.mInfoViewList.get(i).setVisibility(8);
            }
            for (i = 0; i < this.mExtraViewList.size(); i++) {
                this.mExtraViewList.get(i).setVisibility(8);
            }
            this.mInfoOffset = 0.0f;
        } else if (this.mCardType == 2) {
            if (this.mInfoOnSelected) {
                animateInfoHeight(visible);
                return;
            }
            for (i = 0; i < this.mInfoViewList.size(); i++) {
                int i2;
                View view = this.mInfoViewList.get(i);
                if (visible) {
                    i2 = 0;
                } else {
                    i2 = 8;
                }
                view.setVisibility(i2);
            }
        } else if (this.mCardType == 1) {
            animateInfoAlpha(visible);
        }
    }

    private void applySelectedState(boolean focused) {
        removeCallbacks(this.mAnimationTrigger);
        if (this.mCardType == 3) {
            if (!focused) {
                animateInfoOffset(false);
            } else if (this.mDelaySelectedAnim) {
                postDelayed(this.mAnimationTrigger, (long) this.mSelectedAnimationDelay);
            } else {
                post(this.mAnimationTrigger);
                this.mDelaySelectedAnim = true;
            }
        } else if (!this.mInfoOnSelected) {
        } else {
            if (this.mCardType == 2 || this.mCardType == 1) {
                setInfoVisibility(focused);
            }
        }
    }

    private void cancelAnimations() {
        if (this.mAnim != null) {
            this.mAnim.cancel();
            this.mAnim = null;
        }
    }

    private void animateInfoOffset(boolean shown) {
        cancelAnimations();
        int extraHeight = 0;
        if (shown) {
            int widthSpec = MeasureSpec.makeMeasureSpec(this.mMeasuredWidth, 1073741824);
            int heightSpec = MeasureSpec.makeMeasureSpec(0, 0);
            for (int i = 0; i < this.mExtraViewList.size(); i++) {
                View extraView = this.mExtraViewList.get(i);
                extraView.setVisibility(0);
                extraView.measure(widthSpec, heightSpec);
                extraHeight = Math.max(extraHeight, extraView.getMeasuredHeight());
            }
        }
        float f = this.mInfoOffset;
        if (!shown) {
            extraHeight = 0;
        }
        this.mAnim = new InfoOffsetAnimation(f, (float) extraHeight);
        this.mAnim.setDuration((long) this.mSelectedAnimDuration);
        this.mAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        this.mAnim.setAnimationListener(new C02232());
        startAnimation(this.mAnim);
    }

    private void animateInfoHeight(boolean shown) {
        cancelAnimations();
        int extraHeight = 0;
        if (shown) {
            int widthSpec = MeasureSpec.makeMeasureSpec(this.mMeasuredWidth, 1073741824);
            int heightSpec = MeasureSpec.makeMeasureSpec(0, 0);
            for (int i = 0; i < this.mExtraViewList.size(); i++) {
                View extraView = this.mExtraViewList.get(i);
                extraView.setVisibility(0);
                extraView.measure(widthSpec, heightSpec);
                extraHeight = Math.max(extraHeight, extraView.getMeasuredHeight());
            }
        }
        this.mAnim = new InfoHeightAnimation(this.mInfoVisFraction, shown ? 1.0f : 0.0f);
        this.mAnim.setDuration((long) this.mSelectedAnimDuration);
        this.mAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        this.mAnim.setAnimationListener(new C02243());
        startAnimation(this.mAnim);
    }

    private void animateInfoAlpha(boolean shown) {
        cancelAnimations();
        if (shown) {
            for (int i = 0; i < this.mInfoViewList.size(); i++) {
                this.mInfoViewList.get(i).setVisibility(0);
            }
        }
        this.mAnim = new InfoAlphaAnimation(this.mInfoAlpha, shown ? 1.0f : 0.0f);
        this.mAnim.setDuration((long) this.mActivatedAnimDuration);
        this.mAnim.setInterpolator(new DecelerateInterpolator());
        this.mAnim.setAnimationListener(new C02254());
        startAnimation(this.mAnim);
    }

    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(-2, -2);
    }

    protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams lp) {
        if (lp instanceof LayoutParams) {
            return new LayoutParams((LayoutParams) lp);
        }
        return new LayoutParams(lp);
    }

    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    public LeanbackCardView(Context context) {
        super(context);
    }

    public LeanbackCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LeanbackCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public LeanbackCardView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}
