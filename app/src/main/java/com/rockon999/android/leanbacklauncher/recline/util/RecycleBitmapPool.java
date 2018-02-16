package com.rockon999.android.leanbacklauncher.recline.util;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.util.Log;
import android.util.SparseArray;

import java.lang.ref.SoftReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class RecycleBitmapPool {
    private static Method sGetAllocationByteCount;
    private final SparseArray<ArrayList<SoftReference<Bitmap>>> mRecycled8888;

    static {
        try {
            sGetAllocationByteCount = Bitmap.class.getMethod("getAllocationByteCount");
        } catch (NoSuchMethodException ignored) {
        }
    }

    public RecycleBitmapPool() {
        this.mRecycled8888 = new SparseArray<>();
    }

    public static int getSize(Bitmap bitmap) {
        if (sGetAllocationByteCount != null) {
            try {
                return (Integer) sGetAllocationByteCount.invoke(bitmap);
            } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException e) {
                Log.e("RecycleBitmapPool", "getAllocationByteCount() failed", e);
                sGetAllocationByteCount = null;
                return bitmap.getByteCount();
            }
        }
        return bitmap.getByteCount();
    }

    private static int getSize(int width, int height) {
        if (width >= 2048 || height >= 2048) {
            return 0;
        }
        return (width * height) * 4;
    }

    public void addRecycledBitmap(Bitmap bitmap) {
        if (!bitmap.isRecycled() && bitmap.getConfig() == Config.ARGB_8888) {
            int key = getSize(bitmap);
            if (key != 0) {
                synchronized (this.mRecycled8888) {
                    ArrayList<SoftReference<Bitmap>> list = this.mRecycled8888.get(key);
                    if (list == null) {
                        list = new ArrayList<>();
                        this.mRecycled8888.put(key, list);
                    }
                    list.add(new SoftReference(bitmap));
                }
            }
        }
    }

    public Bitmap getRecycledBitmap(int width, int height) {
        int key = getSize(width, height);
        if (key == 0) {
            return null;
        }
        synchronized (this.mRecycled8888) {
            Bitmap bitmap = getRecycledBitmap(this.mRecycled8888.get(key));
            if (sGetAllocationByteCount == null || bitmap != null) {
                return bitmap;
            }
            return null;
        }
    }

    private static Bitmap getRecycledBitmap(ArrayList<SoftReference<Bitmap>> list) {
        if (!(list == null || list.isEmpty())) {
            while (!list.isEmpty()) {
                Bitmap bitmap = (Bitmap) ((SoftReference) list.remove(list.size() - 1)).get();
                if (bitmap != null && !bitmap.isRecycled()) {
                    return bitmap;
                }
            }
        }
        return null;
    }
}
