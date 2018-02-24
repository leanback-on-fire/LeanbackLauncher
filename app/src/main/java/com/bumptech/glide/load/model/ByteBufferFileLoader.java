package com.bumptech.glide.load.model;

import android.util.Log;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.data.DataFetcher.DataCallback;
import com.bumptech.glide.load.model.ModelLoader.LoadData;
import com.bumptech.glide.signature.ObjectKey;
import com.bumptech.glide.util.ByteBufferUtil;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

public class ByteBufferFileLoader implements ModelLoader<File, ByteBuffer> {

    private static class ByteBufferFetcher implements DataFetcher<ByteBuffer> {
        private final File file;

        public ByteBufferFetcher(File file) {
            this.file = file;
        }

        public void loadData(Priority priority, DataCallback<? super ByteBuffer> callback) {
            try {
                callback.onDataReady(ByteBufferUtil.fromFile(this.file));
            } catch (IOException e) {
                if (Log.isLoggable("ByteBufferFileLoader", 3)) {
                    Log.d("ByteBufferFileLoader", "Failed to obtain ByteBuffer for file", e);
                }
                callback.onLoadFailed(e);
            }
        }

        public void cleanup() {
        }

        public void cancel() {
        }

        public Class<ByteBuffer> getDataClass() {
            return ByteBuffer.class;
        }

        public DataSource getDataSource() {
            return DataSource.LOCAL;
        }
    }

    public static class Factory implements ModelLoaderFactory<File, ByteBuffer> {
        public ModelLoader<File, ByteBuffer> build(MultiModelLoaderFactory multiFactory) {
            return new ByteBufferFileLoader();
        }
    }

    public LoadData<ByteBuffer> buildLoadData(File file, int width, int height, Options options) {
        return new LoadData(new ObjectKey(file), new ByteBufferFetcher(file));
    }

    public boolean handles(File file) {
        return true;
    }
}
