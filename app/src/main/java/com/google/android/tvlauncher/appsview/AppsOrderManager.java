package com.google.android.tvlauncher.appsview;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;

import com.google.android.exoplayer2.util.ReusableBufferedOutputStream;
import com.google.android.tvlauncher.util.Partner;
import com.google.android.tvlauncher.util.porting.Edited;
import com.google.android.tvlauncher.util.porting.Reason;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

class AppsOrderManager {
    static final String KEY_SORT_TYPE = "key_sort_type";
    static final String PREF_KEY = "com.google.android.tvlauncher.appsview.PREFERENCE_FILE_KEY";
    static final String VALUE_SORT_TYPE_DEFAULT = "sort_type_default";
    static final String VALUE_SORT_TYPE_USER_ORDER = "sort_type_user_order";
    private static AppsOrderManager sAppsOrderManager;
    private final HashMap<String, Integer> mAppOrderMap = new HashMap<>();
    private LaunchItemComparator mComparator;
    private Context mContext;
    private final HashMap<String, Integer> mDefaultOrderMap = new HashMap<>();
    private SharedPreferences mPrefs;

    private AppsOrderManager(Context paramContext) {
        init(paramContext, Partner.get(paramContext));
    }

    @VisibleForTesting(otherwise = 2)
    AppsOrderManager(Context paramContext, Partner paramPartner) {
        init(paramContext, paramPartner);
    }

    private void addItemsToPreferences(List<LaunchItem> paramList, SharedPreferences.Editor paramEditor) {
        int i = 0;
        while (i < paramList.size()) {
            String str = ((LaunchItem) paramList.get(i)).getPackageName();
            this.mAppOrderMap.put(str, Integer.valueOf(i));
            paramEditor.putInt(str, i);
            i += 1;
        }
    }

    public static AppsOrderManager getInstance(Context paramContext) {
        if (sAppsOrderManager == null) {
            sAppsOrderManager = new AppsOrderManager(paramContext);
        }
        return sAppsOrderManager;
    }

    private void init(Context paramContext, Partner paramPartner) {
        this.mContext = paramContext.getApplicationContext();
        this.mPrefs = this.mContext.getSharedPreferences("com.google.android.tvlauncher.appsview.PREFERENCE_FILE_KEY", 0);
        this.mComparator = new LaunchItemComparator();
        this.mDefaultOrderMap.clear();
        @Edited(reason = Reason.VARIABLE_REUSAGE)
        String[] apps = paramPartner.getOutOfBoxAllAppsList();

        if (apps != null) {
            for (int i = 0; i < apps.length; i++) {
                this.mDefaultOrderMap.put(apps[i], i);
            }
        }
    }

    private void readIntoList() {
        Map<String, ?> localMap = this.mPrefs.getAll();

        for (String str : localMap.keySet()) {
            if ((!TextUtils.equals(str, "key_sort_type")) && ((localMap.get(str) instanceof Integer))) {
                this.mAppOrderMap.put(str, (Integer) localMap.get(str));
            }
        }
    }

    private void removeItem(LaunchItem paramLaunchItem) {
        this.mAppOrderMap.remove(paramLaunchItem.getPackageName());
        SharedPreferences.Editor localEditor = this.mPrefs.edit();
        localEditor.remove(paramLaunchItem.getPackageName());
        localEditor.apply();
    }

    @VisibleForTesting(otherwise = 2)
    HashMap<String, Integer> getAppOrderMap() {
        return this.mAppOrderMap;
    }

    void orderGivenItems(List<LaunchItem> paramList) {
        readIntoList();
        Collections.sort(paramList, this.mComparator);
    }

    void removeItems(List<LaunchItem> paramList) {
        for (LaunchItem item : paramList) {
            removeItem(item);
        }
    }

    void saveOrderSnapshot(List<LaunchItem> paramList) {
        SharedPreferences.Editor localEditor = this.mPrefs.edit();
        addItemsToPreferences(paramList, localEditor);
        localEditor.apply();
    }

    void userChangedOrder(List<LaunchItem> paramList) {
        SharedPreferences.Editor localEditor = this.mPrefs.edit();
        localEditor.putString("key_sort_type", "sort_type_user_order");
        addItemsToPreferences(paramList, localEditor);
        localEditor.apply();
    }

    private class LaunchItemComparator implements Comparator<LaunchItem> {
        private LaunchItemComparator() {
        }

        @Edited(reason = Reason.IF_WHILE_MIXUP)
        private int compareIndices(Integer paramInteger1, Integer paramInteger2) {
            if ((paramInteger1 == null) && (paramInteger2 == null)) {
                return 0;
            }
            if (paramInteger1 == null) {
                return 1;
            }

            if (paramInteger2 == null) {
                return -1;
            }

            if (paramInteger2 > paramInteger1) {
                return -1;
            } else {
                return 1;
            }
        }

        public int compare(LaunchItem paramLaunchItem1, LaunchItem paramLaunchItem2) {
            int i;
            if ((paramLaunchItem1 == null) || (paramLaunchItem2 == null)) {
                i = 0;
                return i;
            }

            Integer localInteger = null;

            if (AppsOrderManager.this.mPrefs.getString("key_sort_type", "sort_type_default").equalsIgnoreCase("sort_type_default")) {
                localInteger = AppsOrderManager.this.mDefaultOrderMap.get(paramLaunchItem1.getPackageName());
                return compareIndices(localInteger, AppsOrderManager.this.mDefaultOrderMap.get(paramLaunchItem2.getPackageName()));
            }

            // todo why is this code here?
            //if ((localInteger != null) && (localInteger != null)) {
            //    return paramLaunchItem1.compareTo(paramLaunchItem2);
            //}

            return compareIndices(AppsOrderManager.this.mAppOrderMap.get(paramLaunchItem1.getPackageName()), AppsOrderManager.this.mAppOrderMap.get(paramLaunchItem2.getPackageName()));
        }
    }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/appsview/AppsOrderManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */