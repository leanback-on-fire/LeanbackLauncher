package com.bumptech.glide.request;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.Option;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy;

public final class RequestOptions
  extends BaseRequestOptions<RequestOptions>
{
  private static RequestOptions centerCropOptions;
  private static RequestOptions centerInsideOptions;
  private static RequestOptions circleCropOptions;
  private static RequestOptions fitCenterOptions;
  private static RequestOptions noAnimationOptions;
  private static RequestOptions noTransformOptions;
  private static RequestOptions skipMemoryCacheFalseOptions;
  private static RequestOptions skipMemoryCacheTrueOptions;
  
  public static RequestOptions bitmapTransform(Context paramContext, @NonNull Transformation<Bitmap> paramTransformation)
  {
    return (RequestOptions)new RequestOptions().transform(paramContext, paramTransformation);
  }
  
  public static RequestOptions centerCropTransform(Context paramContext)
  {
    if (centerCropOptions == null) {
      centerCropOptions = (RequestOptions)((RequestOptions)new RequestOptions().centerCrop(paramContext.getApplicationContext())).autoLock();
    }
    return centerCropOptions;
  }
  
  public static RequestOptions centerInsideTransform(Context paramContext)
  {
    if (centerInsideOptions == null) {
      centerInsideOptions = (RequestOptions)((RequestOptions)new RequestOptions().centerInside(paramContext.getApplicationContext())).autoLock();
    }
    return centerInsideOptions;
  }
  
  public static RequestOptions circleCropTransform(Context paramContext)
  {
    if (circleCropOptions == null) {
      circleCropOptions = (RequestOptions)((RequestOptions)new RequestOptions().circleCrop(paramContext.getApplicationContext())).autoLock();
    }
    return circleCropOptions;
  }
  
  public static RequestOptions decodeTypeOf(@NonNull Class<?> paramClass)
  {
    return (RequestOptions)new RequestOptions().decode(paramClass);
  }
  
  public static RequestOptions diskCacheStrategyOf(@NonNull DiskCacheStrategy paramDiskCacheStrategy)
  {
    return (RequestOptions)new RequestOptions().diskCacheStrategy(paramDiskCacheStrategy);
  }
  
  public static RequestOptions downsampleOf(@NonNull DownsampleStrategy paramDownsampleStrategy)
  {
    return (RequestOptions)new RequestOptions().downsample(paramDownsampleStrategy);
  }
  
  public static RequestOptions encodeFormatOf(@NonNull Bitmap.CompressFormat paramCompressFormat)
  {
    return (RequestOptions)new RequestOptions().encodeFormat(paramCompressFormat);
  }
  
  public static RequestOptions encodeQualityOf(int paramInt)
  {
    return (RequestOptions)new RequestOptions().encodeQuality(paramInt);
  }
  
  public static RequestOptions errorOf(int paramInt)
  {
    return (RequestOptions)new RequestOptions().error(paramInt);
  }
  
  public static RequestOptions errorOf(@Nullable Drawable paramDrawable)
  {
    return (RequestOptions)new RequestOptions().error(paramDrawable);
  }
  
  public static RequestOptions fitCenterTransform(Context paramContext)
  {
    if (fitCenterOptions == null) {
      fitCenterOptions = (RequestOptions)((RequestOptions)new RequestOptions().fitCenter(paramContext.getApplicationContext())).autoLock();
    }
    return fitCenterOptions;
  }
  
  public static RequestOptions formatOf(@NonNull DecodeFormat paramDecodeFormat)
  {
    return (RequestOptions)new RequestOptions().format(paramDecodeFormat);
  }
  
  public static RequestOptions frameOf(long paramLong)
  {
    return (RequestOptions)new RequestOptions().frame(paramLong);
  }
  
  public static RequestOptions noAnimation()
  {
    if (noAnimationOptions == null) {
      noAnimationOptions = (RequestOptions)((RequestOptions)new RequestOptions().dontAnimate()).autoLock();
    }
    return noAnimationOptions;
  }
  
  public static RequestOptions noTransform()
  {
    if (noTransformOptions == null) {
      noTransformOptions = (RequestOptions)((RequestOptions)new RequestOptions().dontTransform()).autoLock();
    }
    return noTransformOptions;
  }
  
  public static <T> RequestOptions option(@NonNull Option<T> paramOption, @NonNull T paramT)
  {
    return (RequestOptions)new RequestOptions().set(paramOption, paramT);
  }
  
  public static RequestOptions overrideOf(int paramInt)
  {
    return overrideOf(paramInt, paramInt);
  }
  
  public static RequestOptions overrideOf(int paramInt1, int paramInt2)
  {
    return (RequestOptions)new RequestOptions().override(paramInt1, paramInt2);
  }
  
  public static RequestOptions placeholderOf(int paramInt)
  {
    return (RequestOptions)new RequestOptions().placeholder(paramInt);
  }
  
  public static RequestOptions placeholderOf(@Nullable Drawable paramDrawable)
  {
    return (RequestOptions)new RequestOptions().placeholder(paramDrawable);
  }
  
  public static RequestOptions priorityOf(@NonNull Priority paramPriority)
  {
    return (RequestOptions)new RequestOptions().priority(paramPriority);
  }
  
  public static RequestOptions signatureOf(@NonNull Key paramKey)
  {
    return (RequestOptions)new RequestOptions().signature(paramKey);
  }
  
  public static RequestOptions sizeMultiplierOf(float paramFloat)
  {
    return (RequestOptions)new RequestOptions().sizeMultiplier(paramFloat);
  }
  
  public static RequestOptions skipMemoryCacheOf(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      if (skipMemoryCacheTrueOptions == null) {
        skipMemoryCacheTrueOptions = (RequestOptions)((RequestOptions)new RequestOptions().skipMemoryCache(true)).autoLock();
      }
      return skipMemoryCacheTrueOptions;
    }
    if (skipMemoryCacheFalseOptions == null) {
      skipMemoryCacheFalseOptions = (RequestOptions)((RequestOptions)new RequestOptions().skipMemoryCache(false)).autoLock();
    }
    return skipMemoryCacheFalseOptions;
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/request/RequestOptions.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */