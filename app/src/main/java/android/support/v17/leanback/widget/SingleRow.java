package android.support.v17.leanback.widget;

import android.support.annotation.NonNull;
import android.support.v4.util.CircularIntArray;
import android.support.v7.widget.RecyclerView.LayoutManager.LayoutPrefetchRegistry;
import java.io.PrintWriter;

class SingleRow
  extends Grid
{
  private final Grid.Location mTmpLocation = new Grid.Location(0);
  
  SingleRow()
  {
    setNumRows(1);
  }
  
  protected final boolean appendVisibleItems(int paramInt, boolean paramBoolean)
  {
    if (this.mProvider.getCount() == 0) {}
    while ((!paramBoolean) && (checkAppendOverLimit(paramInt))) {
      return false;
    }
    boolean bool1 = false;
    int j = getStartIndexForAppend();
    for (;;)
    {
      int k;
      if (j < this.mProvider.getCount())
      {
        k = this.mProvider.createItem(j, true, this.mTmpItem, false);
        if ((this.mFirstVisibleIndex >= 0) && (this.mLastVisibleIndex >= 0)) {
          break label160;
        }
        if (!this.mReversedFlow) {
          break label154;
        }
      }
      boolean bool2;
      label154:
      for (int i = Integer.MAX_VALUE;; i = Integer.MIN_VALUE)
      {
        this.mFirstVisibleIndex = j;
        this.mLastVisibleIndex = j;
        this.mProvider.addItem(this.mTmpItem[0], j, k, 0, i);
        boolean bool3 = true;
        bool2 = true;
        bool1 = bool3;
        if (!paramBoolean)
        {
          if (!checkAppendOverLimit(paramInt)) {
            break;
          }
          bool1 = bool3;
        }
        return bool1;
      }
      label160:
      if (this.mReversedFlow) {}
      for (i = this.mProvider.getEdge(j - 1) - this.mProvider.getSize(j - 1) - this.mSpacing;; i = this.mProvider.getEdge(j - 1) + this.mProvider.getSize(j - 1) + this.mSpacing)
      {
        this.mLastVisibleIndex = j;
        break;
      }
      j += 1;
      bool1 = bool2;
    }
  }
  
  public void collectAdjacentPrefetchPositions(int paramInt1, int paramInt2, @NonNull RecyclerView.LayoutManager.LayoutPrefetchRegistry paramLayoutPrefetchRegistry)
  {
    if (this.mReversedFlow) {
      if (paramInt2 <= 0) {
        break label23;
      }
    }
    label23:
    while (getLastVisibleIndex() == this.mProvider.getCount() - 1) {
      do
      {
        if (getFirstVisibleIndex() != 0) {
          break;
        }
        return;
      } while (paramInt2 < 0);
    }
    int j = getStartIndexForAppend();
    int i = this.mProvider.getSize(this.mLastVisibleIndex) + this.mSpacing;
    int k = this.mProvider.getEdge(this.mLastVisibleIndex);
    paramInt2 = i;
    if (this.mReversedFlow) {
      paramInt2 = -i;
    }
    i = k + paramInt2;
    paramInt2 = j;
    paramLayoutPrefetchRegistry.addPosition(paramInt2, Math.abs(i - paramInt1));
    return;
    i = getStartIndexForPrepend();
    j = this.mProvider.getEdge(this.mFirstVisibleIndex);
    if (this.mReversedFlow) {}
    for (paramInt2 = this.mSpacing;; paramInt2 = -this.mSpacing)
    {
      j += paramInt2;
      paramInt2 = i;
      i = j;
      break;
    }
  }
  
  public final void debugPrint(PrintWriter paramPrintWriter)
  {
    paramPrintWriter.print("SingleRow<");
    paramPrintWriter.print(this.mFirstVisibleIndex);
    paramPrintWriter.print(",");
    paramPrintWriter.print(this.mLastVisibleIndex);
    paramPrintWriter.print(">");
    paramPrintWriter.println();
  }
  
  protected final int findRowMax(boolean paramBoolean, int paramInt, int[] paramArrayOfInt)
  {
    if (paramArrayOfInt != null)
    {
      paramArrayOfInt[0] = 0;
      paramArrayOfInt[1] = paramInt;
    }
    if (this.mReversedFlow) {
      return this.mProvider.getEdge(paramInt);
    }
    return this.mProvider.getEdge(paramInt) + this.mProvider.getSize(paramInt);
  }
  
  protected final int findRowMin(boolean paramBoolean, int paramInt, int[] paramArrayOfInt)
  {
    if (paramArrayOfInt != null)
    {
      paramArrayOfInt[0] = 0;
      paramArrayOfInt[1] = paramInt;
    }
    if (this.mReversedFlow) {
      return this.mProvider.getEdge(paramInt) - this.mProvider.getSize(paramInt);
    }
    return this.mProvider.getEdge(paramInt);
  }
  
  public final CircularIntArray[] getItemPositionsInRows(int paramInt1, int paramInt2)
  {
    this.mTmpItemPositionsInRows[0].clear();
    this.mTmpItemPositionsInRows[0].addLast(paramInt1);
    this.mTmpItemPositionsInRows[0].addLast(paramInt2);
    return this.mTmpItemPositionsInRows;
  }
  
  public final Grid.Location getLocation(int paramInt)
  {
    return this.mTmpLocation;
  }
  
  int getStartIndexForAppend()
  {
    if (this.mLastVisibleIndex >= 0) {
      return this.mLastVisibleIndex + 1;
    }
    if (this.mStartIndex != -1) {
      return Math.min(this.mStartIndex, this.mProvider.getCount() - 1);
    }
    return 0;
  }
  
  int getStartIndexForPrepend()
  {
    if (this.mFirstVisibleIndex >= 0) {
      return this.mFirstVisibleIndex - 1;
    }
    if (this.mStartIndex != -1) {
      return Math.min(this.mStartIndex, this.mProvider.getCount() - 1);
    }
    return this.mProvider.getCount() - 1;
  }
  
  protected final boolean prependVisibleItems(int paramInt, boolean paramBoolean)
  {
    if (this.mProvider.getCount() == 0) {}
    while ((!paramBoolean) && (checkPrependOverLimit(paramInt))) {
      return false;
    }
    boolean bool1 = false;
    int k = this.mProvider.getMinIndex();
    int j = getStartIndexForPrepend();
    for (;;)
    {
      int m;
      if (j >= k)
      {
        m = this.mProvider.createItem(j, false, this.mTmpItem, false);
        if ((this.mFirstVisibleIndex >= 0) && (this.mLastVisibleIndex >= 0)) {
          break label164;
        }
        if (!this.mReversedFlow) {
          break label158;
        }
      }
      boolean bool2;
      label158:
      for (int i = Integer.MIN_VALUE;; i = Integer.MAX_VALUE)
      {
        this.mFirstVisibleIndex = j;
        this.mLastVisibleIndex = j;
        this.mProvider.addItem(this.mTmpItem[0], j, m, 0, i);
        boolean bool3 = true;
        bool2 = true;
        bool1 = bool3;
        if (!paramBoolean)
        {
          if (!checkPrependOverLimit(paramInt)) {
            break;
          }
          bool1 = bool3;
        }
        return bool1;
      }
      label164:
      if (this.mReversedFlow) {}
      for (i = this.mProvider.getEdge(j + 1) + this.mSpacing + m;; i = this.mProvider.getEdge(j + 1) - this.mSpacing - m)
      {
        this.mFirstVisibleIndex = j;
        break;
      }
      j -= 1;
      bool1 = bool2;
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/SingleRow.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */