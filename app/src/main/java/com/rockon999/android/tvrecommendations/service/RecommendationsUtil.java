package com.rockon999.android.tvrecommendations.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;

import com.rockon999.android.tvrecommendations.TvRecommendation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class RecommendationsUtil {
    public static boolean isRecommendation(StatusBarNotification sbn) {
        return !(sbn == null || sbn.getNotification() == null || !TextUtils.equals(sbn.getNotification().category, "recommendation"));
    }

    public static boolean isInPartnerRow(Context context, StatusBarNotification sbn) {
        return /*isPackageOnSystem(context.getPackageManager(), sbn.getPackageName()) &&*/ "partner_row_entry".equals(sbn.getNotification().getGroup());
    }

    public static boolean isCaptivePortal(Context context, StatusBarNotification sbn) {
        String tag = sbn.getTag();
        if (TextUtils.equals(tag, "CaptivePortal.Notification")) {
            return true;
        }
        if (TextUtils.equals(tag, "Connectivity.Notification") || (!TextUtils.isEmpty(tag) && tag.startsWith("ConnectivityNotification:"))) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
            for (Network network : connectivityManager.getAllNetworks()) {
                if (connectivityManager.getNetworkInfo(network).isConnected() && connectivityManager.getNetworkCapabilities(network).hasCapability(17)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean equals(StatusBarNotification left, StatusBarNotification right) {
        boolean z = true;
        if (left == null || right == null) {
            if (left != right) {
                z = false;
            }
            return z;
        } else if (TextUtils.equals(left.getPackageName(), right.getPackageName()) && left.getId() == right.getId() && TextUtils.equals(left.getTag(), right.getTag())) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isPackageOnSystem(PackageManager pkgMan, String packageName) {
        try {
            ApplicationInfo info = pkgMan.getApplicationInfo(packageName, 0);
            if (info == null || (info.flags & 1) == 0) {
                return false;
            }
            return true;
        } catch (NameNotFoundException e) {
            return false;
        }
    }

    public static Bitmap getSizeCappedBitmap(Bitmap image, int maxWidth, int maxHeight) {
        if (image == null) {
            return null;
        }
        int imgWidth = image.getWidth();
        int imgHeight = image.getHeight();
        if ((imgWidth <= maxWidth && imgHeight <= maxHeight) || imgWidth <= 0 || imgHeight <= 0) {
            return image;
        }
        float scale = Math.min(1.0f, ((float) maxHeight) / ((float) imgHeight));
        if (((double) scale) >= 1.0d && imgWidth <= maxWidth) {
            return image;
        }
        float deltaW = ((float) Math.max(((int) (((float) imgWidth) * scale)) - maxWidth, 0)) / scale;
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        Bitmap newImage = Bitmap.createBitmap(image, (int) (deltaW / 2.0f), 0, (int) (((float) imgWidth) - deltaW), imgHeight, matrix, true);
        if (newImage != null) {
            return newImage;
        }
        return image;
    }

    static TvRecommendation fromStatusBarNotification(Context context, StatusBarNotification paramStatusBarNotification) {
        String packageName = paramStatusBarNotification.getPackageName();
        long l = paramStatusBarNotification.getPostTime();
        Notification notification = paramStatusBarNotification.getNotification();

        PendingIntent localPendingIntent = notification.contentIntent;
        String group = notification.getGroup();
        String sortKey = notification.getSortKey();
        boolean autoDismiss = false; // autodismiss
        String backgroundImageUri, replacePkg = "";
        CharSequence title, subtitle, info;

        //    if ((notification.flags & Notification.FLAG_AUTO_CANCEL) == Notification.FLAG_AUTO_CANCEL) {
        int progressMax = 0, progress = 0;

        int color = notification.color;
        Bitmap localBitmap = notification.largeIcon;

        if (localBitmap == null) {
            try {
                Method m = Notification.Builder.class.getMethod("rebuild", Context.class, Notification.class);

                Notification rebuilt = (Notification) m.invoke(null, context, notification);

                localBitmap = rebuilt.largeIcon;
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        if (localBitmap == null && notification.extras != null) {
            localBitmap = notification.extras.getParcelable(Notification.EXTRA_LARGE_ICON);
        }

        if (localBitmap == null && notification.extras != null) {
            localBitmap = notification.extras.getParcelable(Notification.EXTRA_LARGE_ICON_BIG);
        }

        int icon = notification.icon;

        Bundle bundle = notification.extras;

        if (bundle != null) {
            int imgWidth = bundle.getInt("notif_img_width", -1);
            int imgHeight = bundle.getInt("notif_img_height", -1);
            title = (CharSequence) bundle.get(Notification.EXTRA_TITLE);
            subtitle = (CharSequence) bundle.get(Notification.EXTRA_TEXT);
            info = (CharSequence) bundle.get(Notification.EXTRA_INFO_TEXT);
            backgroundImageUri = bundle.getString(Notification.EXTRA_BACKGROUND_IMAGE_URI);

            if (bundle.containsKey(Notification.EXTRA_PROGRESS) && !bundle.containsKey(Notification.EXTRA_PROGRESS_INDETERMINATE)) {
                progressMax = bundle.getInt(Notification.EXTRA_PROGRESS_MAX);
                progress = bundle.getInt(Notification.EXTRA_PROGRESS);
            }

            double score;

            if (bundle.containsKey("cached_score")) {
                score = bundle.getDouble("cached_score");
            } else {
                score = -1.0D;
            }

            replacePkg = bundle.getString("com.google.android.leanbacklauncher.replacespackage");
            return new TvRecommendation(
                    packageName,
                    paramStatusBarNotification.getKey(),
                    l,
                    group,
                    sortKey,
                    localPendingIntent,
                    autoDismiss,
                    imgWidth,
                    imgHeight,
                    color,
                    localBitmap,
                    backgroundImageUri,
                    title,
                    subtitle,
                    info,
                    icon,
                    progressMax,
                    progress,
                    score,
                    replacePkg);
        }
        // }

        return null;
    }
}
