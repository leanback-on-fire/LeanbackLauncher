package android.support.v7.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.annotation.RestrictTo;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.util.DiffUtil;
import android.support.v7.util.DiffUtil.Callback;
import android.support.v7.util.DiffUtil.DiffResult;
import android.support.v7.widget.RecyclerView.Adapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
public class PreferenceGroupAdapter
  extends RecyclerView.Adapter<PreferenceViewHolder>
  implements Preference.OnPreferenceChangeInternalListener, PreferenceGroup.PreferencePositionCallback
{
  private static final String TAG = "PreferenceGroupAdapter";
  private Handler mHandler = new Handler();
  private PreferenceGroup mPreferenceGroup;
  private List<PreferenceLayout> mPreferenceLayouts;
  private List<Preference> mPreferenceList;
  private List<Preference> mPreferenceListInternal;
  private Runnable mSyncRunnable = new Runnable()
  {
    public void run()
    {
      PreferenceGroupAdapter.this.syncMyPreferences();
    }
  };
  private PreferenceLayout mTempPreferenceLayout = new PreferenceLayout();
  
  public PreferenceGroupAdapter(PreferenceGroup paramPreferenceGroup)
  {
    this.mPreferenceGroup = paramPreferenceGroup;
    this.mPreferenceGroup.setOnPreferenceChangeInternalListener(this);
    this.mPreferenceList = new ArrayList();
    this.mPreferenceListInternal = new ArrayList();
    this.mPreferenceLayouts = new ArrayList();
    if ((this.mPreferenceGroup instanceof PreferenceScreen)) {
      setHasStableIds(((PreferenceScreen)this.mPreferenceGroup).shouldUseGeneratedIds());
    }
    for (;;)
    {
      syncMyPreferences();
      return;
      setHasStableIds(true);
    }
  }
  
  private void addPreferenceClassName(Preference paramPreference)
  {
    paramPreference = createPreferenceLayout(paramPreference, null);
    if (!this.mPreferenceLayouts.contains(paramPreference)) {
      this.mPreferenceLayouts.add(paramPreference);
    }
  }
  
  private PreferenceLayout createPreferenceLayout(Preference paramPreference, PreferenceLayout paramPreferenceLayout)
  {
    if (paramPreferenceLayout != null) {}
    for (;;)
    {
      PreferenceLayout.access$102(paramPreferenceLayout, paramPreference.getClass().getName());
      PreferenceLayout.access$202(paramPreferenceLayout, paramPreference.getLayoutResource());
      PreferenceLayout.access$302(paramPreferenceLayout, paramPreference.getWidgetLayoutResource());
      return paramPreferenceLayout;
      paramPreferenceLayout = new PreferenceLayout();
    }
  }
  
  private void flattenPreferenceGroup(List<Preference> paramList, PreferenceGroup paramPreferenceGroup)
  {
    paramPreferenceGroup.sortPreferences();
    int j = paramPreferenceGroup.getPreferenceCount();
    int i = 0;
    while (i < j)
    {
      Preference localPreference = paramPreferenceGroup.getPreference(i);
      paramList.add(localPreference);
      addPreferenceClassName(localPreference);
      if ((localPreference instanceof PreferenceGroup))
      {
        PreferenceGroup localPreferenceGroup = (PreferenceGroup)localPreference;
        if (localPreferenceGroup.isOnSameScreenAsChildren()) {
          flattenPreferenceGroup(paramList, localPreferenceGroup);
        }
      }
      localPreference.setOnPreferenceChangeInternalListener(this);
      i += 1;
    }
  }
  
  private void syncMyPreferences()
  {
    Object localObject1 = this.mPreferenceListInternal.iterator();
    while (((Iterator)localObject1).hasNext()) {
      ((Preference)((Iterator)localObject1).next()).setOnPreferenceChangeInternalListener(null);
    }
    localObject1 = new ArrayList(this.mPreferenceListInternal.size());
    flattenPreferenceGroup((List)localObject1, this.mPreferenceGroup);
    final ArrayList localArrayList = new ArrayList(((List)localObject1).size());
    final Object localObject2 = ((List)localObject1).iterator();
    while (((Iterator)localObject2).hasNext())
    {
      localObject3 = (Preference)((Iterator)localObject2).next();
      if (((Preference)localObject3).isVisible()) {
        localArrayList.add(localObject3);
      }
    }
    localObject2 = this.mPreferenceList;
    this.mPreferenceList = localArrayList;
    this.mPreferenceListInternal = ((List)localObject1);
    Object localObject3 = this.mPreferenceGroup.getPreferenceManager();
    if ((localObject3 != null) && (((PreferenceManager)localObject3).getPreferenceComparisonCallback() != null)) {
      DiffUtil.calculateDiff(new DiffUtil.Callback()
      {
        public boolean areContentsTheSame(int paramAnonymousInt1, int paramAnonymousInt2)
        {
          return this.val$comparisonCallback.arePreferenceContentsTheSame((Preference)localObject2.get(paramAnonymousInt1), (Preference)localArrayList.get(paramAnonymousInt2));
        }
        
        public boolean areItemsTheSame(int paramAnonymousInt1, int paramAnonymousInt2)
        {
          return this.val$comparisonCallback.arePreferenceItemsTheSame((Preference)localObject2.get(paramAnonymousInt1), (Preference)localArrayList.get(paramAnonymousInt2));
        }
        
        public int getNewListSize()
        {
          return localArrayList.size();
        }
        
        public int getOldListSize()
        {
          return localObject2.size();
        }
      }).dispatchUpdatesTo(this);
    }
    for (;;)
    {
      localObject1 = ((List)localObject1).iterator();
      while (((Iterator)localObject1).hasNext()) {
        ((Preference)((Iterator)localObject1).next()).clearWasDetached();
      }
      notifyDataSetChanged();
    }
  }
  
  public Preference getItem(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= getItemCount())) {
      return null;
    }
    return (Preference)this.mPreferenceList.get(paramInt);
  }
  
  public int getItemCount()
  {
    return this.mPreferenceList.size();
  }
  
  public long getItemId(int paramInt)
  {
    if (!hasStableIds()) {
      return -1L;
    }
    return getItem(paramInt).getId();
  }
  
  public int getItemViewType(int paramInt)
  {
    this.mTempPreferenceLayout = createPreferenceLayout(getItem(paramInt), this.mTempPreferenceLayout);
    paramInt = this.mPreferenceLayouts.indexOf(this.mTempPreferenceLayout);
    if (paramInt != -1) {
      return paramInt;
    }
    paramInt = this.mPreferenceLayouts.size();
    this.mPreferenceLayouts.add(new PreferenceLayout(this.mTempPreferenceLayout));
    return paramInt;
  }
  
  public int getPreferenceAdapterPosition(Preference paramPreference)
  {
    int j = this.mPreferenceList.size();
    int i = 0;
    while (i < j)
    {
      Preference localPreference = (Preference)this.mPreferenceList.get(i);
      if ((localPreference != null) && (localPreference.equals(paramPreference))) {
        return i;
      }
      i += 1;
    }
    return -1;
  }
  
  public int getPreferenceAdapterPosition(String paramString)
  {
    int j = this.mPreferenceList.size();
    int i = 0;
    while (i < j)
    {
      if (TextUtils.equals(paramString, ((Preference)this.mPreferenceList.get(i)).getKey())) {
        return i;
      }
      i += 1;
    }
    return -1;
  }
  
  public void onBindViewHolder(PreferenceViewHolder paramPreferenceViewHolder, int paramInt)
  {
    getItem(paramInt).onBindViewHolder(paramPreferenceViewHolder);
  }
  
  public PreferenceViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
  {
    PreferenceLayout localPreferenceLayout = (PreferenceLayout)this.mPreferenceLayouts.get(paramInt);
    LayoutInflater localLayoutInflater = LayoutInflater.from(paramViewGroup.getContext());
    TypedArray localTypedArray = paramViewGroup.getContext().obtainStyledAttributes(null, R.styleable.BackgroundStyle);
    Drawable localDrawable = localTypedArray.getDrawable(R.styleable.BackgroundStyle_android_selectableItemBackground);
    Object localObject = localDrawable;
    if (localDrawable == null) {
      localObject = ContextCompat.getDrawable(paramViewGroup.getContext(), 17301602);
    }
    localTypedArray.recycle();
    paramViewGroup = localLayoutInflater.inflate(localPreferenceLayout.resId, paramViewGroup, false);
    if (paramViewGroup.getBackground() == null) {
      ViewCompat.setBackground(paramViewGroup, (Drawable)localObject);
    }
    localObject = (ViewGroup)paramViewGroup.findViewById(16908312);
    if (localObject != null)
    {
      if (localPreferenceLayout.widgetResId == 0) {
        break label140;
      }
      localLayoutInflater.inflate(localPreferenceLayout.widgetResId, (ViewGroup)localObject);
    }
    for (;;)
    {
      return new PreferenceViewHolder(paramViewGroup);
      label140:
      ((ViewGroup)localObject).setVisibility(8);
    }
  }
  
  public void onPreferenceChange(Preference paramPreference)
  {
    int i = this.mPreferenceList.indexOf(paramPreference);
    if (i != -1) {
      notifyItemChanged(i, paramPreference);
    }
  }
  
  public void onPreferenceHierarchyChange(Preference paramPreference)
  {
    this.mHandler.removeCallbacks(this.mSyncRunnable);
    this.mHandler.post(this.mSyncRunnable);
  }
  
  public void onPreferenceVisibilityChange(Preference paramPreference)
  {
    if (!this.mPreferenceListInternal.contains(paramPreference)) {
      return;
    }
    if (paramPreference.isVisible())
    {
      i = -1;
      Iterator localIterator = this.mPreferenceListInternal.iterator();
      for (;;)
      {
        Preference localPreference;
        if (localIterator.hasNext())
        {
          localPreference = (Preference)localIterator.next();
          if (!paramPreference.equals(localPreference)) {}
        }
        else
        {
          this.mPreferenceList.add(i + 1, paramPreference);
          notifyItemInserted(i + 1);
          return;
        }
        if (localPreference.isVisible()) {
          i += 1;
        }
      }
    }
    int j = this.mPreferenceList.size();
    int i = 0;
    for (;;)
    {
      if ((i >= j) || (paramPreference.equals(this.mPreferenceList.get(i))))
      {
        this.mPreferenceList.remove(i);
        notifyItemRemoved(i);
        return;
      }
      i += 1;
    }
  }
  
  private static class PreferenceLayout
  {
    private String name;
    private int resId;
    private int widgetResId;
    
    public PreferenceLayout() {}
    
    public PreferenceLayout(PreferenceLayout paramPreferenceLayout)
    {
      this.resId = paramPreferenceLayout.resId;
      this.widgetResId = paramPreferenceLayout.widgetResId;
      this.name = paramPreferenceLayout.name;
    }
    
    public boolean equals(Object paramObject)
    {
      if (!(paramObject instanceof PreferenceLayout)) {}
      do
      {
        return false;
        paramObject = (PreferenceLayout)paramObject;
      } while ((this.resId != ((PreferenceLayout)paramObject).resId) || (this.widgetResId != ((PreferenceLayout)paramObject).widgetResId) || (!TextUtils.equals(this.name, ((PreferenceLayout)paramObject).name)));
      return true;
    }
    
    public int hashCode()
    {
      return ((this.resId + 527) * 31 + this.widgetResId) * 31 + this.name.hashCode();
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v7/preference/PreferenceGroupAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */