package com.amazon.tv.firetv.leanbacklauncher.apps

import android.util.SparseArray

enum class RowType(val code: Int) {
    SEARCH(0),
    NOTIFICATIONS(1),
    PARTNER(2),
    APPS(3),
    GAMES(4),
    SETTINGS(5),
    INPUTS(6),
    FAVORITES(7),
    MUSIC(8),
    VIDEO(9),
    ACTUAL_NOTIFICATIONS(10);

    companion object {
        private val intToType = SparseArray<RowType>()

        fun fromRowCode(code: Int): RowType {
            val category = intToType[code]
            return category ?: APPS
        }

        fun fromName(name: String?): RowType? {
            return if (name == null) null else valueOf(name.toUpperCase().trim { it <= ' ' })
        }

        val VALUES = values()
        init {
            for (type in VALUES) {
                intToType.put(type.code, type)
            }
        }
    }
}