package com.google.android.tvlauncher.home.contentrating;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.util.Log;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.xmlpull.v1.XmlPullParserException;

public class ContentRatingsParser
{
  private static final String ATTR_CONTENT_AGE_HINT = "contentAgeHint";
  private static final String ATTR_COUNTRY = "country";
  private static final String ATTR_DESCRIPTION = "description";
  private static final String ATTR_ICON = "icon";
  private static final String ATTR_NAME = "name";
  private static final String ATTR_TITLE = "title";
  private static final String ATTR_VERSION_CODE = "versionCode";
  private static final boolean DEBUG = false;
  public static final String DOMAIN_SYSTEM_RATINGS = "com.android.tv";
  private static final String LIVE_TV_APP_DOMAIN = "com.google.android.tv";
  private static final String TAG = "ContentRatingsParser";
  private static final String TAG_RATING = "rating";
  private static final String TAG_RATING_DEFINITION = "rating-definition";
  private static final String TAG_RATING_ORDER = "rating-order";
  private static final String TAG_RATING_SYSTEM_DEFINITION = "rating-system-definition";
  private static final String TAG_RATING_SYSTEM_DEFINITIONS = "rating-system-definitions";
  private static final String TAG_SUB_RATING = "sub-rating";
  private static final String TAG_SUB_RATING_DEFINITION = "sub-rating-definition";
  private static final String VERSION_CODE = "1";
  private final Context mContext;
  private Resources mResources;
  private String mXmlVersionCode;
  
  public ContentRatingsParser(Context paramContext)
  {
    this.mContext = paramContext;
  }
  
  private static void assertEquals(int paramInt1, int paramInt2, String paramString)
    throws XmlPullParserException
  {
    if (paramInt1 != paramInt2) {
      throw new XmlPullParserException(paramString);
    }
  }
  
  private static void assertEquals(String paramString1, String paramString2, String paramString3)
    throws XmlPullParserException
  {
    if (!paramString2.equals(paramString1)) {
      throw new XmlPullParserException(paramString3);
    }
  }
  
  private void checkVersion(String paramString)
    throws XmlPullParserException
  {
    if (!"1".equals(this.mXmlVersionCode)) {
      throw new XmlPullParserException(paramString);
    }
  }
  
  private String getTitle(XmlResourceParser paramXmlResourceParser, int paramInt)
  {
    int i = paramXmlResourceParser.getAttributeResourceValue(paramInt, 0);
    if (i != 0) {
      return this.mResources.getString(i);
    }
    return paramXmlResourceParser.getAttributeValue(paramInt);
  }
  
  private List<ContentRatingSystem> parse(XmlResourceParser paramXmlResourceParser, String paramString, boolean paramBoolean)
    throws XmlPullParserException, IOException
  {
    try
    {
      this.mResources = this.mContext.getPackageManager().getResourcesForApplication(paramString);
      String str;
      if (!paramString.equals(this.mContext.getPackageName()))
      {
        str = paramString;
        if (!paramString.equals("com.google.android.tv")) {}
      }
      else
      {
        str = "com.android.tv";
      }
      while (paramXmlResourceParser.next() == 0) {}
      assertEquals(paramXmlResourceParser.getEventType(), 2, "Malformed XML: Not a valid XML file");
      assertEquals(paramXmlResourceParser.getName(), "rating-system-definitions", "Malformed XML: Should start with tag rating-system-definitions");
      j = 0;
      int i = 0;
      while (i < paramXmlResourceParser.getAttributeCount())
      {
        if ("versionCode".equals(paramXmlResourceParser.getAttributeName(i)))
        {
          j = 1;
          this.mXmlVersionCode = paramXmlResourceParser.getAttributeValue(i);
        }
        i += 1;
      }
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      int j;
      for (;;)
      {
        Log.w("ContentRatingsParser", "Failed to get resources for " + paramString, localNameNotFoundException);
        this.mResources = this.mContext.getResources();
      }
      if (j == 0) {
        throw new XmlPullParserException("Malformed XML: Should contains a version attribute in rating-system-definitions");
      }
      paramString = new ArrayList();
      while (paramXmlResourceParser.next() != 1) {
        switch (paramXmlResourceParser.getEventType())
        {
        default: 
          break;
        case 2: 
          if ("rating-system-definition".equals(paramXmlResourceParser.getName())) {
            paramString.add(parseRatingSystemDefinition(paramXmlResourceParser, localNameNotFoundException, paramBoolean));
          } else {
            checkVersion("Malformed XML: Should contains rating-system-definition");
          }
          break;
        case 3: 
          if ("rating-system-definitions".equals(paramXmlResourceParser.getName()))
          {
            assertEquals(paramXmlResourceParser.next(), 1, "Malformed XML: Should end with tag rating-system-definitions");
            return paramString;
          }
          checkVersion("Malformed XML: Should end with tag rating-system-definitions");
        }
      }
      throw new XmlPullParserException("rating-system-definitions section is incomplete or section ending tag is missing");
    }
  }
  
  private ContentRatingSystem.Order.Builder parseOrder(XmlResourceParser paramXmlResourceParser)
    throws XmlPullParserException, IOException
  {
    ContentRatingSystem.Order.Builder localBuilder = new ContentRatingSystem.Order.Builder();
    assertEquals(paramXmlResourceParser.getAttributeCount(), 0, "Malformed XML: Attribute isn't allowed in rating-order");
    while (paramXmlResourceParser.next() != 1) {
      switch (paramXmlResourceParser.getEventType())
      {
      default: 
        break;
      case 2: 
        if ("rating".equals(paramXmlResourceParser.getName())) {
          localBuilder = parseRating(paramXmlResourceParser, localBuilder);
        } else {
          checkVersion("Malformed XML: Only rating is allowed in rating-order");
        }
        break;
      case 3: 
        assertEquals(paramXmlResourceParser.getName(), "rating-order", "Malformed XML: Tag mismatch for rating-order");
        return localBuilder;
      }
    }
    throw new XmlPullParserException("rating-order section is incomplete or section ending tag is missing");
  }
  
  private ContentRatingSystem.Order.Builder parseRating(XmlResourceParser paramXmlResourceParser, ContentRatingSystem.Order.Builder paramBuilder)
    throws XmlPullParserException, IOException
  {
    int i = 0;
    if (i < paramXmlResourceParser.getAttributeCount())
    {
      String str = paramXmlResourceParser.getAttributeName(i);
      int j = -1;
      switch (str.hashCode())
      {
      default: 
        switch (j)
        {
        default: 
          label48:
          checkVersion("Malformed XML: rating-order should only contain name");
        }
        break;
      }
      for (;;)
      {
        i += 1;
        break;
        if (!str.equals("name")) {
          break label48;
        }
        j = 0;
        break label48;
        paramBuilder.addRatingName(paramXmlResourceParser.getAttributeValue(i));
      }
    }
    do
    {
      checkVersion("Malformed XML: rating has child");
      do
      {
        if (paramXmlResourceParser.next() == 1) {
          break;
        }
      } while (paramXmlResourceParser.getEventType() != 3);
    } while (!"rating".equals(paramXmlResourceParser.getName()));
    return paramBuilder;
    throw new XmlPullParserException("rating section is incomplete or section ending tag is missing");
  }
  
  private ContentRatingSystem.Rating.Builder parseRatingDefinition(XmlResourceParser paramXmlResourceParser)
    throws XmlPullParserException, IOException
  {
    ContentRatingSystem.Rating.Builder localBuilder = new ContentRatingSystem.Rating.Builder();
    int j = 0;
    Object localObject = localBuilder;
    if (j < paramXmlResourceParser.getAttributeCount())
    {
      localObject = paramXmlResourceParser.getAttributeName(j);
      int i = -1;
      switch (((String)localObject).hashCode())
      {
      default: 
        switch (i)
        {
        default: 
          label92:
          checkVersion("Malformed XML: Unknown attribute " + (String)localObject + " in " + "rating-definition");
        }
        break;
      }
      for (;;)
      {
        j += 1;
        break;
        if (!((String)localObject).equals("name")) {
          break label92;
        }
        i = 0;
        break label92;
        if (!((String)localObject).equals("title")) {
          break label92;
        }
        i = 1;
        break label92;
        if (!((String)localObject).equals("description")) {
          break label92;
        }
        i = 2;
        break label92;
        if (!((String)localObject).equals("icon")) {
          break label92;
        }
        i = 3;
        break label92;
        if (!((String)localObject).equals("contentAgeHint")) {
          break label92;
        }
        i = 4;
        break label92;
        localBuilder.setName(paramXmlResourceParser.getAttributeValue(j));
        continue;
        localBuilder.setTitle(getTitle(paramXmlResourceParser, j));
        continue;
        localBuilder.setDescription(this.mResources.getString(paramXmlResourceParser.getAttributeResourceValue(j, 0)));
        continue;
        localBuilder.setIcon(this.mResources.getDrawable(paramXmlResourceParser.getAttributeResourceValue(j, 0), null));
        continue;
        i = -1;
        try
        {
          int k = Integer.parseInt(paramXmlResourceParser.getAttributeValue(j));
          i = k;
        }
        catch (NumberFormatException localNumberFormatException)
        {
          for (;;) {}
        }
        if (i < 0) {
          throw new XmlPullParserException("Malformed XML: contentAgeHint should be a non-negative number");
        }
        localBuilder.setContentAgeHint(i);
      }
    }
    while (paramXmlResourceParser.next() != 1) {
      switch (paramXmlResourceParser.getEventType())
      {
      default: 
        break;
      case 2: 
        if ("sub-rating".equals(paramXmlResourceParser.getName())) {
          localObject = parseSubRating(paramXmlResourceParser, (ContentRatingSystem.Rating.Builder)localObject);
        } else {
          checkVersion("Malformed XML: Only sub-rating is allowed in rating-definition");
        }
        break;
      case 3: 
        if ("rating-definition".equals(paramXmlResourceParser.getName())) {
          return (ContentRatingSystem.Rating.Builder)localObject;
        }
        checkVersion("Malformed XML: Tag mismatch for rating-definition");
      }
    }
    throw new XmlPullParserException("rating-definition section is incomplete or section ending tag is missing");
  }
  
  private ContentRatingSystem parseRatingSystemDefinition(XmlResourceParser paramXmlResourceParser, String paramString, boolean paramBoolean)
    throws XmlPullParserException, IOException
  {
    ContentRatingSystem.Builder localBuilder = new ContentRatingSystem.Builder(this.mContext);
    localBuilder.setDomain(paramString);
    int j = 0;
    label88:
    int i;
    if (j < paramXmlResourceParser.getAttributeCount())
    {
      paramString = paramXmlResourceParser.getAttributeName(j);
      switch (paramString.hashCode())
      {
      default: 
        i = -1;
        switch (i)
        {
        default: 
          label91:
          checkVersion("Malformed XML: Unknown attribute " + paramString + " in " + "rating-system-definition");
        }
        break;
      }
      for (;;)
      {
        j += 1;
        break;
        if (!paramString.equals("name")) {
          break label88;
        }
        i = 0;
        break label91;
        if (!paramString.equals("country")) {
          break label88;
        }
        i = 1;
        break label91;
        if (!paramString.equals("title")) {
          break label88;
        }
        i = 2;
        break label91;
        if (!paramString.equals("description")) {
          break label88;
        }
        i = 3;
        break label91;
        localBuilder.setName(paramXmlResourceParser.getAttributeValue(j));
        continue;
        paramString = paramXmlResourceParser.getAttributeValue(j).split("\\s*,\\s*");
        int k = paramString.length;
        i = 0;
        while (i < k)
        {
          localBuilder.addCountry(paramString[i]);
          i += 1;
        }
        localBuilder.setTitle(getTitle(paramXmlResourceParser, j));
        continue;
        localBuilder.setDescription(this.mResources.getString(paramXmlResourceParser.getAttributeResourceValue(j, 0)));
      }
    }
    label328:
    while (paramXmlResourceParser.next() != 1) {
      switch (paramXmlResourceParser.getEventType())
      {
      default: 
        break;
      case 2: 
        paramString = paramXmlResourceParser.getName();
        switch (paramString.hashCode())
        {
        default: 
          i = -1;
        }
        for (;;)
        {
          switch (i)
          {
          default: 
            checkVersion("Malformed XML: Unknown tag " + paramString + " in " + "rating-system-definition");
            break label328;
            if (!paramString.equals("rating-definition")) {
              break label416;
            }
            i = 0;
            continue;
            if (!paramString.equals("sub-rating-definition")) {
              break label416;
            }
            i = 1;
            continue;
            if (!paramString.equals("rating-order")) {
              break label416;
            }
            i = 2;
          }
        }
        localBuilder.addRatingBuilder(parseRatingDefinition(paramXmlResourceParser));
        continue;
        localBuilder.addSubRatingBuilder(parseSubRatingDefinition(paramXmlResourceParser));
        continue;
        localBuilder.addOrderBuilder(parseOrder(paramXmlResourceParser));
        break;
      case 3: 
        label416:
        if ("rating-system-definition".equals(paramXmlResourceParser.getName()))
        {
          localBuilder.setIsCustom(paramBoolean);
          return localBuilder.build();
        }
        checkVersion("Malformed XML: Tag mismatch for rating-system-definition");
      }
    }
    throw new XmlPullParserException("rating-system-definition section is incomplete or section ending tag is missing");
  }
  
  private ContentRatingSystem.Rating.Builder parseSubRating(XmlResourceParser paramXmlResourceParser, ContentRatingSystem.Rating.Builder paramBuilder)
    throws XmlPullParserException, IOException
  {
    int i = 0;
    if (i < paramXmlResourceParser.getAttributeCount())
    {
      String str = paramXmlResourceParser.getAttributeName(i);
      int j = -1;
      switch (str.hashCode())
      {
      default: 
        switch (j)
        {
        default: 
          label48:
          checkVersion("Malformed XML: sub-rating should only contain name");
        }
        break;
      }
      for (;;)
      {
        i += 1;
        break;
        if (!str.equals("name")) {
          break label48;
        }
        j = 0;
        break label48;
        paramBuilder.addSubRatingName(paramXmlResourceParser.getAttributeValue(i));
      }
    }
    do
    {
      checkVersion("Malformed XML: sub-rating has child");
      do
      {
        if (paramXmlResourceParser.next() == 1) {
          break;
        }
      } while (paramXmlResourceParser.getEventType() != 3);
    } while (!"sub-rating".equals(paramXmlResourceParser.getName()));
    return paramBuilder;
    throw new XmlPullParserException("sub-rating section is incomplete or section ending tag is missing");
  }
  
  private ContentRatingSystem.SubRating.Builder parseSubRatingDefinition(XmlResourceParser paramXmlResourceParser)
    throws XmlPullParserException, IOException
  {
    ContentRatingSystem.SubRating.Builder localBuilder = new ContentRatingSystem.SubRating.Builder();
    int j = 0;
    if (j < paramXmlResourceParser.getAttributeCount())
    {
      String str = paramXmlResourceParser.getAttributeName(j);
      int i = -1;
      switch (str.hashCode())
      {
      default: 
        switch (i)
        {
        default: 
          label80:
          checkVersion("Malformed XML: Unknown attribute " + str + " in " + "sub-rating-definition");
        }
        break;
      }
      for (;;)
      {
        j += 1;
        break;
        if (!str.equals("name")) {
          break label80;
        }
        i = 0;
        break label80;
        if (!str.equals("title")) {
          break label80;
        }
        i = 1;
        break label80;
        if (!str.equals("description")) {
          break label80;
        }
        i = 2;
        break label80;
        if (!str.equals("icon")) {
          break label80;
        }
        i = 3;
        break label80;
        localBuilder.setName(paramXmlResourceParser.getAttributeValue(j));
        continue;
        localBuilder.setTitle(getTitle(paramXmlResourceParser, j));
        continue;
        localBuilder.setDescription(this.mResources.getString(paramXmlResourceParser.getAttributeResourceValue(j, 0)));
        continue;
        localBuilder.setIcon(this.mResources.getDrawable(paramXmlResourceParser.getAttributeResourceValue(j, 0), null));
      }
    }
    do
    {
      checkVersion("Malformed XML: sub-rating-definition isn't closed");
      while (paramXmlResourceParser.next() != 1) {
        switch (paramXmlResourceParser.getEventType())
        {
        default: 
          checkVersion("Malformed XML: sub-rating-definition has child");
        }
      }
    } while (!"sub-rating-definition".equals(paramXmlResourceParser.getName()));
    return localBuilder;
    throw new XmlPullParserException("sub-rating-definition section is incomplete or section ending tag is missing");
  }
  
  /* Error */
  public List<ContentRatingSystem> parse(android.media.tv.TvContentRatingSystemInfo paramTvContentRatingSystemInfo)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 7
    //   3: aconst_null
    //   4: astore 6
    //   6: aload_1
    //   7: invokevirtual 371	android/media/tv/TvContentRatingSystemInfo:getXmlUri	()Landroid/net/Uri;
    //   10: astore 8
    //   12: aload 6
    //   14: astore 4
    //   16: aload 8
    //   18: invokevirtual 376	android/net/Uri:getAuthority	()Ljava/lang/String;
    //   21: astore 5
    //   23: aload 6
    //   25: astore 4
    //   27: aload 8
    //   29: invokestatic 382	android/content/ContentUris:parseId	(Landroid/net/Uri;)J
    //   32: l2i
    //   33: istore_2
    //   34: aload 6
    //   36: astore 4
    //   38: aload_0
    //   39: getfield 74	com/google/android/tvlauncher/home/contentrating/ContentRatingsParser:mContext	Landroid/content/Context;
    //   42: invokevirtual 124	android/content/Context:getPackageManager	()Landroid/content/pm/PackageManager;
    //   45: aload 5
    //   47: iload_2
    //   48: aconst_null
    //   49: invokevirtual 386	android/content/pm/PackageManager:getXml	(Ljava/lang/String;ILandroid/content/pm/ApplicationInfo;)Landroid/content/res/XmlResourceParser;
    //   52: astore 9
    //   54: aload 9
    //   56: ifnonnull +96 -> 152
    //   59: new 388	java/lang/IllegalArgumentException
    //   62: dup
    //   63: new 160	java/lang/StringBuilder
    //   66: dup
    //   67: invokespecial 161	java/lang/StringBuilder:<init>	()V
    //   70: ldc_w 390
    //   73: invokevirtual 167	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   76: aload 8
    //   78: invokevirtual 393	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   81: invokevirtual 170	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   84: invokespecial 394	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   87: athrow
    //   88: astore_1
    //   89: aload_1
    //   90: athrow
    //   91: astore 5
    //   93: aload 9
    //   95: ifnull +18 -> 113
    //   98: aload_1
    //   99: ifnull +136 -> 235
    //   102: aload 6
    //   104: astore 4
    //   106: aload 9
    //   108: invokeinterface 397 1 0
    //   113: aload 6
    //   115: astore 4
    //   117: aload 5
    //   119: athrow
    //   120: astore_1
    //   121: ldc 38
    //   123: new 160	java/lang/StringBuilder
    //   126: dup
    //   127: invokespecial 161	java/lang/StringBuilder:<init>	()V
    //   130: ldc_w 399
    //   133: invokevirtual 167	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   136: aload 8
    //   138: invokevirtual 393	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   141: invokevirtual 170	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   144: aload_1
    //   145: invokestatic 176	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   148: pop
    //   149: aload 4
    //   151: areturn
    //   152: aload_1
    //   153: invokevirtual 403	android/media/tv/TvContentRatingSystemInfo:isSystemDefined	()Z
    //   156: ifne +101 -> 257
    //   159: iconst_1
    //   160: istore_3
    //   161: aload_0
    //   162: aload 9
    //   164: aload 5
    //   166: iload_3
    //   167: invokespecial 405	com/google/android/tvlauncher/home/contentrating/ContentRatingsParser:parse	(Landroid/content/res/XmlResourceParser;Ljava/lang/String;Z)Ljava/util/List;
    //   170: astore_1
    //   171: aload_1
    //   172: astore 4
    //   174: aload 9
    //   176: ifnull -27 -> 149
    //   179: iconst_0
    //   180: ifeq +28 -> 208
    //   183: aload_1
    //   184: astore 4
    //   186: aload 9
    //   188: invokeinterface 397 1 0
    //   193: aload_1
    //   194: areturn
    //   195: astore 4
    //   197: aload_1
    //   198: astore 4
    //   200: new 407	java/lang/NullPointerException
    //   203: dup
    //   204: invokespecial 408	java/lang/NullPointerException:<init>	()V
    //   207: athrow
    //   208: aload_1
    //   209: astore 4
    //   211: aload 9
    //   213: invokeinterface 397 1 0
    //   218: aload_1
    //   219: areturn
    //   220: astore 7
    //   222: aload 6
    //   224: astore 4
    //   226: aload_1
    //   227: aload 7
    //   229: invokevirtual 412	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   232: goto -119 -> 113
    //   235: aload 6
    //   237: astore 4
    //   239: aload 9
    //   241: invokeinterface 397 1 0
    //   246: goto -133 -> 113
    //   249: astore 5
    //   251: aload 7
    //   253: astore_1
    //   254: goto -161 -> 93
    //   257: iconst_0
    //   258: istore_3
    //   259: goto -98 -> 161
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	262	0	this	ContentRatingsParser
    //   0	262	1	paramTvContentRatingSystemInfo	android.media.tv.TvContentRatingSystemInfo
    //   33	15	2	i	int
    //   160	99	3	bool	boolean
    //   14	171	4	localObject1	Object
    //   195	1	4	localThrowable1	Throwable
    //   198	40	4	localObject2	Object
    //   21	25	5	str1	String
    //   91	74	5	str2	String
    //   249	1	5	localObject3	Object
    //   4	232	6	localObject4	Object
    //   1	1	7	localObject5	Object
    //   220	32	7	localThrowable2	Throwable
    //   10	127	8	localUri	android.net.Uri
    //   52	188	9	localXmlResourceParser	XmlResourceParser
    // Exception table:
    //   from	to	target	type
    //   59	88	88	java/lang/Throwable
    //   152	159	88	java/lang/Throwable
    //   161	171	88	java/lang/Throwable
    //   89	91	91	finally
    //   16	23	120	java/lang/Exception
    //   27	34	120	java/lang/Exception
    //   38	54	120	java/lang/Exception
    //   106	113	120	java/lang/Exception
    //   117	120	120	java/lang/Exception
    //   186	193	120	java/lang/Exception
    //   200	208	120	java/lang/Exception
    //   211	218	120	java/lang/Exception
    //   226	232	120	java/lang/Exception
    //   239	246	120	java/lang/Exception
    //   186	193	195	java/lang/Throwable
    //   106	113	220	java/lang/Throwable
    //   59	88	249	finally
    //   152	159	249	finally
    //   161	171	249	finally
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/home/contentrating/ContentRatingsParser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */