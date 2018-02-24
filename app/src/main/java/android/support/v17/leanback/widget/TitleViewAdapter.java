package android.support.v17.leanback.widget;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;

public abstract class TitleViewAdapter
{
  public static final int BRANDING_VIEW_VISIBLE = 2;
  public static final int FULL_VIEW_VISIBLE = 6;
  public static final int SEARCH_VIEW_VISIBLE = 4;
  
  public Drawable getBadgeDrawable()
  {
    return null;
  }
  
  public SearchOrbView.Colors getSearchAffordanceColors()
  {
    return null;
  }
  
  public abstract View getSearchAffordanceView();
  
  public CharSequence getTitle()
  {
    return null;
  }
  
  public void setAnimationEnabled(boolean paramBoolean) {}
  
  public void setBadgeDrawable(Drawable paramDrawable) {}
  
  public void setOnSearchClickedListener(View.OnClickListener paramOnClickListener)
  {
    View localView = getSearchAffordanceView();
    if (localView != null) {
      localView.setOnClickListener(paramOnClickListener);
    }
  }
  
  public void setSearchAffordanceColors(SearchOrbView.Colors paramColors) {}
  
  public void setTitle(CharSequence paramCharSequence) {}
  
  public void updateComponentsVisibility(int paramInt) {}
  
  public static abstract interface Provider
  {
    public abstract TitleViewAdapter getTitleViewAdapter();
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/TitleViewAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */