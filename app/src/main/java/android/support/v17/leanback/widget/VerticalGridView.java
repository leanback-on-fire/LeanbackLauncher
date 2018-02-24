package android.support.v17.leanback.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v17.leanback.R.styleable;
import android.util.AttributeSet;

public class VerticalGridView
  extends BaseGridView
{
  public VerticalGridView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public VerticalGridView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public VerticalGridView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    this.mLayoutManager.setOrientation(1);
    initAttributes(paramContext, paramAttributeSet);
  }
  
  protected void initAttributes(Context paramContext, AttributeSet paramAttributeSet)
  {
    initBaseGridViewAttributes(paramContext, paramAttributeSet);
    paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.lbVerticalGridView);
    setColumnWidth(paramContext);
    setNumColumns(paramContext.getInt(R.styleable.lbVerticalGridView_numberOfColumns, 1));
    paramContext.recycle();
  }
  
  public void setColumnWidth(int paramInt)
  {
    this.mLayoutManager.setRowHeight(paramInt);
    requestLayout();
  }
  
  void setColumnWidth(TypedArray paramTypedArray)
  {
    if (paramTypedArray.peekValue(R.styleable.lbVerticalGridView_columnWidth) != null) {
      setColumnWidth(paramTypedArray.getLayoutDimension(R.styleable.lbVerticalGridView_columnWidth, 0));
    }
  }
  
  public void setNumColumns(int paramInt)
  {
    this.mLayoutManager.setNumRows(paramInt);
    requestLayout();
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/VerticalGridView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */