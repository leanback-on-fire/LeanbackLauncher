package com.bumptech.glide.load.data;

import android.content.res.AssetManager;
import java.io.IOException;
import java.io.InputStream;

public class StreamAssetPathFetcher extends AssetPathFetcher<InputStream> {
    public StreamAssetPathFetcher(AssetManager assetManager, String assetPath) {
        super(assetManager, assetPath);
    }

    protected InputStream loadResource(AssetManager assetManager, String path) throws IOException {
        return assetManager.open(path);
    }

    protected void close(InputStream data) throws IOException {
        data.close();
    }

    public Class<InputStream> getDataClass() {
        return InputStream.class;
    }
}
