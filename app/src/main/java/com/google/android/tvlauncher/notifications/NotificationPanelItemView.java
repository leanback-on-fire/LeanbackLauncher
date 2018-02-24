package com.google.android.tvlauncher.notifications;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Layout;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalFocusChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.tvlauncher.R;
import com.google.android.tvlauncher.analytics.EventLogger;
import com.google.android.tvlauncher.analytics.LogEvents;
import com.google.android.tvlauncher.analytics.UserActionEvent;
import com.google.android.tvlauncher.util.Util;

public class NotificationPanelItemView extends LinearLayout implements OnGlobalFocusChangeListener {
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

    public NotificationPanelItemView(Context context) {
        super(context);
    }

    public NotificationPanelItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalFocusChangeListener(this);
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getViewTreeObserver().removeOnGlobalFocusChangeListener(this);
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mIcon = (ImageView) findViewById(R.id.notification_icon);
        this.mTitle = (TextView) findViewById(R.id.notification_title);
        this.mText = (TextView) findViewById(R.id.notification_text);
        this.mMainContentText = findViewById(R.id.notification_container);
        this.mExpandedText = (TextView) findViewById(R.id.notification_expanded_text);
        this.mIsRtl = Util.isRtl(getContext());
        Resources res = getResources();
        this.mProgressStrokeWidth = res.getDimensionPixelSize(R.dimen.notification_progress_stroke_width);
        this.mProgressColor = ResourcesCompat.getColor(res, R.color.notification_progress_stroke_color, null);
        this.mProgressMaxColor = ResourcesCompat.getColor(res, R.color.notification_progress_stroke_max_color, null);
        this.mProgressDiameter = res.getDimensionPixelSize(R.dimen.notification_progress_circle_size);
        this.mProgressPaddingTop = res.getDimensionPixelOffset(R.dimen.notification_progress_circle_padding_top);
        this.mProgressPaddingStart = res.getDimensionPixelOffset(R.dimen.notification_progress_circle_padding_start);
        this.mProgressPaint = new Paint();
        this.mProgressPaint.setAntiAlias(true);
        this.mProgressPaint.setStyle(Style.STROKE);
        this.mProgressPaint.setColor(this.mProgressColor);
        this.mProgressPaint.setStrokeWidth((float) this.mProgressStrokeWidth);
        this.mProgressMaxPaint = new Paint();
        this.mProgressMaxPaint.setAntiAlias(true);
        this.mProgressMaxPaint.setStyle(Style.STROKE);
        this.mProgressMaxPaint.setColor(this.mProgressMaxColor);
        this.mProgressMaxPaint.setStrokeWidth((float) this.mProgressStrokeWidth);
        this.mMainContentText.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (NotificationPanelItemView.this.mNotificationKey != null) {
                    NotificationPanelItemView.this.mEventLogger.log(new UserActionEvent(LogEvents.OPEN_NOTIFICATION).putParameter("placement", LogEvents.NOTIFICATION_PLACEMENT_PANEL).putParameter("key", NotificationPanelItemView.this.mNotificationKey));
                    NotificationsUtils.openNotification(view.getContext(), NotificationPanelItemView.this.mNotificationKey);
                }
            }
        });
    }

    public void setNotification(TvNotification notif, EventLogger eventLogger) {
        this.mEventLogger = eventLogger;
        this.mNotificationKey = notif.getNotificationKey();
        this.mTitle.setText(notif.getTitle());
        this.mText.setText(notif.getText());
        if (TextUtils.isEmpty(notif.getTitle())) {
            this.mMainContentText.setContentDescription(notif.getText());
        } else if (TextUtils.isEmpty(notif.getText())) {
            this.mMainContentText.setContentDescription(notif.getTitle());
        } else {
            String formatting = getResources().getString(R.string.notification_content_description_format);
            this.mMainContentText.setContentDescription(String.format(formatting, new Object[]{notif.getTitle(), notif.getText()}));
        }
        this.mExpandedText.setText(notif.getText());
        this.mIcon.setImageIcon(notif.getSmallIcon());
        setProgress(notif.getProgress(), notif.getProgressMax());
        this.mMainContentText.setVisibility(0);
        collapseText();
    }

    public void setProgress(int progress, int progressMax) {
        this.mProgress = progress;
        this.mProgressMax = progressMax;
        if (this.mProgressMax != 0) {
            if (this.mProgressBounds == null) {
                this.mProgressBounds = new RectF();
            }
            setWillNotDraw(false);
        } else {
            this.mProgressBounds = null;
            setWillNotDraw(true);
        }
        requestLayout();
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (this.mProgressBounds != null) {
            int right;
            int left;
            int top = this.mProgressPaddingTop;
            int bottom = top + this.mProgressDiameter;
            if (this.mIsRtl) {
                right = r - this.mProgressPaddingStart;
                left = right - this.mProgressDiameter;
            } else {
                left = this.mProgressPaddingStart;
                right = left + this.mProgressDiameter;
            }
            this.mProgressBounds.set((float) left, (float) top, (float) right, (float) bottom);
        }
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.mProgressMax != 0) {
            float sweepAngle = (((float) this.mProgress) * 360.0f) / ((float) this.mProgressMax);
            if (this.mIsRtl) {
                canvas.drawArc(this.mProgressBounds, -90.0f, -sweepAngle, false, this.mProgressPaint);
                canvas.drawArc(this.mProgressBounds, -90.0f, 360.0f - sweepAngle, false, this.mProgressMaxPaint);
                return;
            }
            canvas.drawArc(this.mProgressBounds, -90.0f, sweepAngle, false, this.mProgressPaint);
            canvas.drawArc(this.mProgressBounds, sweepAngle - 90.0f, 360.0f - sweepAngle, false, this.mProgressMaxPaint);
        }
    }

    private boolean isContentTextCutOff() {
        Layout layout = this.mText.getLayout();
        if (layout != null) {
            int lines = layout.getLineCount();
            if (lines > 0 && layout.getEllipsisCount(lines - 1) > 0) {
                return true;
            }
        }
        return false;
    }

    protected void expandText() {
        this.mText.setVisibility(8);
        this.mTitle.setMaxLines(2);
        this.mExpandedText.setVisibility(0);
        setBackgroundColor(getResources().getColor(R.color.notification_expanded_text_background));
    }

    protected void collapseText() {
        this.mExpandedText.setVisibility(8);
        this.mTitle.setMaxLines(1);
        this.mText.setVisibility(0);
        setBackgroundColor(0);
    }

    public void onGlobalFocusChanged(View oldFocus, View newFocus) {
        Object currentFocus = getFocusedChild();
        if (currentFocus == null) {
            collapseText();
        } else if ((newFocus == currentFocus || newFocus.getParent() == currentFocus) && isContentTextCutOff()) {
            expandText();
        }
    }
}
