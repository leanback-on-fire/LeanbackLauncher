package com.amazon.tv.firetv.leanbacklauncher.apps

import android.util.SparseArray
import java.util.*

enum class AppCategory(val code: Int) {
    OTHER(0),
    SETTINGS(1),
    VIDEO(2),
    MUSIC(3),
    GAME(4);

    companion object {
        private val intToType = SparseArray<AppCategory>()

        fun fromCategoryCode(code: Int): AppCategory {
            val category = intToType[code]
            return category ?: OTHER
        }

        fun fromName(name: String?): AppCategory? {
            return if (name == null)
                null
            else
                valueOf(name.uppercase(Locale.getDefault()).trim { it <= ' ' })
        }

        val VALUES = values()
        init {
            for (type in VALUES) {
                intToType.put(type.code, type)
            }
        }
    }
}