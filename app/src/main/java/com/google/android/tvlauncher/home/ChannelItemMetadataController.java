package com.google.android.tvlauncher.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.media.tv.TvContentRating;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.PluralsRes;
import android.support.annotation.VisibleForTesting;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.source.chunk.ChunkedTrackBlacklistUtil;
import com.google.android.tvlauncher.R;
import com.google.android.tvlauncher.data.CanonicalGenreUtil;
import com.google.android.tvlauncher.model.Program;
import com.google.android.tvlauncher.util.porting.Unsupported;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class ChannelItemMetadataController {
    @SuppressLint({"SimpleDateFormat"})
    private static final SimpleDateFormat DATETIME_PARSE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    @SuppressLint({"SimpleDateFormat"})
    private static final SimpleDateFormat DATE_PARSE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final boolean DEBUG = false;
    private static final int HOUR_IN_MINUTES = 60;
    private static final int MAX_STAR_COUNT = 5;
    private static final int MINUTE_IN_SECONDS = 60;
    private static final String TAG = "ItemMetadata";
    private final CanonicalGenreUtil mCanonicalGenreUtil;
    private final TextView mContentRating;
    private final Context mContext;
    private final DateFormat mDateFormat;
    // private final MeasureFormat mDurationFormat;
    private final String mEpisodeDisplayNumberFormat;
    private final String mEpisodeTitleAndDescriptionFormat;
    private final String mEpisodeTitleFormat;
    private final TextView mFirstRow;
    private final String mFreeWithSubscriptionText;
    private boolean mLegacy = false;
    private final ImageView mLogo;
    private String mLogoUri;
    private final String mMetadataItemSeparator;
    private final String mMetadataPrefix;
    private final String mMetadataSuffix;
    private final TextView mOldPrice;
    private final TextView mPrice;
    private int mRatingImageSize;
    private final TextView mRatingPercentage;
    private final String mSeasonDisplayNumberFormat;
    private final TextView mSecondRow;
    private ColorStateList mStarActiveTint;
    private ColorStateList mStarInactiveTint;
    private final LinearLayout mStarRating;
    private List<ImageView> mStarViews;
    private final TextView mThirdRow;
    private final int mThirdRowDefaultMaxLines;
    private final TextView mThumbCountDown;
    private final TextView mThumbCountUp;
    private final ImageView mThumbDown;
    private final ImageView mThumbUp;
    private final Pattern mThumbsUpDownRatingPattern;

    static {
        DATETIME_PARSE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    ChannelItemMetadataController(View view) {
        this.mContext = view.getContext();
        this.mFirstRow = (TextView) view.findViewById(R.id.first);
        this.mSecondRow = (TextView) view.findViewById(R.id.second);
        this.mThirdRow = (TextView) view.findViewById(R.id.third);
        this.mThirdRowDefaultMaxLines = this.mThirdRow.getMaxLines();
        this.mThumbCountUp = (TextView) view.findViewById(R.id.thumbCountUp);
        this.mThumbCountDown = (TextView) view.findViewById(R.id.thumbCountDown);
        this.mThumbUp = (ImageView) view.findViewById(R.id.thumbUp);
        this.mThumbDown = (ImageView) view.findViewById(R.id.thumbDown);
        this.mRatingPercentage = (TextView) view.findViewById(R.id.rating_percentage);
        this.mStarRating = (LinearLayout) view.findViewById(R.id.star_rating);
        this.mOldPrice = (TextView) view.findViewById(R.id.old_price);
        this.mPrice = (TextView) view.findViewById(R.id.price);
        this.mContentRating = (TextView) view.findViewById(R.id.content_rating);
        this.mLogo = (ImageView) view.findViewById(R.id.program_logo);
        this.mOldPrice.setPaintFlags(this.mOldPrice.getPaintFlags() | 16);
        this.mMetadataItemSeparator = this.mContext.getString(R.string.program_metadata_item_separator);
        this.mMetadataPrefix = this.mContext.getString(R.string.program_metadata_prefix);
        this.mMetadataSuffix = this.mContext.getString(R.string.program_metadata_suffix);
        this.mFreeWithSubscriptionText = this.mContext.getString(R.string.program_availability_free_with_subscription);
        this.mSeasonDisplayNumberFormat = this.mContext.getString(R.string.program_season_display_number);
        this.mEpisodeDisplayNumberFormat = this.mContext.getString(R.string.program_episode_display_number);
        this.mEpisodeTitleFormat = this.mContext.getString(R.string.program_episode_title);
        this.mEpisodeTitleAndDescriptionFormat = this.mContext.getString(R.string.program_episode_title_and_short_description);
        this.mDateFormat = android.text.format.DateFormat.getLongDateFormat(this.mContext);
        //this.mDurationFormat = MeasureFormat.getInstance(Locale.getDefault(), FormatWidth.NARROW);
        this.mCanonicalGenreUtil = new CanonicalGenreUtil(this.mContext);
        this.mThumbsUpDownRatingPattern = Pattern.compile("^(\\d+),(\\d+)$");
    }

    void bindView(@NonNull Program program) {
        CharSequence third;
        String first = program.getTitle();
        String second = null;
        String price = null;
        String oldPrice = null;
        String contentRating = null;
        this.mLogoUri = null;
        if (this.mLegacy) {
            third = program.getShortDescription();
            this.mLogoUri = program.getLogoUri();
        } else {
            int type = program.getType();
            if (type == 4 || type == 10) {
                this.mLogoUri = program.getLogoUri();
            }
            if (type == 4 || type == 0 || type == 1 || type == 2 || type == 3 || type == 6 || type == 5) {
                contentRating = parseContentRating(program.getContentRating());
            }
            second = generateSecondRow(program);
            int availability = program.getAvailability();
            if (availability == 1) {
                price = this.mFreeWithSubscriptionText;
            } else if (availability == 2) {
                oldPrice = program.getStartingPrice();
                price = program.getOfferPrice();
                if (price == null) {
                    price = oldPrice;
                    oldPrice = null;
                }
            }
            third = generateThirdRow(program);
        }
        this.mFirstRow.setText(safeTrim(first));
        this.mSecondRow.setText(safeTrim(second));
        this.mThirdRow.setText(safeTrim(third));
        this.mThirdRow.setMaxLines(this.mThirdRowDefaultMaxLines);
        updateRatingSystem(program.getReviewRating(), program.getReviewRatingStyle());
        this.mPrice.setText(safeTrim(price));
        this.mOldPrice.setText(safeTrim(oldPrice));
        this.mContentRating.setText(safeTrim(contentRating));
        if (this.mLogoUri != null) {
            Glide.with(this.mContext).load(this.mLogoUri).into(this.mLogo);
        }
        updateVisibility();
    }

    private void initializeStarRatingSystem() {
        this.mStarActiveTint = ColorStateList.valueOf(ContextCompat.getColor(this.mContext, R.color.program_meta_rating_active_color));
        this.mStarInactiveTint = ColorStateList.valueOf(ContextCompat.getColor(this.mContext, R.color.program_meta_rating_inactive_color));
        this.mRatingImageSize = this.mContext.getResources().getDimensionPixelSize(R.dimen.program_meta_rating_size);
        this.mStarViews = new ArrayList<>(5);
        for (int i = 0; i < 5; i++) {
            ImageView star = new ImageView(this.mContext);
            star.setImageDrawable(this.mContext.getDrawable(R.drawable.ic_channel_view_filled_star_black));
            this.mStarViews.add(star);
        }
    }

    private void updateRatingSystem(String rating, int ratingStyle) {
        this.mStarRating.removeAllViews();
        this.mThumbCountUp.setText(null);
        this.mThumbCountDown.setText(null);
        this.mRatingPercentage.setText(null);
        if (!this.mLegacy && rating != null) {
            switch (ratingStyle) {
                case 0:
                    try {
                        if (this.mStarViews == null) {
                            initializeStarRatingSystem();
                        }
                        int roundUpScore = Math.round(Float.parseFloat(rating));
                        int i = 0;
                        while (i < 5) {
                            ((ImageView) this.mStarViews.get(i)).setImageTintList(i < roundUpScore ? this.mStarActiveTint : this.mStarInactiveTint);
                            this.mStarRating.addView((View) this.mStarViews.get(i), this.mRatingImageSize, this.mRatingImageSize);
                            i++;
                        }
                        return;
                    } catch (NumberFormatException e) {
                        return;
                    }
                case 1:
                    Matcher matcher = this.mThumbsUpDownRatingPattern.matcher(rating);
                    if (matcher.find() && matcher.groupCount() == 2) {
                        long upCount = Long.parseLong(matcher.group(1));
                        long downCount = Long.parseLong(matcher.group(2));
                        NumberFormat numberFormat = NumberFormat.getInstance();
                        this.mThumbCountUp.setText(numberFormat.format(upCount));
                        this.mThumbCountDown.setText(numberFormat.format(downCount));
                        return;
                    }
                    return;
                case 2:
                    try {
                        float percentage = Float.parseFloat(rating) / 100.0f;
                        NumberFormat percentageFormat = NumberFormat.getPercentInstance();
                        if (rating.indexOf(46) == -1) {
                            percentageFormat.setMaximumFractionDigits(0);
                        } else {
                            percentageFormat.setMaximumFractionDigits(1);
                        }
                        this.mRatingPercentage.setText(percentageFormat.format((double) percentage));
                        return;
                    } catch (NumberFormatException e2) {
                        return;
                    }
                default:
                    return;
            }
        }
    }

    @Nullable
    @Unsupported
    private String parseContentRating(@Nullable String ratingString) {
        return null;
    }

    @Nullable
    private CharSequence safeTrim(@Nullable CharSequence string) {
        if (string instanceof String) {
            return ((String) string).trim();
        }
        return string;
    }

    @Nullable
    private String generateSecondRow(@NonNull Program program) {
        int type = program.getType();
        String seasonDisplayNumber = program.getSeasonDisplayNumber();
        StringBuilder sb = new StringBuilder(150);
        int interactionCount;
        if (type == 4) {
            appendNonEmptyMetadataItem(sb, program.getAuthor());
            appendNonEmptyMetadataItem(sb, formatReleaseDate(program.getReleaseDate()));
            interactionCount = program.getInteractionCount();
            if (interactionCount > 0) {
                appendNonEmptyMetadataItem(sb, formatInteractions(program.getInteractionType(), interactionCount));
            }
        } else if (type == 0) {
            appendNonEmptyMetadataItem(sb, formatReleaseDate(program.getReleaseDate()));
            appendNonEmptyMetadataItem(sb, this.mCanonicalGenreUtil.decodeGenres(program.getCanonicalGenres()));
            appendNonEmptyMetadataItem(sb, formatDurationInHoursAndMinutes(program.getDuration()));
        } else if (type == 1) {
            appendNonEmptyMetadataItem(sb, formatReleaseDate(program.getReleaseDate()));
            appendNonEmptyMetadataItem(sb, this.mCanonicalGenreUtil.decodeGenres(program.getCanonicalGenres()));
            int numberOfSeasons = program.getItemCount();
            if (numberOfSeasons > 0) {
                appendNonEmptyMetadataItem(sb, formatQuantity(R.plurals.program_number_of_seasons, numberOfSeasons));
            }
        } else if (type == 2) {
            appendNonEmptyMetadataItem(sb, formatReleaseDate(program.getReleaseDate()));
            if (!TextUtils.isEmpty(program.getSeasonDisplayNumber())) {
                appendNonEmptyMetadataItem(sb, String.format(Locale.getDefault(), this.mSeasonDisplayNumberFormat, seasonDisplayNumber));
            }
            int numberOfEpisodes = program.getItemCount();
            if (numberOfEpisodes > 0) {
                appendNonEmptyMetadataItem(sb, formatQuantity(R.plurals.program_number_of_episodes, numberOfEpisodes));
            }
            appendNonEmptyMetadataItem(sb, this.mCanonicalGenreUtil.decodeGenres(program.getCanonicalGenres()));
        } else if (type == 3) {
            appendNonEmptyMetadataItem(sb, formatReleaseDate(program.getReleaseDate()));
            if (!TextUtils.isEmpty(program.getSeasonDisplayNumber())) {
                appendNonEmptyMetadataItem(sb, String.format(Locale.getDefault(), this.mSeasonDisplayNumberFormat, seasonDisplayNumber));
            }
            if (!TextUtils.isEmpty(program.getEpisodeDisplayNumber())) {
                appendNonEmptyMetadataItem(sb, String.format(Locale.getDefault(), this.mEpisodeDisplayNumberFormat, program.getEpisodeDisplayNumber()));
            }
            appendNonEmptyMetadataItem(sb, formatDurationInHoursAndMinutes(program.getDuration()));
            appendNonEmptyMetadataItem(sb, this.mCanonicalGenreUtil.decodeGenres(program.getCanonicalGenres()));
        } else if (type == 5) {
            appendNonEmptyMetadataItem(sb, formatReleaseDate(program.getReleaseDate()));
            appendNonEmptyMetadataItem(sb, formatDurationInHoursAndMinutes(program.getDuration()));
            interactionCount = program.getInteractionCount();
            if (interactionCount > 0) {
                appendNonEmptyMetadataItem(sb, formatInteractions(program.getInteractionType(), interactionCount));
            }
        } else if (type == 7) {
            appendNonEmptyMetadataItem(sb, program.getAuthor());
            appendNonEmptyMetadataItem(sb, formatDurationInHoursMinutesAndSeconds(program.getDuration()));
        } else if (type == 8 || type == 10) {
            appendNonEmptyMetadataItem(sb, program.getAuthor());
            int numberOfTracks = program.getItemCount();
            if (numberOfTracks > 0) {
                appendNonEmptyMetadataItem(sb, formatQuantity(R.plurals.program_number_of_tracks, numberOfTracks));
            }
        } else if (type == 11) {
            appendNonEmptyMetadataItem(sb, program.getAuthor());
        }
        if (sb.length() <= 0) {
            return null;
        }
        sb.append(this.mMetadataSuffix);
        return sb.toString();
    }

    @Nullable
    private CharSequence generateThirdRow(@NonNull Program program) {
        int type = program.getType();
        StringBuilder sb = new StringBuilder(150);
        if (type == 3) {
            return formatTvEpisodeTitleAndDescription(program);
        }
        if (type == 7 || type == 8) {
            appendNonEmptyMetadataItem(sb, formatReleaseDate(program.getReleaseDate()));
            appendNonEmptyMetadataItem(sb, program.getShortDescription());
        } else if (type == 9 || type == 10 || type == 11) {
            int interactionCount = program.getInteractionCount();
            if (interactionCount > 0) {
                appendNonEmptyMetadataItem(sb, formatInteractions(program.getInteractionType(), interactionCount));
            }
            appendNonEmptyMetadataItem(sb, program.getShortDescription());
        }
        if (sb.length() <= 0) {
            return program.getShortDescription();
        }
        sb.append(this.mMetadataSuffix);
        return sb.toString();
    }

    @Nullable
    private CharSequence formatTvEpisodeTitleAndDescription(@NonNull Program program) {
        CharSequence episodeTitle = safeTrim(program.getEpisodeTitle());
        CharSequence shortDescription = safeTrim(program.getShortDescription());
        if (TextUtils.isEmpty(episodeTitle)) {
            return shortDescription;
        }
        if (TextUtils.isEmpty(shortDescription)) {
            return Html.fromHtml(String.format(Locale.getDefault(), this.mEpisodeTitleFormat, episodeTitle)); // , 0
        }
        return Html.fromHtml(String.format(Locale.getDefault(), this.mEpisodeTitleAndDescriptionFormat, episodeTitle, shortDescription)); // , 0
    }

    private void appendNonEmptyMetadataItem(StringBuilder sb, CharSequence item) {
        item = safeTrim(item);
        if (!TextUtils.isEmpty(item)) {
            if (sb.length() > 0) {
                sb.append(this.mMetadataItemSeparator);
            } else {
                sb.append(this.mMetadataPrefix);
            }
            sb.append(item);
        }
    }

    private CharSequence formatReleaseDate(String releaseDate) {
        if (releaseDate == null) {
            return null;
        }
        try {
            if (releaseDate.length() == 4) {
                int releaseYear = Integer.parseInt(releaseDate);
                return String.format(Locale.getDefault(), "%d", releaseYear);
            } else if (releaseDate.length() == 10) {
                return this.mDateFormat.format(DATE_PARSE_FORMAT.parse(releaseDate));
            } else {
                if (releaseDate.length() == 20) {
                    return DateUtils.getRelativeTimeSpanString(DATETIME_PARSE_FORMAT.parse(releaseDate).getTime(), System.currentTimeMillis(), 0, 0);
                }
                return null;
            }
        } catch (NumberFormatException | ParseException ignored) {
        }

        return null;
    }

    @Nullable
    @VisibleForTesting(otherwise = 2)
    CharSequence formatDurationInHoursAndMinutes(long milliseconds) {
        long totalMinutes = milliseconds / ChunkedTrackBlacklistUtil.DEFAULT_TRACK_BLACKLIST_MS;
        if (totalMinutes >= 60) {
            long hours = totalMinutes / 60;
            if (hours > 23) {
                return null;
            }
            if (totalMinutes - (60 * hours) <= 0) {
                return "" + hours; // this.mDurationFormat.format(new Measure(Long.valueOf(hours), MeasureUnit.HOUR));
            }
            // todo fix this
            long minutes = totalMinutes - (60 * hours);
            return hours + ":" + minutes; //this.mDurationFormat.formatMeasures(new Measure[]{new Measure(Long.valueOf(hours), MeasureUnit.HOUR), new Measure(Long.valueOf(minutes), MeasureUnit.MINUTE)});
        } else if (totalMinutes > 0) {
            return "00:" + totalMinutes;// this.mDurationFormat.format(new Measure(Long.valueOf(totalMinutes), MeasureUnit.MINUTE));
        } else {
            if (milliseconds > 0) {
                //    return this.mDurationFormat.format(new Measure(Integer.valueOf(1), MeasureUnit.MINUTE));
                return "00:01";
            }
            return null;
        }
    }

    @Nullable
    @VisibleForTesting(otherwise = 2)
    CharSequence formatDurationInHoursMinutesAndSeconds(long milliseconds) {
        return formatDurationInHoursAndMinutes(milliseconds);
        // todo fix this
        /*
        long totalSeconds = milliseconds / 1000;
        if (totalSeconds >= 3600) {
            return formatDurationInHoursAndMinutes(milliseconds);
        }
        if (totalSeconds >= 60) {
            long minutes = totalSeconds / 60;
            if (totalSeconds - (minutes * 60) <= 0) {
                return this.mDurationFormat.format(new Measure(Long.valueOf(minutes), MeasureUnit.MINUTE));
            }
            return this.mDurationFormat.formatMeasures(new Measure[]{new Measure(Long.valueOf(minutes), MeasureUnit.MINUTE), new Measure(Long.valueOf(seconds), MeasureUnit.SECOND)});
        } else if (totalSeconds > 0) {
            return this.mDurationFormat.format(new Measure(Long.valueOf(totalSeconds), MeasureUnit.SECOND));
        } else {
            if (milliseconds > 0) {
                return this.mDurationFormat.format(new Measure(Integer.valueOf(1), MeasureUnit.SECOND));
            }
            return null;
        }*/
    }

    private CharSequence formatInteractions(int type, int count) {
        int stringId;
        switch (type) {
            case 0:
                stringId = R.plurals.program_interactions_views;
                break;
            case 1:
                stringId = R.plurals.program_interactions_listens;
                break;
            case 2:
                stringId = R.plurals.program_interactions_followers;
                break;
            case 3:
                stringId = R.plurals.program_interactions_fans;
                break;
            case 4:
                stringId = R.plurals.program_interactions_likes;
                break;
            case 5:
                stringId = R.plurals.program_interactions_thumbs;
                break;
            case 6:
                stringId = R.plurals.program_interactions_viewers;
                break;
            default:
                stringId = 0;
                break;
        }
        if (stringId == 0) {
            return null;
        }
        return this.mContext.getResources().getQuantityString(stringId, count, new Object[]{Integer.valueOf(count)});
    }

    private CharSequence formatQuantity(@PluralsRes int formatResId, int count) {
        return this.mContext.getResources().getQuantityString(formatResId, count, new Object[]{Integer.valueOf(count)});
    }

    void setFirstRow(String firstRow) {
        this.mFirstRow.setText(firstRow);
        updateVisibility();
    }

    void setSecondRow(String secondRow) {
        this.mSecondRow.setText(secondRow);
        updateVisibility();
    }

    void setThirdRow(String thirdRow, int maxLines) {
        this.mThirdRow.setMaxLines(maxLines);
        setThirdRow(thirdRow);
    }

    private void setThirdRow(String thirdRow) {
        this.mThirdRow.setText(thirdRow);
        updateVisibility();
    }

    public void clear() {
        this.mFirstRow.setText(null);
        this.mSecondRow.setText(null);
        this.mThumbCountUp.setText(null);
        this.mThumbCountDown.setText(null);
        this.mRatingPercentage.setText(null);
        this.mStarRating.removeAllViews();
        this.mThirdRow.setText(null);
        this.mPrice.setText(null);
        this.mOldPrice.setText(null);
        this.mContentRating.setText(null);
        this.mLogoUri = null;
        updateVisibility();
    }

    void setLegacy(boolean legacy) {
        this.mLegacy = legacy;
    }

    private void updateVisibility() {
        boolean z;
        boolean z2 = true;
        setVisibility(this.mFirstRow, this.mFirstRow.length() != 0);
        View view = this.mSecondRow;
        if (this.mSecondRow.length() != 0) {
            z = true;
        } else {
            z = false;
        }
        setVisibility(view, z);
        view = this.mThirdRow;
        if (this.mThirdRow.length() != 0) {
            z = true;
        } else {
            z = false;
        }
        setVisibility(view, z);
        view = this.mStarRating;
        if (this.mStarRating.getChildCount() != 0) {
            z = true;
        } else {
            z = false;
        }
        setVisibility(view, z);
        view = this.mThumbCountUp;
        if (this.mThumbCountUp.length() != 0) {
            z = true;
        } else {
            z = false;
        }
        setVisibility(view, z);
        view = this.mThumbCountDown;
        if (this.mThumbCountDown.length() != 0) {
            z = true;
        } else {
            z = false;
        }
        setVisibility(view, z);
        view = this.mThumbUp;
        if (this.mThumbCountUp.length() != 0) {
            z = true;
        } else {
            z = false;
        }
        setVisibility(view, z);
        view = this.mThumbDown;
        if (this.mThumbCountDown.length() != 0) {
            z = true;
        } else {
            z = false;
        }
        setVisibility(view, z);
        view = this.mRatingPercentage;
        if (this.mRatingPercentage.length() != 0) {
            z = true;
        } else {
            z = false;
        }
        setVisibility(view, z);
        view = this.mPrice;
        if (this.mPrice.length() != 0) {
            z = true;
        } else {
            z = false;
        }
        setVisibility(view, z);
        view = this.mOldPrice;
        if (this.mOldPrice.length() != 0) {
            z = true;
        } else {
            z = false;
        }
        setVisibility(view, z);
        view = this.mContentRating;
        if (this.mContentRating.length() != 0) {
            z = true;
        } else {
            z = false;
        }
        setVisibility(view, z);
        View view2 = this.mLogo;
        if (this.mLogoUri == null) {
            z2 = false;
        }
        setVisibility(view2, z2);
    }

    private void setVisibility(View view, boolean visible) {
        boolean oldVisible;
        int i = 0;
        if (view.getVisibility() == View.VISIBLE) {
            oldVisible = true;
        } else {
            oldVisible = false;
        }
        if (oldVisible != visible) {
            if (!visible) {
                i = 8;
            }
            view.setVisibility(i);
        }
    }
}
