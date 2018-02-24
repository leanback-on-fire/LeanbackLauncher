package com.google.android.exoplayer2.metadata.id3;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.exoplayer2.util.Util;
import java.util.Arrays;

public final class PrivFrame
  extends Id3Frame
{
  public static final Parcelable.Creator<PrivFrame> CREATOR = new Parcelable.Creator()
  {
    public PrivFrame createFromParcel(Parcel paramAnonymousParcel)
    {
      return new PrivFrame(paramAnonymousParcel);
    }
    
    public PrivFrame[] newArray(int paramAnonymousInt)
    {
      return new PrivFrame[paramAnonymousInt];
    }
  };
  public static final String ID = "PRIV";
  public final String owner;
  public final byte[] privateData;
  
  PrivFrame(Parcel paramParcel)
  {
    super("PRIV");
    this.owner = paramParcel.readString();
    this.privateData = paramParcel.createByteArray();
  }
  
  public PrivFrame(String paramString, byte[] paramArrayOfByte)
  {
    super("PRIV");
    this.owner = paramString;
    this.privateData = paramArrayOfByte;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {}
    do
    {
      return true;
      if ((paramObject == null) || (getClass() != paramObject.getClass())) {
        return false;
      }
      paramObject = (PrivFrame)paramObject;
    } while ((Util.areEqual(this.owner, ((PrivFrame)paramObject).owner)) && (Arrays.equals(this.privateData, ((PrivFrame)paramObject).privateData)));
    return false;
  }
  
  public int hashCode()
  {
    if (this.owner != null) {}
    for (int i = this.owner.hashCode();; i = 0) {
      return (i + 527) * 31 + Arrays.hashCode(this.privateData);
    }
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.owner);
    paramParcel.writeByteArray(this.privateData);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/metadata/id3/PrivFrame.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */