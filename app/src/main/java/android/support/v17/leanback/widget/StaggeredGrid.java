package android.support.v17.leanback.widget;

import android.support.v4.util.CircularArray;
import android.support.v4.util.CircularIntArray;
import java.io.PrintWriter;

abstract class StaggeredGrid
  extends Grid
{
  protected int mFirstIndex = -1;
  protected CircularArray<Location> mLocations = new CircularArray(64);
  protected Object mPendingItem;
  protected int mPendingItemSize;
  
  private int calculateOffsetAfterLastItem(int paramInt)
  {
    int i = getLastIndex();
    int k = 0;
    int j = k;
    if (i >= this.mFirstIndex)
    {
      if (getLocation(i).row == paramInt) {
        j = 1;
      }
    }
    else
    {
      if (j == 0) {
        i = getLastIndex();
      }
      if (!isReversedFlow()) {
        break label105;
      }
    }
    label105:
    for (paramInt = -getLocation(i).size - this.mSpacing;; paramInt = getLocation(i).size + this.mSpacing)
    {
      j = i + 1;
      i = paramInt;
      paramInt = j;
      while (paramInt <= getLastIndex())
      {
        i -= getLocation(paramInt).offset;
        paramInt += 1;
      }
      i -= 1;
      break;
    }
    return i;
  }
  
  protected final boolean appendVisbleItemsWithCache(int paramInt, boolean paramBoolean)
  {
    if (this.mLocations.size() == 0) {}
    label331:
    for (;;)
    {
      return false;
      int i1 = this.mProvider.getCount();
      int i;
      int j;
      int m;
      int k;
      if (this.mLastVisibleIndex >= 0)
      {
        i = this.mLastVisibleIndex + 1;
        j = this.mProvider.getEdge(this.mLastVisibleIndex);
        m = getLastIndex();
        k = j;
      }
      for (;;)
      {
        if ((i >= i1) || (i > m)) {
          break label331;
        }
        Location localLocation = getLocation(i);
        j = k;
        if (k != Integer.MAX_VALUE) {
          j = k + localLocation.offset;
        }
        int i2 = localLocation.row;
        k = this.mProvider.createItem(i, true, this.mTmpItem, false);
        int n = m;
        if (k != localLocation.size)
        {
          localLocation.size = k;
          this.mLocations.removeFromEnd(m - i);
          n = i;
        }
        this.mLastVisibleIndex = i;
        if (this.mFirstVisibleIndex < 0) {
          this.mFirstVisibleIndex = i;
        }
        this.mProvider.addItem(this.mTmpItem[0], i, k, i2, j);
        if ((!paramBoolean) && (checkAppendOverLimit(paramInt)))
        {
          return true;
          j = Integer.MAX_VALUE;
          if (this.mStartIndex != -1) {}
          for (k = this.mStartIndex; (k > getLastIndex() + 1) || (k < getFirstIndex()); k = 0)
          {
            this.mLocations.clear();
            return false;
          }
          i = k;
          if (k <= getLastIndex()) {
            break;
          }
          return false;
        }
        k = j;
        if (j == Integer.MAX_VALUE) {
          k = this.mProvider.getEdge(i);
        }
        if ((i2 == this.mNumRows - 1) && (paramBoolean)) {
          return true;
        }
        i += 1;
        m = n;
      }
    }
  }
  
  protected final int appendVisibleItemToRow(int paramInt1, int paramInt2, int paramInt3)
  {
    if ((this.mLastVisibleIndex >= 0) && ((this.mLastVisibleIndex != getLastIndex()) || (this.mLastVisibleIndex != paramInt1 - 1))) {
      throw new IllegalStateException();
    }
    int i;
    Location localLocation;
    Object localObject;
    if (this.mLastVisibleIndex < 0) {
      if ((this.mLocations.size() > 0) && (paramInt1 == getLastIndex() + 1))
      {
        i = calculateOffsetAfterLastItem(paramInt2);
        localLocation = new Location(paramInt2, i, 0);
        this.mLocations.addLast(localLocation);
        if (this.mPendingItem == null) {
          break label196;
        }
        localLocation.size = this.mPendingItemSize;
        localObject = this.mPendingItem;
        this.mPendingItem = null;
        label119:
        if (this.mLocations.size() != 1) {
          break label228;
        }
        this.mLastVisibleIndex = paramInt1;
        this.mFirstVisibleIndex = paramInt1;
        this.mFirstIndex = paramInt1;
      }
    }
    for (;;)
    {
      this.mProvider.addItem(localObject, paramInt1, localLocation.size, paramInt2, paramInt3);
      return localLocation.size;
      i = 0;
      break;
      i = paramInt3 - this.mProvider.getEdge(this.mLastVisibleIndex);
      break;
      label196:
      localLocation.size = this.mProvider.createItem(paramInt1, true, this.mTmpItem, false);
      localObject = this.mTmpItem[0];
      break label119;
      label228:
      if (this.mLastVisibleIndex < 0)
      {
        this.mLastVisibleIndex = paramInt1;
        this.mFirstVisibleIndex = paramInt1;
      }
      else
      {
        this.mLastVisibleIndex += 1;
      }
    }
  }
  
  /* Error */
  protected final boolean appendVisibleItems(int paramInt, boolean paramBoolean)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 63	android/support/v17/leanback/widget/StaggeredGrid:mProvider	Landroid/support/v17/leanback/widget/Grid$Provider;
    //   4: invokeinterface 68 1 0
    //   9: ifne +5 -> 14
    //   12: iconst_0
    //   13: ireturn
    //   14: iload_2
    //   15: ifne +11 -> 26
    //   18: aload_0
    //   19: iload_1
    //   20: invokevirtual 97	android/support/v17/leanback/widget/StaggeredGrid:checkAppendOverLimit	(I)Z
    //   23: ifne -11 -> 12
    //   26: aload_0
    //   27: iload_1
    //   28: iload_2
    //   29: invokevirtual 130	android/support/v17/leanback/widget/StaggeredGrid:appendVisbleItemsWithCache	(IZ)Z
    //   32: istore_3
    //   33: iload_3
    //   34: ifeq +17 -> 51
    //   37: aload_0
    //   38: getfield 79	android/support/v17/leanback/widget/StaggeredGrid:mTmpItem	[Ljava/lang/Object;
    //   41: iconst_0
    //   42: aconst_null
    //   43: aastore
    //   44: aload_0
    //   45: aconst_null
    //   46: putfield 125	android/support/v17/leanback/widget/StaggeredGrid:mPendingItem	Ljava/lang/Object;
    //   49: iconst_1
    //   50: ireturn
    //   51: aload_0
    //   52: iload_1
    //   53: iload_2
    //   54: invokevirtual 133	android/support/v17/leanback/widget/StaggeredGrid:appendVisibleItemsWithoutCache	(IZ)Z
    //   57: istore_2
    //   58: aload_0
    //   59: getfield 79	android/support/v17/leanback/widget/StaggeredGrid:mTmpItem	[Ljava/lang/Object;
    //   62: iconst_0
    //   63: aconst_null
    //   64: aastore
    //   65: aload_0
    //   66: aconst_null
    //   67: putfield 125	android/support/v17/leanback/widget/StaggeredGrid:mPendingItem	Ljava/lang/Object;
    //   70: iload_2
    //   71: ireturn
    //   72: astore 4
    //   74: aload_0
    //   75: getfield 79	android/support/v17/leanback/widget/StaggeredGrid:mTmpItem	[Ljava/lang/Object;
    //   78: iconst_0
    //   79: aconst_null
    //   80: aastore
    //   81: aload_0
    //   82: aconst_null
    //   83: putfield 125	android/support/v17/leanback/widget/StaggeredGrid:mPendingItem	Ljava/lang/Object;
    //   86: aload 4
    //   88: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	89	0	this	StaggeredGrid
    //   0	89	1	paramInt	int
    //   0	89	2	paramBoolean	boolean
    //   32	2	3	bool	boolean
    //   72	15	4	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   26	33	72	finally
    //   51	58	72	finally
  }
  
  protected abstract boolean appendVisibleItemsWithoutCache(int paramInt, boolean paramBoolean);
  
  public final void debugPrint(PrintWriter paramPrintWriter)
  {
    int i = 0;
    int j = this.mLocations.size();
    while (i < j)
    {
      Location localLocation = (Location)this.mLocations.get(i);
      paramPrintWriter.print("<" + (this.mFirstIndex + i) + "," + localLocation.row + ">");
      paramPrintWriter.print(" ");
      paramPrintWriter.println();
      i += 1;
    }
  }
  
  public final int getFirstIndex()
  {
    return this.mFirstIndex;
  }
  
  public final CircularIntArray[] getItemPositionsInRows(int paramInt1, int paramInt2)
  {
    int i = 0;
    while (i < this.mNumRows)
    {
      this.mTmpItemPositionsInRows[i].clear();
      i += 1;
    }
    if (paramInt1 >= 0) {
      if (paramInt1 <= paramInt2)
      {
        CircularIntArray localCircularIntArray = this.mTmpItemPositionsInRows[getLocation(paramInt1).row];
        if ((localCircularIntArray.size() > 0) && (localCircularIntArray.getLast() == paramInt1 - 1))
        {
          localCircularIntArray.popLast();
          localCircularIntArray.addLast(paramInt1);
        }
        for (;;)
        {
          paramInt1 += 1;
          break;
          localCircularIntArray.addLast(paramInt1);
          localCircularIntArray.addLast(paramInt1);
        }
      }
    }
    return this.mTmpItemPositionsInRows;
  }
  
  public final int getLastIndex()
  {
    return this.mFirstIndex + this.mLocations.size() - 1;
  }
  
  public final Location getLocation(int paramInt)
  {
    paramInt -= this.mFirstIndex;
    if ((paramInt < 0) || (paramInt >= this.mLocations.size())) {
      return null;
    }
    return (Location)this.mLocations.get(paramInt);
  }
  
  public final int getSize()
  {
    return this.mLocations.size();
  }
  
  public void invalidateItemsAfter(int paramInt)
  {
    super.invalidateItemsAfter(paramInt);
    this.mLocations.removeFromEnd(getLastIndex() - paramInt + 1);
    if (this.mLocations.size() == 0) {
      this.mFirstIndex = -1;
    }
  }
  
  protected final boolean prependVisbleItemsWithCache(int paramInt, boolean paramBoolean)
  {
    if (this.mLocations.size() == 0) {}
    label322:
    for (;;)
    {
      return false;
      int j;
      int k;
      int i;
      int m;
      if (this.mFirstVisibleIndex >= 0)
      {
        j = this.mProvider.getEdge(this.mFirstVisibleIndex);
        k = getLocation(this.mFirstVisibleIndex).offset;
        i = this.mFirstVisibleIndex - 1;
        m = Math.max(this.mProvider.getMinIndex(), this.mFirstIndex);
      }
      for (;;)
      {
        if (i < m) {
          break label322;
        }
        Location localLocation = getLocation(i);
        int n = localLocation.row;
        int i1 = this.mProvider.createItem(i, false, this.mTmpItem, false);
        if (i1 != localLocation.size)
        {
          this.mLocations.removeFromStart(i + 1 - this.mFirstIndex);
          this.mFirstIndex = this.mFirstVisibleIndex;
          this.mPendingItem = this.mTmpItem[0];
          this.mPendingItemSize = i1;
          return false;
          j = Integer.MAX_VALUE;
          k = 0;
          if (this.mStartIndex != -1) {}
          for (m = this.mStartIndex; (m > getLastIndex()) || (m < getFirstIndex() - 1); m = 0)
          {
            this.mLocations.clear();
            return false;
          }
          i = m;
          if (m >= getFirstIndex()) {
            break;
          }
          return false;
        }
        this.mFirstVisibleIndex = i;
        if (this.mLastVisibleIndex < 0) {
          this.mLastVisibleIndex = i;
        }
        this.mProvider.addItem(this.mTmpItem[0], i, i1, n, j - k);
        if ((!paramBoolean) && (checkPrependOverLimit(paramInt))) {
          return true;
        }
        j = this.mProvider.getEdge(i);
        k = localLocation.offset;
        if ((n == 0) && (paramBoolean)) {
          return true;
        }
        i -= 1;
      }
    }
  }
  
  protected final int prependVisibleItemToRow(int paramInt1, int paramInt2, int paramInt3)
  {
    if ((this.mFirstVisibleIndex >= 0) && ((this.mFirstVisibleIndex != getFirstIndex()) || (this.mFirstVisibleIndex != paramInt1 + 1))) {
      throw new IllegalStateException();
    }
    Location localLocation1;
    int i;
    Location localLocation2;
    Object localObject;
    if (this.mFirstIndex >= 0)
    {
      localLocation1 = getLocation(this.mFirstIndex);
      i = this.mProvider.getEdge(this.mFirstIndex);
      localLocation2 = new Location(paramInt2, 0, 0);
      this.mLocations.addFirst(localLocation2);
      if (this.mPendingItem == null) {
        break label198;
      }
      localLocation2.size = this.mPendingItemSize;
      localObject = this.mPendingItem;
      this.mPendingItem = null;
      label116:
      this.mFirstVisibleIndex = paramInt1;
      this.mFirstIndex = paramInt1;
      if (this.mLastVisibleIndex < 0) {
        this.mLastVisibleIndex = paramInt1;
      }
      if (this.mReversedFlow) {
        break label230;
      }
      paramInt3 -= localLocation2.size;
    }
    for (;;)
    {
      if (localLocation1 != null) {
        localLocation1.offset = (i - paramInt3);
      }
      this.mProvider.addItem(localObject, paramInt1, localLocation2.size, paramInt2, paramInt3);
      return localLocation2.size;
      localLocation1 = null;
      break;
      label198:
      localLocation2.size = this.mProvider.createItem(paramInt1, false, this.mTmpItem, false);
      localObject = this.mTmpItem[0];
      break label116;
      label230:
      paramInt3 += localLocation2.size;
    }
  }
  
  /* Error */
  protected final boolean prependVisibleItems(int paramInt, boolean paramBoolean)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 63	android/support/v17/leanback/widget/StaggeredGrid:mProvider	Landroid/support/v17/leanback/widget/Grid$Provider;
    //   4: invokeinterface 68 1 0
    //   9: ifne +5 -> 14
    //   12: iconst_0
    //   13: ireturn
    //   14: iload_2
    //   15: ifne +11 -> 26
    //   18: aload_0
    //   19: iload_1
    //   20: invokevirtual 209	android/support/v17/leanback/widget/StaggeredGrid:checkPrependOverLimit	(I)Z
    //   23: ifne -11 -> 12
    //   26: aload_0
    //   27: iload_1
    //   28: iload_2
    //   29: invokevirtual 220	android/support/v17/leanback/widget/StaggeredGrid:prependVisbleItemsWithCache	(IZ)Z
    //   32: istore_3
    //   33: iload_3
    //   34: ifeq +17 -> 51
    //   37: aload_0
    //   38: getfield 79	android/support/v17/leanback/widget/StaggeredGrid:mTmpItem	[Ljava/lang/Object;
    //   41: iconst_0
    //   42: aconst_null
    //   43: aastore
    //   44: aload_0
    //   45: aconst_null
    //   46: putfield 125	android/support/v17/leanback/widget/StaggeredGrid:mPendingItem	Ljava/lang/Object;
    //   49: iconst_1
    //   50: ireturn
    //   51: aload_0
    //   52: iload_1
    //   53: iload_2
    //   54: invokevirtual 223	android/support/v17/leanback/widget/StaggeredGrid:prependVisibleItemsWithoutCache	(IZ)Z
    //   57: istore_2
    //   58: aload_0
    //   59: getfield 79	android/support/v17/leanback/widget/StaggeredGrid:mTmpItem	[Ljava/lang/Object;
    //   62: iconst_0
    //   63: aconst_null
    //   64: aastore
    //   65: aload_0
    //   66: aconst_null
    //   67: putfield 125	android/support/v17/leanback/widget/StaggeredGrid:mPendingItem	Ljava/lang/Object;
    //   70: iload_2
    //   71: ireturn
    //   72: astore 4
    //   74: aload_0
    //   75: getfield 79	android/support/v17/leanback/widget/StaggeredGrid:mTmpItem	[Ljava/lang/Object;
    //   78: iconst_0
    //   79: aconst_null
    //   80: aastore
    //   81: aload_0
    //   82: aconst_null
    //   83: putfield 125	android/support/v17/leanback/widget/StaggeredGrid:mPendingItem	Ljava/lang/Object;
    //   86: aload 4
    //   88: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	89	0	this	StaggeredGrid
    //   0	89	1	paramInt	int
    //   0	89	2	paramBoolean	boolean
    //   32	2	3	bool	boolean
    //   72	15	4	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   26	33	72	finally
    //   51	58	72	finally
  }
  
  protected abstract boolean prependVisibleItemsWithoutCache(int paramInt, boolean paramBoolean);
  
  public static class Location
    extends Grid.Location
  {
    public int offset;
    public int size;
    
    public Location(int paramInt1, int paramInt2, int paramInt3)
    {
      super();
      this.offset = paramInt2;
      this.size = paramInt3;
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/StaggeredGrid.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */