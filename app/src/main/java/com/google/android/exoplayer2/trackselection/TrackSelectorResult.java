package com.google.android.exoplayer2.trackselection;

import com.google.android.exoplayer2.RendererConfiguration;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.util.Util;

public final class TrackSelectorResult
{
  public final TrackGroupArray groups;
  public final Object info;
  public final RendererConfiguration[] rendererConfigurations;
  public final TrackSelectionArray selections;
  
  public TrackSelectorResult(TrackGroupArray paramTrackGroupArray, TrackSelectionArray paramTrackSelectionArray, Object paramObject, RendererConfiguration[] paramArrayOfRendererConfiguration)
  {
    this.groups = paramTrackGroupArray;
    this.selections = paramTrackSelectionArray;
    this.info = paramObject;
    this.rendererConfigurations = paramArrayOfRendererConfiguration;
  }
  
  public boolean isEquivalent(TrackSelectorResult paramTrackSelectorResult)
  {
    if (paramTrackSelectorResult == null) {
      return false;
    }
    int i = 0;
    for (;;)
    {
      if (i >= this.selections.length) {
        break label35;
      }
      if (!isEquivalent(paramTrackSelectorResult, i)) {
        break;
      }
      i += 1;
    }
    label35:
    return true;
  }
  
  public boolean isEquivalent(TrackSelectorResult paramTrackSelectorResult, int paramInt)
  {
    if (paramTrackSelectorResult == null) {}
    while ((!Util.areEqual(this.selections.get(paramInt), paramTrackSelectorResult.selections.get(paramInt))) || (!Util.areEqual(this.rendererConfigurations[paramInt], paramTrackSelectorResult.rendererConfigurations[paramInt]))) {
      return false;
    }
    return true;
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/trackselection/TrackSelectorResult.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */