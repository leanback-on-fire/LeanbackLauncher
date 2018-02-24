package com.bumptech.glide.load.data.mediastore;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore.Images.Thumbnails;
import android.provider.MediaStore.Video.Thumbnails;
import android.util.Log;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.data.DataFetcher.DataCallback;
import com.bumptech.glide.load.data.ExifOrientationStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class ThumbFetcher
  implements DataFetcher<InputStream>
{
  private static final String TAG = "MediaStoreThumbFetcher";
  private InputStream inputStream;
  private final Uri mediaStoreImageUri;
  private final ThumbnailStreamOpener opener;
  
  ThumbFetcher(Uri paramUri, ThumbnailStreamOpener paramThumbnailStreamOpener)
  {
    this.mediaStoreImageUri = paramUri;
    this.opener = paramThumbnailStreamOpener;
  }
  
  private static ThumbFetcher build(Context paramContext, Uri paramUri, ThumbnailQuery paramThumbnailQuery)
  {
    return new ThumbFetcher(paramUri, new ThumbnailStreamOpener(paramThumbnailQuery, Glide.get(paramContext).getArrayPool(), paramContext.getContentResolver()));
  }
  
  public static ThumbFetcher buildImageFetcher(Context paramContext, Uri paramUri)
  {
    return build(paramContext, paramUri, new ImageThumbnailQuery(paramContext.getContentResolver()));
  }
  
  public static ThumbFetcher buildVideoFetcher(Context paramContext, Uri paramUri)
  {
    return build(paramContext, paramUri, new VideoThumbnailQuery(paramContext.getContentResolver()));
  }
  
  private InputStream openThumbInputStream()
    throws FileNotFoundException
  {
    InputStream localInputStream = this.opener.open(this.mediaStoreImageUri);
    int i = -1;
    if (localInputStream != null) {
      i = this.opener.getOrientation(this.mediaStoreImageUri);
    }
    Object localObject = localInputStream;
    if (i != -1) {
      localObject = new ExifOrientationStream(localInputStream, i);
    }
    return (InputStream)localObject;
  }
  
  public void cancel() {}
  
  public void cleanup()
  {
    if (this.inputStream != null) {}
    try
    {
      this.inputStream.close();
      return;
    }
    catch (IOException localIOException) {}
  }
  
  public Class<InputStream> getDataClass()
  {
    return InputStream.class;
  }
  
  public DataSource getDataSource()
  {
    return DataSource.LOCAL;
  }
  
  public void loadData(Priority paramPriority, DataFetcher.DataCallback<? super InputStream> paramDataCallback)
  {
    try
    {
      this.inputStream = openThumbInputStream();
      paramDataCallback.onDataReady(this.inputStream);
      return;
    }
    catch (FileNotFoundException paramPriority)
    {
      if (Log.isLoggable("MediaStoreThumbFetcher", 3)) {
        Log.d("MediaStoreThumbFetcher", "Failed to find thumbnail file", paramPriority);
      }
      paramDataCallback.onLoadFailed(paramPriority);
    }
  }
  
  static class ImageThumbnailQuery
    implements ThumbnailQuery
  {
    private static final String[] PATH_PROJECTION = { "_data" };
    private static final String PATH_SELECTION = "kind = 1 AND image_id = ?";
    private final ContentResolver contentResolver;
    
    ImageThumbnailQuery(ContentResolver paramContentResolver)
    {
      this.contentResolver = paramContentResolver;
    }
    
    public Cursor query(Uri paramUri)
    {
      paramUri = paramUri.getLastPathSegment();
      return this.contentResolver.query(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, PATH_PROJECTION, "kind = 1 AND image_id = ?", new String[] { paramUri }, null);
    }
  }
  
  static class VideoThumbnailQuery
    implements ThumbnailQuery
  {
    private static final String[] PATH_PROJECTION = { "_data" };
    private static final String PATH_SELECTION = "kind = 1 AND video_id = ?";
    private final ContentResolver contentResolver;
    
    VideoThumbnailQuery(ContentResolver paramContentResolver)
    {
      this.contentResolver = paramContentResolver;
    }
    
    public Cursor query(Uri paramUri)
    {
      paramUri = paramUri.getLastPathSegment();
      return this.contentResolver.query(MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI, PATH_PROJECTION, "kind = 1 AND video_id = ?", new String[] { paramUri }, null);
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/load/data/mediastore/ThumbFetcher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */