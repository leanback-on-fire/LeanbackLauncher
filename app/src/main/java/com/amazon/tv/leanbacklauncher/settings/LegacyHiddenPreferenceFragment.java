package com.amazon.tv.leanbacklauncher.settings;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.leanback.app.GuidedStepSupportFragment;
import androidx.leanback.widget.GuidanceStylist;
import androidx.leanback.widget.GuidedAction;

import com.amazon.tv.firetv.leanbacklauncher.util.SharedPreferencesUtil;
import com.amazon.tv.leanbacklauncher.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class LegacyHiddenPreferenceFragment extends GuidedStepSupportFragment {

    private HashMap<Long, String> mActionToPackageMap;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    public GuidanceStylist.Guidance onCreateGuidance(Bundle savedInstanceState) {
        return new GuidanceStylist.Guidance(getString(R.string.hidden_applications_title), getString(R.string.hidden_applications_desc), getString(R.string.settings_dialog_title), ResourcesCompat.getDrawable(getResources(), R.drawable.ic_settings_home, null));
    }

    public void onResume() {
        super.onResume();
        this.loadHiddenApps();
    }

    private Drawable buildBannerFromIcon(Drawable icon) {
        Resources resources = getResources();
        int bannerWidth = resources.getDimensionPixelSize(R.dimen.preference_item_banner_width);
        int bannerHeight = resources.getDimensionPixelSize(R.dimen.preference_item_banner_height);
        int iconSize = resources.getDimensionPixelSize(R.dimen.preference_item_icon_size);
        Bitmap bitmap = Bitmap.createBitmap(bannerWidth, bannerHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(ResourcesCompat.getColor(resources, R.color.preference_item_banner_background, null));
        if (icon != null) {
            icon.setBounds((bannerWidth - iconSize) / 2, (bannerHeight - iconSize) / 2, (bannerWidth + iconSize) / 2, (bannerHeight + iconSize) / 2);
            icon.draw(canvas);
        }
        return new BitmapDrawable(resources, bitmap);
    }

    public void onGuidedActionClicked(GuidedAction action) {
        if (action.isChecked()) {
            SharedPreferencesUtil.instance(getActivity()).hide(this.mActionToPackageMap.get(action.getId()));
        } else {
            SharedPreferencesUtil.instance(getActivity()).unhide(this.mActionToPackageMap.get(action.getId()));
        }
    }

    private void loadHiddenApps() {
        SharedPreferencesUtil util = SharedPreferencesUtil.instance(getActivity());
        List<String> packages = new ArrayList<>(util.hidden());

        if (isAdded()) {
            this.mActionToPackageMap = new HashMap<>();
            ArrayList<GuidedAction> actions = new ArrayList<>();
            long actionId = 0;

            PackageManager pm = getActivity().getPackageManager();

            for (String pkg : packages) {
                boolean hidden;

                try {
                    PackageInfo packageInfo = pm.getPackageInfo(pkg, 0);

                    Drawable banner = pm.getApplicationBanner(packageInfo.applicationInfo);

                    if (banner != null) {
                        banner = banner;
                    } else {
                        banner = buildBannerFromIcon(pm.getApplicationIcon(packageInfo.applicationInfo));
                    }

                    hidden = util.isHidden(pkg);
                    if (hidden) // show only hidden apps
                        actions.add(new GuidedAction.Builder(getActivity()).id(actionId).title(pm.getApplicationLabel(packageInfo.applicationInfo)).icon(banner).checkSetId(-1).checked(hidden).build());
                    this.mActionToPackageMap.put(actionId, packageInfo.packageName);
                    actionId++;
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }

            setActions(actions);
        }
    }

}
