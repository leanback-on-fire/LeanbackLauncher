package com.google.android.tvlauncher.home;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Outline;
import android.support.v17.leanback.widget.FacetProvider;
import android.support.v17.leanback.widget.ItemAlignmentFacet;
import android.support.v17.leanback.widget.ItemAlignmentFacet.ItemAlignmentDef;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.tvlauncher.analytics.EventLogger;
import com.google.android.tvlauncher.analytics.LogEvent;
import com.google.android.tvlauncher.data.ChannelProgramsObserver;
import com.google.android.tvlauncher.data.TvDataManager;
import com.google.android.tvlauncher.home.util.ProgramSettings;
import com.google.android.tvlauncher.home.util.ProgramUtil;
import com.google.android.tvlauncher.model.Program;
import com.google.android.tvlauncher.util.ColorUtils;
import com.google.android.tvlauncher.util.ScaleFocusHandler;
import java.util.List;

class ChannelItemsAdapter
  extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
  private static final boolean DEBUG = false;
  private static final int EMPTY_CHANNEL_PROGRAM_ID = -3;
  private static final float LAST_PROGRAM_FOCUSED_COLOR_DARKEN_FACTOR = 0.8F;
  private static final int LAST_PROGRAM_ID = -2;
  private static final int NO_CHANNEL_ID = -1;
  private static final String PAYLOAD_STATE = "PAYLOAD_STATE";
  private static final String TAG = "ChannelItemsAdapter";
  private static final int TYPE_EMPTY_CHANNEL_PROGRAM = 2;
  private static final int TYPE_LAST_PROGRAM = 1;
  private static final int TYPE_PROGRAM = 0;
  private boolean mCanAddToWatchNext;
  private boolean mCanRemoveProgram;
  private long mChannelId = -1L;
  private Bitmap mChannelLogo;
  private final TvDataManager mDataManager;
  private final EventLogger mEventLogger;
  private boolean mIsLegacy;
  private int mLastProgramFocusedColor;
  private OnPerformLastProgramActionListener mOnPerformLastProgramActionListener;
  private OnProgramSelectedListener mOnProgramSelectedListener;
  private String mPackageName;
  private int mPrimaryColor;
  private int mProgramState = 0;
  private final ChannelProgramsObserver mProgramsObserver = new ChannelProgramsObserver()
  {
    public void onProgramsChange(long paramAnonymousLong)
    {
      ChannelItemsAdapter.this.notifyDataSetChanged();
    }
  };
  private boolean mStarted;
  
  ChannelItemsAdapter(Context paramContext, EventLogger paramEventLogger)
  {
    this.mDataManager = TvDataManager.getInstance(paramContext);
    this.mEventLogger = paramEventLogger;
    setHasStableIds(true);
  }
  
  private boolean registerObserverAndUpdateDataIfNeeded()
  {
    this.mDataManager.registerChannelProgramsObserver(this.mChannelId, this.mProgramsObserver);
    if ((this.mDataManager.isProgramDataLoaded(this.mChannelId)) && (!this.mDataManager.isProgramDataStale(this.mChannelId))) {
      return false;
    }
    this.mDataManager.loadProgramData(this.mChannelId);
    return true;
  }
  
  void bind(long paramLong, String paramString, int paramInt, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    this.mPackageName = paramString;
    this.mCanAddToWatchNext = paramBoolean1;
    this.mCanRemoveProgram = paramBoolean2;
    this.mIsLegacy = paramBoolean3;
    int i = this.mProgramState;
    this.mProgramState = paramInt;
    if (paramLong != this.mChannelId)
    {
      if (this.mChannelId != -1L) {
        this.mDataManager.unregisterChannelProgramsObserver(this.mChannelId, this.mProgramsObserver);
      }
      this.mChannelId = paramLong;
      if (this.mChannelId != -1L)
      {
        if (!registerObserverAndUpdateDataIfNeeded()) {
          notifyDataSetChanged();
        }
        this.mStarted = true;
      }
    }
    while (i == paramInt)
    {
      return;
      this.mStarted = false;
      return;
    }
    notifyItemRangeChanged(0, getItemCount(), "PAYLOAD_STATE");
  }
  
  public int getItemCount()
  {
    if (this.mDataManager.isProgramDataLoaded(this.mChannelId)) {
      return this.mDataManager.getProgramCount(this.mChannelId) + 1;
    }
    return 1;
  }
  
  public long getItemId(int paramInt)
  {
    int i = getItemViewType(paramInt);
    if (i == 1) {
      return -2L;
    }
    if (i == 2) {
      return -3L;
    }
    return this.mDataManager.getProgram(this.mChannelId, paramInt).getId();
  }
  
  public int getItemViewType(int paramInt)
  {
    int i = 1;
    int j = getItemCount();
    if (j == 1) {
      i = 2;
    }
    while (paramInt == j - 1) {
      return i;
    }
    return 0;
  }
  
  int getProgramState()
  {
    return this.mProgramState;
  }
  
  public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
  {
    if ((paramViewHolder instanceof ProgramViewHolder)) {
      ((ProgramViewHolder)paramViewHolder).bind(this.mDataManager.getProgram(this.mChannelId, paramInt));
    }
    do
    {
      return;
      if ((paramViewHolder instanceof LastProgramViewHolder))
      {
        ((LastProgramViewHolder)paramViewHolder).bind();
        return;
      }
    } while (!(paramViewHolder instanceof EmptyChannelProgramViewHolder));
    ((EmptyChannelProgramViewHolder)paramViewHolder).bind();
  }
  
  public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt, List<Object> paramList)
  {
    if (paramList.isEmpty()) {
      onBindViewHolder(paramViewHolder, paramInt);
    }
    do
    {
      return;
      if ((paramViewHolder instanceof ProgramViewHolder))
      {
        ((ProgramViewHolder)paramViewHolder).getProgramController().bindState(this.mProgramState);
        return;
      }
      if ((paramViewHolder instanceof LastProgramViewHolder))
      {
        ((LastProgramViewHolder)paramViewHolder).bindState();
        return;
      }
    } while (!(paramViewHolder instanceof EmptyChannelProgramViewHolder));
    ((EmptyChannelProgramViewHolder)paramViewHolder).bindState();
  }
  
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
  {
    if (paramInt == 0) {
      return new ProgramViewHolder(LayoutInflater.from(paramViewGroup.getContext()).inflate(2130968745, paramViewGroup, false), this.mEventLogger);
    }
    if (paramInt == 1) {
      return new LastProgramViewHolder(LayoutInflater.from(paramViewGroup.getContext()).inflate(2130968744, paramViewGroup, false));
    }
    if (paramInt == 2) {
      return new EmptyChannelProgramViewHolder(LayoutInflater.from(paramViewGroup.getContext()).inflate(2130968741, paramViewGroup, false));
    }
    return null;
  }
  
  public void onStart()
  {
    if ((!this.mStarted) && (this.mChannelId != -1L)) {
      registerObserverAndUpdateDataIfNeeded();
    }
    this.mStarted = true;
  }
  
  public void onStop()
  {
    if ((this.mStarted) && (this.mChannelId != -1L)) {
      this.mDataManager.unregisterChannelProgramsObserver(this.mChannelId, this.mProgramsObserver);
    }
    this.mStarted = false;
  }
  
  void recycle()
  {
    bind(-1L, null, this.mProgramState, false, false, false);
    this.mStarted = false;
    notifyDataSetChanged();
  }
  
  void setChannelLogoAndPrimaryColor(Bitmap paramBitmap, int paramInt)
  {
    this.mChannelLogo = paramBitmap;
    this.mPrimaryColor = paramInt;
    this.mLastProgramFocusedColor = ColorUtils.darkenColor(this.mPrimaryColor, 0.8F);
    paramInt = getItemCount();
    if (paramInt > 0) {
      notifyItemChanged(paramInt - 1);
    }
  }
  
  void setOnPerformLastProgramActionListener(OnPerformLastProgramActionListener paramOnPerformLastProgramActionListener)
  {
    this.mOnPerformLastProgramActionListener = paramOnPerformLastProgramActionListener;
  }
  
  void setOnProgramSelectedListener(OnProgramSelectedListener paramOnProgramSelectedListener)
  {
    this.mOnProgramSelectedListener = paramOnProgramSelectedListener;
  }
  
  void setProgramState(int paramInt)
  {
    if (this.mProgramState != paramInt)
    {
      this.mProgramState = paramInt;
      notifyItemRangeChanged(0, getItemCount(), "PAYLOAD_STATE");
    }
  }
  
  private class BaseSpecialProgramViewHolder
    extends RecyclerView.ViewHolder
  {
    private final ScaleFocusHandler mFocusHandler;
    final ProgramSettings mProgramSettings;
    
    BaseSpecialProgramViewHolder(View paramView)
    {
      super();
      this.mProgramSettings = ProgramUtil.getProgramSettings(paramView.getContext());
      this.mFocusHandler = new ScaleFocusHandler(this.mProgramSettings.focusedAnimationDuration, this.mProgramSettings.focusedScale, this.mProgramSettings.focusedElevation, 1);
      this.mFocusHandler.setView(paramView);
      this.mFocusHandler.setOnFocusChangeListener(new View.OnFocusChangeListener()
      {
        public void onFocusChange(View paramAnonymousView, boolean paramAnonymousBoolean)
        {
          ChannelItemsAdapter.BaseSpecialProgramViewHolder.this.handleFocusChange(paramAnonymousView, paramAnonymousBoolean);
        }
      });
      paramView.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          if (ChannelItemsAdapter.this.mOnPerformLastProgramActionListener != null) {
            ChannelItemsAdapter.this.mOnPerformLastProgramActionListener.onPerformLastProgramAction();
          }
        }
      });
    }
    
    void bind()
    {
      bindState();
      this.mFocusHandler.resetFocusedState();
    }
    
    void bindState()
    {
      ProgramUtil.updateSize(this.itemView, ChannelItemsAdapter.this.mProgramState, 1.7777777777777777D, this.mProgramSettings);
    }
    
    protected void handleFocusChange(View paramView, boolean paramBoolean)
    {
      if ((ChannelItemsAdapter.this.mOnProgramSelectedListener != null) && (paramBoolean) && (getAdapterPosition() != -1)) {
        ChannelItemsAdapter.this.mOnProgramSelectedListener.onProgramSelected(null, null);
      }
    }
    
    void setRoundedCornerClipOutline(View paramView)
    {
      paramView.setOutlineProvider(new ViewOutlineProvider()
      {
        public void getOutline(View paramAnonymousView, Outline paramAnonymousOutline)
        {
          paramAnonymousOutline.setRoundRect(0, 0, paramAnonymousView.getWidth(), paramAnonymousView.getHeight(), paramAnonymousView.getResources().getDimensionPixelSize(2131558510));
        }
      });
      paramView.setClipToOutline(true);
    }
  }
  
  private class EmptyChannelProgramViewHolder
    extends ChannelItemsAdapter.BaseSpecialProgramViewHolder
  {
    private static final float DARKEN_FACTOR_BOTTOM_LAYER = 0.269F;
    private static final float DARKEN_FACTOR_MIDDLE_LAYER = 0.376F;
    private final View mBottomLayer;
    private final int mIconSize;
    private final View mMiddleLayer;
    private final ImageView mTopLayer;
    private final int mTopLayerMarginEnd;
    
    EmptyChannelProgramViewHolder(View paramView)
    {
      super(paramView);
      this.mIconSize = paramView.getResources().getDimensionPixelSize(2131558568);
      this.mTopLayerMarginEnd = paramView.getResources().getDimensionPixelSize(2131558572);
      this.mBottomLayer = paramView.findViewById(2131952035);
      this.mMiddleLayer = paramView.findViewById(2131952036);
      this.mTopLayer = ((ImageView)paramView.findViewById(2131952037));
      setRoundedCornerClipOutline(this.mBottomLayer);
      setRoundedCornerClipOutline(this.mMiddleLayer);
      setRoundedCornerClipOutline(this.mTopLayer);
      paramView.setOutlineProvider(new ViewOutlineProvider()
      {
        public void getOutline(View paramAnonymousView, Outline paramAnonymousOutline)
        {
          if (paramAnonymousView.getLayoutDirection() == 0)
          {
            paramAnonymousOutline.setRoundRect(0, 0, paramAnonymousView.getWidth() - ChannelItemsAdapter.EmptyChannelProgramViewHolder.this.mTopLayerMarginEnd, paramAnonymousView.getHeight(), paramAnonymousView.getResources().getDimensionPixelSize(2131558510));
            return;
          }
          paramAnonymousOutline.setRoundRect(ChannelItemsAdapter.EmptyChannelProgramViewHolder.this.mTopLayerMarginEnd, 0, paramAnonymousView.getWidth(), paramAnonymousView.getHeight(), paramAnonymousView.getResources().getDimensionPixelSize(2131558510));
        }
      });
    }
    
    void bind()
    {
      updateLogoAndColor();
      super.bind();
    }
    
    void bindState()
    {
      ViewGroup.MarginLayoutParams localMarginLayoutParams1 = (ViewGroup.MarginLayoutParams)this.mTopLayer.getLayoutParams();
      ViewGroup.MarginLayoutParams localMarginLayoutParams2 = (ViewGroup.MarginLayoutParams)this.itemView.getLayoutParams();
      switch (ChannelItemsAdapter.this.mProgramState)
      {
      }
      for (;;)
      {
        localMarginLayoutParams1.width = ((int)Math.round(localMarginLayoutParams1.height * 1.7777777777777777D));
        this.mTopLayer.setLayoutParams(localMarginLayoutParams1);
        int i = (localMarginLayoutParams1.height - this.mIconSize) / 2;
        int j = (localMarginLayoutParams1.width - this.mIconSize) / 2;
        this.mTopLayer.setPadding(j, i, j, i);
        return;
        localMarginLayoutParams1.height = this.mProgramSettings.defaultHeight;
        localMarginLayoutParams2.setMargins(0, this.mProgramSettings.defaultTopMargin, 0, this.mProgramSettings.defaultBottomMargin);
        localMarginLayoutParams2.setMarginEnd(this.mProgramSettings.defaultHorizontalMargin);
        continue;
        localMarginLayoutParams1.height = this.mProgramSettings.selectedHeight;
        localMarginLayoutParams2.setMargins(0, this.mProgramSettings.selectedVerticalMargin, 0, this.mProgramSettings.selectedVerticalMargin);
        localMarginLayoutParams2.setMarginEnd(this.mProgramSettings.defaultHorizontalMargin);
        continue;
        localMarginLayoutParams1.height = this.mProgramSettings.zoomedOutHeight;
        localMarginLayoutParams2.setMargins(0, this.mProgramSettings.zoomedOutVerticalMargin, 0, this.mProgramSettings.zoomedOutVerticalMargin);
        localMarginLayoutParams2.setMarginEnd(this.mProgramSettings.zoomedOutHorizontalMargin);
      }
    }
    
    void updateLogoAndColor()
    {
      this.mTopLayer.setImageBitmap(ChannelItemsAdapter.this.mChannelLogo);
      this.mTopLayer.setBackgroundColor(ChannelItemsAdapter.this.mPrimaryColor);
      this.mMiddleLayer.setBackgroundColor(ColorUtils.darkenColor(ChannelItemsAdapter.this.mPrimaryColor, 0.376F));
      this.mBottomLayer.setBackgroundColor(ColorUtils.darkenColor(ChannelItemsAdapter.this.mPrimaryColor, 0.269F));
    }
  }
  
  private class LastProgramViewHolder
    extends ChannelItemsAdapter.BaseSpecialProgramViewHolder
    implements FacetProvider
  {
    private final int mDefaultTextSize;
    private ItemAlignmentFacet mFacet;
    private ItemAlignmentFacet.ItemAlignmentDef mItemAlignmentDef;
    private final TextView mText;
    private final int mUnfocusedBackgroundColor;
    private final int mZoomedOutTextSize;
    
    LastProgramViewHolder(View paramView)
    {
      super(paramView);
      this.mText = ((TextView)paramView.findViewById(2131952018));
      setRoundedCornerClipOutline(paramView);
      this.mUnfocusedBackgroundColor = paramView.getContext().getColor(2131820578);
      this.mDefaultTextSize = paramView.getContext().getResources().getDimensionPixelSize(2131558524);
      this.mZoomedOutTextSize = paramView.getContext().getResources().getDimensionPixelSize(2131558526);
      this.mItemAlignmentDef = new ItemAlignmentFacet.ItemAlignmentDef();
      this.mItemAlignmentDef.setItemAlignmentOffsetPercent(-1.0F);
      this.mFacet = new ItemAlignmentFacet();
      this.mFacet.setAlignmentDefs(new ItemAlignmentFacet.ItemAlignmentDef[] { this.mItemAlignmentDef });
    }
    
    void bindState()
    {
      super.bindState();
      switch (ChannelItemsAdapter.this.mProgramState)
      {
      default: 
        return;
      case 0: 
      case 1: 
        this.mText.setTextSize(0, this.mDefaultTextSize);
        return;
      }
      this.mText.setTextSize(0, this.mZoomedOutTextSize);
    }
    
    public Object getFacet(Class<?> paramClass)
    {
      int i = getAdapterPosition();
      if ((i == -1) || (i == 0)) {
        return null;
      }
      double d = ProgramUtil.getAspectRatio(ChannelItemsAdapter.this.mDataManager.getProgram(ChannelItemsAdapter.this.mChannelId, i - 1).getPreviewImageAspectRatio());
      i = 0;
      int j;
      switch (ChannelItemsAdapter.this.mProgramState)
      {
      default: 
        j = (int)(i * d);
        if (ChannelItemsAdapter.this.mProgramState != 2) {
          break;
        }
      }
      for (i = this.mProgramSettings.zoomedOutHorizontalMargin;; i = this.mProgramSettings.defaultHorizontalMargin)
      {
        i = -(j + i);
        this.mItemAlignmentDef.setItemAlignmentOffset(i);
        return this.mFacet;
        i = this.mProgramSettings.defaultHeight;
        break;
        i = this.mProgramSettings.selectedHeight;
        break;
        i = this.mProgramSettings.zoomedOutHeight;
        break;
      }
    }
    
    protected void handleFocusChange(View paramView, boolean paramBoolean)
    {
      super.handleFocusChange(paramView, paramBoolean);
      if (paramBoolean) {}
      for (int i = ChannelItemsAdapter.this.mLastProgramFocusedColor;; i = this.mUnfocusedBackgroundColor)
      {
        paramView.setBackgroundColor(i);
        return;
      }
    }
  }
  
  private class ProgramViewHolder
    extends RecyclerView.ViewHolder
    implements OnProgramViewFocusedListener, EventLogger
  {
    private final EventLogger mEventLogger;
    final ProgramController mProgramController;
    
    ProgramViewHolder(View paramView, EventLogger paramEventLogger)
    {
      super();
      this.mEventLogger = paramEventLogger;
      this.mProgramController = new ProgramController(paramView, this);
      this.mProgramController.setOnProgramViewFocusedListener(this);
      this.mProgramController.setIsWatchNextProgram(false);
    }
    
    void bind(Program paramProgram)
    {
      this.mProgramController.bind(paramProgram, ChannelItemsAdapter.this.mPackageName, ChannelItemsAdapter.this.mProgramState, ChannelItemsAdapter.this.mCanAddToWatchNext, ChannelItemsAdapter.this.mCanRemoveProgram, ChannelItemsAdapter.this.mIsLegacy);
      if ((ChannelItemsAdapter.this.mOnProgramSelectedListener != null) && (this.mProgramController.isViewFocused())) {
        ChannelItemsAdapter.this.mOnProgramSelectedListener.onProgramSelected(paramProgram, this.mProgramController);
      }
    }
    
    ProgramController getProgramController()
    {
      return this.mProgramController;
    }
    
    public void log(LogEvent paramLogEvent)
    {
      paramLogEvent.putParameter("index", getAdapterPosition()).putParameter("count", ChannelItemsAdapter.this.getItemCount());
      this.mEventLogger.log(paramLogEvent);
    }
    
    public void onProgramViewFocused()
    {
      if (ChannelItemsAdapter.this.mOnProgramSelectedListener != null)
      {
        int i = getAdapterPosition();
        if (i != -1)
        {
          Program localProgram = ChannelItemsAdapter.this.mDataManager.getProgram(ChannelItemsAdapter.this.mChannelId, i);
          ChannelItemsAdapter.this.mOnProgramSelectedListener.onProgramSelected(localProgram, this.mProgramController);
        }
      }
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/home/ChannelItemsAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */