package com.google.android.tvlauncher.util;

import android.content.AsyncTaskLoader;
import android.content.Context;

public abstract class LauncherAsyncTaskLoader<T>
  extends AsyncTaskLoader<T>
{
  private T mResult;
  
  public LauncherAsyncTaskLoader(Context paramContext)
  {
    super(paramContext);
  }
  
  public void deliverResult(T paramT)
  {
    if (isReset()) {}
    do
    {
      return;
      this.mResult = paramT;
    } while (!isStarted());
    super.deliverResult(paramT);
  }
  
  protected void onReset()
  {
    super.onReset();
    onStopLoading();
    this.mResult = null;
  }
  
  protected void onStartLoading()
  {
    if (this.mResult != null) {
      deliverResult(this.mResult);
    }
    if ((takeContentChanged()) || (this.mResult == null)) {
      forceLoad();
    }
  }
  
  protected void onStopLoading()
  {
    cancelLoad();
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/util/LauncherAsyncTaskLoader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */