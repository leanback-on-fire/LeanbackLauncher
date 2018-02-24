package android.support.v17.leanback.widget;

import android.content.Context;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.support.v17.leanback.R.id;
import android.support.v17.leanback.R.styleable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

class GuidanceStylingRelativeLayout
  extends RelativeLayout
{
  private float mTitleKeylinePercent;
  
  public GuidanceStylingRelativeLayout(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public GuidanceStylingRelativeLayout(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public GuidanceStylingRelativeLayout(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    this.mTitleKeylinePercent = getKeyLinePercent(paramContext);
  }
  
  public static float getKeyLinePercent(Context paramContext)
  {
    paramContext = paramContext.getTheme().obtainStyledAttributes(R.styleable.LeanbackGuidedStepTheme);
    float f = paramContext.getFloat(R.styleable.LeanbackGuidedStepTheme_guidedStepKeyline, 40.0F);
    paramContext.recycle();
    return f;
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    View localView1 = getRootView().findViewById(R.id.guidance_title);
    View localView2 = getRootView().findViewById(R.id.guidance_breadcrumb);
    View localView3 = getRootView().findViewById(R.id.guidance_description);
    ImageView localImageView = (ImageView)getRootView().findViewById(R.id.guidance_icon);
    paramInt1 = (int)(getMeasuredHeight() * this.mTitleKeylinePercent / 100.0F);
    if ((localView1 != null) && (localView1.getParent() == this))
    {
      paramInt2 = paramInt1 - localView1.getBaseline() - localView2.getMeasuredHeight() - localView1.getPaddingTop() - localView2.getTop();
      if ((localView2 != null) && (localView2.getParent() == this)) {
        localView2.offsetTopAndBottom(paramInt2);
      }
      localView1.offsetTopAndBottom(paramInt2);
      if ((localView3 != null) && (localView3.getParent() == this)) {
        localView3.offsetTopAndBottom(paramInt2);
      }
    }
    if ((localImageView != null) && (localImageView.getParent() == this) && (localImageView.getDrawable() != null)) {
      localImageView.offsetTopAndBottom(paramInt1 - localImageView.getMeasuredHeight() / 2);
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/GuidanceStylingRelativeLayout.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */