package com.google.android.exoplayer2.ui;

import android.widget.TextView;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer.EventListener;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;

public final class DebugTextViewHelper
  implements Runnable, ExoPlayer.EventListener
{
  private static final int REFRESH_INTERVAL_MS = 1000;
  private final SimpleExoPlayer player;
  private boolean started;
  private final TextView textView;
  
  public DebugTextViewHelper(SimpleExoPlayer paramSimpleExoPlayer, TextView paramTextView)
  {
    this.player = paramSimpleExoPlayer;
    this.textView = paramTextView;
  }
  
  private String getAudioString()
  {
    Format localFormat = this.player.getAudioFormat();
    if (localFormat == null) {
      return "";
    }
    return "\n" + localFormat.sampleMimeType + "(id:" + localFormat.id + " hz:" + localFormat.sampleRate + " ch:" + localFormat.channelCount + getDecoderCountersBufferCountString(this.player.getAudioDecoderCounters()) + ")";
  }
  
  private static String getDecoderCountersBufferCountString(DecoderCounters paramDecoderCounters)
  {
    if (paramDecoderCounters == null) {
      return "";
    }
    paramDecoderCounters.ensureUpdated();
    return " rb:" + paramDecoderCounters.renderedOutputBufferCount + " sb:" + paramDecoderCounters.skippedOutputBufferCount + " db:" + paramDecoderCounters.droppedOutputBufferCount + " mcdb:" + paramDecoderCounters.maxConsecutiveDroppedOutputBufferCount;
  }
  
  private String getPlayerStateString()
  {
    String str = "playWhenReady:" + this.player.getPlayWhenReady() + " playbackState:";
    switch (this.player.getPlaybackState())
    {
    default: 
      return str + "unknown";
    case 2: 
      return str + "buffering";
    case 4: 
      return str + "ended";
    case 1: 
      return str + "idle";
    }
    return str + "ready";
  }
  
  private String getPlayerWindowIndexString()
  {
    return " window:" + this.player.getCurrentWindowIndex();
  }
  
  private String getVideoString()
  {
    Format localFormat = this.player.getVideoFormat();
    if (localFormat == null) {
      return "";
    }
    return "\n" + localFormat.sampleMimeType + "(id:" + localFormat.id + " r:" + localFormat.width + "x" + localFormat.height + getDecoderCountersBufferCountString(this.player.getVideoDecoderCounters()) + ")";
  }
  
  private void updateAndPost()
  {
    this.textView.setText(getPlayerStateString() + getPlayerWindowIndexString() + getVideoString() + getAudioString());
    this.textView.removeCallbacks(this);
    this.textView.postDelayed(this, 1000L);
  }
  
  public void onLoadingChanged(boolean paramBoolean) {}
  
  public void onPlayerError(ExoPlaybackException paramExoPlaybackException) {}
  
  public void onPlayerStateChanged(boolean paramBoolean, int paramInt)
  {
    updateAndPost();
  }
  
  public void onPositionDiscontinuity()
  {
    updateAndPost();
  }
  
  public void onTimelineChanged(Timeline paramTimeline, Object paramObject) {}
  
  public void onTracksChanged(TrackGroupArray paramTrackGroupArray, TrackSelectionArray paramTrackSelectionArray) {}
  
  public void run()
  {
    updateAndPost();
  }
  
  public void start()
  {
    if (this.started) {
      return;
    }
    this.started = true;
    this.player.addListener(this);
    updateAndPost();
  }
  
  public void stop()
  {
    if (!this.started) {
      return;
    }
    this.started = false;
    this.player.removeListener(this);
    this.textView.removeCallbacks(this);
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/ui/DebugTextViewHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */