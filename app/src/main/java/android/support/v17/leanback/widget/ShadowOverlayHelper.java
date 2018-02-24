package android.support.v17.leanback.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v17.leanback.R.dimen;
import android.support.v17.leanback.R.id;
import android.view.View;
import android.view.ViewGroup;

public final class ShadowOverlayHelper
{
  public static final int SHADOW_DYNAMIC = 3;
  public static final int SHADOW_NONE = 1;
  public static final int SHADOW_STATIC = 2;
  float mFocusedZ;
  boolean mNeedsOverlay;
  boolean mNeedsRoundedCorner;
  boolean mNeedsShadow;
  boolean mNeedsWrapper;
  int mRoundedCornerRadius;
  int mShadowType = 1;
  float mUnfocusedZ;
  
  static Object getNoneWrapperDynamicShadowImpl(View paramView)
  {
    return paramView.getTag(R.id.lb_shadow_impl);
  }
  
  public static void setNoneWrapperOverlayColor(View paramView, int paramInt)
  {
    Drawable localDrawable = ForegroundHelper.getInstance().getForeground(paramView);
    if ((localDrawable instanceof ColorDrawable))
    {
      ((ColorDrawable)localDrawable).setColor(paramInt);
      return;
    }
    ForegroundHelper.getInstance().setForeground(paramView, new ColorDrawable(paramInt));
  }
  
  public static void setNoneWrapperShadowFocusLevel(View paramView, float paramFloat)
  {
    setShadowFocusLevel(getNoneWrapperDynamicShadowImpl(paramView), 3, paramFloat);
  }
  
  static void setShadowFocusLevel(Object paramObject, int paramInt, float paramFloat)
  {
    float f;
    if (paramObject != null)
    {
      if (paramFloat >= 0.0F) {
        break label37;
      }
      f = 0.0F;
    }
    for (;;)
    {
      switch (paramInt)
      {
      default: 
        return;
        label37:
        f = paramFloat;
        if (paramFloat > 1.0F) {
          f = 1.0F;
        }
        break;
      }
    }
    ShadowHelper.getInstance().setShadowFocusLevel(paramObject, f);
    return;
    StaticShadowHelper.getInstance().setShadowFocusLevel(paramObject, f);
  }
  
  public static boolean supportsDynamicShadow()
  {
    return ShadowHelper.getInstance().supportsDynamicShadow();
  }
  
  public static boolean supportsForeground()
  {
    return ForegroundHelper.supportsForeground();
  }
  
  public static boolean supportsRoundedCorner()
  {
    return RoundedRectHelper.supportsRoundedCorner();
  }
  
  public static boolean supportsShadow()
  {
    return StaticShadowHelper.getInstance().supportsShadow();
  }
  
  public ShadowOverlayContainer createShadowOverlayContainer(Context paramContext)
  {
    if (!needsWrapper()) {
      throw new IllegalArgumentException();
    }
    return new ShadowOverlayContainer(paramContext, this.mShadowType, this.mNeedsOverlay, this.mUnfocusedZ, this.mFocusedZ, this.mRoundedCornerRadius);
  }
  
  public int getShadowType()
  {
    return this.mShadowType;
  }
  
  public boolean needsOverlay()
  {
    return this.mNeedsOverlay;
  }
  
  public boolean needsRoundedCorner()
  {
    return this.mNeedsRoundedCorner;
  }
  
  public boolean needsWrapper()
  {
    return this.mNeedsWrapper;
  }
  
  public void onViewCreated(View paramView)
  {
    if (!needsWrapper())
    {
      if (this.mNeedsShadow) {
        break label34;
      }
      if (this.mNeedsRoundedCorner) {
        RoundedRectHelper.getInstance().setClipToRoundedOutline(paramView, true, this.mRoundedCornerRadius);
      }
    }
    label34:
    while (this.mShadowType != 3) {
      return;
    }
    Object localObject = ShadowHelper.getInstance().addDynamicShadow(paramView, this.mUnfocusedZ, this.mFocusedZ, this.mRoundedCornerRadius);
    paramView.setTag(R.id.lb_shadow_impl, localObject);
  }
  
  public void prepareParentForShadow(ViewGroup paramViewGroup)
  {
    if (this.mShadowType == 2) {
      StaticShadowHelper.getInstance().prepareParent(paramViewGroup);
    }
  }
  
  public void setOverlayColor(View paramView, int paramInt)
  {
    if (needsWrapper())
    {
      ((ShadowOverlayContainer)paramView).setOverlayColor(paramInt);
      return;
    }
    setNoneWrapperOverlayColor(paramView, paramInt);
  }
  
  public void setShadowFocusLevel(View paramView, float paramFloat)
  {
    if (needsWrapper())
    {
      ((ShadowOverlayContainer)paramView).setShadowFocusLevel(paramFloat);
      return;
    }
    setShadowFocusLevel(getNoneWrapperDynamicShadowImpl(paramView), 3, paramFloat);
  }
  
  void setupDynamicShadowZ(Options paramOptions, Context paramContext)
  {
    if (paramOptions.getDynamicShadowUnfocusedZ() < 0.0F)
    {
      paramOptions = paramContext.getResources();
      this.mFocusedZ = paramOptions.getDimension(R.dimen.lb_material_shadow_focused_z);
      this.mUnfocusedZ = paramOptions.getDimension(R.dimen.lb_material_shadow_normal_z);
      return;
    }
    this.mFocusedZ = paramOptions.getDynamicShadowFocusedZ();
    this.mUnfocusedZ = paramOptions.getDynamicShadowUnfocusedZ();
  }
  
  void setupRoundedCornerRadius(Options paramOptions, Context paramContext)
  {
    if (paramOptions.getRoundedCornerRadius() == 0)
    {
      this.mRoundedCornerRadius = paramContext.getResources().getDimensionPixelSize(R.dimen.lb_rounded_rect_corner_radius);
      return;
    }
    this.mRoundedCornerRadius = paramOptions.getRoundedCornerRadius();
  }
  
  public static final class Builder
  {
    private boolean keepForegroundDrawable;
    private boolean needsOverlay;
    private boolean needsRoundedCorner;
    private boolean needsShadow;
    private ShadowOverlayHelper.Options options = ShadowOverlayHelper.Options.DEFAULT;
    private boolean preferZOrder = true;
    
    public ShadowOverlayHelper build(Context paramContext)
    {
      boolean bool3 = true;
      boolean bool2 = true;
      ShadowOverlayHelper localShadowOverlayHelper = new ShadowOverlayHelper();
      localShadowOverlayHelper.mNeedsOverlay = this.needsOverlay;
      if ((this.needsRoundedCorner) && (ShadowOverlayHelper.supportsRoundedCorner()))
      {
        bool1 = true;
        localShadowOverlayHelper.mNeedsRoundedCorner = bool1;
        if ((!this.needsShadow) || (!ShadowOverlayHelper.supportsShadow())) {
          break label124;
        }
      }
      label124:
      for (boolean bool1 = true;; bool1 = false)
      {
        localShadowOverlayHelper.mNeedsShadow = bool1;
        if (localShadowOverlayHelper.mNeedsRoundedCorner) {
          localShadowOverlayHelper.setupRoundedCornerRadius(this.options, paramContext);
        }
        if (!localShadowOverlayHelper.mNeedsShadow) {
          break label182;
        }
        if ((this.preferZOrder) && (ShadowOverlayHelper.supportsDynamicShadow())) {
          break label129;
        }
        localShadowOverlayHelper.mShadowType = 2;
        localShadowOverlayHelper.mNeedsWrapper = true;
        return localShadowOverlayHelper;
        bool1 = false;
        break;
      }
      label129:
      localShadowOverlayHelper.mShadowType = 3;
      localShadowOverlayHelper.setupDynamicShadowZ(this.options, paramContext);
      if (((!ShadowOverlayHelper.supportsForeground()) || (this.keepForegroundDrawable)) && (localShadowOverlayHelper.mNeedsOverlay)) {}
      for (bool1 = bool2;; bool1 = false)
      {
        localShadowOverlayHelper.mNeedsWrapper = bool1;
        return localShadowOverlayHelper;
      }
      label182:
      localShadowOverlayHelper.mShadowType = 1;
      if (((!ShadowOverlayHelper.supportsForeground()) || (this.keepForegroundDrawable)) && (localShadowOverlayHelper.mNeedsOverlay)) {}
      for (bool1 = bool3;; bool1 = false)
      {
        localShadowOverlayHelper.mNeedsWrapper = bool1;
        return localShadowOverlayHelper;
      }
    }
    
    public Builder keepForegroundDrawable(boolean paramBoolean)
    {
      this.keepForegroundDrawable = paramBoolean;
      return this;
    }
    
    public Builder needsOverlay(boolean paramBoolean)
    {
      this.needsOverlay = paramBoolean;
      return this;
    }
    
    public Builder needsRoundedCorner(boolean paramBoolean)
    {
      this.needsRoundedCorner = paramBoolean;
      return this;
    }
    
    public Builder needsShadow(boolean paramBoolean)
    {
      this.needsShadow = paramBoolean;
      return this;
    }
    
    public Builder options(ShadowOverlayHelper.Options paramOptions)
    {
      this.options = paramOptions;
      return this;
    }
    
    public Builder preferZOrder(boolean paramBoolean)
    {
      this.preferZOrder = paramBoolean;
      return this;
    }
  }
  
  public static final class Options
  {
    public static final Options DEFAULT = new Options();
    private float dynamicShadowFocusedZ = -1.0F;
    private float dynamicShadowUnfocusedZ = -1.0F;
    private int roundedCornerRadius = 0;
    
    public Options dynamicShadowZ(float paramFloat1, float paramFloat2)
    {
      this.dynamicShadowUnfocusedZ = paramFloat1;
      this.dynamicShadowFocusedZ = paramFloat2;
      return this;
    }
    
    public final float getDynamicShadowFocusedZ()
    {
      return this.dynamicShadowFocusedZ;
    }
    
    public final float getDynamicShadowUnfocusedZ()
    {
      return this.dynamicShadowUnfocusedZ;
    }
    
    public final int getRoundedCornerRadius()
    {
      return this.roundedCornerRadius;
    }
    
    public Options roundedCornerRadius(int paramInt)
    {
      this.roundedCornerRadius = paramInt;
      return this;
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/ShadowOverlayHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */