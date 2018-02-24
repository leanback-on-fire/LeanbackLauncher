package android.support.v4.graphics.drawable;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.graphics.drawable.DrawableContainer.DrawableContainerState;
import android.graphics.drawable.InsetDrawable;
import android.os.Build.VERSION;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import java.io.IOException;
import java.lang.reflect.Method;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public final class DrawableCompat
{
  static final DrawableCompatBaseImpl IMPL = new DrawableCompatBaseImpl();
  
  static
  {
    if (Build.VERSION.SDK_INT >= 23)
    {
      IMPL = new DrawableCompatApi23Impl();
      return;
    }
    if (Build.VERSION.SDK_INT >= 21)
    {
      IMPL = new DrawableCompatApi21Impl();
      return;
    }
    if (Build.VERSION.SDK_INT >= 19)
    {
      IMPL = new DrawableCompatApi19Impl();
      return;
    }
    if (Build.VERSION.SDK_INT >= 17)
    {
      IMPL = new DrawableCompatApi17Impl();
      return;
    }
  }
  
  public static void applyTheme(@NonNull Drawable paramDrawable, @NonNull Resources.Theme paramTheme)
  {
    IMPL.applyTheme(paramDrawable, paramTheme);
  }
  
  public static boolean canApplyTheme(@NonNull Drawable paramDrawable)
  {
    return IMPL.canApplyTheme(paramDrawable);
  }
  
  public static void clearColorFilter(@NonNull Drawable paramDrawable)
  {
    IMPL.clearColorFilter(paramDrawable);
  }
  
  public static int getAlpha(@NonNull Drawable paramDrawable)
  {
    return IMPL.getAlpha(paramDrawable);
  }
  
  public static ColorFilter getColorFilter(@NonNull Drawable paramDrawable)
  {
    return IMPL.getColorFilter(paramDrawable);
  }
  
  public static int getLayoutDirection(@NonNull Drawable paramDrawable)
  {
    return IMPL.getLayoutDirection(paramDrawable);
  }
  
  public static void inflate(@NonNull Drawable paramDrawable, @NonNull Resources paramResources, @NonNull XmlPullParser paramXmlPullParser, @NonNull AttributeSet paramAttributeSet, @Nullable Resources.Theme paramTheme)
    throws XmlPullParserException, IOException
  {
    IMPL.inflate(paramDrawable, paramResources, paramXmlPullParser, paramAttributeSet, paramTheme);
  }
  
  public static boolean isAutoMirrored(@NonNull Drawable paramDrawable)
  {
    return IMPL.isAutoMirrored(paramDrawable);
  }
  
  public static void jumpToCurrentState(@NonNull Drawable paramDrawable)
  {
    IMPL.jumpToCurrentState(paramDrawable);
  }
  
  public static void setAutoMirrored(@NonNull Drawable paramDrawable, boolean paramBoolean)
  {
    IMPL.setAutoMirrored(paramDrawable, paramBoolean);
  }
  
  public static void setHotspot(@NonNull Drawable paramDrawable, float paramFloat1, float paramFloat2)
  {
    IMPL.setHotspot(paramDrawable, paramFloat1, paramFloat2);
  }
  
  public static void setHotspotBounds(@NonNull Drawable paramDrawable, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    IMPL.setHotspotBounds(paramDrawable, paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public static boolean setLayoutDirection(@NonNull Drawable paramDrawable, int paramInt)
  {
    return IMPL.setLayoutDirection(paramDrawable, paramInt);
  }
  
  public static void setTint(@NonNull Drawable paramDrawable, @ColorInt int paramInt)
  {
    IMPL.setTint(paramDrawable, paramInt);
  }
  
  public static void setTintList(@NonNull Drawable paramDrawable, @Nullable ColorStateList paramColorStateList)
  {
    IMPL.setTintList(paramDrawable, paramColorStateList);
  }
  
  public static void setTintMode(@NonNull Drawable paramDrawable, @Nullable PorterDuff.Mode paramMode)
  {
    IMPL.setTintMode(paramDrawable, paramMode);
  }
  
  public static <T extends Drawable> T unwrap(@NonNull Drawable paramDrawable)
  {
    Drawable localDrawable = paramDrawable;
    if ((paramDrawable instanceof DrawableWrapper)) {
      localDrawable = ((DrawableWrapper)paramDrawable).getWrappedDrawable();
    }
    return localDrawable;
  }
  
  public static Drawable wrap(@NonNull Drawable paramDrawable)
  {
    return IMPL.wrap(paramDrawable);
  }
  
  @RequiresApi(17)
  static class DrawableCompatApi17Impl
    extends DrawableCompat.DrawableCompatBaseImpl
  {
    private static final String TAG = "DrawableCompatApi17";
    private static Method sGetLayoutDirectionMethod;
    private static boolean sGetLayoutDirectionMethodFetched;
    private static Method sSetLayoutDirectionMethod;
    private static boolean sSetLayoutDirectionMethodFetched;
    
    public int getLayoutDirection(Drawable paramDrawable)
    {
      if (!sGetLayoutDirectionMethodFetched) {}
      try
      {
        sGetLayoutDirectionMethod = Drawable.class.getDeclaredMethod("getLayoutDirection", new Class[0]);
        sGetLayoutDirectionMethod.setAccessible(true);
        sGetLayoutDirectionMethodFetched = true;
        if (sGetLayoutDirectionMethod == null) {}
      }
      catch (NoSuchMethodException localNoSuchMethodException)
      {
        for (;;)
        {
          try
          {
            int i = ((Integer)sGetLayoutDirectionMethod.invoke(paramDrawable, new Object[0])).intValue();
            return i;
          }
          catch (Exception paramDrawable)
          {
            Log.i("DrawableCompatApi17", "Failed to invoke getLayoutDirection() via reflection", paramDrawable);
            sGetLayoutDirectionMethod = null;
          }
          localNoSuchMethodException = localNoSuchMethodException;
          Log.i("DrawableCompatApi17", "Failed to retrieve getLayoutDirection() method", localNoSuchMethodException);
        }
      }
      return 0;
    }
    
    public boolean setLayoutDirection(Drawable paramDrawable, int paramInt)
    {
      if (!sSetLayoutDirectionMethodFetched) {}
      try
      {
        sSetLayoutDirectionMethod = Drawable.class.getDeclaredMethod("setLayoutDirection", new Class[] { Integer.TYPE });
        sSetLayoutDirectionMethod.setAccessible(true);
        sSetLayoutDirectionMethodFetched = true;
        if (sSetLayoutDirectionMethod == null) {}
      }
      catch (NoSuchMethodException localNoSuchMethodException)
      {
        for (;;)
        {
          try
          {
            sSetLayoutDirectionMethod.invoke(paramDrawable, new Object[] { Integer.valueOf(paramInt) });
            return true;
          }
          catch (Exception paramDrawable)
          {
            Log.i("DrawableCompatApi17", "Failed to invoke setLayoutDirection(int) via reflection", paramDrawable);
            sSetLayoutDirectionMethod = null;
          }
          localNoSuchMethodException = localNoSuchMethodException;
          Log.i("DrawableCompatApi17", "Failed to retrieve setLayoutDirection(int) method", localNoSuchMethodException);
        }
      }
      return false;
    }
  }
  
  @RequiresApi(19)
  static class DrawableCompatApi19Impl
    extends DrawableCompat.DrawableCompatApi17Impl
  {
    public int getAlpha(Drawable paramDrawable)
    {
      return paramDrawable.getAlpha();
    }
    
    public boolean isAutoMirrored(Drawable paramDrawable)
    {
      return paramDrawable.isAutoMirrored();
    }
    
    public void setAutoMirrored(Drawable paramDrawable, boolean paramBoolean)
    {
      paramDrawable.setAutoMirrored(paramBoolean);
    }
    
    public Drawable wrap(Drawable paramDrawable)
    {
      Object localObject = paramDrawable;
      if (!(paramDrawable instanceof TintAwareDrawable)) {
        localObject = new DrawableWrapperApi19(paramDrawable);
      }
      return (Drawable)localObject;
    }
  }
  
  @RequiresApi(21)
  static class DrawableCompatApi21Impl
    extends DrawableCompat.DrawableCompatApi19Impl
  {
    public void applyTheme(Drawable paramDrawable, Resources.Theme paramTheme)
    {
      paramDrawable.applyTheme(paramTheme);
    }
    
    public boolean canApplyTheme(Drawable paramDrawable)
    {
      return paramDrawable.canApplyTheme();
    }
    
    public void clearColorFilter(Drawable paramDrawable)
    {
      paramDrawable.clearColorFilter();
      if ((paramDrawable instanceof InsetDrawable)) {
        clearColorFilter(((InsetDrawable)paramDrawable).getDrawable());
      }
      for (;;)
      {
        return;
        if ((paramDrawable instanceof DrawableWrapper))
        {
          clearColorFilter(((DrawableWrapper)paramDrawable).getWrappedDrawable());
          return;
        }
        if ((paramDrawable instanceof DrawableContainer))
        {
          paramDrawable = (DrawableContainer.DrawableContainerState)((DrawableContainer)paramDrawable).getConstantState();
          if (paramDrawable != null)
          {
            int i = 0;
            int j = paramDrawable.getChildCount();
            while (i < j)
            {
              Drawable localDrawable = paramDrawable.getChild(i);
              if (localDrawable != null) {
                clearColorFilter(localDrawable);
              }
              i += 1;
            }
          }
        }
      }
    }
    
    public ColorFilter getColorFilter(Drawable paramDrawable)
    {
      return paramDrawable.getColorFilter();
    }
    
    public void inflate(Drawable paramDrawable, Resources paramResources, XmlPullParser paramXmlPullParser, AttributeSet paramAttributeSet, Resources.Theme paramTheme)
      throws IOException, XmlPullParserException
    {
      paramDrawable.inflate(paramResources, paramXmlPullParser, paramAttributeSet, paramTheme);
    }
    
    public void setHotspot(Drawable paramDrawable, float paramFloat1, float paramFloat2)
    {
      paramDrawable.setHotspot(paramFloat1, paramFloat2);
    }
    
    public void setHotspotBounds(Drawable paramDrawable, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      paramDrawable.setHotspotBounds(paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void setTint(Drawable paramDrawable, int paramInt)
    {
      paramDrawable.setTint(paramInt);
    }
    
    public void setTintList(Drawable paramDrawable, ColorStateList paramColorStateList)
    {
      paramDrawable.setTintList(paramColorStateList);
    }
    
    public void setTintMode(Drawable paramDrawable, PorterDuff.Mode paramMode)
    {
      paramDrawable.setTintMode(paramMode);
    }
    
    public Drawable wrap(Drawable paramDrawable)
    {
      Object localObject = paramDrawable;
      if (!(paramDrawable instanceof TintAwareDrawable)) {
        localObject = new DrawableWrapperApi21(paramDrawable);
      }
      return (Drawable)localObject;
    }
  }
  
  @RequiresApi(23)
  static class DrawableCompatApi23Impl
    extends DrawableCompat.DrawableCompatApi21Impl
  {
    public void clearColorFilter(Drawable paramDrawable)
    {
      paramDrawable.clearColorFilter();
    }
    
    public int getLayoutDirection(Drawable paramDrawable)
    {
      return paramDrawable.getLayoutDirection();
    }
    
    public boolean setLayoutDirection(Drawable paramDrawable, int paramInt)
    {
      return paramDrawable.setLayoutDirection(paramInt);
    }
    
    public Drawable wrap(Drawable paramDrawable)
    {
      return paramDrawable;
    }
  }
  
  static class DrawableCompatBaseImpl
  {
    public void applyTheme(Drawable paramDrawable, Resources.Theme paramTheme) {}
    
    public boolean canApplyTheme(Drawable paramDrawable)
    {
      return false;
    }
    
    public void clearColorFilter(Drawable paramDrawable)
    {
      paramDrawable.clearColorFilter();
    }
    
    public int getAlpha(Drawable paramDrawable)
    {
      return 0;
    }
    
    public ColorFilter getColorFilter(Drawable paramDrawable)
    {
      return null;
    }
    
    public int getLayoutDirection(Drawable paramDrawable)
    {
      return 0;
    }
    
    public void inflate(Drawable paramDrawable, Resources paramResources, XmlPullParser paramXmlPullParser, AttributeSet paramAttributeSet, Resources.Theme paramTheme)
      throws IOException, XmlPullParserException
    {
      paramDrawable.inflate(paramResources, paramXmlPullParser, paramAttributeSet);
    }
    
    public boolean isAutoMirrored(Drawable paramDrawable)
    {
      return false;
    }
    
    public void jumpToCurrentState(Drawable paramDrawable)
    {
      paramDrawable.jumpToCurrentState();
    }
    
    public void setAutoMirrored(Drawable paramDrawable, boolean paramBoolean) {}
    
    public void setHotspot(Drawable paramDrawable, float paramFloat1, float paramFloat2) {}
    
    public void setHotspotBounds(Drawable paramDrawable, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {}
    
    public boolean setLayoutDirection(Drawable paramDrawable, int paramInt)
    {
      return false;
    }
    
    public void setTint(Drawable paramDrawable, int paramInt)
    {
      if ((paramDrawable instanceof TintAwareDrawable)) {
        ((TintAwareDrawable)paramDrawable).setTint(paramInt);
      }
    }
    
    public void setTintList(Drawable paramDrawable, ColorStateList paramColorStateList)
    {
      if ((paramDrawable instanceof TintAwareDrawable)) {
        ((TintAwareDrawable)paramDrawable).setTintList(paramColorStateList);
      }
    }
    
    public void setTintMode(Drawable paramDrawable, PorterDuff.Mode paramMode)
    {
      if ((paramDrawable instanceof TintAwareDrawable)) {
        ((TintAwareDrawable)paramDrawable).setTintMode(paramMode);
      }
    }
    
    public Drawable wrap(Drawable paramDrawable)
    {
      Object localObject = paramDrawable;
      if (!(paramDrawable instanceof TintAwareDrawable)) {
        localObject = new DrawableWrapperApi14(paramDrawable);
      }
      return (Drawable)localObject;
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v4/graphics/drawable/DrawableCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */