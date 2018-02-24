package android.support.v17.leanback.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v17.leanback.R.attr;
import android.support.v17.leanback.R.id;
import android.support.v17.leanback.R.layout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class TitleView
  extends FrameLayout
  implements TitleViewAdapter.Provider
{
  private int flags = 6;
  private ImageView mBadgeView;
  private boolean mHasSearchListener = false;
  private SearchOrbView mSearchOrbView;
  private TextView mTextView;
  private final TitleViewAdapter mTitleViewAdapter = new TitleViewAdapter()
  {
    public Drawable getBadgeDrawable()
    {
      return TitleView.this.getBadgeDrawable();
    }
    
    public SearchOrbView.Colors getSearchAffordanceColors()
    {
      return TitleView.this.getSearchAffordanceColors();
    }
    
    public View getSearchAffordanceView()
    {
      return TitleView.this.getSearchAffordanceView();
    }
    
    public CharSequence getTitle()
    {
      return TitleView.this.getTitle();
    }
    
    public void setAnimationEnabled(boolean paramAnonymousBoolean)
    {
      TitleView.this.enableAnimation(paramAnonymousBoolean);
    }
    
    public void setBadgeDrawable(Drawable paramAnonymousDrawable)
    {
      TitleView.this.setBadgeDrawable(paramAnonymousDrawable);
    }
    
    public void setOnSearchClickedListener(View.OnClickListener paramAnonymousOnClickListener)
    {
      TitleView.this.setOnSearchClickedListener(paramAnonymousOnClickListener);
    }
    
    public void setSearchAffordanceColors(SearchOrbView.Colors paramAnonymousColors)
    {
      TitleView.this.setSearchAffordanceColors(paramAnonymousColors);
    }
    
    public void setTitle(CharSequence paramAnonymousCharSequence)
    {
      TitleView.this.setTitle(paramAnonymousCharSequence);
    }
    
    public void updateComponentsVisibility(int paramAnonymousInt)
    {
      TitleView.this.updateComponentsVisibility(paramAnonymousInt);
    }
  };
  
  public TitleView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public TitleView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, R.attr.browseTitleViewStyle);
  }
  
  public TitleView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    paramContext = LayoutInflater.from(paramContext).inflate(R.layout.lb_title_view, this);
    this.mBadgeView = ((ImageView)paramContext.findViewById(R.id.title_badge));
    this.mTextView = ((TextView)paramContext.findViewById(R.id.title_text));
    this.mSearchOrbView = ((SearchOrbView)paramContext.findViewById(R.id.title_orb));
    setClipToPadding(false);
    setClipChildren(false);
  }
  
  private void updateBadgeVisibility()
  {
    if (this.mBadgeView.getDrawable() != null)
    {
      this.mBadgeView.setVisibility(0);
      this.mTextView.setVisibility(8);
      return;
    }
    this.mBadgeView.setVisibility(8);
    this.mTextView.setVisibility(0);
  }
  
  private void updateSearchOrbViewVisiblity()
  {
    int j = 4;
    int i = j;
    if (this.mHasSearchListener)
    {
      i = j;
      if ((this.flags & 0x4) == 4) {
        i = 0;
      }
    }
    this.mSearchOrbView.setVisibility(i);
  }
  
  public void enableAnimation(boolean paramBoolean)
  {
    SearchOrbView localSearchOrbView = this.mSearchOrbView;
    if ((paramBoolean) && (this.mSearchOrbView.hasFocus())) {}
    for (paramBoolean = true;; paramBoolean = false)
    {
      localSearchOrbView.enableOrbColorAnimation(paramBoolean);
      return;
    }
  }
  
  public Drawable getBadgeDrawable()
  {
    return this.mBadgeView.getDrawable();
  }
  
  public SearchOrbView.Colors getSearchAffordanceColors()
  {
    return this.mSearchOrbView.getOrbColors();
  }
  
  public View getSearchAffordanceView()
  {
    return this.mSearchOrbView;
  }
  
  public CharSequence getTitle()
  {
    return this.mTextView.getText();
  }
  
  public TitleViewAdapter getTitleViewAdapter()
  {
    return this.mTitleViewAdapter;
  }
  
  public void setBadgeDrawable(Drawable paramDrawable)
  {
    this.mBadgeView.setImageDrawable(paramDrawable);
    updateBadgeVisibility();
  }
  
  public void setOnSearchClickedListener(View.OnClickListener paramOnClickListener)
  {
    if (paramOnClickListener != null) {}
    for (boolean bool = true;; bool = false)
    {
      this.mHasSearchListener = bool;
      this.mSearchOrbView.setOnOrbClickedListener(paramOnClickListener);
      updateSearchOrbViewVisiblity();
      return;
    }
  }
  
  public void setSearchAffordanceColors(SearchOrbView.Colors paramColors)
  {
    this.mSearchOrbView.setOrbColors(paramColors);
  }
  
  public void setTitle(CharSequence paramCharSequence)
  {
    this.mTextView.setText(paramCharSequence);
    updateBadgeVisibility();
  }
  
  public void updateComponentsVisibility(int paramInt)
  {
    this.flags = paramInt;
    if ((paramInt & 0x2) == 2) {
      updateBadgeVisibility();
    }
    for (;;)
    {
      updateSearchOrbViewVisiblity();
      return;
      this.mBadgeView.setVisibility(8);
      this.mTextView.setVisibility(8);
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/TitleView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */