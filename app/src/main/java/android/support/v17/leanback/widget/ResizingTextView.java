package android.support.v17.leanback.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v17.leanback.R.styleable;
import android.text.Layout;
import android.util.AttributeSet;
import android.widget.TextView;

class ResizingTextView
  extends TextView
{
  public static final int TRIGGER_MAX_LINES = 1;
  private float mDefaultLineSpacingExtra;
  private int mDefaultPaddingBottom;
  private int mDefaultPaddingTop;
  private int mDefaultTextSize;
  private boolean mDefaultsInitialized = false;
  private boolean mIsResized = false;
  private boolean mMaintainLineSpacing;
  private int mResizedPaddingAdjustmentBottom;
  private int mResizedPaddingAdjustmentTop;
  private int mResizedTextSize;
  private int mTriggerConditions;
  
  public ResizingTextView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public ResizingTextView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 16842884);
  }
  
  public ResizingTextView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public ResizingTextView(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1);
    paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.lbResizingTextView, paramInt1, paramInt2);
    try
    {
      this.mTriggerConditions = paramContext.getInt(R.styleable.lbResizingTextView_resizeTrigger, 1);
      this.mResizedTextSize = paramContext.getDimensionPixelSize(R.styleable.lbResizingTextView_resizedTextSize, -1);
      this.mMaintainLineSpacing = paramContext.getBoolean(R.styleable.lbResizingTextView_maintainLineSpacing, false);
      this.mResizedPaddingAdjustmentTop = paramContext.getDimensionPixelOffset(R.styleable.lbResizingTextView_resizedPaddingAdjustmentTop, 0);
      this.mResizedPaddingAdjustmentBottom = paramContext.getDimensionPixelOffset(R.styleable.lbResizingTextView_resizedPaddingAdjustmentBottom, 0);
      return;
    }
    finally
    {
      paramContext.recycle();
    }
  }
  
  private void resizeParamsChanged()
  {
    if (this.mIsResized) {
      requestLayout();
    }
  }
  
  private void setPaddingTopAndBottom(int paramInt1, int paramInt2)
  {
    if (isPaddingRelative())
    {
      setPaddingRelative(getPaddingStart(), paramInt1, getPaddingEnd(), paramInt2);
      return;
    }
    setPadding(getPaddingLeft(), paramInt1, getPaddingRight(), paramInt2);
  }
  
  public boolean getMaintainLineSpacing()
  {
    return this.mMaintainLineSpacing;
  }
  
  public int getResizedPaddingAdjustmentBottom()
  {
    return this.mResizedPaddingAdjustmentBottom;
  }
  
  public int getResizedPaddingAdjustmentTop()
  {
    return this.mResizedPaddingAdjustmentTop;
  }
  
  public int getResizedTextSize()
  {
    return this.mResizedTextSize;
  }
  
  public int getTriggerConditions()
  {
    return this.mTriggerConditions;
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    if (!this.mDefaultsInitialized)
    {
      this.mDefaultTextSize = ((int)getTextSize());
      this.mDefaultLineSpacingExtra = getLineSpacingExtra();
      this.mDefaultPaddingTop = getPaddingTop();
      this.mDefaultPaddingBottom = getPaddingBottom();
      this.mDefaultsInitialized = true;
    }
    setTextSize(0, this.mDefaultTextSize);
    setLineSpacing(this.mDefaultLineSpacingExtra, getLineSpacingMultiplier());
    setPaddingTopAndBottom(this.mDefaultPaddingTop, this.mDefaultPaddingBottom);
    super.onMeasure(paramInt1, paramInt2);
    boolean bool2 = false;
    Layout localLayout = getLayout();
    boolean bool1 = bool2;
    int i;
    if (localLayout != null)
    {
      bool1 = bool2;
      if ((this.mTriggerConditions & 0x1) > 0)
      {
        i = localLayout.getLineCount();
        j = getMaxLines();
        bool1 = bool2;
        if (j > 1)
        {
          if (i != j) {
            break label331;
          }
          bool1 = true;
        }
      }
    }
    int m = (int)getTextSize();
    int k = 0;
    int j = 0;
    if (bool1)
    {
      i = j;
      if (this.mResizedTextSize != -1)
      {
        i = j;
        if (m != this.mResizedTextSize)
        {
          setTextSize(0, this.mResizedTextSize);
          i = 1;
        }
      }
      float f = this.mDefaultLineSpacingExtra + this.mDefaultTextSize - this.mResizedTextSize;
      j = i;
      if (this.mMaintainLineSpacing)
      {
        j = i;
        if (getLineSpacingExtra() != f)
        {
          setLineSpacing(f, getLineSpacingMultiplier());
          j = 1;
        }
      }
      k = this.mDefaultPaddingTop + this.mResizedPaddingAdjustmentTop;
      m = this.mDefaultPaddingBottom + this.mResizedPaddingAdjustmentBottom;
      if (getPaddingTop() == k)
      {
        i = j;
        if (getPaddingBottom() == m) {}
      }
      else
      {
        setPaddingTopAndBottom(k, m);
        i = 1;
      }
    }
    for (;;)
    {
      this.mIsResized = bool1;
      if (i != 0) {
        super.onMeasure(paramInt1, paramInt2);
      }
      return;
      label331:
      bool1 = false;
      break;
      i = k;
      if (this.mResizedTextSize != -1)
      {
        i = k;
        if (m != this.mDefaultTextSize)
        {
          setTextSize(0, this.mDefaultTextSize);
          i = 1;
        }
      }
      j = i;
      if (this.mMaintainLineSpacing)
      {
        j = i;
        if (getLineSpacingExtra() != this.mDefaultLineSpacingExtra)
        {
          setLineSpacing(this.mDefaultLineSpacingExtra, getLineSpacingMultiplier());
          j = 1;
        }
      }
      if (getPaddingTop() == this.mDefaultPaddingTop)
      {
        i = j;
        if (getPaddingBottom() == this.mDefaultPaddingBottom) {}
      }
      else
      {
        setPaddingTopAndBottom(this.mDefaultPaddingTop, this.mDefaultPaddingBottom);
        i = 1;
      }
    }
  }
  
  public void setMaintainLineSpacing(boolean paramBoolean)
  {
    if (this.mMaintainLineSpacing != paramBoolean)
    {
      this.mMaintainLineSpacing = paramBoolean;
      resizeParamsChanged();
    }
  }
  
  public void setResizedPaddingAdjustmentBottom(int paramInt)
  {
    if (this.mResizedPaddingAdjustmentBottom != paramInt)
    {
      this.mResizedPaddingAdjustmentBottom = paramInt;
      resizeParamsChanged();
    }
  }
  
  public void setResizedPaddingAdjustmentTop(int paramInt)
  {
    if (this.mResizedPaddingAdjustmentTop != paramInt)
    {
      this.mResizedPaddingAdjustmentTop = paramInt;
      resizeParamsChanged();
    }
  }
  
  public void setResizedTextSize(int paramInt)
  {
    if (this.mResizedTextSize != paramInt)
    {
      this.mResizedTextSize = paramInt;
      resizeParamsChanged();
    }
  }
  
  public void setTriggerConditions(int paramInt)
  {
    if (this.mTriggerConditions != paramInt)
    {
      this.mTriggerConditions = paramInt;
      requestLayout();
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/ResizingTextView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */