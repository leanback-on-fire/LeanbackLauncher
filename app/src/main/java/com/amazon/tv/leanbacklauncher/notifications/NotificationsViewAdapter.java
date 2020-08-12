package com.amazon.tv.leanbacklauncher.notifications;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import androidx.collection.ArraySet;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.DiffUtil.Callback;
import androidx.recyclerview.widget.DiffUtil.DiffResult;
import androidx.recyclerview.widget.ListUpdateCallback;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.amazon.tv.leanbacklauncher.MainActivity;
import com.amazon.tv.leanbacklauncher.util.Preconditions;
import com.amazon.tv.leanbacklauncher.widget.RowViewAdapter;
import com.amazon.tv.tvrecommendations.TvRecommendation;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public abstract class NotificationsViewAdapter<VH extends ViewHolder> extends RowViewAdapter<VH> implements MainActivity.IdleListener {
    static final int UI_STATE_VISIBLE = 1;
    private HomeScreenMessaging mHomeScreenMessaging;
    private final ArrayList<TvRecommendation> mMasterList = new ArrayList<>();
    private PrioritizeRowUpdateState<VH> mPrioritizeRowUpdateState;
    private final Set<TvRecommendation> mRecommendationToBeRemoved = new ArraySet();
    private final ArrayList<TvRecommendation> mSyncedList = new ArrayList();
    private int mUiState = 0;

    static class PrioritizeRowUpdateState<VH extends ViewHolder> {
        private final NotificationsViewAdapter<VH> mAdapter;
        private final Handler mHandler = new Handler();
        private boolean mIsIdle = false;
        private final long mMaxUpdateTime;
        private final long mMinUpdateTime;
        private final Runnable mStateTickRunnable = new Runnable() {
            public void run() {
                PrioritizeRowUpdateState.this.updateTick();
            }
        };

        PrioritizeRowUpdateState(NotificationsViewAdapter<VH> adapter, long minUpdateTime, long maxUpdateTime) {
            this.mAdapter = adapter;
            this.mMinUpdateTime = minUpdateTime;
            this.mMaxUpdateTime = maxUpdateTime;
        }

        private void scheduleUpdateTickIfNeeded() {
            List<TvRecommendation> masterList = this.mAdapter.getMasterList();
            List<TvRecommendation> syncedList = this.mAdapter.getSyncedList();
            this.mHandler.removeCallbacks(this.mStateTickRunnable);
            if (masterList.size() - syncedList.size() > 0) {
                this.mHandler.postDelayed(this.mStateTickRunnable, ((this.mMaxUpdateTime - this.mMinUpdateTime) / ((long) (masterList.size() - syncedList.size()))) + this.mMinUpdateTime);
            }
        }

        private void unScheduleUpdateTick() {
            this.mHandler.removeCallbacks(this.mStateTickRunnable);
        }

        private void postDeletesAndSubstitutes() {
            final List<TvRecommendation> masterList = this.mAdapter.getMasterList();
            final List<TvRecommendation> syncedList = this.mAdapter.getSyncedList();
            DiffResult result = DiffUtil.calculateDiff(new Callback() {
                public int getOldListSize() {
                    return syncedList.size();
                }

                public int getNewListSize() {
                    return masterList.size();
                }

                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return NotificationUtils.equals(syncedList.get(oldItemPosition), masterList.get(newItemPosition));
                }

                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    return Objects.equals(syncedList.get(oldItemPosition), masterList.get(newItemPosition));
                }

                public Object getChangePayload(int oldItemPosition, int newItemPosition) {
                    return masterList.get(newItemPosition);
                }
            }, false);
            List<TvRecommendation> snapshot = new ArrayList(syncedList);
            result.dispatchUpdatesTo(new ListUpdateCallback() {
                public void onInserted(int position, int count) {
                    for (int i = 0; i < count; i++) {
                        syncedList.add(position + i, null);
                    }
                }

                public void onRemoved(int position, int count) {
                    for (int i = 0; i < count; i++) {
                        syncedList.remove(position);
                    }
                }

                public void onMoved(int fromPosition, int toPosition) {
                    throw new IllegalStateException("Should not receive move events");
                }

                public void onChanged(int position, int count, Object payload) {
                    for (int i = 0; i < count; i++) {
                        syncedList.set(position + i, (TvRecommendation) payload);
                    }
                }
            });
            Iterator<TvRecommendation> iterator = syncedList.iterator();
            while (iterator.hasNext()) {
                if (iterator.next() == null) {
                    iterator.remove();
                }
            }
            this.mAdapter.notifyChangesSinceSnapshot(snapshot);
            this.mAdapter.updateRowVisibility();
        }

        private void postOneInsert() {
            List<TvRecommendation> masterList = this.mAdapter.getMasterList();
            List<TvRecommendation> syncedList = this.mAdapter.getSyncedList();
            int i = 0;
            while (i < masterList.size() && i <= syncedList.size()) {
                if (i == syncedList.size() || !Objects.equals(masterList.get(i), syncedList.get(i))) {
                    syncedList.add(i, masterList.get(i));
                    this.mAdapter.notifyItemInserted(this.mAdapter.getNonNotifItemCount() + i);
                    break;
                }
                i++;
            }
            this.mAdapter.updateRowVisibility();
        }

        private void updateTick() {
            postOneInsert();
            scheduleUpdateTickIfNeeded();
        }

        public void onUiVisible() {
            this.mAdapter.postAllRowUpdates();
        }

        public void onUiInvisible() {
            unScheduleUpdateTick();
        }

        public void onNewRowChange() {
            if (this.mAdapter.getUiState() != 1) {
                return;
            }
            if (this.mIsIdle) {
                this.mAdapter.postAllRowUpdates();
                return;
            }
            postDeletesAndSubstitutes();
            scheduleUpdateTickIfNeeded();
        }

        public void onIdleStateChange(boolean isIdle) {
            this.mIsIdle = isIdle;
            if (isIdle) {
                this.mAdapter.postAllRowUpdates();
                unScheduleUpdateTick();
            }
        }
    }

    protected abstract boolean isPartnerClient();

    protected abstract void onRecommendationDismissed(TvRecommendation tvRecommendation);

    NotificationsViewAdapter(Context context, long minUpdateTime, long maxUpdateTime) {
        super(context);
        if (!isPartnerClient()) {
            this.mPrioritizeRowUpdateState = new PrioritizeRowUpdateState(this, minUpdateTime, maxUpdateTime);
        }
    }

    protected void onClearRecommendations(int reason) {
        if (this.mHomeScreenMessaging != null) {
            this.mHomeScreenMessaging.onClearRecommendations(reason);
        }
    }

    public final int getItemCount() {
        return this.mSyncedList.size() + getNonNotifItemCount();
    }

    public void onIdleStateChange(boolean isIdle) {
        if (!isPartnerClient()) {
            this.mPrioritizeRowUpdateState.onIdleStateChange(isIdle);
        }
    }

    public void onVisibilityChange(boolean isVisible) {
    }

    protected final TvRecommendation getRecommendation(int position) {
        return this.mSyncedList.get(position - getNonNotifItemCount());
    }

    private void purgeDismissedNotifications() {
        for (TvRecommendation rec : this.mRecommendationToBeRemoved) {
            removeRecommendation(rec);
            onRecommendationDismissed(rec);
        }
        this.mRecommendationToBeRemoved.clear();
    }

    protected final void dismissNotification(TvRecommendation rec) {
        this.mRecommendationToBeRemoved.add(rec);
    }

    public final void setNotificationRowViewFlipper(HomeScreenMessaging homeScreenMessaging) {
        this.mHomeScreenMessaging = Preconditions.checkNotNull(homeScreenMessaging);
    }

    private void updateRowVisibility() {
        if (this.mHomeScreenMessaging != null) {
            this.mHomeScreenMessaging.recommendationsUpdated(this.mSyncedList.size() > 0);
        }
    }

    public final void onUiVisible() {
        this.mUiState = 1;
        if (isPartnerClient()) {
            postAllRowUpdates();
        } else {
            this.mPrioritizeRowUpdateState.onUiVisible();
        }
    }

    public final void onUiInvisible() {
        this.mUiState = 2;
        purgeDismissedNotifications();
        if (!isPartnerClient()) {
            this.mPrioritizeRowUpdateState.onUiInvisible();
        }
    }

    public boolean isUiVisible() {
        return this.mUiState == 1;
    }

    int getUiState() {
        return this.mUiState;
    }

    void notifyChangesSinceSnapshot(final List<TvRecommendation> snapshot) {
        DiffResult diffResult = DiffUtil.calculateDiff(new Callback() {
            public int getOldListSize() {
                return snapshot.size();
            }

            public int getNewListSize() {
                return NotificationsViewAdapter.this.mSyncedList.size();
            }

            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return NotificationUtils.equals(snapshot.get(oldItemPosition), NotificationsViewAdapter.this.mSyncedList.get(newItemPosition));
            }

            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                return Objects.equals(snapshot.get(oldItemPosition), NotificationsViewAdapter.this.mSyncedList.get(newItemPosition));
            }
        });
        final int offset = getNonNotifItemCount();
        diffResult.dispatchUpdatesTo(new ListUpdateCallback() {
            public void onInserted(int position, int count) {
                NotificationsViewAdapter.this.notifyItemRangeInserted(offset + position, count);
            }

            public void onRemoved(int position, int count) {
                NotificationsViewAdapter.this.notifyItemRangeRemoved(offset + position, count);
            }

            public void onMoved(int fromPosition, int toPosition) {
                NotificationsViewAdapter.this.notifyItemMoved(offset + fromPosition, toPosition);
            }

            public void onChanged(int position, int count, Object payload) {
                NotificationsViewAdapter.this.notifyItemRangeChanged(offset + position, count, payload);
            }
        });
    }

    void postAllRowUpdates() {
        List<TvRecommendation> snapshot = new ArrayList<>(this.mSyncedList);
        this.mSyncedList.clear();
        this.mSyncedList.addAll(this.mMasterList);
        notifyChangesSinceSnapshot(snapshot);
        updateRowVisibility();
    }

    private void masterListHasChanged() {
        if (isPartnerClient()) {
            switch (this.mUiState) {
                case 0:
                    postAllRowUpdates();
                    return;
                case 1:
                    postAllRowUpdates();
                    return;
                default:
                    return;
            }
        }
        switch (this.mUiState) {
            case 0:
                postAllRowUpdates();
                return;
            case 1:
            case 2:
                this.mPrioritizeRowUpdateState.onNewRowChange();
                return;
            default:
                return;
        }
    }

    List<TvRecommendation> getSyncedList() {
        return this.mSyncedList;
    }

    List<TvRecommendation> getMasterList() {
        return this.mMasterList;
    }

    protected final void onRecommendationsUpdate() {
        masterListHasChanged();
    }

    private int indexOfMasterRecommendation(TvRecommendation rec) {
        for (int i = 0; i < this.mMasterList.size(); i++) {
            if (NotificationUtils.equals(rec, this.mMasterList.get(i))) {
                return i;
            }
        }
        return -1;
    }

    private void removeRecommendation(TvRecommendation rec) {
        int index = indexOfMasterRecommendation(rec);
        if (index != -1) {
            this.mMasterList.remove(index);
            masterListHasChanged();
            if (Log.isLoggable("NotifViewAdapter", 3)) {
                Log.d("NotifViewAdapter", "Recommendation Removed from position" + (index + getNonNotifItemCount()) + ": " + rec);
            }
        }
    }

    protected int getNonNotifItemCount() {
        return 0;
    }

    protected void notifyNonNotifItemChanged(int position) {
        super.notifyItemChanged(position);
    }

    protected void notifyNonNotifItemRemoved(int position) {
        super.notifyItemRemoved(position);
    }

    protected void notifyNonNotifItemInserted(int position) {
        super.notifyItemInserted(position);
    }

    public void dump(String prefix, PrintWriter writer) {
        writer.println(prefix + getClass().getName());
        prefix = prefix + "  ";
        writer.println(prefix + "mMasterList: " + Arrays.toString(this.mMasterList.toArray()));
        writer.println(prefix + "mSyncedList: " + Arrays.toString(this.mSyncedList.toArray()));
    }
}
