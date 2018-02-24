package com.google.android.tvlauncher.data;

import android.content.Context;
import android.content.res.Resources;
import android.media.tv.TvContract;
import android.media.tv.TvContract.Programs.Genres;
import android.text.TextUtils;

import com.google.android.tvlauncher.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CanonicalGenreUtil {
    private static final List<String> CANONICAL_GENRES = Arrays.asList("FAMILY_KIDS", "SPORTS", "SHOPPING", "MOVIES", "COMEDY", "TRAVEL", "DRAMA", "EDUCATION", "ANIMAL_WILDLIFE", "NEWS", "GAMING", "ARTS", "ENTERTAINMENT", "LIFE_STYLE", "MUSIC", "PREMIER", "TECH_SCIENCE");
    private String[] mCanonicalGenreLabels;
    private String[] mCanonicalGenreLabelsFormats;

    public CanonicalGenreUtil(Context paramContext) {
        Resources res = paramContext.getResources();
        this.mCanonicalGenreLabels = res.getStringArray(R.array.genre_labels);
        if (this.mCanonicalGenreLabels.length != CANONICAL_GENRES.size()) {
            throw new IllegalArgumentException("Canonical genre data mismatch");
        }
        this.mCanonicalGenreLabelsFormats = res.getStringArray(R.array.program_canonical_genre_labels_formats);
    }

    public String decodeGenres(String paramString) {
        if (TextUtils.isEmpty(paramString)) {
            return null;
        }

        String[] genres = TvContract.Programs.Genres.decode(paramString);
        if (genres.length == 0) {
            return null;
        }

        ArrayList<String> localArrayList = new ArrayList<>(genres.length);


        //for (String genre : genres) {
        //    if (localArrayList.size() > this.mCanonicalGenreLabelsFormats.length) {
        //        paramString = localArrayList.subList(0, this.mCanonicalGenreLabelsFormats.length);
        //    }
        //} todo

        int j = genres.length;
        int i = 0;
        while (i < j) {
            String localObject = genres[i];
            int k = CANONICAL_GENRES.indexOf(localObject);

            if (k != -1) {
                localArrayList.add(this.mCanonicalGenreLabels[k]);
            }

            i++;
        }

        return String.format(this.mCanonicalGenreLabelsFormats[(localArrayList.size() - 1)], localArrayList.toArray());
    }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/data/CanonicalGenreUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */