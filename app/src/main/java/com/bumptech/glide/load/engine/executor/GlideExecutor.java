package com.bumptech.glide.load.engine.executor;

import android.os.Process;
import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy.Builder;
import android.support.annotation.NonNull;
import android.util.Log;
import java.io.File;
import java.io.FilenameFilter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class GlideExecutor
  extends ThreadPoolExecutor
{
  private static final String CPU_LOCATION = "/sys/devices/system/cpu/";
  private static final String CPU_NAME_REGEX = "cpu[0-9]+";
  public static final String DEFAULT_DISK_CACHE_EXECUTOR_NAME = "disk-cache";
  public static final int DEFAULT_DISK_CACHE_EXECUTOR_THREADS = 1;
  public static final String DEFAULT_SOURCE_EXECUTOR_NAME = "source";
  private static final int MAXIMUM_AUTOMATIC_THREAD_COUNT = 4;
  private static final long SOURCE_UNLIMITED_EXECUTOR_KEEP_ALIVE_TIME_MS = TimeUnit.SECONDS.toMillis(10L);
  private static final String SOURCE_UNLIMITED_EXECUTOR_NAME = "source-unlimited";
  private static final String TAG = "GlideExecutor";
  private final boolean executeSynchronously;
  
  GlideExecutor(int paramInt1, int paramInt2, long paramLong, String paramString, UncaughtThrowableStrategy paramUncaughtThrowableStrategy, boolean paramBoolean1, boolean paramBoolean2)
  {
    this(paramInt1, paramInt2, paramLong, paramString, paramUncaughtThrowableStrategy, paramBoolean1, paramBoolean2, new PriorityBlockingQueue());
  }
  
  GlideExecutor(int paramInt1, int paramInt2, long paramLong, String paramString, UncaughtThrowableStrategy paramUncaughtThrowableStrategy, boolean paramBoolean1, boolean paramBoolean2, BlockingQueue<Runnable> paramBlockingQueue)
  {
    super(paramInt1, paramInt2, paramLong, TimeUnit.MILLISECONDS, paramBlockingQueue, new DefaultThreadFactory(paramString, paramUncaughtThrowableStrategy, paramBoolean1));
    this.executeSynchronously = paramBoolean2;
  }
  
  GlideExecutor(int paramInt, String paramString, UncaughtThrowableStrategy paramUncaughtThrowableStrategy, boolean paramBoolean1, boolean paramBoolean2)
  {
    this(paramInt, paramInt, 0L, paramString, paramUncaughtThrowableStrategy, paramBoolean1, paramBoolean2);
  }
  
  public static int calculateBestThreadCount()
  {
    Object localObject2 = null;
    try
    {
      localObject1 = new File("/sys/devices/system/cpu/").listFiles(new FilenameFilter()
      {
        public boolean accept(File paramAnonymousFile, String paramAnonymousString)
        {
          return GlideExecutor.this.matcher(paramAnonymousString).matches();
        }
      });
      if (localObject1 != null)
      {
        i = localObject1.length;
        return Math.min(4, Math.max(Math.max(1, Runtime.getRuntime().availableProcessors()), i));
      }
    }
    catch (Throwable localThrowable)
    {
      for (;;)
      {
        int i;
        Object localObject1 = localObject2;
        if (Log.isLoggable("GlideExecutor", 6))
        {
          Log.e("GlideExecutor", "Failed to calculate accurate cpu count", localThrowable);
          localObject1 = localObject2;
          continue;
          i = 0;
        }
      }
    }
  }
  
  /* Error */
  private <T> Future<T> maybeWait(Future<T> paramFuture)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 83	com/bumptech/glide/load/engine/executor/GlideExecutor:executeSynchronously	Z
    //   4: ifeq +66 -> 70
    //   7: iconst_0
    //   8: istore_2
    //   9: aload_1
    //   10: invokeinterface 152 1 0
    //   15: istore_3
    //   16: iload_3
    //   17: ifne +43 -> 60
    //   20: aload_1
    //   21: invokeinterface 156 1 0
    //   26: pop
    //   27: goto -18 -> 9
    //   30: astore_1
    //   31: new 158	java/lang/RuntimeException
    //   34: dup
    //   35: aload_1
    //   36: invokespecial 161	java/lang/RuntimeException:<init>	(Ljava/lang/Throwable;)V
    //   39: athrow
    //   40: astore_1
    //   41: iload_2
    //   42: ifeq +9 -> 51
    //   45: invokestatic 167	java/lang/Thread:currentThread	()Ljava/lang/Thread;
    //   48: invokevirtual 170	java/lang/Thread:interrupt	()V
    //   51: aload_1
    //   52: athrow
    //   53: astore 4
    //   55: iconst_1
    //   56: istore_2
    //   57: goto -48 -> 9
    //   60: iload_2
    //   61: ifeq +9 -> 70
    //   64: invokestatic 167	java/lang/Thread:currentThread	()Ljava/lang/Thread;
    //   67: invokevirtual 170	java/lang/Thread:interrupt	()V
    //   70: aload_1
    //   71: areturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	72	0	this	GlideExecutor
    //   0	72	1	paramFuture	Future<T>
    //   8	53	2	i	int
    //   15	2	3	bool	boolean
    //   53	1	4	localInterruptedException	InterruptedException
    // Exception table:
    //   from	to	target	type
    //   20	27	30	java/util/concurrent/ExecutionException
    //   9	16	40	finally
    //   20	27	40	finally
    //   31	40	40	finally
    //   20	27	53	java/lang/InterruptedException
  }
  
  public static GlideExecutor newDiskCacheExecutor()
  {
    return newDiskCacheExecutor(1, "disk-cache", UncaughtThrowableStrategy.DEFAULT);
  }
  
  public static GlideExecutor newDiskCacheExecutor(int paramInt, String paramString, UncaughtThrowableStrategy paramUncaughtThrowableStrategy)
  {
    return new GlideExecutor(paramInt, paramString, paramUncaughtThrowableStrategy, true, false);
  }
  
  public static GlideExecutor newSourceExecutor()
  {
    return newSourceExecutor(calculateBestThreadCount(), "source", UncaughtThrowableStrategy.DEFAULT);
  }
  
  public static GlideExecutor newSourceExecutor(int paramInt, String paramString, UncaughtThrowableStrategy paramUncaughtThrowableStrategy)
  {
    return new GlideExecutor(paramInt, paramString, paramUncaughtThrowableStrategy, false, false);
  }
  
  public static GlideExecutor newUnlimitedSourceExecutor()
  {
    return new GlideExecutor(0, Integer.MAX_VALUE, SOURCE_UNLIMITED_EXECUTOR_KEEP_ALIVE_TIME_MS, "source-unlimited", UncaughtThrowableStrategy.DEFAULT, false, false, new SynchronousQueue());
  }
  
  public void execute(Runnable paramRunnable)
  {
    if (this.executeSynchronously)
    {
      paramRunnable.run();
      return;
    }
    super.execute(paramRunnable);
  }
  
  @NonNull
  public Future<?> submit(Runnable paramRunnable)
  {
    return maybeWait(super.submit(paramRunnable));
  }
  
  @NonNull
  public <T> Future<T> submit(Runnable paramRunnable, T paramT)
  {
    return maybeWait(super.submit(paramRunnable, paramT));
  }
  
  public <T> Future<T> submit(Callable<T> paramCallable)
  {
    return maybeWait(super.submit(paramCallable));
  }
  
  private static final class DefaultThreadFactory
    implements ThreadFactory
  {
    private final String name;
    private final boolean preventNetworkOperations;
    private int threadNum;
    private final GlideExecutor.UncaughtThrowableStrategy uncaughtThrowableStrategy;
    
    DefaultThreadFactory(String paramString, GlideExecutor.UncaughtThrowableStrategy paramUncaughtThrowableStrategy, boolean paramBoolean)
    {
      this.name = paramString;
      this.uncaughtThrowableStrategy = paramUncaughtThrowableStrategy;
      this.preventNetworkOperations = paramBoolean;
    }
    
    public Thread newThread(@NonNull Runnable paramRunnable)
    {
      try
      {
        String str = this.name;
        int i = this.threadNum;
        paramRunnable = new Thread(paramRunnable, String.valueOf(str).length() + 25 + "glide-" + str + "-thread-" + i)
        {
          public void run()
          {
            Process.setThreadPriority(9);
            if (GlideExecutor.DefaultThreadFactory.this.preventNetworkOperations) {
              StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectNetwork().penaltyDeath().build());
            }
            try
            {
              super.run();
              return;
            }
            catch (Throwable localThrowable)
            {
              GlideExecutor.DefaultThreadFactory.this.uncaughtThrowableStrategy.handle(localThrowable);
            }
          }
        };
        this.threadNum += 1;
        return paramRunnable;
      }
      finally
      {
        paramRunnable = finally;
        throw paramRunnable;
      }
    }
  }
  
  public static enum UncaughtThrowableStrategy
  {
    IGNORE,  LOG,  THROW;
    
    public static final UncaughtThrowableStrategy DEFAULT = LOG;
    
    private UncaughtThrowableStrategy() {}
    
    protected void handle(Throwable paramThrowable) {}
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/load/engine/executor/GlideExecutor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */