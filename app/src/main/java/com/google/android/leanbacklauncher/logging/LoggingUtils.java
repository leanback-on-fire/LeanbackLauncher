package com.google.android.leanbacklauncher.logging;

import android.content.Context;
import android.util.Log;
import com.google.android.leanbacklauncher.apps.AppsEntity;
import com.google.android.leanbacklauncher.apps.LaunchPoint;
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
        leanbackEvent.type = Integer.valueOf(3);
        leanbackEvent.timestamp = Long.valueOf(System.currentTimeMillis() * 1000);
        for (int i = 0; i < lps.size(); i++) {
            LaunchPoint lp = (LaunchPoint) lps.get(i);
            AppsEntity entity = (AppsEntity) entities.get(lp.getPackageName());
            apps[i] = createApp(i, lp.getPackageName(), lp.getTitle(), entity == null ? 0 : entity.getOrder(lp.getComponentName()));
        }
        leanbackEvent.appRankAction.apps = apps;
        log(leanbackEvent, context);
        return leanbackEvent;
    }

    private static LeanbackProto$App createApp(int position, String packageName, String appTitle, long score) {
        LeanbackProto$App app = new LeanbackProto$App();
        app.position = Integer.valueOf(position);
        app.packageName = packageName;
        app.appTitle = appTitle;
        app.score = Float.valueOf((float) score);
        return app;
    }

    public static LeanbackProto$LeanbackEvent logRankerActionEvent(String packageName, int actionType, int rowPosition, String tag, Context context) {
        if (Log.isLoggable(TAG, 2)) {
            Log.v(TAG, "Log Ranker Action Leanback Event");
        }
        LeanbackProto$LeanbackEvent leanbackEvent = new LeanbackProto$LeanbackEvent();
        leanbackEvent.type = Integer.valueOf(1);
        leanbackEvent.timestamp = Long.valueOf(System.currentTimeMillis() * 1000);
        LeanbackProto$RankerAction rankerAction = new LeanbackProto$RankerAction();
        rankerAction.packageName = packageName;
        rankerAction.actionType = Integer.valueOf(actionType);
        rankerAction.rowPosition = Integer.valueOf(rowPosition);
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
