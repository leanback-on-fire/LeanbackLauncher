package android.support.v17.leanback.app;

import android.os.Bundle;
import android.support.v17.leanback.R.id;
import android.support.v17.leanback.R.layout;
import android.support.v17.leanback.R.transition;
import android.support.v17.leanback.transition.TransitionHelper;
import android.support.v17.leanback.util.StateMachine;
import android.support.v17.leanback.util.StateMachine.State;
import android.support.v17.leanback.widget.BrowseFrameLayout;
import android.support.v17.leanback.widget.ObjectAdapter;
import android.support.v17.leanback.widget.OnChildLaidOutListener;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.OnItemViewSelectedListener;
import android.support.v17.leanback.widget.Presenter.ViewHolder;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter.ViewHolder;
import android.support.v17.leanback.widget.TitleHelper;
import android.support.v17.leanback.widget.VerticalGridPresenter;
import android.support.v17.leanback.widget.VerticalGridPresenter.ViewHolder;
import android.support.v17.leanback.widget.VerticalGridView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class VerticalGridFragment
  extends BaseFragment
{
  static boolean DEBUG = false;
  static final String TAG = "VerticalGridFragment";
  final StateMachine.State STATE_SET_ENTRANCE_START_STATE = new StateMachine.State("SET_ENTRANCE_START_STATE")
  {
    public void run()
    {
      VerticalGridFragment.this.setEntranceTransitionState(false);
    }
  };
  private ObjectAdapter mAdapter;
  private final OnChildLaidOutListener mChildLaidOutListener = new OnChildLaidOutListener()
  {
    public void onChildLaidOut(ViewGroup paramAnonymousViewGroup, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
    {
      if (paramAnonymousInt == 0) {
        VerticalGridFragment.this.showOrHideTitle();
      }
    }
  };
  private VerticalGridPresenter mGridPresenter;
  VerticalGridPresenter.ViewHolder mGridViewHolder;
  private OnItemViewClickedListener mOnItemViewClickedListener;
  OnItemViewSelectedListener mOnItemViewSelectedListener;
  private Object mSceneAfterEntranceTransition;
  private int mSelectedPosition = -1;
  private final OnItemViewSelectedListener mViewSelectedListener = new OnItemViewSelectedListener()
  {
    public void onItemSelected(Presenter.ViewHolder paramAnonymousViewHolder, Object paramAnonymousObject, RowPresenter.ViewHolder paramAnonymousViewHolder1, Row paramAnonymousRow)
    {
      int i = VerticalGridFragment.this.mGridViewHolder.getGridView().getSelectedPosition();
      if (VerticalGridFragment.DEBUG) {
        Log.v("VerticalGridFragment", "grid selected position " + i);
      }
      VerticalGridFragment.this.gridOnItemSelected(i);
      if (VerticalGridFragment.this.mOnItemViewSelectedListener != null) {
        VerticalGridFragment.this.mOnItemViewSelectedListener.onItemSelected(paramAnonymousViewHolder, paramAnonymousObject, paramAnonymousViewHolder1, paramAnonymousRow);
      }
    }
  };
  
  private void setupFocusSearchListener()
  {
    ((BrowseFrameLayout)getView().findViewById(R.id.grid_frame)).setOnFocusSearchListener(getTitleHelper().getOnFocusSearchListener());
  }
  
  private void updateAdapter()
  {
    if (this.mGridViewHolder != null)
    {
      this.mGridPresenter.onBindViewHolder(this.mGridViewHolder, this.mAdapter);
      if (this.mSelectedPosition != -1) {
        this.mGridViewHolder.getGridView().setSelectedPosition(this.mSelectedPosition);
      }
    }
  }
  
  protected Object createEntranceTransition()
  {
    return TransitionHelper.loadTransition(FragmentUtil.getContext(this), R.transition.lb_vertical_grid_entrance_transition);
  }
  
  void createStateMachineStates()
  {
    super.createStateMachineStates();
    this.mStateMachine.addState(this.STATE_SET_ENTRANCE_START_STATE);
  }
  
  void createStateMachineTransitions()
  {
    super.createStateMachineTransitions();
    this.mStateMachine.addTransition(this.STATE_ENTRANCE_ON_PREPARED, this.STATE_SET_ENTRANCE_START_STATE, this.EVT_ON_CREATEVIEW);
  }
  
  public ObjectAdapter getAdapter()
  {
    return this.mAdapter;
  }
  
  public VerticalGridPresenter getGridPresenter()
  {
    return this.mGridPresenter;
  }
  
  public OnItemViewClickedListener getOnItemViewClickedListener()
  {
    return this.mOnItemViewClickedListener;
  }
  
  void gridOnItemSelected(int paramInt)
  {
    if (paramInt != this.mSelectedPosition)
    {
      this.mSelectedPosition = paramInt;
      showOrHideTitle();
    }
  }
  
  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    paramViewGroup = (ViewGroup)paramLayoutInflater.inflate(R.layout.lb_vertical_grid_fragment, paramViewGroup, false);
    installTitleView(paramLayoutInflater, (ViewGroup)paramViewGroup.findViewById(R.id.grid_frame), paramBundle);
    getProgressBarManager().setRootView(paramViewGroup);
    paramLayoutInflater = (ViewGroup)paramViewGroup.findViewById(R.id.browse_grid_dock);
    this.mGridViewHolder = this.mGridPresenter.onCreateViewHolder(paramLayoutInflater);
    paramLayoutInflater.addView(this.mGridViewHolder.view);
    this.mGridViewHolder.getGridView().setOnChildLaidOutListener(this.mChildLaidOutListener);
    this.mSceneAfterEntranceTransition = TransitionHelper.createScene(paramLayoutInflater, new Runnable()
    {
      public void run()
      {
        VerticalGridFragment.this.setEntranceTransitionState(true);
      }
    });
    updateAdapter();
    return paramViewGroup;
  }
  
  public void onDestroyView()
  {
    super.onDestroyView();
    this.mGridViewHolder = null;
  }
  
  public void onStart()
  {
    super.onStart();
    setupFocusSearchListener();
  }
  
  protected void runEntranceTransition(Object paramObject)
  {
    TransitionHelper.runTransition(this.mSceneAfterEntranceTransition, paramObject);
  }
  
  public void setAdapter(ObjectAdapter paramObjectAdapter)
  {
    this.mAdapter = paramObjectAdapter;
    updateAdapter();
  }
  
  void setEntranceTransitionState(boolean paramBoolean)
  {
    this.mGridPresenter.setEntranceTransitionState(this.mGridViewHolder, paramBoolean);
  }
  
  public void setGridPresenter(VerticalGridPresenter paramVerticalGridPresenter)
  {
    if (paramVerticalGridPresenter == null) {
      throw new IllegalArgumentException("Grid presenter may not be null");
    }
    this.mGridPresenter = paramVerticalGridPresenter;
    this.mGridPresenter.setOnItemViewSelectedListener(this.mViewSelectedListener);
    if (this.mOnItemViewClickedListener != null) {
      this.mGridPresenter.setOnItemViewClickedListener(this.mOnItemViewClickedListener);
    }
  }
  
  public void setOnItemViewClickedListener(OnItemViewClickedListener paramOnItemViewClickedListener)
  {
    this.mOnItemViewClickedListener = paramOnItemViewClickedListener;
    if (this.mGridPresenter != null) {
      this.mGridPresenter.setOnItemViewClickedListener(this.mOnItemViewClickedListener);
    }
  }
  
  public void setOnItemViewSelectedListener(OnItemViewSelectedListener paramOnItemViewSelectedListener)
  {
    this.mOnItemViewSelectedListener = paramOnItemViewSelectedListener;
  }
  
  public void setSelectedPosition(int paramInt)
  {
    this.mSelectedPosition = paramInt;
    if ((this.mGridViewHolder != null) && (this.mGridViewHolder.getGridView().getAdapter() != null)) {
      this.mGridViewHolder.getGridView().setSelectedPositionSmooth(paramInt);
    }
  }
  
  void showOrHideTitle()
  {
    if (this.mGridViewHolder.getGridView().findViewHolderForAdapterPosition(this.mSelectedPosition) == null) {
      return;
    }
    if (!this.mGridViewHolder.getGridView().hasPreviousViewInSameRow(this.mSelectedPosition))
    {
      showTitle(true);
      return;
    }
    showTitle(false);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/app/VerticalGridFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */