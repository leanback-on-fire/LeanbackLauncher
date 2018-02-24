package android.support.v17.leanback.widget;

import android.content.Context;
import android.support.v17.leanback.R.id;
import android.support.v17.leanback.R.layout;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public abstract class AbstractMediaListHeaderPresenter
  extends RowPresenter
{
  private int mBackgroundColor = 0;
  private boolean mBackgroundColorSet;
  private final Context mContext;
  
  public AbstractMediaListHeaderPresenter()
  {
    this.mContext = null;
    setHeaderPresenter(null);
  }
  
  public AbstractMediaListHeaderPresenter(Context paramContext, int paramInt)
  {
    this.mContext = new ContextThemeWrapper(paramContext.getApplicationContext(), paramInt);
    setHeaderPresenter(null);
  }
  
  protected RowPresenter.ViewHolder createRowViewHolder(ViewGroup paramViewGroup)
  {
    if (this.mContext != null) {}
    for (Context localContext = this.mContext;; localContext = paramViewGroup.getContext())
    {
      paramViewGroup = LayoutInflater.from(localContext).inflate(R.layout.lb_media_list_header, paramViewGroup, false);
      paramViewGroup.setFocusable(false);
      paramViewGroup.setFocusableInTouchMode(false);
      paramViewGroup = new ViewHolder(paramViewGroup);
      if (this.mBackgroundColorSet) {
        paramViewGroup.view.setBackgroundColor(this.mBackgroundColor);
      }
      return paramViewGroup;
    }
  }
  
  public boolean isUsingDefaultSelectEffect()
  {
    return false;
  }
  
  protected abstract void onBindMediaListHeaderViewHolder(ViewHolder paramViewHolder, Object paramObject);
  
  protected void onBindRowViewHolder(RowPresenter.ViewHolder paramViewHolder, Object paramObject)
  {
    super.onBindRowViewHolder(paramViewHolder, paramObject);
    onBindMediaListHeaderViewHolder((ViewHolder)paramViewHolder, paramObject);
  }
  
  public void setBackgroundColor(int paramInt)
  {
    this.mBackgroundColorSet = true;
    this.mBackgroundColor = paramInt;
  }
  
  public static class ViewHolder
    extends RowPresenter.ViewHolder
  {
    private final TextView mHeaderView;
    
    public ViewHolder(View paramView)
    {
      super();
      this.mHeaderView = ((TextView)paramView.findViewById(R.id.mediaListHeader));
    }
    
    public TextView getHeaderView()
    {
      return this.mHeaderView;
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/AbstractMediaListHeaderPresenter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */