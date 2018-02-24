package com.google.android.exoplayer2.source.dash.manifest;

import java.util.Collections;
import java.util.List;

public class AdaptationSet
{
  public static final int ID_UNSET = -1;
  public final List<SchemeValuePair> accessibilityDescriptors;
  public final int id;
  public final List<Representation> representations;
  public final int type;
  
  public AdaptationSet(int paramInt1, int paramInt2, List<Representation> paramList, List<SchemeValuePair> paramList1)
  {
    this.id = paramInt1;
    this.type = paramInt2;
    this.representations = Collections.unmodifiableList(paramList);
    if (paramList1 == null) {}
    for (paramList = Collections.emptyList();; paramList = Collections.unmodifiableList(paramList1))
    {
      this.accessibilityDescriptors = paramList;
      return;
    }
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/source/dash/manifest/AdaptationSet.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */