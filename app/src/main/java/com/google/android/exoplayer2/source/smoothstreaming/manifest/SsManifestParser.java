package com.google.android.exoplayer2.source.smoothstreaming.manifest;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Pair;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.drm.DrmInitData;
import com.google.android.exoplayer2.drm.DrmInitData.SchemeData;
import com.google.android.exoplayer2.extractor.mp4.PsshAtomUtil;
import com.google.android.exoplayer2.upstream.ParsingLoadable.Parser;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.CodecSpecificDataUtil;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class SsManifestParser
  implements ParsingLoadable.Parser<SsManifest>
{
  private final XmlPullParserFactory xmlParserFactory;
  
  public SsManifestParser()
  {
    try
    {
      this.xmlParserFactory = XmlPullParserFactory.newInstance();
      return;
    }
    catch (XmlPullParserException localXmlPullParserException)
    {
      throw new RuntimeException("Couldn't create XmlPullParserFactory instance", localXmlPullParserException);
    }
  }
  
  public SsManifest parse(Uri paramUri, InputStream paramInputStream)
    throws IOException
  {
    try
    {
      XmlPullParser localXmlPullParser = this.xmlParserFactory.newPullParser();
      localXmlPullParser.setInput(paramInputStream, null);
      paramUri = (SsManifest)new SmoothStreamingMediaParser(null, paramUri.toString()).parse(localXmlPullParser);
      return paramUri;
    }
    catch (XmlPullParserException paramUri)
    {
      throw new ParserException(paramUri);
    }
  }
  
  private static abstract class ElementParser
  {
    private final String baseUri;
    private final List<Pair<String, Object>> normalizedAttributes;
    private final ElementParser parent;
    private final String tag;
    
    public ElementParser(ElementParser paramElementParser, String paramString1, String paramString2)
    {
      this.parent = paramElementParser;
      this.baseUri = paramString1;
      this.tag = paramString2;
      this.normalizedAttributes = new LinkedList();
    }
    
    private ElementParser newChildParser(ElementParser paramElementParser, String paramString1, String paramString2)
    {
      if ("QualityLevel".equals(paramString1)) {
        return new SsManifestParser.QualityLevelParser(paramElementParser, paramString2);
      }
      if ("Protection".equals(paramString1)) {
        return new SsManifestParser.ProtectionParser(paramElementParser, paramString2);
      }
      if ("StreamIndex".equals(paramString1)) {
        return new SsManifestParser.StreamIndexParser(paramElementParser, paramString2);
      }
      return null;
    }
    
    protected void addChild(Object paramObject) {}
    
    protected abstract Object build();
    
    protected final Object getNormalizedAttribute(String paramString)
    {
      int i = 0;
      while (i < this.normalizedAttributes.size())
      {
        Pair localPair = (Pair)this.normalizedAttributes.get(i);
        if (((String)localPair.first).equals(paramString)) {
          return localPair.second;
        }
        i += 1;
      }
      if (this.parent == null) {
        return null;
      }
      return this.parent.getNormalizedAttribute(paramString);
    }
    
    protected boolean handleChildInline(String paramString)
    {
      return false;
    }
    
    public final Object parse(XmlPullParser paramXmlPullParser)
      throws XmlPullParserException, IOException
    {
      int j = 0;
      int m = 0;
      int i;
      int k;
      switch (paramXmlPullParser.getEventType())
      {
      default: 
        i = m;
        k = j;
      case 2: 
      case 4: 
      case 3: 
        Object localObject;
        label251:
        do
        {
          for (;;)
          {
            paramXmlPullParser.next();
            j = k;
            m = i;
            break;
            localObject = paramXmlPullParser.getName();
            if (this.tag.equals(localObject))
            {
              k = 1;
              parseStartTag(paramXmlPullParser);
              i = m;
            }
            else
            {
              k = j;
              i = m;
              if (j != 0) {
                if (m > 0)
                {
                  i = m + 1;
                  k = j;
                }
                else if (handleChildInline((String)localObject))
                {
                  parseStartTag(paramXmlPullParser);
                  k = j;
                  i = m;
                }
                else
                {
                  localObject = newChildParser(this, (String)localObject, this.baseUri);
                  if (localObject == null)
                  {
                    i = 1;
                    k = j;
                  }
                  else
                  {
                    addChild(((ElementParser)localObject).parse(paramXmlPullParser));
                    k = j;
                    i = m;
                    continue;
                    k = j;
                    i = m;
                    if (j != 0)
                    {
                      k = j;
                      i = m;
                      if (m == 0)
                      {
                        parseText(paramXmlPullParser);
                        k = j;
                        i = m;
                        continue;
                        k = j;
                        i = m;
                        if (j != 0)
                        {
                          if (m <= 0) {
                            break label251;
                          }
                          i = m - 1;
                          k = j;
                        }
                      }
                    }
                  }
                }
              }
            }
          }
          localObject = paramXmlPullParser.getName();
          parseEndTag(paramXmlPullParser);
          k = j;
          i = m;
        } while (handleChildInline((String)localObject));
        return build();
      }
      return null;
    }
    
    protected final boolean parseBoolean(XmlPullParser paramXmlPullParser, String paramString, boolean paramBoolean)
    {
      paramXmlPullParser = paramXmlPullParser.getAttributeValue(null, paramString);
      if (paramXmlPullParser != null) {
        paramBoolean = Boolean.parseBoolean(paramXmlPullParser);
      }
      return paramBoolean;
    }
    
    protected void parseEndTag(XmlPullParser paramXmlPullParser) {}
    
    protected final int parseInt(XmlPullParser paramXmlPullParser, String paramString, int paramInt)
      throws ParserException
    {
      paramXmlPullParser = paramXmlPullParser.getAttributeValue(null, paramString);
      if (paramXmlPullParser != null) {}
      try
      {
        paramInt = Integer.parseInt(paramXmlPullParser);
        return paramInt;
      }
      catch (NumberFormatException paramXmlPullParser)
      {
        throw new ParserException(paramXmlPullParser);
      }
    }
    
    protected final long parseLong(XmlPullParser paramXmlPullParser, String paramString, long paramLong)
      throws ParserException
    {
      paramXmlPullParser = paramXmlPullParser.getAttributeValue(null, paramString);
      if (paramXmlPullParser != null) {}
      try
      {
        paramLong = Long.parseLong(paramXmlPullParser);
        return paramLong;
      }
      catch (NumberFormatException paramXmlPullParser)
      {
        throw new ParserException(paramXmlPullParser);
      }
    }
    
    protected final int parseRequiredInt(XmlPullParser paramXmlPullParser, String paramString)
      throws ParserException
    {
      paramXmlPullParser = paramXmlPullParser.getAttributeValue(null, paramString);
      if (paramXmlPullParser != null) {
        try
        {
          int i = Integer.parseInt(paramXmlPullParser);
          return i;
        }
        catch (NumberFormatException paramXmlPullParser)
        {
          throw new ParserException(paramXmlPullParser);
        }
      }
      throw new SsManifestParser.MissingFieldException(paramString);
    }
    
    protected final long parseRequiredLong(XmlPullParser paramXmlPullParser, String paramString)
      throws ParserException
    {
      paramXmlPullParser = paramXmlPullParser.getAttributeValue(null, paramString);
      if (paramXmlPullParser != null) {
        try
        {
          long l = Long.parseLong(paramXmlPullParser);
          return l;
        }
        catch (NumberFormatException paramXmlPullParser)
        {
          throw new ParserException(paramXmlPullParser);
        }
      }
      throw new SsManifestParser.MissingFieldException(paramString);
    }
    
    protected final String parseRequiredString(XmlPullParser paramXmlPullParser, String paramString)
      throws SsManifestParser.MissingFieldException
    {
      paramXmlPullParser = paramXmlPullParser.getAttributeValue(null, paramString);
      if (paramXmlPullParser != null) {
        return paramXmlPullParser;
      }
      throw new SsManifestParser.MissingFieldException(paramString);
    }
    
    protected void parseStartTag(XmlPullParser paramXmlPullParser)
      throws ParserException
    {}
    
    protected void parseText(XmlPullParser paramXmlPullParser) {}
    
    protected final void putNormalizedAttribute(String paramString, Object paramObject)
    {
      this.normalizedAttributes.add(Pair.create(paramString, paramObject));
    }
  }
  
  public static class MissingFieldException
    extends ParserException
  {
    public MissingFieldException(String paramString)
    {
      super();
    }
  }
  
  private static class ProtectionParser
    extends SsManifestParser.ElementParser
  {
    public static final String KEY_SYSTEM_ID = "SystemID";
    public static final String TAG = "Protection";
    public static final String TAG_PROTECTION_HEADER = "ProtectionHeader";
    private boolean inProtectionHeader;
    private byte[] initData;
    private UUID uuid;
    
    public ProtectionParser(SsManifestParser.ElementParser paramElementParser, String paramString)
    {
      super(paramString, "Protection");
    }
    
    private static String stripCurlyBraces(String paramString)
    {
      String str = paramString;
      if (paramString.charAt(0) == '{')
      {
        str = paramString;
        if (paramString.charAt(paramString.length() - 1) == '}') {
          str = paramString.substring(1, paramString.length() - 1);
        }
      }
      return str;
    }
    
    public Object build()
    {
      return new SsManifest.ProtectionElement(this.uuid, PsshAtomUtil.buildPsshAtom(this.uuid, this.initData));
    }
    
    public boolean handleChildInline(String paramString)
    {
      return "ProtectionHeader".equals(paramString);
    }
    
    public void parseEndTag(XmlPullParser paramXmlPullParser)
    {
      if ("ProtectionHeader".equals(paramXmlPullParser.getName())) {
        this.inProtectionHeader = false;
      }
    }
    
    public void parseStartTag(XmlPullParser paramXmlPullParser)
    {
      if ("ProtectionHeader".equals(paramXmlPullParser.getName()))
      {
        this.inProtectionHeader = true;
        this.uuid = UUID.fromString(stripCurlyBraces(paramXmlPullParser.getAttributeValue(null, "SystemID")));
      }
    }
    
    public void parseText(XmlPullParser paramXmlPullParser)
    {
      if (this.inProtectionHeader) {
        this.initData = Base64.decode(paramXmlPullParser.getText(), 0);
      }
    }
  }
  
  private static class QualityLevelParser
    extends SsManifestParser.ElementParser
  {
    private static final String KEY_BITRATE = "Bitrate";
    private static final String KEY_CHANNELS = "Channels";
    private static final String KEY_CODEC_PRIVATE_DATA = "CodecPrivateData";
    private static final String KEY_FOUR_CC = "FourCC";
    private static final String KEY_INDEX = "Index";
    private static final String KEY_LANGUAGE = "Language";
    private static final String KEY_MAX_HEIGHT = "MaxHeight";
    private static final String KEY_MAX_WIDTH = "MaxWidth";
    private static final String KEY_SAMPLING_RATE = "SamplingRate";
    private static final String KEY_TYPE = "Type";
    public static final String TAG = "QualityLevel";
    private Format format;
    
    public QualityLevelParser(SsManifestParser.ElementParser paramElementParser, String paramString)
    {
      super(paramString, "QualityLevel");
    }
    
    private static List<byte[]> buildCodecSpecificData(String paramString)
    {
      ArrayList localArrayList = new ArrayList();
      byte[][] arrayOfByte;
      if (!TextUtils.isEmpty(paramString))
      {
        paramString = Util.getBytesFromHexString(paramString);
        arrayOfByte = CodecSpecificDataUtil.splitNalUnits(paramString);
        if (arrayOfByte == null) {
          localArrayList.add(paramString);
        }
      }
      else
      {
        return localArrayList;
      }
      Collections.addAll(localArrayList, arrayOfByte);
      return localArrayList;
    }
    
    private static String fourCCToMimeType(String paramString)
    {
      if ((paramString.equalsIgnoreCase("H264")) || (paramString.equalsIgnoreCase("X264")) || (paramString.equalsIgnoreCase("AVC1")) || (paramString.equalsIgnoreCase("DAVC"))) {
        return "video/avc";
      }
      if ((paramString.equalsIgnoreCase("AAC")) || (paramString.equalsIgnoreCase("AACL")) || (paramString.equalsIgnoreCase("AACH")) || (paramString.equalsIgnoreCase("AACP"))) {
        return "audio/mp4a-latm";
      }
      if (paramString.equalsIgnoreCase("TTML")) {
        return "application/ttml+xml";
      }
      if ((paramString.equalsIgnoreCase("ac-3")) || (paramString.equalsIgnoreCase("dac3"))) {
        return "audio/ac3";
      }
      if ((paramString.equalsIgnoreCase("ec-3")) || (paramString.equalsIgnoreCase("dec3"))) {
        return "audio/eac3";
      }
      if (paramString.equalsIgnoreCase("dtsc")) {
        return "audio/vnd.dts";
      }
      if ((paramString.equalsIgnoreCase("dtsh")) || (paramString.equalsIgnoreCase("dtsl"))) {
        return "audio/vnd.dts.hd";
      }
      if (paramString.equalsIgnoreCase("dtse")) {
        return "audio/vnd.dts.hd;profile=lbr";
      }
      if (paramString.equalsIgnoreCase("opus")) {
        return "audio/opus";
      }
      return null;
    }
    
    public Object build()
    {
      return this.format;
    }
    
    public void parseStartTag(XmlPullParser paramXmlPullParser)
      throws ParserException
    {
      int j = ((Integer)getNormalizedAttribute("Type")).intValue();
      String str = paramXmlPullParser.getAttributeValue(null, "Index");
      int i = parseRequiredInt(paramXmlPullParser, "Bitrate");
      Object localObject2 = fourCCToMimeType(parseRequiredString(paramXmlPullParser, "FourCC"));
      if (j == 2)
      {
        this.format = Format.createVideoContainerFormat(str, "video/mp4", (String)localObject2, null, i, parseRequiredInt(paramXmlPullParser, "MaxWidth"), parseRequiredInt(paramXmlPullParser, "MaxHeight"), -1.0F, buildCodecSpecificData(paramXmlPullParser.getAttributeValue(null, "CodecPrivateData")), 0);
        return;
      }
      if (j == 1)
      {
        Object localObject1 = localObject2;
        if (localObject2 == null) {
          localObject1 = "audio/mp4a-latm";
        }
        j = parseRequiredInt(paramXmlPullParser, "Channels");
        int k = parseRequiredInt(paramXmlPullParser, "SamplingRate");
        localObject2 = buildCodecSpecificData(paramXmlPullParser.getAttributeValue(null, "CodecPrivateData"));
        paramXmlPullParser = (XmlPullParser)localObject2;
        if (((List)localObject2).isEmpty())
        {
          paramXmlPullParser = (XmlPullParser)localObject2;
          if ("audio/mp4a-latm".equals(localObject1)) {
            paramXmlPullParser = Collections.singletonList(CodecSpecificDataUtil.buildAacLcAudioSpecificConfig(k, j));
          }
        }
        this.format = Format.createAudioContainerFormat(str, "audio/mp4", (String)localObject1, null, i, j, k, paramXmlPullParser, 0, (String)getNormalizedAttribute("Language"));
        return;
      }
      if (j == 3)
      {
        this.format = Format.createTextContainerFormat(str, "application/mp4", (String)localObject2, null, i, 0, (String)getNormalizedAttribute("Language"));
        return;
      }
      this.format = Format.createContainerFormat(str, "application/mp4", (String)localObject2, null, i, 0, null);
    }
  }
  
  private static class SmoothStreamingMediaParser
    extends SsManifestParser.ElementParser
  {
    private static final String KEY_DURATION = "Duration";
    private static final String KEY_DVR_WINDOW_LENGTH = "DVRWindowLength";
    private static final String KEY_IS_LIVE = "IsLive";
    private static final String KEY_LOOKAHEAD_COUNT = "LookaheadCount";
    private static final String KEY_MAJOR_VERSION = "MajorVersion";
    private static final String KEY_MINOR_VERSION = "MinorVersion";
    private static final String KEY_TIME_SCALE = "TimeScale";
    public static final String TAG = "SmoothStreamingMedia";
    private long duration;
    private long dvrWindowLength;
    private boolean isLive;
    private int lookAheadCount = -1;
    private int majorVersion;
    private int minorVersion;
    private SsManifest.ProtectionElement protectionElement = null;
    private final List<SsManifest.StreamElement> streamElements = new LinkedList();
    private long timescale;
    
    public SmoothStreamingMediaParser(SsManifestParser.ElementParser paramElementParser, String paramString)
    {
      super(paramString, "SmoothStreamingMedia");
    }
    
    public void addChild(Object paramObject)
    {
      if ((paramObject instanceof SsManifest.StreamElement)) {
        this.streamElements.add((SsManifest.StreamElement)paramObject);
      }
      while (!(paramObject instanceof SsManifest.ProtectionElement)) {
        return;
      }
      if (this.protectionElement == null) {}
      for (boolean bool = true;; bool = false)
      {
        Assertions.checkState(bool);
        this.protectionElement = ((SsManifest.ProtectionElement)paramObject);
        return;
      }
    }
    
    public Object build()
    {
      SsManifest.StreamElement[] arrayOfStreamElement = new SsManifest.StreamElement[this.streamElements.size()];
      this.streamElements.toArray(arrayOfStreamElement);
      if (this.protectionElement != null)
      {
        DrmInitData localDrmInitData = new DrmInitData(new DrmInitData.SchemeData[] { new DrmInitData.SchemeData(this.protectionElement.uuid, "video/mp4", this.protectionElement.data) });
        int k = arrayOfStreamElement.length;
        int i = 0;
        while (i < k)
        {
          SsManifest.StreamElement localStreamElement = arrayOfStreamElement[i];
          int j = 0;
          while (j < localStreamElement.formats.length)
          {
            localStreamElement.formats[j] = localStreamElement.formats[j].copyWithDrmInitData(localDrmInitData);
            j += 1;
          }
          i += 1;
        }
      }
      return new SsManifest(this.majorVersion, this.minorVersion, this.timescale, this.duration, this.dvrWindowLength, this.lookAheadCount, this.isLive, this.protectionElement, arrayOfStreamElement);
    }
    
    public void parseStartTag(XmlPullParser paramXmlPullParser)
      throws ParserException
    {
      this.majorVersion = parseRequiredInt(paramXmlPullParser, "MajorVersion");
      this.minorVersion = parseRequiredInt(paramXmlPullParser, "MinorVersion");
      this.timescale = parseLong(paramXmlPullParser, "TimeScale", 10000000L);
      this.duration = parseRequiredLong(paramXmlPullParser, "Duration");
      this.dvrWindowLength = parseLong(paramXmlPullParser, "DVRWindowLength", 0L);
      this.lookAheadCount = parseInt(paramXmlPullParser, "LookaheadCount", -1);
      this.isLive = parseBoolean(paramXmlPullParser, "IsLive", false);
      putNormalizedAttribute("TimeScale", Long.valueOf(this.timescale));
    }
  }
  
  private static class StreamIndexParser
    extends SsManifestParser.ElementParser
  {
    private static final String KEY_DISPLAY_HEIGHT = "DisplayHeight";
    private static final String KEY_DISPLAY_WIDTH = "DisplayWidth";
    private static final String KEY_FRAGMENT_DURATION = "d";
    private static final String KEY_FRAGMENT_REPEAT_COUNT = "r";
    private static final String KEY_FRAGMENT_START_TIME = "t";
    private static final String KEY_LANGUAGE = "Language";
    private static final String KEY_MAX_HEIGHT = "MaxHeight";
    private static final String KEY_MAX_WIDTH = "MaxWidth";
    private static final String KEY_NAME = "Name";
    private static final String KEY_SUB_TYPE = "Subtype";
    private static final String KEY_TIME_SCALE = "TimeScale";
    private static final String KEY_TYPE = "Type";
    private static final String KEY_TYPE_AUDIO = "audio";
    private static final String KEY_TYPE_TEXT = "text";
    private static final String KEY_TYPE_VIDEO = "video";
    private static final String KEY_URL = "Url";
    public static final String TAG = "StreamIndex";
    private static final String TAG_STREAM_FRAGMENT = "c";
    private final String baseUri;
    private int displayHeight;
    private int displayWidth;
    private final List<Format> formats;
    private String language;
    private long lastChunkDuration;
    private int maxHeight;
    private int maxWidth;
    private String name;
    private ArrayList<Long> startTimes;
    private String subType;
    private long timescale;
    private int type;
    private String url;
    
    public StreamIndexParser(SsManifestParser.ElementParser paramElementParser, String paramString)
    {
      super(paramString, "StreamIndex");
      this.baseUri = paramString;
      this.formats = new LinkedList();
    }
    
    private void parseStreamElementStartTag(XmlPullParser paramXmlPullParser)
      throws ParserException
    {
      this.type = parseType(paramXmlPullParser);
      putNormalizedAttribute("Type", Integer.valueOf(this.type));
      if (this.type == 3) {}
      for (this.subType = parseRequiredString(paramXmlPullParser, "Subtype");; this.subType = paramXmlPullParser.getAttributeValue(null, "Subtype"))
      {
        this.name = paramXmlPullParser.getAttributeValue(null, "Name");
        this.url = parseRequiredString(paramXmlPullParser, "Url");
        this.maxWidth = parseInt(paramXmlPullParser, "MaxWidth", -1);
        this.maxHeight = parseInt(paramXmlPullParser, "MaxHeight", -1);
        this.displayWidth = parseInt(paramXmlPullParser, "DisplayWidth", -1);
        this.displayHeight = parseInt(paramXmlPullParser, "DisplayHeight", -1);
        this.language = paramXmlPullParser.getAttributeValue(null, "Language");
        putNormalizedAttribute("Language", this.language);
        this.timescale = parseInt(paramXmlPullParser, "TimeScale", -1);
        if (this.timescale == -1L) {
          this.timescale = ((Long)getNormalizedAttribute("TimeScale")).longValue();
        }
        this.startTimes = new ArrayList();
        return;
      }
    }
    
    private void parseStreamFragmentStartTag(XmlPullParser paramXmlPullParser)
      throws ParserException
    {
      int i = this.startTimes.size();
      long l2 = parseLong(paramXmlPullParser, "t", -9223372036854775807L);
      long l1 = l2;
      if (l2 == -9223372036854775807L) {
        if (i != 0) {
          break label109;
        }
      }
      int j;
      for (l1 = 0L;; l1 = ((Long)this.startTimes.get(i - 1)).longValue() + this.lastChunkDuration)
      {
        j = i + 1;
        this.startTimes.add(Long.valueOf(l1));
        this.lastChunkDuration = parseLong(paramXmlPullParser, "d", -9223372036854775807L);
        l2 = parseLong(paramXmlPullParser, "r", 1L);
        if ((l2 <= 1L) || (this.lastChunkDuration != -9223372036854775807L)) {
          break label156;
        }
        throw new ParserException("Repeated chunk with unspecified duration");
        label109:
        if (this.lastChunkDuration == -1L) {
          break;
        }
      }
      throw new ParserException("Unable to infer start time");
      label156:
      i = 1;
      while (i < l2)
      {
        j += 1;
        this.startTimes.add(Long.valueOf(this.lastChunkDuration * i + l1));
        i += 1;
      }
    }
    
    private int parseType(XmlPullParser paramXmlPullParser)
      throws ParserException
    {
      paramXmlPullParser = paramXmlPullParser.getAttributeValue(null, "Type");
      if (paramXmlPullParser != null)
      {
        if ("audio".equalsIgnoreCase(paramXmlPullParser)) {
          return 1;
        }
        if ("video".equalsIgnoreCase(paramXmlPullParser)) {
          return 2;
        }
        if ("text".equalsIgnoreCase(paramXmlPullParser)) {
          return 3;
        }
        throw new ParserException("Invalid key value[" + paramXmlPullParser + "]");
      }
      throw new SsManifestParser.MissingFieldException("Type");
    }
    
    public void addChild(Object paramObject)
    {
      if ((paramObject instanceof Format)) {
        this.formats.add((Format)paramObject);
      }
    }
    
    public Object build()
    {
      Format[] arrayOfFormat = new Format[this.formats.size()];
      this.formats.toArray(arrayOfFormat);
      return new SsManifest.StreamElement(this.baseUri, this.url, this.type, this.subType, this.timescale, this.name, this.maxWidth, this.maxHeight, this.displayWidth, this.displayHeight, this.language, arrayOfFormat, this.startTimes, this.lastChunkDuration);
    }
    
    public boolean handleChildInline(String paramString)
    {
      return "c".equals(paramString);
    }
    
    public void parseStartTag(XmlPullParser paramXmlPullParser)
      throws ParserException
    {
      if ("c".equals(paramXmlPullParser.getName()))
      {
        parseStreamFragmentStartTag(paramXmlPullParser);
        return;
      }
      parseStreamElementStartTag(paramXmlPullParser);
    }
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/source/smoothstreaming/manifest/SsManifestParser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */