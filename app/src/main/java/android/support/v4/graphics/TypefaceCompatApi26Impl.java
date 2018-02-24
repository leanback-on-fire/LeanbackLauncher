package android.support.v4.graphics;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.fonts.FontVariationAxis;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;
import android.support.v4.content.res.FontResourcesParserCompat.FontFamilyFilesResourceEntry;
import android.support.v4.content.res.FontResourcesParserCompat.FontFileResourceEntry;
import android.util.Log;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;

@RequiresApi(26)
@RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
public class TypefaceCompatApi26Impl
  extends TypefaceCompatApi21Impl
{
  private static final String ABORT_CREATION_METHOD = "abortCreation";
  private static final String ADD_FONT_FROM_ASSET_MANAGER_METHOD = "addFontFromAssetManager";
  private static final String ADD_FONT_FROM_BUFFER_METHOD = "addFontFromBuffer";
  private static final String CREATE_FROM_FAMILIES_WITH_DEFAULT_METHOD = "createFromFamiliesWithDefault";
  private static final String FONT_FAMILY_CLASS = "android.graphics.FontFamily";
  private static final String FREEZE_METHOD = "freeze";
  private static final int RESOLVE_BY_FONT_TABLE = -1;
  private static final String TAG = "TypefaceCompatApi26Impl";
  private static final Method sAbortCreation;
  private static final Method sAddFontFromAssetManager;
  private static final Method sAddFontFromBuffer;
  private static final Method sCreateFromFamiliesWithDefault;
  private static final Class sFontFamily;
  private static final Constructor sFontFamilyCtor;
  private static final Method sFreeze;
  
  static
  {
    try
    {
      localClass = Class.forName("android.graphics.FontFamily");
      localConstructor = localClass.getConstructor(new Class[0]);
      localMethod2 = localClass.getMethod("addFontFromAssetManager", new Class[] { AssetManager.class, String.class, Integer.TYPE, Boolean.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, FontVariationAxis[].class });
      localMethod3 = localClass.getMethod("addFontFromBuffer", new Class[] { ByteBuffer.class, Integer.TYPE, FontVariationAxis[].class, Integer.TYPE, Integer.TYPE });
      localMethod5 = localClass.getMethod("freeze", new Class[0]);
      Method localMethod1 = localClass.getMethod("abortCreation", new Class[0]);
      localMethod4 = Typeface.class.getDeclaredMethod("createFromFamiliesWithDefault", new Class[] { Array.newInstance(localClass, 1).getClass(), Integer.TYPE, Integer.TYPE });
      localMethod4.setAccessible(true);
      sFontFamilyCtor = localConstructor;
      sFontFamily = localClass;
      sAddFontFromAssetManager = localMethod2;
      sAddFontFromBuffer = localMethod3;
      sFreeze = localMethod5;
      sAbortCreation = localMethod1;
      sCreateFromFamiliesWithDefault = localMethod4;
      return;
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      for (;;)
      {
        Log.e("TypefaceCompatApi26Impl", "Unable to collect necessary methods for class " + localClassNotFoundException.getClass().getName(), localClassNotFoundException);
        Class localClass = null;
        Constructor localConstructor = null;
        Method localMethod2 = null;
        Method localMethod3 = null;
        Method localMethod5 = null;
        Object localObject = null;
        Method localMethod4 = null;
      }
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      for (;;) {}
    }
  }
  
  private static boolean abortCreation(Object paramObject)
  {
    try
    {
      boolean bool = ((Boolean)sAbortCreation.invoke(paramObject, new Object[0])).booleanValue();
      return bool;
    }
    catch (IllegalAccessException paramObject)
    {
      throw new RuntimeException((Throwable)paramObject);
    }
    catch (InvocationTargetException paramObject)
    {
      for (;;) {}
    }
  }
  
  private static boolean addFontFromAssetManager(Context paramContext, Object paramObject, String paramString, int paramInt1, int paramInt2, int paramInt3)
  {
    try
    {
      boolean bool = ((Boolean)sAddFontFromAssetManager.invoke(paramObject, new Object[] { paramContext.getAssets(), paramString, Integer.valueOf(0), Boolean.valueOf(false), Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt3), null })).booleanValue();
      return bool;
    }
    catch (IllegalAccessException paramContext)
    {
      throw new RuntimeException(paramContext);
    }
    catch (InvocationTargetException paramContext)
    {
      for (;;) {}
    }
  }
  
  private static boolean addFontFromBuffer(Object paramObject, ByteBuffer paramByteBuffer, int paramInt1, int paramInt2, int paramInt3)
  {
    try
    {
      boolean bool = ((Boolean)sAddFontFromBuffer.invoke(paramObject, new Object[] { paramByteBuffer, Integer.valueOf(paramInt1), null, Integer.valueOf(paramInt2), Integer.valueOf(paramInt3) })).booleanValue();
      return bool;
    }
    catch (IllegalAccessException paramObject)
    {
      throw new RuntimeException((Throwable)paramObject);
    }
    catch (InvocationTargetException paramObject)
    {
      for (;;) {}
    }
  }
  
  private static Typeface createFromFamiliesWithDefault(Object paramObject)
  {
    try
    {
      Object localObject = Array.newInstance(sFontFamily, 1);
      Array.set(localObject, 0, paramObject);
      paramObject = (Typeface)sCreateFromFamiliesWithDefault.invoke(null, new Object[] { localObject, Integer.valueOf(-1), Integer.valueOf(-1) });
      return (Typeface)paramObject;
    }
    catch (IllegalAccessException paramObject)
    {
      throw new RuntimeException((Throwable)paramObject);
    }
    catch (InvocationTargetException paramObject)
    {
      for (;;) {}
    }
  }
  
  private static boolean freeze(Object paramObject)
  {
    try
    {
      boolean bool = ((Boolean)sFreeze.invoke(paramObject, new Object[0])).booleanValue();
      return bool;
    }
    catch (IllegalAccessException paramObject)
    {
      throw new RuntimeException((Throwable)paramObject);
    }
    catch (InvocationTargetException paramObject)
    {
      for (;;) {}
    }
  }
  
  private static boolean isFontFamilyPrivateAPIAvailable()
  {
    if (sAddFontFromAssetManager == null) {
      Log.w("TypefaceCompatApi26Impl", "Unable to collect necessary private methods.Fallback to legacy implementation.");
    }
    return sAddFontFromAssetManager != null;
  }
  
  private static Object newFamily()
  {
    try
    {
      Object localObject = sFontFamilyCtor.newInstance(new Object[0]);
      return localObject;
    }
    catch (InstantiationException localInstantiationException)
    {
      throw new RuntimeException(localInstantiationException);
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      for (;;) {}
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      for (;;) {}
    }
  }
  
  public Typeface createFromFontFamilyFilesResourceEntry(Context paramContext, FontResourcesParserCompat.FontFamilyFilesResourceEntry paramFontFamilyFilesResourceEntry, Resources paramResources, int paramInt)
  {
    if (!isFontFamilyPrivateAPIAvailable()) {
      return super.createFromFontFamilyFilesResourceEntry(paramContext, paramFontFamilyFilesResourceEntry, paramResources, paramInt);
    }
    paramResources = newFamily();
    paramFontFamilyFilesResourceEntry = paramFontFamilyFilesResourceEntry.getEntries();
    int j = paramFontFamilyFilesResourceEntry.length;
    paramInt = 0;
    while (paramInt < j)
    {
      Object localObject = paramFontFamilyFilesResourceEntry[paramInt];
      String str = ((FontResourcesParserCompat.FontFileResourceEntry)localObject).getFileName();
      int k = ((FontResourcesParserCompat.FontFileResourceEntry)localObject).getWeight();
      if (((FontResourcesParserCompat.FontFileResourceEntry)localObject).isItalic()) {}
      for (int i = 1; !addFontFromAssetManager(paramContext, paramResources, str, 0, k, i); i = 0)
      {
        abortCreation(paramResources);
        return null;
      }
      paramInt += 1;
    }
    if (!freeze(paramResources)) {
      return null;
    }
    return createFromFamiliesWithDefault(paramResources);
  }
  
  /* Error */
  public Typeface createFromFontInfo(Context paramContext, @Nullable android.os.CancellationSignal paramCancellationSignal, @android.support.annotation.NonNull android.support.v4.provider.FontsContractCompat.FontInfo[] paramArrayOfFontInfo, int paramInt)
  {
    // Byte code:
    //   0: aload_3
    //   1: arraylength
    //   2: iconst_1
    //   3: if_icmpge +7 -> 10
    //   6: aconst_null
    //   7: astore_2
    //   8: aload_2
    //   9: areturn
    //   10: invokestatic 202	android/support/v4/graphics/TypefaceCompatApi26Impl:isFontFamilyPrivateAPIAvailable	()Z
    //   13: ifne +131 -> 144
    //   16: aload_0
    //   17: aload_3
    //   18: iload 4
    //   20: invokevirtual 244	android/support/v4/graphics/TypefaceCompatApi26Impl:findBestInfo	([Landroid/support/v4/provider/FontsContractCompat$FontInfo;I)Landroid/support/v4/provider/FontsContractCompat$FontInfo;
    //   23: astore 9
    //   25: aload_1
    //   26: invokevirtual 248	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   29: astore_1
    //   30: aload_1
    //   31: aload 9
    //   33: invokevirtual 254	android/support/v4/provider/FontsContractCompat$FontInfo:getUri	()Landroid/net/Uri;
    //   36: ldc_w 256
    //   39: aload_2
    //   40: invokevirtual 262	android/content/ContentResolver:openFileDescriptor	(Landroid/net/Uri;Ljava/lang/String;Landroid/os/CancellationSignal;)Landroid/os/ParcelFileDescriptor;
    //   43: astore_3
    //   44: aconst_null
    //   45: astore_1
    //   46: new 264	android/graphics/Typeface$Builder
    //   49: dup
    //   50: aload_3
    //   51: invokevirtual 270	android/os/ParcelFileDescriptor:getFileDescriptor	()Ljava/io/FileDescriptor;
    //   54: invokespecial 273	android/graphics/Typeface$Builder:<init>	(Ljava/io/FileDescriptor;)V
    //   57: aload 9
    //   59: invokevirtual 274	android/support/v4/provider/FontsContractCompat$FontInfo:getWeight	()I
    //   62: invokevirtual 278	android/graphics/Typeface$Builder:setWeight	(I)Landroid/graphics/Typeface$Builder;
    //   65: aload 9
    //   67: invokevirtual 279	android/support/v4/provider/FontsContractCompat$FontInfo:isItalic	()Z
    //   70: invokevirtual 283	android/graphics/Typeface$Builder:setItalic	(Z)Landroid/graphics/Typeface$Builder;
    //   73: invokevirtual 287	android/graphics/Typeface$Builder:build	()Landroid/graphics/Typeface;
    //   76: astore_2
    //   77: aload_2
    //   78: astore_1
    //   79: aload_1
    //   80: astore_2
    //   81: aload_3
    //   82: ifnull -74 -> 8
    //   85: iconst_0
    //   86: ifeq +18 -> 104
    //   89: aload_3
    //   90: invokevirtual 290	android/os/ParcelFileDescriptor:close	()V
    //   93: aload_1
    //   94: areturn
    //   95: astore_1
    //   96: new 292	java/lang/NullPointerException
    //   99: dup
    //   100: invokespecial 293	java/lang/NullPointerException:<init>	()V
    //   103: athrow
    //   104: aload_3
    //   105: invokevirtual 290	android/os/ParcelFileDescriptor:close	()V
    //   108: aload_1
    //   109: areturn
    //   110: astore_1
    //   111: aload_1
    //   112: athrow
    //   113: astore_2
    //   114: aload_3
    //   115: ifnull +11 -> 126
    //   118: aload_1
    //   119: ifnull +18 -> 137
    //   122: aload_3
    //   123: invokevirtual 290	android/os/ParcelFileDescriptor:close	()V
    //   126: aload_2
    //   127: athrow
    //   128: astore_3
    //   129: aload_1
    //   130: aload_3
    //   131: invokevirtual 296	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   134: goto -8 -> 126
    //   137: aload_3
    //   138: invokevirtual 290	android/os/ParcelFileDescriptor:close	()V
    //   141: goto -15 -> 126
    //   144: aload_1
    //   145: aload_3
    //   146: aload_2
    //   147: invokestatic 302	android/support/v4/provider/FontsContractCompat:prepareFontData	(Landroid/content/Context;[Landroid/support/v4/provider/FontsContractCompat$FontInfo;Landroid/os/CancellationSignal;)Ljava/util/Map;
    //   150: astore 10
    //   152: invokestatic 206	android/support/v4/graphics/TypefaceCompatApi26Impl:newFamily	()Ljava/lang/Object;
    //   155: astore_1
    //   156: iconst_0
    //   157: istore 5
    //   159: aload_3
    //   160: arraylength
    //   161: istore 6
    //   163: iconst_0
    //   164: istore 4
    //   166: iload 4
    //   168: iload 6
    //   170: if_icmpge +96 -> 266
    //   173: aload_3
    //   174: iload 4
    //   176: aaload
    //   177: astore 9
    //   179: aload 10
    //   181: aload 9
    //   183: invokevirtual 254	android/support/v4/provider/FontsContractCompat$FontInfo:getUri	()Landroid/net/Uri;
    //   186: invokeinterface 308 2 0
    //   191: checkcast 81	java/nio/ByteBuffer
    //   194: astore_2
    //   195: aload_2
    //   196: ifnonnull +12 -> 208
    //   199: iload 4
    //   201: iconst_1
    //   202: iadd
    //   203: istore 4
    //   205: goto -39 -> 166
    //   208: aload 9
    //   210: invokevirtual 311	android/support/v4/provider/FontsContractCompat$FontInfo:getTtcIndex	()I
    //   213: istore 8
    //   215: aload 9
    //   217: invokevirtual 274	android/support/v4/provider/FontsContractCompat$FontInfo:getWeight	()I
    //   220: istore 7
    //   222: aload 9
    //   224: invokevirtual 279	android/support/v4/provider/FontsContractCompat$FontInfo:isItalic	()Z
    //   227: ifeq +27 -> 254
    //   230: iconst_1
    //   231: istore 5
    //   233: aload_1
    //   234: aload_2
    //   235: iload 8
    //   237: iload 7
    //   239: iload 5
    //   241: invokestatic 313	android/support/v4/graphics/TypefaceCompatApi26Impl:addFontFromBuffer	(Ljava/lang/Object;Ljava/nio/ByteBuffer;III)Z
    //   244: ifne +16 -> 260
    //   247: aload_1
    //   248: invokestatic 228	android/support/v4/graphics/TypefaceCompatApi26Impl:abortCreation	(Ljava/lang/Object;)Z
    //   251: pop
    //   252: aconst_null
    //   253: areturn
    //   254: iconst_0
    //   255: istore 5
    //   257: goto -24 -> 233
    //   260: iconst_1
    //   261: istore 5
    //   263: goto -64 -> 199
    //   266: iload 5
    //   268: ifne +10 -> 278
    //   271: aload_1
    //   272: invokestatic 228	android/support/v4/graphics/TypefaceCompatApi26Impl:abortCreation	(Ljava/lang/Object;)Z
    //   275: pop
    //   276: aconst_null
    //   277: areturn
    //   278: aload_1
    //   279: invokestatic 230	android/support/v4/graphics/TypefaceCompatApi26Impl:freeze	(Ljava/lang/Object;)Z
    //   282: ifne +5 -> 287
    //   285: aconst_null
    //   286: areturn
    //   287: aload_1
    //   288: invokestatic 232	android/support/v4/graphics/TypefaceCompatApi26Impl:createFromFamiliesWithDefault	(Ljava/lang/Object;)Landroid/graphics/Typeface;
    //   291: areturn
    //   292: astore_2
    //   293: goto -179 -> 114
    //   296: astore_1
    //   297: aconst_null
    //   298: areturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	299	0	this	TypefaceCompatApi26Impl
    //   0	299	1	paramContext	Context
    //   0	299	2	paramCancellationSignal	android.os.CancellationSignal
    //   0	299	3	paramArrayOfFontInfo	android.support.v4.provider.FontsContractCompat.FontInfo[]
    //   0	299	4	paramInt	int
    //   157	110	5	i	int
    //   161	10	6	j	int
    //   220	18	7	k	int
    //   213	23	8	m	int
    //   23	200	9	localFontInfo	android.support.v4.provider.FontsContractCompat.FontInfo
    //   150	30	10	localMap	java.util.Map
    // Exception table:
    //   from	to	target	type
    //   89	93	95	java/lang/Throwable
    //   46	77	110	java/lang/Throwable
    //   111	113	113	finally
    //   122	126	128	java/lang/Throwable
    //   46	77	292	finally
    //   30	44	296	java/io/IOException
    //   89	93	296	java/io/IOException
    //   96	104	296	java/io/IOException
    //   104	108	296	java/io/IOException
    //   122	126	296	java/io/IOException
    //   126	128	296	java/io/IOException
    //   129	134	296	java/io/IOException
    //   137	141	296	java/io/IOException
  }
  
  @Nullable
  public Typeface createFromResourcesFontFile(Context paramContext, Resources paramResources, int paramInt1, String paramString, int paramInt2)
  {
    if (!isFontFamilyPrivateAPIAvailable()) {
      super.createFromResourcesFontFile(paramContext, paramResources, paramInt1, paramString, paramInt2);
    }
    paramResources = newFamily();
    if (!addFontFromAssetManager(paramContext, paramResources, paramString, 0, -1, -1))
    {
      abortCreation(paramResources);
      return null;
    }
    if (!freeze(paramResources)) {
      return null;
    }
    return createFromFamiliesWithDefault(paramResources);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v4/graphics/TypefaceCompatApi26Impl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */