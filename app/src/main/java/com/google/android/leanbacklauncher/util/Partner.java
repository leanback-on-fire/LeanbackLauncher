package com.google.android.leanbacklauncher.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.leanbacklauncher.R;
import com.google.android.leanbacklauncher.apps.AppsManager.SortingMode;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Partner {
    private static final Map<String, Integer> sDefaultPrioritiesMap = new HashMap();
    private static final Object sLock = new Object();
    private static Partner sPartner;
    private static boolean sSearched = false;
    private final String mPackageName;
    private final String mReceiverName;
    private final Resources mResources;
    private boolean mRowDataReady;
    private HashMap<String, Integer> mRowPositions = new HashMap();

    static {
        sDefaultPrioritiesMap.put("input_type_combined_tuners", Integer.valueOf(-3));
        sDefaultPrioritiesMap.put("input_type_tuner", Integer.valueOf(0));
        sDefaultPrioritiesMap.put("input_type_cec_logical", Integer.valueOf(-2));
        sDefaultPrioritiesMap.put("input_type_cec_recorder", Integer.valueOf(-4));
        sDefaultPrioritiesMap.put("input_type_cec_playback", Integer.valueOf(-5));
        sDefaultPrioritiesMap.put("input_type_mhl_mobile", Integer.valueOf(-6));
        sDefaultPrioritiesMap.put("input_type_hdmi", Integer.valueOf(1007));
        sDefaultPrioritiesMap.put("input_type_dvi", Integer.valueOf(1006));
        sDefaultPrioritiesMap.put("input_type_component", Integer.valueOf(1004));
        sDefaultPrioritiesMap.put("input_type_svideo", Integer.valueOf(1002));
        sDefaultPrioritiesMap.put("input_type_composite", Integer.valueOf(1001));
        sDefaultPrioritiesMap.put("input_type_displayport", Integer.valueOf(1008));
        sDefaultPrioritiesMap.put("input_type_vga", Integer.valueOf(1005));
        sDefaultPrioritiesMap.put("input_type_scart", Integer.valueOf(1003));
        sDefaultPrioritiesMap.put("input_type_other", Integer.valueOf(1000));
    }

    public static Partner get(Context context) {
        PackageManager pm = context.getPackageManager();
        synchronized (sLock) {
            if (!sSearched) {
                ResolveInfo info = getPartnerResolveInfo(pm, null);
                if (info != null) {
                    String packageName = info.activityInfo.packageName;
                    try {
                        sPartner = new Partner(packageName, info.activityInfo.name, pm.getResourcesForApplication(packageName));
                        sPartner.sendInitBroadcast(context);
                    } catch (NameNotFoundException e) {
                        Log.w("Partner", "Failed to find resources for " + packageName);
                    }
                }
                sSearched = true;
                if (sPartner == null) {
                    sPartner = new Partner(null, null, null);
                }
            }
        }
        return sPartner;
    }

    public static void resetIfNecessary(Context context, String packageName) {
        synchronized (sLock) {
            if (!(sPartner == null || TextUtils.isEmpty(packageName) || !packageName.equals(sPartner.mPackageName))) {
                sSearched = false;
                sPartner = null;
                get(context);
            }
        }
    }

    protected Partner(String packageName, String receiverName, Resources res) {
        this.mPackageName = packageName;
        this.mReceiverName = receiverName;
        this.mResources = res;
        this.mRowDataReady = false;
    }

    protected void sendInitBroadcast(Context context) {
        if (!TextUtils.isEmpty(this.mPackageName) && !TextUtils.isEmpty(this.mReceiverName)) {
            Intent intent = new Intent("com.google.android.leanbacklauncher.action.PARTNER_CUSTOMIZATION");
            intent.setComponent(new ComponentName(this.mPackageName, this.mReceiverName));
            intent.setFlags(268435456);
            intent.putExtra("com.google.android.leanbacklauncher.extra.ROW_WRAPPING_CUTOFF", context.getResources().getInteger(R.integer.two_row_cut_off));
            context.sendBroadcast(intent);
        }
    }

    public Drawable getSystemBackground() {
        if (this.mResources == null || TextUtils.isEmpty(this.mPackageName)) {
            return null;
        }
        int nameResId = this.mResources.getIdentifier("partner_wallpaper", "string", this.mPackageName);
        if (nameResId == 0) {
            return null;
        }
        String name = this.mResources.getString(nameResId);
        if (TextUtils.isEmpty(name)) {
            return null;
        }
        int wallpaperResId = this.mResources.getIdentifier(name, "drawable", this.mPackageName);
        if (wallpaperResId != 0) {
            return this.mResources.getDrawable(wallpaperResId, null);
        }
        return null;
    }

    public Bitmap getSystemBackgroundMask() {
        return getBitmap("partner_wallpaper_bg_mask");
    }

    public Bitmap getSystemBackgroundVideoMask() {
        return getBitmap("partner_wallpaper_bg_video_mask");
    }

    private Bitmap getBitmap(String key) {
        if (this.mResources == null || TextUtils.isEmpty(this.mPackageName)) {
            return null;
        }
        int keyResId = this.mResources.getIdentifier(key, "string", this.mPackageName);
        if (keyResId == 0) {
            return null;
        }
        String name = this.mResources.getString(keyResId);
        if (TextUtils.isEmpty(name)) {
            return null;
        }
        int bitmapResId = this.mResources.getIdentifier(name, "drawable", this.mPackageName);
        if (bitmapResId != 0) {
            return BitmapFactory.decodeResource(this.mResources, bitmapResId);
        }
        return null;
    }

    public SortingMode getAppSortingMode() {
        SortingMode sortingMode = SortingMode.FIXED;
        if (this.mResources == null || TextUtils.isEmpty(this.mPackageName)) {
            return sortingMode;
        }
        int nameResId = this.mResources.getIdentifier("partner_app_sorting_mode", "string", this.mPackageName);
        if (nameResId != 0) {
            return SortingMode.valueOf(this.mResources.getString(nameResId).toUpperCase(Locale.ENGLISH));
        }
        return isRowEnabled("partner_row") ? SortingMode.RECENCY : SortingMode.FIXED;
    }

    public Drawable getCustomSearchIcon() {
        if (this.mResources == null || TextUtils.isEmpty(this.mPackageName)) {
            return null;
        }
        int nameResId = this.mResources.getIdentifier("partner_search_icon", "string", this.mPackageName);
        if (nameResId == 0) {
            return null;
        }
        String name = this.mResources.getString(nameResId);
        if (TextUtils.isEmpty(name)) {
            return null;
        }
        int iconResId = this.mResources.getIdentifier(name, "drawable", this.mPackageName);
        if (iconResId != 0) {
            return this.mResources.getDrawable(iconResId, null);
        }
        return null;
    }

    public ComponentName getWidgetComponentName() {
        if (this.mResources == null || TextUtils.isEmpty(this.mPackageName)) {
            return null;
        }
        int nameResId = this.mResources.getIdentifier("partner_widget_provider_component_name", "string", this.mPackageName);
        if (nameResId != 0) {
            return ComponentName.unflattenFromString(this.mResources.getString(nameResId));
        }
        return null;
    }

    public String getPartnerFontName() {
        if (!(this.mResources == null || TextUtils.isEmpty(this.mPackageName))) {
            int nameResId = this.mResources.getIdentifier("partner_font", "string", this.mPackageName);
            if (nameResId != 0) {
                String name = this.mResources.getString(nameResId);
                if (!TextUtils.isEmpty(name)) {
                    return name;
                }
            }
        }
        return null;
    }

    public boolean isRowEnabled(String row) {
        return getRowPosition(row) != -1;
    }

    public int getRowPosition(String row) {
        if (!this.mRowDataReady) {
            fetchRowsData();
        }
        if (!this.mRowDataReady) {
            return -1;
        }
        return ((Integer) this.mRowPositions.get(row.trim().toLowerCase())).intValue();
    }

    public String getRowTitle(String row, String defaultValue) {
        String title = defaultValue;
        if (this.mResources == null || TextUtils.isEmpty(this.mPackageName)) {
            return title;
        }
        int resId = this.mResources.getIdentifier(row + "_title", "string", this.mPackageName);
        if (resId != 0) {
            return this.mResources.getString(resId);
        }
        return title;
    }

    public Drawable getRowIcon(String row) {
        if (this.mResources == null || TextUtils.isEmpty(this.mPackageName)) {
            return null;
        }
        int resId = this.mResources.getIdentifier(row + "_icon", "drawable", this.mPackageName);
        if (resId != 0) {
            return this.mResources.getDrawable(resId, null);
        }
        return null;
    }

    public String[] getOutOfBoxOrder() {
        if (this.mResources == null || TextUtils.isEmpty(this.mPackageName)) {
            return null;
        }
        int resId = this.mResources.getIdentifier("partner_out_of_box_order", "array", this.mPackageName);
        if (resId != 0) {
            return this.mResources.getStringArray(resId);
        }
        return null;
    }

    public boolean showLiveTvOnStartUp() {
        if (this.mResources == null || TextUtils.isEmpty(this.mPackageName)) {
            return false;
        }
        int resId = this.mResources.getIdentifier("partner_show_live_tv_on_start_up", "bool", this.mPackageName);
        if (resId != 0) {
            return this.mResources.getBoolean(resId);
        }
        return false;
    }

    public Map<Integer, Integer> getInputsOrderMap() {
        HashMap<Integer, Integer> map = new HashMap();
        if (!(this.mResources == null || TextUtils.isEmpty(this.mPackageName))) {
            String[] inputsArray = null;
            int resId = this.mResources.getIdentifier("home_screen_inputs_ordering", "array", this.mPackageName);
            if (resId != 0) {
                inputsArray = this.mResources.getStringArray(resId);
            }
            if (inputsArray != null) {
                int length = inputsArray.length;
                int i = 0;
                int priority = 0;
                while (i < length) {
                    int priority2;
                    Integer type = (Integer) sDefaultPrioritiesMap.get(inputsArray[i]);
                    if (type != null) {
                        priority2 = priority + 1;
                        map.put(type, Integer.valueOf(priority));
                    } else {
                        priority2 = priority;
                    }
                    i++;
                    priority = priority2;
                }
            }
        }
        return map;
    }

    public boolean showPhysicalTunersSeparately() {
        if (this.mResources == null || TextUtils.isEmpty(this.mPackageName)) {
            return false;
        }
        int resId = this.mResources.getIdentifier("show_physical_tuners_separately", "bool", this.mPackageName);
        if (resId != 0) {
            return this.mResources.getBoolean(resId);
        }
        return false;
    }

    public boolean disableDisconnectedInputs() {
        if (this.mResources == null || TextUtils.isEmpty(this.mPackageName)) {
            return true;
        }
        int resId = this.mResources.getIdentifier("disable_disconnected_inputs", "bool", this.mPackageName);
        if (resId != 0) {
            return this.mResources.getBoolean(resId);
        }
        return true;
    }

    public boolean getStateIconFromTVInput() {
        if (this.mResources == null || TextUtils.isEmpty(this.mPackageName)) {
            return false;
        }
        int resId = this.mResources.getIdentifier("enable_input_state_icon", "bool", this.mPackageName);
        if (resId != 0) {
            return this.mResources.getBoolean(resId);
        }
        return false;
    }

    public String getBundledTunerTitle() {
        if (!(this.mResources == null || TextUtils.isEmpty(this.mPackageName))) {
            int nameResId = this.mResources.getIdentifier("bundled_tuner_title", "string", this.mPackageName);
            if (nameResId != 0) {
                String name = this.mResources.getString(nameResId);
                if (!TextUtils.isEmpty(name)) {
                    return name;
                }
            }
        }
        return null;
    }

    public String getDisconnectedInputToastText() {
        if (!(this.mResources == null || TextUtils.isEmpty(this.mPackageName))) {
            int textResId = this.mResources.getIdentifier("disconnected_input_text", "string", this.mPackageName);
            if (textResId != 0) {
                String name = this.mResources.getString(textResId);
                if (!TextUtils.isEmpty(name)) {
                    return name;
                }
            }
        }
        return null;
    }

    public Drawable getBundledTunerBanner() {
        if (this.mResources == null || TextUtils.isEmpty(this.mPackageName)) {
            return null;
        }
        int resId = this.mResources.getIdentifier("bundled_tuner_banner", "drawable", this.mPackageName);
        if (resId != 0) {
            return this.mResources.getDrawable(resId, null);
        }
        return null;
    }

    public int getBundledTunerLabelColorOption(int defaultColor) {
        int color = defaultColor;
        if (this.mResources == null || TextUtils.isEmpty(this.mPackageName)) {
            return color;
        }
        int nameResId = this.mResources.getIdentifier("bundled_tuner_label_color_option", "integer", this.mPackageName);
        if (nameResId != 0) {
            return this.mResources.getInteger(nameResId);
        }
        return color;
    }

    private void fetchRowsData() {
        String[] rowsArray = null;
        if (!(this.mResources == null || TextUtils.isEmpty(this.mPackageName))) {
            int resId = this.mResources.getIdentifier(getRowPositionResString(), "array", this.mPackageName);
            if (resId != 0) {
                rowsArray = this.mResources.getStringArray(resId);
            }
        }
        if (rowsArray != null) {
            this.mRowPositions.clear();
            this.mRowPositions.put("partner_row", Integer.valueOf(-1));
            this.mRowPositions.put("apps_row", Integer.valueOf(-1));
            this.mRowPositions.put("games_row", Integer.valueOf(-1));
            this.mRowPositions.put("inputs_row", Integer.valueOf(-1));
            this.mRowPositions.put("settings_row", Integer.valueOf(-1));
            int position = 0;
            for (String row : rowsArray) {
                if (((Integer) this.mRowPositions.get(row)).intValue() == -1) {
                    this.mRowPositions.put(row, Integer.valueOf(position));
                    position++;
                }
            }
            this.mRowDataReady = true;
        }
    }

    protected String getRowPositionResString() {
        return "home_screen_row_ordering";
    }

    private static ResolveInfo getPartnerResolveInfo(PackageManager pm, ComponentName name) {
        Intent intent = new Intent("com.google.android.leanbacklauncher.action.PARTNER_CUSTOMIZATION");
        if (name != null) {
            intent.setPackage(name.getPackageName());
        }
        for (ResolveInfo info : pm.queryBroadcastReceivers(intent, 0)) {
            if (isSystemApp(info)) {
                return info;
            }
        }
        return null;
    }

    protected static boolean isSystemApp(ResolveInfo info) {
        return (info.activityInfo == null || info.activityInfo.applicationInfo == null || (info.activityInfo.applicationInfo.flags & 1) == 0) ? false : true;
    }
}
