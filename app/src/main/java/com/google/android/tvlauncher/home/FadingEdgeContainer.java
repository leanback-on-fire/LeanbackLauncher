package com.google.android.tvlauncher.home;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Shader.TileMode;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class FadingEdgeContainer
  extends FrameLayout
{
  private static final boolean DEBUG = false;
  private static final int[] FADE_COLORS_LTR = new int[21];
  private static final int[] FADE_COLORS_RTL = new int[21];
  private static final float[] FADE_COLOR_POSITIONS = new float[21];
  private static final int GRADIENT_CURVE_STEEPNESS = 100;
  private static final int GRADIENT_STEPS = 20;
  private static final String TAG = "FadingEdgeContainer";
  private boolean mFadeEnabled = true;
  private int mFadeWidth;
  private Paint mGradientPaint;
  private Rect mGradientRect;
  
  static
  {
    FADE_COLORS_LTR[0] = 0;
    int i = 1;
    while (i <= 20)
    {
      float f = (float)Math.pow(100.0D, i / 20.0D - 1.0D);
      FADE_COLORS_LTR[i] = Color.argb(f, 0.0F, 0.0F, 0.0F);
      FADE_COLOR_POSITIONS[i] = (i / 20.0F);
      i += 1;
    }
    i = 0;
    while (i < FADE_COLORS_LTR.length)
    {
      FADE_COLORS_RTL[(FADE_COLORS_LTR.length - i - 1)] = FADE_COLORS_LTR[i];
      i += 1;
    }
  }
  
  public FadingEdgeContainer(@NonNull Context paramContext)
  {
    super(paramContext);
    init();
  }
  
  public FadingEdgeContainer(@NonNull Context paramContext, @Nullable AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    init();
  }
  
  public FadingEdgeContainer(@NonNull Context paramContext, @Nullable AttributeSet paramAttributeSet, @AttrRes int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    init();
  }
  
  private void init()
  {
    this.mFadeWidth = getContext().getResources().getDimensionPixelOffset(R.dimen.channel_items_list_fade_width);
    this.mGradientRect = new Rect();
  }
  
  private void setUpPaint(int paramInt)
  {
    this.mGradientPaint = new Paint();
    if (getLayoutDirection() == 0) {}
    for (LinearGradient localLinearGradient = new LinearGradient(0.0F, 0.0F, this.mFadeWidth, 0.0F, FADE_COLORS_LTR, FADE_COLOR_POSITIONS, Shader.TileMode.CLAMP);; localLinearGradient = new LinearGradient(paramInt - this.mFadeWidth, 0.0F, paramInt, 0.0F, FADE_COLORS_RTL, FADE_COLOR_POSITIONS, Shader.TileMode.CLAMP))
    {
      this.mGradientPaint.setShader(localLinearGradient);
      this.mGradientPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
      return;
    }
  }
  
  protected void dispatchDraw(Canvas paramCanvas)
  {
    int i = getHeight();
    int j = getWidth();
    if ((this.mFadeEnabled) && (this.mGradientPaint == null)) {
      setUpPaint(j);
    }
    if (getLayoutDirection() == 0)
    {
      if (this.mFadeEnabled)
      {
        paramCanvas.saveLayer(0.0F, 0.0F, this.mFadeWidth, i, null);
        super.dispatchDraw(paramCanvas);
        this.mGradientRect.set(0, 0, this.mFadeWidth, i);
        paramCanvas.drawRect(this.mGradientRect, this.mGradientPaint);
        paramCanvas.restore();
      }
      paramCanvas.clipRect(this.mFadeWidth, 0, j, i);
      super.dispatchDraw(paramCanvas);
      return;
    }
    if (this.mFadeEnabled)
    {
      paramCanvas.saveLayer(0.0F, 0.0F, j, i, null);
      paramCanvas.clipRect(j - this.mFadeWidth, 0, j, i);
      super.dispatchDraw(paramCanvas);
      this.mGradientRect.set(j - this.mFadeWidth, 0, j, i);
      paramCanvas.drawRect(this.mGradientRect, this.mGradientPaint);
      paramCanvas.restore();
    }
    paramCanvas.clipRect(0, 0, j - this.mFadeWidth, i);
    super.dispatchDraw(paramCanvas);
  }
  
  public void setFadeEnabled(boolean paramBoolean)
  {
    this.mFadeEnabled = paramBoolean;
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/home/FadingEdgeContainer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */