package android.support.v4.view.accessibility;

import android.os.Build.VERSION;
import android.support.annotation.RequiresApi;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityRecord;

public final class AccessibilityEventCompat
{
  public static final int CONTENT_CHANGE_TYPE_CONTENT_DESCRIPTION = 4;
  public static final int CONTENT_CHANGE_TYPE_SUBTREE = 1;
  public static final int CONTENT_CHANGE_TYPE_TEXT = 2;
  public static final int CONTENT_CHANGE_TYPE_UNDEFINED = 0;
  private static final AccessibilityEventCompatBaseImpl IMPL = new AccessibilityEventCompatBaseImpl();
  public static final int TYPES_ALL_MASK = -1;
  public static final int TYPE_ANNOUNCEMENT = 16384;
  public static final int TYPE_ASSIST_READING_CONTEXT = 16777216;
  public static final int TYPE_GESTURE_DETECTION_END = 524288;
  public static final int TYPE_GESTURE_DETECTION_START = 262144;
  @Deprecated
  public static final int TYPE_TOUCH_EXPLORATION_GESTURE_END = 1024;
  @Deprecated
  public static final int TYPE_TOUCH_EXPLORATION_GESTURE_START = 512;
  public static final int TYPE_TOUCH_INTERACTION_END = 2097152;
  public static final int TYPE_TOUCH_INTERACTION_START = 1048576;
  public static final int TYPE_VIEW_ACCESSIBILITY_FOCUSED = 32768;
  public static final int TYPE_VIEW_ACCESSIBILITY_FOCUS_CLEARED = 65536;
  public static final int TYPE_VIEW_CONTEXT_CLICKED = 8388608;
  @Deprecated
  public static final int TYPE_VIEW_HOVER_ENTER = 128;
  @Deprecated
  public static final int TYPE_VIEW_HOVER_EXIT = 256;
  @Deprecated
  public static final int TYPE_VIEW_SCROLLED = 4096;
  @Deprecated
  public static final int TYPE_VIEW_TEXT_SELECTION_CHANGED = 8192;
  public static final int TYPE_VIEW_TEXT_TRAVERSED_AT_MOVEMENT_GRANULARITY = 131072;
  public static final int TYPE_WINDOWS_CHANGED = 4194304;
  @Deprecated
  public static final int TYPE_WINDOW_CONTENT_CHANGED = 2048;
  
  static
  {
    if (Build.VERSION.SDK_INT >= 19)
    {
      IMPL = new AccessibilityEventCompatApi19Impl();
      return;
    }
    if (Build.VERSION.SDK_INT >= 16)
    {
      IMPL = new AccessibilityEventCompatApi16Impl();
      return;
    }
  }
  
  @Deprecated
  public static void appendRecord(AccessibilityEvent paramAccessibilityEvent, AccessibilityRecordCompat paramAccessibilityRecordCompat)
  {
    paramAccessibilityEvent.appendRecord((AccessibilityRecord)paramAccessibilityRecordCompat.getImpl());
  }
  
  @Deprecated
  public static AccessibilityRecordCompat asRecord(AccessibilityEvent paramAccessibilityEvent)
  {
    return new AccessibilityRecordCompat(paramAccessibilityEvent);
  }
  
  public static int getContentChangeTypes(AccessibilityEvent paramAccessibilityEvent)
  {
    return IMPL.getContentChangeTypes(paramAccessibilityEvent);
  }
  
  @Deprecated
  public static AccessibilityRecordCompat getRecord(AccessibilityEvent paramAccessibilityEvent, int paramInt)
  {
    return new AccessibilityRecordCompat(paramAccessibilityEvent.getRecord(paramInt));
  }
  
  @Deprecated
  public static int getRecordCount(AccessibilityEvent paramAccessibilityEvent)
  {
    return paramAccessibilityEvent.getRecordCount();
  }
  
  public static void setContentChangeTypes(AccessibilityEvent paramAccessibilityEvent, int paramInt)
  {
    IMPL.setContentChangeTypes(paramAccessibilityEvent, paramInt);
  }
  
  public int getAction(AccessibilityEvent paramAccessibilityEvent)
  {
    return IMPL.getAction(paramAccessibilityEvent);
  }
  
  public int getMovementGranularity(AccessibilityEvent paramAccessibilityEvent)
  {
    return IMPL.getMovementGranularity(paramAccessibilityEvent);
  }
  
  public void setAction(AccessibilityEvent paramAccessibilityEvent, int paramInt)
  {
    IMPL.setAction(paramAccessibilityEvent, paramInt);
  }
  
  public void setMovementGranularity(AccessibilityEvent paramAccessibilityEvent, int paramInt)
  {
    IMPL.setMovementGranularity(paramAccessibilityEvent, paramInt);
  }
  
  @RequiresApi(16)
  static class AccessibilityEventCompatApi16Impl
    extends AccessibilityEventCompat.AccessibilityEventCompatBaseImpl
  {
    public int getAction(AccessibilityEvent paramAccessibilityEvent)
    {
      return paramAccessibilityEvent.getAction();
    }
    
    public int getMovementGranularity(AccessibilityEvent paramAccessibilityEvent)
    {
      return paramAccessibilityEvent.getMovementGranularity();
    }
    
    public void setAction(AccessibilityEvent paramAccessibilityEvent, int paramInt)
    {
      paramAccessibilityEvent.setAction(paramInt);
    }
    
    public void setMovementGranularity(AccessibilityEvent paramAccessibilityEvent, int paramInt)
    {
      paramAccessibilityEvent.setMovementGranularity(paramInt);
    }
  }
  
  @RequiresApi(19)
  static class AccessibilityEventCompatApi19Impl
    extends AccessibilityEventCompat.AccessibilityEventCompatApi16Impl
  {
    public int getContentChangeTypes(AccessibilityEvent paramAccessibilityEvent)
    {
      return paramAccessibilityEvent.getContentChangeTypes();
    }
    
    public void setContentChangeTypes(AccessibilityEvent paramAccessibilityEvent, int paramInt)
    {
      paramAccessibilityEvent.setContentChangeTypes(paramInt);
    }
  }
  
  static class AccessibilityEventCompatBaseImpl
  {
    public int getAction(AccessibilityEvent paramAccessibilityEvent)
    {
      return 0;
    }
    
    public int getContentChangeTypes(AccessibilityEvent paramAccessibilityEvent)
    {
      return 0;
    }
    
    public int getMovementGranularity(AccessibilityEvent paramAccessibilityEvent)
    {
      return 0;
    }
    
    public void setAction(AccessibilityEvent paramAccessibilityEvent, int paramInt) {}
    
    public void setContentChangeTypes(AccessibilityEvent paramAccessibilityEvent, int paramInt) {}
    
    public void setMovementGranularity(AccessibilityEvent paramAccessibilityEvent, int paramInt) {}
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v4/view/accessibility/AccessibilityEventCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */