package com.google.android.tvlauncher.home.util;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;

public class ProgramUtil
{
  public static final double ASPECT_RATIO_16_9 = 1.7777777777777777D;
  public static final double ASPECT_RATIO_1_1 = 1.0D;
  public static final double ASPECT_RATIO_2_3 = 0.6666666666666666D;
  public static final double ASPECT_RATIO_3_2 = 1.5D;
  public static final double ASPECT_RATIO_4_3 = 1.3333333333333333D;
  
  public static double getAspectRatio(int paramInt)
  {
    switch (paramInt)
    {
    case 0: 
    default: 
      return 1.7777777777777777D;
    case 1: 
      return 1.5D;
    case 3: 
      return 1.0D;
    case 4: 
      return 0.6666666666666666D;
    }
    return 1.3333333333333333D;
  }
  
  public static ProgramSettings getProgramSettings(Context paramContext)
  {
    paramContext = paramContext.getResources();
    ProgramSettings localProgramSettings = new ProgramSettings();
    localProgramSettings.defaultHeight = paramContext.getDimensionPixelSize(R.dimen.program_default_height);
    localProgramSettings.defaultTopMargin = paramContext.getDimensionPixelSize(R.dimen.program_default_margin_top);
    localProgramSettings.defaultBottomMargin = paramContext.getDimensionPixelSize(R.dimen.program_default_margin_bottom);
    localProgramSettings.defaultHorizontalMargin = paramContext.getDimensionPixelSize(R.dimen.program_default_margin_horizontal);
    localProgramSettings.selectedHeight = paramContext.getDimensionPixelSize(R.dimen.program_selected_height);
    localProgramSettings.selectedVerticalMargin = paramContext.getDimensionPixelSize(R.dimen.program_selected_margin_vertical);
    localProgramSettings.zoomedOutHeight = paramContext.getDimensionPixelSize(R.dimen.program_zoomed_out_height);
    localProgramSettings.zoomedOutVerticalMargin = paramContext.getDimensionPixelSize(R.dimen.program_zoomed_out_margin_vertical);
    localProgramSettings.zoomedOutHorizontalMargin = paramContext.getDimensionPixelSize(R.dimen.program_zoomed_out_margin_horizontal);
    localProgramSettings.focusedAnimationDuration = paramContext.getInteger(R.integer.program_focused_animation_duration_ms);
    localProgramSettings.focusedScale = paramContext.getFraction(2131886096, 1, 1);
    localProgramSettings.focusedElevation = paramContext.getDimension(R.dimen.program_focused_elevation);
    return localProgramSettings;
  }
  
  public static void updateSize(View paramView, int paramInt, double paramDouble, ProgramSettings paramProgramSettings)
  {
    ViewGroup.MarginLayoutParams localMarginLayoutParams = (ViewGroup.MarginLayoutParams)paramView.getLayoutParams();
    switch (paramInt)
    {
    }
    for (;;)
    {
      localMarginLayoutParams.width = ((int)Math.round(localMarginLayoutParams.height * paramDouble));
      paramView.setLayoutParams(localMarginLayoutParams);
      return;
      localMarginLayoutParams.height = paramProgramSettings.defaultHeight;
      localMarginLayoutParams.setMargins(0, paramProgramSettings.defaultTopMargin, 0, paramProgramSettings.defaultBottomMargin);
      localMarginLayoutParams.setMarginEnd(paramProgramSettings.defaultHorizontalMargin);
      continue;
      localMarginLayoutParams.height = paramProgramSettings.selectedHeight;
      localMarginLayoutParams.setMargins(0, paramProgramSettings.selectedVerticalMargin, 0, paramProgramSettings.selectedVerticalMargin);
      localMarginLayoutParams.setMarginEnd(paramProgramSettings.defaultHorizontalMargin);
      continue;
      localMarginLayoutParams.height = paramProgramSettings.zoomedOutHeight;
      localMarginLayoutParams.setMargins(0, paramProgramSettings.zoomedOutVerticalMargin, 0, paramProgramSettings.zoomedOutVerticalMargin);
      localMarginLayoutParams.setMarginEnd(paramProgramSettings.zoomedOutHorizontalMargin);
    }
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/home/util/ProgramUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */