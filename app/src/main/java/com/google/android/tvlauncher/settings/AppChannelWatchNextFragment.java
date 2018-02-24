package com.google.android.tvlauncher.settings;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v14.preference.SwitchPreference;
import android.support.v17.preference.LeanbackPreferenceFragment;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.PreferenceViewHolder;
import android.view.View;
import android.widget.ImageView;

import com.google.android.tvlauncher.R;
import com.google.android.tvlauncher.analytics.FragmentEventLogger;
import com.google.android.tvlauncher.analytics.LogEvents;
import com.google.android.tvlauncher.analytics.UserActionEvent;
import com.google.android.tvlauncher.data.TvDataManager;

import java.util.Collections;
import java.util.List;

public class AppChannelWatchNextFragment extends LeanbackPreferenceFragment implements AppModel.LoadAppsCallback, OnPreferenceChangeListener {
    private AppModel mAppModel;
    private final FragmentEventLogger mEventLogger = new FragmentEventLogger(this);
    private PreferenceCategory mSourceGroup;

    private class AppPreference extends SwitchPreference {
        private Drawable mBanner;

        AppPreference(Context context) {
            super(context);
            setLayoutResource(R.layout.appchannel_app_banner);
        }

        public void setBanner(Drawable banner) {
            this.mBanner = banner;
        }

        public void onBindViewHolder(PreferenceViewHolder holder) {
            super.onBindViewHolder(holder);
            View icon = holder.findViewById(R.id.icon_frame);
            ImageView banner = (ImageView) holder.findViewById(R.id.banner);
            if (this.mBanner != null) {
                banner.setImageDrawable(this.mBanner);
                banner.setVisibility(View.VISIBLE);
                icon.setVisibility(View.GONE);
                return;
            }
            banner.setVisibility(View.GONE);
            icon.setVisibility(View.VISIBLE);
        }
    }

    public static Fragment newInstance() {
        return new AppChannelWatchNextFragment();
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Boolean showInWatchNext = (Boolean) newValue;
        SharedPreferences prefs = getContext().getSharedPreferences(TvDataManager.WATCH_NEXT_PREF_FILE_NAME, 0);
        if (preference.getKey() == null || !preference.getKey().startsWith(TvDataManager.WATCH_NEXT_PACKAGE_KEY_PREFIX)) {
            if (!TvDataManager.SHOW_WATCH_NEXT_ROW_KEY.equals(preference.getKey())) {
                return false;
            }
            prefs.edit().putBoolean(preference.getKey(), showInWatchNext.booleanValue()).apply();
            this.mSourceGroup.setEnabled(showInWatchNext.booleanValue());
            return true;
        } else if (showInWatchNext.booleanValue()) {
            prefs.edit().remove(preference.getKey()).apply();
            return true;
        } else {
            prefs.edit().putBoolean(preference.getKey(), false).apply();
            return true;
        }
    }

    public void onResume() {
        super.onResume();
        this.mAppModel.loadApps(this);
    }

    public void onPause() {
        super.onPause();
        this.mAppModel.onPause();
    }

    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        Context preferenceContext = getPreferenceManager().getContext();
        PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(preferenceContext);
        screen.setTitle((int) R.string.watch_next_settings_panel_title);
        setPreferenceScreen(screen);
        this.mSourceGroup = new PreferenceCategory(preferenceContext);
        this.mSourceGroup.setTitle((int) R.string.watch_next_sources_title);
        createWatchNextPreference();
        screen.addPreference(this.mSourceGroup);
        this.mAppModel = new AppModel(preferenceContext);
    }

    private void createWatchNextPreference() {
        PreferenceScreen screen = getPreferenceScreen();
        SwitchPreference switchPreference = new SwitchPreference(getPreferenceManager().getContext());
        switchPreference.setLayoutResource(R.layout.watch_next_preference);
        switchPreference.setKey(TvDataManager.SHOW_WATCH_NEXT_ROW_KEY);
        switchPreference.setTitle((int) R.string.show_watch_next_row);
        switchPreference.setPersistent(false);
        SharedPreferences sharedPrefs = getContext().getSharedPreferences(TvDataManager.WATCH_NEXT_PREF_FILE_NAME, 0);
        if (sharedPrefs != null) {
            boolean enabled = sharedPrefs.getBoolean(TvDataManager.SHOW_WATCH_NEXT_ROW_KEY, true);
            switchPreference.setChecked(enabled);
            this.mSourceGroup.setEnabled(enabled);
        } else {
            switchPreference.setChecked(true);
            this.mSourceGroup.setEnabled(true);
        }
        switchPreference.setOnPreferenceChangeListener(this);
        screen.addPreference(switchPreference);
    }

    public void onAppsLoaded(List<AppModel.AppInfo> applicationInfos) {
        if (isAdded()) {
            Context context = getPreferenceManager().getContext();
            this.mSourceGroup.removeAll();
            Preference summaryText = new Preference(context);
            summaryText.setTitle((int) R.string.watch_next_sources_summary_message);
            summaryText.setSelectable(false);
            this.mSourceGroup.addPreference(summaryText);
            if (applicationInfos != null && applicationInfos.size() > 0) {
                Collections.sort(applicationInfos);
                for (AppModel.AppInfo applicationInfo : applicationInfos) {
                    AppPreference appPreference = new AppPreference(context);
                    String key = TvDataManager.WATCH_NEXT_PACKAGE_KEY_PREFIX.concat(applicationInfo.mPackageName);
                    appPreference.setKey(key);
                    appPreference.setTitle(applicationInfo.mTitle);
                    appPreference.setBanner(applicationInfo.mBanner);
                    appPreference.setIcon(applicationInfo.mIcon);
                    SharedPreferences preferences = getContext().getSharedPreferences(TvDataManager.WATCH_NEXT_PREF_FILE_NAME, 0);
                    if (preferences == null || !preferences.contains(key)) {
                        appPreference.setChecked(true);
                    } else {
                        appPreference.setChecked(false);
                    }
                    appPreference.setPersistent(false);
                    this.mSourceGroup.addPreference(appPreference);
                    appPreference.setOnPreferenceChangeListener(this);
                }
            }
            this.mEventLogger.log(new UserActionEvent(LogEvents.OPEN_MANAGE_CHANNELS));
        }
    }

    public void onAppsChanged() {
        this.mAppModel.loadApps(this);
    }
}
