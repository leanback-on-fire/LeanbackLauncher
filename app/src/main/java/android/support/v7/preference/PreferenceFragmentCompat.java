package android.support.v7.preference;

import android.content.Context;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.annotation.XmlRes;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.preference.internal.AbstractMultiSelectListPreference;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.AdapterDataObserver;
import android.support.v7.widget.RecyclerView.ItemDecoration;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.support.v7.widget.RecyclerView.State;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class PreferenceFragmentCompat
  extends Fragment
  implements PreferenceManager.OnPreferenceTreeClickListener, PreferenceManager.OnDisplayPreferenceDialogListener, PreferenceManager.OnNavigateToScreenListener, DialogPreference.TargetFragment
{
  public static final String ARG_PREFERENCE_ROOT = "android.support.v7.preference.PreferenceFragmentCompat.PREFERENCE_ROOT";
  private static final String DIALOG_FRAGMENT_TAG = "android.support.v7.preference.PreferenceFragment.DIALOG";
  private static final int MSG_BIND_PREFERENCES = 1;
  private static final String PREFERENCES_TAG = "android:preferences";
  private final DividerDecoration mDividerDecoration = new DividerDecoration(null);
  private Handler mHandler = new Handler()
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      switch (paramAnonymousMessage.what)
      {
      default: 
        return;
      }
      PreferenceFragmentCompat.this.bindPreferences();
    }
  };
  private boolean mHavePrefs;
  private boolean mInitDone;
  private int mLayoutResId = R.layout.preference_list_fragment;
  private RecyclerView mList;
  private PreferenceManager mPreferenceManager;
  private final Runnable mRequestFocus = new Runnable()
  {
    public void run()
    {
      PreferenceFragmentCompat.this.mList.focusableViewAvailable(PreferenceFragmentCompat.this.mList);
    }
  };
  private Runnable mSelectPreferenceRunnable;
  private Context mStyledContext;
  
  private void bindPreferences()
  {
    PreferenceScreen localPreferenceScreen = getPreferenceScreen();
    if (localPreferenceScreen != null)
    {
      getListView().setAdapter(onCreateAdapter(localPreferenceScreen));
      localPreferenceScreen.onAttached();
    }
    onBindPreferences();
  }
  
  private void postBindPreferences()
  {
    if (this.mHandler.hasMessages(1)) {
      return;
    }
    this.mHandler.obtainMessage(1).sendToTarget();
  }
  
  private void requirePreferenceManager()
  {
    if (this.mPreferenceManager == null) {
      throw new RuntimeException("This should be called after super.onCreate.");
    }
  }
  
  private void scrollToPreferenceInternal(final Preference paramPreference, final String paramString)
  {
    paramPreference = new Runnable()
    {
      public void run()
      {
        RecyclerView.Adapter localAdapter = PreferenceFragmentCompat.this.mList.getAdapter();
        if (!(localAdapter instanceof PreferenceGroup.PreferencePositionCallback))
        {
          if (localAdapter != null) {
            throw new IllegalStateException("Adapter must implement PreferencePositionCallback");
          }
        }
        else {
          if (paramPreference == null) {
            break label70;
          }
        }
        label70:
        for (int i = ((PreferenceGroup.PreferencePositionCallback)localAdapter).getPreferenceAdapterPosition(paramPreference); i != -1; i = ((PreferenceGroup.PreferencePositionCallback)localAdapter).getPreferenceAdapterPosition(paramString))
        {
          PreferenceFragmentCompat.this.mList.scrollToPosition(i);
          return;
        }
        localAdapter.registerAdapterDataObserver(new PreferenceFragmentCompat.ScrollToPreferenceObserver(localAdapter, PreferenceFragmentCompat.this.mList, paramPreference, paramString));
      }
    };
    if (this.mList == null)
    {
      this.mSelectPreferenceRunnable = paramPreference;
      return;
    }
    paramPreference.run();
  }
  
  private void unbindPreferences()
  {
    PreferenceScreen localPreferenceScreen = getPreferenceScreen();
    if (localPreferenceScreen != null) {
      localPreferenceScreen.onDetached();
    }
    onUnbindPreferences();
  }
  
  public void addPreferencesFromResource(@XmlRes int paramInt)
  {
    requirePreferenceManager();
    setPreferenceScreen(this.mPreferenceManager.inflateFromResource(this.mStyledContext, paramInt, getPreferenceScreen()));
  }
  
  public Preference findPreference(CharSequence paramCharSequence)
  {
    if (this.mPreferenceManager == null) {
      return null;
    }
    return this.mPreferenceManager.findPreference(paramCharSequence);
  }
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  public Fragment getCallbackFragment()
  {
    return null;
  }
  
  public final RecyclerView getListView()
  {
    return this.mList;
  }
  
  public PreferenceManager getPreferenceManager()
  {
    return this.mPreferenceManager;
  }
  
  public PreferenceScreen getPreferenceScreen()
  {
    return this.mPreferenceManager.getPreferenceScreen();
  }
  
  public void onActivityCreated(Bundle paramBundle)
  {
    super.onActivityCreated(paramBundle);
    if (paramBundle != null)
    {
      paramBundle = paramBundle.getBundle("android:preferences");
      if (paramBundle != null)
      {
        PreferenceScreen localPreferenceScreen = getPreferenceScreen();
        if (localPreferenceScreen != null) {
          localPreferenceScreen.restoreHierarchyState(paramBundle);
        }
      }
    }
  }
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  protected void onBindPreferences() {}
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    Object localObject = new TypedValue();
    getActivity().getTheme().resolveAttribute(R.attr.preferenceTheme, (TypedValue)localObject, true);
    int i = ((TypedValue)localObject).resourceId;
    if (i == 0) {
      throw new IllegalStateException("Must specify preferenceTheme in theme");
    }
    this.mStyledContext = new ContextThemeWrapper(getActivity(), i);
    this.mPreferenceManager = new PreferenceManager(this.mStyledContext);
    this.mPreferenceManager.setOnNavigateToScreenListener(this);
    if (getArguments() != null) {}
    for (localObject = getArguments().getString("android.support.v7.preference.PreferenceFragmentCompat.PREFERENCE_ROOT");; localObject = null)
    {
      onCreatePreferences(paramBundle, (String)localObject);
      return;
    }
  }
  
  protected RecyclerView.Adapter onCreateAdapter(PreferenceScreen paramPreferenceScreen)
  {
    return new PreferenceGroupAdapter(paramPreferenceScreen);
  }
  
  public RecyclerView.LayoutManager onCreateLayoutManager()
  {
    return new LinearLayoutManager(getActivity());
  }
  
  public abstract void onCreatePreferences(Bundle paramBundle, String paramString);
  
  public RecyclerView onCreateRecyclerView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    paramLayoutInflater = (RecyclerView)paramLayoutInflater.inflate(R.layout.preference_recyclerview, paramViewGroup, false);
    paramLayoutInflater.setLayoutManager(onCreateLayoutManager());
    paramLayoutInflater.setAccessibilityDelegateCompat(new PreferenceRecyclerViewAccessibilityDelegate(paramLayoutInflater));
    return paramLayoutInflater;
  }
  
  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    Object localObject = this.mStyledContext.obtainStyledAttributes(null, R.styleable.PreferenceFragmentCompat, R.attr.preferenceFragmentCompatStyle, 0);
    this.mLayoutResId = ((TypedArray)localObject).getResourceId(R.styleable.PreferenceFragmentCompat_android_layout, this.mLayoutResId);
    Drawable localDrawable = ((TypedArray)localObject).getDrawable(R.styleable.PreferenceFragmentCompat_android_divider);
    int i = ((TypedArray)localObject).getDimensionPixelSize(R.styleable.PreferenceFragmentCompat_android_dividerHeight, -1);
    boolean bool = ((TypedArray)localObject).getBoolean(R.styleable.PreferenceFragmentCompat_allowDividerAfterLastItem, true);
    ((TypedArray)localObject).recycle();
    localObject = new TypedValue();
    getActivity().getTheme().resolveAttribute(R.attr.preferenceTheme, (TypedValue)localObject, true);
    int j = ((TypedValue)localObject).resourceId;
    paramLayoutInflater = paramLayoutInflater.cloneInContext(new ContextThemeWrapper(paramLayoutInflater.getContext(), j));
    paramViewGroup = paramLayoutInflater.inflate(this.mLayoutResId, paramViewGroup, false);
    localObject = paramViewGroup.findViewById(16908351);
    if (!(localObject instanceof ViewGroup)) {
      throw new RuntimeException("Content has view with id attribute 'android.R.id.list_container' that is not a ViewGroup class");
    }
    localObject = (ViewGroup)localObject;
    paramLayoutInflater = onCreateRecyclerView(paramLayoutInflater, (ViewGroup)localObject, paramBundle);
    if (paramLayoutInflater == null) {
      throw new RuntimeException("Could not create RecyclerView");
    }
    this.mList = paramLayoutInflater;
    paramLayoutInflater.addItemDecoration(this.mDividerDecoration);
    setDivider(localDrawable);
    if (i != -1) {
      setDividerHeight(i);
    }
    this.mDividerDecoration.setAllowDividerAfterLastItem(bool);
    ((ViewGroup)localObject).addView(this.mList);
    this.mHandler.post(this.mRequestFocus);
    return paramViewGroup;
  }
  
  public void onDestroyView()
  {
    this.mHandler.removeCallbacks(this.mRequestFocus);
    this.mHandler.removeMessages(1);
    if (this.mHavePrefs) {
      unbindPreferences();
    }
    this.mList = null;
    super.onDestroyView();
  }
  
  public void onDisplayPreferenceDialog(Preference paramPreference)
  {
    boolean bool1 = false;
    if ((getCallbackFragment() instanceof OnPreferenceDisplayDialogCallback)) {
      bool1 = ((OnPreferenceDisplayDialogCallback)getCallbackFragment()).onPreferenceDisplayDialog(this, paramPreference);
    }
    boolean bool2 = bool1;
    if (!bool1)
    {
      bool2 = bool1;
      if ((getActivity() instanceof OnPreferenceDisplayDialogCallback)) {
        bool2 = ((OnPreferenceDisplayDialogCallback)getActivity()).onPreferenceDisplayDialog(this, paramPreference);
      }
    }
    if (bool2) {}
    while (getFragmentManager().findFragmentByTag("android.support.v7.preference.PreferenceFragment.DIALOG") != null) {
      return;
    }
    if ((paramPreference instanceof EditTextPreference)) {
      paramPreference = EditTextPreferenceDialogFragmentCompat.newInstance(paramPreference.getKey());
    }
    for (;;)
    {
      paramPreference.setTargetFragment(this, 0);
      paramPreference.show(getFragmentManager(), "android.support.v7.preference.PreferenceFragment.DIALOG");
      return;
      if ((paramPreference instanceof ListPreference))
      {
        paramPreference = ListPreferenceDialogFragmentCompat.newInstance(paramPreference.getKey());
      }
      else
      {
        if (!(paramPreference instanceof AbstractMultiSelectListPreference)) {
          break;
        }
        paramPreference = MultiSelectListPreferenceDialogFragmentCompat.newInstance(paramPreference.getKey());
      }
    }
    throw new IllegalArgumentException("Tried to display dialog for unknown preference type. Did you forget to override onDisplayPreferenceDialog()?");
  }
  
  public void onNavigateToScreen(PreferenceScreen paramPreferenceScreen)
  {
    boolean bool = false;
    if ((getCallbackFragment() instanceof OnPreferenceStartScreenCallback)) {
      bool = ((OnPreferenceStartScreenCallback)getCallbackFragment()).onPreferenceStartScreen(this, paramPreferenceScreen);
    }
    if ((!bool) && ((getActivity() instanceof OnPreferenceStartScreenCallback))) {
      ((OnPreferenceStartScreenCallback)getActivity()).onPreferenceStartScreen(this, paramPreferenceScreen);
    }
  }
  
  public boolean onPreferenceTreeClick(Preference paramPreference)
  {
    if (paramPreference.getFragment() != null)
    {
      boolean bool1 = false;
      if ((getCallbackFragment() instanceof OnPreferenceStartFragmentCallback)) {
        bool1 = ((OnPreferenceStartFragmentCallback)getCallbackFragment()).onPreferenceStartFragment(this, paramPreference);
      }
      boolean bool2 = bool1;
      if (!bool1)
      {
        bool2 = bool1;
        if ((getActivity() instanceof OnPreferenceStartFragmentCallback)) {
          bool2 = ((OnPreferenceStartFragmentCallback)getActivity()).onPreferenceStartFragment(this, paramPreference);
        }
      }
      return bool2;
    }
    return false;
  }
  
  public void onSaveInstanceState(Bundle paramBundle)
  {
    super.onSaveInstanceState(paramBundle);
    PreferenceScreen localPreferenceScreen = getPreferenceScreen();
    if (localPreferenceScreen != null)
    {
      Bundle localBundle = new Bundle();
      localPreferenceScreen.saveHierarchyState(localBundle);
      paramBundle.putBundle("android:preferences", localBundle);
    }
  }
  
  public void onStart()
  {
    super.onStart();
    this.mPreferenceManager.setOnPreferenceTreeClickListener(this);
    this.mPreferenceManager.setOnDisplayPreferenceDialogListener(this);
  }
  
  public void onStop()
  {
    super.onStop();
    this.mPreferenceManager.setOnPreferenceTreeClickListener(null);
    this.mPreferenceManager.setOnDisplayPreferenceDialogListener(null);
  }
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  protected void onUnbindPreferences() {}
  
  public void onViewCreated(View paramView, @Nullable Bundle paramBundle)
  {
    super.onViewCreated(paramView, paramBundle);
    if (this.mHavePrefs)
    {
      bindPreferences();
      if (this.mSelectPreferenceRunnable != null)
      {
        this.mSelectPreferenceRunnable.run();
        this.mSelectPreferenceRunnable = null;
      }
    }
    this.mInitDone = true;
  }
  
  public void scrollToPreference(Preference paramPreference)
  {
    scrollToPreferenceInternal(paramPreference, null);
  }
  
  public void scrollToPreference(String paramString)
  {
    scrollToPreferenceInternal(null, paramString);
  }
  
  public void setDivider(Drawable paramDrawable)
  {
    this.mDividerDecoration.setDivider(paramDrawable);
  }
  
  public void setDividerHeight(int paramInt)
  {
    this.mDividerDecoration.setDividerHeight(paramInt);
  }
  
  public void setPreferenceScreen(PreferenceScreen paramPreferenceScreen)
  {
    if ((this.mPreferenceManager.setPreferences(paramPreferenceScreen)) && (paramPreferenceScreen != null))
    {
      onUnbindPreferences();
      this.mHavePrefs = true;
      if (this.mInitDone) {
        postBindPreferences();
      }
    }
  }
  
  public void setPreferencesFromResource(@XmlRes int paramInt, @Nullable String paramString)
  {
    requirePreferenceManager();
    Object localObject = this.mPreferenceManager.inflateFromResource(this.mStyledContext, paramInt, null);
    if (paramString != null)
    {
      Preference localPreference = ((PreferenceScreen)localObject).findPreference(paramString);
      localObject = localPreference;
      if (!(localPreference instanceof PreferenceScreen)) {
        throw new IllegalArgumentException("Preference object with key " + paramString + " is not a PreferenceScreen");
      }
    }
    setPreferenceScreen((PreferenceScreen)localObject);
  }
  
  private class DividerDecoration
    extends RecyclerView.ItemDecoration
  {
    private boolean mAllowDividerAfterLastItem = true;
    private Drawable mDivider;
    private int mDividerHeight;
    
    private DividerDecoration() {}
    
    private boolean shouldDrawDividerBelow(View paramView, RecyclerView paramRecyclerView)
    {
      RecyclerView.ViewHolder localViewHolder = paramRecyclerView.getChildViewHolder(paramView);
      if (((localViewHolder instanceof PreferenceViewHolder)) && (((PreferenceViewHolder)localViewHolder).isDividerAllowedBelow())) {}
      for (int i = 1; i == 0; i = 0) {
        return false;
      }
      boolean bool = this.mAllowDividerAfterLastItem;
      i = paramRecyclerView.indexOfChild(paramView);
      if (i < paramRecyclerView.getChildCount() - 1)
      {
        paramView = paramRecyclerView.getChildViewHolder(paramRecyclerView.getChildAt(i + 1));
        if ((!(paramView instanceof PreferenceViewHolder)) || (!((PreferenceViewHolder)paramView).isDividerAllowedAbove())) {
          break label96;
        }
      }
      label96:
      for (bool = true;; bool = false) {
        return bool;
      }
    }
    
    public void getItemOffsets(Rect paramRect, View paramView, RecyclerView paramRecyclerView, RecyclerView.State paramState)
    {
      if (shouldDrawDividerBelow(paramView, paramRecyclerView)) {
        paramRect.bottom = this.mDividerHeight;
      }
    }
    
    public void onDrawOver(Canvas paramCanvas, RecyclerView paramRecyclerView, RecyclerView.State paramState)
    {
      if (this.mDivider == null) {}
      for (;;)
      {
        return;
        int j = paramRecyclerView.getChildCount();
        int k = paramRecyclerView.getWidth();
        int i = 0;
        while (i < j)
        {
          paramState = paramRecyclerView.getChildAt(i);
          if (shouldDrawDividerBelow(paramState, paramRecyclerView))
          {
            int m = (int)paramState.getY() + paramState.getHeight();
            this.mDivider.setBounds(0, m, k, this.mDividerHeight + m);
            this.mDivider.draw(paramCanvas);
          }
          i += 1;
        }
      }
    }
    
    public void setAllowDividerAfterLastItem(boolean paramBoolean)
    {
      this.mAllowDividerAfterLastItem = paramBoolean;
    }
    
    public void setDivider(Drawable paramDrawable)
    {
      if (paramDrawable != null) {}
      for (this.mDividerHeight = paramDrawable.getIntrinsicHeight();; this.mDividerHeight = 0)
      {
        this.mDivider = paramDrawable;
        PreferenceFragmentCompat.this.mList.invalidateItemDecorations();
        return;
      }
    }
    
    public void setDividerHeight(int paramInt)
    {
      this.mDividerHeight = paramInt;
      PreferenceFragmentCompat.this.mList.invalidateItemDecorations();
    }
  }
  
  public static abstract interface OnPreferenceDisplayDialogCallback
  {
    public abstract boolean onPreferenceDisplayDialog(@NonNull PreferenceFragmentCompat paramPreferenceFragmentCompat, Preference paramPreference);
  }
  
  public static abstract interface OnPreferenceStartFragmentCallback
  {
    public abstract boolean onPreferenceStartFragment(PreferenceFragmentCompat paramPreferenceFragmentCompat, Preference paramPreference);
  }
  
  public static abstract interface OnPreferenceStartScreenCallback
  {
    public abstract boolean onPreferenceStartScreen(PreferenceFragmentCompat paramPreferenceFragmentCompat, PreferenceScreen paramPreferenceScreen);
  }
  
  private static class ScrollToPreferenceObserver
    extends RecyclerView.AdapterDataObserver
  {
    private final RecyclerView.Adapter mAdapter;
    private final String mKey;
    private final RecyclerView mList;
    private final Preference mPreference;
    
    public ScrollToPreferenceObserver(RecyclerView.Adapter paramAdapter, RecyclerView paramRecyclerView, Preference paramPreference, String paramString)
    {
      this.mAdapter = paramAdapter;
      this.mList = paramRecyclerView;
      this.mPreference = paramPreference;
      this.mKey = paramString;
    }
    
    private void scrollToPreference()
    {
      this.mAdapter.unregisterAdapterDataObserver(this);
      if (this.mPreference != null) {}
      for (int i = ((PreferenceGroup.PreferencePositionCallback)this.mAdapter).getPreferenceAdapterPosition(this.mPreference);; i = ((PreferenceGroup.PreferencePositionCallback)this.mAdapter).getPreferenceAdapterPosition(this.mKey))
      {
        if (i != -1) {
          this.mList.scrollToPosition(i);
        }
        return;
      }
    }
    
    public void onChanged()
    {
      scrollToPreference();
    }
    
    public void onItemRangeChanged(int paramInt1, int paramInt2)
    {
      scrollToPreference();
    }
    
    public void onItemRangeChanged(int paramInt1, int paramInt2, Object paramObject)
    {
      scrollToPreference();
    }
    
    public void onItemRangeInserted(int paramInt1, int paramInt2)
    {
      scrollToPreference();
    }
    
    public void onItemRangeMoved(int paramInt1, int paramInt2, int paramInt3)
    {
      scrollToPreference();
    }
    
    public void onItemRangeRemoved(int paramInt1, int paramInt2)
    {
      scrollToPreference();
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v7/preference/PreferenceFragmentCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */