package android.support.v17.leanback.widget;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.CircularIntArray;
import android.support.v7.widget.RecyclerView.LayoutManager.LayoutPrefetchRegistry;
import android.util.SparseIntArray;
import java.io.PrintWriter;
import java.util.Arrays;

abstract class Grid
{
  public static final int START_DEFAULT = -1;
  protected int mFirstVisibleIndex = -1;
  protected int mLastVisibleIndex = -1;
  protected int mNumRows;
  protected Provider mProvider;
  protected boolean mReversedFlow;
  protected int mSpacing;
  protected int mStartIndex = -1;
  Object[] mTmpItem = new Object[1];
  protected CircularIntArray[] mTmpItemPositionsInRows;
  
  public static Grid createGrid(int paramInt)
  {
    if (paramInt == 1) {
      return new SingleRow();
    }
    StaggeredGridDefault localStaggeredGridDefault = new StaggeredGridDefault();
    localStaggeredGridDefault.setNumRows(paramInt);
    return localStaggeredGridDefault;
  }
  
  private void resetVisibleIndexIfEmpty()
  {
    if (this.mLastVisibleIndex < this.mFirstVisibleIndex) {
      resetVisibleIndex();
    }
  }
  
  public boolean appendOneColumnVisibleItems()
  {
    if (this.mReversedFlow) {}
    for (int i = Integer.MAX_VALUE;; i = Integer.MIN_VALUE) {
      return appendVisibleItems(i, true);
    }
  }
  
  public final void appendVisibleItems(int paramInt)
  {
    appendVisibleItems(paramInt, false);
  }
  
  protected abstract boolean appendVisibleItems(int paramInt, boolean paramBoolean);
  
  protected final boolean checkAppendOverLimit(int paramInt)
  {
    boolean bool = true;
    if (this.mLastVisibleIndex < 0) {
      return false;
    }
    if (this.mReversedFlow) {
      if (findRowMin(true, null) > this.mSpacing + paramInt) {}
    }
    for (;;)
    {
      return bool;
      bool = false;
      continue;
      if (findRowMax(false, null) < paramInt - this.mSpacing) {
        bool = false;
      }
    }
  }
  
  protected final boolean checkPrependOverLimit(int paramInt)
  {
    boolean bool = true;
    if (this.mLastVisibleIndex < 0) {
      return false;
    }
    if (this.mReversedFlow) {
      if (findRowMax(false, null) < paramInt - this.mSpacing) {}
    }
    for (;;)
    {
      return bool;
      bool = false;
      continue;
      if (findRowMin(true, null) > this.mSpacing + paramInt) {
        bool = false;
      }
    }
  }
  
  public void collectAdjacentPrefetchPositions(int paramInt1, int paramInt2, @NonNull RecyclerView.LayoutManager.LayoutPrefetchRegistry paramLayoutPrefetchRegistry) {}
  
  public abstract void debugPrint(PrintWriter paramPrintWriter);
  
  public void fillDisappearingItems(int[] paramArrayOfInt, int paramInt, SparseIntArray paramSparseIntArray)
  {
    int j = getLastVisibleIndex();
    int k;
    label68:
    label74:
    int m;
    if (j >= 0)
    {
      i = Arrays.binarySearch(paramArrayOfInt, 0, paramInt, j);
      if (i >= 0) {
        break label230;
      }
      k = -i;
      if (!this.mReversedFlow) {
        break label182;
      }
      i = this.mProvider.getEdge(j) - this.mProvider.getSize(j) - this.mSpacing;
      j = k - 1;
      if (j >= paramInt) {
        break label230;
      }
      int n = paramArrayOfInt[j];
      m = paramSparseIntArray.get(n);
      k = m;
      if (m < 0) {
        k = 0;
      }
      m = this.mProvider.createItem(n, true, this.mTmpItem, true);
      this.mProvider.addItem(this.mTmpItem[0], n, m, k, i);
      if (!this.mReversedFlow) {
        break label215;
      }
    }
    label182:
    label215:
    for (int i = i - m - this.mSpacing;; i = i + m + this.mSpacing)
    {
      j += 1;
      break label74;
      i = 0;
      break;
      i = this.mProvider.getEdge(j) + this.mProvider.getSize(j) + this.mSpacing;
      break label68;
    }
    label230:
    i = getFirstVisibleIndex();
    if (i >= 0)
    {
      paramInt = Arrays.binarySearch(paramArrayOfInt, 0, paramInt, i);
      if (paramInt >= 0) {
        return;
      }
      j = -paramInt;
      if (!this.mReversedFlow) {
        break label386;
      }
      paramInt = this.mProvider.getEdge(i);
      label277:
      i = j - 2;
      label283:
      if (i < 0) {
        return;
      }
      m = paramArrayOfInt[i];
      k = paramSparseIntArray.get(m);
      j = k;
      if (k < 0) {
        j = 0;
      }
      k = this.mProvider.createItem(m, false, this.mTmpItem, true);
      if (!this.mReversedFlow) {
        break label401;
      }
    }
    label386:
    label401:
    for (paramInt = this.mSpacing + paramInt + k;; paramInt = paramInt - this.mSpacing - k)
    {
      this.mProvider.addItem(this.mTmpItem[0], m, k, j, paramInt);
      i -= 1;
      break label283;
      paramInt = 0;
      break;
      paramInt = this.mProvider.getEdge(i);
      break label277;
    }
  }
  
  protected abstract int findRowMax(boolean paramBoolean, int paramInt, int[] paramArrayOfInt);
  
  public final int findRowMax(boolean paramBoolean, @Nullable int[] paramArrayOfInt)
  {
    if (this.mReversedFlow) {}
    for (int i = this.mFirstVisibleIndex;; i = this.mLastVisibleIndex) {
      return findRowMax(paramBoolean, i, paramArrayOfInt);
    }
  }
  
  protected abstract int findRowMin(boolean paramBoolean, int paramInt, int[] paramArrayOfInt);
  
  public final int findRowMin(boolean paramBoolean, @Nullable int[] paramArrayOfInt)
  {
    if (this.mReversedFlow) {}
    for (int i = this.mLastVisibleIndex;; i = this.mFirstVisibleIndex) {
      return findRowMin(paramBoolean, i, paramArrayOfInt);
    }
  }
  
  public final int getFirstVisibleIndex()
  {
    return this.mFirstVisibleIndex;
  }
  
  public final CircularIntArray[] getItemPositionsInRows()
  {
    return getItemPositionsInRows(getFirstVisibleIndex(), getLastVisibleIndex());
  }
  
  public abstract CircularIntArray[] getItemPositionsInRows(int paramInt1, int paramInt2);
  
  public final int getLastVisibleIndex()
  {
    return this.mLastVisibleIndex;
  }
  
  public abstract Location getLocation(int paramInt);
  
  public int getNumRows()
  {
    return this.mNumRows;
  }
  
  public final int getRowIndex(int paramInt)
  {
    Location localLocation = getLocation(paramInt);
    if (localLocation == null) {
      return -1;
    }
    return localLocation.row;
  }
  
  public void invalidateItemsAfter(int paramInt)
  {
    if (paramInt < 0) {}
    do
    {
      do
      {
        return;
      } while (this.mLastVisibleIndex < 0);
      if (this.mLastVisibleIndex >= paramInt) {
        this.mLastVisibleIndex = (paramInt - 1);
      }
      resetVisibleIndexIfEmpty();
    } while (getFirstVisibleIndex() >= 0);
    setStart(paramInt);
  }
  
  public boolean isReversedFlow()
  {
    return this.mReversedFlow;
  }
  
  public final boolean prependOneColumnVisibleItems()
  {
    if (this.mReversedFlow) {}
    for (int i = Integer.MIN_VALUE;; i = Integer.MAX_VALUE) {
      return prependVisibleItems(i, true);
    }
  }
  
  public final void prependVisibleItems(int paramInt)
  {
    prependVisibleItems(paramInt, false);
  }
  
  protected abstract boolean prependVisibleItems(int paramInt, boolean paramBoolean);
  
  public void removeInvisibleItemsAtEnd(int paramInt1, int paramInt2)
  {
    if ((this.mLastVisibleIndex >= this.mFirstVisibleIndex) && (this.mLastVisibleIndex > paramInt1))
    {
      int i;
      if (!this.mReversedFlow) {
        if (this.mProvider.getEdge(this.mLastVisibleIndex) >= paramInt2) {
          i = 1;
        }
      }
      for (;;)
      {
        if (i == 0) {
          break label107;
        }
        this.mProvider.removeItem(this.mLastVisibleIndex);
        this.mLastVisibleIndex -= 1;
        break;
        i = 0;
        continue;
        if (this.mProvider.getEdge(this.mLastVisibleIndex) <= paramInt2) {
          i = 1;
        } else {
          i = 0;
        }
      }
    }
    label107:
    resetVisibleIndexIfEmpty();
  }
  
  public void removeInvisibleItemsAtFront(int paramInt1, int paramInt2)
  {
    if ((this.mLastVisibleIndex >= this.mFirstVisibleIndex) && (this.mFirstVisibleIndex < paramInt1))
    {
      int i = this.mProvider.getSize(this.mFirstVisibleIndex);
      if (!this.mReversedFlow) {
        if (this.mProvider.getEdge(this.mFirstVisibleIndex) + i <= paramInt2) {
          i = 1;
        }
      }
      for (;;)
      {
        if (i == 0) {
          break label125;
        }
        this.mProvider.removeItem(this.mFirstVisibleIndex);
        this.mFirstVisibleIndex += 1;
        break;
        i = 0;
        continue;
        if (this.mProvider.getEdge(this.mFirstVisibleIndex) - i >= paramInt2) {
          i = 1;
        } else {
          i = 0;
        }
      }
    }
    label125:
    resetVisibleIndexIfEmpty();
  }
  
  public void resetVisibleIndex()
  {
    this.mLastVisibleIndex = -1;
    this.mFirstVisibleIndex = -1;
  }
  
  void setNumRows(int paramInt)
  {
    if (paramInt <= 0) {
      throw new IllegalArgumentException();
    }
    if (this.mNumRows == paramInt) {}
    for (;;)
    {
      return;
      this.mNumRows = paramInt;
      this.mTmpItemPositionsInRows = new CircularIntArray[this.mNumRows];
      paramInt = 0;
      while (paramInt < this.mNumRows)
      {
        this.mTmpItemPositionsInRows[paramInt] = new CircularIntArray();
        paramInt += 1;
      }
    }
  }
  
  public void setProvider(Provider paramProvider)
  {
    this.mProvider = paramProvider;
  }
  
  public final void setReversedFlow(boolean paramBoolean)
  {
    this.mReversedFlow = paramBoolean;
  }
  
  public final void setSpacing(int paramInt)
  {
    this.mSpacing = paramInt;
  }
  
  public void setStart(int paramInt)
  {
    this.mStartIndex = paramInt;
  }
  
  public static class Location
  {
    public int row;
    
    public Location(int paramInt)
    {
      this.row = paramInt;
    }
  }
  
  public static abstract interface Provider
  {
    public abstract void addItem(Object paramObject, int paramInt1, int paramInt2, int paramInt3, int paramInt4);
    
    public abstract int createItem(int paramInt, boolean paramBoolean1, Object[] paramArrayOfObject, boolean paramBoolean2);
    
    public abstract int getCount();
    
    public abstract int getEdge(int paramInt);
    
    public abstract int getMinIndex();
    
    public abstract int getSize(int paramInt);
    
    public abstract void removeItem(int paramInt);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/Grid.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */