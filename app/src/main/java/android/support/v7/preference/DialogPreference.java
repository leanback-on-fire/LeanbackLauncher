package android.support.v7.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.TypedArrayUtils;
import android.util.AttributeSet;

public abstract class DialogPreference
  extends Preference
{
  private Drawable mDialogIcon;
  private int mDialogLayoutResId;
  private CharSequence mDialogMessage;
  private CharSequence mDialogTitle;
  private CharSequence mNegativeButtonText;
  private CharSequence mPositiveButtonText;
  
  public DialogPreference(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public DialogPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, TypedArrayUtils.getAttr(paramContext, R.attr.dialogPreferenceStyle, 16842897));
  }
  
  public DialogPreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public DialogPreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.DialogPreference, paramInt1, paramInt2);
    this.mDialogTitle = TypedArrayUtils.getString(paramContext, R.styleable.DialogPreference_dialogTitle, R.styleable.DialogPreference_android_dialogTitle);
    if (this.mDialogTitle == null) {
      this.mDialogTitle = getTitle();
    }
    this.mDialogMessage = TypedArrayUtils.getString(paramContext, R.styleable.DialogPreference_dialogMessage, R.styleable.DialogPreference_android_dialogMessage);
    this.mDialogIcon = TypedArrayUtils.getDrawable(paramContext, R.styleable.DialogPreference_dialogIcon, R.styleable.DialogPreference_android_dialogIcon);
    this.mPositiveButtonText = TypedArrayUtils.getString(paramContext, R.styleable.DialogPreference_positiveButtonText, R.styleable.DialogPreference_android_positiveButtonText);
    this.mNegativeButtonText = TypedArrayUtils.getString(paramContext, R.styleable.DialogPreference_negativeButtonText, R.styleable.DialogPreference_android_negativeButtonText);
    this.mDialogLayoutResId = TypedArrayUtils.getResourceId(paramContext, R.styleable.DialogPreference_dialogLayout, R.styleable.DialogPreference_android_dialogLayout, 0);
    paramContext.recycle();
  }
  
  public Drawable getDialogIcon()
  {
    return this.mDialogIcon;
  }
  
  public int getDialogLayoutResource()
  {
    return this.mDialogLayoutResId;
  }
  
  public CharSequence getDialogMessage()
  {
    return this.mDialogMessage;
  }
  
  public CharSequence getDialogTitle()
  {
    return this.mDialogTitle;
  }
  
  public CharSequence getNegativeButtonText()
  {
    return this.mNegativeButtonText;
  }
  
  public CharSequence getPositiveButtonText()
  {
    return this.mPositiveButtonText;
  }
  
  protected void onClick()
  {
    getPreferenceManager().showDialog(this);
  }
  
  public void setDialogIcon(int paramInt)
  {
    this.mDialogIcon = ContextCompat.getDrawable(getContext(), paramInt);
  }
  
  public void setDialogIcon(Drawable paramDrawable)
  {
    this.mDialogIcon = paramDrawable;
  }
  
  public void setDialogLayoutResource(int paramInt)
  {
    this.mDialogLayoutResId = paramInt;
  }
  
  public void setDialogMessage(int paramInt)
  {
    setDialogMessage(getContext().getString(paramInt));
  }
  
  public void setDialogMessage(CharSequence paramCharSequence)
  {
    this.mDialogMessage = paramCharSequence;
  }
  
  public void setDialogTitle(int paramInt)
  {
    setDialogTitle(getContext().getString(paramInt));
  }
  
  public void setDialogTitle(CharSequence paramCharSequence)
  {
    this.mDialogTitle = paramCharSequence;
  }
  
  public void setNegativeButtonText(int paramInt)
  {
    setNegativeButtonText(getContext().getString(paramInt));
  }
  
  public void setNegativeButtonText(CharSequence paramCharSequence)
  {
    this.mNegativeButtonText = paramCharSequence;
  }
  
  public void setPositiveButtonText(int paramInt)
  {
    setPositiveButtonText(getContext().getString(paramInt));
  }
  
  public void setPositiveButtonText(CharSequence paramCharSequence)
  {
    this.mPositiveButtonText = paramCharSequence;
  }
  
  public static abstract interface TargetFragment
  {
    public abstract Preference findPreference(CharSequence paramCharSequence);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v7/preference/DialogPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */