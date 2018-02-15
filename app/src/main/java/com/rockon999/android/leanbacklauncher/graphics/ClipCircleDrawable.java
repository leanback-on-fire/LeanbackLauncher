package com.rockon999.android.leanbacklauncher.graphics;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;

public final class ClipCircleDrawable extends Drawable {
    private final int mColor;

    public ClipCircleDrawable(int color) {
        this.mColor = color;
    }

    public void draw(Canvas canvas) {
        float diameter = (float) Math.max(getBounds().width(), getBounds().height());
        Paint paint = new Paint();
        paint.setColor(this.mColor);
        paint.setAntiAlias(true);
        canvas.drawCircle(diameter / 2.0f, diameter / 2.0f, diameter / 2.0f, paint);
    }

    public void setAlpha(int alpha) {
    }

    public void setColorFilter(ColorFilter cf) {
    }

    public int getOpacity() {
        return -3;
    }
}
