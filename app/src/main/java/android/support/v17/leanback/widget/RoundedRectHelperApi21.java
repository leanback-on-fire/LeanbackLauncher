package android.support.v17.leanback.widget;

import android.graphics.Outline;
import android.support.annotation.RequiresApi;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewOutlineProvider;

@RequiresApi(21)
class RoundedRectHelperApi21
{
  private static final int MAX_CACHED_PROVIDER = 32;
  private static SparseArray<ViewOutlineProvider> sRoundedRectProvider;
  
  public static void setClipToRoundedOutline(View paramView, boolean paramBoolean, int paramInt)
  {
    if (paramBoolean)
    {
      if (sRoundedRectProvider == null) {
        sRoundedRectProvider = new SparseArray();
      }
      Object localObject2 = (ViewOutlineProvider)sRoundedRectProvider.get(paramInt);
      Object localObject1 = localObject2;
      if (localObject2 == null)
      {
        localObject2 = new RoundedRectOutlineProvider(paramInt);
        localObject1 = localObject2;
        if (sRoundedRectProvider.size() < 32)
        {
          sRoundedRectProvider.put(paramInt, localObject2);
          localObject1 = localObject2;
        }
      }
      paramView.setOutlineProvider((ViewOutlineProvider)localObject1);
    }
    for (;;)
    {
      paramView.setClipToOutline(paramBoolean);
      return;
      paramView.setOutlineProvider(ViewOutlineProvider.BACKGROUND);
    }
  }
  
  static final class RoundedRectOutlineProvider
    extends ViewOutlineProvider
  {
    private int mRadius;
    
    RoundedRectOutlineProvider(int paramInt)
    {
      this.mRadius = paramInt;
    }
    
    public void getOutline(View paramView, Outline paramOutline)
    {
      paramOutline.setRoundRect(0, 0, paramView.getWidth(), paramView.getHeight(), this.mRadius);
      paramOutline.setAlpha(1.0F);
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/RoundedRectHelperApi21.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */