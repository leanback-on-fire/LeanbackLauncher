package com.google.android.tvlauncher.settings;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v14.preference.SwitchPreference;
import android.support.v17.preference.LeanbackPreferenceFragment;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import com.google.android.tvlauncher.R;
import com.google.android.tvlauncher.appsview.AppsViewActivity;
import com.google.android.tvlauncher.data.TvDataManager;
import com.google.android.tvlauncher.util.BuildType;

public class HomeScreenPreferenceFragment extends LeanbackPreferenceFragment implements OnPreferenceChangeListener {
    private static final String REORDER_APPS_KEY = "reorderapps";
    private static final String REORDER_GAMES_KEY = "reordergames";

    public static Fragment newInstance() {
        return new HomeScreenPreferenceFragment();
    }

    public void onCreatePreferences(Bundle bundle, String s) {
        Context preferenceContext = getPreferenceManager().getContext();
        PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(preferenceContext);
        screen.setTitle((int) R.string.settings_dialog_title);
        createGuideViewPreference(screen, preferenceContext);
        createAppsViewPreference(screen, preferenceContext);
        if (BuildType.DOGFOOD.booleanValue()) {
            screen.addPreference((Preference) BuildType.newInstance(Preference.class, "com.google.android.tvlauncher.settings.DogfoodPreference", preferenceContext));
        } else if (BuildType.DEBUG.booleanValue()) {
            screen.addPreference((Preference) BuildType.newInstance(Preference.class, "com.google.android.tvlauncher.settings.DebugPreference", preferenceContext));
        }
        setPreferenceScreen(screen);
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        SharedPreferences prefs = getContext().getSharedPreferences(TvDataManager.PREVIEW_VIDEO_PREF_FILE_NAME, 0);
        if (TvDataManager.SHOW_PREVIEW_VIDEO_KEY.equals(preference.getKey())) {
            Boolean showPreviewVideo = (Boolean) newValue;
            prefs.edit().putBoolean(preference.getKey(), showPreviewVideo.booleanValue()).apply();
            ((SwitchPreference) getPreferenceScreen().findPreference(TvDataManager.ENABLE_PREVIEW_AUDIO_KEY)).setEnabled(showPreviewVideo.booleanValue());
            return true;
        } else if (!TvDataManager.ENABLE_PREVIEW_AUDIO_KEY.equals(preference.getKey())) {
            return false;
        } else {
            prefs.edit().putBoolean(preference.getKey(), ((Boolean) newValue).booleanValue()).apply();
            return true;
        }
    }

    public boolean onPreferenceTreeClick(Preference preference) {
        String key = preference.getKey();
        if (REORDER_APPS_KEY.equals(key)) {
            AppsViewActivity.startAppsViewActivity(Integer.valueOf(0), getContext());
            return true;
        } else if (!REORDER_GAMES_KEY.equals(key)) {
            return super.onPreferenceTreeClick(preference);
        } else {
            AppsViewActivity.startAppsViewActivity(Integer.valueOf(1), getContext());
            return true;
        }
    }

    private void createAppsViewPreference(PreferenceScreen screen, Context preferenceContext) {
        PreferenceCategory appsCategory = new PreferenceCategory(preferenceContext);
        appsCategory.setTitle((int) R.string.apps_view_title);
        screen.addPreference(appsCategory);
        Preference reorderAppsPref = new Preference(preferenceContext);
        reorderAppsPref.setKey(REORDER_APPS_KEY);
        reorderAppsPref.setTitle((int) R.string.customize_app_order_action_title);
        screen.addPreference(reorderAppsPref);
        Preference reorderGamesPref = new Preference(preferenceContext);
        reorderGamesPref.setKey(REORDER_GAMES_KEY);
        reorderGamesPref.setTitle((int) R.string.customize_game_order_action_title);
        screen.addPreference(reorderGamesPref);
        setPreferenceScreen(screen);
    }

    private void createGuideViewPreference(PreferenceScreen screen, Context preferenceContext) {
        PreferenceCategory guideCategory = new PreferenceCategory(preferenceContext);
        guideCategory.setTitle((int) R.string.guide_view_title);
        screen.addPreference(guideCategory);
        Preference channelsPreference = new Preference(getPreferenceManager().getContext());
        channelsPreference.setTitle((int) R.string.add_channels_title);
        channelsPreference.setFragment(AppChannelSelectAppFragment.class.getName());
        channelsPreference.setPersistent(false);
        screen.addPreference(channelsPreference);
        SwitchPreference previewVideoEnablePref = new SwitchPreference(preferenceContext);
        previewVideoEnablePref.setKey(TvDataManager.SHOW_PREVIEW_VIDEO_KEY);
        previewVideoEnablePref.setTitle((int) R.string.home_screen_preview_video_enable);
        SharedPreferences preferences = getContext().getSharedPreferences(TvDataManager.PREVIEW_VIDEO_PREF_FILE_NAME, 0);
        previewVideoEnablePref.setChecked(preferences.getBoolean(TvDataManager.SHOW_PREVIEW_VIDEO_KEY, true));
        previewVideoEnablePref.setPersistent(false);
        previewVideoEnablePref.setOnPreferenceChangeListener(this);
        screen.addPreference(previewVideoEnablePref);
        SwitchPreference previewAudioEnablePref = new SwitchPreference(preferenceContext);
        previewAudioEnablePref.setKey(TvDataManager.ENABLE_PREVIEW_AUDIO_KEY);
        previewAudioEnablePref.setTitle((int) R.string.home_screen_preview_audio_enable);
        previewAudioEnablePref.setChecked(preferences.getBoolean(TvDataManager.ENABLE_PREVIEW_AUDIO_KEY, true));
        previewAudioEnablePref.setPersistent(false);
        previewAudioEnablePref.setOnPreferenceChangeListener(this);
        screen.addPreference(previewAudioEnablePref);
    }
}
