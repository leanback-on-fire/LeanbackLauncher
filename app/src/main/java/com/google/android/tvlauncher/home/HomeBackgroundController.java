package com.google.android.tvlauncher.home;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.AsyncTask;
import android.support.annotation.ColorInt;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import android.support.v7.graphics.Palette;
import android.view.View;
import com.google.android.tvlauncher.util.ColorUtils;

class HomeBackgroundController
{
  private static final int BITMAP_HEIGHT = 540;
  private static final float BITMAP_SCALE = 0.5F;
  private static final int BITMAP_WIDTH = 960;
  private static final float DARK_MODE_COLOR_DARKEN_FACTOR = 0.5F;
  private static final boolean DEBUG = false;
  private static final int FALLBACK_COLOR = -16777216;
  private static final int RADIAL_GRADIENT_VERTICAL_SHIFT = -300;
  private static final float STANDARD_COLOR_DARKEN_FACTOR = 0.4F;
  private static final String TAG = "HomeBackground";
  private static final float TOP_GRADIENT_COLOR_MIX_AMOUNT = 0.2F;
  private static final int TRANSITION_DURATION_MILLIS = 600;
  @ColorInt
  private int mColor1;
  @ColorInt
  private int mColor2;
  @ColorInt
  private int mColor3;
  private final Context mContext;
  private int mCurrentBackgroundIndex = 0;
  private Bitmap mCurrentBitmap;
  private BitmapDrawable mCurrentDrawable;
  private boolean mDarkMode = false;
  private Paint mLinearGradientPaint;
  private Bitmap mOverlayBitmap;
  private Paint mOverlayPaint;
  private Paint mRadialGradientPaint;
  private TransitionDrawable mTransitionDrawable;
  
  HomeBackgroundController(View paramView)
  {
    this.mContext = paramView.getContext();
    this.mCurrentBitmap = createBackgroundBitmap();
    this.mCurrentDrawable = new BitmapDrawable(this.mContext.getResources(), this.mCurrentBitmap);
    this.mCurrentDrawable.setAutoMirrored(true);
    BitmapDrawable[] arrayOfBitmapDrawable = new BitmapDrawable[2];
    arrayOfBitmapDrawable[0] = new BitmapDrawable(this.mContext.getResources(), createBackgroundBitmap());
    arrayOfBitmapDrawable[0].setAutoMirrored(true);
    arrayOfBitmapDrawable[1] = new BitmapDrawable(this.mContext.getResources(), createBackgroundBitmap());
    arrayOfBitmapDrawable[1].setAutoMirrored(true);
    this.mTransitionDrawable = new TransitionDrawable(arrayOfBitmapDrawable);
    paramView.setBackground(this.mTransitionDrawable);
  }
  
  private Bitmap createBackgroundBitmap()
  {
    return Bitmap.createBitmap(960, 540, Bitmap.Config.ARGB_8888);
  }
  
  @WorkerThread
  private void generateBitmap()
  {
    Object localObject = new LinearGradient(0.0F, 0.0F, 0.0F, 540.0F, mixColors(this.mColor2, this.mColor1, 0.2F), this.mColor2, Shader.TileMode.CLAMP);
    if (this.mLinearGradientPaint == null) {
      this.mLinearGradientPaint = new Paint();
    }
    this.mLinearGradientPaint.setShader((Shader)localObject);
    localObject = new RadialGradient(960.0F, -300.0F, (int)Math.sqrt(1627200.0D), this.mColor3, 0, Shader.TileMode.CLAMP);
    if (this.mRadialGradientPaint == null) {
      this.mRadialGradientPaint = new Paint();
    }
    this.mRadialGradientPaint.setShader((Shader)localObject);
    if (this.mOverlayBitmap == null)
    {
      this.mOverlayBitmap = BitmapFactory.decodeResource(this.mContext.getResources(), 2130837618);
      this.mOverlayPaint = new Paint();
      this.mOverlayPaint.setShader(new BitmapShader(this.mOverlayBitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT));
    }
    localObject = new Canvas(this.mCurrentBitmap);
    ((Canvas)localObject).drawRect(0.0F, 0.0F, 960.0F, 540.0F, this.mLinearGradientPaint);
    ((Canvas)localObject).drawRect(0.0F, 0.0F, 960.0F, 540.0F, this.mRadialGradientPaint);
    ((Canvas)localObject).drawRect(0.0F, 0.0F, 960.0F, 540.0F, this.mOverlayPaint);
  }
  
  @ColorInt
  @WorkerThread
  private int mixColors(@ColorInt int paramInt1, @ColorInt int paramInt2, float paramFloat)
  {
    float f = 1.0F - paramFloat;
    return Color.rgb((int)(Color.red(paramInt1) * paramFloat + Color.red(paramInt2) * f), (int)(Color.green(paramInt1) * paramFloat + Color.green(paramInt2) * f), (int)(Color.blue(paramInt1) * paramFloat + Color.blue(paramInt2) * f));
  }
  
  @MainThread
  private void onGenerationComplete()
  {
    if (this.mCurrentBackgroundIndex == 0) {}
    for (int i = 1;; i = 0)
    {
      BitmapDrawable localBitmapDrawable = (BitmapDrawable)this.mTransitionDrawable.getDrawable(i);
      this.mTransitionDrawable.setDrawable(i, this.mCurrentDrawable);
      this.mCurrentDrawable = localBitmapDrawable;
      this.mCurrentBitmap = this.mCurrentDrawable.getBitmap();
      if (this.mCurrentBackgroundIndex != 0) {
        break;
      }
      this.mTransitionDrawable.startTransition(600);
      this.mCurrentBackgroundIndex = 1;
      return;
    }
    this.mTransitionDrawable.reverseTransition(600);
    this.mCurrentBackgroundIndex = 0;
  }
  
  @MainThread
  private void updateBackground(@ColorInt int paramInt1, @ColorInt int paramInt2, @ColorInt int paramInt3)
  {
    this.mDarkMode = false;
    if ((this.mColor1 == paramInt1) && (this.mColor2 == paramInt2) && (this.mColor3 == paramInt3)) {}
    this.mColor1 = paramInt1;
    this.mColor2 = paramInt2;
    this.mColor3 = paramInt3;
    new GenerateBitmapTask(null).execute(new Void[0]);
  }
  
  void enterDarkMode()
  {
    if (this.mDarkMode) {
      return;
    }
    updateBackground(ColorUtils.darkenColor(this.mColor1, 0.5F), ColorUtils.darkenColor(this.mColor2, 0.5F), ColorUtils.darkenColor(this.mColor3, 0.5F));
    this.mDarkMode = true;
  }
  
  @MainThread
  void updateBackground(@NonNull Palette paramPalette)
  {
    updateBackground(ColorUtils.darkenColor(paramPalette.getVibrantColor(paramPalette.getMutedColor(-16777216)), 0.4F), ColorUtils.darkenColor(paramPalette.getDarkVibrantColor(paramPalette.getDarkMutedColor(-16777216)), 0.4F), ColorUtils.darkenColor(paramPalette.getLightVibrantColor(paramPalette.getLightMutedColor(-16777216)), 0.4F));
  }
  
  private class GenerateBitmapTask
    extends AsyncTask<Void, Void, Void>
  {
    private GenerateBitmapTask() {}
    
    protected Void doInBackground(Void... paramVarArgs)
    {
      HomeBackgroundController.this.generateBitmap();
      return null;
    }
    
    protected void onPostExecute(Void paramVoid)
    {
      HomeBackgroundController.this.onGenerationComplete();
    }
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/home/HomeBackgroundController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */