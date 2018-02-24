package android.support.v4.widget;

import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;
import android.support.annotation.StyleRes;
import android.util.Log;
import android.widget.TextView;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;

public final class TextViewCompat
{
  public static final int AUTO_SIZE_TEXT_TYPE_NONE = 0;
  public static final int AUTO_SIZE_TEXT_TYPE_UNIFORM = 1;
  static final TextViewCompatBaseImpl IMPL = new TextViewCompatBaseImpl();
  
  static
  {
    if (Build.VERSION.SDK_INT >= 26)
    {
      IMPL = new TextViewCompatApi26Impl();
      return;
    }
    if (Build.VERSION.SDK_INT >= 23)
    {
      IMPL = new TextViewCompatApi23Impl();
      return;
    }
    if (Build.VERSION.SDK_INT >= 18)
    {
      IMPL = new TextViewCompatApi18Impl();
      return;
    }
    if (Build.VERSION.SDK_INT >= 17)
    {
      IMPL = new TextViewCompatApi17Impl();
      return;
    }
    if (Build.VERSION.SDK_INT >= 16)
    {
      IMPL = new TextViewCompatApi16Impl();
      return;
    }
  }
  
  public static int getAutoSizeMaxTextSize(TextView paramTextView)
  {
    return IMPL.getAutoSizeMaxTextSize(paramTextView);
  }
  
  public static int getAutoSizeMinTextSize(TextView paramTextView)
  {
    return IMPL.getAutoSizeMinTextSize(paramTextView);
  }
  
  public static int getAutoSizeStepGranularity(TextView paramTextView)
  {
    return IMPL.getAutoSizeStepGranularity(paramTextView);
  }
  
  public static int[] getAutoSizeTextAvailableSizes(TextView paramTextView)
  {
    return IMPL.getAutoSizeTextAvailableSizes(paramTextView);
  }
  
  public static int getAutoSizeTextType(TextView paramTextView)
  {
    return IMPL.getAutoSizeTextType(paramTextView);
  }
  
  public static Drawable[] getCompoundDrawablesRelative(@NonNull TextView paramTextView)
  {
    return IMPL.getCompoundDrawablesRelative(paramTextView);
  }
  
  public static int getMaxLines(@NonNull TextView paramTextView)
  {
    return IMPL.getMaxLines(paramTextView);
  }
  
  public static int getMinLines(@NonNull TextView paramTextView)
  {
    return IMPL.getMinLines(paramTextView);
  }
  
  public static void setAutoSizeTextTypeUniformWithConfiguration(TextView paramTextView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    throws IllegalArgumentException
  {
    IMPL.setAutoSizeTextTypeUniformWithConfiguration(paramTextView, paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public static void setAutoSizeTextTypeUniformWithPresetSizes(TextView paramTextView, @NonNull int[] paramArrayOfInt, int paramInt)
    throws IllegalArgumentException
  {
    IMPL.setAutoSizeTextTypeUniformWithPresetSizes(paramTextView, paramArrayOfInt, paramInt);
  }
  
  public static void setAutoSizeTextTypeWithDefaults(TextView paramTextView, int paramInt)
  {
    IMPL.setAutoSizeTextTypeWithDefaults(paramTextView, paramInt);
  }
  
  public static void setCompoundDrawablesRelative(@NonNull TextView paramTextView, @Nullable Drawable paramDrawable1, @Nullable Drawable paramDrawable2, @Nullable Drawable paramDrawable3, @Nullable Drawable paramDrawable4)
  {
    IMPL.setCompoundDrawablesRelative(paramTextView, paramDrawable1, paramDrawable2, paramDrawable3, paramDrawable4);
  }
  
  public static void setCompoundDrawablesRelativeWithIntrinsicBounds(@NonNull TextView paramTextView, @DrawableRes int paramInt1, @DrawableRes int paramInt2, @DrawableRes int paramInt3, @DrawableRes int paramInt4)
  {
    IMPL.setCompoundDrawablesRelativeWithIntrinsicBounds(paramTextView, paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public static void setCompoundDrawablesRelativeWithIntrinsicBounds(@NonNull TextView paramTextView, @Nullable Drawable paramDrawable1, @Nullable Drawable paramDrawable2, @Nullable Drawable paramDrawable3, @Nullable Drawable paramDrawable4)
  {
    IMPL.setCompoundDrawablesRelativeWithIntrinsicBounds(paramTextView, paramDrawable1, paramDrawable2, paramDrawable3, paramDrawable4);
  }
  
  public static void setTextAppearance(@NonNull TextView paramTextView, @StyleRes int paramInt)
  {
    IMPL.setTextAppearance(paramTextView, paramInt);
  }
  
  @Retention(RetentionPolicy.SOURCE)
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  public static @interface AutoSizeTextType {}
  
  @RequiresApi(16)
  static class TextViewCompatApi16Impl
    extends TextViewCompat.TextViewCompatBaseImpl
  {
    public int getMaxLines(TextView paramTextView)
    {
      return paramTextView.getMaxLines();
    }
    
    public int getMinLines(TextView paramTextView)
    {
      return paramTextView.getMinLines();
    }
  }
  
  @RequiresApi(17)
  static class TextViewCompatApi17Impl
    extends TextViewCompat.TextViewCompatApi16Impl
  {
    public Drawable[] getCompoundDrawablesRelative(@NonNull TextView paramTextView)
    {
      int i = 1;
      if (paramTextView.getLayoutDirection() == 1) {}
      for (;;)
      {
        paramTextView = paramTextView.getCompoundDrawables();
        if (i != 0)
        {
          Object localObject1 = paramTextView[2];
          Object localObject2 = paramTextView[0];
          paramTextView[0] = localObject1;
          paramTextView[2] = localObject2;
        }
        return paramTextView;
        i = 0;
      }
    }
    
    public void setCompoundDrawablesRelative(@NonNull TextView paramTextView, @Nullable Drawable paramDrawable1, @Nullable Drawable paramDrawable2, @Nullable Drawable paramDrawable3, @Nullable Drawable paramDrawable4)
    {
      int i = 1;
      Drawable localDrawable;
      if (paramTextView.getLayoutDirection() == 1)
      {
        if (i == 0) {
          break label42;
        }
        localDrawable = paramDrawable3;
        label20:
        if (i == 0) {
          break label48;
        }
      }
      for (;;)
      {
        paramTextView.setCompoundDrawables(localDrawable, paramDrawable2, paramDrawable1, paramDrawable4);
        return;
        i = 0;
        break;
        label42:
        localDrawable = paramDrawable1;
        break label20;
        label48:
        paramDrawable1 = paramDrawable3;
      }
    }
    
    public void setCompoundDrawablesRelativeWithIntrinsicBounds(@NonNull TextView paramTextView, @DrawableRes int paramInt1, @DrawableRes int paramInt2, @DrawableRes int paramInt3, @DrawableRes int paramInt4)
    {
      int i = 1;
      int j;
      if (paramTextView.getLayoutDirection() == 1)
      {
        if (i == 0) {
          break label42;
        }
        j = paramInt3;
        label20:
        if (i == 0) {
          break label48;
        }
      }
      for (;;)
      {
        paramTextView.setCompoundDrawablesWithIntrinsicBounds(j, paramInt2, paramInt1, paramInt4);
        return;
        i = 0;
        break;
        label42:
        j = paramInt1;
        break label20;
        label48:
        paramInt1 = paramInt3;
      }
    }
    
    public void setCompoundDrawablesRelativeWithIntrinsicBounds(@NonNull TextView paramTextView, @Nullable Drawable paramDrawable1, @Nullable Drawable paramDrawable2, @Nullable Drawable paramDrawable3, @Nullable Drawable paramDrawable4)
    {
      int i = 1;
      Drawable localDrawable;
      if (paramTextView.getLayoutDirection() == 1)
      {
        if (i == 0) {
          break label42;
        }
        localDrawable = paramDrawable3;
        label20:
        if (i == 0) {
          break label48;
        }
      }
      for (;;)
      {
        paramTextView.setCompoundDrawablesWithIntrinsicBounds(localDrawable, paramDrawable2, paramDrawable1, paramDrawable4);
        return;
        i = 0;
        break;
        label42:
        localDrawable = paramDrawable1;
        break label20;
        label48:
        paramDrawable1 = paramDrawable3;
      }
    }
  }
  
  @RequiresApi(18)
  static class TextViewCompatApi18Impl
    extends TextViewCompat.TextViewCompatApi17Impl
  {
    public Drawable[] getCompoundDrawablesRelative(@NonNull TextView paramTextView)
    {
      return paramTextView.getCompoundDrawablesRelative();
    }
    
    public void setCompoundDrawablesRelative(@NonNull TextView paramTextView, @Nullable Drawable paramDrawable1, @Nullable Drawable paramDrawable2, @Nullable Drawable paramDrawable3, @Nullable Drawable paramDrawable4)
    {
      paramTextView.setCompoundDrawablesRelative(paramDrawable1, paramDrawable2, paramDrawable3, paramDrawable4);
    }
    
    public void setCompoundDrawablesRelativeWithIntrinsicBounds(@NonNull TextView paramTextView, @DrawableRes int paramInt1, @DrawableRes int paramInt2, @DrawableRes int paramInt3, @DrawableRes int paramInt4)
    {
      paramTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void setCompoundDrawablesRelativeWithIntrinsicBounds(@NonNull TextView paramTextView, @Nullable Drawable paramDrawable1, @Nullable Drawable paramDrawable2, @Nullable Drawable paramDrawable3, @Nullable Drawable paramDrawable4)
    {
      paramTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(paramDrawable1, paramDrawable2, paramDrawable3, paramDrawable4);
    }
  }
  
  @RequiresApi(23)
  static class TextViewCompatApi23Impl
    extends TextViewCompat.TextViewCompatApi18Impl
  {
    public void setTextAppearance(@NonNull TextView paramTextView, @StyleRes int paramInt)
    {
      paramTextView.setTextAppearance(paramInt);
    }
  }
  
  @RequiresApi(26)
  static class TextViewCompatApi26Impl
    extends TextViewCompat.TextViewCompatApi23Impl
  {
    public int getAutoSizeMaxTextSize(TextView paramTextView)
    {
      return paramTextView.getAutoSizeMaxTextSize();
    }
    
    public int getAutoSizeMinTextSize(TextView paramTextView)
    {
      return paramTextView.getAutoSizeMinTextSize();
    }
    
    public int getAutoSizeStepGranularity(TextView paramTextView)
    {
      return paramTextView.getAutoSizeStepGranularity();
    }
    
    public int[] getAutoSizeTextAvailableSizes(TextView paramTextView)
    {
      return paramTextView.getAutoSizeTextAvailableSizes();
    }
    
    public int getAutoSizeTextType(TextView paramTextView)
    {
      return paramTextView.getAutoSizeTextType();
    }
    
    public void setAutoSizeTextTypeUniformWithConfiguration(TextView paramTextView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
      throws IllegalArgumentException
    {
      paramTextView.setAutoSizeTextTypeUniformWithConfiguration(paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void setAutoSizeTextTypeUniformWithPresetSizes(TextView paramTextView, @NonNull int[] paramArrayOfInt, int paramInt)
      throws IllegalArgumentException
    {
      paramTextView.setAutoSizeTextTypeUniformWithPresetSizes(paramArrayOfInt, paramInt);
    }
    
    public void setAutoSizeTextTypeWithDefaults(TextView paramTextView, int paramInt)
    {
      paramTextView.setAutoSizeTextTypeWithDefaults(paramInt);
    }
  }
  
  static class TextViewCompatBaseImpl
  {
    private static final int LINES = 1;
    private static final String LOG_TAG = "TextViewCompatBase";
    private static Field sMaxModeField;
    private static boolean sMaxModeFieldFetched;
    private static Field sMaximumField;
    private static boolean sMaximumFieldFetched;
    private static Field sMinModeField;
    private static boolean sMinModeFieldFetched;
    private static Field sMinimumField;
    private static boolean sMinimumFieldFetched;
    
    private static Field retrieveField(String paramString)
    {
      Object localObject = null;
      try
      {
        Field localField = TextView.class.getDeclaredField(paramString);
        localObject = localField;
        localField.setAccessible(true);
        return localField;
      }
      catch (NoSuchFieldException localNoSuchFieldException)
      {
        Log.e("TextViewCompatBase", "Could not retrieve " + paramString + " field.");
      }
      return (Field)localObject;
    }
    
    private static int retrieveIntFromField(Field paramField, TextView paramTextView)
    {
      try
      {
        int i = paramField.getInt(paramTextView);
        return i;
      }
      catch (IllegalAccessException paramTextView)
      {
        Log.d("TextViewCompatBase", "Could not retrieve value of " + paramField.getName() + " field.");
      }
      return -1;
    }
    
    public int getAutoSizeMaxTextSize(TextView paramTextView)
    {
      if ((paramTextView instanceof AutoSizeableTextView)) {
        return ((AutoSizeableTextView)paramTextView).getAutoSizeMaxTextSize();
      }
      return -1;
    }
    
    public int getAutoSizeMinTextSize(TextView paramTextView)
    {
      if ((paramTextView instanceof AutoSizeableTextView)) {
        return ((AutoSizeableTextView)paramTextView).getAutoSizeMinTextSize();
      }
      return -1;
    }
    
    public int getAutoSizeStepGranularity(TextView paramTextView)
    {
      if ((paramTextView instanceof AutoSizeableTextView)) {
        return ((AutoSizeableTextView)paramTextView).getAutoSizeStepGranularity();
      }
      return -1;
    }
    
    public int[] getAutoSizeTextAvailableSizes(TextView paramTextView)
    {
      if ((paramTextView instanceof AutoSizeableTextView)) {
        return ((AutoSizeableTextView)paramTextView).getAutoSizeTextAvailableSizes();
      }
      return new int[0];
    }
    
    public int getAutoSizeTextType(TextView paramTextView)
    {
      if ((paramTextView instanceof AutoSizeableTextView)) {
        return ((AutoSizeableTextView)paramTextView).getAutoSizeTextType();
      }
      return 0;
    }
    
    public Drawable[] getCompoundDrawablesRelative(@NonNull TextView paramTextView)
    {
      return paramTextView.getCompoundDrawables();
    }
    
    public int getMaxLines(TextView paramTextView)
    {
      if (!sMaxModeFieldFetched)
      {
        sMaxModeField = retrieveField("mMaxMode");
        sMaxModeFieldFetched = true;
      }
      if ((sMaxModeField != null) && (retrieveIntFromField(sMaxModeField, paramTextView) == 1))
      {
        if (!sMaximumFieldFetched)
        {
          sMaximumField = retrieveField("mMaximum");
          sMaximumFieldFetched = true;
        }
        if (sMaximumField != null) {
          return retrieveIntFromField(sMaximumField, paramTextView);
        }
      }
      return -1;
    }
    
    public int getMinLines(TextView paramTextView)
    {
      if (!sMinModeFieldFetched)
      {
        sMinModeField = retrieveField("mMinMode");
        sMinModeFieldFetched = true;
      }
      if ((sMinModeField != null) && (retrieveIntFromField(sMinModeField, paramTextView) == 1))
      {
        if (!sMinimumFieldFetched)
        {
          sMinimumField = retrieveField("mMinimum");
          sMinimumFieldFetched = true;
        }
        if (sMinimumField != null) {
          return retrieveIntFromField(sMinimumField, paramTextView);
        }
      }
      return -1;
    }
    
    public void setAutoSizeTextTypeUniformWithConfiguration(TextView paramTextView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
      throws IllegalArgumentException
    {
      if ((paramTextView instanceof AutoSizeableTextView)) {
        ((AutoSizeableTextView)paramTextView).setAutoSizeTextTypeUniformWithConfiguration(paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void setAutoSizeTextTypeUniformWithPresetSizes(TextView paramTextView, @NonNull int[] paramArrayOfInt, int paramInt)
      throws IllegalArgumentException
    {
      if ((paramTextView instanceof AutoSizeableTextView)) {
        ((AutoSizeableTextView)paramTextView).setAutoSizeTextTypeUniformWithPresetSizes(paramArrayOfInt, paramInt);
      }
    }
    
    public void setAutoSizeTextTypeWithDefaults(TextView paramTextView, int paramInt)
    {
      if ((paramTextView instanceof AutoSizeableTextView)) {
        ((AutoSizeableTextView)paramTextView).setAutoSizeTextTypeWithDefaults(paramInt);
      }
    }
    
    public void setCompoundDrawablesRelative(@NonNull TextView paramTextView, @Nullable Drawable paramDrawable1, @Nullable Drawable paramDrawable2, @Nullable Drawable paramDrawable3, @Nullable Drawable paramDrawable4)
    {
      paramTextView.setCompoundDrawables(paramDrawable1, paramDrawable2, paramDrawable3, paramDrawable4);
    }
    
    public void setCompoundDrawablesRelativeWithIntrinsicBounds(@NonNull TextView paramTextView, @DrawableRes int paramInt1, @DrawableRes int paramInt2, @DrawableRes int paramInt3, @DrawableRes int paramInt4)
    {
      paramTextView.setCompoundDrawablesWithIntrinsicBounds(paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void setCompoundDrawablesRelativeWithIntrinsicBounds(@NonNull TextView paramTextView, @Nullable Drawable paramDrawable1, @Nullable Drawable paramDrawable2, @Nullable Drawable paramDrawable3, @Nullable Drawable paramDrawable4)
    {
      paramTextView.setCompoundDrawablesWithIntrinsicBounds(paramDrawable1, paramDrawable2, paramDrawable3, paramDrawable4);
    }
    
    public void setTextAppearance(TextView paramTextView, @StyleRes int paramInt)
    {
      paramTextView.setTextAppearance(paramTextView.getContext(), paramInt);
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v4/widget/TextViewCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */