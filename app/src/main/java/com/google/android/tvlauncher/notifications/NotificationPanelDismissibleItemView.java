package com.google.android.tvlauncher.notifications;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.TextView;
import com.google.android.tvlauncher.analytics.EventLogger;
import com.google.android.tvlauncher.analytics.LogEvent;
import com.google.android.tvlauncher.analytics.UserActionEvent;

public class NotificationPanelDismissibleItemView
  extends NotificationPanelItemView
{
  private View mDismissButton;
  private TextView mDismissText;
  private int mDismissTranslationX;
  private int mViewFocusTranslationX;
  
  public NotificationPanelDismissibleItemView(Context paramContext)
  {
    super(paramContext);
  }
  
  public NotificationPanelDismissibleItemView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  private void updateBackgrounds(int paramInt)
  {
    if (paramInt == 1)
    {
      this.mMainContentText.setBackgroundResource(2130837725);
      this.mDismissButton.setBackgroundResource(2130837726);
      return;
    }
    this.mMainContentText.setBackgroundResource(2130837726);
    this.mDismissButton.setBackgroundResource(2130837725);
  }
  
  protected void onFinishInflate()
  {
    super.onFinishInflate();
    this.mDismissButton = findViewById(2131951996);
    this.mDismissText = ((TextView)findViewById(2131951997));
    this.mViewFocusTranslationX = getResources().getDimensionPixelSize(2131558911);
    this.mDismissTranslationX = getResources().getDimensionPixelSize(2131558894);
    final Object localObject1 = getResources().getConfiguration();
    updateBackgrounds(((Configuration)localObject1).getLayoutDirection());
    if (((Configuration)localObject1).getLayoutDirection() == 1)
    {
      this.mViewFocusTranslationX *= -1;
      this.mDismissTranslationX *= -1;
    }
    localObject1 = new AnimatorSet();
    final Object localObject2 = ObjectAnimator.ofFloat(this.mMainContentText, View.TRANSLATION_X, new float[] { this.mViewFocusTranslationX, this.mDismissTranslationX });
    ((AnimatorSet)localObject1).playTogether(new Animator[] { ObjectAnimator.ofFloat(this.mDismissButton, View.TRANSLATION_X, new float[] { this.mViewFocusTranslationX, this.mDismissTranslationX }), localObject2 });
    ((AnimatorSet)localObject1).addListener(new AnimatorListenerAdapter()
    {
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        NotificationPanelDismissibleItemView.this.mDismissButton.setVisibility(4);
        NotificationPanelDismissibleItemView.this.mMainContentText.setVisibility(4);
        NotificationPanelDismissibleItemView.this.setBackgroundColor(NotificationPanelDismissibleItemView.this.getContext().getColor(2131820694));
        NotificationPanelDismissibleItemView.this.mEventLogger.log(new UserActionEvent("dismiss_notification").putParameter("placement", "panel").putParameter("key", NotificationPanelDismissibleItemView.this.mNotificationKey));
        NotificationsUtils.dismissNotification(NotificationPanelDismissibleItemView.this.getContext(), NotificationPanelDismissibleItemView.this.mNotificationKey);
      }
      
      public void onAnimationStart(Animator paramAnonymousAnimator)
      {
        NotificationPanelDismissibleItemView.this.collapseText();
        NotificationPanelDismissibleItemView.this.mDismissText.setVisibility(8);
      }
    });
    localObject2 = new AnimatorSet();
    final Object localObject3 = ObjectAnimator.ofFloat(this.mMainContentText, View.TRANSLATION_X, new float[] { 0.0F, this.mViewFocusTranslationX });
    ((AnimatorSet)localObject2).playTogether(new Animator[] { ObjectAnimator.ofFloat(this.mDismissButton, View.TRANSLATION_X, new float[] { 0.0F, this.mViewFocusTranslationX }), localObject3 });
    localObject3 = new AnimatorSet();
    ObjectAnimator localObjectAnimator = ObjectAnimator.ofFloat(this.mMainContentText, View.TRANSLATION_X, new float[] { this.mViewFocusTranslationX, 0.0F });
    ((AnimatorSet)localObject3).playTogether(new Animator[] { ObjectAnimator.ofFloat(this.mDismissButton, View.TRANSLATION_X, new float[] { this.mViewFocusTranslationX, 0.0F }), localObjectAnimator });
    ((AnimatorSet)localObject3).addListener(new AnimatorListenerAdapter()
    {
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        NotificationPanelDismissibleItemView.this.mDismissText.setVisibility(8);
      }
    });
    this.mDismissButton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        if (NotificationPanelDismissibleItemView.this.mNotificationKey != null) {
          localObject1.start();
        }
      }
    });
    this.mDismissButton.setOnFocusChangeListener(new View.OnFocusChangeListener()
    {
      public void onFocusChange(View paramAnonymousView, boolean paramAnonymousBoolean)
      {
        if (paramAnonymousBoolean)
        {
          NotificationPanelDismissibleItemView.this.mDismissText.setVisibility(0);
          localObject2.start();
          return;
        }
        localObject3.start();
      }
    });
  }
  
  public void setNotification(TvNotification paramTvNotification, EventLogger paramEventLogger)
  {
    super.setNotification(paramTvNotification, paramEventLogger);
    this.mDismissText.setText(paramTvNotification.getDismissButtonLabel());
    this.mDismissButton.setVisibility(0);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/notifications/NotificationPanelDismissibleItemView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */