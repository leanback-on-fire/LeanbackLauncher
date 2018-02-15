package com.rockon999.android.leanbacklauncher;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.AdapterDataObserver;
import android.view.View;

public class HomeScreenRow extends AdapterDataObserver {
    private Adapter<?> mAdapter;
    private String mFontName;
    private boolean mHasHeader;
    private final boolean mHideIfEmpty;
    private final int mHomeScreenPosition;
    private Drawable mIcon;
    private RowChangeListener mListener;
    private int mRowScrollOffset;
    private View mRowView;
    private String mTitle;
    private final int mType;
    private boolean mVisible;

    public interface RowChangeListener {
        void onRowVisibilityChanged(int i, boolean z);
    }

    HomeScreenRow(int type, int position, boolean hideIfEmpty) {
        this.mType = type;
        this.mHomeScreenPosition = position;
        this.mHideIfEmpty = hideIfEmpty;
        this.mVisible = isVisible();
        this.mRowView = null;
    }

    public int getType() {
        return this.mType;
    }

    public int getPosition() {
        return this.mHomeScreenPosition;
    }

    public void setHeaderInfo(boolean hasHeader, String title, Drawable icon, String fontName) {
        this.mHasHeader = hasHeader;
        if (hasHeader) {
            this.mTitle = title;
            this.mFontName = fontName;
            this.mIcon = icon;
            return;
        }
        this.mTitle = null;
    }

    public boolean hasHeader() {
        return this.mHasHeader;
    }

    public String getTitle() {
        return this.mTitle;
    }

    public String getFontName() {
        return this.mFontName;
    }

    public Drawable getIcon() {
        return this.mIcon;
    }

    public void setAdapter(Adapter<?> adapter) {
        if (adapter != null) {
            if (this.mHideIfEmpty && this.mAdapter != null) {
                this.mAdapter.unregisterAdapterDataObserver(this);
            }
            this.mAdapter = adapter;
            this.mVisible = isVisible();
            if (this.mHideIfEmpty && this.mAdapter != null) {
                this.mAdapter.registerAdapterDataObserver(this);
            }
        }
    }

    public Adapter<?> getAdapter() {
        return this.mAdapter;
    }

    public void setViewScrollOffset(int size) {
        this.mRowScrollOffset = size;
    }

    public int getRowScrollOffset() {
        return this.mRowScrollOffset;
    }

    public void setChangeListener(RowChangeListener listener) {
        this.mListener = listener;
    }

    public View getRowView() {
        return this.mRowView;
    }

    public void setRowView(View view) {
        this.mRowView = view;
    }

    public boolean isVisible() {
        return !this.mHideIfEmpty || this.mAdapter != null && this.mAdapter.getItemCount() > 0;
    }

    public void onItemRangeRemoved(int positionStart, int itemCount) {
        onChanged();
    }

    public void onItemRangeChanged(int positionStart, int itemCount) {
        onChanged();
    }

    public void onItemRangeInserted(int positionStart, int itemCount) {
        onChanged();
    }

    public void onChanged() {
        if (this.mVisible != isVisible()) {
            this.mVisible = !this.mVisible;
            if (this.mListener != null) {
                this.mListener.onRowVisibilityChanged(this.mHomeScreenPosition, this.mVisible);
            }
        }
    }
}
