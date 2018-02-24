package com.google.android.exoplayer2.trackselection;

import android.util.SparseArray;
import android.util.SparseBooleanArray;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.RendererCapabilities;
import com.google.android.exoplayer2.RendererConfiguration;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.util.Util;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public abstract class MappingTrackSelector
  extends TrackSelector
{
  private MappedTrackInfo currentMappedTrackInfo;
  private final SparseBooleanArray rendererDisabledFlags = new SparseBooleanArray();
  private final SparseArray<Map<TrackGroupArray, SelectionOverride>> selectionOverrides = new SparseArray();
  private int tunnelingAudioSessionId = 0;
  
  private static int findRenderer(RendererCapabilities[] paramArrayOfRendererCapabilities, TrackGroup paramTrackGroup)
    throws ExoPlaybackException
  {
    int n = paramArrayOfRendererCapabilities.length;
    int m = 0;
    int i = 0;
    while (i < paramArrayOfRendererCapabilities.length)
    {
      RendererCapabilities localRendererCapabilities = paramArrayOfRendererCapabilities[i];
      int j = 0;
      while (j < paramTrackGroup.length)
      {
        int i1 = localRendererCapabilities.supportsFormat(paramTrackGroup.getFormat(j)) & 0x3;
        int k = m;
        if (i1 > m)
        {
          m = i;
          k = i1;
          n = m;
          if (i1 == 3) {
            return m;
          }
        }
        j += 1;
        m = k;
      }
      i += 1;
    }
    return n;
  }
  
  private static int[] getFormatSupport(RendererCapabilities paramRendererCapabilities, TrackGroup paramTrackGroup)
    throws ExoPlaybackException
  {
    int[] arrayOfInt = new int[paramTrackGroup.length];
    int i = 0;
    while (i < paramTrackGroup.length)
    {
      arrayOfInt[i] = paramRendererCapabilities.supportsFormat(paramTrackGroup.getFormat(i));
      i += 1;
    }
    return arrayOfInt;
  }
  
  private static int[] getMixedMimeTypeAdaptationSupport(RendererCapabilities[] paramArrayOfRendererCapabilities)
    throws ExoPlaybackException
  {
    int[] arrayOfInt = new int[paramArrayOfRendererCapabilities.length];
    int i = 0;
    while (i < arrayOfInt.length)
    {
      arrayOfInt[i] = paramArrayOfRendererCapabilities[i].supportsMixedMimeTypeAdaptation();
      i += 1;
    }
    return arrayOfInt;
  }
  
  private static void maybeConfigureRenderersForTunneling(RendererCapabilities[] paramArrayOfRendererCapabilities, TrackGroupArray[] paramArrayOfTrackGroupArray, int[][][] paramArrayOfInt, RendererConfiguration[] paramArrayOfRendererConfiguration, TrackSelection[] paramArrayOfTrackSelection, int paramInt)
  {
    if (paramInt == 0) {}
    label119:
    label171:
    label188:
    label211:
    label215:
    for (;;)
    {
      return;
      int m = -1;
      int k = -1;
      int i1 = 1;
      int i = 0;
      int j = i1;
      int n;
      if (i < paramArrayOfRendererCapabilities.length)
      {
        int i2 = paramArrayOfRendererCapabilities[i].getTrackType();
        TrackSelection localTrackSelection = paramArrayOfTrackSelection[i];
        if (i2 != 1)
        {
          j = m;
          n = k;
          if (i2 != 2) {
            break label171;
          }
        }
        j = m;
        n = k;
        if (localTrackSelection == null) {
          break label171;
        }
        j = m;
        n = k;
        if (!rendererSupportsTunneling(paramArrayOfInt[i], paramArrayOfTrackGroupArray[i], localTrackSelection)) {
          break label171;
        }
        if (i2 != 1) {
          break label188;
        }
        if (m != -1) {
          j = 0;
        }
      }
      else
      {
        if ((m == -1) || (k == -1)) {
          break label211;
        }
      }
      for (i = 1;; i = 0)
      {
        if ((j & i) == 0) {
          break label215;
        }
        paramArrayOfRendererCapabilities = new RendererConfiguration(paramInt);
        paramArrayOfRendererConfiguration[m] = paramArrayOfRendererCapabilities;
        paramArrayOfRendererConfiguration[k] = paramArrayOfRendererCapabilities;
        return;
        j = i;
        n = k;
        for (;;)
        {
          i += 1;
          m = j;
          k = n;
          break;
          if (k != -1)
          {
            j = 0;
            break label119;
          }
          n = i;
          j = m;
        }
      }
    }
  }
  
  private static boolean rendererSupportsTunneling(int[][] paramArrayOfInt, TrackGroupArray paramTrackGroupArray, TrackSelection paramTrackSelection)
  {
    if (paramTrackSelection == null) {
      return false;
    }
    int j = paramTrackGroupArray.indexOf(paramTrackSelection.getTrackGroup());
    int i = 0;
    for (;;)
    {
      if (i >= paramTrackSelection.length()) {
        break label57;
      }
      if ((paramArrayOfInt[j][paramTrackSelection.getIndexInTrackGroup(i)] & 0x10) != 16) {
        break;
      }
      i += 1;
    }
    label57:
    return true;
  }
  
  public final void clearSelectionOverride(int paramInt, TrackGroupArray paramTrackGroupArray)
  {
    Map localMap = (Map)this.selectionOverrides.get(paramInt);
    if ((localMap == null) || (!localMap.containsKey(paramTrackGroupArray))) {
      return;
    }
    localMap.remove(paramTrackGroupArray);
    if (localMap.isEmpty()) {
      this.selectionOverrides.remove(paramInt);
    }
    invalidate();
  }
  
  public final void clearSelectionOverrides()
  {
    if (this.selectionOverrides.size() == 0) {
      return;
    }
    this.selectionOverrides.clear();
    invalidate();
  }
  
  public final void clearSelectionOverrides(int paramInt)
  {
    Map localMap = (Map)this.selectionOverrides.get(paramInt);
    if ((localMap == null) || (localMap.isEmpty())) {
      return;
    }
    this.selectionOverrides.remove(paramInt);
    invalidate();
  }
  
  public final MappedTrackInfo getCurrentMappedTrackInfo()
  {
    return this.currentMappedTrackInfo;
  }
  
  public final boolean getRendererDisabled(int paramInt)
  {
    return this.rendererDisabledFlags.get(paramInt);
  }
  
  public final SelectionOverride getSelectionOverride(int paramInt, TrackGroupArray paramTrackGroupArray)
  {
    Map localMap = (Map)this.selectionOverrides.get(paramInt);
    if (localMap != null) {
      return (SelectionOverride)localMap.get(paramTrackGroupArray);
    }
    return null;
  }
  
  public final boolean hasSelectionOverride(int paramInt, TrackGroupArray paramTrackGroupArray)
  {
    Map localMap = (Map)this.selectionOverrides.get(paramInt);
    return (localMap != null) && (localMap.containsKey(paramTrackGroupArray));
  }
  
  public final void onSelectionActivated(Object paramObject)
  {
    this.currentMappedTrackInfo = ((MappedTrackInfo)paramObject);
  }
  
  public final TrackSelectorResult selectTracks(RendererCapabilities[] paramArrayOfRendererCapabilities, TrackGroupArray paramTrackGroupArray)
    throws ExoPlaybackException
  {
    Object localObject4 = new int[paramArrayOfRendererCapabilities.length + 1];
    Object localObject6 = new TrackGroup[paramArrayOfRendererCapabilities.length + 1][];
    int[][][] arrayOfInt = new int[paramArrayOfRendererCapabilities.length + 1][][];
    int i = 0;
    while (i < localObject6.length)
    {
      localObject6[i] = new TrackGroup[paramTrackGroupArray.length];
      arrayOfInt[i] = new int[paramTrackGroupArray.length][];
      i += 1;
    }
    Object localObject3 = getMixedMimeTypeAdaptationSupport(paramArrayOfRendererCapabilities);
    i = 0;
    int j;
    Object localObject1;
    if (i < paramTrackGroupArray.length)
    {
      localObject2 = paramTrackGroupArray.get(i);
      j = findRenderer(paramArrayOfRendererCapabilities, (TrackGroup)localObject2);
      if (j == paramArrayOfRendererCapabilities.length) {}
      for (localObject1 = new int[((TrackGroup)localObject2).length];; localObject1 = getFormatSupport(paramArrayOfRendererCapabilities[j], (TrackGroup)localObject2))
      {
        int k = localObject4[j];
        localObject6[j][k] = localObject2;
        arrayOfInt[j][k] = localObject1;
        localObject4[j] += 1;
        i += 1;
        break;
      }
    }
    Object localObject2 = new TrackGroupArray[paramArrayOfRendererCapabilities.length];
    Object localObject5 = new int[paramArrayOfRendererCapabilities.length];
    i = 0;
    while (i < paramArrayOfRendererCapabilities.length)
    {
      j = localObject4[i];
      localObject2[i] = new TrackGroupArray((TrackGroup[])Arrays.copyOf(localObject6[i], j));
      arrayOfInt[i] = ((int[][])Arrays.copyOf(arrayOfInt[i], j));
      localObject5[i] = paramArrayOfRendererCapabilities[i].getTrackType();
      i += 1;
    }
    i = localObject4[paramArrayOfRendererCapabilities.length];
    localObject6 = new TrackGroupArray((TrackGroup[])Arrays.copyOf(localObject6[paramArrayOfRendererCapabilities.length], i));
    localObject4 = selectTracks(paramArrayOfRendererCapabilities, (TrackGroupArray[])localObject2, arrayOfInt);
    i = 0;
    if (i < paramArrayOfRendererCapabilities.length)
    {
      if (this.rendererDisabledFlags.get(i)) {
        localObject4[i] = null;
      }
      label386:
      for (;;)
      {
        i += 1;
        break;
        TrackGroupArray localTrackGroupArray = localObject2[i];
        localObject1 = (Map)this.selectionOverrides.get(i);
        if (localObject1 == null) {}
        for (localObject1 = null;; localObject1 = (SelectionOverride)((Map)localObject1).get(localTrackGroupArray))
        {
          if (localObject1 == null) {
            break label386;
          }
          localObject4[i] = ((SelectionOverride)localObject1).createTrackSelection(localTrackGroupArray);
          break;
        }
      }
    }
    localObject3 = new MappedTrackInfo((int[])localObject5, (TrackGroupArray[])localObject2, (int[])localObject3, arrayOfInt, (TrackGroupArray)localObject6);
    localObject5 = new RendererConfiguration[paramArrayOfRendererCapabilities.length];
    i = 0;
    if (i < paramArrayOfRendererCapabilities.length)
    {
      if (localObject4[i] != null) {}
      for (localObject1 = RendererConfiguration.DEFAULT;; localObject1 = null)
      {
        localObject5[i] = localObject1;
        i += 1;
        break;
      }
    }
    maybeConfigureRenderersForTunneling(paramArrayOfRendererCapabilities, (TrackGroupArray[])localObject2, arrayOfInt, (RendererConfiguration[])localObject5, (TrackSelection[])localObject4, this.tunnelingAudioSessionId);
    return new TrackSelectorResult(paramTrackGroupArray, new TrackSelectionArray((TrackSelection[])localObject4), localObject3, (RendererConfiguration[])localObject5);
  }
  
  protected abstract TrackSelection[] selectTracks(RendererCapabilities[] paramArrayOfRendererCapabilities, TrackGroupArray[] paramArrayOfTrackGroupArray, int[][][] paramArrayOfInt)
    throws ExoPlaybackException;
  
  public final void setRendererDisabled(int paramInt, boolean paramBoolean)
  {
    if (this.rendererDisabledFlags.get(paramInt) == paramBoolean) {
      return;
    }
    this.rendererDisabledFlags.put(paramInt, paramBoolean);
    invalidate();
  }
  
  public final void setSelectionOverride(int paramInt, TrackGroupArray paramTrackGroupArray, SelectionOverride paramSelectionOverride)
  {
    Map localMap = (Map)this.selectionOverrides.get(paramInt);
    Object localObject = localMap;
    if (localMap == null)
    {
      localObject = new HashMap();
      this.selectionOverrides.put(paramInt, localObject);
    }
    if ((((Map)localObject).containsKey(paramTrackGroupArray)) && (Util.areEqual(((Map)localObject).get(paramTrackGroupArray), paramSelectionOverride))) {
      return;
    }
    ((Map)localObject).put(paramTrackGroupArray, paramSelectionOverride);
    invalidate();
  }
  
  public void setTunnelingAudioSessionId(int paramInt)
  {
    if (this.tunnelingAudioSessionId != paramInt)
    {
      this.tunnelingAudioSessionId = paramInt;
      invalidate();
    }
  }
  
  public static final class MappedTrackInfo
  {
    public static final int RENDERER_SUPPORT_EXCEEDS_CAPABILITIES_TRACKS = 2;
    public static final int RENDERER_SUPPORT_NO_TRACKS = 0;
    public static final int RENDERER_SUPPORT_PLAYABLE_TRACKS = 3;
    public static final int RENDERER_SUPPORT_UNSUPPORTED_TRACKS = 1;
    private final int[][][] formatSupport;
    public final int length;
    private final int[] mixedMimeTypeAdaptiveSupport;
    private final int[] rendererTrackTypes;
    private final TrackGroupArray[] trackGroups;
    private final TrackGroupArray unassociatedTrackGroups;
    
    MappedTrackInfo(int[] paramArrayOfInt1, TrackGroupArray[] paramArrayOfTrackGroupArray, int[] paramArrayOfInt2, int[][][] paramArrayOfInt, TrackGroupArray paramTrackGroupArray)
    {
      this.rendererTrackTypes = paramArrayOfInt1;
      this.trackGroups = paramArrayOfTrackGroupArray;
      this.formatSupport = paramArrayOfInt;
      this.mixedMimeTypeAdaptiveSupport = paramArrayOfInt2;
      this.unassociatedTrackGroups = paramTrackGroupArray;
      this.length = paramArrayOfTrackGroupArray.length;
    }
    
    public int getAdaptiveSupport(int paramInt1, int paramInt2, boolean paramBoolean)
    {
      int m = this.trackGroups[paramInt1].get(paramInt2).length;
      int[] arrayOfInt = new int[m];
      int j = 0;
      int i = 0;
      if (j < m)
      {
        int k = getTrackFormatSupport(paramInt1, paramInt2, j);
        if ((k != 3) && ((!paramBoolean) || (k != 2))) {
          break label100;
        }
        k = i + 1;
        arrayOfInt[i] = j;
        i = k;
      }
      label100:
      for (;;)
      {
        j += 1;
        break;
        return getAdaptiveSupport(paramInt1, paramInt2, Arrays.copyOf(arrayOfInt, i));
      }
    }
    
    public int getAdaptiveSupport(int paramInt1, int paramInt2, int[] paramArrayOfInt)
    {
      int j = 0;
      int i = 8;
      int m = 0;
      Object localObject = null;
      int k = 0;
      while (k < paramArrayOfInt.length)
      {
        int n = paramArrayOfInt[k];
        String str = this.trackGroups[paramInt1].get(paramInt2).getFormat(n).sampleMimeType;
        if (j == 0)
        {
          localObject = str;
          i = Math.min(i, this.formatSupport[paramInt1][paramInt2][k] & 0xC);
          k += 1;
          j += 1;
        }
        else
        {
          if (!Util.areEqual(localObject, str)) {}
          for (n = 1;; n = 0)
          {
            m |= n;
            break;
          }
        }
      }
      paramInt2 = i;
      if (m != 0) {
        paramInt2 = Math.min(i, this.mixedMimeTypeAdaptiveSupport[paramInt1]);
      }
      return paramInt2;
    }
    
    public int getRendererSupport(int paramInt)
    {
      int i = 0;
      int[][] arrayOfInt = this.formatSupport[paramInt];
      paramInt = 0;
      while (paramInt < arrayOfInt.length)
      {
        int j = 0;
        if (j < arrayOfInt[paramInt].length)
        {
          switch (arrayOfInt[paramInt][j] & 0x3)
          {
          }
          for (int k = 1;; k = 2)
          {
            i = Math.max(i, k);
            j += 1;
            break;
            return 3;
          }
        }
        paramInt += 1;
      }
      return i;
    }
    
    public int getTrackFormatSupport(int paramInt1, int paramInt2, int paramInt3)
    {
      return this.formatSupport[paramInt1][paramInt2][paramInt3] & 0x3;
    }
    
    public TrackGroupArray getTrackGroups(int paramInt)
    {
      return this.trackGroups[paramInt];
    }
    
    public int getTrackTypeRendererSupport(int paramInt)
    {
      int j = 0;
      int i = 0;
      while (i < this.length)
      {
        int k = j;
        if (this.rendererTrackTypes[i] == paramInt) {
          k = Math.max(j, getRendererSupport(i));
        }
        i += 1;
        j = k;
      }
      return j;
    }
    
    public TrackGroupArray getUnassociatedTrackGroups()
    {
      return this.unassociatedTrackGroups;
    }
  }
  
  public static final class SelectionOverride
  {
    public final TrackSelection.Factory factory;
    public final int groupIndex;
    public final int length;
    public final int[] tracks;
    
    public SelectionOverride(TrackSelection.Factory paramFactory, int paramInt, int... paramVarArgs)
    {
      this.factory = paramFactory;
      this.groupIndex = paramInt;
      this.tracks = paramVarArgs;
      this.length = paramVarArgs.length;
    }
    
    public boolean containsTrack(int paramInt)
    {
      boolean bool2 = false;
      int[] arrayOfInt = this.tracks;
      int j = arrayOfInt.length;
      int i = 0;
      for (;;)
      {
        boolean bool1 = bool2;
        if (i < j)
        {
          if (arrayOfInt[i] == paramInt) {
            bool1 = true;
          }
        }
        else {
          return bool1;
        }
        i += 1;
      }
    }
    
    public TrackSelection createTrackSelection(TrackGroupArray paramTrackGroupArray)
    {
      return this.factory.createTrackSelection(paramTrackGroupArray.get(this.groupIndex), this.tracks);
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/trackselection/MappingTrackSelector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */