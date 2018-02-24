package android.support.v7.util;

public class BatchingListUpdateCallback
  implements ListUpdateCallback
{
  private static final int TYPE_ADD = 1;
  private static final int TYPE_CHANGE = 3;
  private static final int TYPE_NONE = 0;
  private static final int TYPE_REMOVE = 2;
  int mLastEventCount = -1;
  Object mLastEventPayload = null;
  int mLastEventPosition = -1;
  int mLastEventType = 0;
  final ListUpdateCallback mWrapped;
  
  public BatchingListUpdateCallback(ListUpdateCallback paramListUpdateCallback)
  {
    this.mWrapped = paramListUpdateCallback;
  }
  
  public void dispatchLastEvent()
  {
    if (this.mLastEventType == 0) {
      return;
    }
    switch (this.mLastEventType)
    {
    }
    for (;;)
    {
      this.mLastEventPayload = null;
      this.mLastEventType = 0;
      return;
      this.mWrapped.onInserted(this.mLastEventPosition, this.mLastEventCount);
      continue;
      this.mWrapped.onRemoved(this.mLastEventPosition, this.mLastEventCount);
      continue;
      this.mWrapped.onChanged(this.mLastEventPosition, this.mLastEventCount, this.mLastEventPayload);
    }
  }
  
  public void onChanged(int paramInt1, int paramInt2, Object paramObject)
  {
    if ((this.mLastEventType == 3) && (paramInt1 <= this.mLastEventPosition + this.mLastEventCount) && (paramInt1 + paramInt2 >= this.mLastEventPosition) && (this.mLastEventPayload == paramObject))
    {
      int i = this.mLastEventPosition;
      int j = this.mLastEventCount;
      this.mLastEventPosition = Math.min(paramInt1, this.mLastEventPosition);
      this.mLastEventCount = (Math.max(i + j, paramInt1 + paramInt2) - this.mLastEventPosition);
      return;
    }
    dispatchLastEvent();
    this.mLastEventPosition = paramInt1;
    this.mLastEventCount = paramInt2;
    this.mLastEventPayload = paramObject;
    this.mLastEventType = 3;
  }
  
  public void onInserted(int paramInt1, int paramInt2)
  {
    if ((this.mLastEventType == 1) && (paramInt1 >= this.mLastEventPosition) && (paramInt1 <= this.mLastEventPosition + this.mLastEventCount))
    {
      this.mLastEventCount += paramInt2;
      this.mLastEventPosition = Math.min(paramInt1, this.mLastEventPosition);
      return;
    }
    dispatchLastEvent();
    this.mLastEventPosition = paramInt1;
    this.mLastEventCount = paramInt2;
    this.mLastEventType = 1;
  }
  
  public void onMoved(int paramInt1, int paramInt2)
  {
    dispatchLastEvent();
    this.mWrapped.onMoved(paramInt1, paramInt2);
  }
  
  public void onRemoved(int paramInt1, int paramInt2)
  {
    if ((this.mLastEventType == 2) && (this.mLastEventPosition >= paramInt1) && (this.mLastEventPosition <= paramInt1 + paramInt2))
    {
      this.mLastEventCount += paramInt2;
      this.mLastEventPosition = paramInt1;
      return;
    }
    dispatchLastEvent();
    this.mLastEventPosition = paramInt1;
    this.mLastEventCount = paramInt2;
    this.mLastEventType = 2;
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v7/util/BatchingListUpdateCallback.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */