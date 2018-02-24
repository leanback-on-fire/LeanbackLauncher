package android.support.v7.graphics;

import android.graphics.Color;
import android.support.v4.graphics.ColorUtils;
import android.util.TimingLogger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;

final class ColorCutQuantizer
{
  static final int COMPONENT_BLUE = -1;
  static final int COMPONENT_GREEN = -2;
  static final int COMPONENT_RED = -3;
  private static final String LOG_TAG = "ColorCutQuantizer";
  private static final boolean LOG_TIMINGS = false;
  private static final int QUANTIZE_WORD_MASK = 31;
  private static final int QUANTIZE_WORD_WIDTH = 5;
  private static final Comparator<Vbox> VBOX_COMPARATOR_VOLUME = new Comparator()
  {
    public int compare(ColorCutQuantizer.Vbox paramAnonymousVbox1, ColorCutQuantizer.Vbox paramAnonymousVbox2)
    {
      return paramAnonymousVbox2.getVolume() - paramAnonymousVbox1.getVolume();
    }
  };
  final int[] mColors;
  final Palette.Filter[] mFilters;
  final int[] mHistogram;
  final List<Palette.Swatch> mQuantizedColors;
  private final float[] mTempHsl = new float[3];
  final TimingLogger mTimingLogger = null;
  
  ColorCutQuantizer(int[] paramArrayOfInt, int paramInt, Palette.Filter[] paramArrayOfFilter)
  {
    this.mFilters = paramArrayOfFilter;
    paramArrayOfFilter = new int[32768];
    this.mHistogram = paramArrayOfFilter;
    int i = 0;
    while (i < paramArrayOfInt.length)
    {
      j = quantizeFromRgb888(paramArrayOfInt[i]);
      paramArrayOfInt[i] = j;
      paramArrayOfFilter[j] += 1;
      i += 1;
    }
    i = 0;
    int j = 0;
    while (j < paramArrayOfFilter.length)
    {
      if ((paramArrayOfFilter[j] > 0) && (shouldIgnoreColor(j))) {
        paramArrayOfFilter[j] = 0;
      }
      k = i;
      if (paramArrayOfFilter[j] > 0) {
        k = i + 1;
      }
      j += 1;
      i = k;
    }
    paramArrayOfInt = new int[i];
    this.mColors = paramArrayOfInt;
    int k = 0;
    j = 0;
    while (j < paramArrayOfFilter.length)
    {
      int m = k;
      if (paramArrayOfFilter[j] > 0)
      {
        paramArrayOfInt[k] = j;
        m = k + 1;
      }
      j += 1;
      k = m;
    }
    if (i <= paramInt)
    {
      this.mQuantizedColors = new ArrayList();
      i = paramArrayOfInt.length;
      paramInt = 0;
      while (paramInt < i)
      {
        j = paramArrayOfInt[paramInt];
        this.mQuantizedColors.add(new Palette.Swatch(approximateToRgb888(j), paramArrayOfFilter[j]));
        paramInt += 1;
      }
    }
    this.mQuantizedColors = quantizePixels(paramInt);
  }
  
  private static int approximateToRgb888(int paramInt)
  {
    return approximateToRgb888(quantizedRed(paramInt), quantizedGreen(paramInt), quantizedBlue(paramInt));
  }
  
  static int approximateToRgb888(int paramInt1, int paramInt2, int paramInt3)
  {
    return Color.rgb(modifyWordWidth(paramInt1, 5, 8), modifyWordWidth(paramInt2, 5, 8), modifyWordWidth(paramInt3, 5, 8));
  }
  
  private List<Palette.Swatch> generateAverageColors(Collection<Vbox> paramCollection)
  {
    ArrayList localArrayList = new ArrayList(paramCollection.size());
    paramCollection = paramCollection.iterator();
    while (paramCollection.hasNext())
    {
      Palette.Swatch localSwatch = ((Vbox)paramCollection.next()).getAverageColor();
      if (!shouldIgnoreColor(localSwatch)) {
        localArrayList.add(localSwatch);
      }
    }
    return localArrayList;
  }
  
  static void modifySignificantOctet(int[] paramArrayOfInt, int paramInt1, int paramInt2, int paramInt3)
  {
    switch (paramInt1)
    {
    }
    for (;;)
    {
      return;
      paramInt1 = paramInt2;
      while (paramInt1 <= paramInt3)
      {
        paramInt2 = paramArrayOfInt[paramInt1];
        paramArrayOfInt[paramInt1] = (quantizedGreen(paramInt2) << 10 | quantizedRed(paramInt2) << 5 | quantizedBlue(paramInt2));
        paramInt1 += 1;
      }
      continue;
      paramInt1 = paramInt2;
      while (paramInt1 <= paramInt3)
      {
        paramInt2 = paramArrayOfInt[paramInt1];
        paramArrayOfInt[paramInt1] = (quantizedBlue(paramInt2) << 10 | quantizedGreen(paramInt2) << 5 | quantizedRed(paramInt2));
        paramInt1 += 1;
      }
    }
  }
  
  private static int modifyWordWidth(int paramInt1, int paramInt2, int paramInt3)
  {
    if (paramInt3 > paramInt2) {
      paramInt1 <<= paramInt3 - paramInt2;
    }
    for (;;)
    {
      return (1 << paramInt3) - 1 & paramInt1;
      paramInt1 >>= paramInt2 - paramInt3;
    }
  }
  
  private static int quantizeFromRgb888(int paramInt)
  {
    return modifyWordWidth(Color.red(paramInt), 8, 5) << 10 | modifyWordWidth(Color.green(paramInt), 8, 5) << 5 | modifyWordWidth(Color.blue(paramInt), 8, 5);
  }
  
  private List<Palette.Swatch> quantizePixels(int paramInt)
  {
    PriorityQueue localPriorityQueue = new PriorityQueue(paramInt, VBOX_COMPARATOR_VOLUME);
    localPriorityQueue.offer(new Vbox(0, this.mColors.length - 1));
    splitBoxes(localPriorityQueue, paramInt);
    return generateAverageColors(localPriorityQueue);
  }
  
  static int quantizedBlue(int paramInt)
  {
    return paramInt & 0x1F;
  }
  
  static int quantizedGreen(int paramInt)
  {
    return paramInt >> 5 & 0x1F;
  }
  
  static int quantizedRed(int paramInt)
  {
    return paramInt >> 10 & 0x1F;
  }
  
  private boolean shouldIgnoreColor(int paramInt)
  {
    paramInt = approximateToRgb888(paramInt);
    ColorUtils.colorToHSL(paramInt, this.mTempHsl);
    return shouldIgnoreColor(paramInt, this.mTempHsl);
  }
  
  private boolean shouldIgnoreColor(int paramInt, float[] paramArrayOfFloat)
  {
    if ((this.mFilters != null) && (this.mFilters.length > 0))
    {
      int i = 0;
      int j = this.mFilters.length;
      while (i < j)
      {
        if (!this.mFilters[i].isAllowed(paramInt, paramArrayOfFloat)) {
          return true;
        }
        i += 1;
      }
    }
    return false;
  }
  
  private boolean shouldIgnoreColor(Palette.Swatch paramSwatch)
  {
    return shouldIgnoreColor(paramSwatch.getRgb(), paramSwatch.getHsl());
  }
  
  private void splitBoxes(PriorityQueue<Vbox> paramPriorityQueue, int paramInt)
  {
    while (paramPriorityQueue.size() < paramInt)
    {
      Vbox localVbox = (Vbox)paramPriorityQueue.poll();
      if ((localVbox == null) || (!localVbox.canSplit())) {
        break;
      }
      paramPriorityQueue.offer(localVbox.splitBox());
      paramPriorityQueue.offer(localVbox);
    }
  }
  
  List<Palette.Swatch> getQuantizedColors()
  {
    return this.mQuantizedColors;
  }
  
  private class Vbox
  {
    private int mLowerIndex;
    private int mMaxBlue;
    private int mMaxGreen;
    private int mMaxRed;
    private int mMinBlue;
    private int mMinGreen;
    private int mMinRed;
    private int mPopulation;
    private int mUpperIndex;
    
    Vbox(int paramInt1, int paramInt2)
    {
      this.mLowerIndex = paramInt1;
      this.mUpperIndex = paramInt2;
      fitBox();
    }
    
    final boolean canSplit()
    {
      return getColorCount() > 1;
    }
    
    final int findSplitPoint()
    {
      int i = getLongestColorDimension();
      int[] arrayOfInt1 = ColorCutQuantizer.this.mColors;
      int[] arrayOfInt2 = ColorCutQuantizer.this.mHistogram;
      ColorCutQuantizer.modifySignificantOctet(arrayOfInt1, i, this.mLowerIndex, this.mUpperIndex);
      Arrays.sort(arrayOfInt1, this.mLowerIndex, this.mUpperIndex + 1);
      ColorCutQuantizer.modifySignificantOctet(arrayOfInt1, i, this.mLowerIndex, this.mUpperIndex);
      int k = this.mPopulation / 2;
      i = this.mLowerIndex;
      int j = 0;
      while (i <= this.mUpperIndex)
      {
        j += arrayOfInt2[arrayOfInt1[i]];
        if (j >= k) {
          return Math.min(this.mUpperIndex - 1, i);
        }
        i += 1;
      }
      return this.mLowerIndex;
    }
    
    final void fitBox()
    {
      int[] arrayOfInt1 = ColorCutQuantizer.this.mColors;
      int[] arrayOfInt2 = ColorCutQuantizer.this.mHistogram;
      int i = Integer.MAX_VALUE;
      int k = Integer.MAX_VALUE;
      int i2 = Integer.MAX_VALUE;
      int m = Integer.MIN_VALUE;
      int i1 = Integer.MIN_VALUE;
      int i5 = Integer.MIN_VALUE;
      int i4 = 0;
      int j = this.mLowerIndex;
      while (j <= this.mUpperIndex)
      {
        int n = arrayOfInt1[j];
        int i8 = i4 + arrayOfInt2[n];
        int i7 = ColorCutQuantizer.quantizedRed(n);
        int i6 = ColorCutQuantizer.quantizedGreen(n);
        i4 = ColorCutQuantizer.quantizedBlue(n);
        n = i5;
        if (i7 > i5) {
          n = i7;
        }
        int i3 = i2;
        if (i7 < i2) {
          i3 = i7;
        }
        i2 = i1;
        if (i6 > i1) {
          i2 = i6;
        }
        i7 = k;
        if (i6 < k) {
          i7 = i6;
        }
        i1 = m;
        if (i4 > m) {
          i1 = i4;
        }
        k = i;
        if (i4 < i) {
          k = i4;
        }
        j += 1;
        i4 = i8;
        m = i1;
        i1 = i2;
        i5 = n;
        i = k;
        k = i7;
        i2 = i3;
      }
      this.mMinRed = i2;
      this.mMaxRed = i5;
      this.mMinGreen = k;
      this.mMaxGreen = i1;
      this.mMinBlue = i;
      this.mMaxBlue = m;
      this.mPopulation = i4;
    }
    
    final Palette.Swatch getAverageColor()
    {
      int[] arrayOfInt1 = ColorCutQuantizer.this.mColors;
      int[] arrayOfInt2 = ColorCutQuantizer.this.mHistogram;
      int m = 0;
      int k = 0;
      int j = 0;
      int n = 0;
      int i = this.mLowerIndex;
      while (i <= this.mUpperIndex)
      {
        int i1 = arrayOfInt1[i];
        int i2 = arrayOfInt2[i1];
        n += i2;
        m += ColorCutQuantizer.quantizedRed(i1) * i2;
        k += ColorCutQuantizer.quantizedGreen(i1) * i2;
        j += ColorCutQuantizer.quantizedBlue(i1) * i2;
        i += 1;
      }
      return new Palette.Swatch(ColorCutQuantizer.approximateToRgb888(Math.round(m / n), Math.round(k / n), Math.round(j / n)), n);
    }
    
    final int getColorCount()
    {
      return this.mUpperIndex + 1 - this.mLowerIndex;
    }
    
    final int getLongestColorDimension()
    {
      int i = this.mMaxRed - this.mMinRed;
      int j = this.mMaxGreen - this.mMinGreen;
      int k = this.mMaxBlue - this.mMinBlue;
      if ((i >= j) && (i >= k)) {
        return -3;
      }
      if ((j >= i) && (j >= k)) {
        return -2;
      }
      return -1;
    }
    
    final int getVolume()
    {
      return (this.mMaxRed - this.mMinRed + 1) * (this.mMaxGreen - this.mMinGreen + 1) * (this.mMaxBlue - this.mMinBlue + 1);
    }
    
    final Vbox splitBox()
    {
      if (!canSplit()) {
        throw new IllegalStateException("Can not split a box with only 1 color");
      }
      int i = findSplitPoint();
      Vbox localVbox = new Vbox(ColorCutQuantizer.this, i + 1, this.mUpperIndex);
      this.mUpperIndex = i;
      fitBox();
      return localVbox;
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v7/graphics/ColorCutQuantizer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */