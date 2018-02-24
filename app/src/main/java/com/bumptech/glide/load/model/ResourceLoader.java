package com.bumptech.glide.load.model;

import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.ModelLoader.LoadData;
import java.io.InputStream;

public class ResourceLoader<Data> implements ModelLoader<Integer, Data> {
    private final Resources resources;
    private final ModelLoader<Uri, Data> uriLoader;

    public static class FileDescriptorFactory implements ModelLoaderFactory<Integer, ParcelFileDescriptor> {
        private final Resources resources;

        public FileDescriptorFactory(Resources resources) {
            this.resources = resources;
        }

        public ModelLoader<Integer, ParcelFileDescriptor> build(MultiModelLoaderFactory multiFactory) {
            return new ResourceLoader(this.resources, multiFactory.build(Uri.class, ParcelFileDescriptor.class));
        }
    }

    public static class StreamFactory implements ModelLoaderFactory<Integer, InputStream> {
        private final Resources resources;

        public StreamFactory(Resources resources) {
            this.resources = resources;
        }

        public ModelLoader<Integer, InputStream> build(MultiModelLoaderFactory multiFactory) {
            return new ResourceLoader(this.resources, multiFactory.build(Uri.class, InputStream.class));
        }
    }

    public ResourceLoader(Resources resources, ModelLoader<Uri, Data> uriLoader) {
        this.resources = resources;
        this.uriLoader = uriLoader;
    }

    public LoadData<Data> buildLoadData(Integer model, int width, int height, Options options) {
        Uri uri = getResourceUri(model);
        return uri == null ? null : this.uriLoader.buildLoadData(uri, width, height, options);
    }

    private Uri getResourceUri(Integer model) {
        String valueOf;
        try {
            String valueOf2 = String.valueOf("android.resource://");
            valueOf = String.valueOf(this.resources.getResourcePackageName(model.intValue()));
            String valueOf3 = String.valueOf(this.resources.getResourceTypeName(model.intValue()));
            String valueOf4 = String.valueOf(this.resources.getResourceEntryName(model.intValue()));
            return Uri.parse(new StringBuilder((((String.valueOf(valueOf2).length() + 2) + String.valueOf(valueOf).length()) + String.valueOf(valueOf3).length()) + String.valueOf(valueOf4).length()).append(valueOf2).append(valueOf).append("/").append(valueOf3).append("/").append(valueOf4).toString());
        } catch (NotFoundException e) {
            if (Log.isLoggable("ResourceLoader", 5)) {
                valueOf = String.valueOf(model);
                Log.w("ResourceLoader", new StringBuilder(String.valueOf(valueOf).length() + 30).append("Received invalid resource id: ").append(valueOf).toString(), e);
            }
            return null;
        }
    }

    public boolean handles(Integer model) {
        return true;
    }
}
