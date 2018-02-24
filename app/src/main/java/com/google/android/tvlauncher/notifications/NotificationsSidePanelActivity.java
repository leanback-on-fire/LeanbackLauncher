package com.google.android.tvlauncher.notifications;

import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Scene;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.Transition.TransitionListener;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnPreDrawListener;

import com.google.android.tvlauncher.R;
import com.google.android.tvlauncher.analytics.LogEvents;
import com.google.android.tvlauncher.analytics.LoggingActivity;
import com.google.android.tvlauncher.analytics.UserActionEvent;

public class NotificationsSidePanelActivity extends LoggingActivity implements LoaderCallbacks<Cursor> {
    private static final String TAG = "NotifsSidePanel";
    private View mNoNotifsMessage;
    private RecyclerView mNotifsList;
    private NotificationsPanelAdapter mPanelAdapter;

    public NotificationsSidePanelActivity() {
        super("NotificationSidePanel");
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ViewGroup root = (ViewGroup) findViewById(16908290);
        this.mPanelAdapter = new NotificationsPanelAdapter(this, getEventLogger(), null);
        root.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
            public boolean onPreDraw() {
                root.getViewTreeObserver().removeOnPreDrawListener(this);
                Scene scene = new Scene(root);
                scene.setEnterAction(new Runnable() {
                    public void run() {
                        NotificationsSidePanelActivity.this.setContentView(R.layout.notifications_panel_view);
                        NotificationsSidePanelActivity.this.mNoNotifsMessage = NotificationsSidePanelActivity.this.findViewById(R.id.no_notifications_message);
                        NotificationsSidePanelActivity.this.mNotifsList = (RecyclerView) NotificationsSidePanelActivity.this.findViewById(R.id.notifications_list);
                        NotificationsSidePanelActivity.this.mNotifsList.setLayoutManager(new LinearLayoutManager(NotificationsSidePanelActivity.this, 1, false));
                        NotificationsSidePanelActivity.this.mNotifsList.setAdapter(NotificationsSidePanelActivity.this.mPanelAdapter);
                        NotificationsSidePanelActivity.this.getLoaderManager().initLoader(0, null, NotificationsSidePanelActivity.this);
                    }
                });
                TransitionManager.go(scene, new Slide(GravityCompat.END));
                return false;
            }
        });
    }

    private void showNoNotificationsMessage() {
        this.mNotifsList.setVisibility(View.GONE);
        this.mNoNotifsMessage.setVisibility(View.VISIBLE);
    }

    private void showNotifications() {
        this.mNoNotifsMessage.setVisibility(View.GONE);
        this.mNotifsList.setVisibility(View.VISIBLE);
    }

    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, NotificationsContract.CONTENT_URI, TvNotification.PROJECTION, null, null, null);
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        this.mPanelAdapter.changeCursor(data);
        if (data == null || data.getCount() <= 0) {
            showNoNotificationsMessage();
        } else {
            showNotifications();
        }
        getEventLogger().log(new UserActionEvent(LogEvents.OPEN_NOTIFICATION_PANEL).putParameter("count", data != null ? data.getCount() : 0));
    }

    public void onLoaderReset(Loader<Cursor> loader) {
        this.mPanelAdapter.changeCursor(null);
    }

    public void finish() {
        Scene scene = new Scene((ViewGroup) findViewById(16908290));
        scene.setEnterAction(new Runnable() {
            public void run() {
                NotificationsSidePanelActivity.this.findViewById(R.id.notifications_panel_view).setVisibility(View.GONE);
            }
        });
        Slide slide = new Slide(GravityCompat.END);
        slide.addListener(new TransitionListener() {
            public void onTransitionStart(Transition transition) {
                NotificationsSidePanelActivity.this.getWindow().setDimAmount(0.0f);
            }

            public void onTransitionEnd(Transition transition) {
                transition.removeListener(this);
                NotificationsSidePanelActivity.super.finish();
            }

            public void onTransitionCancel(Transition transition) {
            }

            public void onTransitionPause(Transition transition) {
            }

            public void onTransitionResume(Transition transition) {
            }
        });
        TransitionManager.go(scene, slide);
    }
}
