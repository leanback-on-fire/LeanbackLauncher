package android.support.v17.leanback.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.RestrictTo;
import android.support.v17.leanback.R.dimen;
import android.util.AttributeSet;
import android.view.View;

@RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
public final class SeekBar
  extends View
{
  private AccessibilitySeekListener mAccessibilitySeekListener;
  private int mActiveBarHeight;
  private int mActiveRadius;
  private final Paint mBackgroundPaint = new Paint(1);
  private final RectF mBackgroundRect = new RectF();
  private int mBarHeight;
  private final Paint mKnobPaint = new Paint(1);
  private int mKnobx;
  private int mMax;
  private int mProgress;
  private final Paint mProgressPaint = new Paint(1);
  private final RectF mProgressRect = new RectF();
  private int mSecondProgress;
  private final Paint mSecondProgressPaint = new Paint(1);
  private final RectF mSecondProgressRect = new RectF();
  
  public SeekBar(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    setWillNotDraw(false);
    this.mBackgroundPaint.setColor(-7829368);
    this.mSecondProgressPaint.setColor(-3355444);
    this.mProgressPaint.setColor(-65536);
    this.mKnobPaint.setColor(-1);
    this.mBarHeight = paramContext.getResources().getDimensionPixelSize(R.dimen.lb_playback_transport_progressbar_bar_height);
    this.mActiveBarHeight = paramContext.getResources().getDimensionPixelSize(R.dimen.lb_playback_transport_progressbar_active_bar_height);
    this.mActiveRadius = paramContext.getResources().getDimensionPixelSize(R.dimen.lb_playback_transport_progressbar_active_radius);
  }
  
  private void calculate()
  {
    int k;
    int j;
    int m;
    if (isFocused())
    {
      i = this.mActiveBarHeight;
      k = getWidth();
      j = getHeight();
      m = (j - i) / 2;
      this.mBackgroundRect.set(this.mBarHeight / 2, m, k - this.mBarHeight / 2, j - m);
      if (!isFocused()) {
        break label210;
      }
    }
    label210:
    for (int i = this.mActiveRadius;; i = this.mBarHeight / 2)
    {
      k -= i * 2;
      float f1 = this.mProgress / this.mMax * k;
      this.mProgressRect.set(this.mBarHeight / 2, m, this.mBarHeight / 2 + f1, j - m);
      float f2 = this.mSecondProgress / this.mMax;
      float f3 = k;
      this.mSecondProgressRect.set(this.mBarHeight / 2, m, this.mBarHeight / 2 + f2 * f3, j - m);
      this.mKnobx = ((int)f1 + i);
      invalidate();
      return;
      i = this.mBarHeight;
      break;
    }
  }
  
  public CharSequence getAccessibilityClassName()
  {
    return android.widget.SeekBar.class.getName();
  }
  
  public int getMax()
  {
    return this.mMax;
  }
  
  public int getProgress()
  {
    return this.mProgress;
  }
  
  public int getSecondProgress()
  {
    return this.mSecondProgress;
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    super.onDraw(paramCanvas);
    if (isFocused()) {}
    for (int i = this.mActiveRadius;; i = this.mBarHeight / 2)
    {
      paramCanvas.drawRoundRect(this.mBackgroundRect, i, i, this.mBackgroundPaint);
      paramCanvas.drawRoundRect(this.mSecondProgressRect, i, i, this.mProgressPaint);
      paramCanvas.drawRoundRect(this.mProgressRect, i, i, this.mProgressPaint);
      paramCanvas.drawCircle(this.mKnobx, getHeight() / 2, i, this.mKnobPaint);
      return;
    }
  }
  
  protected void onFocusChanged(boolean paramBoolean, int paramInt, Rect paramRect)
  {
    super.onFocusChanged(paramBoolean, paramInt, paramRect);
    calculate();
  }
  
  protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onSizeChanged(paramInt1, paramInt2, paramInt3, paramInt4);
    calculate();
  }
  
  public boolean performAccessibilityAction(int paramInt, Bundle paramBundle)
  {
    if (this.mAccessibilitySeekListener != null) {}
    switch (paramInt)
    {
    default: 
      return super.performAccessibilityAction(paramInt, paramBundle);
    case 4096: 
      return this.mAccessibilitySeekListener.onAccessibilitySeekForward();
    }
    return this.mAccessibilitySeekListener.onAccessibilitySeekBackward();
  }
  
  public void setAccessibilitySeekListener(AccessibilitySeekListener paramAccessibilitySeekListener)
  {
    this.mAccessibilitySeekListener = paramAccessibilitySeekListener;
  }
  
  public void setActiveBarHeight(int paramInt)
  {
    this.mActiveBarHeight = paramInt;
    calculate();
  }
  
  public void setActiveRadius(int paramInt)
  {
    this.mActiveRadius = paramInt;
    calculate();
  }
  
  public void setBarHeight(int paramInt)
  {
    this.mBarHeight = paramInt;
    calculate();
  }
  
  public void setMax(int paramInt)
  {
    this.mMax = paramInt;
    calculate();
  }
  
  public void setProgress(int paramInt)
  {
    int i;
    if (paramInt > this.mMax) {
      i = this.mMax;
    }
    for (;;)
    {
      this.mProgress = i;
      calculate();
      return;
      i = paramInt;
      if (paramInt < 0) {
        i = 0;
      }
    }
  }
  
  public void setProgressColor(int paramInt)
  {
    this.mProgressPaint.setColor(paramInt);
  }
  
  public void setSecondaryProgress(int paramInt)
  {
    int i;
    if (paramInt > this.mMax) {
      i = this.mMax;
    }
    for (;;)
    {
      this.mSecondProgress = i;
      calculate();
      return;
      i = paramInt;
      if (paramInt < 0) {
        i = 0;
      }
    }
  }
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  public static abstract class AccessibilitySeekListener
  {
    public abstract boolean onAccessibilitySeekBackward();
    
    public abstract boolean onAccessibilitySeekForward();
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/SeekBar.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */