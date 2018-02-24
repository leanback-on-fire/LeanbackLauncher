package com.google.android.exoplayer2.source.dash.manifest;

import java.util.Collections;
import java.util.List;

public class Period
{
  public final List<AdaptationSet> adaptationSets;
  public final String id;
  public final long startMs;
  
  public Period(String paramString, long paramLong, List<AdaptationSet> paramList)
  {
    this.id = paramString;
    this.startMs = paramLong;
    this.adaptationSets = Collections.unmodifiableList(paramList);
  }
  
  public int getAdaptationSetIndex(int paramInt)
  {
    int j = this.adaptationSets.size();
    int i = 0;
    while (i < j)
    {
      if (((AdaptationSet)this.adaptationSets.get(i)).type == paramInt) {
        return i;
      }
      i += 1;
    }
    return -1;
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/source/dash/manifest/Period.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */