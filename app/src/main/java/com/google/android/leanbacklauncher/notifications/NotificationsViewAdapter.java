package com.google.android.leanbacklauncher.notifications;

import android.content.Context;
import android.os.Handler;
import android.support.v7.recyclerview.R.styleable;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import com.google.android.leanbacklauncher.MainActivity.IdleListener;
import com.google.android.leanbacklauncher.notifications.StringDifference.EditItem;
import com.google.android.leanbacklauncher.notifications.StringDifference.EditItem.Op;
import com.google.android.leanbacklauncher.notifications.StringDifference.ExtractDeleteAndUpdateResult;
import com.google.android.leanbacklauncher.util.Preconditions;
import com.google.android.leanbacklauncher.widget.RowViewAdapter;
import com.google.android.leanbacklauncher.tvrecommendations.TvRecommendation;
import java.util.ArrayList;
import java.util.LinkedHashSet;

public abstract class NotificationsViewAdapter<VH extends ViewHolder> extends RowViewAdapter<VH> implements IdleListener {
    private static  /* synthetic */ int[] f8xf9b83482 = null;
    private final Handler mHandler;
    private HomeScreenMessaging mHomeScreenMessaging;
    protected ArrayList<TvRecommendation> mMasterList;
    private PrioritizeRowUpdateState mPrioritizeRowUpdateState;
    private final LinkedHashSet<TvRecommendation> mRecommendationToBeRemoved;
    private boolean mRecommendationsDisabled;
    private final Runnable mStateTickRunnable;
    private final ArrayList<TvRecommendation> mSyncedList;
    private int mUiState;

    /* renamed from: com.google.android.leanbacklauncher.notifications.NotificationsViewAdapter.1 */
    class C01951 implements Runnable {
        C01951() {
        }

        public void run() {
            NotificationsViewAdapter.this.mPrioritizeRowUpdateState.updateTick();
        }
    }

    private final class PrioritizeRowUpdateState {
        private int mAccumulatedChanges;
        private boolean mIsIdle;

        private PrioritizeRowUpdateState() {
            this.mAccumulatedChanges = 0;
            this.mIsIdle = false;
        }

        private void scheduleUpdateTickIfNeeded() {
            NotificationsViewAdapter.this.mHandler.removeCallbacks(NotificationsViewAdapter.this.mStateTickRunnable);
            if (this.mAccumulatedChanges > 0) {
                NotificationsViewAdapter.this.mHandler.postDelayed(NotificationsViewAdapter.this.mStateTickRunnable, (long) ((300000 / this.mAccumulatedChanges) + 300000));
            }
        }

        private void unScheduleUpdateTick() {
            NotificationsViewAdapter.this.mHandler.removeCallbacks(NotificationsViewAdapter.this.mStateTickRunnable);
        }

        private void postDeletesAndSubstitutes() {
            ExtractDeleteAndUpdateResult extractDeleteAndUpdateResult = StringDifference.extractDeleteAndUpdateItems(StringDifference.calculateStringAlignment(NotificationsViewAdapter.this.mMasterList, NotificationsViewAdapter.this.mSyncedList), NotificationsViewAdapter.this.mSyncedList);
            NotificationsViewAdapter.this.applyEditList(extractDeleteAndUpdateResult.mItems, NotificationsViewAdapter.this.mSyncedList);
            this.mAccumulatedChanges = extractDeleteAndUpdateResult.mRemainingEditItems;
            NotificationsViewAdapter.this.updateRowVisibility();
        }

        private void postOneUpdate() {
            ArrayList<EditItem> editItems = StringDifference.calculateStringAlignment(NotificationsViewAdapter.this.mMasterList, NotificationsViewAdapter.this.mSyncedList);
            if (editItems.size() > 0) {
                ArrayList<EditItem> items = new ArrayList();
                items.add((EditItem) editItems.get(0));
                NotificationsViewAdapter.this.applyEditList(items, NotificationsViewAdapter.this.mSyncedList);
                this.mAccumulatedChanges = editItems.size() - 1;
            } else {
                this.mAccumulatedChanges = 0;
            }
            NotificationsViewAdapter.this.updateRowVisibility();
        }

        public void updateTick() {
            postOneUpdate();
            if (this.mAccumulatedChanges > 0) {
                scheduleUpdateTickIfNeeded();
            }
        }

        void onUiVisible() {
            NotificationsViewAdapter.this.postAllRowUpdates();
            this.mAccumulatedChanges = 0;
        }

        void onUiInVisible() {
            unScheduleUpdateTick();
        }

        void onNewRowChange() {
            if (NotificationsViewAdapter.this.mUiState != 1) {
                this.mAccumulatedChanges++;
            } else if (this.mIsIdle) {
                NotificationsViewAdapter.this.postAllRowUpdates();
                this.mAccumulatedChanges = 0;
            } else {
                postDeletesAndSubstitutes();
                scheduleUpdateTickIfNeeded();
            }
        }

        void onIdleStateChange(boolean isIdle) {
            this.mIsIdle = isIdle;
            if (isIdle) {
                NotificationsViewAdapter.this.postAllRowUpdates();
                this.mAccumulatedChanges = 0;
                unScheduleUpdateTick();
            }
        }
    }

    private static /* synthetic */ int[] m1988x4e27365e() {
        if (f8xf9b83482 != null) {
            return f8xf9b83482;
        }
        int[] iArr = new int[Op.values().length];
        try {
            iArr[Op.DELETE.ordinal()] = 1;
        } catch (NoSuchFieldError e) {
        }
        try {
            iArr[Op.INSERT.ordinal()] = 2;
        } catch (NoSuchFieldError e2) {
        }
        try {
            iArr[Op.SUB.ordinal()] = 3;
        } catch (NoSuchFieldError e3) {
        }
        try {
            iArr[Op.UPDATE.ordinal()] = 4;
        } catch (NoSuchFieldError e4) {
        }
        f8xf9b83482 = iArr;
        return iArr;
    }

    protected abstract boolean isPartnerClient();

    protected abstract void onRecommendationDismissed(TvRecommendation tvRecommendation);

    NotificationsViewAdapter(Context context) {
        super(context);
        this.mUiState = 0;
        this.mHandler = new Handler();
        this.mRecommendationToBeRemoved = new LinkedHashSet();
        this.mSyncedList = new ArrayList();
        this.mMasterList = new ArrayList();
        this.mStateTickRunnable = new C01951();
        if (!isPartnerClient()) {
            this.mPrioritizeRowUpdateState = new PrioritizeRowUpdateState();
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
        return (TvRecommendation) this.mSyncedList.get(position - getNonNotifItemCount());
    }

    protected final void purgeDismissedNotifications() {
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
        this.mHomeScreenMessaging = (HomeScreenMessaging) Preconditions.checkNotNull(homeScreenMessaging);
    }

    protected void updateRowVisibility() {
        boolean z = false;
        if (this.mHomeScreenMessaging != null) {
            HomeScreenMessaging homeScreenMessaging = this.mHomeScreenMessaging;
            if (this.mSyncedList.size() > 0) {
                z = true;
            }
            homeScreenMessaging.recommendationsUpdated(z);
        }
    }

    public final void onUiVisible() {
        if (Log.isLoggable("ViewAdapter", 3)) {
            Log.d("ViewAdapter", "onUiVisible()");
        }
        this.mUiState = 1;
        if (isPartnerClient()) {
            postAllRowUpdates();
        } else {
            this.mPrioritizeRowUpdateState.onUiVisible();
        }
    }

    public final void onUiInvisible() {
        if (Log.isLoggable("ViewAdapter", 3)) {
            Log.d("ViewAdapter", "onUiInvisible()");
        }
        this.mUiState = 2;
        purgeDismissedNotifications();
        if (!isPartnerClient()) {
            this.mPrioritizeRowUpdateState.onUiInVisible();
        }
    }

    private void applyEditList(ArrayList<EditItem> editList, ArrayList<TvRecommendation> target) {
        int offset = getNonNotifItemCount();
        for (EditItem i : editList) {
            int n = i.mSrcIndex;
            switch (m1988x4e27365e()[i.mOp.ordinal()]) {
                case android.support.v7.recyclerview.R.styleable.RecyclerView_android_descendantFocusability /*1*/:
                    if (n < target.size()) {
                        target.remove(n);
                        super.notifyItemRemoved(n + offset);
                        break;
                    }
                    Log.d("CMD", "NotificationsViewAdapter::applyEditList  fail d");
                    return;
                case android.support.v7.recyclerview.R.styleable.RecyclerView_layoutManager /*2*/:
                    if (n <= target.size()) {
                        target.add(n, i.mItem);
                        super.notifyItemInserted(n + offset);
                        break;
                    }
                    Log.d("CMD", "NotificationsViewAdapter::applyEditList  fail i");
                    return;
                case android.support.v7.preference.R.styleable.Preference_android_layout /*3*/:
                case android.support.v7.preference.R.styleable.Preference_android_title /*4*/:
                    if (n < target.size()) {
                        target.set(n, i.mItem);
                        super.notifyItemChanged(n + offset);
                        break;
                    }
                    Log.d("CMD", "NotificationsViewAdapter::applyEditList  fail su");
                    return;
                default:
                    break;
            }
        }
    }

    private void postAllRowUpdates() {
        applyEditList(StringDifference.calculateStringAlignment(this.mMasterList, this.mSyncedList), this.mSyncedList);
        updateRowVisibility();
    }

    private void masterListHasChanged() {
        if (isPartnerClient()) {
            switch (this.mUiState) {
                case android.support.v7.preference.R.styleable.Preference_android_icon /*0*/:
                    postAllRowUpdates();
                    return;
                case android.support.v7.recyclerview.R.styleable.RecyclerView_android_descendantFocusability /*1*/:
                    postAllRowUpdates();
                    return;
                default:
                    return;
            }
        }
        switch (this.mUiState) {
            case android.support.v7.preference.R.styleable.Preference_android_icon /*0*/:
                postAllRowUpdates();
            case android.support.v7.recyclerview.R.styleable.RecyclerView_android_descendantFocusability /*1*/:
            case android.support.v7.recyclerview.R.styleable.RecyclerView_layoutManager /*2*/:
                this.mPrioritizeRowUpdateState.onNewRowChange();
            default:
        }
    }

    protected void setAllRecommendationsDisabled(boolean allDisabled) {
        this.mRecommendationsDisabled = allDisabled;
    }

    protected final void onRecommendationsUpdate() {
        masterListHasChanged();
    }

    private int indexOfMasterRecommendation(TvRecommendation rec) {
        for (int i = 0; i < this.mMasterList.size(); i++) {
            if (NotificationUtils.equals(rec, (TvRecommendation) this.mMasterList.get(i))) {
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
            if (Log.isLoggable("ViewAdapter", 3)) {
                Log.d("ViewAdapter", "Recommendation Removed from postition" + (index + getNonNotifItemCount()) + ": " + rec);
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
}
