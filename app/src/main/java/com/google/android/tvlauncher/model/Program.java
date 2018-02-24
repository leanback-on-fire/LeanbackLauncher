package com.google.android.tvlauncher.model;

import com.google.android.tvlauncher.notifications.NotificationsContract;

public interface Program {
    public static final int AUTHOR_COLUMN_INDEX = 11;
    public static final int AVAILABILITY_COLUMN_INDEX = 15;
    public static final int CANONICAL_GENRE_COLUMN_INDEX = 20;
    public static final int CHANNEL_ID_COLUMN_INDEX = 1;
    public static final int CONTENT_ID_INDEX = 27;
    public static final int CONTENT_RATING_COLUMN_INDEX = 18;
    public static final int DURATION_COLUMN_INDEX = 21;
    public static final int EPISODE_DISPLAY_NUMBER_COLUMN_INDEX = 24;
    public static final int EPISODE_TITLE_COLUMN_INDEX = 25;
    public static final int ID_COLUMN_INDEX = 0;
    public static final int INTENT_URI_COLUMN_INDEX = 10;
    public static final int INTERACTION_COUNT_COLUMN_INDEX = 14;
    public static final int INTERACTION_TYPE_COLUMN_INDEX = 13;
    public static final int ITEM_COUNT_COLUMN_INDEX = 22;
    public static final int LIVE_COLUMN_INDEX = 30;
    public static final int LOGO_URI_COLUMN_INDEX = 19;
    public static final int OFFER_PRICE_COLUMN_INDEX = 17;
    public static final int PACKAGE_NAME_INDEX = 33;
    public static final int PLAYBACK_POSITION_COLUMN_INDEX = 26;
    public static final int POSTER_ART_ASPECT_RATIO_COLUMN_INDEX = 6;
    public static final int POSTER_ART_URI_COLUMN_INDEX = 5;
    public static final int PREVIEW_VIDEO_COLUMN_INDEX = 9;
    public static final String[] PROJECTION = new String[]{"_id", "channel_id", "type", NotificationsContract.COLUMN_NOTIF_TITLE, "short_description", "poster_art_uri", "poster_art_aspect_ratio", "thumbnail_uri", "poster_thumbnail_aspect_ratio", "preview_video_uri", "intent_uri", "author", "release_date", "interaction_type", "interaction_count", "availability", "starting_price", "offer_price", "content_rating", "logo_uri", "canonical_genre", "duration_millis", "item_count", "season_display_number", "episode_display_number", "episode_title", "last_playback_position_millis", "content_id", "review_rating_style", "review_rating", "live"};
    public static final int RELEASE_DATE_COLUMN_INDEX = 12;
    public static final int REVIEW_RATING_COLUMN_INDEX = 29;
    public static final int REVIEW_RATING_STYLE_COLUMN_INDEX = 28;
    public static final int SEASON_DISPLAY_NUMBER_COLUMN_INDEX = 23;
    public static final int SHORT_DESCRIPTION_COLUMN_INDEX = 4;
    public static final int STARTING_PRICE_COLUMN_INDEX = 16;
    public static final int THUMBNAIL_ASPECT_RATIO_COLUMN_INDEX = 8;
    public static final int THUMBNAIL_URI_COLUMN_INDEX = 7;
    public static final int TITLE_COLUMN_INDEX = 3;
    public static final int TYPE_COLUMN_INDEX = 2;
    public static final int WATCH_NEXT_LAST_ENGAGEMENT_TIME_INDEX = 32;
    public static final String[] WATCH_NEXT_PROJECTION = new String[]{"_id", "channel_id", "type", NotificationsContract.COLUMN_NOTIF_TITLE, "short_description", "poster_art_uri", "poster_art_aspect_ratio", "thumbnail_uri", "poster_thumbnail_aspect_ratio", "preview_video_uri", "intent_uri", "author", "release_date", "interaction_type", "interaction_count", "availability", "starting_price", "offer_price", "content_rating", "logo_uri", "canonical_genre", "duration_millis", "item_count", "season_display_number", "episode_display_number", "episode_title", "last_playback_position_millis", "content_id", "review_rating_style", "review_rating", "live", "watch_next_type", "last_engagement_time_utc_millis", "package_name"};
    public static final int WATCH_NEXT_TYPE_COLUMN_INDEX = 31;

    String getActionUri();

    String getAuthor();

    int getAvailability();

    String getCanonicalGenres();

    long getChannelId();

    String getContentId();

    String getContentRating();

    long getDuration();

    String getEpisodeDisplayNumber();

    String getEpisodeTitle();

    long getId();

    int getInteractionCount();

    int getInteractionType();

    int getItemCount();

    long getLastEngagementTime();

    String getLogoUri();

    String getOfferPrice();

    String getPackageName();

    long getPlaybackPosition();

    int getPreviewImageAspectRatio();

    String getPreviewImageUri();

    String getPreviewVideoUri();

    String getReleaseDate();

    String getReviewRating();

    int getReviewRatingStyle();

    String getSeasonDisplayNumber();

    String getShortDescription();

    String getStartingPrice();

    int getThumbnailAspectRatio();

    String getThumbnailUri();

    String getTitle();

    int getType();

    int getWatchNextType();

    boolean isLive();
}
