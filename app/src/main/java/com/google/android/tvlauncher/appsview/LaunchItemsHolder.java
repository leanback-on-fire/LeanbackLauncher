package com.google.android.tvlauncher.appsview;

import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

class LaunchItemsHolder {
    private static final String TAG = "LaunchItemsHolder";
    private final ArrayList<LaunchItem> mData = new ArrayList();

    private int getListIndexFromRowColIndex(int paramInt1, int paramInt2) {
        return paramInt1 * 4 + paramInt2;
    }

    static Pair<Integer, Integer> getRowColIndexFromListIndex(int paramInt) {
        if (paramInt == -1) {
            return null;
        }
        return new Pair(Integer.valueOf(paramInt / 4), Integer.valueOf(paramInt % 4));
    }

    static int getRowCount(int paramInt) {
        return (paramInt + 4 - 1) / 4;
    }

    public void addItem(LaunchItem paramLaunchItem) {
        this.mData.add(paramLaunchItem);
    }

    public Pair<Integer, Integer> addItemAtIndexElseEnd(int paramInt, LaunchItem paramLaunchItem) {
        int i = paramInt;
        if (paramInt < this.mData.size()) {
            this.mData.add(paramInt, paramLaunchItem);
        } else {
            this.mData.add(paramLaunchItem);
            i = this.mData.size() - 1;
        }

        return getRowColIndexFromListIndex(i);
    }

    Pair<Integer, Integer> findIndex(LaunchItem paramLaunchItem) {
        return getRowColIndexFromListIndex(this.mData.indexOf(paramLaunchItem));
    }

    public List<LaunchItem> getData() {
        return this.mData;
    }

    public LaunchItem getItem(int paramInt1, int paramInt2) {
        int i = getListIndexFromRowColIndex(paramInt1, paramInt2);
        if ((paramInt1 > getNumRows() - 1) || (paramInt1 < 0)) {
            throw new IndexOutOfBoundsException("Row index out of bounds. Given Index : " + paramInt1 + ". Number of Rows : " + getNumRows());
        }
        if ((paramInt2 > 3) || (paramInt2 < 0)) {
            throw new IndexOutOfBoundsException("App index out of bounds. Given Index : " + paramInt2 + ". Max apps per row: " + 4);
        }
        if ((i > this.mData.size() - 1) || (i < 0)) {
            throw new IndexOutOfBoundsException("List index out of bounds. Given Index : " + i + ". Number of items : " + this.mData.size());
        }
        return (LaunchItem) this.mData.get(i);
    }

    int getNumRows() {
        return (this.mData.size() + 4 - 1) / 4;
    }

    List<LaunchItem> getRowData(int paramInt) {
        if ((paramInt > getNumRows() - 1) || (paramInt < 0)) {
            throw new IndexOutOfBoundsException("Row index out of bounds. Given Index : " + paramInt + ". Number of Rows : " + getNumRows());
        }
        return this.mData.subList(getListIndexFromRowColIndex(paramInt, 0), Math.min(getListIndexFromRowColIndex(paramInt, 4), this.mData.size()));
    }

    Pair<Integer, Integer> removeItem(LaunchItem paramLaunchItem) {
        Pair localPair = findIndex(paramLaunchItem);
        this.mData.remove(paramLaunchItem);
        return localPair;
    }

    public void set(int paramInt1, int paramInt2, LaunchItem paramLaunchItem) {
        paramInt1 = getListIndexFromRowColIndex(paramInt1, paramInt2);
        this.mData.set(paramInt1, paramLaunchItem);
    }

    public void set(Pair<Integer, Integer> paramPair, LaunchItem paramLaunchItem) {
        set(((Integer) paramPair.first).intValue(), ((Integer) paramPair.second).intValue(), paramLaunchItem);
    }

    public void setData(ArrayList<LaunchItem> paramArrayList) {
        this.mData.clear();
        this.mData.addAll(paramArrayList);
    }

    public int size() {
        return this.mData.size();
    }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/appsview/LaunchItemsHolder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */