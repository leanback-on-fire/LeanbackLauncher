package android.support.v17.leanback.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class DetailsOverviewRow
  extends Row
{
  private ObjectAdapter mActionsAdapter = new ArrayObjectAdapter(this.mDefaultActionPresenter);
  private PresenterSelector mDefaultActionPresenter = new ActionPresenterSelector();
  private Drawable mImageDrawable;
  private boolean mImageScaleUpAllowed = true;
  private Object mItem;
  private ArrayList<WeakReference<Listener>> mListeners;
  
  public DetailsOverviewRow(Object paramObject)
  {
    super(null);
    this.mItem = paramObject;
    verify();
  }
  
  private ArrayObjectAdapter getArrayObjectAdapter()
  {
    return (ArrayObjectAdapter)this.mActionsAdapter;
  }
  
  private void verify()
  {
    if (this.mItem == null) {
      throw new IllegalArgumentException("Object cannot be null");
    }
  }
  
  @Deprecated
  public final void addAction(int paramInt, Action paramAction)
  {
    getArrayObjectAdapter().add(paramInt, paramAction);
  }
  
  @Deprecated
  public final void addAction(Action paramAction)
  {
    getArrayObjectAdapter().add(paramAction);
  }
  
  final void addListener(Listener paramListener)
  {
    if (this.mListeners == null) {
      this.mListeners = new ArrayList();
    }
    label92:
    for (;;)
    {
      this.mListeners.add(new WeakReference(paramListener));
      return;
      int i = 0;
      for (;;)
      {
        if (i >= this.mListeners.size()) {
          break label92;
        }
        Listener localListener = (Listener)((WeakReference)this.mListeners.get(i)).get();
        if (localListener == null)
        {
          this.mListeners.remove(i);
        }
        else
        {
          if (localListener == paramListener) {
            break;
          }
          i += 1;
        }
      }
    }
  }
  
  public Action getActionForKeyCode(int paramInt)
  {
    ObjectAdapter localObjectAdapter = getActionsAdapter();
    if (localObjectAdapter != null)
    {
      int i = 0;
      while (i < localObjectAdapter.size())
      {
        Action localAction = (Action)localObjectAdapter.get(i);
        if (localAction.respondsToKeyCode(paramInt)) {
          return localAction;
        }
        i += 1;
      }
    }
    return null;
  }
  
  @Deprecated
  public final List<Action> getActions()
  {
    return getArrayObjectAdapter().unmodifiableList();
  }
  
  public final ObjectAdapter getActionsAdapter()
  {
    return this.mActionsAdapter;
  }
  
  public final Drawable getImageDrawable()
  {
    return this.mImageDrawable;
  }
  
  public final Object getItem()
  {
    return this.mItem;
  }
  
  public boolean isImageScaleUpAllowed()
  {
    return this.mImageScaleUpAllowed;
  }
  
  final void notifyActionsAdapterChanged()
  {
    if (this.mListeners != null)
    {
      int i = 0;
      while (i < this.mListeners.size())
      {
        Listener localListener = (Listener)((WeakReference)this.mListeners.get(i)).get();
        if (localListener == null)
        {
          this.mListeners.remove(i);
        }
        else
        {
          localListener.onActionsAdapterChanged(this);
          i += 1;
        }
      }
    }
  }
  
  final void notifyImageDrawableChanged()
  {
    if (this.mListeners != null)
    {
      int i = 0;
      while (i < this.mListeners.size())
      {
        Listener localListener = (Listener)((WeakReference)this.mListeners.get(i)).get();
        if (localListener == null)
        {
          this.mListeners.remove(i);
        }
        else
        {
          localListener.onImageDrawableChanged(this);
          i += 1;
        }
      }
    }
  }
  
  final void notifyItemChanged()
  {
    if (this.mListeners != null)
    {
      int i = 0;
      while (i < this.mListeners.size())
      {
        Listener localListener = (Listener)((WeakReference)this.mListeners.get(i)).get();
        if (localListener == null)
        {
          this.mListeners.remove(i);
        }
        else
        {
          localListener.onItemChanged(this);
          i += 1;
        }
      }
    }
  }
  
  @Deprecated
  public final boolean removeAction(Action paramAction)
  {
    return getArrayObjectAdapter().remove(paramAction);
  }
  
  final void removeListener(Listener paramListener)
  {
    int i;
    if (this.mListeners != null) {
      i = 0;
    }
    for (;;)
    {
      if (i < this.mListeners.size())
      {
        Listener localListener = (Listener)((WeakReference)this.mListeners.get(i)).get();
        if (localListener == null)
        {
          this.mListeners.remove(i);
          continue;
        }
        if (localListener == paramListener) {
          this.mListeners.remove(i);
        }
      }
      else
      {
        return;
      }
      i += 1;
    }
  }
  
  public final void setActionsAdapter(ObjectAdapter paramObjectAdapter)
  {
    if (paramObjectAdapter != this.mActionsAdapter)
    {
      this.mActionsAdapter = paramObjectAdapter;
      if (this.mActionsAdapter.getPresenterSelector() == null) {
        this.mActionsAdapter.setPresenterSelector(this.mDefaultActionPresenter);
      }
      notifyActionsAdapterChanged();
    }
  }
  
  public final void setImageBitmap(Context paramContext, Bitmap paramBitmap)
  {
    this.mImageDrawable = new BitmapDrawable(paramContext.getResources(), paramBitmap);
    notifyImageDrawableChanged();
  }
  
  public final void setImageDrawable(Drawable paramDrawable)
  {
    if (this.mImageDrawable != paramDrawable)
    {
      this.mImageDrawable = paramDrawable;
      notifyImageDrawableChanged();
    }
  }
  
  public void setImageScaleUpAllowed(boolean paramBoolean)
  {
    if (paramBoolean != this.mImageScaleUpAllowed)
    {
      this.mImageScaleUpAllowed = paramBoolean;
      notifyImageDrawableChanged();
    }
  }
  
  public final void setItem(Object paramObject)
  {
    if (paramObject != this.mItem)
    {
      this.mItem = paramObject;
      notifyItemChanged();
    }
  }
  
  public static class Listener
  {
    public void onActionsAdapterChanged(DetailsOverviewRow paramDetailsOverviewRow) {}
    
    public void onImageDrawableChanged(DetailsOverviewRow paramDetailsOverviewRow) {}
    
    public void onItemChanged(DetailsOverviewRow paramDetailsOverviewRow) {}
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/DetailsOverviewRow.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */