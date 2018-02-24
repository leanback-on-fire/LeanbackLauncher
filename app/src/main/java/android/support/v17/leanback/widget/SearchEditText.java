package android.support.v17.leanback.widget;

import android.content.Context;
import android.support.v17.leanback.R.style;
import android.util.AttributeSet;
import android.view.KeyEvent;

public class SearchEditText
  extends StreamingTextView
{
  private static final boolean DEBUG = false;
  private static final String TAG = SearchEditText.class.getSimpleName();
  private OnKeyboardDismissListener mKeyboardDismissListener;
  
  public SearchEditText(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public SearchEditText(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, R.style.TextAppearance_Leanback_SearchTextEdit);
  }
  
  public SearchEditText(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
  }
  
  public boolean onKeyPreIme(int paramInt, KeyEvent paramKeyEvent)
  {
    if (paramKeyEvent.getKeyCode() == 4)
    {
      if (this.mKeyboardDismissListener != null) {
        this.mKeyboardDismissListener.onKeyboardDismiss();
      }
      return false;
    }
    return super.onKeyPreIme(paramInt, paramKeyEvent);
  }
  
  public void setOnKeyboardDismissListener(OnKeyboardDismissListener paramOnKeyboardDismissListener)
  {
    this.mKeyboardDismissListener = paramOnKeyboardDismissListener;
  }
  
  public static abstract interface OnKeyboardDismissListener
  {
    public abstract void onKeyboardDismiss();
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/SearchEditText.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */