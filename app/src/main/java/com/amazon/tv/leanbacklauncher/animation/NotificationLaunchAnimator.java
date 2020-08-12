package com.amazon.tv.leanbacklauncher.animation;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.AnimatorSet.Builder;
import android.content.res.Resources;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.amazon.tv.leanbacklauncher.R;
import com.amazon.tv.leanbacklauncher.notifications.HomeScreenView;
import com.amazon.tv.leanbacklauncher.notifications.NotificationCardView;

public final class NotificationLaunchAnimator extends ForwardingAnimatorSet {
    public NotificationLaunchAnimator(ViewGroup root, NotificationCardView cause, Rect epicenter, ImageView circleLayerView, int color, View[] headers, HomeScreenView homeScreenView) {
        Resources res = root.getResources();
        int fadeDuration = res.getInteger(R.integer.app_launch_animation_header_fade_out_duration);
        int fadeDelay = res.getInteger(R.integer.app_launch_animation_header_fade_out_delay);
        Animator anim = new CircleTakeoverAnimator(cause, circleLayerView, color);
        anim.setDuration(res.getInteger(R.integer.app_launch_animation_explode_duration));
        Builder builder = this.mDelegate.play(anim);
        builder.with(new MassFadeAnimator.Builder(root).setDirection(MassFadeAnimator.Direction.FADE_OUT).setTarget(NotificationCardView.class).setDuration(res.getInteger(R.integer.app_launch_animation_rec_fade_duration)).build());
        builder.with(new MassSlideAnimator.Builder(root).setEpicenter(epicenter).setExclude(cause).setExclude(NotificationCardView.class).setFade(false).build());
        for (View fadeAnimator : headers) {
            anim = new FadeAnimator(fadeAnimator, FadeAnimator.Direction.FADE_OUT);
            anim.setDuration(fadeDuration);
            anim.setStartDelay(fadeDelay);
            builder.with(anim);
        }
    }
}
