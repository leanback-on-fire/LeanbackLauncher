package android.support.v17.leanback.graphics;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v17.leanback.R.color;
import android.support.v17.leanback.R.fraction;
import android.support.v17.leanback.R.styleable;
import android.view.View;

public final class ColorOverlayDimmer
{
  private final float mActiveLevel;
  private int mAlpha;
  private float mAlphaFloat;
  private final float mDimmedLevel;
  private final Paint mPaint;
  
  private ColorOverlayDimmer(int paramInt, float paramFloat1, float paramFloat2)
  {
    float f1 = paramFloat1;
    if (paramFloat1 > 1.0F) {
      f1 = 1.0F;
    }
    float f2 = f1;
    if (f1 < 0.0F) {
      f2 = 0.0F;
    }
    paramFloat1 = paramFloat2;
    if (paramFloat2 > 1.0F) {
      paramFloat1 = 1.0F;
    }
    paramFloat2 = paramFloat1;
    if (paramFloat1 < 0.0F) {
      paramFloat2 = 0.0F;
    }
    this.mPaint = new Paint();
    paramInt = Color.rgb(Color.red(paramInt), Color.green(paramInt), Color.blue(paramInt));
    this.mPaint.setColor(paramInt);
    this.mActiveLevel = f2;
    this.mDimmedLevel = paramFloat2;
    setActiveLevel(1.0F);
  }
  
  public static ColorOverlayDimmer createColorOverlayDimmer(int paramInt, float paramFloat1, float paramFloat2)
  {
    return new ColorOverlayDimmer(paramInt, paramFloat1, paramFloat2);
  }
  
  public static ColorOverlayDimmer createDefault(Context paramContext)
  {
    TypedArray localTypedArray = paramContext.obtainStyledAttributes(R.styleable.LeanbackTheme);
    int i = localTypedArray.getColor(R.styleable.LeanbackTheme_overlayDimMaskColor, paramContext.getResources().getColor(R.color.lb_view_dim_mask_color));
    float f1 = localTypedArray.getFraction(R.styleable.LeanbackTheme_overlayDimActiveLevel, 1, 1, paramContext.getResources().getFraction(R.fraction.lb_view_active_level, 1, 0));
    float f2 = localTypedArray.getFraction(R.styleable.LeanbackTheme_overlayDimDimmedLevel, 1, 1, paramContext.getResources().getFraction(R.fraction.lb_view_dimmed_level, 1, 1));
    localTypedArray.recycle();
    return new ColorOverlayDimmer(i, f1, f2);
  }
  
  public int applyToColor(int paramInt)
  {
    float f = 1.0F - this.mAlphaFloat;
    return Color.argb(Color.alpha(paramInt), (int)(Color.red(paramInt) * f), (int)(Color.green(paramInt) * f), (int)(Color.blue(paramInt) * f));
  }
  
  public void drawColorOverlay(Canvas paramCanvas, View paramView, boolean paramBoolean)
  {
    paramCanvas.save();
    float f1 = paramView.getLeft() + paramView.getTranslationX();
    float f2 = paramView.getTop() + paramView.getTranslationY();
    paramCanvas.translate(f1, f2);
    paramCanvas.concat(paramView.getMatrix());
    paramCanvas.translate(-f1, -f2);
    if (paramBoolean) {
      paramCanvas.drawRect(paramView.getLeft(), paramView.getTop(), paramView.getRight(), paramView.getBottom(), this.mPaint);
    }
    for (;;)
    {
      paramCanvas.restore();
      return;
      paramCanvas.drawRect(paramView.getLeft() + paramView.getPaddingLeft(), paramView.getTop() + paramView.getPaddingTop(), paramView.getRight() - paramView.getPaddingRight(), paramView.getBottom() - paramView.getPaddingBottom(), this.mPaint);
    }
  }
  
  public int getAlpha()
  {
    return this.mAlpha;
  }
  
  public float getAlphaFloat()
  {
    return this.mAlphaFloat;
  }
  
  public Paint getPaint()
  {
    return this.mPaint;
  }
  
  public boolean needsDraw()
  {
    return this.mAlpha != 0;
  }
  
  public void setActiveLevel(float paramFloat)
  {
    this.mAlphaFloat = (this.mDimmedLevel + (this.mActiveLevel - this.mDimmedLevel) * paramFloat);
    this.mAlpha = ((int)(255.0F * this.mAlphaFloat));
    this.mPaint.setAlpha(this.mAlpha);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/graphics/ColorOverlayDimmer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */