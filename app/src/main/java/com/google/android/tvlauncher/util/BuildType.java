package com.google.android.tvlauncher.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public final class BuildType {
    public static final Boolean DEBUG = Boolean.FALSE;
    public static final Boolean DOGFOOD = Boolean.FALSE;

    public static <T> T newInstance(Class<T> clazz, String className, Object... constructorArgs) {
        ReflectiveOperationException e;
        try {
            Class<? extends T> componentClass = Class.forName(className).asSubclass(clazz);
            Constructor<?>[] constructors = componentClass.getConstructors();
            if (constructors.length == 1) {
                return componentClass.getConstructor(constructors[0].getParameterTypes()).newInstance(constructorArgs);
            }
            throw new RuntimeException("Optional component must have exactly one constructor: " + className);
        } catch (ClassNotFoundException e2) {
            e = e2;
            throw new RuntimeException("Cannot create " + className, e);
        } catch (IllegalAccessException e3) {
            e = e3;
            throw new RuntimeException("Cannot create " + className, e);
        } catch (InstantiationException e4) {
            e = e4;
            throw new RuntimeException("Cannot create " + className, e);
        } catch (InvocationTargetException e5) {
            e = e5;
            throw new RuntimeException("Cannot create " + className, e);
        } catch (NoSuchMethodException e6) {
            e = e6;
            throw new RuntimeException("Cannot create " + className, e);
        }
    }

    private BuildType() {
    }
}
