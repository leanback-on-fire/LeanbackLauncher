package com.bumptech.glide.load.model;

import android.os.ParcelFileDescriptor;
import android.util.Log;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.data.DataFetcher.DataCallback;
import com.bumptech.glide.load.model.ModelLoader.LoadData;
import com.bumptech.glide.signature.ObjectKey;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class FileLoader<Data> implements ModelLoader<File, Data> {
    private final FileOpener<Data> fileOpener;

    public static class Factory<Data> implements ModelLoaderFactory<File, Data> {
        private final FileOpener<Data> opener;

        public Factory(FileOpener<Data> opener) {
            this.opener = opener;
        }

        public final ModelLoader<File, Data> build(MultiModelLoaderFactory multiFactory) {
            return new FileLoader(this.opener);
        }
    }

    public interface FileOpener<Data> {
        void close(Data data) throws IOException;

        Class<Data> getDataClass();

        Data open(File file) throws FileNotFoundException;
    }

    public static class FileDescriptorFactory extends Factory<ParcelFileDescriptor> {
        public FileDescriptorFactory() {
            super(new FileOpener<ParcelFileDescriptor>() {
                public ParcelFileDescriptor open(File file) throws FileNotFoundException {
                    return ParcelFileDescriptor.open(file, 268435456);
                }

                public void close(ParcelFileDescriptor parcelFileDescriptor) throws IOException {
                    parcelFileDescriptor.close();
                }

                public Class<ParcelFileDescriptor> getDataClass() {
                    return ParcelFileDescriptor.class;
                }
            });
        }
    }

    private static class FileFetcher<Data> implements DataFetcher<Data> {
        private Data data;
        private final File file;
        private final FileOpener<Data> opener;

        public FileFetcher(File file, FileOpener<Data> opener) {
            this.file = file;
            this.opener = opener;
        }

        public void loadData(Priority priority, DataCallback<? super Data> callback) {
            try {
                this.data = this.opener.open(this.file);
                callback.onDataReady(this.data);
            } catch (FileNotFoundException e) {
                if (Log.isLoggable("FileLoader", 3)) {
                    Log.d("FileLoader", "Failed to open file", e);
                }
                callback.onLoadFailed(e);
            }
        }

        public void cleanup() {
            if (this.data != null) {
                try {
                    this.opener.close(this.data);
                } catch (IOException e) {
                }
            }
        }

        public void cancel() {
        }

        public Class<Data> getDataClass() {
            return this.opener.getDataClass();
        }

        public DataSource getDataSource() {
            return DataSource.LOCAL;
        }
    }

    public static class StreamFactory extends Factory<InputStream> {
        public StreamFactory() {
            super(new FileOpener<InputStream>() {
                public InputStream open(File file) throws FileNotFoundException {
                    return new FileInputStream(file);
                }

                public void close(InputStream inputStream) throws IOException {
                    inputStream.close();
                }

                public Class<InputStream> getDataClass() {
                    return InputStream.class;
                }
            });
        }
    }

    public FileLoader(FileOpener<Data> fileOpener) {
        this.fileOpener = fileOpener;
    }

    public LoadData<Data> buildLoadData(File model, int width, int height, Options options) {
        return new LoadData(new ObjectKey(model), new FileFetcher(model, this.fileOpener));
    }

    public boolean handles(File model) {
        return true;
    }
}
