package com.amazon.tv.tvrecommendations.service;

import android.net.Uri;

public interface DbMigrationContract {
    Uri CONTENT_UPDATE_URI = Uri.parse("content://com.amazon.tv.tvrecommendations.migration/migrated");
    Uri CONTENT_URI = Uri.parse("content://com.amazon.tv.tvrecommendations.migration/data");
}
