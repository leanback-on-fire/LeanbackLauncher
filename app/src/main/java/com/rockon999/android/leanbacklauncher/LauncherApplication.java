package com.rockon999.android.leanbacklauncher;

import android.app.Application;
import android.util.Log;

import com.rockon999.android.leanbacklauncher.data.ConstData;

import org.xutils.DbManager;
import org.xutils.x;

import java.io.File;

import momo.cn.edu.fjnu.androidutils.base.BaseApplication;

public class LauncherApplication extends BaseApplication {
    private static final String TAG = "LauncherApplication";
    public static DbManager mDBManager;

    @Override
    public void onCreate() {
        super.onCreate();
        ConstData.appContext = getApplicationContext();
        Log.i(TAG, "onCreate");
        initDB();
        initCacheDir();
    }

    /**
     * 初始化数据库
     */
    private void initDB() {
        DbManager.DaoConfig dbConfig = new DbManager.DaoConfig().setDbDir(new File(ConstData.DB_DIRECTORY))
                .setDbName(ConstData.DB_NAME).setDbVersion(ConstData.DB_VERSION).setAllowTransaction(true)
                .setDbOpenListener(new DbManager.DbOpenListener() {
                    @Override
                    public void onDbOpened(DbManager db) {
                        // 开启WAL, 对写入加速提升巨大
                        db.getDatabase().enableWriteAheadLogging();
                    }
                }).setDbUpgradeListener(null);
        if (null == mDBManager)
            mDBManager = x.getDb(dbConfig);
    }
    private void initCacheDir(){
        File cacheImgDirFile = new File(ConstData.CACHE_IMG_DIR);
        if(!cacheImgDirFile.exists())
            cacheImgDirFile.mkdirs();
    }
}
