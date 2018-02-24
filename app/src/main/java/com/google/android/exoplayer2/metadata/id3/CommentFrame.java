package com.google.android.exoplayer2.metadata.id3;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.exoplayer2.util.Util;

public final class CommentFrame
  extends Id3Frame
{
  public static final Parcelable.Creator<CommentFrame> CREATOR = new Parcelable.Creator()
  {
    public CommentFrame createFromParcel(Parcel paramAnonymousParcel)
    {
      return new CommentFrame(paramAnonymousParcel);
    }
    
    public CommentFrame[] newArray(int paramAnonymousInt)
    {
      return new CommentFrame[paramAnonymousInt];
    }
  };
  public static final String ID = "COMM";
  public final String description;
  public final String language;
  public final String text;
  
  CommentFrame(Parcel paramParcel)
  {
    super("COMM");
    this.language = paramParcel.readString();
    this.description = paramParcel.readString();
    this.text = paramParcel.readString();
  }
  
  public CommentFrame(String paramString1, String paramString2, String paramString3)
  {
    super("COMM");
    this.language = paramString1;
    this.description = paramString2;
    this.text = paramString3;
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
      paramObject = (CommentFrame)paramObject;
    } while ((Util.areEqual(this.description, ((CommentFrame)paramObject).description)) && (Util.areEqual(this.language, ((CommentFrame)paramObject).language)) && (Util.areEqual(this.text, ((CommentFrame)paramObject).text)));
    return false;
  }
  
  public int hashCode()
  {
    int k = 0;
    int i;
    if (this.language != null)
    {
      i = this.language.hashCode();
      if (this.description == null) {
        break label68;
      }
    }
    label68:
    for (int j = this.description.hashCode();; j = 0)
    {
      if (this.text != null) {
        k = this.text.hashCode();
      }
      return ((i + 527) * 31 + j) * 31 + k;
      i = 0;
      break;
    }
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.id);
    paramParcel.writeString(this.language);
    paramParcel.writeString(this.text);
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/metadata/id3/CommentFrame.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */