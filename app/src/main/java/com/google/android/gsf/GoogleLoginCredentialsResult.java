package com.google.android.gsf;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class GoogleLoginCredentialsResult
  implements Parcelable
{
  public static final Parcelable.Creator<GoogleLoginCredentialsResult> CREATOR = new Parcelable.Creator()
  {
    public GoogleLoginCredentialsResult createFromParcel(Parcel paramAnonymousParcel)
    {
      return new GoogleLoginCredentialsResult(paramAnonymousParcel, null);
    }
    
    public GoogleLoginCredentialsResult[] newArray(int paramAnonymousInt)
    {
      return new GoogleLoginCredentialsResult[paramAnonymousInt];
    }
  };
  private String mAccount;
  private Intent mCredentialsIntent;
  private String mCredentialsString;
  
  public GoogleLoginCredentialsResult()
  {
    this.mCredentialsString = null;
    this.mCredentialsIntent = null;
    this.mAccount = null;
  }
  
  private GoogleLoginCredentialsResult(Parcel paramParcel)
  {
    readFromParcel(paramParcel);
  }
  
  public int describeContents()
  {
    if (this.mCredentialsIntent != null) {
      return this.mCredentialsIntent.describeContents();
    }
    return 0;
  }
  
  public String getAccount()
  {
    return this.mAccount;
  }
  
  public Intent getCredentialsIntent()
  {
    return this.mCredentialsIntent;
  }
  
  public String getCredentialsString()
  {
    return this.mCredentialsString;
  }
  
  public void readFromParcel(Parcel paramParcel)
  {
    this.mAccount = paramParcel.readString();
    this.mCredentialsString = paramParcel.readString();
    int i = paramParcel.readInt();
    this.mCredentialsIntent = null;
    if (i == 1)
    {
      this.mCredentialsIntent = new Intent();
      this.mCredentialsIntent.readFromParcel(paramParcel);
      this.mCredentialsIntent.setExtrasClassLoader(getClass().getClassLoader());
    }
  }
  
  public void setAccount(String paramString)
  {
    this.mAccount = paramString;
  }
  
  public void setCredentialsIntent(Intent paramIntent)
  {
    this.mCredentialsIntent = paramIntent;
  }
  
  public void setCredentialsString(String paramString)
  {
    this.mCredentialsString = paramString;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.mAccount);
    paramParcel.writeString(this.mCredentialsString);
    if (this.mCredentialsIntent != null)
    {
      paramParcel.writeInt(1);
      this.mCredentialsIntent.writeToParcel(paramParcel, 0);
      return;
    }
    paramParcel.writeInt(0);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gsf/GoogleLoginCredentialsResult.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */