package android.support.v4.media;

import android.media.VolumeProvider;
import android.support.annotation.RequiresApi;

@RequiresApi(21)
class VolumeProviderCompatApi21
{
  public static Object createVolumeProvider(int paramInt1, int paramInt2, int paramInt3, final Delegate paramDelegate)
  {
    new VolumeProvider(paramInt1, paramInt2, paramInt3)
    {
      public void onAdjustVolume(int paramAnonymousInt)
      {
        paramDelegate.onAdjustVolume(paramAnonymousInt);
      }
      
      public void onSetVolumeTo(int paramAnonymousInt)
      {
        paramDelegate.onSetVolumeTo(paramAnonymousInt);
      }
    };
  }
  
  public static void setCurrentVolume(Object paramObject, int paramInt)
  {
    ((VolumeProvider)paramObject).setCurrentVolume(paramInt);
  }
  
  public static abstract interface Delegate
  {
    public abstract void onAdjustVolume(int paramInt);
    
    public abstract void onSetVolumeTo(int paramInt);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v4/media/VolumeProviderCompatApi21.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */