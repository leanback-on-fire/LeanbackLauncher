package android.support.v17.leanback.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Shader.TileMode;
import android.support.v17.leanback.R.styleable;
import android.util.AttributeSet;
import android.view.View;

public class HorizontalGridView
  extends BaseGridView
{
  private boolean mFadingHighEdge;
  private boolean mFadingLowEdge;
  private LinearGradient mHighFadeShader;
  private int mHighFadeShaderLength;
  private int mHighFadeShaderOffset;
  private LinearGradient mLowFadeShader;
  private int mLowFadeShaderLength;
  private int mLowFadeShaderOffset;
  private Bitmap mTempBitmapHigh;
  private Bitmap mTempBitmapLow;
  private Paint mTempPaint = new Paint();
  private Rect mTempRect = new Rect();
  
  public HorizontalGridView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public HorizontalGridView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public HorizontalGridView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    this.mLayoutManager.setOrientation(0);
    initAttributes(paramContext, paramAttributeSet);
  }
  
  private Bitmap getTempBitmapHigh()
  {
    if ((this.mTempBitmapHigh == null) || (this.mTempBitmapHigh.getWidth() != this.mHighFadeShaderLength) || (this.mTempBitmapHigh.getHeight() != getHeight())) {
      this.mTempBitmapHigh = Bitmap.createBitmap(this.mHighFadeShaderLength, getHeight(), Bitmap.Config.ARGB_8888);
    }
    return this.mTempBitmapHigh;
  }
  
  private Bitmap getTempBitmapLow()
  {
    if ((this.mTempBitmapLow == null) || (this.mTempBitmapLow.getWidth() != this.mLowFadeShaderLength) || (this.mTempBitmapLow.getHeight() != getHeight())) {
      this.mTempBitmapLow = Bitmap.createBitmap(this.mLowFadeShaderLength, getHeight(), Bitmap.Config.ARGB_8888);
    }
    return this.mTempBitmapLow;
  }
  
  private boolean needsFadingHighEdge()
  {
    if (!this.mFadingHighEdge) {}
    for (;;)
    {
      return false;
      int i = getChildCount() - 1;
      while (i >= 0)
      {
        View localView = getChildAt(i);
        if (this.mLayoutManager.getOpticalRight(localView) > getWidth() - getPaddingRight() + this.mHighFadeShaderOffset) {
          return true;
        }
        i -= 1;
      }
    }
  }
  
  private boolean needsFadingLowEdge()
  {
    if (!this.mFadingLowEdge) {}
    for (;;)
    {
      return false;
      int j = getChildCount();
      int i = 0;
      while (i < j)
      {
        View localView = getChildAt(i);
        if (this.mLayoutManager.getOpticalLeft(localView) < getPaddingLeft() - this.mLowFadeShaderOffset) {
          return true;
        }
        i += 1;
      }
    }
  }
  
  private void updateLayerType()
  {
    if ((this.mFadingLowEdge) || (this.mFadingHighEdge))
    {
      setLayerType(2, null);
      setWillNotDraw(false);
      return;
    }
    setLayerType(0, null);
    setWillNotDraw(true);
  }
  
  public void draw(Canvas paramCanvas)
  {
    boolean bool1 = needsFadingLowEdge();
    boolean bool2 = needsFadingHighEdge();
    if (!bool1) {
      this.mTempBitmapLow = null;
    }
    if (!bool2) {
      this.mTempBitmapHigh = null;
    }
    if ((!bool1) && (!bool2))
    {
      super.draw(paramCanvas);
      return;
    }
    int i;
    label70:
    int j;
    label97:
    int n;
    int k;
    if (this.mFadingLowEdge)
    {
      i = getPaddingLeft() - this.mLowFadeShaderOffset - this.mLowFadeShaderLength;
      if (!this.mFadingHighEdge) {
        break label521;
      }
      j = getWidth() - getPaddingRight() + this.mHighFadeShaderOffset + this.mHighFadeShaderLength;
      n = paramCanvas.save();
      if (!this.mFadingLowEdge) {
        break label529;
      }
      k = this.mLowFadeShaderLength;
      label116:
      if (!this.mFadingHighEdge) {
        break label535;
      }
    }
    label521:
    label529:
    label535:
    for (int m = this.mHighFadeShaderLength;; m = 0)
    {
      paramCanvas.clipRect(i + k, 0, j - m, getHeight());
      super.draw(paramCanvas);
      paramCanvas.restoreToCount(n);
      Canvas localCanvas = new Canvas();
      this.mTempRect.top = 0;
      this.mTempRect.bottom = getHeight();
      if ((bool1) && (this.mLowFadeShaderLength > 0))
      {
        localBitmap = getTempBitmapLow();
        localBitmap.eraseColor(0);
        localCanvas.setBitmap(localBitmap);
        k = localCanvas.save();
        localCanvas.clipRect(0, 0, this.mLowFadeShaderLength, getHeight());
        localCanvas.translate(-i, 0.0F);
        super.draw(localCanvas);
        localCanvas.restoreToCount(k);
        this.mTempPaint.setShader(this.mLowFadeShader);
        localCanvas.drawRect(0.0F, 0.0F, this.mLowFadeShaderLength, getHeight(), this.mTempPaint);
        this.mTempRect.left = 0;
        this.mTempRect.right = this.mLowFadeShaderLength;
        paramCanvas.translate(i, 0.0F);
        paramCanvas.drawBitmap(localBitmap, this.mTempRect, this.mTempRect, null);
        paramCanvas.translate(-i, 0.0F);
      }
      if ((!bool2) || (this.mHighFadeShaderLength <= 0)) {
        break;
      }
      Bitmap localBitmap = getTempBitmapHigh();
      localBitmap.eraseColor(0);
      localCanvas.setBitmap(localBitmap);
      i = localCanvas.save();
      localCanvas.clipRect(0, 0, this.mHighFadeShaderLength, getHeight());
      localCanvas.translate(-(j - this.mHighFadeShaderLength), 0.0F);
      super.draw(localCanvas);
      localCanvas.restoreToCount(i);
      this.mTempPaint.setShader(this.mHighFadeShader);
      localCanvas.drawRect(0.0F, 0.0F, this.mHighFadeShaderLength, getHeight(), this.mTempPaint);
      this.mTempRect.left = 0;
      this.mTempRect.right = this.mHighFadeShaderLength;
      paramCanvas.translate(j - this.mHighFadeShaderLength, 0.0F);
      paramCanvas.drawBitmap(localBitmap, this.mTempRect, this.mTempRect, null);
      paramCanvas.translate(-(j - this.mHighFadeShaderLength), 0.0F);
      return;
      i = 0;
      break label70;
      j = getWidth();
      break label97;
      k = 0;
      break label116;
    }
  }
  
  public final boolean getFadingLeftEdge()
  {
    return this.mFadingLowEdge;
  }
  
  public final int getFadingLeftEdgeLength()
  {
    return this.mLowFadeShaderLength;
  }
  
  public final int getFadingLeftEdgeOffset()
  {
    return this.mLowFadeShaderOffset;
  }
  
  public final boolean getFadingRightEdge()
  {
    return this.mFadingHighEdge;
  }
  
  public final int getFadingRightEdgeLength()
  {
    return this.mHighFadeShaderLength;
  }
  
  public final int getFadingRightEdgeOffset()
  {
    return this.mHighFadeShaderOffset;
  }
  
  protected void initAttributes(Context paramContext, AttributeSet paramAttributeSet)
  {
    initBaseGridViewAttributes(paramContext, paramAttributeSet);
    paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.lbHorizontalGridView);
    setRowHeight(paramContext);
    setNumRows(paramContext.getInt(R.styleable.lbHorizontalGridView_numberOfRows, 1));
    paramContext.recycle();
    updateLayerType();
    this.mTempPaint = new Paint();
    this.mTempPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
  }
  
  public final void setFadingLeftEdge(boolean paramBoolean)
  {
    if (this.mFadingLowEdge != paramBoolean)
    {
      this.mFadingLowEdge = paramBoolean;
      if (!this.mFadingLowEdge) {
        this.mTempBitmapLow = null;
      }
      invalidate();
      updateLayerType();
    }
  }
  
  public final void setFadingLeftEdgeLength(int paramInt)
  {
    if (this.mLowFadeShaderLength != paramInt)
    {
      this.mLowFadeShaderLength = paramInt;
      if (this.mLowFadeShaderLength == 0) {
        break label51;
      }
    }
    label51:
    for (this.mLowFadeShader = new LinearGradient(0.0F, 0.0F, this.mLowFadeShaderLength, 0.0F, 0, -16777216, Shader.TileMode.CLAMP);; this.mLowFadeShader = null)
    {
      invalidate();
      return;
    }
  }
  
  public final void setFadingLeftEdgeOffset(int paramInt)
  {
    if (this.mLowFadeShaderOffset != paramInt)
    {
      this.mLowFadeShaderOffset = paramInt;
      invalidate();
    }
  }
  
  public final void setFadingRightEdge(boolean paramBoolean)
  {
    if (this.mFadingHighEdge != paramBoolean)
    {
      this.mFadingHighEdge = paramBoolean;
      if (!this.mFadingHighEdge) {
        this.mTempBitmapHigh = null;
      }
      invalidate();
      updateLayerType();
    }
  }
  
  public final void setFadingRightEdgeLength(int paramInt)
  {
    if (this.mHighFadeShaderLength != paramInt)
    {
      this.mHighFadeShaderLength = paramInt;
      if (this.mHighFadeShaderLength == 0) {
        break label51;
      }
    }
    label51:
    for (this.mHighFadeShader = new LinearGradient(0.0F, 0.0F, this.mHighFadeShaderLength, 0.0F, -16777216, 0, Shader.TileMode.CLAMP);; this.mHighFadeShader = null)
    {
      invalidate();
      return;
    }
  }
  
  public final void setFadingRightEdgeOffset(int paramInt)
  {
    if (this.mHighFadeShaderOffset != paramInt)
    {
      this.mHighFadeShaderOffset = paramInt;
      invalidate();
    }
  }
  
  public void setNumRows(int paramInt)
  {
    this.mLayoutManager.setNumRows(paramInt);
    requestLayout();
  }
  
  public void setRowHeight(int paramInt)
  {
    this.mLayoutManager.setRowHeight(paramInt);
    requestLayout();
  }
  
  void setRowHeight(TypedArray paramTypedArray)
  {
    if (paramTypedArray.peekValue(R.styleable.lbHorizontalGridView_rowHeight) != null) {
      setRowHeight(paramTypedArray.getLayoutDimension(R.styleable.lbHorizontalGridView_rowHeight, 0));
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/HorizontalGridView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */