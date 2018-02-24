package com.google.android.exoplayer2.metadata.scte35;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.exoplayer2.util.ParsableByteArray;

public final class PrivateCommand
  extends SpliceCommand
{
  public static final Parcelable.Creator<PrivateCommand> CREATOR = new Parcelable.Creator()
  {
    public PrivateCommand createFromParcel(Parcel paramAnonymousParcel)
    {
      return new PrivateCommand(paramAnonymousParcel, null);
    }
    
    public PrivateCommand[] newArray(int paramAnonymousInt)
    {
      return new PrivateCommand[paramAnonymousInt];
    }
  };
  public final byte[] commandBytes;
  public final long identifier;
  public final long ptsAdjustment;
  
  private PrivateCommand(long paramLong1, byte[] paramArrayOfByte, long paramLong2)
  {
    this.ptsAdjustment = paramLong2;
    this.identifier = paramLong1;
    this.commandBytes = paramArrayOfByte;
  }
  
  private PrivateCommand(Parcel paramParcel)
  {
    this.ptsAdjustment = paramParcel.readLong();
    this.identifier = paramParcel.readLong();
    this.commandBytes = new byte[paramParcel.readInt()];
    paramParcel.readByteArray(this.commandBytes);
  }
  
  static PrivateCommand parseFromSection(ParsableByteArray paramParsableByteArray, int paramInt, long paramLong)
  {
    long l = paramParsableByteArray.readUnsignedInt();
    byte[] arrayOfByte = new byte[paramInt - 4];
    paramParsableByteArray.readBytes(arrayOfByte, 0, arrayOfByte.length);
    return new PrivateCommand(l, arrayOfByte, paramLong);
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeLong(this.ptsAdjustment);
    paramParcel.writeLong(this.identifier);
    paramParcel.writeInt(this.commandBytes.length);
    paramParcel.writeByteArray(this.commandBytes);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/metadata/scte35/PrivateCommand.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */