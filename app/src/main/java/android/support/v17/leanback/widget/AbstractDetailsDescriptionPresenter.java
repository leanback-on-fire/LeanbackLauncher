package android.support.v17.leanback.widget;

import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.support.v17.leanback.R.dimen;
import android.support.v17.leanback.R.id;
import android.support.v17.leanback.R.integer;
import android.support.v17.leanback.R.layout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLayoutChangeListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.TextView;

public abstract class AbstractDetailsDescriptionPresenter
  extends Presenter
{
  private void setTopMargin(TextView paramTextView, int paramInt)
  {
    ViewGroup.MarginLayoutParams localMarginLayoutParams = (ViewGroup.MarginLayoutParams)paramTextView.getLayoutParams();
    localMarginLayoutParams.topMargin = paramInt;
    paramTextView.setLayoutParams(localMarginLayoutParams);
  }
  
  protected abstract void onBindDescription(ViewHolder paramViewHolder, Object paramObject);
  
  public final void onBindViewHolder(Presenter.ViewHolder paramViewHolder, Object paramObject)
  {
    paramViewHolder = (ViewHolder)paramViewHolder;
    onBindDescription(paramViewHolder, paramObject);
    int i = 1;
    int j;
    if (TextUtils.isEmpty(paramViewHolder.mTitle.getText()))
    {
      paramViewHolder.mTitle.setVisibility(8);
      i = 0;
      setTopMargin(paramViewHolder.mTitle, paramViewHolder.mTitleMargin);
      j = 1;
      if (!TextUtils.isEmpty(paramViewHolder.mSubtitle.getText())) {
        break label157;
      }
      paramViewHolder.mSubtitle.setVisibility(8);
      j = 0;
    }
    for (;;)
    {
      if (!TextUtils.isEmpty(paramViewHolder.mBody.getText())) {
        break label212;
      }
      paramViewHolder.mBody.setVisibility(8);
      return;
      paramViewHolder.mTitle.setVisibility(0);
      paramViewHolder.mTitle.setLineSpacing(paramViewHolder.mTitleLineSpacing - paramViewHolder.mTitle.getLineHeight() + paramViewHolder.mTitle.getLineSpacingExtra(), paramViewHolder.mTitle.getLineSpacingMultiplier());
      paramViewHolder.mTitle.setMaxLines(paramViewHolder.mTitleMaxLines);
      break;
      label157:
      paramViewHolder.mSubtitle.setVisibility(0);
      if (i != 0) {
        setTopMargin(paramViewHolder.mSubtitle, paramViewHolder.mUnderTitleBaselineMargin + paramViewHolder.mSubtitleFontMetricsInt.ascent - paramViewHolder.mTitleFontMetricsInt.descent);
      } else {
        setTopMargin(paramViewHolder.mSubtitle, 0);
      }
    }
    label212:
    paramViewHolder.mBody.setVisibility(0);
    paramViewHolder.mBody.setLineSpacing(paramViewHolder.mBodyLineSpacing - paramViewHolder.mBody.getLineHeight() + paramViewHolder.mBody.getLineSpacingExtra(), paramViewHolder.mBody.getLineSpacingMultiplier());
    if (j != 0)
    {
      setTopMargin(paramViewHolder.mBody, paramViewHolder.mUnderSubtitleBaselineMargin + paramViewHolder.mBodyFontMetricsInt.ascent - paramViewHolder.mSubtitleFontMetricsInt.descent);
      return;
    }
    if (i != 0)
    {
      setTopMargin(paramViewHolder.mBody, paramViewHolder.mUnderTitleBaselineMargin + paramViewHolder.mBodyFontMetricsInt.ascent - paramViewHolder.mTitleFontMetricsInt.descent);
      return;
    }
    setTopMargin(paramViewHolder.mBody, 0);
  }
  
  public final ViewHolder onCreateViewHolder(ViewGroup paramViewGroup)
  {
    return new ViewHolder(LayoutInflater.from(paramViewGroup.getContext()).inflate(R.layout.lb_details_description, paramViewGroup, false));
  }
  
  public void onUnbindViewHolder(Presenter.ViewHolder paramViewHolder) {}
  
  public void onViewAttachedToWindow(Presenter.ViewHolder paramViewHolder)
  {
    ((ViewHolder)paramViewHolder).addPreDrawListener();
    super.onViewAttachedToWindow(paramViewHolder);
  }
  
  public void onViewDetachedFromWindow(Presenter.ViewHolder paramViewHolder)
  {
    ((ViewHolder)paramViewHolder).removePreDrawListener();
    super.onViewDetachedFromWindow(paramViewHolder);
  }
  
  public static class ViewHolder
    extends Presenter.ViewHolder
  {
    final TextView mBody;
    final Paint.FontMetricsInt mBodyFontMetricsInt;
    final int mBodyLineSpacing;
    final int mBodyMaxLines;
    final int mBodyMinLines;
    private ViewTreeObserver.OnPreDrawListener mPreDrawListener;
    final TextView mSubtitle;
    final Paint.FontMetricsInt mSubtitleFontMetricsInt;
    final TextView mTitle;
    final Paint.FontMetricsInt mTitleFontMetricsInt;
    final int mTitleLineSpacing;
    final int mTitleMargin;
    final int mTitleMaxLines;
    final int mUnderSubtitleBaselineMargin;
    final int mUnderTitleBaselineMargin;
    
    public ViewHolder(View paramView)
    {
      super();
      this.mTitle = ((TextView)paramView.findViewById(R.id.lb_details_description_title));
      this.mSubtitle = ((TextView)paramView.findViewById(R.id.lb_details_description_subtitle));
      this.mBody = ((TextView)paramView.findViewById(R.id.lb_details_description_body));
      Paint.FontMetricsInt localFontMetricsInt = getFontMetricsInt(this.mTitle);
      int i = paramView.getResources().getDimensionPixelSize(R.dimen.lb_details_description_title_baseline);
      this.mTitleMargin = (localFontMetricsInt.ascent + i);
      this.mUnderTitleBaselineMargin = paramView.getResources().getDimensionPixelSize(R.dimen.lb_details_description_under_title_baseline_margin);
      this.mUnderSubtitleBaselineMargin = paramView.getResources().getDimensionPixelSize(R.dimen.lb_details_description_under_subtitle_baseline_margin);
      this.mTitleLineSpacing = paramView.getResources().getDimensionPixelSize(R.dimen.lb_details_description_title_line_spacing);
      this.mBodyLineSpacing = paramView.getResources().getDimensionPixelSize(R.dimen.lb_details_description_body_line_spacing);
      this.mBodyMaxLines = paramView.getResources().getInteger(R.integer.lb_details_description_body_max_lines);
      this.mBodyMinLines = paramView.getResources().getInteger(R.integer.lb_details_description_body_min_lines);
      this.mTitleMaxLines = this.mTitle.getMaxLines();
      this.mTitleFontMetricsInt = getFontMetricsInt(this.mTitle);
      this.mSubtitleFontMetricsInt = getFontMetricsInt(this.mSubtitle);
      this.mBodyFontMetricsInt = getFontMetricsInt(this.mBody);
      this.mTitle.addOnLayoutChangeListener(new View.OnLayoutChangeListener()
      {
        public void onLayoutChange(View paramAnonymousView, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3, int paramAnonymousInt4, int paramAnonymousInt5, int paramAnonymousInt6, int paramAnonymousInt7, int paramAnonymousInt8)
        {
          AbstractDetailsDescriptionPresenter.ViewHolder.this.addPreDrawListener();
        }
      });
    }
    
    private Paint.FontMetricsInt getFontMetricsInt(TextView paramTextView)
    {
      Paint localPaint = new Paint(1);
      localPaint.setTextSize(paramTextView.getTextSize());
      localPaint.setTypeface(paramTextView.getTypeface());
      return localPaint.getFontMetricsInt();
    }
    
    void addPreDrawListener()
    {
      if (this.mPreDrawListener != null) {
        return;
      }
      this.mPreDrawListener = new ViewTreeObserver.OnPreDrawListener()
      {
        public boolean onPreDraw()
        {
          if ((AbstractDetailsDescriptionPresenter.ViewHolder.this.mSubtitle.getVisibility() == 0) && (AbstractDetailsDescriptionPresenter.ViewHolder.this.mSubtitle.getTop() > AbstractDetailsDescriptionPresenter.ViewHolder.this.view.getHeight()) && (AbstractDetailsDescriptionPresenter.ViewHolder.this.mTitle.getLineCount() > 1))
          {
            AbstractDetailsDescriptionPresenter.ViewHolder.this.mTitle.setMaxLines(AbstractDetailsDescriptionPresenter.ViewHolder.this.mTitle.getLineCount() - 1);
            return false;
          }
          if (AbstractDetailsDescriptionPresenter.ViewHolder.this.mTitle.getLineCount() > 1) {}
          for (int i = AbstractDetailsDescriptionPresenter.ViewHolder.this.mBodyMinLines; AbstractDetailsDescriptionPresenter.ViewHolder.this.mBody.getMaxLines() != i; i = AbstractDetailsDescriptionPresenter.ViewHolder.this.mBodyMaxLines)
          {
            AbstractDetailsDescriptionPresenter.ViewHolder.this.mBody.setMaxLines(i);
            return false;
          }
          AbstractDetailsDescriptionPresenter.ViewHolder.this.removePreDrawListener();
          return true;
        }
      };
      this.view.getViewTreeObserver().addOnPreDrawListener(this.mPreDrawListener);
    }
    
    public TextView getBody()
    {
      return this.mBody;
    }
    
    public TextView getSubtitle()
    {
      return this.mSubtitle;
    }
    
    public TextView getTitle()
    {
      return this.mTitle;
    }
    
    void removePreDrawListener()
    {
      if (this.mPreDrawListener != null)
      {
        this.view.getViewTreeObserver().removeOnPreDrawListener(this.mPreDrawListener);
        this.mPreDrawListener = null;
      }
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/AbstractDetailsDescriptionPresenter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */