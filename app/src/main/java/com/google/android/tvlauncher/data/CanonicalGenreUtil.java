package com.google.android.tvlauncher.data;

import android.content.Context;
import android.content.res.Resources;
import android.media.tv.TvContract.Programs.Genres;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CanonicalGenreUtil
{
  private static final List<String> CANONICAL_GENRES = Arrays.asList(new String[] { "FAMILY_KIDS", "SPORTS", "SHOPPING", "MOVIES", "COMEDY", "TRAVEL", "DRAMA", "EDUCATION", "ANIMAL_WILDLIFE", "NEWS", "GAMING", "ARTS", "ENTERTAINMENT", "LIFE_STYLE", "MUSIC", "PREMIER", "TECH_SCIENCE" });
  private String[] mCanonicalGenreLabels;
  private String[] mCanonicalGenreLabelsFormats;
  
  public CanonicalGenreUtil(Context paramContext)
  {
    paramContext = paramContext.getResources();
    this.mCanonicalGenreLabels = paramContext.getStringArray(2131361792);
    if (this.mCanonicalGenreLabels.length != CANONICAL_GENRES.size()) {
      throw new IllegalArgumentException("Canonical genre data mismatch");
    }
    this.mCanonicalGenreLabelsFormats = paramContext.getStringArray(2131361793);
  }
  
  public String decodeGenres(String paramString)
  {
    if (TextUtils.isEmpty(paramString)) {}
    ArrayList localArrayList;
    do
    {
      do
      {
        return null;
        paramString = TvContract.Programs.Genres.decode(paramString);
      } while (paramString.length == 0);
      localArrayList = new ArrayList(paramString.length);
      int j = paramString.length;
      int i = 0;
      while (i < j)
      {
        Object localObject = paramString[i];
        int k = CANONICAL_GENRES.indexOf(localObject);
        if (k != -1) {
          localArrayList.add(this.mCanonicalGenreLabels[k]);
        }
        i += 1;
      }
    } while (localArrayList.size() == 0);
    paramString = localArrayList;
    if (localArrayList.size() > this.mCanonicalGenreLabelsFormats.length) {
      paramString = localArrayList.subList(0, this.mCanonicalGenreLabelsFormats.length);
    }
    return String.format(this.mCanonicalGenreLabelsFormats[(paramString.size() - 1)], paramString.toArray());
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/data/CanonicalGenreUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */