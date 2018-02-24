package com.google.android.tvlauncher.data;

import com.google.android.tvlauncher.R;
import com.google.android.tvlauncher.model.Program;

class ProgramRef extends DataBufferRef implements Program {
    ProgramRef(AbstractDataBuffer paramAbstractDataBuffer, int paramInt) {
        super(paramAbstractDataBuffer, paramInt);
    }

    public String getActionUri() {
        return getString(10);
    }

    public String getAuthor() {
        return getString(11);
    }

    public int getAvailability() {
        return getInt(15);
    }

    public String getCanonicalGenres() {
        return getString(20);
    }

    public long getChannelId() {
        return getLong(1);
    }

    public String getContentId() {
        return getString(27);
    }

    public String getContentRating() {
        return getString(18);
    }

    public long getDuration() {
        return getLong(21);
    }

    public String getEpisodeDisplayNumber() {
        return getString(24);
    }

    public String getEpisodeTitle() {
        return getString(25);
    }

    public long getId() {
        return getLong(0);
    }

    public int getInteractionCount() {
        return getInt(14);
    }

    public int getInteractionType() {
        return getInt(13);
    }

    public int getItemCount() {
        return getInt(22);
    }

    public long getLastEngagementTime() {
        return getLong(32);
    }

    public String getLogoUri() {
        return getString(19);
    }

    public String getOfferPrice() {
        return getString(17);
    }

    public String getPackageName() {
        return getString(33);
    }

    public long getPlaybackPosition() {
        return getLong(26);
    }

    public int getPreviewImageAspectRatio() {
        return getInt(6);
    }

    public String getPreviewImageUri() {
        return getString(5);
    }

    public String getPreviewVideoUri() {
        return getString(9);
    }

    public String getReleaseDate() {
        return getString(12);
    }

    public String getReviewRating() {
        return getString(29);
    }

    public int getReviewRatingStyle() {
        return getInt(28);
    }

    public String getSeasonDisplayNumber() {
        return getString(23);
    }

    public String getShortDescription() {
        return getString(4);
    }

    public String getStartingPrice() {
        return getString(16);
    }

    public int getThumbnailAspectRatio() {
        return getInt(8);
    }

    public String getThumbnailUri() {
        return getString(R.attr.controller_layout_id);
    }

    public String getTitle() {
        return getString(3);
    }

    public int getType() {
        return getInt(2);
    }

    public int getWatchNextType() {
        return getInt(31);
    }

    public boolean isLive() {
        return getInt(30) == 1;
    }

    public String toString() {
        return "Program{id=" + getId() + ", channelId=" + getChannelId() + ", title='" + getTitle() + '\'' + ", previewImageUri='" + getPreviewImageUri() + '\'' + ", previewVideoUri='" + getPreviewVideoUri() + '\'' + '}';
    }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/data/ProgramRef.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */