package com.bumptech.glide.load.engine.cache;

import android.support.v4.util.Pools.Pool;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.util.LruCache;
import com.bumptech.glide.util.Util;
import com.bumptech.glide.util.pool.FactoryPools;
import com.bumptech.glide.util.pool.FactoryPools.Factory;
import com.bumptech.glide.util.pool.FactoryPools.Poolable;
import com.bumptech.glide.util.pool.StateVerifier;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SafeKeyGenerator
{
  private final Pools.Pool<PoolableDigestContainer> digestPool = FactoryPools.threadSafe(10, new FactoryPools.Factory()
  {
    public SafeKeyGenerator.PoolableDigestContainer create()
    {
      try
      {
        SafeKeyGenerator.PoolableDigestContainer localPoolableDigestContainer = new SafeKeyGenerator.PoolableDigestContainer(MessageDigest.getInstance("SHA-256"));
        return localPoolableDigestContainer;
      }
      catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
      {
        throw new RuntimeException(localNoSuchAlgorithmException);
      }
    }
  });
  private final LruCache<Key, String> loadIdToSafeHash = new LruCache(1000);
  
  private String calculateHexStringDigest(Key paramKey)
  {
    PoolableDigestContainer localPoolableDigestContainer = (PoolableDigestContainer)this.digestPool.acquire();
    try
    {
      paramKey.updateDiskCacheKey(localPoolableDigestContainer.messageDigest);
      paramKey = Util.sha256BytesToHex(localPoolableDigestContainer.messageDigest.digest());
      return paramKey;
    }
    finally
    {
      this.digestPool.release(localPoolableDigestContainer);
    }
  }
  
  public String getSafeKey(Key paramKey)
  {
    synchronized (this.loadIdToSafeHash)
    {
      ??? = (String)this.loadIdToSafeHash.get(paramKey);
      ??? = ???;
      if (??? == null) {
        ??? = calculateHexStringDigest(paramKey);
      }
    }
    synchronized (this.loadIdToSafeHash)
    {
      this.loadIdToSafeHash.put(paramKey, ???);
      return (String)???;
      paramKey = finally;
      throw paramKey;
    }
  }
  
  private static final class PoolableDigestContainer
    implements FactoryPools.Poolable
  {
    private final MessageDigest messageDigest;
    private final StateVerifier stateVerifier = StateVerifier.newInstance();
    
    PoolableDigestContainer(MessageDigest paramMessageDigest)
    {
      this.messageDigest = paramMessageDigest;
    }
    
    public StateVerifier getVerifier()
    {
      return this.stateVerifier;
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/load/engine/cache/SafeKeyGenerator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */