package android.support.v7.widget;

import android.support.annotation.Nullable;
import android.support.v4.os.TraceCompat;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;

final class GapWorker
  implements Runnable
{
  static final ThreadLocal<GapWorker> sGapWorker = new ThreadLocal();
  static Comparator<Task> sTaskComparator = new Comparator()
  {
    public int compare(GapWorker.Task paramAnonymousTask1, GapWorker.Task paramAnonymousTask2)
    {
      int k = -1;
      int j;
      if (paramAnonymousTask1.view == null)
      {
        i = 1;
        if (paramAnonymousTask2.view != null) {
          break label42;
        }
        j = 1;
      }
      for (;;)
      {
        if (i != j)
        {
          if (paramAnonymousTask1.view == null)
          {
            return 1;
            i = 0;
            break;
            label42:
            j = 0;
            continue;
          }
          return -1;
        }
      }
      if (paramAnonymousTask1.immediate != paramAnonymousTask2.immediate)
      {
        if (paramAnonymousTask1.immediate) {}
        for (i = k;; i = 1) {
          return i;
        }
      }
      int i = paramAnonymousTask2.viewVelocity - paramAnonymousTask1.viewVelocity;
      if (i != 0) {
        return i;
      }
      i = paramAnonymousTask1.distanceToItem - paramAnonymousTask2.distanceToItem;
      if (i != 0) {
        return i;
      }
      return 0;
    }
  };
  long mFrameIntervalNs;
  long mPostTimeNs;
  ArrayList<RecyclerView> mRecyclerViews = new ArrayList();
  private ArrayList<Task> mTasks = new ArrayList();
  
  private void buildTaskList()
  {
    int n = this.mRecyclerViews.size();
    int j = 0;
    int i = 0;
    Object localObject;
    int k;
    while (i < n)
    {
      localObject = (RecyclerView)this.mRecyclerViews.get(i);
      k = j;
      if (((RecyclerView)localObject).getWindowVisibility() == 0)
      {
        ((RecyclerView)localObject).mPrefetchRegistry.collectPrefetchPositionsFromView((RecyclerView)localObject, false);
        k = j + ((RecyclerView)localObject).mPrefetchRegistry.mCount;
      }
      i += 1;
      j = k;
    }
    this.mTasks.ensureCapacity(j);
    i = 0;
    j = 0;
    if (j < n)
    {
      RecyclerView localRecyclerView = (RecyclerView)this.mRecyclerViews.get(j);
      int m;
      if (localRecyclerView.getWindowVisibility() != 0) {
        m = i;
      }
      LayoutPrefetchRegistryImpl localLayoutPrefetchRegistryImpl;
      int i1;
      do
      {
        j += 1;
        i = m;
        break;
        localLayoutPrefetchRegistryImpl = localRecyclerView.mPrefetchRegistry;
        i1 = Math.abs(localLayoutPrefetchRegistryImpl.mPrefetchDx) + Math.abs(localLayoutPrefetchRegistryImpl.mPrefetchDy);
        k = 0;
        m = i;
      } while (k >= localLayoutPrefetchRegistryImpl.mCount * 2);
      if (i >= this.mTasks.size())
      {
        localObject = new Task();
        this.mTasks.add(localObject);
        label197:
        m = localLayoutPrefetchRegistryImpl.mPrefetchArray[(k + 1)];
        if (m > i1) {
          break label285;
        }
      }
      label285:
      for (boolean bool = true;; bool = false)
      {
        ((Task)localObject).immediate = bool;
        ((Task)localObject).viewVelocity = i1;
        ((Task)localObject).distanceToItem = m;
        ((Task)localObject).view = localRecyclerView;
        ((Task)localObject).position = localLayoutPrefetchRegistryImpl.mPrefetchArray[k];
        i += 1;
        k += 2;
        break;
        localObject = (Task)this.mTasks.get(i);
        break label197;
      }
    }
    Collections.sort(this.mTasks, sTaskComparator);
  }
  
  private void flushTaskWithDeadline(Task paramTask, long paramLong)
  {
    if (paramTask.immediate) {}
    for (long l = Long.MAX_VALUE;; l = paramLong)
    {
      paramTask = prefetchPositionWithDeadline(paramTask.view, paramTask.position, l);
      if ((paramTask != null) && (paramTask.mNestedRecyclerView != null) && (paramTask.isBound()) && (!paramTask.isInvalid())) {
        prefetchInnerRecyclerViewWithDeadline((RecyclerView)paramTask.mNestedRecyclerView.get(), paramLong);
      }
      return;
    }
  }
  
  private void flushTasksWithDeadline(long paramLong)
  {
    int i = 0;
    for (;;)
    {
      Task localTask;
      if (i < this.mTasks.size())
      {
        localTask = (Task)this.mTasks.get(i);
        if (localTask.view != null) {}
      }
      else
      {
        return;
      }
      flushTaskWithDeadline(localTask, paramLong);
      localTask.clear();
      i += 1;
    }
  }
  
  static boolean isPrefetchPositionAttached(RecyclerView paramRecyclerView, int paramInt)
  {
    int j = paramRecyclerView.mChildHelper.getUnfilteredChildCount();
    int i = 0;
    while (i < j)
    {
      RecyclerView.ViewHolder localViewHolder = RecyclerView.getChildViewHolderInt(paramRecyclerView.mChildHelper.getUnfilteredChildAt(i));
      if ((localViewHolder.mPosition == paramInt) && (!localViewHolder.isInvalid())) {
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  private void prefetchInnerRecyclerViewWithDeadline(@Nullable RecyclerView paramRecyclerView, long paramLong)
  {
    if (paramRecyclerView == null) {}
    LayoutPrefetchRegistryImpl localLayoutPrefetchRegistryImpl;
    do
    {
      return;
      if ((paramRecyclerView.mDataSetHasChangedAfterLayout) && (paramRecyclerView.mChildHelper.getUnfilteredChildCount() != 0)) {
        paramRecyclerView.removeAndRecycleViews();
      }
      localLayoutPrefetchRegistryImpl = paramRecyclerView.mPrefetchRegistry;
      localLayoutPrefetchRegistryImpl.collectPrefetchPositionsFromView(paramRecyclerView, true);
    } while (localLayoutPrefetchRegistryImpl.mCount == 0);
    try
    {
      TraceCompat.beginSection("RV Nested Prefetch");
      paramRecyclerView.mState.prepareForNestedPrefetch(paramRecyclerView.mAdapter);
      int i = 0;
      while (i < localLayoutPrefetchRegistryImpl.mCount * 2)
      {
        prefetchPositionWithDeadline(paramRecyclerView, localLayoutPrefetchRegistryImpl.mPrefetchArray[i], paramLong);
        i += 2;
      }
      return;
    }
    finally
    {
      TraceCompat.endSection();
    }
  }
  
  /* Error */
  private RecyclerView.ViewHolder prefetchPositionWithDeadline(RecyclerView paramRecyclerView, int paramInt, long paramLong)
  {
    // Byte code:
    //   0: aload_1
    //   1: iload_2
    //   2: invokestatic 217	android/support/v7/widget/GapWorker:isPrefetchPositionAttached	(Landroid/support/v7/widget/RecyclerView;I)Z
    //   5: ifeq +5 -> 10
    //   8: aconst_null
    //   9: areturn
    //   10: aload_1
    //   11: getfield 221	android/support/v7/widget/RecyclerView:mRecycler	Landroid/support/v7/widget/RecyclerView$Recycler;
    //   14: astore 5
    //   16: aload_1
    //   17: invokevirtual 224	android/support/v7/widget/RecyclerView:onEnterLayoutOrScroll	()V
    //   20: aload 5
    //   22: iload_2
    //   23: iconst_0
    //   24: lload_3
    //   25: invokevirtual 230	android/support/v7/widget/RecyclerView$Recycler:tryGetViewHolderForPositionByDeadline	(IZJ)Landroid/support/v7/widget/RecyclerView$ViewHolder;
    //   28: astore 6
    //   30: aload 6
    //   32: ifnull +29 -> 61
    //   35: aload 6
    //   37: invokevirtual 141	android/support/v7/widget/RecyclerView$ViewHolder:isBound	()Z
    //   40: ifeq +29 -> 69
    //   43: aload 6
    //   45: invokevirtual 144	android/support/v7/widget/RecyclerView$ViewHolder:isInvalid	()Z
    //   48: ifne +21 -> 69
    //   51: aload 5
    //   53: aload 6
    //   55: getfield 234	android/support/v7/widget/RecyclerView$ViewHolder:itemView	Landroid/view/View;
    //   58: invokevirtual 238	android/support/v7/widget/RecyclerView$Recycler:recycleView	(Landroid/view/View;)V
    //   61: aload_1
    //   62: iconst_0
    //   63: invokevirtual 242	android/support/v7/widget/RecyclerView:onExitLayoutOrScroll	(Z)V
    //   66: aload 6
    //   68: areturn
    //   69: aload 5
    //   71: aload 6
    //   73: iconst_0
    //   74: invokevirtual 246	android/support/v7/widget/RecyclerView$Recycler:addViewHolderToRecycledViewPool	(Landroid/support/v7/widget/RecyclerView$ViewHolder;Z)V
    //   77: goto -16 -> 61
    //   80: astore 5
    //   82: aload_1
    //   83: iconst_0
    //   84: invokevirtual 242	android/support/v7/widget/RecyclerView:onExitLayoutOrScroll	(Z)V
    //   87: aload 5
    //   89: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	90	0	this	GapWorker
    //   0	90	1	paramRecyclerView	RecyclerView
    //   0	90	2	paramInt	int
    //   0	90	3	paramLong	long
    //   14	56	5	localRecycler	RecyclerView.Recycler
    //   80	8	5	localObject	Object
    //   28	44	6	localViewHolder	RecyclerView.ViewHolder
    // Exception table:
    //   from	to	target	type
    //   16	30	80	finally
    //   35	61	80	finally
    //   69	77	80	finally
  }
  
  public void add(RecyclerView paramRecyclerView)
  {
    this.mRecyclerViews.add(paramRecyclerView);
  }
  
  void postFromTraversal(RecyclerView paramRecyclerView, int paramInt1, int paramInt2)
  {
    if ((paramRecyclerView.isAttachedToWindow()) && (this.mPostTimeNs == 0L))
    {
      this.mPostTimeNs = paramRecyclerView.getNanoTime();
      paramRecyclerView.post(this);
    }
    paramRecyclerView.mPrefetchRegistry.setPrefetchVector(paramInt1, paramInt2);
  }
  
  void prefetch(long paramLong)
  {
    buildTaskList();
    flushTasksWithDeadline(paramLong);
  }
  
  public void remove(RecyclerView paramRecyclerView)
  {
    this.mRecyclerViews.remove(paramRecyclerView);
  }
  
  public void run()
  {
    try
    {
      TraceCompat.beginSection("RV Prefetch");
      boolean bool = this.mRecyclerViews.isEmpty();
      if (bool) {
        return;
      }
      int j = this.mRecyclerViews.size();
      long l1 = 0L;
      int i = 0;
      while (i < j)
      {
        RecyclerView localRecyclerView = (RecyclerView)this.mRecyclerViews.get(i);
        long l2 = l1;
        if (localRecyclerView.getWindowVisibility() == 0) {
          l2 = Math.max(localRecyclerView.getDrawingTime(), l1);
        }
        i += 1;
        l1 = l2;
      }
      if (l1 == 0L) {
        return;
      }
      prefetch(TimeUnit.MILLISECONDS.toNanos(l1) + this.mFrameIntervalNs);
      return;
    }
    finally
    {
      this.mPostTimeNs = 0L;
      TraceCompat.endSection();
    }
  }
  
  static class LayoutPrefetchRegistryImpl
    implements RecyclerView.LayoutManager.LayoutPrefetchRegistry
  {
    int mCount;
    int[] mPrefetchArray;
    int mPrefetchDx;
    int mPrefetchDy;
    
    public void addPosition(int paramInt1, int paramInt2)
    {
      if (paramInt1 < 0) {
        throw new IllegalArgumentException("Layout positions must be non-negative");
      }
      if (paramInt2 < 0) {
        throw new IllegalArgumentException("Pixel distance must be non-negative");
      }
      int i = this.mCount * 2;
      if (this.mPrefetchArray == null)
      {
        this.mPrefetchArray = new int[4];
        Arrays.fill(this.mPrefetchArray, -1);
      }
      for (;;)
      {
        this.mPrefetchArray[i] = paramInt1;
        this.mPrefetchArray[(i + 1)] = paramInt2;
        this.mCount += 1;
        return;
        if (i >= this.mPrefetchArray.length)
        {
          int[] arrayOfInt = this.mPrefetchArray;
          this.mPrefetchArray = new int[i * 2];
          System.arraycopy(arrayOfInt, 0, this.mPrefetchArray, 0, arrayOfInt.length);
        }
      }
    }
    
    void clearPrefetchPositions()
    {
      if (this.mPrefetchArray != null) {
        Arrays.fill(this.mPrefetchArray, -1);
      }
      this.mCount = 0;
    }
    
    void collectPrefetchPositionsFromView(RecyclerView paramRecyclerView, boolean paramBoolean)
    {
      this.mCount = 0;
      if (this.mPrefetchArray != null) {
        Arrays.fill(this.mPrefetchArray, -1);
      }
      RecyclerView.LayoutManager localLayoutManager = paramRecyclerView.mLayout;
      if ((paramRecyclerView.mAdapter != null) && (localLayoutManager != null) && (localLayoutManager.isItemPrefetchEnabled()))
      {
        if (!paramBoolean) {
          break label101;
        }
        if (!paramRecyclerView.mAdapterHelper.hasPendingUpdates()) {
          localLayoutManager.collectInitialPrefetchPositions(paramRecyclerView.mAdapter.getItemCount(), this);
        }
      }
      for (;;)
      {
        if (this.mCount > localLayoutManager.mPrefetchMaxCountObserved)
        {
          localLayoutManager.mPrefetchMaxCountObserved = this.mCount;
          localLayoutManager.mPrefetchMaxObservedInInitialPrefetch = paramBoolean;
          paramRecyclerView.mRecycler.updateViewCacheSize();
        }
        return;
        label101:
        if (!paramRecyclerView.hasPendingAdapterUpdates()) {
          localLayoutManager.collectAdjacentPrefetchPositions(this.mPrefetchDx, this.mPrefetchDy, paramRecyclerView.mState, this);
        }
      }
    }
    
    boolean lastPrefetchIncludedPosition(int paramInt)
    {
      if (this.mPrefetchArray != null)
      {
        int j = this.mCount;
        int i = 0;
        while (i < j * 2)
        {
          if (this.mPrefetchArray[i] == paramInt) {
            return true;
          }
          i += 2;
        }
      }
      return false;
    }
    
    void setPrefetchVector(int paramInt1, int paramInt2)
    {
      this.mPrefetchDx = paramInt1;
      this.mPrefetchDy = paramInt2;
    }
  }
  
  static class Task
  {
    public int distanceToItem;
    public boolean immediate;
    public int position;
    public RecyclerView view;
    public int viewVelocity;
    
    public void clear()
    {
      this.immediate = false;
      this.viewVelocity = 0;
      this.distanceToItem = 0;
      this.view = null;
      this.position = 0;
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v7/widget/GapWorker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */