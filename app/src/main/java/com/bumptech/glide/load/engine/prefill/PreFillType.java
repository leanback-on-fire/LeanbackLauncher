package com.bumptech.glide.load.engine.prefill;

import android.graphics.Bitmap.Config;
import android.support.annotation.Nullable;
import com.bumptech.glide.util.Preconditions;

public final class PreFillType
{
  static final Bitmap.Config DEFAULT_CONFIG = Bitmap.Config.RGB_565;
  private final Bitmap.Config config;
  private final int height;
  private final int weight;
  private final int width;
  
  PreFillType(int paramInt1, int paramInt2, Bitmap.Config paramConfig, int paramInt3)
  {
    this.config = ((Bitmap.Config)Preconditions.checkNotNull(paramConfig, "Config must not be null"));
    this.width = paramInt1;
    this.height = paramInt2;
    this.weight = paramInt3;
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if ((paramObject instanceof PreFillType))
    {
      paramObject = (PreFillType)paramObject;
      bool1 = bool2;
      if (this.height == ((PreFillType)paramObject).height)
      {
        bool1 = bool2;
        if (this.width == ((PreFillType)paramObject).width)
        {
          bool1 = bool2;
          if (this.weight == ((PreFillType)paramObject).weight)
          {
            bool1 = bool2;
            if (this.config == ((PreFillType)paramObject).config) {
              bool1 = true;
            }
          }
        }
      }
    }
    return bool1;
  }
  
  Bitmap.Config getConfig()
  {
    return this.config;
  }
  
  int getHeight()
  {
    return this.height;
  }
  
  int getWeight()
  {
    return this.weight;
  }
  
  int getWidth()
  {
    return this.width;
  }
  
  public int hashCode()
  {
    return ((this.width * 31 + this.height) * 31 + this.config.hashCode()) * 31 + this.weight;
  }
  
  public String toString()
  {
    int i = this.width;
    int j = this.height;
    String str = String.valueOf(this.config);
    int k = this.weight;
    return String.valueOf(str).length() + 79 + "PreFillSize{width=" + i + ", height=" + j + ", config=" + str + ", weight=" + k + "}";
  }
  
  public static class Builder
  {
    private Bitmap.Config config;
    private final int height;
    private int weight = 1;
    private final int width;
    
    public Builder(int paramInt)
    {
      this(paramInt, paramInt);
    }
    
    public Builder(int paramInt1, int paramInt2)
    {
      if (paramInt1 <= 0) {
        throw new IllegalArgumentException("Width must be > 0");
      }
      if (paramInt2 <= 0) {
        throw new IllegalArgumentException("Height must be > 0");
      }
      this.width = paramInt1;
      this.height = paramInt2;
    }
    
    PreFillType build()
    {
      return new PreFillType(this.width, this.height, this.config, this.weight);
    }
    
    Bitmap.Config getConfig()
    {
      return this.config;
    }
    
    public Builder setConfig(@Nullable Bitmap.Config paramConfig)
    {
      this.config = paramConfig;
      return this;
    }
    
    public Builder setWeight(int paramInt)
    {
      if (paramInt <= 0) {
        throw new IllegalArgumentException("Weight must be > 0");
      }
      this.weight = paramInt;
      return this;
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/load/engine/prefill/PreFillType.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */