package android.support.v17.leanback.app;

import android.os.Build.VERSION;
import android.support.annotation.RestrictTo;

@RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
public class PermissionHelper
{
  public static void requestPermissions(android.app.Fragment paramFragment, String[] paramArrayOfString, int paramInt)
  {
    if (Build.VERSION.SDK_INT >= 23) {
      PermissionHelper23.requestPermissions(paramFragment, paramArrayOfString, paramInt);
    }
  }
  
  public static void requestPermissions(android.support.v4.app.Fragment paramFragment, String[] paramArrayOfString, int paramInt)
  {
    paramFragment.requestPermissions(paramArrayOfString, paramInt);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/app/PermissionHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */