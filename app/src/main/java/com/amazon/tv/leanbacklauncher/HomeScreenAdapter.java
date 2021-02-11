package com.amazon.tv.leanbacklauncher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
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

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.amazon.tv.firetv.leanbacklauncher.apps.AppCategory;
import com.amazon.tv.firetv.leanbacklauncher.apps.FavoritesAdapter;
import com.amazon.tv.firetv.leanbacklauncher.apps.RowPreferences;
import com.amazon.tv.firetv.leanbacklauncher.apps.RowType;
import com.amazon.tv.leanbacklauncher.HomeScreenRow.RowChangeListener;
import com.amazon.tv.leanbacklauncher.HomeScrollManager.HomeScrollFractionListener;
import com.amazon.tv.leanbacklauncher.apps.AppsAdapter;
import com.amazon.tv.leanbacklauncher.apps.AppsManager;
import com.amazon.tv.leanbacklauncher.apps.ConnectivityListener;
import com.amazon.tv.leanbacklauncher.apps.OnEditModeChangedListener;
import com.amazon.tv.leanbacklauncher.apps.SettingsAdapter;
import com.amazon.tv.leanbacklauncher.inputs.InputsAdapter;
import com.amazon.tv.leanbacklauncher.notifications.HomeScreenMessaging;
import com.amazon.tv.leanbacklauncher.notifications.HomeScreenView;
import com.amazon.tv.leanbacklauncher.notifications.NotificationRowView;
import com.amazon.tv.leanbacklauncher.notifications.NotificationsAdapter;
import com.amazon.tv.leanbacklauncher.notifications.NotificationsServiceAdapter;
import com.amazon.tv.leanbacklauncher.notifications.PartnerAdapter;
import com.amazon.tv.leanbacklauncher.util.Preconditions;
import com.amazon.tv.leanbacklauncher.widget.EditModeView;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class HomeScreenAdapter extends Adapter<HomeScreenAdapter.HomeViewHolder> implements RowChangeListener, ConnectivityListener.Listener, OnEditModeChangedListener {
    private int mActiveItemIndex = -1;
    private ArrayList<HomeScreenRow> mAllRowsList = new ArrayList(7);
    private final AppsManager mAppsManager;
    private Drawable mAssistantIcon;
    private String[] mAssistantSuggestions;
    private ConnectivityListener mConnectivityListener;
    private OnEditModeChangedListener mEditListener;
    private EditModeView mEditModeView;
    private final SparseArray<View> mHeaders = new SparseArray(7);
    private HomeScreenMessaging mHomeScreenMessaging;
    private final LayoutInflater mInflater;
    private InputsAdapter mInputsAdapter;
    private final MainActivity mMainActivity;
    private final PartnerAdapter mPartnerAdapter;
    private BroadcastReceiver mReceiver;
    private final NotificationsAdapter mRecommendationsAdapter;
    private final HomeScrollManager mScrollManager;
    private SearchOrbView mSearch;
    private final SettingsAdapter mSettingsAdapter;
    private ArrayList<HomeScreenRow> mVisRowsList = new ArrayList(7);
    private Adapter<?> mNotificationsAdapter;

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

    public HomeScreenAdapter(MainActivity context, HomeScrollManager scrollMgr, NotificationsAdapter notificationsAdapter, EditModeView editModeView) {
        this.mMainActivity = Preconditions.checkNotNull(context);
        this.mScrollManager = Preconditions.checkNotNull(scrollMgr);
        this.mAppsManager = AppsManager.getInstance(context);
        this.mInflater = LayoutInflater.from(context);
        this.mRecommendationsAdapter = notificationsAdapter;
        this.mConnectivityListener = new ConnectivityListener(context, this);
        this.mSettingsAdapter = new SettingsAdapter(this.mMainActivity, this.mConnectivityListener);
        this.mEditModeView = editModeView;

        this.mPartnerAdapter = null;

        setHasStableIds(true);
        buildRowList();
        this.mAppsManager.refreshLaunchPointList();
        this.mAppsManager.registerUpdateReceivers();
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
        if (this.mAppsManager != null) {
            this.mAppsManager.unregisterUpdateReceivers();
        }
        if (this.mInputsAdapter != null) {
            this.mInputsAdapter.unregisterReceivers();
        }
    }

    // FIXME: WRONG FOCUS
    public void resetRowPositions(boolean smooth) {
        for (int i = 0; i < this.mAllRowsList.size(); i++) {
            if (this.mAllRowsList.get(i).getRowView() instanceof ActiveFrame) {
                ((ActiveFrame) this.mAllRowsList.get(i).getRowView()).resetScrollPosition(smooth);
            }
        }
    }

    public void setRowAlphas(int alpha) {
        Iterator it = this.mAllRowsList.iterator();
        while (it.hasNext()) {
            View activeFrame = ((HomeScreenRow) it.next()).getRowView();
            if (activeFrame instanceof ActiveFrame) {
                for (int i = 0; i < ((ActiveFrame) activeFrame).getChildCount(); i++) {
                    View rowView = ((ActiveFrame) activeFrame).getChildAt(i);
                    if (!(rowView instanceof EditableAppsRowView)) {
                        rowView.setAlpha((float) alpha);
                    } else if (!(((EditableAppsRowView) rowView).getEditMode() || rowView.getAlpha() == ((float) alpha))) {
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
            if (this.mVisRowsList.get(i).getType().getCode() == rowType) {
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
        boolean hasInputsRow = true; //this.mPartner.isRowEnabled("inputs_row");

        // TODO
        this.mAppsManager.setExcludeChannelActivities(hasInputsRow);

        int position = 0;

        Set<AppCategory> enabledCategories = RowPreferences.getEnabledCategories(mMainActivity);

        buildRow(RowType.SEARCH, position++, null, null, null, R.dimen.home_scroll_size_search, false);

        if (RowPreferences.areRecommendationsEnabled(mMainActivity)) {
            buildRow(RowType.NOTIFICATIONS, position++, null, null, null, R.dimen.home_scroll_size_notifications, false);
        }

        if (RowPreferences.areFavoritesEnabled(mMainActivity)) {
            buildRow(RowType.FAVORITES, position++, res.getString(R.string.category_label_favorites), null, null, R.dimen.home_scroll_size_apps, true);
        }

        if (enabledCategories.contains(AppCategory.VIDEO)) {
            buildRow(RowType.VIDEO, position++, res.getString(R.string.category_label_videos), null, null, R.dimen.home_scroll_size_video, true);
        }

        if (enabledCategories.contains(AppCategory.MUSIC)) {
            buildRow(RowType.MUSIC, position++, res.getString(R.string.category_label_music), null, null, R.dimen.home_scroll_size_music, true);
        }

        if (enabledCategories.contains(AppCategory.GAME)) {
            buildRow(RowType.GAMES, position++, res.getString(R.string.category_label_games), null, null, R.dimen.home_scroll_size_games, true);
        }

        buildRow(RowType.APPS, position++, res.getString(R.string.category_label_apps), null, null, R.dimen.home_scroll_size_apps, true);

        if (RowPreferences.areInputsEnabled(mMainActivity)) {
            buildRow(RowType.INPUTS, position++, res.getString(R.string.category_label_inputs), null, null, R.dimen.home_scroll_size_inputs, true);
        }

        buildRow(RowType.SETTINGS, position, res.getString(R.string.category_label_settings), null, null, R.dimen.home_scroll_size_settings, false);
        // TODO Notifications view... buildRow(RowType.ACTUAL_NOTIFICATIONS, position++, null, null, null, R.dimen.home_scroll_size_notifications, false);

        ListComparator comp = new ListComparator();
        Collections.sort(this.mAllRowsList, comp);
        Collections.sort(this.mVisRowsList, comp);
    }

    private void buildRow(RowType type, int position, String title, Drawable icon, String font, int scrollOffsetResId, boolean hideIfEmpty) {
        HomeScreenRow row = new HomeScreenRow(type, position, hideIfEmpty);
        row.setHeaderInfo(title != null, title, icon, font);
        row.setAdapter(initAdapter(type));
        row.setViewScrollOffset(this.mMainActivity.getResources().getDimensionPixelOffset(scrollOffsetResId));
        addRowEntry(row);
    }

    private void addRowEntry(HomeScreenRow row) {
        this.mAllRowsList.add(row);
        row.setChangeListener(this);
        if (row.getType() != RowType.NOTIFICATIONS && row.getType() != RowType.ACTUAL_NOTIFICATIONS && row.getType() != RowType.SEARCH) {
            this.mAppsManager.addAppRow(row);
        }
        if (row.isVisible()) {
            this.mVisRowsList.add(row);
        }
    }

    public void onRowVisibilityChanged(int position, boolean visible) {
        int i;
        if (visible) {
            int insertPoint = this.mVisRowsList.size();
            i = 0;
            while (i < this.mVisRowsList.size()) {
                if (this.mVisRowsList.get(i).getPosition() != position) {
                    if (this.mVisRowsList.get(i).getPosition() > position) {
                        insertPoint = i;
                        break;
                    }
                    i++;
                } else {
                    return;
                }
            }
            this.mVisRowsList.add(insertPoint, this.mAllRowsList.get(position));
            notifyItemInserted(insertPoint);
            return;
        }
        int pos = -1;
        for (i = 0; i < this.mVisRowsList.size(); i++) {
            if (this.mVisRowsList.get(i).getPosition() == position) {
                pos = i;
                break;
            }
        }
        if (pos >= 0) {
            this.mVisRowsList.remove(pos);
            notifyItemRemoved(pos);
        }
    }

    public void refreshAdapterData() {
        if (this.mAppsManager != null) {
            this.mAppsManager.refreshRows();
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
        return this.mVisRowsList.get(position).getType().getCode();
    }

    public int getItemViewType(int position) {
        if (position >= this.mVisRowsList.size()) {
            return -1;
        }
        return this.mVisRowsList.get(position).getPosition();
    }

    public HomeViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        View view;
        HomeScreenRow row = this.mAllRowsList.get(position);
        switch (row.getType()) {
            case SEARCH:
                view = this.mInflater.inflate(R.layout.home_search_orb, parent, false);
                this.mHeaders.put(row.getType().getCode(), view);
                this.mSearch = (SearchOrbView) view;
                if (this.mSearch != null) {
                    this.mSearch.updateAssistantIcon(this.mAssistantIcon);
                    this.mSearch.updateSearchSuggestions(this.mAssistantSuggestions);
                    this.mAppsManager.setSearchPackageChangeListener(this.mSearch, this.mSearch.getSearchPackageName());
                    break;
                }
                break;
            case NOTIFICATIONS:
                view = this.mInflater.inflate(R.layout.home_notification_row, parent, false);
                NotificationRowView notifList = view.findViewById(R.id.list);
                HomeScreenView homeScreenView = view.findViewById(R.id.home_screen_messaging);
                if (!(notifList == null || homeScreenView == null)) {
                    initNotificationsRows(notifList, row.getAdapter(), homeScreenView.getHomeScreenMessaging());
                    break;
                }
            case PARTNER: // todo?
            case SETTINGS:
            case INPUTS:
                view = this.mInflater.inflate(R.layout.home_active_items_row, parent, false);
                this.mHeaders.put(row.getType().getCode(), view.findViewById(R.id.header));
                if (view instanceof ActiveFrame) {
                    initAppRow((ActiveFrame) view, row);
                    break;
                }
                break;
            case APPS:
            case GAMES:
            case FAVORITES:
            case MUSIC:
            case VIDEO:
                view = this.mInflater.inflate(R.layout.home_apps_row, parent, false);
                this.mHeaders.put(row.getType().getCode(), view.findViewById(R.id.header));
                if (view instanceof ActiveFrame) {
                    initAppRow((ActiveFrame) view, row);
                    break;
                }
                break;

            default:
                return null;
        }
        row.setRowView(view);
        view.setTag(row.getType().getCode());
        return new HomeViewHolder(view);
    }

    public void onBindViewHolder(HomeViewHolder holder, int position) {
        holder.itemView.setActivated(position == this.mActiveItemIndex);
    }

    public boolean onFailedToRecycleView(HomeViewHolder holder) {
        if (holder.itemView instanceof ActiveFrame) {
            resetRowAdapter((ActiveFrame) holder.itemView);
        }
        return false;
    }

    public int getItemCount() {
        return this.mVisRowsList.size();
    }

    View[] getRowHeaders() {
        int n = this.mHeaders.size();
        View[] headers = new View[n];
        for (int i = 0; i < n; i++) {
            headers[i] = this.mHeaders.valueAt(i);
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

    public void setOnEditModeChangedListener(OnEditModeChangedListener listener) {
        this.mEditListener = listener;
    }

    public NotificationsAdapter getRecommendationsAdapter() {
        return this.mRecommendationsAdapter;
    }

    private void initNotificationsRows(NotificationRowView list, final Adapter<?> adapter, HomeScreenMessaging homeScreenMessaging) {
        list.setHasFixedSize(true);
        list.setAdapter(adapter);
        this.mHomeScreenMessaging = homeScreenMessaging;
        list.setItemSpacing(this.mMainActivity.getResources().getDimensionPixelOffset(R.dimen.inter_card_spacing));
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.USER_PRESENT");
        if (this.mReceiver == null) {
            this.mReceiver = new BroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {
                    if (intent.getAction().equals("android.intent.action.USER_PRESENT") && (adapter instanceof NotificationsServiceAdapter)) {
                        ((NotificationsServiceAdapter) adapter).reregisterListener();
                        HomeScreenAdapter.this.mMainActivity.unregisterReceiver(this);
                        HomeScreenAdapter.this.mReceiver = null;
                    }
                }
            };
            this.mMainActivity.registerReceiver(this.mReceiver, filter);
        }
    }

    private void initAppRow(ActiveFrame group, HomeScreenRow row) {
        if (group != null) {
            Resources res = this.mMainActivity.getResources();
            group.setTag(R.integer.tag_has_header);
            ActiveItemsRowView list = group.findViewById(R.id.list);

            if (list instanceof EditableAppsRowView) {
                EditableAppsRowView editableList = (EditableAppsRowView) list;
                editableList.setEditModeView(this.mEditModeView);
                editableList.addEditModeListener(this.mEditModeView);
                editableList.addEditModeListener(this);
            }

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
                ImageView iconView = group.findViewById(R.id.icon);
                if (icon != null) {
                    iconView.setImageDrawable(icon);
                    iconView.setVisibility(View.VISIBLE);
                } else {
                    iconView.setVisibility(View.GONE);
                }
            }

            LayoutParams lp = list.getLayoutParams();

            int cardSpacing = res.getDimensionPixelOffset(R.dimen.inter_card_spacing);
            int numMinRows = res.getInteger(R.integer.min_num_banner_rows);
            int numMaxRows = res.getInteger(R.integer.max_num_banner_rows);
            int size = RowPreferences.getBannersSize(mMainActivity);
            int rowHeight = (int) res.getDimension(R.dimen.banner_height) * size / 100;
            int[] constraints;
            int maxApps = RowPreferences.getAppsMax(mMainActivity);

            group.setScaledWhenUnfocused(true);

            switch (row.getType()) {
                case INPUTS:
                case PARTNER:
                    break;
                case FAVORITES:
                    constraints = RowPreferences.getFavoriteRowConstraints(mMainActivity);
                    if (row.getAdapter().getItemCount() > maxApps)
                        numMaxRows = constraints[1];
                    else
                        numMaxRows = constraints[0];
                    // APPLY
                    list.setIsNumRowsAdjustable(true);
                    list.adjustNumRows(numMaxRows, cardSpacing, rowHeight);
                    break;
                case GAMES:
                    constraints = RowPreferences.getRowConstraints(AppCategory.GAME, mMainActivity);
                    if (row.getAdapter().getItemCount() > maxApps)
                        numMaxRows = constraints[1];
                    else
                        numMaxRows = constraints[0];
                    // APPLY
                    list.setIsNumRowsAdjustable(true);
                    list.adjustNumRows(numMaxRows, cardSpacing, rowHeight);
                    break;
                case MUSIC:
                    constraints = RowPreferences.getRowConstraints(AppCategory.MUSIC, mMainActivity);
                    if (row.getAdapter().getItemCount() > maxApps)
                        numMaxRows = constraints[1];
                    else
                        numMaxRows = constraints[0];
                    // APPLY
                    list.setIsNumRowsAdjustable(true);
                    list.adjustNumRows(numMaxRows, cardSpacing, rowHeight);
                    break;
                case VIDEO:
                    constraints = RowPreferences.getRowConstraints(AppCategory.VIDEO, mMainActivity);
                    if (row.getAdapter().getItemCount() > maxApps)
                        numMaxRows = constraints[1];
                    else
                        numMaxRows = constraints[0];
                    // APPLY
                    list.setIsNumRowsAdjustable(true);
                    list.adjustNumRows(numMaxRows, cardSpacing, rowHeight);
                    break;
                case APPS:
                    constraints = RowPreferences.getAllAppsConstraints(mMainActivity);
                    if (row.getAdapter().getItemCount() > maxApps)
                        numMaxRows = constraints[1];
                    else
                        numMaxRows = constraints[0];
                    // APPLY
                    list.setIsNumRowsAdjustable(true);
                    list.adjustNumRows(numMaxRows, cardSpacing, rowHeight);
                    break;
                case SETTINGS:
                    lp.height = (int) res.getDimension(R.dimen.settings_row_height);
                    break;
            }

            list.setItemSpacing(cardSpacing);
        }
    }

    private void beginEditMode(EditableAppsRowView rowView) {
        if (rowView.getChildCount() > 0) {
            rowView.setEditModePending(false);
            View child = rowView.getChildAt(0);
            child.requestFocus();
            child.setSelected(true);
            rowView.setEditMode(true);
        }
    }

    public void prepareEditMode(int rowType) {
        Iterator it = this.mAllRowsList.iterator();
        while (it.hasNext()) {
            HomeScreenRow row = (HomeScreenRow) it.next();
            if (row.getType().getCode() == rowType) {
                View activeFrame = row.getRowView();
                if (activeFrame instanceof ActiveFrame) {
                    for (int i = 0; i < ((ActiveFrame) activeFrame).getChildCount(); i++) {
                        View rowView = ((ActiveFrame) activeFrame).getChildAt(i);
                        if ((rowView instanceof EditableAppsRowView) && ((EditableAppsRowView) rowView).getChildCount() > 0) {
                            if (rowView.isAttachedToWindow()) {
                                beginEditMode((EditableAppsRowView) rowView);
                            } else {
                                ((EditableAppsRowView) rowView).setEditModePending(true);
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

    private Adapter<?> initAdapter(RowType type) {
        Set<AppCategory> enabledCategories = RowPreferences.getEnabledCategories(mMainActivity);
        switch (type) {
            case NOTIFICATIONS:
                return this.mRecommendationsAdapter;
            case ACTUAL_NOTIFICATIONS:
                return this.mNotificationsAdapter;
            case PARTNER:
                return this.mPartnerAdapter;
            case APPS:
                Set<AppCategory> categories = new HashSet<>();
                categories.add(AppCategory.OTHER);
                if (!enabledCategories.contains(AppCategory.VIDEO))
                    categories.add(AppCategory.VIDEO);
                if (!enabledCategories.contains(AppCategory.GAME))
                    categories.add(AppCategory.GAME);
                if (!enabledCategories.contains(AppCategory.MUSIC))
                    categories.add(AppCategory.MUSIC);
                return new AppsAdapter(this.mMainActivity, this.mRecommendationsAdapter, categories.toArray(new AppCategory[categories.size()]));
            case FAVORITES:
                return new FavoritesAdapter(this.mMainActivity, this.mRecommendationsAdapter);
            case VIDEO:
                return new AppsAdapter(this.mMainActivity, null, AppCategory.VIDEO);
            case GAMES:
                return new AppsAdapter(this.mMainActivity, null, AppCategory.GAME);
            case MUSIC:
                return new AppsAdapter(this.mMainActivity, null, AppCategory.MUSIC);
            case SETTINGS:
                return this.mSettingsAdapter;
            case INPUTS:
                // TODO this.mPartner.showPhysicalTunersSeparately(), this.mPartner.disableDisconnectedInputs(), this.mPartner.getStateIconFromTVInput()
                Adapter<?> adapter = new InputsAdapter(this.mMainActivity, new InputsAdapter.Configuration(true, true, true));
                this.mInputsAdapter = (InputsAdapter) adapter;
                return adapter;
            default:
                return null;
        }
    }

    public void onChildViewHolderSelected(RecyclerView parent, ViewHolder child, int position) {
        if (position != this.mActiveItemIndex) {
            ViewHolder activeItem;
            if (position == -1) {
                activeItem = parent.findViewHolderForAdapterPosition(this.mActiveItemIndex);
                if (activeItem != null) {
                    activeItem.itemView.setActivated(false);
                }
            } else {
                if (this.mActiveItemIndex != -1) {
                    activeItem = parent.findViewHolderForAdapterPosition(this.mActiveItemIndex);
                    if (activeItem != null) {
                        activeItem.itemView.setActivated(false);
                    }
                }
                child.itemView.setActivated(true);
            }
            if (BuildConfig.DEBUG) Log.d("******", "set mActiveItemIndex to " + position);
            this.mActiveItemIndex = position;
        }
    }

    public int getScrollOffset(int index) {
        if (index >= 0 || index < this.mVisRowsList.size()) {
            return this.mVisRowsList.get(index).getRowScrollOffset();
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

    public boolean isUiVisible() {
        return this.mRecommendationsAdapter.isUiVisible();
    }

    public void onViewDetachedFromWindow(HomeViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
        if (holder.itemView instanceof HomeScrollFractionListener) {
            this.mScrollManager.removeHomeScrollListener((HomeScrollFractionListener) holder.itemView);
        }
        this.mMainActivity.excludeFromEditAnimation(holder.itemView);
    }

    public void onViewAttachedToWindow(final HomeViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if (holder.itemView instanceof HomeScrollFractionListener) {
            this.mScrollManager.addHomeScrollListener((HomeScrollFractionListener) holder.itemView);
        }
        holder.itemView.addOnLayoutChangeListener(new OnLayoutChangeListener() {
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                v.removeOnLayoutChangeListener(this);
                if (HomeScreenAdapter.this.mMainActivity.isEditAnimationInProgress()) {
                    HomeScreenAdapter.this.mMainActivity.includeInEditAnimation(holder.itemView);
                } else if (!(holder.itemView instanceof ActiveFrame)) {
                } else {
                    if (HomeScreenAdapter.this.mMainActivity.isInEditMode()) {
                        HomeScreenAdapter.this.setActiveFrameChildrenAlpha((ActiveFrame) holder.itemView, 0.0f);
                        return;
                    }
                    HomeScreenAdapter.this.setActiveFrameChildrenAlpha((ActiveFrame) holder.itemView, 1.0f);
                    holder.itemView.post(new Runnable() {
                        public void run() {
                            HomeScreenAdapter.this.beginEditModeForPendingRow((ActiveFrame) holder.itemView);
                        }
                    });
                }
            }
        });
    }

    private void beginEditModeForPendingRow(ActiveFrame frame) {
        for (int i = 0; i < frame.getChildCount(); i++) {
            View rowView = frame.getChildAt(i);
            if ((rowView instanceof EditableAppsRowView) && ((EditableAppsRowView) rowView).isEditModePending()) {
                beginEditMode((EditableAppsRowView) rowView);
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

    public void dump(String prefix, PrintWriter writer) {
        writer.println(prefix + "HomeScreenAdapter");
        prefix = prefix + "  ";
        if (this.mRecommendationsAdapter != null) {
            this.mRecommendationsAdapter.dump(prefix, writer);
        }
        if (this.mPartnerAdapter != null) {
            this.mPartnerAdapter.dump(prefix, writer);
        }
    }

    void onSearchIconUpdate(Drawable assistantIcon) {
        this.mAssistantIcon = assistantIcon;
        if (this.mSearch != null) {
            this.mSearch.updateAssistantIcon(this.mAssistantIcon);
        }
    }

    void onSuggestionsUpdate(String[] suggestions) {
        this.mAssistantSuggestions = suggestions;
        if (this.mSearch != null) {
            this.mSearch.updateSearchSuggestions(this.mAssistantSuggestions);
        }
    }
}
