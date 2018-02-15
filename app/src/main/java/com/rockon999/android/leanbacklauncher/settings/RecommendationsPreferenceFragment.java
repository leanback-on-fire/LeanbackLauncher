package com.rockon999.android.leanbacklauncher.settings;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v14.preference.SwitchPreference;
import android.support.v17.preference.LeanbackPreferenceFragment;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.PreferenceViewHolder;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.rockon999.android.leanbacklauncher.R;

import java.util.ArrayList;
import java.util.Arrays;

public class RecommendationsPreferenceFragment extends LeanbackPreferenceFragment implements BlacklistManager.LoadRecommendationPackagesCallback, OnPreferenceChangeListener {
    private ArrayList<String> mBlacklist;
    private BlacklistManager mBlacklistManager;

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
        screen.setTitle(R.string.recommendation_blacklist_action_title);
        screen.setSummary(R.string.recommendation_blacklist_content_description);
        setPreferenceScreen(screen);
        this.mBlacklistManager = new BlacklistManager(getActivity());
        this.mBlacklistManager.loadRecommendationPackages(this);
    }

    public void onRecommendationPackagesLoaded(String[] recommendationPackaged, String[] blacklistedPackages) {
        this.mBlacklist = new ArrayList(Arrays.asList(blacklistedPackages));
        Context context = getPreferenceManager().getContext();
        PackageManager pm = context.getPackageManager();
        PreferenceScreen screen = getPreferenceScreen();
        screen.removeAll();
        screen.setOrderingAsAdded(false);
        Preference preference = new Preference(context);
        preference.setLayoutResource(R.layout.pref_wall_of_text);
        preference.setSelectable(false);
        preference.setTitle(R.string.recommendation_blacklist_content_description);
        preference.setOrder(0);
        screen.addPreference(preference);
        for (String packageName : recommendationPackaged) {
            CharSequence charSequence = null;
            Drawable drawable = null;
            Drawable icon = null;
            try {
                ApplicationInfo info = pm.getApplicationInfo(packageName, 0);
                Resources res = pm.getResourcesForApplication(packageName);
                charSequence = pm.getApplicationLabel(info);
                if (info.banner != 0) {
                    drawable = res.getDrawable(info.banner, null);
                } else {
                    Intent intent = new Intent();
                    intent.addCategory("android.intent.category.LEANBACK_LAUNCHER");
                    intent.setAction("android.intent.action.MAIN");
                    intent.setPackage(packageName);
                    ResolveInfo resolveInfo = context.getPackageManager().resolveActivity(intent, 0);
                    if (!(resolveInfo == null || resolveInfo.activityInfo == null)) {
                        if (resolveInfo.activityInfo.banner != 0) {
                            drawable = res.getDrawable(resolveInfo.activityInfo.banner, null);
                        }
                    }
                }
                if (drawable == null && info.icon != 0) {
                    icon = res.getDrawable(info.icon, null);
                }
            } catch (NameNotFoundException e) {
            }
            if (TextUtils.isEmpty(charSequence)) {
                charSequence = packageName;
            }
            if (drawable == null && icon == null) {
                //icon = getResources().getDrawable(17301651, null);
                icon = getResources().getDrawable(R.drawable.bg_default, null);
            }
            AppSwitchPreference switchPreference = new AppSwitchPreference(context);
            switchPreference.setLayoutResource(R.layout.leanback_banner_preference);
            switchPreference.setKey(packageName);
            switchPreference.setTitle(charSequence);
            switchPreference.setBanner(drawable);
            switchPreference.setIcon(icon);
            switchPreference.setChecked(!this.mBlacklist.contains(packageName));
            switchPreference.setPersistent(false);
            switchPreference.setOnPreferenceChangeListener(this);
            screen.addPreference(switchPreference);
        }
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Boolean optIn = (Boolean) newValue;
        String packageName = preference.getKey();
        if (optIn.booleanValue()) {
            this.mBlacklist.remove(packageName);
        } else {
            this.mBlacklist.add(packageName);
        }
        this.mBlacklistManager.saveEntityBlacklist(this.mBlacklist);
        return true;
    }
}
