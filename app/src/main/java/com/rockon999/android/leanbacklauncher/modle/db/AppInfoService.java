package com.rockon999.android.leanbacklauncher.modle.db;

import com.rockon999.android.leanbacklauncher.LauncherApplication;
import com.rockon999.android.leanbacklauncher.apps.LaunchPoint;
import com.rockon999.android.leanbacklauncher.bean.AppInfo;

import org.xutils.db.sqlite.WhereBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GaoFei on 2017/6/30.
 * App项的增删改查
 */

public class AppInfoService extends AppBeanService<AppInfo>{

    @Override
    public boolean isExist(AppInfo object) {
        return false;
    }

    public List<AppInfo> getAppInfosByType(int type){
        List<AppInfo> appInfos = new ArrayList<>();
        try{
            appInfos = LauncherApplication.mDBManager.selector(AppInfo.class).where("appType", "=", type).findAll();
        }catch (Exception e){
            e.printStackTrace();
        }
        return appInfos;
    }

    public void deleteByType(int type){
        try{
             LauncherApplication.mDBManager.delete(AppInfo.class, WhereBuilder.b("appType", "=", type));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void saveByLaunchPoints(List<LaunchPoint> launchPoints, int type){
        if(launchPoints != null && launchPoints.size() > 0){
            List<AppInfo> saveAppInfos = new ArrayList<>(launchPoints.size());
            for(LaunchPoint itemLaunchPoint : launchPoints){
                if(itemLaunchPoint.isAddItem())
                    continue;
                AppInfo appInfo = new AppInfo();
                appInfo.setAppType(type);
                appInfo.setCompentName(itemLaunchPoint.getComponentName());
                appInfo.setPackageName(itemLaunchPoint.getPackageName());
                saveAppInfos.add(appInfo);
            }
            saveAll(saveAppInfos);
        }
    }
}
