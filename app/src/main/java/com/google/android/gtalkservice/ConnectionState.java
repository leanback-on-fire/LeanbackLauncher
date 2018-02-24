package com.google.android.gtalkservice;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public final class ConnectionState
  implements Parcelable
{
  public static final int CONNECTING = 2;
  public static final Parcelable.Creator<ConnectionState> CREATOR = new Parcelable.Creator()
  {
    public ConnectionState createFromParcel(Parcel paramAnonymousParcel)
    {
      return new ConnectionState(paramAnonymousParcel);
    }
    
    public ConnectionState[] newArray(int paramAnonymousInt)
    {
      return new ConnectionState[paramAnonymousInt];
    }
  };
  public static final int IDLE = 0;
  public static final int LOGGED_IN = 3;
  public static final int ONLINE = 4;
  public static final int PENDING = 1;
  private volatile int mState;
  
  public ConnectionState(int paramInt)
  {
    setState(paramInt);
  }
  
  public ConnectionState(Parcel paramParcel)
  {
    this.mState = paramParcel.readInt();
  }
  
  public static final String toString(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return "IDLE";
    case 1: 
      return "RECONNECTION_SCHEDULED";
    case 2: 
      return "CONNECTING";
    case 3: 
      return "AUTHENTICATED";
    }
    return "ONLINE";
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public int getState()
  {
    return this.mState;
  }
  
  public boolean isDisconnected()
  {
    return (this.mState == 0) || (this.mState == 1);
  }
  
  public boolean isLoggedIn()
  {
    return this.mState >= 3;
  }
  
  public boolean isLoggingIn()
  {
    return this.mState == 2;
  }
  
  public boolean isOnline()
  {
    return this.mState == 4;
  }
  
  public boolean isPendingReconnect()
  {
    return this.mState == 1;
  }
  
  public void setState(int paramInt)
  {
    this.mState = paramInt;
  }
  
  public final String toString()
  {
    return toString(this.mState);
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.mState);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gtalkservice/ConnectionState.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */