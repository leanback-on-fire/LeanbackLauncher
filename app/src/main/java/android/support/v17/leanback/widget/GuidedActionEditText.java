package android.support.v17.leanback.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.EditText;
import android.widget.TextView;

public class GuidedActionEditText
  extends EditText
  implements ImeKeyMonitor
{
  private ImeKeyMonitor.ImeKeyListener mKeyListener;
  private final Drawable mNoPaddingDrawable = new NoPaddingDrawable();
  private final Drawable mSavedBackground = getBackground();
  
  public GuidedActionEditText(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public GuidedActionEditText(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 16842862);
  }
  
  public GuidedActionEditText(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    setBackground(this.mNoPaddingDrawable);
  }
  
  protected void onFocusChanged(boolean paramBoolean, int paramInt, Rect paramRect)
  {
    super.onFocusChanged(paramBoolean, paramInt, paramRect);
    if (paramBoolean) {
      setBackground(this.mSavedBackground);
    }
    for (;;)
    {
      if (!paramBoolean) {
        setFocusable(false);
      }
      return;
      setBackground(this.mNoPaddingDrawable);
    }
  }
  
  public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo paramAccessibilityNodeInfo)
  {
    super.onInitializeAccessibilityNodeInfo(paramAccessibilityNodeInfo);
    if (isFocused()) {}
    for (String str = EditText.class.getName();; str = TextView.class.getName())
    {
      paramAccessibilityNodeInfo.setClassName(str);
      return;
    }
  }
  
  public boolean onKeyPreIme(int paramInt, KeyEvent paramKeyEvent)
  {
    boolean bool1 = false;
    if (this.mKeyListener != null) {
      bool1 = this.mKeyListener.onKeyPreIme(this, paramInt, paramKeyEvent);
    }
    boolean bool2 = bool1;
    if (!bool1) {
      bool2 = super.onKeyPreIme(paramInt, paramKeyEvent);
    }
    return bool2;
  }
  
  public void setImeKeyListener(ImeKeyMonitor.ImeKeyListener paramImeKeyListener)
  {
    this.mKeyListener = paramImeKeyListener;
  }
  
  static final class NoPaddingDrawable
    extends Drawable
  {
    public void draw(Canvas paramCanvas) {}
    
    public int getOpacity()
    {
      return -2;
    }
    
    public boolean getPadding(Rect paramRect)
    {
      paramRect.set(0, 0, 0, 0);
      return true;
    }
    
    public void setAlpha(int paramInt) {}
    
    public void setColorFilter(ColorFilter paramColorFilter) {}
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/GuidedActionEditText.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */