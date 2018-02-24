package android.support.v17.leanback.widget;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.ColorInt;
import android.support.v17.leanback.R.attr;
import android.support.v17.leanback.R.color;
import android.support.v17.leanback.R.dimen;
import android.support.v17.leanback.R.drawable;
import android.support.v17.leanback.R.fraction;
import android.support.v17.leanback.R.id;
import android.support.v17.leanback.R.integer;
import android.support.v17.leanback.R.layout;
import android.support.v17.leanback.R.styleable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewPropertyAnimator;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class SearchOrbView
  extends FrameLayout
  implements View.OnClickListener
{
  private boolean mAttachedToWindow;
  private boolean mColorAnimationEnabled;
  private ValueAnimator mColorAnimator;
  private final ArgbEvaluator mColorEvaluator = new ArgbEvaluator();
  private Colors mColors;
  private final ValueAnimator.AnimatorUpdateListener mFocusUpdateListener = new ValueAnimator.AnimatorUpdateListener()
  {
    public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
    {
      SearchOrbView.this.setSearchOrbZ(paramAnonymousValueAnimator.getAnimatedFraction());
    }
  };
  private final float mFocusedZ;
  private final float mFocusedZoom;
  private ImageView mIcon;
  private Drawable mIconDrawable;
  private View.OnClickListener mListener;
  private final int mPulseDurationMs;
  private View mRootView;
  private final int mScaleDurationMs;
  private View mSearchOrbView;
  private ValueAnimator mShadowFocusAnimator;
  private final float mUnfocusedZ;
  private final ValueAnimator.AnimatorUpdateListener mUpdateListener = new ValueAnimator.AnimatorUpdateListener()
  {
    public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
    {
      paramAnonymousValueAnimator = (Integer)paramAnonymousValueAnimator.getAnimatedValue();
      SearchOrbView.this.setOrbViewColor(paramAnonymousValueAnimator.intValue());
    }
  };
  
  public SearchOrbView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public SearchOrbView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, R.attr.searchOrbViewStyle);
  }
  
  public SearchOrbView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    Resources localResources = paramContext.getResources();
    this.mRootView = ((LayoutInflater)paramContext.getSystemService("layout_inflater")).inflate(getLayoutResourceId(), this, true);
    this.mSearchOrbView = this.mRootView.findViewById(R.id.search_orb);
    this.mIcon = ((ImageView)this.mRootView.findViewById(R.id.icon));
    this.mFocusedZoom = paramContext.getResources().getFraction(R.fraction.lb_search_orb_focused_zoom, 1, 1);
    this.mPulseDurationMs = paramContext.getResources().getInteger(R.integer.lb_search_orb_pulse_duration_ms);
    this.mScaleDurationMs = paramContext.getResources().getInteger(R.integer.lb_search_orb_scale_duration_ms);
    this.mFocusedZ = paramContext.getResources().getDimensionPixelSize(R.dimen.lb_search_orb_focused_z);
    this.mUnfocusedZ = paramContext.getResources().getDimensionPixelSize(R.dimen.lb_search_orb_unfocused_z);
    TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.lbSearchOrbView, paramInt, 0);
    paramAttributeSet = localTypedArray.getDrawable(R.styleable.lbSearchOrbView_searchOrbIcon);
    paramContext = paramAttributeSet;
    if (paramAttributeSet == null) {
      paramContext = localResources.getDrawable(R.drawable.lb_ic_in_app_search);
    }
    setOrbIcon(paramContext);
    paramInt = localResources.getColor(R.color.lb_default_search_color);
    paramInt = localTypedArray.getColor(R.styleable.lbSearchOrbView_searchOrbColor, paramInt);
    setOrbColors(new Colors(paramInt, localTypedArray.getColor(R.styleable.lbSearchOrbView_searchOrbBrightColor, paramInt), localTypedArray.getColor(R.styleable.lbSearchOrbView_searchOrbIconColor, 0)));
    localTypedArray.recycle();
    setFocusable(true);
    setClipChildren(false);
    setOnClickListener(this);
    setSoundEffectsEnabled(false);
    setSearchOrbZ(0.0F);
    ShadowHelper.getInstance().setZ(this.mIcon, this.mFocusedZ);
  }
  
  private void startShadowFocusAnimation(boolean paramBoolean, int paramInt)
  {
    if (this.mShadowFocusAnimator == null)
    {
      this.mShadowFocusAnimator = ValueAnimator.ofFloat(new float[] { 0.0F, 1.0F });
      this.mShadowFocusAnimator.addUpdateListener(this.mFocusUpdateListener);
    }
    if (paramBoolean) {
      this.mShadowFocusAnimator.start();
    }
    for (;;)
    {
      this.mShadowFocusAnimator.setDuration(paramInt);
      return;
      this.mShadowFocusAnimator.reverse();
    }
  }
  
  private void updateColorAnimator()
  {
    if (this.mColorAnimator != null)
    {
      this.mColorAnimator.end();
      this.mColorAnimator = null;
    }
    if ((this.mColorAnimationEnabled) && (this.mAttachedToWindow))
    {
      this.mColorAnimator = ValueAnimator.ofObject(this.mColorEvaluator, new Object[] { Integer.valueOf(this.mColors.color), Integer.valueOf(this.mColors.brightColor), Integer.valueOf(this.mColors.color) });
      this.mColorAnimator.setRepeatCount(-1);
      this.mColorAnimator.setDuration(this.mPulseDurationMs * 2);
      this.mColorAnimator.addUpdateListener(this.mUpdateListener);
      this.mColorAnimator.start();
    }
  }
  
  void animateOnFocus(boolean paramBoolean)
  {
    if (paramBoolean) {}
    for (float f = this.mFocusedZoom;; f = 1.0F)
    {
      this.mRootView.animate().scaleX(f).scaleY(f).setDuration(this.mScaleDurationMs).start();
      startShadowFocusAnimation(paramBoolean, this.mScaleDurationMs);
      enableOrbColorAnimation(paramBoolean);
      return;
    }
  }
  
  public void enableOrbColorAnimation(boolean paramBoolean)
  {
    this.mColorAnimationEnabled = paramBoolean;
    updateColorAnimator();
  }
  
  float getFocusedZoom()
  {
    return this.mFocusedZoom;
  }
  
  int getLayoutResourceId()
  {
    return R.layout.lb_search_orb;
  }
  
  @ColorInt
  public int getOrbColor()
  {
    return this.mColors.color;
  }
  
  public Colors getOrbColors()
  {
    return this.mColors;
  }
  
  public Drawable getOrbIcon()
  {
    return this.mIconDrawable;
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    this.mAttachedToWindow = true;
    updateColorAnimator();
  }
  
  public void onClick(View paramView)
  {
    if (this.mListener != null) {
      this.mListener.onClick(paramView);
    }
  }
  
  protected void onDetachedFromWindow()
  {
    this.mAttachedToWindow = false;
    updateColorAnimator();
    super.onDetachedFromWindow();
  }
  
  protected void onFocusChanged(boolean paramBoolean, int paramInt, Rect paramRect)
  {
    super.onFocusChanged(paramBoolean, paramInt, paramRect);
    animateOnFocus(paramBoolean);
  }
  
  void scaleOrbViewOnly(float paramFloat)
  {
    this.mSearchOrbView.setScaleX(paramFloat);
    this.mSearchOrbView.setScaleY(paramFloat);
  }
  
  public void setOnOrbClickedListener(View.OnClickListener paramOnClickListener)
  {
    this.mListener = paramOnClickListener;
  }
  
  public void setOrbColor(int paramInt)
  {
    setOrbColors(new Colors(paramInt, paramInt, 0));
  }
  
  @Deprecated
  public void setOrbColor(@ColorInt int paramInt1, @ColorInt int paramInt2)
  {
    setOrbColors(new Colors(paramInt1, paramInt2, 0));
  }
  
  public void setOrbColors(Colors paramColors)
  {
    this.mColors = paramColors;
    this.mIcon.setColorFilter(this.mColors.iconColor);
    if (this.mColorAnimator == null)
    {
      setOrbViewColor(this.mColors.color);
      return;
    }
    enableOrbColorAnimation(true);
  }
  
  public void setOrbIcon(Drawable paramDrawable)
  {
    this.mIconDrawable = paramDrawable;
    this.mIcon.setImageDrawable(this.mIconDrawable);
  }
  
  void setOrbViewColor(int paramInt)
  {
    if ((this.mSearchOrbView.getBackground() instanceof GradientDrawable)) {
      ((GradientDrawable)this.mSearchOrbView.getBackground()).setColor(paramInt);
    }
  }
  
  void setSearchOrbZ(float paramFloat)
  {
    ShadowHelper.getInstance().setZ(this.mSearchOrbView, this.mUnfocusedZ + (this.mFocusedZ - this.mUnfocusedZ) * paramFloat);
  }
  
  public static class Colors
  {
    private static final float sBrightnessAlpha = 0.15F;
    @ColorInt
    public int brightColor;
    @ColorInt
    public int color;
    @ColorInt
    public int iconColor;
    
    public Colors(@ColorInt int paramInt)
    {
      this(paramInt, paramInt);
    }
    
    public Colors(@ColorInt int paramInt1, @ColorInt int paramInt2)
    {
      this(paramInt1, paramInt2, 0);
    }
    
    public Colors(@ColorInt int paramInt1, @ColorInt int paramInt2, @ColorInt int paramInt3)
    {
      this.color = paramInt1;
      int i = paramInt2;
      if (paramInt2 == paramInt1) {
        i = getBrightColor(paramInt1);
      }
      this.brightColor = i;
      this.iconColor = paramInt3;
    }
    
    public static int getBrightColor(int paramInt)
    {
      int i = (int)(Color.red(paramInt) * 0.85F + 38.25F);
      int j = (int)(Color.green(paramInt) * 0.85F + 38.25F);
      int k = (int)(Color.blue(paramInt) * 0.85F + 38.25F);
      return Color.argb((int)(Color.alpha(paramInt) * 0.85F + 38.25F), i, j, k);
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/SearchOrbView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */