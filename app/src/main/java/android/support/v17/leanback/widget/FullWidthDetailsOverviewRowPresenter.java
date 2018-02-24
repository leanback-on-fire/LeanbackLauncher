package android.support.v17.leanback.widget;

import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v17.leanback.R.dimen;
import android.support.v17.leanback.R.id;
import android.support.v17.leanback.R.layout;
import android.support.v17.leanback.graphics.ColorOverlayDimmer;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.support.v7.widget.RecyclerView.ViewHolder;
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

public class FullWidthDetailsOverviewRowPresenter
  extends RowPresenter
{
  public static final int ALIGN_MODE_MIDDLE = 1;
  public static final int ALIGN_MODE_START = 0;
  static final boolean DEBUG = false;
  public static final int STATE_FULL = 1;
  public static final int STATE_HALF = 0;
  public static final int STATE_SMALL = 2;
  static final String TAG = "FullWidthDetailsOverviewRowPresenter";
  static final Handler sHandler = new Handler();
  private static Rect sTmpRect = new Rect();
  OnActionClickedListener mActionClickedListener;
  private int mActionsBackgroundColor = 0;
  private boolean mActionsBackgroundColorSet;
  private int mAlignmentMode;
  private int mBackgroundColor = 0;
  private boolean mBackgroundColorSet;
  final DetailsOverviewLogoPresenter mDetailsOverviewLogoPresenter;
  final Presenter mDetailsPresenter;
  protected int mInitialState = 0;
  private Listener mListener;
  private boolean mParticipatingEntranceTransition;
  
  public FullWidthDetailsOverviewRowPresenter(Presenter paramPresenter)
  {
    this(paramPresenter, new DetailsOverviewLogoPresenter());
  }
  
  public FullWidthDetailsOverviewRowPresenter(Presenter paramPresenter, DetailsOverviewLogoPresenter paramDetailsOverviewLogoPresenter)
  {
    setHeaderPresenter(null);
    setSelectEffectEnabled(false);
    this.mDetailsPresenter = paramPresenter;
    this.mDetailsOverviewLogoPresenter = paramDetailsOverviewLogoPresenter;
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
  
  protected RowPresenter.ViewHolder createRowViewHolder(final ViewGroup paramViewGroup)
  {
    paramViewGroup = new ViewHolder(LayoutInflater.from(paramViewGroup.getContext()).inflate(getLayoutResourceId(), paramViewGroup, false), this.mDetailsPresenter, this.mDetailsOverviewLogoPresenter);
    this.mDetailsOverviewLogoPresenter.setContext(paramViewGroup.mDetailsLogoViewHolder, paramViewGroup, this);
    setState(paramViewGroup, this.mInitialState);
    paramViewGroup.mActionBridgeAdapter = new ActionsItemBridgeAdapter(paramViewGroup);
    FrameLayout localFrameLayout = paramViewGroup.mOverviewFrame;
    if (this.mBackgroundColorSet) {
      localFrameLayout.setBackgroundColor(this.mBackgroundColor);
    }
    if (this.mActionsBackgroundColorSet) {
      localFrameLayout.findViewById(R.id.details_overview_actions_background).setBackgroundColor(this.mActionsBackgroundColor);
    }
    RoundedRectHelper.getInstance().setClipToRoundedOutline(localFrameLayout, true);
    if (!getSelectEffectEnabled()) {
      paramViewGroup.mOverviewFrame.setForeground(null);
    }
    paramViewGroup.mActionsRow.setOnUnhandledKeyListener(new BaseGridView.OnUnhandledKeyListener()
    {
      public boolean onUnhandledKey(KeyEvent paramAnonymousKeyEvent)
      {
        return (paramViewGroup.getOnKeyListener() != null) && (paramViewGroup.getOnKeyListener().onKey(paramViewGroup.view, paramAnonymousKeyEvent.getKeyCode(), paramAnonymousKeyEvent));
      }
    });
    return paramViewGroup;
  }
  
  public final int getActionsBackgroundColor()
  {
    return this.mActionsBackgroundColor;
  }
  
  public final int getAlignmentMode()
  {
    return this.mAlignmentMode;
  }
  
  public final int getBackgroundColor()
  {
    return this.mBackgroundColor;
  }
  
  public final int getInitialState()
  {
    return this.mInitialState;
  }
  
  protected int getLayoutResourceId()
  {
    return R.layout.lb_fullwidth_details_overview;
  }
  
  public OnActionClickedListener getOnActionClickedListener()
  {
    return this.mActionClickedListener;
  }
  
  protected boolean isClippingChildren()
  {
    return true;
  }
  
  public final boolean isParticipatingEntranceTransition()
  {
    return this.mParticipatingEntranceTransition;
  }
  
  public final boolean isUsingDefaultSelectEffect()
  {
    return false;
  }
  
  public final void notifyOnBindLogo(ViewHolder paramViewHolder)
  {
    onLayoutOverviewFrame(paramViewHolder, paramViewHolder.getState(), true);
    onLayoutLogo(paramViewHolder, paramViewHolder.getState(), true);
    if (this.mListener != null) {
      this.mListener.onBindLogo(paramViewHolder);
    }
  }
  
  protected void onBindRowViewHolder(RowPresenter.ViewHolder paramViewHolder, Object paramObject)
  {
    super.onBindRowViewHolder(paramViewHolder, paramObject);
    paramObject = (DetailsOverviewRow)paramObject;
    paramViewHolder = (ViewHolder)paramViewHolder;
    this.mDetailsOverviewLogoPresenter.onBindViewHolder(paramViewHolder.mDetailsLogoViewHolder, paramObject);
    this.mDetailsPresenter.onBindViewHolder(paramViewHolder.mDetailsDescriptionViewHolder, ((DetailsOverviewRow)paramObject).getItem());
    paramViewHolder.onBind();
  }
  
  protected void onLayoutLogo(ViewHolder paramViewHolder, int paramInt, boolean paramBoolean)
  {
    View localView = paramViewHolder.getLogoViewHolder().view;
    ViewGroup.MarginLayoutParams localMarginLayoutParams = (ViewGroup.MarginLayoutParams)localView.getLayoutParams();
    switch (this.mAlignmentMode)
    {
    default: 
      localMarginLayoutParams.setMarginStart(localView.getResources().getDimensionPixelSize(R.dimen.lb_details_v2_logo_margin_start));
      switch (paramViewHolder.getState())
      {
      case 1: 
      default: 
        localMarginLayoutParams.topMargin = (localView.getResources().getDimensionPixelSize(R.dimen.lb_details_v2_blank_height) - localMarginLayoutParams.height / 2);
      }
      break;
    }
    for (;;)
    {
      localView.setLayoutParams(localMarginLayoutParams);
      return;
      localMarginLayoutParams.setMarginStart(localView.getResources().getDimensionPixelSize(R.dimen.lb_details_v2_left) - localMarginLayoutParams.width);
      break;
      localMarginLayoutParams.topMargin = (localView.getResources().getDimensionPixelSize(R.dimen.lb_details_v2_blank_height) + localView.getResources().getDimensionPixelSize(R.dimen.lb_details_v2_actions_height) + localView.getResources().getDimensionPixelSize(R.dimen.lb_details_v2_description_margin_top));
      continue;
      localMarginLayoutParams.topMargin = 0;
    }
  }
  
  protected void onLayoutOverviewFrame(ViewHolder paramViewHolder, int paramInt, boolean paramBoolean)
  {
    int j;
    label18:
    Resources localResources;
    int i;
    label111:
    Object localObject;
    int k;
    if (paramInt == 2)
    {
      paramInt = 1;
      if (paramViewHolder.getState() != 2) {
        break label235;
      }
      j = 1;
      if ((paramInt != j) || (paramBoolean))
      {
        localResources = paramViewHolder.view.getResources();
        paramInt = 0;
        if (this.mDetailsOverviewLogoPresenter.isBoundToImage(paramViewHolder.getLogoViewHolder(), (DetailsOverviewRow)paramViewHolder.getRow())) {
          paramInt = paramViewHolder.getLogoViewHolder().view.getLayoutParams().width;
        }
        switch (this.mAlignmentMode)
        {
        default: 
          if (j != 0)
          {
            i = localResources.getDimensionPixelSize(R.dimen.lb_details_v2_logo_margin_start);
            localObject = (ViewGroup.MarginLayoutParams)paramViewHolder.getOverviewView().getLayoutParams();
            if (j == 0) {
              break label293;
            }
            k = 0;
            label131:
            ((ViewGroup.MarginLayoutParams)localObject).topMargin = k;
            ((ViewGroup.MarginLayoutParams)localObject).rightMargin = i;
            ((ViewGroup.MarginLayoutParams)localObject).leftMargin = i;
            paramViewHolder.getOverviewView().setLayoutParams((ViewGroup.LayoutParams)localObject);
            localObject = paramViewHolder.getDetailsDescriptionFrame();
            ViewGroup.MarginLayoutParams localMarginLayoutParams = (ViewGroup.MarginLayoutParams)((View)localObject).getLayoutParams();
            localMarginLayoutParams.setMarginStart(paramInt);
            ((View)localObject).setLayoutParams(localMarginLayoutParams);
            paramViewHolder = paramViewHolder.getActionsRow();
            localObject = (ViewGroup.MarginLayoutParams)paramViewHolder.getLayoutParams();
            ((ViewGroup.MarginLayoutParams)localObject).setMarginStart(paramInt);
            if (j == 0) {
              break label306;
            }
          }
          break;
        }
      }
    }
    label235:
    label293:
    label306:
    for (paramInt = 0;; paramInt = localResources.getDimensionPixelSize(R.dimen.lb_details_v2_actions_height))
    {
      ((ViewGroup.MarginLayoutParams)localObject).height = paramInt;
      paramViewHolder.setLayoutParams((ViewGroup.LayoutParams)localObject);
      return;
      paramInt = 0;
      break;
      j = 0;
      break label18;
      i = 0;
      paramInt += localResources.getDimensionPixelSize(R.dimen.lb_details_v2_logo_margin_start);
      break label111;
      if (j != 0)
      {
        i = localResources.getDimensionPixelSize(R.dimen.lb_details_v2_left) - paramInt;
        break label111;
      }
      i = 0;
      paramInt = localResources.getDimensionPixelSize(R.dimen.lb_details_v2_left);
      break label111;
      k = localResources.getDimensionPixelSize(R.dimen.lb_details_v2_blank_height);
      break label131;
    }
  }
  
  protected void onRowViewAttachedToWindow(RowPresenter.ViewHolder paramViewHolder)
  {
    super.onRowViewAttachedToWindow(paramViewHolder);
    paramViewHolder = (ViewHolder)paramViewHolder;
    this.mDetailsPresenter.onViewAttachedToWindow(paramViewHolder.mDetailsDescriptionViewHolder);
    this.mDetailsOverviewLogoPresenter.onViewAttachedToWindow(paramViewHolder.mDetailsLogoViewHolder);
  }
  
  protected void onRowViewDetachedFromWindow(RowPresenter.ViewHolder paramViewHolder)
  {
    super.onRowViewDetachedFromWindow(paramViewHolder);
    paramViewHolder = (ViewHolder)paramViewHolder;
    this.mDetailsPresenter.onViewDetachedFromWindow(paramViewHolder.mDetailsDescriptionViewHolder);
    this.mDetailsOverviewLogoPresenter.onViewDetachedFromWindow(paramViewHolder.mDetailsLogoViewHolder);
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
  
  protected void onStateChanged(ViewHolder paramViewHolder, int paramInt)
  {
    onLayoutOverviewFrame(paramViewHolder, paramInt, false);
    onLayoutLogo(paramViewHolder, paramInt, false);
  }
  
  protected void onUnbindRowViewHolder(RowPresenter.ViewHolder paramViewHolder)
  {
    ViewHolder localViewHolder = (ViewHolder)paramViewHolder;
    localViewHolder.onUnbind();
    this.mDetailsPresenter.onUnbindViewHolder(localViewHolder.mDetailsDescriptionViewHolder);
    this.mDetailsOverviewLogoPresenter.onUnbindViewHolder(localViewHolder.mDetailsLogoViewHolder);
    super.onUnbindRowViewHolder(paramViewHolder);
  }
  
  public final void setActionsBackgroundColor(int paramInt)
  {
    this.mActionsBackgroundColor = paramInt;
    this.mActionsBackgroundColorSet = true;
  }
  
  public final void setAlignmentMode(int paramInt)
  {
    this.mAlignmentMode = paramInt;
  }
  
  public final void setBackgroundColor(int paramInt)
  {
    this.mBackgroundColor = paramInt;
    this.mBackgroundColorSet = true;
  }
  
  public void setEntranceTransitionState(RowPresenter.ViewHolder paramViewHolder, boolean paramBoolean)
  {
    super.setEntranceTransitionState(paramViewHolder, paramBoolean);
    if (this.mParticipatingEntranceTransition)
    {
      paramViewHolder = paramViewHolder.view;
      if (!paramBoolean) {
        break label30;
      }
    }
    label30:
    for (int i = 0;; i = 4)
    {
      paramViewHolder.setVisibility(i);
      return;
    }
  }
  
  public final void setInitialState(int paramInt)
  {
    this.mInitialState = paramInt;
  }
  
  public final void setListener(Listener paramListener)
  {
    this.mListener = paramListener;
  }
  
  public void setOnActionClickedListener(OnActionClickedListener paramOnActionClickedListener)
  {
    this.mActionClickedListener = paramOnActionClickedListener;
  }
  
  public final void setParticipatingEntranceTransition(boolean paramBoolean)
  {
    this.mParticipatingEntranceTransition = paramBoolean;
  }
  
  public final void setState(ViewHolder paramViewHolder, int paramInt)
  {
    if (paramViewHolder.getState() != paramInt)
    {
      int i = paramViewHolder.getState();
      paramViewHolder.mState = paramInt;
      onStateChanged(paramViewHolder, i);
    }
  }
  
  class ActionsItemBridgeAdapter
    extends ItemBridgeAdapter
  {
    FullWidthDetailsOverviewRowPresenter.ViewHolder mViewHolder;
    
    ActionsItemBridgeAdapter(FullWidthDetailsOverviewRowPresenter.ViewHolder paramViewHolder)
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
      if ((this.mViewHolder.getOnItemViewClickedListener() != null) || (FullWidthDetailsOverviewRowPresenter.this.mActionClickedListener != null)) {
        paramViewHolder.getPresenter().setOnClickListener(paramViewHolder.getViewHolder(), new View.OnClickListener()
        {
          public void onClick(View paramAnonymousView)
          {
            if (FullWidthDetailsOverviewRowPresenter.ActionsItemBridgeAdapter.this.mViewHolder.getOnItemViewClickedListener() != null) {
              FullWidthDetailsOverviewRowPresenter.ActionsItemBridgeAdapter.this.mViewHolder.getOnItemViewClickedListener().onItemClicked(paramViewHolder.getViewHolder(), paramViewHolder.getItem(), FullWidthDetailsOverviewRowPresenter.ActionsItemBridgeAdapter.this.mViewHolder, FullWidthDetailsOverviewRowPresenter.ActionsItemBridgeAdapter.this.mViewHolder.getRow());
            }
            if (FullWidthDetailsOverviewRowPresenter.this.mActionClickedListener != null) {
              FullWidthDetailsOverviewRowPresenter.this.mActionClickedListener.onActionClicked((Action)paramViewHolder.getItem());
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
      if ((this.mViewHolder.getOnItemViewClickedListener() != null) || (FullWidthDetailsOverviewRowPresenter.this.mActionClickedListener != null)) {
        paramViewHolder.getPresenter().setOnClickListener(paramViewHolder.getViewHolder(), null);
      }
    }
  }
  
  public static abstract class Listener
  {
    public void onBindLogo(FullWidthDetailsOverviewRowPresenter.ViewHolder paramViewHolder) {}
  }
  
  public class ViewHolder
    extends RowPresenter.ViewHolder
  {
    ItemBridgeAdapter mActionBridgeAdapter;
    final HorizontalGridView mActionsRow;
    final OnChildSelectedListener mChildSelectedListener = new OnChildSelectedListener()
    {
      public void onChildSelected(ViewGroup paramAnonymousViewGroup, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
      {
        FullWidthDetailsOverviewRowPresenter.ViewHolder.this.dispatchItemSelection(paramAnonymousView);
      }
    };
    final ViewGroup mDetailsDescriptionFrame;
    final Presenter.ViewHolder mDetailsDescriptionViewHolder;
    final DetailsOverviewLogoPresenter.ViewHolder mDetailsLogoViewHolder;
    final View.OnLayoutChangeListener mLayoutChangeListener = new View.OnLayoutChangeListener()
    {
      public void onLayoutChange(View paramAnonymousView, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3, int paramAnonymousInt4, int paramAnonymousInt5, int paramAnonymousInt6, int paramAnonymousInt7, int paramAnonymousInt8)
      {
        FullWidthDetailsOverviewRowPresenter.ViewHolder.this.checkFirstAndLastPosition(false);
      }
    };
    int mNumItems;
    final FrameLayout mOverviewFrame;
    final ViewGroup mOverviewRoot;
    protected final DetailsOverviewRow.Listener mRowListener = createRowListener();
    final RecyclerView.OnScrollListener mScrollListener = new RecyclerView.OnScrollListener()
    {
      public void onScrollStateChanged(RecyclerView paramAnonymousRecyclerView, int paramAnonymousInt) {}
      
      public void onScrolled(RecyclerView paramAnonymousRecyclerView, int paramAnonymousInt1, int paramAnonymousInt2)
      {
        FullWidthDetailsOverviewRowPresenter.ViewHolder.this.checkFirstAndLastPosition(true);
      }
    };
    int mState = 0;
    final Runnable mUpdateDrawableCallback = new Runnable()
    {
      public void run()
      {
        Row localRow = FullWidthDetailsOverviewRowPresenter.ViewHolder.this.getRow();
        if (localRow == null) {
          return;
        }
        FullWidthDetailsOverviewRowPresenter.this.mDetailsOverviewLogoPresenter.onBindViewHolder(FullWidthDetailsOverviewRowPresenter.ViewHolder.this.mDetailsLogoViewHolder, localRow);
      }
    };
    
    public ViewHolder(View paramView, Presenter paramPresenter, DetailsOverviewLogoPresenter paramDetailsOverviewLogoPresenter)
    {
      super();
      this.mOverviewRoot = ((ViewGroup)paramView.findViewById(R.id.details_root));
      this.mOverviewFrame = ((FrameLayout)paramView.findViewById(R.id.details_frame));
      this.mDetailsDescriptionFrame = ((ViewGroup)paramView.findViewById(R.id.details_overview_description));
      this.mActionsRow = ((HorizontalGridView)this.mOverviewFrame.findViewById(R.id.details_overview_actions));
      this.mActionsRow.setHasOverlappingRendering(false);
      this.mActionsRow.setOnScrollListener(this.mScrollListener);
      this.mActionsRow.setAdapter(this.mActionBridgeAdapter);
      this.mActionsRow.setOnChildSelectedListener(this.mChildSelectedListener);
      int i = paramView.getResources().getDimensionPixelSize(R.dimen.lb_details_overview_actions_fade_size);
      this.mActionsRow.setFadingRightEdgeLength(i);
      this.mActionsRow.setFadingLeftEdgeLength(i);
      this.mDetailsDescriptionViewHolder = paramPresenter.onCreateViewHolder(this.mDetailsDescriptionFrame);
      this.mDetailsDescriptionFrame.addView(this.mDetailsDescriptionViewHolder.view);
      this.mDetailsLogoViewHolder = ((DetailsOverviewLogoPresenter.ViewHolder)paramDetailsOverviewLogoPresenter.onCreateViewHolder(this.mOverviewRoot));
      this.mOverviewRoot.addView(this.mDetailsLogoViewHolder.view);
    }
    
    private int getViewCenter(View paramView)
    {
      return (paramView.getRight() - paramView.getLeft()) / 2;
    }
    
    void bindActions(ObjectAdapter paramObjectAdapter)
    {
      this.mActionBridgeAdapter.setAdapter(paramObjectAdapter);
      this.mActionsRow.setAdapter(this.mActionBridgeAdapter);
      this.mNumItems = this.mActionBridgeAdapter.getItemCount();
    }
    
    void checkFirstAndLastPosition(boolean paramBoolean)
    {
      RecyclerView.ViewHolder localViewHolder = this.mActionsRow.findViewHolderForPosition(this.mNumItems - 1);
      if ((localViewHolder == null) || (localViewHolder.itemView.getRight() > this.mActionsRow.getWidth())) {}
      for (;;)
      {
        localViewHolder = this.mActionsRow.findViewHolderForPosition(0);
        if ((localViewHolder != null) && (localViewHolder.itemView.getLeft() >= 0)) {
          break;
        }
        return;
      }
    }
    
    protected DetailsOverviewRow.Listener createRowListener()
    {
      return new DetailsOverviewRowListener();
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
    
    public final ViewGroup getActionsRow()
    {
      return this.mActionsRow;
    }
    
    public final ViewGroup getDetailsDescriptionFrame()
    {
      return this.mDetailsDescriptionFrame;
    }
    
    public final Presenter.ViewHolder getDetailsDescriptionViewHolder()
    {
      return this.mDetailsDescriptionViewHolder;
    }
    
    public final DetailsOverviewLogoPresenter.ViewHolder getLogoViewHolder()
    {
      return this.mDetailsLogoViewHolder;
    }
    
    public final ViewGroup getOverviewView()
    {
      return this.mOverviewFrame;
    }
    
    public final int getState()
    {
      return this.mState;
    }
    
    void onBind()
    {
      DetailsOverviewRow localDetailsOverviewRow = (DetailsOverviewRow)getRow();
      bindActions(localDetailsOverviewRow.getActionsAdapter());
      localDetailsOverviewRow.addListener(this.mRowListener);
    }
    
    void onUnbind()
    {
      ((DetailsOverviewRow)getRow()).removeListener(this.mRowListener);
      FullWidthDetailsOverviewRowPresenter.sHandler.removeCallbacks(this.mUpdateDrawableCallback);
    }
    
    public class DetailsOverviewRowListener
      extends DetailsOverviewRow.Listener
    {
      public DetailsOverviewRowListener() {}
      
      public void onActionsAdapterChanged(DetailsOverviewRow paramDetailsOverviewRow)
      {
        FullWidthDetailsOverviewRowPresenter.ViewHolder.this.bindActions(paramDetailsOverviewRow.getActionsAdapter());
      }
      
      public void onImageDrawableChanged(DetailsOverviewRow paramDetailsOverviewRow)
      {
        FullWidthDetailsOverviewRowPresenter.sHandler.removeCallbacks(FullWidthDetailsOverviewRowPresenter.ViewHolder.this.mUpdateDrawableCallback);
        FullWidthDetailsOverviewRowPresenter.sHandler.post(FullWidthDetailsOverviewRowPresenter.ViewHolder.this.mUpdateDrawableCallback);
      }
      
      public void onItemChanged(DetailsOverviewRow paramDetailsOverviewRow)
      {
        if (FullWidthDetailsOverviewRowPresenter.ViewHolder.this.mDetailsDescriptionViewHolder != null) {
          FullWidthDetailsOverviewRowPresenter.this.mDetailsPresenter.onUnbindViewHolder(FullWidthDetailsOverviewRowPresenter.ViewHolder.this.mDetailsDescriptionViewHolder);
        }
        FullWidthDetailsOverviewRowPresenter.this.mDetailsPresenter.onBindViewHolder(FullWidthDetailsOverviewRowPresenter.ViewHolder.this.mDetailsDescriptionViewHolder, paramDetailsOverviewRow.getItem());
      }
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/FullWidthDetailsOverviewRowPresenter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */