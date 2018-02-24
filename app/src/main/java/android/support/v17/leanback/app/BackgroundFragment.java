package android.support.v17.leanback.app;

import android.app.Fragment;
import android.support.annotation.RestrictTo;

@RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
public final class BackgroundFragment
  extends Fragment
{
  private BackgroundManager mBackgroundManager;
  
  BackgroundManager getBackgroundManager()
  {
    return this.mBackgroundManager;
  }
  
  public void onDestroy()
  {
    super.onDestroy();
    if (this.mBackgroundManager != null) {
      this.mBackgroundManager.detach();
    }
  }
  
  public void onResume()
  {
    super.onResume();
    if (this.mBackgroundManager != null) {
      this.mBackgroundManager.onResume();
    }
  }
  
  public void onStart()
  {
    super.onStart();
    if (this.mBackgroundManager != null) {
      this.mBackgroundManager.onActivityStart();
    }
  }
  
  public void onStop()
  {
    if (this.mBackgroundManager != null) {
      this.mBackgroundManager.onStop();
    }
    super.onStop();
  }
  
  void setBackgroundManager(BackgroundManager paramBackgroundManager)
  {
    this.mBackgroundManager = paramBackgroundManager;
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/app/BackgroundFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */