package android.support.v17.leanback.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v17.leanback.R.string;
import android.support.v4.content.ContextCompat;
import java.util.List;

public class GuidedAction
  extends Action
{
  public static final long ACTION_ID_CANCEL = -5L;
  public static final long ACTION_ID_CONTINUE = -7L;
  public static final long ACTION_ID_CURRENT = -3L;
  public static final long ACTION_ID_FINISH = -6L;
  public static final long ACTION_ID_NEXT = -2L;
  public static final long ACTION_ID_NO = -9L;
  public static final long ACTION_ID_OK = -4L;
  public static final long ACTION_ID_YES = -8L;
  public static final int CHECKBOX_CHECK_SET_ID = -1;
  public static final int DEFAULT_CHECK_SET_ID = 1;
  static final int EDITING_ACTIVATOR_VIEW = 3;
  static final int EDITING_DESCRIPTION = 2;
  static final int EDITING_NONE = 0;
  static final int EDITING_TITLE = 1;
  public static final int NO_CHECK_SET = 0;
  static final int PF_AUTORESTORE = 64;
  static final int PF_CHECKED = 1;
  static final int PF_ENABLED = 16;
  static final int PF_FOCUSABLE = 32;
  static final int PF_HAS_NEXT = 4;
  static final int PF_INFO_ONLY = 8;
  static final int PF_MULTI_lINE_DESCRIPTION = 2;
  private static final String TAG = "GuidedAction";
  int mActionFlags;
  int mCheckSetId;
  int mDescriptionEditInputType;
  int mDescriptionInputType;
  private CharSequence mEditDescription;
  int mEditInputType;
  private CharSequence mEditTitle;
  int mEditable;
  int mInputType;
  Intent mIntent;
  List<GuidedAction> mSubActions;
  
  protected GuidedAction()
  {
    super(0L);
  }
  
  static final boolean isPasswordVariant(int paramInt)
  {
    paramInt &= 0xFF0;
    return (paramInt == 128) || (paramInt == 144) || (paramInt == 224);
  }
  
  private void setFlags(int paramInt1, int paramInt2)
  {
    this.mActionFlags = (this.mActionFlags & (paramInt2 ^ 0xFFFFFFFF) | paramInt1 & paramInt2);
  }
  
  public int getCheckSetId()
  {
    return this.mCheckSetId;
  }
  
  public CharSequence getDescription()
  {
    return getLabel2();
  }
  
  public int getDescriptionEditInputType()
  {
    return this.mDescriptionEditInputType;
  }
  
  public int getDescriptionInputType()
  {
    return this.mDescriptionInputType;
  }
  
  public CharSequence getEditDescription()
  {
    return this.mEditDescription;
  }
  
  public int getEditInputType()
  {
    return this.mEditInputType;
  }
  
  public CharSequence getEditTitle()
  {
    return this.mEditTitle;
  }
  
  public int getInputType()
  {
    return this.mInputType;
  }
  
  public Intent getIntent()
  {
    return this.mIntent;
  }
  
  public List<GuidedAction> getSubActions()
  {
    return this.mSubActions;
  }
  
  public CharSequence getTitle()
  {
    return getLabel1();
  }
  
  public boolean hasEditableActivatorView()
  {
    return this.mEditable == 3;
  }
  
  public boolean hasMultilineDescription()
  {
    return (this.mActionFlags & 0x2) == 2;
  }
  
  public boolean hasNext()
  {
    return (this.mActionFlags & 0x4) == 4;
  }
  
  public boolean hasSubActions()
  {
    return this.mSubActions != null;
  }
  
  public boolean hasTextEditable()
  {
    return (this.mEditable == 1) || (this.mEditable == 2);
  }
  
  public boolean infoOnly()
  {
    return (this.mActionFlags & 0x8) == 8;
  }
  
  public final boolean isAutoSaveRestoreEnabled()
  {
    return (this.mActionFlags & 0x40) == 64;
  }
  
  public boolean isChecked()
  {
    return (this.mActionFlags & 0x1) == 1;
  }
  
  public boolean isDescriptionEditable()
  {
    return this.mEditable == 2;
  }
  
  public boolean isEditTitleUsed()
  {
    return this.mEditTitle != null;
  }
  
  public boolean isEditable()
  {
    return this.mEditable == 1;
  }
  
  public boolean isEnabled()
  {
    return (this.mActionFlags & 0x10) == 16;
  }
  
  public boolean isFocusable()
  {
    return (this.mActionFlags & 0x20) == 32;
  }
  
  final boolean needAutoSaveDescription()
  {
    return (isDescriptionEditable()) && (!isPasswordVariant(getDescriptionEditInputType()));
  }
  
  final boolean needAutoSaveTitle()
  {
    return (isEditable()) && (!isPasswordVariant(getEditInputType()));
  }
  
  public void onRestoreInstanceState(Bundle paramBundle, String paramString)
  {
    if (needAutoSaveTitle())
    {
      paramBundle = paramBundle.getString(paramString);
      if (paramBundle != null) {
        setTitle(paramBundle);
      }
    }
    do
    {
      do
      {
        return;
        if (!needAutoSaveDescription()) {
          break;
        }
        paramBundle = paramBundle.getString(paramString);
      } while (paramBundle == null);
      setDescription(paramBundle);
      return;
    } while (getCheckSetId() == 0);
    setChecked(paramBundle.getBoolean(paramString, isChecked()));
  }
  
  public void onSaveInstanceState(Bundle paramBundle, String paramString)
  {
    if ((needAutoSaveTitle()) && (getTitle() != null)) {
      paramBundle.putString(paramString, getTitle().toString());
    }
    do
    {
      return;
      if ((needAutoSaveDescription()) && (getDescription() != null))
      {
        paramBundle.putString(paramString, getDescription().toString());
        return;
      }
    } while (getCheckSetId() == 0);
    paramBundle.putBoolean(paramString, isChecked());
  }
  
  public void setChecked(boolean paramBoolean)
  {
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      setFlags(i, 1);
      return;
    }
  }
  
  public void setDescription(CharSequence paramCharSequence)
  {
    setLabel2(paramCharSequence);
  }
  
  public void setEditDescription(CharSequence paramCharSequence)
  {
    this.mEditDescription = paramCharSequence;
  }
  
  public void setEditTitle(CharSequence paramCharSequence)
  {
    this.mEditTitle = paramCharSequence;
  }
  
  public void setEnabled(boolean paramBoolean)
  {
    if (paramBoolean) {}
    for (int i = 16;; i = 0)
    {
      setFlags(i, 16);
      return;
    }
  }
  
  public void setFocusable(boolean paramBoolean)
  {
    if (paramBoolean) {}
    for (int i = 32;; i = 0)
    {
      setFlags(i, 32);
      return;
    }
  }
  
  public void setIntent(Intent paramIntent)
  {
    this.mIntent = paramIntent;
  }
  
  public void setSubActions(List<GuidedAction> paramList)
  {
    this.mSubActions = paramList;
  }
  
  public void setTitle(CharSequence paramCharSequence)
  {
    setLabel1(paramCharSequence);
  }
  
  public static class Builder
    extends GuidedAction.BuilderBase<Builder>
  {
    @Deprecated
    public Builder()
    {
      super();
    }
    
    public Builder(Context paramContext)
    {
      super();
    }
    
    public GuidedAction build()
    {
      GuidedAction localGuidedAction = new GuidedAction();
      applyValues(localGuidedAction);
      return localGuidedAction;
    }
  }
  
  public static abstract class BuilderBase<B extends BuilderBase>
  {
    private int mActionFlags;
    private int mCheckSetId = 0;
    private Context mContext;
    private CharSequence mDescription;
    private int mDescriptionEditInputType = 1;
    private int mDescriptionInputType = 524289;
    private CharSequence mEditDescription;
    private int mEditInputType = 1;
    private CharSequence mEditTitle;
    private int mEditable = 0;
    private Drawable mIcon;
    private long mId;
    private int mInputType = 524289;
    private Intent mIntent;
    private List<GuidedAction> mSubActions;
    private CharSequence mTitle;
    
    public BuilderBase(Context paramContext)
    {
      this.mContext = paramContext;
      this.mActionFlags = 112;
    }
    
    private boolean isChecked()
    {
      return (this.mActionFlags & 0x1) == 1;
    }
    
    private void setFlags(int paramInt1, int paramInt2)
    {
      this.mActionFlags = (this.mActionFlags & (paramInt2 ^ 0xFFFFFFFF) | paramInt1 & paramInt2);
    }
    
    protected final void applyValues(GuidedAction paramGuidedAction)
    {
      paramGuidedAction.setId(this.mId);
      paramGuidedAction.setLabel1(this.mTitle);
      paramGuidedAction.setEditTitle(this.mEditTitle);
      paramGuidedAction.setLabel2(this.mDescription);
      paramGuidedAction.setEditDescription(this.mEditDescription);
      paramGuidedAction.setIcon(this.mIcon);
      paramGuidedAction.mIntent = this.mIntent;
      paramGuidedAction.mEditable = this.mEditable;
      paramGuidedAction.mInputType = this.mInputType;
      paramGuidedAction.mDescriptionInputType = this.mDescriptionInputType;
      paramGuidedAction.mEditInputType = this.mEditInputType;
      paramGuidedAction.mDescriptionEditInputType = this.mDescriptionEditInputType;
      paramGuidedAction.mActionFlags = this.mActionFlags;
      paramGuidedAction.mCheckSetId = this.mCheckSetId;
      paramGuidedAction.mSubActions = this.mSubActions;
    }
    
    public B autoSaveRestoreEnabled(boolean paramBoolean)
    {
      if (paramBoolean) {}
      for (int i = 64;; i = 0)
      {
        setFlags(i, 64);
        return this;
      }
    }
    
    public B checkSetId(int paramInt)
    {
      this.mCheckSetId = paramInt;
      if (this.mEditable != 0) {
        throw new IllegalArgumentException("Editable actions cannot also be in check sets");
      }
      return this;
    }
    
    public B checked(boolean paramBoolean)
    {
      if (paramBoolean) {}
      for (int i = 1;; i = 0)
      {
        setFlags(i, 1);
        if (this.mEditable == 0) {
          break;
        }
        throw new IllegalArgumentException("Editable actions cannot also be checked");
      }
      return this;
    }
    
    public B clickAction(long paramLong)
    {
      if (paramLong == -4L)
      {
        this.mId = -4L;
        this.mTitle = this.mContext.getString(17039370);
      }
      do
      {
        return this;
        if (paramLong == -5L)
        {
          this.mId = -5L;
          this.mTitle = this.mContext.getString(17039360);
          return this;
        }
        if (paramLong == -6L)
        {
          this.mId = -6L;
          this.mTitle = this.mContext.getString(R.string.lb_guidedaction_finish_title);
          return this;
        }
        if (paramLong == -7L)
        {
          this.mId = -7L;
          this.mTitle = this.mContext.getString(R.string.lb_guidedaction_continue_title);
          return this;
        }
        if (paramLong == -8L)
        {
          this.mId = -8L;
          this.mTitle = this.mContext.getString(17039370);
          return this;
        }
      } while (paramLong != -9L);
      this.mId = -9L;
      this.mTitle = this.mContext.getString(17039360);
      return this;
    }
    
    public B description(@StringRes int paramInt)
    {
      this.mDescription = getContext().getString(paramInt);
      return this;
    }
    
    public B description(CharSequence paramCharSequence)
    {
      this.mDescription = paramCharSequence;
      return this;
    }
    
    public B descriptionEditInputType(int paramInt)
    {
      this.mDescriptionEditInputType = paramInt;
      return this;
    }
    
    public B descriptionEditable(boolean paramBoolean)
    {
      if (!paramBoolean) {
        if (this.mEditable == 2) {
          this.mEditable = 0;
        }
      }
      do
      {
        return this;
        this.mEditable = 2;
      } while ((!isChecked()) && (this.mCheckSetId == 0));
      throw new IllegalArgumentException("Editable actions cannot also be checked");
    }
    
    public B descriptionInputType(int paramInt)
    {
      this.mDescriptionInputType = paramInt;
      return this;
    }
    
    public B editDescription(@StringRes int paramInt)
    {
      this.mEditDescription = getContext().getString(paramInt);
      return this;
    }
    
    public B editDescription(CharSequence paramCharSequence)
    {
      this.mEditDescription = paramCharSequence;
      return this;
    }
    
    public B editInputType(int paramInt)
    {
      this.mEditInputType = paramInt;
      return this;
    }
    
    public B editTitle(@StringRes int paramInt)
    {
      this.mEditTitle = getContext().getString(paramInt);
      return this;
    }
    
    public B editTitle(CharSequence paramCharSequence)
    {
      this.mEditTitle = paramCharSequence;
      return this;
    }
    
    public B editable(boolean paramBoolean)
    {
      if (!paramBoolean) {
        if (this.mEditable == 1) {
          this.mEditable = 0;
        }
      }
      do
      {
        return this;
        this.mEditable = 1;
      } while ((!isChecked()) && (this.mCheckSetId == 0));
      throw new IllegalArgumentException("Editable actions cannot also be checked");
    }
    
    public B enabled(boolean paramBoolean)
    {
      if (paramBoolean) {}
      for (int i = 16;; i = 0)
      {
        setFlags(i, 16);
        return this;
      }
    }
    
    public B focusable(boolean paramBoolean)
    {
      if (paramBoolean) {}
      for (int i = 32;; i = 0)
      {
        setFlags(i, 32);
        return this;
      }
    }
    
    public Context getContext()
    {
      return this.mContext;
    }
    
    public B hasEditableActivatorView(boolean paramBoolean)
    {
      if (!paramBoolean) {
        if (this.mEditable == 3) {
          this.mEditable = 0;
        }
      }
      do
      {
        return this;
        this.mEditable = 3;
      } while ((!isChecked()) && (this.mCheckSetId == 0));
      throw new IllegalArgumentException("Editable actions cannot also be checked");
    }
    
    public B hasNext(boolean paramBoolean)
    {
      if (paramBoolean) {}
      for (int i = 4;; i = 0)
      {
        setFlags(i, 4);
        return this;
      }
    }
    
    public B icon(@DrawableRes int paramInt)
    {
      return icon(ContextCompat.getDrawable(getContext(), paramInt));
    }
    
    public B icon(Drawable paramDrawable)
    {
      this.mIcon = paramDrawable;
      return this;
    }
    
    @Deprecated
    public B iconResourceId(@DrawableRes int paramInt, Context paramContext)
    {
      return icon(ContextCompat.getDrawable(paramContext, paramInt));
    }
    
    public B id(long paramLong)
    {
      this.mId = paramLong;
      return this;
    }
    
    public B infoOnly(boolean paramBoolean)
    {
      if (paramBoolean) {}
      for (int i = 8;; i = 0)
      {
        setFlags(i, 8);
        return this;
      }
    }
    
    public B inputType(int paramInt)
    {
      this.mInputType = paramInt;
      return this;
    }
    
    public B intent(Intent paramIntent)
    {
      this.mIntent = paramIntent;
      return this;
    }
    
    public B multilineDescription(boolean paramBoolean)
    {
      if (paramBoolean) {}
      for (int i = 2;; i = 0)
      {
        setFlags(i, 2);
        return this;
      }
    }
    
    public B subActions(List<GuidedAction> paramList)
    {
      this.mSubActions = paramList;
      return this;
    }
    
    public B title(@StringRes int paramInt)
    {
      this.mTitle = getContext().getString(paramInt);
      return this;
    }
    
    public B title(CharSequence paramCharSequence)
    {
      this.mTitle = paramCharSequence;
      return this;
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/GuidedAction.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */