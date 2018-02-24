package com.bumptech.glide.manager;

import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.util.Util;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;

public final class TargetTracker
  implements LifecycleListener
{
  private final Set<Target<?>> targets = Collections.newSetFromMap(new WeakHashMap());
  
  public void clear()
  {
    this.targets.clear();
  }
  
  public List<Target<?>> getAll()
  {
    return new ArrayList(this.targets);
  }
  
  public void onDestroy()
  {
    Iterator localIterator = Util.getSnapshot(this.targets).iterator();
    while (localIterator.hasNext()) {
      ((Target)localIterator.next()).onDestroy();
    }
  }
  
  public void onStart()
  {
    Iterator localIterator = Util.getSnapshot(this.targets).iterator();
    while (localIterator.hasNext()) {
      ((Target)localIterator.next()).onStart();
    }
  }
  
  public void onStop()
  {
    Iterator localIterator = Util.getSnapshot(this.targets).iterator();
    while (localIterator.hasNext()) {
      ((Target)localIterator.next()).onStop();
    }
  }
  
  public void track(Target<?> paramTarget)
  {
    this.targets.add(paramTarget);
  }
  
  public void untrack(Target<?> paramTarget)
  {
    this.targets.remove(paramTarget);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/manager/TargetTracker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */