package com.google.android.tvlauncher.appsview;

import android.content.BroadcastReceiver;
import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.LocaleList;
import android.text.TextUtils;
import android.util.Pair;

import com.google.android.tvlauncher.util.Partner;
import com.google.android.tvlauncher.util.porting.Edited;
import com.google.android.tvlauncher.util.porting.Reason;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class AppsManager
        implements PackageChangedReceiver.Listener, InstallingLaunchItemListener {
    private static final String APP_STORE_PACKAGE_NAME = "com.android.vending";
    private static final boolean DEBUG = false;
    private static final String GAME_STORE_PACKAGE_NAME = "com.google.android.play.games";
    private static final String TAG = "AppsManager";
    private static AppsManager sAppsManager;
    private final List<LaunchItem> mAllLaunchItems = new LinkedList<>();
    private LaunchItem mAppStore;
    private AppsOrderManager mAppsOrderManager;
    private final List<AppsViewChangeListener> mAppsViewListeners = new LinkedList<>();
    private Context mContext;
    private Locale mCurrentLocales; // LocaleList
    private final BroadcastReceiver mExternalAppsUpdateReceiver;
    private final FavoriteItemsManager mFavoriteItemsManager;
    private LaunchItem mGameStore;
    private final List<LaunchItem> mInstallingLaunchItems = new LinkedList<>();
    private boolean mItemsLoaded;
    private final List<LaunchItem> mOemItems = new LinkedList<>();
    private final PackageChangedReceiver mPackageChangedReceiver;
    private int mReceiversRegisteredRefCount;
    private AsyncTask mRefreshTask;
    private SearchPackageChangeListener mSearchChangeListener;
    private String mSearchPackageName;
    private HashSet<String> mShowCaseAppSet;

    private AppsManager(Context paramContext) {
        this.mContext = paramContext.getApplicationContext();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            this.mCurrentLocales = this.mContext.getResources().getConfiguration().getLocales().get(0);
        } else {
            this.mCurrentLocales = mContext.getResources().getConfiguration().locale;
        }

        this.mPackageChangedReceiver = new PackageChangedReceiver(this);
        this.mExternalAppsUpdateReceiver = new ExternalAppsUpdateReceiver();
        @Edited(reason = Reason.IF_ELSE_DECOMPILE_ERROR)
        String[] arrayOfString = Partner.get(this.mContext).getShowcasedAppsList();
        this.mShowCaseAppSet = (arrayOfString != null ? new HashSet<>(Arrays.asList(arrayOfString)) : new HashSet<String>());
        this.mFavoriteItemsManager = new FavoriteItemsManager(this.mContext);
        registerAppsViewChangeListener(this.mFavoriteItemsManager);
        this.mAppsOrderManager = AppsOrderManager.getInstance(paramContext);
        this.mContext.registerComponentCallbacks(new ComponentCallbacks() {
            public void onConfigurationChanged(Configuration paramAnonymousConfiguration) {
                @Edited(reason = Reason.VARIABLE_REUSAGE)
                Locale locales = null;

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    locales = paramAnonymousConfiguration.getLocales().get(0);
                } else {
                    locales = mContext.getResources().getConfiguration().locale;
                }

                if ((AppsManager.this.mCurrentLocales == null) || (!AppsManager.this.mCurrentLocales.equals(locales))) {
                    // AppsManager.access$102(AppsManager.this, false); // todo

                    AppsManager.this.mItemsLoaded = false;
                    AppsManager.this.mOemItems.clear();
                    AppsManager.this.mAllLaunchItems.clear();
                    AppsManager.this.mCurrentLocales = locales; // todo

                    // todo AppsManager.access$002(AppsManager.this, locales);
                }
            }

            public void onLowMemory() {
            }
        });

    }


    @Edited(reason = Reason.IF_ELSE_DECOMPILE_ERROR)
    private void addItemToAppropriateArea(LaunchItem paramLaunchItem) {
        if (!this.mInstallingLaunchItems.contains(paramLaunchItem)) {
            if (checkIfOem(paramLaunchItem)) {
                this.mOemItems.add(paramLaunchItem); // todo
            } else if (checkIfGameStore(paramLaunchItem.getPackageName())) {
                this.mGameStore = paramLaunchItem;
            } else if (checkIfAppStore(paramLaunchItem.getPackageName())) {
                this.mAppStore = paramLaunchItem;
            } else {
                this.mAllLaunchItems.add(paramLaunchItem);
            }

        }
    }

    @Edited(reason = Reason.IF_ELSE_DECOMPILE_ERROR)
    private void addOrUpdateInstallingLaunchItem(LaunchItem paramLaunchItem) {
        if (paramLaunchItem != null) {
            @Edited(reason = {Reason.VARIABLE_REUSAGE, Reason.VARIABLE_NAMING_CLARITY})
            String pkgName = paramLaunchItem.getPackageName();

            ArrayList<LaunchItem> localArrayList = new ArrayList<>();
            getLaunchItems(this.mInstallingLaunchItems, localArrayList, pkgName, true);
            getLaunchItems(this.mAllLaunchItems, localArrayList, pkgName, true);

            Iterator<LaunchItem> iter = localArrayList.iterator();

            while (iter.hasNext()) {
                ((LaunchItem) ((Iterator) iter).next()).setInstallationState(paramLaunchItem);
            }

            if (localArrayList.isEmpty()) {
                localArrayList.add(paramLaunchItem);
            }

            this.mInstallingLaunchItems.addAll(localArrayList);

            for (AppsViewChangeListener mAppsViewListener : this.mAppsViewListeners) {
                mAppsViewListener.onLaunchItemsAddedOrUpdated(localArrayList);
            }
        }
    }

    private void addOrUpdatePackage(String paramString) {
        ArrayList<LaunchItem> localArrayList = new ArrayList<>();
        findAndRemoveExistingLaunchItems(paramString, localArrayList);

        @Edited(reason = Reason.VARIABLE_REUSAGE)
        ArrayList<LaunchItem> items = createLaunchItems(paramString, localArrayList);

        if (!items.isEmpty()) {
            for (LaunchItem item : items) {
                addItemToAppropriateArea(item);
            }

            for (AppsViewChangeListener mAppsViewListener : this.mAppsViewListeners) {
                (mAppsViewListener).onLaunchItemsAddedOrUpdated(items);
            }
        }

        notifyItemsRemoved(localArrayList);
    }

    private void checkForSearchChanges(String paramString) {
        if ((this.mSearchChangeListener != null) && (paramString != null) && (paramString.equalsIgnoreCase(this.mSearchPackageName))) {
            this.mSearchChangeListener.onSearchPackageChanged();
        }
    }

    static boolean checkIfAppStore(String paramString) {
        return "com.android.vending".equalsIgnoreCase(paramString);
    }

    static boolean checkIfGameStore(String paramString) {
        return "com.google.android.play.games".equalsIgnoreCase(paramString);
    }

    private boolean checkIfOem(LaunchItem paramLaunchItem) {
        return (this.mShowCaseAppSet != null) && (this.mShowCaseAppSet.size() != 0) && (this.mShowCaseAppSet.contains(paramLaunchItem.getPackageName()));
    }

    private ArrayList<LaunchItem> createLaunchItems(String paramString, ArrayList<LaunchItem> paramArrayList) {
        Object localObject = paramArrayList;
        if (paramArrayList == null) {
            localObject = new ArrayList();
        }
        @Edited(reason = Reason.VARIABLE_REUSAGE)
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.setPackage(paramString).addCategory("android.intent.category.LEANBACK_LAUNCHER");
        ArrayList<LaunchItem> launchItems = new ArrayList<>();
        List<ResolveInfo> resolveInfos = this.mContext.getPackageManager().queryIntentActivities(intent, 129);

        Iterator localIterator1 = resolveInfos.iterator();

        while (localIterator1.hasNext()) {
            ResolveInfo localResolveInfo = (ResolveInfo) localIterator1.next();

            if (localResolveInfo.activityInfo != null) {
                Iterator localIterator2 = ((ArrayList) localObject).iterator();

                int i = 0;

                while (localIterator2.hasNext()) {
                    @Edited(reason = Reason.IF_ELSE_DECOMPILE_ERROR)
                    LaunchItem localLaunchItem = (LaunchItem) localIterator2.next();
                    i = (!localLaunchItem.isInitialInstall()) && (!localLaunchItem.hasSamePackageName(localResolveInfo)) ? 0 : 1;

                    launchItems.add(localLaunchItem.set(this.mContext, localResolveInfo));
                    localIterator2.remove();
                }

                if (i != 0) {
                    localIterator1.remove();
                }
            }
        }

        Iterator<ResolveInfo> resolveInfoIter = resolveInfos.iterator();
        Iterator localObj = ((ArrayList) localObject).iterator();
        while ((localObj.hasNext()) && (localObj.hasNext())) {
            launchItems.add(((LaunchItem) localObj.next()).set(this.mContext, resolveInfoIter.next()));
            localObj.remove();
        }

        while (resolveInfoIter.hasNext()) {
            launchItems.add(new LaunchItem(this.mContext, resolveInfoIter.next()));
        }

        return launchItems;
    }

    private void findAndRemoveExistingLaunchItems(String paramString, ArrayList<LaunchItem> paramArrayList) {
        getLaunchItems(this.mInstallingLaunchItems, paramArrayList, paramString, true);
        getLaunchItems(this.mAllLaunchItems, paramArrayList, paramString, true);
        getLaunchItems(this.mOemItems, paramArrayList, paramString, true);
    }

    public static AppsManager getInstance(Context paramContext) {
        if (sAppsManager == null) {
            sAppsManager = new AppsManager(paramContext);
        }
        return sAppsManager;
    }

    private ArrayList<LaunchItem> getLaunchItems(List<LaunchItem> paramList, ArrayList<LaunchItem> paramArrayList, String paramString, boolean paramBoolean) {
        ArrayList<LaunchItem> localObject = paramArrayList;
        if (paramArrayList == null) {
            localObject = new ArrayList<>();
        }
        @Edited(reason = Reason.VARIABLE_REUSAGE)
        Iterator iter = paramList.iterator();
        while (iter.hasNext()) {
            LaunchItem item = (LaunchItem) iter.next();
            if (TextUtils.equals(paramString, item.getPackageName())) {
                localObject.add(item);
                if (paramBoolean) {
                    iter.remove();
                }
            }
        }
        return localObject;
    }

    private ArrayList<LaunchItem> getLaunchItems(boolean paramBoolean1, boolean paramBoolean2) {
        ArrayList<LaunchItem> localArrayList = new ArrayList<>();
        getLaunchItems(this.mInstallingLaunchItems, localArrayList, paramBoolean1, paramBoolean2);
        getLaunchItems(this.mAllLaunchItems, localArrayList, paramBoolean1, paramBoolean2);
        this.mAppsOrderManager.orderGivenItems(localArrayList);
        return localArrayList;
    }

    private void getLaunchItems(List<LaunchItem> paramList1, List<LaunchItem> paramList2, boolean paramBoolean1, boolean paramBoolean2) {
        @Edited(reason = Reason.IF_ELSE_DECOMPILE_ERROR)
        int i = (paramBoolean1) && (paramBoolean2) ? 1 : 0;

        for (LaunchItem localLaunchItem : paramList1) {
            if ((i != 0) || (paramBoolean2 == localLaunchItem.isGame())) {
                paramList2.add(localLaunchItem);
            }
        }
    }

    private void notifyItemsRemoved(ArrayList<LaunchItem> paramArrayList) {
        if (!paramArrayList.isEmpty()) {
            for (AppsViewChangeListener mAppsViewListener : this.mAppsViewListeners) {
                mAppsViewListener.onLaunchItemsRemoved(paramArrayList);
            }
            this.mAppsOrderManager.removeItems(paramArrayList);
        }
    }

    @Edited(reason = Reason.IF_ELSE_DECOMPILE_ERROR)
    private void removeInstallingLaunchItem(LaunchItem paramLaunchItem, boolean paramBoolean) {
        // todo second param isn't currently used

        if (paramLaunchItem != null) {
            addOrUpdatePackage(paramLaunchItem.getPackageName());
        }
    }

    private void removePackage(String paramString) {
        ArrayList<LaunchItem> localArrayList = new ArrayList();
        findAndRemoveExistingLaunchItems(paramString, localArrayList);
        notifyItemsRemoved(localArrayList);
        checkForSearchChanges(paramString);
    }

    public void addToFavorites(LaunchItem paramLaunchItem) {
        this.mFavoriteItemsManager.userAddToFavorites(paramLaunchItem);
    }

    public void addToFavorites(String paramString) {
        this.mFavoriteItemsManager.userAddToFavorites(getLaunchItem(paramString));
    }

    public boolean areItemsLoaded() {
        return this.mItemsLoaded;
    }

    ArrayList<LaunchItem> getAllLaunchItemsWithoutSorting() {
        ArrayList localArrayList = new ArrayList();
        getLaunchItems(this.mOemItems, localArrayList, true, true);
        getLaunchItems(this.mInstallingLaunchItems, localArrayList, true, true);
        getLaunchItems(this.mAllLaunchItems, localArrayList, true, true);
        return localArrayList;
    }

    public ArrayList<LaunchItem> getAllNonFavoriteLaunchItems() {
        ArrayList localArrayList = new ArrayList();
        Iterator localIterator = this.mOemItems.iterator();
        LaunchItem localLaunchItem;
        while (localIterator.hasNext()) {
            localLaunchItem = (LaunchItem) localIterator.next();
            if (!isFavorite(localLaunchItem)) {
                localArrayList.add(localLaunchItem);
            }
        }
        localIterator = getAppLaunchItems().iterator();
        while (localIterator.hasNext()) {
            localLaunchItem = (LaunchItem) localIterator.next();
            if (!isFavorite(localLaunchItem)) {
                localArrayList.add(localLaunchItem);
            }
        }
        localIterator = getGameLaunchItems().iterator();
        while (localIterator.hasNext()) {
            localLaunchItem = (LaunchItem) localIterator.next();
            if (!isFavorite(localLaunchItem)) {
                localArrayList.add(localLaunchItem);
            }
        }
        return localArrayList;
    }

    ArrayList<LaunchItem> getAppLaunchItems() {
        return getLaunchItems(true, false);
    }

    public LaunchItem getAppStoreLaunchItem() {
        return this.mAppStore;
    }

    ArrayList<LaunchItem> getGameLaunchItems() {
        return getLaunchItems(false, true);
    }

    public LaunchItem getGameStoreLaunchItem() {
        return this.mGameStore;
    }

    public List<LaunchItem> getHomeScreenItems() {
        return this.mFavoriteItemsManager.getFavoriteItems();
    }

    LaunchItem getInstallingLaunchItem(String paramString) {
        Iterator localIterator = this.mInstallingLaunchItems.iterator();
        while (localIterator.hasNext()) {
            LaunchItem localLaunchItem = (LaunchItem) localIterator.next();
            if (localLaunchItem.getPackageName().equalsIgnoreCase(paramString)) {
                return localLaunchItem;
            }
        }
        return null;
    }

    LaunchItem getLaunchItem(String paramString) {
        Iterator localIterator = this.mOemItems.iterator();
        LaunchItem localLaunchItem;
        while (localIterator.hasNext()) {
            localLaunchItem = (LaunchItem) localIterator.next();
            if (localLaunchItem.getPackageName().equalsIgnoreCase(paramString)) {
                return localLaunchItem;
            }
        }
        localIterator = this.mInstallingLaunchItems.iterator();
        while (localIterator.hasNext()) {
            localLaunchItem = (LaunchItem) localIterator.next();
            if (localLaunchItem.getPackageName().equalsIgnoreCase(paramString)) {
                return localLaunchItem;
            }
        }
        localIterator = this.mAllLaunchItems.iterator();
        while (localIterator.hasNext()) {
            localLaunchItem = (LaunchItem) localIterator.next();
            if (localLaunchItem.getPackageName().equalsIgnoreCase(paramString)) {
                return localLaunchItem;
            }
        }
        return null;
    }

    ArrayList<LaunchItem> getOemLaunchItems() {
        return new ArrayList<>(this.mOemItems);
    }

    @Edited(reason = Reason.IF_ELSE_DECOMPILE_ERROR)
    int getOrderedPosition(LaunchItem paramLaunchItem) {
        ArrayList localArrayList;
        if (paramLaunchItem.isGame()) {
            localArrayList = getGameLaunchItems();
        } else if (checkIfOem(paramLaunchItem)) {
            localArrayList = getOemLaunchItems();
        } else {
            localArrayList = getAppLaunchItems();
        }

        return localArrayList.indexOf(paramLaunchItem);
    }

    public boolean isFavorite(LaunchItem paramLaunchItem) {
        return this.mFavoriteItemsManager.isFavorite(paramLaunchItem);
    }

    boolean isFavoritesFull() {
        return this.mFavoriteItemsManager.isFull();
    }

    public boolean isOnlyApp(LaunchItem paramLaunchItem) {
        ArrayList localArrayList = getAppLaunchItems();
        return (localArrayList.size() == 1) && (localArrayList.contains(paramLaunchItem));
    }

    public boolean isOnlyFavorite(LaunchItem paramLaunchItem) {
        return this.mFavoriteItemsManager.isOnlyFavorite(paramLaunchItem);
    }

    public boolean isOnlyGame(LaunchItem paramLaunchItem) {
        ArrayList localArrayList = getGameLaunchItems();
        return (localArrayList.size() == 1) && (localArrayList.contains(paramLaunchItem));
    }

    List<LaunchItem> makeFirstTimeFavorites(Set<String> paramSet) {
        Object localObject = getAllLaunchItemsWithoutSorting();
        ArrayList localArrayList = new ArrayList(paramSet.size());
        localObject = ((List) localObject).iterator();
        while (((Iterator) localObject).hasNext()) {
            LaunchItem localLaunchItem = (LaunchItem) ((Iterator) localObject).next();
            String str = localLaunchItem.getPackageName();
            if (paramSet.contains(str)) {
                localArrayList.add(localLaunchItem);
                paramSet.remove(str);
            }
        }
        return localArrayList;
    }

    void onAppOrderChange(ArrayList<LaunchItem> paramArrayList, boolean paramBoolean, Pair<Integer, Integer> paramPair) {
        this.mAppsOrderManager.userChangedOrder(paramArrayList);
        this.mAppsOrderManager.orderGivenItems(this.mAllLaunchItems);
        Iterator localIterator = this.mAppsViewListeners.iterator();
        while (localIterator.hasNext()) {
            ((AppsViewChangeListener) localIterator.next()).onEditModeItemOrderChange(paramArrayList, paramBoolean, paramPair);
        }
    }

    public void onInstallingLaunchItemAdded(LaunchItem paramLaunchItem) {
        addOrUpdateInstallingLaunchItem(paramLaunchItem);
    }

    public void onInstallingLaunchItemChanged(LaunchItem paramLaunchItem) {
        addOrUpdateInstallingLaunchItem(paramLaunchItem);
    }

    public void onInstallingLaunchItemRemoved(LaunchItem paramLaunchItem, boolean paramBoolean) {
        removeInstallingLaunchItem(paramLaunchItem, paramBoolean);
    }

    public void onPackageAdded(String paramString) {
        addOrUpdatePackage(paramString);
    }

    public void onPackageChanged(String paramString) {
        addOrUpdatePackage(paramString);
    }

    public void onPackageFullyRemoved(String paramString) {
        removePackage(paramString);
    }

    public void onPackageRemoved(String paramString) {
        removePackage(paramString);
    }

    public void onPackageReplaced(String paramString) {
        addOrUpdatePackage(paramString);
    }

    public void refreshLaunchItems() {
        this.mItemsLoaded = false;
        if (this.mRefreshTask != null) {
            this.mRefreshTask.cancel(true);
        }
        this.mRefreshTask = new CreateLaunchItemsListTask().execute(new Void[0]);
    }

    public void registerAppsViewChangeListener(AppsViewChangeListener paramAppsViewChangeListener) {
        if (!this.mAppsViewListeners.contains(paramAppsViewChangeListener)) {
            this.mAppsViewListeners.add(paramAppsViewChangeListener);
        }
    }

    public void registerUpdateListeners() {
        int i = this.mReceiversRegisteredRefCount;
        this.mReceiversRegisteredRefCount = (i + 1);
        if (i == 0) {
            this.mContext.registerReceiver(this.mPackageChangedReceiver, PackageChangedReceiver.getIntentFilter());
            this.mContext.registerReceiver(this.mExternalAppsUpdateReceiver, ExternalAppsUpdateReceiver.getIntentFilter());
        }
    }

    public void removeFromFavorites(LaunchItem paramLaunchItem) {
        this.mFavoriteItemsManager.userRemoveFromFavorites(paramLaunchItem);
    }

    void saveOrderSnapshot(List<LaunchItem> paramList) {
        this.mAppsOrderManager.saveOrderSnapshot(paramList);
    }

    public void setHomeScreenItemsChangeListener(HomeScreenItemsChangeListener paramHomeScreenItemsChangeListener) {
        this.mFavoriteItemsManager.setHomeScreenItemsChangeListener(paramHomeScreenItemsChangeListener);
    }

    public void setSearchPackageChangeListener(SearchPackageChangeListener paramSearchPackageChangeListener, String paramString) {
        this.mSearchChangeListener = paramSearchPackageChangeListener;
        if (this.mSearchChangeListener != null) {
            this.mSearchPackageName = paramString;
            return;
        }
        this.mSearchPackageName = null;
    }

    public void swapFavoriteAppOrder(LaunchItem paramLaunchItem1, LaunchItem paramLaunchItem2) {
        this.mFavoriteItemsManager.swapAppOrder(paramLaunchItem1, paramLaunchItem2);
    }

    public void unregisterAppsViewChangeListener(AppsViewChangeListener paramAppsViewChangeListener) {
        this.mAppsViewListeners.remove(paramAppsViewChangeListener);
    }

    void unregisterUpdateListeners() {
        int i = this.mReceiversRegisteredRefCount - 1;
        this.mReceiversRegisteredRefCount = i;
        if (i == 0) {
            this.mContext.unregisterReceiver(this.mPackageChangedReceiver);
            this.mContext.unregisterReceiver(this.mExternalAppsUpdateReceiver);
        }
    }

    public static abstract interface AppsViewChangeListener {
        public abstract void onEditModeItemOrderChange(ArrayList<LaunchItem> paramArrayList, boolean paramBoolean, Pair<Integer, Integer> paramPair);

        public abstract void onLaunchItemsAddedOrUpdated(ArrayList<LaunchItem> paramArrayList);

        public abstract void onLaunchItemsLoaded();

        public abstract void onLaunchItemsRemoved(ArrayList<LaunchItem> paramArrayList);
    }

    private class CreateLaunchItemsListTask
            extends AsyncTask<Void, Void, List<LaunchItem>> {
        private CreateLaunchItemsListTask() {
        }

        protected List<LaunchItem> doInBackground(Void... paramVarArgs) {
            @Edited(reason = Reason.VARIABLE_REUSAGE)
            Intent intent = new Intent("android.intent.action.MAIN");
            intent.addCategory("android.intent.category.LEANBACK_LAUNCHER");
            LinkedList<LaunchItem> localLinkedList = new LinkedList<>();

            PackageManager packageManager = AppsManager.this.mContext.getPackageManager();

            if (isCancelled()) {
                return localLinkedList;
            }

            @Edited(reason = Reason.FOR_WHILE_MIXUP)
            List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(intent, 129);

            for (int i = 0; i < resolveInfos.size(); i++) {
                ResolveInfo info = resolveInfos.get(i);

                if (info.activityInfo != null) {
                    localLinkedList.add(new LaunchItem(AppsManager.this.mContext, info));
                }
            }

            return localLinkedList;
        }

        protected void onPostExecute(List<LaunchItem> paramList) {
            AppsManager.this.mAllLaunchItems.clear();
            AppsManager.this.mOemItems.clear();

            for (LaunchItem localLaunchItem : paramList) {
                AppsManager.this.addItemToAppropriateArea(localLaunchItem);
            }

            AppsManager.this.mAppsOrderManager.orderGivenItems(AppsManager.this.mAllLaunchItems);
            // todo AppsManager.access$102(AppsManager.this, true);
            AppsManager.this.mItemsLoaded = true; // todo I think?

            for (AppsViewChangeListener mAppsViewListener : AppsManager.this.mAppsViewListeners) {
                mAppsViewListener.onLaunchItemsLoaded();
            }

            // todo AppsManager.access$902(AppsManager.this, null);
        }
    }

    public static abstract interface HomeScreenItemsChangeListener {
        public abstract void onHomeScreenItemsChanged(List<LaunchItem> paramList);

        public abstract void onHomeScreenItemsLoaded();

        public abstract void onHomeScreenItemsSwapped(int paramInt1, int paramInt2);
    }

    public static abstract interface SearchPackageChangeListener {
        public abstract void onSearchPackageChanged();
    }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/appsview/AppsManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */