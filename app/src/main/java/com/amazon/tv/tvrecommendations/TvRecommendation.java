package com.amazon.tv.tvrecommendations;

import android.app.PendingIntent;
import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

public final class TvRecommendation implements Parcelable {
    public static final Creator<TvRecommendation> CREATOR = new Creator<TvRecommendation>() {
        public TvRecommendation createFromParcel(Parcel parcel) {
            return new TvRecommendation(parcel);
        }

        public TvRecommendation[] newArray(int size) {
            return new TvRecommendation[size];
        }
    };
    private final boolean mAutoDismiss;
    private final String mBackgroundImageUri;
    private final int mColor;
    private final Bitmap mContentImage;
    private final PendingIntent mContentIntent;
    private final String mGroup;
    private final int mHeight;
    private final int mIconResourceId;
    private final String mKey;
    private final String mPackageName;
    private final long mPostTime;
    private final int mProgress;
    private final int mProgressMax;
    private final String mReplacedPackageName;
    private final double mScore;
    private final String mSortKey;
    private final CharSequence mSourceName;
    private final CharSequence mText;
    private final CharSequence mTitle;
    private final int mWidth;

    public TvRecommendation(String packageName, String key, long postTime, String group, String sortKey, PendingIntent contentIntent, boolean autoDismiss, int width, int height, int color, Bitmap image, String backgroundImageUri, CharSequence title, CharSequence contentText, CharSequence label, int iconResourceId, int progressMax, int progress, double score, String replacedPackageName) {
        this.mPackageName = packageName;
        this.mKey = key;
        this.mPostTime = postTime;
        this.mGroup = group;
        this.mSortKey = sortKey;
        this.mContentIntent = contentIntent;
        this.mAutoDismiss = autoDismiss;
        this.mWidth = width;
        this.mHeight = height;
        this.mColor = color;
        this.mContentImage = image;
        this.mBackgroundImageUri = backgroundImageUri;
        this.mTitle = title;
        this.mText = contentText;
        this.mSourceName = label;
        this.mIconResourceId = iconResourceId;
        this.mProgressMax = progressMax;
        this.mProgress = progress;
        this.mScore = score;
        this.mReplacedPackageName = replacedPackageName;
    }

    TvRecommendation(Parcel in) {
        ClassLoader classLoader = TvRecommendation.class.getClassLoader();
        this.mPackageName = in.readString();
        this.mKey = in.readString();
        this.mPostTime = in.readLong();
        this.mGroup = in.readString();
        this.mSortKey = in.readString();
        this.mContentIntent = in.readParcelable(classLoader);
        this.mAutoDismiss = in.readInt() != 0;
        this.mWidth = in.readInt();
        this.mHeight = in.readInt();
        this.mColor = in.readInt();
        this.mContentImage = in.readParcelable(classLoader);
        this.mBackgroundImageUri = in.readString();
        this.mTitle = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(in);
        this.mText = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(in);
        this.mSourceName = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(in);
        this.mIconResourceId = in.readInt();
        this.mProgressMax = in.readInt();
        this.mProgress = in.readInt();
        this.mScore = in.readDouble();
        this.mReplacedPackageName = in.readString();
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(this.mPackageName);
        out.writeString(this.mKey);
        out.writeLong(this.mPostTime);
        out.writeString(this.mGroup);
        out.writeString(this.mSortKey);
        out.writeParcelable(this.mContentIntent, flags);
        out.writeInt(this.mAutoDismiss ? 1 : 0);
        out.writeInt(this.mWidth);
        out.writeInt(this.mHeight);
        out.writeInt(this.mColor);
        out.writeParcelable(this.mContentImage, flags);
        out.writeString(this.mBackgroundImageUri);
        TextUtils.writeToParcel(this.mTitle, out, 0);
        TextUtils.writeToParcel(this.mText, out, 0);
        TextUtils.writeToParcel(this.mSourceName, out, 0);
        out.writeInt(this.mIconResourceId);
        out.writeInt(this.mProgressMax);
        out.writeInt(this.mProgress);
        out.writeDouble(this.mScore);
        out.writeString(this.mReplacedPackageName);
    }

    public int describeContents() {
        return 0;
    }

    public String getPackageName() {
        return this.mPackageName;
    }

    public String getKey() {
        return this.mKey;
    }

    public String getGroup() {
        return this.mGroup;
    }

    public String getSortKey() {
        return this.mSortKey;
    }

    public PendingIntent getContentIntent() {
        return this.mContentIntent;
    }

    public boolean isAutoDismiss() {
        return this.mAutoDismiss;
    }

    public int getWidth() {
        return this.mWidth;
    }

    public int getHeight() {
        return this.mHeight;
    }

    public int getColor() {
        return this.mColor;
    }

    public Bitmap getContentImage() {
        return this.mContentImage;
    }

    public String getBackgroundImageUri() {
        return this.mBackgroundImageUri;
    }

    public CharSequence getTitle() {
        return this.mTitle;
    }

    public CharSequence getText() {
        return this.mText;
    }

    public CharSequence getSourceName() {
        return this.mSourceName;
    }

    public int getBadgeIcon() {
        return this.mIconResourceId;
    }

    public boolean hasProgress() {
        return this.mProgressMax > 0;
    }

    public int getProgressMax() {
        return this.mProgressMax;
    }

    public int getProgress() {
        return this.mProgress;
    }

    public double getScore() {
        return this.mScore;
    }

    public String getReplacedPackageName() {
        return this.mReplacedPackageName;
    }

    public String toString() {
        return super.toString() + " Pkg:" + this.mPackageName + " Title:" + this.mTitle + " Img:" + this.mContentImage + " WxH:" + this.mWidth + "x" + this.mHeight + " Intent:" + this.mContentIntent + " Score:" + this.mScore;
    }
}
