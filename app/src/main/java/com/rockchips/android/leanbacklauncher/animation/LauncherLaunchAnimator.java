package com.rockchips.android.leanbacklauncher.animation;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.AnimatorSet.Builder;
import android.content.res.Resources;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.rockchips.android.leanbacklauncher.R;
import com.rockchips.android.leanbacklauncher.notifications.HomeScreenView;

public final class LauncherLaunchAnimator extends ForwardingAnimatorSet {
    public LauncherLaunchAnimator(ViewGroup root, View cause, Rect epicenter, ImageView circleLayerView, int color, View[] headers, HomeScreenView homeScreenView) {
        Resources res = root.getResources();
        int fadeDuration = res.getInteger(R.integer.app_launch_animation_header_fade_out_duration);
        int fadeDelay = res.getInteger(R.integer.app_launch_animation_header_fade_out_delay);
        Animator anim = new CircleTakeoverAnimator(cause, circleLayerView, color);
        anim.setDuration((long) res.getInteger(R.integer.app_launch_animation_explode_duration));
        Builder builder = ((AnimatorSet) this.mDelegate).play(anim);
        anim = new FadeAnimator(cause, FadeAnimator.Direction.FADE_OUT);
        anim.setDuration((long) res.getInteger(R.integer.app_launch_animation_target_fade_duration));
        anim.setStartDelay((long) res.getInteger(R.integer.app_launch_animation_target_fade_delay));
        builder.with(anim);
        builder.with(new MassSlideAnimator.Builder(root).setEpicenter(epicenter).setExclude(cause).setFade(false).build());
        if (!(homeScreenView == null || homeScreenView.isRowViewVisible())) {
            anim = new FadeAnimator(homeScreenView, FadeAnimator.Direction.FADE_OUT);
            anim.setDuration((long) fadeDuration);
            anim.setStartDelay((long) fadeDelay);
            builder.with(anim);
        }
        for (View header : headers) {
            anim = new FadeAnimator(header, FadeAnimator.Direction.FADE_OUT);
            anim.setDuration((long) fadeDuration);
            anim.setStartDelay((long) fadeDelay);
            builder.with(anim);
        }
    }
}
