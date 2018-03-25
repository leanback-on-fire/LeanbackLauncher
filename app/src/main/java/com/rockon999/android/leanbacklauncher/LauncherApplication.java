package com.rockon999.android.leanbacklauncher;

import android.app.Application;
import android.content.Context;
import android.os.RemoteException;
import android.util.Log;

import com.rockon999.android.leanbacklauncher.capabilities.HighEndLauncherConfiguration;
import com.rockon999.android.leanbacklauncher.capabilities.LauncherConfiguration;
import com.rockon999.android.leanbacklauncher.recommendations.SwitchingRecommendationsClient;
import com.rockon999.android.tvrecommendations.IRecommendationsService;
import com.rockon999.android.tvrecommendations.RecommendationsClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LauncherApplication extends Application {
    private static boolean sBlacklistMigrated;
    private NewBlacklistClient mNewBlacklistClient;
    private OldBlacklistClient mOldBlacklistClient;

    private class NewBlacklistClient extends SwitchingRecommendationsClient {
        private String[] mBlacklist;

        public NewBlacklistClient(Context context) {
            super(context);
        }

        public void saveBlackList(String[] blacklist) {
            this.mBlacklist = blacklist;
            connect();
        }

        protected void onConnected(IRecommendationsService service) {
            try {
                List<String> newBlacklist = new ArrayList(Arrays.asList(service.getBlacklistedPackages()));
                for (String pkg : this.mBlacklist) {
                    if (!newBlacklist.contains(pkg)) {
                        newBlacklist.add(pkg);
                    }
                }
                service.setBlacklistedPackages((String[]) newBlacklist.toArray(new String[newBlacklist.size()]));
            } catch (RemoteException e) {
                Log.e("LauncherApplication", "Could not save migrated blacklist", e);
            }
            disconnect();
            LauncherApplication.this.mNewBlacklistClient = null;
        }

        protected void onDisconnected() {
        }
    }

    private class OldBlacklistClient extends RecommendationsClient {
        public OldBlacklistClient(Context context) {
            super(context);
        }

        protected void onConnected(IRecommendationsService service) {
            synchronized (LauncherApplication.class) {
                if (!LauncherApplication.sBlacklistMigrated) {
                    try {
                        String[] blacklist = service.getBlacklistedPackages();
                        service.setBlacklistedPackages(new String[0]);
                        LauncherApplication.sBlacklistMigrated = true;
                        LauncherApplication.this.getSharedPreferences(getClass().getName(), 0).edit().putInt("blacklist_migrate", 1).apply();
                        if (blacklist == null || blacklist.length <= 0) {
                            Log.d("LauncherApplication", "No blacklist to migrate");
                        } else {
                            LauncherApplication.this.mNewBlacklistClient.saveBlackList(blacklist);
                        }
                    } catch (RemoteException e) {
                        Log.e("LauncherApplication", "Could not migrate blacklist", e);
                    }
                }
            }
            disconnect();
            LauncherApplication.this.mOldBlacklistClient = null;
        }

        protected void onDisconnected() {
        }
    }

    public void onCreate() {
        super.onCreate();
        initDeviceCapabilities();
        initPrimes();
        demigrate();
    }

    private void initDeviceCapabilities() {
        LauncherConfiguration.setInstance(new HighEndLauncherConfiguration());
    }

    private void initPrimes() {
        //final PrimesSettings primesSettings = new PrimesSettings(this);
        /*if (primesSettings.isPrimesEnabled()) {
            Primes primes = Primes.initialize(PrimesApiProvider.newInstance(this, new PrimesConfigurationsProvider() {
                public PrimesConfigurations get() {
                    return PrimesConfigurations.newBuilder().setMetricTransmitter(LauncherApplication.this.getPrimesMetricTransmitter()).setPackageConfigurations(new PrimesPackageConfigurations(primesSettings.isPackageStatsMetricEnabled())).setMemoryConfigurations(new PrimesMemoryConfigurations(primesSettings.isMemoryMetricEnabled())).setCrashConfigurations(new PrimesCrashConfigurations(primesSettings.isCrashMetricEnabled())).build();
                }
            }));
            primes.startMemoryMonitor();
            primes.startCrashMonitor();
            return;
        }*/
        Log.e("LauncherApplication", "PRIMES not enabled");
    }

    // private MetricTransmitter getPrimesMetricTransmitter() {
    //    return new ClearcutMetricTransmitter(this, "LEANBACK_LAUNCHER_PRIMES");
    // }

    private void demigrate() {
        boolean z = false;
        if (sBlacklistMigrated || getSharedPreferences(getClass().getName(), 0).getInt("blacklist_migrate", 0) >= 1) {
            z = true;
        }
        sBlacklistMigrated = z;
        if (!sBlacklistMigrated) {
            this.mOldBlacklistClient = new OldBlacklistClient(this);
            this.mNewBlacklistClient = new NewBlacklistClient(this);
            try {
                this.mOldBlacklistClient.connect();
            } catch (RuntimeException e) {
                Log.v("LauncherApplication", "Couldn't connect to service to read blacklist", e);
            }
        }
    }
}
