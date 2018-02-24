package android.support.v17.leanback.widget;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.util.AttributeSet;
import android.widget.LinearLayout;

class NonOverlappingLinearLayoutWithForeground
  extends LinearLayout
{
  private static final int VERSION_M = 23;
  private Drawable mForeground;
  private boolean mForegroundBoundsChanged;
  private final Rect mSelfBounds = new Rect();
  
  public NonOverlappingLinearLayoutWithForeground(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public NonOverlappingLinearLayoutWithForeground(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public NonOverlappingLinearLayoutWithForeground(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    if ((paramContext.getApplicationInfo().targetSdkVersion >= 23) && (Build.VERSION.SDK_INT >= 23)) {
      return;
    }
    paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, new int[] { 16843017 });
    paramAttributeSet = paramContext.getDrawable(0);
    if (paramAttributeSet != null) {
      setForegroundCompat(paramAttributeSet);
    }
    paramContext.recycle();
  }
  
  public void draw(Canvas paramCanvas)
  {
    super.draw(paramCanvas);
    if (this.mForeground != null)
    {
      Drawable localDrawable = this.mForeground;
      if (this.mForegroundBoundsChanged)
      {
        this.mForegroundBoundsChanged = false;
        Rect localRect = this.mSelfBounds;
        localRect.set(0, 0, getRight() - getLeft(), getBottom() - getTop());
        localDrawable.setBounds(localRect);
      }
      localDrawable.draw(paramCanvas);
    }
  }
  
  protected void drawableStateChanged()
  {
    super.drawableStateChanged();
    if ((this.mForeground != null) && (this.mForeground.isStateful())) {
      this.mForeground.setState(getDrawableState());
    }
  }
  
  public Drawable getForegroundCompat()
  {
    if (Build.VERSION.SDK_INT >= 23) {
      return ForegroundHelper.getInstance().getForeground(this);
    }
    return this.mForeground;
  }
  
  public boolean hasOverlappingRendering()
  {
    return false;
  }
  
  public void jumpDrawablesToCurrentState()
  {
    super.jumpDrawablesToCurrentState();
    if (this.mForeground != null) {
      this.mForeground.jumpToCurrentState();
    }
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    this.mForegroundBoundsChanged |= paramBoolean;
  }
  
  public void setForegroundCompat(Drawable paramDrawable)
  {
    if (Build.VERSION.SDK_INT >= 23) {
      ForegroundHelper.getInstance().setForeground(this, paramDrawable);
    }
    do
    {
      do
      {
        return;
      } while (this.mForeground == paramDrawable);
      this.mForeground = paramDrawable;
      this.mForegroundBoundsChanged = true;
      setWillNotDraw(false);
      this.mForeground.setCallback(this);
    } while (!this.mForeground.isStateful());
    this.mForeground.setState(getDrawableState());
  }
  
  protected boolean verifyDrawable(Drawable paramDrawable)
  {
    return (super.verifyDrawable(paramDrawable)) || (paramDrawable == this.mForeground);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/NonOverlappingLinearLayoutWithForeground.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */