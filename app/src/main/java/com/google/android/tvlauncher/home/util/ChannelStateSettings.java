package com.google.android.tvlauncher.home.util;

public class ChannelStateSettings
{
  private int mChannelLogoAlignmentOriginMargin;
  private int mItemHeight;
  private int mItemMarginBottom;
  private int mItemMarginTop;
  private int mMarginBottom;
  private int mMarginTop;
  
  private ChannelStateSettings(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    this.mItemHeight = paramInt1;
    this.mItemMarginTop = paramInt2;
    this.mItemMarginBottom = paramInt3;
    this.mMarginTop = paramInt4;
    this.mMarginBottom = paramInt5;
    this.mChannelLogoAlignmentOriginMargin = paramInt6;
  }
  
  ChannelStateSettings(ChannelStateSettings paramChannelStateSettings)
  {
    this.mItemHeight = paramChannelStateSettings.getItemHeight();
    this.mItemMarginTop = paramChannelStateSettings.getItemMarginTop();
    this.mItemMarginBottom = paramChannelStateSettings.getItemMarginBottom();
    this.mMarginTop = paramChannelStateSettings.getMarginTop();
    this.mMarginBottom = paramChannelStateSettings.getMarginBottom();
    this.mChannelLogoAlignmentOriginMargin = paramChannelStateSettings.getChannelLogoAlignmentOriginMargin();
  }
  
  public int getChannelLogoAlignmentOriginMargin()
  {
    return this.mChannelLogoAlignmentOriginMargin;
  }
  
  public int getItemHeight()
  {
    return this.mItemHeight;
  }
  
  public int getItemMarginBottom()
  {
    return this.mItemMarginBottom;
  }
  
  public int getItemMarginTop()
  {
    return this.mItemMarginTop;
  }
  
  public int getMarginBottom()
  {
    return this.mMarginBottom;
  }
  
  public int getMarginTop()
  {
    return this.mMarginTop;
  }
  
  void setChannelLogoAlignmentOriginMargin(int paramInt)
  {
    this.mChannelLogoAlignmentOriginMargin = paramInt;
  }
  
  void setItemMarginBottom(int paramInt)
  {
    this.mItemMarginBottom = paramInt;
  }
  
  static class Builder
  {
    private int mChannelLogoAlignmentOriginMargin;
    private int mItemHeight;
    private int mItemMarginBottom;
    private int mItemMarginTop;
    private int mMarginBottom;
    private int mMarginTop;
    
    ChannelStateSettings build()
    {
      return new ChannelStateSettings(this.mItemHeight, this.mItemMarginTop, this.mItemMarginBottom, this.mMarginTop, this.mMarginBottom, this.mChannelLogoAlignmentOriginMargin, null);
    }
    
    Builder setChannelLogoAlignmentOriginMargin(int paramInt)
    {
      this.mChannelLogoAlignmentOriginMargin = paramInt;
      return this;
    }
    
    Builder setItemHeight(int paramInt)
    {
      this.mItemHeight = paramInt;
      return this;
    }
    
    Builder setItemMarginBottom(int paramInt)
    {
      this.mItemMarginBottom = paramInt;
      return this;
    }
    
    Builder setItemMarginTop(int paramInt)
    {
      this.mItemMarginTop = paramInt;
      return this;
    }
    
    Builder setMarginBottom(int paramInt)
    {
      this.mMarginBottom = paramInt;
      return this;
    }
    
    Builder setMarginTop(int paramInt)
    {
      this.mMarginTop = paramInt;
      return this;
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/home/util/ChannelStateSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */