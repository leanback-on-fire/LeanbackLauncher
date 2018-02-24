package android.support.v17.leanback.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.RestrictTo;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;

@RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
public class ScaleFrameLayout
  extends FrameLayout
{
  private static final int DEFAULT_CHILD_GRAVITY = 8388659;
  private float mChildScale = 1.0F;
  private float mLayoutScaleX = 1.0F;
  private float mLayoutScaleY = 1.0F;
  
  public ScaleFrameLayout(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public ScaleFrameLayout(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public ScaleFrameLayout(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
  }
  
  private static int getScaledMeasureSpec(int paramInt, float paramFloat)
  {
    if (paramFloat == 1.0F) {
      return paramInt;
    }
    return View.MeasureSpec.makeMeasureSpec((int)(View.MeasureSpec.getSize(paramInt) / paramFloat + 0.5F), View.MeasureSpec.getMode(paramInt));
  }
  
  public void addView(View paramView, int paramInt, ViewGroup.LayoutParams paramLayoutParams)
  {
    super.addView(paramView, paramInt, paramLayoutParams);
    paramView.setScaleX(this.mChildScale);
    paramView.setScaleY(this.mChildScale);
  }
  
  protected boolean addViewInLayout(View paramView, int paramInt, ViewGroup.LayoutParams paramLayoutParams, boolean paramBoolean)
  {
    paramBoolean = super.addViewInLayout(paramView, paramInt, paramLayoutParams, paramBoolean);
    if (paramBoolean)
    {
      paramView.setScaleX(this.mChildScale);
      paramView.setScaleY(this.mChildScale);
    }
    return paramBoolean;
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int m = getChildCount();
    int n = getLayoutDirection();
    float f1;
    int j;
    int i;
    label91:
    float f2;
    label154:
    int k;
    label157:
    View localView;
    FrameLayout.LayoutParams localLayoutParams;
    int i1;
    int i2;
    if (n == 1)
    {
      f1 = getWidth() - getPivotX();
      if (this.mLayoutScaleX == 1.0F) {
        break label366;
      }
      j = getPaddingLeft() + (int)(f1 - f1 / this.mLayoutScaleX + 0.5F);
      i = (int)((paramInt3 - paramInt1 - f1) / this.mLayoutScaleX + f1 + 0.5F) - getPaddingRight();
      paramInt3 = j;
      f2 = getPivotY();
      if (this.mLayoutScaleY == 1.0F) {
        break label392;
      }
      j = getPaddingTop() + (int)(f2 - f2 / this.mLayoutScaleY + 0.5F);
      paramInt4 = (int)((paramInt4 - paramInt2 - f2) / this.mLayoutScaleY + f2 + 0.5F) - getPaddingBottom();
      k = 0;
      if (k >= m) {
        return;
      }
      localView = getChildAt(k);
      if (localView.getVisibility() != 8)
      {
        localLayoutParams = (FrameLayout.LayoutParams)localView.getLayoutParams();
        i1 = localView.getMeasuredWidth();
        i2 = localView.getMeasuredHeight();
        paramInt2 = localLayoutParams.gravity;
        paramInt1 = paramInt2;
        if (paramInt2 == -1) {
          paramInt1 = 8388659;
        }
        switch (Gravity.getAbsoluteGravity(paramInt1, n) & 0x7)
        {
        default: 
          paramInt2 = paramInt3 + localLayoutParams.leftMargin;
          label265:
          switch (paramInt1 & 0x70)
          {
          default: 
            paramInt1 = j + localLayoutParams.topMargin;
          }
          break;
        }
      }
    }
    for (;;)
    {
      localView.layout(paramInt2, paramInt1, paramInt2 + i1, paramInt1 + i2);
      localView.setPivotX(f1 - paramInt2);
      localView.setPivotY(f2 - paramInt1);
      k += 1;
      break label157;
      f1 = getPivotX();
      break;
      label366:
      i = getPaddingLeft();
      paramInt1 = paramInt3 - paramInt1 - getPaddingRight();
      paramInt3 = i;
      i = paramInt1;
      break label91;
      label392:
      j = getPaddingTop();
      paramInt4 = paramInt4 - paramInt2 - getPaddingBottom();
      break label154;
      paramInt2 = (i - paramInt3 - i1) / 2 + paramInt3 + localLayoutParams.leftMargin - localLayoutParams.rightMargin;
      break label265;
      paramInt2 = i - i1 - localLayoutParams.rightMargin;
      break label265;
      paramInt1 = j + localLayoutParams.topMargin;
      continue;
      paramInt1 = (paramInt4 - j - i2) / 2 + j + localLayoutParams.topMargin - localLayoutParams.bottomMargin;
      continue;
      paramInt1 = paramInt4 - i2 - localLayoutParams.bottomMargin;
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    if ((this.mLayoutScaleX != 1.0F) || (this.mLayoutScaleY != 1.0F))
    {
      super.onMeasure(getScaledMeasureSpec(paramInt1, this.mLayoutScaleX), getScaledMeasureSpec(paramInt2, this.mLayoutScaleY));
      setMeasuredDimension((int)(getMeasuredWidth() * this.mLayoutScaleX + 0.5F), (int)(getMeasuredHeight() * this.mLayoutScaleY + 0.5F));
      return;
    }
    super.onMeasure(paramInt1, paramInt2);
  }
  
  public void setChildScale(float paramFloat)
  {
    if (this.mChildScale != paramFloat)
    {
      this.mChildScale = paramFloat;
      int i = 0;
      while (i < getChildCount())
      {
        getChildAt(i).setScaleX(paramFloat);
        getChildAt(i).setScaleY(paramFloat);
        i += 1;
      }
    }
  }
  
  public void setForeground(Drawable paramDrawable)
  {
    throw new UnsupportedOperationException();
  }
  
  public void setLayoutScaleX(float paramFloat)
  {
    if (paramFloat != this.mLayoutScaleX)
    {
      this.mLayoutScaleX = paramFloat;
      requestLayout();
    }
  }
  
  public void setLayoutScaleY(float paramFloat)
  {
    if (paramFloat != this.mLayoutScaleY)
    {
      this.mLayoutScaleY = paramFloat;
      requestLayout();
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/ScaleFrameLayout.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */