package android.support.multidex;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.os.Build.VERSION;
import android.util.Log;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

final class MultiDexExtractor
{
  private static final int BUFFER_SIZE = 16384;
  private static final String DEX_PREFIX = "classes";
  private static final String DEX_SUFFIX = ".dex";
  private static final String EXTRACTED_NAME_EXT = ".classes";
  private static final String EXTRACTED_SUFFIX = ".zip";
  private static final String KEY_CRC = "crc";
  private static final String KEY_DEX_NUMBER = "dex.number";
  private static final String KEY_TIME_STAMP = "timestamp";
  private static final int MAX_EXTRACT_ATTEMPTS = 3;
  private static final long NO_VALUE = -1L;
  private static final String PREFS_FILE = "multidex.version";
  private static final String TAG = "MultiDex";
  private static Method sApplyMethod;
  
  static
  {
    try
    {
      sApplyMethod = SharedPreferences.Editor.class.getMethod("apply", new Class[0]);
      return;
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      sApplyMethod = null;
    }
  }
  
  private static void apply(SharedPreferences.Editor paramEditor)
  {
    if (sApplyMethod != null) {}
    try
    {
      sApplyMethod.invoke(paramEditor, new Object[0]);
      return;
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      paramEditor.commit();
      return;
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      for (;;) {}
    }
  }
  
  private static void closeQuietly(Closeable paramCloseable)
  {
    try
    {
      paramCloseable.close();
      return;
    }
    catch (IOException paramCloseable)
    {
      Log.w("MultiDex", "Failed to close resource", paramCloseable);
    }
  }
  
  private static void extract(ZipFile paramZipFile, ZipEntry paramZipEntry, File paramFile, String paramString)
    throws IOException, FileNotFoundException
  {
    InputStream localInputStream = paramZipFile.getInputStream(paramZipEntry);
    paramString = File.createTempFile(paramString, ".zip", paramFile.getParentFile());
    Log.i("MultiDex", "Extracting " + paramString.getPath());
    for (;;)
    {
      try
      {
        paramZipFile = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(paramString)));
      }
      finally
      {
        ZipEntry localZipEntry;
        int i;
        continue;
      }
      try
      {
        localZipEntry = new ZipEntry("classes.dex");
        localZipEntry.setTime(paramZipEntry.getTime());
        paramZipFile.putNextEntry(localZipEntry);
        paramZipEntry = new byte['䀀'];
        i = localInputStream.read(paramZipEntry);
        if (i != -1)
        {
          paramZipFile.write(paramZipEntry, 0, i);
          i = localInputStream.read(paramZipEntry);
        }
        else
        {
          paramZipFile.closeEntry();
          try
          {
            paramZipFile.close();
            Log.i("MultiDex", "Renaming to " + paramFile.getPath());
            if (paramString.renameTo(paramFile)) {
              continue;
            }
            throw new IOException("Failed to rename \"" + paramString.getAbsolutePath() + "\" to \"" + paramFile.getAbsolutePath() + "\"");
          }
          finally {}
          closeQuietly(localInputStream);
          paramString.delete();
          throw paramZipFile;
        }
      }
      finally
      {
        paramZipFile.close();
      }
    }
    closeQuietly(localInputStream);
    paramString.delete();
  }
  
  private static SharedPreferences getMultiDexPreferences(Context paramContext)
  {
    if (Build.VERSION.SDK_INT < 11) {}
    for (int i = 0;; i = 4) {
      return paramContext.getSharedPreferences("multidex.version", i);
    }
  }
  
  private static long getTimeStamp(File paramFile)
  {
    long l2 = paramFile.lastModified();
    long l1 = l2;
    if (l2 == -1L) {
      l1 = l2 - 1L;
    }
    return l1;
  }
  
  private static long getZipCrc(File paramFile)
    throws IOException
  {
    long l2 = ZipUtil.getZipCrc(paramFile);
    long l1 = l2;
    if (l2 == -1L) {
      l1 = l2 - 1L;
    }
    return l1;
  }
  
  private static boolean isModified(Context paramContext, File paramFile, long paramLong)
  {
    paramContext = getMultiDexPreferences(paramContext);
    return (paramContext.getLong("timestamp", -1L) != getTimeStamp(paramFile)) || (paramContext.getLong("crc", -1L) != paramLong);
  }
  
  static List<File> load(Context paramContext, ApplicationInfo paramApplicationInfo, File paramFile, boolean paramBoolean)
    throws IOException
  {
    Log.i("MultiDex", "MultiDexExtractor.load(" + paramApplicationInfo.sourceDir + ", " + paramBoolean + ")");
    File localFile = new File(paramApplicationInfo.sourceDir);
    long l = getZipCrc(localFile);
    if ((!paramBoolean) && (!isModified(paramContext, localFile, l))) {}
    for (;;)
    {
      try
      {
        paramApplicationInfo = loadExistingExtractions(paramContext, localFile, paramFile);
        paramContext = paramApplicationInfo;
      }
      catch (IOException paramApplicationInfo)
      {
        Log.w("MultiDex", "Failed to reload existing extracted secondary dex files, falling back to fresh extraction", paramApplicationInfo);
        paramApplicationInfo = performExtractions(localFile, paramFile);
        putStoredApkInfo(paramContext, getTimeStamp(localFile), l, paramApplicationInfo.size() + 1);
        paramContext = paramApplicationInfo;
        continue;
      }
      Log.i("MultiDex", "load found " + paramContext.size() + " secondary dex files");
      return paramContext;
      Log.i("MultiDex", "Detected that extraction must be performed.");
      paramApplicationInfo = performExtractions(localFile, paramFile);
      putStoredApkInfo(paramContext, getTimeStamp(localFile), l, paramApplicationInfo.size() + 1);
      paramContext = paramApplicationInfo;
    }
  }
  
  private static List<File> loadExistingExtractions(Context paramContext, File paramFile1, File paramFile2)
    throws IOException
  {
    Log.i("MultiDex", "loading existing secondary dex files");
    paramFile1 = paramFile1.getName() + ".classes";
    int j = getMultiDexPreferences(paramContext).getInt("dex.number", 1);
    paramContext = new ArrayList(j);
    int i = 2;
    while (i <= j)
    {
      File localFile = new File(paramFile2, paramFile1 + i + ".zip");
      if (localFile.isFile())
      {
        paramContext.add(localFile);
        if (!verifyZipFile(localFile))
        {
          Log.i("MultiDex", "Invalid zip file: " + localFile);
          throw new IOException("Invalid ZIP file.");
        }
      }
      else
      {
        throw new IOException("Missing extracted secondary dex file '" + localFile.getPath() + "'");
      }
      i += 1;
    }
    return paramContext;
  }
  
  private static void mkdirChecked(File paramFile)
    throws IOException
  {
    paramFile.mkdir();
    if (!paramFile.isDirectory())
    {
      File localFile = paramFile.getParentFile();
      if (localFile == null) {
        Log.e("MultiDex", "Failed to create dir " + paramFile.getPath() + ". Parent file is null.");
      }
      for (;;)
      {
        throw new IOException("Failed to create cache directory " + paramFile.getPath());
        Log.e("MultiDex", "Failed to create dir " + paramFile.getPath() + ". parent file is a dir " + localFile.isDirectory() + ", a file " + localFile.isFile() + ", exists " + localFile.exists() + ", readable " + localFile.canRead() + ", writable " + localFile.canWrite());
      }
    }
  }
  
  private static List<File> performExtractions(File paramFile1, File paramFile2)
    throws IOException
  {
    String str2 = paramFile1.getName() + ".classes";
    prepareDexDir(paramFile2, str2);
    localArrayList = new ArrayList();
    localZipFile = new ZipFile(paramFile1);
    i = 2;
    try
    {
      paramFile1 = localZipFile.getEntry("classes" + 2 + ".dex");
      for (;;)
      {
        File localFile;
        int m;
        String str1;
        if (paramFile1 != null)
        {
          localFile = new File(paramFile2, str2 + i + ".zip");
          localArrayList.add(localFile);
          Log.i("MultiDex", "Extraction is needed for file " + localFile);
          int j = 0;
          m = 0;
          for (;;)
          {
            if ((j >= 3) || (m != 0)) {
              break label357;
            }
            int k = j + 1;
            extract(localZipFile, paramFile1, localFile, str2);
            boolean bool = verifyZipFile(localFile);
            StringBuilder localStringBuilder = new StringBuilder().append("Extraction ");
            if (!bool) {
              break;
            }
            str1 = "success";
            label218:
            Log.i("MultiDex", str1 + " - length " + localFile.getAbsolutePath() + ": " + localFile.length());
            m = bool;
            j = k;
            if (!bool)
            {
              localFile.delete();
              m = bool;
              j = k;
              if (localFile.exists())
              {
                Log.w("MultiDex", "Failed to delete corrupted secondary dex '" + localFile.getPath() + "'");
                m = bool;
                j = k;
              }
            }
          }
        }
        try
        {
          localZipFile.close();
          throw paramFile1;
          str1 = "failed";
          break label218;
          if (m == 0) {
            throw new IOException("Could not create zip file " + localFile.getAbsolutePath() + " for secondary dex (" + i + ")");
          }
          i += 1;
          paramFile1 = localZipFile.getEntry("classes" + i + ".dex");
          continue;
          try
          {
            localZipFile.close();
            return localArrayList;
          }
          catch (IOException paramFile1)
          {
            Log.w("MultiDex", "Failed to close resource", paramFile1);
            return localArrayList;
          }
        }
        catch (IOException paramFile2)
        {
          for (;;)
          {
            Log.w("MultiDex", "Failed to close resource", paramFile2);
          }
        }
      }
    }
    finally {}
  }
  
  private static void prepareDexDir(File paramFile, String paramString)
    throws IOException
  {
    mkdirChecked(paramFile.getParentFile());
    mkdirChecked(paramFile);
    paramString = paramFile.listFiles(new FileFilter()
    {
      public boolean accept(File paramAnonymousFile)
      {
        return !paramAnonymousFile.getName().startsWith(this.val$extractedFilePrefix);
      }
    });
    if (paramString == null)
    {
      Log.w("MultiDex", "Failed to list secondary dex dir content (" + paramFile.getPath() + ").");
      return;
    }
    int j = paramString.length;
    int i = 0;
    label69:
    if (i < j)
    {
      paramFile = paramString[i];
      Log.i("MultiDex", "Trying to delete old file " + paramFile.getPath() + " of size " + paramFile.length());
      if (paramFile.delete()) {
        break label163;
      }
      Log.w("MultiDex", "Failed to delete old file " + paramFile.getPath());
    }
    for (;;)
    {
      i += 1;
      break label69;
      break;
      label163:
      Log.i("MultiDex", "Deleted old file " + paramFile.getPath());
    }
  }
  
  private static void putStoredApkInfo(Context paramContext, long paramLong1, long paramLong2, int paramInt)
  {
    paramContext = getMultiDexPreferences(paramContext).edit();
    paramContext.putLong("timestamp", paramLong1);
    paramContext.putLong("crc", paramLong2);
    paramContext.putInt("dex.number", paramInt);
    apply(paramContext);
  }
  
  static boolean verifyZipFile(File paramFile)
  {
    try
    {
      ZipFile localZipFile = new ZipFile(paramFile);
      try
      {
        localZipFile.close();
        return true;
      }
      catch (IOException localIOException1)
      {
        Log.w("MultiDex", "Failed to close zip file: " + paramFile.getAbsolutePath());
      }
    }
    catch (ZipException localZipException)
    {
      for (;;)
      {
        Log.w("MultiDex", "File " + paramFile.getAbsolutePath() + " is not a valid zip file.", localZipException);
      }
    }
    catch (IOException localIOException2)
    {
      for (;;)
      {
        Log.w("MultiDex", "Got an IOException trying to open zip file: " + paramFile.getAbsolutePath(), localIOException2);
      }
    }
    return false;
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/multidex/MultiDexExtractor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */