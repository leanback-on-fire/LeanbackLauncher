package com.google.android.tvlauncher.home.util;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.DimenRes;
import android.support.v17.leanback.widget.HorizontalGridView;
import android.support.v4.content.ContextCompat;
import android.util.SparseArray;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.google.android.tvlauncher.R;

import static com.google.android.tvlauncher.home.util.ChannelStateSettings.*;

public class ChannelUtil {
    public static SparseArray<ChannelStateSettings> getDefaultChannelStateSettings(Context context) {
        SparseArray<ChannelStateSettings> settingsMap = new SparseArray();
        Resources r = context.getResources();
        settingsMap.put(0, new Builder().setItemHeight(getSize(r, R.dimen.program_selected_height)).setItemMarginTop(getSize(r, R.dimen.program_selected_margin_vertical)).setItemMarginBottom(getSize(r, R.dimen.program_selected_margin_vertical)).setMarginTop(getSize(r, R.dimen.channel_selected_margin_top)).setMarginBottom(getSize(r, R.dimen.channel_selected_margin_bottom)).setChannelLogoAlignmentOriginMargin(getSize(r, R.dimen.channel_logo_selected_alignment_origin_margin)).build());
        ChannelStateSettings settings = new Builder().setItemHeight(getSize(r, R.dimen.program_default_height)).setItemMarginTop(getSize(r, R.dimen.program_default_margin_top)).setItemMarginBottom(getSize(r, R.dimen.program_default_margin_bottom)).setChannelLogoAlignmentOriginMargin(getSize(r, R.dimen.channel_logo_alignment_origin_margin)).build();
        settingsMap.put(1, settings);
        ChannelStateSettings settings2 = new ChannelStateSettings(settings);
        settings2.setChannelLogoAlignmentOriginMargin(getSize(r, R.dimen.channel_logo_above_selected_alignment_origin_margin));
        settingsMap.put(2, settings2);
        settings = new ChannelStateSettings(settings2);
        settings.setChannelLogoAlignmentOriginMargin(getSize(r, R.dimen.channel_logo_below_selected_alignment_origin_margin));
        settingsMap.put(3, settings);
        settings = new Builder().setItemHeight(getSize(r, R.dimen.program_zoomed_out_height)).setItemMarginTop(getSize(r, R.dimen.program_zoomed_out_margin_vertical)).setItemMarginBottom(getSize(r, R.dimen.program_zoomed_out_margin_vertical)).setMarginBottom(getSize(r, R.dimen.channel_zoomed_out_margin_bottom)).build();
        settingsMap.put(4, settings);
        settingsMap.put(5, settings);
        settingsMap.put(6, settings);
        settingsMap.put(7, settings);
        settingsMap.put(8, settings);
        settingsMap.put(9, settings);
        return settingsMap;
    }

    public static SparseArray<ChannelStateSettings> getAppsRowStateSettings(Context context) {
        SparseArray<ChannelStateSettings> settingsMap = new SparseArray();
        Resources r = context.getResources();
        settingsMap.put(0, new Builder().setItemHeight(getSize(r, R.dimen.home_app_banner_height)).setItemMarginTop(getSize(r, R.dimen.home_app_banner_selected_margin_top)).setItemMarginBottom(getSize(r, R.dimen.home_app_banner_selected_margin_bottom)).setChannelLogoAlignmentOriginMargin(getSize(r, R.dimen.home_app_channel_logo_selected_alignment_origin_margin)).build());
        ChannelStateSettings defaultSettings = new Builder().setItemHeight(getSize(r, R.dimen.home_app_banner_image_height)).setItemMarginTop(getSize(r, R.dimen.home_app_banner_default_margin_top)).setItemMarginBottom(getSize(r, R.dimen.home_app_banner_default_margin_bottom)).setChannelLogoAlignmentOriginMargin(getSize(r, R.dimen.home_app_channel_logo_default_alignment_origin_margin)).build();
        settingsMap.put(1, defaultSettings);
        settingsMap.put(3, defaultSettings);
        ChannelStateSettings settings = new ChannelStateSettings(defaultSettings);
        settings.setChannelLogoAlignmentOriginMargin(getSize(r, R.dimen.channel_logo_above_selected_alignment_origin_margin));
        settings.setItemMarginBottom(getSize(r, R.dimen.home_app_banner_default_above_selected_margin_bottom));
        settingsMap.put(2, settings);
        settings = new Builder().setItemHeight(getSize(r, R.dimen.home_app_banner_image_height)).setItemMarginTop(getSize(r, R.dimen.home_app_banner_zoomed_out_margin_vertical)).setItemMarginBottom(getSize(r, R.dimen.home_app_banner_zoomed_out_margin_vertical)).setMarginTop(getSize(r, R.dimen.home_app_banner_zoomed_out_row_margin_top)).setMarginBottom(getSize(r, R.dimen.channel_zoomed_out_margin_bottom)).build();
        settingsMap.put(4, settings);
        settingsMap.put(5, settings);
        settingsMap.put(6, settings);
        settingsMap.put(7, settings);
        settingsMap.put(8, settings);
        settingsMap.put(9, settings);
        return settingsMap;
    }

    private static int getSize(Resources resources, @DimenRes int dimension) {
        return resources.getDimensionPixelSize(dimension);
    }

    public static void configureItemsListAlignment(HorizontalGridView itemsList) {
        itemsList.setWindowAlignment(1);
        itemsList.setWindowAlignmentOffsetPercent(0.0f);
        itemsList.setWindowAlignmentOffset(itemsList.getContext().getResources().getDimensionPixelSize(R.dimen.channel_items_list_padding_start));
        itemsList.setItemAlignmentOffsetPercent(0.0f);
    }

    public static void setWatchNextLogo(ImageView logoView) {
        Context context = logoView.getContext();
        logoView.setContentDescription(context.getString(R.string.watch_next_channel_title));
        logoView.setBackgroundColor(ContextCompat.getColor(context, R.color.watch_next_logo_background));
        logoView.setImageDrawable(context.getDrawable(R.drawable.ic_watch_next_icon));
        logoView.setImageTintList(ContextCompat.getColorStateList(context, R.color.watch_next_logo_icon_tint));
        logoView.setScaleType(ScaleType.FIT_CENTER);
        int padding = context.getResources().getDimensionPixelOffset(R.dimen.watch_next_logo_icon_padding);
        logoView.setPadding(padding, padding, padding, padding);
    }
}
