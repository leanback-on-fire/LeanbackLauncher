package com.google.android.tvlauncher.notifications;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.text.Layout;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalFocusChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.tvlauncher.analytics.EventLogger;
import com.google.android.tvlauncher.analytics.LogEvent;
import com.google.android.tvlauncher.analytics.UserActionEvent;
import com.google.android.tvlauncher.util.Util;

public class NotificationPanelItemView
  extends LinearLayout
  implements ViewTreeObserver.OnGlobalFocusChangeListener
{
  protected EventLogger mEventLogger;
  private TextView mExpandedText;
  private ImageView mIcon;
  private boolean mIsRtl;
  protected View mMainContentText;
  protected String mNotificationKey;
  private int mProgress;
  private RectF mProgressBounds;
  private int mProgressColor;
  private int mProgressDiameter;
  private int mProgressMax;
  private int mProgressMaxColor;
  private Paint mProgressMaxPaint;
  private int mProgressPaddingStart;
  private int mProgressPaddingTop;
  private Paint mProgressPaint;
  private int mProgressStrokeWidth;
  private TextView mText;
  private TextView mTitle;
  
  public NotificationPanelItemView(Context paramContext)
  {
    super(paramContext);
  }
  
  public NotificationPanelItemView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  private boolean isContentTextCutOff()
  {
    Layout localLayout = this.mText.getLayout();
    if (localLayout != null)
    {
      int i = localLayout.getLineCount();
      if ((i > 0) && (localLayout.getEllipsisCount(i - 1) > 0)) {
        return true;
      }
    }
    return false;
  }
  
  protected void collapseText()
  {
    this.mExpandedText.setVisibility(8);
    this.mTitle.setMaxLines(1);
    this.mText.setVisibility(0);
    setBackgroundColor(0);
  }
  
  protected void expandText()
  {
    this.mText.setVisibility(8);
    this.mTitle.setMaxLines(2);
    this.mExpandedText.setVisibility(0);
    setBackgroundColor(getResources().getColor(R.color.notification_expanded_text_background));
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    getViewTreeObserver().addOnGlobalFocusChangeListener(this);
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    getViewTreeObserver().removeOnGlobalFocusChangeListener(this);
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    super.onDraw(paramCanvas);
    float f;
    if (this.mProgressMax != 0)
    {
      f = this.mProgress * 360.0F / this.mProgressMax;
      if (this.mIsRtl)
      {
        paramCanvas.drawArc(this.mProgressBounds, -90.0F, -f, false, this.mProgressPaint);
        paramCanvas.drawArc(this.mProgressBounds, -90.0F, 360.0F - f, false, this.mProgressMaxPaint);
      }
    }
    else
    {
      return;
    }
    paramCanvas.drawArc(this.mProgressBounds, -90.0F, f, false, this.mProgressPaint);
    paramCanvas.drawArc(this.mProgressBounds, f - 90.0F, 360.0F - f, false, this.mProgressMaxPaint);
  }
  
  protected void onFinishInflate()
  {
    super.onFinishInflate();
    this.mIcon = ((ImageView)findViewById(R.id.notification_icon));
    this.mTitle = ((TextView)findViewById(R.id.notification_title));
    this.mText = ((TextView)findViewById(R.id.notification_text));
    this.mMainContentText = findViewById(R.id.notification_container);
    this.mExpandedText = ((TextView)findViewById(R.id.notification_expanded_text));
    this.mIsRtl = Util.isRtl(getContext());
    Resources localResources = getResources();
    this.mProgressStrokeWidth = localResources.getDimensionPixelSize(R.dimen.notification_progress_stroke_width);
    this.mProgressColor = localResources.getColor(2131820692, null);
    this.mProgressMaxColor = localResources.getColor(2131820693, null);
    this.mProgressDiameter = localResources.getDimensionPixelSize(R.dimen.notification_progress_circle_size);
    this.mProgressPaddingTop = localResources.getDimensionPixelOffset(R.dimen.notification_progress_circle_padding_top);
    this.mProgressPaddingStart = localResources.getDimensionPixelOffset(R.dimen.notification_progress_circle_padding_start);
    this.mProgressPaint = new Paint();
    this.mProgressPaint.setAntiAlias(true);
    this.mProgressPaint.setStyle(Paint.Style.STROKE);
    this.mProgressPaint.setColor(this.mProgressColor);
    this.mProgressPaint.setStrokeWidth(this.mProgressStrokeWidth);
    this.mProgressMaxPaint = new Paint();
    this.mProgressMaxPaint.setAntiAlias(true);
    this.mProgressMaxPaint.setStyle(Paint.Style.STROKE);
    this.mProgressMaxPaint.setColor(this.mProgressMaxColor);
    this.mProgressMaxPaint.setStrokeWidth(this.mProgressStrokeWidth);
    this.mMainContentText.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        if (NotificationPanelItemView.this.mNotificationKey != null)
        {
          NotificationPanelItemView.this.mEventLogger.log(new UserActionEvent("open_notification").putParameter("placement", "panel").putParameter("key", NotificationPanelItemView.this.mNotificationKey));
          NotificationsUtils.openNotification(paramAnonymousView.getContext(), NotificationPanelItemView.this.mNotificationKey);
        }
      }
    });
  }
  
  public void onGlobalFocusChanged(View paramView1, View paramView2)
  {
    paramView1 = getFocusedChild();
    if (paramView1 == null) {
      collapseText();
    }
    while (((paramView2 != paramView1) && (paramView2.getParent() != paramView1)) || (!isContentTextCutOff())) {
      return;
    }
    expandText();
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    int i;
    if (this.mProgressBounds != null)
    {
      paramInt4 = this.mProgressPaddingTop;
      i = this.mProgressDiameter;
      if (!this.mIsRtl) {
        break label73;
      }
      paramInt2 = paramInt3 - this.mProgressPaddingStart;
      paramInt1 = paramInt2 - this.mProgressDiameter;
    }
    for (;;)
    {
      this.mProgressBounds.set(paramInt1, paramInt4, paramInt2, paramInt4 + i);
      return;
      label73:
      paramInt1 = this.mProgressPaddingStart;
      paramInt2 = paramInt1 + this.mProgressDiameter;
    }
  }
  
  public void setNotification(TvNotification paramTvNotification, EventLogger paramEventLogger)
  {
    this.mEventLogger = paramEventLogger;
    this.mNotificationKey = paramTvNotification.getNotificationKey();
    this.mTitle.setText(paramTvNotification.getTitle());
    this.mText.setText(paramTvNotification.getText());
    if (!TextUtils.isEmpty(paramTvNotification.getTitle())) {
      if (!TextUtils.isEmpty(paramTvNotification.getText()))
      {
        paramEventLogger = getResources().getString(R.string.notification_content_description_format);
        this.mMainContentText.setContentDescription(String.format(paramEventLogger, new Object[] { paramTvNotification.getTitle(), paramTvNotification.getText() }));
      }
    }
    for (;;)
    {
      this.mExpandedText.setText(paramTvNotification.getText());
      this.mIcon.setImageIcon(paramTvNotification.getSmallIcon());
      setProgress(paramTvNotification.getProgress(), paramTvNotification.getProgressMax());
      this.mMainContentText.setVisibility(0);
      collapseText();
      return;
      this.mMainContentText.setContentDescription(paramTvNotification.getTitle());
      continue;
      this.mMainContentText.setContentDescription(paramTvNotification.getText());
    }
  }
  
  public void setProgress(int paramInt1, int paramInt2)
  {
    this.mProgress = paramInt1;
    this.mProgressMax = paramInt2;
    if (this.mProgressMax != 0)
    {
      if (this.mProgressBounds == null) {
        this.mProgressBounds = new RectF();
      }
      setWillNotDraw(false);
    }
    for (;;)
    {
      requestLayout();
      return;
      this.mProgressBounds = null;
      setWillNotDraw(true);
    }
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/notifications/NotificationPanelItemView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */