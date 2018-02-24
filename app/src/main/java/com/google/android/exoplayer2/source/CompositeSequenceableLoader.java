package com.google.android.exoplayer2.source;

public final class CompositeSequenceableLoader
  implements SequenceableLoader
{
  private final SequenceableLoader[] loaders;
  
  public CompositeSequenceableLoader(SequenceableLoader[] paramArrayOfSequenceableLoader)
  {
    this.loaders = paramArrayOfSequenceableLoader;
  }
  
  public boolean continueLoading(long paramLong)
  {
    boolean bool3 = false;
    boolean bool1;
    boolean bool4;
    do
    {
      bool1 = false;
      long l = getNextLoadPositionUs();
      if (l == Long.MIN_VALUE) {
        return bool3;
      }
      SequenceableLoader[] arrayOfSequenceableLoader = this.loaders;
      int j = arrayOfSequenceableLoader.length;
      int i = 0;
      while (i < j)
      {
        SequenceableLoader localSequenceableLoader = arrayOfSequenceableLoader[i];
        boolean bool2 = bool1;
        if (localSequenceableLoader.getNextLoadPositionUs() == l) {
          bool2 = bool1 | localSequenceableLoader.continueLoading(paramLong);
        }
        i += 1;
        bool1 = bool2;
      }
      bool4 = bool3 | bool1;
      bool3 = bool4;
    } while (bool1);
    return bool4;
  }
  
  public long getNextLoadPositionUs()
  {
    long l1 = Long.MAX_VALUE;
    SequenceableLoader[] arrayOfSequenceableLoader = this.loaders;
    int j = arrayOfSequenceableLoader.length;
    int i = 0;
    while (i < j)
    {
      long l3 = arrayOfSequenceableLoader[i].getNextLoadPositionUs();
      l2 = l1;
      if (l3 != Long.MIN_VALUE) {
        l2 = Math.min(l1, l3);
      }
      i += 1;
      l1 = l2;
    }
    long l2 = l1;
    if (l1 == Long.MAX_VALUE) {
      l2 = Long.MIN_VALUE;
    }
    return l2;
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/source/CompositeSequenceableLoader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */