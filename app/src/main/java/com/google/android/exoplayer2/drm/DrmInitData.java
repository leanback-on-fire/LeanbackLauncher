package com.google.android.exoplayer2.drm;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public final class DrmInitData
  implements Comparator<SchemeData>, Parcelable
{
  public static final Parcelable.Creator<DrmInitData> CREATOR = new Parcelable.Creator()
  {
    public DrmInitData createFromParcel(Parcel paramAnonymousParcel)
    {
      return new DrmInitData(paramAnonymousParcel);
    }
    
    public DrmInitData[] newArray(int paramAnonymousInt)
    {
      return new DrmInitData[paramAnonymousInt];
    }
  };
  private int hashCode;
  public final int schemeDataCount;
  private final SchemeData[] schemeDatas;
  
  DrmInitData(Parcel paramParcel)
  {
    this.schemeDatas = ((SchemeData[])paramParcel.createTypedArray(SchemeData.CREATOR));
    this.schemeDataCount = this.schemeDatas.length;
  }
  
  public DrmInitData(List<SchemeData> paramList)
  {
    this(false, (SchemeData[])paramList.toArray(new SchemeData[paramList.size()]));
  }
  
  private DrmInitData(boolean paramBoolean, SchemeData... paramVarArgs)
  {
    SchemeData[] arrayOfSchemeData = paramVarArgs;
    if (paramBoolean) {
      arrayOfSchemeData = (SchemeData[])paramVarArgs.clone();
    }
    Arrays.sort(arrayOfSchemeData, this);
    int i = 1;
    while (i < arrayOfSchemeData.length)
    {
      if (arrayOfSchemeData[(i - 1)].uuid.equals(arrayOfSchemeData[i].uuid)) {
        throw new IllegalArgumentException("Duplicate data for uuid: " + arrayOfSchemeData[i].uuid);
      }
      i += 1;
    }
    this.schemeDatas = arrayOfSchemeData;
    this.schemeDataCount = arrayOfSchemeData.length;
  }
  
  public DrmInitData(SchemeData... paramVarArgs)
  {
    this(true, paramVarArgs);
  }
  
  public int compare(SchemeData paramSchemeData1, SchemeData paramSchemeData2)
  {
    if (C.UUID_NIL.equals(paramSchemeData1.uuid))
    {
      if (C.UUID_NIL.equals(paramSchemeData2.uuid)) {
        return 0;
      }
      return 1;
    }
    return paramSchemeData1.uuid.compareTo(paramSchemeData2.uuid);
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if ((paramObject == null) || (getClass() != paramObject.getClass())) {
      return false;
    }
    return Arrays.equals(this.schemeDatas, ((DrmInitData)paramObject).schemeDatas);
  }
  
  public SchemeData get(int paramInt)
  {
    return this.schemeDatas[paramInt];
  }
  
  public SchemeData get(UUID paramUUID)
  {
    SchemeData[] arrayOfSchemeData = this.schemeDatas;
    int j = arrayOfSchemeData.length;
    int i = 0;
    while (i < j)
    {
      SchemeData localSchemeData = arrayOfSchemeData[i];
      if (localSchemeData.matches(paramUUID)) {
        return localSchemeData;
      }
      i += 1;
    }
    return null;
  }
  
  public int hashCode()
  {
    if (this.hashCode == 0) {
      this.hashCode = Arrays.hashCode(this.schemeDatas);
    }
    return this.hashCode;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeTypedArray(this.schemeDatas, 0);
  }
  
  public static final class SchemeData
    implements Parcelable
  {
    public static final Parcelable.Creator<SchemeData> CREATOR = new Parcelable.Creator()
    {
      public DrmInitData.SchemeData createFromParcel(Parcel paramAnonymousParcel)
      {
        return new DrmInitData.SchemeData(paramAnonymousParcel);
      }
      
      public DrmInitData.SchemeData[] newArray(int paramAnonymousInt)
      {
        return new DrmInitData.SchemeData[paramAnonymousInt];
      }
    };
    public final byte[] data;
    private int hashCode;
    public final String mimeType;
    public final boolean requiresSecureDecryption;
    private final UUID uuid;
    
    SchemeData(Parcel paramParcel)
    {
      this.uuid = new UUID(paramParcel.readLong(), paramParcel.readLong());
      this.mimeType = paramParcel.readString();
      this.data = paramParcel.createByteArray();
      if (paramParcel.readByte() != 0) {}
      for (boolean bool = true;; bool = false)
      {
        this.requiresSecureDecryption = bool;
        return;
      }
    }
    
    public SchemeData(UUID paramUUID, String paramString, byte[] paramArrayOfByte)
    {
      this(paramUUID, paramString, paramArrayOfByte, false);
    }
    
    public SchemeData(UUID paramUUID, String paramString, byte[] paramArrayOfByte, boolean paramBoolean)
    {
      this.uuid = ((UUID)Assertions.checkNotNull(paramUUID));
      this.mimeType = ((String)Assertions.checkNotNull(paramString));
      this.data = ((byte[])Assertions.checkNotNull(paramArrayOfByte));
      this.requiresSecureDecryption = paramBoolean;
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public boolean equals(Object paramObject)
    {
      boolean bool2 = true;
      boolean bool1;
      if (!(paramObject instanceof SchemeData)) {
        bool1 = false;
      }
      do
      {
        do
        {
          return bool1;
          bool1 = bool2;
        } while (paramObject == this);
        paramObject = (SchemeData)paramObject;
        if ((!this.mimeType.equals(((SchemeData)paramObject).mimeType)) || (!Util.areEqual(this.uuid, ((SchemeData)paramObject).uuid))) {
          break;
        }
        bool1 = bool2;
      } while (Arrays.equals(this.data, ((SchemeData)paramObject).data));
      return false;
    }
    
    public int hashCode()
    {
      if (this.hashCode == 0) {
        this.hashCode = ((this.uuid.hashCode() * 31 + this.mimeType.hashCode()) * 31 + Arrays.hashCode(this.data));
      }
      return this.hashCode;
    }
    
    public boolean matches(UUID paramUUID)
    {
      return (C.UUID_NIL.equals(this.uuid)) || (paramUUID.equals(this.uuid));
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramParcel.writeLong(this.uuid.getMostSignificantBits());
      paramParcel.writeLong(this.uuid.getLeastSignificantBits());
      paramParcel.writeString(this.mimeType);
      paramParcel.writeByteArray(this.data);
      if (this.requiresSecureDecryption) {}
      for (paramInt = 1;; paramInt = 0)
      {
        paramParcel.writeByte((byte)paramInt);
        return;
      }
    }
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/drm/DrmInitData.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */