package android.support.v17.leanback.widget;

import android.view.View;

public class ItemBridgeAdapterShadowOverlayWrapper
  extends ItemBridgeAdapter.Wrapper
{
  private final ShadowOverlayHelper mHelper;
  
  public ItemBridgeAdapterShadowOverlayWrapper(ShadowOverlayHelper paramShadowOverlayHelper)
  {
    this.mHelper = paramShadowOverlayHelper;
  }
  
  public View createWrapper(View paramView)
  {
    paramView = paramView.getContext();
    return this.mHelper.createShadowOverlayContainer(paramView);
  }
  
  public void wrap(View paramView1, View paramView2)
  {
    ((ShadowOverlayContainer)paramView1).wrap(paramView2);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/ItemBridgeAdapterShadowOverlayWrapper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */