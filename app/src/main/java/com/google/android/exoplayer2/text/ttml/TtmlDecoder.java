package com.google.android.exoplayer2.text.ttml;

import android.text.Layout.Alignment;
import android.util.Log;
import android.util.Pair;
import com.google.android.exoplayer2.text.SimpleSubtitleDecoder;
import com.google.android.exoplayer2.text.SubtitleDecoderException;
import com.google.android.exoplayer2.util.ColorParser;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.util.XmlPullParserUtil;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public final class TtmlDecoder
  extends SimpleSubtitleDecoder
{
  private static final String ATTR_BEGIN = "begin";
  private static final String ATTR_DURATION = "dur";
  private static final String ATTR_END = "end";
  private static final String ATTR_REGION = "region";
  private static final String ATTR_STYLE = "style";
  private static final Pattern CLOCK_TIME = Pattern.compile("^([0-9][0-9]+):([0-9][0-9]):([0-9][0-9])(?:(\\.[0-9]+)|:([0-9][0-9])(?:\\.([0-9]+))?)?$");
  private static final FrameAndTickRate DEFAULT_FRAME_AND_TICK_RATE = new FrameAndTickRate(30.0F, 1, 1);
  private static final int DEFAULT_FRAME_RATE = 30;
  private static final Pattern FONT_SIZE;
  private static final Pattern OFFSET_TIME = Pattern.compile("^([0-9]+(?:\\.[0-9]+)?)(h|m|s|ms|f|t)$");
  private static final Pattern PERCENTAGE_COORDINATES;
  private static final String TAG = "TtmlDecoder";
  private static final String TTP = "http://www.w3.org/ns/ttml#parameter";
  private final XmlPullParserFactory xmlParserFactory;
  
  static
  {
    FONT_SIZE = Pattern.compile("^(([0-9]*.)?[0-9]+)(px|em|%)$");
    PERCENTAGE_COORDINATES = Pattern.compile("^(\\d+\\.?\\d*?)% (\\d+\\.?\\d*?)%$");
  }
  
  public TtmlDecoder()
  {
    super("TtmlDecoder");
    try
    {
      this.xmlParserFactory = XmlPullParserFactory.newInstance();
      this.xmlParserFactory.setNamespaceAware(true);
      return;
    }
    catch (XmlPullParserException localXmlPullParserException)
    {
      throw new RuntimeException("Couldn't create XmlPullParserFactory instance", localXmlPullParserException);
    }
  }
  
  private TtmlStyle createIfNull(TtmlStyle paramTtmlStyle)
  {
    TtmlStyle localTtmlStyle = paramTtmlStyle;
    if (paramTtmlStyle == null) {
      localTtmlStyle = new TtmlStyle();
    }
    return localTtmlStyle;
  }
  
  private static boolean isSupportedTag(String paramString)
  {
    return (paramString.equals("tt")) || (paramString.equals("head")) || (paramString.equals("body")) || (paramString.equals("div")) || (paramString.equals("p")) || (paramString.equals("span")) || (paramString.equals("br")) || (paramString.equals("style")) || (paramString.equals("styling")) || (paramString.equals("layout")) || (paramString.equals("region")) || (paramString.equals("metadata")) || (paramString.equals("smpte:image")) || (paramString.equals("smpte:data")) || (paramString.equals("smpte:information"));
  }
  
  private static void parseFontSize(String paramString, TtmlStyle paramTtmlStyle)
    throws SubtitleDecoderException
  {
    Object localObject = paramString.split("\\s+");
    label21:
    int i;
    if (localObject.length == 1)
    {
      localObject = FONT_SIZE.matcher(paramString);
      if (!((Matcher)localObject).matches()) {
        break label279;
      }
      paramString = ((Matcher)localObject).group(3);
      i = -1;
      switch (paramString.hashCode())
      {
      }
    }
    for (;;)
    {
      switch (i)
      {
      default: 
        throw new SubtitleDecoderException("Invalid unit for fontSize: '" + paramString + "'.");
        if (localObject.length == 2)
        {
          localObject = FONT_SIZE.matcher(localObject[1]);
          Log.w("TtmlDecoder", "Multiple values in fontSize attribute. Picking the second value for vertical font size and ignoring the first.");
          break label21;
        }
        throw new SubtitleDecoderException("Invalid number of entries for fontSize: " + localObject.length + ".");
        if (paramString.equals("px"))
        {
          i = 0;
          continue;
          if (paramString.equals("em"))
          {
            i = 1;
            continue;
            if (paramString.equals("%")) {
              i = 2;
            }
          }
        }
        break;
      }
    }
    paramTtmlStyle.setFontSizeUnit(1);
    for (;;)
    {
      paramTtmlStyle.setFontSize(Float.valueOf(((Matcher)localObject).group(1)).floatValue());
      return;
      paramTtmlStyle.setFontSizeUnit(2);
      continue;
      paramTtmlStyle.setFontSizeUnit(3);
    }
    label279:
    throw new SubtitleDecoderException("Invalid expression for fontSize: '" + paramString + "'.");
  }
  
  private FrameAndTickRate parseFrameAndTickRates(XmlPullParser paramXmlPullParser)
    throws SubtitleDecoderException
  {
    int i = 30;
    Object localObject = paramXmlPullParser.getAttributeValue("http://www.w3.org/ns/ttml#parameter", "frameRate");
    if (localObject != null) {
      i = Integer.parseInt((String)localObject);
    }
    float f = 1.0F;
    localObject = paramXmlPullParser.getAttributeValue("http://www.w3.org/ns/ttml#parameter", "frameRateMultiplier");
    if (localObject != null)
    {
      localObject = ((String)localObject).split(" ");
      if (localObject.length != 2) {
        throw new SubtitleDecoderException("frameRateMultiplier doesn't have 2 parts");
      }
      f = Integer.parseInt(localObject[0]) / Integer.parseInt(localObject[1]);
    }
    int j = DEFAULT_FRAME_AND_TICK_RATE.subFrameRate;
    localObject = paramXmlPullParser.getAttributeValue("http://www.w3.org/ns/ttml#parameter", "subFrameRate");
    if (localObject != null) {
      j = Integer.parseInt((String)localObject);
    }
    int k = DEFAULT_FRAME_AND_TICK_RATE.tickRate;
    paramXmlPullParser = paramXmlPullParser.getAttributeValue("http://www.w3.org/ns/ttml#parameter", "tickRate");
    if (paramXmlPullParser != null) {
      k = Integer.parseInt(paramXmlPullParser);
    }
    return new FrameAndTickRate(i * f, j, k);
  }
  
  private Map<String, TtmlStyle> parseHeader(XmlPullParser paramXmlPullParser, Map<String, TtmlStyle> paramMap, Map<String, TtmlRegion> paramMap1)
    throws IOException, XmlPullParserException
  {
    paramXmlPullParser.next();
    Object localObject1;
    if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "style"))
    {
      Object localObject2 = XmlPullParserUtil.getAttributeValue(paramXmlPullParser, "style");
      localObject1 = parseStyleAttributes(paramXmlPullParser, new TtmlStyle());
      if (localObject2 != null)
      {
        localObject2 = parseStyleIds((String)localObject2);
        int j = localObject2.length;
        int i = 0;
        while (i < j)
        {
          ((TtmlStyle)localObject1).chain((TtmlStyle)paramMap.get(localObject2[i]));
          i += 1;
        }
      }
      if (((TtmlStyle)localObject1).getId() != null) {
        paramMap.put(((TtmlStyle)localObject1).getId(), localObject1);
      }
    }
    while (XmlPullParserUtil.isEndTag(paramXmlPullParser, "head"))
    {
      return paramMap;
      if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "region"))
      {
        localObject1 = parseRegionAttributes(paramXmlPullParser);
        if (localObject1 != null) {
          paramMap1.put(((Pair)localObject1).first, ((Pair)localObject1).second);
        }
      }
    }
  }
  
  private TtmlNode parseNode(XmlPullParser paramXmlPullParser, TtmlNode paramTtmlNode, Map<String, TtmlRegion> paramMap, FrameAndTickRate paramFrameAndTickRate)
    throws SubtitleDecoderException
  {
    long l5 = -9223372036854775807L;
    long l1 = -9223372036854775807L;
    long l2 = -9223372036854775807L;
    Object localObject1 = "";
    Object localObject2 = null;
    int k = paramXmlPullParser.getAttributeCount();
    TtmlStyle localTtmlStyle = parseStyleAttributes(paramXmlPullParser, null);
    int j = 0;
    label128:
    long l3;
    if (j < k)
    {
      Object localObject3 = paramXmlPullParser.getAttributeName(j);
      Object localObject5 = paramXmlPullParser.getAttributeValue(j);
      int i = -1;
      Object localObject4;
      switch (((String)localObject3).hashCode())
      {
      default: 
        switch (i)
        {
        default: 
          l6 = l5;
          localObject4 = localObject1;
          localObject3 = localObject2;
          l4 = l2;
          l3 = l1;
        }
        break;
      }
      for (;;)
      {
        j += 1;
        l1 = l3;
        l2 = l4;
        localObject2 = localObject3;
        localObject1 = localObject4;
        l5 = l6;
        break;
        if (!((String)localObject3).equals("begin")) {
          break label128;
        }
        i = 0;
        break label128;
        if (!((String)localObject3).equals("end")) {
          break label128;
        }
        i = 1;
        break label128;
        if (!((String)localObject3).equals("dur")) {
          break label128;
        }
        i = 2;
        break label128;
        if (!((String)localObject3).equals("style")) {
          break label128;
        }
        i = 3;
        break label128;
        if (!((String)localObject3).equals("region")) {
          break label128;
        }
        i = 4;
        break label128;
        l3 = parseTimeExpression((String)localObject5, paramFrameAndTickRate);
        l4 = l2;
        localObject3 = localObject2;
        localObject4 = localObject1;
        l6 = l5;
        continue;
        l4 = parseTimeExpression((String)localObject5, paramFrameAndTickRate);
        l3 = l1;
        localObject3 = localObject2;
        localObject4 = localObject1;
        l6 = l5;
        continue;
        l6 = parseTimeExpression((String)localObject5, paramFrameAndTickRate);
        l3 = l1;
        l4 = l2;
        localObject3 = localObject2;
        localObject4 = localObject1;
        continue;
        localObject5 = parseStyleIds((String)localObject5);
        l3 = l1;
        l4 = l2;
        localObject3 = localObject2;
        localObject4 = localObject1;
        l6 = l5;
        if (localObject5.length > 0)
        {
          localObject3 = localObject5;
          l3 = l1;
          l4 = l2;
          localObject4 = localObject1;
          l6 = l5;
          continue;
          l3 = l1;
          l4 = l2;
          localObject3 = localObject2;
          localObject4 = localObject1;
          l6 = l5;
          if (paramMap.containsKey(localObject5))
          {
            localObject4 = localObject5;
            l3 = l1;
            l4 = l2;
            localObject3 = localObject2;
            l6 = l5;
          }
        }
      }
    }
    long l6 = l1;
    long l4 = l2;
    if (paramTtmlNode != null)
    {
      l6 = l1;
      l4 = l2;
      if (paramTtmlNode.startTimeUs != -9223372036854775807L)
      {
        l3 = l1;
        if (l1 != -9223372036854775807L) {
          l3 = l1 + paramTtmlNode.startTimeUs;
        }
        l6 = l3;
        l4 = l2;
        if (l2 != -9223372036854775807L)
        {
          l4 = l2 + paramTtmlNode.startTimeUs;
          l6 = l3;
        }
      }
    }
    l1 = l4;
    if (l4 == -9223372036854775807L)
    {
      if (l5 == -9223372036854775807L) {
        break label620;
      }
      l1 = l6 + l5;
    }
    for (;;)
    {
      return TtmlNode.buildNode(paramXmlPullParser.getName(), l6, l1, localTtmlStyle, (String[])localObject2, (String)localObject1);
      label620:
      l1 = l4;
      if (paramTtmlNode != null)
      {
        l1 = l4;
        if (paramTtmlNode.endTimeUs != -9223372036854775807L) {
          l1 = paramTtmlNode.endTimeUs;
        }
      }
    }
  }
  
  private Pair<String, TtmlRegion> parseRegionAttributes(XmlPullParser paramXmlPullParser)
  {
    String str = XmlPullParserUtil.getAttributeValue(paramXmlPullParser, "id");
    localObject = XmlPullParserUtil.getAttributeValue(paramXmlPullParser, "origin");
    paramXmlPullParser = XmlPullParserUtil.getAttributeValue(paramXmlPullParser, "extent");
    if ((localObject == null) || (str == null)) {}
    do
    {
      return null;
      float f1 = Float.MIN_VALUE;
      float f3 = Float.MIN_VALUE;
      Matcher localMatcher = PERCENTAGE_COORDINATES.matcher((CharSequence)localObject);
      float f2 = f3;
      if (localMatcher.matches()) {}
      try
      {
        f1 = Float.parseFloat(localMatcher.group(1)) / 100.0F;
        f2 = Float.parseFloat(localMatcher.group(2));
        f2 /= 100.0F;
      }
      catch (NumberFormatException localNumberFormatException2)
      {
        for (;;)
        {
          Log.w("TtmlDecoder", "Ignoring region with malformed origin: '" + (String)localObject + "'", localNumberFormatException2);
          f1 = Float.MIN_VALUE;
          f2 = f3;
        }
      }
      float f4 = Float.MIN_VALUE;
      f3 = f4;
      if (paramXmlPullParser != null)
      {
        localObject = PERCENTAGE_COORDINATES.matcher(paramXmlPullParser);
        f3 = f4;
        if (!((Matcher)localObject).matches()) {}
      }
      try
      {
        f3 = Float.parseFloat(((Matcher)localObject).group(1));
        f3 /= 100.0F;
      }
      catch (NumberFormatException localNumberFormatException1)
      {
        for (;;)
        {
          Log.w("TtmlDecoder", "Ignoring malformed region extent: '" + paramXmlPullParser + "'", localNumberFormatException1);
          f3 = f4;
        }
      }
    } while (f1 == Float.MIN_VALUE);
    return new Pair(str, new TtmlRegion(f1, f2, 0, f3));
  }
  
  private TtmlStyle parseStyleAttributes(XmlPullParser paramXmlPullParser, TtmlStyle paramTtmlStyle)
  {
    int k = paramXmlPullParser.getAttributeCount();
    int j = 0;
    TtmlStyle localTtmlStyle1 = paramTtmlStyle;
    if (j < k)
    {
      String str = paramXmlPullParser.getAttributeValue(j);
      paramTtmlStyle = paramXmlPullParser.getAttributeName(j);
      label128:
      int i;
      switch (paramTtmlStyle.hashCode())
      {
      default: 
        i = -1;
        switch (i)
        {
        default: 
          label130:
          paramTtmlStyle = localTtmlStyle1;
        }
        break;
      }
      for (;;)
      {
        j += 1;
        localTtmlStyle1 = paramTtmlStyle;
        break;
        if (!paramTtmlStyle.equals("id")) {
          break label128;
        }
        i = 0;
        break label130;
        if (!paramTtmlStyle.equals("backgroundColor")) {
          break label128;
        }
        i = 1;
        break label130;
        if (!paramTtmlStyle.equals("color")) {
          break label128;
        }
        i = 2;
        break label130;
        if (!paramTtmlStyle.equals("fontFamily")) {
          break label128;
        }
        i = 3;
        break label130;
        if (!paramTtmlStyle.equals("fontSize")) {
          break label128;
        }
        i = 4;
        break label130;
        if (!paramTtmlStyle.equals("fontWeight")) {
          break label128;
        }
        i = 5;
        break label130;
        if (!paramTtmlStyle.equals("fontStyle")) {
          break label128;
        }
        i = 6;
        break label130;
        if (!paramTtmlStyle.equals("textAlign")) {
          break label128;
        }
        i = 7;
        break label130;
        if (!paramTtmlStyle.equals("textDecoration")) {
          break label128;
        }
        i = 8;
        break label130;
        paramTtmlStyle = localTtmlStyle1;
        if ("style".equals(paramXmlPullParser.getName()))
        {
          paramTtmlStyle = createIfNull(localTtmlStyle1).setId(str);
          continue;
          paramTtmlStyle = createIfNull(localTtmlStyle1);
          try
          {
            paramTtmlStyle.setBackgroundColor(ColorParser.parseTtmlColor(str));
          }
          catch (IllegalArgumentException localIllegalArgumentException1)
          {
            Log.w("TtmlDecoder", "failed parsing background value: '" + str + "'");
          }
          continue;
          paramTtmlStyle = createIfNull(localIllegalArgumentException1);
          try
          {
            paramTtmlStyle.setFontColor(ColorParser.parseTtmlColor(str));
          }
          catch (IllegalArgumentException localIllegalArgumentException2)
          {
            Log.w("TtmlDecoder", "failed parsing color value: '" + str + "'");
          }
          continue;
          paramTtmlStyle = createIfNull(localIllegalArgumentException2).setFontFamily(str);
          continue;
          paramTtmlStyle = localIllegalArgumentException2;
          try
          {
            TtmlStyle localTtmlStyle2 = createIfNull(localIllegalArgumentException2);
            paramTtmlStyle = localTtmlStyle2;
            parseFontSize(str, localTtmlStyle2);
            paramTtmlStyle = localTtmlStyle2;
          }
          catch (SubtitleDecoderException localSubtitleDecoderException)
          {
            Log.w("TtmlDecoder", "failed parsing fontSize value: '" + str + "'");
          }
          continue;
          paramTtmlStyle = createIfNull(localSubtitleDecoderException).setBold("bold".equalsIgnoreCase(str));
          continue;
          paramTtmlStyle = createIfNull(localSubtitleDecoderException).setItalic("italic".equalsIgnoreCase(str));
          continue;
          paramTtmlStyle = Util.toLowerInvariant(str);
          switch (paramTtmlStyle.hashCode())
          {
          default: 
            label664:
            i = -1;
          }
          for (;;)
          {
            switch (i)
            {
            default: 
              paramTtmlStyle = localSubtitleDecoderException;
              break;
            case 0: 
              paramTtmlStyle = createIfNull(localSubtitleDecoderException).setTextAlign(Layout.Alignment.ALIGN_NORMAL);
              break;
              if (!paramTtmlStyle.equals("left")) {
                break label664;
              }
              i = 0;
              continue;
              if (!paramTtmlStyle.equals("start")) {
                break label664;
              }
              i = 1;
              continue;
              if (!paramTtmlStyle.equals("right")) {
                break label664;
              }
              i = 2;
              continue;
              if (!paramTtmlStyle.equals("end")) {
                break label664;
              }
              i = 3;
              continue;
              if (!paramTtmlStyle.equals("center")) {
                break label664;
              }
              i = 4;
            }
          }
          paramTtmlStyle = createIfNull(localSubtitleDecoderException).setTextAlign(Layout.Alignment.ALIGN_NORMAL);
          continue;
          paramTtmlStyle = createIfNull(localSubtitleDecoderException).setTextAlign(Layout.Alignment.ALIGN_OPPOSITE);
          continue;
          paramTtmlStyle = createIfNull(localSubtitleDecoderException).setTextAlign(Layout.Alignment.ALIGN_OPPOSITE);
          continue;
          paramTtmlStyle = createIfNull(localSubtitleDecoderException).setTextAlign(Layout.Alignment.ALIGN_CENTER);
          continue;
          paramTtmlStyle = Util.toLowerInvariant(str);
          switch (paramTtmlStyle.hashCode())
          {
          default: 
            label912:
            i = -1;
          }
          for (;;)
          {
            switch (i)
            {
            default: 
              paramTtmlStyle = localSubtitleDecoderException;
              break;
            case 0: 
              paramTtmlStyle = createIfNull(localSubtitleDecoderException).setLinethrough(true);
              break;
              if (!paramTtmlStyle.equals("linethrough")) {
                break label912;
              }
              i = 0;
              continue;
              if (!paramTtmlStyle.equals("nolinethrough")) {
                break label912;
              }
              i = 1;
              continue;
              if (!paramTtmlStyle.equals("underline")) {
                break label912;
              }
              i = 2;
              continue;
              if (!paramTtmlStyle.equals("nounderline")) {
                break label912;
              }
              i = 3;
            }
          }
          paramTtmlStyle = createIfNull(localSubtitleDecoderException).setLinethrough(false);
          continue;
          paramTtmlStyle = createIfNull(localSubtitleDecoderException).setUnderline(true);
          continue;
          paramTtmlStyle = createIfNull(localSubtitleDecoderException).setUnderline(false);
        }
      }
    }
    return localSubtitleDecoderException;
  }
  
  private String[] parseStyleIds(String paramString)
  {
    return paramString.split("\\s+");
  }
  
  private static long parseTimeExpression(String paramString, FrameAndTickRate paramFrameAndTickRate)
    throws SubtitleDecoderException
  {
    Matcher localMatcher = CLOCK_TIME.matcher(paramString);
    double d1;
    double d2;
    if (localMatcher.matches())
    {
      double d4 = Long.parseLong(localMatcher.group(1)) * 3600L;
      double d5 = Long.parseLong(localMatcher.group(2)) * 60L;
      double d6 = Long.parseLong(localMatcher.group(3));
      paramString = localMatcher.group(4);
      if (paramString != null)
      {
        d1 = Double.parseDouble(paramString);
        paramString = localMatcher.group(5);
        if (paramString == null) {
          break label159;
        }
        d2 = (float)Long.parseLong(paramString) / paramFrameAndTickRate.effectiveFrameRate;
        label101:
        paramString = localMatcher.group(6);
        if (paramString == null) {
          break label165;
        }
      }
      label159:
      label165:
      for (double d3 = Long.parseLong(paramString) / paramFrameAndTickRate.subFrameRate / paramFrameAndTickRate.effectiveFrameRate;; d3 = 0.0D)
      {
        return (1000000.0D * (d4 + d5 + d6 + d1 + d2 + d3));
        d1 = 0.0D;
        break;
        d2 = 0.0D;
        break label101;
      }
    }
    localMatcher = OFFSET_TIME.matcher(paramString);
    if (localMatcher.matches())
    {
      d2 = Double.parseDouble(localMatcher.group(1));
      paramString = localMatcher.group(2);
      int i = -1;
      switch (paramString.hashCode())
      {
      default: 
        d1 = d2;
        switch (i)
        {
        default: 
          d1 = d2;
        }
        break;
      }
      for (;;)
      {
        return (1000000.0D * d1);
        if (!paramString.equals("h")) {
          break;
        }
        i = 0;
        break;
        if (!paramString.equals("m")) {
          break;
        }
        i = 1;
        break;
        if (!paramString.equals("s")) {
          break;
        }
        i = 2;
        break;
        if (!paramString.equals("ms")) {
          break;
        }
        i = 3;
        break;
        if (!paramString.equals("f")) {
          break;
        }
        i = 4;
        break;
        if (!paramString.equals("t")) {
          break;
        }
        i = 5;
        break;
        d1 = d2 * 3600.0D;
        continue;
        d1 = d2 * 60.0D;
        continue;
        d1 = d2 / 1000.0D;
        continue;
        d1 = d2 / paramFrameAndTickRate.effectiveFrameRate;
        continue;
        d1 = d2 / paramFrameAndTickRate.tickRate;
      }
    }
    throw new SubtitleDecoderException("Malformed time expression: " + paramString);
  }
  
  protected TtmlSubtitle decode(byte[] paramArrayOfByte, int paramInt)
    throws SubtitleDecoderException
  {
    for (;;)
    {
      XmlPullParser localXmlPullParser;
      HashMap localHashMap1;
      HashMap localHashMap2;
      LinkedList localLinkedList;
      byte[] arrayOfByte;
      try
      {
        localXmlPullParser = this.xmlParserFactory.newPullParser();
        localHashMap1 = new HashMap();
        localHashMap2 = new HashMap();
        localHashMap2.put("", new TtmlRegion());
        localXmlPullParser.setInput(new ByteArrayInputStream(paramArrayOfByte, 0, paramInt), null);
        paramArrayOfByte = null;
        localLinkedList = new LinkedList();
        i = 0;
        j = localXmlPullParser.getEventType();
        localObject1 = DEFAULT_FRAME_AND_TICK_RATE;
        if (j == 1) {
          break;
        }
        localTtmlNode1 = (TtmlNode)localLinkedList.peekLast();
        if (i != 0) {
          break label464;
        }
        localObject2 = localXmlPullParser.getName();
        if (j != 2) {
          continue;
        }
        if ("tt".equals(localObject2)) {
          localObject1 = parseFrameAndTickRates(localXmlPullParser);
        }
        if (isSupportedTag((String)localObject2)) {
          continue;
        }
        Log.i("TtmlDecoder", "Ignoring unsupported tag: " + localXmlPullParser.getName());
        paramInt = i + 1;
        arrayOfByte = paramArrayOfByte;
        localObject2 = localObject1;
      }
      catch (XmlPullParserException paramArrayOfByte)
      {
        TtmlNode localTtmlNode1;
        Object localObject2;
        throw new SubtitleDecoderException("Unable to decode source", paramArrayOfByte);
        try
        {
          TtmlNode localTtmlNode2 = parseNode(localXmlPullParser, localTtmlNode1, localHashMap2, (FrameAndTickRate)localObject1);
          localLinkedList.addLast(localTtmlNode2);
          localObject2 = localObject1;
          arrayOfByte = paramArrayOfByte;
          paramInt = i;
          if (localTtmlNode1 == null) {
            continue;
          }
          localTtmlNode1.addChild(localTtmlNode2);
          localObject2 = localObject1;
          arrayOfByte = paramArrayOfByte;
          paramInt = i;
        }
        catch (SubtitleDecoderException localSubtitleDecoderException)
        {
          Log.w("TtmlDecoder", "Suppressing parser error", localSubtitleDecoderException);
          paramInt = i + 1;
          localObject3 = localObject1;
          arrayOfByte = paramArrayOfByte;
        }
        continue;
        if (j != 4) {
          break label396;
        }
        localTtmlNode1.addChild(TtmlNode.buildTextNode(localXmlPullParser.getText()));
        localObject3 = localObject1;
        arrayOfByte = paramArrayOfByte;
        paramInt = i;
        continue;
      }
      catch (IOException paramArrayOfByte)
      {
        throw new IllegalStateException("Unexpected error when reading input.", paramArrayOfByte);
      }
      localXmlPullParser.next();
      int j = localXmlPullParser.getEventType();
      Object localObject1 = localObject2;
      paramArrayOfByte = arrayOfByte;
      int i = paramInt;
      continue;
      if ("head".equals(localObject2))
      {
        parseHeader(localXmlPullParser, localHashMap1, localHashMap2);
        localObject2 = localObject1;
        arrayOfByte = paramArrayOfByte;
        paramInt = i;
      }
      else
      {
        label396:
        Object localObject3 = localObject1;
        arrayOfByte = paramArrayOfByte;
        paramInt = i;
        if (j == 3)
        {
          if (localXmlPullParser.getName().equals("tt")) {
            paramArrayOfByte = new TtmlSubtitle((TtmlNode)localLinkedList.getLast(), localHashMap1, localHashMap2);
          }
          localLinkedList.removeLast();
          localObject3 = localObject1;
          arrayOfByte = paramArrayOfByte;
          paramInt = i;
          continue;
          label464:
          if (j == 2)
          {
            paramInt = i + 1;
            localObject3 = localObject1;
            arrayOfByte = paramArrayOfByte;
          }
          else
          {
            localObject3 = localObject1;
            arrayOfByte = paramArrayOfByte;
            paramInt = i;
            if (j == 3)
            {
              paramInt = i - 1;
              localObject3 = localObject1;
              arrayOfByte = paramArrayOfByte;
            }
          }
        }
      }
    }
    return paramArrayOfByte;
  }
  
  private static final class FrameAndTickRate
  {
    final float effectiveFrameRate;
    final int subFrameRate;
    final int tickRate;
    
    FrameAndTickRate(float paramFloat, int paramInt1, int paramInt2)
    {
      this.effectiveFrameRate = paramFloat;
      this.subFrameRate = paramInt1;
      this.tickRate = paramInt2;
    }
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/text/ttml/TtmlDecoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */