package com.bumptech.glide.module;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Log;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public final class ManifestParser
{
  private static final String GLIDE_MODULE_VALUE = "GlideModule";
  private static final String TAG = "ManifestParser";
  private final Context context;
  
  public ManifestParser(Context paramContext)
  {
    this.context = paramContext;
  }
  
  /* Error */
  private static GlideModule parseModule(String paramString)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokestatic 35	java/lang/Class:forName	(Ljava/lang/String;)Ljava/lang/Class;
    //   4: astore_1
    //   5: aload_1
    //   6: invokevirtual 39	java/lang/Class:newInstance	()Ljava/lang/Object;
    //   9: astore_0
    //   10: aload_0
    //   11: instanceof 41
    //   14: ifne +101 -> 115
    //   17: aload_0
    //   18: invokestatic 47	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   21: astore_0
    //   22: new 49	java/lang/RuntimeException
    //   25: dup
    //   26: new 51	java/lang/StringBuilder
    //   29: dup
    //   30: aload_0
    //   31: invokestatic 47	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   34: invokevirtual 55	java/lang/String:length	()I
    //   37: bipush 44
    //   39: iadd
    //   40: invokespecial 58	java/lang/StringBuilder:<init>	(I)V
    //   43: ldc 60
    //   45: invokevirtual 64	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   48: aload_0
    //   49: invokevirtual 64	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   52: invokevirtual 68	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   55: invokespecial 71	java/lang/RuntimeException:<init>	(Ljava/lang/String;)V
    //   58: athrow
    //   59: astore_0
    //   60: new 73	java/lang/IllegalArgumentException
    //   63: dup
    //   64: ldc 75
    //   66: aload_0
    //   67: invokespecial 78	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   70: athrow
    //   71: astore_0
    //   72: aload_1
    //   73: invokestatic 47	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   76: astore_1
    //   77: new 49	java/lang/RuntimeException
    //   80: dup
    //   81: new 51	java/lang/StringBuilder
    //   84: dup
    //   85: aload_1
    //   86: invokestatic 47	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   89: invokevirtual 55	java/lang/String:length	()I
    //   92: bipush 53
    //   94: iadd
    //   95: invokespecial 58	java/lang/StringBuilder:<init>	(I)V
    //   98: ldc 80
    //   100: invokevirtual 64	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   103: aload_1
    //   104: invokevirtual 64	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   107: invokevirtual 68	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   110: aload_0
    //   111: invokespecial 81	java/lang/RuntimeException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   114: athrow
    //   115: aload_0
    //   116: checkcast 41	com/bumptech/glide/module/GlideModule
    //   119: areturn
    //   120: astore_0
    //   121: goto -49 -> 72
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	124	0	paramString	String
    //   4	100	1	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   0	5	59	java/lang/ClassNotFoundException
    //   5	10	71	java/lang/InstantiationException
    //   5	10	120	java/lang/IllegalAccessException
  }
  
  public List<GlideModule> parse()
  {
    if (Log.isLoggable("ManifestParser", 3)) {
      Log.d("ManifestParser", "Loading Glide modules");
    }
    ArrayList localArrayList = new ArrayList();
    for (;;)
    {
      try
      {
        ApplicationInfo localApplicationInfo = this.context.getPackageManager().getApplicationInfo(this.context.getPackageName(), 128);
        if (localApplicationInfo.metaData == null) {
          return localArrayList;
        }
        Iterator localIterator = localApplicationInfo.metaData.keySet().iterator();
        if (!localIterator.hasNext()) {
          break;
        }
        String str1 = (String)localIterator.next();
        if ("GlideModule".equals(localApplicationInfo.metaData.get(str1)))
        {
          localArrayList.add(parseModule(str1));
          if (Log.isLoggable("ManifestParser", 3))
          {
            str1 = String.valueOf(str1);
            if (str1.length() != 0)
            {
              str1 = "Loaded Glide module: ".concat(str1);
              Log.d("ManifestParser", str1);
            }
            else
            {
              String str2 = new String("Loaded Glide module: ");
            }
          }
        }
      }
      catch (PackageManager.NameNotFoundException localNameNotFoundException)
      {
        throw new RuntimeException("Unable to find metadata to parse GlideModules", localNameNotFoundException);
      }
    }
    if (Log.isLoggable("ManifestParser", 3)) {
      Log.d("ManifestParser", "Finished loading Glide modules");
    }
    return localArrayList;
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/module/ManifestParser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */