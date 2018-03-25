package com.rockon999.android.leanbacklauncher.settings;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v14.preference.SwitchPreference;
import android.support.v17.preference.LeanbackPreferenceFragment;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.PreferenceViewHolder;
import android.view.View;
import android.widget.ImageView;
import com.rockon999.android.leanbacklauncher.R;

import java.util.List;

public class RecommendationsPreferenceFragment extends LeanbackPreferenceFragment implements OnPreferenceChangeListener, RecommendationsPreferenceManager.LoadRecommendationPackagesCallback {
    private RecommendationsPreferenceManager mPreferenceManager;

    private class AppSwitchPreference extends SwitchPreference {
        private Drawable mBanner;

        public AppSwitchPreference(Context context) {
            super(context);
            setLayoutResource(R.layout.leanback_banner_preference);
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
                banner.setVisibility(0);
                icon.setVisibility(8);
                return;
            }
            banner.setVisibility(8);
            icon.setVisibility(0);
        }
    }

    public void onCreatePreferences(Bundle bundle, String s) {
        PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(getPreferenceManager().getContext());
        screen.setTitle((int) R.string.recommendation_blacklist_action_title);
        screen.setSummary((int) R.string.recommendation_blacklist_content_description);
        setPreferenceScreen(screen);
        this.mPreferenceManager = new RecommendationsPreferenceManager(getActivity());
    }

    public void onResume() {
        super.onResume();
        this.mPreferenceManager.loadRecommendationsPackages(this);
    }

    public void onRecommendationPackagesLoaded(List<RecommendationsPreferenceManager.PackageInfo> packages) {
        if (isAdded()) {
            Context context = getPreferenceManager().getContext();
            PreferenceScreen screen = getPreferenceScreen();
            screen.removeAll();
            screen.setOrderingAsAdded(false);
            Preference text = new Preference(context);
            text.setLayoutResource(R.layout.pref_wall_of_text);
            text.setSelectable(false);
            text.setTitle((int) R.string.recommendation_blacklist_content_description);
            text.setOrder(0);
            screen.addPreference(text);
            for (RecommendationsPreferenceManager.PackageInfo packageInfo : packages) {
                boolean z;
                AppSwitchPreference switchPreference = new AppSwitchPreference(context);
                switchPreference.setLayoutResource(R.layout.leanback_banner_preference);
                switchPreference.setKey(packageInfo.packageName);
                switchPreference.setTitle(packageInfo.appTitle);
                switchPreference.setBanner(packageInfo.banner);
                switchPreference.setIcon(packageInfo.icon);
                if (packageInfo.blacklisted) {
                    z = false;
                } else {
                    z = true;
                }
                switchPreference.setChecked(z);
                switchPreference.setPersistent(false);
                switchPreference.setOnPreferenceChangeListener(this);
                screen.addPreference(switchPreference);
            }
        }
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Boolean optIn = (Boolean) newValue;
        this.mPreferenceManager.savePackageBlacklisted(preference.getKey(), !optIn.booleanValue());
        return true;
    }
}
