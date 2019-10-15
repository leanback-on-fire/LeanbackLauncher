package com.amazon.tv.leanbacklauncher.notifications;

import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;

import com.amazon.tv.leanbacklauncher.recommendations.SwitchingRecommendationsClient;
import com.amazon.tv.leanbacklauncher.trace.AppTrace;
import com.amazon.tv.tvrecommendations.IRecommendationsService;
import com.amazon.tv.tvrecommendations.TvRecommendation;
import com.amazon.tv.tvrecommendations.IRecommendationsClient;

import java.lang.ref.WeakReference;
import java.util.Comparator;
import java.util.List;

public abstract class NotificationsServiceAdapter<VH extends ViewHolder> extends NotificationsViewAdapter<VH> {
    private static HandlerThread sHandlerThread;
    private final Handler mBackgroundHandler;
    protected IRecommendationsService mBoundService;
    private final NotifComparator mComparator = new NotifComparator();
    private AppTrace.TraceTag mNotificationListenerTraceToken;
    private final NotificationsListener mNotificationsListener;
    private final SwitchingRecommendationsClient mRecommendationClient;
    private RecommendationComparator mRecommendationComparator;
    private AppTrace.TraceTag mServiceConnectTraceToken;

    public interface RecommendationComparator extends Comparator<TvRecommendation> {
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

    private static class NotificationsListener extends IRecommendationsClient.Stub {
        private final RecommendationsHandler mHandler;

        public NotificationsListener(RecommendationsHandler handler) {
            this.mHandler = handler;
        }

        public void onServiceStatusChanged(boolean isReady) {
            int i;
            RecommendationsHandler recommendationsHandler = this.mHandler;
            RecommendationsHandler recommendationsHandler2 = this.mHandler;
            if (isReady) {
                i = 1;
            } else {
                i = 0;
            }
            recommendationsHandler.sendMessage(recommendationsHandler2.obtainMessage(4, i, 0));
        }

        public void onClearRecommendations(int reason) {
            this.mHandler.sendMessage(this.mHandler.obtainMessage(2, reason, 0));
        }

        public void onAddRecommendation(TvRecommendation rec) {
            this.mHandler.sendMessage(this.mHandler.obtainMessage(0, rec));
        }

        public void onUpdateRecommendation(TvRecommendation rec) {
            this.mHandler.sendMessage(this.mHandler.obtainMessage(3, rec));
        }

        public void onRemoveRecommendation(TvRecommendation rec) {
            this.mHandler.sendMessage(this.mHandler.obtainMessage(1, rec));
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
            NotificationsServiceAdapter adapter = (NotificationsServiceAdapter) this.mNotificationsServiceAdapter.get();
            if (adapter != null) {
                switch (msg.what) {
                    case 0:
                        adapter.addRecommendation((TvRecommendation) msg.obj);
                        return;
                    case 1:
                        adapter.removeRecommendation((TvRecommendation) msg.obj);
                        return;
                    case 2:
                        adapter.clearRecommendations(msg.arg1);
                        return;
                    case 3:
                        adapter.updateRecommendation((TvRecommendation) msg.obj);
                        return;
                    case 4:
                        adapter.serviceStatusChanged(msg.arg1 != 0);
                        return;
                    default:
                        return;
                }
            }
        }
    }

    protected abstract boolean isPartnerClient();

    public NotificationsServiceAdapter(Context context, long minUpdateTime, long maxUpdateTime) {
        super(context, minUpdateTime, maxUpdateTime);
        synchronized (this) {
            if (sHandlerThread == null) {
                sHandlerThread = new HandlerThread(getClass().getName());
                sHandlerThread.start();
            }
        }
        this.mBackgroundHandler = new Handler(sHandlerThread.getLooper());
        this.mRecommendationComparator = new RecommendationComparator() {
            public int compare(TvRecommendation o1, TvRecommendation o2) {
                if (o1 == null) return 1;
                if (o2 == null) return -1;

                // todo

                if (o1.getContentImage() == null && o2.getContentImage() != null) {
                    return -1;
                } else if (o1.getContentImage() != null && o2.getContentImage() == null) {
                    return 1;
                }

                return Double.compare(o1.getScore(), o2.getScore());
            }
        };
        this.mRecommendationClient = new SwitchingRecommendationsClient(context.getApplicationContext()) {
            protected void onConnected(IRecommendationsService service) {
                NotificationsServiceAdapter.this.onServiceConnected(service);
            }

            protected void onDisconnected() {
                NotificationsServiceAdapter.this.onServiceDisconnected();
            }
        };
        this.mNotificationsListener = new NotificationsListener(new RecommendationsHandler(this));
    }

    protected void onServiceConnected(IRecommendationsService service) {
        if (this.mServiceConnectTraceToken != null) {
            AppTrace.endAsyncSection(this.mServiceConnectTraceToken);
            this.mServiceConnectTraceToken = null;
        }
        this.mBoundService = service;
        this.mBackgroundHandler.post(new Runnable() {
            public void run() {
                IRecommendationsService boundService = NotificationsServiceAdapter.this.mBoundService;
                if (boundService != null) {
                    try {
                        if (NotificationsServiceAdapter.this.isPartnerClient()) {
                            NotificationsServiceAdapter.this.mNotificationListenerTraceToken = AppTrace.beginAsyncSection("registerPartnerRowClient");
                            boundService.registerPartnerRowClient(NotificationsServiceAdapter.this.mNotificationsListener, 1);
                            return;
                        }
                        NotificationsServiceAdapter.this.mNotificationListenerTraceToken = AppTrace.beginAsyncSection("registerRecommendationsClient");
                        boundService.registerRecommendationsClient(NotificationsServiceAdapter.this.mNotificationsListener, 1);
                    } catch (RemoteException e) {
                        Log.e("NotifServiceAdapter", "Exception", e);
                    }
                }
            }
        });
    }

    protected void serviceStatusChanged(boolean isReady) {
        if (Log.isLoggable("NotifServiceAdapter", 3)) {
            Log.d("NotifServiceAdapter", "Notification Service Status changed. Ready = " + isReady);
        }
    }

    protected void onServiceDisconnected() {
        this.mBoundService = null;
    }

    public void onInitUi() {
        if (Log.isLoggable("NotifServiceAdapter", 3)) {
            Log.d("NotifServiceAdapter", "onInitUi()");
        }
        this.mServiceConnectTraceToken = AppTrace.beginAsyncSection("connectToRecService");
        this.mRecommendationClient.connect();
    }

    private String getPackageName(PendingIntent intent) {
        return intent == null ? null : intent.getCreatorPackage();
    }

    protected void onActionRecommendationImpression(final PendingIntent intent, final String group) {
        this.mBackgroundHandler.post(new Runnable() {
            public void run() {
                IRecommendationsService boundService = NotificationsServiceAdapter.this.mBoundService;
                if (boundService != null) {
                    try {
                        boundService.onActionRecommendationImpression(NotificationsServiceAdapter.this.getPackageName(intent), group);
                    } catch (RemoteException e) {
                        Log.e("NotifServiceAdapter", "RemoteException", e);
                    }
                }
            }
        });
    }

    protected void onActionRecommendationClick(final PendingIntent intent, final String group) {
        this.mBackgroundHandler.post(new Runnable() {
            public void run() {
                IRecommendationsService boundService = NotificationsServiceAdapter.this.mBoundService;
                if (boundService != null) {
                    try {
                        boundService.onActionOpenRecommendation(NotificationsServiceAdapter.this.getPackageName(intent), group);
                    } catch (RemoteException e) {
                        Log.e("NotifServiceAdapter", "RemoteException", e);
                    }
                }
            }
        });
    }

    public void reregisterListener() {
        onStopUi();
        onInitUi();
    }

    public void onStopUi() {
        this.mBackgroundHandler.post(new Runnable() {
            public void run() {
                IRecommendationsService boundService = NotificationsServiceAdapter.this.mBoundService;
                if (boundService != null) {
                    try {
                        if (NotificationsServiceAdapter.this.isPartnerClient()) {
                            boundService.unregisterPartnerRowClient(NotificationsServiceAdapter.this.mNotificationsListener);
                        } else {
                            boundService.unregisterRecommendationsClient(NotificationsServiceAdapter.this.mNotificationsListener);
                        }
                    } catch (RemoteException e) {
                        Log.e("NotifServiceAdapter", "Error unregistering notifications client", e);
                    }
                }
                NotificationsServiceAdapter.this.mRecommendationClient.disconnect();
            }
        });
    }

    protected final void onRecommendationDismissed(final TvRecommendation recommendation) {
        this.mBackgroundHandler.post(new Runnable() {
            public void run() {
                IRecommendationsService boundService = NotificationsServiceAdapter.this.mBoundService;
                if (boundService != null) {
                    try {
                        boundService.dismissRecommendation(recommendation.getKey());
                    } catch (RemoteException e) {
                        Log.e("NotifServiceAdapter", "Exception while cancelling notification", e);
                    }
                }
            }
        });
    }

    protected void onNotificationClick(PendingIntent intent, String group) {
    }

    protected final void serviceOnActionOpenLaunchPoint(final String component, final String group) {
        this.mBackgroundHandler.post(new Runnable() {
            public void run() {
                IRecommendationsService boundService = NotificationsServiceAdapter.this.mBoundService;
                if (boundService != null) {
                    try {
                        boundService.onActionOpenLaunchPoint(component, group);
                    } catch (RemoteException e) {
                        Log.e("NotifServiceAdapter", "Exception while cancelling notification", e);
                    }
                }
            }
        });
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
        Bitmap map = rec.getContentImage();
        String pkg = rec.getPackageName();
        // int bytes = map.getByteCount();
    }


    protected void onRecommendationRemoved(TvRecommendation rec) {
    }

    private void insertPartnerRow(TvRecommendation rec) {
        List<TvRecommendation> masterList = getMasterList();
        int position = masterList.size();
        double sortKey = getSortKey(rec);
        for (int i = 0; i < masterList.size(); i++) {
            if (this.mComparator.compare(sortKey, getSortKey((TvRecommendation) masterList.get(i))) < 0) {
                position = i;
                break;
            }
        }
        masterList.add(position, rec);
    }

    private void insertRecommendationsRow(TvRecommendation rec) {
        List<TvRecommendation> masterList = getMasterList();
        int position = masterList.size();
        for (int i = 0; i < masterList.size(); i++) {
            if (this.mRecommendationComparator.compare(masterList.get(i), rec) < 0) {
                position = i;
                break;
            }
        }
        masterList.add(position, rec);
    }

    private void addRecommendation(TvRecommendation rec) {
        if (rec == null) return; // todo

        if (this.mNotificationListenerTraceToken != null) {
            AppTrace.endAsyncSection(this.mNotificationListenerTraceToken);
            this.mNotificationListenerTraceToken = null;
        }
        AppTrace.beginSection("addRecommendation");
        try {
            if (isPartnerClient()) {
                insertPartnerRow(rec);
            } else {
                insertRecommendationsRow(rec);
            }
            onRecommendationsUpdate();
            onNewRecommendation(rec);
        } finally {
            AppTrace.endSection();
        }
    }

    private void removeRecommendation(TvRecommendation rec) {
        if (rec == null) return; // todo

        List<TvRecommendation> masterList = getMasterList();
        boolean found = false;
        int size = masterList.size();
        int index = 0;
        while (index < size) {
            if (NotificationUtils.equals((TvRecommendation) masterList.get(index), rec)) {
                found = true;
                break;
            }
            index++;
        }
        if (found) {
            masterList.remove(index);
            onRecommendationsUpdate();
            onRecommendationRemoved(rec);
        }
    }

    private void clearRecommendations(int reason) {
        getMasterList().clear();
        onRecommendationsUpdate();
        super.onClearRecommendations(reason);
    }

    private void updateRecommendation(TvRecommendation rec) {
        List<TvRecommendation> masterList = getMasterList();
        boolean found = false;
        int size = masterList.size();
        int index = 0;
        while (index < size) {
            if (NotificationUtils.equals((TvRecommendation) masterList.get(index), rec)) {
                found = true;
                break;
            }
            index++;
        }
        if (found) {
            masterList.remove(index);
            if (isPartnerClient()) {
                insertPartnerRow(rec);
            } else {
                insertRecommendationsRow(rec);
            }
            onRecommendationsUpdate();
            return;
        }
        addRecommendation(rec);
    }
}
