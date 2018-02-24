package android.support.v17.leanback.widget;

import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;

class ItemAlignmentFacetHelper
{
  private static Rect sRect = new Rect();
  
  static int getAlignmentPosition(View paramView, ItemAlignmentFacet.ItemAlignmentDef paramItemAlignmentDef, int paramInt)
  {
    GridLayoutManager.LayoutParams localLayoutParams = (GridLayoutManager.LayoutParams)paramView.getLayoutParams();
    Object localObject = paramView;
    if (paramItemAlignmentDef.mViewId != 0)
    {
      View localView = paramView.findViewById(paramItemAlignmentDef.mViewId);
      localObject = localView;
      if (localView == null) {
        localObject = paramView;
      }
    }
    int i = paramItemAlignmentDef.mOffset;
    if (paramInt == 0)
    {
      if (paramView.getLayoutDirection() == 1)
      {
        if (localObject == paramView)
        {
          paramInt = localLayoutParams.getOpticalWidth((View)localObject);
          i = paramInt - i;
          paramInt = i;
          if (paramItemAlignmentDef.mOffsetWithPadding)
          {
            if (paramItemAlignmentDef.mOffsetPercent != 0.0F) {
              break label193;
            }
            paramInt = i - ((View)localObject).getPaddingRight();
          }
          label102:
          i = paramInt;
          if (paramItemAlignmentDef.mOffsetPercent != -1.0F) {
            if (localObject != paramView) {
              break label216;
            }
          }
        }
        label193:
        label216:
        for (i = localLayoutParams.getOpticalWidth((View)localObject);; i = ((View)localObject).getWidth())
        {
          i = paramInt - (int)(i * paramItemAlignmentDef.mOffsetPercent / 100.0F);
          paramInt = i;
          if (paramView != localObject)
          {
            sRect.right = i;
            ((ViewGroup)paramView).offsetDescendantRectToMyCoords((View)localObject, sRect);
            paramInt = sRect.right + localLayoutParams.getOpticalRightInset();
          }
          return paramInt;
          paramInt = ((View)localObject).getWidth();
          break;
          paramInt = i;
          if (paramItemAlignmentDef.mOffsetPercent != 100.0F) {
            break label102;
          }
          paramInt = i + ((View)localObject).getPaddingLeft();
          break label102;
        }
      }
      paramInt = i;
      if (paramItemAlignmentDef.mOffsetWithPadding)
      {
        if (paramItemAlignmentDef.mOffsetPercent == 0.0F) {
          paramInt = i + ((View)localObject).getPaddingLeft();
        }
      }
      else
      {
        label251:
        i = paramInt;
        if (paramItemAlignmentDef.mOffsetPercent != -1.0F) {
          if (localObject != paramView) {
            break label354;
          }
        }
      }
      label354:
      for (i = localLayoutParams.getOpticalWidth((View)localObject);; i = ((View)localObject).getWidth())
      {
        i = paramInt + (int)(i * paramItemAlignmentDef.mOffsetPercent / 100.0F);
        paramInt = i;
        if (paramView == localObject) {
          break;
        }
        sRect.left = i;
        ((ViewGroup)paramView).offsetDescendantRectToMyCoords((View)localObject, sRect);
        return sRect.left - localLayoutParams.getOpticalLeftInset();
        paramInt = i;
        if (paramItemAlignmentDef.mOffsetPercent != 100.0F) {
          break label251;
        }
        paramInt = i - ((View)localObject).getPaddingRight();
        break label251;
      }
    }
    paramInt = i;
    if (paramItemAlignmentDef.mOffsetWithPadding)
    {
      if (paramItemAlignmentDef.mOffsetPercent == 0.0F) {
        paramInt = i + ((View)localObject).getPaddingTop();
      }
    }
    else
    {
      label389:
      i = paramInt;
      if (paramItemAlignmentDef.mOffsetPercent != -1.0F) {
        if (localObject != paramView) {
          break label513;
        }
      }
    }
    label513:
    for (i = localLayoutParams.getOpticalHeight((View)localObject);; i = ((View)localObject).getHeight())
    {
      i = paramInt + (int)(i * paramItemAlignmentDef.mOffsetPercent / 100.0F);
      int j = i;
      if (paramView != localObject)
      {
        sRect.top = i;
        ((ViewGroup)paramView).offsetDescendantRectToMyCoords((View)localObject, sRect);
        j = sRect.top - localLayoutParams.getOpticalTopInset();
      }
      paramInt = j;
      if (!paramItemAlignmentDef.isAlignedToTextViewBaseLine()) {
        break;
      }
      return j + ((View)localObject).getBaseline();
      paramInt = i;
      if (paramItemAlignmentDef.mOffsetPercent != 100.0F) {
        break label389;
      }
      paramInt = i - ((View)localObject).getPaddingBottom();
      break label389;
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/ItemAlignmentFacetHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */