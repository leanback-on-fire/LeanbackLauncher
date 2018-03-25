package com.rockon999.android.leanbacklauncher.wallpaper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader.TileMode;
import android.util.AttributeSet;
import android.view.View;

public class FadeMaskView extends View {
    private Bitmap mMask;
    private Paint mPaint;

    public FadeMaskView(Context context) {
        this(context, null);
    }

    public FadeMaskView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FadeMaskView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mPaint = new Paint();
    }

    public void setBitmap(Bitmap maskImage) {
        this.mMask = maskImage;
        if (this.mMask != null) {
            this.mPaint.setShader(new BitmapShader(this.mMask, TileMode.REPEAT, TileMode.CLAMP));
        }
    }

    protected void onDraw(Canvas canvas) {
        if (this.mMask != null) {
            canvas.drawRect(0.0f, 0.0f, (float) getWidth(), (float) getHeight(), this.mPaint);
        }
    }
}
