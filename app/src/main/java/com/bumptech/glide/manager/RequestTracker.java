package com.bumptech.glide.manager;

import com.bumptech.glide.request.Request;
import com.bumptech.glide.util.Util;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;

public class RequestTracker
{
  private boolean isPaused;
  private final List<Request> pendingRequests = new ArrayList();
  private final Set<Request> requests = Collections.newSetFromMap(new WeakHashMap());
  
  void addRequest(Request paramRequest)
  {
    this.requests.add(paramRequest);
  }
  
  public boolean clearRemoveAndRecycle(Request paramRequest)
  {
    if ((paramRequest != null) && ((this.requests.remove(paramRequest)) || (this.pendingRequests.remove(paramRequest)))) {}
    for (boolean bool = true;; bool = false)
    {
      if (bool)
      {
        paramRequest.clear();
        paramRequest.recycle();
      }
      return bool;
    }
  }
  
  public void clearRequests()
  {
    Iterator localIterator = Util.getSnapshot(this.requests).iterator();
    while (localIterator.hasNext()) {
      clearRemoveAndRecycle((Request)localIterator.next());
    }
    this.pendingRequests.clear();
  }
  
  public boolean isPaused()
  {
    return this.isPaused;
  }
  
  public void pauseRequests()
  {
    this.isPaused = true;
    Iterator localIterator = Util.getSnapshot(this.requests).iterator();
    while (localIterator.hasNext())
    {
      Request localRequest = (Request)localIterator.next();
      if (localRequest.isRunning())
      {
        localRequest.pause();
        this.pendingRequests.add(localRequest);
      }
    }
  }
  
  public void restartRequests()
  {
    Iterator localIterator = Util.getSnapshot(this.requests).iterator();
    while (localIterator.hasNext())
    {
      Request localRequest = (Request)localIterator.next();
      if ((!localRequest.isComplete()) && (!localRequest.isCancelled()))
      {
        localRequest.pause();
        if (!this.isPaused) {
          localRequest.begin();
        } else {
          this.pendingRequests.add(localRequest);
        }
      }
    }
  }
  
  public void resumeRequests()
  {
    this.isPaused = false;
    Iterator localIterator = Util.getSnapshot(this.requests).iterator();
    while (localIterator.hasNext())
    {
      Request localRequest = (Request)localIterator.next();
      if ((!localRequest.isComplete()) && (!localRequest.isCancelled()) && (!localRequest.isRunning())) {
        localRequest.begin();
      }
    }
    this.pendingRequests.clear();
  }
  
  public void runRequest(Request paramRequest)
  {
    this.requests.add(paramRequest);
    if (!this.isPaused)
    {
      paramRequest.begin();
      return;
    }
    this.pendingRequests.add(paramRequest);
  }
  
  public String toString()
  {
    String str = String.valueOf(super.toString());
    int i = this.requests.size();
    boolean bool = this.isPaused;
    return String.valueOf(str).length() + 41 + str + "{numRequests=" + i + ", isPaused=" + bool + "}";
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/manager/RequestTracker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */