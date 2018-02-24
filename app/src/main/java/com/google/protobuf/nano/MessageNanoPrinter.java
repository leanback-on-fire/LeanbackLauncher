package com.google.protobuf.nano;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public final class MessageNanoPrinter
{
  private static final String INDENT = "  ";
  private static final int MAX_STRING_LEN = 200;
  
  private static void appendQuotedBytes(byte[] paramArrayOfByte, StringBuffer paramStringBuffer)
  {
    if (paramArrayOfByte == null)
    {
      paramStringBuffer.append("\"\"");
      return;
    }
    paramStringBuffer.append('"');
    int i = 0;
    if (i < paramArrayOfByte.length)
    {
      int j = paramArrayOfByte[i] & 0xFF;
      if ((j == 92) || (j == 34)) {
        paramStringBuffer.append('\\').append((char)j);
      }
      for (;;)
      {
        i += 1;
        break;
        if ((j >= 32) && (j < 127)) {
          paramStringBuffer.append((char)j);
        } else {
          paramStringBuffer.append(String.format("\\%03o", new Object[] { Integer.valueOf(j) }));
        }
      }
    }
    paramStringBuffer.append('"');
  }
  
  private static String deCamelCaseify(String paramString)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    int i = 0;
    if (i < paramString.length())
    {
      char c = paramString.charAt(i);
      if (i == 0) {
        localStringBuffer.append(Character.toLowerCase(c));
      }
      for (;;)
      {
        i += 1;
        break;
        if (Character.isUpperCase(c)) {
          localStringBuffer.append('_').append(Character.toLowerCase(c));
        } else {
          localStringBuffer.append(c);
        }
      }
    }
    return localStringBuffer.toString();
  }
  
  private static String escapeString(String paramString)
  {
    int j = paramString.length();
    StringBuilder localStringBuilder = new StringBuilder(j);
    int i = 0;
    if (i < j)
    {
      char c = paramString.charAt(i);
      if ((c >= ' ') && (c <= '~') && (c != '"') && (c != '\'')) {
        localStringBuilder.append(c);
      }
      for (;;)
      {
        i += 1;
        break;
        localStringBuilder.append(String.format("\\u%04x", new Object[] { Integer.valueOf(c) }));
      }
    }
    return localStringBuilder.toString();
  }
  
  public static <T extends MessageNano> String print(T paramT)
  {
    if (paramT == null) {
      return "";
    }
    StringBuffer localStringBuffer = new StringBuffer();
    try
    {
      print(null, paramT, new StringBuffer(), localStringBuffer);
      return localStringBuffer.toString();
    }
    catch (IllegalAccessException paramT)
    {
      return "Error printing proto: " + paramT.getMessage();
    }
    catch (InvocationTargetException paramT) {}
    return "Error printing proto: " + paramT.getMessage();
  }
  
  private static void print(String paramString, Object paramObject, StringBuffer paramStringBuffer1, StringBuffer paramStringBuffer2)
    throws IllegalAccessException, InvocationTargetException
  {
    if (paramObject == null) {}
    for (;;)
    {
      return;
      Object localObject1;
      int i;
      if ((paramObject instanceof MessageNano))
      {
        int m = paramStringBuffer1.length();
        if (paramString != null)
        {
          paramStringBuffer2.append(paramStringBuffer1).append(deCamelCaseify(paramString)).append(" <\n");
          paramStringBuffer1.append("  ");
        }
        localObject1 = paramObject.getClass();
        Object localObject2 = ((Class)localObject1).getFields();
        int n = localObject2.length;
        i = 0;
        String str;
        Object localObject3;
        if (i < n)
        {
          Object localObject4 = localObject2[i];
          j = ((Field)localObject4).getModifiers();
          str = ((Field)localObject4).getName();
          if ("cachedSize".equals(str)) {}
          for (;;)
          {
            i += 1;
            break;
            if (((j & 0x1) == 1) && ((j & 0x8) != 8) && (!str.startsWith("_")) && (!str.endsWith("_")))
            {
              localObject3 = ((Field)localObject4).getType();
              localObject4 = ((Field)localObject4).get(paramObject);
              if (((Class)localObject3).isArray())
              {
                if (((Class)localObject3).getComponentType() == Byte.TYPE)
                {
                  print(str, localObject4, paramStringBuffer1, paramStringBuffer2);
                }
                else
                {
                  if (localObject4 == null) {}
                  for (j = 0;; j = Array.getLength(localObject4))
                  {
                    int k = 0;
                    while (k < j)
                    {
                      print(str, Array.get(localObject4, k), paramStringBuffer1, paramStringBuffer2);
                      k += 1;
                    }
                    break;
                  }
                }
              }
              else {
                print(str, localObject4, paramStringBuffer1, paramStringBuffer2);
              }
            }
          }
        }
        localObject2 = ((Class)localObject1).getMethods();
        int j = localObject2.length;
        i = 0;
        for (;;)
        {
          if (i >= j) {
            break label432;
          }
          str = localObject2[i].getName();
          if (str.startsWith("set")) {
            str = str.substring(3);
          }
          try
          {
            localObject3 = ((Class)localObject1).getMethod("has" + str, new Class[0]);
            if (((Boolean)((Method)localObject3).invoke(paramObject, new Object[0])).booleanValue()) {
              break;
            }
          }
          catch (NoSuchMethodException localNoSuchMethodException1)
          {
            for (;;)
            {
              continue;
              try
              {
                localObject3 = ((Class)localObject1).getMethod("get" + localNoSuchMethodException1, new Class[0]);
                print(localNoSuchMethodException1, ((Method)localObject3).invoke(paramObject, new Object[0]), paramStringBuffer1, paramStringBuffer2);
              }
              catch (NoSuchMethodException localNoSuchMethodException2) {}
            }
          }
          i += 1;
        }
        label432:
        if (paramString != null)
        {
          paramStringBuffer1.setLength(m);
          paramStringBuffer2.append(paramStringBuffer1).append(">\n");
        }
      }
      else
      {
        if (!(paramObject instanceof Map)) {
          break;
        }
        paramObject = (Map)paramObject;
        paramString = deCamelCaseify(paramString);
        paramObject = ((Map)paramObject).entrySet().iterator();
        while (((Iterator)paramObject).hasNext())
        {
          localObject1 = (Map.Entry)((Iterator)paramObject).next();
          paramStringBuffer2.append(paramStringBuffer1).append(paramString).append(" <\n");
          i = paramStringBuffer1.length();
          paramStringBuffer1.append("  ");
          print("key", ((Map.Entry)localObject1).getKey(), paramStringBuffer1, paramStringBuffer2);
          print("value", ((Map.Entry)localObject1).getValue(), paramStringBuffer1, paramStringBuffer2);
          paramStringBuffer1.setLength(i);
          paramStringBuffer2.append(paramStringBuffer1).append(">\n");
        }
      }
    }
    paramString = deCamelCaseify(paramString);
    paramStringBuffer2.append(paramStringBuffer1).append(paramString).append(": ");
    if ((paramObject instanceof String))
    {
      paramString = sanitizeString((String)paramObject);
      paramStringBuffer2.append("\"").append(paramString).append("\"");
    }
    for (;;)
    {
      paramStringBuffer2.append("\n");
      return;
      if ((paramObject instanceof byte[])) {
        appendQuotedBytes((byte[])paramObject, paramStringBuffer2);
      } else {
        paramStringBuffer2.append(paramObject);
      }
    }
  }
  
  private static String sanitizeString(String paramString)
  {
    String str = paramString;
    if (!paramString.startsWith("http"))
    {
      str = paramString;
      if (paramString.length() > 200) {
        str = paramString.substring(0, 200) + "[...]";
      }
    }
    return escapeString(str);
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/protobuf/nano/MessageNanoPrinter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */