package android.support.v17.leanback.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.graphics.Bitmap;
import android.os.Build.VERSION;
import android.support.annotation.ColorInt;
import android.support.v17.leanback.R.attr;
import android.support.v17.leanback.R.color;
import android.support.v17.leanback.R.id;
import android.support.v17.leanback.R.layout;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.Arrays;

public class PlaybackTransportRowPresenter
  extends PlaybackRowPresenter
{
  float mDefaultSeekIncrement = 0.01F;
  Presenter mDescriptionPresenter;
  OnActionClickedListener mOnActionClickedListener;
  private final ControlBarPresenter.OnControlClickedListener mOnControlClickedListener = new ControlBarPresenter.OnControlClickedListener()
  {
    public void onControlClicked(Presenter.ViewHolder paramAnonymousViewHolder, Object paramAnonymousObject, ControlBarPresenter.BoundData paramAnonymousBoundData)
    {
      paramAnonymousBoundData = ((PlaybackTransportRowPresenter.BoundData)paramAnonymousBoundData).mRowViewHolder;
      if (paramAnonymousBoundData.getOnItemViewClickedListener() != null) {
        paramAnonymousBoundData.getOnItemViewClickedListener().onItemClicked(paramAnonymousViewHolder, paramAnonymousObject, paramAnonymousBoundData, paramAnonymousBoundData.getRow());
      }
      if ((PlaybackTransportRowPresenter.this.mOnActionClickedListener != null) && ((paramAnonymousObject instanceof Action))) {
        PlaybackTransportRowPresenter.this.mOnActionClickedListener.onActionClicked((Action)paramAnonymousObject);
      }
    }
  };
  private final ControlBarPresenter.OnControlSelectedListener mOnControlSelectedListener = new ControlBarPresenter.OnControlSelectedListener()
  {
    public void onControlSelected(Presenter.ViewHolder paramAnonymousViewHolder, Object paramAnonymousObject, ControlBarPresenter.BoundData paramAnonymousBoundData)
    {
      paramAnonymousBoundData = ((PlaybackTransportRowPresenter.BoundData)paramAnonymousBoundData).mRowViewHolder;
      if ((paramAnonymousBoundData.mSelectedViewHolder != paramAnonymousViewHolder) || (paramAnonymousBoundData.mSelectedItem != paramAnonymousObject))
      {
        paramAnonymousBoundData.mSelectedViewHolder = paramAnonymousViewHolder;
        paramAnonymousBoundData.mSelectedItem = paramAnonymousObject;
        paramAnonymousBoundData.dispatchItemSelection();
      }
    }
  };
  ControlBarPresenter mPlaybackControlsPresenter;
  int mProgressColor = 0;
  boolean mProgressColorSet;
  ControlBarPresenter mSecondaryControlsPresenter;
  
  public PlaybackTransportRowPresenter()
  {
    setHeaderPresenter(null);
    setSelectEffectEnabled(false);
    this.mPlaybackControlsPresenter = new ControlBarPresenter(R.layout.lb_control_bar);
    this.mPlaybackControlsPresenter.setDefaultFocusToMiddle(false);
    this.mSecondaryControlsPresenter = new ControlBarPresenter(R.layout.lb_control_bar);
    this.mSecondaryControlsPresenter.setDefaultFocusToMiddle(false);
    this.mPlaybackControlsPresenter.setOnControlSelectedListener(this.mOnControlSelectedListener);
    this.mSecondaryControlsPresenter.setOnControlSelectedListener(this.mOnControlSelectedListener);
    this.mPlaybackControlsPresenter.setOnControlClickedListener(this.mOnControlClickedListener);
    this.mSecondaryControlsPresenter.setOnControlClickedListener(this.mOnControlClickedListener);
  }
  
  static void formatTime(long paramLong, StringBuilder paramStringBuilder)
  {
    paramStringBuilder.setLength(0);
    if (paramLong < 0L)
    {
      paramStringBuilder.append("--");
      return;
    }
    long l2 = paramLong / 1000L;
    long l1 = l2 / 60L;
    paramLong = l1 / 60L;
    l2 -= 60L * l1;
    l1 -= 60L * paramLong;
    if (paramLong > 0L)
    {
      paramStringBuilder.append(paramLong).append(':');
      if (l1 < 10L) {
        paramStringBuilder.append('0');
      }
    }
    paramStringBuilder.append(l1).append(':');
    if (l2 < 10L) {
      paramStringBuilder.append('0');
    }
    paramStringBuilder.append(l2);
  }
  
  private int getDefaultProgressColor(Context paramContext)
  {
    TypedValue localTypedValue = new TypedValue();
    if (paramContext.getTheme().resolveAttribute(R.attr.playbackProgressPrimaryColor, localTypedValue, true)) {
      return paramContext.getResources().getColor(localTypedValue.resourceId);
    }
    return paramContext.getResources().getColor(R.color.lb_playback_progress_color_no_theme);
  }
  
  private void initRow(final ViewHolder paramViewHolder)
  {
    paramViewHolder.mControlsVh = ((ControlBarPresenter.ViewHolder)this.mPlaybackControlsPresenter.onCreateViewHolder(paramViewHolder.mControlsDock));
    SeekBar localSeekBar = paramViewHolder.mProgressBar;
    if (this.mProgressColorSet) {}
    for (int i = this.mProgressColor;; i = getDefaultProgressColor(paramViewHolder.mControlsDock.getContext()))
    {
      localSeekBar.setProgressColor(i);
      paramViewHolder.mControlsDock.addView(paramViewHolder.mControlsVh.view);
      paramViewHolder.mSecondaryControlsVh = ((ControlBarPresenter.ViewHolder)this.mSecondaryControlsPresenter.onCreateViewHolder(paramViewHolder.mSecondaryControlsDock));
      paramViewHolder.mSecondaryControlsDock.addView(paramViewHolder.mSecondaryControlsVh.view);
      ((PlaybackTransportRowView)paramViewHolder.view).setOnUnhandledKeyListener(new PlaybackTransportRowView.OnUnhandledKeyListener()
      {
        public boolean onUnhandledKey(KeyEvent paramAnonymousKeyEvent)
        {
          return (paramViewHolder.getOnKeyListener() != null) && (paramViewHolder.getOnKeyListener().onKey(paramViewHolder.view, paramAnonymousKeyEvent.getKeyCode(), paramAnonymousKeyEvent));
        }
      });
      return;
    }
  }
  
  protected RowPresenter.ViewHolder createRowViewHolder(ViewGroup paramViewGroup)
  {
    paramViewGroup = new ViewHolder(LayoutInflater.from(paramViewGroup.getContext()).inflate(R.layout.lb_playback_transport_controls_row, paramViewGroup, false), this.mDescriptionPresenter);
    initRow(paramViewGroup);
    return paramViewGroup;
  }
  
  public float getDefaultSeekIncrement()
  {
    return this.mDefaultSeekIncrement;
  }
  
  public OnActionClickedListener getOnActionClickedListener()
  {
    return this.mOnActionClickedListener;
  }
  
  @ColorInt
  public int getProgressColor()
  {
    return this.mProgressColor;
  }
  
  protected void onBindRowViewHolder(RowPresenter.ViewHolder paramViewHolder, Object paramObject)
  {
    super.onBindRowViewHolder(paramViewHolder, paramObject);
    paramViewHolder = (ViewHolder)paramViewHolder;
    paramObject = (PlaybackControlsRow)paramViewHolder.getRow();
    if (((PlaybackControlsRow)paramObject).getItem() == null)
    {
      paramViewHolder.mDescriptionDock.setVisibility(8);
      if (((PlaybackControlsRow)paramObject).getImageDrawable() != null) {
        break label220;
      }
      paramViewHolder.mImageView.setVisibility(8);
    }
    for (;;)
    {
      paramViewHolder.mImageView.setImageDrawable(((PlaybackControlsRow)paramObject).getImageDrawable());
      paramViewHolder.mControlsBoundData.adapter = ((PlaybackControlsRow)paramObject).getPrimaryActionsAdapter();
      paramViewHolder.mControlsBoundData.presenter = paramViewHolder.getPresenter(true);
      paramViewHolder.mControlsBoundData.mRowViewHolder = paramViewHolder;
      this.mPlaybackControlsPresenter.onBindViewHolder(paramViewHolder.mControlsVh, paramViewHolder.mControlsBoundData);
      paramViewHolder.mSecondaryBoundData.adapter = ((PlaybackControlsRow)paramObject).getSecondaryActionsAdapter();
      paramViewHolder.mSecondaryBoundData.presenter = paramViewHolder.getPresenter(false);
      paramViewHolder.mSecondaryBoundData.mRowViewHolder = paramViewHolder;
      this.mSecondaryControlsPresenter.onBindViewHolder(paramViewHolder.mSecondaryControlsVh, paramViewHolder.mSecondaryBoundData);
      paramViewHolder.setTotalTime(((PlaybackControlsRow)paramObject).getDuration());
      paramViewHolder.setCurrentPosition(((PlaybackControlsRow)paramObject).getCurrentPosition());
      paramViewHolder.setBufferedPosition(((PlaybackControlsRow)paramObject).getBufferedPosition());
      ((PlaybackControlsRow)paramObject).setOnPlaybackProgressChangedListener(paramViewHolder.mListener);
      return;
      paramViewHolder.mDescriptionDock.setVisibility(0);
      if (paramViewHolder.mDescriptionViewHolder == null) {
        break;
      }
      this.mDescriptionPresenter.onBindViewHolder(paramViewHolder.mDescriptionViewHolder, ((PlaybackControlsRow)paramObject).getItem());
      break;
      label220:
      paramViewHolder.mImageView.setVisibility(0);
    }
  }
  
  protected void onProgressBarClicked(ViewHolder paramViewHolder)
  {
    if (paramViewHolder != null)
    {
      if (paramViewHolder.mPlayPauseAction == null) {
        paramViewHolder.mPlayPauseAction = new PlaybackControlsRow.PlayPauseAction(paramViewHolder.view.getContext());
      }
      if (paramViewHolder.getOnItemViewClickedListener() != null) {
        paramViewHolder.getOnItemViewClickedListener().onItemClicked(paramViewHolder, paramViewHolder.mPlayPauseAction, paramViewHolder, paramViewHolder.getRow());
      }
      if (this.mOnActionClickedListener != null) {
        this.mOnActionClickedListener.onActionClicked(paramViewHolder.mPlayPauseAction);
      }
    }
  }
  
  public void onReappear(RowPresenter.ViewHolder paramViewHolder)
  {
    paramViewHolder = (ViewHolder)paramViewHolder;
    if (paramViewHolder.view.hasFocus()) {
      paramViewHolder.mProgressBar.requestFocus();
    }
  }
  
  protected void onRowViewAttachedToWindow(RowPresenter.ViewHolder paramViewHolder)
  {
    super.onRowViewAttachedToWindow(paramViewHolder);
    if (this.mDescriptionPresenter != null) {
      this.mDescriptionPresenter.onViewAttachedToWindow(((ViewHolder)paramViewHolder).mDescriptionViewHolder);
    }
  }
  
  protected void onRowViewDetachedFromWindow(RowPresenter.ViewHolder paramViewHolder)
  {
    super.onRowViewDetachedFromWindow(paramViewHolder);
    if (this.mDescriptionPresenter != null) {
      this.mDescriptionPresenter.onViewDetachedFromWindow(((ViewHolder)paramViewHolder).mDescriptionViewHolder);
    }
  }
  
  protected void onRowViewSelected(RowPresenter.ViewHolder paramViewHolder, boolean paramBoolean)
  {
    super.onRowViewSelected(paramViewHolder, paramBoolean);
    if (paramBoolean) {
      ((ViewHolder)paramViewHolder).dispatchItemSelection();
    }
  }
  
  protected void onUnbindRowViewHolder(RowPresenter.ViewHolder paramViewHolder)
  {
    ViewHolder localViewHolder = (ViewHolder)paramViewHolder;
    PlaybackControlsRow localPlaybackControlsRow = (PlaybackControlsRow)localViewHolder.getRow();
    if (localViewHolder.mDescriptionViewHolder != null) {
      this.mDescriptionPresenter.onUnbindViewHolder(localViewHolder.mDescriptionViewHolder);
    }
    this.mPlaybackControlsPresenter.onUnbindViewHolder(localViewHolder.mControlsVh);
    this.mSecondaryControlsPresenter.onUnbindViewHolder(localViewHolder.mSecondaryControlsVh);
    localPlaybackControlsRow.setOnPlaybackProgressChangedListener(null);
    super.onUnbindRowViewHolder(paramViewHolder);
  }
  
  public void setDefaultSeekIncrement(float paramFloat)
  {
    this.mDefaultSeekIncrement = paramFloat;
  }
  
  public void setDescriptionPresenter(Presenter paramPresenter)
  {
    this.mDescriptionPresenter = paramPresenter;
  }
  
  public void setOnActionClickedListener(OnActionClickedListener paramOnActionClickedListener)
  {
    this.mOnActionClickedListener = paramOnActionClickedListener;
  }
  
  public void setProgressColor(@ColorInt int paramInt)
  {
    this.mProgressColor = paramInt;
    this.mProgressColorSet = true;
  }
  
  static class BoundData
    extends PlaybackControlsPresenter.BoundData
  {
    PlaybackTransportRowPresenter.ViewHolder mRowViewHolder;
  }
  
  public class ViewHolder
    extends PlaybackRowPresenter.ViewHolder
    implements PlaybackSeekUi
  {
    PlaybackTransportRowPresenter.BoundData mControlsBoundData = new PlaybackTransportRowPresenter.BoundData();
    final ViewGroup mControlsDock;
    ControlBarPresenter.ViewHolder mControlsVh;
    final TextView mCurrentTime;
    long mCurrentTimeInMs = Long.MIN_VALUE;
    final ViewGroup mDescriptionDock;
    final Presenter.ViewHolder mDescriptionViewHolder;
    final ImageView mImageView;
    boolean mInSeek;
    final PlaybackControlsRow.OnPlaybackProgressCallback mListener = new PlaybackControlsRow.OnPlaybackProgressCallback()
    {
      public void onBufferedPositionChanged(PlaybackControlsRow paramAnonymousPlaybackControlsRow, long paramAnonymousLong)
      {
        PlaybackTransportRowPresenter.ViewHolder.this.setBufferedPosition(paramAnonymousLong);
      }
      
      public void onCurrentPositionChanged(PlaybackControlsRow paramAnonymousPlaybackControlsRow, long paramAnonymousLong)
      {
        PlaybackTransportRowPresenter.ViewHolder.this.setCurrentPosition(paramAnonymousLong);
      }
      
      public void onDurationChanged(PlaybackControlsRow paramAnonymousPlaybackControlsRow, long paramAnonymousLong)
      {
        PlaybackTransportRowPresenter.ViewHolder.this.setTotalTime(paramAnonymousLong);
      }
    };
    PlaybackControlsRow.PlayPauseAction mPlayPauseAction;
    long[] mPositions;
    int mPositionsLength;
    final SeekBar mProgressBar;
    PlaybackTransportRowPresenter.BoundData mSecondaryBoundData = new PlaybackTransportRowPresenter.BoundData();
    final ViewGroup mSecondaryControlsDock;
    ControlBarPresenter.ViewHolder mSecondaryControlsVh;
    long mSecondaryProgressInMs;
    PlaybackSeekUi.Client mSeekClient;
    PlaybackSeekDataProvider mSeekDataProvider;
    Object mSelectedItem;
    Presenter.ViewHolder mSelectedViewHolder;
    final StringBuilder mTempBuilder = new StringBuilder();
    int mThumbHeroIndex = -1;
    PlaybackSeekDataProvider.ResultCallback mThumbResult = new PlaybackSeekDataProvider.ResultCallback()
    {
      public void onThumbnailLoaded(Bitmap paramAnonymousBitmap, int paramAnonymousInt)
      {
        paramAnonymousInt -= PlaybackTransportRowPresenter.ViewHolder.this.mThumbHeroIndex - PlaybackTransportRowPresenter.ViewHolder.this.mThumbsBar.getChildCount() / 2;
        if ((paramAnonymousInt < 0) || (paramAnonymousInt >= PlaybackTransportRowPresenter.ViewHolder.this.mThumbsBar.getChildCount())) {
          return;
        }
        PlaybackTransportRowPresenter.ViewHolder.this.mThumbsBar.setThumbBitmap(paramAnonymousInt, paramAnonymousBitmap);
      }
    };
    final ThumbsBar mThumbsBar;
    final TextView mTotalTime;
    long mTotalTimeInMs = Long.MIN_VALUE;
    
    public ViewHolder(View paramView, Presenter paramPresenter)
    {
      super();
      this.mImageView = ((ImageView)paramView.findViewById(R.id.image));
      this.mDescriptionDock = ((ViewGroup)paramView.findViewById(R.id.description_dock));
      this.mCurrentTime = ((TextView)paramView.findViewById(R.id.current_time));
      this.mTotalTime = ((TextView)paramView.findViewById(R.id.total_time));
      this.mProgressBar = ((SeekBar)paramView.findViewById(R.id.playback_progress));
      this.mProgressBar.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          PlaybackTransportRowPresenter.this.onProgressBarClicked(PlaybackTransportRowPresenter.ViewHolder.this);
        }
      });
      this.mProgressBar.setOnKeyListener(new View.OnKeyListener()
      {
        public boolean onKey(View paramAnonymousView, int paramAnonymousInt, KeyEvent paramAnonymousKeyEvent)
        {
          boolean bool2 = false;
          boolean bool1 = true;
          switch (paramAnonymousInt)
          {
          default: 
            bool1 = false;
          }
          do
          {
            do
            {
              do
              {
                do
                {
                  return bool1;
                  return PlaybackTransportRowPresenter.ViewHolder.this.mInSeek;
                } while (paramAnonymousKeyEvent.getAction() != 0);
                PlaybackTransportRowPresenter.ViewHolder.this.onBackward();
                return true;
              } while (paramAnonymousKeyEvent.getAction() != 0);
              PlaybackTransportRowPresenter.ViewHolder.this.onForward();
              return true;
              if (!PlaybackTransportRowPresenter.ViewHolder.this.mInSeek) {
                return false;
              }
            } while (paramAnonymousKeyEvent.getAction() != 1);
            PlaybackTransportRowPresenter.ViewHolder.this.stopSeek(false);
            return true;
            if (!PlaybackTransportRowPresenter.ViewHolder.this.mInSeek) {
              return false;
            }
          } while (paramAnonymousKeyEvent.getAction() != 1);
          paramAnonymousView = PlaybackTransportRowPresenter.ViewHolder.this;
          if (Build.VERSION.SDK_INT >= 21)
          {
            bool1 = bool2;
            if (PlaybackTransportRowPresenter.ViewHolder.this.mProgressBar.isAccessibilityFocused()) {}
          }
          for (bool1 = true;; bool1 = true)
          {
            paramAnonymousView.stopSeek(bool1);
            return true;
          }
        }
      });
      this.mProgressBar.setAccessibilitySeekListener(new SeekBar.AccessibilitySeekListener()
      {
        public boolean onAccessibilitySeekBackward()
        {
          return PlaybackTransportRowPresenter.ViewHolder.this.onBackward();
        }
        
        public boolean onAccessibilitySeekForward()
        {
          return PlaybackTransportRowPresenter.ViewHolder.this.onForward();
        }
      });
      this.mProgressBar.setMax(Integer.MAX_VALUE);
      this.mControlsDock = ((ViewGroup)paramView.findViewById(R.id.controls_dock));
      this.mSecondaryControlsDock = ((ViewGroup)paramView.findViewById(R.id.secondary_controls_dock));
      if (paramPresenter == null) {}
      for (this$1 = null;; this$1 = paramPresenter.onCreateViewHolder(this.mDescriptionDock))
      {
        this.mDescriptionViewHolder = PlaybackTransportRowPresenter.this;
        if (this.mDescriptionViewHolder != null) {
          this.mDescriptionDock.addView(this.mDescriptionViewHolder.view);
        }
        this.mThumbsBar = ((ThumbsBar)paramView.findViewById(R.id.thumbs_row));
        return;
      }
    }
    
    void dispatchItemSelection()
    {
      if (!isSelected()) {}
      do
      {
        do
        {
          return;
          if (this.mSelectedViewHolder != null) {
            break;
          }
        } while (getOnItemViewSelectedListener() == null);
        getOnItemViewSelectedListener().onItemSelected(null, null, this, getRow());
        return;
      } while (getOnItemViewSelectedListener() == null);
      getOnItemViewSelectedListener().onItemSelected(this.mSelectedViewHolder, this.mSelectedItem, this, getRow());
    }
    
    public final TextView getCurrentPositionView()
    {
      return this.mCurrentTime;
    }
    
    public final Presenter.ViewHolder getDescriptionViewHolder()
    {
      return this.mDescriptionViewHolder;
    }
    
    public final TextView getDurationView()
    {
      return this.mTotalTime;
    }
    
    Presenter getPresenter(boolean paramBoolean)
    {
      if (paramBoolean) {}
      for (ObjectAdapter localObjectAdapter = ((PlaybackControlsRow)getRow()).getPrimaryActionsAdapter(); localObjectAdapter == null; localObjectAdapter = ((PlaybackControlsRow)getRow()).getSecondaryActionsAdapter()) {
        return null;
      }
      if ((localObjectAdapter.getPresenterSelector() instanceof ControlButtonPresenterSelector)) {
        return ((ControlButtonPresenterSelector)localObjectAdapter.getPresenterSelector()).getSecondaryPresenter();
      }
      if (localObjectAdapter.size() > 0) {}
      for (Object localObject = localObjectAdapter.get(0);; localObject = null) {
        return localObjectAdapter.getPresenter(localObject);
      }
    }
    
    boolean onBackward()
    {
      if (!startSeek()) {
        return false;
      }
      updateProgressInSeek(false);
      return true;
    }
    
    boolean onForward()
    {
      if (!startSeek()) {
        return false;
      }
      updateProgressInSeek(true);
      return true;
    }
    
    protected void onSetCurrentPositionLabel(long paramLong)
    {
      if (this.mCurrentTime != null)
      {
        PlaybackTransportRowPresenter.formatTime(paramLong, this.mTempBuilder);
        this.mCurrentTime.setText(this.mTempBuilder.toString());
      }
    }
    
    protected void onSetDurationLabel(long paramLong)
    {
      if (this.mTotalTime != null)
      {
        PlaybackTransportRowPresenter.formatTime(paramLong, this.mTempBuilder);
        this.mTotalTime.setText(this.mTempBuilder.toString());
      }
    }
    
    void setBufferedPosition(long paramLong)
    {
      this.mSecondaryProgressInMs = paramLong;
      double d = paramLong / this.mTotalTimeInMs;
      this.mProgressBar.setSecondaryProgress((int)(d * 2.147483647E9D));
    }
    
    void setCurrentPosition(long paramLong)
    {
      if (paramLong != this.mCurrentTimeInMs)
      {
        this.mCurrentTimeInMs = paramLong;
        onSetCurrentPositionLabel(paramLong);
      }
      if (!this.mInSeek)
      {
        int i = 0;
        if (this.mTotalTimeInMs > 0L) {
          i = (int)(2.147483647E9D * (this.mCurrentTimeInMs / this.mTotalTimeInMs));
        }
        this.mProgressBar.setProgress(i);
      }
    }
    
    public void setPlaybackSeekUiClient(PlaybackSeekUi.Client paramClient)
    {
      this.mSeekClient = paramClient;
    }
    
    void setTotalTime(long paramLong)
    {
      if (this.mTotalTimeInMs != paramLong)
      {
        this.mTotalTimeInMs = paramLong;
        onSetDurationLabel(paramLong);
      }
    }
    
    boolean startSeek()
    {
      if (this.mInSeek) {
        return true;
      }
      if ((this.mSeekClient == null) || (!this.mSeekClient.isSeekEnabled()) || (this.mTotalTimeInMs <= 0L)) {
        return false;
      }
      this.mInSeek = true;
      this.mSeekClient.onSeekStarted();
      this.mSeekDataProvider = this.mSeekClient.getPlaybackSeekDataProvider();
      long[] arrayOfLong;
      int i;
      if (this.mSeekDataProvider != null)
      {
        arrayOfLong = this.mSeekDataProvider.getSeekPositions();
        this.mPositions = arrayOfLong;
        if (this.mPositions == null) {
          break label168;
        }
        i = Arrays.binarySearch(this.mPositions, this.mTotalTimeInMs);
        if (i < 0) {
          break label158;
        }
        this.mPositionsLength = (i + 1);
      }
      for (;;)
      {
        this.mControlsVh.view.setVisibility(4);
        this.mSecondaryControlsVh.view.setVisibility(4);
        this.mDescriptionViewHolder.view.setVisibility(4);
        this.mThumbsBar.setVisibility(0);
        return true;
        arrayOfLong = null;
        break;
        label158:
        this.mPositionsLength = (-1 - i);
        continue;
        label168:
        this.mPositionsLength = 0;
      }
    }
    
    void stopSeek(boolean paramBoolean)
    {
      if (!this.mInSeek) {
        return;
      }
      this.mInSeek = false;
      this.mSeekClient.onSeekFinished(paramBoolean);
      if (this.mSeekDataProvider != null) {
        this.mSeekDataProvider.reset();
      }
      this.mThumbHeroIndex = -1;
      this.mThumbsBar.clearThumbBitmaps();
      this.mSeekDataProvider = null;
      this.mPositions = null;
      this.mPositionsLength = 0;
      this.mControlsVh.view.setVisibility(0);
      this.mSecondaryControlsVh.view.setVisibility(0);
      this.mDescriptionViewHolder.view.setVisibility(0);
      this.mThumbsBar.setVisibility(4);
    }
    
    void updateProgressInSeek(boolean paramBoolean)
    {
      long l2 = this.mCurrentTimeInMs;
      int i;
      long l1;
      if (this.mPositionsLength > 0)
      {
        i = Arrays.binarySearch(this.mPositions, 0, this.mPositionsLength, l2);
        if (paramBoolean) {
          if (i >= 0) {
            if (i < this.mPositionsLength - 1)
            {
              l1 = this.mPositions[(i + 1)];
              i += 1;
              updateThumbsInSeek(i, paramBoolean);
            }
          }
        }
      }
      for (;;)
      {
        double d = l1 / this.mTotalTimeInMs;
        this.mProgressBar.setProgress((int)(2.147483647E9D * d));
        this.mSeekClient.onSeekPositionChanged(l1);
        return;
        l1 = this.mTotalTimeInMs;
        break;
        i = -1 - i;
        if (i <= this.mPositionsLength - 1)
        {
          l1 = this.mPositions[i];
          break;
        }
        l1 = this.mTotalTimeInMs;
        if (i > 0) {
          i -= 1;
        }
        for (;;)
        {
          break;
          i = 0;
        }
        if (i >= 0)
        {
          if (i > 0)
          {
            l1 = this.mPositions[(i - 1)];
            i -= 1;
            break;
          }
          l1 = 0L;
          i = 0;
          break;
        }
        i = -1 - i;
        if (i > 0)
        {
          l1 = this.mPositions[(i - 1)];
          i -= 1;
          break;
        }
        l1 = 0L;
        i = 0;
        break;
        l1 = ((float)this.mTotalTimeInMs * PlaybackTransportRowPresenter.this.getDefaultSeekIncrement());
        if (paramBoolean) {}
        for (;;)
        {
          l2 += l1;
          if (l2 <= this.mTotalTimeInMs) {
            break label303;
          }
          l1 = this.mTotalTimeInMs;
          break;
          l1 = -l1;
        }
        label303:
        l1 = l2;
        if (l2 < 0L) {
          l1 = 0L;
        }
      }
    }
    
    void updateThumbsInSeek(int paramInt, boolean paramBoolean)
    {
      if (this.mThumbHeroIndex == paramInt) {}
      for (;;)
      {
        return;
        int i3 = this.mThumbsBar.getChildCount();
        if ((i3 < 0) || ((i3 & 0x1) == 0)) {
          throw new RuntimeException();
        }
        int i4 = i3 / 2;
        int k = Math.max(paramInt - i3 / 2, 0);
        int m = Math.min(i3 / 2 + paramInt, this.mPositionsLength - 1);
        int i;
        int j;
        boolean bool;
        if (this.mThumbHeroIndex < 0)
        {
          i = k;
          j = m;
          bool = paramBoolean;
          this.mThumbHeroIndex = paramInt;
          if (bool) {
            while (i <= j)
            {
              this.mSeekDataProvider.getThumbnail(i, this.mThumbResult);
              i += 1;
            }
          }
        }
        else
        {
          if (paramInt > this.mThumbHeroIndex) {}
          for (paramBoolean = true;; paramBoolean = false)
          {
            i = Math.max(this.mThumbHeroIndex - i3 / 2, 0);
            j = Math.min(this.mThumbHeroIndex + i3 / 2, this.mPositionsLength - 1);
            if (!paramBoolean) {
              break label256;
            }
            i2 = Math.max(j + 1, k);
            n = m;
            i1 = k;
            for (;;)
            {
              j = n;
              i = i2;
              bool = paramBoolean;
              if (i1 > i2 - 1) {
                break;
              }
              this.mThumbsBar.setThumbBitmap(i1 - paramInt + i4, this.mThumbsBar.getThumbBitmap(i1 - this.mThumbHeroIndex + i4));
              i1 += 1;
            }
          }
          label256:
          int i2 = Math.min(i - 1, m);
          int n = k;
          int i1 = m;
          for (;;)
          {
            j = i2;
            i = n;
            bool = paramBoolean;
            if (i1 < i2 + 1) {
              break;
            }
            this.mThumbsBar.setThumbBitmap(i1 - paramInt + i4, this.mThumbsBar.getThumbBitmap(i1 - this.mThumbHeroIndex + i4));
            i1 -= 1;
          }
        }
        while (j >= i)
        {
          this.mSeekDataProvider.getThumbnail(j, this.mThumbResult);
          j -= 1;
        }
        paramInt = 0;
        while (paramInt < i4 - this.mThumbHeroIndex + k)
        {
          this.mThumbsBar.setThumbBitmap(paramInt, null);
          paramInt += 1;
        }
        paramInt = i4 + m - this.mThumbHeroIndex + 1;
        while (paramInt < i3)
        {
          this.mThumbsBar.setThumbBitmap(paramInt, null);
          paramInt += 1;
        }
      }
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/PlaybackTransportRowPresenter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */