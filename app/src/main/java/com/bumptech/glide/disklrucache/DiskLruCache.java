package com.bumptech.glide.disklrucache;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public final class DiskLruCache
  implements Closeable
{
  static final long ANY_SEQUENCE_NUMBER = -1L;
  private static final String CLEAN = "CLEAN";
  private static final String DIRTY = "DIRTY";
  static final String JOURNAL_FILE = "journal";
  static final String JOURNAL_FILE_BACKUP = "journal.bkp";
  static final String JOURNAL_FILE_TEMP = "journal.tmp";
  static final String MAGIC = "libcore.io.DiskLruCache";
  private static final String READ = "READ";
  private static final String REMOVE = "REMOVE";
  static final String VERSION_1 = "1";
  private final int appVersion;
  private final Callable<Void> cleanupCallable = new Callable()
  {
    public Void call()
      throws Exception
    {
      synchronized (DiskLruCache.this)
      {
        if (DiskLruCache.this.journalWriter == null) {
          return null;
        }
        DiskLruCache.this.trimToSize();
        if (DiskLruCache.this.journalRebuildRequired())
        {
          DiskLruCache.this.rebuildJournal();
          DiskLruCache.access$502(DiskLruCache.this, 0);
        }
        return null;
      }
    }
  };
  private final File directory;
  final ThreadPoolExecutor executorService = new ThreadPoolExecutor(0, 1, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue(), new DiskLruCacheThreadFactory(null));
  private final File journalFile;
  private final File journalFileBackup;
  private final File journalFileTmp;
  private Writer journalWriter;
  private final LinkedHashMap<String, Entry> lruEntries = new LinkedHashMap(0, 0.75F, true);
  private long maxSize;
  private long nextSequenceNumber = 0L;
  private int redundantOpCount;
  private long size = 0L;
  private final int valueCount;
  
  private DiskLruCache(File paramFile, int paramInt1, int paramInt2, long paramLong)
  {
    this.directory = paramFile;
    this.appVersion = paramInt1;
    this.journalFile = new File(paramFile, "journal");
    this.journalFileTmp = new File(paramFile, "journal.tmp");
    this.journalFileBackup = new File(paramFile, "journal.bkp");
    this.valueCount = paramInt2;
    this.maxSize = paramLong;
  }
  
  private void checkNotClosed()
  {
    if (this.journalWriter == null) {
      throw new IllegalStateException("cache is closed");
    }
  }
  
  private void completeEdit(Editor paramEditor, boolean paramBoolean)
    throws IOException
  {
    Entry localEntry;
    try
    {
      localEntry = paramEditor.entry;
      if (localEntry.currentEditor != paramEditor) {
        throw new IllegalStateException();
      }
    }
    finally {}
    if ((paramBoolean) && (!localEntry.readable))
    {
      i = 0;
      while (i < this.valueCount)
      {
        if (paramEditor.written[i] == 0)
        {
          paramEditor.abort();
          throw new IllegalStateException(61 + "Newly created entry didn't create value for index " + i);
        }
        if (!localEntry.getDirtyFile(i).exists())
        {
          paramEditor.abort();
          return;
        }
        i += 1;
      }
    }
    int i = 0;
    for (;;)
    {
      long l1;
      if (i < this.valueCount)
      {
        paramEditor = localEntry.getDirtyFile(i);
        if (paramBoolean)
        {
          if (paramEditor.exists())
          {
            File localFile = localEntry.getCleanFile(i);
            paramEditor.renameTo(localFile);
            l1 = localEntry.lengths[i];
            long l2 = localFile.length();
            localEntry.lengths[i] = l2;
            this.size = (this.size - l1 + l2);
          }
        }
        else {
          deleteIfExists(paramEditor);
        }
      }
      else
      {
        this.redundantOpCount += 1;
        Entry.access$802(localEntry, null);
        if ((localEntry.readable | paramBoolean))
        {
          Entry.access$702(localEntry, true);
          this.journalWriter.append("CLEAN");
          this.journalWriter.append(' ');
          this.journalWriter.append(localEntry.key);
          this.journalWriter.append(localEntry.getLengths());
          this.journalWriter.append('\n');
          if (paramBoolean)
          {
            l1 = this.nextSequenceNumber;
            this.nextSequenceNumber = (1L + l1);
            Entry.access$1302(localEntry, l1);
          }
        }
        for (;;)
        {
          this.journalWriter.flush();
          if ((this.size <= this.maxSize) && (!journalRebuildRequired())) {
            break;
          }
          this.executorService.submit(this.cleanupCallable);
          break;
          this.lruEntries.remove(localEntry.key);
          this.journalWriter.append("REMOVE");
          this.journalWriter.append(' ');
          this.journalWriter.append(localEntry.key);
          this.journalWriter.append('\n');
        }
      }
      i += 1;
    }
  }
  
  private static void deleteIfExists(File paramFile)
    throws IOException
  {
    if ((paramFile.exists()) && (!paramFile.delete())) {
      throw new IOException();
    }
  }
  
  private Editor edit(String paramString, long paramLong)
    throws IOException
  {
    Editor localEditor1 = null;
    for (;;)
    {
      Entry localEntry;
      try
      {
        checkNotClosed();
        localEntry = (Entry)this.lruEntries.get(paramString);
        if (paramLong != -1L)
        {
          localObject = localEditor1;
          if (localEntry != null)
          {
            long l = localEntry.sequenceNumber;
            if (l != paramLong) {
              localObject = localEditor1;
            }
          }
          else
          {
            return (Editor)localObject;
          }
        }
        if (localEntry == null)
        {
          localObject = new Entry(paramString, null);
          this.lruEntries.put(paramString, localObject);
          localEditor1 = new Editor((Entry)localObject, null);
          Entry.access$802((Entry)localObject, localEditor1);
          this.journalWriter.append("DIRTY");
          this.journalWriter.append(' ');
          this.journalWriter.append(paramString);
          this.journalWriter.append('\n');
          this.journalWriter.flush();
          localObject = localEditor1;
          continue;
        }
        localEditor2 = localEntry.currentEditor;
      }
      finally {}
      Editor localEditor2;
      Object localObject = localEntry;
      if (localEditor2 != null) {
        localObject = localEditor1;
      }
    }
  }
  
  private static String inputStreamToString(InputStream paramInputStream)
    throws IOException
  {
    return Util.readFully(new InputStreamReader(paramInputStream, Util.UTF_8));
  }
  
  private boolean journalRebuildRequired()
  {
    return (this.redundantOpCount >= 2000) && (this.redundantOpCount >= this.lruEntries.size());
  }
  
  public static DiskLruCache open(File paramFile, int paramInt1, int paramInt2, long paramLong)
    throws IOException
  {
    if (paramLong <= 0L) {
      throw new IllegalArgumentException("maxSize <= 0");
    }
    if (paramInt2 <= 0) {
      throw new IllegalArgumentException("valueCount <= 0");
    }
    Object localObject1 = new File(paramFile, "journal.bkp");
    Object localObject2;
    if (((File)localObject1).exists())
    {
      localObject2 = new File(paramFile, "journal");
      if (!((File)localObject2).exists()) {
        break label115;
      }
      ((File)localObject1).delete();
    }
    for (;;)
    {
      localObject1 = new DiskLruCache(paramFile, paramInt1, paramInt2, paramLong);
      if (!((DiskLruCache)localObject1).journalFile.exists()) {
        break label217;
      }
      try
      {
        ((DiskLruCache)localObject1).readJournal();
        ((DiskLruCache)localObject1).processJournal();
        return (DiskLruCache)localObject1;
      }
      catch (IOException localIOException)
      {
        label115:
        localObject2 = System.out;
        String str1 = String.valueOf(paramFile);
        String str2 = String.valueOf(localIOException.getMessage());
        ((PrintStream)localObject2).println(String.valueOf(str1).length() + 36 + String.valueOf(str2).length() + "DiskLruCache " + str1 + " is corrupt: " + str2 + ", removing");
        ((DiskLruCache)localObject1).delete();
      }
      renameTo((File)localObject1, (File)localObject2, false);
    }
    label217:
    paramFile.mkdirs();
    paramFile = new DiskLruCache(paramFile, paramInt1, paramInt2, paramLong);
    paramFile.rebuildJournal();
    return paramFile;
  }
  
  private void processJournal()
    throws IOException
  {
    deleteIfExists(this.journalFileTmp);
    Iterator localIterator = this.lruEntries.values().iterator();
    while (localIterator.hasNext())
    {
      Entry localEntry = (Entry)localIterator.next();
      int i;
      if (localEntry.currentEditor == null)
      {
        i = 0;
        while (i < this.valueCount)
        {
          this.size += localEntry.lengths[i];
          i += 1;
        }
      }
      else
      {
        Entry.access$802(localEntry, null);
        i = 0;
        while (i < this.valueCount)
        {
          deleteIfExists(localEntry.getCleanFile(i));
          deleteIfExists(localEntry.getDirtyFile(i));
          i += 1;
        }
        localIterator.remove();
      }
    }
  }
  
  private void readJournal()
    throws IOException
  {
    StrictLineReader localStrictLineReader = new StrictLineReader(new FileInputStream(this.journalFile), Util.US_ASCII);
    label269:
    try
    {
      String str1 = localStrictLineReader.readLine();
      String str2 = localStrictLineReader.readLine();
      String str3 = localStrictLineReader.readLine();
      String str4 = localStrictLineReader.readLine();
      String str5 = localStrictLineReader.readLine();
      if ((!"libcore.io.DiskLruCache".equals(str1)) || (!"1".equals(str2)) || (!Integer.toString(this.appVersion).equals(str3)) || (!Integer.toString(this.valueCount).equals(str4)) || (!"".equals(str5))) {
        throw new IOException(String.valueOf(str1).length() + 35 + String.valueOf(str2).length() + String.valueOf(str4).length() + String.valueOf(str5).length() + "unexpected journal header: [" + str1 + ", " + str2 + ", " + str4 + ", " + str5 + "]");
      }
    }
    finally
    {
      Util.closeQuietly(localStrictLineReader);
      throw ((Throwable)localObject);
      int i = 0;
      try
      {
        for (;;)
        {
          readJournalLine(localStrictLineReader.readLine());
          i += 1;
        }
        rebuildJournal();
      }
      catch (EOFException localEOFException)
      {
        this.redundantOpCount = (i - this.lruEntries.size());
        if (!localStrictLineReader.hasUnterminatedLine()) {
          break label269;
        }
      }
      Util.closeQuietly(localStrictLineReader);
      return;
    }
  }
  
  private void readJournalLine(String paramString)
    throws IOException
  {
    int i = paramString.indexOf(' ');
    if (i == -1)
    {
      paramString = String.valueOf(paramString);
      if (paramString.length() != 0) {}
      for (paramString = "unexpected journal line: ".concat(paramString);; paramString = new String("unexpected journal line: ")) {
        throw new IOException(paramString);
      }
    }
    int j = i + 1;
    int k = paramString.indexOf(' ', j);
    Object localObject2;
    Object localObject1;
    if (k == -1)
    {
      localObject2 = paramString.substring(j);
      localObject1 = localObject2;
      if (i != "REMOVE".length()) {
        break label127;
      }
      localObject1 = localObject2;
      if (!paramString.startsWith("REMOVE")) {
        break label127;
      }
      this.lruEntries.remove(localObject2);
    }
    label127:
    do
    {
      return;
      localObject1 = paramString.substring(j, k);
      Entry localEntry = (Entry)this.lruEntries.get(localObject1);
      localObject2 = localEntry;
      if (localEntry == null)
      {
        localObject2 = new Entry((String)localObject1, null);
        this.lruEntries.put(localObject1, localObject2);
      }
      if ((k != -1) && (i == "CLEAN".length()) && (paramString.startsWith("CLEAN")))
      {
        paramString = paramString.substring(k + 1).split(" ");
        Entry.access$702((Entry)localObject2, true);
        Entry.access$802((Entry)localObject2, null);
        ((Entry)localObject2).setLengths(paramString);
        return;
      }
      if ((k == -1) && (i == "DIRTY".length()) && (paramString.startsWith("DIRTY")))
      {
        Entry.access$802((Entry)localObject2, new Editor((Entry)localObject2, null));
        return;
      }
    } while ((k == -1) && (i == "READ".length()) && (paramString.startsWith("READ")));
    paramString = String.valueOf(paramString);
    if (paramString.length() != 0) {}
    for (paramString = "unexpected journal line: ".concat(paramString);; paramString = new String("unexpected journal line: ")) {
      throw new IOException(paramString);
    }
  }
  
  private void rebuildJournal()
    throws IOException
  {
    for (;;)
    {
      String str1;
      try
      {
        if (this.journalWriter != null) {
          this.journalWriter.close();
        }
        BufferedWriter localBufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.journalFileTmp), Util.US_ASCII));
        try
        {
          localBufferedWriter.write("libcore.io.DiskLruCache");
          localBufferedWriter.write("\n");
          localBufferedWriter.write("1");
          localBufferedWriter.write("\n");
          localBufferedWriter.write(Integer.toString(this.appVersion));
          localBufferedWriter.write("\n");
          localBufferedWriter.write(Integer.toString(this.valueCount));
          localBufferedWriter.write("\n");
          localBufferedWriter.write("\n");
          Iterator localIterator = this.lruEntries.values().iterator();
          if (!localIterator.hasNext()) {
            break;
          }
          localObject3 = (Entry)localIterator.next();
          if (((Entry)localObject3).currentEditor != null)
          {
            str1 = String.valueOf("DIRTY ");
            localObject3 = ((Entry)localObject3).key;
            localBufferedWriter.write(String.valueOf(str1).length() + 1 + String.valueOf(localObject3).length() + str1 + (String)localObject3 + "\n");
            continue;
            localObject1 = finally;
          }
        }
        finally
        {
          localBufferedWriter.close();
        }
        str1 = String.valueOf("CLEAN ");
      }
      finally {}
      String str2 = ((Entry)localObject3).key;
      Object localObject3 = String.valueOf(((Entry)localObject3).getLengths());
      ((Writer)localObject1).write(String.valueOf(str1).length() + 1 + String.valueOf(str2).length() + String.valueOf(localObject3).length() + str1 + str2 + (String)localObject3 + "\n");
    }
    ((Writer)localObject1).close();
    if (this.journalFile.exists()) {
      renameTo(this.journalFile, this.journalFileBackup, true);
    }
    renameTo(this.journalFileTmp, this.journalFile, false);
    this.journalFileBackup.delete();
    this.journalWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.journalFile, true), Util.US_ASCII));
  }
  
  private static void renameTo(File paramFile1, File paramFile2, boolean paramBoolean)
    throws IOException
  {
    if (paramBoolean) {
      deleteIfExists(paramFile2);
    }
    if (!paramFile1.renameTo(paramFile2)) {
      throw new IOException();
    }
  }
  
  private void trimToSize()
    throws IOException
  {
    while (this.size > this.maxSize) {
      remove((String)((Map.Entry)this.lruEntries.entrySet().iterator().next()).getKey());
    }
  }
  
  public void close()
    throws IOException
  {
    for (;;)
    {
      try
      {
        Object localObject1 = this.journalWriter;
        if (localObject1 == null) {
          return;
        }
        localObject1 = new ArrayList(this.lruEntries.values()).iterator();
        if (((Iterator)localObject1).hasNext())
        {
          Entry localEntry = (Entry)((Iterator)localObject1).next();
          if (localEntry.currentEditor == null) {
            continue;
          }
          localEntry.currentEditor.abort();
          continue;
        }
        trimToSize();
      }
      finally {}
      this.journalWriter.close();
      this.journalWriter = null;
    }
  }
  
  public void delete()
    throws IOException
  {
    close();
    Util.deleteContents(this.directory);
  }
  
  public Editor edit(String paramString)
    throws IOException
  {
    return edit(paramString, -1L);
  }
  
  public void flush()
    throws IOException
  {
    try
    {
      checkNotClosed();
      trimToSize();
      this.journalWriter.flush();
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  /* Error */
  public Value get(String paramString)
    throws IOException
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 5
    //   3: aload_0
    //   4: monitorenter
    //   5: aload_0
    //   6: invokespecial 295	com/bumptech/glide/disklrucache/DiskLruCache:checkNotClosed	()V
    //   9: aload_0
    //   10: getfield 85	com/bumptech/glide/disklrucache/DiskLruCache:lruEntries	Ljava/util/LinkedHashMap;
    //   13: aload_1
    //   14: invokevirtual 298	java/util/LinkedHashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   17: checkcast 16	com/bumptech/glide/disklrucache/DiskLruCache$Entry
    //   20: astore 6
    //   22: aload 6
    //   24: ifnonnull +12 -> 36
    //   27: aload 5
    //   29: astore 4
    //   31: aload_0
    //   32: monitorexit
    //   33: aload 4
    //   35: areturn
    //   36: aload 5
    //   38: astore 4
    //   40: aload 6
    //   42: invokestatic 200	com/bumptech/glide/disklrucache/DiskLruCache$Entry:access$700	(Lcom/bumptech/glide/disklrucache/DiskLruCache$Entry;)Z
    //   45: ifeq -14 -> 31
    //   48: aload 6
    //   50: getfield 539	com/bumptech/glide/disklrucache/DiskLruCache$Entry:cleanFiles	[Ljava/io/File;
    //   53: astore 7
    //   55: aload 7
    //   57: arraylength
    //   58: istore_3
    //   59: iconst_0
    //   60: istore_2
    //   61: iload_2
    //   62: iload_3
    //   63: if_icmpge +24 -> 87
    //   66: aload 5
    //   68: astore 4
    //   70: aload 7
    //   72: iload_2
    //   73: aaload
    //   74: invokevirtual 232	java/io/File:exists	()Z
    //   77: ifeq -46 -> 31
    //   80: iload_2
    //   81: iconst_1
    //   82: iadd
    //   83: istore_2
    //   84: goto -23 -> 61
    //   87: aload_0
    //   88: aload_0
    //   89: getfield 179	com/bumptech/glide/disklrucache/DiskLruCache:redundantOpCount	I
    //   92: iconst_1
    //   93: iadd
    //   94: putfield 179	com/bumptech/glide/disklrucache/DiskLruCache:redundantOpCount	I
    //   97: aload_0
    //   98: getfield 137	com/bumptech/glide/disklrucache/DiskLruCache:journalWriter	Ljava/io/Writer;
    //   101: ldc 43
    //   103: invokevirtual 264	java/io/Writer:append	(Ljava/lang/CharSequence;)Ljava/io/Writer;
    //   106: pop
    //   107: aload_0
    //   108: getfield 137	com/bumptech/glide/disklrucache/DiskLruCache:journalWriter	Ljava/io/Writer;
    //   111: bipush 32
    //   113: invokevirtual 267	java/io/Writer:append	(C)Ljava/io/Writer;
    //   116: pop
    //   117: aload_0
    //   118: getfield 137	com/bumptech/glide/disklrucache/DiskLruCache:journalWriter	Ljava/io/Writer;
    //   121: aload_1
    //   122: invokevirtual 264	java/io/Writer:append	(Ljava/lang/CharSequence;)Ljava/io/Writer;
    //   125: pop
    //   126: aload_0
    //   127: getfield 137	com/bumptech/glide/disklrucache/DiskLruCache:journalWriter	Ljava/io/Writer;
    //   130: bipush 10
    //   132: invokevirtual 267	java/io/Writer:append	(C)Ljava/io/Writer;
    //   135: pop
    //   136: aload_0
    //   137: invokespecial 171	com/bumptech/glide/disklrucache/DiskLruCache:journalRebuildRequired	()Z
    //   140: ifeq +15 -> 155
    //   143: aload_0
    //   144: getfield 108	com/bumptech/glide/disklrucache/DiskLruCache:executorService	Ljava/util/concurrent/ThreadPoolExecutor;
    //   147: aload_0
    //   148: getfield 113	com/bumptech/glide/disklrucache/DiskLruCache:cleanupCallable	Ljava/util/concurrent/Callable;
    //   151: invokevirtual 285	java/util/concurrent/ThreadPoolExecutor:submit	(Ljava/util/concurrent/Callable;)Ljava/util/concurrent/Future;
    //   154: pop
    //   155: new 19	com/bumptech/glide/disklrucache/DiskLruCache$Value
    //   158: dup
    //   159: aload_0
    //   160: aload_1
    //   161: aload 6
    //   163: invokestatic 302	com/bumptech/glide/disklrucache/DiskLruCache$Entry:access$1300	(Lcom/bumptech/glide/disklrucache/DiskLruCache$Entry;)J
    //   166: aload 6
    //   168: getfield 539	com/bumptech/glide/disklrucache/DiskLruCache$Entry:cleanFiles	[Ljava/io/File;
    //   171: aload 6
    //   173: invokestatic 243	com/bumptech/glide/disklrucache/DiskLruCache$Entry:access$1100	(Lcom/bumptech/glide/disklrucache/DiskLruCache$Entry;)[J
    //   176: aconst_null
    //   177: invokespecial 542	com/bumptech/glide/disklrucache/DiskLruCache$Value:<init>	(Lcom/bumptech/glide/disklrucache/DiskLruCache;Ljava/lang/String;J[Ljava/io/File;[JLcom/bumptech/glide/disklrucache/DiskLruCache$1;)V
    //   180: astore 4
    //   182: goto -151 -> 31
    //   185: astore_1
    //   186: aload_0
    //   187: monitorexit
    //   188: aload_1
    //   189: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	190	0	this	DiskLruCache
    //   0	190	1	paramString	String
    //   60	24	2	i	int
    //   58	6	3	j	int
    //   29	152	4	localObject1	Object
    //   1	66	5	localObject2	Object
    //   20	152	6	localEntry	Entry
    //   53	18	7	arrayOfFile	File[]
    // Exception table:
    //   from	to	target	type
    //   5	22	185	finally
    //   40	59	185	finally
    //   70	80	185	finally
    //   87	155	185	finally
    //   155	182	185	finally
  }
  
  public File getDirectory()
  {
    return this.directory;
  }
  
  public long getMaxSize()
  {
    try
    {
      long l = this.maxSize;
      return l;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  /* Error */
  public boolean isClosed()
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 137	com/bumptech/glide/disklrucache/DiskLruCache:journalWriter	Ljava/io/Writer;
    //   6: astore_2
    //   7: aload_2
    //   8: ifnonnull +9 -> 17
    //   11: iconst_1
    //   12: istore_1
    //   13: aload_0
    //   14: monitorexit
    //   15: iload_1
    //   16: ireturn
    //   17: iconst_0
    //   18: istore_1
    //   19: goto -6 -> 13
    //   22: astore_2
    //   23: aload_0
    //   24: monitorexit
    //   25: aload_2
    //   26: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	27	0	this	DiskLruCache
    //   12	7	1	bool	boolean
    //   6	2	2	localWriter	Writer
    //   22	4	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   2	7	22	finally
  }
  
  public boolean remove(String paramString)
    throws IOException
  {
    for (;;)
    {
      Entry localEntry;
      int i;
      try
      {
        checkNotClosed();
        localEntry = (Entry)this.lruEntries.get(paramString);
        if (localEntry != null)
        {
          localObject = localEntry.currentEditor;
          if (localObject == null) {}
        }
        else
        {
          bool = false;
          return bool;
        }
        i = 0;
        if (i >= this.valueCount) {
          break label156;
        }
        Object localObject = localEntry.getCleanFile(i);
        if ((((File)localObject).exists()) && (!((File)localObject).delete()))
        {
          paramString = String.valueOf(localObject);
          throw new IOException(String.valueOf(paramString).length() + 17 + "failed to delete " + paramString);
        }
      }
      finally {}
      this.size -= localEntry.lengths[i];
      localEntry.lengths[i] = 0L;
      i += 1;
      continue;
      label156:
      this.redundantOpCount += 1;
      this.journalWriter.append("REMOVE");
      this.journalWriter.append(' ');
      this.journalWriter.append(paramString);
      this.journalWriter.append('\n');
      this.lruEntries.remove(paramString);
      if (journalRebuildRequired()) {
        this.executorService.submit(this.cleanupCallable);
      }
      boolean bool = true;
    }
  }
  
  public void setMaxSize(long paramLong)
  {
    try
    {
      this.maxSize = paramLong;
      this.executorService.submit(this.cleanupCallable);
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public long size()
  {
    try
    {
      long l = this.size;
      return l;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  private static final class DiskLruCacheThreadFactory
    implements ThreadFactory
  {
    public Thread newThread(Runnable paramRunnable)
    {
      try
      {
        paramRunnable = new Thread(paramRunnable, "glide-disk-lru-cache-thread");
        paramRunnable.setPriority(1);
        return paramRunnable;
      }
      finally
      {
        paramRunnable = finally;
        throw paramRunnable;
      }
    }
  }
  
  public final class Editor
  {
    private boolean committed;
    private final DiskLruCache.Entry entry;
    private final boolean[] written;
    
    private Editor(DiskLruCache.Entry paramEntry)
    {
      this.entry = paramEntry;
      if (DiskLruCache.Entry.access$700(paramEntry)) {}
      for (this$1 = null;; this$1 = new boolean[DiskLruCache.this.valueCount])
      {
        this.written = DiskLruCache.this;
        return;
      }
    }
    
    private InputStream newInputStream(int paramInt)
      throws IOException
    {
      synchronized (DiskLruCache.this)
      {
        if (DiskLruCache.Entry.access$800(this.entry) != this) {
          throw new IllegalStateException();
        }
      }
      if (!DiskLruCache.Entry.access$700(this.entry)) {
        return null;
      }
      try
      {
        FileInputStream localFileInputStream = new FileInputStream(this.entry.getCleanFile(paramInt));
        return localFileInputStream;
      }
      catch (FileNotFoundException localFileNotFoundException) {}
      return null;
    }
    
    public void abort()
      throws IOException
    {
      DiskLruCache.this.completeEdit(this, false);
    }
    
    public void abortUnlessCommitted()
    {
      if (!this.committed) {}
      try
      {
        abort();
        return;
      }
      catch (IOException localIOException) {}
    }
    
    public void commit()
      throws IOException
    {
      DiskLruCache.this.completeEdit(this, true);
      this.committed = true;
    }
    
    public File getFile(int paramInt)
      throws IOException
    {
      synchronized (DiskLruCache.this)
      {
        if (DiskLruCache.Entry.access$800(this.entry) != this) {
          throw new IllegalStateException();
        }
      }
      if (!DiskLruCache.Entry.access$700(this.entry)) {
        this.written[paramInt] = true;
      }
      File localFile = this.entry.getDirtyFile(paramInt);
      if (!DiskLruCache.this.directory.exists()) {
        DiskLruCache.this.directory.mkdirs();
      }
      return localFile;
    }
    
    public String getString(int paramInt)
      throws IOException
    {
      InputStream localInputStream = newInputStream(paramInt);
      if (localInputStream != null) {
        return DiskLruCache.inputStreamToString(localInputStream);
      }
      return null;
    }
    
    public void set(int paramInt, String paramString)
      throws IOException
    {
      Object localObject3 = null;
      try
      {
        OutputStreamWriter localOutputStreamWriter = new OutputStreamWriter(new FileOutputStream(getFile(paramInt)), Util.UTF_8);
        Util.closeQuietly(paramString);
      }
      finally
      {
        try
        {
          localOutputStreamWriter.write(paramString);
          Util.closeQuietly(localOutputStreamWriter);
          return;
        }
        finally
        {
          paramString = (String)localObject1;
          Object localObject2 = localObject4;
        }
        localObject1 = finally;
        paramString = (String)localObject3;
      }
      throw ((Throwable)localObject1);
    }
  }
  
  private final class Entry
  {
    File[] cleanFiles;
    private DiskLruCache.Editor currentEditor;
    File[] dirtyFiles;
    private final String key;
    private final long[] lengths;
    private boolean readable;
    private long sequenceNumber;
    
    private Entry(String paramString)
    {
      this.key = paramString;
      this.lengths = new long[DiskLruCache.this.valueCount];
      this.cleanFiles = new File[DiskLruCache.this.valueCount];
      this.dirtyFiles = new File[DiskLruCache.this.valueCount];
      paramString = new StringBuilder(paramString).append('.');
      int j = paramString.length();
      int i = 0;
      while (i < DiskLruCache.this.valueCount)
      {
        paramString.append(i);
        this.cleanFiles[i] = new File(DiskLruCache.this.directory, paramString.toString());
        paramString.append(".tmp");
        this.dirtyFiles[i] = new File(DiskLruCache.this.directory, paramString.toString());
        paramString.setLength(j);
        i += 1;
      }
    }
    
    private IOException invalidLengths(String[] paramArrayOfString)
      throws IOException
    {
      paramArrayOfString = String.valueOf(Arrays.toString(paramArrayOfString));
      if (paramArrayOfString.length() != 0) {}
      for (paramArrayOfString = "unexpected journal line: ".concat(paramArrayOfString);; paramArrayOfString = new String("unexpected journal line: ")) {
        throw new IOException(paramArrayOfString);
      }
    }
    
    private void setLengths(String[] paramArrayOfString)
      throws IOException
    {
      if (paramArrayOfString.length != DiskLruCache.this.valueCount) {
        throw invalidLengths(paramArrayOfString);
      }
      int i = 0;
      try
      {
        while (i < paramArrayOfString.length)
        {
          this.lengths[i] = Long.parseLong(paramArrayOfString[i]);
          i += 1;
        }
        return;
      }
      catch (NumberFormatException localNumberFormatException)
      {
        throw invalidLengths(paramArrayOfString);
      }
    }
    
    public File getCleanFile(int paramInt)
    {
      return this.cleanFiles[paramInt];
    }
    
    public File getDirtyFile(int paramInt)
    {
      return this.dirtyFiles[paramInt];
    }
    
    public String getLengths()
      throws IOException
    {
      StringBuilder localStringBuilder = new StringBuilder();
      long[] arrayOfLong = this.lengths;
      int j = arrayOfLong.length;
      int i = 0;
      while (i < j)
      {
        long l = arrayOfLong[i];
        localStringBuilder.append(' ').append(l);
        i += 1;
      }
      return localStringBuilder.toString();
    }
  }
  
  public final class Value
  {
    private final File[] files;
    private final String key;
    private final long[] lengths;
    private final long sequenceNumber;
    
    private Value(String paramString, long paramLong, File[] paramArrayOfFile, long[] paramArrayOfLong)
    {
      this.key = paramString;
      this.sequenceNumber = paramLong;
      this.files = paramArrayOfFile;
      this.lengths = paramArrayOfLong;
    }
    
    public DiskLruCache.Editor edit()
      throws IOException
    {
      return DiskLruCache.this.edit(this.key, this.sequenceNumber);
    }
    
    public File getFile(int paramInt)
    {
      return this.files[paramInt];
    }
    
    public long getLength(int paramInt)
    {
      return this.lengths[paramInt];
    }
    
    public String getString(int paramInt)
      throws IOException
    {
      return DiskLruCache.inputStreamToString(new FileInputStream(this.files[paramInt]));
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/disklrucache/DiskLruCache.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */