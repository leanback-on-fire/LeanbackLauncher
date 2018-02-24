package com.bumptech.glide.load.engine;

import com.bumptech.glide.load.Key;
import java.security.MessageDigest;

final class DataCacheKey
  implements Key
{
  private final Key signature;
  private final Key sourceKey;
  
  public DataCacheKey(Key paramKey1, Key paramKey2)
  {
    this.sourceKey = paramKey1;
    this.signature = paramKey2;
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if ((paramObject instanceof DataCacheKey))
    {
      paramObject = (DataCacheKey)paramObject;
      bool1 = bool2;
      if (this.sourceKey.equals(((DataCacheKey)paramObject).sourceKey))
      {
        bool1 = bool2;
        if (this.signature.equals(((DataCacheKey)paramObject).signature)) {
          bool1 = true;
        }
      }
    }
    return bool1;
  }
  
  public Key getSourceKey()
  {
    return this.sourceKey;
  }
  
  public int hashCode()
  {
    return this.sourceKey.hashCode() * 31 + this.signature.hashCode();
  }
  
  public String toString()
  {
    String str1 = String.valueOf(this.sourceKey);
    String str2 = String.valueOf(this.signature);
    return String.valueOf(str1).length() + 36 + String.valueOf(str2).length() + "DataCacheKey{sourceKey=" + str1 + ", signature=" + str2 + "}";
  }
  
  public void updateDiskCacheKey(MessageDigest paramMessageDigest)
  {
    this.sourceKey.updateDiskCacheKey(paramMessageDigest);
    this.signature.updateDiskCacheKey(paramMessageDigest);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/load/engine/DataCacheKey.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */