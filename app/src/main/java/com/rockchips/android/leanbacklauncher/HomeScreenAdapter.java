package com.rockchips.android.leanbacklauncher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v17.leanback.widget.OnChildSelectedListener;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLayoutChangeListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

import com.rockchips.android.leanbacklauncher.HomeScrollManager.HomeScrollFractionListener;
import com.rockchips.android.leanbacklauncher.R;
import com.rockchips.android.leanbacklauncher.apps.AppsAdapter;
import com.rockchips.android.leanbacklauncher.apps.AppsRanker;
import com.rockchips.android.leanbacklauncher.apps.AppsUpdateListener;
import com.rockchips.android.leanbacklauncher.apps.ConnectivityListener;
import com.rockchips.android.leanbacklauncher.apps.ConnectivityListener.Listener;
import com.rockchips.android.leanbacklauncher.apps.LaunchPointListGenerator;
import com.rockchips.android.leanbacklauncher.apps.OnEditModeChangedListener;
import com.rockchips.android.leanbacklauncher.apps.SettingsAdapter;
import com.rockchips.android.leanbacklauncher.inputs.InputsAdapter;
import com.rockchips.android.leanbacklauncher.notifications.HomeScreenMessaging;
import com.rockchips.android.leanbacklauncher.notifications.HomeScreenView;
import com.rockchips.android.leanbacklauncher.notifications.NotificationRowView;
import com.rockchips.android.leanbacklauncher.notifications.NotificationsAdapter;
import com.rockchips.android.leanbacklauncher.notifications.NotificationsServiceAdapter;
import com.rockchips.android.leanbacklauncher.notifications.PartnerAdapter;
import com.rockchips.android.leanbacklauncher.util.Partner;
import com.rockchips.android.leanbacklauncher.util.Preconditions;
import com.rockchips.android.leanbacklauncher.widget.EditModeView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class HomeScreenAdapter extends Adapter<HomeScreenAdapter.HomeViewHolder> implements OnChildSelectedListener, HomeScreenRow.RowChangeListener, Listener, OnEditModeChangedListener {
    private static final String TAG = "HomeScreenAdapter";
    private View mActiveItem;
    private ArrayList<HomeScreenRow> mAllRowsList;
    private final AppsUpdateListener mAppRefresher;
    private final AppsRanker mAppsRanker;
    private ConnectivityListener mConnectivityListener;
    private OnEditModeChangedListener mEditListener;
    private EditModeView mEditModeView;
    private final SparseArray<View> mHeaders;
    private HomeScreenMessaging mHomeScreenMessaging;
    private final LayoutInflater mInflater;
    private InputsAdapter mInputsAdapter;
    private final LaunchPointListGenerator mLaunchPointListGenerator;
    private final MainActivity mMainActivity;
    private final Partner mPartner;
    private final PartnerAdapter mPartnerAdapter;
    private BroadcastReceiver mReceiver;
    private final NotificationsAdapter mRecommendationsAdapter;
    private final HomeScrollManager mScrollManager;
    private SearchOrbView mSearch;
    private final SettingsAdapter mSettingsAdapter;
    private ArrayList<HomeScreenRow> mVisRowsList;

    /* renamed from: HomeScreenAdapter.1 */
    class C01551 extends BroadcastReceiver {
        final /* synthetic */ Adapter val$adapter;

        C01551(Adapter val$adapter) {
            this.val$adapter = val$adapter;
        }

        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.intent.action.USER_PRESENT") && (this.val$adapter instanceof NotificationsServiceAdapter)) {
                ((NotificationsServiceAdapter) this.val$adapter).reregisterListener();
                HomeScreenAdapter.this.mMainActivity.unregisterReceiver(this);
                HomeScreenAdapter.this.mReceiver = null;
            }
        }
    }

    /* renamed from: HomeScreenAdapter.2 */
    class C01562 implements OnLayoutChangeListener {
        final /* synthetic */ HomeViewHolder val$holder;

        C01562(HomeViewHolder val$holder) {
            this.val$holder = val$holder;
        }

        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
            v.removeOnLayoutChangeListener(this);
            if (HomeScreenAdapter.this.mMainActivity.isEditAnimationInProgress()) {
                HomeScreenAdapter.this.mMainActivity.includeInEditAnimation(this.val$holder.itemView);
            } else if (!(this.val$holder.itemView instanceof ActiveFrame)) {
            } else {
                if (HomeScreenAdapter.this.mMainActivity.isInEditMode()) {
                    HomeScreenAdapter.this.setActiveFrameChildrenAlpha((ActiveFrame) this.val$holder.itemView, 0.0f);
                    return;
                }
                HomeScreenAdapter.this.setActiveFrameChildrenAlpha((ActiveFrame) this.val$holder.itemView, 1.0f);
                HomeScreenAdapter.this.beginEditModeForPendingRow((ActiveFrame) this.val$holder.itemView);
            }
        }
    }

    static final class HomeViewHolder extends ViewHolder {
        HomeViewHolder(View itemView) {
            super(itemView);
        }
    }

    private static final class ListComparator implements Comparator<HomeScreenRow> {
        private ListComparator() {
        }

        public int compare(HomeScreenRow lhs, HomeScreenRow rhs) {
            return lhs.getPosition() - rhs.getPosition();
        }
    }

    public HomeScreenAdapter(MainActivity context, HomeScrollManager scrollMgr, LaunchPointListGenerator launchPointListGenerator, NotificationsAdapter notificationsAdapter, EditModeView editModeView, AppsRanker appsRanker) {
        this.mHeaders = new SparseArray(7);
        this.mAllRowsList = new ArrayList(7);
        this.mVisRowsList = new ArrayList(7);
        this.mMainActivity = (MainActivity) Preconditions.checkNotNull(context);
        this.mScrollManager = (HomeScrollManager) Preconditions.checkNotNull(scrollMgr);
        this.mLaunchPointListGenerator = (LaunchPointListGenerator) Preconditions.checkNotNull(launchPointListGenerator);
        this.mAppsRanker = appsRanker;
        this.mPartner = Partner.get(this.mMainActivity);
        this.mInflater = LayoutInflater.from(context);
        this.mAppRefresher = new AppsUpdateListener(this.mMainActivity, this.mLaunchPointListGenerator, this.mAppsRanker);
        this.mRecommendationsAdapter = notificationsAdapter;
        this.mConnectivityListener = new ConnectivityListener(context, this);
        this.mSettingsAdapter = new SettingsAdapter(this.mMainActivity, this.mLaunchPointListGenerator, this.mConnectivityListener, appsRanker);
        this.mRecommendationsAdapter.setNotificationCountListener(this.mSettingsAdapter);
        this.mEditModeView = editModeView;
        if (this.mPartner.isRowEnabled("partner_row")) {
            this.mPartnerAdapter = new PartnerAdapter(this.mMainActivity, this.mAppRefresher);
        } else {
            this.mPartnerAdapter = null;
        }
        setHasStableIds(true);
        buildRowList();
        Log.i(TAG, "mVisRowList->size:" + mVisRowsList.size());
        for(int i = 0; i < mVisRowsList.size(); ++i){
            Log.i(TAG, "type:" + mVisRowsList.get(i).getType() + "  position:" + mVisRowsList.get(i).getPosition() );
        }
        Log.i(TAG, "==================================");
        Log.i(TAG, "mAllRowsList->size:" + mAllRowsList.size());
        for(int i = 0; i < mAllRowsList.size(); ++i){
            Log.i(TAG, "type:" + mAllRowsList.get(i).getType() + "  position:" + mAllRowsList.get(i).getPosition() );
        }
        this.mLaunchPointListGenerator.refreshLaunchPointList();
        this.mConnectivityListener.start();
    }

    public void unregisterReceivers() {
        if (this.mReceiver != null) {
            this.mMainActivity.unregisterReceiver(this.mReceiver);
            this.mReceiver = null;
        }
        if (this.mConnectivityListener != null) {
            this.mConnectivityListener.stop();
        }
        if (this.mAppRefresher != null) {
            this.mAppRefresher.unregisterReceivers();
        }
        if (this.mInputsAdapter != null) {
            this.mInputsAdapter.unregisterReceivers();
        }
    }

    public void resetRowPositions(boolean smooth) {
        for (int i = 0; i < this.mAllRowsList.size(); i++) {
            if (((HomeScreenRow) this.mAllRowsList.get(i)).getRowView() instanceof ActiveFrame) {
                ((ActiveFrame) ((HomeScreenRow) this.mAllRowsList.get(i)).getRowView()).resetScrollPosition(smooth);
            }
        }
    }

    public void setRowAlphas(int alpha) {
        for (HomeScreenRow row : this.mAllRowsList) {
            View activeFrame = row.getRowView();
            if (activeFrame instanceof ActiveFrame) {
                for (int i = 0; i < ((ActiveFrame) activeFrame).getChildCount(); i++) {
                    View rowView = ((ActiveFrame) activeFrame).getChildAt(i);
                    if (!(rowView instanceof ActiveItemsRowView)) {
                        rowView.setAlpha((float) alpha);
                    } else if (!(((ActiveItemsRowView) rowView).getEditMode() || rowView.getAlpha() == ((float) alpha))) {
                        rowView.setAlpha((float) alpha);
                    }
                }
            }
        }
    }

    public int getRowIndex(int rowType) {
        int index = -1;
        int size = this.mVisRowsList.size();
        for (int i = 0; i < size; i++) {
            if (((HomeScreenRow) this.mVisRowsList.get(i)).getType() == rowType) {
                index = i;
            }
        }
        return index;
    }

    public void onConnectivityChange() {
        this.mSettingsAdapter.onConnectivityChange();
        if (this.mHomeScreenMessaging != null) {
            this.mHomeScreenMessaging.onConnectivityChange(ConnectivityListener.readConnectivity(this.mMainActivity));
        }
    }

    private void buildRowList() {
        Resources res = this.mMainActivity.getResources();
        String partnerFont = this.mPartner.getPartnerFontName();
        buildRow(0, 0, null, null, null, R.dimen.home_scroll_size_search, false);
        //推荐
        buildRow(8, 1,  res.getString(R.string.home_collection), null, partnerFont, R.dimen.home_scroll_size_apps, false);
     /*   //视频
        buildRow(9, 2,  res.getString(R.string.home_video), null, partnerFont, R.dimen.home_scroll_size_apps, false);
        //音乐
        buildRow(10, 3,  res.getString(R.string.home_music), null, partnerFont, R.dimen.home_scroll_size_apps, false);*/
        //所有应用
        buildRow(3, 2,  res.getString(R.string.category_label_apps), this.mPartner.getRowIcon("apps_row"), partnerFont, R.dimen.home_scroll_size_apps, false);
        //设置
        buildRow(5, 3, this.mPartner.getRowTitle("settings_row", res.getString(R.string.category_label_settings)), this.mPartner.getRowIcon("settings_row"), partnerFont, R.dimen.home_scroll_size_settings, false);
        ListComparator comp = new ListComparator();
        Collections.sort(this.mAllRowsList, comp);
        Collections.sort(this.mVisRowsList, comp);
    }

    private void buildRow(int type, int position, String title, Drawable icon, String font, int scrollOffsetResId, boolean hideIfEmpty) {
        Log.i(TAG, "buildRow->type:" + type);
        Log.i(TAG, "buildRow->position:" + position);
        Log.i(TAG, "buildRow->title:" + title);
        Log.i(TAG, "buildRow->hideIfEmpty:" + hideIfEmpty);
        Log.i(TAG, "buildRow->font:" + font);
        HomeScreenRow row = new HomeScreenRow(type, position, hideIfEmpty);
        row.setHeaderInfo(title != null, title, icon, font);
        row.setAdapter(initAdapter(type));
        row.setViewScrollOffset(this.mMainActivity.getResources().getDimensionPixelOffset(scrollOffsetResId));
        addRowEntry(row);
    }

    private void addRowEntry(HomeScreenRow row) {
        this.mAllRowsList.add(row);
        row.setChangeListener(this);
/*       if (!(row.getType() == 3 || row.getType() == 4)) {
            if (row.getType() == 5) {
            }
            if (row.isVisible())
            }
        }*/
        this.mAppRefresher.addAppRow(row);
        if (row.isVisible()) {
            this.mVisRowsList.add(row);
        }


    }

    public void onRowVisibilityChanged(int position, boolean visible) {
        int i;
        Log.i(TAG, "onRowVisibilityChanged->position:" + position);
        Log.i(TAG, "onRowVisibilityChanged->visible:" + visible);
        if (visible) {
            int insertPoint = this.mVisRowsList.size();
            i = 0;
            while (i < this.mVisRowsList.size()) {
                if (((HomeScreenRow) this.mVisRowsList.get(i)).getPosition() != position) {
                    if (((HomeScreenRow) this.mVisRowsList.get(i)).getPosition() > position) {
                        insertPoint = i;
                        break;
                    }
                    i++;
                } else {
                    return;
                }
            }
            Log.i(TAG, "insertPosition:" + insertPoint);
            this.mVisRowsList.add(insertPoint, (HomeScreenRow) this.mAllRowsList.get(position));
            notifyItemInserted(insertPoint);
        } else {
            int pos = -1;
            for (i = 0; i < this.mVisRowsList.size(); i++) {
                if (((HomeScreenRow) this.mVisRowsList.get(i)).getPosition() == position) {
                    pos = i;
                    break;
                }
            }
            if (pos >= 0) {
                this.mVisRowsList.remove(pos);
                notifyItemRemoved(pos);
            }
        }
    }

    public void refreshAdapterData() {
        if (this.mAppRefresher != null) {
            this.mAppRefresher.refreshRows();
        }
        if (this.mInputsAdapter != null) {
            this.mInputsAdapter.refreshInputsData();
        }
    }

    public void animateSearchIn() {
        if (this.mSearch != null) {
            this.mSearch.animateIn();
        }
    }

    public long getItemId(int position) {
        return (long) ((HomeScreenRow) this.mVisRowsList.get(position)).getType();
    }

    public int getItemViewType(int position) {
        if (position >= this.mVisRowsList.size()) {
            return -1;
        }
        return ((HomeScreenRow) this.mVisRowsList.get(position)).getPosition();
    }

    public HomeViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        View view;
        HomeScreenRow row = (HomeScreenRow) this.mAllRowsList.get(position);
        switch (row.getType()) {
            case android.support.v7.preference.R.styleable.Preference_android_icon /*0*/:
                view = this.mInflater.inflate(R.layout.home_search_orb, parent, false);
                this.mHeaders.put(row.getType(), view);
                this.mSearch = (SearchOrbView) view;
                if (this.mSearch != null) {
                    this.mAppRefresher.setSearchPackageChangeListener(this.mSearch, this.mSearch.getSearchPackageName());
                    break;
                }
                break;
            case android.support.v7.recyclerview.R.styleable.RecyclerView_android_descendantFocusability /*1*/:
                view = this.mInflater.inflate(R.layout.home_notification_row, parent, false);
                NotificationRowView notifList = (NotificationRowView) view.findViewById(R.id.list);
                HomeScreenView homeScreenView = (HomeScreenView) view.findViewById(R.id.home_screen_messaging);
                if (!(notifList == null || homeScreenView == null)) {
                    initNotificationsRows(notifList, row.getAdapter(), homeScreenView.getHomeScreenMessaging());
                    break;
                }
            case android.support.v7.preference.R.styleable.Preference_android_layout /*3*/:
              /*  view = this.mInflater.inflate(R.layout.home_apps_row, parent, false);
                this.mHeaders.put(row.getType(), view.findViewById(R.id.header));
                if (view instanceof ActiveFrame) {
                    initAppRow((ActiveFrame) view, row);
                    break;
                }
                break;*/
            case android.support.v7.recyclerview.R.styleable.RecyclerView_layoutManager /*2*/:
            case android.support.v7.preference.R.styleable.Preference_android_title /*4*/:
            case android.support.v7.preference.R.styleable.Preference_android_selectable /*5*/:
            case android.support.v7.preference.R.styleable.Preference_android_key /*6*/:
            case 8:
            case 9:
            case 10:
                view = this.mInflater.inflate(R.layout.home_apps_row, parent, false);
                this.mHeaders.put(row.getType(), view.findViewById(R.id.header));
                if (view instanceof ActiveFrame) {
                    initAppRow((ActiveFrame) view, row);
                    break;
                }
                break;
            default:
                return null;
        }
        row.setRowView(view);
        view.setTag(Integer.valueOf(row.getType()));
        return new HomeViewHolder(view);
    }

    public void onBindViewHolder(HomeViewHolder holder, int position) {
    }

    public void onViewRecycled(HomeViewHolder holder) {
        super.onViewRecycled(holder);
    }

    public boolean onFailedToRecycleView(HomeViewHolder holder) {
        if (holder.itemView instanceof ActiveFrame) {
            resetRowAdapter((ActiveFrame) holder.itemView);
        }
        return super.onFailedToRecycleView(holder);
    }

    public int getItemCount() {
        return this.mVisRowsList.size();
    }

    View[] getRowHeaders() {
        int n = this.mHeaders.size();
        View[] headers = new View[n];
        for (int i = 0; i < n; i++) {
            headers[i] = (View) this.mHeaders.valueAt(i);
        }
        return headers;
    }

    public void onEditModeChanged(boolean editMode) {
        if (this.mEditListener != null) {
            this.mEditListener.onEditModeChanged(editMode);
        }
    }

    public ArrayList<HomeScreenRow> getAllRows() {
        return new ArrayList(this.mAllRowsList);
    }

    public void setRows(){

    }

    public void setOnEditModeChangedListener(OnEditModeChangedListener listener) {
        this.mEditListener = listener;
    }

    public NotificationsAdapter getRecommendationsAdapter() {
        return this.mRecommendationsAdapter;
    }

    private void initNotificationsRows(NotificationRowView list, Adapter<?> adapter, HomeScreenMessaging homeScreenMessaging) {
        Log.i(TAG, "initNotificationsRows");
        list.setHasFixedSize(true);
        list.setAdapter(adapter);
        this.mHomeScreenMessaging = homeScreenMessaging;
        list.setItemMargin(this.mMainActivity.getResources().getDimensionPixelOffset(R.dimen.inter_card_spacing));
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.USER_PRESENT");
        if (this.mReceiver == null) {
            this.mReceiver = new C01551(adapter);
            this.mMainActivity.registerReceiver(this.mReceiver, filter);
        }
    }

    private void initAppRow(ActiveFrame group, HomeScreenRow row) {
        if (group != null) {
            Resources res = this.mMainActivity.getResources();
            group.setTag(Integer.valueOf(2131427388));
            ActiveItemsRowView list = (ActiveItemsRowView) group.findViewById(R.id.list);
            list.setEditModeView(this.mEditModeView);
            list.addEditModeListener(this.mEditModeView);
            list.addEditModeListener(this);
            list.setHasFixedSize(true);
            list.setAdapter(row.getAdapter());
            if (row.hasHeader()) {
                list.setContentDescription(row.getTitle());
                ((TextView) group.findViewById(R.id.title)).setText(row.getTitle());
                if (!TextUtils.isEmpty(row.getFontName())) {
                    Typeface font = Typeface.create(row.getFontName(), 0);
                    if (font != null) {
                        ((TextView) group.findViewById(R.id.title)).setTypeface(font);
                    }
                }
                Drawable icon = row.getIcon();
                ImageView iconView = (ImageView) group.findViewById(R.id.icon);
                if (icon != null) {
                    iconView.setImageDrawable(icon);
                    iconView.setVisibility(View.VISIBLE);
                } else {
                    iconView.setVisibility(View.GONE);
                }
            }
            LayoutParams lp = list.getLayoutParams();
            int cardSpacing = res.getDimensionPixelOffset(R.dimen.inter_card_spacing);
            group.setScaledWhenUnfocused(true);
            int type = row.getType();
            switch (type) {
                case android.support.v7.recyclerview.R.styleable.RecyclerView_layoutManager /*2*/:
                case android.support.v7.preference.R.styleable.Preference_android_layout /*3*/:
                case android.support.v7.preference.R.styleable.Preference_android_title /*4*/:
                case android.support.v7.preference.R.styleable.Preference_android_key /*6*/:
                case 8:
                    int rowHeight = (int) res.getDimension(R.dimen.banner_height);
                    list.setIsNumRowsAdjustable(true);
                    list.adjustNumRows(res.getInteger(R.integer.max_num_banner_rows), cardSpacing, rowHeight);
                    break;
                case android.support.v7.preference.R.styleable.Preference_android_selectable /*5*/:
                    lp.height = (int) res.getDimension(R.dimen.settings_row_height);
                    break;
            }
            if (type == 3 || type == 4 || type == 8) {
                list.setIsRowEditable(true);
            }
            list.setItemMargin(cardSpacing);
        }
    }

    private void beginEditMode(ActiveItemsRowView rowView) {
        if (rowView.getChildCount() > 0) {
            rowView.setEditModePending(false);
            View child = rowView.getChildAt(0);
            child.requestFocus();
            child.setSelected(true);
            rowView.setEditMode(true);
        }
    }

    public void prepareEditMode(int rowType) {
        for (HomeScreenRow row : this.mAllRowsList) {
            if (row.getType() == rowType) {
                View activeFrame = row.getRowView();
                if (activeFrame instanceof ActiveFrame) {
                    for (int i = 0; i < ((ActiveFrame) activeFrame).getChildCount(); i++) {
                        View rowView = ((ActiveFrame) activeFrame).getChildAt(i);
                        if ((rowView instanceof ActiveItemsRowView) && ((ActiveItemsRowView) rowView).getChildCount() > 0) {
                            if (rowView.isAttachedToWindow()) {
                                beginEditMode((ActiveItemsRowView) rowView);
                            } else {
                                ((ActiveItemsRowView) rowView).setEditModePending(true);
                            }
                        }
                    }
                }
            }
        }
    }

    private void resetRowAdapter(ActiveFrame group) {
        ((ActiveItemsRowView) group.findViewById(R.id.list)).setAdapter(null);
    }

    private Adapter<?> initAdapter(int type) {
        switch (type) {
            case android.support.v7.recyclerview.R.styleable.RecyclerView_android_descendantFocusability /*1*/:
                return this.mRecommendationsAdapter;
            case android.support.v7.recyclerview.R.styleable.RecyclerView_layoutManager /*2*/:
                return this.mPartnerAdapter;
            case  android.support.v7.preference.R.styleable.Preference_android_layout /*3*/:
                return new AppsAdapter(this.mMainActivity, 0, this.mLaunchPointListGenerator, this.mAppsRanker, this.mRecommendationsAdapter);
            case android.support.v7.preference.R.styleable.Preference_android_title /*4*/:
                return new AppsAdapter(this.mMainActivity, 1, this.mLaunchPointListGenerator, this.mAppsRanker, null);
            case 8:
                return new AppsAdapter(this.mMainActivity, 4, this.mLaunchPointListGenerator, this.mAppsRanker, null);
            case 9:
                return new AppsAdapter(this.mMainActivity, 5, this.mLaunchPointListGenerator, this.mAppsRanker, null);
            case 10:
                return new AppsAdapter(this.mMainActivity, 6, this.mLaunchPointListGenerator, this.mAppsRanker, null);
            case android.support.v7.preference.R.styleable.Preference_android_selectable /*5*/:
                return this.mSettingsAdapter;
            case android.support.v7.preference.R.styleable.Preference_android_key /*6*/:
                Adapter<?> adapter = new InputsAdapter(this.mMainActivity, new InputsAdapter.Configuration(this.mPartner.showPhysicalTunersSeparately(), this.mPartner.disableDiconnectedInputs(), this.mPartner.getStateIconFromTVInput()));
                this.mInputsAdapter = (InputsAdapter) adapter;
                return adapter;
            default:
                return null;
        }
    }

    public void onChildSelected(ViewGroup parent, View child, int position, long id) {
        Log.i(TAG, "onChildSelect");
        if (child == this.mActiveItem) {
            Log.i(TAG, "onChildSelect 1");
            return;
        }
        if (child == null) {
            Log.i(TAG, "onChildSelect 2");
            this.mActiveItem.setActivated(false);
            this.mActiveItem = null;
            return;
        }
        if (this.mActiveItem != null) {
            Log.i(TAG, "onChildSelect 3");
            this.mActiveItem.setActivated(false);
        }
        this.mActiveItem = child;
        if (child != null) {
            Log.i(TAG, "onChildSelect 4");
            this.mActiveItem.setActivated(true);
        }
    }

    public int getScrollOffset(int index) {
        if (index >= 0 || index < this.mVisRowsList.size()) {
            return ((HomeScreenRow) this.mVisRowsList.get(index)).getRowScrollOffset();
        }
        return 0;
    }

    public void onReconnectToRecommendationsService() {
        this.mRecommendationsAdapter.reregisterListener();
    }

    public void onInitUi() {
        this.mRecommendationsAdapter.onInitUi();
        if (this.mPartnerAdapter != null) {
            this.mPartnerAdapter.onInitUi();
        }
    }

    public void onUiVisible() {
        this.mRecommendationsAdapter.onUiVisible();
        if (this.mPartnerAdapter != null) {
            this.mPartnerAdapter.onUiVisible();
        }
    }

    public void onUiInvisible() {
        this.mRecommendationsAdapter.onUiInvisible();
        if (this.mPartnerAdapter != null) {
            this.mPartnerAdapter.onUiInvisible();
        }
    }

    public void onStopUi() {
        this.mRecommendationsAdapter.onStopUi();
        if (this.mPartnerAdapter != null) {
            this.mPartnerAdapter.onStopUi();
        }
    }

    public void onViewDetachedFromWindow(HomeViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
        if (holder.itemView instanceof HomeScrollFractionListener) {
            this.mScrollManager.removeHomeScrollListener((HomeScrollFractionListener) holder.itemView);
        }
        this.mMainActivity.excludeFromEditAnimation(holder.itemView);
    }

    public void onViewAttachedToWindow(HomeViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if (holder.itemView instanceof HomeScrollFractionListener) {
            this.mScrollManager.addHomeScrollListener((HomeScrollFractionListener) holder.itemView);
        }
        holder.itemView.addOnLayoutChangeListener(new C01562(holder));
    }

    private void beginEditModeForPendingRow(ActiveFrame frame) {
        for (int i = 0; i < frame.getChildCount(); i++) {
            View rowView = frame.getChildAt(i);
            if ((rowView instanceof ActiveItemsRowView) && ((ActiveItemsRowView) rowView).isEditModePending()) {
                beginEditMode((ActiveItemsRowView) rowView);
            }
        }
    }

    private void setActiveFrameChildrenAlpha(ActiveFrame frame, float alpha) {
        for (int i = 0; i < frame.getChildCount(); i++) {
            frame.getChildAt(i).setAlpha(alpha);
        }
    }

    public void sortRowsIfNeeded(boolean force) {
        for (int i = 0; i < this.mAllRowsList.size(); i++) {
            Adapter<?> adapter = ((HomeScreenRow) this.mAllRowsList.get(i)).getAdapter();
            if (adapter instanceof AppsAdapter) {
                ((AppsAdapter) adapter).sortItemsIfNeeded(force);
            }
        }
    }

    public ArrayList<HomeScreenRow> getVisRowList(){
        return mVisRowsList;
    }
}
