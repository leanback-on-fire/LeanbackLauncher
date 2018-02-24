package com.google.android.tvlauncher.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.AccessibilityDelegate;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewOutlineProvider;
import android.view.Window;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeInfo.CollectionInfo;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ContextMenu
{
  private static final int BOTTOM_ALIGN = 1;
  private static final int SCROLL = 2;
  private static final int TOP_ALIGN = 0;
  private Activity mActivity;
  private View mAnchor;
  private int mAnchorRealHeight;
  private int mAnchorRealWidth;
  private int mAnchorX;
  private int mAnchorY;
  private List<ContextMenuItem> mContextMenuItems = new ArrayList();
  private CutoutOverlayLayout mCutoutOverlay;
  private int mDeltaX;
  private int mDeltaY;
  private final int mDimBackgroundColor;
  private final int mDisabledColor;
  private final int mEnabledColor;
  private final int mFocusedColor;
  private int mGravity;
  private int mHorizontalPosition;
  private boolean mIsShowing = false;
  private FrameLayout mMenuContainer;
  private int mMenuHeight;
  private final int mMenuHeightPerRow;
  private LinearLayout mMenuLinearLayout;
  private final int mMenuVerticalMargin;
  private int mMenuWidth;
  private OnDismissListener mOnDismissListener;
  private OnItemClickListener mOnItemClickListener;
  private final float mOverlayAlpha;
  private ObjectAnimator mOverlayAnimator;
  private final int mOverlayDismissAnimationDuration;
  private final int mOverlayShowAnimationDuration;
  private final int mOverscanHorizontal;
  private final int mOverscanVertical;
  private PopupWindow mPopupWindow;
  private ViewGroup mRootParentWindow;
  private ImageView mTriangle;
  private final int mTriangleEdgeOffset;
  private final int mTriangleHeight;
  private final int mTriangleVerticalMenuMargin;
  private final int mTriangleWidth;
  private final int mUnfocusedColor;
  @VerticalPosition
  private int mVerticalPosition;
  private List<ContextMenuItem> mVisibleItems;
  private List<View> mVisibleMenuItemViews = new ArrayList();
  
  public ContextMenu(Activity paramActivity, View paramView, int paramInt)
  {
    this(paramActivity, paramView, paramInt, paramView.getScaleX(), paramView.getScaleY());
  }
  
  public ContextMenu(Activity paramActivity, View paramView, int paramInt, float paramFloat1, float paramFloat2)
  {
    this.mAnchor = paramView;
    this.mActivity = paramActivity;
    this.mMenuContainer = new FrameLayout(this.mActivity);
    this.mMenuContainer.setContentDescription(this.mActivity.getString(2131492930));
    this.mPopupWindow = new PopupWindow(this.mMenuContainer, -2, -2);
    this.mPopupWindow.setFocusable(true);
    this.mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener()
    {
      public void onDismiss()
      {
        ContextMenu.this.clearDimBackground();
        if (ContextMenu.this.mOnDismissListener != null) {
          ContextMenu.this.mOnDismissListener.onDismiss();
        }
        ContextMenu.access$202(ContextMenu.this, false);
      }
    });
    paramActivity = new int[2];
    this.mAnchor.getLocationInWindow(paramActivity);
    this.mAnchorX = paramActivity[0];
    this.mAnchorY = paramActivity[1];
    this.mAnchorRealWidth = ((int)(this.mAnchor.getWidth() * paramFloat1));
    this.mAnchorRealHeight = ((int)(this.mAnchor.getHeight() * paramFloat2));
    int i = (int)(paramInt * paramFloat1);
    paramInt = (int)(paramInt * paramFloat2);
    this.mMenuVerticalMargin = getDimenInPixels(2131558561);
    this.mTriangleVerticalMenuMargin = getDimenInPixels(2131558559);
    this.mTriangleEdgeOffset = getDimenInPixels(2131558557);
    this.mTriangleHeight = getDimenInPixels(2131558558);
    this.mTriangleWidth = getDimenInPixels(2131558560);
    this.mFocusedColor = this.mActivity.getColor(2131820589);
    this.mUnfocusedColor = this.mActivity.getColor(2131820590);
    this.mEnabledColor = this.mActivity.getColor(2131820592);
    this.mDisabledColor = this.mActivity.getColor(2131820591);
    this.mMenuHeightPerRow = getDimenInPixels(2131558549);
    this.mOverscanHorizontal = getDimenInPixels(2131558953);
    this.mOverscanVertical = getDimenInPixels(2131558954);
    this.mOverlayAlpha = getFloat(2131296261);
    this.mOverlayShowAnimationDuration = this.mActivity.getResources().getInteger(2131689482);
    this.mOverlayDismissAnimationDuration = this.mActivity.getResources().getInteger(2131689481);
    this.mDimBackgroundColor = this.mActivity.getColor(2131820593);
    paramActivity = new RectF(this.mAnchorX, this.mAnchorY, this.mAnchorX + this.mAnchorRealWidth, this.mAnchorY + this.mAnchorRealHeight);
    this.mCutoutOverlay = new CutoutOverlayLayout(this.mActivity, i, paramInt);
    this.mCutoutOverlay.setAnchorRect(paramActivity);
    this.mTriangle = new ImageView(this.mActivity);
  }
  
  private void addMenuItemViews()
  {
    this.mMenuLinearLayout.removeAllViews();
    LayoutInflater localLayoutInflater = (LayoutInflater)this.mActivity.getSystemService("layout_inflater");
    int i = 0;
    while (i < this.mVisibleItems.size())
    {
      View localView = localLayoutInflater.inflate(2130968614, null);
      bindMenuItemView((ContextMenuItem)this.mVisibleItems.get(i), localView);
      this.mMenuLinearLayout.addView(localView, -1, this.mMenuHeightPerRow);
      i += 1;
    }
  }
  
  private void adjustLayoutMenu()
  {
    ViewGroup.MarginLayoutParams localMarginLayoutParams = (ViewGroup.MarginLayoutParams)this.mMenuLinearLayout.getLayoutParams();
    if (this.mHorizontalPosition != 17)
    {
      localMarginLayoutParams.topMargin = 0;
      localMarginLayoutParams.bottomMargin = 0;
      if (this.mHorizontalPosition == 3) {
        localMarginLayoutParams.rightMargin = this.mTriangleHeight;
      }
    }
    while (((this.mHorizontalPosition == 17) && (testBit(this.mGravity, 48))) || ((this.mHorizontalPosition != 17) && (this.mVerticalPosition == 1)))
    {
      this.mMenuLinearLayout.removeAllViews();
      int i = this.mVisibleItems.size() - 1;
      while (i >= 0)
      {
        this.mMenuLinearLayout.addView((View)this.mVisibleMenuItemViews.get(i));
        i -= 1;
      }
      localMarginLayoutParams.leftMargin = this.mTriangleHeight;
      continue;
      if (testBit(this.mGravity, 48))
      {
        localMarginLayoutParams.bottomMargin = this.mMenuVerticalMargin;
        localMarginLayoutParams.topMargin = 0;
      }
      else
      {
        localMarginLayoutParams.bottomMargin = 0;
        localMarginLayoutParams.topMargin = this.mMenuVerticalMargin;
      }
    }
  }
  
  private void adjustMenuShowUpPosition()
  {
    this.mDeltaX = 0;
    this.mDeltaY = 0;
    if (this.mHorizontalPosition == 17)
    {
      int j = this.mAnchorRealWidth - this.mAnchor.getWidth();
      int i = this.mAnchorRealHeight - this.mAnchor.getHeight();
      if (testBit(this.mGravity, 80))
      {
        this.mDeltaY = i;
        if (!testBit(this.mGravity, 5)) {
          break label88;
        }
      }
      label88:
      for (i = j;; i = 0)
      {
        this.mDeltaX = i;
        return;
        i = 0;
        break;
      }
    }
    if (this.mHorizontalPosition == 3) {}
    for (this.mDeltaX = (-this.mMenuWidth);; this.mDeltaX = this.mAnchorRealWidth)
    {
      this.mPopupWindow.setOverlapAnchor(true);
      if (this.mVerticalPosition != 1) {
        break;
      }
      this.mDeltaY = (-(this.mMenuHeight - this.mAnchorRealHeight));
      return;
    }
  }
  
  private void adjustTrianglePosition()
  {
    float f1 = 270.0F;
    float f2 = 90.0F;
    FrameLayout.LayoutParams localLayoutParams = (FrameLayout.LayoutParams)this.mTriangle.getLayoutParams();
    localLayoutParams.gravity = 0;
    if (this.mHorizontalPosition == 17)
    {
      if (getRelativeGravity(this.mGravity, this.mAnchor.getLayoutDirection()) == 8388613)
      {
        localLayoutParams.gravity |= 0x800005;
        localLayoutParams.setMarginEnd(this.mTriangleEdgeOffset);
      }
      while (testBit(this.mGravity, 48))
      {
        localLayoutParams.gravity |= 0x50;
        localLayoutParams.bottomMargin = this.mTriangleVerticalMenuMargin;
        this.mTriangle.setScaleY(-1.0F);
        return;
        localLayoutParams.gravity |= 0x800003;
        localLayoutParams.setMarginStart(this.mTriangleEdgeOffset);
      }
      localLayoutParams.gravity |= 0x30;
      localLayoutParams.topMargin = this.mTriangleVerticalMenuMargin;
      return;
    }
    localLayoutParams.gravity |= 0x800003;
    ImageView localImageView;
    if (getRelativeGravity(this.mHorizontalPosition, this.mAnchor.getLayoutDirection()) == 8388611)
    {
      localLayoutParams.setMarginStart(this.mMenuWidth - 2);
      localImageView = this.mTriangle;
      if (this.mAnchor.getLayoutDirection() == 1)
      {
        localImageView.setRotation(f1);
        if (this.mVerticalPosition != 0) {
          break label341;
        }
        localLayoutParams.gravity |= 0x30;
        localLayoutParams.topMargin = ((this.mAnchorRealHeight - this.mTriangleWidth) / 2);
      }
    }
    for (;;)
    {
      this.mMenuWidth += this.mTriangleHeight;
      return;
      f1 = 90.0F;
      break;
      localLayoutParams.setMarginStart(0);
      localImageView = this.mTriangle;
      if (this.mAnchor.getLayoutDirection() == 1) {}
      for (f1 = f2;; f1 = 270.0F)
      {
        localImageView.setRotation(f1);
        break;
      }
      label341:
      if (this.mVerticalPosition == 1)
      {
        localLayoutParams.gravity |= 0x50;
        localLayoutParams.bottomMargin = ((this.mAnchorRealHeight - this.mTriangleWidth) / 2);
      }
      else
      {
        int i = this.mRootParentWindow.getHeight();
        int j = this.mMenuHeight;
        localLayoutParams.gravity |= 0x30;
        localLayoutParams.topMargin = (this.mAnchorY - (i - j) + (this.mAnchorRealHeight - this.mTriangleWidth) / 2);
      }
    }
  }
  
  private void animateBackgroundOverlayAlpha(float paramFloat, int paramInt)
  {
    if (this.mOverlayAnimator != null) {
      this.mOverlayAnimator.cancel();
    }
    this.mOverlayAnimator = ObjectAnimator.ofFloat(this.mCutoutOverlay, View.ALPHA, new float[] { paramFloat });
    this.mOverlayAnimator.setDuration(paramInt);
    this.mOverlayAnimator.start();
  }
  
  private void bindMenuItemView(final ContextMenuItem paramContextMenuItem, final View paramView)
  {
    Object localObject = (TextView)paramView.findViewById(2131951740);
    ((TextView)localObject).setText(paramContextMenuItem.getTitle());
    Context localContext = paramView.getContext();
    if (paramContextMenuItem.isEnabled())
    {
      i = 2131820595;
      ((TextView)localObject).setTextColor(localContext.getColor(i));
      localObject = (ImageView)paramView.findViewById(2131951739);
      if (!paramContextMenuItem.isEnabled()) {
        break label168;
      }
    }
    label168:
    for (int i = this.mEnabledColor;; i = this.mDisabledColor)
    {
      ((ImageView)localObject).setColorFilter(i, PorterDuff.Mode.SRC_ATOP);
      if (paramContextMenuItem.getIcon() != null) {
        ((ImageView)localObject).setImageDrawable(paramContextMenuItem.getIcon());
      }
      paramView.setBackgroundColor(this.mUnfocusedColor);
      paramView.setFocusable(paramContextMenuItem.isEnabled());
      paramView.setEnabled(paramContextMenuItem.isEnabled());
      paramView.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          if (paramContextMenuItem.isEnabled())
          {
            ContextMenu.this.forceDismiss();
            if (ContextMenu.this.mOnItemClickListener != null) {
              ContextMenu.this.mOnItemClickListener.onItemClick(paramContextMenuItem);
            }
          }
        }
      });
      paramView.setOnFocusChangeListener(new View.OnFocusChangeListener()
      {
        public void onFocusChange(View paramAnonymousView, boolean paramAnonymousBoolean)
        {
          if (paramAnonymousBoolean)
          {
            if (paramContextMenuItem.isLinkedWithTriangle()) {
              ContextMenu.this.mTriangle.setColorFilter(ContextMenu.this.mFocusedColor, PorterDuff.Mode.SRC_ATOP);
            }
            paramView.setBackgroundColor(ContextMenu.this.mFocusedColor);
            return;
          }
          if (paramContextMenuItem.isLinkedWithTriangle()) {
            ContextMenu.this.mTriangle.setColorFilter(ContextMenu.this.mUnfocusedColor, PorterDuff.Mode.SRC_ATOP);
          }
          paramView.setBackgroundColor(ContextMenu.this.mUnfocusedColor);
        }
      });
      this.mVisibleMenuItemViews.add(paramView);
      return;
      i = 2131820594;
      break;
    }
  }
  
  private void calculateMenuSize()
  {
    this.mMenuLinearLayout.measure(0, 0);
    this.mMenuWidth = this.mMenuLinearLayout.getMeasuredWidth();
    this.mMenuHeight = (this.mMenuLinearLayout.getMeasuredHeight() + this.mMenuVerticalMargin);
  }
  
  private void clearDimBackground()
  {
    animateBackgroundOverlayAlpha(0.0F, this.mOverlayDismissAnimationDuration);
    this.mOverlayAnimator.addListener(new AnimatorListenerAdapter()
    {
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        ContextMenu.this.mRootParentWindow.removeView(ContextMenu.this.mCutoutOverlay);
      }
    });
  }
  
  private void determineGravity()
  {
    this.mGravity = 0;
    this.mHorizontalPosition = 17;
    if (this.mAnchor.getLayoutDirection() == 1) {
      if (this.mAnchorX + this.mAnchorRealWidth - this.mMenuWidth >= this.mOverscanHorizontal) {
        this.mGravity |= 0x5;
      }
    }
    while (this.mAnchorY + this.mAnchorRealHeight + this.mMenuHeight <= this.mRootParentWindow.getHeight() - this.mOverscanVertical)
    {
      this.mGravity |= 0x50;
      return;
      this.mGravity |= 0x3;
      continue;
      if (this.mAnchorX + this.mMenuWidth <= this.mRootParentWindow.getWidth() - this.mOverscanHorizontal) {
        this.mGravity |= 0x3;
      } else {
        this.mGravity |= 0x5;
      }
    }
    if (this.mAnchorY - this.mMenuHeight >= this.mOverscanVertical)
    {
      this.mGravity |= 0x30;
      return;
    }
    this.mMenuHeight -= this.mMenuVerticalMargin;
    if (this.mAnchor.getLayoutDirection() == 0) {
      if (this.mAnchorX + this.mAnchorRealWidth + this.mMenuWidth + this.mTriangleHeight <= this.mRootParentWindow.getWidth() - this.mOverscanHorizontal) {
        this.mHorizontalPosition = 5;
      }
    }
    while (this.mAnchorY + this.mMenuHeight <= this.mRootParentWindow.getHeight() - this.mOverscanVertical)
    {
      this.mVerticalPosition = 0;
      return;
      this.mHorizontalPosition = 3;
      continue;
      if (this.mAnchorX - this.mMenuWidth - this.mTriangleHeight >= this.mOverscanHorizontal) {
        this.mHorizontalPosition = 3;
      } else {
        this.mHorizontalPosition = 5;
      }
    }
    if (this.mAnchorY + this.mAnchorRealHeight - this.mMenuHeight >= this.mOverscanVertical)
    {
      this.mVerticalPosition = 1;
      return;
    }
    this.mVerticalPosition = 2;
  }
  
  private void dimBackground()
  {
    this.mRootParentWindow.addView(this.mCutoutOverlay, this.mRootParentWindow.getWidth(), this.mRootParentWindow.getHeight());
    this.mCutoutOverlay.setAlpha(0.0F);
    animateBackgroundOverlayAlpha(this.mOverlayAlpha, this.mOverlayShowAnimationDuration);
  }
  
  private int getDimenInPixels(int paramInt)
  {
    return this.mAnchor.getResources().getDimensionPixelSize(paramInt);
  }
  
  private float getFloat(int paramInt)
  {
    TypedValue localTypedValue = new TypedValue();
    this.mActivity.getResources().getValue(paramInt, localTypedValue, true);
    return localTypedValue.getFloat();
  }
  
  private int getRelativeGravity(int paramInt1, int paramInt2)
  {
    if (((testBit(paramInt1, 5)) && (paramInt2 == 0)) || ((testBit(paramInt1, 3)) && (paramInt2 == 1))) {
      return 8388613;
    }
    if (((testBit(paramInt1, 3)) && (paramInt2 == 0)) || ((testBit(paramInt1, 5)) && (paramInt2 == 1))) {
      return 8388611;
    }
    return 0;
  }
  
  private List<ContextMenuItem> getVisibleItems()
  {
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = this.mContextMenuItems.iterator();
    while (localIterator.hasNext())
    {
      ContextMenuItem localContextMenuItem = (ContextMenuItem)localIterator.next();
      if (localContextMenuItem.isVisible()) {
        localArrayList.add(localContextMenuItem);
      }
    }
    return localArrayList;
  }
  
  private boolean testBit(int paramInt1, int paramInt2)
  {
    if (paramInt2 == 0) {
      if (paramInt1 != 0) {}
    }
    while ((paramInt1 & paramInt2) == paramInt2)
    {
      return true;
      return false;
    }
    return false;
  }
  
  public void addItem(ContextMenuItem paramContextMenuItem)
  {
    this.mContextMenuItems.add(paramContextMenuItem);
  }
  
  public ContextMenuItem findItem(int paramInt)
  {
    Iterator localIterator = this.mContextMenuItems.iterator();
    while (localIterator.hasNext())
    {
      ContextMenuItem localContextMenuItem = (ContextMenuItem)localIterator.next();
      if (localContextMenuItem.getId() == paramInt) {
        return localContextMenuItem;
      }
    }
    return null;
  }
  
  public void forceDismiss()
  {
    this.mPopupWindow.dismiss();
  }
  
  public boolean isShowing()
  {
    return this.mIsShowing;
  }
  
  public void setOnDismissListener(OnDismissListener paramOnDismissListener)
  {
    this.mOnDismissListener = paramOnDismissListener;
  }
  
  public void setOnMenuItemClickListener(OnItemClickListener paramOnItemClickListener)
  {
    this.mOnItemClickListener = paramOnItemClickListener;
  }
  
  public void show()
  {
    this.mVisibleItems = getVisibleItems();
    this.mVisibleMenuItemViews.clear();
    if (this.mRootParentWindow == null) {
      this.mRootParentWindow = ((ViewGroup)this.mActivity.getWindow().getDecorView().getRootView());
    }
    dimBackground();
    this.mMenuLinearLayout = new LinearLayout(this.mActivity);
    this.mMenuLinearLayout.setOrientation(1);
    this.mMenuContainer.addView(this.mMenuLinearLayout, -2, -2);
    this.mMenuContainer.setAccessibilityDelegate(new View.AccessibilityDelegate()
    {
      public void onInitializeAccessibilityNodeInfo(View paramAnonymousView, AccessibilityNodeInfo paramAnonymousAccessibilityNodeInfo)
      {
        super.onInitializeAccessibilityNodeInfo(paramAnonymousView, paramAnonymousAccessibilityNodeInfo);
        paramAnonymousAccessibilityNodeInfo.setCollectionInfo(AccessibilityNodeInfo.CollectionInfo.obtain(ContextMenu.this.mVisibleItems.size(), 0, false));
      }
    });
    this.mMenuLinearLayout.setOutlineProvider(new ViewOutlineProvider()
    {
      public void getOutline(View paramAnonymousView, Outline paramAnonymousOutline)
      {
        paramAnonymousOutline.setRoundRect(0, 0, paramAnonymousView.getWidth(), paramAnonymousView.getHeight(), paramAnonymousView.getResources().getDimensionPixelSize(2131558510));
      }
    });
    this.mMenuLinearLayout.setClipToOutline(true);
    this.mTriangle.setImageDrawable(this.mActivity.getDrawable(2130837608));
    this.mTriangle.setColorFilter(this.mUnfocusedColor, PorterDuff.Mode.SRC_ATOP);
    addMenuItemViews();
    calculateMenuSize();
    determineGravity();
    this.mMenuContainer.addView(this.mTriangle, -2, -2);
    adjustTrianglePosition();
    adjustLayoutMenu();
    int j = ((ViewGroup.MarginLayoutParams)this.mTriangle.getLayoutParams()).topMargin;
    int i;
    if (this.mHorizontalPosition != 17)
    {
      i = 0;
      if (i < this.mVisibleItems.size())
      {
        if ((j >= this.mMenuHeightPerRow * i) && (j <= this.mMenuHeightPerRow * (i + 1))) {
          ((ContextMenuItem)this.mVisibleItems.get(i)).setLinkedWithTriangle(true);
        }
      }
      else
      {
        label267:
        adjustMenuShowUpPosition();
        ((View)this.mVisibleMenuItemViews.get(0)).requestFocus();
        this.mPopupWindow.setWidth(this.mMenuWidth);
        this.mPopupWindow.setHeight(this.mMenuHeight);
        if (this.mHorizontalPosition != 17) {
          break label375;
        }
        this.mPopupWindow.showAsDropDown(this.mAnchor, this.mDeltaX, this.mDeltaY, this.mGravity);
      }
    }
    for (;;)
    {
      this.mIsShowing = true;
      return;
      i += 1;
      break;
      ((ContextMenuItem)this.mVisibleItems.get(0)).setLinkedWithTriangle(true);
      break label267;
      label375:
      this.mPopupWindow.showAsDropDown(this.mAnchor, this.mDeltaX, this.mDeltaY, 3);
    }
  }
  
  private class CutoutOverlayLayout
    extends FrameLayout
  {
    private Paint mPaint = new Paint();
    private int mRadiusX;
    private int mRadiusY;
    private RectF mRect;
    
    public CutoutOverlayLayout(Context paramContext, int paramInt1, int paramInt2)
    {
      super();
      this.mRadiusX = paramInt1;
      this.mRadiusY = paramInt2;
      setWillNotDraw(false);
      setLayerType(2, null);
      this.mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }
    
    protected void onDraw(Canvas paramCanvas)
    {
      paramCanvas.drawColor(ContextMenu.this.mDimBackgroundColor);
      paramCanvas.drawRoundRect(this.mRect, this.mRadiusX, this.mRadiusY, this.mPaint);
    }
    
    public void setAnchorRect(RectF paramRectF)
    {
      this.mRect = paramRectF;
    }
  }
  
  public static abstract interface OnDismissListener
  {
    public abstract void onDismiss();
  }
  
  public static abstract interface OnItemClickListener
  {
    public abstract void onItemClick(ContextMenuItem paramContextMenuItem);
  }
  
  public static @interface VerticalPosition {}
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/util/ContextMenu.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */