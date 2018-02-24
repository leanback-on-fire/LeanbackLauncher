package android.support.v14.preference;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import android.support.v7.preference.DialogPreference;
import android.support.v7.preference.DialogPreference.TargetFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

public abstract class PreferenceDialogFragment
  extends DialogFragment
  implements DialogInterface.OnClickListener
{
  protected static final String ARG_KEY = "key";
  private static final String SAVE_STATE_ICON = "PreferenceDialogFragment.icon";
  private static final String SAVE_STATE_LAYOUT = "PreferenceDialogFragment.layout";
  private static final String SAVE_STATE_MESSAGE = "PreferenceDialogFragment.message";
  private static final String SAVE_STATE_NEGATIVE_TEXT = "PreferenceDialogFragment.negativeText";
  private static final String SAVE_STATE_POSITIVE_TEXT = "PreferenceDialogFragment.positiveText";
  private static final String SAVE_STATE_TITLE = "PreferenceDialogFragment.title";
  private BitmapDrawable mDialogIcon;
  @LayoutRes
  private int mDialogLayoutRes;
  private CharSequence mDialogMessage;
  private CharSequence mDialogTitle;
  private CharSequence mNegativeButtonText;
  private CharSequence mPositiveButtonText;
  private DialogPreference mPreference;
  private int mWhichButtonClicked;
  
  private void requestInputMethod(Dialog paramDialog)
  {
    paramDialog.getWindow().setSoftInputMode(5);
  }
  
  public DialogPreference getPreference()
  {
    if (this.mPreference == null)
    {
      String str = getArguments().getString("key");
      this.mPreference = ((DialogPreference)((DialogPreference.TargetFragment)getTargetFragment()).findPreference(str));
    }
    return this.mPreference;
  }
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  protected boolean needInputMethod()
  {
    return false;
  }
  
  protected void onBindDialogView(View paramView)
  {
    paramView = paramView.findViewById(16908299);
    if (paramView != null)
    {
      CharSequence localCharSequence = this.mDialogMessage;
      int i = 8;
      if (!TextUtils.isEmpty(localCharSequence))
      {
        if ((paramView instanceof TextView)) {
          ((TextView)paramView).setText(localCharSequence);
        }
        i = 0;
      }
      if (paramView.getVisibility() != i) {
        paramView.setVisibility(i);
      }
    }
  }
  
  public void onClick(DialogInterface paramDialogInterface, int paramInt)
  {
    this.mWhichButtonClicked = paramInt;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    Object localObject1 = getTargetFragment();
    if (!(localObject1 instanceof DialogPreference.TargetFragment)) {
      throw new IllegalStateException("Target fragment must implement TargetFragment interface");
    }
    localObject1 = (DialogPreference.TargetFragment)localObject1;
    Object localObject2 = getArguments().getString("key");
    if (paramBundle == null)
    {
      this.mPreference = ((DialogPreference)((DialogPreference.TargetFragment)localObject1).findPreference((CharSequence)localObject2));
      this.mDialogTitle = this.mPreference.getDialogTitle();
      this.mPositiveButtonText = this.mPreference.getPositiveButtonText();
      this.mNegativeButtonText = this.mPreference.getNegativeButtonText();
      this.mDialogMessage = this.mPreference.getDialogMessage();
      this.mDialogLayoutRes = this.mPreference.getDialogLayoutResource();
      paramBundle = this.mPreference.getDialogIcon();
      if ((paramBundle == null) || ((paramBundle instanceof BitmapDrawable))) {
        this.mDialogIcon = ((BitmapDrawable)paramBundle);
      }
    }
    do
    {
      return;
      localObject1 = Bitmap.createBitmap(paramBundle.getIntrinsicWidth(), paramBundle.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
      localObject2 = new Canvas((Bitmap)localObject1);
      paramBundle.setBounds(0, 0, ((Canvas)localObject2).getWidth(), ((Canvas)localObject2).getHeight());
      paramBundle.draw((Canvas)localObject2);
      this.mDialogIcon = new BitmapDrawable(getResources(), (Bitmap)localObject1);
      return;
      this.mDialogTitle = paramBundle.getCharSequence("PreferenceDialogFragment.title");
      this.mPositiveButtonText = paramBundle.getCharSequence("PreferenceDialogFragment.positiveText");
      this.mNegativeButtonText = paramBundle.getCharSequence("PreferenceDialogFragment.negativeText");
      this.mDialogMessage = paramBundle.getCharSequence("PreferenceDialogFragment.message");
      this.mDialogLayoutRes = paramBundle.getInt("PreferenceDialogFragment.layout", 0);
      paramBundle = (Bitmap)paramBundle.getParcelable("PreferenceDialogFragment.icon");
    } while (paramBundle == null);
    this.mDialogIcon = new BitmapDrawable(getResources(), paramBundle);
  }
  
  @NonNull
  public Dialog onCreateDialog(Bundle paramBundle)
  {
    Object localObject = getActivity();
    this.mWhichButtonClicked = -2;
    paramBundle = new AlertDialog.Builder((Context)localObject).setTitle(this.mDialogTitle).setIcon(this.mDialogIcon).setPositiveButton(this.mPositiveButtonText, this).setNegativeButton(this.mNegativeButtonText, this);
    localObject = onCreateDialogView((Context)localObject);
    if (localObject != null)
    {
      onBindDialogView((View)localObject);
      paramBundle.setView((View)localObject);
    }
    for (;;)
    {
      onPrepareDialogBuilder(paramBundle);
      paramBundle = paramBundle.create();
      if (needInputMethod()) {
        requestInputMethod(paramBundle);
      }
      return paramBundle;
      paramBundle.setMessage(this.mDialogMessage);
    }
  }
  
  protected View onCreateDialogView(Context paramContext)
  {
    int i = this.mDialogLayoutRes;
    if (i == 0) {
      return null;
    }
    return LayoutInflater.from(paramContext).inflate(i, null);
  }
  
  public abstract void onDialogClosed(boolean paramBoolean);
  
  public void onDismiss(DialogInterface paramDialogInterface)
  {
    super.onDismiss(paramDialogInterface);
    if (this.mWhichButtonClicked == -1) {}
    for (boolean bool = true;; bool = false)
    {
      onDialogClosed(bool);
      return;
    }
  }
  
  protected void onPrepareDialogBuilder(AlertDialog.Builder paramBuilder) {}
  
  public void onSaveInstanceState(@NonNull Bundle paramBundle)
  {
    super.onSaveInstanceState(paramBundle);
    paramBundle.putCharSequence("PreferenceDialogFragment.title", this.mDialogTitle);
    paramBundle.putCharSequence("PreferenceDialogFragment.positiveText", this.mPositiveButtonText);
    paramBundle.putCharSequence("PreferenceDialogFragment.negativeText", this.mNegativeButtonText);
    paramBundle.putCharSequence("PreferenceDialogFragment.message", this.mDialogMessage);
    paramBundle.putInt("PreferenceDialogFragment.layout", this.mDialogLayoutRes);
    if (this.mDialogIcon != null) {
      paramBundle.putParcelable("PreferenceDialogFragment.icon", this.mDialogIcon.getBitmap());
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v14/preference/PreferenceDialogFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */