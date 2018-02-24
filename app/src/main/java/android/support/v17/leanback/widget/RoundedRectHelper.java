package android.support.v17.leanback.widget;

import android.content.res.Resources;
import android.os.Build.VERSION;
import android.support.v17.leanback.R.dimen;
import android.view.View;

final class RoundedRectHelper
{
  private static final RoundedRectHelper sInstance = new RoundedRectHelper();
  private final Impl mImpl;
  
  private RoundedRectHelper()
  {
    if (supportsRoundedCorner())
    {
      this.mImpl = new Api21Impl();
      return;
    }
    this.mImpl = new StubImpl();
  }
  
  public static RoundedRectHelper getInstance()
  {
    return sInstance;
  }
  
  public static boolean supportsRoundedCorner()
  {
    return Build.VERSION.SDK_INT >= 21;
  }
  
  public void setClipToRoundedOutline(View paramView, boolean paramBoolean)
  {
    this.mImpl.setClipToRoundedOutline(paramView, paramBoolean, paramView.getResources().getDimensionPixelSize(R.dimen.lb_rounded_rect_corner_radius));
  }
  
  public void setClipToRoundedOutline(View paramView, boolean paramBoolean, int paramInt)
  {
    this.mImpl.setClipToRoundedOutline(paramView, paramBoolean, paramInt);
  }
  
  private static final class Api21Impl
    implements RoundedRectHelper.Impl
  {
    public void setClipToRoundedOutline(View paramView, boolean paramBoolean, int paramInt)
    {
      RoundedRectHelperApi21.setClipToRoundedOutline(paramView, paramBoolean, paramInt);
    }
  }
  
  static abstract interface Impl
  {
    public abstract void setClipToRoundedOutline(View paramView, boolean paramBoolean, int paramInt);
  }
  
  private static final class StubImpl
    implements RoundedRectHelper.Impl
  {
    public void setClipToRoundedOutline(View paramView, boolean paramBoolean, int paramInt) {}
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/RoundedRectHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */