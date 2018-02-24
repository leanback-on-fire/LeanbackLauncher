package com.google.android.gsf;

import android.content.ContentQueryMap;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.Handler;
import android.provider.BaseColumns;

public class TalkContract
{
  public static final String AUTHORITY = "com.google.android.providers.talk";
  public static final Uri AUTHORITY_URI = Uri.parse("content://com.google.android.providers.talk");
  public static final String GTALK_CATEGORY = "com.android.im.category.GTALK";
  
  public static final class Account
    implements BaseColumns, TalkContract.AccountColumns
  {
    public static final String ACCOUNT_CONNECTION_STATUS = "account_connStatus";
    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/gtalk-accounts";
    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/gtalk-accounts";
    public static final Uri CONTENT_URI = Uri.parse("content://com.google.android.providers.talk/accounts");
    public static final Uri CONTENT_URI_WITH_STATUS = Uri.parse("content://com.google.android.providers.talk/accounts/status");
    public static final String DEFAULT_SORT_ORDER = "name ASC";
  }
  
  public static abstract interface AccountColumns
  {
    public static final String KEEP_SIGNED_IN = "keep_signed_in";
    public static final String LAST_LOGIN_STATE = "last_login_state";
    public static final String LOCKED = "locked";
    public static final String NAME = "name";
    public static final String USERNAME = "username";
  }
  
  public static class AccountSettings
    implements TalkContract.AccountSettingsColumns
  {
    public static final String CONTENT_TYPE = "vnd.android-dir/gtalk-accountSettings";
    public static final Uri CONTENT_URI = Uri.parse("content://com.google.android.providers.talk/accountSettings");
    private static final int DISABLED = 2;
    private static final int ENABLED = 1;
    private static final int ENABLED_MASK = 3;
    public static final String IMAGE_STABILIZATION_HIGH = "high";
    public static final String IMAGE_STABILIZATION_LOW = "low";
    public static final String IMAGE_STABILIZATION_MEDIUM = "medium";
    public static final String IMAGE_STABILIZATION_OFF = "off";
    public static final String IMAGE_STABILIZATION_VIRTUAL_CAMERA_OPERATOR = "virtual";
    public static final String LAST_RMQ_RECEIVED = "last_rmq_rec";
    public static final String NOTIFICATION_OFF = "off";
    public static final String NOTIFICATION_POPUP = "popup";
    public static final String NOTIFICATION_STATUS_BAR = "statusbar";
    public static final String SETTING_ALLOW_AUDIOCHAT = "audiochat";
    public static final String SETTING_ALLOW_AUDIOCHAT_V2 = "audiochatv2";
    public static final String SETTING_ALLOW_CAMERA = "show_camera";
    public static final String SETTING_ALLOW_VIDEOCHAT = "videochat";
    public static final String SETTING_ALLOW_VIDEOCHAT_V2 = "videochatv2";
    public static final String SETTING_AUTOMATICALLY_CONNECT_GTALK = "gtalk_auto_connect";
    public static final String SETTING_AUTOMATICALLY_START_SERVICE = "auto_start_service";
    public static final String SETTING_HEARTBEAT_INTERVAL = "heartbeat_interval";
    public static final String SETTING_HIDE_OFFLINE_CONTACTS = "hide_offline_contacts";
    public static final String SETTING_IM_NOTIFICATION_TYPE = "text-notif-type";
    public static final String SETTING_JID_RESOURCE = "jid_resource";
    public static final String SETTING_NOTIFY_FRIEND_INVITE = "notify_invite";
    public static final String SETTING_SHOW_AWAY_ON_IDLE = "show_away_on_idle";
    public static final String SETTING_SHOW_MOBILE_INDICATOR = "mobile_indicator";
    public static final String SETTING_TEXT_RINGTONE = "ringtone";
    public static final String SETTING_TEXT_RINGTONE_DEFAULT = "content://settings/system/notification_sound";
    public static final String SETTING_TEXT_VIBRATE = "vibrate";
    public static final String SETTING_TEXT_VIBRATE_WHEN = "vibrate-when";
    public static final String SETTING_UPLOAD_HEARTBEAT_STAT = "upload_heartbeat_stat";
    public static final String SETTING_VIDEO_IMAGE_STABILIZATION = "video-image-stabilization";
    public static final String SETTING_VIDEO_NOTIFICATION_TYPE = "video-notif-type";
    public static final String SETTING_VIDEO_RINGTONE = "ringtone-video";
    public static final String SETTING_VIDEO_RINGTONE_DEFAULT = "content://settings/system/ringtone";
    public static final String SETTING_VIDEO_VIBRATE = "vibrate-video";
    public static final String SETTING_VIDEO_VIBRATE_WHEN = "vibrate-when-video";
    public static final String SHOW_OFFLINE_CONTACTS = "show_offline_contacts";
    private static final int UNSET = 0;
    private static final int USER_SET = 16;
    private static final int USER_SET_MASK = 16;
    public static final String VIBRATE_ALWAYS = "always";
    public static final String VIBRATE_NEVER = "never";
    public static final String VIBRATE_SILENT = "silent";
    public static final String VIDEOCHAT_BLOCK = "off";
    public static final String VIDEOCHAT_VIDEO = "video";
    public static final String VIDEOCHAT_VOICE = "audio";
    
    public static final Uri getContentUriByAccountId(long paramLong)
    {
      Uri.Builder localBuilder = CONTENT_URI.buildUpon();
      ContentUris.appendId(localBuilder, paramLong);
      return localBuilder.build();
    }
    
    private static int getSettingValue(boolean paramBoolean)
    {
      if (paramBoolean) {
        return 1;
      }
      return 2;
    }
    
    private static int getUserSettingValue(boolean paramBoolean1, boolean paramBoolean2)
    {
      int i;
      if (paramBoolean1)
      {
        i = 1;
        if (!paramBoolean2) {
          break label22;
        }
      }
      label22:
      for (int j = 16;; j = 0)
      {
        return j | i;
        i = 2;
        break;
      }
    }
    
    private static boolean isEnabled(long paramLong)
    {
      return (0x3 & paramLong) == 1L;
    }
    
    private static boolean isUserSet(long paramLong)
    {
      return (paramLong & 0x10) == 16L;
    }
    
    public static void putBooleanValue(ContentResolver paramContentResolver, String paramString, boolean paramBoolean, long paramLong)
    {
      ContentValues localContentValues = new ContentValues(3);
      localContentValues.put("name", paramString);
      localContentValues.put("value", Boolean.toString(paramBoolean));
      localContentValues.put("account_id", Long.valueOf(paramLong));
      paramContentResolver.insert(CONTENT_URI, localContentValues);
    }
    
    public static void putLongValue(ContentResolver paramContentResolver, String paramString, long paramLong1, long paramLong2)
    {
      ContentValues localContentValues = new ContentValues(3);
      localContentValues.put("name", paramString);
      localContentValues.put("value", Long.valueOf(paramLong1));
      localContentValues.put("account_id", Long.valueOf(paramLong2));
      paramContentResolver.insert(CONTENT_URI, localContentValues);
    }
    
    public static void putStringValue(ContentResolver paramContentResolver, String paramString1, String paramString2, long paramLong)
    {
      ContentValues localContentValues = new ContentValues(3);
      localContentValues.put("name", paramString1);
      localContentValues.put("value", paramString2);
      localContentValues.put("account_id", Long.valueOf(paramLong));
      paramContentResolver.insert(CONTENT_URI, localContentValues);
    }
    
    public static void setAudioChatEnabled(ContentResolver paramContentResolver, boolean paramBoolean, long paramLong)
    {
      putLongValue(paramContentResolver, "audiochatv2", getSettingValue(paramBoolean), paramLong);
    }
    
    @Deprecated
    public static boolean setAudioChatEnabled(ContentResolver paramContentResolver, boolean paramBoolean1, boolean paramBoolean2, long paramLong1, long paramLong2)
    {
      if ((paramBoolean2) || (!isUserSet(paramLong1)))
      {
        putLongValue(paramContentResolver, "audiochatv2", getUserSettingValue(paramBoolean1, paramBoolean2), paramLong2);
        return true;
      }
      return false;
    }
    
    public static void setAutomaticallyConnectGTalk(ContentResolver paramContentResolver, boolean paramBoolean, long paramLong)
    {
      putBooleanValue(paramContentResolver, "gtalk_auto_connect", paramBoolean, paramLong);
    }
    
    public static void setCameraEnabled(ContentResolver paramContentResolver, boolean paramBoolean, long paramLong)
    {
      putLongValue(paramContentResolver, "show_camera", getSettingValue(paramBoolean), paramLong);
    }
    
    @Deprecated
    public static boolean setCameraEnabled(ContentResolver paramContentResolver, boolean paramBoolean1, boolean paramBoolean2, long paramLong1, long paramLong2)
    {
      if ((paramBoolean2) || (!isUserSet(paramLong1)))
      {
        putLongValue(paramContentResolver, "show_camera", getUserSettingValue(paramBoolean1, paramBoolean2), paramLong2);
        return true;
      }
      return false;
    }
    
    public static void setHeartbeatInterval(ContentResolver paramContentResolver, long paramLong1, long paramLong2)
    {
      putLongValue(paramContentResolver, "heartbeat_interval", paramLong1, paramLong2);
    }
    
    public static void setHideOfflineContacts(ContentResolver paramContentResolver, boolean paramBoolean, long paramLong)
    {
      putBooleanValue(paramContentResolver, "hide_offline_contacts", paramBoolean, paramLong);
    }
    
    public static void setJidResource(ContentResolver paramContentResolver, String paramString, long paramLong)
    {
      putStringValue(paramContentResolver, "jid_resource", paramString, paramLong);
    }
    
    public static void setNotificationType(ContentResolver paramContentResolver, String paramString, long paramLong)
    {
      putStringValue(paramContentResolver, "text-notif-type", paramString, paramLong);
    }
    
    public static void setNotifyFriendInvitation(ContentResolver paramContentResolver, boolean paramBoolean, long paramLong)
    {
      putBooleanValue(paramContentResolver, "notify_invite", paramBoolean, paramLong);
    }
    
    public static void setShowAwayOnIdle(ContentResolver paramContentResolver, boolean paramBoolean, long paramLong)
    {
      putBooleanValue(paramContentResolver, "show_away_on_idle", paramBoolean, paramLong);
    }
    
    public static void setShowMobileIndicator(ContentResolver paramContentResolver, boolean paramBoolean, long paramLong)
    {
      putBooleanValue(paramContentResolver, "mobile_indicator", paramBoolean, paramLong);
    }
    
    public static void setTextRingtoneURI(ContentResolver paramContentResolver, String paramString, long paramLong)
    {
      putStringValue(paramContentResolver, "ringtone", paramString, paramLong);
    }
    
    public static void setTextVibrate(ContentResolver paramContentResolver, boolean paramBoolean, long paramLong)
    {
      putBooleanValue(paramContentResolver, "vibrate", paramBoolean, paramLong);
    }
    
    public static void setTextVibrateWhen(ContentResolver paramContentResolver, String paramString, long paramLong)
    {
      putStringValue(paramContentResolver, "vibrate-when", paramString, paramLong);
    }
    
    public static void setUploadHeartbeatStat(ContentResolver paramContentResolver, boolean paramBoolean, long paramLong)
    {
      putBooleanValue(paramContentResolver, "upload_heartbeat_stat", paramBoolean, paramLong);
    }
    
    public static void setVideoChatEnabled(ContentResolver paramContentResolver, boolean paramBoolean, long paramLong)
    {
      putLongValue(paramContentResolver, "videochatv2", getSettingValue(paramBoolean), paramLong);
    }
    
    @Deprecated
    public static boolean setVideoChatEnabled(ContentResolver paramContentResolver, boolean paramBoolean1, boolean paramBoolean2, long paramLong1, long paramLong2)
    {
      if ((paramBoolean2) || (!isUserSet(paramLong1)))
      {
        putLongValue(paramContentResolver, "videochatv2", getUserSettingValue(paramBoolean1, paramBoolean2), paramLong2);
        return true;
      }
      return false;
    }
    
    public static void setVideoImageStabilization(ContentResolver paramContentResolver, String paramString, long paramLong)
    {
      putStringValue(paramContentResolver, "video-image-stabilization", paramString, paramLong);
    }
    
    public static void setVideoNotificationType(ContentResolver paramContentResolver, String paramString, long paramLong)
    {
      putStringValue(paramContentResolver, "video-notif-type", paramString, paramLong);
    }
    
    public static void setVideoRingtoneURI(ContentResolver paramContentResolver, String paramString, long paramLong)
    {
      putStringValue(paramContentResolver, "ringtone-video", paramString, paramLong);
    }
    
    public static void setVideoVibrate(ContentResolver paramContentResolver, boolean paramBoolean, long paramLong)
    {
      putBooleanValue(paramContentResolver, "vibrate-video", paramBoolean, paramLong);
    }
    
    public static void setVideoVibrateWhen(ContentResolver paramContentResolver, String paramString, long paramLong)
    {
      putStringValue(paramContentResolver, "vibrate-when-video", paramString, paramLong);
    }
    
    public static class QueryMap
      extends ContentQueryMap
    {
      private static final int HAS_CAMERA_V1 = 4;
      private static final int HAS_PMUC_V1 = 8;
      private static final int HAS_VIDEO_V1 = 2;
      private static final int HAS_VOICE_V1 = 1;
      private long mAccountId;
      private ContentResolver mContentResolver;
      
      public QueryMap(ContentResolver paramContentResolver, boolean paramBoolean, long paramLong, Handler paramHandler)
      {
        super("name", paramBoolean, paramHandler);
        this.mContentResolver = paramContentResolver;
        this.mAccountId = paramLong;
      }
      
      private boolean getBoolean(String paramString, boolean paramBoolean)
      {
        paramString = getValues(paramString);
        if (paramString != null) {
          paramBoolean = paramString.getAsBoolean("value").booleanValue();
        }
        return paramBoolean;
      }
      
      public static int getCapabilities(boolean paramBoolean1, boolean paramBoolean2)
      {
        int j = 0;
        if (paramBoolean1) {}
        for (int i = 6;; i = 0)
        {
          if (paramBoolean2) {
            j = 1;
          }
          return j | i;
        }
      }
      
      private int getInteger(String paramString, int paramInt)
      {
        paramString = getValues(paramString);
        if (paramString != null) {
          paramInt = paramString.getAsInteger("value").intValue();
        }
        return paramInt;
      }
      
      private long getLong(String paramString, long paramLong)
      {
        paramString = getValues(paramString);
        if (paramString != null) {
          paramLong = paramString.getAsLong("value").longValue();
        }
        return paramLong;
      }
      
      private String getString(String paramString1, String paramString2)
      {
        paramString1 = getValues(paramString1);
        if (paramString1 != null) {
          paramString2 = paramString1.getAsString("value");
        }
        return paramString2;
      }
      
      private static boolean isTablet(Context paramContext)
      {
        return (paramContext.getResources().getConfiguration().screenLayout & 0xF) > 3;
      }
      
      public boolean getAudioChatEnabled()
      {
        return TalkContract.AccountSettings.isEnabled(getLong("audiochatv2", 2L));
      }
      
      public boolean getAudioChatUnset()
      {
        return getLong("audiochatv2", 0L) == 0L;
      }
      
      public boolean getAutomaticallyConnectToGTalkServer()
      {
        return getBoolean("gtalk_auto_connect", true);
      }
      
      public boolean getCameraEnabled()
      {
        return TalkContract.AccountSettings.isEnabled(getLong("show_camera", 2L));
      }
      
      public boolean getCameraUnset()
      {
        return getLong("show_camera", 0L) == 0L;
      }
      
      public int getCapabilities()
      {
        int j = 8;
        if (getAudioChatEnabled()) {
          j = 0x8 | 0x1;
        }
        int i = j;
        if (getVideoChatEnabled())
        {
          j |= 0x2;
          i = j;
          if (getCameraEnabled()) {
            i = j | 0x4;
          }
        }
        return i;
      }
      
      public long getHeartbeatInterval()
      {
        return getLong("heartbeat_interval", 0L);
      }
      
      public boolean getHideOfflineContacts()
      {
        return getBoolean("hide_offline_contacts", false);
      }
      
      public String getJidResource()
      {
        return getString("jid_resource", null);
      }
      
      public boolean getNotifyFriendInvitation()
      {
        return getBoolean("notify_invite", true);
      }
      
      public boolean getShowAwayOnIdle()
      {
        return getBoolean("show_away_on_idle", true);
      }
      
      public boolean getShowMobileIndicator(Context paramContext)
      {
        if (!isTablet(paramContext)) {}
        for (boolean bool = true;; bool = false) {
          return getBoolean("mobile_indicator", bool);
        }
      }
      
      public String getTextNotification()
      {
        return getString("text-notif-type", "statusbar");
      }
      
      public String getTextRingtoneURI()
      {
        return getString("ringtone", "content://settings/system/notification_sound");
      }
      
      public boolean getTextVibrate()
      {
        return getBoolean("vibrate", false);
      }
      
      public String getTextVibrateWhen()
      {
        String str = getString("vibrate-when", null);
        if (str != null) {
          return str;
        }
        if (getTextVibrate()) {}
        for (str = "always";; str = "never") {
          return str;
        }
      }
      
      public boolean getUploadHeartbeatStat()
      {
        return getBoolean("upload_heartbeat_stat", false);
      }
      
      public boolean getVideoChatEnabled()
      {
        return TalkContract.AccountSettings.isEnabled(getLong("videochatv2", 2L));
      }
      
      public boolean getVideoChatUnset()
      {
        return getLong("videochatv2", 0L) == 0L;
      }
      
      public String getVideoImageStabilization()
      {
        return getString("video-image-stabilization", null);
      }
      
      public String getVideoNotification()
      {
        return getString("video-notif-type", "popup");
      }
      
      public String getVideoRingtoneURI()
      {
        return getString("ringtone-video", "content://settings/system/ringtone");
      }
      
      public boolean getVideoVibrate()
      {
        return getBoolean("vibrate-video", false);
      }
      
      public String getVideoVibrateWhen()
      {
        String str = getString("vibrate-when-video", null);
        if (str != null) {
          return str;
        }
        if (getVideoVibrate()) {}
        for (str = "always";; str = "never") {
          return str;
        }
      }
      
      public void setAudioChatEnabled(boolean paramBoolean)
      {
        TalkContract.AccountSettings.setAudioChatEnabled(this.mContentResolver, paramBoolean, this.mAccountId);
      }
      
      @Deprecated
      public boolean setAudioChatEnabled(boolean paramBoolean1, boolean paramBoolean2)
      {
        long l = getLong("audiochatv2", 0L);
        return TalkContract.AccountSettings.setAudioChatEnabled(this.mContentResolver, paramBoolean1, paramBoolean2, l, this.mAccountId);
      }
      
      public void setAutomaticallyConnectToGTalkServer(boolean paramBoolean)
      {
        TalkContract.AccountSettings.setAutomaticallyConnectGTalk(this.mContentResolver, paramBoolean, this.mAccountId);
      }
      
      public void setCameraEnabled(boolean paramBoolean)
      {
        TalkContract.AccountSettings.setCameraEnabled(this.mContentResolver, paramBoolean, this.mAccountId);
      }
      
      @Deprecated
      public boolean setCameraEnabled(boolean paramBoolean1, boolean paramBoolean2)
      {
        long l = getLong("show_camera", 0L);
        return TalkContract.AccountSettings.setCameraEnabled(this.mContentResolver, paramBoolean1, paramBoolean2, l, this.mAccountId);
      }
      
      public void setHeartbeatInterval(long paramLong)
      {
        TalkContract.AccountSettings.setHeartbeatInterval(this.mContentResolver, paramLong, this.mAccountId);
      }
      
      public void setHideOfflineContacts(boolean paramBoolean)
      {
        TalkContract.AccountSettings.setHideOfflineContacts(this.mContentResolver, paramBoolean, this.mAccountId);
      }
      
      public void setJidResource(String paramString)
      {
        TalkContract.AccountSettings.setJidResource(this.mContentResolver, paramString, this.mAccountId);
      }
      
      public void setNotifyFriendInvitation(boolean paramBoolean)
      {
        TalkContract.AccountSettings.setNotifyFriendInvitation(this.mContentResolver, paramBoolean, this.mAccountId);
      }
      
      public void setShowAwayOnIdle(boolean paramBoolean)
      {
        TalkContract.AccountSettings.setShowAwayOnIdle(this.mContentResolver, paramBoolean, this.mAccountId);
      }
      
      public void setShowMobileIndicator(boolean paramBoolean)
      {
        TalkContract.AccountSettings.setShowMobileIndicator(this.mContentResolver, paramBoolean, this.mAccountId);
      }
      
      public void setTextNotification(String paramString)
      {
        TalkContract.AccountSettings.setNotificationType(this.mContentResolver, paramString, this.mAccountId);
      }
      
      public void setTextRingtoneURI(String paramString)
      {
        TalkContract.AccountSettings.setTextRingtoneURI(this.mContentResolver, paramString, this.mAccountId);
      }
      
      public void setTextVibrate(boolean paramBoolean)
      {
        TalkContract.AccountSettings.setTextVibrate(this.mContentResolver, paramBoolean, this.mAccountId);
      }
      
      public void setTextVibrateWhen(String paramString)
      {
        TalkContract.AccountSettings.setTextVibrateWhen(this.mContentResolver, paramString, this.mAccountId);
      }
      
      public void setUploadHeartbeatStat(boolean paramBoolean)
      {
        TalkContract.AccountSettings.setUploadHeartbeatStat(this.mContentResolver, paramBoolean, this.mAccountId);
      }
      
      public void setVideoChatEnabled(boolean paramBoolean)
      {
        TalkContract.AccountSettings.setVideoChatEnabled(this.mContentResolver, paramBoolean, this.mAccountId);
      }
      
      @Deprecated
      public boolean setVideoChatEnabled(boolean paramBoolean1, boolean paramBoolean2)
      {
        long l = getLong("videochatv2", 0L);
        return TalkContract.AccountSettings.setVideoChatEnabled(this.mContentResolver, paramBoolean1, paramBoolean2, l, this.mAccountId);
      }
      
      public void setVideoImageStabilization(String paramString)
      {
        TalkContract.AccountSettings.setVideoImageStabilization(this.mContentResolver, paramString, this.mAccountId);
      }
      
      public void setVideoNotification(String paramString)
      {
        TalkContract.AccountSettings.setVideoNotificationType(this.mContentResolver, paramString, this.mAccountId);
      }
      
      public void setVideoRingtoneURI(String paramString)
      {
        TalkContract.AccountSettings.setVideoRingtoneURI(this.mContentResolver, paramString, this.mAccountId);
      }
      
      public void setVideoVibrate(boolean paramBoolean)
      {
        TalkContract.AccountSettings.setVideoVibrate(this.mContentResolver, paramBoolean, this.mAccountId);
      }
      
      public void setVideoVibrateWhen(String paramString)
      {
        TalkContract.AccountSettings.setVideoVibrateWhen(this.mContentResolver, paramString, this.mAccountId);
      }
    }
  }
  
  public static abstract interface AccountSettingsColumns
  {
    public static final String ACCOUNT_ID = "account_id";
    public static final String NAME = "name";
    public static final String VALUE = "value";
  }
  
  public static final class AccountStatus
    implements BaseColumns, TalkContract.AccountStatusColumns
  {
    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/gtalk-account-status";
    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/gtalk-account-status";
    public static final Uri CONTENT_URI = Uri.parse("content://com.google.android.providers.talk/accountStatus");
    public static final Uri CONTENT_URI_UNREAD_CHATS = Uri.parse("content://com.google.android.providers.talk/accountStatus/new_messages");
    public static final String DEFAULT_SORT_ORDER = "name ASC";
  }
  
  public static abstract interface AccountStatusColumns
  {
    public static final String ACCOUNT = "account";
    public static final String CONNECTION_STATUS = "connStatus";
    public static final String PRESENCE_STATUS = "presenceStatus";
  }
  
  public static final class Avatars
    implements BaseColumns, TalkContract.AvatarsColumns
  {
    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/gtalk-avatars";
    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/gtalk-avatars";
    public static final Uri CONTENT_URI = Uri.parse("content://com.google.android.providers.talk/avatars");
    public static final Uri CONTENT_URI_AVATARS_BY = Uri.parse("content://com.google.android.providers.talk/avatarsBy");
    public static final String DEFAULT_SORT_ORDER = "contact ASC";
  }
  
  public static abstract interface AvatarsColumns
  {
    public static final String ACCOUNT = "account_id";
    public static final String CONTACT = "contact";
    public static final String DATA = "data";
    public static final String HASH = "hash";
  }
  
  public static final class Chats
    implements BaseColumns, TalkContract.ChatsColumns
  {
    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/gtalk-chats";
    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/gtalk-chats";
    public static final Uri CONTENT_URI = Uri.parse("content://com.google.android.providers.talk/chats");
    public static final Uri CONTENT_URI_BY_ACCOUNT = Uri.parse("content://com.google.android.providers.talk/chats/account");
    public static final String DEFAULT_SORT_ORDER = "last_message_date ASC";
  }
  
  public static abstract interface ChatsColumns
  {
    public static final String ACCOUNT_ID = "account_id";
    public static final String CONTACT_ID = "contact_id";
    public static final String GROUP_CHAT = "groupchat";
    public static final String INITIATED_BY_LOCAL = "local";
    public static final String IS_ACTIVE = "is_active";
    public static final String JID_RESOURCE = "jid_resource";
    public static final String LAST_MESSAGE_DATE = "last_message_date";
    public static final String LAST_UNREAD_MESSAGE = "last_unread_message";
    public static final String OTHER_CLIENT = "otherClient";
    public static final String SHORTCUT = "shortcut";
    public static final String UNSENT_COMPOSED_MESSAGE = "unsent_composed_message";
  }
  
  public static abstract interface CommonPresenceColumns
  {
    public static final int AVAILABLE = 5;
    public static final int AWAY = 2;
    public static final int DO_NOT_DISTURB = 4;
    public static final int IDLE = 3;
    public static final int INVISIBLE = 1;
    public static final int OFFLINE = 0;
    public static final String PRESENCE_CUSTOM_STATUS = "status";
    public static final String PRESENCE_STATUS = "mode";
    public static final String PRIORITY = "priority";
  }
  
  public static abstract interface ConnectionStatus
  {
    public static final int CONNECTING = 1;
    public static final int OFFLINE = 0;
    public static final int ONLINE = 3;
    public static final int SUSPENDED = 2;
  }
  
  public static final class Contacts
    implements BaseColumns, TalkContract.ContactsColumns, TalkContract.PresenceColumns, TalkContract.ChatsColumns
  {
    public static final String AVATAR_DATA = "avatars_data";
    public static final String AVATAR_HASH = "avatars_hash";
    public static final String CHATS_CONTACT = "chats_contact";
    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/gtalk-contacts";
    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/gtalk-contacts";
    public static final Uri CONTENT_URI = Uri.parse("content://com.google.android.providers.talk/contacts");
    public static final Uri CONTENT_URI_BLOCKED_CONTACTS = Uri.parse("content://com.google.android.providers.talk/contacts/blocked");
    public static final Uri CONTENT_URI_CHAT_CONTACTS;
    public static final Uri CONTENT_URI_CONTACTS_BAREBONE = Uri.parse("content://com.google.android.providers.talk/contacts_barebone");
    public static final Uri CONTENT_URI_CONTACT_ID = Uri.parse("content://com.google.android.providers.talk/contacts");
    public static final String DEFAULT_SORT_ORDER = "subscriptionType DESC, (chats._id != 0) DESC, chats._id DESC, mode DESC, nickname COLLATE UNICODE ASC";
    
    static
    {
      CONTENT_URI_CHAT_CONTACTS = Uri.parse("content://com.google.android.providers.talk/contacts_chatting");
    }
  }
  
  public static abstract interface ContactsColumns
  {
    public static final String ACCOUNT = "account";
    public static final String CONTACTLIST = "contactList";
    public static final String NICKNAME = "nickname";
    public static final String OTR = "otr";
    public static final String QUICK_CONTACT = "qc";
    public static final String REJECTED = "rejected";
    public static final String SUBSCRIPTION_STATUS = "subscriptionStatus";
    public static final int SUBSCRIPTION_STATUS_NONE = 0;
    public static final int SUBSCRIPTION_STATUS_SUBSCRIBE_PENDING = 1;
    public static final int SUBSCRIPTION_STATUS_UNSUBSCRIBE_PENDING = 2;
    public static final String SUBSCRIPTION_TYPE = "subscriptionType";
    public static final int SUBSCRIPTION_TYPE_BOTH = 4;
    public static final int SUBSCRIPTION_TYPE_FROM = 3;
    public static final int SUBSCRIPTION_TYPE_INVITATIONS = 5;
    public static final int SUBSCRIPTION_TYPE_NONE = 0;
    public static final int SUBSCRIPTION_TYPE_REMOVE = 1;
    public static final int SUBSCRIPTION_TYPE_TO = 2;
    public static final String TYPE = "type";
    public static final int TYPE_BLOCKED = 3;
    public static final int TYPE_GROUP = 2;
    public static final int TYPE_HIDDEN = 4;
    public static final int TYPE_NORMAL = 0;
    public static final int TYPE_PINNED = 5;
    public static final int TYPE_TEMPORARY = 1;
    public static final String USERNAME = "username";
  }
  
  public static final class ContactsEtag
    implements BaseColumns, TalkContract.ContactsEtagColumns
  {
    private static int COLUMN_ETAG = 0;
    private static int COLUMN_OTR_ETAG = 0;
    private static final String[] CONTACT_ETAG_PROJECTION = { "etag" };
    private static final String[] CONTACT_OTR_ETAG_PROJECTION;
    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/gtalk-contactsEtag";
    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/gtalk-contactsEtag";
    public static final Uri CONTENT_URI = Uri.parse("content://com.google.android.providers.talk/contactsEtag");
    
    static
    {
      COLUMN_ETAG = 0;
      CONTACT_OTR_ETAG_PROJECTION = new String[] { "otr_etag" };
    }
    
    public static final String getOtrEtag(ContentResolver paramContentResolver, long paramLong)
    {
      Object localObject = null;
      Cursor localCursor = paramContentResolver.query(CONTENT_URI, CONTACT_OTR_ETAG_PROJECTION, "account=" + paramLong, null, null);
      paramContentResolver = (ContentResolver)localObject;
      try
      {
        if (localCursor.moveToFirst()) {
          paramContentResolver = localCursor.getString(COLUMN_OTR_ETAG);
        }
        return paramContentResolver;
      }
      finally
      {
        localCursor.close();
      }
    }
    
    public static final String getRosterEtag(ContentResolver paramContentResolver, long paramLong)
    {
      Object localObject = null;
      Cursor localCursor = paramContentResolver.query(CONTENT_URI, CONTACT_ETAG_PROJECTION, "account=" + paramLong, null, null);
      paramContentResolver = (ContentResolver)localObject;
      try
      {
        if (localCursor.moveToFirst()) {
          paramContentResolver = localCursor.getString(COLUMN_ETAG);
        }
        return paramContentResolver;
      }
      finally
      {
        localCursor.close();
      }
    }
    
    public static final Cursor query(ContentResolver paramContentResolver, String[] paramArrayOfString)
    {
      return paramContentResolver.query(CONTENT_URI, paramArrayOfString, null, null, null);
    }
    
    public static final Cursor query(ContentResolver paramContentResolver, String[] paramArrayOfString, String paramString1, String paramString2)
    {
      Uri localUri = CONTENT_URI;
      if (paramString2 == null) {
        paramString2 = null;
      }
      for (;;)
      {
        return paramContentResolver.query(localUri, paramArrayOfString, paramString1, null, paramString2);
      }
    }
  }
  
  public static abstract interface ContactsEtagColumns
  {
    public static final String ACCOUNT = "account";
    public static final String ETAG = "etag";
    public static final String OTR_ETAG = "otr_etag";
  }
  
  public static abstract interface GroupMemberColumns
  {
    public static final String GROUP = "groupId";
    public static final String NICKNAME = "nickname";
    public static final String USERNAME = "username";
  }
  
  public static final class GroupMembers
    implements TalkContract.GroupMemberColumns
  {
    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/gtalk-groupMembers";
    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/gtalk-groupMembers";
    public static final Uri CONTENT_URI = Uri.parse("content://com.google.android.providers.talk/groupMembers");
  }
  
  public static final class Invitation
    implements TalkContract.InvitationColumns, BaseColumns
  {
    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/gtalk-invitations";
    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/gtalk-invitations";
    public static final Uri CONTENT_URI = Uri.parse("content://com.google.android.providers.talk/invitations");
  }
  
  public static abstract interface InvitationColumns
  {
    public static final String ACCOUNT = "accountId";
    public static final String GROUP_NAME = "groupName";
    public static final String INVITE_ID = "inviteId";
    public static final String NOTE = "note";
    public static final String SENDER = "sender";
    public static final String STATUS = "status";
    public static final int STATUS_ACCEPTED = 1;
    public static final int STATUS_PENDING = 0;
    public static final int STATUS_REJECTED = 2;
  }
  
  public static final class LastRmqId
    implements BaseColumns, TalkContract.LastRmqIdColumns
  {
    public static final Uri CONTENT_URI = Uri.parse("content://com.google.android.providers.talk/lastRmqId");
    private static String[] PROJECTION = { "rmq_id" };
    
    public static final long queryLastRmqId(ContentResolver paramContentResolver)
    {
      paramContentResolver = paramContentResolver.query(CONTENT_URI, PROJECTION, null, null, null);
      long l = 0L;
      try
      {
        if (paramContentResolver.moveToFirst()) {
          l = paramContentResolver.getLong(paramContentResolver.getColumnIndexOrThrow("rmq_id"));
        }
        return l;
      }
      finally
      {
        paramContentResolver.close();
      }
    }
    
    public static final void saveLastRmqId(ContentResolver paramContentResolver, long paramLong)
    {
      ContentValues localContentValues = new ContentValues();
      localContentValues.put("_id", Integer.valueOf(1));
      localContentValues.put("rmq_id", Long.valueOf(paramLong));
      paramContentResolver.insert(CONTENT_URI, localContentValues);
    }
  }
  
  public static abstract interface LastRmqIdColumns
  {
    public static final String RMQ_ID = "rmq_id";
  }
  
  public static abstract interface MessageColumns
  {
    public static final String BODY = "body";
    public static final String CONSOLIDATION_KEY = "consolidation_key";
    public static final String DATE = "date";
    public static final String DISPLAY_SENT_TIME = "show_ts";
    public static final String ERROR_CODE = "err_code";
    public static final String ERROR_MESSAGE = "err_msg";
    public static final String IS_GROUP_CHAT = "is_muc";
    public static final String MESSAGE_READ_KEY = "message_read";
    public static final String NICKNAME = "nickname";
    public static final String PACKET_ID = "packet_id";
    public static final String REAL_DATE = "real_date";
    public static final String SEND_STATUS_KEY = "send_status";
    public static final String THREAD_ID = "thread_id";
    public static final String TYPE = "type";
  }
  
  public static abstract interface MessageType
  {
    public static final int CONVERT_TO_GROUPCHAT = 6;
    public static final int END_CAUSE_MESSAGE = 16;
    public static final int INCOMING = 1;
    public static final int MISSED_CALL_MESSAGE = 15;
    public static final int NEW_STATUS_MESSAGE = 13;
    public static final int OTR_IS_TURNED_OFF = 9;
    public static final int OTR_IS_TURNED_ON = 10;
    public static final int OTR_TURNED_ON_BY_BUDDY = 12;
    public static final int OTR_TURNED_ON_BY_USER = 11;
    public static final int OUTGOING = 0;
    public static final int PRESENCE_AVAILABLE = 2;
    public static final int PRESENCE_AWAY = 3;
    public static final int PRESENCE_DND = 4;
    public static final int PRESENCE_UNAVAILABLE = 5;
    public static final int STATUS = 7;
    public static final int STATUS_MESSAGE = 14;
  }
  
  public static final class Messages
    implements BaseColumns, TalkContract.MessageColumns
  {
    public static final String CONTACT = "contact";
    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/gtalk-messages";
    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/gtalk-messages";
    public static final Uri CONTENT_URI = Uri.parse("content://com.google.android.providers.talk/messages");
    public static final Uri CONTENT_URI_BY_ACCOUNT;
    public static final Uri CONTENT_URI_MESSAGES_BY_ACCOUNT_AND_CONTACT;
    public static final Uri CONTENT_URI_MESSAGES_BY_THREAD_ID = Uri.parse("content://com.google.android.providers.talk/messagesByThreadId");
    public static final String DEFAULT_SORT_ORDER = "date ASC";
    public static final Uri OTR_MESSAGES_CONTENT_URI;
    public static final Uri OTR_MESSAGES_CONTENT_URI_BY_ACCOUNT = Uri.parse("content://com.google.android.providers.talk/otrMessagesByAccount");
    public static final Uri OTR_MESSAGES_CONTENT_URI_BY_ACCOUNT_AND_CONTACT;
    public static final Uri OTR_MESSAGES_CONTENT_URI_BY_THREAD_ID;
    
    static
    {
      CONTENT_URI_MESSAGES_BY_ACCOUNT_AND_CONTACT = Uri.parse("content://com.google.android.providers.talk/messagesByAcctAndContact");
      CONTENT_URI_BY_ACCOUNT = Uri.parse("content://com.google.android.providers.talk/messagesByAccount");
      OTR_MESSAGES_CONTENT_URI = Uri.parse("content://com.google.android.providers.talk/otrMessages");
      OTR_MESSAGES_CONTENT_URI_BY_THREAD_ID = Uri.parse("content://com.google.android.providers.talk/otrMessagesByThreadId");
      OTR_MESSAGES_CONTENT_URI_BY_ACCOUNT_AND_CONTACT = Uri.parse("content://com.google.android.providers.talk/otrMessagesByAcctAndContact");
    }
    
    public static final Uri getContentUriByAccount(long paramLong)
    {
      Uri.Builder localBuilder = CONTENT_URI_BY_ACCOUNT.buildUpon();
      ContentUris.appendId(localBuilder, paramLong);
      return localBuilder.build();
    }
    
    @Deprecated
    public static final Uri getContentUriByContact(long paramLong, String paramString)
    {
      Uri.Builder localBuilder = CONTENT_URI_MESSAGES_BY_ACCOUNT_AND_CONTACT.buildUpon();
      ContentUris.appendId(localBuilder, paramLong);
      localBuilder.appendPath(paramString);
      return localBuilder.build();
    }
    
    public static final Uri getContentUriByThreadId(long paramLong)
    {
      Uri.Builder localBuilder = CONTENT_URI_MESSAGES_BY_THREAD_ID.buildUpon();
      ContentUris.appendId(localBuilder, paramLong);
      return localBuilder.build();
    }
    
    public static final Uri getOtrMessagesContentUriByAccount(long paramLong)
    {
      Uri.Builder localBuilder = OTR_MESSAGES_CONTENT_URI_BY_ACCOUNT.buildUpon();
      ContentUris.appendId(localBuilder, paramLong);
      return localBuilder.build();
    }
    
    @Deprecated
    public static final Uri getOtrMessagesContentUriByContact(long paramLong, String paramString)
    {
      Uri.Builder localBuilder = OTR_MESSAGES_CONTENT_URI_BY_ACCOUNT_AND_CONTACT.buildUpon();
      ContentUris.appendId(localBuilder, paramLong);
      localBuilder.appendPath(paramString);
      return localBuilder.build();
    }
    
    public static final Uri getOtrMessagesContentUriByThreadId(long paramLong)
    {
      Uri.Builder localBuilder = OTR_MESSAGES_CONTENT_URI_BY_THREAD_ID.buildUpon();
      ContentUris.appendId(localBuilder, paramLong);
      return localBuilder.build();
    }
  }
  
  public static abstract interface OffTheRecordType
  {
    public static final int DISABLED = 0;
    public static final int ENABLED = 1;
    public static final int ENABLED_BY_BUDDY = 3;
    public static final int ENABLED_BY_USER = 2;
  }
  
  public static final class OutgoingRmq
    implements BaseColumns, TalkContract.OutgoingRmqColumns
  {
    public static final Uri CONTENT_URI = Uri.parse("content://com.google.android.providers.talk/outgoingRmqMessages");
    public static final Uri CONTENT_URI_FOR_HIGHEST_RMQ_ID = Uri.parse("content://com.google.android.providers.talk/outgoingHighestRmqId");
    public static final String DEFAULT_SORT_ORDER = "rmq_id ASC";
    private static String[] RMQ_ID_PROJECTION = { "rmq_id" };
    
    public static final long queryHighestRmqId(ContentResolver paramContentResolver)
    {
      paramContentResolver = paramContentResolver.query(CONTENT_URI_FOR_HIGHEST_RMQ_ID, RMQ_ID_PROJECTION, null, null, null);
      long l = 0L;
      try
      {
        if (paramContentResolver.moveToFirst()) {
          l = paramContentResolver.getLong(paramContentResolver.getColumnIndexOrThrow("rmq_id"));
        }
        return l;
      }
      finally
      {
        paramContentResolver.close();
      }
    }
  }
  
  public static abstract interface OutgoingRmqColumns
  {
    public static final String ACCOUNT_ID = "account";
    public static final String DATA = "data";
    public static final String PACKET_ID = "packet_id";
    public static final String PROTOBUF_TAG = "type";
    public static final String RMQ_ID = "rmq_id";
    public static final String TIMESTAMP = "ts";
  }
  
  public static final class Presence
    implements BaseColumns, TalkContract.PresenceColumns
  {
    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/gtalk-presence";
    public static final Uri CONTENT_URI = Uri.parse("content://com.google.android.providers.talk/presence");
    public static final Uri CONTENT_URI_BY_ACCOUNT = Uri.parse("content://com.google.android.providers.talk/presence/account");
    public static final String DEFAULT_SORT_ORDER = "mode DESC";
  }
  
  public static abstract interface PresenceColumns
    extends TalkContract.CommonPresenceColumns
  {
    public static final String CAPABILITIES = "cap";
    public static final int CAPABILITY_HAS_CAMERA_V1 = 4;
    public static final int CAPABILITY_HAS_PMUC_V1 = 8;
    public static final int CAPABILITY_HAS_VIDEO_V1 = 2;
    public static final int CAPABILITY_HAS_VOICE_V1 = 1;
    public static final String CLIENT_TYPE = "client_type";
    public static final int CLIENT_TYPE_ANDROID = 2;
    public static final int CLIENT_TYPE_DEFAULT = 0;
    public static final int CLIENT_TYPE_MOBILE = 1;
    public static final String CONTACT_ID = "contact_id";
    public static final String JID_RESOURCE = "jid_resource";
  }
  
  public static abstract interface ProviderNames
  {
    public static final String AIM = "AIM";
    public static final String GTALK = "GTalk";
    public static final String ICQ = "ICQ";
    public static final String JABBER = "JABBER";
    public static final String MSN = "MSN";
    public static final String QQ = "QQ";
    public static final String SKYPE = "SKYPE";
    public static final String XMPP = "XMPP";
    public static final String YAHOO = "Yahoo";
  }
  
  public static abstract interface SendingStatus
  {
    public static final int DELIVERED = 3;
    public static final int SENDING = 1;
    public static final int SENT = 2;
  }
  
  public static final class ServerToDeviceRmqIds
    implements BaseColumns, TalkContract.ServerToDeviceRmqIdsColumn
  {
    public static final Uri CONTENT_URI = Uri.parse("content://com.google.android.providers.talk/s2dids");
  }
  
  public static abstract interface ServerToDeviceRmqIdsColumn
  {
    public static final String RMQ_ID = "rmq_id";
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gsf/TalkContract.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */