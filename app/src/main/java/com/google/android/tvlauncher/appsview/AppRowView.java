package com.google.android.tvlauncher.appsview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.tvlauncher.R;

import java.util.ArrayList;

public class AppRowView extends LinearLayout {
    public static final int MAX_APPS = 4;
    private static final String TAG = "AppRowView";
    private OnAppsViewActionListener mOnAppsViewActionListener;

    public AppRowView(Context context) {
        this(context, null);
    }

    public AppRowView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AppRowView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public AppRowView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void addBannerViews(ArrayList<LaunchItem> items, boolean isOem) {
        for (int i = 0; i < 4; i++) {
            if (i < items.size()) {
                if (!addBannerView((LaunchItem) items.get(i), isOem, i)) {
                    Log.e(TAG, "Unable to add item to row. Maximum amount of items.");
                }
            } else if (i < getChildCount()) {
                getChildAt(i).setVisibility(View.GONE);
            }
        }
    }

    public boolean canAddChild() {
        return getChildCount() < 4;
    }

    public void setOnAppsViewActionListener(OnAppsViewActionListener listener) {
        this.mOnAppsViewActionListener = listener;
    }

    public void setOneTimeFocusPosition(int position) {
        getChildAt(position).requestFocus();
    }

    private boolean addBannerView(LaunchItem item, boolean isOem, int position) {
        if (getChildCount() <= position) {
            return addBannerView(createBannerView(item, isOem));
        }
        AppBannerView bannerView = (AppBannerView) getChildAt(position);
        bannerView.setAppBannerItems(item, isOem, this.mOnAppsViewActionListener);
        bannerView.setVisibility(View.VISIBLE);
        return true;
    }

    private boolean addBannerView(AppBannerView childView) {
        if (getChildCount() >= 4) {
            return false;
        }
        addView(childView);
        return true;
    }

    private AppBannerView createBannerView(LaunchItem item, boolean isOem) {
        AppBannerView appBannerView = (AppBannerView) LayoutInflater.from(getContext()).inflate(R.layout.app_banner, this, false);
        appBannerView.setAppBannerItems(item, isOem, this.mOnAppsViewActionListener);
        return appBannerView;
    }
}