package android.support.v17.leanback.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.support.v17.leanback.R.dimen;
import android.support.v17.leanback.R.styleable;
import android.support.v17.leanback.graphics.ColorOverlayDimmer;
import android.support.v17.leanback.system.Settings;
import android.support.v17.leanback.transition.TransitionHelper;
import android.support.v7.widget.RecyclerView.RecycledViewPool;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import java.util.HashMap;

public class ListRowPresenter
  extends RowPresenter
{
  private static final boolean DEBUG = false;
  private static final int DEFAULT_RECYCLED_POOL_SIZE = 24;
  private static final String TAG = "ListRowPresenter";
  private static int sExpandedRowNoHovercardBottomPadding;
  private static int sExpandedSelectedRowTopPadding;
  private static int sSelectedRowTopPadding;
  private int mBrowseRowsFadingEdgeLength = -1;
  private int mExpandedRowHeight;
  private int mFocusZoomFactor;
  private PresenterSelector mHoverCardPresenterSelector;
  private boolean mKeepChildForeground = true;
  private int mNumRows = 1;
  private HashMap<Presenter, Integer> mRecycledPoolSize = new HashMap();
  private boolean mRoundedCornersEnabled = true;
  private int mRowHeight;
  private boolean mShadowEnabled = true;
  ShadowOverlayHelper mShadowOverlayHelper;
  private ItemBridgeAdapter.Wrapper mShadowOverlayWrapper;
  private boolean mUseFocusDimmer;
  
  public ListRowPresenter()
  {
    this(2);
  }
  
  public ListRowPresenter(int paramInt)
  {
    this(paramInt, false);
  }
  
  public ListRowPresenter(int paramInt, boolean paramBoolean)
  {
    if (!FocusHighlightHelper.isValidZoomIndex(paramInt)) {
      throw new IllegalArgumentException("Unhandled zoom factor");
    }
    this.mFocusZoomFactor = paramInt;
    this.mUseFocusDimmer = paramBoolean;
  }
  
  private int getSpaceUnderBaseline(ViewHolder paramViewHolder)
  {
    paramViewHolder = paramViewHolder.getHeaderViewHolder();
    if (paramViewHolder != null)
    {
      if (getHeaderPresenter() != null) {
        return getHeaderPresenter().getSpaceUnderBaseline(paramViewHolder);
      }
      return paramViewHolder.view.getPaddingBottom();
    }
    return 0;
  }
  
  private static void initStatics(Context paramContext)
  {
    if (sSelectedRowTopPadding == 0)
    {
      sSelectedRowTopPadding = paramContext.getResources().getDimensionPixelSize(R.dimen.lb_browse_selected_row_top_padding);
      sExpandedSelectedRowTopPadding = paramContext.getResources().getDimensionPixelSize(R.dimen.lb_browse_expanded_selected_row_top_padding);
      sExpandedRowNoHovercardBottomPadding = paramContext.getResources().getDimensionPixelSize(R.dimen.lb_browse_expanded_row_no_hovercard_bottom_padding);
    }
  }
  
  private void setVerticalPadding(ViewHolder paramViewHolder)
  {
    int j;
    int i;
    if (paramViewHolder.isExpanded())
    {
      j = getSpaceUnderBaseline(paramViewHolder);
      if (paramViewHolder.isSelected())
      {
        i = sExpandedSelectedRowTopPadding;
        j = i - j;
        if (this.mHoverCardPresenterSelector != null) {
          break label65;
        }
        i = sExpandedRowNoHovercardBottomPadding;
      }
    }
    for (;;)
    {
      paramViewHolder.getGridView().setPadding(paramViewHolder.mPaddingLeft, j, paramViewHolder.mPaddingRight, i);
      return;
      i = paramViewHolder.mPaddingTop;
      break;
      label65:
      i = paramViewHolder.mPaddingBottom;
      continue;
      if (paramViewHolder.isSelected())
      {
        j = sSelectedRowTopPadding - paramViewHolder.mPaddingBottom;
        i = sSelectedRowTopPadding;
      }
      else
      {
        j = 0;
        i = paramViewHolder.mPaddingBottom;
      }
    }
  }
  
  private void setupFadingEffect(ListRowView paramListRowView)
  {
    paramListRowView = paramListRowView.getGridView();
    if (this.mBrowseRowsFadingEdgeLength < 0)
    {
      TypedArray localTypedArray = paramListRowView.getContext().obtainStyledAttributes(R.styleable.LeanbackTheme);
      this.mBrowseRowsFadingEdgeLength = ((int)localTypedArray.getDimension(R.styleable.LeanbackTheme_browseRowsFadingEdgeLength, 0.0F));
      localTypedArray.recycle();
    }
    paramListRowView.setFadingLeftEdgeLength(this.mBrowseRowsFadingEdgeLength);
  }
  
  private void updateFooterViewSwitcher(ViewHolder paramViewHolder)
  {
    if ((paramViewHolder.mExpanded) && (paramViewHolder.mSelected))
    {
      if (this.mHoverCardPresenterSelector != null) {
        paramViewHolder.mHoverCardViewSwitcher.init((ViewGroup)paramViewHolder.view, this.mHoverCardPresenterSelector);
      }
      localObject = (ItemBridgeAdapter.ViewHolder)paramViewHolder.mGridView.findViewHolderForPosition(paramViewHolder.mGridView.getSelectedPosition());
      if (localObject == null)
      {
        localObject = null;
        selectChildView(paramViewHolder, (View)localObject, false);
      }
    }
    while (this.mHoverCardPresenterSelector == null) {
      for (;;)
      {
        return;
        Object localObject = ((ItemBridgeAdapter.ViewHolder)localObject).itemView;
      }
    }
    paramViewHolder.mHoverCardViewSwitcher.unselect();
  }
  
  protected void applySelectLevelToChild(ViewHolder paramViewHolder, View paramView)
  {
    if ((this.mShadowOverlayHelper != null) && (this.mShadowOverlayHelper.needsOverlay()))
    {
      int i = paramViewHolder.mColorDimmer.getPaint().getColor();
      this.mShadowOverlayHelper.setOverlayColor(paramView, i);
    }
  }
  
  public final boolean areChildRoundedCornersEnabled()
  {
    return this.mRoundedCornersEnabled;
  }
  
  protected RowPresenter.ViewHolder createRowViewHolder(ViewGroup paramViewGroup)
  {
    initStatics(paramViewGroup.getContext());
    paramViewGroup = new ListRowView(paramViewGroup.getContext());
    setupFadingEffect(paramViewGroup);
    if (this.mRowHeight != 0) {
      paramViewGroup.getGridView().setRowHeight(this.mRowHeight);
    }
    return new ViewHolder(paramViewGroup, paramViewGroup.getGridView(), this);
  }
  
  protected ShadowOverlayHelper.Options createShadowOverlayOptions()
  {
    return ShadowOverlayHelper.Options.DEFAULT;
  }
  
  protected void dispatchItemSelectedListener(RowPresenter.ViewHolder paramViewHolder, boolean paramBoolean)
  {
    ViewHolder localViewHolder = (ViewHolder)paramViewHolder;
    ItemBridgeAdapter.ViewHolder localViewHolder1 = (ItemBridgeAdapter.ViewHolder)localViewHolder.mGridView.findViewHolderForPosition(localViewHolder.mGridView.getSelectedPosition());
    if (localViewHolder1 == null) {
      super.dispatchItemSelectedListener(paramViewHolder, paramBoolean);
    }
    while ((!paramBoolean) || (paramViewHolder.getOnItemViewSelectedListener() == null)) {
      return;
    }
    paramViewHolder.getOnItemViewSelectedListener().onItemSelected(localViewHolder1.getViewHolder(), localViewHolder1.mItem, localViewHolder, localViewHolder.getRow());
  }
  
  public final void enableChildRoundedCorners(boolean paramBoolean)
  {
    this.mRoundedCornersEnabled = paramBoolean;
  }
  
  public void freeze(RowPresenter.ViewHolder paramViewHolder, boolean paramBoolean)
  {
    paramViewHolder = ((ViewHolder)paramViewHolder).mGridView;
    if (!paramBoolean) {}
    for (paramBoolean = true;; paramBoolean = false)
    {
      paramViewHolder.setScrollEnabled(paramBoolean);
      return;
    }
  }
  
  public int getExpandedRowHeight()
  {
    if (this.mExpandedRowHeight != 0) {
      return this.mExpandedRowHeight;
    }
    return this.mRowHeight;
  }
  
  public final int getFocusZoomFactor()
  {
    return this.mFocusZoomFactor;
  }
  
  public final PresenterSelector getHoverCardPresenterSelector()
  {
    return this.mHoverCardPresenterSelector;
  }
  
  public int getRecycledPoolSize(Presenter paramPresenter)
  {
    if (this.mRecycledPoolSize.containsKey(paramPresenter)) {
      return ((Integer)this.mRecycledPoolSize.get(paramPresenter)).intValue();
    }
    return 24;
  }
  
  public int getRowHeight()
  {
    return this.mRowHeight;
  }
  
  public final boolean getShadowEnabled()
  {
    return this.mShadowEnabled;
  }
  
  @Deprecated
  public final int getZoomFactor()
  {
    return this.mFocusZoomFactor;
  }
  
  protected void initializeRowViewHolder(RowPresenter.ViewHolder paramViewHolder)
  {
    super.initializeRowViewHolder(paramViewHolder);
    final ViewHolder localViewHolder = (ViewHolder)paramViewHolder;
    paramViewHolder = paramViewHolder.view.getContext();
    if (this.mShadowOverlayHelper == null)
    {
      this.mShadowOverlayHelper = new ShadowOverlayHelper.Builder().needsOverlay(needsDefaultListSelectEffect()).needsShadow(needsDefaultShadow()).needsRoundedCorner(areChildRoundedCornersEnabled()).preferZOrder(isUsingZOrder(paramViewHolder)).keepForegroundDrawable(this.mKeepChildForeground).options(createShadowOverlayOptions()).build(paramViewHolder);
      if (this.mShadowOverlayHelper.needsWrapper()) {
        this.mShadowOverlayWrapper = new ItemBridgeAdapterShadowOverlayWrapper(this.mShadowOverlayHelper);
      }
    }
    localViewHolder.mItemBridgeAdapter = new ListRowPresenterItemBridgeAdapter(localViewHolder);
    localViewHolder.mItemBridgeAdapter.setWrapper(this.mShadowOverlayWrapper);
    this.mShadowOverlayHelper.prepareParentForShadow(localViewHolder.mGridView);
    FocusHighlightHelper.setupBrowseItemFocusHighlight(localViewHolder.mItemBridgeAdapter, this.mFocusZoomFactor, this.mUseFocusDimmer);
    paramViewHolder = localViewHolder.mGridView;
    if (this.mShadowOverlayHelper.getShadowType() != 3) {}
    for (boolean bool = true;; bool = false)
    {
      paramViewHolder.setFocusDrawingOrderEnabled(bool);
      localViewHolder.mGridView.setOnChildSelectedListener(new OnChildSelectedListener()
      {
        public void onChildSelected(ViewGroup paramAnonymousViewGroup, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
        {
          ListRowPresenter.this.selectChildView(localViewHolder, paramAnonymousView, true);
        }
      });
      localViewHolder.mGridView.setOnUnhandledKeyListener(new BaseGridView.OnUnhandledKeyListener()
      {
        public boolean onUnhandledKey(KeyEvent paramAnonymousKeyEvent)
        {
          return (localViewHolder.getOnKeyListener() != null) && (localViewHolder.getOnKeyListener().onKey(localViewHolder.view, paramAnonymousKeyEvent.getKeyCode(), paramAnonymousKeyEvent));
        }
      });
      localViewHolder.mGridView.setNumRows(this.mNumRows);
      return;
    }
  }
  
  public final boolean isFocusDimmerUsed()
  {
    return this.mUseFocusDimmer;
  }
  
  public final boolean isKeepChildForeground()
  {
    return this.mKeepChildForeground;
  }
  
  public boolean isUsingDefaultListSelectEffect()
  {
    return true;
  }
  
  public final boolean isUsingDefaultSelectEffect()
  {
    return false;
  }
  
  public boolean isUsingDefaultShadow()
  {
    return ShadowOverlayHelper.supportsShadow();
  }
  
  public boolean isUsingZOrder(Context paramContext)
  {
    return !Settings.getInstance(paramContext).preferStaticShadows();
  }
  
  final boolean needsDefaultListSelectEffect()
  {
    return (isUsingDefaultListSelectEffect()) && (getSelectEffectEnabled());
  }
  
  final boolean needsDefaultShadow()
  {
    return (isUsingDefaultShadow()) && (getShadowEnabled());
  }
  
  protected void onBindRowViewHolder(RowPresenter.ViewHolder paramViewHolder, Object paramObject)
  {
    super.onBindRowViewHolder(paramViewHolder, paramObject);
    paramViewHolder = (ViewHolder)paramViewHolder;
    paramObject = (ListRow)paramObject;
    paramViewHolder.mItemBridgeAdapter.setAdapter(((ListRow)paramObject).getAdapter());
    paramViewHolder.mGridView.setAdapter(paramViewHolder.mItemBridgeAdapter);
    paramViewHolder.mGridView.setContentDescription(((ListRow)paramObject).getContentDescription());
  }
  
  protected void onRowViewExpanded(RowPresenter.ViewHolder paramViewHolder, boolean paramBoolean)
  {
    super.onRowViewExpanded(paramViewHolder, paramBoolean);
    paramViewHolder = (ViewHolder)paramViewHolder;
    if (getRowHeight() != getExpandedRowHeight()) {
      if (!paramBoolean) {
        break label50;
      }
    }
    label50:
    for (int i = getExpandedRowHeight();; i = getRowHeight())
    {
      paramViewHolder.getGridView().setRowHeight(i);
      setVerticalPadding(paramViewHolder);
      updateFooterViewSwitcher(paramViewHolder);
      return;
    }
  }
  
  protected void onRowViewSelected(RowPresenter.ViewHolder paramViewHolder, boolean paramBoolean)
  {
    super.onRowViewSelected(paramViewHolder, paramBoolean);
    paramViewHolder = (ViewHolder)paramViewHolder;
    setVerticalPadding(paramViewHolder);
    updateFooterViewSwitcher(paramViewHolder);
  }
  
  protected void onSelectLevelChanged(RowPresenter.ViewHolder paramViewHolder)
  {
    super.onSelectLevelChanged(paramViewHolder);
    paramViewHolder = (ViewHolder)paramViewHolder;
    int i = 0;
    int j = paramViewHolder.mGridView.getChildCount();
    while (i < j)
    {
      applySelectLevelToChild(paramViewHolder, paramViewHolder.mGridView.getChildAt(i));
      i += 1;
    }
  }
  
  protected void onUnbindRowViewHolder(RowPresenter.ViewHolder paramViewHolder)
  {
    ViewHolder localViewHolder = (ViewHolder)paramViewHolder;
    localViewHolder.mGridView.setAdapter(null);
    localViewHolder.mItemBridgeAdapter.clear();
    super.onUnbindRowViewHolder(paramViewHolder);
  }
  
  void selectChildView(ViewHolder paramViewHolder, View paramView, boolean paramBoolean)
  {
    if (paramView != null) {
      if (paramViewHolder.mSelected)
      {
        ItemBridgeAdapter.ViewHolder localViewHolder = (ItemBridgeAdapter.ViewHolder)paramViewHolder.mGridView.getChildViewHolder(paramView);
        if (this.mHoverCardPresenterSelector != null) {
          paramViewHolder.mHoverCardViewSwitcher.select(paramViewHolder.mGridView, paramView, localViewHolder.mItem);
        }
        if ((paramBoolean) && (paramViewHolder.getOnItemViewSelectedListener() != null)) {
          paramViewHolder.getOnItemViewSelectedListener().onItemSelected(localViewHolder.mHolder, localViewHolder.mItem, paramViewHolder, paramViewHolder.mRow);
        }
      }
    }
    do
    {
      return;
      if (this.mHoverCardPresenterSelector != null) {
        paramViewHolder.mHoverCardViewSwitcher.unselect();
      }
    } while ((!paramBoolean) || (paramViewHolder.getOnItemViewSelectedListener() == null));
    paramViewHolder.getOnItemViewSelectedListener().onItemSelected(null, null, paramViewHolder, paramViewHolder.mRow);
  }
  
  public void setEntranceTransitionState(RowPresenter.ViewHolder paramViewHolder, boolean paramBoolean)
  {
    super.setEntranceTransitionState(paramViewHolder, paramBoolean);
    paramViewHolder = ((ViewHolder)paramViewHolder).mGridView;
    if (paramBoolean) {}
    for (int i = 0;; i = 4)
    {
      paramViewHolder.setChildrenVisibility(i);
      return;
    }
  }
  
  public void setExpandedRowHeight(int paramInt)
  {
    this.mExpandedRowHeight = paramInt;
  }
  
  public final void setHoverCardPresenterSelector(PresenterSelector paramPresenterSelector)
  {
    this.mHoverCardPresenterSelector = paramPresenterSelector;
  }
  
  public final void setKeepChildForeground(boolean paramBoolean)
  {
    this.mKeepChildForeground = paramBoolean;
  }
  
  public void setNumRows(int paramInt)
  {
    this.mNumRows = paramInt;
  }
  
  public void setRecycledPoolSize(Presenter paramPresenter, int paramInt)
  {
    this.mRecycledPoolSize.put(paramPresenter, Integer.valueOf(paramInt));
  }
  
  public void setRowHeight(int paramInt)
  {
    this.mRowHeight = paramInt;
  }
  
  public final void setShadowEnabled(boolean paramBoolean)
  {
    this.mShadowEnabled = paramBoolean;
  }
  
  class ListRowPresenterItemBridgeAdapter
    extends ItemBridgeAdapter
  {
    ListRowPresenter.ViewHolder mRowViewHolder;
    
    ListRowPresenterItemBridgeAdapter(ListRowPresenter.ViewHolder paramViewHolder)
    {
      this.mRowViewHolder = paramViewHolder;
    }
    
    public void onAddPresenter(Presenter paramPresenter, int paramInt)
    {
      this.mRowViewHolder.getGridView().getRecycledViewPool().setMaxRecycledViews(paramInt, ListRowPresenter.this.getRecycledPoolSize(paramPresenter));
    }
    
    public void onAttachedToWindow(ItemBridgeAdapter.ViewHolder paramViewHolder)
    {
      ListRowPresenter.this.applySelectLevelToChild(this.mRowViewHolder, paramViewHolder.itemView);
      this.mRowViewHolder.syncActivatedStatus(paramViewHolder.itemView);
    }
    
    public void onBind(final ItemBridgeAdapter.ViewHolder paramViewHolder)
    {
      if (this.mRowViewHolder.getOnItemViewClickedListener() != null) {
        paramViewHolder.mHolder.view.setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramAnonymousView)
          {
            paramAnonymousView = (ItemBridgeAdapter.ViewHolder)ListRowPresenter.ListRowPresenterItemBridgeAdapter.this.mRowViewHolder.mGridView.getChildViewHolder(paramViewHolder.itemView);
            if (ListRowPresenter.ListRowPresenterItemBridgeAdapter.this.mRowViewHolder.getOnItemViewClickedListener() != null) {
              ListRowPresenter.ListRowPresenterItemBridgeAdapter.this.mRowViewHolder.getOnItemViewClickedListener().onItemClicked(paramViewHolder.mHolder, paramAnonymousView.mItem, ListRowPresenter.ListRowPresenterItemBridgeAdapter.this.mRowViewHolder, (ListRow)ListRowPresenter.ListRowPresenterItemBridgeAdapter.this.mRowViewHolder.mRow);
            }
          }
        });
      }
    }
    
    protected void onCreate(ItemBridgeAdapter.ViewHolder paramViewHolder)
    {
      if ((paramViewHolder.itemView instanceof ViewGroup)) {
        TransitionHelper.setTransitionGroup((ViewGroup)paramViewHolder.itemView, true);
      }
      if (ListRowPresenter.this.mShadowOverlayHelper != null) {
        ListRowPresenter.this.mShadowOverlayHelper.onViewCreated(paramViewHolder.itemView);
      }
    }
    
    public void onUnbind(ItemBridgeAdapter.ViewHolder paramViewHolder)
    {
      if (this.mRowViewHolder.getOnItemViewClickedListener() != null) {
        paramViewHolder.mHolder.view.setOnClickListener(null);
      }
    }
  }
  
  public static class SelectItemViewHolderTask
    extends Presenter.ViewHolderTask
  {
    private int mItemPosition;
    Presenter.ViewHolderTask mItemTask;
    private boolean mSmoothScroll = true;
    
    public SelectItemViewHolderTask(int paramInt)
    {
      setItemPosition(paramInt);
    }
    
    public int getItemPosition()
    {
      return this.mItemPosition;
    }
    
    public Presenter.ViewHolderTask getItemTask()
    {
      return this.mItemTask;
    }
    
    public boolean isSmoothScroll()
    {
      return this.mSmoothScroll;
    }
    
    public void run(Presenter.ViewHolder paramViewHolder)
    {
      HorizontalGridView localHorizontalGridView;
      if ((paramViewHolder instanceof ListRowPresenter.ViewHolder))
      {
        localHorizontalGridView = ((ListRowPresenter.ViewHolder)paramViewHolder).getGridView();
        paramViewHolder = null;
        if (this.mItemTask != null) {
          paramViewHolder = new ViewHolderTask()
          {
            final Presenter.ViewHolderTask itemTask = ListRowPresenter.SelectItemViewHolderTask.this.mItemTask;
            
            public void run(RecyclerView.ViewHolder paramAnonymousViewHolder)
            {
              paramAnonymousViewHolder = (ItemBridgeAdapter.ViewHolder)paramAnonymousViewHolder;
              this.itemTask.run(paramAnonymousViewHolder.getViewHolder());
            }
          };
        }
        if (isSmoothScroll()) {
          localHorizontalGridView.setSelectedPositionSmooth(this.mItemPosition, paramViewHolder);
        }
      }
      else
      {
        return;
      }
      localHorizontalGridView.setSelectedPosition(this.mItemPosition, paramViewHolder);
    }
    
    public void setItemPosition(int paramInt)
    {
      this.mItemPosition = paramInt;
    }
    
    public void setItemTask(Presenter.ViewHolderTask paramViewHolderTask)
    {
      this.mItemTask = paramViewHolderTask;
    }
    
    public void setSmoothScroll(boolean paramBoolean)
    {
      this.mSmoothScroll = paramBoolean;
    }
  }
  
  public static class ViewHolder
    extends RowPresenter.ViewHolder
  {
    final HorizontalGridView mGridView;
    final HorizontalHoverCardSwitcher mHoverCardViewSwitcher = new HorizontalHoverCardSwitcher();
    ItemBridgeAdapter mItemBridgeAdapter;
    final ListRowPresenter mListRowPresenter;
    final int mPaddingBottom;
    final int mPaddingLeft;
    final int mPaddingRight;
    final int mPaddingTop;
    
    public ViewHolder(View paramView, HorizontalGridView paramHorizontalGridView, ListRowPresenter paramListRowPresenter)
    {
      super();
      this.mGridView = paramHorizontalGridView;
      this.mListRowPresenter = paramListRowPresenter;
      this.mPaddingTop = this.mGridView.getPaddingTop();
      this.mPaddingBottom = this.mGridView.getPaddingBottom();
      this.mPaddingLeft = this.mGridView.getPaddingLeft();
      this.mPaddingRight = this.mGridView.getPaddingRight();
    }
    
    public final ItemBridgeAdapter getBridgeAdapter()
    {
      return this.mItemBridgeAdapter;
    }
    
    public final HorizontalGridView getGridView()
    {
      return this.mGridView;
    }
    
    public Presenter.ViewHolder getItemViewHolder(int paramInt)
    {
      ItemBridgeAdapter.ViewHolder localViewHolder = (ItemBridgeAdapter.ViewHolder)this.mGridView.findViewHolderForAdapterPosition(paramInt);
      if (localViewHolder == null) {
        return null;
      }
      return localViewHolder.getViewHolder();
    }
    
    public final ListRowPresenter getListRowPresenter()
    {
      return this.mListRowPresenter;
    }
    
    public Object getSelectedItem()
    {
      ItemBridgeAdapter.ViewHolder localViewHolder = (ItemBridgeAdapter.ViewHolder)this.mGridView.findViewHolderForAdapterPosition(getSelectedPosition());
      if (localViewHolder == null) {
        return null;
      }
      return localViewHolder.getItem();
    }
    
    public Presenter.ViewHolder getSelectedItemViewHolder()
    {
      return getItemViewHolder(getSelectedPosition());
    }
    
    public int getSelectedPosition()
    {
      return this.mGridView.getSelectedPosition();
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/ListRowPresenter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */