package com.bumptech.glide.load.resource.gif;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.ConstantState;
import android.os.Build.VERSION;
import android.support.annotation.VisibleForTesting;
import android.view.Gravity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.gifdecoder.GifDecoder;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.util.Preconditions;
import java.nio.ByteBuffer;

public class GifDrawable
  extends Drawable
  implements GifFrameLoader.FrameCallback, Animatable
{
  public static final int LOOP_FOREVER = -1;
  public static final int LOOP_INTRINSIC = 0;
  private boolean applyGravity;
  private Rect destRect;
  private boolean isRecycled;
  private boolean isRunning;
  private boolean isStarted;
  private boolean isVisible = true;
  private int loopCount;
  private int maxLoopCount = -1;
  private Paint paint;
  private final GifState state;
  
  public GifDrawable(Context paramContext, GifDecoder paramGifDecoder, BitmapPool paramBitmapPool, Transformation<Bitmap> paramTransformation, int paramInt1, int paramInt2, Bitmap paramBitmap)
  {
    this(new GifState(paramBitmapPool, new GifFrameLoader(Glide.get(paramContext), paramGifDecoder, paramInt1, paramInt2, paramTransformation, paramBitmap)));
  }
  
  GifDrawable(GifState paramGifState)
  {
    this.state = ((GifState)Preconditions.checkNotNull(paramGifState));
  }
  
  @VisibleForTesting
  GifDrawable(GifFrameLoader paramGifFrameLoader, BitmapPool paramBitmapPool, Paint paramPaint)
  {
    this(new GifState(paramBitmapPool, paramGifFrameLoader));
    this.paint = paramPaint;
  }
  
  private Rect getDestRect()
  {
    if (this.destRect == null) {
      this.destRect = new Rect();
    }
    return this.destRect;
  }
  
  private Paint getPaint()
  {
    if (this.paint == null) {
      this.paint = new Paint(2);
    }
    return this.paint;
  }
  
  private void resetLoopCount()
  {
    this.loopCount = 0;
  }
  
  private void startRunning()
  {
    boolean bool;
    if (!this.isRecycled)
    {
      bool = true;
      Preconditions.checkArgument(bool, "You cannot start a recycled Drawable. Ensure thatyou clear any references to the Drawable when clearing the corresponding request.");
      if (this.state.frameLoader.getFrameCount() != 1) {
        break label39;
      }
      invalidateSelf();
    }
    label39:
    while (this.isRunning)
    {
      return;
      bool = false;
      break;
    }
    this.isRunning = true;
    this.state.frameLoader.subscribe(this);
    invalidateSelf();
  }
  
  private void stopRunning()
  {
    this.isRunning = false;
    this.state.frameLoader.unsubscribe(this);
  }
  
  public void draw(Canvas paramCanvas)
  {
    if (this.isRecycled) {
      return;
    }
    if (this.applyGravity)
    {
      Gravity.apply(119, getIntrinsicWidth(), getIntrinsicHeight(), getBounds(), getDestRect());
      this.applyGravity = false;
    }
    paramCanvas.drawBitmap(this.state.frameLoader.getCurrentFrame(), null, getDestRect(), getPaint());
  }
  
  public ByteBuffer getBuffer()
  {
    return this.state.frameLoader.getBuffer();
  }
  
  public Drawable.ConstantState getConstantState()
  {
    return this.state;
  }
  
  public Bitmap getFirstFrame()
  {
    return this.state.frameLoader.getFirstFrame();
  }
  
  public int getFrameCount()
  {
    return this.state.frameLoader.getFrameCount();
  }
  
  public int getFrameIndex()
  {
    return this.state.frameLoader.getCurrentIndex();
  }
  
  public Transformation<Bitmap> getFrameTransformation()
  {
    return this.state.frameLoader.getFrameTransformation();
  }
  
  public int getIntrinsicHeight()
  {
    return this.state.frameLoader.getHeight();
  }
  
  public int getIntrinsicWidth()
  {
    return this.state.frameLoader.getWidth();
  }
  
  public int getOpacity()
  {
    return -2;
  }
  
  public int getSize()
  {
    return this.state.frameLoader.getSize();
  }
  
  boolean isRecycled()
  {
    return this.isRecycled;
  }
  
  public boolean isRunning()
  {
    return this.isRunning;
  }
  
  protected void onBoundsChange(Rect paramRect)
  {
    super.onBoundsChange(paramRect);
    this.applyGravity = true;
  }
  
  @TargetApi(11)
  public void onFrameReady()
  {
    if ((Build.VERSION.SDK_INT >= 11) && (getCallback() == null))
    {
      stop();
      invalidateSelf();
    }
    do
    {
      return;
      invalidateSelf();
      if (getFrameIndex() == getFrameCount() - 1) {
        this.loopCount += 1;
      }
    } while ((this.maxLoopCount == -1) || (this.loopCount < this.maxLoopCount));
    stop();
  }
  
  public void recycle()
  {
    this.isRecycled = true;
    this.state.frameLoader.clear();
  }
  
  public void setAlpha(int paramInt)
  {
    getPaint().setAlpha(paramInt);
  }
  
  public void setColorFilter(ColorFilter paramColorFilter)
  {
    getPaint().setColorFilter(paramColorFilter);
  }
  
  public void setFrameTransformation(Transformation<Bitmap> paramTransformation, Bitmap paramBitmap)
  {
    this.state.frameLoader.setFrameTransformation(paramTransformation, paramBitmap);
  }
  
  void setIsRunning(boolean paramBoolean)
  {
    this.isRunning = paramBoolean;
  }
  
  public void setLoopCount(int paramInt)
  {
    if ((paramInt <= 0) && (paramInt != -1) && (paramInt != 0)) {
      throw new IllegalArgumentException("Loop count must be greater than 0, or equal to GlideDrawable.LOOP_FOREVER, or equal to GlideDrawable.LOOP_INTRINSIC");
    }
    if (paramInt == 0)
    {
      this.maxLoopCount = this.state.frameLoader.getLoopCount();
      return;
    }
    this.maxLoopCount = paramInt;
  }
  
  public boolean setVisible(boolean paramBoolean1, boolean paramBoolean2)
  {
    boolean bool;
    if (!this.isRecycled)
    {
      bool = true;
      Preconditions.checkArgument(bool, "Cannot change the visibility of a recycled resource. Ensure that you unset the Drawable from your View before changing the View's visibility.");
      this.isVisible = paramBoolean1;
      if (paramBoolean1) {
        break label40;
      }
      stopRunning();
    }
    for (;;)
    {
      return super.setVisible(paramBoolean1, paramBoolean2);
      bool = false;
      break;
      label40:
      if (this.isStarted) {
        startRunning();
      }
    }
  }
  
  public void start()
  {
    this.isStarted = true;
    resetLoopCount();
    if (this.isVisible) {
      startRunning();
    }
  }
  
  public void stop()
  {
    this.isStarted = false;
    stopRunning();
  }
  
  static class GifState
    extends Drawable.ConstantState
  {
    static final int GRAVITY = 119;
    final BitmapPool bitmapPool;
    final GifFrameLoader frameLoader;
    
    public GifState(BitmapPool paramBitmapPool, GifFrameLoader paramGifFrameLoader)
    {
      this.bitmapPool = paramBitmapPool;
      this.frameLoader = paramGifFrameLoader;
    }
    
    public int getChangingConfigurations()
    {
      return 0;
    }
    
    public Drawable newDrawable()
    {
      return new GifDrawable(this);
    }
    
    public Drawable newDrawable(Resources paramResources)
    {
      return newDrawable();
    }
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/load/resource/gif/GifDrawable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */