package android.support.v7.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.support.annotation.RestrictTo;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;

public class ContentFrameLayout
  extends FrameLayout
{
  private OnAttachListener mAttachListener;
  private final Rect mDecorPadding = new Rect();
  private TypedValue mFixedHeightMajor;
  private TypedValue mFixedHeightMinor;
  private TypedValue mFixedWidthMajor;
  private TypedValue mFixedWidthMinor;
  private TypedValue mMinWidthMajor;
  private TypedValue mMinWidthMinor;
  
  public ContentFrameLayout(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public ContentFrameLayout(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public ContentFrameLayout(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
  }
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  public void dispatchFitSystemWindows(Rect paramRect)
  {
    fitSystemWindows(paramRect);
  }
  
  public TypedValue getFixedHeightMajor()
  {
    if (this.mFixedHeightMajor == null) {
      this.mFixedHeightMajor = new TypedValue();
    }
    return this.mFixedHeightMajor;
  }
  
  public TypedValue getFixedHeightMinor()
  {
    if (this.mFixedHeightMinor == null) {
      this.mFixedHeightMinor = new TypedValue();
    }
    return this.mFixedHeightMinor;
  }
  
  public TypedValue getFixedWidthMajor()
  {
    if (this.mFixedWidthMajor == null) {
      this.mFixedWidthMajor = new TypedValue();
    }
    return this.mFixedWidthMajor;
  }
  
  public TypedValue getFixedWidthMinor()
  {
    if (this.mFixedWidthMinor == null) {
      this.mFixedWidthMinor = new TypedValue();
    }
    return this.mFixedWidthMinor;
  }
  
  public TypedValue getMinWidthMajor()
  {
    if (this.mMinWidthMajor == null) {
      this.mMinWidthMajor = new TypedValue();
    }
    return this.mMinWidthMajor;
  }
  
  public TypedValue getMinWidthMinor()
  {
    if (this.mMinWidthMinor == null) {
      this.mMinWidthMinor = new TypedValue();
    }
    return this.mMinWidthMinor;
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    if (this.mAttachListener != null) {
      this.mAttachListener.onAttachedFromWindow();
    }
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    if (this.mAttachListener != null) {
      this.mAttachListener.onDetachedFromWindow();
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    DisplayMetrics localDisplayMetrics = getContext().getResources().getDisplayMetrics();
    int j;
    int i2;
    int n;
    int m;
    TypedValue localTypedValue;
    label68:
    int i;
    if (localDisplayMetrics.widthPixels < localDisplayMetrics.heightPixels)
    {
      j = 1;
      int i1 = View.MeasureSpec.getMode(paramInt1);
      i2 = View.MeasureSpec.getMode(paramInt2);
      n = 0;
      int k = n;
      m = paramInt1;
      if (i1 == Integer.MIN_VALUE)
      {
        if (j == 0) {
          break label424;
        }
        localTypedValue = this.mFixedWidthMinor;
        k = n;
        m = paramInt1;
        if (localTypedValue != null)
        {
          k = n;
          m = paramInt1;
          if (localTypedValue.type != 0)
          {
            i = 0;
            if (localTypedValue.type != 5) {
              break label433;
            }
            i = (int)localTypedValue.getDimension(localDisplayMetrics);
            label115:
            k = n;
            m = paramInt1;
            if (i > 0)
            {
              m = View.MeasureSpec.makeMeasureSpec(Math.min(i - (this.mDecorPadding.left + this.mDecorPadding.right), View.MeasureSpec.getSize(paramInt1)), 1073741824);
              k = 1;
            }
          }
        }
      }
      i = paramInt2;
      if (i2 == Integer.MIN_VALUE)
      {
        if (j == 0) {
          break label465;
        }
        localTypedValue = this.mFixedHeightMajor;
        label180:
        i = paramInt2;
        if (localTypedValue != null)
        {
          i = paramInt2;
          if (localTypedValue.type != 0)
          {
            paramInt1 = 0;
            if (localTypedValue.type != 5) {
              break label474;
            }
            paramInt1 = (int)localTypedValue.getDimension(localDisplayMetrics);
            label217:
            i = paramInt2;
            if (paramInt1 > 0) {
              i = View.MeasureSpec.makeMeasureSpec(Math.min(paramInt1 - (this.mDecorPadding.top + this.mDecorPadding.bottom), View.MeasureSpec.getSize(paramInt2)), 1073741824);
            }
          }
        }
      }
      super.onMeasure(m, i);
      i2 = getMeasuredWidth();
      m = 0;
      n = View.MeasureSpec.makeMeasureSpec(i2, 1073741824);
      paramInt2 = m;
      paramInt1 = n;
      if (k == 0)
      {
        paramInt2 = m;
        paramInt1 = n;
        if (i1 == Integer.MIN_VALUE)
        {
          if (j == 0) {
            break label506;
          }
          localTypedValue = this.mMinWidthMinor;
          label313:
          paramInt2 = m;
          paramInt1 = n;
          if (localTypedValue != null)
          {
            paramInt2 = m;
            paramInt1 = n;
            if (localTypedValue.type != 0)
            {
              paramInt1 = 0;
              if (localTypedValue.type != 5) {
                break label515;
              }
              paramInt1 = (int)localTypedValue.getDimension(localDisplayMetrics);
            }
          }
        }
      }
    }
    for (;;)
    {
      j = paramInt1;
      if (paramInt1 > 0) {
        j = paramInt1 - (this.mDecorPadding.left + this.mDecorPadding.right);
      }
      paramInt2 = m;
      paramInt1 = n;
      if (i2 < j)
      {
        paramInt1 = View.MeasureSpec.makeMeasureSpec(j, 1073741824);
        paramInt2 = 1;
      }
      if (paramInt2 != 0) {
        super.onMeasure(paramInt1, i);
      }
      return;
      j = 0;
      break;
      label424:
      localTypedValue = this.mFixedWidthMajor;
      break label68;
      label433:
      if (localTypedValue.type != 6) {
        break label115;
      }
      i = (int)localTypedValue.getFraction(localDisplayMetrics.widthPixels, localDisplayMetrics.widthPixels);
      break label115;
      label465:
      localTypedValue = this.mFixedHeightMinor;
      break label180;
      label474:
      if (localTypedValue.type != 6) {
        break label217;
      }
      paramInt1 = (int)localTypedValue.getFraction(localDisplayMetrics.heightPixels, localDisplayMetrics.heightPixels);
      break label217;
      label506:
      localTypedValue = this.mMinWidthMajor;
      break label313;
      label515:
      if (localTypedValue.type == 6) {
        paramInt1 = (int)localTypedValue.getFraction(localDisplayMetrics.widthPixels, localDisplayMetrics.widthPixels);
      }
    }
  }
  
  public void setAttachListener(OnAttachListener paramOnAttachListener)
  {
    this.mAttachListener = paramOnAttachListener;
  }
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  public void setDecorPadding(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    this.mDecorPadding.set(paramInt1, paramInt2, paramInt3, paramInt4);
    if (ViewCompat.isLaidOut(this)) {
      requestLayout();
    }
  }
  
  public static abstract interface OnAttachListener
  {
    public abstract void onAttachedFromWindow();
    
    public abstract void onDetachedFromWindow();
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v7/widget/ContentFrameLayout.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */