package com.google.android.tvlauncher.settings;

import android.app.Fragment;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v17.preference.LeanbackPreferenceFragment;
import android.support.v7.preference.Preference;
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

public class AppChannelSelectAppFragment extends LeanbackPreferenceFragment implements AppModel.LoadAppsCallback {
    private AppModel mAppModel;
    private final FragmentEventLogger mEventLogger = new FragmentEventLogger(this);

    private class AppPreference extends Preference {
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
        return new AppChannelSelectAppFragment();
    }

    public void onCreatePreferences(Bundle bundle, String s) {
        PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(getPreferenceManager().getContext());
        screen.setTitle((int) R.string.add_channels_title);
        setPreferenceScreen(screen);
        this.mAppModel = new AppModel(getPreferenceManager().getContext());
    }

    public void onResume() {
        super.onResume();
        this.mAppModel.loadApps(this);
    }

    public void onPause() {
        super.onPause();
        this.mAppModel.onPause();
    }

    public void onAppsLoaded(List<AppModel.AppInfo> applicationInfos) {
        if (isAdded()) {
            Context context = getPreferenceManager().getContext();
            PreferenceScreen screen = getPreferenceScreen();
            screen.removeAll();
            createWatchNextPreference();
            if (applicationInfos != null && applicationInfos.size() > 0) {
                Collections.sort(applicationInfos);
                for (AppModel.AppInfo applicationInfo : applicationInfos) {
                    AppPreference appPreference = new AppPreference(context);
                    appPreference.setKey(applicationInfo.mPackageName);
                    appPreference.setTitle(applicationInfo.mTitle);
                    appPreference.setBanner(applicationInfo.mBanner);
                    appPreference.setIcon(applicationInfo.mIcon);
                    appPreference.setPersistent(false);
                    appPreference.setSummary(context.getResources().getQuantityString(R.plurals.app_channel_count, applicationInfo.mNumberOfChannels, new Object[]{Integer.valueOf(applicationInfo.mNumberOfChannels)}));
                    screen.addPreference(appPreference);
                    appPreference.setFragment(AppChannelEnableChannelFragment.class.getName());
                    Bundle extras = appPreference.getExtras();
                    extras.putString("channel_app", applicationInfo.mPackageName);
                    extras.putString("app_name", applicationInfo.mTitle.toString());
                }
            }
            this.mEventLogger.log(new UserActionEvent(LogEvents.OPEN_MANAGE_CHANNELS));
        }
    }

    private void createWatchNextPreference() {
        PreferenceScreen screen = getPreferenceScreen();
        Preference preference = new Preference(getPreferenceManager().getContext());
        preference.setLayoutResource(R.layout.watch_next_preference);
        preference.setKey(TvDataManager.SHOW_WATCH_NEXT_ROW_KEY);
        preference.setTitle((int) R.string.watch_next_setting_title);
        preference.setFragment(AppChannelWatchNextFragment.class.getName());
        preference.setPersistent(false);
        screen.addPreference(preference);
    }

    public void onAppsChanged() {
        this.mAppModel.loadApps(this);
    }
}
