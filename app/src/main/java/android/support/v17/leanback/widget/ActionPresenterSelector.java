package android.support.v17.leanback.widget;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v17.leanback.R.dimen;
import android.support.v17.leanback.R.id;
import android.support.v17.leanback.R.layout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

class ActionPresenterSelector
  extends PresenterSelector
{
  private final Presenter mOneLineActionPresenter = new OneLineActionPresenter();
  private final Presenter[] mPresenters = { this.mOneLineActionPresenter, this.mTwoLineActionPresenter };
  private final Presenter mTwoLineActionPresenter = new TwoLineActionPresenter();
  
  public Presenter getPresenter(Object paramObject)
  {
    if (TextUtils.isEmpty(((Action)paramObject).getLabel2())) {
      return this.mOneLineActionPresenter;
    }
    return this.mTwoLineActionPresenter;
  }
  
  public Presenter[] getPresenters()
  {
    return this.mPresenters;
  }
  
  static class ActionViewHolder
    extends Presenter.ViewHolder
  {
    Action mAction;
    Button mButton;
    int mLayoutDirection;
    
    public ActionViewHolder(View paramView, int paramInt)
    {
      super();
      this.mButton = ((Button)paramView.findViewById(R.id.lb_action_button));
      this.mLayoutDirection = paramInt;
    }
  }
  
  static class OneLineActionPresenter
    extends Presenter
  {
    public void onBindViewHolder(Presenter.ViewHolder paramViewHolder, Object paramObject)
    {
      paramObject = (Action)paramObject;
      paramViewHolder = (ActionPresenterSelector.ActionViewHolder)paramViewHolder;
      paramViewHolder.mAction = ((Action)paramObject);
      paramViewHolder.mButton.setText(((Action)paramObject).getLabel1());
    }
    
    public Presenter.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup)
    {
      return new ActionPresenterSelector.ActionViewHolder(LayoutInflater.from(paramViewGroup.getContext()).inflate(R.layout.lb_action_1_line, paramViewGroup, false), paramViewGroup.getLayoutDirection());
    }
    
    public void onUnbindViewHolder(Presenter.ViewHolder paramViewHolder)
    {
      ((ActionPresenterSelector.ActionViewHolder)paramViewHolder).mAction = null;
    }
  }
  
  static class TwoLineActionPresenter
    extends Presenter
  {
    public void onBindViewHolder(Presenter.ViewHolder paramViewHolder, Object paramObject)
    {
      paramObject = (Action)paramObject;
      paramViewHolder = (ActionPresenterSelector.ActionViewHolder)paramViewHolder;
      Object localObject = ((Action)paramObject).getIcon();
      paramViewHolder.mAction = ((Action)paramObject);
      int i;
      if (localObject != null)
      {
        i = paramViewHolder.view.getResources().getDimensionPixelSize(R.dimen.lb_action_with_icon_padding_start);
        int j = paramViewHolder.view.getResources().getDimensionPixelSize(R.dimen.lb_action_with_icon_padding_end);
        paramViewHolder.view.setPaddingRelative(i, 0, j, 0);
        if (paramViewHolder.mLayoutDirection != 1) {
          break label143;
        }
        paramViewHolder.mButton.setCompoundDrawablesWithIntrinsicBounds(null, null, (Drawable)localObject, null);
      }
      for (;;)
      {
        localObject = ((Action)paramObject).getLabel1();
        paramObject = ((Action)paramObject).getLabel2();
        if (!TextUtils.isEmpty((CharSequence)localObject)) {
          break label158;
        }
        paramViewHolder.mButton.setText((CharSequence)paramObject);
        return;
        i = paramViewHolder.view.getResources().getDimensionPixelSize(R.dimen.lb_action_padding_horizontal);
        paramViewHolder.view.setPaddingRelative(i, 0, i, 0);
        break;
        label143:
        paramViewHolder.mButton.setCompoundDrawablesWithIntrinsicBounds((Drawable)localObject, null, null, null);
      }
      label158:
      if (TextUtils.isEmpty((CharSequence)paramObject))
      {
        paramViewHolder.mButton.setText((CharSequence)localObject);
        return;
      }
      paramViewHolder.mButton.setText(localObject + "\n" + paramObject);
    }
    
    public Presenter.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup)
    {
      return new ActionPresenterSelector.ActionViewHolder(LayoutInflater.from(paramViewGroup.getContext()).inflate(R.layout.lb_action_2_lines, paramViewGroup, false), paramViewGroup.getLayoutDirection());
    }
    
    public void onUnbindViewHolder(Presenter.ViewHolder paramViewHolder)
    {
      paramViewHolder = (ActionPresenterSelector.ActionViewHolder)paramViewHolder;
      paramViewHolder.mButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
      paramViewHolder.view.setPadding(0, 0, 0, 0);
      paramViewHolder.mAction = null;
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/ActionPresenterSelector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */