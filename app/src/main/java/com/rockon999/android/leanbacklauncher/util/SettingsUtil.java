package com.rockon999.android.leanbacklauncher.util;

public class SettingsUtil {

    private static final int UNKNOWN_TYPE_CODE = -1;
    private static final int APP_CONFIGURE_CODE = 0;
    private static final int EDIT_FAVORITES_CODE = 5;
    private static final int WIFI_TYPE_CODE = 10;
    private static final int NOTIFICATIONS_TYPE_CODE = 20;
    
    public enum Type {
        UNKNOWN(UNKNOWN_TYPE_CODE),
        WIFI(WIFI_TYPE_CODE),
        NOTIFICATIONS(NOTIFICATIONS_TYPE_CODE), APP_CONFIGURE(APP_CONFIGURE_CODE), EDIT_FAVORITES(EDIT_FAVORITES_CODE);

        private final int code;

        Type(int code) {
            this.code = code;
        }

        public static Type fromCode(int code) {
            switch (code) {
                case WIFI_TYPE_CODE:
                    return WIFI;
                case NOTIFICATIONS_TYPE_CODE:
                    return NOTIFICATIONS;
                case APP_CONFIGURE_CODE:
                    return APP_CONFIGURE;
                case EDIT_FAVORITES_CODE:
                    return EDIT_FAVORITES;
                default:
                    return UNKNOWN;
            }
        }

        public int getCode() {
            return code;
        }
    }
}
