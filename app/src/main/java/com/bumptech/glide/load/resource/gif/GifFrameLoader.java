package com.bumptech.glide.load.resource.gif;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.gifdecoder.GifDecoder;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.bumptech.glide.util.Preconditions;
import com.bumptech.glide.util.Util;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

class GifFrameLoader
{
  private final BitmapPool bitmapPool;
  private final List<FrameCallback> callbacks = new ArrayList();
  private final Context context;
  private DelayTarget current;
  private Bitmap firstFrame;
  private final GifDecoder gifDecoder;
  private final Handler handler;
  private boolean isCleared;
  private boolean isLoadPending = false;
  private boolean isRunning = false;
  private DelayTarget next;
  private RequestBuilder<Bitmap> requestBuilder;
  private final RequestManager requestManager;
  private Transformation<Bitmap> transformation;
  
  GifFrameLoader(Context paramContext, BitmapPool paramBitmapPool, RequestManager paramRequestManager, GifDecoder paramGifDecoder, Handler paramHandler, RequestBuilder<Bitmap> paramRequestBuilder, Transformation<Bitmap> paramTransformation, Bitmap paramBitmap)
  {
    this.requestManager = paramRequestManager;
    paramRequestManager = paramHandler;
    if (paramHandler == null) {
      paramRequestManager = new Handler(Looper.getMainLooper(), new FrameLoaderCallback(null));
    }
    this.context = paramContext;
    this.bitmapPool = paramBitmapPool;
    this.handler = paramRequestManager;
    this.requestBuilder = paramRequestBuilder;
    this.gifDecoder = paramGifDecoder;
    setFrameTransformation(paramTransformation, paramBitmap);
  }
  
  public GifFrameLoader(Glide paramGlide, GifDecoder paramGifDecoder, int paramInt1, int paramInt2, Transformation<Bitmap> paramTransformation, Bitmap paramBitmap)
  {
    this(paramGlide.getContext(), paramGlide.getBitmapPool(), Glide.with(paramGlide.getContext()), paramGifDecoder, null, getRequestBuilder(Glide.with(paramGlide.getContext()), paramInt1, paramInt2), paramTransformation, paramBitmap);
  }
  
  private int getFrameSize()
  {
    return Util.getBitmapByteSize(getCurrentFrame().getWidth(), getCurrentFrame().getHeight(), getCurrentFrame().getConfig());
  }
  
  private static RequestBuilder<Bitmap> getRequestBuilder(RequestManager paramRequestManager, int paramInt1, int paramInt2)
  {
    return paramRequestManager.asBitmap().apply(((RequestOptions)RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE).skipMemoryCache(true)).override(paramInt1, paramInt2));
  }
  
  private void loadNextFrame()
  {
    if ((!this.isRunning) || (this.isLoadPending)) {
      return;
    }
    this.isLoadPending = true;
    int i = this.gifDecoder.getNextDelay();
    long l1 = SystemClock.uptimeMillis();
    long l2 = i;
    this.gifDecoder.advance();
    this.next = new DelayTarget(this.handler, this.gifDecoder.getCurrentFrameIndex(), l1 + l2);
    this.requestBuilder.clone().apply(RequestOptions.signatureOf(new FrameSignature())).load(this.gifDecoder).into(this.next);
  }
  
  private void recycleFirstFrame()
  {
    if (this.firstFrame != null)
    {
      this.bitmapPool.put(this.firstFrame);
      this.firstFrame = null;
    }
  }
  
  private void start()
  {
    if (this.isRunning) {
      return;
    }
    this.isRunning = true;
    this.isCleared = false;
    loadNextFrame();
  }
  
  private void stop()
  {
    this.isRunning = false;
  }
  
  void clear()
  {
    this.callbacks.clear();
    recycleFirstFrame();
    stop();
    if (this.current != null)
    {
      this.requestManager.clear(this.current);
      this.current = null;
    }
    if (this.next != null)
    {
      this.requestManager.clear(this.next);
      this.next = null;
    }
    this.gifDecoder.clear();
    this.isCleared = true;
  }
  
  ByteBuffer getBuffer()
  {
    return this.gifDecoder.getData().asReadOnlyBuffer();
  }
  
  Bitmap getCurrentFrame()
  {
    if (this.current != null) {
      return this.current.getResource();
    }
    return this.firstFrame;
  }
  
  int getCurrentIndex()
  {
    if (this.current != null) {
      return this.current.index;
    }
    return -1;
  }
  
  Bitmap getFirstFrame()
  {
    return this.firstFrame;
  }
  
  int getFrameCount()
  {
    return this.gifDecoder.getFrameCount();
  }
  
  Transformation<Bitmap> getFrameTransformation()
  {
    return this.transformation;
  }
  
  int getHeight()
  {
    return getCurrentFrame().getHeight();
  }
  
  int getLoopCount()
  {
    return this.gifDecoder.getLoopCount();
  }
  
  int getSize()
  {
    return this.gifDecoder.getByteSize() + getFrameSize();
  }
  
  int getWidth()
  {
    return getCurrentFrame().getWidth();
  }
  
  void onFrameReady(DelayTarget paramDelayTarget)
  {
    if (this.isCleared)
    {
      this.handler.obtainMessage(2, paramDelayTarget).sendToTarget();
      return;
    }
    if (paramDelayTarget.getResource() != null)
    {
      recycleFirstFrame();
      DelayTarget localDelayTarget = this.current;
      this.current = paramDelayTarget;
      int i = this.callbacks.size() - 1;
      while (i >= 0)
      {
        ((FrameCallback)this.callbacks.get(i)).onFrameReady();
        i -= 1;
      }
      if (localDelayTarget != null) {
        this.handler.obtainMessage(2, localDelayTarget).sendToTarget();
      }
    }
    this.isLoadPending = false;
    loadNextFrame();
  }
  
  void setFrameTransformation(Transformation<Bitmap> paramTransformation, Bitmap paramBitmap)
  {
    this.transformation = ((Transformation)Preconditions.checkNotNull(paramTransformation));
    this.firstFrame = ((Bitmap)Preconditions.checkNotNull(paramBitmap));
    this.requestBuilder = this.requestBuilder.apply(new RequestOptions().transform(this.context, paramTransformation));
  }
  
  void subscribe(FrameCallback paramFrameCallback)
  {
    if (this.isCleared) {
      throw new IllegalStateException("Cannot subscribe to a cleared frame loader");
    }
    boolean bool = this.callbacks.isEmpty();
    if (this.callbacks.contains(paramFrameCallback)) {
      throw new IllegalStateException("Cannot subscribe twice in a row");
    }
    this.callbacks.add(paramFrameCallback);
    if (bool) {
      start();
    }
  }
  
  void unsubscribe(FrameCallback paramFrameCallback)
  {
    this.callbacks.remove(paramFrameCallback);
    if (this.callbacks.isEmpty()) {
      stop();
    }
  }
  
  static class DelayTarget
    extends SimpleTarget<Bitmap>
  {
    private final Handler handler;
    private final int index;
    private Bitmap resource;
    private final long targetTime;
    
    DelayTarget(Handler paramHandler, int paramInt, long paramLong)
    {
      this.handler = paramHandler;
      this.index = paramInt;
      this.targetTime = paramLong;
    }
    
    Bitmap getResource()
    {
      return this.resource;
    }
    
    public void onResourceReady(Bitmap paramBitmap, Transition<? super Bitmap> paramTransition)
    {
      this.resource = paramBitmap;
      paramBitmap = this.handler.obtainMessage(1, this);
      this.handler.sendMessageAtTime(paramBitmap, this.targetTime);
    }
  }
  
  public static abstract interface FrameCallback
  {
    public abstract void onFrameReady();
  }
  
  private class FrameLoaderCallback
    implements Handler.Callback
  {
    public static final int MSG_CLEAR = 2;
    public static final int MSG_DELAY = 1;
    
    private FrameLoaderCallback() {}
    
    public boolean handleMessage(Message paramMessage)
    {
      if (paramMessage.what == 1)
      {
        paramMessage = (GifFrameLoader.DelayTarget)paramMessage.obj;
        GifFrameLoader.this.onFrameReady(paramMessage);
        return true;
      }
      if (paramMessage.what == 2)
      {
        paramMessage = (GifFrameLoader.DelayTarget)paramMessage.obj;
        GifFrameLoader.this.requestManager.clear(paramMessage);
      }
      return false;
    }
  }
  
  static class FrameSignature
    implements Key
  {
    private final UUID uuid;
    
    public FrameSignature()
    {
      this(UUID.randomUUID());
    }
    
    FrameSignature(UUID paramUUID)
    {
      this.uuid = paramUUID;
    }
    
    public boolean equals(Object paramObject)
    {
      if ((paramObject instanceof FrameSignature)) {
        return ((FrameSignature)paramObject).uuid.equals(this.uuid);
      }
      return false;
    }
    
    public int hashCode()
    {
      return this.uuid.hashCode();
    }
    
    public void updateDiskCacheKey(MessageDigest paramMessageDigest)
    {
      throw new UnsupportedOperationException("Not implemented");
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/load/resource/gif/GifFrameLoader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */