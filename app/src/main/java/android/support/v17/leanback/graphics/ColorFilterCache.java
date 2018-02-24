package android.support.v17.leanback.graphics;

import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.util.SparseArray;

public final class ColorFilterCache
{
  private static final SparseArray<ColorFilterCache> sColorToFiltersMap = new SparseArray();
  private final PorterDuffColorFilter[] mFilters = new PorterDuffColorFilter['Ā'];
  
  private ColorFilterCache(int paramInt1, int paramInt2, int paramInt3)
  {
    int i = 0;
    while (i <= 255)
    {
      int j = Color.argb(i, paramInt1, paramInt2, paramInt3);
      this.mFilters[i] = new PorterDuffColorFilter(j, PorterDuff.Mode.SRC_ATOP);
      i += 1;
    }
  }
  
  public static ColorFilterCache getColorFilterCache(int paramInt)
  {
    int i = Color.red(paramInt);
    int j = Color.green(paramInt);
    paramInt = Color.blue(paramInt);
    int k = Color.rgb(i, j, paramInt);
    ColorFilterCache localColorFilterCache2 = (ColorFilterCache)sColorToFiltersMap.get(k);
    ColorFilterCache localColorFilterCache1 = localColorFilterCache2;
    if (localColorFilterCache2 == null)
    {
      localColorFilterCache1 = new ColorFilterCache(i, j, paramInt);
      sColorToFiltersMap.put(k, localColorFilterCache1);
    }
    return localColorFilterCache1;
  }
  
  public ColorFilter getFilterForLevel(float paramFloat)
  {
    if ((paramFloat >= 0.0F) && (paramFloat <= 1.0D))
    {
      int i = (int)(255.0F * paramFloat);
      return this.mFilters[i];
    }
    return null;
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/graphics/ColorFilterCache.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */