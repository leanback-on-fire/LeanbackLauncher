package com.google.android.exoplayer2.source.hls;

import android.net.Uri;
import android.os.SystemClock;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.source.BehindLiveWindowException;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.chunk.Chunk;
import com.google.android.exoplayer2.source.chunk.ChunkedTrackBlacklistUtil;
import com.google.android.exoplayer2.source.chunk.DataChunk;
import com.google.android.exoplayer2.source.hls.playlist.HlsMasterPlaylist.HlsUrl;
import com.google.android.exoplayer2.source.hls.playlist.HlsMediaPlaylist;
import com.google.android.exoplayer2.source.hls.playlist.HlsMediaPlaylist.Segment;
import com.google.android.exoplayer2.source.hls.playlist.HlsPlaylistTracker;
import com.google.android.exoplayer2.trackselection.BaseTrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.util.TimestampAdjuster;
import com.google.android.exoplayer2.util.UriUtil;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

class HlsChunkSource
{
  private final DataSource dataSource;
  private byte[] encryptionIv;
  private String encryptionIvString;
  private byte[] encryptionKey;
  private Uri encryptionKeyUri;
  private IOException fatalError;
  private boolean isTimestampMaster;
  private final HlsPlaylistTracker playlistTracker;
  private byte[] scratchSpace;
  private final TimestampAdjusterProvider timestampAdjusterProvider;
  private final TrackGroup trackGroup;
  private TrackSelection trackSelection;
  private final HlsMasterPlaylist.HlsUrl[] variants;
  
  public HlsChunkSource(HlsPlaylistTracker paramHlsPlaylistTracker, HlsMasterPlaylist.HlsUrl[] paramArrayOfHlsUrl, DataSource paramDataSource, TimestampAdjusterProvider paramTimestampAdjusterProvider)
  {
    this.playlistTracker = paramHlsPlaylistTracker;
    this.variants = paramArrayOfHlsUrl;
    this.dataSource = paramDataSource;
    this.timestampAdjusterProvider = paramTimestampAdjusterProvider;
    paramHlsPlaylistTracker = new Format[paramArrayOfHlsUrl.length];
    paramDataSource = new int[paramArrayOfHlsUrl.length];
    int i = 0;
    while (i < paramArrayOfHlsUrl.length)
    {
      paramHlsPlaylistTracker[i] = paramArrayOfHlsUrl[i].format;
      paramDataSource[i] = i;
      i += 1;
    }
    this.trackGroup = new TrackGroup(paramHlsPlaylistTracker);
    this.trackSelection = new InitializationTrackSelection(this.trackGroup, paramDataSource);
  }
  
  private void clearEncryptionData()
  {
    this.encryptionKeyUri = null;
    this.encryptionKey = null;
    this.encryptionIvString = null;
    this.encryptionIv = null;
  }
  
  private EncryptionKeyChunk newEncryptionKeyChunk(Uri paramUri, String paramString, int paramInt1, int paramInt2, Object paramObject)
  {
    paramUri = new DataSpec(paramUri, 0L, -1L, null, 1);
    return new EncryptionKeyChunk(this.dataSource, paramUri, this.variants[paramInt1].format, paramInt2, paramObject, this.scratchSpace, paramString);
  }
  
  private void setEncryptionData(Uri paramUri, String paramString, byte[] paramArrayOfByte)
  {
    Object localObject;
    byte[] arrayOfByte;
    if (paramString.toLowerCase(Locale.getDefault()).startsWith("0x"))
    {
      localObject = paramString.substring(2);
      localObject = new BigInteger((String)localObject, 16).toByteArray();
      arrayOfByte = new byte[16];
      if (localObject.length <= 16) {
        break label113;
      }
    }
    label113:
    for (int i = localObject.length - 16;; i = 0)
    {
      System.arraycopy(localObject, i, arrayOfByte, arrayOfByte.length - localObject.length + i, localObject.length - i);
      this.encryptionKeyUri = paramUri;
      this.encryptionKey = paramArrayOfByte;
      this.encryptionIvString = paramString;
      this.encryptionIv = arrayOfByte;
      return;
      localObject = paramString;
      break;
    }
  }
  
  public void getNextChunk(HlsMediaChunk paramHlsMediaChunk, long paramLong, HlsChunkHolder paramHlsChunkHolder)
  {
    long l;
    label14:
    int m;
    if (paramHlsMediaChunk == null)
    {
      j = -1;
      if (paramHlsMediaChunk != null) {
        break label91;
      }
      l = 0L;
      this.trackSelection.updateSelectedTrack(l);
      m = this.trackSelection.getSelectedIndexInTrackGroup();
      if (j == m) {
        break label106;
      }
    }
    Object localObject3;
    label91:
    label106:
    for (int i = 1;; i = 0)
    {
      localObject3 = this.variants[m];
      if (this.playlistTracker.isSnapshotValid((HlsMasterPlaylist.HlsUrl)localObject3)) {
        break label112;
      }
      paramHlsChunkHolder.playlist = ((HlsMasterPlaylist.HlsUrl)localObject3);
      return;
      j = this.trackGroup.indexOf(paramHlsMediaChunk.trackFormat);
      break;
      l = Math.max(0L, paramHlsMediaChunk.startTimeUs - paramLong);
      break label14;
    }
    label112:
    Object localObject4 = this.playlistTracker.getPlaylistSnapshot((HlsMasterPlaylist.HlsUrl)localObject3);
    Object localObject2;
    Object localObject1;
    int k;
    if ((paramHlsMediaChunk == null) || (i != 0)) {
      if (paramHlsMediaChunk == null)
      {
        if ((((HlsMediaPlaylist)localObject4).hasEndTag) || (paramLong <= ((HlsMediaPlaylist)localObject4).getEndTimeUs())) {
          break label214;
        }
        i = ((HlsMediaPlaylist)localObject4).mediaSequence + ((HlsMediaPlaylist)localObject4).segments.size();
        localObject2 = localObject4;
        localObject1 = localObject3;
        k = m;
      }
    }
    for (;;)
    {
      if (i >= ((HlsMediaPlaylist)localObject2).mediaSequence) {
        break label374;
      }
      this.fatalError = new BehindLiveWindowException();
      return;
      paramLong = paramHlsMediaChunk.startTimeUs;
      break;
      label214:
      localObject1 = ((HlsMediaPlaylist)localObject4).segments;
      l = ((HlsMediaPlaylist)localObject4).startTimeUs;
      if ((!this.playlistTracker.isLive()) || (paramHlsMediaChunk == null)) {}
      for (boolean bool = true;; bool = false)
      {
        int n = Util.binarySearchFloor((List)localObject1, Long.valueOf(paramLong - l), true, bool) + ((HlsMediaPlaylist)localObject4).mediaSequence;
        k = m;
        localObject1 = localObject3;
        i = n;
        localObject2 = localObject4;
        if (n >= ((HlsMediaPlaylist)localObject4).mediaSequence) {
          break;
        }
        k = m;
        localObject1 = localObject3;
        i = n;
        localObject2 = localObject4;
        if (paramHlsMediaChunk == null) {
          break;
        }
        localObject1 = this.variants[j];
        localObject2 = this.playlistTracker.getPlaylistSnapshot((HlsMasterPlaylist.HlsUrl)localObject1);
        i = paramHlsMediaChunk.getNextChunkIndex();
        k = j;
        break;
      }
      i = paramHlsMediaChunk.getNextChunkIndex();
      k = m;
      localObject1 = localObject3;
      localObject2 = localObject4;
    }
    label374:
    int j = i - ((HlsMediaPlaylist)localObject2).mediaSequence;
    if (j >= ((HlsMediaPlaylist)localObject2).segments.size())
    {
      if (((HlsMediaPlaylist)localObject2).hasEndTag)
      {
        paramHlsChunkHolder.endOfStream = true;
        return;
      }
      paramHlsChunkHolder.playlist = ((HlsMasterPlaylist.HlsUrl)localObject1);
      return;
    }
    localObject4 = (HlsMediaPlaylist.Segment)((HlsMediaPlaylist)localObject2).segments.get(j);
    if (((HlsMediaPlaylist.Segment)localObject4).isEncrypted)
    {
      localObject3 = UriUtil.resolveToUri(((HlsMediaPlaylist)localObject2).baseUri, ((HlsMediaPlaylist.Segment)localObject4).encryptionKeyUri);
      if (!((Uri)localObject3).equals(this.encryptionKeyUri))
      {
        paramHlsChunkHolder.chunk = newEncryptionKeyChunk((Uri)localObject3, ((HlsMediaPlaylist.Segment)localObject4).encryptionIV, k, this.trackSelection.getSelectionReason(), this.trackSelection.getSelectionData());
        return;
      }
      if (!Util.areEqual(((HlsMediaPlaylist.Segment)localObject4).encryptionIV, this.encryptionIvString)) {
        setEncryptionData((Uri)localObject3, ((HlsMediaPlaylist.Segment)localObject4).encryptionIV, this.encryptionKey);
      }
    }
    for (;;)
    {
      localObject3 = null;
      Object localObject5 = ((HlsMediaPlaylist)localObject2).initializationSegment;
      if (localObject5 != null) {
        localObject3 = new DataSpec(UriUtil.resolveToUri(((HlsMediaPlaylist)localObject2).baseUri, ((HlsMediaPlaylist.Segment)localObject5).url), ((HlsMediaPlaylist.Segment)localObject5).byterangeOffset, ((HlsMediaPlaylist.Segment)localObject5).byterangeLength, null);
      }
      paramLong = ((HlsMediaPlaylist)localObject2).startTimeUs + ((HlsMediaPlaylist.Segment)localObject4).relativeStartTimeUs;
      j = ((HlsMediaPlaylist)localObject2).discontinuitySequence + ((HlsMediaPlaylist.Segment)localObject4).relativeDiscontinuitySequence;
      localObject5 = this.timestampAdjusterProvider.getAdjuster(j, paramLong);
      localObject2 = new DataSpec(UriUtil.resolveToUri(((HlsMediaPlaylist)localObject2).baseUri, ((HlsMediaPlaylist.Segment)localObject4).url), ((HlsMediaPlaylist.Segment)localObject4).byterangeOffset, ((HlsMediaPlaylist.Segment)localObject4).byterangeLength, null);
      paramHlsChunkHolder.chunk = new HlsMediaChunk(this.dataSource, (DataSpec)localObject2, (DataSpec)localObject3, (HlsMasterPlaylist.HlsUrl)localObject1, this.trackSelection.getSelectionReason(), this.trackSelection.getSelectionData(), paramLong, ((HlsMediaPlaylist.Segment)localObject4).durationUs + paramLong, i, j, this.isTimestampMaster, (TimestampAdjuster)localObject5, paramHlsMediaChunk, this.encryptionKey, this.encryptionIv);
      return;
      clearEncryptionData();
    }
  }
  
  public TrackGroup getTrackGroup()
  {
    return this.trackGroup;
  }
  
  public void maybeThrowError()
    throws IOException
  {
    if (this.fatalError != null) {
      throw this.fatalError;
    }
  }
  
  public void onChunkLoadCompleted(Chunk paramChunk)
  {
    if ((paramChunk instanceof EncryptionKeyChunk))
    {
      paramChunk = (EncryptionKeyChunk)paramChunk;
      this.scratchSpace = paramChunk.getDataHolder();
      setEncryptionData(paramChunk.dataSpec.uri, paramChunk.iv, paramChunk.getResult());
    }
  }
  
  public boolean onChunkLoadError(Chunk paramChunk, boolean paramBoolean, IOException paramIOException)
  {
    return (paramBoolean) && (ChunkedTrackBlacklistUtil.maybeBlacklistTrack(this.trackSelection, this.trackSelection.indexOf(this.trackGroup.indexOf(paramChunk.trackFormat)), paramIOException));
  }
  
  public void onPlaylistBlacklisted(HlsMasterPlaylist.HlsUrl paramHlsUrl, long paramLong)
  {
    int i = this.trackGroup.indexOf(paramHlsUrl.format);
    if (i != -1)
    {
      i = this.trackSelection.indexOf(i);
      if (i != -1) {
        this.trackSelection.blacklist(i, paramLong);
      }
    }
  }
  
  public void reset()
  {
    this.fatalError = null;
  }
  
  public void selectTracks(TrackSelection paramTrackSelection)
  {
    this.trackSelection = paramTrackSelection;
  }
  
  public void setIsTimestampMaster(boolean paramBoolean)
  {
    this.isTimestampMaster = paramBoolean;
  }
  
  private static final class EncryptionKeyChunk
    extends DataChunk
  {
    public final String iv;
    private byte[] result;
    
    public EncryptionKeyChunk(DataSource paramDataSource, DataSpec paramDataSpec, Format paramFormat, int paramInt, Object paramObject, byte[] paramArrayOfByte, String paramString)
    {
      super(paramDataSpec, 3, paramFormat, paramInt, paramObject, paramArrayOfByte);
      this.iv = paramString;
    }
    
    protected void consume(byte[] paramArrayOfByte, int paramInt)
      throws IOException
    {
      this.result = Arrays.copyOf(paramArrayOfByte, paramInt);
    }
    
    public byte[] getResult()
    {
      return this.result;
    }
  }
  
  public static final class HlsChunkHolder
  {
    public Chunk chunk;
    public boolean endOfStream;
    public HlsMasterPlaylist.HlsUrl playlist;
    
    public HlsChunkHolder()
    {
      clear();
    }
    
    public void clear()
    {
      this.chunk = null;
      this.endOfStream = false;
      this.playlist = null;
    }
  }
  
  private static final class InitializationTrackSelection
    extends BaseTrackSelection
  {
    private int selectedIndex = indexOf(paramTrackGroup.getFormat(0));
    
    public InitializationTrackSelection(TrackGroup paramTrackGroup, int[] paramArrayOfInt)
    {
      super(paramArrayOfInt);
    }
    
    public int getSelectedIndex()
    {
      return this.selectedIndex;
    }
    
    public Object getSelectionData()
    {
      return null;
    }
    
    public int getSelectionReason()
    {
      return 0;
    }
    
    public void updateSelectedTrack(long paramLong)
    {
      paramLong = SystemClock.elapsedRealtime();
      if (!isBlacklisted(this.selectedIndex, paramLong)) {
        return;
      }
      int i = this.length - 1;
      while (i >= 0)
      {
        if (!isBlacklisted(i, paramLong))
        {
          this.selectedIndex = i;
          return;
        }
        i -= 1;
      }
      throw new IllegalStateException();
    }
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/source/hls/HlsChunkSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */