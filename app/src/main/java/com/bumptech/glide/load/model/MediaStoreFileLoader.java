package com.bumptech.glide.load.model;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.data.DataFetcher.DataCallback;
import com.bumptech.glide.load.data.mediastore.MediaStoreUtil;
import com.bumptech.glide.load.model.ModelLoader.LoadData;
import com.bumptech.glide.signature.ObjectKey;
import java.io.File;
import java.io.FileNotFoundException;

public final class MediaStoreFileLoader implements ModelLoader<Uri, File> {
    private final Context context;

    public static final class Factory implements ModelLoaderFactory<Uri, File> {
        private final Context context;

        public Factory(Context context) {
            this.context = context;
        }

        public ModelLoader<Uri, File> build(MultiModelLoaderFactory multiFactory) {
            return new MediaStoreFileLoader(this.context);
        }
    }

    private static class FilePathFetcher implements DataFetcher<File> {
        private static final String[] PROJECTION = new String[]{"_data"};
        private final Context context;
        private final Uri uri;

        FilePathFetcher(Context context, Uri uri) {
            this.context = context;
            this.uri = uri;
        }

        public void loadData(Priority priority, DataCallback<? super File> callback) {
            Cursor cursor = this.context.getContentResolver().query(this.uri, PROJECTION, null, null, null);
            String filePath = null;
            if (cursor != null) {
                try {
                    if (cursor.moveToFirst()) {
                        filePath = cursor.getString(cursor.getColumnIndexOrThrow("_data"));
                    }
                    cursor.close();
                } catch (Throwable th) {
                    cursor.close();
                }
            }
            if (TextUtils.isEmpty(filePath)) {
                String valueOf = String.valueOf(this.uri);
                callback.onLoadFailed(new FileNotFoundException(new StringBuilder(String.valueOf(valueOf).length() + 30).append("Failed to find file path for: ").append(valueOf).toString()));
                return;
            }
            callback.onDataReady(new File(filePath));
        }

        public void cleanup() {
        }

        public void cancel() {
        }

        public Class<File> getDataClass() {
            return File.class;
        }

        public DataSource getDataSource() {
            return DataSource.LOCAL;
        }
    }

    MediaStoreFileLoader(Context context) {
        this.context = context;
    }

    public LoadData<File> buildLoadData(Uri uri, int width, int height, Options options) {
        return new LoadData(new ObjectKey(uri), new FilePathFetcher(this.context, uri));
    }

    public boolean handles(Uri uri) {
        return MediaStoreUtil.isMediaStoreUri(uri);
    }
}
