package com.google.android.exoplayer2.source.hls.playlist;

import android.net.Uri;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.source.UnrecognizedInputFormatException;
import com.google.android.exoplayer2.upstream.ParsingLoadable.Parser;
import com.google.android.exoplayer2.util.Util;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class HlsPlaylistParser
  implements ParsingLoadable.Parser<HlsPlaylist>
{
  private static final String BOOLEAN_FALSE = "NO";
  private static final String BOOLEAN_TRUE = "YES";
  private static final String METHOD_AES128 = "AES-128";
  private static final String METHOD_NONE = "NONE";
  private static final String PLAYLIST_HEADER = "#EXTM3U";
  private static final Pattern REGEX_ATTR_BYTERANGE;
  private static final Pattern REGEX_AUTOSELECT = compileBooleanAttrPattern("AUTOSELECT");
  private static final Pattern REGEX_BANDWIDTH = Pattern.compile("BANDWIDTH=(\\d+)\\b");
  private static final Pattern REGEX_BYTERANGE;
  private static final Pattern REGEX_CODECS = Pattern.compile("CODECS=\"(.+?)\"");
  private static final Pattern REGEX_DEFAULT = compileBooleanAttrPattern("DEFAULT");
  private static final Pattern REGEX_FORCED = compileBooleanAttrPattern("FORCED");
  private static final Pattern REGEX_INSTREAM_ID;
  private static final Pattern REGEX_IV;
  private static final Pattern REGEX_LANGUAGE;
  private static final Pattern REGEX_MEDIA_DURATION;
  private static final Pattern REGEX_MEDIA_SEQUENCE;
  private static final Pattern REGEX_METHOD;
  private static final Pattern REGEX_NAME;
  private static final Pattern REGEX_PLAYLIST_TYPE;
  private static final Pattern REGEX_RESOLUTION = Pattern.compile("RESOLUTION=(\\d+x\\d+)");
  private static final Pattern REGEX_TARGET_DURATION = Pattern.compile("#EXT-X-TARGETDURATION:(\\d+)\\b");
  private static final Pattern REGEX_TIME_OFFSET;
  private static final Pattern REGEX_TYPE;
  private static final Pattern REGEX_URI;
  private static final Pattern REGEX_VERSION = Pattern.compile("#EXT-X-VERSION:(\\d+)\\b");
  private static final String TAG_BYTERANGE = "#EXT-X-BYTERANGE";
  private static final String TAG_DISCONTINUITY = "#EXT-X-DISCONTINUITY";
  private static final String TAG_DISCONTINUITY_SEQUENCE = "#EXT-X-DISCONTINUITY-SEQUENCE";
  private static final String TAG_ENDLIST = "#EXT-X-ENDLIST";
  private static final String TAG_INIT_SEGMENT = "#EXT-X-MAP";
  private static final String TAG_KEY = "#EXT-X-KEY";
  private static final String TAG_MEDIA = "#EXT-X-MEDIA";
  private static final String TAG_MEDIA_DURATION = "#EXTINF";
  private static final String TAG_MEDIA_SEQUENCE = "#EXT-X-MEDIA-SEQUENCE";
  private static final String TAG_PLAYLIST_TYPE = "#EXT-X-PLAYLIST-TYPE";
  private static final String TAG_PROGRAM_DATE_TIME = "#EXT-X-PROGRAM-DATE-TIME";
  private static final String TAG_START = "#EXT-X-START";
  private static final String TAG_STREAM_INF = "#EXT-X-STREAM-INF";
  private static final String TAG_TARGET_DURATION = "#EXT-X-TARGETDURATION";
  private static final String TAG_VERSION = "#EXT-X-VERSION";
  private static final String TYPE_AUDIO = "AUDIO";
  private static final String TYPE_CLOSED_CAPTIONS = "CLOSED-CAPTIONS";
  private static final String TYPE_SUBTITLES = "SUBTITLES";
  private static final String TYPE_VIDEO = "VIDEO";
  
  static
  {
    REGEX_PLAYLIST_TYPE = Pattern.compile("#EXT-X-PLAYLIST-TYPE:(.+)\\b");
    REGEX_MEDIA_SEQUENCE = Pattern.compile("#EXT-X-MEDIA-SEQUENCE:(\\d+)\\b");
    REGEX_MEDIA_DURATION = Pattern.compile("#EXTINF:([\\d\\.]+)\\b");
    REGEX_TIME_OFFSET = Pattern.compile("TIME-OFFSET=([\\d\\.]+)\\b");
    REGEX_BYTERANGE = Pattern.compile("#EXT-X-BYTERANGE:(\\d+(?:@\\d+)?)\\b");
    REGEX_ATTR_BYTERANGE = Pattern.compile("BYTERANGE=\"(\\d+(?:@\\d+)?)\\b\"");
    REGEX_METHOD = Pattern.compile("METHOD=(NONE|AES-128)");
    REGEX_URI = Pattern.compile("URI=\"(.+?)\"");
    REGEX_IV = Pattern.compile("IV=([^,.*]+)");
    REGEX_TYPE = Pattern.compile("TYPE=(AUDIO|VIDEO|SUBTITLES|CLOSED-CAPTIONS)");
    REGEX_LANGUAGE = Pattern.compile("LANGUAGE=\"(.+?)\"");
    REGEX_NAME = Pattern.compile("NAME=\"(.+?)\"");
    REGEX_INSTREAM_ID = Pattern.compile("INSTREAM-ID=\"(.+?)\"");
  }
  
  private static boolean checkPlaylistHeader(BufferedReader paramBufferedReader)
    throws IOException
  {
    int j = paramBufferedReader.read();
    int i = j;
    if (j == 239)
    {
      if ((paramBufferedReader.read() != 187) || (paramBufferedReader.read() != 191)) {
        return false;
      }
      i = paramBufferedReader.read();
    }
    j = skipIgnorableWhitespace(paramBufferedReader, true, i);
    int k = "#EXTM3U".length();
    i = 0;
    for (;;)
    {
      if (i >= k) {
        break label83;
      }
      if (j != "#EXTM3U".charAt(i)) {
        break;
      }
      j = paramBufferedReader.read();
      i += 1;
    }
    label83:
    return Util.isLinebreak(skipIgnorableWhitespace(paramBufferedReader, false, j));
  }
  
  private static Pattern compileBooleanAttrPattern(String paramString)
  {
    return Pattern.compile(paramString + "=(" + "NO" + "|" + "YES" + ")");
  }
  
  private static boolean parseBooleanAttribute(String paramString, Pattern paramPattern, boolean paramBoolean)
  {
    paramString = paramPattern.matcher(paramString);
    if (paramString.find()) {
      paramBoolean = paramString.group(1).equals("YES");
    }
    return paramBoolean;
  }
  
  private static double parseDoubleAttr(String paramString, Pattern paramPattern)
    throws ParserException
  {
    return Double.parseDouble(parseStringAttr(paramString, paramPattern));
  }
  
  private static int parseIntAttr(String paramString, Pattern paramPattern)
    throws ParserException
  {
    return Integer.parseInt(parseStringAttr(paramString, paramPattern));
  }
  
  private static HlsMasterPlaylist parseMasterPlaylist(LineIterator paramLineIterator, String paramString)
    throws IOException
  {
    ArrayList localArrayList1 = new ArrayList();
    ArrayList localArrayList2 = new ArrayList();
    ArrayList localArrayList3 = new ArrayList();
    Object localObject1 = null;
    Format localFormat = null;
    while (paramLineIterator.hasNext())
    {
      Object localObject2 = paramLineIterator.next();
      int j;
      String str1;
      String str2;
      int i;
      if (((String)localObject2).startsWith("#EXT-X-MEDIA"))
      {
        j = parseSelectionFlags((String)localObject2);
        str1 = parseOptionalStringAttr((String)localObject2, REGEX_URI);
        str2 = parseStringAttr((String)localObject2, REGEX_NAME);
        String str3 = parseOptionalStringAttr((String)localObject2, REGEX_LANGUAGE);
        String str4 = parseStringAttr((String)localObject2, REGEX_TYPE);
        i = -1;
        switch (str4.hashCode())
        {
        }
        for (;;)
        {
          switch (i)
          {
          default: 
            break;
          case 0: 
            localObject2 = Format.createAudioContainerFormat(str2, "application/x-mpegURL", null, null, -1, -1, -1, null, j, str3);
            if (str1 != null) {
              break label251;
            }
            localObject1 = localObject2;
            break;
            if (str4.equals("AUDIO"))
            {
              i = 0;
              continue;
              if (str4.equals("SUBTITLES"))
              {
                i = 1;
                continue;
                if (str4.equals("CLOSED-CAPTIONS")) {
                  i = 2;
                }
              }
            }
            break;
          }
        }
        label251:
        localArrayList2.add(new HlsMasterPlaylist.HlsUrl(str2, str1, (Format)localObject2, null, (Format)localObject2, null));
        continue;
        localObject2 = Format.createTextContainerFormat(str2, "application/x-mpegURL", "text/vtt", null, -1, j, str3);
        localArrayList3.add(new HlsMasterPlaylist.HlsUrl(str2, str1, (Format)localObject2, null, (Format)localObject2, null));
        continue;
        if ("CC1".equals(parseOptionalStringAttr((String)localObject2, REGEX_INSTREAM_ID))) {
          localFormat = Format.createTextContainerFormat(str2, "application/x-mpegURL", "application/cea-608", null, -1, j, str3);
        }
      }
      else if (((String)localObject2).startsWith("#EXT-X-STREAM-INF"))
      {
        int m = parseIntAttr((String)localObject2, REGEX_BANDWIDTH);
        str1 = parseOptionalStringAttr((String)localObject2, REGEX_CODECS);
        localObject2 = parseOptionalStringAttr((String)localObject2, REGEX_RESOLUTION);
        if (localObject2 != null)
        {
          localObject2 = ((String)localObject2).split("x");
          i = Integer.parseInt(localObject2[0]);
          int k = Integer.parseInt(localObject2[1]);
          if (i > 0)
          {
            j = k;
            if (k > 0) {}
          }
          else
          {
            i = -1;
          }
        }
        for (j = -1;; j = -1)
        {
          localObject2 = paramLineIterator.next();
          str2 = Integer.toString(localArrayList1.size());
          localArrayList1.add(new HlsMasterPlaylist.HlsUrl(str2, (String)localObject2, Format.createVideoContainerFormat(str2, "application/x-mpegURL", null, str1, m, i, j, -1.0F, null, 0), null, null, null));
          break;
          i = -1;
        }
      }
    }
    return new HlsMasterPlaylist(paramString, localArrayList1, localArrayList2, localArrayList3, (Format)localObject1, localFormat);
  }
  
  private static HlsMediaPlaylist parseMediaPlaylist(LineIterator paramLineIterator, String paramString)
    throws IOException
  {
    int i = 0;
    long l6 = -9223372036854775807L;
    int m = 0;
    int k = 1;
    long l4 = -9223372036854775807L;
    boolean bool1 = false;
    Object localObject2 = null;
    ArrayList localArrayList = new ArrayList();
    long l3 = 0L;
    boolean bool2 = false;
    int n = 0;
    int i1 = 0;
    long l5 = 0L;
    long l7 = 0L;
    long l1 = 0L;
    long l2 = -1L;
    int j = 0;
    boolean bool3 = false;
    String str2 = null;
    String str1 = null;
    while (paramLineIterator.hasNext())
    {
      String str3 = paramLineIterator.next();
      Object localObject1;
      if (str3.startsWith("#EXT-X-PLAYLIST-TYPE"))
      {
        localObject1 = parseStringAttr(str3, REGEX_PLAYLIST_TYPE);
        if ("VOD".equals(localObject1)) {
          i = 1;
        } else if ("EVENT".equals(localObject1)) {
          i = 2;
        } else {
          throw new ParserException("Illegal playlist type: " + (String)localObject1);
        }
      }
      else if (str3.startsWith("#EXT-X-START"))
      {
        l6 = (parseDoubleAttr(str3, REGEX_TIME_OFFSET) * 1000000.0D);
      }
      else
      {
        long l8;
        if (str3.startsWith("#EXT-X-MAP"))
        {
          localObject1 = parseStringAttr(str3, REGEX_URI);
          localObject2 = parseOptionalStringAttr(str3, REGEX_ATTR_BYTERANGE);
          l8 = l1;
          if (localObject2 != null)
          {
            localObject2 = ((String)localObject2).split("@");
            long l9 = Long.parseLong(localObject2[0]);
            l8 = l1;
            l2 = l9;
            if (localObject2.length > 1)
            {
              l8 = Long.parseLong(localObject2[1]);
              l2 = l9;
            }
          }
          localObject2 = new HlsMediaPlaylist.Segment((String)localObject1, l8, l2);
          l1 = 0L;
          l2 = -1L;
        }
        else if (str3.startsWith("#EXT-X-TARGETDURATION"))
        {
          l4 = parseIntAttr(str3, REGEX_TARGET_DURATION) * 1000000L;
        }
        else if (str3.startsWith("#EXT-X-MEDIA-SEQUENCE"))
        {
          m = parseIntAttr(str3, REGEX_MEDIA_SEQUENCE);
          j = m;
        }
        else if (str3.startsWith("#EXT-X-VERSION"))
        {
          k = parseIntAttr(str3, REGEX_VERSION);
        }
        else if (str3.startsWith("#EXTINF"))
        {
          l3 = (parseDoubleAttr(str3, REGEX_MEDIA_DURATION) * 1000000.0D);
        }
        else if (str3.startsWith("#EXT-X-KEY"))
        {
          bool3 = "AES-128".equals(parseStringAttr(str3, REGEX_METHOD));
          if (bool3)
          {
            str2 = parseStringAttr(str3, REGEX_URI);
            str1 = parseOptionalStringAttr(str3, REGEX_IV);
          }
          else
          {
            str2 = null;
            str1 = null;
          }
        }
        else if (str3.startsWith("#EXT-X-BYTERANGE"))
        {
          localObject1 = parseStringAttr(str3, REGEX_BYTERANGE).split("@");
          l8 = Long.parseLong(localObject1[0]);
          l2 = l8;
          if (localObject1.length > 1)
          {
            l1 = Long.parseLong(localObject1[1]);
            l2 = l8;
          }
        }
        else if (str3.startsWith("#EXT-X-DISCONTINUITY-SEQUENCE"))
        {
          bool2 = true;
          n = Integer.parseInt(str3.substring(str3.indexOf(':') + 1));
        }
        else if (str3.equals("#EXT-X-DISCONTINUITY"))
        {
          i1 += 1;
        }
        else if (str3.startsWith("#EXT-X-PROGRAM-DATE-TIME"))
        {
          if (l5 == 0L) {
            l5 = C.msToUs(Util.parseXsDateTime(str3.substring(str3.indexOf(':') + 1))) - l7;
          }
        }
        else
        {
          if (!str3.startsWith("#"))
          {
            if (!bool3) {
              localObject1 = null;
            }
            for (;;)
            {
              j += 1;
              if (l2 == -1L) {
                l1 = 0L;
              }
              localArrayList.add(new HlsMediaPlaylist.Segment(str3, l3, i1, l7, bool3, str2, (String)localObject1, l1, l2));
              l7 += l3;
              l8 = 0L;
              l3 = l1;
              if (l2 != -1L) {
                l3 = l1 + l2;
              }
              l2 = -1L;
              l1 = l3;
              l3 = l8;
              break;
              if (str1 != null) {
                localObject1 = str1;
              } else {
                localObject1 = Integer.toHexString(j);
              }
            }
          }
          if (str3.equals("#EXT-X-ENDLIST")) {
            bool1 = true;
          }
        }
      }
    }
    if (l5 != 0L) {}
    for (bool3 = true;; bool3 = false) {
      return new HlsMediaPlaylist(i, paramString, l6, l5, bool2, n, m, k, l4, bool1, bool3, (HlsMediaPlaylist.Segment)localObject2, localArrayList);
    }
  }
  
  private static String parseOptionalStringAttr(String paramString, Pattern paramPattern)
  {
    paramString = paramPattern.matcher(paramString);
    if (paramString.find()) {
      return paramString.group(1);
    }
    return null;
  }
  
  private static int parseSelectionFlags(String paramString)
  {
    int k = 0;
    int i;
    if (parseBooleanAttribute(paramString, REGEX_DEFAULT, false))
    {
      i = 1;
      if (!parseBooleanAttribute(paramString, REGEX_FORCED, false)) {
        break label52;
      }
    }
    label52:
    for (int j = 2;; j = 0)
    {
      if (parseBooleanAttribute(paramString, REGEX_AUTOSELECT, false)) {
        k = 4;
      }
      return i | j | k;
      i = 0;
      break;
    }
  }
  
  private static String parseStringAttr(String paramString, Pattern paramPattern)
    throws ParserException
  {
    Matcher localMatcher = paramPattern.matcher(paramString);
    if ((localMatcher.find()) && (localMatcher.groupCount() == 1)) {
      return localMatcher.group(1);
    }
    throw new ParserException("Couldn't match " + paramPattern.pattern() + " in " + paramString);
  }
  
  private static int skipIgnorableWhitespace(BufferedReader paramBufferedReader, boolean paramBoolean, int paramInt)
    throws IOException
  {
    while ((paramInt != -1) && (Character.isWhitespace(paramInt)) && ((paramBoolean) || (!Util.isLinebreak(paramInt)))) {
      paramInt = paramBufferedReader.read();
    }
    return paramInt;
  }
  
  public HlsPlaylist parse(Uri paramUri, InputStream paramInputStream)
    throws IOException
  {
    paramInputStream = new BufferedReader(new InputStreamReader(paramInputStream));
    LinkedList localLinkedList = new LinkedList();
    try
    {
      if (!checkPlaylistHeader(paramInputStream)) {
        throw new UnrecognizedInputFormatException("Input does not start with the #EXTM3U header.", paramUri);
      }
    }
    finally
    {
      Util.closeQuietly(paramInputStream);
    }
    for (;;)
    {
      String str = paramInputStream.readLine();
      if (str == null) {
        break;
      }
      str = str.trim();
      if (!str.isEmpty())
      {
        if (str.startsWith("#EXT-X-STREAM-INF"))
        {
          localLinkedList.add(str);
          paramUri = parseMasterPlaylist(new LineIterator(localLinkedList, paramInputStream), paramUri.toString());
          Util.closeQuietly(paramInputStream);
          return paramUri;
        }
        if ((str.startsWith("#EXT-X-TARGETDURATION")) || (str.startsWith("#EXT-X-MEDIA-SEQUENCE")) || (str.startsWith("#EXTINF")) || (str.startsWith("#EXT-X-KEY")) || (str.startsWith("#EXT-X-BYTERANGE")) || (str.equals("#EXT-X-DISCONTINUITY")) || (str.equals("#EXT-X-DISCONTINUITY-SEQUENCE")) || (str.equals("#EXT-X-ENDLIST")))
        {
          localLinkedList.add(str);
          paramUri = parseMediaPlaylist(new LineIterator(localLinkedList, paramInputStream), paramUri.toString());
          Util.closeQuietly(paramInputStream);
          return paramUri;
        }
        localLinkedList.add(str);
      }
    }
    Util.closeQuietly(paramInputStream);
    throw new ParserException("Failed to parse the playlist, could not identify any tags.");
  }
  
  private static class LineIterator
  {
    private final Queue<String> extraLines;
    private String next;
    private final BufferedReader reader;
    
    public LineIterator(Queue<String> paramQueue, BufferedReader paramBufferedReader)
    {
      this.extraLines = paramQueue;
      this.reader = paramBufferedReader;
    }
    
    public boolean hasNext()
      throws IOException
    {
      if (this.next != null) {
        return true;
      }
      if (!this.extraLines.isEmpty())
      {
        this.next = ((String)this.extraLines.poll());
        return true;
      }
      do
      {
        String str = this.reader.readLine();
        this.next = str;
        if (str == null) {
          break;
        }
        this.next = this.next.trim();
      } while (this.next.isEmpty());
      return true;
      return false;
    }
    
    public String next()
      throws IOException
    {
      String str = null;
      if (hasNext())
      {
        str = this.next;
        this.next = null;
      }
      return str;
    }
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/source/hls/playlist/HlsPlaylistParser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */