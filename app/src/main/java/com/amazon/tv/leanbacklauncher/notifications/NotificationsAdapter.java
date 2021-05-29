package com.amazon.tv.leanbacklauncher.notifications;

import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;

import com.amazon.tv.leanbacklauncher.BuildConfig;
import com.amazon.tv.leanbacklauncher.LauncherViewHolder;
import com.amazon.tv.leanbacklauncher.MainActivity.IdleListener;
import com.amazon.tv.leanbacklauncher.OpaqueBitmapTransformation;
import com.amazon.tv.leanbacklauncher.R;
import com.amazon.tv.leanbacklauncher.apps.AppsAdapter.ActionOpenLaunchPointListener;
import com.amazon.tv.leanbacklauncher.capabilities.LauncherConfiguration;
import com.amazon.tv.leanbacklauncher.core.LaunchException;
import com.amazon.tv.leanbacklauncher.trace.AppTrace;
import com.amazon.tv.leanbacklauncher.trace.AppTrace.TraceTag;
import com.amazon.tv.leanbacklauncher.util.Util;
import com.amazon.tv.tvrecommendations.IRecommendationsService;
import com.amazon.tv.tvrecommendations.TvRecommendation;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.lang.ref.WeakReference;
import java.util.LinkedList;

public class NotificationsAdapter extends NotificationsServiceAdapter<NotificationsAdapter.NotifViewHolder> implements IdleListener, ActionOpenLaunchPointListener {
    private static final String TAG = (BuildConfig.DEBUG) ? "*" + "NotificationsAdapter" : "NotificationsAdapter";
    private final CardUpdateController mCardUpdateController = new CardUpdateController();
    private final RequestOptions mGlideOptions;
    private final RequestManager mGlideRequestManager;
    // private boolean mHasNowPlayingCard;
    private final int mImpressionDelay;
    private final Handler mImpressionHandler = new ImpressionHandler(this);
    private final LayoutInflater mInflater;
    private boolean mIsIdle;
    private final boolean mLegacyRecommendationLayoutSupported;
    // private final NowPlayCardListener mNowPlayCardListener;
    //private NowPlayingCardData mNowPlayingData;
    //private long mNowPlayingPosMs;
    //private long mNowPlayingPosUpdateMs;
    //private int mNowPlayingState;
    private final RecommendationsHandler mRecommendationsHandler = new RecommendationsHandler(this);
    private final boolean mRichRecommendationViewSupported;

    private static class CardUpdateController {
        private boolean mIsConnected = false;
        private final LinkedList<NotifViewHolder> mWaitingConnectionNofification = new LinkedList();

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
            boolean z = true;
            synchronized (this) {
                if (this.mIsConnected) {
                    z = false;
                } else {
                    if (notifViewHolder.mQueuedState == 0) {
                        this.mWaitingConnectionNofification.add(notifViewHolder);
                        notifViewHolder.mQueuedState = 1;
                    }
                }
            }
            return z;
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

    private static class ImpressionHandler extends Handler {
        private final WeakReference<NotificationsAdapter> mAdapterRef;

        public ImpressionHandler(NotificationsAdapter adapter) {
            this.mAdapterRef = new WeakReference(adapter);
        }

        public void handleMessage(Message msg) {
            NotificationsAdapter adapter = this.mAdapterRef.get();
            if (adapter != null && msg.what == 11 && !adapter.mIsIdle) {
                NotifViewHolder holder = (NotifViewHolder) msg.obj;
                PendingIntent intent = holder.getPendingIntent();
                if (intent != null) {
                    adapter.onActionRecommendationImpression(intent, holder.getGroup());
                }
            }
        }
    }

    static class NotifViewHolder extends LauncherViewHolder {
        FetchImageTask mImageTask;
        int mQueuedState = 0;
        TvRecommendation mRecommendation;
        RecView mRecView;
        boolean mUseGlide;
        final /* synthetic */ NotificationsAdapter adapter;

        private class FetchImageTask extends AsyncTask<String, Void, Bitmap> {
            private final TraceTag mTraceTag;

            public FetchImageTask(TraceTag traceTag) {
                this.mTraceTag = traceTag;
            }

            protected Bitmap doInBackground(String... params) {
                String notifKey = params[0];
                if (!NotifViewHolder.this.adapter.mCardUpdateController.queueImageFetchIfDisconnected(NotifViewHolder.this)) {
                    Bitmap img = null;
                    try {
                        IRecommendationsService boundService = NotifViewHolder.this.adapter.mBoundService;
                        if (boundService != null) {
                            img = boundService.getImageForRecommendation(notifKey);
                        }
                        if (img != null) {
                            return img;
                        }
                        NotifViewHolder.this.adapter.mCardUpdateController.queueImageFetchIfDisconnected(NotifViewHolder.this);
                        return img;
                    } catch (RemoteException e) {
                        Log.e(TAG, "Exception while fetching card image", e);
                    }
                }
                return null;
            }

            protected void onPostExecute(Bitmap image) {
                if (!isCancelled()) {
                    if (image != null) {
                        NotifViewHolder.this.mRecView.setMainImage(new BitmapDrawable(NotifViewHolder.this.adapter.mContext.getResources(), image));
                    }
                    NotifViewHolder.this.mImageTask = null;
                    AppTrace.endAsyncSection(this.mTraceTag);
                }
            }
        }

        public NotifViewHolder(NotificationsAdapter adapter, View v) {
            super(v);

            boolean z = false;
            this.adapter = adapter;
            if (v instanceof RecView) {
                this.mRecView = (RecView) v;
                if (!(this.mRecView instanceof CaptivePortalNotificationCardView)) {
                    z = true;
                }
                this.mUseGlide = z;
            }
        }

        void init(TvRecommendation rec) {
            this.itemView.setVisibility(View.VISIBLE);
            boolean refreshSameContent = NotificationUtils.equals(rec, this.mRecommendation);
            if (this.mRecView instanceof CaptivePortalNotificationCardView) {
                ((CaptivePortalNotificationCardView) this.mRecView).setRecommendation(rec, !refreshSameContent);
            } else {
                ((RecCardView) this.mRecView).setRecommendation(rec, !refreshSameContent);
            }
            this.mRecommendation = rec;
            setLaunchColor(this.mRecView.getLaunchAnimationColor());
            this.mQueuedState = 0;
            if (this.mUseGlide) {
                if (BuildConfig.DEBUG) Log.d(TAG, "Use glide for image");
                this.mRecView.setUseBackground(false);
                this.mRecView.onStartImageFetch();
                this.adapter.mGlideRequestManager
                        .asBitmap()
                        .load(new RecImageKey(this.mRecommendation))
                        .apply(this.adapter.mGlideOptions)
                        .into(this.mRecView);
//                this.adapter.mGlideRequestManager
//                        .asBitmap()
//                        .load(new RecImageKey(this.mRecommendation))
//                        .apply(this.adapter.mGlideOptions)
//                        .into(this.mRecView);
                return;
            }
            this.mRecView.setUseBackground(true);
            Bitmap contentImage = rec.getContentImage();
            if (contentImage != null) {
                this.mRecView.setMainImage(new BitmapDrawable(this.adapter.mContext.getResources(), contentImage));
            }
            if (!this.adapter.mCardUpdateController.queueImageFetchIfDisconnected(this)) {
                executeImageTask();
            }
        }

        private void executeImageTask() {
            if (!this.mUseGlide) {
                TraceTag traceTag = AppTrace.beginAsyncSection("RecImageFetch");
                if (!(this.mImageTask == null || this.mImageTask.isCancelled())) {
                    this.mImageTask.cancel(true);
                }
                this.mImageTask = new FetchImageTask(traceTag);
                this.mImageTask.execute(this.mRecommendation.getKey());
            }
        }

        public PendingIntent getPendingIntent() {
            return this.mRecommendation != null ? this.mRecommendation.getContentIntent() : null;
        }

        public String getGroup() {
            return this.mRecommendation != null ? this.mRecommendation.getGroup() : null;
        }

        protected void performLaunch() {
            PendingIntent intent = getPendingIntent();
            if (intent != null) {
                try {
                    Util.startActivity(this.adapter.mContext, intent);
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
            this.adapter.onNotificationClick(intent, getGroup());
            if (this.mRecommendation != null && this.mRecommendation.isAutoDismiss()) {
                this.adapter.dismissNotification(this.mRecommendation);
            }
        }
    }

    /*private class NowPlayingViewHolder extends NotifViewHolder {
        NowPlayingCardView mNowPlayingCard;

        public NowPlayingViewHolder(View v) {
            super(NotificationsAdapter.this, v);
            this.mNowPlayingCard = (NowPlayingCardView) v;
        }

        public void init(NowPlayingCardData mediaData) {
            this.itemView.setVisibility(0);
            if (this.mNowPlayingCard != null) {
                this.mNowPlayingCard.setNowPlayingContent(mediaData);
                if (NotificationsAdapter.this.mBoundService != null) {
                    //   this.mNowPlayingCard.setPlayerState(NotificationsAdapter.this.mNowPlayingState, NotificationsAdapter.this.mNowPlayingPosMs, NotificationsAdapter.this.mNowPlayingPosUpdateMs);
                } else {
                    //  this.mNowPlayingCard.stopSelfUpdate();
                }
                setLaunchColor(this.mNowPlayingCard.getLaunchAnimationColor());
            }
        }

        public PendingIntent getPendingIntent() {
            return this.mNowPlayingCard.getClickedIntent();
        }

        protected void onNotificationClick(PendingIntent intent) {
        }
    }*/

    private static class RecommendationsHandler extends Handler {
        private final WeakReference<NotificationsAdapter> mNotificationsAdapter;

        public RecommendationsHandler(NotificationsAdapter adapter) {
            this.mNotificationsAdapter = new WeakReference(adapter);
        }

        public void handleMessage(Message msg) {
            boolean z = true;
            NotificationsAdapter adapter = this.mNotificationsAdapter.get();
            if (adapter != null) {
                switch (msg.what) {
                    case 6:
                        adapter.remoteControllerClientChanged(msg.arg1 != 0);
                        return;
                    case 7:
                        if (msg.obj != null) {
                            // adapter.remoteControllerMediaDataUpdated((NowPlayingCardData) msg.obj);
                            return;
                        }
                        return;
                    case 8:
                        if (msg.obj != null) {
                            long[] values = (long[]) msg.obj;
                            adapter.remoteControllerClientPlaybackStateUpdate((int) values[0], values[1], values[2]);
                            return;
                        }
                        return;
                    case 9:
                        if (msg.arg1 == 0) {
                            z = false;
                        }
                        //    adapter.updateNowPlayingCard(z);
                        return;
                    case 10:
                        // if (adapter.mHasNowPlayingCard) {
                        //     adapter.mHasNowPlayingCard = false;
                        //     adapter.notifyNonNotifItemRemoved(0);
                        //     return;
                        // }
                        return;
                    default:
                        return;
                }
            }
        }
    }

    public NotificationsAdapter(Context context) {
        super(context, 300000, 600000);
        // this.mNowPlayCardListener = new NowPlayCardListener(context);
        this.mImpressionDelay = context.getResources().getInteger(R.integer.impression_delay);
        this.mInflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mRichRecommendationViewSupported = LauncherConfiguration.getInstance().isRichRecommendationViewEnabled();
        this.mLegacyRecommendationLayoutSupported = LauncherConfiguration.getInstance().isLegacyRecommendationLayoutEnabled();
        this.mGlideRequestManager = Glide.with(this.mContext);
        this.mGlideOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .transform(new OpaqueBitmapTransformation(
                        this.mContext, ContextCompat.getColor(context, R.color.notif_background_color))
                );
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

    // public void onMediaDataUpdated(NowPlayingCardData mediaData) {
    //    this.mRecommendationsHandler.sendMessage(this.mRecommendationsHandler.obtainMessage(7, mediaData));
    // }

    public void onClientPlaybackStateUpdate(int state, long stateChangeTimeMs, long currentPosMs) {
        this.mRecommendationsHandler.sendMessage(this.mRecommendationsHandler.obtainMessage(8, new long[]{(long) state, stateChangeTimeMs, currentPosMs}));
    }

    protected boolean isPartnerClient() {
        return false;
    }

    protected void onServiceConnected(IRecommendationsService service) {
        //if (this.mHasNowPlayingCard) {
        //    this.mRecommendationsHandler.sendEmptyMessageDelayed(10, 1500);
        //}
        super.onServiceConnected(service);
        /*try {
            this.mNowPlayCardListener.setRemoteControlListener(this);
        } catch (RemoteException e) {
            Log.e(TAG, "Exception", e);
        }*/
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
        super.onStopUi();
        //  this.mNowPlayCardListener.setRemoteControlListener(null);
        //if (this.mHasNowPlayingCard) {
        //    notifyNonNotifItemChanged(0);
        //}
        super.onStopUi();
    }

    public int getItemViewType(int position) {
        //if (this.mHasNowPlayingCard && position == 0) {
        //    return 1;
        //}
        if (getRecommendation(position) == null || !getRecommendation(position).getPackageName().equals("android")) {
            return 0;
        }
        return 2;
    }

    protected int getNonNotifItemCount() {
        //if (this.mHasNowPlayingCard) {
        //    return 1;
        //}
        return 0;
    }

    public NotifViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        NotifViewHolder notifViewHolder;
        switch (viewType) {
            case 0:
                AppTrace.beginSection("onCreateRecCard");
                try {
                    notifViewHolder = new NotifViewHolder(this, createRecommendationCardView(parent));
                    return notifViewHolder;
                } finally {
                    AppTrace.endSection();
                }
            case 1:
                AppTrace.beginSection("onCreateNowPlayingCard");
                try {
                    //notifViewHolder = new NowPlayingViewHolder(new NowPlayingCardView(parent.getContext()));
                    //  return notifViewHolder;
                } finally {
                    AppTrace.endSection();
                }
            case 2:
                AppTrace.beginSection("onCreateCaptivePortalCard");
                try {
                    notifViewHolder = new NotifViewHolder(this, new CaptivePortalNotificationCardView(parent.getContext()));
                    return notifViewHolder;
                } finally {
                    AppTrace.endSection();
                }
            default:
                Log.e(TAG, "Invalid view type = " + viewType);
                return null;
        }
    }

    protected View createRecommendationCardView(ViewGroup parent) {
        if (!this.mRichRecommendationViewSupported) {
            throw new UnsupportedOperationException("Unsupported device capabilities");
        } else if (this.mLegacyRecommendationLayoutSupported) {
            return this.mInflater.inflate(R.layout.notification_card, parent, false);
        } else {
            return new RecCardView(parent.getContext());
        }
    }

    public void onBindViewHolder(NotifViewHolder holder, int position) {
        if (position < getItemCount()) {
            int type = getItemViewType(position);
            switch (type) {
                case 0:
                case 2:
                    AppTrace.beginSection("onBindRecCard");
                    try {
                        TvRecommendation rec = getRecommendation(position);
                        if (rec != null) {
                            holder.init(rec);
                        }
                        return;
                    } finally {
                        AppTrace.endSection();
                    }
                case 1:
                    AppTrace.beginSection("onBindNowPlayingCard");
                    try {
                        //   if (holder instanceof NowPlayingViewHolder) {
                        // ((NowPlayingViewHolder) holder).init(this.mNowPlayingData);
                        // }
                        AppTrace.endSection();
                        return;
                    } catch (Throwable th) {
                        AppTrace.endSection();
                    }
                default:
                    Log.e(TAG, "Invalid view type = " + type);
                    return;
            }
        }
    }

    public void onViewAttachedToWindow(NotifViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        Message msg = new Message();
        msg.what = 11;
        msg.obj = holder;
        this.mImpressionHandler.sendMessageDelayed(msg, this.mImpressionDelay);
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
        //  if (!this.mHasNowPlayingCard) {
        // this.mHasNowPlayingCard = true;
        // notifyNonNotifItemInserted(0);
        if (refreshExisting) {
            notifyNonNotifItemChanged(0);
        } else {
            notifyNonNotifItemRemoved(0);
            notifyNonNotifItemInserted(0);
        }
    }


    private void remoteControllerClientChanged(boolean clearing) {
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "remoteControllerClientChanged. Clearing= " + clearing);
        }
        this.mRecommendationsHandler.removeMessages(10);
        this.mRecommendationsHandler.removeMessages(9);
        //    if (this.mHasNowPlayingCard && clearing) {
        //        this.mHasNowPlayingCard = false;
        //        notifyNonNotifItemRemoved(0);
        //    }
    }

    /*private void remoteControllerMediaDataUpdated(NowPlayingCardData mediaData) {
        Preconditions.checkNotNull(mediaData);
        if (Log.isLoggable(TAG, 3)) {
            Log.d(TAG, "remoteControllerMediaDataUpdated. mediaData= " + mediaData);
        }
        int refresh = 0;
        // if (this.mNowPlayingData != null && TextUtils.equals(this.mNowPlayingData.playerPackage, mediaData.playerPackage)) {
        //   refresh = 1;
        //  }
        //  this.mNowPlayingData = mediaData;
        this.mRecommendationsHandler.removeMessages(9);
        this.mRecommendationsHandler.sendMessageDelayed(this.mRecommendationsHandler.obtainMessage(9, refresh, 0), 300);
    }*/

    private void remoteControllerClientPlaybackStateUpdate(int state, long stateChangeTimeMs, long currentPosMs) {
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "remoteControllerClientPlaybackStateUpdate. state= " + state);
        }
        //  this.mNowPlayingState = state;
        //  this.mNowPlayingPosMs = currentPosMs;
        //  this.mNowPlayingPosUpdateMs = stateChangeTimeMs;
        //if (this.mHasNowPlayingCard) {
        //    notifyNonNotifItemChanged(0);
        //}
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
