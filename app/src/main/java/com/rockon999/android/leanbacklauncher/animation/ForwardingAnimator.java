package com.rockon999.android.leanbacklauncher.animation;

import android.animation.Animator;
import android.animation.TimeInterpolator;
import android.util.ArrayMap;
import android.view.View;
import com.rockon999.android.leanbacklauncher.util.Preconditions;

import java.util.ArrayList;

public abstract class ForwardingAnimator<T extends Animator> extends Animator implements Resettable, Joinable {
    protected final T mDelegate;
    private ArrayMap<AnimatorListener, AnimatorListener> mListeners;
    private ArrayMap<AnimatorPauseListener, AnimatorPauseListener> mPauseListeners;

    private static final class ProxyingAnimatorListener implements AnimatorListener {
        private final AnimatorListener mDelegate;
        private final Animator mProxyAnimator;

        public ProxyingAnimatorListener(AnimatorListener delegate, Animator proxyAnimator) {
            this.mDelegate = delegate;
            this.mProxyAnimator = proxyAnimator;
        }

        public void onAnimationStart(Animator unused) {
            this.mDelegate.onAnimationStart(this.mProxyAnimator);
        }

        public void onAnimationEnd(Animator unused) {
            this.mDelegate.onAnimationEnd(this.mProxyAnimator);
        }

        public void onAnimationCancel(Animator unused) {
            this.mDelegate.onAnimationCancel(this.mProxyAnimator);
        }

        public void onAnimationRepeat(Animator unused) {
            this.mDelegate.onAnimationRepeat(this.mProxyAnimator);
        }
    }

    private static final class ProxyingAnimatorPauseListener implements AnimatorPauseListener {
        private final AnimatorPauseListener mDelegate;
        private final Animator mProxyAnimator;

        public ProxyingAnimatorPauseListener(AnimatorPauseListener delegate, Animator proxyAnimator) {
            this.mDelegate = delegate;
            this.mProxyAnimator = proxyAnimator;
        }

        public void onAnimationPause(Animator unused) {
            this.mDelegate.onAnimationPause(this.mProxyAnimator);
        }

        public void onAnimationResume(Animator unused) {
            this.mDelegate.onAnimationResume(this.mProxyAnimator);
        }
    }

    public ForwardingAnimator(T delegate) {
        this.mDelegate = Preconditions.checkNotNull(delegate);
    }

    public void reset() {
        if (this.mDelegate instanceof Resettable) {
            ((Resettable) this.mDelegate).reset();
        }
    }

    public void include(View target) {
        if (this.mDelegate instanceof Joinable) {
            ((Joinable) this.mDelegate).include(target);
        }
    }

    public void exclude(View target) {
        if (this.mDelegate instanceof Joinable) {
            ((Joinable) this.mDelegate).exclude(target);
        }
    }

    public void start() {
        this.mDelegate.start();
    }

    public void cancel() {
        this.mDelegate.cancel();
    }

    public void end() {
        this.mDelegate.end();
    }

    public void pause() {
        this.mDelegate.pause();
    }

    public void resume() {
        this.mDelegate.resume();
    }

    public boolean isPaused() {
        return this.mDelegate.isPaused();
    }

    public long getStartDelay() {
        return this.mDelegate.getStartDelay();
    }

    public void setStartDelay(long startDelay) {
        this.mDelegate.setStartDelay(startDelay);
    }

    public long getDuration() {
        return this.mDelegate.getDuration();
    }

    public Animator setDuration(long duration) {
        this.mDelegate.setDuration(duration);
        return this;
    }

    public TimeInterpolator getInterpolator() {
        return this.mDelegate.getInterpolator();
    }

    public void setInterpolator(TimeInterpolator value) {
        this.mDelegate.setInterpolator(value);
    }

    public void addListener(AnimatorListener listener) {
        if (this.mListeners == null) {
            this.mListeners = new ArrayMap();
        }
        if (!this.mListeners.containsKey(listener)) {
            AnimatorListener proxy = new ProxyingAnimatorListener(listener, this);
            this.mListeners.put(listener, proxy);
            this.mDelegate.addListener(proxy);
        }
    }

    public void removeListener(AnimatorListener listener) {
        if (this.mListeners != null) {
            AnimatorListener proxy = this.mListeners.remove(listener);
            if (proxy != null) {
                this.mDelegate.removeListener(proxy);
            }
            if (this.mListeners.isEmpty()) {
                this.mListeners = null;
            }
        }
    }

    public ArrayList<AnimatorListener> getListeners() {
        if (this.mListeners == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(this.mListeners.keySet());
    }

    public void addPauseListener(AnimatorPauseListener listener) {
        if (this.mPauseListeners == null) {
            this.mPauseListeners = new ArrayMap();
        }
        if (!this.mPauseListeners.containsKey(listener)) {
            AnimatorPauseListener proxy = new ProxyingAnimatorPauseListener(listener, this);
            this.mPauseListeners.put(listener, proxy);
            this.mDelegate.addPauseListener(proxy);
        }
    }

    public void removePauseListener(AnimatorPauseListener listener) {
        if (this.mPauseListeners != null) {
            AnimatorPauseListener proxy = this.mPauseListeners.remove(listener);
            if (proxy != null) {
                this.mDelegate.removePauseListener(proxy);
            }
            if (this.mPauseListeners.isEmpty()) {
                this.mPauseListeners = null;
            }
        }
    }

    public void removeAllListeners() {
        this.mDelegate.removeAllListeners();
        this.mListeners = null;
        this.mPauseListeners = null;
    }

    public boolean isRunning() {
        return this.mDelegate.isRunning();
    }

    public boolean isStarted() {
        return this.mDelegate.isStarted();
    }

    public void setupEndValues() {
        this.mDelegate.setupEndValues();
    }

    public void setupStartValues() {
        this.mDelegate.setupStartValues();
    }

    public void setTarget(Object target) {
        this.mDelegate.setTarget(target);
    }

    public String toString() {
        return "ForwardingAnimator@" + Integer.toHexString(hashCode()) + '{' + this.mDelegate.toString() + '}';
    }
}
