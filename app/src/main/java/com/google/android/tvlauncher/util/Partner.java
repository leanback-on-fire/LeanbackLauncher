package com.google.android.tvlauncher.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PointerIconCompat;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.tvlauncher.BuildConfig;
import java.util.HashMap;
import java.util.Map;

public class Partner {
    private static final String ALL_APPS_OUT_OF_BOX = "partner_all_apps_out_of_box";
    private static final String APPS_PROMOTION_ROW_PACKAGE = "apps_promotion_row_package";
    private static final String APPS_SHOWCASED_APPS_LIST = "apps_showcased_apps_list";
    private static final String BUNDLED_TUNER_BANNER = "bundled_tuner_banner";
    private static final String BUNDLED_TUNER_LABEL_COLOR_OPTION = "bundled_tuner_label_color_option";
    private static final String BUNDLED_TUNER_TITLE = "bundled_tuner_title";
    private static final String CHANNEL_PACKAGES_OUT_OF_BOX = "partner_channel_packages_out_of_box";
    private static final int DEFAULT_QUOTA = 1;
    private static final String DISABLE_DISCONNECTED_INPUTS = "disable_disconnected_inputs";
    private static final String DISCONNECTED_INPUT_TEXT = "disconnected_input_text";
    private static final String ENABLE_INPUT_STATE_ICON = "enable_input_state_icon";
    private static final String FAVORITE_APPS_OUT_OF_BOX = "partner_favorite_apps_out_of_box";
    private static final String HEADERS_FONT = "partner_font";
    private static final String INPUTS_ORDER = "home_screen_inputs_ordering";
    public static final int INPUT_LABEL_COLOR_DARK = 1;
    public static final int INPUT_LABEL_COLOR_LIGHT = 0;
    private static final String INPUT_TYPE_BUNDLED_TUNER = "input_type_combined_tuners";
    private static final String INPUT_TYPE_CEC_LOGICAL = "input_type_cec_logical";
    private static final String INPUT_TYPE_CEC_PLAYBACK = "input_type_cec_playback";
    private static final String INPUT_TYPE_CEC_RECORDER = "input_type_cec_recorder";
    private static final String INPUT_TYPE_COMPONENT = "input_type_component";
    private static final String INPUT_TYPE_COMPOSITE = "input_type_composite";
    private static final String INPUT_TYPE_DISPLAY_PORT = "input_type_displayport";
    private static final String INPUT_TYPE_DVI = "input_type_dvi";
    private static final String INPUT_TYPE_HDMI = "input_type_hdmi";
    private static final String INPUT_TYPE_MHL_MOBILE = "input_type_mhl_mobile";
    private static final String INPUT_TYPE_OTHER = "input_type_other";
    private static final String INPUT_TYPE_SCART = "input_type_scart";
    private static final String INPUT_TYPE_SVIDEO = "input_type_svideo";
    private static final String INPUT_TYPE_TUNER = "input_type_tuner";
    private static final String INPUT_TYPE_VGA = "input_type_vga";
    private static final String OUT_OF_BOX_ORDER = "partner_out_of_box_order";
    private static final String PACKAGE_QUOTA_NAME = "partner_package_quota_name";
    private static final String PACKAGE_QUOTA_VALUE = "partner_package_quota_value";
    private static final String SEARCH_ICON = "partner_search_icon";
    private static final String SHOWCASED_APPS_ROW_TITLE = "partner_showcased_apps_row_title";
    private static final String SHOW_PHYSICAL_TUNERS_SEPARATELY = "show_physical_tuners_separately";
    private static final String TAG = "Partner";
    private static final String TYPE_ARRAY = "array";
    private static final String TYPE_BOOLEAN = "bool";
    public static final int TYPE_BUNDLED_TUNER = -3;
    public static final int TYPE_CEC_DEVICE = -2;
    public static final int TYPE_CEC_DEVICE_PLAYBACK = -5;
    public static final int TYPE_CEC_DEVICE_RECORDER = -4;
    private static final String TYPE_DRAWABLE = "drawable";
    private static final String TYPE_INT = "integer";
    public static final int TYPE_MHL_MOBILE = -6;
    private static final String TYPE_STRING = "string";
    private static final String WALLPAPER = "partner_wallpaper";
    private static final Map<String, Integer> sDefaultPrioritiesMap = new HashMap();
    private static final Object sLock = new Object();
    private static Partner sPartner;
    private static boolean sSearched = false;
    private final String mPackageName;
    private HashMap<String, Integer> mPackageQuota;
    private final String mReceiverName;
    private final Resources mResources;

    static {
        sDefaultPrioritiesMap.put(INPUT_TYPE_BUNDLED_TUNER, Integer.valueOf(-3));
        sDefaultPrioritiesMap.put(INPUT_TYPE_TUNER, Integer.valueOf(0));
        sDefaultPrioritiesMap.put(INPUT_TYPE_CEC_LOGICAL, Integer.valueOf(-2));
        sDefaultPrioritiesMap.put(INPUT_TYPE_CEC_RECORDER, Integer.valueOf(-4));
        sDefaultPrioritiesMap.put(INPUT_TYPE_CEC_PLAYBACK, Integer.valueOf(-5));
        sDefaultPrioritiesMap.put(INPUT_TYPE_MHL_MOBILE, Integer.valueOf(-6));
        sDefaultPrioritiesMap.put(INPUT_TYPE_HDMI, Integer.valueOf(PointerIconCompat.TYPE_CROSSHAIR));
        sDefaultPrioritiesMap.put(INPUT_TYPE_DVI, Integer.valueOf(PointerIconCompat.TYPE_CELL));
        sDefaultPrioritiesMap.put(INPUT_TYPE_COMPONENT, Integer.valueOf(PointerIconCompat.TYPE_WAIT));
        sDefaultPrioritiesMap.put(INPUT_TYPE_SVIDEO, Integer.valueOf(PointerIconCompat.TYPE_HAND));
        sDefaultPrioritiesMap.put(INPUT_TYPE_COMPOSITE, Integer.valueOf(PointerIconCompat.TYPE_CONTEXT_MENU));
        sDefaultPrioritiesMap.put(INPUT_TYPE_DISPLAY_PORT, Integer.valueOf(PointerIconCompat.TYPE_TEXT));
        sDefaultPrioritiesMap.put(INPUT_TYPE_VGA, Integer.valueOf(1005));
        sDefaultPrioritiesMap.put(INPUT_TYPE_SCART, Integer.valueOf(PointerIconCompat.TYPE_HELP));
        sDefaultPrioritiesMap.put(INPUT_TYPE_OTHER, Integer.valueOf(1000));
    }

    public static Partner get(Context context) {
        PackageManager pm = context.getPackageManager();
        synchronized (sLock) {
            if (!sSearched) {
                ResolveInfo info = getPartnerResolveInfo(pm);
                if (info != null) {
                    String packageName = info.activityInfo.packageName;
                    String receiverName = info.activityInfo.name;
                    try {
                        Resources res = pm.getResourcesForApplication(packageName);
                        if (BuildType.DEBUG.booleanValue()) {
                            sPartner = (Partner) BuildType.newInstance(Partner.class, "com.google.android.tvlauncher.util.PartnerEmulation", context, packageName, receiverName, res);
                        } else {
                            sPartner = new Partner(packageName, receiverName, res);
                        }
                        sPartner.sendInitBroadcast(context);
                    } catch (NameNotFoundException e) {
                        Log.w(TAG, "Failed to find resources for " + packageName);
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
    }

    protected void sendInitBroadcast(Context context) {
        if (!TextUtils.isEmpty(this.mPackageName) && !TextUtils.isEmpty(this.mReceiverName)) {
            Intent intent = new Intent("com.google.android.tvlauncher.action.PARTNER_CUSTOMIZATION");
            intent.setComponent(new ComponentName(this.mPackageName, this.mReceiverName));
            intent.setFlags(268435456);
            context.sendBroadcast(intent);
        }
    }

    public String getPackageName() {
        return this.mPackageName;
    }

    public Resources getResources() {
        return this.mResources;
    }

    protected static boolean isSystemApp(ResolveInfo info) {
        return (info.activityInfo == null || info.activityInfo.applicationInfo == null || (info.activityInfo.applicationInfo.flags & 1) == 0) ? false : true;
    }

    public Drawable getSystemBackground() {
        return getNamedDrawable(WALLPAPER);
    }

    public Drawable getCustomSearchIcon() {
        return getNamedDrawable(SEARCH_ICON);
    }

    public String getPartnerFontName() {
        return getString(HEADERS_FONT);
    }

    public Map<Integer, Integer> getInputsOrderMap() {
        HashMap<Integer, Integer> map = new HashMap();
        if (!(this.mResources == null || TextUtils.isEmpty(this.mPackageName))) {
            String[] inputsArray = null;
            int resId = this.mResources.getIdentifier(INPUTS_ORDER, TYPE_ARRAY, this.mPackageName);
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
        return getBoolean(SHOW_PHYSICAL_TUNERS_SEPARATELY);
    }

    public boolean disableDisconnectedInputs() {
        return getBoolean(DISABLE_DISCONNECTED_INPUTS);
    }

    public boolean getStateIconFromTVInput() {
        return getBoolean(ENABLE_INPUT_STATE_ICON);
    }

    public String getBundledTunerTitle() {
        return getString(BUNDLED_TUNER_TITLE);
    }

    public String getDisconnectedInputToastText() {
        return getString(DISCONNECTED_INPUT_TEXT);
    }

    public Drawable getBundledTunerBanner() {
        if (this.mResources == null || TextUtils.isEmpty(this.mPackageName)) {
            return null;
        }
        int resId = this.mResources.getIdentifier(BUNDLED_TUNER_BANNER, TYPE_DRAWABLE, this.mPackageName);
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
        int nameResId = this.mResources.getIdentifier(BUNDLED_TUNER_LABEL_COLOR_OPTION, TYPE_INT, this.mPackageName);
        if (nameResId != 0) {
            return this.mResources.getInteger(nameResId);
        }
        return color;
    }

    public String getAppsPromotionRowPackage() {
        return getString(APPS_PROMOTION_ROW_PACKAGE);
    }

    public String[] getShowcasedAppsList() {
        return getStringArray(APPS_SHOWCASED_APPS_LIST);
    }

    public String[] getOutOfBoxFavoriteAppsList() {
        return getStringArray(FAVORITE_APPS_OUT_OF_BOX);
    }

    public String[] getOutOfBoxAllAppsList() {
        return getStringArray(ALL_APPS_OUT_OF_BOX);
    }

    public String[] getOutOfBoxChannelPackagesList() {
        return getStringArray(CHANNEL_PACKAGES_OUT_OF_BOX);
    }

    private static ResolveInfo getPartnerResolveInfo(PackageManager pm) {
        Intent intent = new Intent("com.google.android.tvlauncher.action.PARTNER_CUSTOMIZATION");
        if (BuildType.DEBUG.booleanValue()) {
            intent.setPackage(BuildConfig.APPLICATION_ID);
        }
        for (ResolveInfo info : pm.queryBroadcastReceivers(intent, 0)) {
            if (isSystemApp(info)) {
                return info;
            }
        }
        return null;
    }

    private Drawable getNamedDrawable(String drawableName) {
        if (this.mResources == null || TextUtils.isEmpty(this.mPackageName)) {
            return null;
        }
        int drawableNameResId = this.mResources.getIdentifier(drawableName, TYPE_STRING, this.mPackageName);
        if (drawableNameResId == 0) {
            return null;
        }
        String name = this.mResources.getString(drawableNameResId);
        if (TextUtils.isEmpty(name)) {
            return null;
        }
        int drawableResId = this.mResources.getIdentifier(name, TYPE_DRAWABLE, this.mPackageName);
        if (drawableResId != 0) {
            return this.mResources.getDrawable(drawableResId, null);
        }
        return null;
    }

    private String getString(String stringName) {
        if (!(this.mResources == null || TextUtils.isEmpty(this.mPackageName))) {
            int nameResId = this.mResources.getIdentifier(stringName, TYPE_STRING, this.mPackageName);
            if (nameResId != 0) {
                String name = this.mResources.getString(nameResId);
                if (!TextUtils.isEmpty(name)) {
                    return name;
                }
            }
        }
        return null;
    }

    private boolean getBoolean(String booleanName) {
        if (!(this.mResources == null || TextUtils.isEmpty(this.mPackageName))) {
            int resId = this.mResources.getIdentifier(booleanName, TYPE_BOOLEAN, this.mPackageName);
            if (resId != 0) {
                return this.mResources.getBoolean(resId);
            }
        }
        return false;
    }

    private String[] getStringArray(String arrayName) {
        if (!(this.mResources == null || TextUtils.isEmpty(this.mPackageName))) {
            int resId = this.mResources.getIdentifier(arrayName, TYPE_ARRAY, this.mPackageName);
            if (resId != 0) {
                return this.mResources.getStringArray(resId);
            }
        }
        return null;
    }
}
