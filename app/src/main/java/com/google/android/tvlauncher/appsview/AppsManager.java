package com.google.android.tvlauncher.appsview;

import android.content.BroadcastReceiver;
import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.LocaleList;
import android.text.TextUtils;
import android.util.Pair;
import com.google.android.tvlauncher.util.Partner;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class AppsManager
  implements PackageChangedReceiver.Listener, InstallingLaunchItemListener
{
  private static final String APP_STORE_PACKAGE_NAME = "com.android.vending";
  private static final boolean DEBUG = false;
  private static final String GAME_STORE_PACKAGE_NAME = "com.google.android.play.games";
  private static final String TAG = "AppsManager";
  private static AppsManager sAppsManager;
  private final List<LaunchItem> mAllLaunchItems = new LinkedList();
  private LaunchItem mAppStore;
  private AppsOrderManager mAppsOrderManager;
  private final List<AppsViewChangeListener> mAppsViewListeners = new LinkedList();
  private Context mContext;
  private LocaleList mCurrentLocales;
  private final BroadcastReceiver mExternalAppsUpdateReceiver;
  private final FavoriteItemsManager mFavoriteItemsManager;
  private LaunchItem mGameStore;
  private final List<LaunchItem> mInstallingLaunchItems = new LinkedList();
  private boolean mItemsLoaded;
  private final List<LaunchItem> mOemItems = new LinkedList();
  private final PackageChangedReceiver mPackageChangedReceiver;
  private int mReceiversRegisteredRefCount;
  private AsyncTask mRefreshTask;
  private SearchPackageChangeListener mSearchChangeListener;
  private String mSearchPackageName;
  private HashSet<String> mShowCaseAppSet;
  
  private AppsManager(Context paramContext)
  {
    this.mContext = paramContext.getApplicationContext();
    this.mCurrentLocales = this.mContext.getResources().getConfiguration().getLocales();
    this.mPackageChangedReceiver = new PackageChangedReceiver(this);
    this.mExternalAppsUpdateReceiver = new ExternalAppsUpdateReceiver();
    String[] arrayOfString = Partner.get(this.mContext).getShowcasedAppsList();
    if (arrayOfString != null) {}
    for (this.mShowCaseAppSet = new HashSet(Arrays.asList(arrayOfString));; this.mShowCaseAppSet = new HashSet())
    {
      this.mFavoriteItemsManager = new FavoriteItemsManager(this.mContext);
      registerAppsViewChangeListener(this.mFavoriteItemsManager);
      this.mAppsOrderManager = AppsOrderManager.getInstance(paramContext);
      this.mContext.registerComponentCallbacks(new ComponentCallbacks()
      {
        public void onConfigurationChanged(Configuration paramAnonymousConfiguration)
        {
          paramAnonymousConfiguration = paramAnonymousConfiguration.getLocales();
          if ((AppsManager.this.mCurrentLocales == null) || (!AppsManager.this.mCurrentLocales.equals(paramAnonymousConfiguration)))
          {
            AppsManager.access$102(AppsManager.this, false);
            AppsManager.this.mOemItems.clear();
            AppsManager.this.mAllLaunchItems.clear();
            AppsManager.access$002(AppsManager.this, paramAnonymousConfiguration);
          }
        }
        
        public void onLowMemory() {}
      });
      return;
    }
  }
  
  private void addItemToAppropriateArea(LaunchItem paramLaunchItem)
  {
    if (!this.mInstallingLaunchItems.contains(paramLaunchItem))
    {
      if (!checkIfOem(paramLaunchItem)) {
        break label48;
      }
      this.mOemItems.add(paramLaunchItem);
      if (!checkIfAppStore(paramLaunchItem.getPackageName())) {
        break label62;
      }
      this.mAppStore = paramLaunchItem;
    }
    label48:
    label62:
    while (!checkIfGameStore(paramLaunchItem.getPackageName()))
    {
      return;
      this.mAllLaunchItems.add(paramLaunchItem);
      break;
    }
    this.mGameStore = paramLaunchItem;
  }
  
  private void addOrUpdateInstallingLaunchItem(LaunchItem paramLaunchItem)
  {
    if (paramLaunchItem == null) {}
    for (;;)
    {
      return;
      Object localObject = paramLaunchItem.getPackageName();
      ArrayList localArrayList = new ArrayList();
      getLaunchItems(this.mInstallingLaunchItems, localArrayList, (String)localObject, true);
      getLaunchItems(this.mAllLaunchItems, localArrayList, (String)localObject, true);
      localObject = localArrayList.iterator();
      while (((Iterator)localObject).hasNext()) {
        ((LaunchItem)((Iterator)localObject).next()).setInstallationState(paramLaunchItem);
      }
      if (localArrayList.isEmpty()) {
        localArrayList.add(paramLaunchItem);
      }
      this.mInstallingLaunchItems.addAll(localArrayList);
      paramLaunchItem = this.mAppsViewListeners.iterator();
      while (paramLaunchItem.hasNext()) {
        ((AppsViewChangeListener)paramLaunchItem.next()).onLaunchItemsAddedOrUpdated(localArrayList);
      }
    }
  }
  
  private void addOrUpdatePackage(String paramString)
  {
    ArrayList localArrayList = new ArrayList();
    findAndRemoveExistingLaunchItems(paramString, localArrayList);
    paramString = createLaunchItems(paramString, localArrayList);
    if (!paramString.isEmpty())
    {
      Iterator localIterator = paramString.iterator();
      while (localIterator.hasNext()) {
        addItemToAppropriateArea((LaunchItem)localIterator.next());
      }
      localIterator = this.mAppsViewListeners.iterator();
      while (localIterator.hasNext()) {
        ((AppsViewChangeListener)localIterator.next()).onLaunchItemsAddedOrUpdated(paramString);
      }
    }
    notifyItemsRemoved(localArrayList);
  }
  
  private void checkForSearchChanges(String paramString)
  {
    if ((this.mSearchChangeListener != null) && (paramString != null) && (paramString.equalsIgnoreCase(this.mSearchPackageName))) {
      this.mSearchChangeListener.onSearchPackageChanged();
    }
  }
  
  static boolean checkIfAppStore(String paramString)
  {
    return "com.android.vending".equalsIgnoreCase(paramString);
  }
  
  static boolean checkIfGameStore(String paramString)
  {
    return "com.google.android.play.games".equalsIgnoreCase(paramString);
  }
  
  private boolean checkIfOem(LaunchItem paramLaunchItem)
  {
    return (this.mShowCaseAppSet != null) && (this.mShowCaseAppSet.size() != 0) && (this.mShowCaseAppSet.contains(paramLaunchItem.getPackageName()));
  }
  
  private ArrayList<LaunchItem> createLaunchItems(String paramString, ArrayList<LaunchItem> paramArrayList)
  {
    Object localObject = paramArrayList;
    if (paramArrayList == null) {
      localObject = new ArrayList();
    }
    paramArrayList = new Intent("android.intent.action.MAIN");
    paramArrayList.setPackage(paramString).addCategory("android.intent.category.LEANBACK_LAUNCHER");
    paramString = new ArrayList();
    paramArrayList = this.mContext.getPackageManager().queryIntentActivities(paramArrayList, 129);
    Iterator localIterator1 = paramArrayList.iterator();
    while (localIterator1.hasNext())
    {
      ResolveInfo localResolveInfo = (ResolveInfo)localIterator1.next();
      if (localResolveInfo.activityInfo != null)
      {
        Iterator localIterator2 = ((ArrayList)localObject).iterator();
        for (int i = 0; localIterator2.hasNext(); i = 1)
        {
          label109:
          LaunchItem localLaunchItem = (LaunchItem)localIterator2.next();
          if ((!localLaunchItem.isInitialInstall()) && (!localLaunchItem.hasSamePackageName(localResolveInfo))) {
            break label109;
          }
          paramString.add(localLaunchItem.set(this.mContext, localResolveInfo));
          localIterator2.remove();
        }
        if (i != 0) {
          localIterator1.remove();
        }
      }
    }
    paramArrayList = paramArrayList.iterator();
    localObject = ((ArrayList)localObject).iterator();
    while ((paramArrayList.hasNext()) && (((Iterator)localObject).hasNext()))
    {
      paramString.add(((LaunchItem)((Iterator)localObject).next()).set(this.mContext, (ResolveInfo)paramArrayList.next()));
      ((Iterator)localObject).remove();
    }
    while (paramArrayList.hasNext()) {
      paramString.add(new LaunchItem(this.mContext, (ResolveInfo)paramArrayList.next()));
    }
    return paramString;
  }
  
  private void findAndRemoveExistingLaunchItems(String paramString, ArrayList<LaunchItem> paramArrayList)
  {
    getLaunchItems(this.mInstallingLaunchItems, paramArrayList, paramString, true);
    getLaunchItems(this.mAllLaunchItems, paramArrayList, paramString, true);
    getLaunchItems(this.mOemItems, paramArrayList, paramString, true);
  }
  
  public static AppsManager getInstance(Context paramContext)
  {
    if (sAppsManager == null) {
      sAppsManager = new AppsManager(paramContext);
    }
    return sAppsManager;
  }
  
  private ArrayList<LaunchItem> getLaunchItems(List<LaunchItem> paramList, ArrayList<LaunchItem> paramArrayList, String paramString, boolean paramBoolean)
  {
    Object localObject = paramArrayList;
    if (paramArrayList == null) {
      localObject = new ArrayList();
    }
    paramList = paramList.iterator();
    while (paramList.hasNext())
    {
      paramArrayList = (LaunchItem)paramList.next();
      if (TextUtils.equals(paramString, paramArrayList.getPackageName()))
      {
        ((ArrayList)localObject).add(paramArrayList);
        if (paramBoolean) {
          paramList.remove();
        }
      }
    }
    return (ArrayList<LaunchItem>)localObject;
  }
  
  private ArrayList<LaunchItem> getLaunchItems(boolean paramBoolean1, boolean paramBoolean2)
  {
    ArrayList localArrayList = new ArrayList();
    getLaunchItems(this.mInstallingLaunchItems, localArrayList, paramBoolean1, paramBoolean2);
    getLaunchItems(this.mAllLaunchItems, localArrayList, paramBoolean1, paramBoolean2);
    this.mAppsOrderManager.orderGivenItems(localArrayList);
    return localArrayList;
  }
  
  private void getLaunchItems(List<LaunchItem> paramList1, List<LaunchItem> paramList2, boolean paramBoolean1, boolean paramBoolean2)
  {
    if ((paramBoolean1) && (paramBoolean2)) {}
    for (int i = 1;; i = 0)
    {
      paramList1 = paramList1.iterator();
      while (paramList1.hasNext())
      {
        LaunchItem localLaunchItem = (LaunchItem)paramList1.next();
        if ((i != 0) || (paramBoolean2 == localLaunchItem.isGame())) {
          paramList2.add(localLaunchItem);
        }
      }
    }
  }
  
  private void notifyItemsRemoved(ArrayList<LaunchItem> paramArrayList)
  {
    if (!paramArrayList.isEmpty())
    {
      Iterator localIterator = this.mAppsViewListeners.iterator();
      while (localIterator.hasNext()) {
        ((AppsViewChangeListener)localIterator.next()).onLaunchItemsRemoved(paramArrayList);
      }
      this.mAppsOrderManager.removeItems(paramArrayList);
    }
  }
  
  private void removeInstallingLaunchItem(LaunchItem paramLaunchItem, boolean paramBoolean)
  {
    if (paramLaunchItem == null) {}
    while (paramBoolean) {
      return;
    }
    addOrUpdatePackage(paramLaunchItem.getPackageName());
  }
  
  private void removePackage(String paramString)
  {
    ArrayList localArrayList = new ArrayList();
    findAndRemoveExistingLaunchItems(paramString, localArrayList);
    notifyItemsRemoved(localArrayList);
    checkForSearchChanges(paramString);
  }
  
  public void addToFavorites(LaunchItem paramLaunchItem)
  {
    this.mFavoriteItemsManager.userAddToFavorites(paramLaunchItem);
  }
  
  public void addToFavorites(String paramString)
  {
    this.mFavoriteItemsManager.userAddToFavorites(getLaunchItem(paramString));
  }
  
  public boolean areItemsLoaded()
  {
    return this.mItemsLoaded;
  }
  
  ArrayList<LaunchItem> getAllLaunchItemsWithoutSorting()
  {
    ArrayList localArrayList = new ArrayList();
    getLaunchItems(this.mOemItems, localArrayList, true, true);
    getLaunchItems(this.mInstallingLaunchItems, localArrayList, true, true);
    getLaunchItems(this.mAllLaunchItems, localArrayList, true, true);
    return localArrayList;
  }
  
  public ArrayList<LaunchItem> getAllNonFavoriteLaunchItems()
  {
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = this.mOemItems.iterator();
    LaunchItem localLaunchItem;
    while (localIterator.hasNext())
    {
      localLaunchItem = (LaunchItem)localIterator.next();
      if (!isFavorite(localLaunchItem)) {
        localArrayList.add(localLaunchItem);
      }
    }
    localIterator = getAppLaunchItems().iterator();
    while (localIterator.hasNext())
    {
      localLaunchItem = (LaunchItem)localIterator.next();
      if (!isFavorite(localLaunchItem)) {
        localArrayList.add(localLaunchItem);
      }
    }
    localIterator = getGameLaunchItems().iterator();
    while (localIterator.hasNext())
    {
      localLaunchItem = (LaunchItem)localIterator.next();
      if (!isFavorite(localLaunchItem)) {
        localArrayList.add(localLaunchItem);
      }
    }
    return localArrayList;
  }
  
  ArrayList<LaunchItem> getAppLaunchItems()
  {
    return getLaunchItems(true, false);
  }
  
  public LaunchItem getAppStoreLaunchItem()
  {
    return this.mAppStore;
  }
  
  ArrayList<LaunchItem> getGameLaunchItems()
  {
    return getLaunchItems(false, true);
  }
  
  public LaunchItem getGameStoreLaunchItem()
  {
    return this.mGameStore;
  }
  
  public List<LaunchItem> getHomeScreenItems()
  {
    return this.mFavoriteItemsManager.getFavoriteItems();
  }
  
  LaunchItem getInstallingLaunchItem(String paramString)
  {
    Iterator localIterator = this.mInstallingLaunchItems.iterator();
    while (localIterator.hasNext())
    {
      LaunchItem localLaunchItem = (LaunchItem)localIterator.next();
      if (localLaunchItem.getPackageName().equalsIgnoreCase(paramString)) {
        return localLaunchItem;
      }
    }
    return null;
  }
  
  LaunchItem getLaunchItem(String paramString)
  {
    Iterator localIterator = this.mOemItems.iterator();
    LaunchItem localLaunchItem;
    while (localIterator.hasNext())
    {
      localLaunchItem = (LaunchItem)localIterator.next();
      if (localLaunchItem.getPackageName().equalsIgnoreCase(paramString)) {
        return localLaunchItem;
      }
    }
    localIterator = this.mInstallingLaunchItems.iterator();
    while (localIterator.hasNext())
    {
      localLaunchItem = (LaunchItem)localIterator.next();
      if (localLaunchItem.getPackageName().equalsIgnoreCase(paramString)) {
        return localLaunchItem;
      }
    }
    localIterator = this.mAllLaunchItems.iterator();
    while (localIterator.hasNext())
    {
      localLaunchItem = (LaunchItem)localIterator.next();
      if (localLaunchItem.getPackageName().equalsIgnoreCase(paramString)) {
        return localLaunchItem;
      }
    }
    return null;
  }
  
  ArrayList<LaunchItem> getOemLaunchItems()
  {
    return new ArrayList(this.mOemItems);
  }
  
  int getOrderedPosition(LaunchItem paramLaunchItem)
  {
    ArrayList localArrayList;
    if (paramLaunchItem.isGame()) {
      localArrayList = getGameLaunchItems();
    }
    for (;;)
    {
      return localArrayList.indexOf(paramLaunchItem);
      if (checkIfOem(paramLaunchItem)) {
        localArrayList = getOemLaunchItems();
      } else {
        localArrayList = getAppLaunchItems();
      }
    }
  }
  
  public boolean isFavorite(LaunchItem paramLaunchItem)
  {
    return this.mFavoriteItemsManager.isFavorite(paramLaunchItem);
  }
  
  boolean isFavoritesFull()
  {
    return this.mFavoriteItemsManager.isFull();
  }
  
  public boolean isOnlyApp(LaunchItem paramLaunchItem)
  {
    ArrayList localArrayList = getAppLaunchItems();
    return (localArrayList.size() == 1) && (localArrayList.contains(paramLaunchItem));
  }
  
  public boolean isOnlyFavorite(LaunchItem paramLaunchItem)
  {
    return this.mFavoriteItemsManager.isOnlyFavorite(paramLaunchItem);
  }
  
  public boolean isOnlyGame(LaunchItem paramLaunchItem)
  {
    ArrayList localArrayList = getGameLaunchItems();
    return (localArrayList.size() == 1) && (localArrayList.contains(paramLaunchItem));
  }
  
  List<LaunchItem> makeFirstTimeFavorites(Set<String> paramSet)
  {
    Object localObject = getAllLaunchItemsWithoutSorting();
    ArrayList localArrayList = new ArrayList(paramSet.size());
    localObject = ((List)localObject).iterator();
    while (((Iterator)localObject).hasNext())
    {
      LaunchItem localLaunchItem = (LaunchItem)((Iterator)localObject).next();
      String str = localLaunchItem.getPackageName();
      if (paramSet.contains(str))
      {
        localArrayList.add(localLaunchItem);
        paramSet.remove(str);
      }
    }
    return localArrayList;
  }
  
  void onAppOrderChange(ArrayList<LaunchItem> paramArrayList, boolean paramBoolean, Pair<Integer, Integer> paramPair)
  {
    this.mAppsOrderManager.userChangedOrder(paramArrayList);
    this.mAppsOrderManager.orderGivenItems(this.mAllLaunchItems);
    Iterator localIterator = this.mAppsViewListeners.iterator();
    while (localIterator.hasNext()) {
      ((AppsViewChangeListener)localIterator.next()).onEditModeItemOrderChange(paramArrayList, paramBoolean, paramPair);
    }
  }
  
  public void onInstallingLaunchItemAdded(LaunchItem paramLaunchItem)
  {
    addOrUpdateInstallingLaunchItem(paramLaunchItem);
  }
  
  public void onInstallingLaunchItemChanged(LaunchItem paramLaunchItem)
  {
    addOrUpdateInstallingLaunchItem(paramLaunchItem);
  }
  
  public void onInstallingLaunchItemRemoved(LaunchItem paramLaunchItem, boolean paramBoolean)
  {
    removeInstallingLaunchItem(paramLaunchItem, paramBoolean);
  }
  
  public void onPackageAdded(String paramString)
  {
    addOrUpdatePackage(paramString);
  }
  
  public void onPackageChanged(String paramString)
  {
    addOrUpdatePackage(paramString);
  }
  
  public void onPackageFullyRemoved(String paramString)
  {
    removePackage(paramString);
  }
  
  public void onPackageRemoved(String paramString)
  {
    removePackage(paramString);
  }
  
  public void onPackageReplaced(String paramString)
  {
    addOrUpdatePackage(paramString);
  }
  
  public void refreshLaunchItems()
  {
    this.mItemsLoaded = false;
    if (this.mRefreshTask != null) {
      this.mRefreshTask.cancel(true);
    }
    this.mRefreshTask = new CreateLaunchItemsListTask(null).execute(new Void[0]);
  }
  
  public void registerAppsViewChangeListener(AppsViewChangeListener paramAppsViewChangeListener)
  {
    if (!this.mAppsViewListeners.contains(paramAppsViewChangeListener)) {
      this.mAppsViewListeners.add(paramAppsViewChangeListener);
    }
  }
  
  public void registerUpdateListeners()
  {
    int i = this.mReceiversRegisteredRefCount;
    this.mReceiversRegisteredRefCount = (i + 1);
    if (i == 0)
    {
      this.mContext.registerReceiver(this.mPackageChangedReceiver, PackageChangedReceiver.getIntentFilter());
      this.mContext.registerReceiver(this.mExternalAppsUpdateReceiver, ExternalAppsUpdateReceiver.getIntentFilter());
    }
  }
  
  public void removeFromFavorites(LaunchItem paramLaunchItem)
  {
    this.mFavoriteItemsManager.userRemoveFromFavorites(paramLaunchItem);
  }
  
  void saveOrderSnapshot(List<LaunchItem> paramList)
  {
    this.mAppsOrderManager.saveOrderSnapshot(paramList);
  }
  
  public void setHomeScreenItemsChangeListener(HomeScreenItemsChangeListener paramHomeScreenItemsChangeListener)
  {
    this.mFavoriteItemsManager.setHomeScreenItemsChangeListener(paramHomeScreenItemsChangeListener);
  }
  
  public void setSearchPackageChangeListener(SearchPackageChangeListener paramSearchPackageChangeListener, String paramString)
  {
    this.mSearchChangeListener = paramSearchPackageChangeListener;
    if (this.mSearchChangeListener != null)
    {
      this.mSearchPackageName = paramString;
      return;
    }
    this.mSearchPackageName = null;
  }
  
  public void swapFavoriteAppOrder(LaunchItem paramLaunchItem1, LaunchItem paramLaunchItem2)
  {
    this.mFavoriteItemsManager.swapAppOrder(paramLaunchItem1, paramLaunchItem2);
  }
  
  public void unregisterAppsViewChangeListener(AppsViewChangeListener paramAppsViewChangeListener)
  {
    this.mAppsViewListeners.remove(paramAppsViewChangeListener);
  }
  
  void unregisterUpdateListeners()
  {
    int i = this.mReceiversRegisteredRefCount - 1;
    this.mReceiversRegisteredRefCount = i;
    if (i == 0)
    {
      this.mContext.unregisterReceiver(this.mPackageChangedReceiver);
      this.mContext.unregisterReceiver(this.mExternalAppsUpdateReceiver);
    }
  }
  
  public static abstract interface AppsViewChangeListener
  {
    public abstract void onEditModeItemOrderChange(ArrayList<LaunchItem> paramArrayList, boolean paramBoolean, Pair<Integer, Integer> paramPair);
    
    public abstract void onLaunchItemsAddedOrUpdated(ArrayList<LaunchItem> paramArrayList);
    
    public abstract void onLaunchItemsLoaded();
    
    public abstract void onLaunchItemsRemoved(ArrayList<LaunchItem> paramArrayList);
  }
  
  private class CreateLaunchItemsListTask
    extends AsyncTask<Void, Void, List<LaunchItem>>
  {
    private CreateLaunchItemsListTask() {}
    
    protected List<LaunchItem> doInBackground(Void... paramVarArgs)
    {
      paramVarArgs = new Intent("android.intent.action.MAIN");
      paramVarArgs.addCategory("android.intent.category.LEANBACK_LAUNCHER");
      LinkedList localLinkedList = new LinkedList();
      Object localObject = AppsManager.this.mContext.getPackageManager();
      if (isCancelled())
      {
        paramVarArgs = null;
        return paramVarArgs;
      }
      localObject = ((PackageManager)localObject).queryIntentActivities(paramVarArgs, 129);
      int i = 0;
      for (;;)
      {
        paramVarArgs = localLinkedList;
        if (i >= ((List)localObject).size()) {
          break;
        }
        paramVarArgs = localLinkedList;
        if (isCancelled()) {
          break;
        }
        paramVarArgs = (ResolveInfo)((List)localObject).get(i);
        if (paramVarArgs.activityInfo != null) {
          localLinkedList.add(new LaunchItem(AppsManager.this.mContext, paramVarArgs));
        }
        i += 1;
      }
    }
    
    protected void onPostExecute(List<LaunchItem> paramList)
    {
      AppsManager.this.mAllLaunchItems.clear();
      AppsManager.this.mOemItems.clear();
      paramList = paramList.iterator();
      while (paramList.hasNext())
      {
        LaunchItem localLaunchItem = (LaunchItem)paramList.next();
        AppsManager.this.addItemToAppropriateArea(localLaunchItem);
      }
      AppsManager.this.mAppsOrderManager.orderGivenItems(AppsManager.this.mAllLaunchItems);
      AppsManager.access$102(AppsManager.this, true);
      paramList = AppsManager.this.mAppsViewListeners.iterator();
      while (paramList.hasNext()) {
        ((AppsManager.AppsViewChangeListener)paramList.next()).onLaunchItemsLoaded();
      }
      AppsManager.access$902(AppsManager.this, null);
    }
  }
  
  public static abstract interface HomeScreenItemsChangeListener
  {
    public abstract void onHomeScreenItemsChanged(List<LaunchItem> paramList);
    
    public abstract void onHomeScreenItemsLoaded();
    
    public abstract void onHomeScreenItemsSwapped(int paramInt1, int paramInt2);
  }
  
  public static abstract interface SearchPackageChangeListener
  {
    public abstract void onSearchPackageChanged();
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/appsview/AppsManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */