package android.support.v4.os;

import android.support.annotation.RestrictTo;
import java.util.Locale;

@RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
final class LocaleHelper
{
  static Locale forLanguageTag(String paramString)
  {
    String[] arrayOfString;
    if (paramString.contains("-"))
    {
      arrayOfString = paramString.split("-");
      if (arrayOfString.length > 2) {
        return new Locale(arrayOfString[0], arrayOfString[1], arrayOfString[2]);
      }
      if (arrayOfString.length > 1) {
        return new Locale(arrayOfString[0], arrayOfString[1]);
      }
      if (arrayOfString.length == 1) {
        return new Locale(arrayOfString[0]);
      }
    }
    else if (paramString.contains("_"))
    {
      arrayOfString = paramString.split("_");
      if (arrayOfString.length > 2) {
        return new Locale(arrayOfString[0], arrayOfString[1], arrayOfString[2]);
      }
      if (arrayOfString.length > 1) {
        return new Locale(arrayOfString[0], arrayOfString[1]);
      }
      if (arrayOfString.length == 1) {
        return new Locale(arrayOfString[0]);
      }
    }
    else
    {
      return new Locale(paramString);
    }
    throw new IllegalArgumentException("Can not parse language tag: [" + paramString + "]");
  }
  
  static String toLanguageTag(Locale paramLocale)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(paramLocale.getLanguage());
    String str = paramLocale.getCountry();
    if ((str != null) && (!str.isEmpty()))
    {
      localStringBuilder.append("-");
      localStringBuilder.append(paramLocale.getCountry());
    }
    return localStringBuilder.toString();
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v4/os/LocaleHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */