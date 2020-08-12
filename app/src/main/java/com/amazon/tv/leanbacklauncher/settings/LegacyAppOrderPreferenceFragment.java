package com.amazon.tv.leanbacklauncher.settings;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.leanback.app.GuidedStepSupportFragment;
import androidx.leanback.widget.GuidanceStylist.Guidance;
import androidx.leanback.widget.GuidedAction;
import androidx.leanback.widget.GuidedAction.Builder;

import com.amazon.tv.leanbacklauncher.R;
import com.amazon.tv.leanbacklauncher.apps.AppsManager;
import com.amazon.tv.leanbacklauncher.apps.AppsManager.SortingMode;

import java.util.List;

public class LegacyAppOrderPreferenceFragment extends GuidedStepSupportFragment {
    @NonNull
    public Guidance onCreateGuidance(Bundle savedInstanceState) {
        return new Guidance(getString(R.string.select_app_order_action_title), null, getString(R.string.home_screen_order_content_title), ResourcesCompat.getDrawable(getResources(), R.drawable.ic_settings_home, null));
    }

    public void onCreateActions(@NonNull List<GuidedAction> actions, Bundle savedInstanceState) {
        boolean z = true;
        SortingMode sortingMode = AppsManager.getSavedSortingMode(getActivity());
        actions.add(new Builder(getActivity()).id(1).checkSetId(1).checked(sortingMode == SortingMode.RECENCY).title(R.string.select_app_order_action_description_recency).description(R.string.recency_order_action_description).build());
        Builder builder = new Builder(getActivity()).id(2).checkSetId(1);
        if (sortingMode != SortingMode.FIXED) {
            z = false;
        }
        actions.add(builder.checked(z).title(R.string.select_app_order_action_description_fixed).description(R.string.fixed_order_action_description).build());
    }

    public void onGuidedActionClicked(GuidedAction action) {
        switch ((int) action.getId()) {
            case 1:
                AppsManager.saveSortingMode(getActivity(), SortingMode.RECENCY);
                return;
            case 2:
                AppsManager.saveSortingMode(getActivity(), SortingMode.FIXED);
                return;
            default:
                return;
        }
    }
}
