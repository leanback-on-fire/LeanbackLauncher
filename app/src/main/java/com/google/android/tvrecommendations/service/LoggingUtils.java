package com.google.android.tvrecommendations.service;

public final class LoggingUtils {
   /* private static final String TAG = LoggingUtils.class.getSimpleName();

    public static LeanbackEvent logRecommendationInsertAction(StatusBarNotification notification, double rawScore, double score, int position, Context context) {
        if (Log.isLoggable(TAG, 2)) {
            Log.v(TAG, "Log recommendation insert event");
        }
        LeanbackEvent leanbackEvent = new LeanbackEvent();
        leanbackEvent.type = Integer.valueOf(4);
        leanbackEvent.timestamp = Long.valueOf(System.currentTimeMillis() * 1000);
        RecommendationInsertAction insertAction = new RecommendationInsertAction();
        insertAction.position = Integer.valueOf(position);
        insertAction.packageName = notification.getPackageName();
        insertAction.tagName = notification.getTag();
        insertAction.bucketId = Integer.valueOf(notification.getId());
        insertAction.unnormalizedPriority = Integer.valueOf(notification.getNotification().priority);
        insertAction.normalizedPriority = Float.valueOf((float) rawScore);
        insertAction.score = Float.valueOf((float) score);
        leanbackEvent.recommendationInsertAction = insertAction;
        log(leanbackEvent, context);
        return leanbackEvent;
    }

    public static LeanbackEvent logRecommendationRankEvent(List<StatusBarNotification> notifications, Map<StatusBarNotification, Double> rawScoreMap, Map<StatusBarNotification, Double> scoreMap, Context context) {
        if (Log.isLoggable(TAG, 2)) {
            Log.v(TAG, "Log reccomendation rank event");
        }
        LeanbackEvent leanbackEvent = new LeanbackEvent();
        leanbackEvent.recommendationRankAction = new RecommendationRankAction();
        leanbackEvent.type = Integer.valueOf(2);
        leanbackEvent.timestamp = Long.valueOf(System.currentTimeMillis() * 1000);
        Recommendation[] recs = new Recommendation[notifications.size()];
        for (int i = 0; i < notifications.size(); i++) {
            StatusBarNotification notification = (StatusBarNotification) notifications.get(i);
            Recommendation recommendation = new Recommendation();
            recommendation.position = Integer.valueOf(i);
            recommendation.packageName = notification.getPackageName();
            recommendation.tagName = notification.getTag();
            recommendation.bucketId = Integer.valueOf(0);
            recommendation.notificationId = Integer.valueOf(notification.getId());
            recommendation.unnormalizedPriority = Integer.valueOf(notification.getNotification().priority);
            recommendation.normalizedPriority = Float.valueOf((float) ((Double) rawScoreMap.get(notification)).doubleValue());
            recommendation.score = Float.valueOf((float) ((Double) scoreMap.get(notification)).doubleValue());
            recs[i] = recommendation;
        }
        leanbackEvent.recommendationRankAction.recommendations = recs;
        log(leanbackEvent, context);
        return leanbackEvent;
    }

    private static void log(LeanbackEvent leanbackEvent, Context context) {
        if (Log.isLoggable(TAG, 2)) {
            Log.v(TAG, "Logging Event : " + leanbackEvent);
        }
        LeanbackLauncherEventLogger.getInstance(context).logEvent(TAG, leanbackEvent);
    }

    private LoggingUtils() {
    }*/
}
