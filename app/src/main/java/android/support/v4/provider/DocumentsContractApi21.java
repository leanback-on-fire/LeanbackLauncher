package android.support.v4.provider;

import android.content.Context;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.support.annotation.RequiresApi;

@RequiresApi(21)
class DocumentsContractApi21
{
  private static final String TAG = "DocumentFile";
  
  private static void closeQuietly(AutoCloseable paramAutoCloseable)
  {
    if (paramAutoCloseable != null) {}
    try
    {
      paramAutoCloseable.close();
      return;
    }
    catch (RuntimeException paramAutoCloseable)
    {
      throw paramAutoCloseable;
    }
    catch (Exception paramAutoCloseable) {}
  }
  
  public static Uri createDirectory(Context paramContext, Uri paramUri, String paramString)
  {
    return createFile(paramContext, paramUri, "vnd.android.document/directory", paramString);
  }
  
  public static Uri createFile(Context paramContext, Uri paramUri, String paramString1, String paramString2)
  {
    try
    {
      paramContext = DocumentsContract.createDocument(paramContext.getContentResolver(), paramUri, paramString1, paramString2);
      return paramContext;
    }
    catch (Exception paramContext) {}
    return null;
  }
  
  /* Error */
  public static Uri[] listFiles(Context paramContext, Uri paramUri)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 41	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   4: astore_3
    //   5: aload_1
    //   6: aload_1
    //   7: invokestatic 53	android/provider/DocumentsContract:getDocumentId	(Landroid/net/Uri;)Ljava/lang/String;
    //   10: invokestatic 57	android/provider/DocumentsContract:buildChildDocumentsUriUsingTree	(Landroid/net/Uri;Ljava/lang/String;)Landroid/net/Uri;
    //   13: astore 5
    //   15: new 59	java/util/ArrayList
    //   18: dup
    //   19: invokespecial 60	java/util/ArrayList:<init>	()V
    //   22: astore 4
    //   24: aconst_null
    //   25: astore_2
    //   26: aconst_null
    //   27: astore_0
    //   28: aload_3
    //   29: aload 5
    //   31: iconst_1
    //   32: anewarray 62	java/lang/String
    //   35: dup
    //   36: iconst_0
    //   37: ldc 64
    //   39: aastore
    //   40: aconst_null
    //   41: aconst_null
    //   42: aconst_null
    //   43: invokevirtual 70	android/content/ContentResolver:query	(Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   46: astore_3
    //   47: aload_3
    //   48: astore_0
    //   49: aload_3
    //   50: astore_2
    //   51: aload_3
    //   52: invokeinterface 76 1 0
    //   57: ifeq +76 -> 133
    //   60: aload_3
    //   61: astore_0
    //   62: aload_3
    //   63: astore_2
    //   64: aload 4
    //   66: aload_1
    //   67: aload_3
    //   68: iconst_0
    //   69: invokeinterface 80 2 0
    //   74: invokestatic 83	android/provider/DocumentsContract:buildDocumentUriUsingTree	(Landroid/net/Uri;Ljava/lang/String;)Landroid/net/Uri;
    //   77: invokevirtual 87	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   80: pop
    //   81: goto -34 -> 47
    //   84: astore_1
    //   85: aload_0
    //   86: astore_2
    //   87: ldc 11
    //   89: new 89	java/lang/StringBuilder
    //   92: dup
    //   93: invokespecial 90	java/lang/StringBuilder:<init>	()V
    //   96: ldc 92
    //   98: invokevirtual 96	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   101: aload_1
    //   102: invokevirtual 99	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   105: invokevirtual 103	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   108: invokestatic 109	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   111: pop
    //   112: aload_0
    //   113: invokestatic 111	android/support/v4/provider/DocumentsContractApi21:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   116: aload 4
    //   118: aload 4
    //   120: invokevirtual 115	java/util/ArrayList:size	()I
    //   123: anewarray 117	android/net/Uri
    //   126: invokevirtual 121	java/util/ArrayList:toArray	([Ljava/lang/Object;)[Ljava/lang/Object;
    //   129: checkcast 123	[Landroid/net/Uri;
    //   132: areturn
    //   133: aload_3
    //   134: invokestatic 111	android/support/v4/provider/DocumentsContractApi21:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   137: goto -21 -> 116
    //   140: astore_0
    //   141: aload_2
    //   142: invokestatic 111	android/support/v4/provider/DocumentsContractApi21:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   145: aload_0
    //   146: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	147	0	paramContext	Context
    //   0	147	1	paramUri	Uri
    //   25	117	2	localObject1	Object
    //   4	130	3	localObject2	Object
    //   22	97	4	localArrayList	java.util.ArrayList
    //   13	17	5	localUri	Uri
    // Exception table:
    //   from	to	target	type
    //   28	47	84	java/lang/Exception
    //   51	60	84	java/lang/Exception
    //   64	81	84	java/lang/Exception
    //   28	47	140	finally
    //   51	60	140	finally
    //   64	81	140	finally
    //   87	112	140	finally
  }
  
  public static Uri prepareTreeUri(Uri paramUri)
  {
    return DocumentsContract.buildDocumentUriUsingTree(paramUri, DocumentsContract.getTreeDocumentId(paramUri));
  }
  
  public static Uri renameTo(Context paramContext, Uri paramUri, String paramString)
  {
    try
    {
      paramContext = DocumentsContract.renameDocument(paramContext.getContentResolver(), paramUri, paramString);
      return paramContext;
    }
    catch (Exception paramContext) {}
    return null;
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v4/provider/DocumentsContractApi21.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */