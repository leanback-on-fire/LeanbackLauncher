package android.support.v4.accessibilityservice;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build.VERSION;
import android.support.annotation.RequiresApi;

public final class AccessibilityServiceInfoCompat
{
  public static final int CAPABILITY_CAN_FILTER_KEY_EVENTS = 8;
  public static final int CAPABILITY_CAN_REQUEST_ENHANCED_WEB_ACCESSIBILITY = 4;
  public static final int CAPABILITY_CAN_REQUEST_TOUCH_EXPLORATION = 2;
  public static final int CAPABILITY_CAN_RETRIEVE_WINDOW_CONTENT = 1;
  @Deprecated
  public static final int DEFAULT = 1;
  public static final int FEEDBACK_ALL_MASK = -1;
  public static final int FEEDBACK_BRAILLE = 32;
  public static final int FLAG_INCLUDE_NOT_IMPORTANT_VIEWS = 2;
  public static final int FLAG_REPORT_VIEW_IDS = 16;
  public static final int FLAG_REQUEST_ENHANCED_WEB_ACCESSIBILITY = 8;
  public static final int FLAG_REQUEST_FILTER_KEY_EVENTS = 32;
  public static final int FLAG_REQUEST_TOUCH_EXPLORATION_MODE = 4;
  private static final AccessibilityServiceInfoBaseImpl IMPL = new AccessibilityServiceInfoBaseImpl();
  
  static
  {
    if (Build.VERSION.SDK_INT >= 18)
    {
      IMPL = new AccessibilityServiceInfoApi18Impl();
      return;
    }
    if (Build.VERSION.SDK_INT >= 16)
    {
      IMPL = new AccessibilityServiceInfoApi16Impl();
      return;
    }
  }
  
  public static String capabilityToString(int paramInt)
  {
    switch (paramInt)
    {
    case 3: 
    case 5: 
    case 6: 
    case 7: 
    default: 
      return "UNKNOWN";
    case 1: 
      return "CAPABILITY_CAN_RETRIEVE_WINDOW_CONTENT";
    case 2: 
      return "CAPABILITY_CAN_REQUEST_TOUCH_EXPLORATION";
    case 4: 
      return "CAPABILITY_CAN_REQUEST_ENHANCED_WEB_ACCESSIBILITY";
    }
    return "CAPABILITY_CAN_FILTER_KEY_EVENTS";
  }
  
  public static String feedbackTypeToString(int paramInt)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("[");
    while (paramInt > 0)
    {
      int i = 1 << Integer.numberOfTrailingZeros(paramInt);
      paramInt &= (i ^ 0xFFFFFFFF);
      if (localStringBuilder.length() > 1) {
        localStringBuilder.append(", ");
      }
      switch (i)
      {
      default: 
        break;
      case 1: 
        localStringBuilder.append("FEEDBACK_SPOKEN");
        break;
      case 4: 
        localStringBuilder.append("FEEDBACK_AUDIBLE");
        break;
      case 2: 
        localStringBuilder.append("FEEDBACK_HAPTIC");
        break;
      case 16: 
        localStringBuilder.append("FEEDBACK_GENERIC");
        break;
      case 8: 
        localStringBuilder.append("FEEDBACK_VISUAL");
      }
    }
    localStringBuilder.append("]");
    return localStringBuilder.toString();
  }
  
  public static String flagToString(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return null;
    case 1: 
      return "DEFAULT";
    case 2: 
      return "FLAG_INCLUDE_NOT_IMPORTANT_VIEWS";
    case 4: 
      return "FLAG_REQUEST_TOUCH_EXPLORATION_MODE";
    case 8: 
      return "FLAG_REQUEST_ENHANCED_WEB_ACCESSIBILITY";
    case 16: 
      return "FLAG_REPORT_VIEW_IDS";
    }
    return "FLAG_REQUEST_FILTER_KEY_EVENTS";
  }
  
  @Deprecated
  public static boolean getCanRetrieveWindowContent(AccessibilityServiceInfo paramAccessibilityServiceInfo)
  {
    return paramAccessibilityServiceInfo.getCanRetrieveWindowContent();
  }
  
  public static int getCapabilities(AccessibilityServiceInfo paramAccessibilityServiceInfo)
  {
    return IMPL.getCapabilities(paramAccessibilityServiceInfo);
  }
  
  @Deprecated
  public static String getDescription(AccessibilityServiceInfo paramAccessibilityServiceInfo)
  {
    return paramAccessibilityServiceInfo.getDescription();
  }
  
  @Deprecated
  public static String getId(AccessibilityServiceInfo paramAccessibilityServiceInfo)
  {
    return paramAccessibilityServiceInfo.getId();
  }
  
  @Deprecated
  public static ResolveInfo getResolveInfo(AccessibilityServiceInfo paramAccessibilityServiceInfo)
  {
    return paramAccessibilityServiceInfo.getResolveInfo();
  }
  
  @Deprecated
  public static String getSettingsActivityName(AccessibilityServiceInfo paramAccessibilityServiceInfo)
  {
    return paramAccessibilityServiceInfo.getSettingsActivityName();
  }
  
  public static String loadDescription(AccessibilityServiceInfo paramAccessibilityServiceInfo, PackageManager paramPackageManager)
  {
    return IMPL.loadDescription(paramAccessibilityServiceInfo, paramPackageManager);
  }
  
  @RequiresApi(16)
  static class AccessibilityServiceInfoApi16Impl
    extends AccessibilityServiceInfoCompat.AccessibilityServiceInfoBaseImpl
  {
    public String loadDescription(AccessibilityServiceInfo paramAccessibilityServiceInfo, PackageManager paramPackageManager)
    {
      return paramAccessibilityServiceInfo.loadDescription(paramPackageManager);
    }
  }
  
  @RequiresApi(18)
  static class AccessibilityServiceInfoApi18Impl
    extends AccessibilityServiceInfoCompat.AccessibilityServiceInfoApi16Impl
  {
    public int getCapabilities(AccessibilityServiceInfo paramAccessibilityServiceInfo)
    {
      return paramAccessibilityServiceInfo.getCapabilities();
    }
  }
  
  static class AccessibilityServiceInfoBaseImpl
  {
    public int getCapabilities(AccessibilityServiceInfo paramAccessibilityServiceInfo)
    {
      if (AccessibilityServiceInfoCompat.getCanRetrieveWindowContent(paramAccessibilityServiceInfo)) {
        return 1;
      }
      return 0;
    }
    
    public String loadDescription(AccessibilityServiceInfo paramAccessibilityServiceInfo, PackageManager paramPackageManager)
    {
      return null;
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v4/accessibilityservice/AccessibilityServiceInfoCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */