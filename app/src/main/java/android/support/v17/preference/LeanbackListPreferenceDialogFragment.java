package android.support.v17.preference;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v14.preference.MultiSelectListPreference;
import android.support.v17.leanback.widget.VerticalGridView;
import android.support.v4.util.ArraySet;
import android.support.v7.preference.DialogPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.TextView;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class LeanbackListPreferenceDialogFragment
  extends LeanbackPreferenceDialogFragment
{
  private static final String SAVE_STATE_ENTRIES = "LeanbackListPreferenceDialogFragment.entries";
  private static final String SAVE_STATE_ENTRY_VALUES = "LeanbackListPreferenceDialogFragment.entryValues";
  private static final String SAVE_STATE_INITIAL_SELECTION = "LeanbackListPreferenceDialogFragment.initialSelection";
  private static final String SAVE_STATE_INITIAL_SELECTIONS = "LeanbackListPreferenceDialogFragment.initialSelections";
  private static final String SAVE_STATE_IS_MULTI = "LeanbackListPreferenceDialogFragment.isMulti";
  private static final String SAVE_STATE_MESSAGE = "LeanbackListPreferenceDialogFragment.message";
  private static final String SAVE_STATE_TITLE = "LeanbackListPreferenceDialogFragment.title";
  private CharSequence mDialogMessage;
  private CharSequence mDialogTitle;
  private CharSequence[] mEntries;
  private CharSequence[] mEntryValues;
  private String mInitialSelection;
  private Set<String> mInitialSelections;
  private boolean mMulti;
  
  public static LeanbackListPreferenceDialogFragment newInstanceMulti(String paramString)
  {
    Bundle localBundle = new Bundle(1);
    localBundle.putString("key", paramString);
    paramString = new LeanbackListPreferenceDialogFragment();
    paramString.setArguments(localBundle);
    return paramString;
  }
  
  public static LeanbackListPreferenceDialogFragment newInstanceSingle(String paramString)
  {
    Bundle localBundle = new Bundle(1);
    localBundle.putString("key", paramString);
    paramString = new LeanbackListPreferenceDialogFragment();
    paramString.setArguments(localBundle);
    return paramString;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    int i = 0;
    super.onCreate(paramBundle);
    if (paramBundle == null)
    {
      paramBundle = getPreference();
      this.mDialogTitle = paramBundle.getDialogTitle();
      this.mDialogMessage = paramBundle.getDialogMessage();
      if ((paramBundle instanceof ListPreference))
      {
        this.mMulti = false;
        this.mEntries = ((ListPreference)paramBundle).getEntries();
        this.mEntryValues = ((ListPreference)paramBundle).getEntryValues();
        this.mInitialSelection = ((ListPreference)paramBundle).getValue();
      }
    }
    do
    {
      return;
      if ((paramBundle instanceof MultiSelectListPreference))
      {
        this.mMulti = true;
        this.mEntries = ((MultiSelectListPreference)paramBundle).getEntries();
        this.mEntryValues = ((MultiSelectListPreference)paramBundle).getEntryValues();
        this.mInitialSelections = ((MultiSelectListPreference)paramBundle).getValues();
        return;
      }
      throw new IllegalArgumentException("Preference must be a ListPreference or MultiSelectListPreference");
      this.mDialogTitle = paramBundle.getCharSequence("LeanbackListPreferenceDialogFragment.title");
      this.mDialogMessage = paramBundle.getCharSequence("LeanbackListPreferenceDialogFragment.message");
      this.mMulti = paramBundle.getBoolean("LeanbackListPreferenceDialogFragment.isMulti");
      this.mEntries = paramBundle.getCharSequenceArray("LeanbackListPreferenceDialogFragment.entries");
      this.mEntryValues = paramBundle.getCharSequenceArray("LeanbackListPreferenceDialogFragment.entryValues");
      if (!this.mMulti) {
        break;
      }
      paramBundle = paramBundle.getStringArray("LeanbackListPreferenceDialogFragment.initialSelections");
      if (paramBundle != null) {
        i = paramBundle.length;
      }
      this.mInitialSelections = new ArraySet(i);
    } while (paramBundle == null);
    Collections.addAll(this.mInitialSelections, paramBundle);
    return;
    this.mInitialSelection = paramBundle.getString("LeanbackListPreferenceDialogFragment.initialSelection");
  }
  
  public RecyclerView.Adapter onCreateAdapter()
  {
    if (this.mMulti) {
      return new AdapterMulti(this.mEntries, this.mEntryValues, this.mInitialSelections);
    }
    return new AdapterSingle(this.mEntries, this.mEntryValues, this.mInitialSelection);
  }
  
  @Nullable
  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    paramLayoutInflater = paramLayoutInflater.inflate(R.layout.leanback_list_preference_fragment, paramViewGroup, false);
    paramViewGroup = (VerticalGridView)paramLayoutInflater.findViewById(16908298);
    paramViewGroup.setWindowAlignment(3);
    paramViewGroup.setFocusScrollStrategy(0);
    paramViewGroup.setAdapter(onCreateAdapter());
    paramViewGroup.requestFocus();
    paramViewGroup = this.mDialogTitle;
    if (!TextUtils.isEmpty(paramViewGroup)) {
      ((TextView)paramLayoutInflater.findViewById(R.id.decor_title)).setText(paramViewGroup);
    }
    paramViewGroup = this.mDialogMessage;
    if (!TextUtils.isEmpty(paramViewGroup))
    {
      paramBundle = (TextView)paramLayoutInflater.findViewById(16908299);
      paramBundle.setVisibility(0);
      paramBundle.setText(paramViewGroup);
    }
    return paramLayoutInflater;
  }
  
  public void onSaveInstanceState(Bundle paramBundle)
  {
    super.onSaveInstanceState(paramBundle);
    paramBundle.putCharSequence("LeanbackListPreferenceDialogFragment.title", this.mDialogTitle);
    paramBundle.putCharSequence("LeanbackListPreferenceDialogFragment.message", this.mDialogMessage);
    paramBundle.putBoolean("LeanbackListPreferenceDialogFragment.isMulti", this.mMulti);
    paramBundle.putCharSequenceArray("LeanbackListPreferenceDialogFragment.entries", this.mEntries);
    paramBundle.putCharSequenceArray("LeanbackListPreferenceDialogFragment.entryValues", this.mEntryValues);
    if (this.mMulti)
    {
      paramBundle.putStringArray("LeanbackListPreferenceDialogFragment.initialSelections", (String[])this.mInitialSelections.toArray(new String[this.mInitialSelections.size()]));
      return;
    }
    paramBundle.putString("LeanbackListPreferenceDialogFragment.initialSelection", this.mInitialSelection);
  }
  
  public class AdapterMulti
    extends RecyclerView.Adapter<LeanbackListPreferenceDialogFragment.ViewHolder>
    implements LeanbackListPreferenceDialogFragment.ViewHolder.OnItemClickListener
  {
    private final CharSequence[] mEntries;
    private final CharSequence[] mEntryValues;
    private final Set<String> mSelections;
    
    public AdapterMulti(CharSequence[] paramArrayOfCharSequence, Set<String> paramSet)
    {
      this.mEntries = paramArrayOfCharSequence;
      this.mEntryValues = paramSet;
      Collection localCollection;
      this.mSelections = new HashSet(localCollection);
    }
    
    public int getItemCount()
    {
      return this.mEntries.length;
    }
    
    public void onBindViewHolder(LeanbackListPreferenceDialogFragment.ViewHolder paramViewHolder, int paramInt)
    {
      paramViewHolder.getWidgetView().setChecked(this.mSelections.contains(this.mEntryValues[paramInt].toString()));
      paramViewHolder.getTitleView().setText(this.mEntries[paramInt]);
    }
    
    public LeanbackListPreferenceDialogFragment.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      return new LeanbackListPreferenceDialogFragment.ViewHolder(LayoutInflater.from(paramViewGroup.getContext()).inflate(R.layout.leanback_list_preference_item_multi, paramViewGroup, false), this);
    }
    
    public void onItemClick(LeanbackListPreferenceDialogFragment.ViewHolder paramViewHolder)
    {
      int i = paramViewHolder.getAdapterPosition();
      if (i == -1) {
        return;
      }
      paramViewHolder = this.mEntryValues[i].toString();
      if (this.mSelections.contains(paramViewHolder))
      {
        this.mSelections.remove(paramViewHolder);
        MultiSelectListPreference localMultiSelectListPreference = (MultiSelectListPreference)LeanbackListPreferenceDialogFragment.this.getPreference();
        if (!localMultiSelectListPreference.callChangeListener(new HashSet(this.mSelections))) {
          break label122;
        }
        localMultiSelectListPreference.setValues(new HashSet(this.mSelections));
        LeanbackListPreferenceDialogFragment.access$002(LeanbackListPreferenceDialogFragment.this, this.mSelections);
      }
      for (;;)
      {
        notifyDataSetChanged();
        return;
        this.mSelections.add(paramViewHolder);
        break;
        label122:
        if (this.mSelections.contains(paramViewHolder)) {
          this.mSelections.remove(paramViewHolder);
        } else {
          this.mSelections.add(paramViewHolder);
        }
      }
    }
  }
  
  public class AdapterSingle
    extends RecyclerView.Adapter<LeanbackListPreferenceDialogFragment.ViewHolder>
    implements LeanbackListPreferenceDialogFragment.ViewHolder.OnItemClickListener
  {
    private final CharSequence[] mEntries;
    private final CharSequence[] mEntryValues;
    private CharSequence mSelectedValue;
    
    public AdapterSingle(CharSequence[] paramArrayOfCharSequence1, CharSequence[] paramArrayOfCharSequence2, CharSequence paramCharSequence)
    {
      this.mEntries = paramArrayOfCharSequence1;
      this.mEntryValues = paramArrayOfCharSequence2;
      this.mSelectedValue = paramCharSequence;
    }
    
    public int getItemCount()
    {
      return this.mEntries.length;
    }
    
    public void onBindViewHolder(LeanbackListPreferenceDialogFragment.ViewHolder paramViewHolder, int paramInt)
    {
      paramViewHolder.getWidgetView().setChecked(this.mEntryValues[paramInt].equals(this.mSelectedValue));
      paramViewHolder.getTitleView().setText(this.mEntries[paramInt]);
    }
    
    public LeanbackListPreferenceDialogFragment.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      return new LeanbackListPreferenceDialogFragment.ViewHolder(LayoutInflater.from(paramViewGroup.getContext()).inflate(R.layout.leanback_list_preference_item_single, paramViewGroup, false), this);
    }
    
    public void onItemClick(LeanbackListPreferenceDialogFragment.ViewHolder paramViewHolder)
    {
      int i = paramViewHolder.getAdapterPosition();
      if (i == -1) {
        return;
      }
      paramViewHolder = this.mEntryValues[i];
      ListPreference localListPreference = (ListPreference)LeanbackListPreferenceDialogFragment.this.getPreference();
      if (i >= 0)
      {
        String str = this.mEntryValues[i].toString();
        if (localListPreference.callChangeListener(str))
        {
          localListPreference.setValue(str);
          this.mSelectedValue = paramViewHolder;
        }
      }
      LeanbackListPreferenceDialogFragment.this.getFragmentManager().popBackStack();
      notifyDataSetChanged();
    }
  }
  
  public static class ViewHolder
    extends RecyclerView.ViewHolder
    implements View.OnClickListener
  {
    private final ViewGroup mContainer;
    private final OnItemClickListener mListener;
    private final TextView mTitleView;
    private final Checkable mWidgetView;
    
    public ViewHolder(@NonNull View paramView, @NonNull OnItemClickListener paramOnItemClickListener)
    {
      super();
      this.mWidgetView = ((Checkable)paramView.findViewById(R.id.button));
      this.mContainer = ((ViewGroup)paramView.findViewById(R.id.container));
      this.mTitleView = ((TextView)paramView.findViewById(16908310));
      this.mContainer.setOnClickListener(this);
      this.mListener = paramOnItemClickListener;
    }
    
    public ViewGroup getContainer()
    {
      return this.mContainer;
    }
    
    public TextView getTitleView()
    {
      return this.mTitleView;
    }
    
    public Checkable getWidgetView()
    {
      return this.mWidgetView;
    }
    
    public void onClick(View paramView)
    {
      this.mListener.onItemClick(this);
    }
    
    public static abstract interface OnItemClickListener
    {
      public abstract void onItemClick(LeanbackListPreferenceDialogFragment.ViewHolder paramViewHolder);
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/preference/LeanbackListPreferenceDialogFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */