package android.support.v17.leanback.app;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorInflater;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v17.leanback.R.animator;
import android.support.v17.leanback.R.color;
import android.support.v17.leanback.R.dimen;
import android.support.v17.leanback.R.id;
import android.support.v17.leanback.R.integer;
import android.support.v17.leanback.R.layout;
import android.support.v17.leanback.R.string;
import android.support.v17.leanback.animation.LogAccelerateInterpolator;
import android.support.v17.leanback.animation.LogDecelerateInterpolator;
import android.support.v17.leanback.media.PlaybackGlueHost.HostCallback;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.BaseGridView.OnKeyInterceptListener;
import android.support.v17.leanback.widget.BaseGridView.OnTouchInterceptListener;
import android.support.v17.leanback.widget.BaseOnItemViewClickedListener;
import android.support.v17.leanback.widget.BaseOnItemViewSelectedListener;
import android.support.v17.leanback.widget.ClassPresenterSelector;
import android.support.v17.leanback.widget.ItemAlignmentFacet;
import android.support.v17.leanback.widget.ItemAlignmentFacet.ItemAlignmentDef;
import android.support.v17.leanback.widget.ItemBridgeAdapter.AdapterListener;
import android.support.v17.leanback.widget.ItemBridgeAdapter.ViewHolder;
import android.support.v17.leanback.widget.ObjectAdapter;
import android.support.v17.leanback.widget.PlaybackRowPresenter;
import android.support.v17.leanback.widget.PlaybackRowPresenter.ViewHolder;
import android.support.v17.leanback.widget.PlaybackSeekDataProvider;
import android.support.v17.leanback.widget.PlaybackSeekUi;
import android.support.v17.leanback.widget.PlaybackSeekUi.Client;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Presenter.ViewHolder;
import android.support.v17.leanback.widget.PresenterSelector;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter.ViewHolder;
import android.support.v17.leanback.widget.SparseArrayObjectAdapter;
import android.support.v17.leanback.widget.VerticalGridView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.InputEvent;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;

public class PlaybackSupportFragment
  extends Fragment
{
  private static final int ANIMATING = 1;
  private static final int ANIMATION_MULTIPLIER = 1;
  public static final int BG_DARK = 1;
  public static final int BG_LIGHT = 2;
  public static final int BG_NONE = 0;
  static final String BUNDLE_CONTROL_VISIBLE_ON_CREATEVIEW = "controlvisible_oncreateview";
  private static final boolean DEBUG = false;
  private static final int IDLE = 0;
  private static int START_FADE_OUT = 1;
  private static final String TAG = "PlaybackSupportFragment";
  ObjectAdapter mAdapter;
  private final ItemBridgeAdapter.AdapterListener mAdapterListener = new ItemBridgeAdapter.AdapterListener()
  {
    public void onAttachedToWindow(ItemBridgeAdapter.ViewHolder paramAnonymousViewHolder)
    {
      if (!PlaybackSupportFragment.this.mControlVisible) {
        paramAnonymousViewHolder.getViewHolder().view.setAlpha(0.0F);
      }
    }
    
    public void onBind(ItemBridgeAdapter.ViewHolder paramAnonymousViewHolder) {}
    
    public void onCreate(ItemBridgeAdapter.ViewHolder paramAnonymousViewHolder)
    {
      paramAnonymousViewHolder = paramAnonymousViewHolder.getViewHolder();
      if ((paramAnonymousViewHolder instanceof PlaybackSeekUi)) {
        ((PlaybackSeekUi)paramAnonymousViewHolder).setPlaybackSeekUiClient(PlaybackSupportFragment.this.mChainedClient);
      }
    }
    
    public void onDetachedFromWindow(ItemBridgeAdapter.ViewHolder paramAnonymousViewHolder)
    {
      paramAnonymousViewHolder.getViewHolder().view.setAlpha(1.0F);
      paramAnonymousViewHolder.getViewHolder().view.setTranslationY(0.0F);
      paramAnonymousViewHolder.getViewHolder().view.setAlpha(1.0F);
    }
  };
  int mAnimationTranslateY;
  int mBackgroundType = 1;
  View mBackgroundView;
  int mBgAlpha;
  int mBgDarkColor;
  ValueAnimator mBgFadeInAnimator;
  ValueAnimator mBgFadeOutAnimator;
  int mBgLightColor;
  final PlaybackSeekUi.Client mChainedClient = new PlaybackSeekUi.Client()
  {
    public PlaybackSeekDataProvider getPlaybackSeekDataProvider()
    {
      if (PlaybackSupportFragment.this.mSeekUiClient == null) {
        return null;
      }
      return PlaybackSupportFragment.this.mSeekUiClient.getPlaybackSeekDataProvider();
    }
    
    public boolean isSeekEnabled()
    {
      if (PlaybackSupportFragment.this.mSeekUiClient == null) {
        return false;
      }
      return PlaybackSupportFragment.this.mSeekUiClient.isSeekEnabled();
    }
    
    public void onSeekFinished(boolean paramAnonymousBoolean)
    {
      if (PlaybackSupportFragment.this.mSeekUiClient != null) {
        PlaybackSupportFragment.this.mSeekUiClient.onSeekFinished(paramAnonymousBoolean);
      }
      PlaybackSupportFragment.this.setSeekMode(false);
    }
    
    public void onSeekPositionChanged(long paramAnonymousLong)
    {
      if (PlaybackSupportFragment.this.mSeekUiClient != null) {
        PlaybackSupportFragment.this.mSeekUiClient.onSeekPositionChanged(paramAnonymousLong);
      }
    }
    
    public void onSeekStarted()
    {
      if (PlaybackSupportFragment.this.mSeekUiClient != null) {
        PlaybackSupportFragment.this.mSeekUiClient.onSeekStarted();
      }
      PlaybackSupportFragment.this.setSeekMode(true);
    }
  };
  ValueAnimator mControlRowFadeInAnimator;
  ValueAnimator mControlRowFadeOutAnimator;
  boolean mControlVisible = true;
  boolean mControlVisibleBeforeOnCreateView = true;
  BaseOnItemViewClickedListener mExternalItemClickedListener;
  BaseOnItemViewSelectedListener mExternalItemSelectedListener;
  OnFadeCompleteListener mFadeCompleteListener;
  private final Animator.AnimatorListener mFadeListener = new Animator.AnimatorListener()
  {
    public void onAnimationCancel(Animator paramAnonymousAnimator) {}
    
    public void onAnimationEnd(Animator paramAnonymousAnimator)
    {
      if (PlaybackSupportFragment.this.mBgAlpha > 0)
      {
        PlaybackSupportFragment.this.enableVerticalGridAnimations(true);
        if (PlaybackSupportFragment.this.mFadeCompleteListener != null) {
          PlaybackSupportFragment.this.mFadeCompleteListener.onFadeInComplete();
        }
      }
      do
      {
        return;
        paramAnonymousAnimator = PlaybackSupportFragment.this.getVerticalGridView();
        if ((paramAnonymousAnimator != null) && (paramAnonymousAnimator.getSelectedPosition() == 0))
        {
          paramAnonymousAnimator = (ItemBridgeAdapter.ViewHolder)paramAnonymousAnimator.findViewHolderForAdapterPosition(0);
          if ((paramAnonymousAnimator != null) && ((paramAnonymousAnimator.getPresenter() instanceof PlaybackRowPresenter))) {
            ((PlaybackRowPresenter)paramAnonymousAnimator.getPresenter()).onReappear((RowPresenter.ViewHolder)paramAnonymousAnimator.getViewHolder());
          }
        }
      } while (PlaybackSupportFragment.this.mFadeCompleteListener == null);
      PlaybackSupportFragment.this.mFadeCompleteListener.onFadeOutComplete();
    }
    
    public void onAnimationRepeat(Animator paramAnonymousAnimator) {}
    
    public void onAnimationStart(Animator paramAnonymousAnimator)
    {
      PlaybackSupportFragment.this.enableVerticalGridAnimations(false);
    }
  };
  boolean mFadingEnabled = true;
  private final Handler mHandler = new Handler()
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      if ((paramAnonymousMessage.what == PlaybackSupportFragment.START_FADE_OUT) && (PlaybackSupportFragment.this.mFadingEnabled)) {
        PlaybackSupportFragment.this.hideControlsOverlay(true);
      }
    }
  };
  PlaybackGlueHost.HostCallback mHostCallback;
  boolean mInSeek;
  View.OnKeyListener mInputEventHandler;
  private TimeInterpolator mLogAccelerateInterpolator = new LogAccelerateInterpolator(100, 0);
  private TimeInterpolator mLogDecelerateInterpolator = new LogDecelerateInterpolator(100, 0);
  int mMajorFadeTranslateY;
  int mMinorFadeTranslateY;
  private final BaseOnItemViewClickedListener mOnItemViewClickedListener = new BaseOnItemViewClickedListener()
  {
    public void onItemClicked(Presenter.ViewHolder paramAnonymousViewHolder, Object paramAnonymousObject1, RowPresenter.ViewHolder paramAnonymousViewHolder1, Object paramAnonymousObject2)
    {
      if ((PlaybackSupportFragment.this.mPlaybackItemClickedListener != null) && ((paramAnonymousViewHolder1 instanceof PlaybackRowPresenter.ViewHolder))) {
        PlaybackSupportFragment.this.mPlaybackItemClickedListener.onItemClicked(paramAnonymousViewHolder, paramAnonymousObject1, paramAnonymousViewHolder1, paramAnonymousObject2);
      }
      if (PlaybackSupportFragment.this.mExternalItemClickedListener != null) {
        PlaybackSupportFragment.this.mExternalItemClickedListener.onItemClicked(paramAnonymousViewHolder, paramAnonymousObject1, paramAnonymousViewHolder1, paramAnonymousObject2);
      }
    }
  };
  private final BaseOnItemViewSelectedListener mOnItemViewSelectedListener = new BaseOnItemViewSelectedListener()
  {
    public void onItemSelected(Presenter.ViewHolder paramAnonymousViewHolder, Object paramAnonymousObject1, RowPresenter.ViewHolder paramAnonymousViewHolder1, Object paramAnonymousObject2)
    {
      if (PlaybackSupportFragment.this.mExternalItemSelectedListener != null) {
        PlaybackSupportFragment.this.mExternalItemSelectedListener.onItemSelected(paramAnonymousViewHolder, paramAnonymousObject1, paramAnonymousViewHolder1, paramAnonymousObject2);
      }
    }
  };
  private final BaseGridView.OnKeyInterceptListener mOnKeyInterceptListener = new BaseGridView.OnKeyInterceptListener()
  {
    public boolean onInterceptKeyEvent(KeyEvent paramAnonymousKeyEvent)
    {
      return PlaybackSupportFragment.this.onInterceptInputEvent(paramAnonymousKeyEvent);
    }
  };
  private final BaseGridView.OnTouchInterceptListener mOnTouchInterceptListener = new BaseGridView.OnTouchInterceptListener()
  {
    public boolean onInterceptTouchEvent(MotionEvent paramAnonymousMotionEvent)
    {
      return PlaybackSupportFragment.this.onInterceptInputEvent(paramAnonymousMotionEvent);
    }
  };
  ValueAnimator mOtherRowFadeInAnimator;
  ValueAnimator mOtherRowFadeOutAnimator;
  int mOtherRowsCenterToBottom;
  int mPaddingBottom;
  BaseOnItemViewClickedListener mPlaybackItemClickedListener;
  PlaybackRowPresenter mPresenter;
  ProgressBarManager mProgressBarManager = new ProgressBarManager();
  View mRootView;
  Row mRow;
  RowsSupportFragment mRowsSupportFragment;
  PlaybackSeekUi.Client mSeekUiClient;
  private final SetSelectionRunnable mSetSelectionRunnable = new SetSelectionRunnable(null);
  int mShowTimeMs;
  
  public PlaybackSupportFragment()
  {
    this.mProgressBarManager.setInitialDelay(500L);
  }
  
  private void enableVerticalGridAnimations(boolean paramBoolean)
  {
    if (getVerticalGridView() != null) {
      getVerticalGridView().setAnimateChildLayout(paramBoolean);
    }
  }
  
  static void endAll(ValueAnimator paramValueAnimator1, ValueAnimator paramValueAnimator2)
  {
    if (paramValueAnimator1.isStarted()) {
      paramValueAnimator1.end();
    }
    while (!paramValueAnimator2.isStarted()) {
      return;
    }
    paramValueAnimator2.end();
  }
  
  private static ValueAnimator loadAnimator(Context paramContext, int paramInt)
  {
    paramContext = (ValueAnimator)AnimatorInflater.loadAnimator(paramContext, paramInt);
    paramContext.setDuration(paramContext.getDuration() * 1L);
    return paramContext;
  }
  
  private void loadBgAnimator()
  {
    ValueAnimator.AnimatorUpdateListener local7 = new ValueAnimator.AnimatorUpdateListener()
    {
      public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
      {
        PlaybackSupportFragment.this.setBgAlpha(((Integer)paramAnonymousValueAnimator.getAnimatedValue()).intValue());
      }
    };
    Context localContext = getContext();
    this.mBgFadeInAnimator = loadAnimator(localContext, R.animator.lb_playback_bg_fade_in);
    this.mBgFadeInAnimator.addUpdateListener(local7);
    this.mBgFadeInAnimator.addListener(this.mFadeListener);
    this.mBgFadeOutAnimator = loadAnimator(localContext, R.animator.lb_playback_bg_fade_out);
    this.mBgFadeOutAnimator.addUpdateListener(local7);
    this.mBgFadeOutAnimator.addListener(this.mFadeListener);
  }
  
  private void loadControlRowAnimator()
  {
    ValueAnimator.AnimatorUpdateListener local8 = new ValueAnimator.AnimatorUpdateListener()
    {
      public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
      {
        if (PlaybackSupportFragment.this.getVerticalGridView() == null) {}
        Object localObject;
        do
        {
          do
          {
            return;
            localObject = PlaybackSupportFragment.this.getVerticalGridView().findViewHolderForAdapterPosition(0);
          } while (localObject == null);
          localObject = ((RecyclerView.ViewHolder)localObject).itemView;
        } while (localObject == null);
        float f = ((Float)paramAnonymousValueAnimator.getAnimatedValue()).floatValue();
        ((View)localObject).setAlpha(f);
        ((View)localObject).setTranslationY(PlaybackSupportFragment.this.mAnimationTranslateY * (1.0F - f));
      }
    };
    Context localContext = getContext();
    this.mControlRowFadeInAnimator = loadAnimator(localContext, R.animator.lb_playback_controls_fade_in);
    this.mControlRowFadeInAnimator.addUpdateListener(local8);
    this.mControlRowFadeInAnimator.setInterpolator(this.mLogDecelerateInterpolator);
    this.mControlRowFadeOutAnimator = loadAnimator(localContext, R.animator.lb_playback_controls_fade_out);
    this.mControlRowFadeOutAnimator.addUpdateListener(local8);
    this.mControlRowFadeOutAnimator.setInterpolator(this.mLogAccelerateInterpolator);
  }
  
  private void loadOtherRowAnimator()
  {
    ValueAnimator.AnimatorUpdateListener local9 = new ValueAnimator.AnimatorUpdateListener()
    {
      public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
      {
        if (PlaybackSupportFragment.this.getVerticalGridView() == null) {}
        for (;;)
        {
          return;
          float f = ((Float)paramAnonymousValueAnimator.getAnimatedValue()).floatValue();
          int j = PlaybackSupportFragment.this.getVerticalGridView().getChildCount();
          int i = 0;
          while (i < j)
          {
            paramAnonymousValueAnimator = PlaybackSupportFragment.this.getVerticalGridView().getChildAt(i);
            if (PlaybackSupportFragment.this.getVerticalGridView().getChildAdapterPosition(paramAnonymousValueAnimator) > 0)
            {
              paramAnonymousValueAnimator.setAlpha(f);
              paramAnonymousValueAnimator.setTranslationY(PlaybackSupportFragment.this.mAnimationTranslateY * (1.0F - f));
            }
            i += 1;
          }
        }
      }
    };
    Context localContext = getContext();
    this.mOtherRowFadeInAnimator = loadAnimator(localContext, R.animator.lb_playback_controls_fade_in);
    this.mOtherRowFadeInAnimator.addUpdateListener(local9);
    this.mOtherRowFadeInAnimator.setInterpolator(this.mLogDecelerateInterpolator);
    this.mOtherRowFadeOutAnimator = loadAnimator(localContext, R.animator.lb_playback_controls_fade_out);
    this.mOtherRowFadeOutAnimator.addUpdateListener(local9);
    this.mOtherRowFadeOutAnimator.setInterpolator(new AccelerateInterpolator());
  }
  
  private boolean onInterceptInputEvent(InputEvent paramInputEvent)
  {
    boolean bool2 = false;
    int i;
    int j;
    boolean bool1;
    if (!this.mControlVisible)
    {
      i = 1;
      boolean bool3 = false;
      int k = 0;
      j = 0;
      bool1 = bool3;
      if ((paramInputEvent instanceof KeyEvent))
      {
        int m = ((KeyEvent)paramInputEvent).getKeyCode();
        int n = ((KeyEvent)paramInputEvent).getAction();
        bool1 = bool3;
        j = n;
        k = m;
        if (this.mInputEventHandler != null)
        {
          bool1 = this.mInputEventHandler.onKey(getView(), m, (KeyEvent)paramInputEvent);
          k = m;
          j = n;
        }
      }
      switch (k)
      {
      default: 
        bool2 = bool1;
        if (bool1)
        {
          bool2 = bool1;
          if (j == 0)
          {
            tickle();
            bool2 = bool1;
          }
        }
        break;
      }
    }
    for (;;)
    {
      return bool2;
      i = 0;
      break;
      if (i != 0) {
        bool1 = true;
      }
      bool2 = bool1;
      if (j == 0)
      {
        tickle();
        bool2 = bool1;
        continue;
        if (!this.mInSeek)
        {
          bool2 = bool1;
          if (i == 0)
          {
            bool1 = true;
            bool2 = bool1;
            if (((KeyEvent)paramInputEvent).getAction() == 1)
            {
              hideControlsOverlay(true);
              bool2 = bool1;
            }
          }
        }
      }
    }
  }
  
  static void reverseFirstOrStartSecond(ValueAnimator paramValueAnimator1, ValueAnimator paramValueAnimator2, boolean paramBoolean)
  {
    if (paramValueAnimator1.isStarted())
    {
      paramValueAnimator1.reverse();
      if (!paramBoolean) {
        paramValueAnimator1.end();
      }
    }
    do
    {
      return;
      paramValueAnimator2.start();
    } while (paramBoolean);
    paramValueAnimator2.end();
  }
  
  private void setBgAlpha(int paramInt)
  {
    this.mBgAlpha = paramInt;
    if (this.mBackgroundView != null) {
      this.mBackgroundView.getBackground().setAlpha(paramInt);
    }
  }
  
  private void setupChildFragmentLayout()
  {
    setVerticalGridViewLayout(this.mRowsSupportFragment.getVerticalGridView());
  }
  
  private void setupPresenter()
  {
    Object localObject;
    if ((this.mAdapter != null) && (this.mRow != null) && (this.mPresenter != null))
    {
      localObject = this.mAdapter.getPresenterSelector();
      if (localObject != null) {
        break label69;
      }
      localObject = new ClassPresenterSelector();
      ((ClassPresenterSelector)localObject).addClassPresenter(this.mRow.getClass(), this.mPresenter);
      this.mAdapter.setPresenterSelector((PresenterSelector)localObject);
    }
    label69:
    while (!(localObject instanceof ClassPresenterSelector)) {
      return;
    }
    ((ClassPresenterSelector)localObject).addClassPresenter(this.mRow.getClass(), this.mPresenter);
  }
  
  private void setupRow()
  {
    if (((this.mAdapter instanceof ArrayObjectAdapter)) && (this.mRow != null))
    {
      localArrayObjectAdapter = (ArrayObjectAdapter)this.mAdapter;
      if (localArrayObjectAdapter.size() == 0) {
        localArrayObjectAdapter.add(this.mRow);
      }
    }
    while ((!(this.mAdapter instanceof SparseArrayObjectAdapter)) || (this.mRow == null))
    {
      ArrayObjectAdapter localArrayObjectAdapter;
      return;
      localArrayObjectAdapter.replace(0, this.mRow);
      return;
    }
    ((SparseArrayObjectAdapter)this.mAdapter).set(0, this.mRow);
  }
  
  private void startFadeTimer()
  {
    if (this.mHandler != null)
    {
      this.mHandler.removeMessages(START_FADE_OUT);
      this.mHandler.sendEmptyMessageDelayed(START_FADE_OUT, this.mShowTimeMs);
    }
  }
  
  private void stopFadeTimer()
  {
    if (this.mHandler != null) {
      this.mHandler.removeMessages(START_FADE_OUT);
    }
  }
  
  private void updateBackground()
  {
    int i;
    if (this.mBackgroundView != null)
    {
      int j = this.mBgDarkColor;
      i = j;
      switch (this.mBackgroundType)
      {
      default: 
        i = j;
      }
    }
    for (;;)
    {
      this.mBackgroundView.setBackground(new ColorDrawable(i));
      setBgAlpha(this.mBgAlpha);
      return;
      i = this.mBgLightColor;
      continue;
      i = 0;
    }
  }
  
  @Deprecated
  public void fadeOut()
  {
    showControlsOverlay(false, false);
  }
  
  public ObjectAdapter getAdapter()
  {
    return this.mAdapter;
  }
  
  public int getBackgroundType()
  {
    return this.mBackgroundType;
  }
  
  public OnFadeCompleteListener getFadeCompleteListener()
  {
    return this.mFadeCompleteListener;
  }
  
  public ProgressBarManager getProgressBarManager()
  {
    return this.mProgressBarManager;
  }
  
  VerticalGridView getVerticalGridView()
  {
    if (this.mRowsSupportFragment == null) {
      return null;
    }
    return this.mRowsSupportFragment.getVerticalGridView();
  }
  
  public void hideControlsOverlay(boolean paramBoolean)
  {
    showControlsOverlay(false, paramBoolean);
  }
  
  public boolean isControlsOverlayAutoHideEnabled()
  {
    return this.mFadingEnabled;
  }
  
  public boolean isControlsOverlayVisible()
  {
    return this.mControlVisible;
  }
  
  @Deprecated
  public boolean isFadingEnabled()
  {
    return isControlsOverlayAutoHideEnabled();
  }
  
  public void notifyPlaybackRowChanged()
  {
    if (this.mAdapter == null) {
      return;
    }
    this.mAdapter.notifyItemRangeChanged(0, 1);
  }
  
  protected void onBufferingStateChanged(boolean paramBoolean)
  {
    ProgressBarManager localProgressBarManager = getProgressBarManager();
    if (localProgressBarManager != null)
    {
      if (paramBoolean) {
        localProgressBarManager.show();
      }
    }
    else {
      return;
    }
    localProgressBarManager.hide();
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mOtherRowsCenterToBottom = getResources().getDimensionPixelSize(R.dimen.lb_playback_other_rows_center_to_bottom);
    this.mPaddingBottom = getResources().getDimensionPixelSize(R.dimen.lb_playback_controls_padding_bottom);
    this.mBgDarkColor = getResources().getColor(R.color.lb_playback_controls_background_dark);
    this.mBgLightColor = getResources().getColor(R.color.lb_playback_controls_background_light);
    this.mShowTimeMs = getResources().getInteger(R.integer.lb_playback_controls_show_time_ms);
    this.mMajorFadeTranslateY = getResources().getDimensionPixelSize(R.dimen.lb_playback_major_fade_translate_y);
    this.mMinorFadeTranslateY = getResources().getDimensionPixelSize(R.dimen.lb_playback_minor_fade_translate_y);
    loadBgAnimator();
    loadControlRowAnimator();
    loadOtherRowAnimator();
  }
  
  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    this.mRootView = paramLayoutInflater.inflate(R.layout.lb_playback_fragment, paramViewGroup, false);
    this.mBackgroundView = this.mRootView.findViewById(R.id.playback_fragment_background);
    this.mRowsSupportFragment = ((RowsSupportFragment)getChildFragmentManager().findFragmentById(R.id.playback_controls_dock));
    if (this.mRowsSupportFragment == null)
    {
      this.mRowsSupportFragment = new RowsSupportFragment();
      getChildFragmentManager().beginTransaction().replace(R.id.playback_controls_dock, this.mRowsSupportFragment).commit();
    }
    if (this.mAdapter == null) {
      setAdapter(new ArrayObjectAdapter(new ClassPresenterSelector()));
    }
    for (;;)
    {
      this.mRowsSupportFragment.setOnItemViewSelectedListener(this.mOnItemViewSelectedListener);
      this.mRowsSupportFragment.setOnItemViewClickedListener(this.mOnItemViewClickedListener);
      this.mBgAlpha = 255;
      updateBackground();
      this.mRowsSupportFragment.setExternalAdapterListener(this.mAdapterListener);
      paramLayoutInflater = getProgressBarManager();
      if (paramLayoutInflater != null) {
        paramLayoutInflater.setRootView((ViewGroup)this.mRootView);
      }
      return this.mRootView;
      this.mRowsSupportFragment.setAdapter(this.mAdapter);
    }
  }
  
  public void onDestroy()
  {
    if (this.mHostCallback != null) {
      this.mHostCallback.onHostDestroy();
    }
    super.onDestroy();
  }
  
  public void onDestroyView()
  {
    this.mRootView = null;
    this.mBackgroundView = null;
    super.onDestroyView();
  }
  
  protected void onError(int paramInt, CharSequence paramCharSequence) {}
  
  public void onPause()
  {
    if (this.mHostCallback != null) {
      this.mHostCallback.onHostPause();
    }
    if (this.mHandler.hasMessages(START_FADE_OUT)) {
      this.mHandler.removeMessages(START_FADE_OUT);
    }
    super.onPause();
  }
  
  public void onResume()
  {
    super.onResume();
    if ((this.mControlVisible) && (this.mFadingEnabled)) {
      startFadeTimer();
    }
    getVerticalGridView().setOnTouchInterceptListener(this.mOnTouchInterceptListener);
    getVerticalGridView().setOnKeyInterceptListener(this.mOnKeyInterceptListener);
    if (this.mHostCallback != null) {
      this.mHostCallback.onHostResume();
    }
  }
  
  public void onStart()
  {
    super.onStart();
    setupChildFragmentLayout();
    this.mRowsSupportFragment.setAdapter(this.mAdapter);
    if (this.mHostCallback != null) {
      this.mHostCallback.onHostStart();
    }
  }
  
  public void onStop()
  {
    if (this.mHostCallback != null) {
      this.mHostCallback.onHostStop();
    }
    super.onStop();
  }
  
  protected void onVideoSizeChanged(int paramInt1, int paramInt2) {}
  
  public void onViewCreated(View paramView, Bundle paramBundle)
  {
    super.onViewCreated(paramView, paramBundle);
    this.mControlVisible = true;
    if (!this.mControlVisibleBeforeOnCreateView)
    {
      showControlsOverlay(false, false);
      this.mControlVisibleBeforeOnCreateView = true;
    }
  }
  
  public void resetFocus()
  {
    ItemBridgeAdapter.ViewHolder localViewHolder = (ItemBridgeAdapter.ViewHolder)getVerticalGridView().findViewHolderForAdapterPosition(0);
    if ((localViewHolder != null) && ((localViewHolder.getPresenter() instanceof PlaybackRowPresenter))) {
      ((PlaybackRowPresenter)localViewHolder.getPresenter()).onReappear((RowPresenter.ViewHolder)localViewHolder.getViewHolder());
    }
  }
  
  public void setAdapter(ObjectAdapter paramObjectAdapter)
  {
    this.mAdapter = paramObjectAdapter;
    setupRow();
    setupPresenter();
    setPlaybackRowPresenterAlignment();
    if (this.mRowsSupportFragment != null) {
      this.mRowsSupportFragment.setAdapter(paramObjectAdapter);
    }
  }
  
  public void setBackgroundType(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      throw new IllegalArgumentException("Invalid background type");
    }
    if (paramInt != this.mBackgroundType)
    {
      this.mBackgroundType = paramInt;
      updateBackground();
    }
  }
  
  public void setControlsOverlayAutoHideEnabled(boolean paramBoolean)
  {
    if (paramBoolean != this.mFadingEnabled)
    {
      this.mFadingEnabled = paramBoolean;
      if ((isResumed()) && (getView().hasFocus()))
      {
        showControlsOverlay(true);
        if (!paramBoolean) {
          break label44;
        }
        startFadeTimer();
      }
    }
    return;
    label44:
    stopFadeTimer();
  }
  
  public void setFadeCompleteListener(OnFadeCompleteListener paramOnFadeCompleteListener)
  {
    this.mFadeCompleteListener = paramOnFadeCompleteListener;
  }
  
  @Deprecated
  public void setFadingEnabled(boolean paramBoolean)
  {
    setControlsOverlayAutoHideEnabled(paramBoolean);
  }
  
  public void setHostCallback(PlaybackGlueHost.HostCallback paramHostCallback)
  {
    this.mHostCallback = paramHostCallback;
  }
  
  public void setOnItemViewClickedListener(BaseOnItemViewClickedListener paramBaseOnItemViewClickedListener)
  {
    this.mExternalItemClickedListener = paramBaseOnItemViewClickedListener;
  }
  
  public void setOnItemViewSelectedListener(BaseOnItemViewSelectedListener paramBaseOnItemViewSelectedListener)
  {
    this.mExternalItemSelectedListener = paramBaseOnItemViewSelectedListener;
  }
  
  public final void setOnKeyInterceptListener(View.OnKeyListener paramOnKeyListener)
  {
    this.mInputEventHandler = paramOnKeyListener;
  }
  
  public void setOnPlaybackItemViewClickedListener(BaseOnItemViewClickedListener paramBaseOnItemViewClickedListener)
  {
    this.mPlaybackItemClickedListener = paramBaseOnItemViewClickedListener;
  }
  
  public void setPlaybackRow(Row paramRow)
  {
    this.mRow = paramRow;
    setupRow();
    setupPresenter();
  }
  
  public void setPlaybackRowPresenter(PlaybackRowPresenter paramPlaybackRowPresenter)
  {
    this.mPresenter = paramPlaybackRowPresenter;
    setupPresenter();
    setPlaybackRowPresenterAlignment();
  }
  
  void setPlaybackRowPresenterAlignment()
  {
    if ((this.mAdapter != null) && (this.mAdapter.getPresenterSelector() != null))
    {
      Presenter[] arrayOfPresenter = this.mAdapter.getPresenterSelector().getPresenters();
      if (arrayOfPresenter != null)
      {
        int i = 0;
        while (i < arrayOfPresenter.length)
        {
          if (((arrayOfPresenter[i] instanceof PlaybackRowPresenter)) && (arrayOfPresenter[i].getFacet(ItemAlignmentFacet.class) == null))
          {
            ItemAlignmentFacet localItemAlignmentFacet = new ItemAlignmentFacet();
            ItemAlignmentFacet.ItemAlignmentDef localItemAlignmentDef = new ItemAlignmentFacet.ItemAlignmentDef();
            localItemAlignmentDef.setItemAlignmentOffset(0);
            localItemAlignmentDef.setItemAlignmentOffsetPercent(100.0F);
            localItemAlignmentFacet.setAlignmentDefs(new ItemAlignmentFacet.ItemAlignmentDef[] { localItemAlignmentDef });
            arrayOfPresenter[i].setFacet(ItemAlignmentFacet.class, localItemAlignmentFacet);
          }
          i += 1;
        }
      }
    }
  }
  
  public void setPlaybackSeekUiClient(PlaybackSeekUi.Client paramClient)
  {
    this.mSeekUiClient = paramClient;
  }
  
  void setSeekMode(boolean paramBoolean)
  {
    if (this.mInSeek == paramBoolean) {
      return;
    }
    this.mInSeek = paramBoolean;
    getVerticalGridView().setSelectedPosition(0);
    if (this.mInSeek) {
      stopFadeTimer();
    }
    showControlsOverlay(true);
    int k = getVerticalGridView().getChildCount();
    int i = 0;
    label49:
    View localView;
    if (i < k)
    {
      localView = getVerticalGridView().getChildAt(i);
      if (getVerticalGridView().getChildAdapterPosition(localView) > 0) {
        if (!this.mInSeek) {
          break label99;
        }
      }
    }
    label99:
    for (int j = 4;; j = 0)
    {
      localView.setVisibility(j);
      i += 1;
      break label49;
      break;
    }
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
    if (paramVerticalGridView == null) {
      return;
    }
    paramVerticalGridView.setWindowAlignmentOffset(-this.mPaddingBottom);
    paramVerticalGridView.setWindowAlignmentOffsetPercent(-1.0F);
    paramVerticalGridView.setItemAlignmentOffset(this.mOtherRowsCenterToBottom - this.mPaddingBottom);
    paramVerticalGridView.setItemAlignmentOffsetPercent(50.0F);
    paramVerticalGridView.setPadding(paramVerticalGridView.getPaddingLeft(), paramVerticalGridView.getPaddingTop(), paramVerticalGridView.getPaddingRight(), this.mPaddingBottom);
    paramVerticalGridView.setWindowAlignment(2);
  }
  
  public void showControlsOverlay(boolean paramBoolean)
  {
    showControlsOverlay(true, paramBoolean);
  }
  
  void showControlsOverlay(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (getView() == null) {
      this.mControlVisibleBeforeOnCreateView = paramBoolean1;
    }
    do
    {
      return;
      if (!isResumed()) {
        paramBoolean2 = false;
      }
      if (paramBoolean1 != this.mControlVisible) {
        break;
      }
    } while (paramBoolean2);
    endAll(this.mBgFadeInAnimator, this.mBgFadeOutAnimator);
    endAll(this.mControlRowFadeInAnimator, this.mControlRowFadeOutAnimator);
    endAll(this.mOtherRowFadeInAnimator, this.mOtherRowFadeOutAnimator);
    return;
    this.mControlVisible = paramBoolean1;
    if (!this.mControlVisible) {
      stopFadeTimer();
    }
    label106:
    label151:
    View localView;
    if ((getVerticalGridView() == null) || (getVerticalGridView().getSelectedPosition() == 0))
    {
      i = this.mMajorFadeTranslateY;
      this.mAnimationTranslateY = i;
      if (!paramBoolean1) {
        break label188;
      }
      reverseFirstOrStartSecond(this.mBgFadeOutAnimator, this.mBgFadeInAnimator, paramBoolean2);
      reverseFirstOrStartSecond(this.mControlRowFadeOutAnimator, this.mControlRowFadeInAnimator, paramBoolean2);
      reverseFirstOrStartSecond(this.mOtherRowFadeOutAnimator, this.mOtherRowFadeInAnimator, paramBoolean2);
      if (!paramBoolean2) {
        break label225;
      }
      localView = getView();
      if (!paramBoolean1) {
        break label227;
      }
    }
    label188:
    label225:
    label227:
    for (int i = R.string.lb_playback_controls_shown;; i = R.string.lb_playback_controls_hidden)
    {
      localView.announceForAccessibility(getString(i));
      return;
      i = this.mMinorFadeTranslateY;
      break label106;
      reverseFirstOrStartSecond(this.mBgFadeInAnimator, this.mBgFadeOutAnimator, paramBoolean2);
      reverseFirstOrStartSecond(this.mControlRowFadeInAnimator, this.mControlRowFadeOutAnimator, paramBoolean2);
      reverseFirstOrStartSecond(this.mOtherRowFadeInAnimator, this.mOtherRowFadeOutAnimator, paramBoolean2);
      break label151;
      break;
    }
  }
  
  public void tickle()
  {
    stopFadeTimer();
    showControlsOverlay(true);
  }
  
  public static class OnFadeCompleteListener
  {
    public void onFadeInComplete() {}
    
    public void onFadeOutComplete() {}
  }
  
  private class SetSelectionRunnable
    implements Runnable
  {
    int mPosition;
    boolean mSmooth = true;
    
    private SetSelectionRunnable() {}
    
    public void run()
    {
      if (PlaybackSupportFragment.this.mRowsSupportFragment == null) {
        return;
      }
      PlaybackSupportFragment.this.mRowsSupportFragment.setSelectedPosition(this.mPosition, this.mSmooth);
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/app/PlaybackSupportFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */