package com.google.protobuf.nano;

import java.util.HashMap;
import java.util.Map;

public final class MapFactories
{
  private static volatile MapFactory mapFactory = new DefaultMapFactory(null);
  
  public static MapFactory getMapFactory()
  {
    return mapFactory;
  }
  
  static void setMapFactory(MapFactory paramMapFactory)
  {
    mapFactory = paramMapFactory;
  }
  
  private static class DefaultMapFactory
    implements MapFactories.MapFactory
  {
    public <K, V> Map<K, V> forMap(Map<K, V> paramMap)
    {
      Object localObject = paramMap;
      if (paramMap == null) {
        localObject = new HashMap();
      }
      return (Map<K, V>)localObject;
    }
  }
  
  public static abstract interface MapFactory
  {
    public abstract <K, V> Map<K, V> forMap(Map<K, V> paramMap);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/protobuf/nano/MapFactories.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */