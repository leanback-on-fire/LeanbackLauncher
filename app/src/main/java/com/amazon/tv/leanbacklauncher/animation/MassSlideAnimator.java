package com.amazon.tv.leanbacklauncher.animation;

import android.animation.TimeInterpolator;
import android.content.res.Resources;
import android.graphics.Rect;
import android.view.View;
import android.view.View.OnLayoutChangeListener;
import android.view.ViewGroup;
import android.view.animation.PathInterpolator;

import androidx.leanback.widget.HorizontalGridView;

import com.amazon.tv.leanbacklauncher.R;
import com.amazon.tv.leanbacklauncher.util.Preconditions;

import java.util.ArrayList;
import java.util.Iterator;

public final class MassSlideAnimator extends PropagatingAnimator<MassSlideAnimator.ViewHolder> implements Joinable {
    private static final TimeInterpolator sSlideInInterpolator = new PathInterpolator(0.0f, 0.0f, 0.2f, 1.0f);
    private static final TimeInterpolator sSlideOutInterpolator = new PathInterpolator(0.4f, 0.0f, 1.0f, 1.0f);
    private final Direction mDirection;
    private final Rect mEpicenter;
    private final View mExclude;
    private final Class<?> mExcludeClass;
    private final boolean mFade;
    private final float mPropagationSpeed;
    private final ViewGroup mRoot;
    private final ArrayList<HorizontalGridView> mRows;
    private final Class<?> mTargetClass;

    public static final class Builder {
        private Direction mDirection = Direction.SLIDE_OUT;
        private Rect mEpicenter = new Rect();
        private View mExclude;
        private Class<?> mExcludeClass;
        private boolean mFade = true;
        private final ViewGroup mRoot;
        private Class<?> mTargetClass = ParticipatesInLaunchAnimation.class;

        public Builder(ViewGroup root) {
            this.mRoot = Preconditions.checkNotNull(root);
        }

        public Builder setDirection(Direction direction) {
            this.mDirection = Preconditions.checkNotNull(direction);
            return this;
        }

        public Builder setEpicenter(Rect epicenter) {
            this.mEpicenter = Preconditions.checkNotNull(epicenter);
            return this;
        }

        public Builder setExclude(View exclude) {
            this.mExclude = Preconditions.checkNotNull(exclude);
            return this;
        }

        public Builder setExclude(Class<?> excludeClass) {
            this.mExcludeClass = excludeClass;
            return this;
        }

        public Builder setFade(boolean fade) {
            this.mFade = fade;
            return this;
        }

        public MassSlideAnimator build() {
            return new MassSlideAnimator(this);
        }
    }

    public enum Direction {
        SLIDE_IN,
        SLIDE_OUT
    }

    private final class SlidePropagation implements Propagation<ViewHolder> {
        private final int mWindowHeight;

        SlidePropagation() {
            Rect windowInsets = new Rect();
            MassSlideAnimator.this.mRoot.getWindowVisibleDisplayFrame(windowInsets);
            this.mWindowHeight = windowInsets.height();
        }

        public long getStartDelay(ViewHolder holder) {
            float distanceFraction = ((float) getDistance(holder)) / ((float) this.mWindowHeight);
            long duration = MassSlideAnimator.this.getDuration();
            if (duration < 0) {
                duration = 300;
            }
            return Math.round((((float) duration) / MassSlideAnimator.this.mPropagationSpeed) * distanceFraction);
        }

        private int getDistance(ViewHolder holder) {
            int distance;
            int targetX = holder.mCenter[0];
            int targetY = holder.mCenter[1];
            int epicenterX = MassSlideAnimator.this.mEpicenter.centerX();
            switch (holder.mSide) {
                case 1:
                    distance = (this.mWindowHeight - targetY) + Math.abs(epicenterX - targetX);
                    break;
                case 2:
                    distance = targetY + Math.abs(epicenterX - targetX);
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported side: " + holder.mSide);
            }
            if (distance < 0) {
                return 0;
            }
            if (distance > this.mWindowHeight) {
                return this.mWindowHeight;
            }
            return distance;
        }

        public String toString() {
            return getClass().getSimpleName() + '[' + MassSlideAnimator.this.mDirection.name() + '@' + MassSlideAnimator.this.mEpicenter.centerX() + ',' + MassSlideAnimator.this.mEpicenter.centerY() + ']';
        }
    }

    final class ViewHolder extends PropagatingAnimator.ViewHolder {
        final int[] mCenter;
        final float mEndY;
        private final OnLayoutChangeListener mListener;
        final int mSide;
        final float mStartY;
        final /* synthetic */ MassSlideAnimator this$0;

        private ViewHolder(MassSlideAnimator this$0, View view, ViewGroup root, Rect epicenter, Direction direction) {
            super(view);

            int i = 1;
            this.this$0 = this$0;
            this.mCenter = new int[2];
            this.mListener = new OnLayoutChangeListener() {
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    float restoreTranslationY = v.getTranslationY();
                    v.setTranslationY(0.0f);
                    ViewHolder.this.recordMCenter(v);
                    v.setTranslationY(restoreTranslationY);
                    ViewHolder.this.this$0.invalidateView(ViewHolder.this);
                }
            };
            recordMCenter(view);
            if (this.mCenter[1] > epicenter.centerY()) {
                i = 2;
            }
            this.mSide = i;
            switch (direction) {
                case SLIDE_IN:
                    this.mStartY = getEndY(root);
                    this.mEndY = 0.0f;
                    break;
                case SLIDE_OUT:
                    this.mStartY = 0.0f;
                    this.mEndY = getEndY(root);
                    break;
                default:
                    throw new IllegalArgumentException("Illegal direction: " + direction);
            }
            addListener();
        }

        private void recordMCenter(View view) {
            view.getLocationOnScreen(this.mCenter);
            int[] iArr = this.mCenter;
            iArr[0] = iArr[0] + (view.getWidth() / 2);
            iArr = this.mCenter;
            iArr[1] = iArr[1] + (view.getHeight() / 2);
        }

        private void addListener() {
            this.view.addOnLayoutChangeListener(this.mListener);
        }

        private void removeListener() {
            this.view.removeOnLayoutChangeListener(this.mListener);
        }

        private float getScaleFactor(ViewGroup root) {
            float scale = 1.0f;
            View v = this.view;
            while (v != null && v != root) {
                scale *= v.getScaleY();
                v = (View) v.getParent();
            }
            return scale;
        }

        private float getEndY(ViewGroup root) {
            float scaleFactor = getScaleFactor(root);
            switch (this.mSide) {
                case 1:
                    return ((float) (-root.getHeight())) / scaleFactor;
                case 2:
                    return ((float) root.getHeight()) / scaleFactor;
                default:
                    throw new IllegalArgumentException("Illegal side: " + this.mSide);
            }
        }

        public String toString() {
            return this.view.getClass().getSimpleName() + '@' + Integer.toHexString(System.identityHashCode(this.view)) + ':' + this.mCenter[0] + ',' + this.mCenter[1] + ':' + Math.round(this.mStartY) + ".." + Math.round(this.mEndY);
        }
    }

    private MassSlideAnimator(Builder builder) {
        super(20);
        Resources res = builder.mRoot.getResources();
        // R.fraction.slide_animator_propagation_speed
        float propagationSpeed = (float) 5.8; // todo hardcoded
        this.mRows = new ArrayList<>();
        this.mRoot = builder.mRoot;
        this.mEpicenter = builder.mEpicenter;
        this.mDirection = builder.mDirection;
        this.mTargetClass = builder.mTargetClass;
        this.mExclude = builder.mExclude;
        this.mExcludeClass = builder.mExcludeClass;
        this.mFade = builder.mFade;
        this.mPropagationSpeed = propagationSpeed;//.getFloat();
        super.setPropagation(new SlidePropagation());
        switch (this.mDirection) {
            case SLIDE_IN:
                super.setInterpolator(sSlideInInterpolator);
                break;
            case SLIDE_OUT:
                super.setInterpolator(sSlideOutInterpolator);
                break;
            default:
                throw new IllegalStateException("Unknown direction: " + this.mDirection);
        }
        setDuration(res.getInteger(R.integer.slide_animator_default_duration));
    }

    public PropagatingAnimator<ViewHolder> setPropagation(Propagation<ViewHolder> propagation) {
        throw new UnsupportedOperationException("Propagation is immutable");
    }

    public void setInterpolator(TimeInterpolator value) {
        throw new UnsupportedOperationException("Interpolator is immutable");
    }

    public void setupStartValues() {
        if (size() == 0) {
            addViews(this.mRoot);
            Iterator it = this.mRows.iterator();
            while (it.hasNext()) {
                ((HorizontalGridView) it.next()).setAnimateChildLayout(false);
            }
        }
        super.setupStartValues();
    }

    public void reset() {
        Iterator it = this.mRows.iterator();
        while (it.hasNext()) {
            ((HorizontalGridView) it.next()).setAnimateChildLayout(true);
        }
        super.reset();
        for (int i = size() - 1; i >= 0; i--) {
            removeView(i);
        }
    }

    public ViewHolder removeView(int index) {
        ViewHolder holder = super.removeView(index);
        holder.removeListener();
        return holder;
    }

    public void include(View target) {
        addView(new ViewHolder(this, target, this.mRoot, this.mEpicenter, this.mDirection));
    }

    public void exclude(View target) {
        int n = size();
        for (int i = 0; i < n; i++) {
            if (getView(i).view == target) {
                removeView(i);
                return;
            }
        }
    }

    protected void onSetupStartValues(ViewHolder holder) {
        onUpdateView(holder, 0.0f);
    }

    protected void onUpdateView(ViewHolder holder, float fraction) {
        holder.view.setTranslationY(holder.mStartY + (fraction * (holder.mEndY - holder.mStartY)));
        if (this.mFade) {
            holder.view.setAlpha(this.mDirection == Direction.SLIDE_IN ? fraction : 1.0f - fraction);
        }
    }

    protected void onResetView(ViewHolder holder) {
        holder.view.setTranslationY(0.0f);
        if (this.mFade) {
            holder.view.setAlpha(1.0f);
        }
    }

    private void addViews(ViewGroup localRoot) {
        int n = localRoot.getChildCount();
        for (int i = 0; i < n; i++) {
            View child = localRoot.getChildAt(i);
            if (this.mTargetClass.isInstance(child) && !isExcluded(child)) {
                addView(new ViewHolder(this, child, this.mRoot, this.mEpicenter, this.mDirection));
            }
            if (child instanceof ViewGroup) {
                addViews((ViewGroup) child);
            }
            if (child instanceof HorizontalGridView) {
                this.mRows.add((HorizontalGridView) child);
            }
        }
    }

    private boolean isExcluded(View view) {
        return view == this.mExclude || (this.mExcludeClass != null && this.mExcludeClass.isInstance(view));
    }

    public String toString() {
        StringBuilder buf = new StringBuilder().append("MassSlideAnimator@").append(Integer.toHexString(hashCode())).append(':').append(this.mDirection == Direction.SLIDE_IN ? "SLIDE_IN" : "SLIDE_OUT").append(':').append(this.mEpicenter.centerX()).append(',').append(this.mEpicenter.centerY()).append('{');
        int n = size();
        for (int i = 0; i < n; i++) {
            buf.append("\n    ").append(getView(i).toString().replaceAll("\n", "\n    "));
        }
        return buf.append("\n}").toString();
    }
}
