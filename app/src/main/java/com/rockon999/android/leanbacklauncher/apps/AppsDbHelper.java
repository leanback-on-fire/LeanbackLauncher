package com.rockon999.android.leanbacklauncher.apps;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.rockon999.android.leanbacklauncher.util.Util;

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
            HashMap<String, AppsEntity> entities = new HashMap<>();
            SQLiteDatabase db = AppsDbHelper.this.getWritableDatabase();
            Cursor c = db.query("entity", null, null, null, null, null, null);
            try {
                String key;
                int keyIndex = c.getColumnIndexOrThrow("key");
                int categoryIndex = c.getColumnIndexOrThrow("is_favorite");
                while (c.moveToNext()) {
                    key = c.getString(keyIndex);
                    boolean isFavorite = c.getInt(categoryIndex) != 0;
                    if (!TextUtils.isEmpty(key)) {
                        entities.put(key, new AppsEntity(AppsDbHelper.this.mContext, AppsDbHelper.this, key, isFavorite));
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
        private final ContentValues mEntity;
        private final String mKey;

        public SaveEntityTask(AppsEntity entity, boolean saveScore) {
            this.mKey = entity.getKey();
            this.mComponents = new ArrayList<>();

            this.mEntity = new ContentValues();
            this.mEntity.put("key", this.mKey);
            this.mEntity.put("is_favorite", entity.isFavorite() ? 1 : 0);

            if (saveScore) {
                for (String component : entity.getComponents()) {
                    ContentValues cv = new ContentValues();
                    cv.put("key", this.mKey);
                    cv.put("component", component);
                    cv.put("entity_score", entity.getOrder(component));
                    cv.put("last_opened", entity.getLastOpenedTimeStamp(component));
                    this.mComponents.add(cv);
                }
            }
        }

        protected Void doInBackground(Void... param) {
            SQLiteDatabase db = AppsDbHelper.this.getWritableDatabase();
            if (db.update("entity", this.mEntity, "key = ? ", new String[]{this.mKey}) == 0) {
                db.insert("entity", null, this.mEntity);
            }

            for (ContentValues componentValues : this.mComponents) {
                int count;
                String component = componentValues.getAsString("component");
                long timeStamp = componentValues.getAsLong("last_opened");
                synchronized (AppsDbHelper.this.mLock) {
                    if (AppsDbHelper.this.mMostRecentTimeStamp < timeStamp) {
                        AppsDbHelper.this.mMostRecentTimeStamp = timeStamp;
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
        super(context, databaseName, null, 12);
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
        db.execSQL("CREATE TABLE IF NOT EXISTS entity ( 'key' TEXT PRIMARY KEY, is_favorite INTEGER DEFAULT 0 ) ");
        db.execSQL("CREATE TABLE IF NOT EXISTS entity_scores ( 'key' TEXT NOT NULL , component TEXT, entity_score INTEGER NOT NULL , last_opened INTEGER,  PRIMARY KEY ( 'key', component ),  FOREIGN KEY ( 'key' )  REFERENCES entity ( 'key' )  ) ");
        db.execSQL("CREATE TABLE IF NOT EXISTS rec_migration ( state INTEGER NOT NULL ) ");
        db.execSQL("INSERT INTO rec_migration (state) VALUES (0)");
        Util.setInitialRankingAppliedFlag(this.mContext, false);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("ALTER TABLE entity ADD COLUMN is_favorite INTEGER DEFAULT 0");
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

    public void saveEntity(AppsEntity entity, boolean saveScores) {
        if (!TextUtils.isEmpty(entity.getKey())) {
            createSaveEntityTask(entity, saveScores).execute();
        }
    }

    AsyncTask<Void, Void, Void> createSaveEntityTask(AppsEntity entity, boolean saveScores) {
        return new SaveEntityTask(entity, saveScores);
    }

    public void removeEntity(String key, boolean fullRemoval) {
        if (!TextUtils.isEmpty(key)) {
            new RemoveEntityTask(key, fullRemoval).execute();
        }
    }

    public void loadEntities(Listener listener) {
        new LoadEntitiesTask(listener).execute();
    }

    public long getMostRecentTimeStamp() {
        long longValue;
        synchronized (this.mLock) {
            longValue = this.mMostRecentTimeStamp;
        }
        return longValue;
    }

    public void onMigrationComplete() {
        getWritableDatabase().execSQL("UPDATE rec_migration SET state=2");
    }
}
