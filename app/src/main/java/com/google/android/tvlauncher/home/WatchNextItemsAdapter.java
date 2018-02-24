package com.google.android.tvlauncher.home;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Outline;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import com.google.android.tvlauncher.analytics.EventLogger;
import com.google.android.tvlauncher.data.TvDataManager;
import com.google.android.tvlauncher.data.WatchNextProgramsObserver;
import com.google.android.tvlauncher.home.util.ProgramSettings;
import com.google.android.tvlauncher.home.util.ProgramUtil;
import com.google.android.tvlauncher.model.Program;
import com.google.android.tvlauncher.util.ScaleFocusHandler;
import java.util.List;

class WatchNextItemsAdapter
  extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
  private static final boolean DEBUG = false;
  private static final int LAST_PROGRAM_ID = -2;
  private static final String PAYLOAD_STATE = "PAYLOAD_STATE";
  private static final String TAG = "WatchNextItemsAdapter";
  static final int TYPE_LAST_PROGRAM = 0;
  private static final int TYPE_PROGRAM = 1;
  private final TvDataManager mDataManager;
  private final EventLogger mEventLogger;
  private OnProgramSelectedListener mOnProgramSelectedListener;
  private int mProgramState = 0;
  private final WatchNextProgramsObserver mProgramsObserver = new WatchNextProgramsObserver()
  {
    public void onProgramsChange()
    {
      WatchNextItemsAdapter.this.notifyDataSetChanged();
    }
  };
  private boolean mStarted;
  
  WatchNextItemsAdapter(Context paramContext, EventLogger paramEventLogger)
  {
    this.mDataManager = TvDataManager.getInstance(paramContext);
    this.mEventLogger = paramEventLogger;
    setHasStableIds(true);
  }
  
  private boolean registerObserverAndUpdateDataIfNeeded()
  {
    this.mDataManager.registerWatchNextProgramsObserver(this.mProgramsObserver);
    if ((this.mDataManager.isWatchNextProgramsDataLoaded()) && (!this.mDataManager.isWatchNextProgramsDataStale())) {
      return false;
    }
    this.mDataManager.loadWatchNextProgramData();
    return true;
  }
  
  void bind(int paramInt)
  {
    this.mProgramState = paramInt;
    if (!registerObserverAndUpdateDataIfNeeded()) {
      notifyDataSetChanged();
    }
    this.mStarted = true;
  }
  
  public int getItemCount()
  {
    if (this.mDataManager.isWatchNextProgramsDataLoaded())
    {
      int i = this.mDataManager.getWatchNextProgramsCount();
      if (i > 0) {
        return i;
      }
      return 1;
    }
    return 0;
  }
  
  public long getItemId(int paramInt)
  {
    if (getItemViewType(paramInt) == 0) {
      return -2L;
    }
    return this.mDataManager.getWatchNextProgram(paramInt).getId();
  }
  
  public int getItemViewType(int paramInt)
  {
    if (this.mDataManager.getWatchNextProgramsCount() == 0) {
      return 0;
    }
    return 1;
  }
  
  int getProgramState()
  {
    return this.mProgramState;
  }
  
  public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
  {
    if ((paramViewHolder instanceof ProgramViewHolder)) {
      ((ProgramViewHolder)paramViewHolder).bind(this.mDataManager.getWatchNextProgram(paramInt), this.mProgramState);
    }
    while (!(paramViewHolder instanceof LastProgramViewHolder)) {
      return;
    }
    ((LastProgramViewHolder)paramViewHolder).bind(this.mProgramState);
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
    } while (!(paramViewHolder instanceof LastProgramViewHolder));
    ((LastProgramViewHolder)paramViewHolder).updateSize(this.mProgramState);
  }
  
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
  {
    if (paramInt == 1) {
      return new ProgramViewHolder(LayoutInflater.from(paramViewGroup.getContext()).inflate(2130968745, paramViewGroup, false), this.mEventLogger);
    }
    if (paramInt == 0) {
      return new LastProgramViewHolder(LayoutInflater.from(paramViewGroup.getContext()).inflate(2130968746, paramViewGroup, false));
    }
    return null;
  }
  
  public void onStart()
  {
    if (!this.mStarted) {
      registerObserverAndUpdateDataIfNeeded();
    }
    this.mStarted = true;
  }
  
  public void onStop()
  {
    if (this.mStarted) {
      this.mDataManager.unregisterWatchNextProgramsObserver(this.mProgramsObserver);
    }
    this.mStarted = false;
  }
  
  void recycle()
  {
    onStop();
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
  
  private class LastProgramViewHolder
    extends RecyclerView.ViewHolder
  {
    private final ScaleFocusHandler mFocusHandler;
    private final ProgramSettings mProgramSettings;
    
    LastProgramViewHolder(View paramView)
    {
      super();
      paramView.setOutlineProvider(new ViewOutlineProvider()
      {
        public void getOutline(View paramAnonymousView, Outline paramAnonymousOutline)
        {
          paramAnonymousOutline.setRoundRect(0, 0, paramAnonymousView.getWidth(), paramAnonymousView.getHeight(), paramAnonymousView.getResources().getDimensionPixelSize(2131558510));
        }
      });
      paramView.setClipToOutline(true);
      this.mProgramSettings = ProgramUtil.getProgramSettings(paramView.getContext());
      this.mFocusHandler = new ScaleFocusHandler(this.mProgramSettings.focusedAnimationDuration, this.mProgramSettings.focusedScale, this.mProgramSettings.focusedElevation, 1);
      this.mFocusHandler.setView(paramView);
      this.mFocusHandler.setOnFocusChangeListener(new View.OnFocusChangeListener()
      {
        public void onFocusChange(View paramAnonymousView, boolean paramAnonymousBoolean)
        {
          if ((WatchNextItemsAdapter.this.mOnProgramSelectedListener != null) && (paramAnonymousBoolean) && (WatchNextItemsAdapter.LastProgramViewHolder.this.getAdapterPosition() != -1)) {
            WatchNextItemsAdapter.this.mOnProgramSelectedListener.onProgramSelected(null, null);
          }
        }
      });
    }
    
    void bind(int paramInt)
    {
      updateSize(paramInt);
      this.mFocusHandler.resetFocusedState();
    }
    
    void updateSize(int paramInt)
    {
      ProgramUtil.updateSize(this.itemView, paramInt, 1.7777777777777777D, this.mProgramSettings);
    }
  }
  
  private class ProgramViewHolder
    extends RecyclerView.ViewHolder
    implements OnProgramViewFocusedListener
  {
    private final ProgramController mProgramController;
    
    ProgramViewHolder(View paramView, EventLogger paramEventLogger)
    {
      super();
      this.mProgramController = new ProgramController(paramView, paramEventLogger);
      this.mProgramController.setOnProgramViewFocusedListener(this);
      this.mProgramController.setIsWatchNextProgram(true);
    }
    
    void bind(Program paramProgram, int paramInt)
    {
      this.mProgramController.bind(paramProgram, null, paramInt, false, false, false);
      if ((WatchNextItemsAdapter.this.mOnProgramSelectedListener != null) && (this.mProgramController.isViewFocused())) {
        WatchNextItemsAdapter.this.mOnProgramSelectedListener.onProgramSelected(paramProgram, this.mProgramController);
      }
    }
    
    ProgramController getProgramController()
    {
      return this.mProgramController;
    }
    
    public void onProgramViewFocused()
    {
      if (WatchNextItemsAdapter.this.mOnProgramSelectedListener != null)
      {
        int i = getAdapterPosition();
        if (i != -1)
        {
          Program localProgram = WatchNextItemsAdapter.this.mDataManager.getWatchNextProgram(i);
          WatchNextItemsAdapter.this.mOnProgramSelectedListener.onProgramSelected(localProgram, this.mProgramController);
        }
      }
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/home/WatchNextItemsAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */