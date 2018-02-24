package android.support.v17.leanback.widget;

import android.database.Observable;
import java.util.ArrayList;

public abstract class ObjectAdapter
{
  public static final int NO_ID = -1;
  private boolean mHasStableIds;
  private final DataObservable mObservable = new DataObservable();
  private PresenterSelector mPresenterSelector;
  
  public ObjectAdapter() {}
  
  public ObjectAdapter(Presenter paramPresenter)
  {
    setPresenterSelector(new SinglePresenterSelector(paramPresenter));
  }
  
  public ObjectAdapter(PresenterSelector paramPresenterSelector)
  {
    setPresenterSelector(paramPresenterSelector);
  }
  
  public abstract Object get(int paramInt);
  
  public long getId(int paramInt)
  {
    return -1L;
  }
  
  public final Presenter getPresenter(Object paramObject)
  {
    if (this.mPresenterSelector == null) {
      throw new IllegalStateException("Presenter selector must not be null");
    }
    return this.mPresenterSelector.getPresenter(paramObject);
  }
  
  public final PresenterSelector getPresenterSelector()
  {
    return this.mPresenterSelector;
  }
  
  public final boolean hasStableIds()
  {
    return this.mHasStableIds;
  }
  
  public boolean isImmediateNotifySupported()
  {
    return false;
  }
  
  protected final void notifyChanged()
  {
    this.mObservable.notifyChanged();
  }
  
  public final void notifyItemRangeChanged(int paramInt1, int paramInt2)
  {
    this.mObservable.notifyItemRangeChanged(paramInt1, paramInt2);
  }
  
  protected final void notifyItemRangeInserted(int paramInt1, int paramInt2)
  {
    this.mObservable.notifyItemRangeInserted(paramInt1, paramInt2);
  }
  
  protected final void notifyItemRangeRemoved(int paramInt1, int paramInt2)
  {
    this.mObservable.notifyItemRangeRemoved(paramInt1, paramInt2);
  }
  
  protected void onHasStableIdsChanged() {}
  
  protected void onPresenterSelectorChanged() {}
  
  public final void registerObserver(DataObserver paramDataObserver)
  {
    this.mObservable.registerObserver(paramDataObserver);
  }
  
  public final void setHasStableIds(boolean paramBoolean)
  {
    if (this.mHasStableIds != paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      this.mHasStableIds = paramBoolean;
      if (i != 0) {
        onHasStableIdsChanged();
      }
      return;
    }
  }
  
  public final void setPresenterSelector(PresenterSelector paramPresenterSelector)
  {
    int j = 1;
    if (paramPresenterSelector == null) {
      throw new IllegalArgumentException("Presenter selector must not be null");
    }
    int i;
    if (this.mPresenterSelector != null)
    {
      i = 1;
      if ((i == 0) || (this.mPresenterSelector == paramPresenterSelector)) {
        break label64;
      }
    }
    for (;;)
    {
      this.mPresenterSelector = paramPresenterSelector;
      if (j != 0) {
        onPresenterSelectorChanged();
      }
      if (i != 0) {
        notifyChanged();
      }
      return;
      i = 0;
      break;
      label64:
      j = 0;
    }
  }
  
  public abstract int size();
  
  public final void unregisterAllObservers()
  {
    this.mObservable.unregisterAll();
  }
  
  public final void unregisterObserver(DataObserver paramDataObserver)
  {
    this.mObservable.unregisterObserver(paramDataObserver);
  }
  
  private static final class DataObservable
    extends Observable<ObjectAdapter.DataObserver>
  {
    public void notifyChanged()
    {
      int i = this.mObservers.size() - 1;
      while (i >= 0)
      {
        ((ObjectAdapter.DataObserver)this.mObservers.get(i)).onChanged();
        i -= 1;
      }
    }
    
    public void notifyItemRangeChanged(int paramInt1, int paramInt2)
    {
      int i = this.mObservers.size() - 1;
      while (i >= 0)
      {
        ((ObjectAdapter.DataObserver)this.mObservers.get(i)).onItemRangeChanged(paramInt1, paramInt2);
        i -= 1;
      }
    }
    
    public void notifyItemRangeInserted(int paramInt1, int paramInt2)
    {
      int i = this.mObservers.size() - 1;
      while (i >= 0)
      {
        ((ObjectAdapter.DataObserver)this.mObservers.get(i)).onItemRangeInserted(paramInt1, paramInt2);
        i -= 1;
      }
    }
    
    public void notifyItemRangeRemoved(int paramInt1, int paramInt2)
    {
      int i = this.mObservers.size() - 1;
      while (i >= 0)
      {
        ((ObjectAdapter.DataObserver)this.mObservers.get(i)).onItemRangeRemoved(paramInt1, paramInt2);
        i -= 1;
      }
    }
  }
  
  public static abstract class DataObserver
  {
    public void onChanged() {}
    
    public void onItemRangeChanged(int paramInt1, int paramInt2)
    {
      onChanged();
    }
    
    public void onItemRangeInserted(int paramInt1, int paramInt2)
    {
      onChanged();
    }
    
    public void onItemRangeRemoved(int paramInt1, int paramInt2)
    {
      onChanged();
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/ObjectAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */