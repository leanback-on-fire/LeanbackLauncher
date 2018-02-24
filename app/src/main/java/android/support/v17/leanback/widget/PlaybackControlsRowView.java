package android.support.v17.leanback.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;

class PlaybackControlsRowView
  extends LinearLayout
{
  private OnUnhandledKeyListener mOnUnhandledKeyListener;
  
  public PlaybackControlsRowView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  public PlaybackControlsRowView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
  }
  
  public boolean dispatchKeyEvent(KeyEvent paramKeyEvent)
  {
    if (super.dispatchKeyEvent(paramKeyEvent)) {}
    while ((this.mOnUnhandledKeyListener != null) && (this.mOnUnhandledKeyListener.onUnhandledKey(paramKeyEvent))) {
      return true;
    }
    return false;
  }
  
  public OnUnhandledKeyListener getOnUnhandledKeyListener()
  {
    return this.mOnUnhandledKeyListener;
  }
  
  public boolean hasOverlappingRendering()
  {
    return false;
  }
  
  protected boolean onRequestFocusInDescendants(int paramInt, Rect paramRect)
  {
    View localView = findFocus();
    if ((localView != null) && (localView.requestFocus(paramInt, paramRect))) {
      return true;
    }
    return super.onRequestFocusInDescendants(paramInt, paramRect);
  }
  
  public void setOnUnhandledKeyListener(OnUnhandledKeyListener paramOnUnhandledKeyListener)
  {
    this.mOnUnhandledKeyListener = paramOnUnhandledKeyListener;
  }
  
  public static abstract interface OnUnhandledKeyListener
  {
    public abstract boolean onUnhandledKey(KeyEvent paramKeyEvent);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/PlaybackControlsRowView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */