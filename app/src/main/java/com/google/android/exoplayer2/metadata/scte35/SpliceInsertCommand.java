package com.google.android.exoplayer2.metadata.scte35;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.TimestampAdjuster;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class SpliceInsertCommand
  extends SpliceCommand
{
  public static final Parcelable.Creator<SpliceInsertCommand> CREATOR = new Parcelable.Creator()
  {
    public SpliceInsertCommand createFromParcel(Parcel paramAnonymousParcel)
    {
      return new SpliceInsertCommand(paramAnonymousParcel, null);
    }
    
    public SpliceInsertCommand[] newArray(int paramAnonymousInt)
    {
      return new SpliceInsertCommand[paramAnonymousInt];
    }
  };
  public final boolean autoReturn;
  public final int availNum;
  public final int availsExpected;
  public final long breakDuration;
  public final List<ComponentSplice> componentSpliceList;
  public final boolean outOfNetworkIndicator;
  public final boolean programSpliceFlag;
  public final long programSplicePlaybackPositionUs;
  public final long programSplicePts;
  public final boolean spliceEventCancelIndicator;
  public final long spliceEventId;
  public final boolean spliceImmediateFlag;
  public final int uniqueProgramId;
  
  private SpliceInsertCommand(long paramLong1, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4, long paramLong2, long paramLong3, List<ComponentSplice> paramList, boolean paramBoolean5, long paramLong4, int paramInt1, int paramInt2, int paramInt3)
  {
    this.spliceEventId = paramLong1;
    this.spliceEventCancelIndicator = paramBoolean1;
    this.outOfNetworkIndicator = paramBoolean2;
    this.programSpliceFlag = paramBoolean3;
    this.spliceImmediateFlag = paramBoolean4;
    this.programSplicePts = paramLong2;
    this.programSplicePlaybackPositionUs = paramLong3;
    this.componentSpliceList = Collections.unmodifiableList(paramList);
    this.autoReturn = paramBoolean5;
    this.breakDuration = paramLong4;
    this.uniqueProgramId = paramInt1;
    this.availNum = paramInt2;
    this.availsExpected = paramInt3;
  }
  
  private SpliceInsertCommand(Parcel paramParcel)
  {
    this.spliceEventId = paramParcel.readLong();
    if (paramParcel.readByte() == 1)
    {
      bool1 = true;
      this.spliceEventCancelIndicator = bool1;
      if (paramParcel.readByte() != 1) {
        break label146;
      }
      bool1 = true;
      label43:
      this.outOfNetworkIndicator = bool1;
      if (paramParcel.readByte() != 1) {
        break label152;
      }
      bool1 = true;
      label60:
      this.programSpliceFlag = bool1;
      if (paramParcel.readByte() != 1) {
        break label158;
      }
    }
    ArrayList localArrayList;
    label146:
    label152:
    label158:
    for (boolean bool1 = true;; bool1 = false)
    {
      this.spliceImmediateFlag = bool1;
      this.programSplicePts = paramParcel.readLong();
      this.programSplicePlaybackPositionUs = paramParcel.readLong();
      int j = paramParcel.readInt();
      localArrayList = new ArrayList(j);
      int i = 0;
      while (i < j)
      {
        localArrayList.add(ComponentSplice.createFromParcel(paramParcel));
        i += 1;
      }
      bool1 = false;
      break;
      bool1 = false;
      break label43;
      bool1 = false;
      break label60;
    }
    this.componentSpliceList = Collections.unmodifiableList(localArrayList);
    if (paramParcel.readByte() == 1) {}
    for (bool1 = bool2;; bool1 = false)
    {
      this.autoReturn = bool1;
      this.breakDuration = paramParcel.readLong();
      this.uniqueProgramId = paramParcel.readInt();
      this.availNum = paramParcel.readInt();
      this.availsExpected = paramParcel.readInt();
      return;
    }
  }
  
  static SpliceInsertCommand parseFromSection(ParsableByteArray paramParsableByteArray, long paramLong, TimestampAdjuster paramTimestampAdjuster)
  {
    long l5 = paramParsableByteArray.readUnsignedInt();
    boolean bool5;
    boolean bool1;
    boolean bool2;
    long l3;
    Object localObject1;
    int i;
    int j;
    int k;
    boolean bool6;
    long l2;
    long l1;
    Object localObject2;
    long l4;
    if ((paramParsableByteArray.readUnsignedByte() & 0x80) != 0)
    {
      bool5 = true;
      bool1 = false;
      bool2 = false;
      bool3 = false;
      l3 = -9223372036854775807L;
      localObject1 = Collections.emptyList();
      i = 0;
      j = 0;
      k = 0;
      bool4 = false;
      bool6 = false;
      l2 = -9223372036854775807L;
      l1 = l3;
      localObject2 = localObject1;
      l4 = l2;
      if (bool5) {
        break label344;
      }
      j = paramParsableByteArray.readUnsignedByte();
      if ((j & 0x80) == 0) {
        break label252;
      }
      bool1 = true;
      label94:
      if ((j & 0x40) == 0) {
        break label258;
      }
      bool2 = true;
      label105:
      if ((j & 0x20) == 0) {
        break label264;
      }
      i = 1;
      label116:
      if ((j & 0x10) == 0) {
        break label270;
      }
    }
    label252:
    label258:
    label264:
    label270:
    for (boolean bool3 = true;; bool3 = false)
    {
      l1 = l3;
      if (bool2)
      {
        l1 = l3;
        if (!bool3) {
          l1 = TimeSignalCommand.parseSpliceTime(paramParsableByteArray, paramLong);
        }
      }
      if (bool2) {
        break label276;
      }
      k = paramParsableByteArray.readUnsignedByte();
      localObject2 = new ArrayList(k);
      j = 0;
      for (;;)
      {
        localObject1 = localObject2;
        if (j >= k) {
          break;
        }
        int m = paramParsableByteArray.readUnsignedByte();
        l3 = -9223372036854775807L;
        if (!bool3) {
          l3 = TimeSignalCommand.parseSpliceTime(paramParsableByteArray, paramLong);
        }
        ((List)localObject2).add(new ComponentSplice(m, l3, paramTimestampAdjuster.adjustTsTimestamp(l3), null));
        j += 1;
      }
      bool5 = false;
      break;
      bool1 = false;
      break label94;
      bool2 = false;
      break label105;
      i = 0;
      break label116;
    }
    label276:
    boolean bool4 = bool6;
    paramLong = l2;
    if (i != 0)
    {
      paramLong = paramParsableByteArray.readUnsignedByte();
      if ((0x80 & paramLong) == 0L) {
        break label382;
      }
    }
    label344:
    label382:
    for (bool4 = true;; bool4 = false)
    {
      paramLong = (1L & paramLong) << 32 | paramParsableByteArray.readUnsignedInt();
      i = paramParsableByteArray.readUnsignedShort();
      j = paramParsableByteArray.readUnsignedByte();
      k = paramParsableByteArray.readUnsignedByte();
      l4 = paramLong;
      localObject2 = localObject1;
      return new SpliceInsertCommand(l5, bool5, bool1, bool2, bool3, l1, paramTimestampAdjuster.adjustTsTimestamp(l1), (List)localObject2, bool4, l4, i, j, k);
    }
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = 1;
    paramParcel.writeLong(this.spliceEventId);
    if (this.spliceEventCancelIndicator)
    {
      paramInt = 1;
      paramParcel.writeByte((byte)paramInt);
      if (!this.outOfNetworkIndicator) {
        break label140;
      }
      paramInt = 1;
      label34:
      paramParcel.writeByte((byte)paramInt);
      if (!this.programSpliceFlag) {
        break label145;
      }
      paramInt = 1;
      label49:
      paramParcel.writeByte((byte)paramInt);
      if (!this.spliceImmediateFlag) {
        break label150;
      }
    }
    label140:
    label145:
    label150:
    for (paramInt = 1;; paramInt = 0)
    {
      paramParcel.writeByte((byte)paramInt);
      paramParcel.writeLong(this.programSplicePts);
      paramParcel.writeLong(this.programSplicePlaybackPositionUs);
      int j = this.componentSpliceList.size();
      paramParcel.writeInt(j);
      paramInt = 0;
      while (paramInt < j)
      {
        ((ComponentSplice)this.componentSpliceList.get(paramInt)).writeToParcel(paramParcel);
        paramInt += 1;
      }
      paramInt = 0;
      break;
      paramInt = 0;
      break label34;
      paramInt = 0;
      break label49;
    }
    if (this.autoReturn) {}
    for (paramInt = i;; paramInt = 0)
    {
      paramParcel.writeByte((byte)paramInt);
      paramParcel.writeLong(this.breakDuration);
      paramParcel.writeInt(this.uniqueProgramId);
      paramParcel.writeInt(this.availNum);
      paramParcel.writeInt(this.availsExpected);
      return;
    }
  }
  
  public static final class ComponentSplice
  {
    public final long componentSplicePlaybackPositionUs;
    public final long componentSplicePts;
    public final int componentTag;
    
    private ComponentSplice(int paramInt, long paramLong1, long paramLong2)
    {
      this.componentTag = paramInt;
      this.componentSplicePts = paramLong1;
      this.componentSplicePlaybackPositionUs = paramLong2;
    }
    
    public static ComponentSplice createFromParcel(Parcel paramParcel)
    {
      return new ComponentSplice(paramParcel.readInt(), paramParcel.readLong(), paramParcel.readLong());
    }
    
    public void writeToParcel(Parcel paramParcel)
    {
      paramParcel.writeInt(this.componentTag);
      paramParcel.writeLong(this.componentSplicePts);
      paramParcel.writeLong(this.componentSplicePlaybackPositionUs);
    }
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/metadata/scte35/SpliceInsertCommand.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */