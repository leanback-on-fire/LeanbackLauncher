package com.bumptech.glide.util;

import android.text.TextUtils;
import java.util.Collection;

public final class Preconditions
{
  public static void checkArgument(boolean paramBoolean, String paramString)
  {
    if (!paramBoolean) {
      throw new IllegalArgumentException(paramString);
    }
  }
  
  public static String checkNotEmpty(String paramString)
  {
    if (TextUtils.isEmpty(paramString)) {
      throw new IllegalArgumentException("Must not be null or empty");
    }
    return paramString;
  }
  
  public static <T extends Collection<Y>, Y> T checkNotEmpty(T paramT)
  {
    if (paramT.isEmpty()) {
      throw new IllegalArgumentException("Must not be empty.");
    }
    return paramT;
  }
  
  public static <T> T checkNotNull(T paramT)
  {
    return (T)checkNotNull(paramT, "Argument must not be null");
  }
  
  public static <T> T checkNotNull(T paramT, String paramString)
  {
    if (paramT == null) {
      throw new NullPointerException(paramString);
    }
    return paramT;
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/util/Preconditions.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */