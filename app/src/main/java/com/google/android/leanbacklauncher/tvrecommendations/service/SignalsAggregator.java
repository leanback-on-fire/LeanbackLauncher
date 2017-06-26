package com.google.android.leanbacklauncher.tvrecommendations.service;

import java.util.Date;

class SignalsAggregator implements Aggregator<Signals> {
    private SumAggregator<Integer> mClicks;
    private SumAggregator<Integer> mImpressions;

    SignalsAggregator() {
        this.mClicks = new SumAggregator();
        this.mImpressions = new SumAggregator();
    }

    public void add(Date date, Signals value) {
        this.mClicks.add(date, Integer.valueOf(value.mClicks));
        this.mImpressions.add(date, Integer.valueOf(value.mImpressions));
    }

    public double getAggregatedScore() {
        double impressions = this.mImpressions.getAggregatedScore();
        if (impressions > 0.0d) {
            return this.mClicks.getAggregatedScore() / impressions;
        }
        return 0.0d;
    }

    public void reset() {
        this.mClicks.reset();
        this.mImpressions.reset();
    }
}
