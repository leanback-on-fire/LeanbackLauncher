package com.google.android.tvlauncher.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v14.preference.SwitchPreference;
import android.support.v17.preference.LeanbackPreferenceFragment;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.preference.PreferenceScreen;
import com.google.android.tvlauncher.R;
import com.google.android.tvlauncher.analytics.FragmentEventLogger;
import com.google.android.tvlauncher.analytics.LogEvents;
import com.google.android.tvlauncher.analytics.UserActionEvent;
import com.google.android.tvlauncher.data.PackageChannelsObserver;
import com.google.android.tvlauncher.data.TvDataManager;
import com.google.android.tvlauncher.model.Channel;
import java.util.Collections;
import java.util.List;

public class AppChannelEnableChannelFragment extends LeanbackPreferenceFragment implements OnPreferenceChangeListener {
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

    public void onCreatePreferences(Bundle instanceStateBundle, String s) {
        Context preferenceContext = getPreferenceManager().getContext();
        PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(preferenceContext);
        Bundle args = getArguments();
        if (instanceStateBundle != null) {
            this.mChannelAppKey = instanceStateBundle.getString(CHANNEL_APP_KEY);
            this.mAppName = instanceStateBundle.getString(APP_NAME_KEY);
        } else if (args != null) {
            this.mChannelAppKey = args.getString(CHANNEL_APP_KEY);
            this.mAppName = args.getString(APP_NAME_KEY);
        }
        screen.setTitle(getString(R.string.select_channels_title_with_app_name, new Object[]{this.mAppName}));
        setPreferenceScreen(screen);
        this.mTvDataManager = TvDataManager.getInstance(preferenceContext);
        registerObserverAndUpdateDataIfNeeded();
        this.mStarted = true;
    }

    private void registerObserverAndUpdateDataIfNeeded() {
        this.mTvDataManager.registerPackageChannelsObserver(this.mChannelsObserver);
        if (this.mChannelAppKey != null) {
            loadChannels();
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

    public void onSaveInstanceState(Bundle instanceStateBundle) {
        super.onSaveInstanceState(instanceStateBundle);
        if (this.mChannelAppKey != null) {
            instanceStateBundle.putString(CHANNEL_APP_KEY, this.mChannelAppKey);
        }
        if (this.mAppName != null) {
            instanceStateBundle.putString(APP_NAME_KEY, this.mAppName);
        }
    }

    public void onChannelsLoaded() {
        if (isAdded()) {
            PreferenceScreen screen = getPreferenceScreen();
            screen.removeAll();
            List<Channel> channels = this.mTvDataManager.getPackageChannels(this.mChannelAppKey);
            if (channels != null && channels.size() > 0) {
                Collections.sort(channels);
                for (Channel channel : channels) {
                    SwitchPreference switchPreference = new SwitchPreference(getPreferenceManager().getContext());
                    switchPreference.setLayoutResource(R.layout.appchannel_channel_banner);
                    switchPreference.setKey(Long.toString(channel.getId()));
                    switchPreference.setTitle(channel.getDisplayName());
                    switchPreference.setChecked(channel.isBrowsable());
                    switchPreference.setPersistent(false);
                    switchPreference.setOnPreferenceChangeListener(this);
                    screen.addPreference(switchPreference);
                }
            }
            if (!this.mLoggedOpenEvent) {
                logEvent(LogEvents.OPEN_MANAGE_APP_CHANNELS);
                this.mLoggedOpenEvent = true;
            }
        }
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference.getKey() == null || !preference.getKey().startsWith(TvDataManager.WATCH_NEXT_PACKAGE_KEY_PREFIX)) {
            Boolean browsable = (Boolean) newValue;
            if (browsable != null) {
                this.mTvDataManager.setChannelBrowsable((long) Integer.parseInt(preference.getKey()), browsable.booleanValue());
                logEvent(browsable.booleanValue() ? LogEvents.ADD_CHANNEL : LogEvents.REMOVE_CHANNEL);
            }
        } else {
            Boolean showInWatchNext = (Boolean) newValue;
            SharedPreferences prefs = getContext().getSharedPreferences(TvDataManager.WATCH_NEXT_PREF_FILE_NAME, 0);
            if (showInWatchNext.booleanValue()) {
                prefs.edit().remove(preference.getKey()).apply();
            } else {
                prefs.edit().putBoolean(preference.getKey(), false).apply();
            }
        }
        return true;
    }

    private void loadChannels() {
        if (this.mTvDataManager.isPackageChannelDataLoaded(this.mChannelAppKey)) {
            onChannelsLoaded();
        } else {
            this.mTvDataManager.loadPackageChannelsData(this.mChannelAppKey);
        }
    }

    private void logEvent(String name) {
        UserActionEvent event = new UserActionEvent(name);
        List<Channel> channels = this.mTvDataManager.getPackageChannels(this.mChannelAppKey);
        int totalChannels = channels != null ? channels.size() : 0;
        int browsableChannels = 0;
        if (totalChannels != 0) {
            for (Channel channel : channels) {
                if (channel.isBrowsable()) {
                    browsableChannels++;
                }
            }
        }
        event.putParameter("package_name", this.mChannelAppKey).putParameter(LogEvents.PARAMETER_CHANNEL_COUNT, totalChannels).putParameter(LogEvents.PARAMETER_BROWSABLE_CHANNEL_COUNT, browsableChannels);
        this.mEventLogger.log(event);
    }
}
