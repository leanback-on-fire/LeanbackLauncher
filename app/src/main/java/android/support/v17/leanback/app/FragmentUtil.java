package android.support.v17.leanback.app;

import android.app.Fragment;
import android.content.Context;
import android.os.Build.VERSION;
import android.support.annotation.RequiresApi;

class FragmentUtil
{
  public static Context getContext(Fragment paramFragment)
  {
    if (Build.VERSION.SDK_INT >= 23) {
      return getContextNew(paramFragment);
    }
    return paramFragment.getActivity();
  }
  
  @RequiresApi(23)
  private static Context getContextNew(Fragment paramFragment)
  {
    return paramFragment.getContext();
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/app/FragmentUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */