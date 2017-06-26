package com.google.android.leanbacklauncher.tvrecommendations.service;

import android.content.Context;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import com.google.android.leanbacklauncher.logging.LeanbackProto$LeanbackEvent;
import com.google.android.leanbacklauncher.logging.LeanbackProto$Recommendation;
import com.google.android.leanbacklauncher.logging.LeanbackProto$RecommendationInsertAction;
import com.google.android.leanbacklauncher.logging.LeanbackProto$RecommendationRankAction;
import java.util.List;
import java.util.Map;

public final class LoggingUtils {
    private static final String TAG;

    static {
        TAG = LoggingUtils.class.getSimpleName();
    }

    public static LeanbackProto$LeanbackEvent logRecommendationInsertAction(StatusBarNotification notification, double rawScore, double score, int position, Context context) {
        if (Log.isLoggable(TAG, 2)) {
            Log.v(TAG, "Log recommendation insert event");
        }
        LeanbackProto$LeanbackEvent leanbackEvent = new LeanbackProto$LeanbackEvent();
        leanbackEvent.type = Integer.valueOf(4);
        leanbackEvent.timestamp = Long.valueOf(System.currentTimeMillis() * 1000);
        LeanbackProto$RecommendationInsertAction insertAction = new LeanbackProto$RecommendationInsertAction();
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

    public static LeanbackProto$LeanbackEvent logRecommendationRankEvent(List<StatusBarNotification> notifications, Map<StatusBarNotification, Double> rawScoreMap, Map<StatusBarNotification, Double> scoreMap, Context context) {
        if (Log.isLoggable(TAG, 2)) {
            Log.v(TAG, "Log reccomendation rank event");
        }
        LeanbackProto$LeanbackEvent leanbackEvent = new LeanbackProto$LeanbackEvent();
        leanbackEvent.recommendationRankAction = new LeanbackProto$RecommendationRankAction();
        leanbackEvent.type = Integer.valueOf(2);
        leanbackEvent.timestamp = Long.valueOf(System.currentTimeMillis() * 1000);
        LeanbackProto$Recommendation[] recs = new LeanbackProto$Recommendation[notifications.size()];
        for (int i = 0; i < notifications.size(); i++) {
            StatusBarNotification notification = (StatusBarNotification) notifications.get(i);
            LeanbackProto$Recommendation recommendation = new LeanbackProto$Recommendation();
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

    private static void log(LeanbackProto$LeanbackEvent leanbackEvent, Context context) {
        if (Log.isLoggable(TAG, 2)) {
            Log.v(TAG, "Logging Event : " + leanbackEvent);
        }
        LeanbackLauncherEventLogger.getInstance(context).logEvent(TAG, leanbackEvent);
    }

    private LoggingUtils() {
    }
}
