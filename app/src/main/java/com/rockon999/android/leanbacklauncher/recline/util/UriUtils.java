package com.rockon999.android.leanbacklauncher.recline.util;

import android.content.Intent.ShortcutIconResource;
import android.net.Uri;

public final class UriUtils {
    private UriUtils() {
    }

    public static boolean isAndroidResourceUri(Uri uri) {
        return "android.resource".equals(uri.getScheme());
    }

    public static boolean isContentUri(Uri uri) {
        return "content".equals(uri.getScheme()) || "file".equals(uri.getScheme());
    }

    public static boolean isShortcutIconResourceUri(Uri uri) {
        return "shortcut.icon.resource".equals(uri.getScheme());
    }

    public static ShortcutIconResource getIconResource(Uri uri) {
        ShortcutIconResource iconResource;
        if (isAndroidResourceUri(uri)) {
            iconResource = new ShortcutIconResource();
            iconResource.packageName = uri.getAuthority();
            iconResource.resourceName = uri.toString().substring("android.resource".length() + "://".length()).replaceFirst("/", ":");
            return iconResource;
        } else if (isShortcutIconResourceUri(uri)) {
            iconResource = new ShortcutIconResource();
            iconResource.packageName = uri.getAuthority();
            iconResource.resourceName = uri.toString().substring((("shortcut.icon.resource".length() + "://".length()) + iconResource.packageName.length()) + "/".length()).replaceFirst("/", ":");
            return iconResource;
        } else {
            throw new IllegalArgumentException("Invalid resource URI. " + uri);
        }
    }

    public static boolean isWebUri(Uri resourceUri) {
        Object obj;
        if (resourceUri.getScheme() == null) {
            obj = null;
        } else {
            obj = resourceUri.getScheme().toLowerCase();
        }
        return "http".equals(obj) || "https".equals(obj);
    }
}
