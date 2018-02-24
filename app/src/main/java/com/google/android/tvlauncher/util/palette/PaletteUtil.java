package com.google.android.tvlauncher.util.palette;

import android.content.Context;
import android.graphics.Bitmap;
import com.bumptech.glide.Glide;

public class PaletteUtil {
    private static boolean sPaletteTranscoderRegistered = false;

    private PaletteUtil() {
    }

    public static void registerGlidePaletteTranscoder(Context context) {
        if (!sPaletteTranscoderRegistered) {
            Glide.get(context).getRegistry().register(Bitmap.class, PaletteBitmapContainer.class, new PaletteBitmapTranscoder(context));
            sPaletteTranscoderRegistered = true;
        }
    }
}
