package android.support.v17.leanback.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.FrameLayout;

public class BrowseFrameLayout
  extends FrameLayout
{
  private OnFocusSearchListener mListener;
  private OnChildFocusListener mOnChildFocusListener;
  private View.OnKeyListener mOnDispatchKeyListener;
  
  public BrowseFrameLayout(Context paramContext)
  {
    this(paramContext, null, 0);
  }
  
  public BrowseFrameLayout(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public BrowseFrameLayout(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
  }
  
  public boolean dispatchKeyEvent(KeyEvent paramKeyEvent)
  {
    boolean bool2 = super.dispatchKeyEvent(paramKeyEvent);
    boolean bool1 = bool2;
    if (this.mOnDispatchKeyListener != null)
    {
      bool1 = bool2;
      if (!bool2) {
        bool1 = this.mOnDispatchKeyListener.onKey(getRootView(), paramKeyEvent.getKeyCode(), paramKeyEvent);
      }
    }
    return bool1;
  }
  
  public View focusSearch(View paramView, int paramInt)
  {
    if (this.mListener != null)
    {
      View localView = this.mListener.onFocusSearch(paramView, paramInt);
      if (localView != null) {
        return localView;
      }
    }
    return super.focusSearch(paramView, paramInt);
  }
  
  public OnChildFocusListener getOnChildFocusListener()
  {
    return this.mOnChildFocusListener;
  }
  
  public OnFocusSearchListener getOnFocusSearchListener()
  {
    return this.mListener;
  }
  
  protected boolean onRequestFocusInDescendants(int paramInt, Rect paramRect)
  {
    if ((this.mOnChildFocusListener != null) && (this.mOnChildFocusListener.onRequestFocusInDescendants(paramInt, paramRect))) {
      return true;
    }
    return super.onRequestFocusInDescendants(paramInt, paramRect);
  }
  
  public void requestChildFocus(View paramView1, View paramView2)
  {
    if (this.mOnChildFocusListener != null) {
      this.mOnChildFocusListener.onRequestChildFocus(paramView1, paramView2);
    }
    super.requestChildFocus(paramView1, paramView2);
  }
  
  public void setOnChildFocusListener(OnChildFocusListener paramOnChildFocusListener)
  {
    this.mOnChildFocusListener = paramOnChildFocusListener;
  }
  
  public void setOnDispatchKeyListener(View.OnKeyListener paramOnKeyListener)
  {
    this.mOnDispatchKeyListener = paramOnKeyListener;
  }
  
  public void setOnFocusSearchListener(OnFocusSearchListener paramOnFocusSearchListener)
  {
    this.mListener = paramOnFocusSearchListener;
  }
  
  public static abstract interface OnChildFocusListener
  {
    public abstract void onRequestChildFocus(View paramView1, View paramView2);
    
    public abstract boolean onRequestFocusInDescendants(int paramInt, Rect paramRect);
  }
  
  public static abstract interface OnFocusSearchListener
  {
    public abstract View onFocusSearch(View paramView, int paramInt);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/BrowseFrameLayout.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */