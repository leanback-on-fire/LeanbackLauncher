package com.bumptech.glide.manager;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.util.Util;
import java.util.HashMap;
import java.util.Map;

public class RequestManagerRetriever
  implements Handler.Callback
{
  static final String FRAGMENT_TAG = "com.bumptech.glide.manager";
  private static final int ID_REMOVE_FRAGMENT_MANAGER = 1;
  private static final int ID_REMOVE_SUPPORT_FRAGMENT_MANAGER = 2;
  private static final RequestManagerRetriever INSTANCE = new RequestManagerRetriever();
  private static final String TAG = "RMRetriever";
  private volatile RequestManager applicationManager;
  private final Handler handler = new Handler(Looper.getMainLooper(), this);
  final Map<android.app.FragmentManager, RequestManagerFragment> pendingRequestManagerFragments = new HashMap();
  final Map<android.support.v4.app.FragmentManager, SupportRequestManagerFragment> pendingSupportRequestManagerFragments = new HashMap();
  
  @TargetApi(17)
  private static void assertNotDestroyed(Activity paramActivity)
  {
    if ((Build.VERSION.SDK_INT >= 17) && (paramActivity.isDestroyed())) {
      throw new IllegalArgumentException("You cannot start a load for a destroyed activity");
    }
  }
  
  public static RequestManagerRetriever get()
  {
    return INSTANCE;
  }
  
  private RequestManager getApplicationManager(Context paramContext)
  {
    if (this.applicationManager == null) {}
    try
    {
      if (this.applicationManager == null) {
        this.applicationManager = new RequestManager(Glide.get(paramContext), new ApplicationLifecycle(), new EmptyRequestManagerTreeNode());
      }
      return this.applicationManager;
    }
    finally {}
  }
  
  @TargetApi(11)
  RequestManager fragmentGet(Context paramContext, android.app.FragmentManager paramFragmentManager, android.app.Fragment paramFragment)
  {
    RequestManagerFragment localRequestManagerFragment = getRequestManagerFragment(paramFragmentManager, paramFragment);
    paramFragment = localRequestManagerFragment.getRequestManager();
    paramFragmentManager = paramFragment;
    if (paramFragment == null)
    {
      paramFragmentManager = new RequestManager(Glide.get(paramContext), localRequestManagerFragment.getLifecycle(), localRequestManagerFragment.getRequestManagerTreeNode());
      localRequestManagerFragment.setRequestManager(paramFragmentManager);
    }
    return paramFragmentManager;
  }
  
  @TargetApi(11)
  public RequestManager get(Activity paramActivity)
  {
    if ((Util.isOnBackgroundThread()) || (Build.VERSION.SDK_INT < 11)) {
      return get(paramActivity.getApplicationContext());
    }
    assertNotDestroyed(paramActivity);
    return fragmentGet(paramActivity, paramActivity.getFragmentManager(), null);
  }
  
  @TargetApi(17)
  public RequestManager get(android.app.Fragment paramFragment)
  {
    if (paramFragment.getActivity() == null) {
      throw new IllegalArgumentException("You cannot start a load on a fragment before it is attached");
    }
    if ((Util.isOnBackgroundThread()) || (Build.VERSION.SDK_INT < 17)) {
      return get(paramFragment.getActivity().getApplicationContext());
    }
    android.app.FragmentManager localFragmentManager = paramFragment.getChildFragmentManager();
    return fragmentGet(paramFragment.getActivity(), localFragmentManager, paramFragment);
  }
  
  public RequestManager get(Context paramContext)
  {
    if (paramContext == null) {
      throw new IllegalArgumentException("You cannot start a load on a null Context");
    }
    if ((Util.isOnMainThread()) && (!(paramContext instanceof Application)))
    {
      if ((paramContext instanceof FragmentActivity)) {
        return get((FragmentActivity)paramContext);
      }
      if ((paramContext instanceof Activity)) {
        return get((Activity)paramContext);
      }
      if ((paramContext instanceof ContextWrapper)) {
        return get(((ContextWrapper)paramContext).getBaseContext());
      }
    }
    return getApplicationManager(paramContext);
  }
  
  public RequestManager get(android.support.v4.app.Fragment paramFragment)
  {
    if (paramFragment.getActivity() == null) {
      throw new IllegalArgumentException("You cannot start a load on a fragment before it is attached");
    }
    if (Util.isOnBackgroundThread()) {
      return get(paramFragment.getActivity().getApplicationContext());
    }
    android.support.v4.app.FragmentManager localFragmentManager = paramFragment.getChildFragmentManager();
    return supportFragmentGet(paramFragment.getActivity(), localFragmentManager, paramFragment);
  }
  
  public RequestManager get(FragmentActivity paramFragmentActivity)
  {
    if (Util.isOnBackgroundThread()) {
      return get(paramFragmentActivity.getApplicationContext());
    }
    assertNotDestroyed(paramFragmentActivity);
    return supportFragmentGet(paramFragmentActivity, paramFragmentActivity.getSupportFragmentManager(), null);
  }
  
  @TargetApi(17)
  RequestManagerFragment getRequestManagerFragment(android.app.FragmentManager paramFragmentManager, android.app.Fragment paramFragment)
  {
    RequestManagerFragment localRequestManagerFragment2 = (RequestManagerFragment)paramFragmentManager.findFragmentByTag("com.bumptech.glide.manager");
    RequestManagerFragment localRequestManagerFragment1 = localRequestManagerFragment2;
    if (localRequestManagerFragment2 == null)
    {
      localRequestManagerFragment2 = (RequestManagerFragment)this.pendingRequestManagerFragments.get(paramFragmentManager);
      localRequestManagerFragment1 = localRequestManagerFragment2;
      if (localRequestManagerFragment2 == null)
      {
        localRequestManagerFragment1 = new RequestManagerFragment();
        localRequestManagerFragment1.setParentFragmentHint(paramFragment);
        this.pendingRequestManagerFragments.put(paramFragmentManager, localRequestManagerFragment1);
        paramFragmentManager.beginTransaction().add(localRequestManagerFragment1, "com.bumptech.glide.manager").commitAllowingStateLoss();
        this.handler.obtainMessage(1, paramFragmentManager).sendToTarget();
      }
    }
    return localRequestManagerFragment1;
  }
  
  SupportRequestManagerFragment getSupportRequestManagerFragment(android.support.v4.app.FragmentManager paramFragmentManager, android.support.v4.app.Fragment paramFragment)
  {
    SupportRequestManagerFragment localSupportRequestManagerFragment2 = (SupportRequestManagerFragment)paramFragmentManager.findFragmentByTag("com.bumptech.glide.manager");
    SupportRequestManagerFragment localSupportRequestManagerFragment1 = localSupportRequestManagerFragment2;
    if (localSupportRequestManagerFragment2 == null)
    {
      localSupportRequestManagerFragment2 = (SupportRequestManagerFragment)this.pendingSupportRequestManagerFragments.get(paramFragmentManager);
      localSupportRequestManagerFragment1 = localSupportRequestManagerFragment2;
      if (localSupportRequestManagerFragment2 == null)
      {
        localSupportRequestManagerFragment1 = new SupportRequestManagerFragment();
        localSupportRequestManagerFragment1.setParentFragmentHint(paramFragment);
        this.pendingSupportRequestManagerFragments.put(paramFragmentManager, localSupportRequestManagerFragment1);
        paramFragmentManager.beginTransaction().add(localSupportRequestManagerFragment1, "com.bumptech.glide.manager").commitAllowingStateLoss();
        this.handler.obtainMessage(2, paramFragmentManager).sendToTarget();
      }
    }
    return localSupportRequestManagerFragment1;
  }
  
  public boolean handleMessage(Message paramMessage)
  {
    boolean bool = true;
    Object localObject1 = null;
    Object localObject2 = null;
    switch (paramMessage.what)
    {
    default: 
      bool = false;
      paramMessage = (Message)localObject2;
    }
    for (;;)
    {
      if ((bool) && (localObject1 == null) && (Log.isLoggable("RMRetriever", 5)))
      {
        paramMessage = String.valueOf(paramMessage);
        Log.w("RMRetriever", String.valueOf(paramMessage).length() + 61 + "Failed to remove expected request manager fragment, manager: " + paramMessage);
      }
      return bool;
      localObject1 = (android.app.FragmentManager)paramMessage.obj;
      paramMessage = (Message)localObject1;
      localObject1 = this.pendingRequestManagerFragments.remove(localObject1);
      continue;
      localObject1 = (android.support.v4.app.FragmentManager)paramMessage.obj;
      paramMessage = (Message)localObject1;
      localObject1 = this.pendingSupportRequestManagerFragments.remove(localObject1);
    }
  }
  
  RequestManager supportFragmentGet(Context paramContext, android.support.v4.app.FragmentManager paramFragmentManager, android.support.v4.app.Fragment paramFragment)
  {
    SupportRequestManagerFragment localSupportRequestManagerFragment = getSupportRequestManagerFragment(paramFragmentManager, paramFragment);
    paramFragment = localSupportRequestManagerFragment.getRequestManager();
    paramFragmentManager = paramFragment;
    if (paramFragment == null)
    {
      paramFragmentManager = new RequestManager(Glide.get(paramContext), localSupportRequestManagerFragment.getLifecycle(), localSupportRequestManagerFragment.getRequestManagerTreeNode());
      localSupportRequestManagerFragment.setRequestManager(paramFragmentManager);
    }
    return paramFragmentManager;
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/manager/RequestManagerRetriever.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */