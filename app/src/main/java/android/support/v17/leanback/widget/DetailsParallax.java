package android.support.v17.leanback.widget;

import android.support.v17.leanback.R.id;

public class DetailsParallax
  extends RecyclerViewParallax
{
  final Parallax.IntProperty mFrameBottom = ((RecyclerViewParallax.ChildPositionProperty)addProperty("overviewRowBottom")).adapterPosition(0).viewId(R.id.details_frame).fraction(1.0F);
  final Parallax.IntProperty mFrameTop = ((RecyclerViewParallax.ChildPositionProperty)addProperty("overviewRowTop")).adapterPosition(0).viewId(R.id.details_frame);
  
  public Parallax.IntProperty getOverviewRowBottom()
  {
    return this.mFrameBottom;
  }
  
  public Parallax.IntProperty getOverviewRowTop()
  {
    return this.mFrameTop;
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/DetailsParallax.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */