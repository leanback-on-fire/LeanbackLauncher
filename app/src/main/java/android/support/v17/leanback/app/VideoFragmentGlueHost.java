package android.support.v17.leanback.app;

import android.support.v17.leanback.media.SurfaceHolderGlueHost;
import android.view.SurfaceHolder.Callback;

public class VideoFragmentGlueHost
  extends PlaybackFragmentGlueHost
  implements SurfaceHolderGlueHost
{
  private final VideoFragment mFragment;
  
  public VideoFragmentGlueHost(VideoFragment paramVideoFragment)
  {
    super(paramVideoFragment);
    this.mFragment = paramVideoFragment;
  }
  
  public void setSurfaceHolderCallback(SurfaceHolder.Callback paramCallback)
  {
    this.mFragment.setSurfaceHolderCallback(paramCallback);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/app/VideoFragmentGlueHost.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */