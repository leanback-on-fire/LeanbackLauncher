package com.google.android.exoplayer2.upstream;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.TraceUtil;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.util.concurrent.ExecutorService;

public final class Loader
  implements LoaderErrorThrower
{
  public static final int DONT_RETRY = 2;
  public static final int DONT_RETRY_FATAL = 3;
  private static final int MSG_CANCEL = 1;
  private static final int MSG_END_OF_SOURCE = 2;
  private static final int MSG_FATAL_ERROR = 4;
  private static final int MSG_IO_EXCEPTION = 3;
  private static final int MSG_START = 0;
  public static final int RETRY = 0;
  public static final int RETRY_RESET_ERROR_COUNT = 1;
  private LoadTask<? extends Loadable> currentTask;
  private final ExecutorService downloadExecutorService;
  private IOException fatalError;
  
  public Loader(String paramString)
  {
    this.downloadExecutorService = Util.newSingleThreadExecutor(paramString);
  }
  
  public void cancelLoading()
  {
    this.currentTask.cancel(false);
  }
  
  public boolean isLoading()
  {
    return this.currentTask != null;
  }
  
  public void maybeThrowError()
    throws IOException
  {
    maybeThrowError(Integer.MIN_VALUE);
  }
  
  public void maybeThrowError(int paramInt)
    throws IOException
  {
    if (this.fatalError != null) {
      throw this.fatalError;
    }
    if (this.currentTask != null)
    {
      LoadTask localLoadTask = this.currentTask;
      int i = paramInt;
      if (paramInt == Integer.MIN_VALUE) {
        i = this.currentTask.defaultMinRetryCount;
      }
      localLoadTask.maybeThrowError(i);
    }
  }
  
  public void release()
  {
    release(null);
  }
  
  public void release(Runnable paramRunnable)
  {
    if (this.currentTask != null) {
      this.currentTask.cancel(true);
    }
    if (paramRunnable != null) {
      this.downloadExecutorService.submit(paramRunnable);
    }
    this.downloadExecutorService.shutdown();
  }
  
  public <T extends Loadable> long startLoading(T paramT, Callback<T> paramCallback, int paramInt)
  {
    Looper localLooper = Looper.myLooper();
    if (localLooper != null) {}
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkState(bool);
      long l = SystemClock.elapsedRealtime();
      new LoadTask(localLooper, paramT, paramCallback, paramInt, l).start(0L);
      return l;
    }
  }
  
  public static abstract interface Callback<T extends Loader.Loadable>
  {
    public abstract void onLoadCanceled(T paramT, long paramLong1, long paramLong2, boolean paramBoolean);
    
    public abstract void onLoadCompleted(T paramT, long paramLong1, long paramLong2);
    
    public abstract int onLoadError(T paramT, long paramLong1, long paramLong2, IOException paramIOException);
  }
  
  @SuppressLint({"HandlerLeak"})
  private final class LoadTask<T extends Loader.Loadable>
    extends Handler
    implements Runnable
  {
    private static final String TAG = "LoadTask";
    private final Loader.Callback<T> callback;
    private IOException currentError;
    public final int defaultMinRetryCount;
    private int errorCount;
    private volatile Thread executorThread;
    private final T loadable;
    private volatile boolean released;
    private final long startTimeMs;
    
    public LoadTask(T paramT, Loader.Callback<T> paramCallback, int paramInt, long paramLong)
    {
      super();
      this.loadable = paramCallback;
      this.callback = paramInt;
      this.defaultMinRetryCount = paramLong;
      Object localObject;
      this.startTimeMs = localObject;
    }
    
    private void finish()
    {
      Loader.access$002(Loader.this, null);
    }
    
    private long getRetryDelayMillis()
    {
      return Math.min((this.errorCount - 1) * 1000, 5000);
    }
    
    private void submitToExecutor()
    {
      this.currentError = null;
      Loader.this.downloadExecutorService.submit(Loader.this.currentTask);
    }
    
    public void cancel(boolean paramBoolean)
    {
      this.released = paramBoolean;
      this.currentError = null;
      if (hasMessages(0))
      {
        removeMessages(0);
        if (!paramBoolean) {
          sendEmptyMessage(1);
        }
      }
      for (;;)
      {
        if (paramBoolean)
        {
          finish();
          long l = SystemClock.elapsedRealtime();
          this.callback.onLoadCanceled(this.loadable, l, l - this.startTimeMs, true);
        }
        return;
        this.loadable.cancelLoad();
        if (this.executorThread != null) {
          this.executorThread.interrupt();
        }
      }
    }
    
    public void handleMessage(Message paramMessage)
    {
      if (this.released) {}
      do
      {
        return;
        if (paramMessage.what == 0)
        {
          submitToExecutor();
          return;
        }
        if (paramMessage.what == 4) {
          throw ((Error)paramMessage.obj);
        }
        finish();
        long l1 = SystemClock.elapsedRealtime();
        long l2 = l1 - this.startTimeMs;
        if (this.loadable.isLoadCanceled())
        {
          this.callback.onLoadCanceled(this.loadable, l1, l2, false);
          return;
        }
        switch (paramMessage.what)
        {
        default: 
          return;
        case 1: 
          this.callback.onLoadCanceled(this.loadable, l1, l2, false);
          return;
        case 2: 
          this.callback.onLoadCompleted(this.loadable, l1, l2);
          return;
        }
        this.currentError = ((IOException)paramMessage.obj);
        i = this.callback.onLoadError(this.loadable, l1, l2, this.currentError);
        if (i == 3)
        {
          Loader.access$102(Loader.this, this.currentError);
          return;
        }
      } while (i == 2);
      if (i == 1) {}
      for (int i = 1;; i = this.errorCount + 1)
      {
        this.errorCount = i;
        start(getRetryDelayMillis());
        return;
      }
    }
    
    public void maybeThrowError(int paramInt)
      throws IOException
    {
      if ((this.currentError != null) && (this.errorCount > paramInt)) {
        throw this.currentError;
      }
    }
    
    public void run()
    {
      try
      {
        this.executorThread = Thread.currentThread();
        if (!this.loadable.isLoadCanceled()) {
          TraceUtil.beginSection("load:" + this.loadable.getClass().getSimpleName());
        }
        try
        {
          this.loadable.load();
          TraceUtil.endSection();
          if (!this.released)
          {
            sendEmptyMessage(2);
            return;
          }
        }
        finally
        {
          TraceUtil.endSection();
        }
        return;
      }
      catch (IOException localIOException)
      {
        if (!this.released)
        {
          obtainMessage(3, localIOException).sendToTarget();
          return;
        }
      }
      catch (InterruptedException localInterruptedException)
      {
        Assertions.checkState(this.loadable.isLoadCanceled());
        if (!this.released)
        {
          sendEmptyMessage(2);
          return;
        }
      }
      catch (Exception localException)
      {
        Log.e("LoadTask", "Unexpected exception loading stream", localException);
        if (!this.released)
        {
          obtainMessage(3, new Loader.UnexpectedLoaderException(localException)).sendToTarget();
          return;
        }
      }
      catch (Error localError)
      {
        Log.e("LoadTask", "Unexpected error loading stream", localError);
        if (!this.released) {
          obtainMessage(4, localError).sendToTarget();
        }
        throw localError;
      }
    }
    
    public void start(long paramLong)
    {
      if (Loader.this.currentTask == null) {}
      for (boolean bool = true;; bool = false)
      {
        Assertions.checkState(bool);
        Loader.access$002(Loader.this, this);
        if (paramLong <= 0L) {
          break;
        }
        sendEmptyMessageDelayed(0, paramLong);
        return;
      }
      submitToExecutor();
    }
  }
  
  public static abstract interface Loadable
  {
    public abstract void cancelLoad();
    
    public abstract boolean isLoadCanceled();
    
    public abstract void load()
      throws IOException, InterruptedException;
  }
  
  public static final class UnexpectedLoaderException
    extends IOException
  {
    public UnexpectedLoaderException(Exception paramException)
    {
      super(paramException);
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/upstream/Loader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */