package com.google.android.exoplayer2.source.hls.playlist;

import android.net.Uri;
import android.os.Handler;
import android.os.SystemClock;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.source.AdaptiveMediaSourceEventListener.EventDispatcher;
import com.google.android.exoplayer2.source.chunk.ChunkedTrackBlacklistUtil;
import com.google.android.exoplayer2.upstream.DataSource.Factory;
import com.google.android.exoplayer2.upstream.Loader;
import com.google.android.exoplayer2.upstream.Loader.Callback;
import com.google.android.exoplayer2.upstream.ParsingLoadable;
import com.google.android.exoplayer2.util.UriUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;

public final class HlsPlaylistTracker
  implements Loader.Callback<ParsingLoadable<HlsPlaylist>>
{
  private static final long PRIMARY_URL_KEEPALIVE_MS = 15000L;
  private final DataSource.Factory dataSourceFactory;
  private final AdaptiveMediaSourceEventListener.EventDispatcher eventDispatcher;
  private final Loader initialPlaylistLoader;
  private final Uri initialPlaylistUri;
  private boolean isLive;
  private final List<PlaylistEventListener> listeners;
  private HlsMasterPlaylist masterPlaylist;
  private final int minRetryCount;
  private final IdentityHashMap<HlsMasterPlaylist.HlsUrl, MediaPlaylistBundle> playlistBundles;
  private final HlsPlaylistParser playlistParser;
  private final Handler playlistRefreshHandler;
  private HlsMasterPlaylist.HlsUrl primaryHlsUrl;
  private final PrimaryPlaylistListener primaryPlaylistListener;
  private HlsMediaPlaylist primaryUrlSnapshot;
  
  public HlsPlaylistTracker(Uri paramUri, DataSource.Factory paramFactory, AdaptiveMediaSourceEventListener.EventDispatcher paramEventDispatcher, int paramInt, PrimaryPlaylistListener paramPrimaryPlaylistListener)
  {
    this.initialPlaylistUri = paramUri;
    this.dataSourceFactory = paramFactory;
    this.eventDispatcher = paramEventDispatcher;
    this.minRetryCount = paramInt;
    this.primaryPlaylistListener = paramPrimaryPlaylistListener;
    this.listeners = new ArrayList();
    this.initialPlaylistLoader = new Loader("HlsPlaylistTracker:MasterPlaylist");
    this.playlistParser = new HlsPlaylistParser();
    this.playlistBundles = new IdentityHashMap();
    this.playlistRefreshHandler = new Handler();
  }
  
  private void createBundles(List<HlsMasterPlaylist.HlsUrl> paramList)
  {
    int j = paramList.size();
    long l = SystemClock.elapsedRealtime();
    int i = 0;
    while (i < j)
    {
      MediaPlaylistBundle localMediaPlaylistBundle = new MediaPlaylistBundle((HlsMasterPlaylist.HlsUrl)paramList.get(i), l);
      this.playlistBundles.put(paramList.get(i), localMediaPlaylistBundle);
      i += 1;
    }
  }
  
  private static HlsMediaPlaylist.Segment getFirstOldOverlappingSegment(HlsMediaPlaylist paramHlsMediaPlaylist1, HlsMediaPlaylist paramHlsMediaPlaylist2)
  {
    int i = paramHlsMediaPlaylist2.mediaSequence - paramHlsMediaPlaylist1.mediaSequence;
    paramHlsMediaPlaylist1 = paramHlsMediaPlaylist1.segments;
    if (i < paramHlsMediaPlaylist1.size()) {
      return (HlsMediaPlaylist.Segment)paramHlsMediaPlaylist1.get(i);
    }
    return null;
  }
  
  private HlsMediaPlaylist getLatestPlaylistSnapshot(HlsMediaPlaylist paramHlsMediaPlaylist1, HlsMediaPlaylist paramHlsMediaPlaylist2)
  {
    if (!paramHlsMediaPlaylist2.isNewerThan(paramHlsMediaPlaylist1))
    {
      HlsMediaPlaylist localHlsMediaPlaylist = paramHlsMediaPlaylist1;
      if (paramHlsMediaPlaylist2.hasEndTag) {
        localHlsMediaPlaylist = paramHlsMediaPlaylist1.copyWithEndTag();
      }
      return localHlsMediaPlaylist;
    }
    return paramHlsMediaPlaylist2.copyWith(getLoadedPlaylistStartTimeUs(paramHlsMediaPlaylist1, paramHlsMediaPlaylist2), getLoadedPlaylistDiscontinuitySequence(paramHlsMediaPlaylist1, paramHlsMediaPlaylist2));
  }
  
  private int getLoadedPlaylistDiscontinuitySequence(HlsMediaPlaylist paramHlsMediaPlaylist1, HlsMediaPlaylist paramHlsMediaPlaylist2)
  {
    int j;
    if (paramHlsMediaPlaylist2.hasDiscontinuitySequence)
    {
      j = paramHlsMediaPlaylist2.discontinuitySequence;
      return j;
    }
    if (this.primaryUrlSnapshot != null) {}
    for (int i = this.primaryUrlSnapshot.discontinuitySequence;; i = 0)
    {
      j = i;
      if (paramHlsMediaPlaylist1 == null) {
        break;
      }
      HlsMediaPlaylist.Segment localSegment = getFirstOldOverlappingSegment(paramHlsMediaPlaylist1, paramHlsMediaPlaylist2);
      j = i;
      if (localSegment == null) {
        break;
      }
      return paramHlsMediaPlaylist1.discontinuitySequence + localSegment.relativeDiscontinuitySequence - ((HlsMediaPlaylist.Segment)paramHlsMediaPlaylist2.segments.get(0)).relativeDiscontinuitySequence;
    }
  }
  
  private long getLoadedPlaylistStartTimeUs(HlsMediaPlaylist paramHlsMediaPlaylist1, HlsMediaPlaylist paramHlsMediaPlaylist2)
  {
    long l2;
    if (paramHlsMediaPlaylist2.hasProgramDateTime) {
      l2 = paramHlsMediaPlaylist2.startTimeUs;
    }
    int i;
    label79:
    do
    {
      return l2;
      if (this.primaryUrlSnapshot != null) {}
      for (long l1 = this.primaryUrlSnapshot.startTimeUs;; l1 = 0L)
      {
        l2 = l1;
        if (paramHlsMediaPlaylist1 == null) {
          break;
        }
        i = paramHlsMediaPlaylist1.segments.size();
        HlsMediaPlaylist.Segment localSegment = getFirstOldOverlappingSegment(paramHlsMediaPlaylist1, paramHlsMediaPlaylist2);
        if (localSegment == null) {
          break label79;
        }
        return paramHlsMediaPlaylist1.startTimeUs + localSegment.relativeStartTimeUs;
      }
      l2 = l1;
    } while (i != paramHlsMediaPlaylist2.mediaSequence - paramHlsMediaPlaylist1.mediaSequence);
    return paramHlsMediaPlaylist1.getEndTimeUs();
  }
  
  private boolean maybeSelectNewPrimaryUrl()
  {
    List localList = this.masterPlaylist.variants;
    int j = localList.size();
    long l = SystemClock.elapsedRealtime();
    int i = 0;
    while (i < j)
    {
      MediaPlaylistBundle localMediaPlaylistBundle = (MediaPlaylistBundle)this.playlistBundles.get(localList.get(i));
      if (l > localMediaPlaylistBundle.blacklistUntilMs)
      {
        this.primaryHlsUrl = localMediaPlaylistBundle.playlistUrl;
        localMediaPlaylistBundle.loadPlaylist();
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  private void maybeSetPrimaryUrl(HlsMasterPlaylist.HlsUrl paramHlsUrl)
  {
    if ((!this.masterPlaylist.variants.contains(paramHlsUrl)) || ((this.primaryUrlSnapshot != null) && (this.primaryUrlSnapshot.hasEndTag))) {}
    while (((MediaPlaylistBundle)this.playlistBundles.get(this.primaryHlsUrl)).lastSnapshotAccessTimeMs - SystemClock.elapsedRealtime() <= 15000L) {
      return;
    }
    this.primaryHlsUrl = paramHlsUrl;
    ((MediaPlaylistBundle)this.playlistBundles.get(this.primaryHlsUrl)).loadPlaylist();
  }
  
  private void notifyPlaylistBlacklisting(HlsMasterPlaylist.HlsUrl paramHlsUrl, long paramLong)
  {
    int j = this.listeners.size();
    int i = 0;
    while (i < j)
    {
      ((PlaylistEventListener)this.listeners.get(i)).onPlaylistBlacklisted(paramHlsUrl, paramLong);
      i += 1;
    }
  }
  
  private boolean onPlaylistUpdated(HlsMasterPlaylist.HlsUrl paramHlsUrl, HlsMediaPlaylist paramHlsMediaPlaylist)
  {
    if (paramHlsUrl == this.primaryHlsUrl) {
      if (this.primaryUrlSnapshot == null) {
        if (paramHlsMediaPlaylist.hasEndTag) {
          break label90;
        }
      }
    }
    label90:
    for (boolean bool = true;; bool = false)
    {
      this.isLive = bool;
      this.primaryUrlSnapshot = paramHlsMediaPlaylist;
      this.primaryPlaylistListener.onPrimaryPlaylistRefreshed(paramHlsMediaPlaylist);
      int j = this.listeners.size();
      int i = 0;
      while (i < j)
      {
        ((PlaylistEventListener)this.listeners.get(i)).onPlaylistChanged();
        i += 1;
      }
    }
    return (paramHlsUrl == this.primaryHlsUrl) && (!paramHlsMediaPlaylist.hasEndTag);
  }
  
  public void addListener(PlaylistEventListener paramPlaylistEventListener)
  {
    this.listeners.add(paramPlaylistEventListener);
  }
  
  public HlsMasterPlaylist getMasterPlaylist()
  {
    return this.masterPlaylist;
  }
  
  public HlsMediaPlaylist getPlaylistSnapshot(HlsMasterPlaylist.HlsUrl paramHlsUrl)
  {
    HlsMediaPlaylist localHlsMediaPlaylist = ((MediaPlaylistBundle)this.playlistBundles.get(paramHlsUrl)).getPlaylistSnapshot();
    if (localHlsMediaPlaylist != null) {
      maybeSetPrimaryUrl(paramHlsUrl);
    }
    return localHlsMediaPlaylist;
  }
  
  public boolean isLive()
  {
    return this.isLive;
  }
  
  public boolean isSnapshotValid(HlsMasterPlaylist.HlsUrl paramHlsUrl)
  {
    return ((MediaPlaylistBundle)this.playlistBundles.get(paramHlsUrl)).isSnapshotValid();
  }
  
  public void maybeThrowPlaylistRefreshError()
    throws IOException
  {
    this.initialPlaylistLoader.maybeThrowError();
    if (this.primaryHlsUrl != null) {
      ((MediaPlaylistBundle)this.playlistBundles.get(this.primaryHlsUrl)).mediaPlaylistLoader.maybeThrowError();
    }
  }
  
  public void onLoadCanceled(ParsingLoadable<HlsPlaylist> paramParsingLoadable, long paramLong1, long paramLong2, boolean paramBoolean)
  {
    this.eventDispatcher.loadCanceled(paramParsingLoadable.dataSpec, 4, paramLong1, paramLong2, paramParsingLoadable.bytesLoaded());
  }
  
  public void onLoadCompleted(ParsingLoadable<HlsPlaylist> paramParsingLoadable, long paramLong1, long paramLong2)
  {
    HlsPlaylist localHlsPlaylist = (HlsPlaylist)paramParsingLoadable.getResult();
    boolean bool = localHlsPlaylist instanceof HlsMediaPlaylist;
    Object localObject;
    if (bool)
    {
      localObject = HlsMasterPlaylist.createSingleVariantMasterPlaylist(localHlsPlaylist.baseUri);
      this.masterPlaylist = ((HlsMasterPlaylist)localObject);
      this.primaryHlsUrl = ((HlsMasterPlaylist.HlsUrl)((HlsMasterPlaylist)localObject).variants.get(0));
      ArrayList localArrayList = new ArrayList();
      localArrayList.addAll(((HlsMasterPlaylist)localObject).variants);
      localArrayList.addAll(((HlsMasterPlaylist)localObject).audios);
      localArrayList.addAll(((HlsMasterPlaylist)localObject).subtitles);
      createBundles(localArrayList);
      localObject = (MediaPlaylistBundle)this.playlistBundles.get(this.primaryHlsUrl);
      if (!bool) {
        break label164;
      }
      ((MediaPlaylistBundle)localObject).processLoadedPlaylist((HlsMediaPlaylist)localHlsPlaylist);
    }
    for (;;)
    {
      this.eventDispatcher.loadCompleted(paramParsingLoadable.dataSpec, 4, paramLong1, paramLong2, paramParsingLoadable.bytesLoaded());
      return;
      localObject = (HlsMasterPlaylist)localHlsPlaylist;
      break;
      label164:
      ((MediaPlaylistBundle)localObject).loadPlaylist();
    }
  }
  
  public int onLoadError(ParsingLoadable<HlsPlaylist> paramParsingLoadable, long paramLong1, long paramLong2, IOException paramIOException)
  {
    boolean bool = paramIOException instanceof ParserException;
    this.eventDispatcher.loadError(paramParsingLoadable.dataSpec, 4, paramLong1, paramLong2, paramParsingLoadable.bytesLoaded(), paramIOException, bool);
    if (bool) {
      return 3;
    }
    return 0;
  }
  
  public void refreshPlaylist(HlsMasterPlaylist.HlsUrl paramHlsUrl)
  {
    ((MediaPlaylistBundle)this.playlistBundles.get(paramHlsUrl)).loadPlaylist();
  }
  
  public void release()
  {
    this.initialPlaylistLoader.release();
    Iterator localIterator = this.playlistBundles.values().iterator();
    while (localIterator.hasNext()) {
      ((MediaPlaylistBundle)localIterator.next()).release();
    }
    this.playlistRefreshHandler.removeCallbacksAndMessages(null);
    this.playlistBundles.clear();
  }
  
  public void removeListener(PlaylistEventListener paramPlaylistEventListener)
  {
    this.listeners.remove(paramPlaylistEventListener);
  }
  
  public void start()
  {
    ParsingLoadable localParsingLoadable = new ParsingLoadable(this.dataSourceFactory.createDataSource(), this.initialPlaylistUri, 4, this.playlistParser);
    this.initialPlaylistLoader.startLoading(localParsingLoadable, this, this.minRetryCount);
  }
  
  private final class MediaPlaylistBundle
    implements Loader.Callback<ParsingLoadable<HlsPlaylist>>, Runnable
  {
    private long blacklistUntilMs;
    private long lastSnapshotAccessTimeMs;
    private long lastSnapshotLoadMs;
    private final ParsingLoadable<HlsPlaylist> mediaPlaylistLoadable;
    private final Loader mediaPlaylistLoader;
    private HlsMediaPlaylist playlistSnapshot;
    private final HlsMasterPlaylist.HlsUrl playlistUrl;
    
    public MediaPlaylistBundle(HlsMasterPlaylist.HlsUrl paramHlsUrl, long paramLong)
    {
      this.playlistUrl = paramHlsUrl;
      this.lastSnapshotAccessTimeMs = paramLong;
      this.mediaPlaylistLoader = new Loader("HlsPlaylistTracker:MediaPlaylist");
      this.mediaPlaylistLoadable = new ParsingLoadable(HlsPlaylistTracker.this.dataSourceFactory.createDataSource(), UriUtil.resolveToUri(HlsPlaylistTracker.this.masterPlaylist.baseUri, paramHlsUrl.url), 4, HlsPlaylistTracker.this.playlistParser);
    }
    
    private void processLoadedPlaylist(HlsMediaPlaylist paramHlsMediaPlaylist)
    {
      HlsMediaPlaylist localHlsMediaPlaylist = this.playlistSnapshot;
      this.lastSnapshotLoadMs = SystemClock.elapsedRealtime();
      this.playlistSnapshot = HlsPlaylistTracker.this.getLatestPlaylistSnapshot(localHlsMediaPlaylist, paramHlsMediaPlaylist);
      long l = -9223372036854775807L;
      if (this.playlistSnapshot != localHlsMediaPlaylist) {
        if (HlsPlaylistTracker.this.onPlaylistUpdated(this.playlistUrl, this.playlistSnapshot)) {
          l = this.playlistSnapshot.targetDurationUs;
        }
      }
      for (;;)
      {
        if (l != -9223372036854775807L) {
          HlsPlaylistTracker.this.playlistRefreshHandler.postDelayed(this, C.usToMs(l));
        }
        return;
        if (!this.playlistSnapshot.hasEndTag) {
          l = this.playlistSnapshot.targetDurationUs / 2L;
        }
      }
    }
    
    public HlsMediaPlaylist getPlaylistSnapshot()
    {
      this.lastSnapshotAccessTimeMs = SystemClock.elapsedRealtime();
      return this.playlistSnapshot;
    }
    
    public boolean isSnapshotValid()
    {
      if (this.playlistSnapshot == null) {}
      long l1;
      long l2;
      do
      {
        return false;
        l1 = SystemClock.elapsedRealtime();
        l2 = Math.max(30000L, C.usToMs(this.playlistSnapshot.durationUs));
      } while ((!this.playlistSnapshot.hasEndTag) && (this.playlistSnapshot.playlistType != 2) && (this.playlistSnapshot.playlistType != 1) && (this.lastSnapshotLoadMs + l2 <= l1));
      return true;
    }
    
    public void loadPlaylist()
    {
      this.blacklistUntilMs = 0L;
      if (!this.mediaPlaylistLoader.isLoading()) {
        this.mediaPlaylistLoader.startLoading(this.mediaPlaylistLoadable, this, HlsPlaylistTracker.this.minRetryCount);
      }
    }
    
    public void onLoadCanceled(ParsingLoadable<HlsPlaylist> paramParsingLoadable, long paramLong1, long paramLong2, boolean paramBoolean)
    {
      HlsPlaylistTracker.this.eventDispatcher.loadCanceled(paramParsingLoadable.dataSpec, 4, paramLong1, paramLong2, paramParsingLoadable.bytesLoaded());
    }
    
    public void onLoadCompleted(ParsingLoadable<HlsPlaylist> paramParsingLoadable, long paramLong1, long paramLong2)
    {
      processLoadedPlaylist((HlsMediaPlaylist)paramParsingLoadable.getResult());
      HlsPlaylistTracker.this.eventDispatcher.loadCompleted(paramParsingLoadable.dataSpec, 4, paramLong1, paramLong2, paramParsingLoadable.bytesLoaded());
    }
    
    public int onLoadError(ParsingLoadable<HlsPlaylist> paramParsingLoadable, long paramLong1, long paramLong2, IOException paramIOException)
    {
      boolean bool = paramIOException instanceof ParserException;
      HlsPlaylistTracker.this.eventDispatcher.loadError(paramParsingLoadable.dataSpec, 4, paramLong1, paramLong2, paramParsingLoadable.bytesLoaded(), paramIOException, bool);
      if (bool) {
        return 3;
      }
      int i = 1;
      if (ChunkedTrackBlacklistUtil.shouldBlacklist(paramIOException))
      {
        this.blacklistUntilMs = (SystemClock.elapsedRealtime() + 60000L);
        HlsPlaylistTracker.this.notifyPlaylistBlacklisting(this.playlistUrl, 60000L);
        if ((HlsPlaylistTracker.this.primaryHlsUrl != this.playlistUrl) || (HlsPlaylistTracker.this.maybeSelectNewPrimaryUrl())) {
          break label110;
        }
      }
      label110:
      for (i = 1; i != 0; i = 0) {
        return 0;
      }
      return 2;
    }
    
    public void release()
    {
      this.mediaPlaylistLoader.release();
    }
    
    public void run()
    {
      loadPlaylist();
    }
  }
  
  public static abstract interface PlaylistEventListener
  {
    public abstract void onPlaylistBlacklisted(HlsMasterPlaylist.HlsUrl paramHlsUrl, long paramLong);
    
    public abstract void onPlaylistChanged();
  }
  
  public static abstract interface PrimaryPlaylistListener
  {
    public abstract void onPrimaryPlaylistRefreshed(HlsMediaPlaylist paramHlsMediaPlaylist);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/source/hls/playlist/HlsPlaylistTracker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */