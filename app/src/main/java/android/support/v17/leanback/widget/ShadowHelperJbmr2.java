package android.support.v17.leanback.widget;

import android.support.annotation.RequiresApi;
import android.support.v17.leanback.R.id;
import android.support.v17.leanback.R.layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

@RequiresApi(18)
class ShadowHelperJbmr2
{
  public static Object addShadow(ViewGroup paramViewGroup)
  {
    paramViewGroup.setLayoutMode(1);
    LayoutInflater.from(paramViewGroup.getContext()).inflate(R.layout.lb_shadow, paramViewGroup, true);
    ShadowImpl localShadowImpl = new ShadowImpl();
    localShadowImpl.mNormalShadow = paramViewGroup.findViewById(R.id.lb_shadow_normal);
    localShadowImpl.mFocusShadow = paramViewGroup.findViewById(R.id.lb_shadow_focused);
    return localShadowImpl;
  }
  
  public static void prepareParent(ViewGroup paramViewGroup)
  {
    paramViewGroup.setLayoutMode(1);
  }
  
  public static void setShadowFocusLevel(Object paramObject, float paramFloat)
  {
    paramObject = (ShadowImpl)paramObject;
    ((ShadowImpl)paramObject).mNormalShadow.setAlpha(1.0F - paramFloat);
    ((ShadowImpl)paramObject).mFocusShadow.setAlpha(paramFloat);
  }
  
  static class ShadowImpl
  {
    View mFocusShadow;
    View mNormalShadow;
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/ShadowHelperJbmr2.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */