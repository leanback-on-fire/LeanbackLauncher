package android.support.v17.leanback.widget;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import java.util.HashMap;
import java.util.Map;

public abstract class Presenter
  implements FacetProvider
{
  private Map<Class, Object> mFacets;
  
  protected static void cancelAnimationsRecursive(View paramView)
  {
    if ((paramView != null) && (paramView.hasTransientState()))
    {
      paramView.animate().cancel();
      if ((paramView instanceof ViewGroup))
      {
        int j = ((ViewGroup)paramView).getChildCount();
        int i = 0;
        while ((paramView.hasTransientState()) && (i < j))
        {
          cancelAnimationsRecursive(((ViewGroup)paramView).getChildAt(i));
          i += 1;
        }
      }
    }
  }
  
  public final Object getFacet(Class<?> paramClass)
  {
    if (this.mFacets == null) {
      return null;
    }
    return this.mFacets.get(paramClass);
  }
  
  public abstract void onBindViewHolder(ViewHolder paramViewHolder, Object paramObject);
  
  public abstract ViewHolder onCreateViewHolder(ViewGroup paramViewGroup);
  
  public abstract void onUnbindViewHolder(ViewHolder paramViewHolder);
  
  public void onViewAttachedToWindow(ViewHolder paramViewHolder) {}
  
  public void onViewDetachedFromWindow(ViewHolder paramViewHolder)
  {
    cancelAnimationsRecursive(paramViewHolder.view);
  }
  
  public final void setFacet(Class<?> paramClass, Object paramObject)
  {
    if (this.mFacets == null) {
      this.mFacets = new HashMap();
    }
    this.mFacets.put(paramClass, paramObject);
  }
  
  public void setOnClickListener(ViewHolder paramViewHolder, View.OnClickListener paramOnClickListener)
  {
    paramViewHolder.view.setOnClickListener(paramOnClickListener);
  }
  
  public static class ViewHolder
    implements FacetProvider
  {
    private Map<Class, Object> mFacets;
    public final View view;
    
    public ViewHolder(View paramView)
    {
      this.view = paramView;
    }
    
    public final Object getFacet(Class<?> paramClass)
    {
      if (this.mFacets == null) {
        return null;
      }
      return this.mFacets.get(paramClass);
    }
    
    public final void setFacet(Class<?> paramClass, Object paramObject)
    {
      if (this.mFacets == null) {
        this.mFacets = new HashMap();
      }
      this.mFacets.put(paramClass, paramObject);
    }
  }
  
  public static abstract class ViewHolderTask
  {
    public void run(Presenter.ViewHolder paramViewHolder) {}
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/Presenter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */