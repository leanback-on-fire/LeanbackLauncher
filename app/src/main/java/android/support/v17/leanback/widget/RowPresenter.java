package android.support.v17.leanback.widget;

import android.graphics.Paint;
import android.support.v17.leanback.graphics.ColorOverlayDimmer;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;

public abstract class RowPresenter
  extends Presenter
{
  public static final int SYNC_ACTIVATED_CUSTOM = 0;
  public static final int SYNC_ACTIVATED_TO_EXPANDED = 1;
  public static final int SYNC_ACTIVATED_TO_EXPANDED_AND_SELECTED = 3;
  public static final int SYNC_ACTIVATED_TO_SELECTED = 2;
  private RowHeaderPresenter mHeaderPresenter = new RowHeaderPresenter();
  boolean mSelectEffectEnabled = true;
  int mSyncActivatePolicy = 1;
  
  public RowPresenter()
  {
    this.mHeaderPresenter.setNullItemVisibilityGone(true);
  }
  
  private void updateActivateStatus(ViewHolder paramViewHolder, View paramView)
  {
    switch (this.mSyncActivatePolicy)
    {
    default: 
    case 1: 
    case 2: 
      for (;;)
      {
        paramViewHolder.syncActivatedStatus(paramView);
        return;
        paramViewHolder.setActivated(paramViewHolder.isExpanded());
        continue;
        paramViewHolder.setActivated(paramViewHolder.isSelected());
      }
    }
    if ((paramViewHolder.isExpanded()) && (paramViewHolder.isSelected())) {}
    for (boolean bool = true;; bool = false)
    {
      paramViewHolder.setActivated(bool);
      break;
    }
  }
  
  private void updateHeaderViewVisibility(ViewHolder paramViewHolder)
  {
    if ((this.mHeaderPresenter != null) && (paramViewHolder.mHeaderViewHolder != null)) {
      ((RowContainerView)paramViewHolder.mContainerViewHolder.view).showHeader(paramViewHolder.isExpanded());
    }
  }
  
  protected abstract ViewHolder createRowViewHolder(ViewGroup paramViewGroup);
  
  protected void dispatchItemSelectedListener(ViewHolder paramViewHolder, boolean paramBoolean)
  {
    if ((paramBoolean) && (paramViewHolder.mOnItemViewSelectedListener != null)) {
      paramViewHolder.mOnItemViewSelectedListener.onItemSelected(null, null, paramViewHolder, paramViewHolder.getRowObject());
    }
  }
  
  public void freeze(ViewHolder paramViewHolder, boolean paramBoolean) {}
  
  public final RowHeaderPresenter getHeaderPresenter()
  {
    return this.mHeaderPresenter;
  }
  
  public final ViewHolder getRowViewHolder(Presenter.ViewHolder paramViewHolder)
  {
    if ((paramViewHolder instanceof ContainerViewHolder)) {
      return ((ContainerViewHolder)paramViewHolder).mRowViewHolder;
    }
    return (ViewHolder)paramViewHolder;
  }
  
  public final boolean getSelectEffectEnabled()
  {
    return this.mSelectEffectEnabled;
  }
  
  public final float getSelectLevel(Presenter.ViewHolder paramViewHolder)
  {
    return getRowViewHolder(paramViewHolder).mSelectLevel;
  }
  
  public final int getSyncActivatePolicy()
  {
    return this.mSyncActivatePolicy;
  }
  
  protected void initializeRowViewHolder(ViewHolder paramViewHolder)
  {
    paramViewHolder.mInitialzed = true;
    if (!isClippingChildren())
    {
      if ((paramViewHolder.view instanceof ViewGroup)) {
        ((ViewGroup)paramViewHolder.view).setClipChildren(false);
      }
      if (paramViewHolder.mContainerViewHolder != null) {
        ((ViewGroup)paramViewHolder.mContainerViewHolder.view).setClipChildren(false);
      }
    }
  }
  
  protected boolean isClippingChildren()
  {
    return false;
  }
  
  public boolean isUsingDefaultSelectEffect()
  {
    return true;
  }
  
  final boolean needsDefaultSelectEffect()
  {
    return (isUsingDefaultSelectEffect()) && (getSelectEffectEnabled());
  }
  
  final boolean needsRowContainerView()
  {
    return (this.mHeaderPresenter != null) || (needsDefaultSelectEffect());
  }
  
  protected void onBindRowViewHolder(ViewHolder paramViewHolder, Object paramObject)
  {
    paramViewHolder.mRowObject = paramObject;
    if ((paramObject instanceof Row)) {}
    for (Row localRow = (Row)paramObject;; localRow = null)
    {
      paramViewHolder.mRow = localRow;
      if ((paramViewHolder.mHeaderViewHolder != null) && (paramViewHolder.getRow() != null)) {
        this.mHeaderPresenter.onBindViewHolder(paramViewHolder.mHeaderViewHolder, paramObject);
      }
      return;
    }
  }
  
  public final void onBindViewHolder(Presenter.ViewHolder paramViewHolder, Object paramObject)
  {
    onBindRowViewHolder(getRowViewHolder(paramViewHolder), paramObject);
  }
  
  public final Presenter.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup)
  {
    ViewHolder localViewHolder = createRowViewHolder(paramViewGroup);
    localViewHolder.mInitialzed = false;
    if (needsRowContainerView())
    {
      paramViewGroup = new RowContainerView(paramViewGroup.getContext());
      if (this.mHeaderPresenter != null) {
        localViewHolder.mHeaderViewHolder = ((RowHeaderPresenter.ViewHolder)this.mHeaderPresenter.onCreateViewHolder((ViewGroup)localViewHolder.view));
      }
    }
    for (paramViewGroup = new ContainerViewHolder(paramViewGroup, localViewHolder);; paramViewGroup = localViewHolder)
    {
      initializeRowViewHolder(localViewHolder);
      if (localViewHolder.mInitialzed) {
        break;
      }
      throw new RuntimeException("super.initializeRowViewHolder() must be called");
    }
    return paramViewGroup;
  }
  
  protected void onRowViewAttachedToWindow(ViewHolder paramViewHolder)
  {
    if (paramViewHolder.mHeaderViewHolder != null) {
      this.mHeaderPresenter.onViewAttachedToWindow(paramViewHolder.mHeaderViewHolder);
    }
  }
  
  protected void onRowViewDetachedFromWindow(ViewHolder paramViewHolder)
  {
    if (paramViewHolder.mHeaderViewHolder != null) {
      this.mHeaderPresenter.onViewDetachedFromWindow(paramViewHolder.mHeaderViewHolder);
    }
    cancelAnimationsRecursive(paramViewHolder.view);
  }
  
  protected void onRowViewExpanded(ViewHolder paramViewHolder, boolean paramBoolean)
  {
    updateHeaderViewVisibility(paramViewHolder);
    updateActivateStatus(paramViewHolder, paramViewHolder.view);
  }
  
  protected void onRowViewSelected(ViewHolder paramViewHolder, boolean paramBoolean)
  {
    dispatchItemSelectedListener(paramViewHolder, paramBoolean);
    updateHeaderViewVisibility(paramViewHolder);
    updateActivateStatus(paramViewHolder, paramViewHolder.view);
  }
  
  protected void onSelectLevelChanged(ViewHolder paramViewHolder)
  {
    if (getSelectEffectEnabled())
    {
      paramViewHolder.mColorDimmer.setActiveLevel(paramViewHolder.mSelectLevel);
      if (paramViewHolder.mHeaderViewHolder != null) {
        this.mHeaderPresenter.setSelectLevel(paramViewHolder.mHeaderViewHolder, paramViewHolder.mSelectLevel);
      }
      if (isUsingDefaultSelectEffect()) {
        ((RowContainerView)paramViewHolder.mContainerViewHolder.view).setForegroundColor(paramViewHolder.mColorDimmer.getPaint().getColor());
      }
    }
  }
  
  protected void onUnbindRowViewHolder(ViewHolder paramViewHolder)
  {
    if (paramViewHolder.mHeaderViewHolder != null) {
      this.mHeaderPresenter.onUnbindViewHolder(paramViewHolder.mHeaderViewHolder);
    }
    paramViewHolder.mRow = null;
    paramViewHolder.mRowObject = null;
  }
  
  public final void onUnbindViewHolder(Presenter.ViewHolder paramViewHolder)
  {
    onUnbindRowViewHolder(getRowViewHolder(paramViewHolder));
  }
  
  public final void onViewAttachedToWindow(Presenter.ViewHolder paramViewHolder)
  {
    onRowViewAttachedToWindow(getRowViewHolder(paramViewHolder));
  }
  
  public final void onViewDetachedFromWindow(Presenter.ViewHolder paramViewHolder)
  {
    onRowViewDetachedFromWindow(getRowViewHolder(paramViewHolder));
  }
  
  public void setEntranceTransitionState(ViewHolder paramViewHolder, boolean paramBoolean)
  {
    if ((paramViewHolder.mHeaderViewHolder != null) && (paramViewHolder.mHeaderViewHolder.view.getVisibility() != 8))
    {
      paramViewHolder = paramViewHolder.mHeaderViewHolder.view;
      if (!paramBoolean) {
        break label42;
      }
    }
    label42:
    for (int i = 0;; i = 4)
    {
      paramViewHolder.setVisibility(i);
      return;
    }
  }
  
  public final void setHeaderPresenter(RowHeaderPresenter paramRowHeaderPresenter)
  {
    this.mHeaderPresenter = paramRowHeaderPresenter;
  }
  
  public final void setRowViewExpanded(Presenter.ViewHolder paramViewHolder, boolean paramBoolean)
  {
    paramViewHolder = getRowViewHolder(paramViewHolder);
    paramViewHolder.mExpanded = paramBoolean;
    onRowViewExpanded(paramViewHolder, paramBoolean);
  }
  
  public final void setRowViewSelected(Presenter.ViewHolder paramViewHolder, boolean paramBoolean)
  {
    paramViewHolder = getRowViewHolder(paramViewHolder);
    paramViewHolder.mSelected = paramBoolean;
    onRowViewSelected(paramViewHolder, paramBoolean);
  }
  
  public final void setSelectEffectEnabled(boolean paramBoolean)
  {
    this.mSelectEffectEnabled = paramBoolean;
  }
  
  public final void setSelectLevel(Presenter.ViewHolder paramViewHolder, float paramFloat)
  {
    paramViewHolder = getRowViewHolder(paramViewHolder);
    paramViewHolder.mSelectLevel = paramFloat;
    onSelectLevelChanged(paramViewHolder);
  }
  
  public final void setSyncActivatePolicy(int paramInt)
  {
    this.mSyncActivatePolicy = paramInt;
  }
  
  static class ContainerViewHolder
    extends Presenter.ViewHolder
  {
    final RowPresenter.ViewHolder mRowViewHolder;
    
    public ContainerViewHolder(RowContainerView paramRowContainerView, RowPresenter.ViewHolder paramViewHolder)
    {
      super();
      paramRowContainerView.addRowView(paramViewHolder.view);
      if (paramViewHolder.mHeaderViewHolder != null) {
        paramRowContainerView.addHeaderView(paramViewHolder.mHeaderViewHolder.view);
      }
      this.mRowViewHolder = paramViewHolder;
      this.mRowViewHolder.mContainerViewHolder = this;
    }
  }
  
  public static class ViewHolder
    extends Presenter.ViewHolder
  {
    private static final int ACTIVATED = 1;
    private static final int ACTIVATED_NOT_ASSIGNED = 0;
    private static final int NOT_ACTIVATED = 2;
    int mActivated = 0;
    protected final ColorOverlayDimmer mColorDimmer;
    RowPresenter.ContainerViewHolder mContainerViewHolder;
    boolean mExpanded;
    RowHeaderPresenter.ViewHolder mHeaderViewHolder;
    boolean mInitialzed;
    private BaseOnItemViewClickedListener mOnItemViewClickedListener;
    BaseOnItemViewSelectedListener mOnItemViewSelectedListener;
    private View.OnKeyListener mOnKeyListener;
    Row mRow;
    Object mRowObject;
    float mSelectLevel = 0.0F;
    boolean mSelected;
    
    public ViewHolder(View paramView)
    {
      super();
      this.mColorDimmer = ColorOverlayDimmer.createDefault(paramView.getContext());
    }
    
    public final RowHeaderPresenter.ViewHolder getHeaderViewHolder()
    {
      return this.mHeaderViewHolder;
    }
    
    public final BaseOnItemViewClickedListener getOnItemViewClickedListener()
    {
      return this.mOnItemViewClickedListener;
    }
    
    public final BaseOnItemViewSelectedListener getOnItemViewSelectedListener()
    {
      return this.mOnItemViewSelectedListener;
    }
    
    public View.OnKeyListener getOnKeyListener()
    {
      return this.mOnKeyListener;
    }
    
    public final Row getRow()
    {
      return this.mRow;
    }
    
    public final Object getRowObject()
    {
      return this.mRowObject;
    }
    
    public final float getSelectLevel()
    {
      return this.mSelectLevel;
    }
    
    public Object getSelectedItem()
    {
      return null;
    }
    
    public Presenter.ViewHolder getSelectedItemViewHolder()
    {
      return null;
    }
    
    public final boolean isExpanded()
    {
      return this.mExpanded;
    }
    
    public final boolean isSelected()
    {
      return this.mSelected;
    }
    
    public final void setActivated(boolean paramBoolean)
    {
      if (paramBoolean) {}
      for (int i = 1;; i = 2)
      {
        this.mActivated = i;
        return;
      }
    }
    
    public final void setOnItemViewClickedListener(BaseOnItemViewClickedListener paramBaseOnItemViewClickedListener)
    {
      this.mOnItemViewClickedListener = paramBaseOnItemViewClickedListener;
    }
    
    public final void setOnItemViewSelectedListener(BaseOnItemViewSelectedListener paramBaseOnItemViewSelectedListener)
    {
      this.mOnItemViewSelectedListener = paramBaseOnItemViewSelectedListener;
    }
    
    public void setOnKeyListener(View.OnKeyListener paramOnKeyListener)
    {
      this.mOnKeyListener = paramOnKeyListener;
    }
    
    public final void syncActivatedStatus(View paramView)
    {
      if (this.mActivated == 1) {
        paramView.setActivated(true);
      }
      while (this.mActivated != 2) {
        return;
      }
      paramView.setActivated(false);
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/RowPresenter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */