package android.support.v17.leanback.widget;

import android.os.Build.VERSION;
import android.view.ViewGroup;

final class StaticShadowHelper
{
  static final StaticShadowHelper sInstance = new StaticShadowHelper();
  ShadowHelperVersionImpl mImpl;
  boolean mSupportsShadow;
  
  private StaticShadowHelper()
  {
    if (Build.VERSION.SDK_INT >= 21)
    {
      this.mSupportsShadow = true;
      this.mImpl = new ShadowHelperJbmr2Impl();
      return;
    }
    this.mSupportsShadow = false;
    this.mImpl = new ShadowHelperStubImpl();
  }
  
  public static StaticShadowHelper getInstance()
  {
    return sInstance;
  }
  
  public Object addStaticShadow(ViewGroup paramViewGroup)
  {
    return this.mImpl.addStaticShadow(paramViewGroup);
  }
  
  public void prepareParent(ViewGroup paramViewGroup)
  {
    this.mImpl.prepareParent(paramViewGroup);
  }
  
  public void setShadowFocusLevel(Object paramObject, float paramFloat)
  {
    this.mImpl.setShadowFocusLevel(paramObject, paramFloat);
  }
  
  public boolean supportsShadow()
  {
    return this.mSupportsShadow;
  }
  
  private static final class ShadowHelperJbmr2Impl
    implements StaticShadowHelper.ShadowHelperVersionImpl
  {
    public Object addStaticShadow(ViewGroup paramViewGroup)
    {
      return ShadowHelperJbmr2.addShadow(paramViewGroup);
    }
    
    public void prepareParent(ViewGroup paramViewGroup)
    {
      ShadowHelperJbmr2.prepareParent(paramViewGroup);
    }
    
    public void setShadowFocusLevel(Object paramObject, float paramFloat)
    {
      ShadowHelperJbmr2.setShadowFocusLevel(paramObject, paramFloat);
    }
  }
  
  private static final class ShadowHelperStubImpl
    implements StaticShadowHelper.ShadowHelperVersionImpl
  {
    public Object addStaticShadow(ViewGroup paramViewGroup)
    {
      return null;
    }
    
    public void prepareParent(ViewGroup paramViewGroup) {}
    
    public void setShadowFocusLevel(Object paramObject, float paramFloat) {}
  }
  
  static abstract interface ShadowHelperVersionImpl
  {
    public abstract Object addStaticShadow(ViewGroup paramViewGroup);
    
    public abstract void prepareParent(ViewGroup paramViewGroup);
    
    public abstract void setShadowFocusLevel(Object paramObject, float paramFloat);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/StaticShadowHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */