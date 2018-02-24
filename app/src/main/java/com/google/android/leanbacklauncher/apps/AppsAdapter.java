package com.google.android.leanbacklauncher.apps;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.google.android.leanbacklauncher.EditableAppsRowView;
import com.google.android.leanbacklauncher.LauncherViewHolder;
import com.google.android.leanbacklauncher.R;
import com.google.android.leanbacklauncher.animation.ViewDimmer.DimState;
import com.google.android.leanbacklauncher.apps.AppsManager.SortingMode;
import com.google.android.leanbacklauncher.apps.AppsRanker.RankingListener;
import com.google.android.leanbacklauncher.apps.LaunchPointListGenerator.Listener;
import com.google.android.leanbacklauncher.trace.AppTrace;
import com.google.android.leanbacklauncher.util.Lists;
import com.google.android.leanbacklauncher.util.Lists.Change;
import com.google.android.leanbacklauncher.widget.RowViewAdapter;
import java.util.ArrayList;
import java.util.List;

public class AppsAdapter extends RowViewAdapter<AppViewHolder> implements RankingListener, Listener {
    private final ActionOpenLaunchPointListener mActionOpenLaunchPointListener;
    protected final int mAppType;
    protected AppsManager mAppsManager;
    protected boolean mFlaggedForResort;
    protected final LayoutInflater mInflater;
    private boolean mItemsHaveBeenSorted;
    protected ArrayList<LaunchPoint> mLaunchPoints;
    private Handler mNotifyHandler = new Handler();

    public interface ActionOpenLaunchPointListener {
        void onActionOpenLaunchPoint(String str, String str2);
    }

    static class AppViewHolder extends LauncherViewHolder {
        private final AppsAdapter mAdapter;
        private BannerView mBannerView;
        private String mComponentName;
        private String mPackageName;

        AppViewHolder(View v, AppsAdapter adapter) {
            super(v);
            if (v instanceof BannerView) {
                this.mBannerView = (BannerView) v;
                this.mBannerView.setViewHolder(this);
            }
            this.mAdapter = adapter;
        }

        public void init(LaunchPoint launchPoint) {
            this.itemView.setVisibility(0);
            if (launchPoint != null) {
                this.mPackageName = launchPoint.getPackageName();
                this.mComponentName = launchPoint.getComponentName();
                setLaunchIntent(launchPoint.getLaunchIntent());
                setLaunchTranslucent(launchPoint.isTranslucentTheme());
                setLaunchColor(launchPoint.getLaunchColor());
            }
        }

        public void init(String packageName, String componentName, Intent launchIntent, int launchColor) {
            this.mPackageName = packageName;
            this.mComponentName = componentName;
            setLaunchIntent(launchIntent);
            setLaunchColor(launchColor);
        }

        public void clearBannerState() {
            if (this.mBannerView != null) {
                this.mBannerView.clearBannerForRecycle();
            }
        }

        protected final String getPackageName() {
            return this.mPackageName;
        }

        protected final String getComponentName() {
            return this.mComponentName;
        }

        protected void onLaunchSucceeded() {
            if (this.mAdapter != null) {
                this.mAdapter.mAppsManager.onAppsRankerAction(this.mPackageName, this.mComponentName, 1);
                this.mAdapter.onActionOpenLaunchPoint(this.mComponentName, this.mPackageName);
                if (this.mAdapter.mAppsManager.getSortingMode() == SortingMode.RECENCY) {
                    this.mAdapter.mFlaggedForResort = true;
                }
            }
        }

        private void checkEditModeDimLevel() {
            ViewHolder curView = null;
            if (this.itemView instanceof BannerView) {
                EditableAppsRowView parent = this.itemView.getParent() instanceof EditableAppsRowView ? (EditableAppsRowView) this.itemView.getParent() : null;
                if (parent != null) {
                    curView = parent.getCurViewHolder();
                }
                if (this.itemView.isActivated() && parent != null && parent.getEditMode() && curView != null && curView != this && curView.itemView.isSelected()) {
                    ((BannerView) this.itemView).setDimState(DimState.EDIT_MODE, true);
                }
            }
        }

