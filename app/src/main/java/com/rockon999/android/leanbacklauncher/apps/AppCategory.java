package com.rockon999.android.leanbacklauncher.apps;

import android.util.SparseArray;

public enum AppCategory {
    OTHER(0),
    SETTINGS(1),
    VIDEO(2),
    MUSIC(3),
    GAME(4);

    /**
     * Created by rockon999 on 2/18/18.
     */
    public final int code;

    AppCategory(int code) {
        this.code = code;
    }

    private static final SparseArray<AppCategory> intToType = new SparseArray<>();

    public static AppCategory fromCategoryCode(int code) {
        AppCategory category = intToType.get(code);
        return category == null ? AppCategory.OTHER : category;
    }

    public static AppCategory fromName(String name) {
        if (name == null) return null;
        return valueOf(name.toUpperCase().trim());
    }

    static {
        for (AppCategory type : values()) {
            intToType.put(type.code, type);
        }
    }
}
