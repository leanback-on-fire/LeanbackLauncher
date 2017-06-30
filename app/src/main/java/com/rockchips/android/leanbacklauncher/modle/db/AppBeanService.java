package com.rockchips.android.leanbacklauncher.modle.db;

import android.util.Log;

import com.rockchips.android.leanbacklauncher.LauncherApplication;

import java.util.ArrayList;
import java.util.List;

import momo.cn.edu.fjnu.androidutils.base.BaseBeanService;

/**
 * 数据库的增删改查，这里使用泛型方法
 * Created by GaoFei on 2016/3/25.
 */
public abstract class AppBeanService<T> implements BaseBeanService<T> {
    private final String TAG = AppBeanService.class.getSimpleName();
    @Override
    public void save(T object) {
        try {
            LauncherApplication.mDBManager.save(object);
        } catch (Exception e) {
            Log.e(TAG, "存储对象发生异常：" + e);
            e.printStackTrace();
        }
    }

    @Override
    public void delete(T object) {
        try {
            LauncherApplication.mDBManager.delete(object);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(T object) {
        try {
            LauncherApplication.mDBManager.update(object);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public  List<T> getAll(Class<T> tClass) {
       List<T> lists = new ArrayList<T>();
        try {
            lists = LauncherApplication.mDBManager.findAll(tClass);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lists;
    }

    @Override
    public T getObjectById(Class<T> tClass, Object id) {
        T object = null;
        try {
            object = LauncherApplication.mDBManager.findById(tClass, id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return object;
    }

    @Override
    public abstract boolean isExist(T object) ;

    @Override
    public void saveAll(List<T> objects) {
        try {
        		LauncherApplication.mDBManager.save(objects);
			
        } catch (Exception e) {
        	Log.e(TAG, "saveAll->exception:" + e);
            e.printStackTrace();
        }

    }

    @Override
    public void updateAll(List<T> objects) {
        try {
            LauncherApplication.mDBManager.update(objects);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveOrUpdateAll(List<T> objects) {
        try {
            LauncherApplication.mDBManager.saveOrUpdate(objects);
        } catch (Exception e) {
        	Log.e(TAG, "saveOrUpdateAll->exception:" + e);
            e.printStackTrace();
        }
    }
    
    public void saveOrUpdate(T object){
    	  try {
              LauncherApplication.mDBManager.saveOrUpdate(object);
          } catch (Exception e) {
              e.printStackTrace();
          }
    }
}