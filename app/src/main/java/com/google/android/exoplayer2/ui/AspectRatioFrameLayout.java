package com.google.android.exoplayer2.ui;

import android.content.Context;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import com.google.android.exoplayer2.R.styleable;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class AspectRatioFrameLayout
  extends FrameLayout
{
  private static final float MAX_ASPECT_RATIO_DEFORMATION_FRACTION = 0.01F;
  public static final int RESIZE_MODE_FILL = 3;
  public static final int RESIZE_MODE_FIT = 0;
  public static final int RESIZE_MODE_FIXED_HEIGHT = 2;
  public static final int RESIZE_MODE_FIXED_WIDTH = 1;
  private int resizeMode = 0;
  private float videoAspectRatio;
  
  public AspectRatioFrameLayout(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public AspectRatioFrameLayout(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    if (paramAttributeSet != null) {
      paramContext = paramContext.getTheme().obtainStyledAttributes(paramAttributeSet, R.styleable.AspectRatioFrameLayout, 0, 0);
    }
    try
    {
      this.resizeMode = paramContext.getInt(R.styleable.AspectRatioFrameLayout_resize_mode, 0);
      return;
    }
    finally
    {
      paramContext.recycle();
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(paramInt1, paramInt2);
    if ((this.resizeMode == 3) || (this.videoAspectRatio <= 0.0F)) {}
    float f;
    do
    {
      return;
      paramInt2 = getMeasuredWidth();
      paramInt1 = getMeasuredHeight();
      f = paramInt2 / paramInt1;
      f = this.videoAspectRatio / f - 1.0F;
    } while (Math.abs(f) <= 0.01F);
    switch (this.resizeMode)
    {
    default: 
      if (f > 0.0F) {
        paramInt1 = (int)(paramInt2 / this.videoAspectRatio);
      }
      break;
    }
    for (;;)
    {
      super.onMeasure(View.MeasureSpec.makeMeasureSpec(paramInt2, 1073741824), View.MeasureSpec.makeMeasureSpec(paramInt1, 1073741824));
      return;
      paramInt1 = (int)(paramInt2 / this.videoAspectRatio);
      continue;
      paramInt2 = (int)(paramInt1 * this.videoAspectRatio);
      continue;
      paramInt2 = (int)(paramInt1 * this.videoAspectRatio);
    }
  }
  
  public void setAspectRatio(float paramFloat)
  {
    if (this.videoAspectRatio != paramFloat)
    {
      this.videoAspectRatio = paramFloat;
      requestLayout();
    }
  }
  
  public void setResizeMode(int paramInt)
  {
    if (this.resizeMode != paramInt)
    {
      this.resizeMode = paramInt;
      requestLayout();
    }
  }
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface ResizeMode {}
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/ui/AspectRatioFrameLayout.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */