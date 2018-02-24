package android.support.v17.leanback.widget;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.RestrictTo;
import android.support.v17.leanback.R.id;
import android.util.AttributeSet;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

@RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
public class PlaybackTransportRowView
  extends LinearLayout
{
  private OnUnhandledKeyListener mOnUnhandledKeyListener;
  
  public PlaybackTransportRowView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  public PlaybackTransportRowView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
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
  
  public View focusSearch(View paramView, int paramInt)
  {
    if (paramView != null)
    {
      int i;
      Object localObject;
      if (paramInt == 33)
      {
        i = indexOfChild(getFocusedChild()) - 1;
        while (i >= 0)
        {
          localObject = getChildAt(i);
          if (((View)localObject).hasFocusable()) {
            return (View)localObject;
          }
          i -= 1;
        }
      }
      if (paramInt == 130)
      {
        i = indexOfChild(getFocusedChild()) + 1;
        for (;;)
        {
          if (i >= getChildCount()) {
            break label140;
          }
          View localView = getChildAt(i);
          localObject = localView;
          if (localView.hasFocusable()) {
            break;
          }
          i += 1;
        }
      }
      if (((paramInt == 17) || (paramInt == 66)) && ((getFocusedChild() instanceof ViewGroup))) {
        return FocusFinder.getInstance().findNextFocus((ViewGroup)getFocusedChild(), paramView, paramInt);
      }
    }
    label140:
    return super.focusSearch(paramView, paramInt);
  }
  
  OnUnhandledKeyListener getOnUnhandledKeyListener()
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
    if ((localView != null) && (localView.requestFocus(paramInt, paramRect))) {}
    do
    {
      return true;
      localView = findViewById(R.id.playback_progress);
    } while ((localView != null) && (localView.isFocusable()) && (localView.requestFocus(paramInt, paramRect)));
    return super.onRequestFocusInDescendants(paramInt, paramRect);
  }
  
  void setOnUnhandledKeyListener(OnUnhandledKeyListener paramOnUnhandledKeyListener)
  {
    this.mOnUnhandledKeyListener = paramOnUnhandledKeyListener;
  }
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  public static abstract interface OnUnhandledKeyListener
  {
    public abstract boolean onUnhandledKey(KeyEvent paramKeyEvent);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/PlaybackTransportRowView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */