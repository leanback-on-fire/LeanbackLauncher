package android.support.v17.preference;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View.OnKeyListener;
import android.widget.FrameLayout;

@RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
public class LeanbackSettingsRootView
  extends FrameLayout
{
  private View.OnKeyListener mOnBackKeyListener;
  
  public LeanbackSettingsRootView(Context paramContext)
  {
    super(paramContext);
  }
  
  public LeanbackSettingsRootView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  public LeanbackSettingsRootView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
  }
  
  public boolean dispatchKeyEvent(@NonNull KeyEvent paramKeyEvent)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (paramKeyEvent.getAction() == 1)
    {
      bool1 = bool2;
      if (paramKeyEvent.getKeyCode() == 4)
      {
        bool1 = bool2;
        if (this.mOnBackKeyListener != null) {
          bool1 = this.mOnBackKeyListener.onKey(this, paramKeyEvent.getKeyCode(), paramKeyEvent);
        }
      }
    }
    return (bool1) || (super.dispatchKeyEvent(paramKeyEvent));
  }
  
  public void setOnBackKeyListener(View.OnKeyListener paramOnKeyListener)
  {
    this.mOnBackKeyListener = paramOnKeyListener;
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/preference/LeanbackSettingsRootView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */