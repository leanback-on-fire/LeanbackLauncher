package android.support.v4.widget;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.view.View;
import android.widget.SearchView;
import android.widget.SearchView.OnCloseListener;
import android.widget.SearchView.OnQueryTextListener;

@Deprecated
public final class SearchViewCompat
{
  private SearchViewCompat(Context paramContext) {}
  
  private static void checkIfLegalArg(View paramView)
  {
    if (paramView == null) {
      throw new IllegalArgumentException("searchView must be non-null");
    }
    if (!(paramView instanceof SearchView)) {
      throw new IllegalArgumentException("searchView must be an instance of android.widget.SearchView");
    }
  }
  
  @Deprecated
  public static CharSequence getQuery(View paramView)
  {
    checkIfLegalArg(paramView);
    return ((SearchView)paramView).getQuery();
  }
  
  @Deprecated
  public static boolean isIconified(View paramView)
  {
    checkIfLegalArg(paramView);
    return ((SearchView)paramView).isIconified();
  }
  
  @Deprecated
  public static boolean isQueryRefinementEnabled(View paramView)
  {
    checkIfLegalArg(paramView);
    return ((SearchView)paramView).isQueryRefinementEnabled();
  }
  
  @Deprecated
  public static boolean isSubmitButtonEnabled(View paramView)
  {
    checkIfLegalArg(paramView);
    return ((SearchView)paramView).isSubmitButtonEnabled();
  }
  
  private static SearchView.OnCloseListener newOnCloseListener(OnCloseListener paramOnCloseListener)
  {
    new SearchView.OnCloseListener()
    {
      public boolean onClose()
      {
        return this.val$listener.onClose();
      }
    };
  }
  
  private static SearchView.OnQueryTextListener newOnQueryTextListener(OnQueryTextListener paramOnQueryTextListener)
  {
    new SearchView.OnQueryTextListener()
    {
      public boolean onQueryTextChange(String paramAnonymousString)
      {
        return this.val$listener.onQueryTextChange(paramAnonymousString);
      }
      
      public boolean onQueryTextSubmit(String paramAnonymousString)
      {
        return this.val$listener.onQueryTextSubmit(paramAnonymousString);
      }
    };
  }
  
  @Deprecated
  public static View newSearchView(Context paramContext)
  {
    return new SearchView(paramContext);
  }
  
  @Deprecated
  public static void setIconified(View paramView, boolean paramBoolean)
  {
    checkIfLegalArg(paramView);
    ((SearchView)paramView).setIconified(paramBoolean);
  }
  
  @Deprecated
  public static void setImeOptions(View paramView, int paramInt)
  {
    checkIfLegalArg(paramView);
    ((SearchView)paramView).setImeOptions(paramInt);
  }
  
  @Deprecated
  public static void setInputType(View paramView, int paramInt)
  {
    checkIfLegalArg(paramView);
    ((SearchView)paramView).setInputType(paramInt);
  }
  
  @Deprecated
  public static void setMaxWidth(View paramView, int paramInt)
  {
    checkIfLegalArg(paramView);
    ((SearchView)paramView).setMaxWidth(paramInt);
  }
  
  @Deprecated
  public static void setOnCloseListener(View paramView, OnCloseListener paramOnCloseListener)
  {
    checkIfLegalArg(paramView);
    ((SearchView)paramView).setOnCloseListener(newOnCloseListener(paramOnCloseListener));
  }
  
  @Deprecated
  public static void setOnQueryTextListener(View paramView, OnQueryTextListener paramOnQueryTextListener)
  {
    checkIfLegalArg(paramView);
    ((SearchView)paramView).setOnQueryTextListener(newOnQueryTextListener(paramOnQueryTextListener));
  }
  
  @Deprecated
  public static void setQuery(View paramView, CharSequence paramCharSequence, boolean paramBoolean)
  {
    checkIfLegalArg(paramView);
    ((SearchView)paramView).setQuery(paramCharSequence, paramBoolean);
  }
  
  @Deprecated
  public static void setQueryHint(View paramView, CharSequence paramCharSequence)
  {
    checkIfLegalArg(paramView);
    ((SearchView)paramView).setQueryHint(paramCharSequence);
  }
  
  @Deprecated
  public static void setQueryRefinementEnabled(View paramView, boolean paramBoolean)
  {
    checkIfLegalArg(paramView);
    ((SearchView)paramView).setQueryRefinementEnabled(paramBoolean);
  }
  
  @Deprecated
  public static void setSearchableInfo(View paramView, ComponentName paramComponentName)
  {
    checkIfLegalArg(paramView);
    SearchManager localSearchManager = (SearchManager)paramView.getContext().getSystemService("search");
    ((SearchView)paramView).setSearchableInfo(localSearchManager.getSearchableInfo(paramComponentName));
  }
  
  @Deprecated
  public static void setSubmitButtonEnabled(View paramView, boolean paramBoolean)
  {
    checkIfLegalArg(paramView);
    ((SearchView)paramView).setSubmitButtonEnabled(paramBoolean);
  }
  
  @Deprecated
  public static abstract interface OnCloseListener
  {
    public abstract boolean onClose();
  }
  
  @Deprecated
  public static abstract class OnCloseListenerCompat
    implements SearchViewCompat.OnCloseListener
  {
    public boolean onClose()
    {
      return false;
    }
  }
  
  @Deprecated
  public static abstract interface OnQueryTextListener
  {
    public abstract boolean onQueryTextChange(String paramString);
    
    public abstract boolean onQueryTextSubmit(String paramString);
  }
  
  @Deprecated
  public static abstract class OnQueryTextListenerCompat
    implements SearchViewCompat.OnQueryTextListener
  {
    public boolean onQueryTextChange(String paramString)
    {
      return false;
    }
    
    public boolean onQueryTextSubmit(String paramString)
    {
      return false;
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v4/widget/SearchViewCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */