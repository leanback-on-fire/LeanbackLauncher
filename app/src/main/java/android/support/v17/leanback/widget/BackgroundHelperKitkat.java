package android.support.v17.leanback.widget;

import android.graphics.drawable.Drawable;
import android.support.annotation.RequiresApi;
import android.view.View;

@RequiresApi(19)
class BackgroundHelperKitkat
{
  public static void setBackgroundPreservingAlpha(View paramView, Drawable paramDrawable)
  {
    if (paramView.getBackground() != null) {
      paramDrawable.setAlpha(paramView.getBackground().getAlpha());
    }
    paramView.setBackground(paramDrawable);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/BackgroundHelperKitkat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */