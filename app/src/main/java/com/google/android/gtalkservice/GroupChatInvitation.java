package com.google.android.gtalkservice;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class GroupChatInvitation
  implements Parcelable
{
  public static final Parcelable.Creator<GroupChatInvitation> CREATOR = new Parcelable.Creator()
  {
    public GroupChatInvitation createFromParcel(Parcel paramAnonymousParcel)
    {
      return new GroupChatInvitation(paramAnonymousParcel);
    }
    
    public GroupChatInvitation[] newArray(int paramAnonymousInt)
    {
      return new GroupChatInvitation[paramAnonymousInt];
    }
  };
  private long mGroupContactId;
  private String mInviter;
  private String mPassword;
  private String mReason;
  private String mRoomAddress;
  
  public GroupChatInvitation(Parcel paramParcel)
  {
    this.mRoomAddress = paramParcel.readString();
    this.mInviter = paramParcel.readString();
    this.mReason = paramParcel.readString();
    this.mPassword = paramParcel.readString();
    this.mGroupContactId = paramParcel.readLong();
  }
  
  public GroupChatInvitation(String paramString1, String paramString2, String paramString3, String paramString4, long paramLong)
  {
    this.mRoomAddress = paramString1;
    this.mInviter = paramString2;
    this.mReason = paramString3;
    this.mPassword = paramString4;
    this.mGroupContactId = paramLong;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public long getGroupContactId()
  {
    return this.mGroupContactId;
  }
  
  public String getInviter()
  {
    return this.mInviter;
  }
  
  public String getPassword()
  {
    return this.mPassword;
  }
  
  public String getReason()
  {
    return this.mReason;
  }
  
  public String getRoomAddress()
  {
    return this.mRoomAddress;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.mRoomAddress);
    paramParcel.writeString(this.mInviter);
    paramParcel.writeString(this.mReason);
    paramParcel.writeString(this.mPassword);
    paramParcel.writeLong(this.mGroupContactId);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gtalkservice/GroupChatInvitation.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */