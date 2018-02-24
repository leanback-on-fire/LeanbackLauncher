package android.support.v17.leanback.widget;

import android.graphics.Rect;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;

public final class HorizontalHoverCardSwitcher
  extends PresenterSwitcher
{
  int mCardLeft;
  int mCardRight;
  private int[] mTmpOffsets = new int[2];
  private Rect mTmpRect = new Rect();
  
  protected void insertView(View paramView)
  {
    getParentViewGroup().addView(paramView);
  }
  
  protected void onViewSelected(View paramView)
  {
    int i = 1;
    int j = getParentViewGroup().getWidth() - getParentViewGroup().getPaddingRight();
    int k = getParentViewGroup().getPaddingLeft();
    paramView.measure(0, 0);
    ViewGroup.MarginLayoutParams localMarginLayoutParams = (ViewGroup.MarginLayoutParams)paramView.getLayoutParams();
    if (ViewCompat.getLayoutDirection(paramView) == 1)
    {
      if ((i != 0) || (this.mCardLeft + paramView.getMeasuredWidth() <= j)) {
        break label88;
      }
      localMarginLayoutParams.leftMargin = (j - paramView.getMeasuredWidth());
    }
    for (;;)
    {
      paramView.requestLayout();
      return;
      i = 0;
      break;
      label88:
      if ((i != 0) && (this.mCardLeft < k)) {
        localMarginLayoutParams.leftMargin = k;
      } else if (i != 0) {
        localMarginLayoutParams.leftMargin = (this.mCardRight - paramView.getMeasuredWidth());
      } else {
        localMarginLayoutParams.leftMargin = this.mCardLeft;
      }
    }
  }
  
  public void select(HorizontalGridView paramHorizontalGridView, View paramView, Object paramObject)
  {
    ViewGroup localViewGroup = getParentViewGroup();
    paramHorizontalGridView.getViewSelectedOffsets(paramView, this.mTmpOffsets);
    this.mTmpRect.set(0, 0, paramView.getWidth(), paramView.getHeight());
    localViewGroup.offsetDescendantRectToMyCoords(paramView, this.mTmpRect);
    this.mCardLeft = (this.mTmpRect.left - this.mTmpOffsets[0]);
    this.mCardRight = (this.mTmpRect.right - this.mTmpOffsets[0]);
    select(paramObject);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/HorizontalHoverCardSwitcher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */