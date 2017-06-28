package com.rockchips.android.leanbacklauncher.tvrecommendations.service;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;

import com.rockchips.android.leanbacklauncher.tvrecommendations.TvRecommendation;

public class RecommendationsUtil {
    private RecommendationsUtil() {
    }

    public static boolean isRecommendation(StatusBarNotification sbn) {
        return TextUtils.equals(sbn.getNotification().category, "recommendation");
    }

    public static boolean isInPartnerRow(Context context, StatusBarNotification sbn) {
        if (isPackageOnSystem(context.getPackageManager(), sbn.getPackageName())) {
            return "partner_row_entry".equals(sbn.getNotification().getGroup());
        }
        return false;
    }

    public static boolean isCaptivePortal(StatusBarNotification sbn) {
        return TextUtils.equals(sbn.getTag(), "CaptivePortal.Notification");
    }

    public static boolean equals(StatusBarNotification left, StatusBarNotification right) {
        boolean z = true;
        if (left != null && right != null) {
            return TextUtils.equals(left.getPackageName(), right.getPackageName()) && left.getId() == right.getId() && TextUtils.equals(left.getTag(), right.getTag());
        } else {
            if (left != right) {
                z = false;
            }
            return z;
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
        if ((imgWidth > maxWidth || imgHeight > maxHeight) && imgWidth > 0 && imgHeight > 0) {
            float scale = Math.min(1.0f, ((float) maxHeight) / ((float) imgHeight));
            if (((double) scale) < 1.0d || imgWidth > maxWidth) {
                float deltaW = ((float) Math.max(((int) (((float) imgWidth) * scale)) - maxWidth, 0)) / scale;
                Matrix matrix = new Matrix();
                matrix.postScale(scale, scale);
                Bitmap newImage = Bitmap.createBitmap(image, (int) (deltaW / 2.0f), 0, (int) (((float) imgWidth) - deltaW), imgHeight, matrix, true);
                if (newImage != null) {
                    return newImage;
                }
            }
        }
        return image;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static TvRecommendation fromStatusBarNotification(StatusBarNotification r30) {
        /*
        r26 = r30.getId();
        r28 = r30.getTag();
        r3 = r30.getPackageName();
        r5 = r30.getPostTime();
        r27 = r30.getNotification();
        r0 = r27;
        r9 = r0.contentIntent;
        r7 = r27.getGroup();
        r8 = r27.getSortKey();
        r0 = r27;
        r2 = r0.flags;
        r2 = r2 & 16;
        if (r2 == 0) goto L_0x00e0;
    L_0x0028:
        r10 = 1;
    L_0x0029:
        r0 = r27;
        r13 = r0.color;
        r0 = r27;
        r14 = r0.largeIcon;
        r0 = r27;
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
        r0 = r27;
        r0 = r0.extras;
        r25 = r0;
        if (r25 == 0) goto L_0x00d6;
    L_0x0050:
        r2 = "notif_img_width";
        r29 = -1;
        r0 = r25;
        r1 = r29;
        r11 = r0.getInt(r2, r1);
        r2 = "notif_img_height";
        r29 = -1;
        r0 = r25;
        r1 = r29;
        r12 = r0.getInt(r2, r1);
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
        if (r2 == 0) goto L_0x00b9;
    L_0x00a3:
        r2 = "android.progressIndeterminate";
        r0 = r25;
        r2 = r0.containsKey(r2);
        if (r2 == 0) goto L_0x00e3;
    L_0x00ae:
        r2 = "android.progressIndeterminate";
        r0 = r25;
        r2 = r0.getBoolean(r2);
        if (r2 == 0) goto L_0x00e3;
    L_0x00b9:
        r2 = "cached_score";
        r0 = r25;
        r2 = r0.containsKey(r2);
        if (r2 == 0) goto L_0x00f6;
    L_0x00c4:
        r2 = "cached_score";
        r0 = r25;
        r22 = r0.getDouble(r2);
    L_0x00cd:
        r2 = "com.rockchips.android.leanbacklauncher.replacespackage";
        r0 = r25;
        r24 = r0.getString(r2);
    L_0x00d6:
        r4 = r30.getKey();
        r2 = new com.rockchips.android.tvrecommendations.TvRecommendation;
        r2.<init>(r3, r4, r5, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16, r17, r18, r19, r20, r21, r22, r24);
        return r2;
    L_0x00e0:
        r10 = 0;
        goto L_0x0029;
    L_0x00e3:
        r2 = "android.progressMax";
        r0 = r25;
        r20 = r0.getInt(r2);
        r2 = "android.progress";
        r0 = r25;
        r21 = r0.getInt(r2);
        goto L_0x00b9;
    L_0x00f6:
        r22 = -4616189618054758400; // 0xbff0000000000000 float:0.0 double:-1.0;
        goto L_0x00cd;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.rockchips.android.tvrecommendations.service.RecommendationsUtil.fromStatusBarNotification(android.service.notification.StatusBarNotification):com.rockchips.android.tvrecommendations.TvRecommendation");
    }
}
