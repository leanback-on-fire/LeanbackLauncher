package com.rockon999.android.leanbacklauncher.apps;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.rockon999.android.leanbacklauncher.ActiveItemsRowView;
import com.rockon999.android.leanbacklauncher.BuildConfig;
import com.rockon999.android.leanbacklauncher.LauncherViewHolder;
import com.rockon999.android.leanbacklauncher.R;
import com.rockon999.android.leanbacklauncher.animation.ViewDimmer.DimState;
import com.rockon999.android.leanbacklauncher.apps.AppsAdapter.AppViewHolder;
import com.rockon999.android.leanbacklauncher.apps.LaunchPointListGenerator.Listener;
import com.rockon999.android.leanbacklauncher.util.Lists;
import com.rockon999.android.leanbacklauncher.widget.RowViewAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AppsAdapter extends RowViewAdapter<AppViewHolder> implements AppsRanker.RankingListener, Listener {
    private static final String TAG = "AppsAdapter";
    final AppsRanker mAppsRanker;
    private final boolean mFavoritesRow;
    protected boolean mFlaggedForResort;
    protected final LayoutInflater mInflater;
    private boolean mItemsHaveBeenSorted;
    protected LaunchPointListGenerator mLaunchPointGen;
    protected List<LaunchPoint> mLaunchPoints;
    private Handler mNotifyHandler;

    public static class AppViewHolder extends LauncherViewHolder {
        private final AppsAdapter mAdapter;
        private BannerView mBannerView;
        private String mComponentName;
        private String mPackageName;
        private AppCategory mAppCategory;

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
                this.mAppCategory = launchPoint.getAppCategory();
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

        public final String getComponentName() {
            return this.mComponentName;
        }

        protected void onLaunchSucceeded() {
            if (this.mAdapter != null) {
                this.mAdapter.mAppsRanker.onAction(this.mPackageName, this.mComponentName, 1, this.mAppCategory);
                //this.mAdapter.onActionOpenLaunchPoint(this.mComponentName, this.mPackageName);
                if (this.mAdapter.mAppsRanker.getSortingMode() == AppsPreferences.SortingMode.RECENCY) {
                    this.mAdapter.mFlaggedForResort = true;
                }
            }
        }

        private void checkEditModeDimLevel() {
            ActiveItemsRowView parent = null;
            if (this.itemView instanceof BannerView) {
                if (this.itemView.getParent() instanceof ActiveItemsRowView) {
                    parent = (ActiveItemsRowView) this.itemView.getParent();
                }
                View curView = parent != null ? parent.getCurView() : null;
                if (this.itemView.isActivated() && parent != null && parent.getEditMode() && curView != null && this.itemView != curView && curView.isSelected()) {
                    ((BannerView) this.itemView).setDimState(DimState.EDIT_MODE, true);
                }
            }
        }

        private void setEditModeInBannerView() {
            if (this.itemView instanceof BannerView) {
                ((BannerView) this.itemView).setEditMode(((ActiveItemsRowView) this.itemView.getParent()).getEditMode());
            }
        }

        public void onClick(View v) {
            Log.i(TAG, "onClick->v->className:" + v.getClass().getName());
            if ((v instanceof BannerView) && ((BannerView) v).isEditMode()) {
                ((BannerView) v).onClickInEditMode();
            } else {
                super.onClick(v);
            }
        }
    }

    public static class AppBannerViewHolder extends AppViewHolder {
        private final ImageView mBannerView;
        private final InstallStateOverlayHelper mOverlayHelper;

        public void init(String packageName, String componentName, Intent launchIntent, int launchColor) {
            super.init(packageName, componentName, launchIntent, launchColor);
        }

        public void onClick(View v) {
            super.onClick(v);
        }

        public AppBannerViewHolder(View v, AppsAdapter adapter) {
            super(v, adapter);
            this.mOverlayHelper = new InstallStateOverlayHelper(v);
            if (v != null) {
                this.mBannerView = (ImageView) v.findViewById(R.id.app_banner);
            } else {
                this.mBannerView = null;
            }
        }

        public void init(LaunchPoint launchPoint) {
            super.init(launchPoint);
            if (launchPoint != null && this.mBannerView != null) {
                this.mBannerView.setContentDescription(launchPoint.getTitle());
                this.mBannerView.setImageDrawable(launchPoint.getBannerDrawable());
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

    private static final class AppEditViewHolder extends AppViewHolder {
        public AppEditViewHolder(View v, AppsAdapter adapter) {
            super(v, adapter);
        }
    }

    private static final class AppFallbackViewHolder extends AppViewHolder {
        private final ImageView mIconView;
        private final TextView mLabelView;
        private final View mBannerView;
        private final BannerView rootView;
        private final InstallStateOverlayHelper mOverlayHelper;

        public AppFallbackViewHolder(View v, AppsAdapter adapter) {
            super(v, adapter);
            if (v != null && v instanceof BannerView)
                rootView = (BannerView) v;
            else
                rootView = null;
            this.mOverlayHelper = new InstallStateOverlayHelper(v);
            if (v != null) {
                this.mIconView = (ImageView) v.findViewById(R.id.banner_icon);
                this.mLabelView = (TextView) v.findViewById(R.id.banner_label);
                mBannerView = v.findViewById(R.id.app_banner);
                return;
            }
            mBannerView = null;
            this.mIconView = null;
            this.mLabelView = null;
        }

        public void init(LaunchPoint launchPoint) {
            super.init(launchPoint);

            if (launchPoint != null) {
                Log.i(TAG, "AppFallbackViewHolder->init");
                Drawable icon = launchPoint.getIconDrawable();

                if (this.mIconView != null) {
                    this.mIconView.setImageDrawable(icon);
                }

                if (this.mLabelView != null) {
                    this.mLabelView.setText(launchPoint.getTitle());
                }

                if (this.mBannerView != null) {
                    mBannerView.setBackgroundColor(launchPoint.getLaunchColor());

                    if (rootView != null) {
                        rootView.setBannerBackColor(launchPoint.getLaunchColor());
                    }
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
                this.mOverlayView.setVisibility(View.VISIBLE);
            }
        }

        public void hideOverlay() {
            if (this.mOverlayView != null) {
                this.mOverlayView.setVisibility(View.GONE);
            }
        }
    }

    protected class RefreshTask extends AsyncTask<Void, Void, List<LaunchPoint>> {
        private RefreshTask() {
        }

        protected List<LaunchPoint> doInBackground(Void... paramVarArgs) {
            return AppsAdapter.this.getRefreshedLaunchPointList();
        }

        protected void onPostExecute(List<LaunchPoint> paramArrayList) {
            List<Lists.Change> localObject = Lists.getChanges(AppsAdapter.this.mLaunchPoints, paramArrayList, AppsAdapter.this.mAppsRanker.getLaunchPointComparator());

            AppsAdapter.this.mLaunchPoints = paramArrayList;
            AppsAdapter.this.onPostRefresh();

            for (Lists.Change localObj : localObject) {
                AppsAdapter.this.notifyItemRangeInserted(localObj.index, localObj.count);
            }
        }
    }

    public static final class SettingViewHolder extends AppViewHolder {
        private final ImageView mIconView;
        private final TextView mLabelView;
        private final View mMainView;
        private int settingsType;

        public SettingViewHolder(View v, AppsAdapter adapter) {
            super(v, adapter);
            Log.d("AppsAdapter", "Created SettingsUtil View Holder v = " + v);
            if (v != null) {
                this.mMainView = v.findViewById(R.id.main);
                this.mIconView = (ImageView) v.findViewById(R.id.icon);
                this.mLabelView = (TextView) v.findViewById(R.id.label);
                Log.d("AppsAdapter", "   mMainView  = " + this.mMainView);
                Log.d("AppsAdapter", "   mIconView  = " + this.mIconView);
                Log.d("AppsAdapter", "   mLabelView = " + this.mLabelView);
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
                if (this.mMainView != null) {
                    this.mMainView.setContentDescription(launchPoint.getContentDescription());
                }
                this.settingsType = launchPoint.getSettingsType();
            }
        }

        public int getSettingsType() {
            return settingsType;
        }
    }

    enum ViewType {

        BANNER(0),
        LEGACY(1),
        SETTINGS(2);

        public final int type;

        ViewType(int type) {
            this.type = type;
        }

        public static ViewType fromId(int viewType) {
            // todo
            switch (viewType) {
                case 0:
                    return BANNER;
                case 1:
                    return LEGACY;
                case 2:
                    return SETTINGS;
            }

            return BANNER;
        }
    }

    private List<AppCategory> mIncludedCategories;

    public AppsAdapter(Context context, LaunchPointListGenerator launchPointListGenerator, AppsRanker appsRanker, boolean isFavoritesRow, AppCategory... included) {
        super(context);
        this.mNotifyHandler = new Handler();
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mLaunchPoints = new ArrayList<>();
        this.mAppsRanker = appsRanker;
        this.mIncludedCategories = included == null ? Collections.<AppCategory>emptyList() : Arrays.asList(included);
        this.mFlaggedForResort = false;
        this.mLaunchPointGen = launchPointListGenerator;
        this.mLaunchPointGen.registerChangeListener(this);
        this.mFavoritesRow = isFavoritesRow;
    }

    public AppsAdapter(Context context, LaunchPointListGenerator launchPointListGenerator, AppsRanker appsRanker, boolean isFavoritesRow) {
        super(context);
        this.mNotifyHandler = new Handler();
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mLaunchPoints = new ArrayList<>();
        this.mIncludedCategories = Collections.emptyList();
        this.mAppsRanker = appsRanker;
        this.mFlaggedForResort = false;
        this.mLaunchPointGen = launchPointListGenerator;
        this.mLaunchPointGen.registerChangeListener(this);
        this.mFavoritesRow = isFavoritesRow;
    }

    public int getItemViewType(int position) {
        if (position >= this.mLaunchPoints.size()) {
            Log.e("AppsAdapter", "getItemViewType with out of bounds index = " + position);
            if (this.mIncludedCategories != null && this.mIncludedCategories.contains(AppCategory.SETTINGS)) {
                return ViewType.SETTINGS.type;
            }
            return ViewType.BANNER.type;
        }

        LaunchPoint launchPoint = this.mLaunchPoints.get(position);

        if (mIncludedCategories != null && mIncludedCategories.contains(AppCategory.SETTINGS)) {
            return ViewType.SETTINGS.type;
        } else if (launchPoint.hasBanner()) {
            return ViewType.BANNER.type;
        } else {
            return ViewType.LEGACY.type;
        }
    }

    @SuppressLint("PrivateResource")
    public AppViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.i(TAG, "onCreateViewHolder->viewType:" + viewType);
        ViewType type = ViewType.fromId(viewType);

        switch (type) {
            case BANNER:
                return new AppBannerViewHolder(this.mInflater.inflate(R.layout.app_banner, parent, false), this);
            case LEGACY:
                return new AppFallbackViewHolder(this.mInflater.inflate(R.layout.app_fallback_banner, parent, false), this);
            case SETTINGS:
                return new SettingViewHolder(this.mInflater.inflate(R.layout.setting, parent, false), this);
            default:
                return null;
        }
    }

    public void onBindViewHolder(AppViewHolder holder, int position) {
        Log.i(TAG, "onBindViewHolder->className:" + holder.getClass().getName());
        Log.i(TAG, "onBindViewHolder->position:" + position);
        if (position < this.mLaunchPoints.size()) {
            LaunchPoint launchPoint = this.mLaunchPoints.get(position);
            Log.i(TAG, "onBindViewHolder->packageName:" + launchPoint.getPackageName());
            int itemViewType = holder.getItemViewType();
            holder.clearBannerState();
            holder.init(launchPoint);
        }
    }

    public void onViewAttachedToWindow(AppViewHolder holder) {
        Log.i(TAG, "onViewAttachedToWindow->holder:" + holder.getClass().getName());
        super.onViewAttachedToWindow(holder);
        if ((holder.itemView instanceof BannerView) && (holder.itemView.getParent() instanceof ActiveItemsRowView)) {
            ((ActiveItemsRowView) holder.itemView.getParent()).refreshSelectedView();
            holder.checkEditModeDimLevel();
            holder.setEditModeInBannerView();
        }
    }

    public void onViewDetachedFromWindow(AppViewHolder holder) {
        Log.i(TAG, "onViewDetachedFromWindow->holder:" + holder.getClass().getName());
        super.onViewDetachedFromWindow(holder);
        if (holder.itemView instanceof BannerView) {
            holder.itemView.setSelected(false);
        }
    }

    public int getItemCount() {
        return this.mLaunchPoints.size();
    }

    public void sortItemsIfNeeded(boolean force) {
        Log.i(TAG, "sortItemsIfNeeded");
        boolean z = this.mFlaggedForResort || force;
        this.mFlaggedForResort = false;
        if (force && this.mAppsRanker.getSortingMode() == AppsPreferences.SortingMode.FIXED) {
            saveAppOrderSnapshot();
        }
        if (z) {
            this.mItemsHaveBeenSorted = true;
        }
    }

    public boolean takeItemsHaveBeenSorted() {
        Log.i(TAG, "takeItemsHaveBeenSorted");
        boolean sorted = this.mItemsHaveBeenSorted;
        this.mItemsHaveBeenSorted = false;
        return sorted;
    }

    public boolean moveLaunchPoint(int fromPosition, int toPosition, boolean userAction) {
        Log.i(TAG, "moveLaunchPoint");
        if ((toPosition < 0) || (toPosition > this.mLaunchPoints.size() - 1)) {
            return false; //todo?
        }
        if ((fromPosition < 0) || (fromPosition > this.mLaunchPoints.size() - 1)) {
            return false;
        }
        LaunchPoint localLaunchPoint1 = this.mLaunchPoints.get(fromPosition);
        LaunchPoint localLaunchPoint2 = this.mLaunchPoints.get(toPosition);

        this.mLaunchPoints.set(fromPosition, localLaunchPoint2);
        this.mLaunchPoints.set(toPosition, localLaunchPoint1);

        notifyItemMoved(fromPosition, toPosition);

        if (Math.abs(toPosition - fromPosition) > 1 && toPosition <= fromPosition) {
            notifyItemMoved(toPosition + 1, fromPosition);
        }

        if (userAction) {
            AppsPreferences.saveSortingMode(this.mContext, AppsPreferences.SortingMode.FIXED);
        }
        return true;
    }


    public void saveAppOrderSnapshot() {
        Log.d("LauncherEditMode", "AppsAdapter saw EditMode change and initiated snapshot.");
        this.mAppsRanker.saveOrderSnapshot(this.mLaunchPoints);

        this.notifyDataSetChanged(); // todo debug why this is currently necessary
    }

    public Drawable getDrawableFromLaunchPoint(int index) {
        return this.mLaunchPoints.get(index).getBannerDrawable();
    }

    public LaunchPoint getLaunchPointForPosition(int index) {
        return index >= this.mLaunchPoints.size() ? null : this.mLaunchPoints.get(index);
    }

    protected List<LaunchPoint> getRefreshedLaunchPointList() {
        ArrayList<LaunchPoint> launchPoints = new ArrayList<>();
        if (this.mIncludedCategories == null || this.mIncludedCategories.isEmpty()) {
            launchPoints.addAll(this.mLaunchPointGen.getAllLaunchPoints());
        } else {
            for (AppCategory category : this.mIncludedCategories) {
                switch (category) {
                    case SETTINGS:
                        launchPoints.addAll(this.mLaunchPointGen.getSettingsLaunchPoints(true));
                        break;
                    case MUSIC:
                        launchPoints.addAll(this.mLaunchPointGen.getLaunchPointsByType(AppCategory.MUSIC));
                        break;
                    case VIDEO:
                        launchPoints.addAll(this.mLaunchPointGen.getLaunchPointsByType(AppCategory.VIDEO));
                        break;
                    case OTHER:
                        launchPoints.addAll(this.mLaunchPointGen.getLaunchPointsByType(AppCategory.OTHER));
                        break;
                    case GAME:
                        launchPoints.addAll(this.mLaunchPointGen.getLaunchPointsByType(AppCategory.GAME));
                        break;
                    default:
                        launchPoints = this.mLaunchPointGen.getAllLaunchPoints();
                }
            }
        }

        if (mFavoritesRow) {
            for (int x = launchPoints.size() - 1; x >= 0; x--) {
                LaunchPoint lp = launchPoints.get(x);
                if (!this.mAppsRanker.isFavorite(lp)) {
                    launchPoints.remove(x);
                }
            }
        } else {
            for (int x = launchPoints.size() - 1; x >= 0; x--) {
                LaunchPoint lp = launchPoints.get(x);
                if (this.mAppsRanker.isFavorite(lp)) {
                    launchPoints.remove(x);
                }
            }
        }

        sortLaunchPoints(launchPoints);

        return launchPoints;
    }

    protected void onPostRefresh() {
        Log.i(TAG, "onPostRefresh");
    }

    protected void sortLaunchPoints(ArrayList<LaunchPoint> launchPoints) {
        Log.i(TAG, "sortLaunchPoints");

        //if (this.mIncludedCategories == null || !this.mIncludedCategories.contains(AppCategory.SETTINGS)) {
        //    this.mAppsRanker.rankLaunchPoints(launchPoints, this);
        //} else {
        this.mAppsRanker.rankLaunchPoints(launchPoints, this);
        //}
    }

    public void onLaunchPointsAddedOrUpdated(ArrayList<LaunchPoint> launchPoints) {
        Log.i(TAG, "onLaunchPointAddedOrUpdated:");
        this.mNotifyHandler.post(new LaunchPointsAdded(launchPoints));
    }

    public void onLaunchPointsRemoved(ArrayList<LaunchPoint> launchPoints) {
        Log.i(TAG, "onLaunchPointsRemoved");
        this.mNotifyHandler.post(new LaunchPointsRemoved(launchPoints));
    }

    public void onLaunchPointListGeneratorReady() {
        Log.i(TAG, "onLaunchPointListGeneratorReady");
        this.mLaunchPointGen.addToBlacklist(BuildConfig.APPLICATION_ID, false);

        if (this.mAppsRanker.isReady()) {
            refreshDataSetAsync();
        }
    }

    public void onSettingsChanged() {
    }

    public void onRankerReady() {
        Log.i(TAG, "onRankderReady");
        if (this.mLaunchPointGen.isReady()) {
            refreshDataSetAsync();
        }
    }

    public void onViewRecycled(AppViewHolder holder) {
        Log.i(TAG, "onViewRecycled");
        holder.itemView.setVisibility(View.VISIBLE);
    }

    public void refreshDataSetAsync() {
        Log.i(TAG, "refreshDataSetAsync->stackTrace:" + Log.getStackTraceString(new Throwable()));
        new RefreshTask().execute();
    }

    /* renamed from: AppsAdapter.1 */
    class LaunchPointsAdded implements Runnable {
        final List<LaunchPoint> val$launchPoints;

        LaunchPointsAdded(List<LaunchPoint> val$launchPoints) {
            this.val$launchPoints = val$launchPoints;
        }

        public void run() {
            boolean saveAppOrderChanges = false;

            for (int i = this.val$launchPoints.size() - 1; i >= 0; i--) {
                int j = AppsAdapter.this.mLaunchPoints.size() - 1;
                while (j >= 0) {
                    if (this.val$launchPoints.contains(AppsAdapter.this.mLaunchPoints.get(j))) {
                        AppsAdapter.this.mLaunchPoints.remove(j);
                        AppsAdapter.this.notifyItemRemoved(j);
                    } else {
                        j--;
                    }
                }
                AppsAdapter.this.notifyItemInserted(AppsAdapter.this.mAppsRanker.insertLaunchPoint(AppsAdapter.this.mLaunchPoints, this.val$launchPoints.get(i)));
                saveAppOrderChanges = true;
            }
            if (saveAppOrderChanges && AppsAdapter.this.mAppsRanker.getSortingMode() == AppsPreferences.SortingMode.FIXED) {
                AppsAdapter.this.saveAppOrderSnapshot();
            }
        }
    }

    /* renamed from: AppsAdapter.2 */
    class LaunchPointsRemoved implements Runnable {
        final List<LaunchPoint> val$launchPoints;

        LaunchPointsRemoved(List<LaunchPoint> val$launchPoints) {
            this.val$launchPoints = val$launchPoints;
        }

        public void run() {
            boolean saveAppOrderChanges = false;
            int itemRemovedAt = -1;
            for (int j = AppsAdapter.this.mLaunchPoints.size() - 1; j >= 0; j--) {
                int i;
                for (i = this.val$launchPoints.size() - 1; i >= 0; i--) {
                    if (AppsAdapter.this.mLaunchPoints.get(j).equals(this.val$launchPoints.get(i)) && itemRemovedAt == -1) {
                        this.val$launchPoints.get(i).cancelPendingOperations(AppsAdapter.this.mContext);
                        this.val$launchPoints.remove(i);
                        saveAppOrderChanges = true;
                        itemRemovedAt = j;
                        break;
                    }
                }
            }
            if (saveAppOrderChanges && AppsAdapter.this.mAppsRanker.getSortingMode() == AppsPreferences.SortingMode.FIXED) {
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
                    int i;
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
    }

    public List<AppCategory> getAppCategories() {
        return mIncludedCategories;
    }
}
