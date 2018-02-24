package com.google.android.tvlauncher.model;

public class ChannelPackage {
    private int mChannelCount;
    private String mPackageName;

    public ChannelPackage(String packageName, int channelCount) {
        this.mPackageName = packageName;
        this.mChannelCount = channelCount;
    }

    public String getPackageName() {
        return this.mPackageName;
    }

    public int getChannelCount() {
        return this.mChannelCount;
    }

    public String toString() {
        return "ChannelPackage{mPackageName='" + this.mPackageName + '\'' + ", mChannelCount=" + this.mChannelCount + '}';
    }
}
