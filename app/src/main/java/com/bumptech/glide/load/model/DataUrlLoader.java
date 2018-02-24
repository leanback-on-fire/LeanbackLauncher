package com.bumptech.glide.load.model;

import android.util.Base64;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.data.DataFetcher.DataCallback;
import com.bumptech.glide.load.model.ModelLoader.LoadData;
import com.bumptech.glide.signature.ObjectKey;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public final class DataUrlLoader<Data> implements ModelLoader<String, Data> {
    private final DataDecoder<Data> dataDecoder;

    public interface DataDecoder<Data> {
        void close(Data data) throws IOException;

        Data decode(String str) throws IllegalArgumentException;

        Class<Data> getDataClass();
    }

    private static final class DataUriFetcher<Data> implements DataFetcher<Data> {
        private Data data;
        private final String dataUri;
        private final DataDecoder<Data> reader;

        public DataUriFetcher(String dataUri, DataDecoder<Data> reader) {
            this.dataUri = dataUri;
            this.reader = reader;
        }

        public void loadData(Priority priority, DataCallback<? super Data> callback) {
            try {
                this.data = this.reader.decode(this.dataUri);
                callback.onDataReady(this.data);
            } catch (IllegalArgumentException e) {
                callback.onLoadFailed(e);
            }
        }

        public void cleanup() {
            try {
                this.reader.close(this.data);
            } catch (IOException e) {
            }
        }

        public void cancel() {
        }

        public Class<Data> getDataClass() {
            return this.reader.getDataClass();
        }

        public DataSource getDataSource() {
            return DataSource.LOCAL;
        }
    }

    public static final class StreamFactory implements ModelLoaderFactory<String, InputStream> {
        private final DataDecoder<InputStream> opener = new DataDecoder<InputStream>() {
            public InputStream decode(String url) {
                if (url.startsWith("data:image")) {
                    int commaIndex = url.indexOf(44);
                    if (commaIndex == -1) {
                        throw new IllegalArgumentException("Missing comma in data URL.");
                    } else if (url.substring(0, commaIndex).endsWith(";base64")) {
                        return new ByteArrayInputStream(Base64.decode(url.substring(commaIndex + 1), 0));
                    } else {
                        throw new IllegalArgumentException("Not a base64 image data URL.");
                    }
                }
                throw new IllegalArgumentException("Not a valid image data URL.");
            }

            public void close(InputStream inputStream) throws IOException {
                inputStream.close();
            }

            public Class<InputStream> getDataClass() {
                return InputStream.class;
            }
        };

        public final ModelLoader<String, InputStream> build(MultiModelLoaderFactory multiFactory) {
            return new DataUrlLoader(this.opener);
        }
    }

    public DataUrlLoader(DataDecoder<Data> dataDecoder) {
        this.dataDecoder = dataDecoder;
    }

    public LoadData<Data> buildLoadData(String model, int width, int height, Options options) {
        return new LoadData(new ObjectKey(model), new DataUriFetcher(model, this.dataDecoder));
    }

    public boolean handles(String url) {
        return url.startsWith("data:image");
    }
}
