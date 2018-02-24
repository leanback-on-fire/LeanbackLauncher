package com.bumptech.glide.load.engine;

import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.util.LruCache;
import com.bumptech.glide.util.Util;
import java.nio.ByteBuffer;
import java.security.MessageDigest;

final class ResourceCacheKey
  implements Key
{
  private static final LruCache<Class<?>, byte[]> RESOURCE_CLASS_BYTES = new LruCache(50);
  private final Class<?> decodedResourceClass;
  private final int height;
  private final Options options;
  private final Key signature;
  private final Key sourceKey;
  private final Transformation<?> transformation;
  private final int width;
  
  public ResourceCacheKey(Key paramKey1, Key paramKey2, int paramInt1, int paramInt2, Transformation<?> paramTransformation, Class<?> paramClass, Options paramOptions)
  {
    this.sourceKey = paramKey1;
    this.signature = paramKey2;
    this.width = paramInt1;
    this.height = paramInt2;
    this.transformation = paramTransformation;
    this.decodedResourceClass = paramClass;
    this.options = paramOptions;
  }
  
  private byte[] getResourceClassBytes()
  {
    byte[] arrayOfByte2 = (byte[])RESOURCE_CLASS_BYTES.get(this.decodedResourceClass);
    byte[] arrayOfByte1 = arrayOfByte2;
    if (arrayOfByte2 == null)
    {
      arrayOfByte1 = this.decodedResourceClass.getName().getBytes(CHARSET);
      RESOURCE_CLASS_BYTES.put(this.decodedResourceClass, arrayOfByte1);
    }
    return arrayOfByte1;
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if ((paramObject instanceof ResourceCacheKey))
    {
      paramObject = (ResourceCacheKey)paramObject;
      bool1 = bool2;
      if (this.height == ((ResourceCacheKey)paramObject).height)
      {
        bool1 = bool2;
        if (this.width == ((ResourceCacheKey)paramObject).width)
        {
          bool1 = bool2;
          if (Util.bothNullOrEqual(this.transformation, ((ResourceCacheKey)paramObject).transformation))
          {
            bool1 = bool2;
            if (this.decodedResourceClass.equals(((ResourceCacheKey)paramObject).decodedResourceClass))
            {
              bool1 = bool2;
              if (this.sourceKey.equals(((ResourceCacheKey)paramObject).sourceKey))
              {
                bool1 = bool2;
                if (this.signature.equals(((ResourceCacheKey)paramObject).signature))
                {
                  bool1 = bool2;
                  if (this.options.equals(((ResourceCacheKey)paramObject).options)) {
                    bool1 = true;
                  }
                }
              }
            }
          }
        }
      }
    }
    return bool1;
  }
  
  public int hashCode()
  {
    int j = ((this.sourceKey.hashCode() * 31 + this.signature.hashCode()) * 31 + this.width) * 31 + this.height;
    int i = j;
    if (this.transformation != null) {
      i = j * 31 + this.transformation.hashCode();
    }
    return (i * 31 + this.decodedResourceClass.hashCode()) * 31 + this.options.hashCode();
  }
  
  public String toString()
  {
    String str1 = String.valueOf(this.sourceKey);
    String str2 = String.valueOf(this.signature);
    int i = this.width;
    int j = this.height;
    String str3 = String.valueOf(this.decodedResourceClass);
    String str4 = String.valueOf(this.transformation);
    String str5 = String.valueOf(this.options);
    return String.valueOf(str1).length() + 131 + String.valueOf(str2).length() + String.valueOf(str3).length() + String.valueOf(str4).length() + String.valueOf(str5).length() + "ResourceCacheKey{sourceKey=" + str1 + ", signature=" + str2 + ", width=" + i + ", height=" + j + ", decodedResourceClass=" + str3 + ", transformation='" + str4 + "'" + ", options=" + str5 + "}";
  }
  
  public void updateDiskCacheKey(MessageDigest paramMessageDigest)
  {
    byte[] arrayOfByte = ByteBuffer.allocate(8).putInt(this.width).putInt(this.height).array();
    this.signature.updateDiskCacheKey(paramMessageDigest);
    this.sourceKey.updateDiskCacheKey(paramMessageDigest);
    paramMessageDigest.update(arrayOfByte);
    if (this.transformation != null) {
      this.transformation.updateDiskCacheKey(paramMessageDigest);
    }
    this.options.updateDiskCacheKey(paramMessageDigest);
    paramMessageDigest.update(getResourceClassBytes());
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/load/engine/ResourceCacheKey.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */