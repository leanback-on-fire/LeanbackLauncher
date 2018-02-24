package android.support.v7.preference;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.SharedPreferencesCompat.EditorCompat;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.AbsSavedState;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Preference
  implements Comparable<Preference>
{
  public static final int DEFAULT_ORDER = Integer.MAX_VALUE;
  private boolean mAllowDividerAbove = true;
  private boolean mAllowDividerBelow = true;
  private boolean mBaseMethodCalled;
  private final View.OnClickListener mClickListener = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      Preference.this.performClick(paramAnonymousView);
    }
  };
  private Context mContext;
  private Object mDefaultValue;
  private String mDependencyKey;
  private boolean mDependencyMet = true;
  private List<Preference> mDependents;
  private boolean mEnabled = true;
  private Bundle mExtras;
  private String mFragment;
  private boolean mHasId;
  private boolean mHasSingleLineTitleAttr;
  private Drawable mIcon;
  private int mIconResId;
  private boolean mIconSpaceReserved;
  private long mId;
  private Intent mIntent;
  private String mKey;
  private int mLayoutResId = R.layout.preference;
  private OnPreferenceChangeInternalListener mListener;
  private OnPreferenceChangeListener mOnChangeListener;
  private OnPreferenceClickListener mOnClickListener;
  private int mOrder = Integer.MAX_VALUE;
  private boolean mParentDependencyMet = true;
  private PreferenceGroup mParentGroup;
  private boolean mPersistent = true;
  @Nullable
  private PreferenceDataStore mPreferenceDataStore;
  @Nullable
  private PreferenceManager mPreferenceManager;
  private boolean mRequiresKey;
  private boolean mSelectable = true;
  private boolean mShouldDisableView = true;
  private boolean mSingleLineTitle = true;
  private CharSequence mSummary;
  private CharSequence mTitle;
  private int mViewId = 0;
  private boolean mVisible = true;
  private boolean mWasDetached;
  private int mWidgetLayoutResId;
  
  public Preference(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public Preference(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, TypedArrayUtils.getAttr(paramContext, R.attr.preferenceStyle, 16842894));
  }
  
  public Preference(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public Preference(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    this.mContext = paramContext;
    paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.Preference, paramInt1, paramInt2);
    this.mIconResId = TypedArrayUtils.getResourceId(paramContext, R.styleable.Preference_icon, R.styleable.Preference_android_icon, 0);
    this.mKey = TypedArrayUtils.getString(paramContext, R.styleable.Preference_key, R.styleable.Preference_android_key);
    this.mTitle = TypedArrayUtils.getText(paramContext, R.styleable.Preference_title, R.styleable.Preference_android_title);
    this.mSummary = TypedArrayUtils.getText(paramContext, R.styleable.Preference_summary, R.styleable.Preference_android_summary);
    this.mOrder = TypedArrayUtils.getInt(paramContext, R.styleable.Preference_order, R.styleable.Preference_android_order, Integer.MAX_VALUE);
    this.mFragment = TypedArrayUtils.getString(paramContext, R.styleable.Preference_fragment, R.styleable.Preference_android_fragment);
    this.mLayoutResId = TypedArrayUtils.getResourceId(paramContext, R.styleable.Preference_layout, R.styleable.Preference_android_layout, R.layout.preference);
    this.mWidgetLayoutResId = TypedArrayUtils.getResourceId(paramContext, R.styleable.Preference_widgetLayout, R.styleable.Preference_android_widgetLayout, 0);
    this.mEnabled = TypedArrayUtils.getBoolean(paramContext, R.styleable.Preference_enabled, R.styleable.Preference_android_enabled, true);
    this.mSelectable = TypedArrayUtils.getBoolean(paramContext, R.styleable.Preference_selectable, R.styleable.Preference_android_selectable, true);
    this.mPersistent = TypedArrayUtils.getBoolean(paramContext, R.styleable.Preference_persistent, R.styleable.Preference_android_persistent, true);
    this.mDependencyKey = TypedArrayUtils.getString(paramContext, R.styleable.Preference_dependency, R.styleable.Preference_android_dependency);
    this.mAllowDividerAbove = TypedArrayUtils.getBoolean(paramContext, R.styleable.Preference_allowDividerAbove, R.styleable.Preference_allowDividerAbove, this.mSelectable);
    this.mAllowDividerBelow = TypedArrayUtils.getBoolean(paramContext, R.styleable.Preference_allowDividerBelow, R.styleable.Preference_allowDividerBelow, this.mSelectable);
    if (paramContext.hasValue(R.styleable.Preference_defaultValue)) {
      this.mDefaultValue = onGetDefaultValue(paramContext, R.styleable.Preference_defaultValue);
    }
    for (;;)
    {
      this.mShouldDisableView = TypedArrayUtils.getBoolean(paramContext, R.styleable.Preference_shouldDisableView, R.styleable.Preference_android_shouldDisableView, true);
      this.mHasSingleLineTitleAttr = paramContext.hasValue(R.styleable.Preference_singleLineTitle);
      if (this.mHasSingleLineTitleAttr) {
        this.mSingleLineTitle = TypedArrayUtils.getBoolean(paramContext, R.styleable.Preference_singleLineTitle, R.styleable.Preference_android_singleLineTitle, true);
      }
      this.mIconSpaceReserved = TypedArrayUtils.getBoolean(paramContext, R.styleable.Preference_iconSpaceReserved, R.styleable.Preference_android_iconSpaceReserved, false);
      paramContext.recycle();
      return;
      if (paramContext.hasValue(R.styleable.Preference_android_defaultValue)) {
        this.mDefaultValue = onGetDefaultValue(paramContext, R.styleable.Preference_android_defaultValue);
      }
    }
  }
  
  private void dispatchSetInitialValue()
  {
    if (getPreferenceDataStore() != null) {
      onSetInitialValue(true, this.mDefaultValue);
    }
    do
    {
      return;
      if ((shouldPersist()) && (getSharedPreferences().contains(this.mKey))) {
        break;
      }
    } while (this.mDefaultValue == null);
    onSetInitialValue(false, this.mDefaultValue);
    return;
    onSetInitialValue(true, null);
  }
  
  private void registerDependency()
  {
    if (TextUtils.isEmpty(this.mDependencyKey)) {
      return;
    }
    Preference localPreference = findPreferenceInHierarchy(this.mDependencyKey);
    if (localPreference != null)
    {
      localPreference.registerDependent(this);
      return;
    }
    throw new IllegalStateException("Dependency \"" + this.mDependencyKey + "\" not found for preference \"" + this.mKey + "\" (title: \"" + this.mTitle + "\"");
  }
  
  private void registerDependent(Preference paramPreference)
  {
    if (this.mDependents == null) {
      this.mDependents = new ArrayList();
    }
    this.mDependents.add(paramPreference);
    paramPreference.onDependencyChanged(this, shouldDisableDependents());
  }
  
  private void setEnabledStateOnViews(View paramView, boolean paramBoolean)
  {
    paramView.setEnabled(paramBoolean);
    if ((paramView instanceof ViewGroup))
    {
      paramView = (ViewGroup)paramView;
      int i = paramView.getChildCount() - 1;
      while (i >= 0)
      {
        setEnabledStateOnViews(paramView.getChildAt(i), paramBoolean);
        i -= 1;
      }
    }
  }
  
  private void tryCommit(@NonNull SharedPreferences.Editor paramEditor)
  {
    if (this.mPreferenceManager.shouldCommit()) {
      SharedPreferencesCompat.EditorCompat.getInstance().apply(paramEditor);
    }
  }
  
  private void unregisterDependency()
  {
    if (this.mDependencyKey != null)
    {
      Preference localPreference = findPreferenceInHierarchy(this.mDependencyKey);
      if (localPreference != null) {
        localPreference.unregisterDependent(this);
      }
    }
  }
  
  private void unregisterDependent(Preference paramPreference)
  {
    if (this.mDependents != null) {
      this.mDependents.remove(paramPreference);
    }
  }
  
  void assignParent(@Nullable PreferenceGroup paramPreferenceGroup)
  {
    this.mParentGroup = paramPreferenceGroup;
  }
  
  public boolean callChangeListener(Object paramObject)
  {
    return (this.mOnChangeListener == null) || (this.mOnChangeListener.onPreferenceChange(this, paramObject));
  }
  
  public final void clearWasDetached()
  {
    this.mWasDetached = false;
  }
  
  public int compareTo(@NonNull Preference paramPreference)
  {
    if (this.mOrder != paramPreference.mOrder) {
      return this.mOrder - paramPreference.mOrder;
    }
    if (this.mTitle == paramPreference.mTitle) {
      return 0;
    }
    if (this.mTitle == null) {
      return 1;
    }
    if (paramPreference.mTitle == null) {
      return -1;
    }
    return this.mTitle.toString().compareToIgnoreCase(paramPreference.mTitle.toString());
  }
  
  void dispatchRestoreInstanceState(Bundle paramBundle)
  {
    if (hasKey())
    {
      paramBundle = paramBundle.getParcelable(this.mKey);
      if (paramBundle != null)
      {
        this.mBaseMethodCalled = false;
        onRestoreInstanceState(paramBundle);
        if (!this.mBaseMethodCalled) {
          throw new IllegalStateException("Derived class did not call super.onRestoreInstanceState()");
        }
      }
    }
  }
  
  void dispatchSaveInstanceState(Bundle paramBundle)
  {
    if (hasKey())
    {
      this.mBaseMethodCalled = false;
      Parcelable localParcelable = onSaveInstanceState();
      if (!this.mBaseMethodCalled) {
        throw new IllegalStateException("Derived class did not call super.onSaveInstanceState()");
      }
      if (localParcelable != null) {
        paramBundle.putParcelable(this.mKey, localParcelable);
      }
    }
  }
  
  protected Preference findPreferenceInHierarchy(String paramString)
  {
    if ((TextUtils.isEmpty(paramString)) || (this.mPreferenceManager == null)) {
      return null;
    }
    return this.mPreferenceManager.findPreference(paramString);
  }
  
  public Context getContext()
  {
    return this.mContext;
  }
  
  public String getDependency()
  {
    return this.mDependencyKey;
  }
  
  public Bundle getExtras()
  {
    if (this.mExtras == null) {
      this.mExtras = new Bundle();
    }
    return this.mExtras;
  }
  
  StringBuilder getFilterableStringBuilder()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    CharSequence localCharSequence = getTitle();
    if (!TextUtils.isEmpty(localCharSequence)) {
      localStringBuilder.append(localCharSequence).append(' ');
    }
    localCharSequence = getSummary();
    if (!TextUtils.isEmpty(localCharSequence)) {
      localStringBuilder.append(localCharSequence).append(' ');
    }
    if (localStringBuilder.length() > 0) {
      localStringBuilder.setLength(localStringBuilder.length() - 1);
    }
    return localStringBuilder;
  }
  
  public String getFragment()
  {
    return this.mFragment;
  }
  
  public Drawable getIcon()
  {
    if ((this.mIcon == null) && (this.mIconResId != 0)) {
      this.mIcon = ContextCompat.getDrawable(this.mContext, this.mIconResId);
    }
    return this.mIcon;
  }
  
  long getId()
  {
    return this.mId;
  }
  
  public Intent getIntent()
  {
    return this.mIntent;
  }
  
  public String getKey()
  {
    return this.mKey;
  }
  
  public final int getLayoutResource()
  {
    return this.mLayoutResId;
  }
  
  public OnPreferenceChangeListener getOnPreferenceChangeListener()
  {
    return this.mOnChangeListener;
  }
  
  public OnPreferenceClickListener getOnPreferenceClickListener()
  {
    return this.mOnClickListener;
  }
  
  public int getOrder()
  {
    return this.mOrder;
  }
  
  @Nullable
  public PreferenceGroup getParent()
  {
    return this.mParentGroup;
  }
  
  protected boolean getPersistedBoolean(boolean paramBoolean)
  {
    if (!shouldPersist()) {
      return paramBoolean;
    }
    PreferenceDataStore localPreferenceDataStore = getPreferenceDataStore();
    if (localPreferenceDataStore != null) {
      return localPreferenceDataStore.getBoolean(this.mKey, paramBoolean);
    }
    return this.mPreferenceManager.getSharedPreferences().getBoolean(this.mKey, paramBoolean);
  }
  
  protected float getPersistedFloat(float paramFloat)
  {
    if (!shouldPersist()) {
      return paramFloat;
    }
    PreferenceDataStore localPreferenceDataStore = getPreferenceDataStore();
    if (localPreferenceDataStore != null) {
      return localPreferenceDataStore.getFloat(this.mKey, paramFloat);
    }
    return this.mPreferenceManager.getSharedPreferences().getFloat(this.mKey, paramFloat);
  }
  
  protected int getPersistedInt(int paramInt)
  {
    if (!shouldPersist()) {
      return paramInt;
    }
    PreferenceDataStore localPreferenceDataStore = getPreferenceDataStore();
    if (localPreferenceDataStore != null) {
      return localPreferenceDataStore.getInt(this.mKey, paramInt);
    }
    return this.mPreferenceManager.getSharedPreferences().getInt(this.mKey, paramInt);
  }
  
  protected long getPersistedLong(long paramLong)
  {
    if (!shouldPersist()) {
      return paramLong;
    }
    PreferenceDataStore localPreferenceDataStore = getPreferenceDataStore();
    if (localPreferenceDataStore != null) {
      return localPreferenceDataStore.getLong(this.mKey, paramLong);
    }
    return this.mPreferenceManager.getSharedPreferences().getLong(this.mKey, paramLong);
  }
  
  protected String getPersistedString(String paramString)
  {
    if (!shouldPersist()) {
      return paramString;
    }
    PreferenceDataStore localPreferenceDataStore = getPreferenceDataStore();
    if (localPreferenceDataStore != null) {
      return localPreferenceDataStore.getString(this.mKey, paramString);
    }
    return this.mPreferenceManager.getSharedPreferences().getString(this.mKey, paramString);
  }
  
  public Set<String> getPersistedStringSet(Set<String> paramSet)
  {
    if (!shouldPersist()) {
      return paramSet;
    }
    PreferenceDataStore localPreferenceDataStore = getPreferenceDataStore();
    if (localPreferenceDataStore != null) {
      return localPreferenceDataStore.getStringSet(this.mKey, paramSet);
    }
    return this.mPreferenceManager.getSharedPreferences().getStringSet(this.mKey, paramSet);
  }
  
  @Nullable
  public PreferenceDataStore getPreferenceDataStore()
  {
    if (this.mPreferenceDataStore != null) {
      return this.mPreferenceDataStore;
    }
    if (this.mPreferenceManager != null) {
      return this.mPreferenceManager.getPreferenceDataStore();
    }
    return null;
  }
  
  public PreferenceManager getPreferenceManager()
  {
    return this.mPreferenceManager;
  }
  
  public SharedPreferences getSharedPreferences()
  {
    if ((this.mPreferenceManager == null) || (getPreferenceDataStore() != null)) {
      return null;
    }
    return this.mPreferenceManager.getSharedPreferences();
  }
  
  public boolean getShouldDisableView()
  {
    return this.mShouldDisableView;
  }
  
  public CharSequence getSummary()
  {
    return this.mSummary;
  }
  
  public CharSequence getTitle()
  {
    return this.mTitle;
  }
  
  public final int getWidgetLayoutResource()
  {
    return this.mWidgetLayoutResId;
  }
  
  public boolean hasKey()
  {
    return !TextUtils.isEmpty(this.mKey);
  }
  
  public boolean isEnabled()
  {
    return (this.mEnabled) && (this.mDependencyMet) && (this.mParentDependencyMet);
  }
  
  public boolean isIconSpaceReserved()
  {
    return this.mIconSpaceReserved;
  }
  
  public boolean isPersistent()
  {
    return this.mPersistent;
  }
  
  public boolean isSelectable()
  {
    return this.mSelectable;
  }
  
  public boolean isSingleLineTitle()
  {
    return this.mSingleLineTitle;
  }
  
  public final boolean isVisible()
  {
    return this.mVisible;
  }
  
  protected void notifyChanged()
  {
    if (this.mListener != null) {
      this.mListener.onPreferenceChange(this);
    }
  }
  
  public void notifyDependencyChange(boolean paramBoolean)
  {
    List localList = this.mDependents;
    if (localList == null) {}
    for (;;)
    {
      return;
      int j = localList.size();
      int i = 0;
      while (i < j)
      {
        ((Preference)localList.get(i)).onDependencyChanged(this, paramBoolean);
        i += 1;
      }
    }
  }
  
  protected void notifyHierarchyChanged()
  {
    if (this.mListener != null) {
      this.mListener.onPreferenceHierarchyChange(this);
    }
  }
  
  public void onAttached()
  {
    registerDependency();
  }
  
  protected void onAttachedToHierarchy(PreferenceManager paramPreferenceManager)
  {
    this.mPreferenceManager = paramPreferenceManager;
    if (!this.mHasId) {
      this.mId = paramPreferenceManager.getNextId();
    }
    dispatchSetInitialValue();
  }
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  protected void onAttachedToHierarchy(PreferenceManager paramPreferenceManager, long paramLong)
  {
    this.mId = paramLong;
    this.mHasId = true;
    try
    {
      onAttachedToHierarchy(paramPreferenceManager);
      return;
    }
    finally
    {
      this.mHasId = false;
    }
  }
  
  public void onBindViewHolder(PreferenceViewHolder paramPreferenceViewHolder)
  {
    int j = 4;
    paramPreferenceViewHolder.itemView.setOnClickListener(this.mClickListener);
    paramPreferenceViewHolder.itemView.setId(this.mViewId);
    Object localObject1 = (TextView)paramPreferenceViewHolder.findViewById(16908310);
    Object localObject2;
    if (localObject1 != null)
    {
      localObject2 = getTitle();
      if (TextUtils.isEmpty((CharSequence)localObject2)) {
        break label315;
      }
      ((TextView)localObject1).setText((CharSequence)localObject2);
      ((TextView)localObject1).setVisibility(0);
      if (this.mHasSingleLineTitleAttr) {
        ((TextView)localObject1).setSingleLine(this.mSingleLineTitle);
      }
    }
    localObject1 = (TextView)paramPreferenceViewHolder.findViewById(16908304);
    if (localObject1 != null)
    {
      localObject2 = getSummary();
      if (!TextUtils.isEmpty((CharSequence)localObject2))
      {
        ((TextView)localObject1).setText((CharSequence)localObject2);
        ((TextView)localObject1).setVisibility(0);
      }
    }
    else
    {
      label128:
      localObject1 = (ImageView)paramPreferenceViewHolder.findViewById(16908294);
      if (localObject1 != null)
      {
        if ((this.mIconResId != 0) || (this.mIcon != null))
        {
          if (this.mIcon == null) {
            this.mIcon = ContextCompat.getDrawable(getContext(), this.mIconResId);
          }
          if (this.mIcon != null) {
            ((ImageView)localObject1).setImageDrawable(this.mIcon);
          }
        }
        if (this.mIcon == null) {
          break label335;
        }
        ((ImageView)localObject1).setVisibility(0);
      }
      localObject2 = paramPreferenceViewHolder.findViewById(R.id.icon_frame);
      localObject1 = localObject2;
      if (localObject2 == null) {
        localObject1 = paramPreferenceViewHolder.findViewById(16908350);
      }
      if (localObject1 != null)
      {
        if (this.mIcon == null) {
          break label359;
        }
        ((View)localObject1).setVisibility(0);
      }
      if (!this.mShouldDisableView) {
        break label383;
      }
      setEnabledStateOnViews(paramPreferenceViewHolder.itemView, isEnabled());
    }
    for (;;)
    {
      boolean bool = isSelectable();
      paramPreferenceViewHolder.itemView.setFocusable(bool);
      paramPreferenceViewHolder.itemView.setClickable(bool);
      paramPreferenceViewHolder.setDividerAllowedAbove(this.mAllowDividerAbove);
      paramPreferenceViewHolder.setDividerAllowedBelow(this.mAllowDividerBelow);
      return;
      label315:
      ((TextView)localObject1).setVisibility(8);
      break;
      ((TextView)localObject1).setVisibility(8);
      break label128;
      label335:
      if (this.mIconSpaceReserved) {}
      for (int i = 4;; i = 8)
      {
        ((ImageView)localObject1).setVisibility(i);
        break;
      }
      label359:
      if (this.mIconSpaceReserved) {}
      for (i = j;; i = 8)
      {
        ((View)localObject1).setVisibility(i);
        break;
      }
      label383:
      setEnabledStateOnViews(paramPreferenceViewHolder.itemView, true);
    }
  }
  
  protected void onClick() {}
  
  public void onDependencyChanged(Preference paramPreference, boolean paramBoolean)
  {
    if (this.mDependencyMet == paramBoolean) {
      if (paramBoolean) {
        break label32;
      }
    }
    label32:
    for (paramBoolean = true;; paramBoolean = false)
    {
      this.mDependencyMet = paramBoolean;
      notifyDependencyChange(shouldDisableDependents());
      notifyChanged();
      return;
    }
  }
  
  public void onDetached()
  {
    unregisterDependency();
    this.mWasDetached = true;
  }
  
  protected Object onGetDefaultValue(TypedArray paramTypedArray, int paramInt)
  {
    return null;
  }
  
  @CallSuper
  public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfoCompat paramAccessibilityNodeInfoCompat) {}
  
  public void onParentChanged(Preference paramPreference, boolean paramBoolean)
  {
    if (this.mParentDependencyMet == paramBoolean) {
      if (paramBoolean) {
        break label32;
      }
    }
    label32:
    for (paramBoolean = true;; paramBoolean = false)
    {
      this.mParentDependencyMet = paramBoolean;
      notifyDependencyChange(shouldDisableDependents());
      notifyChanged();
      return;
    }
  }
  
  protected void onPrepareForRemoval()
  {
    unregisterDependency();
  }
  
  protected void onRestoreInstanceState(Parcelable paramParcelable)
  {
    this.mBaseMethodCalled = true;
    if ((paramParcelable != BaseSavedState.EMPTY_STATE) && (paramParcelable != null)) {
      throw new IllegalArgumentException("Wrong state class -- expecting Preference State");
    }
  }
  
  protected Parcelable onSaveInstanceState()
  {
    this.mBaseMethodCalled = true;
    return BaseSavedState.EMPTY_STATE;
  }
  
  protected void onSetInitialValue(boolean paramBoolean, Object paramObject) {}
  
  public Bundle peekExtras()
  {
    return this.mExtras;
  }
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  public void performClick()
  {
    if (!isEnabled()) {}
    do
    {
      Object localObject;
      do
      {
        do
        {
          return;
          onClick();
        } while ((this.mOnClickListener != null) && (this.mOnClickListener.onPreferenceClick(this)));
        localObject = getPreferenceManager();
        if (localObject == null) {
          break;
        }
        localObject = ((PreferenceManager)localObject).getOnPreferenceTreeClickListener();
      } while ((localObject != null) && (((PreferenceManager.OnPreferenceTreeClickListener)localObject).onPreferenceTreeClick(this)));
    } while (this.mIntent == null);
    getContext().startActivity(this.mIntent);
  }
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  protected void performClick(View paramView)
  {
    performClick();
  }
  
  protected boolean persistBoolean(boolean paramBoolean)
  {
    boolean bool1 = false;
    boolean bool2 = true;
    if (!shouldPersist()) {
      bool2 = false;
    }
    do
    {
      return bool2;
      if (!paramBoolean) {
        bool1 = true;
      }
    } while (paramBoolean == getPersistedBoolean(bool1));
    Object localObject = getPreferenceDataStore();
    if (localObject != null)
    {
      ((PreferenceDataStore)localObject).putBoolean(this.mKey, paramBoolean);
      return true;
    }
    localObject = this.mPreferenceManager.getEditor();
    ((SharedPreferences.Editor)localObject).putBoolean(this.mKey, paramBoolean);
    tryCommit((SharedPreferences.Editor)localObject);
    return true;
  }
  
  protected boolean persistFloat(float paramFloat)
  {
    boolean bool = true;
    if (!shouldPersist()) {
      bool = false;
    }
    while (paramFloat == getPersistedFloat(NaN.0F)) {
      return bool;
    }
    Object localObject = getPreferenceDataStore();
    if (localObject != null)
    {
      ((PreferenceDataStore)localObject).putFloat(this.mKey, paramFloat);
      return true;
    }
    localObject = this.mPreferenceManager.getEditor();
    ((SharedPreferences.Editor)localObject).putFloat(this.mKey, paramFloat);
    tryCommit((SharedPreferences.Editor)localObject);
    return true;
  }
  
  protected boolean persistInt(int paramInt)
  {
    boolean bool = true;
    if (!shouldPersist()) {
      bool = false;
    }
    while (paramInt == getPersistedInt(paramInt ^ 0xFFFFFFFF)) {
      return bool;
    }
    Object localObject = getPreferenceDataStore();
    if (localObject != null)
    {
      ((PreferenceDataStore)localObject).putInt(this.mKey, paramInt);
      return true;
    }
    localObject = this.mPreferenceManager.getEditor();
    ((SharedPreferences.Editor)localObject).putInt(this.mKey, paramInt);
    tryCommit((SharedPreferences.Editor)localObject);
    return true;
  }
  
  protected boolean persistLong(long paramLong)
  {
    boolean bool = true;
    if (!shouldPersist()) {
      bool = false;
    }
    while (paramLong == getPersistedLong(0xFFFFFFFFFFFFFFFF ^ paramLong)) {
      return bool;
    }
    Object localObject = getPreferenceDataStore();
    if (localObject != null)
    {
      ((PreferenceDataStore)localObject).putLong(this.mKey, paramLong);
      return true;
    }
    localObject = this.mPreferenceManager.getEditor();
    ((SharedPreferences.Editor)localObject).putLong(this.mKey, paramLong);
    tryCommit((SharedPreferences.Editor)localObject);
    return true;
  }
  
  protected boolean persistString(String paramString)
  {
    boolean bool = true;
    if (!shouldPersist()) {
      bool = false;
    }
    while (TextUtils.equals(paramString, getPersistedString(null))) {
      return bool;
    }
    Object localObject = getPreferenceDataStore();
    if (localObject != null)
    {
      ((PreferenceDataStore)localObject).putString(this.mKey, paramString);
      return true;
    }
    localObject = this.mPreferenceManager.getEditor();
    ((SharedPreferences.Editor)localObject).putString(this.mKey, paramString);
    tryCommit((SharedPreferences.Editor)localObject);
    return true;
  }
  
  public boolean persistStringSet(Set<String> paramSet)
  {
    boolean bool = true;
    if (!shouldPersist()) {
      bool = false;
    }
    while (paramSet.equals(getPersistedStringSet(null))) {
      return bool;
    }
    Object localObject = getPreferenceDataStore();
    if (localObject != null)
    {
      ((PreferenceDataStore)localObject).putStringSet(this.mKey, paramSet);
      return true;
    }
    localObject = this.mPreferenceManager.getEditor();
    ((SharedPreferences.Editor)localObject).putStringSet(this.mKey, paramSet);
    tryCommit((SharedPreferences.Editor)localObject);
    return true;
  }
  
  void requireKey()
  {
    if (TextUtils.isEmpty(this.mKey)) {
      throw new IllegalStateException("Preference does not have a key assigned.");
    }
    this.mRequiresKey = true;
  }
  
  public void restoreHierarchyState(Bundle paramBundle)
  {
    dispatchRestoreInstanceState(paramBundle);
  }
  
  public void saveHierarchyState(Bundle paramBundle)
  {
    dispatchSaveInstanceState(paramBundle);
  }
  
  public void setDefaultValue(Object paramObject)
  {
    this.mDefaultValue = paramObject;
  }
  
  public void setDependency(String paramString)
  {
    unregisterDependency();
    this.mDependencyKey = paramString;
    registerDependency();
  }
  
  public void setEnabled(boolean paramBoolean)
  {
    if (this.mEnabled != paramBoolean)
    {
      this.mEnabled = paramBoolean;
      notifyDependencyChange(shouldDisableDependents());
      notifyChanged();
    }
  }
  
  public void setFragment(String paramString)
  {
    this.mFragment = paramString;
  }
  
  public void setIcon(int paramInt)
  {
    setIcon(ContextCompat.getDrawable(this.mContext, paramInt));
    this.mIconResId = paramInt;
  }
  
  public void setIcon(Drawable paramDrawable)
  {
    if (((paramDrawable == null) && (this.mIcon != null)) || ((paramDrawable != null) && (this.mIcon != paramDrawable)))
    {
      this.mIcon = paramDrawable;
      this.mIconResId = 0;
      notifyChanged();
    }
  }
  
  public void setIconSpaceReserved(boolean paramBoolean)
  {
    this.mIconSpaceReserved = paramBoolean;
    notifyChanged();
  }
  
  public void setIntent(Intent paramIntent)
  {
    this.mIntent = paramIntent;
  }
  
  public void setKey(String paramString)
  {
    this.mKey = paramString;
    if ((this.mRequiresKey) && (!hasKey())) {
      requireKey();
    }
  }
  
  public void setLayoutResource(int paramInt)
  {
    this.mLayoutResId = paramInt;
  }
  
  final void setOnPreferenceChangeInternalListener(OnPreferenceChangeInternalListener paramOnPreferenceChangeInternalListener)
  {
    this.mListener = paramOnPreferenceChangeInternalListener;
  }
  
  public void setOnPreferenceChangeListener(OnPreferenceChangeListener paramOnPreferenceChangeListener)
  {
    this.mOnChangeListener = paramOnPreferenceChangeListener;
  }
  
  public void setOnPreferenceClickListener(OnPreferenceClickListener paramOnPreferenceClickListener)
  {
    this.mOnClickListener = paramOnPreferenceClickListener;
  }
  
  public void setOrder(int paramInt)
  {
    if (paramInt != this.mOrder)
    {
      this.mOrder = paramInt;
      notifyHierarchyChanged();
    }
  }
  
  public void setPersistent(boolean paramBoolean)
  {
    this.mPersistent = paramBoolean;
  }
  
  public void setPreferenceDataStore(PreferenceDataStore paramPreferenceDataStore)
  {
    this.mPreferenceDataStore = paramPreferenceDataStore;
  }
  
  public void setSelectable(boolean paramBoolean)
  {
    if (this.mSelectable != paramBoolean)
    {
      this.mSelectable = paramBoolean;
      notifyChanged();
    }
  }
  
  public void setShouldDisableView(boolean paramBoolean)
  {
    this.mShouldDisableView = paramBoolean;
    notifyChanged();
  }
  
  public void setSingleLineTitle(boolean paramBoolean)
  {
    this.mSingleLineTitle = paramBoolean;
  }
  
  public void setSummary(int paramInt)
  {
    setSummary(this.mContext.getString(paramInt));
  }
  
  public void setSummary(CharSequence paramCharSequence)
  {
    if (((paramCharSequence == null) && (this.mSummary != null)) || ((paramCharSequence != null) && (!paramCharSequence.equals(this.mSummary))))
    {
      this.mSummary = paramCharSequence;
      notifyChanged();
    }
  }
  
  public void setTitle(int paramInt)
  {
    setTitle(this.mContext.getString(paramInt));
  }
  
  public void setTitle(CharSequence paramCharSequence)
  {
    if (((paramCharSequence == null) && (this.mTitle != null)) || ((paramCharSequence != null) && (!paramCharSequence.equals(this.mTitle))))
    {
      this.mTitle = paramCharSequence;
      notifyChanged();
    }
  }
  
  public void setViewId(int paramInt)
  {
    this.mViewId = paramInt;
  }
  
  public final void setVisible(boolean paramBoolean)
  {
    if (this.mVisible != paramBoolean)
    {
      this.mVisible = paramBoolean;
      if (this.mListener != null) {
        this.mListener.onPreferenceVisibilityChange(this);
      }
    }
  }
  
  public void setWidgetLayoutResource(int paramInt)
  {
    this.mWidgetLayoutResId = paramInt;
  }
  
  public boolean shouldDisableDependents()
  {
    return !isEnabled();
  }
  
  protected boolean shouldPersist()
  {
    return (this.mPreferenceManager != null) && (isPersistent()) && (hasKey());
  }
  
  public String toString()
  {
    return getFilterableStringBuilder().toString();
  }
  
  public final boolean wasDetached()
  {
    return this.mWasDetached;
  }
  
  public static class BaseSavedState
    extends AbsSavedState
  {
    public static final Parcelable.Creator<BaseSavedState> CREATOR = new Parcelable.Creator()
    {
      public Preference.BaseSavedState createFromParcel(Parcel paramAnonymousParcel)
      {
        return new Preference.BaseSavedState(paramAnonymousParcel);
      }
      
      public Preference.BaseSavedState[] newArray(int paramAnonymousInt)
      {
        return new Preference.BaseSavedState[paramAnonymousInt];
      }
    };
    
    public BaseSavedState(Parcel paramParcel)
    {
      super();
    }
    
    public BaseSavedState(Parcelable paramParcelable)
    {
      super();
    }
  }
  
  static abstract interface OnPreferenceChangeInternalListener
  {
    public abstract void onPreferenceChange(Preference paramPreference);
    
    public abstract void onPreferenceHierarchyChange(Preference paramPreference);
    
    public abstract void onPreferenceVisibilityChange(Preference paramPreference);
  }
  
  public static abstract interface OnPreferenceChangeListener
  {
    public abstract boolean onPreferenceChange(Preference paramPreference, Object paramObject);
  }
  
  public static abstract interface OnPreferenceClickListener
  {
    public abstract boolean onPreferenceClick(Preference paramPreference);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v7/preference/Preference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */