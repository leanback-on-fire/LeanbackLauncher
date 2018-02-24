package com.google.android.exoplayer2.util;

public final class LibraryLoader
{
  private boolean isAvailable;
  private boolean loadAttempted;
  private String[] nativeLibraries;
  
  public LibraryLoader(String... paramVarArgs)
  {
    this.nativeLibraries = paramVarArgs;
  }
  
  public boolean isAvailable()
  {
    for (;;)
    {
      String[] arrayOfString;
      int j;
      int i;
      try
      {
        if (this.loadAttempted)
        {
          bool = this.isAvailable;
          return bool;
        }
        this.loadAttempted = true;
      }
      finally {}
      try
      {
        arrayOfString = this.nativeLibraries;
        j = arrayOfString.length;
        i = 0;
        if (i < j)
        {
          System.loadLibrary(arrayOfString[i]);
          i += 1;
          continue;
        }
        this.isAvailable = true;
      }
      catch (UnsatisfiedLinkError localUnsatisfiedLinkError)
      {
        continue;
      }
      boolean bool = this.isAvailable;
    }
  }
  
  /* Error */
  public void setLibraries(String... paramVarArgs)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 22	com/google/android/exoplayer2/util/LibraryLoader:loadAttempted	Z
    //   6: ifne +19 -> 25
    //   9: iconst_1
    //   10: istore_2
    //   11: iload_2
    //   12: ldc 33
    //   14: invokestatic 39	com/google/android/exoplayer2/util/Assertions:checkState	(ZLjava/lang/Object;)V
    //   17: aload_0
    //   18: aload_1
    //   19: putfield 16	com/google/android/exoplayer2/util/LibraryLoader:nativeLibraries	[Ljava/lang/String;
    //   22: aload_0
    //   23: monitorexit
    //   24: return
    //   25: iconst_0
    //   26: istore_2
    //   27: goto -16 -> 11
    //   30: astore_1
    //   31: aload_0
    //   32: monitorexit
    //   33: aload_1
    //   34: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	35	0	this	LibraryLoader
    //   0	35	1	paramVarArgs	String[]
    //   10	17	2	bool	boolean
    // Exception table:
    //   from	to	target	type
    //   2	9	30	finally
    //   11	22	30	finally
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/util/LibraryLoader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */