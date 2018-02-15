package com.rockon999.android.leanbacklauncher.recline.util;

public class RefcountObject<T> {
    private T mObject;
    private int mRefcount;
    private RefcountListener mRefcountListener;

    public interface RefcountListener {
        void onRefcountZero(RefcountObject<?> refcountObject);
    }

    public RefcountObject(T object) {
        this.mObject = object;
    }

    public void setRefcountListener(RefcountListener listener) {
        this.mRefcountListener = listener;
    }

    public synchronized int addRef() {
        this.mRefcount++;
        return this.mRefcount;
    }

    public synchronized int releaseRef() {
        this.mRefcount--;
        if (this.mRefcount == 0 && this.mRefcountListener != null) {
            this.mRefcountListener.onRefcountZero(this);
        }
        return this.mRefcount;
    }

    public T getObject() {
        return this.mObject;
    }
}
