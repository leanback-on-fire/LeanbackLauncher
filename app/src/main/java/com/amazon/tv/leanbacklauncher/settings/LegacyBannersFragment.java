package com.amazon.tv.leanbacklauncher.settings;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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
import com.amazon.tv.leanbacklauncher.MainActivity;
import com.amazon.tv.leanbacklauncher.R;

import java.io.File;
import java.lang.Exception;
import java.util.ArrayList;
import java.util.List;

public class LegacyBannersFragment extends GuidedStepSupportFragment {

	/* Action ID definition */
	private static final int ACTION_RAD = 0;
	private static final int ACTION_FWD = 1;
	private static final int ACTION_FCL = 2;
	private static final int ACTION_BACK = 3;

	@NonNull
	public Guidance onCreateGuidance(Bundle savedInstanceState) {
		Activity activity = getActivity();
		String desc = getString(R.string.banners_corners_radius) + ": " + Integer.toString(getCorners(activity)) + ", " + getString(R.string.banners_frame_width) + ": " + Integer.toString(getFrameWidth(activity));
		return new Guidance(
			getString(R.string.banners_prefs_title), // title
			desc, // description
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
			.id(ACTION_RAD)
			.title((int) R.string.banners_corners_radius)
			.description(Integer.toString(getCorners(activity)))
			.descriptionEditable(true)
			.descriptionEditInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED)
			.build()
		);
		actions.add(new Builder(
			activity)
			.id(ACTION_FWD)
			.title((int) R.string.banners_frame_width)
			.description(Integer.toString(getFrameWidth(activity)))
			.descriptionEditable(true)
			.descriptionEditInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED)
			.build()
		);
		actions.add(new Builder(
			activity)
			.id(ACTION_FCL)
			.title((int) R.string.banners_frame_color)
			.description(hexStringColor(getFrameColor(activity)))
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
			case ACTION_RAD:
				try {
					val = Integer.parseInt(action.getDescription().toString());
				} catch (NumberFormatException nfe) {
					val = getCorners(activity);
				}
                setCorners(activity, val);
				break;
			case ACTION_FWD:
				try {
					val = Integer.parseInt(action.getDescription().toString());
				} catch (NumberFormatException nfe) {
					val = getFrameWidth(activity);
				}
                setFrameWidth(activity, val);
				break;
			case ACTION_FCL:
				try {
					val = Color.parseColor(action.getDescription().toString());
				} catch (IllegalArgumentException nfe) {
					val = getFrameColor(activity);
				}
                setFrameColor(activity, val);
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
	private int getCorners(Context context) {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		int targetCorners = (int)getResources().getDimension(R.dimen.banner_corner_radius);
		return pref.getInt("banner_corner_radius", targetCorners);
	}
	private boolean setCorners(Context context, int radius) {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		if (radius > 0)
			pref.edit().putInt("banner_corner_radius", radius).apply();
		refreshHome();
		return true;
	}
	private int getFrameWidth(Context context) {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		int targetStroke = (int)getResources().getDimension(R.dimen.banner_frame_stroke);
		return pref.getInt("banner_frame_stroke", targetStroke);
	}
	private boolean setFrameWidth(Context context, int width) {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		if (width > 0)
			pref.edit().putInt("banner_frame_stroke", width).apply();
		refreshHome();
		return true;
	}
	private int getFrameColor(Context context) {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		int targetColor = (int)getResources().getColor(R.color.banner_focus_frame_color);
		return pref.getInt("banner_focus_frame_color", targetColor);
	}
	private boolean setFrameColor(Context context, int color) {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		if (color > 0)
			pref.edit().putInt("banner_focus_frame_color", color).apply();
		refreshHome();
		return true;
	}
	private String hexStringColor(int color) {
 		return String.format("#%08X", (0xFFFFFFFF & color));
	}
}
