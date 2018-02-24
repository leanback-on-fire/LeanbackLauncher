package com.google.android.tvlauncher.appsview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.tvlauncher.R;
import com.google.android.tvlauncher.data.ChannelProgramsObserver;
import com.google.android.tvlauncher.data.TvDataManager;
import com.google.android.tvlauncher.util.IntentLauncher;

class PromotionRowAdapter extends RecyclerView.Adapter<PromotionRowAdapter.PromotionViewHolder> {
    private static final boolean DEBUG = false;
    private static final int NO_CHANNEL_ID = -1;
    private static final String TAG = "PromotionRowAdapter";
    private long mChannelId = -1L;
    private TvDataManager mDataManager;
    private final ChannelProgramsObserver mProgramsObserver = new ChannelProgramsObserver() {
        public void onProgramsChange(long paramAnonymousLong) {
            PromotionRowAdapter.this.notifyDataSetChanged();
        }
    };
    private boolean mStarted;

    PromotionRowAdapter(Context paramContext) {
        this.mDataManager = TvDataManager.getInstance(paramContext);
        setHasStableIds(true);
    }

    private boolean registerObserverAndUpdateDataIfNeeded() {
        this.mDataManager.registerChannelProgramsObserver(this.mChannelId, this.mProgramsObserver);
        if ((this.mDataManager.isProgramDataLoaded(this.mChannelId)) && (!this.mDataManager.isProgramDataStale(this.mChannelId))) {
            return false;
        }
        this.mDataManager.loadProgramData(this.mChannelId);
        return true;
    }

    public int getItemCount() {
        if (this.mDataManager.isProgramDataLoaded(this.mChannelId)) {
            return this.mDataManager.getProgramCount(this.mChannelId);
        }
        return 0;
    }

    public long getItemId(int paramInt) {
        if (this.mDataManager.isProgramDataLoaded(this.mChannelId)) {
            return this.mDataManager.getProgram(this.mChannelId, paramInt).getId();
        }
        return super.getItemId(paramInt);
    }

    public void onBindViewHolder(PromotionViewHolder paramPromotionViewHolder, int paramInt) {
        if (this.mDataManager.isProgramDataLoaded(this.mChannelId)) {
            paramPromotionViewHolder.mActionUri = this.mDataManager.getProgram(this.mChannelId, paramInt).getActionUri();
            Glide.with(paramPromotionViewHolder.mPreviewImage.getContext()).load(this.mDataManager.getProgram(this.mChannelId, paramInt).getPreviewImageUri()).into(paramPromotionViewHolder.mPreviewImage);
        }
    }

    public PromotionViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt) {
        return new PromotionViewHolder(LayoutInflater.from(paramViewGroup.getContext()).inflate(2130968607, paramViewGroup, false));
    }

    void onStart() {
        if ((!this.mStarted) && (this.mChannelId != -1L)) {
            registerObserverAndUpdateDataIfNeeded();
        }
        this.mStarted = true;
    }

    void onStop() {
        if ((this.mStarted) && (this.mChannelId != -1L)) {
            this.mDataManager.unregisterChannelProgramsObserver(this.mChannelId, this.mProgramsObserver);
        }
        this.mStarted = false;
    }

    public void setChannelId(long paramLong) {
        if (paramLong != this.mChannelId) {
            if (this.mChannelId != -1L) {
                this.mDataManager.unregisterChannelProgramsObserver(this.mChannelId, this.mProgramsObserver);
            }
            this.mChannelId = paramLong;
            if (this.mChannelId != -1L) {
                if (!registerObserverAndUpdateDataIfNeeded()) {
                    notifyDataSetChanged();
                }
                this.mStarted = true;
            }
        } else {
            return;
        }
        this.mStarted = false;
    }

    class PromotionViewHolder
            extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        private String mActionUri;
        private ImageView mPreviewImage;

        PromotionViewHolder(View paramView) {
            super(paramView);
            this.mPreviewImage = ((ImageView) paramView.findViewById(R.id.preview_image));
            paramView.setOnClickListener(this);
        }

        public void onClick(View paramView) {
            if (this.mActionUri != null) {
                IntentLauncher.launchIntentFromUri(paramView.getContext(), this.mActionUri);
            }
        }
    }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/appsview/PromotionRowAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */