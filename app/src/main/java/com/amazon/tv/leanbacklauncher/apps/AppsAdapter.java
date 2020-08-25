package com.amazon.tv.leanbacklauncher.apps;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.amazon.tv.firetv.leanbacklauncher.apps.AppCategory;
import com.amazon.tv.firetv.leanbacklauncher.apps.RowPreferences;
import com.amazon.tv.firetv.leanbacklauncher.util.SharedPreferencesUtil;
import com.amazon.tv.leanbacklauncher.BuildConfig;
import com.amazon.tv.leanbacklauncher.EditableAppsRowView;
import com.amazon.tv.leanbacklauncher.LauncherViewHolder;
import com.amazon.tv.leanbacklauncher.R;
import com.amazon.tv.leanbacklauncher.animation.ViewDimmer;
import com.amazon.tv.leanbacklauncher.trace.AppTrace;
import com.amazon.tv.leanbacklauncher.util.Lists;
import com.amazon.tv.leanbacklauncher.widget.RowViewAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AppsAdapter extends RowViewAdapter<AppsAdapter.AppViewHolder> implements AppsRanker.RankingListener, LaunchPointListGenerator.Listener, SharedPreferences.OnSharedPreferenceChangeListener {
    private final ActionOpenLaunchPointListener mActionOpenLaunchPointListener;
    protected final Set<AppCategory> mAppTypes;
    protected AppFilter mFilter;
    protected AppsManager mAppsManager;
    protected boolean mFlaggedForResort;
    protected final LayoutInflater mInflater;
    private boolean mItemsHaveBeenSorted;
    protected ArrayList<LaunchPoint> mLaunchPoints;
    private Handler mNotifyHandler = new Handler();
    private static String TAG = "AppsAdapter";

    private SharedPreferencesUtil prefUtil;
    private SharedPreferences.OnSharedPreferenceChangeListener listener = this;

    public interface ActionOpenLaunchPointListener {
        void onActionOpenLaunchPoint(String str, String str2);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        refreshDataSetAsync();
    }

    public static abstract class AppFilter {

        public abstract boolean include(LaunchPoint point);

    }

    public static class AppViewHolder extends LauncherViewHolder {
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
            this.itemView.setVisibility(View.VISIBLE);
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

        public final String getPackageName() {
            return this.mPackageName;
        }

        protected final String getComponentName() {
            return this.mComponentName;
        }

        protected void onLaunchSucceeded() {
            if (this.mAdapter != null) {
                this.mAdapter.mAppsManager.onAppsRankerAction(this.mPackageName, this.mComponentName, 1);
                this.mAdapter.onActionOpenLaunchPoint(this.mComponentName, this.mPackageName);
                if (this.mAdapter.mAppsManager.getSortingMode() == AppsManager.SortingMode.RECENCY) {
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
                    ((BannerView) this.itemView).setDimState(ViewDimmer.DimState.EDIT_MODE, true);
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
                this.mBannerView = v.findViewById(R.id.app_banner);
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
                    // background for Logo images
                    if (launchPoint.getLaunchColor() != 0)
                        this.mBannerView.setBackgroundColor(launchPoint.getLaunchColor());
                    else
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
        private final LinearLayout mBannerView;
        private final TextView mLabelView;
        private final InstallStateOverlayHelper mOverlayHelper;

        public AppFallbackViewHolder(View v, AppsAdapter adapter) {
            super(v, adapter);
            this.mOverlayHelper = new InstallStateOverlayHelper(v);
            if (v != null) {
                this.mIconView = v.findViewById(R.id.banner_icon);
                this.mLabelView = v.findViewById(R.id.banner_label);
                this.mBannerView = v.findViewById(R.id.app_banner);
                return;
            }
            this.mIconView = null;
            this.mLabelView = null;
            this.mBannerView = null;
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
                    // dynamic label text
                    if (isLight(launchPoint.getLaunchColor()))
                        this.mLabelView.setTextColor(Color.BLACK);
                    else
                        this.mLabelView.setTextColor(Color.WHITE);
                }
                if (launchPoint.isInstalling()) {
                    this.mOverlayHelper.initOverlay(launchPoint);
                } else {
                    this.mOverlayHelper.hideOverlay();
                }
                // background color
                if (this.mBannerView != null) {
                    this.mBannerView.setBackgroundColor(launchPoint.getLaunchColor());
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
                this.mStateView = v.findViewById(R.id.banner_install_state);
                this.mProgressView = v.findViewById(R.id.banner_install_progress);
                this.mProgressBar = v.findViewById(R.id.progress_bar);
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
                this.mOverlayView.setVisibility(View.VISIBLE);
            }
        }

        public void hideOverlay() {
            if (this.mOverlayView != null) {
                this.mOverlayView.setVisibility(View.GONE);
            }
        }
    }

    private class RefreshTask extends AsyncTask<Void, Void, ArrayList<LaunchPoint>> {
        private RefreshTask() {
        }

        protected ArrayList<LaunchPoint> doInBackground(Void... params) {
            String trace;

            if (!mAppTypes.isEmpty()) {
                StringBuilder section = new StringBuilder("Refresh [");

                for (AppCategory category : mAppTypes) {
                    switch (category) {
                        case OTHER:
                            section.append("OTHER");
                            break;
                        case GAME:
                            section.append("GAME");
                            break;
                        case VIDEO:
                            section.append("VIDEO");
                            break;
                        case MUSIC:
                            section.append("MUSIC");
                            break;
                        case SETTINGS:
                            section.append("SETTINGS");
                            break;
                        default:
                            throw new IllegalStateException();

                    }

                    section.append(',');
                }

                section.append("] Row");

                int lastIndex = section.lastIndexOf(",");

                trace = section.substring(0, lastIndex) + section.substring(lastIndex + 1);
            } else {
                trace = "Refresh [UNKNOWN] Row";
            }

            AppTrace.beginSection(trace);
            try {
                List<LaunchPoint> lps = AppsAdapter.this.getRefreshedLaunchPointList();
                ArrayList<LaunchPoint> filtered = new ArrayList<>();

                // TODO

                for (LaunchPoint lp : lps) {
                    if (mFilter.include(lp)) {
                        filtered.add(lp);
                    }
                }

                return filtered;
            } finally {
                AppTrace.endSection();
            }
        }

        protected void onPostExecute(ArrayList<LaunchPoint> launchPoints) {
            List<Lists.Change> changes = Lists.getChanges(AppsAdapter.this.mLaunchPoints, launchPoints, AppsAdapter.this.mAppsManager.getLaunchPointComparator());

            AppsAdapter.this.mLaunchPoints = launchPoints;
            AppsAdapter.this.onPostRefresh();

            for (Lists.Change change : changes) {
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
                this.mIconView = v.findViewById(R.id.icon);
                this.mLabelView = v.findViewById(R.id.label);
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

    public AppsAdapter(Context context, ActionOpenLaunchPointListener actionOpenLaunchPointListener, AppCategory... appTypes) {
        super(context);
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mLaunchPoints = new ArrayList<>();
        this.mAppsManager = AppsManager.getInstance(context);
        this.prefUtil = SharedPreferencesUtil.instance(context);

        this.prefUtil.addHiddenListener(listener);

        this.mFilter = new AppFilter() {
            @Override
            public boolean include(LaunchPoint point) {
                return true;
            }
        };

        if (appTypes != null) {
            this.mAppTypes = new HashSet<>(Arrays.asList(appTypes));
        } else {
            this.mAppTypes = Collections.emptySet();
        }

        this.mFlaggedForResort = false;
        this.mActionOpenLaunchPointListener = actionOpenLaunchPointListener;
        this.mAppsManager.registerLaunchPointListGeneratorListener(this);
    }

    public int getItemViewType(int position) {
        if (position >= this.mLaunchPoints.size()) {
            Log.e("AppsAdapter", "getItemViewType with out of bounds index = " + position);
            if (!this.mAppTypes.contains(AppCategory.SETTINGS)) {
                return 0;
            }
            return 2;
        }
        LaunchPoint launchPoint = this.mLaunchPoints.get(position);
        if (this.mAppTypes.contains(AppCategory.SETTINGS)) {
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
                LaunchPoint launchPoint = this.mLaunchPoints.get(position);
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
        sortNeeded = this.mFlaggedForResort || force;
        this.mFlaggedForResort = false;
        if (force && this.mAppsManager.getSortingMode() == AppsManager.SortingMode.FIXED) {
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
        LaunchPoint focused = this.mLaunchPoints.get(initPosition);
        this.mLaunchPoints.set(initPosition, this.mLaunchPoints.get(desiredPosition));
        this.mLaunchPoints.set(desiredPosition, focused);
        notifyItemMoved(initPosition, desiredPosition);
        if (Math.abs(desiredPosition - initPosition) > 1) {
            notifyItemMoved(desiredPosition + (desiredPosition - initPosition > 0 ? -1 : 1), initPosition);
        }
        if (!userAction) {
            return true;
        }
        AppsManager.saveSortingMode(this.mContext, AppsManager.SortingMode.FIXED);
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
        return this.mLaunchPoints.get(index).getBannerDrawable();
    }

    public LaunchPoint getLaunchPointForPosition(int index) {
        if (index < 0 || index >= this.mLaunchPoints.size()) {
            return null;
        }
        return this.mLaunchPoints.get(index);
    }

    private void onActionOpenLaunchPoint(String component, String group) {
        if (this.mActionOpenLaunchPointListener != null) {
            this.mActionOpenLaunchPointListener.onActionOpenLaunchPoint(component, group);
        }
    }

    private ArrayList<LaunchPoint> getRefreshedLaunchPointList() {
        ArrayList<LaunchPoint> launchPoints = new ArrayList<>();

        if (this.mAppTypes.isEmpty()) {
            return this.mAppsManager.getAllLaunchPoints();
        }

        for (AppCategory category : this.mAppTypes) {
            switch (category) {
                case OTHER:
                    launchPoints.addAll(this.mAppsManager.getLaunchPointsByCategory(AppCategory.OTHER));
                    break;
                case VIDEO:
                    launchPoints.addAll(this.mAppsManager.getLaunchPointsByCategory(AppCategory.VIDEO));
                    break;
                case MUSIC:
                    launchPoints.addAll(this.mAppsManager.getLaunchPointsByCategory(AppCategory.MUSIC));
                    break;
                case GAME:
                    launchPoints.addAll(this.mAppsManager.getLaunchPointsByCategory(AppCategory.GAME));
                    break;
                case SETTINGS:
                    launchPoints.addAll(this.mAppsManager.getSettingsLaunchPoints(true));
                    break;
            }
        }

        sortLaunchPoints(launchPoints);

        return launchPoints;
    }

    protected void onPostRefresh() {
    }

    protected void sortLaunchPoints(ArrayList<LaunchPoint> launchPoints) {
        if (!this.mAppTypes.contains(AppCategory.SETTINGS)) {
            this.mAppsManager.rankLaunchPoints(launchPoints, this);
        }
    }

    public void onLaunchPointsAddedOrUpdated(final ArrayList<LaunchPoint> launchPoints) {
        this.mNotifyHandler.post(new Runnable() {
            public void run() {
                if (BuildConfig.DEBUG) Log.d(TAG, "run: onLaunchPointsAddedOrUpdated");
                boolean saveAppOrderChanges = false;
                // if (BuildConfig.DEBUG) Log.d(TAG, "size: " + AppsAdapter.this.mLaunchPoints.size());

                if (BuildConfig.DEBUG) Log.d(TAG, "Apps: " + AppsAdapter.this.mLaunchPoints);
                for (int i = 0; i < launchPoints.size(); i++) {
                    LaunchPoint lp = launchPoints.get(i);
                    if (BuildConfig.DEBUG) Log.d(TAG, "Check: " + lp);

                    for (int j = 0; j < AppsAdapter.this.mLaunchPoints.size(); j++) {
                        LaunchPoint alp = AppsAdapter.this.mLaunchPoints.get(j);
                        if (lp.getPackageName().equals(alp.getPackageName())) {
                            if (BuildConfig.DEBUG) Log.d(TAG, "Found: " + alp + " Position: " + j);
                            AppsAdapter.this.mLaunchPoints.remove(j);
                            AppsAdapter.this.notifyItemRemoved(j);
                        }
                    }
                }

                for (int i = launchPoints.size() - 1; i >= 0; i--) {
                    if (!mFilter.include(launchPoints.get(i))) {
                        continue;
                    }

                    LaunchPoint lp = launchPoints.get(i);

                    if (lp != null && !mAppTypes.contains(lp.getAppCategory())) {
                        continue;
                    }

                    if (BuildConfig.DEBUG) Log.d(TAG, "notifyItemInserted");
                    AppsAdapter.this.notifyItemInserted(AppsAdapter.this.mAppsManager.insertLaunchPoint(AppsAdapter.this.mLaunchPoints, lp));
                    saveAppOrderChanges = true;
                }

                if (saveAppOrderChanges && AppsAdapter.this.mAppsManager.getSortingMode() == AppsManager.SortingMode.FIXED) {
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
                        if (AppsAdapter.this.mLaunchPoints.get(j).equals(launchPoints.get(i)) && itemRemovedAt == -1) {
                            launchPoints.remove(i);
                            saveAppOrderChanges = true;
                            itemRemovedAt = j;
                            break;
                        }
                    }
                }
                if (saveAppOrderChanges && AppsAdapter.this.mAppsManager.getSortingMode() == AppsManager.SortingMode.FIXED) {
                    AppsAdapter.this.saveAppOrderSnapshot();
                }
                if (itemRemovedAt != -1) {
                    int numRows;
                    int maxApps = RowPreferences.getAppsMax(AppsAdapter.this.mContext);
                    int viewType = AppsAdapter.this.getItemViewType(itemRemovedAt);
                    Resources res = AppsAdapter.this.mContext.getResources();
                    if (AppsAdapter.this.getItemCount() > maxApps) {
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
        // TODO
    }

    public void onRankerReady() {
        if (this.mAppsManager.isLaunchPointListGeneratorReady()) {
            refreshDataSetAsync();
        }
    }

    public void onViewRecycled(AppViewHolder holder) {
        holder.itemView.setVisibility(View.VISIBLE);
    }

    public void refreshDataSetAsync() {
        new RefreshTask().execute();
    }

    static boolean isDark(int color) {
        return ColorUtils.calculateLuminance(color) < 0.25; // 0.5
    }

    static boolean isLight(int color) {
        return ColorUtils.calculateLuminance(color) > 0.5;
    }

}
