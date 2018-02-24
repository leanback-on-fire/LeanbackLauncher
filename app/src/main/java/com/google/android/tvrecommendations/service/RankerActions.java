package com.google.android.tvrecommendations.service;

public abstract class RankerActions {
    static String actionToString(int action) {
        switch (action) {
            case 0:
                return "ACTION_ADD (" + action + ")";
            case 1:
                return "ACTION_OPEN_LAUNCHPOINT (" + action + ")";
            case 2:
                return "ACTION_OPEN_NOTIFICATION (" + action + ")";
            case 3:
                return "ACTION_REMOVE (" + action + ")";
            case 4:
                return "ACTION_NOTIFICATION_IMPRESSION (" + action + ")";
            default:
                return "UNKNOWN (" + action + ")";
        }
    }
}
