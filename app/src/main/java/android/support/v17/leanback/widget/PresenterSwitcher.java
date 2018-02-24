package android.support.v17.leanback.widget;

import android.view.View;
import android.view.ViewGroup;

public abstract class PresenterSwitcher
{
  private Presenter mCurrentPresenter;
  private Presenter.ViewHolder mCurrentViewHolder;
  private ViewGroup mParent;
  private PresenterSelector mPresenterSelector;
  
  private void showView(boolean paramBoolean)
  {
    if (this.mCurrentViewHolder != null) {
      showView(this.mCurrentViewHolder.view, paramBoolean);
    }
  }
  
  private void switchView(Object paramObject)
  {
    Presenter localPresenter = this.mPresenterSelector.getPresenter(paramObject);
    if (localPresenter != this.mCurrentPresenter)
    {
      showView(false);
      clear();
      this.mCurrentPresenter = localPresenter;
      if (this.mCurrentPresenter == null) {
        return;
      }
      this.mCurrentViewHolder = this.mCurrentPresenter.onCreateViewHolder(this.mParent);
      insertView(this.mCurrentViewHolder.view);
    }
    for (;;)
    {
      this.mCurrentPresenter.onBindViewHolder(this.mCurrentViewHolder, paramObject);
      onViewSelected(this.mCurrentViewHolder.view);
      return;
      if (this.mCurrentPresenter == null) {
        break;
      }
      this.mCurrentPresenter.onUnbindViewHolder(this.mCurrentViewHolder);
    }
  }
  
  public void clear()
  {
    if (this.mCurrentPresenter != null)
    {
      this.mCurrentPresenter.onUnbindViewHolder(this.mCurrentViewHolder);
      this.mParent.removeView(this.mCurrentViewHolder.view);
      this.mCurrentViewHolder = null;
      this.mCurrentPresenter = null;
    }
  }
  
  public final ViewGroup getParentViewGroup()
  {
    return this.mParent;
  }
  
  public void init(ViewGroup paramViewGroup, PresenterSelector paramPresenterSelector)
  {
    clear();
    this.mParent = paramViewGroup;
    this.mPresenterSelector = paramPresenterSelector;
  }
  
  protected abstract void insertView(View paramView);
  
  protected void onViewSelected(View paramView) {}
  
  public void select(Object paramObject)
  {
    switchView(paramObject);
    showView(true);
  }
  
  protected void showView(View paramView, boolean paramBoolean)
  {
    if (paramBoolean) {}
    for (int i = 0;; i = 8)
    {
      paramView.setVisibility(i);
      return;
    }
  }
  
  public void unselect()
  {
    showView(false);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/PresenterSwitcher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */