package com.amazon.tv.firetv.leanbacklauncher.apps;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.leanback.app.GuidedStepFragment;
import androidx.leanback.widget.GuidanceStylist.Guidance;
import androidx.leanback.widget.GuidedAction;

import com.amazon.tv.firetv.leanbacklauncher.util.FireTVUtils;
import com.amazon.tv.firetv.leanbacklauncher.util.SharedPreferencesUtil;
import com.amazon.tv.leanbacklauncher.R;

import java.util.ArrayList;

/**
 * TODO: Javadoc
 */
public class AppInfoFragment extends GuidedStepFragment {

    private static final int ACTION_ID_IN_STORE = 1;
    private static final int ACTION_ID_SETTINGS = ACTION_ID_IN_STORE + 1;
    private static final int ACTION_ID_FAVORITE = ACTION_ID_SETTINGS + 1;
    private static final int ACTION_ID_HIDE = ACTION_ID_FAVORITE + 1;

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

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Guidance onCreateGuidance(Bundle savedInstanceState) {
        return new Guidance(title,
                pkg,
                "",
                icon);
    }

    public void onResume() {
        super.onResume();
        this.updateActions();
    }

    @Override
    public void onGuidedActionClicked(GuidedAction action) {

        if (action.getId() == ACTION_ID_IN_STORE) {
            FireTVUtils.openAppInAmazonStore(getActivity(), pkg);

            getActivity().finish();
        } else if (action.getId() == ACTION_ID_SETTINGS) {
            FireTVUtils.startAppSettings(getActivity(), pkg);

            getActivity().finish();
        } else {
            Context context = getActivity().getApplicationContext();
            SharedPreferencesUtil util = SharedPreferencesUtil.instance(context);

            if (action.getId() == ACTION_ID_FAVORITE) {
                boolean favorited = !util.isFavorite(pkg);

                if (favorited) {
                    util.favorite(pkg);
                } else {
                    util.unfavorite(pkg);
                }
            } else if (action.getId() == ACTION_ID_HIDE) {
                boolean hidden = !util.isHidden(pkg);

                if (hidden) {
                    util.hide(pkg);
                } else {
                    util.unhide(pkg);
                }
            }
        }

        updateActions();
    }

    private void updateActions() {
        Context context = getActivity().getApplicationContext();

        SharedPreferencesUtil util = SharedPreferencesUtil.instance(context);

        ArrayList<GuidedAction> actions = new ArrayList<>();

        String favlabel = (!util.isFavorite(pkg)) ? getString(R.string.app_info_add_favorites) : getString(R.string.app_info_rem_favorites);
        String hidelabel = (!util.isHidden(pkg)) ? getString(R.string.app_info_hide_app) : getString(R.string.app_info_unhide_app);

        GuidedAction action = new GuidedAction.Builder(context)
                .id(ACTION_ID_IN_STORE)
                .title(getString(R.string.app_info_in_store)).build();
        if (FireTVUtils.isAmazonStoreInstalled(context))
            actions.add(action);
        action = new GuidedAction.Builder(context)
                .id(ACTION_ID_SETTINGS)
                .title(getString(R.string.app_info_settings)).build();
        actions.add(action);
        action = new GuidedAction.Builder(context)
                .id(ACTION_ID_FAVORITE)
                .title(favlabel).build();
        actions.add(action);
        action = new GuidedAction.Builder(context)
                .id(ACTION_ID_HIDE)
                .title(hidelabel).build();
        actions.add(action);

        setActions(actions); // APPLY
    }
}
