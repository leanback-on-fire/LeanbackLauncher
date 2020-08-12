package com.amazon.tv.leanbacklauncher.settings;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.core.content.res.ResourcesCompat;
import androidx.leanback.app.GuidedStepSupportFragment;
import androidx.leanback.widget.GuidanceStylist.Guidance;
import androidx.leanback.widget.GuidedAction;
import androidx.leanback.widget.GuidedAction.Builder;

import com.amazon.tv.leanbacklauncher.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LegacyRecommendationsPreferenceFragment extends GuidedStepSupportFragment implements RecommendationsPreferenceManager.LoadRecommendationPackagesCallback {
    private HashMap<Long, String> mActionToPackageMap;
    private RecommendationsPreferenceManager mPreferenceManager;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mPreferenceManager = new RecommendationsPreferenceManager(getActivity());
    }

    public Guidance onCreateGuidance(Bundle savedInstanceState) {
        return new Guidance(getString(R.string.recommendation_blacklist_content_title), getString(R.string.recommendation_blacklist_content_description), getString(R.string.settings_dialog_title), ResourcesCompat.getDrawable(getResources(), R.drawable.ic_settings_home, null));
    }

    public void onResume() {
        super.onResume();
        this.mPreferenceManager.loadRecommendationsPackages(this);
    }

    public void onRecommendationPackagesLoaded(List<RecommendationsPreferenceManager.PackageInfo> infos) {
        if (isAdded()) {
            this.mActionToPackageMap = new HashMap<>();
            ArrayList<GuidedAction> actions = new ArrayList<>();
            long actionId = 0;
            for (RecommendationsPreferenceManager.PackageInfo packageInfo : infos) {
                Drawable banner;
                if (packageInfo.banner != null) {
                    banner = packageInfo.banner;
                } else {
                    banner = buildBannerFromIcon(packageInfo.icon);
                }
                actions.add(new Builder(getActivity()).id(actionId).title(packageInfo.appTitle).icon(banner).checkSetId(-1).checked(!packageInfo.blacklisted).build());
                this.mActionToPackageMap.put(actionId, packageInfo.packageName);
                actionId++;
            }
            setActions(actions);
        }
    }

    private Drawable buildBannerFromIcon(Drawable icon) {
        Resources resources = getResources();
        int bannerWidth = resources.getDimensionPixelSize(R.dimen.preference_item_banner_width);
        int bannerHeight = resources.getDimensionPixelSize(R.dimen.preference_item_banner_height);
        int iconSize = resources.getDimensionPixelSize(R.dimen.preference_item_icon_size);
        Bitmap bitmap = Bitmap.createBitmap(bannerWidth, bannerHeight, Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(ResourcesCompat.getColor(resources, R.color.preference_item_banner_background, null));
        if (icon != null) {
            icon.setBounds((bannerWidth - iconSize) / 2, (bannerHeight - iconSize) / 2, (bannerWidth + iconSize) / 2, (bannerHeight + iconSize) / 2);
            icon.draw(canvas);
        }
        return new BitmapDrawable(resources, bitmap);
    }

    public void onGuidedActionClicked(GuidedAction action) {
        this.mPreferenceManager.savePackageBlacklisted(this.mActionToPackageMap.get(Long.valueOf(action.getId())), !action.isChecked());
    }
}
