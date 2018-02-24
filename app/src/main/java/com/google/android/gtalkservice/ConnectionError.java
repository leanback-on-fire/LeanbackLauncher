package com.google.android.gtalkservice;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public final class ConnectionError
  implements Parcelable
{
  public static final int AUTHENTICATION_EXPIRED = 5;
  public static final int AUTHENTICATION_FAILED = 4;
  public static final int CONNECTION_FAILED = 2;
  public static final Parcelable.Creator<ConnectionError> CREATOR = new Parcelable.Creator()
  {
    public ConnectionError createFromParcel(Parcel paramAnonymousParcel)
    {
      return new ConnectionError(paramAnonymousParcel);
    }
    
    public ConnectionError[] newArray(int paramAnonymousInt)
    {
      return new ConnectionError[paramAnonymousInt];
    }
  };
  public static final int HEART_BEAT_TIMED_OUT = 6;
  public static final int NONE = 0;
  public static final int NO_NETWORK = 1;
  public static final int SERVER_ERROR = 7;
  public static final int SERVER_REJECT_RATE_LIMITING = 8;
  public static final int SERVICE_DISABLED = 11;
  public static final int SESSION_TERMINATED = 9;
  public static final int UNKNOWN = 10;
  public static final int UNKNOWN_HOST = 3;
  public static final String UNKNOWN_HOST_ERROR_STR = "host-unknown";
  private int mError;
  
  public ConnectionError(int paramInt)
  {
    setError(paramInt);
  }
  
  public ConnectionError(Parcel paramParcel)
  {
    this.mError = paramParcel.readInt();
  }
  
  public static boolean isAuthenticationError(int paramInt)
  {
    return paramInt == 4;
  }
  
  public static boolean isNetworkError(int paramInt)
  {
    return (paramInt == 1) || (paramInt == 2) || (paramInt == 3) || (paramInt == 10);
  }
  
  public static final String toString(int paramInt)
  {
    switch (paramInt)
    {
    case 9: 
    default: 
      return "NO ERROR";
    case 1: 
      return "NO NETWORK";
    case 2: 
      return "CONNECTION FAILED";
    case 3: 
      return "UNKNOWN HOST";
    case 4: 
      return "AUTH FAILED";
    case 5: 
      return "AUTH EXPIRED";
    case 6: 
      return "HEARTBEAT TIMEOUT";
    case 7: 
      return "SERVER FAILED";
    case 8: 
      return "SERVER REJECT - RATE LIMIT";
    }
    return "UNKNOWN";
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public int getError()
  {
    return this.mError;
  }
  
  public boolean isAuthenticationError()
  {
    return this.mError == 4;
  }
  
  public boolean isAuthenticationExpired()
  {
    return this.mError == 5;
  }
  
  public boolean isNetworkError()
  {
    return isNetworkError(this.mError);
  }
  
  public boolean isNoError()
  {
    return this.mError == 0;
  }
  
  public void setError(int paramInt)
  {
    this.mError = paramInt;
  }
  
  public final String toString()
  {
    return toString(this.mError);
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.mError);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gtalkservice/ConnectionError.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */