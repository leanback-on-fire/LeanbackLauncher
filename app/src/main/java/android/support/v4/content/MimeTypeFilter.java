package android.support.v4.content;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.ArrayList;

public final class MimeTypeFilter
{
  public static String matches(@Nullable String paramString, @NonNull String[] paramArrayOfString)
  {
    if (paramString == null)
    {
      paramString = null;
      return paramString;
    }
    String[] arrayOfString = paramString.split("/");
    int j = paramArrayOfString.length;
    int i = 0;
    for (;;)
    {
      if (i >= j) {
        break label56;
      }
      String str = paramArrayOfString[i];
      paramString = str;
      if (mimeTypeAgainstFilter(arrayOfString, str.split("/"))) {
        break;
      }
      i += 1;
    }
    label56:
    return null;
  }
  
  public static String matches(@Nullable String[] paramArrayOfString, @NonNull String paramString)
  {
    if (paramArrayOfString == null)
    {
      paramString = null;
      return paramString;
    }
    String[] arrayOfString = paramString.split("/");
    int j = paramArrayOfString.length;
    int i = 0;
    for (;;)
    {
      if (i >= j) {
        break label56;
      }
      String str = paramArrayOfString[i];
      paramString = str;
      if (mimeTypeAgainstFilter(str.split("/"), arrayOfString)) {
        break;
      }
      i += 1;
    }
    label56:
    return null;
  }
  
  public static boolean matches(@Nullable String paramString1, @NonNull String paramString2)
  {
    if (paramString1 == null) {
      return false;
    }
    return mimeTypeAgainstFilter(paramString1.split("/"), paramString2.split("/"));
  }
  
  public static String[] matchesMany(@Nullable String[] paramArrayOfString, @NonNull String paramString)
  {
    int i = 0;
    if (paramArrayOfString == null) {
      return new String[0];
    }
    ArrayList localArrayList = new ArrayList();
    paramString = paramString.split("/");
    int j = paramArrayOfString.length;
    while (i < j)
    {
      String str = paramArrayOfString[i];
      if (mimeTypeAgainstFilter(str.split("/"), paramString)) {
        localArrayList.add(str);
      }
      i += 1;
    }
    return (String[])localArrayList.toArray(new String[localArrayList.size()]);
  }
  
  private static boolean mimeTypeAgainstFilter(@NonNull String[] paramArrayOfString1, @NonNull String[] paramArrayOfString2)
  {
    if (paramArrayOfString2.length != 2) {
      throw new IllegalArgumentException("Ill-formatted MIME type filter. Must be type/subtype.");
    }
    if ((paramArrayOfString2[0].isEmpty()) || (paramArrayOfString2[1].isEmpty())) {
      throw new IllegalArgumentException("Ill-formatted MIME type filter. Type or subtype empty.");
    }
    if (paramArrayOfString1.length != 2) {}
    while (((!"*".equals(paramArrayOfString2[0])) && (!paramArrayOfString2[0].equals(paramArrayOfString1[0]))) || ((!"*".equals(paramArrayOfString2[1])) && (!paramArrayOfString2[1].equals(paramArrayOfString1[1])))) {
      return false;
    }
    return true;
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v4/content/MimeTypeFilter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */