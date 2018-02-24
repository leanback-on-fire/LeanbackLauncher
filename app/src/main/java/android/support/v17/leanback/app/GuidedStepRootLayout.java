package android.support.v17.leanback.app;

import android.content.Context;
import android.support.v17.leanback.widget.Util;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

class GuidedStepRootLayout
  extends LinearLayout
{
  private boolean mFocusOutEnd = false;
  private boolean mFocusOutStart = false;
  
  public GuidedStepRootLayout(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  public GuidedStepRootLayout(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
  }
  
  public View focusSearch(View paramView, int paramInt)
  {
    View localView = super.focusSearch(paramView, paramInt);
    if (((paramInt != 17) && (paramInt != 66)) || (Util.isDescendant(this, localView))) {
      break label42;
    }
    label42:
    label57:
    do
    {
      do
      {
        return localView;
        if (getLayoutDirection() != 0) {
          break;
        }
        if (paramInt != 17) {
          break label57;
        }
      } while (this.mFocusOutStart);
      return paramView;
      if (paramInt == 66) {
        break;
      }
    } while (this.mFocusOutEnd);
    return paramView;
  }
  
  public void setFocusOutEnd(boolean paramBoolean)
  {
    this.mFocusOutEnd = paramBoolean;
  }
  
  public void setFocusOutStart(boolean paramBoolean)
  {
    this.mFocusOutStart = paramBoolean;
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/app/GuidedStepRootLayout.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */