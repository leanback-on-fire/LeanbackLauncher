package com.bumptech.glide;

import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.BaseRequestOptions;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.RequestCoordinator;
import com.bumptech.glide.request.RequestFutureTarget;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.SingleRequest;
import com.bumptech.glide.request.ThumbnailRequestCoordinator;
import com.bumptech.glide.request.target.PreloadTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.signature.ApplicationVersionSignature;
import com.bumptech.glide.signature.ObjectKey;
import com.bumptech.glide.util.Preconditions;
import com.bumptech.glide.util.Util;
import java.io.File;
import java.net.URL;
import java.util.UUID;

public class RequestBuilder<TranscodeType>
  implements Cloneable
{
  private static final TransitionOptions<?, ?> DEFAULT_ANIMATION_OPTIONS = new GenericTransitionOptions();
  private static final BaseRequestOptions<?> DOWNLOAD_ONLY_OPTIONS = ((RequestOptions)((RequestOptions)new RequestOptions().diskCacheStrategy(DiskCacheStrategy.DATA)).priority(Priority.LOW)).skipMemoryCache(true);
  private final GlideContext context;
  private final BaseRequestOptions<?> defaultRequestOptions;
  private boolean isModelSet;
  private boolean isThumbnailBuilt;
  @Nullable
  private Object model;
  @Nullable
  private RequestListener<TranscodeType> requestListener;
  private final RequestManager requestManager;
  @NonNull
  private BaseRequestOptions<?> requestOptions;
  @Nullable
  private Float thumbSizeMultiplier;
  @Nullable
  private RequestBuilder<TranscodeType> thumbnailBuilder;
  private final Class<TranscodeType> transcodeClass;
  private TransitionOptions<?, ? super TranscodeType> transitionOptions = DEFAULT_ANIMATION_OPTIONS;
  
  RequestBuilder(GlideContext paramGlideContext, RequestManager paramRequestManager, Class<TranscodeType> paramClass)
  {
    this.requestManager = paramRequestManager;
    this.context = ((GlideContext)Preconditions.checkNotNull(paramGlideContext));
    this.transcodeClass = paramClass;
    this.defaultRequestOptions = paramRequestManager.getDefaultRequestOptions();
    this.requestOptions = this.defaultRequestOptions;
  }
  
  RequestBuilder(Class<TranscodeType> paramClass, RequestBuilder<?> paramRequestBuilder)
  {
    this(paramRequestBuilder.context, paramRequestBuilder.requestManager, paramClass);
    this.model = paramRequestBuilder.model;
    this.isModelSet = paramRequestBuilder.isModelSet;
    this.requestOptions = paramRequestBuilder.requestOptions;
  }
  
  private Request buildRequest(Target<TranscodeType> paramTarget)
  {
    return buildRequestRecursive(paramTarget, null, this.transitionOptions, this.requestOptions.getPriority(), this.requestOptions.getOverrideWidth(), this.requestOptions.getOverrideHeight());
  }
  
  private Request buildRequestRecursive(Target<TranscodeType> paramTarget, @Nullable ThumbnailRequestCoordinator paramThumbnailRequestCoordinator, TransitionOptions<?, ? super TranscodeType> paramTransitionOptions, Priority paramPriority, int paramInt1, int paramInt2)
  {
    if (this.thumbnailBuilder != null)
    {
      if (this.isThumbnailBuilt) {
        throw new IllegalStateException("You cannot use a request as both the main request and a thumbnail, consider using clone() on the request(s) passed to thumbnail()");
      }
      Object localObject2 = this.thumbnailBuilder.transitionOptions;
      Object localObject1 = localObject2;
      if (DEFAULT_ANIMATION_OPTIONS.equals(localObject2)) {
        localObject1 = paramTransitionOptions;
      }
      if (this.thumbnailBuilder.requestOptions.isPrioritySet()) {}
      for (localObject2 = this.thumbnailBuilder.requestOptions.getPriority();; localObject2 = getThumbnailPriority(paramPriority))
      {
        int k = this.thumbnailBuilder.requestOptions.getOverrideWidth();
        int m = this.thumbnailBuilder.requestOptions.getOverrideHeight();
        int j = m;
        int i = k;
        if (Util.isValidDimensions(paramInt1, paramInt2))
        {
          j = m;
          i = k;
          if (!this.thumbnailBuilder.requestOptions.isValidOverride())
          {
            i = this.requestOptions.getOverrideWidth();
            j = this.requestOptions.getOverrideHeight();
          }
        }
        paramThumbnailRequestCoordinator = new ThumbnailRequestCoordinator(paramThumbnailRequestCoordinator);
        paramTransitionOptions = obtainRequest(paramTarget, this.requestOptions, paramThumbnailRequestCoordinator, paramTransitionOptions, paramPriority, paramInt1, paramInt2);
        this.isThumbnailBuilt = true;
        paramTarget = this.thumbnailBuilder.buildRequestRecursive(paramTarget, paramThumbnailRequestCoordinator, (TransitionOptions)localObject1, (Priority)localObject2, i, j);
        this.isThumbnailBuilt = false;
        paramThumbnailRequestCoordinator.setRequests(paramTransitionOptions, paramTarget);
        return paramThumbnailRequestCoordinator;
      }
    }
    if (this.thumbSizeMultiplier != null)
    {
      paramThumbnailRequestCoordinator = new ThumbnailRequestCoordinator(paramThumbnailRequestCoordinator);
      paramThumbnailRequestCoordinator.setRequests(obtainRequest(paramTarget, this.requestOptions, paramThumbnailRequestCoordinator, paramTransitionOptions, paramPriority, paramInt1, paramInt2), obtainRequest(paramTarget, this.requestOptions.clone().sizeMultiplier(this.thumbSizeMultiplier.floatValue()), paramThumbnailRequestCoordinator, paramTransitionOptions, getThumbnailPriority(paramPriority), paramInt1, paramInt2));
      return paramThumbnailRequestCoordinator;
    }
    return obtainRequest(paramTarget, this.requestOptions, paramThumbnailRequestCoordinator, paramTransitionOptions, paramPriority, paramInt1, paramInt2);
  }
  
  private RequestBuilder<File> getDownloadOnlyRequest()
  {
    return new RequestBuilder(File.class, this).apply(DOWNLOAD_ONLY_OPTIONS);
  }
  
  private Priority getThumbnailPriority(Priority paramPriority)
  {
    switch (paramPriority)
    {
    default: 
      paramPriority = String.valueOf(this.requestOptions.getPriority());
      throw new IllegalArgumentException(String.valueOf(paramPriority).length() + 18 + "unknown priority: " + paramPriority);
    case ???: 
      return Priority.NORMAL;
    case ???: 
      return Priority.HIGH;
    }
    return Priority.IMMEDIATE;
  }
  
  private RequestBuilder<TranscodeType> loadGeneric(@Nullable Object paramObject)
  {
    this.model = paramObject;
    this.isModelSet = true;
    return this;
  }
  
  private Request obtainRequest(Target<TranscodeType> paramTarget, BaseRequestOptions<?> paramBaseRequestOptions, RequestCoordinator paramRequestCoordinator, TransitionOptions<?, ? super TranscodeType> paramTransitionOptions, Priority paramPriority, int paramInt1, int paramInt2)
  {
    paramBaseRequestOptions.lock();
    return SingleRequest.obtain(this.context, this.model, this.transcodeClass, paramBaseRequestOptions, paramInt1, paramInt2, paramPriority, paramTarget, this.requestListener, paramRequestCoordinator, this.context.getEngine(), paramTransitionOptions.getTransitionFactory());
  }
  
  public RequestBuilder<TranscodeType> apply(@NonNull BaseRequestOptions<?> paramBaseRequestOptions)
  {
    Preconditions.checkNotNull(paramBaseRequestOptions);
    if (this.defaultRequestOptions == this.requestOptions) {}
    for (BaseRequestOptions localBaseRequestOptions = this.requestOptions.clone();; localBaseRequestOptions = this.requestOptions)
    {
      this.requestOptions = localBaseRequestOptions.apply(paramBaseRequestOptions);
      return this;
    }
  }
  
  public RequestBuilder<TranscodeType> clone()
  {
    try
    {
      RequestBuilder localRequestBuilder = (RequestBuilder)super.clone();
      localRequestBuilder.requestOptions = localRequestBuilder.requestOptions.clone();
      localRequestBuilder.transitionOptions = localRequestBuilder.transitionOptions.clone();
      return localRequestBuilder;
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new RuntimeException(localCloneNotSupportedException);
    }
  }
  
  @Deprecated
  public FutureTarget<File> downloadOnly(int paramInt1, int paramInt2)
  {
    return getDownloadOnlyRequest().submit(paramInt1, paramInt2);
  }
  
  @Deprecated
  public <Y extends Target<File>> Y downloadOnly(Y paramY)
  {
    return getDownloadOnlyRequest().into(paramY);
  }
  
  @Deprecated
  public FutureTarget<TranscodeType> into(int paramInt1, int paramInt2)
  {
    return submit(paramInt1, paramInt2);
  }
  
  public Target<TranscodeType> into(ImageView paramImageView)
  {
    Util.assertMainThread();
    Preconditions.checkNotNull(paramImageView);
    if ((!this.requestOptions.isTransformationSet()) && (this.requestOptions.isTransformationAllowed()) && (paramImageView.getScaleType() != null))
    {
      if (this.requestOptions.isLocked()) {
        this.requestOptions = this.requestOptions.clone();
      }
      switch (2.$SwitchMap$android$widget$ImageView$ScaleType[paramImageView.getScaleType().ordinal()])
      {
      }
    }
    for (;;)
    {
      return into(this.context.buildImageViewTarget(paramImageView, this.transcodeClass));
      this.requestOptions.optionalCenterCrop(this.context);
      continue;
      this.requestOptions.optionalCenterInside(this.context);
      continue;
      this.requestOptions.optionalFitCenter(this.context);
    }
  }
  
  public <Y extends Target<TranscodeType>> Y into(@NonNull Y paramY)
  {
    Util.assertMainThread();
    Preconditions.checkNotNull(paramY);
    if (!this.isModelSet) {
      throw new IllegalArgumentException("You must call #load() before calling #into()");
    }
    if (paramY.getRequest() != null) {
      this.requestManager.clear(paramY);
    }
    this.requestOptions.lock();
    Request localRequest = buildRequest(paramY);
    paramY.setRequest(localRequest);
    this.requestManager.track(paramY, localRequest);
    return paramY;
  }
  
  public RequestBuilder<TranscodeType> listener(@Nullable RequestListener<TranscodeType> paramRequestListener)
  {
    this.requestListener = paramRequestListener;
    return this;
  }
  
  public RequestBuilder<TranscodeType> load(@Nullable Uri paramUri)
  {
    return loadGeneric(paramUri);
  }
  
  public RequestBuilder<TranscodeType> load(@Nullable File paramFile)
  {
    return loadGeneric(paramFile);
  }
  
  public RequestBuilder<TranscodeType> load(@Nullable Integer paramInteger)
  {
    return loadGeneric(paramInteger).apply(RequestOptions.signatureOf(ApplicationVersionSignature.obtain(this.context)));
  }
  
  public RequestBuilder<TranscodeType> load(@Nullable Object paramObject)
  {
    return loadGeneric(paramObject);
  }
  
  public RequestBuilder<TranscodeType> load(@Nullable String paramString)
  {
    return loadGeneric(paramString);
  }
  
  @Deprecated
  public RequestBuilder<TranscodeType> load(@Nullable URL paramURL)
  {
    return loadGeneric(paramURL);
  }
  
  public RequestBuilder<TranscodeType> load(@Nullable byte[] paramArrayOfByte)
  {
    return loadGeneric(paramArrayOfByte).apply(((RequestOptions)RequestOptions.signatureOf(new ObjectKey(UUID.randomUUID().toString())).diskCacheStrategy(DiskCacheStrategy.NONE)).skipMemoryCache(true));
  }
  
  public Target<TranscodeType> preload()
  {
    return preload(Integer.MIN_VALUE, Integer.MIN_VALUE);
  }
  
  public Target<TranscodeType> preload(int paramInt1, int paramInt2)
  {
    return into(PreloadTarget.obtain(this.requestManager, paramInt1, paramInt2));
  }
  
  public FutureTarget<TranscodeType> submit()
  {
    return submit(Integer.MIN_VALUE, Integer.MIN_VALUE);
  }
  
  public FutureTarget<TranscodeType> submit(int paramInt1, int paramInt2)
  {
    final RequestFutureTarget localRequestFutureTarget = new RequestFutureTarget(this.context.getMainHandler(), paramInt1, paramInt2);
    if (Util.isOnBackgroundThread())
    {
      this.context.getMainHandler().post(new Runnable()
      {
        public void run()
        {
          if (!localRequestFutureTarget.isCancelled()) {
            RequestBuilder.this.into(localRequestFutureTarget);
          }
        }
      });
      return localRequestFutureTarget;
    }
    into(localRequestFutureTarget);
    return localRequestFutureTarget;
  }
  
  public RequestBuilder<TranscodeType> thumbnail(float paramFloat)
  {
    if ((paramFloat < 0.0F) || (paramFloat > 1.0F)) {
      throw new IllegalArgumentException("sizeMultiplier must be between 0 and 1");
    }
    this.thumbSizeMultiplier = Float.valueOf(paramFloat);
    return this;
  }
  
  public RequestBuilder<TranscodeType> thumbnail(@Nullable RequestBuilder<TranscodeType> paramRequestBuilder)
  {
    this.thumbnailBuilder = paramRequestBuilder;
    return this;
  }
  
  public RequestBuilder<TranscodeType> transition(@NonNull TransitionOptions<?, ? super TranscodeType> paramTransitionOptions)
  {
    this.transitionOptions = ((TransitionOptions)Preconditions.checkNotNull(paramTransitionOptions));
    return this;
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/RequestBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */