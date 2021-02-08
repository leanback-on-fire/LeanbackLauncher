package com.amazon.tv.firetv.leanbacklauncher.util

/**
 * Created by rockon999 on 2/24/18.
 */
class SettingsUtil {
    enum class SettingsType(val code: Int) {
        UNKNOWN(-1), WIFI(0), NOTIFICATIONS(1), APP_CONFIGURE(5), EDIT_FAVORITES(10);

        companion object {
            fun fromCode(code: Int): SettingsType {
                for (type in values()) if (type.code == code) return type
                return UNKNOWN
            }
        }
    }
}