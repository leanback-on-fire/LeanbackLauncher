package com.google.android.exoplayer2.source.dash.manifest;

import com.google.android.exoplayer2.util.Util;

public class SchemeValuePair
{
  public final String schemeIdUri;
  public final String value;
  
  public SchemeValuePair(String paramString1, String paramString2)
  {
    this.schemeIdUri = paramString1;
    this.value = paramString2;
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
      paramObject = (SchemeValuePair)paramObject;
    } while ((Util.areEqual(this.schemeIdUri, ((SchemeValuePair)paramObject).schemeIdUri)) && (Util.areEqual(this.value, ((SchemeValuePair)paramObject).value)));
    return false;
  }
  
  public int hashCode()
  {
    int j = 0;
    if (this.schemeIdUri != null) {}
    for (int i = this.schemeIdUri.hashCode();; i = 0)
    {
      if (this.value != null) {
        j = this.value.hashCode();
      }
      return i * 31 + j;
    }
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/source/dash/manifest/SchemeValuePair.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */