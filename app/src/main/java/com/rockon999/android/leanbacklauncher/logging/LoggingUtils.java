package com.rockon999.android.leanbacklauncher.logging;

public final class LoggingUtils {
    private static final String TAG = LoggingUtils.class.getSimpleName();

    /*public static LeanbackEvent logAppRankActionEvent(List<LaunchPoint> lps, Map<String, AppsEntity> entities, Context context) {
        if (Log.isLoggable(TAG, 2)) {
            Log.v(TAG, "Log App Rank Leanback Event.");
        }
        LeanbackEvent leanbackEvent = new LeanbackEvent();
        leanbackEvent.appRankAction = new AppRankAction();
        App[] apps = new App[lps.size()];
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

    private static App createApp(int position, String packageName, String appTitle, long score) {
        App app = new App();
        app.position = Integer.valueOf(position);
        app.packageName = packageName;
        app.appTitle = appTitle;
        app.score = Float.valueOf((float) score);
        return app;
    }

    public static LeanbackEvent logRankerActionEvent(String packageName, int actionType, int rowPosition, String tag, Context context) {
        if (Log.isLoggable(TAG, 2)) {
            Log.v(TAG, "Log Ranker Action Leanback Event");
        }
        LeanbackEvent leanbackEvent = new LeanbackEvent();
        leanbackEvent.type = Integer.valueOf(1);
        leanbackEvent.timestamp = Long.valueOf(System.currentTimeMillis() * 1000);
        RankerAction rankerAction = new RankerAction();
        rankerAction.packageName = packageName;
        rankerAction.actionType = Integer.valueOf(actionType);
        rankerAction.rowPosition = Integer.valueOf(rowPosition);
        rankerAction.tag = tag;
        leanbackEvent.rankerAction = rankerAction;
        log(leanbackEvent, context);
        return leanbackEvent;
    }

    private static void log(LeanbackEvent leanbackEvent, Context context) {
        if (Log.isLoggable(TAG, 2)) {
            Log.v(TAG, "Logging Event : " + leanbackEvent);
        }
        LeanbackLauncherEventLogger logger = LeanbackLauncherEventLogger.getInstance(context);
        if (logger != null) {
            logger.logEvent(TAG, leanbackEvent);
        }
    }*/

    private LoggingUtils() {
    }
}
