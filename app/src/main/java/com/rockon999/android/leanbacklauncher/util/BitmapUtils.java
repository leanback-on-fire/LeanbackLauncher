package com.rockon999.android.leanbacklauncher.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by rockon999 on 2/18/18.
 */

public class BitmapUtils {

    public static Bitmap getBitmapFromFile(String path) {
        File file = new File(path);
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        return BitmapFactory.decodeFile(file.getAbsolutePath(), bmOptions);
    }

    public static void saveBitmapToImage(Bitmap bitmap, String path, Bitmap.CompressFormat format, int i) {
        File file = new File(path);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            bitmap.compress(format, i, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
