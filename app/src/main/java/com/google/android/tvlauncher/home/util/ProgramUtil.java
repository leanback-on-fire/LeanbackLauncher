package com.google.android.tvlauncher.home.util;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import com.google.android.tvlauncher.R;

public class ProgramUtil {
    public static final double ASPECT_RATIO_16_9 = 1.7777777777777777d;
    public static final double ASPECT_RATIO_1_1 = 1.0d;
    public static final double ASPECT_RATIO_2_3 = 0.6666666666666666d;
    public static final double ASPECT_RATIO_3_2 = 1.5d;
    public static final double ASPECT_RATIO_4_3 = 1.3333333333333333d;

    public static ProgramSettings getProgramSettings(Context context) {
        Resources resources = context.getResources();
        ProgramSettings settings = new ProgramSettings();
        settings.defaultHeight = resources.getDimensionPixelSize(R.dimen.program_default_height);
        settings.defaultTopMargin = resources.getDimensionPixelSize(R.dimen.program_default_margin_top);
        settings.defaultBottomMargin = resources.getDimensionPixelSize(R.dimen.program_default_margin_bottom);
        settings.defaultHorizontalMargin = resources.getDimensionPixelSize(R.dimen.program_default_margin_horizontal);
        settings.selectedHeight = resources.getDimensionPixelSize(R.dimen.program_selected_height);
        settings.selectedVerticalMargin = resources.getDimensionPixelSize(R.dimen.program_selected_margin_vertical);
        settings.zoomedOutHeight = resources.getDimensionPixelSize(R.dimen.program_zoomed_out_height);
        settings.zoomedOutVerticalMargin = resources.getDimensionPixelSize(R.dimen.program_zoomed_out_margin_vertical);
        settings.zoomedOutHorizontalMargin = resources.getDimensionPixelSize(R.dimen.program_zoomed_out_margin_horizontal);
        settings.focusedAnimationDuration = resources.getInteger(R.integer.program_focused_animation_duration_ms);
        settings.focusedScale = resources.getFraction(R.fraction.program_focused_scale, 1, 1);
        settings.focusedElevation = resources.getDimension(R.dimen.program_focused_elevation);
        return settings;
    }

    public static double getAspectRatio(int aspectRatio) {
        switch (aspectRatio) {
            case 1:
                return ASPECT_RATIO_3_2;
            case 2:
                return ASPECT_RATIO_4_3;
            case 3:
                return ASPECT_RATIO_1_1;
            case 4:
                return ASPECT_RATIO_2_3;
            default:
                return ASPECT_RATIO_16_9;
        }
    }

    public static void updateSize(View programView, int programState, double aspectRatio, ProgramSettings programSettings) {
        MarginLayoutParams lp = (MarginLayoutParams) programView.getLayoutParams();
        switch (programState) {
            case 0:
                lp.height = programSettings.defaultHeight;
                lp.setMargins(0, programSettings.defaultTopMargin, 0, programSettings.defaultBottomMargin);
                lp.setMarginEnd(programSettings.defaultHorizontalMargin);
                break;
            case 1:
                lp.height = programSettings.selectedHeight;
                lp.setMargins(0, programSettings.selectedVerticalMargin, 0, programSettings.selectedVerticalMargin);
                lp.setMarginEnd(programSettings.defaultHorizontalMargin);
                break;
            case 2:
                lp.height = programSettings.zoomedOutHeight;
                lp.setMargins(0, programSettings.zoomedOutVerticalMargin, 0, programSettings.zoomedOutVerticalMargin);
                lp.setMarginEnd(programSettings.zoomedOutHorizontalMargin);
                break;
        }
        lp.width = (int) Math.round(((double) lp.height) * aspectRatio);
        programView.setLayoutParams(lp);
    }
}
