package com.bumptech.glide.load.engine;

import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.util.Preconditions;
import java.security.MessageDigest;
import java.util.Map;

class EngineKey
  implements Key
{
  private int hashCode;
  private final int height;
  private final Object model;
  private final Options options;
  private final Class<?> resourceClass;
  private final Key signature;
  private final Class<?> transcodeClass;
  private final Map<Class<?>, Transformation<?>> transformations;
  private final int width;
  
  public EngineKey(Object paramObject, Key paramKey, int paramInt1, int paramInt2, Map<Class<?>, Transformation<?>> paramMap, Class<?> paramClass1, Class<?> paramClass2, Options paramOptions)
  {
    this.model = Preconditions.checkNotNull(paramObject);
    this.signature = ((Key)Preconditions.checkNotNull(paramKey, "Signature must not be null"));
    this.width = paramInt1;
    this.height = paramInt2;
    this.transformations = ((Map)Preconditions.checkNotNull(paramMap));
    this.resourceClass = ((Class)Preconditions.checkNotNull(paramClass1, "Resource class must not be null"));
    this.transcodeClass = ((Class)Preconditions.checkNotNull(paramClass2, "Transcode class must not be null"));
    this.options = ((Options)Preconditions.checkNotNull(paramOptions));
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if ((paramObject instanceof EngineKey))
    {
      paramObject = (EngineKey)paramObject;
      bool1 = bool2;
      if (this.model.equals(((EngineKey)paramObject).model))
      {
        bool1 = bool2;
        if (this.signature.equals(((EngineKey)paramObject).signature))
        {
          bool1 = bool2;
          if (this.height == ((EngineKey)paramObject).height)
          {
            bool1 = bool2;
            if (this.width == ((EngineKey)paramObject).width)
            {
              bool1 = bool2;
              if (this.transformations.equals(((EngineKey)paramObject).transformations))
              {
                bool1 = bool2;
                if (this.resourceClass.equals(((EngineKey)paramObject).resourceClass))
                {
                  bool1 = bool2;
                  if (this.transcodeClass.equals(((EngineKey)paramObject).transcodeClass))
                  {
                    bool1 = bool2;
                    if (this.options.equals(((EngineKey)paramObject).options)) {
                      bool1 = true;
                    }
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
    if (this.hashCode == 0)
    {
      this.hashCode = this.model.hashCode();
      this.hashCode = (this.hashCode * 31 + this.signature.hashCode());
      this.hashCode = (this.hashCode * 31 + this.width);
      this.hashCode = (this.hashCode * 31 + this.height);
      this.hashCode = (this.hashCode * 31 + this.transformations.hashCode());
      this.hashCode = (this.hashCode * 31 + this.resourceClass.hashCode());
      this.hashCode = (this.hashCode * 31 + this.transcodeClass.hashCode());
      this.hashCode = (this.hashCode * 31 + this.options.hashCode());
    }
    return this.hashCode;
  }
  
  public String toString()
  {
    String str1 = String.valueOf(this.model);
    int i = this.width;
    int j = this.height;
    String str2 = String.valueOf(this.resourceClass);
    String str3 = String.valueOf(this.transcodeClass);
    String str4 = String.valueOf(this.signature);
    int k = this.hashCode;
    String str5 = String.valueOf(this.transformations);
    String str6 = String.valueOf(this.options);
    return String.valueOf(str1).length() + 151 + String.valueOf(str2).length() + String.valueOf(str3).length() + String.valueOf(str4).length() + String.valueOf(str5).length() + String.valueOf(str6).length() + "EngineKey{model=" + str1 + ", width=" + i + ", height=" + j + ", resourceClass=" + str2 + ", transcodeClass=" + str3 + ", signature=" + str4 + ", hashCode=" + k + ", transformations=" + str5 + ", options=" + str6 + "}";
  }
  
  public void updateDiskCacheKey(MessageDigest paramMessageDigest)
  {
    throw new UnsupportedOperationException();
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/load/engine/EngineKey.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */