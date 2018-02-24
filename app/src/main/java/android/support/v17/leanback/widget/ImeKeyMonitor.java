package android.support.v17.leanback.widget;

import android.view.KeyEvent;
import android.widget.EditText;

public abstract interface ImeKeyMonitor
{
  public abstract void setImeKeyListener(ImeKeyListener paramImeKeyListener);
  
  public static abstract interface ImeKeyListener
  {
    public abstract boolean onKeyPreIme(EditText paramEditText, int paramInt, KeyEvent paramKeyEvent);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/ImeKeyMonitor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */