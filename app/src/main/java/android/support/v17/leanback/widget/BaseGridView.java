package android.support.v17.leanback.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.support.annotation.RestrictTo;
import android.support.v17.leanback.R.styleable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ItemAnimator;
import android.support.v7.widget.RecyclerView.RecyclerListener;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

public abstract class BaseGridView
  extends RecyclerView
{
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  public static final int FOCUS_SCROLL_ALIGNED = 0;
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  public static final int FOCUS_SCROLL_ITEM = 1;
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  public static final int FOCUS_SCROLL_PAGE = 2;
  public static final float ITEM_ALIGN_OFFSET_PERCENT_DISABLED = -1.0F;
  public static final int SAVE_ALL_CHILD = 3;
  public static final int SAVE_LIMITED_CHILD = 2;
  public static final int SAVE_NO_CHILD = 0;
  public static final int SAVE_ON_SCREEN_CHILD = 1;
  public static final int WINDOW_ALIGN_BOTH_EDGE = 3;
  public static final int WINDOW_ALIGN_HIGH_EDGE = 2;
  public static final int WINDOW_ALIGN_LOW_EDGE = 1;
  public static final int WINDOW_ALIGN_NO_EDGE = 0;
  public static final float WINDOW_ALIGN_OFFSET_PERCENT_DISABLED = -1.0F;
  private boolean mAnimateChildLayout = true;
  RecyclerView.RecyclerListener mChainedRecyclerListener;
  private boolean mHasOverlappingRendering = true;
  int mInitialPrefetchItemCount = 4;
  final GridLayoutManager mLayoutManager = new GridLayoutManager(this);
  private OnKeyInterceptListener mOnKeyInterceptListener;
  private OnMotionInterceptListener mOnMotionInterceptListener;
  private OnTouchInterceptListener mOnTouchInterceptListener;
  private OnUnhandledKeyListener mOnUnhandledKeyListener;
  private RecyclerView.ItemAnimator mSavedItemAnimator;
  
  BaseGridView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    setLayoutManager(this.mLayoutManager);
    setPreserveFocusAfterLayout(false);
    setDescendantFocusability(262144);
    setHasFixedSize(true);
    setChildrenDrawingOrderEnabled(true);
    setWillNotDraw(true);
    setOverScrollMode(2);
    ((SimpleItemAnimator)getItemAnimator()).setSupportsChangeAnimations(false);
    super.setRecyclerListener(new RecyclerView.RecyclerListener()
    {
      public void onViewRecycled(RecyclerView.ViewHolder paramAnonymousViewHolder)
      {
        BaseGridView.this.mLayoutManager.onChildRecycled(paramAnonymousViewHolder);
        if (BaseGridView.this.mChainedRecyclerListener != null) {
          BaseGridView.this.mChainedRecyclerListener.onViewRecycled(paramAnonymousViewHolder);
        }
      }
    });
  }
  
  public void addOnChildViewHolderSelectedListener(OnChildViewHolderSelectedListener paramOnChildViewHolderSelectedListener)
  {
    this.mLayoutManager.addOnChildViewHolderSelectedListener(paramOnChildViewHolderSelectedListener);
  }
  
  public void animateIn()
  {
    this.mLayoutManager.slideIn();
  }
  
  public void animateOut()
  {
    this.mLayoutManager.slideOut();
  }
  
  protected boolean dispatchGenericFocusedEvent(MotionEvent paramMotionEvent)
  {
    if ((this.mOnMotionInterceptListener != null) && (this.mOnMotionInterceptListener.onInterceptMotionEvent(paramMotionEvent))) {
      return true;
    }
    return super.dispatchGenericFocusedEvent(paramMotionEvent);
  }
  
  public boolean dispatchKeyEvent(KeyEvent paramKeyEvent)
  {
    if ((this.mOnKeyInterceptListener != null) && (this.mOnKeyInterceptListener.onInterceptKeyEvent(paramKeyEvent))) {}
    while ((super.dispatchKeyEvent(paramKeyEvent)) || ((this.mOnUnhandledKeyListener != null) && (this.mOnUnhandledKeyListener.onUnhandledKey(paramKeyEvent)))) {
      return true;
    }
    return false;
  }
  
  public boolean dispatchTouchEvent(MotionEvent paramMotionEvent)
  {
    if ((this.mOnTouchInterceptListener != null) && (this.mOnTouchInterceptListener.onInterceptTouchEvent(paramMotionEvent))) {
      return true;
    }
    return super.dispatchTouchEvent(paramMotionEvent);
  }
  
  public View focusSearch(int paramInt)
  {
    if (isFocused())
    {
      View localView = this.mLayoutManager.findViewByPosition(this.mLayoutManager.getSelection());
      if (localView != null) {
        return focusSearch(localView, paramInt);
      }
    }
    return super.focusSearch(paramInt);
  }
  
  public int getChildDrawingOrder(int paramInt1, int paramInt2)
  {
    return this.mLayoutManager.getChildDrawingOrder(this, paramInt1, paramInt2);
  }
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  public int getExtraLayoutSpace()
  {
    return this.mLayoutManager.getExtraLayoutSpace();
  }
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  public int getFocusScrollStrategy()
  {
    return this.mLayoutManager.getFocusScrollStrategy();
  }
  
  @Deprecated
  public int getHorizontalMargin()
  {
    return this.mLayoutManager.getHorizontalSpacing();
  }
  
  public int getHorizontalSpacing()
  {
    return this.mLayoutManager.getHorizontalSpacing();
  }
  
  public int getInitialPrefetchItemCount()
  {
    return this.mInitialPrefetchItemCount;
  }
  
  public int getItemAlignmentOffset()
  {
    return this.mLayoutManager.getItemAlignmentOffset();
  }
  
  public float getItemAlignmentOffsetPercent()
  {
    return this.mLayoutManager.getItemAlignmentOffsetPercent();
  }
  
  public int getItemAlignmentViewId()
  {
    return this.mLayoutManager.getItemAlignmentViewId();
  }
  
  public OnUnhandledKeyListener getOnUnhandledKeyListener()
  {
    return this.mOnUnhandledKeyListener;
  }
  
  public final int getSaveChildrenLimitNumber()
  {
    return this.mLayoutManager.mChildrenStates.getLimitNumber();
  }
  
  public final int getSaveChildrenPolicy()
  {
    return this.mLayoutManager.mChildrenStates.getSavePolicy();
  }
  
  public int getSelectedPosition()
  {
    return this.mLayoutManager.getSelection();
  }
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  public int getSelectedSubPosition()
  {
    return this.mLayoutManager.getSubSelection();
  }
  
  @Deprecated
  public int getVerticalMargin()
  {
    return this.mLayoutManager.getVerticalSpacing();
  }
  
  public int getVerticalSpacing()
  {
    return this.mLayoutManager.getVerticalSpacing();
  }
  
  public void getViewSelectedOffsets(View paramView, int[] paramArrayOfInt)
  {
    this.mLayoutManager.getViewSelectedOffsets(paramView, paramArrayOfInt);
  }
  
  public int getWindowAlignment()
  {
    return this.mLayoutManager.getWindowAlignment();
  }
  
  public int getWindowAlignmentOffset()
  {
    return this.mLayoutManager.getWindowAlignmentOffset();
  }
  
  public float getWindowAlignmentOffsetPercent()
  {
    return this.mLayoutManager.getWindowAlignmentOffsetPercent();
  }
  
  public boolean hasOverlappingRendering()
  {
    return this.mHasOverlappingRendering;
  }
  
  public boolean hasPreviousViewInSameRow(int paramInt)
  {
    return this.mLayoutManager.hasPreviousViewInSameRow(paramInt);
  }
  
  void initBaseGridViewAttributes(Context paramContext, AttributeSet paramAttributeSet)
  {
    paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.lbBaseGridView);
    boolean bool1 = paramContext.getBoolean(R.styleable.lbBaseGridView_focusOutFront, false);
    boolean bool2 = paramContext.getBoolean(R.styleable.lbBaseGridView_focusOutEnd, false);
    this.mLayoutManager.setFocusOutAllowed(bool1, bool2);
    bool1 = paramContext.getBoolean(R.styleable.lbBaseGridView_focusOutSideStart, true);
    bool2 = paramContext.getBoolean(R.styleable.lbBaseGridView_focusOutSideEnd, true);
    this.mLayoutManager.setFocusOutSideAllowed(bool1, bool2);
    this.mLayoutManager.setVerticalSpacing(paramContext.getDimensionPixelSize(R.styleable.lbBaseGridView_android_verticalSpacing, paramContext.getDimensionPixelSize(R.styleable.lbBaseGridView_verticalMargin, 0)));
    this.mLayoutManager.setHorizontalSpacing(paramContext.getDimensionPixelSize(R.styleable.lbBaseGridView_android_horizontalSpacing, paramContext.getDimensionPixelSize(R.styleable.lbBaseGridView_horizontalMargin, 0)));
    if (paramContext.hasValue(R.styleable.lbBaseGridView_android_gravity)) {
      setGravity(paramContext.getInt(R.styleable.lbBaseGridView_android_gravity, 0));
    }
    paramContext.recycle();
  }
  
  public boolean isChildLayoutAnimated()
  {
    return this.mAnimateChildLayout;
  }
  
  final boolean isChildrenDrawingOrderEnabledInternal()
  {
    return isChildrenDrawingOrderEnabled();
  }
  
  public boolean isFocusDrawingOrderEnabled()
  {
    return super.isChildrenDrawingOrderEnabled();
  }
  
  public final boolean isFocusSearchDisabled()
  {
    return this.mLayoutManager.isFocusSearchDisabled();
  }
  
  public boolean isItemAlignmentOffsetWithPadding()
  {
    return this.mLayoutManager.isItemAlignmentOffsetWithPadding();
  }
  
  public boolean isScrollEnabled()
  {
    return this.mLayoutManager.isScrollEnabled();
  }
  
  public boolean isWindowAlignmentPreferKeyLineOverHighEdge()
  {
    return this.mLayoutManager.mWindowAlignment.mainAxis().isPreferKeylineOverHighEdge();
  }
  
  public boolean isWindowAlignmentPreferKeyLineOverLowEdge()
  {
    return this.mLayoutManager.mWindowAlignment.mainAxis().isPreferKeylineOverLowEdge();
  }
  
  protected void onFocusChanged(boolean paramBoolean, int paramInt, Rect paramRect)
  {
    super.onFocusChanged(paramBoolean, paramInt, paramRect);
    this.mLayoutManager.onFocusChanged(paramBoolean, paramInt, paramRect);
  }
  
  public boolean onRequestFocusInDescendants(int paramInt, Rect paramRect)
  {
    return this.mLayoutManager.gridOnRequestFocusInDescendants(this, paramInt, paramRect);
  }
  
  public void onRtlPropertiesChanged(int paramInt)
  {
    this.mLayoutManager.onRtlPropertiesChanged(paramInt);
  }
  
  public void removeOnChildViewHolderSelectedListener(OnChildViewHolderSelectedListener paramOnChildViewHolderSelectedListener)
  {
    this.mLayoutManager.removeOnChildViewHolderSelectedListener(paramOnChildViewHolderSelectedListener);
  }
  
  public void scrollToPosition(int paramInt)
  {
    if (this.mLayoutManager.mIsSlidingChildViews)
    {
      this.mLayoutManager.setSelectionWithSub(paramInt, 0, 0);
      return;
    }
    super.scrollToPosition(paramInt);
  }
  
  public void setAnimateChildLayout(boolean paramBoolean)
  {
    if (this.mAnimateChildLayout != paramBoolean)
    {
      this.mAnimateChildLayout = paramBoolean;
      if (!this.mAnimateChildLayout)
      {
        this.mSavedItemAnimator = getItemAnimator();
        super.setItemAnimator(null);
      }
    }
    else
    {
      return;
    }
    super.setItemAnimator(this.mSavedItemAnimator);
  }
  
  public void setChildrenVisibility(int paramInt)
  {
    this.mLayoutManager.setChildrenVisibility(paramInt);
  }
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  public void setExtraLayoutSpace(int paramInt)
  {
    this.mLayoutManager.setExtraLayoutSpace(paramInt);
  }
  
  public void setFocusDrawingOrderEnabled(boolean paramBoolean)
  {
    super.setChildrenDrawingOrderEnabled(paramBoolean);
  }
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  public void setFocusScrollStrategy(int paramInt)
  {
    if ((paramInt != 0) && (paramInt != 1) && (paramInt != 2)) {
      throw new IllegalArgumentException("Invalid scrollStrategy");
    }
    this.mLayoutManager.setFocusScrollStrategy(paramInt);
    requestLayout();
  }
  
  public final void setFocusSearchDisabled(boolean paramBoolean)
  {
    if (paramBoolean) {}
    for (int i = 393216;; i = 262144)
    {
      setDescendantFocusability(i);
      this.mLayoutManager.setFocusSearchDisabled(paramBoolean);
      return;
    }
  }
  
  public void setGravity(int paramInt)
  {
    this.mLayoutManager.setGravity(paramInt);
    requestLayout();
  }
  
  public void setHasOverlappingRendering(boolean paramBoolean)
  {
    this.mHasOverlappingRendering = paramBoolean;
  }
  
  @Deprecated
  public void setHorizontalMargin(int paramInt)
  {
    setHorizontalSpacing(paramInt);
  }
  
  public void setHorizontalSpacing(int paramInt)
  {
    this.mLayoutManager.setHorizontalSpacing(paramInt);
    requestLayout();
  }
  
  public void setInitialPrefetchItemCount(int paramInt)
  {
    this.mInitialPrefetchItemCount = paramInt;
  }
  
  public void setItemAlignmentOffset(int paramInt)
  {
    this.mLayoutManager.setItemAlignmentOffset(paramInt);
    requestLayout();
  }
  
  public void setItemAlignmentOffsetPercent(float paramFloat)
  {
    this.mLayoutManager.setItemAlignmentOffsetPercent(paramFloat);
    requestLayout();
  }
  
  public void setItemAlignmentOffsetWithPadding(boolean paramBoolean)
  {
    this.mLayoutManager.setItemAlignmentOffsetWithPadding(paramBoolean);
    requestLayout();
  }
  
  public void setItemAlignmentViewId(int paramInt)
  {
    this.mLayoutManager.setItemAlignmentViewId(paramInt);
  }
  
  @Deprecated
  public void setItemMargin(int paramInt)
  {
    setItemSpacing(paramInt);
  }
  
  public void setItemSpacing(int paramInt)
  {
    this.mLayoutManager.setItemSpacing(paramInt);
    requestLayout();
  }
  
  public void setLayoutEnabled(boolean paramBoolean)
  {
    this.mLayoutManager.setLayoutEnabled(paramBoolean);
  }
  
  public void setOnChildLaidOutListener(OnChildLaidOutListener paramOnChildLaidOutListener)
  {
    this.mLayoutManager.setOnChildLaidOutListener(paramOnChildLaidOutListener);
  }
  
  public void setOnChildSelectedListener(OnChildSelectedListener paramOnChildSelectedListener)
  {
    this.mLayoutManager.setOnChildSelectedListener(paramOnChildSelectedListener);
  }
  
  public void setOnChildViewHolderSelectedListener(OnChildViewHolderSelectedListener paramOnChildViewHolderSelectedListener)
  {
    this.mLayoutManager.setOnChildViewHolderSelectedListener(paramOnChildViewHolderSelectedListener);
  }
  
  public void setOnKeyInterceptListener(OnKeyInterceptListener paramOnKeyInterceptListener)
  {
    this.mOnKeyInterceptListener = paramOnKeyInterceptListener;
  }
  
  public void setOnMotionInterceptListener(OnMotionInterceptListener paramOnMotionInterceptListener)
  {
    this.mOnMotionInterceptListener = paramOnMotionInterceptListener;
  }
  
  public void setOnTouchInterceptListener(OnTouchInterceptListener paramOnTouchInterceptListener)
  {
    this.mOnTouchInterceptListener = paramOnTouchInterceptListener;
  }
  
  public void setOnUnhandledKeyListener(OnUnhandledKeyListener paramOnUnhandledKeyListener)
  {
    this.mOnUnhandledKeyListener = paramOnUnhandledKeyListener;
  }
  
  public void setPruneChild(boolean paramBoolean)
  {
    this.mLayoutManager.setPruneChild(paramBoolean);
  }
  
  public void setRecyclerListener(RecyclerView.RecyclerListener paramRecyclerListener)
  {
    this.mChainedRecyclerListener = paramRecyclerListener;
  }
  
  public final void setSaveChildrenLimitNumber(int paramInt)
  {
    this.mLayoutManager.mChildrenStates.setLimitNumber(paramInt);
  }
  
  public final void setSaveChildrenPolicy(int paramInt)
  {
    this.mLayoutManager.mChildrenStates.setSavePolicy(paramInt);
  }
  
  public void setScrollEnabled(boolean paramBoolean)
  {
    this.mLayoutManager.setScrollEnabled(paramBoolean);
  }
  
  public void setSelectedPosition(int paramInt)
  {
    this.mLayoutManager.setSelection(paramInt, 0);
  }
  
  public void setSelectedPosition(int paramInt1, int paramInt2)
  {
    this.mLayoutManager.setSelection(paramInt1, paramInt2);
  }
  
  public void setSelectedPosition(final int paramInt, final ViewHolderTask paramViewHolderTask)
  {
    RecyclerView.ViewHolder localViewHolder;
    if (paramViewHolderTask != null)
    {
      localViewHolder = findViewHolderForPosition(paramInt);
      if ((localViewHolder != null) && (!hasPendingAdapterUpdates())) {
        break label41;
      }
      addOnChildViewHolderSelectedListener(new OnChildViewHolderSelectedListener()
      {
        public void onChildViewHolderSelectedAndPositioned(RecyclerView paramAnonymousRecyclerView, RecyclerView.ViewHolder paramAnonymousViewHolder, int paramAnonymousInt1, int paramAnonymousInt2)
        {
          if (paramAnonymousInt1 == paramInt)
          {
            BaseGridView.this.removeOnChildViewHolderSelectedListener(this);
            paramViewHolderTask.run(paramAnonymousViewHolder);
          }
        }
      });
    }
    for (;;)
    {
      setSelectedPosition(paramInt);
      return;
      label41:
      paramViewHolderTask.run(localViewHolder);
    }
  }
  
  public void setSelectedPositionSmooth(int paramInt)
  {
    this.mLayoutManager.setSelectionSmooth(paramInt);
  }
  
  public void setSelectedPositionSmooth(final int paramInt, final ViewHolderTask paramViewHolderTask)
  {
    RecyclerView.ViewHolder localViewHolder;
    if (paramViewHolderTask != null)
    {
      localViewHolder = findViewHolderForPosition(paramInt);
      if ((localViewHolder != null) && (!hasPendingAdapterUpdates())) {
        break label41;
      }
      addOnChildViewHolderSelectedListener(new OnChildViewHolderSelectedListener()
      {
        public void onChildViewHolderSelected(RecyclerView paramAnonymousRecyclerView, RecyclerView.ViewHolder paramAnonymousViewHolder, int paramAnonymousInt1, int paramAnonymousInt2)
        {
          if (paramAnonymousInt1 == paramInt)
          {
            BaseGridView.this.removeOnChildViewHolderSelectedListener(this);
            paramViewHolderTask.run(paramAnonymousViewHolder);
          }
        }
      });
    }
    for (;;)
    {
      setSelectedPositionSmooth(paramInt);
      return;
      label41:
      paramViewHolderTask.run(localViewHolder);
    }
  }
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  public void setSelectedPositionSmoothWithSub(int paramInt1, int paramInt2)
  {
    this.mLayoutManager.setSelectionSmoothWithSub(paramInt1, paramInt2);
  }
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  public void setSelectedPositionWithSub(int paramInt1, int paramInt2)
  {
    this.mLayoutManager.setSelectionWithSub(paramInt1, paramInt2, 0);
  }
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  public void setSelectedPositionWithSub(int paramInt1, int paramInt2, int paramInt3)
  {
    this.mLayoutManager.setSelectionWithSub(paramInt1, paramInt2, paramInt3);
  }
  
  @Deprecated
  public void setVerticalMargin(int paramInt)
  {
    setVerticalSpacing(paramInt);
  }
  
  public void setVerticalSpacing(int paramInt)
  {
    this.mLayoutManager.setVerticalSpacing(paramInt);
    requestLayout();
  }
  
  public void setWindowAlignment(int paramInt)
  {
    this.mLayoutManager.setWindowAlignment(paramInt);
    requestLayout();
  }
  
  public void setWindowAlignmentOffset(int paramInt)
  {
    this.mLayoutManager.setWindowAlignmentOffset(paramInt);
    requestLayout();
  }
  
  public void setWindowAlignmentOffsetPercent(float paramFloat)
  {
    this.mLayoutManager.setWindowAlignmentOffsetPercent(paramFloat);
    requestLayout();
  }
  
  public void setWindowAlignmentPreferKeyLineOverHighEdge(boolean paramBoolean)
  {
    this.mLayoutManager.mWindowAlignment.mainAxis().setPreferKeylineOverHighEdge(paramBoolean);
    requestLayout();
  }
  
  public void setWindowAlignmentPreferKeyLineOverLowEdge(boolean paramBoolean)
  {
    this.mLayoutManager.mWindowAlignment.mainAxis().setPreferKeylineOverLowEdge(paramBoolean);
    requestLayout();
  }
  
  public void smoothScrollToPosition(int paramInt)
  {
    if (this.mLayoutManager.mIsSlidingChildViews)
    {
      this.mLayoutManager.setSelectionWithSub(paramInt, 0, 0);
      return;
    }
    super.smoothScrollToPosition(paramInt);
  }
  
  public static abstract interface OnKeyInterceptListener
  {
    public abstract boolean onInterceptKeyEvent(KeyEvent paramKeyEvent);
  }
  
  public static abstract interface OnMotionInterceptListener
  {
    public abstract boolean onInterceptMotionEvent(MotionEvent paramMotionEvent);
  }
  
  public static abstract interface OnTouchInterceptListener
  {
    public abstract boolean onInterceptTouchEvent(MotionEvent paramMotionEvent);
  }
  
  public static abstract interface OnUnhandledKeyListener
  {
    public abstract boolean onUnhandledKey(KeyEvent paramKeyEvent);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/BaseGridView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */