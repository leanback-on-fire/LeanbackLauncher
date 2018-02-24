package android.support.v7.preference;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Xml;
import android.view.InflateException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

class PreferenceInflater
{
  private static final HashMap<String, Constructor> CONSTRUCTOR_MAP = new HashMap();
  private static final Class<?>[] CONSTRUCTOR_SIGNATURE = { Context.class, AttributeSet.class };
  private static final String EXTRA_TAG_NAME = "extra";
  private static final String INTENT_TAG_NAME = "intent";
  private static final String TAG = "PreferenceInflater";
  private final Object[] mConstructorArgs = new Object[2];
  private final Context mContext;
  private String[] mDefaultPackages;
  private PreferenceManager mPreferenceManager;
  
  public PreferenceInflater(Context paramContext, PreferenceManager paramPreferenceManager)
  {
    this.mContext = paramContext;
    init(paramPreferenceManager);
  }
  
  private Preference createItem(@NonNull String paramString, @Nullable String[] paramArrayOfString, AttributeSet paramAttributeSet)
    throws ClassNotFoundException, InflateException
  {
    localConstructor = (Constructor)CONSTRUCTOR_MAP.get(paramString);
    localObject1 = localConstructor;
    if (localConstructor == null) {}
    try
    {
      localClassLoader = this.mContext.getClassLoader();
      localObject2 = null;
      if ((paramArrayOfString != null) && (paramArrayOfString.length != 0)) {
        break label93;
      }
      paramArrayOfString = localClassLoader.loadClass(paramString);
    }
    catch (ClassNotFoundException paramString)
    {
      for (;;)
      {
        try
        {
          ClassLoader localClassLoader;
          Object localObject2;
          int j;
          localObject1 = localClassLoader.loadClass((String)localObject1 + paramString);
          paramArrayOfString = (String[])localObject1;
          if (localObject1 != null) {
            continue;
          }
          if (localConstructor != null) {
            break;
          }
          throw new InflateException(paramAttributeSet.getPositionDescription() + ": Error inflating class " + paramString);
        }
        catch (ClassNotFoundException localClassNotFoundException)
        {
          int i;
          i += 1;
        }
        paramString = paramString;
        throw paramString;
      }
      throw localClassNotFoundException;
    }
    catch (Exception paramArrayOfString)
    {
      paramString = new InflateException(paramAttributeSet.getPositionDescription() + ": Error inflating class " + paramString);
      paramString.initCause(paramArrayOfString);
      throw paramString;
    }
    localObject1 = paramArrayOfString.getConstructor(CONSTRUCTOR_SIGNATURE);
    ((Constructor)localObject1).setAccessible(true);
    CONSTRUCTOR_MAP.put(paramString, localObject1);
    paramArrayOfString = this.mConstructorArgs;
    paramArrayOfString[1] = paramAttributeSet;
    return (Preference)((Constructor)localObject1).newInstance(paramArrayOfString);
    label93:
    localConstructor = null;
    j = paramArrayOfString.length;
    i = 0;
    localObject1 = localObject2;
    if (i < j) {
      localObject1 = paramArrayOfString[i];
    }
  }
  
  private Preference createItemFromTag(String paramString, AttributeSet paramAttributeSet)
  {
    try
    {
      if (-1 == paramString.indexOf('.')) {
        return onCreateItem(paramString, paramAttributeSet);
      }
      Preference localPreference = createItem(paramString, null, paramAttributeSet);
      return localPreference;
    }
    catch (InflateException paramString)
    {
      throw paramString;
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      paramString = new InflateException(paramAttributeSet.getPositionDescription() + ": Error inflating class (not found)" + paramString);
      paramString.initCause(localClassNotFoundException);
      throw paramString;
    }
    catch (Exception localException)
    {
      paramString = new InflateException(paramAttributeSet.getPositionDescription() + ": Error inflating class " + paramString);
      paramString.initCause(localException);
      throw paramString;
    }
  }
  
  private void init(PreferenceManager paramPreferenceManager)
  {
    this.mPreferenceManager = paramPreferenceManager;
    setDefaultPackages(new String[] { "android.support.v14.preference.", "android.support.v7.preference." });
  }
  
  @NonNull
  private PreferenceGroup onMergeRoots(PreferenceGroup paramPreferenceGroup1, @NonNull PreferenceGroup paramPreferenceGroup2)
  {
    if (paramPreferenceGroup1 == null)
    {
      paramPreferenceGroup2.onAttachedToHierarchy(this.mPreferenceManager);
      return paramPreferenceGroup2;
    }
    return paramPreferenceGroup1;
  }
  
  private void rInflate(XmlPullParser paramXmlPullParser, Preference paramPreference, AttributeSet paramAttributeSet)
    throws XmlPullParserException, IOException
  {
    int i = paramXmlPullParser.getDepth();
    for (;;)
    {
      int j = paramXmlPullParser.next();
      if (((j == 3) && (paramXmlPullParser.getDepth() <= i)) || (j == 1)) {
        break;
      }
      if (j == 2)
      {
        Object localObject = paramXmlPullParser.getName();
        if ("intent".equals(localObject))
        {
          try
          {
            localObject = Intent.parseIntent(getContext().getResources(), paramXmlPullParser, paramAttributeSet);
            paramPreference.setIntent((Intent)localObject);
          }
          catch (IOException paramXmlPullParser)
          {
            paramPreference = new XmlPullParserException("Error parsing preference");
            paramPreference.initCause(paramXmlPullParser);
            throw paramPreference;
          }
        }
        else if ("extra".equals(localObject))
        {
          getContext().getResources().parseBundleExtra("extra", paramAttributeSet, paramPreference.getExtras());
          try
          {
            skipCurrentTag(paramXmlPullParser);
          }
          catch (IOException paramXmlPullParser)
          {
            paramPreference = new XmlPullParserException("Error parsing preference");
            paramPreference.initCause(paramXmlPullParser);
            throw paramPreference;
          }
        }
        else
        {
          localObject = createItemFromTag((String)localObject, paramAttributeSet);
          ((PreferenceGroup)paramPreference).addItemFromInflater((Preference)localObject);
          rInflate(paramXmlPullParser, (Preference)localObject, paramAttributeSet);
        }
      }
    }
  }
  
  private static void skipCurrentTag(XmlPullParser paramXmlPullParser)
    throws XmlPullParserException, IOException
  {
    int i = paramXmlPullParser.getDepth();
    int j;
    do
    {
      j = paramXmlPullParser.next();
    } while ((j != 1) && ((j != 3) || (paramXmlPullParser.getDepth() > i)));
  }
  
  public Context getContext()
  {
    return this.mContext;
  }
  
  public String[] getDefaultPackages()
  {
    return this.mDefaultPackages;
  }
  
  public Preference inflate(int paramInt, @Nullable PreferenceGroup paramPreferenceGroup)
  {
    XmlResourceParser localXmlResourceParser = getContext().getResources().getXml(paramInt);
    try
    {
      paramPreferenceGroup = inflate(localXmlResourceParser, paramPreferenceGroup);
      return paramPreferenceGroup;
    }
    finally
    {
      localXmlResourceParser.close();
    }
  }
  
  public Preference inflate(XmlPullParser paramXmlPullParser, @Nullable PreferenceGroup paramPreferenceGroup)
  {
    AttributeSet localAttributeSet;
    synchronized (this.mConstructorArgs)
    {
      localAttributeSet = Xml.asAttributeSet(paramXmlPullParser);
      this.mConstructorArgs[0] = this.mContext;
    }
    try
    {
      int i;
      do
      {
        i = paramXmlPullParser.next();
      } while ((i != 2) && (i != 1));
      if (i != 2) {
        throw new InflateException(paramXmlPullParser.getPositionDescription() + ": No start tag found!");
      }
    }
    catch (InflateException paramXmlPullParser)
    {
      throw paramXmlPullParser;
      paramXmlPullParser = finally;
      throw paramXmlPullParser;
      paramPreferenceGroup = onMergeRoots(paramPreferenceGroup, (PreferenceGroup)createItemFromTag(paramXmlPullParser.getName(), localAttributeSet));
      rInflate(paramXmlPullParser, paramPreferenceGroup, localAttributeSet);
      return paramPreferenceGroup;
    }
    catch (XmlPullParserException paramXmlPullParser)
    {
      paramPreferenceGroup = new InflateException(paramXmlPullParser.getMessage());
      paramPreferenceGroup.initCause(paramXmlPullParser);
      throw paramPreferenceGroup;
    }
    catch (IOException paramPreferenceGroup)
    {
      paramXmlPullParser = new InflateException(paramXmlPullParser.getPositionDescription() + ": " + paramPreferenceGroup.getMessage());
      paramXmlPullParser.initCause(paramPreferenceGroup);
      throw paramXmlPullParser;
    }
  }
  
  protected Preference onCreateItem(String paramString, AttributeSet paramAttributeSet)
    throws ClassNotFoundException
  {
    return createItem(paramString, this.mDefaultPackages, paramAttributeSet);
  }
  
  public void setDefaultPackages(String[] paramArrayOfString)
  {
    this.mDefaultPackages = paramArrayOfString;
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v7/preference/PreferenceInflater.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */