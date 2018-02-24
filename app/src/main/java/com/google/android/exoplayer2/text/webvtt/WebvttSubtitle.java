package com.google.android.exoplayer2.text.webvtt;

import android.text.SpannableStringBuilder;
import com.google.android.exoplayer2.text.Cue;
import com.google.android.exoplayer2.text.Subtitle;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

final class WebvttSubtitle
  implements Subtitle
{
  private final long[] cueTimesUs;
  private final List<WebvttCue> cues;
  private final int numCues;
  private final long[] sortedCueTimesUs;
  
  public WebvttSubtitle(List<WebvttCue> paramList)
  {
    this.cues = paramList;
    this.numCues = paramList.size();
    this.cueTimesUs = new long[this.numCues * 2];
    int i = 0;
    while (i < this.numCues)
    {
      WebvttCue localWebvttCue = (WebvttCue)paramList.get(i);
      int j = i * 2;
      this.cueTimesUs[j] = localWebvttCue.startTime;
      this.cueTimesUs[(j + 1)] = localWebvttCue.endTime;
      i += 1;
    }
    this.sortedCueTimesUs = Arrays.copyOf(this.cueTimesUs, this.cueTimesUs.length);
    Arrays.sort(this.sortedCueTimesUs);
  }
  
  public List<Cue> getCues(long paramLong)
  {
    Object localObject2 = null;
    Object localObject4 = null;
    Object localObject3 = null;
    int i = 0;
    if (i < this.numCues)
    {
      Object localObject5 = localObject4;
      Object localObject1 = localObject2;
      Object localObject6 = localObject3;
      if (this.cueTimesUs[(i * 2)] <= paramLong)
      {
        localObject5 = localObject4;
        localObject1 = localObject2;
        localObject6 = localObject3;
        if (paramLong < this.cueTimesUs[(i * 2 + 1)])
        {
          localObject1 = localObject2;
          if (localObject2 == null) {
            localObject1 = new ArrayList();
          }
          localObject5 = (WebvttCue)this.cues.get(i);
          if (!((WebvttCue)localObject5).isNormalCue()) {
            break label212;
          }
          if (localObject4 != null) {
            break label140;
          }
          localObject6 = localObject3;
        }
      }
      for (;;)
      {
        i += 1;
        localObject4 = localObject5;
        localObject2 = localObject1;
        localObject3 = localObject6;
        break;
        label140:
        if (localObject3 == null)
        {
          localObject6 = new SpannableStringBuilder();
          ((SpannableStringBuilder)localObject6).append(((WebvttCue)localObject4).text).append("\n").append(((WebvttCue)localObject5).text);
          localObject5 = localObject4;
        }
        else
        {
          ((SpannableStringBuilder)localObject3).append("\n").append(((WebvttCue)localObject5).text);
          localObject5 = localObject4;
          localObject6 = localObject3;
          continue;
          label212:
          ((ArrayList)localObject1).add(localObject5);
          localObject5 = localObject4;
          localObject6 = localObject3;
        }
      }
    }
    if (localObject3 != null) {
      ((ArrayList)localObject2).add(new WebvttCue((CharSequence)localObject3));
    }
    while (localObject2 != null)
    {
      return (List<Cue>)localObject2;
      if (localObject4 != null) {
        ((ArrayList)localObject2).add(localObject4);
      }
    }
    return Collections.emptyList();
  }
  
  public long getEventTime(int paramInt)
  {
    boolean bool2 = true;
    if (paramInt >= 0)
    {
      bool1 = true;
      Assertions.checkArgument(bool1);
      if (paramInt >= this.sortedCueTimesUs.length) {
        break label39;
      }
    }
    label39:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      Assertions.checkArgument(bool1);
      return this.sortedCueTimesUs[paramInt];
      bool1 = false;
      break;
    }
  }
  
  public int getEventTimeCount()
  {
    return this.sortedCueTimesUs.length;
  }
  
  public int getNextEventTimeIndex(long paramLong)
  {
    int i = Util.binarySearchCeil(this.sortedCueTimesUs, paramLong, false, false);
    if (i < this.sortedCueTimesUs.length) {
      return i;
    }
    return -1;
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/text/webvtt/WebvttSubtitle.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */