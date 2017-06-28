package com.rockchips.android.leanbacklauncher.animation;

import android.support.v17.leanback.widget.HorizontalGridView;
import android.view.View;
import android.view.ViewGroup;
import com.rockchips.android.leanbacklauncher.util.Preconditions;

import java.util.ArrayList;

public final class MassFadeAnimator extends PropagatingAnimator<PropagatingAnimator.ViewHolder> implements Joinable {
    private static  /* synthetic */ int[] f3xbf31f146;
    private final Direction mDirection;
    private final float mEndAlpha;
    private final ViewGroup mRoot;
    private final ArrayList<HorizontalGridView> mRows;
    private final float mStartAlpha;
    private final Class<?> mTargetClass;

    public static final class Builder {
        private Direction mDirection;
        private long mDuration;
        private final ViewGroup mRoot;
        private Class<?> mTargetClass;

        public Builder(ViewGroup root) {
            this.mDirection = Direction.FADE_OUT;
            this.mTargetClass = Participant.class;
            this.mDuration = -1;
            this.mRoot = (ViewGroup) Preconditions.checkNotNull(root);
        }

        public Builder setDirection(Direction direction) {
            this.mDirection = (Direction) Preconditions.checkNotNull(direction);
            return this;
        }

        public Builder setTarget(Class<?> targetClass) {
            this.mTargetClass = (Class) Preconditions.checkNotNull(targetClass);
            return this;
        }

        public Builder setDuration(long duration) {
            Preconditions.checkArgument(duration > 0);
            this.mDuration = duration;
            return this;
        }

        public MassFadeAnimator build() {
            return new MassFadeAnimator(new Builder(mRoot));
        }
    }

    public enum Direction {
        FADE_IN,
        FADE_OUT
    }

    public interface Participant {
    }

    static final class ViewHolder extends PropagatingAnimator.ViewHolder {
        ViewHolder(View view) {
            super(view);
        }
    }

    private static /* synthetic */ int[] m1983xcc0e8822() {
        if (f3xbf31f146 != null) {
            return f3xbf31f146;
        }
        int[] iArr = new int[Direction.values().length];
        try {
            iArr[Direction.FADE_IN.ordinal()] = 1;
        } catch (NoSuchFieldError e) {
        }
        try {
            iArr[Direction.FADE_OUT.ordinal()] = 2;
        } catch (NoSuchFieldError e2) {
        }
        f3xbf31f146 = iArr;
        return iArr;
    }

    private MassFadeAnimator(Builder builder) {
        super(10);
        this.mRoot = builder.mRoot;
        this.mDirection = builder.mDirection;
        this.mTargetClass = builder.mTargetClass;
        this.mRows = new ArrayList();
        switch (m1983xcc0e8822()[this.mDirection.ordinal()]) {
            case android.support.v7.recyclerview.R.styleable.RecyclerView_android_descendantFocusability /*1*/:
                this.mStartAlpha = 0.0f;
                this.mEndAlpha = 1.0f;
                break;
            case android.support.v7.recyclerview.R.styleable.RecyclerView_layoutManager /*2*/:
                this.mStartAlpha = 1.0f;
                this.mEndAlpha = 0.0f;
                break;
            default:
                throw new IllegalStateException("Unknown direction: " + this.mDirection);
        }
        if (builder.mDuration > 0) {
            setDuration(builder.mDuration);
        }
    }

    public void setupStartValues() {
        if (size() == 0) {
            addViews(this.mRoot);
            for (HorizontalGridView row : this.mRows) {
                row.setAnimateChildLayout(false);
            }
        }
        super.setupStartValues();
    }

    @Override
    protected void onResetView(PropagatingAnimator.ViewHolder viewHolder) {

    }

    @Override
    protected void onSetupStartValues(PropagatingAnimator.ViewHolder viewHolder) {

    }

    @Override
    protected void onUpdateView(PropagatingAnimator.ViewHolder viewHolder, float f) {

    }

    public void reset() {
        for (HorizontalGridView row : this.mRows) {
            row.setAnimateChildLayout(true);
        }
        super.reset();
    }

    public void include(View target) {
        addView(new ViewHolder(target));
    }

    public void exclude(View target) {
        int n = size();
        for (int i = 0; i < n; i++) {
            if (((ViewHolder) getView(i)).view == target) {
                removeView(i);
                return;
            }
        }
    }

    protected void onSetupStartValues(ViewHolder holder) {
        holder.view.setAlpha(this.mStartAlpha);
    }

    protected void onUpdateView(ViewHolder holder, float fraction) {
        holder.view.setAlpha(this.mStartAlpha + ((this.mEndAlpha - this.mStartAlpha) * fraction));
    }

    protected void onResetView(ViewHolder holder) {
        holder.view.setAlpha(1.0f);
    }

    private void addViews(ViewGroup localRoot) {
        int n = localRoot.getChildCount();
        for (int i = 0; i < n; i++) {
            View child = localRoot.getChildAt(i);
            if (this.mTargetClass.isInstance(child)) {
                addView(new ViewHolder(child));
            }
            if (child instanceof ViewGroup) {
                addViews((ViewGroup) child);
            }
            if (child instanceof HorizontalGridView) {
                this.mRows.add((HorizontalGridView) child);
            }
        }
    }

    public String toString() {
        StringBuilder buf = new StringBuilder().append("MassFadeAnimator@").append(Integer.toHexString(hashCode())).append(':').append(this.mDirection == Direction.FADE_IN ? "FADE_IN" : "FADE_OUT").append('{');
        int n = size();
        for (int i = 0; i < n; i++) {
            buf.append("\n    ").append(((ViewHolder) getView(i)).toString().replaceAll("\n", "\n    "));
        }
        return buf.append("\n}").toString();
    }
}
