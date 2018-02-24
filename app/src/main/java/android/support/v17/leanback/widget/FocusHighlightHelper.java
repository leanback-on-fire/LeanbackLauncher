package android.support.v17.leanback.widget;

import android.animation.TimeAnimator;
import android.animation.TimeAnimator.TimeListener;
import android.content.res.Resources;
import android.graphics.Paint;
import android.support.v17.leanback.R.dimen;
import android.support.v17.leanback.R.fraction;
import android.support.v17.leanback.R.id;
import android.support.v17.leanback.graphics.ColorOverlayDimmer;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewParent;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

public class FocusHighlightHelper
{
  static int getResId(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return 0;
    case 1: 
      return R.fraction.lb_focus_zoom_factor_small;
    case 4: 
      return R.fraction.lb_focus_zoom_factor_xsmall;
    case 2: 
      return R.fraction.lb_focus_zoom_factor_medium;
    }
    return R.fraction.lb_focus_zoom_factor_large;
  }
  
  static boolean isValidZoomIndex(int paramInt)
  {
    return (paramInt == 0) || (getResId(paramInt) > 0);
  }
  
  public static void setupBrowseItemFocusHighlight(ItemBridgeAdapter paramItemBridgeAdapter, int paramInt, boolean paramBoolean)
  {
    paramItemBridgeAdapter.setFocusHighlight(new BrowseItemFocusHighlight(paramInt, paramBoolean));
  }
  
  public static void setupHeaderItemFocusHighlight(ItemBridgeAdapter paramItemBridgeAdapter)
  {
    setupHeaderItemFocusHighlight(paramItemBridgeAdapter, true);
  }
  
  public static void setupHeaderItemFocusHighlight(ItemBridgeAdapter paramItemBridgeAdapter, boolean paramBoolean)
  {
    paramItemBridgeAdapter.setFocusHighlight(new HeaderItemFocusHighlight(paramBoolean));
  }
  
  @Deprecated
  public static void setupHeaderItemFocusHighlight(VerticalGridView paramVerticalGridView)
  {
    setupHeaderItemFocusHighlight(paramVerticalGridView, true);
  }
  
  @Deprecated
  public static void setupHeaderItemFocusHighlight(VerticalGridView paramVerticalGridView, boolean paramBoolean)
  {
    if ((paramVerticalGridView != null) && ((paramVerticalGridView.getAdapter() instanceof ItemBridgeAdapter))) {
      ((ItemBridgeAdapter)paramVerticalGridView.getAdapter()).setFocusHighlight(new HeaderItemFocusHighlight(paramBoolean));
    }
  }
  
  static class BrowseItemFocusHighlight
    implements FocusHighlightHandler
  {
    private static final int DURATION_MS = 150;
    private int mScaleIndex;
    private final boolean mUseDimmer;
    
    BrowseItemFocusHighlight(int paramInt, boolean paramBoolean)
    {
      if (!FocusHighlightHelper.isValidZoomIndex(paramInt)) {
        throw new IllegalArgumentException("Unhandled zoom index");
      }
      this.mScaleIndex = paramInt;
      this.mUseDimmer = paramBoolean;
    }
    
    private FocusHighlightHelper.FocusAnimator getOrCreateAnimator(View paramView)
    {
      FocusHighlightHelper.FocusAnimator localFocusAnimator2 = (FocusHighlightHelper.FocusAnimator)paramView.getTag(R.id.lb_focus_animator);
      FocusHighlightHelper.FocusAnimator localFocusAnimator1 = localFocusAnimator2;
      if (localFocusAnimator2 == null)
      {
        localFocusAnimator1 = new FocusHighlightHelper.FocusAnimator(paramView, getScale(paramView.getResources()), this.mUseDimmer, 150);
        paramView.setTag(R.id.lb_focus_animator, localFocusAnimator1);
      }
      return localFocusAnimator1;
    }
    
    private float getScale(Resources paramResources)
    {
      if (this.mScaleIndex == 0) {
        return 1.0F;
      }
      return paramResources.getFraction(FocusHighlightHelper.getResId(this.mScaleIndex), 1, 1);
    }
    
    public void onInitializeView(View paramView)
    {
      getOrCreateAnimator(paramView).animateFocus(false, true);
    }
    
    public void onItemFocused(View paramView, boolean paramBoolean)
    {
      paramView.setSelected(paramBoolean);
      getOrCreateAnimator(paramView).animateFocus(paramBoolean, false);
    }
  }
  
  static class FocusAnimator
    implements TimeAnimator.TimeListener
  {
    private final TimeAnimator mAnimator = new TimeAnimator();
    private final ColorOverlayDimmer mDimmer;
    private final int mDuration;
    private float mFocusLevel = 0.0F;
    private float mFocusLevelDelta;
    private float mFocusLevelStart;
    private final Interpolator mInterpolator = new AccelerateDecelerateInterpolator();
    private final float mScaleDiff;
    private final View mView;
    private final ShadowOverlayContainer mWrapper;
    
    FocusAnimator(View paramView, float paramFloat, boolean paramBoolean, int paramInt)
    {
      this.mView = paramView;
      this.mDuration = paramInt;
      this.mScaleDiff = (paramFloat - 1.0F);
      if ((paramView instanceof ShadowOverlayContainer)) {}
      for (this.mWrapper = ((ShadowOverlayContainer)paramView);; this.mWrapper = null)
      {
        this.mAnimator.setTimeListener(this);
        if (!paramBoolean) {
          break;
        }
        this.mDimmer = ColorOverlayDimmer.createDefault(paramView.getContext());
        return;
      }
      this.mDimmer = null;
    }
    
    void animateFocus(boolean paramBoolean1, boolean paramBoolean2)
    {
      endAnimation();
      float f;
      if (paramBoolean1)
      {
        f = 1.0F;
        if (!paramBoolean2) {
          break label25;
        }
        setFocusLevel(f);
      }
      label25:
      while (this.mFocusLevel == f)
      {
        return;
        f = 0.0F;
        break;
      }
      this.mFocusLevelStart = this.mFocusLevel;
      this.mFocusLevelDelta = (f - this.mFocusLevelStart);
      this.mAnimator.start();
    }
    
    void endAnimation()
    {
      this.mAnimator.end();
    }
    
    float getFocusLevel()
    {
      return this.mFocusLevel;
    }
    
    public void onTimeUpdate(TimeAnimator paramTimeAnimator, long paramLong1, long paramLong2)
    {
      float f1;
      if (paramLong1 >= this.mDuration)
      {
        f1 = 1.0F;
        this.mAnimator.end();
      }
      for (;;)
      {
        float f2 = f1;
        if (this.mInterpolator != null) {
          f2 = this.mInterpolator.getInterpolation(f1);
        }
        setFocusLevel(this.mFocusLevelStart + this.mFocusLevelDelta * f2);
        return;
        f1 = (float)(paramLong1 / this.mDuration);
      }
    }
    
    void setFocusLevel(float paramFloat)
    {
      this.mFocusLevel = paramFloat;
      float f = 1.0F + this.mScaleDiff * paramFloat;
      this.mView.setScaleX(f);
      this.mView.setScaleY(f);
      if (this.mWrapper != null) {
        this.mWrapper.setShadowFocusLevel(paramFloat);
      }
      int i;
      for (;;)
      {
        if (this.mDimmer != null)
        {
          this.mDimmer.setActiveLevel(paramFloat);
          i = this.mDimmer.getPaint().getColor();
          if (this.mWrapper == null) {
            break;
          }
          this.mWrapper.setOverlayColor(i);
        }
        return;
        ShadowOverlayHelper.setNoneWrapperShadowFocusLevel(this.mView, paramFloat);
      }
      ShadowOverlayHelper.setNoneWrapperOverlayColor(this.mView, i);
    }
  }
  
  static class HeaderItemFocusHighlight
    implements FocusHighlightHandler
  {
    private int mDuration;
    private boolean mInitialized;
    boolean mScaleEnabled;
    private float mSelectScale;
    
    HeaderItemFocusHighlight(boolean paramBoolean)
    {
      this.mScaleEnabled = paramBoolean;
    }
    
    private void viewFocused(View paramView, boolean paramBoolean)
    {
      lazyInit(paramView);
      paramView.setSelected(paramBoolean);
      FocusHighlightHelper.FocusAnimator localFocusAnimator = (FocusHighlightHelper.FocusAnimator)paramView.getTag(R.id.lb_focus_animator);
      Object localObject = localFocusAnimator;
      if (localFocusAnimator == null)
      {
        localObject = new HeaderFocusAnimator(paramView, this.mSelectScale, this.mDuration);
        paramView.setTag(R.id.lb_focus_animator, localObject);
      }
      ((FocusHighlightHelper.FocusAnimator)localObject).animateFocus(paramBoolean, false);
    }
    
    void lazyInit(View paramView)
    {
      if (!this.mInitialized)
      {
        paramView = paramView.getResources();
        if (!this.mScaleEnabled) {
          break label55;
        }
      }
      label55:
      for (float f = Float.parseFloat(paramView.getString(R.dimen.lb_browse_header_select_scale));; f = 1.0F)
      {
        this.mSelectScale = f;
        this.mDuration = Integer.parseInt(paramView.getString(R.dimen.lb_browse_header_select_duration));
        this.mInitialized = true;
        return;
      }
    }
    
    public void onInitializeView(View paramView) {}
    
    public void onItemFocused(View paramView, boolean paramBoolean)
    {
      viewFocused(paramView, paramBoolean);
    }
    
    static class HeaderFocusAnimator
      extends FocusHighlightHelper.FocusAnimator
    {
      ItemBridgeAdapter.ViewHolder mViewHolder;
      
      HeaderFocusAnimator(View paramView, float paramFloat, int paramInt)
      {
        super(paramFloat, false, paramInt);
        for (ViewParent localViewParent = paramView.getParent();; localViewParent = localViewParent.getParent()) {
          if ((localViewParent == null) || ((localViewParent instanceof RecyclerView)))
          {
            if (localViewParent != null) {
              this.mViewHolder = ((ItemBridgeAdapter.ViewHolder)((RecyclerView)localViewParent).getChildViewHolder(paramView));
            }
            return;
          }
        }
      }
      
      void setFocusLevel(float paramFloat)
      {
        Presenter localPresenter = this.mViewHolder.getPresenter();
        if ((localPresenter instanceof RowHeaderPresenter)) {
          ((RowHeaderPresenter)localPresenter).setSelectLevel((RowHeaderPresenter.ViewHolder)this.mViewHolder.getViewHolder(), paramFloat);
        }
        super.setFocusLevel(paramFloat);
      }
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/FocusHighlightHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */