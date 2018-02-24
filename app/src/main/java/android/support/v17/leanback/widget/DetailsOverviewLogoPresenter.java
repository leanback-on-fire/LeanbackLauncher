package android.support.v17.leanback.widget;

import android.graphics.drawable.Drawable;
import android.support.v17.leanback.R.layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

public class DetailsOverviewLogoPresenter
  extends Presenter
{
  public boolean isBoundToImage(ViewHolder paramViewHolder, DetailsOverviewRow paramDetailsOverviewRow)
  {
    return (paramDetailsOverviewRow != null) && (paramDetailsOverviewRow.getImageDrawable() != null);
  }
  
  public void onBindViewHolder(Presenter.ViewHolder paramViewHolder, Object paramObject)
  {
    paramObject = (DetailsOverviewRow)paramObject;
    ImageView localImageView = (ImageView)paramViewHolder.view;
    localImageView.setImageDrawable(((DetailsOverviewRow)paramObject).getImageDrawable());
    if (isBoundToImage((ViewHolder)paramViewHolder, (DetailsOverviewRow)paramObject))
    {
      paramViewHolder = (ViewHolder)paramViewHolder;
      if (paramViewHolder.isSizeFromDrawableIntrinsic())
      {
        ViewGroup.LayoutParams localLayoutParams = localImageView.getLayoutParams();
        localLayoutParams.width = ((DetailsOverviewRow)paramObject).getImageDrawable().getIntrinsicWidth();
        localLayoutParams.height = ((DetailsOverviewRow)paramObject).getImageDrawable().getIntrinsicHeight();
        if ((localImageView.getMaxWidth() > 0) || (localImageView.getMaxHeight() > 0))
        {
          float f2 = 1.0F;
          float f1 = f2;
          if (localImageView.getMaxWidth() > 0)
          {
            f1 = f2;
            if (localLayoutParams.width > localImageView.getMaxWidth()) {
              f1 = localImageView.getMaxWidth() / localLayoutParams.width;
            }
          }
          float f3 = 1.0F;
          f2 = f3;
          if (localImageView.getMaxHeight() > 0)
          {
            f2 = f3;
            if (localLayoutParams.height > localImageView.getMaxHeight()) {
              f2 = localImageView.getMaxHeight() / localLayoutParams.height;
            }
          }
          f1 = Math.min(f1, f2);
          localLayoutParams.width = ((int)(localLayoutParams.width * f1));
          localLayoutParams.height = ((int)(localLayoutParams.height * f1));
        }
        localImageView.setLayoutParams(localLayoutParams);
      }
      paramViewHolder.mParentPresenter.notifyOnBindLogo(paramViewHolder.mParentViewHolder);
    }
  }
  
  public View onCreateView(ViewGroup paramViewGroup)
  {
    return LayoutInflater.from(paramViewGroup.getContext()).inflate(R.layout.lb_fullwidth_details_overview_logo, paramViewGroup, false);
  }
  
  public Presenter.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup)
  {
    Object localObject = onCreateView(paramViewGroup);
    paramViewGroup = new ViewHolder((View)localObject);
    localObject = ((View)localObject).getLayoutParams();
    if ((((ViewGroup.LayoutParams)localObject).width == -2) && (((ViewGroup.LayoutParams)localObject).height == -2)) {}
    for (boolean bool = true;; bool = false)
    {
      paramViewGroup.setSizeFromDrawableIntrinsic(bool);
      return paramViewGroup;
    }
  }
  
  public void onUnbindViewHolder(Presenter.ViewHolder paramViewHolder) {}
  
  public void setContext(ViewHolder paramViewHolder, FullWidthDetailsOverviewRowPresenter.ViewHolder paramViewHolder1, FullWidthDetailsOverviewRowPresenter paramFullWidthDetailsOverviewRowPresenter)
  {
    paramViewHolder.mParentViewHolder = paramViewHolder1;
    paramViewHolder.mParentPresenter = paramFullWidthDetailsOverviewRowPresenter;
  }
  
  public static class ViewHolder
    extends Presenter.ViewHolder
  {
    protected FullWidthDetailsOverviewRowPresenter mParentPresenter;
    protected FullWidthDetailsOverviewRowPresenter.ViewHolder mParentViewHolder;
    private boolean mSizeFromDrawableIntrinsic;
    
    public ViewHolder(View paramView)
    {
      super();
    }
    
    public FullWidthDetailsOverviewRowPresenter getParentPresenter()
    {
      return this.mParentPresenter;
    }
    
    public FullWidthDetailsOverviewRowPresenter.ViewHolder getParentViewHolder()
    {
      return this.mParentViewHolder;
    }
    
    public boolean isSizeFromDrawableIntrinsic()
    {
      return this.mSizeFromDrawableIntrinsic;
    }
    
    public void setSizeFromDrawableIntrinsic(boolean paramBoolean)
    {
      this.mSizeFromDrawableIntrinsic = paramBoolean;
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/DetailsOverviewLogoPresenter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */