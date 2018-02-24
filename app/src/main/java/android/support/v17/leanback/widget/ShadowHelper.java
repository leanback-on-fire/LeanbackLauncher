package android.support.v17.leanback.widget;

import android.os.Build.VERSION;
import android.view.View;

final class ShadowHelper
{
  static final ShadowHelper sInstance = new ShadowHelper();
  ShadowHelperVersionImpl mImpl;
  boolean mSupportsDynamicShadow;
  
  private ShadowHelper()
  {
    if (Build.VERSION.SDK_INT >= 21)
    {
      this.mSupportsDynamicShadow = true;
      this.mImpl = new ShadowHelperApi21Impl();
      return;
    }
    this.mImpl = new ShadowHelperStubImpl();
  }
  
  public static ShadowHelper getInstance()
  {
    return sInstance;
  }
  
  public Object addDynamicShadow(View paramView, float paramFloat1, float paramFloat2, int paramInt)
  {
    return this.mImpl.addDynamicShadow(paramView, paramFloat1, paramFloat2, paramInt);
  }
  
  public void setShadowFocusLevel(Object paramObject, float paramFloat)
  {
    this.mImpl.setShadowFocusLevel(paramObject, paramFloat);
  }
  
  public void setZ(View paramView, float paramFloat)
  {
    this.mImpl.setZ(paramView, paramFloat);
  }
  
  public boolean supportsDynamicShadow()
  {
    return this.mSupportsDynamicShadow;
  }
  
  private static final class ShadowHelperApi21Impl
    implements ShadowHelper.ShadowHelperVersionImpl
  {
    public Object addDynamicShadow(View paramView, float paramFloat1, float paramFloat2, int paramInt)
    {
      return ShadowHelperApi21.addDynamicShadow(paramView, paramFloat1, paramFloat2, paramInt);
    }
    
    public void setShadowFocusLevel(Object paramObject, float paramFloat)
    {
      ShadowHelperApi21.setShadowFocusLevel(paramObject, paramFloat);
    }
    
    public void setZ(View paramView, float paramFloat)
    {
      ShadowHelperApi21.setZ(paramView, paramFloat);
    }
  }
  
  private static final class ShadowHelperStubImpl
    implements ShadowHelper.ShadowHelperVersionImpl
  {
    public Object addDynamicShadow(View paramView, float paramFloat1, float paramFloat2, int paramInt)
    {
      return null;
    }
    
    public void setShadowFocusLevel(Object paramObject, float paramFloat) {}
    
    public void setZ(View paramView, float paramFloat) {}
  }
  
  static abstract interface ShadowHelperVersionImpl
  {
    public abstract Object addDynamicShadow(View paramView, float paramFloat1, float paramFloat2, int paramInt);
    
    public abstract void setShadowFocusLevel(Object paramObject, float paramFloat);
    
    public abstract void setZ(View paramView, float paramFloat);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/ShadowHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */