package android.support.v17.leanback.app;

import android.support.v17.leanback.widget.ObjectAdapter;
import android.support.v17.leanback.widget.ObjectAdapter.DataObserver;
import android.support.v17.leanback.widget.Row;

class ListRowDataAdapter
  extends ObjectAdapter
{
  public static final int ON_CHANGED = 16;
  public static final int ON_ITEM_RANGE_CHANGED = 2;
  public static final int ON_ITEM_RANGE_INSERTED = 4;
  public static final int ON_ITEM_RANGE_REMOVED = 8;
  private final ObjectAdapter mAdapter;
  int mLastVisibleRowIndex;
  
  public ListRowDataAdapter(ObjectAdapter paramObjectAdapter)
  {
    super(paramObjectAdapter.getPresenterSelector());
    this.mAdapter = paramObjectAdapter;
    initialize();
    if (paramObjectAdapter.isImmediateNotifySupported())
    {
      this.mAdapter.registerObserver(new SimpleDataObserver());
      return;
    }
    this.mAdapter.registerObserver(new QueueBasedDataObserver());
  }
  
  void doNotify(int paramInt1, int paramInt2, int paramInt3)
  {
    switch (paramInt1)
    {
    default: 
      throw new IllegalArgumentException("Invalid event type " + paramInt1);
    case 2: 
      notifyItemRangeChanged(paramInt2, paramInt3);
      return;
    case 4: 
      notifyItemRangeInserted(paramInt2, paramInt3);
      return;
    case 8: 
      notifyItemRangeRemoved(paramInt2, paramInt3);
      return;
    }
    notifyChanged();
  }
  
  public Object get(int paramInt)
  {
    return this.mAdapter.get(paramInt);
  }
  
  void initialize()
  {
    this.mLastVisibleRowIndex = -1;
    int i = this.mAdapter.size() - 1;
    for (;;)
    {
      if (i >= 0)
      {
        if (((Row)this.mAdapter.get(i)).isRenderedAsRowView()) {
          this.mLastVisibleRowIndex = i;
        }
      }
      else {
        return;
      }
      i -= 1;
    }
  }
  
  public int size()
  {
    return this.mLastVisibleRowIndex + 1;
  }
  
  private class QueueBasedDataObserver
    extends ObjectAdapter.DataObserver
  {
    QueueBasedDataObserver() {}
    
    public void onChanged()
    {
      ListRowDataAdapter.this.initialize();
      ListRowDataAdapter.this.notifyChanged();
    }
  }
  
  private class SimpleDataObserver
    extends ObjectAdapter.DataObserver
  {
    SimpleDataObserver() {}
    
    public void onChanged()
    {
      ListRowDataAdapter.this.initialize();
      onEventFired(16, -1, -1);
    }
    
    protected void onEventFired(int paramInt1, int paramInt2, int paramInt3)
    {
      ListRowDataAdapter.this.doNotify(paramInt1, paramInt2, paramInt3);
    }
    
    public void onItemRangeChanged(int paramInt1, int paramInt2)
    {
      if (paramInt1 <= ListRowDataAdapter.this.mLastVisibleRowIndex) {
        onEventFired(2, paramInt1, Math.min(paramInt2, ListRowDataAdapter.this.mLastVisibleRowIndex - paramInt1 + 1));
      }
    }
    
    public void onItemRangeInserted(int paramInt1, int paramInt2)
    {
      if (paramInt1 <= ListRowDataAdapter.this.mLastVisibleRowIndex)
      {
        ListRowDataAdapter localListRowDataAdapter = ListRowDataAdapter.this;
        localListRowDataAdapter.mLastVisibleRowIndex += paramInt2;
        onEventFired(4, paramInt1, paramInt2);
      }
      do
      {
        return;
        paramInt1 = ListRowDataAdapter.this.mLastVisibleRowIndex;
        ListRowDataAdapter.this.initialize();
      } while (ListRowDataAdapter.this.mLastVisibleRowIndex <= paramInt1);
      onEventFired(4, paramInt1 + 1, ListRowDataAdapter.this.mLastVisibleRowIndex - paramInt1);
    }
    
    public void onItemRangeRemoved(int paramInt1, int paramInt2)
    {
      if (paramInt1 + paramInt2 - 1 < ListRowDataAdapter.this.mLastVisibleRowIndex)
      {
        ListRowDataAdapter localListRowDataAdapter = ListRowDataAdapter.this;
        localListRowDataAdapter.mLastVisibleRowIndex -= paramInt2;
        onEventFired(8, paramInt1, paramInt2);
      }
      do
      {
        return;
        paramInt2 = ListRowDataAdapter.this.mLastVisibleRowIndex;
        ListRowDataAdapter.this.initialize();
        paramInt2 -= ListRowDataAdapter.this.mLastVisibleRowIndex;
      } while (paramInt2 <= 0);
      onEventFired(8, Math.min(ListRowDataAdapter.this.mLastVisibleRowIndex + 1, paramInt1), paramInt2);
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/app/ListRowDataAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */