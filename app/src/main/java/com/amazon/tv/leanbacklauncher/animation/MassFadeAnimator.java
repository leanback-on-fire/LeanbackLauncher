package com.amazon.tv.leanbacklauncher.animation;

import android.view.View;
import android.view.ViewGroup;

import androidx.leanback.widget.HorizontalGridView;

import com.amazon.tv.leanbacklauncher.util.Preconditions;

import java.util.ArrayList;
import java.util.Iterator;

public final class MassFadeAnimator extends PropagatingAnimator<MassFadeAnimator.ViewHolder> implements Joinable {
    private final Direction mDirection;
    private final float mEndAlpha;
    private final ViewGroup mRoot;
    private final ArrayList<HorizontalGridView> mRows;
    private final float mStartAlpha;
    private final Class<?> mTargetClass;

    public static final class Builder {
        private Direction mDirection = Direction.FADE_OUT;
        private long mDuration = -1;
        private final ViewGroup mRoot;
        private Class<?> mTargetClass = Participant.class;

        public Builder(ViewGroup root) {
            this.mRoot = Preconditions.checkNotNull(root);
        }

        public Builder setDirection(Direction direction) {
            this.mDirection = Preconditions.checkNotNull(direction);
            return this;
        }

        public Builder setTarget(Class<?> targetClass) {
            this.mTargetClass = Preconditions.checkNotNull(targetClass);
            return this;
        }

        public Builder setDuration(long duration) {
            Preconditions.checkArgument(duration > 0);
            this.mDuration = duration;
            return this;
        }

        public MassFadeAnimator build() {
            return new MassFadeAnimator(this);
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

    private MassFadeAnimator(Builder builder) {
        super(10);
        this.mRoot = builder.mRoot;
        this.mDirection = builder.mDirection;
        this.mTargetClass = builder.mTargetClass;
        this.mRows = new ArrayList();
        switch (this.mDirection) {
            case FADE_IN:
                this.mStartAlpha = 0.0f;
                this.mEndAlpha = 1.0f;
                break;
            case FADE_OUT:
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
    }

    public void include(View target) {
        addView(new ViewHolder(target));
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
            buf.append("\n    ").append(getView(i).toString().replaceAll("\n", "\n    "));
        }
        return buf.append("\n}").toString();
    }
}
