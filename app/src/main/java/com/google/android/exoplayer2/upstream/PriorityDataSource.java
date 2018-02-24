package com.google.android.exoplayer2.upstream;

import android.net.Uri;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.PriorityTaskManager;
import java.io.IOException;

public final class PriorityDataSource
  implements DataSource
{
  private final int priority;
  private final PriorityTaskManager priorityTaskManager;
  private final DataSource upstream;
  
  public PriorityDataSource(DataSource paramDataSource, PriorityTaskManager paramPriorityTaskManager, int paramInt)
  {
    this.upstream = ((DataSource)Assertions.checkNotNull(paramDataSource));
    this.priorityTaskManager = ((PriorityTaskManager)Assertions.checkNotNull(paramPriorityTaskManager));
    this.priority = paramInt;
  }
  
  public void close()
    throws IOException
  {
    this.upstream.close();
  }
  
  public Uri getUri()
  {
    return this.upstream.getUri();
  }
  
  public long open(DataSpec paramDataSpec)
    throws IOException
  {
    this.priorityTaskManager.proceedOrThrow(this.priority);
    return this.upstream.open(paramDataSpec);
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    this.priorityTaskManager.proceedOrThrow(this.priority);
    return this.upstream.read(paramArrayOfByte, paramInt1, paramInt2);
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/upstream/PriorityDataSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */