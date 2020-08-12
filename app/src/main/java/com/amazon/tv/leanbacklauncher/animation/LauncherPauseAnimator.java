package com.amazon.tv.leanbacklauncher.animation;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.content.res.Resources;
import android.view.ViewGroup;

import com.amazon.tv.leanbacklauncher.R;

public final class LauncherPauseAnimator extends ForwardingAnimatorSet {
    public LauncherPauseAnimator(ViewGroup root) {
        Resources res = root.getResources();
        Animator anim = new FadeAnimator(root, FadeAnimator.Direction.FADE_OUT);
        anim.setDuration(res.getInteger(R.integer.launcher_pause_animation_duration));
        this.mDelegate.play(anim);
    }
}
