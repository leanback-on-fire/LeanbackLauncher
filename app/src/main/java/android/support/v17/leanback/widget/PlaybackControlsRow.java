package android.support.v17.leanback.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v17.leanback.R.attr;
import android.support.v17.leanback.R.color;
import android.support.v17.leanback.R.drawable;
import android.support.v17.leanback.R.id;
import android.support.v17.leanback.R.string;
import android.support.v17.leanback.R.styleable;
import android.support.v17.leanback.util.MathUtil;
import android.util.TypedValue;

public class PlaybackControlsRow
  extends Row
{
  private long mBufferedProgressMs;
  private long mCurrentTimeMs;
  private Drawable mImageDrawable;
  private Object mItem;
  private OnPlaybackProgressCallback mListener;
  private ObjectAdapter mPrimaryActionsAdapter;
  private ObjectAdapter mSecondaryActionsAdapter;
  private long mTotalTimeMs;
  
  public PlaybackControlsRow() {}
  
  public PlaybackControlsRow(Object paramObject)
  {
    this.mItem = paramObject;
  }
  
  static Bitmap createBitmap(Bitmap paramBitmap, int paramInt)
  {
    Bitmap localBitmap = paramBitmap.copy(paramBitmap.getConfig(), true);
    Canvas localCanvas = new Canvas(localBitmap);
    Paint localPaint = new Paint();
    localPaint.setColorFilter(new PorterDuffColorFilter(paramInt, PorterDuff.Mode.SRC_ATOP));
    localCanvas.drawBitmap(paramBitmap, 0.0F, 0.0F, localPaint);
    return localBitmap;
  }
  
  static int getIconHighlightColor(Context paramContext)
  {
    TypedValue localTypedValue = new TypedValue();
    if (paramContext.getTheme().resolveAttribute(R.attr.playbackControlsIconHighlightColor, localTypedValue, true)) {
      return localTypedValue.data;
    }
    return paramContext.getResources().getColor(R.color.lb_playback_icon_highlight_no_theme);
  }
  
  static Drawable getStyledDrawable(Context paramContext, int paramInt)
  {
    Object localObject = new TypedValue();
    if (!paramContext.getTheme().resolveAttribute(R.attr.playbackControlsActionIcons, (TypedValue)localObject, false)) {
      return null;
    }
    paramContext = paramContext.getTheme().obtainStyledAttributes(((TypedValue)localObject).data, R.styleable.lbPlaybackControlsActionIcons);
    localObject = paramContext.getDrawable(paramInt);
    paramContext.recycle();
    return (Drawable)localObject;
  }
  
  public Action getActionForKeyCode(int paramInt)
  {
    Action localAction = getActionForKeyCode(getPrimaryActionsAdapter(), paramInt);
    if (localAction != null) {
      return localAction;
    }
    return getActionForKeyCode(getSecondaryActionsAdapter(), paramInt);
  }
  
  public Action getActionForKeyCode(ObjectAdapter paramObjectAdapter, int paramInt)
  {
    if ((paramObjectAdapter != this.mPrimaryActionsAdapter) && (paramObjectAdapter != this.mSecondaryActionsAdapter)) {
      throw new IllegalArgumentException("Invalid adapter");
    }
    int i = 0;
    while (i < paramObjectAdapter.size())
    {
      Action localAction = (Action)paramObjectAdapter.get(i);
      if (localAction.respondsToKeyCode(paramInt)) {
        return localAction;
      }
      i += 1;
    }
    return null;
  }
  
  public long getBufferedPosition()
  {
    return this.mBufferedProgressMs;
  }
  
  @Deprecated
  public int getBufferedProgress()
  {
    return MathUtil.safeLongToInt(getBufferedPosition());
  }
  
  @Deprecated
  public long getBufferedProgressLong()
  {
    return this.mBufferedProgressMs;
  }
  
  public long getCurrentPosition()
  {
    return this.mCurrentTimeMs;
  }
  
  @Deprecated
  public int getCurrentTime()
  {
    return MathUtil.safeLongToInt(getCurrentTimeLong());
  }
  
  @Deprecated
  public long getCurrentTimeLong()
  {
    return this.mCurrentTimeMs;
  }
  
  public long getDuration()
  {
    return this.mTotalTimeMs;
  }
  
  public final Drawable getImageDrawable()
  {
    return this.mImageDrawable;
  }
  
  public final Object getItem()
  {
    return this.mItem;
  }
  
  public final ObjectAdapter getPrimaryActionsAdapter()
  {
    return this.mPrimaryActionsAdapter;
  }
  
  public final ObjectAdapter getSecondaryActionsAdapter()
  {
    return this.mSecondaryActionsAdapter;
  }
  
  @Deprecated
  public int getTotalTime()
  {
    return MathUtil.safeLongToInt(getTotalTimeLong());
  }
  
  @Deprecated
  public long getTotalTimeLong()
  {
    return this.mTotalTimeMs;
  }
  
  public void setBufferedPosition(long paramLong)
  {
    if (this.mBufferedProgressMs != paramLong)
    {
      this.mBufferedProgressMs = paramLong;
      if (this.mListener != null) {
        this.mListener.onBufferedPositionChanged(this, this.mBufferedProgressMs);
      }
    }
  }
  
  @Deprecated
  public void setBufferedProgress(int paramInt)
  {
    setBufferedPosition(paramInt);
  }
  
  @Deprecated
  public void setBufferedProgressLong(long paramLong)
  {
    setBufferedPosition(paramLong);
  }
  
  public void setCurrentPosition(long paramLong)
  {
    if (this.mCurrentTimeMs != paramLong)
    {
      this.mCurrentTimeMs = paramLong;
      if (this.mListener != null) {
        this.mListener.onCurrentPositionChanged(this, this.mCurrentTimeMs);
      }
    }
  }
  
  @Deprecated
  public void setCurrentTime(int paramInt)
  {
    setCurrentTimeLong(paramInt);
  }
  
  @Deprecated
  public void setCurrentTimeLong(long paramLong)
  {
    setCurrentPosition(paramLong);
  }
  
  public void setDuration(long paramLong)
  {
    if (this.mTotalTimeMs != paramLong)
    {
      this.mTotalTimeMs = paramLong;
      if (this.mListener != null) {
        this.mListener.onDurationChanged(this, this.mTotalTimeMs);
      }
    }
  }
  
  public final void setImageBitmap(Context paramContext, Bitmap paramBitmap)
  {
    this.mImageDrawable = new BitmapDrawable(paramContext.getResources(), paramBitmap);
  }
  
  public final void setImageDrawable(Drawable paramDrawable)
  {
    this.mImageDrawable = paramDrawable;
  }
  
  public void setOnPlaybackProgressChangedListener(OnPlaybackProgressCallback paramOnPlaybackProgressCallback)
  {
    this.mListener = paramOnPlaybackProgressCallback;
  }
  
  public final void setPrimaryActionsAdapter(ObjectAdapter paramObjectAdapter)
  {
    this.mPrimaryActionsAdapter = paramObjectAdapter;
  }
  
  public final void setSecondaryActionsAdapter(ObjectAdapter paramObjectAdapter)
  {
    this.mSecondaryActionsAdapter = paramObjectAdapter;
  }
  
  @Deprecated
  public void setTotalTime(int paramInt)
  {
    setDuration(paramInt);
  }
  
  @Deprecated
  public void setTotalTimeLong(long paramLong)
  {
    setDuration(paramLong);
  }
  
  public static class ClosedCaptioningAction
    extends PlaybackControlsRow.MultiAction
  {
    public static final int INDEX_OFF = 0;
    public static final int INDEX_ON = 1;
    @Deprecated
    public static int OFF = 0;
    @Deprecated
    public static int ON = 1;
    
    public ClosedCaptioningAction(Context paramContext)
    {
      this(paramContext, PlaybackControlsRow.getIconHighlightColor(paramContext));
    }
    
    public ClosedCaptioningAction(Context paramContext, int paramInt)
    {
      super();
      Object localObject = (BitmapDrawable)PlaybackControlsRow.getStyledDrawable(paramContext, R.styleable.lbPlaybackControlsActionIcons_closed_captioning);
      Drawable[] arrayOfDrawable = new Drawable[2];
      arrayOfDrawable[0] = localObject;
      arrayOfDrawable[1] = new BitmapDrawable(paramContext.getResources(), PlaybackControlsRow.createBitmap(((BitmapDrawable)localObject).getBitmap(), paramInt));
      setDrawables(arrayOfDrawable);
      localObject = new String[arrayOfDrawable.length];
      localObject[0] = paramContext.getString(R.string.lb_playback_controls_closed_captioning_enable);
      localObject[1] = paramContext.getString(R.string.lb_playback_controls_closed_captioning_disable);
      setLabels((String[])localObject);
    }
  }
  
  public static class FastForwardAction
    extends PlaybackControlsRow.MultiAction
  {
    public FastForwardAction(Context paramContext)
    {
      this(paramContext, 1);
    }
    
    public FastForwardAction(Context paramContext, int paramInt)
    {
      super();
      if (paramInt < 1) {
        throw new IllegalArgumentException("numSpeeds must be > 0");
      }
      Object localObject = new Drawable[paramInt + 1];
      localObject[0] = PlaybackControlsRow.getStyledDrawable(paramContext, R.styleable.lbPlaybackControlsActionIcons_fast_forward);
      setDrawables((Drawable[])localObject);
      localObject = new String[getActionCount()];
      localObject[0] = paramContext.getString(R.string.lb_playback_controls_fast_forward);
      String[] arrayOfString = new String[getActionCount()];
      arrayOfString[0] = localObject[0];
      int i = 1;
      while (i <= paramInt)
      {
        int j = i + 1;
        localObject[i] = paramContext.getResources().getString(R.string.lb_control_display_fast_forward_multiplier, new Object[] { Integer.valueOf(j) });
        arrayOfString[i] = paramContext.getResources().getString(R.string.lb_playback_controls_fast_forward_multiplier, new Object[] { Integer.valueOf(j) });
        i += 1;
      }
      setLabels((String[])localObject);
      setSecondaryLabels(arrayOfString);
      addKeyCode(90);
    }
  }
  
  public static class HighQualityAction
    extends PlaybackControlsRow.MultiAction
  {
    public static final int INDEX_OFF = 0;
    public static final int INDEX_ON = 1;
    @Deprecated
    public static int OFF = 0;
    @Deprecated
    public static int ON = 1;
    
    public HighQualityAction(Context paramContext)
    {
      this(paramContext, PlaybackControlsRow.getIconHighlightColor(paramContext));
    }
    
    public HighQualityAction(Context paramContext, int paramInt)
    {
      super();
      Object localObject = (BitmapDrawable)PlaybackControlsRow.getStyledDrawable(paramContext, R.styleable.lbPlaybackControlsActionIcons_high_quality);
      Drawable[] arrayOfDrawable = new Drawable[2];
      arrayOfDrawable[0] = localObject;
      arrayOfDrawable[1] = new BitmapDrawable(paramContext.getResources(), PlaybackControlsRow.createBitmap(((BitmapDrawable)localObject).getBitmap(), paramInt));
      setDrawables(arrayOfDrawable);
      localObject = new String[arrayOfDrawable.length];
      localObject[0] = paramContext.getString(R.string.lb_playback_controls_high_quality_enable);
      localObject[1] = paramContext.getString(R.string.lb_playback_controls_high_quality_disable);
      setLabels((String[])localObject);
    }
  }
  
  public static class MoreActions
    extends Action
  {
    public MoreActions(Context paramContext)
    {
      super();
      setIcon(paramContext.getResources().getDrawable(R.drawable.lb_ic_more));
      setLabel1(paramContext.getString(R.string.lb_playback_controls_more_actions));
    }
  }
  
  public static abstract class MultiAction
    extends Action
  {
    private Drawable[] mDrawables;
    private int mIndex;
    private String[] mLabels;
    private String[] mLabels2;
    
    public MultiAction(int paramInt)
    {
      super();
    }
    
    public int getActionCount()
    {
      if (this.mDrawables != null) {
        return this.mDrawables.length;
      }
      if (this.mLabels != null) {
        return this.mLabels.length;
      }
      return 0;
    }
    
    public Drawable getDrawable(int paramInt)
    {
      if (this.mDrawables == null) {
        return null;
      }
      return this.mDrawables[paramInt];
    }
    
    public int getIndex()
    {
      return this.mIndex;
    }
    
    public String getLabel(int paramInt)
    {
      if (this.mLabels == null) {
        return null;
      }
      return this.mLabels[paramInt];
    }
    
    public String getSecondaryLabel(int paramInt)
    {
      if (this.mLabels2 == null) {
        return null;
      }
      return this.mLabels2[paramInt];
    }
    
    public void nextIndex()
    {
      if (this.mIndex < getActionCount() - 1) {}
      for (int i = this.mIndex + 1;; i = 0)
      {
        setIndex(i);
        return;
      }
    }
    
    public void setDrawables(Drawable[] paramArrayOfDrawable)
    {
      this.mDrawables = paramArrayOfDrawable;
      setIndex(0);
    }
    
    public void setIndex(int paramInt)
    {
      this.mIndex = paramInt;
      if (this.mDrawables != null) {
        setIcon(this.mDrawables[this.mIndex]);
      }
      if (this.mLabels != null) {
        setLabel1(this.mLabels[this.mIndex]);
      }
      if (this.mLabels2 != null) {
        setLabel2(this.mLabels2[this.mIndex]);
      }
    }
    
    public void setLabels(String[] paramArrayOfString)
    {
      this.mLabels = paramArrayOfString;
      setIndex(0);
    }
    
    public void setSecondaryLabels(String[] paramArrayOfString)
    {
      this.mLabels2 = paramArrayOfString;
      setIndex(0);
    }
  }
  
  public static class OnPlaybackProgressCallback
  {
    public void onBufferedPositionChanged(PlaybackControlsRow paramPlaybackControlsRow, long paramLong) {}
    
    public void onCurrentPositionChanged(PlaybackControlsRow paramPlaybackControlsRow, long paramLong) {}
    
    public void onDurationChanged(PlaybackControlsRow paramPlaybackControlsRow, long paramLong) {}
  }
  
  public static class PictureInPictureAction
    extends Action
  {
    public PictureInPictureAction(Context paramContext)
    {
      super();
      setIcon(PlaybackControlsRow.getStyledDrawable(paramContext, R.styleable.lbPlaybackControlsActionIcons_picture_in_picture));
      setLabel1(paramContext.getString(R.string.lb_playback_controls_picture_in_picture));
      addKeyCode(171);
    }
  }
  
  public static class PlayPauseAction
    extends PlaybackControlsRow.MultiAction
  {
    public static final int INDEX_PAUSE = 1;
    public static final int INDEX_PLAY = 0;
    @Deprecated
    public static int PAUSE = 1;
    @Deprecated
    public static int PLAY = 0;
    
    public PlayPauseAction(Context paramContext)
    {
      super();
      Object localObject = new Drawable[2];
      localObject[0] = PlaybackControlsRow.getStyledDrawable(paramContext, R.styleable.lbPlaybackControlsActionIcons_play);
      localObject[1] = PlaybackControlsRow.getStyledDrawable(paramContext, R.styleable.lbPlaybackControlsActionIcons_pause);
      setDrawables((Drawable[])localObject);
      localObject = new String[localObject.length];
      localObject[0] = paramContext.getString(R.string.lb_playback_controls_play);
      localObject[1] = paramContext.getString(R.string.lb_playback_controls_pause);
      setLabels((String[])localObject);
      addKeyCode(85);
      addKeyCode(126);
      addKeyCode(127);
    }
  }
  
  public static class RepeatAction
    extends PlaybackControlsRow.MultiAction
  {
    @Deprecated
    public static int ALL = 1;
    public static final int INDEX_ALL = 1;
    public static final int INDEX_NONE = 0;
    public static final int INDEX_ONE = 2;
    @Deprecated
    public static int NONE = 0;
    @Deprecated
    public static int ONE = 2;
    
    public RepeatAction(Context paramContext)
    {
      this(paramContext, PlaybackControlsRow.getIconHighlightColor(paramContext));
    }
    
    public RepeatAction(Context paramContext, int paramInt)
    {
      this(paramContext, paramInt, paramInt);
    }
    
    public RepeatAction(Context paramContext, int paramInt1, int paramInt2)
    {
      super();
      Drawable[] arrayOfDrawable = new Drawable[3];
      Object localObject1 = (BitmapDrawable)PlaybackControlsRow.getStyledDrawable(paramContext, R.styleable.lbPlaybackControlsActionIcons_repeat);
      BitmapDrawable localBitmapDrawable = (BitmapDrawable)PlaybackControlsRow.getStyledDrawable(paramContext, R.styleable.lbPlaybackControlsActionIcons_repeat_one);
      arrayOfDrawable[0] = localObject1;
      if (localObject1 == null)
      {
        localObject1 = null;
        arrayOfDrawable[1] = localObject1;
        if (localBitmapDrawable != null) {
          break label154;
        }
      }
      label154:
      for (localObject1 = localObject2;; localObject1 = new BitmapDrawable(paramContext.getResources(), PlaybackControlsRow.createBitmap(localBitmapDrawable.getBitmap(), paramInt2)))
      {
        arrayOfDrawable[2] = localObject1;
        setDrawables(arrayOfDrawable);
        localObject1 = new String[arrayOfDrawable.length];
        localObject1[0] = paramContext.getString(R.string.lb_playback_controls_repeat_all);
        localObject1[1] = paramContext.getString(R.string.lb_playback_controls_repeat_one);
        localObject1[2] = paramContext.getString(R.string.lb_playback_controls_repeat_none);
        setLabels((String[])localObject1);
        return;
        localObject1 = new BitmapDrawable(paramContext.getResources(), PlaybackControlsRow.createBitmap(((BitmapDrawable)localObject1).getBitmap(), paramInt1));
        break;
      }
    }
  }
  
  public static class RewindAction
    extends PlaybackControlsRow.MultiAction
  {
    public RewindAction(Context paramContext)
    {
      this(paramContext, 1);
    }
    
    public RewindAction(Context paramContext, int paramInt)
    {
      super();
      if (paramInt < 1) {
        throw new IllegalArgumentException("numSpeeds must be > 0");
      }
      Object localObject = new Drawable[paramInt + 1];
      localObject[0] = PlaybackControlsRow.getStyledDrawable(paramContext, R.styleable.lbPlaybackControlsActionIcons_rewind);
      setDrawables((Drawable[])localObject);
      localObject = new String[getActionCount()];
      localObject[0] = paramContext.getString(R.string.lb_playback_controls_rewind);
      String[] arrayOfString = new String[getActionCount()];
      arrayOfString[0] = localObject[0];
      int i = 1;
      while (i <= paramInt)
      {
        int j = i + 1;
        String str = paramContext.getResources().getString(R.string.lb_control_display_rewind_multiplier, new Object[] { Integer.valueOf(j) });
        localObject[i] = str;
        localObject[i] = str;
        arrayOfString[i] = paramContext.getResources().getString(R.string.lb_playback_controls_rewind_multiplier, new Object[] { Integer.valueOf(j) });
        i += 1;
      }
      setLabels((String[])localObject);
      setSecondaryLabels(arrayOfString);
      addKeyCode(89);
    }
  }
  
  public static class ShuffleAction
    extends PlaybackControlsRow.MultiAction
  {
    public static final int INDEX_OFF = 0;
    public static final int INDEX_ON = 1;
    @Deprecated
    public static int OFF = 0;
    @Deprecated
    public static int ON = 1;
    
    public ShuffleAction(Context paramContext)
    {
      this(paramContext, PlaybackControlsRow.getIconHighlightColor(paramContext));
    }
    
    public ShuffleAction(Context paramContext, int paramInt)
    {
      super();
      Object localObject = (BitmapDrawable)PlaybackControlsRow.getStyledDrawable(paramContext, R.styleable.lbPlaybackControlsActionIcons_shuffle);
      Drawable[] arrayOfDrawable = new Drawable[2];
      arrayOfDrawable[0] = localObject;
      arrayOfDrawable[1] = new BitmapDrawable(paramContext.getResources(), PlaybackControlsRow.createBitmap(((BitmapDrawable)localObject).getBitmap(), paramInt));
      setDrawables(arrayOfDrawable);
      localObject = new String[arrayOfDrawable.length];
      localObject[0] = paramContext.getString(R.string.lb_playback_controls_shuffle_enable);
      localObject[1] = paramContext.getString(R.string.lb_playback_controls_shuffle_disable);
      setLabels((String[])localObject);
    }
  }
  
  public static class SkipNextAction
    extends Action
  {
    public SkipNextAction(Context paramContext)
    {
      super();
      setIcon(PlaybackControlsRow.getStyledDrawable(paramContext, R.styleable.lbPlaybackControlsActionIcons_skip_next));
      setLabel1(paramContext.getString(R.string.lb_playback_controls_skip_next));
      addKeyCode(87);
    }
  }
  
  public static class SkipPreviousAction
    extends Action
  {
    public SkipPreviousAction(Context paramContext)
    {
      super();
      setIcon(PlaybackControlsRow.getStyledDrawable(paramContext, R.styleable.lbPlaybackControlsActionIcons_skip_previous));
      setLabel1(paramContext.getString(R.string.lb_playback_controls_skip_previous));
      addKeyCode(88);
    }
  }
  
  public static abstract class ThumbsAction
    extends PlaybackControlsRow.MultiAction
  {
    public static final int INDEX_OUTLINE = 1;
    public static final int INDEX_SOLID = 0;
    @Deprecated
    public static int OUTLINE = 1;
    @Deprecated
    public static int SOLID = 0;
    
    public ThumbsAction(int paramInt1, Context paramContext, int paramInt2, int paramInt3)
    {
      super();
      setDrawables(new Drawable[] { PlaybackControlsRow.getStyledDrawable(paramContext, paramInt2), PlaybackControlsRow.getStyledDrawable(paramContext, paramInt3) });
    }
  }
  
  public static class ThumbsDownAction
    extends PlaybackControlsRow.ThumbsAction
  {
    public ThumbsDownAction(Context paramContext)
    {
      super(paramContext, R.styleable.lbPlaybackControlsActionIcons_thumb_down, R.styleable.lbPlaybackControlsActionIcons_thumb_down_outline);
      String[] arrayOfString = new String[getActionCount()];
      arrayOfString[0] = paramContext.getString(R.string.lb_playback_controls_thumb_down);
      arrayOfString[1] = paramContext.getString(R.string.lb_playback_controls_thumb_down_outline);
      setLabels(arrayOfString);
    }
  }
  
  public static class ThumbsUpAction
    extends PlaybackControlsRow.ThumbsAction
  {
    public ThumbsUpAction(Context paramContext)
    {
      super(paramContext, R.styleable.lbPlaybackControlsActionIcons_thumb_up, R.styleable.lbPlaybackControlsActionIcons_thumb_up_outline);
      String[] arrayOfString = new String[getActionCount()];
      arrayOfString[0] = paramContext.getString(R.string.lb_playback_controls_thumb_up);
      arrayOfString[1] = paramContext.getString(R.string.lb_playback_controls_thumb_up_outline);
      setLabels(arrayOfString);
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/PlaybackControlsRow.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */