package com.bumptech.glide.request;

import android.content.Context;
import android.content.res.Resources.Theme;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.Option;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.BitmapDrawableTransformation;
import com.bumptech.glide.load.resource.bitmap.BitmapEncoder;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CenterInside;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy;
import com.bumptech.glide.load.resource.bitmap.Downsampler;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.load.resource.bitmap.VideoBitmapDecoder;
import com.bumptech.glide.load.resource.gif.ByteBufferGifDecoder;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.load.resource.gif.GifDrawableTransformation;
import com.bumptech.glide.load.resource.gif.StreamGifDecoder;
import com.bumptech.glide.signature.EmptySignature;
import com.bumptech.glide.util.Preconditions;
import com.bumptech.glide.util.Util;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseRequestOptions<CHILD extends BaseRequestOptions<CHILD>>
  implements Cloneable
{
  private static final int DISK_CACHE_STRATEGY = 4;
  private static final int ERROR_ID = 32;
  private static final int ERROR_PLACEHOLDER = 16;
  private static final int FALLBACK = 8192;
  private static final int FALLBACK_ID = 16384;
  private static final int IS_CACHEABLE = 256;
  private static final int OVERRIDE = 512;
  private static final int PLACEHOLDER = 64;
  private static final int PLACEHOLDER_ID = 128;
  private static final int PRIORITY = 8;
  private static final int RESOURCE_CLASS = 4096;
  private static final int SIGNATURE = 1024;
  private static final int SIZE_MULTIPLIER = 2;
  private static final int THEME = 32768;
  private static final int TRANSFORMATION = 2048;
  private static final int TRANSFORMATION_ALLOWED = 65536;
  private static final int TRANSFORMATION_REQUIRED = 131072;
  private static final int UNSET = -1;
  private static final int USE_UNLIMITED_SOURCE_GENERATORS_POOL = 262144;
  private DiskCacheStrategy diskCacheStrategy = DiskCacheStrategy.AUTOMATIC;
  private int errorId;
  private Drawable errorPlaceholder;
  private Drawable fallbackDrawable;
  private int fallbackId;
  private int fields;
  private boolean isAutoCloneEnabled;
  private boolean isCacheable = true;
  private boolean isLocked;
  private boolean isTransformationAllowed = true;
  private boolean isTransformationRequired;
  private Options options = new Options();
  private int overrideHeight = -1;
  private int overrideWidth = -1;
  private Drawable placeholderDrawable;
  private int placeholderId;
  private Priority priority = Priority.NORMAL;
  private Class<?> resourceClass = Object.class;
  private Key signature = EmptySignature.obtain();
  private float sizeMultiplier = 1.0F;
  private Resources.Theme theme;
  private Map<Class<?>, Transformation<?>> transformations = new HashMap();
  private boolean useUnlimitedSourceGeneratorsPool;
  
  private boolean isSet(int paramInt)
  {
    return isSet(this.fields, paramInt);
  }
  
  private static boolean isSet(int paramInt1, int paramInt2)
  {
    return (paramInt1 & paramInt2) != 0;
  }
  
  private CHILD selfOrThrowIfLocked()
  {
    if (this.isLocked) {
      throw new IllegalStateException("You cannot modify locked RequestOptions, consider clone()");
    }
    return this;
  }
  
  public final CHILD apply(BaseRequestOptions<?> paramBaseRequestOptions)
  {
    if (this.isAutoCloneEnabled) {
      return clone().apply(paramBaseRequestOptions);
    }
    if (isSet(paramBaseRequestOptions.fields, 2)) {
      this.sizeMultiplier = paramBaseRequestOptions.sizeMultiplier;
    }
    if (isSet(paramBaseRequestOptions.fields, 262144)) {
      this.useUnlimitedSourceGeneratorsPool = paramBaseRequestOptions.useUnlimitedSourceGeneratorsPool;
    }
    if (isSet(paramBaseRequestOptions.fields, 4)) {
      this.diskCacheStrategy = paramBaseRequestOptions.diskCacheStrategy;
    }
    if (isSet(paramBaseRequestOptions.fields, 8)) {
      this.priority = paramBaseRequestOptions.priority;
    }
    if (isSet(paramBaseRequestOptions.fields, 16)) {
      this.errorPlaceholder = paramBaseRequestOptions.errorPlaceholder;
    }
    if (isSet(paramBaseRequestOptions.fields, 32)) {
      this.errorId = paramBaseRequestOptions.errorId;
    }
    if (isSet(paramBaseRequestOptions.fields, 64)) {
      this.placeholderDrawable = paramBaseRequestOptions.placeholderDrawable;
    }
    if (isSet(paramBaseRequestOptions.fields, 128)) {
      this.placeholderId = paramBaseRequestOptions.placeholderId;
    }
    if (isSet(paramBaseRequestOptions.fields, 256)) {
      this.isCacheable = paramBaseRequestOptions.isCacheable;
    }
    if (isSet(paramBaseRequestOptions.fields, 512))
    {
      this.overrideWidth = paramBaseRequestOptions.overrideWidth;
      this.overrideHeight = paramBaseRequestOptions.overrideHeight;
    }
    if (isSet(paramBaseRequestOptions.fields, 1024)) {
      this.signature = paramBaseRequestOptions.signature;
    }
    if (isSet(paramBaseRequestOptions.fields, 4096)) {
      this.resourceClass = paramBaseRequestOptions.resourceClass;
    }
    if (isSet(paramBaseRequestOptions.fields, 8192)) {
      this.fallbackDrawable = paramBaseRequestOptions.fallbackDrawable;
    }
    if (isSet(paramBaseRequestOptions.fields, 16384)) {
      this.fallbackId = paramBaseRequestOptions.fallbackId;
    }
    if (isSet(paramBaseRequestOptions.fields, 32768)) {
      this.theme = paramBaseRequestOptions.theme;
    }
    if (isSet(paramBaseRequestOptions.fields, 65536)) {
      this.isTransformationAllowed = paramBaseRequestOptions.isTransformationAllowed;
    }
    if (isSet(paramBaseRequestOptions.fields, 131072)) {
      this.isTransformationRequired = paramBaseRequestOptions.isTransformationRequired;
    }
    if (isSet(paramBaseRequestOptions.fields, 2048)) {
      this.transformations.putAll(paramBaseRequestOptions.transformations);
    }
    if (!this.isTransformationAllowed)
    {
      this.transformations.clear();
      this.fields &= 0xF7FF;
      this.isTransformationRequired = false;
      this.fields &= 0xFFFDFFFF;
    }
    this.fields |= paramBaseRequestOptions.fields;
    this.options.putAll(paramBaseRequestOptions.options);
    return selfOrThrowIfLocked();
  }
  
  public final CHILD autoLock()
  {
    if ((this.isLocked) && (!this.isAutoCloneEnabled)) {
      throw new IllegalStateException("You cannot auto lock an already locked options object, try clone() first");
    }
    this.isAutoCloneEnabled = true;
    return lock();
  }
  
  public CHILD centerCrop(Context paramContext)
  {
    return transform(paramContext, DownsampleStrategy.CENTER_OUTSIDE, new CenterCrop(paramContext));
  }
  
  public CHILD centerInside(Context paramContext)
  {
    return transform(paramContext, DownsampleStrategy.CENTER_INSIDE, new CenterInside(paramContext));
  }
  
  public CHILD circleCrop(Context paramContext)
  {
    return transform(paramContext, DownsampleStrategy.CENTER_OUTSIDE, new CircleCrop(paramContext));
  }
  
  public final CHILD clone()
  {
    try
    {
      BaseRequestOptions localBaseRequestOptions = (BaseRequestOptions)super.clone();
      localBaseRequestOptions.options = new Options();
      localBaseRequestOptions.options.putAll(this.options);
      localBaseRequestOptions.transformations = new HashMap();
      localBaseRequestOptions.transformations.putAll(this.transformations);
      localBaseRequestOptions.isLocked = false;
      localBaseRequestOptions.isAutoCloneEnabled = false;
      return localBaseRequestOptions;
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new RuntimeException(localCloneNotSupportedException);
    }
  }
  
  public final CHILD decode(@NonNull Class<?> paramClass)
  {
    if (this.isAutoCloneEnabled) {
      return clone().decode(paramClass);
    }
    this.resourceClass = ((Class)Preconditions.checkNotNull(paramClass));
    this.fields |= 0x1000;
    return selfOrThrowIfLocked();
  }
  
  public final CHILD diskCacheStrategy(@NonNull DiskCacheStrategy paramDiskCacheStrategy)
  {
    if (this.isAutoCloneEnabled) {
      return clone().diskCacheStrategy(paramDiskCacheStrategy);
    }
    this.diskCacheStrategy = ((DiskCacheStrategy)Preconditions.checkNotNull(paramDiskCacheStrategy));
    this.fields |= 0x4;
    return selfOrThrowIfLocked();
  }
  
  public final CHILD dontAnimate()
  {
    if (this.isAutoCloneEnabled) {
      return clone().dontAnimate();
    }
    set(ByteBufferGifDecoder.DISABLE_ANIMATION, Boolean.valueOf(true));
    set(StreamGifDecoder.DISABLE_ANIMATION, Boolean.valueOf(true));
    return selfOrThrowIfLocked();
  }
  
  public final CHILD dontTransform()
  {
    if (this.isAutoCloneEnabled) {
      return clone().dontTransform();
    }
    this.transformations.clear();
    this.fields &= 0xF7FF;
    this.isTransformationRequired = false;
    this.fields &= 0xFFFDFFFF;
    this.isTransformationAllowed = false;
    this.fields |= 0x10000;
    return selfOrThrowIfLocked();
  }
  
  public CHILD downsample(@NonNull DownsampleStrategy paramDownsampleStrategy)
  {
    return set(Downsampler.DOWNSAMPLE_STRATEGY, (DownsampleStrategy)Preconditions.checkNotNull(paramDownsampleStrategy));
  }
  
  public CHILD encodeFormat(@NonNull Bitmap.CompressFormat paramCompressFormat)
  {
    return set(BitmapEncoder.COMPRESSION_FORMAT, (Bitmap.CompressFormat)Preconditions.checkNotNull(paramCompressFormat));
  }
  
  public CHILD encodeQuality(int paramInt)
  {
    return set(BitmapEncoder.COMPRESSION_QUALITY, Integer.valueOf(paramInt));
  }
  
  public final CHILD error(int paramInt)
  {
    if (this.isAutoCloneEnabled) {
      return clone().error(paramInt);
    }
    this.errorId = paramInt;
    this.fields |= 0x20;
    return selfOrThrowIfLocked();
  }
  
  public final CHILD error(@Nullable Drawable paramDrawable)
  {
    if (this.isAutoCloneEnabled) {
      return clone().error(paramDrawable);
    }
    this.errorPlaceholder = paramDrawable;
    this.fields |= 0x10;
    return selfOrThrowIfLocked();
  }
  
  public final CHILD fallback(int paramInt)
  {
    if (this.isAutoCloneEnabled) {
      return clone().fallback(paramInt);
    }
    this.fallbackId = paramInt;
    this.fields |= 0x4000;
    return selfOrThrowIfLocked();
  }
  
  public final CHILD fallback(Drawable paramDrawable)
  {
    if (this.isAutoCloneEnabled) {
      return clone().fallback(paramDrawable);
    }
    this.fallbackDrawable = paramDrawable;
    this.fields |= 0x2000;
    return selfOrThrowIfLocked();
  }
  
  public CHILD fitCenter(Context paramContext)
  {
    return transform(paramContext, DownsampleStrategy.FIT_CENTER, new FitCenter(paramContext));
  }
  
  public CHILD format(@NonNull DecodeFormat paramDecodeFormat)
  {
    return set(Downsampler.DECODE_FORMAT, (DecodeFormat)Preconditions.checkNotNull(paramDecodeFormat));
  }
  
  public CHILD frame(long paramLong)
  {
    return set(VideoBitmapDecoder.TARGET_FRAME, Long.valueOf(paramLong));
  }
  
  public final DiskCacheStrategy getDiskCacheStrategy()
  {
    return this.diskCacheStrategy;
  }
  
  public final int getErrorId()
  {
    return this.errorId;
  }
  
  public final Drawable getErrorPlaceholder()
  {
    return this.errorPlaceholder;
  }
  
  public final Drawable getFallbackDrawable()
  {
    return this.fallbackDrawable;
  }
  
  public final int getFallbackId()
  {
    return this.fallbackId;
  }
  
  public final Options getOptions()
  {
    return this.options;
  }
  
  public final int getOverrideHeight()
  {
    return this.overrideHeight;
  }
  
  public final int getOverrideWidth()
  {
    return this.overrideWidth;
  }
  
  public final Drawable getPlaceholderDrawable()
  {
    return this.placeholderDrawable;
  }
  
  public final int getPlaceholderId()
  {
    return this.placeholderId;
  }
  
  public final Priority getPriority()
  {
    return this.priority;
  }
  
  public final Class<?> getResourceClass()
  {
    return this.resourceClass;
  }
  
  public final Key getSignature()
  {
    return this.signature;
  }
  
  public final float getSizeMultiplier()
  {
    return this.sizeMultiplier;
  }
  
  public final Resources.Theme getTheme()
  {
    return this.theme;
  }
  
  public final Map<Class<?>, Transformation<?>> getTransformations()
  {
    return this.transformations;
  }
  
  public final boolean getUseUnlimitedSourceGeneratorsPool()
  {
    return this.useUnlimitedSourceGeneratorsPool;
  }
  
  public final boolean isLocked()
  {
    return this.isLocked;
  }
  
  public final boolean isMemoryCacheable()
  {
    return this.isCacheable;
  }
  
  public final boolean isPrioritySet()
  {
    return isSet(8);
  }
  
  public final boolean isTransformationAllowed()
  {
    return this.isTransformationAllowed;
  }
  
  public final boolean isTransformationRequired()
  {
    return this.isTransformationRequired;
  }
  
  public final boolean isTransformationSet()
  {
    return isSet(2048);
  }
  
  public final boolean isValidOverride()
  {
    return Util.isValidDimensions(this.overrideWidth, this.overrideHeight);
  }
  
  public final CHILD lock()
  {
    this.isLocked = true;
    return this;
  }
  
  public CHILD optionalCenterCrop(Context paramContext)
  {
    return optionalTransform(paramContext, DownsampleStrategy.CENTER_OUTSIDE, new CenterCrop(paramContext));
  }
  
  public CHILD optionalCenterInside(Context paramContext)
  {
    return optionalTransform(paramContext, DownsampleStrategy.CENTER_INSIDE, new CenterInside(paramContext));
  }
  
  public CHILD optionalCircleCrop(Context paramContext)
  {
    return optionalTransform(paramContext, DownsampleStrategy.CENTER_OUTSIDE, new CircleCrop(paramContext));
  }
  
  public CHILD optionalFitCenter(Context paramContext)
  {
    return optionalTransform(paramContext, DownsampleStrategy.FIT_CENTER, new FitCenter(paramContext));
  }
  
  public CHILD optionalTransform(Context paramContext, Transformation<Bitmap> paramTransformation)
  {
    if (this.isAutoCloneEnabled) {
      return clone().optionalTransform(paramContext, paramTransformation);
    }
    optionalTransform(Bitmap.class, paramTransformation);
    optionalTransform(BitmapDrawable.class, new BitmapDrawableTransformation(paramContext, paramTransformation));
    optionalTransform(GifDrawable.class, new GifDrawableTransformation(paramContext, paramTransformation));
    return selfOrThrowIfLocked();
  }
  
  final CHILD optionalTransform(Context paramContext, DownsampleStrategy paramDownsampleStrategy, Transformation<Bitmap> paramTransformation)
  {
    if (this.isAutoCloneEnabled) {
      return clone().optionalTransform(paramContext, paramDownsampleStrategy, paramTransformation);
    }
    downsample(paramDownsampleStrategy);
    return optionalTransform(paramContext, paramTransformation);
  }
  
  public final <T> CHILD optionalTransform(Class<T> paramClass, Transformation<T> paramTransformation)
  {
    if (this.isAutoCloneEnabled) {
      return clone().optionalTransform(paramClass, paramTransformation);
    }
    Preconditions.checkNotNull(paramClass);
    Preconditions.checkNotNull(paramTransformation);
    this.transformations.put(paramClass, paramTransformation);
    this.fields |= 0x800;
    this.isTransformationAllowed = true;
    this.fields |= 0x10000;
    return selfOrThrowIfLocked();
  }
  
  public final CHILD override(int paramInt)
  {
    return override(paramInt, paramInt);
  }
  
  public final CHILD override(int paramInt1, int paramInt2)
  {
    if (this.isAutoCloneEnabled) {
      return clone().override(paramInt1, paramInt2);
    }
    this.overrideWidth = paramInt1;
    this.overrideHeight = paramInt2;
    this.fields |= 0x200;
    return selfOrThrowIfLocked();
  }
  
  public final CHILD placeholder(int paramInt)
  {
    if (this.isAutoCloneEnabled) {
      return clone().placeholder(paramInt);
    }
    this.placeholderId = paramInt;
    this.fields |= 0x80;
    return selfOrThrowIfLocked();
  }
  
  public final CHILD placeholder(@Nullable Drawable paramDrawable)
  {
    if (this.isAutoCloneEnabled) {
      return clone().placeholder(paramDrawable);
    }
    this.placeholderDrawable = paramDrawable;
    this.fields |= 0x40;
    return selfOrThrowIfLocked();
  }
  
  public final CHILD priority(@NonNull Priority paramPriority)
  {
    if (this.isAutoCloneEnabled) {
      return clone().priority(paramPriority);
    }
    this.priority = ((Priority)Preconditions.checkNotNull(paramPriority));
    this.fields |= 0x8;
    return selfOrThrowIfLocked();
  }
  
  public final <T> CHILD set(@NonNull Option<T> paramOption, @NonNull T paramT)
  {
    if (this.isAutoCloneEnabled) {
      return clone().set(paramOption, paramT);
    }
    Preconditions.checkNotNull(paramOption);
    Preconditions.checkNotNull(paramT);
    this.options.set(paramOption, paramT);
    return selfOrThrowIfLocked();
  }
  
  public final CHILD signature(@NonNull Key paramKey)
  {
    if (this.isAutoCloneEnabled) {
      return clone().signature(paramKey);
    }
    this.signature = ((Key)Preconditions.checkNotNull(paramKey));
    this.fields |= 0x400;
    return selfOrThrowIfLocked();
  }
  
  public final CHILD sizeMultiplier(float paramFloat)
  {
    if (this.isAutoCloneEnabled) {
      return clone().sizeMultiplier(paramFloat);
    }
    if ((paramFloat < 0.0F) || (paramFloat > 1.0F)) {
      throw new IllegalArgumentException("sizeMultiplier must be between 0 and 1");
    }
    this.sizeMultiplier = paramFloat;
    this.fields |= 0x2;
    return selfOrThrowIfLocked();
  }
  
  public final CHILD skipMemoryCache(boolean paramBoolean)
  {
    boolean bool = true;
    if (this.isAutoCloneEnabled) {
      return clone().skipMemoryCache(true);
    }
    if (!paramBoolean) {}
    for (paramBoolean = bool;; paramBoolean = false)
    {
      this.isCacheable = paramBoolean;
      this.fields |= 0x100;
      return selfOrThrowIfLocked();
    }
  }
  
  public final CHILD theme(Resources.Theme paramTheme)
  {
    if (this.isAutoCloneEnabled) {
      return clone().theme(paramTheme);
    }
    this.theme = paramTheme;
    this.fields |= 0x8000;
    return selfOrThrowIfLocked();
  }
  
  public CHILD transform(Context paramContext, @NonNull Transformation<Bitmap> paramTransformation)
  {
    if (this.isAutoCloneEnabled) {
      return clone().transform(paramContext, paramTransformation);
    }
    optionalTransform(paramContext, paramTransformation);
    this.isTransformationRequired = true;
    this.fields |= 0x20000;
    return selfOrThrowIfLocked();
  }
  
  final CHILD transform(Context paramContext, DownsampleStrategy paramDownsampleStrategy, Transformation<Bitmap> paramTransformation)
  {
    if (this.isAutoCloneEnabled) {
      return clone().transform(paramContext, paramDownsampleStrategy, paramTransformation);
    }
    downsample(paramDownsampleStrategy);
    return transform(paramContext, paramTransformation);
  }
  
  public final <T> CHILD transform(Class<T> paramClass, Transformation<T> paramTransformation)
  {
    if (this.isAutoCloneEnabled) {
      return clone().transform(paramClass, paramTransformation);
    }
    optionalTransform(paramClass, paramTransformation);
    this.isTransformationRequired = true;
    this.fields |= 0x20000;
    return selfOrThrowIfLocked();
  }
  
  public final CHILD useUnlimitedSourceGeneratorsPool(boolean paramBoolean)
  {
    if (this.isAutoCloneEnabled) {
      return clone().useUnlimitedSourceGeneratorsPool(paramBoolean);
    }
    this.useUnlimitedSourceGeneratorsPool = paramBoolean;
    this.fields |= 0x40000;
    return selfOrThrowIfLocked();
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/request/BaseRequestOptions.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */