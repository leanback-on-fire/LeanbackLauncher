package com.bumptech.glide.load.resource.bitmap;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.os.Build;
import android.os.Build.VERSION;
import android.support.annotation.NonNull;
import android.util.Log;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.util.Preconditions;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class TransformationUtils
{
  private static final Lock BITMAP_DRAWABLE_LOCK;
  private static final Paint CIRCLE_CROP_BITMAP_PAINT;
  private static final int CIRCLE_CROP_PAINT_FLAGS = 7;
  private static final Paint CIRCLE_CROP_SHAPE_PAINT;
  private static final Paint DEFAULT_PAINT = new Paint(6);
  public static final int PAINT_FLAGS = 6;
  private static final String TAG = "TransformationUtils";
  
  static
  {
    CIRCLE_CROP_SHAPE_PAINT = new Paint(7);
    if (("XT1097".equals(Build.MODEL)) && (Build.VERSION.SDK_INT == 22)) {}
    for (Object localObject = new ReentrantLock();; localObject = new NoLock(null))
    {
      BITMAP_DRAWABLE_LOCK = (Lock)localObject;
      CIRCLE_CROP_BITMAP_PAINT = new Paint(7);
      CIRCLE_CROP_BITMAP_PAINT.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
      return;
    }
  }
  
  private static void applyMatrix(@NonNull Bitmap paramBitmap1, @NonNull Bitmap paramBitmap2, Matrix paramMatrix)
  {
    BITMAP_DRAWABLE_LOCK.lock();
    try
    {
      paramBitmap2 = new Canvas(paramBitmap2);
      paramBitmap2.drawBitmap(paramBitmap1, paramMatrix, DEFAULT_PAINT);
      clear(paramBitmap2);
      return;
    }
    finally
    {
      BITMAP_DRAWABLE_LOCK.unlock();
    }
  }
  
  public static Bitmap centerCrop(@NonNull BitmapPool paramBitmapPool, @NonNull Bitmap paramBitmap, int paramInt1, int paramInt2)
  {
    if ((paramBitmap.getWidth() == paramInt1) && (paramBitmap.getHeight() == paramInt2)) {
      return paramBitmap;
    }
    float f1 = 0.0F;
    float f2 = 0.0F;
    Matrix localMatrix = new Matrix();
    float f3;
    if (paramBitmap.getWidth() * paramInt2 > paramBitmap.getHeight() * paramInt1)
    {
      f3 = paramInt2 / paramBitmap.getHeight();
      f1 = (paramInt1 - paramBitmap.getWidth() * f3) * 0.5F;
    }
    for (;;)
    {
      localMatrix.setScale(f3, f3);
      localMatrix.postTranslate((int)(f1 + 0.5F), (int)(f2 + 0.5F));
      paramBitmapPool = paramBitmapPool.get(paramInt1, paramInt2, getSafeConfig(paramBitmap));
      setAlpha(paramBitmap, paramBitmapPool);
      applyMatrix(paramBitmap, paramBitmapPool, localMatrix);
      return paramBitmapPool;
      f3 = paramInt1 / paramBitmap.getWidth();
      f2 = (paramInt2 - paramBitmap.getHeight() * f3) * 0.5F;
    }
  }
  
  public static Bitmap centerInside(@NonNull BitmapPool paramBitmapPool, @NonNull Bitmap paramBitmap, int paramInt1, int paramInt2)
  {
    if ((paramBitmap.getWidth() <= paramInt1) && (paramBitmap.getHeight() <= paramInt2))
    {
      if (Log.isLoggable("TransformationUtils", 2)) {
        Log.v("TransformationUtils", "requested target size larger or equal to input, returning input");
      }
      return paramBitmap;
    }
    if (Log.isLoggable("TransformationUtils", 2)) {
      Log.v("TransformationUtils", "requested target size too big for input, fit centering instead");
    }
    return fitCenter(paramBitmapPool, paramBitmap, paramInt1, paramInt2);
  }
  
  public static Bitmap circleCrop(@NonNull BitmapPool paramBitmapPool, @NonNull Bitmap paramBitmap, int paramInt1, int paramInt2)
  {
    paramInt1 = Math.min(paramInt1, paramInt2);
    float f1 = paramInt1 / 2.0F;
    paramInt2 = paramBitmap.getWidth();
    int i = paramBitmap.getHeight();
    float f3 = Math.max(paramInt1 / paramInt2, paramInt1 / i);
    float f2 = f3 * paramInt2;
    f3 *= i;
    float f4 = (paramInt1 - f2) / 2.0F;
    float f5 = (paramInt1 - f3) / 2.0F;
    RectF localRectF = new RectF(f4, f5, f4 + f2, f5 + f3);
    Bitmap localBitmap1 = getAlphaSafeBitmap(paramBitmapPool, paramBitmap);
    Bitmap localBitmap2 = paramBitmapPool.get(paramInt1, paramInt1, Bitmap.Config.ARGB_8888);
    setAlphaIfAvailable(localBitmap2, true);
    BITMAP_DRAWABLE_LOCK.lock();
    try
    {
      Canvas localCanvas = new Canvas(localBitmap2);
      localCanvas.drawCircle(f1, f1, f1, CIRCLE_CROP_SHAPE_PAINT);
      localCanvas.drawBitmap(localBitmap1, null, localRectF, CIRCLE_CROP_BITMAP_PAINT);
      clear(localCanvas);
      BITMAP_DRAWABLE_LOCK.unlock();
      if (!localBitmap1.equals(paramBitmap)) {
        paramBitmapPool.put(localBitmap1);
      }
      return localBitmap2;
    }
    finally
    {
      BITMAP_DRAWABLE_LOCK.unlock();
    }
  }
  
  private static void clear(Canvas paramCanvas)
  {
    paramCanvas.setBitmap(null);
  }
  
  public static Bitmap fitCenter(@NonNull BitmapPool paramBitmapPool, @NonNull Bitmap paramBitmap, int paramInt1, int paramInt2)
  {
    if ((paramBitmap.getWidth() == paramInt1) && (paramBitmap.getHeight() == paramInt2)) {
      if (Log.isLoggable("TransformationUtils", 2)) {
        Log.v("TransformationUtils", "requested target size matches input, returning input");
      }
    }
    float f;
    int i;
    int j;
    do
    {
      return paramBitmap;
      f = Math.min(paramInt1 / paramBitmap.getWidth(), paramInt2 / paramBitmap.getHeight());
      i = (int)(paramBitmap.getWidth() * f);
      j = (int)(paramBitmap.getHeight() * f);
      if ((paramBitmap.getWidth() != i) || (paramBitmap.getHeight() != j)) {
        break;
      }
    } while (!Log.isLoggable("TransformationUtils", 2));
    Log.v("TransformationUtils", "adjusted target size matches input, returning input");
    return paramBitmap;
    paramBitmapPool = paramBitmapPool.get(i, j, getSafeConfig(paramBitmap));
    setAlpha(paramBitmap, paramBitmapPool);
    if (Log.isLoggable("TransformationUtils", 2))
    {
      Log.v("TransformationUtils", 32 + "request: " + paramInt1 + "x" + paramInt2);
      paramInt1 = paramBitmap.getWidth();
      paramInt2 = paramBitmap.getHeight();
      Log.v("TransformationUtils", 32 + "toFit:   " + paramInt1 + "x" + paramInt2);
      paramInt1 = paramBitmapPool.getWidth();
      paramInt2 = paramBitmapPool.getHeight();
      Log.v("TransformationUtils", 32 + "toReuse: " + paramInt1 + "x" + paramInt2);
      Log.v("TransformationUtils", 25 + "minPct:   " + f);
    }
    Matrix localMatrix = new Matrix();
    localMatrix.setScale(f, f);
    applyMatrix(paramBitmap, paramBitmapPool, localMatrix);
    return paramBitmapPool;
  }
  
  private static Bitmap getAlphaSafeBitmap(@NonNull BitmapPool paramBitmapPool, @NonNull Bitmap paramBitmap)
  {
    if (Bitmap.Config.ARGB_8888.equals(paramBitmap.getConfig())) {
      return paramBitmap;
    }
    paramBitmapPool = paramBitmapPool.get(paramBitmap.getWidth(), paramBitmap.getHeight(), Bitmap.Config.ARGB_8888);
    new Canvas(paramBitmapPool).drawBitmap(paramBitmap, 0.0F, 0.0F, null);
    return paramBitmapPool;
  }
  
  public static Lock getBitmapDrawableLock()
  {
    return BITMAP_DRAWABLE_LOCK;
  }
  
  public static int getExifOrientationDegrees(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return 0;
    case 5: 
    case 6: 
      return 90;
    case 3: 
    case 4: 
      return 180;
    }
    return 270;
  }
  
  private static Bitmap.Config getSafeConfig(Bitmap paramBitmap)
  {
    if (paramBitmap.getConfig() != null) {
      return paramBitmap.getConfig();
    }
    return Bitmap.Config.ARGB_8888;
  }
  
  static void initializeMatrixForRotation(int paramInt, Matrix paramMatrix)
  {
    switch (paramInt)
    {
    default: 
      return;
    case 2: 
      paramMatrix.setScale(-1.0F, 1.0F);
      return;
    case 3: 
      paramMatrix.setRotate(180.0F);
      return;
    case 4: 
      paramMatrix.setRotate(180.0F);
      paramMatrix.postScale(-1.0F, 1.0F);
      return;
    case 5: 
      paramMatrix.setRotate(90.0F);
      paramMatrix.postScale(-1.0F, 1.0F);
      return;
    case 6: 
      paramMatrix.setRotate(90.0F);
      return;
    case 7: 
      paramMatrix.setRotate(-90.0F);
      paramMatrix.postScale(-1.0F, 1.0F);
      return;
    }
    paramMatrix.setRotate(-90.0F);
  }
  
  public static Bitmap rotateImage(@NonNull Bitmap paramBitmap, int paramInt)
  {
    Bitmap localBitmap = paramBitmap;
    Object localObject = localBitmap;
    if (paramInt != 0) {}
    try
    {
      localObject = new Matrix();
      ((Matrix)localObject).setRotate(paramInt);
      localObject = Bitmap.createBitmap(paramBitmap, 0, 0, paramBitmap.getWidth(), paramBitmap.getHeight(), (Matrix)localObject, true);
      return (Bitmap)localObject;
    }
    catch (Exception paramBitmap)
    {
      do
      {
        localObject = localBitmap;
      } while (!Log.isLoggable("TransformationUtils", 6));
      Log.e("TransformationUtils", "Exception when trying to orient image", paramBitmap);
    }
    return localBitmap;
  }
  
  public static Bitmap rotateImageExif(@NonNull BitmapPool paramBitmapPool, @NonNull Bitmap paramBitmap, int paramInt)
  {
    Matrix localMatrix = new Matrix();
    initializeMatrixForRotation(paramInt, localMatrix);
    if (localMatrix.isIdentity()) {
      return paramBitmap;
    }
    RectF localRectF = new RectF(0.0F, 0.0F, paramBitmap.getWidth(), paramBitmap.getHeight());
    localMatrix.mapRect(localRectF);
    paramBitmapPool = paramBitmapPool.get(Math.round(localRectF.width()), Math.round(localRectF.height()), getSafeConfig(paramBitmap));
    localMatrix.postTranslate(-localRectF.left, -localRectF.top);
    applyMatrix(paramBitmap, paramBitmapPool, localMatrix);
    return paramBitmapPool;
  }
  
  public static Bitmap roundedCorners(@NonNull BitmapPool paramBitmapPool, @NonNull Bitmap paramBitmap, int paramInt1, int paramInt2, int paramInt3)
  {
    boolean bool2 = false;
    if (paramInt1 > 0)
    {
      bool1 = true;
      Preconditions.checkArgument(bool1, "width must be greater than 0.");
      if (paramInt2 <= 0) {
        break label224;
      }
    }
    label224:
    for (boolean bool1 = true;; bool1 = false)
    {
      Preconditions.checkArgument(bool1, "height must be greater than 0.");
      bool1 = bool2;
      if (paramInt3 > 0) {
        bool1 = true;
      }
      Preconditions.checkArgument(bool1, "roundingRadius must be greater than 0.");
      Bitmap localBitmap1 = getAlphaSafeBitmap(paramBitmapPool, paramBitmap);
      Bitmap localBitmap2 = paramBitmapPool.get(paramInt1, paramInt2, Bitmap.Config.ARGB_8888);
      setAlphaIfAvailable(localBitmap2, true);
      Object localObject = new BitmapShader(localBitmap1, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
      Paint localPaint = new Paint();
      localPaint.setAntiAlias(true);
      localPaint.setShader((Shader)localObject);
      localObject = new RectF(0.0F, 0.0F, localBitmap2.getWidth(), localBitmap2.getHeight());
      BITMAP_DRAWABLE_LOCK.lock();
      try
      {
        Canvas localCanvas = new Canvas(localBitmap2);
        localCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        localCanvas.drawRoundRect((RectF)localObject, paramInt3, paramInt3, localPaint);
        clear(localCanvas);
        BITMAP_DRAWABLE_LOCK.unlock();
        if (!localBitmap1.equals(paramBitmap)) {}
        return localBitmap2;
      }
      finally
      {
        BITMAP_DRAWABLE_LOCK.unlock();
      }
      bool1 = false;
      break;
    }
  }
  
  public static void setAlpha(Bitmap paramBitmap1, Bitmap paramBitmap2)
  {
    setAlphaIfAvailable(paramBitmap2, paramBitmap1.hasAlpha());
  }
  
  @TargetApi(12)
  private static void setAlphaIfAvailable(Bitmap paramBitmap, boolean paramBoolean)
  {
    if ((Build.VERSION.SDK_INT >= 12) && (paramBitmap != null)) {
      paramBitmap.setHasAlpha(paramBoolean);
    }
  }
  
  private static final class NoLock
    implements Lock
  {
    public void lock() {}
    
    public void lockInterruptibly()
      throws InterruptedException
    {}
    
    @NonNull
    public Condition newCondition()
    {
      throw new UnsupportedOperationException("Should not be called");
    }
    
    public boolean tryLock()
    {
      return true;
    }
    
    public boolean tryLock(long paramLong, @NonNull TimeUnit paramTimeUnit)
      throws InterruptedException
    {
      return true;
    }
    
    public void unlock() {}
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/load/resource/bitmap/TransformationUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */