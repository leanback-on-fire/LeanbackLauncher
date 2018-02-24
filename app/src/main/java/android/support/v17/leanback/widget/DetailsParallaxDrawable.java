package android.support.v17.leanback.widget;

import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.v17.leanback.R.attr;
import android.support.v17.leanback.R.color;
import android.support.v17.leanback.R.dimen;
import android.support.v17.leanback.graphics.CompositeDrawable;
import android.support.v17.leanback.graphics.CompositeDrawable.ChildDrawable;
import android.support.v17.leanback.graphics.FitWidthBitmapDrawable;
import android.util.TypedValue;

public class DetailsParallaxDrawable
  extends CompositeDrawable
{
  private Drawable mBottomDrawable;
  
  public DetailsParallaxDrawable(Context paramContext, DetailsParallax paramDetailsParallax)
  {
    int i = -paramContext.getResources().getDimensionPixelSize(R.dimen.lb_details_cover_drawable_parallax_movement);
    FitWidthBitmapDrawable localFitWidthBitmapDrawable = new FitWidthBitmapDrawable();
    ParallaxTarget.PropertyValuesHolderTarget localPropertyValuesHolderTarget = new ParallaxTarget.PropertyValuesHolderTarget(localFitWidthBitmapDrawable, PropertyValuesHolder.ofInt("verticalOffset", new int[] { 0, i }));
    init(paramContext, paramDetailsParallax, localFitWidthBitmapDrawable, new ColorDrawable(), localPropertyValuesHolderTarget);
  }
  
  public DetailsParallaxDrawable(Context paramContext, DetailsParallax paramDetailsParallax, Drawable paramDrawable1, Drawable paramDrawable2, ParallaxTarget paramParallaxTarget)
  {
    init(paramContext, paramDetailsParallax, paramDrawable1, paramDrawable2, paramParallaxTarget);
  }
  
  public DetailsParallaxDrawable(Context paramContext, DetailsParallax paramDetailsParallax, Drawable paramDrawable, ParallaxTarget paramParallaxTarget)
  {
    init(paramContext, paramDetailsParallax, paramDrawable, new ColorDrawable(), paramParallaxTarget);
  }
  
  private static int getDefaultBackgroundColor(Context paramContext)
  {
    TypedValue localTypedValue = new TypedValue();
    if (paramContext.getTheme().resolveAttribute(R.attr.defaultBrandColorDark, localTypedValue, true)) {
      return paramContext.getResources().getColor(localTypedValue.resourceId);
    }
    return paramContext.getResources().getColor(R.color.lb_default_brand_color_dark);
  }
  
  void connect(Context paramContext, DetailsParallax paramDetailsParallax, ParallaxTarget paramParallaxTarget)
  {
    Parallax.IntProperty localIntProperty1 = paramDetailsParallax.getOverviewRowTop();
    Parallax.IntProperty localIntProperty2 = paramDetailsParallax.getOverviewRowBottom();
    int i = paramContext.getResources().getDimensionPixelSize(R.dimen.lb_details_v2_align_pos_for_actions);
    int j = paramContext.getResources().getDimensionPixelSize(R.dimen.lb_details_v2_align_pos_for_description);
    paramDetailsParallax.addEffect(new Parallax.PropertyMarkerValue[] { localIntProperty1.atAbsolute(i), localIntProperty1.atAbsolute(j) }).target(paramParallaxTarget);
    paramDetailsParallax.addEffect(new Parallax.PropertyMarkerValue[] { localIntProperty2.atMax(), localIntProperty2.atMin() }).target(getChildAt(1), CompositeDrawable.ChildDrawable.TOP_ABSOLUTE);
    paramDetailsParallax.addEffect(new Parallax.PropertyMarkerValue[] { localIntProperty1.atMax(), localIntProperty1.atMin() }).target(getChildAt(0), CompositeDrawable.ChildDrawable.BOTTOM_ABSOLUTE);
  }
  
  public Drawable getBottomDrawable()
  {
    return this.mBottomDrawable;
  }
  
  public Drawable getCoverDrawable()
  {
    return getChildAt(0).getDrawable();
  }
  
  @ColorInt
  public int getSolidColor()
  {
    return ((ColorDrawable)this.mBottomDrawable).getColor();
  }
  
  void init(Context paramContext, DetailsParallax paramDetailsParallax, Drawable paramDrawable1, Drawable paramDrawable2, ParallaxTarget paramParallaxTarget)
  {
    if ((paramDrawable2 instanceof ColorDrawable))
    {
      ColorDrawable localColorDrawable = (ColorDrawable)paramDrawable2;
      if (localColorDrawable.getColor() == 0) {
        localColorDrawable.setColor(getDefaultBackgroundColor(paramContext));
      }
    }
    addChildDrawable(paramDrawable1);
    this.mBottomDrawable = paramDrawable2;
    addChildDrawable(paramDrawable2);
    connect(paramContext, paramDetailsParallax, paramParallaxTarget);
  }
  
  public void setSolidColor(@ColorInt int paramInt)
  {
    ((ColorDrawable)this.mBottomDrawable).setColor(paramInt);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/DetailsParallaxDrawable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */