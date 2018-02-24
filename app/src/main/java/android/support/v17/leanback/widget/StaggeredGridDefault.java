package android.support.v17.leanback.widget;

import android.support.v4.util.CircularArray;

final class StaggeredGridDefault
  extends StaggeredGrid
{
  private int findRowEdgeLimitSearchIndex(boolean paramBoolean)
  {
    int k = 0;
    int j = 0;
    int i;
    int m;
    if (paramBoolean)
    {
      i = this.mLastVisibleIndex;
      if (i >= this.mFirstVisibleIndex)
      {
        m = getLocation(i).row;
        if (m == 0) {
          k = 1;
        }
        do
        {
          do
          {
            i -= 1;
            j = k;
            break;
            k = j;
          } while (j == 0);
          k = j;
        } while (m != this.mNumRows - 1);
        return i;
      }
    }
    else
    {
      i = this.mFirstVisibleIndex;
      j = k;
      if (i <= this.mLastVisibleIndex)
      {
        m = getLocation(i).row;
        if (m == this.mNumRows - 1) {
          k = 1;
        }
        do
        {
          do
          {
            i += 1;
            j = k;
            break;
            k = j;
          } while (j == 0);
          k = j;
        } while (m != 0);
        return i;
      }
    }
    return -1;
  }
  
  protected boolean appendVisibleItemsWithoutCache(int paramInt, boolean paramBoolean)
  {
    int i5 = this.mProvider.getCount();
    boolean bool1;
    if (this.mLastVisibleIndex >= 0) {
      if (this.mLastVisibleIndex < getLastIndex()) {
        bool1 = false;
      }
    }
    label94:
    int j;
    label126:
    label165:
    label182:
    label232:
    label268:
    label301:
    label340:
    label368:
    label446:
    label453:
    label484:
    label505:
    label528:
    label579:
    label585:
    label595:
    label610:
    label618:
    label649:
    label680:
    label701:
    label738:
    label771:
    do
    {
      int n;
      boolean bool2;
      int i1;
      int i3;
      boolean bool3;
      int i2;
      do
      {
        do
        {
          do
          {
            do
            {
              return bool1;
              n = this.mLastVisibleIndex + 1;
              m = getLocation(this.mLastVisibleIndex).row;
              i = findRowEdgeLimitSearchIndex(true);
              if (i >= 0) {
                break label453;
              }
              k = Integer.MIN_VALUE;
              i = 0;
              if (i < this.mNumRows)
              {
                if (!this.mReversedFlow) {
                  break;
                }
                k = getRowMin(i);
                if (k == Integer.MIN_VALUE) {
                  break label446;
                }
              }
              if (!this.mReversedFlow) {
                break label484;
              }
              i = k;
              j = m;
              if (getRowMin(m) <= k)
              {
                m += 1;
                i = k;
                j = m;
                if (m == this.mNumRows)
                {
                  j = 0;
                  if (!this.mReversedFlow) {
                    break label505;
                  }
                  i = findRowMin(false, null);
                }
              }
              m = 1;
              k = i;
              bool2 = false;
              i1 = n;
              n = m;
              if (j >= this.mNumRows) {
                break label771;
              }
              bool1 = bool2;
            } while (i1 == i5);
            if (paramBoolean) {
              break;
            }
            bool1 = bool2;
          } while (checkAppendOverLimit(paramInt));
          if (!this.mReversedFlow) {
            break label585;
          }
          i = getRowMin(j);
          if ((i != Integer.MAX_VALUE) && (i != Integer.MIN_VALUE)) {
            break label649;
          }
          if (j != 0) {
            break label618;
          }
          if (!this.mReversedFlow) {
            break label595;
          }
          m = getRowMin(this.mNumRows - 1);
          i = m;
          if (m != Integer.MAX_VALUE)
          {
            i = m;
            if (m != Integer.MIN_VALUE)
            {
              if (!this.mReversedFlow) {
                break label610;
              }
              i = -this.mSpacing;
              i = m + i;
            }
          }
          m = i1 + 1;
          i3 = appendVisibleItemToRow(i1, j, i);
          bool3 = true;
          bool2 = true;
          if (n == 0) {
            break label738;
          }
          i1 = i;
          i = m;
          if (!this.mReversedFlow) {
            break label680;
          }
          i2 = k;
          i4 = n;
          m = i;
          if (i1 - i3 <= k) {
            break label701;
          }
          bool1 = bool2;
        } while (i == i5);
        if (paramBoolean) {
          break;
        }
        bool1 = bool2;
      } while (checkAppendOverLimit(paramInt));
      if (this.mReversedFlow) {}
      for (int m = -i3 - this.mSpacing;; m = this.mSpacing + i3)
      {
        i1 += m;
        i3 = appendVisibleItemToRow(i, j, i1);
        i += 1;
        break label340;
        k = getRowMax(i);
        break label94;
        i += 1;
        break;
        if (this.mReversedFlow) {}
        for (k = findRowMin(false, i, null);; k = findRowMax(true, i, null)) {
          break;
        }
        i = k;
        j = m;
        if (getRowMax(m) < k) {
          break label165;
        }
        break label126;
        i = findRowMax(true, null);
        break label165;
        if (this.mStartIndex != -1)
        {
          i = this.mStartIndex;
          if (this.mLocations.size() <= 0) {
            break label579;
          }
        }
        for (j = getLocation(getLastIndex()).row + 1;; j = i)
        {
          j %= this.mNumRows;
          k = 0;
          m = 0;
          n = i;
          break;
          i = 0;
          break label528;
        }
        i = getRowMax(j);
        break label232;
        m = getRowMax(this.mNumRows - 1);
        break label268;
        i = this.mSpacing;
        break label301;
        if (this.mReversedFlow) {}
        for (i = getRowMax(j - 1);; i = getRowMin(j - 1)) {
          break;
        }
        if (this.mReversedFlow) {}
        for (m = -this.mSpacing;; m = this.mSpacing)
        {
          i += m;
          break;
        }
        if (i1 + i3 < k) {
          break label368;
        }
        m = i;
        i4 = n;
        i2 = k;
        j += 1;
        k = i2;
        n = i4;
        bool2 = bool3;
        i1 = m;
        break label182;
      }
      int i4 = 1;
      if (this.mReversedFlow) {}
      for (int i = getRowMin(j);; i = getRowMax(j))
      {
        i2 = i;
        break;
      }
      bool1 = bool2;
    } while (paramBoolean);
    if (this.mReversedFlow) {}
    for (int k = findRowMin(false, null);; k = findRowMax(true, null))
    {
      j = 0;
      break;
    }
  }
  
  public int findRowMax(boolean paramBoolean, int paramInt, int[] paramArrayOfInt)
  {
    int i = this.mProvider.getEdge(paramInt);
    StaggeredGrid.Location localLocation = getLocation(paramInt);
    int k = localLocation.row;
    int m = paramInt;
    int i1 = 1;
    int n = 1;
    int j = k;
    int i2;
    int i6;
    int i3;
    int i7;
    int i5;
    int i4;
    if (this.mReversedFlow)
    {
      i2 = i;
      paramInt += 1;
      i1 = j;
      j = i2;
      i6 = i;
      i = m;
      i2 = k;
      i3 = j;
      if (n < this.mNumRows)
      {
        i = m;
        i2 = k;
        i3 = j;
        if (paramInt <= this.mLastVisibleIndex)
        {
          localLocation = getLocation(paramInt);
          i3 = i6 + localLocation.offset;
          i7 = m;
          i5 = k;
          i4 = j;
          i = i1;
          i2 = n;
          if (localLocation.row != i1)
          {
            i1 = localLocation.row;
            n += 1;
            if (!paramBoolean) {
              break label248;
            }
            i7 = m;
            i5 = k;
            i4 = j;
            i = i1;
            i2 = n;
            if (i3 <= j) {}
          }
          for (;;)
          {
            i5 = i1;
            i4 = i3;
            i7 = paramInt;
            i2 = n;
            i = i1;
            label248:
            do
            {
              paramInt += 1;
              i6 = i3;
              m = i7;
              k = i5;
              j = i4;
              i1 = i;
              n = i2;
              break;
              i7 = m;
              i5 = k;
              i4 = j;
              i = i1;
              i2 = n;
            } while (i3 >= j);
          }
        }
      }
    }
    else
    {
      i2 = i + this.mProvider.getSize(paramInt);
      paramInt -= 1;
      n = i1;
      i1 = j;
      j = i2;
      i6 = i;
      i = m;
      i2 = k;
      i3 = j;
      if (n < this.mNumRows)
      {
        i = m;
        i2 = k;
        i3 = j;
        if (paramInt >= this.mFirstVisibleIndex)
        {
          i6 -= localLocation.offset;
          localLocation = getLocation(paramInt);
          i5 = m;
          i4 = k;
          i3 = j;
          i = i1;
          i2 = n;
          if (localLocation.row != i1)
          {
            i1 = localLocation.row;
            n += 1;
            i7 = i6 + this.mProvider.getSize(paramInt);
            if (!paramBoolean) {
              break label506;
            }
            i5 = m;
            i4 = k;
            i3 = j;
            i = i1;
            i2 = n;
            if (i7 <= j) {}
          }
          for (;;)
          {
            i4 = i1;
            i3 = i7;
            i5 = paramInt;
            i2 = n;
            i = i1;
            label506:
            do
            {
              paramInt -= 1;
              m = i5;
              k = i4;
              j = i3;
              i1 = i;
              n = i2;
              break;
              i5 = m;
              i4 = k;
              i3 = j;
              i = i1;
              i2 = n;
            } while (i7 >= j);
          }
        }
      }
    }
    if (paramArrayOfInt != null)
    {
      paramArrayOfInt[0] = i2;
      paramArrayOfInt[1] = i;
    }
    return i3;
  }
  
  public int findRowMin(boolean paramBoolean, int paramInt, int[] paramArrayOfInt)
  {
    int i = this.mProvider.getEdge(paramInt);
    StaggeredGrid.Location localLocation = getLocation(paramInt);
    int k = localLocation.row;
    int m = paramInt;
    int i1 = 1;
    int n = 1;
    int j = k;
    int i2;
    int i6;
    int i3;
    int i5;
    int i4;
    int i7;
    if (this.mReversedFlow)
    {
      i2 = i - this.mProvider.getSize(paramInt);
      paramInt -= 1;
      i1 = j;
      j = i2;
      i6 = i;
      i = m;
      i2 = k;
      i3 = j;
      if (n < this.mNumRows)
      {
        i = m;
        i2 = k;
        i3 = j;
        if (paramInt >= this.mFirstVisibleIndex)
        {
          i6 -= localLocation.offset;
          localLocation = getLocation(paramInt);
          i5 = m;
          i4 = k;
          i3 = j;
          i = i1;
          i2 = n;
          if (localLocation.row != i1)
          {
            i1 = localLocation.row;
            n += 1;
            i7 = i6 - this.mProvider.getSize(paramInt);
            if (!paramBoolean) {
              break label270;
            }
            i5 = m;
            i4 = k;
            i3 = j;
            i = i1;
            i2 = n;
            if (i7 <= j) {}
          }
          for (;;)
          {
            i3 = i7;
            i4 = i1;
            i5 = paramInt;
            i2 = n;
            i = i1;
            label270:
            do
            {
              paramInt -= 1;
              m = i5;
              k = i4;
              j = i3;
              i1 = i;
              n = i2;
              break;
              i5 = m;
              i4 = k;
              i3 = j;
              i = i1;
              i2 = n;
            } while (i7 >= j);
          }
        }
      }
    }
    else
    {
      i2 = i;
      paramInt += 1;
      n = i1;
      i1 = j;
      j = i2;
      i6 = i;
      i = m;
      i2 = k;
      i3 = j;
      if (n < this.mNumRows)
      {
        i = m;
        i2 = k;
        i3 = j;
        if (paramInt <= this.mLastVisibleIndex)
        {
          localLocation = getLocation(paramInt);
          i3 = i6 + localLocation.offset;
          i7 = m;
          i5 = k;
          i4 = j;
          i = i1;
          i2 = n;
          if (localLocation.row != i1)
          {
            i1 = localLocation.row;
            n += 1;
            if (!paramBoolean) {
              break label506;
            }
            i7 = m;
            i5 = k;
            i4 = j;
            i = i1;
            i2 = n;
            if (i3 <= j) {}
          }
          for (;;)
          {
            i4 = i3;
            i5 = i1;
            i7 = paramInt;
            i2 = n;
            i = i1;
            label506:
            do
            {
              paramInt += 1;
              i6 = i3;
              m = i7;
              k = i5;
              j = i4;
              i1 = i;
              n = i2;
              break;
              i7 = m;
              i5 = k;
              i4 = j;
              i = i1;
              i2 = n;
            } while (i3 >= j);
          }
        }
      }
    }
    if (paramArrayOfInt != null)
    {
      paramArrayOfInt[0] = i2;
      paramArrayOfInt[1] = i;
    }
    return i3;
  }
  
  int getRowMax(int paramInt)
  {
    if (this.mFirstVisibleIndex < 0) {
      i = Integer.MIN_VALUE;
    }
    int k;
    do
    {
      return i;
      if (!this.mReversedFlow) {
        break;
      }
      k = this.mProvider.getEdge(this.mFirstVisibleIndex);
      i = k;
    } while (getLocation(this.mFirstVisibleIndex).row == paramInt);
    int j = this.mFirstVisibleIndex + 1;
    for (;;)
    {
      if (j > getLastIndex()) {
        break label198;
      }
      localLocation = getLocation(j);
      k += localLocation.offset;
      i = k;
      if (localLocation.row == paramInt) {
        break;
      }
      j += 1;
    }
    j = this.mProvider.getEdge(this.mLastVisibleIndex);
    StaggeredGrid.Location localLocation = getLocation(this.mLastVisibleIndex);
    if (localLocation.row == paramInt) {
      return j + localLocation.size;
    }
    int i = this.mLastVisibleIndex - 1;
    while (i >= getFirstIndex())
    {
      j -= localLocation.offset;
      localLocation = getLocation(i);
      if (localLocation.row == paramInt) {
        return j + localLocation.size;
      }
      i -= 1;
    }
    label198:
    return Integer.MIN_VALUE;
  }
  
  int getRowMin(int paramInt)
  {
    int i;
    if (this.mFirstVisibleIndex < 0) {
      i = Integer.MAX_VALUE;
    }
    StaggeredGrid.Location localLocation;
    int k;
    do
    {
      return i;
      if (this.mReversedFlow)
      {
        j = this.mProvider.getEdge(this.mLastVisibleIndex);
        localLocation = getLocation(this.mLastVisibleIndex);
        if (localLocation.row == paramInt) {
          return j - localLocation.size;
        }
        i = this.mLastVisibleIndex - 1;
        while (i >= getFirstIndex())
        {
          j -= localLocation.offset;
          localLocation = getLocation(i);
          if (localLocation.row == paramInt) {
            return j - localLocation.size;
          }
          i -= 1;
        }
      }
      k = this.mProvider.getEdge(this.mFirstVisibleIndex);
      i = k;
    } while (getLocation(this.mFirstVisibleIndex).row == paramInt);
    int j = this.mFirstVisibleIndex + 1;
    for (;;)
    {
      if (j > getLastIndex()) {
        break label198;
      }
      localLocation = getLocation(j);
      k += localLocation.offset;
      i = k;
      if (localLocation.row == paramInt) {
        break;
      }
      j += 1;
    }
    label198:
    return Integer.MAX_VALUE;
  }
  
  protected boolean prependVisibleItemsWithoutCache(int paramInt, boolean paramBoolean)
  {
    boolean bool1;
    if (this.mFirstVisibleIndex >= 0) {
      if (this.mFirstVisibleIndex > getFirstIndex()) {
        bool1 = false;
      }
    }
    int j;
    label94:
    label130:
    label170:
    label191:
    label235:
    label272:
    label304:
    label343:
    label371:
    label446:
    label453:
    label484:
    label505:
    label528:
    label584:
    label590:
    label600:
    label610:
    label619:
    label650:
    label681:
    label702:
    label740:
    label773:
    do
    {
      int n;
      boolean bool2;
      int i1;
      int i4;
      boolean bool3;
      int i2;
      do
      {
        do
        {
          do
          {
            do
            {
              return bool1;
              n = this.mFirstVisibleIndex - 1;
              m = getLocation(this.mFirstVisibleIndex).row;
              i = findRowEdgeLimitSearchIndex(false);
              if (i >= 0) {
                break label453;
              }
              k = m - 1;
              j = Integer.MAX_VALUE;
              i = this.mNumRows - 1;
              m = k;
              if (i >= 0)
              {
                if (!this.mReversedFlow) {
                  break;
                }
                j = getRowMax(i);
                if (j == Integer.MAX_VALUE) {
                  break label446;
                }
                m = k;
              }
              if (!this.mReversedFlow) {
                break label484;
              }
              i = j;
              k = m;
              if (getRowMax(m) >= j)
              {
                m -= 1;
                i = j;
                k = m;
                if (m < 0)
                {
                  k = this.mNumRows - 1;
                  if (!this.mReversedFlow) {
                    break label505;
                  }
                  i = findRowMax(true, null);
                }
              }
              m = 1;
              j = k;
              k = i;
              bool2 = false;
              i1 = n;
              n = m;
              if (j < 0) {
                break label773;
              }
              bool1 = bool2;
            } while (i1 < 0);
            if (paramBoolean) {
              break;
            }
            bool1 = bool2;
          } while (checkPrependOverLimit(paramInt));
          if (!this.mReversedFlow) {
            break label590;
          }
          i = getRowMax(j);
          if ((i != Integer.MAX_VALUE) && (i != Integer.MIN_VALUE)) {
            break label650;
          }
          if (j != this.mNumRows - 1) {
            break label619;
          }
          if (!this.mReversedFlow) {
            break label600;
          }
          m = getRowMax(0);
          i = m;
          if (m != Integer.MAX_VALUE)
          {
            i = m;
            if (m != Integer.MIN_VALUE)
            {
              if (!this.mReversedFlow) {
                break label610;
              }
              i = this.mSpacing;
              i = m + i;
            }
          }
          m = i1 - 1;
          i4 = prependVisibleItemToRow(i1, j, i);
          bool3 = true;
          bool2 = true;
          if (n == 0) {
            break label740;
          }
          i1 = i;
          i = m;
          if (!this.mReversedFlow) {
            break label681;
          }
          i2 = k;
          i3 = n;
          m = i;
          if (i1 + i4 >= k) {
            break label702;
          }
          bool1 = bool2;
        } while (i < 0);
        if (paramBoolean) {
          break;
        }
        bool1 = bool2;
      } while (checkPrependOverLimit(paramInt));
      if (this.mReversedFlow) {}
      for (int m = this.mSpacing + i4;; m = -i4 - this.mSpacing)
      {
        i1 += m;
        i4 = prependVisibleItemToRow(i, j, i1);
        i -= 1;
        break label343;
        j = getRowMin(i);
        break label94;
        i -= 1;
        break;
        if (this.mReversedFlow) {}
        for (j = findRowMax(true, i, null);; j = findRowMin(false, i, null)) {
          break;
        }
        i = j;
        k = m;
        if (getRowMin(m) > j) {
          break label170;
        }
        break label130;
        i = findRowMin(false, null);
        break label170;
        if (this.mStartIndex != -1)
        {
          i = this.mStartIndex;
          if (this.mLocations.size() < 0) {
            break label584;
          }
        }
        for (j = getLocation(getFirstIndex()).row + this.mNumRows - 1;; j = i)
        {
          j %= this.mNumRows;
          k = 0;
          m = 0;
          n = i;
          break;
          i = 0;
          break label528;
        }
        i = getRowMin(j);
        break label235;
        m = getRowMin(0);
        break label272;
        i = -this.mSpacing;
        break label304;
        if (this.mReversedFlow) {}
        for (i = getRowMin(j + 1);; i = getRowMax(j + 1)) {
          break;
        }
        if (this.mReversedFlow) {}
        for (m = this.mSpacing;; m = -this.mSpacing)
        {
          i += m;
          break;
        }
        if (i1 - i4 > k) {
          break label371;
        }
        m = i;
        i3 = n;
        i2 = k;
        j -= 1;
        k = i2;
        n = i3;
        bool2 = bool3;
        i1 = m;
        break label191;
      }
      int i3 = 1;
      if (this.mReversedFlow) {}
      for (int i = getRowMax(j);; i = getRowMin(j))
      {
        i2 = i;
        break;
      }
      bool1 = bool2;
    } while (paramBoolean);
    if (this.mReversedFlow) {}
    for (int k = findRowMax(true, null);; k = findRowMin(false, null))
    {
      j = this.mNumRows - 1;
      break;
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/StaggeredGridDefault.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */