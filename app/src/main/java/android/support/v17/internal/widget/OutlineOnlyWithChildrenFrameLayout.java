package android.support.v17.internal.widget;

import android.content.Context;
import android.graphics.Outline;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.FrameLayout;

@RequiresApi(21)
@RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
public class OutlineOnlyWithChildrenFrameLayout
  extends FrameLayout
{
  private ViewOutlineProvider mInnerOutlineProvider;
  private ViewOutlineProvider mMagicalOutlineProvider;
  
  public OutlineOnlyWithChildrenFrameLayout(Context paramContext)
  {
    super(paramContext);
  }
  
  public OutlineOnlyWithChildrenFrameLayout(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  public OutlineOnlyWithChildrenFrameLayout(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
  }
  
  public OutlineOnlyWithChildrenFrameLayout(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    invalidateOutline();
  }
  
  public void setOutlineProvider(ViewOutlineProvider paramViewOutlineProvider)
  {
    this.mInnerOutlineProvider = paramViewOutlineProvider;
    if (this.mMagicalOutlineProvider == null) {
      this.mMagicalOutlineProvider = new ViewOutlineProvider()
      {
        public void getOutline(View paramAnonymousView, Outline paramAnonymousOutline)
        {
          if (OutlineOnlyWithChildrenFrameLayout.this.getChildCount() > 0)
          {
            OutlineOnlyWithChildrenFrameLayout.this.mInnerOutlineProvider.getOutline(paramAnonymousView, paramAnonymousOutline);
            return;
          }
          ViewOutlineProvider.BACKGROUND.getOutline(paramAnonymousView, paramAnonymousOutline);
        }
      };
    }
    super.setOutlineProvider(this.mMagicalOutlineProvider);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/internal/widget/OutlineOnlyWithChildrenFrameLayout.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */