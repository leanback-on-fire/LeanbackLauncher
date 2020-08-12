package com.amazon.tv.leanbacklauncher.animation;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.AnimatorSet.Builder;
import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;

import com.amazon.tv.leanbacklauncher.R;

public class LauncherDismissAnimator extends ForwardingAnimatorSet {
    public LauncherDismissAnimator(ViewGroup root, boolean fade, View[] headers) {
        Builder builder = this.mDelegate.play(new MassSlideAnimator.Builder(root).setDirection(MassSlideAnimator.Direction.SLIDE_OUT).setFade(fade).build());
        Resources res = root.getResources();
        int fadeDuration = res.getInteger(R.integer.app_launch_animation_header_fade_out_duration);
        int fadeDelay = res.getInteger(R.integer.app_launch_animation_header_fade_out_delay);
        for (View header : headers) {
            Animator anim = new FadeAnimator(header, FadeAnimator.Direction.FADE_OUT);
            anim.setDuration(fadeDuration);
            anim.setStartDelay(fadeDelay);
            builder.with(anim);
        }
    }
}
