package com.bumptech.glide.load.model;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.data.FileDescriptorLocalUriFetcher;
import com.bumptech.glide.load.data.StreamLocalUriFetcher;
import com.bumptech.glide.signature.ObjectKey;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class UriLoader<Data>
  implements ModelLoader<Uri, Data>
{
  private static final Set<String> SCHEMES = Collections.unmodifiableSet(new HashSet(Arrays.asList(new String[] { "file", "android.resource", "content" })));
  private final LocalUriFetcherFactory<Data> factory;
  
  public UriLoader(LocalUriFetcherFactory<Data> paramLocalUriFetcherFactory)
  {
    this.factory = paramLocalUriFetcherFactory;
  }
  
  public ModelLoader.LoadData<Data> buildLoadData(Uri paramUri, int paramInt1, int paramInt2, Options paramOptions)
  {
    return new ModelLoader.LoadData(new ObjectKey(paramUri), this.factory.build(paramUri));
  }
  
  public boolean handles(Uri paramUri)
  {
    return SCHEMES.contains(paramUri.getScheme());
  }
  
  public static class FileDescriptorFactory
    implements ModelLoaderFactory<Uri, ParcelFileDescriptor>, UriLoader.LocalUriFetcherFactory<ParcelFileDescriptor>
  {
    private final ContentResolver contentResolver;
    
    public FileDescriptorFactory(ContentResolver paramContentResolver)
    {
      this.contentResolver = paramContentResolver;
    }
    
    public DataFetcher<ParcelFileDescriptor> build(Uri paramUri)
    {
      return new FileDescriptorLocalUriFetcher(this.contentResolver, paramUri);
    }
    
    public ModelLoader<Uri, ParcelFileDescriptor> build(MultiModelLoaderFactory paramMultiModelLoaderFactory)
    {
      return new UriLoader(this);
    }
    
    public void teardown() {}
  }
  
  public static abstract interface LocalUriFetcherFactory<Data>
  {
    public abstract DataFetcher<Data> build(Uri paramUri);
  }
  
  public static class StreamFactory
    implements ModelLoaderFactory<Uri, InputStream>, UriLoader.LocalUriFetcherFactory<InputStream>
  {
    private final ContentResolver contentResolver;
    
    public StreamFactory(ContentResolver paramContentResolver)
    {
      this.contentResolver = paramContentResolver;
    }
    
    public DataFetcher<InputStream> build(Uri paramUri)
    {
      return new StreamLocalUriFetcher(this.contentResolver, paramUri);
    }
    
    public ModelLoader<Uri, InputStream> build(MultiModelLoaderFactory paramMultiModelLoaderFactory)
    {
      return new UriLoader(this);
    }
    
    public void teardown() {}
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/load/model/UriLoader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */