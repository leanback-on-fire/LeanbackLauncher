package android.support.v17.leanback.widget;

import android.content.res.Resources;
import android.graphics.Paint;
import android.support.annotation.RestrictTo;
import android.support.v17.leanback.R.fraction;
import android.support.v17.leanback.R.id;
import android.support.v17.leanback.R.layout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class RowHeaderPresenter
  extends Presenter
{
  private final boolean mAnimateSelect;
  private final Paint mFontMeasurePaint = new Paint(1);
  private final int mLayoutResourceId;
  private boolean mNullItemVisibilityGone;
  
  public RowHeaderPresenter()
  {
    this(R.layout.lb_row_header);
  }
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  public RowHeaderPresenter(int paramInt)
  {
    this(paramInt, true);
  }
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  public RowHeaderPresenter(int paramInt, boolean paramBoolean)
  {
    this.mLayoutResourceId = paramInt;
    this.mAnimateSelect = paramBoolean;
  }
  
  protected static float getFontDescent(TextView paramTextView, Paint paramPaint)
  {
    if (paramPaint.getTextSize() != paramTextView.getTextSize()) {
      paramPaint.setTextSize(paramTextView.getTextSize());
    }
    if (paramPaint.getTypeface() != paramTextView.getTypeface()) {
      paramPaint.setTypeface(paramTextView.getTypeface());
    }
    return paramPaint.descent();
  }
  
  public int getSpaceUnderBaseline(ViewHolder paramViewHolder)
  {
    int j = paramViewHolder.view.getPaddingBottom();
    int i = j;
    if ((paramViewHolder.view instanceof TextView)) {
      i = j + (int)getFontDescent((TextView)paramViewHolder.view, this.mFontMeasurePaint);
    }
    return i;
  }
  
  public boolean isNullItemVisibilityGone()
  {
    return this.mNullItemVisibilityGone;
  }
  
  public void onBindViewHolder(Presenter.ViewHolder paramViewHolder, Object paramObject)
  {
    if (paramObject == null) {}
    ViewHolder localViewHolder;
    for (paramObject = null;; paramObject = ((Row)paramObject).getHeaderItem())
    {
      localViewHolder = (ViewHolder)paramViewHolder;
      if (paramObject != null) {
        break;
      }
      if (localViewHolder.mTitleView != null) {
        localViewHolder.mTitleView.setText(null);
      }
      if (localViewHolder.mDescriptionView != null) {
        localViewHolder.mDescriptionView.setText(null);
      }
      paramViewHolder.view.setContentDescription(null);
      if (this.mNullItemVisibilityGone) {
        paramViewHolder.view.setVisibility(8);
      }
      return;
    }
    if (localViewHolder.mTitleView != null) {
      localViewHolder.mTitleView.setText(((HeaderItem)paramObject).getName());
    }
    if (localViewHolder.mDescriptionView != null)
    {
      if (!TextUtils.isEmpty(((HeaderItem)paramObject).getDescription())) {
        break label156;
      }
      localViewHolder.mDescriptionView.setVisibility(8);
    }
    for (;;)
    {
      localViewHolder.mDescriptionView.setText(((HeaderItem)paramObject).getDescription());
      paramViewHolder.view.setContentDescription(((HeaderItem)paramObject).getContentDescription());
      paramViewHolder.view.setVisibility(0);
      return;
      label156:
      localViewHolder.mDescriptionView.setVisibility(0);
    }
  }
  
  public Presenter.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup)
  {
    paramViewGroup = new ViewHolder(LayoutInflater.from(paramViewGroup.getContext()).inflate(this.mLayoutResourceId, paramViewGroup, false));
    if (this.mAnimateSelect) {
      setSelectLevel(paramViewGroup, 0.0F);
    }
    return paramViewGroup;
  }
  
  protected void onSelectLevelChanged(ViewHolder paramViewHolder)
  {
    if (this.mAnimateSelect) {
      paramViewHolder.view.setAlpha(paramViewHolder.mUnselectAlpha + paramViewHolder.mSelectLevel * (1.0F - paramViewHolder.mUnselectAlpha));
    }
  }
  
  public void onUnbindViewHolder(Presenter.ViewHolder paramViewHolder)
  {
    ViewHolder localViewHolder = (ViewHolder)paramViewHolder;
    if (localViewHolder.mTitleView != null) {
      localViewHolder.mTitleView.setText(null);
    }
    if (localViewHolder.mDescriptionView != null) {
      localViewHolder.mDescriptionView.setText(null);
    }
    if (this.mAnimateSelect) {
      setSelectLevel((ViewHolder)paramViewHolder, 0.0F);
    }
  }
  
  public void setNullItemVisibilityGone(boolean paramBoolean)
  {
    this.mNullItemVisibilityGone = paramBoolean;
  }
  
  public final void setSelectLevel(ViewHolder paramViewHolder, float paramFloat)
  {
    paramViewHolder.mSelectLevel = paramFloat;
    onSelectLevelChanged(paramViewHolder);
  }
  
  public static class ViewHolder
    extends Presenter.ViewHolder
  {
    TextView mDescriptionView;
    int mOriginalTextColor;
    float mSelectLevel;
    RowHeaderView mTitleView;
    float mUnselectAlpha;
    
    @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
    public ViewHolder(RowHeaderView paramRowHeaderView)
    {
      super();
      this.mTitleView = paramRowHeaderView;
      initColors();
    }
    
    public ViewHolder(View paramView)
    {
      super();
      this.mTitleView = ((RowHeaderView)paramView.findViewById(R.id.row_header));
      this.mDescriptionView = ((TextView)paramView.findViewById(R.id.row_header_description));
      initColors();
    }
    
    public final float getSelectLevel()
    {
      return this.mSelectLevel;
    }
    
    void initColors()
    {
      if (this.mTitleView != null) {
        this.mOriginalTextColor = this.mTitleView.getCurrentTextColor();
      }
      this.mUnselectAlpha = this.view.getResources().getFraction(R.fraction.lb_browse_header_unselect_alpha, 1, 1);
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/RowHeaderPresenter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */