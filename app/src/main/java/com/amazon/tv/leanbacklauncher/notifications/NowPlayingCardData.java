package com.amazon.tv.leanbacklauncher.notifications;

import android.app.PendingIntent;
import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class NowPlayingCardData implements Parcelable {
    public static final Creator<NowPlayingCardData> CREATOR = new Creator<NowPlayingCardData>() {
        public NowPlayingCardData createFromParcel(Parcel in) {
            return new NowPlayingCardData(in);
        }

        public NowPlayingCardData[] newArray(int size) {
            return new NowPlayingCardData[size];
        }
    };
    public String albumArtist;
    public String albumTitle;
    public String artist;
    public Bitmap artwork;
    public Bitmap badgeIcon;
    public PendingIntent clickIntent;
    public long duration;
    public int launchColor;
    public String playerPackage;
    public String title;
    public long trackNumber;
    public long year;

    public NowPlayingCardData(Parcel in) {
        readFromParcel(in);
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(this.title);
        out.writeString(this.artist);
        out.writeString(this.albumArtist);
        out.writeString(this.albumTitle);
        out.writeLong(this.year);
        out.writeLong(this.trackNumber);
        out.writeLong(this.duration);
        out.writeString(this.playerPackage);
        out.writeParcelable(this.artwork, 0);
        out.writeParcelable(this.badgeIcon, 0);
    }

    public void readFromParcel(Parcel in) {
        this.title = in.readString();
        this.artist = in.readString();
        this.albumArtist = in.readString();
        this.albumTitle = in.readString();
        this.year = in.readLong();
        this.trackNumber = in.readLong();
        this.duration = in.readLong();
        this.playerPackage = in.readString();
        ClassLoader classLoader = Bitmap.class.getClassLoader();
        this.artwork = in.readParcelable(classLoader);
        this.badgeIcon = in.readParcelable(classLoader);
    }

    public int describeContents() {
        return 0;
    }
}
