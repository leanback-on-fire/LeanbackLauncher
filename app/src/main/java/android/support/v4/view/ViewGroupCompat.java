package android.support.v4.view;

import android.os.Build.VERSION;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;

public final class ViewGroupCompat
{
  static final ViewGroupCompatBaseImpl IMPL = new ViewGroupCompatBaseImpl();
  public static final int LAYOUT_MODE_CLIP_BOUNDS = 0;
  public static final int LAYOUT_MODE_OPTICAL_BOUNDS = 1;
  
  static
  {
    if (Build.VERSION.SDK_INT >= 21)
    {
      IMPL = new ViewGroupCompatApi21Impl();
      return;
    }
    if (Build.VERSION.SDK_INT >= 18)
    {
      IMPL = new ViewGroupCompatApi18Impl();
      return;
    }
  }
  
  public static int getLayoutMode(ViewGroup paramViewGroup)
  {
    return IMPL.getLayoutMode(paramViewGroup);
  }
  
  public static int getNestedScrollAxes(@NonNull ViewGroup paramViewGroup)
  {
    return IMPL.getNestedScrollAxes(paramViewGroup);
  }
  
  public static boolean isTransitionGroup(ViewGroup paramViewGroup)
  {
    return IMPL.isTransitionGroup(paramViewGroup);
  }
  
  @Deprecated
  public static boolean onRequestSendAccessibilityEvent(ViewGroup paramViewGroup, View paramView, AccessibilityEvent paramAccessibilityEvent)
  {
    return paramViewGroup.onRequestSendAccessibilityEvent(paramView, paramAccessibilityEvent);
  }
  
  public static void setLayoutMode(ViewGroup paramViewGroup, int paramInt)
  {
    IMPL.setLayoutMode(paramViewGroup, paramInt);
  }
  
  @Deprecated
  public static void setMotionEventSplittingEnabled(ViewGroup paramViewGroup, boolean paramBoolean)
  {
    paramViewGroup.setMotionEventSplittingEnabled(paramBoolean);
  }
  
  public static void setTransitionGroup(ViewGroup paramViewGroup, boolean paramBoolean)
  {
    IMPL.setTransitionGroup(paramViewGroup, paramBoolean);
  }
  
  @RequiresApi(18)
  static class ViewGroupCompatApi18Impl
    extends ViewGroupCompat.ViewGroupCompatBaseImpl
  {
    public int getLayoutMode(ViewGroup paramViewGroup)
    {
      return paramViewGroup.getLayoutMode();
    }
    
    public void setLayoutMode(ViewGroup paramViewGroup, int paramInt)
    {
      paramViewGroup.setLayoutMode(paramInt);
    }
  }
  
  @RequiresApi(21)
  static class ViewGroupCompatApi21Impl
    extends ViewGroupCompat.ViewGroupCompatApi18Impl
  {
    public int getNestedScrollAxes(ViewGroup paramViewGroup)
    {
      return paramViewGroup.getNestedScrollAxes();
    }
    
    public boolean isTransitionGroup(ViewGroup paramViewGroup)
    {
      return paramViewGroup.isTransitionGroup();
    }
    
    public void setTransitionGroup(ViewGroup paramViewGroup, boolean paramBoolean)
    {
      paramViewGroup.setTransitionGroup(paramBoolean);
    }
  }
  
  static class ViewGroupCompatBaseImpl
  {
    public int getLayoutMode(ViewGroup paramViewGroup)
    {
      return 0;
    }
    
    public int getNestedScrollAxes(ViewGroup paramViewGroup)
    {
      if ((paramViewGroup instanceof NestedScrollingParent)) {
        return ((NestedScrollingParent)paramViewGroup).getNestedScrollAxes();
      }
      return 0;
    }
    
    public boolean isTransitionGroup(ViewGroup paramViewGroup)
    {
      return false;
    }
    
    public void setLayoutMode(ViewGroup paramViewGroup, int paramInt) {}
    
    public void setTransitionGroup(ViewGroup paramViewGroup, boolean paramBoolean) {}
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v4/view/ViewGroupCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */