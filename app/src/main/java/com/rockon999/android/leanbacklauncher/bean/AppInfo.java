package com.rockon999.android.leanbacklauncher.bean;

import com.rockon999.android.leanbacklauncher.util.ApplicationInfo;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by GaoFei on 2017/6/30.
 * 应用项目
 */

@Table(name = "AppInfo")
public class AppInfo extends ApplicationInfo {
    @Column(name = "id", isId = true)
    private int id;
    @Column(name = "componentName")
    private String componentName;
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

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
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
    public String toString() {
        return "AppInfo{" +
                "id=" + id +
                ", componentName='" + componentName + '\'' +
                ", appType=" + appType +
                ", packageName='" + packageName + '\'' +
                '}';
    }
}
