package com.google.android.leanbacklauncher.notifications;

import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.MainThread;
import android.support.v7.preference.R.styleable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.leanbacklauncher.LauncherViewHolder;
import com.google.android.leanbacklauncher.MainActivity.IdleListener;
import com.google.android.leanbacklauncher.R;
import com.google.android.leanbacklauncher.apps.AppsAdapter.ActionOpenLaunchPointListener;
import com.google.android.leanbacklauncher.core.LaunchException;
import com.google.android.leanbacklauncher.notifications.NowPlayCardListener.Listener;
import com.google.android.leanbacklauncher.util.Preconditions;
import com.google.android.leanbacklauncher.util.Util;
import com.google.android.leanbacklauncher.tvrecommendations.IRecommendationsService;
import com.google.android.leanbacklauncher.tvrecommendations.TvRecommendation;
import java.lang.ref.WeakReference;
import java.util.LinkedList;

@MainThread
public class NotificationsAdapter extends NotificationsServiceAdapter<NotificationsAdapter.NotifViewHolder> implements IdleListener, Listener, ActionOpenLaunchPointListener {
    private final CardUpdateController mCardUpdateController;
    private boolean mHasNowPlayingCard;
    private final int mImpressionDelay;
    private Handler mImpressionHandler;
    private boolean mIsIdle;
    private int mNotifCount;
    private NotificationCountListener mNotifCountListener;
    private final NowPlayCardListener mNowPlayCardListener;
    private NowPlayingCardData mNowPlayingData;
    private long mNowPlayingPosMs;
    private long mNowPlayingPosUpdateMs;
    private int mNowPlayingState;
    private final RecommendationsHandler mRecommendationsHandler;

    public interface NotificationCountListener {
        void onNotificationCountUpdated(int i);
    }

    /* renamed from: com.google.android.leanbacklauncher.notifications.NotificationsAdapter.1 */
    class C01931 extends Handler {
        C01931() {
        }

        public void handleMessage(Message msg) {
            if (msg.what == 11 && !NotificationsAdapter.this.mIsIdle) {
                NotifViewHolder holder = (NotificationsAdapter.NotifViewHolder)msg.obj;
                PendingIntent intent = holder.getPendingIntent();
                if (intent != null) {
                    NotificationsAdapter.this.onActionRecommendationImpression(intent, holder.getGroup());
                }
            }
        }
    }

    private static class CardUpdateController {
        private boolean mIsConnected;
        private final LinkedList<NotifViewHolder> mWaitingConnectionNofification;

        public CardUpdateController() {
            this.mWaitingConnectionNofification = new LinkedList();
            this.mIsConnected = false;
        }

        public void onDisconnected() {
            this.mIsConnected = false;
        }

        public void onServiceStatusChanged(boolean isReady) {
            synchronized (this) {
                this.mIsConnected = isReady;
                if (isReady) {
                    for (NotifViewHolder notifViewHolder : this.mWaitingConnectionNofification) {
                        if (notifViewHolder.mQueuedState == 1) {
                            notifViewHolder.executeImageTask();
                            notifViewHolder.mQueuedState = 0;
                        } else {
                            notifViewHolder.mQueuedState = 3;
                        }
                    }
                    this.mWaitingConnectionNofification.clear();
                }
            }
        }

        public boolean queueImageFetchIfDisconnected(NotifViewHolder notifViewHolder) {
            synchronized (this) {
                if (this.mIsConnected) {
                    return false;
                }
                if (notifViewHolder.mQueuedState == 0) {
                    this.mWaitingConnectionNofification.add(notifViewHolder);
                    notifViewHolder.mQueuedState = 1;
                }
                return true;
            }
        }

        public void onViewAttachedToWindow(NotifViewHolder notifViewHolder) {
            synchronized (this) {
                if (this.mIsConnected) {
                    if (notifViewHolder.mQueuedState == 3) {
                        notifViewHolder.executeImageTask();
                        notifViewHolder.mQueuedState = 0;
                    }
                } else if (notifViewHolder.mQueuedState == 2) {
                    notifViewHolder.mQueuedState = 1;
                }
            }
        }

        public void onViewDetachedFromWindow(NotifViewHolder notifViewHolder) {
            synchronized (this) {
                if (!this.mIsConnected && notifViewHolder.mQueuedState == 1) {
                    notifViewHolder.mQueuedState = 2;
                }
            }
        }
    }

    class NotifViewHolder extends LauncherViewHolder {
        FetchImageTask mImageTask;
        NotificationCardView mNotificationCard;
        int mQueuedState;
        TvRecommendation mRecommendation;

        private class FetchImageTask extends AsyncTask<String, Void, Bitmap> {
            private FetchImageTask() {
            }

            protected Bitmap doInBackground(String... params) {
                String notifKey = params[0];
                if (!NotificationsAdapter.this.mCardUpdateController.queueImageFetchIfDisconnected(NotifViewHolder.this)) {
                    Bitmap img = null;
                    try {
                        if (NotificationsAdapter.this.mBoundService != null) {
                            img = NotificationsAdapter.this.mBoundService.getImageForRecommendation(notifKey);
                        }
                        if (img == null) {
                            NotificationsAdapter.this.mCardUpdateController.queueImageFetchIfDisconnected(NotifViewHolder.this);
                        }
                        return img;
                    } catch (RemoteException e) {
                        Log.e("NotificationsAdapter", "Exception while fetching card image", e);
                    }
                }
                return null;
            }

            protected void onPostExecute(Bitmap image) {
                if (!isCancelled()) {
                    if (image != null) {
                        NotifViewHolder.this.mNotificationCard.setMainImage(new BitmapDrawable(NotificationsAdapter.this.mContext.getResources(), image));
                    }
                    NotifViewHolder.this.mImageTask = null;
                }
            }
        }

        public NotifViewHolder(View v) {
            super(v);
            this.mQueuedState = 0;
            if (v instanceof NotificationCardView) {
                this.mNotificationCard = (NotificationCardView) v;
            }
        }

        void init(TvRecommendation rec) {
            this.itemView.setVisibility(0);
            if (this.mNotificationCard != null) {
                this.mNotificationCard.setNotificationContent(rec, !NotificationUtils.equals(rec, this.mRecommendation));
                this.mNotificationCard.resetCardState();
                this.mRecommendation = rec;
                setLaunchColor(this.mNotificationCard.getColor());
                this.mQueuedState = 0;
                if (!NotificationsAdapter.this.mCardUpdateController.queueImageFetchIfDisconnected(this)) {
                    executeImageTask();
                }
            }
        }

        private void executeImageTask() {
            if (!(this.mImageTask == null || this.mImageTask.isCancelled())) {
                this.mImageTask.cancel(true);
            }
            this.mImageTask = new FetchImageTask();
            this.mImageTask.execute(new String[]{this.mRecommendation.getKey()});
        }

        public PendingIntent getPendingIntent() {
            return this.mNotificationCard.getClickedIntent();
        }

        public String getGroup() {
            return this.mNotificationCard.getRecommendationGroup();
        }

        protected void performLaunch() {
            PendingIntent intent = getPendingIntent();
            if (intent != null) {
                try {
                    Util.startActivity(NotificationsAdapter.this.mContext, intent);
                    onLaunchSucceeded();
                    onNotificationClick(intent);
                } catch (Throwable t) {
                    LaunchException launchException = new LaunchException("Could not launch notification intent", t);
                }
            } else {
                throw new LaunchException("No notification intent to launch: " + this.mRecommendation);
            }
        }

        protected void onNotificationClick(PendingIntent intent) {
            NotificationsAdapter.this.onNotificationClick(intent, this.mNotificationCard.getRecommendationGroup());
            if (this.mNotificationCard.isAutoDismiss()) {
                NotificationsAdapter.this.dismissNotification(this.mRecommendation);
            }
        }
    }

    private class NowPlayingViewHolder extends NotifViewHolder {
        NowPlayingCardView mNowPlayingCard;

        public NowPlayingViewHolder(View v) {
            super(v);
            this.mNowPlayingCard = (NowPlayingCardView) v;
        }

        public void init(NowPlayingCardData mediaData) {
            this.itemView.setVisibility(0);
            if (this.mNowPlayingCard != null) {
                this.mNowPlayingCard.setNowPlayingContent(mediaData);
                if (NotificationsAdapter.this.mBoundService != null) {
                    this.mNowPlayingCard.setPlayerState(NotificationsAdapter.this.mNowPlayingState, NotificationsAdapter.this.mNowPlayingPosMs, NotificationsAdapter.this.mNowPlayingPosUpdateMs);
                } else {
                    this.mNowPlayingCard.stopSelfUpdate();
                }
                setLaunchColor(this.mNowPlayingCard.getColor());
            }
        }

        public PendingIntent getPendingIntent() {
            return this.mNowPlayingCard.getClickedIntent();
        }

        protected void onNotificationClick(PendingIntent intent) {
        }
    }

    private static class RecommendationsHandler extends Handler {
        private final WeakReference<NotificationsAdapter> mNotificationsAdapter;

        public RecommendationsHandler(NotificationsAdapter adapter) {
            this.mNotificationsAdapter = new WeakReference(adapter);
        }

        public void handleMessage(Message msg) {
            boolean z = true;
            boolean z2 = false;
            NotificationsAdapter adapter = (NotificationsAdapter) this.mNotificationsAdapter.get();
            if (adapter != null) {
                switch (msg.what) {
                    case android.support.v7.preference.R.styleable.Preference_android_key /*6*/:
                        if (msg.arg1 != 0) {
                            z2 = true;
                        }
                        adapter.remoteControllerClientChanged(z2);
                        break;
                    case android.support.v7.preference.R.styleable.Preference_android_summary /*7*/:
                        if (msg.obj != null) {
                            adapter.remoteControllerMediaDataUpdated((NowPlayingCardData) msg.obj);
                            break;
                        }
                        break;
                    case android.support.v7.preference.R.styleable.Preference_android_order /*8*/:
                        if (msg.obj != null) {
                            long[] values = (long[])msg.obj;
                            adapter.remoteControllerClientPlaybackStateUpdate((int) values[0], values[1], values[2]);
                            break;
                        }
                        break;
                    case android.support.v7.preference.R.styleable.Preference_android_widgetLayout /*9*/:
                        if (msg.arg1 == 0) {
                            z = false;
                        }
                        adapter.updateNowPlayingCard(z);
                        break;
                    case android.support.v7.preference.R.styleable.Preference_android_dependency /*10*/:
                        if (adapter.mHasNowPlayingCard) {
                            adapter.mHasNowPlayingCard = false;
                            adapter.notifyNonNotifItemRemoved(0);
                            break;
                        }
                        break;
                }
            }
        }
    }

    public NotificationsAdapter(Context context) {
        super(context);
        this.mRecommendationsHandler = new RecommendationsHandler(this);
        this.mCardUpdateController = new CardUpdateController();
        this.mImpressionHandler = new C01931();
        this.mNowPlayCardListener = new NowPlayCardListener(context);
        this.mImpressionDelay = context.getResources().getInteger(R.integer.impression_delay);
    }

    public void onActionOpenLaunchPoint(String component, String group) {
        super.serviceOnActionOpenLaunchPoint(component, group);
    }

    public void onClientChanged(boolean clearing) {
        int i;
        RecommendationsHandler recommendationsHandler = this.mRecommendationsHandler;
        RecommendationsHandler recommendationsHandler2 = this.mRecommendationsHandler;
        if (clearing) {
            i = 1;
        } else {
            i = 0;
        }
        recommendationsHandler.sendMessage(recommendationsHandler2.obtainMessage(6, i, 0));
    }

    public void onMediaDataUpdated(NowPlayingCardData mediaData) {
        this.mRecommendationsHandler.sendMessage(this.mRecommendationsHandler.obtainMessage(7, mediaData));
    }

    public void onClientPlaybackStateUpdate(int state, long stateChangeTimeMs, long currentPosMs) {
        this.mRecommendationsHandler.sendMessage(this.mRecommendationsHandler.obtainMessage(8, new long[]{(long) state, stateChangeTimeMs, currentPosMs}));
    }

    protected boolean isPartnerClient() {
        return false;
    }

    protected void onServiceConnected(IRecommendationsService service) {
        if (this.mHasNowPlayingCard) {
            this.mRecommendationsHandler.sendEmptyMessageDelayed(10, 1500);
        }
        super.onServiceConnected(service);
        try {
            this.mNowPlayCardListener.setRemoteControlListener(this);
        } catch (RemoteException e) {
            Log.e("NotificationsAdapter", "Exception", e);
        }
    }

    protected void serviceStatusChanged(boolean isReady) {
        super.serviceStatusChanged(isReady);
        this.mCardUpdateController.onServiceStatusChanged(isReady);
    }

    protected void onServiceDisconnected() {
        super.onServiceDisconnected();
        this.mCardUpdateController.onDisconnected();
    }

    public void onInitUi() {
        super.onInitUi();
    }

    public void onStopUi() {
        this.mCardUpdateController.onDisconnected();
        try {
            super.onStopUi();
            this.mNowPlayCardListener.setRemoteControlListener(null);
        } catch (RemoteException e) {
            Log.e("NotificationsAdapter", "Exception", e);
        }
        if (this.mHasNowPlayingCard) {
            notifyNonNotifItemChanged(0);
        }
        super.onStopUi();
    }

    public void setNotificationCountListener(NotificationCountListener listener) {
        this.mNotifCountListener = listener;
        if (this.mNotifCountListener != null) {
            this.mNotifCountListener.onNotificationCountUpdated(this.mNotifCount);
        }
    }

    public int getItemViewType(int position) {
        if (this.mHasNowPlayingCard && position == 0) {
            return 1;
        }
        return 0;
    }

    protected int getNonNotifItemCount() {
        if (this.mHasNowPlayingCard) {
            return 1;
        }
        return 0;
    }

    public NotifViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) this.mContext.getSystemService("layout_inflater");
        switch (viewType) {
            case android.support.v7.preference.R.styleable.Preference_android_icon /*0*/:
                return new NotifViewHolder(inflater.inflate(R.layout.notification_card, parent, false));
            case android.support.v7.recyclerview.R.styleable.RecyclerView_android_descendantFocusability /*1*/:
                return new NowPlayingViewHolder(inflater.inflate(R.layout.now_playing_card, parent, false));
            default:
                Log.e("NotificationsAdapter", "Invalid view type = " + viewType);
                return null;
        }
    }

    public void onBindViewHolder(NotifViewHolder holder, int position) {
        if (position < getItemCount()) {
            int type = getItemViewType(position);
            switch (type) {
                case android.support.v7.preference.R.styleable.Preference_android_icon /*0*/:
                    holder.init(getRecommendation(position));
                    break;
                case android.support.v7.recyclerview.R.styleable.RecyclerView_android_descendantFocusability /*1*/:
                    if (holder instanceof NowPlayingViewHolder) {
                        ((NowPlayingViewHolder) holder).init(this.mNowPlayingData);
                        break;
                    }
                    break;
                default:
                    Log.e("NotificationsAdapter", "Invalid view type = " + type);
                    break;
            }
        }
    }

    public void onViewAttachedToWindow(NotifViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        Message msg = new Message();
        msg.what = 11;
        msg.obj = holder;
        this.mImpressionHandler.sendMessageDelayed(msg, (long) this.mImpressionDelay);
        this.mCardUpdateController.onViewAttachedToWindow(holder);
    }

    public void onViewDetachedFromWindow(NotifViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        this.mImpressionHandler.removeMessages(11, holder);
        this.mCardUpdateController.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
    }

    protected void onNotificationClick(PendingIntent intent, String group) {
        super.onActionRecommendationClick(intent, group);
    }

    private void updateNowPlayingCard(boolean refreshExisting) {
        if (!this.mHasNowPlayingCard) {
            this.mHasNowPlayingCard = true;
            notifyNonNotifItemInserted(0);
        } else if (refreshExisting) {
            notifyNonNotifItemChanged(0);
        } else {
            notifyNonNotifItemRemoved(0);
            notifyNonNotifItemInserted(0);
        }
    }

    private void remoteControllerClientChanged(boolean clearing) {
        if (Log.isLoggable("NotificationsAdapter", 3)) {
            Log.d("NotificationsAdapter", "remoteControllerClientChanged. Clearing= " + clearing);
        }
        this.mRecommendationsHandler.removeMessages(10);
        this.mRecommendationsHandler.removeMessages(9);
        if (this.mHasNowPlayingCard && clearing) {
            this.mHasNowPlayingCard = false;
            notifyNonNotifItemRemoved(0);
        }
    }

    private void remoteControllerMediaDataUpdated(NowPlayingCardData mediaData) {
        Preconditions.checkNotNull(mediaData);
        if (Log.isLoggable("NotificationsAdapter", 3)) {
            Log.d("NotificationsAdapter", "remoteControllerMediaDataUpdated. mediaData= " + mediaData);
        }
        int refresh = 0;
        if (this.mNowPlayingData != null && TextUtils.equals(this.mNowPlayingData.playerPackage, mediaData.playerPackage)) {
            refresh = 1;
        }
        this.mNowPlayingData = mediaData;
        this.mRecommendationsHandler.removeMessages(9);
        this.mRecommendationsHandler.sendMessageDelayed(this.mRecommendationsHandler.obtainMessage(9, refresh, 0), 300);
    }

    private void remoteControllerClientPlaybackStateUpdate(int state, long stateChangeTimeMs, long currentPosMs) {
        if (Log.isLoggable("NotificationsAdapter", 3)) {
            Log.d("NotificationsAdapter", "remoteControllerClientPlaybackStateUpdate. state= " + state);
        }
        this.mNowPlayingState = state;
        this.mNowPlayingPosMs = currentPosMs;
        this.mNowPlayingPosUpdateMs = stateChangeTimeMs;
        if (this.mHasNowPlayingCard) {
            notifyNonNotifItemChanged(0);
        }
    }

    public void onIdleStateChange(boolean isIdle) {
        this.mIsIdle = isIdle;
        super.onIdleStateChange(isIdle);
    }

    public void onVisibilityChange(boolean isVisible) {
        this.mIsIdle = !isVisible;
        super.onVisibilityChange(isVisible);
    }
}
