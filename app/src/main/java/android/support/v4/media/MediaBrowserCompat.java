package android.support.v4.media;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.BadParcelableException;
import android.os.Binder;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;
import android.support.v4.app.BundleCompat;
import android.support.v4.media.session.IMediaSession;
import android.support.v4.media.session.IMediaSession.Stub;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.MediaSessionCompat.Token;
import android.support.v4.os.ResultReceiver;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.Log;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

public final class MediaBrowserCompat
{
  public static final String CUSTOM_ACTION_DOWNLOAD = "android.support.v4.media.action.DOWNLOAD";
  public static final String CUSTOM_ACTION_REMOVE_DOWNLOADED_FILE = "android.support.v4.media.action.REMOVE_DOWNLOADED_FILE";
  static final boolean DEBUG = Log.isLoggable("MediaBrowserCompat", 3);
  public static final String EXTRA_DOWNLOAD_PROGRESS = "android.media.browse.extra.DOWNLOAD_PROGRESS";
  public static final String EXTRA_MEDIA_ID = "android.media.browse.extra.MEDIA_ID";
  public static final String EXTRA_PAGE = "android.media.browse.extra.PAGE";
  public static final String EXTRA_PAGE_SIZE = "android.media.browse.extra.PAGE_SIZE";
  static final String TAG = "MediaBrowserCompat";
  private final MediaBrowserImpl mImpl;
  
  public MediaBrowserCompat(Context paramContext, ComponentName paramComponentName, ConnectionCallback paramConnectionCallback, Bundle paramBundle)
  {
    if (Build.VERSION.SDK_INT >= 26)
    {
      this.mImpl = new MediaBrowserImplApi24(paramContext, paramComponentName, paramConnectionCallback, paramBundle);
      return;
    }
    if (Build.VERSION.SDK_INT >= 23)
    {
      this.mImpl = new MediaBrowserImplApi23(paramContext, paramComponentName, paramConnectionCallback, paramBundle);
      return;
    }
    if (Build.VERSION.SDK_INT >= 21)
    {
      this.mImpl = new MediaBrowserImplApi21(paramContext, paramComponentName, paramConnectionCallback, paramBundle);
      return;
    }
    this.mImpl = new MediaBrowserImplBase(paramContext, paramComponentName, paramConnectionCallback, paramBundle);
  }
  
  public void connect()
  {
    this.mImpl.connect();
  }
  
  public void disconnect()
  {
    this.mImpl.disconnect();
  }
  
  @Nullable
  public Bundle getExtras()
  {
    return this.mImpl.getExtras();
  }
  
  public void getItem(@NonNull String paramString, @NonNull ItemCallback paramItemCallback)
  {
    this.mImpl.getItem(paramString, paramItemCallback);
  }
  
  @NonNull
  public String getRoot()
  {
    return this.mImpl.getRoot();
  }
  
  @NonNull
  public ComponentName getServiceComponent()
  {
    return this.mImpl.getServiceComponent();
  }
  
  @NonNull
  public MediaSessionCompat.Token getSessionToken()
  {
    return this.mImpl.getSessionToken();
  }
  
  public boolean isConnected()
  {
    return this.mImpl.isConnected();
  }
  
  public void search(@NonNull String paramString, Bundle paramBundle, @NonNull SearchCallback paramSearchCallback)
  {
    if (TextUtils.isEmpty(paramString)) {
      throw new IllegalArgumentException("query cannot be empty");
    }
    if (paramSearchCallback == null) {
      throw new IllegalArgumentException("callback cannot be null");
    }
    this.mImpl.search(paramString, paramBundle, paramSearchCallback);
  }
  
  public void sendCustomAction(@NonNull String paramString, Bundle paramBundle, @Nullable CustomActionCallback paramCustomActionCallback)
  {
    if (TextUtils.isEmpty(paramString)) {
      throw new IllegalArgumentException("action cannot be empty");
    }
    this.mImpl.sendCustomAction(paramString, paramBundle, paramCustomActionCallback);
  }
  
  public void subscribe(@NonNull String paramString, @NonNull Bundle paramBundle, @NonNull SubscriptionCallback paramSubscriptionCallback)
  {
    if (TextUtils.isEmpty(paramString)) {
      throw new IllegalArgumentException("parentId is empty");
    }
    if (paramSubscriptionCallback == null) {
      throw new IllegalArgumentException("callback is null");
    }
    if (paramBundle == null) {
      throw new IllegalArgumentException("options are null");
    }
    this.mImpl.subscribe(paramString, paramBundle, paramSubscriptionCallback);
  }
  
  public void subscribe(@NonNull String paramString, @NonNull SubscriptionCallback paramSubscriptionCallback)
  {
    if (TextUtils.isEmpty(paramString)) {
      throw new IllegalArgumentException("parentId is empty");
    }
    if (paramSubscriptionCallback == null) {
      throw new IllegalArgumentException("callback is null");
    }
    this.mImpl.subscribe(paramString, null, paramSubscriptionCallback);
  }
  
  public void unsubscribe(@NonNull String paramString)
  {
    if (TextUtils.isEmpty(paramString)) {
      throw new IllegalArgumentException("parentId is empty");
    }
    this.mImpl.unsubscribe(paramString, null);
  }
  
  public void unsubscribe(@NonNull String paramString, @NonNull SubscriptionCallback paramSubscriptionCallback)
  {
    if (TextUtils.isEmpty(paramString)) {
      throw new IllegalArgumentException("parentId is empty");
    }
    if (paramSubscriptionCallback == null) {
      throw new IllegalArgumentException("callback is null");
    }
    this.mImpl.unsubscribe(paramString, paramSubscriptionCallback);
  }
  
  private static class CallbackHandler
    extends Handler
  {
    private final WeakReference<MediaBrowserCompat.MediaBrowserServiceCallbackImpl> mCallbackImplRef;
    private WeakReference<Messenger> mCallbacksMessengerRef;
    
    CallbackHandler(MediaBrowserCompat.MediaBrowserServiceCallbackImpl paramMediaBrowserServiceCallbackImpl)
    {
      this.mCallbackImplRef = new WeakReference(paramMediaBrowserServiceCallbackImpl);
    }
    
    public void handleMessage(Message paramMessage)
    {
      if ((this.mCallbacksMessengerRef == null) || (this.mCallbacksMessengerRef.get() == null) || (this.mCallbackImplRef.get() == null)) {
        return;
      }
      Bundle localBundle = paramMessage.getData();
      localBundle.setClassLoader(MediaSessionCompat.class.getClassLoader());
      MediaBrowserCompat.MediaBrowserServiceCallbackImpl localMediaBrowserServiceCallbackImpl = (MediaBrowserCompat.MediaBrowserServiceCallbackImpl)this.mCallbackImplRef.get();
      Messenger localMessenger = (Messenger)this.mCallbacksMessengerRef.get();
      for (;;)
      {
        try
        {
          switch (paramMessage.what)
          {
          case 1: 
            Log.w("MediaBrowserCompat", "Unhandled message: " + paramMessage + "\n  Client version: " + 1 + "\n  Service version: " + paramMessage.arg1);
            return;
          }
        }
        catch (BadParcelableException localBadParcelableException)
        {
          Log.e("MediaBrowserCompat", "Could not unparcel the data.");
        }
        if (paramMessage.what != 1) {
          break;
        }
        localMediaBrowserServiceCallbackImpl.onConnectionFailed(localMessenger);
        return;
        localMediaBrowserServiceCallbackImpl.onServiceConnected(localMessenger, localBadParcelableException.getString("data_media_item_id"), (MediaSessionCompat.Token)localBadParcelableException.getParcelable("data_media_session_token"), localBadParcelableException.getBundle("data_root_hints"));
        return;
        localMediaBrowserServiceCallbackImpl.onConnectionFailed(localMessenger);
        return;
        localMediaBrowserServiceCallbackImpl.onLoadChildren(localMessenger, localBadParcelableException.getString("data_media_item_id"), localBadParcelableException.getParcelableArrayList("data_media_item_list"), localBadParcelableException.getBundle("data_options"));
        return;
      }
    }
    
    void setCallbacksMessenger(Messenger paramMessenger)
    {
      this.mCallbacksMessengerRef = new WeakReference(paramMessenger);
    }
  }
  
  public static class ConnectionCallback
  {
    ConnectionCallbackInternal mConnectionCallbackInternal;
    final Object mConnectionCallbackObj;
    
    public ConnectionCallback()
    {
      if (Build.VERSION.SDK_INT >= 21)
      {
        this.mConnectionCallbackObj = MediaBrowserCompatApi21.createConnectionCallback(new StubApi21());
        return;
      }
      this.mConnectionCallbackObj = null;
    }
    
    public void onConnected() {}
    
    public void onConnectionFailed() {}
    
    public void onConnectionSuspended() {}
    
    void setInternalConnectionCallback(ConnectionCallbackInternal paramConnectionCallbackInternal)
    {
      this.mConnectionCallbackInternal = paramConnectionCallbackInternal;
    }
    
    static abstract interface ConnectionCallbackInternal
    {
      public abstract void onConnected();
      
      public abstract void onConnectionFailed();
      
      public abstract void onConnectionSuspended();
    }
    
    private class StubApi21
      implements MediaBrowserCompatApi21.ConnectionCallback
    {
      StubApi21() {}
      
      public void onConnected()
      {
        if (MediaBrowserCompat.ConnectionCallback.this.mConnectionCallbackInternal != null) {
          MediaBrowserCompat.ConnectionCallback.this.mConnectionCallbackInternal.onConnected();
        }
        MediaBrowserCompat.ConnectionCallback.this.onConnected();
      }
      
      public void onConnectionFailed()
      {
        if (MediaBrowserCompat.ConnectionCallback.this.mConnectionCallbackInternal != null) {
          MediaBrowserCompat.ConnectionCallback.this.mConnectionCallbackInternal.onConnectionFailed();
        }
        MediaBrowserCompat.ConnectionCallback.this.onConnectionFailed();
      }
      
      public void onConnectionSuspended()
      {
        if (MediaBrowserCompat.ConnectionCallback.this.mConnectionCallbackInternal != null) {
          MediaBrowserCompat.ConnectionCallback.this.mConnectionCallbackInternal.onConnectionSuspended();
        }
        MediaBrowserCompat.ConnectionCallback.this.onConnectionSuspended();
      }
    }
  }
  
  public static abstract class CustomActionCallback
  {
    public void onError(String paramString, Bundle paramBundle1, Bundle paramBundle2) {}
    
    public void onProgressUpdate(String paramString, Bundle paramBundle1, Bundle paramBundle2) {}
    
    public void onResult(String paramString, Bundle paramBundle1, Bundle paramBundle2) {}
  }
  
  private static class CustomActionResultReceiver
    extends ResultReceiver
  {
    private final String mAction;
    private final MediaBrowserCompat.CustomActionCallback mCallback;
    private final Bundle mExtras;
    
    CustomActionResultReceiver(String paramString, Bundle paramBundle, MediaBrowserCompat.CustomActionCallback paramCustomActionCallback, Handler paramHandler)
    {
      super();
      this.mAction = paramString;
      this.mExtras = paramBundle;
      this.mCallback = paramCustomActionCallback;
    }
    
    protected void onReceiveResult(int paramInt, Bundle paramBundle)
    {
      if (this.mCallback == null) {
        return;
      }
      switch (paramInt)
      {
      default: 
        Log.w("MediaBrowserCompat", "Unknown result code: " + paramInt + " (extras=" + this.mExtras + ", resultData=" + paramBundle + ")");
        return;
      case 1: 
        this.mCallback.onProgressUpdate(this.mAction, this.mExtras, paramBundle);
        return;
      case 0: 
        this.mCallback.onResult(this.mAction, this.mExtras, paramBundle);
        return;
      }
      this.mCallback.onError(this.mAction, this.mExtras, paramBundle);
    }
  }
  
  public static abstract class ItemCallback
  {
    final Object mItemCallbackObj;
    
    public ItemCallback()
    {
      if (Build.VERSION.SDK_INT >= 23)
      {
        this.mItemCallbackObj = MediaBrowserCompatApi23.createItemCallback(new StubApi23());
        return;
      }
      this.mItemCallbackObj = null;
    }
    
    public void onError(@NonNull String paramString) {}
    
    public void onItemLoaded(MediaBrowserCompat.MediaItem paramMediaItem) {}
    
    private class StubApi23
      implements MediaBrowserCompatApi23.ItemCallback
    {
      StubApi23() {}
      
      public void onError(@NonNull String paramString)
      {
        MediaBrowserCompat.ItemCallback.this.onError(paramString);
      }
      
      public void onItemLoaded(Parcel paramParcel)
      {
        if (paramParcel == null)
        {
          MediaBrowserCompat.ItemCallback.this.onItemLoaded(null);
          return;
        }
        paramParcel.setDataPosition(0);
        MediaBrowserCompat.MediaItem localMediaItem = (MediaBrowserCompat.MediaItem)MediaBrowserCompat.MediaItem.CREATOR.createFromParcel(paramParcel);
        paramParcel.recycle();
        MediaBrowserCompat.ItemCallback.this.onItemLoaded(localMediaItem);
      }
    }
  }
  
  private static class ItemReceiver
    extends ResultReceiver
  {
    private final MediaBrowserCompat.ItemCallback mCallback;
    private final String mMediaId;
    
    ItemReceiver(String paramString, MediaBrowserCompat.ItemCallback paramItemCallback, Handler paramHandler)
    {
      super();
      this.mMediaId = paramString;
      this.mCallback = paramItemCallback;
    }
    
    protected void onReceiveResult(int paramInt, Bundle paramBundle)
    {
      if (paramBundle != null) {
        paramBundle.setClassLoader(MediaBrowserCompat.class.getClassLoader());
      }
      if ((paramInt != 0) || (paramBundle == null) || (!paramBundle.containsKey("media_item")))
      {
        this.mCallback.onError(this.mMediaId);
        return;
      }
      paramBundle = paramBundle.getParcelable("media_item");
      if ((paramBundle == null) || ((paramBundle instanceof MediaBrowserCompat.MediaItem)))
      {
        this.mCallback.onItemLoaded((MediaBrowserCompat.MediaItem)paramBundle);
        return;
      }
      this.mCallback.onError(this.mMediaId);
    }
  }
  
  static abstract interface MediaBrowserImpl
  {
    public abstract void connect();
    
    public abstract void disconnect();
    
    @Nullable
    public abstract Bundle getExtras();
    
    public abstract void getItem(@NonNull String paramString, @NonNull MediaBrowserCompat.ItemCallback paramItemCallback);
    
    @NonNull
    public abstract String getRoot();
    
    public abstract ComponentName getServiceComponent();
    
    @NonNull
    public abstract MediaSessionCompat.Token getSessionToken();
    
    public abstract boolean isConnected();
    
    public abstract void search(@NonNull String paramString, Bundle paramBundle, @NonNull MediaBrowserCompat.SearchCallback paramSearchCallback);
    
    public abstract void sendCustomAction(String paramString, Bundle paramBundle, MediaBrowserCompat.CustomActionCallback paramCustomActionCallback);
    
    public abstract void subscribe(@NonNull String paramString, Bundle paramBundle, @NonNull MediaBrowserCompat.SubscriptionCallback paramSubscriptionCallback);
    
    public abstract void unsubscribe(@NonNull String paramString, MediaBrowserCompat.SubscriptionCallback paramSubscriptionCallback);
  }
  
  @RequiresApi(21)
  static class MediaBrowserImplApi21
    implements MediaBrowserCompat.MediaBrowserImpl, MediaBrowserCompat.MediaBrowserServiceCallbackImpl, MediaBrowserCompat.ConnectionCallback.ConnectionCallbackInternal
  {
    protected final Object mBrowserObj;
    protected Messenger mCallbacksMessenger;
    protected final MediaBrowserCompat.CallbackHandler mHandler = new MediaBrowserCompat.CallbackHandler(this);
    private MediaSessionCompat.Token mMediaSessionToken;
    protected final Bundle mRootHints;
    protected MediaBrowserCompat.ServiceBinderWrapper mServiceBinderWrapper;
    private final ArrayMap<String, MediaBrowserCompat.Subscription> mSubscriptions = new ArrayMap();
    
    public MediaBrowserImplApi21(Context paramContext, ComponentName paramComponentName, MediaBrowserCompat.ConnectionCallback paramConnectionCallback, Bundle paramBundle)
    {
      Bundle localBundle = paramBundle;
      if (paramBundle == null) {
        localBundle = new Bundle();
      }
      localBundle.putInt("extra_client_version", 1);
      this.mRootHints = new Bundle(localBundle);
      paramConnectionCallback.setInternalConnectionCallback(this);
      this.mBrowserObj = MediaBrowserCompatApi21.createBrowser(paramContext, paramComponentName, paramConnectionCallback.mConnectionCallbackObj, this.mRootHints);
    }
    
    public void connect()
    {
      MediaBrowserCompatApi21.connect(this.mBrowserObj);
    }
    
    public void disconnect()
    {
      if ((this.mServiceBinderWrapper != null) && (this.mCallbacksMessenger != null)) {}
      try
      {
        this.mServiceBinderWrapper.unregisterCallbackMessenger(this.mCallbacksMessenger);
        MediaBrowserCompatApi21.disconnect(this.mBrowserObj);
        return;
      }
      catch (RemoteException localRemoteException)
      {
        for (;;)
        {
          Log.i("MediaBrowserCompat", "Remote error unregistering client messenger.");
        }
      }
    }
    
    @Nullable
    public Bundle getExtras()
    {
      return MediaBrowserCompatApi21.getExtras(this.mBrowserObj);
    }
    
    public void getItem(@NonNull final String paramString, @NonNull final MediaBrowserCompat.ItemCallback paramItemCallback)
    {
      if (TextUtils.isEmpty(paramString)) {
        throw new IllegalArgumentException("mediaId is empty");
      }
      if (paramItemCallback == null) {
        throw new IllegalArgumentException("cb is null");
      }
      if (!MediaBrowserCompatApi21.isConnected(this.mBrowserObj))
      {
        Log.i("MediaBrowserCompat", "Not connected, unable to retrieve the MediaItem.");
        this.mHandler.post(new Runnable()
        {
          public void run()
          {
            paramItemCallback.onError(paramString);
          }
        });
        return;
      }
      if (this.mServiceBinderWrapper == null)
      {
        this.mHandler.post(new Runnable()
        {
          public void run()
          {
            paramItemCallback.onError(paramString);
          }
        });
        return;
      }
      MediaBrowserCompat.ItemReceiver localItemReceiver = new MediaBrowserCompat.ItemReceiver(paramString, paramItemCallback, this.mHandler);
      try
      {
        this.mServiceBinderWrapper.getMediaItem(paramString, localItemReceiver, this.mCallbacksMessenger);
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Log.i("MediaBrowserCompat", "Remote error getting media item: " + paramString);
        this.mHandler.post(new Runnable()
        {
          public void run()
          {
            paramItemCallback.onError(paramString);
          }
        });
      }
    }
    
    @NonNull
    public String getRoot()
    {
      return MediaBrowserCompatApi21.getRoot(this.mBrowserObj);
    }
    
    public ComponentName getServiceComponent()
    {
      return MediaBrowserCompatApi21.getServiceComponent(this.mBrowserObj);
    }
    
    @NonNull
    public MediaSessionCompat.Token getSessionToken()
    {
      if (this.mMediaSessionToken == null) {
        this.mMediaSessionToken = MediaSessionCompat.Token.fromToken(MediaBrowserCompatApi21.getSessionToken(this.mBrowserObj));
      }
      return this.mMediaSessionToken;
    }
    
    public boolean isConnected()
    {
      return MediaBrowserCompatApi21.isConnected(this.mBrowserObj);
    }
    
    public void onConnected()
    {
      Object localObject = MediaBrowserCompatApi21.getExtras(this.mBrowserObj);
      if (localObject == null) {}
      for (;;)
      {
        return;
        IBinder localIBinder = BundleCompat.getBinder((Bundle)localObject, "extra_messenger");
        if (localIBinder != null)
        {
          this.mServiceBinderWrapper = new MediaBrowserCompat.ServiceBinderWrapper(localIBinder, this.mRootHints);
          this.mCallbacksMessenger = new Messenger(this.mHandler);
          this.mHandler.setCallbacksMessenger(this.mCallbacksMessenger);
        }
        try
        {
          this.mServiceBinderWrapper.registerCallbackMessenger(this.mCallbacksMessenger);
          localObject = IMediaSession.Stub.asInterface(BundleCompat.getBinder((Bundle)localObject, "extra_session_binder"));
          if (localObject == null) {
            continue;
          }
          this.mMediaSessionToken = MediaSessionCompat.Token.fromToken(MediaBrowserCompatApi21.getSessionToken(this.mBrowserObj), (IMediaSession)localObject);
          return;
        }
        catch (RemoteException localRemoteException)
        {
          for (;;)
          {
            Log.i("MediaBrowserCompat", "Remote error registering client messenger.");
          }
        }
      }
    }
    
    public void onConnectionFailed() {}
    
    public void onConnectionFailed(Messenger paramMessenger) {}
    
    public void onConnectionSuspended()
    {
      this.mServiceBinderWrapper = null;
      this.mCallbacksMessenger = null;
      this.mMediaSessionToken = null;
      this.mHandler.setCallbacksMessenger(null);
    }
    
    public void onLoadChildren(Messenger paramMessenger, String paramString, List paramList, Bundle paramBundle)
    {
      if (this.mCallbacksMessenger != paramMessenger) {}
      do
      {
        do
        {
          return;
          paramMessenger = (MediaBrowserCompat.Subscription)this.mSubscriptions.get(paramString);
          if (paramMessenger != null) {
            break;
          }
        } while (!MediaBrowserCompat.DEBUG);
        Log.d("MediaBrowserCompat", "onLoadChildren for id that isn't subscribed id=" + paramString);
        return;
        paramMessenger = paramMessenger.getCallback(paramBundle);
      } while (paramMessenger == null);
      if (paramBundle == null)
      {
        if (paramList == null)
        {
          paramMessenger.onError(paramString);
          return;
        }
        paramMessenger.onChildrenLoaded(paramString, paramList);
        return;
      }
      if (paramList == null)
      {
        paramMessenger.onError(paramString, paramBundle);
        return;
      }
      paramMessenger.onChildrenLoaded(paramString, paramList, paramBundle);
    }
    
    public void onServiceConnected(Messenger paramMessenger, String paramString, MediaSessionCompat.Token paramToken, Bundle paramBundle) {}
    
    public void search(@NonNull final String paramString, final Bundle paramBundle, @NonNull final MediaBrowserCompat.SearchCallback paramSearchCallback)
    {
      if (!isConnected()) {
        throw new IllegalStateException("search() called while not connected");
      }
      if (this.mServiceBinderWrapper == null)
      {
        Log.i("MediaBrowserCompat", "The connected service doesn't support search.");
        this.mHandler.post(new Runnable()
        {
          public void run()
          {
            paramSearchCallback.onError(paramString, paramBundle);
          }
        });
        return;
      }
      MediaBrowserCompat.SearchResultReceiver localSearchResultReceiver = new MediaBrowserCompat.SearchResultReceiver(paramString, paramBundle, paramSearchCallback, this.mHandler);
      try
      {
        this.mServiceBinderWrapper.search(paramString, paramBundle, localSearchResultReceiver, this.mCallbacksMessenger);
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Log.i("MediaBrowserCompat", "Remote error searching items with query: " + paramString, localRemoteException);
        this.mHandler.post(new Runnable()
        {
          public void run()
          {
            paramSearchCallback.onError(paramString, paramBundle);
          }
        });
      }
    }
    
    public void sendCustomAction(final String paramString, final Bundle paramBundle, final MediaBrowserCompat.CustomActionCallback paramCustomActionCallback)
    {
      if (!isConnected()) {
        throw new IllegalStateException("Cannot send a custom action (" + paramString + ") with " + "extras " + paramBundle + " because the browser is not connected to the " + "service.");
      }
      if (this.mServiceBinderWrapper == null)
      {
        Log.i("MediaBrowserCompat", "The connected service doesn't support sendCustomAction.");
        this.mHandler.post(new Runnable()
        {
          public void run()
          {
            paramCustomActionCallback.onError(paramString, paramBundle, null);
          }
        });
      }
      MediaBrowserCompat.CustomActionResultReceiver localCustomActionResultReceiver = new MediaBrowserCompat.CustomActionResultReceiver(paramString, paramBundle, paramCustomActionCallback, this.mHandler);
      try
      {
        this.mServiceBinderWrapper.sendCustomAction(paramString, paramBundle, localCustomActionResultReceiver, this.mCallbacksMessenger);
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Log.i("MediaBrowserCompat", "Remote error sending a custom action: action=" + paramString + ", extras=" + paramBundle, localRemoteException);
        this.mHandler.post(new Runnable()
        {
          public void run()
          {
            paramCustomActionCallback.onError(paramString, paramBundle, null);
          }
        });
      }
    }
    
    public void subscribe(@NonNull String paramString, Bundle paramBundle, @NonNull MediaBrowserCompat.SubscriptionCallback paramSubscriptionCallback)
    {
      MediaBrowserCompat.Subscription localSubscription2 = (MediaBrowserCompat.Subscription)this.mSubscriptions.get(paramString);
      MediaBrowserCompat.Subscription localSubscription1 = localSubscription2;
      if (localSubscription2 == null)
      {
        localSubscription1 = new MediaBrowserCompat.Subscription();
        this.mSubscriptions.put(paramString, localSubscription1);
      }
      MediaBrowserCompat.SubscriptionCallback.access$100(paramSubscriptionCallback, localSubscription1);
      if (paramBundle == null) {}
      for (paramBundle = null;; paramBundle = new Bundle(paramBundle))
      {
        localSubscription1.putCallback(paramBundle, paramSubscriptionCallback);
        if (this.mServiceBinderWrapper != null) {
          break;
        }
        MediaBrowserCompatApi21.subscribe(this.mBrowserObj, paramString, MediaBrowserCompat.SubscriptionCallback.access$200(paramSubscriptionCallback));
        return;
      }
      try
      {
        this.mServiceBinderWrapper.addSubscription(paramString, MediaBrowserCompat.SubscriptionCallback.access$000(paramSubscriptionCallback), paramBundle, this.mCallbacksMessenger);
        return;
      }
      catch (RemoteException paramBundle)
      {
        Log.i("MediaBrowserCompat", "Remote error subscribing media item: " + paramString);
      }
    }
    
    public void unsubscribe(@NonNull String paramString, MediaBrowserCompat.SubscriptionCallback paramSubscriptionCallback)
    {
      MediaBrowserCompat.Subscription localSubscription = (MediaBrowserCompat.Subscription)this.mSubscriptions.get(paramString);
      if (localSubscription == null) {
        return;
      }
      if (this.mServiceBinderWrapper == null) {
        if (paramSubscriptionCallback == null) {
          MediaBrowserCompatApi21.unsubscribe(this.mBrowserObj, paramString);
        }
      }
      for (;;)
      {
        if ((localSubscription.isEmpty()) || (paramSubscriptionCallback == null))
        {
          this.mSubscriptions.remove(paramString);
          return;
          List localList1 = localSubscription.getCallbacks();
          localList3 = localSubscription.getOptionsList();
          i = localList1.size() - 1;
          while (i >= 0)
          {
            if (localList1.get(i) == paramSubscriptionCallback)
            {
              localList1.remove(i);
              localList3.remove(i);
            }
            i -= 1;
          }
          if (localList1.size() != 0) {
            continue;
          }
          MediaBrowserCompatApi21.unsubscribe(this.mBrowserObj, paramString);
          continue;
          if (paramSubscriptionCallback == null) {
            try
            {
              this.mServiceBinderWrapper.removeSubscription(paramString, null, this.mCallbacksMessenger);
            }
            catch (RemoteException localRemoteException)
            {
              Log.d("MediaBrowserCompat", "removeSubscription failed with RemoteException parentId=" + paramString);
            }
          }
        }
        else
        {
          break;
        }
        List localList2 = localSubscription.getCallbacks();
        List localList3 = localSubscription.getOptionsList();
        int i = localList2.size() - 1;
        while (i >= 0)
        {
          if (localList2.get(i) == paramSubscriptionCallback)
          {
            this.mServiceBinderWrapper.removeSubscription(paramString, MediaBrowserCompat.SubscriptionCallback.access$000(paramSubscriptionCallback), this.mCallbacksMessenger);
            localList2.remove(i);
            localList3.remove(i);
          }
          i -= 1;
        }
      }
    }
  }
  
  @RequiresApi(23)
  static class MediaBrowserImplApi23
    extends MediaBrowserCompat.MediaBrowserImplApi21
  {
    public MediaBrowserImplApi23(Context paramContext, ComponentName paramComponentName, MediaBrowserCompat.ConnectionCallback paramConnectionCallback, Bundle paramBundle)
    {
      super(paramComponentName, paramConnectionCallback, paramBundle);
    }
    
    public void getItem(@NonNull String paramString, @NonNull MediaBrowserCompat.ItemCallback paramItemCallback)
    {
      if (this.mServiceBinderWrapper == null)
      {
        MediaBrowserCompatApi23.getItem(this.mBrowserObj, paramString, paramItemCallback.mItemCallbackObj);
        return;
      }
      super.getItem(paramString, paramItemCallback);
    }
  }
  
  @RequiresApi(26)
  static class MediaBrowserImplApi24
    extends MediaBrowserCompat.MediaBrowserImplApi23
  {
    public MediaBrowserImplApi24(Context paramContext, ComponentName paramComponentName, MediaBrowserCompat.ConnectionCallback paramConnectionCallback, Bundle paramBundle)
    {
      super(paramComponentName, paramConnectionCallback, paramBundle);
    }
    
    public void subscribe(@NonNull String paramString, @NonNull Bundle paramBundle, @NonNull MediaBrowserCompat.SubscriptionCallback paramSubscriptionCallback)
    {
      if (paramBundle == null)
      {
        MediaBrowserCompatApi21.subscribe(this.mBrowserObj, paramString, MediaBrowserCompat.SubscriptionCallback.access$200(paramSubscriptionCallback));
        return;
      }
      MediaBrowserCompatApi24.subscribe(this.mBrowserObj, paramString, paramBundle, MediaBrowserCompat.SubscriptionCallback.access$200(paramSubscriptionCallback));
    }
    
    public void unsubscribe(@NonNull String paramString, MediaBrowserCompat.SubscriptionCallback paramSubscriptionCallback)
    {
      if (paramSubscriptionCallback == null)
      {
        MediaBrowserCompatApi21.unsubscribe(this.mBrowserObj, paramString);
        return;
      }
      MediaBrowserCompatApi24.unsubscribe(this.mBrowserObj, paramString, MediaBrowserCompat.SubscriptionCallback.access$200(paramSubscriptionCallback));
    }
  }
  
  static class MediaBrowserImplBase
    implements MediaBrowserCompat.MediaBrowserImpl, MediaBrowserCompat.MediaBrowserServiceCallbackImpl
  {
    static final int CONNECT_STATE_CONNECTED = 3;
    static final int CONNECT_STATE_CONNECTING = 2;
    static final int CONNECT_STATE_DISCONNECTED = 1;
    static final int CONNECT_STATE_DISCONNECTING = 0;
    static final int CONNECT_STATE_SUSPENDED = 4;
    final MediaBrowserCompat.ConnectionCallback mCallback;
    Messenger mCallbacksMessenger;
    final Context mContext;
    private Bundle mExtras;
    final MediaBrowserCompat.CallbackHandler mHandler = new MediaBrowserCompat.CallbackHandler(this);
    private MediaSessionCompat.Token mMediaSessionToken;
    final Bundle mRootHints;
    private String mRootId;
    MediaBrowserCompat.ServiceBinderWrapper mServiceBinderWrapper;
    final ComponentName mServiceComponent;
    MediaServiceConnection mServiceConnection;
    int mState = 1;
    private final ArrayMap<String, MediaBrowserCompat.Subscription> mSubscriptions = new ArrayMap();
    
    public MediaBrowserImplBase(Context paramContext, ComponentName paramComponentName, MediaBrowserCompat.ConnectionCallback paramConnectionCallback, Bundle paramBundle)
    {
      if (paramContext == null) {
        throw new IllegalArgumentException("context must not be null");
      }
      if (paramComponentName == null) {
        throw new IllegalArgumentException("service component must not be null");
      }
      if (paramConnectionCallback == null) {
        throw new IllegalArgumentException("connection callback must not be null");
      }
      this.mContext = paramContext;
      this.mServiceComponent = paramComponentName;
      this.mCallback = paramConnectionCallback;
      if (paramBundle == null) {}
      for (paramContext = null;; paramContext = new Bundle(paramBundle))
      {
        this.mRootHints = paramContext;
        return;
      }
    }
    
    private static String getStateLabel(int paramInt)
    {
      switch (paramInt)
      {
      default: 
        return "UNKNOWN/" + paramInt;
      case 0: 
        return "CONNECT_STATE_DISCONNECTING";
      case 1: 
        return "CONNECT_STATE_DISCONNECTED";
      case 2: 
        return "CONNECT_STATE_CONNECTING";
      case 3: 
        return "CONNECT_STATE_CONNECTED";
      }
      return "CONNECT_STATE_SUSPENDED";
    }
    
    private boolean isCurrent(Messenger paramMessenger, String paramString)
    {
      boolean bool = true;
      if ((this.mCallbacksMessenger != paramMessenger) || (this.mState == 0) || (this.mState == 1))
      {
        if ((this.mState != 0) && (this.mState != 1)) {
          Log.i("MediaBrowserCompat", paramString + " for " + this.mServiceComponent + " with mCallbacksMessenger=" + this.mCallbacksMessenger + " this=" + this);
        }
        bool = false;
      }
      return bool;
    }
    
    public void connect()
    {
      if ((this.mState != 0) && (this.mState != 1)) {
        throw new IllegalStateException("connect() called while neigther disconnecting nor disconnected (state=" + getStateLabel(this.mState) + ")");
      }
      this.mState = 2;
      this.mHandler.post(new Runnable()
      {
        public void run()
        {
          if (MediaBrowserCompat.MediaBrowserImplBase.this.mState == 0) {}
          do
          {
            return;
            MediaBrowserCompat.MediaBrowserImplBase.this.mState = 2;
            if ((MediaBrowserCompat.DEBUG) && (MediaBrowserCompat.MediaBrowserImplBase.this.mServiceConnection != null)) {
              throw new RuntimeException("mServiceConnection should be null. Instead it is " + MediaBrowserCompat.MediaBrowserImplBase.this.mServiceConnection);
            }
            if (MediaBrowserCompat.MediaBrowserImplBase.this.mServiceBinderWrapper != null) {
              throw new RuntimeException("mServiceBinderWrapper should be null. Instead it is " + MediaBrowserCompat.MediaBrowserImplBase.this.mServiceBinderWrapper);
            }
            if (MediaBrowserCompat.MediaBrowserImplBase.this.mCallbacksMessenger != null) {
              throw new RuntimeException("mCallbacksMessenger should be null. Instead it is " + MediaBrowserCompat.MediaBrowserImplBase.this.mCallbacksMessenger);
            }
            Intent localIntent = new Intent("android.media.browse.MediaBrowserService");
            localIntent.setComponent(MediaBrowserCompat.MediaBrowserImplBase.this.mServiceComponent);
            MediaBrowserCompat.MediaBrowserImplBase.this.mServiceConnection = new MediaBrowserCompat.MediaBrowserImplBase.MediaServiceConnection(MediaBrowserCompat.MediaBrowserImplBase.this);
            int i = 0;
            try
            {
              boolean bool = MediaBrowserCompat.MediaBrowserImplBase.this.mContext.bindService(localIntent, MediaBrowserCompat.MediaBrowserImplBase.this.mServiceConnection, 1);
              i = bool;
            }
            catch (Exception localException)
            {
              for (;;)
              {
                Log.e("MediaBrowserCompat", "Failed binding to service " + MediaBrowserCompat.MediaBrowserImplBase.this.mServiceComponent);
              }
            }
            if (i == 0)
            {
              MediaBrowserCompat.MediaBrowserImplBase.this.forceCloseConnection();
              MediaBrowserCompat.MediaBrowserImplBase.this.mCallback.onConnectionFailed();
            }
          } while (!MediaBrowserCompat.DEBUG);
          Log.d("MediaBrowserCompat", "connect...");
          MediaBrowserCompat.MediaBrowserImplBase.this.dump();
        }
      });
    }
    
    public void disconnect()
    {
      this.mState = 0;
      this.mHandler.post(new Runnable()
      {
        public void run()
        {
          if (MediaBrowserCompat.MediaBrowserImplBase.this.mCallbacksMessenger != null) {}
          try
          {
            MediaBrowserCompat.MediaBrowserImplBase.this.mServiceBinderWrapper.disconnect(MediaBrowserCompat.MediaBrowserImplBase.this.mCallbacksMessenger);
            int i = MediaBrowserCompat.MediaBrowserImplBase.this.mState;
            MediaBrowserCompat.MediaBrowserImplBase.this.forceCloseConnection();
            if (i != 0) {
              MediaBrowserCompat.MediaBrowserImplBase.this.mState = i;
            }
            if (MediaBrowserCompat.DEBUG)
            {
              Log.d("MediaBrowserCompat", "disconnect...");
              MediaBrowserCompat.MediaBrowserImplBase.this.dump();
            }
            return;
          }
          catch (RemoteException localRemoteException)
          {
            for (;;)
            {
              Log.w("MediaBrowserCompat", "RemoteException during connect for " + MediaBrowserCompat.MediaBrowserImplBase.this.mServiceComponent);
            }
          }
        }
      });
    }
    
    void dump()
    {
      Log.d("MediaBrowserCompat", "MediaBrowserCompat...");
      Log.d("MediaBrowserCompat", "  mServiceComponent=" + this.mServiceComponent);
      Log.d("MediaBrowserCompat", "  mCallback=" + this.mCallback);
      Log.d("MediaBrowserCompat", "  mRootHints=" + this.mRootHints);
      Log.d("MediaBrowserCompat", "  mState=" + getStateLabel(this.mState));
      Log.d("MediaBrowserCompat", "  mServiceConnection=" + this.mServiceConnection);
      Log.d("MediaBrowserCompat", "  mServiceBinderWrapper=" + this.mServiceBinderWrapper);
      Log.d("MediaBrowserCompat", "  mCallbacksMessenger=" + this.mCallbacksMessenger);
      Log.d("MediaBrowserCompat", "  mRootId=" + this.mRootId);
      Log.d("MediaBrowserCompat", "  mMediaSessionToken=" + this.mMediaSessionToken);
    }
    
    void forceCloseConnection()
    {
      if (this.mServiceConnection != null) {
        this.mContext.unbindService(this.mServiceConnection);
      }
      this.mState = 1;
      this.mServiceConnection = null;
      this.mServiceBinderWrapper = null;
      this.mCallbacksMessenger = null;
      this.mHandler.setCallbacksMessenger(null);
      this.mRootId = null;
      this.mMediaSessionToken = null;
    }
    
    @Nullable
    public Bundle getExtras()
    {
      if (!isConnected()) {
        throw new IllegalStateException("getExtras() called while not connected (state=" + getStateLabel(this.mState) + ")");
      }
      return this.mExtras;
    }
    
    public void getItem(@NonNull final String paramString, @NonNull final MediaBrowserCompat.ItemCallback paramItemCallback)
    {
      if (TextUtils.isEmpty(paramString)) {
        throw new IllegalArgumentException("mediaId is empty");
      }
      if (paramItemCallback == null) {
        throw new IllegalArgumentException("cb is null");
      }
      if (!isConnected())
      {
        Log.i("MediaBrowserCompat", "Not connected, unable to retrieve the MediaItem.");
        this.mHandler.post(new Runnable()
        {
          public void run()
          {
            paramItemCallback.onError(paramString);
          }
        });
        return;
      }
      MediaBrowserCompat.ItemReceiver localItemReceiver = new MediaBrowserCompat.ItemReceiver(paramString, paramItemCallback, this.mHandler);
      try
      {
        this.mServiceBinderWrapper.getMediaItem(paramString, localItemReceiver, this.mCallbacksMessenger);
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Log.i("MediaBrowserCompat", "Remote error getting media item: " + paramString);
        this.mHandler.post(new Runnable()
        {
          public void run()
          {
            paramItemCallback.onError(paramString);
          }
        });
      }
    }
    
    @NonNull
    public String getRoot()
    {
      if (!isConnected()) {
        throw new IllegalStateException("getRoot() called while not connected(state=" + getStateLabel(this.mState) + ")");
      }
      return this.mRootId;
    }
    
    @NonNull
    public ComponentName getServiceComponent()
    {
      if (!isConnected()) {
        throw new IllegalStateException("getServiceComponent() called while not connected (state=" + this.mState + ")");
      }
      return this.mServiceComponent;
    }
    
    @NonNull
    public MediaSessionCompat.Token getSessionToken()
    {
      if (!isConnected()) {
        throw new IllegalStateException("getSessionToken() called while not connected(state=" + this.mState + ")");
      }
      return this.mMediaSessionToken;
    }
    
    public boolean isConnected()
    {
      return this.mState == 3;
    }
    
    public void onConnectionFailed(Messenger paramMessenger)
    {
      Log.e("MediaBrowserCompat", "onConnectFailed for " + this.mServiceComponent);
      if (!isCurrent(paramMessenger, "onConnectFailed")) {
        return;
      }
      if (this.mState != 2)
      {
        Log.w("MediaBrowserCompat", "onConnect from service while mState=" + getStateLabel(this.mState) + "... ignoring");
        return;
      }
      forceCloseConnection();
      this.mCallback.onConnectionFailed();
    }
    
    public void onLoadChildren(Messenger paramMessenger, String paramString, List paramList, Bundle paramBundle)
    {
      if (!isCurrent(paramMessenger, "onLoadChildren")) {}
      do
      {
        do
        {
          return;
          if (MediaBrowserCompat.DEBUG) {
            Log.d("MediaBrowserCompat", "onLoadChildren for " + this.mServiceComponent + " id=" + paramString);
          }
          paramMessenger = (MediaBrowserCompat.Subscription)this.mSubscriptions.get(paramString);
          if (paramMessenger != null) {
            break;
          }
        } while (!MediaBrowserCompat.DEBUG);
        Log.d("MediaBrowserCompat", "onLoadChildren for id that isn't subscribed id=" + paramString);
        return;
        paramMessenger = paramMessenger.getCallback(paramBundle);
      } while (paramMessenger == null);
      if (paramBundle == null)
      {
        if (paramList == null)
        {
          paramMessenger.onError(paramString);
          return;
        }
        paramMessenger.onChildrenLoaded(paramString, paramList);
        return;
      }
      if (paramList == null)
      {
        paramMessenger.onError(paramString, paramBundle);
        return;
      }
      paramMessenger.onChildrenLoaded(paramString, paramList, paramBundle);
    }
    
    public void onServiceConnected(Messenger paramMessenger, String paramString, MediaSessionCompat.Token paramToken, Bundle paramBundle)
    {
      if (!isCurrent(paramMessenger, "onConnect")) {}
      for (;;)
      {
        return;
        if (this.mState != 2)
        {
          Log.w("MediaBrowserCompat", "onConnect from service while mState=" + getStateLabel(this.mState) + "... ignoring");
          return;
        }
        this.mRootId = paramString;
        this.mMediaSessionToken = paramToken;
        this.mExtras = paramBundle;
        this.mState = 3;
        if (MediaBrowserCompat.DEBUG)
        {
          Log.d("MediaBrowserCompat", "ServiceCallbacks.onConnect...");
          dump();
        }
        this.mCallback.onConnected();
        try
        {
          paramMessenger = this.mSubscriptions.entrySet().iterator();
          while (paramMessenger.hasNext())
          {
            paramToken = (Map.Entry)paramMessenger.next();
            paramString = (String)paramToken.getKey();
            paramBundle = (MediaBrowserCompat.Subscription)paramToken.getValue();
            paramToken = paramBundle.getCallbacks();
            paramBundle = paramBundle.getOptionsList();
            int i = 0;
            while (i < paramToken.size())
            {
              this.mServiceBinderWrapper.addSubscription(paramString, MediaBrowserCompat.SubscriptionCallback.access$000((MediaBrowserCompat.SubscriptionCallback)paramToken.get(i)), (Bundle)paramBundle.get(i), this.mCallbacksMessenger);
              i += 1;
            }
          }
          return;
        }
        catch (RemoteException paramMessenger)
        {
          Log.d("MediaBrowserCompat", "addSubscription failed with RemoteException.");
        }
      }
    }
    
    public void search(@NonNull final String paramString, final Bundle paramBundle, @NonNull final MediaBrowserCompat.SearchCallback paramSearchCallback)
    {
      if (!isConnected()) {
        throw new IllegalStateException("search() called while not connected (state=" + getStateLabel(this.mState) + ")");
      }
      MediaBrowserCompat.SearchResultReceiver localSearchResultReceiver = new MediaBrowserCompat.SearchResultReceiver(paramString, paramBundle, paramSearchCallback, this.mHandler);
      try
      {
        this.mServiceBinderWrapper.search(paramString, paramBundle, localSearchResultReceiver, this.mCallbacksMessenger);
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Log.i("MediaBrowserCompat", "Remote error searching items with query: " + paramString, localRemoteException);
        this.mHandler.post(new Runnable()
        {
          public void run()
          {
            paramSearchCallback.onError(paramString, paramBundle);
          }
        });
      }
    }
    
    public void sendCustomAction(@NonNull final String paramString, final Bundle paramBundle, @Nullable final MediaBrowserCompat.CustomActionCallback paramCustomActionCallback)
    {
      if (!isConnected()) {
        throw new IllegalStateException("Cannot send a custom action (" + paramString + ") with " + "extras " + paramBundle + " because the browser is not connected to the " + "service.");
      }
      MediaBrowserCompat.CustomActionResultReceiver localCustomActionResultReceiver = new MediaBrowserCompat.CustomActionResultReceiver(paramString, paramBundle, paramCustomActionCallback, this.mHandler);
      try
      {
        this.mServiceBinderWrapper.sendCustomAction(paramString, paramBundle, localCustomActionResultReceiver, this.mCallbacksMessenger);
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Log.i("MediaBrowserCompat", "Remote error sending a custom action: action=" + paramString + ", extras=" + paramBundle, localRemoteException);
        this.mHandler.post(new Runnable()
        {
          public void run()
          {
            paramCustomActionCallback.onError(paramString, paramBundle, null);
          }
        });
      }
    }
    
    public void subscribe(@NonNull String paramString, Bundle paramBundle, @NonNull MediaBrowserCompat.SubscriptionCallback paramSubscriptionCallback)
    {
      MediaBrowserCompat.Subscription localSubscription2 = (MediaBrowserCompat.Subscription)this.mSubscriptions.get(paramString);
      MediaBrowserCompat.Subscription localSubscription1 = localSubscription2;
      if (localSubscription2 == null)
      {
        localSubscription1 = new MediaBrowserCompat.Subscription();
        this.mSubscriptions.put(paramString, localSubscription1);
      }
      if (paramBundle == null) {}
      for (paramBundle = null;; paramBundle = new Bundle(paramBundle))
      {
        localSubscription1.putCallback(paramBundle, paramSubscriptionCallback);
        if (isConnected()) {}
        try
        {
          this.mServiceBinderWrapper.addSubscription(paramString, MediaBrowserCompat.SubscriptionCallback.access$000(paramSubscriptionCallback), paramBundle, this.mCallbacksMessenger);
          return;
        }
        catch (RemoteException paramBundle)
        {
          Log.d("MediaBrowserCompat", "addSubscription failed with RemoteException parentId=" + paramString);
        }
      }
    }
    
    public void unsubscribe(@NonNull String paramString, MediaBrowserCompat.SubscriptionCallback paramSubscriptionCallback)
    {
      MediaBrowserCompat.Subscription localSubscription = (MediaBrowserCompat.Subscription)this.mSubscriptions.get(paramString);
      if (localSubscription == null) {}
      for (;;)
      {
        return;
        if (paramSubscriptionCallback == null) {}
        try
        {
          if (isConnected()) {
            this.mServiceBinderWrapper.removeSubscription(paramString, null, this.mCallbacksMessenger);
          }
          while ((localSubscription.isEmpty()) || (paramSubscriptionCallback == null))
          {
            this.mSubscriptions.remove(paramString);
            return;
            List localList1 = localSubscription.getCallbacks();
            List localList2 = localSubscription.getOptionsList();
            int i = localList1.size() - 1;
            while (i >= 0)
            {
              if (localList1.get(i) == paramSubscriptionCallback)
              {
                if (isConnected()) {
                  this.mServiceBinderWrapper.removeSubscription(paramString, MediaBrowserCompat.SubscriptionCallback.access$000(paramSubscriptionCallback), this.mCallbacksMessenger);
                }
                localList1.remove(i);
                localList2.remove(i);
              }
              i -= 1;
            }
          }
        }
        catch (RemoteException localRemoteException)
        {
          for (;;)
          {
            Log.d("MediaBrowserCompat", "removeSubscription failed with RemoteException parentId=" + paramString);
          }
        }
      }
    }
    
    private class MediaServiceConnection
      implements ServiceConnection
    {
      MediaServiceConnection() {}
      
      private void postOrRun(Runnable paramRunnable)
      {
        if (Thread.currentThread() == MediaBrowserCompat.MediaBrowserImplBase.this.mHandler.getLooper().getThread())
        {
          paramRunnable.run();
          return;
        }
        MediaBrowserCompat.MediaBrowserImplBase.this.mHandler.post(paramRunnable);
      }
      
      boolean isCurrent(String paramString)
      {
        boolean bool = true;
        if ((MediaBrowserCompat.MediaBrowserImplBase.this.mServiceConnection != this) || (MediaBrowserCompat.MediaBrowserImplBase.this.mState == 0) || (MediaBrowserCompat.MediaBrowserImplBase.this.mState == 1))
        {
          if ((MediaBrowserCompat.MediaBrowserImplBase.this.mState != 0) && (MediaBrowserCompat.MediaBrowserImplBase.this.mState != 1)) {
            Log.i("MediaBrowserCompat", paramString + " for " + MediaBrowserCompat.MediaBrowserImplBase.this.mServiceComponent + " with mServiceConnection=" + MediaBrowserCompat.MediaBrowserImplBase.this.mServiceConnection + " this=" + this);
          }
          bool = false;
        }
        return bool;
      }
      
      public void onServiceConnected(final ComponentName paramComponentName, final IBinder paramIBinder)
      {
        postOrRun(new Runnable()
        {
          public void run()
          {
            if (MediaBrowserCompat.DEBUG)
            {
              Log.d("MediaBrowserCompat", "MediaServiceConnection.onServiceConnected name=" + paramComponentName + " binder=" + paramIBinder);
              MediaBrowserCompat.MediaBrowserImplBase.this.dump();
            }
            if (!MediaBrowserCompat.MediaBrowserImplBase.MediaServiceConnection.this.isCurrent("onServiceConnected")) {}
            do
            {
              return;
              MediaBrowserCompat.MediaBrowserImplBase.this.mServiceBinderWrapper = new MediaBrowserCompat.ServiceBinderWrapper(paramIBinder, MediaBrowserCompat.MediaBrowserImplBase.this.mRootHints);
              MediaBrowserCompat.MediaBrowserImplBase.this.mCallbacksMessenger = new Messenger(MediaBrowserCompat.MediaBrowserImplBase.this.mHandler);
              MediaBrowserCompat.MediaBrowserImplBase.this.mHandler.setCallbacksMessenger(MediaBrowserCompat.MediaBrowserImplBase.this.mCallbacksMessenger);
              MediaBrowserCompat.MediaBrowserImplBase.this.mState = 2;
              try
              {
                if (MediaBrowserCompat.DEBUG)
                {
                  Log.d("MediaBrowserCompat", "ServiceCallbacks.onConnect...");
                  MediaBrowserCompat.MediaBrowserImplBase.this.dump();
                }
                MediaBrowserCompat.MediaBrowserImplBase.this.mServiceBinderWrapper.connect(MediaBrowserCompat.MediaBrowserImplBase.this.mContext, MediaBrowserCompat.MediaBrowserImplBase.this.mCallbacksMessenger);
                return;
              }
              catch (RemoteException localRemoteException)
              {
                Log.w("MediaBrowserCompat", "RemoteException during connect for " + MediaBrowserCompat.MediaBrowserImplBase.this.mServiceComponent);
              }
            } while (!MediaBrowserCompat.DEBUG);
            Log.d("MediaBrowserCompat", "ServiceCallbacks.onConnect...");
            MediaBrowserCompat.MediaBrowserImplBase.this.dump();
          }
        });
      }
      
      public void onServiceDisconnected(final ComponentName paramComponentName)
      {
        postOrRun(new Runnable()
        {
          public void run()
          {
            if (MediaBrowserCompat.DEBUG)
            {
              Log.d("MediaBrowserCompat", "MediaServiceConnection.onServiceDisconnected name=" + paramComponentName + " this=" + this + " mServiceConnection=" + MediaBrowserCompat.MediaBrowserImplBase.this.mServiceConnection);
              MediaBrowserCompat.MediaBrowserImplBase.this.dump();
            }
            if (!MediaBrowserCompat.MediaBrowserImplBase.MediaServiceConnection.this.isCurrent("onServiceDisconnected")) {
              return;
            }
            MediaBrowserCompat.MediaBrowserImplBase.this.mServiceBinderWrapper = null;
            MediaBrowserCompat.MediaBrowserImplBase.this.mCallbacksMessenger = null;
            MediaBrowserCompat.MediaBrowserImplBase.this.mHandler.setCallbacksMessenger(null);
            MediaBrowserCompat.MediaBrowserImplBase.this.mState = 4;
            MediaBrowserCompat.MediaBrowserImplBase.this.mCallback.onConnectionSuspended();
          }
        });
      }
    }
  }
  
  static abstract interface MediaBrowserServiceCallbackImpl
  {
    public abstract void onConnectionFailed(Messenger paramMessenger);
    
    public abstract void onLoadChildren(Messenger paramMessenger, String paramString, List paramList, Bundle paramBundle);
    
    public abstract void onServiceConnected(Messenger paramMessenger, String paramString, MediaSessionCompat.Token paramToken, Bundle paramBundle);
  }
  
  public static class MediaItem
    implements Parcelable
  {
    public static final Parcelable.Creator<MediaItem> CREATOR = new Parcelable.Creator()
    {
      public MediaBrowserCompat.MediaItem createFromParcel(Parcel paramAnonymousParcel)
      {
        return new MediaBrowserCompat.MediaItem(paramAnonymousParcel);
      }
      
      public MediaBrowserCompat.MediaItem[] newArray(int paramAnonymousInt)
      {
        return new MediaBrowserCompat.MediaItem[paramAnonymousInt];
      }
    };
    public static final int FLAG_BROWSABLE = 1;
    public static final int FLAG_PLAYABLE = 2;
    private final MediaDescriptionCompat mDescription;
    private final int mFlags;
    
    MediaItem(Parcel paramParcel)
    {
      this.mFlags = paramParcel.readInt();
      this.mDescription = ((MediaDescriptionCompat)MediaDescriptionCompat.CREATOR.createFromParcel(paramParcel));
    }
    
    public MediaItem(@NonNull MediaDescriptionCompat paramMediaDescriptionCompat, int paramInt)
    {
      if (paramMediaDescriptionCompat == null) {
        throw new IllegalArgumentException("description cannot be null");
      }
      if (TextUtils.isEmpty(paramMediaDescriptionCompat.getMediaId())) {
        throw new IllegalArgumentException("description must have a non-empty media id");
      }
      this.mFlags = paramInt;
      this.mDescription = paramMediaDescriptionCompat;
    }
    
    public static MediaItem fromMediaItem(Object paramObject)
    {
      if ((paramObject == null) || (Build.VERSION.SDK_INT < 21)) {
        return null;
      }
      int i = MediaBrowserCompatApi21.MediaItem.getFlags(paramObject);
      return new MediaItem(MediaDescriptionCompat.fromMediaDescription(MediaBrowserCompatApi21.MediaItem.getDescription(paramObject)), i);
    }
    
    public static List<MediaItem> fromMediaItemList(List<?> paramList)
    {
      if ((paramList == null) || (Build.VERSION.SDK_INT < 21))
      {
        paramList = null;
        return paramList;
      }
      ArrayList localArrayList = new ArrayList(paramList.size());
      Iterator localIterator = paramList.iterator();
      for (;;)
      {
        paramList = localArrayList;
        if (!localIterator.hasNext()) {
          break;
        }
        localArrayList.add(fromMediaItem(localIterator.next()));
      }
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    @NonNull
    public MediaDescriptionCompat getDescription()
    {
      return this.mDescription;
    }
    
    public int getFlags()
    {
      return this.mFlags;
    }
    
    @Nullable
    public String getMediaId()
    {
      return this.mDescription.getMediaId();
    }
    
    public boolean isBrowsable()
    {
      return (this.mFlags & 0x1) != 0;
    }
    
    public boolean isPlayable()
    {
      return (this.mFlags & 0x2) != 0;
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder("MediaItem{");
      localStringBuilder.append("mFlags=").append(this.mFlags);
      localStringBuilder.append(", mDescription=").append(this.mDescription);
      localStringBuilder.append('}');
      return localStringBuilder.toString();
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramParcel.writeInt(this.mFlags);
      this.mDescription.writeToParcel(paramParcel, paramInt);
    }
    
    @Retention(RetentionPolicy.SOURCE)
    @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
    public static @interface Flags {}
  }
  
  public static abstract class SearchCallback
  {
    public void onError(@NonNull String paramString, Bundle paramBundle) {}
    
    public void onSearchResult(@NonNull String paramString, Bundle paramBundle, @NonNull List<MediaBrowserCompat.MediaItem> paramList) {}
  }
  
  private static class SearchResultReceiver
    extends ResultReceiver
  {
    private final MediaBrowserCompat.SearchCallback mCallback;
    private final Bundle mExtras;
    private final String mQuery;
    
    SearchResultReceiver(String paramString, Bundle paramBundle, MediaBrowserCompat.SearchCallback paramSearchCallback, Handler paramHandler)
    {
      super();
      this.mQuery = paramString;
      this.mExtras = paramBundle;
      this.mCallback = paramSearchCallback;
    }
    
    protected void onReceiveResult(int paramInt, Bundle paramBundle)
    {
      if (paramBundle != null) {
        paramBundle.setClassLoader(MediaBrowserCompat.class.getClassLoader());
      }
      if ((paramInt != 0) || (paramBundle == null) || (!paramBundle.containsKey("search_results")))
      {
        this.mCallback.onError(this.mQuery, this.mExtras);
        return;
      }
      Parcelable[] arrayOfParcelable = paramBundle.getParcelableArray("search_results");
      paramBundle = null;
      if (arrayOfParcelable != null)
      {
        ArrayList localArrayList = new ArrayList();
        int i = arrayOfParcelable.length;
        paramInt = 0;
        for (;;)
        {
          paramBundle = localArrayList;
          if (paramInt >= i) {
            break;
          }
          localArrayList.add((MediaBrowserCompat.MediaItem)arrayOfParcelable[paramInt]);
          paramInt += 1;
        }
      }
      this.mCallback.onSearchResult(this.mQuery, this.mExtras, paramBundle);
    }
  }
  
  private static class ServiceBinderWrapper
  {
    private Messenger mMessenger;
    private Bundle mRootHints;
    
    public ServiceBinderWrapper(IBinder paramIBinder, Bundle paramBundle)
    {
      this.mMessenger = new Messenger(paramIBinder);
      this.mRootHints = paramBundle;
    }
    
    private void sendRequest(int paramInt, Bundle paramBundle, Messenger paramMessenger)
      throws RemoteException
    {
      Message localMessage = Message.obtain();
      localMessage.what = paramInt;
      localMessage.arg1 = 1;
      localMessage.setData(paramBundle);
      localMessage.replyTo = paramMessenger;
      this.mMessenger.send(localMessage);
    }
    
    void addSubscription(String paramString, IBinder paramIBinder, Bundle paramBundle, Messenger paramMessenger)
      throws RemoteException
    {
      Bundle localBundle = new Bundle();
      localBundle.putString("data_media_item_id", paramString);
      BundleCompat.putBinder(localBundle, "data_callback_token", paramIBinder);
      localBundle.putBundle("data_options", paramBundle);
      sendRequest(3, localBundle, paramMessenger);
    }
    
    void connect(Context paramContext, Messenger paramMessenger)
      throws RemoteException
    {
      Bundle localBundle = new Bundle();
      localBundle.putString("data_package_name", paramContext.getPackageName());
      localBundle.putBundle("data_root_hints", this.mRootHints);
      sendRequest(1, localBundle, paramMessenger);
    }
    
    void disconnect(Messenger paramMessenger)
      throws RemoteException
    {
      sendRequest(2, null, paramMessenger);
    }
    
    void getMediaItem(String paramString, ResultReceiver paramResultReceiver, Messenger paramMessenger)
      throws RemoteException
    {
      Bundle localBundle = new Bundle();
      localBundle.putString("data_media_item_id", paramString);
      localBundle.putParcelable("data_result_receiver", paramResultReceiver);
      sendRequest(5, localBundle, paramMessenger);
    }
    
    void registerCallbackMessenger(Messenger paramMessenger)
      throws RemoteException
    {
      Bundle localBundle = new Bundle();
      localBundle.putBundle("data_root_hints", this.mRootHints);
      sendRequest(6, localBundle, paramMessenger);
    }
    
    void removeSubscription(String paramString, IBinder paramIBinder, Messenger paramMessenger)
      throws RemoteException
    {
      Bundle localBundle = new Bundle();
      localBundle.putString("data_media_item_id", paramString);
      BundleCompat.putBinder(localBundle, "data_callback_token", paramIBinder);
      sendRequest(4, localBundle, paramMessenger);
    }
    
    void search(String paramString, Bundle paramBundle, ResultReceiver paramResultReceiver, Messenger paramMessenger)
      throws RemoteException
    {
      Bundle localBundle = new Bundle();
      localBundle.putString("data_search_query", paramString);
      localBundle.putBundle("data_search_extras", paramBundle);
      localBundle.putParcelable("data_result_receiver", paramResultReceiver);
      sendRequest(8, localBundle, paramMessenger);
    }
    
    void sendCustomAction(String paramString, Bundle paramBundle, ResultReceiver paramResultReceiver, Messenger paramMessenger)
      throws RemoteException
    {
      Bundle localBundle = new Bundle();
      localBundle.putString("data_custom_action", paramString);
      localBundle.putBundle("data_custom_action_extras", paramBundle);
      localBundle.putParcelable("data_result_receiver", paramResultReceiver);
      sendRequest(9, localBundle, paramMessenger);
    }
    
    void unregisterCallbackMessenger(Messenger paramMessenger)
      throws RemoteException
    {
      sendRequest(7, null, paramMessenger);
    }
  }
  
  private static class Subscription
  {
    private final List<MediaBrowserCompat.SubscriptionCallback> mCallbacks = new ArrayList();
    private final List<Bundle> mOptionsList = new ArrayList();
    
    public MediaBrowserCompat.SubscriptionCallback getCallback(Bundle paramBundle)
    {
      int i = 0;
      while (i < this.mOptionsList.size())
      {
        if (MediaBrowserCompatUtils.areSameOptions((Bundle)this.mOptionsList.get(i), paramBundle)) {
          return (MediaBrowserCompat.SubscriptionCallback)this.mCallbacks.get(i);
        }
        i += 1;
      }
      return null;
    }
    
    public List<MediaBrowserCompat.SubscriptionCallback> getCallbacks()
    {
      return this.mCallbacks;
    }
    
    public List<Bundle> getOptionsList()
    {
      return this.mOptionsList;
    }
    
    public boolean isEmpty()
    {
      return this.mCallbacks.isEmpty();
    }
    
    public void putCallback(Bundle paramBundle, MediaBrowserCompat.SubscriptionCallback paramSubscriptionCallback)
    {
      int i = 0;
      while (i < this.mOptionsList.size())
      {
        if (MediaBrowserCompatUtils.areSameOptions((Bundle)this.mOptionsList.get(i), paramBundle))
        {
          this.mCallbacks.set(i, paramSubscriptionCallback);
          return;
        }
        i += 1;
      }
      this.mCallbacks.add(paramSubscriptionCallback);
      this.mOptionsList.add(paramBundle);
    }
  }
  
  public static abstract class SubscriptionCallback
  {
    private final Object mSubscriptionCallbackObj;
    WeakReference<MediaBrowserCompat.Subscription> mSubscriptionRef;
    private final IBinder mToken;
    
    public SubscriptionCallback()
    {
      if (Build.VERSION.SDK_INT >= 26)
      {
        this.mSubscriptionCallbackObj = MediaBrowserCompatApi24.createSubscriptionCallback(new StubApi24());
        this.mToken = null;
        return;
      }
      if (Build.VERSION.SDK_INT >= 21)
      {
        this.mSubscriptionCallbackObj = MediaBrowserCompatApi21.createSubscriptionCallback(new StubApi21());
        this.mToken = new Binder();
        return;
      }
      this.mSubscriptionCallbackObj = null;
      this.mToken = new Binder();
    }
    
    private void setSubscription(MediaBrowserCompat.Subscription paramSubscription)
    {
      this.mSubscriptionRef = new WeakReference(paramSubscription);
    }
    
    public void onChildrenLoaded(@NonNull String paramString, @NonNull List<MediaBrowserCompat.MediaItem> paramList) {}
    
    public void onChildrenLoaded(@NonNull String paramString, @NonNull List<MediaBrowserCompat.MediaItem> paramList, @NonNull Bundle paramBundle) {}
    
    public void onError(@NonNull String paramString) {}
    
    public void onError(@NonNull String paramString, @NonNull Bundle paramBundle) {}
    
    private class StubApi21
      implements MediaBrowserCompatApi21.SubscriptionCallback
    {
      StubApi21() {}
      
      List<MediaBrowserCompat.MediaItem> applyOptions(List<MediaBrowserCompat.MediaItem> paramList, Bundle paramBundle)
      {
        if (paramList == null) {
          paramBundle = null;
        }
        int m;
        do
        {
          return paramBundle;
          i = paramBundle.getInt("android.media.browse.extra.PAGE", -1);
          m = paramBundle.getInt("android.media.browse.extra.PAGE_SIZE", -1);
          if (i != -1) {
            break;
          }
          paramBundle = paramList;
        } while (m == -1);
        int k = m * i;
        int j = k + m;
        if ((i < 0) || (m < 1) || (k >= paramList.size())) {
          return Collections.EMPTY_LIST;
        }
        int i = j;
        if (j > paramList.size()) {
          i = paramList.size();
        }
        return paramList.subList(k, i);
      }
      
      public void onChildrenLoaded(@NonNull String paramString, List<?> paramList)
      {
        if (MediaBrowserCompat.SubscriptionCallback.this.mSubscriptionRef == null) {}
        for (Object localObject = null; localObject == null; localObject = (MediaBrowserCompat.Subscription)MediaBrowserCompat.SubscriptionCallback.this.mSubscriptionRef.get())
        {
          MediaBrowserCompat.SubscriptionCallback.this.onChildrenLoaded(paramString, MediaBrowserCompat.MediaItem.fromMediaItemList(paramList));
          return;
        }
        paramList = MediaBrowserCompat.MediaItem.fromMediaItemList(paramList);
        List localList = ((MediaBrowserCompat.Subscription)localObject).getCallbacks();
        localObject = ((MediaBrowserCompat.Subscription)localObject).getOptionsList();
        int i = 0;
        label70:
        Bundle localBundle;
        if (i < localList.size())
        {
          localBundle = (Bundle)((List)localObject).get(i);
          if (localBundle != null) {
            break label115;
          }
          MediaBrowserCompat.SubscriptionCallback.this.onChildrenLoaded(paramString, paramList);
        }
        for (;;)
        {
          i += 1;
          break label70;
          break;
          label115:
          MediaBrowserCompat.SubscriptionCallback.this.onChildrenLoaded(paramString, applyOptions(paramList, localBundle), localBundle);
        }
      }
      
      public void onError(@NonNull String paramString)
      {
        MediaBrowserCompat.SubscriptionCallback.this.onError(paramString);
      }
    }
    
    private class StubApi24
      extends MediaBrowserCompat.SubscriptionCallback.StubApi21
      implements MediaBrowserCompatApi24.SubscriptionCallback
    {
      StubApi24()
      {
        super();
      }
      
      public void onChildrenLoaded(@NonNull String paramString, List<?> paramList, @NonNull Bundle paramBundle)
      {
        MediaBrowserCompat.SubscriptionCallback.this.onChildrenLoaded(paramString, MediaBrowserCompat.MediaItem.fromMediaItemList(paramList), paramBundle);
      }
      
      public void onError(@NonNull String paramString, @NonNull Bundle paramBundle)
      {
        MediaBrowserCompat.SubscriptionCallback.this.onError(paramString, paramBundle);
      }
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v4/media/MediaBrowserCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */