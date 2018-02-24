package com.google.android.tvlauncher.appsview;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Pair;

import com.google.android.tvlauncher.R;
import com.google.android.tvlauncher.settings.FavoriteAppsActivity;
import com.google.android.tvlauncher.util.Partner;
import com.google.android.tvlauncher.util.porting.Edited;
import com.google.android.tvlauncher.util.porting.Reason;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

class FavoriteItemsManager
        implements AppsManager.AppsViewChangeListener {
    private static final String FIRSTBOOT_KEY = "com.google.android.tvlauncher.appsview.FavoriteItemsManager.FIRSTBOOT_KEY";
    static final int MAX_HOME_SCREEN_APP_ITEMS = 6;
    private static final String MORE_FAVORITES_PKGNAME = "com.google.android.tvlauncher.appsview.FavoriteItemsManager.MORE_FAVORITES_PKGNAME";
    private static final String PREF_KEY = "com.google.android.tvlauncher.appsview.FavoriteItemsManager.PREFERENCE_KEY";
    private static final String USER_CUSTOMIZATION_KEY = "com.google.android.tvlauncher.appsview.FavoriteItemsManager.USER_CUSTOMIZATION_KEY";
    private Context mContext;
    private final Map<String, Integer> mDefaultItemsOrder = new HashMap<>();
    private final Map<LaunchItem, Integer> mFavoriteItems = new HashMap<>();
    private final SharedPreferences mFirstBootPref;
    private AppsManager.HomeScreenItemsChangeListener mHomeScreenItemsChangeListener;
    private final LaunchItem mMoreFavoritesItem;
    private final SharedPreferences mPackageNamePrefs;
    private boolean mUserCustomization;
    private final SharedPreferences mUserCustomizationPref;

    FavoriteItemsManager(Context paramContext) {
        this.mPackageNamePrefs = paramContext.getSharedPreferences("com.google.android.tvlauncher.appsview.FavoriteItemsManager.PREFERENCE_KEY", 0);
        this.mFirstBootPref = paramContext.getSharedPreferences("com.google.android.tvlauncher.appsview.FavoriteItemsManager.FIRSTBOOT_KEY", 0);
        this.mUserCustomizationPref = paramContext.getSharedPreferences("com.google.android.tvlauncher.appsview.FavoriteItemsManager.USER_CUSTOMIZATION_KEY", 0);
        this.mContext = paramContext;
        this.mMoreFavoritesItem = new LaunchItem(this.mContext, this.mContext.getString(R.string.favorite_more_apps), "com.google.android.tvlauncher.appsview.FavoriteItemsManager.MORE_FAVORITES_PKGNAME", new Intent(this.mContext, FavoriteAppsActivity.class), this.mContext.getDrawable(R.drawable.ic_add_app_plus_24dp));
        this.mUserCustomization = this.mUserCustomizationPref.getBoolean("com.google.android.tvlauncher.appsview.FavoriteItemsManager.USER_CUSTOMIZATION_KEY", false);
        if (this.mFirstBootPref.getBoolean("com.google.android.tvlauncher.appsview.FavoriteItemsManager.FIRSTBOOT_KEY", true)) {
            String[] favorites = Partner.get(this.mContext).getOutOfBoxFavoriteAppsList();
            if (favorites != null) {
                int i = 0;
                while ((i < favorites.length) && (i < 6)) {
                    this.mDefaultItemsOrder.put(favorites[i], Integer.valueOf(i));
                    i += 1;
                }
                initializeFirstBootupFavorites();
            }
            this.mFirstBootPref.edit().putBoolean("com.google.android.tvlauncher.appsview.FavoriteItemsManager.FIRSTBOOT_KEY", false).apply();
        }
    }

    private List<LaunchItem> getSortedFavorites() {
        ArrayList localArrayList = new ArrayList(this.mFavoriteItems.keySet());
        Collections.sort(localArrayList, new LaunchItemUserOrderComparator());
        return localArrayList;
    }

    private void initializeFirstBootupFavorites() {
        SharedPreferences.Editor localEditor = this.mPackageNamePrefs.edit();
        localEditor.clear();
        Iterator localIterator = this.mDefaultItemsOrder.keySet().iterator();
        while (localIterator.hasNext()) {
            String str = (String) localIterator.next();
            localEditor.putInt(str, ((Integer) this.mDefaultItemsOrder.get(str)).intValue());
        }
        localEditor.apply();
    }

    private void notifyItemsSwapped(int paramInt1, int paramInt2) {
        if (this.mHomeScreenItemsChangeListener != null) {
            this.mHomeScreenItemsChangeListener.onHomeScreenItemsSwapped(paramInt1, paramInt2);
        }
    }

    private void notifyOnHomeScreenItemsChanged(List<LaunchItem> paramList) {
        if (this.mHomeScreenItemsChangeListener != null) {
            this.mHomeScreenItemsChangeListener.onHomeScreenItemsChanged(paramList);
        }
    }

    private void notifyOnHomeScreenItemsLoaded() {
        if (this.mHomeScreenItemsChangeListener != null) {
            this.mHomeScreenItemsChangeListener.onHomeScreenItemsLoaded();
        }
    }

    private void readFromPreferences() {
        this.mFavoriteItems.clear();
        Map localMap = this.mPackageNamePrefs.getAll();
        Iterator localIterator = AppsManager.getInstance(this.mContext).getAllLaunchItemsWithoutSorting().iterator();
        while (localIterator.hasNext()) {
            LaunchItem localLaunchItem = (LaunchItem) localIterator.next();
            if (localMap.keySet().contains(localLaunchItem.getPackageName())) {
                this.mFavoriteItems.put(localLaunchItem, (Integer) localMap.get(localLaunchItem.getPackageName()));
            }
        }
    }

    private int removeFavorite(LaunchItem paramLaunchItem) {
        Integer index = this.mFavoriteItems.remove(paramLaunchItem);
        if (index != null) {
            List localList = getSortedFavorites();
            saveOrderSnapshot(localList);
            notifyOnHomeScreenItemsChanged(localList);
        }
        if (index == null) {
            return -1;
        }
        return index.intValue();
    }

    private void userActionSwitchToUserCustomization() {
        if (!this.mUserCustomization) {
            this.mUserCustomizationPref.edit().putBoolean("com.google.android.tvlauncher.appsview.FavoriteItemsManager.USER_CUSTOMIZATION_KEY", true).apply();
            this.mUserCustomization = true;
        }
    }

    List<LaunchItem> getFavoriteItems() {
        List localList = getSortedFavorites();
        if ((localList.size() < 6) && (!localList.contains(this.mMoreFavoritesItem))) {
            localList.add(this.mMoreFavoritesItem);
        }
        return localList;
    }

    boolean isFavorite(LaunchItem paramLaunchItem) {
        return this.mFavoriteItems.containsKey(paramLaunchItem);
    }

    boolean isFull() {
        return this.mFavoriteItems.size() == 6;
    }

    boolean isOnlyFavorite(LaunchItem paramLaunchItem) {
        return (this.mFavoriteItems.size() == 1) && (this.mFavoriteItems.containsKey(paramLaunchItem));
    }

    public void onEditModeItemOrderChange(ArrayList<LaunchItem> paramArrayList, boolean paramBoolean, Pair<Integer, Integer> paramPair) {
    }

    public void onLaunchItemsAddedOrUpdated(ArrayList<LaunchItem> paramArrayList) {
        int i = 0;
        for (LaunchItem localLaunchItem : paramArrayList) {
            if (this.mFavoriteItems.containsKey(localLaunchItem)) {
                this.mFavoriteItems.put(localLaunchItem, this.mFavoriteItems.get(localLaunchItem));
                i = 1;
            } else if ((!this.mUserCustomization) && (this.mDefaultItemsOrder.containsKey(localLaunchItem.getPackageName()))) {
                this.mFavoriteItems.put(localLaunchItem, this.mDefaultItemsOrder.get(localLaunchItem.getPackageName()));
                saveOrderSnapshot(getSortedFavorites());
                i = 1;
            }
        }
        if (i != 0) {
            notifyOnHomeScreenItemsChanged(getFavoriteItems());
        }
    }

    public void onLaunchItemsLoaded() {
        readFromPreferences();
        notifyOnHomeScreenItemsLoaded();
    }

    @Edited(reason = Reason.IF_ELSE_DECOMPILE_ERROR)
    public void onLaunchItemsRemoved(ArrayList<LaunchItem> paramArrayList) {
        int i = 0;
        Iterator<LaunchItem> iter = paramArrayList.iterator();
        if (iter.hasNext()) {
            int j = removeFavorite((LaunchItem) iter.next()) == -1 ? 1 : 0;
            i |= j;
        }
        if (i != 0) {
            notifyOnHomeScreenItemsChanged(getFavoriteItems());
        }
    }

    void saveOrderSnapshot(List<LaunchItem> paramList) {
        SharedPreferences.Editor localEditor = this.mPackageNamePrefs.edit();
        localEditor.clear();
        int i = 0;
        while (i < paramList.size()) {
            LaunchItem localLaunchItem = (LaunchItem) paramList.get(i);
            this.mFavoriteItems.put(localLaunchItem, Integer.valueOf(i));
            localEditor.putInt(localLaunchItem.getPackageName(), i);
            i += 1;
        }
        localEditor.apply();
    }

    void setHomeScreenItemsChangeListener(AppsManager.HomeScreenItemsChangeListener paramHomeScreenItemsChangeListener) {
        this.mHomeScreenItemsChangeListener = paramHomeScreenItemsChangeListener;
    }

    public void swapAppOrder(LaunchItem paramLaunchItem1, LaunchItem paramLaunchItem2) {
        userActionSwitchToUserCustomization();
        int i = this.mFavoriteItems.get(paramLaunchItem1);
        int j = this.mFavoriteItems.get(paramLaunchItem2);
        this.mFavoriteItems.put(paramLaunchItem1, Integer.valueOf(j));
        this.mFavoriteItems.put(paramLaunchItem2, Integer.valueOf(i));
        SharedPreferences.Editor localEditor = this.mPackageNamePrefs.edit();
        localEditor.putInt(paramLaunchItem1.getPackageName(), j);
        localEditor.putInt(paramLaunchItem2.getPackageName(), i);
        localEditor.apply();
        notifyItemsSwapped(i, j);
    }

    void userAddToFavorites(LaunchItem paramLaunchItem) {
        if ((!this.mFavoriteItems.containsKey(paramLaunchItem)) && (this.mFavoriteItems.size() < 6)) {
            userActionSwitchToUserCustomization();
            int i = this.mFavoriteItems.keySet().size();
            this.mFavoriteItems.put(paramLaunchItem, Integer.valueOf(i));
            List<LaunchItem> sortedFavorites = getSortedFavorites();
            saveOrderSnapshot(sortedFavorites);
            notifyOnHomeScreenItemsChanged(sortedFavorites);
        }
    }

    int userRemoveFromFavorites(LaunchItem paramLaunchItem) {
        userActionSwitchToUserCustomization();
        return removeFavorite(paramLaunchItem);
    }

    @Edited(reason = Reason.IF_ELSE_DECOMPILE_ERROR)
    private class LaunchItemUserOrderComparator implements Comparator<LaunchItem> {
        private LaunchItemUserOrderComparator() {
        }

        public int compare(LaunchItem paramLaunchItem1, LaunchItem paramLaunchItem2) {
            if ((paramLaunchItem1 == null) || (paramLaunchItem2 == null)) {
                return 0;
            }
            Integer localInteger2 = FavoriteItemsManager.this.mFavoriteItems.get(paramLaunchItem1);
            Integer localInteger1 = FavoriteItemsManager.this.mFavoriteItems.get(paramLaunchItem2);
            if (localInteger2 == null) {
                return -1;
            } else if (localInteger1 == null) {
                return 1;
            } else {
                /*int i = localInteger1 != null ? localInteger1 : FavoriteItemsManager.this.mFavoriteItems.keySet().size();
                localInteger2 = i;
                localInteger1 = i;
                if (localInteger2 >= localInteger1) {
                    if (localInteger2 > localInteger1) {
                        return 1;
                    }
                }*/
                // todo fix
            }

            return paramLaunchItem1.compareTo(paramLaunchItem2);
        }
    }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/appsview/FavoriteItemsManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */