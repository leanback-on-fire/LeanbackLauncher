package com.bumptech.glide.load.model;

import android.content.res.AssetManager;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.data.FileDescriptorAssetPathFetcher;
import com.bumptech.glide.load.data.StreamAssetPathFetcher;
import com.bumptech.glide.load.model.ModelLoader.LoadData;
import com.bumptech.glide.signature.ObjectKey;
import java.io.InputStream;

public class AssetUriLoader<Data> implements ModelLoader<Uri, Data> {
    private static final int ASSET_PREFIX_LENGTH = "file:///android_asset/".length();
    private final AssetManager assetManager;
    private final AssetFetcherFactory<Data> factory;

    public interface AssetFetcherFactory<Data> {
        DataFetcher<Data> buildFetcher(AssetManager assetManager, String str);
    }

    public static class FileDescriptorFactory implements AssetFetcherFactory<ParcelFileDescriptor>, ModelLoaderFactory<Uri, ParcelFileDescriptor> {
        private final AssetManager assetManager;

        public FileDescriptorFactory(AssetManager assetManager) {
            this.assetManager = assetManager;
        }

        public ModelLoader<Uri, ParcelFileDescriptor> build(MultiModelLoaderFactory multiFactory) {
            return new AssetUriLoader(this.assetManager, this);
        }

        public DataFetcher<ParcelFileDescriptor> buildFetcher(AssetManager assetManager, String assetPath) {
            return new FileDescriptorAssetPathFetcher(assetManager, assetPath);
        }
    }

    public static class StreamFactory implements AssetFetcherFactory<InputStream>, ModelLoaderFactory<Uri, InputStream> {
        private final AssetManager assetManager;

        public StreamFactory(AssetManager assetManager) {
            this.assetManager = assetManager;
        }

        public ModelLoader<Uri, InputStream> build(MultiModelLoaderFactory multiFactory) {
            return new AssetUriLoader(this.assetManager, this);
        }

        public DataFetcher<InputStream> buildFetcher(AssetManager assetManager, String assetPath) {
            return new StreamAssetPathFetcher(assetManager, assetPath);
        }
    }

    public AssetUriLoader(AssetManager assetManager, AssetFetcherFactory<Data> factory) {
        this.assetManager = assetManager;
        this.factory = factory;
    }

    public LoadData<Data> buildLoadData(Uri model, int width, int height, Options options) {
        return new LoadData(new ObjectKey(model), this.factory.buildFetcher(this.assetManager, model.toString().substring(ASSET_PREFIX_LENGTH)));
    }

    public boolean handles(Uri model) {
        if ("file".equals(model.getScheme()) && !model.getPathSegments().isEmpty() && "android_asset".equals(model.getPathSegments().get(0))) {
            return true;
        }
        return false;
    }
}
