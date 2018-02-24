package com.google.android.leanbacklauncher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLayoutChangeListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.leanbacklauncher.HomeScreenRow.RowChangeListener;
import com.google.android.leanbacklauncher.HomeScrollManager.HomeScrollFractionListener;
import com.google.android.leanbacklauncher.apps.AppsAdapter;
import com.google.android.leanbacklauncher.apps.AppsManager;
import com.google.android.leanbacklauncher.apps.ConnectivityListener;
import com.google.android.leanbacklauncher.apps.ConnectivityListener.Listener;
import com.google.android.leanbacklauncher.apps.OnEditModeChangedListener;
import com.google.android.leanbacklauncher.apps.SettingsAdapter;
import com.google.android.leanbacklauncher.inputs.InputsAdapter;
import com.google.android.leanbacklauncher.inputs.InputsAdapter.Configuration;
import com.google.android.leanbacklauncher.notifications.HomeScreenMessaging;
import com.google.android.leanbacklauncher.notifications.HomeScreenView;
import com.google.android.leanbacklauncher.notifications.NotificationRowView;
import com.google.android.leanbacklauncher.notifications.NotificationsAdapter;
import com.google.android.leanbacklauncher.notifications.NotificationsServiceAdapter;
import com.google.android.leanbacklauncher.notifications.PartnerAdapter;
import com.google.android.leanbacklauncher.util.Partner;
import com.google.android.leanbacklauncher.util.Preconditions;
import com.google.android.leanbacklauncher.widget.EditModeView;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

public class HomeScreenAdapter extends Adapter<HomeViewHolder> implements RowChangeListener, Listener, OnEditModeChangedListener {
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
    private final Partner mPartner;
    private final PartnerAdapter mPartnerAdapter;
    private BroadcastReceiver mReceiver;
    private final NotificationsAdapter mRecommendationsAdapter;
    private final HomeScrollManager mScrollManager;
    private SearchOrbView mSearch;
    private final SettingsAdapter mSettingsAdapter;
    private ArrayList<HomeScreenRow> mVisRowsList = new ArrayList(7);

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
        this.mMainActivity = (MainActivity) Preconditions.checkNotNull(context);
        this.mScrollManager = (HomeScrollManager) Preconditions.checkNotNull(scrollMgr);
        this.mAppsManager = AppsManager.getInstance(context);
        this.mPartner = Partner.get(this.mMainActivity);
        this.mInflater = LayoutInflater.from(context);
        this.mRecommendationsAdapter = notificationsAdapter;
        this.mConnectivityListener = new ConnectivityListener(context, this);
        this.mSettingsAdapter = new SettingsAdapter(this.mMainActivity, this.mConnectivityListener);
        this.mEditModeView = editModeView;
        if (this.mPartner.isRowEnabled("partner_row")) {
            this.mPartnerAdapter = new PartnerAdapter(this.mMainActivity, this.mAppsManager);
        } else {
            this.mPartnerAdapter = null;
        }
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

    public void resetRowPositions(boolean smooth) {
        for (int i = 0; i < this.mAllRowsList.size(); i++) {
            if (((HomeScreenRow) this.mAllRowsList.get(i)).getRowView() instanceof ActiveFrame) {
                ((ActiveFrame) ((HomeScreenRow) this.mAllRowsList.get(i)).getRowView()).resetScrollPosition(smooth);
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
        int i;
        Resources res = this.mMainActivity.getResources();
        int rowCount = 7;
        int i2 = 0;
        boolean hasPartnerRow = this.mPartner.isRowEnabled("partner_row");
        if (!hasPartnerRow) {
            rowCount = 7 - 1;
        }
        boolean hasInputsRow = this.mPartner.isRowEnabled("inputs_row");
        this.mAppsManager.setExcludeChannelActivities(hasInputsRow);
        if (!hasInputsRow) {
            rowCount--;
        }
        String partnerFont = this.mPartner.getPartnerFontName();
        int i3 = 0 + 1;
        buildRow(0, 0, null, null, null, R.dimen.home_scroll_size_search, false);
        int i4 = i3 + 1;
        buildRow(1, i3, null, null, null, R.dimen.home_scroll_size_notifications, false);
        int position = this.mPartner.getRowPosition("settings_row");
        if (position == -1) {
            i = (rowCount - 1) - 0;
            i2 = 0 + 1;
        } else {
            i = position + 2;
        }
        buildRow(5, i, this.mPartner.getRowTitle("settings_row", res.getString(R.string.category_label_settings)), this.mPartner.getRowIcon("settings_row"), partnerFont, R.dimen.home_scroll_size_settings, false);
        if (hasInputsRow) {
            position = this.mPartner.getRowPosition("inputs_row");
            if (position == -1) {
                i = (rowCount - 1) - i2;
                i2++;
            } else {
                i = position + 2;
            }
            buildRow(6, i, this.mPartner.getRowTitle("inputs_row", res.getString(R.string.category_label_inputs)), this.mPartner.getRowIcon("inputs_row"), partnerFont, R.dimen.home_scroll_size_inputs, true);
        }
        position = this.mPartner.getRowPosition("games_row");
        if (position == -1) {
            i = (rowCount - 1) - i2;
            i2++;
        } else {
            i = position + 2;
        }
        buildRow(4, i, this.mPartner.getRowTitle("games_row", res.getString(R.string.category_label_games)), this.mPartner.getRowIcon("games_row"), partnerFont, R.dimen.home_scroll_size_games, true);
        position = this.mPartner.getRowPosition("apps_row");
        if (position == -1) {
            i = (rowCount - 1) - i2;
            i2++;
        } else {
            i = position + 2;
        }
        buildRow(3, i, this.mPartner.getRowTitle("apps_row", res.getString(R.string.category_label_apps)), this.mPartner.getRowIcon("apps_row"), partnerFont, R.dimen.home_scroll_size_apps, true);
        if (hasPartnerRow) {
            position = this.mPartner.getRowPosition("partner_row");
            if (position == -1) {
                i = (rowCount - 1) - i2;
                i2++;
            } else {
                i = position + 2;
            }
            buildRow(2, i, this.mPartner.getRowTitle("partner_row", null), this.mPartner.getRowIcon("partner_row"), partnerFont, R.dimen.home_scroll_size_partner, true);
        }
        ListComparator comp = new ListComparator();
        Collections.sort(this.mAllRowsList, comp);
        Collections.sort(this.mVisRowsList, comp);
    }

    private void buildRow(int type, int position, String title, Drawable icon, String font, int scrollOffsetResId, boolean hideIfEmpty) {
        HomeScreenRow row = new HomeScreenRow(type, position, hideIfEmpty);
        row.setHeaderInfo(title != null, title, icon, font);
        row.setAdapter(initAdapter(type));
        row.setViewScrollOffset(this.mMainActivity.getResources().getDimensionPixelOffset(scrollOffsetResId));
        addRowEntry(row);
    }

    private void addRowEntry(HomeScreenRow row) {
        this.mAllRowsList.add(row);
        row.setChangeListener(this);
        if (row.getType() == 3 || row.getType() == 4 || row.getType() == 5) {
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
            this.mVisRowsList.add(insertPoint, this.mAllRowsList.get(position));
            notifyItemInserted(insertPoint);
            return;
        }
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
            case 0:
                view = this.mInflater.inflate(R.layout.home_search_orb, parent, false);
                this.mHeaders.put(row.getType(), view);
                this.mSearch = (SearchOrbView) view;
                if (this.mSearch != null) {
                    this.mSearch.updateAssistantIcon(this.mAssistantIcon);
                    this.mSearch.updateSearchSuggestions(this.mAssistantSuggestions);
                    this.mAppsManager.setSearchPackageChangeListener(this.mSearch, this.mSearch.getSearchPackageName());
                    break;
                }
                break;
            case 1:
                view = this.mInflater.inflate(R.layout.home_notification_row, parent, false);
                NotificationRowView notifList = (NotificationRowView) view.findViewById(R.id.list);
                HomeScreenView homeScreenView = (HomeScreenView) view.findViewById(R.id.home_screen_messaging);
                if (!(notifList == null || homeScreenView == null)) {
                    initNotificationsRows(notifList, row.getAdapter(), homeScreenView.getHomeScreenMessaging());
                    break;
                }
            case 2:
            case 5:
            case 6:
                view = this.mInflater.inflate(R.layout.home_active_items_row, parent, false);
                this.mHeaders.put(row.getType(), view.findViewById(R.id.header));
                if (view instanceof ActiveFrame) {
                    initAppRow((ActiveFrame) view, row);
                    break;
                }
                break;
            case 3:
            case 4:
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
            group.setTag(Integer.valueOf(R.integer.tag_has_header));
            ActiveItemsRowView list = (ActiveItemsRowView) group.findViewById(R.id.list);
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
                ImageView iconView = (ImageView) group.findViewById(R.id.icon);
                if (icon != null) {
                    iconView.setImageDrawable(icon);
                    iconView.setVisibility(0);
                } else {
                    iconView.setVisibility(8);
                }
            }
            LayoutParams lp = list.getLayoutParams();
            int cardSpacing = res.getDimensionPixelOffset(R.dimen.inter_card_spacing);
            group.setScaledWhenUnfocused(true);
            switch (row.getType()) {
                case 2:
                case 3:
                case 4:
                case 6:
                    int rowHeight = (int) res.getDimension(R.dimen.banner_height);
                    list.setIsNumRowsAdjustable(true);
                    list.adjustNumRows(res.getInteger(R.integer.max_num_banner_rows), cardSpacing, rowHeight);
                    break;
                case 5:
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
            if (row.getType() == rowType) {
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

    private Adapter<?> initAdapter(int type) {
        switch (type) {
            case 1:
                return this.mRecommendationsAdapter;
            case 2:
                return this.mPartnerAdapter;
            case 3:
                return new AppsAdapter(this.mMainActivity, 0, this.mRecommendationsAdapter);
            case 4:
                return new AppsAdapter(this.mMainActivity, 1, null);
            case 5:
                return this.mSettingsAdapter;
            case 6:
                Adapter<?> adapter = new InputsAdapter(this.mMainActivity, new Configuration(this.mPartner.showPhysicalTunersSeparately(), this.mPartner.disableDisconnectedInputs(), this.mPartner.getStateIconFromTVInput()));
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
            this.mActiveItemIndex = position;
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
