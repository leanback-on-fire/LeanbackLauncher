package com.google.android.leanbacklauncher.apps;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDoneException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.leanbacklauncher.util.Util;
import com.google.android.leanbacklauncher.tvrecommendations.service.DbHelper;
import com.google.android.leanbacklauncher.tvrecommendations.service.DbStateWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AppsDbHelper extends SQLiteOpenHelper {
    private static boolean DEBUG;
    private static String TAG;
    private static AppsDbHelper sAppsDbHelper;
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
            HashMap<String, AppsEntity> entities = new HashMap();
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
                    long entityScore = 0;
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
                        if (AppsDbHelper.this.mMostRecentTimeStamp.longValue() < lastOpened) {
                            AppsDbHelper.this.mMostRecentTimeStamp = Long.valueOf(lastOpened);
                        }
                    }
                    if (!TextUtils.isEmpty(key)) {
                        AppsEntity entity = (AppsEntity) entities.get(key);
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
            db.delete("entity", "key = ?", selectionArgs);
            if (this.mFullRemoval) {
                db.delete("entity", "key = ?", selectionArgs);
            }
            return null;
        }
    }

    private class SaveEntityTask extends AsyncTask<Void, Void, Void> {
        private final List<ContentValues> mComponents;
        private final String mKey;

        public SaveEntityTask(AppsEntity entity) {
            this.mKey = entity.getKey();
            this.mComponents = new ArrayList();
            for (String component : entity.getComponents()) {
                ContentValues cv = new ContentValues();
                cv.put("key", this.mKey);
                cv.put("component", component);
                cv.put("entity_score", Long.valueOf(entity.getOrder(component)));
                cv.put("last_opened", Long.valueOf(entity.getLastOpenedTimeStamp(component)));
                this.mComponents.add(cv);
            }
        }

        protected Void doInBackground(Void... param) {
            ContentValues cv = new ContentValues();
            cv.put("key", this.mKey);
            SQLiteDatabase db = AppsDbHelper.this.getWritableDatabase();
            if (db.update("entity", cv, "key = ? ", new String[]{this.mKey}) == 0) {
                db.insert("entity", null, cv);
            }
            for (ContentValues componentValues : this.mComponents) {
                int count;
                String component = componentValues.getAsString("component");
                long timeStamp = componentValues.getAsLong("last_opened").longValue();
                synchronized (AppsDbHelper.this.mLock) {
                    if (AppsDbHelper.this.mMostRecentTimeStamp.longValue() < timeStamp) {
                        AppsDbHelper.this.mMostRecentTimeStamp = Long.valueOf(timeStamp);
                    }
                }
                if (component == null) {
                    count = db.update("entity_scores", componentValues, "key = ? AND component IS NULL", new String[]{this.mKey});
                } else {
                    count = db.update("entity_scores", componentValues, "key = ? AND component = ?", new String[]{this.mKey, component});
                }
                if (count == 0) {
                    db.insert("entity_scores", null, componentValues);
                }
            }
            return null;
        }
    }

    static {
        TAG = "AppsDbHelper";
        DEBUG = false;
        sAppsDbHelper = null;
    }

    public AppsDbHelper(Context context) {
        this(context, "launcher.db");
    }

    public AppsDbHelper(Context context, String databaseName) {
        super(context, databaseName, null, 11);
        this.mMostRecentTimeStamp = new Long(0);
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
        db.execSQL("CREATE TABLE IF NOT EXISTS entity ( key TEXT PRIMARY KEY ) ");
        db.execSQL("CREATE TABLE IF NOT EXISTS entity_scores ( key TEXT NOT NULL , component TEXT, entity_score INTEGER NOT NULL , last_opened INTEGER,  PRIMARY KEY ( key, component ),  FOREIGN KEY ( key )  REFERENCES entity ( key )  ) ");
        db.execSQL("CREATE TABLE IF NOT EXISTS rec_migration ( state INTEGER NOT NULL ) ");
        db.execSQL("INSERT INTO rec_migration (state) VALUES (0)");
        Util.setInitialRankingAppliedFlag(this.mContext, false);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onUpgrade(SQLiteDatabase r19, int r20, int r21) {
        /*
        r18 = this;
        switch(r20) {
            case 6: goto L_0x0007;
            case 7: goto L_0x000f;
            case 8: goto L_0x001f;
            case 9: goto L_0x008b;
            case 10: goto L_0x00e7;
            default: goto L_0x0003;
        };
    L_0x0003:
        r18.recreate(r19);
    L_0x0006:
        return;
    L_0x0007:
        r2 = "CREATE TABLE IF NOT EXISTS rec_blacklist (key TEXT PRIMARY KEY)";
        r0 = r19;
        r0.execSQL(r2);
    L_0x000f:
        r2 = "ALTER TABLE entity ADD COLUMN has_recs INTEGER";
        r0 = r19;
        r0.execSQL(r2);
        r2 = "DELETE FROM rec_blacklist";
        r0 = r19;
        r0.execSQL(r2);
    L_0x001f:
        r2 = "CREATE TABLE IF NOT EXISTS entity_scores ( key TEXT NOT NULL, component TEXT, entity_score INTEGER NOT NULL, last_opened INTEGER,  PRIMARY KEY  (key , component),  FOREIGN KEY  (key) REFERENCES entity (key))";
        r0 = r19;
        r0.execSQL(r2);
        r3 = "entity";
        r2 = 2;
        r4 = new java.lang.String[r2];
        r2 = "key";
        r5 = 0;
        r4[r5] = r2;
        r2 = "last_opened";
        r5 = 1;
        r4[r5] = r2;
        r5 = 0;
        r6 = 0;
        r7 = 0;
        r8 = 0;
        r9 = 0;
        r2 = r19;
        r10 = r2.query(r3, r4, r5, r6, r7, r8, r9);
        r12 = new android.content.ContentValues;	 Catch:{ all -> 0x0083 }
        r12.<init>();	 Catch:{ all -> 0x0083 }
    L_0x0049:
        r2 = r10.moveToNext();	 Catch:{ all -> 0x0083 }
        if (r2 == 0) goto L_0x0088;
    L_0x004f:
        r2 = 0;
        r14 = r10.getString(r2);	 Catch:{ all -> 0x0083 }
        r2 = android.text.TextUtils.isEmpty(r14);	 Catch:{ all -> 0x0083 }
        if (r2 != 0) goto L_0x0049;
    L_0x005a:
        r2 = 1;
        r16 = r10.getLong(r2);	 Catch:{ all -> 0x0083 }
        r2 = "key";
        r12.put(r2, r14);	 Catch:{ all -> 0x0083 }
        r2 = "last_opened";
        r3 = java.lang.Long.valueOf(r16);	 Catch:{ all -> 0x0083 }
        r12.put(r2, r3);	 Catch:{ all -> 0x0083 }
        r2 = "entity_score";
        r3 = java.lang.Long.valueOf(r16);	 Catch:{ all -> 0x0083 }
        r12.put(r2, r3);	 Catch:{ all -> 0x0083 }
        r2 = "entity_scores";
        r3 = 0;
        r0 = r19;
        r0.insert(r2, r3, r12);	 Catch:{ all -> 0x0083 }
        goto L_0x0049;
    L_0x0083:
        r2 = move-exception;
        r10.close();
        throw r2;
    L_0x0088:
        r10.close();
    L_0x008b:
        r3 = "entity";
        r2 = 2;
        r4 = new java.lang.String[r2];
        r2 = "key";
        r5 = 0;
        r4[r5] = r2;
        r2 = "oob_order";
        r5 = 1;
        r4[r5] = r2;
        r5 = 0;
        r6 = 0;
        r7 = 0;
        r8 = 0;
        r9 = 0;
        r2 = r19;
        r11 = r2.query(r3, r4, r5, r6, r7, r8, r9);
        r12 = new android.content.ContentValues;	 Catch:{ all -> 0x00df }
        r12.<init>();	 Catch:{ all -> 0x00df }
    L_0x00ad:
        r2 = r11.moveToNext();	 Catch:{ all -> 0x00df }
        if (r2 == 0) goto L_0x00e4;
    L_0x00b3:
        r2 = 0;
        r14 = r11.getString(r2);	 Catch:{ all -> 0x00df }
        r2 = android.text.TextUtils.isEmpty(r14);	 Catch:{ all -> 0x00df }
        if (r2 != 0) goto L_0x00ad;
    L_0x00be:
        r2 = "entity_score";
        r3 = 1;
        r4 = r11.getLong(r3);	 Catch:{ all -> 0x00df }
        r3 = java.lang.Long.valueOf(r4);	 Catch:{ all -> 0x00df }
        r12.put(r2, r3);	 Catch:{ all -> 0x00df }
        r2 = "entity_scores";
        r3 = "key=?";
        r4 = 1;
        r4 = new java.lang.String[r4];	 Catch:{ all -> 0x00df }
        r5 = 0;
        r4[r5] = r14;	 Catch:{ all -> 0x00df }
        r0 = r19;
        r0.update(r2, r12, r3, r4);	 Catch:{ all -> 0x00df }
        goto L_0x00ad;
    L_0x00df:
        r2 = move-exception;
        r11.close();
        throw r2;
    L_0x00e4:
        r11.close();
    L_0x00e7:
        r2 = "CREATE TABLE IF NOT EXISTS rec_migration (state INTEGER NOT NULL)";
        r0 = r19;
        r0.execSQL(r2);
        r18.writeRecommendationMigrationFile(r19);	 Catch:{ IOException -> 0x00fc }
        r2 = "INSERT INTO rec_migration (state) VALUES (1)";
        r0 = r19;
        r0.execSQL(r2);	 Catch:{ IOException -> 0x00fc }
        goto L_0x0006;
    L_0x00fc:
        r13 = move-exception;
        r2 = "INSERT INTO rec_migration (state) VALUES (0)";
        r0 = r19;
        r0.execSQL(r2);
        goto L_0x0006;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.leanbacklauncher.apps.AppsDbHelper.onUpgrade(android.database.sqlite.SQLiteDatabase, int, int):void");
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
            createSaveEntityTask(entity).execute(new Void[0]);
        }
    }

    AsyncTask<Void, Void, Void> createSaveEntityTask(AppsEntity entity) {
        return new SaveEntityTask(entity);
    }

    public void removeEntity(String key, boolean fullRemoval) {
        if (!TextUtils.isEmpty(key)) {
            new RemoveEntityTask(key, fullRemoval).execute(new Void[0]);
        }
    }

    public void loadEntities(Listener listener) {
        new LoadEntitiesTask(listener).execute(new Void[0]);
    }

    public long getMostRecentTimeStamp() {
        long longValue;
        synchronized (this.mLock) {
            longValue = this.mMostRecentTimeStamp.longValue();
        }
        return longValue;
    }

    public File getRecommendationMigrationFile() throws IOException {
        switch (getRecommendationMigrationState()) {
            case android.support.v7.preference.R.styleable.Preference_android_icon /*0*/:
                return null;
            case android.support.v7.recyclerview.R.styleable.RecyclerView_android_descendantFocusability /*1*/:
                return new File(this.mContext.getFilesDir(), "migration_launcher");
            case android.support.v7.recyclerview.R.styleable.RecyclerView_layoutManager /*2*/:
                return DbHelper.getInstance(this.mContext).getRecommendationMigrationFile();
            default:
                return null;
        }
    }

    public int getRecommendationMigrationState() {
        try {
            return (int) DatabaseUtils.longForQuery(getReadableDatabase(), "SELECT state FROM rec_migration", null);
        } catch (SQLiteDoneException e) {
            return 0;
        }
    }

    public void writeRecommendationMigrationFile(SQLiteDatabase db) throws IOException {
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
        writer.close();
    }

    public void onMigrationComplete() {
        Log.i(TAG, "onMigrationComplete->stackTrace:" + Log.getStackTraceString(new Throwable()));
        getWritableDatabase().execSQL("UPDATE rec_migration SET state=2");
    }
}
