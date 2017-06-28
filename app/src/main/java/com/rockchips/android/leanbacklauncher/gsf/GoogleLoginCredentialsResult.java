package com.rockchips.android.leanbacklauncher.gsf;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;

public class GoogleLoginCredentialsResult implements Parcelable {
    public static final Creator<GoogleLoginCredentialsResult> CREATOR;
    private String mAccount;
    private Intent mCredentialsIntent;
    private String mCredentialsString;

    /* renamed from: com.rockchips.android.gsf.GoogleLoginCredentialsResult.1 */
    static class C01441 implements Creator<GoogleLoginCredentialsResult> {
        C01441() {
        }

        public GoogleLoginCredentialsResult createFromParcel(Parcel in) {
            return new GoogleLoginCredentialsResult(null);
        }

        public GoogleLoginCredentialsResult[] newArray(int size) {
            return new GoogleLoginCredentialsResult[size];
        }
    }

    public GoogleLoginCredentialsResult() {
        this.mCredentialsString = null;
        this.mCredentialsIntent = null;
        this.mAccount = null;
    }

    public int describeContents() {
        return this.mCredentialsIntent != null ? this.mCredentialsIntent.describeContents() : 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(this.mAccount);
        out.writeString(this.mCredentialsString);
        if (this.mCredentialsIntent != null) {
            out.writeInt(1);
            this.mCredentialsIntent.writeToParcel(out, 0);
            return;
        }
        out.writeInt(0);
    }

    static {
        CREATOR = new C01441();
    }

    private GoogleLoginCredentialsResult(Parcel in) {
        readFromParcel(in);
    }

    public void readFromParcel(Parcel in) {
        this.mAccount = in.readString();
        this.mCredentialsString = in.readString();
        int hasIntent = in.readInt();
        this.mCredentialsIntent = null;
        if (hasIntent == 1) {
            this.mCredentialsIntent = new Intent();
            this.mCredentialsIntent.readFromParcel(in);
            this.mCredentialsIntent.setExtrasClassLoader(getClass().getClassLoader());
        }
    }
}
