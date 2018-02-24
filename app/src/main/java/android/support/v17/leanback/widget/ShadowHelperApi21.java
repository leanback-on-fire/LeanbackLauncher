package android.support.v17.leanback.widget;

import android.graphics.Outline;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.ViewOutlineProvider;

@RequiresApi(21)
class ShadowHelperApi21
{
  static final ViewOutlineProvider sOutlineProvider = new ViewOutlineProvider()
  {
    public void getOutline(View paramAnonymousView, Outline paramAnonymousOutline)
    {
      paramAnonymousOutline.setRect(0, 0, paramAnonymousView.getWidth(), paramAnonymousView.getHeight());
      paramAnonymousOutline.setAlpha(1.0F);
    }
  };
  
  public static Object addDynamicShadow(View paramView, float paramFloat1, float paramFloat2, int paramInt)
  {
    if (paramInt > 0) {
      RoundedRectHelperApi21.setClipToRoundedOutline(paramView, true, paramInt);
    }
    for (;;)
    {
      ShadowImpl localShadowImpl = new ShadowImpl();
      localShadowImpl.mShadowContainer = paramView;
      localShadowImpl.mNormalZ = paramFloat1;
      localShadowImpl.mFocusedZ = paramFloat2;
      paramView.setZ(localShadowImpl.mNormalZ);
      return localShadowImpl;
      paramView.setOutlineProvider(sOutlineProvider);
    }
  }
  
  public static void setShadowFocusLevel(Object paramObject, float paramFloat)
  {
    paramObject = (ShadowImpl)paramObject;
    ((ShadowImpl)paramObject).mShadowContainer.setZ(((ShadowImpl)paramObject).mNormalZ + (((ShadowImpl)paramObject).mFocusedZ - ((ShadowImpl)paramObject).mNormalZ) * paramFloat);
  }
  
  public static void setZ(View paramView, float paramFloat)
  {
    paramView.setZ(paramFloat);
  }
  
  static class ShadowImpl
  {
    float mFocusedZ;
    float mNormalZ;
    View mShadowContainer;
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/ShadowHelperApi21.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */