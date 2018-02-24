package com.bumptech.glide.manager;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.app.Fragment;
import android.app.FragmentManager;
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

public class RequestManagerRetriever implements Callback {
    private static final RequestManagerRetriever INSTANCE = new RequestManagerRetriever();
    private volatile RequestManager applicationManager;
    private final Handler handler = new Handler(Looper.getMainLooper(), this);
    final Map<FragmentManager, RequestManagerFragment> pendingRequestManagerFragments = new HashMap();
    final Map<android.support.v4.app.FragmentManager, SupportRequestManagerFragment> pendingSupportRequestManagerFragments = new HashMap();

    public static RequestManagerRetriever get() {
        return INSTANCE;
    }

    RequestManagerRetriever() {
    }

    private RequestManager getApplicationManager(Context context) {
        if (this.applicationManager == null) {
            synchronized (this) {
                if (this.applicationManager == null) {
                    this.applicationManager = new RequestManager(Glide.get(context), new ApplicationLifecycle(), new EmptyRequestManagerTreeNode());
                }
            }
        }
        return this.applicationManager;
    }

    public RequestManager get(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("You cannot start a load on a null Context");
        }
        if (Util.isOnMainThread() && !(context instanceof Application)) {
            if (context instanceof FragmentActivity) {
                return get((FragmentActivity) context);
            }
            if (context instanceof Activity) {
                return get((Activity) context);
            }
            if (context instanceof ContextWrapper) {
                return get(((ContextWrapper) context).getBaseContext());
            }
        }
        return getApplicationManager(context);
    }

    public RequestManager get(FragmentActivity activity) {
        if (Util.isOnBackgroundThread()) {
            return get(activity.getApplicationContext());
        }
        assertNotDestroyed(activity);
        return supportFragmentGet(activity, activity.getSupportFragmentManager(), null);
    }

    @TargetApi(11)
    public RequestManager get(Activity activity) {
        if (Util.isOnBackgroundThread() || VERSION.SDK_INT < 11) {
            return get(activity.getApplicationContext());
        }
        assertNotDestroyed(activity);
        return fragmentGet(activity, activity.getFragmentManager(), null);
    }

    @TargetApi(17)
    private static void assertNotDestroyed(Activity activity) {
        if (VERSION.SDK_INT >= 17 && activity.isDestroyed()) {
            throw new IllegalArgumentException("You cannot start a load for a destroyed activity");
        }
    }

    @TargetApi(17)
    RequestManagerFragment getRequestManagerFragment(FragmentManager fm, Fragment parentHint) {
        RequestManagerFragment current = (RequestManagerFragment) fm.findFragmentByTag("com.bumptech.glide.manager");
        if (current != null) {
            return current;
        }
        current = (RequestManagerFragment) this.pendingRequestManagerFragments.get(fm);
        if (current != null) {
            return current;
        }
        current = new RequestManagerFragment();
        current.setParentFragmentHint(parentHint);
        this.pendingRequestManagerFragments.put(fm, current);
        fm.beginTransaction().add(current, "com.bumptech.glide.manager").commitAllowingStateLoss();
        this.handler.obtainMessage(1, fm).sendToTarget();
        return current;
    }

    @TargetApi(11)
    RequestManager fragmentGet(Context context, FragmentManager fm, Fragment parentHint) {
        RequestManagerFragment current = getRequestManagerFragment(fm, parentHint);
        RequestManager requestManager = current.getRequestManager();
        if (requestManager != null) {
            return requestManager;
        }
        requestManager = new RequestManager(Glide.get(context), current.getLifecycle(), current.getRequestManagerTreeNode());
        current.setRequestManager(requestManager);
        return requestManager;
    }

    SupportRequestManagerFragment getSupportRequestManagerFragment(android.support.v4.app.FragmentManager fm, android.support.v4.app.Fragment parentHint) {
        SupportRequestManagerFragment current = (SupportRequestManagerFragment) fm.findFragmentByTag("com.bumptech.glide.manager");
        if (current != null) {
            return current;
        }
        current = (SupportRequestManagerFragment) this.pendingSupportRequestManagerFragments.get(fm);
        if (current != null) {
            return current;
        }
        current = new SupportRequestManagerFragment();
        current.setParentFragmentHint(parentHint);
        this.pendingSupportRequestManagerFragments.put(fm, current);
        fm.beginTransaction().add(current, "com.bumptech.glide.manager").commitAllowingStateLoss();
        this.handler.obtainMessage(2, fm).sendToTarget();
        return current;
    }

    RequestManager supportFragmentGet(Context context, android.support.v4.app.FragmentManager fm, android.support.v4.app.Fragment parentHint) {
        SupportRequestManagerFragment current = getSupportRequestManagerFragment(fm, parentHint);
        RequestManager requestManager = current.getRequestManager();
        if (requestManager != null) {
            return requestManager;
        }
        requestManager = new RequestManager(Glide.get(context), current.getLifecycle(), current.getRequestManagerTreeNode());
        current.setRequestManager(requestManager);
        return requestManager;
    }

    public boolean handleMessage(Message message) {
        boolean handled = true;
        Object removed = null;
        Object key = null;
        switch (message.what) {
            case 1:
                FragmentManager fm = message.obj;
                FragmentManager key2 = fm;
                removed = this.pendingRequestManagerFragments.remove(fm);
                break;
            case 2:
                android.support.v4.app.FragmentManager supportFm = message.obj;
                android.support.v4.app.FragmentManager key3 = supportFm;
                removed = this.pendingSupportRequestManagerFragments.remove(supportFm);
                break;
            default:
                handled = false;
                break;
        }
        if (handled && r3 == null && Log.isLoggable("RMRetriever", 5)) {
            String valueOf = String.valueOf(key);
            Log.w("RMRetriever", new StringBuilder(String.valueOf(valueOf).length() + 61).append("Failed to remove expected request manager fragment, manager: ").append(valueOf).toString());
        }
        return handled;
    }
}
