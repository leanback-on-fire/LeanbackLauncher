package com.bumptech.glide;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import com.bumptech.glide.gifdecoder.GifDecoder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.data.InputStreamRewinder.Factory;
import com.bumptech.glide.load.engine.Engine;
import com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.engine.cache.MemoryCache;
import com.bumptech.glide.load.engine.prefill.BitmapPreFiller;
import com.bumptech.glide.load.engine.prefill.PreFillType.Builder;
import com.bumptech.glide.load.model.AssetUriLoader.FileDescriptorFactory;
import com.bumptech.glide.load.model.AssetUriLoader.StreamFactory;
import com.bumptech.glide.load.model.ByteArrayLoader.ByteBufferFactory;
import com.bumptech.glide.load.model.ByteArrayLoader.StreamFactory;
import com.bumptech.glide.load.model.ByteBufferEncoder;
import com.bumptech.glide.load.model.ByteBufferFileLoader.Factory;
import com.bumptech.glide.load.model.DataUrlLoader.StreamFactory;
import com.bumptech.glide.load.model.FileLoader.FileDescriptorFactory;
import com.bumptech.glide.load.model.FileLoader.StreamFactory;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.MediaStoreFileLoader.Factory;
import com.bumptech.glide.load.model.ResourceLoader.FileDescriptorFactory;
import com.bumptech.glide.load.model.ResourceLoader.StreamFactory;
import com.bumptech.glide.load.model.StreamEncoder;
import com.bumptech.glide.load.model.StringLoader.FileDescriptorFactory;
import com.bumptech.glide.load.model.StringLoader.StreamFactory;
import com.bumptech.glide.load.model.UnitModelLoader.Factory;
import com.bumptech.glide.load.model.UriLoader.FileDescriptorFactory;
import com.bumptech.glide.load.model.UriLoader.StreamFactory;
import com.bumptech.glide.load.model.UrlUriLoader.StreamFactory;
import com.bumptech.glide.load.model.stream.HttpGlideUrlLoader.Factory;
import com.bumptech.glide.load.model.stream.HttpUriLoader.Factory;
import com.bumptech.glide.load.model.stream.MediaStoreImageThumbLoader.Factory;
import com.bumptech.glide.load.model.stream.MediaStoreVideoThumbLoader.Factory;
import com.bumptech.glide.load.model.stream.UrlLoader.StreamFactory;
import com.bumptech.glide.load.resource.bitmap.BitmapDrawableDecoder;
import com.bumptech.glide.load.resource.bitmap.BitmapDrawableEncoder;
import com.bumptech.glide.load.resource.bitmap.BitmapEncoder;
import com.bumptech.glide.load.resource.bitmap.ByteBufferBitmapDecoder;
import com.bumptech.glide.load.resource.bitmap.Downsampler;
import com.bumptech.glide.load.resource.bitmap.StreamBitmapDecoder;
import com.bumptech.glide.load.resource.bitmap.VideoBitmapDecoder;
import com.bumptech.glide.load.resource.bytes.ByteBufferRewinder.Factory;
import com.bumptech.glide.load.resource.file.FileDecoder;
import com.bumptech.glide.load.resource.gif.ByteBufferGifDecoder;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.load.resource.gif.GifDrawableEncoder;
import com.bumptech.glide.load.resource.gif.GifFrameResourceDecoder;
import com.bumptech.glide.load.resource.gif.StreamGifDecoder;
import com.bumptech.glide.load.resource.transcode.BitmapBytesTranscoder;
import com.bumptech.glide.load.resource.transcode.BitmapDrawableTranscoder;
import com.bumptech.glide.load.resource.transcode.GifDrawableBytesTranscoder;
import com.bumptech.glide.manager.ConnectivityMonitorFactory;
import com.bumptech.glide.manager.RequestManagerRetriever;
import com.bumptech.glide.module.GlideModule;
import com.bumptech.glide.module.ManifestParser;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.ImageViewTargetFactory;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.util.Util;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@TargetApi(14)
public class Glide
  implements ComponentCallbacks2
{
  private static final String DEFAULT_DISK_CACHE_DIR = "image_manager_disk_cache";
  private static final String TAG = "Glide";
  private static volatile Glide glide;
  private final ArrayPool arrayPool;
  private final BitmapPool bitmapPool;
  private final BitmapPreFiller bitmapPreFiller;
  private final ConnectivityMonitorFactory connectivityMonitorFactory;
  private final Engine engine;
  private final GlideContext glideContext;
  private final List<RequestManager> managers = new ArrayList();
  private final MemoryCache memoryCache;
  private final Registry registry;
  
  @TargetApi(14)
  Glide(Context paramContext, Engine paramEngine, MemoryCache paramMemoryCache, BitmapPool paramBitmapPool, ArrayPool paramArrayPool, ConnectivityMonitorFactory paramConnectivityMonitorFactory, int paramInt, RequestOptions paramRequestOptions)
  {
    this.engine = paramEngine;
    this.bitmapPool = paramBitmapPool;
    this.arrayPool = paramArrayPool;
    this.memoryCache = paramMemoryCache;
    this.connectivityMonitorFactory = paramConnectivityMonitorFactory;
    this.bitmapPreFiller = new BitmapPreFiller(paramMemoryCache, paramBitmapPool, (DecodeFormat)paramRequestOptions.getOptions().get(Downsampler.DECODE_FORMAT));
    paramMemoryCache = paramContext.getResources();
    paramConnectivityMonitorFactory = new Downsampler(paramMemoryCache.getDisplayMetrics(), paramBitmapPool, paramArrayPool);
    ByteBufferGifDecoder localByteBufferGifDecoder = new ByteBufferGifDecoder(paramContext, paramBitmapPool, paramArrayPool);
    this.registry = new Registry().register(ByteBuffer.class, new ByteBufferEncoder()).register(InputStream.class, new StreamEncoder(paramArrayPool)).append(ByteBuffer.class, Bitmap.class, new ByteBufferBitmapDecoder(paramConnectivityMonitorFactory)).append(InputStream.class, Bitmap.class, new StreamBitmapDecoder(paramConnectivityMonitorFactory, paramArrayPool)).append(ParcelFileDescriptor.class, Bitmap.class, new VideoBitmapDecoder(paramBitmapPool)).register(Bitmap.class, new BitmapEncoder()).append(ByteBuffer.class, BitmapDrawable.class, new BitmapDrawableDecoder(paramMemoryCache, paramBitmapPool, new ByteBufferBitmapDecoder(paramConnectivityMonitorFactory))).append(InputStream.class, BitmapDrawable.class, new BitmapDrawableDecoder(paramMemoryCache, paramBitmapPool, new StreamBitmapDecoder(paramConnectivityMonitorFactory, paramArrayPool))).append(ParcelFileDescriptor.class, BitmapDrawable.class, new BitmapDrawableDecoder(paramMemoryCache, paramBitmapPool, new VideoBitmapDecoder(paramBitmapPool))).register(BitmapDrawable.class, new BitmapDrawableEncoder(paramBitmapPool, new BitmapEncoder())).prepend(InputStream.class, GifDrawable.class, new StreamGifDecoder(localByteBufferGifDecoder, paramArrayPool)).prepend(ByteBuffer.class, GifDrawable.class, localByteBufferGifDecoder).register(GifDrawable.class, new GifDrawableEncoder()).append(GifDecoder.class, GifDecoder.class, new UnitModelLoader.Factory()).append(GifDecoder.class, Bitmap.class, new GifFrameResourceDecoder(paramBitmapPool)).register(new ByteBufferRewinder.Factory()).append(File.class, ByteBuffer.class, new ByteBufferFileLoader.Factory()).append(File.class, InputStream.class, new FileLoader.StreamFactory()).append(File.class, File.class, new FileDecoder()).append(File.class, ParcelFileDescriptor.class, new FileLoader.FileDescriptorFactory()).append(File.class, File.class, new UnitModelLoader.Factory()).register(new InputStreamRewinder.Factory(paramArrayPool)).append(Integer.TYPE, InputStream.class, new ResourceLoader.StreamFactory(paramMemoryCache)).append(Integer.TYPE, ParcelFileDescriptor.class, new ResourceLoader.FileDescriptorFactory(paramMemoryCache)).append(Integer.class, InputStream.class, new ResourceLoader.StreamFactory(paramMemoryCache)).append(Integer.class, ParcelFileDescriptor.class, new ResourceLoader.FileDescriptorFactory(paramMemoryCache)).append(String.class, InputStream.class, new DataUrlLoader.StreamFactory()).append(String.class, InputStream.class, new StringLoader.StreamFactory()).append(String.class, ParcelFileDescriptor.class, new StringLoader.FileDescriptorFactory()).append(Uri.class, InputStream.class, new HttpUriLoader.Factory()).append(Uri.class, InputStream.class, new AssetUriLoader.StreamFactory(paramContext.getAssets())).append(Uri.class, ParcelFileDescriptor.class, new AssetUriLoader.FileDescriptorFactory(paramContext.getAssets())).append(Uri.class, InputStream.class, new MediaStoreImageThumbLoader.Factory(paramContext)).append(Uri.class, InputStream.class, new MediaStoreVideoThumbLoader.Factory(paramContext)).append(Uri.class, InputStream.class, new UriLoader.StreamFactory(paramContext.getContentResolver())).append(Uri.class, ParcelFileDescriptor.class, new UriLoader.FileDescriptorFactory(paramContext.getContentResolver())).append(Uri.class, InputStream.class, new UrlUriLoader.StreamFactory()).append(URL.class, InputStream.class, new UrlLoader.StreamFactory()).append(Uri.class, File.class, new MediaStoreFileLoader.Factory(paramContext)).append(GlideUrl.class, InputStream.class, new HttpGlideUrlLoader.Factory()).append(byte[].class, ByteBuffer.class, new ByteArrayLoader.ByteBufferFactory()).append(byte[].class, InputStream.class, new ByteArrayLoader.StreamFactory()).register(Bitmap.class, BitmapDrawable.class, new BitmapDrawableTranscoder(paramMemoryCache, paramBitmapPool)).register(Bitmap.class, byte[].class, new BitmapBytesTranscoder()).register(GifDrawable.class, byte[].class, new GifDrawableBytesTranscoder());
    paramMemoryCache = new ImageViewTargetFactory();
    this.glideContext = new GlideContext(paramContext, this.registry, paramMemoryCache, paramRequestOptions, paramEngine, this, paramInt);
  }
  
  public static Glide get(Context paramContext)
  {
    if (glide == null)
    {
      try
      {
        if (glide != null) {
          break label129;
        }
        paramContext = paramContext.getApplicationContext();
        localObject = new ManifestParser(paramContext).parse();
        GlideBuilder localGlideBuilder = new GlideBuilder(paramContext);
        Iterator localIterator = ((List)localObject).iterator();
        while (localIterator.hasNext()) {
          ((GlideModule)localIterator.next()).applyOptions(paramContext, localGlideBuilder);
        }
        glide = localGlideBuilder.createGlide();
      }
      finally {}
      Object localObject = ((List)localObject).iterator();
      while (((Iterator)localObject).hasNext()) {
        ((GlideModule)((Iterator)localObject).next()).registerComponents(paramContext, glide.registry);
      }
    }
    label129:
    return glide;
  }
  
  @Nullable
  public static File getPhotoCacheDir(Context paramContext)
  {
    return getPhotoCacheDir(paramContext, "image_manager_disk_cache");
  }
  
  @Nullable
  public static File getPhotoCacheDir(Context paramContext, String paramString)
  {
    paramContext = paramContext.getCacheDir();
    if (paramContext != null)
    {
      paramString = new File(paramContext, paramString);
      paramContext = paramString;
      if (!paramString.mkdirs()) {
        if (paramString.exists())
        {
          paramContext = paramString;
          if (paramString.isDirectory()) {}
        }
        else
        {
          paramContext = null;
        }
      }
      return paramContext;
    }
    if (Log.isLoggable("Glide", 6)) {
      Log.e("Glide", "default disk cache dir is null");
    }
    return null;
  }
  
  @VisibleForTesting
  public static void tearDown()
  {
    glide = null;
  }
  
  public static RequestManager with(Activity paramActivity)
  {
    return RequestManagerRetriever.get().get(paramActivity);
  }
  
  @TargetApi(11)
  public static RequestManager with(android.app.Fragment paramFragment)
  {
    return RequestManagerRetriever.get().get(paramFragment);
  }
  
  public static RequestManager with(Context paramContext)
  {
    return RequestManagerRetriever.get().get(paramContext);
  }
  
  public static RequestManager with(android.support.v4.app.Fragment paramFragment)
  {
    return RequestManagerRetriever.get().get(paramFragment);
  }
  
  public static RequestManager with(FragmentActivity paramFragmentActivity)
  {
    return RequestManagerRetriever.get().get(paramFragmentActivity);
  }
  
  public void clearDiskCache()
  {
    Util.assertBackgroundThread();
    this.engine.clearDiskCache();
  }
  
  public void clearMemory()
  {
    Util.assertMainThread();
    this.memoryCache.clearMemory();
    this.bitmapPool.clearMemory();
    this.arrayPool.clearMemory();
  }
  
  public ArrayPool getArrayPool()
  {
    return this.arrayPool;
  }
  
  public BitmapPool getBitmapPool()
  {
    return this.bitmapPool;
  }
  
  ConnectivityMonitorFactory getConnectivityMonitorFactory()
  {
    return this.connectivityMonitorFactory;
  }
  
  public Context getContext()
  {
    return this.glideContext.getBaseContext();
  }
  
  GlideContext getGlideContext()
  {
    return this.glideContext;
  }
  
  public Registry getRegistry()
  {
    return this.registry;
  }
  
  public void onConfigurationChanged(Configuration paramConfiguration) {}
  
  public void onLowMemory()
  {
    clearMemory();
  }
  
  public void onTrimMemory(int paramInt)
  {
    trimMemory(paramInt);
  }
  
  public void preFillBitmapPool(PreFillType.Builder... paramVarArgs)
  {
    this.bitmapPreFiller.preFill(paramVarArgs);
  }
  
  void registerRequestManager(RequestManager paramRequestManager)
  {
    synchronized (this.managers)
    {
      if (this.managers.contains(paramRequestManager)) {
        throw new IllegalStateException("Cannot register already registered manager");
      }
    }
    this.managers.add(paramRequestManager);
  }
  
  void removeFromManagers(Target<?> paramTarget)
  {
    synchronized (this.managers)
    {
      Iterator localIterator = this.managers.iterator();
      while (localIterator.hasNext()) {
        if (((RequestManager)localIterator.next()).untrack(paramTarget)) {
          return;
        }
      }
      throw new IllegalStateException("Failed to remove target from managers");
    }
  }
  
  public void setMemoryCategory(MemoryCategory paramMemoryCategory)
  {
    Util.assertMainThread();
    this.memoryCache.setSizeMultiplier(paramMemoryCategory.getMultiplier());
    this.bitmapPool.setSizeMultiplier(paramMemoryCategory.getMultiplier());
  }
  
  public void trimMemory(int paramInt)
  {
    Util.assertMainThread();
    this.memoryCache.trimMemory(paramInt);
    this.bitmapPool.trimMemory(paramInt);
    this.arrayPool.trimMemory(paramInt);
  }
  
  void unregisterRequestManager(RequestManager paramRequestManager)
  {
    synchronized (this.managers)
    {
      if (!this.managers.contains(paramRequestManager)) {
        throw new IllegalStateException("Cannot register not yet registered manager");
      }
    }
    this.managers.remove(paramRequestManager);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/Glide.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */