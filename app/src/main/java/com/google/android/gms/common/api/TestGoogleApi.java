package com.google.android.gms.common.api;

import android.content.Context;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import com.google.android.gms.internal.zzym;

public class TestGoogleApi<O extends Api.ApiOptions>
  extends zzd<O>
{
  public TestGoogleApi(@NonNull Context paramContext, Api<O> paramApi, O paramO, Looper paramLooper)
  {
    super(paramContext, paramApi, paramO, paramLooper, new zzym());
  }
  
  public TestGoogleApi(@NonNull FragmentActivity paramFragmentActivity, Api<O> paramApi, O paramO)
  {
    super(paramFragmentActivity, paramApi, paramO, new zzym());
  }
  
  public TestGoogleApi(@NonNull FragmentActivity paramFragmentActivity, Api<O> paramApi, O paramO, Looper paramLooper)
  {
    super(paramFragmentActivity, paramApi, paramO, paramLooper, new zzym());
  }
  
  public GoogleApiClient asGoogleApiClient()
  {
    return super.asGoogleApiClient();
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/common/api/TestGoogleApi.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */