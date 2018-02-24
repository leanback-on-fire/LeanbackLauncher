package android.support.v4.view;

import android.view.KeyEvent;
import android.view.KeyEvent.Callback;
import android.view.KeyEvent.DispatcherState;
import android.view.View;

@Deprecated
public final class KeyEventCompat
{
  @Deprecated
  public static boolean dispatch(KeyEvent paramKeyEvent, KeyEvent.Callback paramCallback, Object paramObject1, Object paramObject2)
  {
    return paramKeyEvent.dispatch(paramCallback, (KeyEvent.DispatcherState)paramObject1, paramObject2);
  }
  
  @Deprecated
  public static Object getKeyDispatcherState(View paramView)
  {
    return paramView.getKeyDispatcherState();
  }
  
  @Deprecated
  public static boolean hasModifiers(KeyEvent paramKeyEvent, int paramInt)
  {
    return paramKeyEvent.hasModifiers(paramInt);
  }
  
  @Deprecated
  public static boolean hasNoModifiers(KeyEvent paramKeyEvent)
  {
    return paramKeyEvent.hasNoModifiers();
  }
  
  @Deprecated
  public static boolean isCtrlPressed(KeyEvent paramKeyEvent)
  {
    return paramKeyEvent.isCtrlPressed();
  }
  
  @Deprecated
  public static boolean isTracking(KeyEvent paramKeyEvent)
  {
    return paramKeyEvent.isTracking();
  }
  
  @Deprecated
  public static boolean metaStateHasModifiers(int paramInt1, int paramInt2)
  {
    return KeyEvent.metaStateHasModifiers(paramInt1, paramInt2);
  }
  
  @Deprecated
  public static boolean metaStateHasNoModifiers(int paramInt)
  {
    return KeyEvent.metaStateHasNoModifiers(paramInt);
  }
  
  @Deprecated
  public static int normalizeMetaState(int paramInt)
  {
    return KeyEvent.normalizeMetaState(paramInt);
  }
  
  @Deprecated
  public static void startTracking(KeyEvent paramKeyEvent)
  {
    paramKeyEvent.startTracking();
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v4/view/KeyEventCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */