package android.support.v17.leanback.app;

import android.content.Context;
import android.content.res.Resources.Theme;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v17.leanback.R.attr;
import android.support.v17.leanback.R.id;
import android.support.v17.leanback.R.layout;
import android.support.v17.leanback.widget.SearchOrbView.Colors;
import android.support.v17.leanback.widget.TitleHelper;
import android.support.v17.leanback.widget.TitleViewAdapter;
import android.support.v17.leanback.widget.TitleViewAdapter.Provider;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class BrandedSupportFragment
  extends Fragment
{
  private static final String TITLE_SHOW = "titleShow";
  private Drawable mBadgeDrawable;
  private View.OnClickListener mExternalOnSearchClickedListener;
  private boolean mSearchAffordanceColorSet;
  private SearchOrbView.Colors mSearchAffordanceColors;
  private boolean mShowingTitle = true;
  private CharSequence mTitle;
  private TitleHelper mTitleHelper;
  private View mTitleView;
  private TitleViewAdapter mTitleViewAdapter;
  
  public Drawable getBadgeDrawable()
  {
    return this.mBadgeDrawable;
  }
  
  public int getSearchAffordanceColor()
  {
    return getSearchAffordanceColors().color;
  }
  
  public SearchOrbView.Colors getSearchAffordanceColors()
  {
    if (this.mSearchAffordanceColorSet) {
      return this.mSearchAffordanceColors;
    }
    if (this.mTitleViewAdapter == null) {
      throw new IllegalStateException("Fragment views not yet created");
    }
    return this.mTitleViewAdapter.getSearchAffordanceColors();
  }
  
  public CharSequence getTitle()
  {
    return this.mTitle;
  }
  
  TitleHelper getTitleHelper()
  {
    return this.mTitleHelper;
  }
  
  public View getTitleView()
  {
    return this.mTitleView;
  }
  
  public TitleViewAdapter getTitleViewAdapter()
  {
    return this.mTitleViewAdapter;
  }
  
  public void installTitleView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    paramLayoutInflater = onInflateTitleView(paramLayoutInflater, paramViewGroup, paramBundle);
    if (paramLayoutInflater != null)
    {
      paramViewGroup.addView(paramLayoutInflater);
      setTitleView(paramLayoutInflater.findViewById(R.id.browse_title_group));
      return;
    }
    setTitleView(null);
  }
  
  public final boolean isShowingTitle()
  {
    return this.mShowingTitle;
  }
  
  public void onDestroyView()
  {
    super.onDestroyView();
    this.mTitleHelper = null;
  }
  
  public View onInflateTitleView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    paramBundle = new TypedValue();
    if (paramViewGroup.getContext().getTheme().resolveAttribute(R.attr.browseTitleViewLayout, paramBundle, true)) {}
    for (int i = paramBundle.resourceId;; i = R.layout.lb_browse_title) {
      return paramLayoutInflater.inflate(i, paramViewGroup, false);
    }
  }
  
  public void onPause()
  {
    if (this.mTitleViewAdapter != null) {
      this.mTitleViewAdapter.setAnimationEnabled(false);
    }
    super.onPause();
  }
  
  public void onResume()
  {
    super.onResume();
    if (this.mTitleViewAdapter != null) {
      this.mTitleViewAdapter.setAnimationEnabled(true);
    }
  }
  
  public void onSaveInstanceState(Bundle paramBundle)
  {
    super.onSaveInstanceState(paramBundle);
    paramBundle.putBoolean("titleShow", this.mShowingTitle);
  }
  
  public void onStart()
  {
    super.onStart();
    if (this.mTitleViewAdapter != null)
    {
      showTitle(this.mShowingTitle);
      this.mTitleViewAdapter.setAnimationEnabled(true);
    }
  }
  
  public void onViewCreated(View paramView, Bundle paramBundle)
  {
    super.onViewCreated(paramView, paramBundle);
    if (paramBundle != null) {
      this.mShowingTitle = paramBundle.getBoolean("titleShow");
    }
    if ((this.mTitleView != null) && ((paramView instanceof ViewGroup)))
    {
      this.mTitleHelper = new TitleHelper((ViewGroup)paramView, this.mTitleView);
      this.mTitleHelper.showTitle(this.mShowingTitle);
    }
  }
  
  public void setBadgeDrawable(Drawable paramDrawable)
  {
    if (this.mBadgeDrawable != paramDrawable)
    {
      this.mBadgeDrawable = paramDrawable;
      if (this.mTitleViewAdapter != null) {
        this.mTitleViewAdapter.setBadgeDrawable(paramDrawable);
      }
    }
  }
  
  public void setOnSearchClickedListener(View.OnClickListener paramOnClickListener)
  {
    this.mExternalOnSearchClickedListener = paramOnClickListener;
    if (this.mTitleViewAdapter != null) {
      this.mTitleViewAdapter.setOnSearchClickedListener(paramOnClickListener);
    }
  }
  
  public void setSearchAffordanceColor(int paramInt)
  {
    setSearchAffordanceColors(new SearchOrbView.Colors(paramInt));
  }
  
  public void setSearchAffordanceColors(SearchOrbView.Colors paramColors)
  {
    this.mSearchAffordanceColors = paramColors;
    this.mSearchAffordanceColorSet = true;
    if (this.mTitleViewAdapter != null) {
      this.mTitleViewAdapter.setSearchAffordanceColors(this.mSearchAffordanceColors);
    }
  }
  
  public void setTitle(CharSequence paramCharSequence)
  {
    this.mTitle = paramCharSequence;
    if (this.mTitleViewAdapter != null) {
      this.mTitleViewAdapter.setTitle(paramCharSequence);
    }
  }
  
  public void setTitleView(View paramView)
  {
    this.mTitleView = paramView;
    if (this.mTitleView == null)
    {
      this.mTitleViewAdapter = null;
      this.mTitleHelper = null;
    }
    do
    {
      return;
      this.mTitleViewAdapter = ((TitleViewAdapter.Provider)this.mTitleView).getTitleViewAdapter();
      this.mTitleViewAdapter.setTitle(this.mTitle);
      this.mTitleViewAdapter.setBadgeDrawable(this.mBadgeDrawable);
      if (this.mSearchAffordanceColorSet) {
        this.mTitleViewAdapter.setSearchAffordanceColors(this.mSearchAffordanceColors);
      }
      if (this.mExternalOnSearchClickedListener != null) {
        setOnSearchClickedListener(this.mExternalOnSearchClickedListener);
      }
    } while (!(getView() instanceof ViewGroup));
    this.mTitleHelper = new TitleHelper((ViewGroup)getView(), this.mTitleView);
  }
  
  public void showTitle(int paramInt)
  {
    if (this.mTitleViewAdapter != null) {
      this.mTitleViewAdapter.updateComponentsVisibility(paramInt);
    }
    showTitle(true);
  }
  
  public void showTitle(boolean paramBoolean)
  {
    if (paramBoolean == this.mShowingTitle) {}
    do
    {
      return;
      this.mShowingTitle = paramBoolean;
    } while (this.mTitleHelper == null);
    this.mTitleHelper.showTitle(paramBoolean);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/app/BrandedSupportFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */