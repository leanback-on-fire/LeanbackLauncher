package android.support.v17.leanback.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.LayerDrawable;
import android.support.annotation.ColorInt;
import android.support.v17.leanback.R.dimen;
import android.support.v17.leanback.R.id;
import android.support.v17.leanback.util.MathUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

class PlaybackControlsPresenter
  extends ControlBarPresenter
{
  private static int sChildMarginBigger;
  private static int sChildMarginBiggest;
  private boolean mMoreActionsEnabled = true;
  
  public PlaybackControlsPresenter(int paramInt)
  {
    super(paramInt);
  }
  
  static void formatTime(long paramLong, StringBuilder paramStringBuilder)
  {
    long l2 = paramLong / 60L;
    long l1 = l2 / 60L;
    paramLong -= l2 * 60L;
    l2 -= l1 * 60L;
    paramStringBuilder.setLength(0);
    if (l1 > 0L)
    {
      paramStringBuilder.append(l1).append(':');
      if (l2 < 10L) {
        paramStringBuilder.append('0');
      }
    }
    paramStringBuilder.append(l2).append(':');
    if (paramLong < 10L) {
      paramStringBuilder.append('0');
    }
    paramStringBuilder.append(paramLong);
  }
  
  public boolean areMoreActionsEnabled()
  {
    return this.mMoreActionsEnabled;
  }
  
  public void enableSecondaryActions(boolean paramBoolean)
  {
    this.mMoreActionsEnabled = paramBoolean;
  }
  
  public void enableTimeMargins(ViewHolder paramViewHolder, boolean paramBoolean)
  {
    int j = 0;
    ViewGroup.MarginLayoutParams localMarginLayoutParams = (ViewGroup.MarginLayoutParams)paramViewHolder.mCurrentTime.getLayoutParams();
    if (paramBoolean) {}
    for (int i = paramViewHolder.mCurrentTimeMarginStart;; i = 0)
    {
      localMarginLayoutParams.setMarginStart(i);
      paramViewHolder.mCurrentTime.setLayoutParams(localMarginLayoutParams);
      localMarginLayoutParams = (ViewGroup.MarginLayoutParams)paramViewHolder.mTotalTime.getLayoutParams();
      i = j;
      if (paramBoolean) {
        i = paramViewHolder.mTotalTimeMarginEnd;
      }
      localMarginLayoutParams.setMarginEnd(i);
      paramViewHolder.mTotalTime.setLayoutParams(localMarginLayoutParams);
      return;
    }
  }
  
  int getChildMarginBigger(Context paramContext)
  {
    if (sChildMarginBigger == 0) {
      sChildMarginBigger = paramContext.getResources().getDimensionPixelSize(R.dimen.lb_playback_controls_child_margin_bigger);
    }
    return sChildMarginBigger;
  }
  
  int getChildMarginBiggest(Context paramContext)
  {
    if (sChildMarginBiggest == 0) {
      sChildMarginBiggest = paramContext.getResources().getDimensionPixelSize(R.dimen.lb_playback_controls_child_margin_biggest);
    }
    return sChildMarginBiggest;
  }
  
  public int getCurrentTime(ViewHolder paramViewHolder)
  {
    return MathUtil.safeLongToInt(getCurrentTimeLong(paramViewHolder));
  }
  
  public long getCurrentTimeLong(ViewHolder paramViewHolder)
  {
    return paramViewHolder.getCurrentTime();
  }
  
  public int getSecondaryProgress(ViewHolder paramViewHolder)
  {
    return MathUtil.safeLongToInt(getSecondaryProgressLong(paramViewHolder));
  }
  
  public long getSecondaryProgressLong(ViewHolder paramViewHolder)
  {
    return paramViewHolder.getSecondaryProgress();
  }
  
  public int getTotalTime(ViewHolder paramViewHolder)
  {
    return MathUtil.safeLongToInt(getTotalTimeLong(paramViewHolder));
  }
  
  public long getTotalTimeLong(ViewHolder paramViewHolder)
  {
    return paramViewHolder.getTotalTime();
  }
  
  public void onBindViewHolder(Presenter.ViewHolder paramViewHolder, Object paramObject)
  {
    ViewHolder localViewHolder = (ViewHolder)paramViewHolder;
    BoundData localBoundData = (BoundData)paramObject;
    if (localViewHolder.mMoreActionsAdapter != localBoundData.secondaryActionsAdapter)
    {
      localViewHolder.mMoreActionsAdapter = localBoundData.secondaryActionsAdapter;
      localViewHolder.mMoreActionsAdapter.registerObserver(localViewHolder.mMoreActionsObserver);
      localViewHolder.mMoreActionsShowing = false;
    }
    super.onBindViewHolder(paramViewHolder, paramObject);
    localViewHolder.showMoreActions(this.mMoreActionsEnabled);
  }
  
  public Presenter.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup)
  {
    return new ViewHolder(LayoutInflater.from(paramViewGroup.getContext()).inflate(getLayoutResourceId(), paramViewGroup, false));
  }
  
  public void onUnbindViewHolder(Presenter.ViewHolder paramViewHolder)
  {
    super.onUnbindViewHolder(paramViewHolder);
    paramViewHolder = (ViewHolder)paramViewHolder;
    if (paramViewHolder.mMoreActionsAdapter != null)
    {
      paramViewHolder.mMoreActionsAdapter.unregisterObserver(paramViewHolder.mMoreActionsObserver);
      paramViewHolder.mMoreActionsAdapter = null;
    }
  }
  
  public void resetFocus(ViewHolder paramViewHolder)
  {
    paramViewHolder.mControlBar.requestFocus();
  }
  
  public void setCurrentTime(ViewHolder paramViewHolder, int paramInt)
  {
    setCurrentTimeLong(paramViewHolder, paramInt);
  }
  
  public void setCurrentTimeLong(ViewHolder paramViewHolder, long paramLong)
  {
    paramViewHolder.setCurrentTime(paramLong);
  }
  
  public void setProgressColor(ViewHolder paramViewHolder, @ColorInt int paramInt)
  {
    ClipDrawable localClipDrawable = new ClipDrawable(new ColorDrawable(paramInt), 3, 1);
    ((LayerDrawable)paramViewHolder.mProgressBar.getProgressDrawable()).setDrawableByLayerId(16908301, localClipDrawable);
  }
  
  public void setSecondaryProgress(ViewHolder paramViewHolder, int paramInt)
  {
    setSecondaryProgressLong(paramViewHolder, paramInt);
  }
  
  public void setSecondaryProgressLong(ViewHolder paramViewHolder, long paramLong)
  {
    paramViewHolder.setSecondaryProgress(paramLong);
  }
  
  public void setTotalTime(ViewHolder paramViewHolder, int paramInt)
  {
    setTotalTimeLong(paramViewHolder, paramInt);
  }
  
  public void setTotalTimeLong(ViewHolder paramViewHolder, long paramLong)
  {
    paramViewHolder.setTotalTime(paramLong);
  }
  
  public void showPrimaryActions(ViewHolder paramViewHolder)
  {
    if (paramViewHolder.mMoreActionsShowing) {
      paramViewHolder.toggleMoreActions();
    }
  }
  
  static class BoundData
    extends ControlBarPresenter.BoundData
  {
    ObjectAdapter secondaryActionsAdapter;
  }
  
  class ViewHolder
    extends ControlBarPresenter.ViewHolder
  {
    final TextView mCurrentTime;
    long mCurrentTimeInMs = -1L;
    int mCurrentTimeMarginStart;
    StringBuilder mCurrentTimeStringBuilder = new StringBuilder();
    ObjectAdapter mMoreActionsAdapter;
    final FrameLayout mMoreActionsDock;
    ObjectAdapter.DataObserver mMoreActionsObserver;
    boolean mMoreActionsShowing;
    Presenter.ViewHolder mMoreActionsViewHolder;
    final ProgressBar mProgressBar;
    long mSecondaryProgressInMs = -1L;
    final TextView mTotalTime;
    long mTotalTimeInMs = -1L;
    int mTotalTimeMarginEnd;
    StringBuilder mTotalTimeStringBuilder = new StringBuilder();
    
    ViewHolder(View paramView)
    {
      super(paramView);
      this.mMoreActionsDock = ((FrameLayout)paramView.findViewById(R.id.more_actions_dock));
      this.mCurrentTime = ((TextView)paramView.findViewById(R.id.current_time));
      this.mTotalTime = ((TextView)paramView.findViewById(R.id.total_time));
      this.mProgressBar = ((ProgressBar)paramView.findViewById(R.id.playback_progress));
      this.mMoreActionsObserver = new ObjectAdapter.DataObserver()
      {
        public void onChanged()
        {
          if (PlaybackControlsPresenter.ViewHolder.this.mMoreActionsShowing) {
            PlaybackControlsPresenter.ViewHolder.this.showControls(PlaybackControlsPresenter.ViewHolder.this.mPresenter);
          }
        }
        
        public void onItemRangeChanged(int paramAnonymousInt1, int paramAnonymousInt2)
        {
          if (PlaybackControlsPresenter.ViewHolder.this.mMoreActionsShowing)
          {
            int i = 0;
            while (i < paramAnonymousInt2)
            {
              PlaybackControlsPresenter.ViewHolder.this.bindControlToAction(paramAnonymousInt1 + i, PlaybackControlsPresenter.ViewHolder.this.mPresenter);
              i += 1;
            }
          }
        }
      };
      this.mCurrentTimeMarginStart = ((ViewGroup.MarginLayoutParams)this.mCurrentTime.getLayoutParams()).getMarginStart();
      this.mTotalTimeMarginEnd = ((ViewGroup.MarginLayoutParams)this.mTotalTime.getLayoutParams()).getMarginEnd();
    }
    
    int getChildMarginFromCenter(Context paramContext, int paramInt)
    {
      int i = PlaybackControlsPresenter.this.getControlIconWidth(paramContext);
      if (paramInt < 4) {
        return i + PlaybackControlsPresenter.this.getChildMarginBiggest(paramContext);
      }
      if (paramInt < 6) {
        return i + PlaybackControlsPresenter.this.getChildMarginBigger(paramContext);
      }
      return i + PlaybackControlsPresenter.this.getChildMarginDefault(paramContext);
    }
    
    long getCurrentTime()
    {
      return this.mTotalTimeInMs;
    }
    
    ObjectAdapter getDisplayedAdapter()
    {
      if (this.mMoreActionsShowing) {
        return this.mMoreActionsAdapter;
      }
      return this.mAdapter;
    }
    
    long getSecondaryProgress()
    {
      return this.mSecondaryProgressInMs;
    }
    
    long getTotalTime()
    {
      return this.mTotalTimeInMs;
    }
    
    void setCurrentTime(long paramLong)
    {
      long l = paramLong / 1000L;
      if (paramLong != this.mCurrentTimeInMs)
      {
        this.mCurrentTimeInMs = paramLong;
        PlaybackControlsPresenter.formatTime(l, this.mCurrentTimeStringBuilder);
        this.mCurrentTime.setText(this.mCurrentTimeStringBuilder.toString());
      }
      double d = this.mCurrentTimeInMs / this.mTotalTimeInMs;
      this.mProgressBar.setProgress((int)(d * 2.147483647E9D));
    }
    
    void setSecondaryProgress(long paramLong)
    {
      this.mSecondaryProgressInMs = paramLong;
      double d = paramLong / this.mTotalTimeInMs;
      this.mProgressBar.setSecondaryProgress((int)(d * 2.147483647E9D));
    }
    
    void setTotalTime(long paramLong)
    {
      if (paramLong <= 0L)
      {
        this.mTotalTime.setVisibility(8);
        this.mProgressBar.setVisibility(8);
        return;
      }
      this.mTotalTime.setVisibility(0);
      this.mProgressBar.setVisibility(0);
      this.mTotalTimeInMs = paramLong;
      PlaybackControlsPresenter.formatTime(paramLong / 1000L, this.mTotalTimeStringBuilder);
      this.mTotalTime.setText(this.mTotalTimeStringBuilder.toString());
      this.mProgressBar.setMax(Integer.MAX_VALUE);
    }
    
    void showMoreActions(boolean paramBoolean)
    {
      if (paramBoolean)
      {
        if (this.mMoreActionsViewHolder == null)
        {
          localMoreActions = new PlaybackControlsRow.MoreActions(this.mMoreActionsDock.getContext());
          this.mMoreActionsViewHolder = this.mPresenter.onCreateViewHolder(this.mMoreActionsDock);
          this.mPresenter.onBindViewHolder(this.mMoreActionsViewHolder, localMoreActions);
          this.mPresenter.setOnClickListener(this.mMoreActionsViewHolder, new View.OnClickListener()
          {
            public void onClick(View paramAnonymousView)
            {
              PlaybackControlsPresenter.ViewHolder.this.toggleMoreActions();
            }
          });
        }
        if (this.mMoreActionsViewHolder.view.getParent() == null) {
          this.mMoreActionsDock.addView(this.mMoreActionsViewHolder.view);
        }
      }
      while ((this.mMoreActionsViewHolder == null) || (this.mMoreActionsViewHolder.view.getParent() == null))
      {
        PlaybackControlsRow.MoreActions localMoreActions;
        return;
      }
      this.mMoreActionsDock.removeView(this.mMoreActionsViewHolder.view);
    }
    
    void toggleMoreActions()
    {
      if (!this.mMoreActionsShowing) {}
      for (boolean bool = true;; bool = false)
      {
        this.mMoreActionsShowing = bool;
        showControls(this.mPresenter);
        return;
      }
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/PlaybackControlsPresenter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */