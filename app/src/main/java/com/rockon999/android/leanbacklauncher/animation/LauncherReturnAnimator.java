package com.rockon999.android.leanbacklauncher.animation;

import android.animation.AnimatorSet.Builder;
import android.content.res.Resources;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;

import com.rockon999.android.leanbacklauncher.R;
import com.rockon999.android.leanbacklauncher.animation.MassSlideAnimator.Direction;

public class LauncherReturnAnimator extends ForwardingAnimatorSet {
    public LauncherReturnAnimator(ViewGroup root, Rect epicenter, View[] headers) {
        Builder builder;
        Resources res = root.getResources();
        int fadeDuration = res.getInteger(R.integer.app_launch_animation_header_fade_in_duration);
        int fadeDelay = res.getInteger(R.integer.app_launch_animation_header_fade_in_delay);

        builder = this.mDelegate.play(new MassSlideAnimator.Builder(root).setEpicenter(epicenter).setDirection(Direction.SLIDE_IN).setFade(false).build());


        for (View header : headers) {
            FadeAnimator anim = new FadeAnimator(header, FadeAnimator.Direction.FADE_IN);
            anim.setDuration((long) fadeDuration);
            anim.setStartDelay((long) fadeDelay);
            builder.with(anim);
        }
    }
}
