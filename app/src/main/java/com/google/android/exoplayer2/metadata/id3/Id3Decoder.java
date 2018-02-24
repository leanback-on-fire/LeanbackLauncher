package com.google.android.exoplayer2.metadata.id3;

import android.util.Log;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.metadata.MetadataDecoder;
import com.google.android.exoplayer2.metadata.MetadataInputBuffer;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.Util;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public final class Id3Decoder
  implements MetadataDecoder
{
  public static final int ID3_HEADER_LENGTH = 10;
  public static final int ID3_TAG = Util.getIntegerCodeForString("ID3");
  private static final int ID3_TEXT_ENCODING_ISO_8859_1 = 0;
  private static final int ID3_TEXT_ENCODING_UTF_16 = 1;
  private static final int ID3_TEXT_ENCODING_UTF_16BE = 2;
  private static final int ID3_TEXT_ENCODING_UTF_8 = 3;
  private static final String TAG = "Id3Decoder";
  
  private static ApicFrame decodeApicFrame(ParsableByteArray paramParsableByteArray, int paramInt1, int paramInt2)
    throws UnsupportedEncodingException
  {
    int i = paramParsableByteArray.readUnsignedByte();
    String str2 = getCharsetName(i);
    byte[] arrayOfByte = new byte[paramInt1 - 1];
    paramParsableByteArray.readBytes(arrayOfByte, 0, paramInt1 - 1);
    String str1;
    if (paramInt2 == 2)
    {
      paramInt2 = 2;
      str1 = "image/" + Util.toLowerInvariant(new String(arrayOfByte, 0, 3, "ISO-8859-1"));
      paramParsableByteArray = str1;
      paramInt1 = paramInt2;
      if (str1.equals("image/jpg"))
      {
        paramParsableByteArray = "image/jpeg";
        paramInt1 = paramInt2;
      }
    }
    for (;;)
    {
      paramInt2 = arrayOfByte[(paramInt1 + 1)];
      paramInt1 += 2;
      int j = indexOfEos(arrayOfByte, paramInt1, i);
      return new ApicFrame(paramParsableByteArray, new String(arrayOfByte, paramInt1, j - paramInt1, str2), paramInt2 & 0xFF, Arrays.copyOfRange(arrayOfByte, j + delimiterLength(i), arrayOfByte.length));
      paramInt2 = indexOfZeroByte(arrayOfByte, 0);
      str1 = Util.toLowerInvariant(new String(arrayOfByte, 0, paramInt2, "ISO-8859-1"));
      paramParsableByteArray = str1;
      paramInt1 = paramInt2;
      if (str1.indexOf('/') == -1)
      {
        paramParsableByteArray = "image/" + str1;
        paramInt1 = paramInt2;
      }
    }
  }
  
  private static BinaryFrame decodeBinaryFrame(ParsableByteArray paramParsableByteArray, int paramInt, String paramString)
  {
    byte[] arrayOfByte = new byte[paramInt];
    paramParsableByteArray.readBytes(arrayOfByte, 0, paramInt);
    return new BinaryFrame(paramString, arrayOfByte);
  }
  
  private static ChapterFrame decodeChapterFrame(ParsableByteArray paramParsableByteArray, int paramInt1, int paramInt2, boolean paramBoolean, int paramInt3)
    throws UnsupportedEncodingException
  {
    int i = paramParsableByteArray.getPosition();
    int j = indexOfZeroByte(paramParsableByteArray.data, i);
    String str = new String(paramParsableByteArray.data, i, j - i, "ISO-8859-1");
    paramParsableByteArray.setPosition(j + 1);
    j = paramParsableByteArray.readInt();
    int k = paramParsableByteArray.readInt();
    long l2 = paramParsableByteArray.readUnsignedInt();
    long l1 = l2;
    if (l2 == 4294967295L) {
      l1 = -1L;
    }
    long l3 = paramParsableByteArray.readUnsignedInt();
    l2 = l3;
    if (l3 == 4294967295L) {
      l2 = -1L;
    }
    ArrayList localArrayList = new ArrayList();
    while (paramParsableByteArray.getPosition() < i + paramInt1)
    {
      Id3Frame localId3Frame = decodeFrame(paramInt2, paramParsableByteArray, paramBoolean, paramInt3);
      if (localId3Frame != null) {
        localArrayList.add(localId3Frame);
      }
    }
    paramParsableByteArray = new Id3Frame[localArrayList.size()];
    localArrayList.toArray(paramParsableByteArray);
    return new ChapterFrame(str, j, k, l1, l2, paramParsableByteArray);
  }
  
  private static ChapterTocFrame decodeChapterTOCFrame(ParsableByteArray paramParsableByteArray, int paramInt1, int paramInt2, boolean paramBoolean, int paramInt3)
    throws UnsupportedEncodingException
  {
    int j = paramParsableByteArray.getPosition();
    int i = indexOfZeroByte(paramParsableByteArray.data, j);
    String str = new String(paramParsableByteArray.data, j, i - j, "ISO-8859-1");
    paramParsableByteArray.setPosition(i + 1);
    i = paramParsableByteArray.readUnsignedByte();
    boolean bool1;
    if ((i & 0x2) != 0)
    {
      bool1 = true;
      if ((i & 0x1) == 0) {
        break label161;
      }
    }
    String[] arrayOfString;
    label161:
    for (boolean bool2 = true;; bool2 = false)
    {
      int k = paramParsableByteArray.readUnsignedByte();
      arrayOfString = new String[k];
      i = 0;
      while (i < k)
      {
        int m = paramParsableByteArray.getPosition();
        int n = indexOfZeroByte(paramParsableByteArray.data, m);
        arrayOfString[i] = new String(paramParsableByteArray.data, m, n - m, "ISO-8859-1");
        paramParsableByteArray.setPosition(n + 1);
        i += 1;
      }
      bool1 = false;
      break;
    }
    ArrayList localArrayList = new ArrayList();
    while (paramParsableByteArray.getPosition() < j + paramInt1)
    {
      Id3Frame localId3Frame = decodeFrame(paramInt2, paramParsableByteArray, paramBoolean, paramInt3);
      if (localId3Frame != null) {
        localArrayList.add(localId3Frame);
      }
    }
    paramParsableByteArray = new Id3Frame[localArrayList.size()];
    localArrayList.toArray(paramParsableByteArray);
    return new ChapterTocFrame(str, bool1, bool2, arrayOfString, paramParsableByteArray);
  }
  
  private static CommentFrame decodeCommentFrame(ParsableByteArray paramParsableByteArray, int paramInt)
    throws UnsupportedEncodingException
  {
    int i = paramParsableByteArray.readUnsignedByte();
    String str2 = getCharsetName(i);
    Object localObject = new byte[3];
    paramParsableByteArray.readBytes((byte[])localObject, 0, 3);
    localObject = new String((byte[])localObject, 0, 3);
    byte[] arrayOfByte = new byte[paramInt - 4];
    paramParsableByteArray.readBytes(arrayOfByte, 0, paramInt - 4);
    paramInt = indexOfEos(arrayOfByte, 0, i);
    String str1 = new String(arrayOfByte, 0, paramInt, str2);
    paramInt += delimiterLength(i);
    if (paramInt < arrayOfByte.length) {}
    for (paramParsableByteArray = new String(arrayOfByte, paramInt, indexOfEos(arrayOfByte, paramInt, i) - paramInt, str2);; paramParsableByteArray = "") {
      return new CommentFrame((String)localObject, str1, paramParsableByteArray);
    }
  }
  
  private static Id3Frame decodeFrame(int paramInt1, ParsableByteArray paramParsableByteArray, boolean paramBoolean, int paramInt2)
  {
    int i4 = paramParsableByteArray.readUnsignedByte();
    int i5 = paramParsableByteArray.readUnsignedByte();
    int i6 = paramParsableByteArray.readUnsignedByte();
    int i2;
    if (paramInt1 >= 3)
    {
      i2 = paramParsableByteArray.readUnsignedByte();
      if (paramInt1 != 4) {
        break label152;
      }
      j = paramParsableByteArray.readUnsignedIntToInt();
      i = j;
      if (!paramBoolean) {
        i = j & 0xFF | (j >> 8 & 0xFF) << 7 | (j >> 16 & 0xFF) << 14 | (j >> 24 & 0xFF) << 21;
      }
      label95:
      if (paramInt1 < 3) {
        break label175;
      }
    }
    label152:
    label175:
    for (int i3 = paramParsableByteArray.readUnsignedShort();; i3 = 0)
    {
      if ((i4 != 0) || (i5 != 0) || (i6 != 0) || (i2 != 0) || (i != 0) || (i3 != 0)) {
        break label181;
      }
      paramParsableByteArray.setPosition(paramParsableByteArray.limit());
      return null;
      i2 = 0;
      break;
      if (paramInt1 == 3)
      {
        i = paramParsableByteArray.readUnsignedIntToInt();
        break label95;
      }
      i = paramParsableByteArray.readUnsignedInt24();
      break label95;
    }
    label181:
    int i7 = paramParsableByteArray.getPosition() + i;
    if (i7 > paramParsableByteArray.limit())
    {
      Log.w("Id3Decoder", "Frame size exceeds remaining tag data");
      paramParsableByteArray.setPosition(paramParsableByteArray.limit());
      return null;
    }
    int m = 0;
    int n = 0;
    int i1 = 0;
    int j = 0;
    int k = 0;
    if (paramInt1 == 3) {
      if ((i3 & 0x80) != 0)
      {
        j = 1;
        if ((i3 & 0x40) == 0) {
          break label315;
        }
        n = 1;
        label260:
        if ((i3 & 0x20) == 0) {
          break label321;
        }
        k = 1;
        label271:
        i3 = j;
        m = j;
        j = i3;
      }
    }
    for (;;)
    {
      if ((m != 0) || (n != 0))
      {
        Log.w("Id3Decoder", "Skipping unsupported compressed or encrypted frame");
        paramParsableByteArray.setPosition(i7);
        return null;
        j = 0;
        break;
        label315:
        n = 0;
        break label260;
        label321:
        k = 0;
        break label271;
        if (paramInt1 == 4)
        {
          if ((i3 & 0x40) != 0)
          {
            k = 1;
            label343:
            if ((i3 & 0x8) == 0) {
              break label393;
            }
            m = 1;
            label354:
            if ((i3 & 0x4) == 0) {
              break label399;
            }
            n = 1;
            label364:
            if ((i3 & 0x2) == 0) {
              break label405;
            }
            i1 = 1;
            label374:
            if ((i3 & 0x1) == 0) {
              break label411;
            }
          }
          label393:
          label399:
          label405:
          label411:
          for (j = 1;; j = 0)
          {
            break;
            k = 0;
            break label343;
            m = 0;
            break label354;
            n = 0;
            break label364;
            i1 = 0;
            break label374;
          }
        }
      }
    }
    m = i;
    if (k != 0)
    {
      m = i - 1;
      paramParsableByteArray.skipBytes(1);
    }
    int i = m;
    if (j != 0)
    {
      i = m - 4;
      paramParsableByteArray.skipBytes(4);
    }
    j = i;
    if (i1 != 0) {
      j = removeUnsynchronization(paramParsableByteArray, i);
    }
    if ((i4 == 84) && (i5 == 88) && (i6 == 88) && ((paramInt1 == 2) || (i2 == 88))) {}
    for (;;)
    {
      try
      {
        localObject1 = decodeTxxxFrame(paramParsableByteArray, j);
        return (Id3Frame)localObject1;
      }
      catch (UnsupportedEncodingException localUnsupportedEncodingException)
      {
        Object localObject1;
        label636:
        label647:
        label798:
        label809:
        Log.w("Id3Decoder", "Unsupported character encoding");
        return null;
      }
      finally
      {
        paramParsableByteArray.setPosition(i7);
      }
      if (i4 != 84) {
        break;
      }
      if (paramInt1 == 2)
      {
        localObject1 = String.format(Locale.US, "%c%c%c", new Object[] { Integer.valueOf(i4), Integer.valueOf(i5), Integer.valueOf(i6) });
        localObject1 = decodeTextInformationFrame(paramParsableByteArray, j, (String)localObject1);
      }
      else
      {
        localObject1 = String.format(Locale.US, "%c%c%c%c", new Object[] { Integer.valueOf(i4), Integer.valueOf(i5), Integer.valueOf(i6), Integer.valueOf(i2) });
        continue;
        localObject1 = decodeWxxxFrame(paramParsableByteArray, j);
        continue;
        if (i4 == 87)
        {
          if (paramInt1 == 2)
          {
            localObject1 = String.format(Locale.US, "%c%c%c", new Object[] { Integer.valueOf(i4), Integer.valueOf(i5), Integer.valueOf(i6) });
            localObject1 = decodeUrlLinkFrame(paramParsableByteArray, j, (String)localObject1);
          }
          else
          {
            localObject1 = String.format(Locale.US, "%c%c%c%c", new Object[] { Integer.valueOf(i4), Integer.valueOf(i5), Integer.valueOf(i6), Integer.valueOf(i2) });
          }
        }
        else
        {
          if ((i4 != 80) || (i5 != 82) || (i6 != 73) || (i2 != 86)) {
            break label1087;
          }
          localObject1 = decodePrivFrame(paramParsableByteArray, j);
          continue;
          localObject1 = decodeGeobFrame(paramParsableByteArray, j);
          continue;
          localObject1 = decodeApicFrame(paramParsableByteArray, j, paramInt1);
        }
      }
    }
    for (;;)
    {
      label821:
      localObject1 = decodeCommentFrame(paramParsableByteArray, j);
      break;
      label1087:
      do
      {
        do
        {
          if ((i4 == 67) && (i5 == 72) && (i6 == 65) && (i2 == 80))
          {
            localObject1 = decodeChapterFrame(paramParsableByteArray, j, paramInt1, paramBoolean, paramInt2);
            break;
          }
          if ((i4 == 67) && (i5 == 84) && (i6 == 79) && (i2 == 67))
          {
            localObject1 = decodeChapterTOCFrame(paramParsableByteArray, j, paramInt1, paramBoolean, paramInt2);
            break;
          }
          if (paramInt1 == 2) {}
          for (localObject1 = String.format(Locale.US, "%c%c%c", new Object[] { Integer.valueOf(i4), Integer.valueOf(i5), Integer.valueOf(i6) });; localObject1 = String.format(Locale.US, "%c%c%c%c", new Object[] { Integer.valueOf(i4), Integer.valueOf(i5), Integer.valueOf(i6), Integer.valueOf(i2) }))
          {
            localObject1 = decodeBinaryFrame(paramParsableByteArray, j, (String)localObject1);
            break;
          }
          if ((i4 != 87) || (i5 != 88) || (i6 != 88)) {
            break label647;
          }
          if (paramInt1 == 2) {
            break label636;
          }
          if (i2 != 88) {
            break label647;
          }
          break label636;
          if ((i4 == 71) && (i5 == 69) && (i6 == 79) && ((i2 == 66) || (paramInt1 == 2))) {
            break label798;
          }
          if (paramInt1 == 2 ? (i4 == 80) || (i5 == 73) || (i6 == 67) : (i4 == 65) && (i5 == 80) && (i6 == 73) && (i2 == 67)) {
            break label809;
          }
        } while ((i4 != 67) || (i5 != 79) || (i6 != 77));
        if (i2 == 77) {
          break label821;
        }
      } while (paramInt1 != 2);
    }
  }
  
  private static GeobFrame decodeGeobFrame(ParsableByteArray paramParsableByteArray, int paramInt)
    throws UnsupportedEncodingException
  {
    int i = paramParsableByteArray.readUnsignedByte();
    String str1 = getCharsetName(i);
    byte[] arrayOfByte = new byte[paramInt - 1];
    paramParsableByteArray.readBytes(arrayOfByte, 0, paramInt - 1);
    paramInt = indexOfZeroByte(arrayOfByte, 0);
    paramParsableByteArray = new String(arrayOfByte, 0, paramInt, "ISO-8859-1");
    paramInt += 1;
    int j = indexOfEos(arrayOfByte, paramInt, i);
    String str2 = new String(arrayOfByte, paramInt, j - paramInt, str1);
    paramInt = j + delimiterLength(i);
    j = indexOfEos(arrayOfByte, paramInt, i);
    return new GeobFrame(paramParsableByteArray, str2, new String(arrayOfByte, paramInt, j - paramInt, str1), Arrays.copyOfRange(arrayOfByte, j + delimiterLength(i), arrayOfByte.length));
  }
  
  private static Id3Header decodeHeader(ParsableByteArray paramParsableByteArray)
  {
    boolean bool = true;
    if (paramParsableByteArray.bytesLeft() < 10)
    {
      Log.w("Id3Decoder", "Data too short to be an ID3 tag");
      return null;
    }
    int i = paramParsableByteArray.readUnsignedInt24();
    if (i != ID3_TAG)
    {
      Log.w("Id3Decoder", "Unexpected first three bytes of ID3 tag header: " + i);
      return null;
    }
    int m = paramParsableByteArray.readUnsignedByte();
    paramParsableByteArray.skipBytes(1);
    int n = paramParsableByteArray.readUnsignedByte();
    int k = paramParsableByteArray.readSynchSafeInt();
    int j;
    if (m == 2)
    {
      if ((n & 0x40) != 0) {}
      for (j = 1;; j = 0)
      {
        i = k;
        if (j == 0) {
          break;
        }
        Log.w("Id3Decoder", "Skipped ID3 tag with majorVersion=2 and undefined compression scheme");
        return null;
      }
    }
    if (m == 3) {
      if ((n & 0x40) != 0)
      {
        j = 1;
        i = k;
        if (j != 0)
        {
          i = paramParsableByteArray.readInt();
          paramParsableByteArray.skipBytes(i);
          i = k - (i + 4);
        }
        if ((m >= 4) || ((n & 0x80) == 0)) {
          break label295;
        }
      }
    }
    for (;;)
    {
      return new Id3Header(m, bool, i);
      j = 0;
      break;
      if (m == 4)
      {
        if ((n & 0x40) != 0)
        {
          i = 1;
          label210:
          j = k;
          if (i != 0)
          {
            i = paramParsableByteArray.readSynchSafeInt();
            paramParsableByteArray.skipBytes(i - 4);
            j = k - i;
          }
          if ((n & 0x10) == 0) {
            break label261;
          }
        }
        label261:
        for (k = 1;; k = 0)
        {
          i = j;
          if (k == 0) {
            break;
          }
          i = j - 10;
          break;
          i = 0;
          break label210;
        }
      }
      Log.w("Id3Decoder", "Skipped ID3 tag with unsupported majorVersion=" + m);
      return null;
      label295:
      bool = false;
    }
  }
  
  private static PrivFrame decodePrivFrame(ParsableByteArray paramParsableByteArray, int paramInt)
    throws UnsupportedEncodingException
  {
    byte[] arrayOfByte = new byte[paramInt];
    paramParsableByteArray.readBytes(arrayOfByte, 0, paramInt);
    paramInt = indexOfZeroByte(arrayOfByte, 0);
    return new PrivFrame(new String(arrayOfByte, 0, paramInt, "ISO-8859-1"), Arrays.copyOfRange(arrayOfByte, paramInt + 1, arrayOfByte.length));
  }
  
  private static TextInformationFrame decodeTextInformationFrame(ParsableByteArray paramParsableByteArray, int paramInt, String paramString)
    throws UnsupportedEncodingException
  {
    if (paramInt <= 1) {
      return new TextInformationFrame(paramString, null, "");
    }
    int i = paramParsableByteArray.readUnsignedByte();
    String str = getCharsetName(i);
    byte[] arrayOfByte = new byte[paramInt - 1];
    paramParsableByteArray.readBytes(arrayOfByte, 0, paramInt - 1);
    return new TextInformationFrame(paramString, null, new String(arrayOfByte, 0, indexOfEos(arrayOfByte, 0, i), str));
  }
  
  private static TextInformationFrame decodeTxxxFrame(ParsableByteArray paramParsableByteArray, int paramInt)
    throws UnsupportedEncodingException
  {
    int i = paramParsableByteArray.readUnsignedByte();
    String str2 = getCharsetName(i);
    byte[] arrayOfByte = new byte[paramInt - 1];
    paramParsableByteArray.readBytes(arrayOfByte, 0, paramInt - 1);
    paramInt = indexOfEos(arrayOfByte, 0, i);
    String str1 = new String(arrayOfByte, 0, paramInt, str2);
    paramInt += delimiterLength(i);
    if (paramInt < arrayOfByte.length) {}
    for (paramParsableByteArray = new String(arrayOfByte, paramInt, indexOfEos(arrayOfByte, paramInt, i) - paramInt, str2);; paramParsableByteArray = "") {
      return new TextInformationFrame("TXXX", str1, paramParsableByteArray);
    }
  }
  
  private static UrlLinkFrame decodeUrlLinkFrame(ParsableByteArray paramParsableByteArray, int paramInt, String paramString)
    throws UnsupportedEncodingException
  {
    if (paramInt == 0) {
      return new UrlLinkFrame(paramString, null, "");
    }
    byte[] arrayOfByte = new byte[paramInt];
    paramParsableByteArray.readBytes(arrayOfByte, 0, paramInt);
    return new UrlLinkFrame(paramString, null, new String(arrayOfByte, 0, indexOfZeroByte(arrayOfByte, 0), "ISO-8859-1"));
  }
  
  private static UrlLinkFrame decodeWxxxFrame(ParsableByteArray paramParsableByteArray, int paramInt)
    throws UnsupportedEncodingException
  {
    int i = paramParsableByteArray.readUnsignedByte();
    String str = getCharsetName(i);
    byte[] arrayOfByte = new byte[paramInt - 1];
    paramParsableByteArray.readBytes(arrayOfByte, 0, paramInt - 1);
    paramInt = indexOfEos(arrayOfByte, 0, i);
    str = new String(arrayOfByte, 0, paramInt, str);
    paramInt += delimiterLength(i);
    if (paramInt < arrayOfByte.length) {}
    for (paramParsableByteArray = new String(arrayOfByte, paramInt, indexOfZeroByte(arrayOfByte, paramInt) - paramInt, "ISO-8859-1");; paramParsableByteArray = "") {
      return new UrlLinkFrame("WXXX", str, paramParsableByteArray);
    }
  }
  
  private static int delimiterLength(int paramInt)
  {
    if ((paramInt == 0) || (paramInt == 3)) {
      return 1;
    }
    return 2;
  }
  
  private static String getCharsetName(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return "ISO-8859-1";
    case 0: 
      return "ISO-8859-1";
    case 1: 
      return "UTF-16";
    case 2: 
      return "UTF-16BE";
    }
    return "UTF-8";
  }
  
  private static int indexOfEos(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    int i = indexOfZeroByte(paramArrayOfByte, paramInt1);
    if (paramInt2 != 0)
    {
      paramInt1 = i;
      if (paramInt2 != 3) {}
    }
    else
    {
      return i;
    }
    do
    {
      paramInt1 = indexOfZeroByte(paramArrayOfByte, paramInt1 + 1);
      if (paramInt1 >= paramArrayOfByte.length - 1) {
        break;
      }
    } while ((paramInt1 % 2 != 0) || (paramArrayOfByte[(paramInt1 + 1)] != 0));
    return paramInt1;
    return paramArrayOfByte.length;
  }
  
  private static int indexOfZeroByte(byte[] paramArrayOfByte, int paramInt)
  {
    while (paramInt < paramArrayOfByte.length)
    {
      if (paramArrayOfByte[paramInt] == 0) {
        return paramInt;
      }
      paramInt += 1;
    }
    return paramArrayOfByte.length;
  }
  
  private static int removeUnsynchronization(ParsableByteArray paramParsableByteArray, int paramInt)
  {
    byte[] arrayOfByte = paramParsableByteArray.data;
    int j = paramParsableByteArray.getPosition();
    int i = paramInt;
    paramInt = j;
    while (paramInt + 1 < i)
    {
      j = i;
      if ((arrayOfByte[paramInt] & 0xFF) == 255)
      {
        j = i;
        if (arrayOfByte[(paramInt + 1)] == 0)
        {
          System.arraycopy(arrayOfByte, paramInt + 2, arrayOfByte, paramInt + 1, i - paramInt - 2);
          j = i - 1;
        }
      }
      paramInt += 1;
      i = j;
    }
    return i;
  }
  
  private static boolean validateV4Frames(ParsableByteArray paramParsableByteArray, boolean paramBoolean)
  {
    int m = paramParsableByteArray.getPosition();
    try
    {
      while (paramParsableByteArray.bytesLeft() >= 10)
      {
        int j = paramParsableByteArray.readInt();
        int i = paramParsableByteArray.readUnsignedIntToInt();
        int n = paramParsableByteArray.readUnsignedShort();
        if ((j == 0) && (i == 0) && (n == 0)) {
          return true;
        }
        j = i;
        if (!paramBoolean)
        {
          if ((i & 0x808080) != 0L) {
            return false;
          }
          j = i & 0xFF | (i >> 8 & 0xFF) << 7 | (i >> 16 & 0xFF) << 14 | (i >> 24 & 0xFF) << 21;
        }
        i = 0;
        if ((n & 0x40) != 0) {
          i = 0 + 1;
        }
        int k = i;
        if ((n & 0x1) != 0) {
          k = i + 4;
        }
        if (j < k) {
          return false;
        }
        i = paramParsableByteArray.bytesLeft();
        if (i < j) {
          return false;
        }
        paramParsableByteArray.skipBytes(j);
      }
    }
    finally
    {
      paramParsableByteArray.setPosition(m);
    }
    return true;
  }
  
  public Metadata decode(MetadataInputBuffer paramMetadataInputBuffer)
  {
    paramMetadataInputBuffer = paramMetadataInputBuffer.data;
    return decode(paramMetadataInputBuffer.array(), paramMetadataInputBuffer.limit());
  }
  
  public Metadata decode(byte[] paramArrayOfByte, int paramInt)
  {
    ArrayList localArrayList = new ArrayList();
    paramArrayOfByte = new ParsableByteArray(paramArrayOfByte, paramInt);
    Id3Header localId3Header = decodeHeader(paramArrayOfByte);
    if (localId3Header == null) {
      return null;
    }
    int i = paramArrayOfByte.getPosition();
    paramInt = localId3Header.framesSize;
    if (localId3Header.isUnsynchronized) {
      paramInt = removeUnsynchronization(paramArrayOfByte, localId3Header.framesSize);
    }
    paramArrayOfByte.setLimit(i + paramInt);
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (localId3Header.majorVersion == 4)
    {
      bool1 = bool2;
      if (!validateV4Frames(paramArrayOfByte, false))
      {
        if (!validateV4Frames(paramArrayOfByte, true)) {
          break label159;
        }
        bool1 = true;
      }
    }
    if (localId3Header.majorVersion == 2) {
      paramInt = 6;
    }
    while (paramArrayOfByte.bytesLeft() >= paramInt)
    {
      Id3Frame localId3Frame = decodeFrame(localId3Header.majorVersion, paramArrayOfByte, bool1, paramInt);
      if (localId3Frame != null)
      {
        localArrayList.add(localId3Frame);
        continue;
        label159:
        Log.w("Id3Decoder", "Failed to validate V4 ID3 tag");
        return null;
        paramInt = 10;
      }
    }
    return new Metadata(localArrayList);
  }
  
  private static final class Id3Header
  {
    private final int framesSize;
    private final boolean isUnsynchronized;
    private final int majorVersion;
    
    public Id3Header(int paramInt1, boolean paramBoolean, int paramInt2)
    {
      this.majorVersion = paramInt1;
      this.isUnsynchronized = paramBoolean;
      this.framesSize = paramInt2;
    }
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/metadata/id3/Id3Decoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */