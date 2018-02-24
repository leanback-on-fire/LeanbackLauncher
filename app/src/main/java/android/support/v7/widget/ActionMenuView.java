package android.support.v7.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.annotation.StyleRes;
import android.support.v7.view.menu.ActionMenuItemView;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuBuilder.Callback;
import android.support.v7.view.menu.MenuBuilder.ItemInvoker;
import android.support.v7.view.menu.MenuItemImpl;
import android.support.v7.view.menu.MenuPresenter.Callback;
import android.support.v7.view.menu.MenuView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewGroup.LayoutParams;
import android.view.accessibility.AccessibilityEvent;

public class ActionMenuView
  extends LinearLayoutCompat
  implements MenuBuilder.ItemInvoker, MenuView
{
  static final int GENERATED_ITEM_PADDING = 4;
  static final int MIN_CELL_SIZE = 56;
  private static final String TAG = "ActionMenuView";
  private MenuPresenter.Callback mActionMenuPresenterCallback;
  private boolean mFormatItems;
  private int mFormatItemsWidth;
  private int mGeneratedItemPadding;
  private MenuBuilder mMenu;
  MenuBuilder.Callback mMenuBuilderCallback;
  private int mMinCellSize;
  OnMenuItemClickListener mOnMenuItemClickListener;
  private Context mPopupContext;
  private int mPopupTheme;
  private ActionMenuPresenter mPresenter;
  private boolean mReserveOverflow;
  
  public ActionMenuView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public ActionMenuView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    setBaselineAligned(false);
    float f = paramContext.getResources().getDisplayMetrics().density;
    this.mMinCellSize = ((int)(56.0F * f));
    this.mGeneratedItemPadding = ((int)(4.0F * f));
    this.mPopupContext = paramContext;
    this.mPopupTheme = 0;
  }
  
  static int measureChildForCells(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    LayoutParams localLayoutParams = (LayoutParams)paramView.getLayoutParams();
    int j = View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramInt3) - paramInt4, View.MeasureSpec.getMode(paramInt3));
    ActionMenuItemView localActionMenuItemView;
    if ((paramView instanceof ActionMenuItemView))
    {
      localActionMenuItemView = (ActionMenuItemView)paramView;
      if ((localActionMenuItemView == null) || (!localActionMenuItemView.hasText())) {
        break label182;
      }
      paramInt4 = 1;
      label54:
      int i = 0;
      paramInt3 = i;
      if (paramInt2 > 0) {
        if (paramInt4 != 0)
        {
          paramInt3 = i;
          if (paramInt2 < 2) {}
        }
        else
        {
          paramView.measure(View.MeasureSpec.makeMeasureSpec(paramInt1 * paramInt2, Integer.MIN_VALUE), j);
          i = paramView.getMeasuredWidth();
          paramInt3 = i / paramInt1;
          paramInt2 = paramInt3;
          if (i % paramInt1 != 0) {
            paramInt2 = paramInt3 + 1;
          }
          paramInt3 = paramInt2;
          if (paramInt4 != 0)
          {
            paramInt3 = paramInt2;
            if (paramInt2 < 2) {
              paramInt3 = 2;
            }
          }
        }
      }
      if ((localLayoutParams.isOverflowButton) || (paramInt4 == 0)) {
        break label188;
      }
    }
    label182:
    label188:
    for (boolean bool = true;; bool = false)
    {
      localLayoutParams.expandable = bool;
      localLayoutParams.cellsUsed = paramInt3;
      paramView.measure(View.MeasureSpec.makeMeasureSpec(paramInt3 * paramInt1, 1073741824), j);
      return paramInt3;
      localActionMenuItemView = null;
      break;
      paramInt4 = 0;
      break label54;
    }
  }
  
  private void onMeasureExactFormat(int paramInt1, int paramInt2)
  {
    int i8 = View.MeasureSpec.getMode(paramInt2);
    paramInt1 = View.MeasureSpec.getSize(paramInt1);
    int i7 = View.MeasureSpec.getSize(paramInt2);
    int i = getPaddingLeft();
    int j = getPaddingRight();
    int i13 = getPaddingTop() + getPaddingBottom();
    int i9 = getChildMeasureSpec(paramInt2, i13, -2);
    int i10 = paramInt1 - (i + j);
    paramInt1 = i10 / this.mMinCellSize;
    paramInt2 = this.mMinCellSize;
    if (paramInt1 == 0)
    {
      setMeasuredDimension(i10, 0);
      return;
    }
    int i11 = this.mMinCellSize + i10 % paramInt2 / paramInt1;
    i = 0;
    int m = 0;
    int k = 0;
    int n = 0;
    j = 0;
    long l1 = 0L;
    int i12 = getChildCount();
    int i1 = 0;
    Object localObject;
    long l2;
    int i2;
    int i3;
    LayoutParams localLayoutParams;
    label272:
    int i4;
    int i5;
    int i6;
    while (i1 < i12)
    {
      localObject = getChildAt(i1);
      if (((View)localObject).getVisibility() == 8)
      {
        l2 = l1;
        i2 = j;
        i1 += 1;
        j = i2;
        l1 = l2;
      }
      else
      {
        boolean bool = localObject instanceof ActionMenuItemView;
        i3 = n + 1;
        if (bool) {
          ((View)localObject).setPadding(this.mGeneratedItemPadding, 0, this.mGeneratedItemPadding, 0);
        }
        localLayoutParams = (LayoutParams)((View)localObject).getLayoutParams();
        localLayoutParams.expanded = false;
        localLayoutParams.extraPixels = 0;
        localLayoutParams.cellsUsed = 0;
        localLayoutParams.expandable = false;
        localLayoutParams.leftMargin = 0;
        localLayoutParams.rightMargin = 0;
        if ((bool) && (((ActionMenuItemView)localObject).hasText()))
        {
          bool = true;
          localLayoutParams.preventEdgeOffset = bool;
          if (!localLayoutParams.isOverflowButton) {
            break label430;
          }
        }
        label430:
        for (paramInt2 = 1;; paramInt2 = paramInt1)
        {
          int i14 = measureChildForCells((View)localObject, i11, paramInt2, i9, i13);
          i4 = Math.max(m, i14);
          paramInt2 = k;
          if (localLayoutParams.expandable) {
            paramInt2 = k + 1;
          }
          if (localLayoutParams.isOverflowButton) {
            j = 1;
          }
          i5 = paramInt1 - i14;
          i6 = Math.max(i, ((View)localObject).getMeasuredHeight());
          paramInt1 = i5;
          k = paramInt2;
          i2 = j;
          m = i4;
          i = i6;
          l2 = l1;
          n = i3;
          if (i14 != 1) {
            break;
          }
          l2 = l1 | 1 << i1;
          paramInt1 = i5;
          k = paramInt2;
          i2 = j;
          m = i4;
          i = i6;
          n = i3;
          break;
          bool = false;
          break label272;
        }
      }
    }
    long l3;
    if ((j != 0) && (n == 2))
    {
      i1 = 1;
      paramInt2 = 0;
      i2 = paramInt1;
      l2 = l1;
      if (k <= 0) {
        break label641;
      }
      l2 = l1;
      if (i2 <= 0) {
        break label641;
      }
      i3 = Integer.MAX_VALUE;
      l3 = 0L;
      i6 = 0;
      i4 = 0;
      label485:
      if (i4 >= i12) {
        break label623;
      }
      localObject = (LayoutParams)getChildAt(i4).getLayoutParams();
      if (((LayoutParams)localObject).expandable) {
        break label551;
      }
      l2 = l3;
      paramInt1 = i6;
      i5 = i3;
    }
    for (;;)
    {
      i4 += 1;
      i3 = i5;
      i6 = paramInt1;
      l3 = l2;
      break label485;
      i1 = 0;
      break;
      label551:
      if (((LayoutParams)localObject).cellsUsed < i3)
      {
        i5 = ((LayoutParams)localObject).cellsUsed;
        l2 = 1 << i4;
        paramInt1 = 1;
      }
      else
      {
        i5 = i3;
        paramInt1 = i6;
        l2 = l3;
        if (((LayoutParams)localObject).cellsUsed == i3)
        {
          l2 = l3 | 1 << i4;
          paramInt1 = i6 + 1;
          i5 = i3;
        }
      }
    }
    label623:
    l1 |= l3;
    if (i6 > i2)
    {
      l2 = l1;
      label641:
      if ((j != 0) || (n != 1)) {
        break label1004;
      }
      paramInt1 = 1;
      label654:
      j = paramInt2;
      if (i2 <= 0) {
        break label1160;
      }
      j = paramInt2;
      if (l2 == 0L) {
        break label1160;
      }
      if ((i2 >= n - 1) && (paramInt1 == 0))
      {
        j = paramInt2;
        if (m <= 1) {
          break label1160;
        }
      }
      float f3 = Long.bitCount(l2);
      float f2 = f3;
      if (paramInt1 == 0)
      {
        float f1 = f3;
        if ((1L & l2) != 0L)
        {
          f1 = f3;
          if (!((LayoutParams)getChildAt(0).getLayoutParams()).preventEdgeOffset) {
            f1 = f3 - 0.5F;
          }
        }
        f2 = f1;
        if ((1 << i12 - 1 & l2) != 0L)
        {
          f2 = f1;
          if (!((LayoutParams)getChildAt(i12 - 1).getLayoutParams()).preventEdgeOffset) {
            f2 = f1 - 0.5F;
          }
        }
      }
      if (f2 <= 0.0F) {
        break label1009;
      }
      j = (int)(i2 * i11 / f2);
      label814:
      k = 0;
      label817:
      if (k >= i12) {
        break label1157;
      }
      if ((1 << k & l2) != 0L) {
        break label1015;
      }
      paramInt1 = paramInt2;
    }
    for (;;)
    {
      k += 1;
      paramInt2 = paramInt1;
      break label817;
      paramInt1 = 0;
      if (paramInt1 < i12)
      {
        localObject = getChildAt(paramInt1);
        localLayoutParams = (LayoutParams)((View)localObject).getLayoutParams();
        if ((1 << paramInt1 & l3) == 0L)
        {
          paramInt2 = i2;
          l2 = l1;
          if (localLayoutParams.cellsUsed == i3 + 1)
          {
            l2 = l1 | 1 << paramInt1;
            paramInt2 = i2;
          }
        }
        for (;;)
        {
          paramInt1 += 1;
          i2 = paramInt2;
          l1 = l2;
          break;
          if ((i1 != 0) && (localLayoutParams.preventEdgeOffset) && (i2 == 1)) {
            ((View)localObject).setPadding(this.mGeneratedItemPadding + i11, 0, this.mGeneratedItemPadding, 0);
          }
          localLayoutParams.cellsUsed += 1;
          localLayoutParams.expanded = true;
          paramInt2 = i2 - 1;
          l2 = l1;
        }
      }
      paramInt2 = 1;
      break;
      label1004:
      paramInt1 = 0;
      break label654;
      label1009:
      j = 0;
      break label814;
      label1015:
      localObject = getChildAt(k);
      localLayoutParams = (LayoutParams)((View)localObject).getLayoutParams();
      if ((localObject instanceof ActionMenuItemView))
      {
        localLayoutParams.extraPixels = j;
        localLayoutParams.expanded = true;
        if ((k == 0) && (!localLayoutParams.preventEdgeOffset)) {
          localLayoutParams.leftMargin = (-j / 2);
        }
        paramInt1 = 1;
      }
      else if (localLayoutParams.isOverflowButton)
      {
        localLayoutParams.extraPixels = j;
        localLayoutParams.expanded = true;
        localLayoutParams.rightMargin = (-j / 2);
        paramInt1 = 1;
      }
      else
      {
        if (k != 0) {
          localLayoutParams.leftMargin = (j / 2);
        }
        paramInt1 = paramInt2;
        if (k != i12 - 1)
        {
          localLayoutParams.rightMargin = (j / 2);
          paramInt1 = paramInt2;
        }
      }
    }
    label1157:
    j = paramInt2;
    label1160:
    if (j != 0)
    {
      paramInt1 = 0;
      if (paramInt1 < i12)
      {
        localObject = getChildAt(paramInt1);
        localLayoutParams = (LayoutParams)((View)localObject).getLayoutParams();
        if (!localLayoutParams.expanded) {}
        for (;;)
        {
          paramInt1 += 1;
          break;
          ((View)localObject).measure(View.MeasureSpec.makeMeasureSpec(localLayoutParams.cellsUsed * i11 + localLayoutParams.extraPixels, 1073741824), i9);
        }
      }
    }
    paramInt1 = i7;
    if (i8 != 1073741824) {
      paramInt1 = i;
    }
    setMeasuredDimension(i10, paramInt1);
  }
  
  protected boolean checkLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
  {
    return (paramLayoutParams != null) && ((paramLayoutParams instanceof LayoutParams));
  }
  
  public void dismissPopupMenus()
  {
    if (this.mPresenter != null) {
      this.mPresenter.dismissPopupMenus();
    }
  }
  
  public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent)
  {
    return false;
  }
  
  protected LayoutParams generateDefaultLayoutParams()
  {
    LayoutParams localLayoutParams = new LayoutParams(-2, -2);
    localLayoutParams.gravity = 16;
    return localLayoutParams;
  }
  
  public LayoutParams generateLayoutParams(AttributeSet paramAttributeSet)
  {
    return new LayoutParams(getContext(), paramAttributeSet);
  }
  
  protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
  {
    if (paramLayoutParams != null)
    {
      if ((paramLayoutParams instanceof LayoutParams)) {}
      for (paramLayoutParams = new LayoutParams((LayoutParams)paramLayoutParams);; paramLayoutParams = new LayoutParams(paramLayoutParams))
      {
        if (paramLayoutParams.gravity <= 0) {
          paramLayoutParams.gravity = 16;
        }
        return paramLayoutParams;
      }
    }
    return generateDefaultLayoutParams();
  }
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  public LayoutParams generateOverflowButtonLayoutParams()
  {
    LayoutParams localLayoutParams = generateDefaultLayoutParams();
    localLayoutParams.isOverflowButton = true;
    return localLayoutParams;
  }
  
  public Menu getMenu()
  {
    ActionMenuPresenter localActionMenuPresenter;
    if (this.mMenu == null)
    {
      localObject = getContext();
      this.mMenu = new MenuBuilder((Context)localObject);
      this.mMenu.setCallback(new MenuBuilderCallback());
      this.mPresenter = new ActionMenuPresenter((Context)localObject);
      this.mPresenter.setReserveOverflow(true);
      localActionMenuPresenter = this.mPresenter;
      if (this.mActionMenuPresenterCallback == null) {
        break label109;
      }
    }
    label109:
    for (Object localObject = this.mActionMenuPresenterCallback;; localObject = new ActionMenuPresenterCallback())
    {
      localActionMenuPresenter.setCallback((MenuPresenter.Callback)localObject);
      this.mMenu.addMenuPresenter(this.mPresenter, this.mPopupContext);
      this.mPresenter.setMenuView(this);
      return this.mMenu;
    }
  }
  
  @Nullable
  public Drawable getOverflowIcon()
  {
    getMenu();
    return this.mPresenter.getOverflowIcon();
  }
  
  public int getPopupTheme()
  {
    return this.mPopupTheme;
  }
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  public int getWindowAnimations()
  {
    return 0;
  }
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  protected boolean hasSupportDividerBeforeChildAt(int paramInt)
  {
    boolean bool2;
    if (paramInt == 0) {
      bool2 = false;
    }
    View localView2;
    boolean bool1;
    do
    {
      do
      {
        return bool2;
        View localView1 = getChildAt(paramInt - 1);
        localView2 = getChildAt(paramInt);
        bool2 = false;
        bool1 = bool2;
        if (paramInt < getChildCount())
        {
          bool1 = bool2;
          if ((localView1 instanceof ActionMenuChildView)) {
            bool1 = false | ((ActionMenuChildView)localView1).needsDividerAfter();
          }
        }
        bool2 = bool1;
      } while (paramInt <= 0);
      bool2 = bool1;
    } while (!(localView2 instanceof ActionMenuChildView));
    return bool1 | ((ActionMenuChildView)localView2).needsDividerBefore();
  }
  
  public boolean hideOverflowMenu()
  {
    return (this.mPresenter != null) && (this.mPresenter.hideOverflowMenu());
  }
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  public void initialize(MenuBuilder paramMenuBuilder)
  {
    this.mMenu = paramMenuBuilder;
  }
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  public boolean invokeItem(MenuItemImpl paramMenuItemImpl)
  {
    return this.mMenu.performItemAction(paramMenuItemImpl, 0);
  }
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  public boolean isOverflowMenuShowPending()
  {
    return (this.mPresenter != null) && (this.mPresenter.isOverflowMenuShowPending());
  }
  
  public boolean isOverflowMenuShowing()
  {
    return (this.mPresenter != null) && (this.mPresenter.isOverflowMenuShowing());
  }
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  public boolean isOverflowReserved()
  {
    return this.mReserveOverflow;
  }
  
  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    super.onConfigurationChanged(paramConfiguration);
    if (this.mPresenter != null)
    {
      this.mPresenter.updateMenuView(false);
      if (this.mPresenter.isOverflowMenuShowing())
      {
        this.mPresenter.hideOverflowMenu();
        this.mPresenter.showOverflowMenu();
      }
    }
  }
  
  public void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    dismissPopupMenus();
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (!this.mFormatItems)
    {
      super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
      return;
    }
    int i2 = getChildCount();
    int i1 = (paramInt4 - paramInt2) / 2;
    int i3 = getDividerWidth();
    int j = 0;
    paramInt4 = 0;
    paramInt2 = paramInt3 - paramInt1 - getPaddingRight() - getPaddingLeft();
    int k = 0;
    paramBoolean = ViewUtils.isLayoutRtl(this);
    int i = 0;
    View localView;
    LayoutParams localLayoutParams;
    if (i < i2)
    {
      localView = getChildAt(i);
      if (localView.getVisibility() == 8) {}
      for (;;)
      {
        i += 1;
        break;
        localLayoutParams = (LayoutParams)localView.getLayoutParams();
        if (localLayoutParams.isOverflowButton)
        {
          m = localView.getMeasuredWidth();
          k = m;
          if (hasSupportDividerBeforeChildAt(i)) {
            k = m + i3;
          }
          int i4 = localView.getMeasuredHeight();
          int n;
          if (paramBoolean)
          {
            m = getPaddingLeft() + localLayoutParams.leftMargin;
            n = m + k;
          }
          for (;;)
          {
            int i5 = i1 - i4 / 2;
            localView.layout(m, i5, n, i5 + i4);
            paramInt2 -= k;
            k = 1;
            break;
            n = getWidth() - getPaddingRight() - localLayoutParams.rightMargin;
            m = n - k;
          }
        }
        int m = localView.getMeasuredWidth() + localLayoutParams.leftMargin + localLayoutParams.rightMargin;
        j += m;
        m = paramInt2 - m;
        paramInt2 = j;
        if (hasSupportDividerBeforeChildAt(i)) {
          paramInt2 = j + i3;
        }
        paramInt4 += 1;
        j = paramInt2;
        paramInt2 = m;
      }
    }
    if ((i2 == 1) && (k == 0))
    {
      localView = getChildAt(0);
      paramInt2 = localView.getMeasuredWidth();
      paramInt4 = localView.getMeasuredHeight();
      paramInt1 = (paramInt3 - paramInt1) / 2 - paramInt2 / 2;
      paramInt3 = i1 - paramInt4 / 2;
      localView.layout(paramInt1, paramInt3, paramInt1 + paramInt2, paramInt3 + paramInt4);
      return;
    }
    if (k != 0)
    {
      paramInt1 = 0;
      label383:
      paramInt1 = paramInt4 - paramInt1;
      if (paramInt1 <= 0) {
        break label481;
      }
      paramInt1 = paramInt2 / paramInt1;
      label396:
      paramInt4 = Math.max(0, paramInt1);
      if (!paramBoolean) {
        break label552;
      }
      paramInt2 = getWidth() - getPaddingRight();
      paramInt1 = 0;
      label419:
      if (paramInt1 < i2)
      {
        localView = getChildAt(paramInt1);
        localLayoutParams = (LayoutParams)localView.getLayoutParams();
        paramInt3 = paramInt2;
        if (localView.getVisibility() != 8) {
          if (!localLayoutParams.isOverflowButton) {
            break label486;
          }
        }
      }
    }
    for (paramInt3 = paramInt2;; paramInt3 = paramInt2 - (localLayoutParams.leftMargin + paramInt3 + paramInt4))
    {
      paramInt1 += 1;
      paramInt2 = paramInt3;
      break label419;
      break;
      paramInt1 = 1;
      break label383;
      label481:
      paramInt1 = 0;
      break label396;
      label486:
      paramInt2 -= localLayoutParams.rightMargin;
      paramInt3 = localView.getMeasuredWidth();
      i = localView.getMeasuredHeight();
      j = i1 - i / 2;
      localView.layout(paramInt2 - paramInt3, j, paramInt2, j + i);
    }
    label552:
    paramInt2 = getPaddingLeft();
    paramInt1 = 0;
    label559:
    if (paramInt1 < i2)
    {
      localView = getChildAt(paramInt1);
      localLayoutParams = (LayoutParams)localView.getLayoutParams();
      paramInt3 = paramInt2;
      if (localView.getVisibility() != 8) {
        if (!localLayoutParams.isOverflowButton) {
          break label616;
        }
      }
    }
    for (paramInt3 = paramInt2;; paramInt3 = paramInt2 + (localLayoutParams.rightMargin + paramInt3 + paramInt4))
    {
      paramInt1 += 1;
      paramInt2 = paramInt3;
      break label559;
      break;
      label616:
      paramInt2 += localLayoutParams.leftMargin;
      paramInt3 = localView.getMeasuredWidth();
      i = localView.getMeasuredHeight();
      j = i1 - i / 2;
      localView.layout(paramInt2, j, paramInt2 + paramInt3, j + i);
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    boolean bool2 = this.mFormatItems;
    if (View.MeasureSpec.getMode(paramInt1) == 1073741824) {}
    int j;
    for (boolean bool1 = true;; bool1 = false)
    {
      this.mFormatItems = bool1;
      if (bool2 != this.mFormatItems) {
        this.mFormatItemsWidth = 0;
      }
      i = View.MeasureSpec.getSize(paramInt1);
      if ((this.mFormatItems) && (this.mMenu != null) && (i != this.mFormatItemsWidth))
      {
        this.mFormatItemsWidth = i;
        this.mMenu.onItemsChanged(true);
      }
      j = getChildCount();
      if ((!this.mFormatItems) || (j <= 0)) {
        break;
      }
      onMeasureExactFormat(paramInt1, paramInt2);
      return;
    }
    int i = 0;
    while (i < j)
    {
      LayoutParams localLayoutParams = (LayoutParams)getChildAt(i).getLayoutParams();
      localLayoutParams.rightMargin = 0;
      localLayoutParams.leftMargin = 0;
      i += 1;
    }
    super.onMeasure(paramInt1, paramInt2);
  }
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  public MenuBuilder peekMenu()
  {
    return this.mMenu;
  }
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  public void setExpandedActionViewsExclusive(boolean paramBoolean)
  {
    this.mPresenter.setExpandedActionViewsExclusive(paramBoolean);
  }
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  public void setMenuCallbacks(MenuPresenter.Callback paramCallback, MenuBuilder.Callback paramCallback1)
  {
    this.mActionMenuPresenterCallback = paramCallback;
    this.mMenuBuilderCallback = paramCallback1;
  }
  
  public void setOnMenuItemClickListener(OnMenuItemClickListener paramOnMenuItemClickListener)
  {
    this.mOnMenuItemClickListener = paramOnMenuItemClickListener;
  }
  
  public void setOverflowIcon(@Nullable Drawable paramDrawable)
  {
    getMenu();
    this.mPresenter.setOverflowIcon(paramDrawable);
  }
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  public void setOverflowReserved(boolean paramBoolean)
  {
    this.mReserveOverflow = paramBoolean;
  }
  
  public void setPopupTheme(@StyleRes int paramInt)
  {
    if (this.mPopupTheme != paramInt)
    {
      this.mPopupTheme = paramInt;
      if (paramInt == 0) {
        this.mPopupContext = getContext();
      }
    }
    else
    {
      return;
    }
    this.mPopupContext = new ContextThemeWrapper(getContext(), paramInt);
  }
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  public void setPresenter(ActionMenuPresenter paramActionMenuPresenter)
  {
    this.mPresenter = paramActionMenuPresenter;
    this.mPresenter.setMenuView(this);
  }
  
  public boolean showOverflowMenu()
  {
    return (this.mPresenter != null) && (this.mPresenter.showOverflowMenu());
  }
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  public static abstract interface ActionMenuChildView
  {
    public abstract boolean needsDividerAfter();
    
    public abstract boolean needsDividerBefore();
  }
  
  private static class ActionMenuPresenterCallback
    implements MenuPresenter.Callback
  {
    public void onCloseMenu(MenuBuilder paramMenuBuilder, boolean paramBoolean) {}
    
    public boolean onOpenSubMenu(MenuBuilder paramMenuBuilder)
    {
      return false;
    }
  }
  
  public static class LayoutParams
    extends LinearLayoutCompat.LayoutParams
  {
    @ViewDebug.ExportedProperty
    public int cellsUsed;
    @ViewDebug.ExportedProperty
    public boolean expandable;
    boolean expanded;
    @ViewDebug.ExportedProperty
    public int extraPixels;
    @ViewDebug.ExportedProperty
    public boolean isOverflowButton;
    @ViewDebug.ExportedProperty
    public boolean preventEdgeOffset;
    
    public LayoutParams(int paramInt1, int paramInt2)
    {
      super(paramInt2);
      this.isOverflowButton = false;
    }
    
    LayoutParams(int paramInt1, int paramInt2, boolean paramBoolean)
    {
      super(paramInt2);
      this.isOverflowButton = paramBoolean;
    }
    
    public LayoutParams(Context paramContext, AttributeSet paramAttributeSet)
    {
      super(paramAttributeSet);
    }
    
    public LayoutParams(LayoutParams paramLayoutParams)
    {
      super();
      this.isOverflowButton = paramLayoutParams.isOverflowButton;
    }
    
    public LayoutParams(ViewGroup.LayoutParams paramLayoutParams)
    {
      super();
    }
  }
  
  private class MenuBuilderCallback
    implements MenuBuilder.Callback
  {
    MenuBuilderCallback() {}
    
    public boolean onMenuItemSelected(MenuBuilder paramMenuBuilder, MenuItem paramMenuItem)
    {
      return (ActionMenuView.this.mOnMenuItemClickListener != null) && (ActionMenuView.this.mOnMenuItemClickListener.onMenuItemClick(paramMenuItem));
    }
    
    public void onMenuModeChange(MenuBuilder paramMenuBuilder)
    {
      if (ActionMenuView.this.mMenuBuilderCallback != null) {
        ActionMenuView.this.mMenuBuilderCallback.onMenuModeChange(paramMenuBuilder);
      }
    }
  }
  
  public static abstract interface OnMenuItemClickListener
  {
    public abstract boolean onMenuItemClick(MenuItem paramMenuItem);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v7/widget/ActionMenuView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */