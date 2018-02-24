package com.bumptech.glide.load.data;

import android.content.ContentResolver;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FileDescriptorLocalUriFetcher extends LocalUriFetcher<ParcelFileDescriptor> {
    public FileDescriptorLocalUriFetcher(ContentResolver contentResolver, Uri uri) {
        super(contentResolver, uri);
    }

    protected ParcelFileDescriptor loadResource(Uri uri, ContentResolver contentResolver) throws FileNotFoundException {
        AssetFileDescriptor assetFileDescriptor = contentResolver.openAssetFileDescriptor(uri, "r");
        if (assetFileDescriptor != null) {
            return assetFileDescriptor.getParcelFileDescriptor();
        }
        String valueOf = String.valueOf(uri);
        throw new FileNotFoundException(new StringBuilder(String.valueOf(valueOf).length() + 28).append("FileDescriptor is null for: ").append(valueOf).toString());
    }

    protected void close(ParcelFileDescriptor data) throws IOException {
        data.close();
    }

    public Class<ParcelFileDescriptor> getDataClass() {
        return ParcelFileDescriptor.class;
    }
}
