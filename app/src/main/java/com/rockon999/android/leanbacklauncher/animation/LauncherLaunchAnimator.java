package com.rockon999.android.leanbacklauncher.animation;

import android.animation.Animator;
import android.animation.AnimatorSet.Builder;
import android.content.res.Resources;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.rockon999.android.leanbacklauncher.R;

public final class LauncherLaunchAnimator extends ForwardingAnimatorSet {
    public LauncherLaunchAnimator(ViewGroup root, View cause, Rect epicenter, ImageView circleLayerView, int color, View[] headers) {
        Resources res = root.getResources();
        int fadeDuration = res.getInteger(R.integer.app_launch_animation_header_fade_out_duration);
        int fadeDelay = res.getInteger(R.integer.app_launch_animation_header_fade_out_delay);
        Animator anim = new CircleTakeoverAnimator(cause, circleLayerView, color);
        anim.setDuration((long) res.getInteger(R.integer.app_launch_animation_explode_duration));
        Builder builder = this.mDelegate.play(anim);
        anim = new FadeAnimator(cause, FadeAnimator.Direction.FADE_OUT);
        anim.setDuration((long) res.getInteger(R.integer.app_launch_animation_target_fade_duration));
        anim.setStartDelay((long) res.getInteger(R.integer.app_launch_animation_target_fade_delay));
        builder.with(anim);
        builder.with(new MassSlideAnimator.Builder(root).setEpicenter(epicenter).setExclude(cause).setFade(false).build());

        for (View header : headers) {
            anim = new FadeAnimator(header, FadeAnimator.Direction.FADE_OUT);
            anim.setDuration((long) fadeDuration);
            anim.setStartDelay((long) fadeDelay);
            builder.with(anim);
        }
    }
}
