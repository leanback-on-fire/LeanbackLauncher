package com.rockchips.android.leanbacklauncher.notifications;

import android.app.PendingIntent;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import com.rockchips.android.leanbacklauncher.recommendations.SwitchingRecommendationsClient;
import com.rockchips.android.leanbacklauncher.tvrecommendations.IRecommendationsService;
import com.rockchips.android.leanbacklauncher.tvrecommendations.TvRecommendation;
import com.rockchips.android.leanbacklauncher.tvrecommendations.IRecommendationsClient;

import java.lang.ref.WeakReference;
import java.util.Comparator;

public abstract class NotificationsServiceAdapter<VH extends ViewHolder> extends NotificationsViewAdapter<VH> {
    protected IRecommendationsService mBoundService;
    private final NotifComparator mComparator;
    private final Handler mHandler;
    protected NotificationsListener mNotificationsListener;
    protected SwitchingRecommendationsClient mRecommendationClient;

    /* renamed from: NotificationsServiceAdapter.1 */
    class C01941 extends SwitchingRecommendationsClient {
        C01941(Context $anonymous0) {
            super($anonymous0);
        }

        protected void onConnected(IRecommendationsService service) {
            NotificationsServiceAdapter.this.onServiceConnected(service);
        }

        protected void onDisconnected() {
            NotificationsServiceAdapter.this.onServiceDisconnected();
        }
    }

    private static class NotifComparator implements Comparator<TvRecommendation> {
        private NotifComparator() {
        }

        public int compare(double lhsSortKey, double rhsSortKey) {
            double sort = lhsSortKey - rhsSortKey;
            if (sort > 0.0d) {
                return 1;
            }
            return sort < 0.0d ? -1 : 0;
        }

        public int compare(TvRecommendation lhs, TvRecommendation rhs) {
            return compare(NotificationsServiceAdapter.getSortKey(lhs), NotificationsServiceAdapter.getSortKey(rhs));
        }
    }

    protected class NotificationsListener extends IRecommendationsClient.Stub {
        protected NotificationsListener() {
        }

        public void onServiceStatusChanged(boolean isReady) {
            int i;
            Handler h1 = NotificationsServiceAdapter.this.mHandler;
            Handler h2 = NotificationsServiceAdapter.this.mHandler;
            if (isReady) {
                i = 1;
            } else {
                i = 0;
            }
            h1.sendMessage(h2.obtainMessage(4, i, 0));
        }

        public void onClearRecommendations(int reason) {
            NotificationsServiceAdapter.this.mHandler.sendMessage(NotificationsServiceAdapter.this.mHandler.obtainMessage(2, reason, 0));
        }

        public void onAddRecommendation(TvRecommendation rec) {
            NotificationsServiceAdapter.this.mHandler.sendMessage(NotificationsServiceAdapter.this.mHandler.obtainMessage(0, rec));
        }

        public void onUpdateRecommendation(TvRecommendation rec) {
            NotificationsServiceAdapter.this.mHandler.sendMessage(NotificationsServiceAdapter.this.mHandler.obtainMessage(3, rec));
        }

        public void onRemoveRecommendation(TvRecommendation rec) {
            NotificationsServiceAdapter.this.mHandler.sendMessage(NotificationsServiceAdapter.this.mHandler.obtainMessage(1, rec));
        }

        public void onRecommendationBatchStart() {
        }

        public void onRecommendationBatchEnd() {
        }
    }

    private static class RecommendationsHandler extends Handler {
        private final WeakReference<NotificationsServiceAdapter> mNotificationsServiceAdapter;

        RecommendationsHandler(NotificationsServiceAdapter adapter) {
            this.mNotificationsServiceAdapter = new WeakReference(adapter);
        }

        public void handleMessage(Message msg) {
            boolean z = false;
            NotificationsServiceAdapter adapter = (NotificationsServiceAdapter) this.mNotificationsServiceAdapter.get();
            if (adapter != null) {
                switch (msg.what) {
                    case android.support.v7.preference.R.styleable.Preference_android_icon /*0*/:
                        adapter.addRecommendation((TvRecommendation) msg.obj);
                    case android.support.v7.recyclerview.R.styleable.RecyclerView_android_descendantFocusability /*1*/:
                        adapter.removeRecommendation((TvRecommendation) msg.obj);
                    case android.support.v7.recyclerview.R.styleable.RecyclerView_layoutManager /*2*/:
                        adapter.clearRecommendations(msg.arg1);
                    case android.support.v7.preference.R.styleable.Preference_android_layout /*3*/:
                        adapter.updateRecommendation((TvRecommendation) msg.obj);
                    case android.support.v7.preference.R.styleable.Preference_android_title /*4*/:
                        if (msg.arg1 != 0) {
                            z = true;
                        }
                        adapter.serviceStatusChanged(z);
                    default:
                }
            }
        }
    }

    protected abstract boolean isPartnerClient();

    protected void onServiceConnected(IRecommendationsService service) {
        this.mBoundService = service;
        try {
            if (isPartnerClient()) {
                this.mBoundService.registerPartnerRowClient(this.mNotificationsListener, 1);
            } else {
                this.mBoundService.registerRecommendationsClient(this.mNotificationsListener, 1);
            }
        } catch (RemoteException e) {
            Log.e("NotificationsServiceAr", "Exception", e);
        }
    }

    protected void serviceStatusChanged(boolean isReady) {
        if (Log.isLoggable("NotificationsServiceAr", 3)) {
            Log.d("NotificationsServiceAr", "Notification Service Status changed. Ready = " + isReady);
        }
    }

    protected void onServiceDisconnected() {
        this.mBoundService = null;
    }

    public void onInitUi() {
        if (Log.isLoggable("NotificationsServiceAr", 3)) {
            Log.d("NotificationsServiceAr", "onInitUi()");
        }
        this.mRecommendationClient.connect();
    }

    private String getPackageName(PendingIntent intent) {
        return intent == null ? null : intent.getCreatorPackage();
    }

    protected void onActionRecommendationImpression(PendingIntent intent, String group) {
        if (this.mBoundService != null) {
            try {
                this.mBoundService.onActionRecommendationImpression(getPackageName(intent), group);
            } catch (RemoteException e) {
                Log.e("NotificationsServiceAr", "RemoteException", e);
            }
        }
    }

    protected void onActionRecommendationClick(PendingIntent intent, String group) {
        if (this.mBoundService != null) {
            try {
                this.mBoundService.onActionOpenRecommendation(getPackageName(intent), group);
            } catch (RemoteException e) {
                Log.e("NotificationsServiceAr", "RemoteException", e);
            }
        }
    }

    public void reregisterListener() {
        onStopUi();
        onInitUi();
    }

    public void onStopUi() {
        if (this.mBoundService != null) {
            try {
                if (isPartnerClient()) {
                    this.mBoundService.unregisterPartnerRowClient(this.mNotificationsListener);
                } else {
                    this.mBoundService.unregisterRecommendationsClient(this.mNotificationsListener);
                }
            } catch (RemoteException e) {
                Log.e("NotificationsServiceAr", "Error unregistering notifications client", e);
            }
        }
        this.mRecommendationClient.disconnect();
    }

    public NotificationsServiceAdapter(Context context) {
        super(context);
        this.mHandler = new RecommendationsHandler(this);
        this.mComparator = new NotifComparator();
        this.mRecommendationClient = new C01941(context);
        this.mNotificationsListener = new NotificationsListener();
    }

    protected final void onRecommendationDismissed(TvRecommendation recommendation) {
        if (this.mBoundService != null) {
            try {
                this.mBoundService.dismissRecommendation(recommendation.getKey());
            } catch (RemoteException e) {
                Log.e("NotificationsServiceAr", "Exception while cancelling notification", e);
            }
        }
    }

    protected final void serviceOnActionOpenLaunchPoint(String component, String group) {
        if (this.mBoundService != null) {
            try {
                this.mBoundService.onActionOpenLaunchPoint(component, group);
            } catch (RemoteException e) {
                Log.e("NotificationsServiceAr", "Exception while cancelling notification", e);
            }
        }
    }

    private static double getSortKey(TvRecommendation rec) {
        double value = -1.0d;
        try {
            return Double.valueOf(rec.getSortKey()).doubleValue();
        } catch (NullPointerException e) {
            return value;
        } catch (NumberFormatException e2) {
            return value;
        }
    }

    protected void onNewRecommendation(TvRecommendation rec) {
    }

    protected void onRecommendationRemoved(TvRecommendation rec) {
    }

    private void insertPartnerRow(TvRecommendation rec) {
        synchronized (this.mMasterList) {
            int position = this.mMasterList.size();
            double sortKey = getSortKey(rec);
            for (int i = 0; i < this.mMasterList.size(); i++) {
                if (this.mComparator.compare(sortKey, getSortKey((TvRecommendation) this.mMasterList.get(i))) < 0) {
                    position = i;
                    break;
                }
            }
            this.mMasterList.add(position, rec);
        }
    }

    private void insertRecommendationsRow(TvRecommendation rec) {
        int position = this.mMasterList.size();
        double score = rec.getScore();
        for (int i = 0; i < this.mMasterList.size(); i++) {
            if (((TvRecommendation) this.mMasterList.get(i)).getScore() < score) {
                position = i;
                break;
            }
        }
        this.mMasterList.add(position, rec);
    }

    private void addRecommendation(TvRecommendation rec) {
        if (isPartnerClient()) {
            insertPartnerRow(rec);
        } else {
            insertRecommendationsRow(rec);
        }
        onRecommendationsUpdate();
        onNewRecommendation(rec);
    }

    private void removeRecommendation(TvRecommendation rec) {
        boolean found = false;
        int size = this.mMasterList.size();
        int index = 0;
        while (index < size) {
            if (NotificationUtils.equals((TvRecommendation) this.mMasterList.get(index), rec)) {
                found = true;
                break;
            }
            index++;
        }
        if (found) {
            this.mMasterList.remove(index);
            onRecommendationsUpdate();
            onRecommendationRemoved(rec);
        }
    }

    private void clearRecommendations(int reason) {
        setAllRecommendationsDisabled(reason == 2);
        this.mMasterList.clear();
        onRecommendationsUpdate();
        super.onClearRecommendations(reason);
    }

    private void updateRecommendation(TvRecommendation rec) {
        boolean found = false;
        int size = this.mMasterList.size();
        int index = 0;
        while (index < size) {
            if (NotificationUtils.equals((TvRecommendation) this.mMasterList.get(index), rec)) {
                found = true;
                break;
            }
            index++;
        }
        if (found) {
            this.mMasterList.remove(index);
            if (isPartnerClient()) {
                insertPartnerRow(rec);
            } else {
                insertRecommendationsRow(rec);
            }
            onRecommendationsUpdate();
        }
    }
}
