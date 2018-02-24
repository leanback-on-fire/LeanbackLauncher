package android.support.v17.leanback.widget;

import android.util.SparseArray;

public class SparseArrayObjectAdapter
  extends ObjectAdapter
{
  private SparseArray<Object> mItems = new SparseArray();
  
  public SparseArrayObjectAdapter() {}
  
  public SparseArrayObjectAdapter(Presenter paramPresenter)
  {
    super(paramPresenter);
  }
  
  public SparseArrayObjectAdapter(PresenterSelector paramPresenterSelector)
  {
    super(paramPresenterSelector);
  }
  
  public void clear()
  {
    int i = this.mItems.size();
    if (i == 0) {
      return;
    }
    this.mItems.clear();
    notifyItemRangeRemoved(0, i);
  }
  
  public void clear(int paramInt)
  {
    paramInt = this.mItems.indexOfKey(paramInt);
    if (paramInt >= 0)
    {
      this.mItems.removeAt(paramInt);
      notifyItemRangeRemoved(paramInt, 1);
    }
  }
  
  public Object get(int paramInt)
  {
    return this.mItems.valueAt(paramInt);
  }
  
  public int indexOf(int paramInt)
  {
    return this.mItems.indexOfKey(paramInt);
  }
  
  public int indexOf(Object paramObject)
  {
    return this.mItems.indexOfValue(paramObject);
  }
  
  public boolean isImmediateNotifySupported()
  {
    return true;
  }
  
  public Object lookup(int paramInt)
  {
    return this.mItems.get(paramInt);
  }
  
  public void notifyArrayItemRangeChanged(int paramInt1, int paramInt2)
  {
    notifyItemRangeChanged(paramInt1, paramInt2);
  }
  
  public void set(int paramInt, Object paramObject)
  {
    int i = this.mItems.indexOfKey(paramInt);
    if (i >= 0)
    {
      if (this.mItems.valueAt(i) != paramObject)
      {
        this.mItems.setValueAt(i, paramObject);
        notifyItemRangeChanged(i, 1);
      }
      return;
    }
    this.mItems.append(paramInt, paramObject);
    notifyItemRangeInserted(this.mItems.indexOfKey(paramInt), 1);
  }
  
  public int size()
  {
    return this.mItems.size();
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/SparseArrayObjectAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */