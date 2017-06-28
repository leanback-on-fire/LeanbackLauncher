package com.rockchips.android.leanbacklauncher.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.view.View;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Created by GaoFei on 2017/6/14.
 */

public class ImageUtils {
    /**
     * 将位图保存成图片文件
     * @param saveBitmap  待保存的位图
     * @param savePath     保存路径
     * @param format        保存格式
     * @param quantity     压缩质量
     * @return                   是否保存成功
     */
    public static boolean saveBitmapToImage(Bitmap saveBitmap, String savePath, Bitmap.CompressFormat format, int quantity){
        boolean result;
        if(saveBitmap == null)
            return false;
        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(savePath);
        } catch (FileNotFoundException e) {

            e.printStackTrace();
            return false;
        }
        result = saveBitmap.compress(format, quantity, stream);
        return result;
    }


    /**
     * 从文件中读取位图
     * @param path 图片路径
     * @return
     */
    public static Bitmap getBitmapFromFile(@NonNull String path){
        return BitmapFactory.decodeFile(path);
    }


    /**
     * 从文件读取压缩之后的位图
     * @param path                 图片路径
     * @param targetWidth     目标位图宽度
     * @param targetHeight    目标位图高度
     * @return
     */
    public static Bitmap getScaledBitmapFromFile(@NonNull String path,int targetWidth,int targetHeight){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        int	originWidth = options.outWidth;
        int originHeight = options.outHeight;
        float scaleX = originWidth/targetWidth;
        float scaleY = originHeight/targetHeight;
        float scale = Math.max(scaleX, scaleY);
        options.inSampleSize = (int)(scale);
        options.inJustDecodeBounds = false;
        Bitmap targetBitmap = BitmapFactory.decodeFile(path, options);
        return targetBitmap;
    }

    /**
     * 从大位图中压缩成小位图
     * @param originBitmap
     * @param targetWidth
     * @param targetHeight
     * @return
     */
    public static Bitmap getScaledBitmp(@NonNull Bitmap originBitmap,int targetWidth,int targetHeight){

        return Bitmap.createScaledBitmap(originBitmap, targetWidth, targetHeight, true);

    }

    /**
     * 保存压缩之后的位图至图片文件中
     * @param originPath       原路径
     * @param targetWidth    目标宽度
     * @param targetHeight   目标高度
     * @param savePath         保存路径
     * @param format            保存格式
     * @param quantity          压缩质量
     * @return
     */
    public static boolean saveScaledBitmap(String originPath, int targetWidth, int targetHeight, String savePath, Bitmap.CompressFormat format, int quantity){

        Bitmap scaleBitmap = getScaledBitmapFromFile(originPath, targetWidth, targetHeight);

        return saveBitmapToImage(scaleBitmap, savePath, format, quantity);

    }

    /**
     *根据源位图获取处理之后的圆角位图
     * @param bitmap  源位图
     * @return
     */
    public static Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2, bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    /**
     * 将视图转化为Bitmap
     * @param view 视图
     * @return
     */
    public static Bitmap getBitmapFromView(View view){
        Bitmap b = Bitmap.createBitmap(view.getLayoutParams().width, view.getLayoutParams().height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        view.layout(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
        view.draw(c);
        return b;
    }
}
