package com.bumptech.glide.load.engine.executor;

import android.os.Process;
import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy.Builder;
import android.util.Log;
import java.io.File;
import java.io.FilenameFilter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public final class GlideExecutor extends ThreadPoolExecutor {
    private static final long SOURCE_UNLIMITED_EXECUTOR_KEEP_ALIVE_TIME_MS = TimeUnit.SECONDS.toMillis(10);
    private final boolean executeSynchronously;

    class AnonymousClass1 implements FilenameFilter {
        final /* synthetic */ Pattern val$cpuNamePattern;

        AnonymousClass1(Pattern pattern) {
            this.val$cpuNamePattern = pattern;
        }

        public boolean accept(File file, String s) {
            return this.val$cpuNamePattern.matcher(s).matches();
        }
    }

    private static final class DefaultThreadFactory implements ThreadFactory {
        private final String name;
        private final boolean preventNetworkOperations;
        private int threadNum;
        private final UncaughtThrowableStrategy uncaughtThrowableStrategy;

        DefaultThreadFactory(String name, UncaughtThrowableStrategy uncaughtThrowableStrategy, boolean preventNetworkOperations) {
            this.name = name;
            this.uncaughtThrowableStrategy = uncaughtThrowableStrategy;
            this.preventNetworkOperations = preventNetworkOperations;
        }

        public synchronized Thread newThread(Runnable runnable) {
            Thread result;
            String str = this.name;
            result = new Thread(runnable, new StringBuilder(String.valueOf(str).length() + 25).append("glide-").append(str).append("-thread-").append(this.threadNum).toString()) {
                public void run() {
                    Process.setThreadPriority(9);
                    if (DefaultThreadFactory.this.preventNetworkOperations) {
                        StrictMode.setThreadPolicy(new Builder().detectNetwork().penaltyDeath().build());
                    }
                    try {
                        super.run();
                    } catch (Throwable t) {
                        DefaultThreadFactory.this.uncaughtThrowableStrategy.handle(t);
                    }
                }
            };
            this.threadNum++;
            return result;
        }
    }

    public enum UncaughtThrowableStrategy {
        IGNORE,
        LOG {
            protected void handle(Throwable t) {
                if (t != null && Log.isLoggable("GlideExecutor", 6)) {
                    Log.e("GlideExecutor", "Request threw uncaught throwable", t);
                }
            }
        },
        THROW {
            protected void handle(Throwable t) {
                super.handle(t);
                if (t != null) {
                    throw new RuntimeException("Request threw uncaught throwable", t);
                }
            }
        };
        
        public static final UncaughtThrowableStrategy DEFAULT = null;

        static {
            DEFAULT = LOG;
        }

        protected void handle(Throwable t) {
        }
    }

    public static GlideExecutor newDiskCacheExecutor() {
        return newDiskCacheExecutor(1, "disk-cache", UncaughtThrowableStrategy.DEFAULT);
    }

    public static GlideExecutor newDiskCacheExecutor(int threadCount, String name, UncaughtThrowableStrategy uncaughtThrowableStrategy) {
        return new GlideExecutor(threadCount, name, uncaughtThrowableStrategy, true, false);
    }

    public static GlideExecutor newSourceExecutor() {
        return newSourceExecutor(calculateBestThreadCount(), "source", UncaughtThrowableStrategy.DEFAULT);
    }

    public static GlideExecutor newSourceExecutor(int threadCount, String name, UncaughtThrowableStrategy uncaughtThrowableStrategy) {
        return new GlideExecutor(threadCount, name, uncaughtThrowableStrategy, false, false);
    }

    public static GlideExecutor newUnlimitedSourceExecutor() {
        return new GlideExecutor(0, Integer.MAX_VALUE, SOURCE_UNLIMITED_EXECUTOR_KEEP_ALIVE_TIME_MS, "source-unlimited", UncaughtThrowableStrategy.DEFAULT, false, false, new SynchronousQueue());
    }

    GlideExecutor(int poolSize, String name, UncaughtThrowableStrategy uncaughtThrowableStrategy, boolean preventNetworkOperations, boolean executeSynchronously) {
        this(poolSize, poolSize, 0, name, uncaughtThrowableStrategy, preventNetworkOperations, executeSynchronously);
    }

    GlideExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTimeInMs, String name, UncaughtThrowableStrategy uncaughtThrowableStrategy, boolean preventNetworkOperations, boolean executeSynchronously) {
        this(corePoolSize, maximumPoolSize, keepAliveTimeInMs, name, uncaughtThrowableStrategy, preventNetworkOperations, executeSynchronously, new PriorityBlockingQueue());
    }

    GlideExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTimeInMs, String name, UncaughtThrowableStrategy uncaughtThrowableStrategy, boolean preventNetworkOperations, boolean executeSynchronously, BlockingQueue<Runnable> queue) {
        super(corePoolSize, maximumPoolSize, keepAliveTimeInMs, TimeUnit.MILLISECONDS, queue, new DefaultThreadFactory(name, uncaughtThrowableStrategy, preventNetworkOperations));
        this.executeSynchronously = executeSynchronously;
    }

    public void execute(Runnable command) {
        if (this.executeSynchronously) {
            command.run();
        } else {
            super.execute(command);
        }
    }

    public Future<?> submit(Runnable task) {
        return maybeWait(super.submit(task));
    }

    private <T> Future<T> maybeWait(Future<T> future) {
        if (this.executeSynchronously) {
            boolean interrupted = false;
            while (!future.isDone()) {
                try {
                    future.get();
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e2) {
                    interrupted = true;
                } catch (Throwable th) {
                    if (interrupted) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
        return future;
    }

    public <T> Future<T> submit(Runnable task, T result) {
        return maybeWait(super.submit(task, result));
    }

    public <T> Future<T> submit(Callable<T> task) {
        return maybeWait(super.submit(task));
    }

    public static int calculateBestThreadCount() {
        File[] cpus = null;
        try {
            cpus = new File("/sys/devices/system/cpu/").listFiles(new AnonymousClass1(Pattern.compile("cpu[0-9]+")));
        } catch (Throwable t) {
            if (Log.isLoggable("GlideExecutor", 6)) {
                Log.e("GlideExecutor", "Failed to calculate accurate cpu count", t);
            }
        }
        return Math.min(4, Math.max(Math.max(1, Runtime.getRuntime().availableProcessors()), cpus != null ? cpus.length : 0));
    }
}
