package android.support.v4.text;

import android.os.Build.VERSION;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import java.util.Locale;

public final class ICUCompat
{
  private static final ICUCompatBaseImpl IMPL = new ICUCompatBaseImpl();
  
  static
  {
    if (Build.VERSION.SDK_INT >= 21)
    {
      IMPL = new ICUCompatApi21Impl();
      return;
    }
  }
  
  @Nullable
  public static String maximizeAndGetScript(Locale paramLocale)
  {
    return IMPL.maximizeAndGetScript(paramLocale);
  }
  
  @RequiresApi(21)
  static class ICUCompatApi21Impl
    extends ICUCompat.ICUCompatBaseImpl
  {
    public String maximizeAndGetScript(Locale paramLocale)
    {
      return ICUCompatApi21.maximizeAndGetScript(paramLocale);
    }
  }
  
  static class ICUCompatBaseImpl
  {
    public String maximizeAndGetScript(Locale paramLocale)
    {
      return ICUCompatIcs.maximizeAndGetScript(paramLocale);
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v4/text/ICUCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */