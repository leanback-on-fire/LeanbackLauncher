package android.support.v17.leanback.widget;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.support.annotation.ColorInt;
import android.support.annotation.RestrictTo;
import android.support.annotation.VisibleForTesting;
import android.support.v17.leanback.R.color;
import android.support.v17.leanback.R.dimen;
import android.support.v17.leanback.R.drawable;
import android.support.v17.leanback.R.styleable;
import android.util.AttributeSet;
import android.util.Property;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.animation.DecelerateInterpolator;

@RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
public class PagingIndicator
  extends View
{
  private static final TimeInterpolator DECELERATE_INTERPOLATOR = new DecelerateInterpolator();
  private static final Property<Dot, Float> DOT_ALPHA = new Property(Float.class, "alpha")
  {
    public Float get(PagingIndicator.Dot paramAnonymousDot)
    {
      return Float.valueOf(paramAnonymousDot.getAlpha());
    }
    
    public void set(PagingIndicator.Dot paramAnonymousDot, Float paramAnonymousFloat)
    {
      paramAnonymousDot.setAlpha(paramAnonymousFloat.floatValue());
    }
  };
  private static final Property<Dot, Float> DOT_DIAMETER = new Property(Float.class, "diameter")
  {
    public Float get(PagingIndicator.Dot paramAnonymousDot)
    {
      return Float.valueOf(paramAnonymousDot.getDiameter());
    }
    
    public void set(PagingIndicator.Dot paramAnonymousDot, Float paramAnonymousFloat)
    {
      paramAnonymousDot.setDiameter(paramAnonymousFloat.floatValue());
    }
  };
  private static final Property<Dot, Float> DOT_TRANSLATION_X = new Property(Float.class, "translation_x")
  {
    public Float get(PagingIndicator.Dot paramAnonymousDot)
    {
      return Float.valueOf(paramAnonymousDot.getTranslationX());
    }
    
    public void set(PagingIndicator.Dot paramAnonymousDot, Float paramAnonymousFloat)
    {
      paramAnonymousDot.setTranslationX(paramAnonymousFloat.floatValue());
    }
  };
  private static final long DURATION_ALPHA = 167L;
  private static final long DURATION_DIAMETER = 417L;
  private static final long DURATION_TRANSLATION_X = 417L;
  private final AnimatorSet mAnimator = new AnimatorSet();
  Bitmap mArrow;
  final int mArrowDiameter;
  private final int mArrowGap;
  Paint mArrowPaint;
  final int mArrowRadius;
  final Rect mArrowRect;
  final float mArrowToBgRatio;
  final Paint mBgPaint;
  private int mCurrentPage;
  int mDotCenterY;
  final int mDotDiameter;
  @ColorInt
  int mDotFgSelectColor;
  private final int mDotGap;
  final int mDotRadius;
  private int[] mDotSelectedNextX;
  private int[] mDotSelectedPrevX;
  private int[] mDotSelectedX;
  private Dot[] mDots;
  final Paint mFgPaint;
  private final AnimatorSet mHideAnimator;
  boolean mIsLtr;
  private int mPageCount;
  private int mPreviousPage;
  private final int mShadowRadius;
  private final AnimatorSet mShowAnimator;
  
  public PagingIndicator(Context paramContext)
  {
    this(paramContext, null, 0);
  }
  
  public PagingIndicator(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public PagingIndicator(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    Resources localResources = getResources();
    paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.PagingIndicator, paramInt, 0);
    this.mDotRadius = getDimensionFromTypedArray(paramContext, R.styleable.PagingIndicator_lbDotRadius, R.dimen.lb_page_indicator_dot_radius);
    this.mDotDiameter = (this.mDotRadius * 2);
    this.mArrowRadius = getDimensionFromTypedArray(paramContext, R.styleable.PagingIndicator_arrowRadius, R.dimen.lb_page_indicator_arrow_radius);
    this.mArrowDiameter = (this.mArrowRadius * 2);
    this.mDotGap = getDimensionFromTypedArray(paramContext, R.styleable.PagingIndicator_dotToDotGap, R.dimen.lb_page_indicator_dot_gap);
    this.mArrowGap = getDimensionFromTypedArray(paramContext, R.styleable.PagingIndicator_dotToArrowGap, R.dimen.lb_page_indicator_arrow_gap);
    paramInt = getColorFromTypedArray(paramContext, R.styleable.PagingIndicator_dotBgColor, R.color.lb_page_indicator_dot);
    this.mBgPaint = new Paint(1);
    this.mBgPaint.setColor(paramInt);
    this.mDotFgSelectColor = getColorFromTypedArray(paramContext, R.styleable.PagingIndicator_arrowBgColor, R.color.lb_page_indicator_arrow_background);
    if ((this.mArrowPaint == null) && (paramContext.hasValue(R.styleable.PagingIndicator_arrowColor))) {
      setArrowColor(paramContext.getColor(R.styleable.PagingIndicator_arrowColor, 0));
    }
    paramContext.recycle();
    if (localResources.getConfiguration().getLayoutDirection() == 0) {}
    for (boolean bool = true;; bool = false)
    {
      this.mIsLtr = bool;
      paramInt = localResources.getColor(R.color.lb_page_indicator_arrow_shadow);
      this.mShadowRadius = localResources.getDimensionPixelSize(R.dimen.lb_page_indicator_arrow_shadow_radius);
      this.mFgPaint = new Paint(1);
      int i = localResources.getDimensionPixelSize(R.dimen.lb_page_indicator_arrow_shadow_offset);
      this.mFgPaint.setShadowLayer(this.mShadowRadius, i, i, paramInt);
      this.mArrow = loadArrow();
      this.mArrowRect = new Rect(0, 0, this.mArrow.getWidth(), this.mArrow.getHeight());
      this.mArrowToBgRatio = (this.mArrow.getWidth() / this.mArrowDiameter);
      this.mShowAnimator = new AnimatorSet();
      this.mShowAnimator.playTogether(new Animator[] { createDotAlphaAnimator(0.0F, 1.0F), createDotDiameterAnimator(this.mDotRadius * 2, this.mArrowRadius * 2), createDotTranslationXAnimator() });
      this.mHideAnimator = new AnimatorSet();
      this.mHideAnimator.playTogether(new Animator[] { createDotAlphaAnimator(1.0F, 0.0F), createDotDiameterAnimator(this.mArrowRadius * 2, this.mDotRadius * 2), createDotTranslationXAnimator() });
      this.mAnimator.playTogether(new Animator[] { this.mShowAnimator, this.mHideAnimator });
      setLayerType(1, null);
      return;
    }
  }
  
  private void adjustDotPosition()
  {
    float f2 = -1.0F;
    int i = 0;
    if (i < this.mCurrentPage)
    {
      this.mDots[i].deselect();
      localDot = this.mDots[i];
      if (i == this.mPreviousPage) {}
      for (f1 = -1.0F;; f1 = 1.0F)
      {
        localDot.mDirection = f1;
        this.mDots[i].mCenterX = this.mDotSelectedPrevX[i];
        i += 1;
        break;
      }
    }
    this.mDots[this.mCurrentPage].select();
    Dot localDot = this.mDots[this.mCurrentPage];
    if (this.mPreviousPage < this.mCurrentPage) {}
    for (float f1 = f2;; f1 = 1.0F)
    {
      localDot.mDirection = f1;
      this.mDots[this.mCurrentPage].mCenterX = this.mDotSelectedX[this.mCurrentPage];
      i = this.mCurrentPage + 1;
      while (i < this.mPageCount)
      {
        this.mDots[i].deselect();
        this.mDots[i].mDirection = 1.0F;
        this.mDots[i].mCenterX = this.mDotSelectedNextX[i];
        i += 1;
      }
    }
  }
  
  private void calculateDotPositions()
  {
    int k = getPaddingLeft();
    int j = getPaddingTop();
    int m = getWidth();
    int n = getPaddingRight();
    int i = getRequiredWidth();
    k = (k + (m - n)) / 2;
    this.mDotSelectedX = new int[this.mPageCount];
    this.mDotSelectedPrevX = new int[this.mPageCount];
    this.mDotSelectedNextX = new int[this.mPageCount];
    if (this.mIsLtr)
    {
      i = k - i / 2;
      this.mDotSelectedX[0] = (this.mDotRadius + i - this.mDotGap + this.mArrowGap);
      this.mDotSelectedPrevX[0] = (this.mDotRadius + i);
      this.mDotSelectedNextX[0] = (this.mDotRadius + i - this.mDotGap * 2 + this.mArrowGap * 2);
      i = 1;
      while (i < this.mPageCount)
      {
        this.mDotSelectedX[i] = (this.mDotSelectedPrevX[(i - 1)] + this.mArrowGap);
        this.mDotSelectedPrevX[i] = (this.mDotSelectedPrevX[(i - 1)] + this.mDotGap);
        this.mDotSelectedNextX[i] = (this.mDotSelectedX[(i - 1)] + this.mArrowGap);
        i += 1;
      }
    }
    i = k + i / 2;
    this.mDotSelectedX[0] = (i - this.mDotRadius + this.mDotGap - this.mArrowGap);
    this.mDotSelectedPrevX[0] = (i - this.mDotRadius);
    this.mDotSelectedNextX[0] = (i - this.mDotRadius + this.mDotGap * 2 - this.mArrowGap * 2);
    i = 1;
    while (i < this.mPageCount)
    {
      this.mDotSelectedX[i] = (this.mDotSelectedPrevX[(i - 1)] - this.mArrowGap);
      this.mDotSelectedPrevX[i] = (this.mDotSelectedPrevX[(i - 1)] - this.mDotGap);
      this.mDotSelectedNextX[i] = (this.mDotSelectedX[(i - 1)] - this.mArrowGap);
      i += 1;
    }
    this.mDotCenterY = (this.mArrowRadius + j);
    adjustDotPosition();
  }
  
  private Animator createDotAlphaAnimator(float paramFloat1, float paramFloat2)
  {
    ObjectAnimator localObjectAnimator = ObjectAnimator.ofFloat(null, DOT_ALPHA, new float[] { paramFloat1, paramFloat2 });
    localObjectAnimator.setDuration(167L);
    localObjectAnimator.setInterpolator(DECELERATE_INTERPOLATOR);
    return localObjectAnimator;
  }
  
  private Animator createDotDiameterAnimator(float paramFloat1, float paramFloat2)
  {
    ObjectAnimator localObjectAnimator = ObjectAnimator.ofFloat(null, DOT_DIAMETER, new float[] { paramFloat1, paramFloat2 });
    localObjectAnimator.setDuration(417L);
    localObjectAnimator.setInterpolator(DECELERATE_INTERPOLATOR);
    return localObjectAnimator;
  }
  
  private Animator createDotTranslationXAnimator()
  {
    ObjectAnimator localObjectAnimator = ObjectAnimator.ofFloat(null, DOT_TRANSLATION_X, new float[] { -this.mArrowGap + this.mDotGap, 0.0F });
    localObjectAnimator.setDuration(417L);
    localObjectAnimator.setInterpolator(DECELERATE_INTERPOLATOR);
    return localObjectAnimator;
  }
  
  private int getColorFromTypedArray(TypedArray paramTypedArray, int paramInt1, int paramInt2)
  {
    return paramTypedArray.getColor(paramInt1, getResources().getColor(paramInt2));
  }
  
  private int getDesiredHeight()
  {
    return getPaddingTop() + this.mArrowDiameter + getPaddingBottom() + this.mShadowRadius;
  }
  
  private int getDesiredWidth()
  {
    return getPaddingLeft() + getRequiredWidth() + getPaddingRight();
  }
  
  private int getDimensionFromTypedArray(TypedArray paramTypedArray, int paramInt1, int paramInt2)
  {
    return paramTypedArray.getDimensionPixelOffset(paramInt1, getResources().getDimensionPixelOffset(paramInt2));
  }
  
  private int getRequiredWidth()
  {
    return this.mDotRadius * 2 + this.mArrowGap * 2 + (this.mPageCount - 3) * this.mDotGap;
  }
  
  private Bitmap loadArrow()
  {
    Bitmap localBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.lb_ic_nav_arrow);
    if (this.mIsLtr) {
      return localBitmap;
    }
    Matrix localMatrix = new Matrix();
    localMatrix.preScale(-1.0F, 1.0F);
    return Bitmap.createBitmap(localBitmap, 0, 0, localBitmap.getWidth(), localBitmap.getHeight(), localMatrix, false);
  }
  
  private void setSelectedPage(int paramInt)
  {
    if (paramInt == this.mCurrentPage) {
      return;
    }
    this.mCurrentPage = paramInt;
    adjustDotPosition();
  }
  
  @VisibleForTesting
  int[] getDotSelectedLeftX()
  {
    return this.mDotSelectedPrevX;
  }
  
  @VisibleForTesting
  int[] getDotSelectedRightX()
  {
    return this.mDotSelectedNextX;
  }
  
  @VisibleForTesting
  int[] getDotSelectedX()
  {
    return this.mDotSelectedX;
  }
  
  @VisibleForTesting
  int getPageCount()
  {
    return this.mPageCount;
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    int i = 0;
    while (i < this.mPageCount)
    {
      this.mDots[i].draw(paramCanvas);
      i += 1;
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    int i = getDesiredHeight();
    switch (View.MeasureSpec.getMode(paramInt2))
    {
    default: 
      paramInt2 = i;
      i = getDesiredWidth();
      switch (View.MeasureSpec.getMode(paramInt1))
      {
      default: 
        paramInt1 = i;
      }
      break;
    }
    for (;;)
    {
      setMeasuredDimension(paramInt1, paramInt2);
      return;
      paramInt2 = View.MeasureSpec.getSize(paramInt2);
      break;
      paramInt2 = Math.min(i, View.MeasureSpec.getSize(paramInt2));
      break;
      paramInt1 = View.MeasureSpec.getSize(paramInt1);
      continue;
      paramInt1 = Math.min(i, View.MeasureSpec.getSize(paramInt1));
    }
  }
  
  public void onPageSelected(int paramInt, boolean paramBoolean)
  {
    if (this.mCurrentPage == paramInt) {
      return;
    }
    if (this.mAnimator.isStarted()) {
      this.mAnimator.end();
    }
    this.mPreviousPage = this.mCurrentPage;
    if (paramBoolean)
    {
      this.mHideAnimator.setTarget(this.mDots[this.mPreviousPage]);
      this.mShowAnimator.setTarget(this.mDots[paramInt]);
      this.mAnimator.start();
    }
    setSelectedPage(paramInt);
  }
  
  public void onRtlPropertiesChanged(int paramInt)
  {
    int i = 0;
    super.onRtlPropertiesChanged(paramInt);
    boolean bool;
    if (paramInt == 0) {
      bool = true;
    }
    while (this.mIsLtr != bool)
    {
      this.mIsLtr = bool;
      this.mArrow = loadArrow();
      if (this.mDots != null)
      {
        Dot[] arrayOfDot = this.mDots;
        int j = arrayOfDot.length;
        paramInt = i;
        for (;;)
        {
          if (paramInt < j)
          {
            arrayOfDot[paramInt].onRtlPropertiesChanged();
            paramInt += 1;
            continue;
            bool = false;
            break;
          }
        }
      }
      calculateDotPositions();
      invalidate();
    }
  }
  
  protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    setMeasuredDimension(paramInt1, paramInt2);
    calculateDotPositions();
  }
  
  public void setArrowBackgroundColor(@ColorInt int paramInt)
  {
    this.mDotFgSelectColor = paramInt;
  }
  
  public void setArrowColor(@ColorInt int paramInt)
  {
    if (this.mArrowPaint == null) {
      this.mArrowPaint = new Paint();
    }
    this.mArrowPaint.setColorFilter(new PorterDuffColorFilter(paramInt, PorterDuff.Mode.SRC_IN));
  }
  
  public void setDotBackgroundColor(@ColorInt int paramInt)
  {
    this.mBgPaint.setColor(paramInt);
  }
  
  public void setPageCount(int paramInt)
  {
    if (paramInt <= 0) {
      throw new IllegalArgumentException("The page count should be a positive integer");
    }
    this.mPageCount = paramInt;
    this.mDots = new Dot[this.mPageCount];
    paramInt = 0;
    while (paramInt < this.mPageCount)
    {
      this.mDots[paramInt] = new Dot();
      paramInt += 1;
    }
    calculateDotPositions();
    setSelectedPage(0);
  }
  
  public class Dot
  {
    static final float LEFT = -1.0F;
    static final float LTR = 1.0F;
    static final float RIGHT = 1.0F;
    static final float RTL = -1.0F;
    float mAlpha;
    float mArrowImageRadius;
    float mCenterX;
    float mDiameter;
    float mDirection = 1.0F;
    @ColorInt
    int mFgColor;
    float mLayoutDirection;
    float mRadius;
    float mTranslationX;
    
    public Dot()
    {
      if (PagingIndicator.this.mIsLtr) {}
      for (;;)
      {
        this.mLayoutDirection = f;
        return;
        f = -1.0F;
      }
    }
    
    public void adjustAlpha()
    {
      this.mFgColor = Color.argb(Math.round(255.0F * this.mAlpha), Color.red(PagingIndicator.this.mDotFgSelectColor), Color.green(PagingIndicator.this.mDotFgSelectColor), Color.blue(PagingIndicator.this.mDotFgSelectColor));
    }
    
    void deselect()
    {
      this.mTranslationX = 0.0F;
      this.mCenterX = 0.0F;
      this.mDiameter = PagingIndicator.this.mDotDiameter;
      this.mRadius = PagingIndicator.this.mDotRadius;
      this.mArrowImageRadius = (this.mRadius * PagingIndicator.this.mArrowToBgRatio);
      this.mAlpha = 0.0F;
      adjustAlpha();
    }
    
    void draw(Canvas paramCanvas)
    {
      float f = this.mCenterX + this.mTranslationX;
      paramCanvas.drawCircle(f, PagingIndicator.this.mDotCenterY, this.mRadius, PagingIndicator.this.mBgPaint);
      if (this.mAlpha > 0.0F)
      {
        PagingIndicator.this.mFgPaint.setColor(this.mFgColor);
        paramCanvas.drawCircle(f, PagingIndicator.this.mDotCenterY, this.mRadius, PagingIndicator.this.mFgPaint);
        paramCanvas.drawBitmap(PagingIndicator.this.mArrow, PagingIndicator.this.mArrowRect, new Rect((int)(f - this.mArrowImageRadius), (int)(PagingIndicator.this.mDotCenterY - this.mArrowImageRadius), (int)(this.mArrowImageRadius + f), (int)(PagingIndicator.this.mDotCenterY + this.mArrowImageRadius)), PagingIndicator.this.mArrowPaint);
      }
    }
    
    public float getAlpha()
    {
      return this.mAlpha;
    }
    
    public float getDiameter()
    {
      return this.mDiameter;
    }
    
    public float getTranslationX()
    {
      return this.mTranslationX;
    }
    
    void onRtlPropertiesChanged()
    {
      if (PagingIndicator.this.mIsLtr) {}
      for (float f = 1.0F;; f = -1.0F)
      {
        this.mLayoutDirection = f;
        return;
      }
    }
    
    void select()
    {
      this.mTranslationX = 0.0F;
      this.mCenterX = 0.0F;
      this.mDiameter = PagingIndicator.this.mArrowDiameter;
      this.mRadius = PagingIndicator.this.mArrowRadius;
      this.mArrowImageRadius = (this.mRadius * PagingIndicator.this.mArrowToBgRatio);
      this.mAlpha = 1.0F;
      adjustAlpha();
    }
    
    public void setAlpha(float paramFloat)
    {
      this.mAlpha = paramFloat;
      adjustAlpha();
      PagingIndicator.this.invalidate();
    }
    
    public void setDiameter(float paramFloat)
    {
      this.mDiameter = paramFloat;
      this.mRadius = (paramFloat / 2.0F);
      this.mArrowImageRadius = (paramFloat / 2.0F * PagingIndicator.this.mArrowToBgRatio);
      PagingIndicator.this.invalidate();
    }
    
    public void setTranslationX(float paramFloat)
    {
      this.mTranslationX = (this.mDirection * paramFloat * this.mLayoutDirection);
      PagingIndicator.this.invalidate();
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/PagingIndicator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */