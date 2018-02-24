package android.support.v17.leanback.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.annotation.VisibleForTesting;
import android.support.v17.leanback.R.attr;
import android.support.v17.leanback.R.integer;
import android.support.v17.leanback.R.styleable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import java.util.ArrayList;

public class BaseCardView
  extends FrameLayout
{
  public static final int CARD_REGION_VISIBLE_ACTIVATED = 1;
  public static final int CARD_REGION_VISIBLE_ALWAYS = 0;
  public static final int CARD_REGION_VISIBLE_SELECTED = 2;
  public static final int CARD_TYPE_INFO_OVER = 1;
  public static final int CARD_TYPE_INFO_UNDER = 2;
  public static final int CARD_TYPE_INFO_UNDER_WITH_EXTRA = 3;
  private static final int CARD_TYPE_INVALID = 4;
  public static final int CARD_TYPE_MAIN_ONLY = 0;
  private static final boolean DEBUG = false;
  private static final int[] LB_PRESSED_STATE_SET = { 16842919 };
  private static final String TAG = "BaseCardView";
  private final int mActivatedAnimDuration;
  private Animation mAnim;
  private final Runnable mAnimationTrigger = new Runnable()
  {
    public void run()
    {
      BaseCardView.this.animateInfoOffset(true);
    }
  };
  private int mCardType;
  private boolean mDelaySelectedAnim;
  ArrayList<View> mExtraViewList;
  private int mExtraVisibility;
  float mInfoAlpha;
  float mInfoOffset;
  ArrayList<View> mInfoViewList;
  float mInfoVisFraction;
  private int mInfoVisibility;
  private ArrayList<View> mMainViewList;
  private int mMeasuredHeight;
  private int mMeasuredWidth;
  private final int mSelectedAnimDuration;
  private int mSelectedAnimationDelay;
  
  public BaseCardView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public BaseCardView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, R.attr.baseCardViewStyle);
  }
  
  public BaseCardView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.lbBaseCardView, paramInt, 0);
    try
    {
      this.mCardType = paramContext.getInteger(R.styleable.lbBaseCardView_cardType, 0);
      paramAttributeSet = paramContext.getDrawable(R.styleable.lbBaseCardView_cardForeground);
      if (paramAttributeSet != null) {
        setForeground(paramAttributeSet);
      }
      paramAttributeSet = paramContext.getDrawable(R.styleable.lbBaseCardView_cardBackground);
      if (paramAttributeSet != null) {
        setBackground(paramAttributeSet);
      }
      this.mInfoVisibility = paramContext.getInteger(R.styleable.lbBaseCardView_infoVisibility, 1);
      this.mExtraVisibility = paramContext.getInteger(R.styleable.lbBaseCardView_extraVisibility, 2);
      if (this.mExtraVisibility < this.mInfoVisibility) {
        this.mExtraVisibility = this.mInfoVisibility;
      }
      this.mSelectedAnimationDelay = paramContext.getInteger(R.styleable.lbBaseCardView_selectedAnimationDelay, getResources().getInteger(R.integer.lb_card_selected_animation_delay));
      this.mSelectedAnimDuration = paramContext.getInteger(R.styleable.lbBaseCardView_selectedAnimationDuration, getResources().getInteger(R.integer.lb_card_selected_animation_duration));
      this.mActivatedAnimDuration = paramContext.getInteger(R.styleable.lbBaseCardView_activatedAnimationDuration, getResources().getInteger(R.integer.lb_card_activated_animation_duration));
      paramContext.recycle();
      this.mDelaySelectedAnim = true;
      this.mMainViewList = new ArrayList();
      this.mInfoViewList = new ArrayList();
      this.mExtraViewList = new ArrayList();
      this.mInfoOffset = 0.0F;
      this.mInfoVisFraction = getFinalInfoVisFraction();
      this.mInfoAlpha = getFinalInfoAlpha();
      return;
    }
    finally
    {
      paramContext.recycle();
    }
  }
  
  private void animateInfoAlpha(boolean paramBoolean)
  {
    float f2 = 1.0F;
    cancelAnimations();
    if (paramBoolean)
    {
      int i = 0;
      while (i < this.mInfoViewList.size())
      {
        ((View)this.mInfoViewList.get(i)).setVisibility(0);
        i += 1;
      }
    }
    if (paramBoolean) {}
    for (float f1 = 1.0F; f1 == this.mInfoAlpha; f1 = 0.0F) {
      return;
    }
    float f3 = this.mInfoAlpha;
    if (paramBoolean) {}
    for (f1 = f2;; f1 = 0.0F)
    {
      this.mAnim = new InfoAlphaAnimation(f3, f1);
      this.mAnim.setDuration(this.mActivatedAnimDuration);
      this.mAnim.setInterpolator(new DecelerateInterpolator());
      this.mAnim.setAnimationListener(new Animation.AnimationListener()
      {
        public void onAnimationEnd(Animation paramAnonymousAnimation)
        {
          if (BaseCardView.this.mInfoAlpha == 0.0D)
          {
            int i = 0;
            while (i < BaseCardView.this.mInfoViewList.size())
            {
              ((View)BaseCardView.this.mInfoViewList.get(i)).setVisibility(8);
              i += 1;
            }
          }
        }
        
        public void onAnimationRepeat(Animation paramAnonymousAnimation) {}
        
        public void onAnimationStart(Animation paramAnonymousAnimation) {}
      });
      startAnimation(this.mAnim);
      return;
    }
  }
  
  private void animateInfoHeight(boolean paramBoolean)
  {
    cancelAnimations();
    if (paramBoolean)
    {
      int i = 0;
      while (i < this.mInfoViewList.size())
      {
        ((View)this.mInfoViewList.get(i)).setVisibility(0);
        i += 1;
      }
    }
    if (paramBoolean) {}
    for (float f = 1.0F; this.mInfoVisFraction == f; f = 0.0F) {
      return;
    }
    this.mAnim = new InfoHeightAnimation(this.mInfoVisFraction, f);
    this.mAnim.setDuration(this.mSelectedAnimDuration);
    this.mAnim.setInterpolator(new AccelerateDecelerateInterpolator());
    this.mAnim.setAnimationListener(new Animation.AnimationListener()
    {
      public void onAnimationEnd(Animation paramAnonymousAnimation)
      {
        if (BaseCardView.this.mInfoVisFraction == 0.0F)
        {
          int i = 0;
          while (i < BaseCardView.this.mInfoViewList.size())
          {
            ((View)BaseCardView.this.mInfoViewList.get(i)).setVisibility(8);
            i += 1;
          }
        }
      }
      
      public void onAnimationRepeat(Animation paramAnonymousAnimation) {}
      
      public void onAnimationStart(Animation paramAnonymousAnimation) {}
    });
    startAnimation(this.mAnim);
  }
  
  private void applyActiveState(boolean paramBoolean)
  {
    if ((hasInfoRegion()) && (this.mInfoVisibility == 1)) {
      setInfoViewVisibility(isRegionVisible(this.mInfoVisibility));
    }
  }
  
  private void applySelectedState(boolean paramBoolean)
  {
    removeCallbacks(this.mAnimationTrigger);
    if (this.mCardType == 3) {
      if (paramBoolean) {
        if (!this.mDelaySelectedAnim)
        {
          post(this.mAnimationTrigger);
          this.mDelaySelectedAnim = true;
        }
      }
    }
    while (this.mInfoVisibility != 2)
    {
      return;
      postDelayed(this.mAnimationTrigger, this.mSelectedAnimationDelay);
      return;
      animateInfoOffset(false);
      return;
    }
    setInfoViewVisibility(paramBoolean);
  }
  
  private void cancelAnimations()
  {
    if (this.mAnim != null)
    {
      this.mAnim.cancel();
      this.mAnim = null;
      clearAnimation();
    }
  }
  
  private void findChildrenViews()
  {
    this.mMainViewList.clear();
    this.mInfoViewList.clear();
    this.mExtraViewList.clear();
    int n = getChildCount();
    int i;
    int j;
    label65:
    int k;
    label67:
    View localView;
    if ((hasInfoRegion()) && (isCurrentRegionVisible(this.mInfoVisibility)))
    {
      i = 1;
      if ((!hasExtraRegion()) || (this.mInfoOffset <= 0.0F)) {
        break label97;
      }
      j = 1;
      k = 0;
      if (k >= n) {
        return;
      }
      localView = getChildAt(k);
      if (localView != null) {
        break label102;
      }
    }
    for (;;)
    {
      k += 1;
      break label67;
      i = 0;
      break;
      label97:
      j = 0;
      break label65;
      label102:
      LayoutParams localLayoutParams = (LayoutParams)localView.getLayoutParams();
      int m;
      if (localLayoutParams.viewType == 1)
      {
        localView.setAlpha(this.mInfoAlpha);
        this.mInfoViewList.add(localView);
        if (i != 0) {}
        for (m = 0;; m = 8)
        {
          localView.setVisibility(m);
          break;
        }
      }
      if (localLayoutParams.viewType == 2)
      {
        this.mExtraViewList.add(localView);
        if (j != 0) {}
        for (m = 0;; m = 8)
        {
          localView.setVisibility(m);
          break;
        }
      }
      this.mMainViewList.add(localView);
      localView.setVisibility(0);
    }
  }
  
  private boolean hasExtraRegion()
  {
    return this.mCardType == 3;
  }
  
  private boolean hasInfoRegion()
  {
    return this.mCardType != 0;
  }
  
  private boolean isCurrentRegionVisible(int paramInt)
  {
    boolean bool2 = true;
    boolean bool1 = bool2;
    switch (paramInt)
    {
    default: 
      bool1 = false;
    }
    do
    {
      return bool1;
      return isActivated();
      if (this.mCardType != 2) {
        break;
      }
      bool1 = bool2;
    } while (this.mInfoVisFraction > 0.0F);
    return false;
    return isSelected();
  }
  
  private boolean isRegionVisible(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return false;
    case 0: 
      return true;
    case 1: 
      return isActivated();
    }
    return isSelected();
  }
  
  private void setInfoViewVisibility(boolean paramBoolean)
  {
    int i;
    if (this.mCardType == 3)
    {
      if (paramBoolean)
      {
        i = 0;
        while (i < this.mInfoViewList.size())
        {
          ((View)this.mInfoViewList.get(i)).setVisibility(0);
          i += 1;
        }
      }
      i = 0;
      while (i < this.mInfoViewList.size())
      {
        ((View)this.mInfoViewList.get(i)).setVisibility(8);
        i += 1;
      }
      i = 0;
      while (i < this.mExtraViewList.size())
      {
        ((View)this.mExtraViewList.get(i)).setVisibility(8);
        i += 1;
      }
      this.mInfoOffset = 0.0F;
    }
    label149:
    label192:
    do
    {
      return;
      if (this.mCardType == 2)
      {
        if (this.mInfoVisibility == 2)
        {
          animateInfoHeight(paramBoolean);
          return;
        }
        i = 0;
        View localView;
        if (i < this.mInfoViewList.size())
        {
          localView = (View)this.mInfoViewList.get(i);
          if (!paramBoolean) {
            break label192;
          }
        }
        for (int j = 0;; j = 8)
        {
          localView.setVisibility(j);
          i += 1;
          break label149;
          break;
        }
      }
    } while (this.mCardType != 1);
    animateInfoAlpha(paramBoolean);
  }
  
  void animateInfoOffset(boolean paramBoolean)
  {
    cancelAnimations();
    int k = 0;
    int i = 0;
    if (paramBoolean)
    {
      int m = View.MeasureSpec.makeMeasureSpec(this.mMeasuredWidth, 1073741824);
      int n = View.MeasureSpec.makeMeasureSpec(0, 0);
      int j = 0;
      for (;;)
      {
        k = i;
        if (j >= this.mExtraViewList.size()) {
          break;
        }
        View localView = (View)this.mExtraViewList.get(j);
        localView.setVisibility(0);
        localView.measure(m, n);
        i = Math.max(i, localView.getMeasuredHeight());
        j += 1;
      }
    }
    float f2 = this.mInfoOffset;
    if (paramBoolean) {}
    for (float f1 = k;; f1 = 0.0F)
    {
      this.mAnim = new InfoOffsetAnimation(f2, f1);
      this.mAnim.setDuration(this.mSelectedAnimDuration);
      this.mAnim.setInterpolator(new AccelerateDecelerateInterpolator());
      this.mAnim.setAnimationListener(new Animation.AnimationListener()
      {
        public void onAnimationEnd(Animation paramAnonymousAnimation)
        {
          if (BaseCardView.this.mInfoOffset == 0.0F)
          {
            int i = 0;
            while (i < BaseCardView.this.mExtraViewList.size())
            {
              ((View)BaseCardView.this.mExtraViewList.get(i)).setVisibility(8);
              i += 1;
            }
          }
        }
        
        public void onAnimationRepeat(Animation paramAnonymousAnimation) {}
        
        public void onAnimationStart(Animation paramAnonymousAnimation) {}
      });
      startAnimation(this.mAnim);
      return;
    }
  }
  
  protected boolean checkLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
  {
    return paramLayoutParams instanceof LayoutParams;
  }
  
  protected LayoutParams generateDefaultLayoutParams()
  {
    return new LayoutParams(-2, -2);
  }
  
  public LayoutParams generateLayoutParams(AttributeSet paramAttributeSet)
  {
    return new LayoutParams(getContext(), paramAttributeSet);
  }
  
  protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
  {
    if ((paramLayoutParams instanceof LayoutParams)) {
      return new LayoutParams((LayoutParams)paramLayoutParams);
    }
    return new LayoutParams(paramLayoutParams);
  }
  
  public int getCardType()
  {
    return this.mCardType;
  }
  
  @Deprecated
  public int getExtraVisibility()
  {
    return this.mExtraVisibility;
  }
  
  final float getFinalInfoAlpha()
  {
    if ((this.mCardType == 1) && (this.mInfoVisibility == 2) && (!isSelected())) {
      return 0.0F;
    }
    return 1.0F;
  }
  
  final float getFinalInfoVisFraction()
  {
    if ((this.mCardType == 2) && (this.mInfoVisibility == 2) && (!isSelected())) {
      return 0.0F;
    }
    return 1.0F;
  }
  
  public int getInfoVisibility()
  {
    return this.mInfoVisibility;
  }
  
  public boolean isSelectedAnimationDelayed()
  {
    return this.mDelaySelectedAnim;
  }
  
  protected int[] onCreateDrawableState(int paramInt)
  {
    int[] arrayOfInt = super.onCreateDrawableState(paramInt);
    int k = arrayOfInt.length;
    int j = 0;
    int i = 0;
    paramInt = 0;
    while (paramInt < k)
    {
      if (arrayOfInt[paramInt] == 16842919) {
        j = 1;
      }
      if (arrayOfInt[paramInt] == 16842910) {
        i = 1;
      }
      paramInt += 1;
    }
    if ((j != 0) && (i != 0)) {
      return View.PRESSED_ENABLED_STATE_SET;
    }
    if (j != 0) {
      return LB_PRESSED_STATE_SET;
    }
    if (i != 0) {
      return View.ENABLED_STATE_SET;
    }
    return View.EMPTY_STATE_SET;
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    removeCallbacks(this.mAnimationTrigger);
    cancelAnimations();
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    float f2 = getPaddingTop();
    int i = 0;
    View localView;
    float f1;
    while (i < this.mMainViewList.size())
    {
      localView = (View)this.mMainViewList.get(i);
      f1 = f2;
      if (localView.getVisibility() != 8)
      {
        localView.layout(getPaddingLeft(), (int)f2, this.mMeasuredWidth + getPaddingLeft(), (int)(localView.getMeasuredHeight() + f2));
        f1 = f2 + localView.getMeasuredHeight();
      }
      i += 1;
      f2 = f1;
    }
    if (hasInfoRegion())
    {
      float f3 = 0.0F;
      i = 0;
      while (i < this.mInfoViewList.size())
      {
        f3 += ((View)this.mInfoViewList.get(i)).getMeasuredHeight();
        i += 1;
      }
      float f4;
      if (this.mCardType == 1)
      {
        f2 -= f3;
        f1 = f2;
        f4 = f3;
        if (f2 < 0.0F)
        {
          f1 = 0.0F;
          f4 = f3;
        }
        i = 0;
      }
      for (;;)
      {
        f2 = f1;
        if (i < this.mInfoViewList.size())
        {
          localView = (View)this.mInfoViewList.get(i);
          f2 = f1;
          f3 = f4;
          if (localView.getVisibility() != 8)
          {
            int k = localView.getMeasuredHeight();
            int j = k;
            if (k > f4) {
              j = (int)f4;
            }
            localView.layout(getPaddingLeft(), (int)f1, this.mMeasuredWidth + getPaddingLeft(), (int)(j + f1));
            f1 += j;
            f4 -= j;
            f2 = f1;
            f3 = f4;
            if (f4 <= 0.0F) {
              f2 = f1;
            }
          }
        }
        else
        {
          if (!hasExtraRegion()) {
            break label514;
          }
          i = 0;
          while (i < this.mExtraViewList.size())
          {
            localView = (View)this.mExtraViewList.get(i);
            f1 = f2;
            if (localView.getVisibility() != 8)
            {
              localView.layout(getPaddingLeft(), (int)f2, this.mMeasuredWidth + getPaddingLeft(), (int)(localView.getMeasuredHeight() + f2));
              f1 = f2 + localView.getMeasuredHeight();
            }
            i += 1;
            f2 = f1;
          }
          if (this.mCardType == 2)
          {
            f1 = f2;
            f4 = f3;
            if (this.mInfoVisibility != 2) {
              break;
            }
            f4 = f3 * this.mInfoVisFraction;
            f1 = f2;
            break;
          }
          f1 = f2 - this.mInfoOffset;
          f4 = f3;
          break;
        }
        i += 1;
        f1 = f2;
        f4 = f3;
      }
    }
    label514:
    onSizeChanged(0, 0, paramInt3 - paramInt1, paramInt4 - paramInt2);
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    this.mMeasuredWidth = 0;
    this.mMeasuredHeight = 0;
    int i = 0;
    int n = 0;
    int i2 = 0;
    int i5 = 0;
    int i3 = 0;
    int i4 = 0;
    findChildrenViews();
    int i6 = View.MeasureSpec.makeMeasureSpec(0, 0);
    int j = 0;
    View localView;
    int m;
    while (j < this.mMainViewList.size())
    {
      localView = (View)this.mMainViewList.get(j);
      m = n;
      k = i;
      if (localView.getVisibility() != 8)
      {
        measureChild(localView, i6, i6);
        this.mMeasuredWidth = Math.max(this.mMeasuredWidth, localView.getMeasuredWidth());
        m = n + localView.getMeasuredHeight();
        k = View.combineMeasuredStates(i, localView.getMeasuredState());
      }
      j += 1;
      n = m;
      i = k;
    }
    setPivotX(this.mMeasuredWidth / 2);
    setPivotY(n / 2);
    int i7 = View.MeasureSpec.makeMeasureSpec(this.mMeasuredWidth, 1073741824);
    int i1 = i3;
    int k = i;
    if (hasInfoRegion())
    {
      m = 0;
      j = i;
      i = i5;
      while (m < this.mInfoViewList.size())
      {
        localView = (View)this.mInfoViewList.get(m);
        k = i;
        i1 = j;
        if (localView.getVisibility() != 8)
        {
          measureChild(localView, i7, i6);
          k = i;
          if (this.mCardType != 1) {
            k = i + localView.getMeasuredHeight();
          }
          i1 = View.combineMeasuredStates(j, localView.getMeasuredState());
        }
        m += 1;
        i = k;
        j = i1;
      }
      i1 = i3;
      i2 = i;
      k = j;
      if (hasExtraRegion())
      {
        i3 = 0;
        m = i4;
        for (;;)
        {
          i1 = m;
          i2 = i;
          k = j;
          if (i3 >= this.mExtraViewList.size()) {
            break;
          }
          localView = (View)this.mExtraViewList.get(i3);
          i1 = m;
          k = j;
          if (localView.getVisibility() != 8)
          {
            measureChild(localView, i7, i6);
            i1 = m + localView.getMeasuredHeight();
            k = View.combineMeasuredStates(j, localView.getMeasuredState());
          }
          i3 += 1;
          m = i1;
          j = k;
        }
      }
    }
    float f3;
    float f1;
    label482:
    float f4;
    if ((hasInfoRegion()) && (this.mInfoVisibility == 2))
    {
      i = 1;
      f3 = n;
      if (i == 0) {
        break label564;
      }
      f1 = i2 * this.mInfoVisFraction;
      f4 = i1;
      if (i == 0) {
        break label571;
      }
    }
    label564:
    label571:
    for (float f2 = 0.0F;; f2 = this.mInfoOffset)
    {
      this.mMeasuredHeight = ((int)(f4 + (f1 + f3) - f2));
      setMeasuredDimension(View.resolveSizeAndState(this.mMeasuredWidth + getPaddingLeft() + getPaddingRight(), paramInt1, k), View.resolveSizeAndState(this.mMeasuredHeight + getPaddingTop() + getPaddingBottom(), paramInt2, k << 16));
      return;
      i = 0;
      break;
      f1 = i2;
      break label482;
    }
  }
  
  public void setActivated(boolean paramBoolean)
  {
    if (paramBoolean != isActivated())
    {
      super.setActivated(paramBoolean);
      applyActiveState(isActivated());
    }
  }
  
  public void setCardType(int paramInt)
  {
    if (this.mCardType != paramInt) {
      if ((paramInt < 0) || (paramInt >= 4)) {
        break label27;
      }
    }
    for (this.mCardType = paramInt;; this.mCardType = 0)
    {
      requestLayout();
      return;
      label27:
      Log.e("BaseCardView", "Invalid card type specified: " + paramInt + ". Defaulting to type CARD_TYPE_MAIN_ONLY.");
    }
  }
  
  @Deprecated
  public void setExtraVisibility(int paramInt)
  {
    if (this.mExtraVisibility != paramInt) {
      this.mExtraVisibility = paramInt;
    }
  }
  
  public void setInfoVisibility(int paramInt)
  {
    if (this.mInfoVisibility != paramInt)
    {
      cancelAnimations();
      this.mInfoVisibility = paramInt;
      this.mInfoVisFraction = getFinalInfoVisFraction();
      requestLayout();
      float f = getFinalInfoAlpha();
      if (f != this.mInfoAlpha)
      {
        this.mInfoAlpha = f;
        paramInt = 0;
        while (paramInt < this.mInfoViewList.size())
        {
          ((View)this.mInfoViewList.get(paramInt)).setAlpha(this.mInfoAlpha);
          paramInt += 1;
        }
      }
    }
  }
  
  public void setSelected(boolean paramBoolean)
  {
    if (paramBoolean != isSelected())
    {
      super.setSelected(paramBoolean);
      applySelectedState(isSelected());
    }
  }
  
  public void setSelectedAnimationDelayed(boolean paramBoolean)
  {
    this.mDelaySelectedAnim = paramBoolean;
  }
  
  public boolean shouldDelayChildPressedState()
  {
    return false;
  }
  
  public String toString()
  {
    return super.toString();
  }
  
  class AnimationBase
    extends Animation
  {
    AnimationBase() {}
    
    @VisibleForTesting
    final void mockEnd()
    {
      applyTransformation(1.0F, null);
      BaseCardView.this.cancelAnimations();
    }
    
    @VisibleForTesting
    final void mockStart()
    {
      getTransformation(0L, null);
    }
  }
  
  final class InfoAlphaAnimation
    extends BaseCardView.AnimationBase
  {
    private float mDelta;
    private float mStartValue;
    
    public InfoAlphaAnimation(float paramFloat1, float paramFloat2)
    {
      super();
      this.mStartValue = paramFloat1;
      this.mDelta = (paramFloat2 - paramFloat1);
    }
    
    protected void applyTransformation(float paramFloat, Transformation paramTransformation)
    {
      BaseCardView.this.mInfoAlpha = (this.mStartValue + this.mDelta * paramFloat);
      int i = 0;
      while (i < BaseCardView.this.mInfoViewList.size())
      {
        ((View)BaseCardView.this.mInfoViewList.get(i)).setAlpha(BaseCardView.this.mInfoAlpha);
        i += 1;
      }
    }
  }
  
  final class InfoHeightAnimation
    extends BaseCardView.AnimationBase
  {
    private float mDelta;
    private float mStartValue;
    
    public InfoHeightAnimation(float paramFloat1, float paramFloat2)
    {
      super();
      this.mStartValue = paramFloat1;
      this.mDelta = (paramFloat2 - paramFloat1);
    }
    
    protected void applyTransformation(float paramFloat, Transformation paramTransformation)
    {
      BaseCardView.this.mInfoVisFraction = (this.mStartValue + this.mDelta * paramFloat);
      BaseCardView.this.requestLayout();
    }
  }
  
  final class InfoOffsetAnimation
    extends BaseCardView.AnimationBase
  {
    private float mDelta;
    private float mStartValue;
    
    public InfoOffsetAnimation(float paramFloat1, float paramFloat2)
    {
      super();
      this.mStartValue = paramFloat1;
      this.mDelta = (paramFloat2 - paramFloat1);
    }
    
    protected void applyTransformation(float paramFloat, Transformation paramTransformation)
    {
      BaseCardView.this.mInfoOffset = (this.mStartValue + this.mDelta * paramFloat);
      BaseCardView.this.requestLayout();
    }
  }
  
  public static class LayoutParams
    extends FrameLayout.LayoutParams
  {
    public static final int VIEW_TYPE_EXTRA = 2;
    public static final int VIEW_TYPE_INFO = 1;
    public static final int VIEW_TYPE_MAIN = 0;
    @ViewDebug.ExportedProperty(category="layout", mapping={@android.view.ViewDebug.IntToString(from=0, to="MAIN"), @android.view.ViewDebug.IntToString(from=1, to="INFO"), @android.view.ViewDebug.IntToString(from=2, to="EXTRA")})
    public int viewType = 0;
    
    public LayoutParams(int paramInt1, int paramInt2)
    {
      super(paramInt2);
    }
    
    public LayoutParams(Context paramContext, AttributeSet paramAttributeSet)
    {
      super(paramAttributeSet);
      paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.lbBaseCardView_Layout);
      this.viewType = paramContext.getInt(R.styleable.lbBaseCardView_Layout_layout_viewType, 0);
      paramContext.recycle();
    }
    
    public LayoutParams(LayoutParams paramLayoutParams)
    {
      super();
      this.viewType = paramLayoutParams.viewType;
    }
    
    public LayoutParams(ViewGroup.LayoutParams paramLayoutParams)
    {
      super();
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/BaseCardView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */