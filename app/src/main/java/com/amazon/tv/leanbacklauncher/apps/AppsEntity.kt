package com.amazon.tv.leanbacklauncher.apps

import android.content.Context
import java.util.*

class AppsEntity(ctx: Context?, helper: AppsDbHelper, packageName: String?) {
    private val mDbHelper: AppsDbHelper
    private val mLastOpened: HashMap<String?, Long?>
    private val mOrder: HashMap<String?, Long?>
    val key: String?

    constructor(context: Context?, helper: AppsDbHelper, packageName: String?, lastOpenTime: Long, initialOrder: Long) : this(context, helper, packageName) {
        setLastOpenedTimeStamp(null, lastOpenTime)
        setOrder(null, initialOrder)
    }

    val components: Set<String?>
        get() = mOrder.keys

    fun setLastOpenedTimeStamp(component: String?, timeStamp: Long) {
        mLastOpened[component] = java.lang.Long.valueOf(timeStamp)
    }

    fun getLastOpenedTimeStamp(component: String?): Long {
        var lastOpened = mLastOpened[component]
        if (lastOpened == null) {
            lastOpened = mLastOpened[null]
            if (lastOpened == null) {
                lastOpened = java.lang.Long.valueOf(0)
            }
        }
        return lastOpened!!.toLong()
    }

    fun getOrder(component: String?): Long {
        var order = mOrder[component]
        if ((order == null || order.toLong() == 0L) && mOrder.keys.size == 1) {
            order = mOrder.values.iterator().next()
            if (order == null) {
                order = java.lang.Long.valueOf(0)
            }
            setOrder(component, order!!.toLong())
        }
        return order ?: 0
    }

    fun setOrder(component: String?, order: Long) {
        mOrder[component] = java.lang.Long.valueOf(order)
    }

    @Synchronized
    fun onAction(actionType: Int, component: String?, group: String?) {
        var time = Date().time
        if (mDbHelper.mostRecentTimeStamp >= time) {
            time = mDbHelper.mostRecentTimeStamp + 1
        }
        when (actionType) {
            0 -> if (getLastOpenedTimeStamp(component) == 0L) {
                setLastOpenedTimeStamp(component, time)
            }
            1 -> setLastOpenedTimeStamp(component, time)
            3 -> mLastOpened.clear()
        }
    }

    init {
        mLastOpened = HashMap<String?, Long?>()
        mOrder = HashMap<String?, Long?>()
        mDbHelper = helper
        key = packageName
    }
}