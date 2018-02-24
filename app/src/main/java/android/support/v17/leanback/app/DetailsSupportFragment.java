package android.support.v17.leanback.app;

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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.Window;
import java.lang.ref.WeakReference;

public class DetailsSupportFragment
  extends BaseSupportFragment
{
  static boolean DEBUG = false;
  static final String TAG = "DetailsSupportFragment";
  final StateMachine.Event EVT_DETAILS_ROW_LOADED = new StateMachine.Event("onFirstRowLoaded");
  final StateMachine.Event EVT_ENTER_TRANSIITON_DONE = new StateMachine.Event("onEnterTransitionDone");
  final StateMachine.Event EVT_NO_ENTER_TRANSITION = new StateMachine.Event("EVT_NO_ENTER_TRANSITION");
  final StateMachine.Event EVT_ONSTART = new StateMachine.Event("onStart");
  final StateMachine.Event EVT_SWITCH_TO_VIDEO = new StateMachine.Event("switchToVideo");
  final StateMachine.State STATE_ENTER_TRANSITION_ADDLISTENER = new StateMachine.State("STATE_ENTER_TRANSITION_PENDING")
  {
    public void run()
    {
      TransitionHelper.addTransitionListener(TransitionHelper.getEnterTransition(DetailsSupportFragment.this.getActivity().getWindow()), DetailsSupportFragment.this.mEnterTransitionListener);
    }
  };
  final StateMachine.State STATE_ENTER_TRANSITION_CANCEL = new StateMachine.State("STATE_ENTER_TRANSITION_CANCEL", false, false)
  {
    public void run()
    {
      if (DetailsSupportFragment.this.mWaitEnterTransitionTimeout != null) {
        DetailsSupportFragment.this.mWaitEnterTransitionTimeout.mRef.clear();
      }
      if (DetailsSupportFragment.this.getActivity() != null)
      {
        Window localWindow = DetailsSupportFragment.this.getActivity().getWindow();
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
      if (DetailsSupportFragment.this.mWaitEnterTransitionTimeout == null) {
        new DetailsSupportFragment.WaitEnterTransitionTimeout(DetailsSupportFragment.this);
      }
    }
  };
  final StateMachine.State STATE_ON_SAFE_START = new StateMachine.State("STATE_ON_SAFE_START")
  {
    public void run()
    {
      DetailsSupportFragment.this.onSafeStart();
    }
  };
  final StateMachine.State STATE_SET_ENTRANCE_START_STATE = new StateMachine.State("STATE_SET_ENTRANCE_START_STATE")
  {
    public void run()
    {
      DetailsSupportFragment.this.mRowsSupportFragment.setEntranceTransitionState(false);
    }
  };
  final StateMachine.State STATE_SWITCH_TO_VIDEO_IN_ON_CREATE = new StateMachine.State("STATE_SWITCH_TO_VIDEO_IN_ON_CREATE", false, false)
  {
    public void run()
    {
      DetailsSupportFragment.this.switchToVideoBeforeVideoSupportFragmentCreated();
    }
  };
  ObjectAdapter mAdapter;
  Drawable mBackgroundDrawable;
  View mBackgroundView;
  int mContainerListAlignTop;
  DetailsSupportFragmentBackgroundController mDetailsBackgroundController;
  DetailsParallax mDetailsParallax;
  TransitionListener mEnterTransitionListener = new TransitionListener()
  {
    public void onTransitionCancel(Object paramAnonymousObject)
    {
      DetailsSupportFragment.this.mStateMachine.fireEvent(DetailsSupportFragment.this.EVT_ENTER_TRANSIITON_DONE);
    }
    
    public void onTransitionEnd(Object paramAnonymousObject)
    {
      DetailsSupportFragment.this.mStateMachine.fireEvent(DetailsSupportFragment.this.EVT_ENTER_TRANSIITON_DONE);
    }
    
    public void onTransitionStart(Object paramAnonymousObject)
    {
      if (DetailsSupportFragment.this.mWaitEnterTransitionTimeout != null) {
        DetailsSupportFragment.this.mWaitEnterTransitionTimeout.mRef.clear();
      }
    }
  };
  BaseOnItemViewSelectedListener mExternalOnItemViewSelectedListener;
  BaseOnItemViewClickedListener mOnItemViewClickedListener;
  final BaseOnItemViewSelectedListener<Object> mOnItemViewSelectedListener = new BaseOnItemViewSelectedListener()
  {
    public void onItemSelected(Presenter.ViewHolder paramAnonymousViewHolder, Object paramAnonymousObject1, RowPresenter.ViewHolder paramAnonymousViewHolder1, Object paramAnonymousObject2)
    {
      int i = DetailsSupportFragment.this.mRowsSupportFragment.getVerticalGridView().getSelectedPosition();
      int j = DetailsSupportFragment.this.mRowsSupportFragment.getVerticalGridView().getSelectedSubPosition();
      if (DetailsSupportFragment.DEBUG) {
        Log.v("DetailsSupportFragment", "row selected position " + i + " subposition " + j);
      }
      DetailsSupportFragment.this.onRowSelected(i, j);
      if (DetailsSupportFragment.this.mExternalOnItemViewSelectedListener != null) {
        DetailsSupportFragment.this.mExternalOnItemViewSelectedListener.onItemSelected(paramAnonymousViewHolder, paramAnonymousObject1, paramAnonymousViewHolder1, paramAnonymousObject2);
      }
    }
  };
  boolean mPendingFocusOnVideo = false;
  TransitionListener mReturnTransitionListener = new TransitionListener()
  {
    public void onTransitionStart(Object paramAnonymousObject)
    {
      DetailsSupportFragment.this.onReturnTransitionStart();
    }
  };
  BrowseFrameLayout mRootView;
  RowsSupportFragment mRowsSupportFragment;
  Object mSceneAfterEntranceTransition;
  final SetSelectionRunnable mSetSelectionRunnable = new SetSelectionRunnable();
  Fragment mVideoSupportFragment;
  WaitEnterTransitionTimeout mWaitEnterTransitionTimeout;
  
  private void setupChildFragmentLayout()
  {
    setVerticalGridViewLayout(this.mRowsSupportFragment.getVerticalGridView());
  }
  
  protected Object createEntranceTransition()
  {
    return TransitionHelper.loadTransition(getContext(), R.transition.lb_details_enter_transition);
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
  
  final Fragment findOrCreateVideoSupportFragment()
  {
    if (this.mVideoSupportFragment != null) {
      return this.mVideoSupportFragment;
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
        localFragment = this.mDetailsBackgroundController.onCreateVideoSupportFragment();
        ((FragmentTransaction)localObject).add(i, localFragment);
        ((FragmentTransaction)localObject).commit();
        localObject = localFragment;
        if (this.mPendingFocusOnVideo)
        {
          getView().post(new Runnable()
          {
            public void run()
            {
              if (DetailsSupportFragment.this.getView() != null) {
                DetailsSupportFragment.this.switchToVideo();
              }
              DetailsSupportFragment.this.mPendingFocusOnVideo = false;
            }
          });
          localObject = localFragment;
        }
      }
    }
    this.mVideoSupportFragment = ((Fragment)localObject);
    return this.mVideoSupportFragment;
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
      if ((this.mRowsSupportFragment != null) && (this.mRowsSupportFragment.getView() != null)) {
        this.mDetailsParallax.setRecyclerView(this.mRowsSupportFragment.getVerticalGridView());
      }
    }
    return this.mDetailsParallax;
  }
  
  public RowsSupportFragment getRowsSupportFragment()
  {
    return this.mRowsSupportFragment;
  }
  
  VerticalGridView getVerticalGridView()
  {
    if (this.mRowsSupportFragment == null) {
      return null;
    }
    return this.mRowsSupportFragment.getVerticalGridView();
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
    this.mRowsSupportFragment = ((RowsSupportFragment)getChildFragmentManager().findFragmentById(R.id.details_rows_dock));
    if (this.mRowsSupportFragment == null)
    {
      this.mRowsSupportFragment = new RowsSupportFragment();
      getChildFragmentManager().beginTransaction().replace(R.id.details_rows_dock, this.mRowsSupportFragment).commit();
    }
    installTitleView(paramLayoutInflater, this.mRootView, paramBundle);
    this.mRowsSupportFragment.setAdapter(this.mAdapter);
    this.mRowsSupportFragment.setOnItemViewSelectedListener(this.mOnItemViewSelectedListener);
    this.mRowsSupportFragment.setOnItemViewClickedListener(this.mOnItemViewClickedListener);
    this.mSceneAfterEntranceTransition = TransitionHelper.createScene(this.mRootView, new Runnable()
    {
      public void run()
      {
        DetailsSupportFragment.this.mRowsSupportFragment.setEntranceTransitionState(true);
      }
    });
    setupDpadNavigation();
    if (Build.VERSION.SDK_INT >= 21) {
      this.mRowsSupportFragment.setExternalAdapterListener(new ItemBridgeAdapter.AdapterListener()
      {
        public void onCreate(ItemBridgeAdapter.ViewHolder paramAnonymousViewHolder)
        {
          if ((DetailsSupportFragment.this.mDetailsParallax != null) && ((paramAnonymousViewHolder.getViewHolder() instanceof FullWidthDetailsOverviewRowPresenter.ViewHolder))) {
            ((FullWidthDetailsOverviewRowPresenter.ViewHolder)paramAnonymousViewHolder.getViewHolder()).getOverviewView().setTag(R.id.lb_parallax_source, DetailsSupportFragment.this.mDetailsParallax);
          }
        }
      });
    }
    return this.mRootView;
  }
  
  protected void onEntranceTransitionEnd()
  {
    this.mRowsSupportFragment.onTransitionEnd();
  }
  
  protected void onEntranceTransitionPrepare()
  {
    this.mRowsSupportFragment.onTransitionPrepare();
  }
  
  protected void onEntranceTransitionStart()
  {
    this.mRowsSupportFragment.onTransitionStart();
  }
  
  public View onInflateTitleView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    return inflateTitle(paramLayoutInflater, paramViewGroup, paramBundle);
  }
  
  @CallSuper
  void onReturnTransitionStart()
  {
    if ((this.mDetailsBackgroundController != null) && (!this.mDetailsBackgroundController.disableVideoParallax()) && (this.mVideoSupportFragment != null))
    {
      FragmentTransaction localFragmentTransaction = getChildFragmentManager().beginTransaction();
      localFragmentTransaction.remove(this.mVideoSupportFragment);
      localFragmentTransaction.commit();
      this.mVideoSupportFragment = null;
    }
  }
  
  void onRowSelected(int paramInt1, int paramInt2)
  {
    Object localObject = getAdapter();
    if ((this.mRowsSupportFragment != null) && (this.mRowsSupportFragment.getView() != null) && (this.mRowsSupportFragment.getView().hasFocus()) && (!this.mPendingFocusOnVideo) && ((localObject == null) || (((ObjectAdapter)localObject).size() == 0) || ((getVerticalGridView().getSelectedPosition() == 0) && (getVerticalGridView().getSelectedSubPosition() == 0)))) {
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
      this.mDetailsParallax.setRecyclerView(this.mRowsSupportFragment.getVerticalGridView());
    }
    if (this.mPendingFocusOnVideo) {
      slideOutGridView();
    }
    while (getView().hasFocus()) {
      return;
    }
    this.mRowsSupportFragment.getVerticalGridView().requestFocus();
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
    Log.e("DetailsSupportFragment", "PresenterSelector.getPresenters() not implemented");
    if (this.mRowsSupportFragment != null) {
      this.mRowsSupportFragment.setAdapter(paramObjectAdapter);
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
      if (this.mRowsSupportFragment != null) {
        this.mRowsSupportFragment.setOnItemViewClickedListener(paramBaseOnItemViewClickedListener);
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
        if (paramAnonymousView1 != DetailsSupportFragment.this.mRootView.getFocusedChild())
        {
          if (paramAnonymousView1.getId() != R.id.details_fragment_root) {
            break label50;
          }
          if (!DetailsSupportFragment.this.mPendingFocusOnVideo)
          {
            DetailsSupportFragment.this.slideInGridView();
            DetailsSupportFragment.this.showTitle(true);
          }
        }
        return;
        label50:
        if (paramAnonymousView1.getId() == R.id.video_surface_container)
        {
          DetailsSupportFragment.this.slideOutGridView();
          DetailsSupportFragment.this.showTitle(false);
          return;
        }
        DetailsSupportFragment.this.showTitle(true);
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
        if ((DetailsSupportFragment.this.mRowsSupportFragment.getVerticalGridView() != null) && (DetailsSupportFragment.this.mRowsSupportFragment.getVerticalGridView().hasFocus()))
        {
          localView = paramAnonymousView;
          if (paramAnonymousInt == 33)
          {
            if ((DetailsSupportFragment.this.mDetailsBackgroundController == null) || (!DetailsSupportFragment.this.mDetailsBackgroundController.canNavigateToVideoSupportFragment()) || (DetailsSupportFragment.this.mVideoSupportFragment == null) || (DetailsSupportFragment.this.mVideoSupportFragment.getView() == null)) {
              break label96;
            }
            localView = DetailsSupportFragment.this.mVideoSupportFragment.getView();
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
                  } while (DetailsSupportFragment.this.getTitleView() == null);
                  localView = paramAnonymousView;
                } while (!DetailsSupportFragment.this.getTitleView().hasFocusable());
                return DetailsSupportFragment.this.getTitleView();
                localView = paramAnonymousView;
              } while (DetailsSupportFragment.this.getTitleView() == null);
              localView = paramAnonymousView;
            } while (!DetailsSupportFragment.this.getTitleView().hasFocus());
            localView = paramAnonymousView;
          } while (paramAnonymousInt != 130);
          localView = paramAnonymousView;
        } while (DetailsSupportFragment.this.mRowsSupportFragment.getVerticalGridView() == null);
        return DetailsSupportFragment.this.mRowsSupportFragment.getVerticalGridView();
      }
    });
    this.mRootView.setOnDispatchKeyListener(new View.OnKeyListener()
    {
      public boolean onKey(View paramAnonymousView, int paramAnonymousInt, KeyEvent paramAnonymousKeyEvent)
      {
        if ((DetailsSupportFragment.this.mVideoSupportFragment != null) && (DetailsSupportFragment.this.mVideoSupportFragment.getView() != null) && (DetailsSupportFragment.this.mVideoSupportFragment.getView().hasFocus()) && ((paramAnonymousInt == 4) || (paramAnonymousInt == 111)) && (DetailsSupportFragment.this.getVerticalGridView().getChildCount() > 0))
        {
          DetailsSupportFragment.this.getVerticalGridView().requestFocus();
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
    if ((this.mVideoSupportFragment != null) && (this.mVideoSupportFragment.getView() != null))
    {
      this.mVideoSupportFragment.getView().requestFocus();
      return;
    }
    this.mStateMachine.fireEvent(this.EVT_SWITCH_TO_VIDEO);
  }
  
  void switchToVideoBeforeVideoSupportFragmentCreated()
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
      if (DetailsSupportFragment.this.mRowsSupportFragment == null) {
        return;
      }
      DetailsSupportFragment.this.mRowsSupportFragment.setSelectedPosition(this.mPosition, this.mSmooth);
    }
  }
  
  static class WaitEnterTransitionTimeout
    implements Runnable
  {
    static final long WAIT_ENTERTRANSITION_START = 200L;
    final WeakReference<DetailsSupportFragment> mRef;
    
    WaitEnterTransitionTimeout(DetailsSupportFragment paramDetailsSupportFragment)
    {
      this.mRef = new WeakReference(paramDetailsSupportFragment);
      paramDetailsSupportFragment.getView().postDelayed(this, 200L);
    }
    
    public void run()
    {
      DetailsSupportFragment localDetailsSupportFragment = (DetailsSupportFragment)this.mRef.get();
      if (localDetailsSupportFragment != null) {
        localDetailsSupportFragment.mStateMachine.fireEvent(localDetailsSupportFragment.EVT_ENTER_TRANSIITON_DONE);
      }
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/app/DetailsSupportFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */