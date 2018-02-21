package com.rockon999.android.leanbacklauncher.logging;

import android.content.Context;
import android.util.Log;

import com.rockon999.android.leanbacklauncher.apps.AppsEntity;
import com.rockon999.android.leanbacklauncher.apps.LaunchPoint;

import java.util.List;
import java.util.Map;

public final class LoggingUtils {
    private static final String TAG;

    static {
        TAG = LoggingUtils.class.getSimpleName();
    }

    public static LeanbackProto$LeanbackEvent logAppRankActionEvent(List<LaunchPoint> lps, Map<String, AppsEntity> entities, Context context) {
        if (Log.isLoggable(TAG, 2)) {
            Log.v(TAG, "Log App Rank Leanback Event.");
        }
        LeanbackProto$LeanbackEvent leanbackEvent = new LeanbackProto$LeanbackEvent();
        leanbackEvent.appRankAction = new LeanbackProto$AppRankAction();
        LeanbackProto$App[] apps = new LeanbackProto$App[lps.size()];
        leanbackEvent.type = 3;
        leanbackEvent.timestamp = System.currentTimeMillis() * 1000;
        for (int i = 0; i < lps.size(); i++) {
            LaunchPoint lp = lps.get(i);
            AppsEntity entity = entities.get(lp.getPackageName());
            apps[i] = createApp(i, lp.getPackageName(), lp.getTitle(), -1);
        }
        leanbackEvent.appRankAction.apps = apps;
        log(leanbackEvent, context);
        return leanbackEvent;
    }

    private static LeanbackProto$App createApp(int position, String packageName, String appTitle, long score) {
        LeanbackProto$App app = new LeanbackProto$App();
        app.position = position;
        app.packageName = packageName;
        app.appTitle = appTitle;
        app.score = (float) score;
        return app;
    }

    public static LeanbackProto$LeanbackEvent logRankerActionEvent(String packageName, int actionType, int rowPosition, String tag, Context context) {
        if (Log.isLoggable(TAG, 2)) {
            Log.v(TAG, "Log Ranker Action Leanback Event");
        }
        LeanbackProto$LeanbackEvent leanbackEvent = new LeanbackProto$LeanbackEvent();
        leanbackEvent.type = 1;
        leanbackEvent.timestamp = System.currentTimeMillis() * 1000;
        LeanbackProto$RankerAction rankerAction = new LeanbackProto$RankerAction();
        rankerAction.packageName = packageName;
        rankerAction.actionType = actionType;
        rankerAction.rowPosition = rowPosition;
        rankerAction.tag = tag;
        leanbackEvent.rankerAction = rankerAction;
        log(leanbackEvent, context);
        return leanbackEvent;
    }

    private static void log(LeanbackProto$LeanbackEvent leanbackEvent, Context context) {
        if (Log.isLoggable(TAG, 2)) {
            Log.v(TAG, "Logging Event : " + leanbackEvent);
        }
        LeanbackLauncherEventLogger logger = LeanbackLauncherEventLogger.getInstance(context);
        if (logger != null) {
            logger.logEvent(TAG, leanbackEvent);
        }
    }

    private LoggingUtils() {
    }
}
