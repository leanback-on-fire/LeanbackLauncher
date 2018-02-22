package com.rockon999.android.leanbacklauncher.apps;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v17.leanback.app.GuidedStepFragment;
import android.support.v17.leanback.widget.GuidanceStylist.Guidance;
import android.support.v17.leanback.widget.GuidedAction;

import com.rockon999.android.leanbacklauncher.R;
import com.rockon999.android.leanbacklauncher.util.FireTVUtils;

import java.util.List;

/**
 * TODO: Javadoc
 */
public class AppInfoFragment extends GuidedStepFragment {

    private static final int ACTION_ID_IN_STORE = 1;
    private static final int ACTION_ID_SETTINGS = ACTION_ID_IN_STORE + 1;
    private static final int ACTION_ID_NOTIFICATION_SETTINGS = ACTION_ID_SETTINGS + 1;

    public static AppInfoFragment newInstance(String title, String pkg, Drawable icon) {
        Bundle args = new Bundle();
        AppInfoFragment fragment = new AppInfoFragment();
        fragment.icon = icon;
        fragment.title = title;
        fragment.pkg = pkg;
        fragment.setArguments(args);
        return fragment;
    }

    private Drawable icon;
    private String title, pkg;

    @NonNull
    @Override
    public Guidance onCreateGuidance(Bundle savedInstanceState) {
        return new Guidance(title,
                pkg,
                "",
                icon);
    }

    @Override
    public void onCreateActions(@NonNull List<GuidedAction> actions, Bundle savedInstanceState) {
        GuidedAction action = new GuidedAction.Builder(getActivity().getApplicationContext())
                .id(ACTION_ID_IN_STORE)
                .title(getString(R.string.app_info_in_store)).build();
        actions.add(action);
        action = new GuidedAction.Builder(getActivity().getApplicationContext())
                .id(ACTION_ID_SETTINGS)
                .title(getString(R.string.app_info_settings)).build();
        actions.add(action);
        //action = new GuidedAction.Builder(getActivity().getApplicationContext())
        //        .id(ACTION_ID_NOTIFICATION_SETTINGS)
        //        .title(getString(R.string.app_info_notification_settings)).build();
        //actions.add(action);
    }

    @Override
    public void onGuidedActionClicked(GuidedAction action) {
        if (action.getId() == ACTION_ID_IN_STORE) {
            FireTVUtils.openAppInAmazonStore(getActivity(), pkg);
        } else if (action.getId() == ACTION_ID_SETTINGS) {
            FireTVUtils.startAppSettings(getActivity(), pkg);
        } else if (action.getId() == ACTION_ID_NOTIFICATION_SETTINGS) {
            //FireTVUtils.openNotificationPreferences(getActivity(), pkg);
        }
        getActivity().finish();
    }
}