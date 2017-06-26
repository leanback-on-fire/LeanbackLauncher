package com.google.android.leanbacklauncher.migration;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PathPermission;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import com.google.android.leanbacklauncher.apps.AppsDbHelper;
import com.google.android.leanbacklauncher.tvrecommendations.service.DbMigrationContract;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class DbMigrationContentProvider extends ContentProvider {
    private static String TAG;
    private AppsDbHelper mDbHelper;

    static {
        TAG = "DbMigrationCP";
    }

  /*  public DbMigrationContentProvider( Context context, String readPermission, String writePermission, PathPermission[] pathPermissions){

    }*/

    public DbMigrationContentProvider(){

    }
    public DbMigrationContentProvider(AppsDbHelper dbHelper) {
        this.mDbHelper = dbHelper;
    }

    public boolean onCreate() {
        Log.i(TAG, "DbMigrationContentProvider->getContext:" + getContext());
        this.mDbHelper = new AppsDbHelper(getContext(), "migration.db");
        return true;
    }

    public AssetFileDescriptor openTypedAssetFile(Uri uri, String mimeTypeFilter, Bundle opts) throws FileNotFoundException {
        if (DbMigrationContract.CONTENT_URI.equals(uri)) {
            try {
                File file = getAppDbHelper().getRecommendationMigrationFile();
                if (file != null) {
                    return new AssetFileDescriptor(ParcelFileDescriptor.open(file, 268435456), 0, -1);
                }
            } catch (IOException e) {
                Log.e(TAG, "Cannot generate a recommendation migration file", e);
            }
            throw new FileNotFoundException("Can't open " + uri);
        }
        throw new FileNotFoundException("Unsupported URI: " + uri);
    }

    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (!DbMigrationContract.CONTENT_UPDATE_URI.equals(uri)) {
            return 0;
        }
        getAppDbHelper().onMigrationComplete();
        return 1;
    }

    private AppsDbHelper getAppDbHelper() {
        if (this.mDbHelper != null) {
            return this.mDbHelper;
        }
        Log.i(TAG, "getAppDbHelper->getContext:" + getContext());
        return AppsDbHelper.getInstance(getContext());
    }

    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        throw new UnsupportedOperationException();
    }

    public String getType(Uri uri) {
        throw new UnsupportedOperationException();
    }

    public Uri insert(Uri uri, ContentValues values) {
        throw new UnsupportedOperationException();
    }

    public int delete(Uri uri, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException();
    }
}
