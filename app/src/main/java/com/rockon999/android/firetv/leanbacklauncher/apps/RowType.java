package com.rockon999.android.firetv.leanbacklauncher.apps;

import android.util.SparseArray;

/**
 * Created by rockon999 on 3/7/18.
 */

public enum RowType {

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

    /**
     * Created by rockon999 on 2/18/18.
     */
    private final int code;

    RowType(int code) {
        this.code = code;
    }

    private static final SparseArray<RowType> intToType = new SparseArray<>();

    public int getCode() {
        return code;
    }

    public static RowType fromRowCode(int code) {
        RowType category = intToType.get(code);
        return category == null ? RowType.APPS : category;
    }

    public static RowType fromName(String name) {
        if (name == null) return null;
        return valueOf(name.toUpperCase().trim());
    }

    static {
        for (RowType type : values()) {
            intToType.put(type.code, type);
        }
    }
}
