package android.support.v17.leanback.widget;

import android.support.v17.leanback.R.id;
import android.support.v17.leanback.R.layout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ControlButtonPresenterSelector
  extends PresenterSelector
{
  private final Presenter[] mPresenters = { this.mPrimaryPresenter };
  private final Presenter mPrimaryPresenter = new ControlButtonPresenter(R.layout.lb_control_button_primary);
  private final Presenter mSecondaryPresenter = new ControlButtonPresenter(R.layout.lb_control_button_secondary);
  
  public Presenter getPresenter(Object paramObject)
  {
    return this.mPrimaryPresenter;
  }
  
  public Presenter[] getPresenters()
  {
    return this.mPresenters;
  }
  
  public Presenter getPrimaryPresenter()
  {
    return this.mPrimaryPresenter;
  }
  
  public Presenter getSecondaryPresenter()
  {
    return this.mSecondaryPresenter;
  }
  
  static class ActionViewHolder
    extends Presenter.ViewHolder
  {
    View mFocusableView;
    ImageView mIcon;
    TextView mLabel;
    
    public ActionViewHolder(View paramView)
    {
      super();
      this.mIcon = ((ImageView)paramView.findViewById(R.id.icon));
      this.mLabel = ((TextView)paramView.findViewById(R.id.label));
      this.mFocusableView = paramView.findViewById(R.id.button);
    }
  }
  
  static class ControlButtonPresenter
    extends Presenter
  {
    private int mLayoutResourceId;
    
    ControlButtonPresenter(int paramInt)
    {
      this.mLayoutResourceId = paramInt;
    }
    
    public void onBindViewHolder(Presenter.ViewHolder paramViewHolder, Object paramObject)
    {
      Action localAction = (Action)paramObject;
      paramObject = (ControlButtonPresenterSelector.ActionViewHolder)paramViewHolder;
      ((ControlButtonPresenterSelector.ActionViewHolder)paramObject).mIcon.setImageDrawable(localAction.getIcon());
      if (((ControlButtonPresenterSelector.ActionViewHolder)paramObject).mLabel != null)
      {
        if (localAction.getIcon() == null) {
          ((ControlButtonPresenterSelector.ActionViewHolder)paramObject).mLabel.setText(localAction.getLabel1());
        }
      }
      else {
        if (!TextUtils.isEmpty(localAction.getLabel2())) {
          break label104;
        }
      }
      label104:
      for (paramViewHolder = localAction.getLabel1();; paramViewHolder = localAction.getLabel2())
      {
        if (!TextUtils.equals(((ControlButtonPresenterSelector.ActionViewHolder)paramObject).mFocusableView.getContentDescription(), paramViewHolder))
        {
          ((ControlButtonPresenterSelector.ActionViewHolder)paramObject).mFocusableView.setContentDescription(paramViewHolder);
          ((ControlButtonPresenterSelector.ActionViewHolder)paramObject).mFocusableView.sendAccessibilityEvent(32768);
        }
        return;
        ((ControlButtonPresenterSelector.ActionViewHolder)paramObject).mLabel.setText(null);
        break;
      }
    }
    
    public Presenter.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup)
    {
      return new ControlButtonPresenterSelector.ActionViewHolder(LayoutInflater.from(paramViewGroup.getContext()).inflate(this.mLayoutResourceId, paramViewGroup, false));
    }
    
    public void onUnbindViewHolder(Presenter.ViewHolder paramViewHolder)
    {
      paramViewHolder = (ControlButtonPresenterSelector.ActionViewHolder)paramViewHolder;
      paramViewHolder.mIcon.setImageDrawable(null);
      if (paramViewHolder.mLabel != null) {
        paramViewHolder.mLabel.setText(null);
      }
      paramViewHolder.mFocusableView.setContentDescription(null);
    }
    
    public void setOnClickListener(Presenter.ViewHolder paramViewHolder, View.OnClickListener paramOnClickListener)
    {
      ((ControlButtonPresenterSelector.ActionViewHolder)paramViewHolder).mFocusableView.setOnClickListener(paramOnClickListener);
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/ControlButtonPresenterSelector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */