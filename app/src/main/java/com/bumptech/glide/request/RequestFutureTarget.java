package com.bumptech.glide.request;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.annotation.Nullable;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.transition.Transition;
import com.bumptech.glide.util.Util;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class RequestFutureTarget<R>
  implements FutureTarget<R>, Runnable
{
  private static final Waiter DEFAULT_WAITER = new Waiter();
  private final boolean assertBackgroundThread;
  private final int height;
  private boolean isCancelled;
  private boolean loadFailed;
  private final Handler mainHandler;
  @Nullable
  private Request request;
  @Nullable
  private R resource;
  private boolean resultReceived;
  private final Waiter waiter;
  private final int width;
  
  public RequestFutureTarget(Handler paramHandler, int paramInt1, int paramInt2)
  {
    this(paramHandler, paramInt1, paramInt2, true, DEFAULT_WAITER);
  }
  
  RequestFutureTarget(Handler paramHandler, int paramInt1, int paramInt2, boolean paramBoolean, Waiter paramWaiter)
  {
    this.mainHandler = paramHandler;
    this.width = paramInt1;
    this.height = paramInt2;
    this.assertBackgroundThread = paramBoolean;
    this.waiter = paramWaiter;
  }
  
  private void clearOnMainThread()
  {
    this.mainHandler.post(this);
  }
  
  private R doGet(Long paramLong)
    throws ExecutionException, InterruptedException, TimeoutException
  {
    try
    {
      if ((this.assertBackgroundThread) && (!isDone())) {
        Util.assertBackgroundThread();
      }
      if (this.isCancelled) {
        throw new CancellationException();
      }
    }
    finally {}
    if (this.loadFailed) {
      throw new ExecutionException(new IllegalStateException("Load failed"));
    }
    if (this.resultReceived) {}
    for (paramLong = this.resource;; paramLong = this.resource)
    {
      return paramLong;
      if (paramLong == null) {
        this.waiter.waitForTimeout(this, 0L);
      }
      while (Thread.interrupted())
      {
        throw new InterruptedException();
        if (paramLong.longValue() > 0L) {
          this.waiter.waitForTimeout(this, paramLong.longValue());
        }
      }
      if (this.loadFailed) {
        throw new ExecutionException(new IllegalStateException("Load failed"));
      }
      if (this.isCancelled) {
        throw new CancellationException();
      }
      if (!this.resultReceived) {
        throw new TimeoutException();
      }
    }
  }
  
  public boolean cancel(boolean paramBoolean)
  {
    boolean bool1 = true;
    paramBoolean = true;
    for (;;)
    {
      try
      {
        boolean bool2 = this.isCancelled;
        if (bool2) {
          return paramBoolean;
        }
        if (!isDone())
        {
          paramBoolean = bool1;
          if (paramBoolean)
          {
            this.isCancelled = true;
            this.waiter.notifyAll(this);
          }
          clearOnMainThread();
        }
        else
        {
          paramBoolean = false;
        }
      }
      finally {}
    }
  }
  
  public R get()
    throws InterruptedException, ExecutionException
  {
    try
    {
      Object localObject = doGet(null);
      return (R)localObject;
    }
    catch (TimeoutException localTimeoutException)
    {
      throw new AssertionError(localTimeoutException);
    }
  }
  
  public R get(long paramLong, TimeUnit paramTimeUnit)
    throws InterruptedException, ExecutionException, TimeoutException
  {
    return (R)doGet(Long.valueOf(paramTimeUnit.toMillis(paramLong)));
  }
  
  @Nullable
  public Request getRequest()
  {
    return this.request;
  }
  
  public void getSize(SizeReadyCallback paramSizeReadyCallback)
  {
    paramSizeReadyCallback.onSizeReady(this.width, this.height);
  }
  
  public boolean isCancelled()
  {
    try
    {
      boolean bool = this.isCancelled;
      return bool;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  /* Error */
  public boolean isDone()
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 79	com/bumptech/glide/request/RequestFutureTarget:isCancelled	Z
    //   6: ifne +12 -> 18
    //   9: aload_0
    //   10: getfield 96	com/bumptech/glide/request/RequestFutureTarget:resultReceived	Z
    //   13: istore_1
    //   14: iload_1
    //   15: ifeq +9 -> 24
    //   18: iconst_1
    //   19: istore_1
    //   20: aload_0
    //   21: monitorexit
    //   22: iload_1
    //   23: ireturn
    //   24: iconst_0
    //   25: istore_1
    //   26: goto -6 -> 20
    //   29: astore_2
    //   30: aload_0
    //   31: monitorexit
    //   32: aload_2
    //   33: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	34	0	this	RequestFutureTarget
    //   13	13	1	bool	boolean
    //   29	4	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   2	14	29	finally
  }
  
  public void onDestroy() {}
  
  public void onLoadCleared(Drawable paramDrawable) {}
  
  public void onLoadFailed(Drawable paramDrawable)
  {
    try
    {
      this.loadFailed = true;
      this.waiter.notifyAll(this);
      return;
    }
    finally
    {
      paramDrawable = finally;
      throw paramDrawable;
    }
  }
  
  public void onLoadStarted(Drawable paramDrawable) {}
  
  public void onResourceReady(R paramR, Transition<? super R> paramTransition)
  {
    try
    {
      this.resultReceived = true;
      this.resource = paramR;
      this.waiter.notifyAll(this);
      return;
    }
    finally
    {
      paramR = finally;
      throw paramR;
    }
  }
  
  public void onStart() {}
  
  public void onStop() {}
  
  public void run()
  {
    if (this.request != null)
    {
      this.request.clear();
      this.request = null;
    }
  }
  
  public void setRequest(@Nullable Request paramRequest)
  {
    this.request = paramRequest;
  }
  
  static class Waiter
  {
    public void notifyAll(Object paramObject)
    {
      paramObject.notifyAll();
    }
    
    public void waitForTimeout(Object paramObject, long paramLong)
      throws InterruptedException
    {
      paramObject.wait(paramLong);
    }
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/request/RequestFutureTarget.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */