package com.google.android.tvlauncher.data;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.support.annotation.MainThread;
import android.support.annotation.WorkerThread;

import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

final class DataLoadingBackgroundTask
        implements Runnable {
    private static final int CORE_THREAD_POOL_SIZE;
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final boolean DEBUG = false;
    private static final int MAX_POOL_SIZE = 15;
    private static final int MAX_THREAD_POOL_SIZE;
    private static final Object POOL_SYNC;
    private static final String TAG = "DLBackgroundTask";
    private static final int THREAD_KEEP_ALIVE_SECONDS = 5;
    private static Executor sExecutor;
    private static final int WORK_QUEUE_CAPACITY = 256;
    private static InternalHandler sHandler = new InternalHandler();
    private static DataLoadingBackgroundTask sPool;
    private static int sPoolSize = 0;
    private static BlockingQueue<Runnable> sWorkQueue;
    private volatile Callbacks mCallbacks;
    private volatile boolean mCancelled;
    private volatile ContentResolver mContentResolver;
    private volatile Object mExtraParam;
    private volatile Object mExtraResult;
    private DataLoadingBackgroundTask mNext;
    private volatile String[] mProjection;
    private volatile Cursor mResult;
    private volatile String mSelection;
    private volatile String[] mSelectionArgs;
    private volatile String[] mSingleSelectionArg;
    private volatile String mSortOrder;
    private volatile long mTag;
    private volatile Uri mUri;

    static {
        CORE_THREAD_POOL_SIZE = Math.min(CPU_COUNT - 1, 2);
        MAX_THREAD_POOL_SIZE = Math.max(CPU_COUNT - 1, 1);
        POOL_SYNC = new Object();
        sWorkQueue = new LinkedBlockingQueue<>(256);
        sExecutor = new ThreadPoolExecutor(CORE_THREAD_POOL_SIZE, MAX_THREAD_POOL_SIZE, 5L, TimeUnit.SECONDS, sWorkQueue);

    }

    private DataLoadingBackgroundTask(Context paramContext) {
        this.mContentResolver = paramContext.getContentResolver();
    }

    @MainThread
    private void finish() {
        if (this.mCallbacks != null) {
            if (!this.mCancelled) {
                this.mCallbacks.onTaskCompleted(this);
            } else {
                this.mCallbacks.onTaskCancelled(this);
            }
        }

        recycle();
    }

    private boolean isCancelled() {
        return this.mCancelled;
    }

    static DataLoadingBackgroundTask obtain(Context paramContext) {
        synchronized (POOL_SYNC) {
            if (sPool != null) {
                DataLoadingBackgroundTask task = sPool;
                sPool = task.mNext;
                task.mNext = null;
                sPoolSize -= 1;
                return task;
            }
            return new DataLoadingBackgroundTask(paramContext);
        }
    }

    @WorkerThread
    private void performQuery() {
        this.mResult = this.mContentResolver.query(this.mUri, this.mProjection, this.mSelection, this.mSelectionArgs, this.mSortOrder);
        if (this.mResult != null) {
            this.mResult.getCount();
        }
    }

    private void postFinish() {
        sHandler.obtainMessage(0, this).sendToTarget();
    }

    private void recycle() {
        synchronized (POOL_SYNC) {
            if (sPoolSize < 15) {
                resetFields();
                this.mNext = sPool;
                sPool = this;
                sPoolSize += 1;
            }
            return;
        }
    }

    private void resetFields() {
        this.mCancelled = false;
        this.mTag = 0L;
        this.mCallbacks = null;
        this.mUri = null;
        this.mProjection = null;
        this.mSelection = null;
        this.mSelectionArgs = null;
        this.mSortOrder = null;
        this.mExtraParam = null;
        this.mResult = null;
        this.mExtraResult = null;
    }

    void cancel() {
        this.mCancelled = true;
    }

    void execute() {
        try {
            sExecutor.execute(this);
            return;
        } catch (RejectedExecutionException localRejectedExecutionException) {
            if (this.mCallbacks != null) {
                this.mCallbacks.onTaskFailed(this, localRejectedExecutionException);
            }
            recycle();
        }
    }

    Callbacks getCallbacks() {
        return this.mCallbacks;
    }

    Object getExtraParam() {
        return this.mExtraParam;
    }

    Object getExtraResult() {
        return this.mExtraResult;
    }

    String[] getProjection() {
        return this.mProjection;
    }

    Cursor getResult() {
        return this.mResult;
    }

    String getSelection() {
        return this.mSelection;
    }

    String[] getSelectionArgs() {
        return this.mSelectionArgs;
    }

    public String getSortOrder() {
        return this.mSortOrder;
    }

    long getTag() {
        return this.mTag;
    }

    Uri getUri() {
        return this.mUri;
    }

    public void run() {
        Process.setThreadPriority(10);
        if (this.mCancelled) {
            postFinish();
            return;
        }
        try {
            performQuery();
            Binder.flushPendingCommands();
            if (this.mCancelled) {
                postFinish();
                return;
            }
        } catch (Throwable localThrowable) {
            throw new RuntimeException("An error occurred while executing ContentResolver query", localThrowable);
        }
        if (this.mCallbacks != null) {
            this.mCallbacks.onTaskPostProcess(this);
        }
        postFinish();
    }

    DataLoadingBackgroundTask setCallbacks(Callbacks paramCallbacks) {
        this.mCallbacks = paramCallbacks;
        return this;
    }

    DataLoadingBackgroundTask setExtraParam(Object paramObject) {
        this.mExtraParam = paramObject;
        return this;
    }

    DataLoadingBackgroundTask setExtraResult(Object paramObject) {
        this.mExtraResult = paramObject;
        return this;
    }

    DataLoadingBackgroundTask setProjection(String[] paramArrayOfString) {
        this.mProjection = paramArrayOfString;
        return this;
    }

    DataLoadingBackgroundTask setSelection(String paramString) {
        this.mSelection = paramString;
        return this;
    }

    DataLoadingBackgroundTask setSelectionArg(String paramString) {
        if (this.mSingleSelectionArg == null) {
            this.mSingleSelectionArg = new String[1];
        }
        this.mSingleSelectionArg[0] = paramString;
        this.mSelectionArgs = this.mSingleSelectionArg;
        return this;
    }

    DataLoadingBackgroundTask setSelectionArgs(String[] paramArrayOfString) {
        this.mSelectionArgs = paramArrayOfString;
        return this;
    }

    public DataLoadingBackgroundTask setSortOrder(String paramString) {
        this.mSortOrder = paramString;
        return this;
    }

    DataLoadingBackgroundTask setTag(long paramLong) {
        this.mTag = paramLong;
        return this;
    }

    DataLoadingBackgroundTask setUri(Uri paramUri) {
        this.mUri = paramUri;
        return this;
    }

    public String toString() {
        return "DataLoadingBackgroundTask{mTag=" + this.mTag + ", mUri=" + this.mUri + ", mSelection='" + this.mSelection + '\'' + ", mSelectionArgs=" + Arrays.toString(this.mSelectionArgs) + ", mSortOrder='" + this.mSortOrder + '\'' + '}';
    }

    static abstract interface Callbacks {
        @MainThread
        public abstract void onTaskCancelled(DataLoadingBackgroundTask paramDataLoadingBackgroundTask);

        @MainThread
        public abstract void onTaskCompleted(DataLoadingBackgroundTask paramDataLoadingBackgroundTask);

        @MainThread
        public abstract void onTaskFailed(DataLoadingBackgroundTask paramDataLoadingBackgroundTask, Throwable paramThrowable);

        @WorkerThread
        public abstract void onTaskPostProcess(DataLoadingBackgroundTask paramDataLoadingBackgroundTask);
    }

    private static class InternalHandler
            extends Handler {
        InternalHandler() {
            super();
        }

        public void handleMessage(Message paramMessage) {
            ((DataLoadingBackgroundTask) paramMessage.obj).finish();
        }
    }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/data/DataLoadingBackgroundTask.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */