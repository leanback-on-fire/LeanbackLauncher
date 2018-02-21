package com.rockon999.android.leanbacklauncher;

import android.animation.Animator;
import android.content.Context;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rockon999.android.leanbacklauncher.HomeScrollManager.HomeScrollFractionListener;
import com.rockon999.android.leanbacklauncher.animation.FadeAnimator;
import com.rockon999.android.leanbacklauncher.animation.ParticipatesInScrollAnimation;
import com.rockon999.android.leanbacklauncher.animation.ViewDimmer;
import com.rockon999.android.leanbacklauncher.animation.ViewDimmer.DimState;

public class ActiveFrame extends LinearLayout implements ParticipatesInScrollAnimation, HomeScrollFractionListener, ActiveItemsRowView.RowCountChangeListener {
    private static final String TAG = "ActiveFrame";
    protected float mActiveTextMargin;
    private int mAnimDuration;
    protected int mBottomPadding;
    protected float mCardSpacing;
    protected DimState mDimState;
    private ViewDimmer mDimmer;
    private float mDownscaleFactor;
    private RowExpandAnimation mExpandAnim;
    private float mExpanded;
    protected View mHeader;
    private Animator mHeaderFadeInAnimation;
    protected int mHeaderHeight;
    private boolean mHeaderVisible;
    protected ActiveItemsRowView mRow;
    protected int mRowMinSpacing;
    protected float mRowPadding;
    private boolean mScalesWhenUnfocused;

    /* renamed from: ActiveFrame.1 */
    class C01521 implements OnLayoutChangeListener {
        C01521() {
        }

        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
            ActiveFrame.this.updateRow(left, right);
        }
    }

    private class RowExpandAnimation extends Animation {
        private float mDelta;
        private float mStartValue;

        public RowExpandAnimation(float start, float end) {
            this.mStartValue = start;
            this.mDelta = end - start;
            setDuration((long) ActiveFrame.this.mAnimDuration);
            setInterpolator(AnimationUtils.loadInterpolator(ActiveFrame.this.getContext(), android.R.anim.accelerate_interpolator));
        }

        protected void applyTransformation(float interpolatedTime, Transformation t) {
            ActiveFrame.this.setExpandedFraction(this.mStartValue + (this.mDelta * interpolatedTime));
        }
    }

    public ActiveFrame(Context context) {
        this(context, null);
    }

    public ActiveFrame(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ActiveFrame(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mDimState = DimState.INACTIVE;
        this.mScalesWhenUnfocused = false;
        this.mExpanded = 1.0f;
        this.mHeaderVisible = true;
        setDescendantFocusability(262144);
        this.mHeaderHeight = getResources().getDimensionPixelSize(R.dimen.header_height);
        this.mActiveTextMargin = getResources().getDimension(R.dimen.header_text_active_margin_extra);
        this.mAnimDuration = getResources().getInteger(R.integer.item_scale_anim_duration);
        this.mBottomPadding = getResources().getDimensionPixelSize(R.dimen.group_vertical_spacing);
        this.mRowPadding = getResources().getDimension(R.dimen.row_padding);
        this.mCardSpacing = getResources().getDimension(R.dimen.card_spacing);
        TypedValue out = new TypedValue();
        getResources().getValue(R.fraction.inactive_banner_scale_down_amount, out, true);
        this.mDownscaleFactor = out.getFloat();
        if (this.mDownscaleFactor < 0.0f || this.mDownscaleFactor >= 1.0f) {
            this.mDownscaleFactor = 0.0f;
        }
        this.mRowMinSpacing = (int) ((1.0f - this.mDownscaleFactor) * getResources().getDimension(R.dimen.inter_card_spacing));
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        int count = getChildCount();
        int i = 0;
        while (i < count) {
            View view = getChildAt(i);
            if (view instanceof ActiveItemsRowView) {
                this.mRow = (ActiveItemsRowView) view;
                break;
            } else {
                i++;
            }
        }
        if (this.mRow != null) {
            this.mRow.addOnLayoutChangeListener(new C01521());
        }
        this.mHeader = findViewById(R.id.header);
        if (this.mHeader != null) {
            this.mDimmer = new ViewDimmer(this);
            TextView title = (TextView) this.mHeader.findViewById(R.id.title);
            if (title != null) {
                this.mDimmer.addDimTarget(title);
            }
            ImageView icon = (ImageView) this.mHeader.findViewById(R.id.icon);
            if (icon != null) {
                this.mDimmer.addDimTarget(icon);
            }
            this.mDimmer.setDimState(this.mDimState, true);
            this.mHeaderFadeInAnimation = new FadeAnimator(this.mHeader, FadeAnimator.Direction.FADE_IN);
        }
    }

    public void setActivated(boolean activated) {
        this.mDimState = ViewDimmer.activatedToDimState(activated);
        if (this.mDimmer != null) {
            this.mDimmer.setDimState(this.mDimState, false);
        }
        if (this.mRow != null) {
            this.mRow.setActivated(activated);
            if (this.mScalesWhenUnfocused) {
                setRowState(ViewDimmer.dimStateToActivated(this.mDimState), hasWindowFocus());
            }
        }
    }

    public void setScaledWhenUnfocused(boolean scalingEnabled) {
        this.mScalesWhenUnfocused = scalingEnabled;
        if (this.mScalesWhenUnfocused) {
            setRowState(ViewDimmer.dimStateToActivated(this.mDimState), false);
        } else {
            setRowState(true, false);
        }
    }

    public void onScrollPositionChanged(int position, float fractionFromTop) {
        //Log.i(TAG, "onScrollPositionChanged->stackTrace:" + Log.getStackTraceString(new Throwable()));
        Log.i(TAG, "onScrollPositionChanged->position:" + position);
        Log.i(TAG, "onScrollPositionChanged->fractionFromTop:" + fractionFromTop);
/*        ViewGroup.LayoutParams lp;
        if (fractionFromTop < 0.99f) {
            if (this.mHeaderVisible) {
                this.mHeaderVisible = false;
                if (this.mHeader != null) {
                    this.mHeaderFadeInAnimation.cancel();
                    lp = this.mHeader.getLayoutParams();
                    if (lp.height != 0) {
                        lp.height = 0;
                        this.mHeader.setLayoutParams(lp);
                    }
                }
                setPadding(0, 0, 0, this.mRowMinSpacing);
            }
        } else if (!this.mHeaderVisible) {
            this.mHeaderVisible = true;
            if (this.mHeader != null) {
                this.mHeaderFadeInAnimation.start();
                lp = this.mHeader.getLayoutParams();
                if (lp.height != this.mHeaderHeight) {
                    lp.height = this.mHeaderHeight;
                    this.mHeader.setLayoutParams(lp);
                }
            }
            setPadding(0, 0, 0, this.mBottomPadding);
        }*/
    }

    public void onRowCountChanged() {
        setExpandedFraction(this.mExpanded);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (this.mRow != null && this.mRow.getScaleY() < 1.0f) {
            setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight() - ((int) (((float) this.mRow.getMeasuredHeight()) * (1.0f - this.mRow.getScaleY()))));
        }
    }

    public void resetScrollPosition(boolean smooth) {
        if (this.mRow != null && this.mRow.getSelectedPosition() != 0) {
            if (smooth) {
                this.mRow.setSelectedPositionSmooth(0);
            } else {
                this.mRow.setSelectedPosition(0);
            }
        }
    }

    private void setRowState(boolean expanded, boolean animate) {
        float f = 1.0f;
        if (this.mExpandAnim != null) {
            this.mExpandAnim.cancel();
            this.mExpandAnim = null;
        }
        if (animate && isAttachedToWindow() && getVisibility() == View.VISIBLE) {
            float f2 = this.mExpanded;
            if (!expanded) {
                f = 0.0f;
            }
            this.mExpandAnim = new RowExpandAnimation(f2, f);
            startAnimation(this.mExpandAnim);
        } else if (expanded) {
            setExpandedFraction(1.0f);
        } else {
            setExpandedFraction(0.0f);
        }
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        adjustRowDimensions(w);
    }

    private void setExpandedFraction(float fraction) {
        this.mExpanded = fraction;
        adjustRowDimensions(getMeasuredWidth());
        if (this.mHeader != null && (this.mHeader.getLayoutParams() instanceof MarginLayoutParams)) {
            MarginLayoutParams lp = (MarginLayoutParams) this.mHeader.getLayoutParams();
            int margin = (int) (this.mActiveTextMargin * fraction);
            if (lp.bottomMargin != margin) {
                lp.bottomMargin = margin;
                this.mHeader.setLayoutParams(lp);
            }
        }
    }

    private void adjustRowDimensions(int frameWidth) {
        boolean isScaled = false;
        float f = 1.0f;
        if (this.mRow != null) {
            if (this.mExpanded < 1.0f) {
                isScaled = true;
            }
            float f2 = (float) frameWidth;
            if (isScaled) {
                f = 1.0f - this.mDownscaleFactor;
            }
            int rowLength = (int) (f2 / f);
            ViewGroup.LayoutParams p = this.mRow.getLayoutParams();
            if (rowLength <= 0 || p.width == rowLength) {
                updateRow(this.mRow.getLeft(), this.mRow.getRight());
                return;
            }
            p.width = rowLength;
            this.mRow.setLayoutParams(p);
        }
    }

    private void updateRow(int left, int right) {
        boolean isScaled = this.mExpanded < 1.0f;
        float scale = 1.0f - ((1.0f - this.mExpanded) * this.mDownscaleFactor);
        float unfocusedScale = 1.0f - this.mDownscaleFactor;
        boolean useRtl = getLayoutDirection() == 1;
        if (this.mRow != null) {
            Adapter adapter = this.mRow.getAdapter();
            if (adapter != null) {
                int rowLength = right - left;
                int deltaW = rowLength - getMeasuredWidth();
                float usableSpace = ((float) rowLength) - (this.mRowPadding * 2.0f);
                int itemCount = adapter.getItemCount();
                int selected = this.mRow.getSelectedPosition();
                int numRows = this.mRow.getNumRows();
                if (numRows <= 0) {
                    numRows = 1;
                }
                int numCol = (int) Math.ceil((double) (((float) itemCount) / ((float) numRows)));
                int selectedCol = (int) Math.floor((double) (((float) this.mRow.getSelectedPosition()) / ((float) numRows)));
                View selectedView = null;
                if (itemCount > 0 && selected >= 0) {
                    ViewHolder holder = this.mRow.findViewHolderForAdapterPosition(selected);
                    if (holder != null) {
                        selectedView = holder.itemView;
                    }
                }
                if (selectedView != null) {
                    int rowAlign;
                    float viewLength = (float) selectedView.getMeasuredWidth();
                    float totalLength = (((float) itemCount) * viewLength) + (this.mCardSpacing * ((float) (itemCount - 1)));
                    float distFromStart = ((this.mCardSpacing + viewLength) * ((float) selectedCol)) + (0.5f * viewLength);
                    float distFromEnd = ((this.mCardSpacing + viewLength) * ((float) ((numCol - selectedCol) - 1))) + (0.5f * viewLength);
                    if (totalLength < ((float) getMeasuredWidth()) - (this.mRowPadding * 2.0f)) {
                        rowAlign = 0;
                    } else {
                        if (distFromStart <= ((float) (getMeasuredWidth() / 2))) {
                            rowAlign = 0;
                        } else {
                            if (distFromEnd < ((float) (getMeasuredWidth() / 2))) {
                                rowAlign = 2;
                            } else {
                                rowAlign = 1;
                            }
                        }
                    }
                    float selectCtr = selectedView.getX() + (viewLength / 2.0f);
                    if (!isScaled) {
                        this.mRow.setPivotX(useRtl ? ((float) rowLength) - this.mRowPadding : this.mRowPadding);
                        this.mRow.setTranslationX(0.0f);
                    } else if (rowAlign == 0) {
                        this.mRow.setPivotX(useRtl ? ((float) rowLength) - this.mRowPadding : this.mRowPadding);
                        this.mRow.setTranslationX(0.0f);
                    } else if (rowAlign == 1) {
                        float f;
                        ActiveItemsRowView activeItemsRowView = this.mRow;
                        if (useRtl) {
                            f = ((float) rowLength) - selectCtr;
                        } else {
                            f = selectCtr;
                        }
                        activeItemsRowView.setPivotX(f);
                        float deltaStart = (distFromStart * unfocusedScale) - ((((float) getMeasuredWidth()) / 2.0f) - this.mRowPadding);
                        if (deltaStart > 0.0f) {
                            deltaStart = 0.0f;
                        } else {
                            deltaStart *= 1.0f - this.mExpanded;
                        }
                        float deltaEnd = (distFromEnd * unfocusedScale) - ((((float) getMeasuredWidth()) / 2.0f) - this.mRowPadding);
                        if (deltaEnd > 0.0f) {
                            deltaEnd = 0.0f;
                        } else {
                            deltaEnd *= 1.0f - this.mExpanded;
                        }
                        float centerOffset = 0.0f;
                        if (deltaStart < 0.0f) {
                            centerOffset = -deltaStart;
                        } else if (deltaEnd < 0.0f) {
                            centerOffset = deltaEnd;
                        }
                        this.mRow.setTranslationX(((float) (useRtl ? -1 : 1)) * (((((float) getMeasuredWidth()) / 2.0f) - selectCtr) - centerOffset));
                    } else if (totalLength <= usableSpace) {
                        float deltaX = ((((float) getMeasuredWidth()) - (this.mRowPadding * 2.0f)) - totalLength) * ((float) (useRtl ? -1 : 1));
                        this.mRow.setPivotX(useRtl ? ((float) rowLength) - this.mRowPadding : this.mRowPadding);
                        this.mRow.setTranslationX(this.mExpanded * deltaX);
                    } else {
                        this.mRow.setPivotX(useRtl ? this.mRowPadding : ((float) rowLength) - this.mRowPadding);
                        this.mRow.setTranslationX((float) ((useRtl ? 1 : -1) * deltaW));
                    }
                } else {
                    this.mRow.setPivotX(useRtl ? ((float) rowLength) - this.mRowPadding : this.mRowPadding);
                    this.mRow.setTranslationX(0.0f);
                }
                this.mRow.setPivotY(0.0f);
                this.mRow.setScaleX(scale);
                this.mRow.setScaleY(scale);
            }
        }
    }

    public void setAnimationsEnabled(boolean enabled) {
        if (this.mDimmer != null) {
            this.mDimmer.setAnimationEnabled(enabled);
        }
    }
}
