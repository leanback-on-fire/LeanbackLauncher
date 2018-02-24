package com.google.android.exoplayer2.util;

public final class ConditionVariable
{
  private boolean isOpen;
  
  /* Error */
  public void block()
    throws java.lang.InterruptedException
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 16	com/google/android/exoplayer2/util/ConditionVariable:isOpen	Z
    //   6: ifne +15 -> 21
    //   9: aload_0
    //   10: invokevirtual 19	java/lang/Object:wait	()V
    //   13: goto -11 -> 2
    //   16: astore_1
    //   17: aload_0
    //   18: monitorexit
    //   19: aload_1
    //   20: athrow
    //   21: aload_0
    //   22: monitorexit
    //   23: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	24	0	this	ConditionVariable
    //   16	4	1	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   2	13	16	finally
  }
  
  public boolean close()
  {
    try
    {
      boolean bool = this.isOpen;
      this.isOpen = false;
      return bool;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  /* Error */
  public boolean open()
  {
    // Byte code:
    //   0: iconst_1
    //   1: istore_1
    //   2: aload_0
    //   3: monitorenter
    //   4: aload_0
    //   5: getfield 16	com/google/android/exoplayer2/util/ConditionVariable:isOpen	Z
    //   8: istore_2
    //   9: iload_2
    //   10: ifeq +9 -> 19
    //   13: iconst_0
    //   14: istore_1
    //   15: aload_0
    //   16: monitorexit
    //   17: iload_1
    //   18: ireturn
    //   19: aload_0
    //   20: iconst_1
    //   21: putfield 16	com/google/android/exoplayer2/util/ConditionVariable:isOpen	Z
    //   24: aload_0
    //   25: invokevirtual 26	java/lang/Object:notifyAll	()V
    //   28: goto -13 -> 15
    //   31: astore_3
    //   32: aload_0
    //   33: monitorexit
    //   34: aload_3
    //   35: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	36	0	this	ConditionVariable
    //   1	17	1	bool1	boolean
    //   8	2	2	bool2	boolean
    //   31	4	3	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   4	9	31	finally
    //   19	28	31	finally
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/util/ConditionVariable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */