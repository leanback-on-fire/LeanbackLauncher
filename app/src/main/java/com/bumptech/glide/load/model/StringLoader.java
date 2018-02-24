package com.bumptech.glide.load.model;

import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.bumptech.glide.load.Options;
import java.io.File;
import java.io.InputStream;

public class StringLoader<Data>
  implements ModelLoader<String, Data>
{
  private final ModelLoader<Uri, Data> uriLoader;
  
  public StringLoader(ModelLoader<Uri, Data> paramModelLoader)
  {
    this.uriLoader = paramModelLoader;
  }
  
  @Nullable
  private static Uri parseUri(String paramString)
  {
    Object localObject;
    if (TextUtils.isEmpty(paramString)) {
      localObject = null;
    }
    Uri localUri;
    do
    {
      return (Uri)localObject;
      if (paramString.startsWith("/")) {
        return toFileUri(paramString);
      }
      localUri = Uri.parse(paramString);
      localObject = localUri;
    } while (localUri.getScheme() != null);
    return toFileUri(paramString);
  }
  
  private static Uri toFileUri(String paramString)
  {
    return Uri.fromFile(new File(paramString));
  }
  
  public ModelLoader.LoadData<Data> buildLoadData(String paramString, int paramInt1, int paramInt2, Options paramOptions)
  {
    paramString = parseUri(paramString);
    if (paramString == null) {
      return null;
    }
    return this.uriLoader.buildLoadData(paramString, paramInt1, paramInt2, paramOptions);
  }
  
  public boolean handles(String paramString)
  {
    return true;
  }
  
  public static class FileDescriptorFactory
    implements ModelLoaderFactory<String, ParcelFileDescriptor>
  {
    public ModelLoader<String, ParcelFileDescriptor> build(MultiModelLoaderFactory paramMultiModelLoaderFactory)
    {
      return new StringLoader(paramMultiModelLoaderFactory.build(Uri.class, ParcelFileDescriptor.class));
    }
    
    public void teardown() {}
  }
  
  public static class StreamFactory
    implements ModelLoaderFactory<String, InputStream>
  {
    public ModelLoader<String, InputStream> build(MultiModelLoaderFactory paramMultiModelLoaderFactory)
    {
      return new StringLoader(paramMultiModelLoaderFactory.build(Uri.class, InputStream.class));
    }
    
    public void teardown() {}
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/load/model/StringLoader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */