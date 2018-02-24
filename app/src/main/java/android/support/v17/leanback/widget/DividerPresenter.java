package android.support.v17.leanback.widget;

import android.support.annotation.RestrictTo;
import android.support.v17.leanback.R.layout;
import android.view.LayoutInflater;
import android.view.ViewGroup;

public class DividerPresenter
  extends Presenter
{
  private final int mLayoutResourceId;
  
  public DividerPresenter()
  {
    this(R.layout.lb_divider);
  }
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  public DividerPresenter(int paramInt)
  {
    this.mLayoutResourceId = paramInt;
  }
  
  public void onBindViewHolder(Presenter.ViewHolder paramViewHolder, Object paramObject) {}
  
  public Presenter.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup)
  {
    return new Presenter.ViewHolder(LayoutInflater.from(paramViewGroup.getContext()).inflate(this.mLayoutResourceId, paramViewGroup, false));
  }
  
  public void onUnbindViewHolder(Presenter.ViewHolder paramViewHolder) {}
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/DividerPresenter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */