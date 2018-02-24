package android.support.v17.leanback.widget;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.util.LruCache;
import android.util.SparseArray;
import android.view.View;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

class ViewsStateBundle
{
  public static final int LIMIT_DEFAULT = 100;
  public static final int UNLIMITED = Integer.MAX_VALUE;
  private LruCache<String, SparseArray<Parcelable>> mChildStates;
  private int mLimitNumber = 100;
  private int mSavePolicy = 0;
  
  static String getSaveStatesKey(int paramInt)
  {
    return Integer.toString(paramInt);
  }
  
  protected void applyPolicyChanges()
  {
    if (this.mSavePolicy == 2)
    {
      if (this.mLimitNumber <= 0) {
        throw new IllegalArgumentException();
      }
      if ((this.mChildStates == null) || (this.mChildStates.maxSize() != this.mLimitNumber)) {
        this.mChildStates = new LruCache(this.mLimitNumber);
      }
    }
    do
    {
      return;
      if ((this.mSavePolicy != 3) && (this.mSavePolicy != 1)) {
        break;
      }
    } while ((this.mChildStates != null) && (this.mChildStates.maxSize() == Integer.MAX_VALUE));
    this.mChildStates = new LruCache(Integer.MAX_VALUE);
    return;
    this.mChildStates = null;
  }
  
  public void clear()
  {
    if (this.mChildStates != null) {
      this.mChildStates.evictAll();
    }
  }
  
  public final int getLimitNumber()
  {
    return this.mLimitNumber;
  }
  
  public final int getSavePolicy()
  {
    return this.mSavePolicy;
  }
  
  public final void loadFromBundle(Bundle paramBundle)
  {
    if ((this.mChildStates != null) && (paramBundle != null))
    {
      this.mChildStates.evictAll();
      Iterator localIterator = paramBundle.keySet().iterator();
      while (localIterator.hasNext())
      {
        String str = (String)localIterator.next();
        this.mChildStates.put(str, paramBundle.getSparseParcelableArray(str));
      }
    }
  }
  
  public final void loadView(View paramView, int paramInt)
  {
    if (this.mChildStates != null)
    {
      Object localObject = getSaveStatesKey(paramInt);
      localObject = (SparseArray)this.mChildStates.remove(localObject);
      if (localObject != null) {
        paramView.restoreHierarchyState((SparseArray)localObject);
      }
    }
  }
  
  public void remove(int paramInt)
  {
    if ((this.mChildStates != null) && (this.mChildStates.size() != 0)) {
      this.mChildStates.remove(getSaveStatesKey(paramInt));
    }
  }
  
  public final Bundle saveAsBundle()
  {
    if ((this.mChildStates == null) || (this.mChildStates.size() == 0))
    {
      localObject = null;
      return (Bundle)localObject;
    }
    Object localObject = this.mChildStates.snapshot();
    Bundle localBundle = new Bundle();
    Iterator localIterator = ((Map)localObject).entrySet().iterator();
    for (;;)
    {
      localObject = localBundle;
      if (!localIterator.hasNext()) {
        break;
      }
      localObject = (Map.Entry)localIterator.next();
      localBundle.putSparseParcelableArray((String)((Map.Entry)localObject).getKey(), (SparseArray)((Map.Entry)localObject).getValue());
    }
  }
  
  public final void saveOffscreenView(View paramView, int paramInt)
  {
    switch (this.mSavePolicy)
    {
    default: 
      return;
    case 2: 
    case 3: 
      saveViewUnchecked(paramView, paramInt);
      return;
    }
    remove(paramInt);
  }
  
  public final Bundle saveOnScreenView(Bundle paramBundle, View paramView, int paramInt)
  {
    Object localObject = paramBundle;
    if (this.mSavePolicy != 0)
    {
      localObject = getSaveStatesKey(paramInt);
      SparseArray localSparseArray = new SparseArray();
      paramView.saveHierarchyState(localSparseArray);
      paramView = paramBundle;
      if (paramBundle == null) {
        paramView = new Bundle();
      }
      paramView.putSparseParcelableArray((String)localObject, localSparseArray);
      localObject = paramView;
    }
    return (Bundle)localObject;
  }
  
  protected final void saveViewUnchecked(View paramView, int paramInt)
  {
    if (this.mChildStates != null)
    {
      String str = getSaveStatesKey(paramInt);
      SparseArray localSparseArray = new SparseArray();
      paramView.saveHierarchyState(localSparseArray);
      this.mChildStates.put(str, localSparseArray);
    }
  }
  
  public final void setLimitNumber(int paramInt)
  {
    this.mLimitNumber = paramInt;
    applyPolicyChanges();
  }
  
  public final void setSavePolicy(int paramInt)
  {
    this.mSavePolicy = paramInt;
    applyPolicyChanges();
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/ViewsStateBundle.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */