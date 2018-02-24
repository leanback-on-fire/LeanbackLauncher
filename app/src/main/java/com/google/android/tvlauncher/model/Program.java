package com.google.android.tvlauncher.model;

public abstract interface Program
{
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
  public static final String[] PROJECTION = { "_id", "channel_id", "type", "title", "short_description", "poster_art_uri", "poster_art_aspect_ratio", "thumbnail_uri", "poster_thumbnail_aspect_ratio", "preview_video_uri", "intent_uri", "author", "release_date", "interaction_type", "interaction_count", "availability", "starting_price", "offer_price", "content_rating", "logo_uri", "canonical_genre", "duration_millis", "item_count", "season_display_number", "episode_display_number", "episode_title", "last_playback_position_millis", "content_id", "review_rating_style", "review_rating", "live" };
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
  public static final String[] WATCH_NEXT_PROJECTION = { "_id", "channel_id", "type", "title", "short_description", "poster_art_uri", "poster_art_aspect_ratio", "thumbnail_uri", "poster_thumbnail_aspect_ratio", "preview_video_uri", "intent_uri", "author", "release_date", "interaction_type", "interaction_count", "availability", "starting_price", "offer_price", "content_rating", "logo_uri", "canonical_genre", "duration_millis", "item_count", "season_display_number", "episode_display_number", "episode_title", "last_playback_position_millis", "content_id", "review_rating_style", "review_rating", "live", "watch_next_type", "last_engagement_time_utc_millis", "package_name" };
  public static final int WATCH_NEXT_TYPE_COLUMN_INDEX = 31;
  
  public abstract String getActionUri();
  
  public abstract String getAuthor();
  
  public abstract int getAvailability();
  
  public abstract String getCanonicalGenres();
  
  public abstract long getChannelId();
  
  public abstract String getContentId();
  
  public abstract String getContentRating();
  
  public abstract long getDuration();
  
  public abstract String getEpisodeDisplayNumber();
  
  public abstract String getEpisodeTitle();
  
  public abstract long getId();
  
  public abstract int getInteractionCount();
  
  public abstract int getInteractionType();
  
  public abstract int getItemCount();
  
  public abstract long getLastEngagementTime();
  
  public abstract String getLogoUri();
  
  public abstract String getOfferPrice();
  
  public abstract String getPackageName();
  
  public abstract long getPlaybackPosition();
  
  public abstract int getPreviewImageAspectRatio();
  
  public abstract String getPreviewImageUri();
  
  public abstract String getPreviewVideoUri();
  
  public abstract String getReleaseDate();
  
  public abstract String getReviewRating();
  
  public abstract int getReviewRatingStyle();
  
  public abstract String getSeasonDisplayNumber();
  
  public abstract String getShortDescription();
  
  public abstract String getStartingPrice();
  
  public abstract int getThumbnailAspectRatio();
  
  public abstract String getThumbnailUri();
  
  public abstract String getTitle();
  
  public abstract int getType();
  
  public abstract int getWatchNextType();
  
  public abstract boolean isLive();
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/model/Program.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */