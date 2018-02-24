package android.support.v17.leanback.widget;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.annotation.ColorInt;
import android.support.v17.leanback.R.attr;
import android.support.v17.leanback.R.color;
import android.support.v17.leanback.R.dimen;
import android.support.v17.leanback.R.id;
import android.support.v17.leanback.R.layout;
import android.support.v17.leanback.graphics.ColorOverlayDimmer;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnLayoutChangeListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

@Deprecated
public class DetailsOverviewRowPresenter
  extends RowPresenter
{
  static final boolean DEBUG = false;
  private static final long DEFAULT_TIMEOUT = 5000L;
  private static final int MORE_ACTIONS_FADE_MS = 100;
  static final String TAG = "DetailsOverviewRowPresenter";
  OnActionClickedListener mActionClickedListener;
  private int mBackgroundColor = 0;
  private boolean mBackgroundColorSet;
  final Presenter mDetailsPresenter;
  private boolean mIsStyleLarge = true;
  private DetailsOverviewSharedElementHelper mSharedElementHelper;
  
  public DetailsOverviewRowPresenter(Presenter paramPresenter)
  {
    setHeaderPresenter(null);
    setSelectEffectEnabled(false);
    this.mDetailsPresenter = paramPresenter;
  }
  
  private int getCardHeight(Context paramContext)
  {
    if (this.mIsStyleLarge) {}
    for (int i = R.dimen.lb_details_overview_height_large;; i = R.dimen.lb_details_overview_height_small) {
      return paramContext.getResources().getDimensionPixelSize(i);
    }
  }
  
  private int getDefaultBackgroundColor(Context paramContext)
  {
    TypedValue localTypedValue = new TypedValue();
    if (paramContext.getTheme().resolveAttribute(R.attr.defaultBrandColor, localTypedValue, true)) {
      return paramContext.getResources().getColor(localTypedValue.resourceId);
    }
    return paramContext.getResources().getColor(R.color.lb_default_brand_color);
  }
  
  private static int getNonNegativeHeight(Drawable paramDrawable)
  {
    if (paramDrawable == null) {}
    for (int i = 0; i > 0; i = paramDrawable.getIntrinsicHeight()) {
      return i;
    }
    return 0;
  }
  
  private static int getNonNegativeWidth(Drawable paramDrawable)
  {
    if (paramDrawable == null) {}
    for (int i = 0; i > 0; i = paramDrawable.getIntrinsicWidth()) {
      return i;
    }
    return 0;
  }
  
  private void initDetailsOverview(final ViewHolder paramViewHolder)
  {
    paramViewHolder.mActionBridgeAdapter = new ActionsItemBridgeAdapter(paramViewHolder);
    FrameLayout localFrameLayout = paramViewHolder.mOverviewFrame;
    ViewGroup.LayoutParams localLayoutParams = localFrameLayout.getLayoutParams();
    localLayoutParams.height = getCardHeight(localFrameLayout.getContext());
    localFrameLayout.setLayoutParams(localLayoutParams);
    if (!getSelectEffectEnabled()) {
      paramViewHolder.mOverviewFrame.setForeground(null);
    }
    paramViewHolder.mActionsRow.setOnUnhandledKeyListener(new BaseGridView.OnUnhandledKeyListener()
    {
      public boolean onUnhandledKey(KeyEvent paramAnonymousKeyEvent)
      {
        return (paramViewHolder.getOnKeyListener() != null) && (paramViewHolder.getOnKeyListener().onKey(paramViewHolder.view, paramAnonymousKeyEvent.getKeyCode(), paramAnonymousKeyEvent));
      }
    });
  }
  
  void bindImageDrawable(ViewHolder paramViewHolder)
  {
    DetailsOverviewRow localDetailsOverviewRow = (DetailsOverviewRow)paramViewHolder.getRow();
    ViewGroup.MarginLayoutParams localMarginLayoutParams = (ViewGroup.MarginLayoutParams)paramViewHolder.mImageView.getLayoutParams();
    int n = getCardHeight(paramViewHolder.mImageView.getContext());
    int i1 = paramViewHolder.mImageView.getResources().getDimensionPixelSize(R.dimen.lb_details_overview_image_margin_vertical);
    int i2 = paramViewHolder.mImageView.getResources().getDimensionPixelSize(R.dimen.lb_details_overview_image_margin_horizontal);
    int i3 = getNonNegativeWidth(localDetailsOverviewRow.getImageDrawable());
    int i4 = getNonNegativeHeight(localDetailsOverviewRow.getImageDrawable());
    boolean bool3 = localDetailsOverviewRow.isImageScaleUpAllowed();
    int k = 0;
    int m = 0;
    boolean bool1 = bool3;
    int j;
    int i;
    boolean bool2;
    if (localDetailsOverviewRow.getImageDrawable() != null)
    {
      j = 0;
      i = m;
      if (i3 > i4)
      {
        k = 1;
        j = k;
        i = m;
        if (this.mIsStyleLarge)
        {
          i = 1;
          j = k;
        }
      }
      if ((j == 0) || (i3 <= n))
      {
        bool2 = bool3;
        if (j == 0)
        {
          bool2 = bool3;
          if (i4 <= n) {}
        }
      }
      else
      {
        bool2 = true;
      }
      if (!bool2) {
        i = 1;
      }
      bool1 = bool2;
      k = i;
      if (i != 0)
      {
        bool1 = bool2;
        k = i;
        if (!bool2)
        {
          if ((j == 0) || (i3 <= n - i2)) {
            break label388;
          }
          bool1 = true;
          k = i;
        }
      }
    }
    if (this.mBackgroundColorSet)
    {
      i = this.mBackgroundColor;
      label237:
      if (k == 0) {
        break label442;
      }
      localMarginLayoutParams.setMarginStart(i2);
      localMarginLayoutParams.bottomMargin = i1;
      localMarginLayoutParams.topMargin = i1;
      paramViewHolder.mOverviewFrame.setBackgroundColor(i);
      paramViewHolder.mRightPanel.setBackground(null);
      paramViewHolder.mImageView.setBackground(null);
      label287:
      RoundedRectHelper.getInstance().setClipToRoundedOutline(paramViewHolder.mOverviewFrame, true);
      if (!bool1) {
        break label487;
      }
      paramViewHolder.mImageView.setScaleType(ImageView.ScaleType.FIT_START);
      paramViewHolder.mImageView.setAdjustViewBounds(true);
      paramViewHolder.mImageView.setMaxWidth(n);
      localMarginLayoutParams.height = -1;
    }
    for (localMarginLayoutParams.width = -2;; localMarginLayoutParams.width = Math.min(n, i3))
    {
      paramViewHolder.mImageView.setLayoutParams(localMarginLayoutParams);
      paramViewHolder.mImageView.setImageDrawable(localDetailsOverviewRow.getImageDrawable());
      if ((localDetailsOverviewRow.getImageDrawable() != null) && (this.mSharedElementHelper != null)) {
        this.mSharedElementHelper.onBindToDrawable(paramViewHolder);
      }
      return;
      label388:
      bool1 = bool2;
      k = i;
      if (j != 0) {
        break;
      }
      bool1 = bool2;
      k = i;
      if (i4 <= n - i1 * 2) {
        break;
      }
      bool1 = true;
      k = i;
      break;
      i = getDefaultBackgroundColor(paramViewHolder.mOverviewView.getContext());
      break label237;
      label442:
      localMarginLayoutParams.bottomMargin = 0;
      localMarginLayoutParams.topMargin = 0;
      localMarginLayoutParams.leftMargin = 0;
      paramViewHolder.mRightPanel.setBackgroundColor(i);
      paramViewHolder.mImageView.setBackgroundColor(i);
      paramViewHolder.mOverviewFrame.setBackground(null);
      break label287;
      label487:
      paramViewHolder.mImageView.setScaleType(ImageView.ScaleType.CENTER);
      paramViewHolder.mImageView.setAdjustViewBounds(false);
      localMarginLayoutParams.height = -2;
    }
  }
  
  protected RowPresenter.ViewHolder createRowViewHolder(ViewGroup paramViewGroup)
  {
    paramViewGroup = new ViewHolder(LayoutInflater.from(paramViewGroup.getContext()).inflate(R.layout.lb_details_overview, paramViewGroup, false), this.mDetailsPresenter);
    initDetailsOverview(paramViewGroup);
    return paramViewGroup;
  }
  
  @ColorInt
  public int getBackgroundColor()
  {
    return this.mBackgroundColor;
  }
  
  public OnActionClickedListener getOnActionClickedListener()
  {
    return this.mActionClickedListener;
  }
  
  public boolean isStyleLarge()
  {
    return this.mIsStyleLarge;
  }
  
  public final boolean isUsingDefaultSelectEffect()
  {
    return false;
  }
  
  protected void onBindRowViewHolder(RowPresenter.ViewHolder paramViewHolder, Object paramObject)
  {
    super.onBindRowViewHolder(paramViewHolder, paramObject);
    paramObject = (DetailsOverviewRow)paramObject;
    paramViewHolder = (ViewHolder)paramViewHolder;
    bindImageDrawable(paramViewHolder);
    this.mDetailsPresenter.onBindViewHolder(paramViewHolder.mDetailsDescriptionViewHolder, ((DetailsOverviewRow)paramObject).getItem());
    paramViewHolder.bindActions(((DetailsOverviewRow)paramObject).getActionsAdapter());
    ((DetailsOverviewRow)paramObject).addListener(paramViewHolder.mListener);
  }
  
  protected void onRowViewAttachedToWindow(RowPresenter.ViewHolder paramViewHolder)
  {
    super.onRowViewAttachedToWindow(paramViewHolder);
    if (this.mDetailsPresenter != null) {
      this.mDetailsPresenter.onViewAttachedToWindow(((ViewHolder)paramViewHolder).mDetailsDescriptionViewHolder);
    }
  }
  
  protected void onRowViewDetachedFromWindow(RowPresenter.ViewHolder paramViewHolder)
  {
    super.onRowViewDetachedFromWindow(paramViewHolder);
    if (this.mDetailsPresenter != null) {
      this.mDetailsPresenter.onViewDetachedFromWindow(((ViewHolder)paramViewHolder).mDetailsDescriptionViewHolder);
    }
  }
  
  protected void onRowViewSelected(RowPresenter.ViewHolder paramViewHolder, boolean paramBoolean)
  {
    super.onRowViewSelected(paramViewHolder, paramBoolean);
    if (paramBoolean) {
      ((ViewHolder)paramViewHolder).dispatchItemSelection(null);
    }
  }
  
  protected void onSelectLevelChanged(RowPresenter.ViewHolder paramViewHolder)
  {
    super.onSelectLevelChanged(paramViewHolder);
    if (getSelectEffectEnabled())
    {
      paramViewHolder = (ViewHolder)paramViewHolder;
      int i = paramViewHolder.mColorDimmer.getPaint().getColor();
      ((ColorDrawable)paramViewHolder.mOverviewFrame.getForeground().mutate()).setColor(i);
    }
  }
  
  protected void onUnbindRowViewHolder(RowPresenter.ViewHolder paramViewHolder)
  {
    ViewHolder localViewHolder = (ViewHolder)paramViewHolder;
    ((DetailsOverviewRow)localViewHolder.getRow()).removeListener(localViewHolder.mListener);
    if (localViewHolder.mDetailsDescriptionViewHolder != null) {
      this.mDetailsPresenter.onUnbindViewHolder(localViewHolder.mDetailsDescriptionViewHolder);
    }
    super.onUnbindRowViewHolder(paramViewHolder);
  }
  
  public void setBackgroundColor(@ColorInt int paramInt)
  {
    this.mBackgroundColor = paramInt;
    this.mBackgroundColorSet = true;
  }
  
  public void setOnActionClickedListener(OnActionClickedListener paramOnActionClickedListener)
  {
    this.mActionClickedListener = paramOnActionClickedListener;
  }
  
  public final void setSharedElementEnterTransition(Activity paramActivity, String paramString)
  {
    setSharedElementEnterTransition(paramActivity, paramString, 5000L);
  }
  
  public final void setSharedElementEnterTransition(Activity paramActivity, String paramString, long paramLong)
  {
    if (this.mSharedElementHelper == null) {
      this.mSharedElementHelper = new DetailsOverviewSharedElementHelper();
    }
    this.mSharedElementHelper.setSharedElementEnterTransition(paramActivity, paramString, paramLong);
  }
  
  public void setStyleLarge(boolean paramBoolean)
  {
    this.mIsStyleLarge = paramBoolean;
  }
  
  class ActionsItemBridgeAdapter
    extends ItemBridgeAdapter
  {
    DetailsOverviewRowPresenter.ViewHolder mViewHolder;
    
    ActionsItemBridgeAdapter(DetailsOverviewRowPresenter.ViewHolder paramViewHolder)
    {
      this.mViewHolder = paramViewHolder;
    }
    
    public void onAttachedToWindow(ItemBridgeAdapter.ViewHolder paramViewHolder)
    {
      paramViewHolder.itemView.removeOnLayoutChangeListener(this.mViewHolder.mLayoutChangeListener);
      paramViewHolder.itemView.addOnLayoutChangeListener(this.mViewHolder.mLayoutChangeListener);
    }
    
    public void onBind(final ItemBridgeAdapter.ViewHolder paramViewHolder)
    {
      if ((this.mViewHolder.getOnItemViewClickedListener() != null) || (DetailsOverviewRowPresenter.this.mActionClickedListener != null)) {
        paramViewHolder.getPresenter().setOnClickListener(paramViewHolder.getViewHolder(), new View.OnClickListener()
        {
          public void onClick(View paramAnonymousView)
          {
            if (DetailsOverviewRowPresenter.ActionsItemBridgeAdapter.this.mViewHolder.getOnItemViewClickedListener() != null) {
              DetailsOverviewRowPresenter.ActionsItemBridgeAdapter.this.mViewHolder.getOnItemViewClickedListener().onItemClicked(paramViewHolder.getViewHolder(), paramViewHolder.getItem(), DetailsOverviewRowPresenter.ActionsItemBridgeAdapter.this.mViewHolder, DetailsOverviewRowPresenter.ActionsItemBridgeAdapter.this.mViewHolder.getRow());
            }
            if (DetailsOverviewRowPresenter.this.mActionClickedListener != null) {
              DetailsOverviewRowPresenter.this.mActionClickedListener.onActionClicked((Action)paramViewHolder.getItem());
            }
          }
        });
      }
    }
    
    public void onDetachedFromWindow(ItemBridgeAdapter.ViewHolder paramViewHolder)
    {
      paramViewHolder.itemView.removeOnLayoutChangeListener(this.mViewHolder.mLayoutChangeListener);
      this.mViewHolder.checkFirstAndLastPosition(false);
    }
    
    public void onUnbind(ItemBridgeAdapter.ViewHolder paramViewHolder)
    {
      if ((this.mViewHolder.getOnItemViewClickedListener() != null) || (DetailsOverviewRowPresenter.this.mActionClickedListener != null)) {
        paramViewHolder.getPresenter().setOnClickListener(paramViewHolder.getViewHolder(), null);
      }
    }
  }
  
  public final class ViewHolder
    extends RowPresenter.ViewHolder
  {
    ItemBridgeAdapter mActionBridgeAdapter;
    final HorizontalGridView mActionsRow;
    final OnChildSelectedListener mChildSelectedListener = new OnChildSelectedListener()
    {
      public void onChildSelected(ViewGroup paramAnonymousViewGroup, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
      {
        DetailsOverviewRowPresenter.ViewHolder.this.dispatchItemSelection(paramAnonymousView);
      }
    };
    final FrameLayout mDetailsDescriptionFrame;
    public final Presenter.ViewHolder mDetailsDescriptionViewHolder;
    final Handler mHandler = new Handler();
    final ImageView mImageView;
    final View.OnLayoutChangeListener mLayoutChangeListener = new View.OnLayoutChangeListener()
    {
      public void onLayoutChange(View paramAnonymousView, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3, int paramAnonymousInt4, int paramAnonymousInt5, int paramAnonymousInt6, int paramAnonymousInt7, int paramAnonymousInt8)
      {
        DetailsOverviewRowPresenter.ViewHolder.this.checkFirstAndLastPosition(false);
      }
    };
    final DetailsOverviewRow.Listener mListener = new DetailsOverviewRow.Listener()
    {
      public void onActionsAdapterChanged(DetailsOverviewRow paramAnonymousDetailsOverviewRow)
      {
        DetailsOverviewRowPresenter.ViewHolder.this.bindActions(paramAnonymousDetailsOverviewRow.getActionsAdapter());
      }
      
      public void onImageDrawableChanged(DetailsOverviewRow paramAnonymousDetailsOverviewRow)
      {
        DetailsOverviewRowPresenter.ViewHolder.this.mHandler.removeCallbacks(DetailsOverviewRowPresenter.ViewHolder.this.mUpdateDrawableCallback);
        DetailsOverviewRowPresenter.ViewHolder.this.mHandler.post(DetailsOverviewRowPresenter.ViewHolder.this.mUpdateDrawableCallback);
      }
      
      public void onItemChanged(DetailsOverviewRow paramAnonymousDetailsOverviewRow)
      {
        if (DetailsOverviewRowPresenter.ViewHolder.this.mDetailsDescriptionViewHolder != null) {
          DetailsOverviewRowPresenter.this.mDetailsPresenter.onUnbindViewHolder(DetailsOverviewRowPresenter.ViewHolder.this.mDetailsDescriptionViewHolder);
        }
        DetailsOverviewRowPresenter.this.mDetailsPresenter.onBindViewHolder(DetailsOverviewRowPresenter.ViewHolder.this.mDetailsDescriptionViewHolder, paramAnonymousDetailsOverviewRow.getItem());
      }
    };
    int mNumItems;
    final FrameLayout mOverviewFrame;
    final ViewGroup mOverviewView;
    final ViewGroup mRightPanel;
    final RecyclerView.OnScrollListener mScrollListener = new RecyclerView.OnScrollListener()
    {
      public void onScrollStateChanged(RecyclerView paramAnonymousRecyclerView, int paramAnonymousInt) {}
      
      public void onScrolled(RecyclerView paramAnonymousRecyclerView, int paramAnonymousInt1, int paramAnonymousInt2)
      {
        DetailsOverviewRowPresenter.ViewHolder.this.checkFirstAndLastPosition(true);
      }
    };
    boolean mShowMoreLeft;
    boolean mShowMoreRight;
    final Runnable mUpdateDrawableCallback = new Runnable()
    {
      public void run()
      {
        DetailsOverviewRowPresenter.this.bindImageDrawable(DetailsOverviewRowPresenter.ViewHolder.this);
      }
    };
    
    public ViewHolder(View paramView, Presenter paramPresenter)
    {
      super();
      this.mOverviewFrame = ((FrameLayout)paramView.findViewById(R.id.details_frame));
      this.mOverviewView = ((ViewGroup)paramView.findViewById(R.id.details_overview));
      this.mImageView = ((ImageView)paramView.findViewById(R.id.details_overview_image));
      this.mRightPanel = ((ViewGroup)paramView.findViewById(R.id.details_overview_right_panel));
      this.mDetailsDescriptionFrame = ((FrameLayout)this.mRightPanel.findViewById(R.id.details_overview_description));
      this.mActionsRow = ((HorizontalGridView)this.mRightPanel.findViewById(R.id.details_overview_actions));
      this.mActionsRow.setHasOverlappingRendering(false);
      this.mActionsRow.setOnScrollListener(this.mScrollListener);
      this.mActionsRow.setAdapter(this.mActionBridgeAdapter);
      this.mActionsRow.setOnChildSelectedListener(this.mChildSelectedListener);
      int i = paramView.getResources().getDimensionPixelSize(R.dimen.lb_details_overview_actions_fade_size);
      this.mActionsRow.setFadingRightEdgeLength(i);
      this.mActionsRow.setFadingLeftEdgeLength(i);
      this.mDetailsDescriptionViewHolder = paramPresenter.onCreateViewHolder(this.mDetailsDescriptionFrame);
      this.mDetailsDescriptionFrame.addView(this.mDetailsDescriptionViewHolder.view);
    }
    
    private int getViewCenter(View paramView)
    {
      return (paramView.getRight() - paramView.getLeft()) / 2;
    }
    
    private void showMoreLeft(boolean paramBoolean)
    {
      if (paramBoolean != this.mShowMoreLeft)
      {
        this.mActionsRow.setFadingLeftEdge(paramBoolean);
        this.mShowMoreLeft = paramBoolean;
      }
    }
    
    private void showMoreRight(boolean paramBoolean)
    {
      if (paramBoolean != this.mShowMoreRight)
      {
        this.mActionsRow.setFadingRightEdge(paramBoolean);
        this.mShowMoreRight = paramBoolean;
      }
    }
    
    void bindActions(ObjectAdapter paramObjectAdapter)
    {
      this.mActionBridgeAdapter.setAdapter(paramObjectAdapter);
      this.mActionsRow.setAdapter(this.mActionBridgeAdapter);
      this.mNumItems = this.mActionBridgeAdapter.getItemCount();
      this.mShowMoreRight = false;
      this.mShowMoreLeft = true;
      showMoreLeft(false);
    }
    
    void checkFirstAndLastPosition(boolean paramBoolean)
    {
      RecyclerView.ViewHolder localViewHolder = this.mActionsRow.findViewHolderForPosition(this.mNumItems - 1);
      if ((localViewHolder == null) || (localViewHolder.itemView.getRight() > this.mActionsRow.getWidth()))
      {
        paramBoolean = true;
        localViewHolder = this.mActionsRow.findViewHolderForPosition(0);
        if ((localViewHolder != null) && (localViewHolder.itemView.getLeft() >= 0)) {
          break label78;
        }
      }
      label78:
      for (boolean bool = true;; bool = false)
      {
        showMoreRight(paramBoolean);
        showMoreLeft(bool);
        return;
        paramBoolean = false;
        break;
      }
    }
    
    void dispatchItemSelection(View paramView)
    {
      if (!isSelected()) {}
      label75:
      do
      {
        return;
        if (paramView != null) {}
        for (paramView = this.mActionsRow.getChildViewHolder(paramView);; paramView = this.mActionsRow.findViewHolderForPosition(this.mActionsRow.getSelectedPosition()))
        {
          paramView = (ItemBridgeAdapter.ViewHolder)paramView;
          if (paramView != null) {
            break label75;
          }
          if (getOnItemViewSelectedListener() == null) {
            break;
          }
          getOnItemViewSelectedListener().onItemSelected(null, null, this, getRow());
          return;
        }
      } while (getOnItemViewSelectedListener() == null);
      getOnItemViewSelectedListener().onItemSelected(paramView.getViewHolder(), paramView.getItem(), this, getRow());
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/DetailsOverviewRowPresenter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */