package com.google.android.tvrecommendations.service;

class Normalizer {
    private double mSum;

    public Normalizer() {
        reset();
    }

    public void addNormalizeableValue(double value) {
        this.mSum += value;
    }

    public double getNormalizedValue(double value) {
        if (this.mSum != 0.0d) {
            return value / this.mSum;
        }
        return 0.0d;
    }

    public void reset() {
        this.mSum = 0.0d;
    }
}
