package com.rockchips.android.leanbacklauncher.tvrecommendations.service;

import java.util.Date;

class SumAggregator<T> implements Aggregator<T> {
    private double mSum;

    SumAggregator() {
        this.mSum = 0.0d;
    }

    public void add(Date date, T value) {
        if (value instanceof Integer) {
            this.mSum += (double) ((Integer) value).intValue();
        } else if (value instanceof Long) {
            this.mSum += (double) ((Long) value).longValue();
        } else if (value instanceof Double) {
            this.mSum += ((Double) value).doubleValue();
        }
    }

    public double getAggregatedScore() {
        return this.mSum;
    }

    public void reset() {
        this.mSum = 0.0d;
    }
}
