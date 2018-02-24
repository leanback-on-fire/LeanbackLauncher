package com.google.android.exoplayer2.source.dash.manifest;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.drm.DrmInitData;
import com.google.android.exoplayer2.drm.DrmInitData.SchemeData;
import com.google.android.exoplayer2.extractor.mp4.PsshAtomUtil;
import com.google.android.exoplayer2.upstream.ParsingLoadable.Parser;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.UriUtil;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.util.XmlPullParserUtil;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.xml.sax.helpers.DefaultHandler;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class DashManifestParser
  extends DefaultHandler
  implements ParsingLoadable.Parser<DashManifest>
{
  private static final Pattern CEA_608_ACCESSIBILITY_PATTERN = Pattern.compile("CC([1-4])=.*");
  private static final Pattern CEA_708_ACCESSIBILITY_PATTERN = Pattern.compile("([1-9]|[1-5][0-9]|6[0-3])=.*");
  private static final Pattern FRAME_RATE_PATTERN = Pattern.compile("(\\d+)(?:/(\\d+))?");
  private static final String TAG = "MpdParser";
  private final String contentId;
  private final XmlPullParserFactory xmlParserFactory;
  
  public DashManifestParser()
  {
    this(null);
  }
  
  public DashManifestParser(String paramString)
  {
    this.contentId = paramString;
    try
    {
      this.xmlParserFactory = XmlPullParserFactory.newInstance();
      return;
    }
    catch (XmlPullParserException paramString)
    {
      throw new RuntimeException("Couldn't create XmlPullParserFactory instance", paramString);
    }
  }
  
  private static int checkContentTypeConsistency(int paramInt1, int paramInt2)
  {
    int i;
    if (paramInt1 == -1) {
      i = paramInt2;
    }
    do
    {
      return i;
      i = paramInt1;
    } while (paramInt2 == -1);
    if (paramInt1 == paramInt2) {}
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkState(bool);
      return paramInt1;
    }
  }
  
  private static String checkLanguageConsistency(String paramString1, String paramString2)
  {
    String str;
    if (paramString1 == null) {
      str = paramString2;
    }
    do
    {
      return str;
      str = paramString1;
    } while (paramString2 == null);
    Assertions.checkState(paramString1.equals(paramString2));
    return paramString1;
  }
  
  private static String getSampleMimeType(String paramString1, String paramString2)
  {
    String str;
    if (MimeTypes.isAudio(paramString1)) {
      str = MimeTypes.getAudioMediaMimeType(paramString2);
    }
    do
    {
      return str;
      if (MimeTypes.isVideo(paramString1)) {
        return MimeTypes.getVideoMediaMimeType(paramString2);
      }
      str = paramString1;
    } while (mimeTypeIsRawText(paramString1));
    if ("application/mp4".equals(paramString1))
    {
      if ("stpp".equals(paramString2)) {
        return "application/ttml+xml";
      }
      if ("wvtt".equals(paramString2)) {
        return "application/x-mp4-vtt";
      }
    }
    else if ("application/x-rawcc".equals(paramString1))
    {
      if (paramString2 != null)
      {
        if (paramString2.contains("cea708")) {
          return "application/cea-708";
        }
        if ((paramString2.contains("eia608")) || (paramString2.contains("cea608"))) {
          return "application/cea-608";
        }
      }
      return null;
    }
    return null;
  }
  
  private static boolean mimeTypeIsRawText(String paramString)
  {
    return (MimeTypes.isText(paramString)) || ("application/ttml+xml".equals(paramString)) || ("application/x-mp4-vtt".equals(paramString)) || ("application/cea-708".equals(paramString)) || ("application/cea-608".equals(paramString));
  }
  
  protected static String parseBaseUrl(XmlPullParser paramXmlPullParser, String paramString)
    throws XmlPullParserException, IOException
  {
    paramXmlPullParser.next();
    return UriUtil.resolve(paramString, paramXmlPullParser.getText());
  }
  
  protected static int parseCea608AccessibilityChannel(List<SchemeValuePair> paramList)
  {
    int i = 0;
    while (i < paramList.size())
    {
      SchemeValuePair localSchemeValuePair = (SchemeValuePair)paramList.get(i);
      if (("urn:scte:dash:cc:cea-608:2015".equals(localSchemeValuePair.schemeIdUri)) && (localSchemeValuePair.value != null))
      {
        Matcher localMatcher = CEA_608_ACCESSIBILITY_PATTERN.matcher(localSchemeValuePair.value);
        if (localMatcher.matches()) {
          return Integer.parseInt(localMatcher.group(1));
        }
        Log.w("MpdParser", "Unable to parse CEA-608 channel number from: " + localSchemeValuePair.value);
      }
      i += 1;
    }
    return -1;
  }
  
  protected static int parseCea708AccessibilityChannel(List<SchemeValuePair> paramList)
  {
    int i = 0;
    while (i < paramList.size())
    {
      SchemeValuePair localSchemeValuePair = (SchemeValuePair)paramList.get(i);
      if (("urn:scte:dash:cc:cea-708:2015".equals(localSchemeValuePair.schemeIdUri)) && (localSchemeValuePair.value != null))
      {
        Matcher localMatcher = CEA_708_ACCESSIBILITY_PATTERN.matcher(localSchemeValuePair.value);
        if (localMatcher.matches()) {
          return Integer.parseInt(localMatcher.group(1));
        }
        Log.w("MpdParser", "Unable to parse CEA-708 service block number from: " + localSchemeValuePair.value);
      }
      i += 1;
    }
    return -1;
  }
  
  protected static long parseDateTime(XmlPullParser paramXmlPullParser, String paramString, long paramLong)
    throws ParserException
  {
    paramXmlPullParser = paramXmlPullParser.getAttributeValue(null, paramString);
    if (paramXmlPullParser == null) {
      return paramLong;
    }
    return Util.parseXsDateTime(paramXmlPullParser);
  }
  
  protected static long parseDuration(XmlPullParser paramXmlPullParser, String paramString, long paramLong)
  {
    paramXmlPullParser = paramXmlPullParser.getAttributeValue(null, paramString);
    if (paramXmlPullParser == null) {
      return paramLong;
    }
    return Util.parseXsDuration(paramXmlPullParser);
  }
  
  protected static float parseFrameRate(XmlPullParser paramXmlPullParser, float paramFloat)
  {
    paramXmlPullParser = paramXmlPullParser.getAttributeValue(null, "frameRate");
    float f = paramFloat;
    int i;
    if (paramXmlPullParser != null)
    {
      paramXmlPullParser = FRAME_RATE_PATTERN.matcher(paramXmlPullParser);
      f = paramFloat;
      if (paramXmlPullParser.matches())
      {
        i = Integer.parseInt(paramXmlPullParser.group(1));
        paramXmlPullParser = paramXmlPullParser.group(2);
        if (TextUtils.isEmpty(paramXmlPullParser)) {
          break label66;
        }
        f = i / Integer.parseInt(paramXmlPullParser);
      }
    }
    return f;
    label66:
    return i;
  }
  
  protected static int parseInt(XmlPullParser paramXmlPullParser, String paramString, int paramInt)
  {
    paramXmlPullParser = paramXmlPullParser.getAttributeValue(null, paramString);
    if (paramXmlPullParser == null) {
      return paramInt;
    }
    return Integer.parseInt(paramXmlPullParser);
  }
  
  protected static long parseLong(XmlPullParser paramXmlPullParser, String paramString, long paramLong)
  {
    paramXmlPullParser = paramXmlPullParser.getAttributeValue(null, paramString);
    if (paramXmlPullParser == null) {
      return paramLong;
    }
    return Long.parseLong(paramXmlPullParser);
  }
  
  protected static SchemeValuePair parseSchemeValuePair(XmlPullParser paramXmlPullParser, String paramString)
    throws XmlPullParserException, IOException
  {
    String str1 = parseString(paramXmlPullParser, "schemeIdUri", null);
    String str2 = parseString(paramXmlPullParser, "value", null);
    do
    {
      paramXmlPullParser.next();
    } while (!XmlPullParserUtil.isEndTag(paramXmlPullParser, paramString));
    return new SchemeValuePair(str1, str2);
  }
  
  protected static String parseString(XmlPullParser paramXmlPullParser, String paramString1, String paramString2)
  {
    paramXmlPullParser = paramXmlPullParser.getAttributeValue(null, paramString1);
    if (paramXmlPullParser == null) {
      return paramString2;
    }
    return paramXmlPullParser;
  }
  
  protected AdaptationSet buildAdaptationSet(int paramInt1, int paramInt2, List<Representation> paramList, List<SchemeValuePair> paramList1)
  {
    return new AdaptationSet(paramInt1, paramInt2, paramList, paramList1);
  }
  
  protected Format buildFormat(String paramString1, String paramString2, int paramInt1, int paramInt2, float paramFloat, int paramInt3, int paramInt4, int paramInt5, String paramString3, int paramInt6, List<SchemeValuePair> paramList, String paramString4)
  {
    String str = getSampleMimeType(paramString2, paramString4);
    if (str != null)
    {
      if (MimeTypes.isVideo(str)) {
        return Format.createVideoContainerFormat(paramString1, paramString2, str, paramString4, paramInt5, paramInt1, paramInt2, paramFloat, null, paramInt6);
      }
      if (MimeTypes.isAudio(str)) {
        return Format.createAudioContainerFormat(paramString1, paramString2, str, paramString4, paramInt5, paramInt3, paramInt4, null, paramInt6, paramString3);
      }
      if (mimeTypeIsRawText(str))
      {
        if ("application/cea-608".equals(str)) {
          paramInt1 = parseCea608AccessibilityChannel(paramList);
        }
        for (;;)
        {
          return Format.createTextContainerFormat(paramString1, paramString2, str, paramString4, paramInt5, paramInt6, paramString3, paramInt1);
          if ("application/cea-708".equals(str)) {
            paramInt1 = parseCea708AccessibilityChannel(paramList);
          } else {
            paramInt1 = -1;
          }
        }
      }
    }
    return Format.createContainerFormat(paramString1, paramString2, str, paramString4, paramInt5, paramInt6, paramString3);
  }
  
  protected DashManifest buildMediaPresentationDescription(long paramLong1, long paramLong2, long paramLong3, boolean paramBoolean, long paramLong4, long paramLong5, long paramLong6, UtcTimingElement paramUtcTimingElement, Uri paramUri, List<Period> paramList)
  {
    return new DashManifest(paramLong1, paramLong2, paramLong3, paramBoolean, paramLong4, paramLong5, paramLong6, paramUtcTimingElement, paramUri, paramList);
  }
  
  protected Period buildPeriod(String paramString, long paramLong, List<AdaptationSet> paramList)
  {
    return new Period(paramString, paramLong, paramList);
  }
  
  protected RangedUri buildRangedUri(String paramString, long paramLong1, long paramLong2)
  {
    return new RangedUri(paramString, paramLong1, paramLong2);
  }
  
  protected Representation buildRepresentation(RepresentationInfo paramRepresentationInfo, String paramString, ArrayList<DrmInitData.SchemeData> paramArrayList, ArrayList<SchemeValuePair> paramArrayList1)
  {
    Object localObject = paramRepresentationInfo.format;
    ArrayList localArrayList = paramRepresentationInfo.drmSchemeDatas;
    localArrayList.addAll(paramArrayList);
    paramArrayList = (ArrayList<DrmInitData.SchemeData>)localObject;
    if (!localArrayList.isEmpty()) {
      paramArrayList = ((Format)localObject).copyWithDrmInitData(new DrmInitData(localArrayList));
    }
    localObject = paramRepresentationInfo.inbandEventStreams;
    ((ArrayList)localObject).addAll(paramArrayList1);
    return Representation.newInstance(paramString, -1L, paramArrayList, paramRepresentationInfo.baseUrl, paramRepresentationInfo.segmentBase, (List)localObject);
  }
  
  protected SegmentBase.SegmentList buildSegmentList(RangedUri paramRangedUri, long paramLong1, long paramLong2, int paramInt, long paramLong3, List<SegmentBase.SegmentTimelineElement> paramList, List<RangedUri> paramList1)
  {
    return new SegmentBase.SegmentList(paramRangedUri, paramLong1, paramLong2, paramInt, paramLong3, paramList, paramList1);
  }
  
  protected SegmentBase.SegmentTemplate buildSegmentTemplate(RangedUri paramRangedUri, long paramLong1, long paramLong2, int paramInt, long paramLong3, List<SegmentBase.SegmentTimelineElement> paramList, UrlTemplate paramUrlTemplate1, UrlTemplate paramUrlTemplate2)
  {
    return new SegmentBase.SegmentTemplate(paramRangedUri, paramLong1, paramLong2, paramInt, paramLong3, paramList, paramUrlTemplate1, paramUrlTemplate2);
  }
  
  protected SegmentBase.SegmentTimelineElement buildSegmentTimelineElement(long paramLong1, long paramLong2)
  {
    return new SegmentBase.SegmentTimelineElement(paramLong1, paramLong2);
  }
  
  protected SegmentBase.SingleSegmentBase buildSingleSegmentBase(RangedUri paramRangedUri, long paramLong1, long paramLong2, long paramLong3, long paramLong4)
  {
    return new SegmentBase.SingleSegmentBase(paramRangedUri, paramLong1, paramLong2, paramLong3, paramLong4);
  }
  
  protected UtcTimingElement buildUtcTimingElement(String paramString1, String paramString2)
  {
    return new UtcTimingElement(paramString1, paramString2);
  }
  
  protected int getContentType(Format paramFormat)
  {
    paramFormat = paramFormat.sampleMimeType;
    if (TextUtils.isEmpty(paramFormat)) {}
    do
    {
      return -1;
      if (MimeTypes.isVideo(paramFormat)) {
        return 2;
      }
      if (MimeTypes.isAudio(paramFormat)) {
        return 1;
      }
    } while (!mimeTypeIsRawText(paramFormat));
    return 3;
  }
  
  public DashManifest parse(Uri paramUri, InputStream paramInputStream)
    throws IOException
  {
    XmlPullParser localXmlPullParser;
    try
    {
      localXmlPullParser = this.xmlParserFactory.newPullParser();
      localXmlPullParser.setInput(paramInputStream, null);
      if ((localXmlPullParser.next() != 2) || (!"MPD".equals(localXmlPullParser.getName()))) {
        throw new ParserException("inputStream does not contain a valid media presentation description");
      }
    }
    catch (XmlPullParserException paramUri)
    {
      throw new ParserException(paramUri);
    }
    paramUri = parseMediaPresentationDescription(localXmlPullParser, paramUri.toString());
    return paramUri;
  }
  
  protected SchemeValuePair parseAccessibility(XmlPullParser paramXmlPullParser)
    throws XmlPullParserException, IOException
  {
    return parseSchemeValuePair(paramXmlPullParser, "Accessibility");
  }
  
  protected AdaptationSet parseAdaptationSet(XmlPullParser paramXmlPullParser, String paramString, SegmentBase paramSegmentBase)
    throws XmlPullParserException, IOException
  {
    int i4 = parseInt(paramXmlPullParser, "id", -1);
    int m = parseContentType(paramXmlPullParser);
    String str1 = paramXmlPullParser.getAttributeValue(null, "mimeType");
    String str2 = paramXmlPullParser.getAttributeValue(null, "codecs");
    int i5 = parseInt(paramXmlPullParser, "width", -1);
    int i6 = parseInt(paramXmlPullParser, "height", -1);
    float f = parseFrameRate(paramXmlPullParser, -1.0F);
    int n = -1;
    int i7 = parseInt(paramXmlPullParser, "audioSamplingRate", -1);
    Object localObject1 = paramXmlPullParser.getAttributeValue(null, "lang");
    ArrayList localArrayList1 = new ArrayList();
    ArrayList localArrayList2 = new ArrayList();
    ArrayList localArrayList3 = new ArrayList();
    ArrayList localArrayList4 = new ArrayList();
    int k = 0;
    int j = 0;
    Object localObject2 = paramSegmentBase;
    paramSegmentBase = paramString;
    paramXmlPullParser.next();
    int i1;
    Object localObject3;
    int i2;
    int i;
    int i3;
    Object localObject4;
    if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "BaseURL"))
    {
      i1 = n;
      localObject3 = localObject1;
      i2 = k;
      i = m;
      i3 = j;
      localObject4 = paramSegmentBase;
      paramString = (String)localObject2;
      if (j == 0)
      {
        localObject4 = parseBaseUrl(paramXmlPullParser, paramSegmentBase);
        i3 = 1;
        paramString = (String)localObject2;
        i = m;
        i2 = k;
        localObject3 = localObject1;
        i1 = n;
      }
    }
    for (;;)
    {
      n = i1;
      localObject1 = localObject3;
      k = i2;
      m = i;
      j = i3;
      paramSegmentBase = (SegmentBase)localObject4;
      localObject2 = paramString;
      if (!XmlPullParserUtil.isEndTag(paramXmlPullParser, "AdaptationSet")) {
        break;
      }
      paramXmlPullParser = new ArrayList(localArrayList4.size());
      j = 0;
      while (j < localArrayList4.size())
      {
        paramXmlPullParser.add(buildRepresentation((RepresentationInfo)localArrayList4.get(j), this.contentId, localArrayList1, localArrayList2));
        j += 1;
      }
      if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "ContentProtection"))
      {
        DrmInitData.SchemeData localSchemeData = parseContentProtection(paramXmlPullParser);
        i1 = n;
        localObject3 = localObject1;
        i2 = k;
        i = m;
        i3 = j;
        localObject4 = paramSegmentBase;
        paramString = (String)localObject2;
        if (localSchemeData != null)
        {
          localArrayList1.add(localSchemeData);
          i1 = n;
          localObject3 = localObject1;
          i2 = k;
          i = m;
          i3 = j;
          localObject4 = paramSegmentBase;
          paramString = (String)localObject2;
        }
      }
      else if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "ContentComponent"))
      {
        localObject3 = checkLanguageConsistency((String)localObject1, paramXmlPullParser.getAttributeValue(null, "lang"));
        i = checkContentTypeConsistency(m, parseContentType(paramXmlPullParser));
        i1 = n;
        i2 = k;
        i3 = j;
        localObject4 = paramSegmentBase;
        paramString = (String)localObject2;
      }
      else if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "Role"))
      {
        i2 = k | parseRole(paramXmlPullParser);
        i1 = n;
        localObject3 = localObject1;
        i = m;
        i3 = j;
        localObject4 = paramSegmentBase;
        paramString = (String)localObject2;
      }
      else if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "AudioChannelConfiguration"))
      {
        i1 = parseAudioChannelConfiguration(paramXmlPullParser);
        localObject3 = localObject1;
        i2 = k;
        i = m;
        i3 = j;
        localObject4 = paramSegmentBase;
        paramString = (String)localObject2;
      }
      else if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "Accessibility"))
      {
        localArrayList3.add(parseAccessibility(paramXmlPullParser));
        i1 = n;
        localObject3 = localObject1;
        i2 = k;
        i = m;
        i3 = j;
        localObject4 = paramSegmentBase;
        paramString = (String)localObject2;
      }
      else if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "Representation"))
      {
        paramString = parseRepresentation(paramXmlPullParser, paramSegmentBase, str1, str2, i5, i6, f, n, i7, (String)localObject1, k, localArrayList3, (SegmentBase)localObject2);
        i = checkContentTypeConsistency(m, getContentType(paramString.format));
        localArrayList4.add(paramString);
        i1 = n;
        localObject3 = localObject1;
        i2 = k;
        i3 = j;
        localObject4 = paramSegmentBase;
        paramString = (String)localObject2;
      }
      else if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "SegmentBase"))
      {
        paramString = parseSegmentBase(paramXmlPullParser, (SegmentBase.SingleSegmentBase)localObject2);
        i1 = n;
        localObject3 = localObject1;
        i2 = k;
        i = m;
        i3 = j;
        localObject4 = paramSegmentBase;
      }
      else if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "SegmentList"))
      {
        paramString = parseSegmentList(paramXmlPullParser, (SegmentBase.SegmentList)localObject2);
        i1 = n;
        localObject3 = localObject1;
        i2 = k;
        i = m;
        i3 = j;
        localObject4 = paramSegmentBase;
      }
      else if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "SegmentTemplate"))
      {
        paramString = parseSegmentTemplate(paramXmlPullParser, (SegmentBase.SegmentTemplate)localObject2);
        i1 = n;
        localObject3 = localObject1;
        i2 = k;
        i = m;
        i3 = j;
        localObject4 = paramSegmentBase;
      }
      else if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "InbandEventStream"))
      {
        localArrayList2.add(parseInbandEventStream(paramXmlPullParser));
        i1 = n;
        localObject3 = localObject1;
        i2 = k;
        i = m;
        i3 = j;
        localObject4 = paramSegmentBase;
        paramString = (String)localObject2;
      }
      else
      {
        i1 = n;
        localObject3 = localObject1;
        i2 = k;
        i = m;
        i3 = j;
        localObject4 = paramSegmentBase;
        paramString = (String)localObject2;
        if (XmlPullParserUtil.isStartTag(paramXmlPullParser))
        {
          parseAdaptationSetChild(paramXmlPullParser);
          i1 = n;
          localObject3 = localObject1;
          i2 = k;
          i = m;
          i3 = j;
          localObject4 = paramSegmentBase;
          paramString = (String)localObject2;
        }
      }
    }
    return buildAdaptationSet(i4, i, paramXmlPullParser, localArrayList3);
  }
  
  protected void parseAdaptationSetChild(XmlPullParser paramXmlPullParser)
    throws XmlPullParserException, IOException
  {}
  
  protected int parseAudioChannelConfiguration(XmlPullParser paramXmlPullParser)
    throws XmlPullParserException, IOException
  {
    int i = -1;
    if ("urn:mpeg:dash:23003:3:audio_channel_configuration:2011".equals(parseString(paramXmlPullParser, "schemeIdUri", null))) {
      i = parseInt(paramXmlPullParser, "value", -1);
    }
    do
    {
      paramXmlPullParser.next();
    } while (!XmlPullParserUtil.isEndTag(paramXmlPullParser, "AudioChannelConfiguration"));
    return i;
  }
  
  protected DrmInitData.SchemeData parseContentProtection(XmlPullParser paramXmlPullParser)
    throws XmlPullParserException, IOException
  {
    Object localObject2 = null;
    Object localObject1 = null;
    int i = 0;
    boolean bool1 = false;
    paramXmlPullParser.next();
    int j;
    Object localObject4;
    boolean bool2;
    if ((XmlPullParserUtil.isStartTag(paramXmlPullParser, "cenc:pssh")) && (paramXmlPullParser.next() == 4))
    {
      j = 1;
      localObject4 = Base64.decode(paramXmlPullParser.getText(), 0);
      localObject3 = PsshAtomUtil.parseUuid((byte[])localObject4);
      bool2 = bool1;
    }
    do
    {
      localObject2 = localObject4;
      bool1 = bool2;
      i = j;
      localObject1 = localObject3;
      if (!XmlPullParserUtil.isEndTag(paramXmlPullParser, "ContentProtection")) {
        break;
      }
      if (j != 0) {
        break label171;
      }
      return null;
      localObject4 = localObject2;
      bool2 = bool1;
      j = i;
      localObject3 = localObject1;
    } while (!XmlPullParserUtil.isStartTag(paramXmlPullParser, "widevine:license"));
    Object localObject3 = paramXmlPullParser.getAttributeValue(null, "robustness_level");
    if ((localObject3 != null) && (((String)localObject3).startsWith("HW"))) {}
    for (bool1 = true;; bool1 = false)
    {
      localObject4 = localObject2;
      bool2 = bool1;
      j = i;
      localObject3 = localObject1;
      break;
    }
    label171:
    if (localObject3 != null) {
      return new DrmInitData.SchemeData((UUID)localObject3, "video/mp4", (byte[])localObject4, bool2);
    }
    Log.w("MpdParser", "Skipped unsupported ContentProtection element");
    return null;
  }
  
  protected int parseContentType(XmlPullParser paramXmlPullParser)
  {
    paramXmlPullParser = paramXmlPullParser.getAttributeValue(null, "contentType");
    if (TextUtils.isEmpty(paramXmlPullParser)) {}
    do
    {
      return -1;
      if ("audio".equals(paramXmlPullParser)) {
        return 1;
      }
      if ("video".equals(paramXmlPullParser)) {
        return 2;
      }
    } while (!"text".equals(paramXmlPullParser));
    return 3;
  }
  
  protected SchemeValuePair parseInbandEventStream(XmlPullParser paramXmlPullParser)
    throws XmlPullParserException, IOException
  {
    return parseSchemeValuePair(paramXmlPullParser, "InbandEventStream");
  }
  
  protected RangedUri parseInitialization(XmlPullParser paramXmlPullParser)
  {
    return parseRangedUrl(paramXmlPullParser, "sourceURL", "range");
  }
  
  protected DashManifest parseMediaPresentationDescription(XmlPullParser paramXmlPullParser, String paramString)
    throws XmlPullParserException, IOException
  {
    long l7 = parseDateTime(paramXmlPullParser, "availabilityStartTime", -9223372036854775807L);
    long l6 = parseDuration(paramXmlPullParser, "mediaPresentationDuration", -9223372036854775807L);
    long l8 = parseDuration(paramXmlPullParser, "minBufferTime", -9223372036854775807L);
    Object localObject1 = paramXmlPullParser.getAttributeValue(null, "type");
    boolean bool;
    long l2;
    label84:
    long l3;
    label101:
    long l4;
    label118:
    Object localObject2;
    ArrayList localArrayList;
    long l1;
    label143:
    int j;
    int i;
    long l5;
    Object localObject3;
    Object localObject4;
    int k;
    int m;
    String str;
    if ((localObject1 != null) && (((String)localObject1).equals("dynamic")))
    {
      bool = true;
      if (!bool) {
        break label306;
      }
      l2 = parseDuration(paramXmlPullParser, "minimumUpdatePeriod", -9223372036854775807L);
      if (!bool) {
        break label314;
      }
      l3 = parseDuration(paramXmlPullParser, "timeShiftBufferDepth", -9223372036854775807L);
      if (!bool) {
        break label322;
      }
      l4 = parseDuration(paramXmlPullParser, "suggestedPresentationDelay", -9223372036854775807L);
      localObject2 = null;
      localObject1 = null;
      localArrayList = new ArrayList();
      if (!bool) {
        break label330;
      }
      l1 = -9223372036854775807L;
      j = 0;
      i = 0;
      l5 = l1;
      label221:
      do
      {
        paramXmlPullParser.next();
        if (!XmlPullParserUtil.isStartTag(paramXmlPullParser, "BaseURL")) {
          break;
        }
        localObject3 = localObject2;
        localObject4 = localObject1;
        l1 = l5;
        k = j;
        m = i;
        str = paramString;
        if (i == 0)
        {
          str = parseBaseUrl(paramXmlPullParser, paramString);
          m = 1;
          k = j;
          l1 = l5;
          localObject4 = localObject1;
          localObject3 = localObject2;
        }
        localObject2 = localObject3;
        localObject1 = localObject4;
        l5 = l1;
        j = k;
        i = m;
        paramString = str;
      } while (!XmlPullParserUtil.isEndTag(paramXmlPullParser, "MPD"));
      l5 = l6;
      if (l6 == -9223372036854775807L)
      {
        if (l1 == -9223372036854775807L) {
          break label641;
        }
        l5 = l1;
      }
    }
    for (;;)
    {
      if (localArrayList.isEmpty())
      {
        throw new ParserException("No periods found.");
        bool = false;
        break;
        label306:
        l2 = -9223372036854775807L;
        break label84;
        label314:
        l3 = -9223372036854775807L;
        break label101;
        label322:
        l4 = -9223372036854775807L;
        break label118;
        label330:
        l1 = 0L;
        break label143;
        if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "UTCTiming"))
        {
          localObject3 = parseUtcTiming(paramXmlPullParser);
          localObject4 = localObject1;
          l1 = l5;
          k = j;
          m = i;
          str = paramString;
          break label221;
        }
        if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "Location"))
        {
          localObject4 = Uri.parse(paramXmlPullParser.nextText());
          localObject3 = localObject2;
          l1 = l5;
          k = j;
          m = i;
          str = paramString;
          break label221;
        }
        localObject3 = localObject2;
        localObject4 = localObject1;
        l1 = l5;
        k = j;
        m = i;
        str = paramString;
        if (!XmlPullParserUtil.isStartTag(paramXmlPullParser, "Period")) {
          break label221;
        }
        localObject3 = localObject2;
        localObject4 = localObject1;
        l1 = l5;
        k = j;
        m = i;
        str = paramString;
        if (j != 0) {
          break label221;
        }
        localObject3 = parsePeriod(paramXmlPullParser, paramString, l5);
        localObject4 = (Period)((Pair)localObject3).first;
        if (((Period)localObject4).startMs == -9223372036854775807L)
        {
          if (bool)
          {
            k = 1;
            localObject3 = localObject2;
            localObject4 = localObject1;
            l1 = l5;
            m = i;
            str = paramString;
            break label221;
          }
          throw new ParserException("Unable to determine start of period " + localArrayList.size());
        }
        l1 = ((Long)((Pair)localObject3).second).longValue();
        if (l1 == -9223372036854775807L) {}
        for (l1 = -9223372036854775807L;; l1 = ((Period)localObject4).startMs + l1)
        {
          localArrayList.add(localObject4);
          localObject3 = localObject2;
          localObject4 = localObject1;
          k = j;
          m = i;
          str = paramString;
          break;
        }
        label641:
        l5 = l6;
        if (!bool) {
          throw new ParserException("Unable to determine duration of static manifest.");
        }
      }
    }
    return buildMediaPresentationDescription(l7, l5, l8, bool, l2, l3, l4, (UtcTimingElement)localObject3, (Uri)localObject4, localArrayList);
  }
  
  protected Pair<Period, Long> parsePeriod(XmlPullParser paramXmlPullParser, String paramString, long paramLong)
    throws XmlPullParserException, IOException
  {
    String str2 = paramXmlPullParser.getAttributeValue(null, "id");
    paramLong = parseDuration(paramXmlPullParser, "start", paramLong);
    long l = parseDuration(paramXmlPullParser, "duration", -9223372036854775807L);
    String str1 = null;
    ArrayList localArrayList = new ArrayList();
    int i = 0;
    Object localObject1 = paramString;
    paramXmlPullParser.next();
    int j;
    Object localObject2;
    if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "BaseURL"))
    {
      j = i;
      paramString = str1;
      localObject2 = localObject1;
      if (i == 0)
      {
        localObject2 = parseBaseUrl(paramXmlPullParser, (String)localObject1);
        j = 1;
        paramString = str1;
      }
    }
    for (;;)
    {
      i = j;
      str1 = paramString;
      localObject1 = localObject2;
      if (!XmlPullParserUtil.isEndTag(paramXmlPullParser, "Period")) {
        break;
      }
      return Pair.create(buildPeriod(str2, paramLong, localArrayList), Long.valueOf(l));
      if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "AdaptationSet"))
      {
        localArrayList.add(parseAdaptationSet(paramXmlPullParser, (String)localObject1, str1));
        j = i;
        paramString = str1;
        localObject2 = localObject1;
      }
      else if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "SegmentBase"))
      {
        paramString = parseSegmentBase(paramXmlPullParser, null);
        j = i;
        localObject2 = localObject1;
      }
      else if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "SegmentList"))
      {
        paramString = parseSegmentList(paramXmlPullParser, null);
        j = i;
        localObject2 = localObject1;
      }
      else
      {
        j = i;
        paramString = str1;
        localObject2 = localObject1;
        if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "SegmentTemplate"))
        {
          paramString = parseSegmentTemplate(paramXmlPullParser, null);
          j = i;
          localObject2 = localObject1;
        }
      }
    }
  }
  
  protected RangedUri parseRangedUrl(XmlPullParser paramXmlPullParser, String paramString1, String paramString2)
  {
    paramString1 = paramXmlPullParser.getAttributeValue(null, paramString1);
    long l1 = 0L;
    long l3 = -1L;
    paramXmlPullParser = paramXmlPullParser.getAttributeValue(null, paramString2);
    long l2 = l3;
    if (paramXmlPullParser != null)
    {
      paramXmlPullParser = paramXmlPullParser.split("-");
      long l4 = Long.parseLong(paramXmlPullParser[0]);
      l1 = l4;
      l2 = l3;
      if (paramXmlPullParser.length == 2)
      {
        l2 = Long.parseLong(paramXmlPullParser[1]) - l4 + 1L;
        l1 = l4;
      }
    }
    return buildRangedUri(paramString1, l1, l2);
  }
  
  protected RepresentationInfo parseRepresentation(XmlPullParser paramXmlPullParser, String paramString1, String paramString2, String paramString3, int paramInt1, int paramInt2, float paramFloat, int paramInt3, int paramInt4, String paramString4, int paramInt5, List<SchemeValuePair> paramList, SegmentBase paramSegmentBase)
    throws XmlPullParserException, IOException
  {
    String str1 = paramXmlPullParser.getAttributeValue(null, "id");
    int i = parseInt(paramXmlPullParser, "bandwidth", -1);
    String str2 = parseString(paramXmlPullParser, "mimeType", paramString2);
    String str3 = parseString(paramXmlPullParser, "codecs", paramString3);
    int j = parseInt(paramXmlPullParser, "width", paramInt1);
    int k = parseInt(paramXmlPullParser, "height", paramInt2);
    paramFloat = parseFrameRate(paramXmlPullParser, paramFloat);
    paramInt2 = paramInt3;
    int m = parseInt(paramXmlPullParser, "audioSamplingRate", paramInt4);
    ArrayList localArrayList1 = new ArrayList();
    ArrayList localArrayList2 = new ArrayList();
    paramInt1 = 0;
    paramString3 = paramString1;
    do
    {
      paramXmlPullParser.next();
      if (!XmlPullParserUtil.isStartTag(paramXmlPullParser, "BaseURL")) {
        break;
      }
      paramInt3 = paramInt2;
      paramInt4 = paramInt1;
      paramString2 = paramString3;
      paramString1 = paramSegmentBase;
      if (paramInt1 == 0)
      {
        paramString2 = parseBaseUrl(paramXmlPullParser, paramString3);
        paramInt4 = 1;
        paramString1 = paramSegmentBase;
        paramInt3 = paramInt2;
      }
      paramInt2 = paramInt3;
      paramInt1 = paramInt4;
      paramString3 = paramString2;
      paramSegmentBase = paramString1;
    } while (!XmlPullParserUtil.isEndTag(paramXmlPullParser, "Representation"));
    paramXmlPullParser = buildFormat(str1, str2, j, k, paramFloat, paramInt3, m, i, paramString4, paramInt5, paramList, str3);
    if (paramString1 != null) {}
    for (;;)
    {
      return new RepresentationInfo(paramXmlPullParser, paramString2, paramString1, localArrayList1, localArrayList2);
      if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "AudioChannelConfiguration"))
      {
        paramInt3 = parseAudioChannelConfiguration(paramXmlPullParser);
        paramInt4 = paramInt1;
        paramString2 = paramString3;
        paramString1 = paramSegmentBase;
        break;
      }
      if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "SegmentBase"))
      {
        paramString1 = parseSegmentBase(paramXmlPullParser, (SegmentBase.SingleSegmentBase)paramSegmentBase);
        paramInt3 = paramInt2;
        paramInt4 = paramInt1;
        paramString2 = paramString3;
        break;
      }
      if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "SegmentList"))
      {
        paramString1 = parseSegmentList(paramXmlPullParser, (SegmentBase.SegmentList)paramSegmentBase);
        paramInt3 = paramInt2;
        paramInt4 = paramInt1;
        paramString2 = paramString3;
        break;
      }
      if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "SegmentTemplate"))
      {
        paramString1 = parseSegmentTemplate(paramXmlPullParser, (SegmentBase.SegmentTemplate)paramSegmentBase);
        paramInt3 = paramInt2;
        paramInt4 = paramInt1;
        paramString2 = paramString3;
        break;
      }
      if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "ContentProtection"))
      {
        DrmInitData.SchemeData localSchemeData = parseContentProtection(paramXmlPullParser);
        paramInt3 = paramInt2;
        paramInt4 = paramInt1;
        paramString2 = paramString3;
        paramString1 = paramSegmentBase;
        if (localSchemeData == null) {
          break;
        }
        localArrayList1.add(localSchemeData);
        paramInt3 = paramInt2;
        paramInt4 = paramInt1;
        paramString2 = paramString3;
        paramString1 = paramSegmentBase;
        break;
      }
      paramInt3 = paramInt2;
      paramInt4 = paramInt1;
      paramString2 = paramString3;
      paramString1 = paramSegmentBase;
      if (!XmlPullParserUtil.isStartTag(paramXmlPullParser, "InbandEventStream")) {
        break;
      }
      localArrayList2.add(parseInbandEventStream(paramXmlPullParser));
      paramInt3 = paramInt2;
      paramInt4 = paramInt1;
      paramString2 = paramString3;
      paramString1 = paramSegmentBase;
      break;
      paramString1 = new SegmentBase.SingleSegmentBase();
    }
  }
  
  protected int parseRole(XmlPullParser paramXmlPullParser)
    throws XmlPullParserException, IOException
  {
    String str1 = parseString(paramXmlPullParser, "schemeIdUri", null);
    String str2 = parseString(paramXmlPullParser, "value", null);
    do
    {
      paramXmlPullParser.next();
    } while (!XmlPullParserUtil.isEndTag(paramXmlPullParser, "Role"));
    if (("urn:mpeg:dash:role:2011".equals(str1)) && ("main".equals(str2))) {
      return 1;
    }
    return 0;
  }
  
  protected SegmentBase.SingleSegmentBase parseSegmentBase(XmlPullParser paramXmlPullParser, SegmentBase.SingleSegmentBase paramSingleSegmentBase)
    throws XmlPullParserException, IOException
  {
    long l1;
    long l3;
    label28:
    long l4;
    label47:
    long l2;
    label57:
    Object localObject;
    if (paramSingleSegmentBase != null)
    {
      l1 = paramSingleSegmentBase.timescale;
      l3 = parseLong(paramXmlPullParser, "timescale", l1);
      if (paramSingleSegmentBase == null) {
        break label173;
      }
      l1 = paramSingleSegmentBase.presentationTimeOffset;
      l4 = parseLong(paramXmlPullParser, "presentationTimeOffset", l1);
      if (paramSingleSegmentBase == null) {
        break label178;
      }
      l1 = paramSingleSegmentBase.indexStart;
      if (paramSingleSegmentBase == null) {
        break label183;
      }
      l2 = paramSingleSegmentBase.indexLength;
      localObject = paramXmlPullParser.getAttributeValue(null, "indexRange");
      if (localObject != null)
      {
        localObject = ((String)localObject).split("-");
        l1 = Long.parseLong(localObject[0]);
        l2 = Long.parseLong(localObject[1]) - l1 + 1L;
      }
      if (paramSingleSegmentBase == null) {
        break label189;
      }
      paramSingleSegmentBase = paramSingleSegmentBase.initialization;
    }
    for (;;)
    {
      paramXmlPullParser.next();
      localObject = paramSingleSegmentBase;
      if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "Initialization")) {
        localObject = parseInitialization(paramXmlPullParser);
      }
      paramSingleSegmentBase = (SegmentBase.SingleSegmentBase)localObject;
      if (XmlPullParserUtil.isEndTag(paramXmlPullParser, "SegmentBase"))
      {
        return buildSingleSegmentBase((RangedUri)localObject, l3, l4, l1, l2);
        l1 = 1L;
        break;
        label173:
        l1 = 0L;
        break label28;
        label178:
        l1 = 0L;
        break label47;
        label183:
        l2 = 0L;
        break label57;
        label189:
        paramSingleSegmentBase = null;
      }
    }
  }
  
  protected SegmentBase.SegmentList parseSegmentList(XmlPullParser paramXmlPullParser, SegmentBase.SegmentList paramSegmentList)
    throws XmlPullParserException, IOException
  {
    long l1;
    long l2;
    label31:
    long l3;
    label52:
    int i;
    label72:
    Object localObject6;
    Object localObject5;
    Object localObject4;
    Object localObject2;
    Object localObject3;
    Object localObject1;
    if (paramSegmentList != null)
    {
      l1 = paramSegmentList.timescale;
      l2 = parseLong(paramXmlPullParser, "timescale", l1);
      if (paramSegmentList == null) {
        break label208;
      }
      l1 = paramSegmentList.presentationTimeOffset;
      l3 = parseLong(paramXmlPullParser, "presentationTimeOffset", l1);
      if (paramSegmentList == null) {
        break label214;
      }
      l1 = paramSegmentList.duration;
      l1 = parseLong(paramXmlPullParser, "duration", l1);
      if (paramSegmentList == null) {
        break label222;
      }
      i = paramSegmentList.startNumber;
      i = parseInt(paramXmlPullParser, "startNumber", i);
      localObject6 = null;
      localObject5 = null;
      localObject4 = null;
      label122:
      do
      {
        paramXmlPullParser.next();
        if (!XmlPullParserUtil.isStartTag(paramXmlPullParser, "Initialization")) {
          break;
        }
        localObject2 = parseInitialization(paramXmlPullParser);
        localObject3 = localObject4;
        localObject1 = localObject5;
        localObject6 = localObject2;
        localObject5 = localObject1;
        localObject4 = localObject3;
      } while (!XmlPullParserUtil.isEndTag(paramXmlPullParser, "SegmentList"));
      paramXmlPullParser = (XmlPullParser)localObject2;
      localObject4 = localObject1;
      localObject5 = localObject3;
      if (paramSegmentList != null)
      {
        if (localObject2 == null) {
          break label319;
        }
        label164:
        if (localObject1 == null) {
          break label328;
        }
        label169:
        if (localObject3 == null) {
          break label337;
        }
        localObject5 = localObject3;
        localObject4 = localObject1;
        paramXmlPullParser = (XmlPullParser)localObject2;
      }
    }
    for (;;)
    {
      return buildSegmentList(paramXmlPullParser, l2, l3, i, l1, (List)localObject4, (List)localObject5);
      l1 = 1L;
      break;
      label208:
      l1 = 0L;
      break label31;
      label214:
      l1 = -9223372036854775807L;
      break label52;
      label222:
      i = 1;
      break label72;
      if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "SegmentTimeline"))
      {
        localObject1 = parseSegmentTimeline(paramXmlPullParser);
        localObject2 = localObject6;
        localObject3 = localObject4;
        break label122;
      }
      localObject2 = localObject6;
      localObject1 = localObject5;
      localObject3 = localObject4;
      if (!XmlPullParserUtil.isStartTag(paramXmlPullParser, "SegmentURL")) {
        break label122;
      }
      localObject3 = localObject4;
      if (localObject4 == null) {
        localObject3 = new ArrayList();
      }
      ((List)localObject3).add(parseSegmentUrl(paramXmlPullParser));
      localObject2 = localObject6;
      localObject1 = localObject5;
      break label122;
      label319:
      localObject2 = paramSegmentList.initialization;
      break label164;
      label328:
      localObject1 = paramSegmentList.segmentTimeline;
      break label169;
      label337:
      localObject5 = paramSegmentList.mediaSegments;
      paramXmlPullParser = (XmlPullParser)localObject2;
      localObject4 = localObject1;
    }
  }
  
  protected SegmentBase.SegmentTemplate parseSegmentTemplate(XmlPullParser paramXmlPullParser, SegmentBase.SegmentTemplate paramSegmentTemplate)
    throws XmlPullParserException, IOException
  {
    long l1;
    long l2;
    label31:
    long l3;
    label52:
    int i;
    label72:
    Object localObject1;
    label91:
    UrlTemplate localUrlTemplate1;
    label113:
    UrlTemplate localUrlTemplate2;
    Object localObject4;
    Object localObject3;
    Object localObject2;
    if (paramSegmentTemplate != null)
    {
      l1 = paramSegmentTemplate.timescale;
      l2 = parseLong(paramXmlPullParser, "timescale", l1);
      if (paramSegmentTemplate == null) {
        break label230;
      }
      l1 = paramSegmentTemplate.presentationTimeOffset;
      l3 = parseLong(paramXmlPullParser, "presentationTimeOffset", l1);
      if (paramSegmentTemplate == null) {
        break label236;
      }
      l1 = paramSegmentTemplate.duration;
      l1 = parseLong(paramXmlPullParser, "duration", l1);
      if (paramSegmentTemplate == null) {
        break label244;
      }
      i = paramSegmentTemplate.startNumber;
      i = parseInt(paramXmlPullParser, "startNumber", i);
      if (paramSegmentTemplate == null) {
        break label249;
      }
      localObject1 = paramSegmentTemplate.mediaTemplate;
      localUrlTemplate1 = parseUrlTemplate(paramXmlPullParser, "media", (UrlTemplate)localObject1);
      if (paramSegmentTemplate == null) {
        break label255;
      }
      localObject1 = paramSegmentTemplate.initializationTemplate;
      localUrlTemplate2 = parseUrlTemplate(paramXmlPullParser, "initialization", (UrlTemplate)localObject1);
      localObject4 = null;
      localObject3 = null;
      label159:
      do
      {
        paramXmlPullParser.next();
        if (!XmlPullParserUtil.isStartTag(paramXmlPullParser, "Initialization")) {
          break;
        }
        localObject1 = parseInitialization(paramXmlPullParser);
        localObject2 = localObject3;
        localObject4 = localObject1;
        localObject3 = localObject2;
      } while (!XmlPullParserUtil.isEndTag(paramXmlPullParser, "SegmentTemplate"));
      paramXmlPullParser = (XmlPullParser)localObject1;
      localObject3 = localObject2;
      if (paramSegmentTemplate != null)
      {
        if (localObject1 == null) {
          break label293;
        }
        label193:
        if (localObject2 == null) {
          break label302;
        }
        localObject3 = localObject2;
      }
    }
    for (paramXmlPullParser = (XmlPullParser)localObject1;; paramXmlPullParser = (XmlPullParser)localObject1)
    {
      return buildSegmentTemplate(paramXmlPullParser, l2, l3, i, l1, (List)localObject3, localUrlTemplate2, localUrlTemplate1);
      l1 = 1L;
      break;
      label230:
      l1 = 0L;
      break label31;
      label236:
      l1 = -9223372036854775807L;
      break label52;
      label244:
      i = 1;
      break label72;
      label249:
      localObject1 = null;
      break label91;
      label255:
      localObject1 = null;
      break label113;
      localObject1 = localObject4;
      localObject2 = localObject3;
      if (!XmlPullParserUtil.isStartTag(paramXmlPullParser, "SegmentTimeline")) {
        break label159;
      }
      localObject2 = parseSegmentTimeline(paramXmlPullParser);
      localObject1 = localObject4;
      break label159;
      label293:
      localObject1 = paramSegmentTemplate.initialization;
      break label193;
      label302:
      localObject3 = paramSegmentTemplate.segmentTimeline;
    }
  }
  
  protected List<SegmentBase.SegmentTimelineElement> parseSegmentTimeline(XmlPullParser paramXmlPullParser)
    throws XmlPullParserException, IOException
  {
    ArrayList localArrayList = new ArrayList();
    long l1 = 0L;
    do
    {
      paramXmlPullParser.next();
      long l2 = l1;
      if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "S"))
      {
        l1 = parseLong(paramXmlPullParser, "t", l1);
        long l3 = parseLong(paramXmlPullParser, "d", -9223372036854775807L);
        int j = parseInt(paramXmlPullParser, "r", 0);
        int i = 0;
        for (;;)
        {
          l2 = l1;
          if (i >= j + 1) {
            break;
          }
          localArrayList.add(buildSegmentTimelineElement(l1, l3));
          l1 += l3;
          i += 1;
        }
      }
      l1 = l2;
    } while (!XmlPullParserUtil.isEndTag(paramXmlPullParser, "SegmentTimeline"));
    return localArrayList;
  }
  
  protected RangedUri parseSegmentUrl(XmlPullParser paramXmlPullParser)
  {
    return parseRangedUrl(paramXmlPullParser, "media", "mediaRange");
  }
  
  protected UrlTemplate parseUrlTemplate(XmlPullParser paramXmlPullParser, String paramString, UrlTemplate paramUrlTemplate)
  {
    paramXmlPullParser = paramXmlPullParser.getAttributeValue(null, paramString);
    if (paramXmlPullParser != null) {
      paramUrlTemplate = UrlTemplate.compile(paramXmlPullParser);
    }
    return paramUrlTemplate;
  }
  
  protected UtcTimingElement parseUtcTiming(XmlPullParser paramXmlPullParser)
  {
    return buildUtcTimingElement(paramXmlPullParser.getAttributeValue(null, "schemeIdUri"), paramXmlPullParser.getAttributeValue(null, "value"));
  }
  
  private static final class RepresentationInfo
  {
    public final String baseUrl;
    public final ArrayList<DrmInitData.SchemeData> drmSchemeDatas;
    public final Format format;
    public final ArrayList<SchemeValuePair> inbandEventStreams;
    public final SegmentBase segmentBase;
    
    public RepresentationInfo(Format paramFormat, String paramString, SegmentBase paramSegmentBase, ArrayList<DrmInitData.SchemeData> paramArrayList, ArrayList<SchemeValuePair> paramArrayList1)
    {
      this.format = paramFormat;
      this.baseUrl = paramString;
      this.segmentBase = paramSegmentBase;
      this.drmSchemeDatas = paramArrayList;
      this.inbandEventStreams = paramArrayList1;
    }
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/source/dash/manifest/DashManifestParser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */