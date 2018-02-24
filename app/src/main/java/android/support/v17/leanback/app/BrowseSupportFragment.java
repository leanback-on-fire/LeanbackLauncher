package android.support.v17.leanback.app;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v17.leanback.R.dimen;
import android.support.v17.leanback.R.fraction;
import android.support.v17.leanback.R.id;
import android.support.v17.leanback.R.layout;
import android.support.v17.leanback.R.styleable;
import android.support.v17.leanback.R.transition;
import android.support.v17.leanback.transition.TransitionHelper;
import android.support.v17.leanback.transition.TransitionListener;
import android.support.v17.leanback.util.StateMachine;
import android.support.v17.leanback.util.StateMachine.Event;
import android.support.v17.leanback.util.StateMachine.State;
import android.support.v17.leanback.widget.BrowseFrameLayout;
import android.support.v17.leanback.widget.BrowseFrameLayout.OnChildFocusListener;
import android.support.v17.leanback.widget.BrowseFrameLayout.OnFocusSearchListener;
import android.support.v17.leanback.widget.InvisibleRowPresenter;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ObjectAdapter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.OnItemViewSelectedListener;
import android.support.v17.leanback.widget.PageRow;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Presenter.ViewHolder;
import android.support.v17.leanback.widget.Presenter.ViewHolderTask;
import android.support.v17.leanback.widget.PresenterSelector;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowHeaderPresenter.ViewHolder;
import android.support.v17.leanback.widget.RowPresenter.ViewHolder;
import android.support.v17.leanback.widget.ScaleFrameLayout;
import android.support.v17.leanback.widget.TitleViewAdapter;
import android.support.v17.leanback.widget.VerticalGridView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.BackStackEntry;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import java.util.HashMap;
import java.util.Map;

public class BrowseSupportFragment
  extends BaseSupportFragment
{
  private static final String ARG_HEADERS_STATE = BrowseSupportFragment.class.getCanonicalName() + ".headersState";
  private static final String ARG_TITLE;
  private static final String CURRENT_SELECTED_POSITION = "currentSelectedPosition";
  static boolean DEBUG = false;
  public static final int HEADERS_DISABLED = 3;
  public static final int HEADERS_ENABLED = 1;
  public static final int HEADERS_HIDDEN = 2;
  static final String HEADER_SHOW = "headerShow";
  static final String HEADER_STACK_INDEX = "headerStackIndex";
  private static final String IS_PAGE_ROW = "isPageRow";
  private static final String LB_HEADERS_BACKSTACK = "lbHeadersBackStack_";
  static final String TAG = "BrowseSupportFragment";
  final StateMachine.Event EVT_HEADER_VIEW_CREATED = new StateMachine.Event("headerFragmentViewCreated");
  final StateMachine.Event EVT_MAIN_FRAGMENT_VIEW_CREATED = new StateMachine.Event("mainFragmentViewCreated");
  final StateMachine.Event EVT_SCREEN_DATA_READY = new StateMachine.Event("screenDataReady");
  final StateMachine.State STATE_SET_ENTRANCE_START_STATE = new StateMachine.State("SET_ENTRANCE_START_STATE")
  {
    public void run()
    {
      BrowseSupportFragment.this.setEntranceTransitionStartState();
    }
  };
  private ObjectAdapter mAdapter;
  private PresenterSelector mAdapterPresenter;
  BackStackListener mBackStackChangedListener;
  private int mBrandColor = 0;
  private boolean mBrandColorSet;
  BrowseFrameLayout mBrowseFrame;
  BrowseTransitionListener mBrowseTransitionListener;
  boolean mCanShowHeaders = true;
  private int mContainerListAlignTop;
  private int mContainerListMarginStart;
  OnItemViewSelectedListener mExternalOnItemViewSelectedListener;
  private HeadersSupportFragment.OnHeaderClickedListener mHeaderClickedListener = new HeadersSupportFragment.OnHeaderClickedListener()
  {
    public void onHeaderClicked(RowHeaderPresenter.ViewHolder paramAnonymousViewHolder, Row paramAnonymousRow)
    {
      if ((!BrowseSupportFragment.this.mCanShowHeaders) || (!BrowseSupportFragment.this.mShowingHeaders) || (BrowseSupportFragment.this.isInHeadersTransition())) {
        return;
      }
      BrowseSupportFragment.this.startHeadersTransitionInternal(false);
      BrowseSupportFragment.this.mMainFragment.getView().requestFocus();
    }
  };
  private PresenterSelector mHeaderPresenterSelector;
  private HeadersSupportFragment.OnHeaderViewSelectedListener mHeaderViewSelectedListener = new HeadersSupportFragment.OnHeaderViewSelectedListener()
  {
    public void onHeaderSelected(RowHeaderPresenter.ViewHolder paramAnonymousViewHolder, Row paramAnonymousRow)
    {
      int i = BrowseSupportFragment.this.mHeadersSupportFragment.getSelectedPosition();
      if (BrowseSupportFragment.DEBUG) {
        Log.v("BrowseSupportFragment", "header selected position " + i);
      }
      BrowseSupportFragment.this.onRowSelected(i);
    }
  };
  boolean mHeadersBackStackEnabled = true;
  private int mHeadersState = 1;
  HeadersSupportFragment mHeadersSupportFragment;
  Object mHeadersTransition;
  boolean mIsPageRow;
  Fragment mMainFragment;
  MainFragmentAdapter mMainFragmentAdapter;
  private MainFragmentAdapterRegistry mMainFragmentAdapterRegistry = new MainFragmentAdapterRegistry();
  private MainFragmentRowsAdapter mMainFragmentRowsAdapter;
  private boolean mMainFragmentScaleEnabled = true;
  private final BrowseFrameLayout.OnChildFocusListener mOnChildFocusListener = new BrowseFrameLayout.OnChildFocusListener()
  {
    public void onRequestChildFocus(View paramAnonymousView1, View paramAnonymousView2)
    {
      if (BrowseSupportFragment.this.getChildFragmentManager().isDestroyed()) {}
      int i;
      do
      {
        do
        {
          return;
        } while ((!BrowseSupportFragment.this.mCanShowHeaders) || (BrowseSupportFragment.this.isInHeadersTransition()));
        i = paramAnonymousView1.getId();
        if ((i == R.id.browse_container_dock) && (BrowseSupportFragment.this.mShowingHeaders))
        {
          BrowseSupportFragment.this.startHeadersTransitionInternal(false);
          return;
        }
      } while ((i != R.id.browse_headers_dock) || (BrowseSupportFragment.this.mShowingHeaders));
      BrowseSupportFragment.this.startHeadersTransitionInternal(true);
    }
    
    public boolean onRequestFocusInDescendants(int paramAnonymousInt, Rect paramAnonymousRect)
    {
      if (BrowseSupportFragment.this.getChildFragmentManager().isDestroyed()) {}
      while (((BrowseSupportFragment.this.mCanShowHeaders) && (BrowseSupportFragment.this.mShowingHeaders) && (BrowseSupportFragment.this.mHeadersSupportFragment != null) && (BrowseSupportFragment.this.mHeadersSupportFragment.getView() != null) && (BrowseSupportFragment.this.mHeadersSupportFragment.getView().requestFocus(paramAnonymousInt, paramAnonymousRect))) || ((BrowseSupportFragment.this.mMainFragment != null) && (BrowseSupportFragment.this.mMainFragment.getView() != null) && (BrowseSupportFragment.this.mMainFragment.getView().requestFocus(paramAnonymousInt, paramAnonymousRect))) || ((BrowseSupportFragment.this.getTitleView() != null) && (BrowseSupportFragment.this.getTitleView().requestFocus(paramAnonymousInt, paramAnonymousRect)))) {
        return true;
      }
      return false;
    }
  };
  private final BrowseFrameLayout.OnFocusSearchListener mOnFocusSearchListener = new BrowseFrameLayout.OnFocusSearchListener()
  {
    public View onFocusSearch(View paramAnonymousView, int paramAnonymousInt)
    {
      int k = 17;
      int i = 1;
      if ((BrowseSupportFragment.this.mCanShowHeaders) && (BrowseSupportFragment.this.isInHeadersTransition())) {}
      label188:
      label196:
      label265:
      label272:
      label278:
      do
      {
        do
        {
          return paramAnonymousView;
          if (BrowseSupportFragment.DEBUG) {
            Log.v("BrowseSupportFragment", "onFocusSearch focused " + paramAnonymousView + " + direction " + paramAnonymousInt);
          }
          if ((BrowseSupportFragment.this.getTitleView() != null) && (paramAnonymousView != BrowseSupportFragment.this.getTitleView()) && (paramAnonymousInt == 33)) {
            return BrowseSupportFragment.this.getTitleView();
          }
          if ((BrowseSupportFragment.this.getTitleView() != null) && (BrowseSupportFragment.this.getTitleView().hasFocus()) && (paramAnonymousInt == 130))
          {
            if ((BrowseSupportFragment.this.mCanShowHeaders) && (BrowseSupportFragment.this.mShowingHeaders)) {}
            for (paramAnonymousView = BrowseSupportFragment.this.mHeadersSupportFragment.getVerticalGridView();; paramAnonymousView = BrowseSupportFragment.this.mMainFragment.getView()) {
              return paramAnonymousView;
            }
          }
          int j;
          if (ViewCompat.getLayoutDirection(paramAnonymousView) == 1)
          {
            if (i == 0) {
              break label265;
            }
            j = 66;
            if (i == 0) {
              break label272;
            }
          }
          for (i = k;; i = 66)
          {
            if ((!BrowseSupportFragment.this.mCanShowHeaders) || (paramAnonymousInt != j)) {
              break label278;
            }
            if ((BrowseSupportFragment.this.isVerticalScrolling()) || (BrowseSupportFragment.this.mShowingHeaders) || (!BrowseSupportFragment.this.isHeadersDataReady())) {
              break;
            }
            return BrowseSupportFragment.this.mHeadersSupportFragment.getVerticalGridView();
            i = 0;
            break label188;
            j = 17;
            break label196;
          }
          if (paramAnonymousInt != i) {
            break;
          }
        } while ((BrowseSupportFragment.this.isVerticalScrolling()) || (BrowseSupportFragment.this.mMainFragment == null) || (BrowseSupportFragment.this.mMainFragment.getView() == null));
        return BrowseSupportFragment.this.mMainFragment.getView();
      } while ((paramAnonymousInt == 130) && (BrowseSupportFragment.this.mShowingHeaders));
      return null;
    }
  };
  private OnItemViewClickedListener mOnItemViewClickedListener;
  private float mScaleFactor;
  private ScaleFrameLayout mScaleFrameLayout;
  private Object mSceneAfterEntranceTransition;
  Object mSceneWithHeaders;
  Object mSceneWithoutHeaders;
  private int mSelectedPosition = -1;
  private final SetSelectionRunnable mSetSelectionRunnable = new SetSelectionRunnable();
  boolean mShowingHeaders = true;
  String mWithHeadersBackStackName;
  private PresenterSelector mWrappingPresenterSelector;
  
  static
  {
    ARG_TITLE = BrowseSupportFragment.class.getCanonicalName() + ".title";
  }
  
  private void createAndSetWrapperPresenter()
  {
    final PresenterSelector localPresenterSelector = this.mAdapter.getPresenterSelector();
    if (localPresenterSelector == null) {
      throw new IllegalArgumentException("Adapter.getPresenterSelector() is null");
    }
    if (localPresenterSelector == this.mAdapterPresenter) {
      return;
    }
    this.mAdapterPresenter = localPresenterSelector;
    Presenter[] arrayOfPresenter1 = localPresenterSelector.getPresenters();
    final InvisibleRowPresenter localInvisibleRowPresenter = new InvisibleRowPresenter();
    final Presenter[] arrayOfPresenter2 = new Presenter[arrayOfPresenter1.length + 1];
    System.arraycopy(arrayOfPresenter2, 0, arrayOfPresenter1, 0, arrayOfPresenter1.length);
    arrayOfPresenter2[(arrayOfPresenter2.length - 1)] = localInvisibleRowPresenter;
    this.mAdapter.setPresenterSelector(new PresenterSelector()
    {
      public Presenter getPresenter(Object paramAnonymousObject)
      {
        if (((Row)paramAnonymousObject).isRenderedAsRowView()) {
          return localPresenterSelector.getPresenter(paramAnonymousObject);
        }
        return localInvisibleRowPresenter;
      }
      
      public Presenter[] getPresenters()
      {
        return arrayOfPresenter2;
      }
    });
  }
  
  public static Bundle createArgs(Bundle paramBundle, String paramString, int paramInt)
  {
    Bundle localBundle = paramBundle;
    if (paramBundle == null) {
      localBundle = new Bundle();
    }
    localBundle.putString(ARG_TITLE, paramString);
    localBundle.putInt(ARG_HEADERS_STATE, paramInt);
    return localBundle;
  }
  
  private boolean createMainFragment(ObjectAdapter paramObjectAdapter, int paramInt)
  {
    boolean bool2 = true;
    Object localObject = null;
    boolean bool3;
    boolean bool1;
    if (!this.mCanShowHeaders)
    {
      paramObjectAdapter = (ObjectAdapter)localObject;
      bool3 = this.mIsPageRow;
      if ((!this.mCanShowHeaders) || (!(paramObjectAdapter instanceof PageRow))) {
        break label156;
      }
      bool1 = true;
      label39:
      this.mIsPageRow = bool1;
      if (this.mMainFragment != null) {
        break label162;
      }
      bool1 = true;
    }
    for (;;)
    {
      if (bool1)
      {
        this.mMainFragment = this.mMainFragmentAdapterRegistry.createFragment(paramObjectAdapter);
        if (!(this.mMainFragment instanceof MainFragmentAdapterProvider))
        {
          throw new IllegalArgumentException("Fragment must implement MainFragmentAdapterProvider");
          if ((paramObjectAdapter == null) || (paramObjectAdapter.size() == 0)) {
            return false;
          }
          int i;
          if (paramInt < 0) {
            i = 0;
          }
          do
          {
            paramObjectAdapter = paramObjectAdapter.get(i);
            break;
            i = paramInt;
          } while (paramInt < paramObjectAdapter.size());
          throw new IllegalArgumentException(String.format("Invalid position %d requested", new Object[] { Integer.valueOf(paramInt) }));
          label156:
          bool1 = false;
          break label39;
          label162:
          if (bool3)
          {
            bool1 = true;
            continue;
          }
          bool1 = this.mIsPageRow;
          continue;
        }
        this.mMainFragmentAdapter = ((MainFragmentAdapterProvider)this.mMainFragment).getMainFragmentAdapter();
        this.mMainFragmentAdapter.setFragmentHost(new FragmentHostImpl());
        if (this.mIsPageRow) {
          break label276;
        }
        if (!(this.mMainFragment instanceof MainFragmentRowsAdapterProvider)) {
          break label262;
        }
        this.mMainFragmentRowsAdapter = ((MainFragmentRowsAdapterProvider)this.mMainFragment).getMainFragmentRowsAdapter();
        if (this.mMainFragmentRowsAdapter != null) {
          break label270;
        }
        label253:
        this.mIsPageRow = bool2;
      }
    }
    for (;;)
    {
      return bool1;
      label262:
      this.mMainFragmentRowsAdapter = null;
      break;
      label270:
      bool2 = false;
      break label253;
      label276:
      this.mMainFragmentRowsAdapter = null;
    }
  }
  
  private void expandMainFragment(boolean paramBoolean)
  {
    ViewGroup.MarginLayoutParams localMarginLayoutParams = (ViewGroup.MarginLayoutParams)this.mScaleFrameLayout.getLayoutParams();
    int i;
    if (!paramBoolean)
    {
      i = this.mContainerListMarginStart;
      localMarginLayoutParams.setMarginStart(i);
      this.mScaleFrameLayout.setLayoutParams(localMarginLayoutParams);
      this.mMainFragmentAdapter.setExpand(paramBoolean);
      setMainFragmentAlignment();
      if ((paramBoolean) || (!this.mMainFragmentScaleEnabled) || (!this.mMainFragmentAdapter.isScalingEnabled())) {
        break label96;
      }
    }
    label96:
    for (float f = this.mScaleFactor;; f = 1.0F)
    {
      this.mScaleFrameLayout.setLayoutScaleY(f);
      this.mScaleFrameLayout.setChildScale(f);
      return;
      i = 0;
      break;
    }
  }
  
  private void onExpandTransitionStart(boolean paramBoolean, Runnable paramRunnable)
  {
    if (paramBoolean)
    {
      paramRunnable.run();
      return;
    }
    new ExpandPreLayout(paramRunnable, this.mMainFragmentAdapter, getView()).execute();
  }
  
  private void readArguments(Bundle paramBundle)
  {
    if (paramBundle == null) {}
    do
    {
      return;
      if (paramBundle.containsKey(ARG_TITLE)) {
        setTitle(paramBundle.getString(ARG_TITLE));
      }
    } while (!paramBundle.containsKey(ARG_HEADERS_STATE));
    setHeadersState(paramBundle.getInt(ARG_HEADERS_STATE));
  }
  
  private void replaceMainFragment(int paramInt)
  {
    if (createMainFragment(this.mAdapter, paramInt))
    {
      swapToMainFragment();
      if ((this.mCanShowHeaders) && (this.mShowingHeaders)) {
        break label42;
      }
    }
    label42:
    for (boolean bool = true;; bool = false)
    {
      expandMainFragment(bool);
      setupMainFragment();
      return;
    }
  }
  
  private void setHeadersOnScreen(boolean paramBoolean)
  {
    View localView = this.mHeadersSupportFragment.getView();
    ViewGroup.MarginLayoutParams localMarginLayoutParams = (ViewGroup.MarginLayoutParams)localView.getLayoutParams();
    if (paramBoolean) {}
    for (int i = 0;; i = -this.mContainerListMarginStart)
    {
      localMarginLayoutParams.setMarginStart(i);
      localView.setLayoutParams(localMarginLayoutParams);
      return;
    }
  }
  
  private void setMainFragmentAlignment()
  {
    int j = this.mContainerListAlignTop;
    int i = j;
    if (this.mMainFragmentScaleEnabled)
    {
      i = j;
      if (this.mMainFragmentAdapter.isScalingEnabled())
      {
        i = j;
        if (this.mShowingHeaders) {
          i = (int)(j / this.mScaleFactor + 0.5F);
        }
      }
    }
    this.mMainFragmentAdapter.setAlignment(i);
  }
  
  private void setupMainFragment()
  {
    if (this.mMainFragmentRowsAdapter != null)
    {
      if (this.mAdapter != null) {
        this.mMainFragmentRowsAdapter.setAdapter(new ListRowDataAdapter(this.mAdapter));
      }
      this.mMainFragmentRowsAdapter.setOnItemViewSelectedListener(new MainFragmentItemViewSelectedListener(this.mMainFragmentRowsAdapter));
      this.mMainFragmentRowsAdapter.setOnItemViewClickedListener(this.mOnItemViewClickedListener);
    }
  }
  
  private void swapToMainFragment()
  {
    final VerticalGridView localVerticalGridView = this.mHeadersSupportFragment.getVerticalGridView();
    if ((isShowingHeaders()) && (localVerticalGridView != null) && (localVerticalGridView.getScrollState() != 0))
    {
      getChildFragmentManager().beginTransaction().replace(R.id.scale_frame, new Fragment()).commit();
      localVerticalGridView.addOnScrollListener(new RecyclerView.OnScrollListener()
      {
        public void onScrollStateChanged(RecyclerView paramAnonymousRecyclerView, int paramAnonymousInt)
        {
          if (paramAnonymousInt == 0)
          {
            localVerticalGridView.removeOnScrollListener(this);
            paramAnonymousRecyclerView = BrowseSupportFragment.this.getChildFragmentManager();
            if (paramAnonymousRecyclerView.findFragmentById(R.id.scale_frame) != BrowseSupportFragment.this.mMainFragment) {
              paramAnonymousRecyclerView.beginTransaction().replace(R.id.scale_frame, BrowseSupportFragment.this.mMainFragment).commit();
            }
          }
        }
      });
      return;
    }
    getChildFragmentManager().beginTransaction().replace(R.id.scale_frame, this.mMainFragment).commit();
  }
  
  protected Object createEntranceTransition()
  {
    return TransitionHelper.loadTransition(getContext(), R.transition.lb_browse_entrance_transition);
  }
  
  void createHeadersTransition()
  {
    Context localContext = getContext();
    if (this.mShowingHeaders) {}
    for (int i = R.transition.lb_browse_headers_in;; i = R.transition.lb_browse_headers_out)
    {
      this.mHeadersTransition = TransitionHelper.loadTransition(localContext, i);
      TransitionHelper.addTransitionListener(this.mHeadersTransition, new TransitionListener()
      {
        public void onTransitionEnd(Object paramAnonymousObject)
        {
          BrowseSupportFragment.this.mHeadersTransition = null;
          if (BrowseSupportFragment.this.mMainFragmentAdapter != null)
          {
            BrowseSupportFragment.this.mMainFragmentAdapter.onTransitionEnd();
            if ((!BrowseSupportFragment.this.mShowingHeaders) && (BrowseSupportFragment.this.mMainFragment != null))
            {
              paramAnonymousObject = BrowseSupportFragment.this.mMainFragment.getView();
              if ((paramAnonymousObject != null) && (!((View)paramAnonymousObject).hasFocus())) {
                ((View)paramAnonymousObject).requestFocus();
              }
            }
          }
          if (BrowseSupportFragment.this.mHeadersSupportFragment != null)
          {
            BrowseSupportFragment.this.mHeadersSupportFragment.onTransitionEnd();
            if (BrowseSupportFragment.this.mShowingHeaders)
            {
              paramAnonymousObject = BrowseSupportFragment.this.mHeadersSupportFragment.getVerticalGridView();
              if ((paramAnonymousObject != null) && (!((VerticalGridView)paramAnonymousObject).hasFocus())) {
                ((VerticalGridView)paramAnonymousObject).requestFocus();
              }
            }
          }
          BrowseSupportFragment.this.updateTitleViewVisibility();
          if (BrowseSupportFragment.this.mBrowseTransitionListener != null) {
            BrowseSupportFragment.this.mBrowseTransitionListener.onHeadersTransitionStop(BrowseSupportFragment.this.mShowingHeaders);
          }
        }
        
        public void onTransitionStart(Object paramAnonymousObject) {}
      });
      return;
    }
  }
  
  void createStateMachineStates()
  {
    super.createStateMachineStates();
    this.mStateMachine.addState(this.STATE_SET_ENTRANCE_START_STATE);
  }
  
  void createStateMachineTransitions()
  {
    super.createStateMachineTransitions();
    this.mStateMachine.addTransition(this.STATE_ENTRANCE_ON_PREPARED, this.STATE_SET_ENTRANCE_START_STATE, this.EVT_HEADER_VIEW_CREATED);
    this.mStateMachine.addTransition(this.STATE_ENTRANCE_ON_PREPARED, this.STATE_ENTRANCE_ON_PREPARED_ON_CREATEVIEW, this.EVT_MAIN_FRAGMENT_VIEW_CREATED);
    this.mStateMachine.addTransition(this.STATE_ENTRANCE_ON_PREPARED, this.STATE_ENTRANCE_PERFORM, this.EVT_SCREEN_DATA_READY);
  }
  
  public void enableMainFragmentScaling(boolean paramBoolean)
  {
    this.mMainFragmentScaleEnabled = paramBoolean;
  }
  
  @Deprecated
  public void enableRowScaling(boolean paramBoolean)
  {
    enableMainFragmentScaling(paramBoolean);
  }
  
  public ObjectAdapter getAdapter()
  {
    return this.mAdapter;
  }
  
  @ColorInt
  public int getBrandColor()
  {
    return this.mBrandColor;
  }
  
  public int getHeadersState()
  {
    return this.mHeadersState;
  }
  
  public HeadersSupportFragment getHeadersSupportFragment()
  {
    return this.mHeadersSupportFragment;
  }
  
  public Fragment getMainFragment()
  {
    return this.mMainFragment;
  }
  
  public final MainFragmentAdapterRegistry getMainFragmentRegistry()
  {
    return this.mMainFragmentAdapterRegistry;
  }
  
  public OnItemViewClickedListener getOnItemViewClickedListener()
  {
    return this.mOnItemViewClickedListener;
  }
  
  public OnItemViewSelectedListener getOnItemViewSelectedListener()
  {
    return this.mExternalOnItemViewSelectedListener;
  }
  
  public RowsSupportFragment getRowsSupportFragment()
  {
    if ((this.mMainFragment instanceof RowsSupportFragment)) {
      return (RowsSupportFragment)this.mMainFragment;
    }
    return null;
  }
  
  public int getSelectedPosition()
  {
    return this.mSelectedPosition;
  }
  
  public RowPresenter.ViewHolder getSelectedRowViewHolder()
  {
    if (this.mMainFragmentRowsAdapter != null)
    {
      int i = this.mMainFragmentRowsAdapter.getSelectedPosition();
      return this.mMainFragmentRowsAdapter.findRowViewHolderByPosition(i);
    }
    return null;
  }
  
  boolean isFirstRowWithContent(int paramInt)
  {
    if ((this.mAdapter == null) || (this.mAdapter.size() == 0)) {}
    label61:
    for (;;)
    {
      return true;
      int i = 0;
      for (;;)
      {
        if (i >= this.mAdapter.size()) {
          break label61;
        }
        if (((Row)this.mAdapter.get(i)).isRenderedAsRowView())
        {
          if (paramInt == i) {
            break;
          }
          return false;
        }
        i += 1;
      }
    }
  }
  
  boolean isFirstRowWithContentOrPageRow(int paramInt)
  {
    if ((this.mAdapter == null) || (this.mAdapter.size() == 0)) {}
    label70:
    for (;;)
    {
      return true;
      int i = 0;
      for (;;)
      {
        if (i >= this.mAdapter.size()) {
          break label70;
        }
        Row localRow = (Row)this.mAdapter.get(i);
        if ((localRow.isRenderedAsRowView()) || ((localRow instanceof PageRow)))
        {
          if (paramInt == i) {
            break;
          }
          return false;
        }
        i += 1;
      }
    }
  }
  
  final boolean isHeadersDataReady()
  {
    return (this.mAdapter != null) && (this.mAdapter.size() != 0);
  }
  
  public final boolean isHeadersTransitionOnBackEnabled()
  {
    return this.mHeadersBackStackEnabled;
  }
  
  public boolean isInHeadersTransition()
  {
    return this.mHeadersTransition != null;
  }
  
  public boolean isShowingHeaders()
  {
    return this.mShowingHeaders;
  }
  
  boolean isVerticalScrolling()
  {
    return (this.mHeadersSupportFragment.isScrolling()) || (this.mMainFragmentAdapter.isScrolling());
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    Context localContext = getContext();
    TypedArray localTypedArray = localContext.obtainStyledAttributes(R.styleable.LeanbackTheme);
    this.mContainerListMarginStart = ((int)localTypedArray.getDimension(R.styleable.LeanbackTheme_browseRowsMarginStart, localContext.getResources().getDimensionPixelSize(R.dimen.lb_browse_rows_margin_start)));
    this.mContainerListAlignTop = ((int)localTypedArray.getDimension(R.styleable.LeanbackTheme_browseRowsMarginTop, localContext.getResources().getDimensionPixelSize(R.dimen.lb_browse_rows_margin_top)));
    localTypedArray.recycle();
    readArguments(getArguments());
    if (this.mCanShowHeaders)
    {
      if (!this.mHeadersBackStackEnabled) {
        break label161;
      }
      this.mWithHeadersBackStackName = ("lbHeadersBackStack_" + this);
      this.mBackStackChangedListener = new BackStackListener();
      getFragmentManager().addOnBackStackChangedListener(this.mBackStackChangedListener);
      this.mBackStackChangedListener.load(paramBundle);
    }
    for (;;)
    {
      this.mScaleFactor = getResources().getFraction(R.fraction.lb_browse_rows_scale, 1, 1);
      return;
      label161:
      if (paramBundle != null) {
        this.mShowingHeaders = paramBundle.getBoolean("headerShow");
      }
    }
  }
  
  public HeadersSupportFragment onCreateHeadersSupportFragment()
  {
    return new HeadersSupportFragment();
  }
  
  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    boolean bool2 = true;
    Object localObject;
    if (getChildFragmentManager().findFragmentById(R.id.scale_frame) == null)
    {
      this.mHeadersSupportFragment = onCreateHeadersSupportFragment();
      createMainFragment(this.mAdapter, this.mSelectedPosition);
      localObject = getChildFragmentManager().beginTransaction().replace(R.id.browse_headers_dock, this.mHeadersSupportFragment);
      if (this.mMainFragment != null)
      {
        ((FragmentTransaction)localObject).replace(R.id.scale_frame, this.mMainFragment);
        ((FragmentTransaction)localObject).commit();
        label82:
        localObject = this.mHeadersSupportFragment;
        if (this.mCanShowHeaders) {
          break label537;
        }
      }
    }
    label448:
    label515:
    label521:
    label529:
    label537:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      ((HeadersSupportFragment)localObject).setHeadersGone(bool1);
      if (this.mHeaderPresenterSelector != null) {
        this.mHeadersSupportFragment.setPresenterSelector(this.mHeaderPresenterSelector);
      }
      this.mHeadersSupportFragment.setAdapter(this.mAdapter);
      this.mHeadersSupportFragment.setOnHeaderViewSelectedListener(this.mHeaderViewSelectedListener);
      this.mHeadersSupportFragment.setOnHeaderClickedListener(this.mHeaderClickedListener);
      paramViewGroup = paramLayoutInflater.inflate(R.layout.lb_browse_fragment, paramViewGroup, false);
      getProgressBarManager().setRootView((ViewGroup)paramViewGroup);
      this.mBrowseFrame = ((BrowseFrameLayout)paramViewGroup.findViewById(R.id.browse_frame));
      this.mBrowseFrame.setOnChildFocusListener(this.mOnChildFocusListener);
      this.mBrowseFrame.setOnFocusSearchListener(this.mOnFocusSearchListener);
      installTitleView(paramLayoutInflater, this.mBrowseFrame, paramBundle);
      this.mScaleFrameLayout = ((ScaleFrameLayout)paramViewGroup.findViewById(R.id.scale_frame));
      this.mScaleFrameLayout.setPivotX(0.0F);
      this.mScaleFrameLayout.setPivotY(this.mContainerListAlignTop);
      setupMainFragment();
      if (this.mBrandColorSet) {
        this.mHeadersSupportFragment.setBackgroundColor(this.mBrandColor);
      }
      this.mSceneWithHeaders = TransitionHelper.createScene(this.mBrowseFrame, new Runnable()
      {
        public void run()
        {
          BrowseSupportFragment.this.showHeaders(true);
        }
      });
      this.mSceneWithoutHeaders = TransitionHelper.createScene(this.mBrowseFrame, new Runnable()
      {
        public void run()
        {
          BrowseSupportFragment.this.showHeaders(false);
        }
      });
      this.mSceneAfterEntranceTransition = TransitionHelper.createScene(this.mBrowseFrame, new Runnable()
      {
        public void run()
        {
          BrowseSupportFragment.this.setEntranceTransitionEndState();
        }
      });
      return paramViewGroup;
      this.mMainFragmentAdapter = new MainFragmentAdapter(null);
      this.mMainFragmentAdapter.setFragmentHost(new FragmentHostImpl());
      break;
      this.mHeadersSupportFragment = ((HeadersSupportFragment)getChildFragmentManager().findFragmentById(R.id.browse_headers_dock));
      this.mMainFragment = getChildFragmentManager().findFragmentById(R.id.scale_frame);
      this.mMainFragmentAdapter = ((MainFragmentAdapterProvider)this.mMainFragment).getMainFragmentAdapter();
      this.mMainFragmentAdapter.setFragmentHost(new FragmentHostImpl());
      if ((paramBundle != null) && (paramBundle.getBoolean("isPageRow", false)))
      {
        bool1 = true;
        this.mIsPageRow = bool1;
        if (paramBundle == null) {
          break label515;
        }
      }
      for (int i = paramBundle.getInt("currentSelectedPosition", 0);; i = 0)
      {
        this.mSelectedPosition = i;
        if (this.mIsPageRow) {
          break label529;
        }
        if (!(this.mMainFragment instanceof MainFragmentRowsAdapterProvider)) {
          break label521;
        }
        this.mMainFragmentRowsAdapter = ((MainFragmentRowsAdapterProvider)this.mMainFragment).getMainFragmentRowsAdapter();
        break;
        bool1 = false;
        break label448;
      }
      this.mMainFragmentRowsAdapter = null;
      break label82;
      this.mMainFragmentRowsAdapter = null;
      break label82;
    }
  }
  
  public void onDestroy()
  {
    if (this.mBackStackChangedListener != null) {
      getFragmentManager().removeOnBackStackChangedListener(this.mBackStackChangedListener);
    }
    super.onDestroy();
  }
  
  public void onDestroyView()
  {
    this.mMainFragmentRowsAdapter = null;
    this.mMainFragmentAdapter = null;
    this.mMainFragment = null;
    this.mHeadersSupportFragment = null;
    super.onDestroyView();
  }
  
  protected void onEntranceTransitionEnd()
  {
    if (this.mMainFragmentAdapter != null) {
      this.mMainFragmentAdapter.onTransitionEnd();
    }
    if (this.mHeadersSupportFragment != null) {
      this.mHeadersSupportFragment.onTransitionEnd();
    }
  }
  
  protected void onEntranceTransitionPrepare()
  {
    this.mHeadersSupportFragment.onTransitionPrepare();
    this.mMainFragmentAdapter.setEntranceTransitionState(false);
    this.mMainFragmentAdapter.onTransitionPrepare();
  }
  
  protected void onEntranceTransitionStart()
  {
    this.mHeadersSupportFragment.onTransitionStart();
    this.mMainFragmentAdapter.onTransitionStart();
  }
  
  void onRowSelected(int paramInt)
  {
    if (paramInt != this.mSelectedPosition) {
      this.mSetSelectionRunnable.post(paramInt, 0, true);
    }
  }
  
  public void onSaveInstanceState(Bundle paramBundle)
  {
    super.onSaveInstanceState(paramBundle);
    paramBundle.putInt("currentSelectedPosition", this.mSelectedPosition);
    paramBundle.putBoolean("isPageRow", this.mIsPageRow);
    if (this.mBackStackChangedListener != null)
    {
      this.mBackStackChangedListener.save(paramBundle);
      return;
    }
    paramBundle.putBoolean("headerShow", this.mShowingHeaders);
  }
  
  public void onStart()
  {
    super.onStart();
    this.mHeadersSupportFragment.setAlignment(this.mContainerListAlignTop);
    setMainFragmentAlignment();
    if ((this.mCanShowHeaders) && (this.mShowingHeaders) && (this.mHeadersSupportFragment != null) && (this.mHeadersSupportFragment.getView() != null)) {
      this.mHeadersSupportFragment.getView().requestFocus();
    }
    for (;;)
    {
      if (this.mCanShowHeaders) {
        showHeaders(this.mShowingHeaders);
      }
      this.mStateMachine.fireEvent(this.EVT_HEADER_VIEW_CREATED);
      return;
      if (((!this.mCanShowHeaders) || (!this.mShowingHeaders)) && (this.mMainFragment != null) && (this.mMainFragment.getView() != null)) {
        this.mMainFragment.getView().requestFocus();
      }
    }
  }
  
  protected void runEntranceTransition(Object paramObject)
  {
    TransitionHelper.runTransition(this.mSceneAfterEntranceTransition, paramObject);
  }
  
  public void setAdapter(ObjectAdapter paramObjectAdapter)
  {
    this.mAdapter = paramObjectAdapter;
    createAndSetWrapperPresenter();
    if (getView() == null) {}
    do
    {
      return;
      replaceMainFragment(this.mSelectedPosition);
    } while (paramObjectAdapter == null);
    if (this.mMainFragmentRowsAdapter != null) {
      this.mMainFragmentRowsAdapter.setAdapter(new ListRowDataAdapter(paramObjectAdapter));
    }
    this.mHeadersSupportFragment.setAdapter(paramObjectAdapter);
  }
  
  public void setBrandColor(@ColorInt int paramInt)
  {
    this.mBrandColor = paramInt;
    this.mBrandColorSet = true;
    if (this.mHeadersSupportFragment != null) {
      this.mHeadersSupportFragment.setBackgroundColor(this.mBrandColor);
    }
  }
  
  public void setBrowseTransitionListener(BrowseTransitionListener paramBrowseTransitionListener)
  {
    this.mBrowseTransitionListener = paramBrowseTransitionListener;
  }
  
  void setEntranceTransitionEndState()
  {
    setHeadersOnScreen(this.mShowingHeaders);
    setSearchOrbViewOnScreen(true);
    this.mMainFragmentAdapter.setEntranceTransitionState(true);
  }
  
  void setEntranceTransitionStartState()
  {
    setHeadersOnScreen(false);
    setSearchOrbViewOnScreen(false);
  }
  
  public void setHeaderPresenterSelector(PresenterSelector paramPresenterSelector)
  {
    this.mHeaderPresenterSelector = paramPresenterSelector;
    if (this.mHeadersSupportFragment != null) {
      this.mHeadersSupportFragment.setPresenterSelector(this.mHeaderPresenterSelector);
    }
  }
  
  public void setHeadersState(int paramInt)
  {
    boolean bool = true;
    if ((paramInt < 1) || (paramInt > 3)) {
      throw new IllegalArgumentException("Invalid headers state: " + paramInt);
    }
    if (DEBUG) {
      Log.v("BrowseSupportFragment", "setHeadersState " + paramInt);
    }
    HeadersSupportFragment localHeadersSupportFragment;
    if (paramInt != this.mHeadersState)
    {
      this.mHeadersState = paramInt;
      switch (paramInt)
      {
      default: 
        Log.w("BrowseSupportFragment", "Unknown headers state: " + paramInt);
        if (this.mHeadersSupportFragment != null)
        {
          localHeadersSupportFragment = this.mHeadersSupportFragment;
          if (this.mCanShowHeaders) {
            break label202;
          }
        }
        break;
      }
    }
    for (;;)
    {
      localHeadersSupportFragment.setHeadersGone(bool);
      return;
      this.mCanShowHeaders = true;
      this.mShowingHeaders = true;
      break;
      this.mCanShowHeaders = true;
      this.mShowingHeaders = false;
      break;
      this.mCanShowHeaders = false;
      this.mShowingHeaders = false;
      break;
      label202:
      bool = false;
    }
  }
  
  public final void setHeadersTransitionOnBackEnabled(boolean paramBoolean)
  {
    this.mHeadersBackStackEnabled = paramBoolean;
  }
  
  public void setOnItemViewClickedListener(OnItemViewClickedListener paramOnItemViewClickedListener)
  {
    this.mOnItemViewClickedListener = paramOnItemViewClickedListener;
    if (this.mMainFragmentRowsAdapter != null) {
      this.mMainFragmentRowsAdapter.setOnItemViewClickedListener(paramOnItemViewClickedListener);
    }
  }
  
  public void setOnItemViewSelectedListener(OnItemViewSelectedListener paramOnItemViewSelectedListener)
  {
    this.mExternalOnItemViewSelectedListener = paramOnItemViewSelectedListener;
  }
  
  void setSearchOrbViewOnScreen(boolean paramBoolean)
  {
    View localView = getTitleViewAdapter().getSearchAffordanceView();
    ViewGroup.MarginLayoutParams localMarginLayoutParams;
    if (localView != null)
    {
      localMarginLayoutParams = (ViewGroup.MarginLayoutParams)localView.getLayoutParams();
      if (!paramBoolean) {
        break label40;
      }
    }
    label40:
    for (int i = 0;; i = -this.mContainerListMarginStart)
    {
      localMarginLayoutParams.setMarginStart(i);
      localView.setLayoutParams(localMarginLayoutParams);
      return;
    }
  }
  
  public void setSelectedPosition(int paramInt)
  {
    setSelectedPosition(paramInt, true);
  }
  
  public void setSelectedPosition(int paramInt, boolean paramBoolean)
  {
    this.mSetSelectionRunnable.post(paramInt, 1, paramBoolean);
  }
  
  public void setSelectedPosition(int paramInt, boolean paramBoolean, Presenter.ViewHolderTask paramViewHolderTask)
  {
    if (this.mMainFragmentAdapterRegistry == null) {}
    do
    {
      return;
      if (paramViewHolderTask != null) {
        startHeadersTransition(false);
      }
    } while (this.mMainFragmentRowsAdapter == null);
    this.mMainFragmentRowsAdapter.setSelectedPosition(paramInt, paramBoolean, paramViewHolderTask);
  }
  
  void setSelection(int paramInt, boolean paramBoolean)
  {
    if (paramInt == -1) {}
    do
    {
      return;
      this.mSelectedPosition = paramInt;
    } while ((this.mHeadersSupportFragment == null) || (this.mMainFragmentAdapter == null));
    this.mHeadersSupportFragment.setSelectedPosition(paramInt, paramBoolean);
    replaceMainFragment(paramInt);
    if (this.mMainFragmentRowsAdapter != null) {
      this.mMainFragmentRowsAdapter.setSelectedPosition(paramInt, paramBoolean);
    }
    updateTitleViewVisibility();
  }
  
  void showHeaders(boolean paramBoolean)
  {
    if (DEBUG) {
      Log.v("BrowseSupportFragment", "showHeaders " + paramBoolean);
    }
    this.mHeadersSupportFragment.setHeadersEnabled(paramBoolean);
    setHeadersOnScreen(paramBoolean);
    if (!paramBoolean) {}
    for (paramBoolean = true;; paramBoolean = false)
    {
      expandMainFragment(paramBoolean);
      return;
    }
  }
  
  public void startHeadersTransition(boolean paramBoolean)
  {
    if (!this.mCanShowHeaders) {
      throw new IllegalStateException("Cannot start headers transition");
    }
    if ((isInHeadersTransition()) || (this.mShowingHeaders == paramBoolean)) {
      return;
    }
    startHeadersTransitionInternal(paramBoolean);
  }
  
  void startHeadersTransitionInternal(final boolean paramBoolean)
  {
    if (getFragmentManager().isDestroyed()) {}
    while (!isHeadersDataReady()) {
      return;
    }
    this.mShowingHeaders = paramBoolean;
    this.mMainFragmentAdapter.onTransitionPrepare();
    this.mMainFragmentAdapter.onTransitionStart();
    if (!paramBoolean) {}
    for (boolean bool = true;; bool = false)
    {
      onExpandTransitionStart(bool, new Runnable()
      {
        public void run()
        {
          BrowseSupportFragment.this.mHeadersSupportFragment.onTransitionPrepare();
          BrowseSupportFragment.this.mHeadersSupportFragment.onTransitionStart();
          BrowseSupportFragment.this.createHeadersTransition();
          if (BrowseSupportFragment.this.mBrowseTransitionListener != null) {
            BrowseSupportFragment.this.mBrowseTransitionListener.onHeadersTransitionStart(paramBoolean);
          }
          if (paramBoolean)
          {
            localObject = BrowseSupportFragment.this.mSceneWithHeaders;
            TransitionHelper.runTransition(localObject, BrowseSupportFragment.this.mHeadersTransition);
            if (BrowseSupportFragment.this.mHeadersBackStackEnabled)
            {
              if (paramBoolean) {
                break label131;
              }
              BrowseSupportFragment.this.getFragmentManager().beginTransaction().addToBackStack(BrowseSupportFragment.this.mWithHeadersBackStackName).commit();
            }
          }
          label131:
          int i;
          do
          {
            return;
            localObject = BrowseSupportFragment.this.mSceneWithoutHeaders;
            break;
            i = BrowseSupportFragment.this.mBackStackChangedListener.mIndexOfHeadersBackStack;
          } while (i < 0);
          Object localObject = BrowseSupportFragment.this.getFragmentManager().getBackStackEntryAt(i);
          BrowseSupportFragment.this.getFragmentManager().popBackStackImmediate(((FragmentManager.BackStackEntry)localObject).getId(), 1);
        }
      });
      return;
    }
  }
  
  void updateTitleViewVisibility()
  {
    if (!this.mShowingHeaders)
    {
      if ((this.mIsPageRow) && (this.mMainFragmentAdapter != null)) {}
      for (bool1 = this.mMainFragmentAdapter.mFragmentHost.mShowTitleView; bool1; bool1 = isFirstRowWithContent(this.mSelectedPosition))
      {
        showTitle(6);
        return;
      }
      showTitle(false);
      return;
    }
    if ((this.mIsPageRow) && (this.mMainFragmentAdapter != null)) {}
    for (boolean bool1 = this.mMainFragmentAdapter.mFragmentHost.mShowTitleView;; bool1 = isFirstRowWithContent(this.mSelectedPosition))
    {
      boolean bool2 = isFirstRowWithContentOrPageRow(this.mSelectedPosition);
      int i = 0;
      if (bool1) {
        i = 0x0 | 0x2;
      }
      int j = i;
      if (bool2) {
        j = i | 0x4;
      }
      if (j == 0) {
        break;
      }
      showTitle(j);
      return;
    }
    showTitle(false);
  }
  
  final class BackStackListener
    implements FragmentManager.OnBackStackChangedListener
  {
    int mIndexOfHeadersBackStack = -1;
    int mLastEntryCount = BrowseSupportFragment.this.getFragmentManager().getBackStackEntryCount();
    
    BackStackListener() {}
    
    void load(Bundle paramBundle)
    {
      if (paramBundle != null)
      {
        this.mIndexOfHeadersBackStack = paramBundle.getInt("headerStackIndex", -1);
        paramBundle = BrowseSupportFragment.this;
        if (this.mIndexOfHeadersBackStack == -1)
        {
          bool = true;
          paramBundle.mShowingHeaders = bool;
        }
      }
      while (BrowseSupportFragment.this.mShowingHeaders) {
        for (;;)
        {
          return;
          boolean bool = false;
        }
      }
      BrowseSupportFragment.this.getFragmentManager().beginTransaction().addToBackStack(BrowseSupportFragment.this.mWithHeadersBackStackName).commit();
    }
    
    public void onBackStackChanged()
    {
      if (BrowseSupportFragment.this.getFragmentManager() == null)
      {
        Log.w("BrowseSupportFragment", "getFragmentManager() is null, stack:", new Exception());
        return;
      }
      int i = BrowseSupportFragment.this.getFragmentManager().getBackStackEntryCount();
      if (i > this.mLastEntryCount)
      {
        FragmentManager.BackStackEntry localBackStackEntry = BrowseSupportFragment.this.getFragmentManager().getBackStackEntryAt(i - 1);
        if (BrowseSupportFragment.this.mWithHeadersBackStackName.equals(localBackStackEntry.getName())) {
          this.mIndexOfHeadersBackStack = (i - 1);
        }
      }
      for (;;)
      {
        this.mLastEntryCount = i;
        return;
        if ((i < this.mLastEntryCount) && (this.mIndexOfHeadersBackStack >= i))
        {
          if (!BrowseSupportFragment.this.isHeadersDataReady())
          {
            BrowseSupportFragment.this.getFragmentManager().beginTransaction().addToBackStack(BrowseSupportFragment.this.mWithHeadersBackStackName).commit();
            return;
          }
          this.mIndexOfHeadersBackStack = -1;
          if (!BrowseSupportFragment.this.mShowingHeaders) {
            BrowseSupportFragment.this.startHeadersTransitionInternal(true);
          }
        }
      }
    }
    
    void save(Bundle paramBundle)
    {
      paramBundle.putInt("headerStackIndex", this.mIndexOfHeadersBackStack);
    }
  }
  
  public static class BrowseTransitionListener
  {
    public void onHeadersTransitionStart(boolean paramBoolean) {}
    
    public void onHeadersTransitionStop(boolean paramBoolean) {}
  }
  
  private class ExpandPreLayout
    implements ViewTreeObserver.OnPreDrawListener
  {
    static final int STATE_FIRST_DRAW = 1;
    static final int STATE_INIT = 0;
    static final int STATE_SECOND_DRAW = 2;
    private final Runnable mCallback;
    private int mState;
    private final View mView;
    private BrowseSupportFragment.MainFragmentAdapter mainFragmentAdapter;
    
    ExpandPreLayout(Runnable paramRunnable, BrowseSupportFragment.MainFragmentAdapter paramMainFragmentAdapter, View paramView)
    {
      this.mView = paramView;
      this.mCallback = paramRunnable;
      this.mainFragmentAdapter = paramMainFragmentAdapter;
    }
    
    void execute()
    {
      this.mView.getViewTreeObserver().addOnPreDrawListener(this);
      this.mainFragmentAdapter.setExpand(false);
      this.mView.invalidate();
      this.mState = 0;
    }
    
    public boolean onPreDraw()
    {
      if ((BrowseSupportFragment.this.getView() == null) || (BrowseSupportFragment.this.getContext() == null))
      {
        this.mView.getViewTreeObserver().removeOnPreDrawListener(this);
        return true;
      }
      if (this.mState == 0)
      {
        this.mainFragmentAdapter.setExpand(true);
        this.mView.invalidate();
        this.mState = 1;
      }
      for (;;)
      {
        return false;
        if (this.mState == 1)
        {
          this.mCallback.run();
          this.mView.getViewTreeObserver().removeOnPreDrawListener(this);
          this.mState = 2;
        }
      }
    }
  }
  
  public static abstract class FragmentFactory<T extends Fragment>
  {
    public abstract T createFragment(Object paramObject);
  }
  
  public static abstract interface FragmentHost
  {
    public abstract void notifyDataReady(BrowseSupportFragment.MainFragmentAdapter paramMainFragmentAdapter);
    
    public abstract void notifyViewCreated(BrowseSupportFragment.MainFragmentAdapter paramMainFragmentAdapter);
    
    public abstract void showTitleView(boolean paramBoolean);
  }
  
  private final class FragmentHostImpl
    implements BrowseSupportFragment.FragmentHost
  {
    boolean mShowTitleView = true;
    
    FragmentHostImpl() {}
    
    public void notifyDataReady(BrowseSupportFragment.MainFragmentAdapter paramMainFragmentAdapter)
    {
      if ((BrowseSupportFragment.this.mMainFragmentAdapter == null) || (BrowseSupportFragment.this.mMainFragmentAdapter.getFragmentHost() != this)) {}
      while (!BrowseSupportFragment.this.mIsPageRow) {
        return;
      }
      BrowseSupportFragment.this.mStateMachine.fireEvent(BrowseSupportFragment.this.EVT_SCREEN_DATA_READY);
    }
    
    public void notifyViewCreated(BrowseSupportFragment.MainFragmentAdapter paramMainFragmentAdapter)
    {
      BrowseSupportFragment.this.mStateMachine.fireEvent(BrowseSupportFragment.this.EVT_MAIN_FRAGMENT_VIEW_CREATED);
      if (!BrowseSupportFragment.this.mIsPageRow) {
        BrowseSupportFragment.this.mStateMachine.fireEvent(BrowseSupportFragment.this.EVT_SCREEN_DATA_READY);
      }
    }
    
    public void showTitleView(boolean paramBoolean)
    {
      this.mShowTitleView = paramBoolean;
      if ((BrowseSupportFragment.this.mMainFragmentAdapter == null) || (BrowseSupportFragment.this.mMainFragmentAdapter.getFragmentHost() != this)) {}
      while (!BrowseSupportFragment.this.mIsPageRow) {
        return;
      }
      BrowseSupportFragment.this.updateTitleViewVisibility();
    }
  }
  
  public static class ListRowFragmentFactory
    extends BrowseSupportFragment.FragmentFactory<RowsSupportFragment>
  {
    public RowsSupportFragment createFragment(Object paramObject)
    {
      return new RowsSupportFragment();
    }
  }
  
  public static class MainFragmentAdapter<T extends Fragment>
  {
    private final T mFragment;
    BrowseSupportFragment.FragmentHostImpl mFragmentHost;
    private boolean mScalingEnabled;
    
    public MainFragmentAdapter(T paramT)
    {
      this.mFragment = paramT;
    }
    
    public final T getFragment()
    {
      return this.mFragment;
    }
    
    public final BrowseSupportFragment.FragmentHost getFragmentHost()
    {
      return this.mFragmentHost;
    }
    
    public boolean isScalingEnabled()
    {
      return this.mScalingEnabled;
    }
    
    public boolean isScrolling()
    {
      return false;
    }
    
    public void onTransitionEnd() {}
    
    public boolean onTransitionPrepare()
    {
      return false;
    }
    
    public void onTransitionStart() {}
    
    public void setAlignment(int paramInt) {}
    
    public void setEntranceTransitionState(boolean paramBoolean) {}
    
    public void setExpand(boolean paramBoolean) {}
    
    void setFragmentHost(BrowseSupportFragment.FragmentHostImpl paramFragmentHostImpl)
    {
      this.mFragmentHost = paramFragmentHostImpl;
    }
    
    public void setScalingEnabled(boolean paramBoolean)
    {
      this.mScalingEnabled = paramBoolean;
    }
  }
  
  public static abstract interface MainFragmentAdapterProvider
  {
    public abstract BrowseSupportFragment.MainFragmentAdapter getMainFragmentAdapter();
  }
  
  public static final class MainFragmentAdapterRegistry
  {
    private static final BrowseSupportFragment.FragmentFactory sDefaultFragmentFactory = new BrowseSupportFragment.ListRowFragmentFactory();
    private final Map<Class, BrowseSupportFragment.FragmentFactory> mItemToFragmentFactoryMapping = new HashMap();
    
    public MainFragmentAdapterRegistry()
    {
      registerFragment(ListRow.class, sDefaultFragmentFactory);
    }
    
    public Fragment createFragment(Object paramObject)
    {
      if (paramObject == null) {}
      for (BrowseSupportFragment.FragmentFactory localFragmentFactory1 = sDefaultFragmentFactory;; localFragmentFactory1 = (BrowseSupportFragment.FragmentFactory)this.mItemToFragmentFactoryMapping.get(paramObject.getClass()))
      {
        BrowseSupportFragment.FragmentFactory localFragmentFactory2 = localFragmentFactory1;
        if (localFragmentFactory1 == null)
        {
          localFragmentFactory2 = localFragmentFactory1;
          if (!(paramObject instanceof PageRow)) {
            localFragmentFactory2 = sDefaultFragmentFactory;
          }
        }
        return localFragmentFactory2.createFragment(paramObject);
      }
    }
    
    public void registerFragment(Class paramClass, BrowseSupportFragment.FragmentFactory paramFragmentFactory)
    {
      this.mItemToFragmentFactoryMapping.put(paramClass, paramFragmentFactory);
    }
  }
  
  class MainFragmentItemViewSelectedListener
    implements OnItemViewSelectedListener
  {
    BrowseSupportFragment.MainFragmentRowsAdapter mMainFragmentRowsAdapter;
    
    public MainFragmentItemViewSelectedListener(BrowseSupportFragment.MainFragmentRowsAdapter paramMainFragmentRowsAdapter)
    {
      this.mMainFragmentRowsAdapter = paramMainFragmentRowsAdapter;
    }
    
    public void onItemSelected(Presenter.ViewHolder paramViewHolder, Object paramObject, RowPresenter.ViewHolder paramViewHolder1, Row paramRow)
    {
      int i = this.mMainFragmentRowsAdapter.getSelectedPosition();
      if (BrowseSupportFragment.DEBUG) {
        Log.v("BrowseSupportFragment", "row selected position " + i);
      }
      BrowseSupportFragment.this.onRowSelected(i);
      if (BrowseSupportFragment.this.mExternalOnItemViewSelectedListener != null) {
        BrowseSupportFragment.this.mExternalOnItemViewSelectedListener.onItemSelected(paramViewHolder, paramObject, paramViewHolder1, paramRow);
      }
    }
  }
  
  public static class MainFragmentRowsAdapter<T extends Fragment>
  {
    private final T mFragment;
    
    public MainFragmentRowsAdapter(T paramT)
    {
      if (paramT == null) {
        throw new IllegalArgumentException("Fragment can't be null");
      }
      this.mFragment = paramT;
    }
    
    public RowPresenter.ViewHolder findRowViewHolderByPosition(int paramInt)
    {
      return null;
    }
    
    public final T getFragment()
    {
      return this.mFragment;
    }
    
    public int getSelectedPosition()
    {
      return 0;
    }
    
    public void setAdapter(ObjectAdapter paramObjectAdapter) {}
    
    public void setOnItemViewClickedListener(OnItemViewClickedListener paramOnItemViewClickedListener) {}
    
    public void setOnItemViewSelectedListener(OnItemViewSelectedListener paramOnItemViewSelectedListener) {}
    
    public void setSelectedPosition(int paramInt, boolean paramBoolean) {}
    
    public void setSelectedPosition(int paramInt, boolean paramBoolean, Presenter.ViewHolderTask paramViewHolderTask) {}
  }
  
  public static abstract interface MainFragmentRowsAdapterProvider
  {
    public abstract BrowseSupportFragment.MainFragmentRowsAdapter getMainFragmentRowsAdapter();
  }
  
  private class SetSelectionRunnable
    implements Runnable
  {
    static final int TYPE_INTERNAL_SYNC = 0;
    static final int TYPE_INVALID = -1;
    static final int TYPE_USER_REQUEST = 1;
    private int mPosition;
    private boolean mSmooth;
    private int mType;
    
    SetSelectionRunnable()
    {
      reset();
    }
    
    private void reset()
    {
      this.mPosition = -1;
      this.mType = -1;
      this.mSmooth = false;
    }
    
    void post(int paramInt1, int paramInt2, boolean paramBoolean)
    {
      if (paramInt2 >= this.mType)
      {
        this.mPosition = paramInt1;
        this.mType = paramInt2;
        this.mSmooth = paramBoolean;
        BrowseSupportFragment.this.mBrowseFrame.removeCallbacks(this);
        BrowseSupportFragment.this.mBrowseFrame.post(this);
      }
    }
    
    public void run()
    {
      BrowseSupportFragment.this.setSelection(this.mPosition, this.mSmooth);
      reset();
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/app/BrowseSupportFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */