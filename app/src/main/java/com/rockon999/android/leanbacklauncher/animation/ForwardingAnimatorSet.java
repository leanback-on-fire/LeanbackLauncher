package com.rockon999.android.leanbacklauncher.animation;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.view.View;

public abstract class ForwardingAnimatorSet extends ForwardingAnimator<AnimatorSet> {
    protected ForwardingAnimatorSet() {
        super(new AnimatorSet());
    }

    public void reset() {
        for (Animator animation : this.mDelegate.getChildAnimations()) {
            if (animation instanceof Resettable) {
                ((Resettable) animation).reset();
            }
        }
    }

    public void include(View target) {
        for (Animator animation : this.mDelegate.getChildAnimations()) {
            if (animation instanceof Joinable) {
                ((Joinable) animation).include(target);
            }
        }
    }

    public void exclude(View target) {
        for (Animator animation : this.mDelegate.getChildAnimations()) {
            if (animation instanceof Joinable) {
                ((Joinable) animation).exclude(target);
            }
        }
    }

    public String toString() {
        StringBuilder buf = new StringBuilder().append(getClass().getSimpleName()).append('@').append(Integer.toHexString(hashCode())).append('{');
        for (Animator animation : this.mDelegate.getChildAnimations()) {
            buf.append("\n    ").append(animation.toString().replaceAll("\n", "\n    "));
        }
        return buf.append("\n}").toString();
    }
}