        private void setEditModeInBannerView() {
            if ((this.itemView instanceof BannerView) && (this.itemView.getParent() instanceof EditableAppsRowView)) {
                ((BannerView) this.itemView).setEditMode(((EditableAppsRowView) this.itemView.getParent()).getEditMode());
            }
        }

        public void onClick(View v) {
            if ((v instanceof BannerView) && ((BannerView) v).isEditMode()) {
                ((BannerView) v).onClickInEditMode();
            } else {
                super.onClick(v);
            }
        }
    }

    public static class AppBannerViewHolder extends AppViewHolder {
        private final Drawable mBackground;
        private final ImageView mBannerView;
        private final InstallStateOverlayHelper mOverlayHelper;

        public /* bridge */ /* synthetic */ void init(String str, String str2, Intent intent, int i) {
            super.init(str, str2, intent, i);
        }

        public /* bridge */ /* synthetic */ void onClick(View view) {
            super.onClick(view);
        }

        public AppBannerViewHolder(View v, AppsAdapter adapter) {
            super(v, adapter);
            this.mOverlayHelper = new InstallStateOverlayHelper(v);
            if (v != null) {
                this.mBannerView = (ImageView) v.findViewById(R.id.app_banner);
            } else {
                this.mBannerView = null;
            }
            this.mBackground = v.getResources().getDrawable(R.drawable.banner_background, null);
        }

        public void init(LaunchPoint launchPoint) {
            super.init(launchPoint);
            if (launchPoint != null && this.mBannerView != null) {
                Drawable drawable = launchPoint.getBannerDrawable();
                if (!(drawable instanceof BitmapDrawable) || ((BitmapDrawable) drawable).getBitmap().hasAlpha()) {
                    this.mBannerView.setBackground(this.mBackground);
                } else {
                    this.mBannerView.setBackground(null);
                }
                this.mBannerView.setContentDescription(launchPoint.getTitle());
                this.mBannerView.setImageDrawable(drawable);
                if (launchPoint.isInstalling()) {
                    this.mOverlayHelper.initOverlay(launchPoint);
                } else {
                    this.mOverlayHelper.hideOverlay();
                }
            }
        }

        public void init(CharSequence title, Drawable banner, int launchColor) {
            super.init(null, null, null, launchColor);
            if (this.mBannerView != null) {
                this.mBannerView.setImageDrawable(banner);
                this.mBannerView.setContentDescription(title);
            }
        }

        public void clearBannerState() {
            super.clearBannerState();
            if (this.mBannerView != null) {
                this.mBannerView.setClipToOutline(true);
            }
        }
    }

    private static final class AppFallbackViewHolder extends AppViewHolder {
        private final ImageView mIconView;
        private final TextView mLabelView;
        private final InstallStateOverlayHelper mOverlayHelper;

        public AppFallbackViewHolder(View v, AppsAdapter adapter) {
            super(v, adapter);
            this.mOverlayHelper = new InstallStateOverlayHelper(v);
            if (v != null) {
                this.mIconView = (ImageView) v.findViewById(R.id.banner_icon);
                this.mLabelView = (TextView) v.findViewById(R.id.banner_label);
                return;
            }
            this.mIconView = null;
            this.mLabelView = null;
        }

        public void init(LaunchPoint launchPoint) {
            super.init(launchPoint);
            if (launchPoint != null) {
                Drawable icon = launchPoint.getIconDrawable();
                if (this.mIconView != null) {
                    this.mIconView.setImageDrawable(icon);
                }
                if (this.mLabelView != null) {
                    this.mLabelView.setText(launchPoint.getTitle());
                }
                if (launchPoint.isInstalling()) {
                    this.mOverlayHelper.initOverlay(launchPoint);
                } else {
                    this.mOverlayHelper.hideOverlay();
                }
            }
        }
    }

    private static class InstallStateOverlayHelper {
        private final View mOverlayView;
        private final ProgressBar mProgressBar;
        private final TextView mProgressView;
        private final TextView mStateView;

        public InstallStateOverlayHelper(View v) {
            if (v != null) {
                this.mOverlayView = v.findViewById(R.id.install_state_overlay);
                this.mStateView = (TextView) v.findViewById(R.id.banner_install_state);
                this.mProgressView = (TextView) v.findViewById(R.id.banner_install_progress);
                this.mProgressBar = (ProgressBar) v.findViewById(R.id.progress_bar);
                return;
            }
            this.mOverlayView = null;
            this.mStateView = null;
            this.mProgressView = null;
            this.mProgressBar = null;
        }

        public void initOverlay(LaunchPoint launchPoint) {
            if (this.mStateView != null) {
                this.mStateView.setText(launchPoint.getInstallStateString(this.mStateView.getContext()));
            }
            if (this.mProgressView != null) {
                this.mProgressView.setText(launchPoint.getInstallProgressString(this.mProgressView.getContext()));
            }
            if (this.mProgressBar != null) {
                int progressPercent = launchPoint.getInstallProgressPercent();
                if (progressPercent == -1) {
                    this.mProgressBar.setIndeterminate(true);
                } else {
                    this.mProgressBar.setProgress(progressPercent);
                    this.mProgressBar.setIndeterminate(false);
                }
            }
            if (this.mOverlayView != null) {
                this.mOverlayView.setVisibility(0);
            }
        }

        public void hideOverlay() {
            if (this.mOverlayView != null) {
                this.mOverlayView.setVisibility(8);
            }
        }
    }

    private class RefreshTask extends AsyncTask<Void, Void, ArrayList<LaunchPoint>> {
        private RefreshTask() {
        }

        protected ArrayList<LaunchPoint> doInBackground(Void... params) {
            String section;
            switch (AppsAdapter.this.mAppType) {
                case 0:
                    section = "RefreshAppsRow";
                    break;
                case 1:
                    section = "RefreshGamesRow";
                    break;
                case 2:
                    section = "RefreshSettingsRow";
                    break;
                default:
                    throw new IllegalStateException();
            }
            AppTrace.beginSection(section);
            try {
                ArrayList<LaunchPoint> access$500 = AppsAdapter.this.getRefreshedLaunchPointList();
                return access$500;
            } finally {
                AppTrace.endSection();
            }
        }

        protected void onPostExecute(ArrayList<LaunchPoint> launchPoints) {
            List<Change> changes = Lists.getChanges(AppsAdapter.this.mLaunchPoints, launchPoints, AppsAdapter.this.mAppsManager.getLaunchPointComparator());
            AppsAdapter.this.mLaunchPoints = launchPoints;
            AppsAdapter.this.onPostRefresh();
            for (Change change : changes) {
                switch (change.type) {
                    case INSERTION:
                        AppsAdapter.this.notifyItemRangeInserted(change.index, change.count);
                        break;
                    case REMOVAL:
                        AppsAdapter.this.notifyItemRangeRemoved(change.index, change.count);
                        break;
                    default:
                        throw new IllegalStateException("Unsupported change type: " + change.type);
                }
            }
        }
    }

    private static final class SettingViewHolder extends AppViewHolder {
        private final ImageView mIconView;
        private final TextView mLabelView;
        private final View mMainView;

        public SettingViewHolder(View v, AppsAdapter adapter) {
            super(v, adapter);
            if (v != null) {
                this.mMainView = v.findViewById(R.id.main);
                this.mIconView = (ImageView) v.findViewById(R.id.icon);
                this.mLabelView = (TextView) v.findViewById(R.id.label);
                return;
            }
            this.mMainView = null;
            this.mIconView = null;
            this.mLabelView = null;
        }

        public void init(LaunchPoint launchPoint) {
            super.init(launchPoint);
            if (launchPoint != null) {
                if (this.mIconView != null) {
                    this.mIconView.setImageDrawable(launchPoint.getIconDrawable());
                }
                if (this.mLabelView != null) {
                    this.mLabelView.setText(launchPoint.getTitle());
                }
                this.mMainView.setContentDescription(launchPoint.getContentDescription());
            }
        }
    }

    public AppsAdapter(Context context, int appType, ActionOpenLaunchPointListener actionOpenLaunchPointListener) {
        super(context);
        this.mInflater = (LayoutInflater) context.getSystemService("layout_inflater");
        this.mLaunchPoints = new ArrayList();
        this.mAppsManager = AppsManager.getInstance(context);
        this.mAppType = appType;
        this.mFlaggedForResort = false;
        this.mActionOpenLaunchPointListener = actionOpenLaunchPointListener;
        this.mAppsManager.registerLaunchPointListGeneratorListener(this);
    }

    public int getItemViewType(int position) {
        if (position >= this.mLaunchPoints.size()) {
            Log.e("AppsAdapter", "getItemViewType with out of bounds index = " + position);
            if (this.mAppType != 2) {
                return 0;
            }
            return 2;
        }
        LaunchPoint launchPoint = (LaunchPoint) this.mLaunchPoints.get(position);
        if (this.mAppType == 2) {
            return 2;
        }
        if (launchPoint.hasBanner()) {
            return 0;
        }
        return 1;
    }

    public AppViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        AppViewHolder holder;
        switch (viewType) {
            case 0:
                AppTrace.beginSection("onCreateAppBannerView");
                try {
                    holder = new AppBannerViewHolder(this.mInflater.inflate(R.layout.app_banner, parent, false), this);
                    return holder;
                } finally {
                    AppTrace.endSection();
                }
            case 1:
                AppTrace.beginSection("onCreateFallBannerView");
                try {
                    holder = new AppFallbackViewHolder(this.mInflater.inflate(R.layout.app_fallback_banner, parent, false), this);
                    return holder;
                } finally {
                    AppTrace.endSection();
                }
            case 2:
                AppTrace.beginSection("onCreateSettingsBannerView");
                try {
                    holder = new SettingViewHolder(this.mInflater.inflate(R.layout.setting, parent, false), this);
                    return holder;
                } finally {
                    AppTrace.endSection();
                }
            default:
                return null;
        }
    }

    public void onBindViewHolder(AppViewHolder holder, int position) {
        if (position < this.mLaunchPoints.size()) {
            AppTrace.beginSection("onBindAppBanner");
            try {
                LaunchPoint launchPoint = (LaunchPoint) this.mLaunchPoints.get(position);
                holder.clearBannerState();
                holder.init(launchPoint);
            } finally {
                AppTrace.endSection();
            }
        }
    }

    public void onViewAttachedToWindow(AppViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if ((holder.itemView instanceof BannerView) && (holder.itemView.getParent() instanceof EditableAppsRowView)) {
            ((EditableAppsRowView) holder.itemView.getParent()).refreshSelectedView();
            holder.checkEditModeDimLevel();
            holder.setEditModeInBannerView();
        }
    }

    public void onViewDetachedFromWindow(AppViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        if (holder.itemView instanceof BannerView) {
            holder.itemView.setSelected(false);
        }
    }

    public int getItemCount() {
        return this.mLaunchPoints.size();
    }

    public void sortItemsIfNeeded(boolean force) {
        boolean sortNeeded;
        if (this.mFlaggedForResort || force) {
            sortNeeded = true;
        } else {
            sortNeeded = false;
        }
        this.mFlaggedForResort = false;
        if (force && this.mAppsManager.getSortingMode() == SortingMode.FIXED) {
            saveAppOrderSnapshot();
        }
        if (sortNeeded) {
            this.mItemsHaveBeenSorted = true;
            sortLaunchPoints(this.mLaunchPoints);
            notifyDataSetChanged();
        }
    }

    public boolean takeItemsHaveBeenSorted() {
        boolean sorted = this.mItemsHaveBeenSorted;
        this.mItemsHaveBeenSorted = false;
        return sorted;
    }

    public boolean moveLaunchPoint(int initPosition, int desiredPosition, boolean userAction) {
        if (desiredPosition < 0 || desiredPosition > this.mLaunchPoints.size() - 1 || initPosition < 0 || initPosition > this.mLaunchPoints.size() - 1) {
            return false;
        }
        LaunchPoint focused = (LaunchPoint) this.mLaunchPoints.get(initPosition);
        this.mLaunchPoints.set(initPosition, (LaunchPoint) this.mLaunchPoints.get(desiredPosition));
        this.mLaunchPoints.set(desiredPosition, focused);
        notifyItemMoved(initPosition, desiredPosition);
        if (Math.abs(desiredPosition - initPosition) > 1) {
            notifyItemMoved(desiredPosition + (desiredPosition - initPosition > 0 ? -1 : 1), initPosition);
        }
        if (!userAction) {
            return true;
        }
        AppsManager.saveSortingMode(this.mContext, SortingMode.FIXED);
        return true;
    }

    public void saveAppOrderSnapshot() {
        if (Log.isLoggable("LauncherEditMode", 2)) {
            Log.d("LauncherEditMode", "AppsAdapter saw EditMode change and initiated snapshot.");
        }
        this.mAppsManager.saveOrderSnapshot(this.mLaunchPoints);
    }

    public Drawable getDrawableFromLaunchPoint(int index) {
        if (index < 0 || index >= this.mLaunchPoints.size()) {
            return null;
        }
        return ((LaunchPoint) this.mLaunchPoints.get(index)).getBannerDrawable();
    }

    public LaunchPoint getLaunchPointForPosition(int index) {
        if (index < 0 || index >= this.mLaunchPoints.size()) {
            return null;
        }
        return (LaunchPoint) this.mLaunchPoints.get(index);
    }

    private void onActionOpenLaunchPoint(String component, String group) {
        if (this.mActionOpenLaunchPointListener != null) {
            this.mActionOpenLaunchPointListener.onActionOpenLaunchPoint(component, group);
        }
    }

    private ArrayList<LaunchPoint> getRefreshedLaunchPointList() {
        ArrayList<LaunchPoint> launchPoints;
        if (this.mAppType == 0) {
            launchPoints = this.mAppsManager.getNonGameLaunchPoints();
        } else if (this.mAppType == 1) {
            launchPoints = this.mAppsManager.getGameLaunchPoints();
        } else if (this.mAppType == 2) {
            launchPoints = this.mAppsManager.getSettingsLaunchPoints(true);
        } else {
            launchPoints = this.mAppsManager.getAllLaunchPoints();
        }
        if (launchPoints == null) {
            launchPoints = new ArrayList();
        }
        sortLaunchPoints(launchPoints);
        return launchPoints;
    }

    protected void onPostRefresh() {
    }

    protected void sortLaunchPoints(ArrayList<LaunchPoint> launchPoints) {
        if (this.mAppType != 2) {
            this.mAppsManager.rankLaunchPoints(launchPoints, this);
        }
    }

    public void onLaunchPointsAddedOrUpdated(final ArrayList<LaunchPoint> launchPoints) {
        this.mNotifyHandler.post(new Runnable() {
            public void run() {
                boolean isGame;
                if (AppsAdapter.this.mAppType == 1) {
                    isGame = true;
                } else {
                    isGame = false;
                }
                boolean saveAppOrderChanges = false;
                for (int i = launchPoints.size() - 1; i >= 0; i--) {
                    boolean gameMatch;
                    if (((LaunchPoint) launchPoints.get(i)).isGame() == isGame) {
                        gameMatch = true;
                    } else {
                        gameMatch = false;
                    }
                    boolean found = false;
                    int j = AppsAdapter.this.mLaunchPoints.size() - 1;
                    while (j >= 0) {
                        if (((LaunchPoint) AppsAdapter.this.mLaunchPoints.get(j)).equals(launchPoints.get(i))) {
                            if (gameMatch) {
                                AppsAdapter.this.mLaunchPoints.set(j, launchPoints.get(i));
                                AppsAdapter.this.notifyItemChanged(j);
                                saveAppOrderChanges = true;
                            } else {
                                AppsAdapter.this.mLaunchPoints.remove(j);
                                AppsAdapter.this.notifyItemRemoved(j);
                                saveAppOrderChanges = true;
                            }
                            found = true;
                            if (!found && gameMatch) {
                                AppsAdapter.this.notifyItemInserted(AppsAdapter.this.mAppsManager.insertLaunchPoint(AppsAdapter.this.mLaunchPoints, (LaunchPoint) launchPoints.get(i)));
                                saveAppOrderChanges = true;
                            }
                        } else {
                            j--;
                        }
                    }
                    AppsAdapter.this.notifyItemInserted(AppsAdapter.this.mAppsManager.insertLaunchPoint(AppsAdapter.this.mLaunchPoints, (LaunchPoint) launchPoints.get(i)));
                    saveAppOrderChanges = true;
                }
                if (saveAppOrderChanges && AppsAdapter.this.mAppsManager.getSortingMode() == SortingMode.FIXED) {
                    AppsAdapter.this.saveAppOrderSnapshot();
                }
            }
        });
    }

    public void onLaunchPointsRemoved(final ArrayList<LaunchPoint> launchPoints) {
        this.mNotifyHandler.post(new Runnable() {
            public void run() {
                int i;
                boolean saveAppOrderChanges = false;
                int itemRemovedAt = -1;
                for (int j = AppsAdapter.this.mLaunchPoints.size() - 1; j >= 0; j--) {
                    for (i = launchPoints.size() - 1; i >= 0; i--) {
                        if (((LaunchPoint) AppsAdapter.this.mLaunchPoints.get(j)).equals(launchPoints.get(i)) && itemRemovedAt == -1) {
                            launchPoints.remove(i);
                            saveAppOrderChanges = true;
                            itemRemovedAt = j;
                            break;
                        }
                    }
                }
                if (saveAppOrderChanges && AppsAdapter.this.mAppsManager.getSortingMode() == SortingMode.FIXED) {
                    AppsAdapter.this.saveAppOrderSnapshot();
                }
                if (itemRemovedAt != -1) {
                    int numRows;
                    int viewType = AppsAdapter.this.getItemViewType(itemRemovedAt);
                    Resources res = AppsAdapter.this.mContext.getResources();
                    if (AppsAdapter.this.getItemCount() > res.getInteger(R.integer.two_row_cut_off)) {
                        numRows = res.getInteger(R.integer.max_num_banner_rows);
                    } else {
                        numRows = res.getInteger(R.integer.min_num_banner_rows);
                    }
                    if ((viewType == 0 || viewType == 1) && numRows > 1) {
                        int lastPosition = itemRemovedAt;
                        for (i = itemRemovedAt; i + numRows < AppsAdapter.this.mLaunchPoints.size(); i += numRows) {
                            AppsAdapter.this.moveLaunchPoint(i, i + numRows, false);
                            lastPosition = i + numRows;
                        }
                        AppsAdapter.this.mLaunchPoints.remove(lastPosition);
                        AppsAdapter.this.notifyItemRemoved(lastPosition);
                    } else {
                        AppsAdapter.this.mLaunchPoints.remove(itemRemovedAt);
                        AppsAdapter.this.notifyItemRemoved(itemRemovedAt);
                    }
                }
                AppsAdapter.this.saveAppOrderSnapshot();
            }
        });
    }

    public void onLaunchPointListGeneratorReady() {
        if (this.mAppsManager.isAppsRankerReady()) {
            refreshDataSetAsync();
        }
    }

    public void onSettingsChanged() {
    }

    public void onRankerReady() {
        if (this.mAppsManager.isLaunchPointListGeneratorReady()) {
            refreshDataSetAsync();
        }
    }

    public void onViewRecycled(AppViewHolder holder) {
        holder.itemView.setVisibility(0);
    }

    public void refreshDataSetAsync() {
        new RefreshTask().execute(new Void[0]);
    }
}
