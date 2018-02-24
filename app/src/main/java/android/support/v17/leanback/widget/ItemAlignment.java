package android.support.v17.leanback.widget;

import android.view.View;

class ItemAlignment
{
  public final Axis horizontal = new Axis(0);
  private Axis mMainAxis = this.horizontal;
  private int mOrientation = 0;
  private Axis mSecondAxis = this.vertical;
  public final Axis vertical = new Axis(1);
  
  public final int getOrientation()
  {
    return this.mOrientation;
  }
  
  public final Axis mainAxis()
  {
    return this.mMainAxis;
  }
  
  public final Axis secondAxis()
  {
    return this.mSecondAxis;
  }
  
  public final void setOrientation(int paramInt)
  {
    this.mOrientation = paramInt;
    if (this.mOrientation == 0)
    {
      this.mMainAxis = this.horizontal;
      this.mSecondAxis = this.vertical;
      return;
    }
    this.mMainAxis = this.vertical;
    this.mSecondAxis = this.horizontal;
  }
  
  static final class Axis
    extends ItemAlignmentFacet.ItemAlignmentDef
  {
    private int mOrientation;
    
    Axis(int paramInt)
    {
      this.mOrientation = paramInt;
    }
    
    public int getAlignmentPosition(View paramView)
    {
      return ItemAlignmentFacetHelper.getAlignmentPosition(paramView, this, this.mOrientation);
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/ItemAlignment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */