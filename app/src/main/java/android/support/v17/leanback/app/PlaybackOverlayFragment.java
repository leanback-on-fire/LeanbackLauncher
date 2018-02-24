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
import android.support.v17.leanback.R.integer;
import android.support.v17.leanback.R.string;
import android.support.v17.leanback.animation.LogAccelerateInterpolator;
import android.support.v17.leanback.animation.LogDecelerateInterpolator;
import android.support.v17.leanback.media.PlaybackGlueHost.HostCallback;
import android.support.v17.leanback.widget.BaseGridView.OnKeyInterceptListener;
import android.support.v17.leanback.widget.BaseGridView.OnTouchInterceptListener;
import android.support.v17.leanback.widget.ItemAlignmentFacet;
import android.support.v17.leanback.widget.ItemAlignmentFacet.ItemAlignmentDef;
import android.support.v17.leanback.widget.ItemBridgeAdapter.AdapterListener;
import android.support.v17.leanback.widget.ItemBridgeAdapter.ViewHolder;
import android.support.v17.leanback.widget.ObjectAdapter;
import android.support.v17.leanback.widget.ObjectAdapter.DataObserver;
import android.support.v17.leanback.widget.PlaybackControlsRowPresenter;
import android.support.v17.leanback.widget.PlaybackControlsRowPresenter.ViewHolder;
import android.support.v17.leanback.widget.PlaybackRowPresenter;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Presenter.ViewHolder;
import android.support.v17.leanback.widget.VerticalGridView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.InputEvent;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;

@Deprecated
public class PlaybackOverlayFragment
  extends DetailsFragment
{
  private static final int ANIMATION_MULTIPLIER = 1;
  public static final int BG_DARK = 1;
  public static final int BG_LIGHT = 2;
  public static final int BG_NONE = 0;
  static final boolean DEBUG = false;
  static final int IDLE = 0;
  private static final int IN = 1;
  static final int OUT = 2;
  static int START_FADE_OUT = 1;
  static final String TAG = "PlaybackOverlayFragment";
  static final Handler sHandler = new FadeHandler();
  private final ItemBridgeAdapter.AdapterListener mAdapterListener = new ItemBridgeAdapter.AdapterListener()
  {
    public void onAttachedToWindow(ItemBridgeAdapter.ViewHolder paramAnonymousViewHolder)
    {
      if (((PlaybackOverlayFragment.this.mFadingStatus == 0) && (PlaybackOverlayFragment.this.mBgAlpha == 0)) || (PlaybackOverlayFragment.this.mFadingStatus == 2)) {
        paramAnonymousViewHolder.getViewHolder().view.setAlpha(0.0F);
      }
      if ((paramAnonymousViewHolder.getPosition() == 0) && (PlaybackOverlayFragment.this.mResetControlsToPrimaryActionsPending)) {
        PlaybackOverlayFragment.this.resetControlsToPrimaryActions(paramAnonymousViewHolder);
      }
    }
    
    public void onBind(ItemBridgeAdapter.ViewHolder paramAnonymousViewHolder)
    {
      if (paramAnonymousViewHolder.getPosition() == 0) {
        PlaybackOverlayFragment.this.updateControlsBottomSpace(paramAnonymousViewHolder);
      }
    }
    
    public void onDetachedFromWindow(ItemBridgeAdapter.ViewHolder paramAnonymousViewHolder)
    {
      paramAnonymousViewHolder.getViewHolder().view.setAlpha(1.0F);
      paramAnonymousViewHolder.getViewHolder().view.setTranslationY(0.0F);
      if ((paramAnonymousViewHolder.getViewHolder() instanceof PlaybackControlsRowPresenter.ViewHolder))
      {
        paramAnonymousViewHolder = ((PlaybackControlsRowPresenter.ViewHolder)paramAnonymousViewHolder.getViewHolder()).mDescriptionViewHolder;
        if (paramAnonymousViewHolder != null) {
          paramAnonymousViewHolder.view.setAlpha(1.0F);
        }
      }
    }
  };
  int mAnimationTranslateY;
  private int mBackgroundType = 1;
  int mBgAlpha;
  private int mBgDarkColor;
  private ValueAnimator mBgFadeInAnimator;
  private ValueAnimator mBgFadeOutAnimator;
  private int mBgLightColor;
  private ValueAnimator mControlRowFadeInAnimator;
  private ValueAnimator mControlRowFadeOutAnimator;
  private ValueAnimator mDescriptionFadeInAnimator;
  private ValueAnimator mDescriptionFadeOutAnimator;
  OnFadeCompleteListener mFadeCompleteListener;
  private final Animator.AnimatorListener mFadeListener = new Animator.AnimatorListener()
  {
    public void onAnimationCancel(Animator paramAnonymousAnimator) {}
    
    public void onAnimationEnd(Animator paramAnonymousAnimator)
    {
      if (PlaybackOverlayFragment.this.mBgAlpha > 0)
      {
        PlaybackOverlayFragment.this.enableVerticalGridAnimations(true);
        PlaybackOverlayFragment.this.startFadeTimer();
        if (PlaybackOverlayFragment.this.mFadeCompleteListener != null) {
          PlaybackOverlayFragment.this.mFadeCompleteListener.onFadeInComplete();
        }
      }
      for (;;)
      {
        PlaybackOverlayFragment.this.mFadingStatus = 0;
        return;
        paramAnonymousAnimator = PlaybackOverlayFragment.this.getVerticalGridView();
        if ((paramAnonymousAnimator != null) && (paramAnonymousAnimator.getSelectedPosition() == 0)) {
          PlaybackOverlayFragment.this.resetControlsToPrimaryActions(null);
        }
        if (PlaybackOverlayFragment.this.mFadeCompleteListener != null) {
          PlaybackOverlayFragment.this.mFadeCompleteListener.onFadeOutComplete();
        }
      }
    }
    
    public void onAnimationRepeat(Animator paramAnonymousAnimator) {}
    
    public void onAnimationStart(Animator paramAnonymousAnimator)
    {
      PlaybackOverlayFragment.this.enableVerticalGridAnimations(false);
    }
  };
  boolean mFadingEnabled = true;
  int mFadingStatus = 0;
  final WeakReference<PlaybackOverlayFragment> mFragmentReference = new WeakReference(this);
  PlaybackGlueHost.HostCallback mHostCallback;
  private PlaybackControlGlue.InputEventHandler mInputEventHandler;
  private TimeInterpolator mLogAccelerateInterpolator = new LogAccelerateInterpolator(100, 0);
  private TimeInterpolator mLogDecelerateInterpolator = new LogDecelerateInterpolator(100, 0);
  private int mMajorFadeTranslateY;
  private int mMinorFadeTranslateY;
  private final ObjectAdapter.DataObserver mObserver = new ObjectAdapter.DataObserver()
  {
    public void onChanged()
    {
      PlaybackOverlayFragment.this.updateControlsBottomSpace(null);
    }
  };
  private final BaseGridView.OnKeyInterceptListener mOnKeyInterceptListener = new BaseGridView.OnKeyInterceptListener()
  {
    public boolean onInterceptKeyEvent(KeyEvent paramAnonymousKeyEvent)
    {
      return PlaybackOverlayFragment.this.onInterceptInputEvent(paramAnonymousKeyEvent);
    }
  };
  private final BaseGridView.OnTouchInterceptListener mOnTouchInterceptListener = new BaseGridView.OnTouchInterceptListener()
  {
    public boolean onInterceptTouchEvent(MotionEvent paramAnonymousMotionEvent)
    {
      return PlaybackOverlayFragment.this.onInterceptInputEvent(paramAnonymousMotionEvent);
    }
  };
  private ValueAnimator mOtherRowFadeInAnimator;
  private ValueAnimator mOtherRowFadeOutAnimator;
  private int mOtherRowsCenterToBottom;
  private int mPaddingBottom;
  boolean mResetControlsToPrimaryActionsPending;
  private View mRootView;
  private int mShowTimeMs;
  
  private boolean areControlsHidden()
  {
    return (this.mFadingStatus == 0) && (this.mBgAlpha == 0);
  }
  
  private static ValueAnimator loadAnimator(Context paramContext, int paramInt)
  {
    paramContext = (ValueAnimator)AnimatorInflater.loadAnimator(paramContext, paramInt);
    paramContext.setDuration(paramContext.getDuration() * 1L);
    return paramContext;
  }
  
  private void loadBgAnimator()
  {
    ValueAnimator.AnimatorUpdateListener local4 = new ValueAnimator.AnimatorUpdateListener()
    {
      public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
      {
        PlaybackOverlayFragment.this.setBgAlpha(((Integer)paramAnonymousValueAnimator.getAnimatedValue()).intValue());
      }
    };
    Context localContext = FragmentUtil.getContext(this);
    this.mBgFadeInAnimator = loadAnimator(localContext, R.animator.lb_playback_bg_fade_in);
    this.mBgFadeInAnimator.addUpdateListener(local4);
    this.mBgFadeInAnimator.addListener(this.mFadeListener);
    this.mBgFadeOutAnimator = loadAnimator(localContext, R.animator.lb_playback_bg_fade_out);
    this.mBgFadeOutAnimator.addUpdateListener(local4);
    this.mBgFadeOutAnimator.addListener(this.mFadeListener);
  }
  
  private void loadControlRowAnimator()
  {
    AnimatorListener local5 = new AnimatorListener()
    {
      void getViews(ArrayList<View> paramAnonymousArrayList)
      {
        View localView = PlaybackOverlayFragment.this.getControlRowView();
        if (localView != null) {
          paramAnonymousArrayList.add(localView);
        }
      }
    };
    ValueAnimator.AnimatorUpdateListener local6 = new ValueAnimator.AnimatorUpdateListener()
    {
      public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
      {
        View localView = PlaybackOverlayFragment.this.getControlRowView();
        if (localView != null)
        {
          float f = ((Float)paramAnonymousValueAnimator.getAnimatedValue()).floatValue();
          localView.setAlpha(f);
          localView.setTranslationY(PlaybackOverlayFragment.this.mAnimationTranslateY * (1.0F - f));
        }
      }
    };
    Context localContext = FragmentUtil.getContext(this);
    this.mControlRowFadeInAnimator = loadAnimator(localContext, R.animator.lb_playback_controls_fade_in);
    this.mControlRowFadeInAnimator.addUpdateListener(local6);
    this.mControlRowFadeInAnimator.addListener(local5);
    this.mControlRowFadeInAnimator.setInterpolator(this.mLogDecelerateInterpolator);
    this.mControlRowFadeOutAnimator = loadAnimator(localContext, R.animator.lb_playback_controls_fade_out);
    this.mControlRowFadeOutAnimator.addUpdateListener(local6);
    this.mControlRowFadeOutAnimator.addListener(local5);
    this.mControlRowFadeOutAnimator.setInterpolator(this.mLogAccelerateInterpolator);
  }
  
  private void loadDescriptionAnimator()
  {
    ValueAnimator.AnimatorUpdateListener local9 = new ValueAnimator.AnimatorUpdateListener()
    {
      public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
      {
        if (PlaybackOverlayFragment.this.getVerticalGridView() == null) {}
        Object localObject;
        do
        {
          do
          {
            return;
            localObject = (ItemBridgeAdapter.ViewHolder)PlaybackOverlayFragment.this.getVerticalGridView().findViewHolderForPosition(0);
          } while ((localObject == null) || (!(((ItemBridgeAdapter.ViewHolder)localObject).getViewHolder() instanceof PlaybackControlsRowPresenter.ViewHolder)));
          localObject = ((PlaybackControlsRowPresenter.ViewHolder)((ItemBridgeAdapter.ViewHolder)localObject).getViewHolder()).mDescriptionViewHolder;
        } while (localObject == null);
        ((Presenter.ViewHolder)localObject).view.setAlpha(((Float)paramAnonymousValueAnimator.getAnimatedValue()).floatValue());
      }
    };
    Context localContext = FragmentUtil.getContext(this);
    this.mDescriptionFadeInAnimator = loadAnimator(localContext, R.animator.lb_playback_description_fade_in);
    this.mDescriptionFadeInAnimator.addUpdateListener(local9);
    this.mDescriptionFadeInAnimator.setInterpolator(this.mLogDecelerateInterpolator);
    this.mDescriptionFadeOutAnimator = loadAnimator(localContext, R.animator.lb_playback_description_fade_out);
    this.mDescriptionFadeOutAnimator.addUpdateListener(local9);
  }
  
  private void loadOtherRowAnimator()
  {
    final AnimatorListener local7 = new AnimatorListener()
    {
      void getViews(ArrayList<View> paramAnonymousArrayList)
      {
        if (PlaybackOverlayFragment.this.getVerticalGridView() == null) {}
        for (;;)
        {
          return;
          int j = PlaybackOverlayFragment.this.getVerticalGridView().getChildCount();
          int i = 0;
          while (i < j)
          {
            View localView = PlaybackOverlayFragment.this.getVerticalGridView().getChildAt(i);
            if (localView != null) {
              paramAnonymousArrayList.add(localView);
            }
            i += 1;
          }
        }
      }
    };
    ValueAnimator.AnimatorUpdateListener local8 = new ValueAnimator.AnimatorUpdateListener()
    {
      public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
      {
        if (PlaybackOverlayFragment.this.getVerticalGridView() == null) {}
        for (;;)
        {
          return;
          float f = ((Float)paramAnonymousValueAnimator.getAnimatedValue()).floatValue();
          paramAnonymousValueAnimator = local7.mViews.iterator();
          while (paramAnonymousValueAnimator.hasNext())
          {
            View localView = (View)paramAnonymousValueAnimator.next();
            if (PlaybackOverlayFragment.this.getVerticalGridView().getChildPosition(localView) > 0)
            {
              localView.setAlpha(f);
              localView.setTranslationY(PlaybackOverlayFragment.this.mAnimationTranslateY * (1.0F - f));
            }
          }
        }
      }
    };
    Context localContext = FragmentUtil.getContext(this);
    this.mOtherRowFadeInAnimator = loadAnimator(localContext, R.animator.lb_playback_controls_fade_in);
    this.mOtherRowFadeInAnimator.addListener(local7);
    this.mOtherRowFadeInAnimator.addUpdateListener(local8);
    this.mOtherRowFadeInAnimator.setInterpolator(this.mLogDecelerateInterpolator);
    this.mOtherRowFadeOutAnimator = loadAnimator(localContext, R.animator.lb_playback_controls_fade_out);
    this.mOtherRowFadeOutAnimator.addListener(local7);
    this.mOtherRowFadeOutAnimator.addUpdateListener(local8);
    this.mOtherRowFadeOutAnimator.setInterpolator(new AccelerateInterpolator());
  }
  
  private void updateBackground()
  {
    int i;
    if (this.mRootView != null)
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
      this.mRootView.setBackground(new ColorDrawable(i));
      return;
      i = this.mBgLightColor;
      continue;
      i = 0;
    }
  }
  
  void enableVerticalGridAnimations(boolean paramBoolean)
  {
    if (getVerticalGridView() != null) {
      getVerticalGridView().setAnimateChildLayout(paramBoolean);
    }
  }
  
  void fade(boolean paramBoolean)
  {
    if (getView() == null) {}
    while (((paramBoolean) && (this.mFadingStatus == 1)) || ((!paramBoolean) && (this.mFadingStatus == 2)) || ((paramBoolean) && (this.mBgAlpha == 255)) || ((!paramBoolean) && (this.mBgAlpha == 0))) {
      return;
    }
    label116:
    View localView;
    if (getVerticalGridView().getSelectedPosition() == 0)
    {
      i = this.mMajorFadeTranslateY;
      this.mAnimationTranslateY = i;
      if (this.mFadingStatus != 0) {
        break label228;
      }
      if (!paramBoolean) {
        break label197;
      }
      this.mBgFadeInAnimator.start();
      this.mControlRowFadeInAnimator.start();
      this.mOtherRowFadeInAnimator.start();
      this.mDescriptionFadeInAnimator.start();
      localView = getView();
      if (!paramBoolean) {
        break label294;
      }
    }
    label197:
    label228:
    label294:
    for (int i = R.string.lb_playback_controls_shown;; i = R.string.lb_playback_controls_hidden)
    {
      localView.announceForAccessibility(getString(i));
      if ((!paramBoolean) || (this.mFadingStatus != 0)) {
        break label301;
      }
      int j = getVerticalGridView().getChildCount();
      i = 0;
      while (i < j)
      {
        getVerticalGridView().getChildAt(i).setTranslationY(this.mAnimationTranslateY);
        i += 1;
      }
      i = this.mMinorFadeTranslateY;
      break;
      this.mBgFadeOutAnimator.start();
      this.mControlRowFadeOutAnimator.start();
      this.mOtherRowFadeOutAnimator.start();
      this.mDescriptionFadeOutAnimator.start();
      break label116;
      if (paramBoolean)
      {
        this.mBgFadeOutAnimator.reverse();
        this.mControlRowFadeOutAnimator.reverse();
        this.mOtherRowFadeOutAnimator.reverse();
        this.mDescriptionFadeOutAnimator.reverse();
        break label116;
      }
      this.mBgFadeInAnimator.reverse();
      this.mControlRowFadeInAnimator.reverse();
      this.mOtherRowFadeInAnimator.reverse();
      this.mDescriptionFadeInAnimator.reverse();
      break label116;
    }
    label301:
    if (paramBoolean) {}
    for (i = 1;; i = 2)
    {
      this.mFadingStatus = i;
      return;
    }
  }
  
  public void fadeOut()
  {
    sHandler.removeMessages(START_FADE_OUT, this.mFragmentReference);
    fade(false);
  }
  
  public int getBackgroundType()
  {
    return this.mBackgroundType;
  }
  
  View getControlRowView()
  {
    if (getVerticalGridView() == null) {}
    RecyclerView.ViewHolder localViewHolder;
    do
    {
      return null;
      localViewHolder = getVerticalGridView().findViewHolderForPosition(0);
    } while (localViewHolder == null);
    return localViewHolder.itemView;
  }
  
  public final PlaybackControlGlue.InputEventHandler getEventHandler()
  {
    return this.mInputEventHandler;
  }
  
  public OnFadeCompleteListener getFadeCompleteListener()
  {
    return this.mFadeCompleteListener;
  }
  
  @Deprecated
  public final InputEventHandler getInputEventHandler()
  {
    return (InputEventHandler)this.mInputEventHandler;
  }
  
  public boolean isFadingEnabled()
  {
    return this.mFadingEnabled;
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
    loadDescriptionAnimator();
  }
  
  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    this.mRootView = super.onCreateView(paramLayoutInflater, paramViewGroup, paramBundle);
    this.mBgAlpha = 255;
    updateBackground();
    getRowsFragment().setExternalAdapterListener(this.mAdapterListener);
    return this.mRootView;
  }
  
  public void onDestroyView()
  {
    this.mRootView = null;
    if (this.mHostCallback != null) {
      this.mHostCallback.onHostDestroy();
    }
    super.onDestroyView();
  }
  
  boolean onInterceptInputEvent(InputEvent paramInputEvent)
  {
    boolean bool2 = areControlsHidden();
    boolean bool1 = false;
    int i = 0;
    if (this.mInputEventHandler != null) {
      bool1 = this.mInputEventHandler.handleInputEvent(paramInputEvent);
    }
    if ((paramInputEvent instanceof KeyEvent)) {
      i = ((KeyEvent)paramInputEvent).getKeyCode();
    }
    switch (i)
    {
    default: 
      if (bool1) {
        tickle();
      }
      break;
    }
    do
    {
      return bool1;
      if (bool2) {
        bool1 = true;
      }
      tickle();
      return bool1;
      if ((this.mFadingEnabled) && (!bool2))
      {
        sHandler.removeMessages(START_FADE_OUT, this.mFragmentReference);
        fade(false);
        return true;
      }
    } while (!bool1);
    tickle();
    return bool1;
  }
  
  public void onPause()
  {
    if (this.mHostCallback != null) {
      this.mHostCallback.onHostPause();
    }
    super.onPause();
  }
  
  public void onResume()
  {
    super.onResume();
    if (this.mFadingEnabled)
    {
      setBgAlpha(0);
      fade(true);
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
    getRowsFragment().getView().requestFocus();
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
  
  void resetControlsToPrimaryActions(ItemBridgeAdapter.ViewHolder paramViewHolder)
  {
    ItemBridgeAdapter.ViewHolder localViewHolder = paramViewHolder;
    if (paramViewHolder == null)
    {
      localViewHolder = paramViewHolder;
      if (getVerticalGridView() != null) {
        localViewHolder = (ItemBridgeAdapter.ViewHolder)getVerticalGridView().findViewHolderForPosition(0);
      }
    }
    if (localViewHolder == null) {
      this.mResetControlsToPrimaryActionsPending = true;
    }
    while (!(localViewHolder.getPresenter() instanceof PlaybackControlsRowPresenter)) {
      return;
    }
    this.mResetControlsToPrimaryActionsPending = false;
    ((PlaybackControlsRowPresenter)localViewHolder.getPresenter()).showPrimaryActions((PlaybackControlsRowPresenter.ViewHolder)localViewHolder.getViewHolder());
  }
  
  public void setAdapter(ObjectAdapter paramObjectAdapter)
  {
    if (getAdapter() != null) {
      getAdapter().unregisterObserver(this.mObserver);
    }
    super.setAdapter(paramObjectAdapter);
    if (paramObjectAdapter != null) {
      paramObjectAdapter.registerObserver(this.mObserver);
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
  
  void setBgAlpha(int paramInt)
  {
    this.mBgAlpha = paramInt;
    if (this.mRootView != null) {
      this.mRootView.getBackground().setAlpha(paramInt);
    }
  }
  
  public final void setEventHandler(PlaybackControlGlue.InputEventHandler paramInputEventHandler)
  {
    this.mInputEventHandler = paramInputEventHandler;
  }
  
  public void setFadeCompleteListener(OnFadeCompleteListener paramOnFadeCompleteListener)
  {
    this.mFadeCompleteListener = paramOnFadeCompleteListener;
  }
  
  public void setFadingEnabled(boolean paramBoolean)
  {
    if (paramBoolean != this.mFadingEnabled)
    {
      this.mFadingEnabled = paramBoolean;
      if (!this.mFadingEnabled) {
        break label55;
      }
      if ((isResumed()) && (this.mFadingStatus == 0) && (!sHandler.hasMessages(START_FADE_OUT, this.mFragmentReference))) {
        startFadeTimer();
      }
    }
    return;
    label55:
    sHandler.removeMessages(START_FADE_OUT, this.mFragmentReference);
    fade(true);
  }
  
  void setHostCallback(PlaybackGlueHost.HostCallback paramHostCallback)
  {
    this.mHostCallback = paramHostCallback;
  }
  
  @Deprecated
  public final void setInputEventHandler(InputEventHandler paramInputEventHandler)
  {
    this.mInputEventHandler = paramInputEventHandler;
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
  
  protected void setupPresenter(Presenter paramPresenter)
  {
    if ((paramPresenter instanceof PlaybackRowPresenter))
    {
      if (paramPresenter.getFacet(ItemAlignmentFacet.class) == null)
      {
        ItemAlignmentFacet localItemAlignmentFacet = new ItemAlignmentFacet();
        ItemAlignmentFacet.ItemAlignmentDef localItemAlignmentDef = new ItemAlignmentFacet.ItemAlignmentDef();
        localItemAlignmentDef.setItemAlignmentOffset(0);
        localItemAlignmentDef.setItemAlignmentOffsetPercent(100.0F);
        localItemAlignmentFacet.setAlignmentDefs(new ItemAlignmentFacet.ItemAlignmentDef[] { localItemAlignmentDef });
        paramPresenter.setFacet(ItemAlignmentFacet.class, localItemAlignmentFacet);
      }
      return;
    }
    super.setupPresenter(paramPresenter);
  }
  
  void startFadeTimer()
  {
    sHandler.removeMessages(START_FADE_OUT, this.mFragmentReference);
    sHandler.sendMessageDelayed(sHandler.obtainMessage(START_FADE_OUT, this.mFragmentReference), this.mShowTimeMs);
  }
  
  public void tickle()
  {
    if ((!this.mFadingEnabled) || (!isResumed())) {
      return;
    }
    if (sHandler.hasMessages(START_FADE_OUT, this.mFragmentReference))
    {
      startFadeTimer();
      return;
    }
    fade(true);
  }
  
  void updateControlsBottomSpace(ItemBridgeAdapter.ViewHolder paramViewHolder)
  {
    boolean bool = false;
    Object localObject = paramViewHolder;
    if (paramViewHolder == null)
    {
      localObject = paramViewHolder;
      if (getVerticalGridView() != null) {
        localObject = (ItemBridgeAdapter.ViewHolder)getVerticalGridView().findViewHolderForPosition(0);
      }
    }
    if ((localObject != null) && ((((ItemBridgeAdapter.ViewHolder)localObject).getPresenter() instanceof PlaybackControlsRowPresenter))) {
      if (getAdapter() != null) {
        break label91;
      }
    }
    label91:
    for (int i = 0;; i = getAdapter().size())
    {
      paramViewHolder = (PlaybackControlsRowPresenter)((ItemBridgeAdapter.ViewHolder)localObject).getPresenter();
      localObject = (PlaybackControlsRowPresenter.ViewHolder)((ItemBridgeAdapter.ViewHolder)localObject).getViewHolder();
      if (i > 1) {
        bool = true;
      }
      paramViewHolder.showBottomSpace((PlaybackControlsRowPresenter.ViewHolder)localObject, bool);
      return;
    }
  }
  
  static abstract class AnimatorListener
    implements Animator.AnimatorListener
  {
    ArrayList<Integer> mLayerType = new ArrayList();
    ArrayList<View> mViews = new ArrayList();
    
    abstract void getViews(ArrayList<View> paramArrayList);
    
    public void onAnimationCancel(Animator paramAnimator) {}
    
    public void onAnimationEnd(Animator paramAnimator)
    {
      int i = 0;
      while (i < this.mViews.size())
      {
        ((View)this.mViews.get(i)).setLayerType(((Integer)this.mLayerType.get(i)).intValue(), null);
        i += 1;
      }
      this.mLayerType.clear();
      this.mViews.clear();
    }
    
    public void onAnimationRepeat(Animator paramAnimator) {}
    
    public void onAnimationStart(Animator paramAnimator)
    {
      getViews(this.mViews);
      paramAnimator = this.mViews.iterator();
      while (paramAnimator.hasNext())
      {
        View localView = (View)paramAnimator.next();
        this.mLayerType.add(Integer.valueOf(localView.getLayerType()));
        localView.setLayerType(2, null);
      }
    }
  }
  
  static class FadeHandler
    extends Handler
  {
    public void handleMessage(Message paramMessage)
    {
      if (paramMessage.what == PlaybackOverlayFragment.START_FADE_OUT)
      {
        paramMessage = (PlaybackOverlayFragment)((WeakReference)paramMessage.obj).get();
        if ((paramMessage != null) && (paramMessage.mFadingEnabled)) {
          paramMessage.fade(false);
        }
      }
    }
  }
  
  @Deprecated
  public static abstract interface InputEventHandler
    extends PlaybackControlGlue.InputEventHandler
  {}
  
  public static class OnFadeCompleteListener
  {
    public void onFadeInComplete() {}
    
    public void onFadeOutComplete() {}
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/app/PlaybackOverlayFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */