package com.google.android.tvlauncher.notifications;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Icon;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.tvlauncher.analytics.EventLogger;
import com.google.android.tvlauncher.analytics.LogEvent;
import com.google.android.tvlauncher.analytics.UserActionEvent;

public class NotificationsTrayItemView
  extends LinearLayout
{
  private ImageView mBigPicture;
  private Button mDismissButton;
  private EventLogger mEventLogger;
  private ImageView mIcon;
  private String mNotificationKey;
  private View mNowPlayingIndicator;
  private Button mSeeMoreButton;
  private TextView mText;
  private TextView mTitle;
  
  public NotificationsTrayItemView(Context paramContext)
  {
    super(paramContext);
  }
  
  public NotificationsTrayItemView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  public NotificationsTrayItemView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
  }
  
  protected void onFinishInflate()
  {
    super.onFinishInflate();
    this.mBigPicture = ((ImageView)findViewById(R.id.big_picture));
    this.mIcon = ((ImageView)findViewById(R.id.small_icon));
    this.mTitle = ((TextView)findViewById(R.id.notif_title));
    this.mText = ((TextView)findViewById(R.id.notif_text));
    this.mDismissButton = ((Button)findViewById(R.id.tray_dismiss));
    this.mSeeMoreButton = ((Button)findViewById(R.id.tray_see_more));
    this.mNowPlayingIndicator = findViewById(R.id.now_playing_bars);
  }
  
  public void setNotification(TvNotification paramTvNotification, EventLogger paramEventLogger)
  {
    this.mNotificationKey = paramTvNotification.getNotificationKey();
    this.mEventLogger = paramEventLogger;
    paramEventLogger = (AccessibilityManager)getContext().getSystemService("accessibility");
    if ((paramEventLogger != null) && (paramEventLogger.isEnabled()))
    {
      setFocusable(true);
      if ((!"Notification.NowPlaying".equals(paramTvNotification.getTag())) || (paramTvNotification.getBigPicture() == null)) {
        break label271;
      }
      this.mBigPicture.setImageBitmap(paramTvNotification.getBigPicture());
      this.mBigPicture.setVisibility(0);
      this.mNowPlayingIndicator.setVisibility(0);
      this.mIcon.setVisibility(8);
      label97:
      this.mTitle.setText(paramTvNotification.getTitle());
      if (!TextUtils.isEmpty(paramTvNotification.getText())) {
        break label370;
      }
      this.mText.setVisibility(8);
      label127:
      if (TextUtils.isEmpty(paramTvNotification.getTitle())) {
        break label403;
      }
      if (TextUtils.isEmpty(paramTvNotification.getText())) {
        break label392;
      }
      setContentDescription(String.format(getResources().getString(R.string.notification_content_description_format), new Object[] { paramTvNotification.getTitle(), paramTvNotification.getText() }));
      label181:
      if (!paramTvNotification.hasContentIntent()) {
        break label414;
      }
      this.mSeeMoreButton.setVisibility(0);
      this.mSeeMoreButton.setText(paramTvNotification.getContentButtonLabel());
      this.mSeeMoreButton.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          NotificationsTrayItemView.this.mEventLogger.log(new UserActionEvent("open_notification").putParameter("placement", "tray").putParameter("key", NotificationsTrayItemView.this.mNotificationKey));
          NotificationsUtils.openNotification(NotificationsTrayItemView.this.getContext(), NotificationsTrayItemView.this.mNotificationKey);
        }
      });
    }
    for (;;)
    {
      this.mDismissButton.setText(paramTvNotification.getDismissButtonLabel());
      if ((paramTvNotification.isDismissible()) && (!paramTvNotification.isOngoing())) {
        break label426;
      }
      this.mDismissButton.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          NotificationsTrayItemView.this.mEventLogger.log(new UserActionEvent("hide_notification").putParameter("key", NotificationsTrayItemView.this.mNotificationKey));
          NotificationsUtils.hideNotification(paramAnonymousView.getContext(), NotificationsTrayItemView.this.mNotificationKey);
        }
      });
      return;
      setFocusable(false);
      break;
      label271:
      if ("com.android.systemui.pip.tv.PipNotification".equals(paramTvNotification.getTag()))
      {
        this.mNowPlayingIndicator.setVisibility(8);
        paramEventLogger = paramTvNotification.getSmallIcon();
        paramEventLogger.setTint(getResources().getColor(2131820686, null));
        this.mIcon.setImageIcon(paramEventLogger);
        this.mIcon.setVisibility(0);
        this.mBigPicture.setVisibility(8);
        break label97;
      }
      this.mNowPlayingIndicator.setVisibility(8);
      this.mBigPicture.setVisibility(8);
      this.mIcon.setVisibility(8);
      break label97;
      label370:
      this.mText.setText(paramTvNotification.getText());
      this.mText.setVisibility(0);
      break label127;
      label392:
      setContentDescription(paramTvNotification.getTitle());
      break label181;
      label403:
      setContentDescription(paramTvNotification.getText());
      break label181;
      label414:
      this.mSeeMoreButton.setVisibility(8);
    }
    label426:
    this.mDismissButton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        NotificationsTrayItemView.this.mEventLogger.log(new UserActionEvent("dismiss_notification").putParameter("placement", "tray").putParameter("key", NotificationsTrayItemView.this.mNotificationKey));
        NotificationsUtils.dismissNotification(paramAnonymousView.getContext(), NotificationsTrayItemView.this.mNotificationKey);
      }
    });
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/notifications/NotificationsTrayItemView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */