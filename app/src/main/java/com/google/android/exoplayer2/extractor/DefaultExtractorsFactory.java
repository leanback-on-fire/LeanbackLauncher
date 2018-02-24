package com.google.android.exoplayer2.extractor;

import java.lang.reflect.Constructor;
import java.util.List;

public final class DefaultExtractorsFactory
  implements ExtractorsFactory
{
  private static List<Class<? extends Extractor>> defaultExtractorClasses;
  
  /* Error */
  public DefaultExtractorsFactory()
  {
    // Byte code:
    //   0: aload_0
    //   1: invokespecial 15	java/lang/Object:<init>	()V
    //   4: ldc 2
    //   6: monitorenter
    //   7: getstatic 17	com/google/android/exoplayer2/extractor/DefaultExtractorsFactory:defaultExtractorClasses	Ljava/util/List;
    //   10: ifnonnull +219 -> 229
    //   13: new 19	java/util/ArrayList
    //   16: dup
    //   17: invokespecial 20	java/util/ArrayList:<init>	()V
    //   20: astore_1
    //   21: aload_1
    //   22: ldc 22
    //   24: invokestatic 28	java/lang/Class:forName	(Ljava/lang/String;)Ljava/lang/Class;
    //   27: ldc 30
    //   29: invokevirtual 34	java/lang/Class:asSubclass	(Ljava/lang/Class;)Ljava/lang/Class;
    //   32: invokeinterface 40 2 0
    //   37: pop
    //   38: aload_1
    //   39: ldc 42
    //   41: invokestatic 28	java/lang/Class:forName	(Ljava/lang/String;)Ljava/lang/Class;
    //   44: ldc 30
    //   46: invokevirtual 34	java/lang/Class:asSubclass	(Ljava/lang/Class;)Ljava/lang/Class;
    //   49: invokeinterface 40 2 0
    //   54: pop
    //   55: aload_1
    //   56: ldc 44
    //   58: invokestatic 28	java/lang/Class:forName	(Ljava/lang/String;)Ljava/lang/Class;
    //   61: ldc 30
    //   63: invokevirtual 34	java/lang/Class:asSubclass	(Ljava/lang/Class;)Ljava/lang/Class;
    //   66: invokeinterface 40 2 0
    //   71: pop
    //   72: aload_1
    //   73: ldc 46
    //   75: invokestatic 28	java/lang/Class:forName	(Ljava/lang/String;)Ljava/lang/Class;
    //   78: ldc 30
    //   80: invokevirtual 34	java/lang/Class:asSubclass	(Ljava/lang/Class;)Ljava/lang/Class;
    //   83: invokeinterface 40 2 0
    //   88: pop
    //   89: aload_1
    //   90: ldc 48
    //   92: invokestatic 28	java/lang/Class:forName	(Ljava/lang/String;)Ljava/lang/Class;
    //   95: ldc 30
    //   97: invokevirtual 34	java/lang/Class:asSubclass	(Ljava/lang/Class;)Ljava/lang/Class;
    //   100: invokeinterface 40 2 0
    //   105: pop
    //   106: aload_1
    //   107: ldc 50
    //   109: invokestatic 28	java/lang/Class:forName	(Ljava/lang/String;)Ljava/lang/Class;
    //   112: ldc 30
    //   114: invokevirtual 34	java/lang/Class:asSubclass	(Ljava/lang/Class;)Ljava/lang/Class;
    //   117: invokeinterface 40 2 0
    //   122: pop
    //   123: aload_1
    //   124: ldc 52
    //   126: invokestatic 28	java/lang/Class:forName	(Ljava/lang/String;)Ljava/lang/Class;
    //   129: ldc 30
    //   131: invokevirtual 34	java/lang/Class:asSubclass	(Ljava/lang/Class;)Ljava/lang/Class;
    //   134: invokeinterface 40 2 0
    //   139: pop
    //   140: aload_1
    //   141: ldc 54
    //   143: invokestatic 28	java/lang/Class:forName	(Ljava/lang/String;)Ljava/lang/Class;
    //   146: ldc 30
    //   148: invokevirtual 34	java/lang/Class:asSubclass	(Ljava/lang/Class;)Ljava/lang/Class;
    //   151: invokeinterface 40 2 0
    //   156: pop
    //   157: aload_1
    //   158: ldc 56
    //   160: invokestatic 28	java/lang/Class:forName	(Ljava/lang/String;)Ljava/lang/Class;
    //   163: ldc 30
    //   165: invokevirtual 34	java/lang/Class:asSubclass	(Ljava/lang/Class;)Ljava/lang/Class;
    //   168: invokeinterface 40 2 0
    //   173: pop
    //   174: aload_1
    //   175: ldc 58
    //   177: invokestatic 28	java/lang/Class:forName	(Ljava/lang/String;)Ljava/lang/Class;
    //   180: ldc 30
    //   182: invokevirtual 34	java/lang/Class:asSubclass	(Ljava/lang/Class;)Ljava/lang/Class;
    //   185: invokeinterface 40 2 0
    //   190: pop
    //   191: aload_1
    //   192: ldc 60
    //   194: invokestatic 28	java/lang/Class:forName	(Ljava/lang/String;)Ljava/lang/Class;
    //   197: ldc 30
    //   199: invokevirtual 34	java/lang/Class:asSubclass	(Ljava/lang/Class;)Ljava/lang/Class;
    //   202: invokeinterface 40 2 0
    //   207: pop
    //   208: aload_1
    //   209: ldc 62
    //   211: invokestatic 28	java/lang/Class:forName	(Ljava/lang/String;)Ljava/lang/Class;
    //   214: ldc 30
    //   216: invokevirtual 34	java/lang/Class:asSubclass	(Ljava/lang/Class;)Ljava/lang/Class;
    //   219: invokeinterface 40 2 0
    //   224: pop
    //   225: aload_1
    //   226: putstatic 17	com/google/android/exoplayer2/extractor/DefaultExtractorsFactory:defaultExtractorClasses	Ljava/util/List;
    //   229: ldc 2
    //   231: monitorexit
    //   232: return
    //   233: astore_1
    //   234: ldc 2
    //   236: monitorexit
    //   237: aload_1
    //   238: athrow
    //   239: astore_2
    //   240: goto -15 -> 225
    //   243: astore_2
    //   244: goto -36 -> 208
    //   247: astore_2
    //   248: goto -57 -> 191
    //   251: astore_2
    //   252: goto -78 -> 174
    //   255: astore_2
    //   256: goto -99 -> 157
    //   259: astore_2
    //   260: goto -120 -> 140
    //   263: astore_2
    //   264: goto -141 -> 123
    //   267: astore_2
    //   268: goto -162 -> 106
    //   271: astore_2
    //   272: goto -183 -> 89
    //   275: astore_2
    //   276: goto -204 -> 72
    //   279: astore_2
    //   280: goto -225 -> 55
    //   283: astore_2
    //   284: goto -246 -> 38
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	287	0	this	DefaultExtractorsFactory
    //   20	206	1	localArrayList	java.util.ArrayList
    //   233	5	1	localObject	Object
    //   239	1	2	localClassNotFoundException1	ClassNotFoundException
    //   243	1	2	localClassNotFoundException2	ClassNotFoundException
    //   247	1	2	localClassNotFoundException3	ClassNotFoundException
    //   251	1	2	localClassNotFoundException4	ClassNotFoundException
    //   255	1	2	localClassNotFoundException5	ClassNotFoundException
    //   259	1	2	localClassNotFoundException6	ClassNotFoundException
    //   263	1	2	localClassNotFoundException7	ClassNotFoundException
    //   267	1	2	localClassNotFoundException8	ClassNotFoundException
    //   271	1	2	localClassNotFoundException9	ClassNotFoundException
    //   275	1	2	localClassNotFoundException10	ClassNotFoundException
    //   279	1	2	localClassNotFoundException11	ClassNotFoundException
    //   283	1	2	localClassNotFoundException12	ClassNotFoundException
    // Exception table:
    //   from	to	target	type
    //   7	21	233	finally
    //   21	38	233	finally
    //   38	55	233	finally
    //   55	72	233	finally
    //   72	89	233	finally
    //   89	106	233	finally
    //   106	123	233	finally
    //   123	140	233	finally
    //   140	157	233	finally
    //   157	174	233	finally
    //   174	191	233	finally
    //   191	208	233	finally
    //   208	225	233	finally
    //   225	229	233	finally
    //   229	232	233	finally
    //   234	237	233	finally
    //   208	225	239	java/lang/ClassNotFoundException
    //   191	208	243	java/lang/ClassNotFoundException
    //   174	191	247	java/lang/ClassNotFoundException
    //   157	174	251	java/lang/ClassNotFoundException
    //   140	157	255	java/lang/ClassNotFoundException
    //   123	140	259	java/lang/ClassNotFoundException
    //   106	123	263	java/lang/ClassNotFoundException
    //   89	106	267	java/lang/ClassNotFoundException
    //   72	89	271	java/lang/ClassNotFoundException
    //   55	72	275	java/lang/ClassNotFoundException
    //   38	55	279	java/lang/ClassNotFoundException
    //   21	38	283	java/lang/ClassNotFoundException
  }
  
  public Extractor[] createExtractors()
  {
    Extractor[] arrayOfExtractor = new Extractor[defaultExtractorClasses.size()];
    int i = 0;
    while (i < arrayOfExtractor.length) {
      try
      {
        arrayOfExtractor[i] = ((Extractor)((Class)defaultExtractorClasses.get(i)).getConstructor(new Class[0]).newInstance(new Object[0]));
        i += 1;
      }
      catch (Exception localException)
      {
        throw new IllegalStateException("Unexpected error creating default extractor", localException);
      }
    }
    return localException;
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/extractor/DefaultExtractorsFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */