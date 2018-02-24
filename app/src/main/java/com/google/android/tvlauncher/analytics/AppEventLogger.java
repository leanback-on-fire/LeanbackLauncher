package com.google.android.tvlauncher.analytics;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.Builder;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.usagereporting.UsageReporting;
import com.google.android.gms.usagereporting.UsageReportingApi;
import com.google.android.gms.usagereporting.UsageReportingApi.OptInOptionsResult;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AppEventLogger
  implements EventLogger
{
  private static final long DEFAULT_PENDING_PARAMETERS_TIMEOUT = 5000L;
  private static final int MSG_FLUSH_PENDING_EVENT = 1;
  private static final String TAG = "FA-AppEventLogger";
  private static AppEventLogger sInstance;
  private final EventLoggerEngine mEngine;
  private List<String> mExpectedParameters;
  private Handler mHandler;
  private String mPendingEventName;
  private Bundle mPendingEventParameters;
  
  @VisibleForTesting
  AppEventLogger(EventLoggerEngine paramEventLoggerEngine)
  {
    this.mEngine = paramEventLoggerEngine;
  }
  
  private static void checkOptedInForUsageReporting(Context paramContext)
  {
    if (Build.TYPE.equals("unknown")) {
      return;
    }
    paramContext = new GoogleApiClient.Builder(paramContext).addApi(UsageReporting.API).build();
    UsageReporting.UsageReportingApi.getOptInOptions(paramContext).setResultCallback(new ResultCallback()
    {
      public void onResult(@NonNull UsageReportingApi.OptInOptionsResult paramAnonymousOptInOptionsResult)
      {
        if (paramAnonymousOptInOptionsResult.getStatus().isSuccess()) {
          AppEventLogger.getInstance().setUsageReportingOptedIn(paramAnonymousOptInOptionsResult.isOptedInForUsageReporting());
        }
        this.val$apiClient.disconnect();
      }
    });
    paramContext.connect();
  }
  
  private void flushPendingEvent()
  {
    if (this.mExpectedParameters != null)
    {
      this.mEngine.logEvent(this.mPendingEventName, this.mPendingEventParameters);
      this.mPendingEventName = null;
      this.mPendingEventParameters = null;
      this.mExpectedParameters = null;
    }
  }
  
  @MainThread
  public static AppEventLogger getInstance()
  {
    return sInstance;
  }
  
  @Nullable
  private Bundle getParameters(LogEvent paramLogEvent)
  {
    Bundle localBundle1 = paramLogEvent.getParameters();
    paramLogEvent = paramLogEvent.getRestrictedParameters();
    if ((localBundle1 != null) && (paramLogEvent != null))
    {
      Bundle localBundle2 = new Bundle();
      localBundle2.putAll(localBundle1);
      localBundle2.putAll(paramLogEvent);
      return localBundle2;
    }
    if (localBundle1 != null) {
      return localBundle1;
    }
    if (paramLogEvent != null) {
      return paramLogEvent;
    }
    return null;
  }
  
  @MainThread
  public static void init(Context paramContext, EventLoggerEngine paramEventLoggerEngine)
  {
    sInstance = new AppEventLogger(paramEventLoggerEngine);
    checkOptedInForUsageReporting(paramContext);
  }
  
  private void mergePendingParameters(LogEvent paramLogEvent)
  {
    if (this.mExpectedParameters == null) {
      Log.e("FA-AppEventLogger", "Unexpected log event parameters " + paramLogEvent);
    }
    do
    {
      do
      {
        return;
        if (!TextUtils.equals(this.mPendingEventName, paramLogEvent.getName()))
        {
          Log.e("FA-AppEventLogger", "Parameters for a previous event " + paramLogEvent + ", expected " + this.mPendingEventName);
          return;
        }
        paramLogEvent = getParameters(paramLogEvent);
      } while (paramLogEvent == null);
      HashSet localHashSet = new HashSet(paramLogEvent.keySet());
      localHashSet.removeAll(this.mExpectedParameters);
      if (!localHashSet.isEmpty()) {
        throw new IllegalArgumentException("Unexpected log event parameters: " + localHashSet);
      }
      if (this.mPendingEventParameters == null) {
        this.mPendingEventParameters = new Bundle();
      }
      this.mPendingEventParameters.putAll(paramLogEvent);
    } while (!this.mPendingEventParameters.keySet().containsAll(this.mExpectedParameters));
    flushPendingEvent();
  }
  
  private void setUsageReportingOptedIn(boolean paramBoolean)
  {
    this.mEngine.setEnabled(paramBoolean);
  }
  
  public void log(LogEvent paramLogEvent)
  {
    if (!this.mEngine.isEnabled()) {
      return;
    }
    if ((paramLogEvent instanceof LogEventParameters))
    {
      mergePendingParameters(paramLogEvent);
      return;
    }
    flushPendingEvent();
    String[] arrayOfString = paramLogEvent.getExpectedParameters();
    if ((arrayOfString != null) && (arrayOfString.length != 0))
    {
      this.mPendingEventName = paramLogEvent.getName();
      this.mPendingEventParameters = getParameters(paramLogEvent);
      this.mExpectedParameters = Arrays.asList(arrayOfString);
      long l2 = paramLogEvent.getParameterTimeout();
      long l1 = l2;
      if (l2 == 0L) {
        l1 = 5000L;
      }
      if (this.mHandler == null) {
        this.mHandler = new Handler(Looper.getMainLooper())
        {
          public void handleMessage(Message paramAnonymousMessage)
          {
            if (paramAnonymousMessage.what == 1) {
              AppEventLogger.this.flushPendingEvent();
            }
          }
        };
      }
      this.mHandler.removeMessages(1);
      this.mHandler.sendEmptyMessageDelayed(1, l1);
      return;
    }
    this.mEngine.logEvent(paramLogEvent.getName(), getParameters(paramLogEvent));
  }
  
  void setName(Activity paramActivity, String paramString)
  {
    this.mEngine.setCurrentScreen(paramActivity, null, paramString);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/analytics/AppEventLogger.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */