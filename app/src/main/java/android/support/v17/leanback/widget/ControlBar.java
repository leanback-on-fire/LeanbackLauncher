package android.support.v17.leanback.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import java.util.ArrayList;

class ControlBar
  extends LinearLayout
{
  private int mChildMarginFromCenter;
  boolean mDefaultFocusToMiddle = true;
  int mLastFocusIndex = -1;
  private OnChildFocusedListener mOnChildFocusedListener;
  
  public ControlBar(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  public ControlBar(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
  }
  
  public void addFocusables(ArrayList<View> paramArrayList, int paramInt1, int paramInt2)
  {
    if ((paramInt1 == 33) || (paramInt1 == 130))
    {
      if ((this.mLastFocusIndex >= 0) && (this.mLastFocusIndex < getChildCount())) {
        paramArrayList.add(getChildAt(this.mLastFocusIndex));
      }
      while (getChildCount() <= 0) {
        return;
      }
      paramArrayList.add(getChildAt(getDefaultFocusIndex()));
      return;
    }
    super.addFocusables(paramArrayList, paramInt1, paramInt2);
  }
  
  int getDefaultFocusIndex()
  {
    if (this.mDefaultFocusToMiddle) {
      return getChildCount() / 2;
    }
    return 0;
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(paramInt1, paramInt2);
    if (this.mChildMarginFromCenter <= 0) {
      return;
    }
    paramInt2 = 0;
    paramInt1 = 0;
    while (paramInt1 < getChildCount() - 1)
    {
      Object localObject = getChildAt(paramInt1);
      View localView = getChildAt(paramInt1 + 1);
      int i = ((View)localObject).getMeasuredWidth();
      int j = localView.getMeasuredWidth();
      i = this.mChildMarginFromCenter - (i + j) / 2;
      localObject = (LinearLayout.LayoutParams)localView.getLayoutParams();
      j = ((LinearLayout.LayoutParams)localObject).getMarginStart();
      ((LinearLayout.LayoutParams)localObject).setMarginStart(i);
      localView.setLayoutParams((ViewGroup.LayoutParams)localObject);
      paramInt2 += i - j;
      paramInt1 += 1;
    }
    setMeasuredDimension(getMeasuredWidth() + paramInt2, getMeasuredHeight());
  }
  
  protected boolean onRequestFocusInDescendants(int paramInt, Rect paramRect)
  {
    if (getChildCount() > 0)
    {
      if ((this.mLastFocusIndex >= 0) && (this.mLastFocusIndex < getChildCount())) {}
      for (int i = this.mLastFocusIndex; getChildAt(i).requestFocus(paramInt, paramRect); i = getDefaultFocusIndex()) {
        return true;
      }
    }
    return super.onRequestFocusInDescendants(paramInt, paramRect);
  }
  
  public void requestChildFocus(View paramView1, View paramView2)
  {
    super.requestChildFocus(paramView1, paramView2);
    this.mLastFocusIndex = indexOfChild(paramView1);
    if (this.mOnChildFocusedListener != null) {
      this.mOnChildFocusedListener.onChildFocusedListener(paramView1, paramView2);
    }
  }
  
  public void setChildMarginFromCenter(int paramInt)
  {
    this.mChildMarginFromCenter = paramInt;
  }
  
  void setDefaultFocusToMiddle(boolean paramBoolean)
  {
    this.mDefaultFocusToMiddle = paramBoolean;
  }
  
  public void setOnChildFocusedListener(OnChildFocusedListener paramOnChildFocusedListener)
  {
    this.mOnChildFocusedListener = paramOnChildFocusedListener;
  }
  
  public static abstract interface OnChildFocusedListener
  {
    public abstract void onChildFocusedListener(View paramView1, View paramView2);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/ControlBar.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */