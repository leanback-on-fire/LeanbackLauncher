package com.bumptech.glide;

import android.annotation.TargetApi;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import com.bumptech.glide.load.engine.Engine;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.ImageViewTargetFactory;
import com.bumptech.glide.request.target.Target;

@TargetApi(14)
public class GlideContext
  extends ContextWrapper
  implements ComponentCallbacks2
{
  private final ComponentCallbacks2 componentCallbacks;
  private final RequestOptions defaultRequestOptions;
  private final Engine engine;
  private final ImageViewTargetFactory imageViewTargetFactory;
  private final int logLevel;
  private final Handler mainHandler;
  private final Registry registry;
  
  public GlideContext(Context paramContext, Registry paramRegistry, ImageViewTargetFactory paramImageViewTargetFactory, RequestOptions paramRequestOptions, Engine paramEngine, ComponentCallbacks2 paramComponentCallbacks2, int paramInt)
  {
    super(paramContext.getApplicationContext());
    this.registry = paramRegistry;
    this.imageViewTargetFactory = paramImageViewTargetFactory;
    this.defaultRequestOptions = paramRequestOptions;
    this.engine = paramEngine;
    this.componentCallbacks = paramComponentCallbacks2;
    this.logLevel = paramInt;
    this.mainHandler = new Handler(Looper.getMainLooper());
  }
  
  public <X> Target<X> buildImageViewTarget(ImageView paramImageView, Class<X> paramClass)
  {
    return this.imageViewTargetFactory.buildTarget(paramImageView, paramClass);
  }
  
  public RequestOptions getDefaultRequestOptions()
  {
    return this.defaultRequestOptions;
  }
  
  public Engine getEngine()
  {
    return this.engine;
  }
  
  public int getLogLevel()
  {
    return this.logLevel;
  }
  
  public Handler getMainHandler()
  {
    return this.mainHandler;
  }
  
  public Registry getRegistry()
  {
    return this.registry;
  }
  
  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    this.componentCallbacks.onConfigurationChanged(paramConfiguration);
  }
  
  public void onLowMemory()
  {
    this.componentCallbacks.onLowMemory();
  }
  
  public void onTrimMemory(int paramInt)
  {
    this.componentCallbacks.onTrimMemory(paramInt);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/GlideContext.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */