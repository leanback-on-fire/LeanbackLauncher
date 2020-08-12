package com.amazon.tv.leanbacklauncher.apps;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDoneException;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.amazon.tv.leanbacklauncher.util.Util;
import com.amazon.tv.tvrecommendations.service.DbHelper;
import com.amazon.tv.tvrecommendations.service.DbStateWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executor;

public class AppsDbHelper extends SQLiteOpenHelper {
    private static String TAG = "AppsDbHelper";
    @SuppressLint({"StaticFieldLeak"})
    private static AppsDbHelper sAppsDbHelper = null;
    private Context mContext;
    private final Object mLock;
    private Long mMostRecentTimeStamp;

    public interface Listener {
        void onEntitiesLoaded(HashMap<String, AppsEntity> hashMap);
    }

    private class LoadEntitiesTask extends AsyncTask<Void, Void, HashMap<String, AppsEntity>> {
        private Listener mListener;

        public LoadEntitiesTask(Listener listener) {
            this.mListener = listener;
        }

        protected HashMap<String, AppsEntity> doInBackground(Void... params) {
            HashMap<String, AppsEntity> entities = new HashMap<>();
            SQLiteDatabase db = AppsDbHelper.this.getWritableDatabase();
            Cursor c = db.query("entity", null, null, null, null, null, null);
            try {
                String key;
                int keyIndex = c.getColumnIndexOrThrow("key");
                while (c.moveToNext()) {
                    key = c.getString(keyIndex);
                    if (!TextUtils.isEmpty(key)) {
                        entities.put(key, new AppsEntity(AppsDbHelper.this.mContext, AppsDbHelper.this, key));
                    }
                }
                c = db.query("entity_scores", null, null, null, null, null, null);
                keyIndex = c.getColumnIndexOrThrow("key");
                int componentIndex = c.getColumnIndex("component");
                int entityScoreIndex = c.getColumnIndex("entity_score");
                int lastOpenedIndex = c.getColumnIndex("last_opened");
                while (c.moveToNext()) {
                    long entityScore = 0L;
                    key = c.getString(keyIndex);
                    String component = c.getString(componentIndex);
                    if (entityScoreIndex == -1) {
                        entityScore = 0;
                    } else {
                        try {
                            entityScore = c.getLong(entityScoreIndex);
                        } catch (Throwable th) {
                            c.close();
                        }
                    }
                    long lastOpened = lastOpenedIndex == -1 ? 0 : c.getLong(lastOpenedIndex);
                    synchronized (AppsDbHelper.this.mLock) {
                        if (AppsDbHelper.this.mMostRecentTimeStamp < lastOpened) {
                            AppsDbHelper.this.mMostRecentTimeStamp = lastOpened;
                        }
                    }
                    if (!TextUtils.isEmpty(key)) {
                        AppsEntity entity = entities.get(key);
                        if (entity != null) {
                            entity.setOrder(component, entityScore);
                            entity.setLastOpenedTimeStamp(component, lastOpened);
                        }
                    }
                }
                c.close();
                return entities;
            } finally {
                c.close();
            }
        }

        public void onPostExecute(HashMap<String, AppsEntity> entities) {
            this.mListener.onEntitiesLoaded(entities);
        }
    }

    interface RecommendationMigrationTable {
    }

    private class RemoveEntityTask extends AsyncTask<Void, Void, Void> {
        boolean mFullRemoval;
        private String mKey;

        public RemoveEntityTask(String key, boolean fullRemoval) {
            this.mKey = key;
            this.mFullRemoval = fullRemoval;
        }

        protected Void doInBackground(Void... params) {
            String[] selectionArgs = new String[]{this.mKey};
            SQLiteDatabase db = AppsDbHelper.this.getWritableDatabase();
            db.beginTransaction();
            try {
                db.delete("entity", "key = ?", selectionArgs);
                if (this.mFullRemoval) {
                    db.delete("entity", "key = ?", selectionArgs);
                }
                db.setTransactionSuccessful();
            } catch (SQLiteException e) {
                Log.e(AppsDbHelper.TAG, "Could not remove entity " + this.mKey, e);
            } finally {
                db.endTransaction();
            }
            return null;
        }
    }

    private class SaveEntityTask extends AsyncTask<Void, Void, Void> {
        private final List<ContentValues> mComponents = new ArrayList<>();
        private final String mKey;

        public SaveEntityTask(AppsEntity entity) {
            this.mKey = entity.getKey();
            for (String component : entity.getComponents()) {
                ContentValues cv = new ContentValues();
                cv.put("key", this.mKey);
                cv.put("component", component);
                cv.put("entity_score", entity.getOrder(component));
                cv.put("last_opened", entity.getLastOpenedTimeStamp(component));
                this.mComponents.add(cv);
            }
        }

        protected Void doInBackground(Void... param) {
            ContentValues cv = new ContentValues();
            cv.put("key", this.mKey);
            SQLiteDatabase db = AppsDbHelper.this.getWritableDatabase();
            db.beginTransaction();
            if (db.update("entity", cv, "key = ? ", new String[]{this.mKey}) == 0) {
                db.insert("entity", null, cv);
            }
            for (ContentValues componentValues : this.mComponents) {
                int count = 0;
                String component = componentValues.getAsString("component");
                long timeStamp = componentValues.getAsLong("last_opened");
                synchronized (AppsDbHelper.this.mLock) {
                    if (AppsDbHelper.this.mMostRecentTimeStamp < timeStamp) {
                        AppsDbHelper.this.mMostRecentTimeStamp = timeStamp;
                    }
                }
                if (component == null) {
                    try {
                        count = db.update("entity_scores", componentValues, "key = ? AND component IS NULL", new String[]{this.mKey});
                    } catch (SQLiteException e) {
                        Log.e(AppsDbHelper.TAG, "Could not save entity " + this.mKey, e);
                        db.endTransaction();
                    } catch (Throwable th) {
                        db.endTransaction();
                    }
                } else {
                    count = db.update("entity_scores", componentValues, "key = ? AND component = ?", new String[]{this.mKey, component});
                }
                if (count == 0) {
                    db.insert("entity_scores", null, componentValues);
                }
            }
            db.setTransactionSuccessful();
            db.endTransaction();
            return null;
        }
    }

    private AppsDbHelper(Context context) {
        this(context, "launcher.db");
    }

    public AppsDbHelper(Context context, String databaseName) {
        super(context, databaseName, null, 11);
        this.mMostRecentTimeStamp = 0L;
        this.mLock = new Object();
        this.mContext = context;
    }

    public static AppsDbHelper getInstance(Context context) {
        if (sAppsDbHelper == null) {
            synchronized (AppsDbHelper.class) {
                if (sAppsDbHelper == null) {
                    sAppsDbHelper = new AppsDbHelper(context.getApplicationContext());
                }
            }
        }
        return sAppsDbHelper;
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS entity ( 'key' TEXT PRIMARY KEY ) ");
        db.execSQL("CREATE TABLE IF NOT EXISTS entity_scores ( 'key' TEXT NOT NULL , component TEXT, entity_score INTEGER NOT NULL , last_opened INTEGER,  PRIMARY KEY ('key', component ),  FOREIGN KEY ( 'key' )  REFERENCES entity ( 'key' )  ) ");
        db.execSQL("CREATE TABLE IF NOT EXISTS rec_migration ( state INTEGER NOT NULL ) ");
        db.execSQL("INSERT INTO rec_migration (state) VALUES (0)");
        Util.setInitialRankingAppliedFlag(this.mContext, false);
    }

    public void onUpgrade(android.database.sqlite.SQLiteDatabase r19, int r20, int r21) {
        recreate(r19); // TODO FIX
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        recreate(db);
    }

    private void recreate(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS entity");
        db.execSQL("DROP TABLE IF EXISTS entity_scores");
        db.execSQL("DROP TABLE IF EXISTS rec_blacklist");
        db.execSQL("DROP TABLE IF EXISTS buckets");
        db.execSQL("DROP TABLE IF EXISTS buffer_scores");
        onCreate(db);
    }

    public void saveEntity(AppsEntity entity) {
        if (!TextUtils.isEmpty(entity.getKey())) {
            createSaveEntityTask(entity).execute();
        }
    }

    private AsyncTask<Void, Void, Void> createSaveEntityTask(AppsEntity entity) {
        return new SaveEntityTask(entity);
    }

    public void removeEntity(String key, boolean fullRemoval) {
        if (!TextUtils.isEmpty(key)) {
            new RemoveEntityTask(key, fullRemoval).execute();
        }
    }

    public void loadEntities(Listener listener, Executor executor) {
        new LoadEntitiesTask(listener).executeOnExecutor(executor);
    }

    public long getMostRecentTimeStamp() {
        long longValue;
        synchronized (this.mLock) {
            longValue = this.mMostRecentTimeStamp;
        }
        return longValue;
    }

    public File getRecommendationMigrationFile() throws IOException {
        File file = null;
        switch (getRecommendationMigrationState()) {
            case 1:
                return new File(this.mContext.getFilesDir(), "migration_launcher");
            case 2:
                try {
                    return DbHelper.getInstance(this.mContext).getRecommendationMigrationFile();
                } catch (IllegalStateException e) {
                    Log.e(TAG, "Cannot migrate database state back after a downgrade", e);
                    return file;
                }
            default:
                return file;
        }
    }

    private int getRecommendationMigrationState() {
        try {
            return (int) DatabaseUtils.longForQuery(getReadableDatabase(), "SELECT state FROM rec_migration", null);
        } catch (SQLiteDoneException e) {
            return 0;
        }
    }

    private void writeRecommendationMigrationFile(SQLiteDatabase db) throws IOException {
        DbStateWriter writer = new DbStateWriter(new FileOutputStream(new File(this.mContext.getFilesDir(), "migration_launcher")));
        Cursor cursor = db.query("entity", new String[]{"key", "notif_bonus", "bonus_timestamp", "has_recs"}, null, null, null, null, null);
        while (cursor.moveToNext()) {
            try {
                writer.writeEntity(cursor.getString(0), cursor.getFloat(1), cursor.getLong(2), cursor.getInt(3) != 0);
            } finally {
                cursor.close();
            }
        }
        cursor = db.query("entity_scores", new String[]{"key", "component", "entity_score", "last_opened"}, null, null, null, null, null);
        while (cursor.moveToNext()) {
            try {
                writer.writeComponent(cursor.getString(0), cursor.getString(1), cursor.getInt(2), cursor.getLong(3));
            } finally {
                cursor.close();
            }
        }
        cursor = db.query("buckets", new String[]{"key", "group_id", "last_updated"}, null, null, null, null, null);
        while (cursor.moveToNext()) {
            try {
                writer.writeBucket(cursor.getString(0), cursor.getString(1), cursor.getLong(2));
            } finally {
                cursor.close();
            }
        }
        cursor = db.query("buffer_scores", new String[]{"_id", "key", "group_id", "day", "mClicks", "mImpressions"}, null, null, null, null, null);
        while (cursor.moveToNext()) {
            try {
                writer.writeSignals(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getInt(3), cursor.getInt(4), cursor.getInt(5));
            } finally {
                cursor.close();
            }
        }
        cursor = db.query("rec_blacklist", new String[]{"key"}, null, null, null, null, null);
        while (cursor.moveToNext()) {
            try {
                writer.writeBlacklistedPackage(cursor.getString(0));
            } finally {
                cursor.close();
            }
        }
        writer.close();
    }

    public void onMigrationComplete() {
        getWritableDatabase().execSQL("UPDATE rec_migration SET state=2");
    }
}
