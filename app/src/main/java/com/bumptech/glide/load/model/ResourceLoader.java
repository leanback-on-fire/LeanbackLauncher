package com.bumptech.glide.load.model;

import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.support.annotation.Nullable;
import android.util.Log;
import com.bumptech.glide.load.Options;
import java.io.InputStream;

public class ResourceLoader<Data>
  implements ModelLoader<Integer, Data>
{
  private static final String TAG = "ResourceLoader";
  private final Resources resources;
  private final ModelLoader<Uri, Data> uriLoader;
  
  public ResourceLoader(Resources paramResources, ModelLoader<Uri, Data> paramModelLoader)
  {
    this.resources = paramResources;
    this.uriLoader = paramModelLoader;
  }
  
  @Nullable
  private Uri getResourceUri(Integer paramInteger)
  {
    try
    {
      Object localObject = String.valueOf("android.resource://");
      String str1 = String.valueOf(this.resources.getResourcePackageName(paramInteger.intValue()));
      String str2 = String.valueOf(this.resources.getResourceTypeName(paramInteger.intValue()));
      String str3 = String.valueOf(this.resources.getResourceEntryName(paramInteger.intValue()));
      localObject = Uri.parse(String.valueOf(localObject).length() + 2 + String.valueOf(str1).length() + String.valueOf(str2).length() + String.valueOf(str3).length() + (String)localObject + str1 + "/" + str2 + "/" + str3);
      return (Uri)localObject;
    }
    catch (Resources.NotFoundException localNotFoundException)
    {
      if (Log.isLoggable("ResourceLoader", 5))
      {
        paramInteger = String.valueOf(paramInteger);
        Log.w("ResourceLoader", String.valueOf(paramInteger).length() + 30 + "Received invalid resource id: " + paramInteger, localNotFoundException);
      }
    }
    return null;
  }
  
  public ModelLoader.LoadData<Data> buildLoadData(Integer paramInteger, int paramInt1, int paramInt2, Options paramOptions)
  {
    paramInteger = getResourceUri(paramInteger);
    if (paramInteger == null) {
      return null;
    }
    return this.uriLoader.buildLoadData(paramInteger, paramInt1, paramInt2, paramOptions);
  }
  
  public boolean handles(Integer paramInteger)
  {
    return true;
  }
  
  public static class FileDescriptorFactory
    implements ModelLoaderFactory<Integer, ParcelFileDescriptor>
  {
    private final Resources resources;
    
    public FileDescriptorFactory(Resources paramResources)
    {
      this.resources = paramResources;
    }
    
    public ModelLoader<Integer, ParcelFileDescriptor> build(MultiModelLoaderFactory paramMultiModelLoaderFactory)
    {
      return new ResourceLoader(this.resources, paramMultiModelLoaderFactory.build(Uri.class, ParcelFileDescriptor.class));
    }
    
    public void teardown() {}
  }
  
  public static class StreamFactory
    implements ModelLoaderFactory<Integer, InputStream>
  {
    private final Resources resources;
    
    public StreamFactory(Resources paramResources)
    {
      this.resources = paramResources;
    }
    
    public ModelLoader<Integer, InputStream> build(MultiModelLoaderFactory paramMultiModelLoaderFactory)
    {
      return new ResourceLoader(this.resources, paramMultiModelLoaderFactory.build(Uri.class, InputStream.class));
    }
    
    public void teardown() {}
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/load/model/ResourceLoader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */