package com.rockon999.android.leanbacklauncher.settings;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v17.leanback.app.GuidedStepSupportFragment;
import android.support.v17.leanback.widget.GuidanceStylist;
import android.support.v17.leanback.widget.GuidedAction;
import android.support.v4.content.res.ResourcesCompat;
import android.text.InputType;
import android.util.Log;

import com.rockon999.android.firetv.leanbacklauncher.apps.AppCategory;
import com.rockon999.android.firetv.leanbacklauncher.apps.RowPreferences;
import com.rockon999.android.firetv.leanbacklauncher.util.SharedPreferencesUtil;
import com.rockon999.android.leanbacklauncher.R;
import com.rockon999.android.leanbacklauncher.apps.AppsManager;

import java.lang.Integer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by rockon999 on 3/25/18.
 */

public class LegacyAppRowPreferenceFragment extends GuidedStepSupportFragment {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    public GuidanceStylist.Guidance onCreateGuidance(Bundle savedInstanceState) {
        return new GuidanceStylist.Guidance(getString(R.string.select_app_customize_rows_title), null, getString(R.string.home_screen_order_content_title), ResourcesCompat.getDrawable(getResources(), R.drawable.ic_settings_home, null));
    }
    
    public void onResume() {
        super.onResume();
        this.updateActions();
    }

    private int musicIndex, gameIndex, videoIndex, favIndex, allIndex;

    public void onGuidedActionClicked(GuidedAction action) {

        long id = action.getId();
        long modId = (id - 1) / 4;
        int subId = (int) ((id - 1) % 4);
        int val = 0;
        boolean enabled = false;
        
        Activity activity = getActivity();
        Set<AppCategory> categories = RowPreferences.getEnabledCategories(activity);

        Log.w("+++ action", action.toString());
        // Log.w("+++ action.id", ""+id);
        // Log.w("+++ categories", categories.toString());
        // Log.w("+++ modId", " "+modId);
        // Log.w("+++ subId", " "+subId);

        if (modId == favIndex) {
            switch (subId) {
                case 0:
                    break;
                case 1:
                    enabled = RowPreferences.areFavoritesEnabled(activity);
                    // Log.w("+++ favorites.enabled", ""+enabled);
                    RowPreferences.setFavoritesEnabled(activity, !enabled);
                    break;
                case 2:
                    val = Integer.parseInt(action.getDescription().toString());
                    Log.w("+++ setFavoriteRowMax", "" + val);
                	RowPreferences.setFavoriteRowMax(activity, val);
                	break;
                case 3:
                    val = Integer.parseInt(action.getDescription().toString());
                    Log.w("+++ setFavoriteRowMin", "" + val);
                	RowPreferences.setFavoriteRowMin(activity, val);
            }
        } else if (modId == musicIndex) {
            switch (subId) {
                case 0:
                    break;
                case 1:
                    enabled = categories.contains(AppCategory.MUSIC);
                    // Log.w("+++ music.enabled", ""+enabled);
                    RowPreferences.setMusicEnabled(activity, !enabled);
                    break;
                case 2:
                    val = Integer.parseInt(action.getDescription().toString());
                    Log.w("+++ setRowMax", "" + val);
                	RowPreferences.setRowMax(AppCategory.MUSIC, activity, val);
                	break;
                case 3:
                    val = Integer.parseInt(action.getDescription().toString());
                    Log.w("+++ setRowMin", "" + val);
                	RowPreferences.setRowMin(AppCategory.MUSIC, activity, val);
            }
        } else if (modId == videoIndex) {
            switch (subId) {
                case 0:
                    break;
                case 1:
                    enabled = categories.contains(AppCategory.VIDEO);
                    // Log.w("+++ videos.enabled", ""+enabled);
                    RowPreferences.setVideosEnabled(activity, !enabled);
                    break;
                case 2:
                    val = Integer.parseInt(action.getDescription().toString());
                    Log.w("+++ setRowMax", "" + val);
                	RowPreferences.setRowMax(AppCategory.VIDEO, activity, val);
                	break;
                case 3:
                    val = Integer.parseInt(action.getDescription().toString());
                    Log.w("+++ setRowMin", "" + val);
                	RowPreferences.setRowMin(AppCategory.VIDEO, activity, val);
            }
        } else if (modId == gameIndex) {
            switch (subId) {
                case 0:
                    break;
                case 1:
                    enabled = categories.contains(AppCategory.GAME);
                    // Log.w("+++ games.enabled", ""+enabled);
                    RowPreferences.setGamesEnabled(activity, !enabled);
                    break;
                case 2:
                    val = Integer.parseInt(action.getDescription().toString());
                    Log.w("+++ setRowMax", "" + val);
                	RowPreferences.setRowMax(AppCategory.GAME, activity, val);
                	break;
                case 3:
                    val = Integer.parseInt(action.getDescription().toString());
                    Log.w("+++ setRowMin", "" + val);
                	RowPreferences.setRowMin(AppCategory.GAME, activity, val);
            }
        } else if (modId == allIndex) {
            switch (subId) {
                case 0:
                    break;
                case 1:
                    // Log.w("+++ apps.rows.max", ""+action.getDescription());
                    val = Integer.parseInt(action.getDescription().toString());
                    Log.w("+++ setAllAppsMax", "" + val);
                	RowPreferences.setAllAppsMax(activity, val);
                	break;
                case 2:
                    // Log.w("+++ apps.rows.min", ""+action.getDescription());
                    val = Integer.parseInt(action.getDescription().toString());
                    Log.w("+++ setAllAppsMin", "" + val);
                	RowPreferences.setAllAppsMin(activity, val);
            }
        } else if (id == 101) { // RECOMENDATIONS
            enabled = RowPreferences.areRecommendationsEnabled(activity);
            // Log.w("+++ recommendations.enabled", ""+enabled);
            RowPreferences.setRecommendationsEnabled(activity, !enabled);
        } else if (id == 201) { // INPUTS
            enabled = RowPreferences.areInputsEnabled(activity);
            // Log.w("+++ inputs.enabled", ""+enabled);
            RowPreferences.setInputsEnabled(activity, !enabled);
        }
        updateActions();
    }
    
//    @Override
//    public long onGuidedActionEditedAndProceed(GuidedAction action) {
//    
//        return GuidedAction.ACTION_ID_NEXT;
//    }

    private void updateActions() {

        Log.w("+++ updateActions()", "+++");
    
        ArrayList<GuidedAction> actions = new ArrayList<>();
        boolean z = true;
        Activity activity = getActivity();
        Set<AppCategory> categories = RowPreferences.getEnabledCategories(activity);
        int i = 0;

        // RECCOMENDATIONS
        actions.add(new GuidedAction.Builder(activity).id(100).title(R.string.recs_row_title).build());
        actions.add(new GuidedAction.Builder(activity).id(101).checkSetId(101).checked(RowPreferences.areRecommendationsEnabled(activity)).title(R.string.enable_recs_row_title).description("").build());
        
        // FAV
        actions.add(new GuidedAction.Builder(activity).id(++i).title(R.string.favorites_row_title).build());
        actions.add(new GuidedAction.Builder(activity).id(++i).checkSetId(i).checked(RowPreferences.areFavoritesEnabled(activity)).title(R.string.enable_favorites_row_title).description("").build());

        int[] constraints = RowPreferences.getFavoriteRowConstraints(activity);

        actions.add(new GuidedAction.Builder(activity).id(++i).title(R.string.max_rows_title).description(Integer.toString(constraints[1])).descriptionEditable(true).descriptionEditInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED).build());
        actions.add(new GuidedAction.Builder(activity).id(++i).title(R.string.min_rows_title).description(Integer.toString(constraints[0])).descriptionEditable(true).descriptionEditInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED).build());

        favIndex = ((i - 1) / 4);

        // MUSIC
        actions.add(new GuidedAction.Builder(activity).id(++i).title(R.string.music_row_title).build());
        actions.add(new GuidedAction.Builder(activity).id(++i).checkSetId(i).checked(categories.contains(AppCategory.MUSIC)).title(R.string.enable_music_row_title).description("").build());

        constraints = RowPreferences.getRowConstraints(AppCategory.MUSIC, activity);

        actions.add(new GuidedAction.Builder(activity).id(++i).title(R.string.max_rows_title).description(Integer.toString(constraints[1])).descriptionEditable(true).descriptionEditInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED).build());
        actions.add(new GuidedAction.Builder(activity).id(++i).title(R.string.min_rows_title).description(Integer.toString(constraints[0])).descriptionEditable(true).descriptionEditInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED).build());

        musicIndex = ((i - 1) / 4);

        // VIDEO
        actions.add(new GuidedAction.Builder(activity).id(++i).title(R.string.videos_row_title).build());
        actions.add(new GuidedAction.Builder(activity).id(++i).checkSetId(i).checked(categories.contains(AppCategory.VIDEO)).title(R.string.enable_videos_row_title).description("").build());

        constraints = RowPreferences.getRowConstraints(AppCategory.VIDEO, activity);

        actions.add(new GuidedAction.Builder(activity).id(++i).title(R.string.max_rows_title).description(Integer.toString(constraints[1])).descriptionEditable(true).descriptionEditInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED).build());
        actions.add(new GuidedAction.Builder(activity).id(++i).title(R.string.min_rows_title).description(Integer.toString(constraints[0])).descriptionEditable(true).descriptionEditInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED).build());

        videoIndex = ((i - 1) / 4);

        // GAME
        actions.add(new GuidedAction.Builder(activity).id(++i).title(R.string.games_row_title).build());
        actions.add(new GuidedAction.Builder(activity).id(++i).checkSetId(i).checked(categories.contains(AppCategory.GAME)).title(R.string.enable_games_row_title).description("").build());

        constraints = RowPreferences.getRowConstraints(AppCategory.GAME, activity);

        actions.add(new GuidedAction.Builder(activity).id(++i).title(R.string.max_rows_title).description(Integer.toString(constraints[1])).descriptionEditable(true).descriptionEditInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED).build());
        actions.add(new GuidedAction.Builder(activity).id(++i).title(R.string.min_rows_title).description(Integer.toString(constraints[0])).descriptionEditable(true).descriptionEditInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED).build());

        gameIndex = ((i - 1) / 4);

        // ALL
        actions.add(new GuidedAction.Builder(activity).id(++i).title(R.string.apps_row_title).build());

        constraints = RowPreferences.getAllAppsConstraints(activity);

        actions.add(new GuidedAction.Builder(activity).id(++i).title(R.string.max_rows_title).description(Integer.toString(constraints[1])).descriptionEditable(true).descriptionEditInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED).build());
        actions.add(new GuidedAction.Builder(activity).id(++i).title(R.string.min_rows_title).description(Integer.toString(constraints[0])).descriptionEditable(true).descriptionEditInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED).build());

        allIndex = (i / 4);
        
        // INPUTS
        actions.add(new GuidedAction.Builder(activity).id(200).title(R.string.inputs_row_title).build());
        actions.add(new GuidedAction.Builder(activity).id(201).checkSetId(201).checked(RowPreferences.areInputsEnabled(activity)).title(R.string.enable_inputs_row_title).description("").build());
        
        setActions(actions); // APPLY
    }

}
