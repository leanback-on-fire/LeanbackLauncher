package com.rockon999.android.leanbacklauncher.animation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.util.Log;
import android.view.View;

import com.rockon999.android.leanbacklauncher.util.Preconditions;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

public abstract class PropagatingAnimator<VH extends PropagatingAnimator.ViewHolder> extends ValueAnimator implements Resettable {
    private static final Propagation<?> sDefaultPropagation;
    private final PropagatingAnimatorListener mListener;
    private long mMaxStartDelay;
    private boolean mNormalized;
    private Propagation<VH> mPropagation;
    private byte mState;
    private final ArrayList<VH> mViews;

    public static abstract class ViewHolder {
        long normalizedStartDelay;
        long rawStartDelay;
        protected final View view;

        protected ViewHolder(View view) {
            this.view = Preconditions.checkNotNull(view);
        }

        public String toString() {
            return this.view.getClass().getSimpleName() + '@' + Integer.toHexString(System.identityHashCode(this.view));
        }
    }

    public interface Propagation<VH extends ViewHolder> {
        long getStartDelay(VH vh);
    }

    private static final class NoPropagation<VH extends ViewHolder> implements Propagation<VH> {
        private NoPropagation() {
        }

        public long getStartDelay(VH vh) {
            return 0;
        }
    }

    private final class PropagatingAnimatorListener extends AnimatorListenerAdapter implements AnimatorUpdateListener {
        private PropagatingAnimatorListener() {
        }

        public void onAnimationStart(Animator animation) {
            PropagatingAnimator.this.mState = (byte) 8;
        }

        public void onAnimationUpdate(ValueAnimator animation) {
            TimeInterpolator interpolator = PropagatingAnimator.this.getInterpolator();
            long duration = PropagatingAnimator.this.getChildAnimationDuration();
            long totalPlayTime = PropagatingAnimator.this.getCurrentPlayTime();
            int n = PropagatingAnimator.this.mViews.size();
            for (int i = 0; i < n; i++) {
                ViewHolder holder = PropagatingAnimator.this.mViews.get(i);
                float fraction = ((float) (totalPlayTime - holder.normalizedStartDelay)) / ((float) duration);
                if (fraction >= 0.0f) {
                    if (fraction > 1.0f) {
                        fraction = 1.0f;
                    }
                    PropagatingAnimator.this.onUpdateView((VH)holder, interpolator.getInterpolation(fraction));
                }
            }
        }

        public void onAnimationCancel(Animator animation) {
            PropagatingAnimator.this.mState = (byte) 16;
            PropagatingAnimator.this.reset();
        }

        public void onAnimationEnd(Animator animation) {
            PropagatingAnimator.this.mState = (byte) 16;
            PropagatingAnimator.this.removeListener();
        }
    }

    protected abstract void onResetView(VH vh);

    protected abstract void onSetupStartValues(VH vh);

    protected abstract void onUpdateView(VH vh, float f);

    static {
        sDefaultPropagation = new NoPropagation();
    }

    protected PropagatingAnimator() {
        this.mViews = new ArrayList<>();
        this.mListener = new PropagatingAnimatorListener();
        this.mPropagation = (Propagation<VH>) sDefaultPropagation;
        this.mState = (byte) 1;
        setFloatValues(0.0f, 1.0f);
        addListener();
    }

    protected PropagatingAnimator(int initialCapacity) {
        this();
        this.mViews.ensureCapacity(initialCapacity);
    }

    public PropagatingAnimator<VH> setPropagation(Propagation<VH> propagation) {
        this.mPropagation = Preconditions.checkNotNull(propagation);
        return this;
    }

    public PropagatingAnimator<VH> addView(VH holder) {
        this.mViews.add(Preconditions.checkNotNull(holder));
        holder.rawStartDelay = getStartDelay(holder);
        this.mNormalized = false;
        if (isStarted()) {
            normalizeStartDelays();
            float fraction = ((float) (getCurrentPlayTime() - holder.normalizedStartDelay)) / ((float) getChildAnimationDuration());
            if (fraction <= 0.0f) {
                onSetupStartValues(holder);
            } else {
                if (fraction > 1.0f) {
                    fraction = 1.0f;
                }
                onUpdateView(holder, getInterpolator().getInterpolation(fraction));
            }
        } else if (this.mState == 2) {
            onSetupStartValues(holder);
        }
        return this;
    }

    public VH removeView(int index) {
        ViewHolder holder = this.mViews.remove(index);
        long startDelay = holder.normalizedStartDelay;
        if (startDelay == 0 || startDelay == this.mMaxStartDelay) {
            this.mNormalized = false;
        }
        if (isStarted()) {
            if (!this.mNormalized) {
                normalizeStartDelays();
            }
            onResetView((VH) holder);
        } else if (this.mState == 2 || this.mState == 16) {
            onResetView((VH) holder);
        }
        return (VH) holder;
    }

    protected final void invalidateView(VH holder) {
        holder.rawStartDelay = getStartDelay(holder);
        this.mNormalized = false;
        if (isStarted()) {
            normalizeStartDelays();
        }
    }

    public VH getView(int index) {
        return this.mViews.get(index);
    }

    public int size() {
        return this.mViews.size();
    }

    public long getChildAnimationDuration() {
        if (!this.mNormalized) {
            normalizeStartDelays();
        }
        return getDuration() - this.mMaxStartDelay;
    }

    public void reset() {
        if (this.mState == 4 || this.mState == 8) {
            StringWriter buf = new StringWriter();
            PrintWriter writer = new PrintWriter(buf);
            writer.println("Reset while started");
            new Exception("stack trace").printStackTrace(writer);
            writer.println(toString());
            Log.w("Animations", buf.toString());
            cancel();
            return;
        }
        int n = this.mViews.size();
        for (int i = 0; i < n; i++) {
            onResetView(this.mViews.get(i));
        }
        this.mState = (byte) 32;
    }

    public PropagatingAnimator<VH> setDuration(long duration) {
        if (isStarted()) {
            throw new IllegalStateException("Can't alter the duration after start");
        }
        super.setDuration(duration);
        int n = this.mViews.size();
        for (int i = 0; i < n; i++) {
            invalidateView(this.mViews.get(i));
        }
        this.mNormalized = false;
        return this;
    }

    public void start() {
        if (!this.mNormalized) {
            normalizeStartDelays();
        }
        setupStartValues();
        this.mState = (byte) 4;
        super.start();
    }

    public void setupStartValues() {
        if (this.mState != (byte) 2) {
            int n = this.mViews.size();
            for (int i = 0; i < n; i++) {
                onSetupStartValues(this.mViews.get(i));
            }
            this.mState = (byte) 2;
        }
    }

    private long getStartDelay(VH holder) {
        long startDelay = this.mPropagation.getStartDelay(holder);
        if (startDelay >= 0 && startDelay < getDuration()) {
            return startDelay;
        }
        throw new UnsupportedOperationException(String.format("Illegal start delay returned by %s: %d", new Object[]{this.mPropagation, startDelay}));
    }

    private void normalizeStartDelays() {
        int i;
        this.mNormalized = true;
        int n = this.mViews.size();
        long minRawDelay = Long.MAX_VALUE;
        for (i = 0; i < n; i++) {
            minRawDelay = Math.min(minRawDelay, this.mViews.get(i).rawStartDelay);
        }
        this.mMaxStartDelay = n == 0 ? 0 : Long.MIN_VALUE;
        for (i = 0; i < n; i++) {
            ViewHolder holder = this.mViews.get(i);
            long normalizedDelay = holder.rawStartDelay - minRawDelay;
            this.mMaxStartDelay = Math.max(this.mMaxStartDelay, normalizedDelay);
            holder.normalizedStartDelay = normalizedDelay;
        }
    }

    private void addListener() {
        addListener(this.mListener);
        addUpdateListener(this.mListener);
    }

    private void removeListener() {
        removeListener(this.mListener);
        removeUpdateListener(this.mListener);
    }

    public String toString() {
        return "PropagatingAnimator@" + Integer.toHexString(hashCode());
    }
}
