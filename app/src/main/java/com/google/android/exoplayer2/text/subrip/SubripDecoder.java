package com.google.android.exoplayer2.text.subrip;

import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.exoplayer2.text.Cue;
import com.google.android.exoplayer2.text.SimpleSubtitleDecoder;
import com.google.android.exoplayer2.util.LongArray;
import com.google.android.exoplayer2.util.ParsableByteArray;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class SubripDecoder
  extends SimpleSubtitleDecoder
{
  private static final String SUBRIP_TIMECODE = "(?:(\\d+):)?(\\d+):(\\d+),(\\d+)";
  private static final Pattern SUBRIP_TIMING_LINE = Pattern.compile("\\s*((?:(\\d+):)?(\\d+):(\\d+),(\\d+))\\s*-->\\s*((?:(\\d+):)?(\\d+):(\\d+),(\\d+))?\\s*");
  private static final String TAG = "SubripDecoder";
  private final StringBuilder textBuilder = new StringBuilder();
  
  public SubripDecoder()
  {
    super("SubripDecoder");
  }
  
  private static long parseTimecode(Matcher paramMatcher, int paramInt)
  {
    return (Long.parseLong(paramMatcher.group(paramInt + 1)) * 60L * 60L * 1000L + Long.parseLong(paramMatcher.group(paramInt + 2)) * 60L * 1000L + Long.parseLong(paramMatcher.group(paramInt + 3)) * 1000L + Long.parseLong(paramMatcher.group(paramInt + 4))) * 1000L;
  }
  
  protected SubripSubtitle decode(byte[] paramArrayOfByte, int paramInt)
  {
    ArrayList localArrayList = new ArrayList();
    LongArray localLongArray = new LongArray();
    paramArrayOfByte = new ParsableByteArray(paramArrayOfByte, paramInt);
    for (;;)
    {
      String str = paramArrayOfByte.readLine();
      if (str == null) {
        break;
      }
      if (str.length() != 0)
      {
        try
        {
          Integer.parseInt(str);
          paramInt = 0;
          str = paramArrayOfByte.readLine();
          Matcher localMatcher = SUBRIP_TIMING_LINE.matcher(str);
          if (localMatcher.matches())
          {
            localLongArray.add(parseTimecode(localMatcher, 1));
            if (!TextUtils.isEmpty(localMatcher.group(6)))
            {
              paramInt = 1;
              localLongArray.add(parseTimecode(localMatcher, 6));
            }
            this.textBuilder.setLength(0);
            for (;;)
            {
              str = paramArrayOfByte.readLine();
              if (TextUtils.isEmpty(str)) {
                break;
              }
              if (this.textBuilder.length() > 0) {
                this.textBuilder.append("<br>");
              }
              this.textBuilder.append(str.trim());
            }
          }
        }
        catch (NumberFormatException localNumberFormatException)
        {
          Log.w("SubripDecoder", "Skipping invalid index: " + str);
        }
        Log.w("SubripDecoder", "Skipping invalid timing: " + str);
        continue;
        localArrayList.add(new Cue(Html.fromHtml(this.textBuilder.toString())));
        if (paramInt != 0) {
          localArrayList.add(null);
        }
      }
    }
    paramArrayOfByte = new Cue[localArrayList.size()];
    localArrayList.toArray(paramArrayOfByte);
    return new SubripSubtitle(paramArrayOfByte, localLongArray.toArray());
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/text/subrip/SubripDecoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */