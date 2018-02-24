package android.support.v17.leanback.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v17.leanback.R.color;
import android.util.AttributeSet;
import android.view.View;

class MediaRowFocusView
  extends View
{
  private final Paint mPaint = createPaint(paramContext);
  private final RectF mRoundRectF = new RectF();
  private int mRoundRectRadius;
  
  public MediaRowFocusView(Context paramContext)
  {
    super(paramContext);
  }
  
  public MediaRowFocusView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  public MediaRowFocusView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
  }
  
  private Paint createPaint(Context paramContext)
  {
    Paint localPaint = new Paint();
    localPaint.setColor(paramContext.getResources().getColor(R.color.lb_playback_media_row_highlight_color));
    return localPaint;
  }
  
  public int getRoundRectRadius()
  {
    return this.mRoundRectRadius;
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    super.onDraw(paramCanvas);
    this.mRoundRectRadius = (getHeight() / 2);
    int i = (this.mRoundRectRadius * 2 - getHeight()) / 2;
    this.mRoundRectF.set(0.0F, -i, getWidth(), getHeight() + i);
    paramCanvas.drawRoundRect(this.mRoundRectF, this.mRoundRectRadius, this.mRoundRectRadius, this.mPaint);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/MediaRowFocusView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */