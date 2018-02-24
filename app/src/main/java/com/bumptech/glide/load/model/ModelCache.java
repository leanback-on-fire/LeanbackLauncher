package com.bumptech.glide.load.model;

import android.support.annotation.Nullable;
import com.bumptech.glide.util.LruCache;
import com.bumptech.glide.util.Util;
import java.util.Queue;

public class ModelCache<A, B>
{
  private static final int DEFAULT_SIZE = 250;
  private final LruCache<ModelKey<A>, B> cache;
  
  public ModelCache()
  {
    this(250);
  }
  
  public ModelCache(int paramInt)
  {
    this.cache = new LruCache(paramInt)
    {
      protected void onItemEvicted(ModelCache.ModelKey<A> paramAnonymousModelKey, B paramAnonymousB)
      {
        paramAnonymousModelKey.release();
      }
    };
  }
  
  public void clear()
  {
    this.cache.clearMemory();
  }
  
  @Nullable
  public B get(A paramA, int paramInt1, int paramInt2)
  {
    paramA = ModelKey.get(paramA, paramInt1, paramInt2);
    Object localObject = this.cache.get(paramA);
    paramA.release();
    return (B)localObject;
  }
  
  public void put(A paramA, int paramInt1, int paramInt2, B paramB)
  {
    paramA = ModelKey.get(paramA, paramInt1, paramInt2);
    this.cache.put(paramA, paramB);
  }
  
  static final class ModelKey<A>
  {
    private static final Queue<ModelKey<?>> KEY_QUEUE = Util.createQueue(0);
    private int height;
    private A model;
    private int width;
    
    static <A> ModelKey<A> get(A paramA, int paramInt1, int paramInt2)
    {
      synchronized (KEY_QUEUE)
      {
        ModelKey localModelKey = (ModelKey)KEY_QUEUE.poll();
        ??? = localModelKey;
        if (localModelKey == null) {
          ??? = new ModelKey();
        }
        ((ModelKey)???).init(paramA, paramInt1, paramInt2);
        return (ModelKey<A>)???;
      }
    }
    
    private void init(A paramA, int paramInt1, int paramInt2)
    {
      this.model = paramA;
      this.width = paramInt1;
      this.height = paramInt2;
    }
    
    public boolean equals(Object paramObject)
    {
      boolean bool2 = false;
      boolean bool1 = bool2;
      if ((paramObject instanceof ModelKey))
      {
        paramObject = (ModelKey)paramObject;
        bool1 = bool2;
        if (this.width == ((ModelKey)paramObject).width)
        {
          bool1 = bool2;
          if (this.height == ((ModelKey)paramObject).height)
          {
            bool1 = bool2;
            if (this.model.equals(((ModelKey)paramObject).model)) {
              bool1 = true;
            }
          }
        }
      }
      return bool1;
    }
    
    public int hashCode()
    {
      return (this.height * 31 + this.width) * 31 + this.model.hashCode();
    }
    
    public void release()
    {
      synchronized (KEY_QUEUE)
      {
        KEY_QUEUE.offer(this);
        return;
      }
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/load/model/ModelCache.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */