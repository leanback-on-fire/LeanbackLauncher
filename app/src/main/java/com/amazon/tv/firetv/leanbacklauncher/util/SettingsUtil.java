package com.amazon.tv.firetv.leanbacklauncher.util;

/**
 * Created by rockon999 on 2/24/18.
 */

public class SettingsUtil {

    public enum SettingsType {
        UNKNOWN(-1),
        WIFI(0),
        NOTIFICATIONS(1),
        APP_CONFIGURE(5),
        EDIT_FAVORITES(10);

        private final int code;

        SettingsType(int code) {
            this.code = code;
        }

        public static SettingsType fromCode(int code) {
            for (SettingsType type : values())
                if (type.code == code)
                    return type;
            return UNKNOWN;
        }

        public int getCode() {
            return code;
        }
    }
}
