package android.support.v17.leanback.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.support.annotation.ColorInt;
import android.support.v17.leanback.R.attr;
import android.support.v17.leanback.R.color;
import android.support.v17.leanback.R.dimen;
import android.support.v17.leanback.R.id;
import android.support.v17.leanback.R.layout;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;

public class PlaybackControlsRowPresenter
  extends PlaybackRowPresenter
{
  static float sShadowZ;
  private int mBackgroundColor = 0;
  private boolean mBackgroundColorSet;
  private Presenter mDescriptionPresenter;
  OnActionClickedListener mOnActionClickedListener;
  private final ControlBarPresenter.OnControlClickedListener mOnControlClickedListener = new ControlBarPresenter.OnControlClickedListener()
  {
    public void onControlClicked(Presenter.ViewHolder paramAnonymousViewHolder, Object paramAnonymousObject, ControlBarPresenter.BoundData paramAnonymousBoundData)
    {
      paramAnonymousBoundData = ((PlaybackControlsRowPresenter.BoundData)paramAnonymousBoundData).mRowViewHolder;
      if (paramAnonymousBoundData.getOnItemViewClickedListener() != null) {
        paramAnonymousBoundData.getOnItemViewClickedListener().onItemClicked(paramAnonymousViewHolder, paramAnonymousObject, paramAnonymousBoundData, paramAnonymousBoundData.getRow());
      }
      if ((PlaybackControlsRowPresenter.this.mOnActionClickedListener != null) && ((paramAnonymousObject instanceof Action))) {
        PlaybackControlsRowPresenter.this.mOnActionClickedListener.onActionClicked((Action)paramAnonymousObject);
      }
    }
  };
  private final ControlBarPresenter.OnControlSelectedListener mOnControlSelectedListener = new ControlBarPresenter.OnControlSelectedListener()
  {
    public void onControlSelected(Presenter.ViewHolder paramAnonymousViewHolder, Object paramAnonymousObject, ControlBarPresenter.BoundData paramAnonymousBoundData)
    {
      paramAnonymousBoundData = ((PlaybackControlsRowPresenter.BoundData)paramAnonymousBoundData).mRowViewHolder;
      if ((paramAnonymousBoundData.mSelectedViewHolder != paramAnonymousViewHolder) || (paramAnonymousBoundData.mSelectedItem != paramAnonymousObject))
      {
        paramAnonymousBoundData.mSelectedViewHolder = paramAnonymousViewHolder;
        paramAnonymousBoundData.mSelectedItem = paramAnonymousObject;
        paramAnonymousBoundData.dispatchItemSelection();
      }
    }
  };
  PlaybackControlsPresenter mPlaybackControlsPresenter;
  private int mProgressColor = 0;
  private boolean mProgressColorSet;
  private boolean mSecondaryActionsHidden;
  private ControlBarPresenter mSecondaryControlsPresenter;
  
  public PlaybackControlsRowPresenter()
  {
    this(null);
  }
  
  public PlaybackControlsRowPresenter(Presenter paramPresenter)
  {
    setHeaderPresenter(null);
    setSelectEffectEnabled(false);
    this.mDescriptionPresenter = paramPresenter;
    this.mPlaybackControlsPresenter = new PlaybackControlsPresenter(R.layout.lb_playback_controls);
    this.mSecondaryControlsPresenter = new ControlBarPresenter(R.layout.lb_control_bar);
    this.mPlaybackControlsPresenter.setOnControlSelectedListener(this.mOnControlSelectedListener);
    this.mSecondaryControlsPresenter.setOnControlSelectedListener(this.mOnControlSelectedListener);
    this.mPlaybackControlsPresenter.setOnControlClickedListener(this.mOnControlClickedListener);
    this.mSecondaryControlsPresenter.setOnControlClickedListener(this.mOnControlClickedListener);
  }
  
  private int getDefaultBackgroundColor(Context paramContext)
  {
    TypedValue localTypedValue = new TypedValue();
    if (paramContext.getTheme().resolveAttribute(R.attr.defaultBrandColor, localTypedValue, true)) {
      return paramContext.getResources().getColor(localTypedValue.resourceId);
    }
    return paramContext.getResources().getColor(R.color.lb_default_brand_color);
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
    Object localObject = (ViewGroup.MarginLayoutParams)paramViewHolder.mControlsDock.getLayoutParams();
    paramViewHolder.mControlsDockMarginStart = ((ViewGroup.MarginLayoutParams)localObject).getMarginStart();
    paramViewHolder.mControlsDockMarginEnd = ((ViewGroup.MarginLayoutParams)localObject).getMarginEnd();
    paramViewHolder.mControlsVh = ((PlaybackControlsPresenter.ViewHolder)this.mPlaybackControlsPresenter.onCreateViewHolder(paramViewHolder.mControlsDock));
    localObject = this.mPlaybackControlsPresenter;
    PlaybackControlsPresenter.ViewHolder localViewHolder = paramViewHolder.mControlsVh;
    if (this.mProgressColorSet)
    {
      i = this.mProgressColor;
      ((PlaybackControlsPresenter)localObject).setProgressColor(localViewHolder, i);
      localObject = this.mPlaybackControlsPresenter;
      localViewHolder = paramViewHolder.mControlsVh;
      if (!this.mBackgroundColorSet) {
        break label190;
      }
    }
    label190:
    for (int i = this.mBackgroundColor;; i = getDefaultBackgroundColor(paramViewHolder.view.getContext()))
    {
      ((PlaybackControlsPresenter)localObject).setBackgroundColor(localViewHolder, i);
      paramViewHolder.mControlsDock.addView(paramViewHolder.mControlsVh.view);
      paramViewHolder.mSecondaryControlsVh = this.mSecondaryControlsPresenter.onCreateViewHolder(paramViewHolder.mSecondaryControlsDock);
      if (!this.mSecondaryActionsHidden) {
        paramViewHolder.mSecondaryControlsDock.addView(paramViewHolder.mSecondaryControlsVh.view);
      }
      ((PlaybackControlsRowView)paramViewHolder.view).setOnUnhandledKeyListener(new PlaybackControlsRowView.OnUnhandledKeyListener()
      {
        public boolean onUnhandledKey(KeyEvent paramAnonymousKeyEvent)
        {
          return (paramViewHolder.getOnKeyListener() != null) && (paramViewHolder.getOnKeyListener().onKey(paramViewHolder.view, paramAnonymousKeyEvent.getKeyCode(), paramAnonymousKeyEvent));
        }
      });
      return;
      i = getDefaultProgressColor(paramViewHolder.mControlsDock.getContext());
      break;
    }
  }
  
  private void updateCardLayout(ViewHolder paramViewHolder, int paramInt)
  {
    Object localObject = paramViewHolder.mCardRightPanel.getLayoutParams();
    ((ViewGroup.LayoutParams)localObject).height = paramInt;
    paramViewHolder.mCardRightPanel.setLayoutParams((ViewGroup.LayoutParams)localObject);
    localObject = (ViewGroup.MarginLayoutParams)paramViewHolder.mControlsDock.getLayoutParams();
    LinearLayout.LayoutParams localLayoutParams = (LinearLayout.LayoutParams)paramViewHolder.mDescriptionDock.getLayoutParams();
    if (paramInt == -2)
    {
      localLayoutParams.height = -2;
      ((ViewGroup.MarginLayoutParams)localObject).setMarginStart(0);
      ((ViewGroup.MarginLayoutParams)localObject).setMarginEnd(0);
      paramViewHolder.mCard.setBackground(null);
      paramViewHolder.setOutline(paramViewHolder.mControlsDock);
      this.mPlaybackControlsPresenter.enableTimeMargins(paramViewHolder.mControlsVh, true);
      paramViewHolder.mDescriptionDock.setLayoutParams(localLayoutParams);
      paramViewHolder.mControlsDock.setLayoutParams((ViewGroup.LayoutParams)localObject);
      return;
    }
    localLayoutParams.height = 0;
    localLayoutParams.weight = 1.0F;
    ((ViewGroup.MarginLayoutParams)localObject).setMarginStart(paramViewHolder.mControlsDockMarginStart);
    ((ViewGroup.MarginLayoutParams)localObject).setMarginEnd(paramViewHolder.mControlsDockMarginEnd);
    ViewGroup localViewGroup = paramViewHolder.mCard;
    if (this.mBackgroundColorSet) {}
    for (paramInt = this.mBackgroundColor;; paramInt = getDefaultBackgroundColor(paramViewHolder.mCard.getContext()))
    {
      localViewGroup.setBackgroundColor(paramInt);
      paramViewHolder.setOutline(paramViewHolder.mCard);
      this.mPlaybackControlsPresenter.enableTimeMargins(paramViewHolder.mControlsVh, false);
      break;
    }
  }
  
  public boolean areSecondaryActionsHidden()
  {
    return this.mSecondaryActionsHidden;
  }
  
  protected RowPresenter.ViewHolder createRowViewHolder(ViewGroup paramViewGroup)
  {
    paramViewGroup = new ViewHolder(LayoutInflater.from(paramViewGroup.getContext()).inflate(R.layout.lb_playback_controls_row, paramViewGroup, false), this.mDescriptionPresenter);
    initRow(paramViewGroup);
    return paramViewGroup;
  }
  
  @ColorInt
  public int getBackgroundColor()
  {
    return this.mBackgroundColor;
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
    this.mPlaybackControlsPresenter.enableSecondaryActions(this.mSecondaryActionsHidden);
    if (((PlaybackControlsRow)paramObject).getItem() == null)
    {
      paramViewHolder.mDescriptionDock.setVisibility(8);
      paramViewHolder.mSpacer.setVisibility(8);
      if ((((PlaybackControlsRow)paramObject).getImageDrawable() != null) && (((PlaybackControlsRow)paramObject).getItem() != null)) {
        break label282;
      }
      paramViewHolder.mImageView.setImageDrawable(null);
      updateCardLayout(paramViewHolder, -2);
    }
    for (;;)
    {
      paramViewHolder.mControlsBoundData.adapter = ((PlaybackControlsRow)paramObject).getPrimaryActionsAdapter();
      paramViewHolder.mControlsBoundData.secondaryActionsAdapter = ((PlaybackControlsRow)paramObject).getSecondaryActionsAdapter();
      paramViewHolder.mControlsBoundData.presenter = paramViewHolder.getPresenter(true);
      paramViewHolder.mControlsBoundData.mRowViewHolder = paramViewHolder;
      this.mPlaybackControlsPresenter.onBindViewHolder(paramViewHolder.mControlsVh, paramViewHolder.mControlsBoundData);
      paramViewHolder.mSecondaryBoundData.adapter = ((PlaybackControlsRow)paramObject).getSecondaryActionsAdapter();
      paramViewHolder.mSecondaryBoundData.presenter = paramViewHolder.getPresenter(false);
      paramViewHolder.mSecondaryBoundData.mRowViewHolder = paramViewHolder;
      this.mSecondaryControlsPresenter.onBindViewHolder(paramViewHolder.mSecondaryControlsVh, paramViewHolder.mSecondaryBoundData);
      this.mPlaybackControlsPresenter.setTotalTime(paramViewHolder.mControlsVh, ((PlaybackControlsRow)paramObject).getTotalTime());
      this.mPlaybackControlsPresenter.setCurrentTime(paramViewHolder.mControlsVh, ((PlaybackControlsRow)paramObject).getCurrentTime());
      this.mPlaybackControlsPresenter.setSecondaryProgress(paramViewHolder.mControlsVh, ((PlaybackControlsRow)paramObject).getBufferedProgress());
      ((PlaybackControlsRow)paramObject).setOnPlaybackProgressChangedListener(paramViewHolder.mListener);
      return;
      paramViewHolder.mDescriptionDock.setVisibility(0);
      if (paramViewHolder.mDescriptionViewHolder != null) {
        this.mDescriptionPresenter.onBindViewHolder(paramViewHolder.mDescriptionViewHolder, ((PlaybackControlsRow)paramObject).getItem());
      }
      paramViewHolder.mSpacer.setVisibility(0);
      break;
      label282:
      paramViewHolder.mImageView.setImageDrawable(((PlaybackControlsRow)paramObject).getImageDrawable());
      updateCardLayout(paramViewHolder, paramViewHolder.mImageView.getLayoutParams().height);
    }
  }
  
  public void onReappear(RowPresenter.ViewHolder paramViewHolder)
  {
    showPrimaryActions((ViewHolder)paramViewHolder);
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
  
  public void setBackgroundColor(@ColorInt int paramInt)
  {
    this.mBackgroundColor = paramInt;
    this.mBackgroundColorSet = true;
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
  
  public void setSecondaryActionsHidden(boolean paramBoolean)
  {
    this.mSecondaryActionsHidden = paramBoolean;
  }
  
  public void showBottomSpace(ViewHolder paramViewHolder, boolean paramBoolean)
  {
    paramViewHolder = paramViewHolder.mBottomSpacer;
    if (paramBoolean) {}
    for (int i = 0;; i = 8)
    {
      paramViewHolder.setVisibility(i);
      return;
    }
  }
  
  public void showPrimaryActions(ViewHolder paramViewHolder)
  {
    this.mPlaybackControlsPresenter.showPrimaryActions(paramViewHolder.mControlsVh);
    if (paramViewHolder.view.hasFocus()) {
      this.mPlaybackControlsPresenter.resetFocus(paramViewHolder.mControlsVh);
    }
  }
  
  static class BoundData
    extends PlaybackControlsPresenter.BoundData
  {
    PlaybackControlsRowPresenter.ViewHolder mRowViewHolder;
  }
  
  public class ViewHolder
    extends PlaybackRowPresenter.ViewHolder
  {
    View mBgView;
    final View mBottomSpacer;
    final ViewGroup mCard;
    final ViewGroup mCardRightPanel;
    PlaybackControlsRowPresenter.BoundData mControlsBoundData = new PlaybackControlsRowPresenter.BoundData();
    final ViewGroup mControlsDock;
    int mControlsDockMarginEnd;
    int mControlsDockMarginStart;
    PlaybackControlsPresenter.ViewHolder mControlsVh;
    final ViewGroup mDescriptionDock;
    public final Presenter.ViewHolder mDescriptionViewHolder;
    final ImageView mImageView;
    final PlaybackControlsRow.OnPlaybackProgressCallback mListener = new PlaybackControlsRow.OnPlaybackProgressCallback()
    {
      public void onBufferedPositionChanged(PlaybackControlsRow paramAnonymousPlaybackControlsRow, long paramAnonymousLong)
      {
        PlaybackControlsRowPresenter.this.mPlaybackControlsPresenter.setSecondaryProgressLong(PlaybackControlsRowPresenter.ViewHolder.this.mControlsVh, paramAnonymousLong);
      }
      
      public void onCurrentPositionChanged(PlaybackControlsRow paramAnonymousPlaybackControlsRow, long paramAnonymousLong)
      {
        PlaybackControlsRowPresenter.this.mPlaybackControlsPresenter.setCurrentTimeLong(PlaybackControlsRowPresenter.ViewHolder.this.mControlsVh, paramAnonymousLong);
      }
      
      public void onDurationChanged(PlaybackControlsRow paramAnonymousPlaybackControlsRow, long paramAnonymousLong)
      {
        PlaybackControlsRowPresenter.this.mPlaybackControlsPresenter.setTotalTimeLong(PlaybackControlsRowPresenter.ViewHolder.this.mControlsVh, paramAnonymousLong);
      }
    };
    PlaybackControlsRowPresenter.BoundData mSecondaryBoundData = new PlaybackControlsRowPresenter.BoundData();
    final ViewGroup mSecondaryControlsDock;
    Presenter.ViewHolder mSecondaryControlsVh;
    Object mSelectedItem;
    Presenter.ViewHolder mSelectedViewHolder;
    final View mSpacer;
    
    ViewHolder(View paramView, Presenter paramPresenter)
    {
      super();
      this.mCard = ((ViewGroup)paramView.findViewById(R.id.controls_card));
      this.mCardRightPanel = ((ViewGroup)paramView.findViewById(R.id.controls_card_right_panel));
      this.mImageView = ((ImageView)paramView.findViewById(R.id.image));
      this.mDescriptionDock = ((ViewGroup)paramView.findViewById(R.id.description_dock));
      this.mControlsDock = ((ViewGroup)paramView.findViewById(R.id.controls_dock));
      this.mSecondaryControlsDock = ((ViewGroup)paramView.findViewById(R.id.secondary_controls_dock));
      this.mSpacer = paramView.findViewById(R.id.spacer);
      this.mBottomSpacer = paramView.findViewById(R.id.bottom_spacer);
      if (paramPresenter == null) {}
      for (this$1 = null;; this$1 = paramPresenter.onCreateViewHolder(this.mDescriptionDock))
      {
        this.mDescriptionViewHolder = PlaybackControlsRowPresenter.this;
        if (this.mDescriptionViewHolder != null) {
          this.mDescriptionDock.addView(this.mDescriptionViewHolder.view);
        }
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
    
    Presenter getPresenter(boolean paramBoolean)
    {
      if (paramBoolean) {}
      for (Object localObject1 = ((PlaybackControlsRow)getRow()).getPrimaryActionsAdapter(); localObject1 == null; localObject1 = ((PlaybackControlsRow)getRow()).getSecondaryActionsAdapter()) {
        return null;
      }
      if ((((ObjectAdapter)localObject1).getPresenterSelector() instanceof ControlButtonPresenterSelector))
      {
        localObject1 = (ControlButtonPresenterSelector)((ObjectAdapter)localObject1).getPresenterSelector();
        if (paramBoolean) {}
        for (localObject1 = ((ControlButtonPresenterSelector)localObject1).getPrimaryPresenter();; localObject1 = ((ControlButtonPresenterSelector)localObject1).getSecondaryPresenter()) {
          return (Presenter)localObject1;
        }
      }
      if (((ObjectAdapter)localObject1).size() > 0) {}
      for (Object localObject2 = ((ObjectAdapter)localObject1).get(0);; localObject2 = null) {
        return ((ObjectAdapter)localObject1).getPresenter(localObject2);
      }
    }
    
    void setOutline(View paramView)
    {
      if (this.mBgView != null)
      {
        RoundedRectHelper.getInstance().setClipToRoundedOutline(this.mBgView, false);
        ShadowHelper.getInstance().setZ(this.mBgView, 0.0F);
      }
      this.mBgView = paramView;
      RoundedRectHelper.getInstance().setClipToRoundedOutline(paramView, true);
      if (PlaybackControlsRowPresenter.sShadowZ == 0.0F) {
        PlaybackControlsRowPresenter.sShadowZ = paramView.getResources().getDimensionPixelSize(R.dimen.lb_playback_controls_z);
      }
      ShadowHelper.getInstance().setZ(paramView, PlaybackControlsRowPresenter.sShadowZ);
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/PlaybackControlsRowPresenter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */