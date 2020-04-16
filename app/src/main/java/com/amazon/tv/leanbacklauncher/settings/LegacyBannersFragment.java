package com.amazon.tv.leanbacklauncher.settings;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.v17.leanback.app.GuidedStepSupportFragment;
import android.support.v17.leanback.widget.GuidanceStylist.Guidance;
import android.support.v17.leanback.widget.GuidedAction;
import android.support.v17.leanback.widget.GuidedAction.Builder;
import android.support.v4.content.res.ResourcesCompat;
import android.text.InputType;

import com.amazon.tv.firetv.leanbacklauncher.apps.RowPreferences;
import com.amazon.tv.leanbacklauncher.MainActivity;
import com.amazon.tv.leanbacklauncher.R;

import java.io.File;
import java.lang.Exception;
import java.util.ArrayList;
import java.util.List;

public class LegacyBannersFragment extends GuidedStepSupportFragment {

	/* Action ID definition */
	private static final int ACTION_SZ = 0;
	private static final int ACTION_RAD = 1;
	private static final int ACTION_FWD = 2;
	private static final int ACTION_FCL = 3;
	private static final int ACTION_BACK = 4;

	@NonNull
	public Guidance onCreateGuidance(Bundle savedInstanceState) {
		Activity activity = getActivity();
		// String desc = getString(R.string.banners_corners_radius) + ": " + Integer.toString(RowPreferences.getCorners(activity)) + ", " + getString(R.string.banners_frame_width) + ": " + Integer.toString(RowPreferences.getFrameWidth(activity));
		return new Guidance(
			getString(R.string.banners_prefs_title), // title
			getString(R.string.banners_prefs_desc), // description
			getString(R.string.settings_dialog_title), // breadcrumb (parent)
			ResourcesCompat.getDrawable(getResources(), R.drawable.ic_settings_home, null) // icon
		);
	}

    public void onResume() {
        super.onResume();
        this.updateActions();
    }

    private void updateActions() {

        ArrayList<GuidedAction> actions = new ArrayList<>();
        Activity activity = getActivity();

		actions.add(new Builder(
			activity)
			.id(ACTION_SZ)
			.title((int) R.string.banners_size)
			.description(Integer.toString(RowPreferences.getBannersSize(activity)))
			.descriptionEditable(true)
			.descriptionEditInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED)
			.build()
		);
		actions.add(new Builder(
			activity)
			.id(ACTION_RAD)
			.title((int) R.string.banners_corners_radius)
			.description(Integer.toString(RowPreferences.getCorners(activity)))
			.descriptionEditable(true)
			.descriptionEditInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED)
			.build()
		);
		actions.add(new Builder(
			activity)
			.id(ACTION_FWD)
			.title((int) R.string.banners_frame_width)
			.description(Integer.toString(RowPreferences.getFrameWidth(activity)))
			.descriptionEditable(true)
			.descriptionEditInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED)
			.build()
		);
		actions.add(new Builder(
			activity)
			.id(ACTION_FCL)
			.title((int) R.string.banners_frame_color)
			.description(hexStringColor(RowPreferences.getFrameColor(activity)))
			.descriptionEditable(true)
			.descriptionEditInputType(InputType.TYPE_CLASS_TEXT)
			.build()
		);
//		actions.add(new Builder(
//			activity)
//			.id(ACTION_BACK)
//			.title(R.string.goback)
//			.description(null)
//			.build()
//		);

        setActions(actions); // APPLY
	}

	public void onGuidedActionClicked(GuidedAction action) {

		Activity activity = getActivity();
		int val;

		switch ((int) action.getId()) {
			case ACTION_SZ:
				try {
					val = Integer.parseInt(action.getDescription().toString());
				} catch (NumberFormatException nfe) {
					val = RowPreferences.getBannersSize(activity);
				}
                RowPreferences.setBannersSize(activity, val);
                refreshHome();
				break;
			case ACTION_RAD:
				try {
					val = Integer.parseInt(action.getDescription().toString());
				} catch (NumberFormatException nfe) {
					val = RowPreferences.getCorners(activity);
				}
                RowPreferences.setCorners(activity, val);
                refreshHome();
				break;
			case ACTION_FWD:
				try {
					val = Integer.parseInt(action.getDescription().toString());
				} catch (NumberFormatException nfe) {
					val = RowPreferences.getFrameWidth(activity);
				}
                RowPreferences.setFrameWidth(activity, val);
                refreshHome();
				break;
			case ACTION_FCL:
				try {
					val = Color.parseColor(action.getDescription().toString());
				} catch (IllegalArgumentException nfe) {
					val = RowPreferences.getFrameColor(activity);
				}
                RowPreferences.setFrameColor(activity, val);
                refreshHome();
				break;
			case ACTION_BACK:
				getFragmentManager().popBackStack();
				break;
			default:
				break;
		}
		updateActions();
	}
	private boolean refreshHome() {
		Activity activity = getActivity();
		Intent Broadcast = new Intent(MainActivity.class.getName()); // ACTION
		Broadcast.putExtra("RefreshHome", true);
		activity.sendBroadcast(Broadcast);
		return true;
	}
	private String hexStringColor(int color) {
 		return String.format("#%08X", (0xFFFFFFFF & color));
	}
}
