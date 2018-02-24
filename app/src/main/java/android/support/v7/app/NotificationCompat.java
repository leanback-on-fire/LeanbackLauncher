package android.support.v7.app;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;
import android.support.v4.app.BundleCompat;
import android.support.v4.app.NotificationBuilderWithBuilderAccessor;
import android.support.v4.app.NotificationCompat.BuilderExtender;
import android.support.v4.app.NotificationCompat.MessagingStyle;
import android.support.v4.app.NotificationCompat.MessagingStyle.Message;
import android.support.v4.app.NotificationCompat.Style;
import android.support.v4.media.session.MediaSessionCompat.Token;
import android.support.v4.text.BidiFormatter;
import android.support.v7.appcompat.R.color;
import android.support.v7.appcompat.R.id;
import android.support.v7.appcompat.R.layout;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.widget.RemoteViews;
import java.util.List;

public class NotificationCompat
  extends android.support.v4.app.NotificationCompat
{
  @RequiresApi(16)
  private static void addBigStyleToBuilderJellybean(Notification paramNotification, android.support.v4.app.NotificationCompat.Builder paramBuilder)
  {
    if ((paramBuilder.mStyle instanceof MediaStyle))
    {
      localMediaStyle = (MediaStyle)paramBuilder.mStyle;
      if (paramBuilder.getBigContentView() != null)
      {
        localRemoteViews = paramBuilder.getBigContentView();
        if ((!(paramBuilder.mStyle instanceof DecoratedMediaCustomViewStyle)) || (localRemoteViews == null)) {
          break label132;
        }
        bool = true;
        NotificationCompatImplBase.overrideMediaBigContentView(paramNotification, paramBuilder.mContext, paramBuilder.mContentTitle, paramBuilder.mContentText, paramBuilder.mContentInfo, paramBuilder.mNumber, paramBuilder.mLargeIcon, paramBuilder.mSubText, paramBuilder.mUseChronometer, paramBuilder.getWhenIfShowing(), paramBuilder.getPriority(), 0, paramBuilder.mActions, localMediaStyle.mShowCancelButton, localMediaStyle.mCancelButtonIntent, bool);
        if (bool) {
          NotificationCompatImplBase.buildIntoRemoteViews(paramBuilder.mContext, paramNotification.bigContentView, localRemoteViews);
        }
      }
    }
    label132:
    while (!(paramBuilder.mStyle instanceof DecoratedCustomViewStyle)) {
      for (;;)
      {
        MediaStyle localMediaStyle;
        return;
        RemoteViews localRemoteViews = paramBuilder.getContentView();
        continue;
        boolean bool = false;
      }
    }
    addDecoratedBigStyleToBuilderJellybean(paramNotification, paramBuilder);
  }
  
  @RequiresApi(21)
  private static void addBigStyleToBuilderLollipop(Notification paramNotification, android.support.v4.app.NotificationCompat.Builder paramBuilder)
  {
    RemoteViews localRemoteViews;
    if (paramBuilder.getBigContentView() != null)
    {
      localRemoteViews = paramBuilder.getBigContentView();
      if ((!(paramBuilder.mStyle instanceof DecoratedMediaCustomViewStyle)) || (localRemoteViews == null)) {
        break label114;
      }
      NotificationCompatImplBase.overrideMediaBigContentView(paramNotification, paramBuilder.mContext, paramBuilder.mContentTitle, paramBuilder.mContentText, paramBuilder.mContentInfo, paramBuilder.mNumber, paramBuilder.mLargeIcon, paramBuilder.mSubText, paramBuilder.mUseChronometer, paramBuilder.getWhenIfShowing(), paramBuilder.getPriority(), 0, paramBuilder.mActions, false, null, true);
      NotificationCompatImplBase.buildIntoRemoteViews(paramBuilder.mContext, paramNotification.bigContentView, localRemoteViews);
      setBackgroundColor(paramBuilder.mContext, paramNotification.bigContentView, paramBuilder.getColor());
    }
    label114:
    while (!(paramBuilder.mStyle instanceof DecoratedCustomViewStyle))
    {
      return;
      localRemoteViews = paramBuilder.getContentView();
      break;
    }
    addDecoratedBigStyleToBuilderJellybean(paramNotification, paramBuilder);
  }
  
  @RequiresApi(16)
  private static void addDecoratedBigStyleToBuilderJellybean(Notification paramNotification, android.support.v4.app.NotificationCompat.Builder paramBuilder)
  {
    RemoteViews localRemoteViews1 = paramBuilder.getBigContentView();
    if (localRemoteViews1 != null) {}
    while (localRemoteViews1 == null)
    {
      return;
      localRemoteViews1 = paramBuilder.getContentView();
    }
    RemoteViews localRemoteViews2 = NotificationCompatImplBase.applyStandardTemplateWithActions(paramBuilder.mContext, paramBuilder.mContentTitle, paramBuilder.mContentText, paramBuilder.mContentInfo, paramBuilder.mNumber, paramNotification.icon, paramBuilder.mLargeIcon, paramBuilder.mSubText, paramBuilder.mUseChronometer, paramBuilder.getWhenIfShowing(), paramBuilder.getPriority(), paramBuilder.getColor(), R.layout.notification_template_custom_big, false, paramBuilder.mActions);
    NotificationCompatImplBase.buildIntoRemoteViews(paramBuilder.mContext, localRemoteViews2, localRemoteViews1);
    paramNotification.bigContentView = localRemoteViews2;
  }
  
  @RequiresApi(21)
  private static void addDecoratedHeadsUpToBuilderLollipop(Notification paramNotification, android.support.v4.app.NotificationCompat.Builder paramBuilder)
  {
    RemoteViews localRemoteViews2 = paramBuilder.getHeadsUpContentView();
    if (localRemoteViews2 != null) {}
    for (RemoteViews localRemoteViews1 = localRemoteViews2; localRemoteViews2 == null; localRemoteViews1 = paramBuilder.getContentView()) {
      return;
    }
    localRemoteViews2 = NotificationCompatImplBase.applyStandardTemplateWithActions(paramBuilder.mContext, paramBuilder.mContentTitle, paramBuilder.mContentText, paramBuilder.mContentInfo, paramBuilder.mNumber, paramNotification.icon, paramBuilder.mLargeIcon, paramBuilder.mSubText, paramBuilder.mUseChronometer, paramBuilder.getWhenIfShowing(), paramBuilder.getPriority(), paramBuilder.getColor(), R.layout.notification_template_custom_big, false, paramBuilder.mActions);
    NotificationCompatImplBase.buildIntoRemoteViews(paramBuilder.mContext, localRemoteViews2, localRemoteViews1);
    paramNotification.headsUpContentView = localRemoteViews2;
  }
  
  @RequiresApi(21)
  private static void addHeadsUpToBuilderLollipop(Notification paramNotification, android.support.v4.app.NotificationCompat.Builder paramBuilder)
  {
    RemoteViews localRemoteViews;
    if (paramBuilder.getHeadsUpContentView() != null)
    {
      localRemoteViews = paramBuilder.getHeadsUpContentView();
      if ((!(paramBuilder.mStyle instanceof DecoratedMediaCustomViewStyle)) || (localRemoteViews == null)) {
        break label117;
      }
      paramNotification.headsUpContentView = NotificationCompatImplBase.generateMediaBigView(paramBuilder.mContext, paramBuilder.mContentTitle, paramBuilder.mContentText, paramBuilder.mContentInfo, paramBuilder.mNumber, paramBuilder.mLargeIcon, paramBuilder.mSubText, paramBuilder.mUseChronometer, paramBuilder.getWhenIfShowing(), paramBuilder.getPriority(), 0, paramBuilder.mActions, false, null, true);
      NotificationCompatImplBase.buildIntoRemoteViews(paramBuilder.mContext, paramNotification.headsUpContentView, localRemoteViews);
      setBackgroundColor(paramBuilder.mContext, paramNotification.headsUpContentView, paramBuilder.getColor());
    }
    label117:
    while (!(paramBuilder.mStyle instanceof DecoratedCustomViewStyle))
    {
      return;
      localRemoteViews = paramBuilder.getContentView();
      break;
    }
    addDecoratedHeadsUpToBuilderLollipop(paramNotification, paramBuilder);
  }
  
  @RequiresApi(16)
  private static void addMessagingFallBackStyle(NotificationCompat.MessagingStyle paramMessagingStyle, NotificationBuilderWithBuilderAccessor paramNotificationBuilderWithBuilderAccessor, android.support.v4.app.NotificationCompat.Builder paramBuilder)
  {
    SpannableStringBuilder localSpannableStringBuilder = new SpannableStringBuilder();
    List localList = paramMessagingStyle.getMessages();
    int i;
    int j;
    if ((paramMessagingStyle.getConversationTitle() != null) || (hasMessagesWithoutSender(paramMessagingStyle.getMessages())))
    {
      i = 1;
      j = localList.size() - 1;
      label45:
      if (j < 0) {
        break label133;
      }
      localObject = (NotificationCompat.MessagingStyle.Message)localList.get(j);
      if (i == 0) {
        break label123;
      }
    }
    label123:
    for (Object localObject = makeMessageLine(paramBuilder, paramMessagingStyle, (NotificationCompat.MessagingStyle.Message)localObject);; localObject = ((NotificationCompat.MessagingStyle.Message)localObject).getText())
    {
      if (j != localList.size() - 1) {
        localSpannableStringBuilder.insert(0, "\n");
      }
      localSpannableStringBuilder.insert(0, (CharSequence)localObject);
      j -= 1;
      break label45;
      i = 0;
      break;
    }
    label133:
    NotificationCompatImplJellybean.addBigTextStyle(paramNotificationBuilderWithBuilderAccessor, localSpannableStringBuilder);
  }
  
  @RequiresApi(14)
  private static RemoteViews addStyleGetContentViewIcs(NotificationBuilderWithBuilderAccessor paramNotificationBuilderWithBuilderAccessor, android.support.v4.app.NotificationCompat.Builder paramBuilder)
  {
    if ((paramBuilder.mStyle instanceof MediaStyle))
    {
      MediaStyle localMediaStyle = (MediaStyle)paramBuilder.mStyle;
      if (((paramBuilder.mStyle instanceof DecoratedMediaCustomViewStyle)) && (paramBuilder.getContentView() != null)) {}
      for (boolean bool = true;; bool = false)
      {
        paramNotificationBuilderWithBuilderAccessor = NotificationCompatImplBase.overrideContentViewMedia(paramNotificationBuilderWithBuilderAccessor, paramBuilder.mContext, paramBuilder.mContentTitle, paramBuilder.mContentText, paramBuilder.mContentInfo, paramBuilder.mNumber, paramBuilder.mLargeIcon, paramBuilder.mSubText, paramBuilder.mUseChronometer, paramBuilder.getWhenIfShowing(), paramBuilder.getPriority(), paramBuilder.mActions, localMediaStyle.mActionsToShowInCompact, localMediaStyle.mShowCancelButton, localMediaStyle.mCancelButtonIntent, bool);
        if (!bool) {
          break;
        }
        NotificationCompatImplBase.buildIntoRemoteViews(paramBuilder.mContext, paramNotificationBuilderWithBuilderAccessor, paramBuilder.getContentView());
        return paramNotificationBuilderWithBuilderAccessor;
      }
    }
    if ((paramBuilder.mStyle instanceof DecoratedCustomViewStyle)) {
      return getDecoratedContentView(paramBuilder);
    }
    return null;
  }
  
  @RequiresApi(16)
  private static RemoteViews addStyleGetContentViewJellybean(NotificationBuilderWithBuilderAccessor paramNotificationBuilderWithBuilderAccessor, android.support.v4.app.NotificationCompat.Builder paramBuilder)
  {
    if ((paramBuilder.mStyle instanceof NotificationCompat.MessagingStyle)) {
      addMessagingFallBackStyle((NotificationCompat.MessagingStyle)paramBuilder.mStyle, paramNotificationBuilderWithBuilderAccessor, paramBuilder);
    }
    return addStyleGetContentViewIcs(paramNotificationBuilderWithBuilderAccessor, paramBuilder);
  }
  
  @RequiresApi(21)
  private static RemoteViews addStyleGetContentViewLollipop(NotificationBuilderWithBuilderAccessor paramNotificationBuilderWithBuilderAccessor, android.support.v4.app.NotificationCompat.Builder paramBuilder)
  {
    if ((paramBuilder.mStyle instanceof MediaStyle))
    {
      MediaStyle localMediaStyle = (MediaStyle)paramBuilder.mStyle;
      int[] arrayOfInt = localMediaStyle.mActionsToShowInCompact;
      Object localObject;
      boolean bool;
      if (localMediaStyle.mToken != null)
      {
        localObject = localMediaStyle.mToken.getToken();
        NotificationCompatImpl21.addMediaStyle(paramNotificationBuilderWithBuilderAccessor, arrayOfInt, localObject);
        if (paramBuilder.getContentView() == null) {
          break label203;
        }
        bool = true;
        label61:
        if ((Build.VERSION.SDK_INT < 21) || (Build.VERSION.SDK_INT > 23)) {
          break label208;
        }
        i = 1;
        label79:
        if ((!bool) && ((i == 0) || (paramBuilder.getBigContentView() == null))) {
          break label213;
        }
      }
      label203:
      label208:
      label213:
      for (int i = 1;; i = 0)
      {
        if ((!(paramBuilder.mStyle instanceof DecoratedMediaCustomViewStyle)) || (i == 0)) {
          break label218;
        }
        paramNotificationBuilderWithBuilderAccessor = NotificationCompatImplBase.overrideContentViewMedia(paramNotificationBuilderWithBuilderAccessor, paramBuilder.mContext, paramBuilder.mContentTitle, paramBuilder.mContentText, paramBuilder.mContentInfo, paramBuilder.mNumber, paramBuilder.mLargeIcon, paramBuilder.mSubText, paramBuilder.mUseChronometer, paramBuilder.getWhenIfShowing(), paramBuilder.getPriority(), paramBuilder.mActions, localMediaStyle.mActionsToShowInCompact, false, null, bool);
        if (bool) {
          NotificationCompatImplBase.buildIntoRemoteViews(paramBuilder.mContext, paramNotificationBuilderWithBuilderAccessor, paramBuilder.getContentView());
        }
        setBackgroundColor(paramBuilder.mContext, paramNotificationBuilderWithBuilderAccessor, paramBuilder.getColor());
        return paramNotificationBuilderWithBuilderAccessor;
        localObject = null;
        break;
        bool = false;
        break label61;
        i = 0;
        break label79;
      }
      label218:
      return null;
    }
    if ((paramBuilder.mStyle instanceof DecoratedCustomViewStyle)) {
      return getDecoratedContentView(paramBuilder);
    }
    return addStyleGetContentViewJellybean(paramNotificationBuilderWithBuilderAccessor, paramBuilder);
  }
  
  @RequiresApi(24)
  private static void addStyleToBuilderApi24(NotificationBuilderWithBuilderAccessor paramNotificationBuilderWithBuilderAccessor, android.support.v4.app.NotificationCompat.Builder paramBuilder)
  {
    if ((paramBuilder.mStyle instanceof DecoratedCustomViewStyle)) {
      NotificationCompatImpl24.addDecoratedCustomViewStyle(paramNotificationBuilderWithBuilderAccessor);
    }
    do
    {
      return;
      if ((paramBuilder.mStyle instanceof DecoratedMediaCustomViewStyle))
      {
        paramBuilder = (DecoratedMediaCustomViewStyle)paramBuilder.mStyle;
        int[] arrayOfInt = paramBuilder.mActionsToShowInCompact;
        if (paramBuilder.mToken != null) {}
        for (paramBuilder = paramBuilder.mToken.getToken();; paramBuilder = null)
        {
          NotificationCompatImpl24.addDecoratedMediaCustomViewStyle(paramNotificationBuilderWithBuilderAccessor, arrayOfInt, paramBuilder);
          return;
        }
      }
    } while ((paramBuilder.mStyle instanceof NotificationCompat.MessagingStyle));
    addStyleGetContentViewLollipop(paramNotificationBuilderWithBuilderAccessor, paramBuilder);
  }
  
  private static NotificationCompat.MessagingStyle.Message findLatestIncomingMessage(NotificationCompat.MessagingStyle paramMessagingStyle)
  {
    paramMessagingStyle = paramMessagingStyle.getMessages();
    int i = paramMessagingStyle.size() - 1;
    while (i >= 0)
    {
      NotificationCompat.MessagingStyle.Message localMessage = (NotificationCompat.MessagingStyle.Message)paramMessagingStyle.get(i);
      if (!TextUtils.isEmpty(localMessage.getSender())) {
        return localMessage;
      }
      i -= 1;
    }
    if (!paramMessagingStyle.isEmpty()) {
      return (NotificationCompat.MessagingStyle.Message)paramMessagingStyle.get(paramMessagingStyle.size() - 1);
    }
    return null;
  }
  
  private static RemoteViews getDecoratedContentView(android.support.v4.app.NotificationCompat.Builder paramBuilder)
  {
    if (paramBuilder.getContentView() == null) {
      return null;
    }
    RemoteViews localRemoteViews = NotificationCompatImplBase.applyStandardTemplateWithActions(paramBuilder.mContext, paramBuilder.mContentTitle, paramBuilder.mContentText, paramBuilder.mContentInfo, paramBuilder.mNumber, paramBuilder.mNotification.icon, paramBuilder.mLargeIcon, paramBuilder.mSubText, paramBuilder.mUseChronometer, paramBuilder.getWhenIfShowing(), paramBuilder.getPriority(), paramBuilder.getColor(), R.layout.notification_template_custom_big, false, null);
    NotificationCompatImplBase.buildIntoRemoteViews(paramBuilder.mContext, localRemoteViews, paramBuilder.getContentView());
    return localRemoteViews;
  }
  
  public static MediaSessionCompat.Token getMediaSession(Notification paramNotification)
  {
    paramNotification = getExtras(paramNotification);
    if (paramNotification != null) {
      if (Build.VERSION.SDK_INT >= 21)
      {
        paramNotification = paramNotification.getParcelable("android.mediaSession");
        if (paramNotification != null) {
          return MediaSessionCompat.Token.fromToken(paramNotification);
        }
      }
      else
      {
        Object localObject = BundleCompat.getBinder(paramNotification, "android.mediaSession");
        if (localObject != null)
        {
          paramNotification = Parcel.obtain();
          paramNotification.writeStrongBinder((IBinder)localObject);
          paramNotification.setDataPosition(0);
          localObject = (MediaSessionCompat.Token)MediaSessionCompat.Token.CREATOR.createFromParcel(paramNotification);
          paramNotification.recycle();
          return (MediaSessionCompat.Token)localObject;
        }
      }
    }
    return null;
  }
  
  private static boolean hasMessagesWithoutSender(List<NotificationCompat.MessagingStyle.Message> paramList)
  {
    int i = paramList.size() - 1;
    while (i >= 0)
    {
      if (((NotificationCompat.MessagingStyle.Message)paramList.get(i)).getSender() == null) {
        return true;
      }
      i -= 1;
    }
    return false;
  }
  
  private static TextAppearanceSpan makeFontColorSpan(int paramInt)
  {
    return new TextAppearanceSpan(null, 0, 0, ColorStateList.valueOf(paramInt), null);
  }
  
  private static CharSequence makeMessageLine(android.support.v4.app.NotificationCompat.Builder paramBuilder, NotificationCompat.MessagingStyle paramMessagingStyle, NotificationCompat.MessagingStyle.Message paramMessage)
  {
    BidiFormatter localBidiFormatter = BidiFormatter.getInstance();
    SpannableStringBuilder localSpannableStringBuilder = new SpannableStringBuilder();
    int j;
    int i;
    if (Build.VERSION.SDK_INT >= 21)
    {
      j = 1;
      if ((j == 0) && (Build.VERSION.SDK_INT > 10)) {
        break label187;
      }
      i = -16777216;
      label42:
      Object localObject = paramMessage.getSender();
      int k = i;
      if (TextUtils.isEmpty(paramMessage.getSender()))
      {
        if (paramMessagingStyle.getUserDisplayName() != null) {
          break label192;
        }
        paramMessagingStyle = "";
        label72:
        k = i;
        localObject = paramMessagingStyle;
        if (j != 0)
        {
          k = i;
          localObject = paramMessagingStyle;
          if (paramBuilder.getColor() != 0)
          {
            k = paramBuilder.getColor();
            localObject = paramMessagingStyle;
          }
        }
      }
      paramBuilder = localBidiFormatter.unicodeWrap((CharSequence)localObject);
      localSpannableStringBuilder.append(paramBuilder);
      localSpannableStringBuilder.setSpan(makeFontColorSpan(k), localSpannableStringBuilder.length() - paramBuilder.length(), localSpannableStringBuilder.length(), 33);
      if (paramMessage.getText() != null) {
        break label200;
      }
    }
    label187:
    label192:
    label200:
    for (paramBuilder = "";; paramBuilder = paramMessage.getText())
    {
      localSpannableStringBuilder.append("  ").append(localBidiFormatter.unicodeWrap(paramBuilder));
      return localSpannableStringBuilder;
      j = 0;
      break;
      i = -1;
      break label42;
      paramMessagingStyle = paramMessagingStyle.getUserDisplayName();
      break label72;
    }
  }
  
  private static void setBackgroundColor(Context paramContext, RemoteViews paramRemoteViews, int paramInt)
  {
    int i = paramInt;
    if (paramInt == 0) {
      i = paramContext.getResources().getColor(R.color.notification_material_background_media_default_color);
    }
    paramRemoteViews.setInt(R.id.status_bar_latest_event_content, "setBackgroundColor", i);
  }
  
  @RequiresApi(24)
  private static class Api24Extender
    extends NotificationCompat.BuilderExtender
  {
    public Notification build(android.support.v4.app.NotificationCompat.Builder paramBuilder, NotificationBuilderWithBuilderAccessor paramNotificationBuilderWithBuilderAccessor)
    {
      NotificationCompat.addStyleToBuilderApi24(paramNotificationBuilderWithBuilderAccessor, paramBuilder);
      return paramNotificationBuilderWithBuilderAccessor.build();
    }
  }
  
  public static class Builder
    extends android.support.v4.app.NotificationCompat.Builder
  {
    public Builder(Context paramContext)
    {
      super();
    }
    
    @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
    protected NotificationCompat.BuilderExtender getExtender()
    {
      if (Build.VERSION.SDK_INT >= 24) {
        return new NotificationCompat.Api24Extender(null);
      }
      if (Build.VERSION.SDK_INT >= 21) {
        return new NotificationCompat.LollipopExtender();
      }
      if (Build.VERSION.SDK_INT >= 16) {
        return new NotificationCompat.JellybeanExtender();
      }
      if (Build.VERSION.SDK_INT >= 14) {
        return new NotificationCompat.IceCreamSandwichExtender();
      }
      return super.getExtender();
    }
    
    @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
    protected CharSequence resolveText()
    {
      if ((this.mStyle instanceof NotificationCompat.MessagingStyle))
      {
        NotificationCompat.MessagingStyle localMessagingStyle = (NotificationCompat.MessagingStyle)this.mStyle;
        NotificationCompat.MessagingStyle.Message localMessage = NotificationCompat.findLatestIncomingMessage(localMessagingStyle);
        CharSequence localCharSequence = localMessagingStyle.getConversationTitle();
        if (localMessage != null)
        {
          if (localCharSequence != null) {
            return NotificationCompat.makeMessageLine(this, localMessagingStyle, localMessage);
          }
          return localMessage.getText();
        }
      }
      return super.resolveText();
    }
    
    @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
    protected CharSequence resolveTitle()
    {
      if ((this.mStyle instanceof NotificationCompat.MessagingStyle))
      {
        Object localObject = (NotificationCompat.MessagingStyle)this.mStyle;
        NotificationCompat.MessagingStyle.Message localMessage = NotificationCompat.findLatestIncomingMessage((NotificationCompat.MessagingStyle)localObject);
        localObject = ((NotificationCompat.MessagingStyle)localObject).getConversationTitle();
        if ((localObject != null) || (localMessage != null))
        {
          if (localObject != null) {
            return (CharSequence)localObject;
          }
          return localMessage.getSender();
        }
      }
      return super.resolveTitle();
    }
  }
  
  public static class DecoratedCustomViewStyle
    extends NotificationCompat.Style
  {}
  
  public static class DecoratedMediaCustomViewStyle
    extends NotificationCompat.MediaStyle
  {}
  
  @RequiresApi(14)
  private static class IceCreamSandwichExtender
    extends NotificationCompat.BuilderExtender
  {
    public Notification build(android.support.v4.app.NotificationCompat.Builder paramBuilder, NotificationBuilderWithBuilderAccessor paramNotificationBuilderWithBuilderAccessor)
    {
      RemoteViews localRemoteViews = NotificationCompat.addStyleGetContentViewIcs(paramNotificationBuilderWithBuilderAccessor, paramBuilder);
      paramNotificationBuilderWithBuilderAccessor = paramNotificationBuilderWithBuilderAccessor.build();
      if (localRemoteViews != null) {
        paramNotificationBuilderWithBuilderAccessor.contentView = localRemoteViews;
      }
      while (paramBuilder.getContentView() == null) {
        return paramNotificationBuilderWithBuilderAccessor;
      }
      paramNotificationBuilderWithBuilderAccessor.contentView = paramBuilder.getContentView();
      return paramNotificationBuilderWithBuilderAccessor;
    }
  }
  
  @RequiresApi(16)
  private static class JellybeanExtender
    extends NotificationCompat.BuilderExtender
  {
    public Notification build(android.support.v4.app.NotificationCompat.Builder paramBuilder, NotificationBuilderWithBuilderAccessor paramNotificationBuilderWithBuilderAccessor)
    {
      RemoteViews localRemoteViews = NotificationCompat.addStyleGetContentViewJellybean(paramNotificationBuilderWithBuilderAccessor, paramBuilder);
      paramNotificationBuilderWithBuilderAccessor = paramNotificationBuilderWithBuilderAccessor.build();
      if (localRemoteViews != null) {
        paramNotificationBuilderWithBuilderAccessor.contentView = localRemoteViews;
      }
      NotificationCompat.addBigStyleToBuilderJellybean(paramNotificationBuilderWithBuilderAccessor, paramBuilder);
      return paramNotificationBuilderWithBuilderAccessor;
    }
  }
  
  @RequiresApi(21)
  private static class LollipopExtender
    extends NotificationCompat.BuilderExtender
  {
    public Notification build(android.support.v4.app.NotificationCompat.Builder paramBuilder, NotificationBuilderWithBuilderAccessor paramNotificationBuilderWithBuilderAccessor)
    {
      RemoteViews localRemoteViews = NotificationCompat.addStyleGetContentViewLollipop(paramNotificationBuilderWithBuilderAccessor, paramBuilder);
      paramNotificationBuilderWithBuilderAccessor = paramNotificationBuilderWithBuilderAccessor.build();
      if (localRemoteViews != null) {
        paramNotificationBuilderWithBuilderAccessor.contentView = localRemoteViews;
      }
      NotificationCompat.addBigStyleToBuilderLollipop(paramNotificationBuilderWithBuilderAccessor, paramBuilder);
      NotificationCompat.addHeadsUpToBuilderLollipop(paramNotificationBuilderWithBuilderAccessor, paramBuilder);
      return paramNotificationBuilderWithBuilderAccessor;
    }
  }
  
  public static class MediaStyle
    extends NotificationCompat.Style
  {
    int[] mActionsToShowInCompact = null;
    PendingIntent mCancelButtonIntent;
    boolean mShowCancelButton;
    MediaSessionCompat.Token mToken;
    
    public MediaStyle() {}
    
    public MediaStyle(android.support.v4.app.NotificationCompat.Builder paramBuilder)
    {
      setBuilder(paramBuilder);
    }
    
    public MediaStyle setCancelButtonIntent(PendingIntent paramPendingIntent)
    {
      this.mCancelButtonIntent = paramPendingIntent;
      return this;
    }
    
    public MediaStyle setMediaSession(MediaSessionCompat.Token paramToken)
    {
      this.mToken = paramToken;
      return this;
    }
    
    public MediaStyle setShowActionsInCompactView(int... paramVarArgs)
    {
      this.mActionsToShowInCompact = paramVarArgs;
      return this;
    }
    
    public MediaStyle setShowCancelButton(boolean paramBoolean)
    {
      this.mShowCancelButton = paramBoolean;
      return this;
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v7/app/NotificationCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */