package com.google.android.tvlauncher.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public final class BuildType
{
  public static final Boolean DEBUG = Boolean.FALSE;
  public static final Boolean DOGFOOD = Boolean.FALSE;
  
  public static <T> T newInstance(Class<T> paramClass, String paramString, Object... paramVarArgs)
  {
    try
    {
      paramClass = Class.forName(paramString).asSubclass(paramClass);
      arrayOfConstructor = paramClass.getConstructors();
      if (arrayOfConstructor.length != 1) {
        throw new RuntimeException("Optional component must have exactly one constructor: " + paramString);
      }
    }
    catch (ClassNotFoundException paramClass)
    {
      Constructor[] arrayOfConstructor;
      throw new RuntimeException("Cannot create " + paramString, paramClass);
      paramClass = paramClass.getConstructor(arrayOfConstructor[0].getParameterTypes()).newInstance(paramVarArgs);
      return paramClass;
    }
    catch (InstantiationException paramClass)
    {
      for (;;) {}
    }
    catch (IllegalAccessException paramClass)
    {
      for (;;) {}
    }
    catch (NoSuchMethodException paramClass)
    {
      for (;;) {}
    }
    catch (InvocationTargetException paramClass)
    {
      for (;;) {}
    }
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/util/BuildType.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */