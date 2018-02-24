package com.bumptech.glide.request;

import android.support.annotation.Nullable;

public class ThumbnailRequestCoordinator
  implements RequestCoordinator, Request
{
  @Nullable
  private RequestCoordinator coordinator;
  private Request full;
  private boolean isRunning;
  private Request thumb;
  
  public ThumbnailRequestCoordinator()
  {
    this(null);
  }
  
  public ThumbnailRequestCoordinator(RequestCoordinator paramRequestCoordinator)
  {
    this.coordinator = paramRequestCoordinator;
  }
  
  private boolean parentCanNotifyStatusChanged()
  {
    return (this.coordinator == null) || (this.coordinator.canNotifyStatusChanged(this));
  }
  
  private boolean parentCanSetImage()
  {
    return (this.coordinator == null) || (this.coordinator.canSetImage(this));
  }
  
  private boolean parentIsAnyResourceSet()
  {
    return (this.coordinator != null) && (this.coordinator.isAnyResourceSet());
  }
  
  public void begin()
  {
    this.isRunning = true;
    if (!this.thumb.isRunning()) {
      this.thumb.begin();
    }
    if ((this.isRunning) && (!this.full.isRunning())) {
      this.full.begin();
    }
  }
  
  public boolean canNotifyStatusChanged(Request paramRequest)
  {
    return (parentCanNotifyStatusChanged()) && (paramRequest.equals(this.full)) && (!isAnyResourceSet());
  }
  
  public boolean canSetImage(Request paramRequest)
  {
    return (parentCanSetImage()) && ((paramRequest.equals(this.full)) || (!this.full.isResourceSet()));
  }
  
  public void clear()
  {
    this.isRunning = false;
    this.thumb.clear();
    this.full.clear();
  }
  
  public boolean isAnyResourceSet()
  {
    return (parentIsAnyResourceSet()) || (isResourceSet());
  }
  
  public boolean isCancelled()
  {
    return this.full.isCancelled();
  }
  
  public boolean isComplete()
  {
    return (this.full.isComplete()) || (this.thumb.isComplete());
  }
  
  public boolean isFailed()
  {
    return this.full.isFailed();
  }
  
  public boolean isPaused()
  {
    return this.full.isPaused();
  }
  
  public boolean isResourceSet()
  {
    return (this.full.isResourceSet()) || (this.thumb.isResourceSet());
  }
  
  public boolean isRunning()
  {
    return this.full.isRunning();
  }
  
  public void onRequestSuccess(Request paramRequest)
  {
    if (paramRequest.equals(this.thumb)) {}
    do
    {
      return;
      if (this.coordinator != null) {
        this.coordinator.onRequestSuccess(this);
      }
    } while (this.thumb.isComplete());
    this.thumb.clear();
  }
  
  public void pause()
  {
    this.isRunning = false;
    this.full.pause();
    this.thumb.pause();
  }
  
  public void recycle()
  {
    this.full.recycle();
    this.thumb.recycle();
  }
  
  public void setRequests(Request paramRequest1, Request paramRequest2)
  {
    this.full = paramRequest1;
    this.thumb = paramRequest2;
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/request/ThumbnailRequestCoordinator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */