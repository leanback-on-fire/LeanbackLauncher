package com.google.android.tvlauncher.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.icu.text.MeasureFormat;
import android.icu.text.MeasureFormat.FormatWidth;
import android.icu.util.Measure;
import android.icu.util.MeasureUnit;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.PluralsRes;
import android.support.annotation.VisibleForTesting;
import android.text.Html;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.google.android.tvlauncher.data.CanonicalGenreUtil;
import com.google.android.tvlauncher.home.contentrating.ContentRatingsManager;
import com.google.android.tvlauncher.home.contentrating.ContentRatingsUtil;
import com.google.android.tvlauncher.model.Program;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Pattern;

class ChannelItemMetadataController
{
  @SuppressLint({"SimpleDateFormat"})
  private static final SimpleDateFormat DATETIME_PARSE_FORMAT;
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
  private final java.text.DateFormat mDateFormat;
  private final MeasureFormat mDurationFormat;
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
  
  static
  {
    DATETIME_PARSE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    DATETIME_PARSE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
  }
  
  ChannelItemMetadataController(View paramView)
  {
    this.mContext = paramView.getContext();
    this.mFirstRow = ((TextView)paramView.findViewById(R.id.first));
    this.mSecondRow = ((TextView)paramView.findViewById(R.id.second));
    this.mThirdRow = ((TextView)paramView.findViewById(R.id.third));
    this.mThirdRowDefaultMaxLines = this.mThirdRow.getMaxLines();
    this.mThumbCountUp = ((TextView)paramView.findViewById(R.id.thumbCountUp));
    this.mThumbCountDown = ((TextView)paramView.findViewById(R.id.thumbCountDown));
    this.mThumbUp = ((ImageView)paramView.findViewById(R.id.thumbUp));
    this.mThumbDown = ((ImageView)paramView.findViewById(R.id.thumbDown));
    this.mRatingPercentage = ((TextView)paramView.findViewById(R.id.rating_percentage));
    this.mStarRating = ((LinearLayout)paramView.findViewById(R.id.star_rating));
    this.mOldPrice = ((TextView)paramView.findViewById(R.id.old_price));
    this.mPrice = ((TextView)paramView.findViewById(R.id.price));
    this.mContentRating = ((TextView)paramView.findViewById(R.id.content_rating));
    this.mLogo = ((ImageView)paramView.findViewById(R.id.program_logo));
    this.mOldPrice.setPaintFlags(this.mOldPrice.getPaintFlags() | 0x10);
    this.mMetadataItemSeparator = this.mContext.getString(R.string.program_metadata_item_separator);
    this.mMetadataPrefix = this.mContext.getString(R.string.program_metadata_prefix);
    this.mMetadataSuffix = this.mContext.getString(R.string.program_metadata_suffix);
    this.mFreeWithSubscriptionText = this.mContext.getString(R.string.program_availability_free_with_subscription);
    this.mSeasonDisplayNumberFormat = this.mContext.getString(R.string.program_season_display_number);
    this.mEpisodeDisplayNumberFormat = this.mContext.getString(R.string.program_episode_display_number);
    this.mEpisodeTitleFormat = this.mContext.getString(R.string.program_episode_title);
    this.mEpisodeTitleAndDescriptionFormat = this.mContext.getString(R.string.program_episode_title_and_short_description);
    this.mDateFormat = android.text.format.DateFormat.getLongDateFormat(this.mContext);
    this.mDurationFormat = MeasureFormat.getInstance(Locale.getDefault(), MeasureFormat.FormatWidth.NARROW);
    this.mCanonicalGenreUtil = new CanonicalGenreUtil(this.mContext);
    this.mThumbsUpDownRatingPattern = Pattern.compile("^(\\d+),(\\d+)$");
  }
  
  private void appendNonEmptyMetadataItem(StringBuilder paramStringBuilder, CharSequence paramCharSequence)
  {
    paramCharSequence = safeTrim(paramCharSequence);
    if (!TextUtils.isEmpty(paramCharSequence))
    {
      if (paramStringBuilder.length() <= 0) {
        break label36;
      }
      paramStringBuilder.append(this.mMetadataItemSeparator);
    }
    for (;;)
    {
      paramStringBuilder.append(paramCharSequence);
      return;
      label36:
      paramStringBuilder.append(this.mMetadataPrefix);
    }
  }
  
  private CharSequence formatInteractions(int paramInt1, int paramInt2)
  {
    switch (paramInt1)
    {
    default: 
      paramInt1 = 0;
    }
    while (paramInt1 != 0)
    {
      return this.mContext.getResources().getQuantityString(paramInt1, paramInt2, new Object[] { Integer.valueOf(paramInt2) });
      paramInt1 = 2131427336;
      continue;
      paramInt1 = 2131427333;
      continue;
      paramInt1 = 2131427331;
      continue;
      paramInt1 = 2131427330;
      continue;
      paramInt1 = 2131427332;
      continue;
      paramInt1 = 2131427334;
      continue;
      paramInt1 = 2131427335;
    }
    return null;
  }
  
  private CharSequence formatQuantity(@PluralsRes int paramInt1, int paramInt2)
  {
    return this.mContext.getResources().getQuantityString(paramInt1, paramInt2, new Object[] { Integer.valueOf(paramInt2) });
  }
  
  private CharSequence formatReleaseDate(String paramString)
  {
    if (paramString == null) {
      return null;
    }
    try
    {
      if (paramString.length() == 4)
      {
        int i = Integer.parseInt(paramString);
        return String.format(Locale.getDefault(), "%d", new Object[] { Integer.valueOf(i) });
      }
      if (paramString.length() == 10)
      {
        paramString = DATE_PARSE_FORMAT.parse(paramString);
        return this.mDateFormat.format(paramString);
      }
      if (paramString.length() == 20)
      {
        paramString = DateUtils.getRelativeTimeSpanString(DATETIME_PARSE_FORMAT.parse(paramString).getTime(), System.currentTimeMillis(), 0L, 0);
        return paramString;
      }
    }
    catch (ParseException paramString)
    {
      return null;
    }
    catch (NumberFormatException paramString)
    {
      for (;;) {}
    }
  }
  
  @Nullable
  private CharSequence formatTvEpisodeTitleAndDescription(@NonNull Program paramProgram)
  {
    CharSequence localCharSequence = safeTrim(paramProgram.getEpisodeTitle());
    paramProgram = safeTrim(paramProgram.getShortDescription());
    if (TextUtils.isEmpty(localCharSequence)) {
      return paramProgram;
    }
    if (TextUtils.isEmpty(paramProgram)) {
      return Html.fromHtml(String.format(Locale.getDefault(), this.mEpisodeTitleFormat, new Object[] { localCharSequence }), 0);
    }
    return Html.fromHtml(String.format(Locale.getDefault(), this.mEpisodeTitleAndDescriptionFormat, new Object[] { localCharSequence, paramProgram }), 0);
  }
  
  @Nullable
  private String generateSecondRow(@NonNull Program paramProgram)
  {
    int i = paramProgram.getType();
    StringBuilder localStringBuilder = new StringBuilder(150);
    if (i == 4)
    {
      appendNonEmptyMetadataItem(localStringBuilder, paramProgram.getAuthor());
      appendNonEmptyMetadataItem(localStringBuilder, formatReleaseDate(paramProgram.getReleaseDate()));
      i = paramProgram.getInteractionCount();
      if (i > 0) {
        appendNonEmptyMetadataItem(localStringBuilder, formatInteractions(paramProgram.getInteractionType(), i));
      }
    }
    while (localStringBuilder.length() > 0)
    {
      localStringBuilder.append(this.mMetadataSuffix);
      return localStringBuilder.toString();
      if (i == 0)
      {
        appendNonEmptyMetadataItem(localStringBuilder, formatReleaseDate(paramProgram.getReleaseDate()));
        appendNonEmptyMetadataItem(localStringBuilder, this.mCanonicalGenreUtil.decodeGenres(paramProgram.getCanonicalGenres()));
        appendNonEmptyMetadataItem(localStringBuilder, formatDurationInHoursAndMinutes(paramProgram.getDuration()));
      }
      else if (i == 1)
      {
        appendNonEmptyMetadataItem(localStringBuilder, formatReleaseDate(paramProgram.getReleaseDate()));
        appendNonEmptyMetadataItem(localStringBuilder, this.mCanonicalGenreUtil.decodeGenres(paramProgram.getCanonicalGenres()));
        i = paramProgram.getItemCount();
        if (i > 0) {
          appendNonEmptyMetadataItem(localStringBuilder, formatQuantity(2131427338, i));
        }
      }
      else
      {
        String str;
        if (i == 2)
        {
          appendNonEmptyMetadataItem(localStringBuilder, formatReleaseDate(paramProgram.getReleaseDate()));
          str = paramProgram.getSeasonDisplayNumber();
          if (!TextUtils.isEmpty(str)) {
            appendNonEmptyMetadataItem(localStringBuilder, String.format(Locale.getDefault(), this.mSeasonDisplayNumberFormat, new Object[] { str }));
          }
          i = paramProgram.getItemCount();
          if (i > 0) {
            appendNonEmptyMetadataItem(localStringBuilder, formatQuantity(2131427337, i));
          }
          appendNonEmptyMetadataItem(localStringBuilder, this.mCanonicalGenreUtil.decodeGenres(paramProgram.getCanonicalGenres()));
        }
        else if (i == 3)
        {
          appendNonEmptyMetadataItem(localStringBuilder, formatReleaseDate(paramProgram.getReleaseDate()));
          str = paramProgram.getSeasonDisplayNumber();
          if (!TextUtils.isEmpty(str)) {
            appendNonEmptyMetadataItem(localStringBuilder, String.format(Locale.getDefault(), this.mSeasonDisplayNumberFormat, new Object[] { str }));
          }
          str = paramProgram.getEpisodeDisplayNumber();
          if (!TextUtils.isEmpty(str)) {
            appendNonEmptyMetadataItem(localStringBuilder, String.format(Locale.getDefault(), this.mEpisodeDisplayNumberFormat, new Object[] { str }));
          }
          appendNonEmptyMetadataItem(localStringBuilder, formatDurationInHoursAndMinutes(paramProgram.getDuration()));
          appendNonEmptyMetadataItem(localStringBuilder, this.mCanonicalGenreUtil.decodeGenres(paramProgram.getCanonicalGenres()));
        }
        else if (i == 5)
        {
          appendNonEmptyMetadataItem(localStringBuilder, formatReleaseDate(paramProgram.getReleaseDate()));
          appendNonEmptyMetadataItem(localStringBuilder, formatDurationInHoursAndMinutes(paramProgram.getDuration()));
          i = paramProgram.getInteractionCount();
          if (i > 0) {
            appendNonEmptyMetadataItem(localStringBuilder, formatInteractions(paramProgram.getInteractionType(), i));
          }
        }
        else if (i == 7)
        {
          appendNonEmptyMetadataItem(localStringBuilder, paramProgram.getAuthor());
          appendNonEmptyMetadataItem(localStringBuilder, formatDurationInHoursMinutesAndSeconds(paramProgram.getDuration()));
        }
        else if ((i == 8) || (i == 10))
        {
          appendNonEmptyMetadataItem(localStringBuilder, paramProgram.getAuthor());
          i = paramProgram.getItemCount();
          if (i > 0) {
            appendNonEmptyMetadataItem(localStringBuilder, formatQuantity(2131427339, i));
          }
        }
        else if (i == 11)
        {
          appendNonEmptyMetadataItem(localStringBuilder, paramProgram.getAuthor());
        }
      }
    }
    return null;
  }
  
  @Nullable
  private CharSequence generateThirdRow(@NonNull Program paramProgram)
  {
    int i = paramProgram.getType();
    StringBuilder localStringBuilder = new StringBuilder(150);
    if (i == 3) {
      return formatTvEpisodeTitleAndDescription(paramProgram);
    }
    if ((i == 7) || (i == 8))
    {
      appendNonEmptyMetadataItem(localStringBuilder, formatReleaseDate(paramProgram.getReleaseDate()));
      appendNonEmptyMetadataItem(localStringBuilder, paramProgram.getShortDescription());
    }
    while (localStringBuilder.length() > 0)
    {
      localStringBuilder.append(this.mMetadataSuffix);
      return localStringBuilder.toString();
      if ((i == 9) || (i == 10) || (i == 11))
      {
        i = paramProgram.getInteractionCount();
        if (i > 0) {
          appendNonEmptyMetadataItem(localStringBuilder, formatInteractions(paramProgram.getInteractionType(), i));
        }
        appendNonEmptyMetadataItem(localStringBuilder, paramProgram.getShortDescription());
      }
    }
    return paramProgram.getShortDescription();
  }
  
  private void initializeStarRatingSystem()
  {
    this.mStarActiveTint = ColorStateList.valueOf(this.mContext.getColor(R.color.program_meta_rating_active_color));
    this.mStarInactiveTint = ColorStateList.valueOf(this.mContext.getColor(R.color.program_meta_rating_inactive_color));
    this.mRatingImageSize = this.mContext.getResources().getDimensionPixelSize(R.dimen.program_meta_rating_size);
    this.mStarViews = new ArrayList(5);
    int i = 0;
    while (i < 5)
    {
      ImageView localImageView = new ImageView(this.mContext);
      localImageView.setImageDrawable(this.mContext.getDrawable(R.drawable.ic_channel_view_filled_star_black));
      this.mStarViews.add(localImageView);
      i += 1;
    }
  }
  
  @Nullable
  private String parseContentRating(@Nullable String paramString)
  {
    try
    {
      paramString = ContentRatingsUtil.stringToContentRatings(paramString);
      if ((paramString != null) && (paramString.length > 0))
      {
        paramString = ContentRatingsManager.getInstance(this.mContext).getDisplayNameForRating(paramString[0]);
        return paramString;
      }
    }
    catch (IllegalArgumentException paramString) {}
    return null;
  }
  
  @Nullable
  private CharSequence safeTrim(@Nullable CharSequence paramCharSequence)
  {
    Object localObject = paramCharSequence;
    if ((paramCharSequence instanceof String)) {
      localObject = ((String)paramCharSequence).trim();
    }
    return (CharSequence)localObject;
  }
  
  private void setThirdRow(String paramString)
  {
    this.mThirdRow.setText(paramString);
    updateVisibility();
  }
  
  private void setVisibility(View paramView, boolean paramBoolean)
  {
    int i = 0;
    boolean bool;
    if (paramView.getVisibility() == 0)
    {
      bool = true;
      if (bool != paramBoolean) {
        if (!paramBoolean) {
          break label34;
        }
      }
    }
    for (;;)
    {
      paramView.setVisibility(i);
      return;
      bool = false;
      break;
      label34:
      i = 8;
    }
  }
  
  /* Error */
  private void updateRatingSystem(String paramString, int paramInt)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 152	com/google/android/tvlauncher/home/ChannelItemMetadataController:mStarRating	Landroid/widget/LinearLayout;
    //   4: invokevirtual 509	android/widget/LinearLayout:removeAllViews	()V
    //   7: aload_0
    //   8: getfield 133	com/google/android/tvlauncher/home/ChannelItemMetadataController:mThumbCountUp	Landroid/widget/TextView;
    //   11: aconst_null
    //   12: invokevirtual 494	android/widget/TextView:setText	(Ljava/lang/CharSequence;)V
    //   15: aload_0
    //   16: getfield 136	com/google/android/tvlauncher/home/ChannelItemMetadataController:mThumbCountDown	Landroid/widget/TextView;
    //   19: aconst_null
    //   20: invokevirtual 494	android/widget/TextView:setText	(Ljava/lang/CharSequence;)V
    //   23: aload_0
    //   24: getfield 147	com/google/android/tvlauncher/home/ChannelItemMetadataController:mRatingPercentage	Landroid/widget/TextView;
    //   27: aconst_null
    //   28: invokevirtual 494	android/widget/TextView:setText	(Ljava/lang/CharSequence;)V
    //   31: aload_0
    //   32: getfield 101	com/google/android/tvlauncher/home/ChannelItemMetadataController:mLegacy	Z
    //   35: ifne +33 -> 68
    //   38: aload_1
    //   39: ifnull +29 -> 68
    //   42: iload_2
    //   43: tableswitch	default:+25->68, 0:+26->69, 1:+182->225, 2:+128->171
    //   68: return
    //   69: aload_0
    //   70: getfield 452	com/google/android/tvlauncher/home/ChannelItemMetadataController:mStarViews	Ljava/util/List;
    //   73: ifnonnull +7 -> 80
    //   76: aload_0
    //   77: invokespecial 511	com/google/android/tvlauncher/home/ChannelItemMetadataController:initializeStarRatingSystem	()V
    //   80: aload_1
    //   81: invokestatic 517	java/lang/Float:parseFloat	(Ljava/lang/String;)F
    //   84: invokestatic 523	java/lang/Math:round	(F)I
    //   87: istore 4
    //   89: iconst_0
    //   90: istore_2
    //   91: iload_2
    //   92: iconst_5
    //   93: if_icmpge -25 -> 68
    //   96: aload_0
    //   97: getfield 452	com/google/android/tvlauncher/home/ChannelItemMetadataController:mStarViews	Ljava/util/List;
    //   100: iload_2
    //   101: invokeinterface 527 2 0
    //   106: checkcast 139	android/widget/ImageView
    //   109: astore 9
    //   111: iload_2
    //   112: iload 4
    //   114: if_icmpge +49 -> 163
    //   117: aload_0
    //   118: getfield 438	com/google/android/tvlauncher/home/ChannelItemMetadataController:mStarActiveTint	Landroid/content/res/ColorStateList;
    //   121: astore_1
    //   122: aload 9
    //   124: aload_1
    //   125: invokevirtual 531	android/widget/ImageView:setImageTintList	(Landroid/content/res/ColorStateList;)V
    //   128: aload_0
    //   129: getfield 152	com/google/android/tvlauncher/home/ChannelItemMetadataController:mStarRating	Landroid/widget/LinearLayout;
    //   132: aload_0
    //   133: getfield 452	com/google/android/tvlauncher/home/ChannelItemMetadataController:mStarViews	Ljava/util/List;
    //   136: iload_2
    //   137: invokeinterface 527 2 0
    //   142: checkcast 103	android/view/View
    //   145: aload_0
    //   146: getfield 447	com/google/android/tvlauncher/home/ChannelItemMetadataController:mRatingImageSize	I
    //   149: aload_0
    //   150: getfield 447	com/google/android/tvlauncher/home/ChannelItemMetadataController:mRatingImageSize	I
    //   153: invokevirtual 535	android/widget/LinearLayout:addView	(Landroid/view/View;II)V
    //   156: iload_2
    //   157: iconst_1
    //   158: iadd
    //   159: istore_2
    //   160: goto -69 -> 91
    //   163: aload_0
    //   164: getfield 441	com/google/android/tvlauncher/home/ChannelItemMetadataController:mStarInactiveTint	Landroid/content/res/ColorStateList;
    //   167: astore_1
    //   168: goto -46 -> 122
    //   171: aload_1
    //   172: invokestatic 517	java/lang/Float:parseFloat	(Ljava/lang/String;)F
    //   175: ldc_w 536
    //   178: fdiv
    //   179: fstore_3
    //   180: invokestatic 542	java/text/NumberFormat:getPercentInstance	()Ljava/text/NumberFormat;
    //   183: astore 9
    //   185: aload_1
    //   186: bipush 46
    //   188: invokevirtual 545	java/lang/String:indexOf	(I)I
    //   191: iconst_m1
    //   192: if_icmpne +24 -> 216
    //   195: aload 9
    //   197: iconst_0
    //   198: invokevirtual 548	java/text/NumberFormat:setMaximumFractionDigits	(I)V
    //   201: aload_0
    //   202: getfield 147	com/google/android/tvlauncher/home/ChannelItemMetadataController:mRatingPercentage	Landroid/widget/TextView;
    //   205: aload 9
    //   207: fload_3
    //   208: f2d
    //   209: invokevirtual 551	java/text/NumberFormat:format	(D)Ljava/lang/String;
    //   212: invokevirtual 494	android/widget/TextView:setText	(Ljava/lang/CharSequence;)V
    //   215: return
    //   216: aload 9
    //   218: iconst_1
    //   219: invokevirtual 548	java/text/NumberFormat:setMaximumFractionDigits	(I)V
    //   222: goto -21 -> 201
    //   225: aload_0
    //   226: getfield 246	com/google/android/tvlauncher/home/ChannelItemMetadataController:mThumbsUpDownRatingPattern	Ljava/util/regex/Pattern;
    //   229: aload_1
    //   230: invokevirtual 555	java/util/regex/Pattern:matcher	(Ljava/lang/CharSequence;)Ljava/util/regex/Matcher;
    //   233: astore_1
    //   234: aload_1
    //   235: invokevirtual 561	java/util/regex/Matcher:find	()Z
    //   238: ifeq -170 -> 68
    //   241: aload_1
    //   242: invokevirtual 564	java/util/regex/Matcher:groupCount	()I
    //   245: iconst_2
    //   246: if_icmpne -178 -> 68
    //   249: aload_1
    //   250: iconst_1
    //   251: invokevirtual 567	java/util/regex/Matcher:group	(I)Ljava/lang/String;
    //   254: invokestatic 573	java/lang/Long:parseLong	(Ljava/lang/String;)J
    //   257: lstore 5
    //   259: aload_1
    //   260: iconst_2
    //   261: invokevirtual 567	java/util/regex/Matcher:group	(I)Ljava/lang/String;
    //   264: invokestatic 573	java/lang/Long:parseLong	(Ljava/lang/String;)J
    //   267: lstore 7
    //   269: invokestatic 575	java/text/NumberFormat:getInstance	()Ljava/text/NumberFormat;
    //   272: astore_1
    //   273: aload_0
    //   274: getfield 133	com/google/android/tvlauncher/home/ChannelItemMetadataController:mThumbCountUp	Landroid/widget/TextView;
    //   277: aload_1
    //   278: lload 5
    //   280: invokevirtual 578	java/text/NumberFormat:format	(J)Ljava/lang/String;
    //   283: invokevirtual 494	android/widget/TextView:setText	(Ljava/lang/CharSequence;)V
    //   286: aload_0
    //   287: getfield 136	com/google/android/tvlauncher/home/ChannelItemMetadataController:mThumbCountDown	Landroid/widget/TextView;
    //   290: aload_1
    //   291: lload 7
    //   293: invokevirtual 578	java/text/NumberFormat:format	(J)Ljava/lang/String;
    //   296: invokevirtual 494	android/widget/TextView:setText	(Ljava/lang/CharSequence;)V
    //   299: return
    //   300: astore_1
    //   301: return
    //   302: astore_1
    //   303: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	304	0	this	ChannelItemMetadataController
    //   0	304	1	paramString	String
    //   0	304	2	paramInt	int
    //   179	29	3	f	float
    //   87	28	4	i	int
    //   257	22	5	l1	long
    //   267	25	7	l2	long
    //   109	108	9	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   69	80	300	java/lang/NumberFormatException
    //   80	89	300	java/lang/NumberFormatException
    //   96	111	300	java/lang/NumberFormatException
    //   117	122	300	java/lang/NumberFormatException
    //   122	156	300	java/lang/NumberFormatException
    //   163	168	300	java/lang/NumberFormatException
    //   171	201	302	java/lang/NumberFormatException
    //   201	215	302	java/lang/NumberFormatException
    //   216	222	302	java/lang/NumberFormatException
  }
  
  private void updateVisibility()
  {
    boolean bool2 = true;
    Object localObject = this.mFirstRow;
    if (this.mFirstRow.length() != 0)
    {
      bool1 = true;
      setVisibility((View)localObject, bool1);
      localObject = this.mSecondRow;
      if (this.mSecondRow.length() == 0) {
        break label304;
      }
      bool1 = true;
      label42:
      setVisibility((View)localObject, bool1);
      localObject = this.mThirdRow;
      if (this.mThirdRow.length() == 0) {
        break label309;
      }
      bool1 = true;
      label65:
      setVisibility((View)localObject, bool1);
      localObject = this.mStarRating;
      if (this.mStarRating.getChildCount() == 0) {
        break label314;
      }
      bool1 = true;
      label88:
      setVisibility((View)localObject, bool1);
      localObject = this.mThumbCountUp;
      if (this.mThumbCountUp.length() == 0) {
        break label319;
      }
      bool1 = true;
      label111:
      setVisibility((View)localObject, bool1);
      localObject = this.mThumbCountDown;
      if (this.mThumbCountDown.length() == 0) {
        break label324;
      }
      bool1 = true;
      label134:
      setVisibility((View)localObject, bool1);
      localObject = this.mThumbUp;
      if (this.mThumbCountUp.length() == 0) {
        break label329;
      }
      bool1 = true;
      label157:
      setVisibility((View)localObject, bool1);
      localObject = this.mThumbDown;
      if (this.mThumbCountDown.length() == 0) {
        break label334;
      }
      bool1 = true;
      label180:
      setVisibility((View)localObject, bool1);
      localObject = this.mRatingPercentage;
      if (this.mRatingPercentage.length() == 0) {
        break label339;
      }
      bool1 = true;
      label203:
      setVisibility((View)localObject, bool1);
      localObject = this.mPrice;
      if (this.mPrice.length() == 0) {
        break label344;
      }
      bool1 = true;
      label226:
      setVisibility((View)localObject, bool1);
      localObject = this.mOldPrice;
      if (this.mOldPrice.length() == 0) {
        break label349;
      }
      bool1 = true;
      label249:
      setVisibility((View)localObject, bool1);
      localObject = this.mContentRating;
      if (this.mContentRating.length() == 0) {
        break label354;
      }
      bool1 = true;
      label272:
      setVisibility((View)localObject, bool1);
      localObject = this.mLogo;
      if (this.mLogoUri == null) {
        break label359;
      }
    }
    label304:
    label309:
    label314:
    label319:
    label324:
    label329:
    label334:
    label339:
    label344:
    label349:
    label354:
    label359:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      setVisibility((View)localObject, bool1);
      return;
      bool1 = false;
      break;
      bool1 = false;
      break label42;
      bool1 = false;
      break label65;
      bool1 = false;
      break label88;
      bool1 = false;
      break label111;
      bool1 = false;
      break label134;
      bool1 = false;
      break label157;
      bool1 = false;
      break label180;
      bool1 = false;
      break label203;
      bool1 = false;
      break label226;
      bool1 = false;
      break label249;
      bool1 = false;
      break label272;
    }
  }
  
  void bindView(@NonNull Program paramProgram)
  {
    String str = paramProgram.getTitle();
    Object localObject4 = null;
    Object localObject1 = null;
    Object localObject7 = null;
    Object localObject2 = null;
    Object localObject6 = null;
    Object localObject5 = null;
    Object localObject3 = null;
    this.mLogoUri = null;
    if (this.mLegacy)
    {
      localObject5 = paramProgram.getShortDescription();
      this.mLogoUri = paramProgram.getLogoUri();
      localObject1 = localObject7;
      localObject2 = localObject6;
      this.mFirstRow.setText(safeTrim(str));
      this.mSecondRow.setText(safeTrim((CharSequence)localObject4));
      this.mThirdRow.setText(safeTrim((CharSequence)localObject5));
      this.mThirdRow.setMaxLines(this.mThirdRowDefaultMaxLines);
      updateRatingSystem(paramProgram.getReviewRating(), paramProgram.getReviewRatingStyle());
      this.mPrice.setText(safeTrim((CharSequence)localObject1));
      this.mOldPrice.setText(safeTrim((CharSequence)localObject2));
      this.mContentRating.setText(safeTrim((CharSequence)localObject3));
      if (this.mLogoUri != null) {
        Glide.with(this.mContext).load(this.mLogoUri).into(this.mLogo);
      }
      updateVisibility();
      return;
    }
    int i = paramProgram.getType();
    if ((i == 4) || (i == 10)) {
      this.mLogoUri = paramProgram.getLogoUri();
    }
    if ((i != 4) && (i != 0) && (i != 1) && (i != 2) && (i != 3) && (i != 6))
    {
      localObject3 = localObject5;
      if (i != 5) {}
    }
    else
    {
      localObject3 = parseContentRating(paramProgram.getContentRating());
    }
    localObject5 = generateSecondRow(paramProgram);
    i = paramProgram.getAvailability();
    if (i == 1) {
      localObject1 = this.mFreeWithSubscriptionText;
    }
    for (;;)
    {
      localObject6 = generateThirdRow(paramProgram);
      localObject4 = localObject5;
      localObject5 = localObject6;
      break;
      if (i == 2)
      {
        localObject4 = paramProgram.getStartingPrice();
        localObject6 = paramProgram.getOfferPrice();
        localObject2 = localObject4;
        localObject1 = localObject6;
        if (localObject6 == null)
        {
          localObject1 = localObject4;
          localObject2 = null;
        }
      }
    }
  }
  
  public void clear()
  {
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
  
  @Nullable
  @VisibleForTesting(otherwise=2)
  CharSequence formatDurationInHoursAndMinutes(long paramLong)
  {
    long l = paramLong / 60000L;
    if (l >= 60L)
    {
      paramLong = l / 60L;
      if (paramLong > 23L) {
        return null;
      }
      l -= 60L * paramLong;
      if (l > 0L) {
        return this.mDurationFormat.formatMeasures(new Measure[] { new Measure(Long.valueOf(paramLong), MeasureUnit.HOUR), new Measure(Long.valueOf(l), MeasureUnit.MINUTE) });
      }
      return this.mDurationFormat.format(new Measure(Long.valueOf(paramLong), MeasureUnit.HOUR));
    }
    if (l > 0L) {
      return this.mDurationFormat.format(new Measure(Long.valueOf(l), MeasureUnit.MINUTE));
    }
    if (paramLong > 0L) {
      return this.mDurationFormat.format(new Measure(Integer.valueOf(1), MeasureUnit.MINUTE));
    }
    return null;
  }
  
  @Nullable
  @VisibleForTesting(otherwise=2)
  CharSequence formatDurationInHoursMinutesAndSeconds(long paramLong)
  {
    long l = paramLong / 1000L;
    if (l >= 3600L) {
      return formatDurationInHoursAndMinutes(paramLong);
    }
    if (l >= 60L)
    {
      paramLong = l / 60L;
      l -= paramLong * 60L;
      if (l > 0L) {
        return this.mDurationFormat.formatMeasures(new Measure[] { new Measure(Long.valueOf(paramLong), MeasureUnit.MINUTE), new Measure(Long.valueOf(l), MeasureUnit.SECOND) });
      }
      return this.mDurationFormat.format(new Measure(Long.valueOf(paramLong), MeasureUnit.MINUTE));
    }
    if (l > 0L) {
      return this.mDurationFormat.format(new Measure(Long.valueOf(l), MeasureUnit.SECOND));
    }
    if (paramLong > 0L) {
      return this.mDurationFormat.format(new Measure(Integer.valueOf(1), MeasureUnit.SECOND));
    }
    return null;
  }
  
  void setFirstRow(String paramString)
  {
    this.mFirstRow.setText(paramString);
    updateVisibility();
  }
  
  void setLegacy(boolean paramBoolean)
  {
    this.mLegacy = paramBoolean;
  }
  
  void setSecondRow(String paramString)
  {
    this.mSecondRow.setText(paramString);
    updateVisibility();
  }
  
  void setThirdRow(String paramString, int paramInt)
  {
    this.mThirdRow.setMaxLines(paramInt);
    setThirdRow(paramString);
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/home/ChannelItemMetadataController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */