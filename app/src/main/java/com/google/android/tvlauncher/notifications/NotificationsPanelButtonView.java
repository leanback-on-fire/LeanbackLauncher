package com.google.android.tvlauncher.notifications;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Outline;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewOutlineProvider;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class NotificationsPanelButtonView
  extends FrameLayout
{
  private int mAllNotifsSeenFocusedColor;
  private int mAllNotifsSeenUnfocusedColor;
  private Drawable mBackground;
  private ImageView mCircle;
  private String mNumberFormat;
  private boolean mSeen;
  private TextView mTextView;
  private int mUnseenNotifsColor;
  
  public NotificationsPanelButtonView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    paramContext = paramContext.getResources();
    this.mAllNotifsSeenUnfocusedColor = paramContext.getColor(2131820757, null);
    this.mAllNotifsSeenFocusedColor = paramContext.getColor(2131820689, null);
    this.mUnseenNotifsColor = paramContext.getColor(2131820691, null);
    this.mNumberFormat = paramContext.getString(R.string.number_format);
    this.mBackground = paramContext.getDrawable(2130837588, null);
    this.mBackground.setTint(this.mAllNotifsSeenUnfocusedColor);
  }
  
  protected void onFinishInflate()
  {
    super.onFinishInflate();
    this.mCircle = ((ImageView)findViewById(R.id.notification_panel_background_circle));
    this.mCircle.setImageDrawable(this.mBackground);
    this.mTextView = ((TextView)findViewById(R.id.notification_panel_count));
    setOutlineProvider(new ViewOutlineProvider()
    {
      public void getOutline(View paramAnonymousView, Outline paramAnonymousOutline)
      {
        paramAnonymousOutline.setOval(0, 0, paramAnonymousView.getMeasuredWidth(), paramAnonymousView.getMeasuredHeight());
      }
    });
    setClipToOutline(true);
    setOnFocusChangeListener(new View.OnFocusChangeListener()
    {
      public void onFocusChange(View paramAnonymousView, boolean paramAnonymousBoolean)
      {
        if (NotificationsPanelButtonView.this.mSeen)
        {
          paramAnonymousView = NotificationsPanelButtonView.this.mBackground;
          if (!paramAnonymousBoolean) {
            break label61;
          }
          i = NotificationsPanelButtonView.this.mAllNotifsSeenFocusedColor;
          paramAnonymousView.setTint(i);
          paramAnonymousView = NotificationsPanelButtonView.this.mTextView;
          if (!paramAnonymousBoolean) {
            break label72;
          }
        }
        label61:
        label72:
        for (int i = NotificationsPanelButtonView.this.mAllNotifsSeenFocusedColor;; i = NotificationsPanelButtonView.this.mAllNotifsSeenUnfocusedColor)
        {
          paramAnonymousView.setTextColor(i);
          return;
          i = NotificationsPanelButtonView.this.mAllNotifsSeenUnfocusedColor;
          break;
        }
      }
    });
  }
  
  public void setCount(int paramInt)
  {
    this.mTextView.setText(String.format(this.mNumberFormat, new Object[] { Integer.valueOf(paramInt) }));
    setContentDescription(getResources().getQuantityString(2131427329, paramInt, new Object[] { Integer.valueOf(paramInt) }));
  }
  
  public void setSeenState(boolean paramBoolean)
  {
    this.mSeen = paramBoolean;
    if (this.mSeen)
    {
      Object localObject = this.mBackground;
      if (hasFocus())
      {
        i = this.mAllNotifsSeenFocusedColor;
        ((Drawable)localObject).setTint(i);
        localObject = this.mTextView;
        if (!hasFocus()) {
          break label65;
        }
      }
      label65:
      for (int i = this.mAllNotifsSeenFocusedColor;; i = this.mAllNotifsSeenUnfocusedColor)
      {
        ((TextView)localObject).setTextColor(i);
        return;
        i = this.mAllNotifsSeenUnfocusedColor;
        break;
      }
    }
    this.mBackground.setTint(this.mUnseenNotifsColor);
    this.mTextView.setTextColor(this.mUnseenNotifsColor);
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/notifications/NotificationsPanelButtonView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */