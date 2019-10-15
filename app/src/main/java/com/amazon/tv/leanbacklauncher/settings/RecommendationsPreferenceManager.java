package com.amazon.tv.leanbacklauncher.settings;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.DeadObjectException;
import android.os.RemoteException;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;

import com.amazon.tv.leanbacklauncher.recommendations.SwitchingRecommendationsClient;
import com.amazon.tv.tvrecommendations.IRecommendationsService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RecommendationsPreferenceManager {
    private Context mContext;

    public interface LoadBlacklistCountCallback {
        void onBlacklistCountLoaded(int i);
    }

    public interface LoadRecommendationPackagesCallback {
        void onRecommendationPackagesLoaded(List<PackageInfo> list);
    }

    private static abstract class AsyncRecommendationsClient extends SwitchingRecommendationsClient {

        private class Task extends AsyncTask<IRecommendationsService, Void, Boolean> {
            private Task() {
            }

            protected Boolean doInBackground(IRecommendationsService... params) {
                try {
                    AsyncRecommendationsClient.this.callServiceInBackground(params[0]);
                } catch (DeadObjectException e) {
                    Log.e("RecPrefManager", "Rec service connection broken", e);
                    Boolean valueOf = Boolean.valueOf(true);
                    return valueOf;
                } catch (RemoteException e2) {
                    Log.e("RecPrefManager", "Call to recommendation service failed", e2);
                } finally {
                    AsyncRecommendationsClient.this.disconnect();
                }
                return Boolean.valueOf(false);
            }

            protected void onPostExecute(Boolean retry) {
                if (retry.booleanValue()) {
                    Log.d("RecPrefManager", "Task failed, retrying");
                    AsyncRecommendationsClient.this.connect();
                    return;
                }
                AsyncRecommendationsClient.this.onPostExecute();
            }
        }

        protected abstract void callServiceInBackground(IRecommendationsService iRecommendationsService) throws RemoteException;

        public AsyncRecommendationsClient(Context context) {
            super(context);
        }

        protected void onConnected(IRecommendationsService service) {
            new Task().execute(new IRecommendationsService[]{service});
        }

        protected void onDisconnected() {
        }

        protected void onPostExecute() {
        }
    }

    private static class LoadBlacklistCountTask extends AsyncRecommendationsClient {
        private int mBlacklistedPackageCount;
        private final LoadBlacklistCountCallback mCallback;
        private final PackageManager mPackageManager;

        public LoadBlacklistCountTask(Context context, LoadBlacklistCountCallback callback) {
            super(context);
            this.mCallback = callback;
            this.mPackageManager = context.getPackageManager();
        }

        protected void callServiceInBackground(IRecommendationsService service) {
            try {
                String[] blacklist = service.getBlacklistedPackages();
                this.mBlacklistedPackageCount = blacklist.length;
                for (String pkg : blacklist) {
                    try {
                        this.mPackageManager.getPackageInfo(pkg, 0);
                    } catch (NameNotFoundException e) {
                        this.mBlacklistedPackageCount--;
                    }
                }
            } catch (RemoteException e2) {
                this.mBlacklistedPackageCount = 0;
            }
        }

        protected void onPostExecute() {
            this.mCallback.onBlacklistCountLoaded(Math.max(0, this.mBlacklistedPackageCount));
        }
    }

    private static class LoadRecommendationPackagesTask extends AsyncRecommendationsClient {
        private final LoadRecommendationPackagesCallback mCallback;
        private final Context mContext;
        private List<PackageInfo> mPackages;

        public LoadRecommendationPackagesTask(Context context, LoadRecommendationPackagesCallback callback) {
            super(context);
            this.mCallback = callback;
            this.mContext = context;
        }

        protected void callServiceInBackground(IRecommendationsService service) throws RemoteException {
            String[] packages = service.getRecommendationsPackages();
            List<String> blacklistedPackages = Arrays.asList(service.getBlacklistedPackages());
            this.mPackages = new ArrayList(packages.length);
            PackageManager pm = this.mContext.getPackageManager();
            for (String packageName : packages) {
                PackageInfo info = new PackageInfo();
                info.packageName = packageName;
                try {
                    ApplicationInfo appInfo = pm.getApplicationInfo(packageName, 0);
                    Resources res = pm.getResourcesForApplication(packageName);
                    info.appTitle = pm.getApplicationLabel(appInfo);
                    if (appInfo.banner != 0) {
                        info.banner = res.getDrawable(appInfo.banner, null);
                    } else {
                        Intent intent = new Intent();
                        intent.addCategory("android.intent.category.LEANBACK_LAUNCHER");
                        intent.setAction("android.intent.action.MAIN");
                        intent.setPackage(packageName);
                        ResolveInfo resolveInfo = pm.resolveActivity(intent, 0);
                        if (!(resolveInfo == null || resolveInfo.activityInfo == null)) {
                            if (resolveInfo.activityInfo.banner != 0) {
                                info.banner = res.getDrawable(resolveInfo.activityInfo.banner, null);
                            }
                            if (info.banner == null && resolveInfo.activityInfo.logo != 0) {
                                info.banner = res.getDrawable(resolveInfo.activityInfo.logo, null);
                            }
                        }
                    }
                    if (info.banner == null && appInfo.icon != 0) {
                        info.icon = res.getDrawable(appInfo.icon, null);
                    }
                    if (TextUtils.isEmpty(info.appTitle)) {
                        info.appTitle = packageName;
                    }
                    if (info.banner == null && info.icon == null) {
                        info.icon = ContextCompat.getDrawable(this.mContext, 17301651);
                    }
                    info.blacklisted = blacklistedPackages.contains(packageName);
                    this.mPackages.add(info);
                } catch (NameNotFoundException e) {
                }
            }
        }

        protected void onPostExecute() {
            this.mCallback.onRecommendationPackagesLoaded(this.mPackages);
        }
    }

    public static class PackageInfo {
        public CharSequence appTitle;
        public Drawable banner;
        public boolean blacklisted;
        public Drawable icon;
        public String packageName;
    }

    private static class SaveBlacklistTask extends AsyncRecommendationsClient {
        private final boolean mBlacklisted;
        private final String mPackageName;

        public SaveBlacklistTask(Context context, String packageName, boolean blacklisted) {
            super(context);
            this.mPackageName = packageName;
            this.mBlacklisted = blacklisted;
        }

        protected void callServiceInBackground(IRecommendationsService service) throws RemoteException {
            List<String> blacklist = new ArrayList(Arrays.asList(service.getBlacklistedPackages()));
            if (!this.mBlacklisted) {
                blacklist.remove(this.mPackageName);
            } else if (!blacklist.contains(this.mPackageName)) {
                blacklist.add(this.mPackageName);
            }
            service.setBlacklistedPackages((String[]) blacklist.toArray(new String[0]));
        }
    }

    public RecommendationsPreferenceManager(Context context) {
        this.mContext = context.getApplicationContext();
    }

    public void loadBlacklistCount(LoadBlacklistCountCallback callback) {
        new LoadBlacklistCountTask(this.mContext, callback).connect();
    }

    public void loadRecommendationsPackages(LoadRecommendationPackagesCallback callback) {
        new LoadRecommendationPackagesTask(this.mContext, callback).connect();
    }

    public void savePackageBlacklisted(String packageName, boolean blacklisted) {
        new SaveBlacklistTask(this.mContext, packageName, blacklisted).connect();
    }
}
