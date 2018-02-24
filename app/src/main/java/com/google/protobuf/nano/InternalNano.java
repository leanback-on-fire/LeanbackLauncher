package com.google.protobuf.nano;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public final class InternalNano
{
  protected static final Charset ISO_8859_1 = Charset.forName("ISO-8859-1");
  public static final Object LAZY_INIT_LOCK = new Object();
  public static final int TYPE_BOOL = 8;
  public static final int TYPE_BYTES = 12;
  public static final int TYPE_DOUBLE = 1;
  public static final int TYPE_ENUM = 14;
  public static final int TYPE_FIXED32 = 7;
  public static final int TYPE_FIXED64 = 6;
  public static final int TYPE_FLOAT = 2;
  public static final int TYPE_GROUP = 10;
  public static final int TYPE_INT32 = 5;
  public static final int TYPE_INT64 = 3;
  public static final int TYPE_MESSAGE = 11;
  public static final int TYPE_SFIXED32 = 15;
  public static final int TYPE_SFIXED64 = 16;
  public static final int TYPE_SINT32 = 17;
  public static final int TYPE_SINT64 = 18;
  public static final int TYPE_STRING = 9;
  public static final int TYPE_UINT32 = 13;
  public static final int TYPE_UINT64 = 4;
  protected static final Charset UTF_8 = Charset.forName("UTF-8");
  
  public static byte[] bytesDefaultValue(String paramString)
  {
    return paramString.getBytes(ISO_8859_1);
  }
  
  public static void cloneUnknownFieldData(ExtendableMessageNano paramExtendableMessageNano1, ExtendableMessageNano paramExtendableMessageNano2)
  {
    if (paramExtendableMessageNano1.unknownFieldData != null) {
      paramExtendableMessageNano2.unknownFieldData = paramExtendableMessageNano1.unknownFieldData.clone();
    }
  }
  
  public static <K, V> int computeMapFieldSize(Map<K, V> paramMap, int paramInt1, int paramInt2, int paramInt3)
  {
    int i = 0;
    int j = CodedOutputByteBufferNano.computeTagSize(paramInt1);
    paramMap = paramMap.entrySet().iterator();
    paramInt1 = i;
    while (paramMap.hasNext())
    {
      Object localObject2 = (Map.Entry)paramMap.next();
      Object localObject1 = ((Map.Entry)localObject2).getKey();
      localObject2 = ((Map.Entry)localObject2).getValue();
      if ((localObject1 == null) || (localObject2 == null)) {
        throw new IllegalStateException("keys and values in maps cannot be null");
      }
      i = CodedOutputByteBufferNano.computeFieldSize(1, paramInt2, localObject1) + CodedOutputByteBufferNano.computeFieldSize(2, paramInt3, localObject2);
      paramInt1 += j + i + CodedOutputByteBufferNano.computeRawVarint32Size(i);
    }
    return paramInt1;
  }
  
  public static byte[] copyFromUtf8(String paramString)
  {
    return paramString.getBytes(UTF_8);
  }
  
  public static <K, V> boolean equals(Map<K, V> paramMap1, Map<K, V> paramMap2)
  {
    if (paramMap1 == paramMap2) {}
    Map.Entry localEntry;
    do
    {
      while (!paramMap1.hasNext())
      {
        do
        {
          do
          {
            return true;
            if (paramMap1 != null) {
              break;
            }
          } while (paramMap2.size() == 0);
          return false;
          if (paramMap2 != null) {
            break;
          }
        } while (paramMap1.size() == 0);
        return false;
        if (paramMap1.size() != paramMap2.size()) {
          return false;
        }
        paramMap1 = paramMap1.entrySet().iterator();
      }
      localEntry = (Map.Entry)paramMap1.next();
      if (!paramMap2.containsKey(localEntry.getKey())) {
        return false;
      }
    } while (equalsMapValue(localEntry.getValue(), paramMap2.get(localEntry.getKey())));
    return false;
  }
  
  public static boolean equals(double[] paramArrayOfDouble1, double[] paramArrayOfDouble2)
  {
    if ((paramArrayOfDouble1 == null) || (paramArrayOfDouble1.length == 0)) {
      return (paramArrayOfDouble2 == null) || (paramArrayOfDouble2.length == 0);
    }
    return Arrays.equals(paramArrayOfDouble1, paramArrayOfDouble2);
  }
  
  public static boolean equals(float[] paramArrayOfFloat1, float[] paramArrayOfFloat2)
  {
    if ((paramArrayOfFloat1 == null) || (paramArrayOfFloat1.length == 0)) {
      return (paramArrayOfFloat2 == null) || (paramArrayOfFloat2.length == 0);
    }
    return Arrays.equals(paramArrayOfFloat1, paramArrayOfFloat2);
  }
  
  public static boolean equals(int[] paramArrayOfInt1, int[] paramArrayOfInt2)
  {
    if ((paramArrayOfInt1 == null) || (paramArrayOfInt1.length == 0)) {
      return (paramArrayOfInt2 == null) || (paramArrayOfInt2.length == 0);
    }
    return Arrays.equals(paramArrayOfInt1, paramArrayOfInt2);
  }
  
  public static boolean equals(long[] paramArrayOfLong1, long[] paramArrayOfLong2)
  {
    if ((paramArrayOfLong1 == null) || (paramArrayOfLong1.length == 0)) {
      return (paramArrayOfLong2 == null) || (paramArrayOfLong2.length == 0);
    }
    return Arrays.equals(paramArrayOfLong1, paramArrayOfLong2);
  }
  
  public static boolean equals(Object[] paramArrayOfObject1, Object[] paramArrayOfObject2)
  {
    boolean bool2 = false;
    int i = 0;
    int k;
    int j;
    int m;
    if (paramArrayOfObject1 == null)
    {
      k = 0;
      j = 0;
      if (paramArrayOfObject2 != null) {
        break label53;
      }
      m = 0;
    }
    for (;;)
    {
      int n = j;
      if (i < k)
      {
        n = j;
        if (paramArrayOfObject1[i] == null)
        {
          i += 1;
          continue;
          k = paramArrayOfObject1.length;
          break;
          label53:
          m = paramArrayOfObject2.length;
          continue;
        }
      }
      while ((n < m) && (paramArrayOfObject2[n] == null)) {
        n += 1;
      }
      int i1;
      label101:
      boolean bool1;
      if (i >= k)
      {
        j = 1;
        if (n < m) {
          break label121;
        }
        i1 = 1;
        if ((j == 0) || (i1 == 0)) {
          break label127;
        }
        bool1 = true;
      }
      label121:
      label127:
      do
      {
        do
        {
          return bool1;
          j = 0;
          break;
          i1 = 0;
          break label101;
          bool1 = bool2;
        } while (j != i1);
        bool1 = bool2;
      } while (!paramArrayOfObject1[i].equals(paramArrayOfObject2[n]));
      i += 1;
      j = n + 1;
    }
  }
  
  public static boolean equals(boolean[] paramArrayOfBoolean1, boolean[] paramArrayOfBoolean2)
  {
    if ((paramArrayOfBoolean1 == null) || (paramArrayOfBoolean1.length == 0)) {
      return (paramArrayOfBoolean2 == null) || (paramArrayOfBoolean2.length == 0);
    }
    return Arrays.equals(paramArrayOfBoolean1, paramArrayOfBoolean2);
  }
  
  public static boolean equals(byte[][] paramArrayOfByte1, byte[][] paramArrayOfByte2)
  {
    boolean bool2 = false;
    int i = 0;
    int k;
    int j;
    int m;
    if (paramArrayOfByte1 == null)
    {
      k = 0;
      j = 0;
      if (paramArrayOfByte2 != null) {
        break label53;
      }
      m = 0;
    }
    for (;;)
    {
      int n = j;
      if (i < k)
      {
        n = j;
        if (paramArrayOfByte1[i] == null)
        {
          i += 1;
          continue;
          k = paramArrayOfByte1.length;
          break;
          label53:
          m = paramArrayOfByte2.length;
          continue;
        }
      }
      while ((n < m) && (paramArrayOfByte2[n] == null)) {
        n += 1;
      }
      int i1;
      label101:
      boolean bool1;
      if (i >= k)
      {
        j = 1;
        if (n < m) {
          break label121;
        }
        i1 = 1;
        if ((j == 0) || (i1 == 0)) {
          break label127;
        }
        bool1 = true;
      }
      label121:
      label127:
      do
      {
        do
        {
          return bool1;
          j = 0;
          break;
          i1 = 0;
          break label101;
          bool1 = bool2;
        } while (j != i1);
        bool1 = bool2;
      } while (!Arrays.equals(paramArrayOfByte1[i], paramArrayOfByte2[n]));
      i += 1;
      j = n + 1;
    }
  }
  
  private static boolean equalsMapValue(Object paramObject1, Object paramObject2)
  {
    if ((paramObject1 == null) || (paramObject2 == null)) {
      throw new IllegalStateException("keys and values in maps cannot be null");
    }
    if (((paramObject1 instanceof byte[])) && ((paramObject2 instanceof byte[]))) {
      return Arrays.equals((byte[])paramObject1, (byte[])paramObject2);
    }
    return paramObject1.equals(paramObject2);
  }
  
  public static <K, V> int hashCode(Map<K, V> paramMap)
  {
    int j;
    if (paramMap == null)
    {
      j = 0;
      return j;
    }
    int i = 0;
    paramMap = paramMap.entrySet().iterator();
    for (;;)
    {
      j = i;
      if (!paramMap.hasNext()) {
        break;
      }
      Map.Entry localEntry = (Map.Entry)paramMap.next();
      i += (hashCodeForMap(localEntry.getKey()) ^ hashCodeForMap(localEntry.getValue()));
    }
  }
  
  public static int hashCode(double[] paramArrayOfDouble)
  {
    if ((paramArrayOfDouble == null) || (paramArrayOfDouble.length == 0)) {
      return 0;
    }
    return Arrays.hashCode(paramArrayOfDouble);
  }
  
  public static int hashCode(float[] paramArrayOfFloat)
  {
    if ((paramArrayOfFloat == null) || (paramArrayOfFloat.length == 0)) {
      return 0;
    }
    return Arrays.hashCode(paramArrayOfFloat);
  }
  
  public static int hashCode(int[] paramArrayOfInt)
  {
    if ((paramArrayOfInt == null) || (paramArrayOfInt.length == 0)) {
      return 0;
    }
    return Arrays.hashCode(paramArrayOfInt);
  }
  
  public static int hashCode(long[] paramArrayOfLong)
  {
    if ((paramArrayOfLong == null) || (paramArrayOfLong.length == 0)) {
      return 0;
    }
    return Arrays.hashCode(paramArrayOfLong);
  }
  
  public static int hashCode(Object[] paramArrayOfObject)
  {
    int k = 0;
    int j = 0;
    int i;
    if (paramArrayOfObject == null) {
      i = 0;
    }
    while (j < i)
    {
      Object localObject = paramArrayOfObject[j];
      int m = k;
      if (localObject != null) {
        m = k * 31 + localObject.hashCode();
      }
      j += 1;
      k = m;
      continue;
      i = paramArrayOfObject.length;
    }
    return k;
  }
  
  public static int hashCode(boolean[] paramArrayOfBoolean)
  {
    if ((paramArrayOfBoolean == null) || (paramArrayOfBoolean.length == 0)) {
      return 0;
    }
    return Arrays.hashCode(paramArrayOfBoolean);
  }
  
  public static int hashCode(byte[][] paramArrayOfByte)
  {
    int k = 0;
    int j = 0;
    int i;
    if (paramArrayOfByte == null) {
      i = 0;
    }
    while (j < i)
    {
      byte[] arrayOfByte = paramArrayOfByte[j];
      int m = k;
      if (arrayOfByte != null) {
        m = k * 31 + Arrays.hashCode(arrayOfByte);
      }
      j += 1;
      k = m;
      continue;
      i = paramArrayOfByte.length;
    }
    return k;
  }
  
  private static int hashCodeForMap(Object paramObject)
  {
    if ((paramObject instanceof byte[])) {
      return Arrays.hashCode((byte[])paramObject);
    }
    return paramObject.hashCode();
  }
  
  public static final <K, V> Map<K, V> mergeMapEntry(CodedInputByteBufferNano paramCodedInputByteBufferNano, Map<K, V> paramMap, MapFactories.MapFactory paramMapFactory, int paramInt1, int paramInt2, V paramV, int paramInt3, int paramInt4)
    throws IOException
  {
    paramMapFactory = paramMapFactory.forMap(paramMap);
    int i = paramCodedInputByteBufferNano.pushLimit(paramCodedInputByteBufferNano.readRawVarint32());
    paramMap = null;
    int j = paramCodedInputByteBufferNano.readTag();
    if (j == 0) {}
    for (;;)
    {
      paramCodedInputByteBufferNano.checkLastTagWas(0);
      paramCodedInputByteBufferNano.popLimit(i);
      paramCodedInputByteBufferNano = paramMap;
      if (paramMap == null) {
        paramCodedInputByteBufferNano = primitiveDefaultValue(paramInt1);
      }
      paramMap = paramV;
      if (paramV == null) {
        paramMap = primitiveDefaultValue(paramInt2);
      }
      paramMapFactory.put(paramCodedInputByteBufferNano, paramMap);
      return paramMapFactory;
      if (j == paramInt3)
      {
        paramMap = paramCodedInputByteBufferNano.readPrimitiveField(paramInt1);
        break;
      }
      if (j == paramInt4)
      {
        if (paramInt2 == 11)
        {
          paramCodedInputByteBufferNano.readMessage((MessageNano)paramV);
          break;
        }
        paramV = paramCodedInputByteBufferNano.readPrimitiveField(paramInt2);
        break;
      }
      if (paramCodedInputByteBufferNano.skipField(j)) {
        break;
      }
    }
  }
  
  private static Object primitiveDefaultValue(int paramInt)
  {
    switch (paramInt)
    {
    case 10: 
    case 11: 
    default: 
      throw new IllegalArgumentException("Type: " + paramInt + " is not a primitive type.");
    case 8: 
      return Boolean.FALSE;
    case 12: 
      return WireFormatNano.EMPTY_BYTES;
    case 9: 
      return "";
    case 2: 
      return Float.valueOf(0.0F);
    case 1: 
      return Double.valueOf(0.0D);
    case 5: 
    case 7: 
    case 13: 
    case 14: 
    case 15: 
    case 17: 
      return Integer.valueOf(0);
    }
    return Long.valueOf(0L);
  }
  
  public static <K, V> void serializeMapField(CodedOutputByteBufferNano paramCodedOutputByteBufferNano, Map<K, V> paramMap, int paramInt1, int paramInt2, int paramInt3)
    throws IOException
  {
    paramMap = paramMap.entrySet().iterator();
    while (paramMap.hasNext())
    {
      Object localObject2 = (Map.Entry)paramMap.next();
      Object localObject1 = ((Map.Entry)localObject2).getKey();
      localObject2 = ((Map.Entry)localObject2).getValue();
      if ((localObject1 == null) || (localObject2 == null)) {
        throw new IllegalStateException("keys and values in maps cannot be null");
      }
      int i = CodedOutputByteBufferNano.computeFieldSize(1, paramInt2, localObject1);
      int j = CodedOutputByteBufferNano.computeFieldSize(2, paramInt3, localObject2);
      paramCodedOutputByteBufferNano.writeTag(paramInt1, 2);
      paramCodedOutputByteBufferNano.writeRawVarint32(i + j);
      paramCodedOutputByteBufferNano.writeField(1, paramInt2, localObject1);
      paramCodedOutputByteBufferNano.writeField(2, paramInt3, localObject2);
    }
  }
  
  public static String stringDefaultValue(String paramString)
  {
    return new String(paramString.getBytes(ISO_8859_1), UTF_8);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/protobuf/nano/InternalNano.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */