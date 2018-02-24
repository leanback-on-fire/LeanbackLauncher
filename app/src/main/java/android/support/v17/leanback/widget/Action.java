package android.support.v17.leanback.widget;

import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import java.util.ArrayList;

public class Action
{
  public static final long NO_ID = -1L;
  private Drawable mIcon;
  private long mId = -1L;
  private ArrayList<Integer> mKeyCodes = new ArrayList();
  private CharSequence mLabel1;
  private CharSequence mLabel2;
  
  public Action(long paramLong)
  {
    this(paramLong, "");
  }
  
  public Action(long paramLong, CharSequence paramCharSequence)
  {
    this(paramLong, paramCharSequence, null);
  }
  
  public Action(long paramLong, CharSequence paramCharSequence1, CharSequence paramCharSequence2)
  {
    this(paramLong, paramCharSequence1, paramCharSequence2, null);
  }
  
  public Action(long paramLong, CharSequence paramCharSequence1, CharSequence paramCharSequence2, Drawable paramDrawable)
  {
    setId(paramLong);
    setLabel1(paramCharSequence1);
    setLabel2(paramCharSequence2);
    setIcon(paramDrawable);
  }
  
  public final void addKeyCode(int paramInt)
  {
    this.mKeyCodes.add(Integer.valueOf(paramInt));
  }
  
  public final Drawable getIcon()
  {
    return this.mIcon;
  }
  
  public final long getId()
  {
    return this.mId;
  }
  
  public final CharSequence getLabel1()
  {
    return this.mLabel1;
  }
  
  public final CharSequence getLabel2()
  {
    return this.mLabel2;
  }
  
  public final void removeKeyCode(int paramInt)
  {
    this.mKeyCodes.remove(paramInt);
  }
  
  public final boolean respondsToKeyCode(int paramInt)
  {
    return this.mKeyCodes.contains(Integer.valueOf(paramInt));
  }
  
  public final void setIcon(Drawable paramDrawable)
  {
    this.mIcon = paramDrawable;
  }
  
  public final void setId(long paramLong)
  {
    this.mId = paramLong;
  }
  
  public final void setLabel1(CharSequence paramCharSequence)
  {
    this.mLabel1 = paramCharSequence;
  }
  
  public final void setLabel2(CharSequence paramCharSequence)
  {
    this.mLabel2 = paramCharSequence;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    if (!TextUtils.isEmpty(this.mLabel1)) {
      localStringBuilder.append(this.mLabel1);
    }
    if (!TextUtils.isEmpty(this.mLabel2))
    {
      if (!TextUtils.isEmpty(this.mLabel1)) {
        localStringBuilder.append(" ");
      }
      localStringBuilder.append(this.mLabel2);
    }
    if ((this.mIcon != null) && (localStringBuilder.length() == 0)) {
      localStringBuilder.append("(action icon)");
    }
    return localStringBuilder.toString();
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/Action.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */