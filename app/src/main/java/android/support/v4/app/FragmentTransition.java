package android.support.v4.app;

import android.graphics.Rect;
import android.os.Build.VERSION;
import android.support.annotation.RequiresApi;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.ViewCompat;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

class FragmentTransition
{
  private static final int[] INVERSE_OPS = { 0, 3, 0, 1, 5, 4, 7, 6, 9, 8 };
  
  private static void addSharedElementsWithMatchingNames(ArrayList<View> paramArrayList, ArrayMap<String, View> paramArrayMap, Collection<String> paramCollection)
  {
    int i = paramArrayMap.size() - 1;
    while (i >= 0)
    {
      View localView = (View)paramArrayMap.valueAt(i);
      if (paramCollection.contains(ViewCompat.getTransitionName(localView))) {
        paramArrayList.add(localView);
      }
      i -= 1;
    }
  }
  
  private static void addToFirstInLastOut(BackStackRecord paramBackStackRecord, BackStackRecord.Op paramOp, SparseArray<FragmentContainerTransition> paramSparseArray, boolean paramBoolean1, boolean paramBoolean2)
  {
    Fragment localFragment = paramOp.fragment;
    if (localFragment == null) {}
    int i3;
    do
    {
      return;
      i3 = localFragment.mContainerId;
    } while (i3 == 0);
    int m;
    label38:
    boolean bool2;
    int i1;
    int i2;
    int n;
    boolean bool1;
    int j;
    int k;
    if (paramBoolean1)
    {
      m = INVERSE_OPS[paramOp.cmd];
      bool2 = false;
      i1 = 0;
      i2 = 0;
      n = 0;
      i = i2;
      bool1 = bool2;
      j = n;
      k = i1;
    }
    switch (m)
    {
    default: 
      k = i1;
      j = n;
      bool1 = bool2;
      i = i2;
    case 2: 
    case 5: 
    case 1: 
    case 7: 
      for (;;)
      {
        Object localObject = (FragmentContainerTransition)paramSparseArray.get(i3);
        paramOp = (BackStackRecord.Op)localObject;
        if (bool1)
        {
          paramOp = ensureContainer((FragmentContainerTransition)localObject, paramSparseArray, i3);
          paramOp.lastIn = localFragment;
          paramOp.lastInIsPop = paramBoolean1;
          paramOp.lastInTransaction = paramBackStackRecord;
        }
        if ((!paramBoolean2) && (j != 0))
        {
          if ((paramOp != null) && (paramOp.firstOut == localFragment)) {
            paramOp.firstOut = null;
          }
          localObject = paramBackStackRecord.mManager;
          if ((localFragment.mState < 1) && (((FragmentManagerImpl)localObject).mCurState >= 1) && (!paramBackStackRecord.mReorderingAllowed))
          {
            ((FragmentManagerImpl)localObject).makeActive(localFragment);
            ((FragmentManagerImpl)localObject).moveToState(localFragment, 1, 0, 0, false);
          }
        }
        localObject = paramOp;
        if (i != 0) {
          if (paramOp != null)
          {
            localObject = paramOp;
            if (paramOp.firstOut != null) {}
          }
          else
          {
            localObject = ensureContainer(paramOp, paramSparseArray, i3);
            ((FragmentContainerTransition)localObject).firstOut = localFragment;
            ((FragmentContainerTransition)localObject).firstOutIsPop = paramBoolean1;
            ((FragmentContainerTransition)localObject).firstOutTransaction = paramBackStackRecord;
          }
        }
        if ((paramBoolean2) || (k == 0) || (localObject == null) || (((FragmentContainerTransition)localObject).lastIn != localFragment)) {
          break;
        }
        ((FragmentContainerTransition)localObject).lastIn = null;
        return;
        m = paramOp.cmd;
        break label38;
        if (paramBoolean2) {
          if ((localFragment.mHiddenChanged) && (!localFragment.mHidden) && (localFragment.mAdded)) {
            bool1 = true;
          }
        }
        for (;;)
        {
          j = 1;
          i = i2;
          k = i1;
          break;
          bool1 = false;
          continue;
          bool1 = localFragment.mHidden;
        }
        if (!paramBoolean2) {
          break label428;
        }
        bool1 = localFragment.mIsNewlyAdded;
        j = 1;
        i = i2;
        k = i1;
      }
      if ((!localFragment.mAdded) && (!localFragment.mHidden)) {}
      for (bool1 = true;; bool1 = false) {
        break;
      }
    case 4: 
      label428:
      if (paramBoolean2)
      {
        if ((localFragment.mHiddenChanged) && (localFragment.mAdded) && (localFragment.mHidden)) {}
        for (i = 1;; i = 0)
        {
          k = 1;
          bool1 = bool2;
          j = n;
          break;
        }
      }
      if ((localFragment.mAdded) && (!localFragment.mHidden)) {}
      for (i = 1;; i = 0) {
        break;
      }
    }
    if (paramBoolean2)
    {
      if ((!localFragment.mAdded) && (localFragment.mView != null) && (localFragment.mView.getVisibility() == 0) && (localFragment.mPostponedAlpha >= 0.0F)) {}
      for (i = 1;; i = 0)
      {
        k = 1;
        bool1 = bool2;
        j = n;
        break;
      }
    }
    if ((localFragment.mAdded) && (!localFragment.mHidden)) {}
    for (int i = 1;; i = 0) {
      break;
    }
  }
  
  public static void calculateFragments(BackStackRecord paramBackStackRecord, SparseArray<FragmentContainerTransition> paramSparseArray, boolean paramBoolean)
  {
    int j = paramBackStackRecord.mOps.size();
    int i = 0;
    while (i < j)
    {
      addToFirstInLastOut(paramBackStackRecord, (BackStackRecord.Op)paramBackStackRecord.mOps.get(i), paramSparseArray, false, paramBoolean);
      i += 1;
    }
  }
  
  private static ArrayMap<String, String> calculateNameOverrides(int paramInt1, ArrayList<BackStackRecord> paramArrayList, ArrayList<Boolean> paramArrayList1, int paramInt2, int paramInt3)
  {
    ArrayMap localArrayMap = new ArrayMap();
    paramInt3 -= 1;
    if (paramInt3 >= paramInt2)
    {
      Object localObject = (BackStackRecord)paramArrayList.get(paramInt3);
      if (!((BackStackRecord)localObject).interactsWith(paramInt1)) {}
      boolean bool;
      do
      {
        paramInt3 -= 1;
        break;
        bool = ((Boolean)paramArrayList1.get(paramInt3)).booleanValue();
      } while (((BackStackRecord)localObject).mSharedElementSourceNames == null);
      int j = ((BackStackRecord)localObject).mSharedElementSourceNames.size();
      ArrayList localArrayList2;
      ArrayList localArrayList1;
      label101:
      int i;
      label104:
      String str1;
      if (bool)
      {
        localArrayList2 = ((BackStackRecord)localObject).mSharedElementSourceNames;
        localArrayList1 = ((BackStackRecord)localObject).mSharedElementTargetNames;
        i = 0;
        if (i < j)
        {
          localObject = (String)localArrayList1.get(i);
          str1 = (String)localArrayList2.get(i);
          String str2 = (String)localArrayMap.remove(str1);
          if (str2 == null) {
            break label188;
          }
          localArrayMap.put(localObject, str2);
        }
      }
      for (;;)
      {
        i += 1;
        break label104;
        break;
        localArrayList1 = ((BackStackRecord)localObject).mSharedElementSourceNames;
        localArrayList2 = ((BackStackRecord)localObject).mSharedElementTargetNames;
        break label101;
        label188:
        localArrayMap.put(localObject, str1);
      }
    }
    return localArrayMap;
  }
  
  public static void calculatePopFragments(BackStackRecord paramBackStackRecord, SparseArray<FragmentContainerTransition> paramSparseArray, boolean paramBoolean)
  {
    if (!paramBackStackRecord.mManager.mContainer.onHasView()) {}
    for (;;)
    {
      return;
      int i = paramBackStackRecord.mOps.size() - 1;
      while (i >= 0)
      {
        addToFirstInLastOut(paramBackStackRecord, (BackStackRecord.Op)paramBackStackRecord.mOps.get(i), paramSparseArray, true, paramBoolean);
        i -= 1;
      }
    }
  }
  
  private static void callSharedElementStartEnd(Fragment paramFragment1, Fragment paramFragment2, boolean paramBoolean1, ArrayMap<String, View> paramArrayMap, boolean paramBoolean2)
  {
    ArrayList localArrayList;
    if (paramBoolean1)
    {
      paramFragment1 = paramFragment2.getEnterTransitionCallback();
      if (paramFragment1 == null) {
        break label109;
      }
      paramFragment2 = new ArrayList();
      localArrayList = new ArrayList();
      if (paramArrayMap != null) {
        break label87;
      }
    }
    label87:
    for (int i = 0;; i = paramArrayMap.size())
    {
      int j = 0;
      while (j < i)
      {
        localArrayList.add(paramArrayMap.keyAt(j));
        paramFragment2.add(paramArrayMap.valueAt(j));
        j += 1;
      }
      paramFragment1 = paramFragment1.getEnterTransitionCallback();
      break;
    }
    if (paramBoolean2)
    {
      paramFragment1.onSharedElementStart(localArrayList, paramFragment2, null);
      label109:
      return;
    }
    paramFragment1.onSharedElementEnd(localArrayList, paramFragment2, null);
  }
  
  @RequiresApi(21)
  private static ArrayMap<String, View> captureInSharedElements(ArrayMap<String, String> paramArrayMap, Object paramObject, FragmentContainerTransition paramFragmentContainerTransition)
  {
    Object localObject = paramFragmentContainerTransition.lastIn;
    View localView = ((Fragment)localObject).getView();
    if ((paramArrayMap.isEmpty()) || (paramObject == null) || (localView == null))
    {
      paramArrayMap.clear();
      paramFragmentContainerTransition = null;
    }
    ArrayMap localArrayMap;
    int i;
    label105:
    do
    {
      return paramFragmentContainerTransition;
      localArrayMap = new ArrayMap();
      FragmentTransitionCompat21.findNamedViews(localArrayMap, localView);
      paramObject = paramFragmentContainerTransition.lastInTransaction;
      if (!paramFragmentContainerTransition.lastInIsPop) {
        break;
      }
      paramFragmentContainerTransition = ((Fragment)localObject).getExitTransitionCallback();
      paramObject = ((BackStackRecord)paramObject).mSharedElementSourceNames;
      if (paramObject != null) {
        localArrayMap.retainAll((Collection)paramObject);
      }
      if (paramFragmentContainerTransition == null) {
        break label214;
      }
      paramFragmentContainerTransition.onMapSharedElements((List)paramObject, localArrayMap);
      i = ((ArrayList)paramObject).size() - 1;
      paramFragmentContainerTransition = localArrayMap;
    } while (i < 0);
    localObject = (String)((ArrayList)paramObject).get(i);
    paramFragmentContainerTransition = (View)localArrayMap.get(localObject);
    if (paramFragmentContainerTransition == null)
    {
      paramFragmentContainerTransition = findKeyForValue(paramArrayMap, (String)localObject);
      if (paramFragmentContainerTransition != null) {
        paramArrayMap.remove(paramFragmentContainerTransition);
      }
    }
    for (;;)
    {
      i -= 1;
      break label105;
      paramFragmentContainerTransition = ((Fragment)localObject).getEnterTransitionCallback();
      paramObject = ((BackStackRecord)paramObject).mSharedElementTargetNames;
      break;
      if (!((String)localObject).equals(ViewCompat.getTransitionName(paramFragmentContainerTransition)))
      {
        localObject = findKeyForValue(paramArrayMap, (String)localObject);
        if (localObject != null) {
          paramArrayMap.put(localObject, ViewCompat.getTransitionName(paramFragmentContainerTransition));
        }
      }
    }
    label214:
    retainValues(paramArrayMap, localArrayMap);
    return localArrayMap;
  }
  
  @RequiresApi(21)
  private static ArrayMap<String, View> captureOutSharedElements(ArrayMap<String, String> paramArrayMap, Object paramObject, FragmentContainerTransition paramFragmentContainerTransition)
  {
    if ((paramArrayMap.isEmpty()) || (paramObject == null))
    {
      paramArrayMap.clear();
      paramFragmentContainerTransition = null;
    }
    ArrayMap localArrayMap;
    int i;
    label92:
    do
    {
      return paramFragmentContainerTransition;
      localObject = paramFragmentContainerTransition.firstOut;
      localArrayMap = new ArrayMap();
      FragmentTransitionCompat21.findNamedViews(localArrayMap, ((Fragment)localObject).getView());
      paramObject = paramFragmentContainerTransition.firstOutTransaction;
      if (!paramFragmentContainerTransition.firstOutIsPop) {
        break;
      }
      paramFragmentContainerTransition = ((Fragment)localObject).getEnterTransitionCallback();
      paramObject = ((BackStackRecord)paramObject).mSharedElementTargetNames;
      localArrayMap.retainAll((Collection)paramObject);
      if (paramFragmentContainerTransition == null) {
        break label189;
      }
      paramFragmentContainerTransition.onMapSharedElements((List)paramObject, localArrayMap);
      i = ((ArrayList)paramObject).size() - 1;
      paramFragmentContainerTransition = localArrayMap;
    } while (i < 0);
    Object localObject = (String)((ArrayList)paramObject).get(i);
    paramFragmentContainerTransition = (View)localArrayMap.get(localObject);
    if (paramFragmentContainerTransition == null) {
      paramArrayMap.remove(localObject);
    }
    for (;;)
    {
      i -= 1;
      break label92;
      paramFragmentContainerTransition = ((Fragment)localObject).getExitTransitionCallback();
      paramObject = ((BackStackRecord)paramObject).mSharedElementSourceNames;
      break;
      if (!((String)localObject).equals(ViewCompat.getTransitionName(paramFragmentContainerTransition)))
      {
        localObject = (String)paramArrayMap.remove(localObject);
        paramArrayMap.put(ViewCompat.getTransitionName(paramFragmentContainerTransition), localObject);
      }
    }
    label189:
    paramArrayMap.retainAll(localArrayMap.keySet());
    return localArrayMap;
  }
  
  @RequiresApi(21)
  private static ArrayList<View> configureEnteringExitingViews(Object paramObject, Fragment paramFragment, ArrayList<View> paramArrayList, View paramView)
  {
    Object localObject = null;
    if (paramObject != null)
    {
      ArrayList localArrayList = new ArrayList();
      paramFragment = paramFragment.getView();
      if (paramFragment != null) {
        FragmentTransitionCompat21.captureTransitioningViews(localArrayList, paramFragment);
      }
      if (paramArrayList != null) {
        localArrayList.removeAll(paramArrayList);
      }
      localObject = localArrayList;
      if (!localArrayList.isEmpty())
      {
        localArrayList.add(paramView);
        FragmentTransitionCompat21.addTargets(paramObject, localArrayList);
        localObject = localArrayList;
      }
    }
    return (ArrayList<View>)localObject;
  }
  
  @RequiresApi(21)
  private static Object configureSharedElementsOrdered(ViewGroup paramViewGroup, final View paramView, ArrayMap<String, String> paramArrayMap, final FragmentContainerTransition paramFragmentContainerTransition, final ArrayList<View> paramArrayList1, final ArrayList<View> paramArrayList2, final Object paramObject1, final Object paramObject2)
  {
    final Fragment localFragment1 = paramFragmentContainerTransition.lastIn;
    final Fragment localFragment2 = paramFragmentContainerTransition.firstOut;
    if ((localFragment1 == null) || (localFragment2 == null)) {
      return null;
    }
    final boolean bool = paramFragmentContainerTransition.lastInIsPop;
    final Object localObject;
    ArrayMap localArrayMap;
    if (paramArrayMap.isEmpty())
    {
      localObject = null;
      localArrayMap = captureOutSharedElements(paramArrayMap, localObject, paramFragmentContainerTransition);
      if (!paramArrayMap.isEmpty()) {
        break label90;
      }
      localObject = null;
    }
    for (;;)
    {
      if ((paramObject1 != null) || (paramObject2 != null) || (localObject != null)) {
        break label104;
      }
      return null;
      localObject = getSharedElementTransition(localFragment1, localFragment2, bool);
      break;
      label90:
      paramArrayList1.addAll(localArrayMap.values());
    }
    label104:
    callSharedElementStartEnd(localFragment1, localFragment2, bool, localArrayMap, true);
    Rect localRect;
    if (localObject != null)
    {
      localRect = new Rect();
      FragmentTransitionCompat21.setSharedElementTargets(localObject, paramView, paramArrayList1);
      setOutEpicenter(localObject, paramObject2, localArrayMap, paramFragmentContainerTransition.firstOutIsPop, paramFragmentContainerTransition.firstOutTransaction);
      paramObject2 = localRect;
      if (paramObject1 != null) {
        FragmentTransitionCompat21.setEpicenter(paramObject1, localRect);
      }
    }
    for (paramObject2 = localRect;; paramObject2 = null)
    {
      OneShotPreDrawListener.add(paramViewGroup, new Runnable()
      {
        public void run()
        {
          Object localObject = FragmentTransition.captureInSharedElements(this.val$nameOverrides, localObject, paramFragmentContainerTransition);
          if (localObject != null)
          {
            paramArrayList2.addAll(((ArrayMap)localObject).values());
            paramArrayList2.add(paramView);
          }
          FragmentTransition.callSharedElementStartEnd(localFragment1, localFragment2, bool, (ArrayMap)localObject, false);
          if (localObject != null)
          {
            FragmentTransitionCompat21.swapSharedElementTargets(localObject, paramArrayList1, paramArrayList2);
            localObject = FragmentTransition.getInEpicenterView((ArrayMap)localObject, paramFragmentContainerTransition, paramObject1, bool);
            if (localObject != null) {
              FragmentTransitionCompat21.getBoundsOnScreen((View)localObject, paramObject2);
            }
          }
        }
      });
      return localObject;
    }
  }
  
  @RequiresApi(21)
  private static Object configureSharedElementsReordered(ViewGroup paramViewGroup, final View paramView, final ArrayMap<String, String> paramArrayMap, FragmentContainerTransition paramFragmentContainerTransition, ArrayList<View> paramArrayList1, ArrayList<View> paramArrayList2, Object paramObject1, Object paramObject2)
  {
    Fragment localFragment1 = paramFragmentContainerTransition.lastIn;
    final Fragment localFragment2 = paramFragmentContainerTransition.firstOut;
    if (localFragment1 != null) {
      localFragment1.getView().setVisibility(0);
    }
    if ((localFragment1 == null) || (localFragment2 == null)) {
      return null;
    }
    final boolean bool = paramFragmentContainerTransition.lastInIsPop;
    Object localObject;
    ArrayMap localArrayMap2;
    final ArrayMap localArrayMap1;
    if (paramArrayMap.isEmpty())
    {
      localObject = null;
      localArrayMap2 = captureOutSharedElements(paramArrayMap, localObject, paramFragmentContainerTransition);
      localArrayMap1 = captureInSharedElements(paramArrayMap, localObject, paramFragmentContainerTransition);
      if (!paramArrayMap.isEmpty()) {
        break label138;
      }
      paramArrayMap = null;
      if (localArrayMap2 != null) {
        localArrayMap2.clear();
      }
      localObject = paramArrayMap;
      if (localArrayMap1 != null)
      {
        localArrayMap1.clear();
        localObject = paramArrayMap;
      }
    }
    for (;;)
    {
      if ((paramObject1 != null) || (paramObject2 != null) || (localObject != null)) {
        break label163;
      }
      return null;
      localObject = getSharedElementTransition(localFragment1, localFragment2, bool);
      break;
      label138:
      addSharedElementsWithMatchingNames(paramArrayList1, localArrayMap2, paramArrayMap.keySet());
      addSharedElementsWithMatchingNames(paramArrayList2, localArrayMap1, paramArrayMap.values());
    }
    label163:
    callSharedElementStartEnd(localFragment1, localFragment2, bool, localArrayMap2, true);
    if (localObject != null)
    {
      paramArrayList2.add(paramView);
      FragmentTransitionCompat21.setSharedElementTargets(localObject, paramView, paramArrayList1);
      setOutEpicenter(localObject, paramObject2, localArrayMap2, paramFragmentContainerTransition.firstOutIsPop, paramFragmentContainerTransition.firstOutTransaction);
      paramArrayList1 = new Rect();
      paramFragmentContainerTransition = getInEpicenterView(localArrayMap1, paramFragmentContainerTransition, paramObject1, bool);
      paramView = paramFragmentContainerTransition;
      paramArrayMap = paramArrayList1;
      if (paramFragmentContainerTransition != null)
      {
        FragmentTransitionCompat21.setEpicenter(paramObject1, paramArrayList1);
        paramArrayMap = paramArrayList1;
      }
    }
    for (paramView = paramFragmentContainerTransition;; paramView = null)
    {
      OneShotPreDrawListener.add(paramViewGroup, new Runnable()
      {
        public void run()
        {
          FragmentTransition.callSharedElementStartEnd(this.val$inFragment, localFragment2, bool, localArrayMap1, false);
          if (paramView != null) {
            FragmentTransitionCompat21.getBoundsOnScreen(paramView, paramArrayMap);
          }
        }
      });
      return localObject;
      paramArrayMap = null;
    }
  }
  
  @RequiresApi(21)
  private static void configureTransitionsOrdered(FragmentManagerImpl paramFragmentManagerImpl, int paramInt, FragmentContainerTransition paramFragmentContainerTransition, View paramView, ArrayMap<String, String> paramArrayMap)
  {
    ViewGroup localViewGroup = null;
    if (paramFragmentManagerImpl.mContainer.onHasView()) {
      localViewGroup = (ViewGroup)paramFragmentManagerImpl.mContainer.onFindViewById(paramInt);
    }
    if (localViewGroup == null) {}
    Fragment localFragment;
    Object localObject3;
    Object localObject1;
    ArrayList localArrayList1;
    Object localObject2;
    do
    {
      do
      {
        return;
        localFragment = paramFragmentContainerTransition.lastIn;
        localObject3 = paramFragmentContainerTransition.firstOut;
        boolean bool1 = paramFragmentContainerTransition.lastInIsPop;
        boolean bool2 = paramFragmentContainerTransition.firstOutIsPop;
        localObject1 = getEnterTransition(localFragment, bool1);
        paramFragmentManagerImpl = getExitTransition((Fragment)localObject3, bool2);
        localArrayList2 = new ArrayList();
        localArrayList1 = new ArrayList();
        localObject2 = configureSharedElementsOrdered(localViewGroup, paramView, paramArrayMap, paramFragmentContainerTransition, localArrayList2, localArrayList1, localObject1, paramFragmentManagerImpl);
      } while ((localObject1 == null) && (localObject2 == null) && (paramFragmentManagerImpl == null));
      localObject3 = configureEnteringExitingViews(paramFragmentManagerImpl, (Fragment)localObject3, localArrayList2, paramView);
      if ((localObject3 == null) || (((ArrayList)localObject3).isEmpty())) {
        paramFragmentManagerImpl = null;
      }
      FragmentTransitionCompat21.addTarget(localObject1, paramView);
      paramFragmentContainerTransition = mergeTransitions(localObject1, paramFragmentManagerImpl, localObject2, localFragment, paramFragmentContainerTransition.lastInIsPop);
    } while (paramFragmentContainerTransition == null);
    ArrayList localArrayList2 = new ArrayList();
    FragmentTransitionCompat21.scheduleRemoveTargets(paramFragmentContainerTransition, localObject1, localArrayList2, paramFragmentManagerImpl, (ArrayList)localObject3, localObject2, localArrayList1);
    scheduleTargetChange(localViewGroup, localFragment, paramView, localArrayList1, localObject1, localArrayList2, paramFragmentManagerImpl, (ArrayList)localObject3);
    FragmentTransitionCompat21.setNameOverridesOrdered(localViewGroup, localArrayList1, paramArrayMap);
    FragmentTransitionCompat21.beginDelayedTransition(localViewGroup, paramFragmentContainerTransition);
    FragmentTransitionCompat21.scheduleNameReset(localViewGroup, localArrayList1, paramArrayMap);
  }
  
  @RequiresApi(21)
  private static void configureTransitionsReordered(FragmentManagerImpl paramFragmentManagerImpl, int paramInt, FragmentContainerTransition paramFragmentContainerTransition, View paramView, ArrayMap<String, String> paramArrayMap)
  {
    ViewGroup localViewGroup = null;
    if (paramFragmentManagerImpl.mContainer.onHasView()) {
      localViewGroup = (ViewGroup)paramFragmentManagerImpl.mContainer.onFindViewById(paramInt);
    }
    if (localViewGroup == null) {}
    Object localObject4;
    ArrayList localArrayList1;
    Object localObject1;
    Object localObject2;
    ArrayList localArrayList2;
    do
    {
      boolean bool1;
      do
      {
        return;
        localObject4 = paramFragmentContainerTransition.lastIn;
        localObject3 = paramFragmentContainerTransition.firstOut;
        bool1 = paramFragmentContainerTransition.lastInIsPop;
        boolean bool2 = paramFragmentContainerTransition.firstOutIsPop;
        paramFragmentManagerImpl = new ArrayList();
        localArrayList1 = new ArrayList();
        localObject1 = getEnterTransition((Fragment)localObject4, bool1);
        localObject2 = getExitTransition((Fragment)localObject3, bool2);
        paramFragmentContainerTransition = configureSharedElementsReordered(localViewGroup, paramView, paramArrayMap, paramFragmentContainerTransition, localArrayList1, paramFragmentManagerImpl, localObject1, localObject2);
      } while ((localObject1 == null) && (paramFragmentContainerTransition == null) && (localObject2 == null));
      localArrayList2 = configureEnteringExitingViews(localObject2, (Fragment)localObject3, localArrayList1, paramView);
      paramView = configureEnteringExitingViews(localObject1, (Fragment)localObject4, paramFragmentManagerImpl, paramView);
      setViewVisibility(paramView, 4);
      localObject4 = mergeTransitions(localObject1, localObject2, paramFragmentContainerTransition, (Fragment)localObject4, bool1);
    } while (localObject4 == null);
    replaceHide(localObject2, (Fragment)localObject3, localArrayList2);
    Object localObject3 = FragmentTransitionCompat21.prepareSetNameOverridesReordered(paramFragmentManagerImpl);
    FragmentTransitionCompat21.scheduleRemoveTargets(localObject4, localObject1, paramView, localObject2, localArrayList2, paramFragmentContainerTransition, paramFragmentManagerImpl);
    FragmentTransitionCompat21.beginDelayedTransition(localViewGroup, localObject4);
    FragmentTransitionCompat21.setNameOverridesReordered(localViewGroup, localArrayList1, paramFragmentManagerImpl, (ArrayList)localObject3, paramArrayMap);
    setViewVisibility(paramView, 0);
    FragmentTransitionCompat21.swapSharedElementTargets(paramFragmentContainerTransition, localArrayList1, paramFragmentManagerImpl);
  }
  
  private static FragmentContainerTransition ensureContainer(FragmentContainerTransition paramFragmentContainerTransition, SparseArray<FragmentContainerTransition> paramSparseArray, int paramInt)
  {
    FragmentContainerTransition localFragmentContainerTransition = paramFragmentContainerTransition;
    if (paramFragmentContainerTransition == null)
    {
      localFragmentContainerTransition = new FragmentContainerTransition();
      paramSparseArray.put(paramInt, localFragmentContainerTransition);
    }
    return localFragmentContainerTransition;
  }
  
  private static String findKeyForValue(ArrayMap<String, String> paramArrayMap, String paramString)
  {
    int j = paramArrayMap.size();
    int i = 0;
    while (i < j)
    {
      if (paramString.equals(paramArrayMap.valueAt(i))) {
        return (String)paramArrayMap.keyAt(i);
      }
      i += 1;
    }
    return null;
  }
  
  @RequiresApi(21)
  private static Object getEnterTransition(Fragment paramFragment, boolean paramBoolean)
  {
    if (paramFragment == null) {
      return null;
    }
    if (paramBoolean) {}
    for (paramFragment = paramFragment.getReenterTransition();; paramFragment = paramFragment.getEnterTransition()) {
      return FragmentTransitionCompat21.cloneTransition(paramFragment);
    }
  }
  
  @RequiresApi(21)
  private static Object getExitTransition(Fragment paramFragment, boolean paramBoolean)
  {
    if (paramFragment == null) {
      return null;
    }
    if (paramBoolean) {}
    for (paramFragment = paramFragment.getReturnTransition();; paramFragment = paramFragment.getExitTransition()) {
      return FragmentTransitionCompat21.cloneTransition(paramFragment);
    }
  }
  
  private static View getInEpicenterView(ArrayMap<String, View> paramArrayMap, FragmentContainerTransition paramFragmentContainerTransition, Object paramObject, boolean paramBoolean)
  {
    paramFragmentContainerTransition = paramFragmentContainerTransition.lastInTransaction;
    if ((paramObject != null) && (paramArrayMap != null) && (paramFragmentContainerTransition.mSharedElementSourceNames != null) && (!paramFragmentContainerTransition.mSharedElementSourceNames.isEmpty()))
    {
      if (paramBoolean) {}
      for (paramFragmentContainerTransition = (String)paramFragmentContainerTransition.mSharedElementSourceNames.get(0);; paramFragmentContainerTransition = (String)paramFragmentContainerTransition.mSharedElementTargetNames.get(0)) {
        return (View)paramArrayMap.get(paramFragmentContainerTransition);
      }
    }
    return null;
  }
  
  @RequiresApi(21)
  private static Object getSharedElementTransition(Fragment paramFragment1, Fragment paramFragment2, boolean paramBoolean)
  {
    if ((paramFragment1 == null) || (paramFragment2 == null)) {
      return null;
    }
    if (paramBoolean) {}
    for (paramFragment1 = paramFragment2.getSharedElementReturnTransition();; paramFragment1 = paramFragment1.getSharedElementEnterTransition()) {
      return FragmentTransitionCompat21.wrapTransitionInSet(FragmentTransitionCompat21.cloneTransition(paramFragment1));
    }
  }
  
  @RequiresApi(21)
  private static Object mergeTransitions(Object paramObject1, Object paramObject2, Object paramObject3, Fragment paramFragment, boolean paramBoolean)
  {
    boolean bool2 = true;
    boolean bool1 = bool2;
    if (paramObject1 != null)
    {
      bool1 = bool2;
      if (paramObject2 != null)
      {
        bool1 = bool2;
        if (paramFragment != null) {
          if (!paramBoolean) {
            break label50;
          }
        }
      }
    }
    label50:
    for (bool1 = paramFragment.getAllowReturnTransitionOverlap(); bool1; bool1 = paramFragment.getAllowEnterTransitionOverlap()) {
      return FragmentTransitionCompat21.mergeTransitionsTogether(paramObject2, paramObject1, paramObject3);
    }
    return FragmentTransitionCompat21.mergeTransitionsInSequence(paramObject2, paramObject1, paramObject3);
  }
  
  @RequiresApi(21)
  private static void replaceHide(Object paramObject, Fragment paramFragment, ArrayList<View> paramArrayList)
  {
    if ((paramFragment != null) && (paramObject != null) && (paramFragment.mAdded) && (paramFragment.mHidden) && (paramFragment.mHiddenChanged))
    {
      paramFragment.setHideReplaced(true);
      FragmentTransitionCompat21.scheduleHideFragmentView(paramObject, paramFragment.getView(), paramArrayList);
      OneShotPreDrawListener.add(paramFragment.mContainer, new Runnable()
      {
        public void run()
        {
          FragmentTransition.setViewVisibility(this.val$exitingViews, 4);
        }
      });
    }
  }
  
  private static void retainValues(ArrayMap<String, String> paramArrayMap, ArrayMap<String, View> paramArrayMap1)
  {
    int i = paramArrayMap.size() - 1;
    while (i >= 0)
    {
      if (!paramArrayMap1.containsKey((String)paramArrayMap.valueAt(i))) {
        paramArrayMap.removeAt(i);
      }
      i -= 1;
    }
  }
  
  @RequiresApi(21)
  private static void scheduleTargetChange(ViewGroup paramViewGroup, final Fragment paramFragment, final View paramView, final ArrayList<View> paramArrayList1, Object paramObject1, final ArrayList<View> paramArrayList2, final Object paramObject2, final ArrayList<View> paramArrayList3)
  {
    OneShotPreDrawListener.add(paramViewGroup, new Runnable()
    {
      public void run()
      {
        ArrayList localArrayList;
        if (this.val$enterTransition != null)
        {
          FragmentTransitionCompat21.removeTarget(this.val$enterTransition, paramView);
          localArrayList = FragmentTransition.configureEnteringExitingViews(this.val$enterTransition, paramFragment, paramArrayList1, paramView);
          paramArrayList2.addAll(localArrayList);
        }
        if (paramArrayList3 != null)
        {
          if (paramObject2 != null)
          {
            localArrayList = new ArrayList();
            localArrayList.add(paramView);
            FragmentTransitionCompat21.replaceTargets(paramObject2, paramArrayList3, localArrayList);
          }
          paramArrayList3.clear();
          paramArrayList3.add(paramView);
        }
      }
    });
  }
  
  @RequiresApi(21)
  private static void setOutEpicenter(Object paramObject1, Object paramObject2, ArrayMap<String, View> paramArrayMap, boolean paramBoolean, BackStackRecord paramBackStackRecord)
  {
    if ((paramBackStackRecord.mSharedElementSourceNames != null) && (!paramBackStackRecord.mSharedElementSourceNames.isEmpty())) {
      if (!paramBoolean) {
        break label62;
      }
    }
    label62:
    for (paramBackStackRecord = (String)paramBackStackRecord.mSharedElementTargetNames.get(0);; paramBackStackRecord = (String)paramBackStackRecord.mSharedElementSourceNames.get(0))
    {
      paramArrayMap = (View)paramArrayMap.get(paramBackStackRecord);
      FragmentTransitionCompat21.setEpicenter(paramObject1, paramArrayMap);
      if (paramObject2 != null) {
        FragmentTransitionCompat21.setEpicenter(paramObject2, paramArrayMap);
      }
      return;
    }
  }
  
  private static void setViewVisibility(ArrayList<View> paramArrayList, int paramInt)
  {
    if (paramArrayList == null) {}
    for (;;)
    {
      return;
      int i = paramArrayList.size() - 1;
      while (i >= 0)
      {
        ((View)paramArrayList.get(i)).setVisibility(paramInt);
        i -= 1;
      }
    }
  }
  
  static void startTransitions(FragmentManagerImpl paramFragmentManagerImpl, ArrayList<BackStackRecord> paramArrayList, ArrayList<Boolean> paramArrayList1, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    if (paramFragmentManagerImpl.mCurState < 1) {}
    SparseArray localSparseArray;
    do
    {
      do
      {
        return;
      } while (Build.VERSION.SDK_INT < 21);
      localSparseArray = new SparseArray();
      i = paramInt1;
      if (i < paramInt2)
      {
        localObject = (BackStackRecord)paramArrayList.get(i);
        if (((Boolean)paramArrayList1.get(i)).booleanValue()) {
          calculatePopFragments((BackStackRecord)localObject, localSparseArray, paramBoolean);
        }
        for (;;)
        {
          i += 1;
          break;
          calculateFragments((BackStackRecord)localObject, localSparseArray, paramBoolean);
        }
      }
    } while (localSparseArray.size() == 0);
    Object localObject = new View(paramFragmentManagerImpl.mHost.getContext());
    int j = localSparseArray.size();
    int i = 0;
    label126:
    int k;
    ArrayMap localArrayMap;
    FragmentContainerTransition localFragmentContainerTransition;
    if (i < j)
    {
      k = localSparseArray.keyAt(i);
      localArrayMap = calculateNameOverrides(k, paramArrayList, paramArrayList1, paramInt1, paramInt2);
      localFragmentContainerTransition = (FragmentContainerTransition)localSparseArray.valueAt(i);
      if (!paramBoolean) {
        break label192;
      }
      configureTransitionsReordered(paramFragmentManagerImpl, k, localFragmentContainerTransition, (View)localObject, localArrayMap);
    }
    for (;;)
    {
      i += 1;
      break label126;
      break;
      label192:
      configureTransitionsOrdered(paramFragmentManagerImpl, k, localFragmentContainerTransition, (View)localObject, localArrayMap);
    }
  }
  
  static class FragmentContainerTransition
  {
    public Fragment firstOut;
    public boolean firstOutIsPop;
    public BackStackRecord firstOutTransaction;
    public Fragment lastIn;
    public boolean lastInIsPop;
    public BackStackRecord lastInTransaction;
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v4/app/FragmentTransition.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */