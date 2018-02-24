package android.support.v17.leanback.widget;

import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.annotation.RestrictTo;
import android.view.View;

@RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
public final class BackgroundHelper
{
  static final BackgroundHelperVersionImpl sImpl = new BackgroundHelperStubImpl();
  
  static
  {
    if (Build.VERSION.SDK_INT >= 19)
    {
      sImpl = new BackgroundHelperKitkatImpl();
      return;
    }
  }
  
  public static void setBackgroundPreservingAlpha(View paramView, Drawable paramDrawable)
  {
    sImpl.setBackgroundPreservingAlpha(paramView, paramDrawable);
  }
  
  private static final class BackgroundHelperKitkatImpl
    implements BackgroundHelper.BackgroundHelperVersionImpl
  {
    public void setBackgroundPreservingAlpha(View paramView, Drawable paramDrawable)
    {
      BackgroundHelperKitkat.setBackgroundPreservingAlpha(paramView, paramDrawable);
    }
  }
  
  private static final class BackgroundHelperStubImpl
    implements BackgroundHelper.BackgroundHelperVersionImpl
  {
    public void setBackgroundPreservingAlpha(View paramView, Drawable paramDrawable)
    {
      paramView.setBackground(paramDrawable);
    }
  }
  
  static abstract interface BackgroundHelperVersionImpl
  {
    public abstract void setBackgroundPreservingAlpha(View paramView, Drawable paramDrawable);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/BackgroundHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */