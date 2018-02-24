package com.google.android.tvrecommendations.service;

class Bucket {
    private ActiveDayBuffer mBuffer = new ActiveDayBuffer(14);
    private long mTimeStamp;

    public Bucket(long timestamp) {
        this.mTimeStamp = timestamp;
    }

    public long getTimestamp() {
        return this.mTimeStamp;
    }

    public void setTimestamp(long time) {
        this.mTimeStamp = time;
    }

    public void updateTimestamp() {
        this.mTimeStamp = System.currentTimeMillis();
    }

    public ActiveDayBuffer getBuffer() {
        return this.mBuffer;
    }
}
