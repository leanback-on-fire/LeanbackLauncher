package com.google.android.tvlauncher.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Partner
{
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
  private static boolean sSearched;
  private final String mPackageName;
  private HashMap<String, Integer> mPackageQuota;
  private final String mReceiverName;
  private final Resources mResources;
  
  static
  {
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
    sSearched = false;
  }
  
  protected Partner(String paramString1, String paramString2, Resources paramResources)
  {
    this.mPackageName = paramString1;
    this.mReceiverName = paramString2;
    this.mResources = paramResources;
  }
  
  public static Partner get(Context paramContext)
  {
    Object localObject2 = paramContext.getPackageManager();
    String str;
    synchronized (sLock)
    {
      Object localObject3;
      if (!sSearched)
      {
        localObject3 = getPartnerResolveInfo((PackageManager)localObject2);
        if (localObject3 != null)
        {
          str = ((ResolveInfo)localObject3).activityInfo.packageName;
          localObject3 = ((ResolveInfo)localObject3).activityInfo.name;
        }
      }
      try
      {
        localObject2 = ((PackageManager)localObject2).getResourcesForApplication(str);
        if (!BuildType.DEBUG.booleanValue()) {
          break label132;
        }
        sPartner = (Partner)BuildType.newInstance(Partner.class, "com.google.android.tvlauncher.util.PartnerEmulation", new Object[] { paramContext, str, localObject3, localObject2 });
        sPartner.sendInitBroadcast(paramContext);
      }
      catch (PackageManager.NameNotFoundException paramContext)
      {
        for (;;)
        {
          Log.w("Partner", "Failed to find resources for " + str);
        }
      }
      sSearched = true;
      if (sPartner == null) {
        sPartner = new Partner(null, null, null);
      }
      return sPartner;
      label132:
      sPartner = new Partner(str, (String)localObject3, (Resources)localObject2);
    }
  }
  
  private boolean getBoolean(String paramString)
  {
    if ((this.mResources != null) && (!TextUtils.isEmpty(this.mPackageName)))
    {
      int i = this.mResources.getIdentifier(paramString, "bool", this.mPackageName);
      if (i != 0) {
        return this.mResources.getBoolean(i);
      }
    }
    return false;
  }
  
  private Drawable getNamedDrawable(String paramString)
  {
    Object localObject2 = null;
    Object localObject1 = localObject2;
    if (this.mResources != null)
    {
      localObject1 = localObject2;
      if (!TextUtils.isEmpty(this.mPackageName))
      {
        int i = this.mResources.getIdentifier(paramString, "string", this.mPackageName);
        localObject1 = localObject2;
        if (i != 0)
        {
          paramString = this.mResources.getString(i);
          localObject1 = localObject2;
          if (!TextUtils.isEmpty(paramString))
          {
            i = this.mResources.getIdentifier(paramString, "drawable", this.mPackageName);
            localObject1 = localObject2;
            if (i != 0) {
              localObject1 = this.mResources.getDrawable(i, null);
            }
          }
        }
      }
    }
    return (Drawable)localObject1;
  }
  
  private static ResolveInfo getPartnerResolveInfo(PackageManager paramPackageManager)
  {
    Object localObject2 = new Intent("com.google.android.tvlauncher.action.PARTNER_CUSTOMIZATION");
    if (BuildType.DEBUG.booleanValue()) {
      ((Intent)localObject2).setPackage("com.google.android.tvlauncher");
    }
    Object localObject1 = null;
    localObject2 = paramPackageManager.queryBroadcastReceivers((Intent)localObject2, 0).iterator();
    do
    {
      paramPackageManager = (PackageManager)localObject1;
      if (!((Iterator)localObject2).hasNext()) {
        break;
      }
      paramPackageManager = (ResolveInfo)((Iterator)localObject2).next();
    } while (!isSystemApp(paramPackageManager));
    return paramPackageManager;
  }
  
  private String getString(String paramString)
  {
    if ((this.mResources != null) && (!TextUtils.isEmpty(this.mPackageName)))
    {
      int i = this.mResources.getIdentifier(paramString, "string", this.mPackageName);
      if (i != 0)
      {
        paramString = this.mResources.getString(i);
        if (!TextUtils.isEmpty(paramString)) {
          return paramString;
        }
      }
    }
    return null;
  }
  
  private String[] getStringArray(String paramString)
  {
    if ((this.mResources != null) && (!TextUtils.isEmpty(this.mPackageName)))
    {
      int i = this.mResources.getIdentifier(paramString, "array", this.mPackageName);
      if (i != 0) {
        return this.mResources.getStringArray(i);
      }
    }
    return null;
  }
  
  protected static boolean isSystemApp(ResolveInfo paramResolveInfo)
  {
    return (paramResolveInfo.activityInfo != null) && (paramResolveInfo.activityInfo.applicationInfo != null) && ((paramResolveInfo.activityInfo.applicationInfo.flags & 0x1) != 0);
  }
  
  public static void resetIfNecessary(Context paramContext, String paramString)
  {
    synchronized (sLock)
    {
      if ((sPartner != null) && (!TextUtils.isEmpty(paramString)) && (paramString.equals(sPartner.mPackageName)))
      {
        sSearched = false;
        sPartner = null;
        get(paramContext);
      }
      return;
    }
  }
  
  public boolean disableDisconnectedInputs()
  {
    return getBoolean("disable_disconnected_inputs");
  }
  
  public String getAppsPromotionRowPackage()
  {
    return getString("apps_promotion_row_package");
  }
  
  public Drawable getBundledTunerBanner()
  {
    Object localObject2 = null;
    Object localObject1 = localObject2;
    if (this.mResources != null)
    {
      localObject1 = localObject2;
      if (!TextUtils.isEmpty(this.mPackageName))
      {
        int i = this.mResources.getIdentifier("bundled_tuner_banner", "drawable", this.mPackageName);
        localObject1 = localObject2;
        if (i != 0) {
          localObject1 = this.mResources.getDrawable(i, null);
        }
      }
    }
    return (Drawable)localObject1;
  }
  
  public int getBundledTunerLabelColorOption(int paramInt)
  {
    int i = paramInt;
    if (this.mResources != null)
    {
      i = paramInt;
      if (!TextUtils.isEmpty(this.mPackageName))
      {
        int j = this.mResources.getIdentifier("bundled_tuner_label_color_option", "integer", this.mPackageName);
        i = paramInt;
        if (j != 0) {
          i = this.mResources.getInteger(j);
        }
      }
    }
    return i;
  }
  
  public String getBundledTunerTitle()
  {
    return getString("bundled_tuner_title");
  }
  
  public Drawable getCustomSearchIcon()
  {
    return getNamedDrawable("partner_search_icon");
  }
  
  public String getDisconnectedInputToastText()
  {
    return getString("disconnected_input_text");
  }
  
  public Map<Integer, Integer> getInputsOrderMap()
  {
    HashMap localHashMap = new HashMap();
    int j;
    if ((this.mResources != null) && (!TextUtils.isEmpty(this.mPackageName)))
    {
      String[] arrayOfString = null;
      int i = this.mResources.getIdentifier("home_screen_inputs_ordering", "array", this.mPackageName);
      if (i != 0) {
        arrayOfString = this.mResources.getStringArray(i);
      }
      if (arrayOfString != null)
      {
        int m = arrayOfString.length;
        j = 0;
        i = 0;
        if (j < m)
        {
          Object localObject = arrayOfString[j];
          localObject = (Integer)sDefaultPrioritiesMap.get(localObject);
          if (localObject == null) {
            break label133;
          }
          int k = i + 1;
          localHashMap.put(localObject, Integer.valueOf(i));
          i = k;
        }
      }
    }
    label133:
    for (;;)
    {
      j += 1;
      break;
      return localHashMap;
    }
  }
  
  public String[] getOutOfBoxAllAppsList()
  {
    return getStringArray("partner_all_apps_out_of_box");
  }
  
  public String[] getOutOfBoxChannelPackagesList()
  {
    return getStringArray("partner_channel_packages_out_of_box");
  }
  
  public String[] getOutOfBoxFavoriteAppsList()
  {
    return getStringArray("partner_favorite_apps_out_of_box");
  }
  
  public String getPackageName()
  {
    return this.mPackageName;
  }
  
  public String getPartnerFontName()
  {
    return getString("partner_font");
  }
  
  public Resources getResources()
  {
    return this.mResources;
  }
  
  public String[] getShowcasedAppsList()
  {
    return getStringArray("apps_showcased_apps_list");
  }
  
  public boolean getStateIconFromTVInput()
  {
    return getBoolean("enable_input_state_icon");
  }
  
  public Drawable getSystemBackground()
  {
    return getNamedDrawable("partner_wallpaper");
  }
  
  protected void sendInitBroadcast(Context paramContext)
  {
    if ((!TextUtils.isEmpty(this.mPackageName)) && (!TextUtils.isEmpty(this.mReceiverName)))
    {
      Intent localIntent = new Intent("com.google.android.tvlauncher.action.PARTNER_CUSTOMIZATION");
      localIntent.setComponent(new ComponentName(this.mPackageName, this.mReceiverName));
      localIntent.setFlags(268435456);
      paramContext.sendBroadcast(localIntent);
    }
  }
  
  public boolean showPhysicalTunersSeparately()
  {
    return getBoolean("show_physical_tuners_separately");
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/util/Partner.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */