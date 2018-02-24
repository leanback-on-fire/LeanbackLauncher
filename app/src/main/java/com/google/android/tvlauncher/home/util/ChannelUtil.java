package com.google.android.tvlauncher.home.util;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.DimenRes;
import android.support.v17.leanback.widget.HorizontalGridView;
import android.support.v4.content.ContextCompat;
import android.util.SparseArray;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class ChannelUtil
{
  public static void configureItemsListAlignment(HorizontalGridView paramHorizontalGridView)
  {
    paramHorizontalGridView.setWindowAlignment(1);
    paramHorizontalGridView.setWindowAlignmentOffsetPercent(0.0F);
    paramHorizontalGridView.setWindowAlignmentOffset(paramHorizontalGridView.getContext().getResources().getDimensionPixelSize(R.dimen.channel_items_list_padding_start));
    paramHorizontalGridView.setItemAlignmentOffsetPercent(0.0F);
  }
  
  public static SparseArray<ChannelStateSettings> getAppsRowStateSettings(Context paramContext)
  {
    SparseArray localSparseArray = new SparseArray();
    paramContext = paramContext.getResources();
    localSparseArray.put(0, new ChannelStateSettings.Builder().setItemHeight(getSize(paramContext, 2131558593)).setItemMarginTop(getSize(paramContext, 2131558596)).setItemMarginBottom(getSize(paramContext, 2131558595)).setChannelLogoAlignmentOriginMargin(getSize(paramContext, 2131558603)).build());
    ChannelStateSettings localChannelStateSettings = new ChannelStateSettings.Builder().setItemHeight(getSize(paramContext, 2131558594)).setItemMarginTop(getSize(paramContext, 2131558591)).setItemMarginBottom(getSize(paramContext, 2131558589)).setChannelLogoAlignmentOriginMargin(getSize(paramContext, 2131558601)).build();
    localSparseArray.put(1, localChannelStateSettings);
    localSparseArray.put(3, localChannelStateSettings);
    localChannelStateSettings = new ChannelStateSettings(localChannelStateSettings);
    localChannelStateSettings.setChannelLogoAlignmentOriginMargin(getSize(paramContext, 2131558527));
    localChannelStateSettings.setItemMarginBottom(getSize(paramContext, 2131558588));
    localSparseArray.put(2, localChannelStateSettings);
    paramContext = new ChannelStateSettings.Builder().setItemHeight(getSize(paramContext, 2131558594)).setItemMarginTop(getSize(paramContext, 2131558599)).setItemMarginBottom(getSize(paramContext, 2131558599)).setMarginTop(getSize(paramContext, 2131558600)).setMarginBottom(getSize(paramContext, 2131558546)).build();
    localSparseArray.put(4, paramContext);
    localSparseArray.put(5, paramContext);
    localSparseArray.put(6, paramContext);
    localSparseArray.put(7, paramContext);
    localSparseArray.put(8, paramContext);
    localSparseArray.put(9, paramContext);
    return localSparseArray;
  }
  
  public static SparseArray<ChannelStateSettings> getDefaultChannelStateSettings(Context paramContext)
  {
    SparseArray localSparseArray = new SparseArray();
    paramContext = paramContext.getResources();
    localSparseArray.put(0, new ChannelStateSettings.Builder().setItemHeight(getSize(paramContext, 2131558998)).setItemMarginTop(getSize(paramContext, 2131558999)).setItemMarginBottom(getSize(paramContext, 2131558999)).setMarginTop(getSize(paramContext, 2131558541)).setMarginBottom(getSize(paramContext, 2131558540)).setChannelLogoAlignmentOriginMargin(getSize(paramContext, 2131558531)).build());
    ChannelStateSettings localChannelStateSettings = new ChannelStateSettings.Builder().setItemHeight(getSize(paramContext, 2131558971)).setItemMarginTop(getSize(paramContext, 2131558974)).setItemMarginBottom(getSize(paramContext, 2131558972)).setChannelLogoAlignmentOriginMargin(getSize(paramContext, 2131558528)).build();
    localSparseArray.put(1, localChannelStateSettings);
    localChannelStateSettings = new ChannelStateSettings(localChannelStateSettings);
    localChannelStateSettings.setChannelLogoAlignmentOriginMargin(getSize(paramContext, 2131558527));
    localSparseArray.put(2, localChannelStateSettings);
    localChannelStateSettings = new ChannelStateSettings(localChannelStateSettings);
    localChannelStateSettings.setChannelLogoAlignmentOriginMargin(getSize(paramContext, 2131558529));
    localSparseArray.put(3, localChannelStateSettings);
    paramContext = new ChannelStateSettings.Builder().setItemHeight(getSize(paramContext, 2131559000)).setItemMarginTop(getSize(paramContext, 2131559002)).setItemMarginBottom(getSize(paramContext, 2131559002)).setMarginBottom(getSize(paramContext, 2131558546)).build();
    localSparseArray.put(4, paramContext);
    localSparseArray.put(5, paramContext);
    localSparseArray.put(6, paramContext);
    localSparseArray.put(7, paramContext);
    localSparseArray.put(8, paramContext);
    localSparseArray.put(9, paramContext);
    return localSparseArray;
  }
  
  private static int getSize(Resources paramResources, @DimenRes int paramInt)
  {
    return paramResources.getDimensionPixelSize(paramInt);
  }
  
  public static void setWatchNextLogo(ImageView paramImageView)
  {
    Context localContext = paramImageView.getContext();
    paramImageView.setContentDescription(localContext.getString(R.string.watch_next_channel_title));
    paramImageView.setBackgroundColor(localContext.getColor(R.color.watch_next_logo_background));
    paramImageView.setImageDrawable(localContext.getDrawable(R.drawable.ic_watch_next_icon));
    paramImageView.setImageTintList(ContextCompat.getColorStateList(localContext, 2131820746));
    paramImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
    int i = localContext.getResources().getDimensionPixelOffset(R.dimen.watch_next_logo_icon_padding);
    paramImageView.setPadding(i, i, i, i);
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/home/util/ChannelUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */