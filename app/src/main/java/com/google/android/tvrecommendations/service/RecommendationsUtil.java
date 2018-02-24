package com.google.android.tvrecommendations.service;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.Network;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;

public class RecommendationsUtil {
    public static boolean isRecommendation(StatusBarNotification sbn) {
        return (sbn == null || sbn.getNotification() == null || !TextUtils.equals(sbn.getNotification().category, "recommendation")) ? false : true;
    }

    public static boolean isInPartnerRow(Context context, StatusBarNotification sbn) {
        return isPackageOnSystem(context.getPackageManager(), sbn.getPackageName()) && "partner_row_entry".equals(sbn.getNotification().getGroup());
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

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static com.google.android.tvrecommendations.TvRecommendation fromStatusBarNotification(android.service.notification.StatusBarNotification r27) {
        /*
        r3 = r27.getPackageName();
        r5 = r27.getPostTime();
        r26 = r27.getNotification();
        r0 = r26;
        r9 = r0.contentIntent;
        r7 = r26.getGroup();
        r8 = r26.getSortKey();
        r0 = r26;
        r2 = r0.flags;
        r2 = r2 & 16;
        if (r2 == 0) goto L_0x00d6;
    L_0x0020:
        r10 = 1;
    L_0x0021:
        r0 = r26;
        r13 = r0.color;
        r0 = r26;
        r14 = r0.largeIcon;
        r0 = r26;
        r0 = r0.icon;
        r19 = r0;
        r11 = 0;
        r12 = 0;
        r15 = 0;
        r16 = 0;
        r17 = 0;
        r18 = 0;
        r20 = 0;
        r21 = 0;
        r22 = 0;
        r24 = 0;
        r0 = r26;
        r0 = r0.extras;
        r25 = r0;
        if (r25 == 0) goto L_0x00cc;
    L_0x0048:
        r2 = "notif_img_width";
        r4 = -1;
        r0 = r25;
        r11 = r0.getInt(r2, r4);
        r2 = "notif_img_height";
        r4 = -1;
        r0 = r25;
        r12 = r0.getInt(r2, r4);
        r2 = "android.title";
        r0 = r25;
        r16 = r0.get(r2);
        r16 = (java.lang.CharSequence) r16;
        r2 = "android.text";
        r0 = r25;
        r17 = r0.get(r2);
        r17 = (java.lang.CharSequence) r17;
        r2 = "android.infoText";
        r0 = r25;
        r18 = r0.get(r2);
        r18 = (java.lang.CharSequence) r18;
        r2 = "android.backgroundImageUri";
        r0 = r25;
        r15 = r0.getString(r2);
        r20 = 0;
        r21 = 0;
        r2 = "android.progress";
        r0 = r25;
        r2 = r0.containsKey(r2);
        if (r2 == 0) goto L_0x00b2;
    L_0x008e:
        r2 = "android.progressIndeterminate";
        r0 = r25;
        r2 = r0.containsKey(r2);
        if (r2 == 0) goto L_0x00a2;
    L_0x0098:
        r2 = "android.progressIndeterminate";
        r0 = r25;
        r2 = r0.getBoolean(r2);
        if (r2 != 0) goto L_0x00b2;
    L_0x00a2:
        r2 = "android.progressMax";
        r0 = r25;
        r20 = r0.getInt(r2);
        r2 = "android.progress";
        r0 = r25;
        r21 = r0.getInt(r2);
    L_0x00b2:
        r2 = "cached_score";
        r0 = r25;
        r2 = r0.containsKey(r2);
        if (r2 == 0) goto L_0x00d9;
    L_0x00bc:
        r2 = "cached_score";
        r0 = r25;
        r22 = r0.getDouble(r2);
    L_0x00c4:
        r2 = "com.google.android.leanbacklauncher.replacespackage";
        r0 = r25;
        r24 = r0.getString(r2);
    L_0x00cc:
        r2 = new com.google.android.tvrecommendations.TvRecommendation;
        r4 = r27.getKey();
        r2.<init>(r3, r4, r5, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16, r17, r18, r19, r20, r21, r22, r24);
        return r2;
    L_0x00d6:
        r10 = 0;
        goto L_0x0021;
    L_0x00d9:
        r22 = -4616189618054758400; // 0xbff0000000000000 float:0.0 double:-1.0;
        goto L_0x00c4;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.tvrecommendations.service.RecommendationsUtil.fromStatusBarNotification(android.service.notification.StatusBarNotification):com.google.android.tvrecommendations.TvRecommendation");
    }
}
