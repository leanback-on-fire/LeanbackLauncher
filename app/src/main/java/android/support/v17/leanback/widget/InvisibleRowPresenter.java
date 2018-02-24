package android.support.v17.leanback.widget;

import android.support.annotation.RestrictTo;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

@RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
public class InvisibleRowPresenter
  extends RowPresenter
{
  public InvisibleRowPresenter()
  {
    setHeaderPresenter(null);
  }
  
  protected RowPresenter.ViewHolder createRowViewHolder(ViewGroup paramViewGroup)
  {
    paramViewGroup = new RelativeLayout(paramViewGroup.getContext());
    paramViewGroup.setLayoutParams(new RelativeLayout.LayoutParams(0, 0));
    return new RowPresenter.ViewHolder(paramViewGroup);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/InvisibleRowPresenter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */