package com.rockon999.android.leanbacklauncher.animation;

import android.animation.Animator;
import android.content.res.Resources;
import android.view.ViewGroup;

import com.rockon999.android.leanbacklauncher.R;

public final class LauncherPauseAnimator extends ForwardingAnimatorSet {
    public LauncherPauseAnimator(ViewGroup root) {
        Resources res = root.getResources();
        Animator anim = new FadeAnimator(root, FadeAnimator.Direction.FADE_OUT);
        anim.setDuration((long) res.getInteger(R.integer.launcher_pause_animation_duration));
        this.mDelegate.play(anim);
    }
}
