package com.rockon999.android.leanbacklauncher.bean;

import android.text.TextUtils;

import com.rockon999.android.leanbacklauncher.apps.LaunchPoint;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by GaoFei on 2017/6/30.
 * 应用项目
 */

@Table(name = "AppInfo")
public class AppInfo {
    @Column(name = "id", isId = true)
    private int id;
    @Column(name = "compentName")
    private String compentName;
    @Column(name = "appType")
    private int appType;
    @Column(name = "packageName")
    private String packageName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCompentName() {
        return compentName;
    }

    public void setCompentName(String compentName) {
        this.compentName = compentName;
    }

    public int getAppType() {
        return appType;
    }

    public void setAppType(int appType) {
        this.appType = appType;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null)
            return false;
        boolean isEqual = false;
        if(obj instanceof AppInfo){
            AppInfo otherInfo = (AppInfo)obj;
            isEqual = TextUtils.equals(compentName, otherInfo.compentName) && TextUtils.equals(packageName, otherInfo.packageName);
        }else if(obj instanceof LaunchPoint){
            LaunchPoint otherLaunchPoint = (LaunchPoint)obj;
            isEqual = TextUtils.equals(compentName, otherLaunchPoint.getComponentName()) && TextUtils.equals(packageName, otherLaunchPoint.getPackageName());
        }
        return isEqual;
    }

    @Override
    public String toString() {
        return "AppInfo{" +
                "id=" + id +
                ", compentName='" + compentName + '\'' +
                ", appType=" + appType +
                ", packageName='" + packageName + '\'' +
                '}';
    }
}
