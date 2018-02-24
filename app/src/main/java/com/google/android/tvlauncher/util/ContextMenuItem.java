package com.google.android.tvlauncher.util;

import android.graphics.drawable.Drawable;

public class ContextMenuItem {
    private boolean mEnabled = true;
    private Drawable mIcon;
    private int mId;
    private boolean mIsLinkedWithTriangle = false;
    private String mTitle;
    private boolean mVisible = true;

    public boolean isLinkedWithTriangle() {
        return this.mIsLinkedWithTriangle;
    }

    public void setLinkedWithTriangle(boolean linkedWithTriangle) {
        this.mIsLinkedWithTriangle = linkedWithTriangle;
    }

    public ContextMenuItem(int id, String title, Drawable icon) {
        this.mId = id;
        this.mTitle = title;
        this.mIcon = icon;
    }

    public int getId() {
        return this.mId;
    }

    public String getTitle() {
        return this.mTitle;
    }

    public Drawable getIcon() {
        return this.mIcon;
    }

    public boolean isEnabled() {
        return this.mEnabled;
    }

    public boolean isVisible() {
        return this.mVisible;
    }

    public void setEnabled(boolean isEnabled) {
        this.mEnabled = isEnabled;
    }

    public void setVisible(boolean isVisible) {
        this.mVisible = isVisible;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public void setIcon(Drawable icon) {
        this.mIcon = icon;
    }
}
