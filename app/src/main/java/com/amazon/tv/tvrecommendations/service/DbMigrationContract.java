package com.amazon.tv.tvrecommendations.service;

import android.net.Uri;

public interface DbMigrationContract {
    public static final Uri CONTENT_UPDATE_URI = Uri.parse("content://com.amazon.tv.tvrecommendations.migration/migrated");
    public static final Uri CONTENT_URI = Uri.parse("content://com.amazon.tv.tvrecommendations.migration/data");
}
