package android.support.v17.leanback.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.support.annotation.ColorInt;
import android.support.v17.leanback.R.dimen;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;

public class ShadowOverlayContainer
  extends FrameLayout
{
  public static final int SHADOW_DYNAMIC = 3;
  public static final int SHADOW_NONE = 1;
  public static final int SHADOW_STATIC = 2;
  private static final Rect sTempRect = new Rect();
  private float mFocusedZ;
  private boolean mInitialized;
  int mOverlayColor;
  private Paint mOverlayPaint;
  private int mRoundedCornerRadius;
  private boolean mRoundedCorners;
  private Object mShadowImpl;
  private int mShadowType = 1;
  private float mUnfocusedZ;
  private View mWrappedView;
  
  public ShadowOverlayContainer(Context paramContext)
  {
    this(paramContext, null, 0);
  }
  
  ShadowOverlayContainer(Context paramContext, int paramInt1, boolean paramBoolean, float paramFloat1, float paramFloat2, int paramInt2)
  {
    super(paramContext);
    this.mUnfocusedZ = paramFloat1;
    this.mFocusedZ = paramFloat2;
    initialize(paramInt1, paramBoolean, paramInt2);
  }
  
  public ShadowOverlayContainer(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public ShadowOverlayContainer(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    useStaticShadow();
    useDynamicShadow();
  }
  
  public static void prepareParentForShadow(ViewGroup paramViewGroup)
  {
    StaticShadowHelper.getInstance().prepareParent(paramViewGroup);
  }
  
  public static boolean supportsDynamicShadow()
  {
    return ShadowHelper.getInstance().supportsDynamicShadow();
  }
  
  public static boolean supportsShadow()
  {
    return StaticShadowHelper.getInstance().supportsShadow();
  }
  
  public void draw(Canvas paramCanvas)
  {
    super.draw(paramCanvas);
    if ((this.mOverlayPaint != null) && (this.mOverlayColor != 0)) {
      paramCanvas.drawRect(this.mWrappedView.getLeft(), this.mWrappedView.getTop(), this.mWrappedView.getRight(), this.mWrappedView.getBottom(), this.mOverlayPaint);
    }
  }
  
  public int getShadowType()
  {
    return this.mShadowType;
  }
  
  public View getWrappedView()
  {
    return this.mWrappedView;
  }
  
  public boolean hasOverlappingRendering()
  {
    return false;
  }
  
  void initialize(int paramInt1, boolean paramBoolean, int paramInt2)
  {
    if (this.mInitialized) {
      throw new IllegalStateException();
    }
    this.mInitialized = true;
    this.mRoundedCornerRadius = paramInt2;
    boolean bool;
    if (paramInt2 > 0)
    {
      bool = true;
      this.mRoundedCorners = bool;
      this.mShadowType = paramInt1;
      switch (this.mShadowType)
      {
      }
    }
    for (;;)
    {
      if (!paramBoolean) {
        break label161;
      }
      setWillNotDraw(false);
      this.mOverlayColor = 0;
      this.mOverlayPaint = new Paint();
      this.mOverlayPaint.setColor(this.mOverlayColor);
      this.mOverlayPaint.setStyle(Paint.Style.FILL);
      return;
      bool = false;
      break;
      this.mShadowImpl = ShadowHelper.getInstance().addDynamicShadow(this, this.mUnfocusedZ, this.mFocusedZ, this.mRoundedCornerRadius);
      continue;
      this.mShadowImpl = StaticShadowHelper.getInstance().addStaticShadow(this);
    }
    label161:
    setWillNotDraw(true);
    this.mOverlayPaint = null;
  }
  
  @Deprecated
  public void initialize(boolean paramBoolean1, boolean paramBoolean2)
  {
    initialize(paramBoolean1, paramBoolean2, true);
  }
  
  @Deprecated
  public void initialize(boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    int i;
    if (!paramBoolean1)
    {
      i = 1;
      if (!paramBoolean3) {
        break label45;
      }
    }
    label45:
    for (int j = getContext().getResources().getDimensionPixelSize(R.dimen.lb_rounded_rect_corner_radius);; j = 0)
    {
      initialize(i, paramBoolean2, j);
      return;
      i = this.mShadowType;
      break;
    }
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    if ((paramBoolean) && (this.mWrappedView != null))
    {
      sTempRect.left = ((int)this.mWrappedView.getPivotX());
      sTempRect.top = ((int)this.mWrappedView.getPivotY());
      offsetDescendantRectToMyCoords(this.mWrappedView, sTempRect);
      setPivotX(sTempRect.left);
      setPivotY(sTempRect.top);
    }
  }
  
  public void setOverlayColor(@ColorInt int paramInt)
  {
    if ((this.mOverlayPaint != null) && (paramInt != this.mOverlayColor))
    {
      this.mOverlayColor = paramInt;
      this.mOverlayPaint.setColor(paramInt);
      invalidate();
    }
  }
  
  public void setShadowFocusLevel(float paramFloat)
  {
    if (this.mShadowImpl != null) {
      ShadowOverlayHelper.setShadowFocusLevel(this.mShadowImpl, this.mShadowType, paramFloat);
    }
  }
  
  public void useDynamicShadow()
  {
    useDynamicShadow(getResources().getDimension(R.dimen.lb_material_shadow_normal_z), getResources().getDimension(R.dimen.lb_material_shadow_focused_z));
  }
  
  public void useDynamicShadow(float paramFloat1, float paramFloat2)
  {
    if (this.mInitialized) {
      throw new IllegalStateException("Already initialized");
    }
    if (supportsDynamicShadow())
    {
      this.mShadowType = 3;
      this.mUnfocusedZ = paramFloat1;
      this.mFocusedZ = paramFloat2;
    }
  }
  
  public void useStaticShadow()
  {
    if (this.mInitialized) {
      throw new IllegalStateException("Already initialized");
    }
    if (supportsShadow()) {
      this.mShadowType = 2;
    }
  }
  
  public void wrap(View paramView)
  {
    int j = -1;
    if ((!this.mInitialized) || (this.mWrappedView != null)) {
      throw new IllegalStateException();
    }
    ViewGroup.LayoutParams localLayoutParams = paramView.getLayoutParams();
    int i;
    if (localLayoutParams != null)
    {
      FrameLayout.LayoutParams localLayoutParams1 = new FrameLayout.LayoutParams(localLayoutParams.width, localLayoutParams.height);
      if (localLayoutParams.width == -1)
      {
        i = -1;
        localLayoutParams.width = i;
        if (localLayoutParams.height != -1) {
          break label136;
        }
        i = j;
        label82:
        localLayoutParams.height = i;
        setLayoutParams(localLayoutParams);
        addView(paramView, localLayoutParams1);
      }
    }
    for (;;)
    {
      if ((this.mRoundedCorners) && (this.mShadowType == 2)) {
        RoundedRectHelper.getInstance().setClipToRoundedOutline(paramView, true);
      }
      this.mWrappedView = paramView;
      return;
      i = -2;
      break;
      label136:
      i = -2;
      break label82;
      addView(paramView);
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/ShadowOverlayContainer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */