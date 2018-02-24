package com.google.android.gms.common.data;

import java.util.NoSuchElementException;

public class zzg<T>
  extends zzb<T>
{
  private T zzaQb;
  
  public zzg(DataBuffer<T> paramDataBuffer)
  {
    super(paramDataBuffer);
  }
  
  public T next()
  {
    if (!hasNext())
    {
      int i = this.zzaPF;
      throw new NoSuchElementException(46 + "Cannot advance the iterator beyond " + i);
    }
    this.zzaPF += 1;
    if (this.zzaPF == 0)
    {
      this.zzaQb = this.zzaPE.get(0);
      if (!(this.zzaQb instanceof zzc))
      {
        String str = String.valueOf(this.zzaQb.getClass());
        throw new IllegalStateException(String.valueOf(str).length() + 44 + "DataBuffer reference of type " + str + " is not movable");
      }
    }
    else
    {
      ((zzc)this.zzaQb).zzfL(this.zzaPF);
    }
    return (T)this.zzaQb;
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/common/data/zzg.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */