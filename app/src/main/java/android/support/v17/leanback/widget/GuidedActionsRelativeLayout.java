package android.support.v17.leanback.widget;

import android.content.Context;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.support.v17.leanback.R.id;
import android.support.v17.leanback.R.styleable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.RelativeLayout;

class GuidedActionsRelativeLayout
  extends RelativeLayout
{
  private boolean mInOverride = false;
  private InterceptKeyEventListener mInterceptKeyEventListener;
  private float mKeyLinePercent;
  
  public GuidedActionsRelativeLayout(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public GuidedActionsRelativeLayout(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public GuidedActionsRelativeLayout(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    this.mKeyLinePercent = GuidanceStylingRelativeLayout.getKeyLinePercent(paramContext);
  }
  
  private void init()
  {
    TypedArray localTypedArray = getContext().getTheme().obtainStyledAttributes(R.styleable.LeanbackGuidedStepTheme);
    this.mKeyLinePercent = localTypedArray.getFloat(R.styleable.LeanbackGuidedStepTheme_guidedStepKeyline, 40.0F);
    localTypedArray.recycle();
  }
  
  public boolean dispatchKeyEvent(KeyEvent paramKeyEvent)
  {
    if ((this.mInterceptKeyEventListener != null) && (this.mInterceptKeyEventListener.onInterceptKeyEvent(paramKeyEvent))) {
      return true;
    }
    return super.dispatchKeyEvent(paramKeyEvent);
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    this.mInOverride = false;
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    int i = View.MeasureSpec.getSize(paramInt2);
    if (i > 0)
    {
      Object localObject = findViewById(R.id.guidedactions_sub_list);
      if (localObject != null)
      {
        localObject = (ViewGroup.MarginLayoutParams)((View)localObject).getLayoutParams();
        if ((((ViewGroup.MarginLayoutParams)localObject).topMargin < 0) && (!this.mInOverride)) {
          this.mInOverride = true;
        }
        if (this.mInOverride) {
          ((ViewGroup.MarginLayoutParams)localObject).topMargin = ((int)(this.mKeyLinePercent * i / 100.0F));
        }
      }
    }
    super.onMeasure(paramInt1, paramInt2);
  }
  
  public void setInterceptKeyEventListener(InterceptKeyEventListener paramInterceptKeyEventListener)
  {
    this.mInterceptKeyEventListener = paramInterceptKeyEventListener;
  }
  
  static abstract interface InterceptKeyEventListener
  {
    public abstract boolean onInterceptKeyEvent(KeyEvent paramKeyEvent);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/GuidedActionsRelativeLayout.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */