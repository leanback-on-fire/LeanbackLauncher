package com.google.android.tvlauncher.home.util;

public class ChannelStateSettings {
    private int mChannelLogoAlignmentOriginMargin;
    private int mItemHeight;
    private int mItemMarginBottom;
    private int mItemMarginTop;
    private int mMarginBottom;
    private int mMarginTop;

    static class Builder {
        private int mChannelLogoAlignmentOriginMargin;
        private int mItemHeight;
        private int mItemMarginBottom;
        private int mItemMarginTop;
        private int mMarginBottom;
        private int mMarginTop;

        Builder() {
        }

        Builder setItemHeight(int itemHeight) {
            this.mItemHeight = itemHeight;
            return this;
        }

        Builder setItemMarginTop(int itemMarginTop) {
            this.mItemMarginTop = itemMarginTop;
            return this;
        }

        Builder setItemMarginBottom(int itemMarginBottom) {
            this.mItemMarginBottom = itemMarginBottom;
            return this;
        }

        Builder setMarginTop(int marginTop) {
            this.mMarginTop = marginTop;
            return this;
        }

        Builder setMarginBottom(int marginBottom) {
            this.mMarginBottom = marginBottom;
            return this;
        }

        Builder setChannelLogoAlignmentOriginMargin(int channelLogoAlignmentOriginMargin) {
            this.mChannelLogoAlignmentOriginMargin = channelLogoAlignmentOriginMargin;
            return this;
        }

        ChannelStateSettings build() {
            return new ChannelStateSettings(this.mItemHeight, this.mItemMarginTop, this.mItemMarginBottom, this.mMarginTop, this.mMarginBottom, this.mChannelLogoAlignmentOriginMargin);
        }
    }

    private ChannelStateSettings(int itemHeight, int itemMarginTop, int itemMarginBottom, int marginTop, int marginBottom, int channelLogoAlignmentOriginMargin) {
        this.mItemHeight = itemHeight;
        this.mItemMarginTop = itemMarginTop;
        this.mItemMarginBottom = itemMarginBottom;
        this.mMarginTop = marginTop;
        this.mMarginBottom = marginBottom;
        this.mChannelLogoAlignmentOriginMargin = channelLogoAlignmentOriginMargin;
    }

    ChannelStateSettings(ChannelStateSettings copy) {
        this.mItemHeight = copy.getItemHeight();
        this.mItemMarginTop = copy.getItemMarginTop();
        this.mItemMarginBottom = copy.getItemMarginBottom();
        this.mMarginTop = copy.getMarginTop();
        this.mMarginBottom = copy.getMarginBottom();
        this.mChannelLogoAlignmentOriginMargin = copy.getChannelLogoAlignmentOriginMargin();
    }

    public int getItemHeight() {
        return this.mItemHeight;
    }

    public int getItemMarginTop() {
        return this.mItemMarginTop;
    }

    public int getItemMarginBottom() {
        return this.mItemMarginBottom;
    }

    public int getMarginTop() {
        return this.mMarginTop;
    }

    public int getMarginBottom() {
        return this.mMarginBottom;
    }

    public int getChannelLogoAlignmentOriginMargin() {
        return this.mChannelLogoAlignmentOriginMargin;
    }

    void setItemMarginBottom(int itemMarginBottom) {
        this.mItemMarginBottom = itemMarginBottom;
    }

    void setChannelLogoAlignmentOriginMargin(int channelLogoAlignmentOriginMargin) {
        this.mChannelLogoAlignmentOriginMargin = channelLogoAlignmentOriginMargin;
    }
}
