package com.google.android.tvlauncher.home;

import android.content.Context;
import android.graphics.Outline;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;

import com.google.android.tvlauncher.R;
import com.google.android.tvlauncher.analytics.EventLogger;
import com.google.android.tvlauncher.data.TvDataManager;
import com.google.android.tvlauncher.data.WatchNextProgramsObserver;
import com.google.android.tvlauncher.home.util.ProgramSettings;
import com.google.android.tvlauncher.home.util.ProgramUtil;
import com.google.android.tvlauncher.model.Program;
import com.google.android.tvlauncher.util.ScaleFocusHandler;

import java.util.List;

class WatchNextItemsAdapter extends Adapter<ViewHolder> {
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
    private final WatchNextProgramsObserver mProgramsObserver = new WatchNextProgramsObserver() {
        public void onProgramsChange() {
            WatchNextItemsAdapter.this.notifyDataSetChanged();
        }
    };
    private boolean mStarted;

    private class LastProgramViewHolder extends ViewHolder {
        private final ScaleFocusHandler mFocusHandler;
        private final ProgramSettings mProgramSettings;

        LastProgramViewHolder(View v) {
            super(v);
            v.setOutlineProvider(new ViewOutlineProvider() {
                public void getOutline(View view, Outline outline) {
                    outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), (float) view.getResources().getDimensionPixelSize(R.dimen.card_rounded_corner_radius));
                }
            });
            v.setClipToOutline(true);
            this.mProgramSettings = ProgramUtil.getProgramSettings(v.getContext());
            this.mFocusHandler = new ScaleFocusHandler(this.mProgramSettings.focusedAnimationDuration, this.mProgramSettings.focusedScale, this.mProgramSettings.focusedElevation, 1);

            this.mFocusHandler.setView(v);
            this.mFocusHandler.setOnFocusChangeListener(new OnFocusChangeListener() {
                public void onFocusChange(View v, boolean hasFocus) {
                    if (WatchNextItemsAdapter.this.mOnProgramSelectedListener != null && hasFocus && LastProgramViewHolder.this.getAdapterPosition() != -1) {
                        WatchNextItemsAdapter.this.mOnProgramSelectedListener.onProgramSelected(null, null);
                    }
                }
            });
        }

        void bind(int programState) {
            updateSize(programState);
            this.mFocusHandler.resetFocusedState();
        }

        void updateSize(int programState) {
            ProgramUtil.updateSize(this.itemView, programState, ProgramUtil.ASPECT_RATIO_16_9, this.mProgramSettings);
        }
    }

    private class ProgramViewHolder extends ViewHolder implements OnProgramViewFocusedListener {
        private final ProgramController mProgramController;

        ProgramViewHolder(View v, EventLogger eventLogger) {
            super(v);
            this.mProgramController = new ProgramController(v, eventLogger);
            this.mProgramController.setOnProgramViewFocusedListener(this);
            this.mProgramController.setIsWatchNextProgram(true);
        }

        public void onProgramViewFocused() {
            if (WatchNextItemsAdapter.this.mOnProgramSelectedListener != null) {
                int position = getAdapterPosition();
                if (position != -1) {
                    WatchNextItemsAdapter.this.mOnProgramSelectedListener.onProgramSelected(WatchNextItemsAdapter.this.mDataManager.getWatchNextProgram(position), this.mProgramController);
                }
            }
        }

        ProgramController getProgramController() {
            return this.mProgramController;
        }

        void bind(Program program, int programState) {
            this.mProgramController.bind(program, null, programState, false, false, false);
            if (WatchNextItemsAdapter.this.mOnProgramSelectedListener != null && this.mProgramController.isViewFocused()) {
                WatchNextItemsAdapter.this.mOnProgramSelectedListener.onProgramSelected(program, this.mProgramController);
            }
        }
    }

    WatchNextItemsAdapter(Context context, EventLogger eventLogger) {
        this.mDataManager = TvDataManager.getInstance(context);
        this.mEventLogger = eventLogger;
        setHasStableIds(true);
    }

    void setOnProgramSelectedListener(OnProgramSelectedListener listener) {
        this.mOnProgramSelectedListener = listener;
    }

    void bind(int programState) {
        this.mProgramState = programState;
        if (!registerObserverAndUpdateDataIfNeeded()) {
            notifyDataSetChanged();
        }
        this.mStarted = true;
    }

    int getProgramState() {
        return this.mProgramState;
    }

    void setProgramState(int programState) {
        if (this.mProgramState != programState) {
            this.mProgramState = programState;
            notifyItemRangeChanged(0, getItemCount(), PAYLOAD_STATE);
        }
    }

    private boolean registerObserverAndUpdateDataIfNeeded() {
        this.mDataManager.registerWatchNextProgramsObserver(this.mProgramsObserver);
        if (this.mDataManager.isWatchNextProgramsDataLoaded() && !this.mDataManager.isWatchNextProgramsDataStale()) {
            return false;
        }
        this.mDataManager.loadWatchNextProgramData();
        return true;
    }

    public void onStart() {
        if (!this.mStarted) {
            registerObserverAndUpdateDataIfNeeded();
        }
        this.mStarted = true;
    }

    public void onStop() {
        if (this.mStarted) {
            this.mDataManager.unregisterWatchNextProgramsObserver(this.mProgramsObserver);
        }
        this.mStarted = false;
    }

    void recycle() {
        onStop();
    }

    public int getItemCount() {
        if (!this.mDataManager.isWatchNextProgramsDataLoaded()) {
            return 0;
        }
        int programCount = this.mDataManager.getWatchNextProgramsCount();
        if (programCount > 0) {
            return programCount;
        }
        return 1;
    }

    public int getItemViewType(int position) {
        if (this.mDataManager.getWatchNextProgramsCount() == 0) {
            return 0;
        }
        return 1;
    }

    public long getItemId(int position) {
        if (getItemViewType(position) == 0) {
            return -2;
        }
        return this.mDataManager.getWatchNextProgram(position).getId();
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 1) {
            return new ProgramViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_program, parent, false), this.mEventLogger);
        }
        if (viewType == 0) {
            return new LastProgramViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_watch_next_last_program, parent, false));
        }
        return null;
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        if (holder instanceof ProgramViewHolder) {
            ((ProgramViewHolder) holder).bind(this.mDataManager.getWatchNextProgram(position), this.mProgramState);
        } else if (holder instanceof LastProgramViewHolder) {
            ((LastProgramViewHolder) holder).bind(this.mProgramState);
        }
    }

    public void onBindViewHolder(ViewHolder holder, int position, List<Object> payloads) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else if (holder instanceof ProgramViewHolder) {
            ((ProgramViewHolder) holder).getProgramController().bindState(this.mProgramState);
        } else if (holder instanceof LastProgramViewHolder) {
            ((LastProgramViewHolder) holder).updateSize(this.mProgramState);
        }
    }
}
