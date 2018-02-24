package com.bumptech.glide.load.engine.bitmap_recycle;

import android.support.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class GroupedLinkedMap<K extends Poolable, V>
{
  private final LinkedEntry<K, V> head = new LinkedEntry();
  private final Map<K, LinkedEntry<K, V>> keyToEntry = new HashMap();
  
  private void makeHead(LinkedEntry<K, V> paramLinkedEntry)
  {
    removeEntry(paramLinkedEntry);
    paramLinkedEntry.prev = this.head;
    paramLinkedEntry.next = this.head.next;
    updateEntry(paramLinkedEntry);
  }
  
  private void makeTail(LinkedEntry<K, V> paramLinkedEntry)
  {
    removeEntry(paramLinkedEntry);
    paramLinkedEntry.prev = this.head.prev;
    paramLinkedEntry.next = this.head;
    updateEntry(paramLinkedEntry);
  }
  
  private static <K, V> void removeEntry(LinkedEntry<K, V> paramLinkedEntry)
  {
    paramLinkedEntry.prev.next = paramLinkedEntry.next;
    paramLinkedEntry.next.prev = paramLinkedEntry.prev;
  }
  
  private static <K, V> void updateEntry(LinkedEntry<K, V> paramLinkedEntry)
  {
    paramLinkedEntry.next.prev = paramLinkedEntry;
    paramLinkedEntry.prev.next = paramLinkedEntry;
  }
  
  @Nullable
  public V get(K paramK)
  {
    LinkedEntry localLinkedEntry = (LinkedEntry)this.keyToEntry.get(paramK);
    if (localLinkedEntry == null)
    {
      localLinkedEntry = new LinkedEntry(paramK);
      this.keyToEntry.put(paramK, localLinkedEntry);
    }
    for (paramK = localLinkedEntry;; paramK = localLinkedEntry)
    {
      makeHead(paramK);
      return (V)paramK.removeLast();
      paramK.offer();
    }
  }
  
  public void put(K paramK, V paramV)
  {
    LinkedEntry localLinkedEntry = (LinkedEntry)this.keyToEntry.get(paramK);
    if (localLinkedEntry == null)
    {
      localLinkedEntry = new LinkedEntry(paramK);
      makeTail(localLinkedEntry);
      this.keyToEntry.put(paramK, localLinkedEntry);
    }
    for (paramK = localLinkedEntry;; paramK = localLinkedEntry)
    {
      paramK.add(paramV);
      return;
      paramK.offer();
    }
  }
  
  @Nullable
  public V removeLast()
  {
    for (LinkedEntry localLinkedEntry = this.head.prev; !localLinkedEntry.equals(this.head); localLinkedEntry = localLinkedEntry.prev)
    {
      Object localObject = localLinkedEntry.removeLast();
      if (localObject != null) {
        return (V)localObject;
      }
      removeEntry(localLinkedEntry);
      this.keyToEntry.remove(localLinkedEntry.key);
      ((Poolable)localLinkedEntry.key).offer();
    }
    return null;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder("GroupedLinkedMap( ");
    LinkedEntry localLinkedEntry = this.head.next;
    int i = 0;
    while (!localLinkedEntry.equals(this.head))
    {
      i = 1;
      localStringBuilder.append('{').append(localLinkedEntry.key).append(':').append(localLinkedEntry.size()).append("}, ");
      localLinkedEntry = localLinkedEntry.next;
    }
    if (i != 0) {
      localStringBuilder.delete(localStringBuilder.length() - 2, localStringBuilder.length());
    }
    return " )";
  }
  
  private static class LinkedEntry<K, V>
  {
    private final K key;
    LinkedEntry<K, V> next = this;
    LinkedEntry<K, V> prev = this;
    private List<V> values;
    
    public LinkedEntry()
    {
      this(null);
    }
    
    public LinkedEntry(K paramK)
    {
      this.key = paramK;
    }
    
    public void add(V paramV)
    {
      if (this.values == null) {
        this.values = new ArrayList();
      }
      this.values.add(paramV);
    }
    
    @Nullable
    public V removeLast()
    {
      int i = size();
      if (i > 0) {
        return (V)this.values.remove(i - 1);
      }
      return null;
    }
    
    public int size()
    {
      if (this.values != null) {
        return this.values.size();
      }
      return 0;
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/load/engine/bitmap_recycle/GroupedLinkedMap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */