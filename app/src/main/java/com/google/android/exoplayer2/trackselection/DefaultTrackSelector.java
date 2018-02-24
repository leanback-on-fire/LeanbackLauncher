package com.google.android.exoplayer2.trackselection;

import android.content.Context;
import android.graphics.Point;
import android.text.TextUtils;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.RendererCapabilities;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class DefaultTrackSelector
  extends MappingTrackSelector
{
  private static final float FRACTION_TO_CONSIDER_FULLSCREEN = 0.98F;
  private static final int[] NO_TRACKS = new int[0];
  private static final int WITHIN_RENDERER_CAPABILITIES_BONUS = 1000;
  private final TrackSelection.Factory adaptiveVideoTrackSelectionFactory;
  private final AtomicReference<Parameters> paramsReference;
  
  public DefaultTrackSelector()
  {
    this(null);
  }
  
  public DefaultTrackSelector(TrackSelection.Factory paramFactory)
  {
    this.adaptiveVideoTrackSelectionFactory = paramFactory;
    this.paramsReference = new AtomicReference(new Parameters());
  }
  
  private static int compareFormatValues(int paramInt1, int paramInt2)
  {
    int i = -1;
    if (paramInt1 == -1)
    {
      paramInt1 = i;
      if (paramInt2 == -1) {
        paramInt1 = 0;
      }
      return paramInt1;
    }
    if (paramInt2 == -1) {
      return 1;
    }
    return paramInt1 - paramInt2;
  }
  
  private static void filterAdaptiveTrackCountForMimeType(TrackGroup paramTrackGroup, int[] paramArrayOfInt, int paramInt1, String paramString, int paramInt2, int paramInt3, List<Integer> paramList)
  {
    int i = paramList.size() - 1;
    while (i >= 0)
    {
      int j = ((Integer)paramList.get(i)).intValue();
      if (!isSupportedAdaptiveVideoTrack(paramTrackGroup.getFormat(j), paramString, paramArrayOfInt[j], paramInt1, paramInt2, paramInt3)) {
        paramList.remove(i);
      }
      i -= 1;
    }
  }
  
  protected static boolean formatHasLanguage(Format paramFormat, String paramString)
  {
    return (paramString != null) && (paramString.equals(Util.normalizeLanguageCode(paramFormat.language)));
  }
  
  private static int getAdaptiveTrackCountForMimeType(TrackGroup paramTrackGroup, int[] paramArrayOfInt, int paramInt1, String paramString, int paramInt2, int paramInt3, List<Integer> paramList)
  {
    int j = 0;
    int i = 0;
    while (i < paramList.size())
    {
      int m = ((Integer)paramList.get(i)).intValue();
      int k = j;
      if (isSupportedAdaptiveVideoTrack(paramTrackGroup.getFormat(m), paramString, paramArrayOfInt[m], paramInt1, paramInt2, paramInt3)) {
        k = j + 1;
      }
      i += 1;
      j = k;
    }
    return j;
  }
  
  private static int[] getAdaptiveTracksForGroup(TrackGroup paramTrackGroup, int[] paramArrayOfInt, boolean paramBoolean1, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, boolean paramBoolean2)
  {
    if (paramTrackGroup.length < 2) {
      return NO_TRACKS;
    }
    List localList = getViewportFilteredTrackIndices(paramTrackGroup, paramInt4, paramInt5, paramBoolean2);
    if (localList.size() < 2) {
      return NO_TRACKS;
    }
    Object localObject2 = null;
    Object localObject1 = null;
    if (!paramBoolean1)
    {
      HashSet localHashSet = new HashSet();
      paramInt5 = 0;
      paramInt4 = 0;
      for (;;)
      {
        localObject2 = localObject1;
        if (paramInt4 >= localList.size()) {
          break;
        }
        String str = paramTrackGroup.getFormat(((Integer)localList.get(paramInt4)).intValue()).sampleMimeType;
        localObject2 = localObject1;
        int i = paramInt5;
        if (!localHashSet.contains(str))
        {
          localHashSet.add(str);
          int j = getAdaptiveTrackCountForMimeType(paramTrackGroup, paramArrayOfInt, paramInt1, str, paramInt2, paramInt3, localList);
          localObject2 = localObject1;
          i = paramInt5;
          if (j > paramInt5)
          {
            localObject2 = str;
            i = j;
          }
        }
        paramInt4 += 1;
        localObject1 = localObject2;
        paramInt5 = i;
      }
    }
    filterAdaptiveTrackCountForMimeType(paramTrackGroup, paramArrayOfInt, paramInt1, (String)localObject2, paramInt2, paramInt3, localList);
    if (localList.size() < 2) {
      return NO_TRACKS;
    }
    return Util.toArray(localList);
  }
  
  private static Point getMaxVideoSizeInViewport(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int m = 1;
    int k = paramInt1;
    int j = paramInt2;
    int i;
    if (paramBoolean)
    {
      if (paramInt3 <= paramInt4) {
        break label77;
      }
      i = 1;
      if (paramInt1 <= paramInt2) {
        break label83;
      }
    }
    for (;;)
    {
      k = paramInt1;
      j = paramInt2;
      if (i != m)
      {
        j = paramInt1;
        k = paramInt2;
      }
      if (paramInt3 * j < paramInt4 * k) {
        break label89;
      }
      return new Point(k, Util.ceilDivide(k * paramInt4, paramInt3));
      label77:
      i = 0;
      break;
      label83:
      m = 0;
    }
    label89:
    return new Point(Util.ceilDivide(j * paramInt3, paramInt4), j);
  }
  
  private static List<Integer> getViewportFilteredTrackIndices(TrackGroup paramTrackGroup, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    ArrayList localArrayList = new ArrayList(paramTrackGroup.length);
    int i = 0;
    while (i < paramTrackGroup.length)
    {
      localArrayList.add(Integer.valueOf(i));
      i += 1;
    }
    if ((paramInt1 == Integer.MAX_VALUE) || (paramInt2 == Integer.MAX_VALUE)) {}
    for (;;)
    {
      return localArrayList;
      i = Integer.MAX_VALUE;
      int j = 0;
      while (j < paramTrackGroup.length)
      {
        Format localFormat = paramTrackGroup.getFormat(j);
        int k = i;
        if (localFormat.width > 0)
        {
          k = i;
          if (localFormat.height > 0)
          {
            Point localPoint = getMaxVideoSizeInViewport(paramBoolean, paramInt1, paramInt2, localFormat.width, localFormat.height);
            int m = localFormat.width * localFormat.height;
            k = i;
            if (localFormat.width >= (int)(localPoint.x * 0.98F))
            {
              k = i;
              if (localFormat.height >= (int)(localPoint.y * 0.98F))
              {
                k = i;
                if (m < i) {
                  k = m;
                }
              }
            }
          }
        }
        j += 1;
        i = k;
      }
      if (i != Integer.MAX_VALUE)
      {
        paramInt1 = localArrayList.size() - 1;
        while (paramInt1 >= 0)
        {
          paramInt2 = paramTrackGroup.getFormat(((Integer)localArrayList.get(paramInt1)).intValue()).getPixelCount();
          if ((paramInt2 == -1) || (paramInt2 > i)) {
            localArrayList.remove(paramInt1);
          }
          paramInt1 -= 1;
        }
      }
    }
  }
  
  protected static boolean isSupported(int paramInt, boolean paramBoolean)
  {
    paramInt &= 0x3;
    return (paramInt == 3) || ((paramBoolean) && (paramInt == 2));
  }
  
  private static boolean isSupportedAdaptiveVideoTrack(Format paramFormat, String paramString, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (isSupported(paramInt1, false))
    {
      bool1 = bool2;
      if ((paramInt1 & paramInt2) != 0) {
        if (paramString != null)
        {
          bool1 = bool2;
          if (!Util.areEqual(paramFormat.sampleMimeType, paramString)) {}
        }
        else if (paramFormat.width != -1)
        {
          bool1 = bool2;
          if (paramFormat.width > paramInt3) {}
        }
        else if (paramFormat.height != -1)
        {
          bool1 = bool2;
          if (paramFormat.height > paramInt4) {}
        }
        else
        {
          bool1 = true;
        }
      }
    }
    return bool1;
  }
  
  private static TrackSelection selectAdaptiveVideoTrack(RendererCapabilities paramRendererCapabilities, TrackGroupArray paramTrackGroupArray, int[][] paramArrayOfInt, int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2, int paramInt3, int paramInt4, boolean paramBoolean3, TrackSelection.Factory paramFactory)
    throws ExoPlaybackException
  {
    int i;
    label29:
    int j;
    if (paramBoolean1)
    {
      i = 12;
      if ((!paramBoolean2) || ((paramRendererCapabilities.supportsMixedMimeTypeAdaptation() & i) == 0)) {
        break label95;
      }
      paramBoolean1 = true;
      j = 0;
    }
    for (;;)
    {
      if (j >= paramTrackGroupArray.length) {
        break label110;
      }
      paramRendererCapabilities = paramTrackGroupArray.get(j);
      int[] arrayOfInt = getAdaptiveTracksForGroup(paramRendererCapabilities, paramArrayOfInt[j], paramBoolean1, i, paramInt1, paramInt2, paramInt3, paramInt4, paramBoolean3);
      if (arrayOfInt.length > 0)
      {
        return paramFactory.createTrackSelection(paramRendererCapabilities, arrayOfInt);
        i = 8;
        break;
        label95:
        paramBoolean1 = false;
        break label29;
      }
      j += 1;
    }
    label110:
    return null;
  }
  
  private static TrackSelection selectFixedVideoTrack(TrackGroupArray paramTrackGroupArray, int[][] paramArrayOfInt, int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    Object localObject1 = null;
    int i1 = 0;
    int n = 0;
    int i3 = -1;
    int i2 = -1;
    int m = 0;
    while (m < paramTrackGroupArray.length)
    {
      TrackGroup localTrackGroup = paramTrackGroupArray.get(m);
      List localList = getViewportFilteredTrackIndices(localTrackGroup, paramInt3, paramInt4, paramBoolean1);
      int[] arrayOfInt = paramArrayOfInt[m];
      int j = 0;
      if (j < localTrackGroup.length)
      {
        int i4 = i3;
        Object localObject2 = localObject1;
        int i5 = i2;
        int i6 = i1;
        int i7 = n;
        Format localFormat;
        if (isSupported(arrayOfInt[j], paramBoolean3))
        {
          localFormat = localTrackGroup.getFormat(j);
          if ((!localList.contains(Integer.valueOf(j))) || ((localFormat.width != -1) && (localFormat.width > paramInt1)) || ((localFormat.height != -1) && (localFormat.height > paramInt2))) {
            break label222;
          }
        }
        label222:
        for (i4 = 1;; i4 = 0)
        {
          if ((i4 != 0) || (paramBoolean2)) {
            break label228;
          }
          i7 = n;
          i6 = i1;
          i5 = i2;
          localObject2 = localObject1;
          i4 = i3;
          j += 1;
          i3 = i4;
          localObject1 = localObject2;
          i2 = i5;
          i1 = i6;
          n = i7;
          break;
        }
        label228:
        int i;
        label236:
        int k;
        if (i4 != 0)
        {
          i = 2;
          k = i;
          if (isSupported(arrayOfInt[j], false)) {
            k = i + 1000;
          }
          if (k <= n) {
            break label372;
          }
          i = 1;
          label270:
          if (k == n)
          {
            if (localFormat.getPixelCount() == i2) {
              break label378;
            }
            i = compareFormatValues(localFormat.getPixelCount(), i2);
            label299:
            if (i4 == 0) {
              break label399;
            }
            if (i <= 0) {
              break label393;
            }
            i = 1;
          }
        }
        for (;;)
        {
          i4 = i3;
          localObject2 = localObject1;
          i5 = i2;
          i6 = i1;
          i7 = n;
          if (i == 0) {
            break;
          }
          localObject2 = localTrackGroup;
          i6 = j;
          i4 = localFormat.bitrate;
          i5 = localFormat.getPixelCount();
          i7 = k;
          break;
          i = 1;
          break label236;
          label372:
          i = 0;
          break label270;
          label378:
          i = compareFormatValues(localFormat.bitrate, i3);
          break label299;
          label393:
          i = 0;
          continue;
          label399:
          if (i < 0) {
            i = 1;
          } else {
            i = 0;
          }
        }
      }
      m += 1;
    }
    if (localObject1 == null) {
      return null;
    }
    return new FixedTrackSelection((TrackGroup)localObject1, i1);
  }
  
  public Parameters getParameters()
  {
    return (Parameters)this.paramsReference.get();
  }
  
  protected TrackSelection selectAudioTrack(TrackGroupArray paramTrackGroupArray, int[][] paramArrayOfInt, String paramString, boolean paramBoolean)
  {
    Object localObject1 = null;
    int i1 = 0;
    int n = 0;
    int m = 0;
    while (m < paramTrackGroupArray.length)
    {
      TrackGroup localTrackGroup = paramTrackGroupArray.get(m);
      int[] arrayOfInt = paramArrayOfInt[m];
      int j = 0;
      if (j < localTrackGroup.length)
      {
        Object localObject2 = localObject1;
        int i2 = i1;
        int i = n;
        if (isSupported(arrayOfInt[j], paramBoolean))
        {
          localObject2 = localTrackGroup.getFormat(j);
          if ((((Format)localObject2).selectionFlags & 0x1) == 0) {
            break label188;
          }
          i = 1;
          label95:
          if (!formatHasLanguage((Format)localObject2, paramString)) {
            break label200;
          }
          if (i == 0) {
            break label194;
          }
          i = 4;
        }
        for (;;)
        {
          int k = i;
          if (isSupported(arrayOfInt[j], false)) {
            k = i + 1000;
          }
          localObject2 = localObject1;
          i2 = i1;
          i = n;
          if (k > n)
          {
            localObject2 = localTrackGroup;
            i2 = j;
            i = k;
          }
          j += 1;
          localObject1 = localObject2;
          i1 = i2;
          n = i;
          break;
          label188:
          i = 0;
          break label95;
          label194:
          i = 3;
          continue;
          label200:
          if (i != 0) {
            i = 2;
          } else {
            i = 1;
          }
        }
      }
      m += 1;
    }
    if (localObject1 == null) {
      return null;
    }
    return new FixedTrackSelection((TrackGroup)localObject1, i1);
  }
  
  protected TrackSelection selectOtherTrack(int paramInt, TrackGroupArray paramTrackGroupArray, int[][] paramArrayOfInt, boolean paramBoolean)
  {
    Object localObject1 = null;
    int n = 0;
    int m = 0;
    int k = 0;
    while (k < paramTrackGroupArray.length)
    {
      TrackGroup localTrackGroup = paramTrackGroupArray.get(k);
      int[] arrayOfInt = paramArrayOfInt[k];
      paramInt = 0;
      if (paramInt < localTrackGroup.length)
      {
        Object localObject2 = localObject1;
        int i1 = n;
        int j = m;
        int i;
        if (isSupported(arrayOfInt[paramInt], paramBoolean))
        {
          if ((localTrackGroup.getFormat(paramInt).selectionFlags & 0x1) == 0) {
            break label167;
          }
          i = 1;
          label87:
          if (i == 0) {
            break label173;
          }
        }
        label167:
        label173:
        for (j = 2;; j = 1)
        {
          i = j;
          if (isSupported(arrayOfInt[paramInt], false)) {
            i = j + 1000;
          }
          localObject2 = localObject1;
          i1 = n;
          j = m;
          if (i > m)
          {
            localObject2 = localTrackGroup;
            i1 = paramInt;
            j = i;
          }
          paramInt += 1;
          localObject1 = localObject2;
          n = i1;
          m = j;
          break;
          i = 0;
          break label87;
        }
      }
      k += 1;
    }
    if (localObject1 == null) {
      return null;
    }
    return new FixedTrackSelection((TrackGroup)localObject1, n);
  }
  
  protected TrackSelection selectTextTrack(TrackGroupArray paramTrackGroupArray, int[][] paramArrayOfInt, String paramString1, String paramString2, boolean paramBoolean)
  {
    Object localObject1 = null;
    int n = 0;
    int m = 0;
    int k = 0;
    TrackGroup localTrackGroup;
    int j;
    Object localObject2;
    int i2;
    int i1;
    Format localFormat;
    int i;
    label95:
    int i3;
    if (k < paramTrackGroupArray.length)
    {
      localTrackGroup = paramTrackGroupArray.get(k);
      int[] arrayOfInt = paramArrayOfInt[k];
      j = 0;
      if (j < localTrackGroup.length)
      {
        localObject2 = localObject1;
        i2 = n;
        i1 = m;
        if (isSupported(arrayOfInt[j], paramBoolean))
        {
          localFormat = localTrackGroup.getFormat(j);
          if ((localFormat.selectionFlags & 0x1) == 0) {
            break label198;
          }
          i = 1;
          if ((localFormat.selectionFlags & 0x2) == 0) {
            break label204;
          }
          i3 = 1;
          label108:
          if (!formatHasLanguage(localFormat, paramString1)) {
            break label227;
          }
          if (i == 0) {
            break label210;
          }
          i = 6;
          label126:
          if (!isSupported(arrayOfInt[j], false)) {
            break label305;
          }
          i += 1000;
        }
      }
    }
    label198:
    label204:
    label210:
    label227:
    label305:
    for (;;)
    {
      localObject2 = localObject1;
      i2 = n;
      i1 = m;
      if (i > m)
      {
        localObject2 = localTrackGroup;
        i2 = j;
        i1 = i;
      }
      do
      {
        j += 1;
        localObject1 = localObject2;
        n = i2;
        m = i1;
        break;
        i = 0;
        break label95;
        i3 = 0;
        break label108;
        if (i3 == 0)
        {
          i = 5;
          break label126;
        }
        i = 4;
        break label126;
        if (i != 0)
        {
          i = 3;
          break label126;
        }
        localObject2 = localObject1;
        i2 = n;
        i1 = m;
      } while (i3 == 0);
      if (formatHasLanguage(localFormat, paramString2))
      {
        i = 2;
        break label126;
      }
      i = 1;
      break label126;
      k += 1;
      break;
      if (localObject1 == null) {
        return null;
      }
      return new FixedTrackSelection((TrackGroup)localObject1, n);
    }
  }
  
  protected TrackSelection[] selectTracks(RendererCapabilities[] paramArrayOfRendererCapabilities, TrackGroupArray[] paramArrayOfTrackGroupArray, int[][][] paramArrayOfInt)
    throws ExoPlaybackException
  {
    TrackSelection[] arrayOfTrackSelection = new TrackSelection[paramArrayOfRendererCapabilities.length];
    Parameters localParameters = (Parameters)this.paramsReference.get();
    int i = 0;
    if (i < paramArrayOfRendererCapabilities.length)
    {
      switch (paramArrayOfRendererCapabilities[i].getTrackType())
      {
      default: 
        arrayOfTrackSelection[i] = selectOtherTrack(paramArrayOfRendererCapabilities[i].getTrackType(), paramArrayOfTrackGroupArray[i], paramArrayOfInt[i], localParameters.exceedRendererCapabilitiesIfNecessary);
      }
      for (;;)
      {
        i += 1;
        break;
        arrayOfTrackSelection[i] = selectVideoTrack(paramArrayOfRendererCapabilities[i], paramArrayOfTrackGroupArray[i], paramArrayOfInt[i], localParameters.maxVideoWidth, localParameters.maxVideoHeight, localParameters.allowNonSeamlessAdaptiveness, localParameters.allowMixedMimeAdaptiveness, localParameters.viewportWidth, localParameters.viewportHeight, localParameters.orientationMayChange, this.adaptiveVideoTrackSelectionFactory, localParameters.exceedVideoConstraintsIfNecessary, localParameters.exceedRendererCapabilitiesIfNecessary);
        continue;
        arrayOfTrackSelection[i] = selectAudioTrack(paramArrayOfTrackGroupArray[i], paramArrayOfInt[i], localParameters.preferredAudioLanguage, localParameters.exceedRendererCapabilitiesIfNecessary);
        continue;
        arrayOfTrackSelection[i] = selectTextTrack(paramArrayOfTrackGroupArray[i], paramArrayOfInt[i], localParameters.preferredTextLanguage, localParameters.preferredAudioLanguage, localParameters.exceedRendererCapabilitiesIfNecessary);
      }
    }
    return arrayOfTrackSelection;
  }
  
  protected TrackSelection selectVideoTrack(RendererCapabilities paramRendererCapabilities, TrackGroupArray paramTrackGroupArray, int[][] paramArrayOfInt, int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2, int paramInt3, int paramInt4, boolean paramBoolean3, TrackSelection.Factory paramFactory, boolean paramBoolean4, boolean paramBoolean5)
    throws ExoPlaybackException
  {
    TrackSelection localTrackSelection = null;
    if (paramFactory != null) {
      localTrackSelection = selectAdaptiveVideoTrack(paramRendererCapabilities, paramTrackGroupArray, paramArrayOfInt, paramInt1, paramInt2, paramBoolean1, paramBoolean2, paramInt3, paramInt4, paramBoolean3, paramFactory);
    }
    paramRendererCapabilities = localTrackSelection;
    if (localTrackSelection == null) {
      paramRendererCapabilities = selectFixedVideoTrack(paramTrackGroupArray, paramArrayOfInt, paramInt1, paramInt2, paramInt3, paramInt4, paramBoolean3, paramBoolean4, paramBoolean5);
    }
    return paramRendererCapabilities;
  }
  
  public void setParameters(Parameters paramParameters)
  {
    Assertions.checkNotNull(paramParameters);
    if (!((Parameters)this.paramsReference.getAndSet(paramParameters)).equals(paramParameters)) {
      invalidate();
    }
  }
  
  public static final class Parameters
  {
    public final boolean allowMixedMimeAdaptiveness;
    public final boolean allowNonSeamlessAdaptiveness;
    public final boolean exceedRendererCapabilitiesIfNecessary;
    public final boolean exceedVideoConstraintsIfNecessary;
    public final int maxVideoHeight;
    public final int maxVideoWidth;
    public final boolean orientationMayChange;
    public final String preferredAudioLanguage;
    public final String preferredTextLanguage;
    public final int viewportHeight;
    public final int viewportWidth;
    
    public Parameters()
    {
      this(null, null, false, true, Integer.MAX_VALUE, Integer.MAX_VALUE, true, true, Integer.MAX_VALUE, Integer.MAX_VALUE, true);
    }
    
    public Parameters(String paramString1, String paramString2, boolean paramBoolean1, boolean paramBoolean2, int paramInt1, int paramInt2, boolean paramBoolean3, boolean paramBoolean4, int paramInt3, int paramInt4, boolean paramBoolean5)
    {
      this.preferredAudioLanguage = paramString1;
      this.preferredTextLanguage = paramString2;
      this.allowMixedMimeAdaptiveness = paramBoolean1;
      this.allowNonSeamlessAdaptiveness = paramBoolean2;
      this.maxVideoWidth = paramInt1;
      this.maxVideoHeight = paramInt2;
      this.exceedVideoConstraintsIfNecessary = paramBoolean3;
      this.exceedRendererCapabilitiesIfNecessary = paramBoolean4;
      this.viewportWidth = paramInt3;
      this.viewportHeight = paramInt4;
      this.orientationMayChange = paramBoolean5;
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
        paramObject = (Parameters)paramObject;
      } while ((this.allowMixedMimeAdaptiveness == ((Parameters)paramObject).allowMixedMimeAdaptiveness) && (this.allowNonSeamlessAdaptiveness == ((Parameters)paramObject).allowNonSeamlessAdaptiveness) && (this.maxVideoWidth == ((Parameters)paramObject).maxVideoWidth) && (this.maxVideoHeight == ((Parameters)paramObject).maxVideoHeight) && (this.exceedVideoConstraintsIfNecessary == ((Parameters)paramObject).exceedVideoConstraintsIfNecessary) && (this.exceedRendererCapabilitiesIfNecessary == ((Parameters)paramObject).exceedRendererCapabilitiesIfNecessary) && (this.orientationMayChange == ((Parameters)paramObject).orientationMayChange) && (this.viewportWidth == ((Parameters)paramObject).viewportWidth) && (this.viewportHeight == ((Parameters)paramObject).viewportHeight) && (TextUtils.equals(this.preferredAudioLanguage, ((Parameters)paramObject).preferredAudioLanguage)) && (TextUtils.equals(this.preferredTextLanguage, ((Parameters)paramObject).preferredTextLanguage)));
      return false;
    }
    
    public int hashCode()
    {
      int n = 1;
      int i1 = this.preferredAudioLanguage.hashCode();
      int i2 = this.preferredTextLanguage.hashCode();
      int i;
      int j;
      label39:
      int i3;
      int i4;
      int k;
      label60:
      int m;
      if (this.allowMixedMimeAdaptiveness)
      {
        i = 1;
        if (!this.allowNonSeamlessAdaptiveness) {
          break label146;
        }
        j = 1;
        i3 = this.maxVideoWidth;
        i4 = this.maxVideoHeight;
        if (!this.exceedVideoConstraintsIfNecessary) {
          break label151;
        }
        k = 1;
        if (!this.exceedRendererCapabilitiesIfNecessary) {
          break label156;
        }
        m = 1;
        label70:
        if (!this.orientationMayChange) {
          break label162;
        }
      }
      for (;;)
      {
        return (((((((((i1 * 31 + i2) * 31 + i) * 31 + j) * 31 + i3) * 31 + i4) * 31 + k) * 31 + m) * 31 + n) * 31 + this.viewportWidth) * 31 + this.viewportHeight;
        i = 0;
        break;
        label146:
        j = 0;
        break label39;
        label151:
        k = 0;
        break label60;
        label156:
        m = 0;
        break label70;
        label162:
        n = 0;
      }
    }
    
    public Parameters withAllowMixedMimeAdaptiveness(boolean paramBoolean)
    {
      if (paramBoolean == this.allowMixedMimeAdaptiveness) {
        return this;
      }
      return new Parameters(this.preferredAudioLanguage, this.preferredTextLanguage, paramBoolean, this.allowNonSeamlessAdaptiveness, this.maxVideoWidth, this.maxVideoHeight, this.exceedVideoConstraintsIfNecessary, this.exceedRendererCapabilitiesIfNecessary, this.viewportWidth, this.viewportHeight, this.orientationMayChange);
    }
    
    public Parameters withAllowNonSeamlessAdaptiveness(boolean paramBoolean)
    {
      if (paramBoolean == this.allowNonSeamlessAdaptiveness) {
        return this;
      }
      return new Parameters(this.preferredAudioLanguage, this.preferredTextLanguage, this.allowMixedMimeAdaptiveness, paramBoolean, this.maxVideoWidth, this.maxVideoHeight, this.exceedVideoConstraintsIfNecessary, this.exceedRendererCapabilitiesIfNecessary, this.viewportWidth, this.viewportHeight, this.orientationMayChange);
    }
    
    public Parameters withExceedRendererCapabilitiesIfNecessary(boolean paramBoolean)
    {
      if (paramBoolean == this.exceedRendererCapabilitiesIfNecessary) {
        return this;
      }
      return new Parameters(this.preferredAudioLanguage, this.preferredTextLanguage, this.allowMixedMimeAdaptiveness, this.allowNonSeamlessAdaptiveness, this.maxVideoWidth, this.maxVideoHeight, this.exceedVideoConstraintsIfNecessary, paramBoolean, this.viewportWidth, this.viewportHeight, this.orientationMayChange);
    }
    
    public Parameters withExceedVideoConstraintsIfNecessary(boolean paramBoolean)
    {
      if (paramBoolean == this.exceedVideoConstraintsIfNecessary) {
        return this;
      }
      return new Parameters(this.preferredAudioLanguage, this.preferredTextLanguage, this.allowMixedMimeAdaptiveness, this.allowNonSeamlessAdaptiveness, this.maxVideoWidth, this.maxVideoHeight, paramBoolean, this.exceedRendererCapabilitiesIfNecessary, this.viewportWidth, this.viewportHeight, this.orientationMayChange);
    }
    
    public Parameters withMaxVideoSize(int paramInt1, int paramInt2)
    {
      if ((paramInt1 == this.maxVideoWidth) && (paramInt2 == this.maxVideoHeight)) {
        return this;
      }
      return new Parameters(this.preferredAudioLanguage, this.preferredTextLanguage, this.allowMixedMimeAdaptiveness, this.allowNonSeamlessAdaptiveness, paramInt1, paramInt2, this.exceedVideoConstraintsIfNecessary, this.exceedRendererCapabilitiesIfNecessary, this.viewportWidth, this.viewportHeight, this.orientationMayChange);
    }
    
    public Parameters withMaxVideoSizeSd()
    {
      return withMaxVideoSize(1279, 719);
    }
    
    public Parameters withPreferredAudioLanguage(String paramString)
    {
      paramString = Util.normalizeLanguageCode(paramString);
      if (TextUtils.equals(paramString, this.preferredAudioLanguage)) {
        return this;
      }
      return new Parameters(paramString, this.preferredTextLanguage, this.allowMixedMimeAdaptiveness, this.allowNonSeamlessAdaptiveness, this.maxVideoWidth, this.maxVideoHeight, this.exceedVideoConstraintsIfNecessary, this.exceedRendererCapabilitiesIfNecessary, this.viewportWidth, this.viewportHeight, this.orientationMayChange);
    }
    
    public Parameters withPreferredTextLanguage(String paramString)
    {
      paramString = Util.normalizeLanguageCode(paramString);
      if (TextUtils.equals(paramString, this.preferredTextLanguage)) {
        return this;
      }
      return new Parameters(this.preferredAudioLanguage, paramString, this.allowMixedMimeAdaptiveness, this.allowNonSeamlessAdaptiveness, this.maxVideoWidth, this.maxVideoHeight, this.exceedVideoConstraintsIfNecessary, this.exceedRendererCapabilitiesIfNecessary, this.viewportWidth, this.viewportHeight, this.orientationMayChange);
    }
    
    public Parameters withViewportSize(int paramInt1, int paramInt2, boolean paramBoolean)
    {
      if ((paramInt1 == this.viewportWidth) && (paramInt2 == this.viewportHeight) && (paramBoolean == this.orientationMayChange)) {
        return this;
      }
      return new Parameters(this.preferredAudioLanguage, this.preferredTextLanguage, this.allowMixedMimeAdaptiveness, this.allowNonSeamlessAdaptiveness, this.maxVideoWidth, this.maxVideoHeight, this.exceedVideoConstraintsIfNecessary, this.exceedRendererCapabilitiesIfNecessary, paramInt1, paramInt2, paramBoolean);
    }
    
    public Parameters withViewportSizeFromContext(Context paramContext, boolean paramBoolean)
    {
      paramContext = Util.getPhysicalDisplaySize(paramContext);
      return withViewportSize(paramContext.x, paramContext.y, paramBoolean);
    }
    
    public Parameters withoutVideoSizeConstraints()
    {
      return withMaxVideoSize(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }
    
    public Parameters withoutViewportSizeConstraints()
    {
      return withViewportSize(Integer.MAX_VALUE, Integer.MAX_VALUE, true);
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/trackselection/DefaultTrackSelector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */