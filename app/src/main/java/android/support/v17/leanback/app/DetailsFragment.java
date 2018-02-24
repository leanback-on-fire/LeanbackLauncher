package android.support.v17.leanback.app;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.CallSuper;
import android.support.v17.leanback.R.dimen;
import android.support.v17.leanback.R.id;
import android.support.v17.leanback.R.layout;
import android.support.v17.leanback.R.transition;
import android.support.v17.leanback.transition.TransitionHelper;
import android.support.v17.leanback.transition.TransitionListener;
import android.support.v17.leanback.util.StateMachine;
import android.support.v17.leanback.util.StateMachine.Event;
import android.support.v17.leanback.util.StateMachine.State;
import android.support.v17.leanback.widget.BaseOnItemViewClickedListener;
import android.support.v17.leanback.widget.BaseOnItemViewSelectedListener;
import android.support.v17.leanback.widget.BrowseFrameLayout;
import android.support.v17.leanback.widget.BrowseFrameLayout.OnChildFocusListener;
import android.support.v17.leanback.widget.BrowseFrameLayout.OnFocusSearchListener;
import android.support.v17.leanback.widget.DetailsParallax;
import android.support.v17.leanback.widget.FullWidthDetailsOverviewRowPresenter;
import android.support.v17.leanback.widget.FullWidthDetailsOverviewRowPresenter.ViewHolder;
import android.support.v17.leanback.widget.ItemAlignmentFacet;
import android.support.v17.leanback.widget.ItemAlignmentFacet.ItemAlignmentDef;
import android.support.v17.leanback.widget.ItemBridgeAdapter.AdapterListener;
import android.support.v17.leanback.widget.ItemBridgeAdapter.ViewHolder;
import android.support.v17.leanback.widget.ObjectAdapter;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Presenter.ViewHolder;
import android.support.v17.leanback.widget.PresenterSelector;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v17.leanback.widget.RowPresenter.ViewHolder;
import android.support.v17.leanback.widget.VerticalGridView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.Window;
import java.lang.ref.WeakReference;

public class DetailsFragment
  extends BaseFragment
{
  static boolean DEBUG = false;
  static final String TAG = "DetailsFragment";
  final StateMachine.Event EVT_DETAILS_ROW_LOADED = new StateMachine.Event("onFirstRowLoaded");
  final StateMachine.Event EVT_ENTER_TRANSIITON_DONE = new StateMachine.Event("onEnterTransitionDone");
  final StateMachine.Event EVT_NO_ENTER_TRANSITION = new StateMachine.Event("EVT_NO_ENTER_TRANSITION");
  final StateMachine.Event EVT_ONSTART = new StateMachine.Event("onStart");
  final StateMachine.Event EVT_SWITCH_TO_VIDEO = new StateMachine.Event("switchToVideo");
  final StateMachine.State STATE_ENTER_TRANSITION_ADDLISTENER = new StateMachine.State("STATE_ENTER_TRANSITION_PENDING")
  {
    public void run()
    {
      TransitionHelper.addTransitionListener(TransitionHelper.getEnterTransition(DetailsFragment.this.getActivity().getWindow()), DetailsFragment.this.mEnterTransitionListener);
    }
  };
  final StateMachine.State STATE_ENTER_TRANSITION_CANCEL = new StateMachine.State("STATE_ENTER_TRANSITION_CANCEL", false, false)
  {
    public void run()
    {
      if (DetailsFragment.this.mWaitEnterTransitionTimeout != null) {
        DetailsFragment.this.mWaitEnterTransitionTimeout.mRef.clear();
      }
      if (DetailsFragment.this.getActivity() != null)
      {
        Window localWindow = DetailsFragment.this.getActivity().getWindow();
        Object localObject1 = TransitionHelper.getReturnTransition(localWindow);
        Object localObject2 = TransitionHelper.getSharedElementReturnTransition(localWindow);
        TransitionHelper.setEnterTransition(localWindow, null);
        TransitionHelper.setSharedElementEnterTransition(localWindow, null);
        TransitionHelper.setReturnTransition(localWindow, localObject1);
        TransitionHelper.setSharedElementReturnTransition(localWindow, localObject2);
      }
    }
  };
  final StateMachine.State STATE_ENTER_TRANSITION_COMPLETE = new StateMachine.State("STATE_ENTER_TRANSIITON_COMPLETE", true, false);
  final StateMachine.State STATE_ENTER_TRANSITION_INIT = new StateMachine.State("STATE_ENTER_TRANSIITON_INIT");
  final StateMachine.State STATE_ENTER_TRANSITION_PENDING = new StateMachine.State("STATE_ENTER_TRANSITION_PENDING")
  {
    public void run()
    {
      if (DetailsFragment.this.mWaitEnterTransitionTimeout == null) {
        new DetailsFragment.WaitEnterTransitionTimeout(DetailsFragment.this);
      }
    }
  };
  final StateMachine.State STATE_ON_SAFE_START = new StateMachine.State("STATE_ON_SAFE_START")
  {
    public void run()
    {
      DetailsFragment.this.onSafeStart();
    }
  };
  final StateMachine.State STATE_SET_ENTRANCE_START_STATE = new StateMachine.State("STATE_SET_ENTRANCE_START_STATE")
  {
    public void run()
    {
      DetailsFragment.this.mRowsFragment.setEntranceTransitionState(false);
    }
  };
  final StateMachine.State STATE_SWITCH_TO_VIDEO_IN_ON_CREATE = new StateMachine.State("STATE_SWITCH_TO_VIDEO_IN_ON_CREATE", false, false)
  {
    public void run()
    {
      DetailsFragment.this.switchToVideoBeforeVideoFragmentCreated();
    }
  };
  ObjectAdapter mAdapter;
  Drawable mBackgroundDrawable;
  View mBackgroundView;
  int mContainerListAlignTop;
  DetailsFragmentBackgroundController mDetailsBackgroundController;
  DetailsParallax mDetailsParallax;
  TransitionListener mEnterTransitionListener = new TransitionListener()
  {
    public void onTransitionCancel(Object paramAnonymousObject)
    {
      DetailsFragment.this.mStateMachine.fireEvent(DetailsFragment.this.EVT_ENTER_TRANSIITON_DONE);
    }
    
    public void onTransitionEnd(Object paramAnonymousObject)
    {
      DetailsFragment.this.mStateMachine.fireEvent(DetailsFragment.this.EVT_ENTER_TRANSIITON_DONE);
    }
    
    public void onTransitionStart(Object paramAnonymousObject)
    {
      if (DetailsFragment.this.mWaitEnterTransitionTimeout != null) {
        DetailsFragment.this.mWaitEnterTransitionTimeout.mRef.clear();
      }
    }
  };
  BaseOnItemViewSelectedListener mExternalOnItemViewSelectedListener;
  BaseOnItemViewClickedListener mOnItemViewClickedListener;
  final BaseOnItemViewSelectedListener<Object> mOnItemViewSelectedListener = new BaseOnItemViewSelectedListener()
  {
    public void onItemSelected(Presenter.ViewHolder paramAnonymousViewHolder, Object paramAnonymousObject1, RowPresenter.ViewHolder paramAnonymousViewHolder1, Object paramAnonymousObject2)
    {
      int i = DetailsFragment.this.mRowsFragment.getVerticalGridView().getSelectedPosition();
      int j = DetailsFragment.this.mRowsFragment.getVerticalGridView().getSelectedSubPosition();
      if (DetailsFragment.DEBUG) {
        Log.v("DetailsFragment", "row selected position " + i + " subposition " + j);
      }
      DetailsFragment.this.onRowSelected(i, j);
      if (DetailsFragment.this.mExternalOnItemViewSelectedListener != null) {
        DetailsFragment.this.mExternalOnItemViewSelectedListener.onItemSelected(paramAnonymousViewHolder, paramAnonymousObject1, paramAnonymousViewHolder1, paramAnonymousObject2);
      }
    }
  };
  boolean mPendingFocusOnVideo = false;
  TransitionListener mReturnTransitionListener = new TransitionListener()
  {
    public void onTransitionStart(Object paramAnonymousObject)
    {
      DetailsFragment.this.onReturnTransitionStart();
    }
  };
  BrowseFrameLayout mRootView;
  RowsFragment mRowsFragment;
  Object mSceneAfterEntranceTransition;
  final SetSelectionRunnable mSetSelectionRunnable = new SetSelectionRunnable();
  Fragment mVideoFragment;
  WaitEnterTransitionTimeout mWaitEnterTransitionTimeout;
  
  private void setupChildFragmentLayout()
  {
    setVerticalGridViewLayout(this.mRowsFragment.getVerticalGridView());
  }
  
  protected Object createEntranceTransition()
  {
    return TransitionHelper.loadTransition(FragmentUtil.getContext(this), R.transition.lb_details_enter_transition);
  }
  
  void createStateMachineStates()
  {
    super.createStateMachineStates();
    this.mStateMachine.addState(this.STATE_SET_ENTRANCE_START_STATE);
    this.mStateMachine.addState(this.STATE_ON_SAFE_START);
    this.mStateMachine.addState(this.STATE_SWITCH_TO_VIDEO_IN_ON_CREATE);
    this.mStateMachine.addState(this.STATE_ENTER_TRANSITION_INIT);
    this.mStateMachine.addState(this.STATE_ENTER_TRANSITION_ADDLISTENER);
    this.mStateMachine.addState(this.STATE_ENTER_TRANSITION_CANCEL);
    this.mStateMachine.addState(this.STATE_ENTER_TRANSITION_PENDING);
    this.mStateMachine.addState(this.STATE_ENTER_TRANSITION_COMPLETE);
  }
  
  void createStateMachineTransitions()
  {
    super.createStateMachineTransitions();
    this.mStateMachine.addTransition(this.STATE_START, this.STATE_ENTER_TRANSITION_INIT, this.EVT_ON_CREATE);
    this.mStateMachine.addTransition(this.STATE_ENTER_TRANSITION_INIT, this.STATE_ENTER_TRANSITION_COMPLETE, this.COND_TRANSITION_NOT_SUPPORTED);
    this.mStateMachine.addTransition(this.STATE_ENTER_TRANSITION_INIT, this.STATE_ENTER_TRANSITION_COMPLETE, this.EVT_NO_ENTER_TRANSITION);
    this.mStateMachine.addTransition(this.STATE_ENTER_TRANSITION_INIT, this.STATE_ENTER_TRANSITION_CANCEL, this.EVT_SWITCH_TO_VIDEO);
    this.mStateMachine.addTransition(this.STATE_ENTER_TRANSITION_CANCEL, this.STATE_ENTER_TRANSITION_COMPLETE);
    this.mStateMachine.addTransition(this.STATE_ENTER_TRANSITION_INIT, this.STATE_ENTER_TRANSITION_ADDLISTENER, this.EVT_ON_CREATEVIEW);
    this.mStateMachine.addTransition(this.STATE_ENTER_TRANSITION_ADDLISTENER, this.STATE_ENTER_TRANSITION_COMPLETE, this.EVT_ENTER_TRANSIITON_DONE);
    this.mStateMachine.addTransition(this.STATE_ENTER_TRANSITION_ADDLISTENER, this.STATE_ENTER_TRANSITION_PENDING, this.EVT_DETAILS_ROW_LOADED);
    this.mStateMachine.addTransition(this.STATE_ENTER_TRANSITION_PENDING, this.STATE_ENTER_TRANSITION_COMPLETE, this.EVT_ENTER_TRANSIITON_DONE);
    this.mStateMachine.addTransition(this.STATE_ENTER_TRANSITION_COMPLETE, this.STATE_ENTRANCE_PERFORM);
    this.mStateMachine.addTransition(this.STATE_ENTRANCE_INIT, this.STATE_SWITCH_TO_VIDEO_IN_ON_CREATE, this.EVT_SWITCH_TO_VIDEO);
    this.mStateMachine.addTransition(this.STATE_SWITCH_TO_VIDEO_IN_ON_CREATE, this.STATE_ENTRANCE_COMPLETE);
    this.mStateMachine.addTransition(this.STATE_ENTRANCE_COMPLETE, this.STATE_SWITCH_TO_VIDEO_IN_ON_CREATE, this.EVT_SWITCH_TO_VIDEO);
    this.mStateMachine.addTransition(this.STATE_ENTRANCE_ON_PREPARED, this.STATE_SET_ENTRANCE_START_STATE, this.EVT_ONSTART);
    this.mStateMachine.addTransition(this.STATE_START, this.STATE_ON_SAFE_START, this.EVT_ONSTART);
    this.mStateMachine.addTransition(this.STATE_ENTRANCE_COMPLETE, this.STATE_ON_SAFE_START);
    this.mStateMachine.addTransition(this.STATE_ENTER_TRANSITION_COMPLETE, this.STATE_ON_SAFE_START);
  }
  
  final Fragment findOrCreateVideoFragment()
  {
    if (this.mVideoFragment != null) {
      return this.mVideoFragment;
    }
    Fragment localFragment = getChildFragmentManager().findFragmentById(R.id.video_surface_container);
    Object localObject = localFragment;
    if (localFragment == null)
    {
      localObject = localFragment;
      if (this.mDetailsBackgroundController != null)
      {
        localObject = getChildFragmentManager().beginTransaction();
        int i = R.id.video_surface_container;
        localFragment = this.mDetailsBackgroundController.onCreateVideoFragment();
        ((FragmentTransaction)localObject).add(i, localFragment);
        ((FragmentTransaction)localObject).commit();
        localObject = localFragment;
        if (this.mPendingFocusOnVideo)
        {
          getView().post(new Runnable()
          {
            public void run()
            {
              if (DetailsFragment.this.getView() != null) {
                DetailsFragment.this.switchToVideo();
              }
              DetailsFragment.this.mPendingFocusOnVideo = false;
            }
          });
          localObject = localFragment;
        }
      }
    }
    this.mVideoFragment = ((Fragment)localObject);
    return this.mVideoFragment;
  }
  
  public ObjectAdapter getAdapter()
  {
    return this.mAdapter;
  }
  
  public BaseOnItemViewClickedListener getOnItemViewClickedListener()
  {
    return this.mOnItemViewClickedListener;
  }
  
  public DetailsParallax getParallax()
  {
    if (this.mDetailsParallax == null)
    {
      this.mDetailsParallax = new DetailsParallax();
      if ((this.mRowsFragment != null) && (this.mRowsFragment.getView() != null)) {
        this.mDetailsParallax.setRecyclerView(this.mRowsFragment.getVerticalGridView());
      }
    }
    return this.mDetailsParallax;
  }
  
  public RowsFragment getRowsFragment()
  {
    return this.mRowsFragment;
  }
  
  VerticalGridView getVerticalGridView()
  {
    if (this.mRowsFragment == null) {
      return null;
    }
    return this.mRowsFragment.getVerticalGridView();
  }
  
  @Deprecated
  protected View inflateTitle(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    return super.onInflateTitleView(paramLayoutInflater, paramViewGroup, paramBundle);
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mContainerListAlignTop = getResources().getDimensionPixelSize(R.dimen.lb_details_rows_align_top);
    paramBundle = getActivity();
    if (paramBundle != null)
    {
      if (TransitionHelper.getEnterTransition(paramBundle.getWindow()) == null) {
        this.mStateMachine.fireEvent(this.EVT_NO_ENTER_TRANSITION);
      }
      paramBundle = TransitionHelper.getReturnTransition(paramBundle.getWindow());
      if (paramBundle != null) {
        TransitionHelper.addTransitionListener(paramBundle, this.mReturnTransitionListener);
      }
      return;
    }
    this.mStateMachine.fireEvent(this.EVT_NO_ENTER_TRANSITION);
  }
  
  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    this.mRootView = ((BrowseFrameLayout)paramLayoutInflater.inflate(R.layout.lb_details_fragment, paramViewGroup, false));
    this.mBackgroundView = this.mRootView.findViewById(R.id.details_background_view);
    if (this.mBackgroundView != null) {
      this.mBackgroundView.setBackground(this.mBackgroundDrawable);
    }
    this.mRowsFragment = ((RowsFragment)getChildFragmentManager().findFragmentById(R.id.details_rows_dock));
    if (this.mRowsFragment == null)
    {
      this.mRowsFragment = new RowsFragment();
      getChildFragmentManager().beginTransaction().replace(R.id.details_rows_dock, this.mRowsFragment).commit();
    }
    installTitleView(paramLayoutInflater, this.mRootView, paramBundle);
    this.mRowsFragment.setAdapter(this.mAdapter);
    this.mRowsFragment.setOnItemViewSelectedListener(this.mOnItemViewSelectedListener);
    this.mRowsFragment.setOnItemViewClickedListener(this.mOnItemViewClickedListener);
    this.mSceneAfterEntranceTransition = TransitionHelper.createScene(this.mRootView, new Runnable()
    {
      public void run()
      {
        DetailsFragment.this.mRowsFragment.setEntranceTransitionState(true);
      }
    });
    setupDpadNavigation();
    if (Build.VERSION.SDK_INT >= 21) {
      this.mRowsFragment.setExternalAdapterListener(new ItemBridgeAdapter.AdapterListener()
      {
        public void onCreate(ItemBridgeAdapter.ViewHolder paramAnonymousViewHolder)
        {
          if ((DetailsFragment.this.mDetailsParallax != null) && ((paramAnonymousViewHolder.getViewHolder() instanceof FullWidthDetailsOverviewRowPresenter.ViewHolder))) {
            ((FullWidthDetailsOverviewRowPresenter.ViewHolder)paramAnonymousViewHolder.getViewHolder()).getOverviewView().setTag(R.id.lb_parallax_source, DetailsFragment.this.mDetailsParallax);
          }
        }
      });
    }
    return this.mRootView;
  }
  
  protected void onEntranceTransitionEnd()
  {
    this.mRowsFragment.onTransitionEnd();
  }
  
  protected void onEntranceTransitionPrepare()
  {
    this.mRowsFragment.onTransitionPrepare();
  }
  
  protected void onEntranceTransitionStart()
  {
    this.mRowsFragment.onTransitionStart();
  }
  
  public View onInflateTitleView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    return inflateTitle(paramLayoutInflater, paramViewGroup, paramBundle);
  }
  
  @CallSuper
  void onReturnTransitionStart()
  {
    if ((this.mDetailsBackgroundController != null) && (!this.mDetailsBackgroundController.disableVideoParallax()) && (this.mVideoFragment != null))
    {
      FragmentTransaction localFragmentTransaction = getChildFragmentManager().beginTransaction();
      localFragmentTransaction.remove(this.mVideoFragment);
      localFragmentTransaction.commit();
      this.mVideoFragment = null;
    }
  }
  
  void onRowSelected(int paramInt1, int paramInt2)
  {
    Object localObject = getAdapter();
    if ((this.mRowsFragment != null) && (this.mRowsFragment.getView() != null) && (this.mRowsFragment.getView().hasFocus()) && (!this.mPendingFocusOnVideo) && ((localObject == null) || (((ObjectAdapter)localObject).size() == 0) || ((getVerticalGridView().getSelectedPosition() == 0) && (getVerticalGridView().getSelectedSubPosition() == 0)))) {
      showTitle(true);
    }
    while ((localObject != null) && (((ObjectAdapter)localObject).size() > paramInt1))
    {
      localObject = getVerticalGridView();
      int j = ((VerticalGridView)localObject).getChildCount();
      if (j > 0) {
        this.mStateMachine.fireEvent(this.EVT_DETAILS_ROW_LOADED);
      }
      int i = 0;
      while (i < j)
      {
        ItemBridgeAdapter.ViewHolder localViewHolder = (ItemBridgeAdapter.ViewHolder)((VerticalGridView)localObject).getChildViewHolder(((VerticalGridView)localObject).getChildAt(i));
        RowPresenter localRowPresenter = (RowPresenter)localViewHolder.getPresenter();
        onSetRowStatus(localRowPresenter, localRowPresenter.getRowViewHolder(localViewHolder.getViewHolder()), localViewHolder.getAdapterPosition(), paramInt1, paramInt2);
        i += 1;
      }
      showTitle(false);
    }
  }
  
  @CallSuper
  void onSafeStart()
  {
    if (this.mDetailsBackgroundController != null) {
      this.mDetailsBackgroundController.onStart();
    }
  }
  
  protected void onSetDetailsOverviewRowStatus(FullWidthDetailsOverviewRowPresenter paramFullWidthDetailsOverviewRowPresenter, FullWidthDetailsOverviewRowPresenter.ViewHolder paramViewHolder, int paramInt1, int paramInt2, int paramInt3)
  {
    if (paramInt2 > paramInt1)
    {
      paramFullWidthDetailsOverviewRowPresenter.setState(paramViewHolder, 0);
      return;
    }
    if ((paramInt2 == paramInt1) && (paramInt3 == 1))
    {
      paramFullWidthDetailsOverviewRowPresenter.setState(paramViewHolder, 0);
      return;
    }
    if ((paramInt2 == paramInt1) && (paramInt3 == 0))
    {
      paramFullWidthDetailsOverviewRowPresenter.setState(paramViewHolder, 1);
      return;
    }
    paramFullWidthDetailsOverviewRowPresenter.setState(paramViewHolder, 2);
  }
  
  protected void onSetRowStatus(RowPresenter paramRowPresenter, RowPresenter.ViewHolder paramViewHolder, int paramInt1, int paramInt2, int paramInt3)
  {
    if ((paramRowPresenter instanceof FullWidthDetailsOverviewRowPresenter)) {
      onSetDetailsOverviewRowStatus((FullWidthDetailsOverviewRowPresenter)paramRowPresenter, (FullWidthDetailsOverviewRowPresenter.ViewHolder)paramViewHolder, paramInt1, paramInt2, paramInt3);
    }
  }
  
  public void onStart()
  {
    super.onStart();
    setupChildFragmentLayout();
    this.mStateMachine.fireEvent(this.EVT_ONSTART);
    if (this.mDetailsParallax != null) {
      this.mDetailsParallax.setRecyclerView(this.mRowsFragment.getVerticalGridView());
    }
    if (this.mPendingFocusOnVideo) {
      slideOutGridView();
    }
    while (getView().hasFocus()) {
      return;
    }
    this.mRowsFragment.getVerticalGridView().requestFocus();
  }
  
  public void onStop()
  {
    if (this.mDetailsBackgroundController != null) {
      this.mDetailsBackgroundController.onStop();
    }
    super.onStop();
  }
  
  protected void runEntranceTransition(Object paramObject)
  {
    TransitionHelper.runTransition(this.mSceneAfterEntranceTransition, paramObject);
  }
  
  public void setAdapter(ObjectAdapter paramObjectAdapter)
  {
    this.mAdapter = paramObjectAdapter;
    Presenter[] arrayOfPresenter = paramObjectAdapter.getPresenterSelector().getPresenters();
    if (arrayOfPresenter != null)
    {
      int i = 0;
      while (i < arrayOfPresenter.length)
      {
        setupPresenter(arrayOfPresenter[i]);
        i += 1;
      }
    }
    Log.e("DetailsFragment", "PresenterSelector.getPresenters() not implemented");
    if (this.mRowsFragment != null) {
      this.mRowsFragment.setAdapter(paramObjectAdapter);
    }
  }
  
  void setBackgroundDrawable(Drawable paramDrawable)
  {
    if (this.mBackgroundView != null) {
      this.mBackgroundView.setBackground(paramDrawable);
    }
    this.mBackgroundDrawable = paramDrawable;
  }
  
  public void setOnItemViewClickedListener(BaseOnItemViewClickedListener paramBaseOnItemViewClickedListener)
  {
    if (this.mOnItemViewClickedListener != paramBaseOnItemViewClickedListener)
    {
      this.mOnItemViewClickedListener = paramBaseOnItemViewClickedListener;
      if (this.mRowsFragment != null) {
        this.mRowsFragment.setOnItemViewClickedListener(paramBaseOnItemViewClickedListener);
      }
    }
  }
  
  public void setOnItemViewSelectedListener(BaseOnItemViewSelectedListener paramBaseOnItemViewSelectedListener)
  {
    this.mExternalOnItemViewSelectedListener = paramBaseOnItemViewSelectedListener;
  }
  
  public void setSelectedPosition(int paramInt)
  {
    setSelectedPosition(paramInt, true);
  }
  
  public void setSelectedPosition(int paramInt, boolean paramBoolean)
  {
    this.mSetSelectionRunnable.mPosition = paramInt;
    this.mSetSelectionRunnable.mSmooth = paramBoolean;
    if ((getView() != null) && (getView().getHandler() != null)) {
      getView().getHandler().post(this.mSetSelectionRunnable);
    }
  }
  
  void setVerticalGridViewLayout(VerticalGridView paramVerticalGridView)
  {
    paramVerticalGridView.setItemAlignmentOffset(-this.mContainerListAlignTop);
    paramVerticalGridView.setItemAlignmentOffsetPercent(-1.0F);
    paramVerticalGridView.setWindowAlignmentOffset(0);
    paramVerticalGridView.setWindowAlignmentOffsetPercent(-1.0F);
    paramVerticalGridView.setWindowAlignment(0);
  }
  
  protected void setupDetailsOverviewRowPresenter(FullWidthDetailsOverviewRowPresenter paramFullWidthDetailsOverviewRowPresenter)
  {
    ItemAlignmentFacet localItemAlignmentFacet = new ItemAlignmentFacet();
    ItemAlignmentFacet.ItemAlignmentDef localItemAlignmentDef1 = new ItemAlignmentFacet.ItemAlignmentDef();
    localItemAlignmentDef1.setItemAlignmentViewId(R.id.details_frame);
    localItemAlignmentDef1.setItemAlignmentOffset(-getResources().getDimensionPixelSize(R.dimen.lb_details_v2_align_pos_for_actions));
    localItemAlignmentDef1.setItemAlignmentOffsetPercent(0.0F);
    ItemAlignmentFacet.ItemAlignmentDef localItemAlignmentDef2 = new ItemAlignmentFacet.ItemAlignmentDef();
    localItemAlignmentDef2.setItemAlignmentViewId(R.id.details_frame);
    localItemAlignmentDef2.setItemAlignmentFocusViewId(R.id.details_overview_description);
    localItemAlignmentDef2.setItemAlignmentOffset(-getResources().getDimensionPixelSize(R.dimen.lb_details_v2_align_pos_for_description));
    localItemAlignmentDef2.setItemAlignmentOffsetPercent(0.0F);
    localItemAlignmentFacet.setAlignmentDefs(new ItemAlignmentFacet.ItemAlignmentDef[] { localItemAlignmentDef1, localItemAlignmentDef2 });
    paramFullWidthDetailsOverviewRowPresenter.setFacet(ItemAlignmentFacet.class, localItemAlignmentFacet);
  }
  
  void setupDpadNavigation()
  {
    this.mRootView.setOnChildFocusListener(new BrowseFrameLayout.OnChildFocusListener()
    {
      public void onRequestChildFocus(View paramAnonymousView1, View paramAnonymousView2)
      {
        if (paramAnonymousView1 != DetailsFragment.this.mRootView.getFocusedChild())
        {
          if (paramAnonymousView1.getId() != R.id.details_fragment_root) {
            break label50;
          }
          if (!DetailsFragment.this.mPendingFocusOnVideo)
          {
            DetailsFragment.this.slideInGridView();
            DetailsFragment.this.showTitle(true);
          }
        }
        return;
        label50:
        if (paramAnonymousView1.getId() == R.id.video_surface_container)
        {
          DetailsFragment.this.slideOutGridView();
          DetailsFragment.this.showTitle(false);
          return;
        }
        DetailsFragment.this.showTitle(true);
      }
      
      public boolean onRequestFocusInDescendants(int paramAnonymousInt, Rect paramAnonymousRect)
      {
        return false;
      }
    });
    this.mRootView.setOnFocusSearchListener(new BrowseFrameLayout.OnFocusSearchListener()
    {
      public View onFocusSearch(View paramAnonymousView, int paramAnonymousInt)
      {
        View localView;
        if ((DetailsFragment.this.mRowsFragment.getVerticalGridView() != null) && (DetailsFragment.this.mRowsFragment.getVerticalGridView().hasFocus()))
        {
          localView = paramAnonymousView;
          if (paramAnonymousInt == 33)
          {
            if ((DetailsFragment.this.mDetailsBackgroundController == null) || (!DetailsFragment.this.mDetailsBackgroundController.canNavigateToVideoFragment()) || (DetailsFragment.this.mVideoFragment == null) || (DetailsFragment.this.mVideoFragment.getView() == null)) {
              break label96;
            }
            localView = DetailsFragment.this.mVideoFragment.getView();
          }
        }
        label96:
        do
        {
          do
          {
            do
            {
              do
              {
                do
                {
                  do
                  {
                    return localView;
                    localView = paramAnonymousView;
                  } while (DetailsFragment.this.getTitleView() == null);
                  localView = paramAnonymousView;
                } while (!DetailsFragment.this.getTitleView().hasFocusable());
                return DetailsFragment.this.getTitleView();
                localView = paramAnonymousView;
              } while (DetailsFragment.this.getTitleView() == null);
              localView = paramAnonymousView;
            } while (!DetailsFragment.this.getTitleView().hasFocus());
            localView = paramAnonymousView;
          } while (paramAnonymousInt != 130);
          localView = paramAnonymousView;
        } while (DetailsFragment.this.mRowsFragment.getVerticalGridView() == null);
        return DetailsFragment.this.mRowsFragment.getVerticalGridView();
      }
    });
    this.mRootView.setOnDispatchKeyListener(new View.OnKeyListener()
    {
      public boolean onKey(View paramAnonymousView, int paramAnonymousInt, KeyEvent paramAnonymousKeyEvent)
      {
        if ((DetailsFragment.this.mVideoFragment != null) && (DetailsFragment.this.mVideoFragment.getView() != null) && (DetailsFragment.this.mVideoFragment.getView().hasFocus()) && ((paramAnonymousInt == 4) || (paramAnonymousInt == 111)) && (DetailsFragment.this.getVerticalGridView().getChildCount() > 0))
        {
          DetailsFragment.this.getVerticalGridView().requestFocus();
          return true;
        }
        return false;
      }
    });
  }
  
  protected void setupPresenter(Presenter paramPresenter)
  {
    if ((paramPresenter instanceof FullWidthDetailsOverviewRowPresenter)) {
      setupDetailsOverviewRowPresenter((FullWidthDetailsOverviewRowPresenter)paramPresenter);
    }
  }
  
  void slideInGridView()
  {
    if (getVerticalGridView() != null) {
      getVerticalGridView().animateIn();
    }
  }
  
  void slideOutGridView()
  {
    if (getVerticalGridView() != null) {
      getVerticalGridView().animateOut();
    }
  }
  
  void switchToRows()
  {
    this.mPendingFocusOnVideo = false;
    VerticalGridView localVerticalGridView = getVerticalGridView();
    if ((localVerticalGridView != null) && (localVerticalGridView.getChildCount() > 0)) {
      localVerticalGridView.requestFocus();
    }
  }
  
  void switchToVideo()
  {
    if ((this.mVideoFragment != null) && (this.mVideoFragment.getView() != null))
    {
      this.mVideoFragment.getView().requestFocus();
      return;
    }
    this.mStateMachine.fireEvent(this.EVT_SWITCH_TO_VIDEO);
  }
  
  void switchToVideoBeforeVideoFragmentCreated()
  {
    this.mDetailsBackgroundController.switchToVideoBeforeCreate();
    showTitle(false);
    this.mPendingFocusOnVideo = true;
    slideOutGridView();
  }
  
  private class SetSelectionRunnable
    implements Runnable
  {
    int mPosition;
    boolean mSmooth = true;
    
    SetSelectionRunnable() {}
    
    public void run()
    {
      if (DetailsFragment.this.mRowsFragment == null) {
        return;
      }
      DetailsFragment.this.mRowsFragment.setSelectedPosition(this.mPosition, this.mSmooth);
    }
  }
  
  static class WaitEnterTransitionTimeout
    implements Runnable
  {
    static final long WAIT_ENTERTRANSITION_START = 200L;
    final WeakReference<DetailsFragment> mRef;
    
    WaitEnterTransitionTimeout(DetailsFragment paramDetailsFragment)
    {
      this.mRef = new WeakReference(paramDetailsFragment);
      paramDetailsFragment.getView().postDelayed(this, 200L);
    }
    
    public void run()
    {
      DetailsFragment localDetailsFragment = (DetailsFragment)this.mRef.get();
      if (localDetailsFragment != null) {
        localDetailsFragment.mStateMachine.fireEvent(localDetailsFragment.EVT_ENTER_TRANSIITON_DONE);
      }
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/app/DetailsFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */