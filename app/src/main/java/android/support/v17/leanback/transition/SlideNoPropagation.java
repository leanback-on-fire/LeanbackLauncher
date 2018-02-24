package android.support.v17.leanback.transition;

import android.content.Context;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;
import android.transition.Slide;
import android.util.AttributeSet;

@RequiresApi(21)
@RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
public class SlideNoPropagation
  extends Slide
{
  public SlideNoPropagation() {}
  
  public SlideNoPropagation(int paramInt)
  {
    super(paramInt);
  }
  
  public SlideNoPropagation(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  public void setSlideEdge(int paramInt)
  {
    super.setSlideEdge(paramInt);
    setPropagation(null);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/transition/SlideNoPropagation.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */