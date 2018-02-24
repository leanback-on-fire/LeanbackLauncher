package com.google.android.tvlauncher.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v14.preference.SwitchPreference;
import android.support.v17.preference.LeanbackPreferenceFragment;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.preference.PreferenceScreen;

import com.google.android.tvlauncher.analytics.FragmentEventLogger;
import com.google.android.tvlauncher.analytics.UserActionEvent;
import com.google.android.tvlauncher.data.PackageChannelsObserver;
import com.google.android.tvlauncher.data.TvDataManager;
import com.google.android.tvlauncher.model.Channel;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class AppChannelEnableChannelFragment
        extends LeanbackPreferenceFragment
        implements Preference.OnPreferenceChangeListener {
    static final String APP_NAME_KEY = "app_name";
    static final String CHANNEL_APP_KEY = "channel_app";
    private String mAppName;
    private String mChannelAppKey;
    private final PackageChannelsObserver mChannelsObserver = new PackageChannelsObserver() {
        public void onChannelsChange() {
            AppChannelEnableChannelFragment.this.onChannelsLoaded();
        }
    };
    private final FragmentEventLogger mEventLogger = new FragmentEventLogger(this);
    private boolean mLoggedOpenEvent;
    private boolean mStarted;
    private TvDataManager mTvDataManager;

    private void loadChannels() {
        if (this.mTvDataManager.isPackageChannelDataLoaded(this.mChannelAppKey)) {
            onChannelsLoaded();
            return;
        }
        this.mTvDataManager.loadPackageChannelsData(this.mChannelAppKey);
    }

    private void logEvent(String paramString) {
        paramString = new UserActionEvent(paramString);
        Object localObject = this.mTvDataManager.getPackageChannels(this.mChannelAppKey);
        if (localObject != null) {
        }
        int k;
        for (int j = ((List) localObject).size(); ; j = 0) {
            k = 0;
            int i = 0;
            if (j == 0) {
                break;
            }
            localObject = ((List) localObject).iterator();
            for (; ; ) {
                k = i;
                if (!((Iterator) localObject).hasNext()) {
                    break;
                }
                if (((Channel) ((Iterator) localObject).next()).isBrowsable()) {
                    i += 1;
                }
            }
        }
        paramString.putParameter("package_name", this.mChannelAppKey).putParameter("channel_count", j).putParameter("browsable_channel_count", k);
        this.mEventLogger.log(paramString);
    }

    private void registerObserverAndUpdateDataIfNeeded() {
        this.mTvDataManager.registerPackageChannelsObserver(this.mChannelsObserver);
        if (this.mChannelAppKey != null) {
            loadChannels();
        }
    }

    public void onChannelsLoaded() {
        if (!isAdded()) {
        }
        do {
            return;
            PreferenceScreen localPreferenceScreen = getPreferenceScreen();
            localPreferenceScreen.removeAll();
            Object localObject = this.mTvDataManager.getPackageChannels(this.mChannelAppKey);
            if ((localObject != null) && (((List) localObject).size() > 0)) {
                Collections.sort((List) localObject);
                localObject = ((List) localObject).iterator();
                while (((Iterator) localObject).hasNext()) {
                    Channel localChannel = (Channel) ((Iterator) localObject).next();
                    SwitchPreference localSwitchPreference = new SwitchPreference(getPreferenceManager().getContext());
                    localSwitchPreference.setLayoutResource(2130968609);
                    localSwitchPreference.setKey(Long.toString(localChannel.getId()));
                    localSwitchPreference.setTitle(localChannel.getDisplayName());
                    localSwitchPreference.setChecked(localChannel.isBrowsable());
                    localSwitchPreference.setPersistent(false);
                    localSwitchPreference.setOnPreferenceChangeListener(this);
                    localPreferenceScreen.addPreference(localSwitchPreference);
                }
            }
        } while (this.mLoggedOpenEvent);
        logEvent("open_manage_app_channels");
        this.mLoggedOpenEvent = true;
    }

    public void onCreatePreferences(Bundle paramBundle, String paramString) {
        paramString = getPreferenceManager().getContext();
        PreferenceScreen localPreferenceScreen = getPreferenceManager().createPreferenceScreen(paramString);
        Bundle localBundle = getArguments();
        if (paramBundle != null) {
            this.mChannelAppKey = paramBundle.getString("channel_app");
            this.mAppName = paramBundle.getString("app_name");
        }
        for (; ; ) {
            localPreferenceScreen.setTitle(getString(2131493072, new Object[]{this.mAppName}));
            setPreferenceScreen(localPreferenceScreen);
            this.mTvDataManager = TvDataManager.getInstance(paramString);
            registerObserverAndUpdateDataIfNeeded();
            this.mStarted = true;
            return;
            if (localBundle != null) {
                this.mChannelAppKey = localBundle.getString("channel_app");
                this.mAppName = localBundle.getString("app_name");
            }
        }
    }

    public boolean onPreferenceChange(Preference paramPreference, Object paramObject) {
        SharedPreferences localSharedPreferences;
        if ((paramPreference.getKey() != null) && (paramPreference.getKey().startsWith("watch_next_package_key_prefix"))) {
            paramObject = (Boolean) paramObject;
            localSharedPreferences = getContext().getSharedPreferences("com.google.android.tvlauncher.data.TvDataManager.WATCH_NEXT_PREF_FILE_NAME", 0);
            if (((Boolean) paramObject).booleanValue()) {
                localSharedPreferences.edit().remove(paramPreference.getKey()).apply();
            }
        }
        do {
            return true;
            localSharedPreferences.edit().putBoolean(paramPreference.getKey(), false).apply();
            return true;
            paramObject = (Boolean) paramObject;
        } while (paramObject == null);
        int i = Integer.parseInt(paramPreference.getKey());
        this.mTvDataManager.setChannelBrowsable(i, ((Boolean) paramObject).booleanValue());
        if (((Boolean) paramObject).booleanValue()) {
        }
        for (paramPreference = "add_channel"; ; paramPreference = "remove_channel") {
            logEvent(paramPreference);
            return true;
        }
    }

    public void onSaveInstanceState(Bundle paramBundle) {
        super.onSaveInstanceState(paramBundle);
        if (this.mChannelAppKey != null) {
            paramBundle.putString("channel_app", this.mChannelAppKey);
        }
        if (this.mAppName != null) {
            paramBundle.putString("app_name", this.mAppName);
        }
    }

    public void onStart() {
        super.onStart();
        if (!this.mStarted) {
            registerObserverAndUpdateDataIfNeeded();
            this.mStarted = true;
        }
    }

    public void onStop() {
        super.onStop();
        if (this.mStarted) {
            this.mTvDataManager.unregisterPackageChannelsObserver(this.mChannelsObserver);
            this.mStarted = false;
        }
    }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/settings/AppChannelEnableChannelFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */