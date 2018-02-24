package com.bumptech.glide.manager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import com.bumptech.glide.RequestManager;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class SupportRequestManagerFragment
  extends Fragment
{
  private static final String TAG = "SupportRMFragment";
  private final HashSet<SupportRequestManagerFragment> childRequestManagerFragments = new HashSet();
  private final ActivityFragmentLifecycle lifecycle;
  @Nullable
  private Fragment parentFragmentHint;
  @Nullable
  private RequestManager requestManager;
  private final RequestManagerTreeNode requestManagerTreeNode = new SupportFragmentRequestManagerTreeNode(null);
  @Nullable
  private SupportRequestManagerFragment rootRequestManagerFragment;
  
  public SupportRequestManagerFragment()
  {
    this(new ActivityFragmentLifecycle());
  }
  
  @SuppressLint({"ValidFragment"})
  public SupportRequestManagerFragment(ActivityFragmentLifecycle paramActivityFragmentLifecycle)
  {
    this.lifecycle = paramActivityFragmentLifecycle;
  }
  
  private void addChildRequestManagerFragment(SupportRequestManagerFragment paramSupportRequestManagerFragment)
  {
    this.childRequestManagerFragments.add(paramSupportRequestManagerFragment);
  }
  
  private Fragment getParentFragmentUsingHint()
  {
    Fragment localFragment = getParentFragment();
    if (localFragment != null) {
      return localFragment;
    }
    return this.parentFragmentHint;
  }
  
  private boolean isDescendant(Fragment paramFragment)
  {
    Fragment localFragment = getParentFragmentUsingHint();
    while (paramFragment.getParentFragment() != null)
    {
      if (paramFragment.getParentFragment() == localFragment) {
        return true;
      }
      paramFragment = paramFragment.getParentFragment();
    }
    return false;
  }
  
  private void registerFragmentWithRoot(FragmentActivity paramFragmentActivity)
  {
    unregisterFragmentWithRoot();
    this.rootRequestManagerFragment = RequestManagerRetriever.get().getSupportRequestManagerFragment(paramFragmentActivity.getSupportFragmentManager(), null);
    if (this.rootRequestManagerFragment != this) {
      this.rootRequestManagerFragment.addChildRequestManagerFragment(this);
    }
  }
  
  private void removeChildRequestManagerFragment(SupportRequestManagerFragment paramSupportRequestManagerFragment)
  {
    this.childRequestManagerFragments.remove(paramSupportRequestManagerFragment);
  }
  
  private void unregisterFragmentWithRoot()
  {
    if (this.rootRequestManagerFragment != null)
    {
      this.rootRequestManagerFragment.removeChildRequestManagerFragment(this);
      this.rootRequestManagerFragment = null;
    }
  }
  
  public Set<SupportRequestManagerFragment> getDescendantRequestManagerFragments()
  {
    if (this.rootRequestManagerFragment == null) {
      return Collections.emptySet();
    }
    if (this.rootRequestManagerFragment == this) {
      return Collections.unmodifiableSet(this.childRequestManagerFragments);
    }
    HashSet localHashSet = new HashSet();
    Iterator localIterator = this.rootRequestManagerFragment.getDescendantRequestManagerFragments().iterator();
    while (localIterator.hasNext())
    {
      SupportRequestManagerFragment localSupportRequestManagerFragment = (SupportRequestManagerFragment)localIterator.next();
      if (isDescendant(localSupportRequestManagerFragment.getParentFragmentUsingHint())) {
        localHashSet.add(localSupportRequestManagerFragment);
      }
    }
    return Collections.unmodifiableSet(localHashSet);
  }
  
  ActivityFragmentLifecycle getLifecycle()
  {
    return this.lifecycle;
  }
  
  @Nullable
  public RequestManager getRequestManager()
  {
    return this.requestManager;
  }
  
  public RequestManagerTreeNode getRequestManagerTreeNode()
  {
    return this.requestManagerTreeNode;
  }
  
  public void onAttach(Activity paramActivity)
  {
    super.onAttach(paramActivity);
    try
    {
      registerFragmentWithRoot(getActivity());
      return;
    }
    catch (IllegalStateException paramActivity)
    {
      while (!Log.isLoggable("SupportRMFragment", 5)) {}
      Log.w("SupportRMFragment", "Unable to register fragment with root", paramActivity);
    }
  }
  
  public void onDestroy()
  {
    super.onDestroy();
    this.lifecycle.onDestroy();
    unregisterFragmentWithRoot();
  }
  
  public void onDetach()
  {
    super.onDetach();
    this.parentFragmentHint = null;
    unregisterFragmentWithRoot();
  }
  
  public void onLowMemory()
  {
    super.onLowMemory();
    if (this.requestManager != null) {
      this.requestManager.onLowMemory();
    }
  }
  
  public void onStart()
  {
    super.onStart();
    this.lifecycle.onStart();
  }
  
  public void onStop()
  {
    super.onStop();
    this.lifecycle.onStop();
  }
  
  void setParentFragmentHint(Fragment paramFragment)
  {
    this.parentFragmentHint = paramFragment;
    if ((paramFragment != null) && (paramFragment.getActivity() != null)) {
      registerFragmentWithRoot(paramFragment.getActivity());
    }
  }
  
  public void setRequestManager(RequestManager paramRequestManager)
  {
    this.requestManager = paramRequestManager;
  }
  
  public String toString()
  {
    String str1 = String.valueOf(super.toString());
    String str2 = String.valueOf(getParentFragmentUsingHint());
    return String.valueOf(str1).length() + 9 + String.valueOf(str2).length() + str1 + "{parent=" + str2 + "}";
  }
  
  private class SupportFragmentRequestManagerTreeNode
    implements RequestManagerTreeNode
  {
    private SupportFragmentRequestManagerTreeNode() {}
    
    public Set<RequestManager> getDescendants()
    {
      Object localObject = SupportRequestManagerFragment.this.getDescendantRequestManagerFragments();
      HashSet localHashSet = new HashSet(((Set)localObject).size());
      localObject = ((Set)localObject).iterator();
      while (((Iterator)localObject).hasNext())
      {
        SupportRequestManagerFragment localSupportRequestManagerFragment = (SupportRequestManagerFragment)((Iterator)localObject).next();
        if (localSupportRequestManagerFragment.getRequestManager() != null) {
          localHashSet.add(localSupportRequestManagerFragment.getRequestManager());
        }
      }
      return localHashSet;
    }
    
    public String toString()
    {
      String str1 = String.valueOf(super.toString());
      String str2 = String.valueOf(SupportRequestManagerFragment.this);
      return String.valueOf(str1).length() + 11 + String.valueOf(str2).length() + str1 + "{fragment=" + str2 + "}";
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/manager/SupportRequestManagerFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */