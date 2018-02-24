package com.bumptech.glide.load.engine.prefill;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

final class PreFillQueue
{
  private final Map<PreFillType, Integer> bitmapsPerType;
  private int bitmapsRemaining;
  private int keyIndex;
  private final List<PreFillType> keyList;
  
  public PreFillQueue(Map<PreFillType, Integer> paramMap)
  {
    this.bitmapsPerType = paramMap;
    this.keyList = new ArrayList(paramMap.keySet());
    paramMap = paramMap.values().iterator();
    while (paramMap.hasNext())
    {
      Integer localInteger = (Integer)paramMap.next();
      this.bitmapsRemaining += localInteger.intValue();
    }
  }
  
  public int getSize()
  {
    return this.bitmapsRemaining;
  }
  
  public boolean isEmpty()
  {
    return this.bitmapsRemaining == 0;
  }
  
  public PreFillType remove()
  {
    PreFillType localPreFillType = (PreFillType)this.keyList.get(this.keyIndex);
    Integer localInteger = (Integer)this.bitmapsPerType.get(localPreFillType);
    if (localInteger.intValue() == 1)
    {
      this.bitmapsPerType.remove(localPreFillType);
      this.keyList.remove(this.keyIndex);
      this.bitmapsRemaining -= 1;
      if (!this.keyList.isEmpty()) {
        break label118;
      }
    }
    label118:
    for (int i = 0;; i = (this.keyIndex + 1) % this.keyList.size())
    {
      this.keyIndex = i;
      return localPreFillType;
      this.bitmapsPerType.put(localPreFillType, Integer.valueOf(localInteger.intValue() - 1));
      break;
    }
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/load/engine/prefill/PreFillQueue.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */