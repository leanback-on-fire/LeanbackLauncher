package android.support.v17.leanback.widget;

import android.content.Context;
import android.support.v17.leanback.R.id;
import android.support.v17.leanback.R.layout;
import android.support.v17.leanback.system.Settings;
import android.support.v17.leanback.transition.TransitionHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class VerticalGridPresenter
  extends Presenter
{
  private static final boolean DEBUG = false;
  private static final String TAG = "GridPresenter";
  private int mFocusZoomFactor;
  private boolean mKeepChildForeground = true;
  private int mNumColumns = -1;
  private OnItemViewClickedListener mOnItemViewClickedListener;
  private OnItemViewSelectedListener mOnItemViewSelectedListener;
  private boolean mRoundedCornersEnabled = true;
  private boolean mShadowEnabled = true;
  ShadowOverlayHelper mShadowOverlayHelper;
  private ItemBridgeAdapter.Wrapper mShadowOverlayWrapper;
  private boolean mUseFocusDimmer;
  
  public VerticalGridPresenter()
  {
    this(3);
  }
  
  public VerticalGridPresenter(int paramInt)
  {
    this(paramInt, true);
  }
  
  public VerticalGridPresenter(int paramInt, boolean paramBoolean)
  {
    this.mFocusZoomFactor = paramInt;
    this.mUseFocusDimmer = paramBoolean;
  }
  
  public final boolean areChildRoundedCornersEnabled()
  {
    return this.mRoundedCornersEnabled;
  }
  
  protected ViewHolder createGridViewHolder(ViewGroup paramViewGroup)
  {
    return new ViewHolder((VerticalGridView)LayoutInflater.from(paramViewGroup.getContext()).inflate(R.layout.lb_vertical_grid, paramViewGroup, false).findViewById(R.id.browse_grid));
  }
  
  protected ShadowOverlayHelper.Options createShadowOverlayOptions()
  {
    return ShadowOverlayHelper.Options.DEFAULT;
  }
  
  public final void enableChildRoundedCorners(boolean paramBoolean)
  {
    this.mRoundedCornersEnabled = paramBoolean;
  }
  
  public final int getFocusZoomFactor()
  {
    return this.mFocusZoomFactor;
  }
  
  public final boolean getKeepChildForeground()
  {
    return this.mKeepChildForeground;
  }
  
  public int getNumberOfColumns()
  {
    return this.mNumColumns;
  }
  
  public final OnItemViewClickedListener getOnItemViewClickedListener()
  {
    return this.mOnItemViewClickedListener;
  }
  
  public final OnItemViewSelectedListener getOnItemViewSelectedListener()
  {
    return this.mOnItemViewSelectedListener;
  }
  
  public final boolean getShadowEnabled()
  {
    return this.mShadowEnabled;
  }
  
  protected void initializeGridViewHolder(final ViewHolder paramViewHolder)
  {
    boolean bool = true;
    if (this.mNumColumns == -1) {
      throw new IllegalStateException("Number of columns must be set");
    }
    paramViewHolder.getGridView().setNumColumns(this.mNumColumns);
    paramViewHolder.mInitialized = true;
    Object localObject = paramViewHolder.mGridView.getContext();
    if (this.mShadowOverlayHelper == null)
    {
      this.mShadowOverlayHelper = new ShadowOverlayHelper.Builder().needsOverlay(this.mUseFocusDimmer).needsShadow(needsDefaultShadow()).needsRoundedCorner(areChildRoundedCornersEnabled()).preferZOrder(isUsingZOrder((Context)localObject)).keepForegroundDrawable(this.mKeepChildForeground).options(createShadowOverlayOptions()).build((Context)localObject);
      if (this.mShadowOverlayHelper.needsWrapper()) {
        this.mShadowOverlayWrapper = new ItemBridgeAdapterShadowOverlayWrapper(this.mShadowOverlayHelper);
      }
    }
    paramViewHolder.mItemBridgeAdapter.setWrapper(this.mShadowOverlayWrapper);
    this.mShadowOverlayHelper.prepareParentForShadow(paramViewHolder.mGridView);
    localObject = paramViewHolder.getGridView();
    if (this.mShadowOverlayHelper.getShadowType() != 3) {}
    for (;;)
    {
      ((VerticalGridView)localObject).setFocusDrawingOrderEnabled(bool);
      FocusHighlightHelper.setupBrowseItemFocusHighlight(paramViewHolder.mItemBridgeAdapter, this.mFocusZoomFactor, this.mUseFocusDimmer);
      paramViewHolder.getGridView().setOnChildSelectedListener(new OnChildSelectedListener()
      {
        public void onChildSelected(ViewGroup paramAnonymousViewGroup, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
        {
          VerticalGridPresenter.this.selectChildView(paramViewHolder, paramAnonymousView);
        }
      });
      return;
      bool = false;
    }
  }
  
  public final boolean isFocusDimmerUsed()
  {
    return this.mUseFocusDimmer;
  }
  
  public boolean isUsingDefaultShadow()
  {
    return ShadowOverlayHelper.supportsShadow();
  }
  
  public boolean isUsingZOrder(Context paramContext)
  {
    return !Settings.getInstance(paramContext).preferStaticShadows();
  }
  
  final boolean needsDefaultShadow()
  {
    return (isUsingDefaultShadow()) && (getShadowEnabled());
  }
  
  public void onBindViewHolder(Presenter.ViewHolder paramViewHolder, Object paramObject)
  {
    paramViewHolder = (ViewHolder)paramViewHolder;
    paramViewHolder.mItemBridgeAdapter.setAdapter((ObjectAdapter)paramObject);
    paramViewHolder.getGridView().setAdapter(paramViewHolder.mItemBridgeAdapter);
  }
  
  public final ViewHolder onCreateViewHolder(ViewGroup paramViewGroup)
  {
    paramViewGroup = createGridViewHolder(paramViewGroup);
    paramViewGroup.mInitialized = false;
    paramViewGroup.mItemBridgeAdapter = new VerticalGridItemBridgeAdapter();
    initializeGridViewHolder(paramViewGroup);
    if (!paramViewGroup.mInitialized) {
      throw new RuntimeException("super.initializeGridViewHolder() must be called");
    }
    return paramViewGroup;
  }
  
  public void onUnbindViewHolder(Presenter.ViewHolder paramViewHolder)
  {
    paramViewHolder = (ViewHolder)paramViewHolder;
    paramViewHolder.mItemBridgeAdapter.setAdapter(null);
    paramViewHolder.getGridView().setAdapter(null);
  }
  
  void selectChildView(ViewHolder paramViewHolder, View paramView)
  {
    if (getOnItemViewSelectedListener() != null) {
      if (paramView != null) {
        break label31;
      }
    }
    label31:
    for (paramViewHolder = null; paramViewHolder == null; paramViewHolder = (ItemBridgeAdapter.ViewHolder)paramViewHolder.getGridView().getChildViewHolder(paramView))
    {
      getOnItemViewSelectedListener().onItemSelected(null, null, null, null);
      return;
    }
    getOnItemViewSelectedListener().onItemSelected(paramViewHolder.mHolder, paramViewHolder.mItem, null, null);
  }
  
  public void setEntranceTransitionState(ViewHolder paramViewHolder, boolean paramBoolean)
  {
    paramViewHolder = paramViewHolder.mGridView;
    if (paramBoolean) {}
    for (int i = 0;; i = 4)
    {
      paramViewHolder.setChildrenVisibility(i);
      return;
    }
  }
  
  public final void setKeepChildForeground(boolean paramBoolean)
  {
    this.mKeepChildForeground = paramBoolean;
  }
  
  public void setNumberOfColumns(int paramInt)
  {
    if (paramInt < 0) {
      throw new IllegalArgumentException("Invalid number of columns");
    }
    if (this.mNumColumns != paramInt) {
      this.mNumColumns = paramInt;
    }
  }
  
  public final void setOnItemViewClickedListener(OnItemViewClickedListener paramOnItemViewClickedListener)
  {
    this.mOnItemViewClickedListener = paramOnItemViewClickedListener;
  }
  
  public final void setOnItemViewSelectedListener(OnItemViewSelectedListener paramOnItemViewSelectedListener)
  {
    this.mOnItemViewSelectedListener = paramOnItemViewSelectedListener;
  }
  
  public final void setShadowEnabled(boolean paramBoolean)
  {
    this.mShadowEnabled = paramBoolean;
  }
  
  class VerticalGridItemBridgeAdapter
    extends ItemBridgeAdapter
  {
    VerticalGridItemBridgeAdapter() {}
    
    public void onAttachedToWindow(ItemBridgeAdapter.ViewHolder paramViewHolder)
    {
      paramViewHolder.itemView.setActivated(true);
    }
    
    public void onBind(final ItemBridgeAdapter.ViewHolder paramViewHolder)
    {
      if (VerticalGridPresenter.this.getOnItemViewClickedListener() != null) {
        paramViewHolder.mHolder.view.setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramAnonymousView)
          {
            if (VerticalGridPresenter.this.getOnItemViewClickedListener() != null) {
              VerticalGridPresenter.this.getOnItemViewClickedListener().onItemClicked(paramViewHolder.mHolder, paramViewHolder.mItem, null, null);
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
      if (VerticalGridPresenter.this.mShadowOverlayHelper != null) {
        VerticalGridPresenter.this.mShadowOverlayHelper.onViewCreated(paramViewHolder.itemView);
      }
    }
    
    public void onUnbind(ItemBridgeAdapter.ViewHolder paramViewHolder)
    {
      if (VerticalGridPresenter.this.getOnItemViewClickedListener() != null) {
        paramViewHolder.mHolder.view.setOnClickListener(null);
      }
    }
  }
  
  public static class ViewHolder
    extends Presenter.ViewHolder
  {
    final VerticalGridView mGridView;
    boolean mInitialized;
    ItemBridgeAdapter mItemBridgeAdapter;
    
    public ViewHolder(VerticalGridView paramVerticalGridView)
    {
      super();
      this.mGridView = paramVerticalGridView;
    }
    
    public VerticalGridView getGridView()
    {
      return this.mGridView;
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/VerticalGridPresenter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */