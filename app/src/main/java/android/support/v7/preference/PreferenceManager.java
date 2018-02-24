package android.support.v7.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.SharedPreferencesCompat.EditorCompat;
import android.text.TextUtils;

public class PreferenceManager
{
  public static final String KEY_HAS_SET_DEFAULT_VALUES = "_has_set_default_values";
  private static final int STORAGE_DEFAULT = 0;
  private static final int STORAGE_DEVICE_PROTECTED = 1;
  private Context mContext;
  @Nullable
  private SharedPreferences.Editor mEditor;
  private long mNextId = 0L;
  private boolean mNoCommit;
  private OnDisplayPreferenceDialogListener mOnDisplayPreferenceDialogListener;
  private OnNavigateToScreenListener mOnNavigateToScreenListener;
  private OnPreferenceTreeClickListener mOnPreferenceTreeClickListener;
  private PreferenceComparisonCallback mPreferenceComparisonCallback;
  @Nullable
  private PreferenceDataStore mPreferenceDataStore;
  private PreferenceScreen mPreferenceScreen;
  @Nullable
  private SharedPreferences mSharedPreferences;
  private int mSharedPreferencesMode;
  private String mSharedPreferencesName;
  private int mStorage = 0;
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  public PreferenceManager(Context paramContext)
  {
    this.mContext = paramContext;
    setSharedPreferencesName(getDefaultSharedPreferencesName(paramContext));
  }
  
  public static SharedPreferences getDefaultSharedPreferences(Context paramContext)
  {
    return paramContext.getSharedPreferences(getDefaultSharedPreferencesName(paramContext), getDefaultSharedPreferencesMode());
  }
  
  private static int getDefaultSharedPreferencesMode()
  {
    return 0;
  }
  
  private static String getDefaultSharedPreferencesName(Context paramContext)
  {
    return paramContext.getPackageName() + "_preferences";
  }
  
  public static void setDefaultValues(Context paramContext, int paramInt, boolean paramBoolean)
  {
    setDefaultValues(paramContext, getDefaultSharedPreferencesName(paramContext), getDefaultSharedPreferencesMode(), paramInt, paramBoolean);
  }
  
  public static void setDefaultValues(Context paramContext, String paramString, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    SharedPreferences localSharedPreferences = paramContext.getSharedPreferences("_has_set_default_values", 0);
    if ((paramBoolean) || (!localSharedPreferences.getBoolean("_has_set_default_values", false)))
    {
      PreferenceManager localPreferenceManager = new PreferenceManager(paramContext);
      localPreferenceManager.setSharedPreferencesName(paramString);
      localPreferenceManager.setSharedPreferencesMode(paramInt1);
      localPreferenceManager.inflateFromResource(paramContext, paramInt2, null);
      paramContext = localSharedPreferences.edit().putBoolean("_has_set_default_values", true);
      SharedPreferencesCompat.EditorCompat.getInstance().apply(paramContext);
    }
  }
  
  private void setNoCommit(boolean paramBoolean)
  {
    if ((!paramBoolean) && (this.mEditor != null)) {
      SharedPreferencesCompat.EditorCompat.getInstance().apply(this.mEditor);
    }
    this.mNoCommit = paramBoolean;
  }
  
  public PreferenceScreen createPreferenceScreen(Context paramContext)
  {
    paramContext = new PreferenceScreen(paramContext, null);
    paramContext.onAttachedToHierarchy(this);
    return paramContext;
  }
  
  public Preference findPreference(CharSequence paramCharSequence)
  {
    if (this.mPreferenceScreen == null) {
      return null;
    }
    return this.mPreferenceScreen.findPreference(paramCharSequence);
  }
  
  public Context getContext()
  {
    return this.mContext;
  }
  
  SharedPreferences.Editor getEditor()
  {
    if (this.mPreferenceDataStore != null) {
      return null;
    }
    if (this.mNoCommit)
    {
      if (this.mEditor == null) {
        this.mEditor = getSharedPreferences().edit();
      }
      return this.mEditor;
    }
    return getSharedPreferences().edit();
  }
  
  long getNextId()
  {
    try
    {
      long l = this.mNextId;
      this.mNextId = (1L + l);
      return l;
    }
    finally {}
  }
  
  public OnDisplayPreferenceDialogListener getOnDisplayPreferenceDialogListener()
  {
    return this.mOnDisplayPreferenceDialogListener;
  }
  
  public OnNavigateToScreenListener getOnNavigateToScreenListener()
  {
    return this.mOnNavigateToScreenListener;
  }
  
  public OnPreferenceTreeClickListener getOnPreferenceTreeClickListener()
  {
    return this.mOnPreferenceTreeClickListener;
  }
  
  public PreferenceComparisonCallback getPreferenceComparisonCallback()
  {
    return this.mPreferenceComparisonCallback;
  }
  
  @Nullable
  public PreferenceDataStore getPreferenceDataStore()
  {
    return this.mPreferenceDataStore;
  }
  
  public PreferenceScreen getPreferenceScreen()
  {
    return this.mPreferenceScreen;
  }
  
  public SharedPreferences getSharedPreferences()
  {
    if (getPreferenceDataStore() != null) {
      return null;
    }
    if (this.mSharedPreferences == null) {
      switch (this.mStorage)
      {
      }
    }
    for (Context localContext = this.mContext;; localContext = ContextCompat.createDeviceProtectedStorageContext(this.mContext))
    {
      this.mSharedPreferences = localContext.getSharedPreferences(this.mSharedPreferencesName, this.mSharedPreferencesMode);
      return this.mSharedPreferences;
    }
  }
  
  public int getSharedPreferencesMode()
  {
    return this.mSharedPreferencesMode;
  }
  
  public String getSharedPreferencesName()
  {
    return this.mSharedPreferencesName;
  }
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  public PreferenceScreen inflateFromResource(Context paramContext, int paramInt, PreferenceScreen paramPreferenceScreen)
  {
    setNoCommit(true);
    paramContext = (PreferenceScreen)new PreferenceInflater(paramContext, this).inflate(paramInt, paramPreferenceScreen);
    paramContext.onAttachedToHierarchy(this);
    setNoCommit(false);
    return paramContext;
  }
  
  public boolean isStorageDefault()
  {
    return (Build.VERSION.SDK_INT < 24) || (this.mStorage == 0);
  }
  
  public boolean isStorageDeviceProtected()
  {
    if (Build.VERSION.SDK_INT >= 24) {
      return this.mStorage == 1;
    }
    return false;
  }
  
  public void setOnDisplayPreferenceDialogListener(OnDisplayPreferenceDialogListener paramOnDisplayPreferenceDialogListener)
  {
    this.mOnDisplayPreferenceDialogListener = paramOnDisplayPreferenceDialogListener;
  }
  
  public void setOnNavigateToScreenListener(OnNavigateToScreenListener paramOnNavigateToScreenListener)
  {
    this.mOnNavigateToScreenListener = paramOnNavigateToScreenListener;
  }
  
  public void setOnPreferenceTreeClickListener(OnPreferenceTreeClickListener paramOnPreferenceTreeClickListener)
  {
    this.mOnPreferenceTreeClickListener = paramOnPreferenceTreeClickListener;
  }
  
  public void setPreferenceComparisonCallback(PreferenceComparisonCallback paramPreferenceComparisonCallback)
  {
    this.mPreferenceComparisonCallback = paramPreferenceComparisonCallback;
  }
  
  public void setPreferenceDataStore(PreferenceDataStore paramPreferenceDataStore)
  {
    this.mPreferenceDataStore = paramPreferenceDataStore;
  }
  
  public boolean setPreferences(PreferenceScreen paramPreferenceScreen)
  {
    if (paramPreferenceScreen != this.mPreferenceScreen)
    {
      if (this.mPreferenceScreen != null) {
        this.mPreferenceScreen.onDetached();
      }
      this.mPreferenceScreen = paramPreferenceScreen;
      return true;
    }
    return false;
  }
  
  public void setSharedPreferencesMode(int paramInt)
  {
    this.mSharedPreferencesMode = paramInt;
    this.mSharedPreferences = null;
  }
  
  public void setSharedPreferencesName(String paramString)
  {
    this.mSharedPreferencesName = paramString;
    this.mSharedPreferences = null;
  }
  
  public void setStorageDefault()
  {
    if (Build.VERSION.SDK_INT >= 24)
    {
      this.mStorage = 0;
      this.mSharedPreferences = null;
    }
  }
  
  public void setStorageDeviceProtected()
  {
    if (Build.VERSION.SDK_INT >= 24)
    {
      this.mStorage = 1;
      this.mSharedPreferences = null;
    }
  }
  
  boolean shouldCommit()
  {
    return !this.mNoCommit;
  }
  
  public void showDialog(Preference paramPreference)
  {
    if (this.mOnDisplayPreferenceDialogListener != null) {
      this.mOnDisplayPreferenceDialogListener.onDisplayPreferenceDialog(paramPreference);
    }
  }
  
  public static abstract interface OnDisplayPreferenceDialogListener
  {
    public abstract void onDisplayPreferenceDialog(Preference paramPreference);
  }
  
  public static abstract interface OnNavigateToScreenListener
  {
    public abstract void onNavigateToScreen(PreferenceScreen paramPreferenceScreen);
  }
  
  public static abstract interface OnPreferenceTreeClickListener
  {
    public abstract boolean onPreferenceTreeClick(Preference paramPreference);
  }
  
  public static abstract class PreferenceComparisonCallback
  {
    public abstract boolean arePreferenceContentsTheSame(Preference paramPreference1, Preference paramPreference2);
    
    public abstract boolean arePreferenceItemsTheSame(Preference paramPreference1, Preference paramPreference2);
  }
  
  public static class SimplePreferenceComparisonCallback
    extends PreferenceManager.PreferenceComparisonCallback
  {
    public boolean arePreferenceContentsTheSame(Preference paramPreference1, Preference paramPreference2)
    {
      if (paramPreference1.getClass() != paramPreference2.getClass()) {
        return false;
      }
      if ((paramPreference1 == paramPreference2) && (paramPreference1.wasDetached())) {
        return false;
      }
      if (!TextUtils.equals(paramPreference1.getTitle(), paramPreference2.getTitle())) {
        return false;
      }
      if (!TextUtils.equals(paramPreference1.getSummary(), paramPreference2.getSummary())) {
        return false;
      }
      Drawable localDrawable1 = paramPreference1.getIcon();
      Drawable localDrawable2 = paramPreference2.getIcon();
      if ((localDrawable1 != localDrawable2) && ((localDrawable1 == null) || (!localDrawable1.equals(localDrawable2)))) {
        return false;
      }
      if (paramPreference1.isEnabled() != paramPreference2.isEnabled()) {
        return false;
      }
      if (paramPreference1.isSelectable() != paramPreference2.isSelectable()) {
        return false;
      }
      if (((paramPreference1 instanceof TwoStatePreference)) && (((TwoStatePreference)paramPreference1).isChecked() != ((TwoStatePreference)paramPreference2).isChecked())) {
        return false;
      }
      return (!(paramPreference1 instanceof DropDownPreference)) || (paramPreference1 == paramPreference2);
    }
    
    public boolean arePreferenceItemsTheSame(Preference paramPreference1, Preference paramPreference2)
    {
      return paramPreference1.getId() == paramPreference2.getId();
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v7/preference/PreferenceManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */