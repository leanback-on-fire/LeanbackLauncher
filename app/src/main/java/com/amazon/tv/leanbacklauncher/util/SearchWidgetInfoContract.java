package com.amazon.tv.leanbacklauncher.util;

import android.net.Uri;

public class SearchWidgetInfoContract {
    public static final Uri ICON_CONTENT_URI = Uri.parse("content://com.google.android.katniss.search.WidgetInfoProvider/state");
    public static final Uri SUGGESTIONS_CONTENT_URI = Uri.parse("content://com.google.android.katniss.search.WidgetInfoProvider/suggestions");
}
