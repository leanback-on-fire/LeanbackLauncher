package com.google.android.tvlauncher.notifications;

import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Scene;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.Transition.TransitionListener;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.Window;
import com.google.android.tvlauncher.analytics.EventLogger;
import com.google.android.tvlauncher.analytics.LoggingActivity;
import com.google.android.tvlauncher.analytics.UserActionEvent;

public class NotificationsSidePanelActivity
  extends LoggingActivity
  implements LoaderManager.LoaderCallbacks<Cursor>
{
  private static final String TAG = "NotifsSidePanel";
  private View mNoNotifsMessage;
  private RecyclerView mNotifsList;
  private NotificationsPanelAdapter mPanelAdapter;
  
  public NotificationsSidePanelActivity()
  {
    super("NotificationSidePanel");
  }
  
  private void showNoNotificationsMessage()
  {
    this.mNotifsList.setVisibility(8);
    this.mNoNotifsMessage.setVisibility(0);
  }
  
  private void showNotifications()
  {
    this.mNoNotifsMessage.setVisibility(8);
    this.mNotifsList.setVisibility(0);
  }
  
  public void finish()
  {
    Scene localScene = new Scene((ViewGroup)findViewById(16908290));
    localScene.setEnterAction(new Runnable()
    {
      public void run()
      {
        NotificationsSidePanelActivity.this.findViewById(2131952027).setVisibility(8);
      }
    });
    Slide localSlide = new Slide(8388613);
    localSlide.addListener(new Transition.TransitionListener()
    {
      public void onTransitionCancel(Transition paramAnonymousTransition) {}
      
      public void onTransitionEnd(Transition paramAnonymousTransition)
      {
        paramAnonymousTransition.removeListener(this);
        NotificationsSidePanelActivity.this.finish();
      }
      
      public void onTransitionPause(Transition paramAnonymousTransition) {}
      
      public void onTransitionResume(Transition paramAnonymousTransition) {}
      
      public void onTransitionStart(Transition paramAnonymousTransition)
      {
        NotificationsSidePanelActivity.this.getWindow().setDimAmount(0.0F);
      }
    });
    TransitionManager.go(localScene, localSlide);
  }
  
  protected void onCreate(final Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    paramBundle = (ViewGroup)findViewById(16908290);
    this.mPanelAdapter = new NotificationsPanelAdapter(this, getEventLogger(), null);
    paramBundle.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
    {
      public boolean onPreDraw()
      {
        paramBundle.getViewTreeObserver().removeOnPreDrawListener(this);
        Scene localScene = new Scene(paramBundle);
        localScene.setEnterAction(new Runnable()
        {
          public void run()
          {
            NotificationsSidePanelActivity.this.setContentView(2130968715);
            NotificationsSidePanelActivity.access$002(NotificationsSidePanelActivity.this, NotificationsSidePanelActivity.this.findViewById(2131952028));
            NotificationsSidePanelActivity.access$102(NotificationsSidePanelActivity.this, (RecyclerView)NotificationsSidePanelActivity.this.findViewById(2131952029));
            NotificationsSidePanelActivity.this.mNotifsList.setLayoutManager(new LinearLayoutManager(NotificationsSidePanelActivity.this, 1, false));
            NotificationsSidePanelActivity.this.mNotifsList.setAdapter(NotificationsSidePanelActivity.this.mPanelAdapter);
            NotificationsSidePanelActivity.this.getLoaderManager().initLoader(0, null, NotificationsSidePanelActivity.this);
          }
        });
        TransitionManager.go(localScene, new Slide(8388613));
        return false;
      }
    });
  }
  
  public Loader<Cursor> onCreateLoader(int paramInt, Bundle paramBundle)
  {
    return new CursorLoader(this, NotificationsContract.CONTENT_URI, TvNotification.PROJECTION, null, null, null);
  }
  
  public void onLoadFinished(Loader<Cursor> paramLoader, Cursor paramCursor)
  {
    this.mPanelAdapter.changeCursor(paramCursor);
    UserActionEvent localUserActionEvent;
    if ((paramCursor != null) && (paramCursor.getCount() > 0))
    {
      showNotifications();
      paramLoader = getEventLogger();
      localUserActionEvent = new UserActionEvent("open_notification_panel");
      if (paramCursor == null) {
        break label74;
      }
    }
    label74:
    for (int i = paramCursor.getCount();; i = 0)
    {
      paramLoader.log(localUserActionEvent.putParameter("count", i));
      return;
      showNoNotificationsMessage();
      break;
    }
  }
  
  public void onLoaderReset(Loader<Cursor> paramLoader)
  {
    this.mPanelAdapter.changeCursor(null);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/notifications/NotificationsSidePanelActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */