package com.amazon.tv.leanbacklauncher.apps

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDoneException
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.os.AsyncTask
import android.text.TextUtils
import android.util.Log
import com.amazon.tv.leanbacklauncher.apps.AppsDbHelper
import com.amazon.tv.leanbacklauncher.util.Util.setInitialRankingAppliedFlag
import com.amazon.tv.tvrecommendations.service.DbHelper
import com.amazon.tv.tvrecommendations.service.DbStateWriter
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import java.util.concurrent.Executor

class AppsDbHelper(context: Context, databaseName: String?) : SQLiteOpenHelper(context, databaseName, null, 11) {
    private val mContext: Context
    private val mLock: Any
    private var mMostRecentTimeStamp = 0L

    interface Listener {
        fun onEntitiesLoaded(hashMap: HashMap<String, AppsEntity>)
    }

    private inner class LoadEntitiesTask(private val mListener: Listener) : AsyncTask<Void?, Void?, HashMap<String, AppsEntity>>() {
        override fun doInBackground(vararg params: Void?): HashMap<String, AppsEntity> {
            val entities = HashMap<String, AppsEntity>()
            val db = this@AppsDbHelper.writableDatabase
            var c = db.query("entity", null, null, null, null, null, null)
            return try {
                var key: String
                var keyIndex = c.getColumnIndexOrThrow("key")
                while (c.moveToNext()) {
                    key = c.getString(keyIndex)
                    if (!TextUtils.isEmpty(key)) {
                        entities[key] = AppsEntity(mContext, this@AppsDbHelper, key)
                    }
                }
                c = db.query("entity_scores", null, null, null, null, null, null)
                keyIndex = c.getColumnIndexOrThrow("key")
                val componentIndex = c.getColumnIndex("component")
                val entityScoreIndex = c.getColumnIndex("entity_score")
                val lastOpenedIndex = c.getColumnIndex("last_opened")
                while (c.moveToNext()) {
                    var entityScore = 0L
                    key = c.getString(keyIndex)
                    val component = c.getString(componentIndex)
                    if (entityScoreIndex == -1) {
                        entityScore = 0
                    } else {
                        try {
                            entityScore = c.getLong(entityScoreIndex)
                        } catch (th: Throwable) {
                            c.close()
                        }
                    }
                    val lastOpened = if (lastOpenedIndex == -1) 0 else c.getLong(lastOpenedIndex)
                    synchronized(mLock) {
                        if (mMostRecentTimeStamp < lastOpened) {
                            mMostRecentTimeStamp = lastOpened
                        }
                    }
                    if (!TextUtils.isEmpty(key)) {
                        val entity = entities[key]
                        if (entity != null) {
                            entity.setOrder(component, entityScore)
                            entity.setLastOpenedTimeStamp(component, lastOpened)
                        }
                    }
                }
                c.close()
                entities
            } finally {
                c.close()
            }
        }

        public override fun onPostExecute(entities: HashMap<String, AppsEntity>) {
            mListener.onEntitiesLoaded(entities)
        }
    }

    internal interface RecommendationMigrationTable

    private inner class RemoveEntityTask(private val mKey: String, var mFullRemoval: Boolean) : AsyncTask<Void?, Void?, Void?>() {
        override fun doInBackground(vararg params: Void?): Void? {
            val selectionArgs = arrayOf(mKey)
            val db = this@AppsDbHelper.writableDatabase
            db.beginTransaction()
            try {
                db.delete("entity", "key = ?", selectionArgs)
                if (mFullRemoval) {
                    db.delete("entity", "key = ?", selectionArgs)
                }
                db.setTransactionSuccessful()
            } catch (e: SQLiteException) {
                Log.e(TAG, "Could not remove entity " + mKey, e)
            } finally {
                db.endTransaction()
            }
            return null
        }
    }

    private inner class SaveEntityTask(entity: AppsEntity) : AsyncTask<Any?, Any?, Any?>() {
        private val mComponents: MutableList<ContentValues> = ArrayList()
        private val mKey: String?

        override fun doInBackground(vararg params: Any?): Any? {
            val cv = ContentValues()
            cv.put("key", mKey)
            val db = this@AppsDbHelper.writableDatabase
            db.beginTransaction()
            if (db.update("entity", cv, "key = ? ", arrayOf(mKey)) == 0) {
                db.insert("entity", null, cv)
            }
            for (componentValues in mComponents) {
                var count = 0
                val component = componentValues.getAsString("component")
                val timeStamp = componentValues.getAsLong("last_opened")
                synchronized(mLock) {
                    if (mMostRecentTimeStamp < timeStamp) {
                        mMostRecentTimeStamp = timeStamp
                    }
                }
                if (component == null) {
                    try {
                        count = db.update("entity_scores", componentValues, "key = ? AND component IS NULL", arrayOf(mKey))
                    } catch (e: SQLiteException) {
                        Log.e(TAG, "Could not save entity " + mKey, e)
                        db.endTransaction()
                    } catch (th: Throwable) {
                        db.endTransaction()
                    }
                } else {
                    count = db.update("entity_scores", componentValues, "key = ? AND component = ?", arrayOf(mKey, component))
                }
                if (count == 0) {
                    db.insert("entity_scores", null, componentValues)
                }
            }
            db.setTransactionSuccessful()
            db.endTransaction()
            return null
        }

        init {
            mKey = entity.key
            for (component in entity.components) {
                val cv = ContentValues()
                cv.put("key", mKey)
                cv.put("component", component)
                cv.put("entity_score", entity.getOrder(component))
                cv.put("last_opened", entity.getLastOpenedTimeStamp(component))
                mComponents.add(cv)
            }
        }
    }

    private constructor(context: Context) : this(context, "launcher.db") {}

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE IF NOT EXISTS entity ( 'key' TEXT PRIMARY KEY ) ")
        db.execSQL("CREATE TABLE IF NOT EXISTS entity_scores ( 'key' TEXT NOT NULL , component TEXT, entity_score INTEGER NOT NULL , last_opened INTEGER,  PRIMARY KEY ('key', component ),  FOREIGN KEY ( 'key' )  REFERENCES entity ( 'key' )  ) ")
        db.execSQL("CREATE TABLE IF NOT EXISTS rec_migration ( state INTEGER NOT NULL ) ")
        db.execSQL("INSERT INTO rec_migration (state) VALUES (0)")
        setInitialRankingAppliedFlag(mContext, false)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        recreate(db) // TODO FIX
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        recreate(db)
    }

    private fun recreate(db: SQLiteDatabase) {
        db.execSQL("DROP TABLE IF EXISTS entity")
        db.execSQL("DROP TABLE IF EXISTS entity_scores")
        db.execSQL("DROP TABLE IF EXISTS rec_blacklist")
        db.execSQL("DROP TABLE IF EXISTS buckets")
        db.execSQL("DROP TABLE IF EXISTS buffer_scores")
        onCreate(db)
    }

    fun saveEntity(entity: AppsEntity) {
        if (!TextUtils.isEmpty(entity.key)) {
            createSaveEntityTask(entity).execute()
        }
    }

    private fun createSaveEntityTask(entity: AppsEntity): SaveEntityTask {
        return SaveEntityTask(entity)
    }

    fun removeEntity(key: String?, fullRemoval: Boolean) {
            if (key != null && key.isNotEmpty()) {
                RemoveEntityTask(key, fullRemoval).execute()
            }
    }

    fun loadEntities(listener: Listener, executor: Executor?) {
        LoadEntitiesTask(listener).executeOnExecutor(executor)
    }

    val mostRecentTimeStamp: Long
        get() {
            var longValue: Long
            synchronized(mLock) { longValue = mMostRecentTimeStamp }
            return longValue
        }

    @get:Throws(IOException::class)
    val recommendationMigrationFile: File?
        get() {
            val file: File? = null
            return when (recommendationMigrationState) {
                1 -> File(mContext.filesDir, "migration_launcher")
                2 -> {
                    return try {
                        DbHelper.getInstance(mContext).recommendationMigrationFile
                    } catch (e: IllegalStateException) {
                        Log.e(TAG, "Cannot migrate database state back after a downgrade", e)
                        file
                    }
                    file
                }
                else -> file
            }
        }
    private val recommendationMigrationState: Int
        private get() = try {
            DatabaseUtils.longForQuery(readableDatabase, "SELECT state FROM rec_migration", null).toInt()
        } catch (e: SQLiteDoneException) {
            0
        }

    @Throws(IOException::class)
    private fun writeRecommendationMigrationFile(db: SQLiteDatabase) {
        val writer = DbStateWriter(FileOutputStream(File(mContext.filesDir, "migration_launcher")))
        var cursor = db.query("entity", arrayOf("key", "notif_bonus", "bonus_timestamp", "has_recs"), null, null, null, null, null)
        while (cursor.moveToNext()) {
            try {
                writer.writeEntity(cursor.getString(0), cursor.getFloat(1), cursor.getLong(2), cursor.getInt(3) != 0)
            } finally {
                cursor.close()
            }
        }
        cursor = db.query("entity_scores", arrayOf("key", "component", "entity_score", "last_opened"), null, null, null, null, null)
        while (cursor.moveToNext()) {
            try {
                writer.writeComponent(cursor.getString(0), cursor.getString(1), cursor.getInt(2), cursor.getLong(3))
            } finally {
                cursor.close()
            }
        }
        cursor = db.query("buckets", arrayOf("key", "group_id", "last_updated"), null, null, null, null, null)
        while (cursor.moveToNext()) {
            try {
                writer.writeBucket(cursor.getString(0), cursor.getString(1), cursor.getLong(2))
            } finally {
                cursor.close()
            }
        }
        cursor = db.query("buffer_scores", arrayOf("_id", "key", "group_id", "day", "mClicks", "mImpressions"), null, null, null, null, null)
        while (cursor.moveToNext()) {
            try {
                writer.writeSignals(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getInt(3), cursor.getInt(4), cursor.getInt(5))
            } finally {
                cursor.close()
            }
        }
        cursor = db.query("rec_blacklist", arrayOf("key"), null, null, null, null, null)
        while (cursor.moveToNext()) {
            try {
                writer.writeBlacklistedPackage(cursor.getString(0))
            } finally {
                cursor.close()
            }
        }
        writer.close()
    }

    fun onMigrationComplete() {
        writableDatabase.execSQL("UPDATE rec_migration SET state=2")
    }

    companion object {
        private const val TAG = "AppsDbHelper"

        @SuppressLint("StaticFieldLeak")
        private var sAppsDbHelper: AppsDbHelper? = null
        @JvmStatic
        fun getInstance(context: Context): AppsDbHelper? {
            if (sAppsDbHelper == null) {
                synchronized(AppsDbHelper::class.java) {
                    if (sAppsDbHelper == null) {
                        sAppsDbHelper = AppsDbHelper(context.applicationContext)
                    }
                }
            }
            return sAppsDbHelper
        }
    }

    init {
        mLock = Any()
        mContext = context
    }
}