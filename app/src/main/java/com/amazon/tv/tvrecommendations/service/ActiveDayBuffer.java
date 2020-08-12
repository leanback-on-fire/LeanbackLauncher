package com.amazon.tv.tvrecommendations.service;

import android.util.SparseArray;

import java.util.Date;

class ActiveDayBuffer {
    protected final SparseArray<Signals> mBuffer;
    protected boolean mDirty = true;
    protected final int mLength;
    protected double mScore = -1.0d;

    public ActiveDayBuffer(int length) {
        this.mLength = length;
        this.mBuffer = new SparseArray(length + 1);
    }

    public void set(Date date, Signals value) {
        this.mBuffer.put(DateUtil.getDay(date), value);
        while (this.mBuffer.size() > this.mLength) {
            this.mBuffer.removeAt(0);
        }
        this.mDirty = true;
    }

    public int size() {
        return this.mLength;
    }

    public Signals get(Date date) {
        return this.mBuffer.get(DateUtil.getDay(date));
    }

    public Signals getAt(int index) {
        if (index < 0 || index >= this.mBuffer.size()) {
            return null;
        }
        return this.mBuffer.valueAt(index);
    }

    public int getDayAt(int index) {
        if (index < 0 || index >= this.mBuffer.size()) {
            return -1;
        }
        return this.mBuffer.keyAt(index);
    }

    public boolean hasData() {
        return this.mBuffer.size() > 0;
    }

    public double getAggregatedScore(Aggregator<Signals> aggregator) {
        if (!this.mDirty) {
            return this.mScore;
        }
        aggregator.reset();
        if (this.mBuffer.size() == 0) {
            this.mScore = Ranker.getGroupStarterScore();
            this.mDirty = false;
            return this.mScore;
        }
        for (int i = 0; i < this.mBuffer.size(); i++) {
            aggregator.add(DateUtil.getDate(this.mBuffer.keyAt(i)), this.mBuffer.valueAt(i));
        }
        this.mScore = aggregator.getAggregatedScore();
        this.mDirty = false;
        return this.mScore;
    }
}
