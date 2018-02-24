package android.support.v17.leanback.widget;

import android.content.Context;
import android.support.v17.leanback.R.id;
import android.support.v17.leanback.R.layout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

public final class ListRowView
  extends LinearLayout
{
  private HorizontalGridView mGridView;
  
  public ListRowView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public ListRowView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public ListRowView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    LayoutInflater.from(paramContext).inflate(R.layout.lb_list_row, this);
    this.mGridView = ((HorizontalGridView)findViewById(R.id.row_content));
    this.mGridView.setHasFixedSize(false);
    setOrientation(1);
    setDescendantFocusability(262144);
  }
  
  public HorizontalGridView getGridView()
  {
    return this.mGridView;
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/ListRowView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */