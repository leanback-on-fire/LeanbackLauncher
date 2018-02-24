package android.support.v4.graphics;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.CancellationSignal;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;
import android.support.v4.content.res.FontResourcesParserCompat.FontFamilyFilesResourceEntry;
import android.support.v4.content.res.FontResourcesParserCompat.FontFileResourceEntry;
import android.support.v4.provider.FontsContractCompat.FontInfo;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@RequiresApi(14)
@RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
class TypefaceCompatBaseImpl
  implements TypefaceCompat.TypefaceCompatImpl
{
  private static final String CACHE_FILE_PREFIX = "cached_font_";
  private static final String TAG = "TypefaceCompatBaseImpl";
  
  private FontResourcesParserCompat.FontFileResourceEntry findBestEntry(FontResourcesParserCompat.FontFamilyFilesResourceEntry paramFontFamilyFilesResourceEntry, int paramInt)
  {
    (FontResourcesParserCompat.FontFileResourceEntry)findBestFont(paramFontFamilyFilesResourceEntry.getEntries(), paramInt, new StyleExtractor()
    {
      public int getWeight(FontResourcesParserCompat.FontFileResourceEntry paramAnonymousFontFileResourceEntry)
      {
        return paramAnonymousFontFileResourceEntry.getWeight();
      }
      
      public boolean isItalic(FontResourcesParserCompat.FontFileResourceEntry paramAnonymousFontFileResourceEntry)
      {
        return paramAnonymousFontFileResourceEntry.isItalic();
      }
    });
  }
  
  private static <T> T findBestFont(T[] paramArrayOfT, int paramInt, StyleExtractor<T> paramStyleExtractor)
  {
    int i;
    int i1;
    label19:
    Object localObject;
    int j;
    label32:
    T ?;
    int m;
    if ((paramInt & 0x1) == 0)
    {
      i = 400;
      if ((paramInt & 0x2) == 0) {
        break label125;
      }
      i1 = 1;
      localObject = null;
      j = Integer.MAX_VALUE;
      int n = paramArrayOfT.length;
      paramInt = 0;
      if (paramInt >= n) {
        break label137;
      }
      ? = paramArrayOfT[paramInt];
      m = Math.abs(paramStyleExtractor.getWeight(?) - i);
      if (paramStyleExtractor.isItalic(?) != i1) {
        break label131;
      }
    }
    label125:
    label131:
    for (int k = 0;; k = 1)
    {
      m = m * 2 + k;
      if (localObject != null)
      {
        k = j;
        if (j <= m) {}
      }
      else
      {
        localObject = ?;
        k = m;
      }
      paramInt += 1;
      j = k;
      break label32;
      i = 700;
      break;
      i1 = 0;
      break label19;
    }
    label137:
    return (T)localObject;
  }
  
  @Nullable
  public Typeface createFromFontFamilyFilesResourceEntry(Context paramContext, FontResourcesParserCompat.FontFamilyFilesResourceEntry paramFontFamilyFilesResourceEntry, Resources paramResources, int paramInt)
  {
    paramFontFamilyFilesResourceEntry = findBestEntry(paramFontFamilyFilesResourceEntry, paramInt);
    if (paramFontFamilyFilesResourceEntry == null) {
      return null;
    }
    return TypefaceCompat.createFromResourcesFontFile(paramContext, paramResources, paramFontFamilyFilesResourceEntry.getResourceId(), paramFontFamilyFilesResourceEntry.getFileName(), paramInt);
  }
  
  public Typeface createFromFontInfo(Context paramContext, @Nullable CancellationSignal paramCancellationSignal, @NonNull FontsContractCompat.FontInfo[] paramArrayOfFontInfo, int paramInt)
  {
    if (paramArrayOfFontInfo.length < 1) {
      return null;
    }
    Object localObject = findBestInfo(paramArrayOfFontInfo, paramInt);
    paramArrayOfFontInfo = null;
    paramCancellationSignal = null;
    try
    {
      localObject = paramContext.getContentResolver().openInputStream(((FontsContractCompat.FontInfo)localObject).getUri());
      paramCancellationSignal = (CancellationSignal)localObject;
      paramArrayOfFontInfo = (FontsContractCompat.FontInfo[])localObject;
      paramContext = createFromInputStream(paramContext, (InputStream)localObject);
      TypefaceCompatUtil.closeQuietly((Closeable)localObject);
      return paramContext;
    }
    catch (IOException paramContext)
    {
      return null;
    }
    finally
    {
      TypefaceCompatUtil.closeQuietly(paramArrayOfFontInfo);
    }
  }
  
  protected Typeface createFromInputStream(Context paramContext, InputStream paramInputStream)
  {
    paramContext = TypefaceCompatUtil.getTempFile(paramContext);
    if (paramContext == null) {
      return null;
    }
    try
    {
      boolean bool = TypefaceCompatUtil.copyToFile(paramContext, paramInputStream);
      if (!bool) {
        return null;
      }
      paramInputStream = Typeface.createFromFile(paramContext.getPath());
      return paramInputStream;
    }
    catch (RuntimeException paramInputStream)
    {
      return null;
    }
    finally
    {
      paramContext.delete();
    }
  }
  
  @Nullable
  public Typeface createFromResourcesFontFile(Context paramContext, Resources paramResources, int paramInt1, String paramString, int paramInt2)
  {
    paramContext = TypefaceCompatUtil.getTempFile(paramContext);
    if (paramContext == null) {
      return null;
    }
    try
    {
      boolean bool = TypefaceCompatUtil.copyToFile(paramContext, paramResources, paramInt1);
      if (!bool) {
        return null;
      }
      paramResources = Typeface.createFromFile(paramContext.getPath());
      return paramResources;
    }
    catch (RuntimeException paramResources)
    {
      return null;
    }
    finally
    {
      paramContext.delete();
    }
  }
  
  protected FontsContractCompat.FontInfo findBestInfo(FontsContractCompat.FontInfo[] paramArrayOfFontInfo, int paramInt)
  {
    (FontsContractCompat.FontInfo)findBestFont(paramArrayOfFontInfo, paramInt, new StyleExtractor()
    {
      public int getWeight(FontsContractCompat.FontInfo paramAnonymousFontInfo)
      {
        return paramAnonymousFontInfo.getWeight();
      }
      
      public boolean isItalic(FontsContractCompat.FontInfo paramAnonymousFontInfo)
      {
        return paramAnonymousFontInfo.isItalic();
      }
    });
  }
  
  private static abstract interface StyleExtractor<T>
  {
    public abstract int getWeight(T paramT);
    
    public abstract boolean isItalic(T paramT);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v4/graphics/TypefaceCompatBaseImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */