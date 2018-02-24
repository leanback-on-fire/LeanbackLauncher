package android.support.v17.leanback.widget;

import android.content.Context;
import android.content.res.Resources;
import android.support.v17.leanback.R.dimen;
import android.support.v17.leanback.R.id;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

class ControlBarPresenter
  extends Presenter
{
  static final int MAX_CONTROLS = 7;
  private static int sChildMarginDefault;
  private static int sControlIconWidth;
  boolean mDefaultFocusToMiddle = true;
  private int mLayoutResourceId;
  OnControlClickedListener mOnControlClickedListener;
  OnControlSelectedListener mOnControlSelectedListener;
  
  public ControlBarPresenter(int paramInt)
  {
    this.mLayoutResourceId = paramInt;
  }
  
  int getChildMarginDefault(Context paramContext)
  {
    if (sChildMarginDefault == 0) {
      sChildMarginDefault = paramContext.getResources().getDimensionPixelSize(R.dimen.lb_playback_controls_child_margin_default);
    }
    return sChildMarginDefault;
  }
  
  int getControlIconWidth(Context paramContext)
  {
    if (sControlIconWidth == 0) {
      sControlIconWidth = paramContext.getResources().getDimensionPixelSize(R.dimen.lb_control_icon_width);
    }
    return sControlIconWidth;
  }
  
  public int getLayoutResourceId()
  {
    return this.mLayoutResourceId;
  }
  
  public OnControlSelectedListener getOnItemControlListener()
  {
    return this.mOnControlSelectedListener;
  }
  
  public OnControlClickedListener getOnItemViewClickedListener()
  {
    return this.mOnControlClickedListener;
  }
  
  public void onBindViewHolder(Presenter.ViewHolder paramViewHolder, Object paramObject)
  {
    paramViewHolder = (ViewHolder)paramViewHolder;
    paramObject = (BoundData)paramObject;
    if (paramViewHolder.mAdapter != ((BoundData)paramObject).adapter)
    {
      paramViewHolder.mAdapter = ((BoundData)paramObject).adapter;
      if (paramViewHolder.mAdapter != null) {
        paramViewHolder.mAdapter.registerObserver(paramViewHolder.mDataObserver);
      }
    }
    paramViewHolder.mPresenter = ((BoundData)paramObject).presenter;
    paramViewHolder.mData = ((BoundData)paramObject);
    paramViewHolder.showControls(paramViewHolder.mPresenter);
  }
  
  public Presenter.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup)
  {
    return new ViewHolder(LayoutInflater.from(paramViewGroup.getContext()).inflate(getLayoutResourceId(), paramViewGroup, false));
  }
  
  public void onUnbindViewHolder(Presenter.ViewHolder paramViewHolder)
  {
    paramViewHolder = (ViewHolder)paramViewHolder;
    if (paramViewHolder.mAdapter != null)
    {
      paramViewHolder.mAdapter.unregisterObserver(paramViewHolder.mDataObserver);
      paramViewHolder.mAdapter = null;
    }
    paramViewHolder.mData = null;
  }
  
  public void setBackgroundColor(ViewHolder paramViewHolder, int paramInt)
  {
    paramViewHolder.mControlsContainer.setBackgroundColor(paramInt);
  }
  
  void setDefaultFocusToMiddle(boolean paramBoolean)
  {
    this.mDefaultFocusToMiddle = paramBoolean;
  }
  
  public void setOnControlClickedListener(OnControlClickedListener paramOnControlClickedListener)
  {
    this.mOnControlClickedListener = paramOnControlClickedListener;
  }
  
  public void setOnControlSelectedListener(OnControlSelectedListener paramOnControlSelectedListener)
  {
    this.mOnControlSelectedListener = paramOnControlSelectedListener;
  }
  
  static class BoundData
  {
    ObjectAdapter adapter;
    Presenter presenter;
  }
  
  static abstract interface OnControlClickedListener
  {
    public abstract void onControlClicked(Presenter.ViewHolder paramViewHolder, Object paramObject, ControlBarPresenter.BoundData paramBoundData);
  }
  
  static abstract interface OnControlSelectedListener
  {
    public abstract void onControlSelected(Presenter.ViewHolder paramViewHolder, Object paramObject, ControlBarPresenter.BoundData paramBoundData);
  }
  
  class ViewHolder
    extends Presenter.ViewHolder
  {
    ObjectAdapter mAdapter;
    ControlBar mControlBar;
    View mControlsContainer;
    ControlBarPresenter.BoundData mData;
    ObjectAdapter.DataObserver mDataObserver;
    Presenter mPresenter;
    SparseArray<Presenter.ViewHolder> mViewHolders = new SparseArray();
    
    ViewHolder(View paramView)
    {
      super();
      this.mControlsContainer = paramView.findViewById(R.id.controls_container);
      this.mControlBar = ((ControlBar)paramView.findViewById(R.id.control_bar));
      if (this.mControlBar == null) {
        throw new IllegalStateException("Couldn't find control_bar");
      }
      this.mControlBar.setDefaultFocusToMiddle(ControlBarPresenter.this.mDefaultFocusToMiddle);
      this.mControlBar.setOnChildFocusedListener(new ControlBar.OnChildFocusedListener()
      {
        public void onChildFocusedListener(View paramAnonymousView1, View paramAnonymousView2)
        {
          if (ControlBarPresenter.this.mOnControlSelectedListener == null) {}
          for (;;)
          {
            return;
            int i = 0;
            while (i < ControlBarPresenter.ViewHolder.this.mViewHolders.size())
            {
              if (((Presenter.ViewHolder)ControlBarPresenter.ViewHolder.this.mViewHolders.get(i)).view == paramAnonymousView1)
              {
                ControlBarPresenter.this.mOnControlSelectedListener.onControlSelected((Presenter.ViewHolder)ControlBarPresenter.ViewHolder.this.mViewHolders.get(i), ControlBarPresenter.ViewHolder.this.getDisplayedAdapter().get(i), ControlBarPresenter.ViewHolder.this.mData);
                return;
              }
              i += 1;
            }
          }
        }
      });
      this.mDataObserver = new ObjectAdapter.DataObserver()
      {
        public void onChanged()
        {
          if (ControlBarPresenter.ViewHolder.this.mAdapter == ControlBarPresenter.ViewHolder.this.getDisplayedAdapter()) {
            ControlBarPresenter.ViewHolder.this.showControls(ControlBarPresenter.ViewHolder.this.mPresenter);
          }
        }
        
        public void onItemRangeChanged(int paramAnonymousInt1, int paramAnonymousInt2)
        {
          if (ControlBarPresenter.ViewHolder.this.mAdapter == ControlBarPresenter.ViewHolder.this.getDisplayedAdapter())
          {
            int i = 0;
            while (i < paramAnonymousInt2)
            {
              ControlBarPresenter.ViewHolder.this.bindControlToAction(paramAnonymousInt1 + i, ControlBarPresenter.ViewHolder.this.mPresenter);
              i += 1;
            }
          }
        }
      };
    }
    
    private void bindControlToAction(final int paramInt, final ObjectAdapter paramObjectAdapter, Presenter paramPresenter)
    {
      Presenter.ViewHolder localViewHolder = (Presenter.ViewHolder)this.mViewHolders.get(paramInt);
      Object localObject = paramObjectAdapter.get(paramInt);
      paramObjectAdapter = localViewHolder;
      if (localViewHolder == null)
      {
        paramObjectAdapter = paramPresenter.onCreateViewHolder(this.mControlBar);
        this.mViewHolders.put(paramInt, paramObjectAdapter);
        paramPresenter.setOnClickListener(paramObjectAdapter, new View.OnClickListener()
        {
          public void onClick(View paramAnonymousView)
          {
            paramAnonymousView = ControlBarPresenter.ViewHolder.this.getDisplayedAdapter().get(paramInt);
            if (ControlBarPresenter.this.mOnControlClickedListener != null) {
              ControlBarPresenter.this.mOnControlClickedListener.onControlClicked(paramObjectAdapter, paramAnonymousView, ControlBarPresenter.ViewHolder.this.mData);
            }
          }
        });
      }
      if (paramObjectAdapter.view.getParent() == null) {
        this.mControlBar.addView(paramObjectAdapter.view);
      }
      paramPresenter.onBindViewHolder(paramObjectAdapter, localObject);
    }
    
    void bindControlToAction(int paramInt, Presenter paramPresenter)
    {
      bindControlToAction(paramInt, getDisplayedAdapter(), paramPresenter);
    }
    
    int getChildMarginFromCenter(Context paramContext, int paramInt)
    {
      return ControlBarPresenter.this.getChildMarginDefault(paramContext) + ControlBarPresenter.this.getControlIconWidth(paramContext);
    }
    
    ObjectAdapter getDisplayedAdapter()
    {
      return this.mAdapter;
    }
    
    void showControls(Presenter paramPresenter)
    {
      ObjectAdapter localObjectAdapter = getDisplayedAdapter();
      if (localObjectAdapter == null) {}
      for (int i = 0;; i = localObjectAdapter.size())
      {
        View localView = this.mControlBar.getFocusedChild();
        if ((localView != null) && (i > 0) && (this.mControlBar.indexOfChild(localView) >= i)) {
          this.mControlBar.getChildAt(localObjectAdapter.size() - 1).requestFocus();
        }
        j = this.mControlBar.getChildCount() - 1;
        while (j >= i)
        {
          this.mControlBar.removeViewAt(j);
          j -= 1;
        }
      }
      int j = 0;
      while ((j < i) && (j < 7))
      {
        bindControlToAction(j, localObjectAdapter, paramPresenter);
        j += 1;
      }
      this.mControlBar.setChildMarginFromCenter(getChildMarginFromCenter(this.mControlBar.getContext(), i));
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/ControlBarPresenter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */