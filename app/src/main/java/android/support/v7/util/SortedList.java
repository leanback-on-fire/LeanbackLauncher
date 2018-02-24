package android.support.v7.util;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;

public class SortedList<T>
{
  private static final int CAPACITY_GROWTH = 10;
  private static final int DELETION = 2;
  private static final int INSERTION = 1;
  public static final int INVALID_POSITION = -1;
  private static final int LOOKUP = 4;
  private static final int MIN_CAPACITY = 10;
  private BatchedCallback mBatchedCallback;
  private Callback mCallback;
  T[] mData;
  private int mMergedSize;
  private T[] mOldData;
  private int mOldDataSize;
  private int mOldDataStart;
  private int mSize;
  private final Class<T> mTClass;
  
  public SortedList(Class<T> paramClass, Callback<T> paramCallback)
  {
    this(paramClass, paramCallback, 10);
  }
  
  public SortedList(Class<T> paramClass, Callback<T> paramCallback, int paramInt)
  {
    this.mTClass = paramClass;
    this.mData = ((Object[])Array.newInstance(paramClass, paramInt));
    this.mCallback = paramCallback;
    this.mSize = 0;
  }
  
  private int add(T paramT, boolean paramBoolean)
  {
    int j = findIndexOf(paramT, this.mData, 0, this.mSize, 1);
    int i;
    if (j == -1) {
      i = 0;
    }
    Object localObject;
    do
    {
      do
      {
        addToData(i, paramT);
        if (paramBoolean) {
          this.mCallback.onInserted(i, 1);
        }
        return i;
        i = j;
      } while (j >= this.mSize);
      localObject = this.mData[j];
      i = j;
    } while (!this.mCallback.areItemsTheSame(localObject, paramT));
    if (this.mCallback.areContentsTheSame(localObject, paramT))
    {
      this.mData[j] = paramT;
      return j;
    }
    this.mData[j] = paramT;
    this.mCallback.onChanged(j, 1);
    return j;
  }
  
  private void addAllInternal(T[] paramArrayOfT)
  {
    int i;
    int j;
    if (!(this.mCallback instanceof BatchedCallback))
    {
      i = 1;
      if (i != 0) {
        beginBatchedUpdates();
      }
      this.mOldData = this.mData;
      this.mOldDataStart = 0;
      this.mOldDataSize = this.mSize;
      Arrays.sort(paramArrayOfT, this.mCallback);
      j = deduplicate(paramArrayOfT);
      if (this.mSize != 0) {
        break label105;
      }
      this.mData = paramArrayOfT;
      this.mSize = j;
      this.mMergedSize = j;
      this.mCallback.onInserted(0, j);
    }
    for (;;)
    {
      this.mOldData = null;
      if (i != 0) {
        endBatchedUpdates();
      }
      return;
      i = 0;
      break;
      label105:
      merge(paramArrayOfT, j);
    }
  }
  
  private void addToData(int paramInt, T paramT)
  {
    if (paramInt > this.mSize) {
      throw new IndexOutOfBoundsException("cannot add item to " + paramInt + " because size is " + this.mSize);
    }
    if (this.mSize == this.mData.length)
    {
      Object[] arrayOfObject = (Object[])Array.newInstance(this.mTClass, this.mData.length + 10);
      System.arraycopy(this.mData, 0, arrayOfObject, 0, paramInt);
      arrayOfObject[paramInt] = paramT;
      System.arraycopy(this.mData, paramInt, arrayOfObject, paramInt + 1, this.mSize - paramInt);
      this.mData = arrayOfObject;
    }
    for (;;)
    {
      this.mSize += 1;
      return;
      System.arraycopy(this.mData, paramInt, this.mData, paramInt + 1, this.mSize - paramInt);
      this.mData[paramInt] = paramT;
    }
  }
  
  private int deduplicate(T[] paramArrayOfT)
  {
    if (paramArrayOfT.length == 0) {
      throw new IllegalArgumentException("Input array must be non-empty");
    }
    int k = 0;
    int i = 1;
    int j = 1;
    if (j < paramArrayOfT.length)
    {
      T ? = paramArrayOfT[j];
      int m = this.mCallback.compare(paramArrayOfT[k], ?);
      if (m > 0) {
        throw new IllegalArgumentException("Input must be sorted in ascending order.");
      }
      if (m == 0)
      {
        m = findSameItem(?, paramArrayOfT, k, i);
        if (m != -1) {
          paramArrayOfT[m] = ?;
        }
      }
      for (;;)
      {
        j += 1;
        break;
        if (i != j) {
          paramArrayOfT[i] = ?;
        }
        i += 1;
        continue;
        if (i != j) {
          paramArrayOfT[i] = ?;
        }
        k = i;
        i += 1;
      }
    }
    return i;
  }
  
  private int findIndexOf(T paramT, T[] paramArrayOfT, int paramInt1, int paramInt2, int paramInt3)
  {
    while (paramInt1 < paramInt2)
    {
      int i = (paramInt1 + paramInt2) / 2;
      T ? = paramArrayOfT[i];
      int j = this.mCallback.compare(?, paramT);
      if (j < 0)
      {
        paramInt1 = i + 1;
      }
      else
      {
        if (j == 0)
        {
          if (this.mCallback.areItemsTheSame(?, paramT)) {}
          do
          {
            return i;
            paramInt1 = linearEqualitySearch(paramT, i, paramInt1, paramInt2);
            if (paramInt3 != 1) {
              break;
            }
          } while (paramInt1 == -1);
          return paramInt1;
          return paramInt1;
        }
        paramInt2 = i;
      }
    }
    if (paramInt3 == 1) {}
    for (;;)
    {
      return paramInt1;
      paramInt1 = -1;
    }
  }
  
  private int findSameItem(T paramT, T[] paramArrayOfT, int paramInt1, int paramInt2)
  {
    while (paramInt1 < paramInt2)
    {
      if (this.mCallback.areItemsTheSame(paramArrayOfT[paramInt1], paramT)) {
        return paramInt1;
      }
      paramInt1 += 1;
    }
    return -1;
  }
  
  private int linearEqualitySearch(T paramT, int paramInt1, int paramInt2, int paramInt3)
  {
    int i = paramInt1 - 1;
    Object localObject;
    if (i >= paramInt2)
    {
      localObject = this.mData[i];
      if (this.mCallback.compare(localObject, paramT) == 0) {}
    }
    else
    {
      paramInt1 += 1;
    }
    for (;;)
    {
      if (paramInt1 < paramInt3)
      {
        localObject = this.mData[paramInt1];
        if (this.mCallback.compare(localObject, paramT) == 0) {}
      }
      else
      {
        return -1;
        if (this.mCallback.areItemsTheSame(localObject, paramT)) {
          return i;
        }
        i -= 1;
        break;
      }
      if (this.mCallback.areItemsTheSame(localObject, paramT)) {
        return paramInt1;
      }
      paramInt1 += 1;
    }
  }
  
  private void merge(T[] paramArrayOfT, int paramInt)
  {
    int i = this.mSize;
    this.mData = ((Object[])Array.newInstance(this.mTClass, i + paramInt + 10));
    this.mMergedSize = 0;
    i = 0;
    for (;;)
    {
      if ((this.mOldDataStart < this.mOldDataSize) || (i < paramInt))
      {
        if (this.mOldDataStart == this.mOldDataSize)
        {
          paramInt -= i;
          System.arraycopy(paramArrayOfT, i, this.mData, this.mMergedSize, paramInt);
          this.mMergedSize += paramInt;
          this.mSize += paramInt;
          this.mCallback.onInserted(this.mMergedSize - paramInt, paramInt);
        }
      }
      else {
        return;
      }
      if (i == paramInt)
      {
        paramInt = this.mOldDataSize - this.mOldDataStart;
        System.arraycopy(this.mOldData, this.mOldDataStart, this.mData, this.mMergedSize, paramInt);
        this.mMergedSize += paramInt;
        return;
      }
      Object localObject1 = this.mOldData[this.mOldDataStart];
      Object localObject2 = paramArrayOfT[i];
      int j = this.mCallback.compare(localObject1, localObject2);
      if (j > 0)
      {
        localObject1 = this.mData;
        j = this.mMergedSize;
        this.mMergedSize = (j + 1);
        localObject1[j] = localObject2;
        this.mSize += 1;
        i += 1;
        this.mCallback.onInserted(this.mMergedSize - 1, 1);
      }
      else if ((j == 0) && (this.mCallback.areItemsTheSame(localObject1, localObject2)))
      {
        Object[] arrayOfObject = this.mData;
        j = this.mMergedSize;
        this.mMergedSize = (j + 1);
        arrayOfObject[j] = localObject2;
        j = i + 1;
        this.mOldDataStart += 1;
        i = j;
        if (!this.mCallback.areContentsTheSame(localObject1, localObject2))
        {
          this.mCallback.onChanged(this.mMergedSize - 1, 1);
          i = j;
        }
      }
      else
      {
        localObject2 = this.mData;
        j = this.mMergedSize;
        this.mMergedSize = (j + 1);
        localObject2[j] = localObject1;
        this.mOldDataStart += 1;
      }
    }
  }
  
  private boolean remove(T paramT, boolean paramBoolean)
  {
    int i = findIndexOf(paramT, this.mData, 0, this.mSize, 2);
    if (i == -1) {
      return false;
    }
    removeItemAtIndex(i, paramBoolean);
    return true;
  }
  
  private void removeItemAtIndex(int paramInt, boolean paramBoolean)
  {
    System.arraycopy(this.mData, paramInt + 1, this.mData, paramInt, this.mSize - paramInt - 1);
    this.mSize -= 1;
    this.mData[this.mSize] = null;
    if (paramBoolean) {
      this.mCallback.onRemoved(paramInt, 1);
    }
  }
  
  private void throwIfMerging()
  {
    if (this.mOldData != null) {
      throw new IllegalStateException("Cannot call this method from within addAll");
    }
  }
  
  public int add(T paramT)
  {
    throwIfMerging();
    return add(paramT, true);
  }
  
  public void addAll(Collection<T> paramCollection)
  {
    addAll(paramCollection.toArray((Object[])Array.newInstance(this.mTClass, paramCollection.size())), true);
  }
  
  public void addAll(T... paramVarArgs)
  {
    addAll(paramVarArgs, false);
  }
  
  public void addAll(T[] paramArrayOfT, boolean paramBoolean)
  {
    throwIfMerging();
    if (paramArrayOfT.length == 0) {
      return;
    }
    if (paramBoolean)
    {
      addAllInternal(paramArrayOfT);
      return;
    }
    Object[] arrayOfObject = (Object[])Array.newInstance(this.mTClass, paramArrayOfT.length);
    System.arraycopy(paramArrayOfT, 0, arrayOfObject, 0, paramArrayOfT.length);
    addAllInternal(arrayOfObject);
  }
  
  public void beginBatchedUpdates()
  {
    throwIfMerging();
    if ((this.mCallback instanceof BatchedCallback)) {
      return;
    }
    if (this.mBatchedCallback == null) {
      this.mBatchedCallback = new BatchedCallback(this.mCallback);
    }
    this.mCallback = this.mBatchedCallback;
  }
  
  public void clear()
  {
    throwIfMerging();
    if (this.mSize == 0) {
      return;
    }
    int i = this.mSize;
    Arrays.fill(this.mData, 0, i, null);
    this.mSize = 0;
    this.mCallback.onRemoved(0, i);
  }
  
  public void endBatchedUpdates()
  {
    throwIfMerging();
    if ((this.mCallback instanceof BatchedCallback)) {
      ((BatchedCallback)this.mCallback).dispatchLastEvent();
    }
    if (this.mCallback == this.mBatchedCallback) {
      this.mCallback = this.mBatchedCallback.mWrappedCallback;
    }
  }
  
  public T get(int paramInt)
    throws IndexOutOfBoundsException
  {
    if ((paramInt >= this.mSize) || (paramInt < 0)) {
      throw new IndexOutOfBoundsException("Asked to get item at " + paramInt + " but size is " + this.mSize);
    }
    if ((this.mOldData != null) && (paramInt >= this.mMergedSize)) {
      return (T)this.mOldData[(paramInt - this.mMergedSize + this.mOldDataStart)];
    }
    return (T)this.mData[paramInt];
  }
  
  public int indexOf(T paramT)
  {
    if (this.mOldData != null)
    {
      int i = findIndexOf(paramT, this.mData, 0, this.mMergedSize, 4);
      if (i != -1) {
        return i;
      }
      i = findIndexOf(paramT, this.mOldData, this.mOldDataStart, this.mOldDataSize, 4);
      if (i != -1) {
        return i - this.mOldDataStart + this.mMergedSize;
      }
      return -1;
    }
    return findIndexOf(paramT, this.mData, 0, this.mSize, 4);
  }
  
  public void recalculatePositionOfItemAt(int paramInt)
  {
    throwIfMerging();
    Object localObject = get(paramInt);
    removeItemAtIndex(paramInt, false);
    int i = add(localObject, false);
    if (paramInt != i) {
      this.mCallback.onMoved(paramInt, i);
    }
  }
  
  public boolean remove(T paramT)
  {
    throwIfMerging();
    return remove(paramT, true);
  }
  
  public T removeItemAt(int paramInt)
  {
    throwIfMerging();
    Object localObject = get(paramInt);
    removeItemAtIndex(paramInt, true);
    return (T)localObject;
  }
  
  public int size()
  {
    return this.mSize;
  }
  
  public void updateItemAt(int paramInt, T paramT)
  {
    throwIfMerging();
    Object localObject = get(paramInt);
    int i;
    if ((localObject == paramT) || (!this.mCallback.areContentsTheSame(localObject, paramT)))
    {
      i = 1;
      if ((localObject == paramT) || (this.mCallback.compare(localObject, paramT) != 0)) {
        break label77;
      }
      this.mData[paramInt] = paramT;
      if (i != 0) {
        this.mCallback.onChanged(paramInt, 1);
      }
    }
    label77:
    do
    {
      return;
      i = 0;
      break;
      if (i != 0) {
        this.mCallback.onChanged(paramInt, 1);
      }
      removeItemAtIndex(paramInt, false);
      i = add(paramT, false);
    } while (paramInt == i);
    this.mCallback.onMoved(paramInt, i);
  }
  
  public static class BatchedCallback<T2>
    extends SortedList.Callback<T2>
  {
    private final BatchingListUpdateCallback mBatchingListUpdateCallback;
    final SortedList.Callback<T2> mWrappedCallback;
    
    public BatchedCallback(SortedList.Callback<T2> paramCallback)
    {
      this.mWrappedCallback = paramCallback;
      this.mBatchingListUpdateCallback = new BatchingListUpdateCallback(this.mWrappedCallback);
    }
    
    public boolean areContentsTheSame(T2 paramT21, T2 paramT22)
    {
      return this.mWrappedCallback.areContentsTheSame(paramT21, paramT22);
    }
    
    public boolean areItemsTheSame(T2 paramT21, T2 paramT22)
    {
      return this.mWrappedCallback.areItemsTheSame(paramT21, paramT22);
    }
    
    public int compare(T2 paramT21, T2 paramT22)
    {
      return this.mWrappedCallback.compare(paramT21, paramT22);
    }
    
    public void dispatchLastEvent()
    {
      this.mBatchingListUpdateCallback.dispatchLastEvent();
    }
    
    public void onChanged(int paramInt1, int paramInt2)
    {
      this.mBatchingListUpdateCallback.onChanged(paramInt1, paramInt2, null);
    }
    
    public void onInserted(int paramInt1, int paramInt2)
    {
      this.mBatchingListUpdateCallback.onInserted(paramInt1, paramInt2);
    }
    
    public void onMoved(int paramInt1, int paramInt2)
    {
      this.mBatchingListUpdateCallback.onMoved(paramInt1, paramInt2);
    }
    
    public void onRemoved(int paramInt1, int paramInt2)
    {
      this.mBatchingListUpdateCallback.onRemoved(paramInt1, paramInt2);
    }
  }
  
  public static abstract class Callback<T2>
    implements Comparator<T2>, ListUpdateCallback
  {
    public abstract boolean areContentsTheSame(T2 paramT21, T2 paramT22);
    
    public abstract boolean areItemsTheSame(T2 paramT21, T2 paramT22);
    
    public abstract int compare(T2 paramT21, T2 paramT22);
    
    public abstract void onChanged(int paramInt1, int paramInt2);
    
    public void onChanged(int paramInt1, int paramInt2, Object paramObject)
    {
      onChanged(paramInt1, paramInt2);
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v7/util/SortedList.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */