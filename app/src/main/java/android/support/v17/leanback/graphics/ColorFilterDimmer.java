package android.support.v17.leanback.graphics;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.support.v17.leanback.R.color;
import android.support.v17.leanback.R.fraction;
import android.support.v17.leanback.R.styleable;
import android.view.View;

public final class ColorFilterDimmer
{
  private final float mActiveLevel;
  private final ColorFilterCache mColorDimmer;
  private final float mDimmedLevel;
  private ColorFilter mFilter;
  private final Paint mPaint;
  
  private ColorFilterDimmer(ColorFilterCache paramColorFilterCache, float paramFloat1, float paramFloat2)
  {
    this.mColorDimmer = paramColorFilterCache;
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
    this.mActiveLevel = f2;
    this.mDimmedLevel = paramFloat2;
    this.mPaint = new Paint();
  }
  
  public static ColorFilterDimmer create(ColorFilterCache paramColorFilterCache, float paramFloat1, float paramFloat2)
  {
    return new ColorFilterDimmer(paramColorFilterCache, paramFloat1, paramFloat2);
  }
  
  public static ColorFilterDimmer createDefault(Context paramContext)
  {
    TypedArray localTypedArray = paramContext.obtainStyledAttributes(R.styleable.LeanbackTheme);
    int i = localTypedArray.getColor(R.styleable.LeanbackTheme_overlayDimMaskColor, paramContext.getResources().getColor(R.color.lb_view_dim_mask_color));
    float f1 = localTypedArray.getFraction(R.styleable.LeanbackTheme_overlayDimActiveLevel, 1, 1, paramContext.getResources().getFraction(R.fraction.lb_view_active_level, 1, 0));
    float f2 = localTypedArray.getFraction(R.styleable.LeanbackTheme_overlayDimDimmedLevel, 1, 1, paramContext.getResources().getFraction(R.fraction.lb_view_dimmed_level, 1, 1));
    localTypedArray.recycle();
    return new ColorFilterDimmer(ColorFilterCache.getColorFilterCache(i), f1, f2);
  }
  
  public void applyFilterToView(View paramView)
  {
    if (this.mFilter != null) {
      paramView.setLayerType(2, this.mPaint);
    }
    for (;;)
    {
      paramView.invalidate();
      return;
      paramView.setLayerType(0, null);
    }
  }
  
  public ColorFilter getColorFilter()
  {
    return this.mFilter;
  }
  
  public Paint getPaint()
  {
    return this.mPaint;
  }
  
  public void setActiveLevel(float paramFloat)
  {
    float f = paramFloat;
    if (paramFloat < 0.0F) {
      f = 0.0F;
    }
    paramFloat = f;
    if (f > 1.0F) {
      paramFloat = 1.0F;
    }
    this.mFilter = this.mColorDimmer.getFilterForLevel(this.mDimmedLevel + (this.mActiveLevel - this.mDimmedLevel) * paramFloat);
    this.mPaint.setColorFilter(this.mFilter);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/graphics/ColorFilterDimmer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */