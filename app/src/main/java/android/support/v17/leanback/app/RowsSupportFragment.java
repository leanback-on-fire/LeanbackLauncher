package android.support.v17.leanback.app;

import android.animation.TimeAnimator;
import android.animation.TimeAnimator.TimeListener;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v17.leanback.R.id;
import android.support.v17.leanback.R.integer;
import android.support.v17.leanback.R.layout;
import android.support.v17.leanback.widget.BaseOnItemViewClickedListener;
import android.support.v17.leanback.widget.BaseOnItemViewSelectedListener;
import android.support.v17.leanback.widget.HorizontalGridView;
import android.support.v17.leanback.widget.ItemBridgeAdapter;
import android.support.v17.leanback.widget.ItemBridgeAdapter.AdapterListener;
import android.support.v17.leanback.widget.ItemBridgeAdapter.ViewHolder;
import android.support.v17.leanback.widget.ListRowPresenter.ViewHolder;
import android.support.v17.leanback.widget.ObjectAdapter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.OnItemViewSelectedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Presenter.ViewHolder;
import android.support.v17.leanback.widget.Presenter.ViewHolderTask;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v17.leanback.widget.RowPresenter.ViewHolder;
import android.support.v17.leanback.widget.VerticalGridView;
import android.support.v17.leanback.widget.ViewHolderTask;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.RecycledViewPool;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import java.util.ArrayList;

public class RowsSupportFragment
  extends BaseRowSupportFragment
  implements BrowseSupportFragment.MainFragmentRowsAdapterProvider, BrowseSupportFragment.MainFragmentAdapterProvider
{
  static final int ALIGN_TOP_NOT_SET = Integer.MIN_VALUE;
  static final boolean DEBUG = false;
  static final String TAG = "RowsSupportFragment";
  boolean mAfterEntranceTransition = true;
  private int mAlignedTop = Integer.MIN_VALUE;
  private final ItemBridgeAdapter.AdapterListener mBridgeAdapterListener = new ItemBridgeAdapter.AdapterListener()
  {
    public void onAddPresenter(Presenter paramAnonymousPresenter, int paramAnonymousInt)
    {
      if (RowsSupportFragment.this.mExternalAdapterListener != null) {
        RowsSupportFragment.this.mExternalAdapterListener.onAddPresenter(paramAnonymousPresenter, paramAnonymousInt);
      }
    }
    
    public void onAttachedToWindow(ItemBridgeAdapter.ViewHolder paramAnonymousViewHolder)
    {
      RowsSupportFragment.setRowViewExpanded(paramAnonymousViewHolder, RowsSupportFragment.this.mExpand);
      RowPresenter localRowPresenter = (RowPresenter)paramAnonymousViewHolder.getPresenter();
      localRowPresenter.setEntranceTransitionState(localRowPresenter.getRowViewHolder(paramAnonymousViewHolder.getViewHolder()), RowsSupportFragment.this.mAfterEntranceTransition);
      if (RowsSupportFragment.this.mExternalAdapterListener != null) {
        RowsSupportFragment.this.mExternalAdapterListener.onAttachedToWindow(paramAnonymousViewHolder);
      }
    }
    
    public void onBind(ItemBridgeAdapter.ViewHolder paramAnonymousViewHolder)
    {
      if (RowsSupportFragment.this.mExternalAdapterListener != null) {
        RowsSupportFragment.this.mExternalAdapterListener.onBind(paramAnonymousViewHolder);
      }
    }
    
    public void onCreate(ItemBridgeAdapter.ViewHolder paramAnonymousViewHolder)
    {
      VerticalGridView localVerticalGridView = RowsSupportFragment.this.getVerticalGridView();
      if (localVerticalGridView != null) {
        localVerticalGridView.setClipChildren(false);
      }
      RowsSupportFragment.this.setupSharedViewPool(paramAnonymousViewHolder);
      RowsSupportFragment.this.mViewsCreated = true;
      paramAnonymousViewHolder.setExtraObject(new RowsSupportFragment.RowViewHolderExtra(RowsSupportFragment.this, paramAnonymousViewHolder));
      RowsSupportFragment.setRowViewSelected(paramAnonymousViewHolder, false, true);
      if (RowsSupportFragment.this.mExternalAdapterListener != null) {
        RowsSupportFragment.this.mExternalAdapterListener.onCreate(paramAnonymousViewHolder);
      }
      paramAnonymousViewHolder = ((RowPresenter)paramAnonymousViewHolder.getPresenter()).getRowViewHolder(paramAnonymousViewHolder.getViewHolder());
      paramAnonymousViewHolder.setOnItemViewSelectedListener(RowsSupportFragment.this.mOnItemViewSelectedListener);
      paramAnonymousViewHolder.setOnItemViewClickedListener(RowsSupportFragment.this.mOnItemViewClickedListener);
    }
    
    public void onDetachedFromWindow(ItemBridgeAdapter.ViewHolder paramAnonymousViewHolder)
    {
      if (RowsSupportFragment.this.mSelectedViewHolder == paramAnonymousViewHolder)
      {
        RowsSupportFragment.setRowViewSelected(RowsSupportFragment.this.mSelectedViewHolder, false, true);
        RowsSupportFragment.this.mSelectedViewHolder = null;
      }
      if (RowsSupportFragment.this.mExternalAdapterListener != null) {
        RowsSupportFragment.this.mExternalAdapterListener.onDetachedFromWindow(paramAnonymousViewHolder);
      }
    }
    
    public void onUnbind(ItemBridgeAdapter.ViewHolder paramAnonymousViewHolder)
    {
      RowsSupportFragment.setRowViewSelected(paramAnonymousViewHolder, false, true);
      if (RowsSupportFragment.this.mExternalAdapterListener != null) {
        RowsSupportFragment.this.mExternalAdapterListener.onUnbind(paramAnonymousViewHolder);
      }
    }
  };
  boolean mExpand = true;
  ItemBridgeAdapter.AdapterListener mExternalAdapterListener;
  private MainFragmentAdapter mMainFragmentAdapter;
  private MainFragmentRowsAdapter mMainFragmentRowsAdapter;
  BaseOnItemViewClickedListener mOnItemViewClickedListener;
  BaseOnItemViewSelectedListener mOnItemViewSelectedListener;
  private ArrayList<Presenter> mPresenterMapper;
  private RecyclerView.RecycledViewPool mRecycledViewPool;
  int mSelectAnimatorDuration;
  Interpolator mSelectAnimatorInterpolator = new DecelerateInterpolator(2.0F);
  ItemBridgeAdapter.ViewHolder mSelectedViewHolder;
  private int mSubPosition;
  boolean mViewsCreated;
  
  private void freezeRows(boolean paramBoolean)
  {
    VerticalGridView localVerticalGridView = getVerticalGridView();
    if (localVerticalGridView != null)
    {
      int j = localVerticalGridView.getChildCount();
      int i = 0;
      while (i < j)
      {
        ItemBridgeAdapter.ViewHolder localViewHolder = (ItemBridgeAdapter.ViewHolder)localVerticalGridView.getChildViewHolder(localVerticalGridView.getChildAt(i));
        RowPresenter localRowPresenter = (RowPresenter)localViewHolder.getPresenter();
        localRowPresenter.freeze(localRowPresenter.getRowViewHolder(localViewHolder.getViewHolder()), paramBoolean);
        i += 1;
      }
    }
  }
  
  static RowPresenter.ViewHolder getRowViewHolder(ItemBridgeAdapter.ViewHolder paramViewHolder)
  {
    if (paramViewHolder == null) {
      return null;
    }
    return ((RowPresenter)paramViewHolder.getPresenter()).getRowViewHolder(paramViewHolder.getViewHolder());
  }
  
  static void setRowViewExpanded(ItemBridgeAdapter.ViewHolder paramViewHolder, boolean paramBoolean)
  {
    ((RowPresenter)paramViewHolder.getPresenter()).setRowViewExpanded(paramViewHolder.getViewHolder(), paramBoolean);
  }
  
  static void setRowViewSelected(ItemBridgeAdapter.ViewHolder paramViewHolder, boolean paramBoolean1, boolean paramBoolean2)
  {
    ((RowViewHolderExtra)paramViewHolder.getExtraObject()).animateSelect(paramBoolean1, paramBoolean2);
    ((RowPresenter)paramViewHolder.getPresenter()).setRowViewSelected(paramViewHolder.getViewHolder(), paramBoolean1);
  }
  
  @Deprecated
  public void enableRowScaling(boolean paramBoolean) {}
  
  protected VerticalGridView findGridViewFromRoot(View paramView)
  {
    return (VerticalGridView)paramView.findViewById(R.id.container_list);
  }
  
  public RowPresenter.ViewHolder findRowViewHolderByPosition(int paramInt)
  {
    if (this.mVerticalGridView == null) {
      return null;
    }
    return getRowViewHolder((ItemBridgeAdapter.ViewHolder)this.mVerticalGridView.findViewHolderForAdapterPosition(paramInt));
  }
  
  int getLayoutResourceId()
  {
    return R.layout.lb_rows_fragment;
  }
  
  public BrowseSupportFragment.MainFragmentAdapter getMainFragmentAdapter()
  {
    if (this.mMainFragmentAdapter == null) {
      this.mMainFragmentAdapter = new MainFragmentAdapter(this);
    }
    return this.mMainFragmentAdapter;
  }
  
  public BrowseSupportFragment.MainFragmentRowsAdapter getMainFragmentRowsAdapter()
  {
    if (this.mMainFragmentRowsAdapter == null) {
      this.mMainFragmentRowsAdapter = new MainFragmentRowsAdapter(this);
    }
    return this.mMainFragmentRowsAdapter;
  }
  
  public BaseOnItemViewClickedListener getOnItemViewClickedListener()
  {
    return this.mOnItemViewClickedListener;
  }
  
  public BaseOnItemViewSelectedListener getOnItemViewSelectedListener()
  {
    return this.mOnItemViewSelectedListener;
  }
  
  public RowPresenter.ViewHolder getRowViewHolder(int paramInt)
  {
    VerticalGridView localVerticalGridView = getVerticalGridView();
    if (localVerticalGridView == null) {
      return null;
    }
    return getRowViewHolder((ItemBridgeAdapter.ViewHolder)localVerticalGridView.findViewHolderForAdapterPosition(paramInt));
  }
  
  public boolean isScrolling()
  {
    if (getVerticalGridView() == null) {}
    while (getVerticalGridView().getScrollState() == 0) {
      return false;
    }
    return true;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mSelectAnimatorDuration = getResources().getInteger(R.integer.lb_browse_rows_anim_duration);
  }
  
  public void onDestroyView()
  {
    this.mViewsCreated = false;
    super.onDestroyView();
  }
  
  void onRowSelected(RecyclerView paramRecyclerView, RecyclerView.ViewHolder paramViewHolder, int paramInt1, int paramInt2)
  {
    boolean bool = true;
    if ((this.mSelectedViewHolder != paramViewHolder) || (this.mSubPosition != paramInt2))
    {
      this.mSubPosition = paramInt2;
      if (this.mSelectedViewHolder != null) {
        setRowViewSelected(this.mSelectedViewHolder, false, false);
      }
      this.mSelectedViewHolder = ((ItemBridgeAdapter.ViewHolder)paramViewHolder);
      if (this.mSelectedViewHolder != null) {
        setRowViewSelected(this.mSelectedViewHolder, true, false);
      }
    }
    if (this.mMainFragmentAdapter != null)
    {
      paramRecyclerView = this.mMainFragmentAdapter.getFragmentHost();
      if (paramInt1 > 0) {
        break label94;
      }
    }
    for (;;)
    {
      paramRecyclerView.showTitleView(bool);
      return;
      label94:
      bool = false;
    }
  }
  
  public void onTransitionEnd()
  {
    super.onTransitionEnd();
    freezeRows(false);
  }
  
  public boolean onTransitionPrepare()
  {
    boolean bool = super.onTransitionPrepare();
    if (bool) {
      freezeRows(true);
    }
    return bool;
  }
  
  public void onViewCreated(View paramView, Bundle paramBundle)
  {
    super.onViewCreated(paramView, paramBundle);
    getVerticalGridView().setItemAlignmentViewId(R.id.row_content);
    getVerticalGridView().setSaveChildrenPolicy(2);
    setAlignment(this.mAlignedTop);
    this.mRecycledViewPool = null;
    this.mPresenterMapper = null;
    if (this.mMainFragmentAdapter != null) {
      this.mMainFragmentAdapter.getFragmentHost().notifyViewCreated(this.mMainFragmentAdapter);
    }
  }
  
  public void setAlignment(int paramInt)
  {
    if (paramInt == Integer.MIN_VALUE) {}
    VerticalGridView localVerticalGridView;
    do
    {
      return;
      this.mAlignedTop = paramInt;
      localVerticalGridView = getVerticalGridView();
    } while (localVerticalGridView == null);
    localVerticalGridView.setItemAlignmentOffset(0);
    localVerticalGridView.setItemAlignmentOffsetPercent(-1.0F);
    localVerticalGridView.setItemAlignmentOffsetWithPadding(true);
    localVerticalGridView.setWindowAlignmentOffset(this.mAlignedTop);
    localVerticalGridView.setWindowAlignmentOffsetPercent(-1.0F);
    localVerticalGridView.setWindowAlignment(0);
  }
  
  public void setEntranceTransitionState(boolean paramBoolean)
  {
    this.mAfterEntranceTransition = paramBoolean;
    VerticalGridView localVerticalGridView = getVerticalGridView();
    if (localVerticalGridView != null)
    {
      int j = localVerticalGridView.getChildCount();
      int i = 0;
      while (i < j)
      {
        ItemBridgeAdapter.ViewHolder localViewHolder = (ItemBridgeAdapter.ViewHolder)localVerticalGridView.getChildViewHolder(localVerticalGridView.getChildAt(i));
        RowPresenter localRowPresenter = (RowPresenter)localViewHolder.getPresenter();
        localRowPresenter.setEntranceTransitionState(localRowPresenter.getRowViewHolder(localViewHolder.getViewHolder()), this.mAfterEntranceTransition);
        i += 1;
      }
    }
  }
  
  public void setExpand(boolean paramBoolean)
  {
    this.mExpand = paramBoolean;
    VerticalGridView localVerticalGridView = getVerticalGridView();
    if (localVerticalGridView != null)
    {
      int j = localVerticalGridView.getChildCount();
      int i = 0;
      while (i < j)
      {
        setRowViewExpanded((ItemBridgeAdapter.ViewHolder)localVerticalGridView.getChildViewHolder(localVerticalGridView.getChildAt(i)), this.mExpand);
        i += 1;
      }
    }
  }
  
  void setExternalAdapterListener(ItemBridgeAdapter.AdapterListener paramAdapterListener)
  {
    this.mExternalAdapterListener = paramAdapterListener;
  }
  
  public void setOnItemViewClickedListener(BaseOnItemViewClickedListener paramBaseOnItemViewClickedListener)
  {
    this.mOnItemViewClickedListener = paramBaseOnItemViewClickedListener;
    if (this.mViewsCreated) {
      throw new IllegalStateException("Item clicked listener must be set before views are created");
    }
  }
  
  public void setOnItemViewSelectedListener(BaseOnItemViewSelectedListener paramBaseOnItemViewSelectedListener)
  {
    this.mOnItemViewSelectedListener = paramBaseOnItemViewSelectedListener;
    paramBaseOnItemViewSelectedListener = getVerticalGridView();
    if (paramBaseOnItemViewSelectedListener != null)
    {
      int j = paramBaseOnItemViewSelectedListener.getChildCount();
      int i = 0;
      while (i < j)
      {
        getRowViewHolder((ItemBridgeAdapter.ViewHolder)paramBaseOnItemViewSelectedListener.getChildViewHolder(paramBaseOnItemViewSelectedListener.getChildAt(i))).setOnItemViewSelectedListener(this.mOnItemViewSelectedListener);
        i += 1;
      }
    }
  }
  
  public void setSelectedPosition(int paramInt, boolean paramBoolean, final Presenter.ViewHolderTask paramViewHolderTask)
  {
    VerticalGridView localVerticalGridView = getVerticalGridView();
    if (localVerticalGridView == null) {
      return;
    }
    ViewHolderTask local2 = null;
    if (paramViewHolderTask != null) {
      local2 = new ViewHolderTask()
      {
        public void run(final RecyclerView.ViewHolder paramAnonymousViewHolder)
        {
          paramAnonymousViewHolder.itemView.post(new Runnable()
          {
            public void run()
            {
              RowsSupportFragment.2.this.val$rowHolderTask.run(RowsSupportFragment.getRowViewHolder((ItemBridgeAdapter.ViewHolder)paramAnonymousViewHolder));
            }
          });
        }
      };
    }
    if (paramBoolean)
    {
      localVerticalGridView.setSelectedPositionSmooth(paramInt, local2);
      return;
    }
    localVerticalGridView.setSelectedPosition(paramInt, local2);
  }
  
  void setupSharedViewPool(ItemBridgeAdapter.ViewHolder paramViewHolder)
  {
    paramViewHolder = ((RowPresenter)paramViewHolder.getPresenter()).getRowViewHolder(paramViewHolder.getViewHolder());
    HorizontalGridView localHorizontalGridView;
    if ((paramViewHolder instanceof ListRowPresenter.ViewHolder))
    {
      localHorizontalGridView = ((ListRowPresenter.ViewHolder)paramViewHolder).getGridView();
      if (this.mRecycledViewPool != null) {
        break label69;
      }
      this.mRecycledViewPool = localHorizontalGridView.getRecycledViewPool();
    }
    for (;;)
    {
      paramViewHolder = ((ListRowPresenter.ViewHolder)paramViewHolder).getBridgeAdapter();
      if (this.mPresenterMapper != null) {
        break;
      }
      this.mPresenterMapper = paramViewHolder.getPresenterMapper();
      return;
      label69:
      localHorizontalGridView.setRecycledViewPool(this.mRecycledViewPool);
    }
    paramViewHolder.setPresenterMapper(this.mPresenterMapper);
  }
  
  void updateAdapter()
  {
    super.updateAdapter();
    this.mSelectedViewHolder = null;
    this.mViewsCreated = false;
    ItemBridgeAdapter localItemBridgeAdapter = getBridgeAdapter();
    if (localItemBridgeAdapter != null) {
      localItemBridgeAdapter.setAdapterListener(this.mBridgeAdapterListener);
    }
  }
  
  public static class MainFragmentAdapter
    extends BrowseSupportFragment.MainFragmentAdapter<RowsSupportFragment>
  {
    public MainFragmentAdapter(RowsSupportFragment paramRowsSupportFragment)
    {
      super();
      setScalingEnabled(true);
    }
    
    public boolean isScrolling()
    {
      return ((RowsSupportFragment)getFragment()).isScrolling();
    }
    
    public void onTransitionEnd()
    {
      ((RowsSupportFragment)getFragment()).onTransitionEnd();
    }
    
    public boolean onTransitionPrepare()
    {
      return ((RowsSupportFragment)getFragment()).onTransitionPrepare();
    }
    
    public void onTransitionStart()
    {
      ((RowsSupportFragment)getFragment()).onTransitionStart();
    }
    
    public void setAlignment(int paramInt)
    {
      ((RowsSupportFragment)getFragment()).setAlignment(paramInt);
    }
    
    public void setEntranceTransitionState(boolean paramBoolean)
    {
      ((RowsSupportFragment)getFragment()).setEntranceTransitionState(paramBoolean);
    }
    
    public void setExpand(boolean paramBoolean)
    {
      ((RowsSupportFragment)getFragment()).setExpand(paramBoolean);
    }
  }
  
  public static class MainFragmentRowsAdapter
    extends BrowseSupportFragment.MainFragmentRowsAdapter<RowsSupportFragment>
  {
    public MainFragmentRowsAdapter(RowsSupportFragment paramRowsSupportFragment)
    {
      super();
    }
    
    public RowPresenter.ViewHolder findRowViewHolderByPosition(int paramInt)
    {
      return ((RowsSupportFragment)getFragment()).findRowViewHolderByPosition(paramInt);
    }
    
    public int getSelectedPosition()
    {
      return ((RowsSupportFragment)getFragment()).getSelectedPosition();
    }
    
    public void setAdapter(ObjectAdapter paramObjectAdapter)
    {
      ((RowsSupportFragment)getFragment()).setAdapter(paramObjectAdapter);
    }
    
    public void setOnItemViewClickedListener(OnItemViewClickedListener paramOnItemViewClickedListener)
    {
      ((RowsSupportFragment)getFragment()).setOnItemViewClickedListener(paramOnItemViewClickedListener);
    }
    
    public void setOnItemViewSelectedListener(OnItemViewSelectedListener paramOnItemViewSelectedListener)
    {
      ((RowsSupportFragment)getFragment()).setOnItemViewSelectedListener(paramOnItemViewSelectedListener);
    }
    
    public void setSelectedPosition(int paramInt, boolean paramBoolean)
    {
      ((RowsSupportFragment)getFragment()).setSelectedPosition(paramInt, paramBoolean);
    }
    
    public void setSelectedPosition(int paramInt, boolean paramBoolean, Presenter.ViewHolderTask paramViewHolderTask)
    {
      ((RowsSupportFragment)getFragment()).setSelectedPosition(paramInt, paramBoolean, paramViewHolderTask);
    }
  }
  
  final class RowViewHolderExtra
    implements TimeAnimator.TimeListener
  {
    final RowPresenter mRowPresenter;
    final Presenter.ViewHolder mRowViewHolder;
    final TimeAnimator mSelectAnimator = new TimeAnimator();
    int mSelectAnimatorDurationInUse;
    Interpolator mSelectAnimatorInterpolatorInUse;
    float mSelectLevelAnimDelta;
    float mSelectLevelAnimStart;
    
    RowViewHolderExtra(ItemBridgeAdapter.ViewHolder paramViewHolder)
    {
      this.mRowPresenter = ((RowPresenter)paramViewHolder.getPresenter());
      this.mRowViewHolder = paramViewHolder.getViewHolder();
      this.mSelectAnimator.setTimeListener(this);
    }
    
    void animateSelect(boolean paramBoolean1, boolean paramBoolean2)
    {
      this.mSelectAnimator.end();
      float f;
      if (paramBoolean1)
      {
        f = 1.0F;
        if (!paramBoolean2) {
          break label35;
        }
        this.mRowPresenter.setSelectLevel(this.mRowViewHolder, f);
      }
      label35:
      while (this.mRowPresenter.getSelectLevel(this.mRowViewHolder) == f)
      {
        return;
        f = 0.0F;
        break;
      }
      this.mSelectAnimatorDurationInUse = RowsSupportFragment.this.mSelectAnimatorDuration;
      this.mSelectAnimatorInterpolatorInUse = RowsSupportFragment.this.mSelectAnimatorInterpolator;
      this.mSelectLevelAnimStart = this.mRowPresenter.getSelectLevel(this.mRowViewHolder);
      this.mSelectLevelAnimDelta = (f - this.mSelectLevelAnimStart);
      this.mSelectAnimator.start();
    }
    
    public void onTimeUpdate(TimeAnimator paramTimeAnimator, long paramLong1, long paramLong2)
    {
      if (this.mSelectAnimator.isRunning()) {
        updateSelect(paramLong1, paramLong2);
      }
    }
    
    void updateSelect(long paramLong1, long paramLong2)
    {
      float f1;
      if (paramLong1 >= this.mSelectAnimatorDurationInUse)
      {
        f1 = 1.0F;
        this.mSelectAnimator.end();
      }
      for (;;)
      {
        float f2 = f1;
        if (this.mSelectAnimatorInterpolatorInUse != null) {
          f2 = this.mSelectAnimatorInterpolatorInUse.getInterpolation(f1);
        }
        f1 = this.mSelectLevelAnimStart;
        float f3 = this.mSelectLevelAnimDelta;
        this.mRowPresenter.setSelectLevel(this.mRowViewHolder, f1 + f3 * f2);
        return;
        f1 = (float)(paramLong1 / this.mSelectAnimatorDurationInUse);
      }
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/app/RowsSupportFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */