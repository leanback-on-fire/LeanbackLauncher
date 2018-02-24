package android.support.v7.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.appcompat.R.id;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;

@RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
public class AlertDialogLayout
  extends LinearLayoutCompat
{
  public AlertDialogLayout(@Nullable Context paramContext)
  {
    super(paramContext);
  }
  
  public AlertDialogLayout(@Nullable Context paramContext, @Nullable AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  private void forceUniformWidth(int paramInt1, int paramInt2)
  {
    int j = View.MeasureSpec.makeMeasureSpec(getMeasuredWidth(), 1073741824);
    int i = 0;
    while (i < paramInt1)
    {
      View localView = getChildAt(i);
      if (localView.getVisibility() != 8)
      {
        LinearLayoutCompat.LayoutParams localLayoutParams = (LinearLayoutCompat.LayoutParams)localView.getLayoutParams();
        if (localLayoutParams.width == -1)
        {
          int k = localLayoutParams.height;
          localLayoutParams.height = localView.getMeasuredHeight();
          measureChildWithMargins(localView, j, 0, paramInt2, 0);
          localLayoutParams.height = k;
        }
      }
      i += 1;
    }
  }
  
  private static int resolveMinimumHeight(View paramView)
  {
    int i = ViewCompat.getMinimumHeight(paramView);
    if (i > 0) {
      return i;
    }
    if ((paramView instanceof ViewGroup))
    {
      paramView = (ViewGroup)paramView;
      if (paramView.getChildCount() == 1) {
        return resolveMinimumHeight(paramView.getChildAt(0));
      }
    }
    return 0;
  }
  
  private void setChildFrame(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paramView.layout(paramInt1, paramInt2, paramInt1 + paramInt3, paramInt2 + paramInt4);
  }
  
  private boolean tryOnMeasure(int paramInt1, int paramInt2)
  {
    Object localObject1 = null;
    Object localObject3 = null;
    Object localObject2 = null;
    int i5 = getChildCount();
    int i = 0;
    View localView;
    if (i < i5)
    {
      localView = getChildAt(i);
      if (localView.getVisibility() == 8) {}
      for (;;)
      {
        i += 1;
        break;
        j = localView.getId();
        if (j == R.id.topPanel)
        {
          localObject1 = localView;
        }
        else if (j == R.id.buttonPanel)
        {
          localObject3 = localView;
        }
        else
        {
          if ((j != R.id.contentPanel) && (j != R.id.customPanel)) {
            break label114;
          }
          if (localObject2 != null) {
            return false;
          }
          localObject2 = localView;
        }
      }
      label114:
      return false;
    }
    int i7 = View.MeasureSpec.getMode(paramInt2);
    int i3 = View.MeasureSpec.getSize(paramInt2);
    int i6 = View.MeasureSpec.getMode(paramInt1);
    int j = 0;
    i = getPaddingTop() + getPaddingBottom();
    int k = i;
    if (localObject1 != null)
    {
      ((View)localObject1).measure(paramInt1, 0);
      k = i + ((View)localObject1).getMeasuredHeight();
      j = View.combineMeasuredStates(0, ((View)localObject1).getMeasuredState());
    }
    i = 0;
    int i2 = 0;
    int m = j;
    int n = k;
    if (localObject3 != null)
    {
      ((View)localObject3).measure(paramInt1, 0);
      i = resolveMinimumHeight((View)localObject3);
      i2 = ((View)localObject3).getMeasuredHeight() - i;
      n = k + i;
      m = View.combineMeasuredStates(j, ((View)localObject3).getMeasuredState());
    }
    int i1 = 0;
    j = m;
    k = n;
    if (localObject2 != null) {
      if (i7 != 0) {
        break label523;
      }
    }
    label523:
    for (j = 0;; j = View.MeasureSpec.makeMeasureSpec(Math.max(0, i3 - n), i7))
    {
      ((View)localObject2).measure(paramInt1, j);
      i1 = ((View)localObject2).getMeasuredHeight();
      k = n + i1;
      j = View.combineMeasuredStates(m, ((View)localObject2).getMeasuredState());
      i3 -= k;
      m = j;
      int i4 = i3;
      n = k;
      if (localObject3 != null)
      {
        i2 = Math.min(i3, i2);
        n = i;
        m = i3;
        if (i2 > 0)
        {
          m = i3 - i2;
          n = i + i2;
        }
        ((View)localObject3).measure(paramInt1, View.MeasureSpec.makeMeasureSpec(n, 1073741824));
        n = k - i + ((View)localObject3).getMeasuredHeight();
        i = View.combineMeasuredStates(j, ((View)localObject3).getMeasuredState());
        i4 = m;
        m = i;
      }
      j = m;
      i = n;
      if (localObject2 != null)
      {
        j = m;
        i = n;
        if (i4 > 0)
        {
          ((View)localObject2).measure(paramInt1, View.MeasureSpec.makeMeasureSpec(i1 + i4, i7));
          i = n - i1 + ((View)localObject2).getMeasuredHeight();
          j = View.combineMeasuredStates(m, ((View)localObject2).getMeasuredState());
        }
      }
      m = 0;
      k = 0;
      while (k < i5)
      {
        localView = getChildAt(k);
        n = m;
        if (localView.getVisibility() != 8) {
          n = Math.max(m, localView.getMeasuredWidth());
        }
        k += 1;
        m = n;
      }
    }
    setMeasuredDimension(View.resolveSizeAndState(m + (getPaddingLeft() + getPaddingRight()), paramInt1, j), View.resolveSizeAndState(i, paramInt2, 0));
    if (i6 != 1073741824) {
      forceUniformWidth(i5, paramInt2);
    }
    return true;
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int j = getPaddingLeft();
    int k = paramInt3 - paramInt1;
    int m = getPaddingRight();
    int n = getPaddingRight();
    paramInt1 = getMeasuredHeight();
    int i1 = getChildCount();
    int i2 = getGravity();
    Object localObject;
    label91:
    label94:
    int i3;
    int i4;
    LinearLayoutCompat.LayoutParams localLayoutParams;
    int i;
    switch (i2 & 0x70)
    {
    default: 
      paramInt1 = getPaddingTop();
      localObject = getDividerDrawable();
      if (localObject == null)
      {
        paramInt3 = 0;
        paramInt4 = 0;
        if (paramInt4 >= i1) {
          return;
        }
        localObject = getChildAt(paramInt4);
        paramInt2 = paramInt1;
        if (localObject != null)
        {
          paramInt2 = paramInt1;
          if (((View)localObject).getVisibility() != 8)
          {
            i3 = ((View)localObject).getMeasuredWidth();
            i4 = ((View)localObject).getMeasuredHeight();
            localLayoutParams = (LinearLayoutCompat.LayoutParams)((View)localObject).getLayoutParams();
            i = localLayoutParams.gravity;
            paramInt2 = i;
            if (i < 0) {
              paramInt2 = i2 & 0x800007;
            }
            switch (GravityCompat.getAbsoluteGravity(paramInt2, ViewCompat.getLayoutDirection(this)) & 0x7)
            {
            default: 
              paramInt2 = j + localLayoutParams.leftMargin;
            }
          }
        }
      }
      break;
    }
    for (;;)
    {
      i = paramInt1;
      if (hasDividerBeforeChildAt(paramInt4)) {
        i = paramInt1 + paramInt3;
      }
      paramInt1 = i + localLayoutParams.topMargin;
      setChildFrame((View)localObject, paramInt2, paramInt1, i3, i4);
      paramInt2 = paramInt1 + (localLayoutParams.bottomMargin + i4);
      paramInt4 += 1;
      paramInt1 = paramInt2;
      break label94;
      paramInt1 = getPaddingTop() + paramInt4 - paramInt2 - paramInt1;
      break;
      paramInt1 = getPaddingTop() + (paramInt4 - paramInt2 - paramInt1) / 2;
      break;
      paramInt3 = ((Drawable)localObject).getIntrinsicHeight();
      break label91;
      paramInt2 = (k - j - n - i3) / 2 + j + localLayoutParams.leftMargin - localLayoutParams.rightMargin;
      continue;
      paramInt2 = k - m - i3 - localLayoutParams.rightMargin;
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    if (!tryOnMeasure(paramInt1, paramInt2)) {
      super.onMeasure(paramInt1, paramInt2);
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v7/widget/AlertDialogLayout.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */