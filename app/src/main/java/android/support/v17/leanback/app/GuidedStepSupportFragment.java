package android.support.v17.leanback.app;

import android.animation.AnimatorSet;
import android.content.Context;
import android.content.res.Resources.Theme;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import android.support.v17.leanback.R.attr;
import android.support.v17.leanback.R.id;
import android.support.v17.leanback.R.layout;
import android.support.v17.leanback.transition.TransitionHelper;
import android.support.v17.leanback.widget.GuidanceStylist;
import android.support.v17.leanback.widget.GuidanceStylist.Guidance;
import android.support.v17.leanback.widget.GuidedAction;
import android.support.v17.leanback.widget.GuidedActionAdapter;
import android.support.v17.leanback.widget.GuidedActionAdapter.ClickListener;
import android.support.v17.leanback.widget.GuidedActionAdapter.EditListener;
import android.support.v17.leanback.widget.GuidedActionAdapter.FocusListener;
import android.support.v17.leanback.widget.GuidedActionAdapterGroup;
import android.support.v17.leanback.widget.GuidedActionsStylist;
import android.support.v17.leanback.widget.VerticalGridView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.BackStackEntry;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.LinearLayout.LayoutParams;
import java.util.ArrayList;
import java.util.List;

public class GuidedStepSupportFragment
  extends Fragment
  implements GuidedActionAdapter.FocusListener
{
  private static final boolean DEBUG = false;
  private static final String ENTRY_NAME_ENTRANCE = "GuidedStepEntrance";
  private static final String ENTRY_NAME_REPLACE = "GuidedStepDefault";
  private static final String EXTRA_ACTION_PREFIX = "action_";
  private static final String EXTRA_BUTTON_ACTION_PREFIX = "buttonaction_";
  public static final String EXTRA_UI_STYLE = "uiStyle";
  private static final boolean IS_FRAMEWORK_FRAGMENT = false;
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  public static final int SLIDE_FROM_BOTTOM = 1;
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  public static final int SLIDE_FROM_SIDE = 0;
  private static final String TAG = "GuidedStepSupportFragment";
  private static final String TAG_LEAN_BACK_ACTIONS_FRAGMENT = "leanBackGuidedStepSupportFragment";
  public static final int UI_STYLE_ACTIVITY_ROOT = 2;
  @Deprecated
  public static final int UI_STYLE_DEFAULT = 0;
  public static final int UI_STYLE_ENTRANCE = 1;
  public static final int UI_STYLE_REPLACE = 0;
  private int entranceTransitionType = 0;
  private List<GuidedAction> mActions = new ArrayList();
  GuidedActionsStylist mActionsStylist = onCreateActionsStylist();
  private GuidedActionAdapter mAdapter;
  private GuidedActionAdapterGroup mAdapterGroup;
  private List<GuidedAction> mButtonActions = new ArrayList();
  private GuidedActionsStylist mButtonActionsStylist = onCreateButtonActionsStylist();
  private GuidedActionAdapter mButtonAdapter;
  private GuidanceStylist mGuidanceStylist = onCreateGuidanceStylist();
  private GuidedActionAdapter mSubAdapter;
  private ContextThemeWrapper mThemeWrapper;
  
  public GuidedStepSupportFragment()
  {
    onProvideFragmentTransitions();
  }
  
  public static int add(FragmentManager paramFragmentManager, GuidedStepSupportFragment paramGuidedStepSupportFragment)
  {
    return add(paramFragmentManager, paramGuidedStepSupportFragment, 16908290);
  }
  
  public static int add(FragmentManager paramFragmentManager, GuidedStepSupportFragment paramGuidedStepSupportFragment, int paramInt)
  {
    int j = 0;
    GuidedStepSupportFragment localGuidedStepSupportFragment = getCurrentGuidedStepSupportFragment(paramFragmentManager);
    if (localGuidedStepSupportFragment != null)
    {
      i = 1;
      paramFragmentManager = paramFragmentManager.beginTransaction();
      if (i == 0) {
        break label71;
      }
    }
    label71:
    for (int i = j;; i = 1)
    {
      paramGuidedStepSupportFragment.setUiStyle(i);
      paramFragmentManager.addToBackStack(paramGuidedStepSupportFragment.generateStackEntryName());
      if (localGuidedStepSupportFragment != null) {
        paramGuidedStepSupportFragment.onAddSharedElementTransition(paramFragmentManager, localGuidedStepSupportFragment);
      }
      return paramFragmentManager.replace(paramInt, paramGuidedStepSupportFragment, "leanBackGuidedStepSupportFragment").commit();
      i = 0;
      break;
    }
  }
  
  public static int addAsRoot(FragmentActivity paramFragmentActivity, GuidedStepSupportFragment paramGuidedStepSupportFragment, int paramInt)
  {
    paramFragmentActivity.getWindow().getDecorView();
    paramFragmentActivity = paramFragmentActivity.getSupportFragmentManager();
    if (paramFragmentActivity.findFragmentByTag("leanBackGuidedStepSupportFragment") != null)
    {
      Log.w("GuidedStepSupportFragment", "Fragment is already exists, likely calling addAsRoot() when savedInstanceState is not null in Activity.onCreate().");
      return -1;
    }
    paramFragmentActivity = paramFragmentActivity.beginTransaction();
    paramGuidedStepSupportFragment.setUiStyle(2);
    return paramFragmentActivity.replace(paramInt, paramGuidedStepSupportFragment, "leanBackGuidedStepSupportFragment").commit();
  }
  
  private static void addNonNullSharedElementTransition(FragmentTransaction paramFragmentTransaction, View paramView, String paramString)
  {
    if (paramView != null) {
      TransitionHelper.addSharedElement(paramFragmentTransaction, paramView, paramString);
    }
  }
  
  static String generateStackEntryName(int paramInt, Class paramClass)
  {
    switch (paramInt)
    {
    default: 
      return "";
    case 0: 
      return "GuidedStepDefault" + paramClass.getName();
    }
    return "GuidedStepEntrance" + paramClass.getName();
  }
  
  public static GuidedStepSupportFragment getCurrentGuidedStepSupportFragment(FragmentManager paramFragmentManager)
  {
    paramFragmentManager = paramFragmentManager.findFragmentByTag("leanBackGuidedStepSupportFragment");
    if ((paramFragmentManager instanceof GuidedStepSupportFragment)) {
      return (GuidedStepSupportFragment)paramFragmentManager;
    }
    return null;
  }
  
  private int getFirstCheckedAction()
  {
    int i = 0;
    int j = this.mActions.size();
    while (i < j)
    {
      if (((GuidedAction)this.mActions.get(i)).isChecked()) {
        return i;
      }
      i += 1;
    }
    return 0;
  }
  
  static String getGuidedStepSupportFragmentClassName(String paramString)
  {
    if (paramString.startsWith("GuidedStepDefault")) {
      return paramString.substring("GuidedStepDefault".length());
    }
    if (paramString.startsWith("GuidedStepEntrance")) {
      return paramString.substring("GuidedStepEntrance".length());
    }
    return "";
  }
  
  private LayoutInflater getThemeInflater(LayoutInflater paramLayoutInflater)
  {
    if (this.mThemeWrapper == null) {
      return paramLayoutInflater;
    }
    return paramLayoutInflater.cloneInContext(this.mThemeWrapper);
  }
  
  private static boolean isGuidedStepTheme(Context paramContext)
  {
    int i = R.attr.guidedStepThemeFlag;
    TypedValue localTypedValue = new TypedValue();
    return (paramContext.getTheme().resolveAttribute(i, localTypedValue, true)) && (localTypedValue.type == 18) && (localTypedValue.data != 0);
  }
  
  static final boolean isSaveEnabled(GuidedAction paramGuidedAction)
  {
    return (paramGuidedAction.isAutoSaveRestoreEnabled()) && (paramGuidedAction.getId() != -1L);
  }
  
  static boolean isStackEntryUiStyleEntrance(String paramString)
  {
    return (paramString != null) && (paramString.startsWith("GuidedStepEntrance"));
  }
  
  private void resolveTheme()
  {
    Object localObject = getContext();
    int i = onProvideTheme();
    if ((i == -1) && (!isGuidedStepTheme((Context)localObject)))
    {
      i = R.attr.guidedStepTheme;
      localTypedValue = new TypedValue();
      bool2 = ((Context)localObject).getTheme().resolveAttribute(i, localTypedValue, true);
      bool1 = bool2;
      if (bool2)
      {
        localObject = new ContextThemeWrapper((Context)localObject, localTypedValue.resourceId);
        if (isGuidedStepTheme((Context)localObject))
        {
          this.mThemeWrapper = ((ContextThemeWrapper)localObject);
          bool1 = bool2;
        }
      }
      else if (!bool1)
      {
        Log.e("GuidedStepSupportFragment", "GuidedStepSupportFragment does not have an appropriate theme set.");
      }
    }
    while (i == -1) {
      for (;;)
      {
        TypedValue localTypedValue;
        boolean bool2;
        return;
        boolean bool1 = false;
        this.mThemeWrapper = null;
      }
    }
    this.mThemeWrapper = new ContextThemeWrapper((Context)localObject, i);
  }
  
  public void collapseAction(boolean paramBoolean)
  {
    if ((this.mActionsStylist != null) && (this.mActionsStylist.getActionsGridView() != null)) {
      this.mActionsStylist.collapseAction(paramBoolean);
    }
  }
  
  public void collapseSubActions()
  {
    collapseAction(true);
  }
  
  public void expandAction(GuidedAction paramGuidedAction, boolean paramBoolean)
  {
    this.mActionsStylist.expandAction(paramGuidedAction, paramBoolean);
  }
  
  public void expandSubActions(GuidedAction paramGuidedAction)
  {
    if (!paramGuidedAction.hasSubActions()) {
      return;
    }
    expandAction(paramGuidedAction, true);
  }
  
  public GuidedAction findActionById(long paramLong)
  {
    int i = findActionPositionById(paramLong);
    if (i >= 0) {
      return (GuidedAction)this.mActions.get(i);
    }
    return null;
  }
  
  public int findActionPositionById(long paramLong)
  {
    if (this.mActions != null)
    {
      int i = 0;
      while (i < this.mActions.size())
      {
        GuidedAction localGuidedAction = (GuidedAction)this.mActions.get(i);
        if (((GuidedAction)this.mActions.get(i)).getId() == paramLong) {
          return i;
        }
        i += 1;
      }
    }
    return -1;
  }
  
  public GuidedAction findButtonActionById(long paramLong)
  {
    int i = findButtonActionPositionById(paramLong);
    if (i >= 0) {
      return (GuidedAction)this.mButtonActions.get(i);
    }
    return null;
  }
  
  public int findButtonActionPositionById(long paramLong)
  {
    if (this.mButtonActions != null)
    {
      int i = 0;
      while (i < this.mButtonActions.size())
      {
        GuidedAction localGuidedAction = (GuidedAction)this.mButtonActions.get(i);
        if (((GuidedAction)this.mButtonActions.get(i)).getId() == paramLong) {
          return i;
        }
        i += 1;
      }
    }
    return -1;
  }
  
  public void finishGuidedStepSupportFragments()
  {
    FragmentManager localFragmentManager = getFragmentManager();
    int i = localFragmentManager.getBackStackEntryCount();
    if (i > 0)
    {
      i -= 1;
      while (i >= 0)
      {
        FragmentManager.BackStackEntry localBackStackEntry = localFragmentManager.getBackStackEntryAt(i);
        if (isStackEntryUiStyleEntrance(localBackStackEntry.getName()))
        {
          GuidedStepSupportFragment localGuidedStepSupportFragment = getCurrentGuidedStepSupportFragment(localFragmentManager);
          if (localGuidedStepSupportFragment != null) {
            localGuidedStepSupportFragment.setUiStyle(1);
          }
          localFragmentManager.popBackStackImmediate(localBackStackEntry.getId(), 1);
          return;
        }
        i -= 1;
      }
    }
    ActivityCompat.finishAfterTransition(getActivity());
  }
  
  final String generateStackEntryName()
  {
    return generateStackEntryName(getUiStyle(), getClass());
  }
  
  public View getActionItemView(int paramInt)
  {
    RecyclerView.ViewHolder localViewHolder = this.mActionsStylist.getActionsGridView().findViewHolderForPosition(paramInt);
    if (localViewHolder == null) {
      return null;
    }
    return localViewHolder.itemView;
  }
  
  public List<GuidedAction> getActions()
  {
    return this.mActions;
  }
  
  final String getAutoRestoreKey(GuidedAction paramGuidedAction)
  {
    return "action_" + paramGuidedAction.getId();
  }
  
  public View getButtonActionItemView(int paramInt)
  {
    RecyclerView.ViewHolder localViewHolder = this.mButtonActionsStylist.getActionsGridView().findViewHolderForPosition(paramInt);
    if (localViewHolder == null) {
      return null;
    }
    return localViewHolder.itemView;
  }
  
  public List<GuidedAction> getButtonActions()
  {
    return this.mButtonActions;
  }
  
  final String getButtonAutoRestoreKey(GuidedAction paramGuidedAction)
  {
    return "buttonaction_" + paramGuidedAction.getId();
  }
  
  public GuidanceStylist getGuidanceStylist()
  {
    return this.mGuidanceStylist;
  }
  
  public GuidedActionsStylist getGuidedActionsStylist()
  {
    return this.mActionsStylist;
  }
  
  public GuidedActionsStylist getGuidedButtonActionsStylist()
  {
    return this.mButtonActionsStylist;
  }
  
  public int getSelectedActionPosition()
  {
    return this.mActionsStylist.getActionsGridView().getSelectedPosition();
  }
  
  public int getSelectedButtonActionPosition()
  {
    return this.mButtonActionsStylist.getActionsGridView().getSelectedPosition();
  }
  
  public int getUiStyle()
  {
    Bundle localBundle = getArguments();
    if (localBundle == null) {
      return 1;
    }
    return localBundle.getInt("uiStyle", 1);
  }
  
  public boolean isExpanded()
  {
    return this.mActionsStylist.isExpanded();
  }
  
  public boolean isFocusOutEndAllowed()
  {
    return false;
  }
  
  public boolean isFocusOutStartAllowed()
  {
    return false;
  }
  
  public boolean isSubActionsExpanded()
  {
    return this.mActionsStylist.isSubActionsExpanded();
  }
  
  public void notifyActionChanged(int paramInt)
  {
    if (this.mAdapter != null) {
      this.mAdapter.notifyItemChanged(paramInt);
    }
  }
  
  public void notifyButtonActionChanged(int paramInt)
  {
    if (this.mButtonAdapter != null) {
      this.mButtonAdapter.notifyItemChanged(paramInt);
    }
  }
  
  protected void onAddSharedElementTransition(FragmentTransaction paramFragmentTransaction, GuidedStepSupportFragment paramGuidedStepSupportFragment)
  {
    paramGuidedStepSupportFragment = paramGuidedStepSupportFragment.getView();
    addNonNullSharedElementTransition(paramFragmentTransaction, paramGuidedStepSupportFragment.findViewById(R.id.action_fragment_root), "action_fragment_root");
    addNonNullSharedElementTransition(paramFragmentTransaction, paramGuidedStepSupportFragment.findViewById(R.id.action_fragment_background), "action_fragment_background");
    addNonNullSharedElementTransition(paramFragmentTransaction, paramGuidedStepSupportFragment.findViewById(R.id.action_fragment), "action_fragment");
    addNonNullSharedElementTransition(paramFragmentTransaction, paramGuidedStepSupportFragment.findViewById(R.id.guidedactions_root), "guidedactions_root");
    addNonNullSharedElementTransition(paramFragmentTransaction, paramGuidedStepSupportFragment.findViewById(R.id.guidedactions_content), "guidedactions_content");
    addNonNullSharedElementTransition(paramFragmentTransaction, paramGuidedStepSupportFragment.findViewById(R.id.guidedactions_list_background), "guidedactions_list_background");
    addNonNullSharedElementTransition(paramFragmentTransaction, paramGuidedStepSupportFragment.findViewById(R.id.guidedactions_root2), "guidedactions_root2");
    addNonNullSharedElementTransition(paramFragmentTransaction, paramGuidedStepSupportFragment.findViewById(R.id.guidedactions_content2), "guidedactions_content2");
    addNonNullSharedElementTransition(paramFragmentTransaction, paramGuidedStepSupportFragment.findViewById(R.id.guidedactions_list_background2), "guidedactions_list_background2");
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    onProvideFragmentTransitions();
    ArrayList localArrayList = new ArrayList();
    onCreateActions(localArrayList, paramBundle);
    if (paramBundle != null) {
      onRestoreActions(localArrayList, paramBundle);
    }
    setActions(localArrayList);
    localArrayList = new ArrayList();
    onCreateButtonActions(localArrayList, paramBundle);
    if (paramBundle != null) {
      onRestoreButtonActions(localArrayList, paramBundle);
    }
    setButtonActions(localArrayList);
  }
  
  public void onCreateActions(@NonNull List<GuidedAction> paramList, Bundle paramBundle) {}
  
  public GuidedActionsStylist onCreateActionsStylist()
  {
    return new GuidedActionsStylist();
  }
  
  public View onCreateBackgroundView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    return paramLayoutInflater.inflate(R.layout.lb_guidedstep_background, paramViewGroup, false);
  }
  
  public void onCreateButtonActions(@NonNull List<GuidedAction> paramList, Bundle paramBundle) {}
  
  public GuidedActionsStylist onCreateButtonActionsStylist()
  {
    GuidedActionsStylist localGuidedActionsStylist = new GuidedActionsStylist();
    localGuidedActionsStylist.setAsButtonActions();
    return localGuidedActionsStylist;
  }
  
  @NonNull
  public GuidanceStylist.Guidance onCreateGuidance(Bundle paramBundle)
  {
    return new GuidanceStylist.Guidance("", "", "", null);
  }
  
  public GuidanceStylist onCreateGuidanceStylist()
  {
    return new GuidanceStylist();
  }
  
  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    resolveTheme();
    LayoutInflater localLayoutInflater = getThemeInflater(paramLayoutInflater);
    paramViewGroup = (GuidedStepRootLayout)localLayoutInflater.inflate(R.layout.lb_guidedstep_fragment, paramViewGroup, false);
    paramViewGroup.setFocusOutStart(isFocusOutStartAllowed());
    paramViewGroup.setFocusOutEnd(isFocusOutEndAllowed());
    Object localObject = (ViewGroup)paramViewGroup.findViewById(R.id.content_fragment);
    paramLayoutInflater = (ViewGroup)paramViewGroup.findViewById(R.id.action_fragment);
    GuidanceStylist.Guidance localGuidance = onCreateGuidance(paramBundle);
    ((ViewGroup)localObject).addView(this.mGuidanceStylist.onCreateView(localLayoutInflater, (ViewGroup)localObject, localGuidance));
    paramLayoutInflater.addView(this.mActionsStylist.onCreateView(localLayoutInflater, paramLayoutInflater));
    localObject = this.mButtonActionsStylist.onCreateView(localLayoutInflater, paramLayoutInflater);
    paramLayoutInflater.addView((View)localObject);
    paramLayoutInflater = new GuidedActionAdapter.EditListener()
    {
      public void onGuidedActionEditCanceled(GuidedAction paramAnonymousGuidedAction)
      {
        GuidedStepSupportFragment.this.onGuidedActionEditCanceled(paramAnonymousGuidedAction);
      }
      
      public long onGuidedActionEditedAndProceed(GuidedAction paramAnonymousGuidedAction)
      {
        return GuidedStepSupportFragment.this.onGuidedActionEditedAndProceed(paramAnonymousGuidedAction);
      }
      
      public void onImeClose()
      {
        GuidedStepSupportFragment.this.runImeAnimations(false);
      }
      
      public void onImeOpen()
      {
        GuidedStepSupportFragment.this.runImeAnimations(true);
      }
    };
    this.mAdapter = new GuidedActionAdapter(this.mActions, new GuidedActionAdapter.ClickListener()
    {
      public void onGuidedActionClicked(GuidedAction paramAnonymousGuidedAction)
      {
        GuidedStepSupportFragment.this.onGuidedActionClicked(paramAnonymousGuidedAction);
        if (GuidedStepSupportFragment.this.isExpanded()) {
          GuidedStepSupportFragment.this.collapseAction(true);
        }
        while ((!paramAnonymousGuidedAction.hasSubActions()) && (!paramAnonymousGuidedAction.hasEditableActivatorView())) {
          return;
        }
        GuidedStepSupportFragment.this.expandAction(paramAnonymousGuidedAction, true);
      }
    }, this, this.mActionsStylist, false);
    this.mButtonAdapter = new GuidedActionAdapter(this.mButtonActions, new GuidedActionAdapter.ClickListener()
    {
      public void onGuidedActionClicked(GuidedAction paramAnonymousGuidedAction)
      {
        GuidedStepSupportFragment.this.onGuidedActionClicked(paramAnonymousGuidedAction);
      }
    }, this, this.mButtonActionsStylist, false);
    this.mSubAdapter = new GuidedActionAdapter(null, new GuidedActionAdapter.ClickListener()
    {
      public void onGuidedActionClicked(GuidedAction paramAnonymousGuidedAction)
      {
        if (GuidedStepSupportFragment.this.mActionsStylist.isInExpandTransition()) {}
        while (!GuidedStepSupportFragment.this.onSubGuidedActionClicked(paramAnonymousGuidedAction)) {
          return;
        }
        GuidedStepSupportFragment.this.collapseSubActions();
      }
    }, this, this.mActionsStylist, true);
    this.mAdapterGroup = new GuidedActionAdapterGroup();
    this.mAdapterGroup.addAdpter(this.mAdapter, this.mButtonAdapter);
    this.mAdapterGroup.addAdpter(this.mSubAdapter, null);
    this.mAdapterGroup.setEditListener(paramLayoutInflater);
    this.mActionsStylist.setEditListener(paramLayoutInflater);
    this.mActionsStylist.getActionsGridView().setAdapter(this.mAdapter);
    if (this.mActionsStylist.getSubActionsGridView() != null) {
      this.mActionsStylist.getSubActionsGridView().setAdapter(this.mSubAdapter);
    }
    this.mButtonActionsStylist.getActionsGridView().setAdapter(this.mButtonAdapter);
    if (this.mButtonActions.size() == 0)
    {
      paramLayoutInflater = (LinearLayout.LayoutParams)((View)localObject).getLayoutParams();
      paramLayoutInflater.weight = 0.0F;
      ((View)localObject).setLayoutParams(paramLayoutInflater);
      paramLayoutInflater = onCreateBackgroundView(localLayoutInflater, paramViewGroup, paramBundle);
      if (paramLayoutInflater != null) {
        ((FrameLayout)paramViewGroup.findViewById(R.id.guidedstep_background_view_root)).addView(paramLayoutInflater, 0);
      }
      return paramViewGroup;
    }
    if (this.mThemeWrapper != null) {}
    for (paramLayoutInflater = this.mThemeWrapper;; paramLayoutInflater = getContext())
    {
      localObject = new TypedValue();
      if (!paramLayoutInflater.getTheme().resolveAttribute(R.attr.guidedActionContentWidthWeightTwoPanels, (TypedValue)localObject, true)) {
        break;
      }
      paramLayoutInflater = paramViewGroup.findViewById(R.id.action_fragment_root);
      float f = ((TypedValue)localObject).getFloat();
      localObject = (LinearLayout.LayoutParams)paramLayoutInflater.getLayoutParams();
      ((LinearLayout.LayoutParams)localObject).weight = f;
      paramLayoutInflater.setLayoutParams((ViewGroup.LayoutParams)localObject);
      break;
    }
  }
  
  public void onDestroyView()
  {
    this.mGuidanceStylist.onDestroyView();
    this.mActionsStylist.onDestroyView();
    this.mButtonActionsStylist.onDestroyView();
    this.mAdapter = null;
    this.mSubAdapter = null;
    this.mButtonAdapter = null;
    this.mAdapterGroup = null;
    super.onDestroyView();
  }
  
  public void onGuidedActionClicked(GuidedAction paramGuidedAction) {}
  
  public void onGuidedActionEditCanceled(GuidedAction paramGuidedAction)
  {
    onGuidedActionEdited(paramGuidedAction);
  }
  
  @Deprecated
  public void onGuidedActionEdited(GuidedAction paramGuidedAction) {}
  
  public long onGuidedActionEditedAndProceed(GuidedAction paramGuidedAction)
  {
    onGuidedActionEdited(paramGuidedAction);
    return -2L;
  }
  
  public void onGuidedActionFocused(GuidedAction paramGuidedAction) {}
  
  protected void onProvideFragmentTransitions()
  {
    int i;
    Object localObject1;
    Object localObject2;
    Object localObject3;
    if (Build.VERSION.SDK_INT >= 21)
    {
      i = getUiStyle();
      if (i != 0) {
        break label115;
      }
      localObject1 = TransitionHelper.createFadeAndShortSlide(8388613);
      TransitionHelper.exclude(localObject1, R.id.guidedstep_background, true);
      TransitionHelper.exclude(localObject1, R.id.guidedactions_sub_list_background, true);
      TransitionHelper.setEnterTransition(this, localObject1);
      localObject1 = TransitionHelper.createFadeTransition(3);
      TransitionHelper.include(localObject1, R.id.guidedactions_sub_list_background);
      localObject2 = TransitionHelper.createChangeBounds(false);
      localObject3 = TransitionHelper.createTransitionSet(false);
      TransitionHelper.addTransition(localObject3, localObject1);
      TransitionHelper.addTransition(localObject3, localObject2);
      TransitionHelper.setSharedElementEnterTransition(this, localObject3);
    }
    for (;;)
    {
      localObject1 = TransitionHelper.createFadeAndShortSlide(8388611);
      TransitionHelper.exclude(localObject1, R.id.guidedstep_background, true);
      TransitionHelper.exclude(localObject1, R.id.guidedactions_sub_list_background, true);
      TransitionHelper.setExitTransition(this, localObject1);
      return;
      label115:
      if (i == 1)
      {
        if (this.entranceTransitionType == 0)
        {
          localObject1 = TransitionHelper.createFadeTransition(3);
          TransitionHelper.include(localObject1, R.id.guidedstep_background);
          localObject2 = TransitionHelper.createFadeAndShortSlide(8388615);
          TransitionHelper.include(localObject2, R.id.content_fragment);
          TransitionHelper.include(localObject2, R.id.action_fragment_root);
          localObject3 = TransitionHelper.createTransitionSet(false);
          TransitionHelper.addTransition(localObject3, localObject1);
          TransitionHelper.addTransition(localObject3, localObject2);
          TransitionHelper.setEnterTransition(this, localObject3);
        }
        for (;;)
        {
          TransitionHelper.setSharedElementEnterTransition(this, null);
          break;
          localObject1 = TransitionHelper.createFadeAndShortSlide(80);
          TransitionHelper.include(localObject1, R.id.guidedstep_background_view_root);
          localObject2 = TransitionHelper.createTransitionSet(false);
          TransitionHelper.addTransition(localObject2, localObject1);
          TransitionHelper.setEnterTransition(this, localObject2);
        }
      }
      if (i == 2)
      {
        TransitionHelper.setEnterTransition(this, null);
        TransitionHelper.setSharedElementEnterTransition(this, null);
      }
    }
  }
  
  public int onProvideTheme()
  {
    return -1;
  }
  
  final void onRestoreActions(List<GuidedAction> paramList, Bundle paramBundle)
  {
    int i = 0;
    int j = paramList.size();
    while (i < j)
    {
      GuidedAction localGuidedAction = (GuidedAction)paramList.get(i);
      if (isSaveEnabled(localGuidedAction)) {
        localGuidedAction.onRestoreInstanceState(paramBundle, getAutoRestoreKey(localGuidedAction));
      }
      i += 1;
    }
  }
  
  final void onRestoreButtonActions(List<GuidedAction> paramList, Bundle paramBundle)
  {
    int i = 0;
    int j = paramList.size();
    while (i < j)
    {
      GuidedAction localGuidedAction = (GuidedAction)paramList.get(i);
      if (isSaveEnabled(localGuidedAction)) {
        localGuidedAction.onRestoreInstanceState(paramBundle, getButtonAutoRestoreKey(localGuidedAction));
      }
      i += 1;
    }
  }
  
  public void onResume()
  {
    super.onResume();
    getView().findViewById(R.id.action_fragment).requestFocus();
  }
  
  final void onSaveActions(List<GuidedAction> paramList, Bundle paramBundle)
  {
    int i = 0;
    int j = paramList.size();
    while (i < j)
    {
      GuidedAction localGuidedAction = (GuidedAction)paramList.get(i);
      if (isSaveEnabled(localGuidedAction)) {
        localGuidedAction.onSaveInstanceState(paramBundle, getAutoRestoreKey(localGuidedAction));
      }
      i += 1;
    }
  }
  
  final void onSaveButtonActions(List<GuidedAction> paramList, Bundle paramBundle)
  {
    int i = 0;
    int j = paramList.size();
    while (i < j)
    {
      GuidedAction localGuidedAction = (GuidedAction)paramList.get(i);
      if (isSaveEnabled(localGuidedAction)) {
        localGuidedAction.onSaveInstanceState(paramBundle, getButtonAutoRestoreKey(localGuidedAction));
      }
      i += 1;
    }
  }
  
  public void onSaveInstanceState(Bundle paramBundle)
  {
    super.onSaveInstanceState(paramBundle);
    onSaveActions(this.mActions, paramBundle);
    onSaveButtonActions(this.mButtonActions, paramBundle);
  }
  
  public boolean onSubGuidedActionClicked(GuidedAction paramGuidedAction)
  {
    return true;
  }
  
  public void openInEditMode(GuidedAction paramGuidedAction)
  {
    this.mActionsStylist.openInEditMode(paramGuidedAction);
  }
  
  public void popBackStackToGuidedStepSupportFragment(Class paramClass, int paramInt)
  {
    if (!GuidedStepSupportFragment.class.isAssignableFrom(paramClass)) {}
    for (;;)
    {
      return;
      FragmentManager localFragmentManager = getFragmentManager();
      int i = localFragmentManager.getBackStackEntryCount();
      paramClass = paramClass.getName();
      if (i > 0)
      {
        i -= 1;
        while (i >= 0)
        {
          FragmentManager.BackStackEntry localBackStackEntry = localFragmentManager.getBackStackEntryAt(i);
          if (paramClass.equals(getGuidedStepSupportFragmentClassName(localBackStackEntry.getName())))
          {
            localFragmentManager.popBackStackImmediate(localBackStackEntry.getId(), paramInt);
            return;
          }
          i -= 1;
        }
      }
    }
  }
  
  void runImeAnimations(boolean paramBoolean)
  {
    ArrayList localArrayList = new ArrayList();
    if (paramBoolean)
    {
      this.mGuidanceStylist.onImeAppearing(localArrayList);
      this.mActionsStylist.onImeAppearing(localArrayList);
      this.mButtonActionsStylist.onImeAppearing(localArrayList);
    }
    for (;;)
    {
      AnimatorSet localAnimatorSet = new AnimatorSet();
      localAnimatorSet.playTogether(localArrayList);
      localAnimatorSet.start();
      return;
      this.mGuidanceStylist.onImeDisappearing(localArrayList);
      this.mActionsStylist.onImeDisappearing(localArrayList);
      this.mButtonActionsStylist.onImeDisappearing(localArrayList);
    }
  }
  
  public void setActions(List<GuidedAction> paramList)
  {
    this.mActions = paramList;
    if (this.mAdapter != null) {
      this.mAdapter.setActions(this.mActions);
    }
  }
  
  public void setButtonActions(List<GuidedAction> paramList)
  {
    this.mButtonActions = paramList;
    if (this.mButtonAdapter != null) {
      this.mButtonAdapter.setActions(this.mButtonActions);
    }
  }
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  public void setEntranceTransitionType(int paramInt)
  {
    this.entranceTransitionType = paramInt;
  }
  
  public void setSelectedActionPosition(int paramInt)
  {
    this.mActionsStylist.getActionsGridView().setSelectedPosition(paramInt);
  }
  
  public void setSelectedButtonActionPosition(int paramInt)
  {
    this.mButtonActionsStylist.getActionsGridView().setSelectedPosition(paramInt);
  }
  
  public void setUiStyle(int paramInt)
  {
    int j = getUiStyle();
    Bundle localBundle2 = getArguments();
    int i = 0;
    Bundle localBundle1 = localBundle2;
    if (localBundle2 == null)
    {
      localBundle1 = new Bundle();
      i = 1;
    }
    localBundle1.putInt("uiStyle", paramInt);
    if (i != 0) {
      setArguments(localBundle1);
    }
    if (paramInt != j) {
      onProvideFragmentTransitions();
    }
  }
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  public static class DummyFragment
    extends Fragment
  {
    public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
    {
      paramLayoutInflater = new View(paramLayoutInflater.getContext());
      paramLayoutInflater.setVisibility(8);
      return paramLayoutInflater;
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/app/GuidedStepSupportFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */