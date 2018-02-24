package android.support.v17.leanback.widget;

public abstract interface PlaybackSeekUi
{
  public abstract void setPlaybackSeekUiClient(Client paramClient);
  
  public static class Client
  {
    public PlaybackSeekDataProvider getPlaybackSeekDataProvider()
    {
      return null;
    }
    
    public boolean isSeekEnabled()
    {
      return false;
    }
    
    public void onSeekFinished(boolean paramBoolean) {}
    
    public void onSeekPositionChanged(long paramLong) {}
    
    public void onSeekStarted() {}
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/PlaybackSeekUi.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */