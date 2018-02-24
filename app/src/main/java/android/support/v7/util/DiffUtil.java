package android.support.v7.util;

import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v7.widget.RecyclerView.Adapter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class DiffUtil
{
  private static final Comparator<Snake> SNAKE_COMPARATOR = new Comparator()
  {
    public int compare(DiffUtil.Snake paramAnonymousSnake1, DiffUtil.Snake paramAnonymousSnake2)
    {
      int j = paramAnonymousSnake1.x - paramAnonymousSnake2.x;
      int i = j;
      if (j == 0) {
        i = paramAnonymousSnake1.y - paramAnonymousSnake2.y;
      }
      return i;
    }
  };
  
  public static DiffResult calculateDiff(Callback paramCallback)
  {
    return calculateDiff(paramCallback, true);
  }
  
  public static DiffResult calculateDiff(Callback paramCallback, boolean paramBoolean)
  {
    int i = paramCallback.getOldListSize();
    int j = paramCallback.getNewListSize();
    ArrayList localArrayList1 = new ArrayList();
    ArrayList localArrayList2 = new ArrayList();
    localArrayList2.add(new Range(0, i, 0, j));
    i = i + j + Math.abs(i - j);
    int[] arrayOfInt1 = new int[i * 2];
    int[] arrayOfInt2 = new int[i * 2];
    ArrayList localArrayList3 = new ArrayList();
    while (!localArrayList2.isEmpty())
    {
      Range localRange2 = (Range)localArrayList2.remove(localArrayList2.size() - 1);
      Snake localSnake = diffPartial(paramCallback, localRange2.oldListStart, localRange2.oldListEnd, localRange2.newListStart, localRange2.newListEnd, arrayOfInt1, arrayOfInt2, i);
      if (localSnake != null)
      {
        if (localSnake.size > 0) {
          localArrayList1.add(localSnake);
        }
        localSnake.x += localRange2.oldListStart;
        localSnake.y += localRange2.newListStart;
        Range localRange1;
        if (localArrayList3.isEmpty())
        {
          localRange1 = new Range();
          label217:
          localRange1.oldListStart = localRange2.oldListStart;
          localRange1.newListStart = localRange2.newListStart;
          if (!localSnake.reverse) {
            break label362;
          }
          localRange1.oldListEnd = localSnake.x;
          localRange1.newListEnd = localSnake.y;
          label265:
          localArrayList2.add(localRange1);
          if (!localSnake.reverse) {
            break label457;
          }
          if (!localSnake.removal) {
            break label420;
          }
          localRange2.oldListStart = (localSnake.x + localSnake.size + 1);
          localRange2.newListStart = (localSnake.y + localSnake.size);
        }
        for (;;)
        {
          localArrayList2.add(localRange2);
          break;
          localRange1 = (Range)localArrayList3.remove(localArrayList3.size() - 1);
          break label217;
          label362:
          if (localSnake.removal)
          {
            localRange1.oldListEnd = (localSnake.x - 1);
            localRange1.newListEnd = localSnake.y;
            break label265;
          }
          localRange1.oldListEnd = localSnake.x;
          localRange1.newListEnd = (localSnake.y - 1);
          break label265;
          label420:
          localRange2.oldListStart = (localSnake.x + localSnake.size);
          localRange2.newListStart = (localSnake.y + localSnake.size + 1);
          continue;
          label457:
          localRange2.oldListStart = (localSnake.x + localSnake.size);
          localRange2.newListStart = (localSnake.y + localSnake.size);
        }
      }
      localArrayList3.add(localRange2);
    }
    Collections.sort(localArrayList1, SNAKE_COMPARATOR);
    return new DiffResult(paramCallback, localArrayList1, arrayOfInt1, arrayOfInt2, paramBoolean);
  }
  
  private static Snake diffPartial(Callback paramCallback, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt5)
  {
    int m = paramInt2 - paramInt1;
    int n = paramInt4 - paramInt3;
    if ((paramInt2 - paramInt1 < 1) || (paramInt4 - paramInt3 < 1)) {
      return null;
    }
    int i1 = m - n;
    int i2 = (m + n + 1) / 2;
    Arrays.fill(paramArrayOfInt1, paramInt5 - i2 - 1, paramInt5 + i2 + 1, 0);
    Arrays.fill(paramArrayOfInt2, paramInt5 - i2 - 1 + i1, paramInt5 + i2 + 1 + i1, m);
    int i;
    if (i1 % 2 != 0)
    {
      paramInt4 = 1;
      i = 0;
    }
    for (;;)
    {
      if (i > i2) {
        break label658;
      }
      int j = -i;
      boolean bool;
      int k;
      for (;;)
      {
        if (j > i) {
          break label379;
        }
        if ((j == -i) || ((j != i) && (paramArrayOfInt1[(paramInt5 + j - 1)] < paramArrayOfInt1[(paramInt5 + j + 1)]))) {
          paramInt2 = paramArrayOfInt1[(paramInt5 + j + 1)];
        }
        for (bool = false;; bool = true)
        {
          k = paramInt2 - j;
          while ((paramInt2 < m) && (k < n) && (paramCallback.areItemsTheSame(paramInt1 + paramInt2, paramInt3 + k)))
          {
            paramInt2 += 1;
            k += 1;
          }
          paramInt4 = 0;
          break;
          paramInt2 = paramArrayOfInt1[(paramInt5 + j - 1)] + 1;
        }
        paramArrayOfInt1[(paramInt5 + j)] = paramInt2;
        if ((paramInt4 != 0) && (j >= i1 - i + 1) && (j <= i1 + i - 1) && (paramArrayOfInt1[(paramInt5 + j)] >= paramArrayOfInt2[(paramInt5 + j)]))
        {
          paramCallback = new Snake();
          paramCallback.x = paramArrayOfInt2[(paramInt5 + j)];
          paramCallback.y = (paramCallback.x - j);
          paramCallback.size = (paramArrayOfInt1[(paramInt5 + j)] - paramArrayOfInt2[(paramInt5 + j)]);
          paramCallback.removal = bool;
          paramCallback.reverse = false;
          return paramCallback;
        }
        j += 2;
      }
      label379:
      j = -i;
      while (j <= i)
      {
        int i3 = j + i1;
        if ((i3 == i + i1) || ((i3 != -i + i1) && (paramArrayOfInt2[(paramInt5 + i3 - 1)] < paramArrayOfInt2[(paramInt5 + i3 + 1)]))) {
          paramInt2 = paramArrayOfInt2[(paramInt5 + i3 - 1)];
        }
        for (bool = false;; bool = true)
        {
          k = paramInt2 - i3;
          while ((paramInt2 > 0) && (k > 0) && (paramCallback.areItemsTheSame(paramInt1 + paramInt2 - 1, paramInt3 + k - 1)))
          {
            paramInt2 -= 1;
            k -= 1;
          }
          paramInt2 = paramArrayOfInt2[(paramInt5 + i3 + 1)] - 1;
        }
        paramArrayOfInt2[(paramInt5 + i3)] = paramInt2;
        if ((paramInt4 == 0) && (j + i1 >= -i) && (j + i1 <= i) && (paramArrayOfInt1[(paramInt5 + i3)] >= paramArrayOfInt2[(paramInt5 + i3)]))
        {
          paramCallback = new Snake();
          paramCallback.x = paramArrayOfInt2[(paramInt5 + i3)];
          paramCallback.y = (paramCallback.x - i3);
          paramCallback.size = (paramArrayOfInt1[(paramInt5 + i3)] - paramArrayOfInt2[(paramInt5 + i3)]);
          paramCallback.removal = bool;
          paramCallback.reverse = true;
          return paramCallback;
        }
        j += 2;
      }
      i += 1;
    }
    label658:
    throw new IllegalStateException("DiffUtil hit an unexpected case while trying to calculate the optimal path. Please make sure your data is not changing during the diff calculation.");
  }
  
  public static abstract class Callback
  {
    public abstract boolean areContentsTheSame(int paramInt1, int paramInt2);
    
    public abstract boolean areItemsTheSame(int paramInt1, int paramInt2);
    
    @Nullable
    public Object getChangePayload(int paramInt1, int paramInt2)
    {
      return null;
    }
    
    public abstract int getNewListSize();
    
    public abstract int getOldListSize();
  }
  
  public static class DiffResult
  {
    private static final int FLAG_CHANGED = 2;
    private static final int FLAG_IGNORE = 16;
    private static final int FLAG_MASK = 31;
    private static final int FLAG_MOVED_CHANGED = 4;
    private static final int FLAG_MOVED_NOT_CHANGED = 8;
    private static final int FLAG_NOT_CHANGED = 1;
    private static final int FLAG_OFFSET = 5;
    private final DiffUtil.Callback mCallback;
    private final boolean mDetectMoves;
    private final int[] mNewItemStatuses;
    private final int mNewListSize;
    private final int[] mOldItemStatuses;
    private final int mOldListSize;
    private final List<DiffUtil.Snake> mSnakes;
    
    DiffResult(DiffUtil.Callback paramCallback, List<DiffUtil.Snake> paramList, int[] paramArrayOfInt1, int[] paramArrayOfInt2, boolean paramBoolean)
    {
      this.mSnakes = paramList;
      this.mOldItemStatuses = paramArrayOfInt1;
      this.mNewItemStatuses = paramArrayOfInt2;
      Arrays.fill(this.mOldItemStatuses, 0);
      Arrays.fill(this.mNewItemStatuses, 0);
      this.mCallback = paramCallback;
      this.mOldListSize = paramCallback.getOldListSize();
      this.mNewListSize = paramCallback.getNewListSize();
      this.mDetectMoves = paramBoolean;
      addRootSnake();
      findMatchingItems();
    }
    
    private void addRootSnake()
    {
      if (this.mSnakes.isEmpty()) {}
      for (DiffUtil.Snake localSnake = null;; localSnake = (DiffUtil.Snake)this.mSnakes.get(0))
      {
        if ((localSnake == null) || (localSnake.x != 0) || (localSnake.y != 0))
        {
          localSnake = new DiffUtil.Snake();
          localSnake.x = 0;
          localSnake.y = 0;
          localSnake.removal = false;
          localSnake.size = 0;
          localSnake.reverse = false;
          this.mSnakes.add(0, localSnake);
        }
        return;
      }
    }
    
    private void dispatchAdditions(List<DiffUtil.PostponedUpdate> paramList, ListUpdateCallback paramListUpdateCallback, int paramInt1, int paramInt2, int paramInt3)
    {
      if (!this.mDetectMoves) {
        paramListUpdateCallback.onInserted(paramInt1, paramInt2);
      }
      do
      {
        return;
        paramInt2 -= 1;
      } while (paramInt2 < 0);
      int i = this.mNewItemStatuses[(paramInt3 + paramInt2)] & 0x1F;
      Iterator localIterator;
      switch (i)
      {
      default: 
        throw new IllegalStateException("unknown flag for pos " + (paramInt3 + paramInt2) + " " + Long.toBinaryString(i));
      case 0: 
        paramListUpdateCallback.onInserted(paramInt1, 1);
        localIterator = paramList.iterator();
      case 4: 
      case 8: 
        while (localIterator.hasNext())
        {
          DiffUtil.PostponedUpdate localPostponedUpdate = (DiffUtil.PostponedUpdate)localIterator.next();
          localPostponedUpdate.currentPos += 1;
          continue;
          int j = this.mNewItemStatuses[(paramInt3 + paramInt2)] >> 5;
          paramListUpdateCallback.onMoved(removePostponedUpdate(paramList, j, true).currentPos, paramInt1);
          if (i == 4) {
            paramListUpdateCallback.onChanged(paramInt1, 1, this.mCallback.getChangePayload(j, paramInt3 + paramInt2));
          }
        }
      }
      for (;;)
      {
        paramInt2 -= 1;
        break;
        paramList.add(new DiffUtil.PostponedUpdate(paramInt3 + paramInt2, paramInt1, false));
      }
    }
    
    private void dispatchRemovals(List<DiffUtil.PostponedUpdate> paramList, ListUpdateCallback paramListUpdateCallback, int paramInt1, int paramInt2, int paramInt3)
    {
      if (!this.mDetectMoves) {
        paramListUpdateCallback.onRemoved(paramInt1, paramInt2);
      }
      do
      {
        return;
        paramInt2 -= 1;
      } while (paramInt2 < 0);
      int i = this.mOldItemStatuses[(paramInt3 + paramInt2)] & 0x1F;
      Object localObject;
      switch (i)
      {
      default: 
        throw new IllegalStateException("unknown flag for pos " + (paramInt3 + paramInt2) + " " + Long.toBinaryString(i));
      case 0: 
        paramListUpdateCallback.onRemoved(paramInt1 + paramInt2, 1);
        localObject = paramList.iterator();
      case 4: 
      case 8: 
        while (((Iterator)localObject).hasNext())
        {
          DiffUtil.PostponedUpdate localPostponedUpdate = (DiffUtil.PostponedUpdate)((Iterator)localObject).next();
          localPostponedUpdate.currentPos -= 1;
          continue;
          int j = this.mOldItemStatuses[(paramInt3 + paramInt2)] >> 5;
          localObject = removePostponedUpdate(paramList, j, false);
          paramListUpdateCallback.onMoved(paramInt1 + paramInt2, ((DiffUtil.PostponedUpdate)localObject).currentPos - 1);
          if (i == 4) {
            paramListUpdateCallback.onChanged(((DiffUtil.PostponedUpdate)localObject).currentPos - 1, 1, this.mCallback.getChangePayload(paramInt3 + paramInt2, j));
          }
        }
      }
      for (;;)
      {
        paramInt2 -= 1;
        break;
        paramList.add(new DiffUtil.PostponedUpdate(paramInt3 + paramInt2, paramInt1 + paramInt2, true));
      }
    }
    
    private void findAddition(int paramInt1, int paramInt2, int paramInt3)
    {
      if (this.mOldItemStatuses[(paramInt1 - 1)] != 0) {
        return;
      }
      findMatchingItem(paramInt1, paramInt2, paramInt3, false);
    }
    
    private boolean findMatchingItem(int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean)
    {
      int j;
      int k;
      int i;
      if (paramBoolean)
      {
        j = paramInt2 - 1;
        k = paramInt1;
        i = paramInt2 - 1;
        paramInt2 = k;
      }
      while (paramInt3 >= 0)
      {
        DiffUtil.Snake localSnake = (DiffUtil.Snake)this.mSnakes.get(paramInt3);
        k = localSnake.x;
        int m = localSnake.size;
        int n = localSnake.y;
        int i1 = localSnake.size;
        if (paramBoolean)
        {
          paramInt2 -= 1;
          for (;;)
          {
            if (paramInt2 < k + m) {
              break label257;
            }
            if (this.mCallback.areItemsTheSame(paramInt2, j))
            {
              if (this.mCallback.areContentsTheSame(paramInt2, j)) {}
              for (paramInt1 = 8;; paramInt1 = 4)
              {
                this.mNewItemStatuses[j] = (paramInt2 << 5 | 0x10);
                this.mOldItemStatuses[paramInt2] = (j << 5 | paramInt1);
                return true;
                j = paramInt1 - 1;
                k = paramInt1 - 1;
                i = paramInt2;
                paramInt2 = k;
                break;
              }
            }
            paramInt2 -= 1;
          }
        }
        paramInt2 = i - 1;
        while (paramInt2 >= n + i1)
        {
          if (this.mCallback.areItemsTheSame(j, paramInt2))
          {
            if (this.mCallback.areContentsTheSame(j, paramInt2)) {}
            for (paramInt3 = 8;; paramInt3 = 4)
            {
              this.mOldItemStatuses[(paramInt1 - 1)] = (paramInt2 << 5 | 0x10);
              this.mNewItemStatuses[paramInt2] = (paramInt1 - 1 << 5 | paramInt3);
              return true;
            }
          }
          paramInt2 -= 1;
        }
        label257:
        paramInt2 = localSnake.x;
        i = localSnake.y;
        paramInt3 -= 1;
      }
      return false;
    }
    
    private void findMatchingItems()
    {
      int j = this.mOldListSize;
      int i = this.mNewListSize;
      int k = this.mSnakes.size() - 1;
      while (k >= 0)
      {
        DiffUtil.Snake localSnake = (DiffUtil.Snake)this.mSnakes.get(k);
        int i2 = localSnake.x;
        int i3 = localSnake.size;
        int n = localSnake.y;
        int i1 = localSnake.size;
        int m;
        if (this.mDetectMoves)
        {
          for (;;)
          {
            m = i;
            if (j <= i2 + i3) {
              break;
            }
            findAddition(j, i, k);
            j -= 1;
          }
          while (m > n + i1)
          {
            findRemoval(j, m, k);
            m -= 1;
          }
        }
        i = 0;
        if (i < localSnake.size)
        {
          m = localSnake.x + i;
          n = localSnake.y + i;
          if (this.mCallback.areContentsTheSame(m, n)) {}
          for (j = 1;; j = 2)
          {
            this.mOldItemStatuses[m] = (n << 5 | j);
            this.mNewItemStatuses[n] = (m << 5 | j);
            i += 1;
            break;
          }
        }
        j = localSnake.x;
        i = localSnake.y;
        k -= 1;
      }
    }
    
    private void findRemoval(int paramInt1, int paramInt2, int paramInt3)
    {
      if (this.mNewItemStatuses[(paramInt2 - 1)] != 0) {
        return;
      }
      findMatchingItem(paramInt1, paramInt2, paramInt3, true);
    }
    
    private static DiffUtil.PostponedUpdate removePostponedUpdate(List<DiffUtil.PostponedUpdate> paramList, int paramInt, boolean paramBoolean)
    {
      int i = paramList.size() - 1;
      while (i >= 0)
      {
        DiffUtil.PostponedUpdate localPostponedUpdate2 = (DiffUtil.PostponedUpdate)paramList.get(i);
        if ((localPostponedUpdate2.posInOwnerList == paramInt) && (localPostponedUpdate2.removal == paramBoolean))
        {
          paramList.remove(i);
          paramInt = i;
          localPostponedUpdate1 = localPostponedUpdate2;
          if (paramInt >= paramList.size()) {
            break label123;
          }
          localPostponedUpdate1 = (DiffUtil.PostponedUpdate)paramList.get(paramInt);
          int j = localPostponedUpdate1.currentPos;
          if (paramBoolean) {}
          for (i = 1;; i = -1)
          {
            localPostponedUpdate1.currentPos = (i + j);
            paramInt += 1;
            break;
          }
        }
        i -= 1;
      }
      DiffUtil.PostponedUpdate localPostponedUpdate1 = null;
      label123:
      return localPostponedUpdate1;
    }
    
    public void dispatchUpdatesTo(ListUpdateCallback paramListUpdateCallback)
    {
      ArrayList localArrayList;
      int k;
      int j;
      int i;
      if ((paramListUpdateCallback instanceof BatchingListUpdateCallback))
      {
        paramListUpdateCallback = (BatchingListUpdateCallback)paramListUpdateCallback;
        localArrayList = new ArrayList();
        k = this.mOldListSize;
        j = this.mNewListSize;
        i = this.mSnakes.size() - 1;
      }
      for (;;)
      {
        if (i < 0) {
          break label234;
        }
        DiffUtil.Snake localSnake = (DiffUtil.Snake)this.mSnakes.get(i);
        int m = localSnake.size;
        int n = localSnake.x + m;
        int i1 = localSnake.y + m;
        if (n < k) {
          dispatchRemovals(localArrayList, paramListUpdateCallback, n, k - n, n);
        }
        if (i1 < j) {
          dispatchAdditions(localArrayList, paramListUpdateCallback, n, j - i1, i1);
        }
        j = m - 1;
        for (;;)
        {
          if (j >= 0)
          {
            if ((this.mOldItemStatuses[(localSnake.x + j)] & 0x1F) == 2) {
              paramListUpdateCallback.onChanged(localSnake.x + j, 1, this.mCallback.getChangePayload(localSnake.x + j, localSnake.y + j));
            }
            j -= 1;
            continue;
            paramListUpdateCallback = new BatchingListUpdateCallback(paramListUpdateCallback);
            break;
          }
        }
        k = localSnake.x;
        j = localSnake.y;
        i -= 1;
      }
      label234:
      paramListUpdateCallback.dispatchLastEvent();
    }
    
    public void dispatchUpdatesTo(final RecyclerView.Adapter paramAdapter)
    {
      dispatchUpdatesTo(new ListUpdateCallback()
      {
        public void onChanged(int paramAnonymousInt1, int paramAnonymousInt2, Object paramAnonymousObject)
        {
          paramAdapter.notifyItemRangeChanged(paramAnonymousInt1, paramAnonymousInt2, paramAnonymousObject);
        }
        
        public void onInserted(int paramAnonymousInt1, int paramAnonymousInt2)
        {
          paramAdapter.notifyItemRangeInserted(paramAnonymousInt1, paramAnonymousInt2);
        }
        
        public void onMoved(int paramAnonymousInt1, int paramAnonymousInt2)
        {
          paramAdapter.notifyItemMoved(paramAnonymousInt1, paramAnonymousInt2);
        }
        
        public void onRemoved(int paramAnonymousInt1, int paramAnonymousInt2)
        {
          paramAdapter.notifyItemRangeRemoved(paramAnonymousInt1, paramAnonymousInt2);
        }
      });
    }
    
    @VisibleForTesting
    List<DiffUtil.Snake> getSnakes()
    {
      return this.mSnakes;
    }
  }
  
  private static class PostponedUpdate
  {
    int currentPos;
    int posInOwnerList;
    boolean removal;
    
    public PostponedUpdate(int paramInt1, int paramInt2, boolean paramBoolean)
    {
      this.posInOwnerList = paramInt1;
      this.currentPos = paramInt2;
      this.removal = paramBoolean;
    }
  }
  
  static class Range
  {
    int newListEnd;
    int newListStart;
    int oldListEnd;
    int oldListStart;
    
    public Range() {}
    
    public Range(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      this.oldListStart = paramInt1;
      this.oldListEnd = paramInt2;
      this.newListStart = paramInt3;
      this.newListEnd = paramInt4;
    }
  }
  
  static class Snake
  {
    boolean removal;
    boolean reverse;
    int size;
    int x;
    int y;
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v7/util/DiffUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */