package android.support.v17.leanback.widget;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ArrayObjectAdapter
  extends ObjectAdapter
{
  private ArrayList<Object> mItems = new ArrayList();
  
  public ArrayObjectAdapter() {}
  
  public ArrayObjectAdapter(Presenter paramPresenter)
  {
    super(paramPresenter);
  }
  
  public ArrayObjectAdapter(PresenterSelector paramPresenterSelector)
  {
    super(paramPresenterSelector);
  }
  
  public void add(int paramInt, Object paramObject)
  {
    this.mItems.add(paramInt, paramObject);
    notifyItemRangeInserted(paramInt, 1);
  }
  
  public void add(Object paramObject)
  {
    add(this.mItems.size(), paramObject);
  }
  
  public void addAll(int paramInt, Collection paramCollection)
  {
    int i = paramCollection.size();
    if (i == 0) {
      return;
    }
    this.mItems.addAll(paramInt, paramCollection);
    notifyItemRangeInserted(paramInt, i);
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
  
  public Object get(int paramInt)
  {
    return this.mItems.get(paramInt);
  }
  
  public int indexOf(Object paramObject)
  {
    return this.mItems.indexOf(paramObject);
  }
  
  public boolean isImmediateNotifySupported()
  {
    return true;
  }
  
  public void notifyArrayItemRangeChanged(int paramInt1, int paramInt2)
  {
    notifyItemRangeChanged(paramInt1, paramInt2);
  }
  
  public boolean remove(Object paramObject)
  {
    int i = this.mItems.indexOf(paramObject);
    if (i >= 0)
    {
      this.mItems.remove(i);
      notifyItemRangeRemoved(i, 1);
    }
    return i >= 0;
  }
  
  public int removeItems(int paramInt1, int paramInt2)
  {
    int i = Math.min(paramInt2, this.mItems.size() - paramInt1);
    if (i <= 0) {
      return 0;
    }
    paramInt2 = 0;
    while (paramInt2 < i)
    {
      this.mItems.remove(paramInt1);
      paramInt2 += 1;
    }
    notifyItemRangeRemoved(paramInt1, i);
    return i;
  }
  
  public void replace(int paramInt, Object paramObject)
  {
    this.mItems.set(paramInt, paramObject);
    notifyItemRangeChanged(paramInt, 1);
  }
  
  public int size()
  {
    return this.mItems.size();
  }
  
  public <E> List<E> unmodifiableList()
  {
    return Collections.unmodifiableList(this.mItems);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/ArrayObjectAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */