package com.bumptech.glide.load.data.mediastore;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore.Images.Thumbnails;
import android.provider.MediaStore.Video;
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

public class ThumbFetcher implements DataFetcher<InputStream> {
    private InputStream inputStream;
    private final Uri mediaStoreImageUri;
    private final ThumbnailStreamOpener opener;

    static class ImageThumbnailQuery implements ThumbnailQuery {
        private static final String[] PATH_PROJECTION = new String[]{"_data"};
        private final ContentResolver contentResolver;

        ImageThumbnailQuery(ContentResolver contentResolver) {
            this.contentResolver = contentResolver;
        }

        public Cursor query(Uri uri) {
            String imageId = uri.getLastPathSegment();
            return this.contentResolver.query(Thumbnails.EXTERNAL_CONTENT_URI, PATH_PROJECTION, "kind = 1 AND image_id = ?", new String[]{imageId}, null);
        }
    }

    static class VideoThumbnailQuery implements ThumbnailQuery {
        private static final String[] PATH_PROJECTION = new String[]{"_data"};
        private final ContentResolver contentResolver;

        VideoThumbnailQuery(ContentResolver contentResolver) {
            this.contentResolver = contentResolver;
        }

        public Cursor query(Uri uri) {
            String videoId = uri.getLastPathSegment();
            return this.contentResolver.query(Video.Thumbnails.EXTERNAL_CONTENT_URI, PATH_PROJECTION, "kind = 1 AND video_id = ?", new String[]{videoId}, null);
        }
    }

    public static ThumbFetcher buildImageFetcher(Context context, Uri uri) {
        return build(context, uri, new ImageThumbnailQuery(context.getContentResolver()));
    }

    public static ThumbFetcher buildVideoFetcher(Context context, Uri uri) {
        return build(context, uri, new VideoThumbnailQuery(context.getContentResolver()));
    }

    private static ThumbFetcher build(Context context, Uri uri, ThumbnailQuery query) {
        return new ThumbFetcher(uri, new ThumbnailStreamOpener(query, Glide.get(context).getArrayPool(), context.getContentResolver()));
    }

    ThumbFetcher(Uri mediaStoreImageUri, ThumbnailStreamOpener opener) {
        this.mediaStoreImageUri = mediaStoreImageUri;
        this.opener = opener;
    }

    public void loadData(Priority priority, DataCallback<? super InputStream> callback) {
        try {
            this.inputStream = openThumbInputStream();
            callback.onDataReady(this.inputStream);
        } catch (FileNotFoundException e) {
            if (Log.isLoggable("MediaStoreThumbFetcher", 3)) {
                Log.d("MediaStoreThumbFetcher", "Failed to find thumbnail file", e);
            }
            callback.onLoadFailed(e);
        }
    }

    private InputStream openThumbInputStream() throws FileNotFoundException {
        InputStream result = this.opener.open(this.mediaStoreImageUri);
        int orientation = -1;
        if (result != null) {
            orientation = this.opener.getOrientation(this.mediaStoreImageUri);
        }
        if (orientation != -1) {
            return new ExifOrientationStream(result, orientation);
        }
        return result;
    }

    public void cleanup() {
        if (this.inputStream != null) {
            try {
                this.inputStream.close();
            } catch (IOException e) {
            }
        }
    }

    public void cancel() {
    }

    public Class<InputStream> getDataClass() {
        return InputStream.class;
    }

    public DataSource getDataSource() {
        return DataSource.LOCAL;
    }
}
