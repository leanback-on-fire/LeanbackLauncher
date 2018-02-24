package android.support.v7.widget;

import android.content.Context;
import android.os.Build.VERSION;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.annotation.StyleRes;
import android.support.v4.widget.PopupWindowCompat;
import android.support.v7.appcompat.R.styleable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver.OnScrollChangedListener;
import android.widget.PopupWindow;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;

class AppCompatPopupWindow
  extends PopupWindow
{
  private static final boolean COMPAT_OVERLAP_ANCHOR;
  private static final String TAG = "AppCompatPopupWindow";
  private boolean mOverlapAnchor;
  
  static
  {
    if (Build.VERSION.SDK_INT < 21) {}
    for (boolean bool = true;; bool = false)
    {
      COMPAT_OVERLAP_ANCHOR = bool;
      return;
    }
  }
  
  public AppCompatPopupWindow(@NonNull Context paramContext, @Nullable AttributeSet paramAttributeSet, @AttrRes int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    init(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public AppCompatPopupWindow(@NonNull Context paramContext, @Nullable AttributeSet paramAttributeSet, @AttrRes int paramInt1, @StyleRes int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    init(paramContext, paramAttributeSet, paramInt1, paramInt2);
  }
  
  private void init(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    paramContext = TintTypedArray.obtainStyledAttributes(paramContext, paramAttributeSet, R.styleable.PopupWindow, paramInt1, paramInt2);
    if (paramContext.hasValue(R.styleable.PopupWindow_overlapAnchor)) {
      setSupportOverlapAnchor(paramContext.getBoolean(R.styleable.PopupWindow_overlapAnchor, false));
    }
    setBackgroundDrawable(paramContext.getDrawable(R.styleable.PopupWindow_android_popupBackground));
    paramInt1 = Build.VERSION.SDK_INT;
    if ((paramInt2 != 0) && (paramInt1 < 11) && (paramContext.hasValue(R.styleable.PopupWindow_android_popupAnimationStyle))) {
      setAnimationStyle(paramContext.getResourceId(R.styleable.PopupWindow_android_popupAnimationStyle, -1));
    }
    paramContext.recycle();
    if (Build.VERSION.SDK_INT < 14) {
      wrapOnScrollChangedListener(this);
    }
  }
  
  private static void wrapOnScrollChangedListener(final PopupWindow paramPopupWindow)
  {
    try
    {
      Field localField1 = PopupWindow.class.getDeclaredField("mAnchor");
      localField1.setAccessible(true);
      Field localField2 = PopupWindow.class.getDeclaredField("mOnScrollChangedListener");
      localField2.setAccessible(true);
      localField2.set(paramPopupWindow, new ViewTreeObserver.OnScrollChangedListener()
      {
        public void onScrollChanged()
        {
          try
          {
            WeakReference localWeakReference = (WeakReference)this.val$fieldAnchor.get(paramPopupWindow);
            if (localWeakReference != null)
            {
              if (localWeakReference.get() == null) {
                return;
              }
              this.val$originalListener.onScrollChanged();
              return;
            }
          }
          catch (IllegalAccessException localIllegalAccessException) {}
        }
      });
      return;
    }
    catch (Exception paramPopupWindow)
    {
      Log.d("AppCompatPopupWindow", "Exception while installing workaround OnScrollChangedListener", paramPopupWindow);
    }
  }
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  public boolean getSupportOverlapAnchor()
  {
    if (COMPAT_OVERLAP_ANCHOR) {
      return this.mOverlapAnchor;
    }
    return PopupWindowCompat.getOverlapAnchor(this);
  }
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  public void setSupportOverlapAnchor(boolean paramBoolean)
  {
    if (COMPAT_OVERLAP_ANCHOR)
    {
      this.mOverlapAnchor = paramBoolean;
      return;
    }
    PopupWindowCompat.setOverlapAnchor(this, paramBoolean);
  }
  
  public void showAsDropDown(View paramView, int paramInt1, int paramInt2)
  {
    int i = paramInt2;
    if (COMPAT_OVERLAP_ANCHOR)
    {
      i = paramInt2;
      if (this.mOverlapAnchor) {
        i = paramInt2 - paramView.getHeight();
      }
    }
    super.showAsDropDown(paramView, paramInt1, i);
  }
  
  public void showAsDropDown(View paramView, int paramInt1, int paramInt2, int paramInt3)
  {
    int i = paramInt2;
    if (COMPAT_OVERLAP_ANCHOR)
    {
      i = paramInt2;
      if (this.mOverlapAnchor) {
        i = paramInt2 - paramView.getHeight();
      }
    }
    super.showAsDropDown(paramView, paramInt1, i, paramInt3);
  }
  
  public void update(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int i = paramInt2;
    if (COMPAT_OVERLAP_ANCHOR)
    {
      i = paramInt2;
      if (this.mOverlapAnchor) {
        i = paramInt2 - paramView.getHeight();
      }
    }
    super.update(paramView, paramInt1, i, paramInt3, paramInt4);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v7/widget/AppCompatPopupWindow.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */