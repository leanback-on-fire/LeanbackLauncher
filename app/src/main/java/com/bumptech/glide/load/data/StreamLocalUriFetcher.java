package com.bumptech.glide.load.data;

import android.content.ContentResolver;
import android.net.Uri;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class StreamLocalUriFetcher extends LocalUriFetcher<InputStream> {
    public StreamLocalUriFetcher(ContentResolver resolver, Uri uri) {
        super(resolver, uri);
    }

    protected InputStream loadResource(Uri uri, ContentResolver contentResolver) throws FileNotFoundException {
        InputStream inputStream = contentResolver.openInputStream(uri);
        if (inputStream != null) {
            return inputStream;
        }
        String valueOf = String.valueOf(uri);
        throw new FileNotFoundException(new StringBuilder(String.valueOf(valueOf).length() + 25).append("InputStream is null for :").append(valueOf).toString());
    }

    protected void close(InputStream data) throws IOException {
        data.close();
    }

    public Class<InputStream> getDataClass() {
        return InputStream.class;
    }
}
