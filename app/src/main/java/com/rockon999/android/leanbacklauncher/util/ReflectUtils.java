package com.rockon999.android.leanbacklauncher.util;

import java.lang.reflect.Method;

/**
 * Created by rockon999 on 2017/6/16.
 */

public class ReflectUtils {
    public static Object invokeMethod(Class<?> className, Object object, String methodName, Class[] paramsClasss, Object[] paramsObjects){
        try{
            Method  method= className.getDeclaredMethod(methodName, paramsClasss);
            method.setAccessible(true);
            return method.invoke(object, paramsObjects);
        }catch (Exception ignored){

        }
        return null;
    }
}
