package android.support.v17.leanback.widget;

import android.support.v17.leanback.R.id;
import android.support.v17.leanback.R.layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

class MediaItemActionPresenter
  extends Presenter
{
  public void onBindViewHolder(Presenter.ViewHolder paramViewHolder, Object paramObject)
  {
    paramViewHolder = (ViewHolder)paramViewHolder;
    paramObject = (MultiActionsProvider.MultiAction)paramObject;
    paramViewHolder.getIcon().setImageDrawable(((MultiActionsProvider.MultiAction)paramObject).getCurrentDrawable());
  }
  
  public Presenter.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup)
  {
    return new ViewHolder(LayoutInflater.from(paramViewGroup.getContext()).inflate(R.layout.lb_row_media_item_action, paramViewGroup, false));
  }
  
  public void onUnbindViewHolder(Presenter.ViewHolder paramViewHolder) {}
  
  static class ViewHolder
    extends Presenter.ViewHolder
  {
    final ImageView mIcon;
    
    public ViewHolder(View paramView)
    {
      super();
      this.mIcon = ((ImageView)paramView.findViewById(R.id.actionIcon));
    }
    
    public ImageView getIcon()
    {
      return this.mIcon;
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/MediaItemActionPresenter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */