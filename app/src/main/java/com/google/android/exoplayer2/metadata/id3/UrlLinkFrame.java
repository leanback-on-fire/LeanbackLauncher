package com.google.android.exoplayer2.metadata.id3;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.exoplayer2.util.Util;

public final class UrlLinkFrame
  extends Id3Frame
{
  public static final Parcelable.Creator<UrlLinkFrame> CREATOR = new Parcelable.Creator()
  {
    public UrlLinkFrame createFromParcel(Parcel paramAnonymousParcel)
    {
      return new UrlLinkFrame(paramAnonymousParcel);
    }
    
    public UrlLinkFrame[] newArray(int paramAnonymousInt)
    {
      return new UrlLinkFrame[paramAnonymousInt];
    }
  };
  public final String description;
  public final String url;
  
  UrlLinkFrame(Parcel paramParcel)
  {
    super(paramParcel.readString());
    this.description = paramParcel.readString();
    this.url = paramParcel.readString();
  }
  
  public UrlLinkFrame(String paramString1, String paramString2, String paramString3)
  {
    super(paramString1);
    this.description = paramString2;
    this.url = paramString3;
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
      paramObject = (UrlLinkFrame)paramObject;
    } while ((this.id.equals(((UrlLinkFrame)paramObject).id)) && (Util.areEqual(this.description, ((UrlLinkFrame)paramObject).description)) && (Util.areEqual(this.url, ((UrlLinkFrame)paramObject).url)));
    return false;
  }
  
  public int hashCode()
  {
    int j = 0;
    int k = this.id.hashCode();
    if (this.description != null) {}
    for (int i = this.description.hashCode();; i = 0)
    {
      if (this.url != null) {
        j = this.url.hashCode();
      }
      return ((k + 527) * 31 + i) * 31 + j;
    }
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.id);
    paramParcel.writeString(this.description);
    paramParcel.writeString(this.url);
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/metadata/id3/UrlLinkFrame.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */