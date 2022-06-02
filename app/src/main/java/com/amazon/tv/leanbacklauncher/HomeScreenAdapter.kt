package com.amazon.tv.leanbacklauncher

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLayoutChangeListener
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.amazon.tv.firetv.leanbacklauncher.apps.AppCategory
import com.amazon.tv.firetv.leanbacklauncher.apps.FavoritesAdapter
import com.amazon.tv.firetv.leanbacklauncher.apps.RowPreferences.areFavoritesEnabled
import com.amazon.tv.firetv.leanbacklauncher.apps.RowPreferences.areInputsEnabled
import com.amazon.tv.firetv.leanbacklauncher.apps.RowPreferences.areRecommendationsEnabled
import com.amazon.tv.firetv.leanbacklauncher.apps.RowPreferences.getAppsColumns
import com.amazon.tv.firetv.leanbacklauncher.apps.RowPreferences.getBannersSize
import com.amazon.tv.firetv.leanbacklauncher.apps.RowPreferences.getEnabledCategories
import com.amazon.tv.firetv.leanbacklauncher.apps.RowPreferences.getFavoriteRowMax
import com.amazon.tv.firetv.leanbacklauncher.apps.RowPreferences.getRowMax
import com.amazon.tv.firetv.leanbacklauncher.apps.RowType
import com.amazon.tv.leanbacklauncher.HomeScreenAdapter.HomeViewHolder
import com.amazon.tv.leanbacklauncher.HomeScreenRow.RowChangeListener
import com.amazon.tv.leanbacklauncher.HomeScrollManager.HomeScrollFractionListener
import com.amazon.tv.leanbacklauncher.apps.*
import com.amazon.tv.leanbacklauncher.apps.AppsManager.Companion.getInstance
import com.amazon.tv.leanbacklauncher.apps.ConnectivityListener.Companion.readConnectivity
import com.amazon.tv.leanbacklauncher.inputs.InputsAdapter
import com.amazon.tv.leanbacklauncher.notifications.*
import com.amazon.tv.leanbacklauncher.util.Preconditions
import com.amazon.tv.leanbacklauncher.widget.EditModeView
import java.io.PrintWriter
import java.util.*
import kotlin.math.abs

class HomeScreenAdapter(
    context: MainActivity,
    scrollMgr: HomeScrollManager,
    notificationsAdapter: NotificationsAdapter?,
    editModeView: EditModeView
) : RecyclerView.Adapter<HomeViewHolder?>(), RowChangeListener, ConnectivityListener.Listener,
    OnEditModeChangedListener {
//    private val TAG by lazy { if (BuildConfig.DEBUG) ("[*]" + javaClass.simpleName).take(21) else javaClass.simpleName }
    private var mActiveItemIndex = -1
    private val mAllRowsList: ArrayList<HomeScreenRow> = ArrayList<HomeScreenRow>(7)
    private val mAppsManager: AppsManager? = getInstance(context)
    private var mAssistantIcon: Drawable? = null
    private var mAssistantSuggestions: Array<String>? = emptyArray()
    private val mConnectivityListener: ConnectivityListener?
    private var mEditListener: OnEditModeChangedListener? = null
    private val mEditModeView: EditModeView
    private val mHeaders: SparseArray<View?> = SparseArray<View?>(7)
    private var mHomeScreenMessaging: HomeScreenMessaging? = null
    private val mInflater: LayoutInflater = LayoutInflater.from(context)
    private var mInputsAdapter: InputsAdapter? = null
    private val mMainActivity: MainActivity = Preconditions.checkNotNull(context)
    private val mPartnerAdapter: PartnerAdapter?
    private var mReceiver: BroadcastReceiver? = null
    val recommendationsAdapter: NotificationsAdapter? = notificationsAdapter
    private val mScrollManager: HomeScrollManager = Preconditions.checkNotNull(scrollMgr)
    private var mSearch: SearchOrbView? = null
    private val mSettingsAdapter: SettingsAdapter
    private val mVisRowsList: ArrayList<HomeScreenRow> = ArrayList<HomeScreenRow>(7)
    private val mNotificationsAdapter: RecyclerView.Adapter<*>? = null

    init {
        mConnectivityListener = ConnectivityListener(context, this)
        mSettingsAdapter = SettingsAdapter(mMainActivity, mConnectivityListener)
        mEditModeView = editModeView
        mPartnerAdapter = null
        setHasStableIds(true)
        buildRowList()
        mAppsManager?.refreshLaunchPointList()
        mAppsManager?.registerUpdateReceivers()
        //if (Permission.isLocationPermissionGranted(mMainActivity))
        mConnectivityListener.start()
    }

    class HomeViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!)

    private class ListComparator : Comparator<HomeScreenRow> {
        override fun compare(lhs: HomeScreenRow, rhs: HomeScreenRow): Int {
            return lhs.position - rhs.position
        }
    }

    fun unregisterReceivers() {
        mReceiver?.let {
            mMainActivity.unregisterReceiver(it)
            mReceiver = null
        }
        mConnectivityListener?.stop()
        mAppsManager?.unregisterUpdateReceivers()
        mInputsAdapter?.unregisterReceivers()

    }

    // TODO: check scrolls
    fun resetRowPositions(smooth: Boolean) {
        for (i in mAllRowsList.indices) {
            if (mAllRowsList[i].rowView is ActiveFrame) {
                (mAllRowsList[i].rowView as ActiveFrame).resetScrollPosition(smooth)
            }
        }
    }

    fun setRowAlphas(alpha: Int) {
        val it: Iterator<*> = mAllRowsList.iterator()
        while (it.hasNext()) {
            val activeFrame = (it.next() as HomeScreenRow).rowView
            if (activeFrame is ActiveFrame) {
                for (i in 0 until activeFrame.childCount) {
                    val rowView = activeFrame.getChildAt(i)
                    if (rowView !is EditableAppsRowView) {
                        rowView.alpha = alpha.toFloat()
                    } else if (!(rowView.editMode || rowView.getAlpha() == alpha.toFloat())) {
                        rowView.setAlpha(alpha.toFloat())
                    }
                }
            }
        }
    }

    fun getRowIndex(rowType: Int): Int {
        var index = -1
        val size = mVisRowsList.size
        for (i in 0 until size) {
            if (mVisRowsList[i].type.code == rowType) {
                index = i
            }
        }
        return index
    }

    override fun onConnectivityChange() {
        mSettingsAdapter.onConnectivityChange()
        mHomeScreenMessaging?.onConnectivityChange(readConnectivity(mMainActivity))

    }

    private fun buildRowList() {
        val res = mMainActivity.resources
        val hasInputsRow = true //this.mPartner.isRowEnabled("inputs_row");

        // TODO
        mAppsManager?.setExcludeChannelActivities(hasInputsRow)
        var position = 0
        val enabledCategories = getEnabledCategories(mMainActivity)

        buildRow(
            RowType.SEARCH,
            position++,
            null,
            null,
            null,
            R.dimen.home_scroll_size_search,
            false
        )
        if (areRecommendationsEnabled(mMainActivity)) {
            buildRow(
                RowType.NOTIFICATIONS,
                position++,
                null,
                null,
                null,
                R.dimen.home_scroll_size_notifications,
                false
            )
        }
        if (areFavoritesEnabled(mMainActivity)) {
            buildRow(
                RowType.FAVORITES,
                position++,
                res.getString(R.string.category_label_favorites),
                null,
                null,
                R.dimen.home_scroll_size_apps,
                true
            )
        }
        if (enabledCategories.contains(AppCategory.VIDEO)) {
            buildRow(
                RowType.VIDEO,
                position++,
                res.getString(R.string.category_label_videos),
                null,
                null,
                R.dimen.home_scroll_size_video,
                true
            )
        }
        if (enabledCategories.contains(AppCategory.MUSIC)) {
            buildRow(
                RowType.MUSIC,
                position++,
                res.getString(R.string.category_label_music),
                null,
                null,
                R.dimen.home_scroll_size_music,
                true
            )
        }
        if (enabledCategories.contains(AppCategory.GAME)) {
            buildRow(
                RowType.GAMES,
                position++,
                res.getString(R.string.category_label_games),
                null,
                null,
                R.dimen.home_scroll_size_games,
                true
            )
        }
        buildRow(
            RowType.APPS,
            position++,
            res.getString(R.string.category_label_apps),
            null,
            null,
            R.dimen.home_scroll_size_apps,
            true
        )
        if (areInputsEnabled(mMainActivity)) {
            buildRow(
                RowType.INPUTS,
                position++,
                res.getString(R.string.category_label_inputs),
                null,
                null,
                R.dimen.home_scroll_size_inputs,
                true
            )
        }
        buildRow(
            RowType.SETTINGS,
            position,
            res.getString(R.string.category_label_settings),
            null,
            null,
            R.dimen.home_scroll_size_settings,
            false
        )
        // TODO Notifications view... buildRow(RowType.ACTUAL_NOTIFICATIONS, position++, null, null, null, R.dimen.home_scroll_size_notifications, false);

        val comp = ListComparator()
        Collections.sort(mAllRowsList, comp)
        Collections.sort(mVisRowsList, comp)
    }

    private fun buildRow(
        type: RowType,
        position: Int,
        title: String?,
        icon: Drawable?,
        font: String?,
        scrollOffsetResId: Int,
        hideIfEmpty: Boolean
    ) {
        val row = HomeScreenRow(type, position, hideIfEmpty)
        row.setHeaderInfo(title != null, title, icon, font)
        row.adapter = initAdapter(type)
        row.setViewScrollOffset(mMainActivity.resources.getDimensionPixelOffset(scrollOffsetResId))
        addRowEntry(row)
    }

    private fun addRowEntry(row: HomeScreenRow) {
        mAllRowsList.add(row)
        row.setChangeListener(this)
        if (row.type !== RowType.NOTIFICATIONS && row.type !== RowType.ACTUAL_NOTIFICATIONS && row.type !== RowType.SEARCH) {
            mAppsManager?.addAppRow(row)
        }
        if (row.isVisible) {
            mVisRowsList.add(row)
        }
    }

    override fun onRowVisibilityChanged(position: Int, visible: Boolean) {
        var i: Int
        if (visible) {
            var insertPoint = mVisRowsList.size
            i = 0
            while (i < mVisRowsList.size) {
                if (mVisRowsList[i].position != position) {
                    if (mVisRowsList[i].position > position) {
                        insertPoint = i
                        break
                    }
                    i++
                } else {
                    return
                }
            }
            mVisRowsList.add(insertPoint, mAllRowsList[position])
            notifyItemInserted(insertPoint)
            return
        }
        var pos = -1
        i = 0
        while (i < mVisRowsList.size) {
            if (mVisRowsList[i].position == position) {
                pos = i
                break
            }
            i++
        }
        if (pos >= 0) {
            mVisRowsList.removeAt(pos)
            notifyItemRemoved(pos)
        }
    }

    fun refreshAdapterData() {
        mAppsManager?.refreshRows()
        mInputsAdapter?.refreshInputsData()

    }

    fun animateSearchIn() {
        mSearch?.animateIn()
    }

    override fun getItemId(position: Int): Long {
        return mVisRowsList[position].type.code.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return if (position >= mVisRowsList.size) {
            -1
        } else mVisRowsList[position].position
    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): HomeViewHolder {
        var view = View(parent.context)
        val mRow = mAllRowsList[position]
        mRow.let { row ->
            when (row.type) {
                RowType.SEARCH -> {
                    view = mInflater.inflate(R.layout.home_search_orb, parent, false)
                    mHeaders.put(row.type.code, view)
                    mSearch = view as SearchOrbView
                    mSearch?.let {
                        mAppsManager?.setSearchPackageChangeListener(it, it.searchPackageName)
                        if (it.isKatnissPackagePresent) {
                            it.showSearch()
                            it.updateAssistantIcon(mAssistantIcon)
                            it.updateSearchSuggestions(mAssistantSuggestions)
                        } else {
                            if (it.isAssistPackagePresent)
                                it.showSearch()
                            else
                                it.hideSearch()
                        }
                    }
                }
                RowType.NOTIFICATIONS -> {
                    view = mInflater.inflate(R.layout.home_notification_row, parent, false)
                    val notifList: NotificationRowView? = view.findViewById(R.id.list)
                    val homeScreenView: HomeScreenView? =
                        view.findViewById(R.id.home_screen_messaging)
                    if (!(notifList == null || homeScreenView == null)) {
                        row.adapter?.let { adapter ->
                            initNotificationsRows(
                                notifList,
                                adapter,
                                homeScreenView.homeScreenMessaging
                            )
                        }
                    }
                }
                RowType.PARTNER, RowType.SETTINGS, RowType.INPUTS -> {
                    view = mInflater.inflate(R.layout.home_active_items_row, parent, false)
                    mHeaders.put(row.type.code, view.findViewById(R.id.header))
                    if (view is ActiveFrame) {
                        initAppRow(view as ActiveFrame, row)
                    }
                }
                RowType.APPS, RowType.GAMES, RowType.FAVORITES, RowType.MUSIC, RowType.VIDEO -> {
                    view = mInflater.inflate(R.layout.home_apps_row, parent, false)
                    mHeaders.put(row.type.code, view.findViewById(R.id.header))
                    if (view is ActiveFrame) {
                        initAppRow(view as ActiveFrame, row)
                    }
                }
                RowType.ACTUAL_NOTIFICATIONS -> TODO()
                //else -> TODO()
            }
            row.rowView = view
            view.tag = row.type.code
        }
        return HomeViewHolder(view)
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        holder.itemView.isActivated = position == mActiveItemIndex
    }

    override fun onFailedToRecycleView(holder: HomeViewHolder): Boolean {
        if (holder.itemView is ActiveFrame) {
            resetRowAdapter(holder.itemView)
        }
        return false
    }

    override fun getItemCount(): Int {
        return mVisRowsList.size
    }

    val rowHeaders: Array<View?>
        get() {
            val n = mHeaders.size()
            val headers = arrayOfNulls<View>(n)
            for (i in 0 until n) {
                headers[i] = mHeaders.valueAt(i)
            }
            return headers
        }

    override fun onEditModeChanged(z: Boolean) {
        mEditListener?.onEditModeChanged(z)
    }

    val allRows: ArrayList<HomeScreenRow?>
        get() = ArrayList<HomeScreenRow?>(mAllRowsList)

    fun setOnEditModeChangedListener(listener: OnEditModeChangedListener?) {
        mEditListener = listener
    }

    private fun initNotificationsRows(
        list: NotificationRowView,
        adapter: RecyclerView.Adapter<*>,
        homeScreenMessaging: HomeScreenMessaging
    ) {
        list.setHasFixedSize(true)
        list.adapter = adapter
        mHomeScreenMessaging = homeScreenMessaging
        list.setItemSpacing(mMainActivity.resources.getDimensionPixelOffset(R.dimen.inter_card_spacing))
        val filter = IntentFilter()
        filter.addAction("android.intent.action.USER_PRESENT")
        if (mReceiver == null) {
            mReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    if (intent.action == "android.intent.action.USER_PRESENT" && adapter is NotificationsServiceAdapter<*>) {
                        adapter.reregisterListener()
                        mMainActivity.unregisterReceiver(this)
                        mReceiver = null
                    }
                }
            }
            mMainActivity.registerReceiver(mReceiver, filter)
        }
    }

    private fun initAppRow(group: ActiveFrame?, row: HomeScreenRow?) {
        if (group != null) {
            val res = mMainActivity.resources
            group.tag = R.integer.tag_has_header

            val list: ActiveItemsRowView = group.findViewById(R.id.list)
            if (list is EditableAppsRowView) {
                list.setEditModeView(mEditModeView)
                list.addEditModeListener(mEditModeView)
                list.addEditModeListener(this)
            }
            list.setHasFixedSize(true)
            list.adapter = row!!.adapter

            if (row.hasHeader()) {
                list.contentDescription = row.title
                val titleView = group.findViewById<View>(R.id.title)
                titleView?.let { tv ->
                    (tv as TextView).text = row.title
                    if (!TextUtils.isEmpty(row.fontName)) {
                        val font = Typeface.create(row.fontName, Typeface.NORMAL)
                        if (font != null) {
                            tv.typeface = font
                        }
                    }
                }
                val icon = row.icon
                val iconView = group.findViewById<ImageView>(R.id.icon)
                if (icon != null) {
                    iconView.setImageDrawable(icon)
                    iconView.visibility = View.VISIBLE
                } else {
                    iconView.visibility = View.GONE
                }
            }

            val lp = list.layoutParams
            val cardSpacing = res.getDimensionPixelOffset(R.dimen.inter_card_spacing)

            val size = getBannersSize(mMainActivity)
            val rowHeight = res.getDimension(R.dimen.banner_height).toInt() * size / 100
            val userMax: Int
            group.setScaledWhenUnfocused(true)

            // calculate number of rows based on maxApps:
            // always fill a least one full row of maxApps
            val curApps = row.adapter!!.itemCount
            val maxCols = getAppsColumns(mMainActivity)
            val minRows = res.getInteger(R.integer.min_num_banner_rows)
            val maxRows: Int // = res.getInteger(R.integer.max_num_banner_rows)
            var base = abs(curApps / maxCols)
            val lost = (maxCols * (base + 1)) - curApps
            if (lost < base + 1) base += 1
            when (row.type) {
                RowType.INPUTS, RowType.PARTNER -> {
                }
                RowType.FAVORITES -> {
                    userMax = getFavoriteRowMax(mMainActivity)
                    maxRows = if (base > 0) base.coerceAtMost(userMax) else minRows
                    list.setIsNumRowsAdjustable(true)
                    list.adjustNumRows(maxRows, cardSpacing, rowHeight)
                }
                RowType.GAMES -> {
                    userMax = getRowMax(AppCategory.GAME, mMainActivity)
                    maxRows = if (base > 0) base.coerceAtMost(userMax) else minRows
                    list.setIsNumRowsAdjustable(true)
                    list.adjustNumRows(maxRows, cardSpacing, rowHeight)
                }
                RowType.MUSIC -> {
                    userMax = getRowMax(AppCategory.MUSIC, mMainActivity)
                    maxRows = if (base > 0) base.coerceAtMost(userMax) else minRows
                    list.setIsNumRowsAdjustable(true)
                    list.adjustNumRows(maxRows, cardSpacing, rowHeight)
                }
                RowType.VIDEO -> {
                    userMax = getRowMax(AppCategory.VIDEO, mMainActivity)
                    maxRows = if (base > 0) base.coerceAtMost(userMax) else minRows
                    list.setIsNumRowsAdjustable(true)
                    list.adjustNumRows(maxRows, cardSpacing, rowHeight)
                }
                RowType.APPS -> {
                    userMax = getRowMax(AppCategory.OTHER, mMainActivity)
                    maxRows = if (base > 0) base.coerceAtMost(userMax) else minRows
                    list.setIsNumRowsAdjustable(true)
                    list.adjustNumRows(maxRows, cardSpacing, rowHeight)
                }
                RowType.SETTINGS -> lp.height =
                    res.getDimension(R.dimen.settings_row_height).toInt()
                else -> {}
            }
            list.setItemSpacing(cardSpacing)
        }
    }

    private fun beginEditMode(rowView: EditableAppsRowView) {
        if (rowView.childCount > 0) {
            rowView.isEditModePending = false
            val child = rowView.getChildAt(0)
            child.requestFocus()
            child.isSelected = true
            rowView.editMode = true
        }
    }

    fun prepareEditMode(rowType: Int) {
        val it: Iterator<*> = mAllRowsList.iterator()
        while (it.hasNext()) {
            val row = it.next() as HomeScreenRow
            if (row.type.code == rowType) {
                val activeFrame = row.rowView
                if (activeFrame is ActiveFrame) {
                    for (i in 0 until activeFrame.childCount) {
                        val rowView = activeFrame.getChildAt(i)
                        if (rowView is EditableAppsRowView && rowView.childCount > 0) {
                            if (rowView.isAttachedToWindow()) {
                                beginEditMode(rowView)
                            } else {
                                rowView.isEditModePending = true
                            }
                        }
                    }
                }
            }
        }
    }

    private fun resetRowAdapter(group: ActiveFrame) {
        (group.findViewById<View>(R.id.list) as ActiveItemsRowView).adapter = null
    }

    private fun initAdapter(type: RowType): RecyclerView.Adapter<*>? {
        val enabledCategories = getEnabledCategories(mMainActivity)
        return when (type) {
            RowType.NOTIFICATIONS -> recommendationsAdapter
            RowType.ACTUAL_NOTIFICATIONS -> mNotificationsAdapter
            RowType.PARTNER -> mPartnerAdapter
            RowType.FAVORITES -> FavoritesAdapter(mMainActivity, null)
            RowType.APPS -> {
                val categories: MutableSet<AppCategory> = HashSet()
                categories.add(AppCategory.OTHER)
                if (!enabledCategories.contains(AppCategory.VIDEO)) categories.add(AppCategory.VIDEO)
                if (!enabledCategories.contains(AppCategory.MUSIC)) categories.add(AppCategory.MUSIC)
                if (!enabledCategories.contains(AppCategory.GAME)) categories.add(AppCategory.GAME)
                AppsAdapter(mMainActivity, null, *categories.toTypedArray())
            }
            RowType.VIDEO -> AppsAdapter(mMainActivity, null, AppCategory.VIDEO)
            RowType.MUSIC -> AppsAdapter(mMainActivity, null, AppCategory.MUSIC)
            RowType.GAMES -> AppsAdapter(mMainActivity, null, AppCategory.GAME)
            RowType.SETTINGS -> mSettingsAdapter
            RowType.INPUTS -> {
                // TODO this.mPartner.showPhysicalTunersSeparately(), this.mPartner.disableDisconnectedInputs(), this.mPartner.getStateIconFromTVInput()
                val adapter: RecyclerView.Adapter<*> =
                    InputsAdapter(mMainActivity, InputsAdapter.Configuration(true, true, true))
                mInputsAdapter = adapter as InputsAdapter
                adapter
            }
            else -> null
        }
    }

    fun onChildViewHolderSelected(
        parent: RecyclerView?,
        child: RecyclerView.ViewHolder?,
        position: Int
    ) {
        if (position != mActiveItemIndex) {
            val activeItem: RecyclerView.ViewHolder?
            if (position == -1) {
                activeItem = parent?.findViewHolderForAdapterPosition(mActiveItemIndex)
                activeItem?.itemView?.isActivated = false
            } else {
                if (mActiveItemIndex != -1) {
                    activeItem = parent?.findViewHolderForAdapterPosition(mActiveItemIndex)
                    activeItem?.itemView?.isActivated = false
                }
                child?.itemView?.isActivated = true
            }
//            if (BuildConfig.DEBUG) Log.d(TAG, "onChildViewHolderSelected: set mActiveItemIndex to $position")
            mActiveItemIndex = position
        }
    }

    fun getScrollOffset(index: Int): Int {
        return if (index >= 0 || index < mVisRowsList.size) {
            mVisRowsList[index].rowScrollOffset
        } else 0
    }

    fun onReconnectToRecommendationsService() {
        recommendationsAdapter?.reregisterListener()
    }

    fun onInitUi() {
        recommendationsAdapter?.onInitUi()
        mPartnerAdapter?.onInitUi()
    }

    fun onUiVisible() {
        recommendationsAdapter?.onUiVisible()
        mPartnerAdapter?.onUiVisible()
    }

    fun onUiInvisible() {
        recommendationsAdapter?.onUiInvisible()
        mPartnerAdapter?.onUiInvisible()
    }

    fun onStopUi() {
        recommendationsAdapter?.onStopUi()
        mPartnerAdapter?.onStopUi()
    }

    val isUiVisible: Boolean
        get() = recommendationsAdapter!!.isUiVisible

    override fun onViewDetachedFromWindow(holder: HomeViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.itemView.clearAnimation()
        if (holder.itemView is HomeScrollFractionListener) {
            mScrollManager.removeHomeScrollListener(holder.itemView as HomeScrollFractionListener)
        }
        mMainActivity.excludeFromEditAnimation(holder.itemView)
    }

    override fun onViewAttachedToWindow(holder: HomeViewHolder) {
        super.onViewAttachedToWindow(holder)
        if (holder.itemView is HomeScrollFractionListener) {
            mScrollManager.addHomeScrollListener(holder.itemView as HomeScrollFractionListener)
        }
        holder.itemView.addOnLayoutChangeListener(object : OnLayoutChangeListener {
            override fun onLayoutChange(
                v: View,
                left: Int,
                top: Int,
                right: Int,
                bottom: Int,
                oldLeft: Int,
                oldTop: Int,
                oldRight: Int,
                oldBottom: Int
            ) {
                v.removeOnLayoutChangeListener(this)
                when {
                    mMainActivity.isEditAnimationInProgress -> {
                        mMainActivity.includeInEditAnimation(holder.itemView)
                    }
                    holder.itemView !is ActiveFrame -> {
                        // TODO()
                    }
                    else -> {
                        if (mMainActivity.isInEditMode) {
                            setActiveFrameChildrenAlpha(holder.itemView, 0.0f)
                            return
                        }
                        setActiveFrameChildrenAlpha(holder.itemView, 1.0f)
                        holder.itemView.post {
                            beginEditModeForPendingRow(holder.itemView)
                        }
                    }
                }
            }
        })
    }

    private fun beginEditModeForPendingRow(frame: ActiveFrame) {
        for (i in 0 until frame.childCount) {
            val rowView = frame.getChildAt(i)
            if (rowView is EditableAppsRowView && rowView.isEditModePending) {
                beginEditMode(rowView)
            }
        }
    }

    private fun setActiveFrameChildrenAlpha(frame: ActiveFrame, alpha: Float) {
        for (i in 0 until frame.childCount) {
            frame.getChildAt(i).alpha = alpha
        }
    }

    fun sortRowsIfNeeded(force: Boolean) {
        for (i in mAllRowsList.indices) {
            val adapter = mAllRowsList[i].adapter
            if (adapter is AppsAdapter) {
                adapter.sortItemsIfNeeded(force)
            }
        }
    }

    fun dump(prefix: String, writer: PrintWriter) {
        var prefix = prefix
        writer.println(prefix + "HomeScreenAdapter")
        prefix = "$prefix  "
        recommendationsAdapter?.dump(prefix, writer)
        mPartnerAdapter?.dump(prefix, writer)
    }

    fun onSearchIconUpdate(assistantIcon: Drawable?) {
        mAssistantIcon = assistantIcon
        mSearch?.updateAssistantIcon(mAssistantIcon)
    }

    fun onSuggestionsUpdate(suggestions: Array<String>?) {
        mAssistantSuggestions = suggestions
        mSearch?.updateSearchSuggestions(mAssistantSuggestions)
    }

}