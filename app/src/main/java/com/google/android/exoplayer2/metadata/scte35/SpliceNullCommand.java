package com.google.android.exoplayer2.metadata.scte35;

import android.os.Parcel;
import android.os.Parcelable.Creator;

public final class SpliceNullCommand
  extends SpliceCommand
{
  public static final Parcelable.Creator<SpliceNullCommand> CREATOR = new Parcelable.Creator()
  {
    public SpliceNullCommand createFromParcel(Parcel paramAnonymousParcel)
    {
      return new SpliceNullCommand();
    }
    
    public SpliceNullCommand[] newArray(int paramAnonymousInt)
    {
      return new SpliceNullCommand[paramAnonymousInt];
    }
  };
  
  public void writeToParcel(Parcel paramParcel, int paramInt) {}
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/metadata/scte35/SpliceNullCommand.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */