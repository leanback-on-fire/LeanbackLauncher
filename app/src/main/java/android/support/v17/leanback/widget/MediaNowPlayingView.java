package android.support.v17.leanback.widget;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.RestrictTo;
import android.support.v17.leanback.R.id;
import android.support.v17.leanback.R.layout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;

@RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
public class MediaNowPlayingView
  extends LinearLayout
{
  private final ImageView mImage1;
  private final ImageView mImage2;
  private final ImageView mImage3;
  protected final LinearInterpolator mLinearInterpolator = new LinearInterpolator();
  private final ObjectAnimator mObjectAnimator1;
  private final ObjectAnimator mObjectAnimator2;
  private final ObjectAnimator mObjectAnimator3;
  
  public MediaNowPlayingView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    LayoutInflater.from(paramContext).inflate(R.layout.lb_playback_now_playing_bars, this, true);
    this.mImage1 = ((ImageView)findViewById(R.id.bar1));
    this.mImage2 = ((ImageView)findViewById(R.id.bar2));
    this.mImage3 = ((ImageView)findViewById(R.id.bar3));
    this.mImage1.setPivotY(this.mImage1.getDrawable().getIntrinsicHeight());
    this.mImage2.setPivotY(this.mImage2.getDrawable().getIntrinsicHeight());
    this.mImage3.setPivotY(this.mImage3.getDrawable().getIntrinsicHeight());
    setDropScale(this.mImage1);
    setDropScale(this.mImage2);
    setDropScale(this.mImage3);
    this.mObjectAnimator1 = ObjectAnimator.ofFloat(this.mImage1, "scaleY", new float[] { 0.41666666F, 0.25F, 0.41666666F, 0.5833333F, 0.75F, 0.8333333F, 0.9166667F, 1.0F, 0.9166667F, 1.0F, 0.8333333F, 0.6666667F, 0.5F, 0.33333334F, 0.16666667F, 0.33333334F, 0.5F, 0.5833333F, 0.75F, 0.9166667F, 0.75F, 0.5833333F, 0.41666666F, 0.25F, 0.41666666F, 0.6666667F, 0.41666666F, 0.25F, 0.33333334F, 0.41666666F });
    this.mObjectAnimator1.setRepeatCount(-1);
    this.mObjectAnimator1.setDuration(2320L);
    this.mObjectAnimator1.setInterpolator(this.mLinearInterpolator);
    this.mObjectAnimator2 = ObjectAnimator.ofFloat(this.mImage2, "scaleY", new float[] { 1.0F, 0.9166667F, 0.8333333F, 0.9166667F, 1.0F, 0.9166667F, 0.75F, 0.5833333F, 0.75F, 0.9166667F, 1.0F, 0.8333333F, 0.6666667F, 0.8333333F, 1.0F, 0.9166667F, 0.75F, 0.41666666F, 0.25F, 0.41666666F, 0.6666667F, 0.8333333F, 1.0F, 0.8333333F, 0.75F, 0.6666667F, 1.0F });
    this.mObjectAnimator2.setRepeatCount(-1);
    this.mObjectAnimator2.setDuration(2080L);
    this.mObjectAnimator2.setInterpolator(this.mLinearInterpolator);
    this.mObjectAnimator3 = ObjectAnimator.ofFloat(this.mImage3, "scaleY", new float[] { 0.6666667F, 0.75F, 0.8333333F, 1.0F, 0.9166667F, 0.75F, 0.5833333F, 0.41666666F, 0.5833333F, 0.6666667F, 0.75F, 1.0F, 0.9166667F, 1.0F, 0.75F, 0.5833333F, 0.75F, 0.9166667F, 1.0F, 0.8333333F, 0.6666667F, 0.75F, 0.5833333F, 0.41666666F, 0.25F, 0.6666667F });
    this.mObjectAnimator3.setRepeatCount(-1);
    this.mObjectAnimator3.setDuration(2000L);
    this.mObjectAnimator3.setInterpolator(this.mLinearInterpolator);
  }
  
  static void setDropScale(View paramView)
  {
    paramView.setScaleY(0.083333336F);
  }
  
  private void startAnimation()
  {
    startAnimation(this.mObjectAnimator1);
    startAnimation(this.mObjectAnimator2);
    startAnimation(this.mObjectAnimator3);
    this.mImage1.setVisibility(0);
    this.mImage2.setVisibility(0);
    this.mImage3.setVisibility(0);
  }
  
  private void startAnimation(Animator paramAnimator)
  {
    if (!paramAnimator.isStarted()) {
      paramAnimator.start();
    }
  }
  
  private void stopAnimation()
  {
    stopAnimation(this.mObjectAnimator1, this.mImage1);
    stopAnimation(this.mObjectAnimator2, this.mImage2);
    stopAnimation(this.mObjectAnimator3, this.mImage3);
    this.mImage1.setVisibility(8);
    this.mImage2.setVisibility(8);
    this.mImage3.setVisibility(8);
  }
  
  private void stopAnimation(Animator paramAnimator, View paramView)
  {
    if (paramAnimator.isStarted())
    {
      paramAnimator.cancel();
      setDropScale(paramView);
    }
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    if (getVisibility() == 0) {
      startAnimation();
    }
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    stopAnimation();
  }
  
  public void setVisibility(int paramInt)
  {
    super.setVisibility(paramInt);
    if (paramInt == 8)
    {
      stopAnimation();
      return;
    }
    startAnimation();
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/MediaNowPlayingView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */