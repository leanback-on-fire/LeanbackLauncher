package com.google.android.tvlauncher.appsview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import java.util.ArrayList;

public class AppRowView
  extends LinearLayout
{
  public static final int MAX_APPS = 4;
  private static final String TAG = "AppRowView";
  private OnAppsViewActionListener mOnAppsViewActionListener;
  
  public AppRowView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public AppRowView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public AppRowView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public AppRowView(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
  }
  
  private boolean addBannerView(AppBannerView paramAppBannerView)
  {
    if (getChildCount() < 4)
    {
      addView(paramAppBannerView);
      return true;
    }
    return false;
  }
  
  private boolean addBannerView(LaunchItem paramLaunchItem, boolean paramBoolean, int paramInt)
  {
    if (getChildCount() > paramInt)
    {
      AppBannerView localAppBannerView = (AppBannerView)getChildAt(paramInt);
      localAppBannerView.setAppBannerItems(paramLaunchItem, paramBoolean, this.mOnAppsViewActionListener);
      localAppBannerView.setVisibility(0);
      return true;
    }
    return addBannerView(createBannerView(paramLaunchItem, paramBoolean));
  }
  
  private AppBannerView createBannerView(LaunchItem paramLaunchItem, boolean paramBoolean)
  {
    AppBannerView localAppBannerView = (AppBannerView)LayoutInflater.from(getContext()).inflate(2130968606, this, false);
    localAppBannerView.setAppBannerItems(paramLaunchItem, paramBoolean, this.mOnAppsViewActionListener);
    return localAppBannerView;
  }
  
  public void addBannerViews(ArrayList<LaunchItem> paramArrayList, boolean paramBoolean)
  {
    int i = 0;
    if (i < 4)
    {
      if (i < paramArrayList.size()) {
        if (!addBannerView((LaunchItem)paramArrayList.get(i), paramBoolean, i)) {
          Log.e("AppRowView", "Unable to add item to row. Maximum amount of items.");
        }
      }
      for (;;)
      {
        i += 1;
        break;
        if (i < getChildCount()) {
          getChildAt(i).setVisibility(8);
        }
      }
    }
  }
  
  public boolean canAddChild()
  {
    return getChildCount() < 4;
  }
  
  public void setOnAppsViewActionListener(OnAppsViewActionListener paramOnAppsViewActionListener)
  {
    this.mOnAppsViewActionListener = paramOnAppsViewActionListener;
  }
  
  public void setOneTimeFocusPosition(int paramInt)
  {
    getChildAt(paramInt).requestFocus();
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/appsview/AppRowView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */