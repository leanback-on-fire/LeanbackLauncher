package com.google.android.gms.internal;

import android.os.Looper;
import android.support.annotation.NonNull;
import com.google.android.gms.common.internal.zzac;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class zzaug
  extends zzauk
{
  private static final AtomicLong zzbOO = new AtomicLong(Long.MIN_VALUE);
  private ExecutorService zzbOE;
  private zzd zzbOF;
  private zzd zzbOG;
  private final PriorityBlockingQueue<FutureTask<?>> zzbOH = new PriorityBlockingQueue();
  private final BlockingQueue<FutureTask<?>> zzbOI = new LinkedBlockingQueue();
  private final Thread.UncaughtExceptionHandler zzbOJ = new zzb("Thread death: Uncaught exception on worker thread");
  private final Thread.UncaughtExceptionHandler zzbOK = new zzb("Thread death: Uncaught exception on network thread");
  private final Object zzbOL = new Object();
  private final Semaphore zzbOM = new Semaphore(2);
  private volatile boolean zzbON;
  
  zzaug(zzauh paramzzauh)
  {
    super(paramzzauh);
  }
  
  private void zza(zzc<?> paramzzc)
  {
    synchronized (this.zzbOL)
    {
      this.zzbOH.add(paramzzc);
      if (this.zzbOF == null)
      {
        this.zzbOF = new zzd("Measurement Worker", this.zzbOH);
        this.zzbOF.setUncaughtExceptionHandler(this.zzbOJ);
        this.zzbOF.start();
        return;
      }
      this.zzbOF.zzhJ();
    }
  }
  
  private void zza(FutureTask<?> paramFutureTask)
  {
    synchronized (this.zzbOL)
    {
      this.zzbOI.add(paramFutureTask);
      if (this.zzbOG == null)
      {
        this.zzbOG = new zzd("Measurement Network", this.zzbOI);
        this.zzbOG.setUncaughtExceptionHandler(this.zzbOK);
        this.zzbOG.start();
        return;
      }
      this.zzbOG.zzhJ();
    }
  }
  
  protected void onInitialize() {}
  
  public void zzLS()
  {
    if (Thread.currentThread() != this.zzbOG) {
      throw new IllegalStateException("Call expected from network thread");
    }
  }
  
  public boolean zzOl()
  {
    return Thread.currentThread() == this.zzbOF;
  }
  
  ExecutorService zzOm()
  {
    synchronized (this.zzbOL)
    {
      if (this.zzbOE == null) {
        this.zzbOE = new ThreadPoolExecutor(0, 1, 30L, TimeUnit.SECONDS, new ArrayBlockingQueue(100));
      }
      ExecutorService localExecutorService = this.zzbOE;
      return localExecutorService;
    }
  }
  
  public boolean zzbb()
  {
    return Looper.myLooper() == Looper.getMainLooper();
  }
  
  public <V> Future<V> zze(Callable<V> paramCallable)
    throws IllegalStateException
  {
    zznA();
    zzac.zzC(paramCallable);
    paramCallable = new zzc(paramCallable, false, "Task exception on worker thread");
    if (Thread.currentThread() == this.zzbOF)
    {
      paramCallable.run();
      return paramCallable;
    }
    zza(paramCallable);
    return paramCallable;
  }
  
  public <V> Future<V> zzf(Callable<V> paramCallable)
    throws IllegalStateException
  {
    zznA();
    zzac.zzC(paramCallable);
    paramCallable = new zzc(paramCallable, true, "Task exception on worker thread");
    if (Thread.currentThread() == this.zzbOF)
    {
      paramCallable.run();
      return paramCallable;
    }
    zza(paramCallable);
    return paramCallable;
  }
  
  public void zzmW()
  {
    if (Thread.currentThread() != this.zzbOF) {
      throw new IllegalStateException("Call expected from worker thread");
    }
  }
  
  public void zzp(Runnable paramRunnable)
    throws IllegalStateException
  {
    zznA();
    zzac.zzC(paramRunnable);
    zza(new zzc(paramRunnable, false, "Task exception on worker thread"));
  }
  
  public void zzq(Runnable paramRunnable)
    throws IllegalStateException
  {
    zznA();
    zzac.zzC(paramRunnable);
    zza(new zzc(paramRunnable, false, "Task exception on network thread"));
  }
  
  static class zza
    extends RuntimeException
  {}
  
  private final class zzb
    implements Thread.UncaughtExceptionHandler
  {
    private final String zzbOP;
    
    public zzb(String paramString)
    {
      zzac.zzC(paramString);
      this.zzbOP = paramString;
    }
    
    public void uncaughtException(Thread paramThread, Throwable paramThrowable)
    {
      try
      {
        zzaug.this.zzMg().zzNT().zzm(this.zzbOP, paramThrowable);
        return;
      }
      finally
      {
        paramThread = finally;
        throw paramThread;
      }
    }
  }
  
  private final class zzc<V>
    extends FutureTask<V>
    implements Comparable<zzc>
  {
    private final boolean mHighPriority;
    private final String zzbOP;
    private final long zzbOR;
    
    zzc(Runnable paramRunnable, boolean paramBoolean, String paramString)
    {
      super(null);
      zzac.zzC(paramString);
      this.zzbOR = zzaug.zzOn().getAndIncrement();
      this.zzbOP = paramString;
      this.mHighPriority = paramBoolean;
      if (this.zzbOR == Long.MAX_VALUE) {
        zzaug.this.zzMg().zzNT().log("Tasks index overflow");
      }
    }
    
    zzc(boolean paramBoolean, String paramString)
    {
      super();
      Object localObject;
      zzac.zzC(localObject);
      this.zzbOR = zzaug.zzOn().getAndIncrement();
      this.zzbOP = ((String)localObject);
      this.mHighPriority = paramString;
      if (this.zzbOR == Long.MAX_VALUE) {
        zzaug.this.zzMg().zzNT().log("Tasks index overflow");
      }
    }
    
    protected void setException(Throwable paramThrowable)
    {
      zzaug.this.zzMg().zzNT().zzm(this.zzbOP, paramThrowable);
      if ((paramThrowable instanceof zzaug.zza)) {
        Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), paramThrowable);
      }
      super.setException(paramThrowable);
    }
    
    public int zzb(@NonNull zzc paramzzc)
    {
      if (this.mHighPriority != paramzzc.mHighPriority) {
        if (!this.mHighPriority) {}
      }
      while (this.zzbOR < paramzzc.zzbOR)
      {
        return -1;
        return 1;
      }
      if (this.zzbOR > paramzzc.zzbOR) {
        return 1;
      }
      zzaug.this.zzMg().zzNU().zzm("Two tasks share the same index. index", Long.valueOf(this.zzbOR));
      return 0;
    }
  }
  
  private final class zzd
    extends Thread
  {
    private final Object zzbOS;
    private final BlockingQueue<FutureTask<?>> zzbOT;
    
    public zzd(BlockingQueue<FutureTask<?>> paramBlockingQueue)
    {
      zzac.zzC(paramBlockingQueue);
      Object localObject;
      zzac.zzC(localObject);
      this.zzbOS = new Object();
      this.zzbOT = ((BlockingQueue)localObject);
      setName(paramBlockingQueue);
    }
    
    private void zza(InterruptedException paramInterruptedException)
    {
      zzaug.this.zzMg().zzNV().zzm(String.valueOf(getName()).concat(" was interrupted"), paramInterruptedException);
    }
    
    /* Error */
    public void run()
    {
      // Byte code:
      //   0: iconst_0
      //   1: istore_1
      //   2: iload_1
      //   3: ifne +27 -> 30
      //   6: aload_0
      //   7: getfield 18	com/google/android/gms/internal/zzaug$zzd:zzbOQ	Lcom/google/android/gms/internal/zzaug;
      //   10: invokestatic 81	com/google/android/gms/internal/zzaug:zza	(Lcom/google/android/gms/internal/zzaug;)Ljava/util/concurrent/Semaphore;
      //   13: invokevirtual 86	java/util/concurrent/Semaphore:acquire	()V
      //   16: iconst_1
      //   17: istore_1
      //   18: goto -16 -> 2
      //   21: astore_3
      //   22: aload_0
      //   23: aload_3
      //   24: invokespecial 88	com/google/android/gms/internal/zzaug$zzd:zza	(Ljava/lang/InterruptedException;)V
      //   27: goto -25 -> 2
      //   30: aload_0
      //   31: getfield 34	com/google/android/gms/internal/zzaug$zzd:zzbOT	Ljava/util/concurrent/BlockingQueue;
      //   34: invokeinterface 94 1 0
      //   39: checkcast 96	java/util/concurrent/FutureTask
      //   42: astore_3
      //   43: aload_3
      //   44: ifnull +67 -> 111
      //   47: aload_3
      //   48: invokevirtual 98	java/util/concurrent/FutureTask:run	()V
      //   51: goto -21 -> 30
      //   54: astore 4
      //   56: aload_0
      //   57: getfield 18	com/google/android/gms/internal/zzaug$zzd:zzbOQ	Lcom/google/android/gms/internal/zzaug;
      //   60: invokestatic 102	com/google/android/gms/internal/zzaug:zzc	(Lcom/google/android/gms/internal/zzaug;)Ljava/lang/Object;
      //   63: astore_3
      //   64: aload_3
      //   65: monitorenter
      //   66: aload_0
      //   67: getfield 18	com/google/android/gms/internal/zzaug$zzd:zzbOQ	Lcom/google/android/gms/internal/zzaug;
      //   70: invokestatic 81	com/google/android/gms/internal/zzaug:zza	(Lcom/google/android/gms/internal/zzaug;)Ljava/util/concurrent/Semaphore;
      //   73: invokevirtual 105	java/util/concurrent/Semaphore:release	()V
      //   76: aload_0
      //   77: getfield 18	com/google/android/gms/internal/zzaug$zzd:zzbOQ	Lcom/google/android/gms/internal/zzaug;
      //   80: invokestatic 102	com/google/android/gms/internal/zzaug:zzc	(Lcom/google/android/gms/internal/zzaug;)Ljava/lang/Object;
      //   83: invokevirtual 108	java/lang/Object:notifyAll	()V
      //   86: aload_0
      //   87: aload_0
      //   88: getfield 18	com/google/android/gms/internal/zzaug$zzd:zzbOQ	Lcom/google/android/gms/internal/zzaug;
      //   91: invokestatic 111	com/google/android/gms/internal/zzaug:zzd	(Lcom/google/android/gms/internal/zzaug;)Lcom/google/android/gms/internal/zzaug$zzd;
      //   94: if_acmpne +215 -> 309
      //   97: aload_0
      //   98: getfield 18	com/google/android/gms/internal/zzaug$zzd:zzbOQ	Lcom/google/android/gms/internal/zzaug;
      //   101: aconst_null
      //   102: invokestatic 114	com/google/android/gms/internal/zzaug:zza	(Lcom/google/android/gms/internal/zzaug;Lcom/google/android/gms/internal/zzaug$zzd;)Lcom/google/android/gms/internal/zzaug$zzd;
      //   105: pop
      //   106: aload_3
      //   107: monitorexit
      //   108: aload 4
      //   110: athrow
      //   111: aload_0
      //   112: getfield 32	com/google/android/gms/internal/zzaug$zzd:zzbOS	Ljava/lang/Object;
      //   115: astore_3
      //   116: aload_3
      //   117: monitorenter
      //   118: aload_0
      //   119: getfield 34	com/google/android/gms/internal/zzaug$zzd:zzbOT	Ljava/util/concurrent/BlockingQueue;
      //   122: invokeinterface 117 1 0
      //   127: ifnonnull +25 -> 152
      //   130: aload_0
      //   131: getfield 18	com/google/android/gms/internal/zzaug$zzd:zzbOQ	Lcom/google/android/gms/internal/zzaug;
      //   134: invokestatic 121	com/google/android/gms/internal/zzaug:zzb	(Lcom/google/android/gms/internal/zzaug;)Z
      //   137: istore_2
      //   138: iload_2
      //   139: ifne +13 -> 152
      //   142: aload_0
      //   143: getfield 32	com/google/android/gms/internal/zzaug$zzd:zzbOS	Ljava/lang/Object;
      //   146: ldc2_w 122
      //   149: invokevirtual 127	java/lang/Object:wait	(J)V
      //   152: aload_3
      //   153: monitorexit
      //   154: aload_0
      //   155: getfield 18	com/google/android/gms/internal/zzaug$zzd:zzbOQ	Lcom/google/android/gms/internal/zzaug;
      //   158: invokestatic 102	com/google/android/gms/internal/zzaug:zzc	(Lcom/google/android/gms/internal/zzaug;)Ljava/lang/Object;
      //   161: astore_3
      //   162: aload_3
      //   163: monitorenter
      //   164: aload_0
      //   165: getfield 34	com/google/android/gms/internal/zzaug$zzd:zzbOT	Ljava/util/concurrent/BlockingQueue;
      //   168: invokeinterface 117 1 0
      //   173: ifnonnull +124 -> 297
      //   176: aload_3
      //   177: monitorexit
      //   178: aload_0
      //   179: getfield 18	com/google/android/gms/internal/zzaug$zzd:zzbOQ	Lcom/google/android/gms/internal/zzaug;
      //   182: invokestatic 102	com/google/android/gms/internal/zzaug:zzc	(Lcom/google/android/gms/internal/zzaug;)Ljava/lang/Object;
      //   185: astore_3
      //   186: aload_3
      //   187: monitorenter
      //   188: aload_0
      //   189: getfield 18	com/google/android/gms/internal/zzaug$zzd:zzbOQ	Lcom/google/android/gms/internal/zzaug;
      //   192: invokestatic 81	com/google/android/gms/internal/zzaug:zza	(Lcom/google/android/gms/internal/zzaug;)Ljava/util/concurrent/Semaphore;
      //   195: invokevirtual 105	java/util/concurrent/Semaphore:release	()V
      //   198: aload_0
      //   199: getfield 18	com/google/android/gms/internal/zzaug$zzd:zzbOQ	Lcom/google/android/gms/internal/zzaug;
      //   202: invokestatic 102	com/google/android/gms/internal/zzaug:zzc	(Lcom/google/android/gms/internal/zzaug;)Ljava/lang/Object;
      //   205: invokevirtual 108	java/lang/Object:notifyAll	()V
      //   208: aload_0
      //   209: aload_0
      //   210: getfield 18	com/google/android/gms/internal/zzaug$zzd:zzbOQ	Lcom/google/android/gms/internal/zzaug;
      //   213: invokestatic 111	com/google/android/gms/internal/zzaug:zzd	(Lcom/google/android/gms/internal/zzaug;)Lcom/google/android/gms/internal/zzaug$zzd;
      //   216: if_acmpne +33 -> 249
      //   219: aload_0
      //   220: getfield 18	com/google/android/gms/internal/zzaug$zzd:zzbOQ	Lcom/google/android/gms/internal/zzaug;
      //   223: aconst_null
      //   224: invokestatic 114	com/google/android/gms/internal/zzaug:zza	(Lcom/google/android/gms/internal/zzaug;Lcom/google/android/gms/internal/zzaug$zzd;)Lcom/google/android/gms/internal/zzaug$zzd;
      //   227: pop
      //   228: aload_3
      //   229: monitorexit
      //   230: return
      //   231: astore 4
      //   233: aload_0
      //   234: aload 4
      //   236: invokespecial 88	com/google/android/gms/internal/zzaug$zzd:zza	(Ljava/lang/InterruptedException;)V
      //   239: goto -87 -> 152
      //   242: astore 4
      //   244: aload_3
      //   245: monitorexit
      //   246: aload 4
      //   248: athrow
      //   249: aload_0
      //   250: aload_0
      //   251: getfield 18	com/google/android/gms/internal/zzaug$zzd:zzbOQ	Lcom/google/android/gms/internal/zzaug;
      //   254: invokestatic 130	com/google/android/gms/internal/zzaug:zze	(Lcom/google/android/gms/internal/zzaug;)Lcom/google/android/gms/internal/zzaug$zzd;
      //   257: if_acmpne +22 -> 279
      //   260: aload_0
      //   261: getfield 18	com/google/android/gms/internal/zzaug$zzd:zzbOQ	Lcom/google/android/gms/internal/zzaug;
      //   264: aconst_null
      //   265: invokestatic 132	com/google/android/gms/internal/zzaug:zzb	(Lcom/google/android/gms/internal/zzaug;Lcom/google/android/gms/internal/zzaug$zzd;)Lcom/google/android/gms/internal/zzaug$zzd;
      //   268: pop
      //   269: goto -41 -> 228
      //   272: astore 4
      //   274: aload_3
      //   275: monitorexit
      //   276: aload 4
      //   278: athrow
      //   279: aload_0
      //   280: getfield 18	com/google/android/gms/internal/zzaug$zzd:zzbOQ	Lcom/google/android/gms/internal/zzaug;
      //   283: invokevirtual 47	com/google/android/gms/internal/zzaug:zzMg	()Lcom/google/android/gms/internal/zzaua;
      //   286: invokevirtual 135	com/google/android/gms/internal/zzaua:zzNT	()Lcom/google/android/gms/internal/zzaua$zza;
      //   289: ldc -119
      //   291: invokevirtual 140	com/google/android/gms/internal/zzaua$zza:log	(Ljava/lang/String;)V
      //   294: goto -66 -> 228
      //   297: aload_3
      //   298: monitorexit
      //   299: goto -269 -> 30
      //   302: astore 4
      //   304: aload_3
      //   305: monitorexit
      //   306: aload 4
      //   308: athrow
      //   309: aload_0
      //   310: aload_0
      //   311: getfield 18	com/google/android/gms/internal/zzaug$zzd:zzbOQ	Lcom/google/android/gms/internal/zzaug;
      //   314: invokestatic 130	com/google/android/gms/internal/zzaug:zze	(Lcom/google/android/gms/internal/zzaug;)Lcom/google/android/gms/internal/zzaug$zzd;
      //   317: if_acmpne +22 -> 339
      //   320: aload_0
      //   321: getfield 18	com/google/android/gms/internal/zzaug$zzd:zzbOQ	Lcom/google/android/gms/internal/zzaug;
      //   324: aconst_null
      //   325: invokestatic 132	com/google/android/gms/internal/zzaug:zzb	(Lcom/google/android/gms/internal/zzaug;Lcom/google/android/gms/internal/zzaug$zzd;)Lcom/google/android/gms/internal/zzaug$zzd;
      //   328: pop
      //   329: goto -223 -> 106
      //   332: astore 4
      //   334: aload_3
      //   335: monitorexit
      //   336: aload 4
      //   338: athrow
      //   339: aload_0
      //   340: getfield 18	com/google/android/gms/internal/zzaug$zzd:zzbOQ	Lcom/google/android/gms/internal/zzaug;
      //   343: invokevirtual 47	com/google/android/gms/internal/zzaug:zzMg	()Lcom/google/android/gms/internal/zzaua;
      //   346: invokevirtual 135	com/google/android/gms/internal/zzaua:zzNT	()Lcom/google/android/gms/internal/zzaua$zza;
      //   349: ldc -119
      //   351: invokevirtual 140	com/google/android/gms/internal/zzaua$zza:log	(Ljava/lang/String;)V
      //   354: goto -248 -> 106
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	357	0	this	zzd
      //   1	17	1	i	int
      //   137	2	2	bool	boolean
      //   21	3	3	localInterruptedException1	InterruptedException
      //   54	55	4	localObject2	Object
      //   231	4	4	localInterruptedException2	InterruptedException
      //   242	5	4	localObject3	Object
      //   272	5	4	localObject4	Object
      //   302	5	4	localObject5	Object
      //   332	5	4	localObject6	Object
      // Exception table:
      //   from	to	target	type
      //   6	16	21	java/lang/InterruptedException
      //   30	43	54	finally
      //   47	51	54	finally
      //   111	118	54	finally
      //   154	164	54	finally
      //   246	249	54	finally
      //   306	309	54	finally
      //   142	152	231	java/lang/InterruptedException
      //   118	138	242	finally
      //   142	152	242	finally
      //   152	154	242	finally
      //   233	239	242	finally
      //   244	246	242	finally
      //   188	228	272	finally
      //   228	230	272	finally
      //   249	269	272	finally
      //   274	276	272	finally
      //   279	294	272	finally
      //   164	178	302	finally
      //   297	299	302	finally
      //   304	306	302	finally
      //   66	106	332	finally
      //   106	108	332	finally
      //   309	329	332	finally
      //   334	336	332	finally
      //   339	354	332	finally
    }
    
    public void zzhJ()
    {
      synchronized (this.zzbOS)
      {
        this.zzbOS.notifyAll();
        return;
      }
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/internal/zzaug.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */