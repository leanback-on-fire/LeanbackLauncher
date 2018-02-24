package com.bumptech.glide.load.model;

import android.content.res.AssetManager;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.data.FileDescriptorAssetPathFetcher;
import com.bumptech.glide.load.data.StreamAssetPathFetcher;
import com.bumptech.glide.signature.ObjectKey;
import java.io.InputStream;
import java.util.List;

public class AssetUriLoader<Data>
  implements ModelLoader<Uri, Data>
{
  private static final String ASSET_PATH_SEGMENT = "android_asset";
  private static final String ASSET_PREFIX = "file:///android_asset/";
  private static final int ASSET_PREFIX_LENGTH = "file:///android_asset/".length();
  private final AssetManager assetManager;
  private final AssetFetcherFactory<Data> factory;
  
  public AssetUriLoader(AssetManager paramAssetManager, AssetFetcherFactory<Data> paramAssetFetcherFactory)
  {
    this.assetManager = paramAssetManager;
    this.factory = paramAssetFetcherFactory;
  }
  
  public ModelLoader.LoadData<Data> buildLoadData(Uri paramUri, int paramInt1, int paramInt2, Options paramOptions)
  {
    paramOptions = paramUri.toString().substring(ASSET_PREFIX_LENGTH);
    return new ModelLoader.LoadData(new ObjectKey(paramUri), this.factory.buildFetcher(this.assetManager, paramOptions));
  }
  
  public boolean handles(Uri paramUri)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if ("file".equals(paramUri.getScheme()))
    {
      bool1 = bool2;
      if (!paramUri.getPathSegments().isEmpty())
      {
        bool1 = bool2;
        if ("android_asset".equals(paramUri.getPathSegments().get(0))) {
          bool1 = true;
        }
      }
    }
    return bool1;
  }
  
  public static abstract interface AssetFetcherFactory<Data>
  {
    public abstract DataFetcher<Data> buildFetcher(AssetManager paramAssetManager, String paramString);
  }
  
  public static class FileDescriptorFactory
    implements ModelLoaderFactory<Uri, ParcelFileDescriptor>, AssetUriLoader.AssetFetcherFactory<ParcelFileDescriptor>
  {
    private final AssetManager assetManager;
    
    public FileDescriptorFactory(AssetManager paramAssetManager)
    {
      this.assetManager = paramAssetManager;
    }
    
    public ModelLoader<Uri, ParcelFileDescriptor> build(MultiModelLoaderFactory paramMultiModelLoaderFactory)
    {
      return new AssetUriLoader(this.assetManager, this);
    }
    
    public DataFetcher<ParcelFileDescriptor> buildFetcher(AssetManager paramAssetManager, String paramString)
    {
      return new FileDescriptorAssetPathFetcher(paramAssetManager, paramString);
    }
    
    public void teardown() {}
  }
  
  public static class StreamFactory
    implements ModelLoaderFactory<Uri, InputStream>, AssetUriLoader.AssetFetcherFactory<InputStream>
  {
    private final AssetManager assetManager;
    
    public StreamFactory(AssetManager paramAssetManager)
    {
      this.assetManager = paramAssetManager;
    }
    
    public ModelLoader<Uri, InputStream> build(MultiModelLoaderFactory paramMultiModelLoaderFactory)
    {
      return new AssetUriLoader(this.assetManager, this);
    }
    
    public DataFetcher<InputStream> buildFetcher(AssetManager paramAssetManager, String paramString)
    {
      return new StreamAssetPathFetcher(paramAssetManager, paramString);
    }
    
    public void teardown() {}
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/load/model/AssetUriLoader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */