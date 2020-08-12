package com.amazon.tv.leanbacklauncher.animation;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.view.View;

import java.util.Iterator;

public abstract class ForwardingAnimatorSet extends ForwardingAnimator<AnimatorSet> {
    protected ForwardingAnimatorSet() {
        super(new AnimatorSet());
    }

    public void reset() {
        Iterator it = this.mDelegate.getChildAnimations().iterator();
        while (it.hasNext()) {
            Animator animation = (Animator) it.next();
            if (animation instanceof Resettable) {
                ((Resettable) animation).reset();
            }
        }
    }

    public void include(View target) {
        Iterator it = this.mDelegate.getChildAnimations().iterator();
        while (it.hasNext()) {
            Animator animation = (Animator) it.next();
            if (animation instanceof Joinable) {
                ((Joinable) animation).include(target);
            }
        }
    }

    public void exclude(View target) {
        Iterator it = this.mDelegate.getChildAnimations().iterator();
        while (it.hasNext()) {
            Animator animation = (Animator) it.next();
            if (animation instanceof Joinable) {
                ((Joinable) animation).exclude(target);
            }
        }
    }

    public String toString() {
        StringBuilder buf = new StringBuilder().append(getClass().getSimpleName()).append('@').append(Integer.toHexString(hashCode())).append('{');
        Iterator it = this.mDelegate.getChildAnimations().iterator();
        while (it.hasNext()) {
            buf.append("\n    ").append(it.next().toString().replaceAll("\n", "\n    "));
        }
        return buf.append("\n}").toString();
    }
}
