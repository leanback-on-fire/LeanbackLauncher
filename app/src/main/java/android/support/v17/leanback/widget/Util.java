package android.support.v17.leanback.widget;

import android.support.annotation.RestrictTo;
import android.view.View;
import android.view.ViewGroup;

@RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
public class Util
{
  public static boolean isDescendant(ViewGroup paramViewGroup, View paramView)
  {
    boolean bool2 = false;
    for (;;)
    {
      boolean bool1 = bool2;
      if (paramView != null)
      {
        if (paramView != paramViewGroup) {
          break label17;
        }
        bool1 = true;
      }
      label17:
      do
      {
        return bool1;
        paramView = paramView.getParent();
        bool1 = bool2;
      } while (!(paramView instanceof View));
      paramView = (View)paramView;
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/Util.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */