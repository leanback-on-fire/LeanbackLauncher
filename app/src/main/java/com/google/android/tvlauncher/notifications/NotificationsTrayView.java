package com.google.android.tvlauncher.notifications;

import android.content.Context;
import android.content.res.Resources;
import android.support.v17.leanback.widget.HorizontalGridView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class NotificationsTrayView
  extends FrameLayout
{
  private HorizontalGridView mNotificationsRow;
  
  public NotificationsTrayView(Context paramContext)
  {
    super(paramContext);
  }
  
  public NotificationsTrayView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  public NotificationsTrayAdapter getTrayAdapter()
  {
    if (this.mNotificationsRow != null) {
      return (NotificationsTrayAdapter)this.mNotificationsRow.getAdapter();
    }
    return null;
  }
  
  protected void onFinishInflate()
  {
    super.onFinishInflate();
    this.mNotificationsRow = ((HorizontalGridView)findViewById(R.id.notifications_row));
    this.mNotificationsRow.setWindowAlignment(0);
    this.mNotificationsRow.setWindowAlignmentOffsetPercent(0.0F);
    this.mNotificationsRow.setWindowAlignmentOffset(getContext().getResources().getDimensionPixelSize(R.dimen.notifications_list_padding_start));
    this.mNotificationsRow.setItemAlignmentOffsetPercent(0.0F);
  }
  
  public void setTrayAdapter(NotificationsTrayAdapter paramNotificationsTrayAdapter)
  {
    this.mNotificationsRow.setAdapter(paramNotificationsTrayAdapter);
    updateVisibility();
  }
  
  public void updateVisibility()
  {
    if ((this.mNotificationsRow.getAdapter() != null) && (this.mNotificationsRow.getAdapter().getItemCount() > 0)) {}
    for (int i = 0;; i = 8)
    {
      setVisibility(i);
      return;
    }
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/notifications/NotificationsTrayView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */