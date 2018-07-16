package com.rockon999.android.leanbacklauncher.settings;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v17.leanback.app.GuidedStepSupportFragment;
import android.support.v17.leanback.widget.GuidanceStylist;
import android.support.v17.leanback.widget.GuidedAction;
import android.support.v4.content.res.ResourcesCompat;
import android.text.InputType;

import com.rockon999.android.firetv.leanbacklauncher.apps.AppCategory;
import com.rockon999.android.firetv.leanbacklauncher.apps.RowPreferences;
import com.rockon999.android.firetv.leanbacklauncher.util.SharedPreferencesUtil;
import com.rockon999.android.leanbacklauncher.R;
import com.rockon999.android.leanbacklauncher.apps.AppsManager;

import java.util.List;
import java.util.Set;

/**
 * Created by rockon999 on 3/25/18.
 */

public class LegacyAppRowPreferenceFragment extends GuidedStepSupportFragment {
    @NonNull
    public GuidanceStylist.Guidance onCreateGuidance(Bundle savedInstanceState) {
        return new GuidanceStylist.Guidance(getString(R.string.select_app_order_action_title), null, getString(R.string.home_screen_order_content_title), ResourcesCompat.getDrawable(getResources(), R.drawable.ic_settings_home, null));
    }

    private int musicIndex, gameIndex, videoIndex, favIndex, allIndex;

    public void onCreateActions(@NonNull List<GuidedAction> actions, Bundle savedInstanceState) {
        boolean z = true;
        Activity activity = getActivity();

        Set<AppCategory> categories = RowPreferences.getEnabledCategories(activity);

        int i = 0;

        actions.add(new GuidedAction.Builder(activity).id(++i).title(R.string.music_row_title).build());
        actions.add(new GuidedAction.Builder(activity).id(++i).checkSetId(i).checked(categories.contains(AppCategory.MUSIC)).title(R.string.enable_music_row_title).description("").build());


        int[] constraints = RowPreferences.getRowConstraints(AppCategory.MUSIC, activity);

        actions.add(new GuidedAction.Builder(activity).id(++i).title(R.string.max_rows_title).description(Integer.toString(constraints[1])).descriptionEditable(true).descriptionEditInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED).build());
        actions.add(new GuidedAction.Builder(activity).id(++i).title(R.string.min_rows_title).description(Integer.toString(constraints[0])).descriptionEditable(true).descriptionEditInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED).build());

        musicIndex = ((i - 1) / 4);

        actions.add(new GuidedAction.Builder(activity).id(++i).title(R.string.videos_row_title).build());
        actions.add(new GuidedAction.Builder(activity).id(++i).checkSetId(i).checked(categories.contains(AppCategory.VIDEO)).title(R.string.enable_videos_row_title).description("").build());

        constraints = RowPreferences.getRowConstraints(AppCategory.VIDEO, activity);

        actions.add(new GuidedAction.Builder(activity).id(++i).title(R.string.max_rows_title).description(Integer.toString(constraints[1])).descriptionEditable(true).descriptionEditInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED).build());
        actions.add(new GuidedAction.Builder(activity).id(++i).title(R.string.min_rows_title).description(Integer.toString(constraints[0])).descriptionEditable(true).descriptionEditInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED).build());


        videoIndex = ((i - 1) / 4);

        actions.add(new GuidedAction.Builder(activity).id(++i).title(R.string.games_row_title).build());
        actions.add(new GuidedAction.Builder(activity).id(++i).checkSetId(i).checked(categories.contains(AppCategory.GAME)).title(R.string.enable_games_row_title).description("").build());

        constraints = RowPreferences.getRowConstraints(AppCategory.GAME, activity);

        actions.add(new GuidedAction.Builder(activity).id(++i).title(R.string.max_rows_title).description(Integer.toString(constraints[1])).descriptionEditable(true).descriptionEditInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED).build());
        actions.add(new GuidedAction.Builder(activity).id(++i).title(R.string.min_rows_title).description(Integer.toString(constraints[0])).descriptionEditable(true).descriptionEditInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED).build());

        gameIndex = ((i - 1) / 4);

        actions.add(new GuidedAction.Builder(activity).id(++i).title(R.string.favorites_row_title).build());
        actions.add(new GuidedAction.Builder(activity).id(++i).checkSetId(i).checked(RowPreferences.areFavoritesEnabled(activity)).title(R.string.enable_favorites_row_title).description("").build());

        constraints = RowPreferences.getFavoriteRowConstraints(activity);

        actions.add(new GuidedAction.Builder(activity).id(++i).title(R.string.max_rows_title).description(Integer.toString(constraints[1])).descriptionEditable(true).descriptionEditInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED).build());
        actions.add(new GuidedAction.Builder(activity).id(++i).title(R.string.min_rows_title).description(Integer.toString(constraints[0])).descriptionEditable(true).descriptionEditInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED).build());

        favIndex = ((i - 1) / 4);

        actions.add(new GuidedAction.Builder(activity).id(++i).title(R.string.apps_row_title).build());

        constraints = RowPreferences.getAllAppsConstraints(activity);

        actions.add(new GuidedAction.Builder(activity).id(++i).title(R.string.max_rows_title).description(Integer.toString(constraints[1])).descriptionEditable(true).descriptionEditInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED).build());
        actions.add(new GuidedAction.Builder(activity).id(++i).title(R.string.min_rows_title).description(Integer.toString(constraints[0])).descriptionEditable(true).descriptionEditInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED).build());

        allIndex = (i / 4);
    }

    public void onGuidedActionClicked(GuidedAction action) {

        long id = action.getId();
        long modId = (id - 1) / 4;
        int subId = (int) ((id - 1) % 4);

        if (modId == musicIndex) {
            switch (subId) {
                case 0:
                    break;
                case 1:
                case 2:
                case 3:
            }
        } else if (modId == videoIndex) {
            switch (subId) {
                case 0:
                    break;
                case 1:
                case 2:
                case 3:
            }
        } else if (modId == gameIndex) {
            switch (subId) {
                case 0:
                    break;
                case 1:
                case 2:
                case 3:
            }
        } else if (modId == favIndex) {
            switch (subId) {
                case 0:
                    break;
                case 1:
                case 2:
                case 3:
            }
        } else if (modId == allIndex) {
            switch (subId) {
                case 0:
                    break;
                case 1:
                case 2:
            }
        }
    }
}
