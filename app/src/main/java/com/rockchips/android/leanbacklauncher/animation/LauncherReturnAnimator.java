package com.rockchips.android.leanbacklauncher.animation;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.AnimatorSet.Builder;
import android.content.res.Resources;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;

import com.rockchips.android.leanbacklauncher.R;
import com.rockchips.android.leanbacklauncher.animation.MassSlideAnimator.Direction;
import com.rockchips.android.leanbacklauncher.notifications.HomeScreenView;
import com.rockchips.android.leanbacklauncher.notifications.NotificationCardView;

public class LauncherReturnAnimator extends ForwardingAnimatorSet {
    public LauncherReturnAnimator(ViewGroup root, Rect epicenter, View[] headers, HomeScreenView homeScreenView) {
        Builder builder;
        Resources res = root.getResources();
        int fadeDuration = res.getInteger(R.integer.app_launch_animation_header_fade_in_duration);
        int fadeDelay = res.getInteger(R.integer.app_launch_animation_header_fade_in_delay);
        if (root.findFocus() instanceof NotificationCardView) {
            builder = ((AnimatorSet) this.mDelegate).play(new MassSlideAnimator.Builder(root).setEpicenter(epicenter).setDirection(Direction.SLIDE_IN).setExclude(NotificationCardView.class).setFade(false).build());
            builder.with(new MassFadeAnimator.Builder(root).setDirection(MassFadeAnimator.Direction.FADE_IN).setTarget(NotificationCardView.class).setDuration((long) res.getInteger(R.integer.app_launch_animation_rec_fade_duration)).build());
        } else {
            builder = ((AnimatorSet) this.mDelegate).play(new MassSlideAnimator.Builder(root).setEpicenter(epicenter).setDirection(Direction.SLIDE_IN).setFade(false).build());
            if (!(homeScreenView == null || homeScreenView.isRowViewVisible())) {
                Animator anim = new FadeAnimator(homeScreenView, FadeAnimator.Direction.FADE_IN);
                anim.setDuration((long) fadeDuration);
                anim.setStartDelay((long) fadeDelay);
                builder.with(anim);
            }
        }
        for (View header : headers) {
            FadeAnimator anim = new FadeAnimator(header, FadeAnimator.Direction.FADE_IN);
            anim.setDuration((long) fadeDuration);
            anim.setStartDelay((long) fadeDelay);
            builder.with(anim);
        }
    }
}
