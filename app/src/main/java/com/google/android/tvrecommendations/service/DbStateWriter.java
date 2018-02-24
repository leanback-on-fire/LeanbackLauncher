package com.google.android.tvrecommendations.service;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public class DbStateWriter {
    private final ObjectOutputStream mOut;

    public DbStateWriter(OutputStream stream) throws IOException {
        this.mOut = new ObjectOutputStream(stream);
        this.mOut.writeInt(1);
    }

    public void writeEntity(String key, float bonus, long bonusTime, boolean hasRecommendations) throws IOException {
        this.mOut.writeChar(101);
        this.mOut.writeUTF(key);
        this.mOut.writeFloat(bonus);
        this.mOut.writeLong(bonusTime);
        this.mOut.writeBoolean(hasRecommendations);
    }

    public void writeComponent(String key, String component, int order, long lastOpenedTimestamp) throws IOException {
        this.mOut.writeChar(99);
        this.mOut.writeUTF(key);
        ObjectOutputStream objectOutputStream = this.mOut;
        if (component == null) {
            component = "";
        }
        objectOutputStream.writeUTF(component);
        this.mOut.writeInt(order);
        this.mOut.writeLong(lastOpenedTimestamp);
    }

    public void writeBucket(String key, String groupId, long lastUpdatedTimestamp) throws IOException {
        this.mOut.writeChar(98);
        this.mOut.writeUTF(key);
        ObjectOutputStream objectOutputStream = this.mOut;
        if (groupId == null) {
            groupId = "";
        }
        objectOutputStream.writeUTF(groupId);
        this.mOut.writeLong(lastUpdatedTimestamp);
    }

    public void writeSignals(int id, String key, String groupId, int day, int clicks, int impressions) throws IOException {
        this.mOut.writeChar(115);
        this.mOut.writeInt(id);
        this.mOut.writeUTF(key);
        ObjectOutputStream objectOutputStream = this.mOut;
        if (groupId == null) {
            groupId = "";
        }
        objectOutputStream.writeUTF(groupId);
        this.mOut.writeInt(day);
        this.mOut.writeInt(clicks);
        this.mOut.writeInt(impressions);
    }

    public void writeBlacklistedPackage(String key) throws IOException {
        this.mOut.writeChar(107);
        this.mOut.writeUTF(key);
    }

    public void close() throws IOException {
        this.mOut.close();
    }
}
