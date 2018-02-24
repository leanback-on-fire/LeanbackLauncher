package com.bumptech.glide.manager;

import com.bumptech.glide.RequestManager;
import java.util.Set;

public abstract interface RequestManagerTreeNode
{
  public abstract Set<RequestManager> getDescendants();
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/manager/RequestManagerTreeNode.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */