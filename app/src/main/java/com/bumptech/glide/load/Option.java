package com.bumptech.glide.load;

import android.support.annotation.Nullable;
import com.bumptech.glide.util.Preconditions;
import java.security.MessageDigest;

public final class Option<T>
{
  private static final CacheKeyUpdater<Object> EMPTY_UPDATER = new CacheKeyUpdater()
  {
    public void update(byte[] paramAnonymousArrayOfByte, Object paramAnonymousObject, MessageDigest paramAnonymousMessageDigest) {}
  };
  private final CacheKeyUpdater<T> cacheKeyUpdater;
  private final T defaultValue;
  private final String key;
  private volatile byte[] keyBytes;
  
  Option(String paramString, T paramT, CacheKeyUpdater<T> paramCacheKeyUpdater)
  {
    this.key = Preconditions.checkNotEmpty(paramString);
    this.defaultValue = paramT;
    this.cacheKeyUpdater = ((CacheKeyUpdater)Preconditions.checkNotNull(paramCacheKeyUpdater));
  }
  
  public static <T> Option<T> disk(String paramString, CacheKeyUpdater<T> paramCacheKeyUpdater)
  {
    return new Option(paramString, null, paramCacheKeyUpdater);
  }
  
  public static <T> Option<T> disk(String paramString, T paramT, CacheKeyUpdater<T> paramCacheKeyUpdater)
  {
    return new Option(paramString, paramT, paramCacheKeyUpdater);
  }
  
  private static <T> CacheKeyUpdater<T> emptyUpdater()
  {
    return EMPTY_UPDATER;
  }
  
  private byte[] getKeyBytes()
  {
    if (this.keyBytes == null) {
      this.keyBytes = this.key.getBytes(Key.CHARSET);
    }
    return this.keyBytes;
  }
  
  public static <T> Option<T> memory(String paramString)
  {
    return new Option(paramString, null, emptyUpdater());
  }
  
  public static <T> Option<T> memory(String paramString, T paramT)
  {
    return new Option(paramString, paramT, emptyUpdater());
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof Option))
    {
      paramObject = (Option)paramObject;
      return this.key.equals(((Option)paramObject).key);
    }
    return false;
  }
  
  @Nullable
  public T getDefaultValue()
  {
    return (T)this.defaultValue;
  }
  
  public int hashCode()
  {
    return this.key.hashCode();
  }
  
  public String toString()
  {
    String str = this.key;
    return String.valueOf(str).length() + 14 + "Option{key='" + str + "'" + "}";
  }
  
  public void update(T paramT, MessageDigest paramMessageDigest)
  {
    this.cacheKeyUpdater.update(getKeyBytes(), paramT, paramMessageDigest);
  }
  
  public static abstract interface CacheKeyUpdater<T>
  {
    public abstract void update(byte[] paramArrayOfByte, T paramT, MessageDigest paramMessageDigest);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/load/Option.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */