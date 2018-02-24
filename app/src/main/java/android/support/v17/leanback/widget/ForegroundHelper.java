package android.support.v17.leanback.widget;

import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.view.View;

final class ForegroundHelper
{
  static final ForegroundHelper sInstance = new ForegroundHelper();
  ForegroundHelperVersionImpl mImpl;
  
  private ForegroundHelper()
  {
    if (supportsForeground())
    {
      this.mImpl = new ForegroundHelperApi23Impl();
      return;
    }
    this.mImpl = new ForegroundHelperStubImpl();
  }
  
  public static ForegroundHelper getInstance()
  {
    return sInstance;
  }
  
  public static boolean supportsForeground()
  {
    return Build.VERSION.SDK_INT >= 23;
  }
  
  public Drawable getForeground(View paramView)
  {
    return this.mImpl.getForeground(paramView);
  }
  
  public void setForeground(View paramView, Drawable paramDrawable)
  {
    this.mImpl.setForeground(paramView, paramDrawable);
  }
  
  private static final class ForegroundHelperApi23Impl
    implements ForegroundHelper.ForegroundHelperVersionImpl
  {
    public Drawable getForeground(View paramView)
    {
      return ForegroundHelperApi23.getForeground(paramView);
    }
    
    public void setForeground(View paramView, Drawable paramDrawable)
    {
      ForegroundHelperApi23.setForeground(paramView, paramDrawable);
    }
  }
  
  private static final class ForegroundHelperStubImpl
    implements ForegroundHelper.ForegroundHelperVersionImpl
  {
    public Drawable getForeground(View paramView)
    {
      return null;
    }
    
    public void setForeground(View paramView, Drawable paramDrawable) {}
  }
  
  static abstract interface ForegroundHelperVersionImpl
  {
    public abstract Drawable getForeground(View paramView);
    
    public abstract void setForeground(View paramView, Drawable paramDrawable);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/ForegroundHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */