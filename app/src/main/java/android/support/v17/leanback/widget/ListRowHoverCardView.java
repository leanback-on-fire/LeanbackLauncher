package android.support.v17.leanback.widget;

import android.content.Context;
import android.support.v17.leanback.R.id;
import android.support.v17.leanback.R.layout;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public final class ListRowHoverCardView
  extends LinearLayout
{
  private final TextView mDescriptionView;
  private final TextView mTitleView;
  
  public ListRowHoverCardView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public ListRowHoverCardView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public ListRowHoverCardView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    LayoutInflater.from(paramContext).inflate(R.layout.lb_list_row_hovercard, this);
    this.mTitleView = ((TextView)findViewById(R.id.title));
    this.mDescriptionView = ((TextView)findViewById(R.id.description));
  }
  
  public final CharSequence getDescription()
  {
    return this.mDescriptionView.getText();
  }
  
  public final CharSequence getTitle()
  {
    return this.mTitleView.getText();
  }
  
  public final void setDescription(CharSequence paramCharSequence)
  {
    if (!TextUtils.isEmpty(paramCharSequence))
    {
      this.mDescriptionView.setText(paramCharSequence);
      this.mDescriptionView.setVisibility(0);
      return;
    }
    this.mDescriptionView.setVisibility(8);
  }
  
  public final void setTitle(CharSequence paramCharSequence)
  {
    if (!TextUtils.isEmpty(paramCharSequence))
    {
      this.mTitleView.setText(paramCharSequence);
      this.mTitleView.setVisibility(0);
      return;
    }
    this.mTitleView.setVisibility(8);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/ListRowHoverCardView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */