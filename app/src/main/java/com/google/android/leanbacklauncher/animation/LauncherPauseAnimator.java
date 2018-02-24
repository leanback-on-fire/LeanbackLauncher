package com.google.android.leanbacklauncher.animation;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.content.res.Resources;
import android.view.ViewGroup;
import com.google.android.leanbacklauncher.R;
import com.google.android.leanbacklauncher.animation.FadeAnimator.Direction;

public final class LauncherPauseAnimator extends ForwardingAnimatorSet {
    public LauncherPauseAnimator(ViewGroup root) {
        Resources res = root.getResources();
        Animator anim = new FadeAnimator(root, Direction.FADE_OUT);
        anim.setDuration((long) res.getInteger(R.integer.launcher_pause_animation_duration));
        ((AnimatorSet) this.mDelegate).play(anim);
    }
}
