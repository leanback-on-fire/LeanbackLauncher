package com.google.android.tvlauncher.util;

import android.graphics.drawable.Drawable;

public class ContextMenuItem
{
  private boolean mEnabled;
  private Drawable mIcon;
  private int mId;
  private boolean mIsLinkedWithTriangle;
  private String mTitle;
  private boolean mVisible;
  
  public ContextMenuItem(int paramInt, String paramString, Drawable paramDrawable)
  {
    this.mId = paramInt;
    this.mTitle = paramString;
    this.mIcon = paramDrawable;
    this.mEnabled = true;
    this.mVisible = true;
    this.mIsLinkedWithTriangle = false;
  }
  
  public Drawable getIcon()
  {
    return this.mIcon;
  }
  
  public int getId()
  {
    return this.mId;
  }
  
  public String getTitle()
  {
    return this.mTitle;
  }
  
  public boolean isEnabled()
  {
    return this.mEnabled;
  }
  
  public boolean isLinkedWithTriangle()
  {
    return this.mIsLinkedWithTriangle;
  }
  
  public boolean isVisible()
  {
    return this.mVisible;
  }
  
  public void setEnabled(boolean paramBoolean)
  {
    this.mEnabled = paramBoolean;
  }
  
  public void setIcon(Drawable paramDrawable)
  {
    this.mIcon = paramDrawable;
  }
  
  public void setLinkedWithTriangle(boolean paramBoolean)
  {
    this.mIsLinkedWithTriangle = paramBoolean;
  }
  
  public void setTitle(String paramString)
  {
    this.mTitle = paramString;
  }
  
  public void setVisible(boolean paramBoolean)
  {
    this.mVisible = paramBoolean;
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/util/ContextMenuItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */